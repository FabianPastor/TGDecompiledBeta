package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.RecyclerListView;

public class MentionsAdapter extends RecyclerListView.SelectionAdapter implements NotificationCenter.NotificationCenterDelegate {
    private static final String punctuationsChars = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n";
    private final boolean USE_DIVIDERS = false;
    private LongSparseArray<TLRPC.BotInfo> botInfo;
    private int botsCount;
    private Runnable cancelDelayRunnable;
    /* access modifiers changed from: private */
    public int channelLastReqId;
    /* access modifiers changed from: private */
    public int channelReqId;
    private boolean contextMedia;
    private int contextQueryReqid;
    /* access modifiers changed from: private */
    public Runnable contextQueryRunnable;
    /* access modifiers changed from: private */
    public int contextUsernameReqid;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private boolean delayLocalResults;
    /* access modifiers changed from: private */
    public MentionsAdapterDelegate delegate;
    private long dialog_id;
    /* access modifiers changed from: private */
    public TLRPC.User foundContextBot;
    private TLRPC.ChatFull info;
    private boolean inlineMediaEnabled = true;
    private boolean isDarkTheme;
    private boolean isReversed = false;
    /* access modifiers changed from: private */
    public boolean isSearchingMentions;
    private Object[] lastData;
    /* access modifiers changed from: private */
    public boolean lastForSearch;
    private int lastItemCount = -1;
    /* access modifiers changed from: private */
    public Location lastKnownLocation;
    /* access modifiers changed from: private */
    public int lastPosition;
    private int lastReqId;
    private String[] lastSearchKeyboardLanguage;
    private String lastSticker;
    /* access modifiers changed from: private */
    public String lastText;
    /* access modifiers changed from: private */
    public boolean lastUsernameOnly;
    private SendMessagesHelper.LocationProvider locationProvider = new SendMessagesHelper.LocationProvider(new SendMessagesHelper.LocationProvider.LocationProviderDelegate() {
        public void onLocationAcquired(Location location) {
            if (MentionsAdapter.this.foundContextBot != null && MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                Location unused = MentionsAdapter.this.lastKnownLocation = location;
                MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                mentionsAdapter.searchForContextBotResults(true, mentionsAdapter.foundContextBot, MentionsAdapter.this.searchingContextQuery, "");
            }
        }

        public void onUnableLocationAcquire() {
            MentionsAdapter.this.onLocationUnavailable();
        }
    }) {
        public void stop() {
            super.stop();
            Location unused = MentionsAdapter.this.lastKnownLocation = null;
        }
    };
    private Context mContext;
    private EmojiView.ChooseStickerActionTracker mentionsStickersActionTracker;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> messages;
    private boolean needBotContext = true;
    private boolean needUsernames = true;
    private String nextQueryOffset;
    /* access modifiers changed from: private */
    public boolean noUserName;
    private ChatActivity parentFragment;
    private final Theme.ResourcesProvider resourcesProvider;
    private int resultLength;
    private int resultStartPosition;
    private SearchAdapterHelper searchAdapterHelper;
    /* access modifiers changed from: private */
    public Runnable searchGlobalRunnable;
    private ArrayList<TLRPC.BotInlineResult> searchResultBotContext;
    private TLRPC.TL_inlineBotSwitchPM searchResultBotContextSwitch;
    private ArrayList<String> searchResultCommands;
    private ArrayList<String> searchResultCommandsHelp;
    private ArrayList<TLRPC.User> searchResultCommandsUsers;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<MediaDataController.KeywordResult> searchResultSuggestions;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> searchResultUsernames;
    /* access modifiers changed from: private */
    public LongSparseArray<TLObject> searchResultUsernamesMap;
    /* access modifiers changed from: private */
    public String searchingContextQuery;
    /* access modifiers changed from: private */
    public String searchingContextUsername;
    private ArrayList<StickerResult> stickers;
    private HashMap<String, TLRPC.Document> stickersMap;
    private ArrayList<String> stickersToLoad = new ArrayList<>();
    private int threadMessageId;
    private boolean visibleByStickersSearch;

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(TLRPC.BotInlineResult botInlineResult);

        void onContextSearch(boolean z);

