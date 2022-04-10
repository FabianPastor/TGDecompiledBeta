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
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import androidx.core.content.pm.ShortcutManagerCompat;
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
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.messenger.ringtone.RingtoneUploader;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$DraftMessage;
import org.telegram.tgnet.TLRPC$EmojiKeyword;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$MessagesFilter;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_account_saveRingtone;
import org.telegram.tgnet.TLRPC$TL_account_savedRingtoneConverted;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon;
import org.telegram.tgnet.TLRPC$TL_attachMenuBots;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotsNotModified;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
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
import org.telegram.tgnet.TLRPC$TL_error;
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
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
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
import org.telegram.tgnet.TLRPC$TL_messages_faveSticker;
import org.telegram.tgnet.TLRPC$TL_messages_favedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_featuredStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getAllDrafts;
import org.telegram.tgnet.TLRPC$TL_messages_getArchivedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachMenuBots;
import org.telegram.tgnet.TLRPC$TL_messages_getAvailableReactions;
import org.telegram.tgnet.TLRPC$TL_messages_getFeaturedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getScheduledMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getSearchCounters;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC$TL_messages_readFeaturedStickers;
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
import org.telegram.tgnet.TLRPC$TL_topPeer;
import org.telegram.tgnet.TLRPC$TL_topPeerCategoryBotsInline;
import org.telegram.tgnet.TLRPC$TL_topPeerCategoryCorrespondents;
import org.telegram.tgnet.TLRPC$TL_topPeerCategoryPeers;
import org.telegram.tgnet.TLRPC$TL_updateBotCommands;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.tgnet.TLRPC$messages_StickerSet;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.Bulletin;
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
    private static volatile MediaDataController[] Instance = new MediaDataController[3];
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
    public static final int TYPE_FAVE = 2;
    public static final int TYPE_FEATURED = 3;
    public static final int TYPE_GREETINGS = 3;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MASK = 1;
    private static RectF bitmapRect;
    private static Comparator<TLRPC$MessageEntity> entityComparator = MediaDataController$$ExternalSyntheticLambda118.INSTANCE;
    private static Paint erasePaint;
    private static Paint roundPaint;
    private static Path roundPath;
    private HashMap<String, ArrayList<TLRPC$Document>> allStickers = new HashMap<>();
    private HashMap<String, ArrayList<TLRPC$Document>> allStickersFeatured = new HashMap<>();
    private int[] archivedStickersCount = new int[2];
    private TLRPC$TL_attachMenuBots attachMenuBots = new TLRPC$TL_attachMenuBots();
    private HashMap<String, TLRPC$BotInfo> botInfos = new HashMap<>();
    private LongSparseArray<TLRPC$Message> botKeyboards = new LongSparseArray<>();
    private SparseLongArray botKeyboardsByMids = new SparseLongArray();
    private HashMap<String, Boolean> currentFetchingEmoji = new HashMap<>();
    private LongSparseArray<String> diceEmojiStickerSetsById = new LongSparseArray<>();
    private HashMap<String, TLRPC$TL_messages_stickerSet> diceStickerSetsByEmoji = new HashMap<>();
    private String doubleTapReaction;
    private LongSparseArray<SparseArray<TLRPC$Message>> draftMessages = new LongSparseArray<>();
    private SharedPreferences draftPreferences;
    private LongSparseArray<SparseArray<TLRPC$DraftMessage>> drafts = new LongSparseArray<>();
    private LongSparseArray<Integer> draftsFolderIds = new LongSparseArray<>();
    private List<TLRPC$TL_availableReaction> enabledReactionsList = new ArrayList();
    private ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = new ArrayList<>();
    private LongSparseArray<TLRPC$StickerSetCovered> featuredStickerSetsById = new LongSparseArray<>();
    private boolean featuredStickersLoaded;
    private TLRPC$Document greetingsSticker;
    private LongSparseArray<TLRPC$TL_messages_stickerSet> groupStickerSets = new LongSparseArray<>();
    public ArrayList<TLRPC$TL_topPeer> hints = new ArrayList<>();
    private boolean inTransaction;
    public ArrayList<TLRPC$TL_topPeer> inlineBots = new ArrayList<>();
    private LongSparseArray<TLRPC$TL_messages_stickerSet> installedStickerSetsById = new LongSparseArray<>();
    private boolean isLoadingMenuBots;
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
    private int[] loadDate = new int[5];
    private int loadFeaturedDate;
    private long loadFeaturedHash;
    private long[] loadHash = new long[5];
    boolean loaded;
    boolean loading;
    private HashSet<String> loadingDiceStickerSets = new HashSet<>();
    private boolean loadingDrafts;
    private boolean loadingFeaturedStickers;
    private boolean loadingMoreSearchMessages;
    private LongSparseArray<Boolean> loadingPinnedMessages = new LongSparseArray<>();
    private boolean loadingRecentGifs;
    private boolean[] loadingRecentStickers = new boolean[4];
    private boolean[] loadingStickers = new boolean[5];
    private int menuBotsUpdateDate;
    private long menuBotsUpdateHash;
    private int mergeReqId;
    private int[] messagesSearchCount = {0, 0};
    private boolean[] messagesSearchEndReached = {false, false};
    private List<TLRPC$TL_availableReaction> reactionsList = new ArrayList();
    private HashMap<String, TLRPC$TL_availableReaction> reactionsMap = new HashMap<>();
    private int reactionsUpdateDate;
    private int reactionsUpdateHash;
    private ArrayList<Long> readingStickerSets = new ArrayList<>();
    private ArrayList<TLRPC$Document> recentGifs = new ArrayList<>();
    private boolean recentGifsLoaded;
    private ArrayList<TLRPC$Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};
    private boolean[] recentStickersLoaded = new boolean[4];
    private LongSparseArray<Runnable> removingStickerSetsUndos = new LongSparseArray<>();
    private int reqId;
    public final RingtoneDataStore ringtoneDataStore;
    public HashMap<String, RingtoneUploader> ringtoneUploaderHashMap = new HashMap<>();
    private Runnable[] scheduledLoadStickers = new Runnable[5];
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();
    private SparseArray<MessageObject>[] searchResultMessagesMap = {new SparseArray<>(), new SparseArray<>()};
    private ArrayList<TLRPC$TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>(), new ArrayList<>(0), new ArrayList<>(), new ArrayList<>()};
    private LongSparseArray<TLRPC$TL_messages_stickerSet> stickerSetsById = new LongSparseArray<>();
    private ConcurrentHashMap<String, TLRPC$TL_messages_stickerSet> stickerSetsByName = new ConcurrentHashMap<>(100, 1.0f, 1);
    private LongSparseArray<String> stickersByEmoji = new LongSparseArray<>();
    private LongSparseArray<TLRPC$Document>[] stickersByIds = {new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>(), new LongSparseArray<>()};
    private boolean[] stickersLoaded = new boolean[5];
    private ArrayList<Long> unreadStickerSets = new ArrayList<>();
    private HashMap<String, ArrayList<TLRPC$Message>> verifyingMessages = new HashMap<>();

    public static class KeywordResult {
        public String emoji;
        public String keyword;
    }

    public interface KeywordResultCallback {
        void run(ArrayList<KeywordResult> arrayList, String str);
    }

    public static long calcHash(long j, long j2) {
        return (((j ^ (j2 >> 21)) ^ (j2 << 35)) ^ (j2 >> 4)) + j2;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$markFaturedStickersAsRead$39(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$markFaturedStickersByIdAsRead$40(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$removeInline$105(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$removePeer$106(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$saveDraft$138(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static MediaDataController getInstance(int i) {
        MediaDataController mediaDataController = Instance[i];
        if (mediaDataController == null) {
            synchronized (MediaDataController.class) {
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
                            sparseArray.put(str.startsWith("t_") ? Utilities.parseInt(str.substring(str.lastIndexOf(95) + 1)).intValue() : 0, TLdeserialize);
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
                    sparseArray2.put(z ? Utilities.parseInt(str.substring(str.lastIndexOf(95) + 1)).intValue() : 0, TLdeserialize2);
                }
                serializedData.cleanup();
            } catch (Exception unused) {
            }
        }
        loadStickersByEmojiOrName("tg_placeholders_android", false, true);
        this.ringtoneDataStore = new RingtoneDataStore(this.currentAccount);
    }

    public void cleanup() {
        int i = 0;
        while (true) {
            ArrayList<TLRPC$Document>[] arrayListArr = this.recentStickers;
            if (i >= arrayListArr.length) {
                break;
            }
            arrayListArr[i].clear();
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
        this.loadFeaturedDate = 0;
        this.loadFeaturedHash = 0;
        this.allStickers.clear();
        this.allStickersFeatured.clear();
        this.stickersByEmoji.clear();
        this.featuredStickerSetsById.clear();
        this.featuredStickerSets.clear();
        this.unreadStickerSets.clear();
        this.recentGifs.clear();
        this.stickerSetsById.clear();
        this.installedStickerSetsById.clear();
        this.stickerSetsByName.clear();
        this.diceStickerSetsByEmoji.clear();
        this.diceEmojiStickerSetsById.clear();
        this.loadingDiceStickerSets.clear();
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = false;
        this.loadingRecentGifs = false;
        this.recentGifsLoaded = false;
        this.currentFetchingEmoji.clear();
        if (Build.VERSION.SDK_INT >= 25) {
            Utilities.globalQueue.postRunnable(MediaDataController$$ExternalSyntheticLambda115.INSTANCE);
        }
        this.verifyingMessages.clear();
        this.loading = false;
        this.loaded = false;
        this.hints.clear();
        this.inlineBots.clear();
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda4(this));
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

    public TLRPC$TL_attachMenuBots getAttachMenuBots() {
        return this.attachMenuBots;
    }

    public void loadAttachMenuBots(boolean z, boolean z2) {
        long j;
        this.isLoadingMenuBots = true;
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda5(this));
            return;
        }
        TLRPC$TL_messages_getAttachMenuBots tLRPC$TL_messages_getAttachMenuBots = new TLRPC$TL_messages_getAttachMenuBots();
        if (z2) {
            j = 0;
        } else {
            j = this.menuBotsUpdateHash;
        }
        tLRPC$TL_messages_getAttachMenuBots.hash = j;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getAttachMenuBots, new MediaDataController$$ExternalSyntheticLambda128(this));
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

    private void processLoadedMenuBots(TLRPC$TL_attachMenuBots tLRPC$TL_attachMenuBots, long j, int i, boolean z) {
        if (!(tLRPC$TL_attachMenuBots == null || i == 0)) {
            this.attachMenuBots = tLRPC$TL_attachMenuBots;
            this.menuBotsUpdateHash = j;
        }
        this.menuBotsUpdateDate = i;
        if (tLRPC$TL_attachMenuBots != null) {
            getMessagesController().putUsers(tLRPC$TL_attachMenuBots.users, z);
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda7(this));
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda91(this, tLRPC$TL_attachMenuBots, j, i));
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

    public List<TLRPC$TL_availableReaction> getReactionsList() {
        return this.reactionsList;
    }

    public void loadReactions(boolean z, boolean z2) {
        int i;
        this.isLoadingReactions = true;
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda9(this));
            return;
        }
        TLRPC$TL_messages_getAvailableReactions tLRPC$TL_messages_getAvailableReactions = new TLRPC$TL_messages_getAvailableReactions();
        if (z2) {
            i = 0;
        } else {
            i = this.reactionsUpdateHash;
        }
        tLRPC$TL_messages_getAvailableReactions.hash = i;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getAvailableReactions, new MediaDataController$$ExternalSyntheticLambda126(this));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0076  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadReactions$6() {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadReactions$6():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReactions$7(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        if (tLObject instanceof TLRPC$TL_messages_availableReactionsNotModified) {
            processLoadedReactions((List<TLRPC$TL_availableReaction>) null, 0, currentTimeMillis, false);
        } else if (tLObject instanceof TLRPC$TL_messages_availableReactions) {
            TLRPC$TL_messages_availableReactions tLRPC$TL_messages_availableReactions = (TLRPC$TL_messages_availableReactions) tLObject;
            processLoadedReactions(tLRPC$TL_messages_availableReactions.reactions, tLRPC$TL_messages_availableReactions.hash, currentTimeMillis, false);
        }
    }

    private void processLoadedReactions(List<TLRPC$TL_availableReaction> list, int i, int i2, boolean z) {
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
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda0(list));
        }
        if (!z) {
            putReactionsToCache(list, i, i2);
        } else if (Math.abs((System.currentTimeMillis() / 1000) - ((long) i2)) >= 3600) {
            loadReactions(false, true);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$processLoadedReactions$8(List list) {
        for (int i = 0; i < list.size(); i++) {
            ImageReceiver imageReceiver = new ImageReceiver();
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction = (TLRPC$TL_availableReaction) list.get(i);
            imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.activate_animation), (String) null, (Drawable) null, (String) null, (Object) null, 1);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
            ImageReceiver imageReceiver2 = new ImageReceiver();
            imageReceiver2.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.appear_animation), "60_60_nolimit", (Drawable) null, (String) null, (Object) null, 1);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver2);
            ImageReceiver imageReceiver3 = new ImageReceiver();
            imageReceiver3.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.around_animation), (String) null, (Drawable) null, (String) null, (Object) null, 1);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver3);
            ImageReceiver imageReceiver4 = new ImageReceiver();
            imageReceiver4.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.center_icon), (String) null, (Drawable) null, (String) null, (Object) null, 1);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver4);
            ImageReceiver imageReceiver5 = new ImageReceiver();
            imageReceiver5.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.static_icon), (String) null, (Drawable) null, (String) null, (Object) null, 1);
            ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver5);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.reactionsDidLoad, new Object[0]);
    }

    private void putReactionsToCache(List<TLRPC$TL_availableReaction> list, int i, int i2) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda63(this, list != null ? new ArrayList(list) : null, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putReactionsToCache$9(ArrayList arrayList, int i, int i2) {
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
        if (this.loadingFeaturedStickers) {
            return;
        }
        if (!this.featuredStickersLoaded || Math.abs((System.currentTimeMillis() / 1000) - ((long) this.loadFeaturedDate)) >= 3600) {
            loadFeaturedStickers(true, false);
        }
    }

    public ArrayList<TLRPC$Document> getRecentStickers(int i) {
        ArrayList<TLRPC$Document> arrayList = this.recentStickers[i];
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
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, tLRPC$Document3, 5);
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
                getConnectionsManager().sendRequest(tLRPC$TL_messages_faveSticker, new MediaDataController$$ExternalSyntheticLambda149(this, obj2, tLRPC$TL_messages_faveSticker));
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
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_saveRecentSticker, new MediaDataController$$ExternalSyntheticLambda150(this, obj2, tLRPC$TL_messages_saveRecentSticker));
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
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda23(this, i4, tLRPC$Document2));
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
    public /* synthetic */ void lambda$addRecentSticker$11(Object obj, TLRPC$TL_messages_faveSticker tLRPC$TL_messages_faveSticker, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null || !FileRefController.isFileRefError(tLRPC$TL_error.text) || obj == null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda11(this));
            return;
        }
        getFileRefController().requestReference(obj, tLRPC$TL_messages_faveSticker);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addRecentSticker$10() {
        getMediaDataController().loadRecents(2, false, false, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addRecentSticker$12(Object obj, TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null && FileRefController.isFileRefError(tLRPC$TL_error.text) && obj != null) {
            getFileRefController().requestReference(obj, tLRPC$TL_messages_saveRecentSticker);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addRecentSticker$13(int i, TLRPC$Document tLRPC$Document) {
        int i2 = i == 0 ? 3 : i == 1 ? 4 : 5;
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
        getConnectionsManager().sendRequest(tLRPC$TL_messages_saveGif, new MediaDataController$$ExternalSyntheticLambda159(this, tLRPC$TL_messages_saveGif));
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda83(this, tLRPC$Document));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeRecentGif$14(TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null && FileRefController.isFileRefError(tLRPC$TL_error.text)) {
            getFileRefController().requestReference("gif", tLRPC$TL_messages_saveGif);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeRecentGif$15(TLRPC$Document tLRPC$Document) {
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

    public void addRecentGif(TLRPC$Document tLRPC$Document, int i) {
        boolean z;
        if (tLRPC$Document != null) {
            int i2 = 0;
            while (true) {
                if (i2 >= this.recentGifs.size()) {
                    z = false;
                    break;
                }
                TLRPC$Document tLRPC$Document2 = this.recentGifs.get(i2);
                if (tLRPC$Document2.id == tLRPC$Document.id) {
                    this.recentGifs.remove(i2);
                    this.recentGifs.add(0, tLRPC$Document2);
                    z = true;
                    break;
                }
                i2++;
            }
            if (!z) {
                this.recentGifs.add(0, tLRPC$Document);
            }
            if (this.recentGifs.size() > getMessagesController().maxRecentGifsCount) {
                ArrayList<TLRPC$Document> arrayList = this.recentGifs;
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda84(this, arrayList.remove(arrayList.size() - 1)));
            }
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(tLRPC$Document);
            processLoadedRecentDocuments(0, arrayList2, true, i, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addRecentGif$16(TLRPC$Document tLRPC$Document) {
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
            if (r4 == 0) goto L_0x0071
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r13.documents
            r0.documents = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_stickerPack> r1 = r13.packs
            r0.packs = r1
            org.telegram.tgnet.TLRPC$StickerSet r1 = r13.set
            r0.set = r1
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda98 r1 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda98
            r1.<init>(r12, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x00b4
        L_0x0071:
            androidx.collection.LongSparseArray r4 = new androidx.collection.LongSparseArray
            r4.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r13.documents
            int r6 = r6.size()
            r7 = 0
        L_0x007d:
            if (r7 >= r6) goto L_0x008f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r8 = r13.documents
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC$Document) r8
            long r9 = r8.id
            r4.put(r9, r8)
            int r7 = r7 + 1
            goto L_0x007d
        L_0x008f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r0.documents
            int r6 = r6.size()
            r7 = 0
        L_0x0096:
            if (r1 >= r6) goto L_0x00b3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r8 = r0.documents
            java.lang.Object r8 = r8.get(r1)
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC$Document) r8
            long r8 = r8.id
            java.lang.Object r8 = r4.get(r8)
            org.telegram.tgnet.TLRPC$Document r8 = (org.telegram.tgnet.TLRPC$Document) r8
            if (r8 == 0) goto L_0x00b0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r7 = r0.documents
            r7.set(r1, r8)
            r7 = 1
        L_0x00b0:
            int r1 = r1 + 1
            goto L_0x0096
        L_0x00b3:
            r2 = r7
        L_0x00b4:
            if (r2 == 0) goto L_0x00ec
            if (r3 == 0) goto L_0x00bc
            r12.putSetToCache(r0)
            goto L_0x00ec
        L_0x00bc:
            org.telegram.tgnet.TLRPC$StickerSet r0 = r13.set
            boolean r7 = r0.masks
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
            if (r13 == 0) goto L_0x00ec
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
        L_0x00ec:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.replaceStickerSet(org.telegram.tgnet.TLRPC$TL_messages_stickerSet):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$replaceStickerSet$17(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
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

    private void loadGroupStickerSet(TLRPC$StickerSet tLRPC$StickerSet, boolean z) {
        if (z) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda87(this, tLRPC$StickerSet));
            return;
        }
        TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
        TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
        tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
        tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
        tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new MediaDataController$$ExternalSyntheticLambda127(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadGroupStickerSet$19(TLRPC$StickerSet tLRPC$StickerSet) {
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
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda99(this, tLRPC$TL_messages_stickerSet));
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadGroupStickerSet$18(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.groupStickerSets.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tLRPC$TL_messages_stickerSet.set.id));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadGroupStickerSet$21(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda97(this, (TLRPC$TL_messages_stickerSet) tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadGroupStickerSet$20(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.groupStickerSets.put(tLRPC$TL_messages_stickerSet.set.id, tLRPC$TL_messages_stickerSet);
        getNotificationCenter().postNotificationName(NotificationCenter.groupStickersDidLoad, Long.valueOf(tLRPC$TL_messages_stickerSet.set.id));
    }

    private void putSetToCache(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda96(this, tLRPC$TL_messages_stickerSet));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putSetToCache$22(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
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
        return this.featuredStickerSets;
    }

    public ArrayList<Long> getUnreadStickerSets() {
        return this.unreadStickerSets;
    }

    public boolean areAllTrendingStickerSetsUnread() {
        int size = this.featuredStickerSets.size();
        for (int i = 0; i < size; i++) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = this.featuredStickerSets.get(i);
            if (!isStickerPackInstalled(tLRPC$StickerSetCovered.set.id) && ((!tLRPC$StickerSetCovered.covers.isEmpty() || tLRPC$StickerSetCovered.cover != null) && !this.unreadStickerSets.contains(Long.valueOf(tLRPC$StickerSetCovered.set.id)))) {
                return false;
            }
        }
        return true;
    }

    public boolean isStickerPackInstalled(long j) {
        return this.installedStickerSetsById.indexOfKey(j) >= 0;
    }

    public boolean isStickerPackUnread(long j) {
        return this.unreadStickerSets.contains(Long.valueOf(j));
    }

    public boolean isStickerPackInstalled(String str) {
        return this.stickerSetsByName.containsKey(str);
    }

    public String getEmojiForSticker(long j) {
        String str = this.stickersByEmoji.get(j);
        return str != null ? str : "";
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers} */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        if (r6.recentStickersLoaded[r7] != false) goto L_0x001f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000d, code lost:
        if (r6.recentGifsLoaded != false) goto L_0x001f;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadRecents(int r7, boolean r8, boolean r9, boolean r10) {
        /*
            r6 = this;
            r0 = 0
            r1 = 1
            if (r8 == 0) goto L_0x0010
            boolean r2 = r6.loadingRecentGifs
            if (r2 == 0) goto L_0x0009
            return
        L_0x0009:
            r6.loadingRecentGifs = r1
            boolean r2 = r6.recentGifsLoaded
            if (r2 == 0) goto L_0x0020
            goto L_0x001f
        L_0x0010:
            boolean[] r2 = r6.loadingRecentStickers
            boolean r3 = r2[r7]
            if (r3 == 0) goto L_0x0017
            return
        L_0x0017:
            r2[r7] = r1
            boolean[] r2 = r6.recentStickersLoaded
            boolean r2 = r2[r7]
            if (r2 == 0) goto L_0x0020
        L_0x001f:
            r9 = 0
        L_0x0020:
            if (r9 == 0) goto L_0x0034
            org.telegram.messenger.MessagesStorage r9 = r6.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r9 = r9.getStorageQueue()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda104 r10 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda104
            r10.<init>(r6, r8, r7)
            r9.postRunnable(r10)
            goto L_0x00fd
        L_0x0034:
            int r9 = r6.currentAccount
            android.content.SharedPreferences r9 = org.telegram.messenger.MessagesController.getEmojiSettings(r9)
            r2 = 3
            if (r10 != 0) goto L_0x0083
            r3 = 0
            if (r8 == 0) goto L_0x0048
            java.lang.String r10 = "lastGifLoadTime"
            long r9 = r9.getLong(r10, r3)
            goto L_0x0069
        L_0x0048:
            if (r7 != 0) goto L_0x0051
            java.lang.String r10 = "lastStickersLoadTime"
            long r9 = r9.getLong(r10, r3)
            goto L_0x0069
        L_0x0051:
            if (r7 != r1) goto L_0x005a
            java.lang.String r10 = "lastStickersLoadTimeMask"
            long r9 = r9.getLong(r10, r3)
            goto L_0x0069
        L_0x005a:
            if (r7 != r2) goto L_0x0063
            java.lang.String r10 = "lastStickersLoadTimeGreet"
            long r9 = r9.getLong(r10, r3)
            goto L_0x0069
        L_0x0063:
            java.lang.String r10 = "lastStickersLoadTimeFavs"
            long r9 = r9.getLong(r10, r3)
        L_0x0069:
            long r3 = java.lang.System.currentTimeMillis()
            long r3 = r3 - r9
            long r9 = java.lang.Math.abs(r3)
            r3 = 3600000(0x36ee80, double:1.7786363E-317)
            int r5 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r5 >= 0) goto L_0x0083
            if (r8 == 0) goto L_0x007e
            r6.loadingRecentGifs = r0
            goto L_0x0082
        L_0x007e:
            boolean[] r8 = r6.loadingRecentStickers
            r8[r7] = r0
        L_0x0082:
            return
        L_0x0083:
            if (r8 == 0) goto L_0x009f
            org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs r8 = new org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs
            r8.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r6.recentGifs
            long r9 = calcDocumentsHash(r9)
            r8.hash = r9
            org.telegram.tgnet.ConnectionsManager r9 = r6.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda133 r10 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda133
            r10.<init>(r6, r7)
            r9.sendRequest(r8, r10)
            goto L_0x00fd
        L_0x009f:
            r8 = 2
            if (r7 != r8) goto L_0x00b2
            org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers r8 = new org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers
            r8.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r9 = r6.recentStickers
            r9 = r9[r7]
            long r9 = calcDocumentsHash(r9)
            r8.hash = r9
            goto L_0x00f1
        L_0x00b2:
            if (r7 != r2) goto L_0x00dd
            org.telegram.tgnet.TLRPC$TL_messages_getStickers r8 = new org.telegram.tgnet.TLRPC$TL_messages_getStickers
            r8.<init>()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "👋"
            r9.append(r10)
            java.lang.String r10 = "⭐"
            java.lang.String r10 = org.telegram.messenger.Emoji.fixEmoji(r10)
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.emoticon = r9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r9 = r6.recentStickers
            r9 = r9[r7]
            long r9 = calcDocumentsHash(r9)
            r8.hash = r9
            goto L_0x00f1
        L_0x00dd:
            org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers r8 = new org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers
            r8.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>[] r9 = r6.recentStickers
            r9 = r9[r7]
            long r9 = calcDocumentsHash(r9)
            r8.hash = r9
            if (r7 != r1) goto L_0x00ef
            r0 = 1
        L_0x00ef:
            r8.attached = r0
        L_0x00f1:
            org.telegram.tgnet.ConnectionsManager r9 = r6.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda132 r10 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda132
            r10.<init>(r6, r7)
            r9.sendRequest(r8, r10)
        L_0x00fd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadRecents(int, boolean, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRecents$24(boolean z, int i) {
        NativeByteBuffer byteBufferValue;
        int i2 = 3;
        if (z) {
            i2 = 2;
        } else if (i != 0) {
            i2 = i == 1 ? 4 : i == 3 ? 6 : 5;
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
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda107(this, z, arrayList, i));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRecents$23(boolean z, ArrayList arrayList, int i) {
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
    public /* synthetic */ void lambda$loadRecents$25(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        processLoadedRecentDocuments(i, tLObject instanceof TLRPC$TL_messages_savedGifs ? ((TLRPC$TL_messages_savedGifs) tLObject).gifs : null, true, 0, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadRecents$26(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList<TLRPC$Document> arrayList;
        if (i == 3) {
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
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda106(this, z, i, arrayList, z2, i2));
        }
        if (i2 == 0) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda105(this, z, i, arrayList));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedRecentDocuments$27(boolean z, int i, ArrayList arrayList, boolean z2, int i2) {
        int i3 = i;
        ArrayList arrayList2 = arrayList;
        try {
            SQLiteDatabase database = getMessagesStorage().getDatabase();
            int i4 = 2;
            int i5 = z ? getMessagesController().maxRecentGifsCount : i3 == 3 ? 200 : i3 == 2 ? getMessagesController().maxFaveStickersCount : getMessagesController().maxRecentStickersCount;
            database.beginTransaction();
            SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            int size = arrayList.size();
            int i6 = z ? 2 : i3 == 0 ? 3 : i3 == 1 ? 4 : i3 == 3 ? 6 : 5;
            if (z2) {
                database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + i6).stepThis().dispose();
            }
            int i7 = 0;
            while (true) {
                if (i7 >= size) {
                    break;
                } else if (i7 == i5) {
                    break;
                } else {
                    TLRPC$Document tLRPC$Document = (TLRPC$Document) arrayList2.get(i7);
                    executeFast.requery();
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    int i8 = i7;
                    sb.append(tLRPC$Document.id);
                    executeFast.bindString(1, sb.toString());
                    executeFast.bindInteger(i4, i6);
                    executeFast.bindString(3, "");
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
                    i7 = i8 + 1;
                    i4 = 2;
                }
            }
            executeFast.dispose();
            database.commitTransaction();
            if (arrayList.size() >= i5) {
                database.beginTransaction();
                while (i5 < arrayList.size()) {
                    database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((TLRPC$Document) arrayList2.get(i5)).id + "' AND type = " + i6).stepThis().dispose();
                    i5++;
                }
                database.commitTransaction();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedRecentDocuments$28(boolean z, int i, ArrayList arrayList) {
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

    public void reorderStickers(int i, ArrayList<Long> arrayList) {
        Collections.sort(this.stickerSets[i], new MediaDataController$$ExternalSyntheticLambda117(arrayList));
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
        loadStickers(i, false, true);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$reorderStickers$29(ArrayList arrayList, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2) {
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
        if (this.stickerSetsById.indexOfKey(tLRPC$TL_messages_stickerSet.set.id) < 0 && !this.stickerSetsByName.containsKey(tLRPC$TL_messages_stickerSet.set.short_name)) {
            int i = tLRPC$TL_messages_stickerSet.set.masks;
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
            getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
            loadStickers(i, false, true);
        }
    }

    public void loadFeaturedStickers(boolean z, boolean z2) {
        long j;
        if (!this.loadingFeaturedStickers) {
            this.loadingFeaturedStickers = true;
            if (z) {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda6(this));
                return;
            }
            TLRPC$TL_messages_getFeaturedStickers tLRPC$TL_messages_getFeaturedStickers = new TLRPC$TL_messages_getFeaturedStickers();
            if (z2) {
                j = 0;
            } else {
                j = this.loadFeaturedHash;
            }
            tLRPC$TL_messages_getFeaturedStickers.hash = j;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getFeaturedStickers, new MediaDataController$$ExternalSyntheticLambda158(this, tLRPC$TL_messages_getFeaturedStickers));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0082 A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadFeaturedStickers$30() {
        /*
            r11 = this;
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = 0
            r1 = 0
            r3 = 0
            org.telegram.messenger.MessagesStorage r5 = r11.getMessagesStorage()     // Catch:{ all -> 0x0079 }
            org.telegram.SQLite.SQLiteDatabase r5 = r5.getDatabase()     // Catch:{ all -> 0x0079 }
            java.lang.String r6 = "SELECT data, unread, date, hash FROM stickers_featured WHERE 1"
            java.lang.Object[] r7 = new java.lang.Object[r1]     // Catch:{ all -> 0x0079 }
            org.telegram.SQLite.SQLiteCursor r5 = r5.queryFinalized(r6, r7)     // Catch:{ all -> 0x0079 }
            boolean r6 = r5.next()     // Catch:{ all -> 0x0075 }
            if (r6 == 0) goto L_0x006e
            org.telegram.tgnet.NativeByteBuffer r6 = r5.byteBufferValue(r1)     // Catch:{ all -> 0x0075 }
            if (r6 == 0) goto L_0x0046
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ all -> 0x0075 }
            r7.<init>()     // Catch:{ all -> 0x0075 }
            int r0 = r6.readInt32(r1)     // Catch:{ all -> 0x0044 }
            r8 = 0
        L_0x002f:
            if (r8 >= r0) goto L_0x003f
            int r9 = r6.readInt32(r1)     // Catch:{ all -> 0x0044 }
            org.telegram.tgnet.TLRPC$StickerSetCovered r9 = org.telegram.tgnet.TLRPC$StickerSetCovered.TLdeserialize(r6, r9, r1)     // Catch:{ all -> 0x0044 }
            r7.add(r9)     // Catch:{ all -> 0x0044 }
            int r8 = r8 + 1
            goto L_0x002f
        L_0x003f:
            r6.reuse()     // Catch:{ all -> 0x0044 }
            r0 = r7
            goto L_0x0046
        L_0x0044:
            r0 = move-exception
            goto L_0x007d
        L_0x0046:
            r6 = 1
            org.telegram.tgnet.NativeByteBuffer r6 = r5.byteBufferValue(r6)     // Catch:{ all -> 0x0075 }
            if (r6 == 0) goto L_0x0065
            int r7 = r6.readInt32(r1)     // Catch:{ all -> 0x0075 }
            r8 = 0
        L_0x0052:
            if (r8 >= r7) goto L_0x0062
            long r9 = r6.readInt64(r1)     // Catch:{ all -> 0x0075 }
            java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ all -> 0x0075 }
            r2.add(r9)     // Catch:{ all -> 0x0075 }
            int r8 = r8 + 1
            goto L_0x0052
        L_0x0062:
            r6.reuse()     // Catch:{ all -> 0x0075 }
        L_0x0065:
            r6 = 2
            int r1 = r5.intValue(r6)     // Catch:{ all -> 0x0075 }
            long r3 = r11.calcFeaturedStickersHash(r0)     // Catch:{ all -> 0x0075 }
        L_0x006e:
            r5.dispose()
            r5 = r3
            r4 = r1
            r1 = r0
            goto L_0x0088
        L_0x0075:
            r6 = move-exception
            r7 = r0
            r0 = r6
            goto L_0x007d
        L_0x0079:
            r5 = move-exception
            r7 = r0
            r0 = r5
            r5 = r7
        L_0x007d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x008e }
            if (r5 == 0) goto L_0x0085
            r5.dispose()
        L_0x0085:
            r5 = r3
            r4 = r1
            r1 = r7
        L_0x0088:
            r3 = 1
            r0 = r11
            r0.processLoadedFeaturedStickers(r1, r2, r3, r4, r5)
            return
        L_0x008e:
            r0 = move-exception
            if (r5 == 0) goto L_0x0094
            r5.dispose()
        L_0x0094:
            goto L_0x0096
        L_0x0095:
            throw r0
        L_0x0096:
            goto L_0x0095
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadFeaturedStickers$30():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFeaturedStickers$32(TLRPC$TL_messages_getFeaturedStickers tLRPC$TL_messages_getFeaturedStickers, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda80(this, tLObject, tLRPC$TL_messages_getFeaturedStickers));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFeaturedStickers$31(TLObject tLObject, TLRPC$TL_messages_getFeaturedStickers tLRPC$TL_messages_getFeaturedStickers) {
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TLRPC$TL_messages_featuredStickers) {
            TLRPC$TL_messages_featuredStickers tLRPC$TL_messages_featuredStickers = (TLRPC$TL_messages_featuredStickers) tLObject2;
            processLoadedFeaturedStickers(tLRPC$TL_messages_featuredStickers.sets, tLRPC$TL_messages_featuredStickers.unread, false, (int) (System.currentTimeMillis() / 1000), tLRPC$TL_messages_featuredStickers.hash);
            return;
        }
        processLoadedFeaturedStickers((ArrayList<TLRPC$StickerSetCovered>) null, (ArrayList<Long>) null, false, (int) (System.currentTimeMillis() / 1000), tLRPC$TL_messages_getFeaturedStickers.hash);
    }

    private void processLoadedFeaturedStickers(ArrayList<TLRPC$StickerSetCovered> arrayList, ArrayList<Long> arrayList2, boolean z, int i, long j) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda2(this));
        Utilities.stageQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda109(this, z, arrayList, i, j, arrayList2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$33() {
        this.loadingFeaturedStickers = false;
        this.featuredStickersLoaded = true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$37(boolean z, ArrayList arrayList, int i, long j, ArrayList arrayList2) {
        ArrayList arrayList3 = arrayList;
        int i2 = i;
        long j2 = j;
        long j3 = 0;
        if ((z && (arrayList3 == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i2)) >= 3600)) || (!z && arrayList3 == null && j2 == 0)) {
            MediaDataController$$ExternalSyntheticLambda66 mediaDataController$$ExternalSyntheticLambda66 = new MediaDataController$$ExternalSyntheticLambda66(this, arrayList3, j2);
            if (arrayList3 == null && !z) {
                j3 = 1000;
            }
            AndroidUtilities.runOnUIThread(mediaDataController$$ExternalSyntheticLambda66, j3);
            if (arrayList3 == null) {
                return;
            }
        }
        if (arrayList3 != null) {
            try {
                ArrayList arrayList4 = new ArrayList();
                LongSparseArray longSparseArray = new LongSparseArray();
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) arrayList3.get(i3);
                    arrayList4.add(tLRPC$StickerSetCovered);
                    longSparseArray.put(tLRPC$StickerSetCovered.set.id, tLRPC$StickerSetCovered);
                }
                if (!z) {
                    putFeaturedStickersToCache(arrayList4, arrayList2, i, j);
                }
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda70(this, arrayList2, longSparseArray, arrayList4, j, i));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda17(this, i2));
            putFeaturedStickersToCache((ArrayList<TLRPC$StickerSetCovered>) null, (ArrayList<Long>) null, i, 0);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$34(ArrayList arrayList, long j) {
        if (!(arrayList == null || j == 0)) {
            this.loadFeaturedHash = j;
        }
        loadFeaturedStickers(false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$35(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, long j, int i) {
        this.unreadStickerSets = arrayList;
        this.featuredStickerSetsById = longSparseArray;
        this.featuredStickerSets = arrayList2;
        this.loadFeaturedHash = j;
        this.loadFeaturedDate = i;
        loadStickers(3, true, false);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedFeaturedStickers$36(int i) {
        this.loadFeaturedDate = i;
    }

    private void putFeaturedStickersToCache(ArrayList<TLRPC$StickerSetCovered> arrayList, ArrayList<Long> arrayList2, int i, long j) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda71(this, arrayList != null ? new ArrayList(arrayList) : null, arrayList2, i, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putFeaturedStickersToCache$38(ArrayList arrayList, ArrayList arrayList2, int i, long j) {
        if (arrayList != null) {
            try {
                SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
                executeFast.requery();
                int i2 = 4;
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    i2 += ((TLRPC$StickerSetCovered) arrayList.get(i3)).getObjectSize();
                }
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(i2);
                NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer((arrayList2.size() * 8) + 4);
                nativeByteBuffer.writeInt32(arrayList.size());
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    ((TLRPC$StickerSetCovered) arrayList.get(i4)).serializeToStream(nativeByteBuffer);
                }
                nativeByteBuffer2.writeInt32(arrayList2.size());
                for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                    nativeByteBuffer2.writeInt64(((Long) arrayList2.get(i5)).longValue());
                }
                executeFast.bindInteger(1, 1);
                executeFast.bindByteBuffer(2, nativeByteBuffer);
                executeFast.bindByteBuffer(3, nativeByteBuffer2);
                executeFast.bindInteger(4, i);
                executeFast.bindLong(5, j);
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

    private long calcFeaturedStickersHash(ArrayList<TLRPC$StickerSetCovered> arrayList) {
        long j = 0;
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$StickerSet tLRPC$StickerSet = arrayList.get(i).set;
                if (!tLRPC$StickerSet.archived) {
                    j = calcHash(j, tLRPC$StickerSet.id);
                    if (this.unreadStickerSets.contains(Long.valueOf(tLRPC$StickerSet.id))) {
                        j = calcHash(j, 1);
                    }
                }
            }
        }
        return j;
    }

    public void markFaturedStickersAsRead(boolean z) {
        if (!this.unreadStickerSets.isEmpty()) {
            this.unreadStickerSets.clear();
            this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
            getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
            putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
            if (z) {
                getConnectionsManager().sendRequest(new TLRPC$TL_messages_readFeaturedStickers(), MediaDataController$$ExternalSyntheticLambda165.INSTANCE);
            }
        }
    }

    public long getFeaturesStickersHashWithoutUnread() {
        long j = 0;
        for (int i = 0; i < this.featuredStickerSets.size(); i++) {
            TLRPC$StickerSet tLRPC$StickerSet = this.featuredStickerSets.get(i).set;
            if (!tLRPC$StickerSet.archived) {
                j = calcHash(j, tLRPC$StickerSet.id);
            }
        }
        return j;
    }

    public void markFaturedStickersByIdAsRead(long j) {
        if (this.unreadStickerSets.contains(Long.valueOf(j)) && !this.readingStickerSets.contains(Long.valueOf(j))) {
            this.readingStickerSets.add(Long.valueOf(j));
            TLRPC$TL_messages_readFeaturedStickers tLRPC$TL_messages_readFeaturedStickers = new TLRPC$TL_messages_readFeaturedStickers();
            tLRPC$TL_messages_readFeaturedStickers.id.add(Long.valueOf(j));
            getConnectionsManager().sendRequest(tLRPC$TL_messages_readFeaturedStickers, MediaDataController$$ExternalSyntheticLambda161.INSTANCE);
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda28(this, j), 1000);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$markFaturedStickersByIdAsRead$41(long j) {
        this.unreadStickerSets.remove(Long.valueOf(j));
        this.readingStickerSets.remove(Long.valueOf(j));
        this.loadFeaturedHash = calcFeaturedStickersHash(this.featuredStickerSets);
        getNotificationCenter().postNotificationName(NotificationCenter.featuredStickersDidLoad, new Object[0]);
        putFeaturedStickersToCache(this.featuredStickerSets, this.unreadStickerSets, this.loadFeaturedDate, this.loadFeaturedHash);
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
                    AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda86(this, tLRPC$Message, stickerSetName));
                } else {
                    lambda$verifyAnimatedStickerMessage$42(tLRPC$Message, stickerSetName);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: verifyAnimatedStickerMessageInternal */
    public void lambda$verifyAnimatedStickerMessage$42(TLRPC$Message tLRPC$Message, String str) {
        ArrayList arrayList = this.verifyingMessages.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.verifyingMessages.put(str, arrayList);
        }
        arrayList.add(tLRPC$Message);
        TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
        tLRPC$TL_messages_getStickerSet.stickerset = MessageObject.getInputStickerSet(tLRPC$Message);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new MediaDataController$$ExternalSyntheticLambda151(this, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$verifyAnimatedStickerMessageInternal$44(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda57(this, str, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$verifyAnimatedStickerMessageInternal$43(String str, TLObject tLObject) {
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
        if (i != 1) {
            z2 = false;
        }
        tLRPC$TL_messages_getArchivedStickers.masks = z2;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getArchivedStickers, new MediaDataController$$ExternalSyntheticLambda134(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadArchivedStickersCount$46(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda94(this, tLRPC$TL_error, tLObject, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadArchivedStickersCount$45(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_archivedStickers tLRPC$TL_messages_archivedStickers = (TLRPC$TL_messages_archivedStickers) tLObject;
            this.archivedStickersCount[i] = tLRPC$TL_messages_archivedStickers.count;
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putInt("archivedStickersCount" + i, tLRPC$TL_messages_archivedStickers.count).commit();
            getNotificationCenter().postNotificationName(NotificationCenter.archivedStickersCountDidLoad, Integer.valueOf(i));
        }
    }

    private void processLoadStickersResponse(int i, TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers) {
        TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers2 = tLRPC$TL_messages_allStickers;
        ArrayList arrayList = new ArrayList();
        long j = 1000;
        if (tLRPC$TL_messages_allStickers2.sets.isEmpty()) {
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), tLRPC$TL_messages_allStickers2.hash);
        } else {
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
                MediaDataController$$ExternalSyntheticLambda154 mediaDataController$$ExternalSyntheticLambda154 = r0;
                ConnectionsManager connectionsManager = getConnectionsManager();
                MediaDataController$$ExternalSyntheticLambda154 mediaDataController$$ExternalSyntheticLambda1542 = new MediaDataController$$ExternalSyntheticLambda154(this, arrayList, i2, longSparseArray, tLRPC$StickerSet, tLRPC$TL_messages_allStickers, i);
                connectionsManager.sendRequest(tLRPC$TL_messages_getStickerSet, mediaDataController$$ExternalSyntheticLambda154);
                i2++;
                j = 1000;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadStickersResponse$48(ArrayList arrayList, int i, LongSparseArray longSparseArray, TLRPC$StickerSet tLRPC$StickerSet, TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda78(this, tLObject, arrayList, i, longSparseArray, tLRPC$StickerSet, tLRPC$TL_messages_allStickers, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadStickersResponse$47(TLObject tLObject, ArrayList arrayList, int i, LongSparseArray longSparseArray, TLRPC$StickerSet tLRPC$StickerSet, TLRPC$TL_messages_allStickers tLRPC$TL_messages_allStickers, int i2) {
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

    public void loadStickersByEmojiOrName(String str, boolean z, boolean z2) {
        if (this.loadingDiceStickerSets.contains(str)) {
            return;
        }
        if (!z || this.diceStickerSetsByEmoji.get(str) == null) {
            this.loadingDiceStickerSets.add(str);
            if (z2) {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda60(this, str, z));
                return;
            }
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            if (z) {
                TLRPC$TL_inputStickerSetDice tLRPC$TL_inputStickerSetDice = new TLRPC$TL_inputStickerSetDice();
                tLRPC$TL_inputStickerSetDice.emoticon = str;
                tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetDice;
            } else {
                TLRPC$TL_inputStickerSetShortName tLRPC$TL_inputStickerSetShortName = new TLRPC$TL_inputStickerSetShortName();
                tLRPC$TL_inputStickerSetShortName.short_name = str;
                tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetShortName;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new MediaDataController$$ExternalSyntheticLambda153(this, str, z));
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
    public /* synthetic */ void lambda$loadStickersByEmojiOrName$49(java.lang.String r12, boolean r13) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadStickersByEmojiOrName$49(java.lang.String, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickersByEmojiOrName$51(String str, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda95(this, tLRPC$TL_error, tLObject, str, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickersByEmojiOrName$50(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str, boolean z) {
        if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
            processLoadedDiceStickers(str, z, (TLRPC$TL_messages_stickerSet) tLObject, false, (int) (System.currentTimeMillis() / 1000));
            return;
        }
        processLoadedDiceStickers(str, z, (TLRPC$TL_messages_stickerSet) null, false, (int) (System.currentTimeMillis() / 1000));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedDiceStickers$52(String str) {
        this.loadingDiceStickerSets.remove(str);
    }

    private void processLoadedDiceStickers(String str, boolean z, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z2, int i) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda50(this, str));
        Utilities.stageQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda111(this, z2, tLRPC$TL_messages_stickerSet, i, str, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedDiceStickers$55(boolean z, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, int i, String str, boolean z2) {
        long j = 1000;
        if ((z && (tLRPC$TL_messages_stickerSet == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i)) >= 86400)) || (!z && tLRPC$TL_messages_stickerSet == null)) {
            MediaDataController$$ExternalSyntheticLambda59 mediaDataController$$ExternalSyntheticLambda59 = new MediaDataController$$ExternalSyntheticLambda59(this, str, z2);
            if (tLRPC$TL_messages_stickerSet != null || z) {
                j = 0;
            }
            AndroidUtilities.runOnUIThread(mediaDataController$$ExternalSyntheticLambda59, j);
            if (tLRPC$TL_messages_stickerSet == null) {
                return;
            }
        }
        if (tLRPC$TL_messages_stickerSet != null) {
            if (!z) {
                putDiceStickersToCache(str, tLRPC$TL_messages_stickerSet, i);
            }
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda58(this, str, tLRPC$TL_messages_stickerSet));
        } else if (!z) {
            putDiceStickersToCache(str, (TLRPC$TL_messages_stickerSet) null, i);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedDiceStickers$53(String str, boolean z) {
        loadStickersByEmojiOrName(str, z, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedDiceStickers$54(String str, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.diceStickerSetsByEmoji.put(str, tLRPC$TL_messages_stickerSet);
        this.diceEmojiStickerSetsById.put(tLRPC$TL_messages_stickerSet.set.id, str);
        getNotificationCenter().postNotificationName(NotificationCenter.diceStickersDidLoad, str);
    }

    private void putDiceStickersToCache(String str, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, int i) {
        if (!TextUtils.isEmpty(str)) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda100(this, tLRPC$TL_messages_stickerSet, str, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putDiceStickersToCache$56(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, String str, int i) {
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

    public void loadStickers(int i, boolean z, boolean z2) {
        loadStickers(i, z, z2, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getAllStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadStickers(int r4, boolean r5, boolean r6, boolean r7) {
        /*
            r3 = this;
            boolean[] r0 = r3.loadingStickers
            boolean r0 = r0[r4]
            if (r0 == 0) goto L_0x0012
            if (r7 == 0) goto L_0x0011
            java.lang.Runnable[] r5 = r3.scheduledLoadStickers
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda24 r7 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda24
            r7.<init>(r3, r4, r6)
            r5[r4] = r7
        L_0x0011:
            return
        L_0x0012:
            r7 = 4
            r0 = 3
            if (r4 != r0) goto L_0x0027
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r3.featuredStickerSets
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0026
            org.telegram.messenger.MessagesController r1 = r3.getMessagesController()
            boolean r1 = r1.preloadFeaturedStickers
            if (r1 != 0) goto L_0x002c
        L_0x0026:
            return
        L_0x0027:
            if (r4 == r7) goto L_0x002c
            r3.loadArchivedStickersCount(r4, r5)
        L_0x002c:
            boolean[] r1 = r3.loadingStickers
            r2 = 1
            r1[r4] = r2
            if (r5 == 0) goto L_0x0045
            org.telegram.messenger.MessagesStorage r5 = r3.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r5 = r5.getStorageQueue()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda16 r6 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda16
            r6.<init>(r3, r4)
            r5.postRunnable(r6)
            goto L_0x00b7
        L_0x0045:
            if (r4 != r0) goto L_0x006f
            org.telegram.tgnet.TLRPC$TL_messages_allStickers r5 = new org.telegram.tgnet.TLRPC$TL_messages_allStickers
            r5.<init>()
            long r6 = r3.loadFeaturedHash
            r5.hash = r6
            r6 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r7 = r3.featuredStickerSets
            int r7 = r7.size()
        L_0x0057:
            if (r6 >= r7) goto L_0x006b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSet> r0 = r5.sets
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r3.featuredStickerSets
            java.lang.Object r1 = r1.get(r6)
            org.telegram.tgnet.TLRPC$StickerSetCovered r1 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r1
            org.telegram.tgnet.TLRPC$StickerSet r1 = r1.set
            r0.add(r1)
            int r6 = r6 + 1
            goto L_0x0057
        L_0x006b:
            r3.processLoadStickersResponse(r4, r5)
            goto L_0x00b7
        L_0x006f:
            if (r4 != r7) goto L_0x008a
            org.telegram.tgnet.TLRPC$TL_messages_getStickerSet r5 = new org.telegram.tgnet.TLRPC$TL_messages_getStickerSet
            r5.<init>()
            org.telegram.tgnet.TLRPC$TL_inputStickerSetAnimatedEmoji r6 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetAnimatedEmoji
            r6.<init>()
            r5.stickerset = r6
            org.telegram.tgnet.ConnectionsManager r6 = r3.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda131 r7 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda131
            r7.<init>(r3, r4)
            r6.sendRequest(r5, r7)
            goto L_0x00b7
        L_0x008a:
            r0 = 0
            if (r4 != 0) goto L_0x009d
            org.telegram.tgnet.TLRPC$TL_messages_getAllStickers r5 = new org.telegram.tgnet.TLRPC$TL_messages_getAllStickers
            r5.<init>()
            if (r6 == 0) goto L_0x0096
            goto L_0x009a
        L_0x0096:
            long[] r6 = r3.loadHash
            r0 = r6[r4]
        L_0x009a:
            r5.hash = r0
            goto L_0x00ab
        L_0x009d:
            org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers r5 = new org.telegram.tgnet.TLRPC$TL_messages_getMaskStickers
            r5.<init>()
            if (r6 == 0) goto L_0x00a5
            goto L_0x00a9
        L_0x00a5:
            long[] r6 = r3.loadHash
            r0 = r6[r4]
        L_0x00a9:
            r5.hash = r0
        L_0x00ab:
            org.telegram.tgnet.ConnectionsManager r6 = r3.getConnectionsManager()
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda135 r7 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda135
            r7.<init>(r3, r4, r0)
            r6.sendRequest(r5, r7)
        L_0x00b7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadStickers(int, boolean, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickers$57(int i, boolean z) {
        loadStickers(i, false, z, false);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x006f A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadStickers$58(int r15) {
        /*
            r14 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            org.telegram.messenger.MessagesStorage r4 = r14.getMessagesStorage()     // Catch:{ all -> 0x0066 }
            org.telegram.SQLite.SQLiteDatabase r4 = r4.getDatabase()     // Catch:{ all -> 0x0066 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0066 }
            r5.<init>()     // Catch:{ all -> 0x0066 }
            java.lang.String r6 = "SELECT data, date, hash FROM stickers_v2 WHERE id = "
            r5.append(r6)     // Catch:{ all -> 0x0066 }
            int r6 = r15 + 1
            r5.append(r6)     // Catch:{ all -> 0x0066 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0066 }
            java.lang.Object[] r6 = new java.lang.Object[r1]     // Catch:{ all -> 0x0066 }
            org.telegram.SQLite.SQLiteCursor r4 = r4.queryFinalized(r5, r6)     // Catch:{ all -> 0x0066 }
            boolean r5 = r4.next()     // Catch:{ all -> 0x0062 }
            if (r5 == 0) goto L_0x005b
            org.telegram.tgnet.NativeByteBuffer r5 = r4.byteBufferValue(r1)     // Catch:{ all -> 0x0062 }
            if (r5 == 0) goto L_0x0052
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x0062 }
            r6.<init>()     // Catch:{ all -> 0x0062 }
            int r0 = r5.readInt32(r1)     // Catch:{ all -> 0x0050 }
            r7 = 0
        L_0x003b:
            if (r7 >= r0) goto L_0x004b
            int r8 = r5.readInt32(r1)     // Catch:{ all -> 0x0050 }
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = org.telegram.tgnet.TLRPC$messages_StickerSet.TLdeserialize(r5, r8, r1)     // Catch:{ all -> 0x0050 }
            r6.add(r8)     // Catch:{ all -> 0x0050 }
            int r7 = r7 + 1
            goto L_0x003b
        L_0x004b:
            r5.reuse()     // Catch:{ all -> 0x0050 }
            r0 = r6
            goto L_0x0052
        L_0x0050:
            r0 = move-exception
            goto L_0x006a
        L_0x0052:
            r5 = 1
            int r1 = r4.intValue(r5)     // Catch:{ all -> 0x0062 }
            long r2 = calcStickersHash(r0)     // Catch:{ all -> 0x0062 }
        L_0x005b:
            r4.dispose()
            r9 = r0
            r11 = r1
            r12 = r2
            goto L_0x0075
        L_0x0062:
            r5 = move-exception
            r6 = r0
            r0 = r5
            goto L_0x006a
        L_0x0066:
            r4 = move-exception
            r6 = r0
            r0 = r4
            r4 = r6
        L_0x006a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x007c }
            if (r4 == 0) goto L_0x0072
            r4.dispose()
        L_0x0072:
            r11 = r1
            r12 = r2
            r9 = r6
        L_0x0075:
            r10 = 1
            r7 = r14
            r8 = r15
            r7.processLoadedStickers(r8, r9, r10, r11, r12)
            return
        L_0x007c:
            r15 = move-exception
            if (r4 == 0) goto L_0x0082
            r4.dispose()
        L_0x0082:
            goto L_0x0084
        L_0x0083:
            throw r15
        L_0x0084:
            goto L_0x0083
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadStickers$58(int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickers$59(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TLRPC$TL_messages_stickerSet) {
            ArrayList arrayList = new ArrayList();
            arrayList.add((TLRPC$TL_messages_stickerSet) tLObject2);
            processLoadedStickers(i, arrayList, false, (int) (System.currentTimeMillis() / 1000), calcStickersHash(arrayList));
            return;
        }
        processLoadedStickers(i, (ArrayList<TLRPC$TL_messages_stickerSet>) null, false, (int) (System.currentTimeMillis() / 1000), 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickers$61(int i, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda77(this, tLObject, i, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadStickers$60(TLObject tLObject, int i, long j) {
        if (tLObject instanceof TLRPC$TL_messages_allStickers) {
            processLoadStickersResponse(i, (TLRPC$TL_messages_allStickers) tLObject);
            return;
        }
        processLoadedStickers(i, (ArrayList<TLRPC$TL_messages_stickerSet>) null, false, (int) (System.currentTimeMillis() / 1000), j);
    }

    private void putStickersToCache(int i, ArrayList<TLRPC$TL_messages_stickerSet> arrayList, int i2, long j) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda64(this, arrayList != null ? new ArrayList(arrayList) : null, i, i2, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putStickersToCache$62(ArrayList arrayList, int i, int i2, long j) {
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
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered = this.featuredStickerSetsById.get(j);
        if (tLRPC$StickerSetCovered != null) {
            return tLRPC$StickerSetCovered.set.short_name;
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
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda15(this, i));
        Utilities.stageQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda108(this, z, arrayList, i2, j, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedStickers$63(int i) {
        this.loadingStickers[i] = false;
        this.stickersLoaded[i] = true;
        Runnable[] runnableArr = this.scheduledLoadStickers;
        if (runnableArr[i] != null) {
            runnableArr[i].run();
            this.scheduledLoadStickers[i] = null;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedStickers$67(boolean z, ArrayList arrayList, int i, long j, int i2) {
        int i3;
        int i4;
        MediaDataController mediaDataController = this;
        ArrayList arrayList2 = arrayList;
        int i5 = i;
        long j2 = 0;
        if ((z && (arrayList2 == null || Math.abs((System.currentTimeMillis() / 1000) - ((long) i5)) >= 3600)) || (!z && arrayList2 == null && j == 0)) {
            MediaDataController$$ExternalSyntheticLambda68 mediaDataController$$ExternalSyntheticLambda68 = new MediaDataController$$ExternalSyntheticLambda68(this, arrayList, j, i2);
            if (arrayList2 == null && !z) {
                j2 = 1000;
            }
            AndroidUtilities.runOnUIThread(mediaDataController$$ExternalSyntheticLambda68, j2);
            if (arrayList2 == null) {
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
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda20(this, i2, longSparseArray, hashMap, arrayList3, j, i, longSparseArray3, hashMap4, longSparseArray2));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else if (!z) {
            int i10 = i2;
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda18(this, i10, i5));
            putStickersToCache(i10, (ArrayList<TLRPC$TL_messages_stickerSet>) null, i, 0);
            return;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedStickers$64(ArrayList arrayList, long j, int i) {
        if (!(arrayList == null || j == 0)) {
            this.loadHash[i] = j;
        }
        loadStickers(i, false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedStickers$65(int i, LongSparseArray longSparseArray, HashMap hashMap, ArrayList arrayList, long j, int i2, LongSparseArray longSparseArray2, HashMap hashMap2, LongSparseArray longSparseArray3) {
        int i3 = i;
        LongSparseArray longSparseArray4 = longSparseArray;
        HashMap hashMap3 = hashMap2;
        for (int i4 = 0; i4 < this.stickerSets[i3].size(); i4++) {
            TLRPC$StickerSet tLRPC$StickerSet = this.stickerSets[i3].get(i4).set;
            this.stickerSetsById.remove(tLRPC$StickerSet.id);
            this.stickerSetsByName.remove(tLRPC$StickerSet.short_name);
            if (!(i3 == 3 || i3 == 4)) {
                this.installedStickerSetsById.remove(tLRPC$StickerSet.id);
            }
        }
        for (int i5 = 0; i5 < longSparseArray.size(); i5++) {
            this.stickerSetsById.put(longSparseArray.keyAt(i5), (TLRPC$TL_messages_stickerSet) longSparseArray.valueAt(i5));
            if (!(i3 == 3 || i3 == 4)) {
                this.installedStickerSetsById.put(longSparseArray.keyAt(i5), (TLRPC$TL_messages_stickerSet) longSparseArray.valueAt(i5));
            }
        }
        HashMap hashMap4 = hashMap;
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
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedStickers$66(int i, int i2) {
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
            getFileLoader().loadFile(forSticker, obj, forSticker.imageType == 1 ? "tgs" : "webp", 2, 1);
        }
    }

    public void toggleStickerSet(Context context, TLObject tLObject, int i, BaseFragment baseFragment, boolean z, boolean z2) {
        TLRPC$StickerSet tLRPC$StickerSet;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        int i2;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2;
        Context context2 = context;
        TLObject tLObject2 = tLObject;
        int i3 = i;
        BaseFragment baseFragment2 = baseFragment;
        if (tLObject2 instanceof TLRPC$TL_messages_stickerSet) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet3 = (TLRPC$TL_messages_stickerSet) tLObject2;
            tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet3;
            tLRPC$StickerSet = tLRPC$TL_messages_stickerSet3.set;
        } else if (tLObject2 instanceof TLRPC$StickerSetCovered) {
            TLRPC$StickerSet tLRPC$StickerSet2 = ((TLRPC$StickerSetCovered) tLObject2).set;
            if (i3 != 2) {
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
        int i4 = tLRPC$StickerSet.masks;
        tLRPC$StickerSet.archived = i3 == 1;
        int i5 = 0;
        while (true) {
            if (i5 >= this.stickerSets[i4].size()) {
                i2 = 0;
                break;
            }
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet4 = this.stickerSets[i4].get(i5);
            if (tLRPC$TL_messages_stickerSet4.set.id == tLRPC$StickerSet.id) {
                this.stickerSets[i4].remove(i5);
                if (i3 == 2) {
                    this.stickerSets[i4].add(0, tLRPC$TL_messages_stickerSet4);
                } else {
                    this.stickerSetsById.remove(tLRPC$TL_messages_stickerSet4.set.id);
                    this.installedStickerSetsById.remove(tLRPC$TL_messages_stickerSet4.set.id);
                    this.stickerSetsByName.remove(tLRPC$TL_messages_stickerSet4.set.short_name);
                }
                i2 = i5;
            } else {
                i5++;
            }
        }
        this.loadHash[i4] = calcStickersHash(this.stickerSets[i4]);
        putStickersToCache(i4, this.stickerSets[i4], this.loadDate[i4], this.loadHash[i4]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i4));
        if (i3 == 2) {
            if (!cancelRemovingStickerSet(tLRPC$StickerSet.id)) {
                toggleStickerSetInternal(context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i4, z2);
            }
        } else if (!z2 || baseFragment2 == null) {
            toggleStickerSetInternal(context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i4, false);
        } else {
            StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(context2, tLObject2, i3);
            Bulletin.UndoButton delayedAction = new Bulletin.UndoButton(context2, false).setUndoAction(new MediaDataController$$ExternalSyntheticLambda89(this, tLRPC$StickerSet, i4, i2, tLRPC$TL_messages_stickerSet)).setDelayedAction(new MediaDataController$$ExternalSyntheticLambda48(this, context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i4));
            stickerSetBulletinLayout.setButton(delayedAction);
            LongSparseArray<Runnable> longSparseArray = this.removingStickerSetsUndos;
            long j = tLRPC$StickerSet.id;
            delayedAction.getClass();
            longSparseArray.put(j, new MediaDataController$$ExternalSyntheticLambda114(delayedAction));
            Bulletin.make(baseFragment2, (Bulletin.Layout) stickerSetBulletinLayout, 2750).show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSet$68(TLRPC$StickerSet tLRPC$StickerSet, int i, int i2, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        tLRPC$StickerSet.archived = false;
        this.stickerSets[i].add(i2, tLRPC$TL_messages_stickerSet);
        this.stickerSetsById.put(tLRPC$StickerSet.id, tLRPC$TL_messages_stickerSet);
        this.installedStickerSetsById.put(tLRPC$StickerSet.id, tLRPC$TL_messages_stickerSet);
        this.stickerSetsByName.put(tLRPC$StickerSet.short_name, tLRPC$TL_messages_stickerSet);
        this.removingStickerSetsUndos.remove(tLRPC$StickerSet.id);
        this.loadHash[i] = calcStickersHash(this.stickerSets[i]);
        putStickersToCache(i, this.stickerSets[i], this.loadDate[i], this.loadHash[i]);
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSet$69(Context context, int i, BaseFragment baseFragment, boolean z, TLObject tLObject, TLRPC$StickerSet tLRPC$StickerSet, int i2) {
        toggleStickerSetInternal(context, i, baseFragment, z, tLObject, tLRPC$StickerSet, i2, false);
    }

    private void toggleStickerSetInternal(Context context, int i, BaseFragment baseFragment, boolean z, TLObject tLObject, TLRPC$StickerSet tLRPC$StickerSet, int i2, boolean z2) {
        int i3 = i;
        TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$StickerSet;
        TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
        tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet2.access_hash;
        tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet2.id;
        if (i3 != 0) {
            TLRPC$TL_messages_installStickerSet tLRPC$TL_messages_installStickerSet = new TLRPC$TL_messages_installStickerSet();
            tLRPC$TL_messages_installStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
            boolean z3 = true;
            if (i3 != 1) {
                z3 = false;
            }
            tLRPC$TL_messages_installStickerSet.archived = z3;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_installStickerSet, new MediaDataController$$ExternalSyntheticLambda157(this, tLRPC$StickerSet, baseFragment, z, i2, z2, context, tLObject));
            return;
        }
        TLRPC$TL_messages_uninstallStickerSet tLRPC$TL_messages_uninstallStickerSet = new TLRPC$TL_messages_uninstallStickerSet();
        tLRPC$TL_messages_uninstallStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_uninstallStickerSet, new MediaDataController$$ExternalSyntheticLambda156(this, tLRPC$StickerSet2, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSetInternal$71(TLRPC$StickerSet tLRPC$StickerSet, BaseFragment baseFragment, boolean z, int i, boolean z2, Context context, TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda90(this, tLRPC$StickerSet, tLObject2, baseFragment, z, i, tLRPC$TL_error, z2, context, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSetInternal$70(TLRPC$StickerSet tLRPC$StickerSet, TLObject tLObject, BaseFragment baseFragment, boolean z, int i, TLRPC$TL_error tLRPC$TL_error, boolean z2, Context context, TLObject tLObject2) {
        this.removingStickerSetsUndos.remove(tLRPC$StickerSet.id);
        if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
            processStickerSetInstallResultArchive(baseFragment, z, i, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
        }
        loadStickers(i, false, false, true);
        if (tLRPC$TL_error == null && z2 && baseFragment != null) {
            Bulletin.make(baseFragment, (Bulletin.Layout) new StickerSetBulletinLayout(context, tLObject2, 2), 1500).show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSetInternal$73(TLRPC$StickerSet tLRPC$StickerSet, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda88(this, tLRPC$StickerSet, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSetInternal$72(TLRPC$StickerSet tLRPC$StickerSet, int i) {
        this.removingStickerSetsUndos.remove(tLRPC$StickerSet.id);
        loadStickers(i, false, true);
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
        getNotificationCenter().postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(i));
        TLRPC$TL_messages_toggleStickerSets tLRPC$TL_messages_toggleStickerSets = new TLRPC$TL_messages_toggleStickerSets();
        tLRPC$TL_messages_toggleStickerSets.stickersets = arrayList2;
        if (i3 == 0) {
            tLRPC$TL_messages_toggleStickerSets.uninstall = true;
        } else if (i3 == 1) {
            tLRPC$TL_messages_toggleStickerSets.archive = true;
        } else if (i3 == 2) {
            tLRPC$TL_messages_toggleStickerSets.unarchive = true;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_toggleStickerSets, new MediaDataController$$ExternalSyntheticLambda138(this, i2, baseFragment, z, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSets$75(int i, BaseFragment baseFragment, boolean z, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda22(this, i, tLObject, baseFragment, z, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleStickerSets$74(int i, TLObject tLObject, BaseFragment baseFragment, boolean z, int i2) {
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
                MediaDataController$$ExternalSyntheticLambda148 mediaDataController$$ExternalSyntheticLambda148 = r0;
                MediaDataController$$ExternalSyntheticLambda148 mediaDataController$$ExternalSyntheticLambda1482 = new MediaDataController$$ExternalSyntheticLambda148(this, j2, tLRPC$TL_messages_search, j, i, i2, i3, tLRPC$User, tLRPC$Chat, z2);
                this.mergeReqId = connectionsManager.sendRequest(tLRPC$TL_messages_search, mediaDataController$$ExternalSyntheticLambda148, 2);
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
            MediaDataController$$ExternalSyntheticLambda152 mediaDataController$$ExternalSyntheticLambda152 = r0;
            MediaDataController$$ExternalSyntheticLambda152 mediaDataController$$ExternalSyntheticLambda1522 = new MediaDataController$$ExternalSyntheticLambda152(this, str3, i12, z2, tLRPC$TL_messages_search2, j3, j, i, j2, i3, tLRPC$User, tLRPC$Chat);
            this.reqId = getConnectionsManager().sendRequest(tLRPC$TL_messages_search2, mediaDataController$$ExternalSyntheticLambda152, 2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchMessagesInChat$77(long j, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j2, int i, int i2, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda42(this, j, tLObject, tLRPC$TL_messages_search, j2, i, i2, i3, tLRPC$User, tLRPC$Chat, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchMessagesInChat$76(long j, TLObject tLObject, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j2, int i, int i2, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean z) {
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
    public /* synthetic */ void lambda$searchMessagesInChat$79(String str, int i, boolean z, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j, long j2, int i2, long j3, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda25(this, i, z, tLObject, tLRPC$TL_messages_search, j, j2, i2, arrayList, j3, i3, tLRPC$User, tLRPC$Chat));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchMessagesInChat$78(int i, boolean z, TLObject tLObject, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j, long j2, int i2, ArrayList arrayList, long j3, int i3, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat) {
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
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda142 r15 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda142
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
    public /* synthetic */ void lambda$loadMedia$80(long j, int i, int i2, int i3, int i4, int i5, boolean z, int i6, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda32(this, j, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCounts$85(long j, int i) {
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
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda46(this, j2, iArr));
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
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getSearchCounters, new MediaDataController$$ExternalSyntheticLambda160(this, iArr, j2)), i);
            }
            if (!z) {
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda47(this, j2, iArr2));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCounts$81(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCounts$83(int[] iArr, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda45(this, j, iArr));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCounts$82(long j, int[] iArr) {
        getNotificationCenter().postNotificationName(NotificationCenter.mediaCountsDidLoad, Long.valueOf(j), iArr);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCounts$84(long j, int[] iArr) {
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
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_getSearchCounters, new MediaDataController$$ExternalSyntheticLambda141(this, j, i, i2)), i2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCount$86(long j, int i, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
            return 0;
        }
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
            TLRPC$Document tLRPC$Document = tLRPC$MessageMedia.document;
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
        int i;
        boolean z = tLRPC$Message instanceof TLRPC$TL_message_secret;
        if (z && (((tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) || MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isGifMessage(tLRPC$Message)) && (i = tLRPC$Message.media.ttl_seconds) != 0 && i <= 60)) {
            return false;
        }
        if (!z && (tLRPC$Message instanceof TLRPC$TL_message)) {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && tLRPC$MessageMedia.ttl_seconds != 0) {
                return false;
            }
        }
        if (getMediaType(tLRPC$Message) != -1) {
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
            MediaDataController$$ExternalSyntheticLambda102 mediaDataController$$ExternalSyntheticLambda102 = r0;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            MediaDataController$$ExternalSyntheticLambda102 mediaDataController$$ExternalSyntheticLambda1022 = new MediaDataController$$ExternalSyntheticLambda102(this, tLRPC$messages_Messages, i5, j, i6, i4, z2, i3, i7);
            dispatchQueue.postRunnable(mediaDataController$$ExternalSyntheticLambda102);
        } else if (i9 != 2) {
            loadMedia(j, i, i2, i3, i4, 0, i6, i7);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedMedia$88(TLRPC$messages_Messages tLRPC$messages_Messages, int i, long j, int i2, int i3, boolean z, int i4, int i5) {
        TLRPC$messages_Messages tLRPC$messages_Messages2 = tLRPC$messages_Messages;
        LongSparseArray longSparseArray = new LongSparseArray();
        for (int i6 = 0; i6 < tLRPC$messages_Messages2.users.size(); i6++) {
            TLRPC$User tLRPC$User = tLRPC$messages_Messages2.users.get(i6);
            longSparseArray.put(tLRPC$User.id, tLRPC$User);
        }
        ArrayList arrayList = new ArrayList();
        for (int i7 = 0; i7 < tLRPC$messages_Messages2.messages.size(); i7++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$messages_Messages2.messages.get(i7), (LongSparseArray<TLRPC$User>) longSparseArray, true, true);
            messageObject.createStrippedThumb();
            arrayList.add(messageObject);
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda103(this, tLRPC$messages_Messages, i, j, arrayList, i2, i3, z, i4, i5));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedMedia$87(TLRPC$messages_Messages tLRPC$messages_Messages, int i, long j, ArrayList arrayList, int i2, int i3, boolean z, int i4, int i5) {
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
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda44(this, j, z, i, i2, i4, i3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedMediaCount$89(long j, boolean z, int i, int i2, int i3, int i4) {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda35(this, j, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putMediaCountDatabase$90(long j, int i, int i2) {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda34(this, j, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getMediaCountDatabase$91(long j, int i, int i2) {
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
            /* JADX WARNING: Removed duplicated region for block: B:61:0x034d A[Catch:{ Exception -> 0x040e, all -> 0x040b }] */
            /* JADX WARNING: Removed duplicated region for block: B:72:0x039b A[Catch:{ Exception -> 0x040e, all -> 0x040b }] */
            /* JADX WARNING: Removed duplicated region for block: B:75:0x03a7 A[SYNTHETIC, Splitter:B:75:0x03a7] */
            /* JADX WARNING: Removed duplicated region for block: B:79:0x03bc A[Catch:{ Exception -> 0x040e, all -> 0x040b }] */
            /* JADX WARNING: Removed duplicated region for block: B:84:0x03d9 A[Catch:{ Exception -> 0x040e, all -> 0x040b }] */
            /* JADX WARNING: Removed duplicated region for block: B:85:0x03e5 A[Catch:{ Exception -> 0x040e, all -> 0x040b }] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r16 = this;
                    r1 = r16
                    org.telegram.tgnet.TLRPC$TL_messages_messages r3 = new org.telegram.tgnet.TLRPC$TL_messages_messages
                    r3.<init>()
                    java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x040e }
                    r2.<init>()     // Catch:{ Exception -> 0x040e }
                    java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x040e }
                    r4.<init>()     // Catch:{ Exception -> 0x040e }
                    int r5 = r2     // Catch:{ Exception -> 0x040e }
                    r6 = 1
                    int r5 = r5 + r6
                    org.telegram.messenger.MediaDataController r7 = org.telegram.messenger.MediaDataController.this     // Catch:{ Exception -> 0x040e }
                    org.telegram.messenger.MessagesStorage r7 = r7.getMessagesStorage()     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteDatabase r7 = r7.getDatabase()     // Catch:{ Exception -> 0x040e }
                    long r8 = r3     // Catch:{ Exception -> 0x040e }
                    boolean r8 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)     // Catch:{ Exception -> 0x040e }
                    r11 = 2
                    r12 = 0
                    if (r8 != 0) goto L_0x02a4
                    int r8 = r5     // Catch:{ Exception -> 0x040e }
                    if (r8 != 0) goto L_0x00ae
                    java.util.Locale r8 = java.util.Locale.US     // Catch:{ Exception -> 0x040e }
                    java.lang.String r14 = "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)"
                    java.lang.Object[] r15 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x040e }
                    long r9 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r15[r12] = r9     // Catch:{ Exception -> 0x040e }
                    int r9 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r15[r6] = r9     // Catch:{ Exception -> 0x040e }
                    java.lang.String r9 = java.lang.String.format(r8, r14, r15)     // Catch:{ Exception -> 0x040e }
                    java.lang.Object[] r10 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r9 = r7.queryFinalized(r9, r10)     // Catch:{ Exception -> 0x040e }
                    boolean r10 = r9.next()     // Catch:{ Exception -> 0x040e }
                    if (r10 == 0) goto L_0x005b
                    int r8 = r9.intValue(r12)     // Catch:{ Exception -> 0x040e }
                    if (r8 != r6) goto L_0x00a9
                    r8 = 1
                    goto L_0x00aa
                L_0x005b:
                    r9.dispose()     // Catch:{ Exception -> 0x040e }
                    java.lang.String r9 = "SELECT min(mid) FROM media_v4 WHERE uid = %d AND type = %d AND mid > 0"
                    java.lang.Object[] r10 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x040e }
                    long r14 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r14 = java.lang.Long.valueOf(r14)     // Catch:{ Exception -> 0x040e }
                    r10[r12] = r14     // Catch:{ Exception -> 0x040e }
                    int r14 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x040e }
                    r10[r6] = r14     // Catch:{ Exception -> 0x040e }
                    java.lang.String r8 = java.lang.String.format(r8, r9, r10)     // Catch:{ Exception -> 0x040e }
                    java.lang.Object[] r9 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r9 = r7.queryFinalized(r8, r9)     // Catch:{ Exception -> 0x040e }
                    boolean r8 = r9.next()     // Catch:{ Exception -> 0x040e }
                    if (r8 == 0) goto L_0x00a9
                    int r8 = r9.intValue(r12)     // Catch:{ Exception -> 0x040e }
                    if (r8 == 0) goto L_0x00a9
                    java.lang.String r10 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)"
                    org.telegram.SQLite.SQLitePreparedStatement r10 = r7.executeFast(r10)     // Catch:{ Exception -> 0x040e }
                    r10.requery()     // Catch:{ Exception -> 0x040e }
                    long r14 = r3     // Catch:{ Exception -> 0x040e }
                    r10.bindLong(r6, r14)     // Catch:{ Exception -> 0x040e }
                    int r14 = r6     // Catch:{ Exception -> 0x040e }
                    r10.bindInteger(r11, r14)     // Catch:{ Exception -> 0x040e }
                    r14 = 3
                    r10.bindInteger(r14, r12)     // Catch:{ Exception -> 0x040e }
                    r14 = 4
                    r10.bindInteger(r14, r8)     // Catch:{ Exception -> 0x040e }
                    r10.step()     // Catch:{ Exception -> 0x040e }
                    r10.dispose()     // Catch:{ Exception -> 0x040e }
                L_0x00a9:
                    r8 = 0
                L_0x00aa:
                    r9.dispose()     // Catch:{ Exception -> 0x040e }
                    goto L_0x00af
                L_0x00ae:
                    r8 = 0
                L_0x00af:
                    int r9 = r7     // Catch:{ Exception -> 0x040e }
                    if (r9 == 0) goto L_0x015b
                    java.util.Locale r9 = java.util.Locale.US     // Catch:{ Exception -> 0x040e }
                    java.lang.String r14 = "SELECT start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND start <= %d ORDER BY end DESC LIMIT 1"
                    r15 = 3
                    java.lang.Object[] r13 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x040e }
                    long r10 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r10 = java.lang.Long.valueOf(r10)     // Catch:{ Exception -> 0x040e }
                    r13[r12] = r10     // Catch:{ Exception -> 0x040e }
                    int r10 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x040e }
                    r13[r6] = r10     // Catch:{ Exception -> 0x040e }
                    int r10 = r7     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x040e }
                    r11 = 2
                    r13[r11] = r10     // Catch:{ Exception -> 0x040e }
                    java.lang.String r10 = java.lang.String.format(r9, r14, r13)     // Catch:{ Exception -> 0x040e }
                    java.lang.Object[] r11 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r10 = r7.queryFinalized(r10, r11)     // Catch:{ Exception -> 0x040e }
                    boolean r11 = r10.next()     // Catch:{ Exception -> 0x040e }
                    if (r11 == 0) goto L_0x00eb
                    r10.intValue(r12)     // Catch:{ Exception -> 0x040e }
                    int r11 = r10.intValue(r6)     // Catch:{ Exception -> 0x040e }
                    goto L_0x00ec
                L_0x00eb:
                    r11 = 0
                L_0x00ec:
                    r10.dispose()     // Catch:{ Exception -> 0x040e }
                    if (r11 <= r6) goto L_0x012a
                    java.lang.String r8 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d"
                    r10 = 5
                    java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x040e }
                    long r13 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r13 = java.lang.Long.valueOf(r13)     // Catch:{ Exception -> 0x040e }
                    r10[r12] = r13     // Catch:{ Exception -> 0x040e }
                    int r13 = r7     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x040e }
                    r10[r6] = r13     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x040e }
                    r13 = 2
                    r10[r13] = r11     // Catch:{ Exception -> 0x040e }
                    int r11 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x040e }
                    r13 = 3
                    r10[r13] = r11     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040e }
                    r11 = 4
                    r10[r11] = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.String r5 = java.lang.String.format(r9, r8, r10)     // Catch:{ Exception -> 0x040e }
                    java.lang.Object[] r8 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r5 = r7.queryFinalized(r5, r8)     // Catch:{ Exception -> 0x040e }
                    r8 = 0
                    goto L_0x02a2
                L_0x012a:
                    java.lang.String r10 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d"
                    r11 = 4
                    java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x040e }
                    long r13 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r13 = java.lang.Long.valueOf(r13)     // Catch:{ Exception -> 0x040e }
                    r11[r12] = r13     // Catch:{ Exception -> 0x040e }
                    int r13 = r7     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x040e }
                    r11[r6] = r13     // Catch:{ Exception -> 0x040e }
                    int r13 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x040e }
                    r14 = 2
                    r11[r14] = r13     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040e }
                    r13 = 3
                    r11[r13] = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.String r5 = java.lang.String.format(r9, r10, r11)     // Catch:{ Exception -> 0x040e }
                    java.lang.Object[] r9 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r5 = r7.queryFinalized(r5, r9)     // Catch:{ Exception -> 0x040e }
                    goto L_0x02a2
                L_0x015b:
                    int r9 = r5     // Catch:{ Exception -> 0x040e }
                    if (r9 == 0) goto L_0x0211
                    java.util.Locale r9 = java.util.Locale.US     // Catch:{ Exception -> 0x040e }
                    java.lang.String r10 = "SELECT start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end >= %d ORDER BY end ASC LIMIT 1"
                    r11 = 3
                    java.lang.Object[] r13 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x040e }
                    r14 = r7
                    long r6 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x040e }
                    r13[r12] = r6     // Catch:{ Exception -> 0x040e }
                    int r6 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x040e }
                    r7 = 1
                    r13[r7] = r6     // Catch:{ Exception -> 0x040e }
                    int r6 = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x040e }
                    r7 = 2
                    r13[r7] = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.String r6 = java.lang.String.format(r9, r10, r13)     // Catch:{ Exception -> 0x040e }
                    java.lang.Object[] r7 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r6 = r14.queryFinalized(r6, r7)     // Catch:{ Exception -> 0x040e }
                    boolean r7 = r6.next()     // Catch:{ Exception -> 0x040e }
                    if (r7 == 0) goto L_0x019a
                    int r7 = r6.intValue(r12)     // Catch:{ Exception -> 0x040e }
                    r10 = 1
                    r6.intValue(r10)     // Catch:{ Exception -> 0x040e }
                    goto L_0x019b
                L_0x019a:
                    r7 = 0
                L_0x019b:
                    r6.dispose()     // Catch:{ Exception -> 0x040e }
                    r6 = 1
                    if (r7 <= r6) goto L_0x01db
                    java.lang.String r6 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid >= %d AND mid <= %d AND type = %d ORDER BY date ASC, mid ASC LIMIT %d"
                    r10 = 5
                    java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x040e }
                    long r11 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r11 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x040e }
                    r12 = 0
                    r10[r12] = r11     // Catch:{ Exception -> 0x040e }
                    int r11 = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x040e }
                    r12 = 1
                    r10[r12] = r11     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x040e }
                    r12 = 2
                    r10[r12] = r7     // Catch:{ Exception -> 0x040e }
                    int r7 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x040e }
                    r12 = 3
                    r10[r12] = r7     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040e }
                    r7 = 4
                    r10[r7] = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.String r5 = java.lang.String.format(r9, r6, r10)     // Catch:{ Exception -> 0x040e }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040e }
                    goto L_0x020e
                L_0x01db:
                    java.lang.String r6 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND mid >= %d AND type = %d ORDER BY date ASC, mid ASC LIMIT %d"
                    r7 = 4
                    java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x040e }
                    long r11 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r8 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x040e }
                    r11 = 0
                    r7[r11] = r8     // Catch:{ Exception -> 0x040e }
                    int r8 = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x040e }
                    r10 = 1
                    r7[r10] = r8     // Catch:{ Exception -> 0x040e }
                    int r8 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x040e }
                    r10 = 2
                    r7[r10] = r8     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040e }
                    r8 = 3
                    r7[r8] = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.String r5 = java.lang.String.format(r9, r6, r7)     // Catch:{ Exception -> 0x040e }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040e }
                    r8 = 1
                L_0x020e:
                    r12 = 1
                    goto L_0x02a2
                L_0x0211:
                    r14 = r7
                    java.util.Locale r6 = java.util.Locale.US     // Catch:{ Exception -> 0x040e }
                    java.lang.String r7 = "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d"
                    r9 = 2
                    java.lang.Object[] r10 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x040e }
                    long r11 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r9 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x040e }
                    r12 = 0
                    r10[r12] = r9     // Catch:{ Exception -> 0x040e }
                    int r9 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r11 = 1
                    r10[r11] = r9     // Catch:{ Exception -> 0x040e }
                    java.lang.String r7 = java.lang.String.format(r6, r7, r10)     // Catch:{ Exception -> 0x040e }
                    java.lang.Object[] r9 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r7 = r14.queryFinalized(r7, r9)     // Catch:{ Exception -> 0x040e }
                    boolean r9 = r7.next()     // Catch:{ Exception -> 0x040e }
                    if (r9 == 0) goto L_0x0240
                    int r9 = r7.intValue(r12)     // Catch:{ Exception -> 0x040e }
                    goto L_0x0241
                L_0x0240:
                    r9 = 0
                L_0x0241:
                    r7.dispose()     // Catch:{ Exception -> 0x040e }
                    r7 = 1
                    if (r9 <= r7) goto L_0x0278
                    java.lang.String r7 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d"
                    r10 = 4
                    java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x040e }
                    long r11 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r11 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x040e }
                    r12 = 0
                    r10[r12] = r11     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r11 = 1
                    r10[r11] = r9     // Catch:{ Exception -> 0x040e }
                    int r9 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r12 = 2
                    r10[r12] = r9     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040e }
                    r9 = 3
                    r10[r9] = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.String r5 = java.lang.String.format(r6, r7, r10)     // Catch:{ Exception -> 0x040e }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040e }
                    goto L_0x02a1
                L_0x0278:
                    java.lang.String r7 = "SELECT data, mid FROM media_v4 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d"
                    r9 = 3
                    java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x040e }
                    long r11 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r11 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x040e }
                    r12 = 0
                    r9[r12] = r11     // Catch:{ Exception -> 0x040e }
                    int r11 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x040e }
                    r10 = 1
                    r9[r10] = r11     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040e }
                    r10 = 2
                    r9[r10] = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.String r5 = java.lang.String.format(r6, r7, r9)     // Catch:{ Exception -> 0x040e }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040e }
                L_0x02a1:
                    r12 = 0
                L_0x02a2:
                    r7 = r8
                    goto L_0x02df
                L_0x02a4:
                    r14 = r7
                    int r6 = r7     // Catch:{ Exception -> 0x040e }
                    if (r6 == 0) goto L_0x02e1
                    java.util.Locale r6 = java.util.Locale.US     // Catch:{ Exception -> 0x040e }
                    java.lang.String r7 = "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d"
                    r8 = 4
                    java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x040e }
                    long r9 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r10 = 0
                    r8[r10] = r9     // Catch:{ Exception -> 0x040e }
                    int r9 = r7     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r10 = 1
                    r8[r10] = r9     // Catch:{ Exception -> 0x040e }
                    int r9 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r10 = 2
                    r8[r10] = r9     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040e }
                    r9 = 3
                    r8[r9] = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.String r5 = java.lang.String.format(r6, r7, r8)     // Catch:{ Exception -> 0x040e }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040e }
                L_0x02dd:
                    r7 = 1
                    r12 = 0
                L_0x02df:
                    r13 = 0
                    goto L_0x0347
                L_0x02e1:
                    int r6 = r5     // Catch:{ Exception -> 0x040e }
                    if (r6 == 0) goto L_0x031a
                    java.util.Locale r6 = java.util.Locale.US     // Catch:{ Exception -> 0x040e }
                    java.lang.String r7 = "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d AND type = %d ORDER BY m.mid DESC LIMIT %d"
                    r8 = 4
                    java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x040e }
                    long r9 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r10 = 0
                    r8[r10] = r9     // Catch:{ Exception -> 0x040e }
                    int r9 = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r10 = 1
                    r8[r10] = r9     // Catch:{ Exception -> 0x040e }
                    int r9 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r10 = 2
                    r8[r10] = r9     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040e }
                    r9 = 3
                    r8[r9] = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.String r5 = java.lang.String.format(r6, r7, r8)     // Catch:{ Exception -> 0x040e }
                    r6 = 0
                    java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r7)     // Catch:{ Exception -> 0x040e }
                    goto L_0x02dd
                L_0x031a:
                    java.util.Locale r6 = java.util.Locale.US     // Catch:{ Exception -> 0x040e }
                    java.lang.String r7 = "SELECT m.data, m.mid, r.random_id FROM media_v4 as m LEFT JOIN randoms_v2 as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d"
                    r8 = 3
                    java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x040e }
                    long r9 = r3     // Catch:{ Exception -> 0x040e }
                    java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r10 = 0
                    r8[r10] = r9     // Catch:{ Exception -> 0x040e }
                    int r9 = r6     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x040e }
                    r10 = 1
                    r8[r10] = r9     // Catch:{ Exception -> 0x040e }
                    java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x040e }
                    r9 = 2
                    r8[r9] = r5     // Catch:{ Exception -> 0x040e }
                    java.lang.String r5 = java.lang.String.format(r6, r7, r8)     // Catch:{ Exception -> 0x040e }
                    r13 = 0
                    java.lang.Object[] r6 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x040e }
                    org.telegram.SQLite.SQLiteCursor r5 = r14.queryFinalized(r5, r6)     // Catch:{ Exception -> 0x040e }
                    r7 = 1
                    r12 = 0
                L_0x0347:
                    boolean r6 = r5.next()     // Catch:{ Exception -> 0x040e }
                    if (r6 == 0) goto L_0x039b
                    org.telegram.tgnet.NativeByteBuffer r6 = r5.byteBufferValue(r13)     // Catch:{ Exception -> 0x040e }
                    if (r6 == 0) goto L_0x0397
                    int r8 = r6.readInt32(r13)     // Catch:{ Exception -> 0x040e }
                    org.telegram.tgnet.TLRPC$Message r8 = org.telegram.tgnet.TLRPC$Message.TLdeserialize(r6, r8, r13)     // Catch:{ Exception -> 0x040e }
                    org.telegram.messenger.MediaDataController r9 = org.telegram.messenger.MediaDataController.this     // Catch:{ Exception -> 0x040e }
                    org.telegram.messenger.UserConfig r9 = r9.getUserConfig()     // Catch:{ Exception -> 0x040e }
                    long r9 = r9.clientUserId     // Catch:{ Exception -> 0x040e }
                    r8.readAttachPath(r6, r9)     // Catch:{ Exception -> 0x040e }
                    r6.reuse()     // Catch:{ Exception -> 0x040e }
                    r6 = 1
                    int r9 = r5.intValue(r6)     // Catch:{ Exception -> 0x040e }
                    r8.id = r9     // Catch:{ Exception -> 0x040e }
                    long r9 = r3     // Catch:{ Exception -> 0x040e }
                    r8.dialog_id = r9     // Catch:{ Exception -> 0x040e }
                    boolean r6 = org.telegram.messenger.DialogObject.isEncryptedDialog(r9)     // Catch:{ Exception -> 0x040e }
                    if (r6 == 0) goto L_0x0382
                    r6 = 2
                    long r9 = r5.longValue(r6)     // Catch:{ Exception -> 0x040e }
                    r8.random_id = r9     // Catch:{ Exception -> 0x040e }
                    goto L_0x0383
                L_0x0382:
                    r6 = 2
                L_0x0383:
                    if (r12 == 0) goto L_0x038c
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r9 = r3.messages     // Catch:{ Exception -> 0x040e }
                    r10 = 0
                    r9.add(r10, r8)     // Catch:{ Exception -> 0x040e }
                    goto L_0x0392
                L_0x038c:
                    r10 = 0
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r9 = r3.messages     // Catch:{ Exception -> 0x040e }
                    r9.add(r8)     // Catch:{ Exception -> 0x040e }
                L_0x0392:
                    org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r8, r2, r4)     // Catch:{ Exception -> 0x040e }
                    goto L_0x02df
                L_0x0397:
                    r6 = 2
                    r10 = 0
                    goto L_0x02df
                L_0x039b:
                    r10 = 0
                    r5.dispose()     // Catch:{ Exception -> 0x040e }
                    boolean r5 = r2.isEmpty()     // Catch:{ Exception -> 0x040e }
                    java.lang.String r6 = ","
                    if (r5 != 0) goto L_0x03b6
                    org.telegram.messenger.MediaDataController r5 = org.telegram.messenger.MediaDataController.this     // Catch:{ Exception -> 0x040e }
                    org.telegram.messenger.MessagesStorage r5 = r5.getMessagesStorage()     // Catch:{ Exception -> 0x040e }
                    java.lang.String r2 = android.text.TextUtils.join(r6, r2)     // Catch:{ Exception -> 0x040e }
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r8 = r3.users     // Catch:{ Exception -> 0x040e }
                    r5.getUsersInternal(r2, r8)     // Catch:{ Exception -> 0x040e }
                L_0x03b6:
                    boolean r2 = r4.isEmpty()     // Catch:{ Exception -> 0x040e }
                    if (r2 != 0) goto L_0x03cb
                    org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.this     // Catch:{ Exception -> 0x040e }
                    org.telegram.messenger.MessagesStorage r2 = r2.getMessagesStorage()     // Catch:{ Exception -> 0x040e }
                    java.lang.String r4 = android.text.TextUtils.join(r6, r4)     // Catch:{ Exception -> 0x040e }
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r5 = r3.chats     // Catch:{ Exception -> 0x040e }
                    r2.getChatsInternal(r4, r5)     // Catch:{ Exception -> 0x040e }
                L_0x03cb:
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r3.messages     // Catch:{ Exception -> 0x040e }
                    int r2 = r2.size()     // Catch:{ Exception -> 0x040e }
                    int r4 = r2     // Catch:{ Exception -> 0x040e }
                    if (r2 <= r4) goto L_0x03e5
                    int r2 = r5     // Catch:{ Exception -> 0x040e }
                    if (r2 != 0) goto L_0x03e5
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r2 = r3.messages     // Catch:{ Exception -> 0x040e }
                    int r4 = r2.size()     // Catch:{ Exception -> 0x040e }
                    r5 = 1
                    int r4 = r4 - r5
                    r2.remove(r4)     // Catch:{ Exception -> 0x040e }
                    goto L_0x03e9
                L_0x03e5:
                    int r2 = r5     // Catch:{ Exception -> 0x040e }
                    if (r2 == 0) goto L_0x03eb
                L_0x03e9:
                    r13 = 0
                    goto L_0x03ec
                L_0x03eb:
                    r13 = r7
                L_0x03ec:
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
                    goto L_0x0441
                L_0x040b:
                    r0 = move-exception
                    r15 = r0
                    goto L_0x0445
                L_0x040e:
                    r0 = move-exception
                    r2 = r0
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r4 = r3.messages     // Catch:{ all -> 0x040b }
                    r4.clear()     // Catch:{ all -> 0x040b }
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r4 = r3.chats     // Catch:{ all -> 0x040b }
                    r4.clear()     // Catch:{ all -> 0x040b }
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r3.users     // Catch:{ all -> 0x040b }
                    r4.clear()     // Catch:{ all -> 0x040b }
                    org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x040b }
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
                L_0x0441:
                    r2.processLoadedMedia(r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)
                    return
                L_0x0445:
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
                    goto L_0x0469
                L_0x0468:
                    throw r15
                L_0x0469:
                    goto L_0x0468
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda21(this, i3, arrayList, z, j, i2, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putMediaDatabase$92(int i, ArrayList arrayList, boolean z, long j, int i2, int i3) {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda38(this, j, j2, j3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMusic$94(long j, long j2, long j3) {
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
                    AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda41(this, j, arrayList, arrayList2));
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
                                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda41(this, j, arrayList, arrayList2));
                            }
                        } catch (Exception e3) {
                            e = e3;
                            FileLog.e((Throwable) e);
                            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda41(this, j, arrayList, arrayList2));
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
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda41(this, j, arrayList, arrayList2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMusic$93(long j, ArrayList arrayList, ArrayList arrayList2) {
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
            Utilities.globalQueue.postRunnable(new MediaDataController$$ExternalSyntheticLambda62(this, arrayList));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x01b7 A[SYNTHETIC, Splitter:B:57:0x01b7] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x025e A[Catch:{ all -> 0x02c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0275 A[Catch:{ all -> 0x02c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x028e A[Catch:{ all -> 0x02c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0293 A[Catch:{ all -> 0x02c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x029b A[Catch:{ all -> 0x02c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x02b4 A[Catch:{ all -> 0x02c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x02ba A[Catch:{ all -> 0x02c9 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$buildShortcuts$95(java.util.ArrayList r21) {
        /*
            r20 = this;
            r1 = r21
            java.lang.String r0 = "NewConversationShortcut"
            java.lang.String r2 = org.telegram.messenger.SharedConfig.directShareHash     // Catch:{ all -> 0x02c9 }
            r3 = 0
            if (r2 != 0) goto L_0x002a
            java.util.UUID r2 = java.util.UUID.randomUUID()     // Catch:{ all -> 0x02c9 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x02c9 }
            org.telegram.messenger.SharedConfig.directShareHash = r2     // Catch:{ all -> 0x02c9 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            java.lang.String r4 = "mainconfig"
            android.content.SharedPreferences r2 = r2.getSharedPreferences(r4, r3)     // Catch:{ all -> 0x02c9 }
            android.content.SharedPreferences$Editor r2 = r2.edit()     // Catch:{ all -> 0x02c9 }
            java.lang.String r4 = "directShareHash2"
            java.lang.String r5 = org.telegram.messenger.SharedConfig.directShareHash     // Catch:{ all -> 0x02c9 }
            android.content.SharedPreferences$Editor r2 = r2.putString(r4, r5)     // Catch:{ all -> 0x02c9 }
            r2.commit()     // Catch:{ all -> 0x02c9 }
        L_0x002a:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            java.util.List r2 = androidx.core.content.pm.ShortcutManagerCompat.getDynamicShortcuts(r2)     // Catch:{ all -> 0x02c9 }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x02c9 }
            r4.<init>()     // Catch:{ all -> 0x02c9 }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x02c9 }
            r5.<init>()     // Catch:{ all -> 0x02c9 }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x02c9 }
            r6.<init>()     // Catch:{ all -> 0x02c9 }
            java.lang.String r7 = "did3_"
            java.lang.String r8 = "compose"
            if (r2 == 0) goto L_0x00a3
            boolean r9 = r2.isEmpty()     // Catch:{ all -> 0x02c9 }
            if (r9 != 0) goto L_0x00a3
            r5.add(r8)     // Catch:{ all -> 0x02c9 }
            r9 = 0
        L_0x004f:
            int r10 = r21.size()     // Catch:{ all -> 0x02c9 }
            if (r9 >= r10) goto L_0x0076
            java.lang.Object r10 = r1.get(r9)     // Catch:{ all -> 0x02c9 }
            org.telegram.tgnet.TLRPC$TL_topPeer r10 = (org.telegram.tgnet.TLRPC$TL_topPeer) r10     // Catch:{ all -> 0x02c9 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x02c9 }
            r11.<init>()     // Catch:{ all -> 0x02c9 }
            r11.append(r7)     // Catch:{ all -> 0x02c9 }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.peer     // Catch:{ all -> 0x02c9 }
            long r12 = org.telegram.messenger.MessageObject.getPeerId(r10)     // Catch:{ all -> 0x02c9 }
            r11.append(r12)     // Catch:{ all -> 0x02c9 }
            java.lang.String r10 = r11.toString()     // Catch:{ all -> 0x02c9 }
            r5.add(r10)     // Catch:{ all -> 0x02c9 }
            int r9 = r9 + 1
            goto L_0x004f
        L_0x0076:
            r9 = 0
        L_0x0077:
            int r10 = r2.size()     // Catch:{ all -> 0x02c9 }
            if (r9 >= r10) goto L_0x0096
            java.lang.Object r10 = r2.get(r9)     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutInfoCompat r10 = (androidx.core.content.pm.ShortcutInfoCompat) r10     // Catch:{ all -> 0x02c9 }
            java.lang.String r10 = r10.getId()     // Catch:{ all -> 0x02c9 }
            boolean r11 = r5.remove(r10)     // Catch:{ all -> 0x02c9 }
            if (r11 != 0) goto L_0x0090
            r6.add(r10)     // Catch:{ all -> 0x02c9 }
        L_0x0090:
            r4.add(r10)     // Catch:{ all -> 0x02c9 }
            int r9 = r9 + 1
            goto L_0x0077
        L_0x0096:
            boolean r2 = r5.isEmpty()     // Catch:{ all -> 0x02c9 }
            if (r2 == 0) goto L_0x00a3
            boolean r2 = r6.isEmpty()     // Catch:{ all -> 0x02c9 }
            if (r2 == 0) goto L_0x00a3
            return
        L_0x00a3:
            android.content.Intent r2 = new android.content.Intent     // Catch:{ all -> 0x02c9 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r9 = org.telegram.ui.LaunchActivity.class
            r2.<init>(r5, r9)     // Catch:{ all -> 0x02c9 }
            java.lang.String r5 = "new_dialog"
            r2.setAction(r5)     // Catch:{ all -> 0x02c9 }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x02c9 }
            r5.<init>()     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r9 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ all -> 0x02c9 }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            r9.<init>((android.content.Context) r10, (java.lang.String) r8)     // Catch:{ all -> 0x02c9 }
            r10 = 2131626615(0x7f0e0a77, float:1.8880471E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r0, r10)     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r9 = r9.setShortLabel(r11)     // Catch:{ all -> 0x02c9 }
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r10)     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r9.setLongLabel(r0)     // Catch:{ all -> 0x02c9 }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            r10 = 2131166130(0x7var_b2, float:1.7946497E38)
            androidx.core.graphics.drawable.IconCompat r9 = androidx.core.graphics.drawable.IconCompat.createWithResource(r9, r10)     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setIcon(r9)     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setIntent(r2)     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutInfoCompat r0 = r0.build()     // Catch:{ all -> 0x02c9 }
            r5.add(r0)     // Catch:{ all -> 0x02c9 }
            boolean r0 = r4.contains(r8)     // Catch:{ all -> 0x02c9 }
            if (r0 == 0) goto L_0x00f4
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutManagerCompat.updateShortcuts(r0, r5)     // Catch:{ all -> 0x02c9 }
            goto L_0x00f9
        L_0x00f4:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutManagerCompat.addDynamicShortcuts(r0, r5)     // Catch:{ all -> 0x02c9 }
        L_0x00f9:
            r5.clear()     // Catch:{ all -> 0x02c9 }
            boolean r0 = r6.isEmpty()     // Catch:{ all -> 0x02c9 }
            if (r0 != 0) goto L_0x0107
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r0, r6)     // Catch:{ all -> 0x02c9 }
        L_0x0107:
            java.util.HashSet r2 = new java.util.HashSet     // Catch:{ all -> 0x02c9 }
            r6 = 1
            r2.<init>(r6)     // Catch:{ all -> 0x02c9 }
            java.lang.String r0 = SHORTCUT_CATEGORY     // Catch:{ all -> 0x02c9 }
            r2.add(r0)     // Catch:{ all -> 0x02c9 }
        L_0x0112:
            int r0 = r21.size()     // Catch:{ all -> 0x02c9 }
            if (r3 >= r0) goto L_0x02c9
            android.content.Intent r8 = new android.content.Intent     // Catch:{ all -> 0x02c9 }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            java.lang.Class<org.telegram.messenger.OpenChatReceiver> r9 = org.telegram.messenger.OpenChatReceiver.class
            r8.<init>(r0, r9)     // Catch:{ all -> 0x02c9 }
            java.lang.Object r0 = r1.get(r3)     // Catch:{ all -> 0x02c9 }
            org.telegram.tgnet.TLRPC$TL_topPeer r0 = (org.telegram.tgnet.TLRPC$TL_topPeer) r0     // Catch:{ all -> 0x02c9 }
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer     // Catch:{ all -> 0x02c9 }
            long r9 = org.telegram.messenger.MessageObject.getPeerId(r0)     // Catch:{ all -> 0x02c9 }
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r9)     // Catch:{ all -> 0x02c9 }
            if (r0 == 0) goto L_0x0146
            java.lang.String r0 = "userId"
            r8.putExtra(r0, r9)     // Catch:{ all -> 0x02c9 }
            org.telegram.messenger.MessagesController r0 = r20.getMessagesController()     // Catch:{ all -> 0x02c9 }
            java.lang.Long r12 = java.lang.Long.valueOf(r9)     // Catch:{ all -> 0x02c9 }
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r12)     // Catch:{ all -> 0x02c9 }
            r12 = 0
            goto L_0x015a
        L_0x0146:
            org.telegram.messenger.MessagesController r0 = r20.getMessagesController()     // Catch:{ all -> 0x02c9 }
            long r12 = -r9
            java.lang.Long r14 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x02c9 }
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r14)     // Catch:{ all -> 0x02c9 }
            java.lang.String r14 = "chatId"
            r8.putExtra(r14, r12)     // Catch:{ all -> 0x02c9 }
            r12 = r0
            r0 = 0
        L_0x015a:
            if (r0 == 0) goto L_0x0162
            boolean r13 = org.telegram.messenger.UserObject.isDeleted(r0)     // Catch:{ all -> 0x02c9 }
            if (r13 == 0) goto L_0x0166
        L_0x0162:
            if (r12 != 0) goto L_0x0166
            goto L_0x02c2
        L_0x0166:
            if (r0 == 0) goto L_0x0177
            java.lang.String r12 = r0.first_name     // Catch:{ all -> 0x02c9 }
            java.lang.String r13 = r0.last_name     // Catch:{ all -> 0x02c9 }
            java.lang.String r12 = org.telegram.messenger.ContactsController.formatName(r12, r13)     // Catch:{ all -> 0x02c9 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x02c9 }
            if (r0 == 0) goto L_0x0186
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x02c9 }
            goto L_0x0187
        L_0x0177:
            java.lang.String r0 = r12.title     // Catch:{ all -> 0x02c9 }
            org.telegram.tgnet.TLRPC$ChatPhoto r12 = r12.photo     // Catch:{ all -> 0x02c9 }
            if (r12 == 0) goto L_0x0185
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small     // Catch:{ all -> 0x02c9 }
            r19 = r12
            r12 = r0
            r0 = r19
            goto L_0x0187
        L_0x0185:
            r12 = r0
        L_0x0186:
            r0 = 0
        L_0x0187:
            java.lang.String r13 = "currentAccount"
            r14 = r20
            int r15 = r14.currentAccount     // Catch:{ all -> 0x02c9 }
            r8.putExtra(r13, r15)     // Catch:{ all -> 0x02c9 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x02c9 }
            r13.<init>()     // Catch:{ all -> 0x02c9 }
            java.lang.String r15 = "com.tmessages.openchat"
            r13.append(r15)     // Catch:{ all -> 0x02c9 }
            r13.append(r9)     // Catch:{ all -> 0x02c9 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x02c9 }
            r8.setAction(r13)     // Catch:{ all -> 0x02c9 }
            java.lang.String r13 = "dialogId"
            r8.putExtra(r13, r9)     // Catch:{ all -> 0x02c9 }
            java.lang.String r13 = "hash"
            java.lang.String r15 = org.telegram.messenger.SharedConfig.directShareHash     // Catch:{ all -> 0x02c9 }
            r8.putExtra(r13, r15)     // Catch:{ all -> 0x02c9 }
            r13 = 67108864(0x4000000, float:1.5046328E-36)
            r8.addFlags(r13)     // Catch:{ all -> 0x02c9 }
            if (r0 == 0) goto L_0x025e
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r6)     // Catch:{ all -> 0x0257 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0257 }
            android.graphics.Bitmap r13 = android.graphics.BitmapFactory.decodeFile(r0)     // Catch:{ all -> 0x0257 }
            if (r13 == 0) goto L_0x0255
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)     // Catch:{ all -> 0x0252 }
            android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0252 }
            android.graphics.Bitmap r15 = android.graphics.Bitmap.createBitmap(r0, r0, r15)     // Catch:{ all -> 0x0252 }
            android.graphics.Canvas r11 = new android.graphics.Canvas     // Catch:{ all -> 0x0252 }
            r11.<init>(r15)     // Catch:{ all -> 0x0252 }
            android.graphics.Paint r16 = roundPaint     // Catch:{ all -> 0x0252 }
            r17 = 1073741824(0x40000000, float:2.0)
            if (r16 != 0) goto L_0x0220
            android.graphics.Paint r6 = new android.graphics.Paint     // Catch:{ all -> 0x0252 }
            r1 = 3
            r6.<init>(r1)     // Catch:{ all -> 0x0252 }
            roundPaint = r6     // Catch:{ all -> 0x0252 }
            android.graphics.RectF r1 = new android.graphics.RectF     // Catch:{ all -> 0x0252 }
            r1.<init>()     // Catch:{ all -> 0x0252 }
            bitmapRect = r1     // Catch:{ all -> 0x0252 }
            android.graphics.Paint r1 = new android.graphics.Paint     // Catch:{ all -> 0x0252 }
            r6 = 1
            r1.<init>(r6)     // Catch:{ all -> 0x0252 }
            erasePaint = r1     // Catch:{ all -> 0x0252 }
            android.graphics.PorterDuffXfermode r6 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x0252 }
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x0252 }
            r6.<init>(r14)     // Catch:{ all -> 0x0252 }
            r1.setXfermode(r6)     // Catch:{ all -> 0x0252 }
            android.graphics.Path r1 = new android.graphics.Path     // Catch:{ all -> 0x0252 }
            r1.<init>()     // Catch:{ all -> 0x0252 }
            roundPath = r1     // Catch:{ all -> 0x0252 }
            int r6 = r0 / 2
            float r6 = (float) r6     // Catch:{ all -> 0x0252 }
            int r14 = r0 / 2
            float r14 = (float) r14     // Catch:{ all -> 0x0252 }
            int r0 = r0 / 2
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r17)     // Catch:{ all -> 0x0252 }
            int r0 = r0 - r18
            float r0 = (float) r0     // Catch:{ all -> 0x0252 }
            r18 = r15
            android.graphics.Path$Direction r15 = android.graphics.Path.Direction.CW     // Catch:{ all -> 0x0252 }
            r1.addCircle(r6, r14, r0, r15)     // Catch:{ all -> 0x0252 }
            android.graphics.Path r0 = roundPath     // Catch:{ all -> 0x0252 }
            r0.toggleInverseFillType()     // Catch:{ all -> 0x0252 }
            goto L_0x0222
        L_0x0220:
            r18 = r15
        L_0x0222:
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x0252 }
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)     // Catch:{ all -> 0x0252 }
            float r1 = (float) r1     // Catch:{ all -> 0x0252 }
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)     // Catch:{ all -> 0x0252 }
            float r6 = (float) r6     // Catch:{ all -> 0x0252 }
            r14 = 1110966272(0x42380000, float:46.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x0252 }
            float r15 = (float) r15     // Catch:{ all -> 0x0252 }
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x0252 }
            float r14 = (float) r14     // Catch:{ all -> 0x0252 }
            r0.set(r1, r6, r15, r14)     // Catch:{ all -> 0x0252 }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x0252 }
            android.graphics.Paint r1 = roundPaint     // Catch:{ all -> 0x0252 }
            r6 = 0
            r11.drawBitmap(r13, r6, r0, r1)     // Catch:{ all -> 0x0252 }
            android.graphics.Path r0 = roundPath     // Catch:{ all -> 0x0252 }
            android.graphics.Paint r1 = erasePaint     // Catch:{ all -> 0x0252 }
            r11.drawPath(r0, r1)     // Catch:{ all -> 0x0252 }
            r11.setBitmap(r6)     // Catch:{ Exception -> 0x024f }
        L_0x024f:
            r11 = r18
            goto L_0x0260
        L_0x0252:
            r0 = move-exception
            r11 = r13
            goto L_0x025a
        L_0x0255:
            r11 = r13
            goto L_0x0260
        L_0x0257:
            r0 = move-exception
            r6 = 0
            r11 = r6
        L_0x025a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x02c9 }
            goto L_0x0260
        L_0x025e:
            r6 = 0
            r11 = r6
        L_0x0260:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02c9 }
            r0.<init>()     // Catch:{ all -> 0x02c9 }
            r0.append(r7)     // Catch:{ all -> 0x02c9 }
            r0.append(r9)     // Catch:{ all -> 0x02c9 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02c9 }
            boolean r1 = android.text.TextUtils.isEmpty(r12)     // Catch:{ all -> 0x02c9 }
            if (r1 == 0) goto L_0x0277
            java.lang.String r12 = " "
        L_0x0277:
            androidx.core.content.pm.ShortcutInfoCompat$Builder r1 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ all -> 0x02c9 }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            r1.<init>((android.content.Context) r6, (java.lang.String) r0)     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r1 = r1.setShortLabel(r12)     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r1 = r1.setLongLabel(r12)     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r1 = r1.setIntent(r8)     // Catch:{ all -> 0x02c9 }
            boolean r6 = org.telegram.messenger.SharedConfig.directShare     // Catch:{ all -> 0x02c9 }
            if (r6 == 0) goto L_0x0291
            r1.setCategories(r2)     // Catch:{ all -> 0x02c9 }
        L_0x0291:
            if (r11 == 0) goto L_0x029b
            androidx.core.graphics.drawable.IconCompat r6 = androidx.core.graphics.drawable.IconCompat.createWithBitmap(r11)     // Catch:{ all -> 0x02c9 }
            r1.setIcon(r6)     // Catch:{ all -> 0x02c9 }
            goto L_0x02a7
        L_0x029b:
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            r8 = 2131166131(0x7var_b3, float:1.7946499E38)
            androidx.core.graphics.drawable.IconCompat r6 = androidx.core.graphics.drawable.IconCompat.createWithResource(r6, r8)     // Catch:{ all -> 0x02c9 }
            r1.setIcon(r6)     // Catch:{ all -> 0x02c9 }
        L_0x02a7:
            androidx.core.content.pm.ShortcutInfoCompat r1 = r1.build()     // Catch:{ all -> 0x02c9 }
            r5.add(r1)     // Catch:{ all -> 0x02c9 }
            boolean r0 = r4.contains(r0)     // Catch:{ all -> 0x02c9 }
            if (r0 == 0) goto L_0x02ba
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutManagerCompat.updateShortcuts(r0, r5)     // Catch:{ all -> 0x02c9 }
            goto L_0x02bf
        L_0x02ba:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x02c9 }
            androidx.core.content.pm.ShortcutManagerCompat.addDynamicShortcuts(r0, r5)     // Catch:{ all -> 0x02c9 }
        L_0x02bf:
            r5.clear()     // Catch:{ all -> 0x02c9 }
        L_0x02c2:
            int r3 = r3 + 1
            r1 = r21
            r6 = 1
            goto L_0x0112
        L_0x02c9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$buildShortcuts$95(java.util.ArrayList):void");
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
                getConnectionsManager().sendRequest(tLRPC$TL_contacts_getTopPeers, new MediaDataController$$ExternalSyntheticLambda130(this));
            } else if (!this.loaded) {
                this.loading = true;
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda14(this));
                this.loaded = true;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$97() {
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
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda72(this, arrayList3, arrayList4, arrayList, arrayList2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$96(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
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
    public /* synthetic */ void lambda$loadHints$102(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_contacts_topPeers) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda76(this, tLObject));
        } else if (tLObject instanceof TLRPC$TL_contacts_topPeersDisabled) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda3(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$100(TLObject tLObject) {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda92(this, tLRPC$TL_contacts_topPeers));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$99(TLRPC$TL_contacts_topPeers tLRPC$TL_contacts_topPeers) {
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
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda8(this));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$98() {
        getUserConfig().suggestContacts = true;
        getUserConfig().lastHintsSyncTime = (int) (System.currentTimeMillis() / 1000);
        getUserConfig().saveConfig(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadHints$101() {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda13(this));
        buildShortcuts();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearTopPeers$103() {
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
            Collections.sort(this.inlineBots, MediaDataController$$ExternalSyntheticLambda121.INSTANCE);
            if (this.inlineBots.size() > 20) {
                ArrayList<TLRPC$TL_topPeer> arrayList = this.inlineBots;
                arrayList.remove(arrayList.size() - 1);
            }
            savePeer(j, 1, tLRPC$TL_topPeer.rating);
            getNotificationCenter().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$increaseInlineRaiting$104(TLRPC$TL_topPeer tLRPC$TL_topPeer, TLRPC$TL_topPeer tLRPC$TL_topPeer2) {
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
                getConnectionsManager().sendRequest(tLRPC$TL_contacts_resetTopPeerRating, MediaDataController$$ExternalSyntheticLambda164.INSTANCE);
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
                getConnectionsManager().sendRequest(tLRPC$TL_contacts_resetTopPeerRating, MediaDataController$$ExternalSyntheticLambda162.INSTANCE);
                return;
            }
        }
    }

    public void increasePeerRaiting(long j) {
        TLRPC$User user;
        if (getUserConfig().suggestContacts && DialogObject.isUserDialog(j) && (user = getMessagesController().getUser(Long.valueOf(j))) != null && !user.bot && !user.self) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda26(this, j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$increasePeerRaiting$109(long j) {
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
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda30(this, j, d));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$increasePeerRaiting$108(long j, double d) {
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
        Collections.sort(this.hints, MediaDataController$$ExternalSyntheticLambda120.INSTANCE);
        savePeer(j, 0, tLRPC$TL_topPeer.rating);
        getNotificationCenter().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$increasePeerRaiting$107(TLRPC$TL_topPeer tLRPC$TL_topPeer, TLRPC$TL_topPeer tLRPC$TL_topPeer2) {
        double d = tLRPC$TL_topPeer.rating;
        double d2 = tLRPC$TL_topPeer2.rating;
        if (d > d2) {
            return -1;
        }
        return d < d2 ? 1 : 0;
    }

    private void savePeer(long j, int i, double d) {
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda33(this, j, i, d));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$savePeer$110(long j, int i, double d) {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda31(this, j, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$deletePeer$111(long j, int i) {
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

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0062 A[Catch:{ Exception -> 0x024f }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0093 A[Catch:{ Exception -> 0x024f }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00a0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00a8 A[SYNTHETIC, Splitter:B:39:0x00a8] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00d4 A[Catch:{ all -> 0x016d }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00ef A[Catch:{ all -> 0x016d }] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0183 A[Catch:{ Exception -> 0x024f }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01ee A[Catch:{ Exception -> 0x024f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void installShortcut(long r17) {
        /*
            r16 = this;
            r1 = r17
            android.content.Intent r3 = r16.createIntrnalShortcutIntent(r17)     // Catch:{ Exception -> 0x024f }
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r17)     // Catch:{ Exception -> 0x024f }
            r4 = 0
            if (r0 == 0) goto L_0x002f
            int r0 = org.telegram.messenger.DialogObject.getEncryptedChatId(r17)     // Catch:{ Exception -> 0x024f }
            org.telegram.messenger.MessagesController r5 = r16.getMessagesController()     // Catch:{ Exception -> 0x024f }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x024f }
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r5.getEncryptedChat(r0)     // Catch:{ Exception -> 0x024f }
            if (r0 != 0) goto L_0x0020
            return
        L_0x0020:
            org.telegram.messenger.MessagesController r5 = r16.getMessagesController()     // Catch:{ Exception -> 0x024f }
            long r6 = r0.user_id     // Catch:{ Exception -> 0x024f }
            java.lang.Long r0 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x024f }
            org.telegram.tgnet.TLRPC$User r0 = r5.getUser(r0)     // Catch:{ Exception -> 0x024f }
            goto L_0x0041
        L_0x002f:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r17)     // Catch:{ Exception -> 0x024f }
            if (r0 == 0) goto L_0x0044
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()     // Catch:{ Exception -> 0x024f }
            java.lang.Long r5 = java.lang.Long.valueOf(r17)     // Catch:{ Exception -> 0x024f }
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r5)     // Catch:{ Exception -> 0x024f }
        L_0x0041:
            r5 = r0
            r6 = r4
            goto L_0x0059
        L_0x0044:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r17)     // Catch:{ Exception -> 0x024f }
            if (r0 == 0) goto L_0x024e
            org.telegram.messenger.MessagesController r0 = r16.getMessagesController()     // Catch:{ Exception -> 0x024f }
            long r5 = -r1
            java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ Exception -> 0x024f }
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r5)     // Catch:{ Exception -> 0x024f }
            r6 = r0
            r5 = r4
        L_0x0059:
            if (r5 != 0) goto L_0x005e
            if (r6 != 0) goto L_0x005e
            return
        L_0x005e:
            r0 = 1
            r7 = 0
            if (r5 == 0) goto L_0x0093
            boolean r8 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r5)     // Catch:{ Exception -> 0x024f }
            if (r8 == 0) goto L_0x0074
            java.lang.String r8 = "RepliesTitle"
            r9 = 2131627715(0x7f0e0ec3, float:1.8882702E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x024f }
        L_0x0071:
            r9 = r4
            r10 = 1
            goto L_0x009e
        L_0x0074:
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r5)     // Catch:{ Exception -> 0x024f }
            if (r8 == 0) goto L_0x0084
            java.lang.String r8 = "SavedMessages"
            r9 = 2131627856(0x7f0e0var_, float:1.8882988E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x024f }
            goto L_0x0071
        L_0x0084:
            java.lang.String r8 = r5.first_name     // Catch:{ Exception -> 0x024f }
            java.lang.String r9 = r5.last_name     // Catch:{ Exception -> 0x024f }
            java.lang.String r8 = org.telegram.messenger.ContactsController.formatName(r8, r9)     // Catch:{ Exception -> 0x024f }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r5.photo     // Catch:{ Exception -> 0x024f }
            if (r9 == 0) goto L_0x009c
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small     // Catch:{ Exception -> 0x024f }
            goto L_0x009d
        L_0x0093:
            java.lang.String r8 = r6.title     // Catch:{ Exception -> 0x024f }
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r6.photo     // Catch:{ Exception -> 0x024f }
            if (r9 == 0) goto L_0x009c
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small     // Catch:{ Exception -> 0x024f }
            goto L_0x009d
        L_0x009c:
            r9 = r4
        L_0x009d:
            r10 = 0
        L_0x009e:
            if (r10 != 0) goto L_0x00a6
            if (r9 == 0) goto L_0x00a3
            goto L_0x00a6
        L_0x00a3:
            r9 = r4
            goto L_0x0171
        L_0x00a6:
            if (r10 != 0) goto L_0x00b9
            java.io.File r9 = org.telegram.messenger.FileLoader.getPathToAttach(r9, r0)     // Catch:{ all -> 0x00b5 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x00b5 }
            android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeFile(r9)     // Catch:{ all -> 0x00b5 }
            goto L_0x00ba
        L_0x00b5:
            r0 = move-exception
            r9 = r4
            goto L_0x016e
        L_0x00b9:
            r9 = r4
        L_0x00ba:
            if (r10 != 0) goto L_0x00be
            if (r9 == 0) goto L_0x0171
        L_0x00be:
            r11 = 1114112000(0x42680000, float:58.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x016d }
            android.graphics.Bitmap$Config r12 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x016d }
            android.graphics.Bitmap r12 = android.graphics.Bitmap.createBitmap(r11, r11, r12)     // Catch:{ all -> 0x016d }
            r12.eraseColor(r7)     // Catch:{ all -> 0x016d }
            android.graphics.Canvas r13 = new android.graphics.Canvas     // Catch:{ all -> 0x016d }
            r13.<init>(r12)     // Catch:{ all -> 0x016d }
            if (r10 == 0) goto L_0x00ef
            org.telegram.ui.Components.AvatarDrawable r10 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x016d }
            r10.<init>((org.telegram.tgnet.TLRPC$User) r5)     // Catch:{ all -> 0x016d }
            boolean r14 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r5)     // Catch:{ all -> 0x016d }
            if (r14 == 0) goto L_0x00e5
            r0 = 12
            r10.setAvatarType(r0)     // Catch:{ all -> 0x016d }
            goto L_0x00e8
        L_0x00e5:
            r10.setAvatarType(r0)     // Catch:{ all -> 0x016d }
        L_0x00e8:
            r10.setBounds(r7, r7, r11, r11)     // Catch:{ all -> 0x016d }
            r10.draw(r13)     // Catch:{ all -> 0x016d }
            goto L_0x013e
        L_0x00ef:
            android.graphics.BitmapShader r10 = new android.graphics.BitmapShader     // Catch:{ all -> 0x016d }
            android.graphics.Shader$TileMode r14 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x016d }
            r10.<init>(r9, r14, r14)     // Catch:{ all -> 0x016d }
            android.graphics.Paint r14 = roundPaint     // Catch:{ all -> 0x016d }
            if (r14 != 0) goto L_0x0108
            android.graphics.Paint r14 = new android.graphics.Paint     // Catch:{ all -> 0x016d }
            r14.<init>(r0)     // Catch:{ all -> 0x016d }
            roundPaint = r14     // Catch:{ all -> 0x016d }
            android.graphics.RectF r0 = new android.graphics.RectF     // Catch:{ all -> 0x016d }
            r0.<init>()     // Catch:{ all -> 0x016d }
            bitmapRect = r0     // Catch:{ all -> 0x016d }
        L_0x0108:
            float r0 = (float) r11     // Catch:{ all -> 0x016d }
            int r14 = r9.getWidth()     // Catch:{ all -> 0x016d }
            float r14 = (float) r14     // Catch:{ all -> 0x016d }
            float r0 = r0 / r14
            r13.save()     // Catch:{ all -> 0x016d }
            r13.scale(r0, r0)     // Catch:{ all -> 0x016d }
            android.graphics.Paint r0 = roundPaint     // Catch:{ all -> 0x016d }
            r0.setShader(r10)     // Catch:{ all -> 0x016d }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x016d }
            int r10 = r9.getWidth()     // Catch:{ all -> 0x016d }
            float r10 = (float) r10     // Catch:{ all -> 0x016d }
            int r14 = r9.getHeight()     // Catch:{ all -> 0x016d }
            float r14 = (float) r14     // Catch:{ all -> 0x016d }
            r15 = 0
            r0.set(r15, r15, r10, r14)     // Catch:{ all -> 0x016d }
            android.graphics.RectF r0 = bitmapRect     // Catch:{ all -> 0x016d }
            int r10 = r9.getWidth()     // Catch:{ all -> 0x016d }
            float r10 = (float) r10     // Catch:{ all -> 0x016d }
            int r14 = r9.getHeight()     // Catch:{ all -> 0x016d }
            float r14 = (float) r14     // Catch:{ all -> 0x016d }
            android.graphics.Paint r15 = roundPaint     // Catch:{ all -> 0x016d }
            r13.drawRoundRect(r0, r10, r14, r15)     // Catch:{ all -> 0x016d }
            r13.restore()     // Catch:{ all -> 0x016d }
        L_0x013e:
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x016d }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x016d }
            r10 = 2131165305(0x7var_, float:1.7944823E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r10)     // Catch:{ all -> 0x016d }
            r10 = 1097859072(0x41700000, float:15.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ all -> 0x016d }
            int r11 = r11 - r10
            r14 = 1073741824(0x40000000, float:2.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x016d }
            int r15 = r11 - r15
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x016d }
            int r11 = r11 - r14
            int r14 = r15 + r10
            int r10 = r10 + r11
            r0.setBounds(r15, r11, r14, r10)     // Catch:{ all -> 0x016d }
            r0.draw(r13)     // Catch:{ all -> 0x016d }
            r13.setBitmap(r4)     // Catch:{ Exception -> 0x016b }
        L_0x016b:
            r9 = r12
            goto L_0x0171
        L_0x016d:
            r0 = move-exception
        L_0x016e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x024f }
        L_0x0171:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x024f }
            r10 = 26
            r11 = 2131165303(0x7var_, float:1.794482E38)
            r12 = 2131165302(0x7var_, float:1.7944817E38)
            r13 = 2131165306(0x7var_a, float:1.7944825E38)
            r14 = 2131165304(0x7var_, float:1.7944821E38)
            if (r0 < r10) goto L_0x01ee
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ Exception -> 0x024f }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x024f }
            r10.<init>()     // Catch:{ Exception -> 0x024f }
            java.lang.String r15 = "sdid_"
            r10.append(r15)     // Catch:{ Exception -> 0x024f }
            r10.append(r1)     // Catch:{ Exception -> 0x024f }
            java.lang.String r1 = r10.toString()     // Catch:{ Exception -> 0x024f }
            r0.<init>((android.content.Context) r7, (java.lang.String) r1)     // Catch:{ Exception -> 0x024f }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setShortLabel(r8)     // Catch:{ Exception -> 0x024f }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r0 = r0.setIntent(r3)     // Catch:{ Exception -> 0x024f }
            if (r9 == 0) goto L_0x01ad
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithBitmap(r9)     // Catch:{ Exception -> 0x024f }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x024f }
            goto L_0x01e4
        L_0x01ad:
            if (r5 == 0) goto L_0x01c7
            boolean r1 = r5.bot     // Catch:{ Exception -> 0x024f }
            if (r1 == 0) goto L_0x01bd
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithResource(r1, r12)     // Catch:{ Exception -> 0x024f }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x024f }
            goto L_0x01e4
        L_0x01bd:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithResource(r1, r13)     // Catch:{ Exception -> 0x024f }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x024f }
            goto L_0x01e4
        L_0x01c7:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r6)     // Catch:{ Exception -> 0x024f }
            if (r1 == 0) goto L_0x01db
            boolean r1 = r6.megagroup     // Catch:{ Exception -> 0x024f }
            if (r1 != 0) goto L_0x01db
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithResource(r1, r11)     // Catch:{ Exception -> 0x024f }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x024f }
            goto L_0x01e4
        L_0x01db:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            androidx.core.graphics.drawable.IconCompat r1 = androidx.core.graphics.drawable.IconCompat.createWithResource(r1, r14)     // Catch:{ Exception -> 0x024f }
            r0.setIcon(r1)     // Catch:{ Exception -> 0x024f }
        L_0x01e4:
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            androidx.core.content.pm.ShortcutInfoCompat r0 = r0.build()     // Catch:{ Exception -> 0x024f }
            androidx.core.content.pm.ShortcutManagerCompat.requestPinShortcut(r1, r0, r4)     // Catch:{ Exception -> 0x024f }
            goto L_0x0253
        L_0x01ee:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x024f }
            r0.<init>()     // Catch:{ Exception -> 0x024f }
            if (r9 == 0) goto L_0x01fb
            java.lang.String r1 = "android.intent.extra.shortcut.ICON"
            r0.putExtra(r1, r9)     // Catch:{ Exception -> 0x024f }
            goto L_0x0234
        L_0x01fb:
            java.lang.String r1 = "android.intent.extra.shortcut.ICON_RESOURCE"
            if (r5 == 0) goto L_0x0217
            boolean r2 = r5.bot     // Catch:{ Exception -> 0x024f }
            if (r2 == 0) goto L_0x020d
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r12)     // Catch:{ Exception -> 0x024f }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x024f }
            goto L_0x0234
        L_0x020d:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r13)     // Catch:{ Exception -> 0x024f }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x024f }
            goto L_0x0234
        L_0x0217:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r6)     // Catch:{ Exception -> 0x024f }
            if (r2 == 0) goto L_0x022b
            boolean r2 = r6.megagroup     // Catch:{ Exception -> 0x024f }
            if (r2 != 0) goto L_0x022b
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r11)     // Catch:{ Exception -> 0x024f }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x024f }
            goto L_0x0234
        L_0x022b:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            android.content.Intent$ShortcutIconResource r2 = android.content.Intent.ShortcutIconResource.fromContext(r2, r14)     // Catch:{ Exception -> 0x024f }
            r0.putExtra(r1, r2)     // Catch:{ Exception -> 0x024f }
        L_0x0234:
            java.lang.String r1 = "android.intent.extra.shortcut.INTENT"
            r0.putExtra(r1, r3)     // Catch:{ Exception -> 0x024f }
            java.lang.String r1 = "android.intent.extra.shortcut.NAME"
            r0.putExtra(r1, r8)     // Catch:{ Exception -> 0x024f }
            java.lang.String r1 = "duplicate"
            r0.putExtra(r1, r7)     // Catch:{ Exception -> 0x024f }
            java.lang.String r1 = "com.android.launcher.action.INSTALL_SHORTCUT"
            r0.setAction(r1)     // Catch:{ Exception -> 0x024f }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x024f }
            r1.sendBroadcast(r0)     // Catch:{ Exception -> 0x024f }
            goto L_0x0253
        L_0x024e:
            return
        L_0x024f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0253:
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
    public static /* synthetic */ int lambda$static$112(TLRPC$MessageEntity tLRPC$MessageEntity, TLRPC$MessageEntity tLRPC$MessageEntity2) {
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
            getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new MediaDataController$$ExternalSyntheticLambda137(this, i2, tLRPC$TL_messages_search, j, i));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPinnedMessages$114(int i, TLRPC$TL_messages_search tLRPC$TL_messages_search, long j, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda27(this, j2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPinnedMessages$113(long j) {
        this.loadingPinnedMessages.remove(j);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPinnedMessages$115(long j, long j2, ArrayList arrayList) {
        loadPinnedMessageInternal(j, j2, arrayList, false);
    }

    public ArrayList<MessageObject> loadPinnedMessages(long j, long j2, ArrayList<Integer> arrayList, boolean z) {
        if (!z) {
            return loadPinnedMessageInternal(j, j2, arrayList, true);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda39(this, j, j2, arrayList));
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v13, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0174 A[Catch:{ Exception -> 0x01c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.ArrayList<org.telegram.messenger.MessageObject> loadPinnedMessageInternal(long r18, long r20, java.util.ArrayList<java.lang.Integer> r22, boolean r23) {
        /*
            r17 = this;
            r5 = r18
            r3 = r20
            r0 = r22
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x01c9 }
            r1.<init>(r0)     // Catch:{ Exception -> 0x01c9 }
            r7 = 0
            java.lang.String r9 = ","
            int r10 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r10 == 0) goto L_0x0034
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01c9 }
            r10.<init>()     // Catch:{ Exception -> 0x01c9 }
            int r11 = r22.size()     // Catch:{ Exception -> 0x01c9 }
            r12 = 0
        L_0x001d:
            if (r12 >= r11) goto L_0x0038
            java.lang.Object r13 = r0.get(r12)     // Catch:{ Exception -> 0x01c9 }
            java.lang.Integer r13 = (java.lang.Integer) r13     // Catch:{ Exception -> 0x01c9 }
            int r14 = r10.length()     // Catch:{ Exception -> 0x01c9 }
            if (r14 == 0) goto L_0x002e
            r10.append(r9)     // Catch:{ Exception -> 0x01c9 }
        L_0x002e:
            r10.append(r13)     // Catch:{ Exception -> 0x01c9 }
            int r12 = r12 + 1
            goto L_0x001d
        L_0x0034:
            java.lang.String r10 = android.text.TextUtils.join(r9, r0)     // Catch:{ Exception -> 0x01c9 }
        L_0x0038:
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x01c9 }
            r0.<init>()     // Catch:{ Exception -> 0x01c9 }
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ Exception -> 0x01c9 }
            r11.<init>()     // Catch:{ Exception -> 0x01c9 }
            java.util.ArrayList r12 = new java.util.ArrayList     // Catch:{ Exception -> 0x01c9 }
            r12.<init>()     // Catch:{ Exception -> 0x01c9 }
            java.util.ArrayList r13 = new java.util.ArrayList     // Catch:{ Exception -> 0x01c9 }
            r13.<init>()     // Catch:{ Exception -> 0x01c9 }
            java.util.ArrayList r14 = new java.util.ArrayList     // Catch:{ Exception -> 0x01c9 }
            r14.<init>()     // Catch:{ Exception -> 0x01c9 }
            org.telegram.messenger.UserConfig r15 = r17.getUserConfig()     // Catch:{ Exception -> 0x01c9 }
            long r7 = r15.clientUserId     // Catch:{ Exception -> 0x01c9 }
            org.telegram.messenger.MessagesStorage r15 = r17.getMessagesStorage()     // Catch:{ Exception -> 0x01c9 }
            org.telegram.SQLite.SQLiteDatabase r15 = r15.getDatabase()     // Catch:{ Exception -> 0x01c9 }
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ Exception -> 0x01c9 }
            r22 = r12
            java.lang.String r12 = "SELECT data, mid, date FROM messages_v2 WHERE mid IN (%s) AND uid = %d"
            r16 = r11
            r11 = 2
            java.lang.Object[] r3 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x01c9 }
            r4 = 0
            r3[r4] = r10     // Catch:{ Exception -> 0x01c9 }
            java.lang.Long r10 = java.lang.Long.valueOf(r18)     // Catch:{ Exception -> 0x01c9 }
            r11 = 1
            r3[r11] = r10     // Catch:{ Exception -> 0x01c9 }
            java.lang.String r2 = java.lang.String.format(r2, r12, r3)     // Catch:{ Exception -> 0x01c9 }
            java.lang.Object[] r3 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x01c9 }
            org.telegram.SQLite.SQLiteCursor r2 = r15.queryFinalized(r2, r3)     // Catch:{ Exception -> 0x01c9 }
        L_0x007e:
            boolean r3 = r2.next()     // Catch:{ Exception -> 0x01c9 }
            if (r3 == 0) goto L_0x00be
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r4)     // Catch:{ Exception -> 0x01c9 }
            if (r3 == 0) goto L_0x00bc
            int r10 = r3.readInt32(r4)     // Catch:{ Exception -> 0x01c9 }
            org.telegram.tgnet.TLRPC$Message r10 = org.telegram.tgnet.TLRPC$Message.TLdeserialize(r3, r10, r4)     // Catch:{ Exception -> 0x01c9 }
            org.telegram.tgnet.TLRPC$MessageAction r4 = r10.action     // Catch:{ Exception -> 0x01c9 }
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear     // Catch:{ Exception -> 0x01c9 }
            if (r4 != 0) goto L_0x00b9
            r10.readAttachPath(r3, r7)     // Catch:{ Exception -> 0x01c9 }
            int r4 = r2.intValue(r11)     // Catch:{ Exception -> 0x01c9 }
            r10.id = r4     // Catch:{ Exception -> 0x01c9 }
            r4 = 2
            int r12 = r2.intValue(r4)     // Catch:{ Exception -> 0x01c9 }
            r10.date = r12     // Catch:{ Exception -> 0x01c9 }
            r10.dialog_id = r5     // Catch:{ Exception -> 0x01c9 }
            org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r10, r13, r14)     // Catch:{ Exception -> 0x01c9 }
            r0.add(r10)     // Catch:{ Exception -> 0x01c9 }
            int r4 = r10.id     // Catch:{ Exception -> 0x01c9 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x01c9 }
            r1.remove(r4)     // Catch:{ Exception -> 0x01c9 }
        L_0x00b9:
            r3.reuse()     // Catch:{ Exception -> 0x01c9 }
        L_0x00bc:
            r4 = 0
            goto L_0x007e
        L_0x00be:
            r2.dispose()     // Catch:{ Exception -> 0x01c9 }
            boolean r2 = r1.isEmpty()     // Catch:{ Exception -> 0x01c9 }
            if (r2 != 0) goto L_0x0122
            org.telegram.messenger.MessagesStorage r2 = r17.getMessagesStorage()     // Catch:{ Exception -> 0x01c9 }
            org.telegram.SQLite.SQLiteDatabase r2 = r2.getDatabase()     // Catch:{ Exception -> 0x01c9 }
            java.util.Locale r3 = java.util.Locale.US     // Catch:{ Exception -> 0x01c9 }
            java.lang.String r4 = "SELECT data FROM chat_pinned_v2 WHERE uid = %d AND mid IN (%s)"
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x01c9 }
            java.lang.Long r12 = java.lang.Long.valueOf(r18)     // Catch:{ Exception -> 0x01c9 }
            r15 = 0
            r10[r15] = r12     // Catch:{ Exception -> 0x01c9 }
            java.lang.String r12 = android.text.TextUtils.join(r9, r1)     // Catch:{ Exception -> 0x01c9 }
            r10[r11] = r12     // Catch:{ Exception -> 0x01c9 }
            java.lang.String r3 = java.lang.String.format(r3, r4, r10)     // Catch:{ Exception -> 0x01c9 }
            java.lang.Object[] r4 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x01c9 }
            org.telegram.SQLite.SQLiteCursor r2 = r2.queryFinalized(r3, r4)     // Catch:{ Exception -> 0x01c9 }
        L_0x00ed:
            boolean r3 = r2.next()     // Catch:{ Exception -> 0x01c9 }
            if (r3 == 0) goto L_0x011f
            org.telegram.tgnet.NativeByteBuffer r3 = r2.byteBufferValue(r15)     // Catch:{ Exception -> 0x01c9 }
            if (r3 == 0) goto L_0x00ed
            int r4 = r3.readInt32(r15)     // Catch:{ Exception -> 0x01c9 }
            org.telegram.tgnet.TLRPC$Message r4 = org.telegram.tgnet.TLRPC$Message.TLdeserialize(r3, r4, r15)     // Catch:{ Exception -> 0x01c9 }
            org.telegram.tgnet.TLRPC$MessageAction r10 = r4.action     // Catch:{ Exception -> 0x01c9 }
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear     // Catch:{ Exception -> 0x01c9 }
            if (r10 != 0) goto L_0x011b
            r4.readAttachPath(r3, r7)     // Catch:{ Exception -> 0x01c9 }
            r4.dialog_id = r5     // Catch:{ Exception -> 0x01c9 }
            org.telegram.messenger.MessagesStorage.addUsersAndChatsFromMessage(r4, r13, r14)     // Catch:{ Exception -> 0x01c9 }
            r0.add(r4)     // Catch:{ Exception -> 0x01c9 }
            int r4 = r4.id     // Catch:{ Exception -> 0x01c9 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x01c9 }
            r1.remove(r4)     // Catch:{ Exception -> 0x01c9 }
        L_0x011b:
            r3.reuse()     // Catch:{ Exception -> 0x01c9 }
            goto L_0x00ed
        L_0x011f:
            r2.dispose()     // Catch:{ Exception -> 0x01c9 }
        L_0x0122:
            boolean r2 = r1.isEmpty()     // Catch:{ Exception -> 0x01c9 }
            if (r2 != 0) goto L_0x016c
            r3 = r20
            r7 = 0
            int r2 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x0156
            org.telegram.tgnet.TLRPC$TL_channels_getMessages r8 = new org.telegram.tgnet.TLRPC$TL_channels_getMessages     // Catch:{ Exception -> 0x01c9 }
            r8.<init>()     // Catch:{ Exception -> 0x01c9 }
            org.telegram.messenger.MessagesController r2 = r17.getMessagesController()     // Catch:{ Exception -> 0x01c9 }
            org.telegram.tgnet.TLRPC$InputChannel r2 = r2.getInputChannel((long) r3)     // Catch:{ Exception -> 0x01c9 }
            r8.channel = r2     // Catch:{ Exception -> 0x01c9 }
            r8.id = r1     // Catch:{ Exception -> 0x01c9 }
            org.telegram.tgnet.ConnectionsManager r10 = r17.getConnectionsManager()     // Catch:{ Exception -> 0x01c9 }
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda145 r11 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda145     // Catch:{ Exception -> 0x01c9 }
            r1 = r11
            r2 = r17
            r3 = r20
            r5 = r18
            r7 = r8
            r1.<init>(r2, r3, r5, r7)     // Catch:{ Exception -> 0x01c9 }
            r10.sendRequest(r8, r11)     // Catch:{ Exception -> 0x01c9 }
            goto L_0x016c
        L_0x0156:
            org.telegram.tgnet.TLRPC$TL_messages_getMessages r2 = new org.telegram.tgnet.TLRPC$TL_messages_getMessages     // Catch:{ Exception -> 0x01c9 }
            r2.<init>()     // Catch:{ Exception -> 0x01c9 }
            r2.id = r1     // Catch:{ Exception -> 0x01c9 }
            org.telegram.tgnet.ConnectionsManager r1 = r17.getConnectionsManager()     // Catch:{ Exception -> 0x01c9 }
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda147 r3 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda147     // Catch:{ Exception -> 0x01c9 }
            r4 = r17
            r3.<init>(r4, r5, r2)     // Catch:{ Exception -> 0x01c7 }
            r1.sendRequest(r2, r3)     // Catch:{ Exception -> 0x01c7 }
            goto L_0x016e
        L_0x016c:
            r4 = r17
        L_0x016e:
            boolean r1 = r0.isEmpty()     // Catch:{ Exception -> 0x01c7 }
            if (r1 != 0) goto L_0x01cf
            boolean r1 = r13.isEmpty()     // Catch:{ Exception -> 0x01c7 }
            if (r1 != 0) goto L_0x0188
            org.telegram.messenger.MessagesStorage r1 = r17.getMessagesStorage()     // Catch:{ Exception -> 0x01c7 }
            java.lang.String r2 = android.text.TextUtils.join(r9, r13)     // Catch:{ Exception -> 0x01c7 }
            r3 = r16
            r1.getUsersInternal(r2, r3)     // Catch:{ Exception -> 0x01c7 }
            goto L_0x018a
        L_0x0188:
            r3 = r16
        L_0x018a:
            boolean r1 = r14.isEmpty()     // Catch:{ Exception -> 0x01c7 }
            if (r1 != 0) goto L_0x019e
            org.telegram.messenger.MessagesStorage r1 = r17.getMessagesStorage()     // Catch:{ Exception -> 0x01c7 }
            java.lang.String r2 = android.text.TextUtils.join(r9, r14)     // Catch:{ Exception -> 0x01c7 }
            r5 = r22
            r1.getChatsInternal(r2, r5)     // Catch:{ Exception -> 0x01c7 }
            goto L_0x01a0
        L_0x019e:
            r5 = r22
        L_0x01a0:
            if (r23 == 0) goto L_0x01b5
            r1 = 1
            r2 = 1
            r18 = r17
            r19 = r0
            r20 = r3
            r21 = r5
            r22 = r1
            r23 = r2
            java.util.ArrayList r0 = r18.broadcastPinnedMessage(r19, r20, r21, r22, r23)     // Catch:{ Exception -> 0x01c7 }
            return r0
        L_0x01b5:
            r1 = 1
            r2 = 0
            r18 = r17
            r19 = r0
            r20 = r3
            r21 = r5
            r22 = r1
            r23 = r2
            r18.broadcastPinnedMessage(r19, r20, r21, r22, r23)     // Catch:{ Exception -> 0x01c7 }
            goto L_0x01cf
        L_0x01c7:
            r0 = move-exception
            goto L_0x01cc
        L_0x01c9:
            r0 = move-exception
            r4 = r17
        L_0x01cc:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01cf:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.loadPinnedMessageInternal(long, long, java.util.ArrayList, boolean):java.util.ArrayList");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:9:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$116(long r13, long r15, org.telegram.tgnet.TLRPC$TL_channels_getMessages r17, org.telegram.tgnet.TLObject r18, org.telegram.tgnet.TLRPC$TL_error r19) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPinnedMessageInternal$116(long, long, org.telegram.tgnet.TLRPC$TL_channels_getMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:9:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadPinnedMessageInternal$117(long r11, org.telegram.tgnet.TLRPC$TL_messages_getMessages r13, org.telegram.tgnet.TLObject r14, org.telegram.tgnet.TLRPC$TL_error r15) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$loadPinnedMessageInternal$117(long, org.telegram.tgnet.TLRPC$TL_messages_getMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    private void savePinnedMessages(long j, ArrayList<TLRPC$Message> arrayList) {
        if (!arrayList.isEmpty()) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda67(this, arrayList, j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$savePinnedMessages$118(ArrayList arrayList, long j) {
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
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda73(this, arrayList4, z, arrayList5));
            int size = arrayList.size();
            int i3 = 0;
            int i4 = 0;
            while (i4 < size) {
                TLRPC$Message tLRPC$Message = arrayList.get(i4);
                TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
                if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto)) {
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
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda75(this, arrayList2, z, arrayList3, arrayList, arrayList6, longSparseArray, longSparseArray2));
        return null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastPinnedMessage$119(ArrayList arrayList, boolean z, ArrayList arrayList2) {
        getMessagesController().putUsers(arrayList, z);
        getMessagesController().putChats(arrayList2, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastPinnedMessage$121(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        boolean z2 = z;
        ArrayList arrayList5 = arrayList4;
        getMessagesController().putUsers(arrayList, z2);
        getMessagesController().putChats(arrayList2, z2);
        int size = arrayList3.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList3.get(i2);
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto)) {
                i++;
            }
            arrayList5.add(new MessageObject(this.currentAccount, tLRPC$Message, (LongSparseArray<TLRPC$User>) longSparseArray, (LongSparseArray<TLRPC$Chat>) longSparseArray2, false, i < 30));
        }
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda61(this, arrayList5));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastPinnedMessage$120(ArrayList arrayList) {
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
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda69 r9 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda69
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
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda49 r9 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda49
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
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$123(ArrayList arrayList, long j, LongSparseArray longSparseArray, Runnable runnable) {
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
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda40(this, j, arrayList2));
            if (runnable != null) {
                runnable.run();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$122(long j, ArrayList arrayList) {
        getNotificationCenter().postNotificationName(NotificationCenter.replyMessagesDidLoad, Long.valueOf(j), arrayList, null);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$127(LongSparseArray longSparseArray, LongSparseArray longSparseArray2, boolean z, long j, Runnable runnable) {
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
                            MessagesStorage.addUsersAndChatsFromMessage(TLdeserialize, arrayList4, arrayList5);
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
                        MediaDataController$$ExternalSyntheticLambda144 mediaDataController$$ExternalSyntheticLambda144 = r1;
                        MediaDataController$$ExternalSyntheticLambda144 mediaDataController$$ExternalSyntheticLambda1442 = new MediaDataController$$ExternalSyntheticLambda144(this, j, keyAt2, longSparseArray, z, runnable);
                        connectionsManager.sendRequest(tLRPC$TL_messages_getScheduledMessages, mediaDataController$$ExternalSyntheticLambda144);
                    } else {
                        i = size2;
                        if (keyAt2 != 0) {
                            TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
                            tLRPC$TL_channels_getMessages.channel = getMessagesController().getInputChannel(keyAt2);
                            tLRPC$TL_channels_getMessages.id = (ArrayList) longSparseArray4.valueAt(i4);
                            MediaDataController$$ExternalSyntheticLambda143 mediaDataController$$ExternalSyntheticLambda143 = r1;
                            ConnectionsManager connectionsManager2 = getConnectionsManager();
                            MediaDataController$$ExternalSyntheticLambda143 mediaDataController$$ExternalSyntheticLambda1432 = new MediaDataController$$ExternalSyntheticLambda143(this, j, keyAt2, longSparseArray, z, runnable);
                            connectionsManager2.sendRequest(tLRPC$TL_channels_getMessages, mediaDataController$$ExternalSyntheticLambda143);
                        } else {
                            TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages = new TLRPC$TL_messages_getMessages();
                            tLRPC$TL_messages_getMessages.id = (ArrayList) longSparseArray4.valueAt(i4);
                            getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages, new MediaDataController$$ExternalSyntheticLambda146(this, j, longSparseArray, z, runnable));
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
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$124(long j, long j2, LongSparseArray longSparseArray, boolean z, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$125(long j, long j2, LongSparseArray longSparseArray, boolean z, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
    public /* synthetic */ void lambda$loadReplyMessagesForMessages$126(long j, LongSparseArray longSparseArray, boolean z, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda110(this, z, arrayList, longSparseArray));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveReplyMessages$128(boolean z, ArrayList arrayList, LongSparseArray longSparseArray) {
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
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda74(this, arrayList2, z, arrayList3, arrayList6, longSparseArray, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastReplyMessages$129(ArrayList arrayList, boolean z, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray, long j) {
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

    public static ArrayList<TextStyleSpan.TextStyleRun> getTextStyleRuns(ArrayList<TLRPC$MessageEntity> arrayList, CharSequence charSequence, int i) {
        int i2;
        int i3;
        ArrayList<TextStyleSpan.TextStyleRun> arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList(arrayList);
        Collections.sort(arrayList3, MediaDataController$$ExternalSyntheticLambda119.INSTANCE);
        int size = arrayList3.size();
        for (int i4 = 0; i4 < size; i4++) {
            TLRPC$MessageEntity tLRPC$MessageEntity = (TLRPC$MessageEntity) arrayList3.get(i4);
            if (tLRPC$MessageEntity != null && tLRPC$MessageEntity.length > 0 && (i2 = tLRPC$MessageEntity.offset) >= 0 && i2 < charSequence.length()) {
                if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length > charSequence.length()) {
                    tLRPC$MessageEntity.length = charSequence.length() - tLRPC$MessageEntity.offset;
                }
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
        return arrayList2;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$getTextStyleRuns$130(TLRPC$MessageEntity tLRPC$MessageEntity, TLRPC$MessageEntity tLRPC$MessageEntity2) {
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
        if (r1 != null) goto L_0x0059;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0054, code lost:
        r1 = new java.util.ArrayList<>();
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
        r1.add(r3);
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
        r1.add(r3);
        r10 = r10 - 2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> getEntities(java.lang.CharSequence[] r19, boolean r20) {
        /*
            r18 = this;
            r0 = r18
            r1 = 0
            if (r19 == 0) goto L_0x02e2
            r2 = 0
            r3 = r19[r2]
            if (r3 != 0) goto L_0x000c
            goto L_0x02e2
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
            if (r1 != 0) goto L_0x0059
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
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
            java.lang.CharSequence r13 = r0.substring(r13, r2, r14)
            r14 = r19[r2]
            int r15 = r6 + 3
            java.lang.CharSequence r14 = r0.substring(r14, r15, r5)
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
            java.lang.CharSequence r3 = r0.substring(r11, r15, r3)
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
            r1.add(r3)
            int r10 = r10 + -6
            goto L_0x015b
        L_0x011e:
            int r3 = r6 + 1
            if (r3 == r5) goto L_0x015b
            r4 = 3
            java.lang.CharSequence[] r4 = new java.lang.CharSequence[r4]
            r7 = r19[r2]
            java.lang.CharSequence r7 = r0.substring(r7, r2, r6)
            r4[r2] = r7
            r7 = r19[r2]
            java.lang.CharSequence r3 = r0.substring(r7, r3, r5)
            r4[r9] = r3
            r3 = r19[r2]
            int r7 = r5 + 1
            r11 = r19[r2]
            int r11 = r11.length()
            java.lang.CharSequence r3 = r0.substring(r3, r7, r11)
            r4[r8] = r3
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.concat(r4)
            r19[r2] = r3
            org.telegram.tgnet.TLRPC$TL_messageEntityCode r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode
            r3.<init>()
            r3.offset = r6
            int r5 = r5 - r6
            int r5 = r5 - r9
            r3.length = r5
            r1.add(r3)
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
            java.lang.CharSequence r4 = r0.substring(r4, r2, r6)
            r3[r2] = r4
            r4 = r19[r2]
            int r5 = r6 + 2
            r8 = r19[r2]
            int r8 = r8.length()
            java.lang.CharSequence r4 = r0.substring(r4, r5, r8)
            r3[r9] = r4
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.concat(r3)
            r19[r2] = r3
            if (r1 != 0) goto L_0x018b
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L_0x018b:
            org.telegram.tgnet.TLRPC$TL_messageEntityCode r3 = new org.telegram.tgnet.TLRPC$TL_messageEntityCode
            r3.<init>()
            r3.offset = r6
            r3.length = r9
            r1.add(r3)
        L_0x0197:
            r3 = r19[r2]
            boolean r3 = r3 instanceof android.text.Spanned
            if (r3 == 0) goto L_0x02b5
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
            boolean r11 = checkInclusion(r8, r1, r2)
            if (r11 != 0) goto L_0x01e3
            boolean r11 = checkInclusion(r10, r1, r9)
            if (r11 != 0) goto L_0x01e3
            boolean r11 = checkIntersection(r8, r10, r1)
            if (r11 == 0) goto L_0x01d5
            goto L_0x01e3
        L_0x01d5:
            if (r1 != 0) goto L_0x01dc
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L_0x01dc:
            int r6 = r6.getStyleFlags()
            r0.addStyle(r6, r8, r10, r1)
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
            if (r1 != 0) goto L_0x0200
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
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
            r1.add(r6)
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
            if (r1 != 0) goto L_0x026f
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
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
            r1.add(r6)
            r7 = r4[r5]
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r7 = r7.getTextStyleRun()
            if (r7 == 0) goto L_0x02b2
            int r7 = r7.flags
            int r8 = r6.offset
            int r6 = r6.length
            int r6 = r6 + r8
            r0.addStyle(r7, r8, r6, r1)
        L_0x02b2:
            int r5 = r5 + 1
            goto L_0x0270
        L_0x02b5:
            r3 = r19[r2]
            if (r1 != 0) goto L_0x02be
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L_0x02be:
            java.util.regex.Pattern r4 = BOLD_PATTERN
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda125 r5 = org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda125.INSTANCE
            java.lang.CharSequence r3 = r0.parsePattern(r3, r4, r1, r5)
            java.util.regex.Pattern r4 = ITALIC_PATTERN
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda123 r5 = org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda123.INSTANCE
            java.lang.CharSequence r3 = r0.parsePattern(r3, r4, r1, r5)
            java.util.regex.Pattern r4 = SPOILER_PATTERN
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda122 r5 = org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda122.INSTANCE
            java.lang.CharSequence r3 = r0.parsePattern(r3, r4, r1, r5)
            if (r20 == 0) goto L_0x02e0
            java.util.regex.Pattern r4 = STRIKE_PATTERN
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda124 r5 = org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda124.INSTANCE
            java.lang.CharSequence r3 = r0.parsePattern(r3, r4, r1, r5)
        L_0x02e0:
            r19[r2] = r3
        L_0x02e2:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.getEntities(java.lang.CharSequence[], boolean):java.util.ArrayList");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$131(Void voidR) {
        return new TLRPC$TL_messageEntityBold();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$132(Void voidR) {
        return new TLRPC$TL_messageEntityItalic();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$133(Void voidR) {
        return new TLRPC$TL_messageEntitySpoiler();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ TLRPC$MessageEntity lambda$getEntities$134(Void voidR) {
        return new TLRPC$TL_messageEntityStrike();
    }

    private CharSequence parsePattern(CharSequence charSequence, Pattern pattern, List<TLRPC$MessageEntity> list, GenericProvider<Void, TLRPC$MessageEntity> genericProvider) {
        Matcher matcher = pattern.matcher(charSequence);
        int i = 0;
        while (matcher.find()) {
            String group = matcher.group(1);
            charSequence = charSequence.subSequence(0, matcher.start() - i) + group + charSequence.subSequence(matcher.end() - i, charSequence.length());
            TLRPC$MessageEntity provide = genericProvider.provide(null);
            provide.offset = matcher.start() - i;
            provide.length = group.length();
            list.add(provide);
            i += (matcher.end() - matcher.start()) - group.length();
        }
        return charSequence;
    }

    public void loadDraftsIfNeed() {
        if (!getUserConfig().draftsLoaded && !this.loadingDrafts) {
            this.loadingDrafts = true;
            getConnectionsManager().sendRequest(new TLRPC$TL_messages_getAllDrafts(), new MediaDataController$$ExternalSyntheticLambda129(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDraftsIfNeed$135() {
        this.loadingDrafts = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDraftsIfNeed$137(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda10(this));
            return;
        }
        getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda12(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDraftsIfNeed$136() {
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
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_saveDraft, MediaDataController$$ExternalSyntheticLambda163.INSTANCE);
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
                    getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda19(this, tLRPC$DraftMessage2.reply_to_msg_id, j, ChatObject.isChannel(tLRPC$Chat) ? tLRPC$Chat.id : 0, i));
                }
            }
            getNotificationCenter().postNotificationName(NotificationCenter.newDraftReceived, Long.valueOf(j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDraft$141(int i, long j, long j2, int i2) {
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
                getConnectionsManager().sendRequest(tLRPC$TL_channels_getMessages, new MediaDataController$$ExternalSyntheticLambda140(this, j, i2));
            } else {
                TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages = new TLRPC$TL_messages_getMessages();
                tLRPC$TL_messages_getMessages.id.add(Integer.valueOf(i));
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages, new MediaDataController$$ExternalSyntheticLambda139(this, j, i2));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDraft$139(long j, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            if (!tLRPC$messages_Messages.messages.isEmpty()) {
                saveDraftReplyMessage(j, i, tLRPC$messages_Messages.messages.get(0));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDraft$140(long j, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            if (!tLRPC$messages_Messages.messages.isEmpty()) {
                saveDraftReplyMessage(j, i, tLRPC$messages_Messages.messages.get(0));
            }
        }
    }

    private void saveDraftReplyMessage(long j, int i, TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message != null) {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda36(this, j, i, tLRPC$Message));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveDraftReplyMessage$142(long j, int i, TLRPC$Message tLRPC$Message) {
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
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda65(this, arrayList, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearBotKeyboard$143(ArrayList arrayList, long j) {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda29(this, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadBotKeyboard$145(long j) {
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
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda85(this, tLRPC$Message, j));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadBotKeyboard$144(TLRPC$Message tLRPC$Message, long j) {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda37(this, j, j2, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadBotInfo$147(long j, long j2, int i) {
        try {
            TLRPC$BotInfo loadBotInfoInternal = loadBotInfoInternal(j, j2);
            if (loadBotInfoInternal != null) {
                AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda81(this, loadBotInfoInternal, i));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadBotInfo$146(TLRPC$BotInfo tLRPC$BotInfo, int i) {
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
                    AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda43(this, j, tLRPC$Message));
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putBotKeyboard$148(long j, TLRPC$Message tLRPC$Message) {
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
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda82(this, tLRPC$BotInfo, j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putBotInfo$149(TLRPC$BotInfo tLRPC$BotInfo, long j) {
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
        getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda101(this, tLRPC$TL_updateBotCommands, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateBotInfo$150(TLRPC$TL_updateBotCommands tLRPC$TL_updateBotCommands, long j) {
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
        if (string == null || getReactionsMap().get(string) == null) {
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
        this.ringtoneDataStore.loadUserRingtones();
    }

    public boolean saveToRingtones(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return false;
        }
        if (this.ringtoneDataStore.contains(tLRPC$Document.id)) {
            return true;
        }
        if (tLRPC$Document.size > MessagesController.getInstance(this.currentAccount).ringtoneSizeMax) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLargeError", NUM, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", NUM, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax / 1024)));
            return false;
        }
        int i = 0;
        while (i < tLRPC$Document.attributes.size()) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (!(tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) || tLRPC$DocumentAttribute.duration <= MessagesController.getInstance(this.currentAccount).ringtoneDurationMax) {
                i++;
            } else {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLongError", NUM, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", NUM, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax)));
                return false;
            }
        }
        TLRPC$TL_account_saveRingtone tLRPC$TL_account_saveRingtone = new TLRPC$TL_account_saveRingtone();
        TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
        tLRPC$TL_account_saveRingtone.id = tLRPC$TL_inputDocument;
        tLRPC$TL_inputDocument.id = tLRPC$Document.id;
        tLRPC$TL_inputDocument.file_reference = tLRPC$Document.file_reference;
        tLRPC$TL_inputDocument.access_hash = tLRPC$Document.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_saveRingtone, new MediaDataController$$ExternalSyntheticLambda155(this, tLRPC$Document));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveToRingtones$152(TLRPC$Document tLRPC$Document, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda79(this, tLObject, tLRPC$Document));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveToRingtones$151(TLObject tLObject, TLRPC$Document tLRPC$Document) {
        if (tLObject == null) {
            return;
        }
        if (tLObject instanceof TLRPC$TL_account_savedRingtoneConverted) {
            this.ringtoneDataStore.addTone(((TLRPC$TL_account_savedRingtoneConverted) tLObject).document);
        } else {
            this.ringtoneDataStore.addTone(tLRPC$Document);
        }
    }

    public void fetchNewEmojiKeywords(String[] strArr) {
        if (strArr != null) {
            int i = 0;
            while (i < strArr.length) {
                String str = strArr[i];
                if (!TextUtils.isEmpty(str) && this.currentFetchingEmoji.get(str) == null) {
                    this.currentFetchingEmoji.put(str, Boolean.TRUE);
                    getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda55(this, str));
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
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$158(java.lang.String r10) {
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
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda52 r0 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda52
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
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda136 r3 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda136
            r3.<init>(r9, r5, r1, r10)
            r2.sendRequest(r0, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$fetchNewEmojiKeywords$158(java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$153(String str) {
        this.currentFetchingEmoji.remove(str);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$157(int i, String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_emojiKeywordsDifference tLRPC$TL_emojiKeywordsDifference = (TLRPC$TL_emojiKeywordsDifference) tLObject;
            if (i == -1 || tLRPC$TL_emojiKeywordsDifference.lang_code.equals(str)) {
                putEmojiKeywords(str2, tLRPC$TL_emojiKeywordsDifference);
            } else {
                getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda51(this, str2));
            }
        } else {
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda53(this, str2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$155(String str) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("DELETE FROM emoji_keywords_info_v2 WHERE lang = ?");
            executeFast.bindString(1, str);
            executeFast.step();
            executeFast.dispose();
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda56(this, str));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$154(String str) {
        this.currentFetchingEmoji.remove(str);
        fetchNewEmojiKeywords(new String[]{str});
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fetchNewEmojiKeywords$156(String str) {
        this.currentFetchingEmoji.remove(str);
    }

    private void putEmojiKeywords(String str, TLRPC$TL_emojiKeywordsDifference tLRPC$TL_emojiKeywordsDifference) {
        if (tLRPC$TL_emojiKeywordsDifference != null) {
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda93(this, tLRPC$TL_emojiKeywordsDifference, str));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putEmojiKeywords$160(TLRPC$TL_emojiKeywordsDifference tLRPC$TL_emojiKeywordsDifference, String str) {
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
            AndroidUtilities.runOnUIThread(new MediaDataController$$ExternalSyntheticLambda54(this, str));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putEmojiKeywords$159(String str) {
        this.currentFetchingEmoji.remove(str);
        getNotificationCenter().postNotificationName(NotificationCenter.newEmojiSuggestionsAvailable, str);
    }

    public void getEmojiSuggestions(String[] strArr, String str, boolean z, KeywordResultCallback keywordResultCallback) {
        getEmojiSuggestions(strArr, str, z, keywordResultCallback, (CountDownLatch) null);
    }

    public void getEmojiSuggestions(String[] strArr, String str, boolean z, KeywordResultCallback keywordResultCallback, CountDownLatch countDownLatch) {
        if (keywordResultCallback != null) {
            if (TextUtils.isEmpty(str) || strArr == null) {
                keywordResultCallback.run(new ArrayList(), (String) null);
                return;
            }
            getMessagesStorage().getStorageQueue().postRunnable(new MediaDataController$$ExternalSyntheticLambda112(this, strArr, keywordResultCallback, str, z, new ArrayList(Emoji.recentEmoji), countDownLatch));
            if (countDownLatch != null) {
                try {
                    countDownLatch.await();
                } catch (Throwable unused) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x011b  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0122  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$getEmojiSuggestions$164(java.lang.String[] r15, org.telegram.messenger.MediaDataController.KeywordResultCallback r16, java.lang.String r17, boolean r18, java.util.ArrayList r19, java.util.concurrent.CountDownLatch r20) {
        /*
            r14 = this;
            r0 = r15
            r1 = r16
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            r4 = 0
            r5 = 0
            r8 = r4
            r6 = 0
            r7 = 0
        L_0x0012:
            int r9 = r0.length     // Catch:{ Exception -> 0x010a }
            r10 = 1
            if (r6 >= r9) goto L_0x003d
            org.telegram.messenger.MessagesStorage r9 = r14.getMessagesStorage()     // Catch:{ Exception -> 0x010a }
            org.telegram.SQLite.SQLiteDatabase r9 = r9.getDatabase()     // Catch:{ Exception -> 0x010a }
            java.lang.String r11 = "SELECT alias FROM emoji_keywords_info_v2 WHERE lang = ?"
            java.lang.Object[] r12 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x010a }
            r13 = r0[r6]     // Catch:{ Exception -> 0x010a }
            r12[r5] = r13     // Catch:{ Exception -> 0x010a }
            org.telegram.SQLite.SQLiteCursor r9 = r9.queryFinalized(r11, r12)     // Catch:{ Exception -> 0x010a }
            boolean r11 = r9.next()     // Catch:{ Exception -> 0x010a }
            if (r11 == 0) goto L_0x0034
            java.lang.String r8 = r9.stringValue(r5)     // Catch:{ Exception -> 0x010a }
        L_0x0034:
            r9.dispose()     // Catch:{ Exception -> 0x010a }
            if (r8 == 0) goto L_0x003a
            r7 = 1
        L_0x003a:
            int r6 = r6 + 1
            goto L_0x0012
        L_0x003d:
            if (r7 != 0) goto L_0x0049
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda113 r3 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda113     // Catch:{ Exception -> 0x010a }
            r6 = r14
            r3.<init>(r14, r15, r1, r2)     // Catch:{ Exception -> 0x0108 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)     // Catch:{ Exception -> 0x0108 }
            return
        L_0x0049:
            r6 = r14
            java.lang.String r0 = r17.toLowerCase()     // Catch:{ Exception -> 0x0108 }
            r7 = 0
        L_0x004f:
            r9 = 2
            if (r7 >= r9) goto L_0x010f
            if (r7 != r10) goto L_0x0065
            org.telegram.messenger.LocaleController r11 = org.telegram.messenger.LocaleController.getInstance()     // Catch:{ Exception -> 0x0108 }
            java.lang.String r11 = r11.getTranslitString(r0, r5, r5)     // Catch:{ Exception -> 0x0108 }
            boolean r12 = r11.equals(r0)     // Catch:{ Exception -> 0x0108 }
            if (r12 == 0) goto L_0x0064
            goto L_0x0104
        L_0x0064:
            r0 = r11
        L_0x0065:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0108 }
            r11.<init>(r0)     // Catch:{ Exception -> 0x0108 }
            int r12 = r11.length()     // Catch:{ Exception -> 0x0108 }
        L_0x006e:
            if (r12 <= 0) goto L_0x0082
            int r12 = r12 + -1
            char r13 = r11.charAt(r12)     // Catch:{ Exception -> 0x0108 }
            int r13 = r13 + r10
            char r13 = (char) r13     // Catch:{ Exception -> 0x0108 }
            r11.setCharAt(r12, r13)     // Catch:{ Exception -> 0x0108 }
            if (r13 == 0) goto L_0x006e
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0108 }
            goto L_0x0083
        L_0x0082:
            r11 = r4
        L_0x0083:
            if (r18 == 0) goto L_0x0098
            org.telegram.messenger.MessagesStorage r9 = r14.getMessagesStorage()     // Catch:{ Exception -> 0x0108 }
            org.telegram.SQLite.SQLiteDatabase r9 = r9.getDatabase()     // Catch:{ Exception -> 0x0108 }
            java.lang.String r11 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword = ?"
            java.lang.Object[] r12 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x0108 }
            r12[r5] = r0     // Catch:{ Exception -> 0x0108 }
            org.telegram.SQLite.SQLiteCursor r9 = r9.queryFinalized(r11, r12)     // Catch:{ Exception -> 0x0108 }
            goto L_0x00d2
        L_0x0098:
            if (r11 == 0) goto L_0x00af
            org.telegram.messenger.MessagesStorage r12 = r14.getMessagesStorage()     // Catch:{ Exception -> 0x0108 }
            org.telegram.SQLite.SQLiteDatabase r12 = r12.getDatabase()     // Catch:{ Exception -> 0x0108 }
            java.lang.String r13 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword >= ? AND keyword < ?"
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x0108 }
            r9[r5] = r0     // Catch:{ Exception -> 0x0108 }
            r9[r10] = r11     // Catch:{ Exception -> 0x0108 }
            org.telegram.SQLite.SQLiteCursor r9 = r12.queryFinalized(r13, r9)     // Catch:{ Exception -> 0x0108 }
            goto L_0x00d2
        L_0x00af:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0108 }
            r9.<init>()     // Catch:{ Exception -> 0x0108 }
            r9.append(r0)     // Catch:{ Exception -> 0x0108 }
            java.lang.String r0 = "%"
            r9.append(r0)     // Catch:{ Exception -> 0x0108 }
            java.lang.String r0 = r9.toString()     // Catch:{ Exception -> 0x0108 }
            org.telegram.messenger.MessagesStorage r9 = r14.getMessagesStorage()     // Catch:{ Exception -> 0x0108 }
            org.telegram.SQLite.SQLiteDatabase r9 = r9.getDatabase()     // Catch:{ Exception -> 0x0108 }
            java.lang.String r11 = "SELECT emoji, keyword FROM emoji_keywords_v2 WHERE keyword LIKE ?"
            java.lang.Object[] r12 = new java.lang.Object[r10]     // Catch:{ Exception -> 0x0108 }
            r12[r5] = r0     // Catch:{ Exception -> 0x0108 }
            org.telegram.SQLite.SQLiteCursor r9 = r9.queryFinalized(r11, r12)     // Catch:{ Exception -> 0x0108 }
        L_0x00d2:
            boolean r11 = r9.next()     // Catch:{ Exception -> 0x0108 }
            if (r11 == 0) goto L_0x0101
            java.lang.String r11 = r9.stringValue(r5)     // Catch:{ Exception -> 0x0108 }
            java.lang.String r12 = "️"
            java.lang.String r13 = ""
            java.lang.String r11 = r11.replace(r12, r13)     // Catch:{ Exception -> 0x0108 }
            java.lang.Object r12 = r3.get(r11)     // Catch:{ Exception -> 0x0108 }
            if (r12 == 0) goto L_0x00eb
            goto L_0x00d2
        L_0x00eb:
            java.lang.Boolean r12 = java.lang.Boolean.TRUE     // Catch:{ Exception -> 0x0108 }
            r3.put(r11, r12)     // Catch:{ Exception -> 0x0108 }
            org.telegram.messenger.MediaDataController$KeywordResult r12 = new org.telegram.messenger.MediaDataController$KeywordResult     // Catch:{ Exception -> 0x0108 }
            r12.<init>()     // Catch:{ Exception -> 0x0108 }
            r12.emoji = r11     // Catch:{ Exception -> 0x0108 }
            java.lang.String r11 = r9.stringValue(r10)     // Catch:{ Exception -> 0x0108 }
            r12.keyword = r11     // Catch:{ Exception -> 0x0108 }
            r2.add(r12)     // Catch:{ Exception -> 0x0108 }
            goto L_0x00d2
        L_0x0101:
            r9.dispose()     // Catch:{ Exception -> 0x0108 }
        L_0x0104:
            int r7 = r7 + 1
            goto L_0x004f
        L_0x0108:
            r0 = move-exception
            goto L_0x010c
        L_0x010a:
            r0 = move-exception
            r6 = r14
        L_0x010c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x010f:
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda116 r0 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda116
            r3 = r19
            r0.<init>(r3)
            java.util.Collections.sort(r2, r0)
            if (r20 == 0) goto L_0x0122
            r1.run(r2, r8)
            r20.countDown()
            goto L_0x012a
        L_0x0122:
            org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda1 r0 = new org.telegram.messenger.MediaDataController$$ExternalSyntheticLambda1
            r0.<init>(r1, r2, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x012a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MediaDataController.lambda$getEmojiSuggestions$164(java.lang.String[], org.telegram.messenger.MediaDataController$KeywordResultCallback, java.lang.String, boolean, java.util.ArrayList, java.util.concurrent.CountDownLatch):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getEmojiSuggestions$161(String[] strArr, KeywordResultCallback keywordResultCallback, ArrayList arrayList) {
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
    public static /* synthetic */ int lambda$getEmojiSuggestions$162(ArrayList arrayList, KeywordResult keywordResult, KeywordResult keywordResult2) {
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
}
