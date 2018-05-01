package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
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
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.support.widget.GridLayoutManagerFixed;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
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
import org.telegram.tgnet.TLRPC.TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_reportSpam;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
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
import org.telegram.tgnet.TLRPC.TL_message;
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
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardForceReply;
import org.telegram.tgnet.TLRPC.TL_user;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.MentionsAdapter;
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
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatBigEmptyView;
import org.telegram.ui.Components.CorrectlyMeasuringTextView;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.ExtendedGridLayoutManager;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.InstantCameraView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended;
import org.telegram.ui.Components.ShareAlert;
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
    private ArrayList<MessageObject> animatingMessageObjects = new ArrayList();
    private Paint aspectPaint;
    private Path aspectPath;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private ActionBarMenuItem attachItem;
    private ChatAvatarContainer avatarContainer;
    private ChatBigEmptyView bigEmptyView;
    private MessageObject botButtons;
    private PhotoViewerProvider botContextProvider = new C23342();
    private ArrayList<Object> botContextResults;
    private SparseArray<BotInfo> botInfo = new SparseArray();
    private MessageObject botReplyButtons;
    private String botUser;
    private int botsCount;
    private FrameLayout bottomOverlay;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private TextView bottomOverlayText;
    private boolean[] cacheEndReached = new boolean[2];
    private int canEditMessagesCount;
    private int cantDeleteMessagesCount;
    protected ChatActivityEnterView chatActivityEnterView;
    private ChatActivityAdapter chatAdapter;
    private ChatAttachAlert chatAttachAlert;
    private long chatEnterTime;
    private GridLayoutManagerFixed chatLayoutManager;
    private long chatLeaveTime;
    private RecyclerListView chatListView;
    private boolean chatListViewIgnoreLayout;
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
    private int editingMessageObjectReqId;
    private View emojiButtonRed;
    private TextView emptyView;
    private FrameLayout emptyViewContainer;
    private boolean[] endReached = new boolean[2];
    private boolean first = true;
    private boolean firstLoading = true;
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
    private FragmentContextView fragmentLocationContextView;
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
    protected ChatFull info;
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
    OnItemClickListenerExtended onItemClickListener = new C20154();
    OnItemLongClickListenerExtended onItemLongClickListener = new C20143();
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
    private PhotoViewerProvider photoViewerProvider = new C23331();
    private FileLocation pinnedImageLocation;
    private View pinnedLineView;
    private BackupImageView pinnedMessageImageView;
    private SimpleTextView pinnedMessageNameTextView;
    private MessageObject pinnedMessageObject;
    private SimpleTextView pinnedMessageTextView;
    private FrameLayout pinnedMessageView;
    private AnimatorSet pinnedMessageViewAnimator;
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
    private TextureView videoTextureView;
    private AnimatorSet voiceHintAnimation;
    private Runnable voiceHintHideRunnable;
    private TextView voiceHintTextView;
    private Runnable waitingForCharaterEnterRunnable;
    private ArrayList<Integer> waitingForLoad = new ArrayList();
    private boolean waitingForReplyMessageLoad;
    private boolean wasManualScroll;
    private boolean wasPaused;

    /* renamed from: org.telegram.ui.ChatActivity$3 */
    class C20143 implements OnItemLongClickListenerExtended {
        C20143() {
        }

        public boolean onItemClick(View view, int i, float f, float f2) {
            ChatActivity.this.wasManualScroll = true;
            boolean z = false;
            if (ChatActivity.this.actionBar.isActionModeShowed() == 0) {
                ChatActivity.this.createMenu(view, false, true);
            } else {
                if ((view instanceof ChatMessageCell) != 0) {
                    z = ((ChatMessageCell) view).isInsideBackground(f, f2) ^ 1;
                }
                ChatActivity.this.processRowSelect(view, z);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$4 */
    class C20154 implements OnItemClickListenerExtended {
        C20154() {
        }

        public void onItemClick(View view, int i, float f, float f2) {
            ChatActivity.this.wasManualScroll = true;
            boolean z = false;
            if (ChatActivity.this.actionBar.isActionModeShowed() != 0) {
                if ((view instanceof ChatMessageCell) != 0) {
                    z = ((ChatMessageCell) view).isInsideBackground(f, f2) ^ 1;
                }
                ChatActivity.this.processRowSelect(view, z);
                return;
            }
            ChatActivity.this.createMenu(view, true, false);
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$9 */
    class C20179 extends ActionBarMenuOnItemClick {
        C20179() {
        }

        public void onItemClick(final int i) {
            MessageObject messageObject = null;
            boolean z = false;
            if (i == -1) {
                if (ChatActivity.this.actionBar.isActionModeShowed() != 0) {
                    for (i = 1; i >= 0; i--) {
                        ChatActivity.this.selectedMessagesIds[i].clear();
                        ChatActivity.this.selectedMessagesCanCopyIds[i].clear();
                        ChatActivity.this.selectedMessagesCanStarIds[i].clear();
                    }
                    ChatActivity.this.cantDeleteMessagesCount = 0;
                    ChatActivity.this.canEditMessagesCount = 0;
                    if (ChatActivity.this.chatActivityEnterView.isEditingMessage() != 0) {
                        ChatActivity.this.chatActivityEnterView.setEditingMessageObject(null, false);
                    } else {
                        ChatActivity.this.actionBar.hideActionMode();
                        ChatActivity.this.updatePinnedMessageView(true);
                    }
                    ChatActivity.this.updateVisibleRows();
                } else {
                    ChatActivity.this.finishFragment();
                }
            } else if (i == 10) {
                CharSequence charSequence = TtmlNode.ANONYMOUS_REGION_ID;
                boolean z2 = false;
                i = 1;
                while (i >= 0) {
                    ArrayList arrayList = new ArrayList();
                    for (int i2 = 0; i2 < ChatActivity.this.selectedMessagesCanCopyIds[i].size(); i2++) {
                        arrayList.add(Integer.valueOf(ChatActivity.this.selectedMessagesCanCopyIds[i].keyAt(i2)));
                    }
                    if (ChatActivity.this.currentEncryptedChat == null) {
                        Collections.sort(arrayList);
                    } else {
                        Collections.sort(arrayList, Collections.reverseOrder());
                    }
                    boolean z3 = z2;
                    String str = charSequence;
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        StringBuilder stringBuilder;
                        r6 = (MessageObject) ChatActivity.this.selectedMessagesCanCopyIds[i].get(((Integer) arrayList.get(i3)).intValue());
                        if (str.length() != 0) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str);
                            stringBuilder.append("\n\n");
                            str = stringBuilder.toString();
                        }
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str);
                        stringBuilder.append(ChatActivity.this.getMessageContent(r6, z3, true));
                        str = stringBuilder.toString();
                        z3 = r6.messageOwner.from_id;
                    }
                    i--;
                    Object obj = str;
                    z2 = z3;
                }
                if (charSequence.length() != 0) {
                    AndroidUtilities.addToClipboard(charSequence);
                }
                for (i = 1; i >= 0; i--) {
                    ChatActivity.this.selectedMessagesIds[i].clear();
                    ChatActivity.this.selectedMessagesCanCopyIds[i].clear();
                    ChatActivity.this.selectedMessagesCanStarIds[i].clear();
                }
                ChatActivity.this.cantDeleteMessagesCount = 0;
                ChatActivity.this.canEditMessagesCount = 0;
                ChatActivity.this.actionBar.hideActionMode();
                ChatActivity.this.updatePinnedMessageView(true);
                ChatActivity.this.updateVisibleRows();
            } else if (i == 12) {
                if (ChatActivity.this.getParentActivity() != 0) {
                    ChatActivity.this.createDeleteMessagesAlert(null, null);
                }
            } else if (i == 11) {
                i = new Bundle();
                i.putBoolean("onlySelect", true);
                i.putInt("dialogsType", 3);
                BaseFragment dialogsActivity = new DialogsActivity(i);
                dialogsActivity.setDelegate(ChatActivity.this);
                ChatActivity.this.presentFragment(dialogsActivity);
            } else if (i != 13) {
                if (i != 15) {
                    if (i != 16) {
                        if (i == 17) {
                            if (ChatActivity.this.currentUser != 0) {
                                if (ChatActivity.this.getParentActivity() != 0) {
                                    if (ChatActivity.this.currentUser.phone == 0 || ChatActivity.this.currentUser.phone.length() == 0) {
                                        ChatActivity.this.shareMyContact(ChatActivity.this.replyingMessageObject);
                                    } else {
                                        i = new Bundle();
                                        i.putInt("user_id", ChatActivity.this.currentUser.id);
                                        i.putBoolean("addContact", true);
                                        ChatActivity.this.presentFragment(new ContactAddActivity(i));
                                    }
                                }
                            }
                            return;
                        } else if (i == 18) {
                            ChatActivity.this.toggleMute(false);
                        } else if (i == 24) {
                            try {
                                DataQuery.getInstance(ChatActivity.this.currentAccount).installShortcut((long) ChatActivity.this.currentUser.id);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        } else if (i == 21) {
                            ChatActivity.this.showDialog(AlertsCreator.createReportAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.dialog_id, 0, ChatActivity.this));
                        } else if (i == 19) {
                            r6 = null;
                            i = 1;
                            while (i >= 0) {
                                if (r6 == null && ChatActivity.this.selectedMessagesIds[i].size() == 1) {
                                    r0 = new ArrayList();
                                    for (r1 = 0; r1 < ChatActivity.this.selectedMessagesIds[i].size(); r1++) {
                                        r0.add(Integer.valueOf(ChatActivity.this.selectedMessagesIds[i].keyAt(r1)));
                                    }
                                    r6 = (MessageObject) ChatActivity.this.messagesDict[i].get(((Integer) r0.get(0)).intValue());
                                }
                                ChatActivity.this.selectedMessagesIds[i].clear();
                                ChatActivity.this.selectedMessagesCanCopyIds[i].clear();
                                ChatActivity.this.selectedMessagesCanStarIds[i].clear();
                                i--;
                            }
                            if (r6 != null && (r6.messageOwner.id > 0 || (r6.messageOwner.id < 0 && ChatActivity.this.currentEncryptedChat != 0))) {
                                ChatActivity.this.showReplyPanel(true, r6, null, null, false);
                            }
                            ChatActivity.this.cantDeleteMessagesCount = 0;
                            ChatActivity.this.canEditMessagesCount = 0;
                            ChatActivity.this.actionBar.hideActionMode();
                            ChatActivity.this.updatePinnedMessageView(true);
                            ChatActivity.this.updateVisibleRows();
                        } else if (i == 22) {
                            for (i = 0; i < 2; i++) {
                                for (r1 = 0; r1 < ChatActivity.this.selectedMessagesCanStarIds[i].size(); r1++) {
                                    DataQuery.getInstance(ChatActivity.this.currentAccount).addRecentSticker(2, ((MessageObject) ChatActivity.this.selectedMessagesCanStarIds[i].valueAt(r1)).getDocument(), (int) (System.currentTimeMillis() / 1000), ChatActivity.this.hasUnfavedSelected ^ true);
                                }
                            }
                            for (i = 1; i >= 0; i--) {
                                ChatActivity.this.selectedMessagesIds[i].clear();
                                ChatActivity.this.selectedMessagesCanCopyIds[i].clear();
                                ChatActivity.this.selectedMessagesCanStarIds[i].clear();
                            }
                            ChatActivity.this.cantDeleteMessagesCount = 0;
                            ChatActivity.this.canEditMessagesCount = 0;
                            ChatActivity.this.actionBar.hideActionMode();
                            ChatActivity.this.updatePinnedMessageView(true);
                            ChatActivity.this.updateVisibleRows();
                        } else if (i == ChatActivity.edit) {
                            i = 1;
                            while (i >= 0) {
                                if (messageObject == null && ChatActivity.this.selectedMessagesIds[i].size() == 1) {
                                    r0 = new ArrayList();
                                    for (r1 = 0; r1 < ChatActivity.this.selectedMessagesIds[i].size(); r1++) {
                                        r0.add(Integer.valueOf(ChatActivity.this.selectedMessagesIds[i].keyAt(r1)));
                                    }
                                    messageObject = (MessageObject) ChatActivity.this.messagesDict[i].get(((Integer) r0.get(0)).intValue());
                                }
                                ChatActivity.this.selectedMessagesIds[i].clear();
                                ChatActivity.this.selectedMessagesCanCopyIds[i].clear();
                                ChatActivity.this.selectedMessagesCanStarIds[i].clear();
                                i--;
                            }
                            ChatActivity.this.startReplyOnTextChange = false;
                            ChatActivity.this.startEditingMessageObject(messageObject);
                            ChatActivity.this.cantDeleteMessagesCount = 0;
                            ChatActivity.this.canEditMessagesCount = 0;
                            ChatActivity.this.updatePinnedMessageView(true);
                            ChatActivity.this.updateVisibleRows();
                        } else if (i == 14) {
                            ChatActivity.this.openAttachMenu();
                        } else if (i == ChatActivity.bot_help) {
                            SendMessagesHelper.getInstance(ChatActivity.this.currentAccount).sendMessage("/help", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                        } else if (i == ChatActivity.bot_settings) {
                            SendMessagesHelper.getInstance(ChatActivity.this.currentAccount).sendMessage("/settings", ChatActivity.this.dialog_id, null, null, false, null, null, null);
                        } else if (i == ChatActivity.search) {
                            ChatActivity.this.openSearchWithText(null);
                        } else if (!(i != 32 || ChatActivity.this.currentUser == 0 || ChatActivity.this.getParentActivity() == 0)) {
                            VoIPHelper.startCall(ChatActivity.this.currentUser, ChatActivity.this.getParentActivity(), MessagesController.getInstance(ChatActivity.this.currentAccount).getUserFull(ChatActivity.this.currentUser.id));
                        }
                    }
                }
                if (ChatActivity.this.getParentActivity() != null) {
                    if (((int) ChatActivity.this.dialog_id) < 0 && ((int) (ChatActivity.this.dialog_id >> 32)) != 1) {
                        z = true;
                    }
                    Builder builder = new Builder(ChatActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    if (i == 15) {
                        builder.setMessage(LocaleController.getString("AreYouSureClearHistory", C0446R.string.AreYouSureClearHistory));
                    } else if (z) {
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0446R.string.AreYouSureDeleteAndExit));
                    } else {
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", C0446R.string.AreYouSureDeleteThisChat));
                    }
                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i != 15) {
                                if (z == null) {
                                    MessagesController.getInstance(ChatActivity.this.currentAccount).deleteDialog(ChatActivity.this.dialog_id, 0);
                                } else if (ChatObject.isNotInChat(ChatActivity.this.currentChat) != null) {
                                    MessagesController.getInstance(ChatActivity.this.currentAccount).deleteDialog(ChatActivity.this.dialog_id, 0);
                                } else {
                                    MessagesController.getInstance(ChatActivity.this.currentAccount).deleteUserFromChat((int) (-ChatActivity.this.dialog_id), MessagesController.getInstance(ChatActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(ChatActivity.this.currentAccount).getClientUserId())), null);
                                }
                                ChatActivity.this.finishFragment();
                                return;
                            }
                            if (!(ChatObject.isChannel(ChatActivity.this.currentChat) == null || ChatActivity.this.info == null || ChatActivity.this.info.pinned_msg_id == null)) {
                                dialogInterface = MessagesController.getNotificationsSettings(ChatActivity.this.currentAccount).edit();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("pin_");
                                stringBuilder.append(ChatActivity.this.dialog_id);
                                dialogInterface.putInt(stringBuilder.toString(), ChatActivity.this.info.pinned_msg_id).commit();
                                ChatActivity.this.updatePinnedMessageView(true);
                            }
                            MessagesController.getInstance(ChatActivity.this.currentAccount).deleteDialog(ChatActivity.this.dialog_id, 1);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    ChatActivity.this.showDialog(builder.create());
                }
            } else if (ChatActivity.this.getParentActivity() != 0) {
                ChatActivity.this.showDialog(AlertsCreator.createTTLAlert(ChatActivity.this.getParentActivity(), ChatActivity.this.currentEncryptedChat).create());
            }
        }
    }

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
        class C20181 implements ChatMessageCellDelegate {
            C20181() {
            }

            public void didPressedShare(ChatMessageCell chatMessageCell) {
                if (ChatActivity.this.getParentActivity() != null) {
                    if (ChatActivity.this.chatActivityEnterView != null) {
                        ChatActivity.this.chatActivityEnterView.closeKeyboard();
                    }
                    chatMessageCell = chatMessageCell.getMessageObject();
                    if (!UserObject.isUserSelf(ChatActivity.this.currentUser) || chatMessageCell.messageOwner.fwd_from.saved_from_peer == null) {
                        ArrayList arrayList = null;
                        if (chatMessageCell.getGroupId() != 0) {
                            GroupedMessages groupedMessages = (GroupedMessages) ChatActivity.this.groupedMessagesMap.get(chatMessageCell.getGroupId());
                            if (groupedMessages != null) {
                                arrayList = groupedMessages.messages;
                            }
                        }
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                            arrayList.add(chatMessageCell);
                        }
                        ArrayList arrayList2 = arrayList;
                        chatMessageCell = ChatActivity.this;
                        Context access$28400 = ChatActivityAdapter.this.mContext;
                        boolean z = ChatObject.isChannel(ChatActivity.this.currentChat) && !ChatActivity.this.currentChat.megagroup && ChatActivity.this.currentChat.username != null && ChatActivity.this.currentChat.username.length() > 0;
                        chatMessageCell.showDialog(new ShareAlert(access$28400, arrayList2, null, z, null, false));
                    } else {
                        Bundle bundle = new Bundle();
                        if (chatMessageCell.messageOwner.fwd_from.saved_from_peer.channel_id != 0) {
                            bundle.putInt("chat_id", chatMessageCell.messageOwner.fwd_from.saved_from_peer.channel_id);
                        } else if (chatMessageCell.messageOwner.fwd_from.saved_from_peer.chat_id != 0) {
                            bundle.putInt("chat_id", chatMessageCell.messageOwner.fwd_from.saved_from_peer.chat_id);
                        } else if (chatMessageCell.messageOwner.fwd_from.saved_from_peer.user_id != 0) {
                            bundle.putInt("user_id", chatMessageCell.messageOwner.fwd_from.saved_from_peer.user_id);
                        }
                        bundle.putInt("message_id", chatMessageCell.messageOwner.fwd_from.saved_from_msg_id);
                        if (MessagesController.getInstance(ChatActivity.this.currentAccount).checkCanOpenChat(bundle, ChatActivity.this) != null) {
                            ChatActivity.this.presentFragment(new ChatActivity(bundle));
                        }
                    }
                }
            }

            public boolean needPlayMessage(MessageObject messageObject) {
                if (!messageObject.isVoice()) {
                    if (!messageObject.isRoundVideo()) {
                        if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(ChatActivity.this.messages, messageObject);
                        }
                        return false;
                    }
                }
                boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                MediaController.getInstance().setVoiceMessagesPlaylist(playMessage ? ChatActivity.this.createVoiceMessagesPlaylist(messageObject, false) : null, false);
                return playMessage;
            }

            public void didPressedChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i) {
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    ChatActivity.this.processRowSelect(chatMessageCell, true);
                    return;
                }
                if (!(chat == null || chat == ChatActivity.this.currentChat)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", chat.id);
                    if (i != 0) {
                        bundle.putInt("message_id", i);
                    }
                    if (MessagesController.getInstance(ChatActivity.this.currentAccount).checkCanOpenChat(bundle, ChatActivity.this, chatMessageCell.getMessageObject()) != null) {
                        ChatActivity.this.presentFragment(new ChatActivity(bundle), true);
                    }
                }
            }

            public void didPressedOther(ChatMessageCell chatMessageCell) {
                if (chatMessageCell.getMessageObject().type != 16) {
                    ChatActivity.this.createMenu(chatMessageCell, true, false, false);
                } else if (ChatActivity.this.currentUser != null) {
                    VoIPHelper.startCall(ChatActivity.this.currentUser, ChatActivity.this.getParentActivity(), MessagesController.getInstance(ChatActivity.this.currentAccount).getUserFull(ChatActivity.this.currentUser.id));
                }
            }

            public void didPressedUserAvatar(ChatMessageCell chatMessageCell, User user) {
                boolean z = true;
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    ChatActivity.this.processRowSelect(chatMessageCell, true);
                    return;
                }
                if (!(user == null || user.id == UserConfig.getInstance(ChatActivity.this.currentAccount).getClientUserId())) {
                    chatMessageCell = new Bundle();
                    chatMessageCell.putInt("user_id", user.id);
                    BaseFragment profileActivity = new ProfileActivity(chatMessageCell);
                    if (ChatActivity.this.currentUser == null || ChatActivity.this.currentUser.id != user.id) {
                        z = false;
                    }
                    profileActivity.setPlayProfileAnimation(z);
                    ChatActivity.this.presentFragment(profileActivity);
                }
            }

            public void didPressedBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                if (ChatActivity.this.getParentActivity() != null) {
                    if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0 || (keyboardButton instanceof TL_keyboardButtonSwitchInline) || (keyboardButton instanceof TL_keyboardButtonCallback) || (keyboardButton instanceof TL_keyboardButtonGame) || (keyboardButton instanceof TL_keyboardButtonUrl) || (keyboardButton instanceof TL_keyboardButtonBuy)) {
                        ChatActivity.this.chatActivityEnterView.didPressedBotButton(keyboardButton, chatMessageCell.getMessageObject(), chatMessageCell.getMessageObject());
                    }
                }
            }

            public void didPressedCancelSendButton(ChatMessageCell chatMessageCell) {
                chatMessageCell = chatMessageCell.getMessageObject();
                if (chatMessageCell.messageOwner.send_state != 0) {
                    SendMessagesHelper.getInstance(ChatActivity.this.currentAccount).cancelSendingMessage(chatMessageCell);
                }
            }

            public void didLongPressed(ChatMessageCell chatMessageCell) {
                ChatActivity.this.createMenu(chatMessageCell, false, false);
            }

            public boolean canPerformActions() {
                return (ChatActivity.this.actionBar == null || ChatActivity.this.actionBar.isActionModeShowed()) ? false : true;
            }

            public void didPressedUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z) {
                if (characterStyle != null) {
                    boolean z2 = false;
                    if (characterStyle instanceof URLSpanMono) {
                        ((URLSpanMono) characterStyle).copyToClipboard();
                        Toast.makeText(ChatActivity.this.getParentActivity(), LocaleController.getString("TextCopied", true), 0).show();
                    } else if (characterStyle instanceof URLSpanUserMention) {
                        messageObject = MessagesController.getInstance(ChatActivity.this.currentAccount).getUser(Utilities.parseInt(((URLSpanUserMention) characterStyle).getURL()));
                        if (messageObject != null) {
                            MessagesController.openChatOrProfileWith(messageObject, null, ChatActivity.this, 0, false);
                        }
                    } else if (characterStyle instanceof URLSpanNoUnderline) {
                        characterStyle = ((URLSpanNoUnderline) characterStyle).getURL();
                        if (characterStyle.startsWith("@")) {
                            MessagesController.getInstance(ChatActivity.this.currentAccount).openByUserName(characterStyle.substring(1), ChatActivity.this, 0);
                        } else {
                            if (!characterStyle.startsWith("#")) {
                                if (!characterStyle.startsWith("$")) {
                                    if (characterStyle.startsWith("/") && URLSpanBotCommand.enabled) {
                                        ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                                        if (ChatActivity.this.currentChat != null && ChatActivity.this.currentChat.megagroup) {
                                            z2 = true;
                                        }
                                        chatActivityEnterView.setCommand(messageObject, characterStyle, z, z2);
                                        if (!z && ChatActivity.this.chatActivityEnterView.getFieldText() == null) {
                                            ChatActivity.this.showReplyPanel(false, null, null, null, false);
                                        }
                                    }
                                }
                            }
                            if (ChatObject.isChannel(ChatActivity.this.currentChat) != null) {
                                ChatActivity.this.openSearchWithText(characterStyle);
                            } else {
                                messageObject = new DialogsActivity(null);
                                messageObject.setSearchString(characterStyle);
                                ChatActivity.this.presentFragment(messageObject);
                            }
                        }
                    } else {
                        final String url = ((URLSpan) characterStyle).getURL();
                        if (z) {
                            messageObject = new BottomSheet.Builder(ChatActivity.this.getParentActivity());
                            messageObject.setTitle(url);
                            messageObject.setItems(new CharSequence[]{LocaleController.getString("Open", C0446R.string.Open), LocaleController.getString("Copy", C0446R.string.Copy)}, new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    boolean z = true;
                                    if (i == 0) {
                                        Context parentActivity = ChatActivity.this.getParentActivity();
                                        String str = url;
                                        if (ChatActivity.this.inlineReturn != 0) {
                                            z = null;
                                        }
                                        Browser.openUrl(parentActivity, str, z);
                                    } else if (i == 1) {
                                        dialogInterface = url;
                                        if (dialogInterface.startsWith("mailto:") != 0) {
                                            dialogInterface = dialogInterface.substring(7);
                                        } else if (dialogInterface.startsWith("tel:") != 0) {
                                            dialogInterface = dialogInterface.substring(4);
                                        }
                                        AndroidUtilities.addToClipboard(dialogInterface);
                                    }
                                }
                            });
                            ChatActivity.this.showDialog(messageObject.create());
                        } else if (characterStyle instanceof URLSpanReplacement) {
                            ChatActivity.this.showOpenUrlAlert(((URLSpanReplacement) characterStyle).getURL(), true);
                        } else if (characterStyle instanceof URLSpan) {
                            if (!((messageObject.messageOwner.media instanceof TL_messageMediaWebPage) == null || messageObject.messageOwner.media.webpage == null || messageObject.messageOwner.media.webpage.cached_page == null)) {
                                characterStyle = url.toLowerCase();
                                z = messageObject.messageOwner.media.webpage.url.toLowerCase();
                                if ((characterStyle.contains("telegra.ph") || characterStyle.contains("t.me/iv")) && (characterStyle.contains(z) || z.contains(characterStyle) != null)) {
                                    ArticleViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity(), ChatActivity.this);
                                    ArticleViewer.getInstance().open(messageObject);
                                    return;
                                }
                            }
                            Context parentActivity = ChatActivity.this.getParentActivity();
                            if (ChatActivity.this.inlineReturn == 0) {
                                z2 = true;
                            }
                            Browser.openUrl(parentActivity, url, z2);
                        } else if ((characterStyle instanceof ClickableSpan) != null) {
                            ((ClickableSpan) characterStyle).onClick(ChatActivity.this.fragmentView);
                        }
                    }
                }
            }

            public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                EmbedBottomSheet.show(ChatActivityAdapter.this.mContext, str2, str3, str4, str, i, i2);
            }

            public void didPressedReplyMessage(ChatMessageCell chatMessageCell, int i) {
                chatMessageCell = chatMessageCell.getMessageObject();
                ChatActivity.this.scrollToMessageId(i, chatMessageCell.getId(), true, chatMessageCell.getDialogId() == ChatActivity.this.mergeDialogId ? true : null, false);
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void didPressedViaBot(ChatMessageCell chatMessageCell, String str) {
                if ((ChatActivity.this.bottomOverlayChat == null || ChatActivity.this.bottomOverlayChat.getVisibility() != null) && ((ChatActivity.this.bottomOverlay == null || ChatActivity.this.bottomOverlay.getVisibility() != null) && ChatActivity.this.chatActivityEnterView != null && str != null && str.length() > null)) {
                    chatMessageCell = ChatActivity.this.chatActivityEnterView;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("@");
                    stringBuilder.append(str);
                    stringBuilder.append(" ");
                    chatMessageCell.setFieldText(stringBuilder.toString());
                    ChatActivity.this.chatActivityEnterView.openKeyboard();
                }
            }

            public void didPressedImage(ChatMessageCell chatMessageCell) {
                MessageObject messageObject = chatMessageCell.getMessageObject();
                int i = 0;
                if (messageObject.isSendError()) {
                    ChatActivity.this.createMenu(chatMessageCell, false, false);
                } else if (!messageObject.isSending()) {
                    if (messageObject.needDrawBluredPreview()) {
                        if (ChatActivity.this.sendSecretMessageRead(messageObject)) {
                            chatMessageCell.invalidate();
                        }
                        SecretMediaViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                        SecretMediaViewer.getInstance().openMedia(messageObject, ChatActivity.this.photoViewerProvider);
                    } else {
                        File file = null;
                        if (messageObject.type == 13) {
                            chatMessageCell = ChatActivity.this;
                            Context parentActivity = ChatActivity.this.getParentActivity();
                            BaseFragment baseFragment = ChatActivity.this;
                            InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                            if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0 && ChatObject.canSendStickers(ChatActivity.this.currentChat)) {
                                file = ChatActivity.this.chatActivityEnterView;
                            }
                            chatMessageCell.showDialog(new StickersAlert(parentActivity, baseFragment, inputStickerSet, null, file));
                        } else {
                            if (!(messageObject.isVideo() != null || messageObject.type == 1 || (messageObject.type == null && messageObject.isWebpageDocument() == null))) {
                                if (messageObject.isGif() == null) {
                                    if (messageObject.type == 3) {
                                        ChatActivity.this.sendSecretMessageRead(messageObject);
                                        try {
                                            if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == null)) {
                                                file = new File(messageObject.messageOwner.attachPath);
                                            }
                                            if (file == null || file.exists() == null) {
                                                file = FileLoader.getPathToMessage(messageObject.messageOwner);
                                            }
                                            chatMessageCell = new Intent("android.intent.action.VIEW");
                                            if (VERSION.SDK_INT >= 24) {
                                                chatMessageCell.setFlags(1);
                                                chatMessageCell.setDataAndType(FileProvider.getUriForFile(ChatActivity.this.getParentActivity(), "org.telegram.messenger.provider", file), MimeTypes.VIDEO_MP4);
                                            } else {
                                                chatMessageCell.setDataAndType(Uri.fromFile(file), MimeTypes.VIDEO_MP4);
                                            }
                                            ChatActivity.this.getParentActivity().startActivityForResult(chatMessageCell, 500);
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                            ChatActivity.this.alertUserOpenError(messageObject);
                                        }
                                    } else if (messageObject.type == 4) {
                                        if (AndroidUtilities.isGoogleMapsInstalled(ChatActivity.this) != null) {
                                            if (messageObject.isLiveLocation() != null) {
                                                chatMessageCell = new LocationActivity(2);
                                                chatMessageCell.setMessageObject(messageObject);
                                                chatMessageCell.setDelegate(ChatActivity.this);
                                                ChatActivity.this.presentFragment(chatMessageCell);
                                            } else {
                                                if (ChatActivity.this.currentEncryptedChat == null) {
                                                    i = 3;
                                                }
                                                chatMessageCell = new LocationActivity(i);
                                                chatMessageCell.setMessageObject(messageObject);
                                                chatMessageCell.setDelegate(ChatActivity.this);
                                                ChatActivity.this.presentFragment(chatMessageCell);
                                            }
                                        } else {
                                            return;
                                        }
                                    } else if (messageObject.type == 9 || messageObject.type == null) {
                                        if (messageObject.getDocumentName().toLowerCase().endsWith("attheme") != null) {
                                            File pathToMessage;
                                            Holder holder;
                                            ThemeInfo applyThemeFile;
                                            if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == null)) {
                                                chatMessageCell = new File(messageObject.messageOwner.attachPath);
                                                if (chatMessageCell.exists()) {
                                                    if (chatMessageCell == null) {
                                                        pathToMessage = FileLoader.getPathToMessage(messageObject.messageOwner);
                                                        if (pathToMessage.exists()) {
                                                            chatMessageCell = pathToMessage;
                                                        }
                                                    }
                                                    if (ChatActivity.this.chatLayoutManager != null) {
                                                        i = ChatActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
                                                        if (i == 0) {
                                                            ChatActivity.this.scrollToPositionOnRecreate = i;
                                                            holder = (Holder) ChatActivity.this.chatListView.findViewHolderForAdapterPosition(ChatActivity.this.scrollToPositionOnRecreate);
                                                            if (holder == null) {
                                                                ChatActivity.this.scrollToOffsetOnRecreate = (ChatActivity.this.chatListView.getMeasuredHeight() - holder.itemView.getBottom()) - ChatActivity.this.chatListView.getPaddingBottom();
                                                            } else {
                                                                ChatActivity.this.scrollToPositionOnRecreate = -1;
                                                            }
                                                        } else {
                                                            ChatActivity.this.scrollToPositionOnRecreate = -1;
                                                        }
                                                    }
                                                    applyThemeFile = Theme.applyThemeFile(chatMessageCell, messageObject.getDocumentName(), true);
                                                    if (applyThemeFile == null) {
                                                        ChatActivity.this.presentFragment(new ThemePreviewActivity(chatMessageCell, applyThemeFile));
                                                        return;
                                                    }
                                                    ChatActivity.this.scrollToPositionOnRecreate = -1;
                                                }
                                            }
                                            chatMessageCell = null;
                                            if (chatMessageCell == null) {
                                                pathToMessage = FileLoader.getPathToMessage(messageObject.messageOwner);
                                                if (pathToMessage.exists()) {
                                                    chatMessageCell = pathToMessage;
                                                }
                                            }
                                            if (ChatActivity.this.chatLayoutManager != null) {
                                                i = ChatActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
                                                if (i == 0) {
                                                    ChatActivity.this.scrollToPositionOnRecreate = -1;
                                                } else {
                                                    ChatActivity.this.scrollToPositionOnRecreate = i;
                                                    holder = (Holder) ChatActivity.this.chatListView.findViewHolderForAdapterPosition(ChatActivity.this.scrollToPositionOnRecreate);
                                                    if (holder == null) {
                                                        ChatActivity.this.scrollToPositionOnRecreate = -1;
                                                    } else {
                                                        ChatActivity.this.scrollToOffsetOnRecreate = (ChatActivity.this.chatListView.getMeasuredHeight() - holder.itemView.getBottom()) - ChatActivity.this.chatListView.getPaddingBottom();
                                                    }
                                                }
                                            }
                                            applyThemeFile = Theme.applyThemeFile(chatMessageCell, messageObject.getDocumentName(), true);
                                            if (applyThemeFile == null) {
                                                ChatActivity.this.scrollToPositionOnRecreate = -1;
                                            } else {
                                                ChatActivity.this.presentFragment(new ThemePreviewActivity(chatMessageCell, applyThemeFile));
                                                return;
                                            }
                                        }
                                        try {
                                            AndroidUtilities.openForView(messageObject, ChatActivity.this.getParentActivity());
                                        } catch (Throwable e2) {
                                            FileLog.m3e(e2);
                                            ChatActivity.this.alertUserOpenError(messageObject);
                                        }
                                    }
                                }
                            }
                            if (messageObject.isVideo() != null) {
                                ChatActivity.this.sendSecretMessageRead(messageObject);
                            }
                            PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                            PhotoViewer instance = PhotoViewer.getInstance();
                            long j = 0;
                            long access$2300 = messageObject.type != null ? ChatActivity.this.dialog_id : 0;
                            if (messageObject.type != null) {
                                j = ChatActivity.this.mergeDialogId;
                            }
                            if (instance.openPhoto(messageObject, access$2300, j, ChatActivity.this.photoViewerProvider) != null) {
                                PhotoViewer.getInstance().setParentChatActivity(ChatActivity.this);
                            }
                        }
                    }
                }
            }

            public void didPressedInstantButton(ChatMessageCell chatMessageCell, int i) {
                chatMessageCell = chatMessageCell.getMessageObject();
                if (i == 0) {
                    if (chatMessageCell.messageOwner.media != 0 && chatMessageCell.messageOwner.media.webpage != 0 && chatMessageCell.messageOwner.media.webpage.cached_page != 0) {
                        ArticleViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity(), ChatActivity.this);
                        ArticleViewer.getInstance().open(chatMessageCell);
                    }
                } else if (chatMessageCell.messageOwner.media != 0 && chatMessageCell.messageOwner.media.webpage != 0) {
                    Browser.openUrl(ChatActivity.this.getParentActivity(), chatMessageCell.messageOwner.media.webpage.url);
                }
            }

            public boolean isChatAdminCell(int i) {
                return (ChatObject.isChannel(ChatActivity.this.currentChat) && ChatActivity.this.currentChat.megagroup) ? MessagesController.getInstance(ChatActivity.this.currentAccount).isChannelAdmin(ChatActivity.this.currentChat.id, i) : false;
            }
        }

        /* renamed from: org.telegram.ui.ChatActivity$ChatActivityAdapter$2 */
        class C20192 implements ChatActionCellDelegate {
            C20192() {
            }

            public void didClickedImage(ChatActionCell chatActionCell) {
                MessageObject messageObject = chatActionCell.getMessageObject();
                PhotoViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                chatActionCell = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 640);
                if (chatActionCell != null) {
                    PhotoViewer.getInstance().openPhoto(chatActionCell.location, ChatActivity.this.photoViewerProvider);
                } else {
                    PhotoViewer.getInstance().openPhoto(messageObject, 0, 0, ChatActivity.this.photoViewerProvider);
                }
            }

            public void didLongPressed(ChatActionCell chatActionCell) {
                ChatActivity.this.createMenu(chatActionCell, false, false);
            }

            public void needOpenUserProfile(int i) {
                boolean z = true;
                Bundle bundle;
                if (i < 0) {
                    bundle = new Bundle();
                    bundle.putInt("chat_id", -i);
                    if (MessagesController.getInstance(ChatActivity.this.currentAccount).checkCanOpenChat(bundle, ChatActivity.this) != 0) {
                        ChatActivity.this.presentFragment(new ChatActivity(bundle), true);
                    }
                } else if (i != UserConfig.getInstance(ChatActivity.this.currentAccount).getClientUserId()) {
                    bundle = new Bundle();
                    bundle.putInt("user_id", i);
                    if (ChatActivity.this.currentEncryptedChat != null && i == ChatActivity.this.currentUser.id) {
                        bundle.putLong("dialog_id", ChatActivity.this.dialog_id);
                    }
                    BaseFragment profileActivity = new ProfileActivity(bundle);
                    if (ChatActivity.this.currentUser == null || ChatActivity.this.currentUser.id != i) {
                        z = false;
                    }
                    profileActivity.setPlayProfileAnimation(z);
                    ChatActivity.this.presentFragment(profileActivity);
                }
            }

            public void didPressedReplyMessage(ChatActionCell chatActionCell, int i) {
                chatActionCell = chatActionCell.getMessageObject();
                ChatActivity.this.scrollToMessageId(i, chatActionCell.getId(), true, chatActionCell.getDialogId() == ChatActivity.this.mergeDialogId ? true : null, false);
            }

            public void didPressedBotButton(MessageObject messageObject, KeyboardButton keyboardButton) {
                if (ChatActivity.this.getParentActivity() != null) {
                    if (ChatActivity.this.bottomOverlayChat.getVisibility() != 0 || (keyboardButton instanceof TL_keyboardButtonSwitchInline) || (keyboardButton instanceof TL_keyboardButtonCallback) || (keyboardButton instanceof TL_keyboardButtonGame) || (keyboardButton instanceof TL_keyboardButtonUrl) || (keyboardButton instanceof TL_keyboardButtonBuy)) {
                        ChatActivity.this.chatActivityEnterView.didPressedBotButton(keyboardButton, messageObject, messageObject);
                    }
                }
            }
        }

        /* renamed from: org.telegram.ui.ChatActivity$ChatActivityAdapter$3 */
        class C20203 implements BotHelpCellDelegate {
            C20203() {
            }

            public void didPressUrl(String str) {
                if (str.startsWith("@")) {
                    MessagesController.getInstance(ChatActivity.this.currentAccount).openByUserName(str.substring(1), ChatActivity.this, 0);
                    return;
                }
                if (!str.startsWith("#")) {
                    if (!str.startsWith("$")) {
                        if (str.startsWith("/")) {
                            ChatActivity.this.chatActivityEnterView.setCommand(null, str, false, false);
                            if (ChatActivity.this.chatActivityEnterView.getFieldText() == null) {
                                ChatActivity.this.showReplyPanel(false, null, null, null, false);
                                return;
                            }
                            return;
                        }
                        return;
                    }
                }
                BaseFragment dialogsActivity = new DialogsActivity(null);
                dialogsActivity.setSearchString(str);
                ChatActivity.this.presentFragment(dialogsActivity);
            }
        }

        public long getItemId(int i) {
            return -1;
        }

        public ChatActivityAdapter(Context context) {
            this.mContext = context;
            ChatActivity chatActivity = (ChatActivity.this.currentUser == null || ChatActivity.this.currentUser.bot == null) ? null : true;
            this.isBot = chatActivity;
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
                if (ChatActivity.this.forwardEndReached[0]) {
                    if (ChatActivity.this.mergeDialogId == 0 || ChatActivity.this.forwardEndReached[1]) {
                        this.loadingDownRow = -1;
                        this.messagesStartRow = this.rowCount;
                        this.rowCount += ChatActivity.this.messages.size();
                        this.messagesEndRow = this.rowCount;
                        if (ChatActivity.this.endReached[0]) {
                            if (ChatActivity.this.mergeDialogId != 0 || ChatActivity.this.endReached[1]) {
                                this.loadingUpRow = -1;
                            }
                        }
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.loadingUpRow = i;
                    }
                }
                int i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.loadingDownRow = i2;
                this.messagesStartRow = this.rowCount;
                this.rowCount += ChatActivity.this.messages.size();
                this.messagesEndRow = this.rowCount;
                if (ChatActivity.this.endReached[0]) {
                    if (ChatActivity.this.mergeDialogId != 0) {
                    }
                    this.loadingUpRow = -1;
                }
                i = this.rowCount;
                this.rowCount = i + 1;
                this.loadingUpRow = i;
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

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 0) {
                if (ChatActivity.this.chatMessageCellsCache.isEmpty() == 0) {
                    i = (View) ChatActivity.this.chatMessageCellsCache.get(0);
                    ChatActivity.this.chatMessageCellsCache.remove(0);
                } else {
                    i = new ChatMessageCell(this.mContext);
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) i;
                chatMessageCell.setDelegate(new C20181());
                if (ChatActivity.this.currentEncryptedChat == null) {
                    chatMessageCell.setAllowAssistant(true);
                }
            } else if (i == 1) {
                i = new ChatActionCell(this.mContext);
                ((ChatActionCell) i).setDelegate(new C20192());
            } else if (i == 2) {
                i = new ChatUnreadCell(this.mContext);
            } else if (i == 3) {
                i = new BotHelpCell(this.mContext);
                ((BotHelpCell) i).setDelegate(new C20203());
            } else {
                i = i == 4 ? new ChatLoadingCell(this.mContext) : 0;
            }
            i.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(i);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ViewHolder viewHolder2 = viewHolder;
            int i2 = i;
            String str = null;
            if (i2 == this.botInfoRow) {
                BotHelpCell botHelpCell = (BotHelpCell) viewHolder2.itemView;
                if (ChatActivity.this.botInfo.size() != 0) {
                    str = ((BotInfo) ChatActivity.this.botInfo.get(ChatActivity.this.currentUser.id)).description;
                }
                botHelpCell.setText(str);
                return;
            }
            boolean z;
            if (i2 != r0.loadingDownRow) {
                if (i2 != r0.loadingUpRow) {
                    if (i2 >= r0.messagesStartRow && i2 < r0.messagesEndRow) {
                        MessageObject messageObject = (MessageObject) ChatActivity.this.messages.get(i2 - r0.messagesStartRow);
                        View view = viewHolder2.itemView;
                        boolean z2 = view instanceof ChatMessageCell;
                        if (z2) {
                            boolean z3;
                            GroupedMessages access$11800;
                            GroupedMessagePosition groupedMessagePosition;
                            int i3;
                            int indexOf;
                            boolean z4;
                            int itemViewType;
                            int itemViewType2;
                            MessageObject messageObject2;
                            boolean z5;
                            MessageObject messageObject3;
                            boolean z6;
                            int indexOf2;
                            final ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                            if (ChatActivity.this.currentChat == null) {
                                if (!UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                    z3 = false;
                                    chatMessageCell.isChat = z3;
                                    access$11800 = ChatActivity.this.getValidGroupedMessage(messageObject);
                                    if (access$11800 == null) {
                                        groupedMessagePosition = (GroupedMessagePosition) access$11800.positions.get(messageObject);
                                        i3 = -100;
                                        if (groupedMessagePosition == null) {
                                            if ((groupedMessagePosition.flags & 4) == 0) {
                                                indexOf = (access$11800.posArray.indexOf(groupedMessagePosition) + i2) + 1;
                                                z4 = false;
                                            } else {
                                                z4 = true;
                                                indexOf = -100;
                                            }
                                            if ((groupedMessagePosition.flags & 8) == 0) {
                                                i3 = (i2 - access$11800.posArray.size()) + access$11800.posArray.indexOf(groupedMessagePosition);
                                                z = false;
                                            } else {
                                                z = true;
                                            }
                                            itemViewType = getItemViewType(i3);
                                            itemViewType2 = getItemViewType(indexOf);
                                            if (!(messageObject.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && itemViewType == viewHolder.getItemViewType()) {
                                                messageObject2 = (MessageObject) ChatActivity.this.messages.get(i3 - r0.messagesStartRow);
                                                z5 = messageObject2.isOutOwner() != messageObject.isOutOwner() && Math.abs(messageObject2.messageOwner.date - messageObject.messageOwner.date) <= 300;
                                                if (z5) {
                                                    if (ChatActivity.this.currentChat == null) {
                                                        if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                            if (messageObject2.getFromId() == messageObject.getFromId()) {
                                                            }
                                                            z = false;
                                                        }
                                                    }
                                                    z = true;
                                                }
                                                z = z5;
                                            }
                                            if (itemViewType2 == viewHolder.getItemViewType()) {
                                                messageObject3 = (MessageObject) ChatActivity.this.messages.get(indexOf - r0.messagesStartRow);
                                                z4 = (messageObject3.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && messageObject3.isOutOwner() == messageObject.isOutOwner() && Math.abs(messageObject3.messageOwner.date - messageObject.messageOwner.date) <= 300;
                                                if (z4) {
                                                    if (ChatActivity.this.currentChat != null) {
                                                        if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                            if (messageObject3.getFromId() == messageObject.getFromId()) {
                                                            }
                                                            z4 = false;
                                                        }
                                                    }
                                                    z4 = true;
                                                }
                                            }
                                            chatMessageCell.setMessageObject(messageObject, access$11800, z, z4);
                                            if (z2 && DownloadController.getInstance(ChatActivity.this.currentAccount).canDownloadMedia(messageObject)) {
                                                chatMessageCell.downloadAudioIfNeed();
                                            }
                                            z6 = ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID && messageObject.getId() == ChatActivity.this.highlightMessageId;
                                            chatMessageCell.setHighlighted(z6);
                                            if (ChatActivity.this.searchContainer != null && ChatActivity.this.searchContainer.getVisibility() == 0) {
                                                if (DataQuery.getInstance(ChatActivity.this.currentAccount).isMessageFound(messageObject.getId(), messageObject.getDialogId() != ChatActivity.this.mergeDialogId) && DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery() != null) {
                                                    chatMessageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                                                    indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                                                    if (indexOf2 != -1) {
                                                        ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                                        chatMessageCell.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                                                            public boolean onPreDraw() {
                                                                PipRoundVideoView instance = PipRoundVideoView.getInstance();
                                                                if (instance != null) {
                                                                    instance.showTemporary(true);
                                                                }
                                                                chatMessageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                                                ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                                                                float imageWidth = ((float) photoImage.getImageWidth()) / ChatActivity.this.instantCameraView.getCameraRect().width;
                                                                int[] iArr = new int[2];
                                                                chatMessageCell.setAlpha(0.0f);
                                                                chatMessageCell.getLocationOnScreen(iArr);
                                                                iArr[0] = iArr[0] + photoImage.getImageX();
                                                                iArr[1] = iArr[1] + photoImage.getImageY();
                                                                final View cameraContainer = ChatActivity.this.instantCameraView.getCameraContainer();
                                                                cameraContainer.setPivotX(0.0f);
                                                                cameraContainer.setPivotY(0.0f);
                                                                AnimatorSet animatorSet = new AnimatorSet();
                                                                r9 = new Animator[8];
                                                                r9[0] = ObjectAnimator.ofFloat(ChatActivity.this.instantCameraView, "alpha", new float[]{0.0f});
                                                                r9[1] = ObjectAnimator.ofFloat(cameraContainer, "scaleX", new float[]{imageWidth});
                                                                r9[2] = ObjectAnimator.ofFloat(cameraContainer, "scaleY", new float[]{imageWidth});
                                                                r9[3] = ObjectAnimator.ofFloat(cameraContainer, "translationX", new float[]{((float) iArr[0]) - r3.f26x});
                                                                r9[4] = ObjectAnimator.ofFloat(cameraContainer, "translationY", new float[]{((float) iArr[1]) - r3.f27y});
                                                                r9[5] = ObjectAnimator.ofFloat(ChatActivity.this.instantCameraView.getSwitchButtonView(), "alpha", new float[]{0.0f});
                                                                r9[6] = ObjectAnimator.ofInt(ChatActivity.this.instantCameraView.getPaint(), "alpha", new int[]{0});
                                                                r9[7] = ObjectAnimator.ofFloat(ChatActivity.this.instantCameraView.getMuteImageView(), "alpha", new float[]{0.0f});
                                                                animatorSet.playTogether(r9);
                                                                animatorSet.setDuration(180);
                                                                animatorSet.setInterpolator(new DecelerateInterpolator());
                                                                animatorSet.addListener(new AnimatorListenerAdapter() {

                                                                    /* renamed from: org.telegram.ui.ChatActivity$ChatActivityAdapter$4$1$1 */
                                                                    class C10591 extends AnimatorListenerAdapter {
                                                                        C10591() {
                                                                        }

                                                                        public void onAnimationEnd(Animator animator) {
                                                                            ChatActivity.this.instantCameraView.hideCamera(true);
                                                                            ChatActivity.this.instantCameraView.setVisibility(4);
                                                                        }
                                                                    }

                                                                    public void onAnimationEnd(Animator animator) {
                                                                        animator = new AnimatorSet();
                                                                        r0 = new Animator[2];
                                                                        r0[0] = ObjectAnimator.ofFloat(cameraContainer, "alpha", new float[]{0.0f});
                                                                        r0[1] = ObjectAnimator.ofFloat(chatMessageCell, "alpha", new float[]{1.0f});
                                                                        animator.playTogether(r0);
                                                                        animator.setDuration(100);
                                                                        animator.setInterpolator(new DecelerateInterpolator());
                                                                        animator.addListener(new C10591());
                                                                        animator.start();
                                                                    }
                                                                });
                                                                animatorSet.start();
                                                                return true;
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                            chatMessageCell.setHighlightedText(null);
                                            indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                                            if (indexOf2 != -1) {
                                                ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                                chatMessageCell.getViewTreeObserver().addOnPreDrawListener(/* anonymous class already generated */);
                                            }
                                        } else {
                                            indexOf = -100;
                                        }
                                    } else {
                                        i3 = i2 - 1;
                                        indexOf = i2 + 1;
                                    }
                                    z = false;
                                    z4 = false;
                                    itemViewType = getItemViewType(i3);
                                    itemViewType2 = getItemViewType(indexOf);
                                    messageObject2 = (MessageObject) ChatActivity.this.messages.get(i3 - r0.messagesStartRow);
                                    if (messageObject2.isOutOwner() != messageObject.isOutOwner()) {
                                    }
                                    if (z5) {
                                        if (ChatActivity.this.currentChat == null) {
                                            if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                if (messageObject2.getFromId() == messageObject.getFromId()) {
                                                }
                                                z = false;
                                                if (itemViewType2 == viewHolder.getItemViewType()) {
                                                    messageObject3 = (MessageObject) ChatActivity.this.messages.get(indexOf - r0.messagesStartRow);
                                                    if (messageObject3.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                                    }
                                                    if (z4) {
                                                        if (ChatActivity.this.currentChat != null) {
                                                            if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                                if (messageObject3.getFromId() == messageObject.getFromId()) {
                                                                }
                                                                z4 = false;
                                                            }
                                                        }
                                                        z4 = true;
                                                    }
                                                }
                                                chatMessageCell.setMessageObject(messageObject, access$11800, z, z4);
                                                chatMessageCell.downloadAudioIfNeed();
                                                if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                                }
                                                chatMessageCell.setHighlighted(z6);
                                                if (messageObject.getDialogId() != ChatActivity.this.mergeDialogId) {
                                                }
                                                chatMessageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                                                indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                                                if (indexOf2 != -1) {
                                                    ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                                    chatMessageCell.getViewTreeObserver().addOnPreDrawListener(/* anonymous class already generated */);
                                                }
                                            }
                                        }
                                        z = true;
                                        if (itemViewType2 == viewHolder.getItemViewType()) {
                                            messageObject3 = (MessageObject) ChatActivity.this.messages.get(indexOf - r0.messagesStartRow);
                                            if (messageObject3.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                            }
                                            if (z4) {
                                                if (ChatActivity.this.currentChat != null) {
                                                    if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                        if (messageObject3.getFromId() == messageObject.getFromId()) {
                                                        }
                                                        z4 = false;
                                                    }
                                                }
                                                z4 = true;
                                            }
                                        }
                                        chatMessageCell.setMessageObject(messageObject, access$11800, z, z4);
                                        chatMessageCell.downloadAudioIfNeed();
                                        if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                        }
                                        chatMessageCell.setHighlighted(z6);
                                        if (messageObject.getDialogId() != ChatActivity.this.mergeDialogId) {
                                        }
                                        chatMessageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                                        indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                                        if (indexOf2 != -1) {
                                            ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                            chatMessageCell.getViewTreeObserver().addOnPreDrawListener(/* anonymous class already generated */);
                                        }
                                    }
                                    z = z5;
                                    if (itemViewType2 == viewHolder.getItemViewType()) {
                                        messageObject3 = (MessageObject) ChatActivity.this.messages.get(indexOf - r0.messagesStartRow);
                                        if (messageObject3.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                        }
                                        if (z4) {
                                            if (ChatActivity.this.currentChat != null) {
                                                if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                    if (messageObject3.getFromId() == messageObject.getFromId()) {
                                                    }
                                                    z4 = false;
                                                }
                                            }
                                            z4 = true;
                                        }
                                    }
                                    chatMessageCell.setMessageObject(messageObject, access$11800, z, z4);
                                    chatMessageCell.downloadAudioIfNeed();
                                    if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                    }
                                    chatMessageCell.setHighlighted(z6);
                                    if (messageObject.getDialogId() != ChatActivity.this.mergeDialogId) {
                                    }
                                    chatMessageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                                    indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                                    if (indexOf2 != -1) {
                                        ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                        chatMessageCell.getViewTreeObserver().addOnPreDrawListener(/* anonymous class already generated */);
                                    }
                                }
                            }
                            z3 = true;
                            chatMessageCell.isChat = z3;
                            access$11800 = ChatActivity.this.getValidGroupedMessage(messageObject);
                            if (access$11800 == null) {
                                i3 = i2 - 1;
                                indexOf = i2 + 1;
                            } else {
                                groupedMessagePosition = (GroupedMessagePosition) access$11800.positions.get(messageObject);
                                i3 = -100;
                                if (groupedMessagePosition == null) {
                                    indexOf = -100;
                                } else {
                                    if ((groupedMessagePosition.flags & 4) == 0) {
                                        z4 = true;
                                        indexOf = -100;
                                    } else {
                                        indexOf = (access$11800.posArray.indexOf(groupedMessagePosition) + i2) + 1;
                                        z4 = false;
                                    }
                                    if ((groupedMessagePosition.flags & 8) == 0) {
                                        z = true;
                                    } else {
                                        i3 = (i2 - access$11800.posArray.size()) + access$11800.posArray.indexOf(groupedMessagePosition);
                                        z = false;
                                    }
                                    itemViewType = getItemViewType(i3);
                                    itemViewType2 = getItemViewType(indexOf);
                                    messageObject2 = (MessageObject) ChatActivity.this.messages.get(i3 - r0.messagesStartRow);
                                    if (messageObject2.isOutOwner() != messageObject.isOutOwner()) {
                                    }
                                    if (z5) {
                                        if (ChatActivity.this.currentChat == null) {
                                            if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                if (messageObject2.getFromId() == messageObject.getFromId()) {
                                                }
                                                z = false;
                                                if (itemViewType2 == viewHolder.getItemViewType()) {
                                                    messageObject3 = (MessageObject) ChatActivity.this.messages.get(indexOf - r0.messagesStartRow);
                                                    if (messageObject3.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                                    }
                                                    if (z4) {
                                                        if (ChatActivity.this.currentChat != null) {
                                                            if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                                if (messageObject3.getFromId() == messageObject.getFromId()) {
                                                                }
                                                                z4 = false;
                                                            }
                                                        }
                                                        z4 = true;
                                                    }
                                                }
                                                chatMessageCell.setMessageObject(messageObject, access$11800, z, z4);
                                                chatMessageCell.downloadAudioIfNeed();
                                                if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                                }
                                                chatMessageCell.setHighlighted(z6);
                                                if (messageObject.getDialogId() != ChatActivity.this.mergeDialogId) {
                                                }
                                                chatMessageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                                                indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                                                if (indexOf2 != -1) {
                                                    ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                                    chatMessageCell.getViewTreeObserver().addOnPreDrawListener(/* anonymous class already generated */);
                                                }
                                            }
                                        }
                                        z = true;
                                        if (itemViewType2 == viewHolder.getItemViewType()) {
                                            messageObject3 = (MessageObject) ChatActivity.this.messages.get(indexOf - r0.messagesStartRow);
                                            if (messageObject3.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                            }
                                            if (z4) {
                                                if (ChatActivity.this.currentChat != null) {
                                                    if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                        if (messageObject3.getFromId() == messageObject.getFromId()) {
                                                        }
                                                        z4 = false;
                                                    }
                                                }
                                                z4 = true;
                                            }
                                        }
                                        chatMessageCell.setMessageObject(messageObject, access$11800, z, z4);
                                        chatMessageCell.downloadAudioIfNeed();
                                        if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                        }
                                        chatMessageCell.setHighlighted(z6);
                                        if (messageObject.getDialogId() != ChatActivity.this.mergeDialogId) {
                                        }
                                        chatMessageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                                        indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                                        if (indexOf2 != -1) {
                                            ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                            chatMessageCell.getViewTreeObserver().addOnPreDrawListener(/* anonymous class already generated */);
                                        }
                                    }
                                    z = z5;
                                    if (itemViewType2 == viewHolder.getItemViewType()) {
                                        messageObject3 = (MessageObject) ChatActivity.this.messages.get(indexOf - r0.messagesStartRow);
                                        if (messageObject3.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                        }
                                        if (z4) {
                                            if (ChatActivity.this.currentChat != null) {
                                                if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                    if (messageObject3.getFromId() == messageObject.getFromId()) {
                                                    }
                                                    z4 = false;
                                                }
                                            }
                                            z4 = true;
                                        }
                                    }
                                    chatMessageCell.setMessageObject(messageObject, access$11800, z, z4);
                                    chatMessageCell.downloadAudioIfNeed();
                                    if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                    }
                                    chatMessageCell.setHighlighted(z6);
                                    if (messageObject.getDialogId() != ChatActivity.this.mergeDialogId) {
                                    }
                                    chatMessageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                                    indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                                    if (indexOf2 != -1) {
                                        ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                        chatMessageCell.getViewTreeObserver().addOnPreDrawListener(/* anonymous class already generated */);
                                    }
                                }
                            }
                            z = false;
                            z4 = false;
                            itemViewType = getItemViewType(i3);
                            itemViewType2 = getItemViewType(indexOf);
                            messageObject2 = (MessageObject) ChatActivity.this.messages.get(i3 - r0.messagesStartRow);
                            if (messageObject2.isOutOwner() != messageObject.isOutOwner()) {
                            }
                            if (z5) {
                                if (ChatActivity.this.currentChat == null) {
                                    if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                        if (messageObject2.getFromId() == messageObject.getFromId()) {
                                        }
                                        z = false;
                                        if (itemViewType2 == viewHolder.getItemViewType()) {
                                            messageObject3 = (MessageObject) ChatActivity.this.messages.get(indexOf - r0.messagesStartRow);
                                            if (messageObject3.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                            }
                                            if (z4) {
                                                if (ChatActivity.this.currentChat != null) {
                                                    if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                        if (messageObject3.getFromId() == messageObject.getFromId()) {
                                                        }
                                                        z4 = false;
                                                    }
                                                }
                                                z4 = true;
                                            }
                                        }
                                        chatMessageCell.setMessageObject(messageObject, access$11800, z, z4);
                                        chatMessageCell.downloadAudioIfNeed();
                                        if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                        }
                                        chatMessageCell.setHighlighted(z6);
                                        if (messageObject.getDialogId() != ChatActivity.this.mergeDialogId) {
                                        }
                                        chatMessageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                                        indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                                        if (indexOf2 != -1) {
                                            ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                            chatMessageCell.getViewTreeObserver().addOnPreDrawListener(/* anonymous class already generated */);
                                        }
                                    }
                                }
                                z = true;
                                if (itemViewType2 == viewHolder.getItemViewType()) {
                                    messageObject3 = (MessageObject) ChatActivity.this.messages.get(indexOf - r0.messagesStartRow);
                                    if (messageObject3.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                    }
                                    if (z4) {
                                        if (ChatActivity.this.currentChat != null) {
                                            if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                                if (messageObject3.getFromId() == messageObject.getFromId()) {
                                                }
                                                z4 = false;
                                            }
                                        }
                                        z4 = true;
                                    }
                                }
                                chatMessageCell.setMessageObject(messageObject, access$11800, z, z4);
                                chatMessageCell.downloadAudioIfNeed();
                                if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                }
                                chatMessageCell.setHighlighted(z6);
                                if (messageObject.getDialogId() != ChatActivity.this.mergeDialogId) {
                                }
                                chatMessageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                                indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                                if (indexOf2 != -1) {
                                    ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                    chatMessageCell.getViewTreeObserver().addOnPreDrawListener(/* anonymous class already generated */);
                                }
                            }
                            z = z5;
                            if (itemViewType2 == viewHolder.getItemViewType()) {
                                messageObject3 = (MessageObject) ChatActivity.this.messages.get(indexOf - r0.messagesStartRow);
                                if (messageObject3.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                }
                                if (z4) {
                                    if (ChatActivity.this.currentChat != null) {
                                        if (UserObject.isUserSelf(ChatActivity.this.currentUser)) {
                                            if (messageObject3.getFromId() == messageObject.getFromId()) {
                                            }
                                            z4 = false;
                                        }
                                    }
                                    z4 = true;
                                }
                            }
                            chatMessageCell.setMessageObject(messageObject, access$11800, z, z4);
                            chatMessageCell.downloadAudioIfNeed();
                            if (ChatActivity.this.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                            }
                            chatMessageCell.setHighlighted(z6);
                            if (messageObject.getDialogId() != ChatActivity.this.mergeDialogId) {
                            }
                            chatMessageCell.setHighlightedText(DataQuery.getInstance(ChatActivity.this.currentAccount).getLastSearchQuery());
                            indexOf2 = ChatActivity.this.animatingMessageObjects.indexOf(messageObject);
                            if (indexOf2 != -1) {
                                ChatActivity.this.animatingMessageObjects.remove(indexOf2);
                                chatMessageCell.getViewTreeObserver().addOnPreDrawListener(/* anonymous class already generated */);
                            }
                        } else if (view instanceof ChatActionCell) {
                            ChatActionCell chatActionCell = (ChatActionCell) view;
                            chatActionCell.setMessageObject(messageObject);
                            chatActionCell.setAlpha(1.0f);
                        } else if (view instanceof ChatUnreadCell) {
                            ((ChatUnreadCell) view).setText(LocaleController.getString("UnreadMessages", C0446R.string.UnreadMessages));
                            if (ChatActivity.this.createUnreadMessageAfterId != 0) {
                                ChatActivity.this.createUnreadMessageAfterId = 0;
                            }
                        }
                        if (messageObject != null && messageObject.messageOwner != null && messageObject.messageOwner.media_unread && messageObject.messageOwner.mentioned) {
                            if (!(messageObject.isVoice() || messageObject.isRoundVideo())) {
                                ChatActivity.this.newMentionsCount = ChatActivity.this.newMentionsCount - 1;
                                if (ChatActivity.this.newMentionsCount <= 0) {
                                    i2 = false;
                                    ChatActivity.this.newMentionsCount = 0;
                                    ChatActivity.this.hasAllMentionsLocal = true;
                                    ChatActivity.this.showMentiondownButton(false, true);
                                } else {
                                    i2 = false;
                                    ChatActivity.this.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(ChatActivity.this.newMentionsCount)}));
                                }
                                MessagesController instance = MessagesController.getInstance(ChatActivity.this.currentAccount);
                                int id = messageObject.getId();
                                if (ChatObject.isChannel(ChatActivity.this.currentChat)) {
                                    i2 = ChatActivity.this.currentChat.id;
                                }
                                instance.markMentionMessageAsRead(id, i2, ChatActivity.this.dialog_id);
                                messageObject.setContentIsRead();
                            }
                            if (z2) {
                                ((ChatMessageCell) view).setHighlightedAnimated();
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                }
            }
            z = false;
            ChatLoadingCell chatLoadingCell = (ChatLoadingCell) viewHolder2.itemView;
            if (ChatActivity.this.loadsCount > 1) {
                z = true;
            }
            chatLoadingCell.setProgressVisible(z);
        }

        public int getItemViewType(int i) {
            if (i < this.messagesStartRow || i >= this.messagesEndRow) {
                return i == this.botInfoRow ? 3 : 4;
            } else {
                return ((MessageObject) ChatActivity.this.messages.get(i - this.messagesStartRow)).contentType;
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ChatMessageCell) {
                boolean z;
                boolean z2;
                int i;
                final ChatMessageCell chatMessageCell = (ChatMessageCell) viewHolder.itemView;
                MessageObject messageObject = chatMessageCell.getMessageObject();
                boolean z3 = false;
                if (ChatActivity.this.actionBar.isActionModeShowed()) {
                    MessageObject editingMessageObject = ChatActivity.this.chatActivityEnterView != null ? ChatActivity.this.chatActivityEnterView.getEditingMessageObject() : null;
                    int i2 = messageObject.getDialogId() == ChatActivity.this.dialog_id ? 0 : 1;
                    if (editingMessageObject != messageObject) {
                        if (ChatActivity.this.selectedMessagesIds[i2].indexOfKey(messageObject.getId()) < 0) {
                            chatMessageCell.setBackgroundDrawable(null);
                            z = false;
                            z2 = z;
                            i = 1;
                        }
                    }
                    ChatActivity.this.setCellSelectionBackground(messageObject, chatMessageCell, i2);
                    z = true;
                    z2 = z;
                    i = 1;
                } else {
                    chatMessageCell.setBackgroundDrawable(null);
                    i = 0;
                    z2 = i;
                }
                boolean z4 = i ^ 1;
                z = i != 0 && z2;
                chatMessageCell.setCheckPressed(z4, z);
                chatMessageCell.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        chatMessageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                        int measuredHeight = ChatActivity.this.chatListView.getMeasuredHeight();
                        int top = chatMessageCell.getTop();
                        chatMessageCell.getBottom();
                        top = top >= 0 ? 0 : -top;
                        int measuredHeight2 = chatMessageCell.getMeasuredHeight();
                        if (measuredHeight2 > measuredHeight) {
                            measuredHeight2 = top + measuredHeight;
                        }
                        chatMessageCell.setVisiblePart(top, measuredHeight2 - top);
                        return true;
                    }
                });
                if (ChatActivity.this.highlightMessageId != ConnectionsManager.DEFAULT_DATACENTER_ID && chatMessageCell.getMessageObject().getId() == ChatActivity.this.highlightMessageId) {
                    z3 = true;
                }
                chatMessageCell.setHighlighted(z3);
            }
        }

        public void updateRowAtPosition(int i) {
            if (ChatActivity.this.chatLayoutManager != null) {
                int indexOf;
                if (!(ChatActivity.this.wasManualScroll || ChatActivity.this.unreadMessageObject == null)) {
                    indexOf = ChatActivity.this.messages.indexOf(ChatActivity.this.unreadMessageObject);
                    if (indexOf >= 0) {
                        indexOf += this.messagesStartRow;
                        notifyItemChanged(i);
                        if (indexOf != -1) {
                            ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(indexOf, ((ChatActivity.this.chatListView.getMeasuredHeight() - ChatActivity.this.chatListView.getPaddingBottom()) - ChatActivity.this.chatListView.getPaddingTop()) - AndroidUtilities.dp(29.0f));
                        }
                    }
                }
                indexOf = -1;
                notifyItemChanged(i);
                if (indexOf != -1) {
                    ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(indexOf, ((ChatActivity.this.chatListView.getMeasuredHeight() - ChatActivity.this.chatListView.getPaddingBottom()) - ChatActivity.this.chatListView.getPaddingTop()) - AndroidUtilities.dp(29.0f));
                }
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            messageObject = ChatActivity.this.messages.indexOf(messageObject);
            if (messageObject != -1) {
                updateRowAtPosition(messageObject + this.messagesStartRow);
            }
        }

        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemChanged(int i) {
            try {
                super.notifyItemChanged(i);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRangeChanged(int i, int i2) {
            try {
                super.notifyItemRangeChanged(i, i2);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemInserted(int i) {
            updateRows();
            try {
                super.notifyItemInserted(i);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemMoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemMoved(i, i2);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeInserted(i, i2);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRemoved(int i) {
            updateRows();
            try {
                super.notifyItemRemoved(i);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(i, i2);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$1 */
    class C23331 extends EmptyPhotoViewerProvider {
        C23331() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            ImageReceiver imageReceiver;
            View childAt;
            FileLocation fileLocation2 = fileLocation;
            int childCount = ChatActivity.this.chatListView.getChildCount();
            int i2 = 0;
            int i3 = 0;
            while (true) {
                imageReceiver = null;
                if (i3 >= childCount) {
                    return null;
                }
                childAt = ChatActivity.this.chatListView.getChildAt(i3);
                MessageObject messageObject2;
                if (childAt instanceof ChatMessageCell) {
                    if (messageObject != null) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                        messageObject2 = chatMessageCell.getMessageObject();
                        if (messageObject2 != null && messageObject2.getId() == messageObject.getId()) {
                            imageReceiver = chatMessageCell.getPhotoImage();
                        }
                    }
                } else if (childAt instanceof ChatActionCell) {
                    ChatActionCell chatActionCell = (ChatActionCell) childAt;
                    messageObject2 = chatActionCell.getMessageObject();
                    if (messageObject2 != null) {
                        if (messageObject != null) {
                            if (messageObject2.getId() == messageObject.getId()) {
                                imageReceiver = chatActionCell.getPhotoImage();
                            }
                        } else if (fileLocation2 != null && messageObject2.photoThumbs != null) {
                            for (int i4 = 0; i4 < messageObject2.photoThumbs.size(); i4++) {
                                PhotoSize photoSize = (PhotoSize) messageObject2.photoThumbs.get(i4);
                                if (photoSize.location.volume_id == fileLocation2.volume_id && photoSize.location.local_id == fileLocation2.local_id) {
                                    imageReceiver = chatActionCell.getPhotoImage();
                                    break;
                                }
                            }
                        }
                    }
                }
                if (imageReceiver != null) {
                    break;
                }
                i3++;
            }
            int[] iArr = new int[2];
            childAt.getLocationInWindow(iArr);
            PlaceProviderObject placeProviderObject = new PlaceProviderObject();
            placeProviderObject.viewX = iArr[0];
            int i5 = iArr[1];
            if (VERSION.SDK_INT < 21) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            placeProviderObject.viewY = i5 - i2;
            placeProviderObject.parentView = ChatActivity.this.chatListView;
            placeProviderObject.imageReceiver = imageReceiver;
            placeProviderObject.thumb = imageReceiver.getBitmapSafe();
            placeProviderObject.radius = imageReceiver.getRoundRadius();
            if ((childAt instanceof ChatActionCell) && ChatActivity.this.currentChat != null) {
                placeProviderObject.dialogId = -ChatActivity.this.currentChat.id;
            }
            if ((ChatActivity.this.pinnedMessageView != null && ChatActivity.this.pinnedMessageView.getTag() == null) || (ChatActivity.this.reportSpamView != null && ChatActivity.this.reportSpamView.getTag() == null)) {
                placeProviderObject.clipTopAddition = AndroidUtilities.dp(48.0f);
            }
            return placeProviderObject;
        }
    }

    /* renamed from: org.telegram.ui.ChatActivity$2 */
    class C23342 extends EmptyPhotoViewerProvider {
        C23342() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            if (i >= 0) {
                if (i < ChatActivity.this.botContextResults.size()) {
                    fileLocation = ChatActivity.this.mentionListView.getChildCount();
                    i = ChatActivity.this.botContextResults.get(i);
                    int i2 = 0;
                    int i3 = 0;
                    while (i3 < fileLocation) {
                        ImageReceiver photoImage;
                        View childAt = ChatActivity.this.mentionListView.getChildAt(i3);
                        if (childAt instanceof ContextLinkCell) {
                            ContextLinkCell contextLinkCell = (ContextLinkCell) childAt;
                            if (contextLinkCell.getResult() == i) {
                                photoImage = contextLinkCell.getPhotoImage();
                                if (photoImage == null) {
                                    messageObject = new int[2];
                                    childAt.getLocationInWindow(messageObject);
                                    fileLocation = new PlaceProviderObject();
                                    fileLocation.viewX = messageObject[0];
                                    messageObject = messageObject[1];
                                    if (VERSION.SDK_INT >= 21) {
                                        i2 = AndroidUtilities.statusBarHeight;
                                    }
                                    fileLocation.viewY = messageObject - i2;
                                    fileLocation.parentView = ChatActivity.this.mentionListView;
                                    fileLocation.imageReceiver = photoImage;
                                    fileLocation.thumb = photoImage.getBitmapSafe();
                                    fileLocation.radius = photoImage.getRoundRadius();
                                    return fileLocation;
                                }
                                i3++;
                            }
                        }
                        photoImage = null;
                        if (photoImage == null) {
                            i3++;
                        } else {
                            messageObject = new int[2];
                            childAt.getLocationInWindow(messageObject);
                            fileLocation = new PlaceProviderObject();
                            fileLocation.viewX = messageObject[0];
                            messageObject = messageObject[1];
                            if (VERSION.SDK_INT >= 21) {
                                i2 = AndroidUtilities.statusBarHeight;
                            }
                            fileLocation.viewY = messageObject - i2;
                            fileLocation.parentView = ChatActivity.this.mentionListView;
                            fileLocation.imageReceiver = photoImage;
                            fileLocation.thumb = photoImage.getBitmapSafe();
                            fileLocation.radius = photoImage.getRoundRadius();
                            return fileLocation;
                        }
                    }
                    return null;
                }
            }
            return null;
        }

        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo) {
            if (i >= 0) {
                if (i < ChatActivity.this.botContextResults.size()) {
                    ChatActivity.this.sendBotInlineResult((BotInlineResult) ChatActivity.this.botContextResults.get(i));
                }
            }
        }
    }

    public ChatActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        final int i = this.arguments.getInt("chat_id", 0);
        final int i2 = this.arguments.getInt("user_id", 0);
        final int i3 = this.arguments.getInt("enc_id", 0);
        this.inlineReturn = this.arguments.getLong("inline_return", 0);
        String string = this.arguments.getString("inline_query");
        this.startLoadFromMessageId = this.arguments.getInt("message_id", 0);
        int i4 = this.arguments.getInt("migrated_to", 0);
        this.scrollToTopOnResume = this.arguments.getBoolean("scrollToTopOnResume", false);
        final CountDownLatch countDownLatch;
        if (i != 0) {
            r1.currentChat = MessagesController.getInstance(r1.currentAccount).getChat(Integer.valueOf(i));
            if (r1.currentChat == null) {
                countDownLatch = new CountDownLatch(1);
                final MessagesStorage instance = MessagesStorage.getInstance(r1.currentAccount);
                instance.getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        ChatActivity.this.currentChat = instance.getChat(i);
                        countDownLatch.countDown();
                    }
                });
                try {
                    countDownLatch.await();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                if (r1.currentChat == null) {
                    return false;
                }
                MessagesController.getInstance(r1.currentAccount).putChat(r1.currentChat, true);
            }
            if (i > 0) {
                r1.dialog_id = (long) (-i);
            } else {
                r1.isBroadcast = true;
                r1.dialog_id = AndroidUtilities.makeBroadcastId(i);
            }
            if (ChatObject.isChannel(r1.currentChat)) {
                MessagesController.getInstance(r1.currentAccount).startShortPoll(i, false);
            }
        } else if (i2 != 0) {
            r1.currentUser = MessagesController.getInstance(r1.currentAccount).getUser(Integer.valueOf(i2));
            if (r1.currentUser == null) {
                r2 = MessagesStorage.getInstance(r1.currentAccount);
                countDownLatch = new CountDownLatch(1);
                r2.getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        ChatActivity.this.currentUser = r2.getUser(i2);
                        countDownLatch.countDown();
                    }
                });
                try {
                    countDownLatch.await();
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
                if (r1.currentUser == null) {
                    return false;
                }
                MessagesController.getInstance(r1.currentAccount).putUser(r1.currentUser, true);
            }
            r1.dialog_id = (long) i2;
            r1.botUser = r1.arguments.getString("botUser");
            if (string != null) {
                MessagesController.getInstance(r1.currentAccount).sendBotStart(r1.currentUser, string);
            }
        } else if (i3 == 0) {
            return false;
        } else {
            final CountDownLatch countDownLatch2;
            r1.currentEncryptedChat = MessagesController.getInstance(r1.currentAccount).getEncryptedChat(Integer.valueOf(i3));
            r2 = MessagesStorage.getInstance(r1.currentAccount);
            if (r1.currentEncryptedChat == null) {
                countDownLatch2 = new CountDownLatch(1);
                r2.getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        ChatActivity.this.currentEncryptedChat = r2.getEncryptedChat(i3);
                        countDownLatch2.countDown();
                    }
                });
                try {
                    countDownLatch2.await();
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
                if (r1.currentEncryptedChat == null) {
                    return false;
                }
                MessagesController.getInstance(r1.currentAccount).putEncryptedChat(r1.currentEncryptedChat, true);
            }
            r1.currentUser = MessagesController.getInstance(r1.currentAccount).getUser(Integer.valueOf(r1.currentEncryptedChat.user_id));
            if (r1.currentUser == null) {
                countDownLatch2 = new CountDownLatch(1);
                r2.getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        ChatActivity.this.currentUser = r2.getUser(ChatActivity.this.currentEncryptedChat.user_id);
                        countDownLatch2.countDown();
                    }
                });
                try {
                    countDownLatch2.await();
                } catch (Throwable e222) {
                    FileLog.m3e(e222);
                }
                if (r1.currentUser == null) {
                    return false;
                }
                MessagesController.getInstance(r1.currentAccount).putUser(r1.currentUser, true);
            }
            r1.dialog_id = ((long) i3) << 32;
            int[] iArr = r1.maxMessageId;
            r1.maxMessageId[1] = Integer.MIN_VALUE;
            iArr[0] = Integer.MIN_VALUE;
            iArr = r1.minMessageId;
            r1.minMessageId[1] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            iArr[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
        }
        if (r1.currentUser != null) {
            MediaController.getInstance().startMediaObserver();
        }
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messagesDidLoaded);
        NotificationCenter.getGlobalInstance().addObserver(r1, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.closeChats);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messagesRead);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.historyCleared);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messageReceivedByAck);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messageSendError);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.encryptedChatUpdated);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messagesReadEncrypted);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.screenshotTook);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.blockedUsersDidLoaded);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.replaceMessagesObjects);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.didLoadedReplyMessages);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.didReceivedWebpages);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.didReceivedWebpagesInUpdates);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.messagesReadContent);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.botInfoDidLoaded);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.botKeyboardDidLoaded);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.chatSearchResultsAvailable);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.chatSearchResultsLoading);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.didUpdatedMessagesViews);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.chatInfoCantLoad);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.didLoadedPinnedMessage);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.peerSettingsDidLoaded);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.newDraftReceived);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.userInfoDidLoaded);
        NotificationCenter.getGlobalInstance().addObserver(r1, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.channelRightsUpdated);
        NotificationCenter.getInstance(r1.currentAccount).addObserver(r1, NotificationCenter.updateMentionsCount);
        super.onFragmentCreate();
        if (r1.currentEncryptedChat == null && !r1.isBroadcast) {
            DataQuery.getInstance(r1.currentAccount).loadBotKeyboard(r1.dialog_id);
        }
        r1.loading = true;
        MessagesController.getInstance(r1.currentAccount).loadPeerSettings(r1.currentUser, r1.currentChat);
        MessagesController.getInstance(r1.currentAccount).setLastCreatedDialogId(r1.dialog_id, true);
        if (r1.startLoadFromMessageId == 0) {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(r1.currentAccount);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("diditem");
            stringBuilder.append(r1.dialog_id);
            i3 = notificationsSettings.getInt(stringBuilder.toString(), 0);
            if (i3 != 0) {
                r1.wasManualScroll = true;
                r1.loadingFromOldPosition = true;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("diditemo");
                stringBuilder2.append(r1.dialog_id);
                r1.startLoadFromMessageOffset = notificationsSettings.getInt(stringBuilder2.toString(), 0);
                r1.startLoadFromMessageId = i3;
            }
        } else {
            r1.needSelectFromMessageId = true;
        }
        MessagesController instance2;
        long j;
        int i5;
        boolean isChannel;
        if (r1.startLoadFromMessageId != 0) {
            r1.waitingForLoad.add(Integer.valueOf(r1.lastLoadIndex));
            int i6;
            if (i4 != 0) {
                r1.mergeDialogId = (long) i4;
                instance2 = MessagesController.getInstance(r1.currentAccount);
                j = r1.mergeDialogId;
                if (r1.loadingFromOldPosition) {
                    i5 = 50;
                } else {
                    i5 = AndroidUtilities.isTablet() ? bot_help : 20;
                }
                i6 = r1.startLoadFromMessageId;
                i = r1.classGuid;
                isChannel = ChatObject.isChannel(r1.currentChat);
                i3 = r1.lastLoadIndex;
                r1.lastLoadIndex = i3 + 1;
                instance2.loadMessages(j, i5, i6, 0, true, 0, i, 3, 0, isChannel, i3);
            } else {
                instance2 = MessagesController.getInstance(r1.currentAccount);
                j = r1.dialog_id;
                if (r1.loadingFromOldPosition) {
                    i5 = 50;
                } else {
                    i5 = AndroidUtilities.isTablet() ? bot_help : 20;
                }
                i6 = r1.startLoadFromMessageId;
                i = r1.classGuid;
                isChannel = ChatObject.isChannel(r1.currentChat);
                i3 = r1.lastLoadIndex;
                r1.lastLoadIndex = i3 + 1;
                instance2.loadMessages(j, i5, i6, 0, true, 0, i, 3, 0, isChannel, i3);
            }
        } else {
            r1.waitingForLoad.add(Integer.valueOf(r1.lastLoadIndex));
            instance2 = MessagesController.getInstance(r1.currentAccount);
            j = r1.dialog_id;
            i5 = AndroidUtilities.isTablet() ? bot_help : 20;
            i = r1.classGuid;
            isChannel = ChatObject.isChannel(r1.currentChat);
            i3 = r1.lastLoadIndex;
            r1.lastLoadIndex = i3 + 1;
            instance2.loadMessages(j, i5, 0, 0, true, 0, i, 2, 0, isChannel, i3);
        }
        if (r1.currentChat != null) {
            CountDownLatch countDownLatch3 = r1.isBroadcast ? new CountDownLatch(1) : null;
            MessagesController.getInstance(r1.currentAccount).loadChatInfo(r1.currentChat.id, countDownLatch3, ChatObject.isChannel(r1.currentChat));
            if (r1.isBroadcast && countDownLatch3 != null) {
                try {
                    countDownLatch3.await();
                } catch (Throwable e2222) {
                    FileLog.m3e(e2222);
                }
            }
        }
        if (i2 != 0 && r1.currentUser.bot) {
            DataQuery.getInstance(r1.currentAccount).loadBotInfo(i2, true, r1.classGuid);
        } else if (r1.info instanceof TL_chatFull) {
            for (i = 0; i < r1.info.participants.participants.size(); i++) {
                User user = MessagesController.getInstance(r1.currentAccount).getUser(Integer.valueOf(((ChatParticipant) r1.info.participants.participants.get(i)).user_id));
                if (user != null && user.bot) {
                    DataQuery.getInstance(r1.currentAccount).loadBotInfo(user.id, true, r1.classGuid);
                }
            }
        }
        if (r1.currentUser != null) {
            r1.userBlocked = MessagesController.getInstance(r1.currentAccount).blockedUsers.contains(Integer.valueOf(r1.currentUser.id));
        }
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(r1.currentAccount).postNotificationName(NotificationCenter.openedChatChanged, Long.valueOf(r1.dialog_id), Boolean.valueOf(false));
        }
        if (!(r1.currentEncryptedChat == null || AndroidUtilities.getMyLayerVersion(r1.currentEncryptedChat.layer) == 73)) {
            SecretChatHelper.getInstance(r1.currentAccount).sendNotifyLayerMessage(r1.currentEncryptedChat, null);
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
        MessagesController.getInstance(this.currentAccount).setLastCreatedDialogId(this.dialog_id, false);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDidLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesRead);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.historyCleared);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByAck);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageSendError);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesReadEncrypted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.removeAllMessagesFromDialog);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.screenshotTook);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didCreatedNewDeleteTask);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateMessageMedia);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.replaceMessagesObjects);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didLoadedReplyMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedWebpages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedWebpagesInUpdates);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesReadContent);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botInfoDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botKeyboardDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatSearchResultsAvailable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatSearchResultsLoading);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatedMessagesViews);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoCantLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didLoadedPinnedMessage);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.peerSettingsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newDraftReceived);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.channelRightsUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateMentionsCount);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.openedChatChanged, Long.valueOf(this.dialog_id), Boolean.valueOf(true));
        }
        if (this.currentUser != null) {
            MediaController.getInstance().stopMediaObserver();
        }
        if (this.currentEncryptedChat != null) {
            try {
                if (VERSION.SDK_INT >= edit && (SharedConfig.passcodeHash.length() == 0 || SharedConfig.allowScreenCapture)) {
                    MediaController.getInstance().setFlagSecure(this, false);
                }
            } catch (Throwable th) {
                FileLog.m3e(th);
            }
        }
        if (this.currentUser != null) {
            MessagesController.getInstance(this.currentAccount).cancelLoadFullUser(this.currentUser.id);
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
            MessagesController.getInstance(this.currentAccount).startShortPoll(this.currentChat.id, true);
        }
    }

    public android.view.View createView(android.content.Context r34) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r33 = this;
        r7 = r33;
        r8 = r34;
        r1 = r7.chatMessageCellsCache;
        r1 = r1.isEmpty();
        r9 = 8;
        r10 = 0;
        if (r1 == 0) goto L_0x001f;
    L_0x000f:
        r1 = r10;
    L_0x0010:
        if (r1 >= r9) goto L_0x001f;
    L_0x0012:
        r2 = r7.chatMessageCellsCache;
        r3 = new org.telegram.ui.Cells.ChatMessageCell;
        r3.<init>(r8);
        r2.add(r3);
        r1 = r1 + 1;
        goto L_0x0010;
    L_0x001f:
        r11 = 1;
        r1 = r11;
    L_0x0021:
        if (r1 < 0) goto L_0x003b;
    L_0x0023:
        r2 = r7.selectedMessagesIds;
        r2 = r2[r1];
        r2.clear();
        r2 = r7.selectedMessagesCanCopyIds;
        r2 = r2[r1];
        r2.clear();
        r2 = r7.selectedMessagesCanStarIds;
        r2 = r2[r1];
        r2.clear();
        r1 = r1 + -1;
        goto L_0x0021;
    L_0x003b:
        r7.cantDeleteMessagesCount = r10;
        r7.canEditMessagesCount = r10;
        r12 = 0;
        r7.roundVideoContainer = r12;
        r7.hasOwnBackground = r11;
        r1 = r7.chatAttachAlert;
        if (r1 == 0) goto L_0x005c;
    L_0x0048:
        r1 = r7.chatAttachAlert;	 Catch:{ Exception -> 0x0055 }
        r1 = r1.isShowing();	 Catch:{ Exception -> 0x0055 }
        if (r1 == 0) goto L_0x0055;	 Catch:{ Exception -> 0x0055 }
    L_0x0050:
        r1 = r7.chatAttachAlert;	 Catch:{ Exception -> 0x0055 }
        r1.dismiss();	 Catch:{ Exception -> 0x0055 }
    L_0x0055:
        r1 = r7.chatAttachAlert;
        r1.onDestroy();
        r7.chatAttachAlert = r12;
    L_0x005c:
        org.telegram.ui.ActionBar.Theme.createChatResources(r8, r10);
        r1 = r7.actionBar;
        r1.setAddToContainer(r10);
        r1 = r7.actionBar;
        r2 = new org.telegram.ui.ActionBar.BackDrawable;
        r2.<init>(r10);
        r1.setBackButtonDrawable(r2);
        r1 = r7.actionBar;
        r2 = new org.telegram.ui.ChatActivity$9;
        r2.<init>();
        r1.setActionBarMenuOnItemClick(r2);
        r1 = new org.telegram.ui.Components.ChatAvatarContainer;
        r2 = r7.currentEncryptedChat;
        if (r2 == 0) goto L_0x0080;
    L_0x007e:
        r2 = r11;
        goto L_0x0081;
    L_0x0080:
        r2 = r10;
    L_0x0081:
        r1.<init>(r8, r7, r2);
        r7.avatarContainer = r1;
        r1 = r7.actionBar;
        r2 = r7.avatarContainer;
        r13 = -2;
        r14 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r15 = 51;
        r16 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r17 = 0;
        r18 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r19 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19);
        r1.addView(r2, r10, r3);
        r1 = r7.currentChat;
        if (r1 == 0) goto L_0x00e1;
    L_0x00a2:
        r1 = r7.currentChat;
        r1 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r1 != 0) goto L_0x00e1;
    L_0x00aa:
        r1 = r7.currentChat;
        r1 = r1.participants_count;
        r2 = r7.info;
        if (r2 == 0) goto L_0x00bc;
    L_0x00b2:
        r1 = r7.info;
        r1 = r1.participants;
        r1 = r1.participants;
        r1 = r1.size();
    L_0x00bc:
        if (r1 == 0) goto L_0x00dc;
    L_0x00be:
        r1 = r7.currentChat;
        r1 = r1.deactivated;
        if (r1 != 0) goto L_0x00dc;
    L_0x00c4:
        r1 = r7.currentChat;
        r1 = r1.left;
        if (r1 != 0) goto L_0x00dc;
    L_0x00ca:
        r1 = r7.currentChat;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatForbidden;
        if (r1 != 0) goto L_0x00dc;
    L_0x00d0:
        r1 = r7.info;
        if (r1 == 0) goto L_0x00e1;
    L_0x00d4:
        r1 = r7.info;
        r1 = r1.participants;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
        if (r1 == 0) goto L_0x00e1;
    L_0x00dc:
        r1 = r7.avatarContainer;
        r1.setEnabled(r10);
    L_0x00e1:
        r1 = r7.actionBar;
        r1 = r1.createMenu();
        r2 = r7.currentEncryptedChat;
        if (r2 != 0) goto L_0x011c;
    L_0x00eb:
        r2 = r7.isBroadcast;
        if (r2 != 0) goto L_0x011c;
    L_0x00ef:
        r2 = NUM; // 0x7f0700ac float:1.7944927E38 double:1.052935588E-314;
        r2 = r1.addItem(r10, r2);
        r2 = r2.setIsSearchField(r11);
        r3 = new org.telegram.ui.ChatActivity$10;
        r3.<init>();
        r2 = r2.setActionBarMenuItemSearchListener(r3);
        r7.searchItem = r2;
        r2 = r7.searchItem;
        r2 = r2.getSearchField();
        r3 = "Search";
        r4 = NUM; // 0x7f0c059a float:1.86121E38 double:1.053098107E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
        r2.setHint(r3);
        r2 = r7.searchItem;
        r2.setVisibility(r9);
    L_0x011c:
        r2 = NUM; // 0x7f0700a9 float:1.794492E38 double:1.0529355865E-314;
        r2 = r1.addItem(r10, r2);
        r7.headerItem = r2;
        r2 = r7.currentUser;
        if (r2 == 0) goto L_0x015c;
    L_0x0129:
        r2 = r7.headerItem;
        r3 = 32;
        r4 = "Call";
        r5 = NUM; // 0x7f0c00f1 float:1.860968E38 double:1.0530975175E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2.addSubItem(r3, r4);
        r2 = r7.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r3 = r7.currentUser;
        r3 = r3.id;
        r2 = r2.getUserFull(r3);
        if (r2 == 0) goto L_0x0155;
    L_0x0149:
        r2 = r2.phone_calls_available;
        if (r2 == 0) goto L_0x0155;
    L_0x014d:
        r2 = r7.headerItem;
        r3 = 32;
        r2.showSubItem(r3);
        goto L_0x015c;
    L_0x0155:
        r2 = r7.headerItem;
        r3 = 32;
        r2.hideSubItem(r3);
    L_0x015c:
        r2 = r7.searchItem;
        if (r2 == 0) goto L_0x0170;
    L_0x0160:
        r2 = r7.headerItem;
        r3 = 40;
        r4 = "Search";
        r5 = NUM; // 0x7f0c059a float:1.86121E38 double:1.053098107E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2.addSubItem(r3, r4);
    L_0x0170:
        r2 = r7.currentChat;
        r2 = org.telegram.messenger.ChatObject.isChannel(r2);
        if (r2 == 0) goto L_0x01a4;
    L_0x0178:
        r2 = r7.currentChat;
        r2 = r2.creator;
        if (r2 != 0) goto L_0x01a4;
    L_0x017e:
        r2 = r7.currentChat;
        r2 = r2.megagroup;
        if (r2 == 0) goto L_0x0194;
    L_0x0184:
        r2 = r7.currentChat;
        r2 = r2.username;
        if (r2 == 0) goto L_0x01a4;
    L_0x018a:
        r2 = r7.currentChat;
        r2 = r2.username;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x01a4;
    L_0x0194:
        r2 = r7.headerItem;
        r3 = 21;
        r4 = "ReportChat";
        r5 = NUM; // 0x7f0c055e float:1.8611979E38 double:1.0530980773E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2.addSubItem(r3, r4);
    L_0x01a4:
        r2 = r7.currentUser;
        r13 = 17;
        if (r2 == 0) goto L_0x01b4;
    L_0x01aa:
        r2 = r7.headerItem;
        r3 = "";
        r2 = r2.addSubItem(r13, r3);
        r7.addContactItem = r2;
    L_0x01b4:
        r2 = r7.currentEncryptedChat;
        if (r2 == 0) goto L_0x01cb;
    L_0x01b8:
        r2 = r7.headerItem;
        r3 = 13;
        r4 = "SetTimer";
        r5 = NUM; // 0x7f0c05ea float:1.8612263E38 double:1.0530981465E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2 = r2.addSubItem(r3, r4);
        r7.timeItem2 = r2;
    L_0x01cb:
        r2 = r7.currentChat;
        r2 = org.telegram.messenger.ChatObject.isChannel(r2);
        if (r2 == 0) goto L_0x01e7;
    L_0x01d3:
        r2 = r7.currentChat;
        if (r2 == 0) goto L_0x01f7;
    L_0x01d7:
        r2 = r7.currentChat;
        r2 = r2.megagroup;
        if (r2 == 0) goto L_0x01f7;
    L_0x01dd:
        r2 = r7.currentChat;
        r2 = r2.username;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 == 0) goto L_0x01f7;
    L_0x01e7:
        r2 = r7.headerItem;
        r3 = 15;
        r4 = "ClearHistory";
        r5 = NUM; // 0x7f0c018a float:1.8609991E38 double:1.053097593E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2.addSubItem(r3, r4);
    L_0x01f7:
        r2 = r7.currentChat;
        r2 = org.telegram.messenger.ChatObject.isChannel(r2);
        if (r2 != 0) goto L_0x0228;
    L_0x01ff:
        r2 = r7.currentChat;
        if (r2 == 0) goto L_0x0218;
    L_0x0203:
        r2 = r7.isBroadcast;
        if (r2 != 0) goto L_0x0218;
    L_0x0207:
        r2 = r7.headerItem;
        r3 = 16;
        r4 = "DeleteAndExit";
        r5 = NUM; // 0x7f0c01f1 float:1.86102E38 double:1.053097644E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2.addSubItem(r3, r4);
        goto L_0x0228;
    L_0x0218:
        r2 = r7.headerItem;
        r3 = 16;
        r4 = "DeleteChatUser";
        r5 = NUM; // 0x7f0c01f5 float:1.8610208E38 double:1.053097646E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2.addSubItem(r3, r4);
    L_0x0228:
        r2 = r7.currentUser;
        if (r2 == 0) goto L_0x024a;
    L_0x022c:
        r2 = r7.currentUser;
        r2 = r2.self;
        if (r2 != 0) goto L_0x0233;
    L_0x0232:
        goto L_0x024a;
    L_0x0233:
        r2 = r7.currentUser;
        r2 = r2.self;
        if (r2 == 0) goto L_0x0254;
    L_0x0239:
        r2 = r7.headerItem;
        r3 = 24;
        r4 = "AddShortcut";
        r5 = NUM; // 0x7f0c0047 float:1.8609336E38 double:1.0530974335E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2.addSubItem(r3, r4);
        goto L_0x0254;
    L_0x024a:
        r2 = r7.headerItem;
        r3 = 18;
        r2 = r2.addSubItem(r3, r12);
        r7.muteItem = r2;
    L_0x0254:
        r2 = r7.currentUser;
        if (r2 == 0) goto L_0x0285;
    L_0x0258:
        r2 = r7.currentEncryptedChat;
        if (r2 != 0) goto L_0x0285;
    L_0x025c:
        r2 = r7.currentUser;
        r2 = r2.bot;
        if (r2 == 0) goto L_0x0285;
    L_0x0262:
        r2 = r7.headerItem;
        r3 = 31;
        r4 = "BotSettings";
        r5 = NUM; // 0x7f0c00e5 float:1.8609656E38 double:1.0530975116E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2.addSubItem(r3, r4);
        r2 = r7.headerItem;
        r3 = 30;
        r4 = "BotHelp";
        r5 = NUM; // 0x7f0c00e0 float:1.8609646E38 double:1.053097509E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2.addSubItem(r3, r4);
        r33.updateBotButtons();
    L_0x0285:
        r33.updateTitle();
        r2 = r7.avatarContainer;
        r2.updateOnlineCount();
        r2 = r7.avatarContainer;
        r2.updateSubtitle();
        r33.updateTitleIcons();
        r2 = 14;
        r3 = NUM; // 0x7f0700a9 float:1.794492E38 double:1.0529355865E-314;
        r1 = r1.addItem(r2, r3);
        r1 = r1.setOverrideMenuClick(r11);
        r1 = r1.setAllowCloseAnimation(r10);
        r7.attachItem = r1;
        r1 = r7.attachItem;
        r1.setVisibility(r9);
        r1 = r7.actionModeViews;
        r1.clear();
        r1 = r7.actionBar;
        r1 = r1.createActionMode();
        r2 = new org.telegram.ui.Components.NumberTextView;
        r3 = r1.getContext();
        r2.<init>(r3);
        r7.selectedMessagesCountTextView = r2;
        r2 = r7.selectedMessagesCountTextView;
        r3 = 18;
        r2.setTextSize(r3);
        r2 = r7.selectedMessagesCountTextView;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r2.setTypeface(r3);
        r2 = r7.selectedMessagesCountTextView;
        r3 = "actionBarActionModeDefaultIcon";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setTextColor(r3);
        r2 = r7.selectedMessagesCountTextView;
        r14 = 0;
        r15 = -1;
        r16 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r17 = 65;
        r18 = 0;
        r19 = 0;
        r20 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r15, r16, r17, r18, r19, r20);
        r1.addView(r2, r3);
        r2 = r7.selectedMessagesCountTextView;
        r3 = new org.telegram.ui.ChatActivity$11;
        r3.<init>();
        r2.setOnTouchListener(r3);
        r2 = new org.telegram.ui.ChatActivity$12;
        r2.<init>(r8);
        r7.actionModeTitleContainer = r2;
        r2 = r7.actionModeTitleContainer;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r15, r16, r17, r18, r19, r20);
        r1.addView(r2, r3);
        r2 = r7.actionModeTitleContainer;
        r3 = new org.telegram.ui.ChatActivity$13;
        r3.<init>();
        r2.setOnTouchListener(r3);
        r2 = r7.actionModeTitleContainer;
        r2.setVisibility(r9);
        r2 = new org.telegram.ui.ActionBar.SimpleTextView;
        r2.<init>(r8);
        r7.actionModeTextView = r2;
        r2 = r7.actionModeTextView;
        r3 = 18;
        r2.setTextSize(r3);
        r2 = r7.actionModeTextView;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r2.setTypeface(r3);
        r2 = r7.actionModeTextView;
        r3 = "actionBarActionModeDefaultIcon";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setTextColor(r3);
        r2 = r7.actionModeTextView;
        r3 = "Edit";
        r4 = NUM; // 0x7f0c0215 float:1.8610273E38 double:1.053097662E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
        r2.setText(r3);
        r2 = r7.actionModeTitleContainer;
        r3 = r7.actionModeTextView;
        r4 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r14 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r4);
        r2.addView(r3, r4);
        r2 = new org.telegram.ui.ActionBar.SimpleTextView;
        r2.<init>(r8);
        r7.actionModeSubTextView = r2;
        r2 = r7.actionModeSubTextView;
        r3 = 3;
        r2.setGravity(r3);
        r2 = r7.actionModeSubTextView;
        r3 = "actionBarActionModeDefaultIcon";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setTextColor(r3);
        r2 = r7.actionModeTitleContainer;
        r3 = r7.actionModeSubTextView;
        r4 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r4);
        r2.addView(r3, r4);
        r2 = r7.currentEncryptedChat;
        r15 = 23;
        r3 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        if (r2 != 0) goto L_0x03fa;
    L_0x038b:
        r2 = r7.actionModeViews;
        r4 = NUM; // 0x7f07009b float:1.7944892E38 double:1.0529355796E-314;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.addItemWithWidth(r15, r4, r5);
        r2.add(r4);
        r2 = r7.isBroadcast;
        if (r2 != 0) goto L_0x03b1;
    L_0x039f:
        r2 = r7.actionModeViews;
        r4 = 19;
        r5 = NUM; // 0x7f0700aa float:1.7944923E38 double:1.052935587E-314;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.addItemWithWidth(r4, r5, r6);
        r2.add(r4);
    L_0x03b1:
        r2 = r7.actionModeViews;
        r4 = 22;
        r5 = NUM; // 0x7f0700a6 float:1.7944915E38 double:1.052935585E-314;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.addItemWithWidth(r4, r5, r6);
        r2.add(r4);
        r2 = r7.actionModeViews;
        r4 = 10;
        r5 = NUM; // 0x7f0700a3 float:1.7944909E38 double:1.0529355836E-314;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.addItemWithWidth(r4, r5, r6);
        r2.add(r4);
        r2 = r7.actionModeViews;
        r4 = 11;
        r5 = NUM; // 0x7f0700a7 float:1.7944917E38 double:1.0529355855E-314;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.addItemWithWidth(r4, r5, r6);
        r2.add(r4);
        r2 = r7.actionModeViews;
        r4 = 12;
        r5 = NUM; // 0x7f0700a4 float:1.794491E38 double:1.052935584E-314;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r1.addItemWithWidth(r4, r5, r3);
        r2.add(r3);
        goto L_0x0452;
    L_0x03fa:
        r2 = r7.actionModeViews;
        r4 = NUM; // 0x7f07009b float:1.7944892E38 double:1.0529355796E-314;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.addItemWithWidth(r15, r4, r5);
        r2.add(r4);
        r2 = r7.actionModeViews;
        r4 = 19;
        r5 = NUM; // 0x7f0700aa float:1.7944923E38 double:1.052935587E-314;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.addItemWithWidth(r4, r5, r6);
        r2.add(r4);
        r2 = r7.actionModeViews;
        r4 = 22;
        r5 = NUM; // 0x7f0700a6 float:1.7944915E38 double:1.052935585E-314;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.addItemWithWidth(r4, r5, r6);
        r2.add(r4);
        r2 = r7.actionModeViews;
        r4 = 10;
        r5 = NUM; // 0x7f0700a3 float:1.7944909E38 double:1.0529355836E-314;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.addItemWithWidth(r4, r5, r6);
        r2.add(r4);
        r2 = r7.actionModeViews;
        r4 = 12;
        r5 = NUM; // 0x7f0700a4 float:1.794491E38 double:1.052935584E-314;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r1.addItemWithWidth(r4, r5, r3);
        r2.add(r3);
    L_0x0452:
        r2 = r1.getItem(r15);
        r3 = r7.canEditMessagesCount;
        if (r3 != r11) goto L_0x046f;
    L_0x045a:
        r3 = r7.selectedMessagesIds;
        r3 = r3[r10];
        r3 = r3.size();
        r4 = r7.selectedMessagesIds;
        r4 = r4[r11];
        r4 = r4.size();
        r3 = r3 + r4;
        if (r3 != r11) goto L_0x046f;
    L_0x046d:
        r3 = r10;
        goto L_0x0470;
    L_0x046f:
        r3 = r9;
    L_0x0470:
        r2.setVisibility(r3);
        r2 = 10;
        r2 = r1.getItem(r2);
        r3 = r7.selectedMessagesCanCopyIds;
        r3 = r3[r10];
        r3 = r3.size();
        r4 = r7.selectedMessagesCanCopyIds;
        r4 = r4[r11];
        r4 = r4.size();
        r3 = r3 + r4;
        if (r3 == 0) goto L_0x048e;
    L_0x048c:
        r3 = r10;
        goto L_0x048f;
    L_0x048e:
        r3 = r9;
    L_0x048f:
        r2.setVisibility(r3);
        r2 = 22;
        r2 = r1.getItem(r2);
        r3 = r7.selectedMessagesCanStarIds;
        r3 = r3[r10];
        r3 = r3.size();
        r4 = r7.selectedMessagesCanStarIds;
        r4 = r4[r11];
        r4 = r4.size();
        r3 = r3 + r4;
        if (r3 == 0) goto L_0x04ad;
    L_0x04ab:
        r3 = r10;
        goto L_0x04ae;
    L_0x04ad:
        r3 = r9;
    L_0x04ae:
        r2.setVisibility(r3);
        r2 = 12;
        r1 = r1.getItem(r2);
        r2 = r7.cantDeleteMessagesCount;
        if (r2 != 0) goto L_0x04bd;
    L_0x04bb:
        r2 = r10;
        goto L_0x04be;
    L_0x04bd:
        r2 = r9;
    L_0x04be:
        r1.setVisibility(r2);
        r33.checkActionBarMenu();
        r1 = new org.telegram.ui.ChatActivity$14;
        r1.<init>(r8);
        r7.fragmentView = r1;
        r1 = r7.fragmentView;
        r1 = (org.telegram.ui.Components.SizeNotifierFrameLayout) r1;
        r7.contentView = r1;
        r1 = r7.contentView;
        r2 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper();
        r1.setBackgroundImage(r2);
        r1 = new android.widget.FrameLayout;
        r1.<init>(r8);
        r7.emptyViewContainer = r1;
        r1 = r7.emptyViewContainer;
        r6 = 4;
        r1.setVisibility(r6);
        r1 = r7.contentView;
        r2 = r7.emptyViewContainer;
        r5 = -2;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r5, r13);
        r1.addView(r2, r3);
        r1 = r7.emptyViewContainer;
        r2 = new org.telegram.ui.ChatActivity$15;
        r2.<init>();
        r1.setOnTouchListener(r2);
        r1 = r7.currentEncryptedChat;
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        if (r1 != 0) goto L_0x05cc;
    L_0x0503:
        r1 = r7.currentUser;
        if (r1 == 0) goto L_0x0522;
    L_0x0507:
        r1 = r7.currentUser;
        r1 = r1.self;
        if (r1 == 0) goto L_0x0522;
    L_0x050d:
        r1 = new org.telegram.ui.Components.ChatBigEmptyView;
        r1.<init>(r8, r10);
        r7.bigEmptyView = r1;
        r1 = r7.emptyViewContainer;
        r2 = r7.bigEmptyView;
        r3 = new android.widget.FrameLayout$LayoutParams;
        r3.<init>(r5, r5, r13);
        r1.addView(r2, r3);
        goto L_0x0620;
    L_0x0522:
        r1 = new android.widget.TextView;
        r1.<init>(r8);
        r7.emptyView = r1;
        r1 = r7.currentUser;
        if (r1 == 0) goto L_0x0561;
    L_0x052d:
        r1 = r7.currentUser;
        r1 = r1.id;
        r2 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r1 == r2) goto L_0x0561;
    L_0x0536:
        r1 = r7.currentUser;
        r1 = r1.id;
        r2 = 429000; // 0x68bc8 float:6.01157E-40 double:2.11954E-318;
        if (r1 == r2) goto L_0x0561;
    L_0x053f:
        r1 = r7.currentUser;
        r1 = r1.id;
        r2 = 4244000; // 0x40c220 float:5.94711E-39 double:2.0968146E-317;
        if (r1 == r2) goto L_0x0561;
    L_0x0548:
        r1 = r7.currentUser;
        r1 = r1.id;
        r1 = org.telegram.messenger.MessagesController.isSupportId(r1);
        if (r1 == 0) goto L_0x0561;
    L_0x0552:
        r1 = r7.emptyView;
        r2 = "GotAQuestion";
        r3 = NUM; // 0x7f0c02fd float:1.8610744E38 double:1.0530977764E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r1.setText(r2);
        goto L_0x056f;
    L_0x0561:
        r1 = r7.emptyView;
        r2 = "NoMessages";
        r3 = NUM; // 0x7f0c0407 float:1.8611283E38 double:1.053097908E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r1.setText(r2);
    L_0x056f:
        r1 = r7.emptyView;
        r1.setTextSize(r11, r4);
        r1 = r7.emptyView;
        r1.setGravity(r13);
        r1 = r7.emptyView;
        r2 = "chat_serviceText";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.emptyView;
        r2 = NUM; // 0x7f0701e4 float:1.794556E38 double:1.052935742E-314;
        r1.setBackgroundResource(r2);
        r1 = r7.emptyView;
        r1 = r1.getBackground();
        r2 = org.telegram.ui.ActionBar.Theme.colorFilter;
        r1.setColorFilter(r2);
        r1 = r7.emptyView;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r1.setTypeface(r2);
        r1 = r7.emptyView;
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.setPadding(r2, r3, r4, r6);
        r1 = r7.emptyViewContainer;
        r2 = r7.emptyView;
        r3 = new android.widget.FrameLayout$LayoutParams;
        r3.<init>(r5, r5, r13);
        r1.addView(r2, r3);
        goto L_0x0620;
    L_0x05cc:
        r1 = new org.telegram.ui.Components.ChatBigEmptyView;
        r1.<init>(r8, r11);
        r7.bigEmptyView = r1;
        r1 = r7.currentEncryptedChat;
        r1 = r1.admin_id;
        r2 = r7.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.getClientUserId();
        if (r1 != r2) goto L_0x05fc;
    L_0x05e3:
        r1 = r7.bigEmptyView;
        r2 = "EncryptedPlaceholderTitleOutgoing";
        r3 = NUM; // 0x7f0c023d float:1.8610354E38 double:1.0530976816E-314;
        r4 = new java.lang.Object[r11];
        r6 = r7.currentUser;
        r6 = org.telegram.messenger.UserObject.getFirstName(r6);
        r4[r10] = r6;
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);
        r1.setSecretText(r2);
        goto L_0x0614;
    L_0x05fc:
        r1 = r7.bigEmptyView;
        r2 = "EncryptedPlaceholderTitleIncoming";
        r3 = NUM; // 0x7f0c023c float:1.8610352E38 double:1.053097681E-314;
        r4 = new java.lang.Object[r11];
        r6 = r7.currentUser;
        r6 = org.telegram.messenger.UserObject.getFirstName(r6);
        r4[r10] = r6;
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r3, r4);
        r1.setSecretText(r2);
    L_0x0614:
        r1 = r7.emptyViewContainer;
        r2 = r7.bigEmptyView;
        r3 = new android.widget.FrameLayout$LayoutParams;
        r3.<init>(r5, r5, r13);
        r1.addView(r2, r3);
    L_0x0620:
        r1 = r7.chatActivityEnterView;
        if (r1 == 0) goto L_0x0639;
    L_0x0624:
        r1 = r7.chatActivityEnterView;
        r1.onDestroy();
        r1 = r7.chatActivityEnterView;
        r1 = r1.isEditingMessage();
        if (r1 != 0) goto L_0x0639;
    L_0x0631:
        r1 = r7.chatActivityEnterView;
        r1 = r1.getFieldText();
        r6 = r1;
        goto L_0x063a;
    L_0x0639:
        r6 = r12;
    L_0x063a:
        r1 = r7.mentionsAdapter;
        if (r1 == 0) goto L_0x0643;
    L_0x063e:
        r1 = r7.mentionsAdapter;
        r1.onDestroy();
    L_0x0643:
        r1 = new org.telegram.ui.ChatActivity$16;
        r1.<init>(r8);
        r7.chatListView = r1;
        r1 = r7.chatListView;
        r2 = java.lang.Integer.valueOf(r11);
        r1.setTag(r2);
        r1 = r7.chatListView;
        r1.setVerticalScrollBarEnabled(r11);
        r1 = r7.chatListView;
        r2 = new org.telegram.ui.ChatActivity$ChatActivityAdapter;
        r2.<init>(r8);
        r7.chatAdapter = r2;
        r1.setAdapter(r2);
        r1 = r7.chatListView;
        r1.setClipToPadding(r10);
        r1 = r7.chatListView;
        r2 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.setPadding(r10, r2, r10, r3);
        r1 = r7.chatListView;
        r1.setItemAnimator(r12);
        r1 = r7.chatListView;
        r1.setLayoutAnimation(r12);
        r4 = new org.telegram.ui.ChatActivity$17;
        r16 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r17 = 1;
        r18 = 1;
        r1 = r4;
        r2 = r7;
        r3 = r8;
        r15 = r4;
        r12 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = r16;
        r5 = r17;
        r23 = r6;
        r10 = 4;
        r6 = r18;
        r1.<init>(r3, r4, r5, r6);
        r7.chatLayoutManager = r15;
        r1 = r7.chatLayoutManager;
        r2 = new org.telegram.ui.ChatActivity$18;
        r2.<init>();
        r1.setSpanSizeLookup(r2);
        r1 = r7.chatListView;
        r2 = r7.chatLayoutManager;
        r1.setLayoutManager(r2);
        r1 = r7.chatListView;
        r2 = new org.telegram.ui.ChatActivity$19;
        r2.<init>();
        r1.addItemDecoration(r2);
        r1 = r7.contentView;
        r2 = r7.chatListView;
        r3 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r3);
        r1.addView(r2, r3);
        r1 = r7.chatListView;
        r2 = r7.onItemLongClickListener;
        r1.setOnItemLongClickListener(r2);
        r1 = r7.chatListView;
        r2 = r7.onItemClickListener;
        r1.setOnItemClickListener(r2);
        r1 = r7.chatListView;
        r2 = new org.telegram.ui.ChatActivity$20;
        r2.<init>();
        r1.setOnScrollListener(r2);
        r1 = new android.widget.FrameLayout;
        r1.<init>(r8);
        r7.progressView = r1;
        r1 = r7.progressView;
        r1.setVisibility(r10);
        r1 = r7.contentView;
        r2 = r7.progressView;
        r15 = 51;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r14, r15);
        r1.addView(r2, r3);
        r1 = new android.view.View;
        r1.<init>(r8);
        r7.progressView2 = r1;
        r1 = r7.progressView2;
        r2 = NUM; // 0x7f0701e5 float:1.7945562E38 double:1.0529357426E-314;
        r1.setBackgroundResource(r2);
        r1 = r7.progressView2;
        r1 = r1.getBackground();
        r2 = org.telegram.ui.ActionBar.Theme.colorFilter;
        r1.setColorFilter(r2);
        r1 = r7.progressView;
        r2 = r7.progressView2;
        r3 = 36;
        r4 = 36;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r13);
        r1.addView(r2, r3);
        r1 = new org.telegram.ui.Components.RadialProgressView;
        r1.<init>(r8);
        r7.progressBar = r1;
        r1 = r7.progressBar;
        r2 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.setSize(r2);
        r1 = r7.progressBar;
        r2 = "chat_serviceText";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setProgressColor(r2);
        r1 = r7.progressView;
        r2 = r7.progressBar;
        r3 = 32;
        r4 = 32;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r13);
        r1.addView(r2, r3);
        r1 = new org.telegram.ui.Cells.ChatActionCell;
        r1.<init>(r8);
        r7.floatingDateView = r1;
        r1 = r7.floatingDateView;
        r2 = 0;
        r1.setAlpha(r2);
        r1 = r7.contentView;
        r2 = r7.floatingDateView;
        r16 = -2;
        r17 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r18 = 49;
        r19 = 0;
        r20 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r21 = 0;
        r22 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = r7.floatingDateView;
        r2 = new org.telegram.ui.ChatActivity$21;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = r7.currentChat;
        r1 = org.telegram.messenger.ChatObject.isChannel(r1);
        r6 = 48;
        if (r1 == 0) goto L_0x08d0;
    L_0x0788:
        r1 = new android.widget.FrameLayout;
        r1.<init>(r8);
        r7.pinnedMessageView = r1;
        r1 = r7.pinnedMessageView;
        r2 = java.lang.Integer.valueOf(r11);
        r1.setTag(r2);
        r1 = r7.pinnedMessageView;
        r2 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = -r2;
        r2 = (float) r2;
        r1.setTranslationY(r2);
        r1 = r7.pinnedMessageView;
        r1.setVisibility(r9);
        r1 = r7.pinnedMessageView;
        r2 = NUM; // 0x7f070030 float:1.7944675E38 double:1.0529355267E-314;
        r1.setBackgroundResource(r2);
        r1 = r7.pinnedMessageView;
        r1 = r1.getBackground();
        r2 = new android.graphics.PorterDuffColorFilter;
        r3 = "chat_topPanelBackground";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r4 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r3, r4);
        r1.setColorFilter(r2);
        r1 = r7.contentView;
        r2 = r7.pinnedMessageView;
        r3 = 50;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r3, r15);
        r1.addView(r2, r3);
        r1 = r7.pinnedMessageView;
        r2 = new org.telegram.ui.ChatActivity$22;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = new android.view.View;
        r1.<init>(r8);
        r7.pinnedLineView = r1;
        r1 = r7.pinnedLineView;
        r2 = "chat_topPanelLine";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setBackgroundColor(r2);
        r1 = r7.pinnedMessageView;
        r2 = r7.pinnedLineView;
        r16 = 2;
        r17 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r18 = 51;
        r19 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r20 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r21 = 0;
        r22 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = new org.telegram.ui.Components.BackupImageView;
        r1.<init>(r8);
        r7.pinnedMessageImageView = r1;
        r1 = r7.pinnedMessageView;
        r2 = r7.pinnedMessageImageView;
        r16 = 32;
        r19 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = new org.telegram.ui.ActionBar.SimpleTextView;
        r1.<init>(r8);
        r7.pinnedMessageNameTextView = r1;
        r1 = r7.pinnedMessageNameTextView;
        r2 = 14;
        r1.setTextSize(r2);
        r1 = r7.pinnedMessageNameTextView;
        r2 = "chat_topPanelTitle";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.pinnedMessageNameTextView;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r1.setTypeface(r2);
        r1 = r7.pinnedMessageView;
        r2 = r7.pinnedMessageNameTextView;
        r16 = -1;
        r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r19 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r20 = NUM; // 0x40e9999a float:7.3 double:5.380627025E-315;
        r21 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r17 = r3;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = new org.telegram.ui.ActionBar.SimpleTextView;
        r1.<init>(r8);
        r7.pinnedMessageTextView = r1;
        r1 = r7.pinnedMessageTextView;
        r2 = 14;
        r1.setTextSize(r2);
        r1 = r7.pinnedMessageTextView;
        r2 = "chat_topPanelMessage";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.pinnedMessageView;
        r2 = r7.pinnedMessageTextView;
        r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r20 = NUM; // 0x41ca6666 float:25.3 double:5.453415206E-315;
        r17 = r3;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = new android.widget.ImageView;
        r1.<init>(r8);
        r7.closePinned = r1;
        r1 = r7.closePinned;
        r2 = NUM; // 0x7f070140 float:1.7945227E38 double:1.052935661E-314;
        r1.setImageResource(r2);
        r1 = r7.closePinned;
        r2 = new android.graphics.PorterDuffColorFilter;
        r3 = "chat_topPanelClose";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r4 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r3, r4);
        r1.setColorFilter(r2);
        r1 = r7.closePinned;
        r2 = android.widget.ImageView.ScaleType.CENTER;
        r1.setScaleType(r2);
        r1 = r7.pinnedMessageView;
        r2 = r7.closePinned;
        r3 = 53;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r6, r3);
        r1.addView(r2, r3);
        r1 = r7.closePinned;
        r2 = new org.telegram.ui.ChatActivity$23;
        r2.<init>();
        r1.setOnClickListener(r2);
    L_0x08d0:
        r1 = new android.widget.LinearLayout;
        r1.<init>(r8);
        r7.reportSpamView = r1;
        r1 = r7.reportSpamView;
        r2 = java.lang.Integer.valueOf(r11);
        r1.setTag(r2);
        r1 = r7.reportSpamView;
        r2 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = -r2;
        r2 = (float) r2;
        r1.setTranslationY(r2);
        r1 = r7.reportSpamView;
        r1.setVisibility(r9);
        r1 = r7.reportSpamView;
        r2 = NUM; // 0x7f070030 float:1.7944675E38 double:1.0529355267E-314;
        r1.setBackgroundResource(r2);
        r1 = r7.reportSpamView;
        r1 = r1.getBackground();
        r2 = new android.graphics.PorterDuffColorFilter;
        r3 = "chat_topPanelBackground";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r4 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r3, r4);
        r1.setColorFilter(r2);
        r1 = r7.contentView;
        r2 = r7.reportSpamView;
        r3 = 50;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r3, r15);
        r1.addView(r2, r3);
        r1 = new android.widget.TextView;
        r1.<init>(r8);
        r7.addToContactsButton = r1;
        r1 = r7.addToContactsButton;
        r2 = "chat_addContact";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.addToContactsButton;
        r1.setVisibility(r9);
        r1 = r7.addToContactsButton;
        r1.setTextSize(r11, r12);
        r1 = r7.addToContactsButton;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r1.setTypeface(r2);
        r1 = r7.addToContactsButton;
        r1.setSingleLine(r11);
        r1 = r7.addToContactsButton;
        r1.setMaxLines(r11);
        r1 = r7.addToContactsButton;
        r2 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = 0;
        r1.setPadding(r2, r4, r3, r4);
        r1 = r7.addToContactsButton;
        r1.setGravity(r13);
        r1 = r7.addToContactsButton;
        r2 = "AddContactChat";
        r3 = NUM; // 0x7f0c003f float:1.860932E38 double:1.0530974296E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r1.setText(r2);
        r1 = r7.reportSpamView;
        r2 = r7.addToContactsButton;
        r24 = -1;
        r25 = -1;
        r26 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r27 = 51;
        r28 = 0;
        r29 = 0;
        r30 = 0;
        r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r24, r25, r26, r27, r28, r29, r30, r31);
        r1.addView(r2, r3);
        r1 = r7.addToContactsButton;
        r2 = new org.telegram.ui.ChatActivity$24;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = new android.widget.FrameLayout;
        r1.<init>(r8);
        r7.reportSpamContainer = r1;
        r1 = r7.reportSpamView;
        r2 = r7.reportSpamContainer;
        r26 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r31 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r24, r25, r26, r27, r28, r29, r30, r31);
        r1.addView(r2, r3);
        r1 = new android.widget.TextView;
        r1.<init>(r8);
        r7.reportSpamButton = r1;
        r1 = r7.reportSpamButton;
        r2 = "chat_reportSpam";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.reportSpamButton;
        r1.setTextSize(r11, r12);
        r1 = r7.reportSpamButton;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r1.setTypeface(r2);
        r1 = r7.reportSpamButton;
        r1.setSingleLine(r11);
        r1 = r7.reportSpamButton;
        r1.setMaxLines(r11);
        r1 = r7.currentChat;
        if (r1 == 0) goto L_0x09f5;
    L_0x09e6:
        r1 = r7.reportSpamButton;
        r2 = "ReportSpamAndLeave";
        r3 = NUM; // 0x7f0c0569 float:1.8612001E38 double:1.0530980827E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r1.setText(r2);
        goto L_0x0a03;
    L_0x09f5:
        r1 = r7.reportSpamButton;
        r2 = "ReportSpam";
        r3 = NUM; // 0x7f0c0565 float:1.8611993E38 double:1.053098081E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r1.setText(r2);
    L_0x0a03:
        r1 = r7.reportSpamButton;
        r1.setGravity(r13);
        r1 = r7.reportSpamButton;
        r2 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = 0;
        r1.setPadding(r2, r4, r3, r4);
        r1 = r7.reportSpamContainer;
        r2 = r7.reportSpamButton;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r14, r15);
        r1.addView(r2, r3);
        r1 = r7.reportSpamButton;
        r2 = new org.telegram.ui.ChatActivity$25;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = new android.widget.ImageView;
        r1.<init>(r8);
        r7.closeReportSpam = r1;
        r1 = r7.closeReportSpam;
        r2 = NUM; // 0x7f070140 float:1.7945227E38 double:1.052935661E-314;
        r1.setImageResource(r2);
        r1 = r7.closeReportSpam;
        r2 = new android.graphics.PorterDuffColorFilter;
        r3 = "chat_topPanelClose";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r4 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r3, r4);
        r1.setColorFilter(r2);
        r1 = r7.closeReportSpam;
        r2 = android.widget.ImageView.ScaleType.CENTER;
        r1.setScaleType(r2);
        r1 = r7.reportSpamContainer;
        r2 = r7.closeReportSpam;
        r3 = 53;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r6, r3);
        r1.addView(r2, r3);
        r1 = r7.closeReportSpam;
        r2 = new org.telegram.ui.ChatActivity$26;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = new android.widget.FrameLayout;
        r1.<init>(r8);
        r7.alertView = r1;
        r1 = r7.alertView;
        r2 = java.lang.Integer.valueOf(r11);
        r1.setTag(r2);
        r1 = r7.alertView;
        r2 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = -r2;
        r2 = (float) r2;
        r1.setTranslationY(r2);
        r1 = r7.alertView;
        r1.setVisibility(r9);
        r1 = r7.alertView;
        r2 = NUM; // 0x7f070030 float:1.7944675E38 double:1.0529355267E-314;
        r1.setBackgroundResource(r2);
        r1 = r7.alertView;
        r1 = r1.getBackground();
        r2 = new android.graphics.PorterDuffColorFilter;
        r3 = "chat_topPanelBackground";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r4 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r3, r4);
        r1.setColorFilter(r2);
        r1 = r7.contentView;
        r2 = r7.alertView;
        r3 = 50;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r3, r15);
        r1.addView(r2, r3);
        r1 = new android.widget.TextView;
        r1.<init>(r8);
        r7.alertNameTextView = r1;
        r1 = r7.alertNameTextView;
        r1.setTextSize(r11, r12);
        r1 = r7.alertNameTextView;
        r2 = "chat_topPanelTitle";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.alertNameTextView;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r1.setTypeface(r2);
        r1 = r7.alertNameTextView;
        r1.setSingleLine(r11);
        r1 = r7.alertNameTextView;
        r2 = android.text.TextUtils.TruncateAt.END;
        r1.setEllipsize(r2);
        r1 = r7.alertNameTextView;
        r1.setMaxLines(r11);
        r1 = r7.alertView;
        r2 = r7.alertNameTextView;
        r16 = -2;
        r17 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r18 = 51;
        r19 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r20 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r21 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r22 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = new android.widget.TextView;
        r1.<init>(r8);
        r7.alertTextView = r1;
        r1 = r7.alertTextView;
        r1.setTextSize(r11, r12);
        r1 = r7.alertTextView;
        r2 = "chat_topPanelMessage";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.alertTextView;
        r1.setSingleLine(r11);
        r1 = r7.alertTextView;
        r2 = android.text.TextUtils.TruncateAt.END;
        r1.setEllipsize(r2);
        r1 = r7.alertTextView;
        r1.setMaxLines(r11);
        r1 = r7.alertView;
        r2 = r7.alertTextView;
        r20 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = new android.widget.FrameLayout;
        r1.<init>(r8);
        r7.pagedownButton = r1;
        r1 = r7.pagedownButton;
        r1.setVisibility(r10);
        r1 = r7.contentView;
        r2 = r7.pagedownButton;
        r16 = 66;
        r17 = NUM; // 0x426c0000 float:59.0 double:5.50573981E-315;
        r18 = 85;
        r19 = 0;
        r20 = 0;
        r21 = -NUM; // 0xffffffffc0400000 float:-3.0 double:NaN;
        r22 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = r7.pagedownButton;
        r2 = new org.telegram.ui.ChatActivity$27;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = new android.widget.FrameLayout;
        r1.<init>(r8);
        r7.mentiondownButton = r1;
        r1 = r7.mentiondownButton;
        r1.setVisibility(r10);
        r1 = r7.contentView;
        r2 = r7.mentiondownButton;
        r16 = 46;
        r21 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = r7.mentiondownButton;
        r2 = new org.telegram.ui.ChatActivity$28;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = r7.mentiondownButton;
        r2 = new org.telegram.ui.ChatActivity$29;
        r2.<init>();
        r1.setOnLongClickListener(r2);
        r1 = r7.isBroadcast;
        r4 = 46;
        if (r1 != 0) goto L_0x0cc6;
    L_0x0ba0:
        r1 = new org.telegram.ui.ChatActivity$30;
        r1.<init>(r8);
        r7.mentionContainer = r1;
        r1 = r7.mentionContainer;
        r1.setVisibility(r9);
        r1 = r7.mentionContainer;
        r2 = 0;
        r1.setWillNotDraw(r2);
        r1 = r7.contentView;
        r2 = r7.mentionContainer;
        r3 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        r5 = 83;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r3, r5);
        r1.addView(r2, r3);
        r1 = new org.telegram.ui.ChatActivity$31;
        r1.<init>(r8);
        r7.mentionListView = r1;
        r1 = r7.mentionListView;
        r2 = new org.telegram.ui.ChatActivity$32;
        r2.<init>();
        r1.setOnTouchListener(r2);
        r1 = r7.mentionListView;
        r2 = 2;
        r2 = java.lang.Integer.valueOf(r2);
        r1.setTag(r2);
        r1 = new org.telegram.ui.ChatActivity$33;
        r1.<init>(r8);
        r7.mentionLayoutManager = r1;
        r1 = r7.mentionLayoutManager;
        r1.setOrientation(r11);
        r1 = new org.telegram.ui.ChatActivity$34;
        r2 = 100;
        r1.<init>(r8, r2);
        r7.mentionGridLayoutManager = r1;
        r1 = r7.mentionGridLayoutManager;
        r2 = new org.telegram.ui.ChatActivity$35;
        r2.<init>();
        r1.setSpanSizeLookup(r2);
        r1 = r7.mentionListView;
        r2 = new org.telegram.ui.ChatActivity$36;
        r2.<init>();
        r1.addItemDecoration(r2);
        r1 = r7.mentionListView;
        r2 = 0;
        r1.setItemAnimator(r2);
        r1 = r7.mentionListView;
        r1.setLayoutAnimation(r2);
        r1 = r7.mentionListView;
        r2 = 0;
        r1.setClipToPadding(r2);
        r1 = r7.mentionListView;
        r2 = r7.mentionLayoutManager;
        r1.setLayoutManager(r2);
        r1 = r7.mentionListView;
        r2 = 2;
        r1.setOverScrollMode(r2);
        r1 = r7.mentionContainer;
        r2 = r7.mentionListView;
        r3 = -NUM; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r3);
        r1.addView(r2, r3);
        r5 = r7.mentionListView;
        r3 = new org.telegram.ui.Adapters.MentionsAdapter;
        r16 = 0;
        r1 = r7.dialog_id;
        r6 = new org.telegram.ui.ChatActivity$37;
        r6.<init>();
        r17 = r1;
        r1 = r3;
        r2 = r8;
        r12 = r3;
        r3 = r16;
        r14 = r4;
        r9 = r5;
        r4 = r17;
        r1.<init>(r2, r3, r4, r6);
        r7.mentionsAdapter = r12;
        r9.setAdapter(r12);
        r1 = r7.currentChat;
        r1 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r1 == 0) goto L_0x0c62;
    L_0x0c58:
        r1 = r7.currentChat;
        if (r1 == 0) goto L_0x0c69;
    L_0x0c5c:
        r1 = r7.currentChat;
        r1 = r1.megagroup;
        if (r1 == 0) goto L_0x0c69;
    L_0x0c62:
        r1 = r7.mentionsAdapter;
        r2 = r7.botInfo;
        r1.setBotInfo(r2);
    L_0x0c69:
        r1 = r7.mentionsAdapter;
        r1.setParentFragment(r7);
        r1 = r7.mentionsAdapter;
        r2 = r7.info;
        r1.setChatInfo(r2);
        r1 = r7.mentionsAdapter;
        r2 = r7.currentChat;
        if (r2 == 0) goto L_0x0c7d;
    L_0x0c7b:
        r2 = r11;
        goto L_0x0c7e;
    L_0x0c7d:
        r2 = 0;
    L_0x0c7e:
        r1.setNeedUsernames(r2);
        r1 = r7.mentionsAdapter;
        r2 = r7.currentEncryptedChat;
        if (r2 == 0) goto L_0x0c94;
    L_0x0c87:
        r2 = r7.currentEncryptedChat;
        r2 = r2.layer;
        r2 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r2);
        if (r2 < r14) goto L_0x0c92;
    L_0x0c91:
        goto L_0x0c94;
    L_0x0c92:
        r2 = 0;
        goto L_0x0c95;
    L_0x0c94:
        r2 = r11;
    L_0x0c95:
        r1.setNeedBotContext(r2);
        r1 = r7.mentionsAdapter;
        r2 = r7.currentChat;
        if (r2 == 0) goto L_0x0ca1;
    L_0x0c9e:
        r2 = r7.botsCount;
        goto L_0x0ca2;
    L_0x0ca1:
        r2 = r11;
    L_0x0ca2:
        r1.setBotsCount(r2);
        r1 = r7.mentionListView;
        r2 = new org.telegram.ui.ChatActivity$38;
        r2.<init>();
        r7.mentionsOnItemClickListener = r2;
        r1.setOnItemClickListener(r2);
        r1 = r7.mentionListView;
        r2 = new org.telegram.ui.ChatActivity$39;
        r2.<init>();
        r1.setOnItemLongClickListener(r2);
        r1 = r7.mentionListView;
        r2 = new org.telegram.ui.ChatActivity$40;
        r2.<init>();
        r1.setOnScrollListener(r2);
        goto L_0x0cc7;
    L_0x0cc6:
        r14 = r4;
    L_0x0cc7:
        r1 = new android.widget.ImageView;
        r1.<init>(r8);
        r7.pagedownButtonImage = r1;
        r1 = r7.pagedownButtonImage;
        r2 = NUM; // 0x7f07017b float:1.7945347E38 double:1.0529356903E-314;
        r1.setImageResource(r2);
        r1 = r7.pagedownButtonImage;
        r2 = android.widget.ImageView.ScaleType.CENTER;
        r1.setScaleType(r2);
        r1 = r7.pagedownButtonImage;
        r2 = new android.graphics.PorterDuffColorFilter;
        r3 = "chat_goDownButtonIcon";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r4 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r3, r4);
        r1.setColorFilter(r2);
        r1 = r7.pagedownButtonImage;
        r2 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = 0;
        r1.setPadding(r3, r2, r3, r3);
        r1 = NUM; // 0x42280000 float:42.0 double:5.483722033E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r3 = "chat_goDownButton";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.createCircleDrawable(r2, r3);
        r3 = r34.getResources();
        r4 = NUM; // 0x7f07017c float:1.7945349E38 double:1.052935691E-314;
        r3 = r3.getDrawable(r4);
        r3 = r3.mutate();
        r4 = new android.graphics.PorterDuffColorFilter;
        r5 = "chat_goDownButtonShadow";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r6 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r4.<init>(r5, r6);
        r3.setColorFilter(r4);
        r4 = new org.telegram.ui.Components.CombinedDrawable;
        r5 = 0;
        r4.<init>(r3, r2, r5, r5);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r4.setIconSize(r2, r3);
        r2 = r7.pagedownButtonImage;
        r2.setBackgroundDrawable(r4);
        r2 = r7.pagedownButton;
        r3 = r7.pagedownButtonImage;
        r4 = 81;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r14, r4);
        r2.addView(r3, r4);
        r2 = new android.widget.TextView;
        r2.<init>(r8);
        r7.pagedownButtonCounter = r2;
        r2 = r7.pagedownButtonCounter;
        r2.setVisibility(r10);
        r2 = r7.pagedownButtonCounter;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r2.setTypeface(r3);
        r2 = r7.pagedownButtonCounter;
        r3 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r2.setTextSize(r11, r3);
        r2 = r7.pagedownButtonCounter;
        r3 = "chat_goDownButtonCounter";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setTextColor(r3);
        r2 = r7.pagedownButtonCounter;
        r2.setGravity(r13);
        r2 = r7.pagedownButtonCounter;
        r3 = NUM; // 0x41380000 float:11.5 double:5.406012226E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = "chat_goDownButtonCounterBackground";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r3, r4);
        r2.setBackgroundDrawable(r3);
        r2 = r7.pagedownButtonCounter;
        r3 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2.setMinWidth(r3);
        r2 = r7.pagedownButtonCounter;
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = 0;
        r2.setPadding(r3, r6, r4, r5);
        r2 = r7.pagedownButton;
        r3 = r7.pagedownButtonCounter;
        r4 = 49;
        r5 = -2;
        r6 = 23;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r4);
        r2.addView(r3, r4);
        r2 = new android.widget.ImageView;
        r2.<init>(r8);
        r7.mentiondownButtonImage = r2;
        r2 = r7.mentiondownButtonImage;
        r3 = NUM; // 0x7f070131 float:1.7945197E38 double:1.0529356537E-314;
        r2.setImageResource(r3);
        r2 = r7.mentiondownButtonImage;
        r3 = android.widget.ImageView.ScaleType.CENTER;
        r2.setScaleType(r3);
        r2 = r7.mentiondownButtonImage;
        r3 = new android.graphics.PorterDuffColorFilter;
        r4 = "chat_goDownButtonIcon";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r6 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r3.<init>(r4, r6);
        r2.setColorFilter(r3);
        r2 = r7.mentiondownButtonImage;
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = 0;
        r2.setPadding(r4, r3, r4, r4);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r3 = "chat_goDownButton";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.createCircleDrawable(r2, r3);
        r3 = r34.getResources();
        r4 = NUM; // 0x7f07017c float:1.7945349E38 double:1.052935691E-314;
        r3 = r3.getDrawable(r4);
        r3 = r3.mutate();
        r4 = new android.graphics.PorterDuffColorFilter;
        r6 = "chat_goDownButtonShadow";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r9 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r4.<init>(r6, r9);
        r3.setColorFilter(r4);
        r4 = new org.telegram.ui.Components.CombinedDrawable;
        r6 = 0;
        r4.<init>(r3, r2, r6, r6);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r4.setIconSize(r2, r1);
        r1 = r7.mentiondownButtonImage;
        r1.setBackgroundDrawable(r4);
        r1 = r7.mentiondownButton;
        r2 = r7.mentiondownButtonImage;
        r3 = 83;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r14, r3);
        r1.addView(r2, r3);
        r1 = new android.widget.TextView;
        r1.<init>(r8);
        r7.mentiondownButtonCounter = r1;
        r1 = r7.mentiondownButtonCounter;
        r1.setVisibility(r10);
        r1 = r7.mentiondownButtonCounter;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r1.setTypeface(r2);
        r1 = r7.mentiondownButtonCounter;
        r2 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r1.setTextSize(r11, r2);
        r1 = r7.mentiondownButtonCounter;
        r2 = "chat_goDownButtonCounter";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.mentiondownButtonCounter;
        r1.setGravity(r13);
        r1 = r7.mentiondownButtonCounter;
        r2 = NUM; // 0x41380000 float:11.5 double:5.406012226E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = "chat_goDownButtonCounterBackground";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r2, r3);
        r1.setBackgroundDrawable(r2);
        r1 = r7.mentiondownButtonCounter;
        r2 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.setMinWidth(r2);
        r1 = r7.mentiondownButtonCounter;
        r2 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = 0;
        r1.setPadding(r2, r6, r3, r4);
        r1 = r7.mentiondownButton;
        r2 = r7.mentiondownButtonCounter;
        r3 = 49;
        r4 = 23;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4, r3);
        r1.addView(r2, r3);
        r1 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r1 == 0) goto L_0x0ec8;
    L_0x0ec2:
        r1 = org.telegram.messenger.AndroidUtilities.isSmallTablet();
        if (r1 == 0) goto L_0x0f05;
    L_0x0ec8:
        r1 = r7.contentView;
        r2 = new org.telegram.ui.Components.FragmentContextView;
        r2.<init>(r8, r7, r11);
        r7.fragmentLocationContextView = r2;
        r16 = -1;
        r17 = NUM; // 0x421c0000 float:39.0 double:5.479836543E-315;
        r18 = 51;
        r19 = 0;
        r20 = -NUM; // 0xffffffffc2100000 float:-36.0 double:NaN;
        r21 = 0;
        r22 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = r7.contentView;
        r2 = new org.telegram.ui.Components.FragmentContextView;
        r3 = 0;
        r2.<init>(r8, r7, r3);
        r7.fragmentContextView = r2;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = r7.fragmentContextView;
        r2 = r7.fragmentLocationContextView;
        r1.setAdditionalContextView(r2);
        r1 = r7.fragmentLocationContextView;
        r2 = r7.fragmentContextView;
        r1.setAdditionalContextView(r2);
    L_0x0f05:
        r1 = r7.contentView;
        r2 = r7.actionBar;
        r1.addView(r2);
        r1 = new android.view.View;
        r1.<init>(r8);
        r7.overlayView = r1;
        r1 = r7.overlayView;
        r2 = new org.telegram.ui.ChatActivity$41;
        r2.<init>();
        r1.setOnTouchListener(r2);
        r1 = r7.contentView;
        r2 = r7.overlayView;
        r3 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r3, r15);
        r1.addView(r2, r4);
        r1 = r7.overlayView;
        r2 = 8;
        r1.setVisibility(r2);
        r1 = new org.telegram.ui.Components.InstantCameraView;
        r1.<init>(r8, r7);
        r7.instantCameraView = r1;
        r1 = r7.contentView;
        r2 = r7.instantCameraView;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r3, r15);
        r1.addView(r2, r4);
        r1 = new org.telegram.ui.Components.ChatActivityEnterView;
        r2 = r33.getParentActivity();
        r3 = r7.contentView;
        r1.<init>(r2, r3, r7, r11);
        r7.chatActivityEnterView = r1;
        r1 = r7.chatActivityEnterView;
        r2 = r7.dialog_id;
        r4 = r7.currentAccount;
        r1.setDialogId(r2, r4);
        r1 = r7.chatActivityEnterView;
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r1.setId(r2);
        r1 = r7.chatActivityEnterView;
        r2 = r7.botsCount;
        r3 = r7.hasBotsCommands;
        r1.setBotsCount(r2, r3);
        r1 = r7.chatActivityEnterView;
        r2 = r7.currentEncryptedChat;
        if (r2 == 0) goto L_0x0f7d;
    L_0x0f6e:
        r2 = r7.currentEncryptedChat;
        r2 = r2.layer;
        r2 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r2);
        r3 = 23;
        if (r2 < r3) goto L_0x0f7b;
    L_0x0f7a:
        goto L_0x0f7d;
    L_0x0f7b:
        r2 = 0;
        goto L_0x0f7e;
    L_0x0f7d:
        r2 = r11;
    L_0x0f7e:
        r3 = r7.currentEncryptedChat;
        if (r3 == 0) goto L_0x0f8f;
    L_0x0f82:
        r3 = r7.currentEncryptedChat;
        r3 = r3.layer;
        r3 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r3);
        if (r3 < r14) goto L_0x0f8d;
    L_0x0f8c:
        goto L_0x0f8f;
    L_0x0f8d:
        r3 = 0;
        goto L_0x0f90;
    L_0x0f8f:
        r3 = r11;
    L_0x0f90:
        r1.setAllowStickersAndGifs(r2, r3);
        r1 = r7.contentView;
        r2 = r7.chatActivityEnterView;
        r3 = r7.contentView;
        r3 = r3.getChildCount();
        r3 = r3 - r11;
        r4 = 83;
        r6 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5, r4);
        r1.addView(r2, r3, r4);
        r1 = r7.chatActivityEnterView;
        r2 = new org.telegram.ui.ChatActivity$42;
        r2.<init>();
        r1.setDelegate(r2);
        r1 = new org.telegram.ui.ChatActivity$43;
        r1.<init>(r8);
        r2 = r7.chatActivityEnterView;
        r3 = 48;
        r2.addTopView(r1, r3);
        r2 = new org.telegram.ui.ChatActivity$44;
        r2.<init>();
        r1.setOnClickListener(r2);
        r2 = new android.view.View;
        r2.<init>(r8);
        r7.replyLineView = r2;
        r2 = r7.replyLineView;
        r4 = "chat_replyPanelLine";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r2.setBackgroundColor(r4);
        r2 = r7.replyLineView;
        r4 = 83;
        r6 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r11, r4);
        r1.addView(r2, r4);
        r2 = new android.widget.ImageView;
        r2.<init>(r8);
        r7.replyIconImageView = r2;
        r2 = r7.replyIconImageView;
        r4 = new android.graphics.PorterDuffColorFilter;
        r6 = "chat_replyPanelIcons";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r9 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r4.<init>(r6, r9);
        r2.setColorFilter(r4);
        r2 = r7.replyIconImageView;
        r4 = android.widget.ImageView.ScaleType.CENTER;
        r2.setScaleType(r4);
        r2 = r7.replyIconImageView;
        r4 = 52;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r14, r15);
        r1.addView(r2, r4);
        r2 = new android.widget.ImageView;
        r2.<init>(r8);
        r7.replyCloseImageView = r2;
        r2 = r7.replyCloseImageView;
        r4 = new android.graphics.PorterDuffColorFilter;
        r6 = "chat_replyPanelClose";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r9 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r4.<init>(r6, r9);
        r2.setColorFilter(r4);
        r2 = r7.replyCloseImageView;
        r4 = NUM; // 0x7f07014f float:1.7945257E38 double:1.0529356685E-314;
        r2.setImageResource(r4);
        r2 = r7.replyCloseImageView;
        r4 = android.widget.ImageView.ScaleType.CENTER;
        r2.setScaleType(r4);
        r2 = r7.replyCloseImageView;
        r16 = 52;
        r17 = NUM; // 0x42380000 float:46.0 double:5.488902687E-315;
        r18 = 53;
        r19 = 0;
        r20 = NUM; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r21 = 0;
        r22 = 0;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r4);
        r2 = r7.replyCloseImageView;
        r4 = new org.telegram.ui.ChatActivity$45;
        r4.<init>();
        r2.setOnClickListener(r4);
        r2 = new org.telegram.ui.ActionBar.SimpleTextView;
        r2.<init>(r8);
        r7.replyNameTextView = r2;
        r2 = r7.replyNameTextView;
        r4 = 14;
        r2.setTextSize(r4);
        r2 = r7.replyNameTextView;
        r4 = "chat_replyPanelName";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r2.setTextColor(r4);
        r2 = r7.replyNameTextView;
        r4 = "fonts/rmedium.ttf";
        r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4);
        r2.setTypeface(r4);
        r2 = r7.replyNameTextView;
        r16 = -1;
        r17 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r18 = 51;
        r19 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r20 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r21 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r4);
        r2 = new org.telegram.ui.ActionBar.SimpleTextView;
        r2.<init>(r8);
        r7.replyObjectTextView = r2;
        r2 = r7.replyObjectTextView;
        r4 = 14;
        r2.setTextSize(r4);
        r2 = r7.replyObjectTextView;
        r4 = "chat_replyPanelMessage";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r2.setTextColor(r4);
        r2 = r7.replyObjectTextView;
        r20 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r4);
        r2 = new org.telegram.ui.Components.BackupImageView;
        r2.<init>(r8);
        r7.replyImageView = r2;
        r2 = r7.replyImageView;
        r16 = 34;
        r17 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r20 = NUM; // 0x40c00000 float:6.0 double:5.367157323E-315;
        r21 = 0;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r4);
        r1 = new android.widget.FrameLayout;
        r1.<init>(r8);
        r7.stickersPanel = r1;
        r1 = r7.stickersPanel;
        r2 = 8;
        r1.setVisibility(r2);
        r1 = r7.contentView;
        r2 = r7.stickersPanel;
        r16 = -2;
        r17 = NUM; // 0x42a30000 float:81.5 double:5.52354831E-315;
        r18 = 83;
        r19 = 0;
        r20 = 0;
        r22 = NUM; // 0x42180000 float:38.0 double:5.47854138E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r4);
        r1 = new org.telegram.ui.ChatActivity$46;
        r1.<init>();
        r2 = new org.telegram.ui.ChatActivity$47;
        r2.<init>(r8, r1);
        r7.stickersListView = r2;
        r2 = r7.stickersListView;
        r4 = 3;
        r4 = java.lang.Integer.valueOf(r4);
        r2.setTag(r4);
        r2 = r7.stickersListView;
        r4 = new org.telegram.ui.ChatActivity$48;
        r4.<init>(r1);
        r2.setOnTouchListener(r4);
        r1 = r7.stickersListView;
        r1.setDisallowInterceptTouchEvents(r11);
        r1 = new org.telegram.messenger.support.widget.LinearLayoutManager;
        r1.<init>(r8);
        r2 = 0;
        r1.setOrientation(r2);
        r4 = r7.stickersListView;
        r4.setLayoutManager(r1);
        r1 = r7.stickersListView;
        r1.setClipToPadding(r2);
        r1 = r7.stickersListView;
        r2 = 2;
        r1.setOverScrollMode(r2);
        r1 = r7.stickersPanel;
        r2 = r7.stickersListView;
        r4 = NUM; // 0x429c0000 float:78.0 double:5.521281773E-315;
        r6 = -1;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r4);
        r1.addView(r2, r4);
        r33.initStickers();
        r1 = new android.widget.ImageView;
        r1.<init>(r8);
        r7.stickersPanelArrow = r1;
        r1 = r7.stickersPanelArrow;
        r2 = NUM; // 0x7f0701d5 float:1.794553E38 double:1.0529357347E-314;
        r1.setImageResource(r2);
        r1 = r7.stickersPanelArrow;
        r2 = new android.graphics.PorterDuffColorFilter;
        r4 = "chat_stickersHintPanel";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r6 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r4, r6);
        r1.setColorFilter(r2);
        r1 = r7.stickersPanel;
        r2 = r7.stickersPanelArrow;
        r17 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r19 = NUM; // 0x42540000 float:53.0 double:5.49796883E-315;
        r22 = 0;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r4);
        r1 = new org.telegram.ui.ChatActivity$49;
        r1.<init>(r8);
        r7.searchContainer = r1;
        r1 = r7.searchContainer;
        r2 = new org.telegram.ui.ChatActivity$50;
        r2.<init>();
        r1.setOnTouchListener(r2);
        r1 = r7.searchContainer;
        r2 = 0;
        r1.setWillNotDraw(r2);
        r1 = r7.searchContainer;
        r1.setVisibility(r10);
        r1 = r7.searchContainer;
        r1.setFocusable(r11);
        r1 = r7.searchContainer;
        r1.setFocusableInTouchMode(r11);
        r1 = r7.searchContainer;
        r1.setClickable(r11);
        r1 = r7.searchContainer;
        r2 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r4 = 0;
        r1.setPadding(r4, r2, r4, r4);
        r1 = new android.widget.ImageView;
        r1.<init>(r8);
        r7.searchUpButton = r1;
        r1 = r7.searchUpButton;
        r2 = android.widget.ImageView.ScaleType.CENTER;
        r1.setScaleType(r2);
        r1 = r7.searchUpButton;
        r2 = NUM; // 0x7f0701be float:1.7945483E38 double:1.0529357234E-314;
        r1.setImageResource(r2);
        r1 = r7.searchUpButton;
        r2 = new android.graphics.PorterDuffColorFilter;
        r4 = "chat_searchPanelIcons";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r6 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r4, r6);
        r1.setColorFilter(r2);
        r1 = r7.searchContainer;
        r2 = r7.searchUpButton;
        r16 = 48;
        r17 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r18 = 53;
        r19 = 0;
        r21 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r4);
        r1 = r7.searchUpButton;
        r2 = new org.telegram.ui.ChatActivity$51;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = new android.widget.ImageView;
        r1.<init>(r8);
        r7.searchDownButton = r1;
        r1 = r7.searchDownButton;
        r2 = android.widget.ImageView.ScaleType.CENTER;
        r1.setScaleType(r2);
        r1 = r7.searchDownButton;
        r2 = NUM; // 0x7f0701bc float:1.7945478E38 double:1.0529357224E-314;
        r1.setImageResource(r2);
        r1 = r7.searchDownButton;
        r2 = new android.graphics.PorterDuffColorFilter;
        r4 = "chat_searchPanelIcons";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r6 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r4, r6);
        r1.setColorFilter(r2);
        r1 = r7.searchContainer;
        r2 = r7.searchDownButton;
        r21 = 0;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r4);
        r1 = r7.searchDownButton;
        r2 = new org.telegram.ui.ChatActivity$52;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = r7.currentChat;
        if (r1 == 0) goto L_0x128d;
    L_0x1234:
        r1 = r7.currentChat;
        r1 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r1 == 0) goto L_0x1242;
    L_0x123c:
        r1 = r7.currentChat;
        r1 = r1.megagroup;
        if (r1 == 0) goto L_0x128d;
    L_0x1242:
        r1 = new android.widget.ImageView;
        r1.<init>(r8);
        r7.searchUserButton = r1;
        r1 = r7.searchUserButton;
        r2 = android.widget.ImageView.ScaleType.CENTER;
        r1.setScaleType(r2);
        r1 = r7.searchUserButton;
        r2 = NUM; // 0x7f0701f1 float:1.7945586E38 double:1.0529357486E-314;
        r1.setImageResource(r2);
        r1 = r7.searchUserButton;
        r2 = new android.graphics.PorterDuffColorFilter;
        r4 = "chat_searchPanelIcons";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r6 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r4, r6);
        r1.setColorFilter(r2);
        r1 = r7.searchContainer;
        r2 = r7.searchUserButton;
        r16 = 48;
        r17 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r18 = 51;
        r19 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r4);
        r1 = r7.searchUserButton;
        r2 = new org.telegram.ui.ChatActivity$53;
        r2.<init>();
        r1.setOnClickListener(r2);
    L_0x128d:
        r1 = new android.widget.ImageView;
        r1.<init>(r8);
        r7.searchCalendarButton = r1;
        r1 = r7.searchCalendarButton;
        r2 = android.widget.ImageView.ScaleType.CENTER;
        r1.setScaleType(r2);
        r1 = r7.searchCalendarButton;
        r2 = NUM; // 0x7f0701b9 float:1.7945472E38 double:1.052935721E-314;
        r1.setImageResource(r2);
        r1 = r7.searchCalendarButton;
        r2 = new android.graphics.PorterDuffColorFilter;
        r4 = "chat_searchPanelIcons";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r6 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r2.<init>(r4, r6);
        r1.setColorFilter(r2);
        r1 = r7.searchContainer;
        r2 = r7.searchCalendarButton;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r3, r15);
        r1.addView(r2, r3);
        r1 = r7.searchCalendarButton;
        r2 = new org.telegram.ui.ChatActivity$54;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = new org.telegram.ui.ActionBar.SimpleTextView;
        r1.<init>(r8);
        r7.searchCountText = r1;
        r1 = r7.searchCountText;
        r2 = "chat_searchPanelText";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.searchCountText;
        r2 = 15;
        r1.setTextSize(r2);
        r1 = r7.searchCountText;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r1.setTypeface(r2);
        r1 = r7.searchCountText;
        r2 = 5;
        r1.setGravity(r2);
        r1 = r7.searchContainer;
        r2 = r7.searchCountText;
        r16 = -2;
        r17 = -NUM; // 0xffffffffc0000000 float:-2.0 double:NaN;
        r18 = 21;
        r19 = 0;
        r20 = 0;
        r21 = NUM; // 0x42d80000 float:108.0 double:5.540709225E-315;
        r22 = 0;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = new org.telegram.ui.ChatActivity$55;
        r1.<init>(r8);
        r7.bottomOverlay = r1;
        r1 = r7.bottomOverlay;
        r2 = 0;
        r1.setWillNotDraw(r2);
        r1 = r7.bottomOverlay;
        r1.setVisibility(r10);
        r1 = r7.bottomOverlay;
        r1.setFocusable(r11);
        r1 = r7.bottomOverlay;
        r1.setFocusableInTouchMode(r11);
        r1 = r7.bottomOverlay;
        r1.setClickable(r11);
        r1 = r7.bottomOverlay;
        r2 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = 0;
        r1.setPadding(r3, r2, r3, r3);
        r1 = r7.contentView;
        r2 = r7.bottomOverlay;
        r3 = 80;
        r4 = -1;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r15, r3);
        r1.addView(r2, r3);
        r1 = new android.widget.TextView;
        r1.<init>(r8);
        r7.bottomOverlayText = r1;
        r1 = r7.bottomOverlayText;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r1.setTextSize(r11, r2);
        r1 = r7.bottomOverlayText;
        r1.setGravity(r13);
        r1 = r7.bottomOverlayText;
        r2 = 2;
        r1.setMaxLines(r2);
        r1 = r7.bottomOverlayText;
        r2 = android.text.TextUtils.TruncateAt.END;
        r1.setEllipsize(r2);
        r1 = r7.bottomOverlayText;
        r2 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r1.setLineSpacing(r2, r3);
        r1 = r7.bottomOverlayText;
        r2 = "chat_secretChatStatusText";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.bottomOverlay;
        r2 = r7.bottomOverlayText;
        r18 = 17;
        r19 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r21 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22);
        r1.addView(r2, r3);
        r1 = new org.telegram.ui.ChatActivity$56;
        r1.<init>(r8);
        r7.bottomOverlayChat = r1;
        r1 = r7.bottomOverlayChat;
        r4 = 0;
        r1.setWillNotDraw(r4);
        r1 = r7.bottomOverlayChat;
        r2 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.setPadding(r4, r2, r4, r4);
        r1 = r7.bottomOverlayChat;
        r1.setVisibility(r10);
        r1 = r7.contentView;
        r2 = r7.bottomOverlayChat;
        r3 = 80;
        r6 = -1;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r15, r3);
        r1.addView(r2, r3);
        r1 = r7.bottomOverlayChat;
        r2 = new org.telegram.ui.ChatActivity$57;
        r2.<init>();
        r1.setOnClickListener(r2);
        r1 = new android.widget.TextView;
        r1.<init>(r8);
        r7.bottomOverlayChatText = r1;
        r1 = r7.bottomOverlayChatText;
        r2 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r1.setTextSize(r11, r2);
        r1 = r7.bottomOverlayChatText;
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r1.setTypeface(r2);
        r1 = r7.bottomOverlayChatText;
        r2 = "chat_fieldOverlayText";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setTextColor(r2);
        r1 = r7.bottomOverlayChat;
        r2 = r7.bottomOverlayChatText;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r5, r13);
        r1.addView(r2, r3);
        r1 = r7.contentView;
        r2 = r7.searchContainer;
        r3 = 80;
        r5 = -1;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r15, r3);
        r1.addView(r2, r3);
        r1 = r7.chatAdapter;
        r1.updateRows();
        r1 = r7.loading;
        if (r1 == 0) goto L_0x142c;
    L_0x140d:
        r1 = r7.messages;
        r1 = r1.isEmpty();
        if (r1 == 0) goto L_0x142c;
    L_0x1415:
        r1 = r7.progressView;
        r2 = r7.chatAdapter;
        r2 = r2.botInfoRow;
        r3 = -1;
        if (r2 != r3) goto L_0x1421;
    L_0x1420:
        goto L_0x1422;
    L_0x1421:
        r4 = r10;
    L_0x1422:
        r1.setVisibility(r4);
        r1 = r7.chatListView;
        r2 = 0;
        r1.setEmptyView(r2);
        goto L_0x1438;
    L_0x142c:
        r1 = r7.progressView;
        r1.setVisibility(r10);
        r1 = r7.chatListView;
        r2 = r7.emptyViewContainer;
        r1.setEmptyView(r2);
    L_0x1438:
        r33.checkBotKeyboard();
        r33.updateContactStatus();
        r33.updateBottomOverlay();
        r33.updateSecretStatus();
        r33.updateSpamView();
        r7.updatePinnedMessageView(r11);
        r1 = r7.currentEncryptedChat;	 Catch:{ Throwable -> 0x1468 }
        if (r1 == 0) goto L_0x146d;	 Catch:{ Throwable -> 0x1468 }
    L_0x144e:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x1468 }
        r2 = 23;	 Catch:{ Throwable -> 0x1468 }
        if (r1 < r2) goto L_0x146d;	 Catch:{ Throwable -> 0x1468 }
    L_0x1454:
        r1 = org.telegram.messenger.SharedConfig.passcodeHash;	 Catch:{ Throwable -> 0x1468 }
        r1 = r1.length();	 Catch:{ Throwable -> 0x1468 }
        if (r1 == 0) goto L_0x1460;	 Catch:{ Throwable -> 0x1468 }
    L_0x145c:
        r1 = org.telegram.messenger.SharedConfig.allowScreenCapture;	 Catch:{ Throwable -> 0x1468 }
        if (r1 == 0) goto L_0x146d;	 Catch:{ Throwable -> 0x1468 }
    L_0x1460:
        r1 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Throwable -> 0x1468 }
        r1.setFlagSecure(r7, r11);	 Catch:{ Throwable -> 0x1468 }
        goto L_0x146d;
    L_0x1468:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
    L_0x146d:
        r12 = r23;
        if (r12 == 0) goto L_0x1476;
    L_0x1471:
        r1 = r7.chatActivityEnterView;
        r1.setFieldText(r12);
    L_0x1476:
        r33.fixLayoutInternal();
        r1 = r7.fragmentView;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatActivity.createView(android.content.Context):android.view.View");
    }

    private TextureView createTextureView(boolean z) {
        if (this.parentLayout == null) {
            return null;
        }
        if (this.roundVideoContainer == null) {
            if (VERSION.SDK_INT >= 21) {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    public void setTranslationY(float f) {
                        super.setTranslationY(f);
                        ChatActivity.this.contentView.invalidate();
                    }
                };
                this.roundVideoContainer.setOutlineProvider(new ViewOutlineProvider() {
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                    }
                });
                this.roundVideoContainer.setClipToOutline(true);
            } else {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    protected void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        ChatActivity.this.aspectPath.reset();
                        i = (float) (i / 2);
                        ChatActivity.this.aspectPath.addCircle(i, (float) (i2 / 2), i, Direction.CW);
                        ChatActivity.this.aspectPath.toggleInverseFillType();
                    }

                    public void setTranslationY(float f) {
                        super.setTranslationY(f);
                        ChatActivity.this.contentView.invalidate();
                    }

                    public void setVisibility(int i) {
                        super.setVisibility(i);
                        if (i == 0) {
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
            if (z) {
                this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            this.videoTextureView = new TextureView(getParentActivity());
            this.videoTextureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        z = (ViewGroup) this.roundVideoContainer.getParent();
        if (!(z == null || z == this.contentView)) {
            z.removeView(this.roundVideoContainer);
            z = false;
        }
        if (!z) {
            this.contentView.addView(this.roundVideoContainer, 1, new FrameLayout.LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize));
        }
        this.roundVideoContainer.setVisibility(4);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    private void destroyTextureView() {
        if (this.roundVideoContainer != null) {
            if (this.roundVideoContainer.getParent() != null) {
                this.contentView.removeView(this.roundVideoContainer);
                this.aspectRatioFrameLayout.setDrawingReady(false);
                this.roundVideoContainer.setVisibility(4);
                if (VERSION.SDK_INT < 21) {
                    this.roundVideoContainer.setLayerType(0, null);
                }
            }
        }
    }

    private void sendBotInlineResult(BotInlineResult botInlineResult) {
        int contextBotId = this.mentionsAdapter.getContextBotId();
        HashMap hashMap = new HashMap();
        hashMap.put(TtmlNode.ATTR_ID, botInlineResult.id);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
        stringBuilder.append(botInlineResult.query_id);
        hashMap.put("query_id", stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
        stringBuilder.append(contextBotId);
        hashMap.put("bot", stringBuilder.toString());
        hashMap.put("bot_name", this.mentionsAdapter.getContextBotName());
        SendMessagesHelper.prepareSendingBotContextResult(botInlineResult, hashMap, this.dialog_id, this.replyingMessageObject);
        this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
        showReplyPanel(false, null, null, null, false);
        DataQuery.getInstance(this.currentAccount).increaseInlineRaiting(contextBotId);
    }

    private void mentionListViewUpdateLayout() {
        if (this.mentionListView.getChildCount() <= 0) {
            this.mentionListViewScrollOffsetY = 0;
            this.mentionListViewLastViewPosition = -1;
            return;
        }
        View childAt = this.mentionListView.getChildAt(this.mentionListView.getChildCount() - 1);
        Holder holder = (Holder) this.mentionListView.findContainingViewHolder(childAt);
        Holder holder2;
        int measuredHeight;
        RecyclerListView recyclerListView;
        if (this.mentionLayoutManager.getReverseLayout()) {
            if (holder != null) {
                this.mentionListViewLastViewPosition = holder.getAdapterPosition();
                this.mentionListViewLastViewTop = childAt.getBottom();
            } else {
                this.mentionListViewLastViewPosition = -1;
            }
            childAt = this.mentionListView.getChildAt(0);
            holder2 = (Holder) this.mentionListView.findContainingViewHolder(childAt);
            measuredHeight = (childAt.getBottom() >= this.mentionListView.getMeasuredHeight() || holder2 == null || holder2.getAdapterPosition() != 0) ? this.mentionListView.getMeasuredHeight() : childAt.getBottom();
            if (this.mentionListViewScrollOffsetY != measuredHeight) {
                recyclerListView = this.mentionListView;
                this.mentionListViewScrollOffsetY = measuredHeight;
                recyclerListView.setBottomGlowOffset(measuredHeight);
                this.mentionListView.setTopGlowOffset(0);
                this.mentionListView.invalidate();
                this.mentionContainer.invalidate();
            }
        } else {
            if (holder != null) {
                this.mentionListViewLastViewPosition = holder.getAdapterPosition();
                this.mentionListViewLastViewTop = childAt.getTop();
            } else {
                this.mentionListViewLastViewPosition = -1;
            }
            childAt = this.mentionListView.getChildAt(0);
            holder2 = (Holder) this.mentionListView.findContainingViewHolder(childAt);
            measuredHeight = (childAt.getTop() <= 0 || holder2 == null || holder2.getAdapterPosition() != 0) ? 0 : childAt.getTop();
            if (this.mentionListViewScrollOffsetY != measuredHeight) {
                recyclerListView = this.mentionListView;
                this.mentionListViewScrollOffsetY = measuredHeight;
                recyclerListView.setTopGlowOffset(measuredHeight);
                this.mentionListView.setBottomGlowOffset(0);
                this.mentionListView.invalidate();
                this.mentionContainer.invalidate();
            }
        }
    }

    private void checkBotCommands() {
        boolean z = false;
        URLSpanBotCommand.enabled = false;
        if (this.currentUser != null && this.currentUser.bot) {
            URLSpanBotCommand.enabled = true;
        } else if (this.info instanceof TL_chatFull) {
            int i;
            while (i < this.info.participants.participants.size()) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(i)).user_id));
                if (user == null || !user.bot) {
                    i++;
                } else {
                    URLSpanBotCommand.enabled = true;
                    return;
                }
            }
        } else if (this.info instanceof TL_channelFull) {
            if (!(this.info.bot_info.isEmpty() || this.currentChat == null || !this.currentChat.megagroup)) {
                z = true;
            }
            URLSpanBotCommand.enabled = z;
        }
    }

    private GroupedMessages getValidGroupedMessage(MessageObject messageObject) {
        if (messageObject.getGroupId() == 0) {
            return null;
        }
        GroupedMessages groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(messageObject.getGroupId());
        if (groupedMessages != null) {
            if (groupedMessages.messages.size() <= 1) {
                return null;
            }
            if (groupedMessages.positions.get(messageObject) == null) {
                return null;
            }
        }
        return groupedMessages;
    }

    private void jumpToDate(int i) {
        if (!this.messages.isEmpty()) {
            MessageObject messageObject = (MessageObject) this.messages.get(this.messages.size() - 1);
            if (((MessageObject) this.messages.get(0)).messageOwner.date >= i && messageObject.messageOwner.date <= i) {
                int size = this.messages.size() - 1;
                while (size >= 0) {
                    messageObject = (MessageObject) this.messages.get(size);
                    if (messageObject.messageOwner.date < i || messageObject.getId() == 0) {
                        size--;
                    } else {
                        scrollToMessageId(messageObject.getId(), 0, false, messageObject.getDialogId() == this.mergeDialogId ? 1 : 0, false);
                    }
                }
            } else if (((int) this.dialog_id) != 0) {
                clearChatData();
                this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                MessagesController instance = MessagesController.getInstance(this.currentAccount);
                long j = this.dialog_id;
                int i2 = this.classGuid;
                boolean isChannel = ChatObject.isChannel(this.currentChat);
                int i3 = this.lastLoadIndex;
                this.lastLoadIndex = i3 + 1;
                instance.loadMessages(j, bot_help, 0, i, true, 0, i2, 4, 0, isChannel, i3);
                this.floatingDateView.setAlpha(0.0f);
                this.floatingDateView.setTag(null);
            }
        }
    }

    public void processInlineBotContextPM(TL_inlineBotSwitchPM tL_inlineBotSwitchPM) {
        if (tL_inlineBotSwitchPM != null) {
            User contextBotUser = this.mentionsAdapter.getContextBotUser();
            if (contextBotUser != null) {
                this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                if (this.dialog_id == ((long) contextBotUser.id)) {
                    this.inlineReturn = this.dialog_id;
                    MessagesController.getInstance(this.currentAccount).sendBotStart(this.currentUser, tL_inlineBotSwitchPM.start_param);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", contextBotUser.id);
                    bundle.putString("inline_query", tL_inlineBotSwitchPM.start_param);
                    bundle.putLong("inline_return", this.dialog_id);
                    if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this) != null) {
                        presentFragment(new ChatActivity(bundle));
                    }
                }
            }
        }
    }

    private void createChatAttachView() {
        if (getParentActivity() != null && this.chatAttachAlert == null) {
            this.chatAttachAlert = new ChatAttachAlert(getParentActivity(), this);
            this.chatAttachAlert.setDelegate(new ChatAttachViewDelegate() {
                public void didPressedButton(int i) {
                    int i2 = i;
                    if (ChatActivity.this.getParentActivity() != null) {
                        if (ChatActivity.this.chatAttachAlert != null) {
                            if (!(i2 == 8 || i2 == 7)) {
                                if (i2 != 4 || ChatActivity.this.chatAttachAlert.getSelectedPhotos().isEmpty()) {
                                    if (ChatActivity.this.chatAttachAlert != null) {
                                        ChatActivity.this.chatAttachAlert.dismissWithButtonClick(i2);
                                    }
                                    ChatActivity.this.processSelectedAttach(i2);
                                    return;
                                }
                            }
                            if (i2 != 8) {
                                ChatActivity.this.chatAttachAlert.dismiss();
                            }
                            HashMap selectedPhotos = ChatActivity.this.chatAttachAlert.getSelectedPhotos();
                            ArrayList selectedPhotosOrder = ChatActivity.this.chatAttachAlert.getSelectedPhotosOrder();
                            if (!selectedPhotos.isEmpty()) {
                                ArrayList arrayList = new ArrayList();
                                for (int i3 = 0; i3 < selectedPhotosOrder.size(); i3++) {
                                    PhotoEntry photoEntry = (PhotoEntry) selectedPhotos.get(selectedPhotosOrder.get(i3));
                                    SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
                                    if (photoEntry.imagePath != null) {
                                        sendingMediaInfo.path = photoEntry.imagePath;
                                    } else if (photoEntry.path != null) {
                                        sendingMediaInfo.path = photoEntry.path;
                                    }
                                    sendingMediaInfo.isVideo = photoEntry.isVideo;
                                    ArrayList arrayList2 = null;
                                    sendingMediaInfo.caption = photoEntry.caption != null ? photoEntry.caption.toString() : null;
                                    sendingMediaInfo.entities = photoEntry.entities;
                                    if (!photoEntry.stickers.isEmpty()) {
                                        arrayList2 = new ArrayList(photoEntry.stickers);
                                    }
                                    sendingMediaInfo.masks = arrayList2;
                                    sendingMediaInfo.ttl = photoEntry.ttl;
                                    sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                                    arrayList.add(sendingMediaInfo);
                                    photoEntry.reset();
                                }
                                SendMessagesHelper.prepareSendingMedia(arrayList, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null, i2 == 4, SharedConfig.groupPhotosEnabled);
                                ChatActivity.this.showReplyPanel(false, null, null, null, false);
                                DataQuery.getInstance(ChatActivity.this.currentAccount).cleanDraft(ChatActivity.this.dialog_id, true);
                            }
                        }
                    }
                }

                public View getRevealView() {
                    return ChatActivity.this.chatActivityEnterView.getAttachButton();
                }

                public void didSelectBot(User user) {
                    if (ChatActivity.this.chatActivityEnterView != null) {
                        if (!TextUtils.isEmpty(user.username)) {
                            ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("@");
                            stringBuilder.append(user.username);
                            stringBuilder.append(" ");
                            chatActivityEnterView.setFieldText(stringBuilder.toString());
                            ChatActivity.this.chatActivityEnterView.openKeyboard();
                        }
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

    public void setBotUser(String str) {
        if (this.inlineReturn != 0) {
            MessagesController.getInstance(this.currentAccount).sendBotStart(this.currentUser, str);
            return;
        }
        this.botUser = str;
        updateBottomOverlay();
    }

    public boolean playFirstUnreadVoiceMessage() {
        if (this.chatActivityEnterView != null && this.chatActivityEnterView.isRecordingAudioVideo()) {
            return true;
        }
        for (int size = this.messages.size() - 1; size >= 0; size--) {
            MessageObject messageObject = (MessageObject) this.messages.get(size);
            if ((messageObject.isVoice() || messageObject.isRoundVideo()) && messageObject.isContentUnread() && !messageObject.isOut()) {
                MediaController.getInstance().setVoiceMessagesPlaylist(MediaController.getInstance().playMessage(messageObject) ? createVoiceMessagesPlaylist(messageObject, true) : null, true);
                return true;
            }
        }
        if (VERSION.SDK_INT < edit || getParentActivity() == null || getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            return false;
        }
        getParentActivity().requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
        return true;
    }

    private void initStickers() {
        if (!(this.chatActivityEnterView == null || getParentActivity() == null || this.stickersAdapter != null)) {
            if (this.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= edit) {
                if (this.stickersAdapter != null) {
                    this.stickersAdapter.onDestroy();
                }
                this.stickersListView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
                RecyclerListView recyclerListView = this.stickersListView;
                Adapter stickersAdapter = new StickersAdapter(getParentActivity(), new StickersAdapterDelegate() {
                    public void needChangePanelVisibility(final boolean z) {
                        if (!(z && ChatActivity.this.stickersPanel.getVisibility() == 0) && (z || ChatActivity.this.stickersPanel.getVisibility() != 8)) {
                            if (z) {
                                ChatActivity.this.stickersListView.scrollToPosition(0);
                                ChatActivity.this.stickersPanel.setVisibility(ChatActivity.this.allowStickersPanel ? 0 : 4);
                            }
                            if (ChatActivity.this.runningAnimation != null) {
                                ChatActivity.this.runningAnimation.cancel();
                                ChatActivity.this.runningAnimation = null;
                            }
                            if (ChatActivity.this.stickersPanel.getVisibility() != 4) {
                                ChatActivity.this.runningAnimation = new AnimatorSet();
                                AnimatorSet access$22500 = ChatActivity.this.runningAnimation;
                                Animator[] animatorArr = new Animator[1];
                                FrameLayout access$18900 = ChatActivity.this.stickersPanel;
                                String str = "alpha";
                                float[] fArr = new float[2];
                                float f = 1.0f;
                                fArr[0] = z ? 0.0f : 1.0f;
                                if (!z) {
                                    f = 0.0f;
                                }
                                fArr[1] = f;
                                animatorArr[0] = ObjectAnimator.ofFloat(access$18900, str, fArr);
                                access$22500.playTogether(animatorArr);
                                ChatActivity.this.runningAnimation.setDuration(150);
                                ChatActivity.this.runningAnimation.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        if (ChatActivity.this.runningAnimation != null && ChatActivity.this.runningAnimation.equals(animator) != null) {
                                            if (z == null) {
                                                ChatActivity.this.stickersAdapter.clearStickers();
                                                ChatActivity.this.stickersPanel.setVisibility(8);
                                                if (StickerPreviewViewer.getInstance().isVisible() != null) {
                                                    StickerPreviewViewer.getInstance().close();
                                                }
                                                StickerPreviewViewer.getInstance().reset();
                                            }
                                            ChatActivity.this.runningAnimation = null;
                                        }
                                    }

                                    public void onAnimationCancel(Animator animator) {
                                        if (ChatActivity.this.runningAnimation != null && ChatActivity.this.runningAnimation.equals(animator) != null) {
                                            ChatActivity.this.runningAnimation = null;
                                        }
                                    }
                                });
                                ChatActivity.this.runningAnimation.start();
                            } else if (!z) {
                                ChatActivity.this.stickersPanel.setVisibility(8);
                            }
                        }
                    }
                });
                this.stickersAdapter = stickersAdapter;
                recyclerListView.setAdapter(stickersAdapter);
                recyclerListView = this.stickersListView;
                OnItemClickListener anonymousClass63 = new OnItemClickListener() {
                    public void onItemClick(View view, int i) {
                        view = ChatActivity.this.stickersAdapter.getItem(i);
                        if ((view instanceof TL_document) != 0) {
                            SendMessagesHelper.getInstance(ChatActivity.this.currentAccount).sendSticker(view, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                            ChatActivity.this.showReplyPanel(false, null, null, null, false);
                            ChatActivity.this.chatActivityEnterView.addStickerToRecent(view);
                        }
                        ChatActivity.this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                };
                this.stickersOnItemClickListener = anonymousClass63;
                recyclerListView.setOnItemClickListener(anonymousClass63);
            }
        }
    }

    public void shareMyContact(final MessageObject messageObject) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("ShareYouPhoneNumberTitle", C0446R.string.ShareYouPhoneNumberTitle));
        if (this.currentUser == null) {
            builder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfo", C0446R.string.AreYouSureShareMyContactInfo));
        } else if (this.currentUser.bot) {
            builder.setMessage(LocaleController.getString("AreYouSureShareMyContactInfoBot", C0446R.string.AreYouSureShareMyContactInfoBot));
        } else {
            Object[] objArr = new Object[2];
            PhoneFormat instance = PhoneFormat.getInstance();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("+");
            stringBuilder.append(UserConfig.getInstance(this.currentAccount).getCurrentUser().phone);
            objArr[0] = instance.format(stringBuilder.toString());
            objArr[1] = ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name);
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureShareMyContactInfoUser", C0446R.string.AreYouSureShareMyContactInfoUser, objArr)));
        }
        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SendMessagesHelper.getInstance(ChatActivity.this.currentAccount).sendMessage(UserConfig.getInstance(ChatActivity.this.currentAccount).getCurrentUser(), ChatActivity.this.dialog_id, messageObject, null, null);
                ChatActivity.this.moveScrollToLastMessage();
                ChatActivity.this.showReplyPanel(false, null, null, null, false);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void hideVoiceHint() {
        this.voiceHintAnimation = new AnimatorSet();
        AnimatorSet animatorSet = this.voiceHintAnimation;
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.voiceHintTextView, "alpha", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        this.voiceHintAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(ChatActivity.this.voiceHintAnimation) != null) {
                    ChatActivity.this.voiceHintAnimation = null;
                    ChatActivity.this.voiceHintHideRunnable = null;
                    if (ChatActivity.this.voiceHintTextView != null) {
                        ChatActivity.this.voiceHintTextView.setVisibility(8);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(ChatActivity.this.voiceHintAnimation) != null) {
                    ChatActivity.this.voiceHintHideRunnable = null;
                    ChatActivity.this.voiceHintHideRunnable = null;
                }
            }
        });
        this.voiceHintAnimation.setDuration(300);
        this.voiceHintAnimation.start();
    }

    private void showVoiceHint(boolean z, boolean z2) {
        if (!(getParentActivity() == null || this.fragmentView == null)) {
            if (!z || this.voiceHintTextView != null) {
                int indexOfChild;
                if (this.voiceHintTextView == null) {
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
                    indexOfChild = sizeNotifierFrameLayout.indexOfChild(this.chatActivityEnterView);
                    if (indexOfChild != -1) {
                        this.voiceHintTextView = new TextView(getParentActivity());
                        this.voiceHintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
                        this.voiceHintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
                        this.voiceHintTextView.setTextSize(1, 14.0f);
                        this.voiceHintTextView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
                        this.voiceHintTextView.setGravity(16);
                        this.voiceHintTextView.setAlpha(0.0f);
                        sizeNotifierFrameLayout.addView(this.voiceHintTextView, indexOfChild + 1, LayoutHelper.createFrame(-2, -2.0f, 85, 5.0f, 0.0f, 5.0f, 3.0f));
                    } else {
                        return;
                    }
                }
                if (z) {
                    if (this.voiceHintAnimation) {
                        this.voiceHintAnimation.cancel();
                        this.voiceHintAnimation = null;
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.voiceHintHideRunnable);
                    this.voiceHintHideRunnable = null;
                    hideVoiceHint();
                    return;
                }
                z = this.voiceHintTextView;
                if (z2) {
                    z2 = "HoldToVideo";
                    indexOfChild = C0446R.string.HoldToVideo;
                } else {
                    z2 = "HoldToAudio";
                    indexOfChild = C0446R.string.HoldToAudio;
                }
                z.setText(LocaleController.getString(z2, indexOfChild));
                if (this.voiceHintHideRunnable) {
                    if (this.voiceHintAnimation) {
                        this.voiceHintAnimation.cancel();
                        this.voiceHintAnimation = null;
                    } else {
                        AndroidUtilities.cancelRunOnUIThread(this.voiceHintHideRunnable);
                        z = new Runnable() {
                            public void run() {
                                ChatActivity.this.hideVoiceHint();
                            }
                        };
                        this.voiceHintHideRunnable = z;
                        AndroidUtilities.runOnUIThread(z, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        return;
                    }
                } else if (this.voiceHintAnimation) {
                    return;
                }
                this.voiceHintTextView.setVisibility(0);
                this.voiceHintAnimation = new AnimatorSet();
                z = this.voiceHintAnimation;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.voiceHintTextView, "alpha", new float[]{1.0f});
                z.playTogether(animatorArr);
                this.voiceHintAnimation.addListener(new AnimatorListenerAdapter() {

                    /* renamed from: org.telegram.ui.ChatActivity$67$1 */
                    class C10391 implements Runnable {
                        C10391() {
                        }

                        public void run() {
                            ChatActivity.this.hideVoiceHint();
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivity.this.voiceHintAnimation) != null) {
                            ChatActivity.this.voiceHintAnimation = null;
                            AndroidUtilities.runOnUIThread(ChatActivity.this.voiceHintHideRunnable = new C10391(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (animator.equals(ChatActivity.this.voiceHintAnimation) != null) {
                            ChatActivity.this.voiceHintAnimation = null;
                        }
                    }
                });
                this.voiceHintAnimation.setDuration(300);
                this.voiceHintAnimation.start();
            }
        }
    }

    private void showMediaBannedHint() {
        if (!(getParentActivity() == null || this.currentChat == null || this.currentChat.banned_rights == null || this.fragmentView == null)) {
            if (this.mediaBanTooltip == null || this.mediaBanTooltip.getVisibility() != 0) {
                SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
                int indexOfChild = sizeNotifierFrameLayout.indexOfChild(this.chatActivityEnterView);
                if (indexOfChild != -1) {
                    if (this.mediaBanTooltip == null) {
                        this.mediaBanTooltip = new CorrectlyMeasuringTextView(getParentActivity());
                        this.mediaBanTooltip.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
                        this.mediaBanTooltip.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
                        this.mediaBanTooltip.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
                        this.mediaBanTooltip.setGravity(16);
                        this.mediaBanTooltip.setTextSize(1, 14.0f);
                        sizeNotifierFrameLayout.addView(this.mediaBanTooltip, indexOfChild + 1, LayoutHelper.createFrame(-2, -2.0f, 85, 30.0f, 0.0f, 5.0f, 3.0f));
                    }
                    if (AndroidUtilities.isBannedForever(this.currentChat.banned_rights.until_date)) {
                        this.mediaBanTooltip.setText(LocaleController.getString("AttachMediaRestrictedForever", C0446R.string.AttachMediaRestrictedForever));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestricted", C0446R.string.AttachMediaRestricted, LocaleController.formatDateForBan((long) this.currentChat.banned_rights.until_date)));
                    }
                    this.mediaBanTooltip.setVisibility(0);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaBanTooltip, "alpha", new float[]{0.0f, 1.0f})});
                    animatorSet.addListener(new AnimatorListenerAdapter() {

                        /* renamed from: org.telegram.ui.ChatActivity$68$1 */
                        class C10411 implements Runnable {

                            /* renamed from: org.telegram.ui.ChatActivity$68$1$1 */
                            class C10401 extends AnimatorListenerAdapter {
                                C10401() {
                                }

                                public void onAnimationEnd(Animator animator) {
                                    if (ChatActivity.this.mediaBanTooltip != null) {
                                        ChatActivity.this.mediaBanTooltip.setVisibility(8);
                                    }
                                }
                            }

                            C10411() {
                            }

                            public void run() {
                                if (ChatActivity.this.mediaBanTooltip != null) {
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    Animator[] animatorArr = new Animator[1];
                                    animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.mediaBanTooltip, "alpha", new float[]{0.0f});
                                    animatorSet.playTogether(animatorArr);
                                    animatorSet.addListener(new C10401());
                                    animatorSet.setDuration(300);
                                    animatorSet.start();
                                }
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
                            AndroidUtilities.runOnUIThread(new C10411(), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                        }
                    });
                    animatorSet.setDuration(300);
                    animatorSet.start();
                }
            }
        }
    }

    private void showGifHint() {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (!globalMainSettings.getBoolean("gifhint", false)) {
            globalMainSettings.edit().putBoolean("gifhint", true).commit();
            if (!(getParentActivity() == null || this.fragmentView == null)) {
                if (this.gifHintTextView == null) {
                    if (this.allowContextBotPanelSecond) {
                        SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
                        int indexOfChild = sizeNotifierFrameLayout.indexOfChild(this.chatActivityEnterView);
                        if (indexOfChild != -1) {
                            this.chatActivityEnterView.setOpenGifsTabFirst();
                            this.emojiButtonRed = new View(getParentActivity());
                            this.emojiButtonRed.setBackgroundResource(C0446R.drawable.redcircle);
                            indexOfChild++;
                            sizeNotifierFrameLayout.addView(this.emojiButtonRed, indexOfChild, LayoutHelper.createFrame(10, 10.0f, 83, 30.0f, 0.0f, 0.0f, 27.0f));
                            this.gifHintTextView = new TextView(getParentActivity());
                            this.gifHintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
                            this.gifHintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
                            this.gifHintTextView.setTextSize(1, 14.0f);
                            this.gifHintTextView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
                            this.gifHintTextView.setText(LocaleController.getString("TapHereGifs", C0446R.string.TapHereGifs));
                            this.gifHintTextView.setGravity(16);
                            sizeNotifierFrameLayout.addView(this.gifHintTextView, indexOfChild, LayoutHelper.createFrame(-2, -2.0f, 83, 5.0f, 0.0f, 5.0f, 3.0f));
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.gifHintTextView, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.emojiButtonRed, "alpha", new float[]{0.0f, 1.0f})});
                            animatorSet.addListener(new AnimatorListenerAdapter() {

                                /* renamed from: org.telegram.ui.ChatActivity$69$1 */
                                class C10431 implements Runnable {

                                    /* renamed from: org.telegram.ui.ChatActivity$69$1$1 */
                                    class C10421 extends AnimatorListenerAdapter {
                                        C10421() {
                                        }

                                        public void onAnimationEnd(Animator animator) {
                                            if (ChatActivity.this.gifHintTextView != null) {
                                                ChatActivity.this.gifHintTextView.setVisibility(8);
                                            }
                                        }
                                    }

                                    C10431() {
                                    }

                                    public void run() {
                                        if (ChatActivity.this.gifHintTextView != null) {
                                            AnimatorSet animatorSet = new AnimatorSet();
                                            Animator[] animatorArr = new Animator[1];
                                            animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.gifHintTextView, "alpha", new float[]{0.0f});
                                            animatorSet.playTogether(animatorArr);
                                            animatorSet.addListener(new C10421());
                                            animatorSet.setDuration(300);
                                            animatorSet.start();
                                        }
                                    }
                                }

                                public void onAnimationEnd(Animator animator) {
                                    AndroidUtilities.runOnUIThread(new C10431(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                                }
                            });
                            animatorSet.setDuration(300);
                            animatorSet.start();
                            return;
                        }
                        return;
                    }
                    if (this.chatActivityEnterView != null) {
                        this.chatActivityEnterView.setOpenGifsTabFirst();
                    }
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
                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator) != null) {
                            ChatActivity.this.mentionListAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator) != null) {
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
                public void onAnimationEnd(Animator animator) {
                    if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator) != null) {
                        ChatActivity.this.mentionContainer.setVisibility(4);
                        ChatActivity.this.mentionListAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatActivity.this.mentionListAnimation != null && ChatActivity.this.mentionListAnimation.equals(animator) != null) {
                        ChatActivity.this.mentionListAnimation = null;
                    }
                }
            });
            this.mentionListAnimation.setDuration(200);
            this.mentionListAnimation.start();
        }
    }

    private void hideFloatingDateView(boolean z) {
        if (this.floatingDateView.getTag() != null && !this.currentFloatingDateOnScreen) {
            if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
                this.floatingDateView.setTag(null);
                if (z) {
                    this.floatingDateAnimation = new AnimatorSet();
                    this.floatingDateAnimation.setDuration(150);
                    z = this.floatingDateAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.floatingDateView, "alpha", new float[]{0.0f});
                    z.playTogether(animatorArr);
                    this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatActivity.this.floatingDateAnimation) != null) {
                                ChatActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    this.floatingDateAnimation.setStartDelay(500);
                    this.floatingDateAnimation.start();
                    return;
                }
                if (this.floatingDateAnimation) {
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

    protected void setIgnoreAttachOnPause(boolean z) {
        this.ignoreAttachOnPause = z;
    }

    private void checkScrollForLoad(boolean z) {
        if (this.chatLayoutManager != null) {
            if (!r0.paused) {
                int i;
                int findFirstVisibleItemPosition = r0.chatLayoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition == -1) {
                    i = 0;
                } else {
                    i = Math.abs(r0.chatLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                }
                if (i > 0 || r0.currentEncryptedChat != null) {
                    if ((r0.chatAdapter.getItemCount() - findFirstVisibleItemPosition) - i <= (z ? 25 : 5) && !r0.loading) {
                        int i2;
                        if (!r0.endReached[0]) {
                            r0.loading = true;
                            r0.waitingForLoad.add(Integer.valueOf(r0.lastLoadIndex));
                            int i3;
                            if (r0.messagesByDays.size() != 0) {
                                MessagesController instance = MessagesController.getInstance(r0.currentAccount);
                                long j = r0.dialog_id;
                                i2 = r0.maxMessageId[0];
                                boolean z2 = r0.cacheEndReached[0] ^ 1;
                                int i4 = r0.minDate[0];
                                i3 = r0.classGuid;
                                boolean isChannel = ChatObject.isChannel(r0.currentChat);
                                int i5 = r0.lastLoadIndex;
                                r0.lastLoadIndex = i5 + 1;
                                instance.loadMessages(j, 50, i2, 0, z2, i4, i3, 0, 0, isChannel, i5);
                            } else {
                                MessagesController instance2 = MessagesController.getInstance(r0.currentAccount);
                                long j2 = r0.dialog_id;
                                boolean z3 = r0.cacheEndReached[0] ^ 1;
                                i2 = r0.minDate[0];
                                int i6 = r0.classGuid;
                                boolean isChannel2 = ChatObject.isChannel(r0.currentChat);
                                i3 = r0.lastLoadIndex;
                                r0.lastLoadIndex = i3 + 1;
                                instance2.loadMessages(j2, 50, 0, 0, z3, i2, i6, 0, 0, isChannel2, i3);
                            }
                        } else if (!(r0.mergeDialogId == 0 || r0.endReached[1])) {
                            r0.loading = true;
                            r0.waitingForLoad.add(Integer.valueOf(r0.lastLoadIndex));
                            MessagesController instance3 = MessagesController.getInstance(r0.currentAccount);
                            long j3 = r0.mergeDialogId;
                            int i7 = r0.maxMessageId[1];
                            boolean z4 = r0.cacheEndReached[1] ^ 1;
                            int i8 = r0.minDate[1];
                            i2 = r0.classGuid;
                            boolean isChannel3 = ChatObject.isChannel(r0.currentChat);
                            int i9 = r0.lastLoadIndex;
                            r0.lastLoadIndex = i9 + 1;
                            instance3.loadMessages(j3, 50, i7, 0, z4, i8, i2, 0, 0, isChannel3, i9);
                        }
                    }
                    if (i > 0 && !r0.loadingForward && findFirstVisibleItemPosition <= 10) {
                        MessagesController instance4;
                        long j4;
                        int i10;
                        int i11;
                        int i12;
                        boolean isChannel4;
                        int i13;
                        if (r0.mergeDialogId != 0 && !r0.forwardEndReached[1]) {
                            r0.waitingForLoad.add(Integer.valueOf(r0.lastLoadIndex));
                            instance4 = MessagesController.getInstance(r0.currentAccount);
                            j4 = r0.mergeDialogId;
                            i10 = r0.minMessageId[1];
                            i11 = r0.maxDate[1];
                            i12 = r0.classGuid;
                            isChannel4 = ChatObject.isChannel(r0.currentChat);
                            i13 = r0.lastLoadIndex;
                            r0.lastLoadIndex = i13 + 1;
                            instance4.loadMessages(j4, 50, i10, 0, true, i11, i12, 1, 0, isChannel4, i13);
                            r0.loadingForward = true;
                        } else if (!r0.forwardEndReached[0]) {
                            r0.waitingForLoad.add(Integer.valueOf(r0.lastLoadIndex));
                            instance4 = MessagesController.getInstance(r0.currentAccount);
                            j4 = r0.dialog_id;
                            i10 = r0.minMessageId[0];
                            i11 = r0.maxDate[0];
                            i12 = r0.classGuid;
                            isChannel4 = ChatObject.isChannel(r0.currentChat);
                            i13 = r0.lastLoadIndex;
                            r0.lastLoadIndex = i13 + 1;
                            instance4.loadMessages(j4, 50, i10, 0, true, i11, i12, 1, 0, isChannel4, i13);
                            r0.loadingForward = true;
                        }
                    }
                }
            }
        }
    }

    private void processSelectedAttach(int i) {
        int i2 = 0;
        File generatePicturePath;
        if (i == 0) {
            if (VERSION.SDK_INT < edit || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                try {
                    i = new Intent("android.media.action.IMAGE_CAPTURE");
                    generatePicturePath = AndroidUtilities.generatePicturePath();
                    if (generatePicturePath != null) {
                        if (VERSION.SDK_INT >= 24) {
                            i.putExtra("output", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.provider", generatePicturePath));
                            i.addFlags(2);
                            i.addFlags(1);
                        } else {
                            i.putExtra("output", Uri.fromFile(generatePicturePath));
                        }
                        this.currentPicturePath = generatePicturePath.getAbsolutePath();
                    }
                    startActivityForResult(i, 0);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } else {
                getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
            }
        } else if (i == 1) {
            if (VERSION.SDK_INT < edit || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                boolean z;
                if (this.currentEncryptedChat != null) {
                    if (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 46) {
                        z = false;
                        i = new PhotoAlbumPickerActivity(false, z, true, this);
                        i.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                            public void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList) {
                                SendMessagesHelper.prepareSendingMedia(arrayList, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null, false, SharedConfig.groupPhotosEnabled);
                                ChatActivity.this.showReplyPanel(false, null, null, null, false);
                                DataQuery.getInstance(ChatActivity.this.currentAccount).cleanDraft(ChatActivity.this.dialog_id, true);
                            }

                            public void startPhotoSelectActivity() {
                                try {
                                    Intent intent = new Intent();
                                    intent.setType("video/*");
                                    intent.setAction("android.intent.action.GET_CONTENT");
                                    intent.putExtra("android.intent.extra.sizeLimit", NUM);
                                    Intent intent2 = new Intent("android.intent.action.PICK");
                                    intent2.setType("image/*");
                                    intent2 = Intent.createChooser(intent2, null);
                                    intent2.putExtra("android.intent.extra.INITIAL_INTENTS", new Intent[]{intent});
                                    ChatActivity.this.startActivityForResult(intent2, 1);
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        });
                        presentFragment(i);
                    }
                }
                z = true;
                i = new PhotoAlbumPickerActivity(false, z, true, this);
                i.setDelegate(/* anonymous class already generated */);
                presentFragment(i);
            } else {
                getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
            }
        } else if (i == 2) {
            if (VERSION.SDK_INT < edit || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                try {
                    i = new Intent("android.media.action.VIDEO_CAPTURE");
                    generatePicturePath = AndroidUtilities.generateVideoPath();
                    if (generatePicturePath != null) {
                        if (VERSION.SDK_INT >= 24) {
                            i.putExtra("output", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.provider", generatePicturePath));
                            i.addFlags(2);
                            i.addFlags(1);
                        } else if (VERSION.SDK_INT >= 18) {
                            i.putExtra("output", Uri.fromFile(generatePicturePath));
                        }
                        i.putExtra("android.intent.extra.sizeLimit", NUM);
                        this.currentPicturePath = generatePicturePath.getAbsolutePath();
                    }
                    startActivityForResult(i, 2);
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            } else {
                getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 20);
            }
        } else if (i == 6) {
            if (AndroidUtilities.isGoogleMapsInstalled(this) != 0) {
                if (this.currentEncryptedChat == null) {
                    i2 = 1;
                }
                i = new LocationActivity(i2);
                i.setDialogId(this.dialog_id);
                i.setDelegate(this);
                presentFragment(i);
            }
        } else if (i == 4) {
            if (VERSION.SDK_INT < edit || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                i = new DocumentSelectActivity();
                i.setDelegate(new DocumentSelectActivityDelegate() {
                    public void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList) {
                        documentSelectActivity.finishFragment();
                        SendMessagesHelper.prepareSendingDocuments(arrayList, arrayList, null, null, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, null);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false);
                        DataQuery.getInstance(ChatActivity.this.currentAccount).cleanDraft(ChatActivity.this.dialog_id, true);
                    }

                    public void startDocumentSelectActivity() {
                        try {
                            Intent intent = new Intent("android.intent.action.GET_CONTENT");
                            if (VERSION.SDK_INT >= 18) {
                                intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
                            }
                            intent.setType("*/*");
                            ChatActivity.this.startActivityForResult(intent, 21);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
                presentFragment(i);
            } else {
                getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
            }
        } else if (i == 3) {
            if (VERSION.SDK_INT < edit || getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                i = new AudioSelectActivity();
                i.setDelegate(new AudioSelectActivityDelegate() {
                    public void didSelectAudio(ArrayList<MessageObject> arrayList) {
                        SendMessagesHelper.prepareSendingAudioDocuments(arrayList, ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject);
                        ChatActivity.this.showReplyPanel(false, null, null, null, false);
                        DataQuery.getInstance(ChatActivity.this.currentAccount).cleanDraft(ChatActivity.this.dialog_id, true);
                    }
                });
                presentFragment(i);
            } else {
                getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
            }
        } else if (i == 5) {
            if (VERSION.SDK_INT < edit || getParentActivity().checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                try {
                    i = new Intent("android.intent.action.PICK", Contacts.CONTENT_URI);
                    i.setType("vnd.android.cursor.dir/phone_v2");
                    startActivityForResult(i, bot_settings);
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
            } else {
                getParentActivity().requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 5);
            }
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return (dialog == this.chatAttachAlert || super.dismissDialogOnPause(dialog) == null) ? null : true;
    }

    private void searchLinks(final CharSequence charSequence, final boolean z) {
        if (this.currentEncryptedChat == null || (MessagesController.getInstance(this.currentAccount).secretWebpagePreview != 0 && AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= 46)) {
            if (z && this.foundWebPage != null) {
                if (this.foundWebPage.url != null) {
                    int indexOf = TextUtils.indexOf(charSequence, this.foundWebPage.url);
                    char c = '\u0001';
                    char c2 = '\u0000';
                    if (indexOf != -1) {
                        if (this.foundWebPage.url.length() + indexOf != charSequence.length()) {
                            c = '\u0000';
                        }
                        if (c == '\u0000') {
                            c2 = charSequence.charAt(this.foundWebPage.url.length() + indexOf);
                        }
                    } else if (this.foundWebPage.display_url != null) {
                        indexOf = TextUtils.indexOf(charSequence, this.foundWebPage.display_url);
                        if (indexOf == -1 || this.foundWebPage.display_url.length() + indexOf != charSequence.length()) {
                            c = '\u0000';
                        }
                        if (indexOf != -1 && r2 == '\u0000') {
                            c2 = charSequence.charAt(this.foundWebPage.display_url.length() + indexOf);
                        }
                    } else {
                        c = '\u0000';
                    }
                    if (indexOf != -1 && (r2 != '\u0000' || r4 == ' ' || r4 == ',' || r4 == '.' || r4 == '!' || r4 == '/')) {
                        return;
                    }
                }
                this.pendingLinkSearchString = null;
                showReplyPanel(false, null, null, this.foundWebPage, false);
            }
            final MessagesController instance = MessagesController.getInstance(this.currentAccount);
            Utilities.searchQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.ui.ChatActivity$76$1 */
                class C10451 implements Runnable {
                    C10451() {
                    }

                    public void run() {
                        if (ChatActivity.this.foundWebPage != null) {
                            ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false);
                            ChatActivity.this.foundWebPage = null;
                        }
                    }
                }

                /* renamed from: org.telegram.ui.ChatActivity$76$2 */
                class C10462 implements Runnable {
                    C10462() {
                    }

                    public void run() {
                        if (ChatActivity.this.foundWebPage != null) {
                            ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false);
                            ChatActivity.this.foundWebPage = null;
                        }
                    }
                }

                /* renamed from: org.telegram.ui.ChatActivity$76$3 */
                class C10483 implements Runnable {

                    /* renamed from: org.telegram.ui.ChatActivity$76$3$1 */
                    class C10471 implements OnClickListener {
                        C10471() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            instance.secretWebpagePreview = 1;
                            MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", MessagesController.getInstance(ChatActivity.this.currentAccount).secretWebpagePreview).commit();
                            ChatActivity.this.foundUrls = 0;
                            ChatActivity.this.searchLinks(charSequence, z);
                        }
                    }

                    C10483() {
                    }

                    public void run() {
                        Builder builder = new Builder(ChatActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C10471());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                        builder.setMessage(LocaleController.getString("SecretLinkPreviewAlert", C0446R.string.SecretLinkPreviewAlert));
                        ChatActivity.this.showDialog(builder.create());
                        instance.secretWebpagePreview = 0;
                        MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", instance.secretWebpagePreview).commit();
                    }
                }

                public void run() {
                    if (ChatActivity.this.linkSearchRequestId != 0) {
                        ConnectionsManager.getInstance(ChatActivity.this.currentAccount).cancelRequest(ChatActivity.this.linkSearchRequestId, true);
                        ChatActivity.this.linkSearchRequestId = 0;
                    }
                    Iterable iterable = null;
                    CharSequence join;
                    try {
                        Matcher matcher = AndroidUtilities.WEB_URL.matcher(charSequence);
                        while (matcher.find()) {
                            if (matcher.start() <= 0 || charSequence.charAt(matcher.start() - 1) != '@') {
                                if (iterable == null) {
                                    iterable = new ArrayList();
                                }
                                iterable.add(charSequence.subSequence(matcher.start(), matcher.end()));
                            }
                        }
                        if (!(iterable == null || ChatActivity.this.foundUrls == null || iterable.size() != ChatActivity.this.foundUrls.size())) {
                            boolean z = true;
                            for (int i = 0; i < iterable.size(); i++) {
                                if (!TextUtils.equals((CharSequence) iterable.get(i), (CharSequence) ChatActivity.this.foundUrls.get(i))) {
                                    z = false;
                                }
                            }
                            if (z) {
                                return;
                            }
                        }
                        ChatActivity.this.foundUrls = iterable;
                        if (iterable == null) {
                            AndroidUtilities.runOnUIThread(new C10451());
                            return;
                        }
                        join = TextUtils.join(" ", iterable);
                        if (ChatActivity.this.currentEncryptedChat == null || instance.secretWebpagePreview != 2) {
                            final TLObject tL_messages_getWebPagePreview = new TL_messages_getWebPagePreview();
                            if (join instanceof String) {
                                tL_messages_getWebPagePreview.message = (String) join;
                            } else {
                                tL_messages_getWebPagePreview.message = join.toString();
                            }
                            ChatActivity.this.linkSearchRequestId = ConnectionsManager.getInstance(ChatActivity.this.currentAccount).sendRequest(tL_messages_getWebPagePreview, new RequestDelegate() {
                                public void run(final TLObject tLObject, final TL_error tL_error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            ChatActivity.this.linkSearchRequestId = 0;
                                            if (tL_error != null) {
                                                return;
                                            }
                                            if (tLObject instanceof TL_messageMediaWebPage) {
                                                ChatActivity.this.foundWebPage = ((TL_messageMediaWebPage) tLObject).webpage;
                                                if (!(ChatActivity.this.foundWebPage instanceof TL_webPage)) {
                                                    if (!(ChatActivity.this.foundWebPage instanceof TL_webPagePending)) {
                                                        if (ChatActivity.this.foundWebPage != null) {
                                                            ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false);
                                                            ChatActivity.this.foundWebPage = null;
                                                            return;
                                                        }
                                                        return;
                                                    }
                                                }
                                                if (ChatActivity.this.foundWebPage instanceof TL_webPagePending) {
                                                    ChatActivity.this.pendingLinkSearchString = tL_messages_getWebPagePreview.message;
                                                }
                                                if (ChatActivity.this.currentEncryptedChat != null && (ChatActivity.this.foundWebPage instanceof TL_webPagePending)) {
                                                    ChatActivity.this.foundWebPage.url = tL_messages_getWebPagePreview.message;
                                                }
                                                ChatActivity.this.showReplyPanel(true, null, null, ChatActivity.this.foundWebPage, false);
                                            } else if (ChatActivity.this.foundWebPage != null) {
                                                ChatActivity.this.showReplyPanel(false, null, null, ChatActivity.this.foundWebPage, false);
                                                ChatActivity.this.foundWebPage = null;
                                            }
                                        }
                                    });
                                }
                            });
                            ConnectionsManager.getInstance(ChatActivity.this.currentAccount).bindRequestToGuid(ChatActivity.this.linkSearchRequestId, ChatActivity.this.classGuid);
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new C10483());
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                        String toLowerCase = charSequence.toString().toLowerCase();
                        if (charSequence.length() >= 13) {
                            if (toLowerCase.contains("http://") || toLowerCase.contains("https://")) {
                                join = charSequence;
                            }
                        }
                        AndroidUtilities.runOnUIThread(new C10462());
                    }
                }
            });
        }
    }

    private void forwardMessages(ArrayList<MessageObject> arrayList, boolean z) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                if (z) {
                    arrayList = arrayList.iterator();
                    while (arrayList.hasNext()) {
                        SendMessagesHelper.getInstance(this.currentAccount).processForwardFromMyName((MessageObject) arrayList.next(), this.dialog_id);
                    }
                } else {
                    AlertsCreator.showSendMediaAlert(SendMessagesHelper.getInstance(this.currentAccount).sendMessage(arrayList, this.dialog_id), this);
                }
            }
        }
    }

    private void checkBotKeyboard() {
        if (!(this.chatActivityEnterView == null || this.botButtons == null)) {
            if (!this.userBlocked) {
                if (this.botButtons.messageOwner.reply_markup instanceof TL_replyKeyboardForceReply) {
                    SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("answered_");
                    stringBuilder.append(this.dialog_id);
                    if (mainSettings.getInt(stringBuilder.toString(), 0) != this.botButtons.getId() && (this.replyingMessageObject == null || this.chatActivityEnterView.getFieldText() == null)) {
                        this.botReplyButtons = this.botButtons;
                        this.chatActivityEnterView.setButtons(this.botButtons);
                        showReplyPanel(true, this.botButtons, null, null, false);
                    }
                } else {
                    if (this.replyingMessageObject != null && this.botReplyButtons == this.replyingMessageObject) {
                        this.botReplyButtons = null;
                        showReplyPanel(false, null, null, null, false);
                    }
                    this.chatActivityEnterView.setButtons(this.botButtons);
                }
            }
        }
    }

    public void showReplyPanel(boolean z, MessageObject messageObject, ArrayList<MessageObject> arrayList, WebPage webPage, boolean z2) {
        MessageObject messageObject2 = messageObject;
        WebPage webPage2 = webPage;
        if (this.chatActivityEnterView != null) {
            if (z) {
                if (messageObject2 != null || arrayList != null || webPage2 != null) {
                    ArrayList arrayList2;
                    boolean z3;
                    PhotoSize closestPhotoSizeWithSize;
                    int dp;
                    if (r6.searchItem != null && r6.actionBar.isSearchFieldVisible()) {
                        r6.actionBar.closeSearchField(false);
                        r6.chatActivityEnterView.setFieldFocused();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (ChatActivity.this.chatActivityEnterView != null) {
                                    ChatActivity.this.chatActivityEnterView.openKeyboard();
                                }
                            }
                        }, 100);
                    }
                    if (messageObject2 == null || messageObject.getDialogId() == r6.dialog_id) {
                        arrayList2 = arrayList;
                        z3 = false;
                    } else {
                        arrayList2 = new ArrayList();
                        arrayList2.add(messageObject2);
                        z3 = true;
                        messageObject2 = null;
                    }
                    String charSequence;
                    if (messageObject2 != null) {
                        r6.forwardingMessages = null;
                        r6.replyingMessageObject = messageObject2;
                        r6.chatActivityEnterView.setReplyingMessageObject(messageObject2);
                        if (r6.foundWebPage == null) {
                            CharSequence userName;
                            if (messageObject2.isFromUser()) {
                                User user = MessagesController.getInstance(r6.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                                if (user != null) {
                                    userName = UserObject.getUserName(user);
                                } else {
                                    return;
                                }
                            }
                            Chat chat = MessagesController.getInstance(r6.currentAccount).getChat(Integer.valueOf(messageObject2.messageOwner.to_id.channel_id));
                            if (chat != null) {
                                userName = chat.title;
                            } else {
                                return;
                            }
                            r6.replyIconImageView.setImageResource(C0446R.drawable.msg_panel_reply);
                            r6.replyNameTextView.setText(userName);
                            if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                                r6.replyObjectTextView.setText(Emoji.replaceEmoji(messageObject2.messageOwner.media.game.title, r6.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                            } else if (messageObject2.messageText != null) {
                                charSequence = messageObject2.messageText.toString();
                                if (charSequence.length() > 150) {
                                    charSequence = charSequence.substring(0, 150);
                                }
                                r6.replyObjectTextView.setText(Emoji.replaceEmoji(charSequence.replace('\n', ' '), r6.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                            }
                        } else {
                            return;
                        }
                    } else if (arrayList2 == null) {
                        r6.replyIconImageView.setImageResource(C0446R.drawable.msg_panel_link);
                        if (webPage2 instanceof TL_webPagePending) {
                            r6.replyNameTextView.setText(LocaleController.getString("GettingLinkInfo", C0446R.string.GettingLinkInfo));
                            r6.replyObjectTextView.setText(r6.pendingLinkSearchString);
                        } else {
                            if (webPage2.site_name != null) {
                                r6.replyNameTextView.setText(webPage2.site_name);
                            } else if (webPage2.title != null) {
                                r6.replyNameTextView.setText(webPage2.title);
                            } else {
                                r6.replyNameTextView.setText(LocaleController.getString("LinkPreview", C0446R.string.LinkPreview));
                            }
                            if (webPage2.title != null) {
                                r6.replyObjectTextView.setText(webPage2.title);
                            } else if (webPage2.description != null) {
                                r6.replyObjectTextView.setText(webPage2.description);
                            } else if (webPage2.author != null) {
                                r6.replyObjectTextView.setText(webPage2.author);
                            } else {
                                r6.replyObjectTextView.setText(webPage2.display_url);
                            }
                            r6.chatActivityEnterView.setWebPage(webPage2, true);
                        }
                    } else if (!arrayList2.isEmpty()) {
                        r6.replyingMessageObject = null;
                        r6.chatActivityEnterView.setReplyingMessageObject(null);
                        r6.forwardingMessages = arrayList2;
                        if (r6.foundWebPage == null) {
                            int i;
                            r6.chatActivityEnterView.setForceShowSendButton(true, false);
                            ArrayList arrayList3 = new ArrayList();
                            r6.replyIconImageView.setImageResource(C0446R.drawable.msg_panel_forward);
                            MessageObject messageObject3 = (MessageObject) arrayList2.get(0);
                            if (messageObject3.isFromUser()) {
                                arrayList3.add(Integer.valueOf(messageObject3.messageOwner.from_id));
                            } else {
                                arrayList3.add(Integer.valueOf(-messageObject3.messageOwner.to_id.channel_id));
                            }
                            int i2 = ((MessageObject) arrayList2.get(0)).type;
                            for (i = 1; i < arrayList2.size(); i++) {
                                Object valueOf;
                                MessageObject messageObject4 = (MessageObject) arrayList2.get(i);
                                if (messageObject4.isFromUser()) {
                                    valueOf = Integer.valueOf(messageObject4.messageOwner.from_id);
                                } else {
                                    valueOf = Integer.valueOf(-messageObject4.messageOwner.to_id.channel_id);
                                }
                                if (!arrayList3.contains(valueOf)) {
                                    arrayList3.add(valueOf);
                                }
                                if (((MessageObject) arrayList2.get(i)).type != i2) {
                                    i2 = -1;
                                }
                            }
                            CharSequence stringBuilder = new StringBuilder();
                            for (i = 0; i < arrayList3.size(); i++) {
                                User user2;
                                Chat chat2;
                                Integer num = (Integer) arrayList3.get(i);
                                if (num.intValue() > 0) {
                                    user2 = MessagesController.getInstance(r6.currentAccount).getUser(num);
                                    chat2 = null;
                                } else {
                                    chat2 = MessagesController.getInstance(r6.currentAccount).getChat(Integer.valueOf(-num.intValue()));
                                    user2 = null;
                                }
                                if (user2 != null || chat2 != null) {
                                    if (arrayList3.size() != 1) {
                                        if (arrayList3.size() != 2) {
                                            if (stringBuilder.length() != 0) {
                                                stringBuilder.append(" ");
                                                stringBuilder.append(LocaleController.formatPluralString("AndOther", arrayList3.size() - 1));
                                                break;
                                            }
                                        }
                                        if (stringBuilder.length() > 0) {
                                            stringBuilder.append(", ");
                                        }
                                        if (user2 == null) {
                                            stringBuilder.append(chat2.title);
                                        } else if (!TextUtils.isEmpty(user2.first_name)) {
                                            stringBuilder.append(user2.first_name);
                                        } else if (TextUtils.isEmpty(user2.last_name)) {
                                            stringBuilder.append(" ");
                                        } else {
                                            stringBuilder.append(user2.last_name);
                                        }
                                    } else if (user2 != null) {
                                        stringBuilder.append(UserObject.getUserName(user2));
                                    } else {
                                        stringBuilder.append(chat2.title);
                                    }
                                }
                            }
                            r6.replyNameTextView.setText(stringBuilder);
                            if (!(i2 == -1 || i2 == 0 || i2 == 10)) {
                                if (i2 != 11) {
                                    if (i2 == 1) {
                                        r6.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedPhoto", arrayList2.size()));
                                        if (arrayList2.size() == 1) {
                                            messageObject2 = (MessageObject) arrayList2.get(0);
                                        }
                                    } else if (i2 == 4) {
                                        r6.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedLocation", arrayList2.size()));
                                    } else if (i2 == 3) {
                                        r6.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedVideo", arrayList2.size()));
                                        if (arrayList2.size() == 1) {
                                            messageObject2 = (MessageObject) arrayList2.get(0);
                                        }
                                    } else if (i2 == 12) {
                                        r6.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedContact", arrayList2.size()));
                                    } else if (i2 == 2) {
                                        r6.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedAudio", arrayList2.size()));
                                    } else if (i2 == 5) {
                                        r6.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedRound", arrayList2.size()));
                                    } else if (i2 == 14) {
                                        r6.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMusic", arrayList2.size()));
                                    } else if (i2 == 13) {
                                        r6.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedSticker", arrayList2.size()));
                                    } else if (i2 == 8 || i2 == 9) {
                                        if (arrayList2.size() != 1) {
                                            r6.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedFile", arrayList2.size()));
                                        } else if (i2 == 8) {
                                            r6.replyObjectTextView.setText(LocaleController.getString("AttachGif", C0446R.string.AttachGif));
                                        } else {
                                            CharSequence documentFileName = FileLoader.getDocumentFileName(((MessageObject) arrayList2.get(0)).getDocument());
                                            if (documentFileName.length() != 0) {
                                                r6.replyObjectTextView.setText(documentFileName);
                                            }
                                            messageObject2 = (MessageObject) arrayList2.get(0);
                                        }
                                    }
                                }
                            }
                            if (arrayList2.size() != 1 || ((MessageObject) arrayList2.get(0)).messageText == null) {
                                r6.replyObjectTextView.setText(LocaleController.formatPluralString("ForwardedMessageCount", arrayList2.size()));
                            } else {
                                MessageObject messageObject5 = (MessageObject) arrayList2.get(0);
                                if (messageObject5.messageOwner.media instanceof TL_messageMediaGame) {
                                    r6.replyObjectTextView.setText(Emoji.replaceEmoji(messageObject5.messageOwner.media.game.title, r6.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                                } else {
                                    charSequence = messageObject5.messageText.toString();
                                    if (charSequence.length() > 150) {
                                        charSequence = charSequence.substring(0, 150);
                                    }
                                    r6.replyObjectTextView.setText(Emoji.replaceEmoji(charSequence.replace('\n', ' '), r6.replyObjectTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                                }
                            }
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) r6.replyNameTextView.getLayoutParams();
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) r6.replyObjectTextView.getLayoutParams();
                    if (messageObject2 != null) {
                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs2, 80);
                        if (closestPhotoSizeWithSize == null) {
                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 80);
                        }
                    } else {
                        closestPhotoSizeWithSize = null;
                    }
                    if (!(closestPhotoSizeWithSize == null || (closestPhotoSizeWithSize instanceof TL_photoSizeEmpty) || (closestPhotoSizeWithSize.location instanceof TL_fileLocationUnavailable) || messageObject2.type == 13)) {
                        if (messageObject2 == null || !messageObject2.isSecretMedia()) {
                            if (messageObject2.isRoundVideo()) {
                                r6.replyImageView.setRoundRadius(AndroidUtilities.dp(17.0f));
                            } else {
                                r6.replyImageView.setRoundRadius(0);
                            }
                            r6.replyImageLocation = closestPhotoSizeWithSize.location;
                            r6.replyImageView.setImage(r6.replyImageLocation, "50_50", (Drawable) null);
                            r6.replyImageView.setVisibility(0);
                            dp = AndroidUtilities.dp(96.0f);
                            layoutParams2.leftMargin = dp;
                            layoutParams.leftMargin = dp;
                            r6.replyNameTextView.setLayoutParams(layoutParams);
                            r6.replyObjectTextView.setLayoutParams(layoutParams2);
                            r6.chatActivityEnterView.showTopView(false, z3);
                        }
                    }
                    r6.replyImageView.setImageBitmap(null);
                    r6.replyImageLocation = null;
                    r6.replyImageView.setVisibility(4);
                    dp = AndroidUtilities.dp(52.0f);
                    layoutParams2.leftMargin = dp;
                    layoutParams.leftMargin = dp;
                    r6.replyNameTextView.setLayoutParams(layoutParams);
                    r6.replyObjectTextView.setLayoutParams(layoutParams2);
                    r6.chatActivityEnterView.showTopView(false, z3);
                }
            } else if (r6.replyingMessageObject != null || r6.forwardingMessages != null || r6.foundWebPage != null) {
                if (r6.replyingMessageObject != null && (r6.replyingMessageObject.messageOwner.reply_markup instanceof TL_replyKeyboardForceReply)) {
                    Editor edit = MessagesController.getMainSettings(r6.currentAccount).edit();
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("answered_");
                    stringBuilder2.append(r6.dialog_id);
                    edit.putInt(stringBuilder2.toString(), r6.replyingMessageObject.getId()).commit();
                }
                if (r6.foundWebPage != null) {
                    r6.foundWebPage = null;
                    r6.chatActivityEnterView.setWebPage(null, z2 ^ 1);
                    if (!(webPage2 == null || (r6.replyingMessageObject == null && r6.forwardingMessages == null))) {
                        showReplyPanel(true, r6.replyingMessageObject, r6.forwardingMessages, null, false);
                        return;
                    }
                }
                if (r6.forwardingMessages != null) {
                    forwardMessages(r6.forwardingMessages, false);
                }
                r6.chatActivityEnterView.setForceShowSendButton(false, false);
                r6.chatActivityEnterView.hideTopView(false);
                r6.chatActivityEnterView.setReplyingMessageObject(null);
                r6.replyingMessageObject = null;
                r6.forwardingMessages = null;
                r6.replyImageLocation = null;
            }
        }
    }

    private void moveScrollToLastMessage() {
        if (this.chatListView != null && !this.messages.isEmpty()) {
            this.chatLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    private boolean sendSecretMessageRead(MessageObject messageObject) {
        int i = 0;
        if (messageObject != null && !messageObject.isOut() && messageObject.isSecretMedia() && messageObject.messageOwner.destroyTime == 0) {
            if (messageObject.messageOwner.ttl > 0) {
                if (this.currentEncryptedChat != null) {
                    MessagesController.getInstance(this.currentAccount).markMessageAsRead(this.dialog_id, messageObject.messageOwner.random_id, messageObject.messageOwner.ttl);
                } else {
                    MessagesController instance = MessagesController.getInstance(this.currentAccount);
                    int id = messageObject.getId();
                    if (ChatObject.isChannel(this.currentChat)) {
                        i = this.currentChat.id;
                    }
                    instance.markMessageAsRead(id, i, messageObject.messageOwner.ttl);
                }
                messageObject.messageOwner.destroyTime = messageObject.messageOwner.ttl + ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                return true;
            }
        }
        return false;
    }

    private void clearChatData() {
        this.messages.clear();
        this.messagesByDays.clear();
        this.waitingForLoad.clear();
        this.groupedMessagesMap.clear();
        this.progressView.setVisibility(this.chatAdapter.botInfoRow == -1 ? 0 : 4);
        this.chatListView.setEmptyView(null);
        for (int i = 0; i < 2; i++) {
            this.messagesDict[i].clear();
            if (this.currentEncryptedChat == null) {
                this.maxMessageId[i] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                this.minMessageId[i] = Integer.MIN_VALUE;
            } else {
                this.maxMessageId[i] = Integer.MIN_VALUE;
                this.minMessageId[i] = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            this.maxDate[i] = Integer.MIN_VALUE;
            this.minDate[i] = 0;
            this.endReached[i] = false;
            this.cacheEndReached[i] = false;
            this.forwardEndReached[i] = true;
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

    private void scrollToLastMessage(boolean z) {
        if (!this.forwardEndReached[0] || this.first_unread_id != 0 || this.startLoadFromMessageId != 0) {
            clearChatData();
            this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
            MessagesController instance = MessagesController.getInstance(this.currentAccount);
            long j = this.dialog_id;
            int i = this.classGuid;
            boolean isChannel = ChatObject.isChannel(this.currentChat);
            int i2 = this.lastLoadIndex;
            this.lastLoadIndex = i2 + 1;
            instance.loadMessages(j, bot_help, 0, 0, true, 0, i, 0, 0, isChannel, i2);
        } else if (!z || this.chatLayoutManager.findFirstCompletelyVisibleItemPosition()) {
            this.chatLayoutManager.scrollToPositionWithOffset(0, 0);
        } else {
            showPagedownButton(false, true);
            this.highlightMessageId = true;
            updateVisibleRows();
        }
    }

    private void updateTextureViewPosition() {
        if (this.fragmentView != null) {
            boolean z;
            int childCount = this.chatListView.getChildCount();
            int dp = this.chatActivityEnterView.isTopViewVisible() ? AndroidUtilities.dp(48.0f) : 0;
            for (int i = 0; i < childCount; i++) {
                View childAt = this.chatListView.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject messageObject = chatMessageCell.getMessageObject();
                    if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                        ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                        this.roundVideoContainer.setTranslationX(((float) photoImage.getImageX()) + chatMessageCell.getTranslationX());
                        this.roundVideoContainer.setTranslationY((float) (((this.fragmentView.getPaddingTop() + chatMessageCell.getTop()) + photoImage.getImageY()) - dp));
                        this.fragmentView.invalidate();
                        this.roundVideoContainer.invalidate();
                        z = true;
                        break;
                    }
                }
            }
            z = false;
            if (this.roundVideoContainer != null) {
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject != null && playingMessageObject.eventId == 0) {
                    if (z) {
                        MediaController.getInstance().setCurrentRoundVisible(true);
                        scrollToMessageId(playingMessageObject.getId(), 0, false, 0, true);
                    } else {
                        this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                        this.fragmentView.invalidate();
                        if (playingMessageObject != null && playingMessageObject.isRoundVideo()) {
                            if (!this.checkTextureViewPosition) {
                                if (PipRoundVideoView.getInstance() == null) {
                                    scrollToMessageId(playingMessageObject.getId(), 0, false, 0, true);
                                }
                            }
                            MediaController.getInstance().setCurrentRoundVisible(false);
                        }
                    }
                }
            }
        }
    }

    private void updateMessagesVisisblePart() {
        if (this.chatListView != null) {
            View view;
            View view2;
            View view3;
            MessageObject messageObject;
            View view4;
            int childCount = r0.chatListView.getChildCount();
            int dp = r0.chatActivityEnterView.isTopViewVisible() ? AndroidUtilities.dp(48.0f) : 0;
            int measuredHeight = r0.chatListView.getMeasuredHeight();
            int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int i2 = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int i3 = 0;
            Object obj = null;
            View view5 = null;
            View view6 = null;
            View view7 = null;
            int i4 = Integer.MIN_VALUE;
            int i5 = Integer.MIN_VALUE;
            int i6 = ConnectionsManager.DEFAULT_DATACENTER_ID;
            while (i3 < childCount) {
                int i7;
                int i8;
                View childAt = r0.chatListView.getChildAt(i3);
                boolean z = childAt instanceof ChatMessageCell;
                if (z) {
                    int i9;
                    i7 = childCount;
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    view = view7;
                    int top = chatMessageCell.getTop();
                    chatMessageCell.getBottom();
                    if (top >= 0) {
                        view2 = view5;
                        view3 = view6;
                        i9 = 0;
                    } else {
                        view3 = view6;
                        i9 = -top;
                        view2 = view5;
                    }
                    int measuredHeight2 = chatMessageCell.getMeasuredHeight();
                    if (measuredHeight2 > measuredHeight) {
                        measuredHeight2 = i9 + measuredHeight;
                    }
                    chatMessageCell.setVisiblePart(i9, measuredHeight2 - i9);
                    MessageObject messageObject2 = chatMessageCell.getMessageObject();
                    if (r0.roundVideoContainer != null && messageObject2.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject2)) {
                        ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                        i8 = measuredHeight;
                        r0.roundVideoContainer.setTranslationX(((float) photoImage.getImageX()) + chatMessageCell.getTranslationX());
                        r0.roundVideoContainer.setTranslationY((float) (((r0.fragmentView.getPaddingTop() + top) + photoImage.getImageY()) - dp));
                        r0.fragmentView.invalidate();
                        r0.roundVideoContainer.invalidate();
                        obj = 1;
                    } else {
                        i8 = measuredHeight;
                    }
                    messageObject = messageObject2;
                } else {
                    i7 = childCount;
                    i8 = measuredHeight;
                    view2 = view5;
                    view3 = view6;
                    view = view7;
                    messageObject = childAt instanceof ChatActionCell ? ((ChatActionCell) childAt).getMessageObject() : null;
                }
                if (!(messageObject == null || messageObject.isOut() || !messageObject.isUnread())) {
                    measuredHeight = messageObject.getId();
                    if (measuredHeight > 0) {
                        i5 = Math.max(i5, messageObject.getId());
                    }
                    if (measuredHeight < 0) {
                        i6 = Math.min(i6, messageObject.getId());
                    }
                    i4 = Math.max(i4, messageObject.messageOwner.date);
                }
                if (childAt.getBottom() > r0.chatListView.getPaddingTop()) {
                    childCount = childAt.getBottom();
                    if (childCount < i2) {
                        if (!z) {
                            if (!(childAt instanceof ChatActionCell)) {
                                view5 = view2;
                                i2 = childCount;
                                view3 = childAt;
                                view2 = view5;
                            }
                        }
                        view5 = childAt;
                        i2 = childCount;
                        view3 = childAt;
                        view2 = view5;
                    }
                    if ((childAt instanceof ChatActionCell) && ((ChatActionCell) childAt).getMessageObject().isDateObject) {
                        if (childAt.getAlpha() != 1.0f) {
                            childAt.setAlpha(1.0f);
                        }
                        if (childCount < i) {
                            i = childCount;
                            view7 = childAt;
                            view6 = view3;
                            view5 = view2;
                            i3++;
                            childCount = i7;
                            measuredHeight = i8;
                        }
                    }
                }
                view7 = view;
                view6 = view3;
                view5 = view2;
                i3++;
                childCount = i7;
                measuredHeight = i8;
            }
            view2 = view5;
            view3 = view6;
            view = view7;
            if (r0.roundVideoContainer != null) {
                if (obj == null) {
                    r0.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                    r0.fragmentView.invalidate();
                    messageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (messageObject != null && messageObject.isRoundVideo() && messageObject.eventId == 0 && r0.checkTextureViewPosition) {
                        MediaController.getInstance().setCurrentRoundVisible(false);
                    }
                } else {
                    MediaController.getInstance().setCurrentRoundVisible(true);
                }
            }
            if (view2 != null) {
                view4 = view2;
                if (view4 instanceof ChatMessageCell) {
                    messageObject = ((ChatMessageCell) view4).getMessageObject();
                } else {
                    messageObject = ((ChatActionCell) view4).getMessageObject();
                }
                r0.floatingDateView.setCustomDate(messageObject.messageOwner.date);
            }
            r0.currentFloatingDateOnScreen = false;
            view4 = view3;
            boolean z2 = ((view4 instanceof ChatMessageCell) || (view4 instanceof ChatActionCell)) ? false : true;
            r0.currentFloatingTopIsNotMessage = z2;
            if (view != null) {
                view4 = view;
                if (view4.getTop() <= r0.chatListView.getPaddingTop()) {
                    if (!r0.currentFloatingTopIsNotMessage) {
                        if (view4.getAlpha() != 0.0f) {
                            view4.setAlpha(0.0f);
                        }
                        if (r0.floatingDateAnimation != null) {
                            r0.floatingDateAnimation.cancel();
                            r0.floatingDateAnimation = null;
                        }
                        if (r0.floatingDateView.getTag() == null) {
                            r0.floatingDateView.setTag(Integer.valueOf(1));
                        }
                        if (r0.floatingDateView.getAlpha() != 1.0f) {
                            r0.floatingDateView.setAlpha(1.0f);
                        }
                        r0.currentFloatingDateOnScreen = true;
                        dp = view4.getBottom() - r0.chatListView.getPaddingTop();
                        if (dp > r0.floatingDateView.getMeasuredHeight() || dp >= r0.floatingDateView.getMeasuredHeight() * 2) {
                            r0.floatingDateView.setTranslationY(0.0f);
                        } else {
                            r0.floatingDateView.setTranslationY((float) (((-r0.floatingDateView.getMeasuredHeight()) * 2) + dp));
                        }
                    }
                }
                if (view4.getAlpha() != 1.0f) {
                    view4.setAlpha(1.0f);
                }
                hideFloatingDateView(r0.currentFloatingTopIsNotMessage ^ true);
                dp = view4.getBottom() - r0.chatListView.getPaddingTop();
                if (dp > r0.floatingDateView.getMeasuredHeight()) {
                }
                r0.floatingDateView.setTranslationY(0.0f);
            } else {
                hideFloatingDateView(true);
                r0.floatingDateView.setTranslationY(0.0f);
            }
            if (!r0.firstLoading) {
                boolean z3;
                MessagesController instance;
                long j;
                boolean z4;
                if (i5 == Integer.MIN_VALUE) {
                    if (i6 == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        if (!r0.firstUnreadSent) {
                            r0.newUnreadMessageCount = 0;
                            r0.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(r0.newUnreadMessageCount)}));
                            if (r0.pagedownButtonCounter.getVisibility() != 4) {
                                r0.pagedownButtonCounter.setVisibility(4);
                            }
                            if (r0.chatLayoutManager.findFirstVisibleItemPosition() == 0) {
                                MessagesController.getInstance(r0.currentAccount).markDialogAsRead(r0.dialog_id, r0.minMessageId[0], r0.minMessageId[0], r0.maxDate[0], false, 0, true);
                                r0.firstUnreadSent = true;
                            }
                        }
                    }
                }
                int i10 = 0;
                for (dp = 0; dp < r0.messages.size(); dp++) {
                    MessageObject messageObject3 = (MessageObject) r0.messages.get(dp);
                    measuredHeight = messageObject3.getId();
                    if (i5 != Integer.MIN_VALUE && measuredHeight > 0 && measuredHeight <= i5 && messageObject3.isUnread()) {
                        messageObject3.setIsRead();
                        i10++;
                    }
                    if (i6 != ConnectionsManager.DEFAULT_DATACENTER_ID && measuredHeight < 0 && measuredHeight >= i6 && messageObject3.isUnread()) {
                        messageObject3.setIsRead();
                        i10++;
                    }
                }
                if (i5 != r0.minMessageId[0]) {
                    if (i6 != r0.minMessageId[0]) {
                        r0.newUnreadMessageCount -= i10;
                        if (r0.newUnreadMessageCount < 0) {
                            r0.newUnreadMessageCount = 0;
                        }
                        r0.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(r0.newUnreadMessageCount)}));
                        if (r0.newUnreadMessageCount > 0) {
                            if (r0.pagedownButtonCounter.getVisibility() != 4) {
                                r0.pagedownButtonCounter.setVisibility(4);
                            }
                        } else if (r0.pagedownButtonCounter.getVisibility() != 0) {
                            z3 = false;
                            r0.pagedownButtonCounter.setVisibility(0);
                            instance = MessagesController.getInstance(r0.currentAccount);
                            j = r0.dialog_id;
                            if (i5 != r0.minMessageId[z3]) {
                                if (i6 != r0.minMessageId[z3]) {
                                    z4 = z3;
                                    i = i4;
                                    instance.markDialogAsRead(j, i5, i6, i, false, i10, z4);
                                    r0.firstUnreadSent = true;
                                }
                            }
                            i = i4;
                            z4 = true;
                            instance.markDialogAsRead(j, i5, i6, i, false, i10, z4);
                            r0.firstUnreadSent = true;
                        }
                        z3 = false;
                        instance = MessagesController.getInstance(r0.currentAccount);
                        j = r0.dialog_id;
                        if (i5 != r0.minMessageId[z3]) {
                            if (i6 != r0.minMessageId[z3]) {
                                z4 = z3;
                                i = i4;
                                instance.markDialogAsRead(j, i5, i6, i, false, i10, z4);
                                r0.firstUnreadSent = true;
                            }
                        }
                        i = i4;
                        z4 = true;
                        instance.markDialogAsRead(j, i5, i6, i, false, i10, z4);
                        r0.firstUnreadSent = true;
                    }
                }
                r0.newUnreadMessageCount = 0;
                r0.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(r0.newUnreadMessageCount)}));
                if (r0.newUnreadMessageCount > 0) {
                    if (r0.pagedownButtonCounter.getVisibility() != 0) {
                        z3 = false;
                        r0.pagedownButtonCounter.setVisibility(0);
                        instance = MessagesController.getInstance(r0.currentAccount);
                        j = r0.dialog_id;
                        if (i5 != r0.minMessageId[z3]) {
                            if (i6 != r0.minMessageId[z3]) {
                                z4 = z3;
                                i = i4;
                                instance.markDialogAsRead(j, i5, i6, i, false, i10, z4);
                                r0.firstUnreadSent = true;
                            }
                        }
                        i = i4;
                        z4 = true;
                        instance.markDialogAsRead(j, i5, i6, i, false, i10, z4);
                        r0.firstUnreadSent = true;
                    }
                } else if (r0.pagedownButtonCounter.getVisibility() != 4) {
                    r0.pagedownButtonCounter.setVisibility(4);
                }
                z3 = false;
                instance = MessagesController.getInstance(r0.currentAccount);
                j = r0.dialog_id;
                if (i5 != r0.minMessageId[z3]) {
                    if (i6 != r0.minMessageId[z3]) {
                        z4 = z3;
                        i = i4;
                        instance.markDialogAsRead(j, i5, i6, i, false, i10, z4);
                        r0.firstUnreadSent = true;
                    }
                }
                i = i4;
                z4 = true;
                instance.markDialogAsRead(j, i5, i6, i, false, i10, z4);
                r0.firstUnreadSent = true;
            }
        }
    }

    private void toggleMute(boolean z) {
        StringBuilder stringBuilder;
        TL_dialog tL_dialog;
        if (MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id)) {
            z = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            stringBuilder = new StringBuilder();
            stringBuilder.append("notify2_");
            stringBuilder.append(this.dialog_id);
            z.putInt(stringBuilder.toString(), 0);
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(this.dialog_id, 0);
            z.commit();
            tL_dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (tL_dialog != null) {
                tL_dialog.notify_settings = new TL_peerNotifySettings();
            }
            NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(this.dialog_id);
        } else if (z) {
            z = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            stringBuilder = new StringBuilder();
            stringBuilder.append("notify2_");
            stringBuilder.append(this.dialog_id);
            z.putInt(stringBuilder.toString(), 2);
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(this.dialog_id, 1);
            z.commit();
            tL_dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (tL_dialog != null) {
                tL_dialog.notify_settings = new TL_peerNotifySettings();
                tL_dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(this.dialog_id);
            NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(this.dialog_id);
        } else {
            showDialog(AlertsCreator.createMuteAlert(getParentActivity(), this.dialog_id));
        }
    }

    private int getScrollOffsetForMessage(MessageObject messageObject) {
        int measuredHeight;
        GroupedMessages validGroupedMessage = getValidGroupedMessage(messageObject);
        if (validGroupedMessage != null) {
            float f;
            GroupedMessagePosition groupedMessagePosition = (GroupedMessagePosition) validGroupedMessage.positions.get(messageObject);
            float max = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f;
            if (groupedMessagePosition.siblingHeights != null) {
                f = groupedMessagePosition.siblingHeights[0];
            } else {
                f = groupedMessagePosition.ph;
            }
            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
            float f2 = 0.0f;
            float f3 = f2;
            for (int i = 0; i < validGroupedMessage.posArray.size(); i++) {
                GroupedMessagePosition groupedMessagePosition2 = (GroupedMessagePosition) validGroupedMessage.posArray.get(i);
                if (sparseBooleanArray.indexOfKey(groupedMessagePosition2.minY) < 0 && groupedMessagePosition2.siblingHeights == null) {
                    sparseBooleanArray.put(groupedMessagePosition2.minY, true);
                    if (groupedMessagePosition2.minY < groupedMessagePosition.minY) {
                        f3 -= groupedMessagePosition2.ph;
                    } else if (groupedMessagePosition2.minY > groupedMessagePosition.minY) {
                        f3 += groupedMessagePosition2.ph;
                    }
                    f2 += groupedMessagePosition2.ph;
                }
            }
            if (Math.abs(f2 - f) < 0.02f) {
                measuredHeight = ((((int) (((float) this.chatListView.getMeasuredHeight()) - (f2 * max))) / 2) - this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f);
            } else {
                measuredHeight = ((((int) (((float) this.chatListView.getMeasuredHeight()) - ((f + f3) * max))) / 2) - this.chatListView.getPaddingTop()) - AndroidUtilities.dp(7.0f);
            }
        } else {
            measuredHeight = ConnectionsManager.DEFAULT_DATACENTER_ID;
        }
        if (measuredHeight == ConnectionsManager.DEFAULT_DATACENTER_ID) {
            measuredHeight = (this.chatListView.getMeasuredHeight() - messageObject.getApproximateHeight()) / 2;
        }
        return Math.max(0, measuredHeight);
    }

    public void scrollToMessageId(int i, int i2, boolean z, int i3, boolean z2) {
        boolean z3;
        int scrollOffsetForMessage;
        int i4;
        int i5 = i;
        boolean z4 = z;
        int i6 = i3;
        this.wasManualScroll = true;
        MessageObject messageObject = (MessageObject) this.messagesDict[i6].get(i5);
        if (messageObject != null) {
            z3 = false;
            if (r0.messages.indexOf(messageObject) != -1) {
                boolean z5;
                if (z4) {
                    r0.highlightMessageId = i5;
                } else {
                    r0.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                }
                scrollOffsetForMessage = getScrollOffsetForMessage(messageObject);
                if (z2) {
                    if (r0.messages.get(r0.messages.size() - 1) == messageObject) {
                        r0.chatListView.smoothScrollToPosition(r0.chatAdapter.getItemCount() - 1);
                    } else {
                        r0.chatListView.smoothScrollToPosition(r0.chatAdapter.messagesStartRow + r0.messages.indexOf(messageObject));
                    }
                } else if (r0.messages.get(r0.messages.size() - 1) == messageObject) {
                    r0.chatLayoutManager.scrollToPositionWithOffset(r0.chatAdapter.getItemCount() - 1, scrollOffsetForMessage, false);
                } else {
                    r0.chatLayoutManager.scrollToPositionWithOffset(r0.chatAdapter.messagesStartRow + r0.messages.indexOf(messageObject), scrollOffsetForMessage, false);
                }
                updateVisibleRows();
                scrollOffsetForMessage = r0.chatListView.getChildCount();
                i4 = 0;
                while (i4 < scrollOffsetForMessage) {
                    View childAt = r0.chatListView.getChildAt(i4);
                    MessageObject messageObject2;
                    if (childAt instanceof ChatMessageCell) {
                        messageObject2 = ((ChatMessageCell) childAt).getMessageObject();
                        if (messageObject2 != null && messageObject2.getId() == messageObject.getId()) {
                        }
                        i4++;
                    } else {
                        if (childAt instanceof ChatActionCell) {
                            messageObject2 = ((ChatActionCell) childAt).getMessageObject();
                            if (messageObject2 != null && messageObject2.getId() == messageObject.getId()) {
                            }
                        } else {
                            continue;
                        }
                        i4++;
                    }
                    z5 = true;
                    break;
                }
                z5 = false;
                if (!z5) {
                    showPagedownButton(true, true);
                }
                if (z3) {
                    if (r0.currentEncryptedChat != null || MessagesStorage.getInstance(r0.currentAccount).checkMessageId(r0.dialog_id, r0.startLoadFromMessageId)) {
                        r0.waitingForLoad.clear();
                        r0.waitingForReplyMessageLoad = true;
                        r0.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        r0.scrollToMessagePosition = -10000;
                        r0.startLoadFromMessageId = i5;
                        if (i5 == r0.createUnreadMessageAfterId) {
                            r0.createUnreadMessageAfterIdLoading = true;
                        }
                        r0.waitingForLoad.add(Integer.valueOf(r0.lastLoadIndex));
                        MessagesController instance = MessagesController.getInstance(r0.currentAccount);
                        long j = i6 != 0 ? r0.dialog_id : r0.mergeDialogId;
                        scrollOffsetForMessage = AndroidUtilities.isTablet() ? bot_help : 20;
                        i4 = r0.startLoadFromMessageId;
                        int i7 = r0.classGuid;
                        boolean isChannel = ChatObject.isChannel(r0.currentChat);
                        i5 = r0.lastLoadIndex;
                        r0.lastLoadIndex = i5 + 1;
                        instance.loadMessages(j, scrollOffsetForMessage, i4, 0, true, 0, i7, 3, 0, isChannel, i5);
                    } else {
                        return;
                    }
                }
                r0.returnToMessageId = i2;
                r0.returnToLoadIndex = i6;
                r0.needSelectFromMessageId = z4;
            }
        }
        z3 = true;
        if (z3) {
            if (r0.currentEncryptedChat != null) {
            }
            r0.waitingForLoad.clear();
            r0.waitingForReplyMessageLoad = true;
            r0.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
            r0.scrollToMessagePosition = -10000;
            r0.startLoadFromMessageId = i5;
            if (i5 == r0.createUnreadMessageAfterId) {
                r0.createUnreadMessageAfterIdLoading = true;
            }
            r0.waitingForLoad.add(Integer.valueOf(r0.lastLoadIndex));
            MessagesController instance2 = MessagesController.getInstance(r0.currentAccount);
            if (i6 != 0) {
            }
            if (AndroidUtilities.isTablet()) {
            }
            scrollOffsetForMessage = AndroidUtilities.isTablet() ? bot_help : 20;
            i4 = r0.startLoadFromMessageId;
            int i72 = r0.classGuid;
            boolean isChannel2 = ChatObject.isChannel(r0.currentChat);
            i5 = r0.lastLoadIndex;
            r0.lastLoadIndex = i5 + 1;
            instance2.loadMessages(j, scrollOffsetForMessage, i4, 0, true, 0, i72, 3, 0, isChannel2, i5);
        }
        r0.returnToMessageId = i2;
        r0.returnToLoadIndex = i6;
        r0.needSelectFromMessageId = z4;
    }

    private void showPagedownButton(boolean z, boolean z2) {
        if (this.pagedownButton != null) {
            if (z) {
                this.pagedownButtonShowedByScroll = false;
                if (!this.pagedownButton.getTag()) {
                    if (this.pagedownButtonAnimation) {
                        this.pagedownButtonAnimation.cancel();
                        this.pagedownButtonAnimation = null;
                    }
                    if (z2) {
                        if (!this.pagedownButton.getTranslationY()) {
                            this.pagedownButton.setTranslationY((float) AndroidUtilities.dp(100.0f));
                        }
                        this.pagedownButton.setVisibility(0);
                        this.pagedownButton.setTag(Integer.valueOf(1));
                        this.pagedownButtonAnimation = new AnimatorSet();
                        if (this.mentiondownButton.getVisibility()) {
                            z = this.pagedownButtonAnimation;
                            z2 = new Animator[1];
                            z2[0] = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[]{0.0f});
                            z.playTogether(z2);
                        } else {
                            z = this.pagedownButtonAnimation;
                            z2 = new Animator[2];
                            z2[0] = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[]{0.0f});
                            z2[1] = ObjectAnimator.ofFloat(this.mentiondownButton, "translationY", new float[]{(float) (-AndroidUtilities.dp(72.0f))});
                            z.playTogether(z2);
                        }
                        this.pagedownButtonAnimation.setDuration(200);
                        this.pagedownButtonAnimation.start();
                    } else {
                        this.pagedownButton.setVisibility(0);
                    }
                }
            } else {
                this.returnToMessageId = 0;
                this.newUnreadMessageCount = 0;
                if (this.pagedownButton.getTag()) {
                    this.pagedownButton.setTag(null);
                    if (this.pagedownButtonAnimation) {
                        this.pagedownButtonAnimation.cancel();
                        this.pagedownButtonAnimation = null;
                    }
                    if (z2) {
                        this.pagedownButtonAnimation = new AnimatorSet();
                        if (this.mentiondownButton.getVisibility()) {
                            z = this.pagedownButtonAnimation;
                            z2 = new Animator[1];
                            z2[0] = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[]{(float) AndroidUtilities.dp(100.0f)});
                            z.playTogether(z2);
                        } else {
                            z = this.pagedownButtonAnimation;
                            z2 = new Animator[2];
                            z2[0] = ObjectAnimator.ofFloat(this.pagedownButton, "translationY", new float[]{(float) AndroidUtilities.dp(100.0f)});
                            z2[1] = ObjectAnimator.ofFloat(this.mentiondownButton, "translationY", new float[]{0.0f});
                            z.playTogether(z2);
                        }
                        this.pagedownButtonAnimation.setDuration(200);
                        this.pagedownButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                ChatActivity.this.pagedownButtonCounter.setVisibility(4);
                                ChatActivity.this.pagedownButton.setVisibility(4);
                            }
                        });
                        this.pagedownButtonAnimation.start();
                    } else {
                        this.pagedownButton.setVisibility(true);
                    }
                }
            }
        }
    }

    private void showMentiondownButton(boolean z, boolean z2) {
        if (this.mentiondownButton != null) {
            if (!z) {
                this.returnToMessageId = 0;
                if (this.mentiondownButton.getTag()) {
                    this.mentiondownButton.setTag(null);
                    if (this.mentiondownButtonAnimation) {
                        this.mentiondownButtonAnimation.cancel();
                        this.mentiondownButtonAnimation = null;
                    }
                    if (z2) {
                        if (this.pagedownButton.getVisibility()) {
                            this.mentiondownButtonAnimation = ObjectAnimator.ofFloat(this.mentiondownButton, "translationY", new float[]{(float) AndroidUtilities.dp(100.0f)}).setDuration(200);
                        } else {
                            this.mentiondownButtonAnimation = ObjectAnimator.ofFloat(this.mentiondownButton, "alpha", new float[]{1.0f, 0.0f}).setDuration(200);
                        }
                        this.mentiondownButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                ChatActivity.this.mentiondownButtonCounter.setVisibility(4);
                                ChatActivity.this.mentiondownButton.setVisibility(4);
                            }
                        });
                        this.mentiondownButtonAnimation.start();
                    } else {
                        this.mentiondownButton.setVisibility(true);
                    }
                }
            } else if (!this.mentiondownButton.getTag()) {
                if (this.mentiondownButtonAnimation) {
                    this.mentiondownButtonAnimation.cancel();
                    this.mentiondownButtonAnimation = null;
                }
                if (z2) {
                    this.mentiondownButton.setVisibility(0);
                    this.mentiondownButton.setTag(Integer.valueOf(1));
                    if (this.pagedownButton.getVisibility()) {
                        if (!this.mentiondownButton.getTranslationY()) {
                            this.mentiondownButton.setTranslationY((float) AndroidUtilities.dp(100.0f));
                        }
                        this.mentiondownButtonAnimation = ObjectAnimator.ofFloat(this.mentiondownButton, "translationY", new float[]{0.0f}).setDuration(200);
                    } else {
                        this.mentiondownButton.setTranslationY((float) (-AndroidUtilities.dp(true)));
                        this.mentiondownButtonAnimation = ObjectAnimator.ofFloat(this.mentiondownButton, "alpha", new float[]{0.0f, 1.0f}).setDuration(200);
                    }
                    this.mentiondownButtonAnimation.start();
                } else {
                    this.mentiondownButton.setVisibility(0);
                }
            }
        }
    }

    private void updateSecretStatus() {
        if (this.bottomOverlay != null) {
            int i = 1;
            if (ChatObject.isChannel(this.currentChat) && this.currentChat.banned_rights != null && this.currentChat.banned_rights.send_messages) {
                if (AndroidUtilities.isBannedForever(this.currentChat.banned_rights.until_date)) {
                    this.bottomOverlayText.setText(LocaleController.getString("SendMessageRestrictedForever", C0446R.string.SendMessageRestrictedForever));
                } else {
                    this.bottomOverlayText.setText(LocaleController.formatString("SendMessageRestricted", C0446R.string.SendMessageRestricted, LocaleController.formatDateForBan((long) this.currentChat.banned_rights.until_date)));
                }
                this.bottomOverlay.setVisibility(0);
                if (this.mentionListAnimation != null) {
                    this.mentionListAnimation.cancel();
                    this.mentionListAnimation = null;
                }
                this.mentionContainer.setVisibility(8);
                this.mentionContainer.setTag(null);
            } else {
                if (this.currentEncryptedChat != null) {
                    if (this.bigEmptyView != null) {
                        if (this.currentEncryptedChat instanceof TL_encryptedChatRequested) {
                            this.bottomOverlayText.setText(LocaleController.getString("EncryptionProcessing", C0446R.string.EncryptionProcessing));
                            this.bottomOverlay.setVisibility(0);
                        } else if (this.currentEncryptedChat instanceof TL_encryptedChatWaiting) {
                            TextView textView = this.bottomOverlayText;
                            Object[] objArr = new Object[1];
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("<b>");
                            stringBuilder.append(this.currentUser.first_name);
                            stringBuilder.append("</b>");
                            objArr[0] = stringBuilder.toString();
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AwaitingEncryption", C0446R.string.AwaitingEncryption, objArr)));
                            this.bottomOverlay.setVisibility(0);
                        } else if (this.currentEncryptedChat instanceof TL_encryptedChatDiscarded) {
                            this.bottomOverlayText.setText(LocaleController.getString("EncryptionRejected", C0446R.string.EncryptionRejected));
                            this.bottomOverlay.setVisibility(0);
                            this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                            DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, false);
                        } else {
                            if (this.currentEncryptedChat instanceof TL_encryptedChat) {
                                this.bottomOverlay.setVisibility(4);
                            }
                            i = 0;
                        }
                        checkRaiseSensors();
                        checkActionBarMenu();
                    }
                }
                this.bottomOverlay.setVisibility(4);
                return;
            }
            if (i != 0) {
                this.chatActivityEnterView.hidePopup(false);
                if (getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (this.mentionsAdapter != null) {
            this.mentionsAdapter.onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (i == 17 && this.chatAttachAlert != null) {
            this.chatAttachAlert.checkCamera(false);
        } else if (i == 21) {
            if (!(getParentActivity() == 0 || iArr == null || iArr.length == 0 || iArr[0] == 0)) {
                i = new Builder(getParentActivity());
                i.setTitle(LocaleController.getString("AppName", NUM));
                i.setMessage(LocaleController.getString("PermissionNoAudioVideo", NUM));
                i.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new OnClickListener() {
                    @TargetApi(9)
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            dialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            i = new StringBuilder();
                            i.append("package:");
                            i.append(ApplicationLoader.applicationContext.getPackageName());
                            dialogInterface.setData(Uri.parse(i.toString()));
                            ChatActivity.this.getParentActivity().startActivity(dialogInterface);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
                i.setPositiveButton(LocaleController.getString("OK", NUM), null);
                i.show();
            }
        } else if (i == 19 && iArr != null && iArr.length > null && iArr[0] == null) {
            processSelectedAttach(0);
        } else if (i == 20 && iArr != null && iArr.length > null && iArr[0] == null) {
            processSelectedAttach(2);
        } else if (i == 101 && this.currentUser != 0) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                VoIPHelper.permissionDenied(getParentActivity(), null);
            } else {
                VoIPHelper.startCall(this.currentUser, getParentActivity(), MessagesController.getInstance(this.currentAccount).getUserFull(this.currentUser.id));
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
        ChatActivity chatActivity = this;
        MessageObject messageObject2 = messageObject;
        if (messageObject2 == null) {
            return -1;
        }
        int i = 0;
        InputStickerSet inputStickerSet;
        String str;
        if (chatActivity.currentEncryptedChat == null) {
            int i2 = (chatActivity.isBroadcast && messageObject.getId() <= 0 && messageObject.isSendError()) ? 1 : 0;
            if ((chatActivity.isBroadcast || messageObject.getId() > 0 || !messageObject.isOut()) && i2 == 0) {
                if (messageObject2.type == 6) {
                    return -1;
                }
                if (!(messageObject2.type == 10 || messageObject2.type == 11)) {
                    if (messageObject2.type != 16) {
                        if (messageObject.isVoice()) {
                            return 2;
                        }
                        if (messageObject.isSticker()) {
                            inputStickerSet = messageObject.getInputStickerSet();
                            if (inputStickerSet instanceof TL_inputStickerSetID) {
                                if (!DataQuery.getInstance(chatActivity.currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                                    return 7;
                                }
                            } else if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !DataQuery.getInstance(chatActivity.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                                return 7;
                            }
                            return 9;
                        }
                        if (!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) {
                            if (!((messageObject2.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic())) {
                                if (messageObject.isVideo()) {
                                }
                            }
                            if (!TextUtils.isEmpty(messageObject2.messageOwner.attachPath) && new File(messageObject2.messageOwner.attachPath).exists()) {
                                i = 1;
                            }
                            if (i == 0 && FileLoader.getPathToMessage(messageObject2.messageOwner).exists()) {
                                i = 1;
                            }
                            if (i != 0) {
                                if (messageObject.getDocument() != null) {
                                    str = messageObject.getDocument().mime_type;
                                    if (str != null) {
                                        if (messageObject.getDocumentName().toLowerCase().endsWith("attheme")) {
                                            return 10;
                                        }
                                        if (str.endsWith("/xml")) {
                                            return 5;
                                        }
                                        if (str.endsWith("/png") || str.endsWith("/jpg") || str.endsWith("/jpeg")) {
                                            return 6;
                                        }
                                    }
                                }
                                return 4;
                            }
                        }
                        if (messageObject2.type == 12) {
                            return 8;
                        }
                        return messageObject.isMediaEmpty() ? 3 : 2;
                    }
                }
                return messageObject.getId() == 0 ? -1 : 1;
            } else if (messageObject.isSendError()) {
                return !messageObject.isMediaEmpty() ? 0 : 20;
            } else {
                return -1;
            }
        } else if (messageObject.isSending() || messageObject2.type == 6) {
            return -1;
        } else {
            if (messageObject.isSendError()) {
                return !messageObject.isMediaEmpty() ? 0 : 20;
            } else {
                if (messageObject2.type != 10) {
                    if (messageObject2.type != 11) {
                        if (messageObject.isVoice()) {
                            return 2;
                        }
                        if (messageObject.isSticker()) {
                            inputStickerSet = messageObject.getInputStickerSet();
                            return (!(inputStickerSet instanceof TL_inputStickerSetShortName) || DataQuery.getInstance(chatActivity.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) ? 2 : 7;
                        } else if (!messageObject.isRoundVideo() && ((messageObject2.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
                            if (!TextUtils.isEmpty(messageObject2.messageOwner.attachPath) && new File(messageObject2.messageOwner.attachPath).exists()) {
                                i = 1;
                            }
                            if (i == 0 && FileLoader.getPathToMessage(messageObject2.messageOwner).exists()) {
                                i = 1;
                            }
                            if (i != 0) {
                                if (messageObject.getDocument() != null) {
                                    str = messageObject.getDocument().mime_type;
                                    if (str != null && str.endsWith("text/xml")) {
                                        return 5;
                                    }
                                }
                                if (messageObject2.messageOwner.ttl <= 0) {
                                    return 4;
                                }
                            }
                        } else if (messageObject2.type == 12) {
                            return 8;
                        } else {
                            if (messageObject.isMediaEmpty()) {
                                return 3;
                            }
                        }
                    }
                }
                if (messageObject.getId() != 0) {
                    if (!messageObject.isSending()) {
                        return 1;
                    }
                }
                return -1;
            }
        }
    }

    private void addToSelectedMessages(MessageObject messageObject, boolean z) {
        addToSelectedMessages(messageObject, z, true);
    }

    private void addToSelectedMessages(MessageObject messageObject, boolean z, boolean z2) {
        if (messageObject != null) {
            int i = messageObject.getDialogId() == this.dialog_id ? 0 : 1;
            if (z && messageObject.getGroupId() != 0) {
                GroupedMessages groupedMessages = (GroupedMessages) this.groupedMessagesMap.get(messageObject.getGroupId());
                if (groupedMessages != null) {
                    z = false;
                    z2 = z;
                    boolean z3 = z2;
                    while (z < groupedMessages.messages.size()) {
                        if (this.selectedMessagesIds[i].indexOfKey(((MessageObject) groupedMessages.messages.get(z)).getId()) < 0) {
                            z3 = z;
                            z2 = true;
                        }
                        z++;
                    }
                    z = false;
                    while (z < groupedMessages.messages.size()) {
                        MessageObject messageObject2 = (MessageObject) groupedMessages.messages.get(z);
                        if (!z2) {
                            addToSelectedMessages(messageObject2, false, z == groupedMessages.messages.size() - 1);
                        } else if (this.selectedMessagesIds[i].indexOfKey(messageObject2.getId()) < 0) {
                            addToSelectedMessages(messageObject2, false, z == z3);
                        }
                        z++;
                    }
                }
                return;
            } else if (this.selectedMessagesIds[i].indexOfKey(messageObject.getId()) < false) {
                this.selectedMessagesIds[i].remove(messageObject.getId());
                if (!messageObject.type || messageObject.caption) {
                    this.selectedMessagesCanCopyIds[i].remove(messageObject.getId());
                }
                if (messageObject.isSticker()) {
                    this.selectedMessagesCanStarIds[i].remove(messageObject.getId());
                }
                if (messageObject.canEditMessage(this.currentChat) && messageObject.getGroupId() != 0) {
                    r14 = (GroupedMessages) this.groupedMessagesMap.get(messageObject.getGroupId());
                    if (r14 != null && r14.messages.size() > true) {
                        this.canEditMessagesCount -= true;
                    }
                }
                if (messageObject.canDeleteMessage(this.currentChat) == null) {
                    this.cantDeleteMessagesCount -= 1;
                }
            } else if (this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size() < true) {
                this.selectedMessagesIds[i].put(messageObject.getId(), messageObject);
                if (!messageObject.type || messageObject.caption) {
                    this.selectedMessagesCanCopyIds[i].put(messageObject.getId(), messageObject);
                }
                if (messageObject.isSticker()) {
                    this.selectedMessagesCanStarIds[i].put(messageObject.getId(), messageObject);
                }
                if (messageObject.canEditMessage(this.currentChat) && messageObject.getGroupId() != 0) {
                    r14 = (GroupedMessages) this.groupedMessagesMap.get(messageObject.getGroupId());
                    if (r14 != null && r14.messages.size() > true) {
                        this.canEditMessagesCount += true;
                    }
                }
                if (messageObject.canDeleteMessage(this.currentChat) == null) {
                    this.cantDeleteMessagesCount += 1;
                }
            } else {
                return;
            }
        }
        if (z2 && this.actionBar.isActionModeShowed() != null) {
            messageObject = this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size();
            if (messageObject == null) {
                this.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                this.startReplyOnTextChange = false;
            } else {
                int i2;
                z = this.actionBar.createActionMode().getItem(true);
                z2 = this.actionBar.createActionMode().getItem(22);
                ActionBarMenuItem item = this.actionBar.createActionMode().getItem(edit);
                final ActionBarMenuItem item2 = this.actionBar.createActionMode().getItem(19);
                int visibility = z.getVisibility();
                boolean visibility2 = z2.getVisibility();
                boolean z4 = true;
                z.setVisibility(this.selectedMessagesCanCopyIds[0].size() + this.selectedMessagesCanCopyIds[1].size() != 0 ? 0 : 8);
                messageObject = (DataQuery.getInstance(this.currentAccount).canAddStickerToFavorites() && this.selectedMessagesCanStarIds[0].size() + this.selectedMessagesCanStarIds[1].size() == messageObject) ? null : 8;
                z2.setVisibility(messageObject);
                messageObject = z.getVisibility();
                z = z2.getVisibility();
                this.actionBar.createActionMode().getItem(12).setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
                if (item != null) {
                    i2 = (this.canEditMessagesCount == 1 && this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size() == 1) ? 0 : 8;
                    item.setVisibility(i2);
                }
                this.hasUnfavedSelected = false;
                for (i2 = 0; i2 < 2; i2++) {
                    for (int i3 = 0; i3 < this.selectedMessagesCanStarIds[i2].size(); i3++) {
                        if (!DataQuery.getInstance(this.currentAccount).isStickerInFavorites(((MessageObject) this.selectedMessagesCanStarIds[i2].valueAt(i3)).getDocument())) {
                            this.hasUnfavedSelected = true;
                            break;
                        }
                    }
                    if (this.hasUnfavedSelected) {
                        break;
                    }
                }
                z2.setIcon(this.hasUnfavedSelected ? C0446R.drawable.ic_ab_fave : C0446R.drawable.ic_ab_unfave);
                if (item2 != null) {
                    AnimatorSet animatorSet;
                    if ((!this.currentEncryptedChat || AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) >= true) && !this.isBroadcast && (!this.bottomOverlayChat || this.bottomOverlayChat.getVisibility())) {
                        if (this.currentChat) {
                            if (!ChatObject.isNotInChat(this.currentChat) && (!ChatObject.isChannel(this.currentChat) || ChatObject.canPost(this.currentChat) || this.currentChat.megagroup)) {
                                if (!ChatObject.canSendMessages(this.currentChat)) {
                                }
                            }
                        }
                        z2 = true;
                        if (z2 && this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size()) {
                            z4 = false;
                        }
                        z2 = z4 && !this.chatActivityEnterView.hasText();
                        this.startReplyOnTextChange = z2;
                        if (item2.getVisibility() != z4) {
                            if (this.replyButtonAnimation) {
                                this.replyButtonAnimation.cancel();
                            }
                            if (visibility == messageObject) {
                                if (visibility2 != z) {
                                    this.replyButtonAnimation = new AnimatorSet();
                                    item2.setPivotX((float) AndroidUtilities.dp(54.0f));
                                    item.setPivotX((float) AndroidUtilities.dp(54.0f));
                                    if (z4) {
                                        item2.setVisibility(z4);
                                        z2 = this.replyButtonAnimation;
                                        z = new Animator[4];
                                        z[0] = ObjectAnimator.ofFloat(item2, "alpha", new float[]{1.0f});
                                        z[1] = ObjectAnimator.ofFloat(item2, "scaleX", new float[]{1.0f});
                                        z[2] = ObjectAnimator.ofFloat(item, "alpha", new float[]{1.0f});
                                        z[3] = ObjectAnimator.ofFloat(item, "scaleX", new float[]{1.0f});
                                        z2.playTogether(z);
                                    } else {
                                        animatorSet = this.replyButtonAnimation;
                                        z = new Animator[4];
                                        z[0] = ObjectAnimator.ofFloat(item2, "alpha", new float[]{0.0f});
                                        z[1] = ObjectAnimator.ofFloat(item2, "scaleX", new float[]{0.0f});
                                        z[2] = ObjectAnimator.ofFloat(item, "alpha", new float[]{0.0f});
                                        z[3] = ObjectAnimator.ofFloat(item, "scaleX", new float[]{0.0f});
                                        animatorSet.playTogether(z);
                                    }
                                    this.replyButtonAnimation.setDuration(true);
                                    this.replyButtonAnimation.addListener(new AnimatorListenerAdapter() {
                                        public void onAnimationEnd(Animator animator) {
                                            if (ChatActivity.this.replyButtonAnimation != null && ChatActivity.this.replyButtonAnimation.equals(animator) != null && z4 == 8) {
                                                item2.setVisibility(8);
                                            }
                                        }

                                        public void onAnimationCancel(Animator animator) {
                                            if (ChatActivity.this.replyButtonAnimation != null && ChatActivity.this.replyButtonAnimation.equals(animator) != null) {
                                                ChatActivity.this.replyButtonAnimation = null;
                                            }
                                        }
                                    });
                                    this.replyButtonAnimation.start();
                                }
                            }
                            if (z4) {
                                item2.setAlpha(1.0f);
                                item2.setScaleX(1.0f);
                            } else {
                                item2.setAlpha(0.0f);
                                item2.setScaleX(0.0f);
                            }
                            item2.setVisibility(z4);
                        }
                    }
                    z2 = false;
                    z4 = false;
                    if (z4) {
                    }
                    this.startReplyOnTextChange = z2;
                    if (item2.getVisibility() != z4) {
                        if (this.replyButtonAnimation) {
                            this.replyButtonAnimation.cancel();
                        }
                        if (visibility == messageObject) {
                            if (visibility2 != z) {
                                this.replyButtonAnimation = new AnimatorSet();
                                item2.setPivotX((float) AndroidUtilities.dp(54.0f));
                                item.setPivotX((float) AndroidUtilities.dp(54.0f));
                                if (z4) {
                                    animatorSet = this.replyButtonAnimation;
                                    z = new Animator[4];
                                    z[0] = ObjectAnimator.ofFloat(item2, "alpha", new float[]{0.0f});
                                    z[1] = ObjectAnimator.ofFloat(item2, "scaleX", new float[]{0.0f});
                                    z[2] = ObjectAnimator.ofFloat(item, "alpha", new float[]{0.0f});
                                    z[3] = ObjectAnimator.ofFloat(item, "scaleX", new float[]{0.0f});
                                    animatorSet.playTogether(z);
                                } else {
                                    item2.setVisibility(z4);
                                    z2 = this.replyButtonAnimation;
                                    z = new Animator[4];
                                    z[0] = ObjectAnimator.ofFloat(item2, "alpha", new float[]{1.0f});
                                    z[1] = ObjectAnimator.ofFloat(item2, "scaleX", new float[]{1.0f});
                                    z[2] = ObjectAnimator.ofFloat(item, "alpha", new float[]{1.0f});
                                    z[3] = ObjectAnimator.ofFloat(item, "scaleX", new float[]{1.0f});
                                    z2.playTogether(z);
                                }
                                this.replyButtonAnimation.setDuration(true);
                                this.replyButtonAnimation.addListener(/* anonymous class already generated */);
                                this.replyButtonAnimation.start();
                            }
                        }
                        if (z4) {
                            item2.setAlpha(0.0f);
                            item2.setScaleX(0.0f);
                        } else {
                            item2.setAlpha(1.0f);
                            item2.setScaleX(1.0f);
                        }
                        item2.setVisibility(z4);
                    }
                }
            }
        }
    }

    private void processRowSelect(View view, boolean z) {
        view = view instanceof ChatMessageCell ? ((ChatMessageCell) view).getMessageObject() : view instanceof ChatActionCell ? ((ChatActionCell) view).getMessageObject() : null;
        int messageType = getMessageType(view);
        if (messageType >= 2) {
            if (messageType != 20) {
                addToSelectedMessages(view, z);
                updateActionModeTitle();
                updateVisibleRows();
            }
        }
    }

    private void updateActionModeTitle() {
        if (this.actionBar.isActionModeShowed()) {
            if (!(this.selectedMessagesIds[0].size() == 0 && this.selectedMessagesIds[1].size() == 0)) {
                this.selectedMessagesCountTextView.setNumber(this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size(), true);
            }
        }
    }

    private void updateTitle() {
        if (this.avatarContainer != null) {
            if (this.currentChat != null) {
                this.avatarContainer.setTitle(this.currentChat.title);
            } else if (this.currentUser != null) {
                if (this.currentUser.self) {
                    this.avatarContainer.setTitle(LocaleController.getString("SavedMessages", C0446R.string.SavedMessages));
                } else if (MessagesController.isSupportId(this.currentUser.id) || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.id)) != null || (ContactsController.getInstance(this.currentAccount).contactsDict.size() == 0 && ContactsController.getInstance(this.currentAccount).isLoadingContacts())) {
                    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
                } else if (TextUtils.isEmpty(this.currentUser.phone)) {
                    this.avatarContainer.setTitle(UserObject.getUserName(this.currentUser));
                } else {
                    ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
                    PhoneFormat instance = PhoneFormat.getInstance();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(this.currentUser.phone);
                    chatAvatarContainer.setTitle(instance.format(stringBuilder.toString()));
                }
            }
        }
    }

    private void updateBotButtons() {
        if (!(this.headerItem == null || this.currentUser == null || this.currentEncryptedChat != null)) {
            if (this.currentUser.bot) {
                int i;
                int i2 = 0;
                if (this.botInfo.size() != 0) {
                    int i3 = 0;
                    int i4 = i3;
                    i = i4;
                    while (i3 < this.botInfo.size()) {
                        BotInfo botInfo = (BotInfo) this.botInfo.valueAt(i3);
                        int i5 = i4;
                        for (i4 = 0; i4 < botInfo.commands.size(); i4++) {
                            TL_botCommand tL_botCommand = (TL_botCommand) botInfo.commands.get(i4);
                            if (tL_botCommand.command.toLowerCase().equals("help")) {
                                i5 = 1;
                            } else if (tL_botCommand.command.toLowerCase().equals("settings")) {
                                i = 1;
                            }
                            if (i != 0 && r5 != 0) {
                                break;
                            }
                        }
                        i4 = i5;
                        i3++;
                    }
                    i2 = i4;
                } else {
                    i = 0;
                }
                if (i2 != 0) {
                    this.headerItem.showSubItem(bot_help);
                } else {
                    this.headerItem.hideSubItem(bot_help);
                }
                if (i != 0) {
                    this.headerItem.showSubItem(bot_settings);
                } else {
                    this.headerItem.hideSubItem(bot_settings);
                }
            }
        }
    }

    private void updateTitleIcons() {
        if (this.avatarContainer != null) {
            Drawable drawable = null;
            Drawable drawable2 = MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.chat_muteIconDrawable : null;
            ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
            if (this.currentEncryptedChat != null) {
                drawable = Theme.chat_lockIconDrawable;
            }
            chatAvatarContainer.setTitleIcons(drawable, drawable2);
            if (this.muteItem != null) {
                if (drawable2 != null) {
                    this.muteItem.setText(LocaleController.getString("UnmuteNotifications", C0446R.string.UnmuteNotifications));
                } else {
                    this.muteItem.setText(LocaleController.getString("MuteNotifications", C0446R.string.MuteNotifications));
                }
            }
        }
    }

    private void checkAndUpdateAvatar() {
        if (this.currentUser != null) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
            if (user != null) {
                this.currentUser = user;
            } else {
                return;
            }
        } else if (this.currentChat != null) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChat.id));
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

    public void openVideoEditor(String str, String str2) {
        ChatActivity chatActivity = this;
        if (getParentActivity() != null) {
            String str3 = str;
            final Bitmap createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(str3, 1);
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            final ArrayList arrayList = new ArrayList();
            PhotoEntry photoEntry = new PhotoEntry(0, 0, 0, str3, 0, true);
            photoEntry.caption = str2;
            arrayList.add(photoEntry);
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 2, new EmptyPhotoViewerProvider() {
                public boolean canScrollAway() {
                    return false;
                }

                public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
                    return new BitmapHolder(createVideoThumbnail, 0);
                }

                public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo) {
                    ChatActivity.this.sendMedia((PhotoEntry) arrayList.get(0), videoEditedInfo);
                }
            }, chatActivity);
            return;
        }
        SendMessagesHelper.prepareSendingVideo(str, 0, 0, 0, 0, null, chatActivity.dialog_id, chatActivity.replyingMessageObject, null, null, 0);
        showReplyPanel(false, null, null, null, false);
        DataQuery.getInstance(chatActivity.currentAccount).cleanDraft(chatActivity.dialog_id, true);
    }

    private void showAttachmentError() {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("UnsupportedAttachment", C0446R.string.UnsupportedAttachment), 0).show();
        }
    }

    private void sendUriAsDocument(Uri uri) {
        if (uri != null) {
            String copyFileToCache;
            String str;
            String uri2 = uri.toString();
            if (uri2.contains("com.google.android.apps.photos.contentprovider")) {
                try {
                    uri2 = uri2.split("/1/")[1];
                    int indexOf = uri2.indexOf("/ACTUAL");
                    if (indexOf != -1) {
                        uri = Uri.parse(URLDecoder.decode(uri2.substring(0, indexOf), C0542C.UTF8_NAME));
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
            uri2 = AndroidUtilities.getPath(uri);
            if (uri2 == null) {
                uri2 = uri.toString();
                copyFileToCache = MediaController.copyFileToCache(uri, "file");
                str = uri2;
            } else {
                copyFileToCache = uri2;
                str = copyFileToCache;
            }
            if (copyFileToCache == null) {
                showAttachmentError();
            } else {
                SendMessagesHelper.prepareSendingDocument(copyFileToCache, str, null, null, this.dialog_id, this.replyingMessageObject, null);
            }
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        Throwable th;
        ChatActivity chatActivity = this;
        int i3 = i;
        Intent intent2 = intent;
        if (i2 == -1) {
            if (i3 != 0) {
                if (i3 != 2) {
                    Uri data;
                    String path;
                    if (i3 == 1) {
                        if (intent2 != null) {
                            if (intent.getData() != null) {
                                data = intent.getData();
                                if (data.toString().contains(MimeTypes.BASE_TYPE_VIDEO)) {
                                    try {
                                        path = AndroidUtilities.getPath(data);
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                        path = null;
                                    }
                                    if (path == null) {
                                        showAttachmentError();
                                    }
                                    if (chatActivity.paused) {
                                        chatActivity.startVideoEdit = path;
                                    } else {
                                        openVideoEditor(path, null);
                                    }
                                } else {
                                    SendMessagesHelper.prepareSendingPhoto(null, data, chatActivity.dialog_id, chatActivity.replyingMessageObject, null, null, null, null, 0);
                                }
                                showReplyPanel(false, null, null, null, false);
                                DataQuery.getInstance(chatActivity.currentAccount).cleanDraft(chatActivity.dialog_id, true);
                            }
                        }
                        showAttachmentError();
                        return;
                    }
                    int i4 = 0;
                    if (i3 == 21) {
                        if (intent2 == null) {
                            showAttachmentError();
                            return;
                        }
                        if (intent.getData() != null) {
                            sendUriAsDocument(intent.getData());
                        } else if (intent.getClipData() != null) {
                            ClipData clipData = intent.getClipData();
                            while (i4 < clipData.getItemCount()) {
                                sendUriAsDocument(clipData.getItemAt(i4).getUri());
                                i4++;
                            }
                        } else {
                            showAttachmentError();
                        }
                        showReplyPanel(false, null, null, null, false);
                        DataQuery.getInstance(chatActivity.currentAccount).cleanDraft(chatActivity.dialog_id, true);
                    } else if (i3 == bot_settings) {
                        if (intent2 != null) {
                            if (intent.getData() != null) {
                                data = intent.getData();
                                Cursor query;
                                try {
                                    query = getParentActivity().getContentResolver().query(data, new String[]{"display_name", "data1"}, null, null, null);
                                    if (query != null) {
                                        i3 = 0;
                                        while (query.moveToNext()) {
                                            try {
                                                path = query.getString(0);
                                                String string = query.getString(1);
                                                User tL_user = new TL_user();
                                                tL_user.first_name = path;
                                                tL_user.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                                tL_user.phone = string;
                                                SendMessagesHelper.getInstance(chatActivity.currentAccount).sendMessage(tL_user, chatActivity.dialog_id, chatActivity.replyingMessageObject, null, null);
                                                boolean z = true;
                                            } catch (Throwable e2) {
                                                th = e2;
                                            }
                                        }
                                        if (i3 != 0) {
                                            showReplyPanel(false, null, null, null, false);
                                            DataQuery.getInstance(chatActivity.currentAccount).cleanDraft(chatActivity.dialog_id, true);
                                        }
                                    }
                                    if (query != null) {
                                        try {
                                            if (!query.isClosed()) {
                                                query.close();
                                            }
                                        } catch (Throwable e22) {
                                            FileLog.m3e(e22);
                                        }
                                    }
                                } catch (Throwable e222) {
                                    th = e222;
                                    query = null;
                                    if (query != null) {
                                        try {
                                            if (!query.isClosed()) {
                                                query.close();
                                            }
                                        } catch (Throwable e2222) {
                                            FileLog.m3e(e2222);
                                        }
                                    }
                                    throw th;
                                }
                            }
                        }
                        showAttachmentError();
                        return;
                    }
                }
            }
            createChatAttachView();
            if (chatActivity.chatAttachAlert != null) {
                chatActivity.chatAttachAlert.onActivityResultFragment(i3, intent2, chatActivity.currentPicturePath);
            }
            chatActivity.currentPicturePath = null;
        }
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.currentPicturePath != null) {
            bundle.putString("path", this.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        this.currentPicturePath = bundle.getString("path");
    }

    private void removeUnreadPlane(boolean z) {
        if (this.unreadMessageObject != null) {
            if (z) {
                z = this.forwardEndReached;
                this.forwardEndReached[1] = true;
                z[0] = 1;
                this.first_unread_id = 0;
                this.last_message_id = 0;
            }
            this.createUnreadMessageAfterId = 0;
            this.createUnreadMessageAfterIdLoading = false;
            removeMessageObject(this.unreadMessageObject);
            this.unreadMessageObject = false;
        }
    }

    public boolean processSendingText(String str) {
        return this.chatActivityEnterView.processSendingText(str);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        boolean z;
        Object obj;
        Throwable th;
        ChatActivity chatActivity = this;
        int i3 = i;
        Object[] objArr2 = objArr;
        int clientUserId;
        ArrayList arrayList;
        int i4;
        MessageObject messageObject;
        MessageObject messageObject2;
        boolean z2;
        int i5;
        int i6;
        int intValue;
        boolean booleanValue;
        int intValue2;
        int intValue3;
        int intValue4;
        int size;
        MessageObject messageObject3;
        int i7;
        int i8;
        MessageObject messageObject4;
        MessageObject playingMessageObject;
        boolean z3;
        boolean isUnread;
        ArrayList arrayList2;
        Message tL_message;
        GroupedMessages groupedMessages;
        boolean z4;
        LongSparseArray longSparseArray;
        Message tL_message2;
        MessageObject messageObject5;
        GroupedMessages groupedMessages2;
        View findViewByPosition;
        boolean z5;
        if (i3 == NotificationCenter.messagesDidLoaded) {
            if (((Integer) objArr2[10]).intValue() == chatActivity.classGuid) {
                if (!chatActivity.openAnimationEnded) {
                    NotificationCenter.getInstance(chatActivity.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoaded, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.botKeyboardDidLoaded});
                }
                i3 = chatActivity.waitingForLoad.indexOf(Integer.valueOf(((Integer) objArr2[11]).intValue()));
                clientUserId = UserConfig.getInstance(chatActivity.currentAccount).getClientUserId();
                if (i3 != -1) {
                    boolean z6;
                    boolean z7;
                    boolean z8;
                    int i9;
                    int i10;
                    int i11;
                    boolean z9;
                    LongSparseArray longSparseArray2;
                    chatActivity.waitingForLoad.remove(i3);
                    arrayList = (ArrayList) objArr2[2];
                    if (chatActivity.waitingForReplyMessageLoad) {
                        if (!chatActivity.createUnreadMessageAfterIdLoading) {
                            i4 = 0;
                            while (i4 < arrayList.size()) {
                                messageObject = (MessageObject) arrayList.get(i4);
                                if (messageObject.getId() != chatActivity.startLoadFromMessageId) {
                                    i4++;
                                    if (i4 < arrayList.size()) {
                                        messageObject2 = (MessageObject) arrayList.get(i4);
                                        if (messageObject.getId() >= chatActivity.startLoadFromMessageId && messageObject2.getId() < chatActivity.startLoadFromMessageId) {
                                            chatActivity.startLoadFromMessageId = messageObject.getId();
                                        }
                                    }
                                }
                                z2 = true;
                            }
                            z2 = false;
                            if (!z2) {
                                chatActivity.startLoadFromMessageId = 0;
                                return;
                            }
                        }
                        i5 = chatActivity.startLoadFromMessageId;
                        z6 = chatActivity.needSelectFromMessageId;
                        i6 = chatActivity.createUnreadMessageAfterId;
                        z7 = chatActivity.createUnreadMessageAfterIdLoading;
                        clearChatData();
                        chatActivity.createUnreadMessageAfterId = i6;
                        chatActivity.startLoadFromMessageId = i5;
                        chatActivity.needSelectFromMessageId = z6;
                    } else {
                        z7 = false;
                    }
                    chatActivity.loadsCount++;
                    z2 = ((Long) objArr2[0]).longValue() != chatActivity.dialog_id;
                    intValue = ((Integer) objArr2[1]).intValue();
                    booleanValue = ((Boolean) objArr2[3]).booleanValue();
                    intValue2 = ((Integer) objArr2[4]).intValue();
                    ((Integer) objArr2[7]).intValue();
                    intValue3 = ((Integer) objArr2[8]).intValue();
                    i4 = ((Integer) objArr2[12]).intValue();
                    intValue4 = ((Integer) objArr2[13]).intValue();
                    if (intValue4 < 0) {
                        intValue4 *= -1;
                        chatActivity.hasAllMentionsLocal = false;
                    } else if (chatActivity.first) {
                        chatActivity.hasAllMentionsLocal = true;
                    }
                    if (intValue3 == 4) {
                        chatActivity.startLoadFromMessageId = i4;
                        for (size = arrayList.size() - 1; size > 0; size--) {
                            messageObject3 = (MessageObject) arrayList.get(size);
                            if (messageObject3.type < 0 && messageObject3.getId() == chatActivity.startLoadFromMessageId) {
                                chatActivity.startLoadFromMessageId = ((MessageObject) arrayList.get(size - 1)).getId();
                                break;
                            }
                        }
                    }
                    if (intValue2 != 0) {
                        chatActivity.last_message_id = ((Integer) objArr2[5]).intValue();
                        if (intValue3 == 3) {
                            if (chatActivity.loadingFromOldPosition) {
                                size = ((Integer) objArr2[6]).intValue();
                                if (size != 0) {
                                    chatActivity.createUnreadMessageAfterId = intValue2;
                                }
                                i7 = 0;
                                chatActivity.loadingFromOldPosition = false;
                            } else {
                                i7 = 0;
                                size = 0;
                            }
                            chatActivity.first_unread_id = i7;
                        } else {
                            chatActivity.first_unread_id = intValue2;
                            size = ((Integer) objArr2[6]).intValue();
                        }
                    } else {
                        if (chatActivity.startLoadFromMessageId != 0 && (intValue3 == 3 || intValue3 == 4)) {
                            chatActivity.last_message_id = ((Integer) objArr2[5]).intValue();
                        }
                        size = 0;
                    }
                    if (intValue3 == 0 || (chatActivity.startLoadFromMessageId == 0 && chatActivity.last_message_id == 0)) {
                        z8 = false;
                    } else {
                        z8 = false;
                        chatActivity.forwardEndReached[z2] = false;
                    }
                    if ((intValue3 == 1 || intValue3 == 3) && z2) {
                        boolean[] zArr = chatActivity.endReached;
                        chatActivity.cacheEndReached[z8] = true;
                        zArr[z8] = true;
                        chatActivity.forwardEndReached[z8] = z8;
                        chatActivity.minMessageId[z8] = z8;
                    }
                    if (chatActivity.loadsCount == 1 && arrayList.size() > 20) {
                        chatActivity.loadsCount++;
                    }
                    if (chatActivity.firstLoading) {
                        if (!chatActivity.forwardEndReached[z2]) {
                            chatActivity.messages.clear();
                            chatActivity.messagesByDays.clear();
                            chatActivity.groupedMessagesMap.clear();
                            for (i7 = 0; i7 < 2; i7++) {
                                chatActivity.messagesDict[i7].clear();
                                if (chatActivity.currentEncryptedChat == null) {
                                    chatActivity.maxMessageId[i7] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    i8 = Integer.MIN_VALUE;
                                    chatActivity.minMessageId[i7] = Integer.MIN_VALUE;
                                } else {
                                    i8 = Integer.MIN_VALUE;
                                    chatActivity.maxMessageId[i7] = Integer.MIN_VALUE;
                                    chatActivity.minMessageId[i7] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                }
                                chatActivity.maxDate[i7] = i8;
                                chatActivity.minDate[i7] = 0;
                            }
                        }
                        chatActivity.firstLoading = false;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (ChatActivity.this.parentLayout != null) {
                                    ChatActivity.this.parentLayout.resumeDelayedFragmentAnimation();
                                }
                            }
                        });
                    }
                    if (intValue3 == 1) {
                        Collections.reverse(arrayList);
                    }
                    if (chatActivity.currentEncryptedChat == null) {
                        DataQuery.getInstance(chatActivity.currentAccount).loadReplyMessagesForMessages(arrayList, chatActivity.dialog_id);
                    }
                    if (intValue3 == 2 && arrayList.isEmpty() && !booleanValue) {
                        chatActivity.forwardEndReached[0] = true;
                    }
                    MediaController instance = MediaController.getInstance();
                    intValue2 = 0;
                    i4 = 0;
                    i8 = 0;
                    LongSparseArray longSparseArray3 = null;
                    LongSparseArray longSparseArray4 = null;
                    while (intValue2 < arrayList.size()) {
                        int i12;
                        MediaController mediaController;
                        messageObject4 = (MessageObject) arrayList.get(intValue2);
                        i4 += messageObject4.getApproximateHeight();
                        i9 = intValue4;
                        if (chatActivity.currentUser != null) {
                            if (chatActivity.currentUser.self) {
                                i10 = size;
                                messageObject4.messageOwner.out = true;
                            } else {
                                i10 = size;
                            }
                            if ((chatActivity.currentUser.bot && messageObject4.isOut()) || chatActivity.currentUser.id == clientUserId) {
                                messageObject4.setIsRead();
                            }
                        } else {
                            i10 = size;
                        }
                        if (chatActivity.messagesDict[z2].indexOfKey(messageObject4.getId()) >= 0) {
                            i12 = clientUserId;
                        } else {
                            if (instance.isPlayingMessage(messageObject4)) {
                                playingMessageObject = instance.getPlayingMessageObject();
                                messageObject4.audioProgress = playingMessageObject.audioProgress;
                                messageObject4.audioProgressSec = playingMessageObject.audioProgressSec;
                                messageObject4.audioPlayerDuration = playingMessageObject.audioPlayerDuration;
                            }
                            if (!z2 && ChatObject.isChannel(chatActivity.currentChat) && messageObject4.getId() == 1) {
                                chatActivity.endReached[z2] = true;
                                chatActivity.cacheEndReached[z2] = true;
                            }
                            if (messageObject4.getId() > 0) {
                                i12 = clientUserId;
                                chatActivity.maxMessageId[z2] = Math.min(messageObject4.getId(), chatActivity.maxMessageId[z2]);
                                chatActivity.minMessageId[z2] = Math.max(messageObject4.getId(), chatActivity.minMessageId[z2]);
                            } else {
                                i12 = clientUserId;
                                if (chatActivity.currentEncryptedChat != null) {
                                    chatActivity.maxMessageId[z2] = Math.max(messageObject4.getId(), chatActivity.maxMessageId[z2]);
                                    chatActivity.minMessageId[z2] = Math.min(messageObject4.getId(), chatActivity.minMessageId[z2]);
                                }
                            }
                            if (messageObject4.messageOwner.date != 0) {
                                chatActivity.maxDate[z2] = Math.max(chatActivity.maxDate[z2], messageObject4.messageOwner.date);
                                if (chatActivity.minDate[z2] == 0 || messageObject4.messageOwner.date < chatActivity.minDate[z2]) {
                                    chatActivity.minDate[z2] = messageObject4.messageOwner.date;
                                }
                            }
                            if (messageObject4.getId() == chatActivity.last_message_id) {
                                z3 = true;
                                chatActivity.forwardEndReached[z2] = true;
                            } else {
                                z3 = true;
                            }
                            if (messageObject4.type >= 0) {
                                if (z2 != z3 || !(messageObject4.messageOwner.action instanceof TL_messageActionChatMigrateTo)) {
                                    int i13;
                                    Message tL_message3;
                                    if (chatActivity.needAnimateToMessage != null && chatActivity.needAnimateToMessage.getId() == messageObject4.getId() && messageObject4.getId() < 0 && messageObject4.type == 5) {
                                        messageObject4 = chatActivity.needAnimateToMessage;
                                        chatActivity.animatingMessageObjects.add(messageObject4);
                                        chatActivity.needAnimateToMessage = null;
                                    }
                                    if (!messageObject4.isOut()) {
                                        isUnread = messageObject4.isUnread();
                                    }
                                    chatActivity.messagesDict[z2].put(messageObject4.getId(), messageObject4);
                                    arrayList2 = (ArrayList) chatActivity.messagesByDays.get(messageObject4.dateKey);
                                    if (arrayList2 == null) {
                                        arrayList2 = new ArrayList();
                                        chatActivity.messagesByDays.put(messageObject4.dateKey, arrayList2);
                                        tL_message = new TL_message();
                                        i11 = intValue;
                                        z9 = booleanValue;
                                        tL_message.message = LocaleController.formatDateChat((long) messageObject4.messageOwner.date);
                                        tL_message.id = 0;
                                        tL_message.date = messageObject4.messageOwner.date;
                                        messageObject2 = new MessageObject(chatActivity.currentAccount, tL_message, false);
                                        messageObject2.type = 10;
                                        messageObject2.contentType = 1;
                                        messageObject2.isDateObject = true;
                                        if (intValue3 == 1) {
                                            chatActivity.messages.add(0, messageObject2);
                                        } else {
                                            chatActivity.messages.add(messageObject2);
                                        }
                                        i8++;
                                    } else {
                                        i11 = intValue;
                                        z9 = booleanValue;
                                    }
                                    if (messageObject4.hasValidGroupId()) {
                                        GroupedMessages groupedMessages3;
                                        groupedMessages = (GroupedMessages) chatActivity.groupedMessagesMap.get(messageObject4.messageOwner.grouped_id);
                                        if (groupedMessages == null || chatActivity.messages.size() <= 1) {
                                            mediaController = instance;
                                            z4 = z7;
                                            groupedMessages3 = groupedMessages;
                                        } else {
                                            if (intValue3 == 1) {
                                                playingMessageObject = (MessageObject) chatActivity.messages.get(0);
                                            } else {
                                                playingMessageObject = (MessageObject) chatActivity.messages.get(chatActivity.messages.size() - 2);
                                            }
                                            mediaController = instance;
                                            z4 = z7;
                                            groupedMessages3 = groupedMessages;
                                            if (playingMessageObject.messageOwner.grouped_id == messageObject4.messageOwner.grouped_id) {
                                                if (playingMessageObject.localGroupId != 0) {
                                                    messageObject4.localGroupId = playingMessageObject.localGroupId;
                                                    groupedMessages = (GroupedMessages) chatActivity.groupedMessagesMap.get(playingMessageObject.localGroupId);
                                                    if (groupedMessages == null) {
                                                        groupedMessages = new GroupedMessages();
                                                        groupedMessages.groupId = messageObject4.getGroupId();
                                                        chatActivity.groupedMessagesMap.put(groupedMessages.groupId, groupedMessages);
                                                        i13 = intValue2;
                                                        longSparseArray2 = longSparseArray3;
                                                    } else {
                                                        longSparseArray2 = longSparseArray3;
                                                        if (longSparseArray2 != null) {
                                                            if (longSparseArray2.indexOfKey(messageObject4.getGroupId()) < 0) {
                                                                i13 = intValue2;
                                                            }
                                                        }
                                                        longSparseArray = longSparseArray4;
                                                        if (longSparseArray == null) {
                                                            longSparseArray = new LongSparseArray();
                                                        }
                                                        i13 = intValue2;
                                                        longSparseArray.put(messageObject4.getGroupId(), groupedMessages);
                                                        longSparseArray4 = longSparseArray;
                                                    }
                                                    if (longSparseArray2 == null) {
                                                        longSparseArray2 = new LongSparseArray();
                                                    }
                                                    longSparseArray2.put(groupedMessages.groupId, groupedMessages);
                                                    if (intValue3 == 1) {
                                                        groupedMessages.messages.add(messageObject4);
                                                    } else {
                                                        groupedMessages.messages.add(0, messageObject4);
                                                    }
                                                }
                                            } else if (playingMessageObject.messageOwner.grouped_id != messageObject4.messageOwner.grouped_id) {
                                                messageObject4.localGroupId = Utilities.random.nextLong();
                                                groupedMessages = null;
                                                if (groupedMessages == null) {
                                                    longSparseArray2 = longSparseArray3;
                                                    if (longSparseArray2 != null) {
                                                        if (longSparseArray2.indexOfKey(messageObject4.getGroupId()) < 0) {
                                                            i13 = intValue2;
                                                        }
                                                    }
                                                    longSparseArray = longSparseArray4;
                                                    if (longSparseArray == null) {
                                                        longSparseArray = new LongSparseArray();
                                                    }
                                                    i13 = intValue2;
                                                    longSparseArray.put(messageObject4.getGroupId(), groupedMessages);
                                                    longSparseArray4 = longSparseArray;
                                                } else {
                                                    groupedMessages = new GroupedMessages();
                                                    groupedMessages.groupId = messageObject4.getGroupId();
                                                    chatActivity.groupedMessagesMap.put(groupedMessages.groupId, groupedMessages);
                                                    i13 = intValue2;
                                                    longSparseArray2 = longSparseArray3;
                                                }
                                                if (longSparseArray2 == null) {
                                                    longSparseArray2 = new LongSparseArray();
                                                }
                                                longSparseArray2.put(groupedMessages.groupId, groupedMessages);
                                                if (intValue3 == 1) {
                                                    groupedMessages.messages.add(0, messageObject4);
                                                } else {
                                                    groupedMessages.messages.add(messageObject4);
                                                }
                                            }
                                        }
                                        groupedMessages = groupedMessages3;
                                        if (groupedMessages == null) {
                                            groupedMessages = new GroupedMessages();
                                            groupedMessages.groupId = messageObject4.getGroupId();
                                            chatActivity.groupedMessagesMap.put(groupedMessages.groupId, groupedMessages);
                                            i13 = intValue2;
                                            longSparseArray2 = longSparseArray3;
                                        } else {
                                            longSparseArray2 = longSparseArray3;
                                            if (longSparseArray2 != null) {
                                                if (longSparseArray2.indexOfKey(messageObject4.getGroupId()) < 0) {
                                                    i13 = intValue2;
                                                }
                                            }
                                            longSparseArray = longSparseArray4;
                                            if (longSparseArray == null) {
                                                longSparseArray = new LongSparseArray();
                                            }
                                            i13 = intValue2;
                                            longSparseArray.put(messageObject4.getGroupId(), groupedMessages);
                                            longSparseArray4 = longSparseArray;
                                        }
                                        if (longSparseArray2 == null) {
                                            longSparseArray2 = new LongSparseArray();
                                        }
                                        longSparseArray2.put(groupedMessages.groupId, groupedMessages);
                                        if (intValue3 == 1) {
                                            groupedMessages.messages.add(messageObject4);
                                        } else {
                                            groupedMessages.messages.add(0, messageObject4);
                                        }
                                    } else {
                                        mediaController = instance;
                                        i13 = intValue2;
                                        z4 = z7;
                                        longSparseArray2 = longSparseArray3;
                                        longSparseArray = longSparseArray4;
                                        if (messageObject4.messageOwner.grouped_id != 0) {
                                            messageObject4.messageOwner.grouped_id = 0;
                                        }
                                        longSparseArray4 = longSparseArray;
                                    }
                                    i8++;
                                    arrayList2.add(messageObject4);
                                    if (intValue3 == 1) {
                                        chatActivity.messages.add(0, messageObject4);
                                    } else {
                                        chatActivity.messages.add(chatActivity.messages.size() - 1, messageObject4);
                                    }
                                    MessageObject messageObject6;
                                    if (chatActivity.currentEncryptedChat == null) {
                                        if (!(chatActivity.createUnreadMessageAfterId == 0 || intValue3 == 1)) {
                                            intValue2 = i13 + 1;
                                            if (intValue2 < arrayList.size()) {
                                                messageObject6 = (MessageObject) arrayList.get(intValue2);
                                                if (!messageObject4.isOut()) {
                                                    if (messageObject6.getId() >= chatActivity.createUnreadMessageAfterId) {
                                                    }
                                                }
                                            }
                                        }
                                        clientUserId = 2;
                                        messageObject6 = null;
                                        if (intValue3 != clientUserId && messageObject4.getId() == chatActivity.first_unread_id) {
                                            if (i4 <= AndroidUtilities.displaySize.y / clientUserId) {
                                                z6 = false;
                                                if (!chatActivity.forwardEndReached[0]) {
                                                }
                                            } else {
                                                z6 = false;
                                            }
                                            tL_message2 = new TL_message();
                                            tL_message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                                            tL_message2.id = z6;
                                            messageObject2 = new MessageObject(chatActivity.currentAccount, tL_message2, z6);
                                            messageObject2.type = 6;
                                            messageObject2.contentType = 2;
                                            chatActivity.messages.add(chatActivity.messages.size() - 1, messageObject2);
                                            chatActivity.unreadMessageObject = messageObject2;
                                            chatActivity.scrollToMessage = chatActivity.unreadMessageObject;
                                            chatActivity.scrollToMessagePosition = -10000;
                                            i8++;
                                        } else if ((intValue3 == 3 || intValue3 == 4) && messageObject4.getId() == chatActivity.startLoadFromMessageId) {
                                            if (chatActivity.needSelectFromMessageId) {
                                                chatActivity.highlightMessageId = messageObject4.getId();
                                            } else {
                                                chatActivity.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                            }
                                            chatActivity.scrollToMessage = messageObject4;
                                            chatActivity.startLoadFromMessageId = 0;
                                            if (chatActivity.scrollToMessagePosition == -10000) {
                                                chatActivity.scrollToMessagePosition = -9000;
                                            }
                                        }
                                        if (!(intValue3 == 2 || chatActivity.unreadMessageObject != null || chatActivity.createUnreadMessageAfterId == 0)) {
                                            if (chatActivity.currentEncryptedChat == null && !messageObject4.isOut()) {
                                                if (messageObject4.getId() < chatActivity.createUnreadMessageAfterId) {
                                                }
                                                if (intValue3 != 1 || messageObject6 != null) {
                                                    clientUserId = i13;
                                                } else if (messageObject6 == null && r36) {
                                                    clientUserId = i13;
                                                    if (clientUserId == arrayList.size() - 1) {
                                                    }
                                                    longSparseArray3 = longSparseArray2;
                                                    intValue2 = clientUserId + 1;
                                                    intValue4 = i9;
                                                    size = i10;
                                                    clientUserId = i12;
                                                    intValue = i11;
                                                    booleanValue = z9;
                                                    instance = mediaController;
                                                    z7 = z4;
                                                }
                                                tL_message3 = new TL_message();
                                                tL_message3.message = TtmlNode.ANONYMOUS_REGION_ID;
                                                tL_message3.id = 0;
                                                messageObject5 = new MessageObject(chatActivity.currentAccount, tL_message3, false);
                                                messageObject5.type = 6;
                                                messageObject5.contentType = 2;
                                                if (intValue3 != 1) {
                                                    chatActivity.messages.add(1, messageObject5);
                                                } else {
                                                    chatActivity.messages.add(chatActivity.messages.size() - 1, messageObject5);
                                                }
                                                chatActivity.unreadMessageObject = messageObject5;
                                                if (intValue3 == 3) {
                                                    chatActivity.scrollToMessage = chatActivity.unreadMessageObject;
                                                    chatActivity.startLoadFromMessageId = 0;
                                                    chatActivity.scrollToMessagePosition = -9000;
                                                }
                                                i8++;
                                                longSparseArray3 = longSparseArray2;
                                                intValue2 = clientUserId + 1;
                                                intValue4 = i9;
                                                size = i10;
                                                clientUserId = i12;
                                                intValue = i11;
                                                booleanValue = z9;
                                                instance = mediaController;
                                                z7 = z4;
                                            }
                                            if (!(chatActivity.currentEncryptedChat == null || messageObject4.isOut() || messageObject4.getId() > chatActivity.createUnreadMessageAfterId)) {
                                                if (intValue3 != 1) {
                                                }
                                                clientUserId = i13;
                                                tL_message3 = new TL_message();
                                                tL_message3.message = TtmlNode.ANONYMOUS_REGION_ID;
                                                tL_message3.id = 0;
                                                messageObject5 = new MessageObject(chatActivity.currentAccount, tL_message3, false);
                                                messageObject5.type = 6;
                                                messageObject5.contentType = 2;
                                                if (intValue3 != 1) {
                                                    chatActivity.messages.add(chatActivity.messages.size() - 1, messageObject5);
                                                } else {
                                                    chatActivity.messages.add(1, messageObject5);
                                                }
                                                chatActivity.unreadMessageObject = messageObject5;
                                                if (intValue3 == 3) {
                                                    chatActivity.scrollToMessage = chatActivity.unreadMessageObject;
                                                    chatActivity.startLoadFromMessageId = 0;
                                                    chatActivity.scrollToMessagePosition = -9000;
                                                }
                                                i8++;
                                                longSparseArray3 = longSparseArray2;
                                                intValue2 = clientUserId + 1;
                                                intValue4 = i9;
                                                size = i10;
                                                clientUserId = i12;
                                                intValue = i11;
                                                booleanValue = z9;
                                                instance = mediaController;
                                                z7 = z4;
                                            }
                                        }
                                        clientUserId = i13;
                                        longSparseArray3 = longSparseArray2;
                                        intValue2 = clientUserId + 1;
                                        intValue4 = i9;
                                        size = i10;
                                        clientUserId = i12;
                                        intValue = i11;
                                        booleanValue = z9;
                                        instance = mediaController;
                                        z7 = z4;
                                    } else {
                                        if (!(chatActivity.createUnreadMessageAfterId == 0 || intValue3 == 1)) {
                                            intValue2 = i13 - 1;
                                            if (intValue2 >= 0) {
                                                messageObject6 = (MessageObject) arrayList.get(intValue2);
                                                if (!messageObject4.isOut()) {
                                                    if (messageObject6.getId() >= chatActivity.createUnreadMessageAfterId) {
                                                    }
                                                }
                                            }
                                        }
                                        clientUserId = 2;
                                        messageObject6 = null;
                                        if (intValue3 != clientUserId) {
                                        }
                                        if (chatActivity.needSelectFromMessageId) {
                                            chatActivity.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                        } else {
                                            chatActivity.highlightMessageId = messageObject4.getId();
                                        }
                                        chatActivity.scrollToMessage = messageObject4;
                                        chatActivity.startLoadFromMessageId = 0;
                                        if (chatActivity.scrollToMessagePosition == -10000) {
                                            chatActivity.scrollToMessagePosition = -9000;
                                        }
                                        if (messageObject4.getId() < chatActivity.createUnreadMessageAfterId) {
                                        }
                                        if (intValue3 != 1) {
                                        }
                                        clientUserId = i13;
                                        tL_message3 = new TL_message();
                                        tL_message3.message = TtmlNode.ANONYMOUS_REGION_ID;
                                        tL_message3.id = 0;
                                        messageObject5 = new MessageObject(chatActivity.currentAccount, tL_message3, false);
                                        messageObject5.type = 6;
                                        messageObject5.contentType = 2;
                                        if (intValue3 != 1) {
                                            chatActivity.messages.add(1, messageObject5);
                                        } else {
                                            chatActivity.messages.add(chatActivity.messages.size() - 1, messageObject5);
                                        }
                                        chatActivity.unreadMessageObject = messageObject5;
                                        if (intValue3 == 3) {
                                            chatActivity.scrollToMessage = chatActivity.unreadMessageObject;
                                            chatActivity.startLoadFromMessageId = 0;
                                            chatActivity.scrollToMessagePosition = -9000;
                                        }
                                        i8++;
                                        longSparseArray3 = longSparseArray2;
                                        intValue2 = clientUserId + 1;
                                        intValue4 = i9;
                                        size = i10;
                                        clientUserId = i12;
                                        intValue = i11;
                                        booleanValue = z9;
                                        instance = mediaController;
                                        z7 = z4;
                                    }
                                    clientUserId = 2;
                                    if (intValue3 != clientUserId) {
                                    }
                                    if (chatActivity.needSelectFromMessageId) {
                                        chatActivity.highlightMessageId = messageObject4.getId();
                                    } else {
                                        chatActivity.highlightMessageId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    }
                                    chatActivity.scrollToMessage = messageObject4;
                                    chatActivity.startLoadFromMessageId = 0;
                                    if (chatActivity.scrollToMessagePosition == -10000) {
                                        chatActivity.scrollToMessagePosition = -9000;
                                    }
                                    if (messageObject4.getId() < chatActivity.createUnreadMessageAfterId) {
                                    }
                                    if (intValue3 != 1) {
                                    }
                                    clientUserId = i13;
                                    tL_message3 = new TL_message();
                                    tL_message3.message = TtmlNode.ANONYMOUS_REGION_ID;
                                    tL_message3.id = 0;
                                    messageObject5 = new MessageObject(chatActivity.currentAccount, tL_message3, false);
                                    messageObject5.type = 6;
                                    messageObject5.contentType = 2;
                                    if (intValue3 != 1) {
                                        chatActivity.messages.add(chatActivity.messages.size() - 1, messageObject5);
                                    } else {
                                        chatActivity.messages.add(1, messageObject5);
                                    }
                                    chatActivity.unreadMessageObject = messageObject5;
                                    if (intValue3 == 3) {
                                        chatActivity.scrollToMessage = chatActivity.unreadMessageObject;
                                        chatActivity.startLoadFromMessageId = 0;
                                        chatActivity.scrollToMessagePosition = -9000;
                                    }
                                    i8++;
                                    longSparseArray3 = longSparseArray2;
                                    intValue2 = clientUserId + 1;
                                    intValue4 = i9;
                                    size = i10;
                                    clientUserId = i12;
                                    intValue = i11;
                                    booleanValue = z9;
                                    instance = mediaController;
                                    z7 = z4;
                                }
                            }
                        }
                        i11 = intValue;
                        z9 = booleanValue;
                        mediaController = instance;
                        clientUserId = intValue2;
                        z4 = z7;
                        longSparseArray3 = longSparseArray3;
                        longSparseArray4 = longSparseArray4;
                        intValue2 = clientUserId + 1;
                        intValue4 = i9;
                        size = i10;
                        clientUserId = i12;
                        intValue = i11;
                        booleanValue = z9;
                        instance = mediaController;
                        z7 = z4;
                    }
                    i9 = intValue4;
                    i11 = intValue;
                    z9 = booleanValue;
                    i10 = size;
                    longSparseArray2 = longSparseArray3;
                    longSparseArray = longSparseArray4;
                    if (z7) {
                        chatActivity.createUnreadMessageAfterId = 0;
                    }
                    if (intValue3 == 0 && i8 == 0) {
                        chatActivity.loadsCount--;
                    }
                    if (longSparseArray2 != null) {
                        clientUserId = 0;
                        while (clientUserId < longSparseArray2.size()) {
                            groupedMessages2 = (GroupedMessages) longSparseArray2.valueAt(clientUserId);
                            groupedMessages2.calculate();
                            if (!(chatActivity.chatAdapter == null || longSparseArray == null || longSparseArray.indexOfKey(longSparseArray2.keyAt(clientUserId)) < 0)) {
                                i7 = chatActivity.messages.indexOf((MessageObject) groupedMessages2.messages.get(groupedMessages2.messages.size() - 1));
                                if (i7 >= 0) {
                                    chatActivity.chatAdapter.notifyItemRangeChanged(i7 + chatActivity.chatAdapter.messagesStartRow, groupedMessages2.messages.size());
                                }
                            }
                            clientUserId++;
                        }
                    }
                    if (chatActivity.forwardEndReached[z2]) {
                        clientUserId = 1;
                        if (!z2) {
                            chatActivity.first_unread_id = 0;
                            chatActivity.last_message_id = 0;
                            chatActivity.createUnreadMessageAfterId = 0;
                        }
                    } else {
                        clientUserId = 1;
                    }
                    if (intValue3 == clientUserId) {
                        if (arrayList.size() == i11 || (z9 && chatActivity.currentEncryptedChat == null && !chatActivity.forwardEndReached[z2])) {
                            size = 0;
                        } else {
                            chatActivity.forwardEndReached[z2] = true;
                            if (!z2) {
                                i3 = 0;
                                chatActivity.first_unread_id = 0;
                                chatActivity.last_message_id = 0;
                                chatActivity.createUnreadMessageAfterId = 0;
                                chatActivity.chatAdapter.notifyItemRemoved(chatActivity.chatAdapter.loadingDownRow);
                                size = 1;
                            } else {
                                i3 = 0;
                                size = 0;
                            }
                            chatActivity.startLoadFromMessageId = i3;
                        }
                        if (i8 > 0) {
                            i3 = chatActivity.chatLayoutManager.findFirstVisibleItemPosition();
                            if (i3 == 0) {
                                i3++;
                            }
                            View findViewByPosition2 = chatActivity.chatLayoutManager.findViewByPosition(i3);
                            if (findViewByPosition2 == null) {
                                clientUserId = 0;
                            } else {
                                clientUserId = (chatActivity.chatListView.getMeasuredHeight() - findViewByPosition2.getBottom()) - chatActivity.chatListView.getPaddingBottom();
                            }
                            chatActivity.chatAdapter.notifyItemRangeInserted(1, i8);
                            if (i3 != -1) {
                                chatActivity.chatLayoutManager.scrollToPositionWithOffset((i3 + i8) - size, clientUserId);
                            }
                        }
                        chatActivity.loadingForward = false;
                    } else {
                        if (!(arrayList.size() >= i11 || intValue3 == 3 || intValue3 == 4)) {
                            if (z9) {
                                if (chatActivity.currentEncryptedChat == null) {
                                    if (!chatActivity.isBroadcast) {
                                        isUnread = true;
                                        if (intValue3 != 2) {
                                            chatActivity.cacheEndReached[z2] = isUnread;
                                        }
                                    }
                                }
                                isUnread = true;
                                chatActivity.endReached[z2] = true;
                                if (intValue3 != 2) {
                                    chatActivity.cacheEndReached[z2] = isUnread;
                                }
                            } else if (intValue3 != 2 || (arrayList.size() == 0 && chatActivity.messages.isEmpty())) {
                                chatActivity.endReached[z2] = true;
                            }
                        }
                        chatActivity.loading = false;
                        if (chatActivity.chatListView != null) {
                            TextView textView;
                            Object[] objArr3;
                            if (!(chatActivity.first || chatActivity.scrollToTopOnResume)) {
                                if (!chatActivity.forceScrollToTop) {
                                    if (i8 != 0) {
                                        if (chatActivity.endReached[z2]) {
                                            if (!z2) {
                                                if (chatActivity.mergeDialogId == 0) {
                                                    i3 = 1;
                                                    chatActivity.chatAdapter.notifyItemRangeChanged(chatActivity.chatAdapter.loadingUpRow - i3, 2);
                                                    chatActivity.chatAdapter.updateRows();
                                                    i3 = 1;
                                                    clientUserId = chatActivity.chatLayoutManager.findFirstVisibleItemPosition();
                                                    findViewByPosition = chatActivity.chatLayoutManager.findViewByPosition(clientUserId);
                                                    if (findViewByPosition != null) {
                                                        size = 0;
                                                    } else {
                                                        size = (chatActivity.chatListView.getMeasuredHeight() - findViewByPosition.getBottom()) - chatActivity.chatListView.getPaddingBottom();
                                                    }
                                                    if (i8 - i3 > 0) {
                                                        intValue4 = chatActivity.chatAdapter.messagesEndRow;
                                                        chatActivity.chatAdapter.notifyItemChanged(chatActivity.chatAdapter.loadingUpRow);
                                                        chatActivity.chatAdapter.notifyItemRangeInserted(intValue4, i8 - i3);
                                                    }
                                                    if (clientUserId != -1) {
                                                        chatActivity.chatLayoutManager.scrollToPositionWithOffset(clientUserId, size);
                                                    }
                                                }
                                            }
                                            i3 = 1;
                                        }
                                        i3 = 0;
                                        clientUserId = chatActivity.chatLayoutManager.findFirstVisibleItemPosition();
                                        findViewByPosition = chatActivity.chatLayoutManager.findViewByPosition(clientUserId);
                                        if (findViewByPosition != null) {
                                            size = (chatActivity.chatListView.getMeasuredHeight() - findViewByPosition.getBottom()) - chatActivity.chatListView.getPaddingBottom();
                                        } else {
                                            size = 0;
                                        }
                                        if (i8 - i3 > 0) {
                                            intValue4 = chatActivity.chatAdapter.messagesEndRow;
                                            chatActivity.chatAdapter.notifyItemChanged(chatActivity.chatAdapter.loadingUpRow);
                                            chatActivity.chatAdapter.notifyItemRangeInserted(intValue4, i8 - i3);
                                        }
                                        if (clientUserId != -1) {
                                            chatActivity.chatLayoutManager.scrollToPositionWithOffset(clientUserId, size);
                                        }
                                    } else if (chatActivity.endReached[z2]) {
                                        if (z2 || chatActivity.mergeDialogId != 0) {
                                            z5 = true;
                                        }
                                        chatActivity.chatAdapter.notifyItemRemoved(chatActivity.chatAdapter.loadingUpRow);
                                    }
                                    z5 = true;
                                    if (chatActivity.paused) {
                                        chatActivity.scrollToTopOnResume = z5;
                                        if (chatActivity.scrollToMessage != null) {
                                            chatActivity.scrollToTopUnReadOnResume = z5;
                                        }
                                    }
                                    if (chatActivity.first && chatActivity.chatListView != null) {
                                        chatActivity.chatListView.setEmptyView(chatActivity.emptyViewContainer);
                                    }
                                }
                            }
                            chatActivity.forceScrollToTop = false;
                            chatActivity.chatAdapter.notifyDataSetChanged();
                            if (chatActivity.scrollToMessage != null) {
                                if (chatActivity.startLoadFromMessageOffset != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                    i3 = (-chatActivity.startLoadFromMessageOffset) - chatActivity.chatListView.getPaddingBottom();
                                    chatActivity.startLoadFromMessageOffset = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                } else {
                                    if (chatActivity.scrollToMessagePosition == -9000) {
                                        i3 = getScrollOffsetForMessage(chatActivity.scrollToMessage);
                                    } else if (chatActivity.scrollToMessagePosition == -10000) {
                                        i3 = -AndroidUtilities.dp(11.0f);
                                    } else {
                                        i3 = chatActivity.scrollToMessagePosition;
                                    }
                                    isUnread = false;
                                    if (!chatActivity.messages.isEmpty()) {
                                        if (chatActivity.messages.get(chatActivity.messages.size() - 1) != chatActivity.scrollToMessage) {
                                            if (chatActivity.messages.get(chatActivity.messages.size() - 2) == chatActivity.scrollToMessage) {
                                                chatActivity.chatLayoutManager.scrollToPositionWithOffset(chatActivity.chatAdapter.messagesStartRow + chatActivity.messages.indexOf(chatActivity.scrollToMessage), i3, isUnread);
                                            }
                                        }
                                        chatActivity.chatLayoutManager.scrollToPositionWithOffset(chatActivity.chatAdapter.loadingUpRow, i3, isUnread);
                                    }
                                    chatActivity.chatListView.invalidate();
                                    if (chatActivity.scrollToMessagePosition != -10000) {
                                        if (chatActivity.scrollToMessagePosition == -9000) {
                                        }
                                        chatActivity.scrollToMessagePosition = -10000;
                                        chatActivity.scrollToMessage = null;
                                    }
                                    showPagedownButton(true, true);
                                    if (i10 != 0) {
                                        chatActivity.pagedownButtonCounter.setVisibility(0);
                                        textView = chatActivity.pagedownButtonCounter;
                                        objArr3 = new Object[1];
                                        size = i10;
                                        chatActivity.newUnreadMessageCount = size;
                                        objArr3[0] = Integer.valueOf(size);
                                        textView.setText(String.format("%d", objArr3));
                                    }
                                    chatActivity.scrollToMessagePosition = -10000;
                                    chatActivity.scrollToMessage = null;
                                }
                                isUnread = true;
                                if (chatActivity.messages.isEmpty()) {
                                    if (chatActivity.messages.get(chatActivity.messages.size() - 1) != chatActivity.scrollToMessage) {
                                        if (chatActivity.messages.get(chatActivity.messages.size() - 2) == chatActivity.scrollToMessage) {
                                            chatActivity.chatLayoutManager.scrollToPositionWithOffset(chatActivity.chatAdapter.messagesStartRow + chatActivity.messages.indexOf(chatActivity.scrollToMessage), i3, isUnread);
                                        }
                                    }
                                    chatActivity.chatLayoutManager.scrollToPositionWithOffset(chatActivity.chatAdapter.loadingUpRow, i3, isUnread);
                                }
                                chatActivity.chatListView.invalidate();
                                if (chatActivity.scrollToMessagePosition != -10000) {
                                    if (chatActivity.scrollToMessagePosition == -9000) {
                                    }
                                    chatActivity.scrollToMessagePosition = -10000;
                                    chatActivity.scrollToMessage = null;
                                }
                                showPagedownButton(true, true);
                                if (i10 != 0) {
                                    chatActivity.pagedownButtonCounter.setVisibility(0);
                                    textView = chatActivity.pagedownButtonCounter;
                                    objArr3 = new Object[1];
                                    size = i10;
                                    chatActivity.newUnreadMessageCount = size;
                                    objArr3[0] = Integer.valueOf(size);
                                    textView.setText(String.format("%d", objArr3));
                                }
                                chatActivity.scrollToMessagePosition = -10000;
                                chatActivity.scrollToMessage = null;
                            } else {
                                moveScrollToLastMessage();
                            }
                            if (i9 != 0) {
                                z5 = true;
                                showMentiondownButton(true, true);
                                chatActivity.mentiondownButtonCounter.setVisibility(0);
                                textView = chatActivity.mentiondownButtonCounter;
                                objArr3 = new Object[1];
                                i6 = i9;
                                chatActivity.newMentionsCount = i6;
                                objArr3[0] = Integer.valueOf(i6);
                                textView.setText(String.format("%d", objArr3));
                                if (chatActivity.paused) {
                                    chatActivity.scrollToTopOnResume = z5;
                                    if (chatActivity.scrollToMessage != null) {
                                        chatActivity.scrollToTopUnReadOnResume = z5;
                                    }
                                }
                                chatActivity.chatListView.setEmptyView(chatActivity.emptyViewContainer);
                            }
                            z5 = true;
                            if (chatActivity.paused) {
                                chatActivity.scrollToTopOnResume = z5;
                                if (chatActivity.scrollToMessage != null) {
                                    chatActivity.scrollToTopUnReadOnResume = z5;
                                }
                            }
                            chatActivity.chatListView.setEmptyView(chatActivity.emptyViewContainer);
                        } else {
                            chatActivity.scrollToTopOnResume = true;
                            if (chatActivity.scrollToMessage != null) {
                                chatActivity.scrollToTopUnReadOnResume = true;
                            }
                        }
                    }
                    if (chatActivity.first && chatActivity.messages.size() > 0) {
                        chatActivity.first = false;
                    }
                    if (chatActivity.messages.isEmpty() && chatActivity.currentEncryptedChat == null && chatActivity.currentUser != null && chatActivity.currentUser.bot && chatActivity.botUser == null) {
                        chatActivity.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                        updateBottomOverlay();
                    }
                    if (i8 == 0 && chatActivity.currentEncryptedChat != null && !chatActivity.endReached[0]) {
                        chatActivity.first = true;
                        if (chatActivity.chatListView != null) {
                            chatActivity.chatListView.setEmptyView(null);
                        }
                        if (chatActivity.emptyViewContainer != null) {
                            chatActivity.emptyViewContainer.setVisibility(4);
                        }
                    } else if (chatActivity.progressView != null) {
                        chatActivity.progressView.setVisibility(4);
                    }
                    checkScrollForLoad(false);
                }
            }
        } else if (i3 == NotificationCenter.emojiDidLoaded) {
            if (chatActivity.chatListView != null) {
                chatActivity.chatListView.invalidateViews();
            }
            if (chatActivity.replyObjectTextView != null) {
                chatActivity.replyObjectTextView.invalidate();
            }
            if (chatActivity.alertTextView != null) {
                chatActivity.alertTextView.invalidate();
            }
            if (chatActivity.pinnedMessageTextView != null) {
                chatActivity.pinnedMessageTextView.invalidate();
            }
            if (chatActivity.mentionListView != null) {
                chatActivity.mentionListView.invalidateViews();
            }
        } else if (i3 == NotificationCenter.updateInterfaces) {
            Chat chat;
            i3 = ((Integer) objArr2[0]).intValue();
            clientUserId = i3 & 1;
            if (!(clientUserId == 0 && (i3 & 16) == 0)) {
                if (chatActivity.currentChat != null) {
                    Chat chat2 = MessagesController.getInstance(chatActivity.currentAccount).getChat(Integer.valueOf(chatActivity.currentChat.id));
                    if (chat2 != null) {
                        chatActivity.currentChat = chat2;
                    }
                } else if (chatActivity.currentUser != null) {
                    User user = MessagesController.getInstance(chatActivity.currentAccount).getUser(Integer.valueOf(chatActivity.currentUser.id));
                    if (user != null) {
                        chatActivity.currentUser = user;
                    }
                }
                updateTitle();
            }
            if ((i3 & 32) == 0) {
                if ((i3 & 4) == 0) {
                    r13 = null;
                    if (!((i3 & 2) == 0 && (i3 & 8) == 0 && clientUserId == 0)) {
                        checkAndUpdateAvatar();
                        updateVisibleRows();
                    }
                    if ((i3 & 64) != 0) {
                        r13 = 1;
                    }
                    if ((i3 & MessagesController.UPDATE_MASK_CHANNEL) != 0 && ChatObject.isChannel(chatActivity.currentChat)) {
                        chat = MessagesController.getInstance(chatActivity.currentAccount).getChat(Integer.valueOf(chatActivity.currentChat.id));
                        if (chat == null) {
                            chatActivity.currentChat = chat;
                            updateBottomOverlay();
                            if (chatActivity.chatActivityEnterView != null) {
                                chatActivity.chatActivityEnterView.setDialogId(chatActivity.dialog_id, chatActivity.currentAccount);
                            }
                            r13 = 1;
                        }
                    }
                    if (!(chatActivity.avatarContainer == null || r13 == null)) {
                        chatActivity.avatarContainer.updateSubtitle();
                    }
                    if ((i3 & 128) != 0) {
                        updateContactStatus();
                    }
                }
            }
            if (!(chatActivity.currentChat == null || chatActivity.avatarContainer == null)) {
                chatActivity.avatarContainer.updateOnlineCount();
            }
            r13 = 1;
            checkAndUpdateAvatar();
            updateVisibleRows();
            if ((i3 & 64) != 0) {
                r13 = 1;
            }
            chat = MessagesController.getInstance(chatActivity.currentAccount).getChat(Integer.valueOf(chatActivity.currentChat.id));
            if (chat == null) {
                chatActivity.currentChat = chat;
                updateBottomOverlay();
                if (chatActivity.chatActivityEnterView != null) {
                    chatActivity.chatActivityEnterView.setDialogId(chatActivity.dialog_id, chatActivity.currentAccount);
                }
                r13 = 1;
                chatActivity.avatarContainer.updateSubtitle();
                if ((i3 & 128) != 0) {
                    updateContactStatus();
                }
            }
        } else if (i3 == NotificationCenter.didReceivedNewMessages) {
            if (((Long) objArr2[0]).longValue() == chatActivity.dialog_id) {
                Object obj2;
                i3 = UserConfig.getInstance(chatActivity.currentAccount).getClientUserId();
                ArrayList arrayList3 = (ArrayList) objArr2[1];
                if (chatActivity.currentEncryptedChat != null && arrayList3.size() == 1) {
                    messageObject = (MessageObject) arrayList3.get(0);
                    if (chatActivity.currentEncryptedChat != null && messageObject.isOut() && messageObject.messageOwner.action != null && (messageObject.messageOwner.action instanceof TL_messageEncryptedAction) && (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) && getParentActivity() != null && AndroidUtilities.getPeerLayerVersion(chatActivity.currentEncryptedChat.layer) < 17 && chatActivity.currentEncryptedChat.ttl > 0 && chatActivity.currentEncryptedChat.ttl <= 60) {
                        r2 = new Builder(getParentActivity());
                        r2.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                        r2.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                        r2.setMessage(LocaleController.formatString("CompatibilityChat", C0446R.string.CompatibilityChat, chatActivity.currentUser.first_name, chatActivity.currentUser.first_name));
                        showDialog(r2.create());
                    }
                }
                if (!(chatActivity.currentChat == null && chatActivity.inlineReturn == 0)) {
                    for (clientUserId = 0; clientUserId < arrayList3.size(); clientUserId++) {
                        messageObject = (MessageObject) arrayList3.get(clientUserId);
                        if (chatActivity.currentChat != null) {
                            if (((messageObject.messageOwner.action instanceof TL_messageActionChatDeleteUser) && messageObject.messageOwner.action.user_id == i3) || ((messageObject.messageOwner.action instanceof TL_messageActionChatAddUser) && messageObject.messageOwner.action.users.contains(Integer.valueOf(i3)))) {
                                Chat chat3 = MessagesController.getInstance(chatActivity.currentAccount).getChat(Integer.valueOf(chatActivity.currentChat.id));
                                if (chat3 != null) {
                                    chatActivity.currentChat = chat3;
                                    checkActionBarMenu();
                                    updateBottomOverlay();
                                    if (chatActivity.avatarContainer != null) {
                                        chatActivity.avatarContainer.updateSubtitle();
                                    }
                                }
                            } else if (messageObject.messageOwner.reply_to_msg_id != 0 && messageObject.replyMessageObject == null) {
                                messageObject.replyMessageObject = (MessageObject) chatActivity.messagesDict[0].get(messageObject.messageOwner.reply_to_msg_id);
                                if (messageObject.messageOwner.action instanceof TL_messageActionPinMessage) {
                                    messageObject.generatePinMessageText(null, null);
                                } else if (messageObject.messageOwner.action instanceof TL_messageActionGameScore) {
                                    messageObject.generateGameMessageText(null);
                                } else if (messageObject.messageOwner.action instanceof TL_messageActionPaymentSent) {
                                    messageObject.generatePaymentSentMessageText(null);
                                }
                                if (!(!messageObject.isMegagroup() || messageObject.replyMessageObject == null || messageObject.replyMessageObject.messageOwner == null)) {
                                    Message message = messageObject.replyMessageObject.messageOwner;
                                    message.flags |= Integer.MIN_VALUE;
                                }
                            }
                        } else if (!(chatActivity.inlineReturn == 0 || messageObject.messageOwner.reply_markup == null)) {
                            for (intValue = 0; intValue < messageObject.messageOwner.reply_markup.rows.size(); intValue++) {
                                TL_keyboardButtonRow tL_keyboardButtonRow = (TL_keyboardButtonRow) messageObject.messageOwner.reply_markup.rows.get(intValue);
                                for (i7 = 0; i7 < tL_keyboardButtonRow.buttons.size(); i7++) {
                                    KeyboardButton keyboardButton = (KeyboardButton) tL_keyboardButtonRow.buttons.get(i7);
                                    if (keyboardButton instanceof TL_keyboardButtonSwitchInline) {
                                        processSwitchButton((TL_keyboardButtonSwitchInline) keyboardButton);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                final Bundle bundle;
                final BaseFragment baseFragment;
                if (chatActivity.forwardEndReached[0]) {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("received new messages ");
                        stringBuilder.append(arrayList3.size());
                        stringBuilder.append(" in dialog ");
                        stringBuilder.append(chatActivity.dialog_id);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    clientUserId = 0;
                    HashMap hashMap = null;
                    longSparseArray = null;
                    obj2 = null;
                    r8 = null;
                    r9 = null;
                    r13 = 1;
                    while (clientUserId < arrayList3.size()) {
                        int i14;
                        ArrayList arrayList4;
                        MessageObject messageObject7 = (MessageObject) arrayList3.get(clientUserId);
                        if (chatActivity.currentUser != null && ((chatActivity.currentUser.bot && messageObject7.isOut()) || chatActivity.currentUser.id == i3)) {
                            messageObject7.setIsRead();
                        }
                        if (!(chatActivity.avatarContainer == null || chatActivity.currentEncryptedChat == null || messageObject7.messageOwner.action == null || !(messageObject7.messageOwner.action instanceof TL_messageEncryptedAction) || !(messageObject7.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL))) {
                            chatActivity.avatarContainer.setTime(((TL_decryptedMessageActionSetMessageTTL) messageObject7.messageOwner.action.encryptedAction).ttl_seconds);
                        }
                        if (messageObject7.type >= 0) {
                            if (chatActivity.messagesDict[0].indexOfKey(messageObject7.getId()) < 0) {
                                MessageObject messageObject8;
                                if (clientUserId == 0 && messageObject7.messageOwner.id < 0 && messageObject7.type == 5) {
                                    chatActivity.animatingMessageObjects.add(messageObject7);
                                }
                                if (messageObject7.hasValidGroupId()) {
                                    r11 = (GroupedMessages) chatActivity.groupedMessagesMap.get(messageObject7.getGroupId());
                                    if (r11 == null) {
                                        r11 = new GroupedMessages();
                                        r11.groupId = messageObject7.getGroupId();
                                        chatActivity.groupedMessagesMap.put(r11.groupId, r11);
                                    }
                                    groupedMessages = r11;
                                    if (longSparseArray == null) {
                                        longSparseArray = new LongSparseArray();
                                    }
                                    longSparseArray.put(groupedMessages.groupId, groupedMessages);
                                    groupedMessages.messages.add(messageObject7);
                                } else {
                                    groupedMessages = null;
                                }
                                if (groupedMessages != null) {
                                    r12 = groupedMessages.messages.size() > 1 ? (MessageObject) groupedMessages.messages.get(groupedMessages.messages.size() - 2) : null;
                                    if (r12 != null) {
                                        size = chatActivity.messages.indexOf(r12);
                                        r11 = -1;
                                        if (size != r11) {
                                            if (messageObject7.messageOwner.id >= 0) {
                                                if (chatActivity.messages.isEmpty()) {
                                                    r11 = chatActivity.messages.size();
                                                    i4 = 0;
                                                    while (i4 < r11) {
                                                        messageObject8 = (MessageObject) chatActivity.messages.get(i4);
                                                        i14 = i3;
                                                        if (messageObject8.type >= 0 || messageObject8.messageOwner.date <= 0) {
                                                            arrayList4 = arrayList3;
                                                        } else {
                                                            if (messageObject8.messageOwner.id <= 0 || messageObject7.messageOwner.id <= 0) {
                                                                arrayList4 = arrayList3;
                                                            } else {
                                                                arrayList4 = arrayList3;
                                                                if (messageObject8.messageOwner.id < messageObject7.messageOwner.id) {
                                                                    if (messageObject8.getGroupId() != 0) {
                                                                        groupedMessages = (GroupedMessages) chatActivity.groupedMessagesMap.get(messageObject8.getGroupId());
                                                                        if (groupedMessages != null) {
                                                                        }
                                                                        if (groupedMessages != null) {
                                                                            size = i4;
                                                                        } else {
                                                                            size = chatActivity.messages.indexOf(groupedMessages.messages.get(groupedMessages.messages.size() - 1));
                                                                        }
                                                                        if (size == -1 || size > chatActivity.messages.size()) {
                                                                            size = chatActivity.messages.size();
                                                                        }
                                                                    }
                                                                    groupedMessages = null;
                                                                    if (groupedMessages != null) {
                                                                        size = chatActivity.messages.indexOf(groupedMessages.messages.get(groupedMessages.messages.size() - 1));
                                                                    } else {
                                                                        size = i4;
                                                                    }
                                                                    size = chatActivity.messages.size();
                                                                }
                                                            }
                                                            if (messageObject8.messageOwner.date < messageObject7.messageOwner.date) {
                                                                if (messageObject8.getGroupId() != 0) {
                                                                    groupedMessages = (GroupedMessages) chatActivity.groupedMessagesMap.get(messageObject8.getGroupId());
                                                                    if (groupedMessages != null) {
                                                                    }
                                                                    if (groupedMessages != null) {
                                                                        size = i4;
                                                                    } else {
                                                                        size = chatActivity.messages.indexOf(groupedMessages.messages.get(groupedMessages.messages.size() - 1));
                                                                    }
                                                                    size = chatActivity.messages.size();
                                                                }
                                                                groupedMessages = null;
                                                                if (groupedMessages != null) {
                                                                    size = chatActivity.messages.indexOf(groupedMessages.messages.get(groupedMessages.messages.size() - 1));
                                                                } else {
                                                                    size = i4;
                                                                }
                                                                size = chatActivity.messages.size();
                                                            }
                                                        }
                                                        i4++;
                                                        i3 = i14;
                                                        arrayList3 = arrayList4;
                                                    }
                                                    i14 = i3;
                                                    arrayList4 = arrayList3;
                                                    size = chatActivity.messages.size();
                                                }
                                            }
                                            i14 = i3;
                                            arrayList4 = arrayList3;
                                            size = 0;
                                        } else {
                                            i14 = i3;
                                            arrayList4 = arrayList3;
                                        }
                                        if (chatActivity.currentEncryptedChat != null && (messageObject7.messageOwner.media instanceof TL_messageMediaWebPage) && (messageObject7.messageOwner.media.webpage instanceof TL_webPageUrlPending)) {
                                            if (hashMap == null) {
                                                hashMap = new HashMap();
                                            }
                                            arrayList = (ArrayList) hashMap.get(messageObject7.messageOwner.media.webpage.url);
                                            if (arrayList == null) {
                                                arrayList = new ArrayList();
                                                hashMap.put(messageObject7.messageOwner.media.webpage.url, arrayList);
                                            }
                                            arrayList.add(messageObject7);
                                        }
                                        messageObject7.checkLayout();
                                        if (messageObject7.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                                            if (chatActivity.currentChat != null && chatActivity.currentChat.megagroup && ((messageObject7.messageOwner.action instanceof TL_messageActionChatAddUser) || (messageObject7.messageOwner.action instanceof TL_messageActionChatDeleteUser))) {
                                                r9 = 1;
                                            }
                                            if (chatActivity.minDate[0] == 0 || messageObject7.messageOwner.date < chatActivity.minDate[0]) {
                                                chatActivity.minDate[0] = messageObject7.messageOwner.date;
                                            }
                                            if (messageObject7.isOut()) {
                                                removeUnreadPlane(true);
                                                obj2 = 1;
                                            }
                                            if (messageObject7.getId() <= 0) {
                                                i4 = 0;
                                                chatActivity.maxMessageId[0] = Math.min(messageObject7.getId(), chatActivity.maxMessageId[0]);
                                                chatActivity.minMessageId[0] = Math.max(messageObject7.getId(), chatActivity.minMessageId[0]);
                                            } else {
                                                i4 = 0;
                                                if (chatActivity.currentEncryptedChat != null) {
                                                    chatActivity.maxMessageId[0] = Math.max(messageObject7.getId(), chatActivity.maxMessageId[0]);
                                                    chatActivity.minMessageId[0] = Math.min(messageObject7.getId(), chatActivity.minMessageId[0]);
                                                }
                                            }
                                            chatActivity.maxDate[i4] = Math.max(chatActivity.maxDate[i4], messageObject7.messageOwner.date);
                                            chatActivity.messagesDict[i4].put(messageObject7.getId(), messageObject7);
                                            arrayList = (ArrayList) chatActivity.messagesByDays.get(messageObject7.dateKey);
                                            if (size > chatActivity.messages.size()) {
                                                size = chatActivity.messages.size();
                                            }
                                            if (arrayList == null) {
                                                arrayList = new ArrayList();
                                                chatActivity.messagesByDays.put(messageObject7.dateKey, arrayList);
                                                tL_message = new TL_message();
                                                tL_message.message = LocaleController.formatDateChat((long) messageObject7.messageOwner.date);
                                                tL_message.id = 0;
                                                tL_message.date = messageObject7.messageOwner.date;
                                                messageObject3 = new MessageObject(chatActivity.currentAccount, tL_message, false);
                                                messageObject3.type = 10;
                                                messageObject3.contentType = 1;
                                                messageObject3.isDateObject = true;
                                                chatActivity.messages.add(size, messageObject3);
                                                if (chatActivity.chatAdapter != null) {
                                                    chatActivity.chatAdapter.notifyItemInserted(size);
                                                }
                                            }
                                            if (!messageObject7.isOut()) {
                                                if (chatActivity.paused && size == 0) {
                                                    if (!(chatActivity.scrollToTopUnReadOnResume || chatActivity.unreadMessageObject == null)) {
                                                        removeMessageObject(chatActivity.unreadMessageObject);
                                                        if (size > 0) {
                                                            size--;
                                                        }
                                                        chatActivity.unreadMessageObject = null;
                                                    }
                                                    if (chatActivity.unreadMessageObject == null) {
                                                        tL_message = new TL_message();
                                                        tL_message.message = TtmlNode.ANONYMOUS_REGION_ID;
                                                        tL_message.id = 0;
                                                        messageObject4 = new MessageObject(chatActivity.currentAccount, tL_message, false);
                                                        messageObject4.type = 6;
                                                        messageObject4.contentType = 2;
                                                        chatActivity.messages.add(0, messageObject4);
                                                        if (chatActivity.chatAdapter != null) {
                                                            chatActivity.chatAdapter.notifyItemInserted(0);
                                                        }
                                                        chatActivity.unreadMessageObject = messageObject4;
                                                        chatActivity.scrollToMessage = chatActivity.unreadMessageObject;
                                                        chatActivity.scrollToMessagePosition = -10000;
                                                        chatActivity.scrollToTopUnReadOnResume = true;
                                                        r13 = null;
                                                        if (chatActivity.unreadMessageObject != null) {
                                                            r13 = 1;
                                                        }
                                                    }
                                                }
                                                if (chatActivity.unreadMessageObject != null) {
                                                    r13 = 1;
                                                }
                                            }
                                            arrayList.add(0, messageObject7);
                                            chatActivity.messages.add(size, messageObject7);
                                            if (chatActivity.chatAdapter != null) {
                                                chatActivity.chatAdapter.notifyItemChanged(size);
                                                chatActivity.chatAdapter.notifyItemInserted(size);
                                            }
                                            if (messageObject7.isOut() && messageObject7.messageOwner.mentioned && messageObject7.isContentUnread()) {
                                                size = 1;
                                                chatActivity.newMentionsCount++;
                                            } else {
                                                size = 1;
                                            }
                                            chatActivity.newUnreadMessageCount += size;
                                            if (messageObject7.type != 10 || messageObject7.type == 11) {
                                                r8 = 1;
                                                clientUserId++;
                                                i3 = i14;
                                                arrayList3 = arrayList4;
                                            } else {
                                                clientUserId++;
                                                i3 = i14;
                                                arrayList3 = arrayList4;
                                            }
                                        } else {
                                            bundle = new Bundle();
                                            bundle.putInt("chat_id", messageObject7.messageOwner.action.channel_id);
                                            baseFragment = chatActivity.parentLayout.fragmentsStack.size() <= 0 ? (BaseFragment) chatActivity.parentLayout.fragmentsStack.get(chatActivity.parentLayout.fragmentsStack.size() - 1) : null;
                                            clientUserId = messageObject7.messageOwner.action.channel_id;
                                            AndroidUtilities.runOnUIThread(new Runnable() {

                                                /* renamed from: org.telegram.ui.ChatActivity$85$1 */
                                                class C10521 implements Runnable {
                                                    C10521() {
                                                    }

                                                    public void run() {
                                                        MessagesController.getInstance(ChatActivity.this.currentAccount).loadFullChat(clientUserId, 0, true);
                                                    }
                                                }

                                                public void run() {
                                                    ActionBarLayout access$25300 = ChatActivity.this.parentLayout;
                                                    if (baseFragment != null) {
                                                        NotificationCenter.getInstance(ChatActivity.this.currentAccount).removeObserver(baseFragment, NotificationCenter.closeChats);
                                                    }
                                                    NotificationCenter.getInstance(ChatActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                    access$25300.presentFragment(new ChatActivity(bundle), true);
                                                    AndroidUtilities.runOnUIThread(new C10521(), 1000);
                                                }
                                            });
                                            if (longSparseArray != null) {
                                                for (i3 = 0; i3 < longSparseArray.size(); i3++) {
                                                    ((GroupedMessages) longSparseArray.valueAt(i3)).calculate();
                                                }
                                            }
                                            return;
                                        }
                                    }
                                }
                                r11 = -1;
                                size = -1;
                                if (size != r11) {
                                    i14 = i3;
                                    arrayList4 = arrayList3;
                                } else {
                                    if (messageObject7.messageOwner.id >= 0) {
                                        if (chatActivity.messages.isEmpty()) {
                                            r11 = chatActivity.messages.size();
                                            i4 = 0;
                                            while (i4 < r11) {
                                                messageObject8 = (MessageObject) chatActivity.messages.get(i4);
                                                i14 = i3;
                                                if (messageObject8.type >= 0) {
                                                }
                                                arrayList4 = arrayList3;
                                                i4++;
                                                i3 = i14;
                                                arrayList3 = arrayList4;
                                            }
                                            i14 = i3;
                                            arrayList4 = arrayList3;
                                            size = chatActivity.messages.size();
                                        }
                                    }
                                    i14 = i3;
                                    arrayList4 = arrayList3;
                                    size = 0;
                                }
                                if (hashMap == null) {
                                    hashMap = new HashMap();
                                }
                                arrayList = (ArrayList) hashMap.get(messageObject7.messageOwner.media.webpage.url);
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                    hashMap.put(messageObject7.messageOwner.media.webpage.url, arrayList);
                                }
                                arrayList.add(messageObject7);
                                messageObject7.checkLayout();
                                if (messageObject7.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                                    r9 = 1;
                                    chatActivity.minDate[0] = messageObject7.messageOwner.date;
                                    if (messageObject7.isOut()) {
                                        removeUnreadPlane(true);
                                        obj2 = 1;
                                    }
                                    if (messageObject7.getId() <= 0) {
                                        i4 = 0;
                                        if (chatActivity.currentEncryptedChat != null) {
                                            chatActivity.maxMessageId[0] = Math.max(messageObject7.getId(), chatActivity.maxMessageId[0]);
                                            chatActivity.minMessageId[0] = Math.min(messageObject7.getId(), chatActivity.minMessageId[0]);
                                        }
                                    } else {
                                        i4 = 0;
                                        chatActivity.maxMessageId[0] = Math.min(messageObject7.getId(), chatActivity.maxMessageId[0]);
                                        chatActivity.minMessageId[0] = Math.max(messageObject7.getId(), chatActivity.minMessageId[0]);
                                    }
                                    chatActivity.maxDate[i4] = Math.max(chatActivity.maxDate[i4], messageObject7.messageOwner.date);
                                    chatActivity.messagesDict[i4].put(messageObject7.getId(), messageObject7);
                                    arrayList = (ArrayList) chatActivity.messagesByDays.get(messageObject7.dateKey);
                                    if (size > chatActivity.messages.size()) {
                                        size = chatActivity.messages.size();
                                    }
                                    if (arrayList == null) {
                                        arrayList = new ArrayList();
                                        chatActivity.messagesByDays.put(messageObject7.dateKey, arrayList);
                                        tL_message = new TL_message();
                                        tL_message.message = LocaleController.formatDateChat((long) messageObject7.messageOwner.date);
                                        tL_message.id = 0;
                                        tL_message.date = messageObject7.messageOwner.date;
                                        messageObject3 = new MessageObject(chatActivity.currentAccount, tL_message, false);
                                        messageObject3.type = 10;
                                        messageObject3.contentType = 1;
                                        messageObject3.isDateObject = true;
                                        chatActivity.messages.add(size, messageObject3);
                                        if (chatActivity.chatAdapter != null) {
                                            chatActivity.chatAdapter.notifyItemInserted(size);
                                        }
                                    }
                                    if (!messageObject7.isOut()) {
                                        removeMessageObject(chatActivity.unreadMessageObject);
                                        if (size > 0) {
                                            size--;
                                        }
                                        chatActivity.unreadMessageObject = null;
                                        if (chatActivity.unreadMessageObject == null) {
                                            tL_message = new TL_message();
                                            tL_message.message = TtmlNode.ANONYMOUS_REGION_ID;
                                            tL_message.id = 0;
                                            messageObject4 = new MessageObject(chatActivity.currentAccount, tL_message, false);
                                            messageObject4.type = 6;
                                            messageObject4.contentType = 2;
                                            chatActivity.messages.add(0, messageObject4);
                                            if (chatActivity.chatAdapter != null) {
                                                chatActivity.chatAdapter.notifyItemInserted(0);
                                            }
                                            chatActivity.unreadMessageObject = messageObject4;
                                            chatActivity.scrollToMessage = chatActivity.unreadMessageObject;
                                            chatActivity.scrollToMessagePosition = -10000;
                                            chatActivity.scrollToTopUnReadOnResume = true;
                                            r13 = null;
                                            if (chatActivity.unreadMessageObject != null) {
                                                r13 = 1;
                                            }
                                        }
                                        if (chatActivity.unreadMessageObject != null) {
                                            r13 = 1;
                                        }
                                    }
                                    arrayList.add(0, messageObject7);
                                    chatActivity.messages.add(size, messageObject7);
                                    if (chatActivity.chatAdapter != null) {
                                        chatActivity.chatAdapter.notifyItemChanged(size);
                                        chatActivity.chatAdapter.notifyItemInserted(size);
                                    }
                                    if (messageObject7.isOut()) {
                                    }
                                    size = 1;
                                    chatActivity.newUnreadMessageCount += size;
                                    if (messageObject7.type != 10) {
                                    }
                                    r8 = 1;
                                    clientUserId++;
                                    i3 = i14;
                                    arrayList3 = arrayList4;
                                } else {
                                    bundle = new Bundle();
                                    bundle.putInt("chat_id", messageObject7.messageOwner.action.channel_id);
                                    if (chatActivity.parentLayout.fragmentsStack.size() <= 0) {
                                    }
                                    clientUserId = messageObject7.messageOwner.action.channel_id;
                                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                    if (longSparseArray != null) {
                                        for (i3 = 0; i3 < longSparseArray.size(); i3++) {
                                            ((GroupedMessages) longSparseArray.valueAt(i3)).calculate();
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                        i14 = i3;
                        arrayList4 = arrayList3;
                        clientUserId++;
                        i3 = i14;
                        arrayList3 = arrayList4;
                    }
                    if (hashMap != null) {
                        MessagesController.getInstance(chatActivity.currentAccount).reloadWebPages(chatActivity.dialog_id, hashMap);
                    }
                    if (longSparseArray != null) {
                        for (i3 = 0; i3 < longSparseArray.size(); i3++) {
                            r2 = (GroupedMessages) longSparseArray.valueAt(i3);
                            intValue4 = r2.posArray.size();
                            r2.calculate();
                            i5 = r2.posArray.size();
                            if (i5 - intValue4 > 0 && chatActivity.chatAdapter != null) {
                                clientUserId = chatActivity.messages.indexOf(r2.messages.get(r2.messages.size() - 1));
                                if (clientUserId >= 0) {
                                    chatActivity.chatAdapter.notifyItemRangeChanged(clientUserId, i5);
                                }
                            }
                        }
                    }
                    if (chatActivity.progressView != null) {
                        chatActivity.progressView.setVisibility(4);
                    }
                    if (chatActivity.chatAdapter == null) {
                        chatActivity.scrollToTopOnResume = true;
                    } else if (r13 != null) {
                        chatActivity.chatAdapter.updateRowWithMessageObject(chatActivity.unreadMessageObject);
                    }
                    if (chatActivity.chatListView == null || chatActivity.chatAdapter == null) {
                        chatActivity.scrollToTopOnResume = true;
                    } else {
                        size = chatActivity.chatLayoutManager.findFirstVisibleItemPosition();
                        if (size == -1) {
                            size = 0;
                        }
                        if (size != 0) {
                            if (obj2 == null) {
                                if (chatActivity.newUnreadMessageCount == 0 || chatActivity.pagedownButtonCounter == null) {
                                    z2 = true;
                                } else {
                                    chatActivity.pagedownButtonCounter.setVisibility(0);
                                    z2 = true;
                                    chatActivity.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(chatActivity.newUnreadMessageCount)}));
                                }
                                showPagedownButton(z2, z2);
                                if (chatActivity.newMentionsCount == 0 && chatActivity.mentiondownButtonCounter != null) {
                                    chatActivity.mentiondownButtonCounter.setVisibility(0);
                                    chatActivity.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(chatActivity.newMentionsCount)}));
                                    showMentiondownButton(true, true);
                                }
                            }
                        }
                        chatActivity.newUnreadMessageCount = 0;
                        if (!chatActivity.firstLoading) {
                            if (chatActivity.paused) {
                                chatActivity.scrollToTopOnResume = true;
                            } else {
                                chatActivity.forceScrollToTop = true;
                                moveScrollToLastMessage();
                            }
                        }
                        if (chatActivity.newMentionsCount == 0) {
                        }
                    }
                    r13 = r8;
                    obj2 = r9;
                } else {
                    intValue = chatActivity.currentEncryptedChat != null ? ConnectionsManager.DEFAULT_DATACENTER_ID : Integer.MIN_VALUE;
                    i5 = Integer.MIN_VALUE;
                    obj2 = null;
                    r13 = null;
                    for (clientUserId = 0; clientUserId < arrayList3.size(); clientUserId++) {
                        messageObject5 = (MessageObject) arrayList3.get(clientUserId);
                        if (chatActivity.currentUser != null && ((chatActivity.currentUser.bot && messageObject5.isOut()) || chatActivity.currentUser.id == i3)) {
                            messageObject5.setIsRead();
                        }
                        if (!(chatActivity.avatarContainer == null || chatActivity.currentEncryptedChat == null || messageObject5.messageOwner.action == null || !(messageObject5.messageOwner.action instanceof TL_messageEncryptedAction) || !(messageObject5.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL))) {
                            chatActivity.avatarContainer.setTime(((TL_decryptedMessageActionSetMessageTTL) messageObject5.messageOwner.action.encryptedAction).ttl_seconds);
                        }
                        if (messageObject5.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                            bundle = new Bundle();
                            bundle.putInt("chat_id", messageObject5.messageOwner.action.channel_id);
                            baseFragment = chatActivity.parentLayout.fragmentsStack.size() > 0 ? (BaseFragment) chatActivity.parentLayout.fragmentsStack.get(chatActivity.parentLayout.fragmentsStack.size() - 1) : null;
                            clientUserId = messageObject5.messageOwner.action.channel_id;
                            AndroidUtilities.runOnUIThread(new Runnable() {

                                /* renamed from: org.telegram.ui.ChatActivity$84$1 */
                                class C10511 implements Runnable {
                                    C10511() {
                                    }

                                    public void run() {
                                        MessagesController.getInstance(ChatActivity.this.currentAccount).loadFullChat(clientUserId, 0, true);
                                    }
                                }

                                public void run() {
                                    ActionBarLayout access$24900 = ChatActivity.this.parentLayout;
                                    if (baseFragment != null) {
                                        NotificationCenter.getInstance(ChatActivity.this.currentAccount).removeObserver(baseFragment, NotificationCenter.closeChats);
                                    }
                                    NotificationCenter.getInstance(ChatActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    access$24900.presentFragment(new ChatActivity(bundle), true);
                                    AndroidUtilities.runOnUIThread(new C10511(), 1000);
                                }
                            });
                            return;
                        }
                        if (chatActivity.currentChat != null && chatActivity.currentChat.megagroup && ((messageObject5.messageOwner.action instanceof TL_messageActionChatAddUser) || (messageObject5.messageOwner.action instanceof TL_messageActionChatDeleteUser))) {
                            obj2 = 1;
                        }
                        if (clientUserId == 0 && messageObject5.messageOwner.id < 0 && messageObject5.type == 5) {
                            chatActivity.needAnimateToMessage = messageObject5;
                        }
                        if (messageObject5.isOut() && messageObject5.isSending()) {
                            scrollToLastMessage(false);
                            return;
                        }
                        if (messageObject5.type >= 0) {
                            if (chatActivity.messagesDict[0].indexOfKey(messageObject5.getId()) < 0) {
                                messageObject5.checkLayout();
                                i5 = Math.max(i5, messageObject5.messageOwner.date);
                                if (messageObject5.getId() > 0) {
                                    intValue = Math.max(messageObject5.getId(), intValue);
                                    chatActivity.last_message_id = Math.max(chatActivity.last_message_id, messageObject5.getId());
                                } else if (chatActivity.currentEncryptedChat != null) {
                                    intValue = Math.min(messageObject5.getId(), intValue);
                                    chatActivity.last_message_id = Math.min(chatActivity.last_message_id, messageObject5.getId());
                                }
                                if (messageObject5.messageOwner.mentioned && messageObject5.isContentUnread()) {
                                    intValue3 = 1;
                                    chatActivity.newMentionsCount++;
                                } else {
                                    intValue3 = 1;
                                }
                                chatActivity.newUnreadMessageCount += intValue3;
                                if (messageObject5.type == 10 || messageObject5.type == 11) {
                                    r13 = 1;
                                }
                            }
                        }
                    }
                    if (chatActivity.newUnreadMessageCount == 0 || chatActivity.pagedownButtonCounter == null) {
                        clientUserId = 0;
                    } else {
                        clientUserId = 0;
                        chatActivity.pagedownButtonCounter.setVisibility(0);
                        chatActivity.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(chatActivity.newUnreadMessageCount)}));
                    }
                    if (!(chatActivity.newMentionsCount == 0 || chatActivity.mentiondownButtonCounter == null)) {
                        chatActivity.mentiondownButtonCounter.setVisibility(clientUserId);
                        chatActivity.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(chatActivity.newMentionsCount)}));
                        showMentiondownButton(true, true);
                    }
                    updateVisibleRows();
                }
                if (!(chatActivity.messages.isEmpty() || chatActivity.botUser == null || chatActivity.botUser.length() != 0)) {
                    chatActivity.botUser = null;
                    updateBottomOverlay();
                }
                if (r13 != null) {
                    updateTitle();
                    checkAndUpdateAvatar();
                }
                if (obj2 != null) {
                    MessagesController.getInstance(chatActivity.currentAccount).loadFullChat(chatActivity.currentChat.id, 0, true);
                }
            }
        } else if (i3 == NotificationCenter.closeChats) {
            if (objArr2 == null || objArr2.length <= 0) {
                removeSelfFromStack();
            } else if (((Long) objArr2[0]).longValue() == chatActivity.dialog_id) {
                finishFragment();
            }
        } else if (i3 == NotificationCenter.messagesRead) {
            int i15;
            SparseLongArray sparseLongArray = (SparseLongArray) objArr2[0];
            SparseLongArray sparseLongArray2 = (SparseLongArray) objArr2[1];
            if (sparseLongArray != null) {
                clientUserId = sparseLongArray.size();
                i5 = 0;
                while (i5 < clientUserId) {
                    intValue = sparseLongArray.keyAt(i5);
                    long j = sparseLongArray.get(intValue);
                    if (((long) intValue) != chatActivity.dialog_id) {
                        i5++;
                    } else {
                        i3 = chatActivity.messages.size();
                        i15 = 0;
                        for (clientUserId = 0; clientUserId < i3; clientUserId++) {
                            messageObject = (MessageObject) chatActivity.messages.get(clientUserId);
                            if (!messageObject.isOut() && messageObject.getId() > 0 && messageObject.getId() <= ((int) j)) {
                                if (!messageObject.isUnread()) {
                                    break;
                                }
                                messageObject.setIsRead();
                                chatActivity.newUnreadMessageCount--;
                                i15 = 1;
                            }
                        }
                        i3 = 0;
                        removeUnreadPlane(false);
                        if (i15 != 0) {
                            if (chatActivity.newUnreadMessageCount < 0) {
                                chatActivity.newUnreadMessageCount = i3;
                            }
                            if (chatActivity.pagedownButtonCounter != null) {
                                chatActivity.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(chatActivity.newUnreadMessageCount)}));
                                if (chatActivity.newUnreadMessageCount > 0) {
                                    if (chatActivity.pagedownButtonCounter.getVisibility() != 4) {
                                        chatActivity.pagedownButtonCounter.setVisibility(4);
                                    }
                                } else if (chatActivity.pagedownButtonCounter.getVisibility() != 0) {
                                    chatActivity.pagedownButtonCounter.setVisibility(0);
                                }
                            }
                        }
                        if (sparseLongArray2 != null) {
                            i3 = sparseLongArray2.size();
                            clientUserId = 0;
                            while (clientUserId < i3) {
                                i5 = sparseLongArray2.keyAt(clientUserId);
                                intValue = (int) sparseLongArray2.get(i5);
                                if (((long) i5) == chatActivity.dialog_id) {
                                    clientUserId++;
                                } else {
                                    i3 = chatActivity.messages.size();
                                    for (clientUserId = 0; clientUserId < i3; clientUserId++) {
                                        playingMessageObject = (MessageObject) chatActivity.messages.get(clientUserId);
                                        if (playingMessageObject.isOut() && playingMessageObject.getId() > 0 && playingMessageObject.getId() <= intValue) {
                                            if (!playingMessageObject.isUnread()) {
                                                break;
                                            }
                                            playingMessageObject.setIsRead();
                                            i15 = 1;
                                        }
                                    }
                                }
                            }
                        }
                        if (i15 != 0) {
                            updateVisibleRows();
                        }
                    }
                }
            }
            i3 = 0;
            i15 = 0;
            if (i15 != 0) {
                if (chatActivity.newUnreadMessageCount < 0) {
                    chatActivity.newUnreadMessageCount = i3;
                }
                if (chatActivity.pagedownButtonCounter != null) {
                    chatActivity.pagedownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(chatActivity.newUnreadMessageCount)}));
                    if (chatActivity.newUnreadMessageCount > 0) {
                        if (chatActivity.pagedownButtonCounter.getVisibility() != 0) {
                            chatActivity.pagedownButtonCounter.setVisibility(0);
                        }
                    } else if (chatActivity.pagedownButtonCounter.getVisibility() != 4) {
                        chatActivity.pagedownButtonCounter.setVisibility(4);
                    }
                }
            }
            if (sparseLongArray2 != null) {
                i3 = sparseLongArray2.size();
                clientUserId = 0;
                while (clientUserId < i3) {
                    i5 = sparseLongArray2.keyAt(clientUserId);
                    intValue = (int) sparseLongArray2.get(i5);
                    if (((long) i5) == chatActivity.dialog_id) {
                        i3 = chatActivity.messages.size();
                        for (clientUserId = 0; clientUserId < i3; clientUserId++) {
                            playingMessageObject = (MessageObject) chatActivity.messages.get(clientUserId);
                            if (!playingMessageObject.isUnread()) {
                                break;
                            }
                            playingMessageObject.setIsRead();
                            i15 = 1;
                        }
                    } else {
                        clientUserId++;
                    }
                }
            }
            if (i15 != 0) {
                updateVisibleRows();
            }
        } else if (i3 == NotificationCenter.historyCleared) {
            if (((Long) objArr2[0]).longValue() == chatActivity.dialog_id) {
                i3 = ((Integer) objArr2[1]).intValue();
                clientUserId = 0;
                r13 = null;
                while (clientUserId < chatActivity.messages.size()) {
                    playingMessageObject = (MessageObject) chatActivity.messages.get(clientUserId);
                    i5 = playingMessageObject.getId();
                    if (i5 > 0) {
                        if (i5 <= i3) {
                            if (chatActivity.info == null || chatActivity.info.pinned_msg_id != i5) {
                                i6 = 0;
                            } else {
                                chatActivity.pinnedMessageObject = null;
                                i6 = 0;
                                chatActivity.info.pinned_msg_id = 0;
                                MessagesStorage.getInstance(chatActivity.currentAccount).updateChannelPinnedMessage(chatActivity.info.id, 0);
                                updatePinnedMessageView(true);
                            }
                            chatActivity.messages.remove(clientUserId);
                            clientUserId--;
                            chatActivity.messagesDict[i6].remove(i5);
                            r4 = (ArrayList) chatActivity.messagesByDays.get(playingMessageObject.dateKey);
                            if (r4 != null) {
                                r4.remove(playingMessageObject);
                                if (r4.isEmpty()) {
                                    chatActivity.messagesByDays.remove(playingMessageObject.dateKey);
                                    if (clientUserId >= 0 && clientUserId < chatActivity.messages.size()) {
                                        chatActivity.messages.remove(clientUserId);
                                        clientUserId--;
                                    }
                                }
                            }
                            intValue4 = 1;
                            r13 = 1;
                            clientUserId += intValue4;
                        }
                    }
                    intValue4 = 1;
                    clientUserId += intValue4;
                }
                if (chatActivity.messages.isEmpty()) {
                    if (chatActivity.endReached[0] || chatActivity.loading) {
                        if (chatActivity.botButtons != null) {
                            chatActivity.botButtons = null;
                            if (chatActivity.chatActivityEnterView != null) {
                                chatActivity.chatActivityEnterView.setButtons(null, false);
                            }
                        }
                        if (chatActivity.currentEncryptedChat == null && chatActivity.currentUser != null && chatActivity.currentUser.bot && chatActivity.botUser == null) {
                            chatActivity.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                            updateBottomOverlay();
                        }
                    } else {
                        if (chatActivity.progressView != null) {
                            chatActivity.progressView.setVisibility(4);
                        }
                        if (chatActivity.chatListView != null) {
                            chatActivity.chatListView.setEmptyView(null);
                        }
                        if (chatActivity.currentEncryptedChat == null) {
                            r1 = chatActivity.maxMessageId;
                            intValue4 = 1;
                            chatActivity.maxMessageId[1] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                            clientUserId = 0;
                            r1[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                            r1 = chatActivity.minMessageId;
                            intValue = Integer.MIN_VALUE;
                            chatActivity.minMessageId[1] = Integer.MIN_VALUE;
                            r1[0] = Integer.MIN_VALUE;
                        } else {
                            clientUserId = 0;
                            intValue4 = 1;
                            intValue = Integer.MIN_VALUE;
                            r1 = chatActivity.maxMessageId;
                            chatActivity.maxMessageId[1] = Integer.MIN_VALUE;
                            r1[0] = Integer.MIN_VALUE;
                            r1 = chatActivity.minMessageId;
                            chatActivity.minMessageId[1] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                            r1[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        }
                        r1 = chatActivity.maxDate;
                        chatActivity.maxDate[intValue4] = intValue;
                        r1[clientUserId] = intValue;
                        r1 = chatActivity.minDate;
                        chatActivity.minDate[intValue4] = clientUserId;
                        r1[clientUserId] = clientUserId;
                        chatActivity.waitingForLoad.add(Integer.valueOf(chatActivity.lastLoadIndex));
                        MessagesController instance2 = MessagesController.getInstance(chatActivity.currentAccount);
                        r1 = chatActivity.dialog_id;
                        boolean z10 = chatActivity.cacheEndReached[0] ^ 1;
                        int i16 = chatActivity.minDate[0];
                        intValue4 = chatActivity.classGuid;
                        boolean isChannel = ChatObject.isChannel(chatActivity.currentChat);
                        i5 = chatActivity.lastLoadIndex;
                        chatActivity.lastLoadIndex = i5 + 1;
                        instance2.loadMessages(r1, bot_help, 0, 0, z10, i16, intValue4, 0, 0, isChannel, i5);
                        chatActivity.loading = true;
                    }
                }
                if (!(r13 == null || chatActivity.chatAdapter == null)) {
                    removeUnreadPlane(true);
                    chatActivity.chatAdapter.notifyDataSetChanged();
                }
            }
        } else if (i3 == NotificationCenter.messagesDeleted) {
            LongSparseArray longSparseArray5;
            Integer num;
            MessageObject messageObject9;
            ArrayList arrayList5;
            int i17;
            GroupedMessages groupedMessages4;
            arrayList = (ArrayList) objArr2[0];
            clientUserId = ((Integer) objArr2[1]).intValue();
            if (ChatObject.isChannel(chatActivity.currentChat)) {
                if (clientUserId == 0 && chatActivity.mergeDialogId != 0) {
                    intValue4 = 1;
                    i5 = arrayList.size();
                    intValue = 0;
                    booleanValue = false;
                    longSparseArray5 = null;
                    r9 = null;
                    r13 = null;
                    while (intValue < i5) {
                        num = (Integer) arrayList.get(intValue);
                        messageObject9 = (MessageObject) chatActivity.messagesDict[intValue4].get(num.intValue());
                        if (intValue4 == 0 && chatActivity.info != null && chatActivity.info.pinned_msg_id == num.intValue()) {
                            chatActivity.pinnedMessageObject = null;
                            chatActivity.info.pinned_msg_id = 0;
                            MessagesStorage.getInstance(chatActivity.currentAccount).updateChannelPinnedMessage(clientUserId, 0);
                            updatePinnedMessageView(true);
                        }
                        if (messageObject9 != null) {
                            size = chatActivity.messages.indexOf(messageObject9);
                            if (size != -1) {
                                if (chatActivity.selectedMessagesIds[intValue4].indexOfKey(num.intValue()) >= 0) {
                                    booleanValue = intValue != i5 + -1;
                                    addToSelectedMessages(messageObject9, false, booleanValue);
                                    r13 = 1;
                                }
                                r9 = (MessageObject) chatActivity.messages.remove(size);
                                if (r9.getGroupId() == 0) {
                                    arrayList5 = arrayList;
                                    i17 = clientUserId;
                                    groupedMessages4 = (GroupedMessages) chatActivity.groupedMessagesMap.get(r9.getGroupId());
                                    if (groupedMessages4 != null) {
                                        if (longSparseArray5 == null) {
                                            longSparseArray5 = new LongSparseArray();
                                        }
                                        longSparseArray5.put(groupedMessages4.groupId, groupedMessages4);
                                        groupedMessages4.messages.remove(messageObject9);
                                    }
                                } else {
                                    arrayList5 = arrayList;
                                    i17 = clientUserId;
                                }
                                chatActivity.messagesDict[intValue4].remove(num.intValue());
                                arrayList = (ArrayList) chatActivity.messagesByDays.get(messageObject9.dateKey);
                                if (arrayList != null) {
                                    arrayList.remove(messageObject9);
                                    if (arrayList.isEmpty()) {
                                        chatActivity.messagesByDays.remove(messageObject9.dateKey);
                                        if (size >= 0 && size < chatActivity.messages.size()) {
                                            chatActivity.messages.remove(size);
                                        }
                                    }
                                }
                                r9 = 1;
                                intValue++;
                                arrayList = arrayList5;
                                clientUserId = i17;
                            }
                        }
                        arrayList5 = arrayList;
                        i17 = clientUserId;
                        intValue++;
                        arrayList = arrayList5;
                        clientUserId = i17;
                    }
                    if (!(r13 == null || r6)) {
                        addToSelectedMessages(null, false, true);
                    }
                    if (longSparseArray5 != null) {
                        for (i3 = 0; i3 < longSparseArray5.size(); i3++) {
                            r2 = (GroupedMessages) longSparseArray5.valueAt(i3);
                            if (r2.messages.isEmpty()) {
                                r2.calculate();
                                intValue4 = chatActivity.messages.indexOf((MessageObject) r2.messages.get(r2.messages.size() - 1));
                                if (intValue4 >= 0 && chatActivity.chatAdapter != null) {
                                    chatActivity.chatAdapter.notifyItemRangeChanged(intValue4 + chatActivity.chatAdapter.messagesStartRow, r2.messages.size());
                                }
                            } else {
                                chatActivity.groupedMessagesMap.remove(r2.groupId);
                            }
                        }
                    }
                    if (chatActivity.messages.isEmpty()) {
                        if (!chatActivity.endReached[0] || chatActivity.loading) {
                            if (chatActivity.botButtons != null) {
                                chatActivity.botButtons = null;
                                if (chatActivity.chatActivityEnterView != null) {
                                    chatActivity.chatActivityEnterView.setButtons(null, false);
                                }
                            }
                            if (chatActivity.currentEncryptedChat == null && chatActivity.currentUser != null && chatActivity.currentUser.bot && chatActivity.botUser == null) {
                                chatActivity.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                                updateBottomOverlay();
                            }
                        } else {
                            if (chatActivity.progressView != null) {
                                chatActivity.progressView.setVisibility(4);
                            }
                            if (chatActivity.chatListView != null) {
                                chatActivity.chatListView.setEmptyView(null);
                            }
                            if (chatActivity.currentEncryptedChat == null) {
                                r1 = chatActivity.maxMessageId;
                                intValue4 = 1;
                                chatActivity.maxMessageId[1] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                clientUserId = 0;
                                r1[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                r1 = chatActivity.minMessageId;
                                intValue = Integer.MIN_VALUE;
                                chatActivity.minMessageId[1] = Integer.MIN_VALUE;
                                r1[0] = Integer.MIN_VALUE;
                            } else {
                                clientUserId = 0;
                                intValue4 = 1;
                                intValue = Integer.MIN_VALUE;
                                r1 = chatActivity.maxMessageId;
                                chatActivity.maxMessageId[1] = Integer.MIN_VALUE;
                                r1[0] = Integer.MIN_VALUE;
                                r1 = chatActivity.minMessageId;
                                chatActivity.minMessageId[1] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                r1[0] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                            }
                            r1 = chatActivity.maxDate;
                            chatActivity.maxDate[intValue4] = intValue;
                            r1[clientUserId] = intValue;
                            r1 = chatActivity.minDate;
                            chatActivity.minDate[intValue4] = clientUserId;
                            r1[clientUserId] = clientUserId;
                            chatActivity.waitingForLoad.add(Integer.valueOf(chatActivity.lastLoadIndex));
                            MessagesController instance3 = MessagesController.getInstance(chatActivity.currentAccount);
                            r1 = chatActivity.dialog_id;
                            z4 = chatActivity.cacheEndReached[0] ^ 1;
                            int i18 = chatActivity.minDate[0];
                            intValue4 = chatActivity.classGuid;
                            boolean isChannel2 = ChatObject.isChannel(chatActivity.currentChat);
                            i5 = chatActivity.lastLoadIndex;
                            chatActivity.lastLoadIndex = i5 + 1;
                            instance3.loadMessages(r1, bot_help, 0, 0, z4, i18, intValue4, 0, 0, isChannel2, i5);
                            chatActivity.loading = true;
                        }
                    }
                    if (chatActivity.chatAdapter != null) {
                        if (r9 == null) {
                            removeUnreadPlane(false);
                            i3 = chatActivity.chatListView.getChildCount();
                            for (clientUserId = 0; clientUserId < i3; clientUserId++) {
                                findViewByPosition = chatActivity.chatListView.getChildAt(clientUserId);
                                r12 = findViewByPosition instanceof ChatMessageCell ? ((ChatMessageCell) findViewByPosition).getMessageObject() : findViewByPosition instanceof ChatActionCell ? ((ChatActionCell) findViewByPosition).getMessageObject() : null;
                                if (r12 != null) {
                                    i5 = chatActivity.messages.indexOf(r12);
                                    if (i5 < 0) {
                                        i4 = chatActivity.chatAdapter.messagesStartRow + i5;
                                        size = findViewByPosition.getBottom();
                                        break;
                                    }
                                }
                            }
                            size = 0;
                            i4 = -1;
                            chatActivity.chatAdapter.notifyDataSetChanged();
                            if (i4 != -1) {
                                chatActivity.chatLayoutManager.scrollToPositionWithOffset(i4, (chatActivity.chatListView.getMeasuredHeight() - size) - chatActivity.chatListView.getPaddingBottom());
                            }
                        } else {
                            chatActivity.first_unread_id = 0;
                            chatActivity.last_message_id = 0;
                            chatActivity.createUnreadMessageAfterId = 0;
                            removeMessageObject(chatActivity.unreadMessageObject);
                            chatActivity.unreadMessageObject = null;
                            if (chatActivity.pagedownButtonCounter != null) {
                                chatActivity.pagedownButtonCounter.setVisibility(4);
                            }
                        }
                    }
                } else if (clientUserId != chatActivity.currentChat.id) {
                    return;
                }
            } else if (clientUserId != 0) {
                return;
            }
            intValue4 = 0;
            i5 = arrayList.size();
            intValue = 0;
            booleanValue = false;
            longSparseArray5 = null;
            r9 = null;
            r13 = null;
            while (intValue < i5) {
                num = (Integer) arrayList.get(intValue);
                messageObject9 = (MessageObject) chatActivity.messagesDict[intValue4].get(num.intValue());
                chatActivity.pinnedMessageObject = null;
                chatActivity.info.pinned_msg_id = 0;
                MessagesStorage.getInstance(chatActivity.currentAccount).updateChannelPinnedMessage(clientUserId, 0);
                updatePinnedMessageView(true);
                if (messageObject9 != null) {
                    size = chatActivity.messages.indexOf(messageObject9);
                    if (size != -1) {
                        if (chatActivity.selectedMessagesIds[intValue4].indexOfKey(num.intValue()) >= 0) {
                            if (intValue != i5 + -1) {
                            }
                            addToSelectedMessages(messageObject9, false, booleanValue);
                            r13 = 1;
                        }
                        r9 = (MessageObject) chatActivity.messages.remove(size);
                        if (r9.getGroupId() == 0) {
                            arrayList5 = arrayList;
                            i17 = clientUserId;
                        } else {
                            arrayList5 = arrayList;
                            i17 = clientUserId;
                            groupedMessages4 = (GroupedMessages) chatActivity.groupedMessagesMap.get(r9.getGroupId());
                            if (groupedMessages4 != null) {
                                if (longSparseArray5 == null) {
                                    longSparseArray5 = new LongSparseArray();
                                }
                                longSparseArray5.put(groupedMessages4.groupId, groupedMessages4);
                                groupedMessages4.messages.remove(messageObject9);
                            }
                        }
                        chatActivity.messagesDict[intValue4].remove(num.intValue());
                        arrayList = (ArrayList) chatActivity.messagesByDays.get(messageObject9.dateKey);
                        if (arrayList != null) {
                            arrayList.remove(messageObject9);
                            if (arrayList.isEmpty()) {
                                chatActivity.messagesByDays.remove(messageObject9.dateKey);
                                chatActivity.messages.remove(size);
                            }
                        }
                        r9 = 1;
                        intValue++;
                        arrayList = arrayList5;
                        clientUserId = i17;
                    }
                }
                arrayList5 = arrayList;
                i17 = clientUserId;
                intValue++;
                arrayList = arrayList5;
                clientUserId = i17;
            }
            addToSelectedMessages(null, false, true);
            if (longSparseArray5 != null) {
                for (i3 = 0; i3 < longSparseArray5.size(); i3++) {
                    r2 = (GroupedMessages) longSparseArray5.valueAt(i3);
                    if (r2.messages.isEmpty()) {
                        r2.calculate();
                        intValue4 = chatActivity.messages.indexOf((MessageObject) r2.messages.get(r2.messages.size() - 1));
                        chatActivity.chatAdapter.notifyItemRangeChanged(intValue4 + chatActivity.chatAdapter.messagesStartRow, r2.messages.size());
                    } else {
                        chatActivity.groupedMessagesMap.remove(r2.groupId);
                    }
                }
            }
            if (chatActivity.messages.isEmpty()) {
                if (chatActivity.endReached[0]) {
                }
                if (chatActivity.botButtons != null) {
                    chatActivity.botButtons = null;
                    if (chatActivity.chatActivityEnterView != null) {
                        chatActivity.chatActivityEnterView.setButtons(null, false);
                    }
                }
                chatActivity.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                updateBottomOverlay();
            }
            if (chatActivity.chatAdapter != null) {
                if (r9 == null) {
                    chatActivity.first_unread_id = 0;
                    chatActivity.last_message_id = 0;
                    chatActivity.createUnreadMessageAfterId = 0;
                    removeMessageObject(chatActivity.unreadMessageObject);
                    chatActivity.unreadMessageObject = null;
                    if (chatActivity.pagedownButtonCounter != null) {
                        chatActivity.pagedownButtonCounter.setVisibility(4);
                    }
                } else {
                    removeUnreadPlane(false);
                    i3 = chatActivity.chatListView.getChildCount();
                    for (clientUserId = 0; clientUserId < i3; clientUserId++) {
                        findViewByPosition = chatActivity.chatListView.getChildAt(clientUserId);
                        if (findViewByPosition instanceof ChatMessageCell) {
                        }
                        if (r12 != null) {
                            i5 = chatActivity.messages.indexOf(r12);
                            if (i5 < 0) {
                                i4 = chatActivity.chatAdapter.messagesStartRow + i5;
                                size = findViewByPosition.getBottom();
                                break;
                            }
                        }
                    }
                    size = 0;
                    i4 = -1;
                    chatActivity.chatAdapter.notifyDataSetChanged();
                    if (i4 != -1) {
                        chatActivity.chatLayoutManager.scrollToPositionWithOffset(i4, (chatActivity.chatListView.getMeasuredHeight() - size) - chatActivity.chatListView.getPaddingBottom());
                    }
                }
            }
        } else if (i3 == NotificationCenter.messageReceivedByServer) {
            r1 = (Integer) objArr2[0];
            playingMessageObject = (MessageObject) chatActivity.messagesDict[0].get(r1.intValue());
            if (playingMessageObject != null) {
                Integer num2 = (Integer) objArr2[1];
                if (num2.equals(r1) || chatActivity.messagesDict[0].indexOfKey(num2.intValue()) < 0) {
                    tL_message2 = (Message) objArr2[2];
                    if (tL_message2 != null) {
                        try {
                            Object obj3;
                            r12 = (!playingMessageObject.isForwarded() || ((playingMessageObject.messageOwner.reply_markup != null || tL_message2.reply_markup == null) && playingMessageObject.messageOwner.message.equals(tL_message2.message))) ? null : 1;
                            if (r12 == null) {
                                try {
                                    if (playingMessageObject.messageOwner.params == null || !playingMessageObject.messageOwner.params.containsKey("query_id")) {
                                        if (tL_message2.media == null || playingMessageObject.messageOwner.media == null || tL_message2.media.getClass().equals(playingMessageObject.messageOwner.media.getClass())) {
                                            z2 = false;
                                            obj3 = r12;
                                            z = z2;
                                            obj = obj3;
                                            if (!(playingMessageObject.getGroupId() == 0 || tL_message2.grouped_id == 0)) {
                                                groupedMessages2 = (GroupedMessages) chatActivity.groupedMessagesMap.get(playingMessageObject.getGroupId());
                                                if (groupedMessages2 != null) {
                                                    chatActivity.groupedMessagesMap.put(tL_message2.grouped_id, groupedMessages2);
                                                }
                                            }
                                            playingMessageObject.messageOwner = tL_message2;
                                            playingMessageObject.generateThumbs(true);
                                            playingMessageObject.setType();
                                            if (tL_message2.media instanceof TL_messageMediaGame) {
                                                playingMessageObject.applyNewText();
                                            }
                                        }
                                    }
                                } catch (Throwable e) {
                                    th = e;
                                    FileLog.m3e(th);
                                    obj = r12;
                                    z = false;
                                    groupedMessages2 = (GroupedMessages) chatActivity.groupedMessagesMap.get(playingMessageObject.getGroupId());
                                    if (groupedMessages2 != null) {
                                        chatActivity.groupedMessagesMap.put(tL_message2.grouped_id, groupedMessages2);
                                    }
                                    playingMessageObject.messageOwner = tL_message2;
                                    playingMessageObject.generateThumbs(true);
                                    playingMessageObject.setType();
                                    if (tL_message2.media instanceof TL_messageMediaGame) {
                                        playingMessageObject.applyNewText();
                                    }
                                    if (obj != null) {
                                        playingMessageObject.measureInlineBotButtons();
                                    }
                                    chatActivity.messagesDict[0].remove(r1.intValue());
                                    chatActivity.messagesDict[0].put(num2.intValue(), playingMessageObject);
                                    playingMessageObject.messageOwner.id = num2.intValue();
                                    playingMessageObject.messageOwner.send_state = 0;
                                    playingMessageObject.forceUpdate = z;
                                    arrayList = new ArrayList();
                                    arrayList.add(playingMessageObject);
                                    if (chatActivity.currentEncryptedChat == null) {
                                        DataQuery.getInstance(chatActivity.currentAccount).loadReplyMessagesForMessages(arrayList, chatActivity.dialog_id);
                                    }
                                    if (chatActivity.chatAdapter != null) {
                                        chatActivity.chatAdapter.updateRowWithMessageObject(playingMessageObject);
                                    }
                                    moveScrollToLastMessage();
                                    NotificationsController.getInstance(chatActivity.currentAccount).playOutChatSound();
                                }
                            }
                            z2 = true;
                            obj3 = r12;
                            z = z2;
                            obj = obj3;
                        } catch (Throwable e2) {
                            th = e2;
                            r12 = null;
                            FileLog.m3e(th);
                            obj = r12;
                            z = false;
                            groupedMessages2 = (GroupedMessages) chatActivity.groupedMessagesMap.get(playingMessageObject.getGroupId());
                            if (groupedMessages2 != null) {
                                chatActivity.groupedMessagesMap.put(tL_message2.grouped_id, groupedMessages2);
                            }
                            playingMessageObject.messageOwner = tL_message2;
                            playingMessageObject.generateThumbs(true);
                            playingMessageObject.setType();
                            if (tL_message2.media instanceof TL_messageMediaGame) {
                                playingMessageObject.applyNewText();
                            }
                            if (obj != null) {
                                playingMessageObject.measureInlineBotButtons();
                            }
                            chatActivity.messagesDict[0].remove(r1.intValue());
                            chatActivity.messagesDict[0].put(num2.intValue(), playingMessageObject);
                            playingMessageObject.messageOwner.id = num2.intValue();
                            playingMessageObject.messageOwner.send_state = 0;
                            playingMessageObject.forceUpdate = z;
                            arrayList = new ArrayList();
                            arrayList.add(playingMessageObject);
                            if (chatActivity.currentEncryptedChat == null) {
                                DataQuery.getInstance(chatActivity.currentAccount).loadReplyMessagesForMessages(arrayList, chatActivity.dialog_id);
                            }
                            if (chatActivity.chatAdapter != null) {
                                chatActivity.chatAdapter.updateRowWithMessageObject(playingMessageObject);
                            }
                            moveScrollToLastMessage();
                            NotificationsController.getInstance(chatActivity.currentAccount).playOutChatSound();
                        }
                        groupedMessages2 = (GroupedMessages) chatActivity.groupedMessagesMap.get(playingMessageObject.getGroupId());
                        if (groupedMessages2 != null) {
                            chatActivity.groupedMessagesMap.put(tL_message2.grouped_id, groupedMessages2);
                        }
                        playingMessageObject.messageOwner = tL_message2;
                        playingMessageObject.generateThumbs(true);
                        playingMessageObject.setType();
                        if (tL_message2.media instanceof TL_messageMediaGame) {
                            playingMessageObject.applyNewText();
                        }
                    } else {
                        obj = null;
                        z = false;
                    }
                    if (obj != null) {
                        playingMessageObject.measureInlineBotButtons();
                    }
                    chatActivity.messagesDict[0].remove(r1.intValue());
                    chatActivity.messagesDict[0].put(num2.intValue(), playingMessageObject);
                    playingMessageObject.messageOwner.id = num2.intValue();
                    playingMessageObject.messageOwner.send_state = 0;
                    playingMessageObject.forceUpdate = z;
                    arrayList = new ArrayList();
                    arrayList.add(playingMessageObject);
                    if (chatActivity.currentEncryptedChat == null) {
                        DataQuery.getInstance(chatActivity.currentAccount).loadReplyMessagesForMessages(arrayList, chatActivity.dialog_id);
                    }
                    if (chatActivity.chatAdapter != null) {
                        chatActivity.chatAdapter.updateRowWithMessageObject(playingMessageObject);
                    }
                    if (chatActivity.chatLayoutManager != null && z && chatActivity.chatLayoutManager.findFirstVisibleItemPosition() == 0) {
                        moveScrollToLastMessage();
                    }
                    NotificationsController.getInstance(chatActivity.currentAccount).playOutChatSound();
                } else {
                    messageObject = (MessageObject) chatActivity.messagesDict[0].get(r1.intValue());
                    chatActivity.messagesDict[0].remove(r1.intValue());
                    if (messageObject != null) {
                        i3 = chatActivity.messages.indexOf(messageObject);
                        chatActivity.messages.remove(i3);
                        arrayList2 = (ArrayList) chatActivity.messagesByDays.get(messageObject.dateKey);
                        arrayList2.remove(playingMessageObject);
                        if (arrayList2.isEmpty()) {
                            chatActivity.messagesByDays.remove(playingMessageObject.dateKey);
                            if (i3 >= 0 && i3 < chatActivity.messages.size()) {
                                chatActivity.messages.remove(i3);
                            }
                        }
                        if (chatActivity.chatAdapter != null) {
                            chatActivity.chatAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } else if (i3 == NotificationCenter.messageReceivedByAck) {
            r1 = (MessageObject) chatActivity.messagesDict[0].get(((Integer) objArr2[0]).intValue());
            if (r1 != null) {
                r1.messageOwner.send_state = 0;
                if (chatActivity.chatAdapter != null) {
                    chatActivity.chatAdapter.updateRowWithMessageObject(r1);
                }
            }
        } else if (i3 == NotificationCenter.messageSendError) {
            r1 = (MessageObject) chatActivity.messagesDict[0].get(((Integer) objArr2[0]).intValue());
            if (r1 != null) {
                r1.messageOwner.send_state = 2;
                updateVisibleRows();
            }
        } else if (i3 == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr2[0];
            if (chatActivity.currentChat != null && chatFull.id == chatActivity.currentChat.id) {
                if (chatFull instanceof TL_channelFull) {
                    if (chatActivity.currentChat.megagroup) {
                        if (chatFull.participants != null) {
                            size = 0;
                            for (clientUserId = 0; clientUserId < chatFull.participants.participants.size(); clientUserId++) {
                                size = Math.max(((ChatParticipant) chatFull.participants.participants.get(clientUserId)).date, size);
                            }
                        } else {
                            size = 0;
                        }
                        if (size == 0 || Math.abs((System.currentTimeMillis() / 1000) - ((long) size)) > 3600) {
                            MessagesController.getInstance(chatActivity.currentAccount).loadChannelParticipants(Integer.valueOf(chatActivity.currentChat.id));
                        }
                    }
                    if (chatFull.participants == null && chatActivity.info != null) {
                        chatFull.participants = chatActivity.info.participants;
                    }
                }
                chatActivity.info = chatFull;
                if (chatActivity.chatActivityEnterView != null) {
                    chatActivity.chatActivityEnterView.setChatInfo(chatActivity.info);
                }
                if (chatActivity.mentionsAdapter != null) {
                    chatActivity.mentionsAdapter.setChatInfo(chatActivity.info);
                }
                if (objArr2[3] instanceof MessageObject) {
                    chatActivity.pinnedMessageObject = (MessageObject) objArr2[3];
                    updatePinnedMessageView(false);
                } else {
                    updatePinnedMessageView(true);
                }
                if (chatActivity.avatarContainer != null) {
                    chatActivity.avatarContainer.updateOnlineCount();
                    chatActivity.avatarContainer.updateSubtitle();
                }
                if (chatActivity.isBroadcast) {
                    SendMessagesHelper.getInstance(chatActivity.currentAccount).setCurrentChatInfo(chatActivity.info);
                }
                if (chatActivity.info instanceof TL_chatFull) {
                    chatActivity.hasBotsCommands = false;
                    chatActivity.botInfo.clear();
                    chatActivity.botsCount = 0;
                    URLSpanBotCommand.enabled = false;
                    for (i3 = 0; i3 < chatActivity.info.participants.participants.size(); i3++) {
                        User user2 = MessagesController.getInstance(chatActivity.currentAccount).getUser(Integer.valueOf(((ChatParticipant) chatActivity.info.participants.participants.get(i3)).user_id));
                        if (user2 != null && user2.bot) {
                            URLSpanBotCommand.enabled = true;
                            chatActivity.botsCount++;
                            DataQuery.getInstance(chatActivity.currentAccount).loadBotInfo(user2.id, true, chatActivity.classGuid);
                        }
                    }
                    if (chatActivity.chatListView != null) {
                        chatActivity.chatListView.invalidateViews();
                    }
                } else if (chatActivity.info instanceof TL_channelFull) {
                    chatActivity.hasBotsCommands = false;
                    chatActivity.botInfo.clear();
                    chatActivity.botsCount = 0;
                    z5 = (chatActivity.info.bot_info.isEmpty() || chatActivity.currentChat == null || !chatActivity.currentChat.megagroup) ? false : true;
                    URLSpanBotCommand.enabled = z5;
                    chatActivity.botsCount = chatActivity.info.bot_info.size();
                    for (i3 = 0; i3 < chatActivity.info.bot_info.size(); i3++) {
                        BotInfo botInfo = (BotInfo) chatActivity.info.bot_info.get(i3);
                        if (!botInfo.commands.isEmpty() && (!ChatObject.isChannel(chatActivity.currentChat) || (chatActivity.currentChat != null && chatActivity.currentChat.megagroup))) {
                            chatActivity.hasBotsCommands = true;
                        }
                        chatActivity.botInfo.put(botInfo.user_id, botInfo);
                    }
                    if (chatActivity.chatListView != null) {
                        chatActivity.chatListView.invalidateViews();
                    }
                    if (chatActivity.mentionsAdapter != null && (!ChatObject.isChannel(chatActivity.currentChat) || (chatActivity.currentChat != null && chatActivity.currentChat.megagroup))) {
                        chatActivity.mentionsAdapter.setBotInfo(chatActivity.botInfo);
                    }
                }
                if (chatActivity.chatActivityEnterView != null) {
                    chatActivity.chatActivityEnterView.setBotsCount(chatActivity.botsCount, chatActivity.hasBotsCommands);
                }
                if (chatActivity.mentionsAdapter != null) {
                    chatActivity.mentionsAdapter.setBotsCount(chatActivity.botsCount);
                }
                if (ChatObject.isChannel(chatActivity.currentChat) && chatActivity.mergeDialogId == 0 && chatActivity.info.migrated_from_chat_id != 0) {
                    chatActivity.mergeDialogId = (long) (-chatActivity.info.migrated_from_chat_id);
                    chatActivity.maxMessageId[1] = chatActivity.info.migrated_from_max_id;
                    if (chatActivity.chatAdapter != null) {
                        chatActivity.chatAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (i3 == NotificationCenter.chatInfoCantLoad) {
            i3 = ((Integer) objArr2[0]).intValue();
            if (chatActivity.currentChat != null && chatActivity.currentChat.id == i3) {
                i3 = ((Integer) objArr2[1]).intValue();
                if (getParentActivity() != null) {
                    if (chatActivity.closeChatDialog == null) {
                        r2 = new Builder(getParentActivity());
                        r2.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                        if (i3 == 0) {
                            r2.setMessage(LocaleController.getString("ChannelCantOpenPrivate", C0446R.string.ChannelCantOpenPrivate));
                        } else if (i3 == 1) {
                            r2.setMessage(LocaleController.getString("ChannelCantOpenNa", C0446R.string.ChannelCantOpenNa));
                        } else if (i3 == 2) {
                            r2.setMessage(LocaleController.getString("ChannelCantOpenBanned", C0446R.string.ChannelCantOpenBanned));
                        }
                        r2.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                        Dialog create = r2.create();
                        chatActivity.closeChatDialog = create;
                        showDialog(create);
                        chatActivity.loading = false;
                        if (chatActivity.progressView != null) {
                            chatActivity.progressView.setVisibility(4);
                        }
                        if (chatActivity.chatAdapter != null) {
                            chatActivity.chatAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } else if (i3 == NotificationCenter.contactsDidLoaded) {
            updateContactStatus();
            if (chatActivity.currentEncryptedChat != null) {
                updateSpamView();
            }
            if (chatActivity.avatarContainer != null) {
                chatActivity.avatarContainer.updateSubtitle();
            }
        } else if (i3 == NotificationCenter.encryptedChatUpdated) {
            EncryptedChat encryptedChat = (EncryptedChat) objArr2[0];
            if (chatActivity.currentEncryptedChat != null && encryptedChat.id == chatActivity.currentEncryptedChat.id) {
                chatActivity.currentEncryptedChat = encryptedChat;
                updateContactStatus();
                updateSecretStatus();
                initStickers();
                if (chatActivity.chatActivityEnterView != null) {
                    ChatActivityEnterView chatActivityEnterView = chatActivity.chatActivityEnterView;
                    if (chatActivity.currentEncryptedChat != null) {
                        if (AndroidUtilities.getPeerLayerVersion(chatActivity.currentEncryptedChat.layer) < edit) {
                            isUnread = false;
                            if (chatActivity.currentEncryptedChat != null) {
                                if (AndroidUtilities.getPeerLayerVersion(chatActivity.currentEncryptedChat.layer) >= 46) {
                                    z3 = false;
                                    chatActivityEnterView.setAllowStickersAndGifs(isUnread, z3);
                                    chatActivity.chatActivityEnterView.checkRoundVideo();
                                }
                            }
                            z3 = true;
                            chatActivityEnterView.setAllowStickersAndGifs(isUnread, z3);
                            chatActivity.chatActivityEnterView.checkRoundVideo();
                        }
                    }
                    isUnread = true;
                    if (chatActivity.currentEncryptedChat != null) {
                        if (AndroidUtilities.getPeerLayerVersion(chatActivity.currentEncryptedChat.layer) >= 46) {
                            z3 = false;
                            chatActivityEnterView.setAllowStickersAndGifs(isUnread, z3);
                            chatActivity.chatActivityEnterView.checkRoundVideo();
                        }
                    }
                    z3 = true;
                    chatActivityEnterView.setAllowStickersAndGifs(isUnread, z3);
                    chatActivity.chatActivityEnterView.checkRoundVideo();
                }
                if (chatActivity.mentionsAdapter != null) {
                    MentionsAdapter mentionsAdapter = chatActivity.mentionsAdapter;
                    isUnread = !chatActivity.chatActivityEnterView.isEditingMessage() && (chatActivity.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(chatActivity.currentEncryptedChat.layer) >= 46);
                    mentionsAdapter.setNeedBotContext(isUnread);
                }
            }
        } else if (i3 == NotificationCenter.messagesReadEncrypted) {
            i3 = ((Integer) objArr2[0]).intValue();
            if (chatActivity.currentEncryptedChat != null && chatActivity.currentEncryptedChat.id == i3) {
                i3 = ((Integer) objArr2[1]).intValue();
                Iterator it = chatActivity.messages.iterator();
                while (it.hasNext()) {
                    playingMessageObject = (MessageObject) it.next();
                    if (playingMessageObject.isOut()) {
                        if (playingMessageObject.isOut() && !playingMessageObject.isUnread()) {
                            break;
                        } else if (playingMessageObject.messageOwner.date - 1 <= i3) {
                            playingMessageObject.setIsRead();
                        }
                    }
                }
                updateVisibleRows();
            }
        } else if (i3 == NotificationCenter.removeAllMessagesFromDialog) {
            if (chatActivity.dialog_id == ((Long) objArr2[0]).longValue()) {
                chatActivity.messages.clear();
                chatActivity.waitingForLoad.clear();
                chatActivity.messagesByDays.clear();
                chatActivity.groupedMessagesMap.clear();
                for (i3 = 1; i3 >= 0; i3--) {
                    chatActivity.messagesDict[i3].clear();
                    if (chatActivity.currentEncryptedChat == null) {
                        chatActivity.maxMessageId[i3] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        i5 = Integer.MIN_VALUE;
                        chatActivity.minMessageId[i3] = Integer.MIN_VALUE;
                    } else {
                        i5 = Integer.MIN_VALUE;
                        chatActivity.maxMessageId[i3] = Integer.MIN_VALUE;
                        chatActivity.minMessageId[i3] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    }
                    chatActivity.maxDate[i3] = i5;
                    chatActivity.minDate[i3] = 0;
                    chatActivity.selectedMessagesIds[i3].clear();
                    chatActivity.selectedMessagesCanCopyIds[i3].clear();
                    chatActivity.selectedMessagesCanStarIds[i3].clear();
                }
                chatActivity.cantDeleteMessagesCount = 0;
                chatActivity.canEditMessagesCount = 0;
                chatActivity.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                if (chatActivity.botButtons != null) {
                    chatActivity.botButtons = null;
                    if (chatActivity.chatActivityEnterView != null) {
                        chatActivity.chatActivityEnterView.setButtons(null, false);
                    }
                }
                if (((Boolean) objArr2[1]).booleanValue()) {
                    if (chatActivity.chatAdapter != null) {
                        chatActivity.progressView.setVisibility(chatActivity.chatAdapter.botInfoRow == -1 ? 0 : 4);
                        chatActivity.chatListView.setEmptyView(null);
                    }
                    for (i3 = 0; i3 < 2; i3++) {
                        chatActivity.endReached[i3] = false;
                        chatActivity.cacheEndReached[i3] = false;
                        chatActivity.forwardEndReached[i3] = true;
                    }
                    chatActivity.first = true;
                    chatActivity.firstLoading = true;
                    chatActivity.loading = true;
                    chatActivity.startLoadFromMessageId = 0;
                    chatActivity.needSelectFromMessageId = false;
                    chatActivity.waitingForLoad.add(Integer.valueOf(chatActivity.lastLoadIndex));
                    MessagesController instance4 = MessagesController.getInstance(chatActivity.currentAccount);
                    r9 = chatActivity.dialog_id;
                    r11 = AndroidUtilities.isTablet() ? bot_help : 20;
                    i3 = chatActivity.classGuid;
                    boolean isChannel3 = ChatObject.isChannel(chatActivity.currentChat);
                    clientUserId = chatActivity.lastLoadIndex;
                    chatActivity.lastLoadIndex = clientUserId + 1;
                    instance4.loadMessages(r9, r11, 0, 0, true, 0, i3, 2, 0, isChannel3, clientUserId);
                } else if (chatActivity.progressView != null) {
                    chatActivity.progressView.setVisibility(4);
                    chatActivity.chatListView.setEmptyView(chatActivity.emptyViewContainer);
                }
                if (chatActivity.chatAdapter != null) {
                    chatActivity.chatAdapter.notifyDataSetChanged();
                }
                if (chatActivity.currentEncryptedChat == null && chatActivity.currentUser != null && chatActivity.currentUser.bot && chatActivity.botUser == null) {
                    chatActivity.botUser = TtmlNode.ANONYMOUS_REGION_ID;
                    updateBottomOverlay();
                }
            }
        } else {
            intValue4 = -1;
            if (i3 == NotificationCenter.screenshotTook) {
                updateInformationForScreenshotDetector();
            } else if (i3 == NotificationCenter.blockedUsersDidLoaded) {
                if (chatActivity.currentUser != null) {
                    z5 = chatActivity.userBlocked;
                    chatActivity.userBlocked = MessagesController.getInstance(chatActivity.currentAccount).blockedUsers.contains(Integer.valueOf(chatActivity.currentUser.id));
                    if (z5 != chatActivity.userBlocked) {
                        updateBottomOverlay();
                    }
                }
            } else if (i3 == NotificationCenter.FileNewChunkAvailable) {
                r1 = (MessageObject) objArr2[0];
                long longValue = ((Long) objArr2[3]).longValue();
                if (longValue != 0 && chatActivity.dialog_id == r1.getDialogId()) {
                    r1 = (MessageObject) chatActivity.messagesDict[0].get(r1.getId());
                    if (r1 != null) {
                        r1.messageOwner.media.document.size = (int) longValue;
                        updateVisibleRows();
                    }
                }
            } else if (i3 == NotificationCenter.didCreatedNewDeleteTask) {
                SparseArray sparseArray = (SparseArray) objArr2[0];
                clientUserId = 0;
                Object obj4 = null;
                while (clientUserId < sparseArray.size()) {
                    intValue = sparseArray.keyAt(clientUserId);
                    ArrayList arrayList6 = (ArrayList) sparseArray.get(intValue);
                    r8 = obj4;
                    for (intValue4 = 0; intValue4 < arrayList6.size(); intValue4++) {
                        r9 = ((Long) arrayList6.get(intValue4)).longValue();
                        if (intValue4 == 0) {
                            size = (int) (r9 >> 32);
                            if (size < 0) {
                                size = 0;
                            }
                            if (size != (ChatObject.isChannel(chatActivity.currentChat) ? chatActivity.currentChat.id : 0)) {
                                return;
                            }
                        }
                        r9 = (MessageObject) chatActivity.messagesDict[0].get((int) r9);
                        if (r9 != null) {
                            r9.messageOwner.destroyTime = intValue;
                            r8 = 1;
                        }
                    }
                    clientUserId++;
                    obj4 = r8;
                }
                if (obj4 != null) {
                    updateVisibleRows();
                }
            } else if (i3 == NotificationCenter.messagePlayingDidStarted) {
                r1 = (MessageObject) objArr2[0];
                if (r1.eventId == 0) {
                    sendSecretMessageRead(r1);
                    if (r1.isRoundVideo()) {
                        MediaController.getInstance().setTextureView(createTextureView(true), chatActivity.aspectRatioFrameLayout, chatActivity.roundVideoContainer, true);
                        updateTextureViewPosition();
                    }
                    if (chatActivity.chatListView != null) {
                        i3 = chatActivity.chatListView.getChildCount();
                        for (clientUserId = 0; clientUserId < i3; clientUserId++) {
                            findViewByPosition = chatActivity.chatListView.getChildAt(clientUserId);
                            if (findViewByPosition instanceof ChatMessageCell) {
                                r3 = (ChatMessageCell) findViewByPosition;
                                messageObject = r3.getMessageObject();
                                if (messageObject != null) {
                                    if (!messageObject.isVoice()) {
                                        if (!messageObject.isMusic()) {
                                            if (messageObject.isRoundVideo()) {
                                                r3.checkRoundVideoPlayback(false);
                                                if (!(MediaController.getInstance().isPlayingMessage(messageObject) || messageObject.audioProgress == 0.0f)) {
                                                    messageObject.resetPlayingProgress();
                                                    r3.invalidate();
                                                }
                                            }
                                        }
                                    }
                                    r3.updateButtonState(false);
                                }
                            }
                        }
                        i3 = chatActivity.mentionListView.getChildCount();
                        for (clientUserId = 0; clientUserId < i3; clientUserId++) {
                            findViewByPosition = chatActivity.mentionListView.getChildAt(clientUserId);
                            if (findViewByPosition instanceof ContextLinkCell) {
                                r3 = (ContextLinkCell) findViewByPosition;
                                messageObject = r3.getMessageObject();
                                if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                                    r3.updateButtonState(false);
                                }
                            }
                        }
                    }
                }
            } else {
                if (i3 != NotificationCenter.messagePlayingDidReset) {
                    if (i3 != NotificationCenter.messagePlayingPlayStateChanged) {
                        if (i3 == NotificationCenter.messagePlayingProgressDidChanged) {
                            r1 = (Integer) objArr2[0];
                            if (chatActivity.chatListView != null) {
                                clientUserId = chatActivity.chatListView.getChildCount();
                                for (intValue4 = 0; intValue4 < clientUserId; intValue4++) {
                                    View childAt = chatActivity.chatListView.getChildAt(intValue4);
                                    if (childAt instanceof ChatMessageCell) {
                                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                                        MessageObject messageObject10 = chatMessageCell.getMessageObject();
                                        if (messageObject10 != null && messageObject10.getId() == r1.intValue()) {
                                            r1 = MediaController.getInstance().getPlayingMessageObject();
                                            if (r1 != null) {
                                                messageObject10.audioProgress = r1.audioProgress;
                                                messageObject10.audioProgressSec = r1.audioProgressSec;
                                                messageObject10.audioPlayerDuration = r1.audioPlayerDuration;
                                                chatMessageCell.updatePlayingMessageProgress();
                                                if (chatActivity.drawLaterRoundProgressCell == chatMessageCell) {
                                                    chatActivity.fragmentView.invalidate();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (i3 == NotificationCenter.updateMessageMedia) {
                            Message message2 = (Message) objArr2[0];
                            playingMessageObject = (MessageObject) chatActivity.messagesDict[0].get(message2.id);
                            if (playingMessageObject != null) {
                                playingMessageObject.messageOwner.media = message2.media;
                                playingMessageObject.messageOwner.attachPath = message2.attachPath;
                                playingMessageObject.generateThumbs(false);
                                if (playingMessageObject.getGroupId() != 0 && (playingMessageObject.photoThumbs == null || playingMessageObject.photoThumbs.isEmpty())) {
                                    r2 = (GroupedMessages) chatActivity.groupedMessagesMap.get(playingMessageObject.getGroupId());
                                    if (r2 != null) {
                                        i5 = r2.messages.indexOf(playingMessageObject);
                                        if (i5 >= 0) {
                                            intValue = r2.messages.size();
                                            if (i5 <= 0 || i5 >= r2.messages.size() - 1) {
                                                r12 = null;
                                            } else {
                                                groupedMessages2 = new GroupedMessages();
                                                groupedMessages2.groupId = Utilities.random.nextLong();
                                                intValue3 = i5 + 1;
                                                groupedMessages2.messages.addAll(r2.messages.subList(intValue3, r2.messages.size()));
                                                for (i7 = 0; i7 < groupedMessages2.messages.size(); i7++) {
                                                    ((MessageObject) groupedMessages2.messages.get(i7)).localGroupId = groupedMessages2.groupId;
                                                    r2.messages.remove(intValue3);
                                                }
                                                chatActivity.groupedMessagesMap.put(groupedMessages2.groupId, groupedMessages2);
                                                r12 = (MessageObject) groupedMessages2.messages.get(groupedMessages2.messages.size() - 1);
                                                groupedMessages2.calculate();
                                            }
                                            r2.messages.remove(i5);
                                            if (r2.messages.isEmpty()) {
                                                chatActivity.groupedMessagesMap.remove(r2.groupId);
                                            } else {
                                                if (r12 == null) {
                                                    r12 = (MessageObject) r2.messages.get(r2.messages.size() - 1);
                                                }
                                                r2.calculate();
                                                clientUserId = chatActivity.messages.indexOf(r12);
                                                if (clientUserId >= 0 && chatActivity.chatAdapter != null) {
                                                    chatActivity.chatAdapter.notifyItemRangeChanged(clientUserId + chatActivity.chatAdapter.messagesStartRow, intValue);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (message2.media.ttl_seconds == 0 || !((message2.media.photo instanceof TL_photoEmpty) || (message2.media.document instanceof TL_documentEmpty))) {
                                    updateVisibleRows();
                                } else {
                                    playingMessageObject.setType();
                                    chatActivity.chatAdapter.updateRowWithMessageObject(playingMessageObject);
                                }
                            }
                        } else if (i3 == NotificationCenter.replaceMessagesObjects) {
                            r1 = ((Long) objArr2[0]).longValue();
                            if (r1 == chatActivity.dialog_id || r1 == chatActivity.mergeDialogId) {
                                i3 = r1 == chatActivity.dialog_id ? 0 : 1;
                                r4 = (ArrayList) objArr2[1];
                                clientUserId = 0;
                                longSparseArray = null;
                                r13 = null;
                                while (clientUserId < r4.size()) {
                                    ArrayList arrayList7;
                                    int i19;
                                    messageObject2 = (MessageObject) r4.get(clientUserId);
                                    messageObject5 = (MessageObject) chatActivity.messagesDict[i3].get(messageObject2.getId());
                                    if (chatActivity.pinnedMessageObject != null && chatActivity.pinnedMessageObject.getId() == messageObject2.getId()) {
                                        chatActivity.pinnedMessageObject = messageObject2;
                                        updatePinnedMessageView(true);
                                    }
                                    if (messageObject5 != null) {
                                        if (messageObject2.type >= 0) {
                                            if (r13 == null && (messageObject2.messageOwner.media instanceof TL_messageMediaWebPage)) {
                                                r13 = 1;
                                            }
                                            if (messageObject5.replyMessageObject != null) {
                                                messageObject2.replyMessageObject = messageObject5.replyMessageObject;
                                                if (messageObject2.messageOwner.action instanceof TL_messageActionGameScore) {
                                                    messageObject2.generateGameMessageText(null);
                                                } else if (messageObject2.messageOwner.action instanceof TL_messageActionPaymentSent) {
                                                    messageObject2.generatePaymentSentMessageText(null);
                                                }
                                            }
                                            messageObject2.messageOwner.attachPath = messageObject5.messageOwner.attachPath;
                                            messageObject2.attachPathExists = messageObject5.attachPathExists;
                                            messageObject2.mediaExists = messageObject5.mediaExists;
                                            chatActivity.messagesDict[i3].put(messageObject5.getId(), messageObject2);
                                        } else {
                                            chatActivity.messagesDict[i3].remove(messageObject5.getId());
                                        }
                                        intValue2 = chatActivity.messages.indexOf(messageObject5);
                                        Object obj5;
                                        if (intValue2 >= 0) {
                                            int i20;
                                            ArrayList arrayList8 = (ArrayList) chatActivity.messagesByDays.get(messageObject5.dateKey);
                                            i4 = arrayList8 != null ? arrayList8.indexOf(messageObject5) : intValue4;
                                            if (messageObject5.getGroupId() != 0) {
                                                arrayList7 = r4;
                                                GroupedMessages groupedMessages5 = (GroupedMessages) chatActivity.groupedMessagesMap.get(messageObject5.getGroupId());
                                                if (groupedMessages5 != null) {
                                                    i5 = groupedMessages5.messages.indexOf(messageObject5);
                                                    if (i5 >= 0) {
                                                        if (messageObject5.getGroupId() != messageObject2.getGroupId()) {
                                                            obj5 = r13;
                                                            chatActivity.groupedMessagesMap.put(messageObject2.getGroupId(), groupedMessages5);
                                                        } else {
                                                            obj5 = r13;
                                                        }
                                                        if (messageObject2.photoThumbs != null) {
                                                            if (!messageObject2.photoThumbs.isEmpty()) {
                                                                groupedMessages5.messages.set(i5, messageObject2);
                                                                GroupedMessagePosition groupedMessagePosition = (GroupedMessagePosition) groupedMessages5.positions.remove(messageObject5);
                                                                if (groupedMessagePosition != null) {
                                                                    groupedMessages5.positions.put(messageObject2, groupedMessagePosition);
                                                                }
                                                                if (longSparseArray == null) {
                                                                    longSparseArray = new LongSparseArray();
                                                                }
                                                                longSparseArray.put(groupedMessages5.groupId, groupedMessages5);
                                                                i19 = i3;
                                                            }
                                                        }
                                                        if (longSparseArray == null) {
                                                            longSparseArray = new LongSparseArray();
                                                        }
                                                        longSparseArray.put(groupedMessages5.groupId, groupedMessages5);
                                                        if (i5 <= 0 || i5 >= groupedMessages5.messages.size() - 1) {
                                                            i19 = i3;
                                                            i20 = i4;
                                                        } else {
                                                            r11 = new GroupedMessages();
                                                            r11.groupId = Utilities.random.nextLong();
                                                            i8 = i5 + 1;
                                                            i19 = i3;
                                                            r11.messages.addAll(groupedMessages5.messages.subList(i8, groupedMessages5.messages.size()));
                                                            i3 = 0;
                                                            while (i3 < r11.messages.size()) {
                                                                i20 = i4;
                                                                ((MessageObject) r11.messages.get(i3)).localGroupId = r11.groupId;
                                                                groupedMessages5.messages.remove(i8);
                                                                i3++;
                                                                i4 = i20;
                                                            }
                                                            i20 = i4;
                                                            longSparseArray.put(r11.groupId, r11);
                                                            chatActivity.groupedMessagesMap.put(r11.groupId, r11);
                                                        }
                                                        groupedMessages5.messages.remove(i5);
                                                        if (messageObject2.type < 0) {
                                                            chatActivity.messages.set(intValue2, messageObject2);
                                                            if (chatActivity.chatAdapter != null) {
                                                                chatActivity.chatAdapter.updateRowAtPosition(chatActivity.chatAdapter.messagesStartRow + intValue2);
                                                            }
                                                            if (i20 >= 0) {
                                                                arrayList8.set(i20, messageObject2);
                                                            }
                                                        } else {
                                                            i4 = i20;
                                                            chatActivity.messages.remove(intValue2);
                                                            if (chatActivity.chatAdapter != null) {
                                                                chatActivity.chatAdapter.notifyItemRemoved(chatActivity.chatAdapter.messagesStartRow + intValue2);
                                                            }
                                                            if (i4 >= 0) {
                                                                arrayList8.remove(i4);
                                                                if (arrayList8.isEmpty()) {
                                                                    chatActivity.messagesByDays.remove(messageObject5.dateKey);
                                                                    chatActivity.messages.remove(intValue2);
                                                                    chatActivity.chatAdapter.notifyItemRemoved(chatActivity.chatAdapter.messagesStartRow);
                                                                }
                                                            }
                                                        }
                                                        r13 = obj5;
                                                    }
                                                }
                                                i19 = i3;
                                                obj5 = r13;
                                            } else {
                                                i19 = i3;
                                                arrayList7 = r4;
                                                obj5 = r13;
                                            }
                                            i20 = i4;
                                            if (messageObject2.type < 0) {
                                                i4 = i20;
                                                chatActivity.messages.remove(intValue2);
                                                if (chatActivity.chatAdapter != null) {
                                                    chatActivity.chatAdapter.notifyItemRemoved(chatActivity.chatAdapter.messagesStartRow + intValue2);
                                                }
                                                if (i4 >= 0) {
                                                    arrayList8.remove(i4);
                                                    if (arrayList8.isEmpty()) {
                                                        chatActivity.messagesByDays.remove(messageObject5.dateKey);
                                                        chatActivity.messages.remove(intValue2);
                                                        chatActivity.chatAdapter.notifyItemRemoved(chatActivity.chatAdapter.messagesStartRow);
                                                    }
                                                }
                                            } else {
                                                chatActivity.messages.set(intValue2, messageObject2);
                                                if (chatActivity.chatAdapter != null) {
                                                    chatActivity.chatAdapter.updateRowAtPosition(chatActivity.chatAdapter.messagesStartRow + intValue2);
                                                }
                                                if (i20 >= 0) {
                                                    arrayList8.set(i20, messageObject2);
                                                }
                                            }
                                            r13 = obj5;
                                        } else {
                                            i19 = i3;
                                            arrayList7 = r4;
                                            obj5 = r13;
                                        }
                                    } else {
                                        i19 = i3;
                                        arrayList7 = r4;
                                    }
                                    clientUserId++;
                                    r4 = arrayList7;
                                    i3 = i19;
                                    intValue4 = -1;
                                }
                                if (longSparseArray != null) {
                                    for (i3 = 0; i3 < longSparseArray.size(); i3++) {
                                        r2 = (GroupedMessages) longSparseArray.valueAt(i3);
                                        if (r2.messages.isEmpty()) {
                                            chatActivity.groupedMessagesMap.remove(r2.groupId);
                                        } else {
                                            r2.calculate();
                                            intValue4 = chatActivity.messages.indexOf((MessageObject) r2.messages.get(r2.messages.size() - 1));
                                            if (intValue4 >= 0 && chatActivity.chatAdapter != null) {
                                                chatActivity.chatAdapter.notifyItemRangeChanged(intValue4 + chatActivity.chatAdapter.messagesStartRow, r2.messages.size());
                                            }
                                        }
                                    }
                                }
                            } else {
                                return;
                            }
                        } else if (i3 == NotificationCenter.notificationsSettingsUpdated) {
                            updateTitleIcons();
                            if (ChatObject.isChannel(chatActivity.currentChat)) {
                                updateBottomOverlay();
                            }
                        } else if (i3 == NotificationCenter.didLoadedReplyMessages) {
                            if (((Long) objArr2[0]).longValue() == chatActivity.dialog_id) {
                                updateVisibleRows();
                            }
                        } else if (i3 == NotificationCenter.didLoadedPinnedMessage) {
                            r1 = (MessageObject) objArr2[0];
                            if (r1.getDialogId() == chatActivity.dialog_id && chatActivity.info != null && chatActivity.info.pinned_msg_id == r1.getId()) {
                                chatActivity.pinnedMessageObject = r1;
                                chatActivity.loadingPinnedMessage = 0;
                                updatePinnedMessageView(true);
                            }
                        } else if (i3 == NotificationCenter.didReceivedWebpages) {
                            arrayList = (ArrayList) objArr2[0];
                            r13 = null;
                            for (clientUserId = 0; clientUserId < arrayList.size(); clientUserId++) {
                                tL_message = (Message) arrayList.get(clientUserId);
                                long dialogId = MessageObject.getDialogId(tL_message);
                                if (dialogId == chatActivity.dialog_id || dialogId == chatActivity.mergeDialogId) {
                                    messageObject = (MessageObject) chatActivity.messagesDict[dialogId == chatActivity.dialog_id ? 0 : 1].get(tL_message.id);
                                    if (messageObject != null) {
                                        messageObject.messageOwner.media = new TL_messageMediaWebPage();
                                        messageObject.messageOwner.media.webpage = tL_message.media.webpage;
                                        messageObject.generateThumbs(true);
                                        r13 = 1;
                                    }
                                }
                            }
                            if (r13 != null) {
                                updateVisibleRows();
                            }
                        } else if (i3 == NotificationCenter.didReceivedWebpagesInUpdates) {
                            if (chatActivity.foundWebPage != null) {
                                LongSparseArray longSparseArray6 = (LongSparseArray) objArr2[0];
                                i3 = 0;
                                while (i3 < longSparseArray6.size()) {
                                    WebPage webPage = (WebPage) longSparseArray6.valueAt(i3);
                                    if (webPage.id == chatActivity.foundWebPage.id) {
                                        showReplyPanel(!(webPage instanceof TL_webPageEmpty), null, null, webPage, false);
                                    } else {
                                        i3++;
                                    }
                                }
                            }
                        } else if (i3 == NotificationCenter.messagesReadContent) {
                            arrayList = (ArrayList) objArr2[0];
                            size = ChatObject.isChannel(chatActivity.currentChat) ? chatActivity.currentChat.id : 0;
                            r13 = null;
                            for (clientUserId = 0; clientUserId < arrayList.size(); clientUserId++) {
                                long longValue2 = ((Long) arrayList.get(clientUserId)).longValue();
                                intValue4 = (int) (longValue2 >> 32);
                                if (intValue4 < 0) {
                                    intValue4 = 0;
                                }
                                if (intValue4 == size) {
                                    playingMessageObject = (MessageObject) chatActivity.messagesDict[0].get((int) longValue2);
                                    if (playingMessageObject != null) {
                                        playingMessageObject.setContentIsRead();
                                        if (playingMessageObject.messageOwner.mentioned) {
                                            chatActivity.newMentionsCount--;
                                            if (chatActivity.newMentionsCount <= 0) {
                                                chatActivity.newMentionsCount = 0;
                                                chatActivity.hasAllMentionsLocal = true;
                                                showMentiondownButton(false, true);
                                            } else {
                                                chatActivity.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(chatActivity.newMentionsCount)}));
                                            }
                                        }
                                        r13 = 1;
                                    }
                                }
                            }
                            if (r13 != null) {
                                updateVisibleRows();
                            }
                        } else if (i3 == NotificationCenter.botInfoDidLoaded) {
                            if (chatActivity.classGuid == ((Integer) objArr2[1]).intValue()) {
                                BotInfo botInfo2 = (BotInfo) objArr2[0];
                                if (chatActivity.currentEncryptedChat == null) {
                                    if (!(botInfo2.commands.isEmpty() || ChatObject.isChannel(chatActivity.currentChat))) {
                                        chatActivity.hasBotsCommands = true;
                                    }
                                    chatActivity.botInfo.put(botInfo2.user_id, botInfo2);
                                    if (chatActivity.chatAdapter != null) {
                                        chatActivity.chatAdapter.notifyItemChanged(chatActivity.chatAdapter.botInfoRow);
                                    }
                                    if (chatActivity.mentionsAdapter != null && (!ChatObject.isChannel(chatActivity.currentChat) || (chatActivity.currentChat != null && chatActivity.currentChat.megagroup))) {
                                        chatActivity.mentionsAdapter.setBotInfo(chatActivity.botInfo);
                                    }
                                    if (chatActivity.chatActivityEnterView != null) {
                                        chatActivity.chatActivityEnterView.setBotsCount(chatActivity.botsCount, chatActivity.hasBotsCommands);
                                    }
                                }
                                updateBotButtons();
                            }
                        } else if (i3 == NotificationCenter.botKeyboardDidLoaded) {
                            if (chatActivity.dialog_id == ((Long) objArr2[1]).longValue()) {
                                tL_message2 = (Message) objArr2[0];
                                if (tL_message2 == null || chatActivity.userBlocked) {
                                    chatActivity.botButtons = null;
                                    if (chatActivity.chatActivityEnterView != null) {
                                        if (chatActivity.replyingMessageObject != null && chatActivity.botReplyButtons == chatActivity.replyingMessageObject) {
                                            chatActivity.botReplyButtons = null;
                                            showReplyPanel(false, null, null, null, false);
                                        }
                                        chatActivity.chatActivityEnterView.setButtons(chatActivity.botButtons);
                                    }
                                } else {
                                    chatActivity.botButtons = new MessageObject(chatActivity.currentAccount, tL_message2, false);
                                    checkBotKeyboard();
                                }
                            }
                        } else if (i3 == NotificationCenter.chatSearchResultsAvailable) {
                            if (chatActivity.classGuid == ((Integer) objArr2[0]).intValue()) {
                                clientUserId = ((Integer) objArr2[1]).intValue();
                                long longValue3 = ((Long) objArr2[3]).longValue();
                                if (clientUserId != 0) {
                                    intValue2 = 2;
                                    scrollToMessageId(clientUserId, 0, true, longValue3 == chatActivity.dialog_id ? 0 : 1, false);
                                } else {
                                    intValue2 = 2;
                                }
                                updateSearchButtons(((Integer) objArr2[intValue2]).intValue(), ((Integer) objArr2[4]).intValue(), ((Integer) objArr2[5]).intValue());
                                if (chatActivity.searchItem != null) {
                                    chatActivity.searchItem.setShowSearchProgress(false);
                                }
                            }
                        } else if (i3 == NotificationCenter.chatSearchResultsLoading) {
                            if (chatActivity.classGuid == ((Integer) objArr2[0]).intValue() && chatActivity.searchItem != null) {
                                chatActivity.searchItem.setShowSearchProgress(true);
                            }
                        } else if (i3 == NotificationCenter.didUpdatedMessagesViews) {
                            SparseIntArray sparseIntArray = (SparseIntArray) ((SparseArray) objArr2[0]).get((int) chatActivity.dialog_id);
                            if (sparseIntArray != null) {
                                r13 = null;
                                for (clientUserId = 0; clientUserId < sparseIntArray.size(); clientUserId++) {
                                    intValue4 = sparseIntArray.keyAt(clientUserId);
                                    messageObject = (MessageObject) chatActivity.messagesDict[0].get(intValue4);
                                    if (messageObject != null) {
                                        intValue4 = sparseIntArray.get(intValue4);
                                        if (intValue4 > messageObject.messageOwner.views) {
                                            messageObject.messageOwner.views = intValue4;
                                            r13 = 1;
                                        }
                                    }
                                }
                                if (r13 != null) {
                                    updateVisibleRows();
                                }
                            }
                        } else if (i3 == NotificationCenter.peerSettingsDidLoaded) {
                            if (((Long) objArr2[0]).longValue() == chatActivity.dialog_id) {
                                updateSpamView();
                            }
                        } else if (i3 == NotificationCenter.newDraftReceived) {
                            if (((Long) objArr2[0]).longValue() == chatActivity.dialog_id) {
                                applyDraftMaybe(true);
                            }
                        } else if (i3 == NotificationCenter.userInfoDidLoaded) {
                            r1 = (Integer) objArr2[0];
                            if (chatActivity.currentUser != null && chatActivity.currentUser.id == r1.intValue()) {
                                TL_userFull tL_userFull = (TL_userFull) objArr2[1];
                                if (chatActivity.headerItem != null) {
                                    if (tL_userFull.phone_calls_available) {
                                        chatActivity.headerItem.showSubItem(32);
                                    } else {
                                        chatActivity.headerItem.hideSubItem(32);
                                    }
                                }
                            }
                        } else if (i3 == NotificationCenter.didSetNewWallpapper) {
                            if (chatActivity.fragmentView != null) {
                                ((SizeNotifierFrameLayout) chatActivity.fragmentView).setBackgroundImage(Theme.getCachedWallpaper());
                                chatActivity.progressView2.getBackground().setColorFilter(Theme.colorFilter);
                                if (chatActivity.emptyView != null) {
                                    chatActivity.emptyView.getBackground().setColorFilter(Theme.colorFilter);
                                }
                                if (chatActivity.bigEmptyView != null) {
                                    chatActivity.bigEmptyView.getBackground().setColorFilter(Theme.colorFilter);
                                }
                                chatActivity.chatListView.invalidateViews();
                            }
                        } else if (i3 == NotificationCenter.channelRightsUpdated) {
                            Chat chat4 = (Chat) objArr2[0];
                            if (!(chatActivity.currentChat == null || chat4.id != chatActivity.currentChat.id || chatActivity.chatActivityEnterView == null)) {
                                chatActivity.currentChat = chat4;
                                chatActivity.chatActivityEnterView.checkChannelRights();
                                checkRaiseSensors();
                                updateSecretStatus();
                            }
                        } else if (i3 == NotificationCenter.updateMentionsCount && chatActivity.dialog_id == ((Long) objArr2[0]).longValue()) {
                            clientUserId = ((Integer) objArr2[1]).intValue();
                            if (chatActivity.newMentionsCount > clientUserId) {
                                chatActivity.newMentionsCount = clientUserId;
                                if (chatActivity.newMentionsCount <= 0) {
                                    chatActivity.newMentionsCount = 0;
                                    chatActivity.hasAllMentionsLocal = true;
                                    showMentiondownButton(false, true);
                                } else {
                                    chatActivity.mentiondownButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(chatActivity.newMentionsCount)}));
                                }
                            }
                        }
                    }
                }
                if (i3 == NotificationCenter.messagePlayingDidReset) {
                    destroyTextureView();
                }
                if (chatActivity.chatListView != null) {
                    i3 = chatActivity.chatListView.getChildCount();
                    for (clientUserId = 0; clientUserId < i3; clientUserId++) {
                        findViewByPosition = chatActivity.chatListView.getChildAt(clientUserId);
                        if (findViewByPosition instanceof ChatMessageCell) {
                            r3 = (ChatMessageCell) findViewByPosition;
                            messageObject = r3.getMessageObject();
                            if (messageObject != null) {
                                if (!messageObject.isVoice()) {
                                    if (!messageObject.isMusic()) {
                                        if (messageObject.isRoundVideo() && !MediaController.getInstance().isPlayingMessage(messageObject)) {
                                            r3.checkRoundVideoPlayback(true);
                                        }
                                    }
                                }
                                r3.updateButtonState(false);
                            }
                        }
                    }
                    i3 = chatActivity.mentionListView.getChildCount();
                    for (clientUserId = 0; clientUserId < i3; clientUserId++) {
                        findViewByPosition = chatActivity.mentionListView.getChildAt(clientUserId);
                        if (findViewByPosition instanceof ContextLinkCell) {
                            r3 = (ContextLinkCell) findViewByPosition;
                            messageObject = r3.getMessageObject();
                            if (messageObject != null && (messageObject.isVoice() || messageObject.isMusic())) {
                                r3.updateButtonState(false);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean processSwitchButton(TL_keyboardButtonSwitchInline tL_keyboardButtonSwitchInline) {
        if (!(this.inlineReturn == 0 || tL_keyboardButtonSwitchInline.same_peer)) {
            if (this.parentLayout != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("@");
                stringBuilder.append(this.currentUser.username);
                stringBuilder.append(" ");
                stringBuilder.append(tL_keyboardButtonSwitchInline.query);
                CharSequence stringBuilder2 = stringBuilder.toString();
                if (this.inlineReturn == this.dialog_id) {
                    this.inlineReturn = 0;
                    this.chatActivityEnterView.setFieldText(stringBuilder2);
                } else {
                    DataQuery.getInstance(this.currentAccount).saveDraft(this.inlineReturn, stringBuilder2, null, null, false);
                    if (this.parentLayout.fragmentsStack.size() > 1) {
                        BaseFragment baseFragment = (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2);
                        if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).dialog_id == this.inlineReturn) {
                            finishFragment();
                        } else {
                            tL_keyboardButtonSwitchInline = new Bundle();
                            int i = (int) this.inlineReturn;
                            int i2 = (int) (this.inlineReturn >> 32);
                            if (i == 0) {
                                tL_keyboardButtonSwitchInline.putInt("enc_id", i2);
                            } else if (i > 0) {
                                tL_keyboardButtonSwitchInline.putInt("user_id", i);
                            } else if (i < 0) {
                                tL_keyboardButtonSwitchInline.putInt("chat_id", -i);
                            }
                            presentFragment(new ChatActivity(tL_keyboardButtonSwitchInline), true);
                        }
                    }
                }
                return true;
            }
        }
        return null;
    }

    private void updateSearchButtons(int i, int i2, int i3) {
        if (this.searchUpButton != null) {
            this.searchUpButton.setEnabled((i & 1) != 0);
            this.searchDownButton.setEnabled((i & 2) != 0 ? 1 : 0);
            float f = 0.5f;
            this.searchUpButton.setAlpha(this.searchUpButton.isEnabled() ? 1.0f : 0.5f);
            i = this.searchDownButton;
            if (this.searchDownButton.isEnabled()) {
                f = 1.0f;
            }
            i.setAlpha(f);
            if (i3 < 0) {
                this.searchCountText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            } else if (i3 == 0) {
                this.searchCountText.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
            } else {
                this.searchCountText.setText(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf(i2 + 1), Integer.valueOf(i3)));
            }
        }
    }

    public boolean needDelayOpenAnimation() {
        return this.firstLoading;
    }

    public void onTransitionAnimationStart(boolean z, boolean z2) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoaded, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoaded, NotificationCenter.botKeyboardDidLoaded});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        if (z) {
            this.openAnimationEnded = false;
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        if (z) {
            this.openAnimationEnded = true;
            if (this.currentUser) {
                MessagesController.getInstance(this.currentAccount).loadFullUser(this.currentUser, this.classGuid, false);
            }
            if (VERSION.SDK_INT >= true) {
                createChatAttachView();
            }
            if (this.chatActivityEnterView.hasRecordVideo() && !this.chatActivityEnterView.isSendButtonVisible()) {
                z2 = this.currentChat && ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup;
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                z2 = z2 ? "needShowRoundHintChannel" : "needShowRoundHint";
                if (globalMainSettings.getBoolean(z2, true) && Utilities.random.nextFloat() < true) {
                    showVoiceHint(false, this.chatActivityEnterView.isInVideoMode());
                    globalMainSettings.edit().putBoolean(z2, false).commit();
                }
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (this.closeChatDialog != null && dialog == this.closeChatDialog) {
            MessagesController.getInstance(this.currentAccount).deleteDialog(this.dialog_id, 0);
            if (this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() != null || this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this) {
                finishFragment();
                return;
            }
            BaseFragment baseFragment = (BaseFragment) this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1);
            removeSelfFromStack();
            baseFragment.finishFragment();
        }
    }

    public boolean extendActionMode(Menu menu) {
        if (!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isVisible()) {
            if (this.chatActivityEnterView.getSelectionLength() != 0) {
                if (menu.findItem(16908321) == null) {
                }
            }
            return true;
        } else if (PhotoViewer.getInstance().getSelectiongLength() == 0 || menu.findItem(16908321) == null) {
            return true;
        }
        if (VERSION.SDK_INT >= edit) {
            menu.removeItem(16908341);
        }
        CharSequence spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("Bold", C0446R.string.Bold));
        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, spannableStringBuilder.length(), 33);
        menu.add(C0446R.id.menu_groupbolditalic, C0446R.id.menu_bold, 6, spannableStringBuilder);
        spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("Italic", C0446R.string.Italic));
        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), 0, spannableStringBuilder.length(), 33);
        menu.add(C0446R.id.menu_groupbolditalic, C0446R.id.menu_italic, 7, spannableStringBuilder);
        menu.add(C0446R.id.menu_groupbolditalic, C0446R.id.menu_regular, 8, LocaleController.getString("Regular", C0446R.string.Regular));
        return true;
    }

    private void updateBottomOverlay() {
        if (this.bottomOverlayChatText != null) {
            if (this.currentChat != null) {
                if (!ChatObject.isChannel(this.currentChat) || (this.currentChat instanceof TL_channelForbidden)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisGroup", C0446R.string.DeleteThisGroup));
                } else if (ChatObject.isNotInChat(this.currentChat)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelJoin", C0446R.string.ChannelJoin));
                } else if (MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id)) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelUnmute", C0446R.string.ChannelUnmute));
                } else {
                    this.bottomOverlayChatText.setText(LocaleController.getString("ChannelMute", C0446R.string.ChannelMute));
                }
            } else if (this.userBlocked) {
                if (this.currentUser.bot) {
                    this.bottomOverlayChatText.setText(LocaleController.getString("BotUnblock", C0446R.string.BotUnblock));
                } else {
                    this.bottomOverlayChatText.setText(LocaleController.getString("Unblock", C0446R.string.Unblock));
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
                this.bottomOverlayChatText.setText(LocaleController.getString("DeleteThisChat", C0446R.string.DeleteThisChat));
            } else {
                this.bottomOverlayChatText.setText(LocaleController.getString("BotStart", C0446R.string.BotStart));
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
                    if (this.chatActivityEnterView.isEditingMessage()) {
                        this.chatActivityEnterView.setVisibility(0);
                        this.bottomOverlayChat.setVisibility(4);
                        this.chatActivityEnterView.setFieldFocused();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                ChatActivity.this.chatActivityEnterView.openKeyboard();
                            }
                        }, 100);
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

    public void showAlert(String str, String str2) {
        if (!(this.alertView == null || str == null)) {
            if (str2 != null) {
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
                        public void onAnimationEnd(Animator animator) {
                            if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animator) != null) {
                                ChatActivity.this.alertViewAnimator = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animator) != null) {
                                ChatActivity.this.alertViewAnimator = null;
                            }
                        }
                    });
                    this.alertViewAnimator.start();
                }
                this.alertNameTextView.setText(str);
                this.alertTextView.setText(Emoji.replaceEmoji(str2.replace('\n', ' '), this.alertTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                if (this.hideAlertViewRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.hideAlertViewRunnable);
                }
                str = new Runnable() {

                    /* renamed from: org.telegram.ui.ChatActivity$88$1 */
                    class C10531 extends AnimatorListenerAdapter {
                        C10531() {
                        }

                        public void onAnimationEnd(Animator animator) {
                            if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animator) != null) {
                                ChatActivity.this.alertView.setVisibility(8);
                                ChatActivity.this.alertViewAnimator = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (ChatActivity.this.alertViewAnimator != null && ChatActivity.this.alertViewAnimator.equals(animator) != null) {
                                ChatActivity.this.alertViewAnimator = null;
                            }
                        }
                    }

                    public void run() {
                        if (ChatActivity.this.hideAlertViewRunnable == this && ChatActivity.this.alertView.getTag() == null) {
                            ChatActivity.this.alertView.setTag(Integer.valueOf(1));
                            if (ChatActivity.this.alertViewAnimator != null) {
                                ChatActivity.this.alertViewAnimator.cancel();
                                ChatActivity.this.alertViewAnimator = null;
                            }
                            ChatActivity.this.alertViewAnimator = new AnimatorSet();
                            AnimatorSet access$25700 = ChatActivity.this.alertViewAnimator;
                            Animator[] animatorArr = new Animator[1];
                            animatorArr[0] = ObjectAnimator.ofFloat(ChatActivity.this.alertView, "translationY", new float[]{(float) (-AndroidUtilities.dp(50.0f))});
                            access$25700.playTogether(animatorArr);
                            ChatActivity.this.alertViewAnimator.setDuration(200);
                            ChatActivity.this.alertViewAnimator.addListener(new C10531());
                            ChatActivity.this.alertViewAnimator.start();
                        }
                    }
                };
                this.hideAlertViewRunnable = str;
                AndroidUtilities.runOnUIThread(str, 3000);
            }
        }
    }

    private void hidePinnedMessageView(boolean z) {
        if (this.pinnedMessageView.getTag() == null) {
            this.pinnedMessageView.setTag(Integer.valueOf(1));
            if (this.pinnedMessageViewAnimator != null) {
                this.pinnedMessageViewAnimator.cancel();
                this.pinnedMessageViewAnimator = null;
            }
            if (z) {
                this.pinnedMessageViewAnimator = new AnimatorSet();
                z = this.pinnedMessageViewAnimator;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", new float[]{(float) (-AndroidUtilities.dp(50.0f))});
                z.playTogether(animatorArr);
                this.pinnedMessageViewAnimator.setDuration(200);
                this.pinnedMessageViewAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animator) != null) {
                            ChatActivity.this.pinnedMessageView.setVisibility(8);
                            ChatActivity.this.pinnedMessageViewAnimator = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animator) != null) {
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

    private void updatePinnedMessageView(boolean z) {
        if (this.pinnedMessageView != null) {
            if (this.info != null) {
                if (!(this.pinnedMessageObject == null || this.info.pinned_msg_id == this.pinnedMessageObject.getId())) {
                    this.pinnedMessageObject = null;
                }
                if (this.info.pinned_msg_id != 0 && this.pinnedMessageObject == null) {
                    this.pinnedMessageObject = (MessageObject) this.messagesDict[0].get(this.info.pinned_msg_id);
                }
            }
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            if (!(this.info == null || this.info.pinned_msg_id == 0)) {
                int i = this.info.pinned_msg_id;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("pin_");
                stringBuilder.append(this.dialog_id);
                if (i != notificationsSettings.getInt(stringBuilder.toString(), 0)) {
                    if (this.actionBar != null) {
                        if (!this.actionBar.isActionModeShowed()) {
                            if (this.actionBar.isSearchFieldVisible()) {
                            }
                        }
                    }
                    if (this.pinnedMessageObject != null) {
                        int dp;
                        if (this.pinnedMessageView.getTag() != null) {
                            this.pinnedMessageView.setTag(null);
                            if (this.pinnedMessageViewAnimator != null) {
                                this.pinnedMessageViewAnimator.cancel();
                                this.pinnedMessageViewAnimator = null;
                            }
                            if (z) {
                                this.pinnedMessageView.setVisibility(0);
                                this.pinnedMessageViewAnimator = new AnimatorSet();
                                z = this.pinnedMessageViewAnimator;
                                Animator[] animatorArr = new Animator[1];
                                animatorArr[0] = ObjectAnimator.ofFloat(this.pinnedMessageView, "translationY", new float[]{0.0f});
                                z.playTogether(animatorArr);
                                this.pinnedMessageViewAnimator.setDuration(200);
                                this.pinnedMessageViewAnimator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animator) != null) {
                                            ChatActivity.this.pinnedMessageViewAnimator = null;
                                        }
                                    }

                                    public void onAnimationCancel(Animator animator) {
                                        if (ChatActivity.this.pinnedMessageViewAnimator != null && ChatActivity.this.pinnedMessageViewAnimator.equals(animator) != null) {
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
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.pinnedMessageNameTextView.getLayoutParams();
                        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.pinnedMessageTextView.getLayoutParams();
                        PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.pinnedMessageObject.photoThumbs2, AndroidUtilities.dp(50.0f));
                        if (closestPhotoSizeWithSize == null) {
                            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.pinnedMessageObject.photoThumbs, AndroidUtilities.dp(50.0f));
                        }
                        if (!(closestPhotoSizeWithSize == null || (closestPhotoSizeWithSize instanceof TL_photoSizeEmpty) || (closestPhotoSizeWithSize.location instanceof TL_fileLocationUnavailable))) {
                            if (this.pinnedMessageObject.type != 13) {
                                if (this.pinnedMessageObject.isRoundVideo()) {
                                    this.pinnedMessageImageView.setRoundRadius(AndroidUtilities.dp(16.0f));
                                } else {
                                    this.pinnedMessageImageView.setRoundRadius(0);
                                }
                                this.pinnedImageLocation = closestPhotoSizeWithSize.location;
                                this.pinnedMessageImageView.setImage(this.pinnedImageLocation, "50_50", (Drawable) null);
                                this.pinnedMessageImageView.setVisibility(0);
                                dp = AndroidUtilities.dp(55.0f);
                                layoutParams2.leftMargin = dp;
                                layoutParams.leftMargin = dp;
                                this.pinnedMessageNameTextView.setLayoutParams(layoutParams);
                                this.pinnedMessageTextView.setLayoutParams(layoutParams2);
                                this.pinnedMessageNameTextView.setText(LocaleController.getString("PinnedMessage", C0446R.string.PinnedMessage));
                                if (this.pinnedMessageObject.type) {
                                    this.pinnedMessageTextView.setText(String.format("%s - %s", new Object[]{this.pinnedMessageObject.getMusicAuthor(), this.pinnedMessageObject.getMusicTitle()}));
                                } else if (this.pinnedMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
                                    this.pinnedMessageTextView.setText(Emoji.replaceEmoji(this.pinnedMessageObject.messageOwner.media.game.title, this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                                } else if (this.pinnedMessageObject.messageText) {
                                    z = this.pinnedMessageObject.messageText.toString();
                                    if (z.length() > 150) {
                                        z = z.substring(0, 150);
                                    }
                                    this.pinnedMessageTextView.setText(Emoji.replaceEmoji(z.replace('\n', ' '), this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                                }
                            }
                        }
                        this.pinnedMessageImageView.setImageBitmap(null);
                        this.pinnedImageLocation = null;
                        this.pinnedMessageImageView.setVisibility(4);
                        dp = AndroidUtilities.dp(18.0f);
                        layoutParams2.leftMargin = dp;
                        layoutParams.leftMargin = dp;
                        this.pinnedMessageNameTextView.setLayoutParams(layoutParams);
                        this.pinnedMessageTextView.setLayoutParams(layoutParams2);
                        this.pinnedMessageNameTextView.setText(LocaleController.getString("PinnedMessage", C0446R.string.PinnedMessage));
                        if (this.pinnedMessageObject.type) {
                            this.pinnedMessageTextView.setText(String.format("%s - %s", new Object[]{this.pinnedMessageObject.getMusicAuthor(), this.pinnedMessageObject.getMusicTitle()}));
                        } else if (this.pinnedMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
                            this.pinnedMessageTextView.setText(Emoji.replaceEmoji(this.pinnedMessageObject.messageOwner.media.game.title, this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                        } else if (this.pinnedMessageObject.messageText) {
                            z = this.pinnedMessageObject.messageText.toString();
                            if (z.length() > 150) {
                                z = z.substring(0, 150);
                            }
                            this.pinnedMessageTextView.setText(Emoji.replaceEmoji(z.replace('\n', ' '), this.pinnedMessageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
                        }
                    } else {
                        this.pinnedImageLocation = null;
                        hidePinnedMessageView(z);
                        if (this.loadingPinnedMessage != this.info.pinned_msg_id) {
                            this.loadingPinnedMessage = this.info.pinned_msg_id;
                            DataQuery.getInstance(this.currentAccount).loadPinnedMessage(this.currentChat.id, this.info.pinned_msg_id, true);
                        }
                    }
                    checkListViewPaddings();
                }
            }
            hidePinnedMessageView(z);
            checkListViewPaddings();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateSpamView() {
        if (this.reportSpamView == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("no spam view found");
            }
            return;
        }
        int i;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (this.currentEncryptedChat != null) {
            int i2 = (this.currentEncryptedChat.admin_id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).isLoadingContacts() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.id)) != null) ? 0 : 1;
            if (i2 != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("spam3_");
                stringBuilder.append(this.dialog_id);
            }
            i = i2;
            if (i == 0) {
                if (this.reportSpamView.getTag() == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("hide spam button");
                    }
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
                        public void onAnimationEnd(Animator animator) {
                            if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator) != null) {
                                ChatActivity.this.reportSpamView.setVisibility(8);
                                ChatActivity.this.reportSpamViewAnimator = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator) != null) {
                                ChatActivity.this.reportSpamViewAnimator = null;
                            }
                        }
                    });
                    this.reportSpamViewAnimator.start();
                }
            } else if (this.reportSpamView.getTag() != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("show spam button");
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
                this.reportSpamViewAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator) != null) {
                            ChatActivity.this.reportSpamViewAnimator = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (ChatActivity.this.reportSpamViewAnimator != null && ChatActivity.this.reportSpamViewAnimator.equals(animator) != null) {
                            ChatActivity.this.reportSpamViewAnimator = null;
                        }
                    }
                });
                this.reportSpamViewAnimator.start();
            }
            checkListViewPaddings();
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("spam3_");
        stringBuilder2.append(this.dialog_id);
        if (notificationsSettings.getInt(stringBuilder2.toString(), 0) == 2) {
            i = 1;
            if (i == 0) {
                if (this.reportSpamView.getTag() != null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("show spam button");
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
                    this.reportSpamViewAnimator.addListener(/* anonymous class already generated */);
                    this.reportSpamViewAnimator.start();
                }
            } else if (this.reportSpamView.getTag() == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("hide spam button");
                }
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
                this.reportSpamViewAnimator.addListener(/* anonymous class already generated */);
                this.reportSpamViewAnimator.start();
            }
            checkListViewPaddings();
        }
        i = 0;
        if (i == 0) {
            if (this.reportSpamView.getTag() == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("hide spam button");
                }
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
                this.reportSpamViewAnimator.addListener(/* anonymous class already generated */);
                this.reportSpamViewAnimator.start();
            }
        } else if (this.reportSpamView.getTag() != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("show spam button");
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
            this.reportSpamViewAnimator.addListener(/* anonymous class already generated */);
            this.reportSpamViewAnimator.start();
        }
        checkListViewPaddings();
    }

    private void updateContactStatus() {
        if (this.addContactItem != null) {
            if (this.currentUser == null) {
                this.addContactItem.setVisibility(8);
            } else {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
                if (user != null) {
                    this.currentUser = user;
                }
                if (!((this.currentEncryptedChat != null && !(this.currentEncryptedChat instanceof TL_encryptedChat)) || MessagesController.isSupportId(this.currentUser.id) || UserObject.isDeleted(this.currentUser) || ContactsController.getInstance(this.currentAccount).isLoadingContacts())) {
                    if (!(TextUtils.isEmpty(this.currentUser.phone) || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.id)) == null)) {
                        if (ContactsController.getInstance(this.currentAccount).contactsDict.size() == 0) {
                            if (!ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
                            }
                        }
                    }
                    this.addContactItem.setVisibility(0);
                    if (TextUtils.isEmpty(this.currentUser.phone)) {
                        this.addContactItem.setText(LocaleController.getString("ShareMyContactInfo", C0446R.string.ShareMyContactInfo));
                        this.addToContactsButton.setVisibility(8);
                        this.reportSpamButton.setPadding(AndroidUtilities.dp(50.0f), 0, AndroidUtilities.dp(50.0f), 0);
                        this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, 1.0f, 51, 0, 0, 0, AndroidUtilities.dp(1.0f)));
                    } else {
                        this.addContactItem.setText(LocaleController.getString("AddToContacts", C0446R.string.AddToContacts));
                        this.reportSpamButton.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(50.0f), 0);
                        this.addToContactsButton.setVisibility(0);
                        this.reportSpamContainer.setLayoutParams(LayoutHelper.createLinear(-1, -1, 0.5f, 51, 0, 0, 0, AndroidUtilities.dp(1.0f)));
                    }
                }
                this.addContactItem.setVisibility(8);
            }
            checkListViewPaddings();
        }
    }

    private void checkListViewPaddingsInternal() {
        if (this.chatLayoutManager != null) {
            try {
                int indexOf;
                View findViewByPosition;
                int measuredHeight;
                int findFirstVisibleItemPosition = this.chatLayoutManager.findFirstVisibleItemPosition();
                if (!(this.wasManualScroll || this.unreadMessageObject == null)) {
                    indexOf = this.messages.indexOf(this.unreadMessageObject);
                    if (indexOf >= 0) {
                        indexOf = this.chatAdapter.messagesStartRow + indexOf;
                        findFirstVisibleItemPosition = -1;
                        if (findFirstVisibleItemPosition != -1) {
                            findViewByPosition = this.chatLayoutManager.findViewByPosition(findFirstVisibleItemPosition);
                            if (findViewByPosition == null) {
                                measuredHeight = (this.chatListView.getMeasuredHeight() - findViewByPosition.getBottom()) - this.chatListView.getPaddingBottom();
                                if (this.chatListView.getPaddingTop() == AndroidUtilities.dp(52.0f) && ((this.pinnedMessageView != null && this.pinnedMessageView.getTag() == null) || (this.reportSpamView != null && this.reportSpamView.getTag() == null))) {
                                    this.chatListView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(3.0f));
                                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.floatingDateView.getLayoutParams();
                                    layoutParams.topMargin = AndroidUtilities.dp(52.0f);
                                    this.floatingDateView.setLayoutParams(layoutParams);
                                    this.chatListView.setTopGlowOffset(AndroidUtilities.dp(48.0f));
                                } else if (this.chatListView.getPaddingTop() != AndroidUtilities.dp(4.0f) || ((this.pinnedMessageView != null && this.pinnedMessageView.getTag() == null) || (this.reportSpamView != null && this.reportSpamView.getTag() == null))) {
                                    findFirstVisibleItemPosition = -1;
                                } else {
                                    this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
                                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.floatingDateView.getLayoutParams();
                                    layoutParams2.topMargin = AndroidUtilities.dp(4.0f);
                                    this.floatingDateView.setLayoutParams(layoutParams2);
                                    this.chatListView.setTopGlowOffset(0);
                                }
                                if (findFirstVisibleItemPosition == -1) {
                                    this.chatLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, measuredHeight);
                                } else if (indexOf != -1) {
                                    this.chatLayoutManager.scrollToPositionWithOffset(indexOf, ((this.chatListView.getMeasuredHeight() - this.chatListView.getPaddingBottom()) - this.chatListView.getPaddingTop()) - AndroidUtilities.dp(29.0f));
                                }
                            }
                        }
                        measuredHeight = 0;
                        if (this.chatListView.getPaddingTop() == AndroidUtilities.dp(52.0f)) {
                        }
                        if (this.chatListView.getPaddingTop() != AndroidUtilities.dp(4.0f)) {
                        }
                        findFirstVisibleItemPosition = -1;
                        if (findFirstVisibleItemPosition == -1) {
                            this.chatLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, measuredHeight);
                        } else if (indexOf != -1) {
                            this.chatLayoutManager.scrollToPositionWithOffset(indexOf, ((this.chatListView.getMeasuredHeight() - this.chatListView.getPaddingBottom()) - this.chatListView.getPaddingTop()) - AndroidUtilities.dp(29.0f));
                        }
                    }
                }
                indexOf = -1;
                if (findFirstVisibleItemPosition != -1) {
                    findViewByPosition = this.chatLayoutManager.findViewByPosition(findFirstVisibleItemPosition);
                    if (findViewByPosition == null) {
                        measuredHeight = (this.chatListView.getMeasuredHeight() - findViewByPosition.getBottom()) - this.chatListView.getPaddingBottom();
                        if (this.chatListView.getPaddingTop() == AndroidUtilities.dp(52.0f)) {
                        }
                        if (this.chatListView.getPaddingTop() != AndroidUtilities.dp(4.0f)) {
                        }
                        findFirstVisibleItemPosition = -1;
                        if (findFirstVisibleItemPosition == -1) {
                            this.chatLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, measuredHeight);
                        } else if (indexOf != -1) {
                            this.chatLayoutManager.scrollToPositionWithOffset(indexOf, ((this.chatListView.getMeasuredHeight() - this.chatListView.getPaddingBottom()) - this.chatListView.getPaddingTop()) - AndroidUtilities.dp(29.0f));
                        }
                    }
                }
                measuredHeight = 0;
                if (this.chatListView.getPaddingTop() == AndroidUtilities.dp(52.0f)) {
                }
                if (this.chatListView.getPaddingTop() != AndroidUtilities.dp(4.0f)) {
                }
                findFirstVisibleItemPosition = -1;
                if (findFirstVisibleItemPosition == -1) {
                    this.chatLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, measuredHeight);
                } else if (indexOf != -1) {
                    this.chatLayoutManager.scrollToPositionWithOffset(indexOf, ((this.chatListView.getMeasuredHeight() - this.chatListView.getPaddingBottom()) - this.chatListView.getPaddingTop()) - AndroidUtilities.dp(29.0f));
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    private void checkListViewPaddings() {
        if (this.wasManualScroll || this.unreadMessageObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ChatActivity.this.checkListViewPaddingsInternal();
                }
            });
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
        NotificationsController.getInstance(this.currentAccount).setOpenedDialogId(this.dialog_id);
        if (this.scrollToTopOnResume) {
            if (!this.scrollToTopUnReadOnResume || this.scrollToMessage == null) {
                moveScrollToLastMessage();
            } else if (this.chatListView != null) {
                int scrollOffsetForMessage;
                boolean z;
                if (this.scrollToMessagePosition == -9000) {
                    scrollOffsetForMessage = getScrollOffsetForMessage(this.scrollToMessage);
                } else if (this.scrollToMessagePosition == -10000) {
                    scrollOffsetForMessage = -AndroidUtilities.dp(11.0f);
                } else {
                    scrollOffsetForMessage = this.scrollToMessagePosition;
                    z = true;
                    this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.messagesStartRow + this.messages.indexOf(this.scrollToMessage), scrollOffsetForMessage, z);
                }
                z = false;
                this.chatLayoutManager.scrollToPositionWithOffset(this.chatAdapter.messagesStartRow + this.messages.indexOf(this.scrollToMessage), scrollOffsetForMessage, z);
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
        if (!(this.bottomOverlayChat == null || this.bottomOverlayChat.getVisibility() == 0)) {
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
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ChatActivity.this.openVideoEditor(ChatActivity.this.startVideoEdit, null);
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
        MessageObject messageObject;
        CharSequence charSequence;
        super.onPause();
        MessagesController.getInstance(this.currentAccount).markDialogAsReadNow(this.dialog_id);
        MediaController.getInstance().stopRaiseToEarSensors(this);
        this.paused = true;
        this.wasPaused = true;
        NotificationsController.getInstance(this.currentAccount).setOpenedDialogId(0);
        Message message = null;
        int i = 0;
        if (this.ignoreAttachOnPause || this.chatActivityEnterView == null || this.bottomOverlayChat.getVisibility() == 0) {
            z = true;
            messageObject = null;
            charSequence = messageObject;
        } else {
            this.chatActivityEnterView.onPause();
            messageObject = this.replyingMessageObject;
            if (!this.chatActivityEnterView.isEditingMessage()) {
                charSequence = AndroidUtilities.getTrimmedString(this.chatActivityEnterView.getFieldText());
                if (!(TextUtils.isEmpty(charSequence) || TextUtils.equals(charSequence, "@gif"))) {
                    z = this.chatActivityEnterView.isMessageWebPageSearchEnabled();
                    this.chatActivityEnterView.setFieldFocused(false);
                }
            }
            charSequence = null;
            z = this.chatActivityEnterView.isMessageWebPageSearchEnabled();
            this.chatActivityEnterView.setFieldFocused(false);
        }
        if (this.chatAttachAlert != null) {
            if (this.ignoreAttachOnPause) {
                this.ignoreAttachOnPause = false;
            } else {
                this.chatAttachAlert.onPause();
            }
        }
        CharSequence[] charSequenceArr = new CharSequence[]{charSequence};
        ArrayList entities = DataQuery.getInstance(this.currentAccount).getEntities(charSequenceArr);
        DataQuery instance = DataQuery.getInstance(this.currentAccount);
        long j = this.dialog_id;
        CharSequence charSequence2 = charSequenceArr[0];
        if (messageObject != null) {
            message = messageObject.messageOwner;
        }
        instance.saveDraft(j, charSequence2, entities, message, !z);
        MessagesController.getInstance(this.currentAccount).cancelTyping(0, this.dialog_id);
        if (!this.pausedOnLastMessage) {
            int findFirstVisibleItemPosition;
            StringBuilder stringBuilder;
            Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (this.chatLayoutManager != null) {
                findFirstVisibleItemPosition = this.chatLayoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition != 0) {
                    Holder holder = (Holder) this.chatListView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                    if (holder != null) {
                        boolean z2;
                        StringBuilder stringBuilder2;
                        int i2;
                        int id = holder.itemView instanceof ChatMessageCell ? ((ChatMessageCell) holder.itemView).getMessageObject().getId() : holder.itemView instanceof ChatActionCell ? ((ChatActionCell) holder.itemView).getMessageObject().getId() : 0;
                        if (id == 0) {
                            holder = (Holder) this.chatListView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition + 1);
                        }
                        findFirstVisibleItemPosition--;
                        while (findFirstVisibleItemPosition >= this.chatAdapter.messagesStartRow) {
                            MessageObject messageObject2 = (MessageObject) this.messages.get(findFirstVisibleItemPosition - this.chatAdapter.messagesStartRow);
                            if (messageObject2.getId() == 0) {
                                findFirstVisibleItemPosition--;
                            } else {
                                if (!messageObject2.isOut() && messageObject2.isUnread()) {
                                    z2 = true;
                                    if (!(holder == null || r2)) {
                                        findFirstVisibleItemPosition = holder.itemView instanceof ChatMessageCell ? ((ChatMessageCell) holder.itemView).getMessageObject().getId() : holder.itemView instanceof ChatActionCell ? ((ChatActionCell) holder.itemView).getMessageObject().getId() : 0;
                                        if ((findFirstVisibleItemPosition > 0 && this.currentEncryptedChat == null) || (findFirstVisibleItemPosition < 0 && this.currentEncryptedChat != null)) {
                                            i = holder.itemView.getBottom() - this.chatListView.getMeasuredHeight();
                                            if (BuildVars.LOGS_ENABLED) {
                                                stringBuilder2 = new StringBuilder();
                                                stringBuilder2.append("save offset = ");
                                                stringBuilder2.append(i);
                                                stringBuilder2.append(" for mid ");
                                                stringBuilder2.append(findFirstVisibleItemPosition);
                                                FileLog.m0d(stringBuilder2.toString());
                                            }
                                            i2 = i;
                                            i = findFirstVisibleItemPosition;
                                            findFirstVisibleItemPosition = i2;
                                            if (i == 0) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("diditem");
                                                stringBuilder.append(this.dialog_id);
                                                edit.putInt(stringBuilder.toString(), i);
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("diditemo");
                                                stringBuilder.append(this.dialog_id);
                                                edit.putInt(stringBuilder.toString(), findFirstVisibleItemPosition);
                                            } else {
                                                this.pausedOnLastMessage = true;
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("diditem");
                                                stringBuilder.append(this.dialog_id);
                                                edit.remove(stringBuilder.toString());
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("diditemo");
                                                stringBuilder.append(this.dialog_id);
                                                edit.remove(stringBuilder.toString());
                                            }
                                            edit.commit();
                                        }
                                    }
                                }
                                z2 = false;
                                if (holder.itemView instanceof ChatMessageCell) {
                                    if (holder.itemView instanceof ChatActionCell) {
                                    }
                                }
                                i = holder.itemView.getBottom() - this.chatListView.getMeasuredHeight();
                                if (BuildVars.LOGS_ENABLED) {
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("save offset = ");
                                    stringBuilder2.append(i);
                                    stringBuilder2.append(" for mid ");
                                    stringBuilder2.append(findFirstVisibleItemPosition);
                                    FileLog.m0d(stringBuilder2.toString());
                                }
                                i2 = i;
                                i = findFirstVisibleItemPosition;
                                findFirstVisibleItemPosition = i2;
                                if (i == 0) {
                                    this.pausedOnLastMessage = true;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("diditem");
                                    stringBuilder.append(this.dialog_id);
                                    edit.remove(stringBuilder.toString());
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("diditemo");
                                    stringBuilder.append(this.dialog_id);
                                    edit.remove(stringBuilder.toString());
                                } else {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("diditem");
                                    stringBuilder.append(this.dialog_id);
                                    edit.putInt(stringBuilder.toString(), i);
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("diditemo");
                                    stringBuilder.append(this.dialog_id);
                                    edit.putInt(stringBuilder.toString(), findFirstVisibleItemPosition);
                                }
                                edit.commit();
                            }
                        }
                        z2 = false;
                        if (holder.itemView instanceof ChatMessageCell) {
                        }
                        i = holder.itemView.getBottom() - this.chatListView.getMeasuredHeight();
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("save offset = ");
                            stringBuilder2.append(i);
                            stringBuilder2.append(" for mid ");
                            stringBuilder2.append(findFirstVisibleItemPosition);
                            FileLog.m0d(stringBuilder2.toString());
                        }
                        i2 = i;
                        i = findFirstVisibleItemPosition;
                        findFirstVisibleItemPosition = i2;
                        if (i == 0) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("diditem");
                            stringBuilder.append(this.dialog_id);
                            edit.putInt(stringBuilder.toString(), i);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("diditemo");
                            stringBuilder.append(this.dialog_id);
                            edit.putInt(stringBuilder.toString(), findFirstVisibleItemPosition);
                        } else {
                            this.pausedOnLastMessage = true;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("diditem");
                            stringBuilder.append(this.dialog_id);
                            edit.remove(stringBuilder.toString());
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("diditemo");
                            stringBuilder.append(this.dialog_id);
                            edit.remove(stringBuilder.toString());
                        }
                        edit.commit();
                    }
                }
            }
            findFirstVisibleItemPosition = 0;
            if (i == 0) {
                this.pausedOnLastMessage = true;
                stringBuilder = new StringBuilder();
                stringBuilder.append("diditem");
                stringBuilder.append(this.dialog_id);
                edit.remove(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append("diditemo");
                stringBuilder.append(this.dialog_id);
                edit.remove(stringBuilder.toString());
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("diditem");
                stringBuilder.append(this.dialog_id);
                edit.putInt(stringBuilder.toString(), i);
                stringBuilder = new StringBuilder();
                stringBuilder.append("diditemo");
                stringBuilder.append(this.dialog_id);
                edit.putInt(stringBuilder.toString(), findFirstVisibleItemPosition);
            }
            edit.commit();
        }
        if (this.currentUser != null) {
            this.chatLeaveTime = System.currentTimeMillis();
            updateInformationForScreenshotDetector();
        }
    }

    private void applyDraftMaybe(boolean z) {
        if (this.chatActivityEnterView != null) {
            DraftMessage draft = DataQuery.getInstance(this.currentAccount).getDraft(this.dialog_id);
            Message draftMessage = (draft == null || draft.reply_to_msg_id == 0) ? null : DataQuery.getInstance(this.currentAccount).getDraftMessage(this.dialog_id);
            if (this.chatActivityEnterView.getFieldText() == null) {
                if (draft != null) {
                    this.chatActivityEnterView.setWebPage(null, draft.no_webpage ^ true);
                    if (draft.entities.isEmpty()) {
                        z = draft.message;
                    } else {
                        z = SpannableStringBuilder.valueOf(draft.message);
                        DataQuery.getInstance(this.currentAccount);
                        DataQuery.sortEntities(draft.entities);
                        int i = 0;
                        int i2 = i;
                        while (i < draft.entities.size()) {
                            int i3;
                            MessageEntity messageEntity = (MessageEntity) draft.entities.get(i);
                            boolean z2 = messageEntity instanceof TL_inputMessageEntityMentionName;
                            if (!z2) {
                                if (!(messageEntity instanceof TL_messageEntityMentionName)) {
                                    if (messageEntity instanceof TL_messageEntityCode) {
                                        z.insert((messageEntity.offset + messageEntity.length) + i2, "`");
                                        z.insert(messageEntity.offset + i2, "`");
                                        i2 += 2;
                                    } else if (messageEntity instanceof TL_messageEntityPre) {
                                        z.insert((messageEntity.offset + messageEntity.length) + i2, "```");
                                        z.insert(messageEntity.offset + i2, "```");
                                        i2 += 6;
                                    } else if (messageEntity instanceof TL_messageEntityBold) {
                                        z.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), messageEntity.offset + i2, (messageEntity.offset + messageEntity.length) + i2, 33);
                                    } else if (messageEntity instanceof TL_messageEntityItalic) {
                                        z.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), messageEntity.offset + i2, (messageEntity.offset + messageEntity.length) + i2, 33);
                                    }
                                    i++;
                                }
                            }
                            if (z2) {
                                i3 = ((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id;
                            } else {
                                i3 = ((TL_messageEntityMentionName) messageEntity).user_id;
                            }
                            if ((messageEntity.offset + i2) + messageEntity.length < z.length() && z.charAt((messageEntity.offset + i2) + messageEntity.length) == ' ') {
                                messageEntity.length++;
                            }
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                            stringBuilder.append(i3);
                            z.setSpan(new URLSpanUserMention(stringBuilder.toString(), 1), messageEntity.offset + i2, (messageEntity.offset + i2) + messageEntity.length, 33);
                            i++;
                        }
                    }
                    this.chatActivityEnterView.setFieldText(z);
                    if (getArguments().getBoolean("hasUrl", false)) {
                        this.chatActivityEnterView.setSelection(draft.message.indexOf(10) + 1);
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
            } else if (z && draft == null) {
                this.chatActivityEnterView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                showReplyPanel(false, null, null, null, false);
            }
            if (!(this.replyingMessageObject || draftMessage == null)) {
                this.replyingMessageObject = new MessageObject(this.currentAccount, draftMessage, MessagesController.getInstance(this.currentAccount).getUsers(), false);
                showReplyPanel(true, this.replyingMessageObject, null, null, false);
            }
        }
    }

    private void updateInformationForScreenshotDetector() {
        if (this.currentUser != null) {
            if (this.currentEncryptedChat != null) {
                ArrayList arrayList = new ArrayList();
                if (this.chatListView != null) {
                    int childCount = this.chatListView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = this.chatListView.getChildAt(i);
                        MessageObject messageObject = null;
                        if (childAt instanceof ChatMessageCell) {
                            messageObject = ((ChatMessageCell) childAt).getMessageObject();
                        }
                        if (!(messageObject == null || messageObject.getId() >= 0 || messageObject.messageOwner.random_id == 0)) {
                            arrayList.add(Long.valueOf(messageObject.messageOwner.random_id));
                        }
                    }
                }
                MediaController.getInstance().setLastVisibleMessageIds(this.currentAccount, this.chatEnterTime, this.chatLeaveTime, this.currentUser, this.currentEncryptedChat, arrayList, 0);
            } else {
                SecretMediaViewer instance = SecretMediaViewer.getInstance();
                MessageObject currentMessageObject = instance.getCurrentMessageObject();
                if (!(currentMessageObject == null || currentMessageObject.isOut())) {
                    MediaController.getInstance().setLastVisibleMessageIds(this.currentAccount, instance.getOpenTime(), instance.getCloseTime(), this.currentUser, null, null, currentMessageObject.getId());
                }
            }
        }
    }

    private boolean fixLayoutInternal() {
        if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
            this.selectedMessagesCountTextView.setTextSize(20);
        } else {
            this.selectedMessagesCountTextView.setTextSize(18);
        }
        int childCount = this.chatListView.getChildCount();
        HashMap hashMap = null;
        int i = 0;
        while (true) {
            boolean z = true;
            if (i >= childCount) {
                break;
            }
            View childAt = this.chatListView.getChildAt(i);
            if (childAt instanceof ChatMessageCell) {
                GroupedMessages currentMessagesGroup = ((ChatMessageCell) childAt).getCurrentMessagesGroup();
                if (currentMessagesGroup != null && currentMessagesGroup.hasSibling) {
                    if (hashMap == null) {
                        hashMap = new HashMap();
                    }
                    if (!hashMap.containsKey(Long.valueOf(currentMessagesGroup.groupId))) {
                        hashMap.put(Long.valueOf(currentMessagesGroup.groupId), currentMessagesGroup);
                        int indexOf = this.messages.indexOf((MessageObject) currentMessagesGroup.messages.get(currentMessagesGroup.messages.size() - 1));
                        if (indexOf >= 0) {
                            this.chatAdapter.notifyItemRangeChanged(indexOf + this.chatAdapter.messagesStartRow, currentMessagesGroup.messages.size());
                        }
                    }
                }
            }
            i++;
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
            ActionBar actionBar = this.actionBar;
            if (!(this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(0) == this)) {
                if (this.parentLayout.fragmentsStack.size() != 1) {
                    z = false;
                }
            }
            actionBar.setBackButtonDrawable(new BackDrawable(z));
            if (!(this.fragmentContextView == null || this.fragmentContextView.getParent() == null)) {
                this.fragmentView.setPadding(0, 0, 0, 0);
                ((ViewGroup) this.fragmentView).removeView(this.fragmentContextView);
            }
        }
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

    public void onConfigurationChanged(Configuration configuration) {
        fixLayout();
        if ((this.visibleDialog instanceof DatePickerDialog) != null) {
            this.visibleDialog.dismiss();
        }
    }

    private void createDeleteMessagesAlert(MessageObject messageObject, GroupedMessages groupedMessages) {
        createDeleteMessagesAlert(messageObject, groupedMessages, 1);
    }

    private void createDeleteMessagesAlert(MessageObject messageObject, GroupedMessages groupedMessages, int i) {
        ChatActivity chatActivity = this;
        final MessageObject messageObject2 = messageObject;
        final GroupedMessages groupedMessages2 = groupedMessages;
        int i2 = i;
        if (getParentActivity() != null) {
            int i3;
            User user;
            Builder builder = new Builder(getParentActivity());
            int i4 = 1;
            int i5 = 0;
            int size = groupedMessages2 != null ? groupedMessages2.messages.size() : messageObject2 != null ? 1 : chatActivity.selectedMessagesIds[0].size() + chatActivity.selectedMessagesIds[1].size();
            builder.setMessage(LocaleController.formatString("AreYouSureDeleteMessages", C0446R.string.AreYouSureDeleteMessages, LocaleController.formatPluralString("messages", size)));
            builder.setTitle(LocaleController.getString("Message", C0446R.string.Message));
            final boolean[] zArr = new boolean[3];
            final boolean[] zArr2 = new boolean[1];
            int i6 = (chatActivity.currentUser == null || !MessagesController.getInstance(chatActivity.currentAccount).canRevokePmInbox) ? 0 : 1;
            if (chatActivity.currentUser != null) {
                i3 = MessagesController.getInstance(chatActivity.currentAccount).revokeTimePmLimit;
            } else {
                i3 = MessagesController.getInstance(chatActivity.currentAccount).revokeTimeLimit;
            }
            View frameLayout;
            View checkBoxCell;
            float f;
            float f2;
            if (chatActivity.currentChat == null || !chatActivity.currentChat.megagroup) {
                if (!ChatObject.isChannel(chatActivity.currentChat) && chatActivity.currentEncryptedChat == null) {
                    Object obj;
                    i2 = ConnectionsManager.getInstance(chatActivity.currentAccount).getCurrentTime();
                    if (!((chatActivity.currentUser == null || chatActivity.currentUser.id == UserConfig.getInstance(chatActivity.currentAccount).getClientUserId() || chatActivity.currentUser.bot) && chatActivity.currentChat == null)) {
                        if (messageObject2 == null) {
                            obj = null;
                            Object obj2 = null;
                            for (i4 = 1; i4 >= 0; i4--) {
                                Object obj3 = obj;
                                for (i5 = 0; i5 < chatActivity.selectedMessagesIds[i4].size(); i5++) {
                                    MessageObject messageObject3 = (MessageObject) chatActivity.selectedMessagesIds[i4].valueAt(i5);
                                    if (messageObject3.messageOwner.action == null) {
                                        if (!messageObject3.isOut() && i6 == 0) {
                                            if (chatActivity.currentChat != null) {
                                                if (!chatActivity.currentChat.creator) {
                                                    if (chatActivity.currentChat.admin && chatActivity.currentChat.admins_enabled) {
                                                    }
                                                }
                                            }
                                            obj = null;
                                            obj2 = 1;
                                            break;
                                        }
                                        if (obj3 == null && i2 - messageObject3.messageOwner.date <= i3) {
                                            obj3 = 1;
                                        }
                                    }
                                }
                                obj = obj3;
                                if (obj2 != null) {
                                    break;
                                }
                            }
                        } else if (!messageObject.isSendError() && ((messageObject2.messageOwner.action == null || (messageObject2.messageOwner.action instanceof TL_messageActionEmpty)) && ((messageObject.isOut() || i6 != 0 || (chatActivity.currentChat != null && (chatActivity.currentChat.creator || (chatActivity.currentChat.admin && chatActivity.currentChat.admins_enabled)))) && i2 - messageObject2.messageOwner.date <= i3))) {
                            obj = 1;
                        }
                        if (obj != null) {
                            frameLayout = new FrameLayout(getParentActivity());
                            checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
                            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            if (chatActivity.currentChat == null) {
                                checkBoxCell.setText(LocaleController.getString("DeleteForAll", C0446R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                            } else {
                                checkBoxCell.setText(LocaleController.formatString("DeleteForUser", C0446R.string.DeleteForUser, UserObject.getFirstName(chatActivity.currentUser)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                            }
                            if (LocaleController.isRTL) {
                                f = 16.0f;
                                f2 = 8.0f;
                                i6 = AndroidUtilities.dp(8.0f);
                            } else {
                                f = 16.0f;
                                i6 = AndroidUtilities.dp(16.0f);
                                f2 = 8.0f;
                            }
                            checkBoxCell.setPadding(i6, 0, LocaleController.isRTL ? AndroidUtilities.dp(f2) : AndroidUtilities.dp(f), 0);
                            frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                            checkBoxCell.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                                    zArr2[0] = zArr2[0] ^ true;
                                    checkBoxCell.setChecked(zArr2[0], true);
                                }
                            });
                            builder.setView(frameLayout);
                        }
                    }
                    obj = null;
                    if (obj != null) {
                        frameLayout = new FrameLayout(getParentActivity());
                        checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        if (chatActivity.currentChat == null) {
                            checkBoxCell.setText(LocaleController.formatString("DeleteForUser", C0446R.string.DeleteForUser, UserObject.getFirstName(chatActivity.currentUser)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                        } else {
                            checkBoxCell.setText(LocaleController.getString("DeleteForAll", C0446R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                        }
                        if (LocaleController.isRTL) {
                            f = 16.0f;
                            f2 = 8.0f;
                            i6 = AndroidUtilities.dp(8.0f);
                        } else {
                            f = 16.0f;
                            i6 = AndroidUtilities.dp(16.0f);
                            f2 = 8.0f;
                        }
                        if (LocaleController.isRTL) {
                        }
                        checkBoxCell.setPadding(i6, 0, LocaleController.isRTL ? AndroidUtilities.dp(f2) : AndroidUtilities.dp(f), 0);
                        frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                        checkBoxCell.setOnClickListener(/* anonymous class already generated */);
                        builder.setView(frameLayout);
                    }
                }
                user = null;
            } else {
                User user2;
                boolean canBlockUsers = ChatObject.canBlockUsers(chatActivity.currentChat);
                int currentTime = ConnectionsManager.getInstance(chatActivity.currentAccount).getCurrentTime();
                if (messageObject2 != null) {
                    if (!(messageObject2.messageOwner.action == null || (messageObject2.messageOwner.action instanceof TL_messageActionEmpty))) {
                        if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatDeleteUser)) {
                            user2 = null;
                            i3 = (!messageObject.isSendError() && messageObject.getDialogId() == chatActivity.mergeDialogId && ((messageObject2.messageOwner.action == null || (messageObject2.messageOwner.action instanceof TL_messageActionEmpty)) && messageObject.isOut() && currentTime - messageObject2.messageOwner.date <= i3)) ? 1 : 0;
                        }
                    }
                    user2 = MessagesController.getInstance(chatActivity.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.from_id));
                    if (!!messageObject.isSendError()) {
                    }
                } else {
                    int i7;
                    i3 = -1;
                    int i8 = 1;
                    int i9 = -1;
                    while (i8 >= 0) {
                        i7 = i9;
                        i9 = i5;
                        while (i9 < chatActivity.selectedMessagesIds[i8].size()) {
                            MessageObject messageObject4 = (MessageObject) chatActivity.selectedMessagesIds[i8].valueAt(i9);
                            if (i7 == -1) {
                                i7 = messageObject4.messageOwner.from_id;
                            }
                            if (i7 >= 0) {
                                if (i7 == messageObject4.messageOwner.from_id) {
                                    i9++;
                                }
                            }
                            i9 = -2;
                        }
                        i9 = i7;
                        if (i9 == -2) {
                            break;
                        }
                        i8--;
                        i5 = 0;
                    }
                    i5 = 1;
                    size = 0;
                    Object obj4 = null;
                    while (i5 >= 0) {
                        i7 = size;
                        size = 0;
                        while (size < chatActivity.selectedMessagesIds[i5].size()) {
                            MessageObject messageObject5 = (MessageObject) chatActivity.selectedMessagesIds[i5].valueAt(size);
                            if (i5 != i4) {
                                if (i5 == 0 && !messageObject5.isOut()) {
                                }
                                size++;
                                i4 = 1;
                            } else if (messageObject5.isOut() && messageObject5.messageOwner.action == null) {
                                if (currentTime - messageObject5.messageOwner.date <= 172800) {
                                    i7 = 1;
                                }
                                size++;
                                i4 = 1;
                            }
                            size = 0;
                            obj4 = 1;
                        }
                        size = i7;
                        if (obj4 != null) {
                            i3 = size;
                            i4 = -1;
                            break;
                        }
                        i5--;
                        i4 = 1;
                        i3 = -1;
                    }
                    i4 = i3;
                    i3 = size;
                    user2 = i9 != i4 ? MessagesController.getInstance(chatActivity.currentAccount).getUser(Integer.valueOf(i9)) : null;
                }
                if (!(user2 == null || user2.id == UserConfig.getInstance(chatActivity.currentAccount).getClientUserId())) {
                    i4 = 2;
                    if (i2 != 2) {
                        if (i2 != 1 || chatActivity.currentChat.creator) {
                            frameLayout = new FrameLayout(getParentActivity());
                            i5 = 0;
                            size = 0;
                            while (i5 < 3) {
                                boolean z;
                                if (canBlockUsers || i5 != 0) {
                                    float f3;
                                    float f4;
                                    View checkBoxCell2 = new CheckBoxCell(getParentActivity(), 1);
                                    checkBoxCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    checkBoxCell2.setTag(Integer.valueOf(i5));
                                    if (i5 == 0) {
                                        checkBoxCell2.setText(LocaleController.getString("DeleteBanUser", C0446R.string.DeleteBanUser), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    } else if (i5 == 1) {
                                        checkBoxCell2.setText(LocaleController.getString("DeleteReportSpam", C0446R.string.DeleteReportSpam), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                    } else if (i5 == i4) {
                                        Object[] objArr = new Object[1];
                                        z = canBlockUsers;
                                        objArr[0] = ContactsController.formatName(user2.first_name, user2.last_name);
                                        checkBoxCell2.setText(LocaleController.formatString("DeleteAllFrom", C0446R.string.DeleteAllFrom, objArr), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                                        if (LocaleController.isRTL) {
                                            f3 = 16.0f;
                                            f4 = 8.0f;
                                            i3 = AndroidUtilities.dp(8.0f);
                                        } else {
                                            f3 = 16.0f;
                                            i3 = AndroidUtilities.dp(16.0f);
                                            f4 = 8.0f;
                                        }
                                        checkBoxCell2.setPadding(i3, 0, LocaleController.isRTL ? AndroidUtilities.dp(f4) : AndroidUtilities.dp(f3), 0);
                                        frameLayout.addView(checkBoxCell2, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) (48 * size), 0.0f, 0.0f));
                                        checkBoxCell2.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View view) {
                                                if (view.isEnabled()) {
                                                    CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                                                    Integer num = (Integer) checkBoxCell.getTag();
                                                    zArr[num.intValue()] = zArr[num.intValue()] ^ true;
                                                    checkBoxCell.setChecked(zArr[num.intValue()], true);
                                                }
                                            }
                                        });
                                        size++;
                                    }
                                    z = canBlockUsers;
                                    if (LocaleController.isRTL) {
                                        f3 = 16.0f;
                                        f4 = 8.0f;
                                        i3 = AndroidUtilities.dp(8.0f);
                                    } else {
                                        f3 = 16.0f;
                                        i3 = AndroidUtilities.dp(16.0f);
                                        f4 = 8.0f;
                                    }
                                    if (LocaleController.isRTL) {
                                    }
                                    checkBoxCell2.setPadding(i3, 0, LocaleController.isRTL ? AndroidUtilities.dp(f4) : AndroidUtilities.dp(f3), 0);
                                    frameLayout.addView(checkBoxCell2, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) (48 * size), 0.0f, 0.0f));
                                    checkBoxCell2.setOnClickListener(/* anonymous class already generated */);
                                    size++;
                                } else {
                                    z = canBlockUsers;
                                }
                                i5++;
                                canBlockUsers = z;
                                i4 = 2;
                            }
                            builder.setView(frameLayout);
                            user = user2;
                        } else {
                            final AlertDialog[] alertDialogArr = new AlertDialog[]{new AlertDialog(getParentActivity(), 1)};
                            TLObject tL_channels_getParticipant = new TL_channels_getParticipant();
                            tL_channels_getParticipant.channel = MessagesController.getInputChannel(chatActivity.currentChat);
                            tL_channels_getParticipant.user_id = MessagesController.getInstance(chatActivity.currentAccount).getInputUser(user2);
                            i4 = ConnectionsManager.getInstance(chatActivity.currentAccount).sendRequest(tL_channels_getParticipant, new RequestDelegate() {
                                public void run(final TLObject tLObject, TL_error tL_error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                                            /*
                                            r4 = this;
                                            r0 = 0;
                                            r1 = org.telegram.ui.ChatActivity.AnonymousClass97.this;	 Catch:{ Throwable -> 0x000a }
                                            r1 = r0;	 Catch:{ Throwable -> 0x000a }
                                            r1 = r1[r0];	 Catch:{ Throwable -> 0x000a }
                                            r1.dismiss();	 Catch:{ Throwable -> 0x000a }
                                        L_0x000a:
                                            r1 = org.telegram.ui.ChatActivity.AnonymousClass97.this;
                                            r1 = r0;
                                            r2 = 0;
                                            r1[r0] = r2;
                                            r1 = 2;
                                            r2 = r1;
                                            if (r2 == 0) goto L_0x0027;
                                        L_0x0016:
                                            r2 = r1;
                                            r2 = (org.telegram.tgnet.TLRPC.TL_channels_channelParticipant) r2;
                                            r3 = r2.participant;
                                            r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
                                            if (r3 != 0) goto L_0x0027;
                                        L_0x0020:
                                            r2 = r2.participant;
                                            r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
                                            if (r2 != 0) goto L_0x0027;
                                        L_0x0026:
                                            goto L_0x0028;
                                        L_0x0027:
                                            r0 = r1;
                                        L_0x0028:
                                            r1 = org.telegram.ui.ChatActivity.AnonymousClass97.this;
                                            r1 = org.telegram.ui.ChatActivity.this;
                                            r2 = org.telegram.ui.ChatActivity.AnonymousClass97.this;
                                            r2 = r2;
                                            r3 = org.telegram.ui.ChatActivity.AnonymousClass97.this;
                                            r3 = r3;
                                            r1.createDeleteMessagesAlert(r2, r3, r0);
                                            return;
                                            */
                                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatActivity.97.1.run():void");
                                        }
                                    });
                                }
                            });
                            if (i4 != 0) {
                                AndroidUtilities.runOnUIThread(new Runnable() {

                                    /* renamed from: org.telegram.ui.ChatActivity$98$1 */
                                    class C10571 implements OnClickListener {
                                        C10571() {
                                        }

                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ConnectionsManager.getInstance(ChatActivity.this.currentAccount).cancelRequest(i4, true);
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
                                            alertDialogArr[0].setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new C10571());
                                            ChatActivity.this.showDialog(alertDialogArr[0]);
                                        }
                                    }
                                }, 1000);
                            }
                            return;
                        }
                    }
                }
                if (i3 != 0) {
                    frameLayout = new FrameLayout(getParentActivity());
                    checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    if (chatActivity.currentChat != null) {
                        checkBoxCell.setText(LocaleController.getString("DeleteForAll", C0446R.string.DeleteForAll), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                    } else {
                        checkBoxCell.setText(LocaleController.formatString("DeleteForUser", C0446R.string.DeleteForUser, UserObject.getFirstName(chatActivity.currentUser)), TtmlNode.ANONYMOUS_REGION_ID, false, false);
                    }
                    if (LocaleController.isRTL) {
                        f = 16.0f;
                        i6 = AndroidUtilities.dp(16.0f);
                        f2 = 8.0f;
                    } else {
                        f = 16.0f;
                        f2 = 8.0f;
                        i6 = AndroidUtilities.dp(8.0f);
                    }
                    checkBoxCell.setPadding(i6, 0, LocaleController.isRTL ? AndroidUtilities.dp(f2) : AndroidUtilities.dp(f), 0);
                    frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    checkBoxCell.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                            zArr2[0] = zArr2[0] ^ true;
                            checkBoxCell.setChecked(zArr2[0], true);
                        }
                    });
                    builder.setView(frameLayout);
                } else {
                    user2 = null;
                }
                user = user2;
            }
            final boolean[] zArr3 = zArr2;
            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {

                /* renamed from: org.telegram.ui.ChatActivity$102$1 */
                class C20101 implements RequestDelegate {
                    public void run(TLObject tLObject, TL_error tL_error) {
                    }

                    C20101() {
                    }
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    ArrayList arrayList = null;
                    ArrayList arrayList2;
                    MessageObject messageObject;
                    if (messageObject2 != null) {
                        ArrayList arrayList3;
                        dialogInterface = new ArrayList();
                        if (groupedMessages2 != null) {
                            arrayList2 = null;
                            for (int i2 = 0; i2 < groupedMessages2.messages.size(); i2++) {
                                messageObject = (MessageObject) groupedMessages2.messages.get(i2);
                                dialogInterface.add(Integer.valueOf(messageObject.getId()));
                                if (!(ChatActivity.this.currentEncryptedChat == null || messageObject.messageOwner.random_id == 0 || messageObject.type == 10)) {
                                    if (arrayList2 == null) {
                                        arrayList2 = new ArrayList();
                                    }
                                    arrayList2.add(Long.valueOf(messageObject.messageOwner.random_id));
                                }
                            }
                            arrayList3 = arrayList2;
                        } else {
                            dialogInterface.add(Integer.valueOf(messageObject2.getId()));
                            if (!(ChatActivity.this.currentEncryptedChat == null || messageObject2.messageOwner.random_id == 0 || messageObject2.type == 10)) {
                                arrayList = new ArrayList();
                                arrayList.add(Long.valueOf(messageObject2.messageOwner.random_id));
                            }
                            arrayList3 = arrayList;
                        }
                        MessagesController.getInstance(ChatActivity.this.currentAccount).deleteMessages(dialogInterface, arrayList3, ChatActivity.this.currentEncryptedChat, messageObject2.messageOwner.to_id.channel_id, zArr3[0]);
                    } else {
                        arrayList2 = null;
                        for (dialogInterface = 1; dialogInterface >= null; dialogInterface--) {
                            int i3;
                            ArrayList arrayList4;
                            int i4;
                            MessageObject messageObject2;
                            ArrayList arrayList5;
                            arrayList2 = new ArrayList();
                            for (int i5 = 0; i5 < ChatActivity.this.selectedMessagesIds[dialogInterface].size(); i5++) {
                                arrayList2.add(Integer.valueOf(ChatActivity.this.selectedMessagesIds[dialogInterface].keyAt(i5)));
                            }
                            if (!arrayList2.isEmpty()) {
                                messageObject = (MessageObject) ChatActivity.this.selectedMessagesIds[dialogInterface].get(((Integer) arrayList2.get(0)).intValue());
                                if (messageObject.messageOwner.to_id.channel_id != 0) {
                                    i3 = messageObject.messageOwner.to_id.channel_id;
                                    if (ChatActivity.this.currentEncryptedChat == null) {
                                        arrayList4 = new ArrayList();
                                        for (i4 = 0; i4 < ChatActivity.this.selectedMessagesIds[dialogInterface].size(); i4++) {
                                            messageObject2 = (MessageObject) ChatActivity.this.selectedMessagesIds[dialogInterface].valueAt(i4);
                                            if (!(messageObject2.messageOwner.random_id == 0 || messageObject2.type == 10)) {
                                                arrayList4.add(Long.valueOf(messageObject2.messageOwner.random_id));
                                            }
                                        }
                                        arrayList5 = arrayList4;
                                    } else {
                                        arrayList5 = null;
                                    }
                                    MessagesController.getInstance(ChatActivity.this.currentAccount).deleteMessages(arrayList2, arrayList5, ChatActivity.this.currentEncryptedChat, i3, zArr3[0]);
                                }
                            }
                            i3 = 0;
                            if (ChatActivity.this.currentEncryptedChat == null) {
                                arrayList5 = null;
                            } else {
                                arrayList4 = new ArrayList();
                                for (i4 = 0; i4 < ChatActivity.this.selectedMessagesIds[dialogInterface].size(); i4++) {
                                    messageObject2 = (MessageObject) ChatActivity.this.selectedMessagesIds[dialogInterface].valueAt(i4);
                                    arrayList4.add(Long.valueOf(messageObject2.messageOwner.random_id));
                                }
                                arrayList5 = arrayList4;
                            }
                            MessagesController.getInstance(ChatActivity.this.currentAccount).deleteMessages(arrayList2, arrayList5, ChatActivity.this.currentEncryptedChat, i3, zArr3[0]);
                        }
                        ChatActivity.this.actionBar.hideActionMode();
                        ChatActivity.this.updatePinnedMessageView(true);
                        dialogInterface = arrayList2;
                    }
                    if (user != 0) {
                        if (zArr[0] != 0) {
                            MessagesController.getInstance(ChatActivity.this.currentAccount).deleteUserFromChat(ChatActivity.this.currentChat.id, user, ChatActivity.this.info);
                        }
                        if (zArr[1] != 0) {
                            i = new TL_channels_reportSpam();
                            i.channel = MessagesController.getInputChannel(ChatActivity.this.currentChat);
                            i.user_id = MessagesController.getInstance(ChatActivity.this.currentAccount).getInputUser(user);
                            i.id = dialogInterface;
                            ConnectionsManager.getInstance(ChatActivity.this.currentAccount).sendRequest(i, new C20101());
                        }
                        if (zArr[2] != null) {
                            MessagesController.getInstance(ChatActivity.this.currentAccount).deleteUserChannelHistory(ChatActivity.this.currentChat, user, 0);
                        }
                    }
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    private void createMenu(View view, boolean z, boolean z2) {
        createMenu(view, z, z2, true);
    }

    private void createMenu(View view, boolean z, boolean z2, boolean z3) {
        View view2 = view;
        if (!this.actionBar.isActionModeShowed()) {
            GroupedMessages groupedMessages = null;
            MessageObject messageObject = view2 instanceof ChatMessageCell ? ((ChatMessageCell) view2).getMessageObject() : view2 instanceof ChatActionCell ? ((ChatActionCell) view2).getMessageObject() : null;
            if (messageObject != null) {
                int messageType = getMessageType(messageObject);
                if (z && (messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) {
                    scrollToMessageId(messageObject.messageOwner.reply_to_msg_id, messageObject.messageOwner.id, true, 0, false);
                    return;
                }
                boolean z4;
                ActionBarMenu createActionMode;
                View item;
                View item2;
                AnimatorSet animatorSet;
                Collection arrayList;
                int i;
                View view3;
                Builder builder;
                ArrayList arrayList2;
                final ArrayList arrayList3;
                TL_messageActionPhoneCall tL_messageActionPhoneCall;
                String str;
                int i2;
                User user;
                User user2;
                r6.selectedObject = null;
                r6.selectedObjectGroup = null;
                r6.forwardingMessage = null;
                r6.forwardingMessageGroup = null;
                for (int i3 = 1; i3 >= 0; i3--) {
                    r6.selectedMessagesCanCopyIds[i3].clear();
                    r6.selectedMessagesCanStarIds[i3].clear();
                    r6.selectedMessagesIds[i3].clear();
                }
                r6.cantDeleteMessagesCount = 0;
                r6.canEditMessagesCount = 0;
                r6.actionBar.hideActionMode();
                updatePinnedMessageView(true);
                if (z3) {
                    groupedMessages = getValidGroupedMessage(messageObject);
                }
                boolean z5 = messageObject.getDialogId() != r6.mergeDialogId && messageObject.getId() > 0 && ChatObject.isChannel(r6.currentChat) && ((r6.currentChat.creator || (r6.currentChat.admin_rights != null && ((r6.currentChat.megagroup && r6.currentChat.admin_rights.pin_messages) || (!r6.currentChat.megagroup && r6.currentChat.admin_rights.edit_messages)))) && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty)));
                boolean z6 = messageObject.getDialogId() != r6.mergeDialogId && r6.info != null && r6.info.pinned_msg_id == messageObject.getId() && (r6.currentChat.creator || (r6.currentChat.admin_rights != null && ((r6.currentChat.megagroup && r6.currentChat.admin_rights.pin_messages) || (!r6.currentChat.megagroup && r6.currentChat.admin_rights.edit_messages))));
                boolean z7 = groupedMessages == null && messageObject.canEditMessage(r6.currentChat) && !r6.chatActivityEnterView.hasAudioToSend() && messageObject.getDialogId() != r6.mergeDialogId;
                if ((r6.currentEncryptedChat == null || AndroidUtilities.getPeerLayerVersion(r6.currentEncryptedChat.layer) >= 46) && (!(messageType == 1 && (messageObject.getDialogId() == r6.mergeDialogId || messageObject.needDrawBluredPreview())) && ((r6.currentEncryptedChat != null || messageObject.getId() >= 0) && ((r6.bottomOverlayChat == null || r6.bottomOverlayChat.getVisibility() != 0) && !r6.isBroadcast)))) {
                    if (r6.currentChat != null) {
                        if (!ChatObject.isNotInChat(r6.currentChat) && (!ChatObject.isChannel(r6.currentChat) || ChatObject.canPost(r6.currentChat) || r6.currentChat.megagroup)) {
                            if (!ChatObject.canSendMessages(r6.currentChat)) {
                            }
                        }
                    }
                    z4 = true;
                    if (!(z || messageType < 2 || messageType == 20 || messageObject.needDrawBluredPreview())) {
                        if (messageObject.isLiveLocation()) {
                            createActionMode = r6.actionBar.createActionMode();
                            item = createActionMode.getItem(11);
                            if (item != null) {
                                item.setVisibility(0);
                            }
                            item2 = createActionMode.getItem(12);
                            if (item2 != null) {
                                item2.setVisibility(0);
                            }
                            r6.actionBar.showActionMode();
                            updatePinnedMessageView(true);
                            animatorSet = new AnimatorSet();
                            arrayList = new ArrayList();
                            for (i = 0; i < r6.actionModeViews.size(); i++) {
                                view3 = (View) r6.actionModeViews.get(i);
                                view3.setPivotY((float) (ActionBar.getCurrentActionBarHeight() / 2));
                                AndroidUtilities.clearDrawableAnimation(view3);
                                arrayList.add(ObjectAnimator.ofFloat(view3, "scaleY", new float[]{0.1f, 1.0f}));
                            }
                            animatorSet.playTogether(arrayList);
                            animatorSet.setDuration(250);
                            animatorSet.start();
                            addToSelectedMessages(messageObject, z2);
                            r6.selectedMessagesCountTextView.setNumber(r6.selectedMessagesIds[0].size() + r6.selectedMessagesIds[1].size(), false);
                            updateVisibleRows();
                            return;
                        }
                    }
                    if (messageType >= 0) {
                        r6.selectedObject = messageObject;
                        r6.selectedObjectGroup = groupedMessages;
                        if (getParentActivity() == null) {
                            builder = new Builder(getParentActivity());
                            arrayList2 = new ArrayList();
                            arrayList3 = new ArrayList();
                            if (messageType == 0) {
                                arrayList2.add(LocaleController.getString("Retry", C0446R.string.Retry));
                                arrayList3.add(Integer.valueOf(0));
                                arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                                arrayList3.add(Integer.valueOf(1));
                            } else if (messageType == 1) {
                                if (r6.currentChat != null || r6.isBroadcast) {
                                    if (messageObject.messageOwner.action != null && (messageObject.messageOwner.action instanceof TL_messageActionPhoneCall)) {
                                        tL_messageActionPhoneCall = (TL_messageActionPhoneCall) messageObject.messageOwner.action;
                                        if ((!(tL_messageActionPhoneCall.reason instanceof TL_phoneCallDiscardReasonMissed) || (tL_messageActionPhoneCall.reason instanceof TL_phoneCallDiscardReasonBusy)) && !messageObject.isOutOwner()) {
                                            str = "CallBack";
                                            i2 = C0446R.string.CallBack;
                                        } else {
                                            str = "CallAgain";
                                            i2 = C0446R.string.CallAgain;
                                        }
                                        arrayList2.add(LocaleController.getString(str, i2));
                                        arrayList3.add(Integer.valueOf(18));
                                        if (VoIPHelper.canRateCall(tL_messageActionPhoneCall)) {
                                            arrayList2.add(LocaleController.getString("CallMessageReportProblem", C0446R.string.CallMessageReportProblem));
                                            arrayList3.add(Integer.valueOf(19));
                                        }
                                    }
                                    if (z && r6.selectedObject.getId() > 0 && z4) {
                                        arrayList2.add(LocaleController.getString("Reply", C0446R.string.Reply));
                                        arrayList3.add(Integer.valueOf(8));
                                    }
                                    if (messageObject.canDeleteMessage(r6.currentChat)) {
                                        arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                                        arrayList3.add(Integer.valueOf(1));
                                    }
                                } else {
                                    if (z4) {
                                        arrayList2.add(LocaleController.getString("Reply", C0446R.string.Reply));
                                        arrayList3.add(Integer.valueOf(8));
                                    }
                                    if (z6) {
                                        arrayList2.add(LocaleController.getString("UnpinMessage", C0446R.string.UnpinMessage));
                                        arrayList3.add(Integer.valueOf(14));
                                    } else if (z5) {
                                        arrayList2.add(LocaleController.getString("PinMessage", C0446R.string.PinMessage));
                                        arrayList3.add(Integer.valueOf(13));
                                    }
                                    if (z7) {
                                        arrayList2.add(LocaleController.getString("Edit", C0446R.string.Edit));
                                        arrayList3.add(Integer.valueOf(12));
                                    }
                                    if (r6.selectedObject.contentType == 0 && r6.selectedObject.getId() > 0 && !r6.selectedObject.isOut() && (ChatObject.isChannel(r6.currentChat) || (r6.currentUser != null && r6.currentUser.bot))) {
                                        arrayList2.add(LocaleController.getString("ReportChat", C0446R.string.ReportChat));
                                        arrayList3.add(Integer.valueOf(edit));
                                    }
                                    if (messageObject.canDeleteMessage(r6.currentChat)) {
                                        arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                                        arrayList3.add(Integer.valueOf(1));
                                    }
                                }
                            } else if (messageType == 20) {
                                arrayList2.add(LocaleController.getString("Retry", C0446R.string.Retry));
                                arrayList3.add(Integer.valueOf(0));
                                arrayList2.add(LocaleController.getString("Copy", C0446R.string.Copy));
                                arrayList3.add(Integer.valueOf(3));
                                arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                                arrayList3.add(Integer.valueOf(1));
                            } else if (r6.currentEncryptedChat != null) {
                                if (z4) {
                                    arrayList2.add(LocaleController.getString("Reply", C0446R.string.Reply));
                                    arrayList3.add(Integer.valueOf(8));
                                }
                                if (r6.selectedObject.type == 0 || r6.selectedObject.caption != null) {
                                    arrayList2.add(LocaleController.getString("Copy", C0446R.string.Copy));
                                    arrayList3.add(Integer.valueOf(3));
                                }
                                if (ChatObject.isChannel(r6.currentChat) && r6.currentChat.megagroup && !TextUtils.isEmpty(r6.currentChat.username) && ChatObject.hasAdminRights(r6.currentChat)) {
                                    arrayList2.add(LocaleController.getString("CopyLink", C0446R.string.CopyLink));
                                    arrayList3.add(Integer.valueOf(22));
                                }
                                if (messageType == 3) {
                                    if ((r6.selectedObject.messageOwner.media instanceof TL_messageMediaWebPage) && MessageObject.isNewGifDocument(r6.selectedObject.messageOwner.media.webpage.document)) {
                                        arrayList2.add(LocaleController.getString("SaveToGIFs", C0446R.string.SaveToGIFs));
                                        arrayList3.add(Integer.valueOf(11));
                                    }
                                } else if (messageType != 4) {
                                    if (r6.selectedObject.isVideo()) {
                                        if (r6.selectedObject.isMusic()) {
                                            arrayList2.add(LocaleController.getString("SaveToMusic", C0446R.string.SaveToMusic));
                                            arrayList3.add(Integer.valueOf(10));
                                            arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                            arrayList3.add(Integer.valueOf(6));
                                        } else if (r6.selectedObject.getDocument() != null) {
                                            if (MessageObject.isNewGifDocument(r6.selectedObject.getDocument())) {
                                                arrayList2.add(LocaleController.getString("SaveToGIFs", C0446R.string.SaveToGIFs));
                                                arrayList3.add(Integer.valueOf(11));
                                            }
                                            arrayList2.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                                            arrayList3.add(Integer.valueOf(10));
                                            arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                            arrayList3.add(Integer.valueOf(6));
                                        } else if (!r6.selectedObject.needDrawBluredPreview()) {
                                            arrayList2.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                                            arrayList3.add(Integer.valueOf(4));
                                        }
                                    } else if (!r6.selectedObject.needDrawBluredPreview()) {
                                        arrayList2.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                                        arrayList3.add(Integer.valueOf(4));
                                        arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                        arrayList3.add(Integer.valueOf(6));
                                    }
                                } else if (messageType == 5) {
                                    arrayList2.add(LocaleController.getString("ApplyLocalizationFile", C0446R.string.ApplyLocalizationFile));
                                    arrayList3.add(Integer.valueOf(5));
                                    arrayList2.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                                    arrayList3.add(Integer.valueOf(10));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(6));
                                } else if (messageType == 10) {
                                    arrayList2.add(LocaleController.getString("ApplyThemeFile", C0446R.string.ApplyThemeFile));
                                    arrayList3.add(Integer.valueOf(5));
                                    arrayList2.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                                    arrayList3.add(Integer.valueOf(10));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(6));
                                } else if (messageType == 6) {
                                    arrayList2.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                                    arrayList3.add(Integer.valueOf(7));
                                    arrayList2.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                                    arrayList3.add(Integer.valueOf(10));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(6));
                                } else if (messageType != 7) {
                                    if (r6.selectedObject.isMask()) {
                                        arrayList2.add(LocaleController.getString("AddToStickers", C0446R.string.AddToStickers));
                                        arrayList3.add(Integer.valueOf(9));
                                        if (!DataQuery.getInstance(r6.currentAccount).isStickerInFavorites(r6.selectedObject.getDocument())) {
                                            arrayList2.add(LocaleController.getString("DeleteFromFavorites", C0446R.string.DeleteFromFavorites));
                                            arrayList3.add(Integer.valueOf(21));
                                        } else if (DataQuery.getInstance(r6.currentAccount).canAddStickerToFavorites()) {
                                            arrayList2.add(LocaleController.getString("AddToFavorites", C0446R.string.AddToFavorites));
                                            arrayList3.add(Integer.valueOf(20));
                                        }
                                    } else {
                                        arrayList2.add(LocaleController.getString("AddToMasks", C0446R.string.AddToMasks));
                                        arrayList3.add(Integer.valueOf(9));
                                    }
                                } else if (messageType == 8) {
                                    user = MessagesController.getInstance(r6.currentAccount).getUser(Integer.valueOf(r6.selectedObject.messageOwner.media.user_id));
                                    if (!(user == null || user.id == UserConfig.getInstance(r6.currentAccount).getClientUserId() || ContactsController.getInstance(r6.currentAccount).contactsDict.get(Integer.valueOf(user.id)) != null)) {
                                        arrayList2.add(LocaleController.getString("AddContactTitle", C0446R.string.AddContactTitle));
                                        arrayList3.add(Integer.valueOf(15));
                                    }
                                    if (!TextUtils.isEmpty(r6.selectedObject.messageOwner.media.phone_number)) {
                                        arrayList2.add(LocaleController.getString("Copy", C0446R.string.Copy));
                                        arrayList3.add(Integer.valueOf(16));
                                        arrayList2.add(LocaleController.getString("Call", C0446R.string.Call));
                                        arrayList3.add(Integer.valueOf(17));
                                    }
                                } else if (messageType == 9) {
                                    if (DataQuery.getInstance(r6.currentAccount).isStickerInFavorites(r6.selectedObject.getDocument())) {
                                        arrayList2.add(LocaleController.getString("AddToFavorites", C0446R.string.AddToFavorites));
                                        arrayList3.add(Integer.valueOf(20));
                                    } else {
                                        arrayList2.add(LocaleController.getString("DeleteFromFavorites", C0446R.string.DeleteFromFavorites));
                                        arrayList3.add(Integer.valueOf(21));
                                    }
                                }
                                if (!(r6.selectedObject.needDrawBluredPreview() || r6.selectedObject.isLiveLocation())) {
                                    arrayList2.add(LocaleController.getString("Forward", C0446R.string.Forward));
                                    arrayList3.add(Integer.valueOf(2));
                                }
                                if (z6) {
                                    arrayList2.add(LocaleController.getString("UnpinMessage", C0446R.string.UnpinMessage));
                                    arrayList3.add(Integer.valueOf(14));
                                } else if (z5) {
                                    arrayList2.add(LocaleController.getString("PinMessage", C0446R.string.PinMessage));
                                    arrayList3.add(Integer.valueOf(13));
                                }
                                if (z7) {
                                    arrayList2.add(LocaleController.getString("Edit", C0446R.string.Edit));
                                    arrayList3.add(Integer.valueOf(12));
                                }
                                if (r6.selectedObject.contentType == 0 && r6.selectedObject.getId() > 0 && !r6.selectedObject.isOut() && (ChatObject.isChannel(r6.currentChat) || (r6.currentUser != null && r6.currentUser.bot))) {
                                    arrayList2.add(LocaleController.getString("ReportChat", C0446R.string.ReportChat));
                                    arrayList3.add(Integer.valueOf(edit));
                                }
                                if (messageObject.canDeleteMessage(r6.currentChat)) {
                                    arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                                    arrayList3.add(Integer.valueOf(1));
                                }
                            } else {
                                if (z4) {
                                    arrayList2.add(LocaleController.getString("Reply", C0446R.string.Reply));
                                    arrayList3.add(Integer.valueOf(8));
                                }
                                if (r6.selectedObject.type == 0 || r6.selectedObject.caption != null) {
                                    arrayList2.add(LocaleController.getString("Copy", C0446R.string.Copy));
                                    arrayList3.add(Integer.valueOf(3));
                                }
                                if (messageType != 4) {
                                    if (r6.selectedObject.isVideo()) {
                                        arrayList2.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                                        arrayList3.add(Integer.valueOf(4));
                                        arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                        arrayList3.add(Integer.valueOf(6));
                                    } else if (r6.selectedObject.isMusic()) {
                                        arrayList2.add(LocaleController.getString("SaveToMusic", C0446R.string.SaveToMusic));
                                        arrayList3.add(Integer.valueOf(10));
                                        arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                        arrayList3.add(Integer.valueOf(6));
                                    } else if (!r6.selectedObject.isVideo() || r6.selectedObject.getDocument() == null) {
                                        arrayList2.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                                        arrayList3.add(Integer.valueOf(4));
                                    } else {
                                        arrayList2.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                                        arrayList3.add(Integer.valueOf(10));
                                        arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                        arrayList3.add(Integer.valueOf(6));
                                    }
                                } else if (messageType == 5) {
                                    arrayList2.add(LocaleController.getString("ApplyLocalizationFile", C0446R.string.ApplyLocalizationFile));
                                    arrayList3.add(Integer.valueOf(5));
                                } else if (messageType == 10) {
                                    arrayList2.add(LocaleController.getString("ApplyThemeFile", C0446R.string.ApplyThemeFile));
                                    arrayList3.add(Integer.valueOf(5));
                                } else if (messageType == 7) {
                                    arrayList2.add(LocaleController.getString("AddToStickers", C0446R.string.AddToStickers));
                                    arrayList3.add(Integer.valueOf(9));
                                } else if (messageType == 8) {
                                    user2 = MessagesController.getInstance(r6.currentAccount).getUser(Integer.valueOf(r6.selectedObject.messageOwner.media.user_id));
                                    if (!(user2 == null || user2.id == UserConfig.getInstance(r6.currentAccount).getClientUserId() || ContactsController.getInstance(r6.currentAccount).contactsDict.get(Integer.valueOf(user2.id)) != null)) {
                                        arrayList2.add(LocaleController.getString("AddContactTitle", C0446R.string.AddContactTitle));
                                        arrayList3.add(Integer.valueOf(15));
                                    }
                                    if (!TextUtils.isEmpty(r6.selectedObject.messageOwner.media.phone_number)) {
                                        arrayList2.add(LocaleController.getString("Copy", C0446R.string.Copy));
                                        arrayList3.add(Integer.valueOf(16));
                                        arrayList2.add(LocaleController.getString("Call", C0446R.string.Call));
                                        arrayList3.add(Integer.valueOf(17));
                                    }
                                }
                                arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                                arrayList3.add(Integer.valueOf(1));
                            }
                            if (arrayList3.isEmpty()) {
                                builder.setItems((CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]), new OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (ChatActivity.this.selectedObject != null && i >= 0) {
                                            if (i < arrayList3.size()) {
                                                ChatActivity.this.processSelectedOption(((Integer) arrayList3.get(i)).intValue());
                                            }
                                        }
                                    }
                                });
                                builder.setTitle(LocaleController.getString("Message", C0446R.string.Message));
                                showDialog(builder.create());
                            }
                        }
                    }
                }
                z4 = false;
                if (messageObject.isLiveLocation()) {
                    createActionMode = r6.actionBar.createActionMode();
                    item = createActionMode.getItem(11);
                    if (item != null) {
                        item.setVisibility(0);
                    }
                    item2 = createActionMode.getItem(12);
                    if (item2 != null) {
                        item2.setVisibility(0);
                    }
                    r6.actionBar.showActionMode();
                    updatePinnedMessageView(true);
                    animatorSet = new AnimatorSet();
                    arrayList = new ArrayList();
                    for (i = 0; i < r6.actionModeViews.size(); i++) {
                        view3 = (View) r6.actionModeViews.get(i);
                        view3.setPivotY((float) (ActionBar.getCurrentActionBarHeight() / 2));
                        AndroidUtilities.clearDrawableAnimation(view3);
                        arrayList.add(ObjectAnimator.ofFloat(view3, "scaleY", new float[]{0.1f, 1.0f}));
                    }
                    animatorSet.playTogether(arrayList);
                    animatorSet.setDuration(250);
                    animatorSet.start();
                    addToSelectedMessages(messageObject, z2);
                    r6.selectedMessagesCountTextView.setNumber(r6.selectedMessagesIds[0].size() + r6.selectedMessagesIds[1].size(), false);
                    updateVisibleRows();
                    return;
                }
                if (messageType >= 0) {
                    r6.selectedObject = messageObject;
                    r6.selectedObjectGroup = groupedMessages;
                    if (getParentActivity() == null) {
                        builder = new Builder(getParentActivity());
                        arrayList2 = new ArrayList();
                        arrayList3 = new ArrayList();
                        if (messageType == 0) {
                            arrayList2.add(LocaleController.getString("Retry", C0446R.string.Retry));
                            arrayList3.add(Integer.valueOf(0));
                            arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                            arrayList3.add(Integer.valueOf(1));
                        } else if (messageType == 1) {
                            if (r6.currentChat != null) {
                            }
                            tL_messageActionPhoneCall = (TL_messageActionPhoneCall) messageObject.messageOwner.action;
                            if (tL_messageActionPhoneCall.reason instanceof TL_phoneCallDiscardReasonMissed) {
                            }
                            str = "CallBack";
                            i2 = C0446R.string.CallBack;
                            arrayList2.add(LocaleController.getString(str, i2));
                            arrayList3.add(Integer.valueOf(18));
                            if (VoIPHelper.canRateCall(tL_messageActionPhoneCall)) {
                                arrayList2.add(LocaleController.getString("CallMessageReportProblem", C0446R.string.CallMessageReportProblem));
                                arrayList3.add(Integer.valueOf(19));
                            }
                            arrayList2.add(LocaleController.getString("Reply", C0446R.string.Reply));
                            arrayList3.add(Integer.valueOf(8));
                            if (messageObject.canDeleteMessage(r6.currentChat)) {
                                arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                                arrayList3.add(Integer.valueOf(1));
                            }
                        } else if (messageType == 20) {
                            arrayList2.add(LocaleController.getString("Retry", C0446R.string.Retry));
                            arrayList3.add(Integer.valueOf(0));
                            arrayList2.add(LocaleController.getString("Copy", C0446R.string.Copy));
                            arrayList3.add(Integer.valueOf(3));
                            arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                            arrayList3.add(Integer.valueOf(1));
                        } else if (r6.currentEncryptedChat != null) {
                            if (z4) {
                                arrayList2.add(LocaleController.getString("Reply", C0446R.string.Reply));
                                arrayList3.add(Integer.valueOf(8));
                            }
                            arrayList2.add(LocaleController.getString("Copy", C0446R.string.Copy));
                            arrayList3.add(Integer.valueOf(3));
                            if (messageType != 4) {
                                if (messageType == 5) {
                                    arrayList2.add(LocaleController.getString("ApplyLocalizationFile", C0446R.string.ApplyLocalizationFile));
                                    arrayList3.add(Integer.valueOf(5));
                                } else if (messageType == 10) {
                                    arrayList2.add(LocaleController.getString("ApplyThemeFile", C0446R.string.ApplyThemeFile));
                                    arrayList3.add(Integer.valueOf(5));
                                } else if (messageType == 7) {
                                    arrayList2.add(LocaleController.getString("AddToStickers", C0446R.string.AddToStickers));
                                    arrayList3.add(Integer.valueOf(9));
                                } else if (messageType == 8) {
                                    user2 = MessagesController.getInstance(r6.currentAccount).getUser(Integer.valueOf(r6.selectedObject.messageOwner.media.user_id));
                                    arrayList2.add(LocaleController.getString("AddContactTitle", C0446R.string.AddContactTitle));
                                    arrayList3.add(Integer.valueOf(15));
                                    if (TextUtils.isEmpty(r6.selectedObject.messageOwner.media.phone_number)) {
                                        arrayList2.add(LocaleController.getString("Copy", C0446R.string.Copy));
                                        arrayList3.add(Integer.valueOf(16));
                                        arrayList2.add(LocaleController.getString("Call", C0446R.string.Call));
                                        arrayList3.add(Integer.valueOf(17));
                                    }
                                }
                            } else if (r6.selectedObject.isVideo()) {
                                arrayList2.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                                arrayList3.add(Integer.valueOf(4));
                                arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                arrayList3.add(Integer.valueOf(6));
                            } else if (r6.selectedObject.isMusic()) {
                                arrayList2.add(LocaleController.getString("SaveToMusic", C0446R.string.SaveToMusic));
                                arrayList3.add(Integer.valueOf(10));
                                arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                arrayList3.add(Integer.valueOf(6));
                            } else {
                                if (r6.selectedObject.isVideo()) {
                                }
                                arrayList2.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                                arrayList3.add(Integer.valueOf(4));
                            }
                            arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                            arrayList3.add(Integer.valueOf(1));
                        } else {
                            if (z4) {
                                arrayList2.add(LocaleController.getString("Reply", C0446R.string.Reply));
                                arrayList3.add(Integer.valueOf(8));
                            }
                            arrayList2.add(LocaleController.getString("Copy", C0446R.string.Copy));
                            arrayList3.add(Integer.valueOf(3));
                            arrayList2.add(LocaleController.getString("CopyLink", C0446R.string.CopyLink));
                            arrayList3.add(Integer.valueOf(22));
                            if (messageType == 3) {
                                arrayList2.add(LocaleController.getString("SaveToGIFs", C0446R.string.SaveToGIFs));
                                arrayList3.add(Integer.valueOf(11));
                            } else if (messageType != 4) {
                                if (messageType == 5) {
                                    arrayList2.add(LocaleController.getString("ApplyLocalizationFile", C0446R.string.ApplyLocalizationFile));
                                    arrayList3.add(Integer.valueOf(5));
                                    arrayList2.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                                    arrayList3.add(Integer.valueOf(10));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(6));
                                } else if (messageType == 10) {
                                    arrayList2.add(LocaleController.getString("ApplyThemeFile", C0446R.string.ApplyThemeFile));
                                    arrayList3.add(Integer.valueOf(5));
                                    arrayList2.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                                    arrayList3.add(Integer.valueOf(10));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(6));
                                } else if (messageType == 6) {
                                    arrayList2.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                                    arrayList3.add(Integer.valueOf(7));
                                    arrayList2.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                                    arrayList3.add(Integer.valueOf(10));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(6));
                                } else if (messageType != 7) {
                                    if (messageType == 8) {
                                        user = MessagesController.getInstance(r6.currentAccount).getUser(Integer.valueOf(r6.selectedObject.messageOwner.media.user_id));
                                        arrayList2.add(LocaleController.getString("AddContactTitle", C0446R.string.AddContactTitle));
                                        arrayList3.add(Integer.valueOf(15));
                                        if (TextUtils.isEmpty(r6.selectedObject.messageOwner.media.phone_number)) {
                                            arrayList2.add(LocaleController.getString("Copy", C0446R.string.Copy));
                                            arrayList3.add(Integer.valueOf(16));
                                            arrayList2.add(LocaleController.getString("Call", C0446R.string.Call));
                                            arrayList3.add(Integer.valueOf(17));
                                        }
                                    } else if (messageType == 9) {
                                        if (DataQuery.getInstance(r6.currentAccount).isStickerInFavorites(r6.selectedObject.getDocument())) {
                                            arrayList2.add(LocaleController.getString("DeleteFromFavorites", C0446R.string.DeleteFromFavorites));
                                            arrayList3.add(Integer.valueOf(21));
                                        } else {
                                            arrayList2.add(LocaleController.getString("AddToFavorites", C0446R.string.AddToFavorites));
                                            arrayList3.add(Integer.valueOf(20));
                                        }
                                    }
                                } else if (r6.selectedObject.isMask()) {
                                    arrayList2.add(LocaleController.getString("AddToStickers", C0446R.string.AddToStickers));
                                    arrayList3.add(Integer.valueOf(9));
                                    if (!DataQuery.getInstance(r6.currentAccount).isStickerInFavorites(r6.selectedObject.getDocument())) {
                                        arrayList2.add(LocaleController.getString("DeleteFromFavorites", C0446R.string.DeleteFromFavorites));
                                        arrayList3.add(Integer.valueOf(21));
                                    } else if (DataQuery.getInstance(r6.currentAccount).canAddStickerToFavorites()) {
                                        arrayList2.add(LocaleController.getString("AddToFavorites", C0446R.string.AddToFavorites));
                                        arrayList3.add(Integer.valueOf(20));
                                    }
                                } else {
                                    arrayList2.add(LocaleController.getString("AddToMasks", C0446R.string.AddToMasks));
                                    arrayList3.add(Integer.valueOf(9));
                                }
                            } else if (r6.selectedObject.isVideo()) {
                                if (r6.selectedObject.isMusic()) {
                                    arrayList2.add(LocaleController.getString("SaveToMusic", C0446R.string.SaveToMusic));
                                    arrayList3.add(Integer.valueOf(10));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(6));
                                } else if (r6.selectedObject.getDocument() != null) {
                                    if (MessageObject.isNewGifDocument(r6.selectedObject.getDocument())) {
                                        arrayList2.add(LocaleController.getString("SaveToGIFs", C0446R.string.SaveToGIFs));
                                        arrayList3.add(Integer.valueOf(11));
                                    }
                                    arrayList2.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                                    arrayList3.add(Integer.valueOf(10));
                                    arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                    arrayList3.add(Integer.valueOf(6));
                                } else if (r6.selectedObject.needDrawBluredPreview()) {
                                    arrayList2.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                                    arrayList3.add(Integer.valueOf(4));
                                }
                            } else if (r6.selectedObject.needDrawBluredPreview()) {
                                arrayList2.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                                arrayList3.add(Integer.valueOf(4));
                                arrayList2.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                                arrayList3.add(Integer.valueOf(6));
                            }
                            arrayList2.add(LocaleController.getString("Forward", C0446R.string.Forward));
                            arrayList3.add(Integer.valueOf(2));
                            if (z6) {
                                arrayList2.add(LocaleController.getString("UnpinMessage", C0446R.string.UnpinMessage));
                                arrayList3.add(Integer.valueOf(14));
                            } else if (z5) {
                                arrayList2.add(LocaleController.getString("PinMessage", C0446R.string.PinMessage));
                                arrayList3.add(Integer.valueOf(13));
                            }
                            if (z7) {
                                arrayList2.add(LocaleController.getString("Edit", C0446R.string.Edit));
                                arrayList3.add(Integer.valueOf(12));
                            }
                            arrayList2.add(LocaleController.getString("ReportChat", C0446R.string.ReportChat));
                            arrayList3.add(Integer.valueOf(edit));
                            if (messageObject.canDeleteMessage(r6.currentChat)) {
                                arrayList2.add(LocaleController.getString("Delete", C0446R.string.Delete));
                                arrayList3.add(Integer.valueOf(1));
                            }
                        }
                        if (arrayList3.isEmpty()) {
                            builder.setItems((CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]), /* anonymous class already generated */);
                            builder.setTitle(LocaleController.getString("Message", C0446R.string.Message));
                            showDialog(builder.create());
                        }
                    }
                }
            }
        }
    }

    private void startEditingMessageObject(MessageObject messageObject) {
        if (messageObject != null) {
            if (getParentActivity() != null) {
                if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                    this.actionBar.closeSearchField();
                    this.chatActivityEnterView.setFieldFocused();
                }
                this.mentionsAdapter.setNeedBotContext(false);
                this.chatListView.setOnItemLongClickListener((OnItemLongClickListenerExtended) null);
                this.chatListView.setOnItemClickListener((OnItemClickListenerExtended) null);
                this.chatListView.setClickable(false);
                this.chatListView.setLongClickable(false);
                this.chatActivityEnterView.setEditingMessageObject(messageObject, messageObject.isMediaEmpty() ^ true);
                updateBottomOverlay();
                this.actionModeTitleContainer.setVisibility(0);
                this.selectedMessagesCountTextView.setVisibility(8);
                checkEditTimer();
                this.chatActivityEnterView.setAllowStickersAndGifs(false, false);
                ActionBarMenu createActionMode = this.actionBar.createActionMode();
                createActionMode.getItem(19).setVisibility(8);
                createActionMode.getItem(10).setVisibility(8);
                if (createActionMode.getItem(11) != null) {
                    createActionMode.getItem(11).setVisibility(8);
                }
                createActionMode.getItem(12).setVisibility(8);
                createActionMode.getItem(edit).setVisibility(8);
                createActionMode.getItem(22).setVisibility(8);
                this.actionBar.showActionMode();
                updatePinnedMessageView(true);
                updateVisibleRows();
                TLObject tL_messages_getMessageEditData = new TL_messages_getMessageEditData();
                tL_messages_getMessageEditData.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) this.dialog_id);
                tL_messages_getMessageEditData.id = messageObject.getId();
                this.editingMessageObjectReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMessageEditData, new RequestDelegate() {
                    public void run(final TLObject tLObject, TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                ChatActivity.this.editingMessageObjectReqId = 0;
                                if (tLObject == null) {
                                    Builder builder = new Builder(ChatActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                    builder.setMessage(LocaleController.getString("EditMessageError", C0446R.string.EditMessageError));
                                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
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
            }
        }
    }

    private String getMessageContent(MessageObject messageObject, int i, boolean z) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        if (z && i != messageObject.messageOwner.from_id) {
            if (messageObject.messageOwner.from_id > 0) {
                i = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (i != 0) {
                    z = new StringBuilder();
                    z.append(ContactsController.formatName(i.first_name, i.last_name));
                    z.append(":\n");
                    str = z.toString();
                }
            } else if (messageObject.messageOwner.from_id < 0) {
                i = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-messageObject.messageOwner.from_id));
                if (i != 0) {
                    z = new StringBuilder();
                    z.append(i.title);
                    z.append(":\n");
                    str = z.toString();
                }
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != 0) {
            i = new StringBuilder();
            i.append(str);
            i.append(messageObject.messageOwner.message);
            return i.toString();
        } else if (messageObject.messageOwner.media == 0 || messageObject.messageOwner.message == 0) {
            i = new StringBuilder();
            i.append(str);
            i.append(messageObject.messageText);
            return i.toString();
        } else {
            i = new StringBuilder();
            i.append(str);
            i.append(messageObject.messageOwner.message);
            return i.toString();
        }
    }

    private void saveMessageToGallery(MessageObject messageObject) {
        String str = messageObject.messageOwner.attachPath;
        if (!(TextUtils.isEmpty(str) || new File(str).exists())) {
            str = null;
        }
        if (TextUtils.isEmpty(str)) {
            str = FileLoader.getPathToMessage(messageObject.messageOwner).toString();
        }
        MediaController.saveFile(str, getParentActivity(), messageObject.isVideo(), null, null);
    }

    private void processSelectedOption(int r23) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r22 = this;
        r7 = r22;
        r1 = r7.selectedObject;
        if (r1 == 0) goto L_0x05eb;
    L_0x0006:
        r1 = r22.getParentActivity();
        if (r1 != 0) goto L_0x000e;
    L_0x000c:
        goto L_0x05eb;
    L_0x000e:
        r1 = 3;
        r2 = NUM; // 0x7f0c0107 float:1.8609725E38 double:1.0530975284E-314;
        r3 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = 4;
        r8 = 23;
        r9 = 2;
        r10 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
        r11 = NUM; // 0x7f0c048c float:1.8611553E38 double:1.0530979736E-314;
        r12 = 1;
        r13 = 0;
        r14 = 0;
        switch(r23) {
            case 0: goto L_0x05a3;
            case 1: goto L_0x0590;
            case 2: goto L_0x056d;
            case 3: goto L_0x0562;
            case 4: goto L_0x051b;
            case 5: goto L_0x03fe;
            case 6: goto L_0x0377;
            case 7: goto L_0x031e;
            case 8: goto L_0x0312;
            case 9: goto L_0x02e6;
            case 10: goto L_0x025a;
            case 11: goto L_0x0241;
            case 12: goto L_0x0236;
            case 13: goto L_0x0170;
            case 14: goto L_0x0132;
            case 15: goto L_0x0104;
            case 16: goto L_0x00f7;
            case 17: goto L_0x00be;
            case 18: goto L_0x00a1;
            case 19: goto L_0x0090;
            case 20: goto L_0x0079;
            case 21: goto L_0x0062;
            case 22: goto L_0x003d;
            case 23: goto L_0x0028;
            default: goto L_0x0026;
        };
    L_0x0026:
        goto L_0x05e6;
    L_0x0028:
        r1 = r22.getParentActivity();
        r2 = r7.dialog_id;
        r4 = r7.selectedObject;
        r4 = r4.getId();
        r1 = org.telegram.ui.Components.AlertsCreator.createReportAlert(r1, r2, r4, r7);
        r7.showDialog(r1);
        goto L_0x05e6;
    L_0x003d:
        r1 = new org.telegram.tgnet.TLRPC$TL_channels_exportMessageLink;
        r1.<init>();
        r2 = r7.selectedObject;
        r2 = r2.getId();
        r1.id = r2;
        r2 = r7.currentChat;
        r2 = org.telegram.messenger.MessagesController.getInputChannel(r2);
        r1.channel = r2;
        r2 = r7.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r3 = new org.telegram.ui.ChatActivity$108;
        r3.<init>();
        r2.sendRequest(r1, r3);
        goto L_0x05e6;
    L_0x0062:
        r1 = r7.currentAccount;
        r1 = org.telegram.messenger.DataQuery.getInstance(r1);
        r2 = r7.selectedObject;
        r2 = r2.getDocument();
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 / r4;
        r3 = (int) r10;
        r1.addRecentSticker(r9, r2, r3, r12);
        goto L_0x05e6;
    L_0x0079:
        r1 = r7.currentAccount;
        r1 = org.telegram.messenger.DataQuery.getInstance(r1);
        r2 = r7.selectedObject;
        r2 = r2.getDocument();
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 / r4;
        r3 = (int) r10;
        r1.addRecentSticker(r9, r2, r3, r13);
        goto L_0x05e6;
    L_0x0090:
        r1 = r22.getParentActivity();
        r2 = r7.selectedObject;
        r2 = r2.messageOwner;
        r2 = r2.action;
        r2 = (org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall) r2;
        org.telegram.ui.Components.voip.VoIPHelper.showRateAlert(r1, r2);
        goto L_0x05e6;
    L_0x00a1:
        r1 = r7.currentUser;
        if (r1 == 0) goto L_0x05e6;
    L_0x00a5:
        r1 = r7.currentUser;
        r2 = r22.getParentActivity();
        r3 = r7.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r4 = r7.currentUser;
        r4 = r4.id;
        r3 = r3.getUserFull(r4);
        org.telegram.ui.Components.voip.VoIPHelper.startCall(r1, r2, r3);
        goto L_0x05e6;
    L_0x00be:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x00f0 }
        r2 = "android.intent.action.DIAL";	 Catch:{ Exception -> 0x00f0 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00f0 }
        r4.<init>();	 Catch:{ Exception -> 0x00f0 }
        r5 = "tel:";	 Catch:{ Exception -> 0x00f0 }
        r4.append(r5);	 Catch:{ Exception -> 0x00f0 }
        r5 = r7.selectedObject;	 Catch:{ Exception -> 0x00f0 }
        r5 = r5.messageOwner;	 Catch:{ Exception -> 0x00f0 }
        r5 = r5.media;	 Catch:{ Exception -> 0x00f0 }
        r5 = r5.phone_number;	 Catch:{ Exception -> 0x00f0 }
        r4.append(r5);	 Catch:{ Exception -> 0x00f0 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x00f0 }
        r4 = android.net.Uri.parse(r4);	 Catch:{ Exception -> 0x00f0 }
        r1.<init>(r2, r4);	 Catch:{ Exception -> 0x00f0 }
        r2 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;	 Catch:{ Exception -> 0x00f0 }
        r1.addFlags(r2);	 Catch:{ Exception -> 0x00f0 }
        r2 = r22.getParentActivity();	 Catch:{ Exception -> 0x00f0 }
        r2.startActivityForResult(r1, r3);	 Catch:{ Exception -> 0x00f0 }
        goto L_0x05e6;
    L_0x00f0:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
        goto L_0x05e6;
    L_0x00f7:
        r1 = r7.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.phone_number;
        org.telegram.messenger.AndroidUtilities.addToClipboard(r1);
        goto L_0x05e6;
    L_0x0104:
        r1 = new android.os.Bundle;
        r1.<init>();
        r2 = "user_id";
        r3 = r7.selectedObject;
        r3 = r3.messageOwner;
        r3 = r3.media;
        r3 = r3.user_id;
        r1.putInt(r2, r3);
        r2 = "phone";
        r3 = r7.selectedObject;
        r3 = r3.messageOwner;
        r3 = r3.media;
        r3 = r3.phone_number;
        r1.putString(r2, r3);
        r2 = "addContact";
        r1.putBoolean(r2, r12);
        r2 = new org.telegram.ui.ContactAddActivity;
        r2.<init>(r1);
        r7.presentFragment(r2);
        goto L_0x05e6;
    L_0x0132:
        r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r3 = r22.getParentActivity();
        r1.<init>(r3);
        r3 = "UnpinMessageAlert";
        r4 = NUM; // 0x7f0c0673 float:1.861254E38 double:1.053098214E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);
        r1.setMessage(r3);
        r3 = "OK";
        r3 = org.telegram.messenger.LocaleController.getString(r3, r11);
        r4 = new org.telegram.ui.ChatActivity$107;
        r4.<init>();
        r1.setPositiveButton(r3, r4);
        r3 = "AppName";
        r3 = org.telegram.messenger.LocaleController.getString(r3, r10);
        r1.setTitle(r3);
        r3 = "Cancel";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setNegativeButton(r2, r14);
        r1 = r1.create();
        r7.showDialog(r1);
        goto L_0x05e6;
    L_0x0170:
        r1 = r7.selectedObject;
        r1 = r1.getId();
        r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r4 = r22.getParentActivity();
        r3.<init>(r4);
        r4 = r7.currentChat;
        r4 = org.telegram.messenger.ChatObject.isChannel(r4);
        if (r4 == 0) goto L_0x01fd;
    L_0x0187:
        r4 = r7.currentChat;
        r4 = r4.megagroup;
        if (r4 == 0) goto L_0x01fd;
    L_0x018d:
        r4 = "PinMessageAlert";
        r5 = NUM; // 0x7f0c0517 float:1.8611835E38 double:1.053098042E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r3.setMessage(r4);
        r4 = new boolean[r12];
        r4[r13] = r12;
        r5 = new android.widget.FrameLayout;
        r6 = r22.getParentActivity();
        r5.<init>(r6);
        r6 = new org.telegram.ui.Cells.CheckBoxCell;
        r8 = r22.getParentActivity();
        r6.<init>(r8, r12);
        r8 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r13);
        r6.setBackgroundDrawable(r8);
        r8 = "PinNotify";
        r9 = NUM; // 0x7f0c0519 float:1.8611839E38 double:1.053098043E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r9);
        r9 = "";
        r6.setText(r8, r9, r12, r13);
        r8 = org.telegram.messenger.LocaleController.isRTL;
        r9 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        if (r8 == 0) goto L_0x01cf;
    L_0x01ca:
        r8 = org.telegram.messenger.AndroidUtilities.dp(r9);
        goto L_0x01d0;
    L_0x01cf:
        r8 = r13;
    L_0x01d0:
        r12 = org.telegram.messenger.LocaleController.isRTL;
        if (r12 == 0) goto L_0x01d6;
    L_0x01d4:
        r9 = r13;
        goto L_0x01da;
    L_0x01d6:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
    L_0x01da:
        r6.setPadding(r8, r13, r9, r13);
        r15 = -1;
        r16 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r17 = 51;
        r18 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r19 = 0;
        r20 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r21 = 0;
        r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21);
        r5.addView(r6, r8);
        r8 = new org.telegram.ui.ChatActivity$105;
        r8.<init>(r4);
        r6.setOnClickListener(r8);
        r3.setView(r5);
        goto L_0x020d;
    L_0x01fd:
        r4 = "PinMessageAlertChannel";
        r5 = NUM; // 0x7f0c0518 float:1.8611837E38 double:1.0530980427E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r3.setMessage(r4);
        r4 = new boolean[r12];
        r4[r13] = r13;
    L_0x020d:
        r5 = "OK";
        r5 = org.telegram.messenger.LocaleController.getString(r5, r11);
        r6 = new org.telegram.ui.ChatActivity$106;
        r6.<init>(r1, r4);
        r3.setPositiveButton(r5, r6);
        r1 = "AppName";
        r1 = org.telegram.messenger.LocaleController.getString(r1, r10);
        r3.setTitle(r1);
        r1 = "Cancel";
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);
        r3.setNegativeButton(r1, r14);
        r1 = r3.create();
        r7.showDialog(r1);
        goto L_0x05e6;
    L_0x0236:
        r1 = r7.selectedObject;
        r7.startEditingMessageObject(r1);
        r7.selectedObject = r14;
        r7.selectedObjectGroup = r14;
        goto L_0x05e6;
    L_0x0241:
        r1 = r7.selectedObject;
        r1 = r1.getDocument();
        r2 = r7.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r2.saveGif(r1);
        r22.showGifHint();
        r2 = r7.chatActivityEnterView;
        r2.addRecentGif(r1);
        goto L_0x05e6;
    L_0x025a:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r8) goto L_0x027c;
    L_0x025e:
        r2 = r22.getParentActivity();
        r3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r2 = r2.checkSelfPermission(r3);
        if (r2 == 0) goto L_0x027c;
    L_0x026a:
        r1 = r22.getParentActivity();
        r2 = new java.lang.String[r12];
        r3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r2[r13] = r3;
        r1.requestPermissions(r2, r6);
        r7.selectedObject = r14;
        r7.selectedObjectGroup = r14;
        return;
    L_0x027c:
        r2 = r7.selectedObject;
        r2 = r2.getDocument();
        r2 = org.telegram.messenger.FileLoader.getDocumentFileName(r2);
        r3 = android.text.TextUtils.isEmpty(r2);
        if (r3 == 0) goto L_0x0292;
    L_0x028c:
        r2 = r7.selectedObject;
        r2 = r2.getFileName();
    L_0x0292:
        r3 = r7.selectedObject;
        r3 = r3.messageOwner;
        r3 = r3.attachPath;
        if (r3 == 0) goto L_0x02ac;
    L_0x029a:
        r4 = r3.length();
        if (r4 <= 0) goto L_0x02ac;
    L_0x02a0:
        r4 = new java.io.File;
        r4.<init>(r3);
        r4 = r4.exists();
        if (r4 != 0) goto L_0x02ac;
    L_0x02ab:
        r3 = r14;
    L_0x02ac:
        if (r3 == 0) goto L_0x02b4;
    L_0x02ae:
        r4 = r3.length();
        if (r4 != 0) goto L_0x02c0;
    L_0x02b4:
        r3 = r7.selectedObject;
        r3 = r3.messageOwner;
        r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3);
        r3 = r3.toString();
    L_0x02c0:
        r4 = r22.getParentActivity();
        r5 = r7.selectedObject;
        r5 = r5.isMusic();
        if (r5 == 0) goto L_0x02cd;
    L_0x02cc:
        goto L_0x02ce;
    L_0x02cd:
        r1 = r9;
    L_0x02ce:
        r5 = r7.selectedObject;
        r5 = r5.getDocument();
        if (r5 == 0) goto L_0x02df;
    L_0x02d6:
        r5 = r7.selectedObject;
        r5 = r5.getDocument();
        r5 = r5.mime_type;
        goto L_0x02e1;
    L_0x02df:
        r5 = "";
    L_0x02e1:
        org.telegram.messenger.MediaController.saveFile(r3, r4, r1, r2, r5);
        goto L_0x05e6;
    L_0x02e6:
        r8 = new org.telegram.ui.Components.StickersAlert;
        r2 = r22.getParentActivity();
        r1 = r7.selectedObject;
        r4 = r1.getInputStickerSet();
        r5 = 0;
        r1 = r7.bottomOverlayChat;
        r1 = r1.getVisibility();
        if (r1 == 0) goto L_0x0307;
    L_0x02fb:
        r1 = r7.currentChat;
        r1 = org.telegram.messenger.ChatObject.canSendStickers(r1);
        if (r1 == 0) goto L_0x0307;
    L_0x0303:
        r1 = r7.chatActivityEnterView;
        r6 = r1;
        goto L_0x0308;
    L_0x0307:
        r6 = r14;
    L_0x0308:
        r1 = r8;
        r3 = r7;
        r1.<init>(r2, r3, r4, r5, r6);
        r7.showDialog(r8);
        goto L_0x05e6;
    L_0x0312:
        r2 = 1;
        r3 = r7.selectedObject;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r1 = r7;
        r1.showReplyPanel(r2, r3, r4, r5, r6);
        goto L_0x05e6;
    L_0x031e:
        r1 = r7.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        if (r1 == 0) goto L_0x0338;
    L_0x0326:
        r2 = r1.length();
        if (r2 <= 0) goto L_0x0338;
    L_0x032c:
        r2 = new java.io.File;
        r2.<init>(r1);
        r2 = r2.exists();
        if (r2 != 0) goto L_0x0338;
    L_0x0337:
        r1 = r14;
    L_0x0338:
        if (r1 == 0) goto L_0x0340;
    L_0x033a:
        r2 = r1.length();
        if (r2 != 0) goto L_0x034c;
    L_0x0340:
        r1 = r7.selectedObject;
        r1 = r1.messageOwner;
        r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1);
        r1 = r1.toString();
    L_0x034c:
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 < r8) goto L_0x036e;
    L_0x0350:
        r2 = r22.getParentActivity();
        r3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r2 = r2.checkSelfPermission(r3);
        if (r2 == 0) goto L_0x036e;
    L_0x035c:
        r1 = r22.getParentActivity();
        r2 = new java.lang.String[r12];
        r3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r2[r13] = r3;
        r1.requestPermissions(r2, r6);
        r7.selectedObject = r14;
        r7.selectedObjectGroup = r14;
        return;
    L_0x036e:
        r2 = r22.getParentActivity();
        org.telegram.messenger.MediaController.saveFile(r1, r2, r13, r14, r14);
        goto L_0x05e6;
    L_0x0377:
        r1 = r7.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        if (r1 == 0) goto L_0x0391;
    L_0x037f:
        r2 = r1.length();
        if (r2 <= 0) goto L_0x0391;
    L_0x0385:
        r2 = new java.io.File;
        r2.<init>(r1);
        r2 = r2.exists();
        if (r2 != 0) goto L_0x0391;
    L_0x0390:
        r1 = r14;
    L_0x0391:
        if (r1 == 0) goto L_0x0399;
    L_0x0393:
        r2 = r1.length();
        if (r2 != 0) goto L_0x03a5;
    L_0x0399:
        r1 = r7.selectedObject;
        r1 = r1.messageOwner;
        r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1);
        r1 = r1.toString();
    L_0x03a5:
        r2 = new android.content.Intent;
        r4 = "android.intent.action.SEND";
        r2.<init>(r4);
        r4 = r7.selectedObject;
        r4 = r4.getDocument();
        r4 = r4.mime_type;
        r2.setType(r4);
        r4 = new java.io.File;
        r4.<init>(r1);
        r1 = android.os.Build.VERSION.SDK_INT;
        r5 = 24;
        if (r1 < r5) goto L_0x03df;
    L_0x03c2:
        r1 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x03d5 }
        r5 = r22.getParentActivity();	 Catch:{ Exception -> 0x03d5 }
        r6 = "org.telegram.messenger.provider";	 Catch:{ Exception -> 0x03d5 }
        r5 = android.support.v4.content.FileProvider.getUriForFile(r5, r6, r4);	 Catch:{ Exception -> 0x03d5 }
        r2.putExtra(r1, r5);	 Catch:{ Exception -> 0x03d5 }
        r2.setFlags(r12);	 Catch:{ Exception -> 0x03d5 }
        goto L_0x03e8;
    L_0x03d5:
        r1 = "android.intent.extra.STREAM";
        r4 = android.net.Uri.fromFile(r4);
        r2.putExtra(r1, r4);
        goto L_0x03e8;
    L_0x03df:
        r1 = "android.intent.extra.STREAM";
        r4 = android.net.Uri.fromFile(r4);
        r2.putExtra(r1, r4);
    L_0x03e8:
        r1 = r22.getParentActivity();
        r4 = "ShareFile";
        r5 = NUM; // 0x7f0c05ef float:1.8612273E38 double:1.053098149E-314;
        r4 = org.telegram.messenger.LocaleController.getString(r4, r5);
        r2 = android.content.Intent.createChooser(r2, r4);
        r1.startActivityForResult(r2, r3);
        goto L_0x05e6;
    L_0x03fe:
        r1 = r7.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x041c;
    L_0x040a:
        r1 = new java.io.File;
        r2 = r7.selectedObject;
        r2 = r2.messageOwner;
        r2 = r2.attachPath;
        r1.<init>(r2);
        r2 = r1.exists();
        if (r2 == 0) goto L_0x041c;
    L_0x041b:
        goto L_0x041d;
    L_0x041c:
        r1 = r14;
    L_0x041d:
        if (r1 != 0) goto L_0x042e;
    L_0x041f:
        r2 = r7.selectedObject;
        r2 = r2.messageOwner;
        r2 = org.telegram.messenger.FileLoader.getPathToMessage(r2);
        r3 = r2.exists();
        if (r3 == 0) goto L_0x042e;
    L_0x042d:
        r1 = r2;
    L_0x042e:
        if (r1 == 0) goto L_0x05e6;
    L_0x0430:
        r2 = r1.getName();
        r2 = r2.toLowerCase();
        r3 = "attheme";
        r2 = r2.endsWith(r3);
        if (r2 == 0) goto L_0x04ca;
    L_0x0440:
        r2 = r7.chatLayoutManager;
        r3 = -1;
        if (r2 == 0) goto L_0x0477;
    L_0x0445:
        r2 = r7.chatLayoutManager;
        r2 = r2.findFirstVisibleItemPosition();
        if (r2 == 0) goto L_0x0475;
    L_0x044d:
        r7.scrollToPositionOnRecreate = r2;
        r2 = r7.chatListView;
        r4 = r7.scrollToPositionOnRecreate;
        r2 = r2.findViewHolderForAdapterPosition(r4);
        r2 = (org.telegram.ui.Components.RecyclerListView.Holder) r2;
        if (r2 == 0) goto L_0x0472;
    L_0x045b:
        r4 = r7.chatListView;
        r4 = r4.getMeasuredHeight();
        r2 = r2.itemView;
        r2 = r2.getBottom();
        r4 = r4 - r2;
        r2 = r7.chatListView;
        r2 = r2.getPaddingBottom();
        r4 = r4 - r2;
        r7.scrollToOffsetOnRecreate = r4;
        goto L_0x0477;
    L_0x0472:
        r7.scrollToPositionOnRecreate = r3;
        goto L_0x0477;
    L_0x0475:
        r7.scrollToPositionOnRecreate = r3;
    L_0x0477:
        r2 = r7.selectedObject;
        r2 = r2.getDocumentName();
        r2 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r1, r2, r12);
        if (r2 == 0) goto L_0x048d;
    L_0x0483:
        r3 = new org.telegram.ui.ThemePreviewActivity;
        r3.<init>(r1, r2);
        r7.presentFragment(r3);
        goto L_0x05e6;
    L_0x048d:
        r7.scrollToPositionOnRecreate = r3;
        r1 = r22.getParentActivity();
        if (r1 != 0) goto L_0x049a;
    L_0x0495:
        r7.selectedObject = r14;
        r7.selectedObjectGroup = r14;
        return;
    L_0x049a:
        r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r2 = r22.getParentActivity();
        r1.<init>(r2);
        r2 = "AppName";
        r2 = org.telegram.messenger.LocaleController.getString(r2, r10);
        r1.setTitle(r2);
        r2 = "IncorrectTheme";
        r3 = NUM; // 0x7f0c032a float:1.8610835E38 double:1.0530977987E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r1.setMessage(r2);
        r2 = "OK";
        r2 = org.telegram.messenger.LocaleController.getString(r2, r11);
        r1.setPositiveButton(r2, r14);
        r1 = r1.create();
        r7.showDialog(r1);
        goto L_0x05e6;
    L_0x04ca:
        r2 = org.telegram.messenger.LocaleController.getInstance();
        r3 = r7.currentAccount;
        r1 = r2.applyLanguageFile(r1, r3);
        if (r1 == 0) goto L_0x04e0;
    L_0x04d6:
        r1 = new org.telegram.ui.LanguageSelectActivity;
        r1.<init>();
        r7.presentFragment(r1);
        goto L_0x05e6;
    L_0x04e0:
        r1 = r22.getParentActivity();
        if (r1 != 0) goto L_0x04eb;
    L_0x04e6:
        r7.selectedObject = r14;
        r7.selectedObjectGroup = r14;
        return;
    L_0x04eb:
        r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r2 = r22.getParentActivity();
        r1.<init>(r2);
        r2 = "AppName";
        r2 = org.telegram.messenger.LocaleController.getString(r2, r10);
        r1.setTitle(r2);
        r2 = "IncorrectLocalization";
        r3 = NUM; // 0x7f0c0329 float:1.8610833E38 double:1.053097798E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r1.setMessage(r2);
        r2 = "OK";
        r2 = org.telegram.messenger.LocaleController.getString(r2, r11);
        r1.setPositiveButton(r2, r14);
        r1 = r1.create();
        r7.showDialog(r1);
        goto L_0x05e6;
    L_0x051b:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r8) goto L_0x053d;
    L_0x051f:
        r1 = r22.getParentActivity();
        r2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1 = r1.checkSelfPermission(r2);
        if (r1 == 0) goto L_0x053d;
    L_0x052b:
        r1 = r22.getParentActivity();
        r2 = new java.lang.String[r12];
        r3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r2[r13] = r3;
        r1.requestPermissions(r2, r6);
        r7.selectedObject = r14;
        r7.selectedObjectGroup = r14;
        return;
    L_0x053d:
        r1 = r7.selectedObjectGroup;
        if (r1 == 0) goto L_0x055b;
    L_0x0541:
        r1 = r7.selectedObjectGroup;
        r1 = r1.messages;
        r1 = r1.size();
        if (r13 >= r1) goto L_0x05e6;
    L_0x054b:
        r1 = r7.selectedObjectGroup;
        r1 = r1.messages;
        r1 = r1.get(r13);
        r1 = (org.telegram.messenger.MessageObject) r1;
        r7.saveMessageToGallery(r1);
        r13 = r13 + 1;
        goto L_0x0541;
    L_0x055b:
        r1 = r7.selectedObject;
        r7.saveMessageToGallery(r1);
        goto L_0x05e6;
    L_0x0562:
        r1 = r7.selectedObject;
        r1 = r7.getMessageContent(r1, r13, r13);
        org.telegram.messenger.AndroidUtilities.addToClipboard(r1);
        goto L_0x05e6;
    L_0x056d:
        r2 = r7.selectedObject;
        r7.forwardingMessage = r2;
        r2 = r7.selectedObjectGroup;
        r7.forwardingMessageGroup = r2;
        r2 = new android.os.Bundle;
        r2.<init>();
        r3 = "onlySelect";
        r2.putBoolean(r3, r12);
        r3 = "dialogsType";
        r2.putInt(r3, r1);
        r1 = new org.telegram.ui.DialogsActivity;
        r1.<init>(r2);
        r1.setDelegate(r7);
        r7.presentFragment(r1);
        goto L_0x05e6;
    L_0x0590:
        r1 = r22.getParentActivity();
        if (r1 != 0) goto L_0x059b;
    L_0x0596:
        r7.selectedObject = r14;
        r7.selectedObjectGroup = r14;
        return;
    L_0x059b:
        r1 = r7.selectedObject;
        r2 = r7.selectedObjectGroup;
        r7.createDeleteMessagesAlert(r1, r2);
        goto L_0x05e6;
    L_0x05a3:
        r1 = r7.selectedObjectGroup;
        if (r1 == 0) goto L_0x05d2;
    L_0x05a7:
        r1 = r13;
    L_0x05a8:
        r2 = r7.selectedObjectGroup;
        r2 = r2.messages;
        r2 = r2.size();
        if (r1 >= r2) goto L_0x05cc;
    L_0x05b2:
        r2 = r7.currentAccount;
        r2 = org.telegram.messenger.SendMessagesHelper.getInstance(r2);
        r3 = r7.selectedObjectGroup;
        r3 = r3.messages;
        r3 = r3.get(r1);
        r3 = (org.telegram.messenger.MessageObject) r3;
        r2 = r2.retrySendMessage(r3, r13);
        if (r2 != 0) goto L_0x05c9;
    L_0x05c8:
        r12 = r13;
    L_0x05c9:
        r1 = r1 + 1;
        goto L_0x05a8;
    L_0x05cc:
        if (r12 == 0) goto L_0x05e6;
    L_0x05ce:
        r22.moveScrollToLastMessage();
        goto L_0x05e6;
    L_0x05d2:
        r1 = r7.currentAccount;
        r1 = org.telegram.messenger.SendMessagesHelper.getInstance(r1);
        r2 = r7.selectedObject;
        r1 = r1.retrySendMessage(r2, r13);
        if (r1 == 0) goto L_0x05e6;
    L_0x05e0:
        r22.updateVisibleRows();
        r22.moveScrollToLastMessage();
    L_0x05e6:
        r7.selectedObject = r14;
        r7.selectedObjectGroup = r14;
        return;
    L_0x05eb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatActivity.processSelectedOption(int):void");
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        ArrayList<Long> arrayList2 = arrayList;
        int i = 0;
        if (this.forwardingMessage != null || r6.selectedMessagesIds[0].size() != 0 || r6.selectedMessagesIds[1].size() != 0) {
            int i2;
            DialogsActivity dialogsActivity2;
            ArrayList arrayList3 = new ArrayList();
            if (r6.forwardingMessage != null) {
                if (r6.forwardingMessageGroup != null) {
                    arrayList3.addAll(r6.forwardingMessageGroup.messages);
                } else {
                    arrayList3.add(r6.forwardingMessage);
                }
                r6.forwardingMessage = null;
                r6.forwardingMessageGroup = null;
            } else {
                for (i2 = 1; i2 >= 0; i2--) {
                    int i3;
                    ArrayList arrayList4 = new ArrayList();
                    for (i3 = 0; i3 < r6.selectedMessagesIds[i2].size(); i3++) {
                        arrayList4.add(Integer.valueOf(r6.selectedMessagesIds[i2].keyAt(i3)));
                    }
                    Collections.sort(arrayList4);
                    for (i3 = 0; i3 < arrayList4.size(); i3++) {
                        Integer num = (Integer) arrayList4.get(i3);
                        MessageObject messageObject = (MessageObject) r6.selectedMessagesIds[i2].get(num.intValue());
                        if (messageObject != null && num.intValue() > 0) {
                            arrayList3.add(messageObject);
                        }
                    }
                    r6.selectedMessagesCanCopyIds[i2].clear();
                    r6.selectedMessagesCanStarIds[i2].clear();
                    r6.selectedMessagesIds[i2].clear();
                }
                r6.cantDeleteMessagesCount = 0;
                r6.canEditMessagesCount = 0;
                r6.actionBar.hideActionMode();
                updatePinnedMessageView(true);
            }
            if (arrayList.size() <= 1 && ((Long) arrayList2.get(0)).longValue() != ((long) UserConfig.getInstance(r6.currentAccount).getClientUserId())) {
                if (charSequence == null) {
                    long longValue = ((Long) arrayList2.get(0)).longValue();
                    if (longValue != r6.dialog_id) {
                        i2 = (int) longValue;
                        int i4 = (int) (longValue >> 32);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("scrollToTopOnResume", r6.scrollToTopOnResume);
                        if (i2 == 0) {
                            bundle.putInt("enc_id", i4);
                        } else if (i2 > 0) {
                            bundle.putInt("user_id", i2);
                        } else if (i2 < 0) {
                            bundle.putInt("chat_id", -i2);
                        }
                        if (i2 == 0) {
                            dialogsActivity2 = dialogsActivity;
                        } else if (!MessagesController.getInstance(r6.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
                            return;
                        }
                        BaseFragment chatActivity = new ChatActivity(bundle);
                        if (presentFragment(chatActivity, true)) {
                            chatActivity.showReplyPanel(true, null, arrayList3, null, false);
                            if (!AndroidUtilities.isTablet()) {
                                removeSelfFromStack();
                            }
                        } else {
                            dialogsActivity.finishFragment();
                        }
                    } else {
                        dialogsActivity2 = dialogsActivity;
                        dialogsActivity.finishFragment();
                        moveScrollToLastMessage();
                        showReplyPanel(true, null, arrayList3, null, false);
                        if (AndroidUtilities.isTablet()) {
                            r6.actionBar.hideActionMode();
                            updatePinnedMessageView(true);
                        }
                        updateVisibleRows();
                    }
                }
            }
            dialogsActivity2 = dialogsActivity;
            while (i < arrayList.size()) {
                long longValue2 = ((Long) arrayList2.get(i)).longValue();
                if (charSequence != null) {
                    SendMessagesHelper.getInstance(r6.currentAccount).sendMessage(charSequence.toString(), longValue2, null, null, true, null, null, null);
                }
                SendMessagesHelper.getInstance(r6.currentAccount).sendMessage(arrayList3, longValue2);
                i++;
            }
            dialogsActivity.finishFragment();
        }
    }

    public boolean checkRecordLocked() {
        if (this.chatActivityEnterView == null || !this.chatActivityEnterView.isRecordLocked()) {
            return false;
        }
        Builder builder = new Builder(getParentActivity());
        if (this.chatActivityEnterView.isInVideoMode()) {
            builder.setTitle(LocaleController.getString("DiscardVideoMessageTitle", C0446R.string.DiscardVideoMessageTitle));
            builder.setMessage(LocaleController.getString("DiscardVideoMessageDescription", C0446R.string.DiscardVideoMessageDescription));
        } else {
            builder.setTitle(LocaleController.getString("DiscardVoiceMessageTitle", C0446R.string.DiscardVoiceMessageTitle));
            builder.setMessage(LocaleController.getString("DiscardVoiceMessageDescription", C0446R.string.DiscardVoiceMessageDescription));
        }
        builder.setPositiveButton(LocaleController.getString("DiscardVoiceMessageAction", C0446R.string.DiscardVoiceMessageAction), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (ChatActivity.this.chatActivityEnterView != null) {
                    ChatActivity.this.chatActivityEnterView.cancelRecordingAudioVideo();
                }
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
        showDialog(builder.create());
        return true;
    }

    public boolean onBackPressed() {
        if (checkRecordLocked()) {
            return false;
        }
        if (this.actionBar != null && this.actionBar.isActionModeShowed()) {
            for (int i = 1; i >= 0; i--) {
                this.selectedMessagesIds[i].clear();
                this.selectedMessagesCanCopyIds[i].clear();
                this.selectedMessagesCanStarIds[i].clear();
            }
            this.chatActivityEnterView.setEditingMessageObject(null, false);
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
            int indexOf;
            int childCount;
            MessageObject editingMessageObject;
            int i;
            View childAt;
            int i2;
            ChatActionCell chatActionCell;
            ChatMessageCell chatMessageCell;
            MessageObject messageObject;
            boolean z;
            int i3;
            boolean z2;
            boolean z3;
            boolean z4;
            DataQuery instance;
            if (!(r0.wasManualScroll || r0.unreadMessageObject == null || r0.chatListView.getMeasuredHeight() == 0)) {
                indexOf = r0.messages.indexOf(r0.unreadMessageObject);
                if (indexOf >= 0) {
                    indexOf += r0.chatAdapter.messagesStartRow;
                    childCount = r0.chatListView.getChildCount();
                    editingMessageObject = r0.chatActivityEnterView == null ? r0.chatActivityEnterView.getEditingMessageObject() : null;
                    i = 0;
                    while (i < childCount) {
                        childAt = r0.chatListView.getChildAt(i);
                        if (childAt instanceof ChatMessageCell) {
                            i2 = childCount;
                            if (childAt instanceof ChatActionCell) {
                                chatActionCell = (ChatActionCell) childAt;
                                chatActionCell.setMessageObject(chatActionCell.getMessageObject());
                            }
                        } else {
                            chatMessageCell = (ChatMessageCell) childAt;
                            messageObject = chatMessageCell.getMessageObject();
                            z = true;
                            if (r0.actionBar.isActionModeShowed()) {
                                i2 = childCount;
                                childAt.setBackgroundDrawable(null);
                                i3 = 0;
                                z2 = i3;
                            } else {
                                i2 = childCount;
                                i3 = messageObject.getDialogId() != r0.dialog_id ? 0 : 1;
                                if (messageObject != editingMessageObject) {
                                    if (r0.selectedMessagesIds[i3].indexOfKey(messageObject.getId()) >= 0) {
                                        childAt.setBackgroundDrawable(null);
                                        z3 = false;
                                        z2 = z3;
                                        i3 = 1;
                                    }
                                }
                                setCellSelectionBackground(messageObject, chatMessageCell, i3);
                                z3 = true;
                                z2 = z3;
                                i3 = 1;
                            }
                            chatMessageCell.setMessageObject(chatMessageCell.getMessageObject(), chatMessageCell.getCurrentMessagesGroup(), chatMessageCell.isPinnedBottom(), chatMessageCell.isPinnedTop());
                            z4 = i3 ^ 1;
                            z3 = i3 == 0 && z2;
                            chatMessageCell.setCheckPressed(z4, z3);
                            z3 = r0.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID && messageObject != null && messageObject.getId() == r0.highlightMessageId;
                            chatMessageCell.setHighlighted(z3);
                            if (r0.searchContainer != null && r0.searchContainer.getVisibility() == 0) {
                                instance = DataQuery.getInstance(r0.currentAccount);
                                childCount = messageObject.getId();
                                if (messageObject.getDialogId() == r0.mergeDialogId) {
                                    z = false;
                                }
                                if (instance.isMessageFound(childCount, z) && DataQuery.getInstance(r0.currentAccount).getLastSearchQuery() != null) {
                                    chatMessageCell.setHighlightedText(DataQuery.getInstance(r0.currentAccount).getLastSearchQuery());
                                }
                            }
                            chatMessageCell.setHighlightedText(null);
                        }
                        i++;
                        childCount = i2;
                    }
                    r0.chatListView.invalidate();
                    if (indexOf != -1) {
                        r0.chatLayoutManager.scrollToPositionWithOffset(indexOf, ((r0.chatListView.getMeasuredHeight() - r0.chatListView.getPaddingBottom()) - r0.chatListView.getPaddingTop()) - AndroidUtilities.dp(29.0f));
                    }
                }
            }
            indexOf = -1;
            childCount = r0.chatListView.getChildCount();
            if (r0.chatActivityEnterView == null) {
            }
            i = 0;
            while (i < childCount) {
                childAt = r0.chatListView.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    i2 = childCount;
                    if (childAt instanceof ChatActionCell) {
                        chatActionCell = (ChatActionCell) childAt;
                        chatActionCell.setMessageObject(chatActionCell.getMessageObject());
                    }
                } else {
                    chatMessageCell = (ChatMessageCell) childAt;
                    messageObject = chatMessageCell.getMessageObject();
                    z = true;
                    if (r0.actionBar.isActionModeShowed()) {
                        i2 = childCount;
                        childAt.setBackgroundDrawable(null);
                        i3 = 0;
                        z2 = i3;
                    } else {
                        i2 = childCount;
                        if (messageObject.getDialogId() != r0.dialog_id) {
                        }
                        if (messageObject != editingMessageObject) {
                            if (r0.selectedMessagesIds[i3].indexOfKey(messageObject.getId()) >= 0) {
                                childAt.setBackgroundDrawable(null);
                                z3 = false;
                                z2 = z3;
                                i3 = 1;
                            }
                        }
                        setCellSelectionBackground(messageObject, chatMessageCell, i3);
                        z3 = true;
                        z2 = z3;
                        i3 = 1;
                    }
                    chatMessageCell.setMessageObject(chatMessageCell.getMessageObject(), chatMessageCell.getCurrentMessagesGroup(), chatMessageCell.isPinnedBottom(), chatMessageCell.isPinnedTop());
                    z4 = i3 ^ 1;
                    if (i3 == 0) {
                    }
                    chatMessageCell.setCheckPressed(z4, z3);
                    if (r0.highlightMessageId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                    }
                    chatMessageCell.setHighlighted(z3);
                    instance = DataQuery.getInstance(r0.currentAccount);
                    childCount = messageObject.getId();
                    if (messageObject.getDialogId() == r0.mergeDialogId) {
                        z = false;
                    }
                    chatMessageCell.setHighlightedText(DataQuery.getInstance(r0.currentAccount).getLastSearchQuery());
                }
                i++;
                childCount = i2;
            }
            r0.chatListView.invalidate();
            if (indexOf != -1) {
                r0.chatLayoutManager.scrollToPositionWithOffset(indexOf, ((r0.chatListView.getMeasuredHeight() - r0.chatListView.getPaddingBottom()) - r0.chatListView.getPaddingTop()) - AndroidUtilities.dp(29.0f));
            }
        }
    }

    private void checkEditTimer() {
        if (this.chatActivityEnterView != null) {
            MessageObject editingMessageObject = this.chatActivityEnterView.getEditingMessageObject();
            if (editingMessageObject != null) {
                if (this.currentUser == null || !this.currentUser.self) {
                    int abs = editingMessageObject.canEditMessageAnytime(this.currentChat) ? 360 : (MessagesController.getInstance(this.currentAccount).maxEditTime + 300) - Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - editingMessageObject.messageOwner.date);
                    if (abs > 0) {
                        if (abs <= 300) {
                            if (this.actionModeSubTextView.getVisibility() != 0) {
                                this.actionModeSubTextView.setVisibility(0);
                            }
                            SimpleTextView simpleTextView = this.actionModeSubTextView;
                            Object[] objArr = new Object[1];
                            objArr[0] = String.format("%d:%02d", new Object[]{Integer.valueOf(abs / 60), Integer.valueOf(abs % 60)});
                            simpleTextView.setText(LocaleController.formatString("TimeToEdit", C0446R.string.TimeToEdit, objArr));
                        } else if (this.actionModeSubTextView.getVisibility() != 8) {
                            this.actionModeSubTextView.setVisibility(8);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                ChatActivity.this.checkEditTimer();
                            }
                        }, 1000);
                    } else {
                        this.chatActivityEnterView.onEditTimeExpired();
                        this.actionModeSubTextView.setText(LocaleController.formatString("TimeToEditExpired", C0446R.string.TimeToEditExpired, new Object[0]));
                    }
                    return;
                }
                if (this.actionModeSubTextView.getVisibility() != 8) {
                    this.actionModeSubTextView.setVisibility(8);
                }
            }
        }
    }

    private ArrayList<MessageObject> createVoiceMessagesPlaylist(MessageObject messageObject, boolean z) {
        ArrayList<MessageObject> arrayList = new ArrayList();
        arrayList.add(messageObject);
        int id = messageObject.getId();
        messageObject.getDialogId();
        if (id != 0) {
            for (int size = this.messages.size() - 1; size >= 0; size--) {
                MessageObject messageObject2 = (MessageObject) this.messages.get(size);
                if (messageObject2.getDialogId() != this.mergeDialogId || messageObject.getDialogId() == this.mergeDialogId) {
                    if (((this.currentEncryptedChat == null && messageObject2.getId() > id) || (this.currentEncryptedChat != null && messageObject2.getId() < id)) && ((messageObject2.isVoice() || messageObject2.isRoundVideo()) && (!z || (messageObject2.isContentUnread() && !messageObject2.isOut())))) {
                        arrayList.add(messageObject2);
                    }
                }
            }
        }
        return arrayList;
    }

    private void alertUserOpenError(MessageObject messageObject) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
            if (messageObject.type == 3) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", C0446R.string.NoPlayerInstalled));
            } else {
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", C0446R.string.NoHandleAppInstalled, messageObject.getDocument().mime_type));
            }
            showDialog(builder.create());
        }
    }

    private void openSearchWithText(String str) {
        boolean z = false;
        if (!this.actionBar.isSearchFieldVisible()) {
            this.avatarContainer.setVisibility(8);
            this.headerItem.setVisibility(8);
            this.attachItem.setVisibility(8);
            this.searchItem.setVisibility(0);
            updateSearchButtons(0, 0, -1);
            updateBottomOverlay();
        }
        if (str == null) {
            z = true;
        }
        this.openSearchKeyboard = z;
        this.searchItem.openSearch(this.openSearchKeyboard);
        if (str != null) {
            this.searchItem.getSearchField().setText(str);
            this.searchItem.getSearchField().setSelection(this.searchItem.getSearchField().length());
            DataQuery.getInstance(this.currentAccount).searchMessagesInChat(str, this.dialog_id, this.mergeDialogId, this.classGuid, 0, this.searchingUserMessages);
        }
        updatePinnedMessageView(true);
    }

    public void didSelectLocation(MessageMedia messageMedia, int i) {
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(messageMedia, this.dialog_id, this.replyingMessageObject, null, null);
        moveScrollToLastMessage();
        if (i == 1) {
            showReplyPanel(false, null, null, null, false);
            DataQuery.getInstance(this.currentAccount).cleanDraft(this.dialog_id, true);
        }
        if (this.paused != 0) {
            this.scrollToTopOnResume = true;
        }
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
        if (this.currentEncryptedChat != null) {
            if (AndroidUtilities.getPeerLayerVersion(this.currentEncryptedChat.layer) < 73) {
                return false;
            }
        }
        return true;
    }

    public EncryptedChat getCurrentEncryptedChat() {
        return this.currentEncryptedChat;
    }

    public ChatFull getCurrentChatInfo() {
        return this.info;
    }

    public void sendMedia(PhotoEntry photoEntry, VideoEditedInfo videoEditedInfo) {
        ChatActivity chatActivity = this;
        PhotoEntry photoEntry2 = photoEntry;
        VideoEditedInfo videoEditedInfo2 = videoEditedInfo;
        if (photoEntry2.isVideo) {
            if (videoEditedInfo2 != null) {
                String str = photoEntry2.path;
                long j = videoEditedInfo2.estimatedSize;
                long j2 = videoEditedInfo2.estimatedDuration;
                int i = videoEditedInfo2.resultWidth;
                int i2 = videoEditedInfo2.resultHeight;
                long j3 = chatActivity.dialog_id;
                MessageObject messageObject = chatActivity.replyingMessageObject;
                CharSequence charSequence = photoEntry2.caption;
                ArrayList arrayList = photoEntry2.entities;
                SendMessagesHelper.prepareSendingVideo(str, j, j2, i, i2, videoEditedInfo2, j3, messageObject, charSequence, arrayList, photoEntry2.ttl);
            } else {
                SendMessagesHelper.prepareSendingVideo(photoEntry2.path, 0, 0, 0, 0, null, chatActivity.dialog_id, chatActivity.replyingMessageObject, photoEntry2.caption, photoEntry2.entities, photoEntry2.ttl);
            }
            boolean z = true;
            showReplyPanel(false, null, null, null, false);
            DataQuery.getInstance(chatActivity.currentAccount).cleanDraft(chatActivity.dialog_id, z);
            return;
        }
        z = true;
        if (photoEntry2.imagePath != null) {
            SendMessagesHelper.prepareSendingPhoto(photoEntry2.imagePath, null, chatActivity.dialog_id, chatActivity.replyingMessageObject, photoEntry2.caption, photoEntry2.entities, photoEntry2.stickers, null, photoEntry2.ttl);
            showReplyPanel(false, null, null, null, false);
            DataQuery.getInstance(chatActivity.currentAccount).cleanDraft(chatActivity.dialog_id, z);
        } else if (photoEntry2.path != null) {
            SendMessagesHelper.prepareSendingPhoto(photoEntry2.path, null, chatActivity.dialog_id, chatActivity.replyingMessageObject, photoEntry2.caption, photoEntry2.entities, photoEntry2.stickers, null, photoEntry2.ttl);
            showReplyPanel(false, null, null, null, false);
            DataQuery.getInstance(chatActivity.currentAccount).cleanDraft(chatActivity.dialog_id, z);
        }
    }

    public void showOpenGameAlert(TL_game tL_game, MessageObject messageObject, String str, boolean z, int i) {
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
        if (z) {
            z = new Builder(getParentActivity());
            z.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            String formatName = user != null ? ContactsController.formatName(user.first_name, user.last_name) : TtmlNode.ANONYMOUS_REGION_ID;
            z.setMessage(LocaleController.formatString("BotPermissionGameAlert", C0446R.string.BotPermissionGameAlert, formatName));
            final TL_game tL_game2 = tL_game;
            final MessageObject messageObject2 = messageObject;
            final String str2 = str;
            final int i2 = i;
            z.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    ChatActivity.this.showOpenGameAlert(tL_game2, messageObject2, str2, false, i2);
                    dialogInterface = MessagesController.getNotificationsSettings(ChatActivity.this.currentAccount).edit();
                    i = new StringBuilder();
                    i.append("askgame_");
                    i.append(i2);
                    dialogInterface.putBoolean(i.toString(), false).commit();
                }
            });
            z.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
            showDialog(z.create());
        } else if (VERSION.SDK_INT < true || AndroidUtilities.isTablet() || !WebviewActivity.supportWebview()) {
            z = getParentActivity();
            tL_game = tL_game.short_name;
            i = (user == null || user.username == 0) ? TtmlNode.ANONYMOUS_REGION_ID : user.username;
            WebviewActivity.openGameInBrowser(str, messageObject, z, tL_game, i);
        } else if (this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this) {
            i = (user == null || TextUtils.isEmpty(user.username) != 0) ? TtmlNode.ANONYMOUS_REGION_ID : user.username;
            presentFragment(new WebviewActivity(str, i, tL_game.title, tL_game.short_name, messageObject));
        }
    }

    public void showOpenUrlAlert(final String str, boolean z) {
        boolean z2 = false;
        if (!Browser.isInternalUrl(str, null)) {
            if (z) {
                z = new Builder(getParentActivity());
                z.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                z.setMessage(LocaleController.formatString("OpenUrlAlert", C0446R.string.OpenUrlAlert, str));
                z.setPositiveButton(LocaleController.getString("Open", C0446R.string.Open), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Browser.openUrl(ChatActivity.this.getParentActivity(), str, ChatActivity.this.inlineReturn == 0);
                    }
                });
                z.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                showDialog(z.create());
                return;
            }
        }
        Context parentActivity = getParentActivity();
        if (this.inlineReturn == 0) {
            z2 = true;
        }
        Browser.openUrl(parentActivity, str, z2);
    }

    private void removeMessageObject(MessageObject messageObject) {
        messageObject = this.messages.indexOf(messageObject);
        if (messageObject != -1) {
            this.messages.remove(messageObject);
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyItemRemoved(this.chatAdapter.messagesStartRow + messageObject);
            }
        }
    }

    private void setCellSelectionBackground(MessageObject messageObject, ChatMessageCell chatMessageCell, int i) {
        messageObject = getValidGroupedMessage(messageObject);
        if (messageObject != null) {
            Object obj = null;
            for (int i2 = 0; i2 < messageObject.messages.size(); i2++) {
                if (this.selectedMessagesIds[i].indexOfKey(((MessageObject) messageObject.messages.get(i2)).getId()) < 0) {
                    obj = 1;
                    break;
                }
            }
            if (obj == null) {
                messageObject = null;
            }
        }
        if (messageObject == null) {
            chatMessageCell.setBackgroundColor(Theme.getColor(Theme.key_chat_selectedBackground));
        } else {
            chatMessageCell.setBackground(null);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        View subtitleTextView;
        View emojiView;
        AnonymousClass113 anonymousClass113 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                ChatActivity.this.updateVisibleRows();
                if (ChatActivity.this.chatActivityEnterView != null && ChatActivity.this.chatActivityEnterView.getEmojiView() != null) {
                    ChatActivity.this.chatActivityEnterView.getEmojiView().updateUIColors();
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[340];
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
        View view = null;
        themeDescriptionArr[10] = new ThemeDescription(this.avatarContainer != null ? r0.avatarContainer.getTitleTextView() : null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        if (r0.avatarContainer != null) {
            subtitleTextView = r0.avatarContainer.getSubtitleTextView();
        } else {
            subtitleTextView = null;
        }
        themeDescriptionArr[11] = new ThemeDescription(subtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, null, null, Theme.key_actionBarDefaultSubtitle, null);
        themeDescriptionArr[12] = new ThemeDescription(r0.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[13] = new ThemeDescription(r0.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        themeDescriptionArr[14] = new ThemeDescription(r0.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        themeDescriptionArr[15] = new ThemeDescription(r0.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[16] = new ThemeDescription(r0.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefault);
        themeDescriptionArr[17] = new ThemeDescription(r0.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefaultTop);
        themeDescriptionArr[18] = new ThemeDescription(r0.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector);
        themeDescriptionArr[19] = new ThemeDescription(r0.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[20] = new ThemeDescription(r0.actionModeTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[21] = new ThemeDescription(r0.actionModeSubTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[22] = new ThemeDescription(r0.avatarContainer != null ? r0.avatarContainer.getTitleTextView() : null, 0, null, null, new Drawable[]{Theme.chat_muteIconDrawable}, null, Theme.key_chat_muteIcon);
        if (r0.avatarContainer != null) {
            subtitleTextView = r0.avatarContainer.getTitleTextView();
        } else {
            subtitleTextView = null;
        }
        themeDescriptionArr[edit] = new ThemeDescription(subtitleTextView, 0, null, null, new Drawable[]{Theme.chat_lockIconDrawable}, null, Theme.key_chat_lockIcon);
        themeDescriptionArr[24] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[25] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[26] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[27] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[28] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[29] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[bot_help] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[bot_settings] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[32] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageRed);
        themeDescriptionArr[33] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageOrange);
        themeDescriptionArr[34] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageViolet);
        themeDescriptionArr[35] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageGreen);
        themeDescriptionArr[36] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageCyan);
        themeDescriptionArr[37] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageBlue);
        themeDescriptionArr[38] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessagePink);
        themeDescriptionArr[39] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble);
        themeDescriptionArr[search] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected);
        themeDescriptionArr[41] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, Theme.key_chat_inBubbleShadow);
        themeDescriptionArr[42] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble);
        themeDescriptionArr[43] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected);
        themeDescriptionArr[44] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, Theme.key_chat_outBubbleShadow);
        themeDescriptionArr[45] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[46] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, Theme.key_chat_serviceLink);
        themeDescriptionArr[47] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_shareIconDrawable, Theme.chat_replyIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawalbe, Theme.chat_goIconDrawable}, null, Theme.key_chat_serviceIcon);
        themeDescriptionArr[48] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[49] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackgroundSelected);
        themeDescriptionArr[50] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, null, null, Theme.key_chat_messageTextIn);
        themeDescriptionArr[51] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextOut);
        themeDescriptionArr[52] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, null, null, Theme.key_chat_messageLinkIn, null);
        themeDescriptionArr[53] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageLinkOut, null);
        themeDescriptionArr[54] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheck);
        themeDescriptionArr[55] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected);
        themeDescriptionArr[56] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutClockDrawable}, null, Theme.key_chat_outSentClock);
        themeDescriptionArr[57] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedClockDrawable}, null, Theme.key_chat_outSentClockSelected);
        themeDescriptionArr[58] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInClockDrawable}, null, Theme.key_chat_inSentClock);
        themeDescriptionArr[59] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedClockDrawable}, null, Theme.key_chat_inSentClockSelected);
        themeDescriptionArr[60] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck);
        themeDescriptionArr[61] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgStickerHalfCheckDrawable, Theme.chat_msgStickerCheckDrawable, Theme.chat_msgStickerClockDrawable, Theme.chat_msgStickerViewsDrawable}, null, Theme.key_chat_serviceText);
        themeDescriptionArr[62] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaClockDrawable}, null, Theme.key_chat_mediaSentClock);
        themeDescriptionArr[63] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsDrawable}, null, Theme.key_chat_outViews);
        themeDescriptionArr[64] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable}, null, Theme.key_chat_outViewsSelected);
        themeDescriptionArr[65] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsDrawable}, null, Theme.key_chat_inViews);
        themeDescriptionArr[66] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable}, null, Theme.key_chat_inViewsSelected);
        themeDescriptionArr[67] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaViewsDrawable}, null, Theme.key_chat_mediaViews);
        themeDescriptionArr[68] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, null, Theme.key_chat_outMenu);
        themeDescriptionArr[69] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, null, Theme.key_chat_outMenuSelected);
        themeDescriptionArr[70] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuDrawable}, null, Theme.key_chat_inMenu);
        themeDescriptionArr[71] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, null, Theme.key_chat_inMenuSelected);
        themeDescriptionArr[72] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, null, Theme.key_chat_mediaMenu);
        themeDescriptionArr[73] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutInstantDrawable, Theme.chat_msgOutCallDrawable}, null, Theme.key_chat_outInstant);
        themeDescriptionArr[74] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCallSelectedDrawable}, null, Theme.key_chat_outInstantSelected);
        themeDescriptionArr[75] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_msgInCallDrawable}, null, Theme.key_chat_inInstant);
        themeDescriptionArr[76] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInCallSelectedDrawable}, null, Theme.key_chat_inInstantSelected);
        themeDescriptionArr[77] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpRedDrawable, Theme.chat_msgCallDownRedDrawable}, null, Theme.key_calls_callReceivedRedIcon);
        themeDescriptionArr[78] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable, Theme.chat_msgCallDownGreenDrawable}, null, Theme.key_calls_callReceivedGreenIcon);
        themeDescriptionArr[79] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, null, null, Theme.key_chat_sentError);
        themeDescriptionArr[80] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgErrorDrawable}, null, Theme.key_chat_sentErrorIcon);
        themeDescriptionArr[81] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, anonymousClass113, Theme.key_chat_selectedBackground);
        themeDescriptionArr[82] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, null, null, Theme.key_chat_previewDurationText);
        themeDescriptionArr[83] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, null, null, Theme.key_chat_previewGameText);
        themeDescriptionArr[84] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantText);
        themeDescriptionArr[85] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantText);
        themeDescriptionArr[86] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantSelectedText);
        themeDescriptionArr[87] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantSelectedText);
        themeDescriptionArr[88] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, null, null, Theme.key_chat_secretTimeText);
        themeDescriptionArr[89] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerNameText);
        themeDescriptionArr[90] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, null, null, Theme.key_chat_botButtonText);
        themeDescriptionArr[91] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, null, null, Theme.key_chat_botProgress);
        themeDescriptionArr[92] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inForwardedNameText);
        themeDescriptionArr[93] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outForwardedNameText);
        themeDescriptionArr[94] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inViaBotNameText);
        themeDescriptionArr[95] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outViaBotNameText);
        themeDescriptionArr[96] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerViaBotNameText);
        themeDescriptionArr[97] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyLine);
        themeDescriptionArr[98] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyLine);
        themeDescriptionArr[99] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyLine);
        themeDescriptionArr[100] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyNameText);
        themeDescriptionArr[101] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyNameText);
        themeDescriptionArr[102] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyNameText);
        themeDescriptionArr[103] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMessageText);
        themeDescriptionArr[104] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMessageText);
        themeDescriptionArr[105] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageText);
        themeDescriptionArr[106] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageText);
        themeDescriptionArr[107] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText);
        themeDescriptionArr[108] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText);
        themeDescriptionArr[109] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyMessageText);
        themeDescriptionArr[110] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewLine);
        themeDescriptionArr[111] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewLine);
        themeDescriptionArr[112] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSiteNameText);
        themeDescriptionArr[113] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSiteNameText);
        themeDescriptionArr[114] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactNameText);
        themeDescriptionArr[115] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactNameText);
        themeDescriptionArr[116] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactPhoneText);
        themeDescriptionArr[117] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactPhoneText);
        themeDescriptionArr[118] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaProgress);
        themeDescriptionArr[119] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioProgress);
        themeDescriptionArr[120] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioProgress);
        themeDescriptionArr[121] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress);
        themeDescriptionArr[122] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSelectedProgress);
        themeDescriptionArr[123] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaTimeText);
        themeDescriptionArr[124] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeText);
        themeDescriptionArr[125] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeText);
        themeDescriptionArr[126] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText);
        themeDescriptionArr[127] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_adminText);
        themeDescriptionArr[128] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_adminSelectedText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_AC3] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_HDMV_DTS] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioPerfomerText);
        themeDescriptionArr[131] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioPerfomerText);
        themeDescriptionArr[132] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioTitleText);
        themeDescriptionArr[133] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioTitleText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_SPLICE_INFO] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_E_AC3] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationText);
        themeDescriptionArr[136] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationSelectedText);
        themeDescriptionArr[137] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationSelectedText);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_DTS] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbar);
        themeDescriptionArr[139] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbar);
        themeDescriptionArr[140] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarSelected);
        themeDescriptionArr[141] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarSelected);
        themeDescriptionArr[142] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarFill);
        themeDescriptionArr[143] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioCacheSeekbar);
        themeDescriptionArr[144] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarFill);
        themeDescriptionArr[145] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioCacheSeekbar);
        themeDescriptionArr[146] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbar);
        themeDescriptionArr[147] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbar);
        themeDescriptionArr[148] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarSelected);
        themeDescriptionArr[149] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarSelected);
        themeDescriptionArr[150] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarFill);
        themeDescriptionArr[151] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarFill);
        themeDescriptionArr[152] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgress);
        themeDescriptionArr[153] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgress);
        themeDescriptionArr[154] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgressSelected);
        themeDescriptionArr[155] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgressSelected);
        themeDescriptionArr[156] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileNameText);
        themeDescriptionArr[157] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileNameText);
        themeDescriptionArr[158] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoText);
        themeDescriptionArr[159] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoText);
        themeDescriptionArr[160] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoSelectedText);
        themeDescriptionArr[161] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoSelectedText);
        themeDescriptionArr[162] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackground);
        themeDescriptionArr[163] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackground);
        themeDescriptionArr[164] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackgroundSelected);
        themeDescriptionArr[165] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackgroundSelected);
        themeDescriptionArr[166] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueNameText);
        themeDescriptionArr[167] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueNameText);
        themeDescriptionArr[168] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoText);
        themeDescriptionArr[169] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoText);
        themeDescriptionArr[170] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoSelectedText);
        themeDescriptionArr[171] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoSelectedText);
        themeDescriptionArr[172] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaInfoText);
        themeDescriptionArr[173] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, null, null, Theme.key_chat_linkSelectBackground);
        themeDescriptionArr[174] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, null, null, Theme.key_chat_textSelectBackground);
        themeDescriptionArr[175] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][0], Theme.chat_fileStatesDrawable[1][0], Theme.chat_fileStatesDrawable[2][0], Theme.chat_fileStatesDrawable[3][0], Theme.chat_fileStatesDrawable[4][0]}, null, Theme.key_chat_outLoader);
        themeDescriptionArr[176] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][0], Theme.chat_fileStatesDrawable[1][0], Theme.chat_fileStatesDrawable[2][0], Theme.chat_fileStatesDrawable[3][0], Theme.chat_fileStatesDrawable[4][0]}, null, Theme.key_chat_outBubble);
        themeDescriptionArr[177] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][1], Theme.chat_fileStatesDrawable[1][1], Theme.chat_fileStatesDrawable[2][1], Theme.chat_fileStatesDrawable[3][1], Theme.chat_fileStatesDrawable[4][1]}, null, Theme.key_chat_outLoaderSelected);
        themeDescriptionArr[178] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][1], Theme.chat_fileStatesDrawable[1][1], Theme.chat_fileStatesDrawable[2][1], Theme.chat_fileStatesDrawable[3][1], Theme.chat_fileStatesDrawable[4][1]}, null, Theme.key_chat_outBubbleSelected);
        themeDescriptionArr[179] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][0], Theme.chat_fileStatesDrawable[6][0], Theme.chat_fileStatesDrawable[7][0], Theme.chat_fileStatesDrawable[8][0], Theme.chat_fileStatesDrawable[9][0]}, null, Theme.key_chat_inLoader);
        themeDescriptionArr[180] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][0], Theme.chat_fileStatesDrawable[6][0], Theme.chat_fileStatesDrawable[7][0], Theme.chat_fileStatesDrawable[8][0], Theme.chat_fileStatesDrawable[9][0]}, null, Theme.key_chat_inBubble);
        themeDescriptionArr[181] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][1], Theme.chat_fileStatesDrawable[6][1], Theme.chat_fileStatesDrawable[7][1], Theme.chat_fileStatesDrawable[8][1], Theme.chat_fileStatesDrawable[9][1]}, null, Theme.key_chat_inLoaderSelected);
        themeDescriptionArr[182] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][1], Theme.chat_fileStatesDrawable[6][1], Theme.chat_fileStatesDrawable[7][1], Theme.chat_fileStatesDrawable[8][1], Theme.chat_fileStatesDrawable[9][1]}, null, Theme.key_chat_inBubbleSelected);
        themeDescriptionArr[183] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, Theme.key_chat_mediaLoaderPhoto);
        themeDescriptionArr[184] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, Theme.key_chat_mediaLoaderPhotoIcon);
        themeDescriptionArr[185] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, Theme.key_chat_mediaLoaderPhotoSelected);
        themeDescriptionArr[186] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, Theme.key_chat_mediaLoaderPhotoIconSelected);
        themeDescriptionArr[187] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, Theme.key_chat_outLoaderPhoto);
        themeDescriptionArr[188] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, Theme.key_chat_outLoaderPhotoIcon);
        themeDescriptionArr[PsExtractor.PRIVATE_STREAM_1] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, Theme.key_chat_outLoaderPhotoSelected);
        themeDescriptionArr[190] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, Theme.key_chat_outLoaderPhotoIconSelected);
        themeDescriptionArr[191] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, Theme.key_chat_inLoaderPhoto);
        themeDescriptionArr[PsExtractor.AUDIO_STREAM] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, Theme.key_chat_inLoaderPhotoIcon);
        themeDescriptionArr[193] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, Theme.key_chat_inLoaderPhotoSelected);
        themeDescriptionArr[194] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, Theme.key_chat_inLoaderPhotoIconSelected);
        themeDescriptionArr[195] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][0]}, null, Theme.key_chat_outFileIcon);
        themeDescriptionArr[196] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][1]}, null, Theme.key_chat_outFileSelectedIcon);
        themeDescriptionArr[197] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][0]}, null, Theme.key_chat_inFileIcon);
        themeDescriptionArr[198] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][1]}, null, Theme.key_chat_inFileSelectedIcon);
        themeDescriptionArr[199] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactBackground);
        themeDescriptionArr[Callback.DEFAULT_DRAG_ANIMATION_DURATION] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactIcon);
        themeDescriptionArr[201] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactBackground);
        themeDescriptionArr[202] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactIcon);
        themeDescriptionArr[203] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationBackground);
        themeDescriptionArr[204] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationIcon);
        themeDescriptionArr[205] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationBackground);
        themeDescriptionArr[206] = new ThemeDescription(r0.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationIcon);
        themeDescriptionArr[207] = new ThemeDescription(r0.mentionContainer, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[208] = new ThemeDescription(r0.mentionContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[209] = new ThemeDescription(r0.searchContainer, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[210] = new ThemeDescription(r0.searchContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[211] = new ThemeDescription(r0.bottomOverlay, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[212] = new ThemeDescription(r0.bottomOverlay, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[213] = new ThemeDescription(r0.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[214] = new ThemeDescription(r0.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[215] = new ThemeDescription(r0.chatActivityEnterView, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[216] = new ThemeDescription(r0.chatActivityEnterView, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        themeDescriptionArr[217] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"audioVideoButtonContainer"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[218] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, null, null, null, Theme.key_chat_messagePanelText);
        themeDescriptionArr[219] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordSendText"}, null, null, null, Theme.key_chat_fieldOverlayText);
        themeDescriptionArr[220] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, null, null, null, Theme.key_chat_messagePanelHint);
        themeDescriptionArr[221] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"sendButton"}, null, null, null, Theme.key_chat_messagePanelSend);
        themeDescriptionArr[222] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"emojiButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[223] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"botButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[224] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"notifyButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[225] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"attachButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[226] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"audioSendButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[227] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"videoSendButton"}, null, null, null, Theme.key_chat_messagePanelIcons);
        themeDescriptionArr[228] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"doneButtonImage"}, null, null, null, Theme.key_chat_editDoneIcon);
        themeDescriptionArr[229] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioPanel"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[230] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"micDrawable"}, null, null, null, Theme.key_chat_messagePanelVoicePressed);
        themeDescriptionArr[231] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"cameraDrawable"}, null, null, null, Theme.key_chat_messagePanelVoicePressed);
        themeDescriptionArr[232] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"sendDrawable"}, null, null, null, Theme.key_chat_messagePanelVoicePressed);
        themeDescriptionArr[233] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLock);
        themeDescriptionArr[234] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockTopDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLock);
        themeDescriptionArr[235] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockArrowDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLock);
        themeDescriptionArr[236] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockBackgroundDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLockBackground);
        themeDescriptionArr[237] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockShadowDrawable"}, null, null, null, Theme.key_chat_messagePanelVoiceLockShadow);
        themeDescriptionArr[238] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordDeleteImageView"}, null, null, null, Theme.key_chat_messagePanelVoiceDelete);
        themeDescriptionArr[239] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioBackground"}, null, null, null, Theme.key_chat_recordedVoiceBackground);
        themeDescriptionArr[PsExtractor.VIDEO_STREAM_MASK] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordTimeText"}, null, null, null, Theme.key_chat_recordTime);
        themeDescriptionArr[241] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"recordTimeContainer"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[242] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordCancelText"}, null, null, null, Theme.key_chat_recordVoiceCancel);
        themeDescriptionArr[243] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUND, new Class[]{ChatActivityEnterView.class}, new String[]{"recordPanel"}, null, null, null, Theme.key_chat_messagePanelBackground);
        themeDescriptionArr[244] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioTimeTextView"}, null, null, null, Theme.key_chat_messagePanelVoiceDuration);
        themeDescriptionArr[245] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"recordCancelImage"}, null, null, null, Theme.key_chat_recordVoiceCancel);
        themeDescriptionArr[246] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"doneButtonProgress"}, null, null, null, Theme.key_contextProgressInner1);
        themeDescriptionArr[247] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"doneButtonProgress"}, null, null, null, Theme.key_contextProgressOuter1);
        themeDescriptionArr[248] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"cancelBotButton"}, null, null, null, Theme.key_chat_messagePanelCancelInlineBot);
        themeDescriptionArr[249] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"redDotPaint"}, null, null, null, Theme.key_chat_recordedVoiceDot);
        themeDescriptionArr[Callback.DEFAULT_SWIPE_ANIMATION_DURATION] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"paint"}, null, null, null, Theme.key_chat_messagePanelVoiceBackground);
        themeDescriptionArr[251] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"paintRecord"}, null, null, null, Theme.key_chat_messagePanelVoiceShadow);
        themeDescriptionArr[252] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"seekBarWaveform"}, null, null, null, Theme.key_chat_recordedVoiceProgress);
        themeDescriptionArr[253] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"seekBarWaveform"}, null, null, null, Theme.key_chat_recordedVoiceProgressInner);
        themeDescriptionArr[254] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"playDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPause);
        themeDescriptionArr[255] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"pauseDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPause);
        themeDescriptionArr[256] = new ThemeDescription(r0.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"dotPaint"}, null, null, null, Theme.key_chat_emojiPanelNewTrending);
        themeDescriptionArr[257] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"playDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPausePressed);
        themeDescriptionArr[258] = new ThemeDescription(r0.chatActivityEnterView, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"pauseDrawable"}, null, null, null, Theme.key_chat_recordedVoicePlayPausePressed);
        themeDescriptionArr[259] = new ThemeDescription(r0.chatActivityEnterView != null ? r0.chatActivityEnterView.getEmojiView() : r0.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, (ThemeDescriptionDelegate) anonymousClass113, Theme.key_chat_emojiPanelBackground);
        themeDescriptionArr[260] = new ThemeDescription(r0.chatActivityEnterView != null ? r0.chatActivityEnterView.getEmojiView() : r0.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, (ThemeDescriptionDelegate) anonymousClass113, Theme.key_chat_emojiPanelShadowLine);
        themeDescriptionArr[261] = new ThemeDescription(r0.chatActivityEnterView != null ? r0.chatActivityEnterView.getEmojiView() : r0.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, (ThemeDescriptionDelegate) anonymousClass113, Theme.key_chat_emojiPanelEmptyText);
        themeDescriptionArr[262] = new ThemeDescription(r0.chatActivityEnterView != null ? r0.chatActivityEnterView.getEmojiView() : r0.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, (ThemeDescriptionDelegate) anonymousClass113, Theme.key_chat_emojiPanelIcon);
        themeDescriptionArr[263] = new ThemeDescription(r0.chatActivityEnterView != null ? r0.chatActivityEnterView.getEmojiView() : r0.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, (ThemeDescriptionDelegate) anonymousClass113, Theme.key_chat_emojiPanelIconSelected);
        themeDescriptionArr[264] = new ThemeDescription(r0.chatActivityEnterView != null ? r0.chatActivityEnterView.getEmojiView() : r0.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, (ThemeDescriptionDelegate) anonymousClass113, Theme.key_chat_emojiPanelStickerPackSelector);
        themeDescriptionArr[265] = new ThemeDescription(r0.chatActivityEnterView != null ? r0.chatActivityEnterView.getEmojiView() : r0.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, (ThemeDescriptionDelegate) anonymousClass113, Theme.key_chat_emojiPanelIconSelector);
        themeDescriptionArr[266] = new ThemeDescription(r0.chatActivityEnterView != null ? r0.chatActivityEnterView.getEmojiView() : r0.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, (ThemeDescriptionDelegate) anonymousClass113, Theme.key_chat_emojiPanelBackspace);
        themeDescriptionArr[267] = new ThemeDescription(r0.chatActivityEnterView != null ? r0.chatActivityEnterView.getEmojiView() : r0.chatActivityEnterView, 0, new Class[]{EmojiView.class}, null, null, null, (ThemeDescriptionDelegate) anonymousClass113, Theme.key_chat_emojiPanelTrendingTitle);
        if (r0.chatActivityEnterView != null) {
            emojiView = r0.chatActivityEnterView.getEmojiView();
        } else {
            emojiView = r0.chatActivityEnterView;
        }
        themeDescriptionArr[268] = new ThemeDescription(emojiView, 0, new Class[]{EmojiView.class}, null, null, null, (ThemeDescriptionDelegate) anonymousClass113, Theme.key_chat_emojiPanelTrendingDescription);
        themeDescriptionArr[269] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonText);
        themeDescriptionArr[270] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonBackground);
        themeDescriptionArr[271] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonBackgroundPressed);
        themeDescriptionArr[272] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground);
        themeDescriptionArr[273] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause);
        themeDescriptionArr[274] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle);
        themeDescriptionArr[275] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_FASTSCROLL | ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerPerformer);
        themeDescriptionArr[276] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose);
        themeDescriptionArr[277] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_returnToCallBackground);
        themeDescriptionArr[278] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_returnToCallText);
        themeDescriptionArr[279] = new ThemeDescription(r0.pinnedLineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_chat_topPanelLine);
        themeDescriptionArr[280] = new ThemeDescription(r0.pinnedMessageNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle);
        themeDescriptionArr[281] = new ThemeDescription(r0.pinnedMessageTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelMessage);
        themeDescriptionArr[282] = new ThemeDescription(r0.alertNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle);
        themeDescriptionArr[283] = new ThemeDescription(r0.alertTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelMessage);
        themeDescriptionArr[284] = new ThemeDescription(r0.closePinned, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose);
        themeDescriptionArr[285] = new ThemeDescription(r0.closeReportSpam, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose);
        themeDescriptionArr[286] = new ThemeDescription(r0.reportSpamView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_topPanelBackground);
        themeDescriptionArr[287] = new ThemeDescription(r0.alertView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_topPanelBackground);
        themeDescriptionArr[288] = new ThemeDescription(r0.pinnedMessageView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_topPanelBackground);
        themeDescriptionArr[289] = new ThemeDescription(r0.addToContactsButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_addContact);
        themeDescriptionArr[290] = new ThemeDescription(r0.reportSpamButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_reportSpam);
        themeDescriptionArr[291] = new ThemeDescription(r0.replyLineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_chat_replyPanelLine);
        themeDescriptionArr[292] = new ThemeDescription(r0.replyNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_replyPanelName);
        themeDescriptionArr[293] = new ThemeDescription(r0.replyObjectTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_replyPanelMessage);
        themeDescriptionArr[294] = new ThemeDescription(r0.replyIconImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_replyPanelIcons);
        themeDescriptionArr[295] = new ThemeDescription(r0.replyCloseImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_replyPanelClose);
        themeDescriptionArr[296] = new ThemeDescription(r0.searchUpButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[297] = new ThemeDescription(r0.searchDownButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[298] = new ThemeDescription(r0.searchCalendarButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[299] = new ThemeDescription(r0.searchUserButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_searchPanelIcons);
        themeDescriptionArr[300] = new ThemeDescription(r0.searchCountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_searchPanelText);
        themeDescriptionArr[301] = new ThemeDescription(r0.bottomOverlayText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_secretChatStatusText);
        themeDescriptionArr[302] = new ThemeDescription(r0.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText);
        themeDescriptionArr[303] = new ThemeDescription(r0.bigEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[304] = new ThemeDescription(r0.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[305] = new ThemeDescription(r0.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[306] = new ThemeDescription(r0.stickersPanelArrow, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_stickersHintPanel);
        themeDescriptionArr[307] = new ThemeDescription(r0.stickersListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{StickerCell.class}, null, null, null, Theme.key_chat_stickersHintPanel);
        themeDescriptionArr[308] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, null, null, null, Theme.key_chat_unreadMessagesStartBackground);
        themeDescriptionArr[309] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_chat_unreadMessagesStartArrowIcon);
        themeDescriptionArr[310] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_unreadMessagesStartText);
        themeDescriptionArr[311] = new ThemeDescription(r0.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[312] = new ThemeDescription(r0.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[313] = new ThemeDescription(r0.bigEmptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[314] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_SERVICEBACKGROUND, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[315] = new ThemeDescription(r0.chatListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_serviceText);
        themeDescriptionArr[316] = new ThemeDescription(r0.mentionListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{BotSwitchCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_botSwitchToInlineText);
        themeDescriptionArr[317] = new ThemeDescription(r0.mentionListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{MentionCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[318] = new ThemeDescription(r0.mentionListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{MentionCell.class}, new String[]{"usernameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[319] = new ThemeDescription(r0.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, new Drawable[]{Theme.chat_inlineResultFile, Theme.chat_inlineResultAudio, Theme.chat_inlineResultLocation}, null, Theme.key_chat_inlineResultIcon);
        themeDescriptionArr[320] = new ThemeDescription(r0.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[321] = new ThemeDescription(r0.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        themeDescriptionArr[322] = new ThemeDescription(r0.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[323] = new ThemeDescription(r0.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_chat_inAudioProgress);
        themeDescriptionArr[324] = new ThemeDescription(r0.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress);
        themeDescriptionArr[325] = new ThemeDescription(r0.mentionListView, 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_divider);
        themeDescriptionArr[326] = new ThemeDescription(r0.gifHintTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_gifSaveHintBackground);
        themeDescriptionArr[327] = new ThemeDescription(r0.gifHintTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_gifSaveHintText);
        themeDescriptionArr[328] = new ThemeDescription(r0.pagedownButtonCounter, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButtonCounterBackground);
        themeDescriptionArr[329] = new ThemeDescription(r0.pagedownButtonCounter, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_goDownButtonCounter);
        themeDescriptionArr[330] = new ThemeDescription(r0.pagedownButtonImage, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButton);
        themeDescriptionArr[331] = new ThemeDescription(r0.pagedownButtonImage, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButtonShadow);
        themeDescriptionArr[332] = new ThemeDescription(r0.pagedownButtonImage, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_goDownButtonIcon);
        themeDescriptionArr[333] = new ThemeDescription(r0.mentiondownButtonCounter, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButtonCounterBackground);
        themeDescriptionArr[334] = new ThemeDescription(r0.mentiondownButtonCounter, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_goDownButtonCounter);
        themeDescriptionArr[335] = new ThemeDescription(r0.mentiondownButtonImage, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButton);
        themeDescriptionArr[336] = new ThemeDescription(r0.mentiondownButtonImage, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButtonShadow);
        themeDescriptionArr[337] = new ThemeDescription(r0.mentiondownButtonImage, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_goDownButtonIcon);
        themeDescriptionArr[338] = new ThemeDescription(r0.avatarContainer != null ? r0.avatarContainer.getTimeItem() : null, 0, null, null, null, null, Theme.key_chat_secretTimerBackground);
        if (r0.avatarContainer != null) {
            view = r0.avatarContainer.getTimeItem();
        }
        themeDescriptionArr[339] = new ThemeDescription(view, 0, null, null, null, null, Theme.key_chat_secretTimerText);
        return themeDescriptionArr;
    }
}