        void onItemCountUpdate(int i, int i2);
    }

    static /* synthetic */ int access$1704(MentionsAdapter x0) {
        int i = x0.channelLastReqId + 1;
        x0.channelLastReqId = i;
        return i;
    }

    private static class StickerResult {
        public Object parent;
        public TLRPC.Document sticker;

        public StickerResult(TLRPC.Document s, Object p) {
            this.sticker = s;
            this.parent = p;
        }
    }

    public MentionsAdapter(Context context, boolean darkTheme, long did, int threadMessageId2, MentionsAdapterDelegate mentionsAdapterDelegate, Theme.ResourcesProvider resourcesProvider2) {
        this.resourcesProvider = resourcesProvider2;
        this.mContext = context;
        this.delegate = mentionsAdapterDelegate;
        this.isDarkTheme = darkTheme;
        this.dialog_id = did;
        this.threadMessageId = threadMessageId2;
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ boolean canApplySearchResults(int i) {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
            }

            public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
            }

            public /* synthetic */ LongSparseArray getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged(int searchId) {
                MentionsAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                if (MentionsAdapter.this.lastText != null) {
                    MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                    mentionsAdapter.searchUsernameOrHashtag(mentionsAdapter.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages, MentionsAdapter.this.lastUsernameOnly, MentionsAdapter.this.lastForSearch);
                }
            }
        });
        if (!darkTheme) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ArrayList<StickerResult> arrayList;
        if ((id == NotificationCenter.fileLoaded || id == NotificationCenter.fileLoadFailed) && (arrayList = this.stickers) != null && !arrayList.isEmpty() && !this.stickersToLoad.isEmpty() && this.visibleByStickersSearch) {
            boolean z = false;
            this.stickersToLoad.remove(args[0]);
            if (this.stickersToLoad.isEmpty()) {
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (getItemCountInternal() > 0) {
                    z = true;
                }
                mentionsAdapterDelegate.needChangePanelVisibility(z);
            }
        }
    }

    private void addStickerToResult(TLRPC.Document document, Object parent) {
        if (document != null) {
            String key = document.dc_id + "_" + document.id;
            HashMap<String, TLRPC.Document> hashMap = this.stickersMap;
            if (hashMap != null && hashMap.containsKey(key)) {
                return;
            }
            if (UserConfig.getInstance(this.currentAccount).isPremium() || !MessageObject.isPremiumSticker(document)) {
                if (this.stickers == null) {
                    this.stickers = new ArrayList<>();
                    this.stickersMap = new HashMap<>();
                }
                this.stickers.add(new StickerResult(document, parent));
                this.stickersMap.put(key, document);
                EmojiView.ChooseStickerActionTracker chooseStickerActionTracker = this.mentionsStickersActionTracker;
                if (chooseStickerActionTracker != null) {
                    chooseStickerActionTracker.checkVisibility();
                }
            }
        }
    }

    private void addStickersToResult(ArrayList<TLRPC.Document> documents, Object parent) {
        if (documents != null && !documents.isEmpty()) {
            int size = documents.size();
            for (int a = 0; a < size; a++) {
                TLRPC.Document document = documents.get(a);
                String key = document.dc_id + "_" + document.id;
                HashMap<String, TLRPC.Document> hashMap = this.stickersMap;
                if ((hashMap == null || !hashMap.containsKey(key)) && (UserConfig.getInstance(this.currentAccount).isPremium() || !MessageObject.isPremiumSticker(document))) {
                    int b = 0;
                    int size2 = document.attributes.size();
                    while (true) {
                        if (b >= size2) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute = document.attributes.get(b);
                        if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                            parent = attribute.stickerset;
                            break;
                        }
                        b++;
                    }
                    if (this.stickers == null) {
                        this.stickers = new ArrayList<>();
                        this.stickersMap = new HashMap<>();
                    }
                    this.stickers.add(new StickerResult(document, parent));
                    this.stickersMap.put(key, document);
                }
            }
        }
    }

    private boolean checkStickerFilesExistAndDownload() {
        if (this.stickers == null) {
            return false;
        }
        this.stickersToLoad.clear();
        int size = Math.min(6, this.stickers.size());
        for (int a = 0; a < size; a++) {
            StickerResult result = this.stickers.get(a);
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(result.sticker.thumbs, 90);
            if (((thumb instanceof TLRPC.TL_photoSize) || (thumb instanceof TLRPC.TL_photoSizeProgressive)) && !FileLoader.getInstance(this.currentAccount).getPathToAttach(thumb, "webp", true).exists()) {
                this.stickersToLoad.add(FileLoader.getAttachFileName(thumb, "webp"));
                FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForDocument(thumb, result.sticker), result.parent, "webp", 1, 1);
            }
        }
        return this.stickersToLoad.isEmpty();
    }

    private boolean isValidSticker(TLRPC.Document document, String emoji) {
        int b = 0;
        int size2 = document.attributes.size();
        while (b < size2) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(b);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                b++;
            } else if (attribute.alt == null || !attribute.alt.contains(emoji)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void searchServerStickers(String emoji, String originalEmoji) {
        TLRPC.TL_messages_getStickers req = new TLRPC.TL_messages_getStickers();
        req.emoticon = originalEmoji;
        req.hash = 0;
        this.lastReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MentionsAdapter$$ExternalSyntheticLambda7(this, emoji));
    }

    /* renamed from: lambda$searchServerStickers$1$org-telegram-ui-Adapters-MentionsAdapter  reason: not valid java name */
    public /* synthetic */ void m2634x5b1741cc(String emoji, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MentionsAdapter$$ExternalSyntheticLambda3(this, emoji, response));
    }

    /* renamed from: lambda$searchServerStickers$0$org-telegram-ui-Adapters-MentionsAdapter  reason: not valid java name */
    public /* synthetic */ void m2633x53b20cad(String emoji, TLObject response) {
        ArrayList<StickerResult> arrayList;
        boolean z = false;
        this.lastReqId = 0;
        if (emoji.equals(this.lastSticker) && (response instanceof TLRPC.TL_messages_stickers)) {
            this.delayLocalResults = false;
            TLRPC.TL_messages_stickers res = (TLRPC.TL_messages_stickers) response;
            ArrayList<StickerResult> arrayList2 = this.stickers;
            int oldCount = arrayList2 != null ? arrayList2.size() : 0;
            ArrayList<TLRPC.Document> arrayList3 = res.stickers;
            addStickersToResult(arrayList3, "sticker_search_" + emoji);
            ArrayList<StickerResult> arrayList4 = this.stickers;
            int newCount = arrayList4 != null ? arrayList4.size() : 0;
            if (!this.visibleByStickersSearch && (arrayList = this.stickers) != null && !arrayList.isEmpty()) {
                checkStickerFilesExistAndDownload();
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (getItemCountInternal() > 0) {
                    z = true;
                }
                mentionsAdapterDelegate.needChangePanelVisibility(z);
                this.visibleByStickersSearch = true;
            }
            if (oldCount != newCount) {
                notifyDataSetChanged();
            }
        }
    }

    public void notifyDataSetChanged() {
        MentionsAdapterDelegate mentionsAdapterDelegate;
        boolean hadChanges = false;
        if (this.lastItemCount == -1 || this.lastData == null) {
            MentionsAdapterDelegate mentionsAdapterDelegate2 = this.delegate;
            if (mentionsAdapterDelegate2 != null) {
                mentionsAdapterDelegate2.onItemCountUpdate(0, getItemCount());
            }
            super.notifyDataSetChanged();
            this.lastData = new Object[getItemCount()];
            int i = 0;
            while (true) {
                Object[] objArr = this.lastData;
                if (i < objArr.length) {
                    objArr[i] = getItem(i);
                    i++;
                } else {
                    return;
                }
            }
        } else {
            int oldCount = this.lastItemCount;
            int newCount = getItemCount();
            if (oldCount != newCount) {
                hadChanges = true;
            }
            int min = Math.min(oldCount, newCount);
            Object[] newData = new Object[newCount];
            for (int i2 = 0; i2 < newCount; i2++) {
                newData[i2] = getItem(i2);
            }
            for (int i3 = 0; i3 < min; i3++) {
                if (i3 >= 0) {
                    Object[] objArr2 = this.lastData;
                    if (i3 < objArr2.length && i3 < newData.length && itemsEqual(objArr2[i3], newData[i3])) {
                    }
                }
                notifyItemChanged(i3);
                hadChanges = true;
            }
            notifyItemRangeRemoved(min, oldCount - min);
            notifyItemRangeInserted(min, newCount - min);
            if (hadChanges && (mentionsAdapterDelegate = this.delegate) != null) {
                mentionsAdapterDelegate.onItemCountUpdate(oldCount, newCount);
            }
            this.lastData = newData;
        }
    }

    private boolean itemsEqual(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if ((a instanceof StickerResult) && (b instanceof StickerResult) && ((StickerResult) a).sticker == ((StickerResult) b).sticker) {
            return true;
        }
        if ((a instanceof TLRPC.User) && (b instanceof TLRPC.User) && ((TLRPC.User) a).id == ((TLRPC.User) b).id) {
            return true;
        }
        if ((a instanceof TLRPC.Chat) && (b instanceof TLRPC.Chat) && ((TLRPC.Chat) a).id == ((TLRPC.Chat) b).id) {
            return true;
        }
        if ((a instanceof String) && (b instanceof String) && a.equals(b)) {
            return true;
        }
        if (!(a instanceof MediaDataController.KeywordResult) || !(b instanceof MediaDataController.KeywordResult) || ((MediaDataController.KeywordResult) a).keyword == null || !((MediaDataController.KeywordResult) a).keyword.equals(((MediaDataController.KeywordResult) b).keyword) || ((MediaDataController.KeywordResult) a).emoji == null || !((MediaDataController.KeywordResult) a).emoji.equals(((MediaDataController.KeywordResult) b).emoji)) {
            return false;
        }
        return true;
    }

    private void clearStickers() {
        this.lastSticker = null;
        this.stickers = null;
        this.stickersMap = null;
        notifyDataSetChanged();
        if (this.lastReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
            this.lastReqId = 0;
        }
        EmojiView.ChooseStickerActionTracker chooseStickerActionTracker = this.mentionsStickersActionTracker;
        if (chooseStickerActionTracker != null) {
            chooseStickerActionTracker.checkVisibility();
        }
    }

    public void onDestroy() {
        SendMessagesHelper.LocationProvider locationProvider2 = this.locationProvider;
        if (locationProvider2 != null) {
            locationProvider2.stop();
        }
        Runnable runnable = this.contextQueryRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.contextQueryRunnable = null;
        }
        if (this.contextUsernameReqid != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextUsernameReqid, true);
            this.contextUsernameReqid = 0;
        }
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        this.foundContextBot = null;
        this.inlineMediaEnabled = true;
        this.searchingContextUsername = null;
        this.searchingContextQuery = null;
        this.noUserName = false;
        if (!this.isDarkTheme) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
        }
    }

    public void setParentFragment(ChatActivity fragment) {
        this.parentFragment = fragment;
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        ChatActivity chatActivity;
        TLRPC.Chat chat;
        this.currentAccount = UserConfig.selectedAccount;
        this.info = chatInfo;
        if (!(this.inlineMediaEnabled || this.foundContextBot == null || (chatActivity = this.parentFragment) == null || (chat = chatActivity.getCurrentChat()) == null)) {
            boolean canSendStickers = ChatObject.canSendStickers(chat);
            this.inlineMediaEnabled = canSendStickers;
            if (canSendStickers) {
                this.searchResultUsernames = null;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(false);
                processFoundUser(this.foundContextBot);
            }
        }
        String str = this.lastText;
        if (str != null) {
            searchUsernameOrHashtag(str, this.lastPosition, this.messages, this.lastUsernameOnly, this.lastForSearch);
        }
    }

    public void setNeedUsernames(boolean value) {
        this.needUsernames = value;
    }

    public void setNeedBotContext(boolean value) {
        this.needBotContext = value;
    }

    public void setBotInfo(LongSparseArray<TLRPC.BotInfo> info2) {
        this.botInfo = info2;
    }

    public void setBotsCount(int count) {
        this.botsCount = count;
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        if (mentionsAdapterDelegate != null) {
            mentionsAdapterDelegate.needChangePanelVisibility(false);
        }
    }

    public TLRPC.TL_inlineBotSwitchPM getBotContextSwitch() {
        return this.searchResultBotContextSwitch;
    }

    public long getContextBotId() {
        TLRPC.User user = this.foundContextBot;
        if (user != null) {
            return user.id;
        }
        return 0;
    }

    public TLRPC.User getContextBotUser() {
        return this.foundContextBot;
    }

    public String getContextBotName() {
        TLRPC.User user = this.foundContextBot;
        return user != null ? user.username : "";
    }

    /* access modifiers changed from: private */
    public void processFoundUser(TLRPC.User user) {
        ChatActivity chatActivity;
        TLRPC.Chat chat;
        this.contextUsernameReqid = 0;
        this.locationProvider.stop();
        if (user == null || !user.bot || user.bot_inline_placeholder == null) {
            this.foundContextBot = null;
            this.inlineMediaEnabled = true;
        } else {
            this.foundContextBot = user;
            ChatActivity chatActivity2 = this.parentFragment;
            if (!(chatActivity2 == null || (chat = chatActivity2.getCurrentChat()) == null)) {
                boolean canSendStickers = ChatObject.canSendStickers(chat);
                this.inlineMediaEnabled = canSendStickers;
                if (!canSendStickers) {
                    notifyDataSetChanged();
                    this.delegate.needChangePanelVisibility(true);
                    return;
                }
            }
            if (this.foundContextBot.bot_inline_geo) {
                SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                if (preferences.getBoolean("inlinegeo_" + this.foundContextBot.id, false) || (chatActivity = this.parentFragment) == null || chatActivity.getParentActivity() == null) {
                    checkLocationPermissionsOrStart();
                } else {
                    TLRPC.User foundContextBotFinal = this.foundContextBot;
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentFragment.getParentActivity());
                    builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                    builder.setMessage(LocaleController.getString("ShareYouLocationInline", NUM));
                    boolean[] buttonClicked = new boolean[1];
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new MentionsAdapter$$ExternalSyntheticLambda1(this, buttonClicked, foundContextBotFinal));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new MentionsAdapter$$ExternalSyntheticLambda0(this, buttonClicked));
                    this.parentFragment.showDialog(builder.create(), new MentionsAdapter$$ExternalSyntheticLambda2(this, buttonClicked));
                }
            }
        }
        if (this.foundContextBot == null) {
            this.noUserName = true;
            return;
        }
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        if (mentionsAdapterDelegate != null) {
            mentionsAdapterDelegate.onContextSearch(true);
        }
        searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
    }

    /* renamed from: lambda$processFoundUser$2$org-telegram-ui-Adapters-MentionsAdapter  reason: not valid java name */
    public /* synthetic */ void m2628xde31628e(boolean[] buttonClicked, TLRPC.User foundContextBotFinal, DialogInterface dialogInterface, int i) {
        buttonClicked[0] = true;
        if (foundContextBotFinal != null) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putBoolean("inlinegeo_" + foundContextBotFinal.id, true).commit();
            checkLocationPermissionsOrStart();
        }
    }

    /* renamed from: lambda$processFoundUser$3$org-telegram-ui-Adapters-MentionsAdapter  reason: not valid java name */
    public /* synthetic */ void m2629xe59697ad(boolean[] buttonClicked, DialogInterface dialog, int which) {
        buttonClicked[0] = true;
        onLocationUnavailable();
    }

    /* renamed from: lambda$processFoundUser$4$org-telegram-ui-Adapters-MentionsAdapter  reason: not valid java name */
    public /* synthetic */ void m2630xecfbcccc(boolean[] buttonClicked, DialogInterface dialog) {
        if (!buttonClicked[0]) {
            onLocationUnavailable();
        }
    }

    private void searchForContextBot(String username, String query) {
        String str;
        String str2;
        TLRPC.User user = this.foundContextBot;
        if (user == null || user.username == null || !this.foundContextBot.username.equals(username) || (str2 = this.searchingContextQuery) == null || !str2.equals(query)) {
            if (this.foundContextBot != null) {
                if (this.inlineMediaEnabled || username == null || query == null) {
                    this.delegate.needChangePanelVisibility(false);
                } else {
                    return;
                }
            }
            Runnable runnable = this.contextQueryRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.contextQueryRunnable = null;
            }
            if (TextUtils.isEmpty(username) || ((str = this.searchingContextUsername) != null && !str.equals(username))) {
                if (this.contextUsernameReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextUsernameReqid, true);
                    this.contextUsernameReqid = 0;
                }
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.foundContextBot = null;
                this.inlineMediaEnabled = true;
                this.searchingContextUsername = null;
                this.searchingContextQuery = null;
                this.locationProvider.stop();
                this.noUserName = false;
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (mentionsAdapterDelegate != null) {
                    mentionsAdapterDelegate.onContextSearch(false);
                }
                if (username == null || username.length() == 0) {
                    return;
                }
            }
            if (query == null) {
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.searchingContextQuery = null;
                MentionsAdapterDelegate mentionsAdapterDelegate2 = this.delegate;
                if (mentionsAdapterDelegate2 != null) {
                    mentionsAdapterDelegate2.onContextSearch(false);
                    return;
                }
                return;
            }
            MentionsAdapterDelegate mentionsAdapterDelegate3 = this.delegate;
            if (mentionsAdapterDelegate3 != null) {
                if (this.foundContextBot != null) {
                    mentionsAdapterDelegate3.onContextSearch(true);
                } else if (username.equals("gif")) {
                    this.searchingContextUsername = "gif";
                    this.delegate.onContextSearch(false);
                }
            }
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            this.searchingContextQuery = query;
            final String str3 = query;
            final String str4 = username;
            final MessagesController messagesController2 = messagesController;
            final MessagesStorage messagesStorage2 = messagesStorage;
            AnonymousClass4 r1 = new Runnable() {
                public void run() {
                    if (MentionsAdapter.this.contextQueryRunnable == this) {
                        Runnable unused = MentionsAdapter.this.contextQueryRunnable = null;
                        if (MentionsAdapter.this.foundContextBot == null && !MentionsAdapter.this.noUserName) {
                            String unused2 = MentionsAdapter.this.searchingContextUsername = str4;
                            TLObject object = messagesController2.getUserOrChat(MentionsAdapter.this.searchingContextUsername);
                            if (object instanceof TLRPC.User) {
                                MentionsAdapter.this.processFoundUser((TLRPC.User) object);
                                return;
                            }
                            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                            req.username = MentionsAdapter.this.searchingContextUsername;
                            MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                            int unused3 = mentionsAdapter.contextUsernameReqid = ConnectionsManager.getInstance(mentionsAdapter.currentAccount).sendRequest(req, new MentionsAdapter$4$$ExternalSyntheticLambda1(this, str4, messagesController2, messagesStorage2));
                        } else if (!MentionsAdapter.this.noUserName) {
                            MentionsAdapter mentionsAdapter2 = MentionsAdapter.this;
                            mentionsAdapter2.searchForContextBotResults(true, mentionsAdapter2.foundContextBot, str3, "");
                        }
                    }
                }

                /* renamed from: lambda$run$1$org-telegram-ui-Adapters-MentionsAdapter$4  reason: not valid java name */
                public /* synthetic */ void m2638lambda$run$1$orgtelegramuiAdaptersMentionsAdapter$4(String username, MessagesController messagesController, MessagesStorage messagesStorage, TLObject response, TLRPC.TL_error error) {
                    AndroidUtilities.runOnUIThread(new MentionsAdapter$4$$ExternalSyntheticLambda0(this, username, error, response, messagesController, messagesStorage));
                }

                /* renamed from: lambda$run$0$org-telegram-ui-Adapters-MentionsAdapter$4  reason: not valid java name */
                public /* synthetic */ void m2637lambda$run$0$orgtelegramuiAdaptersMentionsAdapter$4(String username, TLRPC.TL_error error, TLObject response, MessagesController messagesController, MessagesStorage messagesStorage) {
                    if (MentionsAdapter.this.searchingContextUsername != null && MentionsAdapter.this.searchingContextUsername.equals(username)) {
                        TLRPC.User user = null;
                        if (error == null) {
                            TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
                            if (!res.users.isEmpty()) {
                                user = res.users.get(0);
                                messagesController.putUser(user, false);
                                messagesStorage.putUsersAndChats(res.users, (ArrayList<TLRPC.Chat>) null, true, true);
                            }
                        }
                        MentionsAdapter.this.processFoundUser(user);
                        int unused = MentionsAdapter.this.contextUsernameReqid = 0;
                    }
                }
            };
            this.contextQueryRunnable = r1;
            AndroidUtilities.runOnUIThread(r1, 400);
        }
    }

    /* access modifiers changed from: private */
    public void onLocationUnavailable() {
        TLRPC.User user = this.foundContextBot;
        if (user != null && user.bot_inline_geo) {
            Location location = new Location("network");
            this.lastKnownLocation = location;
            location.setLatitude(-1000.0d);
            this.lastKnownLocation.setLongitude(-1000.0d);
            searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
        }
    }

    private void checkLocationPermissionsOrStart() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && chatActivity.getParentActivity() != null) {
            if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                TLRPC.User user = this.foundContextBot;
                if (user != null && user.bot_inline_geo) {
                    this.locationProvider.start();
                    return;
                }
                return;
            }
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
        }
    }

    public void setSearchingMentions(boolean value) {
        this.isSearchingMentions = value;
    }

    public String getBotCaption() {
        TLRPC.User user = this.foundContextBot;
        if (user != null) {
            return user.bot_inline_placeholder;
        }
        String str = this.searchingContextUsername;
        if (str == null || !str.equals("gif")) {
            return null;
        }
        return "Search GIFs";
    }

    public void searchForContextBotForNextOffset() {
        String str;
        TLRPC.User user;
        String str2;
        if (this.contextQueryReqid == 0 && (str = this.nextQueryOffset) != null && str.length() != 0 && (user = this.foundContextBot) != null && (str2 = this.searchingContextQuery) != null) {
            searchForContextBotResults(true, user, str2, this.nextQueryOffset);
        }
    }

    /* access modifiers changed from: private */
    public void searchForContextBotResults(boolean cache, TLRPC.User user, String query, String offset) {
        Location location;
        TLRPC.User user2 = user;
        String str = query;
        String str2 = offset;
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        if (!this.inlineMediaEnabled) {
            MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
            if (mentionsAdapterDelegate != null) {
                mentionsAdapterDelegate.onContextSearch(false);
            }
        } else if (str == null || user2 == null) {
            this.searchingContextQuery = null;
        } else if (!user2.bot_inline_geo || this.lastKnownLocation != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.dialog_id);
            sb.append("_");
            sb.append(str);
            sb.append("_");
            sb.append(str2);
            sb.append("_");
            sb.append(this.dialog_id);
            sb.append("_");
            sb.append(user2.id);
            sb.append("_");
            sb.append((!user2.bot_inline_geo || this.lastKnownLocation.getLatitude() == -1000.0d) ? "" : Double.valueOf(this.lastKnownLocation.getLatitude() + this.lastKnownLocation.getLongitude()));
            String key = sb.toString();
            MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            MessagesStorage messagesStorage2 = messagesStorage;
            RequestDelegate requestDelegate = new MentionsAdapter$$ExternalSyntheticLambda8(this, query, cache, user, offset, messagesStorage, key);
            if (cache) {
                messagesStorage2.getBotCache(key, requestDelegate);
                return;
            }
            TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
            req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user2);
            req.query = str;
            req.offset = str2;
            if (!(!user2.bot_inline_geo || (location = this.lastKnownLocation) == null || location.getLatitude() == -1000.0d)) {
                req.flags |= 1;
                req.geo_point = new TLRPC.TL_inputGeoPoint();
                req.geo_point.lat = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLatitude());
                req.geo_point._long = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLongitude());
            }
            if (DialogObject.isEncryptedDialog(this.dialog_id)) {
                req.peer = new TLRPC.TL_inputPeerEmpty();
            } else {
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialog_id);
            }
            this.contextQueryReqid = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 2);
        }
    }

    /* renamed from: lambda$searchForContextBotResults$6$org-telegram-ui-Adapters-MentionsAdapter  reason: not valid java name */
    public /* synthetic */ void m2632xcd6fb36b(String query, boolean cache, TLRPC.User user, String offset, MessagesStorage messagesStorage, String key, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MentionsAdapter$$ExternalSyntheticLambda4(this, query, cache, response, user, offset, messagesStorage, key));
    }

    /* renamed from: lambda$searchForContextBotResults$5$org-telegram-ui-Adapters-MentionsAdapter  reason: not valid java name */
    public /* synthetic */ void m2631xCLASSNAMEa7e4c(String query, boolean cache, TLObject response, TLRPC.User user, String offset, MessagesStorage messagesStorage, String key) {
        if (query.equals(this.searchingContextQuery)) {
            boolean z = false;
            this.contextQueryReqid = 0;
            if (!cache || response != null) {
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (mentionsAdapterDelegate != null) {
                    mentionsAdapterDelegate.onContextSearch(false);
                }
            } else {
                searchForContextBotResults(false, user, query, offset);
            }
            if (response instanceof TLRPC.TL_messages_botResults) {
                TLRPC.TL_messages_botResults res = (TLRPC.TL_messages_botResults) response;
                if (!cache && res.cache_time != 0) {
                    messagesStorage.saveBotCache(key, res);
                }
                this.nextQueryOffset = res.next_offset;
                if (this.searchResultBotContextSwitch == null) {
                    this.searchResultBotContextSwitch = res.switch_pm;
                }
                int a = 0;
                while (a < res.results.size()) {
                    TLRPC.BotInlineResult result = (TLRPC.BotInlineResult) res.results.get(a);
                    if (!(result.document instanceof TLRPC.TL_document) && !(result.photo instanceof TLRPC.TL_photo) && !"game".equals(result.type) && result.content == null && (result.send_message instanceof TLRPC.TL_botInlineMessageMediaAuto)) {
                        res.results.remove(a);
                        a--;
                    }
                    result.query_id = res.query_id;
                    a++;
                }
                boolean added = false;
                if (this.searchResultBotContext == null || offset.length() == 0) {
                    this.searchResultBotContext = res.results;
                    this.contextMedia = res.gallery;
                } else {
                    added = true;
                    this.searchResultBotContext.addAll(res.results);
                    if (res.results.isEmpty()) {
                        this.nextQueryOffset = "";
                    }
                }
                Runnable runnable = this.cancelDelayRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.cancelDelayRunnable = null;
                }
                this.searchResultHashtags = null;
                this.stickers = null;
                this.searchResultUsernames = null;
                this.searchResultUsernamesMap = null;
                this.searchResultCommands = null;
                this.searchResultSuggestions = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                if (added) {
                    boolean hasTop = this.searchResultBotContextSwitch != null;
                    notifyItemChanged(((this.searchResultBotContext.size() - res.results.size()) + (hasTop ? 1 : 0)) - 1);
                    notifyItemRangeInserted((this.searchResultBotContext.size() - res.results.size()) + (hasTop ? 1 : 0), res.results.size());
                } else {
                    notifyDataSetChanged();
                }
                MentionsAdapterDelegate mentionsAdapterDelegate2 = this.delegate;
                if (!this.searchResultBotContext.isEmpty() || this.searchResultBotContextSwitch != null) {
                    z = true;
                }
                mentionsAdapterDelegate2.needChangePanelVisibility(z);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v10, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0383, code lost:
        if (r8.info != null) goto L_0x0394;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x0385, code lost:
        if (r2 == 0) goto L_0x0394;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x0387, code lost:
        r8.lastText = r9;
        r8.lastPosition = r10;
        r8.messages = r11;
        r8.delegate.needChangePanelVisibility(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x0393, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0394, code lost:
        r8.resultStartPosition = r2;
        r8.resultLength = r15.length() + 1;
        r5 = 0;
        r21 = r2;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchUsernameOrHashtag(java.lang.String r34, int r35, java.util.ArrayList<org.telegram.messenger.MessageObject> r36, boolean r37, boolean r38) {
        /*
            r33 = this;
            r8 = r33
            r9 = r34
            r10 = r35
            r11 = r36
            r12 = r37
            r13 = r38
            java.lang.Runnable r0 = r8.cancelDelayRunnable
            r1 = 0
            if (r0 == 0) goto L_0x0016
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r8.cancelDelayRunnable = r1
        L_0x0016:
            int r0 = r8.channelReqId
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L_0x0029
            int r0 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            int r4 = r8.channelReqId
            r0.cancelRequest(r4, r3)
            r8.channelReqId = r2
        L_0x0029:
            java.lang.Runnable r0 = r8.searchGlobalRunnable
            if (r0 == 0) goto L_0x0032
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r8.searchGlobalRunnable = r1
        L_0x0032:
            boolean r0 = android.text.TextUtils.isEmpty(r34)
            if (r0 != 0) goto L_0x083e
            int r0 = r34.length()
            int r4 = r8.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r4 = r4.maxMessageLength
            if (r0 <= r4) goto L_0x0049
            r0 = r1
            goto L_0x083f
        L_0x0049:
            r0 = r35
            int r4 = r34.length()
            if (r4 <= 0) goto L_0x0055
            int r0 = r0 + -1
            r14 = r0
            goto L_0x0056
        L_0x0055:
            r14 = r0
        L_0x0056:
            r8.lastText = r1
            r8.lastUsernameOnly = r12
            r8.lastForSearch = r13
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r15 = r0
            r0 = -1
            if (r12 != 0) goto L_0x0077
            if (r9 == 0) goto L_0x0077
            int r4 = r34.length()
            if (r4 <= 0) goto L_0x0077
            int r4 = r34.length()
            r5 = 14
            if (r4 > r5) goto L_0x0077
            r4 = 1
            goto L_0x0078
        L_0x0077:
            r4 = 0
        L_0x0078:
            r16 = r4
            java.lang.String r4 = ""
            if (r16 == 0) goto L_0x0112
            r4 = r34
            r6 = r34
            int r7 = r6.length()
            r17 = 0
            r1 = r17
        L_0x008a:
            if (r1 >= r7) goto L_0x0104
            char r3 = r6.charAt(r1)
            int r2 = r7 + -1
            if (r1 >= r2) goto L_0x009b
            int r2 = r1 + 1
            char r2 = r6.charAt(r2)
            goto L_0x009c
        L_0x009b:
            r2 = 0
        L_0x009c:
            int r5 = r7 + -1
            if (r1 >= r5) goto L_0x00d4
            r5 = 55356(0xd83c, float:7.757E-41)
            if (r3 != r5) goto L_0x00d4
            r5 = 57339(0xdffb, float:8.0349E-41)
            if (r2 < r5) goto L_0x00d4
            r5 = 57343(0xdfff, float:8.0355E-41)
            if (r2 > r5) goto L_0x00d4
            r21 = r0
            r5 = 2
            java.lang.CharSequence[] r0 = new java.lang.CharSequence[r5]
            r5 = 0
            java.lang.CharSequence r19 = r6.subSequence(r5, r1)
            r0[r5] = r19
            int r5 = r1 + 2
            r22 = r2
            int r2 = r6.length()
            java.lang.CharSequence r2 = r6.subSequence(r5, r2)
            r5 = 1
            r0[r5] = r2
            java.lang.CharSequence r0 = android.text.TextUtils.concat(r0)
            int r7 = r7 + -2
            int r1 = r1 + -1
            r6 = r0
            goto L_0x00fd
        L_0x00d4:
            r21 = r0
            r22 = r2
            r0 = 65039(0xfe0f, float:9.1139E-41)
            if (r3 != r0) goto L_0x00fd
            r0 = 2
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r0]
            r0 = 0
            java.lang.CharSequence r5 = r6.subSequence(r0, r1)
            r2[r0] = r5
            int r0 = r1 + 1
            int r5 = r6.length()
            java.lang.CharSequence r0 = r6.subSequence(r0, r5)
            r5 = 1
            r2[r5] = r0
            java.lang.CharSequence r0 = android.text.TextUtils.concat(r2)
            int r7 = r7 + -1
            int r1 = r1 + -1
            r6 = r0
        L_0x00fd:
            r0 = 1
            int r1 = r1 + r0
            r0 = r21
            r2 = 0
            r3 = 1
            goto L_0x008a
        L_0x0104:
            r21 = r0
            java.lang.String r0 = r6.toString()
            java.lang.String r0 = r0.trim()
            r8.lastSticker = r0
            r7 = r4
            goto L_0x0115
        L_0x0112:
            r21 = r0
            r7 = r4
        L_0x0115:
            if (r16 == 0) goto L_0x0127
            boolean r0 = org.telegram.messenger.Emoji.isValidEmoji(r7)
            if (r0 != 0) goto L_0x0125
            java.lang.String r0 = r8.lastSticker
            boolean r0 = org.telegram.messenger.Emoji.isValidEmoji(r0)
            if (r0 == 0) goto L_0x0127
        L_0x0125:
            r0 = 1
            goto L_0x0128
        L_0x0127:
            r0 = 0
        L_0x0128:
            r22 = r0
            if (r22 == 0) goto L_0x0283
            org.telegram.ui.ChatActivity r3 = r8.parentFragment
            if (r3 == 0) goto L_0x0283
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getCurrentChat()
            if (r3 == 0) goto L_0x0142
            org.telegram.ui.ChatActivity r3 = r8.parentFragment
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getCurrentChat()
            boolean r3 = org.telegram.messenger.ChatObject.canSendStickers(r3)
            if (r3 == 0) goto L_0x0283
        L_0x0142:
            java.util.ArrayList<java.lang.String> r3 = r8.stickersToLoad
            r3.clear()
            int r3 = org.telegram.messenger.SharedConfig.suggestStickers
            r4 = 2
            if (r3 == r4) goto L_0x026e
            if (r22 != 0) goto L_0x0150
            goto L_0x026e
        L_0x0150:
            r3 = 0
            r8.stickers = r3
            r8.stickersMap = r3
            r3 = 4
            int r4 = r8.lastReqId
            if (r4 == 0) goto L_0x016a
            int r4 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            int r5 = r8.lastReqId
            r6 = 1
            r4.cancelRequest(r5, r6)
            r4 = 0
            r8.lastReqId = r4
            goto L_0x016b
        L_0x016a:
            r4 = 0
        L_0x016b:
            int r5 = r8.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            boolean r5 = r5.suggestStickersApiOnly
            r8.delayLocalResults = r4
            if (r5 != 0) goto L_0x0219
            int r6 = r8.currentAccount
            org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r6)
            java.util.ArrayList r6 = r6.getRecentStickersNoCopy(r4)
            int r4 = r8.currentAccount
            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
            r1 = 2
            java.util.ArrayList r4 = r4.getRecentStickersNoCopy(r1)
            r1 = 0
            r21 = 0
            r0 = 20
            int r2 = r6.size()
            int r0 = java.lang.Math.min(r0, r2)
            r2 = r21
        L_0x019b:
            if (r2 >= r0) goto L_0x01c3
            java.lang.Object r21 = r6.get(r2)
            r26 = r0
            r0 = r21
            org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC.Document) r0
            r21 = r3
            java.lang.String r3 = r8.lastSticker
            boolean r3 = r8.isValidSticker(r0, r3)
            if (r3 == 0) goto L_0x01bc
            java.lang.String r3 = "recent"
            r8.addStickerToResult(r0, r3)
            int r1 = r1 + 1
            r3 = 5
            if (r1 < r3) goto L_0x01bc
            goto L_0x01c7
        L_0x01bc:
            int r2 = r2 + 1
            r3 = r21
            r0 = r26
            goto L_0x019b
        L_0x01c3:
            r26 = r0
            r21 = r3
        L_0x01c7:
            r0 = 0
            int r2 = r4.size()
        L_0x01cc:
            if (r0 >= r2) goto L_0x01e8
            java.lang.Object r3 = r4.get(r0)
            org.telegram.tgnet.TLRPC$Document r3 = (org.telegram.tgnet.TLRPC.Document) r3
            r26 = r1
            java.lang.String r1 = r8.lastSticker
            boolean r1 = r8.isValidSticker(r3, r1)
            if (r1 == 0) goto L_0x01e3
            java.lang.String r1 = "fav"
            r8.addStickerToResult(r3, r1)
        L_0x01e3:
            int r0 = r0 + 1
            r1 = r26
            goto L_0x01cc
        L_0x01e8:
            r26 = r1
            int r0 = r8.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.util.HashMap r0 = r0.getAllStickers()
            if (r0 == 0) goto L_0x01ff
            java.lang.String r1 = r8.lastSticker
            java.lang.Object r1 = r0.get(r1)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            goto L_0x0200
        L_0x01ff:
            r1 = 0
        L_0x0200:
            if (r1 == 0) goto L_0x020c
            boolean r2 = r1.isEmpty()
            if (r2 != 0) goto L_0x020c
            r2 = 0
            r8.addStickersToResult(r1, r2)
        L_0x020c:
            java.util.ArrayList<org.telegram.ui.Adapters.MentionsAdapter$StickerResult> r2 = r8.stickers
            if (r2 == 0) goto L_0x021b
            org.telegram.ui.Adapters.MentionsAdapter$5 r3 = new org.telegram.ui.Adapters.MentionsAdapter$5
            r3.<init>(r4, r6)
            java.util.Collections.sort(r2, r3)
            goto L_0x021b
        L_0x0219:
            r21 = r3
        L_0x021b:
            int r0 = org.telegram.messenger.SharedConfig.suggestStickers
            if (r0 == 0) goto L_0x0221
            if (r5 == 0) goto L_0x0226
        L_0x0221:
            java.lang.String r0 = r8.lastSticker
            r8.searchServerStickers(r0, r7)
        L_0x0226:
            java.util.ArrayList<org.telegram.ui.Adapters.MentionsAdapter$StickerResult> r0 = r8.stickers
            if (r0 == 0) goto L_0x025e
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x025e
            int r0 = org.telegram.messenger.SharedConfig.suggestStickers
            if (r0 != 0) goto L_0x0249
            java.util.ArrayList<org.telegram.ui.Adapters.MentionsAdapter$StickerResult> r0 = r8.stickers
            int r0 = r0.size()
            r1 = 5
            if (r0 >= r1) goto L_0x0249
            r0 = 1
            r8.delayLocalResults = r0
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r1 = 0
            r0.needChangePanelVisibility(r1)
            r8.visibleByStickersSearch = r1
            goto L_0x025a
        L_0x0249:
            r33.checkStickerFilesExistAndDownload()
            java.util.ArrayList<java.lang.String> r0 = r8.stickersToLoad
            boolean r0 = r0.isEmpty()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            r1.needChangePanelVisibility(r0)
            r1 = 1
            r8.visibleByStickersSearch = r1
        L_0x025a:
            r33.notifyDataSetChanged()
            goto L_0x026a
        L_0x025e:
            boolean r0 = r8.visibleByStickersSearch
            if (r0 == 0) goto L_0x026a
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r1 = 0
            r0.needChangePanelVisibility(r1)
            r8.visibleByStickersSearch = r1
        L_0x026a:
            r0 = r21
            goto L_0x0319
        L_0x026e:
            boolean r0 = r8.visibleByStickersSearch
            if (r0 == 0) goto L_0x0282
            int r0 = org.telegram.messenger.SharedConfig.suggestStickers
            r1 = 2
            if (r0 != r1) goto L_0x0282
            r0 = 0
            r8.visibleByStickersSearch = r0
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            r1.needChangePanelVisibility(r0)
            r33.notifyDataSetChanged()
        L_0x0282:
            return
        L_0x0283:
            if (r12 != 0) goto L_0x0313
            boolean r0 = r8.needBotContext
            if (r0 == 0) goto L_0x0313
            r0 = 0
            char r1 = r9.charAt(r0)
            r0 = 64
            if (r1 != r0) goto L_0x0313
            r0 = 32
            int r1 = r9.indexOf(r0)
            int r0 = r34.length()
            r2 = 0
            r3 = 0
            if (r1 <= 0) goto L_0x02ac
            r4 = 1
            java.lang.String r2 = r9.substring(r4, r1)
            int r4 = r1 + 1
            java.lang.String r3 = r9.substring(r4)
            goto L_0x02d6
        L_0x02ac:
            int r4 = r0 + -1
            char r4 = r9.charAt(r4)
            r5 = 116(0x74, float:1.63E-43)
            if (r4 != r5) goto L_0x02d2
            int r4 = r0 + -2
            char r4 = r9.charAt(r4)
            r5 = 111(0x6f, float:1.56E-43)
            if (r4 != r5) goto L_0x02d2
            int r4 = r0 + -3
            char r4 = r9.charAt(r4)
            r5 = 98
            if (r4 != r5) goto L_0x02d2
            r4 = 1
            java.lang.String r2 = r9.substring(r4)
            java.lang.String r3 = ""
            goto L_0x02d6
        L_0x02d2:
            r4 = 0
            r8.searchForContextBot(r4, r4)
        L_0x02d6:
            if (r2 == 0) goto L_0x030d
            int r4 = r2.length()
            r5 = 1
            if (r4 < r5) goto L_0x030d
            r4 = 1
        L_0x02e0:
            int r5 = r2.length()
            if (r4 >= r5) goto L_0x030c
            char r5 = r2.charAt(r4)
            r6 = 48
            if (r5 < r6) goto L_0x02f2
            r6 = 57
            if (r5 <= r6) goto L_0x0309
        L_0x02f2:
            r6 = 97
            if (r5 < r6) goto L_0x02fa
            r6 = 122(0x7a, float:1.71E-43)
            if (r5 <= r6) goto L_0x0309
        L_0x02fa:
            r6 = 65
            if (r5 < r6) goto L_0x0302
            r6 = 90
            if (r5 <= r6) goto L_0x0309
        L_0x0302:
            r6 = 95
            if (r5 == r6) goto L_0x0309
            java.lang.String r2 = ""
            goto L_0x030c
        L_0x0309:
            int r4 = r4 + 1
            goto L_0x02e0
        L_0x030c:
            goto L_0x030f
        L_0x030d:
            java.lang.String r2 = ""
        L_0x030f:
            r8.searchForContextBot(r2, r3)
            goto L_0x0317
        L_0x0313:
            r0 = 0
            r8.searchForContextBot(r0, r0)
        L_0x0317:
            r0 = r21
        L_0x0319:
            org.telegram.tgnet.TLRPC$User r1 = r8.foundContextBot
            if (r1 == 0) goto L_0x031e
            return
        L_0x031e:
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r1)
            r1 = -1
            if (r12 == 0) goto L_0x033e
            r2 = 1
            java.lang.String r3 = r9.substring(r2)
            r15.append(r3)
            r2 = 0
            r8.resultStartPosition = r2
            int r2 = r15.length()
            r8.resultLength = r2
            r0 = 0
            r5 = r0
            r21 = r1
            goto L_0x0430
        L_0x033e:
            r2 = r14
        L_0x033f:
            if (r2 < 0) goto L_0x042a
            int r3 = r34.length()
            if (r2 < r3) goto L_0x034b
            r21 = r0
            goto L_0x0424
        L_0x034b:
            char r3 = r9.charAt(r2)
            if (r2 == 0) goto L_0x036e
            int r5 = r2 + -1
            char r5 = r9.charAt(r5)
            r4 = 32
            if (r5 == r4) goto L_0x036e
            int r4 = r2 + -1
            char r4 = r9.charAt(r4)
            r5 = 10
            if (r4 == r5) goto L_0x036e
            r4 = 58
            if (r3 != r4) goto L_0x036a
            goto L_0x036e
        L_0x036a:
            r21 = r0
            goto L_0x0420
        L_0x036e:
            r4 = 64
            if (r3 != r4) goto L_0x03a5
            boolean r5 = r8.needUsernames
            if (r5 != 0) goto L_0x0381
            boolean r5 = r8.needBotContext
            if (r5 == 0) goto L_0x037d
            if (r2 != 0) goto L_0x037d
            goto L_0x0381
        L_0x037d:
            r21 = r0
            goto L_0x0420
        L_0x0381:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r8.info
            if (r4 != 0) goto L_0x0394
            if (r2 == 0) goto L_0x0394
            r8.lastText = r9
            r8.lastPosition = r10
            r8.messages = r11
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r4 = r8.delegate
            r5 = 0
            r4.needChangePanelVisibility(r5)
            return
        L_0x0394:
            r1 = r2
            r0 = 0
            r8.resultStartPosition = r2
            int r4 = r15.length()
            r5 = 1
            int r4 = r4 + r5
            r8.resultLength = r4
            r5 = r0
            r21 = r1
            goto L_0x0430
        L_0x03a5:
            r5 = 35
            if (r3 != r5) goto L_0x03cc
            org.telegram.ui.Adapters.SearchAdapterHelper r4 = r8.searchAdapterHelper
            boolean r4 = r4.loadRecentHashtags()
            if (r4 == 0) goto L_0x03c5
            r0 = 1
            r8.resultStartPosition = r2
            int r4 = r15.length()
            r5 = 1
            int r4 = r4 + r5
            r8.resultLength = r4
            r4 = 0
            r15.insert(r4, r3)
            r5 = r0
            r21 = r1
            goto L_0x0430
        L_0x03c5:
            r8.lastText = r9
            r8.lastPosition = r10
            r8.messages = r11
            return
        L_0x03cc:
            if (r2 != 0) goto L_0x03e5
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r8.botInfo
            if (r5 == 0) goto L_0x03e5
            r5 = 47
            if (r3 != r5) goto L_0x03e5
            r0 = 2
            r8.resultStartPosition = r2
            int r4 = r15.length()
            r5 = 1
            int r4 = r4 + r5
            r8.resultLength = r4
            r5 = r0
            r21 = r1
            goto L_0x0430
        L_0x03e5:
            r5 = 58
            if (r3 != r5) goto L_0x041e
            int r5 = r15.length()
            if (r5 <= 0) goto L_0x041e
            r5 = 0
            char r4 = r15.charAt(r5)
            java.lang.String r5 = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n"
            int r4 = r5.indexOf(r4)
            if (r4 < 0) goto L_0x03fe
            r4 = 1
            goto L_0x03ff
        L_0x03fe:
            r4 = 0
        L_0x03ff:
            if (r4 == 0) goto L_0x040b
            int r5 = r15.length()
            r21 = r0
            r0 = 1
            if (r5 <= r0) goto L_0x0420
            goto L_0x040d
        L_0x040b:
            r21 = r0
        L_0x040d:
            r0 = 3
            r8.resultStartPosition = r2
            int r5 = r15.length()
            r18 = 1
            int r5 = r5 + 1
            r8.resultLength = r5
            r5 = r0
            r21 = r1
            goto L_0x0430
        L_0x041e:
            r21 = r0
        L_0x0420:
            r0 = 0
            r15.insert(r0, r3)
        L_0x0424:
            int r2 = r2 + -1
            r0 = r21
            goto L_0x033f
        L_0x042a:
            r21 = r0
            r5 = r21
            r21 = r1
        L_0x0430:
            r0 = -1
            if (r5 != r0) goto L_0x043a
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r1 = 0
            r0.needChangePanelVisibility(r1)
            return
        L_0x043a:
            if (r5 != 0) goto L_0x06fd
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r4 = r1
            r1 = 0
        L_0x0443:
            r2 = 100
            int r3 = r36.size()
            int r2 = java.lang.Math.min(r2, r3)
            if (r1 >= r2) goto L_0x0474
            java.lang.Object r2 = r11.get(r1)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            long r2 = r2.getFromChatId()
            r26 = 0
            int r20 = (r2 > r26 ? 1 : (r2 == r26 ? 0 : -1))
            if (r20 <= 0) goto L_0x0470
            java.lang.Long r0 = java.lang.Long.valueOf(r2)
            boolean r0 = r4.contains(r0)
            if (r0 != 0) goto L_0x0470
            java.lang.Long r0 = java.lang.Long.valueOf(r2)
            r4.add(r0)
        L_0x0470:
            int r1 = r1 + 1
            r0 = -1
            goto L_0x0443
        L_0x0474:
            java.lang.String r0 = r15.toString()
            java.lang.String r3 = r0.toLowerCase()
            r0 = 32
            int r0 = r3.indexOf(r0)
            if (r0 < 0) goto L_0x0486
            r0 = 1
            goto L_0x0487
        L_0x0486:
            r0 = 0
        L_0x0487:
            r20 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r2 = r0
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r1 = r0
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r23 = r5
            int r5 = r8.currentAccount
            org.telegram.messenger.MediaDataController r5 = org.telegram.messenger.MediaDataController.getInstance(r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r5 = r5.inlineBots
            if (r12 != 0) goto L_0x0522
            r26 = r7
            boolean r7 = r8.needBotContext
            if (r7 == 0) goto L_0x051e
            if (r21 != 0) goto L_0x051e
            boolean r7 = r5.isEmpty()
            if (r7 != 0) goto L_0x051e
            r7 = 0
            r27 = 0
            r9 = r27
        L_0x04b9:
            int r10 = r5.size()
            if (r9 >= r10) goto L_0x051a
            java.lang.Object r10 = r5.get(r9)
            org.telegram.tgnet.TLRPC$TL_topPeer r10 = (org.telegram.tgnet.TLRPC.TL_topPeer) r10
            org.telegram.tgnet.TLRPC$Peer r10 = r10.peer
            long r10 = r10.user_id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r10 = r6.getUser(r10)
            if (r10 != 0) goto L_0x04d7
            r11 = r14
            r27 = r15
            goto L_0x0510
        L_0x04d7:
            java.lang.String r11 = r10.username
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 != 0) goto L_0x0509
            int r11 = r3.length()
            if (r11 == 0) goto L_0x04f6
            java.lang.String r11 = r10.username
            java.lang.String r11 = r11.toLowerCase()
            boolean r11 = r11.startsWith(r3)
            if (r11 == 0) goto L_0x04f2
            goto L_0x04f6
        L_0x04f2:
            r11 = r14
            r27 = r15
            goto L_0x050c
        L_0x04f6:
            r2.add(r10)
            r11 = r14
            r27 = r15
            long r14 = r10.id
            r1.put(r14, r10)
            long r14 = r10.id
            r0.put(r14, r10)
            int r7 = r7 + 1
            goto L_0x050c
        L_0x0509:
            r11 = r14
            r27 = r15
        L_0x050c:
            r14 = 5
            if (r7 != r14) goto L_0x0510
            goto L_0x0527
        L_0x0510:
            int r9 = r9 + 1
            r10 = r35
            r14 = r11
            r15 = r27
            r11 = r36
            goto L_0x04b9
        L_0x051a:
            r11 = r14
            r27 = r15
            goto L_0x0527
        L_0x051e:
            r11 = r14
            r27 = r15
            goto L_0x0527
        L_0x0522:
            r26 = r7
            r11 = r14
            r27 = r15
        L_0x0527:
            org.telegram.ui.ChatActivity r7 = r8.parentFragment
            if (r7 == 0) goto L_0x0538
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getCurrentChat()
            org.telegram.ui.ChatActivity r9 = r8.parentFragment
            int r9 = r9.getThreadId()
            r10 = r9
            r9 = r7
            goto L_0x054e
        L_0x0538:
            org.telegram.tgnet.TLRPC$ChatFull r7 = r8.info
            if (r7 == 0) goto L_0x054a
            long r9 = r7.id
            java.lang.Long r7 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r7 = r6.getChat(r7)
            r9 = 0
            r10 = r9
            r9 = r7
            goto L_0x054e
        L_0x054a:
            r7 = 0
            r9 = 0
            r10 = r9
            r9 = r7
        L_0x054e:
            if (r9 == 0) goto L_0x0681
            org.telegram.tgnet.TLRPC$ChatFull r7 = r8.info
            if (r7 == 0) goto L_0x0681
            org.telegram.tgnet.TLRPC$ChatParticipants r7 = r7.participants
            if (r7 == 0) goto L_0x0681
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r7 == 0) goto L_0x056c
            boolean r7 = r9.megagroup
            if (r7 == 0) goto L_0x0563
            goto L_0x056c
        L_0x0563:
            r29 = r1
            r28 = r5
            r15 = r6
            r30 = r11
            goto L_0x0688
        L_0x056c:
            if (r13 == 0) goto L_0x0571
            r19 = -1
            goto L_0x0573
        L_0x0571:
            r19 = 0
        L_0x0573:
            r7 = r19
        L_0x0575:
            org.telegram.tgnet.TLRPC$ChatFull r14 = r8.info
            org.telegram.tgnet.TLRPC$ChatParticipants r14 = r14.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r14 = r14.participants
            int r14 = r14.size()
            if (r7 >= r14) goto L_0x0679
            r14 = -1
            if (r7 != r14) goto L_0x05b5
            int r15 = r3.length()
            if (r15 != 0) goto L_0x0596
            r2.add(r9)
            r29 = r1
            r28 = r5
            r15 = r6
            r30 = r11
            goto L_0x066a
        L_0x0596:
            java.lang.String r15 = r9.title
            r19 = 0
            java.lang.String r14 = r9.username
            r28 = r9
            r29 = r14
            long r13 = r9.id
            long r13 = -r13
            r30 = r11
            r11 = r28
            r28 = r5
            r5 = r15
            r15 = r6
            r6 = r19
            r32 = r29
            r29 = r1
            r1 = r32
            goto L_0x061c
        L_0x05b5:
            org.telegram.tgnet.TLRPC$ChatFull r13 = r8.info
            org.telegram.tgnet.TLRPC$ChatParticipants r13 = r13.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r13 = r13.participants
            java.lang.Object r13 = r13.get(r7)
            org.telegram.tgnet.TLRPC$ChatParticipant r13 = (org.telegram.tgnet.TLRPC.ChatParticipant) r13
            long r14 = r13.user_id
            java.lang.Long r14 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$User r14 = r6.getUser(r14)
            if (r14 == 0) goto L_0x0663
            if (r12 != 0) goto L_0x05df
            boolean r15 = org.telegram.messenger.UserObject.isUserSelf(r14)
            if (r15 != 0) goto L_0x05d6
            goto L_0x05df
        L_0x05d6:
            r29 = r1
            r28 = r5
            r15 = r6
            r30 = r11
            goto L_0x066a
        L_0x05df:
            r28 = r5
            r15 = r6
            long r5 = r14.id
            int r5 = r1.indexOfKey(r5)
            if (r5 < 0) goto L_0x05f0
            r29 = r1
            r30 = r11
            goto L_0x066a
        L_0x05f0:
            int r5 = r3.length()
            if (r5 != 0) goto L_0x0603
            boolean r5 = r14.deleted
            if (r5 != 0) goto L_0x0603
            r2.add(r14)
            r29 = r1
            r30 = r11
            goto L_0x066a
        L_0x0603:
            java.lang.String r5 = r14.first_name
            java.lang.String r6 = r14.last_name
            r29 = r1
            java.lang.String r1 = r14.username
            r19 = r14
            r30 = r5
            r31 = r6
            long r5 = r14.id
            r13 = r5
            r5 = r30
            r6 = r31
            r30 = r11
            r11 = r19
        L_0x061c:
            boolean r19 = android.text.TextUtils.isEmpty(r1)
            if (r19 != 0) goto L_0x062c
            java.lang.String r12 = r1.toLowerCase()
            boolean r12 = r12.startsWith(r3)
            if (r12 != 0) goto L_0x065c
        L_0x062c:
            boolean r12 = android.text.TextUtils.isEmpty(r5)
            if (r12 != 0) goto L_0x063c
            java.lang.String r12 = r5.toLowerCase()
            boolean r12 = r12.startsWith(r3)
            if (r12 != 0) goto L_0x065c
        L_0x063c:
            boolean r12 = android.text.TextUtils.isEmpty(r6)
            if (r12 != 0) goto L_0x064c
            java.lang.String r12 = r6.toLowerCase()
            boolean r12 = r12.startsWith(r3)
            if (r12 != 0) goto L_0x065c
        L_0x064c:
            if (r20 == 0) goto L_0x066a
            java.lang.String r12 = org.telegram.messenger.ContactsController.formatName(r5, r6)
            java.lang.String r12 = r12.toLowerCase()
            boolean r12 = r12.startsWith(r3)
            if (r12 == 0) goto L_0x066a
        L_0x065c:
            r2.add(r11)
            r0.put(r13, r11)
            goto L_0x066a
        L_0x0663:
            r29 = r1
            r28 = r5
            r15 = r6
            r30 = r11
        L_0x066a:
            int r7 = r7 + 1
            r12 = r37
            r13 = r38
            r6 = r15
            r5 = r28
            r1 = r29
            r11 = r30
            goto L_0x0575
        L_0x0679:
            r29 = r1
            r28 = r5
            r15 = r6
            r30 = r11
            goto L_0x0688
        L_0x0681:
            r29 = r1
            r28 = r5
            r15 = r6
            r30 = r11
        L_0x0688:
            org.telegram.ui.Adapters.MentionsAdapter$6 r1 = new org.telegram.ui.Adapters.MentionsAdapter$6
            r1.<init>(r0, r4)
            java.util.Collections.sort(r2, r1)
            r1 = 0
            r8.searchResultHashtags = r1
            r8.stickers = r1
            r8.searchResultCommands = r1
            r8.searchResultCommandsHelp = r1
            r8.searchResultCommandsUsers = r1
            r8.searchResultSuggestions = r1
            if (r9 == 0) goto L_0x06e7
            boolean r1 = r9.megagroup
            if (r1 == 0) goto L_0x06e7
            int r1 = r3.length()
            if (r1 <= 0) goto L_0x06e7
            int r1 = r2.size()
            r5 = 5
            if (r1 >= r5) goto L_0x06bd
            org.telegram.ui.Adapters.MentionsAdapter$$ExternalSyntheticLambda5 r1 = new org.telegram.ui.Adapters.MentionsAdapter$$ExternalSyntheticLambda5
            r1.<init>(r8, r2, r0)
            r8.cancelDelayRunnable = r1
            r5 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r5)
            goto L_0x06c1
        L_0x06bd:
            r1 = 1
            r8.showUsersResult(r2, r0, r1)
        L_0x06c1:
            org.telegram.ui.Adapters.MentionsAdapter$7 r11 = new org.telegram.ui.Adapters.MentionsAdapter$7
            r12 = r0
            r0 = r11
            r13 = r29
            r1 = r33
            r14 = r2
            r2 = r9
            r17 = r3
            r19 = r4
            r4 = r10
            r7 = r23
            r23 = r28
            r5 = r14
            r6 = r12
            r25 = r9
            r24 = r26
            r9 = r7
            r7 = r15
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.searchGlobalRunnable = r11
            r0 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r11, r0)
            goto L_0x06fb
        L_0x06e7:
            r12 = r0
            r14 = r2
            r17 = r3
            r19 = r4
            r25 = r9
            r9 = r23
            r24 = r26
            r23 = r28
            r13 = r29
            r0 = 1
            r8.showUsersResult(r14, r12, r0)
        L_0x06fb:
            goto L_0x083d
        L_0x06fd:
            r9 = r5
            r24 = r7
            r30 = r14
            r27 = r15
            r0 = 1
            r15 = r6
            if (r9 != r0) goto L_0x0761
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r1 = r27.toString()
            java.lang.String r1 = r1.toLowerCase()
            org.telegram.ui.Adapters.SearchAdapterHelper r2 = r8.searchAdapterHelper
            java.util.ArrayList r2 = r2.getHashtags()
            r3 = 0
        L_0x071c:
            int r4 = r2.size()
            if (r3 >= r4) goto L_0x073e
            java.lang.Object r4 = r2.get(r3)
            org.telegram.ui.Adapters.SearchAdapterHelper$HashtagObject r4 = (org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject) r4
            if (r4 == 0) goto L_0x073b
            java.lang.String r5 = r4.hashtag
            if (r5 == 0) goto L_0x073b
            java.lang.String r5 = r4.hashtag
            boolean r5 = r5.startsWith(r1)
            if (r5 == 0) goto L_0x073b
            java.lang.String r5 = r4.hashtag
            r0.add(r5)
        L_0x073b:
            int r3 = r3 + 1
            goto L_0x071c
        L_0x073e:
            r8.searchResultHashtags = r0
            r3 = 0
            r8.stickers = r3
            r8.searchResultUsernames = r3
            r8.searchResultUsernamesMap = r3
            r8.searchResultCommands = r3
            r8.searchResultCommandsHelp = r3
            r8.searchResultCommandsUsers = r3
            r8.searchResultSuggestions = r3
            r33.notifyDataSetChanged()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r3 = r8.delegate
            java.util.ArrayList<java.lang.String> r4 = r8.searchResultHashtags
            boolean r4 = r4.isEmpty()
            r5 = 1
            r4 = r4 ^ r5
            r3.needChangePanelVisibility(r4)
            goto L_0x083d
        L_0x0761:
            r0 = 2
            if (r9 != r0) goto L_0x07f9
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.lang.String r3 = r27.toString()
            java.lang.String r3 = r3.toLowerCase()
            r4 = 0
        L_0x077c:
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r8.botInfo
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x07d9
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r8.botInfo
            java.lang.Object r5 = r5.valueAt(r4)
            org.telegram.tgnet.TLRPC$BotInfo r5 = (org.telegram.tgnet.TLRPC.BotInfo) r5
            r6 = 0
        L_0x078d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_botCommand> r7 = r5.commands
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x07d6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_botCommand> r7 = r5.commands
            java.lang.Object r7 = r7.get(r6)
            org.telegram.tgnet.TLRPC$TL_botCommand r7 = (org.telegram.tgnet.TLRPC.TL_botCommand) r7
            if (r7 == 0) goto L_0x07d3
            java.lang.String r10 = r7.command
            if (r10 == 0) goto L_0x07d3
            java.lang.String r10 = r7.command
            boolean r10 = r10.startsWith(r3)
            if (r10 == 0) goto L_0x07d3
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "/"
            r10.append(r11)
            java.lang.String r11 = r7.command
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r0.add(r10)
            java.lang.String r10 = r7.description
            r1.add(r10)
            long r10 = r5.user_id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r10 = r15.getUser(r10)
            r2.add(r10)
        L_0x07d3:
            int r6 = r6 + 1
            goto L_0x078d
        L_0x07d6:
            int r4 = r4 + 1
            goto L_0x077c
        L_0x07d9:
            r4 = 0
            r8.searchResultHashtags = r4
            r8.stickers = r4
            r8.searchResultUsernames = r4
            r8.searchResultUsernamesMap = r4
            r8.searchResultSuggestions = r4
            r8.searchResultCommands = r0
            r8.searchResultCommandsHelp = r1
            r8.searchResultCommandsUsers = r2
            r33.notifyDataSetChanged()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r4 = r8.delegate
            boolean r5 = r0.isEmpty()
            r6 = 1
            r5 = r5 ^ r6
            r4.needChangePanelVisibility(r5)
            goto L_0x083d
        L_0x07f9:
            r0 = 3
            if (r9 != r0) goto L_0x0829
            java.lang.String[] r0 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
            java.lang.String[] r1 = r8.lastSearchKeyboardLanguage
            boolean r1 = java.util.Arrays.equals(r0, r1)
            if (r1 != 0) goto L_0x0811
            int r1 = r8.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.fetchNewEmojiKeywords(r0)
        L_0x0811:
            r8.lastSearchKeyboardLanguage = r0
            int r1 = r8.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            java.lang.String[] r2 = r8.lastSearchKeyboardLanguage
            java.lang.String r3 = r27.toString()
            org.telegram.ui.Adapters.MentionsAdapter$$ExternalSyntheticLambda6 r4 = new org.telegram.ui.Adapters.MentionsAdapter$$ExternalSyntheticLambda6
            r4.<init>(r8)
            r5 = 0
            r1.getEmojiSuggestions(r2, r3, r5, r4)
            goto L_0x083c
        L_0x0829:
            r0 = 4
            if (r9 != r0) goto L_0x083c
            r0 = 0
            r8.searchResultHashtags = r0
            r8.searchResultUsernames = r0
            r8.searchResultUsernamesMap = r0
            r8.searchResultSuggestions = r0
            r8.searchResultCommands = r0
            r8.searchResultCommandsHelp = r0
            r8.searchResultCommandsUsers = r0
            goto L_0x083d
        L_0x083c:
        L_0x083d:
            return
        L_0x083e:
            r0 = r1
        L_0x083f:
            r8.searchForContextBot(r0, r0)
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            r2 = 0
            r1.needChangePanelVisibility(r2)
            r8.lastText = r0
            r33.clearStickers()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchUsernameOrHashtag(java.lang.String, int, java.util.ArrayList, boolean, boolean):void");
    }

    /* renamed from: lambda$searchUsernameOrHashtag$7$org-telegram-ui-Adapters-MentionsAdapter  reason: not valid java name */
    public /* synthetic */ void m2635xea5050b6(ArrayList newResult, LongSparseArray newMap) {
        this.cancelDelayRunnable = null;
        showUsersResult(newResult, newMap, true);
    }

    /* renamed from: lambda$searchUsernameOrHashtag$8$org-telegram-ui-Adapters-MentionsAdapter  reason: not valid java name */
    public /* synthetic */ void m2636xf1b585d5(ArrayList param, String alias) {
        this.searchResultSuggestions = param;
        this.searchResultHashtags = null;
        this.stickers = null;
        this.searchResultUsernames = null;
        this.searchResultUsernamesMap = null;
        this.searchResultCommands = null;
        this.searchResultCommandsHelp = null;
        this.searchResultCommandsUsers = null;
        notifyDataSetChanged();
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        ArrayList<MediaDataController.KeywordResult> arrayList = this.searchResultSuggestions;
        mentionsAdapterDelegate.needChangePanelVisibility(arrayList != null && !arrayList.isEmpty());
    }

    public void setIsReversed(boolean isReversed2) {
        if (this.isReversed != isReversed2) {
            this.isReversed = isReversed2;
            int itemCount = getLastItemCount();
            if (itemCount > 0) {
                notifyItemChanged(0);
            }
            if (itemCount > 1) {
                notifyItemChanged(itemCount - 1);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showUsersResult(ArrayList<TLObject> newResult, LongSparseArray<TLObject> newMap, boolean notify) {
        this.searchResultUsernames = newResult;
        this.searchResultUsernamesMap = newMap;
        Runnable runnable = this.cancelDelayRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.cancelDelayRunnable = null;
        }
        this.searchResultBotContext = null;
        this.stickers = null;
        if (notify) {
            notifyDataSetChanged();
            this.delegate.needChangePanelVisibility(!this.searchResultUsernames.isEmpty());
        }
    }

    public int getResultStartPosition() {
        return this.resultStartPosition;
    }

    public int getResultLength() {
        return this.resultLength;
    }

    public ArrayList<TLRPC.BotInlineResult> getSearchResultBotContext() {
        return this.searchResultBotContext;
    }

    public int getItemCount() {
        int itemCountInternal = getItemCountInternal();
        this.lastItemCount = itemCountInternal;
        return itemCountInternal;
    }

    public int getLastItemCount() {
        return this.lastItemCount;
    }

    public int getItemCountInternal() {
        int i = 1;
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 1;
        }
        ArrayList<StickerResult> arrayList = this.stickers;
        if (arrayList != null) {
            return arrayList.size();
        }
        ArrayList<TLRPC.BotInlineResult> arrayList2 = this.searchResultBotContext;
        if (arrayList2 != null) {
            int size = arrayList2.size();
            if (this.searchResultBotContextSwitch == null) {
                i = 0;
            }
            return size + i;
        }
        ArrayList<TLObject> arrayList3 = this.searchResultUsernames;
        if (arrayList3 != null) {
            return arrayList3.size();
        }
        ArrayList<String> arrayList4 = this.searchResultHashtags;
        if (arrayList4 != null) {
            return arrayList4.size();
        }
        ArrayList<String> arrayList5 = this.searchResultCommands;
        if (arrayList5 != null) {
            return arrayList5.size();
        }
        ArrayList<MediaDataController.KeywordResult> arrayList6 = this.searchResultSuggestions;
        if (arrayList6 != null) {
            return arrayList6.size();
        }
        return 0;
    }

    public void clear(boolean safe) {
        if (!safe || (this.channelReqId == 0 && this.contextQueryReqid == 0 && this.contextUsernameReqid == 0 && this.lastReqId == 0)) {
            this.foundContextBot = null;
            ArrayList<StickerResult> arrayList = this.stickers;
            if (arrayList != null) {
                arrayList.clear();
            }
            ArrayList<TLRPC.BotInlineResult> arrayList2 = this.searchResultBotContext;
            if (arrayList2 != null) {
                arrayList2.clear();
            }
            this.searchResultBotContextSwitch = null;
            ArrayList<TLObject> arrayList3 = this.searchResultUsernames;
            if (arrayList3 != null) {
                arrayList3.clear();
            }
            ArrayList<String> arrayList4 = this.searchResultHashtags;
            if (arrayList4 != null) {
                arrayList4.clear();
            }
            ArrayList<String> arrayList5 = this.searchResultCommands;
            if (arrayList5 != null) {
                arrayList5.clear();
            }
            ArrayList<MediaDataController.KeywordResult> arrayList6 = this.searchResultSuggestions;
            if (arrayList6 != null) {
                arrayList6.clear();
            }
            notifyDataSetChanged();
        }
    }

    public int getItemViewType(int position) {
        if (this.stickers != null) {
            return 4;
        }
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 3;
        }
        if (this.searchResultBotContext == null) {
            return 0;
        }
        if (position != 0 || this.searchResultBotContextSwitch == null) {
            return 1;
        }
        return 2;
    }

    public void addHashtagsFromMessage(CharSequence message) {
        this.searchAdapterHelper.addHashtagsFromMessage(message);
    }

    public int getItemPosition(int i) {
        if (this.searchResultBotContext == null || this.searchResultBotContextSwitch == null) {
            return i;
        }
        return i - 1;
    }

    public Object getItemParent(int i) {
        ArrayList<StickerResult> arrayList = this.stickers;
        if (arrayList == null || i < 0 || i >= arrayList.size()) {
            return null;
        }
        return this.stickers.get(i).parent;
    }

    public Object getItem(int i) {
        ArrayList<StickerResult> arrayList = this.stickers;
        if (arrayList == null) {
            ArrayList<TLRPC.BotInlineResult> arrayList2 = this.searchResultBotContext;
            if (arrayList2 != null) {
                TLRPC.TL_inlineBotSwitchPM tL_inlineBotSwitchPM = this.searchResultBotContextSwitch;
                if (tL_inlineBotSwitchPM != null) {
                    if (i == 0) {
                        return tL_inlineBotSwitchPM;
                    }
                    i--;
                }
                if (i < 0 || i >= arrayList2.size()) {
                    return null;
                }
                return this.searchResultBotContext.get(i);
            }
            ArrayList<TLObject> arrayList3 = this.searchResultUsernames;
            if (arrayList3 == null) {
                ArrayList<String> arrayList4 = this.searchResultHashtags;
                if (arrayList4 == null) {
                    ArrayList<MediaDataController.KeywordResult> arrayList5 = this.searchResultSuggestions;
                    if (arrayList5 == null) {
                        ArrayList<String> arrayList6 = this.searchResultCommands;
                        if (arrayList6 == null || i < 0 || i >= arrayList6.size()) {
                            return null;
                        }
                        ArrayList<TLRPC.User> arrayList7 = this.searchResultCommandsUsers;
                        if (arrayList7 == null || (this.botsCount == 1 && !(this.info instanceof TLRPC.TL_channelFull))) {
                            return this.searchResultCommands.get(i);
                        }
                        if (arrayList7.get(i) != null) {
                            Object[] objArr = new Object[2];
                            objArr[0] = this.searchResultCommands.get(i);
                            objArr[1] = this.searchResultCommandsUsers.get(i) != null ? this.searchResultCommandsUsers.get(i).username : "";
                            return String.format("%s@%s", objArr);
                        }
                        return String.format("%s", new Object[]{this.searchResultCommands.get(i)});
                    } else if (i < 0 || i >= arrayList5.size()) {
                        return null;
                    } else {
                        return this.searchResultSuggestions.get(i);
                    }
                } else if (i < 0 || i >= arrayList4.size()) {
                    return null;
                } else {
                    return this.searchResultHashtags.get(i);
                }
            } else if (i < 0 || i >= arrayList3.size()) {
                return null;
            } else {
                return this.searchResultUsernames.get(i);
            }
        } else if (i < 0 || i >= arrayList.size()) {
            return null;
        } else {
            return this.stickers.get(i).sticker;
        }
    }

    public boolean isLongClickEnabled() {
        return (this.searchResultHashtags == null && this.searchResultCommands == null) ? false : true;
    }

    public boolean isBotCommands() {
        return this.searchResultCommands != null;
    }

    public boolean isStickers() {
        return this.stickers != null;
    }

    public boolean isBotContext() {
        return this.searchResultBotContext != null;
    }

    public boolean isBannedInline() {
        return this.foundContextBot != null && !this.inlineMediaEnabled;
    }

    public boolean isMediaLayout() {
        return this.contextMedia || this.stickers != null;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return (this.foundContextBot == null || this.inlineMediaEnabled) && this.stickers == null;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new MentionCell(this.mContext, this.resourcesProvider);
                ((MentionCell) view).setIsDarkTheme(this.isDarkTheme);
                break;
            case 1:
                view = new ContextLinkCell(this.mContext);
                ((ContextLinkCell) view).setDelegate(new MentionsAdapter$$ExternalSyntheticLambda9(this));
                break;
            case 2:
                view = new BotSwitchCell(this.mContext);
                break;
            case 3:
                TextView textView = new TextView(this.mContext);
                textView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                textView.setTextSize(1, 14.0f);
                textView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText2"));
                view = textView;
                break;
            default:
                view = new StickerCell(this.mContext);
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    /* renamed from: lambda$onCreateViewHolder$9$org-telegram-ui-Adapters-MentionsAdapter  reason: not valid java name */
    public /* synthetic */ void m2627xddefa6d9(ContextLinkCell cell) {
        this.delegate.onContextClick(cell.getResult());
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();
        if (type == 4) {
            StickerCell stickerCell = (StickerCell) holder.itemView;
            StickerResult result = this.stickers.get(position);
            stickerCell.setSticker(result.sticker, result.parent);
            stickerCell.setClearsInputField(true);
        } else if (type == 3) {
            TextView textView = (TextView) holder.itemView;
            TLRPC.Chat chat = this.parentFragment.getCurrentChat();
            if (chat == null) {
                return;
            }
            if (!ChatObject.hasAdminRights(chat) && chat.default_banned_rights != null && chat.default_banned_rights.send_inline) {
                textView.setText(LocaleController.getString("GlobalAttachInlineRestricted", NUM));
            } else if (AndroidUtilities.isBannedForever(chat.banned_rights)) {
                textView.setText(LocaleController.getString("AttachInlineRestrictedForever", NUM));
            } else {
                textView.setText(LocaleController.formatString("AttachInlineRestricted", NUM, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
            }
        } else if (this.searchResultBotContext != null) {
            boolean hasTop = this.searchResultBotContextSwitch != null;
            if (holder.getItemViewType() != 2) {
                if (hasTop) {
                    position--;
                }
                ((ContextLinkCell) holder.itemView).setLink(this.searchResultBotContext.get(position), this.foundContextBot, this.contextMedia, position != this.searchResultBotContext.size() - 1, hasTop && position == 0, "gif".equals(this.searchingContextUsername));
            } else if (hasTop) {
                ((BotSwitchCell) holder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else {
            ArrayList<TLObject> arrayList = this.searchResultUsernames;
            if (arrayList != null) {
                TLObject object = arrayList.get(position);
                if (object instanceof TLRPC.User) {
                    ((MentionCell) holder.itemView).setUser((TLRPC.User) object);
                } else if (object instanceof TLRPC.Chat) {
                    ((MentionCell) holder.itemView).setChat((TLRPC.Chat) object);
                }
            } else if (this.searchResultHashtags != null) {
                ((MentionCell) holder.itemView).setText(this.searchResultHashtags.get(position));
            } else if (this.searchResultSuggestions != null) {
                ((MentionCell) holder.itemView).setEmojiSuggestion(this.searchResultSuggestions.get(position));
            } else if (this.searchResultCommands != null) {
                MentionCell mentionCell = (MentionCell) holder.itemView;
                String str = this.searchResultCommands.get(position);
                String str2 = this.searchResultCommandsHelp.get(position);
                ArrayList<TLRPC.User> arrayList2 = this.searchResultCommandsUsers;
                mentionCell.setBotCommand(str, str2, arrayList2 != null ? arrayList2.get(position) : null);
            }
            ((MentionCell) holder.itemView).setDivider(false);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        TLRPC.User user;
        if (requestCode == 2 && (user = this.foundContextBot) != null && user.bot_inline_geo) {
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                onLocationUnavailable();
            } else {
                this.locationProvider.start();
            }
        }
    }

    public void doSomeStickersAction() {
        if (isStickers()) {
            if (this.mentionsStickersActionTracker == null) {
                AnonymousClass8 r1 = new EmojiView.ChooseStickerActionTracker(this.currentAccount, this.dialog_id, this.threadMessageId) {
                    public boolean isShown() {
                        return MentionsAdapter.this.isStickers();
                    }
                };
                this.mentionsStickersActionTracker = r1;
                r1.checkVisibility();
            }
            this.mentionsStickersActionTracker.doSomeAction();
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
