package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.messenger.ringtone.RingtoneUploader;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$AttachMenuPeerType;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$DraftMessage;
import org.telegram.tgnet.TLRPC$EmojiKeyword;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessagesFilter;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$Reaction;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_account_emojiStatuses;
import org.telegram.tgnet.TLRPC$TL_account_emojiStatusesNotModified;
import org.telegram.tgnet.TLRPC$TL_account_saveRingtone;
import org.telegram.tgnet.TLRPC$TL_account_savedRingtoneConverted;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon;
import org.telegram.tgnet.TLRPC$TL_attachMenuBots;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotsNotModified;
import org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeBotPM;
import org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeBroadcast;
import org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeChat;
import org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypePM;
import org.telegram.tgnet.TLRPC$TL_attachMenuPeerTypeSameBotPM;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_channels_getMessages;
import org.telegram.tgnet.TLRPC$TL_contacts_getTopPeers;
import org.telegram.tgnet.TLRPC$TL_contacts_resetTopPeerRating;
import org.telegram.tgnet.TLRPC$TL_contacts_topPeers;
import org.telegram.tgnet.TLRPC$TL_contacts_topPeersDisabled;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_documentEmpty;
import org.telegram.tgnet.TLRPC$TL_draftMessage;
import org.telegram.tgnet.TLRPC$TL_draftMessageEmpty;
import org.telegram.tgnet.TLRPC$TL_emojiKeyword;
import org.telegram.tgnet.TLRPC$TL_emojiKeywordDeleted;
import org.telegram.tgnet.TLRPC$TL_emojiKeywordsDifference;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_getPremiumPromo;
import org.telegram.tgnet.TLRPC$TL_help_premiumPromo;
import org.telegram.tgnet.TLRPC$TL_inputDocument;
import org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterGif;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotos;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPinned;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterVideo;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetDice;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmojiGenericAnimations;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetPremiumGifts;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC$TL_messageEmpty;
import org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote;
import org.telegram.tgnet.TLRPC$TL_messageEntityBold;
import org.telegram.tgnet.TLRPC$TL_messageEntityCode;
import org.telegram.tgnet.TLRPC$TL_messageEntityCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC$TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_messageEntityPre;
import org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler;
import org.telegram.tgnet.TLRPC$TL_messageEntityStrike;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageEntityUnderline;
import org.telegram.tgnet.TLRPC$TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_message_secret;
import org.telegram.tgnet.TLRPC$TL_messages_allStickers;
import org.telegram.tgnet.TLRPC$TL_messages_archivedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_availableReactions;
import org.telegram.tgnet.TLRPC$TL_messages_availableReactionsNotModified;
import org.telegram.tgnet.TLRPC$TL_messages_channelMessages;
import org.telegram.tgnet.TLRPC$TL_messages_clearRecentReactions;
import org.telegram.tgnet.TLRPC$TL_messages_clearRecentStickers;
import org.telegram.tgnet.TLRPC$TL_messages_faveSticker;
import org.telegram.tgnet.TLRPC$TL_messages_favedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_featuredStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getAllDrafts;
import org.telegram.tgnet.TLRPC$TL_messages_getArchivedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBots;
import org.telegram.tgnet.TLRPC$TL_messages_getAvailableReactions;
import org.telegram.tgnet.TLRPC$TL_messages_getMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getRecentReactions;
import org.telegram.tgnet.TLRPC$TL_messages_getScheduledMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getSearchCounters;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getTopReactions;
import org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC$TL_messages_reactions;
import org.telegram.tgnet.TLRPC$TL_messages_reactionsNotModified;
import org.telegram.tgnet.TLRPC$TL_messages_recentStickers;
import org.telegram.tgnet.TLRPC$TL_messages_saveDraft;
import org.telegram.tgnet.TLRPC$TL_messages_saveGif;
import org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC$TL_messages_savedGifs;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_messages_searchCounter;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.tgnet.TLRPC$TL_messages_toggleStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_uninstallStickerSet;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_stickerPack;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_topPeer;
import org.telegram.tgnet.TLRPC$TL_topPeerCategoryBotsInline;
import org.telegram.tgnet.TLRPC$TL_topPeerCategoryCorrespondents;
import org.telegram.tgnet.TLRPC$TL_topPeerCategoryPeers;
import org.telegram.tgnet.TLRPC$TL_updateBotCommands;
import org.telegram.tgnet.TLRPC$Theme;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.tgnet.TLRPC$messages_StickerSet;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.StickerSetBulletinLayout;
import org.telegram.ui.Components.StickersArchiveAlert;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.URLSpanReplacement;

public class MediaDataController extends BaseController {
    public static final String ATTACH_MENU_BOT_ANIMATED_ICON_KEY = "android_animated";
    public static final String ATTACH_MENU_BOT_COLOR_DARK_ICON = "dark_icon";
    public static final String ATTACH_MENU_BOT_COLOR_DARK_TEXT = "dark_text";
    public static final String ATTACH_MENU_BOT_COLOR_LIGHT_ICON = "light_icon";
    public static final String ATTACH_MENU_BOT_COLOR_LIGHT_TEXT = "light_text";
    public static final String ATTACH_MENU_BOT_PLACEHOLDER_STATIC_KEY = "placeholder_static";
    public static final String ATTACH_MENU_BOT_STATIC_ICON_KEY = "default_static";
    private static Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static Pattern ITALIC_PATTERN = Pattern.compile("__(.+?)__");
    private static volatile MediaDataController[] Instance = new MediaDataController[4];
    public static final int MEDIA_AUDIO = 2;
    public static final int MEDIA_FILE = 1;
    public static final int MEDIA_GIF = 5;
    public static final int MEDIA_MUSIC = 4;
    public static final int MEDIA_PHOTOS_ONLY = 6;
    public static final int MEDIA_PHOTOVIDEO = 0;
    public static final int MEDIA_TYPES_COUNT = 8;
    public static final int MEDIA_URL = 3;
    public static final int MEDIA_VIDEOS_ONLY = 7;
    public static String SHORTCUT_CATEGORY = "org.telegram.messenger.SHORTCUT_SHARE";
    private static Pattern SPOILER_PATTERN = Pattern.compile("\\|\\|(.+?)\\|\\|");
    private static Pattern STRIKE_PATTERN = Pattern.compile("~~(.+?)~~");
    public static final int TYPE_EMOJI = 4;
    public static final int TYPE_EMOJIPACKS = 5;
    public static final int TYPE_FAVE = 2;
    public static final int TYPE_FEATURED = 3;
    public static final int TYPE_FEATURED_EMOJIPACKS = 6;
    public static final int TYPE_GREETINGS = 3;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MASK = 1;
    public static final int TYPE_PREMIUM_STICKERS = 7;
    private static RectF bitmapRect;
    private static Comparator<TLRPC$MessageEntity> entityComparator = MediaDataController$$ExternalSyntheticLambda147.INSTANCE;
    private static Paint erasePaint;
    private static final Object[] lockObjects = new Object[4];
    private static Paint roundPaint;
    private static Path roundPath;
    private HashMap<String, ArrayList<TLRPC$Document>> allStickers = new HashMap<>();
    private HashMap<String, ArrayList<TLRPC$Document>> allStickersFeatured = new HashMap<>();
    private int[] archivedStickersCount = new int[7];
    private TLRPC$TL_attachMenuBots attachMenuBots = new TLRPC$TL_attachMenuBots();
    private HashMap<String, TLRPC$BotInfo> botInfos = new HashMap<>();
    private LongSparseArray<TLRPC$Message> botKeyboards = new LongSparseArray<>();
    private SparseLongArray botKeyboardsByMids = new SparseLongArray();
    private HashMap<String, Boolean> currentFetchingEmoji = new HashMap<>();
    public final ArrayList<ChatThemeBottomSheet.ChatThemeItem> defaultEmojiThemes = new ArrayList<>();
    private LongSparseArray<String> diceEmojiStickerSetsById = new LongSparseArray<>();
    private HashMap<String, TLRPC$TL_messages_stickerSet> diceStickerSetsByEmoji = new HashMap<>();
    private String doubleTapReaction;
    private LongSparseArray<SparseArray<TLRPC$Message>> draftMessages = new LongSparseArray<>();
    private SharedPreferences draftPreferences;
    private LongSparseArray<SparseArray<TLRPC$DraftMessage>> drafts = new LongSparseArray<>();
    private LongSparseArray<Integer> draftsFolderIds = new LongSparseArray<>();
    private ArrayList<TLRPC$EmojiStatus>[] emojiStatuses = new ArrayList[2];
    private Long[] emojiStatusesFetchDate = new Long[2];
    private boolean[] emojiStatusesFetching = new boolean[2];
    private boolean[] emojiStatusesFromCacheFetched = new boolean[2];
    private long[] emojiStatusesHash = new long[2];
    private List<TLRPC$TL_availableReaction> enabledReactionsList = new ArrayList();
    private ArrayList<TLRPC$StickerSetCovered>[] featuredStickerSets = {new ArrayList<>(), new ArrayList<>()};
    private LongSparseArray<TLRPC$StickerSetCovered>[] featuredStickerSetsById = {new LongSparseArray<>(), new LongSparseArray<>()};
    private boolean[] featuredStickersLoaded = new boolean[2];
    private TLRPC$Document greetingsSticker;
    private LongSparseArray<TLRPC$TL_messages_stickerSet> groupStickerSets = new LongSparseArray<>();
    public ArrayList<TLRPC$TL_topPeer> hints = new ArrayList<>();
    private boolean inTransaction;
    public ArrayList<TLRPC$TL_topPeer> inlineBots = new ArrayList<>();
    private ArrayList<Long> installedForceStickerSetsById = new ArrayList<>();
    private LongSparseArray<TLRPC$TL_messages_stickerSet> installedStickerSetsById = new LongSparseArray<>();
    private boolean isLoadingMenuBots;
    private boolean isLoadingPremiumPromo;
    private boolean isLoadingReactions;
    private long lastDialogId;
    private int lastGuid;
    private long lastMergeDialogId;
    private int lastReplyMessageId;
    private int lastReqId;
    private int lastReturnedNum;
    private TLRPC$Chat lastSearchChat;
    private String lastSearchQuery;
    private TLRPC$User lastSearchUser;
    private int[] loadDate = new int[7];
    private int[] loadFeaturedDate = new int[2];
    private long[] loadFeaturedHash = new long[2];
    public boolean loadFeaturedPremium;
    private long[] loadHash = new long[7];
    boolean loaded;
    boolean loading;
    private HashSet<String> loadingDiceStickerSets = new HashSet<>();
    private boolean loadingDrafts;
    private boolean[] loadingFeaturedStickers = new boolean[2];
    private boolean loadingGenericAnimations;
    private boolean loadingMoreSearchMessages;
    private LongSparseArray<Boolean> loadingPinnedMessages = new LongSparseArray<>();
    private boolean loadingPremiumGiftStickers;
    private boolean loadingRecentGifs;
    boolean loadingRecentReactions;
    private boolean[] loadingRecentStickers = new boolean[9];
    private boolean[] loadingStickers = new boolean[7];
    private int menuBotsUpdateDate;
    private long menuBotsUpdateHash;
    private int mergeReqId;
    private int[] messagesSearchCount = {0, 0};
    private boolean[] messagesSearchEndReached = {false, false};
    public final ArrayList<TLRPC$Document> premiumPreviewStickers = new ArrayList<>();
    private TLRPC$TL_help_premiumPromo premiumPromo;
    private int premiumPromoUpdateDate;
    boolean previewStickersLoading;
    private boolean reactionsCacheGenerated;
    private List<TLRPC$TL_availableReaction> reactionsList = new ArrayList();
    private HashMap<String, TLRPC$TL_availableReaction> reactionsMap = new HashMap<>();
    private int reactionsUpdateDate;
    private int reactionsUpdateHash;
    private ArrayList<Long>[] readingStickerSets = {new ArrayList<>(), new ArrayList<>()};
    private ArrayList<TLRPC$Document> recentGifs = new ArrayList<>();
    private boolean recentGifsLoaded;
    ArrayList<TLRPC$Reaction> recentReactions = new ArrayList<>();
    private ArrayList<TLRPC$Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    private boolean[] recentStickersLoaded = new boolean[9];
    private LongSparseArray<Runnable> removingStickerSetsUndos = new LongSparseArray<>();
    private int reqId;
    public final RingtoneDataStore ringtoneDataStore;
    public HashMap<String, RingtoneUploader> ringtoneUploaderHashMap = new HashMap<>();
    private Runnable[] scheduledLoadStickers = new Runnable[7];
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();
    private SparseArray<MessageObject>[] searchResultMessagesMap = {new SparseArray<>(), new SparseArray<>()};
    private ArrayList<TLRPC$TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(0), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    private LongSparseArray<TLRPC$TL_messages_stickerSet> stickerSetsById = new LongSparseArray<>();
    private ConcurrentHashMap<String, TLRPC$TL_messages_stickerSet> stickerSetsByName = new ConcurrentHashMap<>(100, 1.0f, 1);
    private LongSparseArray<String> stickersByEmoji = new LongSparseArray<>();
    private LongSparseArray<TLRPC$Document>[] stickersByIds = {new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>()};
    private boolean[] stickersLoaded = new boolean[7];
    ArrayList<TLRPC$Reaction> topReactions = new ArrayList<>();
    private boolean triedLoadingEmojipacks = false;
    private ArrayList<Long> uninstalledForceStickerSetsById = new ArrayList<>();
    private ArrayList<Long>[] unreadStickerSets = {new ArrayList<>(), new ArrayList<>()};
    private HashMap<String, ArrayList<TLRPC$Message>> verifyingMessages = new HashMap<>();

    public interface KeywordResultCallback {
        void run(ArrayList<KeywordResult> arrayList, String str);
    }

    public static long calcHash(long j, long j2) {
        return (((j ^ (j2 >> 21)) ^ (j2 << 35)) ^ (j2 >> 4)) + j2;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$markFeaturedStickersAsRead$51(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$markFeaturedStickersByIdAsRead$52(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$removeInline$131(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$removePeer$132(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$saveDraft$164(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    public static MediaDataController getInstance(int i) {
        MediaDataController mediaDataController = Instance[i];
        if (mediaDataController == null) {
            synchronized (lockObjects) {
                mediaDataController = Instance[i];
                if (mediaDataController == null) {
                    MediaDataController[] mediaDataControllerArr = Instance;
                    MediaDataController mediaDataController2 = new MediaDataController(i);
                    mediaDataControllerArr[i] = mediaDataController2;
                    mediaDataController = mediaDataController2;
                }
            }
        }
        return mediaDataController;
    }

    public MediaDataController(int i) {
        super(i);
        boolean z;
        if (this.currentAccount == 0) {
            this.draftPreferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);
        } else {
            Context context = ApplicationLoader.applicationContext;
            this.draftPreferences = context.getSharedPreferences("drafts" + this.currentAccount, 0);
        }
        for (Map.Entry next : this.draftPreferences.getAll().entrySet()) {
            try {
                String str = (String) next.getKey();
                long longValue = Utilities.parseLong(str).longValue();
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes((String) next.getValue()));
                if (!str.startsWith("r_")) {
                    z = str.startsWith("rt_");
                    if (!z) {
                        TLRPC$DraftMessage TLdeserialize = TLRPC$DraftMessage.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                        if (TLdeserialize != null) {
                            SparseArray sparseArray = this.drafts.get(longValue);
                            if (sparseArray == null) {
                                sparseArray = new SparseArray();
                                this.drafts.put(longValue, sparseArray);
                            }
                            sparseArray.put(str.startsWith("t_") ? Utilities.parseInt((CharSequence) str.substring(str.lastIndexOf(95) + 1)).intValue() : 0, TLdeserialize);
                        }
                        serializedData.cleanup();
                    }
                } else {
                    z = false;
                }
                TLRPC$Message TLdeserialize2 = TLRPC$Message.TLdeserialize(serializedData, serializedData.readInt32(true), true);
                if (TLdeserialize2 != null) {
                    TLdeserialize2.readAttachPath(serializedData, getUserConfig().clientUserId);
                    SparseArray sparseArray2 = this.draftMessages.get(longValue);
                    if (sparseArray2 == null) {
                        sparseArray2 = new SparseArray();
                        this.draftMessages.put(longValue, sparseArray2);
                    }
                    sparseArray2.put(z ? Utilities.parseInt((CharSequence) str.substring(str.lastIndexOf(95) + 1)).intValue() : 0, TLdeserialize2);
                }
                serializedData.cleanup();
            } catch (Exception unused) {
            }
        }
        loadStickersByEmojiOrName("tg_placeholders_android", false, true);
        loadEmojiThemes();
        loadRecentAndTopReactions(false);
        this.ringtoneDataStore = new RingtoneDataStore(this.currentAccount);
    }

    public void cleanup() {
        int i = 0;
        while (true) {
            ArrayList<TLRPC$Document>[] arrayListArr = this.recentStickers;
            if (i >= arrayListArr.length) {
                break;
            }
            if (arrayListArr[i] != null) {
                arrayListArr[i].clear();
            }
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = false;
            i++;
        }
        for (int i2 = 0; i2 < 4; i2++) {
            this.loadHash[i2] = 0;
            this.loadDate[i2] = 0;
            this.stickerSets[i2].clear();
            this.loadingStickers[i2] = false;
            this.stickersLoaded[i2] = false;
        }
        this.loadingPinnedMessages.clear();
        int[] iArr = this.loadFeaturedDate;
        iArr[0] = 0;
        long[] jArr = this.loadFeaturedHash;
        jArr[0] = 0;
        iArr[1] = 0;
        jArr[1] = 0;
        this.allStickers.clear();
        this.allStickersFeatured.clear();
        this.stickersByEmoji.clear();
        this.featuredStickerSetsById[0].clear();
        this.featuredStickerSets[0].clear();
        this.featuredStickerSetsById[1].clear();
        this.featuredStickerSets[1].clear();
        this.unreadStickerSets[0].clear();
        this.unreadStickerSets[1].clear();
        this.recentGifs.clear();
        this.stickerSetsById.clear();
        this.installedStickerSetsById.clear();
        this.stickerSetsByName.clear();
        this.diceStickerSetsByEmoji.clear();
        this.diceEmojiStickerSetsById.clear();
        this.loadingDiceStickerSets.clear();
        boolean[] zArr = this.loadingFeaturedStickers;
        zArr[0] = false;
        boolean[] zArr2 = this.featuredStickersLoaded;
        zArr2[0] = false;
        zArr[1] = false;
        zArr2[1] = false;
        this.loadingRecentGifs = false;
        this.recentGifsLoaded = false;
        this.currentFetchingEmoji.clear();
        if (Build.VERSION.SDK_INT >= 25) {
            Utilities.globalQueue.postRunnable(MediaDataController$$ExternalSyntheticLambda144.INSTANCE);
        }
        this.verifyingMessages.clear();
        this.loading = false;
        this.loaded = false;
        this.hints.clear();
        this.inlineBots.clear();
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda6(this));
        this.drafts.clear();
        this.draftMessages.clear();
        this.draftPreferences.edit().clear().apply();
        this.botInfos.clear();
        this.botKeyboards.clear();
        this.botKeyboardsByMids.clear();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$cleanup$0() {
        try {
            ShortcutManagerCompat.removeAllDynamicShortcuts(ApplicationLoader.applicationContext);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanup$1() {
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
    }

    public boolean areStickersLoaded(int i) {
        return this.stickersLoaded[i];
    }

    public void checkStickers(int i) {
        if (this.loadingStickers[i]) {
            return;
        }
        if (!this.stickersLoaded[i] || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadDate[i])) >= 3600) {
            loadStickers(i, true, false);
        }
    }

    public void checkReactions() {
        if (!this.isLoadingReactions && Math.abs((System.currentTimeMillis() / 1000) - ((long) this.reactionsUpdateDate)) >= 3600) {
            loadReactions(true, false);
        }
    }

    public void checkMenuBots() {
        if (!this.isLoadingMenuBots && Math.abs((System.currentTimeMillis() / 1000) - ((long) this.menuBotsUpdateDate)) >= 3600) {
            loadAttachMenuBots(true, false);
        }
    }

    public void checkPremiumPromo() {
        if (!this.isLoadingPremiumPromo && Math.abs((System.currentTimeMillis() / 1000) - ((long) this.premiumPromoUpdateDate)) >= 3600) {
            loadPremiumPromo(true);
        }
    }

    public TLRPC$TL_help_premiumPromo getPremiumPromo() {
        return this.premiumPromo;
    }

    public TLRPC$TL_attachMenuBots getAttachMenuBots() {
        return this.attachMenuBots;
    }

    public void loadAttachMenuBots(boolean z, boolean z2) {
        long j;
        this.isLoadingMenuBots = true;
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda11(this));
            return;
        }
        TLRPC$TL_messages_getAttachMenuBots tLRPC$TL_messages_getAttachMenuBots = new TLRPC$TL_messages_getAttachMenuBots();
        if (z2) {
            j = 0;
        } else {
            j = this.menuBotsUpdateHash;
        }
        tLRPC$TL_messages_getAttachMenuBots.hash = j;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getAttachMenuBots, new MediaDataController$$ExternalSyntheticLambda162(this));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: org.telegram.tgnet.TLRPC$TL_attachMenuBots} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: org.telegram.tgnet.TLRPC$TL_attachMenuBots} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: org.telegram.tgnet.TLRPC$TL_attachMenuBots} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.telegram.SQLite.SQLiteCursor} */
    /* JADX WARNING: type inference failed for: r7v5, types: [org.telegram.tgnet.TLRPC$TL_attachMenuBots] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0065  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadAttachMenuBots$2() {
        /*
            r14 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            org.telegram.messenger.MessagesStorage r4 = r14.getMessagesStorage()     // Catch:{ Exception -> 0x004f }
            org.telegram.SQLite.SQLiteDatabase r4 = r4.getDatabase()     // Catch:{ Exception -> 0x004f }
            java.lang.String r5 = "SELECT data, hash, date FROM attach_menu_bots"
            java.lang.Object[] r6 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x004f }
            org.telegram.SQLite.SQLiteCursor r4 = r4.queryFinalized(r5, r6)     // Catch:{ Exception -> 0x004f }
            boolean r5 = r4.next()     // Catch:{ Exception -> 0x0045, all -> 0x0043 }
            if (r5 == 0) goto L_0x003c
            org.telegram.tgnet.NativeByteBuffer r5 = r4.byteBufferValue(r1)     // Catch:{ Exception -> 0x0045, all -> 0x0043 }
            r6 = 1
            if (r5 == 0) goto L_0x0033
            int r7 = r5.readInt32(r1)     // Catch:{ Exception -> 0x0045, all -> 0x0043 }
            org.telegram.tgnet.TLRPC$AttachMenuBots r7 = org.telegram.tgnet.TLRPC$AttachMenuBots.TLdeserialize(r5, r7, r6)     // Catch:{ Exception -> 0x0045, all -> 0x0043 }
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_attachMenuBots     // Catch:{ Exception -> 0x0045, all -> 0x0043 }
            if (r8 == 0) goto L_0x0030
            org.telegram.tgnet.TLRPC$TL_attachMenuBots r7 = (org.telegram.tgnet.TLRPC$TL_attachMenuBots) r7     // Catch:{ Exception -> 0x0045, all -> 0x0043 }
            r0 = r7
        L_0x0030:
            r5.reuse()     // Catch:{ Exception -> 0x0045, all -> 0x0043 }
        L_0x0033:
            long r2 = r4.longValue(r6)     // Catch:{ Exception -> 0x0045, all -> 0x0043 }
            r5 = 2
            int r1 = r4.intValue(r5)     // Catch:{ Exception -> 0x0045, all -> 0x0043 }
        L_0x003c:
            r4.dispose()
            r7 = r0
            r10 = r1
            r8 = r2
            goto L_0x005d
        L_0x0043:
            r0 = move-exception
            goto L_0x0063
        L_0x0045:
            r5 = move-exception
            r12 = r2
            r2 = r0
            r0 = r4
            r3 = r12
            goto L_0x0052
        L_0x004b:
            r1 = move-exception
            r4 = r0
            r0 = r1
            goto L_0x0063
        L_0x004f:
            r5 = move-exception
            r3 = r2
            r2 = r0
        L_0x0052:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5, (boolean) r1)     // Catch:{ all -> 0x004b }
            if (r0 == 0) goto L_0x005a
            r0.dispose()
        L_0x005a:
            r7 = r2
            r8 = r3
            r10 = 0
        L_0x005d:
            r11 = 1
            r6 = r14
            r6.processLoadedMenuBots(r7, r8, r10, r11)
            return
        L_0x0063:
            if (r4 == 0) goto L_0x0068
            r4.dispose()
        L_0x0068:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadAttachMenuBots$2():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAttachMenuBots$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (tLObject instanceof TLRPC$TL_attachMenuBotsNotModified) {
            processLoadedMenuBots((TLRPC$TL_attachMenuBots) null, 0, currentTimeMillis, false);
        } else if (tLObject instanceof TLRPC$TL_attachMenuBots) {
            TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots = (TLRPC$TL_attachMenuBots) tLObject;
            processLoadedMenuBots(tLRPC$TL_attachMenuBots, tLRPC$TL_attachMenuBots.hash, currentTimeMillis, false);
        }
    }

    public void processLoadedMenuBots(TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots, long j, int i, boolean z) {
        if (!(tLRPC$TL_attachMenuBots == null || i == 0)) {
            this.attachMenuBots = tLRPC$TL_attachMenuBots;
            this.menuBotsUpdateHash = j;
        }
        this.menuBotsUpdateDate = i;
        if (tLRPC$TL_attachMenuBots != null) {
            getMessagesController().putUsers(tLRPC$TL_attachMenuBots.users, z);
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda16(this));
        }
        if (!z) {
            putMenuBotsToCache(tLRPC$TL_attachMenuBots, j, i);
        } else if (Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 3600) {
            loadAttachMenuBots(false, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedMenuBots$4() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.attachMenuBotsDidLoad, new Object[0]);
    }

    private void putMenuBotsToCache(TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots, long j, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda99(this, tLRPC$TL_attachMenuBots, j, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putMenuBotsToCache$5(TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots, long j, int i) {
        if (tLRPC$TL_attachMenuBots != null) {
            try {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM attach_menu_bots").stepThis().dispose();
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO attach_menu_bots VALUES(?, ?, ?)");
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_attachMenuBots.getObjectSize());
                tLRPC$TL_attachMenuBots.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindLong(2, j);
                executeFast.bindInteger(3, i);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE attach_menu_bots SET date = ?");
            executeFast2.requery();
            executeFast2.bindLong(1, (long) i);
            executeFast2.step();
            executeFast2.dispose();
        }
    }

    public void loadPremiumPromo(boolean z) {
        this.isLoadingPremiumPromo = true;
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda19(this));
            return;
        }
        getConnectionsManager().sendRequest(new TLRPC$TL_help_getPremiumPromo(), new MediaDataController$$ExternalSyntheticLambda161(this));
    }

    /* JADX WARNING: type inference failed for: r0v2, types: [org.telegram.tgnet.TLRPC$TL_help_premiumPromo] */
    /* JADX WARNING: type inference failed for: r0v4 */
    /* JADX WARNING: type inference failed for: r0v10, types: [org.telegram.tgnet.TLRPC$TL_help_premiumPromo] */
    /* JADX WARNING: type inference failed for: r0v11 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:29:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadPremiumPromo$6() {
        /*
            r7 = this;
            r0 = 0
            r1 = 1
            r2 = 0
            org.telegram.messenger.MessagesStorage r3 = r7.getMessagesStorage()     // Catch:{ Exception -> 0x003d }
            org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch:{ Exception -> 0x003d }
            java.lang.String r4 = "SELECT data, date FROM premium_promo"
            java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x003d }
            org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r5)     // Catch:{ Exception -> 0x003d }
            boolean r4 = r3.next()     // Catch:{ Exception -> 0x0034, all -> 0x0032 }
            if (r4 == 0) goto L_0x002e
            org.telegram.tgnet.NativeByteBuffer r4 = r3.byteBufferValue(r2)     // Catch:{ Exception -> 0x0034, all -> 0x0032 }
            if (r4 == 0) goto L_0x002a
            int r5 = r4.readInt32(r2)     // Catch:{ Exception -> 0x0034, all -> 0x0032 }
            org.telegram.tgnet.TLRPC$TL_help_premiumPromo r0 = org.telegram.tgnet.TLRPC$TL_help_premiumPromo.TLdeserialize(r4, r5, r1)     // Catch:{ Exception -> 0x0034, all -> 0x0032 }
            r4.reuse()     // Catch:{ Exception -> 0x0034, all -> 0x0032 }
        L_0x002a:
            int r2 = r3.intValue(r1)     // Catch:{ Exception -> 0x0034, all -> 0x0032 }
        L_0x002e:
            r3.dispose()
            goto L_0x0048
        L_0x0032:
            r0 = move-exception
            goto L_0x004e
        L_0x0034:
            r4 = move-exception
            r6 = r3
            r3 = r0
            r0 = r6
            goto L_0x003f
        L_0x0039:
            r1 = move-exception
            r3 = r0
            r0 = r1
            goto L_0x004e
        L_0x003d:
            r4 = move-exception
            r3 = r0
        L_0x003f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4, (boolean) r2)     // Catch:{ all -> 0x0039 }
            if (r0 == 0) goto L_0x0047
            r0.dispose()
        L_0x0047:
            r0 = r3
        L_0x0048:
            if (r0 == 0) goto L_0x004d
            r7.processLoadedPremiumPromo(r0, r2, r1)
        L_0x004d:
            return
        L_0x004e:
            if (r3 == 0) goto L_0x0053
            r3.dispose()
        L_0x0053:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPremiumPromo$6():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPremiumPromo$7(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (tLObject instanceof TLRPC$TL_help_premiumPromo) {
            processLoadedPremiumPromo((TLRPC$TL_help_premiumPromo) tLObject, currentTimeMillis, false);
        }
    }

    private void processLoadedPremiumPromo(TLRPC$TL_help_premiumPromo tLRPC$TL_help_premiumPromo, int i, boolean z) {
        this.premiumPromo = tLRPC$TL_help_premiumPromo;
        this.premiumPromoUpdateDate = i;
        getMessagesController().putUsers(tLRPC$TL_help_premiumPromo.users, z);
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda12(this));
        if (!z) {
            putPremiumPromoToCache(tLRPC$TL_help_premiumPromo, i);
        } else if (Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 86400 || BuildVars.DEBUG_PRIVATE_VERSION) {
            loadPremiumPromo(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedPremiumPromo$8() {
        getNotificationCenter().postNotificationName(NotificationCenter.premiumPromoUpdated, new Object[0]);
    }

    private void putPremiumPromoToCache(TLRPC$TL_help_premiumPromo tLRPC$TL_help_premiumPromo, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda105(this, tLRPC$TL_help_premiumPromo, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putPremiumPromoToCache$9(TLRPC$TL_help_premiumPromo tLRPC$TL_help_premiumPromo, int i) {
        if (tLRPC$TL_help_premiumPromo != null) {
            try {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM premium_promo").stepThis().dispose();
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO premium_promo VALUES(?, ?)");
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_help_premiumPromo.getObjectSize());
                tLRPC$TL_help_premiumPromo.serializeToStream(nativeByteBuffer);
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindInteger(2, i);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE premium_promo SET date = ?");
            executeFast2.requery();
            executeFast2.bindInteger(1, i);
            executeFast2.step();
            executeFast2.dispose();
        }
    }

    public List<TLRPC$TL_availableReaction> getReactionsList() {
        return this.reactionsList;
    }

    public void loadReactions(boolean z, boolean z2) {
        int i;
        this.isLoadingReactions = true;
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda20(this));
            return;
        }
        TLRPC$TL_messages_getAvailableReactions tLRPC$TL_messages_getAvailableReactions = new TLRPC$TL_messages_getAvailableReactions();
        if (z2) {
            i = 0;
        } else {
            i = this.reactionsUpdateHash;
        }
        tLRPC$TL_messages_getAvailableReactions.hash = i;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getAvailableReactions, new MediaDataController$$ExternalSyntheticLambda166(this));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0076  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadReactions$10() {
        /*
            r9 = this;
            r0 = 0
            r1 = 1
            r2 = 0
            org.telegram.messenger.MessagesStorage r3 = r9.getMessagesStorage()     // Catch:{ Exception -> 0x005f, all -> 0x005b }
            org.telegram.SQLite.SQLiteDatabase r3 = r3.getDatabase()     // Catch:{ Exception -> 0x005f, all -> 0x005b }
            java.lang.String r4 = "SELECT data, hash, date FROM reactions"
            java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x005f, all -> 0x005b }
            org.telegram.SQLite.SQLiteCursor r3 = r3.queryFinalized(r4, r5)     // Catch:{ Exception -> 0x005f, all -> 0x005b }
            boolean r4 = r3.next()     // Catch:{ Exception -> 0x0056 }
            if (r4 == 0) goto L_0x0051
            org.telegram.tgnet.NativeByteBuffer r4 = r3.byteBufferValue(r2)     // Catch:{ Exception -> 0x0056 }
            if (r4 == 0) goto L_0x0040
            int r5 = r4.readInt32(r2)     // Catch:{ Exception -> 0x0056 }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Exception -> 0x0056 }
            r6.<init>(r5)     // Catch:{ Exception -> 0x0056 }
            r0 = 0
        L_0x0029:
            if (r0 >= r5) goto L_0x0039
            int r7 = r4.readInt32(r2)     // Catch:{ Exception -> 0x003e }
            org.telegram.tgnet.TLRPC$TL_availableReaction r7 = org.telegram.tgnet.TLRPC$TL_availableReaction.TLdeserialize(r4, r7, r1)     // Catch:{ Exception -> 0x003e }
            r6.add(r7)     // Catch:{ Exception -> 0x003e }
            int r0 = r0 + 1
            goto L_0x0029
        L_0x0039:
            r4.reuse()     // Catch:{ Exception -> 0x003e }
            r0 = r6
            goto L_0x0040
        L_0x003e:
            r0 = move-exception
            goto L_0x0059
        L_0x0040:
            int r4 = r3.intValue(r1)     // Catch:{ Exception -> 0x0056 }
            r5 = 2
            int r2 = r3.intValue(r5)     // Catch:{ Exception -> 0x004d }
            r8 = r4
            r4 = r2
            r2 = r8
            goto L_0x0052
        L_0x004d:
            r5 = move-exception
            r6 = r0
            r0 = r5
            goto L_0x0064
        L_0x0051:
            r4 = 0
        L_0x0052:
            r3.dispose()
            goto L_0x006f
        L_0x0056:
            r4 = move-exception
            r6 = r0
            r0 = r4
        L_0x0059:
            r4 = 0
            goto L_0x0064
        L_0x005b:
            r1 = move-exception
            r3 = r0
            r0 = r1
            goto L_0x0074
        L_0x005f:
            r3 = move-exception
            r6 = r0
            r4 = 0
            r0 = r3
            r3 = r6
        L_0x0064:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r2)     // Catch:{ all -> 0x0073 }
            if (r3 == 0) goto L_0x006c
            r3.dispose()
        L_0x006c:
            r2 = r4
            r0 = r6
            r4 = 0
        L_0x006f:
            r9.processLoadedReactions(r0, r2, r4, r1)
            return
        L_0x0073:
            r0 = move-exception
        L_0x0074:
            if (r3 == 0) goto L_0x0079
            r3.dispose()
        L_0x0079:
            goto L_0x007b
        L_0x007a:
            throw r0
        L_0x007b:
            goto L_0x007a
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadReactions$10():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReactions$11(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (tLObject instanceof TLRPC$TL_messages_availableReactionsNotModified) {
            processLoadedReactions((List<TLRPC$TL_availableReaction>) null, 0, currentTimeMillis, false);
        } else if (tLObject instanceof TLRPC$TL_messages_availableReactions) {
            TLRPC$TL_messages_availableReactions tLRPC$TL_messages_availableReactions = (TLRPC$TL_messages_availableReactions) tLObject;
            processLoadedReactions(tLRPC$TL_messages_availableReactions.reactions, tLRPC$TL_messages_availableReactions.hash, currentTimeMillis, false);
        }
    }

    public void processLoadedReactions(List<TLRPC$TL_availableReaction> list, int i, int i2, boolean z) {
        if (!(list == null || i2 == 0)) {
            this.reactionsList.clear();
            this.reactionsMap.clear();
            this.enabledReactionsList.clear();
            this.reactionsList.addAll(list);
            for (int i3 = 0; i3 < this.reactionsList.size(); i3++) {
                this.reactionsList.get(i3).positionInList = i3;
                this.reactionsMap.put(this.reactionsList.get(i3).reaction, this.reactionsList.get(i3));
                if (!this.reactionsList.get(i3).inactive) {
                    this.enabledReactionsList.add(this.reactionsList.get(i3));
                }
            }
            this.reactionsUpdateHash = i;
        }
        this.reactionsUpdateDate = i2;
        if (list != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda8(this));
        }
        this.isLoadingReactions = false;
        if (!z) {
            putReactionsToCache(list, i, i2);
        } else if (Math.abs((System.currentTimeMillis() / 1000) - ((long) i2)) >= 3600) {
            loadReactions(false, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedReactions$12() {
        preloadReactions();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reactionsDidLoad, new Object[0]);
    }

    public void preloadReactions() {
        if (this.reactionsList != null && !this.reactionsCacheGenerated) {
            this.reactionsCacheGenerated = true;
            ArrayList arrayList = new ArrayList(this.reactionsList);
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$TL_availableReaction tLRPC$TL_availableReaction = (TLRPC$TL_availableReaction) arrayList.get(i);
                int sizeForBigReaction = ReactionsEffectOverlay.sizeForBigReaction();
                preloadImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.around_animation), ReactionsEffectOverlay.getFilterForAroundAnimation(), true);
                ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$TL_availableReaction.effect_animation);
                preloadImage(forDocument, sizeForBigReaction + "_" + sizeForBigReaction);
                preloadImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.activate_animation), (String) null);
                preloadImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.appear_animation), "30_30_nolimit_pcache");
                preloadImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.center_icon), (String) null);
            }
        }
    }

    private void preloadImage(ImageLocation imageLocation, String str) {
        preloadImage(imageLocation, str, false);
    }

    private void preloadImage(ImageLocation imageLocation, String str, boolean z) {
        ImageReceiver imageReceiver = new ImageReceiver();
        imageReceiver.setDelegate(new MediaDataController$$ExternalSyntheticLambda155(imageReceiver));
        imageReceiver.setFileLoadingPriority(0);
        imageReceiver.setUniqKeyPrefix("preload");
        imageReceiver.setImage(imageLocation, str, (Drawable) null, (String) null, 0, 11);
        ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$preloadImage$14(ImageReceiver imageReceiver, ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
        if (z) {
            RLottieDrawable lottieAnimation = imageReceiver.getLottieAnimation();
            if (lottieAnimation != null) {
                lottieAnimation.checkCache(new MediaDataController$$ExternalSyntheticLambda1(imageReceiver));
                return;
            }
            imageReceiver.clearImage();
            imageReceiver.setDelegate((ImageReceiver.ImageReceiverDelegate) null);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$preloadImage$13(ImageReceiver imageReceiver) {
        imageReceiver.clearImage();
        imageReceiver.setDelegate((ImageReceiver.ImageReceiverDelegate) null);
    }

    private void putReactionsToCache(List<TLRPC$TL_availableReaction> list, int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda70(this, list != null ? new ArrayList(list) : null, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putReactionsToCache$15(ArrayList arrayList, int i, int i2) {
        if (arrayList != null) {
            try {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM reactions").stepThis().dispose();
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO reactions VALUES(?, ?, ?)");
                executeFast.requery();
                int i3 = 4;
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    i3 += ((TLRPC$TL_availableReaction) arrayList.get(i4)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    ((TLRPC$TL_availableReaction) arrayList.get(i5)).serializeToStream(nativeByteBuffer);
                }
                executeFast.bindByteBuffer(1, nativeByteBuffer);
                executeFast.bindInteger(2, i);
                executeFast.bindInteger(3, i2);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE reactions SET date = ?");
            executeFast2.requery();
            executeFast2.bindLong(1, (long) i2);
            executeFast2.step();
            executeFast2.dispose();
        }
    }

    public void checkFeaturedStickers() {
        if (this.loadingFeaturedStickers[0]) {
            return;
        }
        if (!this.featuredStickersLoaded[0] || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadFeaturedDate[0])) >= 3600) {
            loadFeaturedStickers(false, true, false);
        }
    }

    public void checkFeaturedEmoji() {
        if (this.loadingFeaturedStickers[1]) {
            return;
        }
        if (!this.featuredStickersLoaded[1] || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadFeaturedDate[1])) >= 3600) {
            loadFeaturedStickers(true, true, false);
        }
    }

    public ArrayList<TLRPC$Document> getRecentStickers(int i) {
        ArrayList<TLRPC$Document> arrayList = this.recentStickers[i];
        if (i == 7) {
            return new ArrayList<>(this.recentStickers[i]);
        }
        return new ArrayList<>(arrayList.subList(0, Math.min(arrayList.size(), 20)));
    }

    public ArrayList<TLRPC$Document> getRecentStickersNoCopy(int i) {
        return this.recentStickers[i];
    }

    public boolean isStickerInFavorites(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return false;
        }
        for (int i = 0; i < this.recentStickers[2].size(); i++) {
            TLRPC$Document tLRPC$Document2 = this.recentStickers[2].get(i);
            if (tLRPC$Document2.id == tLRPC$Document.id && tLRPC$Document2.dc_id == tLRPC$Document.dc_id) {
                return true;
            }
        }
        return false;
    }

    public void clearRecentStickers() {
        getConnectionsManager().sendRequest(new TLRPC$TL_messages_clearRecentStickers(), new MediaDataController$$ExternalSyntheticLambda164(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecentStickers$18(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda84(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecentStickers$17(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda4(this));
            this.recentStickers[0].clear();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.FALSE, 0);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecentStickers$16() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE type = 3").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void addRecentSticker(int i, Object obj, TLRPC$Document tLRPC$Document, int i2, boolean z) {
        boolean z2;
        int i3;
        TLRPC$Document tLRPC$Document2;
        int i4 = i;
        Object obj2 = obj;
        TLRPC$Document tLRPC$Document3 = tLRPC$Document;
        boolean z3 = z;
        if (i4 == 3) {
            return;
        }
        if (MessageObject.isStickerDocument(tLRPC$Document) || MessageObject.isAnimatedStickerDocument(tLRPC$Document3, true)) {
            int i5 = 0;
            while (true) {
                if (i5 >= this.recentStickers[i4].size()) {
                    z2 = false;
                    break;
                }
                TLRPC$Document tLRPC$Document4 = this.recentStickers[i4].get(i5);
                if (tLRPC$Document4.id == tLRPC$Document3.id) {
                    this.recentStickers[i4].remove(i5);
                    if (!z3) {
                        this.recentStickers[i4].add(0, tLRPC$Document4);
                    }
                    z2 = true;
                } else {
                    i5++;
                }
            }
            if (!z2 && !z3) {
                this.recentStickers[i4].add(0, tLRPC$Document3);
            }
            if (i4 == 2) {
                if (z3) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, tLRPC$Document3, 4);
                } else {
                    boolean z4 = this.recentStickers[i4].size() > getMessagesController().maxFaveStickersCount;
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    int i6 = NotificationCenter.showBulletin;
                    Object[] objArr = new Object[3];
                    objArr[0] = 0;
                    objArr[1] = tLRPC$Document3;
                    objArr[2] = Integer.valueOf(z4 ? 6 : 5);
                    globalInstance.postNotificationName(i6, objArr);
                }
                TLRPC$TL_messages_faveSticker tLRPC$TL_messages_faveSticker = new TLRPC$TL_messages_faveSticker();
                TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
                tLRPC$TL_messages_faveSticker.id = tLRPC$TL_inputDocument;
                tLRPC$TL_inputDocument.id = tLRPC$Document3.id;
                tLRPC$TL_inputDocument.access_hash = tLRPC$Document3.access_hash;
                byte[] bArr = tLRPC$Document3.file_reference;
                tLRPC$TL_inputDocument.file_reference = bArr;
                if (bArr == null) {
                    tLRPC$TL_inputDocument.file_reference = new byte[0];
                }
                tLRPC$TL_messages_faveSticker.unfave = z3;
                getConnectionsManager().sendRequest(tLRPC$TL_messages_faveSticker, new MediaDataController$$ExternalSyntheticLambda190(this, obj2, tLRPC$TL_messages_faveSticker));
                i3 = getMessagesController().maxFaveStickersCount;
            } else {
                if (i4 == 0 && z3) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, tLRPC$Document3, 3);
                    TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker = new TLRPC$TL_messages_saveRecentSticker();
                    TLRPC$TL_inputDocument tLRPC$TL_inputDocument2 = new TLRPC$TL_inputDocument();
                    tLRPC$TL_messages_saveRecentSticker.id = tLRPC$TL_inputDocument2;
                    tLRPC$TL_inputDocument2.id = tLRPC$Document3.id;
                    tLRPC$TL_inputDocument2.access_hash = tLRPC$Document3.access_hash;
                    byte[] bArr2 = tLRPC$Document3.file_reference;
                    tLRPC$TL_inputDocument2.file_reference = bArr2;
                    if (bArr2 == null) {
                        tLRPC$TL_inputDocument2.file_reference = new byte[0];
                    }
                    tLRPC$TL_messages_saveRecentSticker.unsave = true;
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_saveRecentSticker, new MediaDataController$$ExternalSyntheticLambda191(this, obj2, tLRPC$TL_messages_saveRecentSticker));
                }
                i3 = getMessagesController().maxRecentStickersCount;
            }
            if (this.recentStickers[i4].size() > i3 || z3) {
                if (z3) {
                    tLRPC$Document2 = tLRPC$Document3;
                } else {
                    ArrayList<TLRPC$Document>[] arrayListArr = this.recentStickers;
                    tLRPC$Document2 = arrayListArr[i4].remove(arrayListArr[i4].size() - 1);
                }
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda30(this, i4, tLRPC$Document2));
            }
            if (!z3) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(tLRPC$Document3);
                processLoadedRecentDocuments(i, arrayList, false, i2, false);
            }
            if (i4 == 2 || (i4 == 0 && z3)) {
                getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.FALSE, Integer.valueOf(i));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addRecentSticker$20(Object obj, TLRPC$TL_messages_faveSticker tLRPC$TL_messages_faveSticker, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null || !FileRefController.isFileRefError(tLRPC$TL_error.text) || obj == null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda7(this));
            return;
        }
        getFileRefController().requestReference(obj, tLRPC$TL_messages_faveSticker);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addRecentSticker$19() {
        getMediaDataController().loadRecents(2, false, false, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addRecentSticker$21(Object obj, TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null && FileRefController.isFileRefError(tLRPC$TL_error.text) && obj != null) {
            getFileRefController().requestReference(obj, tLRPC$TL_messages_saveRecentSticker);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addRecentSticker$22(int i, TLRPC$Document tLRPC$Document) {
        int i2 = 5;
        if (i == 0) {
            i2 = 3;
        } else if (i == 1) {
            i2 = 4;
        } else if (i == 5) {
            i2 = 7;
        }
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + tLRPC$Document.id + "' AND type = " + i2).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public ArrayList<TLRPC$Document> getRecentGifs() {
        return new ArrayList<>(this.recentGifs);
    }

    public void removeRecentGif(TLRPC$Document tLRPC$Document) {
        int size = this.recentGifs.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            } else if (this.recentGifs.get(i).id == tLRPC$Document.id) {
                this.recentGifs.remove(i);
                break;
            } else {
                i++;
            }
        }
        TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif = new TLRPC$TL_messages_saveGif();
        TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
        tLRPC$TL_messages_saveGif.id = tLRPC$TL_inputDocument;
        tLRPC$TL_inputDocument.id = tLRPC$Document.id;
        tLRPC$TL_inputDocument.access_hash = tLRPC$Document.access_hash;
        byte[] bArr = tLRPC$Document.file_reference;
        tLRPC$TL_inputDocument.file_reference = bArr;
        if (bArr == null) {
            tLRPC$TL_inputDocument.file_reference = new byte[0];
        }
        tLRPC$TL_messages_saveGif.unsave = true;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_saveGif, new MediaDataController$$ExternalSyntheticLambda200(this, tLRPC$TL_messages_saveGif));
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda92(this, tLRPC$Document));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeRecentGif$23(TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null && FileRefController.isFileRefError(tLRPC$TL_error.text)) {
            getFileRefController().requestReference("gif", tLRPC$TL_messages_saveGif);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeRecentGif$24(TLRPC$Document tLRPC$Document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + tLRPC$Document.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean hasRecentGif(TLRPC$Document tLRPC$Document) {
        for (int i = 0; i < this.recentGifs.size(); i++) {
            TLRPC$Document tLRPC$Document2 = this.recentGifs.get(i);
            if (tLRPC$Document2.id == tLRPC$Document.id) {
                this.recentGifs.remove(i);
                this.recentGifs.add(0, tLRPC$Document2);
                return true;
            }
        }
        return false;
    }

    public void addRecentGif(TLRPC$Document tLRPC$Document, int i, boolean z) {
        boolean z2;
        if (tLRPC$Document != null) {
            int i2 = 0;
            while (true) {
                if (i2 >= this.recentGifs.size()) {
                    z2 = false;
                    break;
                }
                TLRPC$Document tLRPC$Document2 = this.recentGifs.get(i2);
                if (tLRPC$Document2.id == tLRPC$Document.id) {
                    this.recentGifs.remove(i2);
                    this.recentGifs.add(0, tLRPC$Document2);
                    z2 = true;
                    break;
                }
                i2++;
            }
            if (!z2) {
                this.recentGifs.add(0, tLRPC$Document);
            }
            if ((this.recentGifs.size() > getMessagesController().savedGifsLimitDefault && !UserConfig.getInstance(this.currentAccount).isPremium()) || this.recentGifs.size() > getMessagesController().savedGifsLimitPremium) {
                ArrayList<TLRPC$Document> arrayList = this.recentGifs;
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda93(this, arrayList.remove(arrayList.size() - 1)));
                if (z) {
                    AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda141(tLRPC$Document));
                }
            }
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(tLRPC$Document);
            processLoadedRecentDocuments(0, arrayList2, true, i, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addRecentGif$25(TLRPC$Document tLRPC$Document) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + tLRPC$Document.id + "' AND type = 2").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean isLoadingStickers(int i) {
        return this.loadingStickers[i];
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x003c, code lost:
        r0 = r12.groupStickerSets.get(r13.set.id);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void replaceStickerSet(org.telegram.tgnet.TLRPC$TL_messages_stickerSet r13) {
        /*
            r12 = this;
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r0 = r12.stickerSetsById
            org.telegram.tgnet.TLRPC$StickerSet r1 = r13.set
            long r1 = r1.id
            java.lang.Object r0 = r0.get(r1)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r0
            androidx.collection.LongSparseArray<java.lang.String> r1 = r12.diceEmojiStickerSetsById
            org.telegram.tgnet.TLRPC$StickerSet r2 = r13.set
            long r2 = r2.id
            java.lang.Object r1 = r1.get(r2)
            java.lang.String r1 = (java.lang.String) r1
            if (r1 == 0) goto L_0x002a
            java.util.HashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r2 = r12.diceStickerSetsByEmoji
            r2.put(r1, r13)
            long r2 = java.lang.System.currentTimeMillis()
            r4 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 / r4
            int r3 = (int) r2
            r12.putDiceStickersToCache(r1, r13, r3)
        L_0x002a:
            if (r0 != 0) goto L_0x0038
            j$.util.concurrent.ConcurrentHashMap<java.lang.String, org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r0 = r12.stickerSetsByName
            org.telegram.tgnet.TLRPC$StickerSet r1 = r13.set
            java.lang.String r1 = r1.short_name
            java.lang.Object r0 = r0.get(r1)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r0
        L_0x0038:
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x004c
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r0 = r12.groupStickerSets
            org.telegram.tgnet.TLRPC$StickerSet r3 = r13.set
            long r3 = r3.id
            java.lang.Object r0 = r0.get(r3)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r0
            if (r0 == 0) goto L_0x004c
            r3 = 1
            goto L_0x004d
        L_0x004c:
            r3 = 0
        L_0x004d:
            if (r0 != 0) goto L_0x0050
            return
        L_0x0050:
            org.telegram.tgnet.TLRPC$StickerSet r4 = r13.set
            java.lang.String r4 = r4.short_name
            java.lang.String r5 = "AnimatedEmojies"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x0072
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r13.documents
            r0.documents = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_stickerPack> r4 = r13.packs
            r0.packs = r4
            org.telegram.tgnet.TLRPC$StickerSet r4 = r13.set
            r0.set = r4
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda110 r4 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda110
            r4.<init>(r12, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            r8 = 1
            goto L_0x00b5
        L_0x0072:
            androidx.collection.LongSparseArray r4 = new androidx.collection.LongSparseArray
            r4.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r13.documents
            int r6 = r6.size()
            r7 = 0
        L_0x007e:
            if (r7 >= r6) goto L_0x0090
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r8 = r13.documents
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC$Document) r8
            long r9 = r8.id
            r4.put(r9, r8)
            int r7 = r7 + 1
            goto L_0x007e
        L_0x0090:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r0.documents
            int r6 = r6.size()
            r7 = 0
            r8 = 0
        L_0x0098:
            if (r7 >= r6) goto L_0x00b5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r0.documents
            java.lang.Object r9 = r9.get(r7)
            org.telegram.tgnet.TLRPC$Document r9 = (org.telegram.tgnet.TLRPC$Document) r9
            long r9 = r9.id
            java.lang.Object r9 = r4.get(r9)
            org.telegram.tgnet.TLRPC$Document r9 = (org.telegram.tgnet.TLRPC$Document) r9
            if (r9 == 0) goto L_0x00b2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r8 = r0.documents
            r8.set(r7, r9)
            r8 = 1
        L_0x00b2:
            int r7 = r7 + 1
            goto L_0x0098
        L_0x00b5:
            if (r8 == 0) goto L_0x00f9
            if (r3 == 0) goto L_0x00bd
            r12.putSetToCache(r0)
            goto L_0x00f9
        L_0x00bd:
            org.telegram.tgnet.TLRPC$StickerSet r0 = r13.set
            boolean r3 = r0.masks
            if (r3 == 0) goto L_0x00c5
            r7 = 1
            goto L_0x00cd
        L_0x00c5:
            boolean r0 = r0.emojis
            if (r0 == 0) goto L_0x00cc
            r1 = 5
            r7 = 5
            goto L_0x00cd
        L_0x00cc:
            r7 = 0
        L_0x00cd:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet>[] r0 = r12.stickerSets
            r8 = r0[r7]
            int[] r0 = r12.loadDate
            r9 = r0[r7]
            long[] r0 = r12.loadHash
            r10 = r0[r7]
            r6 = r12
            r6.putStickersToCache(r7, r8, r9, r10)
            org.telegram.tgnet.TLRPC$StickerSet r13 = r13.set
            java.lang.String r13 = r13.short_name
            boolean r13 = r5.equals(r13)
            if (r13 == 0) goto L_0x00f9
            r13 = 4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet>[] r0 = r12.stickerSets
            r3 = r0[r13]
            int[] r0 = r12.loadDate
            r4 = r0[r13]
            long[] r0 = r12.loadHash
            r5 = r0[r13]
            r2 = 4
            r1 = r12
            r1.putStickersToCache(r2, r3, r4, r5)
        L_0x00f9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.replaceStickerSet(org.telegram.tgnet.TLRPC$TL_messages_stickerSet):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$replaceStickerSet$27(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        LongSparseArray<TLRPC$Document> stickerByIds = getStickerByIds(4);
        for (int i = 0; i < tLRPC$TL_messages_stickerSet.documents.size(); i++) {
            TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i);
            stickerByIds.put(tLRPC$Document.id, tLRPC$Document);
        }
    }

    public TLRPC$TL_messages_stickerSet getStickerSetByName(String str) {
        return this.stickerSetsByName.get(str);
    }

    public TLRPC$TL_messages_stickerSet getStickerSetByEmojiOrName(String str) {
        return this.diceStickerSetsByEmoji.get(str);
    }

    public TLRPC$TL_messages_stickerSet getStickerSetById(long j) {
        return this.stickerSetsById.get(j);
    }

    public TLRPC$TL_messages_stickerSet getGroupStickerSetById(TLRPC$StickerSet tLRPC$StickerSet) {
        TLRPC$StickerSet tLRPC$StickerSet2;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSetsById.get(tLRPC$StickerSet.id);
        if (tLRPC$TL_messages_stickerSet == null) {
            tLRPC$TL_messages_stickerSet = this.groupStickerSets.get(tLRPC$StickerSet.id);
            if (tLRPC$TL_messages_stickerSet == null || (tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set) == null) {
                loadGroupStickerSet(tLRPC$StickerSet, true);
            } else if (tLRPC$StickerSet2.hash != tLRPC$StickerSet.hash) {
                loadGroupStickerSet(tLRPC$StickerSet, false);
            }
        }
        return tLRPC$TL_messages_stickerSet;
    }

    public void putGroupStickerSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.groupStickerSets.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
    }

    public TLRPC$TL_messages_stickerSet getStickerSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z) {
        return getStickerSet(tLRPC$InputStickerSet, z, (Runnable) null);
    }

    public TLRPC$TL_messages_stickerSet getStickerSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z, Runnable runnable) {
        String str;
        if (tLRPC$InputStickerSet == null) {
            return null;
        }
        if ((tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetID) && this.stickerSetsById.containsKey(tLRPC$InputStickerSet.id)) {
            return this.stickerSetsById.get(tLRPC$InputStickerSet.id);
        }
        if ((tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetShortName) && (str = tLRPC$InputStickerSet.short_name) != null && this.stickerSetsByName.containsKey(str.toLowerCase())) {
            return this.stickerSetsByName.get(tLRPC$InputStickerSet.short_name.toLowerCase());
        }
        if (z) {
            return null;
        }
        TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
        tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$InputStickerSet;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new MediaDataController$$ExternalSyntheticLambda192(this, runnable));
        return null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getStickerSet$29(Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda107(this, (TLRPC$TL_messages_stickerSet) tLObject));
        } else if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getStickerSet$28(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        TLRPC$StickerSet tLRPC$StickerSet;
        if (tLRPC$TL_messages_stickerSet != null && (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set) != null) {
            this.stickerSetsById.put(tLRPC$StickerSet.id, tLRPC$TL_messages_stickerSet);
            this.stickerSetsByName.put(tLRPC$TL_messages_stickerSet.set.short_name.toLowerCase(), tLRPC$TL_messages_stickerSet);
            getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tLRPC$TL_messages_stickerSet.set.id), tLRPC$TL_messages_stickerSet);
        }
    }

    private void loadGroupStickerSet(TLRPC$StickerSet tLRPC$StickerSet, boolean z) {
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda96(this, tLRPC$StickerSet));
            return;
        }
        TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
        TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
        tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
        tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
        tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new MediaDataController$$ExternalSyntheticLambda167(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadGroupStickerSet$31(TLRPC$StickerSet tLRPC$StickerSet) {
        TLRPC$StickerSet tLRPC$StickerSet2;
        NativeByteBuffer byteBufferValue;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLiteCursor queryFinalized = database.queryFinalized("SELECT document FROM web_recent_v3 WHERE id = 's_" + tLRPC$StickerSet.id + "'", new Object[0]);
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
            if (queryFinalized.next() && !queryFinalized.isNull(0) && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$TL_messages_stickerSet = TLRPC$messages_StickerSet.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tLRPC$TL_messages_stickerSet == null || (tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set) == null || tLRPC$StickerSet2.hash != tLRPC$StickerSet.hash) {
                loadGroupStickerSet(tLRPC$StickerSet, false);
            }
            if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.set != null) {
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda108(this, tLRPC$TL_messages_stickerSet));
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadGroupStickerSet$30(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.groupStickerSets.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tLRPC$TL_messages_stickerSet.set.id), tLRPC$TL_messages_stickerSet);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadGroupStickerSet$33(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda106(this, (TLRPC$TL_messages_stickerSet) tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadGroupStickerSet$32(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.groupStickerSets.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tLRPC$TL_messages_stickerSet.set.id), tLRPC$TL_messages_stickerSet);
    }

    private void putSetToCache(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda109(this, tLRPC$TL_messages_stickerSet));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putSetToCache$34(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindString(1, "s_" + tLRPC$TL_messages_stickerSet.set.id);
            executeFast.bindInteger(2, 6);
            executeFast.bindString(3, "");
            executeFast.bindString(4, "");
            executeFast.bindString(5, "");
            executeFast.bindInteger(6, 0);
            executeFast.bindInteger(7, 0);
            executeFast.bindInteger(8, 0);
            executeFast.bindInteger(9, 0);
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_messages_stickerSet.getObjectSize());
            tLRPC$TL_messages_stickerSet.serializeToStream(nativeByteBuffer);
            executeFast.bindByteBuffer(10, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public HashMap<String, ArrayList<TLRPC$Document>> getAllStickers() {
        return this.allStickers;
    }

    public HashMap<String, ArrayList<TLRPC$Document>> getAllStickersFeatured() {
        return this.allStickersFeatured;
    }

    public TLRPC$Document getEmojiAnimatedSticker(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        String replace = charSequence.toString().replace("️", "");
        ArrayList<TLRPC$TL_messages_stickerSet> stickerSets2 = getStickerSets(4);
        int size = stickerSets2.size();
        for (int i = 0; i < size; i++) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSets2.get(i);
            int size2 = tLRPC$TL_messages_stickerSet.packs.size();
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet.packs.get(i2);
                if (!tLRPC$TL_stickerPack.documents.isEmpty() && TextUtils.equals(tLRPC$TL_stickerPack.emoticon, replace)) {
                    return getStickerByIds(4).get(tLRPC$TL_stickerPack.documents.get(0).longValue());
                }
            }
        }
        return null;
    }

    public boolean canAddStickerToFavorites() {
        return !this.stickersLoaded[0] || this.stickerSets[0].size() >= 5 || !this.recentStickers[2].isEmpty();
    }

    public ArrayList<TLRPC$TL_messages_stickerSet> getStickerSets(int i) {
        if (i == 3) {
            return this.stickerSets[2];
        }
        return this.stickerSets[i];
    }

    public LongSparseArray<TLRPC$Document> getStickerByIds(int i) {
        return this.stickersByIds[i];
    }

    public ArrayList<TLRPC$StickerSetCovered> getFeaturedStickerSets() {
        return this.featuredStickerSets[0];
    }

    public ArrayList<TLRPC$StickerSetCovered> getFeaturedEmojiSets() {
        return this.featuredStickerSets[1];
    }

    public ArrayList<Long> getUnreadStickerSets() {
        return this.unreadStickerSets[0];
    }

    public ArrayList<Long> getUnreadEmojiSets() {
        return this.unreadStickerSets[1];
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean areAllTrendingStickerSetsUnread(boolean r8) {
        /*
            r7 = this;
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r0 = r7.featuredStickerSets
            r0 = r0[r8]
            int r0 = r0.size()
            r1 = 0
            r2 = 0
        L_0x000a:
            if (r2 >= r0) goto L_0x0043
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r3 = r7.featuredStickerSets
            r3 = r3[r8]
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$StickerSetCovered r3 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r3
            org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
            long r4 = r4.id
            boolean r4 = r7.isStickerPackInstalled((long) r4)
            if (r4 != 0) goto L_0x0040
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r3.covers
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x002d
            org.telegram.tgnet.TLRPC$Document r4 = r3.cover
            if (r4 != 0) goto L_0x002d
            goto L_0x0040
        L_0x002d:
            java.util.ArrayList<java.lang.Long>[] r4 = r7.unreadStickerSets
            r4 = r4[r8]
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.set
            long r5 = r3.id
            java.lang.Long r3 = java.lang.Long.valueOf(r5)
            boolean r3 = r4.contains(r3)
            if (r3 != 0) goto L_0x0040
            return r1
        L_0x0040:
            int r2 = r2 + 1
            goto L_0x000a
        L_0x0043:
            r8 = 1
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.areAllTrendingStickerSetsUnread(boolean):boolean");
    }

    public boolean isStickerPackInstalled(long j) {
        return isStickerPackInstalled(j, true);
    }

    public boolean isStickerPackInstalled(long j, boolean z) {
        return (this.installedStickerSetsById.indexOfKey(j) >= 0 || (z && this.installedForceStickerSetsById.contains(Long.valueOf(j)))) && (!z || !this.uninstalledForceStickerSetsById.contains(Long.valueOf(j)));
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isStickerPackUnread(boolean r2, long r3) {
        /*
            r1 = this;
            java.util.ArrayList<java.lang.Long>[] r0 = r1.unreadStickerSets
            r2 = r0[r2]
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            boolean r2 = r2.contains(r3)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.isStickerPackUnread(boolean, long):boolean");
    }

    public boolean isStickerPackInstalled(String str) {
        return this.stickerSetsByName.containsKey(str);
    }

    public String getEmojiForSticker(long j) {
        String str = this.stickersByEmoji.get(j);
        return str != null ? str : "";
    }

    public static boolean canShowAttachMenuBotForTarget(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, String str) {
        Iterator<TLRPC$AttachMenuPeerType> it = tLRPC$TL_attachMenuBot.peer_types.iterator();
        while (it.hasNext()) {
            TLRPC$AttachMenuPeerType next = it.next();
            if (((next instanceof TLRPC$TL_attachMenuPeerTypeSameBotPM) || (next instanceof TLRPC$TL_attachMenuPeerTypeBotPM)) && str.equals("bots")) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeBroadcast) && str.equals("channels")) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeChat) && str.equals("groups")) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypePM) && str.equals("users")) {
                return true;
            }
        }
        return false;
    }

    public static boolean canShowAttachMenuBot(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot, TLObject tLObject) {
        TLRPC$Chat tLRPC$Chat = null;
        TLRPC$User tLRPC$User = tLObject instanceof TLRPC$User ? (TLRPC$User) tLObject : null;
        if (tLObject instanceof TLRPC$Chat) {
            tLRPC$Chat = (TLRPC$Chat) tLObject;
        }
        Iterator<TLRPC$AttachMenuPeerType> it = tLRPC$TL_attachMenuBot.peer_types.iterator();
        while (it.hasNext()) {
            TLRPC$AttachMenuPeerType next = it.next();
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeSameBotPM) && tLRPC$User != null && tLRPC$User.bot && tLRPC$User.id == tLRPC$TL_attachMenuBot.bot_id) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeBotPM) && tLRPC$User != null && tLRPC$User.bot && tLRPC$User.id != tLRPC$TL_attachMenuBot.bot_id) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypePM) && tLRPC$User != null && !tLRPC$User.bot) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeChat) && tLRPC$Chat != null && !ChatObject.isChannelAndNotMegaGroup(tLRPC$Chat)) {
                return true;
            }
            if ((next instanceof TLRPC$TL_attachMenuPeerTypeBroadcast) && tLRPC$Chat != null && ChatObject.isChannelAndNotMegaGroup(tLRPC$Chat)) {
                return true;
            }
        }
        return false;
    }

    public static TLRPC$TL_attachMenuBotIcon getAnimatedAttachMenuBotIcon(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        Iterator<TLRPC$TL_attachMenuBotIcon> it = tLRPC$TL_attachMenuBot.icons.iterator();
        while (it.hasNext()) {
            TLRPC$TL_attachMenuBotIcon next = it.next();
            if (next.name.equals("android_animated")) {
                return next;
            }
        }
        return null;
    }

    public static TLRPC$TL_attachMenuBotIcon getStaticAttachMenuBotIcon(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        Iterator<TLRPC$TL_attachMenuBotIcon> it = tLRPC$TL_attachMenuBot.icons.iterator();
        while (it.hasNext()) {
            TLRPC$TL_attachMenuBotIcon next = it.next();
            if (next.name.equals("default_static")) {
                return next;
            }
        }
        return null;
    }

    public static TLRPC$TL_attachMenuBotIcon getPlaceholderStaticAttachMenuBotIcon(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        Iterator<TLRPC$TL_attachMenuBotIcon> it = tLRPC$TL_attachMenuBot.icons.iterator();
        while (it.hasNext()) {
            TLRPC$TL_attachMenuBotIcon next = it.next();
            if (next.name.equals("placeholder_static")) {
                return next;
            }
        }
        return null;
    }

    public static long calcDocumentsHash(ArrayList<TLRPC$Document> arrayList) {
        return calcDocumentsHash(arrayList, 200);
    }

    public static long calcDocumentsHash(ArrayList<TLRPC$Document> arrayList, int i) {
        long j = 0;
        if (arrayList == null) {
            return 0;
        }
        int min = Math.min(i, arrayList.size());
        for (int i2 = 0; i2 < min; i2++) {
            TLRPC$Document tLRPC$Document = arrayList.get(i2);
            if (tLRPC$Document != null) {
                j = calcHash(j, tLRPC$Document.id);
            }
        }
        return j;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX WARNING: type inference failed for: r10v9 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        if (r7.recentStickersLoaded[r8] != false) goto L_0x001f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000d, code lost:
        if (r7.recentGifsLoaded != false) goto L_0x001f;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadRecents(int r8, boolean r9, boolean r10, boolean r11) {
        /*
            r7 = this;
            r0 = 0
            r1 = 1
            if (r9 == 0) goto L_0x0010
            boolean r2 = r7.loadingRecentGifs
            if (r2 == 0) goto L_0x0009
            return
        L_0x0009:
            r7.loadingRecentGifs = r1
            boolean r2 = r7.recentGifsLoaded
            if (r2 == 0) goto L_0x0020
            goto L_0x001f
        L_0x0010:
            boolean[] r2 = r7.loadingRecentStickers
            boolean r3 = r2[r8]
            if (r3 == 0) goto L_0x0017
            return
        L_0x0017:
            r2[r8] = r1
            boolean[] r2 = r7.recentStickersLoaded
            boolean r2 = r2[r8]
            if (r2 == 0) goto L_0x0020
        L_0x001f:
            r10 = 0
        L_0x0020:
            if (r10 == 0) goto L_0x0034
            org.telegram.messenger.MessagesStorage r10 = r7.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r10 = r10.getStorageQueue()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda118 r11 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda118
            r11.<init>(r7, r9, r8)
            r10.postRunnable(r11)
            goto L_0x013c
        L_0x0034:
            int r10 = r7.currentAccount
            android.content.SharedPreferences r10 = org.telegram.messenger.MessagesController.getEmojiSettings(r10)
            r2 = 7
            r3 = 3
            if (r11 != 0) goto L_0x0097
            r4 = 0
            if (r9 == 0) goto L_0x0049
            java.lang.String r11 = "lastGifLoadTime"
            long r10 = r10.getLong(r11, r4)
            goto L_0x007d
        L_0x0049:
            if (r8 != 0) goto L_0x0052
            java.lang.String r11 = "lastStickersLoadTime"
            long r10 = r10.getLong(r11, r4)
            goto L_0x007d
        L_0x0052:
            if (r8 != r1) goto L_0x005b
            java.lang.String r11 = "lastStickersLoadTimeMask"
            long r10 = r10.getLong(r11, r4)
            goto L_0x007d
        L_0x005b:
            if (r8 != r3) goto L_0x0064
            java.lang.String r11 = "lastStickersLoadTimeGreet"
            long r10 = r10.getLong(r11, r4)
            goto L_0x007d
        L_0x0064:
            r11 = 5
            if (r8 != r11) goto L_0x006e
            java.lang.String r11 = "lastStickersLoadTimeEmojiPacks"
            long r10 = r10.getLong(r11, r4)
            goto L_0x007d
        L_0x006e:
            if (r8 != r2) goto L_0x0077
            java.lang.String r11 = "lastStickersLoadTimePremiumStickers"
            long r10 = r10.getLong(r11, r4)
            goto L_0x007d
        L_0x0077:
            java.lang.String r11 = "lastStickersLoadTimeFavs"
            long r10 = r10.getLong(r11, r4)
        L_0x007d:
            long r4 = java.lang.System.currentTimeMillis()
            long r4 = r4 - r10
            long r10 = java.lang.Math.abs(r4)
            r4 = 3600000(0x36ee80, double:1.7786363E-317)
            int r6 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x0097
            if (r9 == 0) goto L_0x0092
            r7.loadingRecentGifs = r0
            goto L_0x0096
        L_0x0092:
            boolean[] r9 = r7.loadingRecentStickers
            r9[r8] = r0
        L_0x0096:
            return
        L_0x0097:
            if (r9 == 0) goto L_0x00b4
            org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs r9 = new org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs
            r9.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r7.recentGifs
            long r10 = calcDocumentsHash(r10)
            r9.hash = r10
            org.telegram.tgnet.ConnectionsManager r10 = r7.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda170 r11 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda170
            r11.<init>(r7, r8)
            r10.sendRequest(r9, r11)
            goto L_0x013c
        L_0x00b4:
            r9 = 2
            if (r8 != r9) goto L_0x00c7
            org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers r9 = new org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers
            r9.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r10 = r7.recentStickers
            r10 = r10[r8]
            long r10 = calcDocumentsHash(r10)
            r9.hash = r10
            goto L_0x0130
        L_0x00c7:
            java.lang.String r9 = "⭐"
            if (r8 != r3) goto L_0x00f3
            org.telegram.tgnet.TLRPC$TL_messages_getStickers r10 = new org.telegram.tgnet.TLRPC$TL_messages_getStickers
            r10.<init>()
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r0 = "👋"
            r11.append(r0)
            java.lang.String r9 = org.telegram.messenger.Emoji.fixEmoji(r9)
            r11.append(r9)
            java.lang.String r9 = r11.toString()
            r10.emoticon = r9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r9 = r7.recentStickers
            r9 = r9[r8]
            long r0 = calcDocumentsHash(r9)
            r10.hash = r0
        L_0x00f1:
            r9 = r10
            goto L_0x0130
        L_0x00f3:
            if (r8 != r2) goto L_0x011c
            org.telegram.tgnet.TLRPC$TL_messages_getStickers r10 = new org.telegram.tgnet.TLRPC$TL_messages_getStickers
            r10.<init>()
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r0 = "📂"
            r11.append(r0)
            java.lang.String r9 = org.telegram.messenger.Emoji.fixEmoji(r9)
            r11.append(r9)
            java.lang.String r9 = r11.toString()
            r10.emoticon = r9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r9 = r7.recentStickers
            r9 = r9[r8]
            long r0 = calcDocumentsHash(r9)
            r10.hash = r0
            goto L_0x00f1
        L_0x011c:
            org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers r9 = new org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers
            r9.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r10 = r7.recentStickers
            r10 = r10[r8]
            long r10 = calcDocumentsHash(r10)
            r9.hash = r10
            if (r8 != r1) goto L_0x012e
            r0 = 1
        L_0x012e:
            r9.attached = r0
        L_0x0130:
            org.telegram.tgnet.ConnectionsManager r10 = r7.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda169 r11 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda169
            r11.<init>(r7, r8)
            r10.sendRequest(r9, r11)
        L_0x013c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadRecents(int, boolean, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRecents$36(boolean z, int i) {
        NativeByteBuffer byteBufferValue;
        int i2 = 7;
        if (z) {
            i2 = 2;
        } else if (i == 0) {
            i2 = 3;
        } else if (i == 1) {
            i2 = 4;
        } else if (i == 3) {
            i2 = 6;
        } else if (i != 5) {
            i2 = i == 7 ? 8 : 5;
        }
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            SQLiteCursor queryFinalized = database.queryFinalized("SELECT document FROM web_recent_v3 WHERE type = " + i2 + " ORDER BY date DESC", new Object[0]);
            ArrayList arrayList = new ArrayList();
            while (queryFinalized.next()) {
                if (!queryFinalized.isNull(0) && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                    TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    if (TLdeserialize != null) {
                        arrayList.add(TLdeserialize);
                    }
                    byteBufferValue.reuse();
                }
            }
            queryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda122(this, z, arrayList, i));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRecents$35(boolean z, ArrayList arrayList, int i) {
        if (z) {
            this.recentGifs = arrayList;
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
        } else {
            this.recentStickers[i] = arrayList;
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = true;
        }
        if (i == 3) {
            preloadNextGreetingsSticker();
        }
        getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(z), Integer.valueOf(i));
        loadRecents(i, z, false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRecents$37(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        processLoadedRecentDocuments(i, tLObject instanceof TLRPC$TL_messages_savedGifs ? ((TLRPC$TL_messages_savedGifs) tLObject).gifs : null, true, 0, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRecents$38(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList<TLRPC$Document> arrayList;
        if (i == 3 || i == 7) {
            if (tLObject instanceof TLRPC$TL_messages_stickers) {
                arrayList = ((TLRPC$TL_messages_stickers) tLObject).stickers;
                processLoadedRecentDocuments(i, arrayList, false, 0, true);
            }
        } else if (i == 2) {
            if (tLObject instanceof TLRPC$TL_messages_favedStickers) {
                arrayList = ((TLRPC$TL_messages_favedStickers) tLObject).stickers;
                processLoadedRecentDocuments(i, arrayList, false, 0, true);
            }
        } else if (tLObject instanceof TLRPC$TL_messages_recentStickers) {
            arrayList = ((TLRPC$TL_messages_recentStickers) tLObject).stickers;
            processLoadedRecentDocuments(i, arrayList, false, 0, true);
        }
        arrayList = null;
        processLoadedRecentDocuments(i, arrayList, false, 0, true);
    }

    private void preloadNextGreetingsSticker() {
        if (!this.recentStickers[3].isEmpty()) {
            ArrayList<TLRPC$Document>[] arrayListArr = this.recentStickers;
            this.greetingsSticker = arrayListArr[3].get(Utilities.random.nextInt(arrayListArr[3].size()));
            getFileLoader().loadFile(ImageLocation.getForDocument(this.greetingsSticker), this.greetingsSticker, (String) null, 0, 1);
        }
    }

    public TLRPC$Document getGreetingsSticker() {
        TLRPC$Document tLRPC$Document = this.greetingsSticker;
        preloadNextGreetingsSticker();
        return tLRPC$Document;
    }

    /* access modifiers changed from: protected */
    public void processLoadedRecentDocuments(int i, ArrayList<TLRPC$Document> arrayList, boolean z, int i2, boolean z2) {
        if (arrayList != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda120(this, z, i, arrayList, z2, i2));
        }
        if (i2 == 0) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda119(this, z, i, arrayList));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedRecentDocuments$39(boolean z, int i, ArrayList arrayList, boolean z2, int i2) {
        int i3;
        int i4 = i;
        ArrayList arrayList2 = arrayList;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            int i5 = 2;
            int i6 = 3;
            if (z) {
                i3 = getMessagesController().maxRecentGifsCount;
            } else {
                if (i4 != 3) {
                    if (i4 != 7) {
                        i3 = i4 == 2 ? getMessagesController().maxFaveStickersCount : getMessagesController().maxRecentStickersCount;
                    }
                }
                i3 = 200;
            }
            database.beginTransaction();
            SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int size = arrayList.size();
            int i7 = z ? 2 : i4 == 0 ? 3 : i4 == 1 ? 4 : i4 == 3 ? 6 : i4 == 5 ? 7 : i4 == 7 ? 8 : 5;
            if (z2) {
                database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + i7).stepThis().dispose();
            }
            int i8 = 0;
            while (true) {
                if (i8 >= size) {
                    break;
                } else if (i8 == i3) {
                    break;
                } else {
                    TLRPC$Document tLRPC$Document = (TLRPC$Document) arrayList2.get(i8);
                    executeFast.requery();
                    executeFast.bindString(1, "" + tLRPC$Document.id);
                    executeFast.bindInteger(i5, i7);
                    executeFast.bindString(i6, "");
                    executeFast.bindString(4, "");
                    executeFast.bindString(5, "");
                    executeFast.bindInteger(6, 0);
                    executeFast.bindInteger(7, 0);
                    executeFast.bindInteger(8, 0);
                    executeFast.bindInteger(9, i2 != 0 ? i2 : size - i8);
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Document.getObjectSize());
                    tLRPC$Document.serializeToStream(nativeByteBuffer);
                    executeFast.bindByteBuffer(10, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    i8++;
                    i5 = 2;
                    i6 = 3;
                }
            }
            executeFast.dispose();
            database.commitTransaction();
            if (arrayList.size() >= i3) {
                database.beginTransaction();
                while (i3 < arrayList.size()) {
                    database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((TLRPC$Document) arrayList2.get(i3)).id + "' AND type = " + i7).stepThis().dispose();
                    i3++;
                }
                database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedRecentDocuments$40(boolean z, int i, ArrayList arrayList) {
        SharedPreferences.Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
        if (z) {
            this.loadingRecentGifs = false;
            this.recentGifsLoaded = true;
            edit.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
        } else {
            this.loadingRecentStickers[i] = false;
            this.recentStickersLoaded[i] = true;
            if (i == 0) {
                edit.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
            } else if (i == 1) {
                edit.putLong("lastStickersLoadTimeMask", System.currentTimeMillis()).commit();
            } else if (i == 3) {
                edit.putLong("lastStickersLoadTimeGreet", System.currentTimeMillis()).commit();
            } else if (i == 5) {
                edit.putLong("lastStickersLoadTimeEmojiPacks", System.currentTimeMillis()).commit();
            } else if (i == 7) {
                edit.putLong("lastStickersLoadTimePremiumStickers", System.currentTimeMillis()).commit();
            } else {
                edit.putLong("lastStickersLoadTimeFavs", System.currentTimeMillis()).commit();
            }
        }
        if (arrayList != null) {
            if (z) {
                this.recentGifs = arrayList;
            } else {
                this.recentStickers[i] = arrayList;
            }
            if (i == 3) {
                preloadNextGreetingsSticker();
            }
            getNotificationCenter().postNotificationName(NotificationCenter.recentDocumentsDidLoad, Boolean.valueOf(z), Integer.valueOf(i));
        }
    }

    public void reorderStickers(int i, ArrayList<Long> arrayList, boolean z) {
        Collections.sort(this.stickerSets[i], new MediaDataController$$ExternalSyntheticLambda146(arrayList));
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i), Boolean.valueOf(z));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$reorderStickers$41(ArrayList arrayList, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2) {
        int indexOf = arrayList.indexOf(Long.valueOf(tLRPC$TL_messages_stickerSet.set.id));
        int indexOf2 = arrayList.indexOf(Long.valueOf(tLRPC$TL_messages_stickerSet2.set.id));
        if (indexOf > indexOf2) {
            return 1;
        }
        return indexOf < indexOf2 ? -1 : 0;
    }

    public void calcNewHash(int i) {
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
    }

    public void storeTempStickerSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.stickerSetsById.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
        this.stickerSetsByName.put(tLRPC$TL_messages_stickerSet.set.short_name, tLRPC$TL_messages_stickerSet);
    }

    public void addNewStickerSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        int i;
        if (this.stickerSetsById.indexOfKey(tLRPC$TL_messages_stickerSet.set.id) < 0 && !this.stickerSetsByName.containsKey(tLRPC$TL_messages_stickerSet.set.short_name)) {
            TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
            if (tLRPC$StickerSet.masks) {
                i = 1;
            } else {
                i = tLRPC$StickerSet.emojis ? 5 : 0;
            }
            this.stickerSets[i].add(0, tLRPC$TL_messages_stickerSet);
            this.stickerSetsById.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
            this.installedStickerSetsById.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
            this.stickerSetsByName.put(tLRPC$TL_messages_stickerSet.set.short_name, tLRPC$TL_messages_stickerSet);
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i2 = 0; i2 < tLRPC$TL_messages_stickerSet.documents.size(); i2++) {
                TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i2);
                longSparseArray.put(tLRPC$Document.id, tLRPC$Document);
            }
            for (int i3 = 0; i3 < tLRPC$TL_messages_stickerSet.packs.size(); i3++) {
                TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet.packs.get(i3);
                String replace = tLRPC$TL_stickerPack.emoticon.replace("️", "");
                tLRPC$TL_stickerPack.emoticon = replace;
                ArrayList arrayList = this.allStickers.get(replace);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.allStickers.put(tLRPC$TL_stickerPack.emoticon, arrayList);
                }
                for (int i4 = 0; i4 < tLRPC$TL_stickerPack.documents.size(); i4++) {
                    Long l = tLRPC$TL_stickerPack.documents.get(i4);
                    if (this.stickersByEmoji.indexOfKey(l.longValue()) < 0) {
                        this.stickersByEmoji.put(l.longValue(), tLRPC$TL_stickerPack.emoticon);
                    }
                    TLRPC$Document tLRPC$Document2 = (TLRPC$Document) longSparseArray.get(l.longValue());
                    if (tLRPC$Document2 != null) {
                        arrayList.add(tLRPC$Document2);
                    }
                }
            }
            this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i), Boolean.TRUE);
            loadStickers(i, false, true);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFeaturedStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFeaturedEmojiStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFeaturedStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFeaturedStickers} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadFeaturedStickers(boolean r5, boolean r6, boolean r7) {
        /*
            r4 = this;
            boolean[] r0 = r4.loadingFeaturedStickers
            boolean r1 = r0[r5]
            if (r1 == 0) goto L_0x0007
            return
        L_0x0007:
            r1 = 1
            r0[r5] = r1
            if (r6 == 0) goto L_0x001d
            org.telegram.messenger.MessagesStorage r6 = r4.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r6 = r6.getStorageQueue()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda116 r7 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda116
            r7.<init>(r4, r5)
            r6.postRunnable(r7)
            goto L_0x004b
        L_0x001d:
            r2 = 0
            if (r5 == 0) goto L_0x0030
            org.telegram.tgnet.TLRPC$TL_messages_getFeaturedEmojiStickers r6 = new org.telegram.tgnet.TLRPC$TL_messages_getFeaturedEmojiStickers
            r6.<init>()
            if (r7 == 0) goto L_0x0029
            goto L_0x002d
        L_0x0029:
            long[] r7 = r4.loadFeaturedHash
            r2 = r7[r1]
        L_0x002d:
            r6.hash = r2
            goto L_0x003f
        L_0x0030:
            org.telegram.tgnet.TLRPC$TL_messages_getFeaturedStickers r6 = new org.telegram.tgnet.TLRPC$TL_messages_getFeaturedStickers
            r6.<init>()
            if (r7 == 0) goto L_0x0038
            goto L_0x003d
        L_0x0038:
            long[] r7 = r4.loadFeaturedHash
            r0 = 0
            r2 = r7[r0]
        L_0x003d:
            r6.hash = r2
        L_0x003f:
            org.telegram.tgnet.ConnectionsManager r7 = r4.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda201 r0 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda201
            r0.<init>(r4, r5, r2)
            r7.sendRequest(r6, r0)
        L_0x004b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadFeaturedStickers(boolean, boolean, boolean):void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: boolean} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00ab A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadFeaturedStickers$42(boolean r15) {
        /*
            r14 = this;
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0 = 0
            r1 = 0
            r4 = 0
            org.telegram.messenger.MessagesStorage r2 = r14.getMessagesStorage()     // Catch:{ all -> 0x00a1 }
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ all -> 0x00a1 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a1 }
            r6.<init>()     // Catch:{ all -> 0x00a1 }
            java.lang.String r7 = "SELECT data, unread, date, hash, premium FROM stickers_featured WHERE emoji = "
            r6.append(r7)     // Catch:{ all -> 0x00a1 }
            r7 = 1
            if (r15 == 0) goto L_0x0020
            r8 = 1
            goto L_0x0021
        L_0x0020:
            r8 = 0
        L_0x0021:
            r6.append(r8)     // Catch:{ all -> 0x00a1 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00a1 }
            java.lang.Object[] r8 = new java.lang.Object[r1]     // Catch:{ all -> 0x00a1 }
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r6, r8)     // Catch:{ all -> 0x00a1 }
            boolean r6 = r2.next()     // Catch:{ all -> 0x009c }
            if (r6 == 0) goto L_0x0093
            org.telegram.tgnet.NativeByteBuffer r6 = r2.byteBufferValue(r1)     // Catch:{ all -> 0x009c }
            if (r6 == 0) goto L_0x005b
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ all -> 0x009c }
            r8.<init>()     // Catch:{ all -> 0x009c }
            int r0 = r6.readInt32(r1)     // Catch:{ all -> 0x0059 }
            r9 = 0
        L_0x0044:
            if (r9 >= r0) goto L_0x0054
            int r10 = r6.readInt32(r1)     // Catch:{ all -> 0x0059 }
            org.telegram.tgnet.TLRPC$StickerSetCovered r10 = org.telegram.tgnet.TLRPC$StickerSetCovered.TLdeserialize(r6, r10, r1)     // Catch:{ all -> 0x0059 }
            r8.add(r10)     // Catch:{ all -> 0x0059 }
            int r9 = r9 + 1
            goto L_0x0044
        L_0x0054:
            r6.reuse()     // Catch:{ all -> 0x0059 }
            r0 = r8
            goto L_0x005b
        L_0x0059:
            r0 = move-exception
            goto L_0x009f
        L_0x005b:
            org.telegram.tgnet.NativeByteBuffer r6 = r2.byteBufferValue(r7)     // Catch:{ all -> 0x009c }
            if (r6 == 0) goto L_0x0079
            int r8 = r6.readInt32(r1)     // Catch:{ all -> 0x009c }
            r9 = 0
        L_0x0066:
            if (r9 >= r8) goto L_0x0076
            long r10 = r6.readInt64(r1)     // Catch:{ all -> 0x009c }
            java.lang.Long r10 = java.lang.Long.valueOf(r10)     // Catch:{ all -> 0x009c }
            r3.add(r10)     // Catch:{ all -> 0x009c }
            int r9 = r9 + 1
            goto L_0x0066
        L_0x0076:
            r6.reuse()     // Catch:{ all -> 0x009c }
        L_0x0079:
            r6 = 2
            int r6 = r2.intValue(r6)     // Catch:{ all -> 0x009c }
            long r4 = r14.calcFeaturedStickersHash(r15, r0)     // Catch:{ all -> 0x008f }
            r8 = 4
            int r8 = r2.intValue(r8)     // Catch:{ all -> 0x008f }
            if (r8 != r7) goto L_0x008a
            r1 = 1
        L_0x008a:
            r12 = r4
            r4 = r1
            r1 = r6
            r5 = r12
            goto L_0x0095
        L_0x008f:
            r7 = move-exception
            r8 = r0
            r0 = r7
            goto L_0x00a6
        L_0x0093:
            r5 = r4
            r4 = 0
        L_0x0095:
            r2.dispose()
            r2 = r0
            r7 = r5
            r6 = r1
            goto L_0x00b1
        L_0x009c:
            r6 = move-exception
            r8 = r0
            r0 = r6
        L_0x009f:
            r6 = 0
            goto L_0x00a6
        L_0x00a1:
            r2 = move-exception
            r8 = r0
            r6 = 0
            r0 = r2
            r2 = r8
        L_0x00a6:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x00b8 }
            if (r2 == 0) goto L_0x00ae
            r2.dispose()
        L_0x00ae:
            r2 = r8
            r7 = r4
            r4 = 0
        L_0x00b1:
            r5 = 1
            r0 = r14
            r1 = r15
            r0.processLoadedFeaturedStickers(r1, r2, r3, r4, r5, r6, r7)
            return
        L_0x00b8:
            r15 = move-exception
            if (r2 == 0) goto L_0x00be
            r2.dispose()
        L_0x00be:
            goto L_0x00c0
        L_0x00bf:
            throw r15
        L_0x00c0:
            goto L_0x00bf
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadFeaturedStickers$42(boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFeaturedStickers$44(boolean z, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda89(this, tLObject, z, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFeaturedStickers$43(TLObject tLObject, boolean z, long j) {
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TLRPC$TL_messages_featuredStickers) {
            TLRPC$TL_messages_featuredStickers tLRPC$TL_messages_featuredStickers = (TLRPC$TL_messages_featuredStickers) tLObject2;
            processLoadedFeaturedStickers(z, tLRPC$TL_messages_featuredStickers.sets, tLRPC$TL_messages_featuredStickers.unread, tLRPC$TL_messages_featuredStickers.premium, false, (int) (System.currentTimeMillis() / 1000), tLRPC$TL_messages_featuredStickers.hash);
            return;
        }
        processLoadedFeaturedStickers(z, (ArrayList<TLRPC$StickerSetCovered>) null, (ArrayList<Long>) null, false, false, (int) (System.currentTimeMillis() / 1000), j);
    }

    private void processLoadedFeaturedStickers(boolean z, ArrayList<TLRPC$StickerSetCovered> arrayList, ArrayList<Long> arrayList2, boolean z2, boolean z3, int i, long j) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda115(this, z));
        Utilities.stageQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda124(this, z3, arrayList, i, j, z, arrayList2, z2));
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$45(boolean r3) {
        /*
            r2 = this;
            boolean[] r0 = r2.loadingFeaturedStickers
            r1 = 0
            r0[r3] = r1
            boolean[] r0 = r2.featuredStickersLoaded
            r1 = 1
            r0[r3] = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$processLoadedFeaturedStickers$45(boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$49(boolean z, ArrayList arrayList, int i, long j, boolean z2, ArrayList arrayList2, boolean z3) {
        ArrayList arrayList3 = arrayList;
        int i2 = i;
        long j2 = 0;
        if ((z && (arrayList3 == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i2)) >= 3600)) || (!z && arrayList3 == null && j == 0)) {
            MediaDataController$$ExternalSyntheticLambda76 mediaDataController$$ExternalSyntheticLambda76 = new MediaDataController$$ExternalSyntheticLambda76(this, arrayList, j, z2);
            if (arrayList3 == null && !z) {
                j2 = 1000;
            }
            AndroidUtilities.runOnUIThread(mediaDataController$$ExternalSyntheticLambda76, j2);
            if (arrayList3 == null) {
                return;
            }
        }
        if (arrayList3 != null) {
            try {
                ArrayList arrayList4 = new ArrayList();
                LongSparseArray longSparseArray = new LongSparseArray();
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) arrayList.get(i3);
                    arrayList4.add(tLRPC$StickerSetCovered);
                    longSparseArray.put(tLRPC$StickerSetCovered.set.id, tLRPC$StickerSetCovered);
                }
                if (!z) {
                    putFeaturedStickersToCache(z2, arrayList4, arrayList2, i, j, z3);
                }
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda126(this, z2, arrayList2, longSparseArray, arrayList4, j, i, z3));
            } catch (Throwable th) {
                FileLog.e(th);
            }
            return;
        }
        boolean z4 = z2;
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda117(this, z4, i2));
        putFeaturedStickersToCache(z4, (ArrayList<TLRPC$StickerSetCovered>) null, (ArrayList<Long>) null, i, 0, z3);
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$46(java.util.ArrayList r3, long r4, boolean r6) {
        /*
            r2 = this;
            if (r3 == 0) goto L_0x000c
            r0 = 0
            int r3 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r3 == 0) goto L_0x000c
            long[] r3 = r2.loadFeaturedHash
            r3[r6] = r4
        L_0x000c:
            r3 = 0
            r2.loadFeaturedStickers(r6, r3, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$processLoadedFeaturedStickers$46(java.util.ArrayList, long, boolean):void");
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$47(boolean r2, java.util.ArrayList r3, androidx.collection.LongSparseArray r4, java.util.ArrayList r5, long r6, int r8, boolean r9) {
        /*
            r1 = this;
            java.util.ArrayList<java.lang.Long>[] r0 = r1.unreadStickerSets
            r0[r2] = r3
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r3 = r1.featuredStickerSetsById
            r3[r2] = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r3 = r1.featuredStickerSets
            r3[r2] = r5
            long[] r3 = r1.loadFeaturedHash
            r3[r2] = r6
            int[] r3 = r1.loadFeaturedDate
            r3[r2] = r8
            r1.loadFeaturedPremium = r9
            if (r2 == 0) goto L_0x001a
            r3 = 6
            goto L_0x001b
        L_0x001a:
            r3 = 3
        L_0x001b:
            r4 = 1
            r5 = 0
            r1.loadStickers(r3, r4, r5)
            org.telegram.messenger.NotificationCenter r3 = r1.getNotificationCenter()
            if (r2 == 0) goto L_0x0029
            int r2 = org.telegram.messenger.NotificationCenter.featuredEmojiDidLoad
            goto L_0x002b
        L_0x0029:
            int r2 = org.telegram.messenger.NotificationCenter.featuredStickersDidLoad
        L_0x002b:
            java.lang.Object[] r4 = new java.lang.Object[r5]
            r3.postNotificationName(r2, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$processLoadedFeaturedStickers$47(boolean, java.util.ArrayList, androidx.collection.LongSparseArray, java.util.ArrayList, long, int, boolean):void");
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$48(boolean r2, int r3) {
        /*
            r1 = this;
            int[] r0 = r1.loadFeaturedDate
            r0[r2] = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$processLoadedFeaturedStickers$48(boolean, int):void");
    }

    private void putFeaturedStickersToCache(boolean z, ArrayList<TLRPC$StickerSetCovered> arrayList, ArrayList<Long> arrayList2, int i, long j, boolean z2) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda77(this, arrayList != null ? new ArrayList(arrayList) : null, arrayList2, i, j, z2, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putFeaturedStickersToCache$50(ArrayList arrayList, ArrayList arrayList2, int i, long j, boolean z, boolean z2) {
        int i2 = 1;
        if (arrayList != null) {
            try {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?, ?, ?)");
                executeFast.requery();
                int i3 = 4;
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    i3 += ((TLRPC$StickerSetCovered) arrayList.get(i4)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer((arrayList2.size() * 8) + 4);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    ((TLRPC$StickerSetCovered) arrayList.get(i5)).serializeToStream(nativeByteBuffer);
                }
                nativeByteBuffer2.writeInt32(arrayList2.size());
                for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                    nativeByteBuffer2.writeInt64(((Long) arrayList2.get(i6)).longValue());
                }
                executeFast.bindInteger(1, 1);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindByteBuffer(3, nativeByteBuffer2);
                executeFast.bindInteger(4, i);
                executeFast.bindLong(5, j);
                executeFast.bindInteger(6, z ? 1 : 0);
                if (!z2) {
                    i2 = 0;
                }
                executeFast.bindInteger(7, i2);
                executeFast.step();
                nativeByteBuffer.reuse();
                nativeByteBuffer2.reuse();
                executeFast.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
            executeFast2.requery();
            executeFast2.bindInteger(1, i);
            executeFast2.step();
            executeFast2.dispose();
        }
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long calcFeaturedStickersHash(boolean r8, java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r9) {
        /*
            r7 = this;
            r0 = 0
            if (r9 == 0) goto L_0x003e
            boolean r2 = r9.isEmpty()
            if (r2 == 0) goto L_0x000b
            goto L_0x003e
        L_0x000b:
            r2 = 0
        L_0x000c:
            int r3 = r9.size()
            if (r2 >= r3) goto L_0x003e
            java.lang.Object r3 = r9.get(r2)
            org.telegram.tgnet.TLRPC$StickerSetCovered r3 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r3
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.set
            boolean r4 = r3.archived
            if (r4 == 0) goto L_0x001f
            goto L_0x003b
        L_0x001f:
            long r4 = r3.id
            long r0 = calcHash(r0, r4)
            java.util.ArrayList<java.lang.Long>[] r4 = r7.unreadStickerSets
            r4 = r4[r8]
            long r5 = r3.id
            java.lang.Long r3 = java.lang.Long.valueOf(r5)
            boolean r3 = r4.contains(r3)
            if (r3 == 0) goto L_0x003b
            r3 = 1
            long r0 = calcHash(r0, r3)
        L_0x003b:
            int r2 = r2 + 1
            goto L_0x000c
        L_0x003e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.calcFeaturedStickersHash(boolean, java.util.ArrayList):long");
    }

    /* JADX WARNING: type inference failed for: r10v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void markFeaturedStickersAsRead(boolean r10, boolean r11) {
        /*
            r9 = this;
            java.util.ArrayList<java.lang.Long>[] r0 = r9.unreadStickerSets
            r0 = r0[r10]
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x000b
            return
        L_0x000b:
            java.util.ArrayList<java.lang.Long>[] r0 = r9.unreadStickerSets
            r0 = r0[r10]
            r0.clear()
            long[] r0 = r9.loadFeaturedHash
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r1 = r9.featuredStickerSets
            r1 = r1[r10]
            long r1 = r9.calcFeaturedStickersHash(r10, r1)
            r0[r10] = r1
            org.telegram.messenger.NotificationCenter r0 = r9.getNotificationCenter()
            if (r10 == 0) goto L_0x0027
            int r1 = org.telegram.messenger.NotificationCenter.featuredEmojiDidLoad
            goto L_0x0029
        L_0x0027:
            int r1 = org.telegram.messenger.NotificationCenter.featuredStickersDidLoad
        L_0x0029:
            r2 = 0
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r0.postNotificationName(r1, r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r0 = r9.featuredStickerSets
            r3 = r0[r10]
            java.util.ArrayList<java.lang.Long>[] r0 = r9.unreadStickerSets
            r4 = r0[r10]
            int[] r0 = r9.loadFeaturedDate
            r5 = r0[r10]
            long[] r0 = r9.loadFeaturedHash
            r6 = r0[r10]
            boolean r8 = r9.loadFeaturedPremium
            r1 = r9
            r2 = r10
            r1.putFeaturedStickersToCache(r2, r3, r4, r5, r6, r8)
            if (r11 == 0) goto L_0x0056
            org.telegram.tgnet.TLRPC$TL_messages_readFeaturedStickers r10 = new org.telegram.tgnet.TLRPC$TL_messages_readFeaturedStickers
            r10.<init>()
            org.telegram.tgnet.ConnectionsManager r11 = r9.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda206 r0 = org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda206.INSTANCE
            r11.sendRequest(r10, r0)
        L_0x0056:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.markFeaturedStickersAsRead(boolean, boolean):void");
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getFeaturedStickersHashWithoutUnread(boolean r6) {
        /*
            r5 = this;
            r0 = 0
            r2 = 0
        L_0x0003:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r3 = r5.featuredStickerSets
            r3 = r3[r6]
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0027
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r3 = r5.featuredStickerSets
            r3 = r3[r6]
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$StickerSetCovered r3 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r3
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.set
            boolean r4 = r3.archived
            if (r4 == 0) goto L_0x001e
            goto L_0x0024
        L_0x001e:
            long r3 = r3.id
            long r0 = calcHash(r0, r3)
        L_0x0024:
            int r2 = r2 + 1
            goto L_0x0003
        L_0x0027:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.getFeaturedStickersHashWithoutUnread(boolean):long");
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void markFeaturedStickersByIdAsRead(boolean r4, long r5) {
        /*
            r3 = this;
            java.util.ArrayList<java.lang.Long>[] r0 = r3.unreadStickerSets
            r0 = r0[r4]
            java.lang.Long r1 = java.lang.Long.valueOf(r5)
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L_0x0049
            java.util.ArrayList<java.lang.Long>[] r0 = r3.readingStickerSets
            r0 = r0[r4]
            java.lang.Long r1 = java.lang.Long.valueOf(r5)
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L_0x001d
            goto L_0x0049
        L_0x001d:
            java.util.ArrayList<java.lang.Long>[] r0 = r3.readingStickerSets
            r0 = r0[r4]
            java.lang.Long r1 = java.lang.Long.valueOf(r5)
            r0.add(r1)
            org.telegram.tgnet.TLRPC$TL_messages_readFeaturedStickers r0 = new org.telegram.tgnet.TLRPC$TL_messages_readFeaturedStickers
            r0.<init>()
            java.util.ArrayList<java.lang.Long> r1 = r0.id
            java.lang.Long r2 = java.lang.Long.valueOf(r5)
            r1.add(r2)
            org.telegram.tgnet.ConnectionsManager r1 = r3.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda205 r2 = org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda205.INSTANCE
            r1.sendRequest(r0, r2)
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda121 r0 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda121
            r0.<init>(r3, r4, r5)
            r4 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r4)
        L_0x0049:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.markFeaturedStickersByIdAsRead(boolean, long):void");
    }

    /* JADX WARNING: type inference failed for: r9v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$markFeaturedStickersByIdAsRead$53(boolean r9, long r10) {
        /*
            r8 = this;
            java.util.ArrayList<java.lang.Long>[] r0 = r8.unreadStickerSets
            r0 = r0[r9]
            java.lang.Long r1 = java.lang.Long.valueOf(r10)
            r0.remove(r1)
            java.util.ArrayList<java.lang.Long>[] r0 = r8.readingStickerSets
            r0 = r0[r9]
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            r0.remove(r10)
            long[] r10 = r8.loadFeaturedHash
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r11 = r8.featuredStickerSets
            r11 = r11[r9]
            long r0 = r8.calcFeaturedStickersHash(r9, r11)
            r10[r9] = r0
            org.telegram.messenger.NotificationCenter r10 = r8.getNotificationCenter()
            if (r9 == 0) goto L_0x002b
            int r11 = org.telegram.messenger.NotificationCenter.featuredEmojiDidLoad
            goto L_0x002d
        L_0x002b:
            int r11 = org.telegram.messenger.NotificationCenter.featuredStickersDidLoad
        L_0x002d:
            r0 = 0
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r10.postNotificationName(r11, r0)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r10 = r8.featuredStickerSets
            r2 = r10[r9]
            java.util.ArrayList<java.lang.Long>[] r10 = r8.unreadStickerSets
            r3 = r10[r9]
            int[] r10 = r8.loadFeaturedDate
            r4 = r10[r9]
            long[] r10 = r8.loadFeaturedHash
            r5 = r10[r9]
            boolean r7 = r8.loadFeaturedPremium
            r0 = r8
            r1 = r9
            r0.putFeaturedStickersToCache(r1, r2, r3, r4, r5, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$markFeaturedStickersByIdAsRead$53(boolean, long):void");
    }

    public int getArchivedStickersCount(int i) {
        return this.archivedStickersCount[i];
    }

    public void verifyAnimatedStickerMessage(TLRPC$Message tLRPC$Message) {
        verifyAnimatedStickerMessage(tLRPC$Message, false);
    }

    public void verifyAnimatedStickerMessage(TLRPC$Message tLRPC$Message, boolean z) {
        if (tLRPC$Message != null) {
            TLRPC$Document document = MessageObject.getDocument(tLRPC$Message);
            String stickerSetName = MessageObject.getStickerSetName(document);
            if (!TextUtils.isEmpty(stickerSetName)) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSetsByName.get(stickerSetName);
                if (tLRPC$TL_messages_stickerSet != null) {
                    int size = tLRPC$TL_messages_stickerSet.documents.size();
                    for (int i = 0; i < size; i++) {
                        TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i);
                        if (tLRPC$Document.id == document.id && tLRPC$Document.dc_id == document.dc_id) {
                            tLRPC$Message.stickerVerified = 1;
                            return;
                        }
                    }
                } else if (z) {
                    AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda95(this, tLRPC$Message, stickerSetName));
                } else {
                    lambda$verifyAnimatedStickerMessage$54(tLRPC$Message, stickerSetName);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: verifyAnimatedStickerMessageInternal */
    public void lambda$verifyAnimatedStickerMessage$54(TLRPC$Message tLRPC$Message, String str) {
        ArrayList arrayList = this.verifyingMessages.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.verifyingMessages.put(str, arrayList);
        }
        arrayList.add(tLRPC$Message);
        TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
        tLRPC$TL_messages_getStickerSet.stickerset = MessageObject.getInputStickerSet(tLRPC$Message);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new MediaDataController$$ExternalSyntheticLambda193(this, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$verifyAnimatedStickerMessageInternal$56(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda64(this, str, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$verifyAnimatedStickerMessageInternal$55(String str, TLObject tLObject) {
        ArrayList arrayList = this.verifyingMessages.get(str);
        if (tLObject != null) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            storeTempStickerSet(tLRPC$TL_messages_stickerSet);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i);
                TLRPC$Document document = MessageObject.getDocument(tLRPC$Message);
                int size2 = tLRPC$TL_messages_stickerSet.documents.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= size2) {
                        break;
                    }
                    TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i2);
                    if (tLRPC$Document.id == document.id && tLRPC$Document.dc_id == document.dc_id) {
                        tLRPC$Message.stickerVerified = 1;
                        break;
                    }
                    i2++;
                }
                if (tLRPC$Message.stickerVerified == 0) {
                    tLRPC$Message.stickerVerified = 2;
                }
            }
        } else {
            int size3 = arrayList.size();
            for (int i3 = 0; i3 < size3; i3++) {
                ((TLRPC$Message) arrayList.get(i3)).stickerVerified = 2;
            }
        }
        getNotificationCenter().postNotificationName(NotificationCenter.didVerifyMessagesStickers, arrayList);
        getMessagesStorage().updateMessageVerifyFlags(arrayList);
    }

    public void loadArchivedStickersCount(int i, boolean z) {
        boolean z2 = true;
        if (z) {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            int i2 = notificationsSettings.getInt("archivedStickersCount" + i, -1);
            if (i2 == -1) {
                loadArchivedStickersCount(i, false);
                return;
            }
            this.archivedStickersCount[i] = i2;
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
            return;
        }
        TLRPC$TL_messages_getArchivedStickers tLRPC$TL_messages_getArchivedStickers = new TLRPC$TL_messages_getArchivedStickers();
        tLRPC$TL_messages_getArchivedStickers.limit = 0;
        tLRPC$TL_messages_getArchivedStickers.masks = i == 1;
        if (i != 5) {
            z2 = false;
        }
        tLRPC$TL_messages_getArchivedStickers.emojis = z2;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getArchivedStickers, new MediaDataController$$ExternalSyntheticLambda171(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadArchivedStickersCount$58(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda103(this, tLRPC$TL_error, tLObject, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadArchivedStickersCount$57(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_archivedStickers tLRPC$TL_messages_archivedStickers = (TLRPC$TL_messages_archivedStickers) tLObject;
            this.archivedStickersCount[i] = tLRPC$TL_messages_archivedStickers.count;
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putInt("archivedStickersCount" + i, tLRPC$TL_messages_archivedStickers.count).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
        }
    }

    private void processLoadStickersResponse(int i, TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers) {
        processLoadStickersResponse(i, tLRPC$TL_messages_allStickers, (Runnable) null);
    }

    private void processLoadStickersResponse(int i, TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers, Runnable runnable) {
        TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers2 = tLRPC$TL_messages_allStickers;
        ArrayList arrayList = new ArrayList();
        long j = 1000;
        if (tLRPC$TL_messages_allStickers2.sets.isEmpty()) {
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), tLRPC$TL_messages_allStickers2.hash, runnable);
            return;
        }
        LongSparseArray longSparseArray = new LongSparseArray();
        int i2 = 0;
        while (i2 < tLRPC$TL_messages_allStickers2.sets.size()) {
            TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_allStickers2.sets.get(i2);
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSetsById.get(tLRPC$StickerSet.id);
            if (tLRPC$TL_messages_stickerSet != null) {
                TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set;
                if (tLRPC$StickerSet2.hash == tLRPC$StickerSet.hash) {
                    tLRPC$StickerSet2.archived = tLRPC$StickerSet.archived;
                    tLRPC$StickerSet2.installed = tLRPC$StickerSet.installed;
                    tLRPC$StickerSet2.official = tLRPC$StickerSet.official;
                    longSparseArray.put(tLRPC$StickerSet2.id, tLRPC$TL_messages_stickerSet);
                    arrayList.add(tLRPC$TL_messages_stickerSet);
                    if (longSparseArray.size() == tLRPC$TL_messages_allStickers2.sets.size()) {
                        processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / j), tLRPC$TL_messages_allStickers2.hash);
                    }
                    i2++;
                    j = 1000;
                }
            }
            arrayList.add((Object) null);
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
            MediaDataController$$ExternalSyntheticLambda196 mediaDataController$$ExternalSyntheticLambda196 = r0;
            ConnectionsManager connectionsManager = getConnectionsManager();
            MediaDataController$$ExternalSyntheticLambda196 mediaDataController$$ExternalSyntheticLambda1962 = new MediaDataController$$ExternalSyntheticLambda196(this, arrayList, i2, longSparseArray, tLRPC$StickerSet, tLRPC$TL_messages_allStickers, i);
            connectionsManager.sendRequest(tLRPC$TL_messages_getStickerSet, mediaDataController$$ExternalSyntheticLambda196);
            i2++;
            j = 1000;
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadStickersResponse$60(ArrayList arrayList, int i, LongSparseArray longSparseArray, TLRPC$StickerSet tLRPC$StickerSet, TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda87(this, tLObject, arrayList, i, longSparseArray, tLRPC$StickerSet, tLRPC$TL_messages_allStickers, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadStickersResponse$59(TLObject tLObject, ArrayList arrayList, int i, LongSparseArray longSparseArray, TLRPC$StickerSet tLRPC$StickerSet, TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers, int i2) {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
        arrayList.set(i, tLRPC$TL_messages_stickerSet);
        longSparseArray.put(tLRPC$StickerSet.id, tLRPC$TL_messages_stickerSet);
        if (longSparseArray.size() == tLRPC$TL_messages_allStickers.sets.size()) {
            int i3 = 0;
            while (i3 < arrayList.size()) {
                if (arrayList.get(i3) == null) {
                    arrayList.remove(i3);
                    i3--;
                }
                i3++;
            }
            processLoadedStickers(i2, arrayList, false, (int) (System.currentTimeMillis() / 1000), tLRPC$TL_messages_allStickers.hash);
        }
    }

    public void checkPremiumGiftStickers() {
        if (getUserConfig().premiumGiftsStickerPack != null) {
            String str = getUserConfig().premiumGiftsStickerPack;
            TLRPC$TL_messages_stickerSet stickerSetByName = getStickerSetByName(str);
            if (stickerSetByName == null) {
                stickerSetByName = getStickerSetByEmojiOrName(str);
            }
            if (stickerSetByName == null) {
                getInstance(this.currentAccount).loadStickersByEmojiOrName(str, false, true);
            }
        }
        if (!this.loadingPremiumGiftStickers && System.currentTimeMillis() - getUserConfig().lastUpdatedPremiumGiftsStickerPack >= 86400000) {
            this.loadingPremiumGiftStickers = true;
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            tLRPC$TL_messages_getStickerSet.stickerset = new TLRPC$TL_inputStickerSetPremiumGifts();
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new MediaDataController$$ExternalSyntheticLambda163(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPremiumGiftStickers$62(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda85(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPremiumGiftStickers$61(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            getUserConfig().premiumGiftsStickerPack = tLRPC$TL_messages_stickerSet.set.short_name;
            getUserConfig().lastUpdatedPremiumGiftsStickerPack = System.currentTimeMillis();
            getUserConfig().saveConfig(false);
            processLoadedDiceStickers(getUserConfig().premiumGiftsStickerPack, false, tLRPC$TL_messages_stickerSet, false, (int) (System.currentTimeMillis() / 1000));
            getNotificationCenter().postNotificationName(NotificationCenter.didUpdatePremiumGiftStickers, new Object[0]);
        }
    }

    public void checkGenericAnimations() {
        if (getUserConfig().genericAnimationsStickerPack != null) {
            String str = getUserConfig().genericAnimationsStickerPack;
            TLRPC$TL_messages_stickerSet stickerSetByName = getStickerSetByName(str);
            if (stickerSetByName == null) {
                stickerSetByName = getStickerSetByEmojiOrName(str);
            }
            if (stickerSetByName == null) {
                getInstance(this.currentAccount).loadStickersByEmojiOrName(str, false, true);
            }
        }
        if (!this.loadingGenericAnimations) {
            this.loadingGenericAnimations = true;
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            tLRPC$TL_messages_getStickerSet.stickerset = new TLRPC$TL_inputStickerSetEmojiGenericAnimations();
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new MediaDataController$$ExternalSyntheticLambda168(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkGenericAnimations$64(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda82(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkGenericAnimations$63(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            getUserConfig().genericAnimationsStickerPack = tLRPC$TL_messages_stickerSet.set.short_name;
            getUserConfig().lastUpdatedGenericAnimations = System.currentTimeMillis();
            getUserConfig().saveConfig(false);
            processLoadedDiceStickers(getUserConfig().genericAnimationsStickerPack, false, tLRPC$TL_messages_stickerSet, false, (int) (System.currentTimeMillis() / 1000));
            for (int i = 0; i < tLRPC$TL_messages_stickerSet.documents.size(); i++) {
                preloadImage(ImageLocation.getForDocument(tLRPC$TL_messages_stickerSet.documents.get(i)), (String) null);
            }
        }
    }

    public void loadStickersByEmojiOrName(String str, boolean z, boolean z2) {
        if (this.loadingDiceStickerSets.contains(str)) {
            return;
        }
        if (!z || this.diceStickerSetsByEmoji.get(str) == null) {
            this.loadingDiceStickerSets.add(str);
            if (z2) {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda66(this, str, z));
                return;
            }
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            if (ObjectsCompat$$ExternalSyntheticBackport0.m(getUserConfig().premiumGiftsStickerPack, str)) {
                tLRPC$TL_messages_getStickerSet.stickerset = new TLRPC$TL_inputStickerSetPremiumGifts();
            } else if (z) {
                TLRPC$TL_inputStickerSetDice tLRPC$TL_inputStickerSetDice = new TLRPC$TL_inputStickerSetDice();
                tLRPC$TL_inputStickerSetDice.emoticon = str;
                tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetDice;
            } else {
                TLRPC$TL_inputStickerSetShortName tLRPC$TL_inputStickerSetShortName = new TLRPC$TL_inputStickerSetShortName();
                tLRPC$TL_inputStickerSetShortName.short_name = str;
                tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetShortName;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new MediaDataController$$ExternalSyntheticLambda195(this, str, z));
        }
    }

    /* JADX WARNING: type inference failed for: r7v0, types: [org.telegram.tgnet.TLRPC$TL_messages_stickerSet] */
    /* JADX WARNING: type inference failed for: r7v1 */
    /* JADX WARNING: type inference failed for: r7v2 */
    /* JADX WARNING: type inference failed for: r0v5, types: [org.telegram.tgnet.TLRPC$TL_messages_stickerSet] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0042 A[DONT_GENERATE] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadStickersByEmojiOrName$65(java.lang.String r12, boolean r13) {
        /*
            r11 = this;
            r0 = 0
            r1 = 0
            org.telegram.messenger.MessagesStorage r2 = r11.getMessagesStorage()     // Catch:{ all -> 0x003b }
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ all -> 0x003b }
            java.lang.String r3 = "SELECT data, date FROM stickers_dice WHERE emoji = ?"
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]     // Catch:{ all -> 0x003b }
            r5[r1] = r12     // Catch:{ all -> 0x003b }
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r5)     // Catch:{ all -> 0x003b }
            boolean r3 = r2.next()     // Catch:{ all -> 0x0036 }
            if (r3 == 0) goto L_0x0030
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r1)     // Catch:{ all -> 0x0036 }
            if (r3 == 0) goto L_0x002c
            int r5 = r3.readInt32(r1)     // Catch:{ all -> 0x0036 }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = org.telegram.tgnet.TLRPC$messages_StickerSet.TLdeserialize(r3, r5, r1)     // Catch:{ all -> 0x0036 }
            r3.reuse()     // Catch:{ all -> 0x0036 }
        L_0x002c:
            int r1 = r2.intValue(r4)     // Catch:{ all -> 0x0036 }
        L_0x0030:
            r2.dispose()
            r7 = r0
            r9 = r1
            goto L_0x0047
        L_0x0036:
            r3 = move-exception
            r10 = r2
            r2 = r0
            r0 = r10
            goto L_0x003d
        L_0x003b:
            r3 = move-exception
            r2 = r0
        L_0x003d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ all -> 0x004f }
            if (r0 == 0) goto L_0x0045
            r0.dispose()
        L_0x0045:
            r7 = r2
            r9 = 0
        L_0x0047:
            r8 = 1
            r4 = r11
            r5 = r12
            r6 = r13
            r4.processLoadedDiceStickers(r5, r6, r7, r8, r9)
            return
        L_0x004f:
            r12 = move-exception
            if (r0 == 0) goto L_0x0055
            r0.dispose()
        L_0x0055:
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadStickersByEmojiOrName$65(java.lang.String, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickersByEmojiOrName$67(String str, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda104(this, tLRPC$TL_error, tLObject, str, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickersByEmojiOrName$66(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, boolean z) {
        if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
            processLoadedDiceStickers(str, z, (TLRPC$TL_messages_stickerSet) tLObject, false, (int) (System.currentTimeMillis() / 1000));
            return;
        }
        processLoadedDiceStickers(str, z, (TLRPC$TL_messages_stickerSet) null, false, (int) (System.currentTimeMillis() / 1000));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedDiceStickers$68(String str) {
        this.loadingDiceStickerSets.remove(str);
    }

    private void processLoadedDiceStickers(String str, boolean z, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z2, int i) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda59(this, str));
        Utilities.stageQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda127(this, z2, tLRPC$TL_messages_stickerSet, i, str, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedDiceStickers$71(boolean z, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, int i, String str, boolean z2) {
        long j = 1000;
        if ((z && (tLRPC$TL_messages_stickerSet == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 86400)) || (!z && tLRPC$TL_messages_stickerSet == null)) {
            MediaDataController$$ExternalSyntheticLambda67 mediaDataController$$ExternalSyntheticLambda67 = new MediaDataController$$ExternalSyntheticLambda67(this, str, z2);
            if (tLRPC$TL_messages_stickerSet != null || z) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(mediaDataController$$ExternalSyntheticLambda67, j);
            if (tLRPC$TL_messages_stickerSet == null) {
                return;
            }
        }
        if (tLRPC$TL_messages_stickerSet != null) {
            if (!z) {
                putDiceStickersToCache(str, tLRPC$TL_messages_stickerSet, i);
            }
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda65(this, str, tLRPC$TL_messages_stickerSet));
        } else if (!z) {
            putDiceStickersToCache(str, (TLRPC$TL_messages_stickerSet) null, i);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedDiceStickers$69(String str, boolean z) {
        loadStickersByEmojiOrName(str, z, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedDiceStickers$70(String str, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.diceStickerSetsByEmoji.put(str, tLRPC$TL_messages_stickerSet);
        this.diceEmojiStickerSetsById.put(tLRPC$TL_messages_stickerSet.set.id, str);
        getNotificationCenter().postNotificationName(NotificationCenter.diceStickersDidLoad, str);
    }

    private void putDiceStickersToCache(String str, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, int i) {
        if (!TextUtils.isEmpty(str)) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda111(this, tLRPC$TL_messages_stickerSet, str, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putDiceStickersToCache$72(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, String str, int i) {
        if (tLRPC$TL_messages_stickerSet != null) {
            try {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_dice VALUES(?, ?, ?)");
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_messages_stickerSet.getObjectSize());
                tLRPC$TL_messages_stickerSet.serializeToStream(nativeByteBuffer);
                executeFast.bindString(1, str);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, i);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_dice SET date = ?");
            executeFast2.requery();
            executeFast2.bindInteger(1, i);
            executeFast2.step();
            executeFast2.dispose();
        }
    }

    public void markSetInstalling(long j, boolean z) {
        this.uninstalledForceStickerSetsById.remove(Long.valueOf(j));
        if (z && !this.installedForceStickerSetsById.contains(Long.valueOf(j))) {
            this.installedForceStickerSetsById.add(Long.valueOf(j));
        }
        if (!z) {
            this.installedForceStickerSetsById.remove(Long.valueOf(j));
        }
    }

    public void markSetUninstalling(long j, boolean z) {
        this.installedForceStickerSetsById.remove(Long.valueOf(j));
        if (z && !this.uninstalledForceStickerSetsById.contains(Long.valueOf(j))) {
            this.uninstalledForceStickerSetsById.add(Long.valueOf(j));
        }
        if (!z) {
            this.uninstalledForceStickerSetsById.remove(Long.valueOf(j));
        }
    }

    public void loadStickers(int i, boolean z, boolean z2) {
        loadStickers(i, z, z2, false, (Utilities.Callback<ArrayList<TLRPC$TL_messages_stickerSet>>) null);
    }

    public void loadStickers(int i, boolean z, boolean z2, boolean z3) {
        loadStickers(i, z, z2, z3, (Utilities.Callback<ArrayList<TLRPC$TL_messages_stickerSet>>) null);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getAllStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v13, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadStickers(int r7, boolean r8, boolean r9, boolean r10, org.telegram.messenger.Utilities.Callback<java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet>> r11) {
        /*
            r6 = this;
            boolean[] r0 = r6.loadingStickers
            boolean r0 = r0[r7]
            r1 = 0
            if (r0 == 0) goto L_0x0019
            if (r10 == 0) goto L_0x0013
            java.lang.Runnable[] r8 = r6.scheduledLoadStickers
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda32 r10 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda32
            r10.<init>(r6, r7, r9, r11)
            r8[r7] = r10
            goto L_0x0018
        L_0x0013:
            if (r11 == 0) goto L_0x0018
            r11.run(r1)
        L_0x0018:
            return
        L_0x0019:
            r10 = 4
            r0 = 3
            r2 = 0
            r3 = 6
            r4 = 1
            if (r7 != r0) goto L_0x0038
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r5 = r6.featuredStickerSets
            r5 = r5[r2]
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0032
            org.telegram.messenger.MessagesController r5 = r6.getMessagesController()
            boolean r5 = r5.preloadFeaturedStickers
            if (r5 != 0) goto L_0x0057
        L_0x0032:
            if (r11 == 0) goto L_0x0037
            r11.run(r1)
        L_0x0037:
            return
        L_0x0038:
            if (r7 != r3) goto L_0x0052
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r5 = r6.featuredStickerSets
            r5 = r5[r4]
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x004c
            org.telegram.messenger.MessagesController r5 = r6.getMessagesController()
            boolean r5 = r5.preloadFeaturedStickers
            if (r5 != 0) goto L_0x0057
        L_0x004c:
            if (r11 == 0) goto L_0x0051
            r11.run(r1)
        L_0x0051:
            return
        L_0x0052:
            if (r7 == r10) goto L_0x0057
            r6.loadArchivedStickersCount(r7, r8)
        L_0x0057:
            boolean[] r1 = r6.loadingStickers
            r1[r7] = r4
            if (r8 == 0) goto L_0x006f
            org.telegram.messenger.MessagesStorage r8 = r6.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r8 = r8.getStorageQueue()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda28 r9 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda28
            r9.<init>(r6, r7, r11)
            r8.postRunnable(r9)
            goto L_0x010c
        L_0x006f:
            if (r7 == r0) goto L_0x00d7
            if (r7 != r3) goto L_0x0075
            goto L_0x00d7
        L_0x0075:
            if (r7 != r10) goto L_0x0091
            org.telegram.tgnet.TLRPC$TL_messages_getStickerSet r8 = new org.telegram.tgnet.TLRPC$TL_messages_getStickerSet
            r8.<init>()
            org.telegram.tgnet.TLRPC$TL_inputStickerSetAnimatedEmoji r9 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetAnimatedEmoji
            r9.<init>()
            r8.stickerset = r9
            org.telegram.tgnet.ConnectionsManager r9 = r6.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda174 r10 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda174
            r10.<init>(r6, r7, r11)
            r9.sendRequest(r8, r10)
            goto L_0x010c
        L_0x0091:
            r0 = 0
            if (r7 != 0) goto L_0x00a5
            org.telegram.tgnet.TLRPC$TL_messages_getAllStickers r8 = new org.telegram.tgnet.TLRPC$TL_messages_getAllStickers
            r8.<init>()
            if (r9 == 0) goto L_0x009d
            goto L_0x00a1
        L_0x009d:
            long[] r9 = r6.loadHash
            r0 = r9[r7]
        L_0x00a1:
            r8.hash = r0
        L_0x00a3:
            r4 = r0
            goto L_0x00c6
        L_0x00a5:
            r8 = 5
            if (r7 != r8) goto L_0x00b7
            org.telegram.tgnet.TLRPC$TL_messages_getEmojiStickers r8 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiStickers
            r8.<init>()
            if (r9 == 0) goto L_0x00b0
            goto L_0x00b4
        L_0x00b0:
            long[] r9 = r6.loadHash
            r0 = r9[r7]
        L_0x00b4:
            r8.hash = r0
            goto L_0x00a3
        L_0x00b7:
            org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers r8 = new org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers
            r8.<init>()
            if (r9 == 0) goto L_0x00bf
            goto L_0x00c3
        L_0x00bf:
            long[] r9 = r6.loadHash
            r0 = r9[r7]
        L_0x00c3:
            r8.hash = r0
            goto L_0x00a3
        L_0x00c6:
            org.telegram.tgnet.ConnectionsManager r9 = r6.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda175 r10 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda175
            r0 = r10
            r1 = r6
            r2 = r7
            r3 = r11
            r0.<init>(r1, r2, r3, r4)
            r9.sendRequest(r8, r10)
            goto L_0x010c
        L_0x00d7:
            if (r7 != r3) goto L_0x00da
            goto L_0x00db
        L_0x00da:
            r4 = 0
        L_0x00db:
            org.telegram.tgnet.TLRPC$TL_messages_allStickers r8 = new org.telegram.tgnet.TLRPC$TL_messages_allStickers
            r8.<init>()
            long[] r9 = r6.loadFeaturedHash
            r0 = r9[r4]
            r8.hash = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r9 = r6.featuredStickerSets
            r9 = r9[r4]
            int r9 = r9.size()
        L_0x00ee:
            if (r2 >= r9) goto L_0x0104
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSet> r10 = r8.sets
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered>[] r0 = r6.featuredStickerSets
            r0 = r0[r4]
            java.lang.Object r0 = r0.get(r2)
            org.telegram.tgnet.TLRPC$StickerSetCovered r0 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r0
            org.telegram.tgnet.TLRPC$StickerSet r0 = r0.set
            r10.add(r0)
            int r2 = r2 + 1
            goto L_0x00ee
        L_0x0104:
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda137 r9 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda137
            r9.<init>(r11)
            r6.processLoadStickersResponse(r7, r8, r9)
        L_0x010c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadStickers(int, boolean, boolean, boolean, org.telegram.messenger.Utilities$Callback):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickers$73(int i, boolean z, Utilities.Callback callback) {
        loadStickers(i, false, z, false, callback);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x005c, code lost:
        if (r1 == null) goto L_0x0061;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadStickers$75(int r10, org.telegram.messenger.Utilities.Callback r11) {
        /*
            r9 = this;
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = 0
            r3 = 0
            r1 = 0
            org.telegram.messenger.MessagesStorage r5 = r9.getMessagesStorage()     // Catch:{ all -> 0x0058 }
            org.telegram.SQLite.SQLiteDatabase r5 = r5.getDatabase()     // Catch:{ all -> 0x0058 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0058 }
            r6.<init>()     // Catch:{ all -> 0x0058 }
            java.lang.String r7 = "SELECT data, date, hash FROM stickers_v2 WHERE id = "
            r6.append(r7)     // Catch:{ all -> 0x0058 }
            int r7 = r10 + 1
            r6.append(r7)     // Catch:{ all -> 0x0058 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0058 }
            java.lang.Object[] r7 = new java.lang.Object[r0]     // Catch:{ all -> 0x0058 }
            org.telegram.SQLite.SQLiteCursor r1 = r5.queryFinalized(r6, r7)     // Catch:{ all -> 0x0058 }
            boolean r5 = r1.next()     // Catch:{ all -> 0x0058 }
            if (r5 == 0) goto L_0x005e
            org.telegram.tgnet.NativeByteBuffer r5 = r1.byteBufferValue(r0)     // Catch:{ all -> 0x0058 }
            if (r5 == 0) goto L_0x004e
            int r6 = r5.readInt32(r0)     // Catch:{ all -> 0x0058 }
            r7 = 0
        L_0x003b:
            if (r7 >= r6) goto L_0x004b
            int r8 = r5.readInt32(r0)     // Catch:{ all -> 0x0058 }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = org.telegram.tgnet.TLRPC$messages_StickerSet.TLdeserialize(r5, r8, r0)     // Catch:{ all -> 0x0058 }
            r2.add(r8)     // Catch:{ all -> 0x0058 }
            int r7 = r7 + 1
            goto L_0x003b
        L_0x004b:
            r5.reuse()     // Catch:{ all -> 0x0058 }
        L_0x004e:
            r5 = 1
            int r0 = r1.intValue(r5)     // Catch:{ all -> 0x0058 }
            long r3 = calcStickersHash(r2)     // Catch:{ all -> 0x0058 }
            goto L_0x005e
        L_0x0058:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x006f }
            if (r1 == 0) goto L_0x0061
        L_0x005e:
            r1.dispose()
        L_0x0061:
            r5 = r3
            r4 = r0
            r3 = 1
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda140 r7 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda140
            r7.<init>(r11, r2)
            r0 = r9
            r1 = r10
            r0.processLoadedStickers(r1, r2, r3, r4, r5, r7)
            return
        L_0x006f:
            r10 = move-exception
            if (r1 == 0) goto L_0x0075
            r1.dispose()
        L_0x0075:
            goto L_0x0077
        L_0x0076:
            throw r10
        L_0x0077:
            goto L_0x0076
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadStickers$75(int, org.telegram.messenger.Utilities$Callback):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadStickers$74(Utilities.Callback callback, ArrayList arrayList) {
        if (callback != null) {
            callback.run(arrayList);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadStickers$76(Utilities.Callback callback) {
        if (callback != null) {
            callback.run(null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickers$79(int i, Utilities.Callback callback, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        Utilities.Callback callback2 = callback;
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TLRPC$TL_messages_stickerSet) {
            ArrayList arrayList = new ArrayList();
            arrayList.add((TLRPC$TL_messages_stickerSet) tLObject2);
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), calcStickersHash(arrayList), new MediaDataController$$ExternalSyntheticLambda139(callback2));
            return;
        }
        processLoadedStickers(i, (ArrayList<TLRPC$TL_messages_stickerSet>) null, false, (int) (System.currentTimeMillis() / 1000), 0, new MediaDataController$$ExternalSyntheticLambda136(callback2));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadStickers$77(Utilities.Callback callback) {
        if (callback != null) {
            callback.run(null);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadStickers$78(Utilities.Callback callback) {
        if (callback != null) {
            callback.run(null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickers$83(int i, Utilities.Callback callback, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda86(this, tLObject, i, callback, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickers$82(TLObject tLObject, int i, Utilities.Callback callback, long j) {
        if (tLObject instanceof TLRPC$TL_messages_allStickers) {
            processLoadStickersResponse(i, (TLRPC$TL_messages_allStickers) tLObject, new MediaDataController$$ExternalSyntheticLambda138(callback));
            return;
        }
        processLoadedStickers(i, (ArrayList<TLRPC$TL_messages_stickerSet>) null, false, (int) (System.currentTimeMillis() / 1000), j, new MediaDataController$$ExternalSyntheticLambda135(callback));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadStickers$80(Utilities.Callback callback) {
        if (callback != null) {
            callback.run(null);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadStickers$81(Utilities.Callback callback) {
        if (callback != null) {
            callback.run(null);
        }
    }

    private void putStickersToCache(int i, ArrayList<TLRPC$TL_messages_stickerSet> arrayList, int i2, long j) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda71(this, arrayList != null ? new ArrayList(arrayList) : null, i, i2, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putStickersToCache$84(ArrayList arrayList, int i, int i2, long j) {
        if (arrayList != null) {
            try {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                executeFast.requery();
                int i3 = 4;
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    i3 += ((TLRPC$TL_messages_stickerSet) arrayList.get(i4)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i3);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    ((TLRPC$TL_messages_stickerSet) arrayList.get(i5)).serializeToStream(nativeByteBuffer);
                }
                executeFast.bindInteger(1, i + 1);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindInteger(3, i2);
                executeFast.bindLong(4, j);
                executeFast.step();
                nativeByteBuffer.reuse();
                executeFast.dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
            executeFast2.requery();
            executeFast2.bindLong(1, (long) i2);
            executeFast2.step();
            executeFast2.dispose();
        }
    }

    public String getStickerSetName(long j) {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSetsById.get(j);
        if (tLRPC$TL_messages_stickerSet != null) {
            return tLRPC$TL_messages_stickerSet.set.short_name;
        }
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered = this.featuredStickerSetsById[0].get(j);
        if (tLRPC$StickerSetCovered != null) {
            return tLRPC$StickerSetCovered.set.short_name;
        }
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = this.featuredStickerSetsById[1].get(j);
        if (tLRPC$StickerSetCovered2 != null) {
            return tLRPC$StickerSetCovered2.set.short_name;
        }
        return null;
    }

    public static long getStickerSetId(TLRPC$Document tLRPC$Document) {
        for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                TLRPC$InputStickerSet tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset;
                if (tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetID) {
                    return tLRPC$InputStickerSet.id;
                }
                return -1;
            }
        }
        return -1;
    }

    public static TLRPC$InputStickerSet getInputStickerSet(TLRPC$Document tLRPC$Document) {
        for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                TLRPC$InputStickerSet tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset;
                if (tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetEmpty) {
                    return null;
                }
                return tLRPC$InputStickerSet;
            }
        }
        return null;
    }

    private static long calcStickersHash(ArrayList<TLRPC$TL_messages_stickerSet> arrayList) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$StickerSet tLRPC$StickerSet = arrayList.get(i).set;
            if (!tLRPC$StickerSet.archived) {
                j = calcHash(j, (long) tLRPC$StickerSet.hash);
            }
        }
        return j;
    }

    private void processLoadedStickers(int i, ArrayList<TLRPC$TL_messages_stickerSet> arrayList, boolean z, int i2, long j) {
        processLoadedStickers(i, arrayList, z, i2, j, (Runnable) null);
    }

    private void processLoadedStickers(int i, ArrayList<TLRPC$TL_messages_stickerSet> arrayList, boolean z, int i2, long j, Runnable runnable) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda23(this, i));
        Utilities.stageQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda123(this, z, arrayList, i2, j, i, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedStickers$85(int i) {
        this.loadingStickers[i] = false;
        this.stickersLoaded[i] = true;
        Runnable[] runnableArr = this.scheduledLoadStickers;
        if (runnableArr[i] != null) {
            runnableArr[i].run();
            this.scheduledLoadStickers[i] = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedStickers$89(boolean z, ArrayList arrayList, int i, long j, int i2, Runnable runnable) {
        int i3;
        int i4;
        MediaDataController mediaDataController = this;
        ArrayList arrayList2 = arrayList;
        int i5 = i;
        long j2 = 1000;
        if ((z && (arrayList2 == null || BuildVars.DEBUG_PRIVATE_VERSION || Math.abs((System.currentTimeMillis() / 1000) - ((long) i5)) >= 3600)) || (!z && arrayList2 == null && j == 0)) {
            MediaDataController$$ExternalSyntheticLambda74 mediaDataController$$ExternalSyntheticLambda74 = new MediaDataController$$ExternalSyntheticLambda74(this, arrayList, j, i2);
            if (arrayList2 != null || z) {
                j2 = 0;
            }
            AndroidUtilities.runOnUIThread(mediaDataController$$ExternalSyntheticLambda74, j2);
            if (arrayList2 == null) {
                if (runnable != null) {
                    runnable.run();
                    return;
                }
                return;
            }
        }
        if (arrayList2 != null) {
            try {
                ArrayList arrayList3 = new ArrayList();
                LongSparseArray longSparseArray = new LongSparseArray();
                HashMap hashMap = new HashMap();
                LongSparseArray longSparseArray2 = new LongSparseArray();
                LongSparseArray longSparseArray3 = new LongSparseArray();
                HashMap hashMap2 = new HashMap();
                int i6 = 0;
                while (i6 < arrayList.size()) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) arrayList2.get(i6);
                    if (tLRPC$TL_messages_stickerSet != null) {
                        if (mediaDataController.removingStickerSetsUndos.indexOfKey(tLRPC$TL_messages_stickerSet.set.id) < 0) {
                            arrayList3.add(tLRPC$TL_messages_stickerSet);
                            longSparseArray.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
                            hashMap.put(tLRPC$TL_messages_stickerSet.set.short_name, tLRPC$TL_messages_stickerSet);
                            int i7 = 0;
                            while (i7 < tLRPC$TL_messages_stickerSet.documents.size()) {
                                TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i7);
                                if (tLRPC$Document != null) {
                                    if (!(tLRPC$Document instanceof TLRPC$TL_documentEmpty)) {
                                        i4 = i6;
                                        longSparseArray3.put(tLRPC$Document.id, tLRPC$Document);
                                        i7++;
                                        i6 = i4;
                                    }
                                }
                                i4 = i6;
                                i7++;
                                i6 = i4;
                            }
                            i3 = i6;
                            if (!tLRPC$TL_messages_stickerSet.set.archived) {
                                int i8 = 0;
                                while (i8 < tLRPC$TL_messages_stickerSet.packs.size()) {
                                    TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet.packs.get(i8);
                                    if (tLRPC$TL_stickerPack != null) {
                                        String str = tLRPC$TL_stickerPack.emoticon;
                                        if (str != null) {
                                            String replace = str.replace("️", "");
                                            tLRPC$TL_stickerPack.emoticon = replace;
                                            ArrayList arrayList4 = (ArrayList) hashMap2.get(replace);
                                            if (arrayList4 == null) {
                                                arrayList4 = new ArrayList();
                                                hashMap2.put(tLRPC$TL_stickerPack.emoticon, arrayList4);
                                            }
                                            int i9 = 0;
                                            while (i9 < tLRPC$TL_stickerPack.documents.size()) {
                                                Long l = tLRPC$TL_stickerPack.documents.get(i9);
                                                HashMap hashMap3 = hashMap2;
                                                if (longSparseArray2.indexOfKey(l.longValue()) < 0) {
                                                    longSparseArray2.put(l.longValue(), tLRPC$TL_stickerPack.emoticon);
                                                }
                                                TLRPC$Document tLRPC$Document2 = (TLRPC$Document) longSparseArray3.get(l.longValue());
                                                if (tLRPC$Document2 != null) {
                                                    arrayList4.add(tLRPC$Document2);
                                                }
                                                i9++;
                                                ArrayList arrayList5 = arrayList;
                                                hashMap2 = hashMap3;
                                            }
                                        }
                                    }
                                    i8++;
                                    ArrayList arrayList6 = arrayList;
                                    hashMap2 = hashMap2;
                                }
                            }
                            i6 = i3 + 1;
                            mediaDataController = this;
                            arrayList2 = arrayList;
                            hashMap2 = hashMap2;
                        }
                    }
                    i3 = i6;
                    i6 = i3 + 1;
                    mediaDataController = this;
                    arrayList2 = arrayList;
                    hashMap2 = hashMap2;
                }
                HashMap hashMap4 = hashMap2;
                if (!z) {
                    putStickersToCache(i2, arrayList3, i, j);
                }
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda26(this, i2, longSparseArray, hashMap, arrayList3, j, i, longSparseArray3, hashMap4, longSparseArray2, runnable));
            } catch (Throwable th) {
                FileLog.e(th);
                if (runnable != null) {
                    runnable.run();
                }
            }
        } else if (!z) {
            int i10 = i2;
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda24(this, i10, i5));
            putStickersToCache(i10, (ArrayList<TLRPC$TL_messages_stickerSet>) null, i, 0);
            if (runnable != null) {
                runnable.run();
            }
        } else {
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedStickers$86(ArrayList arrayList, long j, int i) {
        if (!(arrayList == null || j == 0)) {
            this.loadHash[i] = j;
        }
        loadStickers(i, false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedStickers$87(int i, LongSparseArray longSparseArray, HashMap hashMap, ArrayList arrayList, long j, int i2, LongSparseArray longSparseArray2, HashMap hashMap2, LongSparseArray longSparseArray3, Runnable runnable) {
        int i3 = i;
        LongSparseArray longSparseArray4 = longSparseArray;
        HashMap hashMap3 = hashMap2;
        for (int i4 = 0; i4 < this.stickerSets[i3].size(); i4++) {
            TLRPC$StickerSet tLRPC$StickerSet = this.stickerSets[i3].get(i4).set;
            this.stickerSetsById.remove(tLRPC$StickerSet.id);
            this.stickerSetsByName.remove(tLRPC$StickerSet.short_name);
            if (!(i3 == 3 || i3 == 6 || i3 == 4)) {
                this.installedStickerSetsById.remove(tLRPC$StickerSet.id);
            }
        }
        for (int i5 = 0; i5 < longSparseArray.size(); i5++) {
            this.stickerSetsById.put(longSparseArray.keyAt(i5), (TLRPC$TL_messages_stickerSet) longSparseArray.valueAt(i5));
            if (!(i3 == 3 || i3 == 6 || i3 == 4)) {
                this.installedStickerSetsById.put(longSparseArray.keyAt(i5), (TLRPC$TL_messages_stickerSet) longSparseArray.valueAt(i5));
            }
        }
        this.stickerSetsByName.putAll(hashMap);
        this.stickerSets[i3] = arrayList;
        this.loadHash[i3] = j;
        this.loadDate[i3] = i2;
        this.stickersByIds[i3] = longSparseArray2;
        if (i3 == 0) {
            this.allStickers = hashMap3;
            this.stickersByEmoji = longSparseArray3;
        } else if (i3 == 3) {
            this.allStickersFeatured = hashMap3;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i), Boolean.TRUE);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedStickers$88(int i, int i2) {
        this.loadDate[i] = i2;
    }

    public boolean cancelRemovingStickerSet(long j) {
        Runnable runnable = this.removingStickerSetsUndos.get(j);
        if (runnable == null) {
            return false;
        }
        runnable.run();
        return true;
    }

    public void preloadStickerSetThumb(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        ArrayList<TLRPC$Document> arrayList;
        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_messages_stickerSet.set.thumbs, 90);
        if (closestPhotoSizeWithSize != null && (arrayList = tLRPC$TL_messages_stickerSet.documents) != null && !arrayList.isEmpty()) {
            loadStickerSetThumbInternal(closestPhotoSizeWithSize, tLRPC$TL_messages_stickerSet, arrayList.get(0), tLRPC$TL_messages_stickerSet.set.thumb_version);
        }
    }

    public void preloadStickerSetThumb(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$StickerSetCovered.set.thumbs, 90);
        if (closestPhotoSizeWithSize != null) {
            TLRPC$Document tLRPC$Document = tLRPC$StickerSetCovered.cover;
            if (tLRPC$Document == null) {
                if (!tLRPC$StickerSetCovered.covers.isEmpty()) {
                    tLRPC$Document = tLRPC$StickerSetCovered.covers.get(0);
                } else {
                    return;
                }
            }
            loadStickerSetThumbInternal(closestPhotoSizeWithSize, tLRPC$StickerSetCovered, tLRPC$Document, tLRPC$StickerSetCovered.set.thumb_version);
        }
    }

    private void loadStickerSetThumbInternal(TLRPC$PhotoSize tLRPC$PhotoSize, Object obj, TLRPC$Document tLRPC$Document, int i) {
        ImageLocation forSticker = ImageLocation.getForSticker(tLRPC$PhotoSize, tLRPC$Document, i);
        if (forSticker != null) {
            getFileLoader().loadFile(forSticker, obj, forSticker.imageType == 1 ? "tgs" : "webp", 3, 1);
        }
    }

    public void toggleStickerSet(Context context, TLObject tLObject, int i, BaseFragment baseFragment, boolean z, boolean z2) {
        toggleStickerSet(context, tLObject, i, baseFragment, z, z2, (Runnable) null);
    }

    public void toggleStickerSet(Context context, TLObject tLObject, int i, BaseFragment baseFragment, boolean z, boolean z2, Runnable runnable) {
        TLRPC$StickerSet tLRPC$StickerSet;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        int i2;
        int i3;
        char c;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2;
        Context context2 = context;
        TLObject tLObject2 = tLObject;
        int i4 = i;
        BaseFragment baseFragment2 = baseFragment;
        if (tLObject2 instanceof TLRPC$TL_messages_stickerSet) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet3 = (TLRPC$TL_messages_stickerSet) tLObject2;
            tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet3;
            tLRPC$StickerSet = tLRPC$TL_messages_stickerSet3.set;
        } else if (tLObject2 instanceof TLRPC$StickerSetCovered) {
            TLRPC$StickerSet tLRPC$StickerSet2 = ((TLRPC$StickerSetCovered) tLObject2).set;
            if (i4 != 2) {
                tLRPC$TL_messages_stickerSet2 = this.stickerSetsById.get(tLRPC$StickerSet2.id);
                if (tLRPC$TL_messages_stickerSet2 == null) {
                    return;
                }
            } else {
                tLRPC$TL_messages_stickerSet2 = null;
            }
            tLRPC$StickerSet = tLRPC$StickerSet2;
            tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet2;
        } else {
            throw new IllegalArgumentException("Invalid type of the given stickerSetObject: " + tLObject.getClass());
        }
        if (tLRPC$StickerSet.masks) {
            i2 = 1;
        } else {
            i2 = tLRPC$StickerSet.emojis ? 5 : 0;
        }
        tLRPC$StickerSet.archived = i4 == 1;
        int i5 = 0;
        while (true) {
            if (i5 >= this.stickerSets[i2].size()) {
                i3 = 0;
                break;
            }
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet4 = this.stickerSets[i2].get(i5);
            if (tLRPC$TL_messages_stickerSet4.set.id == tLRPC$StickerSet.id) {
                this.stickerSets[i2].remove(i5);
                if (i4 == 2) {
                    this.stickerSets[i2].add(0, tLRPC$TL_messages_stickerSet4);
                } else {
                    this.stickerSetsById.remove(tLRPC$TL_messages_stickerSet4.set.id);
                    this.installedStickerSetsById.remove(tLRPC$TL_messages_stickerSet4.set.id);
                    this.stickerSetsByName.remove(tLRPC$TL_messages_stickerSet4.set.short_name);
                }
                i3 = i5;
            } else {
                i5++;
            }
        }
        this.loadHash[i2] = calcStickersHash(this.stickerSets[i2]);
        putStickersToCache(i2, this.stickerSets[i2], this.loadDate[i2], this.loadHash[i2]);
        if (i4 == 2) {
            if (!cancelRemovingStickerSet(tLRPC$StickerSet.id)) {
                toggleStickerSetInternal(context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i2, z2);
            }
            c = 0;
        } else if (!z2 || baseFragment2 == null) {
            c = 0;
            toggleStickerSetInternal(context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i2, false);
        } else {
            StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(context2, tLObject2, i4);
            boolean[] zArr = new boolean[1];
            markSetUninstalling(tLRPC$StickerSet.id, true);
            StickerSetBulletinLayout stickerSetBulletinLayout2 = stickerSetBulletinLayout;
            int i6 = i3;
            c = 0;
            Bulletin.UndoButton delayedAction = new Bulletin.UndoButton(context2, false).setUndoAction(new MediaDataController$$ExternalSyntheticLambda133(this, zArr, tLRPC$StickerSet, i2, i6, tLRPC$TL_messages_stickerSet, runnable)).setDelayedAction(new MediaDataController$$ExternalSyntheticLambda130(this, zArr, context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i2));
            stickerSetBulletinLayout2.setButton(delayedAction);
            LongSparseArray<Runnable> longSparseArray = this.removingStickerSetsUndos;
            long j = tLRPC$StickerSet.id;
            delayedAction.getClass();
            longSparseArray.put(j, new MediaDataController$$ExternalSyntheticLambda142(delayedAction));
            Bulletin.make(baseFragment2, (Bulletin.Layout) stickerSetBulletinLayout2, 2750).show();
        }
        NotificationCenter notificationCenter = getNotificationCenter();
        int i7 = NotificationCenter.stickersDidLoad;
        Object[] objArr = new Object[2];
        objArr[c] = Integer.valueOf(i2);
        objArr[1] = Boolean.TRUE;
        notificationCenter.postNotificationName(i7, objArr);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSet$90(boolean[] zArr, TLRPC$StickerSet tLRPC$StickerSet, int i, int i2, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, Runnable runnable) {
        if (!zArr[0]) {
            zArr[0] = true;
            markSetUninstalling(tLRPC$StickerSet.id, false);
            tLRPC$StickerSet.archived = false;
            this.stickerSets[i].add(i2, tLRPC$TL_messages_stickerSet);
            this.stickerSetsById.put(tLRPC$StickerSet.id, tLRPC$TL_messages_stickerSet);
            this.installedStickerSetsById.put(tLRPC$StickerSet.id, tLRPC$TL_messages_stickerSet);
            this.stickerSetsByName.put(tLRPC$StickerSet.short_name, tLRPC$TL_messages_stickerSet);
            this.removingStickerSetsUndos.remove(tLRPC$StickerSet.id);
            this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
            putStickersToCache(i, this.stickerSets[i], this.loadDate[i], this.loadHash[i]);
            if (runnable != null) {
                runnable.run();
            }
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i), Boolean.TRUE);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSet$91(boolean[] zArr, Context context, int i, BaseFragment baseFragment, boolean z, TLObject tLObject, TLRPC$StickerSet tLRPC$StickerSet, int i2) {
        if (!zArr[0]) {
            zArr[0] = true;
            toggleStickerSetInternal(context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i2, false);
        }
    }

    public void removeMultipleStickerSets(Context context, BaseFragment baseFragment, ArrayList<TLRPC$TL_messages_stickerSet> arrayList) {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        int i;
        ArrayList<TLRPC$TL_messages_stickerSet> arrayList2 = arrayList;
        if (arrayList2 != null && !arrayList.isEmpty() && (tLRPC$TL_messages_stickerSet = arrayList2.get(arrayList.size() - 1)) != null) {
            TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
            if (tLRPC$StickerSet.masks) {
                i = 1;
            } else {
                i = tLRPC$StickerSet.emojis ? 5 : 0;
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                arrayList2.get(i2).set.archived = false;
            }
            int[] iArr = new int[arrayList.size()];
            for (int i3 = 0; i3 < this.stickerSets[i].size(); i3++) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = this.stickerSets[i].get(i3);
                int i4 = 0;
                while (true) {
                    if (i4 >= arrayList.size()) {
                        break;
                    } else if (tLRPC$TL_messages_stickerSet2.set.id == arrayList2.get(i4).set.id) {
                        iArr[i4] = i3;
                        this.stickerSets[i].remove(i3);
                        this.stickerSetsById.remove(tLRPC$TL_messages_stickerSet2.set.id);
                        this.installedStickerSetsById.remove(tLRPC$TL_messages_stickerSet2.set.id);
                        this.stickerSetsByName.remove(tLRPC$TL_messages_stickerSet2.set.short_name);
                        break;
                    } else {
                        i4++;
                    }
                }
            }
            ArrayList<TLRPC$TL_messages_stickerSet>[] arrayListArr = this.stickerSets;
            ArrayList<TLRPC$TL_messages_stickerSet> arrayList3 = arrayListArr[i];
            int i5 = this.loadDate[i];
            long[] jArr = this.loadHash;
            long calcStickersHash = calcStickersHash(arrayListArr[i]);
            jArr[i] = calcStickersHash;
            putStickersToCache(i, arrayList3, i5, calcStickersHash);
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i), Boolean.TRUE);
            for (int i6 = 0; i6 < arrayList.size(); i6++) {
                markSetUninstalling(arrayList2.get(i6).set.id, true);
            }
            Context context2 = context;
            boolean[] zArr = new boolean[1];
            ArrayList<TLRPC$TL_messages_stickerSet> arrayList4 = arrayList;
            StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(context2, tLRPC$TL_messages_stickerSet, arrayList.size(), 0, (TLRPC$Document) null, baseFragment.getResourceProvider());
            Bulletin.UndoButton delayedAction = new Bulletin.UndoButton(context2, false).setUndoAction(new MediaDataController$$ExternalSyntheticLambda131(this, zArr, arrayList4, i, iArr)).setDelayedAction(new MediaDataController$$ExternalSyntheticLambda132(this, zArr, arrayList4, context, baseFragment, i));
            stickerSetBulletinLayout.setButton(delayedAction);
            for (int i7 = 0; i7 < arrayList.size(); i7++) {
                LongSparseArray<Runnable> longSparseArray = this.removingStickerSetsUndos;
                long j = arrayList2.get(i7).set.id;
                delayedAction.getClass();
                longSparseArray.put(j, new MediaDataController$$ExternalSyntheticLambda142(delayedAction));
            }
            Bulletin.make(baseFragment, (Bulletin.Layout) stickerSetBulletinLayout, 2750).show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeMultipleStickerSets$92(boolean[] zArr, ArrayList arrayList, int i, int[] iArr) {
        if (!zArr[0]) {
            zArr[0] = true;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                markSetUninstalling(((TLRPC$TL_messages_stickerSet) arrayList.get(i2)).set.id, false);
                ((TLRPC$TL_messages_stickerSet) arrayList.get(i2)).set.archived = false;
                this.stickerSets[i].add(iArr[i2], (TLRPC$TL_messages_stickerSet) arrayList.get(i2));
                this.stickerSetsById.put(((TLRPC$TL_messages_stickerSet) arrayList.get(i2)).set.id, (TLRPC$TL_messages_stickerSet) arrayList.get(i2));
                this.installedStickerSetsById.put(((TLRPC$TL_messages_stickerSet) arrayList.get(i2)).set.id, (TLRPC$TL_messages_stickerSet) arrayList.get(i2));
                this.stickerSetsByName.put(((TLRPC$TL_messages_stickerSet) arrayList.get(i2)).set.short_name, (TLRPC$TL_messages_stickerSet) arrayList.get(i2));
                this.removingStickerSetsUndos.remove(((TLRPC$TL_messages_stickerSet) arrayList.get(i2)).set.id);
            }
            ArrayList<TLRPC$TL_messages_stickerSet>[] arrayListArr = this.stickerSets;
            ArrayList<TLRPC$TL_messages_stickerSet> arrayList2 = arrayListArr[i];
            int i3 = this.loadDate[i];
            long[] jArr = this.loadHash;
            long calcStickersHash = calcStickersHash(arrayListArr[i]);
            jArr[i] = calcStickersHash;
            putStickersToCache(i, arrayList2, i3, calcStickersHash);
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i), Boolean.TRUE);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeMultipleStickerSets$93(boolean[] zArr, ArrayList arrayList, Context context, BaseFragment baseFragment, int i) {
        if (!zArr[0]) {
            zArr[0] = true;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                toggleStickerSetInternal(context, 0, baseFragment, true, (TLObject) arrayList.get(i2), ((TLRPC$TL_messages_stickerSet) arrayList.get(i2)).set, i, false);
            }
        }
    }

    private void toggleStickerSetInternal(Context context, int i, BaseFragment baseFragment, boolean z, TLObject tLObject, TLRPC$StickerSet tLRPC$StickerSet, int i2, boolean z2) {
        int i3 = i;
        TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$StickerSet;
        TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
        tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet2.access_hash;
        long j = tLRPC$StickerSet2.id;
        tLRPC$TL_inputStickerSetID.id = j;
        if (i3 != 0) {
            TLRPC$TL_messages_installStickerSet tLRPC$TL_messages_installStickerSet = new TLRPC$TL_messages_installStickerSet();
            tLRPC$TL_messages_installStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
            tLRPC$TL_messages_installStickerSet.archived = i3 == 1;
            markSetInstalling(tLRPC$StickerSet2.id, true);
            getConnectionsManager().sendRequest(tLRPC$TL_messages_installStickerSet, new MediaDataController$$ExternalSyntheticLambda199(this, tLRPC$StickerSet, baseFragment, z, i2, z2, context, tLObject));
            return;
        }
        markSetUninstalling(j, true);
        TLRPC$TL_messages_uninstallStickerSet tLRPC$TL_messages_uninstallStickerSet = new TLRPC$TL_messages_uninstallStickerSet();
        tLRPC$TL_messages_uninstallStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_uninstallStickerSet, new MediaDataController$$ExternalSyntheticLambda198(this, tLRPC$StickerSet2, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSetInternal$96(TLRPC$StickerSet tLRPC$StickerSet, BaseFragment baseFragment, boolean z, int i, boolean z2, Context context, TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda98(this, tLRPC$StickerSet, tLObject2, baseFragment, z, i, tLRPC$TL_error, z2, context, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSetInternal$95(TLRPC$StickerSet tLRPC$StickerSet, TLObject tLObject, BaseFragment baseFragment, boolean z, int i, TLRPC$TL_error tLRPC$TL_error, boolean z2, Context context, TLObject tLObject2) {
        this.removingStickerSetsUndos.remove(tLRPC$StickerSet.id);
        if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
            processStickerSetInstallResultArchive(baseFragment, z, i, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
        }
        loadStickers(i, false, false, true, new MediaDataController$$ExternalSyntheticLambda156(this, tLRPC$StickerSet));
        if (tLRPC$TL_error == null && z2 && baseFragment != null) {
            Bulletin.make(baseFragment, (Bulletin.Layout) new StickerSetBulletinLayout(context, tLObject2, 2), 1500).show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSetInternal$94(TLRPC$StickerSet tLRPC$StickerSet, ArrayList arrayList) {
        markSetInstalling(tLRPC$StickerSet.id, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSetInternal$99(TLRPC$StickerSet tLRPC$StickerSet, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda97(this, tLRPC$StickerSet, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSetInternal$98(TLRPC$StickerSet tLRPC$StickerSet, int i) {
        this.removingStickerSetsUndos.remove(tLRPC$StickerSet.id);
        loadStickers(i, false, true, false, new MediaDataController$$ExternalSyntheticLambda157(this, tLRPC$StickerSet));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSetInternal$97(TLRPC$StickerSet tLRPC$StickerSet, ArrayList arrayList) {
        markSetUninstalling(tLRPC$StickerSet.id, false);
    }

    public void toggleStickerSets(ArrayList<TLRPC$StickerSet> arrayList, int i, int i2, BaseFragment baseFragment, boolean z) {
        int i3 = i2;
        int size = arrayList.size();
        ArrayList<TLRPC$InputStickerSet> arrayList2 = new ArrayList<>(size);
        int i4 = 0;
        while (true) {
            boolean z2 = true;
            if (i4 >= size) {
                break;
            }
            TLRPC$StickerSet tLRPC$StickerSet = arrayList.get(i4);
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
            arrayList2.add(tLRPC$TL_inputStickerSetID);
            if (i3 != 0) {
                if (i3 != 1) {
                    z2 = false;
                }
                tLRPC$StickerSet.archived = z2;
            }
            int size2 = this.stickerSets[i].size();
            int i5 = 0;
            while (true) {
                if (i5 >= size2) {
                    break;
                }
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSets[i].get(i5);
                if (tLRPC$TL_messages_stickerSet.set.id == tLRPC$TL_inputStickerSetID.id) {
                    this.stickerSets[i].remove(i5);
                    if (i3 == 2) {
                        this.stickerSets[i].add(0, tLRPC$TL_messages_stickerSet);
                    } else {
                        this.stickerSetsById.remove(tLRPC$TL_messages_stickerSet.set.id);
                        this.installedStickerSetsById.remove(tLRPC$TL_messages_stickerSet.set.id);
                        this.stickerSetsByName.remove(tLRPC$TL_messages_stickerSet.set.short_name);
                    }
                } else {
                    i5++;
                }
            }
            i4++;
        }
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        putStickersToCache(i, this.stickerSets[i], this.loadDate[i], this.loadHash[i]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i), Boolean.TRUE);
        TLRPC$TL_messages_toggleStickerSets tLRPC$TL_messages_toggleStickerSets = new TLRPC$TL_messages_toggleStickerSets();
        tLRPC$TL_messages_toggleStickerSets.stickersets = arrayList2;
        if (i3 == 0) {
            tLRPC$TL_messages_toggleStickerSets.uninstall = true;
        } else if (i3 == 1) {
            tLRPC$TL_messages_toggleStickerSets.archive = true;
        } else if (i3 == 2) {
            tLRPC$TL_messages_toggleStickerSets.unarchive = true;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_toggleStickerSets, new MediaDataController$$ExternalSyntheticLambda177(this, i2, baseFragment, z, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSets$101(int i, BaseFragment baseFragment, boolean z, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda29(this, i, tLObject, baseFragment, z, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSets$100(int i, TLObject tLObject, BaseFragment baseFragment, boolean z, int i2) {
        if (i != 0) {
            if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
                processStickerSetInstallResultArchive(baseFragment, z, i2, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
            }
            loadStickers(i2, false, false, true);
            return;
        }
        loadStickers(i2, false, true);
    }

    public void processStickerSetInstallResultArchive(BaseFragment baseFragment, boolean z, int i, TLRPC$TL_messages_stickerSetInstallResultArchive tLRPC$TL_messages_stickerSetInstallResultArchive) {
        int size = tLRPC$TL_messages_stickerSetInstallResultArchive.sets.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.installedStickerSetsById.remove(tLRPC$TL_messages_stickerSetInstallResultArchive.sets.get(i2).set.id);
        }
        loadArchivedStickersCount(i, false);
        getNotificationCenter().postNotificationName(NotificationCenter.needAddArchivedStickers, tLRPC$TL_messages_stickerSetInstallResultArchive.sets);
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(new StickersArchiveAlert(baseFragment.getParentActivity(), z ? baseFragment : null, tLRPC$TL_messages_stickerSetInstallResultArchive.sets).create());
        }
    }

    private int getMask() {
        int i = 1;
        if (this.lastReturnedNum >= this.searchResultMessages.size() - 1) {
            boolean[] zArr = this.messagesSearchEndReached;
            if (zArr[0] && zArr[1]) {
                i = 0;
            }
        }
        return this.lastReturnedNum > 0 ? i | 2 : i;
    }

    public ArrayList<MessageObject> getFoundMessageObjects() {
        return this.searchResultMessages;
    }

    public void clearFoundMessageObjects() {
        this.searchResultMessages.clear();
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isMessageFound(int r2, boolean r3) {
        /*
            r1 = this;
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r0 = r1.searchResultMessagesMap
            r3 = r0[r3]
            int r2 = r3.indexOfKey(r2)
            if (r2 < 0) goto L_0x000c
            r2 = 1
            goto L_0x000d
        L_0x000c:
            r2 = 0
        L_0x000d:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.isMessageFound(int, boolean):boolean");
    }

    public void searchMessagesInChat(String str, long j, long j2, int i, int i2, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat) {
        searchMessagesInChat(str, j, j2, i, i2, i3, false, tLRPC$User, tLRPC$Chat, true);
    }

    public void jumpToSearchedMessage(int i, int i2) {
        if (i2 >= 0 && i2 < this.searchResultMessages.size()) {
            this.lastReturnedNum = i2;
            MessageObject messageObject = this.searchResultMessages.get(i2);
            NotificationCenter notificationCenter = getNotificationCenter();
            int i3 = NotificationCenter.chatSearchResultsAvailable;
            int[] iArr = this.messagesSearchCount;
            notificationCenter.postNotificationName(i3, Integer.valueOf(i), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), Boolean.TRUE);
        }
    }

    public void loadMoreSearchMessages() {
        if (!this.loadingMoreSearchMessages) {
            boolean[] zArr = this.messagesSearchEndReached;
            if (!zArr[0] || this.lastMergeDialogId != 0 || !zArr[1]) {
                int size = this.searchResultMessages.size();
                this.lastReturnedNum = this.searchResultMessages.size();
                searchMessagesInChat((String) null, this.lastDialogId, this.lastMergeDialogId, this.lastGuid, 1, this.lastReplyMessageId, false, this.lastSearchUser, this.lastSearchChat, false);
                this.lastReturnedNum = size;
                this.loadingMoreSearchMessages = true;
            }
        }
    }

    private void searchMessagesInChat(String str, long j, long j2, int i, int i2, int i3, boolean z, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean z2) {
        long j3;
        int i4;
        String str2;
        long j4;
        String str3;
        long j5;
        int i5;
        long j6 = j;
        long j7 = j2;
        int i6 = i2;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        boolean z3 = !z;
        if (this.reqId != 0) {
            getConnectionsManager().cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.mergeReqId != 0) {
            getConnectionsManager().cancelRequest(this.mergeReqId, true);
            this.mergeReqId = 0;
        }
        if (str != null) {
            if (z3) {
                boolean[] zArr = this.messagesSearchEndReached;
                zArr[1] = false;
                zArr[0] = false;
                int[] iArr = this.messagesSearchCount;
                iArr[1] = 0;
                iArr[0] = 0;
                this.searchResultMessages.clear();
                this.searchResultMessagesMap[0].clear();
                this.searchResultMessagesMap[1].clear();
                getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(i));
            }
            str2 = str;
            j3 = j6;
            i4 = 0;
        } else if (!this.searchResultMessages.isEmpty()) {
            if (i6 == 1) {
                int i7 = this.lastReturnedNum + 1;
                this.lastReturnedNum = i7;
                if (i7 < this.searchResultMessages.size()) {
                    MessageObject messageObject = this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i8 = NotificationCenter.chatSearchResultsAvailable;
                    int[] iArr2 = this.messagesSearchCount;
                    notificationCenter.postNotificationName(i8, Integer.valueOf(i), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr2[0] + iArr2[1]), Boolean.valueOf(z2));
                    return;
                }
                boolean[] zArr2 = this.messagesSearchEndReached;
                if (!zArr2[0] || j7 != 0 || !zArr2[1]) {
                    String str4 = this.lastSearchQuery;
                    ArrayList<MessageObject> arrayList = this.searchResultMessages;
                    MessageObject messageObject2 = arrayList.get(arrayList.size() - 1);
                    if (messageObject2.getDialogId() != j6 || this.messagesSearchEndReached[0]) {
                        i5 = messageObject2.getDialogId() == j7 ? messageObject2.getId() : 0;
                        this.messagesSearchEndReached[1] = false;
                        j5 = j7;
                    } else {
                        i5 = messageObject2.getId();
                        j5 = j6;
                    }
                    j3 = j5;
                    i4 = i5;
                    str2 = str4;
                    z3 = false;
                } else {
                    this.lastReturnedNum--;
                    return;
                }
            } else if (i6 == 2) {
                int i9 = this.lastReturnedNum - 1;
                this.lastReturnedNum = i9;
                if (i9 < 0) {
                    this.lastReturnedNum = 0;
                    return;
                }
                if (i9 >= this.searchResultMessages.size()) {
                    this.lastReturnedNum = this.searchResultMessages.size() - 1;
                }
                MessageObject messageObject3 = this.searchResultMessages.get(this.lastReturnedNum);
                NotificationCenter notificationCenter2 = getNotificationCenter();
                int i10 = NotificationCenter.chatSearchResultsAvailable;
                int[] iArr3 = this.messagesSearchCount;
                notificationCenter2.postNotificationName(i10, Integer.valueOf(i), Integer.valueOf(messageObject3.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject3.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr3[0] + iArr3[1]), Boolean.valueOf(z2));
                return;
            } else {
                return;
            }
        } else {
            return;
        }
        boolean[] zArr3 = this.messagesSearchEndReached;
        if (!zArr3[0] || zArr3[1]) {
            j4 = 0;
        } else {
            j4 = 0;
            if (j7 != 0) {
                j3 = j7;
            }
        }
        if (j3 != j6 || !z3) {
            str3 = str2;
        } else if (j7 != j4) {
            TLRPC$InputPeer inputPeer = getMessagesController().getInputPeer(j7);
            if (inputPeer != null) {
                TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
                tLRPC$TL_messages_search.peer = inputPeer;
                this.lastMergeDialogId = j7;
                tLRPC$TL_messages_search.limit = 1;
                tLRPC$TL_messages_search.q = str2;
                if (tLRPC$User2 != null) {
                    tLRPC$TL_messages_search.from_id = MessagesController.getInputPeer(tLRPC$User);
                    tLRPC$TL_messages_search.flags = 1 | tLRPC$TL_messages_search.flags;
                } else if (tLRPC$Chat2 != null) {
                    tLRPC$TL_messages_search.from_id = MessagesController.getInputPeer(tLRPC$Chat);
                    tLRPC$TL_messages_search.flags = 1 | tLRPC$TL_messages_search.flags;
                }
                tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterEmpty();
                ConnectionsManager connectionsManager = getConnectionsManager();
                MediaDataController$$ExternalSyntheticLambda187 mediaDataController$$ExternalSyntheticLambda187 = r0;
                MediaDataController$$ExternalSyntheticLambda187 mediaDataController$$ExternalSyntheticLambda1872 = new MediaDataController$$ExternalSyntheticLambda187(this, j2, tLRPC$TL_messages_search, j, i, i2, i3, tLRPC$User, tLRPC$Chat, z2);
                this.mergeReqId = connectionsManager.sendRequest(tLRPC$TL_messages_search, mediaDataController$$ExternalSyntheticLambda187, 2);
                return;
            }
            return;
        } else {
            str3 = str2;
            this.lastMergeDialogId = 0;
            zArr3[1] = true;
            this.messagesSearchCount[1] = 0;
        }
        TLRPC$TL_messages_search tLRPC$TL_messages_search2 = new TLRPC$TL_messages_search();
        TLRPC$InputPeer inputPeer2 = getMessagesController().getInputPeer(j3);
        tLRPC$TL_messages_search2.peer = inputPeer2;
        if (inputPeer2 != null) {
            this.lastGuid = i;
            this.lastDialogId = j6;
            this.lastSearchUser = tLRPC$User2;
            TLRPC$Chat tLRPC$Chat3 = tLRPC$Chat;
            this.lastSearchChat = tLRPC$Chat3;
            this.lastReplyMessageId = i3;
            tLRPC$TL_messages_search2.limit = 21;
            tLRPC$TL_messages_search2.q = str3 != null ? str3 : "";
            tLRPC$TL_messages_search2.offset_id = i4;
            if (tLRPC$User2 != null) {
                tLRPC$TL_messages_search2.from_id = MessagesController.getInputPeer(tLRPC$User);
                tLRPC$TL_messages_search2.flags |= 1;
            } else if (tLRPC$Chat3 != null) {
                tLRPC$TL_messages_search2.from_id = MessagesController.getInputPeer(tLRPC$Chat);
                tLRPC$TL_messages_search2.flags |= 1;
            }
            int i11 = this.lastReplyMessageId;
            if (i11 != 0) {
                tLRPC$TL_messages_search2.top_msg_id = i11;
                tLRPC$TL_messages_search2.flags |= 2;
            }
            tLRPC$TL_messages_search2.filter = new TLRPC$TL_inputMessagesFilterEmpty();
            int i12 = this.lastReqId + 1;
            this.lastReqId = i12;
            this.lastSearchQuery = str3;
            MediaDataController$$ExternalSyntheticLambda194 mediaDataController$$ExternalSyntheticLambda194 = r0;
            MediaDataController$$ExternalSyntheticLambda194 mediaDataController$$ExternalSyntheticLambda1942 = new MediaDataController$$ExternalSyntheticLambda194(this, str3, i12, z2, tLRPC$TL_messages_search2, j3, j, i, j2, i3, tLRPC$User, tLRPC$Chat);
            this.reqId = getConnectionsManager().sendRequest(tLRPC$TL_messages_search2, mediaDataController$$ExternalSyntheticLambda194, 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchMessagesInChat$103(long j, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j2, int i, int i2, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda49(this, j, tLObject, tLRPC$TL_messages_search, j2, i, i2, i3, tLRPC$User, tLRPC$Chat, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchMessagesInChat$102(long j, TLObject tLObject, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j2, int i, int i2, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean z) {
        TLRPC$TL_messages_search tLRPC$TL_messages_search2 = tLRPC$TL_messages_search;
        if (this.lastMergeDialogId == j) {
            this.mergeReqId = 0;
            if (tLObject != null) {
                TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                this.messagesSearchEndReached[1] = tLRPC$messages_Messages.messages.isEmpty();
                this.messagesSearchCount[1] = tLRPC$messages_Messages instanceof TLRPC$TL_messages_messagesSlice ? tLRPC$messages_Messages.count : tLRPC$messages_Messages.messages.size();
                searchMessagesInChat(tLRPC$TL_messages_search2.q, j2, j, i, i2, i3, true, tLRPC$User, tLRPC$Chat, z);
                return;
            }
            this.messagesSearchEndReached[1] = true;
            this.messagesSearchCount[1] = 0;
            searchMessagesInChat(tLRPC$TL_messages_search2.q, j2, j, i, i2, i3, true, tLRPC$User, tLRPC$Chat, z);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchMessagesInChat$105(String str, int i, boolean z, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j, long j2, int i2, long j3, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList arrayList = new ArrayList();
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            int min = Math.min(tLRPC$messages_Messages.messages.size(), 20);
            for (int i4 = 0; i4 < min; i4++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$messages_Messages.messages.get(i4), false, false);
                messageObject.setQuery(str);
                arrayList.add(messageObject);
            }
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda33(this, i, z, tLObject, tLRPC$TL_messages_search, j, j2, i2, arrayList, j3, i3, tLRPC$User, tLRPC$Chat));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchMessagesInChat$104(int i, boolean z, TLObject tLObject, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j, long j2, int i2, ArrayList arrayList, long j3, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat) {
        if (i == this.lastReqId) {
            this.reqId = 0;
            if (!z) {
                this.loadingMoreSearchMessages = false;
            }
            if (tLObject != null) {
                TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                int i4 = 0;
                while (i4 < tLRPC$messages_Messages.messages.size()) {
                    TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i4);
                    if ((tLRPC$Message instanceof TLRPC$TL_messageEmpty) || (tLRPC$Message.action instanceof TLRPC$TL_messageActionHistoryClear)) {
                        tLRPC$messages_Messages.messages.remove(i4);
                        i4--;
                    }
                    i4++;
                }
                getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
                getMessagesController().putUsers(tLRPC$messages_Messages.users, false);
                getMessagesController().putChats(tLRPC$messages_Messages.chats, false);
                if (tLRPC$TL_messages_search.offset_id == 0 && j == j2) {
                    this.lastReturnedNum = 0;
                    this.searchResultMessages.clear();
                    this.searchResultMessagesMap[0].clear();
                    this.searchResultMessagesMap[1].clear();
                    this.messagesSearchCount[0] = 0;
                    getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsLoading, Integer.valueOf(i2));
                }
                int min = Math.min(tLRPC$messages_Messages.messages.size(), 20);
                int i5 = 0;
                boolean z2 = false;
                while (i5 < min) {
                    TLRPC$Message tLRPC$Message2 = tLRPC$messages_Messages.messages.get(i5);
                    MessageObject messageObject = (MessageObject) arrayList.get(i5);
                    this.searchResultMessages.add(messageObject);
                    this.searchResultMessagesMap[j == j2 ? (char) 0 : 1].put(messageObject.getId(), messageObject);
                    i5++;
                    z2 = true;
                }
                this.messagesSearchEndReached[j == j2 ? (char) 0 : 1] = tLRPC$messages_Messages.messages.size() < 21;
                this.messagesSearchCount[j == j2 ? (char) 0 : 1] = ((tLRPC$messages_Messages instanceof TLRPC$TL_messages_messagesSlice) || (tLRPC$messages_Messages instanceof TLRPC$TL_messages_channelMessages)) ? tLRPC$messages_Messages.count : tLRPC$messages_Messages.messages.size();
                if (this.searchResultMessages.isEmpty()) {
                    getNotificationCenter().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(i2), 0, Integer.valueOf(getMask()), 0L, 0, 0, Boolean.valueOf(z));
                } else if (z2) {
                    if (this.lastReturnedNum >= this.searchResultMessages.size()) {
                        this.lastReturnedNum = this.searchResultMessages.size() - 1;
                    }
                    MessageObject messageObject2 = this.searchResultMessages.get(this.lastReturnedNum);
                    NotificationCenter notificationCenter = getNotificationCenter();
                    int i6 = NotificationCenter.chatSearchResultsAvailable;
                    int[] iArr = this.messagesSearchCount;
                    notificationCenter.postNotificationName(i6, Integer.valueOf(i2), Integer.valueOf(messageObject2.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject2.getDialogId()), Integer.valueOf(this.lastReturnedNum), Integer.valueOf(iArr[0] + iArr[1]), Boolean.valueOf(z));
                }
                if (j == j2) {
                    boolean[] zArr = this.messagesSearchEndReached;
                    if (zArr[0] && j3 != 0 && !zArr[1]) {
                        searchMessagesInChat(this.lastSearchQuery, j2, j3, i2, 0, i3, true, tLRPC$User, tLRPC$Chat, z);
                    }
                }
            }
        }
    }

    public String getLastSearchQuery() {
        return this.lastSearchQuery;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x010e  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x002a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadMedia(long r17, int r19, int r20, int r21, int r22, int r23, int r24, int r25) {
        /*
            r16 = this;
            r2 = r17
            r5 = r19
            r6 = r20
            r7 = r21
            r8 = r22
            r9 = r23
            r11 = r24
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r17)
            r1 = 1
            if (r0 == 0) goto L_0x0022
            long r12 = -r2
            r14 = r16
            int r0 = r14.currentAccount
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r12, r0)
            if (r0 == 0) goto L_0x0024
            r10 = 1
            goto L_0x0026
        L_0x0022:
            r14 = r16
        L_0x0024:
            r0 = 0
            r10 = 0
        L_0x0026:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0066
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "load media did "
            r0.append(r4)
            r0.append(r2)
            java.lang.String r4 = " count = "
            r0.append(r4)
            r0.append(r5)
            java.lang.String r4 = " max_id "
            r0.append(r4)
            r0.append(r6)
            java.lang.String r4 = " type = "
            r0.append(r4)
            r0.append(r8)
            java.lang.String r4 = " cache = "
            r0.append(r4)
            r0.append(r9)
            java.lang.String r4 = " classGuid = "
            r0.append(r4)
            r0.append(r11)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0066:
            if (r9 != 0) goto L_0x010e
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r17)
            if (r0 == 0) goto L_0x0070
            goto L_0x010e
        L_0x0070:
            org.telegram.tgnet.TLRPC$TL_messages_search r12 = new org.telegram.tgnet.TLRPC$TL_messages_search
            r12.<init>()
            r12.limit = r5
            if (r7 == 0) goto L_0x007f
            r12.offset_id = r7
            int r0 = -r5
            r12.add_offset = r0
            goto L_0x0081
        L_0x007f:
            r12.offset_id = r6
        L_0x0081:
            if (r8 != 0) goto L_0x008b
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo
            r0.<init>()
            r12.filter = r0
            goto L_0x00d6
        L_0x008b:
            r0 = 6
            if (r8 != r0) goto L_0x0096
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotos r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotos
            r0.<init>()
            r12.filter = r0
            goto L_0x00d6
        L_0x0096:
            r0 = 7
            if (r8 != r0) goto L_0x00a1
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterVideo r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterVideo
            r0.<init>()
            r12.filter = r0
            goto L_0x00d6
        L_0x00a1:
            if (r8 != r1) goto L_0x00ab
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument
            r0.<init>()
            r12.filter = r0
            goto L_0x00d6
        L_0x00ab:
            r0 = 2
            if (r8 != r0) goto L_0x00b6
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice
            r0.<init>()
            r12.filter = r0
            goto L_0x00d6
        L_0x00b6:
            r0 = 3
            if (r8 != r0) goto L_0x00c1
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl
            r0.<init>()
            r12.filter = r0
            goto L_0x00d6
        L_0x00c1:
            r0 = 4
            if (r8 != r0) goto L_0x00cc
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic
            r0.<init>()
            r12.filter = r0
            goto L_0x00d6
        L_0x00cc:
            r0 = 5
            if (r8 != r0) goto L_0x00d6
            org.telegram.tgnet.TLRPC$TL_inputMessagesFilterGif r0 = new org.telegram.tgnet.TLRPC$TL_inputMessagesFilterGif
            r0.<init>()
            r12.filter = r0
        L_0x00d6:
            java.lang.String r0 = ""
            r12.q = r0
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r0 = r0.getInputPeer((long) r2)
            r12.peer = r0
            if (r0 != 0) goto L_0x00e7
            return
        L_0x00e7:
            org.telegram.tgnet.ConnectionsManager r13 = r16.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda181 r15 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda181
            r0 = r15
            r1 = r16
            r2 = r17
            r4 = r21
            r5 = r19
            r6 = r20
            r7 = r22
            r8 = r24
            r9 = r10
            r10 = r25
            r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10)
            int r0 = r13.sendRequest(r12, r15)
            org.telegram.tgnet.ConnectionsManager r1 = r16.getConnectionsManager()
            r1.bindRequestToGuid(r0, r11)
            goto L_0x0124
        L_0x010e:
            r0 = r16
            r1 = r17
            r3 = r19
            r4 = r20
            r5 = r21
            r6 = r22
            r7 = r24
            r8 = r10
            r9 = r23
            r10 = r25
            r0.loadMediaDatabase(r1, r3, r4, r5, r6, r7, r8, r9, r10)
        L_0x0124:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadMedia(long, int, int, int, int, int, int, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMedia$106(long j, int i, int i2, int i3, int i4, int i5, boolean z, int i6, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            long j2 = j;
            getMessagesController().removeDeletedMessagesFromArray(j, tLRPC$messages_Messages.messages);
            boolean z2 = false;
            if (i == 0 ? tLRPC$messages_Messages.messages.size() == 0 : tLRPC$messages_Messages.messages.size() <= 1) {
                z2 = true;
            }
            processLoadedMedia(tLRPC$messages_Messages, j, i2, i3, i, i4, 0, i5, z, z2, i6);
        }
    }

    public void getMediaCounts(long j, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda39(this, j, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCounts$111(long j, int i) {
        long j2 = j;
        try {
            int[] iArr = {-1, -1, -1, -1, -1, -1, -1, -1};
            int[] iArr2 = {-1, -1, -1, -1, -1, -1, -1, -1};
            int[] iArr3 = {0, 0, 0, 0, 0, 0, 0, 0};
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT type, count, old FROM media_counts_v2 WHERE uid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            while (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(0);
                if (intValue >= 0 && intValue < 8) {
                    int intValue2 = queryFinalized.intValue(1);
                    iArr[intValue] = intValue2;
                    iArr2[intValue] = intValue2;
                    iArr3[intValue] = queryFinalized.intValue(2);
                }
            }
            queryFinalized.dispose();
            if (DialogObject.isEncryptedDialog(j)) {
                for (int i2 = 0; i2 < 8; i2++) {
                    if (iArr[i2] == -1) {
                        SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v4 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i2)}), new Object[0]);
                        if (queryFinalized2.next()) {
                            iArr[i2] = queryFinalized2.intValue(0);
                        } else {
                            iArr[i2] = 0;
                        }
                        queryFinalized2.dispose();
                        putMediaCountDatabase(j2, i2, iArr[i2]);
                    }
                }
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda54(this, j2, iArr));
                return;
            }
            TLRPC$TL_messages_getSearchCounters tLRPC$TL_messages_getSearchCounters = new TLRPC$TL_messages_getSearchCounters();
            tLRPC$TL_messages_getSearchCounters.peer = getMessagesController().getInputPeer(j2);
            int i3 = 0;
            boolean z = false;
            for (int i4 = 8; i3 < i4; i4 = 8) {
                if (tLRPC$TL_messages_getSearchCounters.peer == null) {
                    iArr[i3] = 0;
                } else if (iArr[i3] == -1 || iArr3[i3] == 1) {
                    if (i3 == 0) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterPhotoVideo());
                    } else if (i3 == 1) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterDocument());
                    } else if (i3 == 2) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterRoundVoice());
                    } else if (i3 == 3) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterUrl());
                    } else if (i3 == 4) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterMusic());
                    } else if (i3 == 6) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterPhotos());
                    } else if (i3 == 7) {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterVideo());
                    } else {
                        tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterGif());
                    }
                    if (iArr[i3] == -1) {
                        z = true;
                    } else if (iArr3[i3] == 1) {
                        iArr[i3] = -1;
                    }
                }
                i3++;
            }
            if (!tLRPC$TL_messages_getSearchCounters.filters.isEmpty()) {
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getSearchCounters, new MediaDataController$$ExternalSyntheticLambda202(this, iArr, j2)), i);
            }
            if (!z) {
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda52(this, j2, iArr2));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCounts$107(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCounts$109(int[] iArr, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int i;
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (iArr[i2] < 0) {
                iArr[i2] = 0;
            }
        }
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            int size = tLRPC$Vector.objects.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC$TL_messages_searchCounter tLRPC$TL_messages_searchCounter = (TLRPC$TL_messages_searchCounter) tLRPC$Vector.objects.get(i3);
                TLRPC$MessagesFilter tLRPC$MessagesFilter = tLRPC$TL_messages_searchCounter.filter;
                if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterPhotoVideo) {
                    i = 0;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterDocument) {
                    i = 1;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterRoundVoice) {
                    i = 2;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterUrl) {
                    i = 3;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterMusic) {
                    i = 4;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterGif) {
                    i = 5;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterPhotos) {
                    i = 6;
                } else if (tLRPC$MessagesFilter instanceof TLRPC$TL_inputMessagesFilterVideo) {
                    i = 7;
                }
                iArr[i] = tLRPC$TL_messages_searchCounter.count;
                putMediaCountDatabase(j, i, iArr[i]);
            }
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda53(this, j, iArr));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCounts$108(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCounts$110(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    public void getMediaCount(long j, int i, int i2, boolean z) {
        if (z || DialogObject.isEncryptedDialog(j)) {
            getMediaCountDatabase(j, i, i2);
            return;
        }
        TLRPC$TL_messages_getSearchCounters tLRPC$TL_messages_getSearchCounters = new TLRPC$TL_messages_getSearchCounters();
        if (i == 0) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterPhotoVideo());
        } else if (i == 1) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterDocument());
        } else if (i == 2) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterRoundVoice());
        } else if (i == 3) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterUrl());
        } else if (i == 4) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterMusic());
        } else if (i == 5) {
            tLRPC$TL_messages_getSearchCounters.filters.add(new TLRPC$TL_inputMessagesFilterGif());
        }
        TLRPC$InputPeer inputPeer = getMessagesController().getInputPeer(j);
        tLRPC$TL_messages_getSearchCounters.peer = inputPeer;
        if (inputPeer != null) {
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getSearchCounters, new MediaDataController$$ExternalSyntheticLambda180(this, j, i, i2)), i2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCount$112(long j, int i, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            if (!tLRPC$Vector.objects.isEmpty()) {
                processLoadedMediaCount(((TLRPC$TL_messages_searchCounter) tLRPC$Vector.objects.get(0)).count, j, i, i2, false, 0);
            }
        }
    }

    public static int getMediaType(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message == null) {
            return -1;
        }
        if (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) {
            return 0;
        }
        if (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument) {
            TLRPC$Document tLRPC$Document = MessageObject.getMedia(tLRPC$Message).document;
            if (tLRPC$Document == null) {
                return -1;
            }
            boolean z = false;
            boolean z2 = false;
            boolean z3 = false;
            boolean z4 = false;
            boolean z5 = false;
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                    z = tLRPC$DocumentAttribute.round_message;
                    z2 = !z;
                } else if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAnimated) {
                    z3 = true;
                } else if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                    z = tLRPC$DocumentAttribute.voice;
                    z5 = !z;
                } else if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                    z4 = true;
                }
            }
            if (z) {
                return 2;
            }
            if (z2 && !z3 && !z4) {
                return 0;
            }
            if (z4) {
                return -1;
            }
            if (z3) {
                return 5;
            }
            if (z5) {
                return 4;
            }
            return 1;
        }
        if (!tLRPC$Message.entities.isEmpty()) {
            for (int i2 = 0; i2 < tLRPC$Message.entities.size(); i2++) {
                TLRPC$MessageEntity tLRPC$MessageEntity = tLRPC$Message.entities.get(i2);
                if ((tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityUrl) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityTextUrl) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityEmail)) {
                    return 3;
                }
            }
        }
        return -1;
    }

    public static boolean canAddMessageToMedia(TLRPC$Message tLRPC$Message) {
        boolean z = tLRPC$Message instanceof TLRPC$TL_message_secret;
        if (z && (((MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) || MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isGifMessage(tLRPC$Message)) && MessageObject.getMedia(tLRPC$Message).ttl_seconds != 0 && MessageObject.getMedia(tLRPC$Message).ttl_seconds <= 60)) {
            return false;
        }
        if ((z || !(tLRPC$Message instanceof TLRPC$TL_message) || ((!(MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) && !(MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument)) || MessageObject.getMedia(tLRPC$Message).ttl_seconds == 0)) && getMediaType(tLRPC$Message) != -1) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void processLoadedMedia(TLRPC$messages_Messages tLRPC$messages_Messages, long j, int i, int i2, int i3, int i4, int i5, int i6, boolean z, boolean z2, int i7) {
        TLRPC$messages_Messages tLRPC$messages_Messages2 = tLRPC$messages_Messages;
        int i8 = i3;
        int i9 = i5;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("process load media did " + j + " count = " + i + " max_id=" + i2 + " min_id=" + i8 + " type = " + i4 + " cache = " + i9 + " classGuid = " + i6);
        } else {
            long j2 = j;
            int i10 = i;
            int i11 = i2;
            int i12 = i4;
            int i13 = i6;
        }
        if (i9 == 0 || (((!tLRPC$messages_Messages2.messages.isEmpty() || i8 != 0) && (tLRPC$messages_Messages2.messages.size() > 1 || i8 == 0)) || DialogObject.isEncryptedDialog(j))) {
            if (i9 == 0) {
                ImageLoader.saveMessagesThumbs(tLRPC$messages_Messages2.messages);
                getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages2.users, tLRPC$messages_Messages2.chats, true, true);
                putMediaDatabase(j, i4, tLRPC$messages_Messages2.messages, i2, i3, z2);
            }
            MediaDataController$$ExternalSyntheticLambda113 mediaDataController$$ExternalSyntheticLambda113 = r0;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            MediaDataController$$ExternalSyntheticLambda113 mediaDataController$$ExternalSyntheticLambda1132 = new MediaDataController$$ExternalSyntheticLambda113(this, tLRPC$messages_Messages, i5, j, i6, i4, z2, i3, i7);
            dispatchQueue.postRunnable(mediaDataController$$ExternalSyntheticLambda113);
        } else if (i9 != 2) {
            loadMedia(j, i, i2, i3, i4, 0, i6, i7);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedMedia$114(TLRPC$messages_Messages tLRPC$messages_Messages, int i, long j, int i2, int i3, boolean z, int i4, int i5) {
        TLRPC$messages_Messages tLRPC$messages_Messages2 = tLRPC$messages_Messages;
        LongSparseArray longSparseArray = new LongSparseArray();
        for (int i6 = 0; i6 < tLRPC$messages_Messages2.users.size(); i6++) {
            TLRPC$User tLRPC$User = tLRPC$messages_Messages2.users.get(i6);
            longSparseArray.put(tLRPC$User.id, tLRPC$User);
        }
        ArrayList arrayList = new ArrayList();
        for (int i7 = 0; i7 < tLRPC$messages_Messages2.messages.size(); i7++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$messages_Messages2.messages.get(i7), (LongSparseArray<TLRPC$User>) longSparseArray, true, false);
            messageObject.createStrippedThumb();
            arrayList.add(messageObject);
        }
        getFileLoader().checkMediaExistance(arrayList);
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda114(this, tLRPC$messages_Messages, i, j, arrayList, i2, i3, z, i4, i5));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedMedia$113(TLRPC$messages_Messages tLRPC$messages_Messages, int i, long j, ArrayList arrayList, int i2, int i3, boolean z, int i4, int i5) {
        TLRPC$messages_Messages tLRPC$messages_Messages2 = tLRPC$messages_Messages;
        int i6 = tLRPC$messages_Messages2.count;
        boolean z2 = true;
        getMessagesController().putUsers(tLRPC$messages_Messages2.users, i != 0);
        getMessagesController().putChats(tLRPC$messages_Messages2.chats, i != 0);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i7 = NotificationCenter.mediaDidLoad;
        Object[] objArr = new Object[8];
        objArr[0] = Long.valueOf(j);
        objArr[1] = Integer.valueOf(i6);
        objArr[2] = arrayList;
        objArr[3] = Integer.valueOf(i2);
        objArr[4] = Integer.valueOf(i3);
        objArr[5] = Boolean.valueOf(z);
        if (i4 == 0) {
            z2 = false;
        }
        objArr[6] = Boolean.valueOf(z2);
        objArr[7] = Integer.valueOf(i5);
        notificationCenter.postNotificationName(i7, objArr);
    }

    private void processLoadedMediaCount(int i, long j, int i2, int i3, boolean z, int i4) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda51(this, j, z, i, i2, i4, i3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedMediaCount$115(long j, boolean z, int i, int i2, int i3, int i4) {
        int i5 = i;
        int i6 = i2;
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(j);
        int i7 = 0;
        boolean z2 = z && (i5 == -1 || (i5 == 0 && i6 == 2)) && !isEncryptedDialog;
        if (z2 || (i3 == 1 && !isEncryptedDialog)) {
            getMediaCount(j, i2, i4, false);
        }
        if (!z2) {
            long j2 = j;
            if (!z) {
                putMediaCountDatabase(j, i6, i5);
            }
            NotificationCenter notificationCenter = getNotificationCenter();
            int i8 = NotificationCenter.mediaCountDidLoad;
            Object[] objArr = new Object[4];
            objArr[0] = Long.valueOf(j);
            if (!z || i5 != -1) {
                i7 = i5;
            }
            objArr[1] = Integer.valueOf(i7);
            objArr[2] = Boolean.valueOf(z);
            objArr[3] = Integer.valueOf(i2);
            notificationCenter.postNotificationName(i8, objArr);
        }
    }

    private void putMediaCountDatabase(long j, int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda42(this, j, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putMediaCountDatabase$116(long j, int i, int i2) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindLong(1, j);
            executeFast.bindInteger(2, i);
            executeFast.bindInteger(3, i2);
            executeFast.bindInteger(4, 0);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void getMediaCountDatabase(long j, int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda41(this, j, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCountDatabase$117(long j, int i, int i2) {
        int i3;
        int i4;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            Locale locale = Locale.US;
            SQLiteCursor queryFinalized = database.queryFinalized(String.format(locale, "SELECT count, old FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
            if (queryFinalized.next()) {
                i4 = queryFinalized.intValue(0);
                i3 = queryFinalized.intValue(1);
            } else {
                i4 = -1;
                i3 = 0;
            }
            queryFinalized.dispose();
            if (i4 == -1 && DialogObject.isEncryptedDialog(j)) {
                SQLiteCursor queryFinalized2 = getMessagesStorage().getDatabase().queryFinalized(String.format(locale, "SELECT COUNT(mid) FROM media_v4 WHERE uid = %d AND type = %d LIMIT 1", new Object[]{Long.valueOf(j), Integer.valueOf(i)}), new Object[0]);
                if (queryFinalized2.next()) {
                    i4 = queryFinalized2.intValue(0);
                }
                queryFinalized2.dispose();
                if (i4 != -1) {
                    try {
                        putMediaCountDatabase(j, i, i4);
                        processLoadedMediaCount(i4, j, i, i2, true, i3);
                    } catch (Exception e) {
                        e = e;
                        FileLog.e((Throwable) e);
                    }
                }
            }
            long j2 = j;
            int i5 = i;
            processLoadedMediaCount(i4, j, i, i2, true, i3);
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    private void loadMediaDatabase(long j, int i, int i2, int i3, int i4, int i5, boolean z, int i6, int i7) {
        final int i8 = i;
        final long j2 = j;
        final int i9 = i3;
        final int i10 = i4;
        final int i11 = i2;
        final int i12 = i5;
        final int i13 = i6;
        final boolean z2 = z;
        final int i14 = i7;
        AnonymousClass1 r0 = new Runnable() {
            /* JADX WARNING: Removed duplicated region for block: B:61:0x034d A[Catch:{ Exception -> 0x040f, all -> 0x040c }] */
            /* JADX WARNING: Removed duplicated region for block: B:72:0x039c A[Catch:{ Exception -> 0x040f, all -> 0x040c }] */
            /* JADX WARNING: Removed duplicated region for block: B:75:0x03a8 A[SYNTHETIC, Splitter:B:75:0x03a8] */
            /* JADX WARNING: Removed duplicated region for block: B:79:0x03bd A[Catch:{ Exception -> 0x040f, all -> 0x040c }] */
            /* JADX WARNING: Removed duplicated region for block: B:84:0x03da A[Catch:{ Exception -> 0x040f, all -> 0x040c }] */
            /* JADX WARNING: Removed duplicated region for block: B:85:0x03e6 A[Catch:{ Exception -> 0x040f, all -> 0x040c }] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r16 = this;
                    r1 = r16
                    org.telegram.tgnet.TLRPC$TL_messages_messages r3 = new org.telegram.tgnet.TLRPC$TL_messages_messages
                    r3.<init>()
                    java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x040f }
                    r2.<init>()     // Catch:{ Exception -> 0x040f }
                    java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x040f }
                    r4.<init>()     // Catch:{ Exception -> 0x040f }
                    int r5 = r2     // Catch:{ Exception -> 0x040f }
                    r6 = 1
                    int r5 = r5 + r6
                    org.telegram.messenger.MediaDataController r7 = org.telegram.messenger.MediaDataController.this     // Catch:{ Exception -> 0x040f }
                    org.telegram.messenger.MessagesStorage r7 = r7.getMessagesStorage()     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteDatabase r7 = r7.getDatabase()     // Catch:{ Exception -> 0x040f }
                    long r8 = r3     // Catch:{ Exception -> 0x040f }
                    boolean r8 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)     // Catch:{ Exception -> 0x040f }
                    r11 = 2
                    r12 = 0
                    if (r8 != 0) goto L_0x02a4
                    int r8 = r5     // Catch:{ Exception -> 0x040f }
                    if (r8 != 0) goto L_0x00ae
                    java.util.Locale r8 = java.util.Locale.US     // Catch:{ Exception -> 0x040f }
                    java.lang.String r14 = "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)"
                    java.lang.Object[] r15 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x040f }
                    long r9 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r15[r12] = r9     // Catch:{ Exception -> 0x040f }
                    int r9 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r15[r6] = r9     // Catch:{ Exception -> 0x040f }
                    java.lang.String r9 = java.lang.String.format(r8, r14, r15)     // Catch:{ Exception -> 0x040f }
                    java.lang.Object[] r10 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r9 = r7.queryFinalized(r9, r10)     // Catch:{ Exception -> 0x040f }
                    boolean r10 = r9.next()     // Catch:{ Exception -> 0x040f }
                    if (r10 == 0) goto L_0x005b
                    int r8 = r9.intValue(r12)     // Catch:{ Exception -> 0x040f }
                    if (r8 != r6) goto L_0x00a9
                    r8 = 1
                    goto L_0x00aa
                L_0x005b:
                    r9.dispose()     // Catch:{ Exception -> 0x040f }
                    java.lang.String r9 = "SELECT min(mid) FROM media_v4 WHERE uid = %d AND type = %d AND mid > 0"
                    java.lang.Object[] r10 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x040f }
                    long r14 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r14 = java.lang.Long.valueOf(r14)     // Catch:{ Exception -> 0x040f }
                    r10[r12] = r14     // Catch:{ Exception -> 0x040f }
                    int r14 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x040f }
                    r10[r6] = r14     // Catch:{ Exception -> 0x040f }
                    java.lang.String r8 = java.lang.String.format(r8, r9, r10)     // Catch:{ Exception -> 0x040f }
                    java.lang.Object[] r9 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r9 = r7.queryFinalized(r8, r9)     // Catch:{ Exception -> 0x040f }
                    boolean r8 = r9.next()     // Catch:{ Exception -> 0x040f }
                    if (r8 == 0) goto L_0x00a9
                    int r8 = r9.intValue(r12)     // Catch:{ Exception -> 0x040f }
                    if (r8 == 0) goto L_0x00a9
                    java.lang.String r10 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)"
                    org.telegram.SQLite.SQLitePreparedStatement r10 = r7.executeFast(r10)     // Catch:{ Exception -> 0x040f }
                    r10.requery()     // Catch:{ Exception -> 0x040f }
                    long r14 = r3     // Catch:{ Exception -> 0x040f }
                    r10.bindLong(r6, r14)     // Catch:{ Exception -> 0x040f }
                    int r14 = r6     // Catch:{ Exception -> 0x040f }
                    r10.bindInteger(r11, r14)     // Catch:{ Exception -> 0x040f }
                    r14 = 3
                    r10.bindInteger(r14, r12)     // Catch:{ Exception -> 0x040f }
                    r14 = 4
                    r10.bindInteger(r14, r8)     // Catch:{ Exception -> 0x040f }
                    r10.step()     // Catch:{ Exception -> 0x040f }
                    r10.dispose()     // Catch:{ Exception -> 0x040f }
                L_0x00a9:
                    r8 = 0
                L_0x00aa:
                    r9.dispose()     // Catch:{ Exception -> 0x040f }
                    goto L_0x00af
                L_0x00ae:
                    r8 = 0
                L_0x00af:
                    int r9 = r7     // Catch:{ Exception -> 0x040f }
                    if (r9 == 0) goto L_0x015b
                    java.util.Locale r9 = java.util.Locale.US     // Catch:{ Exception -> 0x040f }
                    java.lang.String r14 = "SELECT start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND start <= %d ORDER BY end DESC LIMIT 1"
                    r15 = 3
                    java.lang.Object[] r13 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x040f }
                    long r10 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r10 = java.lang.Long.valueOf(r10)     // Catch:{ Exception -> 0x040f }
                    r13[r12] = r10     // Catch:{ Exception -> 0x040f }
                    int r10 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x040f }
                    r13[r6] = r10     // Catch:{ Exception -> 0x040f }
                    int r10 = r7     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x040f }
                    r11 = 2
                    r13[r11] = r10     // Catch:{ Exception -> 0x040f }
                    java.lang.String r10 = java.lang.String.format(r9, r14, r13)     // Catch:{ Exception -> 0x040f }
                    java.lang.Object[] r11 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r10 = r7.queryFinalized(r10, r11)     // Catch:{ Exception -> 0x040f }
                    boolean r11 = r10.next()     // Catch:{ Exception -> 0x040f }
                    if (r11 == 0) goto L_0x00eb
                    r10.intValue(r12)     // Catch:{ Exception -> 0x040f }
                    int r11 = r10.intValue(r6)     // Catch:{ Exception -> 0x040f }
                    goto L_0x00ec
                L_0x00eb:
                    r11 = 0
                L_0x00ec:
                    r10.dispose()     // Catch:{ Exception -> 0x040f }
                    if (r11 <= r6) goto L_0x012a
                    java.lang.String r8 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d"
                    r10 = 5
                    java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x040f }
                    long r13 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r13 = java.lang.Long.valueOf(r13)     // Catch:{ Exception -> 0x040f }
                    r10[r12] = r13     // Catch:{ Exception -> 0x040f }
                    int r13 = r7     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x040f }
                    r10[r6] = r13     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x040f }
                    r13 = 2
                    r10[r13] = r11     // Catch:{ Exception -> 0x040f }
                    int r11 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x040f }
                    r13 = 3
                    r10[r13] = r11     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040f }
                    r11 = 4
                    r10[r11] = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.String r5 = java.lang.String.format(r9, r8, r10)     // Catch:{ Exception -> 0x040f }
                    java.lang.Object[] r8 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r5 = r7.queryFinalized(r5, r8)     // Catch:{ Exception -> 0x040f }
                    r8 = 0
                    goto L_0x02a2
                L_0x012a:
                    java.lang.String r10 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d"
                    r11 = 4
                    java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x040f }
                    long r13 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r13 = java.lang.Long.valueOf(r13)     // Catch:{ Exception -> 0x040f }
                    r11[r12] = r13     // Catch:{ Exception -> 0x040f }
                    int r13 = r7     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x040f }
                    r11[r6] = r13     // Catch:{ Exception -> 0x040f }
                    int r13 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x040f }
                    r14 = 2
                    r11[r14] = r13     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040f }
                    r13 = 3
                    r11[r13] = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.String r5 = java.lang.String.format(r9, r10, r11)     // Catch:{ Exception -> 0x040f }
                    java.lang.Object[] r9 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r5 = r7.queryFinalized(r5, r9)     // Catch:{ Exception -> 0x040f }
                    goto L_0x02a2
                L_0x015b:
                    int r9 = r5     // Catch:{ Exception -> 0x040f }
                    if (r9 == 0) goto L_0x0211
                    java.util.Locale r9 = java.util.Locale.US     // Catch:{ Exception -> 0x040f }
                    java.lang.String r10 = "SELECT start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end >= %d ORDER BY end ASC LIMIT 1"
                    r11 = 3
                    java.lang.Object[] r13 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x040f }
                    r14 = r7
                    long r6 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x040f }
                    r13[r12] = r6     // Catch:{ Exception -> 0x040f }
                    int r6 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x040f }
                    r7 = 1
                    r13[r7] = r6     // Catch:{ Exception -> 0x040f }
                    int r6 = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x040f }
                    r7 = 2
                    r13[r7] = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.String r6 = java.lang.String.format(r9, r10, r13)     // Catch:{ Exception -> 0x040f }
                    java.lang.Object[] r7 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r6 = r14.queryFinalized(r6, r7)     // Catch:{ Exception -> 0x040f }
                    boolean r7 = r6.next()     // Catch:{ Exception -> 0x040f }
                    if (r7 == 0) goto L_0x019a
                    int r7 = r6.intValue(r12)     // Catch:{ Exception -> 0x040f }
                    r10 = 1
                    r6.intValue(r10)     // Catch:{ Exception -> 0x040f }
                    goto L_0x019b
                L_0x019a:
                    r7 = 0
                L_0x019b:
                    r6.dispose()     // Catch:{ Exception -> 0x040f }
                    r6 = 1
                    if (r7 <= r6) goto L_0x01db
                    java.lang.String r6 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid >= %d AND mid <= %d AND type = %d ORDER BY date ASC, mid ASC LIMIT %d"
                    r10 = 5
                    java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x040f }
                    long r11 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r11 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x040f }
                    r12 = 0
                    r10[r12] = r11     // Catch:{ Exception -> 0x040f }
                    int r11 = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x040f }
                    r12 = 1
                    r10[r12] = r11     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x040f }
                    r12 = 2
                    r10[r12] = r7     // Catch:{ Exception -> 0x040f }
                    int r7 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x040f }
                    r12 = 3
                    r10[r12] = r7     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040f }
                    r7 = 4
                    r10[r7] = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.String r5 = java.lang.String.format(r9, r6, r10)     // Catch:{ Exception -> 0x040f }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040f }
                    goto L_0x020e
                L_0x01db:
                    java.lang.String r6 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid >= %d AND type = %d ORDER BY date ASC, mid ASC LIMIT %d"
                    r7 = 4
                    java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x040f }
                    long r11 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r8 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x040f }
                    r11 = 0
                    r7[r11] = r8     // Catch:{ Exception -> 0x040f }
                    int r8 = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x040f }
                    r10 = 1
                    r7[r10] = r8     // Catch:{ Exception -> 0x040f }
                    int r8 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x040f }
                    r10 = 2
                    r7[r10] = r8     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040f }
                    r8 = 3
                    r7[r8] = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.String r5 = java.lang.String.format(r9, r6, r7)     // Catch:{ Exception -> 0x040f }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040f }
                    r8 = 1
                L_0x020e:
                    r12 = 1
                    goto L_0x02a2
                L_0x0211:
                    r14 = r7
                    java.util.Locale r6 = java.util.Locale.US     // Catch:{ Exception -> 0x040f }
                    java.lang.String r7 = "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d"
                    r9 = 2
                    java.lang.Object[] r10 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x040f }
                    long r11 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r9 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x040f }
                    r12 = 0
                    r10[r12] = r9     // Catch:{ Exception -> 0x040f }
                    int r9 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r11 = 1
                    r10[r11] = r9     // Catch:{ Exception -> 0x040f }
                    java.lang.String r7 = java.lang.String.format(r6, r7, r10)     // Catch:{ Exception -> 0x040f }
                    java.lang.Object[] r9 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r7 = r14.queryFinalized(r7, r9)     // Catch:{ Exception -> 0x040f }
                    boolean r9 = r7.next()     // Catch:{ Exception -> 0x040f }
                    if (r9 == 0) goto L_0x0240
                    int r9 = r7.intValue(r12)     // Catch:{ Exception -> 0x040f }
                    goto L_0x0241
                L_0x0240:
                    r9 = 0
                L_0x0241:
                    r7.dispose()     // Catch:{ Exception -> 0x040f }
                    r7 = 1
                    if (r9 <= r7) goto L_0x0278
                    java.lang.String r7 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d"
                    r10 = 4
                    java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x040f }
                    long r11 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r11 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x040f }
                    r12 = 0
                    r10[r12] = r11     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r11 = 1
                    r10[r11] = r9     // Catch:{ Exception -> 0x040f }
                    int r9 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r12 = 2
                    r10[r12] = r9     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040f }
                    r9 = 3
                    r10[r9] = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.String r5 = java.lang.String.format(r6, r7, r10)     // Catch:{ Exception -> 0x040f }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040f }
                    goto L_0x02a1
                L_0x0278:
                    java.lang.String r7 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d"
                    r9 = 3
                    java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x040f }
                    long r11 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r11 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x040f }
                    r12 = 0
                    r9[r12] = r11     // Catch:{ Exception -> 0x040f }
                    int r11 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x040f }
                    r10 = 1
                    r9[r10] = r11     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040f }
                    r10 = 2
                    r9[r10] = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.String r5 = java.lang.String.format(r6, r7, r9)     // Catch:{ Exception -> 0x040f }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040f }
                L_0x02a1:
                    r12 = 0
                L_0x02a2:
                    r7 = r8
                    goto L_0x02df
                L_0x02a4:
                    r14 = r7
                    int r6 = r7     // Catch:{ Exception -> 0x040f }
                    if (r6 == 0) goto L_0x02e1
                    java.util.Locale r6 = java.util.Locale.US     // Catch:{ Exception -> 0x040f }
                    java.lang.String r7 = "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d"
                    r8 = 4
                    java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x040f }
                    long r9 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r10 = 0
                    r8[r10] = r9     // Catch:{ Exception -> 0x040f }
                    int r9 = r7     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r10 = 1
                    r8[r10] = r9     // Catch:{ Exception -> 0x040f }
                    int r9 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r10 = 2
                    r8[r10] = r9     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040f }
                    r9 = 3
                    r8[r9] = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.String r5 = java.lang.String.format(r6, r7, r8)     // Catch:{ Exception -> 0x040f }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040f }
                L_0x02dd:
                    r7 = 1
                    r12 = 0
                L_0x02df:
                    r13 = 0
                    goto L_0x0347
                L_0x02e1:
                    int r6 = r5     // Catch:{ Exception -> 0x040f }
                    if (r6 == 0) goto L_0x031a
                    java.util.Locale r6 = java.util.Locale.US     // Catch:{ Exception -> 0x040f }
                    java.lang.String r7 = "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d AND type = %d ORDER BY m.mid DESC LIMIT %d"
                    r8 = 4
                    java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x040f }
                    long r9 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r10 = 0
                    r8[r10] = r9     // Catch:{ Exception -> 0x040f }
                    int r9 = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r10 = 1
                    r8[r10] = r9     // Catch:{ Exception -> 0x040f }
                    int r9 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r10 = 2
                    r8[r10] = r9     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040f }
                    r9 = 3
                    r8[r9] = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.String r5 = java.lang.String.format(r6, r7, r8)     // Catch:{ Exception -> 0x040f }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040f }
                    goto L_0x02dd
                L_0x031a:
                    java.util.Locale r6 = java.util.Locale.US     // Catch:{ Exception -> 0x040f }
                    java.lang.String r7 = "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d"
                    r8 = 3
                    java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x040f }
                    long r9 = r3     // Catch:{ Exception -> 0x040f }
                    java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r10 = 0
                    r8[r10] = r9     // Catch:{ Exception -> 0x040f }
                    int r9 = r6     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040f }
                    r10 = 1
                    r8[r10] = r9     // Catch:{ Exception -> 0x040f }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040f }
                    r9 = 2
                    r8[r9] = r5     // Catch:{ Exception -> 0x040f }
                    java.lang.String r5 = java.lang.String.format(r6, r7, r8)     // Catch:{ Exception -> 0x040f }
                    r13 = 0
                    java.lang.Object[] r6 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x040f }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r6)     // Catch:{ Exception -> 0x040f }
                    r7 = 1
                    r12 = 0
                L_0x0347:
                    boolean r6 = r5.next()     // Catch:{ Exception -> 0x040f }
                    if (r6 == 0) goto L_0x039c
                    org.telegram.tgnet.NativeByteBuffer r6 = r5.byteBufferValue(r13)     // Catch:{ Exception -> 0x040f }
                    if (r6 == 0) goto L_0x0398
                    int r8 = r6.readInt32(r13)     // Catch:{ Exception -> 0x040f }
                    org.telegram.tgnet.TLRPC$Message r8 = org.telegram.tgnet.TLRPC$Message.TLdeserialize(r6, r8, r13)     // Catch:{ Exception -> 0x040f }
                    org.telegram.messenger.MediaDataController r9 = org.telegram.messenger.MediaDataController.this     // Catch:{ Exception -> 0x040f }
                    org.telegram.messenger.UserConfig r9 = r9.getUserConfig()     // Catch:{ Exception -> 0x040f }
                    long r9 = r9.clientUserId     // Catch:{ Exception -> 0x040f }
                    r8.readAttachPath(r6, r9)     // Catch:{ Exception -> 0x040f }
                    r6.reuse()     // Catch:{ Exception -> 0x040f }
                    r6 = 1
                    int r9 = r5.intValue(r6)     // Catch:{ Exception -> 0x040f }
                    r8.id = r9     // Catch:{ Exception -> 0x040f }
                    long r9 = r3     // Catch:{ Exception -> 0x040f }
                    r8.dialog_id = r9     // Catch:{ Exception -> 0x040f }
                    boolean r6 = org.telegram.messenger.DialogObject.isEncryptedDialog(r9)     // Catch:{ Exception -> 0x040f }
                    if (r6 == 0) goto L_0x0382
                    r6 = 2
                    long r9 = r5.longValue(r6)     // Catch:{ Exception -> 0x040f }
                    r8.random_id = r9     // Catch:{ Exception -> 0x040f }
                    goto L_0x0383
                L_0x0382:
                    r6 = 2
                L_0x0383:
                    if (r12 == 0) goto L_0x038c
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r9 = r3.messages     // Catch:{ Exception -> 0x040f }
                    r10 = 0
                    r9.add(r10, r8)     // Catch:{ Exception -> 0x040f }
                    goto L_0x0392
                L_0x038c:
                    r10 = 0
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r9 = r3.messages     // Catch:{ Exception -> 0x040f }
                    r9.add(r8)     // Catch:{ Exception -> 0x040f }
                L_0x0392:
                    r9 = 0
                    org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r8, r2, r4, r9)     // Catch:{ Exception -> 0x040f }
                    goto L_0x02df
                L_0x0398:
                    r6 = 2
                    r10 = 0
                    goto L_0x02df
                L_0x039c:
                    r10 = 0
                    r5.dispose()     // Catch:{ Exception -> 0x040f }
                    boolean r5 = r2.isEmpty()     // Catch:{ Exception -> 0x040f }
                    java.lang.String r6 = ","
                    if (r5 != 0) goto L_0x03b7
                    org.telegram.messenger.MediaDataController r5 = org.telegram.messenger.MediaDataController.this     // Catch:{ Exception -> 0x040f }
                    org.telegram.messenger.MessagesStorage r5 = r5.getMessagesStorage()     // Catch:{ Exception -> 0x040f }
                    java.lang.String r2 = android.text.TextUtils.join(r6, r2)     // Catch:{ Exception -> 0x040f }
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r3.users     // Catch:{ Exception -> 0x040f }
                    r5.getUsersInternal(r2, r8)     // Catch:{ Exception -> 0x040f }
                L_0x03b7:
                    boolean r2 = r4.isEmpty()     // Catch:{ Exception -> 0x040f }
                    if (r2 != 0) goto L_0x03cc
                    org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.this     // Catch:{ Exception -> 0x040f }
                    org.telegram.messenger.MessagesStorage r2 = r2.getMessagesStorage()     // Catch:{ Exception -> 0x040f }
                    java.lang.String r4 = android.text.TextUtils.join(r6, r4)     // Catch:{ Exception -> 0x040f }
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r3.chats     // Catch:{ Exception -> 0x040f }
                    r2.getChatsInternal(r4, r5)     // Catch:{ Exception -> 0x040f }
                L_0x03cc:
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r3.messages     // Catch:{ Exception -> 0x040f }
                    int r2 = r2.size()     // Catch:{ Exception -> 0x040f }
                    int r4 = r2     // Catch:{ Exception -> 0x040f }
                    if (r2 <= r4) goto L_0x03e6
                    int r2 = r5     // Catch:{ Exception -> 0x040f }
                    if (r2 != 0) goto L_0x03e6
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r3.messages     // Catch:{ Exception -> 0x040f }
                    int r4 = r2.size()     // Catch:{ Exception -> 0x040f }
                    r5 = 1
                    int r4 = r4 - r5
                    r2.remove(r4)     // Catch:{ Exception -> 0x040f }
                    goto L_0x03ea
                L_0x03e6:
                    int r2 = r5     // Catch:{ Exception -> 0x040f }
                    if (r2 == 0) goto L_0x03ec
                L_0x03ea:
                    r13 = 0
                    goto L_0x03ed
                L_0x03ec:
                    r13 = r7
                L_0x03ed:
                    int r2 = r8
                    org.telegram.messenger.MediaDataController$1$$ExternalSyntheticLambda0 r4 = new org.telegram.messenger.MediaDataController$1$$ExternalSyntheticLambda0
                    r4.<init>(r1, r1, r2)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
                    org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.this
                    long r4 = r3
                    int r6 = r2
                    int r7 = r7
                    int r8 = r5
                    int r9 = r6
                    int r10 = r9
                    int r11 = r8
                    boolean r12 = r10
                    int r14 = r11
                    goto L_0x0442
                L_0x040c:
                    r0 = move-exception
                    r15 = r0
                    goto L_0x0446
                L_0x040f:
                    r0 = move-exception
                    r2 = r0
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r4 = r3.messages     // Catch:{ all -> 0x040c }
                    r4.clear()     // Catch:{ all -> 0x040c }
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r3.chats     // Catch:{ all -> 0x040c }
                    r4.clear()     // Catch:{ all -> 0x040c }
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r3.users     // Catch:{ all -> 0x040c }
                    r4.clear()     // Catch:{ all -> 0x040c }
                    org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x040c }
                    int r2 = r8
                    org.telegram.messenger.MediaDataController$1$$ExternalSyntheticLambda0 r4 = new org.telegram.messenger.MediaDataController$1$$ExternalSyntheticLambda0
                    r4.<init>(r1, r1, r2)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
                    org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.this
                    long r4 = r3
                    int r6 = r2
                    int r7 = r7
                    int r8 = r5
                    int r9 = r6
                    int r10 = r9
                    int r11 = r8
                    boolean r12 = r10
                    int r14 = r11
                    r13 = 0
                L_0x0442:
                    r2.processLoadedMedia(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)
                    return
                L_0x0446:
                    int r2 = r8
                    org.telegram.messenger.MediaDataController$1$$ExternalSyntheticLambda0 r4 = new org.telegram.messenger.MediaDataController$1$$ExternalSyntheticLambda0
                    r4.<init>(r1, r1, r2)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
                    org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.this
                    long r4 = r3
                    int r6 = r2
                    int r7 = r7
                    int r8 = r5
                    int r9 = r6
                    int r10 = r9
                    int r11 = r8
                    boolean r12 = r10
                    int r14 = r11
                    r13 = 0
                    r2.processLoadedMedia(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)
                    goto L_0x046a
                L_0x0469:
                    throw r15
                L_0x046a:
                    goto L_0x0469
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.AnonymousClass1.run():void");
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0(Runnable runnable, int i) {
                MediaDataController.this.getMessagesStorage().completeTaskForGuid(runnable, i);
            }
        };
        MessagesStorage messagesStorage = getMessagesStorage();
        messagesStorage.getStorageQueue().postRunnable(r0);
        messagesStorage.bindTaskToGuid(r0, i5);
    }

    private void putMediaDatabase(long j, int i, ArrayList<TLRPC$Message> arrayList, int i2, int i3, boolean z) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda27(this, i3, arrayList, z, j, i2, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putMediaDatabase$118(int i, ArrayList arrayList, boolean z, long j, int i2, int i3) {
        int i4;
        ArrayList arrayList2 = arrayList;
        long j2 = j;
        int i5 = i2;
        int i6 = i3;
        if (i == 0) {
            try {
                if (arrayList.isEmpty() || z) {
                    getMessagesStorage().doneHolesInMedia(j2, i5, i6);
                    if (arrayList.isEmpty()) {
                        return;
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return;
            }
        }
        getMessagesStorage().getDatabase().beginTransaction();
        SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO media_v4 VALUES(?, ?, ?, ?, ?)");
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            TLRPC$Message tLRPC$Message = (TLRPC$Message) it.next();
            if (canAddMessageToMedia(tLRPC$Message)) {
                executeFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Message.getObjectSize());
                tLRPC$Message.serializeToStream(nativeByteBuffer);
                executeFast.bindInteger(1, tLRPC$Message.id);
                executeFast.bindLong(2, j2);
                executeFast.bindInteger(3, tLRPC$Message.date);
                executeFast.bindInteger(4, i6);
                executeFast.bindByteBuffer(5, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
        }
        executeFast.dispose();
        if (!(z && i5 == 0 && i == 0)) {
            if (!z || i != 0) {
                i4 = ((TLRPC$Message) arrayList.get(arrayList.size() - 1)).id;
            } else {
                i4 = 1;
            }
            if (i != 0) {
                getMessagesStorage().closeHolesInMedia(j, i4, ((TLRPC$Message) arrayList.get(0)).id, i3);
            } else if (i5 != 0) {
                getMessagesStorage().closeHolesInMedia(j, i4, i2, i3);
            } else {
                getMessagesStorage().closeHolesInMedia(j, i4, Integer.MAX_VALUE, i3);
            }
        }
        getMessagesStorage().getDatabase().commitTransaction();
    }

    public void loadMusic(long j, long j2, long j3) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda45(this, j, j2, j3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMusic$120(long j, long j2, long j3) {
        SQLiteCursor sQLiteCursor;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int i = 0;
        while (i < 2) {
            ArrayList arrayList3 = i == 0 ? arrayList : arrayList2;
            if (i == 0) {
                try {
                    if (!DialogObject.isEncryptedDialog(j)) {
                        sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j), Long.valueOf(j2), 4}), new Object[0]);
                    } else {
                        sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j), Long.valueOf(j2), 4}), new Object[0]);
                    }
                } catch (Exception e) {
                    e = e;
                    long j4 = j;
                    FileLog.e((Throwable) e);
                    AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda48(this, j, arrayList, arrayList2));
                }
            } else if (!DialogObject.isEncryptedDialog(j)) {
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j), Long.valueOf(j3), 4}), new Object[0]);
            } else {
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[]{Long.valueOf(j), Long.valueOf(j3), 4}), new Object[0]);
            }
            while (sQLiteCursor.next()) {
                NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    if (MessageObject.isMusicMessage(TLdeserialize)) {
                        TLdeserialize.id = sQLiteCursor.intValue(1);
                        try {
                            TLdeserialize.dialog_id = j;
                            try {
                                arrayList3.add(0, new MessageObject(this.currentAccount, TLdeserialize, false, true));
                            } catch (Exception e2) {
                                e = e2;
                                FileLog.e((Throwable) e);
                                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda48(this, j, arrayList, arrayList2));
                            }
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.e((Throwable) e);
                            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda48(this, j, arrayList, arrayList2));
                        }
                    }
                }
                long j5 = j;
            }
            long j6 = j;
            sQLiteCursor.dispose();
            i++;
        }
        long j7 = j;
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda48(this, j, arrayList, arrayList2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMusic$119(long j, ArrayList arrayList, ArrayList arrayList2) {
        getNotificationCenter().postNotificationName(NotificationCenter.musicDidLoad, Long.valueOf(j), arrayList, arrayList2);
    }

    public void buildShortcuts() {
        if (Build.VERSION.SDK_INT >= 23) {
            int maxShortcutCountPerActivity = ShortcutManagerCompat.getMaxShortcutCountPerActivity(ApplicationLoader.applicationContext) - 2;
            if (maxShortcutCountPerActivity <= 0) {
                maxShortcutCountPerActivity = 5;
            }
            ArrayList arrayList = new ArrayList();
            if (SharedConfig.passcodeHash.length() <= 0) {
                for (int i = 0; i < this.hints.size(); i++) {
                    arrayList.add(this.hints.get(i));
                    if (arrayList.size() == maxShortcutCountPerActivity - 2) {
                        break;
                    }
                }
            }
            Utilities.globalQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda68(this, arrayList));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x01b5 A[SYNTHETIC, Splitter:B:57:0x01b5] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0260 A[Catch:{ all -> 0x02ca }] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0277 A[Catch:{ all -> 0x02ca }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0290 A[Catch:{ all -> 0x02ca }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0295 A[Catch:{ all -> 0x02ca }] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x029d A[Catch:{ all -> 0x02ca }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x02b5 A[Catch:{ all -> 0x02ca }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x02bb A[Catch:{ all -> 0x02ca }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$buildShortcuts$121(java.util.ArrayList r21) {
        /*
            r20 = this;
            r1 = r21
            java.lang.String r0 = "NewConversationShortcut"
            java.lang.String r2 = org.telegram.messenger.SharedConfig.directShareHash     // Catch:{ all -> 0x02ca }
            r3 = 0
            if (r2 != 0) goto L_0x002a
            java.util.UUID r2 = java.util.UUID.randomUUID()     // Catch:{ all -> 0x02ca }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x02ca }
            org.telegram.messenger.SharedConfig.directShareHash = r2     // Catch:{ all -> 0x02ca }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            java.lang.String r4 = "mainconfig"
            android.content.SharedPreferences r2 = r2.getSharedPreferences(r4, r3)     // Catch:{ all -> 0x02ca }
            android.content.SharedPreferences$Editor r2 = r2.edit()     // Catch:{ all -> 0x02ca }
            java.lang.String r4 = "directShareHash2"
            java.lang.String r5 = org.telegram.messenger.SharedConfig.directShareHash     // Catch:{ all -> 0x02ca }
            android.content.SharedPreferences$Editor r2 = r2.putString(r4, r5)     // Catch:{ all -> 0x02ca }
            r2.commit()     // Catch:{ all -> 0x02ca }
        L_0x002a:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            java.util.List r2 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r2)     // Catch:{ all -> 0x02ca }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x02ca }
            r4.<init>()     // Catch:{ all -> 0x02ca }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x02ca }
            r5.<init>()     // Catch:{ all -> 0x02ca }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x02ca }
            r6.<init>()     // Catch:{ all -> 0x02ca }
            java.lang.String r7 = "did3_"
            java.lang.String r8 = "compose"
            if (r2 == 0) goto L_0x00a3
            boolean r9 = r2.isEmpty()     // Catch:{ all -> 0x02ca }
            if (r9 != 0) goto L_0x00a3
            r5.add(r8)     // Catch:{ all -> 0x02ca }
            r9 = 0
        L_0x004f:
            int r10 = r21.size()     // Catch:{ all -> 0x02ca }
            if (r9 >= r10) goto L_0x0076
            java.lang.Object r10 = r1.get(r9)     // Catch:{ all -> 0x02ca }
            org.telegram.tgnet.TLRPC$TL_topPeer r10 = (org.telegram.tgnet.TLRPC$TL_topPeer) r10     // Catch:{ all -> 0x02ca }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ca }
            r11.<init>()     // Catch:{ all -> 0x02ca }
            r11.append(r7)     // Catch:{ all -> 0x02ca }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.peer     // Catch:{ all -> 0x02ca }
            long r12 = org.telegram.messenger.MessageObject.getPeerId(r10)     // Catch:{ all -> 0x02ca }
            r11.append(r12)     // Catch:{ all -> 0x02ca }
            java.lang.String r10 = r11.toString()     // Catch:{ all -> 0x02ca }
            r5.add(r10)     // Catch:{ all -> 0x02ca }
            int r9 = r9 + 1
            goto L_0x004f
        L_0x0076:
            r9 = 0
        L_0x0077:
            int r10 = r2.size()     // Catch:{ all -> 0x02ca }
            if (r9 >= r10) goto L_0x0096
            java.lang.Object r10 = r2.get(r9)     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutInfoCompat r10 = (androidx.core.content.pm.ShortcutInfoCompat) r10     // Catch:{ all -> 0x02ca }
            java.lang.String r10 = r10.getId()     // Catch:{ all -> 0x02ca }
            boolean r11 = r5.remove(r10)     // Catch:{ all -> 0x02ca }
            if (r11 != 0) goto L_0x0090
            r6.add(r10)     // Catch:{ all -> 0x02ca }
        L_0x0090:
            r4.add(r10)     // Catch:{ all -> 0x02ca }
            int r9 = r9 + 1
            goto L_0x0077
        L_0x0096:
            boolean r2 = r5.isEmpty()     // Catch:{ all -> 0x02ca }
            if (r2 == 0) goto L_0x00a3
            boolean r2 = r6.isEmpty()     // Catch:{ all -> 0x02ca }
            if (r2 == 0) goto L_0x00a3
            return
        L_0x00a3:
            android.content.Intent r2 = new android.content.Intent     // Catch:{ all -> 0x02ca }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            java.lang.Class<org.telegram.ui.LaunchActivity> r9 = org.telegram.ui.LaunchActivity.class
            r2.<init>(r5, r9)     // Catch:{ all -> 0x02ca }
            java.lang.String r5 = "new_dialog"
            r2.setAction(r5)     // Catch:{ all -> 0x02ca }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x02ca }
            r5.<init>()     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r9 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ all -> 0x02ca }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            r9.<init>((android.content.Context) r10, (java.lang.String) r8)     // Catch:{ all -> 0x02ca }
            int r10 = org.telegram.messenger.R.string.NewConversationShortcut     // Catch:{ all -> 0x02ca }
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r10)     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r9 = r9.setShortLabel(r11)     // Catch:{ all -> 0x02ca }
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r10)     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r9.setLongLabel(r0)     // Catch:{ all -> 0x02ca }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            int r10 = org.telegram.messenger.R.drawable.shortcut_compose     // Catch:{ all -> 0x02ca }
            androidx.core.graphics.drawable.IconCompat r9 = androidx.core.graphics.drawable.IconCompat.createWithResource(r9, r10)     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setIcon(r9)     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setIntent(r2)     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutInfoCompat r0 = r0.build()     // Catch:{ all -> 0x02ca }
            r5.add(r0)     // Catch:{ all -> 0x02ca }
            boolean r0 = r4.contains(r8)     // Catch:{ all -> 0x02ca }
            if (r0 == 0) goto L_0x00f2
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutManagerCompat.updateShortcuts(r0, r5)     // Catch:{ all -> 0x02ca }
            goto L_0x00f7
        L_0x00f2:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutManagerCompat.addDynamicShortcuts(r0, r5)     // Catch:{ all -> 0x02ca }
        L_0x00f7:
            r5.clear()     // Catch:{ all -> 0x02ca }
            boolean r0 = r6.isEmpty()     // Catch:{ all -> 0x02ca }
            if (r0 != 0) goto L_0x0105
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r0, r6)     // Catch:{ all -> 0x02ca }
        L_0x0105:
            java.util.HashSet r2 = new java.util.HashSet     // Catch:{ all -> 0x02ca }
            r6 = 1
            r2.<init>(r6)     // Catch:{ all -> 0x02ca }
            java.lang.String r0 = SHORTCUT_CATEGORY     // Catch:{ all -> 0x02ca }
            r2.add(r0)     // Catch:{ all -> 0x02ca }
        L_0x0110:
            int r0 = r21.size()     // Catch:{ all -> 0x02ca }
            if (r3 >= r0) goto L_0x02ca
            android.content.Intent r8 = new android.content.Intent     // Catch:{ all -> 0x02ca }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            java.lang.Class<org.telegram.messenger.OpenChatReceiver> r9 = org.telegram.messenger.OpenChatReceiver.class
            r8.<init>(r0, r9)     // Catch:{ all -> 0x02ca }
            java.lang.Object r0 = r1.get(r3)     // Catch:{ all -> 0x02ca }
            org.telegram.tgnet.TLRPC$TL_topPeer r0 = (org.telegram.tgnet.TLRPC$TL_topPeer) r0     // Catch:{ all -> 0x02ca }
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer     // Catch:{ all -> 0x02ca }
            long r9 = org.telegram.messenger.MessageObject.getPeerId(r0)     // Catch:{ all -> 0x02ca }
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r9)     // Catch:{ all -> 0x02ca }
            if (r0 == 0) goto L_0x0144
            java.lang.String r0 = "userId"
            r8.putExtra(r0, r9)     // Catch:{ all -> 0x02ca }
            org.telegram.messenger.MessagesController r0 = r20.getMessagesController()     // Catch:{ all -> 0x02ca }
            java.lang.Long r12 = java.lang.Long.valueOf(r9)     // Catch:{ all -> 0x02ca }
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r12)     // Catch:{ all -> 0x02ca }
            r12 = 0
            goto L_0x0158
        L_0x0144:
            org.telegram.messenger.MessagesController r0 = r20.getMessagesController()     // Catch:{ all -> 0x02ca }
            long r12 = -r9
            java.lang.Long r14 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x02ca }
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r14)     // Catch:{ all -> 0x02ca }
            java.lang.String r14 = "chatId"
            r8.putExtra(r14, r12)     // Catch:{ all -> 0x02ca }
            r12 = r0
            r0 = 0
        L_0x0158:
            if (r0 == 0) goto L_0x0160
            boolean r13 = org.telegram.messenger.UserObject.isDeleted(r0)     // Catch:{ all -> 0x02ca }
            if (r13 == 0) goto L_0x0164
        L_0x0160:
            if (r12 != 0) goto L_0x0164
            goto L_0x02c3
        L_0x0164:
            if (r0 == 0) goto L_0x0175
            java.lang.String r12 = r0.first_name     // Catch:{ all -> 0x02ca }
            java.lang.String r13 = r0.last_name     // Catch:{ all -> 0x02ca }
            java.lang.String r12 = org.telegram.messenger.ContactsController.formatName(r12, r13)     // Catch:{ all -> 0x02ca }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x02ca }
            if (r0 == 0) goto L_0x0184
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x02ca }
            goto L_0x0185
        L_0x0175:
            java.lang.String r0 = r12.title     // Catch:{ all -> 0x02ca }
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r12.photo     // Catch:{ all -> 0x02ca }
            if (r12 == 0) goto L_0x0183
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ all -> 0x02ca }
            r19 = r12
            r12 = r0
            r0 = r19
            goto L_0x0185
        L_0x0183:
            r12 = r0
        L_0x0184:
            r0 = 0
        L_0x0185:
            java.lang.String r13 = "currentAccount"
            r14 = r20
            int r15 = r14.currentAccount     // Catch:{ all -> 0x02ca }
            r8.putExtra(r13, r15)     // Catch:{ all -> 0x02ca }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ca }
            r13.<init>()     // Catch:{ all -> 0x02ca }
            java.lang.String r15 = "com.tmessages.openchat"
            r13.append(r15)     // Catch:{ all -> 0x02ca }
            r13.append(r9)     // Catch:{ all -> 0x02ca }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x02ca }
            r8.setAction(r13)     // Catch:{ all -> 0x02ca }
            java.lang.String r13 = "dialogId"
            r8.putExtra(r13, r9)     // Catch:{ all -> 0x02ca }
            java.lang.String r13 = "hash"
            java.lang.String r15 = org.telegram.messenger.SharedConfig.directShareHash     // Catch:{ all -> 0x02ca }
            r8.putExtra(r13, r15)     // Catch:{ all -> 0x02ca }
            r13 = 67108864(0x4000000, float:1.5046328E-36)
            r8.addFlags(r13)     // Catch:{ all -> 0x02ca }
            if (r0 == 0) goto L_0x0260
            org.telegram.messenger.FileLoader r13 = r20.getFileLoader()     // Catch:{ all -> 0x0259 }
            java.io.File r0 = r13.getPathToAttach(r0, r6)     // Catch:{ all -> 0x0259 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0259 }
            android.graphics.Bitmap r13 = android.graphics.BitmapFactory.decodeFile(r0)     // Catch:{ all -> 0x0259 }
            if (r13 == 0) goto L_0x0257
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ all -> 0x0254 }
            android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0254 }
            android.graphics.Bitmap r15 = android.graphics.Bitmap.createBitmap(r0, r0, r15)     // Catch:{ all -> 0x0254 }
            android.graphics.Canvas r11 = new android.graphics.Canvas     // Catch:{ all -> 0x0254 }
            r11.<init>(r15)     // Catch:{ all -> 0x0254 }
            android.graphics.Paint r16 = roundPaint     // Catch:{ all -> 0x0254 }
            r17 = 1073741824(0x40000000, float:2.0)
            if (r16 != 0) goto L_0x0222
            android.graphics.Paint r6 = new android.graphics.Paint     // Catch:{ all -> 0x0254 }
            r1 = 3
            r6.<init>(r1)     // Catch:{ all -> 0x0254 }
            roundPaint = r6     // Catch:{ all -> 0x0254 }
            android.graphics.RectF r1 = new android.graphics.RectF     // Catch:{ all -> 0x0254 }
            r1.<init>()     // Catch:{ all -> 0x0254 }
            bitmapRect = r1     // Catch:{ all -> 0x0254 }
            android.graphics.Paint r1 = new android.graphics.Paint     // Catch:{ all -> 0x0254 }
            r6 = 1
            r1.<init>(r6)     // Catch:{ all -> 0x0254 }
            erasePaint = r1     // Catch:{ all -> 0x0254 }
            android.graphics.PorterDuffXfermode r6 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x0254 }
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x0254 }
            r6.<init>(r14)     // Catch:{ all -> 0x0254 }
            r1.setXfermode(r6)     // Catch:{ all -> 0x0254 }
            android.graphics.Path r1 = new android.graphics.Path     // Catch:{ all -> 0x0254 }
            r1.<init>()     // Catch:{ all -> 0x0254 }
            roundPath = r1     // Catch:{ all -> 0x0254 }
            int r6 = r0 / 2
            float r6 = (float) r6     // Catch:{ all -> 0x0254 }
            int r14 = r0 / 2
            float r14 = (float) r14     // Catch:{ all -> 0x0254 }
            int r0 = r0 / 2
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r17)     // Catch:{ all -> 0x0254 }
            int r0 = r0 - r18
            float r0 = (float) r0     // Catch:{ all -> 0x0254 }
            r18 = r15
            android.graphics.Path$Direction r15 = android.graphics.Path.Direction.CW     // Catch:{ all -> 0x0254 }
            r1.addCircle(r6, r14, r0, r15)     // Catch:{ all -> 0x0254 }
            android.graphics.Path r0 = roundPath     // Catch:{ all -> 0x0254 }
            r0.toggleInverseFillType()     // Catch:{ all -> 0x0254 }
            goto L_0x0224
        L_0x0222:
            r18 = r15
        L_0x0224:
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x0254 }
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)     // Catch:{ all -> 0x0254 }
            float r1 = (float) r1     // Catch:{ all -> 0x0254 }
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)     // Catch:{ all -> 0x0254 }
            float r6 = (float) r6     // Catch:{ all -> 0x0254 }
            r14 = 1110966272(0x42380000, float:46.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x0254 }
            float r15 = (float) r15     // Catch:{ all -> 0x0254 }
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x0254 }
            float r14 = (float) r14     // Catch:{ all -> 0x0254 }
            r0.set(r1, r6, r15, r14)     // Catch:{ all -> 0x0254 }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x0254 }
            android.graphics.Paint r1 = roundPaint     // Catch:{ all -> 0x0254 }
            r6 = 0
            r11.drawBitmap(r13, r6, r0, r1)     // Catch:{ all -> 0x0254 }
            android.graphics.Path r0 = roundPath     // Catch:{ all -> 0x0254 }
            android.graphics.Paint r1 = erasePaint     // Catch:{ all -> 0x0254 }
            r11.drawPath(r0, r1)     // Catch:{ all -> 0x0254 }
            r11.setBitmap(r6)     // Catch:{ Exception -> 0x0251 }
        L_0x0251:
            r11 = r18
            goto L_0x0262
        L_0x0254:
            r0 = move-exception
            r11 = r13
            goto L_0x025c
        L_0x0257:
            r11 = r13
            goto L_0x0262
        L_0x0259:
            r0 = move-exception
            r6 = 0
            r11 = r6
        L_0x025c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x02ca }
            goto L_0x0262
        L_0x0260:
            r6 = 0
            r11 = r6
        L_0x0262:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ca }
            r0.<init>()     // Catch:{ all -> 0x02ca }
            r0.append(r7)     // Catch:{ all -> 0x02ca }
            r0.append(r9)     // Catch:{ all -> 0x02ca }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02ca }
            boolean r1 = android.text.TextUtils.isEmpty(r12)     // Catch:{ all -> 0x02ca }
            if (r1 == 0) goto L_0x0279
            java.lang.String r12 = " "
        L_0x0279:
            androidx.core.content.pm.ShortcutInfoCompat$Builder r1 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ all -> 0x02ca }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            r1.<init>((android.content.Context) r6, (java.lang.String) r0)     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r1 = r1.setShortLabel(r12)     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r1 = r1.setLongLabel(r12)     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r1 = r1.setIntent(r8)     // Catch:{ all -> 0x02ca }
            boolean r6 = org.telegram.messenger.SharedConfig.directShare     // Catch:{ all -> 0x02ca }
            if (r6 == 0) goto L_0x0293
            r1.setCategories(r2)     // Catch:{ all -> 0x02ca }
        L_0x0293:
            if (r11 == 0) goto L_0x029d
            androidx.core.graphics.drawable.IconCompat r6 = androidx.core.graphics.drawable.IconCompat.createWithBitmap(r11)     // Catch:{ all -> 0x02ca }
            r1.setIcon(r6)     // Catch:{ all -> 0x02ca }
            goto L_0x02a8
        L_0x029d:
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            int r8 = org.telegram.messenger.R.drawable.shortcut_user     // Catch:{ all -> 0x02ca }
            androidx.core.graphics.drawable.IconCompat r6 = androidx.core.graphics.drawable.IconCompat.createWithResource(r6, r8)     // Catch:{ all -> 0x02ca }
            r1.setIcon(r6)     // Catch:{ all -> 0x02ca }
        L_0x02a8:
            androidx.core.content.pm.ShortcutInfoCompat r1 = r1.build()     // Catch:{ all -> 0x02ca }
            r5.add(r1)     // Catch:{ all -> 0x02ca }
            boolean r0 = r4.contains(r0)     // Catch:{ all -> 0x02ca }
            if (r0 == 0) goto L_0x02bb
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutManagerCompat.updateShortcuts(r0, r5)     // Catch:{ all -> 0x02ca }
            goto L_0x02c0
        L_0x02bb:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02ca }
            androidx.core.content.pm.ShortcutManagerCompat.addDynamicShortcuts(r0, r5)     // Catch:{ all -> 0x02ca }
        L_0x02c0:
            r5.clear()     // Catch:{ all -> 0x02ca }
        L_0x02c3:
            int r3 = r3 + 1
            r1 = r21
            r6 = 1
            goto L_0x0110
        L_0x02ca:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$buildShortcuts$121(java.util.ArrayList):void");
    }

    public void loadHints(boolean z) {
        if (!this.loading && getUserConfig().suggestContacts) {
            if (!z) {
                this.loading = true;
                TLRPC$TL_contacts_getTopPeers tLRPC$TL_contacts_getTopPeers = new TLRPC$TL_contacts_getTopPeers();
                tLRPC$TL_contacts_getTopPeers.hash = 0;
                tLRPC$TL_contacts_getTopPeers.bots_pm = false;
                tLRPC$TL_contacts_getTopPeers.correspondents = true;
                tLRPC$TL_contacts_getTopPeers.groups = false;
                tLRPC$TL_contacts_getTopPeers.channels = false;
                tLRPC$TL_contacts_getTopPeers.bots_inline = true;
                tLRPC$TL_contacts_getTopPeers.offset = 0;
                tLRPC$TL_contacts_getTopPeers.limit = 20;
                getConnectionsManager().sendRequest(tLRPC$TL_contacts_getTopPeers, new MediaDataController$$ExternalSyntheticLambda159(this));
            } else if (!this.loaded) {
                this.loading = true;
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda18(this));
                this.loaded = true;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$123() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        long clientUserId = getUserConfig().getClientUserId();
        try {
            ArrayList arrayList5 = new ArrayList();
            ArrayList arrayList6 = new ArrayList();
            int i = 0;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT did, type, rating FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
            while (queryFinalized.next()) {
                long longValue = queryFinalized.longValue(i);
                if (longValue != clientUserId) {
                    int intValue = queryFinalized.intValue(1);
                    TLRPC$TL_topPeer tLRPC$TL_topPeer = new TLRPC$TL_topPeer();
                    tLRPC$TL_topPeer.rating = queryFinalized.doubleValue(2);
                    if (longValue > 0) {
                        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                        tLRPC$TL_topPeer.peer = tLRPC$TL_peerUser;
                        tLRPC$TL_peerUser.user_id = longValue;
                        arrayList5.add(Long.valueOf(longValue));
                    } else {
                        TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                        tLRPC$TL_topPeer.peer = tLRPC$TL_peerChat;
                        long j = -longValue;
                        tLRPC$TL_peerChat.chat_id = j;
                        arrayList6.add(Long.valueOf(j));
                    }
                    if (intValue == 0) {
                        arrayList.add(tLRPC$TL_topPeer);
                    } else if (intValue == 1) {
                        arrayList2.add(tLRPC$TL_topPeer);
                    }
                    i = 0;
                }
            }
            queryFinalized.dispose();
            if (!arrayList5.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", arrayList5), arrayList3);
            }
            if (!arrayList6.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList6), arrayList4);
            }
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda78(this, arrayList3, arrayList4, arrayList, arrayList2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$122(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        this.loading = false;
        this.loaded = true;
        this.hints = arrayList3;
        this.inlineBots = arrayList4;
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        if (Math.abs(getUserConfig().lastHintsSyncTime - ((int) (System.currentTimeMillis() / 1000))) >= 86400) {
            loadHints(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$128(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_contacts_topPeers) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda83(this, tLObject));
        } else if (tLObject instanceof TLRPC$TL_contacts_topPeersDisabled) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda9(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$126(TLObject tLObject) {
        TLRPC$TL_contacts_topPeers tLRPC$TL_contacts_topPeers = (TLRPC$TL_contacts_topPeers) tLObject;
        getMessagesController().putUsers(tLRPC$TL_contacts_topPeers.users, false);
        getMessagesController().putChats(tLRPC$TL_contacts_topPeers.chats, false);
        for (int i = 0; i < tLRPC$TL_contacts_topPeers.categories.size(); i++) {
            TLRPC$TL_topPeerCategoryPeers tLRPC$TL_topPeerCategoryPeers = tLRPC$TL_contacts_topPeers.categories.get(i);
            if (tLRPC$TL_topPeerCategoryPeers.category instanceof TLRPC$TL_topPeerCategoryBotsInline) {
                this.inlineBots = tLRPC$TL_topPeerCategoryPeers.peers;
                getUserConfig().botRatingLoadTime = (int) (System.currentTimeMillis() / 1000);
            } else {
                this.hints = tLRPC$TL_topPeerCategoryPeers.peers;
                long clientUserId = getUserConfig().getClientUserId();
                int i2 = 0;
                while (true) {
                    if (i2 >= this.hints.size()) {
                        break;
                    } else if (this.hints.get(i2).peer.user_id == clientUserId) {
                        this.hints.remove(i2);
                        break;
                    } else {
                        i2++;
                    }
                }
                getUserConfig().ratingLoadTime = (int) (System.currentTimeMillis() / 1000);
            }
        }
        getUserConfig().saveConfig(false);
        buildShortcuts();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda100(this, tLRPC$TL_contacts_topPeers));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$125(TLRPC$TL_contacts_topPeers tLRPC$TL_contacts_topPeers) {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
            getMessagesStorage().getDatabase().beginTransaction();
            getMessagesStorage().putUsersAndChats(tLRPC$TL_contacts_topPeers.users, tLRPC$TL_contacts_topPeers.chats, false, false);
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            for (int i = 0; i < tLRPC$TL_contacts_topPeers.categories.size(); i++) {
                TLRPC$TL_topPeerCategoryPeers tLRPC$TL_topPeerCategoryPeers = tLRPC$TL_contacts_topPeers.categories.get(i);
                int i2 = tLRPC$TL_topPeerCategoryPeers.category instanceof TLRPC$TL_topPeerCategoryBotsInline ? 1 : 0;
                for (int i3 = 0; i3 < tLRPC$TL_topPeerCategoryPeers.peers.size(); i3++) {
                    TLRPC$TL_topPeer tLRPC$TL_topPeer = tLRPC$TL_topPeerCategoryPeers.peers.get(i3);
                    executeFast.requery();
                    executeFast.bindLong(1, MessageObject.getPeerId(tLRPC$TL_topPeer.peer));
                    executeFast.bindInteger(2, i2);
                    executeFast.bindDouble(3, tLRPC$TL_topPeer.rating);
                    executeFast.bindInteger(4, 0);
                    executeFast.step();
                }
            }
            executeFast.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda5(this));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$124() {
        getUserConfig().suggestContacts = true;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$127() {
        getUserConfig().suggestContacts = false;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
        clearTopPeers();
    }

    public void clearTopPeers() {
        this.hints.clear();
        this.inlineBots.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda14(this));
        buildShortcuts();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearTopPeers$129() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
        } catch (Exception unused) {
        }
    }

    public void increaseInlineRaiting(long j) {
        if (getUserConfig().suggestContacts) {
            int max = getUserConfig().botRatingLoadTime != 0 ? Math.max(1, ((int) (System.currentTimeMillis() / 1000)) - getUserConfig().botRatingLoadTime) : 60;
            TLRPC$TL_topPeer tLRPC$TL_topPeer = null;
            int i = 0;
            while (true) {
                if (i >= this.inlineBots.size()) {
                    break;
                }
                TLRPC$TL_topPeer tLRPC$TL_topPeer2 = this.inlineBots.get(i);
                if (tLRPC$TL_topPeer2.peer.user_id == j) {
                    tLRPC$TL_topPeer = tLRPC$TL_topPeer2;
                    break;
                }
                i++;
            }
            if (tLRPC$TL_topPeer == null) {
                tLRPC$TL_topPeer = new TLRPC$TL_topPeer();
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_topPeer.peer = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = j;
                this.inlineBots.add(tLRPC$TL_topPeer);
            }
            tLRPC$TL_topPeer.rating += Math.exp((double) (max / getMessagesController().ratingDecay));
            Collections.sort(this.inlineBots, MediaDataController$$ExternalSyntheticLambda150.INSTANCE);
            if (this.inlineBots.size() > 20) {
                ArrayList<TLRPC$TL_topPeer> arrayList = this.inlineBots;
                arrayList.remove(arrayList.size() - 1);
            }
            savePeer(j, 1, tLRPC$TL_topPeer.rating);
            getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$increaseInlineRaiting$130(TLRPC$TL_topPeer tLRPC$TL_topPeer, TLRPC$TL_topPeer tLRPC$TL_topPeer2) {
        double d = tLRPC$TL_topPeer.rating;
        double d2 = tLRPC$TL_topPeer2.rating;
        if (d > d2) {
            return -1;
        }
        return d < d2 ? 1 : 0;
    }

    public void removeInline(long j) {
        for (int i = 0; i < this.inlineBots.size(); i++) {
            if (this.inlineBots.get(i).peer.user_id == j) {
                this.inlineBots.remove(i);
                TLRPC$TL_contacts_resetTopPeerRating tLRPC$TL_contacts_resetTopPeerRating = new TLRPC$TL_contacts_resetTopPeerRating();
                tLRPC$TL_contacts_resetTopPeerRating.category = new TLRPC$TL_topPeerCategoryBotsInline();
                tLRPC$TL_contacts_resetTopPeerRating.peer = getMessagesController().getInputPeer(j);
                getConnectionsManager().sendRequest(tLRPC$TL_contacts_resetTopPeerRating, MediaDataController$$ExternalSyntheticLambda203.INSTANCE);
                deletePeer(j, 1);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
                return;
            }
        }
    }

    public void removePeer(long j) {
        for (int i = 0; i < this.hints.size(); i++) {
            if (this.hints.get(i).peer.user_id == j) {
                this.hints.remove(i);
                getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                TLRPC$TL_contacts_resetTopPeerRating tLRPC$TL_contacts_resetTopPeerRating = new TLRPC$TL_contacts_resetTopPeerRating();
                tLRPC$TL_contacts_resetTopPeerRating.category = new TLRPC$TL_topPeerCategoryCorrespondents();
                tLRPC$TL_contacts_resetTopPeerRating.peer = getMessagesController().getInputPeer(j);
                deletePeer(j, 0);
                getConnectionsManager().sendRequest(tLRPC$TL_contacts_resetTopPeerRating, MediaDataController$$ExternalSyntheticLambda204.INSTANCE);
                return;
            }
        }
    }

    public void increasePeerRaiting(long j) {
        TLRPC$User user;
        if (getUserConfig().suggestContacts && DialogObject.isUserDialog(j) && (user = getMessagesController().getUser(Long.valueOf(j))) != null && !user.bot && !user.self) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda34(this, j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$increasePeerRaiting$135(long j) {
        int i;
        double d = 0.0d;
        try {
            int i2 = 0;
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages_v2 WHERE uid = %d AND out = 1", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next()) {
                i2 = queryFinalized.intValue(0);
                i = queryFinalized.intValue(1);
            } else {
                i = 0;
            }
            queryFinalized.dispose();
            if (i2 > 0 && getUserConfig().ratingLoadTime != 0) {
                d = (double) (i - getUserConfig().ratingLoadTime);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda37(this, j, d));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$increasePeerRaiting$134(long j, double d) {
        TLRPC$TL_topPeer tLRPC$TL_topPeer;
        int i = 0;
        while (true) {
            if (i >= this.hints.size()) {
                tLRPC$TL_topPeer = null;
                break;
            }
            tLRPC$TL_topPeer = this.hints.get(i);
            if (tLRPC$TL_topPeer.peer.user_id == j) {
                break;
            }
            i++;
        }
        if (tLRPC$TL_topPeer == null) {
            tLRPC$TL_topPeer = new TLRPC$TL_topPeer();
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            tLRPC$TL_topPeer.peer = tLRPC$TL_peerUser;
            tLRPC$TL_peerUser.user_id = j;
            this.hints.add(tLRPC$TL_topPeer);
        }
        double d2 = tLRPC$TL_topPeer.rating;
        double d3 = (double) getMessagesController().ratingDecay;
        Double.isNaN(d3);
        tLRPC$TL_topPeer.rating = d2 + Math.exp(d / d3);
        Collections.sort(this.hints, MediaDataController$$ExternalSyntheticLambda149.INSTANCE);
        savePeer(j, 0, tLRPC$TL_topPeer.rating);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$increasePeerRaiting$133(TLRPC$TL_topPeer tLRPC$TL_topPeer, TLRPC$TL_topPeer tLRPC$TL_topPeer2) {
        double d = tLRPC$TL_topPeer.rating;
        double d2 = tLRPC$TL_topPeer2.rating;
        if (d > d2) {
            return -1;
        }
        return d < d2 ? 1 : 0;
    }

    private void savePeer(long j, int i, double d) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda40(this, j, i, d));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$savePeer$136(long j, int i, double d) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
            executeFast.requery();
            executeFast.bindLong(1, j);
            executeFast.bindInteger(2, i);
            executeFast.bindDouble(3, d);
            executeFast.bindInteger(4, ((int) System.currentTimeMillis()) / 1000);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void deletePeer(long j, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda38(this, j, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$deletePeer$137(long j, int i) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[]{Long.valueOf(j), Integer.valueOf(i)})).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private Intent createIntrnalShortcutIntent(long j) {
        Intent intent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
        if (DialogObject.isEncryptedDialog(j)) {
            int encryptedChatId = DialogObject.getEncryptedChatId(j);
            intent.putExtra("encId", encryptedChatId);
            if (getMessagesController().getEncryptedChat(Integer.valueOf(encryptedChatId)) == null) {
                return null;
            }
        } else if (DialogObject.isUserDialog(j)) {
            intent.putExtra("userId", j);
        } else if (!DialogObject.isChatDialog(j)) {
            return null;
        } else {
            intent.putExtra("chatId", -j);
        }
        intent.putExtra("currentAccount", this.currentAccount);
        intent.setAction("com.tmessages.openchat" + j);
        intent.addFlags(67108864);
        return intent;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0062 A[Catch:{ Exception -> 0x0254 }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0091 A[Catch:{ Exception -> 0x0254 }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x009e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00a6 A[SYNTHETIC, Splitter:B:39:0x00a6] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00bb  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00d6 A[Catch:{ all -> 0x016e }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00f1 A[Catch:{ all -> 0x016e }] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0178 A[Catch:{ Exception -> 0x0254 }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01eb A[Catch:{ Exception -> 0x0254 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void installShortcut(long r17) {
        /*
            r16 = this;
            r1 = r17
            android.content.Intent r3 = r16.createIntrnalShortcutIntent(r17)     // Catch:{ Exception -> 0x0254 }
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r17)     // Catch:{ Exception -> 0x0254 }
            r4 = 0
            if (r0 == 0) goto L_0x002f
            int r0 = org.telegram.messenger.DialogObject.getEncryptedChatId(r17)     // Catch:{ Exception -> 0x0254 }
            org.telegram.messenger.MessagesController r5 = r16.getMessagesController()     // Catch:{ Exception -> 0x0254 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x0254 }
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r5.getEncryptedChat(r0)     // Catch:{ Exception -> 0x0254 }
            if (r0 != 0) goto L_0x0020
            return
        L_0x0020:
            org.telegram.messenger.MessagesController r5 = r16.getMessagesController()     // Catch:{ Exception -> 0x0254 }
            long r6 = r0.user_id     // Catch:{ Exception -> 0x0254 }
            java.lang.Long r0 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x0254 }
            org.telegram.tgnet.TLRPC$User r0 = r5.getUser(r0)     // Catch:{ Exception -> 0x0254 }
            goto L_0x0041
        L_0x002f:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r17)     // Catch:{ Exception -> 0x0254 }
            if (r0 == 0) goto L_0x0044
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()     // Catch:{ Exception -> 0x0254 }
            java.lang.Long r5 = java.lang.Long.valueOf(r17)     // Catch:{ Exception -> 0x0254 }
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r5)     // Catch:{ Exception -> 0x0254 }
        L_0x0041:
            r5 = r0
            r6 = r4
            goto L_0x0059
        L_0x0044:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r17)     // Catch:{ Exception -> 0x0254 }
            if (r0 == 0) goto L_0x0253
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()     // Catch:{ Exception -> 0x0254 }
            long r5 = -r1
            java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ Exception -> 0x0254 }
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r5)     // Catch:{ Exception -> 0x0254 }
            r6 = r0
            r5 = r4
        L_0x0059:
            if (r5 != 0) goto L_0x005e
            if (r6 != 0) goto L_0x005e
            return
        L_0x005e:
            r0 = 1
            r7 = 0
            if (r5 == 0) goto L_0x0091
            boolean r8 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r5)     // Catch:{ Exception -> 0x0254 }
            if (r8 == 0) goto L_0x0073
            java.lang.String r8 = "RepliesTitle"
            int r9 = org.telegram.messenger.R.string.RepliesTitle     // Catch:{ Exception -> 0x0254 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0254 }
        L_0x0070:
            r9 = r4
            r10 = 1
            goto L_0x009c
        L_0x0073:
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r5)     // Catch:{ Exception -> 0x0254 }
            if (r8 == 0) goto L_0x0082
            java.lang.String r8 = "SavedMessages"
            int r9 = org.telegram.messenger.R.string.SavedMessages     // Catch:{ Exception -> 0x0254 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0254 }
            goto L_0x0070
        L_0x0082:
            java.lang.String r8 = r5.first_name     // Catch:{ Exception -> 0x0254 }
            java.lang.String r9 = r5.last_name     // Catch:{ Exception -> 0x0254 }
            java.lang.String r8 = org.telegram.messenger.ContactsController.formatName(r8, r9)     // Catch:{ Exception -> 0x0254 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r5.photo     // Catch:{ Exception -> 0x0254 }
            if (r9 == 0) goto L_0x009a
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small     // Catch:{ Exception -> 0x0254 }
            goto L_0x009b
        L_0x0091:
            java.lang.String r8 = r6.title     // Catch:{ Exception -> 0x0254 }
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r6.photo     // Catch:{ Exception -> 0x0254 }
            if (r9 == 0) goto L_0x009a
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small     // Catch:{ Exception -> 0x0254 }
            goto L_0x009b
        L_0x009a:
            r9 = r4
        L_0x009b:
            r10 = 0
        L_0x009c:
            if (r10 != 0) goto L_0x00a4
            if (r9 == 0) goto L_0x00a1
            goto L_0x00a4
        L_0x00a1:
            r9 = r4
            goto L_0x0172
        L_0x00a4:
            if (r10 != 0) goto L_0x00bb
            org.telegram.messenger.FileLoader r11 = r16.getFileLoader()     // Catch:{ all -> 0x00b7 }
            java.io.File r9 = r11.getPathToAttach(r9, r0)     // Catch:{ all -> 0x00b7 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x00b7 }
            android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeFile(r9)     // Catch:{ all -> 0x00b7 }
            goto L_0x00bc
        L_0x00b7:
            r0 = move-exception
            r9 = r4
            goto L_0x016f
        L_0x00bb:
            r9 = r4
        L_0x00bc:
            if (r10 != 0) goto L_0x00c0
            if (r9 == 0) goto L_0x0172
        L_0x00c0:
            r11 = 1114112000(0x42680000, float:58.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x016e }
            android.graphics.Bitmap$Config r12 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x016e }
            android.graphics.Bitmap r12 = android.graphics.Bitmap.createBitmap(r11, r11, r12)     // Catch:{ all -> 0x016e }
            r12.eraseColor(r7)     // Catch:{ all -> 0x016e }
            android.graphics.Canvas r13 = new android.graphics.Canvas     // Catch:{ all -> 0x016e }
            r13.<init>(r12)     // Catch:{ all -> 0x016e }
            if (r10 == 0) goto L_0x00f1
            org.telegram.ui.Components.AvatarDrawable r10 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x016e }
            r10.<init>((org.telegram.tgnet.TLRPC$User) r5)     // Catch:{ all -> 0x016e }
            boolean r14 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r5)     // Catch:{ all -> 0x016e }
            if (r14 == 0) goto L_0x00e7
            r0 = 12
            r10.setAvatarType(r0)     // Catch:{ all -> 0x016e }
            goto L_0x00ea
        L_0x00e7:
            r10.setAvatarType(r0)     // Catch:{ all -> 0x016e }
        L_0x00ea:
            r10.setBounds(r7, r7, r11, r11)     // Catch:{ all -> 0x016e }
            r10.draw(r13)     // Catch:{ all -> 0x016e }
            goto L_0x0140
        L_0x00f1:
            android.graphics.BitmapShader r10 = new android.graphics.BitmapShader     // Catch:{ all -> 0x016e }
            android.graphics.Shader$TileMode r14 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x016e }
            r10.<init>(r9, r14, r14)     // Catch:{ all -> 0x016e }
            android.graphics.Paint r14 = roundPaint     // Catch:{ all -> 0x016e }
            if (r14 != 0) goto L_0x010a
            android.graphics.Paint r14 = new android.graphics.Paint     // Catch:{ all -> 0x016e }
            r14.<init>(r0)     // Catch:{ all -> 0x016e }
            roundPaint = r14     // Catch:{ all -> 0x016e }
            android.graphics.RectF r0 = new android.graphics.RectF     // Catch:{ all -> 0x016e }
            r0.<init>()     // Catch:{ all -> 0x016e }
            bitmapRect = r0     // Catch:{ all -> 0x016e }
        L_0x010a:
            float r0 = (float) r11     // Catch:{ all -> 0x016e }
            int r14 = r9.getWidth()     // Catch:{ all -> 0x016e }
            float r14 = (float) r14     // Catch:{ all -> 0x016e }
            float r0 = r0 / r14
            r13.save()     // Catch:{ all -> 0x016e }
            r13.scale(r0, r0)     // Catch:{ all -> 0x016e }
            android.graphics.Paint r0 = roundPaint     // Catch:{ all -> 0x016e }
            r0.setShader(r10)     // Catch:{ all -> 0x016e }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x016e }
            int r10 = r9.getWidth()     // Catch:{ all -> 0x016e }
            float r10 = (float) r10     // Catch:{ all -> 0x016e }
            int r14 = r9.getHeight()     // Catch:{ all -> 0x016e }
            float r14 = (float) r14     // Catch:{ all -> 0x016e }
            r15 = 0
            r0.set(r15, r15, r10, r14)     // Catch:{ all -> 0x016e }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x016e }
            int r10 = r9.getWidth()     // Catch:{ all -> 0x016e }
            float r10 = (float) r10     // Catch:{ all -> 0x016e }
            int r14 = r9.getHeight()     // Catch:{ all -> 0x016e }
            float r14 = (float) r14     // Catch:{ all -> 0x016e }
            android.graphics.Paint r15 = roundPaint     // Catch:{ all -> 0x016e }
            r13.drawRoundRect(r0, r10, r14, r15)     // Catch:{ all -> 0x016e }
            r13.restore()     // Catch:{ all -> 0x016e }
        L_0x0140:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x016e }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x016e }
            int r10 = org.telegram.messenger.R.drawable.book_logo     // Catch:{ all -> 0x016e }
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r10)     // Catch:{ all -> 0x016e }
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ all -> 0x016e }
            int r11 = r11 - r10
            r14 = 1073741824(0x40000000, float:2.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x016e }
            int r15 = r11 - r15
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x016e }
            int r11 = r11 - r14
            int r14 = r15 + r10
            int r10 = r10 + r11
            r0.setBounds(r15, r11, r14, r10)     // Catch:{ all -> 0x016e }
            r0.draw(r13)     // Catch:{ all -> 0x016e }
            r13.setBitmap(r4)     // Catch:{ Exception -> 0x016c }
        L_0x016c:
            r9 = r12
            goto L_0x0172
        L_0x016e:
            r0 = move-exception
        L_0x016f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0254 }
        L_0x0172:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0254 }
            r10 = 26
            if (r0 < r10) goto L_0x01eb
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ Exception -> 0x0254 }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0254 }
            r10.<init>()     // Catch:{ Exception -> 0x0254 }
            java.lang.String r11 = "sdid_"
            r10.append(r11)     // Catch:{ Exception -> 0x0254 }
            r10.append(r1)     // Catch:{ Exception -> 0x0254 }
            java.lang.String r1 = r10.toString()     // Catch:{ Exception -> 0x0254 }
            r0.<init>((android.content.Context) r7, (java.lang.String) r1)     // Catch:{ Exception -> 0x0254 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setShortLabel(r8)     // Catch:{ Exception -> 0x0254 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setIntent(r3)     // Catch:{ Exception -> 0x0254 }
            if (r9 == 0) goto L_0x01a2
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithBitmap(r9)     // Catch:{ Exception -> 0x0254 }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x0254 }
            goto L_0x01e1
        L_0x01a2:
            if (r5 == 0) goto L_0x01c0
            boolean r1 = r5.bot     // Catch:{ Exception -> 0x0254 }
            if (r1 == 0) goto L_0x01b4
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            int r2 = org.telegram.messenger.R.drawable.book_bot     // Catch:{ Exception -> 0x0254 }
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithResource(r1, r2)     // Catch:{ Exception -> 0x0254 }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x0254 }
            goto L_0x01e1
        L_0x01b4:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            int r2 = org.telegram.messenger.R.drawable.book_user     // Catch:{ Exception -> 0x0254 }
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithResource(r1, r2)     // Catch:{ Exception -> 0x0254 }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x0254 }
            goto L_0x01e1
        L_0x01c0:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r6)     // Catch:{ Exception -> 0x0254 }
            if (r1 == 0) goto L_0x01d6
            boolean r1 = r6.megagroup     // Catch:{ Exception -> 0x0254 }
            if (r1 != 0) goto L_0x01d6
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            int r2 = org.telegram.messenger.R.drawable.book_channel     // Catch:{ Exception -> 0x0254 }
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithResource(r1, r2)     // Catch:{ Exception -> 0x0254 }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x0254 }
            goto L_0x01e1
        L_0x01d6:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            int r2 = org.telegram.messenger.R.drawable.book_group     // Catch:{ Exception -> 0x0254 }
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithResource(r1, r2)     // Catch:{ Exception -> 0x0254 }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x0254 }
        L_0x01e1:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            androidx.core.content.pm.ShortcutInfoCompat r0 = r0.build()     // Catch:{ Exception -> 0x0254 }
            androidx.core.content.pm.ShortcutManagerCompat.requestPinShortcut(r1, r0, r4)     // Catch:{ Exception -> 0x0254 }
            goto L_0x0258
        L_0x01eb:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0254 }
            r0.<init>()     // Catch:{ Exception -> 0x0254 }
            if (r9 == 0) goto L_0x01f8
            java.lang.String r1 = "android.intent.extra.shortcut.ICON"
            r0.putExtra(r1, r9)     // Catch:{ Exception -> 0x0254 }
            goto L_0x0239
        L_0x01f8:
            java.lang.String r1 = "android.intent.extra.shortcut.ICON_RESOURCE"
            if (r5 == 0) goto L_0x0218
            boolean r2 = r5.bot     // Catch:{ Exception -> 0x0254 }
            if (r2 == 0) goto L_0x020c
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            int r4 = org.telegram.messenger.R.drawable.book_bot     // Catch:{ Exception -> 0x0254 }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r4)     // Catch:{ Exception -> 0x0254 }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x0254 }
            goto L_0x0239
        L_0x020c:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            int r4 = org.telegram.messenger.R.drawable.book_user     // Catch:{ Exception -> 0x0254 }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r4)     // Catch:{ Exception -> 0x0254 }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x0254 }
            goto L_0x0239
        L_0x0218:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r6)     // Catch:{ Exception -> 0x0254 }
            if (r2 == 0) goto L_0x022e
            boolean r2 = r6.megagroup     // Catch:{ Exception -> 0x0254 }
            if (r2 != 0) goto L_0x022e
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            int r4 = org.telegram.messenger.R.drawable.book_channel     // Catch:{ Exception -> 0x0254 }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r4)     // Catch:{ Exception -> 0x0254 }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x0254 }
            goto L_0x0239
        L_0x022e:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            int r4 = org.telegram.messenger.R.drawable.book_group     // Catch:{ Exception -> 0x0254 }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r4)     // Catch:{ Exception -> 0x0254 }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x0254 }
        L_0x0239:
            java.lang.String r1 = "android.intent.extra.shortcut.INTENT"
            r0.putExtra(r1, r3)     // Catch:{ Exception -> 0x0254 }
            java.lang.String r1 = "android.intent.extra.shortcut.NAME"
            r0.putExtra(r1, r8)     // Catch:{ Exception -> 0x0254 }
            java.lang.String r1 = "duplicate"
            r0.putExtra(r1, r7)     // Catch:{ Exception -> 0x0254 }
            java.lang.String r1 = "com.android.launcher.action.INSTALL_SHORTCUT"
            r0.setAction(r1)     // Catch:{ Exception -> 0x0254 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0254 }
            r1.sendBroadcast(r0)     // Catch:{ Exception -> 0x0254 }
            goto L_0x0258
        L_0x0253:
            return
        L_0x0254:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0258:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.installShortcut(long):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x00a4 A[Catch:{ Exception -> 0x00d4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00ad A[Catch:{ Exception -> 0x00d4 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void uninstallShortcut(long r7) {
        /*
            r6 = this;
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00d4 }
            r1 = 26
            if (r0 < r1) goto L_0x004b
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x00d4 }
            r1.<init>()     // Catch:{ Exception -> 0x00d4 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4 }
            r2.<init>()     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r3 = "sdid_"
            r2.append(r3)     // Catch:{ Exception -> 0x00d4 }
            r2.append(r7)     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d4 }
            r1.add(r2)     // Catch:{ Exception -> 0x00d4 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4 }
            r2.<init>()     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r3 = "ndid_"
            r2.append(r3)     // Catch:{ Exception -> 0x00d4 }
            r2.append(r7)     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r7 = r2.toString()     // Catch:{ Exception -> 0x00d4 }
            r1.add(r7)     // Catch:{ Exception -> 0x00d4 }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00d4 }
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r7, r1)     // Catch:{ Exception -> 0x00d4 }
            r7 = 30
            if (r0 < r7) goto L_0x00d8
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00d4 }
            java.lang.Class<android.content.pm.ShortcutManager> r8 = android.content.pm.ShortcutManager.class
            java.lang.Object r7 = r7.getSystemService(r8)     // Catch:{ Exception -> 0x00d4 }
            android.content.pm.ShortcutManager r7 = (android.content.pm.ShortcutManager) r7     // Catch:{ Exception -> 0x00d4 }
            r7.removeLongLivedShortcuts(r1)     // Catch:{ Exception -> 0x00d4 }
            goto L_0x00d8
        L_0x004b:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)     // Catch:{ Exception -> 0x00d4 }
            r1 = 0
            if (r0 == 0) goto L_0x0074
            int r0 = org.telegram.messenger.DialogObject.getEncryptedChatId(r7)     // Catch:{ Exception -> 0x00d4 }
            org.telegram.messenger.MessagesController r2 = r6.getMessagesController()     // Catch:{ Exception -> 0x00d4 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x00d4 }
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r2.getEncryptedChat(r0)     // Catch:{ Exception -> 0x00d4 }
            if (r0 != 0) goto L_0x0065
            return
        L_0x0065:
            org.telegram.messenger.MessagesController r2 = r6.getMessagesController()     // Catch:{ Exception -> 0x00d4 }
            long r3 = r0.user_id     // Catch:{ Exception -> 0x00d4 }
            java.lang.Long r0 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x00d4 }
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)     // Catch:{ Exception -> 0x00d4 }
            goto L_0x0086
        L_0x0074:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r7)     // Catch:{ Exception -> 0x00d4 }
            if (r0 == 0) goto L_0x008a
            org.telegram.messenger.MessagesController r0 = r6.getMessagesController()     // Catch:{ Exception -> 0x00d4 }
            java.lang.Long r2 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x00d4 }
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)     // Catch:{ Exception -> 0x00d4 }
        L_0x0086:
            r5 = r1
            r1 = r0
            r0 = r5
            goto L_0x009d
        L_0x008a:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r7)     // Catch:{ Exception -> 0x00d4 }
            if (r0 == 0) goto L_0x00d3
            org.telegram.messenger.MessagesController r0 = r6.getMessagesController()     // Catch:{ Exception -> 0x00d4 }
            long r2 = -r7
            java.lang.Long r2 = java.lang.Long.valueOf(r2)     // Catch:{ Exception -> 0x00d4 }
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)     // Catch:{ Exception -> 0x00d4 }
        L_0x009d:
            if (r1 != 0) goto L_0x00a2
            if (r0 != 0) goto L_0x00a2
            return
        L_0x00a2:
            if (r1 == 0) goto L_0x00ad
            java.lang.String r0 = r1.first_name     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r1 = r1.last_name     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r0, r1)     // Catch:{ Exception -> 0x00d4 }
            goto L_0x00af
        L_0x00ad:
            java.lang.String r0 = r0.title     // Catch:{ Exception -> 0x00d4 }
        L_0x00af:
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x00d4 }
            r1.<init>()     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r2 = "android.intent.extra.shortcut.INTENT"
            android.content.Intent r7 = r6.createIntrnalShortcutIntent(r7)     // Catch:{ Exception -> 0x00d4 }
            r1.putExtra(r2, r7)     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r7 = "android.intent.extra.shortcut.NAME"
            r1.putExtra(r7, r0)     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r7 = "duplicate"
            r8 = 0
            r1.putExtra(r7, r8)     // Catch:{ Exception -> 0x00d4 }
            java.lang.String r7 = "com.android.launcher.action.UNINSTALL_SHORTCUT"
            r1.setAction(r7)     // Catch:{ Exception -> 0x00d4 }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00d4 }
            r7.sendBroadcast(r1)     // Catch:{ Exception -> 0x00d4 }
            goto L_0x00d8
        L_0x00d3:
            return
        L_0x00d4:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x00d8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.uninstallShortcut(long):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$138(TLRPC$MessageEntity tLRPC$MessageEntity, TLRPC$MessageEntity tLRPC$MessageEntity2) {
        int i = tLRPC$MessageEntity.offset;
        int i2 = tLRPC$MessageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void loadPinnedMessages(long j, int i, int i2) {
        if (this.loadingPinnedMessages.indexOfKey(j) < 0) {
            this.loadingPinnedMessages.put(j, Boolean.TRUE);
            TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
            tLRPC$TL_messages_search.peer = getMessagesController().getInputPeer(j);
            tLRPC$TL_messages_search.limit = 40;
            tLRPC$TL_messages_search.offset_id = i;
            tLRPC$TL_messages_search.q = "";
            tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterPinned();
            getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new MediaDataController$$ExternalSyntheticLambda176(this, i2, tLRPC$TL_messages_search, j, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPinnedMessages$140(int i, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        boolean z;
        int i3;
        int i4;
        TLObject tLObject2 = tLObject;
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        if (tLObject2 instanceof TLRPC$messages_Messages) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject2;
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i5 = 0; i5 < tLRPC$messages_Messages.users.size(); i5++) {
                TLRPC$User tLRPC$User = tLRPC$messages_Messages.users.get(i5);
                longSparseArray.put(tLRPC$User.id, tLRPC$User);
            }
            LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i6 = 0; i6 < tLRPC$messages_Messages.chats.size(); i6++) {
                TLRPC$Chat tLRPC$Chat = tLRPC$messages_Messages.chats.get(i6);
                longSparseArray2.put(tLRPC$Chat.id, tLRPC$Chat);
            }
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            getMessagesController().putUsers(tLRPC$messages_Messages.users, false);
            getMessagesController().putChats(tLRPC$messages_Messages.chats, false);
            int size = tLRPC$messages_Messages.messages.size();
            for (int i7 = 0; i7 < size; i7++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i7);
                if (!(tLRPC$Message instanceof TLRPC$TL_messageService) && !(tLRPC$Message instanceof TLRPC$TL_messageEmpty)) {
                    arrayList.add(Integer.valueOf(tLRPC$Message.id));
                    Integer valueOf = Integer.valueOf(tLRPC$Message.id);
                    MessageObject messageObject = r10;
                    MessageObject messageObject2 = new MessageObject(this.currentAccount, tLRPC$Message, (LongSparseArray<TLRPC$User>) longSparseArray, (LongSparseArray<TLRPC$Chat>) longSparseArray2, false, false);
                    hashMap.put(valueOf, messageObject);
                }
            }
            if (i != 0 && arrayList.isEmpty()) {
                arrayList.add(Integer.valueOf(i));
            }
            boolean z2 = tLRPC$messages_Messages.messages.size() < tLRPC$TL_messages_search.limit;
            i3 = Math.max(tLRPC$messages_Messages.count, tLRPC$messages_Messages.messages.size());
            z = z2;
        } else {
            if (i != 0) {
                arrayList.add(Integer.valueOf(i));
                i4 = 1;
            } else {
                i4 = 0;
            }
            i3 = i4;
            z = false;
        }
        long j2 = j;
        getMessagesStorage().updatePinnedMessages(j2, arrayList, true, i3, i2, z, hashMap);
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda35(this, j2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPinnedMessages$139(long j) {
        this.loadingPinnedMessages.remove(j);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPinnedMessages$141(long j, long j2, ArrayList arrayList) {
        loadPinnedMessageInternal(j, j2, arrayList, false);
    }

    public ArrayList<MessageObject> loadPinnedMessages(long j, long j2, ArrayList<Integer> arrayList, boolean z) {
        if (!z) {
            return loadPinnedMessageInternal(j, j2, arrayList, true);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda46(this, j, j2, arrayList));
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0176 A[Catch:{ Exception -> 0x01c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.ArrayList<org.telegram.messenger.MessageObject> loadPinnedMessageInternal(long r19, long r21, java.util.ArrayList<java.lang.Integer> r23, boolean r24) {
        /*
            r18 = this;
            r5 = r19
            r3 = r21
            r0 = r23
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x01cb }
            r1.<init>(r0)     // Catch:{ Exception -> 0x01cb }
            r9 = 0
            java.lang.String r11 = ","
            int r7 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r7 == 0) goto L_0x0034
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01cb }
            r7.<init>()     // Catch:{ Exception -> 0x01cb }
            int r12 = r23.size()     // Catch:{ Exception -> 0x01cb }
            r13 = 0
        L_0x001d:
            if (r13 >= r12) goto L_0x0038
            java.lang.Object r14 = r0.get(r13)     // Catch:{ Exception -> 0x01cb }
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ Exception -> 0x01cb }
            int r15 = r7.length()     // Catch:{ Exception -> 0x01cb }
            if (r15 == 0) goto L_0x002e
            r7.append(r11)     // Catch:{ Exception -> 0x01cb }
        L_0x002e:
            r7.append(r14)     // Catch:{ Exception -> 0x01cb }
            int r13 = r13 + 1
            goto L_0x001d
        L_0x0034:
            java.lang.String r7 = android.text.TextUtils.join(r11, r0)     // Catch:{ Exception -> 0x01cb }
        L_0x0038:
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x01cb }
            r0.<init>()     // Catch:{ Exception -> 0x01cb }
            java.util.ArrayList r12 = new java.util.ArrayList     // Catch:{ Exception -> 0x01cb }
            r12.<init>()     // Catch:{ Exception -> 0x01cb }
            java.util.ArrayList r13 = new java.util.ArrayList     // Catch:{ Exception -> 0x01cb }
            r13.<init>()     // Catch:{ Exception -> 0x01cb }
            java.util.ArrayList r14 = new java.util.ArrayList     // Catch:{ Exception -> 0x01cb }
            r14.<init>()     // Catch:{ Exception -> 0x01cb }
            java.util.ArrayList r15 = new java.util.ArrayList     // Catch:{ Exception -> 0x01cb }
            r15.<init>()     // Catch:{ Exception -> 0x01cb }
            org.telegram.messenger.UserConfig r9 = r18.getUserConfig()     // Catch:{ Exception -> 0x01cb }
            long r9 = r9.clientUserId     // Catch:{ Exception -> 0x01cb }
            org.telegram.messenger.MessagesStorage r16 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x01cb }
            org.telegram.SQLite.SQLiteDatabase r8 = r16.getDatabase()     // Catch:{ Exception -> 0x01cb }
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ Exception -> 0x01cb }
            r23 = r13
            java.lang.String r13 = "SELECT data, mid, date FROM messages_v2 WHERE mid IN (%s) AND uid = %d"
            r17 = r12
            r12 = 2
            java.lang.Object[] r3 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x01cb }
            r4 = 0
            r3[r4] = r7     // Catch:{ Exception -> 0x01cb }
            java.lang.Long r7 = java.lang.Long.valueOf(r19)     // Catch:{ Exception -> 0x01cb }
            r12 = 1
            r3[r12] = r7     // Catch:{ Exception -> 0x01cb }
            java.lang.String r2 = java.lang.String.format(r2, r13, r3)     // Catch:{ Exception -> 0x01cb }
            java.lang.Object[] r3 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x01cb }
            org.telegram.SQLite.SQLiteCursor r2 = r8.queryFinalized(r2, r3)     // Catch:{ Exception -> 0x01cb }
        L_0x007e:
            boolean r3 = r2.next()     // Catch:{ Exception -> 0x01cb }
            if (r3 == 0) goto L_0x00bf
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r4)     // Catch:{ Exception -> 0x01cb }
            if (r3 == 0) goto L_0x00bd
            int r7 = r3.readInt32(r4)     // Catch:{ Exception -> 0x01cb }
            org.telegram.tgnet.TLRPC$Message r7 = org.telegram.tgnet.TLRPC$Message.TLdeserialize(r3, r7, r4)     // Catch:{ Exception -> 0x01cb }
            org.telegram.tgnet.TLRPC$MessageAction r4 = r7.action     // Catch:{ Exception -> 0x01cb }
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear     // Catch:{ Exception -> 0x01cb }
            if (r4 != 0) goto L_0x00ba
            r7.readAttachPath(r3, r9)     // Catch:{ Exception -> 0x01cb }
            int r4 = r2.intValue(r12)     // Catch:{ Exception -> 0x01cb }
            r7.id = r4     // Catch:{ Exception -> 0x01cb }
            r4 = 2
            int r8 = r2.intValue(r4)     // Catch:{ Exception -> 0x01cb }
            r7.date = r8     // Catch:{ Exception -> 0x01cb }
            r7.dialog_id = r5     // Catch:{ Exception -> 0x01cb }
            r4 = 0
            org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r7, r14, r15, r4)     // Catch:{ Exception -> 0x01cb }
            r0.add(r7)     // Catch:{ Exception -> 0x01cb }
            int r4 = r7.id     // Catch:{ Exception -> 0x01cb }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x01cb }
            r1.remove(r4)     // Catch:{ Exception -> 0x01cb }
        L_0x00ba:
            r3.reuse()     // Catch:{ Exception -> 0x01cb }
        L_0x00bd:
            r4 = 0
            goto L_0x007e
        L_0x00bf:
            r2.dispose()     // Catch:{ Exception -> 0x01cb }
            boolean r2 = r1.isEmpty()     // Catch:{ Exception -> 0x01cb }
            if (r2 != 0) goto L_0x0124
            org.telegram.messenger.MessagesStorage r2 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x01cb }
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ Exception -> 0x01cb }
            java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x01cb }
            java.lang.String r4 = "SELECT data FROM chat_pinned_v2 WHERE uid = %d AND mid IN (%s)"
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x01cb }
            java.lang.Long r8 = java.lang.Long.valueOf(r19)     // Catch:{ Exception -> 0x01cb }
            r13 = 0
            r7[r13] = r8     // Catch:{ Exception -> 0x01cb }
            java.lang.String r8 = android.text.TextUtils.join(r11, r1)     // Catch:{ Exception -> 0x01cb }
            r7[r12] = r8     // Catch:{ Exception -> 0x01cb }
            java.lang.String r3 = java.lang.String.format(r3, r4, r7)     // Catch:{ Exception -> 0x01cb }
            java.lang.Object[] r4 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x01cb }
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch:{ Exception -> 0x01cb }
        L_0x00ee:
            boolean r3 = r2.next()     // Catch:{ Exception -> 0x01cb }
            if (r3 == 0) goto L_0x0121
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r13)     // Catch:{ Exception -> 0x01cb }
            if (r3 == 0) goto L_0x00ee
            int r4 = r3.readInt32(r13)     // Catch:{ Exception -> 0x01cb }
            org.telegram.tgnet.TLRPC$Message r4 = org.telegram.tgnet.TLRPC$Message.TLdeserialize(r3, r4, r13)     // Catch:{ Exception -> 0x01cb }
            org.telegram.tgnet.TLRPC$MessageAction r7 = r4.action     // Catch:{ Exception -> 0x01cb }
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear     // Catch:{ Exception -> 0x01cb }
            if (r7 != 0) goto L_0x011d
            r4.readAttachPath(r3, r9)     // Catch:{ Exception -> 0x01cb }
            r4.dialog_id = r5     // Catch:{ Exception -> 0x01cb }
            r7 = 0
            org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r4, r14, r15, r7)     // Catch:{ Exception -> 0x01cb }
            r0.add(r4)     // Catch:{ Exception -> 0x01cb }
            int r4 = r4.id     // Catch:{ Exception -> 0x01cb }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x01cb }
            r1.remove(r4)     // Catch:{ Exception -> 0x01cb }
        L_0x011d:
            r3.reuse()     // Catch:{ Exception -> 0x01cb }
            goto L_0x00ee
        L_0x0121:
            r2.dispose()     // Catch:{ Exception -> 0x01cb }
        L_0x0124:
            boolean r2 = r1.isEmpty()     // Catch:{ Exception -> 0x01cb }
            if (r2 != 0) goto L_0x016e
            r3 = r21
            r7 = 0
            int r2 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x0158
            org.telegram.tgnet.TLRPC$TL_channels_getMessages r8 = new org.telegram.tgnet.TLRPC$TL_channels_getMessages     // Catch:{ Exception -> 0x01cb }
            r8.<init>()     // Catch:{ Exception -> 0x01cb }
            org.telegram.messenger.MessagesController r2 = r18.getMessagesController()     // Catch:{ Exception -> 0x01cb }
            org.telegram.tgnet.TLRPC$InputChannel r2 = r2.getInputChannel((long) r3)     // Catch:{ Exception -> 0x01cb }
            r8.channel = r2     // Catch:{ Exception -> 0x01cb }
            r8.id = r1     // Catch:{ Exception -> 0x01cb }
            org.telegram.tgnet.ConnectionsManager r9 = r18.getConnectionsManager()     // Catch:{ Exception -> 0x01cb }
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda184 r10 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda184     // Catch:{ Exception -> 0x01cb }
            r1 = r10
            r2 = r18
            r3 = r21
            r5 = r19
            r7 = r8
            r1.<init>(r2, r3, r5, r7)     // Catch:{ Exception -> 0x01cb }
            r9.sendRequest(r8, r10)     // Catch:{ Exception -> 0x01cb }
            goto L_0x016e
        L_0x0158:
            org.telegram.tgnet.TLRPC$TL_messages_getMessages r2 = new org.telegram.tgnet.TLRPC$TL_messages_getMessages     // Catch:{ Exception -> 0x01cb }
            r2.<init>()     // Catch:{ Exception -> 0x01cb }
            r2.id = r1     // Catch:{ Exception -> 0x01cb }
            org.telegram.tgnet.ConnectionsManager r1 = r18.getConnectionsManager()     // Catch:{ Exception -> 0x01cb }
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda186 r3 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda186     // Catch:{ Exception -> 0x01cb }
            r4 = r18
            r3.<init>(r4, r5, r2)     // Catch:{ Exception -> 0x01c9 }
            r1.sendRequest(r2, r3)     // Catch:{ Exception -> 0x01c9 }
            goto L_0x0170
        L_0x016e:
            r4 = r18
        L_0x0170:
            boolean r1 = r0.isEmpty()     // Catch:{ Exception -> 0x01c9 }
            if (r1 != 0) goto L_0x01d1
            boolean r1 = r14.isEmpty()     // Catch:{ Exception -> 0x01c9 }
            if (r1 != 0) goto L_0x018a
            org.telegram.messenger.MessagesStorage r1 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x01c9 }
            java.lang.String r2 = android.text.TextUtils.join(r11, r14)     // Catch:{ Exception -> 0x01c9 }
            r3 = r17
            r1.getUsersInternal(r2, r3)     // Catch:{ Exception -> 0x01c9 }
            goto L_0x018c
        L_0x018a:
            r3 = r17
        L_0x018c:
            boolean r1 = r15.isEmpty()     // Catch:{ Exception -> 0x01c9 }
            if (r1 != 0) goto L_0x01a0
            org.telegram.messenger.MessagesStorage r1 = r18.getMessagesStorage()     // Catch:{ Exception -> 0x01c9 }
            java.lang.String r2 = android.text.TextUtils.join(r11, r15)     // Catch:{ Exception -> 0x01c9 }
            r5 = r23
            r1.getChatsInternal(r2, r5)     // Catch:{ Exception -> 0x01c9 }
            goto L_0x01a2
        L_0x01a0:
            r5 = r23
        L_0x01a2:
            if (r24 == 0) goto L_0x01b7
            r1 = 1
            r2 = 1
            r19 = r18
            r20 = r0
            r21 = r3
            r22 = r5
            r23 = r1
            r24 = r2
            java.util.ArrayList r0 = r19.broadcastPinnedMessage(r20, r21, r22, r23, r24)     // Catch:{ Exception -> 0x01c9 }
            return r0
        L_0x01b7:
            r1 = 1
            r2 = 0
            r19 = r18
            r20 = r0
            r21 = r3
            r22 = r5
            r23 = r1
            r24 = r2
            r19.broadcastPinnedMessage(r20, r21, r22, r23, r24)     // Catch:{ Exception -> 0x01c9 }
            goto L_0x01d1
        L_0x01c9:
            r0 = move-exception
            goto L_0x01ce
        L_0x01cb:
            r0 = move-exception
            r4 = r18
        L_0x01ce:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01d1:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadPinnedMessageInternal(long, long, java.util.ArrayList, boolean):java.util.ArrayList");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:9:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$142(long r13, long r15, org.telegram.tgnet.TLRPC$TL_channels_getMessages r17, org.telegram.tgnet.TLObject r18, org.telegram.tgnet.TLRPC$TL_error r19) {
        /*
            r12 = this;
            r0 = 1
            if (r19 != 0) goto L_0x0043
            r1 = r18
            org.telegram.tgnet.TLRPC$messages_Messages r1 = (org.telegram.tgnet.TLRPC$messages_Messages) r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r1.messages
            removeEmptyMessages(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r1.messages
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0043
            org.telegram.messenger.MessagesController r2 = r12.getMessagesController()
            java.lang.Long r3 = java.lang.Long.valueOf(r13)
            r2.getChat(r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r1.messages
            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r4 = r1.messages
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r1.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r6 = r1.chats
            r7 = 0
            r8 = 0
            r3 = r12
            r3.broadcastPinnedMessage(r4, r5, r6, r7, r8)
            org.telegram.messenger.MessagesStorage r2 = r12.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r1.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r1.chats
            r2.putUsersAndChats(r3, r4, r0, r0)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r1.messages
            r2 = r12
            r4 = r15
            r12.savePinnedMessages(r4, r1)
            goto L_0x0046
        L_0x0043:
            r2 = r12
            r4 = r15
            r0 = 0
        L_0x0046:
            if (r0 != 0) goto L_0x0059
            org.telegram.messenger.MessagesStorage r3 = r12.getMessagesStorage()
            r0 = r17
            java.util.ArrayList<java.lang.Integer> r6 = r0.id
            r7 = 0
            r8 = -1
            r9 = 0
            r10 = 0
            r11 = 0
            r4 = r15
            r3.updatePinnedMessages(r4, r6, r7, r8, r9, r10, r11)
        L_0x0059:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPinnedMessageInternal$142(long, long, org.telegram.tgnet.TLRPC$TL_channels_getMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:9:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$143(long r11, org.telegram.tgnet.TLRPC$TL_messages_getMessages r13, org.telegram.tgnet.TLObject r14, org.telegram.tgnet.TLRPC$TL_error r15) {
        /*
            r10 = this;
            r0 = 1
            if (r15 != 0) goto L_0x0034
            org.telegram.tgnet.TLRPC$messages_Messages r14 = (org.telegram.tgnet.TLRPC$messages_Messages) r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            removeEmptyMessages(r15)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            boolean r15 = r15.isEmpty()
            if (r15 != 0) goto L_0x0034
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            org.telegram.messenger.ImageLoader.saveMessagesThumbs(r15)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r14.messages
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r3 = r14.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r14.chats
            r5 = 0
            r6 = 0
            r1 = r10
            r1.broadcastPinnedMessage(r2, r3, r4, r5, r6)
            org.telegram.messenger.MessagesStorage r15 = r10.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r1 = r14.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r14.chats
            r15.putUsersAndChats(r1, r2, r0, r0)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r14 = r14.messages
            r10.savePinnedMessages(r11, r14)
            goto L_0x0035
        L_0x0034:
            r0 = 0
        L_0x0035:
            if (r0 != 0) goto L_0x0046
            org.telegram.messenger.MessagesStorage r1 = r10.getMessagesStorage()
            java.util.ArrayList<java.lang.Integer> r4 = r13.id
            r5 = 0
            r6 = -1
            r7 = 0
            r8 = 0
            r9 = 0
            r2 = r11
            r1.updatePinnedMessages(r2, r4, r5, r6, r7, r8, r9)
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPinnedMessageInternal$143(long, org.telegram.tgnet.TLRPC$TL_messages_getMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    private void savePinnedMessages(long j, ArrayList<TLRPC$Message> arrayList) {
        if (!arrayList.isEmpty()) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda73(this, arrayList, j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$savePinnedMessages$144(ArrayList arrayList, long j) {
        try {
            getMessagesStorage().getDatabase().beginTransaction();
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO chat_pinned_v2 VALUES(?, ?, ?)");
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Message.getObjectSize());
                tLRPC$Message.serializeToStream(nativeByteBuffer);
                executeFast.requery();
                executeFast.bindLong(1, j);
                executeFast.bindInteger(2, tLRPC$Message.id);
                executeFast.bindByteBuffer(3, nativeByteBuffer);
                executeFast.step();
                nativeByteBuffer.reuse();
            }
            executeFast.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private ArrayList<MessageObject> broadcastPinnedMessage(ArrayList<TLRPC$Message> arrayList, ArrayList<TLRPC$User> arrayList2, ArrayList<TLRPC$Chat> arrayList3, boolean z, boolean z2) {
        ArrayList<TLRPC$User> arrayList4 = arrayList2;
        ArrayList<TLRPC$Chat> arrayList5 = arrayList3;
        if (arrayList.isEmpty()) {
            return null;
        }
        LongSparseArray longSparseArray = new LongSparseArray();
        for (int i = 0; i < arrayList2.size(); i++) {
            TLRPC$User tLRPC$User = arrayList4.get(i);
            longSparseArray.put(tLRPC$User.id, tLRPC$User);
        }
        LongSparseArray longSparseArray2 = new LongSparseArray();
        for (int i2 = 0; i2 < arrayList3.size(); i2++) {
            TLRPC$Chat tLRPC$Chat = arrayList5.get(i2);
            longSparseArray2.put(tLRPC$Chat.id, tLRPC$Chat);
        }
        ArrayList<MessageObject> arrayList6 = new ArrayList<>();
        if (z2) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda79(this, arrayList4, z, arrayList5));
            int size = arrayList.size();
            int i3 = 0;
            int i4 = 0;
            while (i4 < size) {
                TLRPC$Message tLRPC$Message = arrayList.get(i4);
                if ((MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument) || (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto)) {
                    i3++;
                }
                int i5 = i3;
                MessageObject messageObject = r1;
                MessageObject messageObject2 = new MessageObject(this.currentAccount, tLRPC$Message, (LongSparseArray<TLRPC$User>) longSparseArray, (LongSparseArray<TLRPC$Chat>) longSparseArray2, false, i5 < 30);
                arrayList6.add(messageObject);
                i4++;
                i3 = i5;
            }
            return arrayList6;
        }
        ArrayList<TLRPC$Message> arrayList7 = arrayList;
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda81(this, arrayList2, z, arrayList3, arrayList, arrayList6, longSparseArray, longSparseArray2));
        return null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastPinnedMessage$145(ArrayList arrayList, boolean z, ArrayList arrayList2) {
        getMessagesController().putUsers(arrayList, z);
        getMessagesController().putChats(arrayList2, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastPinnedMessage$147(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        boolean z2 = z;
        ArrayList arrayList5 = arrayList4;
        getMessagesController().putUsers(arrayList, z2);
        getMessagesController().putChats(arrayList2, z2);
        int size = arrayList3.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList3.get(i2);
            if ((MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument) || (MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto)) {
                i++;
            }
            arrayList5.add(new MessageObject(this.currentAccount, tLRPC$Message, (LongSparseArray<TLRPC$User>) longSparseArray, (LongSparseArray<TLRPC$Chat>) longSparseArray2, false, i < 30));
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda69(this, arrayList5));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastPinnedMessage$146(ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.didLoadPinnedMessages, Long.valueOf(((MessageObject) arrayList.get(0)).getDialogId()), null, Boolean.TRUE, arrayList, null, 0, -1, Boolean.FALSE);
    }

    private static void removeEmptyMessages(ArrayList<TLRPC$Message> arrayList) {
        int i = 0;
        while (i < arrayList.size()) {
            TLRPC$Message tLRPC$Message = arrayList.get(i);
            if (tLRPC$Message == null || (tLRPC$Message instanceof TLRPC$TL_messageEmpty) || (tLRPC$Message.action instanceof TLRPC$TL_messageActionHistoryClear)) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00b6, code lost:
        if (r12 != 0) goto L_0x00c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00bf, code lost:
        if (r12 != 0) goto L_0x00c1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadReplyMessagesForMessages(java.util.ArrayList<org.telegram.messenger.MessageObject> r15, long r16, boolean r18, java.lang.Runnable r19) {
        /*
            r14 = this;
            r0 = r15
            r5 = r16
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r16)
            r2 = 0
            if (r1 == 0) goto L_0x0080
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            androidx.collection.LongSparseArray r7 = new androidx.collection.LongSparseArray
            r7.<init>()
        L_0x0014:
            int r1 = r15.size()
            if (r2 >= r1) goto L_0x005a
            java.lang.Object r1 = r15.get(r2)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 != 0) goto L_0x0023
            goto L_0x0057
        L_0x0023:
            boolean r4 = r1.isReply()
            if (r4 == 0) goto L_0x0057
            org.telegram.messenger.MessageObject r4 = r1.replyMessageObject
            if (r4 != 0) goto L_0x0057
            org.telegram.tgnet.TLRPC$Message r4 = r1.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = r4.reply_to
            long r8 = r4.reply_to_random_id
            java.lang.Object r4 = r7.get(r8)
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            if (r4 != 0) goto L_0x0043
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r7.put(r8, r4)
        L_0x0043:
            r4.add(r1)
            java.lang.Long r1 = java.lang.Long.valueOf(r8)
            boolean r1 = r3.contains(r1)
            if (r1 != 0) goto L_0x0057
            java.lang.Long r1 = java.lang.Long.valueOf(r8)
            r3.add(r1)
        L_0x0057:
            int r2 = r2 + 1
            goto L_0x0014
        L_0x005a:
            boolean r0 = r3.isEmpty()
            if (r0 == 0) goto L_0x0066
            if (r19 == 0) goto L_0x0065
            r19.run()
        L_0x0065:
            return
        L_0x0066:
            org.telegram.messenger.MessagesStorage r0 = r14.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r8 = r0.getStorageQueue()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda75 r9 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda75
            r0 = r9
            r1 = r14
            r2 = r3
            r3 = r16
            r5 = r7
            r6 = r19
            r0.<init>(r1, r2, r3, r5, r6)
            r8.postRunnable(r9)
            goto L_0x0148
        L_0x0080:
            androidx.collection.LongSparseArray r3 = new androidx.collection.LongSparseArray
            r3.<init>()
            androidx.collection.LongSparseArray r4 = new androidx.collection.LongSparseArray
            r4.<init>()
        L_0x008a:
            int r1 = r15.size()
            if (r2 >= r1) goto L_0x0122
            java.lang.Object r1 = r15.get(r2)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 != 0) goto L_0x009a
            goto L_0x011e
        L_0x009a:
            int r7 = r1.getId()
            if (r7 <= 0) goto L_0x011e
            boolean r7 = r1.isReply()
            if (r7 == 0) goto L_0x011e
            org.telegram.tgnet.TLRPC$Message r7 = r1.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r8 = r7.reply_to
            int r9 = r8.reply_to_msg_id
            org.telegram.tgnet.TLRPC$Peer r8 = r8.reply_to_peer_id
            r10 = 0
            if (r8 == 0) goto L_0x00b9
            long r12 = r8.channel_id
            int r8 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r8 == 0) goto L_0x00c2
            goto L_0x00c1
        L_0x00b9:
            org.telegram.tgnet.TLRPC$Peer r8 = r7.peer_id
            long r12 = r8.channel_id
            int r8 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r8 == 0) goto L_0x00c2
        L_0x00c1:
            r10 = r12
        L_0x00c2:
            org.telegram.messenger.MessageObject r8 = r1.replyMessageObject
            if (r8 == 0) goto L_0x00da
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            if (r8 == 0) goto L_0x011e
            org.telegram.tgnet.TLRPC$Peer r8 = r8.peer_id
            if (r8 == 0) goto L_0x011e
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r7 == 0) goto L_0x00d3
            goto L_0x011e
        L_0x00d3:
            long r7 = r8.channel_id
            int r12 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x00da
            goto L_0x011e
        L_0x00da:
            java.lang.Object r7 = r3.get(r5)
            android.util.SparseArray r7 = (android.util.SparseArray) r7
            java.lang.Object r8 = r4.get(r10)
            java.util.ArrayList r8 = (java.util.ArrayList) r8
            if (r7 != 0) goto L_0x00f0
            android.util.SparseArray r7 = new android.util.SparseArray
            r7.<init>()
            r3.put(r5, r7)
        L_0x00f0:
            if (r8 != 0) goto L_0x00fa
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r4.put(r10, r8)
        L_0x00fa:
            java.lang.Object r10 = r7.get(r9)
            java.util.ArrayList r10 = (java.util.ArrayList) r10
            if (r10 != 0) goto L_0x011b
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            r7.put(r9, r10)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r9)
            boolean r7 = r8.contains(r7)
            if (r7 != 0) goto L_0x011b
            java.lang.Integer r7 = java.lang.Integer.valueOf(r9)
            r8.add(r7)
        L_0x011b:
            r10.add(r1)
        L_0x011e:
            int r2 = r2 + 1
            goto L_0x008a
        L_0x0122:
            boolean r0 = r3.isEmpty()
            if (r0 == 0) goto L_0x012e
            if (r19 == 0) goto L_0x012d
            r19.run()
        L_0x012d:
            return
        L_0x012e:
            org.telegram.messenger.MessagesStorage r0 = r14.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r8 = r0.getStorageQueue()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda55 r9 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda55
            r0 = r9
            r1 = r14
            r2 = r3
            r3 = r4
            r4 = r18
            r5 = r16
            r7 = r19
            r0.<init>(r1, r2, r3, r4, r5, r7)
            r8.postRunnable(r9)
        L_0x0148:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadReplyMessagesForMessages(java.util.ArrayList, long, boolean, java.lang.Runnable):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$149(ArrayList arrayList, long j, LongSparseArray longSparseArray, Runnable runnable) {
        try {
            ArrayList arrayList2 = new ArrayList();
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms_v2 as r INNER JOIN messages_v2 as m ON r.mid = m.mid AND r.uid = m.uid WHERE r.random_id IN(%s)", new Object[]{TextUtils.join(",", arrayList)}), new Object[0]);
            while (queryFinalized.next()) {
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                if (byteBufferValue != null) {
                    TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                    byteBufferValue.reuse();
                    TLdeserialize.id = queryFinalized.intValue(1);
                    TLdeserialize.date = queryFinalized.intValue(2);
                    TLdeserialize.dialog_id = j;
                    long longValue = queryFinalized.longValue(3);
                    ArrayList arrayList3 = (ArrayList) longSparseArray.get(longValue);
                    longSparseArray.remove(longValue);
                    if (arrayList3 != null) {
                        MessageObject messageObject = new MessageObject(this.currentAccount, TLdeserialize, false, false);
                        arrayList2.add(messageObject);
                        for (int i = 0; i < arrayList3.size(); i++) {
                            MessageObject messageObject2 = (MessageObject) arrayList3.get(i);
                            messageObject2.replyMessageObject = messageObject;
                            messageObject2.messageOwner.reply_to = new TLRPC$TL_messageReplyHeader();
                            messageObject2.messageOwner.reply_to.reply_to_msg_id = messageObject.getId();
                        }
                    }
                }
            }
            queryFinalized.dispose();
            if (longSparseArray.size() != 0) {
                for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                    ArrayList arrayList4 = (ArrayList) longSparseArray.valueAt(i2);
                    for (int i3 = 0; i3 < arrayList4.size(); i3++) {
                        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = ((MessageObject) arrayList4.get(i3)).messageOwner.reply_to;
                        if (tLRPC$TL_messageReplyHeader != null) {
                            tLRPC$TL_messageReplyHeader.reply_to_random_id = 0;
                        }
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda47(this, j, arrayList2));
            if (runnable != null) {
                runnable.run();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$148(long j, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j), arrayList, null);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$153(LongSparseArray longSparseArray, LongSparseArray longSparseArray2, boolean z, long j, Runnable runnable) {
        int i;
        int i2;
        boolean z2;
        SQLiteCursor sQLiteCursor;
        LongSparseArray longSparseArray3 = longSparseArray;
        LongSparseArray longSparseArray4 = longSparseArray2;
        long j2 = j;
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            int i3 = 0;
            for (int size = longSparseArray.size(); i3 < size; size = i2) {
                long keyAt = longSparseArray3.keyAt(i3);
                SparseArray sparseArray = (SparseArray) longSparseArray3.valueAt(i3);
                ArrayList arrayList6 = (ArrayList) longSparseArray4.get(keyAt);
                if (arrayList6 == null) {
                    i2 = size;
                } else {
                    if (z) {
                        i2 = size;
                        sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM scheduled_messages_v2 WHERE mid IN(%s) AND uid = %d", new Object[]{TextUtils.join(",", arrayList6), Long.valueOf(j)}), new Object[0]);
                        z2 = false;
                    } else {
                        i2 = size;
                        SQLiteDatabase database = getMessagesStorage().getDatabase();
                        Locale locale = Locale.US;
                        String join = TextUtils.join(",", arrayList6);
                        z2 = false;
                        sQLiteCursor = database.queryFinalized(String.format(locale, "SELECT data, mid, date, uid FROM messages_v2 WHERE mid IN(%s) AND uid = %d", new Object[]{join, Long.valueOf(j)}), new Object[0]);
                    }
                    while (sQLiteCursor.next()) {
                        NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(z2 ? 1 : 0);
                        if (byteBufferValue != null) {
                            TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(z2), z2);
                            TLdeserialize.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                            byteBufferValue.reuse();
                            TLdeserialize.id = sQLiteCursor.intValue(1);
                            TLdeserialize.date = sQLiteCursor.intValue(2);
                            TLdeserialize.dialog_id = j2;
                            MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList4, arrayList5, (ArrayList<Long>) null);
                            arrayList.add(TLdeserialize);
                            TLRPC$Peer tLRPC$Peer = TLdeserialize.peer_id;
                            long j3 = tLRPC$Peer != null ? tLRPC$Peer.channel_id : 0;
                            ArrayList arrayList7 = (ArrayList) longSparseArray4.get(j3);
                            if (arrayList7 != null) {
                                arrayList7.remove(Integer.valueOf(TLdeserialize.id));
                                if (arrayList7.isEmpty()) {
                                    longSparseArray4.remove(j3);
                                }
                            }
                        }
                        z2 = false;
                    }
                    sQLiteCursor.dispose();
                }
                i3++;
                longSparseArray3 = longSparseArray;
            }
            if (!arrayList4.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", arrayList4), arrayList2);
            }
            if (!arrayList5.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList5), arrayList3);
            }
            broadcastReplyMessages(arrayList, longSparseArray, arrayList2, arrayList3, j, true);
            if (!longSparseArray2.isEmpty()) {
                int size2 = longSparseArray2.size();
                int i4 = 0;
                while (i4 < size2) {
                    long keyAt2 = longSparseArray4.keyAt(i4);
                    if (z) {
                        TLRPC$TL_messages_getScheduledMessages tLRPC$TL_messages_getScheduledMessages = new TLRPC$TL_messages_getScheduledMessages();
                        tLRPC$TL_messages_getScheduledMessages.peer = getMessagesController().getInputPeer(j2);
                        tLRPC$TL_messages_getScheduledMessages.id = (ArrayList) longSparseArray4.valueAt(i4);
                        ConnectionsManager connectionsManager = getConnectionsManager();
                        i = size2;
                        MediaDataController$$ExternalSyntheticLambda183 mediaDataController$$ExternalSyntheticLambda183 = r1;
                        MediaDataController$$ExternalSyntheticLambda183 mediaDataController$$ExternalSyntheticLambda1832 = new MediaDataController$$ExternalSyntheticLambda183(this, j, keyAt2, longSparseArray, z, runnable);
                        connectionsManager.sendRequest(tLRPC$TL_messages_getScheduledMessages, mediaDataController$$ExternalSyntheticLambda183);
                    } else {
                        i = size2;
                        if (keyAt2 != 0) {
                            TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
                            tLRPC$TL_channels_getMessages.channel = getMessagesController().getInputChannel(keyAt2);
                            tLRPC$TL_channels_getMessages.id = (ArrayList) longSparseArray4.valueAt(i4);
                            MediaDataController$$ExternalSyntheticLambda182 mediaDataController$$ExternalSyntheticLambda182 = r1;
                            ConnectionsManager connectionsManager2 = getConnectionsManager();
                            MediaDataController$$ExternalSyntheticLambda182 mediaDataController$$ExternalSyntheticLambda1822 = new MediaDataController$$ExternalSyntheticLambda182(this, j, keyAt2, longSparseArray, z, runnable);
                            connectionsManager2.sendRequest(tLRPC$TL_channels_getMessages, mediaDataController$$ExternalSyntheticLambda182);
                        } else {
                            TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages = new TLRPC$TL_messages_getMessages();
                            tLRPC$TL_messages_getMessages.id = (ArrayList) longSparseArray4.valueAt(i4);
                            getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages, new MediaDataController$$ExternalSyntheticLambda185(this, j, longSparseArray, z, runnable));
                        }
                    }
                    i4++;
                    size2 = i;
                }
            } else if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$150(long j, long j2, LongSparseArray longSparseArray, boolean z, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            for (int i = 0; i < tLRPC$messages_Messages.messages.size(); i++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i);
                long j3 = j;
                if (tLRPC$Message.dialog_id == 0) {
                    tLRPC$Message.dialog_id = j3;
                }
            }
            long j4 = j;
            MessageObject.fixMessagePeer(tLRPC$messages_Messages.messages, j2);
            ImageLoader.saveMessagesThumbs(tLRPC$messages_Messages.messages);
            broadcastReplyMessages(tLRPC$messages_Messages.messages, longSparseArray, tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, j, false);
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            saveReplyMessages(longSparseArray, tLRPC$messages_Messages.messages, z);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$151(long j, long j2, LongSparseArray longSparseArray, boolean z, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            for (int i = 0; i < tLRPC$messages_Messages.messages.size(); i++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i);
                long j3 = j;
                if (tLRPC$Message.dialog_id == 0) {
                    tLRPC$Message.dialog_id = j3;
                }
            }
            long j4 = j;
            MessageObject.fixMessagePeer(tLRPC$messages_Messages.messages, j2);
            ImageLoader.saveMessagesThumbs(tLRPC$messages_Messages.messages);
            broadcastReplyMessages(tLRPC$messages_Messages.messages, longSparseArray, tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, j, false);
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            saveReplyMessages(longSparseArray, tLRPC$messages_Messages.messages, z);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$152(long j, LongSparseArray longSparseArray, boolean z, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            for (int i = 0; i < tLRPC$messages_Messages.messages.size(); i++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i);
                if (tLRPC$Message.dialog_id == 0) {
                    tLRPC$Message.dialog_id = j;
                }
            }
            ImageLoader.saveMessagesThumbs(tLRPC$messages_Messages.messages);
            broadcastReplyMessages(tLRPC$messages_Messages.messages, longSparseArray, tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, j, false);
            getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
            saveReplyMessages(longSparseArray, tLRPC$messages_Messages.messages, z);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    private void saveReplyMessages(LongSparseArray<SparseArray<ArrayList<MessageObject>>> longSparseArray, ArrayList<TLRPC$Message> arrayList, boolean z) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda125(this, z, arrayList, longSparseArray));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveReplyMessages$154(boolean z, ArrayList arrayList, LongSparseArray longSparseArray) {
        SQLitePreparedStatement sQLitePreparedStatement;
        try {
            getMessagesStorage().getDatabase().beginTransaction();
            if (z) {
                sQLitePreparedStatement = getMessagesStorage().getDatabase().executeFast("UPDATE scheduled_messages_v2 SET replydata = ?, reply_to_message_id = ? WHERE mid = ? AND uid = ?");
            } else {
                sQLitePreparedStatement = getMessagesStorage().getDatabase().executeFast("UPDATE messages_v2 SET replydata = ?, reply_to_message_id = ? WHERE mid = ? AND uid = ?");
            }
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i);
                SparseArray sparseArray = (SparseArray) longSparseArray.get(MessageObject.getDialogId(tLRPC$Message));
                if (sparseArray != null) {
                    ArrayList arrayList2 = (ArrayList) sparseArray.get(tLRPC$Message.id);
                    if (arrayList2 != null) {
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Message.getObjectSize());
                        tLRPC$Message.serializeToStream(nativeByteBuffer);
                        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                            MessageObject messageObject = (MessageObject) arrayList2.get(i2);
                            sQLitePreparedStatement.requery();
                            sQLitePreparedStatement.bindByteBuffer(1, nativeByteBuffer);
                            sQLitePreparedStatement.bindInteger(2, tLRPC$Message.id);
                            sQLitePreparedStatement.bindInteger(3, messageObject.getId());
                            sQLitePreparedStatement.bindLong(4, messageObject.getDialogId());
                            sQLitePreparedStatement.step();
                        }
                        nativeByteBuffer.reuse();
                    }
                }
            }
            sQLitePreparedStatement.dispose();
            getMessagesStorage().getDatabase().commitTransaction();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void broadcastReplyMessages(ArrayList<TLRPC$Message> arrayList, LongSparseArray<SparseArray<ArrayList<MessageObject>>> longSparseArray, ArrayList<TLRPC$User> arrayList2, ArrayList<TLRPC$Chat> arrayList3, long j, boolean z) {
        LongSparseArray longSparseArray2 = new LongSparseArray();
        for (int i = 0; i < arrayList2.size(); i++) {
            TLRPC$User tLRPC$User = arrayList2.get(i);
            longSparseArray2.put(tLRPC$User.id, tLRPC$User);
        }
        ArrayList<TLRPC$User> arrayList4 = arrayList2;
        LongSparseArray longSparseArray3 = new LongSparseArray();
        for (int i2 = 0; i2 < arrayList3.size(); i2++) {
            TLRPC$Chat tLRPC$Chat = arrayList3.get(i2);
            longSparseArray3.put(tLRPC$Chat.id, tLRPC$Chat);
        }
        ArrayList<TLRPC$Chat> arrayList5 = arrayList3;
        ArrayList arrayList6 = new ArrayList();
        int size = arrayList.size();
        for (int i3 = 0; i3 < size; i3++) {
            arrayList6.add(new MessageObject(this.currentAccount, arrayList.get(i3), (LongSparseArray<TLRPC$User>) longSparseArray2, (LongSparseArray<TLRPC$Chat>) longSparseArray3, false, false));
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda80(this, arrayList2, z, arrayList3, arrayList6, longSparseArray, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastReplyMessages$155(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray, long j) {
        ArrayList arrayList4;
        getMessagesController().putUsers(arrayList, z);
        getMessagesController().putChats(arrayList2, z);
        int size = arrayList3.size();
        boolean z2 = false;
        for (int i = 0; i < size; i++) {
            MessageObject messageObject = (MessageObject) arrayList3.get(i);
            SparseArray sparseArray = (SparseArray) longSparseArray.get(messageObject.getDialogId());
            if (!(sparseArray == null || (arrayList4 = (ArrayList) sparseArray.get(messageObject.getId())) == null)) {
                for (int i2 = 0; i2 < arrayList4.size(); i2++) {
                    MessageObject messageObject2 = (MessageObject) arrayList4.get(i2);
                    messageObject2.replyMessageObject = messageObject;
                    TLRPC$MessageAction tLRPC$MessageAction = messageObject2.messageOwner.action;
                    if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPinMessage) {
                        messageObject2.generatePinMessageText((TLRPC$User) null, (TLRPC$Chat) null);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionGameScore) {
                        messageObject2.generateGameMessageText((TLRPC$User) null);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPaymentSent) {
                        messageObject2.generatePaymentSentMessageText((TLRPC$User) null);
                    }
                }
                z2 = true;
            }
        }
        if (z2) {
            getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j), arrayList3, longSparseArray);
        }
    }

    public static void sortEntities(ArrayList<TLRPC$MessageEntity> arrayList) {
        Collections.sort(arrayList, entityComparator);
    }

    private static boolean checkInclusion(int i, List<TLRPC$MessageEntity> list, boolean z) {
        if (list != null && !list.isEmpty()) {
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$MessageEntity tLRPC$MessageEntity = list.get(i2);
                int i3 = tLRPC$MessageEntity.offset;
                if (z) {
                    if (i3 >= i) {
                        continue;
                    }
                } else if (i3 > i) {
                    continue;
                }
                if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length > i) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkIntersection(int i, int i2, List<TLRPC$MessageEntity> list) {
        if (list != null && !list.isEmpty()) {
            int size = list.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC$MessageEntity tLRPC$MessageEntity = list.get(i3);
                int i4 = tLRPC$MessageEntity.offset;
                if (i4 > i && i4 + tLRPC$MessageEntity.length <= i2) {
                    return true;
                }
            }
        }
        return false;
    }

    public CharSequence substring(CharSequence charSequence, int i, int i2) {
        if (charSequence instanceof SpannableStringBuilder) {
            return charSequence.subSequence(i, i2);
        }
        if (charSequence instanceof SpannedString) {
            return charSequence.subSequence(i, i2);
        }
        return TextUtils.substring(charSequence, i, i2);
    }

    private static CharacterStyle createNewSpan(CharacterStyle characterStyle, TextStyleSpan.TextStyleRun textStyleRun, TextStyleSpan.TextStyleRun textStyleRun2, boolean z) {
        TextStyleSpan.TextStyleRun textStyleRun3 = new TextStyleSpan.TextStyleRun(textStyleRun);
        if (textStyleRun2 != null) {
            if (z) {
                textStyleRun3.merge(textStyleRun2);
            } else {
                textStyleRun3.replace(textStyleRun2);
            }
        }
        if (characterStyle instanceof TextStyleSpan) {
            return new TextStyleSpan(textStyleRun3);
        }
        if (characterStyle instanceof URLSpanReplacement) {
            return new URLSpanReplacement(((URLSpanReplacement) characterStyle).getURL(), textStyleRun3);
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x008a A[Catch:{ Exception -> 0x00c0 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void addStyleToText(org.telegram.ui.Components.TextStyleSpan r11, int r12, int r13, android.text.Spannable r14, boolean r15) {
        /*
            java.lang.Class<android.text.style.CharacterStyle> r0 = android.text.style.CharacterStyle.class
            java.lang.Object[] r0 = r14.getSpans(r12, r13, r0)     // Catch:{ Exception -> 0x00c0 }
            android.text.style.CharacterStyle[] r0 = (android.text.style.CharacterStyle[]) r0     // Catch:{ Exception -> 0x00c0 }
            r1 = 33
            if (r0 == 0) goto L_0x00aa
            int r2 = r0.length     // Catch:{ Exception -> 0x00c0 }
            if (r2 <= 0) goto L_0x00aa
            r2 = 0
        L_0x0010:
            int r3 = r0.length     // Catch:{ Exception -> 0x00c0 }
            if (r2 >= r3) goto L_0x00aa
            r3 = r0[r2]     // Catch:{ Exception -> 0x00c0 }
            if (r11 == 0) goto L_0x001c
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = r11.getTextStyleRun()     // Catch:{ Exception -> 0x00c0 }
            goto L_0x0021
        L_0x001c:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x00c0 }
            r4.<init>()     // Catch:{ Exception -> 0x00c0 }
        L_0x0021:
            boolean r5 = r3 instanceof org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x00c0 }
            if (r5 == 0) goto L_0x002d
            r5 = r3
            org.telegram.ui.Components.TextStyleSpan r5 = (org.telegram.ui.Components.TextStyleSpan) r5     // Catch:{ Exception -> 0x00c0 }
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = r5.getTextStyleRun()     // Catch:{ Exception -> 0x00c0 }
            goto L_0x003f
        L_0x002d:
            boolean r5 = r3 instanceof org.telegram.ui.Components.URLSpanReplacement     // Catch:{ Exception -> 0x00c0 }
            if (r5 == 0) goto L_0x00a6
            r5 = r3
            org.telegram.ui.Components.URLSpanReplacement r5 = (org.telegram.ui.Components.URLSpanReplacement) r5     // Catch:{ Exception -> 0x00c0 }
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = r5.getTextStyleRun()     // Catch:{ Exception -> 0x00c0 }
            if (r5 != 0) goto L_0x003f
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x00c0 }
            r5.<init>()     // Catch:{ Exception -> 0x00c0 }
        L_0x003f:
            if (r5 != 0) goto L_0x0043
            goto L_0x00a6
        L_0x0043:
            int r6 = r14.getSpanStart(r3)     // Catch:{ Exception -> 0x00c0 }
            int r7 = r14.getSpanEnd(r3)     // Catch:{ Exception -> 0x00c0 }
            r14.removeSpan(r3)     // Catch:{ Exception -> 0x00c0 }
            if (r6 <= r12) goto L_0x006a
            if (r13 <= r7) goto L_0x006a
            android.text.style.CharacterStyle r3 = createNewSpan(r3, r5, r4, r15)     // Catch:{ Exception -> 0x00c0 }
            r14.setSpan(r3, r6, r7, r1)     // Catch:{ Exception -> 0x00c0 }
            if (r11 == 0) goto L_0x0068
            org.telegram.ui.Components.TextStyleSpan r3 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x00c0 }
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x00c0 }
            r5.<init>(r4)     // Catch:{ Exception -> 0x00c0 }
            r3.<init>(r5)     // Catch:{ Exception -> 0x00c0 }
            r14.setSpan(r3, r7, r13, r1)     // Catch:{ Exception -> 0x00c0 }
        L_0x0068:
            r13 = r6
            goto L_0x00a6
        L_0x006a:
            r8 = 0
            if (r6 > r12) goto L_0x0087
            if (r6 == r12) goto L_0x0076
            android.text.style.CharacterStyle r9 = createNewSpan(r3, r5, r8, r15)     // Catch:{ Exception -> 0x00c0 }
            r14.setSpan(r9, r6, r12, r1)     // Catch:{ Exception -> 0x00c0 }
        L_0x0076:
            if (r7 <= r12) goto L_0x0087
            if (r11 == 0) goto L_0x0085
            android.text.style.CharacterStyle r9 = createNewSpan(r3, r5, r4, r15)     // Catch:{ Exception -> 0x00c0 }
            int r10 = java.lang.Math.min(r7, r13)     // Catch:{ Exception -> 0x00c0 }
            r14.setSpan(r9, r12, r10, r1)     // Catch:{ Exception -> 0x00c0 }
        L_0x0085:
            r9 = r7
            goto L_0x0088
        L_0x0087:
            r9 = r12
        L_0x0088:
            if (r7 < r13) goto L_0x00a5
            if (r7 == r13) goto L_0x0093
            android.text.style.CharacterStyle r8 = createNewSpan(r3, r5, r8, r15)     // Catch:{ Exception -> 0x00c0 }
            r14.setSpan(r8, r13, r7, r1)     // Catch:{ Exception -> 0x00c0 }
        L_0x0093:
            if (r13 <= r6) goto L_0x00a5
            if (r7 > r12) goto L_0x00a5
            if (r11 == 0) goto L_0x00a4
            android.text.style.CharacterStyle r12 = createNewSpan(r3, r5, r4, r15)     // Catch:{ Exception -> 0x00c0 }
            int r13 = java.lang.Math.min(r7, r13)     // Catch:{ Exception -> 0x00c0 }
            r14.setSpan(r12, r6, r13, r1)     // Catch:{ Exception -> 0x00c0 }
        L_0x00a4:
            r13 = r6
        L_0x00a5:
            r12 = r9
        L_0x00a6:
            int r2 = r2 + 1
            goto L_0x0010
        L_0x00aa:
            if (r11 == 0) goto L_0x00c4
            if (r12 >= r13) goto L_0x00c4
            int r15 = r14.length()     // Catch:{ Exception -> 0x00c0 }
            if (r12 >= r15) goto L_0x00c4
            int r15 = r14.length()     // Catch:{ Exception -> 0x00c0 }
            int r13 = java.lang.Math.min(r15, r13)     // Catch:{ Exception -> 0x00c0 }
            r14.setSpan(r11, r12, r13, r1)     // Catch:{ Exception -> 0x00c0 }
            goto L_0x00c4
        L_0x00c0:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
        L_0x00c4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.addStyleToText(org.telegram.ui.Components.TextStyleSpan, int, int, android.text.Spannable, boolean):void");
    }

    public static void addTextStyleRuns(MessageObject messageObject, Spannable spannable) {
        addTextStyleRuns(messageObject.messageOwner.entities, messageObject.messageText, spannable, -1);
    }

    public static void addTextStyleRuns(TLRPC$DraftMessage tLRPC$DraftMessage, Spannable spannable, int i) {
        addTextStyleRuns(tLRPC$DraftMessage.entities, tLRPC$DraftMessage.message, spannable, i);
    }

    public static void addTextStyleRuns(MessageObject messageObject, Spannable spannable, int i) {
        addTextStyleRuns(messageObject.messageOwner.entities, messageObject.messageText, spannable, i);
    }

    public static void addTextStyleRuns(ArrayList<TLRPC$MessageEntity> arrayList, CharSequence charSequence, Spannable spannable) {
        addTextStyleRuns(arrayList, charSequence, spannable, -1);
    }

    public static void addTextStyleRuns(ArrayList<TLRPC$MessageEntity> arrayList, CharSequence charSequence, Spannable spannable, int i) {
        for (TextStyleSpan removeSpan : (TextStyleSpan[]) spannable.getSpans(0, spannable.length(), TextStyleSpan.class)) {
            spannable.removeSpan(removeSpan);
        }
        Iterator<TextStyleSpan.TextStyleRun> it = getTextStyleRuns(arrayList, charSequence, i).iterator();
        while (it.hasNext()) {
            TextStyleSpan.TextStyleRun next = it.next();
            addStyleToText(new TextStyleSpan(next), next.start, next.end, spannable, true);
        }
    }

    public static void addAnimatedEmojiSpans(ArrayList<TLRPC$MessageEntity> arrayList, CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt) {
        AnimatedEmojiSpan animatedEmojiSpan;
        if ((charSequence instanceof Spannable) && arrayList != null) {
            Spannable spannable = (Spannable) charSequence;
            AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannable.getSpans(0, spannable.length(), AnimatedEmojiSpan.class);
            for (AnimatedEmojiSpan animatedEmojiSpan2 : animatedEmojiSpanArr) {
                if (animatedEmojiSpan2 != null) {
                    spannable.removeSpan(animatedEmojiSpan2);
                }
            }
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$MessageEntity tLRPC$MessageEntity = arrayList.get(i);
                if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCustomEmoji) {
                    TLRPC$TL_messageEntityCustomEmoji tLRPC$TL_messageEntityCustomEmoji = (TLRPC$TL_messageEntityCustomEmoji) tLRPC$MessageEntity;
                    int i2 = tLRPC$MessageEntity.offset;
                    int i3 = tLRPC$MessageEntity.length + i2;
                    if (i2 < i3 && i3 <= spannable.length()) {
                        if (tLRPC$TL_messageEntityCustomEmoji.document != null) {
                            animatedEmojiSpan = new AnimatedEmojiSpan(tLRPC$TL_messageEntityCustomEmoji.document, fontMetricsInt);
                        } else {
                            animatedEmojiSpan = new AnimatedEmojiSpan(tLRPC$TL_messageEntityCustomEmoji.document_id, fontMetricsInt);
                        }
                        spannable.setSpan(animatedEmojiSpan, i2, i3, 33);
                    }
                }
            }
        }
    }

    public static ArrayList<TextStyleSpan.TextStyleRun> getTextStyleRuns(ArrayList<TLRPC$MessageEntity> arrayList, CharSequence charSequence, int i) {
        int i2;
        int i3;
        ArrayList<TextStyleSpan.TextStyleRun> arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList(arrayList);
        Collections.sort(arrayList3, MediaDataController$$ExternalSyntheticLambda148.INSTANCE);
        int size = arrayList3.size();
        for (int i4 = 0; i4 < size; i4++) {
            TLRPC$MessageEntity tLRPC$MessageEntity = (TLRPC$MessageEntity) arrayList3.get(i4);
            if (tLRPC$MessageEntity != null && tLRPC$MessageEntity.length > 0 && (i2 = tLRPC$MessageEntity.offset) >= 0 && i2 < charSequence.length()) {
                if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length > charSequence.length()) {
                    tLRPC$MessageEntity.length = charSequence.length() - tLRPC$MessageEntity.offset;
                }
                if (!(tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCustomEmoji)) {
                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                    int i5 = tLRPC$MessageEntity.offset;
                    textStyleRun.start = i5;
                    textStyleRun.end = i5 + tLRPC$MessageEntity.length;
                    if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntitySpoiler) {
                        textStyleRun.flags = 256;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityStrike) {
                        textStyleRun.flags = 8;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityUnderline) {
                        textStyleRun.flags = 16;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityBlockquote) {
                        textStyleRun.flags = 32;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityBold) {
                        textStyleRun.flags = 1;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityItalic) {
                        textStyleRun.flags = 2;
                    } else if ((tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCode) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityPre)) {
                        textStyleRun.flags = 4;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityMentionName) {
                        textStyleRun.flags = 64;
                        textStyleRun.urlEntity = tLRPC$MessageEntity;
                    } else if (tLRPC$MessageEntity instanceof TLRPC$TL_inputMessageEntityMentionName) {
                        textStyleRun.flags = 64;
                        textStyleRun.urlEntity = tLRPC$MessageEntity;
                    } else {
                        textStyleRun.flags = 128;
                        textStyleRun.urlEntity = tLRPC$MessageEntity;
                    }
                    textStyleRun.flags &= i;
                    int size2 = arrayList2.size();
                    int i6 = 0;
                    while (i3 < size2) {
                        TextStyleSpan.TextStyleRun textStyleRun2 = arrayList2.get(i3);
                        int i7 = textStyleRun.start;
                        int i8 = textStyleRun2.start;
                        if (i7 > i8) {
                            int i9 = textStyleRun2.end;
                            if (i7 < i9) {
                                if (textStyleRun.end < i9) {
                                    TextStyleSpan.TextStyleRun textStyleRun3 = new TextStyleSpan.TextStyleRun(textStyleRun);
                                    textStyleRun3.merge(textStyleRun2);
                                    int i10 = i3 + 1;
                                    arrayList2.add(i10, textStyleRun3);
                                    TextStyleSpan.TextStyleRun textStyleRun4 = new TextStyleSpan.TextStyleRun(textStyleRun2);
                                    textStyleRun4.start = textStyleRun.end;
                                    i3 = i10 + 1;
                                    size2 = size2 + 1 + 1;
                                    arrayList2.add(i3, textStyleRun4);
                                } else {
                                    TextStyleSpan.TextStyleRun textStyleRun5 = new TextStyleSpan.TextStyleRun(textStyleRun);
                                    textStyleRun5.merge(textStyleRun2);
                                    textStyleRun5.end = textStyleRun2.end;
                                    i3++;
                                    size2++;
                                    arrayList2.add(i3, textStyleRun5);
                                }
                                int i11 = textStyleRun.start;
                                textStyleRun.start = textStyleRun2.end;
                                textStyleRun2.end = i11;
                            }
                        } else {
                            int i12 = textStyleRun.end;
                            if (i8 < i12) {
                                int i13 = textStyleRun2.end;
                                if (i12 == i13) {
                                    textStyleRun2.merge(textStyleRun);
                                } else if (i12 < i13) {
                                    TextStyleSpan.TextStyleRun textStyleRun6 = new TextStyleSpan.TextStyleRun(textStyleRun2);
                                    textStyleRun6.merge(textStyleRun);
                                    textStyleRun6.end = textStyleRun.end;
                                    i3++;
                                    size2++;
                                    arrayList2.add(i3, textStyleRun6);
                                    textStyleRun2.start = textStyleRun.end;
                                } else {
                                    TextStyleSpan.TextStyleRun textStyleRun7 = new TextStyleSpan.TextStyleRun(textStyleRun);
                                    textStyleRun7.start = textStyleRun2.end;
                                    i3++;
                                    size2++;
                                    arrayList2.add(i3, textStyleRun7);
                                    textStyleRun2.merge(textStyleRun);
                                }
                                textStyleRun.end = i8;
                            }
                        }
                        i6 = i3 + 1;
                    }
                    if (textStyleRun.start < textStyleRun.end) {
                        arrayList2.add(textStyleRun);
                    }
                }
            }
        }
        return arrayList2;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$getTextStyleRuns$156(TLRPC$MessageEntity tLRPC$MessageEntity, TLRPC$MessageEntity tLRPC$MessageEntity2) {
        int i = tLRPC$MessageEntity.offset;
        int i2 = tLRPC$MessageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void addStyle(int i, int i2, int i3, ArrayList<TLRPC$MessageEntity> arrayList) {
        if ((i & 256) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntitySpoiler(), i2, i3));
        }
        if ((i & 1) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityBold(), i2, i3));
        }
        if ((i & 2) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityItalic(), i2, i3));
        }
        if ((i & 4) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityCode(), i2, i3));
        }
        if ((i & 8) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityStrike(), i2, i3));
        }
        if ((i & 16) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityUnderline(), i2, i3));
        }
        if ((i & 32) != 0) {
            arrayList.add(setEntityStartEnd(new TLRPC$TL_messageEntityBlockquote(), i2, i3));
        }
    }

    private TLRPC$MessageEntity setEntityStartEnd(TLRPC$MessageEntity tLRPC$MessageEntity, int i, int i2) {
        tLRPC$MessageEntity.offset = i;
        tLRPC$MessageEntity.length = i2 - i;
        return tLRPC$MessageEntity;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0052, code lost:
        if (r0 != null) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0054, code lost:
        r0 = new java.util.ArrayList<>();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0059, code lost:
        if (r4 == false) goto L_0x005d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005b, code lost:
        r12 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x005d, code lost:
        r12 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x005e, code lost:
        r12 = r12 + r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0065, code lost:
        if (r12 >= r19[0].length()) goto L_0x0074;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x006d, code lost:
        if (r19[0].charAt(r12) != '`') goto L_0x0074;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x006f, code lost:
        r5 = r5 + 1;
        r12 = r12 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0074, code lost:
        if (r4 == false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0076, code lost:
        r10 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0078, code lost:
        r10 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0079, code lost:
        r10 = r10 + r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x007a, code lost:
        if (r4 == false) goto L_0x011e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x007c, code lost:
        if (r6 <= 0) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x007e, code lost:
        r4 = r19[0].charAt(r6 - 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0087, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x008a, code lost:
        if (r4 == ' ') goto L_0x0091;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x008c, code lost:
        if (r4 != 10) goto L_0x008f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x008f, code lost:
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0091, code lost:
        r4 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0092, code lost:
        r13 = substring(r19[0], 0, r6 - r4);
        r14 = substring(r19[0], r6 + 3, r5);
        r15 = r5 + 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00aa, code lost:
        if (r15 >= r19[0].length()) goto L_0x00b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00ac, code lost:
        r3 = r19[0].charAt(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00b3, code lost:
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00b4, code lost:
        r11 = r19[0];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00b6, code lost:
        if (r3 == ' ') goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00b8, code lost:
        if (r3 != 10) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00bb, code lost:
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00bd, code lost:
        r3 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00be, code lost:
        r3 = substring(r11, r15 + r3, r19[0].length());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00cf, code lost:
        if (r13.length() == 0) goto L_0x00dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00d1, code lost:
        r13 = org.telegram.messenger.AndroidUtilities.concat(r13, "\n");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00dc, code lost:
        r4 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00e1, code lost:
        if (r3.length() == 0) goto L_0x00ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00e3, code lost:
        r3 = org.telegram.messenger.AndroidUtilities.concat("\n", r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00f1, code lost:
        if (android.text.TextUtils.isEmpty(r14) != false) goto L_0x015b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00f3, code lost:
        r19[0] = org.telegram.messenger.AndroidUtilities.concat(r13, r14, r3);
        r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityPre();
        r3.offset = (r4 ^ 1) + r6;
        r3.length = ((r5 - r6) - 3) + (r4 ^ 1);
        r3.language = "";
        r0.add(r3);
        r10 = r10 - 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x011e, code lost:
        r3 = r6 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0120, code lost:
        if (r3 == r5) goto L_0x015b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0122, code lost:
        r19[0] = org.telegram.messenger.AndroidUtilities.concat(substring(r19[0], 0, r6), substring(r19[0], r3, r5), substring(r19[0], r5 + 1, r19[0].length()));
        r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode();
        r3.offset = r6;
        r3.length = (r5 - r6) - 1;
        r0.add(r3);
        r10 = r10 - 2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> getEntities(java.lang.CharSequence[] r19, boolean r20) {
        /*
            r18 = this;
            r1 = r18
            r0 = 0
            if (r19 == 0) goto L_0x039e
            r2 = 0
            r3 = r19[r2]
            if (r3 != 0) goto L_0x000c
            goto L_0x039e
        L_0x000c:
            r3 = -1
            r4 = 0
            r5 = 0
        L_0x000f:
            r6 = -1
        L_0x0010:
            r7 = r19[r2]
            if (r4 != 0) goto L_0x0017
            java.lang.String r8 = "`"
            goto L_0x0019
        L_0x0017:
            java.lang.String r8 = "```"
        L_0x0019:
            int r5 = android.text.TextUtils.indexOf(r7, r8, r5)
            r7 = 32
            r8 = 2
            r9 = 1
            if (r5 == r3) goto L_0x0160
            r10 = 96
            if (r6 != r3) goto L_0x0052
            r4 = r19[r2]
            int r4 = r4.length()
            int r4 = r4 - r5
            if (r4 <= r8) goto L_0x0046
            r4 = r19[r2]
            int r6 = r5 + 1
            char r4 = r4.charAt(r6)
            if (r4 != r10) goto L_0x0046
            r4 = r19[r2]
            int r6 = r5 + 2
            char r4 = r4.charAt(r6)
            if (r4 != r10) goto L_0x0046
            r4 = 1
            goto L_0x0047
        L_0x0046:
            r4 = 0
        L_0x0047:
            if (r4 == 0) goto L_0x004a
            r9 = 3
        L_0x004a:
            int r6 = r5 + r9
            r17 = r6
            r6 = r5
            r5 = r17
            goto L_0x0010
        L_0x0052:
            if (r0 != 0) goto L_0x0059
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
        L_0x0059:
            if (r4 == 0) goto L_0x005d
            r12 = 3
            goto L_0x005e
        L_0x005d:
            r12 = 1
        L_0x005e:
            int r12 = r12 + r5
        L_0x005f:
            r13 = r19[r2]
            int r13 = r13.length()
            if (r12 >= r13) goto L_0x0074
            r13 = r19[r2]
            char r13 = r13.charAt(r12)
            if (r13 != r10) goto L_0x0074
            int r5 = r5 + 1
            int r12 = r12 + 1
            goto L_0x005f
        L_0x0074:
            if (r4 == 0) goto L_0x0078
            r10 = 3
            goto L_0x0079
        L_0x0078:
            r10 = 1
        L_0x0079:
            int r10 = r10 + r5
            if (r4 == 0) goto L_0x011e
            if (r6 <= 0) goto L_0x0087
            r4 = r19[r2]
            int r12 = r6 + -1
            char r4 = r4.charAt(r12)
            goto L_0x0088
        L_0x0087:
            r4 = 0
        L_0x0088:
            r12 = 10
            if (r4 == r7) goto L_0x0091
            if (r4 != r12) goto L_0x008f
            goto L_0x0091
        L_0x008f:
            r4 = 0
            goto L_0x0092
        L_0x0091:
            r4 = 1
        L_0x0092:
            r13 = r19[r2]
            int r14 = r6 - r4
            java.lang.CharSequence r13 = r1.substring(r13, r2, r14)
            r14 = r19[r2]
            int r15 = r6 + 3
            java.lang.CharSequence r14 = r1.substring(r14, r15, r5)
            int r15 = r5 + 3
            r16 = r19[r2]
            int r3 = r16.length()
            if (r15 >= r3) goto L_0x00b3
            r3 = r19[r2]
            char r3 = r3.charAt(r15)
            goto L_0x00b4
        L_0x00b3:
            r3 = 0
        L_0x00b4:
            r11 = r19[r2]
            if (r3 == r7) goto L_0x00bd
            if (r3 != r12) goto L_0x00bb
            goto L_0x00bd
        L_0x00bb:
            r3 = 0
            goto L_0x00be
        L_0x00bd:
            r3 = 1
        L_0x00be:
            int r15 = r15 + r3
            r3 = r19[r2]
            int r3 = r3.length()
            java.lang.CharSequence r3 = r1.substring(r11, r15, r3)
            int r7 = r13.length()
            java.lang.String r11 = "\n"
            if (r7 == 0) goto L_0x00dc
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r8]
            r7[r2] = r13
            r7[r9] = r11
            java.lang.CharSequence r13 = org.telegram.messenger.AndroidUtilities.concat(r7)
            goto L_0x00dd
        L_0x00dc:
            r4 = 1
        L_0x00dd:
            int r7 = r3.length()
            if (r7 == 0) goto L_0x00ed
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r8]
            r7[r2] = r11
            r7[r9] = r3
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.concat(r7)
        L_0x00ed:
            boolean r7 = android.text.TextUtils.isEmpty(r14)
            if (r7 != 0) goto L_0x015b
            r7 = 3
            java.lang.CharSequence[] r11 = new java.lang.CharSequence[r7]
            r11[r2] = r13
            r11[r9] = r14
            r11[r8] = r3
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.concat(r11)
            r19[r2] = r3
            org.telegram.tgnet.TLRPC$TL_messageEntityPre r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityPre
            r3.<init>()
            r7 = r4 ^ 1
            int r7 = r7 + r6
            r3.offset = r7
            int r5 = r5 - r6
            r6 = 3
            int r5 = r5 - r6
            r4 = r4 ^ 1
            int r5 = r5 + r4
            r3.length = r5
            java.lang.String r4 = ""
            r3.language = r4
            r0.add(r3)
            int r10 = r10 + -6
            goto L_0x015b
        L_0x011e:
            int r3 = r6 + 1
            if (r3 == r5) goto L_0x015b
            r4 = 3
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r4]
            r7 = r19[r2]
            java.lang.CharSequence r7 = r1.substring(r7, r2, r6)
            r4[r2] = r7
            r7 = r19[r2]
            java.lang.CharSequence r3 = r1.substring(r7, r3, r5)
            r4[r9] = r3
            r3 = r19[r2]
            int r7 = r5 + 1
            r11 = r19[r2]
            int r11 = r11.length()
            java.lang.CharSequence r3 = r1.substring(r3, r7, r11)
            r4[r8] = r3
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.concat(r4)
            r19[r2] = r3
            org.telegram.tgnet.TLRPC$TL_messageEntityCode r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode
            r3.<init>()
            r3.offset = r6
            int r5 = r5 - r6
            int r5 = r5 - r9
            r3.length = r5
            r0.add(r3)
            int r10 = r10 + -2
        L_0x015b:
            r5 = r10
            r3 = -1
            r4 = 0
            goto L_0x000f
        L_0x0160:
            if (r6 == r3) goto L_0x0197
            if (r4 == 0) goto L_0x0197
            java.lang.CharSequence[] r3 = new java.lang.CharSequence[r8]
            r4 = r19[r2]
            java.lang.CharSequence r4 = r1.substring(r4, r2, r6)
            r3[r2] = r4
            r4 = r19[r2]
            int r5 = r6 + 2
            r8 = r19[r2]
            int r8 = r8.length()
            java.lang.CharSequence r4 = r1.substring(r4, r5, r8)
            r3[r9] = r4
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.concat(r3)
            r19[r2] = r3
            if (r0 != 0) goto L_0x018b
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
        L_0x018b:
            org.telegram.tgnet.TLRPC$TL_messageEntityCode r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode
            r3.<init>()
            r3.offset = r6
            r3.length = r9
            r0.add(r3)
        L_0x0197:
            r3 = r19[r2]
            boolean r3 = r3 instanceof android.text.Spanned
            if (r3 == 0) goto L_0x0371
            r3 = r19[r2]
            android.text.Spanned r3 = (android.text.Spanned) r3
            r4 = r19[r2]
            int r4 = r4.length()
            java.lang.Class<org.telegram.ui.Components.TextStyleSpan> r5 = org.telegram.ui.Components.TextStyleSpan.class
            java.lang.Object[] r4 = r3.getSpans(r2, r4, r5)
            org.telegram.ui.Components.TextStyleSpan[] r4 = (org.telegram.ui.Components.TextStyleSpan[]) r4
            if (r4 == 0) goto L_0x01e6
            int r5 = r4.length
            if (r5 <= 0) goto L_0x01e6
            r5 = 0
        L_0x01b5:
            int r6 = r4.length
            if (r5 >= r6) goto L_0x01e6
            r6 = r4[r5]
            int r8 = r3.getSpanStart(r6)
            int r10 = r3.getSpanEnd(r6)
            boolean r11 = checkInclusion(r8, r0, r2)
            if (r11 != 0) goto L_0x01e3
            boolean r11 = checkInclusion(r10, r0, r9)
            if (r11 != 0) goto L_0x01e3
            boolean r11 = checkIntersection(r8, r10, r0)
            if (r11 == 0) goto L_0x01d5
            goto L_0x01e3
        L_0x01d5:
            if (r0 != 0) goto L_0x01dc
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
        L_0x01dc:
            int r6 = r6.getStyleFlags()
            r1.addStyle(r6, r8, r10, r0)
        L_0x01e3:
            int r5 = r5 + 1
            goto L_0x01b5
        L_0x01e6:
            r4 = r19[r2]
            int r4 = r4.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanUserMention> r5 = org.telegram.ui.Components.URLSpanUserMention.class
            java.lang.Object[] r4 = r3.getSpans(r2, r4, r5)
            org.telegram.ui.Components.URLSpanUserMention[] r4 = (org.telegram.ui.Components.URLSpanUserMention[]) r4
            if (r4 == 0) goto L_0x0255
            int r5 = r4.length
            if (r5 <= 0) goto L_0x0255
            if (r0 != 0) goto L_0x0200
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
        L_0x0200:
            r5 = 0
        L_0x0201:
            int r6 = r4.length
            if (r5 >= r6) goto L_0x0255
            org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName r6 = new org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            r6.<init>()
            org.telegram.messenger.MessagesController r8 = r18.getMessagesController()
            r10 = r4[r5]
            java.lang.String r10 = r10.getURL()
            java.lang.Long r10 = org.telegram.messenger.Utilities.parseLong(r10)
            long r10 = r10.longValue()
            org.telegram.tgnet.TLRPC$InputUser r8 = r8.getInputUser((long) r10)
            r6.user_id = r8
            if (r8 == 0) goto L_0x0252
            r8 = r4[r5]
            int r8 = r3.getSpanStart(r8)
            r6.offset = r8
            r8 = r4[r5]
            int r8 = r3.getSpanEnd(r8)
            r10 = r19[r2]
            int r10 = r10.length()
            int r8 = java.lang.Math.min(r8, r10)
            int r10 = r6.offset
            int r8 = r8 - r10
            r6.length = r8
            r11 = r19[r2]
            int r10 = r10 + r8
            int r10 = r10 - r9
            char r8 = r11.charAt(r10)
            if (r8 != r7) goto L_0x024f
            int r8 = r6.length
            int r8 = r8 - r9
            r6.length = r8
        L_0x024f:
            r0.add(r6)
        L_0x0252:
            int r5 = r5 + 1
            goto L_0x0201
        L_0x0255:
            r4 = r19[r2]
            int r4 = r4.length()
            java.lang.Class<org.telegram.ui.Components.URLSpanReplacement> r5 = org.telegram.ui.Components.URLSpanReplacement.class
            java.lang.Object[] r4 = r3.getSpans(r2, r4, r5)
            org.telegram.ui.Components.URLSpanReplacement[] r4 = (org.telegram.ui.Components.URLSpanReplacement[]) r4
            if (r4 == 0) goto L_0x02b5
            int r5 = r4.length
            if (r5 <= 0) goto L_0x02b5
            if (r0 != 0) goto L_0x026f
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
        L_0x026f:
            r5 = 0
        L_0x0270:
            int r6 = r4.length
            if (r5 >= r6) goto L_0x02b5
            org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl r6 = new org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            r6.<init>()
            r7 = r4[r5]
            int r7 = r3.getSpanStart(r7)
            r6.offset = r7
            r7 = r4[r5]
            int r7 = r3.getSpanEnd(r7)
            r8 = r19[r2]
            int r8 = r8.length()
            int r7 = java.lang.Math.min(r7, r8)
            int r8 = r6.offset
            int r7 = r7 - r8
            r6.length = r7
            r7 = r4[r5]
            java.lang.String r7 = r7.getURL()
            r6.url = r7
            r0.add(r6)
            r7 = r4[r5]
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r7 = r7.getTextStyleRun()
            if (r7 == 0) goto L_0x02b2
            int r7 = r7.flags
            int r8 = r6.offset
            int r6 = r6.length
            int r6 = r6 + r8
            r1.addStyle(r7, r8, r6, r0)
        L_0x02b2:
            int r5 = r5 + 1
            goto L_0x0270
        L_0x02b5:
            r4 = r19[r2]
            int r4 = r4.length()
            java.lang.Class<org.telegram.ui.Components.AnimatedEmojiSpan> r5 = org.telegram.ui.Components.AnimatedEmojiSpan.class
            java.lang.Object[] r4 = r3.getSpans(r2, r4, r5)
            org.telegram.ui.Components.AnimatedEmojiSpan[] r4 = (org.telegram.ui.Components.AnimatedEmojiSpan[]) r4
            if (r4 == 0) goto L_0x030c
            int r5 = r4.length
            if (r5 <= 0) goto L_0x030c
            if (r0 != 0) goto L_0x02cf
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
        L_0x02cf:
            r5 = r0
            r6 = 0
        L_0x02d1:
            int r0 = r4.length
            if (r6 >= r0) goto L_0x030b
            r0 = r4[r6]
            if (r0 == 0) goto L_0x0308
            org.telegram.tgnet.TLRPC$TL_messageEntityCustomEmoji r7 = new org.telegram.tgnet.TLRPC$TL_messageEntityCustomEmoji     // Catch:{ Exception -> 0x0304 }
            r7.<init>()     // Catch:{ Exception -> 0x0304 }
            int r8 = r3.getSpanStart(r0)     // Catch:{ Exception -> 0x0304 }
            r7.offset = r8     // Catch:{ Exception -> 0x0304 }
            int r8 = r3.getSpanEnd(r0)     // Catch:{ Exception -> 0x0304 }
            r10 = r19[r2]     // Catch:{ Exception -> 0x0304 }
            int r10 = r10.length()     // Catch:{ Exception -> 0x0304 }
            int r8 = java.lang.Math.min(r8, r10)     // Catch:{ Exception -> 0x0304 }
            int r10 = r7.offset     // Catch:{ Exception -> 0x0304 }
            int r8 = r8 - r10
            r7.length = r8     // Catch:{ Exception -> 0x0304 }
            long r10 = r0.getDocumentId()     // Catch:{ Exception -> 0x0304 }
            r7.document_id = r10     // Catch:{ Exception -> 0x0304 }
            org.telegram.tgnet.TLRPC$Document r0 = r0.document     // Catch:{ Exception -> 0x0304 }
            r7.document = r0     // Catch:{ Exception -> 0x0304 }
            r5.add(r7)     // Catch:{ Exception -> 0x0304 }
            goto L_0x0308
        L_0x0304:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0308:
            int r6 = r6 + 1
            goto L_0x02d1
        L_0x030b:
            r0 = r5
        L_0x030c:
            boolean r4 = r3 instanceof android.text.Spannable
            if (r4 == 0) goto L_0x0371
            r4 = r3
            android.text.Spannable r4 = (android.text.Spannable) r4
            org.telegram.messenger.AndroidUtilities.addLinks(r4, r9, r2, r2)
            r4 = r19[r2]
            int r4 = r4.length()
            java.lang.Class<android.text.style.URLSpan> r5 = android.text.style.URLSpan.class
            java.lang.Object[] r4 = r3.getSpans(r2, r4, r5)
            android.text.style.URLSpan[] r4 = (android.text.style.URLSpan[]) r4
            if (r4 == 0) goto L_0x0371
            int r5 = r4.length
            if (r5 <= 0) goto L_0x0371
            if (r0 != 0) goto L_0x0330
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
        L_0x0330:
            r5 = 0
        L_0x0331:
            int r6 = r4.length
            if (r5 >= r6) goto L_0x0371
            r6 = r4[r5]
            boolean r6 = r6 instanceof org.telegram.ui.Components.URLSpanReplacement
            if (r6 != 0) goto L_0x036e
            r6 = r4[r5]
            boolean r6 = r6 instanceof org.telegram.ui.Components.URLSpanUserMention
            if (r6 == 0) goto L_0x0341
            goto L_0x036e
        L_0x0341:
            org.telegram.tgnet.TLRPC$TL_messageEntityUrl r6 = new org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            r6.<init>()
            r7 = r4[r5]
            int r7 = r3.getSpanStart(r7)
            r6.offset = r7
            r7 = r4[r5]
            int r7 = r3.getSpanEnd(r7)
            r8 = r19[r2]
            int r8 = r8.length()
            int r7 = java.lang.Math.min(r7, r8)
            int r8 = r6.offset
            int r7 = r7 - r8
            r6.length = r7
            r7 = r4[r5]
            java.lang.String r7 = r7.getURL()
            r6.url = r7
            r0.add(r6)
        L_0x036e:
            int r5 = r5 + 1
            goto L_0x0331
        L_0x0371:
            r3 = r19[r2]
            if (r0 != 0) goto L_0x037a
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
        L_0x037a:
            java.util.regex.Pattern r4 = BOLD_PATTERN
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda154 r5 = org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda154.INSTANCE
            java.lang.CharSequence r3 = r1.parsePattern(r3, r4, r0, r5)
            java.util.regex.Pattern r4 = ITALIC_PATTERN
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda153 r5 = org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda153.INSTANCE
            java.lang.CharSequence r3 = r1.parsePattern(r3, r4, r0, r5)
            java.util.regex.Pattern r4 = SPOILER_PATTERN
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda152 r5 = org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda152.INSTANCE
            java.lang.CharSequence r3 = r1.parsePattern(r3, r4, r0, r5)
            if (r20 == 0) goto L_0x039c
            java.util.regex.Pattern r4 = STRIKE_PATTERN
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda151 r5 = org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda151.INSTANCE
            java.lang.CharSequence r3 = r1.parsePattern(r3, r4, r0, r5)
        L_0x039c:
            r19[r2] = r3
        L_0x039e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.getEntities(java.lang.CharSequence[], boolean):java.util.ArrayList");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$157(Void voidR) {
        return new TLRPC$TL_messageEntityBold();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$158(Void voidR) {
        return new TLRPC$TL_messageEntityItalic();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$159(Void voidR) {
        return new TLRPC$TL_messageEntitySpoiler();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$160(Void voidR) {
        return new TLRPC$TL_messageEntityStrike();
    }

    private CharSequence parsePattern(CharSequence charSequence, Pattern pattern, List<TLRPC$MessageEntity> list, GenericProvider<Void, TLRPC$MessageEntity> genericProvider) {
        URLSpan[] uRLSpanArr;
        Matcher matcher = pattern.matcher(charSequence);
        int i = 0;
        while (matcher.find()) {
            boolean z = true;
            String group = matcher.group(1);
            if ((charSequence instanceof Spannable) && (uRLSpanArr = (URLSpan[]) ((Spannable) charSequence).getSpans(matcher.start() - i, matcher.end() - i, URLSpan.class)) != null && uRLSpanArr.length > 0) {
                z = false;
            }
            if (z) {
                charSequence = charSequence.subSequence(0, matcher.start() - i) + group + charSequence.subSequence(matcher.end() - i, charSequence.length());
                TLRPC$MessageEntity provide = genericProvider.provide(null);
                provide.offset = matcher.start() - i;
                provide.length = group.length();
                list.add(provide);
            }
            i += (matcher.end() - matcher.start()) - group.length();
        }
        return charSequence;
    }

    public void loadDraftsIfNeed() {
        if (!getUserConfig().draftsLoaded && !this.loadingDrafts) {
            this.loadingDrafts = true;
            getConnectionsManager().sendRequest(new TLRPC$TL_messages_getAllDrafts(), new MediaDataController$$ExternalSyntheticLambda165(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDraftsIfNeed$161() {
        this.loadingDrafts = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDraftsIfNeed$163(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda13(this));
            return;
        }
        getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda17(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDraftsIfNeed$162() {
        this.loadingDrafts = false;
        UserConfig userConfig = getUserConfig();
        userConfig.draftsLoaded = true;
        userConfig.saveConfig(false);
    }

    public int getDraftFolderId(long j) {
        return this.draftsFolderIds.get(j, 0).intValue();
    }

    public void setDraftFolderId(long j, int i) {
        this.draftsFolderIds.put(j, Integer.valueOf(i));
    }

    public void clearDraftsFolderIds() {
        this.draftsFolderIds.clear();
    }

    public LongSparseArray<SparseArray<TLRPC$DraftMessage>> getDrafts() {
        return this.drafts;
    }

    public TLRPC$DraftMessage getDraft(long j, int i) {
        SparseArray sparseArray = this.drafts.get(j);
        if (sparseArray == null) {
            return null;
        }
        return (TLRPC$DraftMessage) sparseArray.get(i);
    }

    public TLRPC$Message getDraftMessage(long j, int i) {
        SparseArray sparseArray = this.draftMessages.get(j);
        if (sparseArray == null) {
            return null;
        }
        return (TLRPC$Message) sparseArray.get(i);
    }

    public void saveDraft(long j, int i, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$Message tLRPC$Message, boolean z) {
        saveDraft(j, i, charSequence, arrayList, tLRPC$Message, z, false);
    }

    public void saveDraft(long j, int i, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$Message tLRPC$Message, boolean z, boolean z2) {
        TLRPC$DraftMessage tLRPC$DraftMessage;
        String str;
        TLRPC$DraftMessage tLRPC$DraftMessage2;
        long j2 = j;
        int i2 = i;
        ArrayList<TLRPC$MessageEntity> arrayList2 = arrayList;
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        if (!TextUtils.isEmpty(charSequence) || tLRPC$Message2 != null) {
            tLRPC$DraftMessage = new TLRPC$TL_draftMessage();
        } else {
            tLRPC$DraftMessage = new TLRPC$TL_draftMessageEmpty();
        }
        TLRPC$DraftMessage tLRPC$DraftMessage3 = tLRPC$DraftMessage;
        tLRPC$DraftMessage3.date = (int) (System.currentTimeMillis() / 1000);
        if (charSequence == null) {
            str = "";
        } else {
            str = charSequence.toString();
        }
        tLRPC$DraftMessage3.message = str;
        tLRPC$DraftMessage3.no_webpage = z;
        if (tLRPC$Message2 != null) {
            tLRPC$DraftMessage3.reply_to_msg_id = tLRPC$Message2.id;
            tLRPC$DraftMessage3.flags |= 1;
        }
        if (arrayList2 != null && !arrayList.isEmpty()) {
            tLRPC$DraftMessage3.entities = arrayList2;
            tLRPC$DraftMessage3.flags |= 8;
        }
        SparseArray sparseArray = this.drafts.get(j);
        if (sparseArray == null) {
            tLRPC$DraftMessage2 = null;
        } else {
            tLRPC$DraftMessage2 = (TLRPC$DraftMessage) sparseArray.get(i2);
        }
        if (!z2) {
            if (tLRPC$DraftMessage2 != null && tLRPC$DraftMessage2.message.equals(tLRPC$DraftMessage3.message) && tLRPC$DraftMessage2.reply_to_msg_id == tLRPC$DraftMessage3.reply_to_msg_id && tLRPC$DraftMessage2.no_webpage == tLRPC$DraftMessage3.no_webpage) {
                return;
            }
            if (tLRPC$DraftMessage2 == null && TextUtils.isEmpty(tLRPC$DraftMessage3.message) && tLRPC$DraftMessage3.reply_to_msg_id == 0) {
                return;
            }
        }
        saveDraft(j, i, tLRPC$DraftMessage3, tLRPC$Message, false);
        if (i2 == 0) {
            if (!DialogObject.isEncryptedDialog(j)) {
                TLRPC$TL_messages_saveDraft tLRPC$TL_messages_saveDraft = new TLRPC$TL_messages_saveDraft();
                TLRPC$InputPeer inputPeer = getMessagesController().getInputPeer(j);
                tLRPC$TL_messages_saveDraft.peer = inputPeer;
                if (inputPeer != null) {
                    tLRPC$TL_messages_saveDraft.message = tLRPC$DraftMessage3.message;
                    tLRPC$TL_messages_saveDraft.no_webpage = tLRPC$DraftMessage3.no_webpage;
                    tLRPC$TL_messages_saveDraft.reply_to_msg_id = tLRPC$DraftMessage3.reply_to_msg_id;
                    tLRPC$TL_messages_saveDraft.entities = tLRPC$DraftMessage3.entities;
                    tLRPC$TL_messages_saveDraft.flags = tLRPC$DraftMessage3.flags;
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_saveDraft, MediaDataController$$ExternalSyntheticLambda207.INSTANCE);
                } else {
                    return;
                }
            }
            getMessagesController().sortDialogs((LongSparseArray<TLRPC$Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void saveDraft(long j, int i, TLRPC$DraftMessage tLRPC$DraftMessage, TLRPC$Message tLRPC$Message, boolean z) {
        TLRPC$Chat tLRPC$Chat;
        StringBuilder sb;
        String str;
        long j2 = j;
        int i2 = i;
        TLRPC$DraftMessage tLRPC$DraftMessage2 = tLRPC$DraftMessage;
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        SharedPreferences.Editor edit = this.draftPreferences.edit();
        MessagesController messagesController = getMessagesController();
        if (tLRPC$DraftMessage2 == null || (tLRPC$DraftMessage2 instanceof TLRPC$TL_draftMessageEmpty)) {
            SparseArray sparseArray = this.drafts.get(j2);
            if (sparseArray != null) {
                sparseArray.remove(i2);
                if (sparseArray.size() == 0) {
                    this.drafts.remove(j2);
                }
            }
            SparseArray sparseArray2 = this.draftMessages.get(j2);
            if (sparseArray2 != null) {
                sparseArray2.remove(i2);
                if (sparseArray2.size() == 0) {
                    this.draftMessages.remove(j2);
                }
            }
            if (i2 == 0) {
                this.draftPreferences.edit().remove("" + j2).remove("r_" + j2).commit();
            } else {
                this.draftPreferences.edit().remove("t_" + j2 + "_" + i2).remove("rt_" + j2 + "_" + i2).commit();
            }
            messagesController.removeDraftDialogIfNeed(j2);
        } else {
            SparseArray sparseArray3 = this.drafts.get(j2);
            if (sparseArray3 == null) {
                sparseArray3 = new SparseArray();
                this.drafts.put(j2, sparseArray3);
            }
            sparseArray3.put(i2, tLRPC$DraftMessage2);
            if (i2 == 0) {
                messagesController.putDraftDialogIfNeed(j2, tLRPC$DraftMessage2);
            }
            try {
                SerializedData serializedData = new SerializedData(tLRPC$DraftMessage.getObjectSize());
                tLRPC$DraftMessage2.serializeToStream(serializedData);
                if (i2 == 0) {
                    str = "" + j2;
                } else {
                    str = "t_" + j2 + "_" + i2;
                }
                edit.putString(str, Utilities.bytesToHex(serializedData.toByteArray()));
                serializedData.cleanup();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        SparseArray sparseArray4 = this.draftMessages.get(j2);
        if (tLRPC$Message2 == null) {
            if (sparseArray4 != null) {
                sparseArray4.remove(i2);
                if (sparseArray4.size() == 0) {
                    this.draftMessages.remove(j2);
                }
            }
            if (i2 == 0) {
                edit.remove("r_" + j2);
            } else {
                edit.remove("rt_" + j2 + "_" + i2);
            }
        } else {
            if (sparseArray4 == null) {
                sparseArray4 = new SparseArray();
                this.draftMessages.put(j2, sparseArray4);
            }
            sparseArray4.put(i2, tLRPC$Message2);
            SerializedData serializedData2 = new SerializedData(tLRPC$Message.getObjectSize());
            tLRPC$Message2.serializeToStream(serializedData2);
            if (i2 == 0) {
                sb.append("r_");
                sb.append(j2);
            } else {
                sb = new StringBuilder();
                sb.append("rt_");
                sb.append(j2);
                sb.append("_");
                sb.append(i2);
            }
            edit.putString(sb.toString(), Utilities.bytesToHex(serializedData2.toByteArray()));
            serializedData2.cleanup();
        }
        edit.commit();
        if (z && i2 == 0) {
            if (!(tLRPC$DraftMessage2 == null || tLRPC$DraftMessage2.reply_to_msg_id == 0 || tLRPC$Message2 != null)) {
                TLRPC$User tLRPC$User = null;
                if (DialogObject.isUserDialog(j)) {
                    tLRPC$User = getMessagesController().getUser(Long.valueOf(j));
                    tLRPC$Chat = null;
                } else {
                    tLRPC$Chat = getMessagesController().getChat(Long.valueOf(-j2));
                }
                if (!(tLRPC$User == null && tLRPC$Chat == null)) {
                    getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda25(this, tLRPC$DraftMessage2.reply_to_msg_id, j, ChatObject.isChannel(tLRPC$Chat) ? tLRPC$Chat.id : 0, i));
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDraft$167(int i, long j, long j2, int i2) {
        NativeByteBuffer byteBufferValue;
        TLRPC$Message tLRPC$Message = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE mid = %d and uid = %d", new Object[]{Integer.valueOf(i), Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next() && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$Message = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                tLRPC$Message.readAttachPath(byteBufferValue, getUserConfig().clientUserId);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tLRPC$Message != null) {
                saveDraftReplyMessage(j, i2, tLRPC$Message);
            } else if (j2 != 0) {
                TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
                tLRPC$TL_channels_getMessages.channel = getMessagesController().getInputChannel(j2);
                tLRPC$TL_channels_getMessages.id.add(Integer.valueOf(i));
                getConnectionsManager().sendRequest(tLRPC$TL_channels_getMessages, new MediaDataController$$ExternalSyntheticLambda178(this, j, i2));
            } else {
                TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages = new TLRPC$TL_messages_getMessages();
                tLRPC$TL_messages_getMessages.id.add(Integer.valueOf(i));
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages, new MediaDataController$$ExternalSyntheticLambda179(this, j, i2));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDraft$165(long j, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            if (!tLRPC$messages_Messages.messages.isEmpty()) {
                saveDraftReplyMessage(j, i, tLRPC$messages_Messages.messages.get(0));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDraft$166(long j, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            if (!tLRPC$messages_Messages.messages.isEmpty()) {
                saveDraftReplyMessage(j, i, tLRPC$messages_Messages.messages.get(0));
            }
        }
    }

    private void saveDraftReplyMessage(long j, int i, TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda43(this, j, i, tLRPC$Message));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDraftReplyMessage$168(long j, int i, TLRPC$Message tLRPC$Message) {
        String str;
        SparseArray sparseArray = this.drafts.get(j);
        TLRPC$DraftMessage tLRPC$DraftMessage = sparseArray != null ? (TLRPC$DraftMessage) sparseArray.get(i) : null;
        if (tLRPC$DraftMessage != null && tLRPC$DraftMessage.reply_to_msg_id == tLRPC$Message.id) {
            SparseArray sparseArray2 = this.draftMessages.get(j);
            if (sparseArray2 == null) {
                sparseArray2 = new SparseArray();
                this.draftMessages.put(j, sparseArray2);
            }
            sparseArray2.put(i, tLRPC$Message);
            SerializedData serializedData = new SerializedData(tLRPC$Message.getObjectSize());
            tLRPC$Message.serializeToStream(serializedData);
            SharedPreferences.Editor edit = this.draftPreferences.edit();
            if (i == 0) {
                str = "r_" + j;
            } else {
                str = "rt_" + j + "_" + i;
            }
            edit.putString(str, Utilities.bytesToHex(serializedData.toByteArray())).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
            serializedData.cleanup();
        }
    }

    public void clearAllDrafts(boolean z) {
        this.drafts.clear();
        this.draftMessages.clear();
        this.draftsFolderIds.clear();
        this.draftPreferences.edit().clear().commit();
        if (z) {
            getMessagesController().sortDialogs((LongSparseArray<TLRPC$Chat>) null);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public void cleanDraft(long j, int i, boolean z) {
        SparseArray sparseArray = this.drafts.get(j);
        TLRPC$DraftMessage tLRPC$DraftMessage = sparseArray != null ? (TLRPC$DraftMessage) sparseArray.get(i) : null;
        if (tLRPC$DraftMessage != null) {
            if (!z) {
                SparseArray sparseArray2 = this.drafts.get(j);
                if (sparseArray2 != null) {
                    sparseArray2.remove(i);
                    if (sparseArray2.size() == 0) {
                        this.drafts.remove(j);
                    }
                }
                SparseArray sparseArray3 = this.draftMessages.get(j);
                if (sparseArray3 != null) {
                    sparseArray3.remove(i);
                    if (sparseArray3.size() == 0) {
                        this.draftMessages.remove(j);
                    }
                }
                if (i == 0) {
                    this.draftPreferences.edit().remove("" + j).remove("r_" + j).commit();
                    getMessagesController().sortDialogs((LongSparseArray<TLRPC$Chat>) null);
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    return;
                }
                this.draftPreferences.edit().remove("t_" + j + "_" + i).remove("rt_" + j + "_" + i).commit();
            } else if (tLRPC$DraftMessage.reply_to_msg_id != 0) {
                tLRPC$DraftMessage.reply_to_msg_id = 0;
                tLRPC$DraftMessage.flags &= -2;
                saveDraft(j, i, tLRPC$DraftMessage.message, tLRPC$DraftMessage.entities, (TLRPC$Message) null, tLRPC$DraftMessage.no_webpage, true);
            }
        }
    }

    public void beginTransaction() {
        this.inTransaction = true;
    }

    public void endTransaction() {
        this.inTransaction = false;
    }

    public void clearBotKeyboard(long j, ArrayList<Integer> arrayList) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda72(this, arrayList, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearBotKeyboard$169(ArrayList arrayList, long j) {
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                long j2 = this.botKeyboardsByMids.get(((Integer) arrayList.get(i)).intValue());
                if (j2 != 0) {
                    this.botKeyboards.remove(j2);
                    this.botKeyboardsByMids.delete(((Integer) arrayList.get(i)).intValue());
                    getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(j2));
                }
            }
            return;
        }
        this.botKeyboards.remove(j);
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, null, Long.valueOf(j));
    }

    public void loadBotKeyboard(long j) {
        TLRPC$Message tLRPC$Message = this.botKeyboards.get(j);
        if (tLRPC$Message != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, tLRPC$Message, Long.valueOf(j));
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda36(this, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadBotKeyboard$171(long j) {
        NativeByteBuffer byteBufferValue;
        TLRPC$Message tLRPC$Message = null;
        try {
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
            if (queryFinalized.next() && !queryFinalized.isNull(0) && (byteBufferValue = queryFinalized.byteBufferValue(0)) != null) {
                tLRPC$Message = TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                byteBufferValue.reuse();
            }
            queryFinalized.dispose();
            if (tLRPC$Message != null) {
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda94(this, tLRPC$Message, j));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadBotKeyboard$170(TLRPC$Message tLRPC$Message, long j) {
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, tLRPC$Message, Long.valueOf(j));
    }

    private TLRPC$BotInfo loadBotInfoInternal(long j, long j2) throws SQLiteException {
        TLRPC$BotInfo tLRPC$BotInfo;
        NativeByteBuffer byteBufferValue;
        SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info_v2 WHERE uid = %d AND dialogId = %d", new Object[]{Long.valueOf(j), Long.valueOf(j2)}), new Object[0]);
        if (!queryFinalized.next() || queryFinalized.isNull(0) || (byteBufferValue = queryFinalized.byteBufferValue(0)) == null) {
            tLRPC$BotInfo = null;
        } else {
            tLRPC$BotInfo = TLRPC$BotInfo.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
            byteBufferValue.reuse();
        }
        queryFinalized.dispose();
        return tLRPC$BotInfo;
    }

    public void loadBotInfo(long j, long j2, boolean z, int i) {
        if (z) {
            HashMap<String, TLRPC$BotInfo> hashMap = this.botInfos;
            TLRPC$BotInfo tLRPC$BotInfo = hashMap.get(j + "_" + j2);
            if (tLRPC$BotInfo != null) {
                getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, tLRPC$BotInfo, Integer.valueOf(i));
                return;
            }
        }
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda44(this, j, j2, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadBotInfo$173(long j, long j2, int i) {
        try {
            TLRPC$BotInfo loadBotInfoInternal = loadBotInfoInternal(j, j2);
            if (loadBotInfoInternal != null) {
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda90(this, loadBotInfoInternal, i));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadBotInfo$172(TLRPC$BotInfo tLRPC$BotInfo, int i) {
        getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, tLRPC$BotInfo, Integer.valueOf(i));
    }

    public void putBotKeyboard(long j, TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message != null) {
            try {
                int i = 0;
                SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", new Object[]{Long.valueOf(j)}), new Object[0]);
                if (queryFinalized.next()) {
                    i = queryFinalized.intValue(0);
                }
                queryFinalized.dispose();
                if (i < tLRPC$Message.id) {
                    SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
                    executeFast.requery();
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$Message.getObjectSize());
                    tLRPC$Message.serializeToStream(nativeByteBuffer);
                    executeFast.bindLong(1, j);
                    executeFast.bindInteger(2, tLRPC$Message.id);
                    executeFast.bindByteBuffer(3, nativeByteBuffer);
                    executeFast.step();
                    nativeByteBuffer.reuse();
                    executeFast.dispose();
                    AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda50(this, j, tLRPC$Message));
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putBotKeyboard$174(long j, TLRPC$Message tLRPC$Message) {
        TLRPC$Message tLRPC$Message2 = this.botKeyboards.get(j);
        this.botKeyboards.put(j, tLRPC$Message);
        if (MessageObject.getChannelId(tLRPC$Message) == 0) {
            if (tLRPC$Message2 != null) {
                this.botKeyboardsByMids.delete(tLRPC$Message2.id);
            }
            this.botKeyboardsByMids.put(tLRPC$Message.id, j);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.botKeyboardDidLoad, tLRPC$Message, Long.valueOf(j));
    }

    public void putBotInfo(long j, TLRPC$BotInfo tLRPC$BotInfo) {
        if (tLRPC$BotInfo != null) {
            HashMap<String, TLRPC$BotInfo> hashMap = this.botInfos;
            hashMap.put(tLRPC$BotInfo.user_id + "_" + j, tLRPC$BotInfo);
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda91(this, tLRPC$BotInfo, j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putBotInfo$175(TLRPC$BotInfo tLRPC$BotInfo, long j) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_info_v2 VALUES(?, ?, ?)");
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$BotInfo.getObjectSize());
            tLRPC$BotInfo.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, tLRPC$BotInfo.user_id);
            executeFast.bindLong(2, j);
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void updateBotInfo(long j, TLRPC$TL_updateBotCommands tLRPC$TL_updateBotCommands) {
        HashMap<String, TLRPC$BotInfo> hashMap = this.botInfos;
        TLRPC$BotInfo tLRPC$BotInfo = hashMap.get(tLRPC$TL_updateBotCommands.bot_id + "_" + j);
        if (tLRPC$BotInfo != null) {
            tLRPC$BotInfo.commands = tLRPC$TL_updateBotCommands.commands;
            getNotificationCenter().postNotificationName(NotificationCenter.botInfoDidLoad, tLRPC$BotInfo, 0);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda112(this, tLRPC$TL_updateBotCommands, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateBotInfo$176(TLRPC$TL_updateBotCommands tLRPC$TL_updateBotCommands, long j) {
        try {
            TLRPC$BotInfo loadBotInfoInternal = loadBotInfoInternal(tLRPC$TL_updateBotCommands.bot_id, j);
            if (loadBotInfoInternal != null) {
                loadBotInfoInternal.commands = tLRPC$TL_updateBotCommands.commands;
            }
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO bot_info_v2 VALUES(?, ?, ?)");
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(loadBotInfoInternal.getObjectSize());
            loadBotInfoInternal.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, loadBotInfoInternal.user_id);
            executeFast.bindLong(2, j);
            executeFast.bindByteBuffer(3, nativeByteBuffer);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public HashMap<String, TLRPC$TL_availableReaction> getReactionsMap() {
        return this.reactionsMap;
    }

    public String getDoubleTapReaction() {
        String str = this.doubleTapReaction;
        if (str != null) {
            return str;
        }
        if (getReactionsList().isEmpty()) {
            return null;
        }
        String string = MessagesController.getEmojiSettings(this.currentAccount).getString("reaction_on_double_tap", (String) null);
        if (string == null || (getReactionsMap().get(string) == null && !string.startsWith("animated_"))) {
            return getReactionsList().get(0).reaction;
        }
        this.doubleTapReaction = string;
        return string;
    }

    public void setDoubleTapReaction(String str) {
        MessagesController.getEmojiSettings(this.currentAccount).edit().putString("reaction_on_double_tap", str).apply();
        this.doubleTapReaction = str;
    }

    public List<TLRPC$TL_availableReaction> getEnabledReactionsList() {
        return this.enabledReactionsList;
    }

    public void uploadRingtone(String str) {
        if (!this.ringtoneUploaderHashMap.containsKey(str)) {
            this.ringtoneUploaderHashMap.put(str, new RingtoneUploader(str, this.currentAccount));
            this.ringtoneDataStore.addUploadingTone(str);
        }
    }

    public void onRingtoneUploaded(String str, TLRPC$Document tLRPC$Document, boolean z) {
        this.ringtoneUploaderHashMap.remove(str);
        this.ringtoneDataStore.onRingtoneUploaded(str, tLRPC$Document, z);
    }

    public void checkRingtones() {
        this.ringtoneDataStore.lambda$new$0();
    }

    public boolean saveToRingtones(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return false;
        }
        if (this.ringtoneDataStore.contains(tLRPC$Document.id)) {
            return true;
        }
        if (tLRPC$Document.size > ((long) MessagesController.getInstance(this.currentAccount).ringtoneSizeMax)) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLargeError", R.string.TooLargeError, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", R.string.ErrorRingtoneSizeTooBig, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax / 1024)));
            return false;
        }
        int i = 0;
        while (i < tLRPC$Document.attributes.size()) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (!(tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) || tLRPC$DocumentAttribute.duration <= MessagesController.getInstance(this.currentAccount).ringtoneDurationMax) {
                i++;
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLongError", R.string.TooLongError, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", R.string.ErrorRingtoneDurationTooLong, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax)));
                return false;
            }
        }
        TLRPC$TL_account_saveRingtone tLRPC$TL_account_saveRingtone = new TLRPC$TL_account_saveRingtone();
        TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
        tLRPC$TL_account_saveRingtone.id = tLRPC$TL_inputDocument;
        tLRPC$TL_inputDocument.id = tLRPC$Document.id;
        tLRPC$TL_inputDocument.file_reference = tLRPC$Document.file_reference;
        tLRPC$TL_inputDocument.access_hash = tLRPC$Document.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_saveRingtone, new MediaDataController$$ExternalSyntheticLambda197(this, tLRPC$Document));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveToRingtones$178(TLRPC$Document tLRPC$Document, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda88(this, tLObject, tLRPC$Document));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveToRingtones$177(TLObject tLObject, TLRPC$Document tLRPC$Document) {
        if (tLObject == null) {
            return;
        }
        if (tLObject instanceof TLRPC$TL_account_savedRingtoneConverted) {
            this.ringtoneDataStore.addTone(((TLRPC$TL_account_savedRingtoneConverted) tLObject).document);
        } else {
            this.ringtoneDataStore.addTone(tLRPC$Document);
        }
    }

    public void preloadPremiumPreviewStickers() {
        if (this.previewStickersLoading || !this.premiumPreviewStickers.isEmpty()) {
            int i = 0;
            while (i < Math.min(this.premiumPreviewStickers.size(), 3)) {
                ArrayList<TLRPC$Document> arrayList = this.premiumPreviewStickers;
                TLRPC$Document tLRPC$Document = arrayList.get(i == 2 ? arrayList.size() - 1 : i);
                if (MessageObject.isPremiumSticker(tLRPC$Document)) {
                    ImageReceiver imageReceiver = new ImageReceiver();
                    imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$Document), (String) null, (Drawable) null, "webp", (Object) null, 1);
                    ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
                    ImageReceiver imageReceiver2 = new ImageReceiver();
                    imageReceiver2.setImage(ImageLocation.getForDocument(MessageObject.getPremiumStickerAnimation(tLRPC$Document), tLRPC$Document), (String) null, (ImageLocation) null, (String) null, "tgs", (Object) null, 1);
                    ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver2);
                }
                i++;
            }
            return;
        }
        TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers = new TLRPC$TL_messages_getStickers();
        tLRPC$TL_messages_getStickers.emoticon = Emoji.fixEmoji("⭐") + Emoji.fixEmoji("⭐");
        tLRPC$TL_messages_getStickers.hash = 0;
        this.previewStickersLoading = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getStickers, new MediaDataController$$ExternalSyntheticLambda160(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$preloadPremiumPreviewStickers$180(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda102(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$preloadPremiumPreviewStickers$179(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            this.previewStickersLoading = false;
            this.premiumPreviewStickers.clear();
            this.premiumPreviewStickers.addAll(((TLRPC$TL_messages_stickers) tLObject).stickers);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.premiumStickersPreviewLoaded, new Object[0]);
        }
    }

    public void chekAllMedia(boolean z) {
        if (z) {
            this.reactionsUpdateDate = 0;
            int[] iArr = this.loadFeaturedDate;
            iArr[0] = 0;
            iArr[1] = 0;
        }
        loadRecents(2, false, true, false);
        loadRecents(3, false, true, false);
        loadRecents(7, false, false, true);
        checkFeaturedStickers();
        checkFeaturedEmoji();
        checkReactions();
        checkMenuBots();
        checkPremiumPromo();
        checkPremiumGiftStickers();
        checkGenericAnimations();
    }

    public static class KeywordResult {
        public String emoji;
        public String keyword;

        public KeywordResult() {
        }

        public KeywordResult(String str, String str2) {
            this.emoji = str;
            this.keyword = str2;
        }
    }

    public void fetchNewEmojiKeywords(String[] strArr) {
        if (strArr != null) {
            int i = 0;
            while (i < strArr.length) {
                String str = strArr[i];
                if (!TextUtils.isEmpty(str) && this.currentFetchingEmoji.get(str) == null) {
                    this.currentFetchingEmoji.put(str, Boolean.TRUE);
                    getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda60(this, str));
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0055  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$186(java.lang.String r10) {
        /*
            r9 = this;
            r0 = -1
            r1 = 0
            r2 = 0
            org.telegram.messenger.MessagesStorage r4 = r9.getMessagesStorage()     // Catch:{ Exception -> 0x0033 }
            org.telegram.SQLite.SQLiteDatabase r4 = r4.getDatabase()     // Catch:{ Exception -> 0x0033 }
            java.lang.String r5 = "SELECT alias, version, date FROM emoji_keywords_info_v2 WHERE lang = ?"
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0033 }
            r8 = 0
            r7[r8] = r10     // Catch:{ Exception -> 0x0033 }
            org.telegram.SQLite.SQLiteCursor r4 = r4.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x0033 }
            boolean r5 = r4.next()     // Catch:{ Exception -> 0x0033 }
            if (r5 == 0) goto L_0x002c
            java.lang.String r1 = r4.stringValue(r8)     // Catch:{ Exception -> 0x0033 }
            int r5 = r4.intValue(r6)     // Catch:{ Exception -> 0x0033 }
            r6 = 2
            long r2 = r4.longValue(r6)     // Catch:{ Exception -> 0x0031 }
            goto L_0x002d
        L_0x002c:
            r5 = -1
        L_0x002d:
            r4.dispose()     // Catch:{ Exception -> 0x0031 }
            goto L_0x0038
        L_0x0031:
            r4 = move-exception
            goto L_0x0035
        L_0x0033:
            r4 = move-exception
            r5 = -1
        L_0x0035:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x0038:
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r4 != 0) goto L_0x0055
            long r6 = java.lang.System.currentTimeMillis()
            long r6 = r6 - r2
            long r2 = java.lang.Math.abs(r6)
            r6 = 3600000(0x36ee80, double:1.7786363E-317)
            int r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x0055
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda58 r0 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda58
            r0.<init>(r9, r10)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        L_0x0055:
            if (r5 != r0) goto L_0x005f
            org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords r0 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywords
            r0.<init>()
            r0.lang_code = r10
            goto L_0x0068
        L_0x005f:
            org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference r0 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiKeywordsDifference
            r0.<init>()
            r0.lang_code = r10
            r0.from_version = r5
        L_0x0068:
            org.telegram.tgnet.ConnectionsManager r2 = r9.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda173 r3 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda173
            r3.<init>(r9, r5, r1, r10)
            r2.sendRequest(r0, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$fetchNewEmojiKeywords$186(java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$181(String str) {
        this.currentFetchingEmoji.remove(str);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$185(int i, String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_emojiKeywordsDifference tLRPC$TL_emojiKeywordsDifference = (TLRPC$TL_emojiKeywordsDifference) tLObject;
            if (i == -1 || tLRPC$TL_emojiKeywordsDifference.lang_code.equals(str)) {
                putEmojiKeywords(str2, tLRPC$TL_emojiKeywordsDifference);
            } else {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda62(this, str2));
            }
        } else {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda61(this, str2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$183(String str) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_info_v2 WHERE lang = ?");
            executeFast.bindString(1, str);
            executeFast.step();
            executeFast.dispose();
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda63(this, str));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$182(String str) {
        this.currentFetchingEmoji.remove(str);
        fetchNewEmojiKeywords(new String[]{str});
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$184(String str) {
        this.currentFetchingEmoji.remove(str);
    }

    private void putEmojiKeywords(String str, TLRPC$TL_emojiKeywordsDifference tLRPC$TL_emojiKeywordsDifference) {
        if (tLRPC$TL_emojiKeywordsDifference != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda101(this, tLRPC$TL_emojiKeywordsDifference, str));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putEmojiKeywords$188(TLRPC$TL_emojiKeywordsDifference tLRPC$TL_emojiKeywordsDifference, String str) {
        try {
            if (!tLRPC$TL_emojiKeywordsDifference.keywords.isEmpty()) {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_v2 VALUES(?, ?, ?)");
                SQLitePreparedStatement executeFast2 = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_v2 WHERE lang = ? AND keyword = ? AND emoji = ?");
                getMessagesStorage().getDatabase().beginTransaction();
                int size = tLRPC$TL_emojiKeywordsDifference.keywords.size();
                for (int i = 0; i < size; i++) {
                    TLRPC$EmojiKeyword tLRPC$EmojiKeyword = tLRPC$TL_emojiKeywordsDifference.keywords.get(i);
                    if (tLRPC$EmojiKeyword instanceof TLRPC$TL_emojiKeyword) {
                        TLRPC$TL_emojiKeyword tLRPC$TL_emojiKeyword = (TLRPC$TL_emojiKeyword) tLRPC$EmojiKeyword;
                        String lowerCase = tLRPC$TL_emojiKeyword.keyword.toLowerCase();
                        int size2 = tLRPC$TL_emojiKeyword.emoticons.size();
                        for (int i2 = 0; i2 < size2; i2++) {
                            executeFast.requery();
                            executeFast.bindString(1, tLRPC$TL_emojiKeywordsDifference.lang_code);
                            executeFast.bindString(2, lowerCase);
                            executeFast.bindString(3, tLRPC$TL_emojiKeyword.emoticons.get(i2));
                            executeFast.step();
                        }
                    } else if (tLRPC$EmojiKeyword instanceof TLRPC$TL_emojiKeywordDeleted) {
                        TLRPC$TL_emojiKeywordDeleted tLRPC$TL_emojiKeywordDeleted = (TLRPC$TL_emojiKeywordDeleted) tLRPC$EmojiKeyword;
                        String lowerCase2 = tLRPC$TL_emojiKeywordDeleted.keyword.toLowerCase();
                        int size3 = tLRPC$TL_emojiKeywordDeleted.emoticons.size();
                        for (int i3 = 0; i3 < size3; i3++) {
                            executeFast2.requery();
                            executeFast2.bindString(1, tLRPC$TL_emojiKeywordsDifference.lang_code);
                            executeFast2.bindString(2, lowerCase2);
                            executeFast2.bindString(3, tLRPC$TL_emojiKeywordDeleted.emoticons.get(i3));
                            executeFast2.step();
                        }
                    }
                }
                getMessagesStorage().getDatabase().commitTransaction();
                executeFast.dispose();
                executeFast2.dispose();
            }
            SQLitePreparedStatement executeFast3 = getMessagesStorage().getDatabase().executeFast("REPLACE INTO emoji_keywords_info_v2 VALUES(?, ?, ?, ?)");
            executeFast3.bindString(1, str);
            executeFast3.bindString(2, tLRPC$TL_emojiKeywordsDifference.lang_code);
            executeFast3.bindInteger(3, tLRPC$TL_emojiKeywordsDifference.version);
            executeFast3.bindLong(4, System.currentTimeMillis());
            executeFast3.step();
            executeFast3.dispose();
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda57(this, str));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putEmojiKeywords$187(String str) {
        this.currentFetchingEmoji.remove(str);
        getNotificationCenter().postNotificationName(NotificationCenter.newEmojiSuggestionsAvailable, str);
    }

    public void getEmojiSuggestions(String[] strArr, String str, boolean z, KeywordResultCallback keywordResultCallback, boolean z2) {
        getEmojiSuggestions(strArr, str, z, keywordResultCallback, (CountDownLatch) null, z2);
    }

    public void getEmojiSuggestions(String[] strArr, String str, boolean z, KeywordResultCallback keywordResultCallback, CountDownLatch countDownLatch, boolean z2) {
        if (keywordResultCallback != null) {
            if (TextUtils.isEmpty(str) || strArr == null) {
                keywordResultCallback.run(new ArrayList(), (String) null);
                return;
            }
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda128(this, strArr, keywordResultCallback, str, z, new ArrayList(Emoji.recentEmoji), z2, countDownLatch));
            if (countDownLatch != null) {
                try {
                    countDownLatch.await();
                } catch (Throwable unused) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getEmojiSuggestions$194(String[] strArr, KeywordResultCallback keywordResultCallback, String str, boolean z, ArrayList arrayList, boolean z2, CountDownLatch countDownLatch) {
        String str2;
        SQLiteCursor sQLiteCursor;
        String[] strArr2 = strArr;
        KeywordResultCallback keywordResultCallback2 = keywordResultCallback;
        CountDownLatch countDownLatch2 = countDownLatch;
        ArrayList arrayList2 = new ArrayList();
        HashMap hashMap = new HashMap();
        String str3 = null;
        int i = 0;
        boolean z3 = false;
        while (i < strArr2.length) {
            try {
                SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT alias FROM emoji_keywords_info_v2 WHERE lang = ?", strArr2[i]);
                if (queryFinalized.next()) {
                    str3 = queryFinalized.stringValue(0);
                }
                queryFinalized.dispose();
                if (str3 != null) {
                    z3 = true;
                }
                i++;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (!z3) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda129(this, strArr2, keywordResultCallback2, arrayList2));
            return;
        }
        String lowerCase = str.toLowerCase();
        for (int i2 = 0; i2 < 2; i2++) {
            if (i2 == 1) {
                String translitString = LocaleController.getInstance().getTranslitString(lowerCase, false, false);
                if (translitString.equals(lowerCase)) {
                } else {
                    lowerCase = translitString;
                }
            }
            StringBuilder sb = new StringBuilder(lowerCase);
            int length = sb.length();
            while (true) {
                if (length <= 0) {
                    str2 = null;
                    break;
                }
                length--;
                char charAt = (char) (sb.charAt(length) + 1);
                sb.setCharAt(length, charAt);
                if (charAt != 0) {
                    str2 = sb.toString();
                    break;
                }
            }
            if (z) {
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized("SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword = ?", lowerCase);
            } else if (str2 != null) {
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized("SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword >= ? AND keyword < ?", lowerCase, str2);
            } else {
                lowerCase = lowerCase + "%";
                sQLiteCursor = getMessagesStorage().getDatabase().queryFinalized("SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword LIKE ?", lowerCase);
            }
            while (sQLiteCursor.next()) {
                String replace = sQLiteCursor.stringValue(0).replace("️", "");
                if (hashMap.get(replace) == null) {
                    hashMap.put(replace, Boolean.TRUE);
                    KeywordResult keywordResult = new KeywordResult();
                    keywordResult.emoji = replace;
                    keywordResult.keyword = sQLiteCursor.stringValue(1);
                    arrayList2.add(keywordResult);
                }
            }
            sQLiteCursor.dispose();
        }
        Collections.sort(arrayList2, new MediaDataController$$ExternalSyntheticLambda145(arrayList));
        if (z2 && SharedConfig.suggestAnimatedEmoji) {
            fillWithAnimatedEmoji(arrayList2, (Integer) null, new MediaDataController$$ExternalSyntheticLambda0(countDownLatch2, keywordResultCallback2, arrayList2, str3));
        } else if (countDownLatch2 != null) {
            keywordResultCallback2.run(arrayList2, str3);
            countDownLatch.countDown();
        } else {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda2(keywordResultCallback2, arrayList2, str3));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getEmojiSuggestions$189(String[] strArr, KeywordResultCallback keywordResultCallback, ArrayList arrayList) {
        int i = 0;
        while (i < strArr.length) {
            if (this.currentFetchingEmoji.get(strArr[i]) == null) {
                i++;
            } else {
                return;
            }
        }
        keywordResultCallback.run(arrayList, (String) null);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$getEmojiSuggestions$190(ArrayList arrayList, KeywordResult keywordResult, KeywordResult keywordResult2) {
        int indexOf = arrayList.indexOf(keywordResult.emoji);
        int i = Integer.MAX_VALUE;
        if (indexOf < 0) {
            indexOf = Integer.MAX_VALUE;
        }
        int indexOf2 = arrayList.indexOf(keywordResult2.emoji);
        if (indexOf2 >= 0) {
            i = indexOf2;
        }
        if (indexOf < i) {
            return -1;
        }
        if (indexOf > i) {
            return 1;
        }
        int length = keywordResult.keyword.length();
        int length2 = keywordResult2.keyword.length();
        if (length < length2) {
            return -1;
        }
        if (length > length2) {
            return 1;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$getEmojiSuggestions$192(CountDownLatch countDownLatch, KeywordResultCallback keywordResultCallback, ArrayList arrayList, String str) {
        if (countDownLatch != null) {
            keywordResultCallback.run(arrayList, str);
            countDownLatch.countDown();
            return;
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda3(keywordResultCallback, arrayList, str));
    }

    public void fillWithAnimatedEmoji(ArrayList<KeywordResult> arrayList, Integer num, Runnable runnable) {
        if (arrayList != null && !arrayList.isEmpty()) {
            ArrayList[] arrayListArr = new ArrayList[2];
            arrayListArr[0] = getStickerSets(5);
            MediaDataController$$ExternalSyntheticLambda56 mediaDataController$$ExternalSyntheticLambda56 = new MediaDataController$$ExternalSyntheticLambda56(this, num, arrayList, arrayListArr, runnable);
            if ((arrayListArr[0] == null || arrayListArr[0].isEmpty()) && !this.triedLoadingEmojipacks) {
                this.triedLoadingEmojipacks = true;
                boolean[] zArr = new boolean[1];
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda134(this, zArr, arrayListArr, mediaDataController$$ExternalSyntheticLambda56));
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda143(zArr, mediaDataController$$ExternalSyntheticLambda56), 900);
                return;
            }
            mediaDataController$$ExternalSyntheticLambda56.run();
        } else if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0220  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0260  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0186  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$fillWithAnimatedEmoji$195(java.lang.Integer r19, java.util.ArrayList r20, java.util.ArrayList[] r21, java.lang.Runnable r22) {
        /*
            r18 = this;
            r0 = r18
            r1 = r20
            java.util.ArrayList r2 = r18.getFeaturedEmojiSets()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r5 = 2
            if (r19 != 0) goto L_0x0027
            int r7 = r20.size()
            r8 = 5
            if (r7 <= r8) goto L_0x001e
            r5 = 1
            goto L_0x002b
        L_0x001e:
            int r7 = r20.size()
            if (r7 <= r5) goto L_0x0025
            goto L_0x002b
        L_0x0025:
            r5 = 3
            goto L_0x002b
        L_0x0027:
            int r5 = r19.intValue()
        L_0x002b:
            r7 = 0
            r8 = 0
        L_0x002d:
            r9 = 15
            int r10 = r20.size()
            int r9 = java.lang.Math.min(r9, r10)
            if (r8 >= r9) goto L_0x026a
            java.lang.Object r9 = r1.get(r8)
            org.telegram.messenger.MediaDataController$KeywordResult r9 = (org.telegram.messenger.MediaDataController.KeywordResult) r9
            java.lang.String r9 = r9.emoji
            if (r9 != 0) goto L_0x0046
            r0 = r1
            goto L_0x0262
        L_0x0046:
            r4.clear()
            int r10 = r0.currentAccount
            org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)
            boolean r10 = r10.isPremium()
            java.util.ArrayList<java.lang.String> r11 = org.telegram.messenger.Emoji.recentEmoji
            java.lang.String r12 = "animated_"
            r13 = 0
            if (r11 == 0) goto L_0x00ac
            r11 = 0
        L_0x005b:
            java.util.ArrayList<java.lang.String> r14 = org.telegram.messenger.Emoji.recentEmoji
            int r14 = r14.size()
            if (r11 >= r14) goto L_0x00ac
            java.util.ArrayList<java.lang.String> r14 = org.telegram.messenger.Emoji.recentEmoji
            java.lang.Object r14 = r14.get(r11)
            java.lang.String r14 = (java.lang.String) r14
            boolean r14 = r14.startsWith(r12)
            if (r14 == 0) goto L_0x00a2
            java.util.ArrayList<java.lang.String> r14 = org.telegram.messenger.Emoji.recentEmoji     // Catch:{ Exception -> 0x00a1 }
            java.lang.Object r14 = r14.get(r11)     // Catch:{ Exception -> 0x00a1 }
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ Exception -> 0x00a1 }
            r15 = 9
            java.lang.String r14 = r14.substring(r15)     // Catch:{ Exception -> 0x00a1 }
            long r14 = java.lang.Long.parseLong(r14)     // Catch:{ Exception -> 0x00a1 }
            int r6 = r0.currentAccount     // Catch:{ Exception -> 0x00a1 }
            org.telegram.tgnet.TLRPC$Document r6 = org.telegram.ui.Components.AnimatedEmojiDrawable.findDocument(r6, r14)     // Catch:{ Exception -> 0x00a1 }
            if (r6 == 0) goto L_0x00a2
            if (r10 != 0) goto L_0x0093
            boolean r14 = org.telegram.messenger.MessageObject.isFreeEmoji(r6)     // Catch:{ Exception -> 0x00a1 }
            if (r14 == 0) goto L_0x00a2
        L_0x0093:
            java.lang.String r14 = org.telegram.messenger.MessageObject.findAnimatedEmojiEmoticon(r6, r13)     // Catch:{ Exception -> 0x00a1 }
            boolean r14 = r9.equals(r14)     // Catch:{ Exception -> 0x00a1 }
            if (r14 == 0) goto L_0x00a2
            r4.add(r6)     // Catch:{ Exception -> 0x00a1 }
            goto L_0x00a2
        L_0x00a1:
        L_0x00a2:
            int r6 = r4.size()
            if (r6 < r5) goto L_0x00a9
            goto L_0x00ac
        L_0x00a9:
            int r11 = r11 + 1
            goto L_0x005b
        L_0x00ac:
            int r6 = r4.size()
            if (r6 >= r5) goto L_0x0175
            r6 = r21[r7]
            if (r6 == 0) goto L_0x0175
            r6 = 0
        L_0x00b7:
            r11 = r21[r7]
            int r11 = r11.size()
            if (r6 >= r11) goto L_0x0175
            r11 = r21[r7]
            java.lang.Object r11 = r11.get(r6)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r11 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r11
            if (r11 == 0) goto L_0x0160
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r14 = r11.documents
            if (r14 == 0) goto L_0x0160
            r14 = 0
        L_0x00ce:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r15 = r11.documents
            int r15 = r15.size()
            if (r14 >= r15) goto L_0x0160
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r15 = r11.documents
            java.lang.Object r15 = r15.get(r14)
            org.telegram.tgnet.TLRPC$Document r15 = (org.telegram.tgnet.TLRPC$Document) r15
            if (r15 == 0) goto L_0x014e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r15.attributes
            if (r13 == 0) goto L_0x014e
            boolean r13 = r4.contains(r15)
            if (r13 != 0) goto L_0x014e
            r13 = 0
        L_0x00eb:
            int r7 = r4.size()
            if (r13 >= r7) goto L_0x0110
            java.lang.Object r7 = r4.get(r13)
            org.telegram.tgnet.TLRPC$Document r7 = (org.telegram.tgnet.TLRPC$Document) r7
            r17 = r11
            r16 = r12
            long r11 = r7.id
            long r0 = r15.id
            int r7 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1))
            if (r7 != 0) goto L_0x0105
            r0 = 1
            goto L_0x0115
        L_0x0105:
            int r13 = r13 + 1
            r0 = r18
            r1 = r20
            r12 = r16
            r11 = r17
            goto L_0x00eb
        L_0x0110:
            r17 = r11
            r16 = r12
            r0 = 0
        L_0x0115:
            if (r0 == 0) goto L_0x0118
            goto L_0x0152
        L_0x0118:
            r0 = 0
        L_0x0119:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r15.attributes
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x0133
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r15.attributes
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$DocumentAttribute r1 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r1
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeCustomEmoji
            if (r7 == 0) goto L_0x0130
            org.telegram.tgnet.TLRPC$TL_documentAttributeCustomEmoji r1 = (org.telegram.tgnet.TLRPC$TL_documentAttributeCustomEmoji) r1
            goto L_0x0134
        L_0x0130:
            int r0 = r0 + 1
            goto L_0x0119
        L_0x0133:
            r1 = 0
        L_0x0134:
            if (r1 == 0) goto L_0x0152
            java.lang.String r0 = r1.alt
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x0152
            if (r10 != 0) goto L_0x0144
            boolean r0 = r1.free
            if (r0 == 0) goto L_0x0152
        L_0x0144:
            r4.add(r15)
            int r0 = r4.size()
            if (r0 < r5) goto L_0x0152
            goto L_0x0162
        L_0x014e:
            r17 = r11
            r16 = r12
        L_0x0152:
            int r14 = r14 + 1
            r0 = r18
            r1 = r20
            r12 = r16
            r11 = r17
            r7 = 0
            r13 = 0
            goto L_0x00ce
        L_0x0160:
            r16 = r12
        L_0x0162:
            int r0 = r4.size()
            if (r0 < r5) goto L_0x0169
            goto L_0x0177
        L_0x0169:
            int r6 = r6 + 1
            r0 = r18
            r1 = r20
            r12 = r16
            r7 = 0
            r13 = 0
            goto L_0x00b7
        L_0x0175:
            r16 = r12
        L_0x0177:
            int r0 = r4.size()
            if (r0 >= r5) goto L_0x021a
            if (r2 == 0) goto L_0x021a
            r0 = 0
        L_0x0180:
            int r1 = r2.size()
            if (r0 >= r1) goto L_0x021a
            java.lang.Object r1 = r2.get(r0)
            org.telegram.tgnet.TLRPC$StickerSetCovered r1 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r1
            if (r1 != 0) goto L_0x0190
            goto L_0x0216
        L_0x0190:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered
            if (r6 == 0) goto L_0x0199
            org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered r1 = (org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered) r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r1.documents
            goto L_0x019b
        L_0x0199:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r1.covers
        L_0x019b:
            if (r1 != 0) goto L_0x019f
            goto L_0x0216
        L_0x019f:
            r6 = 0
        L_0x01a0:
            int r7 = r1.size()
            if (r6 >= r7) goto L_0x020f
            java.lang.Object r7 = r1.get(r6)
            org.telegram.tgnet.TLRPC$Document r7 = (org.telegram.tgnet.TLRPC$Document) r7
            if (r7 == 0) goto L_0x020c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r11 = r7.attributes
            if (r11 == 0) goto L_0x020c
            boolean r11 = r4.contains(r7)
            if (r11 != 0) goto L_0x020c
            r11 = 0
        L_0x01b9:
            int r12 = r4.size()
            if (r11 >= r12) goto L_0x01d2
            java.lang.Object r12 = r4.get(r11)
            org.telegram.tgnet.TLRPC$Document r12 = (org.telegram.tgnet.TLRPC$Document) r12
            long r12 = r12.id
            long r14 = r7.id
            int r17 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r17 != 0) goto L_0x01cf
            r11 = 1
            goto L_0x01d3
        L_0x01cf:
            int r11 = r11 + 1
            goto L_0x01b9
        L_0x01d2:
            r11 = 0
        L_0x01d3:
            if (r11 == 0) goto L_0x01d6
            goto L_0x020c
        L_0x01d6:
            r11 = 0
        L_0x01d7:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r7.attributes
            int r12 = r12.size()
            if (r11 >= r12) goto L_0x01f1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r7.attributes
            java.lang.Object r12 = r12.get(r11)
            org.telegram.tgnet.TLRPC$DocumentAttribute r12 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r12
            boolean r13 = r12 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeCustomEmoji
            if (r13 == 0) goto L_0x01ee
            org.telegram.tgnet.TLRPC$TL_documentAttributeCustomEmoji r12 = (org.telegram.tgnet.TLRPC$TL_documentAttributeCustomEmoji) r12
            goto L_0x01f2
        L_0x01ee:
            int r11 = r11 + 1
            goto L_0x01d7
        L_0x01f1:
            r12 = 0
        L_0x01f2:
            if (r12 == 0) goto L_0x020c
            java.lang.String r11 = r12.alt
            boolean r11 = r9.equals(r11)
            if (r11 == 0) goto L_0x020c
            if (r10 != 0) goto L_0x0202
            boolean r11 = r12.free
            if (r11 == 0) goto L_0x020c
        L_0x0202:
            r4.add(r7)
            int r7 = r4.size()
            if (r7 < r5) goto L_0x020c
            goto L_0x020f
        L_0x020c:
            int r6 = r6 + 1
            goto L_0x01a0
        L_0x020f:
            int r1 = r4.size()
            if (r1 < r5) goto L_0x0216
            goto L_0x021a
        L_0x0216:
            int r0 = r0 + 1
            goto L_0x0180
        L_0x021a:
            boolean r0 = r4.isEmpty()
            if (r0 != 0) goto L_0x0260
            r0 = r20
            java.lang.Object r1 = r0.get(r8)
            org.telegram.messenger.MediaDataController$KeywordResult r1 = (org.telegram.messenger.MediaDataController.KeywordResult) r1
            java.lang.String r1 = r1.keyword
            r6 = 0
        L_0x022b:
            int r7 = r4.size()
            if (r6 >= r7) goto L_0x0262
            java.lang.Object r7 = r4.get(r6)
            org.telegram.tgnet.TLRPC$Document r7 = (org.telegram.tgnet.TLRPC$Document) r7
            if (r7 == 0) goto L_0x0259
            org.telegram.messenger.MediaDataController$KeywordResult r9 = new org.telegram.messenger.MediaDataController$KeywordResult
            r9.<init>()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r11 = r16
            r10.append(r11)
            long r12 = r7.id
            r10.append(r12)
            java.lang.String r7 = r10.toString()
            r9.emoji = r7
            r9.keyword = r1
            r3.add(r9)
            goto L_0x025b
        L_0x0259:
            r11 = r16
        L_0x025b:
            int r6 = r6 + 1
            r16 = r11
            goto L_0x022b
        L_0x0260:
            r0 = r20
        L_0x0262:
            int r8 = r8 + 1
            r1 = r0
            r7 = 0
            r0 = r18
            goto L_0x002d
        L_0x026a:
            r0 = r1
            r1 = 0
            r0.addAll(r1, r3)
            if (r22 == 0) goto L_0x0274
            r22.run()
        L_0x0274:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$fillWithAnimatedEmoji$195(java.lang.Integer, java.util.ArrayList, java.util.ArrayList[], java.lang.Runnable):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fillWithAnimatedEmoji$197(boolean[] zArr, ArrayList[] arrayListArr, Runnable runnable) {
        loadStickers(5, true, false, false, new MediaDataController$$ExternalSyntheticLambda158(zArr, arrayListArr, runnable));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$fillWithAnimatedEmoji$196(boolean[] zArr, ArrayList[] arrayListArr, Runnable runnable, ArrayList arrayList) {
        if (!zArr[0]) {
            arrayListArr[0] = arrayList;
            runnable.run();
            zArr[0] = true;
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$fillWithAnimatedEmoji$198(boolean[] zArr, Runnable runnable) {
        if (!zArr[0]) {
            runnable.run();
            zArr[0] = true;
        }
    }

    public void loadEmojiThemes() {
        Context context = ApplicationLoader.applicationContext;
        SharedPreferences sharedPreferences = context.getSharedPreferences("emojithemes_config_" + this.currentAccount, 0);
        int i = sharedPreferences.getInt("count", 0);
        final ArrayList arrayList = new ArrayList();
        arrayList.add(new ChatThemeBottomSheet.ChatThemeItem(EmojiThemes.createHomePreviewTheme()));
        for (int i2 = 0; i2 < i; i2++) {
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(sharedPreferences.getString("theme_" + i2, "")));
            try {
                EmojiThemes createPreviewFullTheme = EmojiThemes.createPreviewFullTheme(TLRPC$Theme.TLdeserialize(serializedData, serializedData.readInt32(true), true));
                if (createPreviewFullTheme.items.size() >= 4) {
                    arrayList.add(new ChatThemeBottomSheet.ChatThemeItem(createPreviewFullTheme));
                }
                ChatThemeController.chatThemeQueue.postRunnable(new Runnable() {
                    public void run() {
                        for (int i = 0; i < arrayList.size(); i++) {
                            ((ChatThemeBottomSheet.ChatThemeItem) arrayList.get(i)).chatTheme.loadPreviewColors(0);
                        }
                        AndroidUtilities.runOnUIThread(new MediaDataController$2$$ExternalSyntheticLambda0(this, arrayList));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$0(ArrayList arrayList) {
                        MediaDataController.this.defaultEmojiThemes.clear();
                        MediaDataController.this.defaultEmojiThemes.addAll(arrayList);
                    }
                });
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    public void generateEmojiPreviewThemes(ArrayList<TLRPC$TL_theme> arrayList, final int i) {
        Context context = ApplicationLoader.applicationContext;
        SharedPreferences.Editor edit = context.getSharedPreferences("emojithemes_config_" + i, 0).edit();
        edit.putInt("count", arrayList.size());
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            TLRPC$TL_theme tLRPC$TL_theme = arrayList.get(i2);
            SerializedData serializedData = new SerializedData(tLRPC$TL_theme.getObjectSize());
            tLRPC$TL_theme.serializeToStream(serializedData);
            edit.putString("theme_" + i2, Utilities.bytesToHex(serializedData.toByteArray()));
        }
        edit.apply();
        if (!arrayList.isEmpty()) {
            final ArrayList arrayList2 = new ArrayList();
            arrayList2.add(new ChatThemeBottomSheet.ChatThemeItem(EmojiThemes.createHomePreviewTheme()));
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                EmojiThemes createPreviewFullTheme = EmojiThemes.createPreviewFullTheme(arrayList.get(i3));
                ChatThemeBottomSheet.ChatThemeItem chatThemeItem = new ChatThemeBottomSheet.ChatThemeItem(createPreviewFullTheme);
                if (createPreviewFullTheme.items.size() >= 4) {
                    arrayList2.add(chatThemeItem);
                }
            }
            ChatThemeController.chatThemeQueue.postRunnable(new Runnable() {
                public void run() {
                    for (int i = 0; i < arrayList2.size(); i++) {
                        ((ChatThemeBottomSheet.ChatThemeItem) arrayList2.get(i)).chatTheme.loadPreviewColors(i);
                    }
                    AndroidUtilities.runOnUIThread(new MediaDataController$3$$ExternalSyntheticLambda0(this, arrayList2));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$0(ArrayList arrayList) {
                    MediaDataController.this.defaultEmojiThemes.clear();
                    MediaDataController.this.defaultEmojiThemes.addAll(arrayList);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiPreviewThemesChanged, new Object[0]);
                }
            });
            return;
        }
        this.defaultEmojiThemes.clear();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiPreviewThemesChanged, new Object[0]);
    }

    public ArrayList<TLRPC$EmojiStatus> getDefaultEmojiStatuses() {
        if (!this.emojiStatusesFromCacheFetched[1]) {
            fetchEmojiStatuses(1, true);
        } else if (this.emojiStatusesFetchDate[1] == null || (System.currentTimeMillis() / 1000) - this.emojiStatusesFetchDate[1].longValue() > 1800) {
            fetchEmojiStatuses(1, false);
        }
        return this.emojiStatuses[1];
    }

    public ArrayList<TLRPC$EmojiStatus> getRecentEmojiStatuses() {
        if (!this.emojiStatusesFromCacheFetched[0]) {
            fetchEmojiStatuses(0, true);
        } else if (this.emojiStatusesFetchDate[0] == null || (System.currentTimeMillis() / 1000) - this.emojiStatusesFetchDate[0].longValue() > 1800) {
            fetchEmojiStatuses(0, false);
        }
        return this.emojiStatuses[0];
    }

    public ArrayList<TLRPC$EmojiStatus> clearRecentEmojiStatuses() {
        ArrayList<TLRPC$EmojiStatus>[] arrayListArr = this.emojiStatuses;
        if (arrayListArr[0] != null) {
            arrayListArr[0].clear();
        }
        this.emojiStatusesHash[0] = 0;
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda21(this));
        return this.emojiStatuses[0];
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecentEmojiStatuses$199() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_statuses WHERE type = 0").stepThis().dispose();
        } catch (Exception unused) {
        }
    }

    public void pushRecentEmojiStatus(TLRPC$EmojiStatus tLRPC$EmojiStatus) {
        if (this.emojiStatuses[0] != null) {
            if (tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatus) {
                long j = ((TLRPC$TL_emojiStatus) tLRPC$EmojiStatus).document_id;
                int i = 0;
                while (i < this.emojiStatuses[0].size()) {
                    if ((this.emojiStatuses[0].get(i) instanceof TLRPC$TL_emojiStatus) && ((TLRPC$TL_emojiStatus) this.emojiStatuses[0].get(i)).document_id == j) {
                        this.emojiStatuses[0].remove(i);
                        i--;
                    }
                    i++;
                }
            }
            this.emojiStatuses[0].add(0, tLRPC$EmojiStatus);
            while (this.emojiStatuses[0].size() > 50) {
                ArrayList<TLRPC$EmojiStatus>[] arrayListArr = this.emojiStatuses;
                arrayListArr[0].remove(arrayListArr[0].size() - 1);
            }
            TLRPC$TL_account_emojiStatuses tLRPC$TL_account_emojiStatuses = new TLRPC$TL_account_emojiStatuses();
            tLRPC$TL_account_emojiStatuses.hash = this.emojiStatusesHash[0];
            tLRPC$TL_account_emojiStatuses.statuses = this.emojiStatuses[0];
            updateEmojiStatuses(0, tLRPC$TL_account_emojiStatuses);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.tgnet.TLRPC$TL_account_getDefaultEmojiStatuses} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.tgnet.TLRPC$TL_account_getRecentEmojiStatuses} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: org.telegram.tgnet.TLRPC$TL_account_getDefaultEmojiStatuses} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: org.telegram.tgnet.TLRPC$TL_account_getDefaultEmojiStatuses} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void fetchEmojiStatuses(int r4, boolean r5) {
        /*
            r3 = this;
            boolean[] r0 = r3.emojiStatusesFetching
            boolean r1 = r0[r4]
            if (r1 == 0) goto L_0x0007
            return
        L_0x0007:
            r1 = 1
            r0[r4] = r1
            if (r5 == 0) goto L_0x001d
            org.telegram.messenger.MessagesStorage r5 = r3.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r5 = r5.getStorageQueue()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda22 r0 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda22
            r0.<init>(r3, r4)
            r5.postRunnable(r0)
            goto L_0x0044
        L_0x001d:
            if (r4 != 0) goto L_0x002b
            org.telegram.tgnet.TLRPC$TL_account_getRecentEmojiStatuses r5 = new org.telegram.tgnet.TLRPC$TL_account_getRecentEmojiStatuses
            r5.<init>()
            long[] r0 = r3.emojiStatusesHash
            r1 = r0[r4]
            r5.hash = r1
            goto L_0x0036
        L_0x002b:
            org.telegram.tgnet.TLRPC$TL_account_getDefaultEmojiStatuses r5 = new org.telegram.tgnet.TLRPC$TL_account_getDefaultEmojiStatuses
            r5.<init>()
            long[] r0 = r3.emojiStatusesHash
            r1 = r0[r4]
            r5.hash = r1
        L_0x0036:
            int r0 = r3.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda172 r1 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda172
            r1.<init>(r3, r4)
            r0.sendRequest(r5, r1)
        L_0x0044:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.fetchEmojiStatuses(int, boolean):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x007c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$fetchEmojiStatuses$201(int r9) {
        /*
            r8 = this;
            r0 = 1
            r1 = 0
            org.telegram.messenger.MessagesStorage r2 = r8.getMessagesStorage()     // Catch:{ Exception -> 0x0064 }
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ Exception -> 0x0064 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0064 }
            r3.<init>()     // Catch:{ Exception -> 0x0064 }
            java.lang.String r4 = "SELECT data FROM emoji_statuses WHERE type = "
            r3.append(r4)     // Catch:{ Exception -> 0x0064 }
            r3.append(r9)     // Catch:{ Exception -> 0x0064 }
            java.lang.String r4 = " LIMIT 1"
            r3.append(r4)     // Catch:{ Exception -> 0x0064 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0064 }
            java.lang.Object[] r4 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x0064 }
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch:{ Exception -> 0x0064 }
            boolean r3 = r2.next()     // Catch:{ Exception -> 0x0064 }
            if (r3 == 0) goto L_0x005d
            int r3 = r2.getColumnCount()     // Catch:{ Exception -> 0x0064 }
            if (r3 <= 0) goto L_0x005d
            boolean r3 = r2.isNull(r1)     // Catch:{ Exception -> 0x0064 }
            if (r3 != 0) goto L_0x005d
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r1)     // Catch:{ Exception -> 0x0064 }
            if (r3 == 0) goto L_0x005d
            int r4 = r3.readInt32(r1)     // Catch:{ Exception -> 0x0064 }
            org.telegram.tgnet.TLRPC$account_EmojiStatuses r4 = org.telegram.tgnet.TLRPC$account_EmojiStatuses.TLdeserialize(r3, r4, r1)     // Catch:{ Exception -> 0x0064 }
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_account_emojiStatuses     // Catch:{ Exception -> 0x0064 }
            if (r5 == 0) goto L_0x0058
            long[] r5 = r8.emojiStatusesHash     // Catch:{ Exception -> 0x0064 }
            long r6 = r4.hash     // Catch:{ Exception -> 0x0064 }
            r5[r9] = r6     // Catch:{ Exception -> 0x0064 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$EmojiStatus>[] r5 = r8.emojiStatuses     // Catch:{ Exception -> 0x0064 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$EmojiStatus> r4 = r4.statuses     // Catch:{ Exception -> 0x0064 }
            r5[r9] = r4     // Catch:{ Exception -> 0x0064 }
            r4 = 1
            goto L_0x0059
        L_0x0058:
            r4 = 0
        L_0x0059:
            r3.reuse()     // Catch:{ Exception -> 0x0062 }
            goto L_0x005e
        L_0x005d:
            r4 = 0
        L_0x005e:
            r2.dispose()     // Catch:{ Exception -> 0x0062 }
            goto L_0x0069
        L_0x0062:
            r2 = move-exception
            goto L_0x0066
        L_0x0064:
            r2 = move-exception
            r4 = 0
        L_0x0066:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0069:
            boolean[] r2 = r8.emojiStatusesFromCacheFetched
            r2[r9] = r0
            boolean[] r0 = r8.emojiStatusesFetching
            r0[r9] = r1
            if (r4 == 0) goto L_0x007c
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda10 r9 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda10
            r9.<init>(r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r9)
            goto L_0x007f
        L_0x007c:
            r8.fetchEmojiStatuses(r9, r1)
        L_0x007f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$fetchEmojiStatuses$201(int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchEmojiStatuses$200() {
        getNotificationCenter().postNotificationName(NotificationCenter.recentEmojiStatusesUpdate, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchEmojiStatuses$203(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.emojiStatusesFetchDate[i] = Long.valueOf(System.currentTimeMillis() / 1000);
        if (tLObject instanceof TLRPC$TL_account_emojiStatusesNotModified) {
            this.emojiStatusesFetching[i] = false;
        } else if (tLObject instanceof TLRPC$TL_account_emojiStatuses) {
            TLRPC$TL_account_emojiStatuses tLRPC$TL_account_emojiStatuses = (TLRPC$TL_account_emojiStatuses) tLObject;
            this.emojiStatusesHash[i] = tLRPC$TL_account_emojiStatuses.hash;
            this.emojiStatuses[i] = tLRPC$TL_account_emojiStatuses.statuses;
            updateEmojiStatuses(i, tLRPC$TL_account_emojiStatuses);
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda15(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchEmojiStatuses$202() {
        getNotificationCenter().postNotificationName(NotificationCenter.recentEmojiStatusesUpdate, new Object[0]);
    }

    private void updateEmojiStatuses(int i, TLRPC$TL_account_emojiStatuses tLRPC$TL_account_emojiStatuses) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda31(this, i, tLRPC$TL_account_emojiStatuses));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEmojiStatuses$204(int i, TLRPC$TL_account_emojiStatuses tLRPC$TL_account_emojiStatuses) {
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            database.executeFast("DELETE FROM emoji_statuses WHERE type = " + i).stepThis().dispose();
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("INSERT INTO emoji_statuses VALUES(?, ?)");
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLRPC$TL_account_emojiStatuses.getObjectSize());
            tLRPC$TL_account_emojiStatuses.serializeToStream(nativeByteBuffer);
            executeFast.bindByteBuffer(1, nativeByteBuffer);
            executeFast.bindInteger(2, i);
            executeFast.step();
            nativeByteBuffer.reuse();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.emojiStatusesFetching[i] = false;
    }

    public ArrayList<TLRPC$Reaction> getRecentReactions() {
        return this.recentReactions;
    }

    public void clearRecentReactions() {
        this.recentReactions.clear();
        Context context = ApplicationLoader.applicationContext;
        context.getSharedPreferences("recent_reactions_" + this.currentAccount, 0).edit().clear().apply();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_messages_clearRecentReactions(), new RequestDelegate() {
            public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            }
        });
    }

    public ArrayList<TLRPC$Reaction> getTopReactions() {
        return this.topReactions;
    }

    public void loadRecentAndTopReactions(boolean z) {
        if (!this.loadingRecentReactions && this.recentReactions.isEmpty() && !z) {
            Context context = ApplicationLoader.applicationContext;
            SharedPreferences sharedPreferences = context.getSharedPreferences("recent_reactions_" + this.currentAccount, 0);
            Context context2 = ApplicationLoader.applicationContext;
            SharedPreferences sharedPreferences2 = context2.getSharedPreferences("top_reactions_" + this.currentAccount, 0);
            this.recentReactions.clear();
            this.topReactions.clear();
            this.recentReactions.addAll(loadReactionsFromPref(sharedPreferences));
            this.topReactions.addAll(loadReactionsFromPref(sharedPreferences2));
            this.loadingRecentReactions = true;
            TLRPC$TL_messages_getRecentReactions tLRPC$TL_messages_getRecentReactions = new TLRPC$TL_messages_getRecentReactions();
            tLRPC$TL_messages_getRecentReactions.hash = sharedPreferences.getLong("hash", 0);
            tLRPC$TL_messages_getRecentReactions.limit = 50;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getRecentReactions, new MediaDataController$$ExternalSyntheticLambda189(this, sharedPreferences));
            TLRPC$TL_messages_getTopReactions tLRPC$TL_messages_getTopReactions = new TLRPC$TL_messages_getTopReactions();
            tLRPC$TL_messages_getTopReactions.hash = sharedPreferences2.getLong("hash", 0);
            tLRPC$TL_messages_getTopReactions.limit = 100;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getTopReactions, new MediaDataController$$ExternalSyntheticLambda188(this, sharedPreferences2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRecentAndTopReactions$205(SharedPreferences sharedPreferences, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            if (tLObject instanceof TLRPC$TL_messages_reactions) {
                TLRPC$TL_messages_reactions tLRPC$TL_messages_reactions = (TLRPC$TL_messages_reactions) tLObject;
                this.recentReactions.clear();
                this.recentReactions.addAll(tLRPC$TL_messages_reactions.reactions);
                saveReactionsToPref(sharedPreferences, tLRPC$TL_messages_reactions.hash, tLRPC$TL_messages_reactions.reactions);
            }
            boolean z = tLObject instanceof TLRPC$TL_messages_reactionsNotModified;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRecentAndTopReactions$206(SharedPreferences sharedPreferences, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            if (tLObject instanceof TLRPC$TL_messages_reactions) {
                TLRPC$TL_messages_reactions tLRPC$TL_messages_reactions = (TLRPC$TL_messages_reactions) tLObject;
                this.topReactions.clear();
                this.topReactions.addAll(tLRPC$TL_messages_reactions.reactions);
                saveReactionsToPref(sharedPreferences, tLRPC$TL_messages_reactions.hash, tLRPC$TL_messages_reactions.reactions);
            }
            boolean z = tLObject instanceof TLRPC$TL_messages_reactionsNotModified;
        }
    }

    public static void saveReactionsToPref(SharedPreferences sharedPreferences, long j, ArrayList<? extends TLObject> arrayList) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("count", arrayList.size());
        edit.putLong("hash", j);
        for (int i = 0; i < arrayList.size(); i++) {
            TLObject tLObject = (TLObject) arrayList.get(i);
            SerializedData serializedData = new SerializedData(tLObject.getObjectSize());
            tLObject.serializeToStream(serializedData);
            edit.putString("object_" + i, Utilities.bytesToHex(serializedData.toByteArray()));
        }
        edit.apply();
    }

    public static ArrayList<TLRPC$Reaction> loadReactionsFromPref(SharedPreferences sharedPreferences) {
        int i = sharedPreferences.getInt("count", 0);
        ArrayList<TLRPC$Reaction> arrayList = new ArrayList<>(i);
        if (i > 0) {
            for (int i2 = 0; i2 < i; i2++) {
                SerializedData serializedData = new SerializedData(Utilities.hexToBytes(sharedPreferences.getString("object_" + i2, "")));
                try {
                    arrayList.add(TLRPC$Reaction.TLdeserialize(serializedData, serializedData.readInt32(true), true));
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
        return arrayList;
    }
}
