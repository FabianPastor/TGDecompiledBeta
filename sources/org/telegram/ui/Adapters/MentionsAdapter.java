package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
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
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inlineBotSwitchPM;
import org.telegram.tgnet.TLRPC$TL_messages_botResults;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoSizeProgressive;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;

public class MentionsAdapter extends RecyclerListView.SelectionAdapter implements NotificationCenter.NotificationCenterDelegate {
    private SparseArray<TLRPC$BotInfo> botInfo;
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
    public TLRPC$User foundContextBot;
    private TLRPC$ChatFull info;
    private boolean inlineMediaEnabled = true;
    private boolean isDarkTheme;
    /* access modifiers changed from: private */
    public boolean isSearchingMentions;
    /* access modifiers changed from: private */
    public boolean lastForSearch;
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
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> messages;
    private boolean needBotContext = true;
    private boolean needUsernames = true;
    private String nextQueryOffset;
    /* access modifiers changed from: private */
    public boolean noUserName;
    private ChatActivity parentFragment;
    private int resultLength;
    private int resultStartPosition;
    private SearchAdapterHelper searchAdapterHelper;
    /* access modifiers changed from: private */
    public Runnable searchGlobalRunnable;
    private ArrayList<TLRPC$BotInlineResult> searchResultBotContext;
    private TLRPC$TL_inlineBotSwitchPM searchResultBotContextSwitch;
    private ArrayList<String> searchResultCommands;
    private ArrayList<String> searchResultCommandsHelp;
    private ArrayList<TLRPC$User> searchResultCommandsUsers;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<MediaDataController.KeywordResult> searchResultSuggestions;
    /* access modifiers changed from: private */
    public ArrayList<TLObject> searchResultUsernames;
    /* access modifiers changed from: private */
    public SparseArray<TLObject> searchResultUsernamesMap;
    /* access modifiers changed from: private */
    public String searchingContextQuery;
    /* access modifiers changed from: private */
    public String searchingContextUsername;
    private ArrayList<StickerResult> stickers;
    private HashMap<String, TLRPC$Document> stickersMap;
    private ArrayList<String> stickersToLoad = new ArrayList<>();
    private boolean visibleByStickersSearch;

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(TLRPC$BotInlineResult tLRPC$BotInlineResult);

        void onContextSearch(boolean z);
    }

    static /* synthetic */ int access$1704(MentionsAdapter mentionsAdapter) {
        int i = mentionsAdapter.channelLastReqId + 1;
        mentionsAdapter.channelLastReqId = i;
        return i;
    }

    private static class StickerResult {
        public Object parent;
        public TLRPC$Document sticker;

        public StickerResult(TLRPC$Document tLRPC$Document, Object obj) {
            this.sticker = tLRPC$Document;
            this.parent = obj;
        }
    }

    public MentionsAdapter(Context context, boolean z, long j, MentionsAdapterDelegate mentionsAdapterDelegate) {
        this.mContext = context;
        this.delegate = mentionsAdapterDelegate;
        this.isDarkTheme = z;
        this.dialog_id = j;
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ boolean canApplySearchResults(int i) {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
            }

            public /* synthetic */ SparseArray getExcludeCallParticipants() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
            }

            public /* synthetic */ SparseArray getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged(int i) {
                MentionsAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                if (MentionsAdapter.this.lastText != null) {
                    MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                    mentionsAdapter.searchUsernameOrHashtag(mentionsAdapter.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages, MentionsAdapter.this.lastUsernameOnly, MentionsAdapter.this.lastForSearch);
                }
            }
        });
        if (!z) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ArrayList<StickerResult> arrayList;
        if ((i == NotificationCenter.fileLoaded || i == NotificationCenter.fileLoadFailed) && (arrayList = this.stickers) != null && !arrayList.isEmpty() && !this.stickersToLoad.isEmpty() && this.visibleByStickersSearch) {
            boolean z = false;
            this.stickersToLoad.remove(objArr[0]);
            if (this.stickersToLoad.isEmpty()) {
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                ArrayList<StickerResult> arrayList2 = this.stickers;
                if (arrayList2 != null && !arrayList2.isEmpty()) {
                    z = true;
                }
                mentionsAdapterDelegate.needChangePanelVisibility(z);
            }
        }
    }

    private void addStickerToResult(TLRPC$Document tLRPC$Document, Object obj) {
        if (tLRPC$Document != null) {
            String str = tLRPC$Document.dc_id + "_" + tLRPC$Document.id;
            HashMap<String, TLRPC$Document> hashMap = this.stickersMap;
            if (hashMap == null || !hashMap.containsKey(str)) {
                if (this.stickers == null) {
                    this.stickers = new ArrayList<>();
                    this.stickersMap = new HashMap<>();
                }
                this.stickers.add(new StickerResult(tLRPC$Document, obj));
                this.stickersMap.put(str, tLRPC$Document);
            }
        }
    }

    private void addStickersToResult(ArrayList<TLRPC$Document> arrayList, Object obj) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLRPC$Document tLRPC$Document = arrayList.get(i);
                String str = tLRPC$Document.dc_id + "_" + tLRPC$Document.id;
                HashMap<String, TLRPC$Document> hashMap = this.stickersMap;
                if (hashMap == null || !hashMap.containsKey(str)) {
                    if (this.stickers == null) {
                        this.stickers = new ArrayList<>();
                        this.stickersMap = new HashMap<>();
                    }
                    int size2 = tLRPC$Document.attributes.size();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= size2) {
                            break;
                        }
                        TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i2);
                        if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                            obj = tLRPC$DocumentAttribute.stickerset;
                            break;
                        }
                        i2++;
                    }
                    this.stickers.add(new StickerResult(tLRPC$Document, obj));
                    this.stickersMap.put(str, tLRPC$Document);
                }
            }
        }
    }

    private boolean checkStickerFilesExistAndDownload() {
        if (this.stickers == null) {
            return false;
        }
        this.stickersToLoad.clear();
        int min = Math.min(6, this.stickers.size());
        for (int i = 0; i < min; i++) {
            StickerResult stickerResult = this.stickers.get(i);
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(stickerResult.sticker.thumbs, 90);
            if (((closestPhotoSizeWithSize instanceof TLRPC$TL_photoSize) || (closestPhotoSizeWithSize instanceof TLRPC$TL_photoSizeProgressive)) && !FileLoader.getPathToAttach(closestPhotoSizeWithSize, "webp", true).exists()) {
                this.stickersToLoad.add(FileLoader.getAttachFileName(closestPhotoSizeWithSize, "webp"));
                FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForDocument(closestPhotoSizeWithSize, stickerResult.sticker), stickerResult.parent, "webp", 1, 1);
            }
        }
        return this.stickersToLoad.isEmpty();
    }

    private boolean isValidSticker(TLRPC$Document tLRPC$Document, String str) {
        int size = tLRPC$Document.attributes.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            }
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                String str2 = tLRPC$DocumentAttribute.alt;
                if (str2 == null || !str2.contains(str)) {
                    return false;
                }
                return true;
            }
            i++;
        }
        return false;
    }

    private void searchServerStickers(String str, String str2) {
        TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers = new TLRPC$TL_messages_getStickers();
        tLRPC$TL_messages_getStickers.emoticon = str2;
        tLRPC$TL_messages_getStickers.hash = 0;
        this.lastReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getStickers, new RequestDelegate(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                MentionsAdapter.this.lambda$searchServerStickers$1$MentionsAdapter(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchServerStickers$1 */
    public /* synthetic */ void lambda$searchServerStickers$1$MentionsAdapter(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, tLObject) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MentionsAdapter.this.lambda$searchServerStickers$0$MentionsAdapter(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchServerStickers$0 */
    public /* synthetic */ void lambda$searchServerStickers$0$MentionsAdapter(String str, TLObject tLObject) {
        ArrayList<StickerResult> arrayList;
        int i = 0;
        this.lastReqId = 0;
        if (str.equals(this.lastSticker) && (tLObject instanceof TLRPC$TL_messages_stickers)) {
            this.delayLocalResults = false;
            TLRPC$TL_messages_stickers tLRPC$TL_messages_stickers = (TLRPC$TL_messages_stickers) tLObject;
            ArrayList<StickerResult> arrayList2 = this.stickers;
            int size = arrayList2 != null ? arrayList2.size() : 0;
            ArrayList<TLRPC$Document> arrayList3 = tLRPC$TL_messages_stickers.stickers;
            addStickersToResult(arrayList3, "sticker_search_" + str);
            ArrayList<StickerResult> arrayList4 = this.stickers;
            if (arrayList4 != null) {
                i = arrayList4.size();
            }
            if (!this.visibleByStickersSearch && (arrayList = this.stickers) != null && !arrayList.isEmpty()) {
                checkStickerFilesExistAndDownload();
                this.delegate.needChangePanelVisibility(this.stickersToLoad.isEmpty());
                this.visibleByStickersSearch = true;
            }
            if (size != i) {
                notifyDataSetChanged();
            }
        }
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

    public void setParentFragment(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        ChatActivity chatActivity;
        TLRPC$Chat currentChat;
        this.currentAccount = UserConfig.selectedAccount;
        this.info = tLRPC$ChatFull;
        if (!(this.inlineMediaEnabled || this.foundContextBot == null || (chatActivity = this.parentFragment) == null || (currentChat = chatActivity.getCurrentChat()) == null)) {
            boolean canSendStickers = ChatObject.canSendStickers(currentChat);
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

    public void setNeedUsernames(boolean z) {
        this.needUsernames = z;
    }

    public void setNeedBotContext(boolean z) {
        this.needBotContext = z;
    }

    public void setBotInfo(SparseArray<TLRPC$BotInfo> sparseArray) {
        this.botInfo = sparseArray;
    }

    public void setBotsCount(int i) {
        this.botsCount = i;
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

    public TLRPC$TL_inlineBotSwitchPM getBotContextSwitch() {
        return this.searchResultBotContextSwitch;
    }

    public int getContextBotId() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User != null) {
            return tLRPC$User.id;
        }
        return 0;
    }

    public TLRPC$User getContextBotUser() {
        return this.foundContextBot;
    }

    public String getContextBotName() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        return tLRPC$User != null ? tLRPC$User.username : "";
    }

    /* access modifiers changed from: private */
    public void processFoundUser(TLRPC$User tLRPC$User) {
        ChatActivity chatActivity;
        TLRPC$Chat currentChat;
        this.contextUsernameReqid = 0;
        this.locationProvider.stop();
        if (tLRPC$User == null || !tLRPC$User.bot || tLRPC$User.bot_inline_placeholder == null) {
            this.foundContextBot = null;
            this.inlineMediaEnabled = true;
        } else {
            this.foundContextBot = tLRPC$User;
            ChatActivity chatActivity2 = this.parentFragment;
            if (!(chatActivity2 == null || (currentChat = chatActivity2.getCurrentChat()) == null)) {
                boolean canSendStickers = ChatObject.canSendStickers(currentChat);
                this.inlineMediaEnabled = canSendStickers;
                if (!canSendStickers) {
                    notifyDataSetChanged();
                    this.delegate.needChangePanelVisibility(true);
                    return;
                }
            }
            if (this.foundContextBot.bot_inline_geo) {
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                if (notificationsSettings.getBoolean("inlinegeo_" + this.foundContextBot.id, false) || (chatActivity = this.parentFragment) == null || chatActivity.getParentActivity() == null) {
                    checkLocationPermissionsOrStart();
                } else {
                    TLRPC$User tLRPC$User2 = this.foundContextBot;
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentFragment.getParentActivity());
                    builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                    builder.setMessage(LocaleController.getString("ShareYouLocationInline", NUM));
                    boolean[] zArr = new boolean[1];
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(zArr, tLRPC$User2) {
                        public final /* synthetic */ boolean[] f$1;
                        public final /* synthetic */ TLRPC$User f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MentionsAdapter.this.lambda$processFoundUser$2$MentionsAdapter(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener(zArr) {
                        public final /* synthetic */ boolean[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MentionsAdapter.this.lambda$processFoundUser$3$MentionsAdapter(this.f$1, dialogInterface, i);
                        }
                    });
                    this.parentFragment.showDialog(builder.create(), new DialogInterface.OnDismissListener(zArr) {
                        public final /* synthetic */ boolean[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onDismiss(DialogInterface dialogInterface) {
                            MentionsAdapter.this.lambda$processFoundUser$4$MentionsAdapter(this.f$1, dialogInterface);
                        }
                    });
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$processFoundUser$2 */
    public /* synthetic */ void lambda$processFoundUser$2$MentionsAdapter(boolean[] zArr, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        if (tLRPC$User != null) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putBoolean("inlinegeo_" + tLRPC$User.id, true).commit();
            checkLocationPermissionsOrStart();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processFoundUser$3 */
    public /* synthetic */ void lambda$processFoundUser$3$MentionsAdapter(boolean[] zArr, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        onLocationUnavailable();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processFoundUser$4 */
    public /* synthetic */ void lambda$processFoundUser$4$MentionsAdapter(boolean[] zArr, DialogInterface dialogInterface) {
        if (!zArr[0]) {
            onLocationUnavailable();
        }
    }

    private void searchForContextBot(String str, String str2) {
        String str3;
        String str4;
        String str5;
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User == null || (str4 = tLRPC$User.username) == null || !str4.equals(str) || (str5 = this.searchingContextQuery) == null || !str5.equals(str2)) {
            this.searchResultBotContext = null;
            this.searchResultBotContextSwitch = null;
            notifyDataSetChanged();
            if (this.foundContextBot != null) {
                if (this.inlineMediaEnabled || str == null || str2 == null) {
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
            if (TextUtils.isEmpty(str) || ((str3 = this.searchingContextUsername) != null && !str3.equals(str))) {
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
                if (str == null || str.length() == 0) {
                    return;
                }
            }
            if (str2 == null) {
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
                } else if (str.equals("gif")) {
                    this.searchingContextUsername = "gif";
                    this.delegate.onContextSearch(false);
                }
            }
            final MessagesController instance = MessagesController.getInstance(this.currentAccount);
            final MessagesStorage instance2 = MessagesStorage.getInstance(this.currentAccount);
            this.searchingContextQuery = str2;
            final String str6 = str2;
            final String str7 = str;
            AnonymousClass4 r1 = new Runnable() {
                public void run() {
                    if (MentionsAdapter.this.contextQueryRunnable == this) {
                        Runnable unused = MentionsAdapter.this.contextQueryRunnable = null;
                        if (MentionsAdapter.this.foundContextBot == null && !MentionsAdapter.this.noUserName) {
                            String unused2 = MentionsAdapter.this.searchingContextUsername = str7;
                            TLObject userOrChat = instance.getUserOrChat(MentionsAdapter.this.searchingContextUsername);
                            if (userOrChat instanceof TLRPC$User) {
                                MentionsAdapter.this.processFoundUser((TLRPC$User) userOrChat);
                                return;
                            }
                            TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
                            tLRPC$TL_contacts_resolveUsername.username = MentionsAdapter.this.searchingContextUsername;
                            MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                            int unused3 = mentionsAdapter.contextUsernameReqid = ConnectionsManager.getInstance(mentionsAdapter.currentAccount).sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate(str7, instance, instance2) {
                                public final /* synthetic */ String f$1;
                                public final /* synthetic */ MessagesController f$2;
                                public final /* synthetic */ MessagesStorage f$3;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                }

                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    MentionsAdapter.AnonymousClass4.this.lambda$run$1$MentionsAdapter$4(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                                }
                            });
                        } else if (!MentionsAdapter.this.noUserName) {
                            MentionsAdapter mentionsAdapter2 = MentionsAdapter.this;
                            mentionsAdapter2.searchForContextBotResults(true, mentionsAdapter2.foundContextBot, str6, "");
                        }
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$run$1 */
                public /* synthetic */ void lambda$run$1$MentionsAdapter$4(String str, MessagesController messagesController, MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(str, tLRPC$TL_error, tLObject, messagesController, messagesStorage) {
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ TLRPC$TL_error f$2;
                        public final /* synthetic */ TLObject f$3;
                        public final /* synthetic */ MessagesController f$4;
                        public final /* synthetic */ MessagesStorage f$5;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                        }

                        public final void run() {
                            MentionsAdapter.AnonymousClass4.this.lambda$run$0$MentionsAdapter$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                        }
                    });
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$run$0 */
                public /* synthetic */ void lambda$run$0$MentionsAdapter$4(String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, MessagesController messagesController, MessagesStorage messagesStorage) {
                    if (MentionsAdapter.this.searchingContextUsername != null && MentionsAdapter.this.searchingContextUsername.equals(str)) {
                        TLRPC$User tLRPC$User = null;
                        if (tLRPC$TL_error == null) {
                            TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
                            if (!tLRPC$TL_contacts_resolvedPeer.users.isEmpty()) {
                                TLRPC$User tLRPC$User2 = tLRPC$TL_contacts_resolvedPeer.users.get(0);
                                messagesController.putUser(tLRPC$User2, false);
                                messagesStorage.putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, (ArrayList<TLRPC$Chat>) null, true, true);
                                tLRPC$User = tLRPC$User2;
                            }
                        }
                        MentionsAdapter.this.processFoundUser(tLRPC$User);
                    }
                }
            };
            this.contextQueryRunnable = r1;
            AndroidUtilities.runOnUIThread(r1, 400);
        }
    }

    /* access modifiers changed from: private */
    public void onLocationUnavailable() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User != null && tLRPC$User.bot_inline_geo) {
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
                TLRPC$User tLRPC$User = this.foundContextBot;
                if (tLRPC$User != null && tLRPC$User.bot_inline_geo) {
                    this.locationProvider.start();
                    return;
                }
                return;
            }
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
        }
    }

    public void setSearchingMentions(boolean z) {
        this.isSearchingMentions = z;
    }

    public String getBotCaption() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User != null) {
            return tLRPC$User.bot_inline_placeholder;
        }
        String str = this.searchingContextUsername;
        if (str == null || !str.equals("gif")) {
            return null;
        }
        return "Search GIFs";
    }

    public void searchForContextBotForNextOffset() {
        String str;
        TLRPC$User tLRPC$User;
        String str2;
        if (this.contextQueryReqid == 0 && (str = this.nextQueryOffset) != null && str.length() != 0 && (tLRPC$User = this.foundContextBot) != null && (str2 = this.searchingContextQuery) != null) {
            searchForContextBotResults(true, tLRPC$User, str2, this.nextQueryOffset);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006a, code lost:
        r1 = r8.lastKnownLocation;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchForContextBotResults(boolean r17, org.telegram.tgnet.TLRPC$User r18, java.lang.String r19, java.lang.String r20) {
        /*
            r16 = this;
            r8 = r16
            r9 = r18
            r10 = r19
            r11 = r20
            int r0 = r8.contextQueryReqid
            r1 = 0
            r12 = 1
            if (r0 == 0) goto L_0x001b
            int r0 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            int r2 = r8.contextQueryReqid
            r0.cancelRequest(r2, r12)
            r8.contextQueryReqid = r1
        L_0x001b:
            boolean r0 = r8.inlineMediaEnabled
            if (r0 != 0) goto L_0x0027
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            if (r0 == 0) goto L_0x0026
            r0.onContextSearch(r1)
        L_0x0026:
            return
        L_0x0027:
            if (r10 == 0) goto L_0x012a
            if (r9 != 0) goto L_0x002d
            goto L_0x012a
        L_0x002d:
            boolean r0 = r9.bot_inline_geo
            if (r0 == 0) goto L_0x0036
            android.location.Location r0 = r8.lastKnownLocation
            if (r0 != 0) goto L_0x0036
            return
        L_0x0036:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            long r1 = r8.dialog_id
            r0.append(r1)
            java.lang.String r1 = "_"
            r0.append(r1)
            r0.append(r10)
            r0.append(r1)
            r0.append(r11)
            r0.append(r1)
            long r2 = r8.dialog_id
            r0.append(r2)
            r0.append(r1)
            int r2 = r9.id
            r0.append(r2)
            r0.append(r1)
            boolean r1 = r9.bot_inline_geo
            r13 = -4571364728013586432(0xCLASSNAMEfNUM, double:-1000.0)
            if (r1 == 0) goto L_0x0088
            android.location.Location r1 = r8.lastKnownLocation
            if (r1 == 0) goto L_0x0088
            double r1 = r1.getLatitude()
            int r3 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x0088
            android.location.Location r1 = r8.lastKnownLocation
            double r1 = r1.getLatitude()
            android.location.Location r3 = r8.lastKnownLocation
            double r3 = r3.getLongitude()
            double r1 = r1 + r3
            java.lang.Double r1 = java.lang.Double.valueOf(r1)
            goto L_0x008a
        L_0x0088:
            java.lang.String r1 = ""
        L_0x008a:
            r0.append(r1)
            java.lang.String r15 = r0.toString()
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$7VOW9r6Q7UQLRFa7lqtsaj0SP0s r6 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$7VOW9r6Q7UQLRFa7lqtsaj0SP0s
            r0 = r6
            r1 = r16
            r2 = r19
            r3 = r17
            r4 = r18
            r5 = r20
            r12 = r6
            r6 = r7
            r13 = r7
            r7 = r15
            r0.<init>(r2, r3, r4, r5, r6, r7)
            if (r17 == 0) goto L_0x00b2
            r13.getBotCache(r15, r12)
            goto L_0x0129
        L_0x00b2:
            org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults r0 = new org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults
            r0.<init>()
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$InputUser r1 = r1.getInputUser((org.telegram.tgnet.TLRPC$User) r9)
            r0.bot = r1
            r0.query = r10
            r0.offset = r11
            boolean r1 = r9.bot_inline_geo
            if (r1 == 0) goto L_0x0103
            android.location.Location r1 = r8.lastKnownLocation
            if (r1 == 0) goto L_0x0103
            double r1 = r1.getLatitude()
            r3 = -4571364728013586432(0xCLASSNAMEfNUM, double:-1000.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0103
            int r1 = r0.flags
            r2 = 1
            r1 = r1 | r2
            r0.flags = r1
            org.telegram.tgnet.TLRPC$TL_inputGeoPoint r1 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint
            r1.<init>()
            r0.geo_point = r1
            android.location.Location r2 = r8.lastKnownLocation
            double r2 = r2.getLatitude()
            double r2 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r2)
            r1.lat = r2
            org.telegram.tgnet.TLRPC$InputGeoPoint r1 = r0.geo_point
            android.location.Location r2 = r8.lastKnownLocation
            double r2 = r2.getLongitude()
            double r2 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r2)
            r1._long = r2
        L_0x0103:
            long r1 = r8.dialog_id
            int r2 = (int) r1
            if (r2 == 0) goto L_0x0115
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer((int) r2)
            r0.peer = r1
            goto L_0x011c
        L_0x0115:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r1.<init>()
            r0.peer = r1
        L_0x011c:
            int r1 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            r2 = 2
            int r0 = r1.sendRequest(r0, r12, r2)
            r8.contextQueryReqid = r0
        L_0x0129:
            return
        L_0x012a:
            r0 = 0
            r8.searchingContextQuery = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchForContextBotResults(boolean, org.telegram.tgnet.TLRPC$User, java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchForContextBotResults$6 */
    public /* synthetic */ void lambda$searchForContextBotResults$6$MentionsAdapter(String str, boolean z, TLRPC$User tLRPC$User, String str2, MessagesStorage messagesStorage, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, z, tLObject, tLRPC$User, str2, messagesStorage, str3) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ TLRPC$User f$4;
            public final /* synthetic */ String f$5;
            public final /* synthetic */ MessagesStorage f$6;
            public final /* synthetic */ String f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                MentionsAdapter.this.lambda$searchForContextBotResults$5$MentionsAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchForContextBotResults$5 */
    public /* synthetic */ void lambda$searchForContextBotResults$5$MentionsAdapter(String str, boolean z, TLObject tLObject, TLRPC$User tLRPC$User, String str2, MessagesStorage messagesStorage, String str3) {
        boolean z2;
        if (str.equals(this.searchingContextQuery)) {
            boolean z3 = false;
            this.contextQueryReqid = 0;
            if (!z || tLObject != null) {
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (mentionsAdapterDelegate != null) {
                    mentionsAdapterDelegate.onContextSearch(false);
                }
            } else {
                searchForContextBotResults(false, tLRPC$User, str, str2);
            }
            if (tLObject instanceof TLRPC$TL_messages_botResults) {
                TLRPC$TL_messages_botResults tLRPC$TL_messages_botResults = (TLRPC$TL_messages_botResults) tLObject;
                if (!z && tLRPC$TL_messages_botResults.cache_time != 0) {
                    messagesStorage.saveBotCache(str3, tLRPC$TL_messages_botResults);
                }
                this.nextQueryOffset = tLRPC$TL_messages_botResults.next_offset;
                if (this.searchResultBotContextSwitch == null) {
                    this.searchResultBotContextSwitch = tLRPC$TL_messages_botResults.switch_pm;
                }
                int i = 0;
                while (i < tLRPC$TL_messages_botResults.results.size()) {
                    TLRPC$BotInlineResult tLRPC$BotInlineResult = tLRPC$TL_messages_botResults.results.get(i);
                    if (!(tLRPC$BotInlineResult.document instanceof TLRPC$TL_document) && !(tLRPC$BotInlineResult.photo instanceof TLRPC$TL_photo) && !"game".equals(tLRPC$BotInlineResult.type) && tLRPC$BotInlineResult.content == null && (tLRPC$BotInlineResult.send_message instanceof TLRPC$TL_botInlineMessageMediaAuto)) {
                        tLRPC$TL_messages_botResults.results.remove(i);
                        i--;
                    }
                    tLRPC$BotInlineResult.query_id = tLRPC$TL_messages_botResults.query_id;
                    i++;
                }
                if (this.searchResultBotContext == null || str2.length() == 0) {
                    this.searchResultBotContext = tLRPC$TL_messages_botResults.results;
                    this.contextMedia = tLRPC$TL_messages_botResults.gallery;
                    z2 = false;
                } else {
                    this.searchResultBotContext.addAll(tLRPC$TL_messages_botResults.results);
                    if (tLRPC$TL_messages_botResults.results.isEmpty()) {
                        this.nextQueryOffset = "";
                    }
                    z2 = true;
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
                if (z2) {
                    int i2 = this.searchResultBotContextSwitch != null ? 1 : 0;
                    notifyItemChanged(((this.searchResultBotContext.size() - tLRPC$TL_messages_botResults.results.size()) + i2) - 1);
                    notifyItemRangeInserted((this.searchResultBotContext.size() - tLRPC$TL_messages_botResults.results.size()) + i2, tLRPC$TL_messages_botResults.results.size());
                } else {
                    notifyDataSetChanged();
                }
                MentionsAdapterDelegate mentionsAdapterDelegate2 = this.delegate;
                if (!this.searchResultBotContext.isEmpty() || this.searchResultBotContextSwitch != null) {
                    z3 = true;
                }
                mentionsAdapterDelegate2.needChangePanelVisibility(z3);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v8, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v14, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v15, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x03e7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchUsernameOrHashtag(java.lang.String r21, int r22, java.util.ArrayList<org.telegram.messenger.MessageObject> r23, boolean r24, boolean r25) {
        /*
            r20 = this;
            r8 = r20
            r0 = r21
            r1 = r22
            r2 = r23
            r3 = r24
            r4 = r25
            java.lang.Runnable r5 = r8.cancelDelayRunnable
            r6 = 0
            if (r5 == 0) goto L_0x0016
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r5)
            r8.cancelDelayRunnable = r6
        L_0x0016:
            int r5 = r8.channelReqId
            r7 = 0
            r9 = 1
            if (r5 == 0) goto L_0x0029
            int r5 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)
            int r10 = r8.channelReqId
            r5.cancelRequest(r10, r9)
            r8.channelReqId = r7
        L_0x0029:
            java.lang.Runnable r5 = r8.searchGlobalRunnable
            if (r5 == 0) goto L_0x0032
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r5)
            r8.searchGlobalRunnable = r6
        L_0x0032:
            boolean r5 = android.text.TextUtils.isEmpty(r21)
            if (r5 != 0) goto L_0x0722
            int r5 = r21.length()
            int r10 = r8.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r10 = r10.maxMessageLength
            if (r5 <= r10) goto L_0x0048
            goto L_0x0722
        L_0x0048:
            int r5 = r21.length()
            if (r5 <= 0) goto L_0x0051
            int r5 = r1 + -1
            goto L_0x0052
        L_0x0051:
            r5 = r1
        L_0x0052:
            r8.lastText = r6
            r8.lastUsernameOnly = r3
            r8.lastForSearch = r4
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            if (r3 != 0) goto L_0x006f
            int r11 = r21.length()
            if (r11 <= 0) goto L_0x006f
            int r11 = r21.length()
            r12 = 14
            if (r11 > r12) goto L_0x006f
            r11 = 1
            goto L_0x0070
        L_0x006f:
            r11 = 0
        L_0x0070:
            java.lang.String r12 = ""
            if (r11 == 0) goto L_0x00f3
            int r14 = r21.length()
            r6 = r0
            r15 = 0
        L_0x007a:
            if (r15 >= r14) goto L_0x00e7
            char r9 = r6.charAt(r15)
            int r7 = r14 + -1
            if (r15 >= r7) goto L_0x008b
            int r13 = r15 + 1
            char r13 = r6.charAt(r13)
            goto L_0x008c
        L_0x008b:
            r13 = 0
        L_0x008c:
            if (r15 >= r7) goto L_0x00bd
            r7 = 55356(0xd83c, float:7.757E-41)
            if (r9 != r7) goto L_0x00bd
            r7 = 57339(0xdffb, float:8.0349E-41)
            if (r13 < r7) goto L_0x00bd
            r7 = 57343(0xdfff, float:8.0355E-41)
            if (r13 > r7) goto L_0x00bd
            r7 = 2
            java.lang.CharSequence[] r9 = new java.lang.CharSequence[r7]
            r7 = 0
            java.lang.CharSequence r13 = r6.subSequence(r7, r15)
            r9[r7] = r13
            int r7 = r15 + 2
            int r13 = r6.length()
            java.lang.CharSequence r6 = r6.subSequence(r7, r13)
            r7 = 1
            r9[r7] = r6
            java.lang.CharSequence r6 = android.text.TextUtils.concat(r9)
            int r14 = r14 + -2
            int r15 = r15 + -1
            goto L_0x00e2
        L_0x00bd:
            r7 = 65039(0xfe0f, float:9.1139E-41)
            if (r9 != r7) goto L_0x00e2
            r7 = 2
            java.lang.CharSequence[] r9 = new java.lang.CharSequence[r7]
            r7 = 0
            java.lang.CharSequence r13 = r6.subSequence(r7, r15)
            r9[r7] = r13
            int r7 = r15 + 1
            int r13 = r6.length()
            java.lang.CharSequence r6 = r6.subSequence(r7, r13)
            r7 = 1
            r9[r7] = r6
            java.lang.CharSequence r6 = android.text.TextUtils.concat(r9)
            int r14 = r14 + -1
            int r15 = r15 + -1
            goto L_0x00e3
        L_0x00e2:
            r7 = 1
        L_0x00e3:
            int r15 = r15 + r7
            r7 = 0
            r9 = 1
            goto L_0x007a
        L_0x00e7:
            java.lang.String r6 = r6.toString()
            java.lang.String r6 = r6.trim()
            r8.lastSticker = r6
            r6 = r0
            goto L_0x00f4
        L_0x00f3:
            r6 = r12
        L_0x00f4:
            if (r11 == 0) goto L_0x0106
            boolean r7 = org.telegram.messenger.Emoji.isValidEmoji(r6)
            if (r7 != 0) goto L_0x0104
            java.lang.String r7 = r8.lastSticker
            boolean r7 = org.telegram.messenger.Emoji.isValidEmoji(r7)
            if (r7 == 0) goto L_0x0106
        L_0x0104:
            r7 = 1
            goto L_0x0107
        L_0x0106:
            r7 = 0
        L_0x0107:
            if (r7 == 0) goto L_0x0249
            org.telegram.ui.ChatActivity r9 = r8.parentFragment
            if (r9 == 0) goto L_0x0249
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getCurrentChat()
            if (r9 == 0) goto L_0x011f
            org.telegram.ui.ChatActivity r9 = r8.parentFragment
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getCurrentChat()
            boolean r9 = org.telegram.messenger.ChatObject.canSendStickers(r9)
            if (r9 == 0) goto L_0x0249
        L_0x011f:
            java.util.ArrayList<java.lang.String> r9 = r8.stickersToLoad
            r9.clear()
            int r9 = org.telegram.messenger.SharedConfig.suggestStickers
            r12 = 2
            if (r9 == r12) goto L_0x0236
            if (r7 != 0) goto L_0x012d
            goto L_0x0236
        L_0x012d:
            r7 = 0
            r8.stickers = r7
            r8.stickersMap = r7
            int r7 = r8.lastReqId
            if (r7 == 0) goto L_0x0146
            int r7 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7)
            int r9 = r8.lastReqId
            r12 = 1
            r7.cancelRequest(r9, r12)
            r7 = 0
            r8.lastReqId = r7
            goto L_0x0147
        L_0x0146:
            r7 = 0
        L_0x0147:
            int r9 = r8.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            boolean r9 = r9.suggestStickersApiOnly
            r8.delayLocalResults = r7
            if (r9 != 0) goto L_0x01e2
            int r12 = r8.currentAccount
            org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
            java.util.ArrayList r12 = r12.getRecentStickersNoCopy(r7)
            int r7 = r8.currentAccount
            org.telegram.messenger.MediaDataController r7 = org.telegram.messenger.MediaDataController.getInstance(r7)
            r13 = 2
            java.util.ArrayList r7 = r7.getRecentStickersNoCopy(r13)
            r13 = 20
            int r14 = r12.size()
            int r13 = java.lang.Math.min(r13, r14)
            r14 = 0
            r17 = 0
        L_0x0175:
            if (r14 >= r13) goto L_0x0197
            java.lang.Object r18 = r12.get(r14)
            r11 = r18
            org.telegram.tgnet.TLRPC$Document r11 = (org.telegram.tgnet.TLRPC$Document) r11
            java.lang.String r15 = r8.lastSticker
            boolean r15 = r8.isValidSticker(r11, r15)
            if (r15 == 0) goto L_0x0194
            java.lang.String r15 = "recent"
            r8.addStickerToResult(r11, r15)
            int r11 = r17 + 1
            r15 = 5
            if (r11 < r15) goto L_0x0192
            goto L_0x0197
        L_0x0192:
            r17 = r11
        L_0x0194:
            int r14 = r14 + 1
            goto L_0x0175
        L_0x0197:
            int r11 = r7.size()
            r13 = 0
        L_0x019c:
            if (r13 >= r11) goto L_0x01b4
            java.lang.Object r14 = r7.get(r13)
            org.telegram.tgnet.TLRPC$Document r14 = (org.telegram.tgnet.TLRPC$Document) r14
            java.lang.String r15 = r8.lastSticker
            boolean r15 = r8.isValidSticker(r14, r15)
            if (r15 == 0) goto L_0x01b1
            java.lang.String r15 = "fav"
            r8.addStickerToResult(r14, r15)
        L_0x01b1:
            int r13 = r13 + 1
            goto L_0x019c
        L_0x01b4:
            int r11 = r8.currentAccount
            org.telegram.messenger.MediaDataController r11 = org.telegram.messenger.MediaDataController.getInstance(r11)
            java.util.HashMap r11 = r11.getAllStickers()
            if (r11 == 0) goto L_0x01c9
            java.lang.String r13 = r8.lastSticker
            java.lang.Object r11 = r11.get(r13)
            java.util.ArrayList r11 = (java.util.ArrayList) r11
            goto L_0x01ca
        L_0x01c9:
            r11 = 0
        L_0x01ca:
            if (r11 == 0) goto L_0x01d6
            boolean r13 = r11.isEmpty()
            if (r13 != 0) goto L_0x01d6
            r13 = 0
            r8.addStickersToResult(r11, r13)
        L_0x01d6:
            java.util.ArrayList<org.telegram.ui.Adapters.MentionsAdapter$StickerResult> r11 = r8.stickers
            if (r11 == 0) goto L_0x01e2
            org.telegram.ui.Adapters.MentionsAdapter$5 r13 = new org.telegram.ui.Adapters.MentionsAdapter$5
            r13.<init>(r7, r12)
            java.util.Collections.sort(r11, r13)
        L_0x01e2:
            int r7 = org.telegram.messenger.SharedConfig.suggestStickers
            if (r7 == 0) goto L_0x01e8
            if (r9 == 0) goto L_0x01ed
        L_0x01e8:
            java.lang.String r7 = r8.lastSticker
            r8.searchServerStickers(r7, r6)
        L_0x01ed:
            java.util.ArrayList<org.telegram.ui.Adapters.MentionsAdapter$StickerResult> r6 = r8.stickers
            if (r6 == 0) goto L_0x0225
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x0225
            int r6 = org.telegram.messenger.SharedConfig.suggestStickers
            if (r6 != 0) goto L_0x0210
            java.util.ArrayList<org.telegram.ui.Adapters.MentionsAdapter$StickerResult> r6 = r8.stickers
            int r6 = r6.size()
            r7 = 5
            if (r6 >= r7) goto L_0x0210
            r6 = 1
            r8.delayLocalResults = r6
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r6 = r8.delegate
            r7 = 0
            r6.needChangePanelVisibility(r7)
            r8.visibleByStickersSearch = r7
            goto L_0x0221
        L_0x0210:
            r20.checkStickerFilesExistAndDownload()
            java.util.ArrayList<java.lang.String> r6 = r8.stickersToLoad
            boolean r6 = r6.isEmpty()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r7 = r8.delegate
            r7.needChangePanelVisibility(r6)
            r6 = 1
            r8.visibleByStickersSearch = r6
        L_0x0221:
            r20.notifyDataSetChanged()
            goto L_0x0232
        L_0x0225:
            boolean r6 = r8.visibleByStickersSearch
            if (r6 == 0) goto L_0x0232
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r6 = r8.delegate
            r7 = 0
            r6.needChangePanelVisibility(r7)
            r8.visibleByStickersSearch = r7
            goto L_0x0233
        L_0x0232:
            r7 = 0
        L_0x0233:
            r6 = 4
            goto L_0x02db
        L_0x0236:
            r7 = 0
            boolean r0 = r8.visibleByStickersSearch
            if (r0 == 0) goto L_0x0248
            r0 = 2
            if (r9 != r0) goto L_0x0248
            r8.visibleByStickersSearch = r7
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r0.needChangePanelVisibility(r7)
            r20.notifyDataSetChanged()
        L_0x0248:
            return
        L_0x0249:
            r7 = 0
            if (r3 != 0) goto L_0x02d6
            boolean r6 = r8.needBotContext
            if (r6 == 0) goto L_0x02d6
            char r6 = r0.charAt(r7)
            r7 = 64
            if (r6 != r7) goto L_0x02d6
            r6 = 32
            int r7 = r0.indexOf(r6)
            int r6 = r21.length()
            if (r7 <= 0) goto L_0x0272
            r9 = 1
            java.lang.String r6 = r0.substring(r9, r7)
            int r7 = r7 + r9
            java.lang.String r7 = r0.substring(r7)
            r9 = r7
            r7 = r6
            r6 = 1
            goto L_0x029e
        L_0x0272:
            int r7 = r6 + -1
            char r7 = r0.charAt(r7)
            r9 = 116(0x74, float:1.63E-43)
            if (r7 != r9) goto L_0x0297
            int r7 = r6 + -2
            char r7 = r0.charAt(r7)
            r9 = 111(0x6f, float:1.56E-43)
            if (r7 != r9) goto L_0x0297
            r7 = 3
            int r6 = r6 - r7
            char r6 = r0.charAt(r6)
            r7 = 98
            if (r6 != r7) goto L_0x0297
            r6 = 1
            java.lang.String r7 = r0.substring(r6)
            r9 = r12
            goto L_0x029e
        L_0x0297:
            r6 = 1
            r7 = 0
            r8.searchForContextBot(r7, r7)
            r7 = 0
            r9 = 0
        L_0x029e:
            if (r7 == 0) goto L_0x02d2
            int r11 = r7.length()
            if (r11 < r6) goto L_0x02d2
            r6 = 1
        L_0x02a7:
            int r11 = r7.length()
            if (r6 >= r11) goto L_0x02d1
            char r11 = r7.charAt(r6)
            r13 = 48
            if (r11 < r13) goto L_0x02b9
            r13 = 57
            if (r11 <= r13) goto L_0x02ce
        L_0x02b9:
            r13 = 97
            if (r11 < r13) goto L_0x02c1
            r13 = 122(0x7a, float:1.71E-43)
            if (r11 <= r13) goto L_0x02ce
        L_0x02c1:
            r13 = 65
            if (r11 < r13) goto L_0x02c9
            r13 = 90
            if (r11 <= r13) goto L_0x02ce
        L_0x02c9:
            r13 = 95
            if (r11 == r13) goto L_0x02ce
            goto L_0x02d2
        L_0x02ce:
            int r6 = r6 + 1
            goto L_0x02a7
        L_0x02d1:
            r12 = r7
        L_0x02d2:
            r8.searchForContextBot(r12, r9)
            goto L_0x02da
        L_0x02d6:
            r6 = 0
            r8.searchForContextBot(r6, r6)
        L_0x02da:
            r6 = -1
        L_0x02db:
            org.telegram.tgnet.TLRPC$User r7 = r8.foundContextBot
            if (r7 == 0) goto L_0x02e0
            return
        L_0x02e0:
            int r7 = r8.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            if (r3 == 0) goto L_0x02ff
            r9 = 1
            java.lang.String r0 = r0.substring(r9)
            r10.append(r0)
            r0 = 0
            r8.resultStartPosition = r0
            int r0 = r10.length()
            r8.resultLength = r0
            r0 = -1
            r5 = -1
        L_0x02fb:
            r6 = 0
        L_0x02fc:
            r11 = 0
            goto L_0x03df
        L_0x02ff:
            if (r5 < 0) goto L_0x03dc
            int r9 = r21.length()
            if (r5 < r9) goto L_0x030c
            r11 = 0
            r12 = 64
            goto L_0x03d8
        L_0x030c:
            char r9 = r0.charAt(r5)
            r11 = 58
            if (r5 == 0) goto L_0x032e
            int r12 = r5 + -1
            char r13 = r0.charAt(r12)
            r14 = 32
            if (r13 == r14) goto L_0x032e
            char r12 = r0.charAt(r12)
            r13 = 10
            if (r12 == r13) goto L_0x032e
            if (r9 != r11) goto L_0x0329
            goto L_0x032e
        L_0x0329:
            r11 = 0
            r12 = 64
            goto L_0x03d5
        L_0x032e:
            r12 = 64
            if (r9 != r12) goto L_0x035f
            boolean r11 = r8.needUsernames
            if (r11 != 0) goto L_0x0340
            boolean r11 = r8.needBotContext
            if (r11 == 0) goto L_0x033d
            if (r5 != 0) goto L_0x033d
            goto L_0x0340
        L_0x033d:
            r11 = 0
            goto L_0x03d5
        L_0x0340:
            org.telegram.tgnet.TLRPC$ChatFull r6 = r8.info
            if (r6 != 0) goto L_0x0353
            if (r5 == 0) goto L_0x0353
            r8.lastText = r0
            r8.lastPosition = r1
            r8.messages = r2
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r1 = 0
            r0.needChangePanelVisibility(r1)
            return
        L_0x0353:
            r8.resultStartPosition = r5
            int r0 = r10.length()
            r13 = 1
            int r0 = r0 + r13
            r8.resultLength = r0
            r0 = -1
            goto L_0x02fb
        L_0x035f:
            r13 = 1
            r14 = 35
            if (r9 != r14) goto L_0x038b
            org.telegram.ui.Adapters.SearchAdapterHelper r6 = r8.searchAdapterHelper
            boolean r6 = r6.loadRecentHashtags()
            if (r6 == 0) goto L_0x037e
            r8.resultStartPosition = r5
            int r0 = r10.length()
            int r0 = r0 + r13
            r8.resultLength = r0
            r5 = 0
            r10.insert(r5, r9)
            r0 = -1
            r5 = -1
            r6 = 1
            goto L_0x02fc
        L_0x037e:
            r5 = 0
            r8.lastText = r0
            r8.lastPosition = r1
            r8.messages = r2
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r0.needChangePanelVisibility(r5)
            return
        L_0x038b:
            if (r5 != 0) goto L_0x03a4
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r13 = r8.botInfo
            if (r13 == 0) goto L_0x03a4
            r13 = 47
            if (r9 != r13) goto L_0x03a4
            r8.resultStartPosition = r5
            int r0 = r10.length()
            r1 = 1
            int r0 = r0 + r1
            r8.resultLength = r0
            r0 = -1
            r5 = -1
            r6 = 2
            goto L_0x02fc
        L_0x03a4:
            if (r9 != r11) goto L_0x033d
            int r11 = r10.length()
            if (r11 <= 0) goto L_0x033d
            r11 = 0
            char r13 = r10.charAt(r11)
            java.lang.String r11 = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n"
            int r11 = r11.indexOf(r13)
            if (r11 < 0) goto L_0x03bb
            r11 = 1
            goto L_0x03bc
        L_0x03bb:
            r11 = 0
        L_0x03bc:
            if (r11 == 0) goto L_0x03c6
            int r11 = r10.length()
            r13 = 1
            if (r11 <= r13) goto L_0x033d
            goto L_0x03c7
        L_0x03c6:
            r13 = 1
        L_0x03c7:
            r8.resultStartPosition = r5
            int r0 = r10.length()
            int r0 = r0 + r13
            r8.resultLength = r0
            r0 = -1
            r5 = -1
            r6 = 3
            goto L_0x02fc
        L_0x03d5:
            r10.insert(r11, r9)
        L_0x03d8:
            int r5 = r5 + -1
            goto L_0x02ff
        L_0x03dc:
            r11 = 0
            r0 = -1
            r5 = -1
        L_0x03df:
            if (r6 != r0) goto L_0x03e7
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r0.needChangePanelVisibility(r11)
            return
        L_0x03e7:
            if (r6 != 0) goto L_0x05f1
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
        L_0x03ef:
            r6 = 100
            int r9 = r23.size()
            int r6 = java.lang.Math.min(r6, r9)
            if (r1 >= r6) goto L_0x041b
            java.lang.Object r6 = r2.get(r1)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            int r6 = r6.getFromChatId()
            if (r6 <= 0) goto L_0x0418
            java.lang.Integer r9 = java.lang.Integer.valueOf(r6)
            boolean r9 = r0.contains(r9)
            if (r9 != 0) goto L_0x0418
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r0.add(r6)
        L_0x0418:
            int r1 = r1 + 1
            goto L_0x03ef
        L_0x041b:
            java.lang.String r1 = r10.toString()
            java.lang.String r6 = r1.toLowerCase()
            r1 = 32
            int r1 = r6.indexOf(r1)
            if (r1 < 0) goto L_0x042d
            r1 = 1
            goto L_0x042e
        L_0x042d:
            r1 = 0
        L_0x042e:
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            android.util.SparseArray r10 = new android.util.SparseArray
            r10.<init>()
            int r11 = r8.currentAccount
            org.telegram.messenger.MediaDataController r11 = org.telegram.messenger.MediaDataController.getInstance(r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r11 = r11.inlineBots
            if (r3 != 0) goto L_0x04a0
            boolean r12 = r8.needBotContext
            if (r12 == 0) goto L_0x04a0
            if (r5 != 0) goto L_0x04a0
            boolean r5 = r11.isEmpty()
            if (r5 != 0) goto L_0x04a0
            r5 = 0
            r12 = 0
        L_0x0455:
            int r13 = r11.size()
            if (r5 >= r13) goto L_0x04a0
            java.lang.Object r13 = r11.get(r5)
            org.telegram.tgnet.TLRPC$TL_topPeer r13 = (org.telegram.tgnet.TLRPC$TL_topPeer) r13
            org.telegram.tgnet.TLRPC$Peer r13 = r13.peer
            int r13 = r13.user_id
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r13 = r7.getUser(r13)
            if (r13 != 0) goto L_0x0470
            goto L_0x049d
        L_0x0470:
            java.lang.String r14 = r13.username
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 != 0) goto L_0x0499
            int r14 = r6.length()
            if (r14 == 0) goto L_0x048a
            java.lang.String r14 = r13.username
            java.lang.String r14 = r14.toLowerCase()
            boolean r14 = r14.startsWith(r6)
            if (r14 == 0) goto L_0x0499
        L_0x048a:
            r9.add(r13)
            int r14 = r13.id
            r2.put(r14, r13)
            int r14 = r13.id
            r10.put(r14, r13)
            int r12 = r12 + 1
        L_0x0499:
            r13 = 5
            if (r12 != r13) goto L_0x049d
            goto L_0x04a0
        L_0x049d:
            int r5 = r5 + 1
            goto L_0x0455
        L_0x04a0:
            org.telegram.ui.ChatActivity r5 = r8.parentFragment
            if (r5 == 0) goto L_0x04af
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getCurrentChat()
            org.telegram.ui.ChatActivity r11 = r8.parentFragment
            int r11 = r11.getThreadId()
            goto L_0x04c0
        L_0x04af:
            org.telegram.tgnet.TLRPC$ChatFull r5 = r8.info
            if (r5 == 0) goto L_0x04be
            int r5 = r5.id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r5 = r7.getChat(r5)
            goto L_0x04bf
        L_0x04be:
            r5 = 0
        L_0x04bf:
            r11 = 0
        L_0x04c0:
            if (r5 == 0) goto L_0x059c
            org.telegram.tgnet.TLRPC$ChatFull r12 = r8.info
            if (r12 == 0) goto L_0x059c
            org.telegram.tgnet.TLRPC$ChatParticipants r12 = r12.participants
            if (r12 == 0) goto L_0x059c
            boolean r12 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r12 == 0) goto L_0x04d4
            boolean r12 = r5.megagroup
            if (r12 == 0) goto L_0x059c
        L_0x04d4:
            if (r4 == 0) goto L_0x04d9
            r16 = -1
            goto L_0x04db
        L_0x04d9:
            r16 = 0
        L_0x04db:
            r4 = r16
        L_0x04dd:
            org.telegram.tgnet.TLRPC$ChatFull r12 = r8.info
            org.telegram.tgnet.TLRPC$ChatParticipants r12 = r12.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r12 = r12.participants
            int r12 = r12.size()
            if (r4 >= r12) goto L_0x059c
            r12 = -1
            if (r4 != r12) goto L_0x0503
            int r13 = r6.length()
            if (r13 != 0) goto L_0x04f7
            r9.add(r5)
            goto L_0x0594
        L_0x04f7:
            java.lang.String r13 = r5.title
            java.lang.String r14 = r5.username
            int r15 = r5.id
            int r15 = -r15
            r21 = r2
            r2 = r5
            r12 = 0
            goto L_0x054d
        L_0x0503:
            org.telegram.tgnet.TLRPC$ChatFull r13 = r8.info
            org.telegram.tgnet.TLRPC$ChatParticipants r13 = r13.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r13 = r13.participants
            java.lang.Object r13 = r13.get(r4)
            org.telegram.tgnet.TLRPC$ChatParticipant r13 = (org.telegram.tgnet.TLRPC$ChatParticipant) r13
            int r13 = r13.user_id
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r13 = r7.getUser(r13)
            if (r13 == 0) goto L_0x0594
            if (r3 != 0) goto L_0x0523
            boolean r14 = org.telegram.messenger.UserObject.isUserSelf(r13)
            if (r14 != 0) goto L_0x0594
        L_0x0523:
            int r14 = r13.id
            int r14 = r2.indexOfKey(r14)
            if (r14 < 0) goto L_0x052d
            goto L_0x0594
        L_0x052d:
            int r14 = r6.length()
            if (r14 != 0) goto L_0x053b
            boolean r14 = r13.deleted
            if (r14 != 0) goto L_0x053b
            r9.add(r13)
            goto L_0x0594
        L_0x053b:
            java.lang.String r14 = r13.first_name
            java.lang.String r15 = r13.last_name
            java.lang.String r12 = r13.username
            r21 = r2
            int r2 = r13.id
            r19 = r15
            r15 = r2
            r2 = r13
            r13 = r14
            r14 = r12
            r12 = r19
        L_0x054d:
            boolean r16 = android.text.TextUtils.isEmpty(r14)
            if (r16 != 0) goto L_0x055d
            java.lang.String r14 = r14.toLowerCase()
            boolean r14 = r14.startsWith(r6)
            if (r14 != 0) goto L_0x058d
        L_0x055d:
            boolean r14 = android.text.TextUtils.isEmpty(r13)
            if (r14 != 0) goto L_0x056d
            java.lang.String r14 = r13.toLowerCase()
            boolean r14 = r14.startsWith(r6)
            if (r14 != 0) goto L_0x058d
        L_0x056d:
            boolean r14 = android.text.TextUtils.isEmpty(r12)
            if (r14 != 0) goto L_0x057d
            java.lang.String r14 = r12.toLowerCase()
            boolean r14 = r14.startsWith(r6)
            if (r14 != 0) goto L_0x058d
        L_0x057d:
            if (r1 == 0) goto L_0x0596
            java.lang.String r12 = org.telegram.messenger.ContactsController.formatName(r13, r12)
            java.lang.String r12 = r12.toLowerCase()
            boolean r12 = r12.startsWith(r6)
            if (r12 == 0) goto L_0x0596
        L_0x058d:
            r9.add(r2)
            r10.put(r15, r2)
            goto L_0x0596
        L_0x0594:
            r21 = r2
        L_0x0596:
            int r4 = r4 + 1
            r2 = r21
            goto L_0x04dd
        L_0x059c:
            org.telegram.ui.Adapters.MentionsAdapter$6 r1 = new org.telegram.ui.Adapters.MentionsAdapter$6
            r1.<init>(r10, r0)
            java.util.Collections.sort(r9, r1)
            r0 = 0
            r8.searchResultHashtags = r0
            r8.stickers = r0
            r8.searchResultCommands = r0
            r8.searchResultCommandsHelp = r0
            r8.searchResultCommandsUsers = r0
            r8.searchResultSuggestions = r0
            if (r5 == 0) goto L_0x05eb
            boolean r0 = r5.megagroup
            if (r0 == 0) goto L_0x05eb
            int r0 = r6.length()
            if (r0 <= 0) goto L_0x05eb
            int r0 = r9.size()
            r1 = 5
            if (r0 >= r1) goto L_0x05d1
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$icr0qLuv4y7kGI8GqDkSgnx2gXE r0 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$icr0qLuv4y7kGI8GqDkSgnx2gXE
            r0.<init>(r9, r10)
            r8.cancelDelayRunnable = r0
            r1 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
            goto L_0x05d5
        L_0x05d1:
            r0 = 1
            r8.showUsersResult(r9, r10, r0)
        L_0x05d5:
            org.telegram.ui.Adapters.MentionsAdapter$7 r12 = new org.telegram.ui.Adapters.MentionsAdapter$7
            r0 = r12
            r1 = r20
            r2 = r5
            r3 = r6
            r4 = r11
            r5 = r9
            r6 = r10
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.searchGlobalRunnable = r12
            r0 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12, r0)
            goto L_0x0721
        L_0x05eb:
            r0 = 1
            r8.showUsersResult(r9, r10, r0)
            goto L_0x0721
        L_0x05f1:
            r0 = 1
            if (r6 != r0) goto L_0x0649
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r1 = r10.toString()
            java.lang.String r1 = r1.toLowerCase()
            org.telegram.ui.Adapters.SearchAdapterHelper r2 = r8.searchAdapterHelper
            java.util.ArrayList r2 = r2.getHashtags()
            r7 = 0
        L_0x0608:
            int r3 = r2.size()
            if (r7 >= r3) goto L_0x0628
            java.lang.Object r3 = r2.get(r7)
            org.telegram.ui.Adapters.SearchAdapterHelper$HashtagObject r3 = (org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject) r3
            if (r3 == 0) goto L_0x0625
            java.lang.String r4 = r3.hashtag
            if (r4 == 0) goto L_0x0625
            boolean r4 = r4.startsWith(r1)
            if (r4 == 0) goto L_0x0625
            java.lang.String r3 = r3.hashtag
            r0.add(r3)
        L_0x0625:
            int r7 = r7 + 1
            goto L_0x0608
        L_0x0628:
            r8.searchResultHashtags = r0
            r1 = 0
            r8.stickers = r1
            r8.searchResultUsernames = r1
            r8.searchResultUsernamesMap = r1
            r8.searchResultCommands = r1
            r8.searchResultCommandsHelp = r1
            r8.searchResultCommandsUsers = r1
            r8.searchResultSuggestions = r1
            r20.notifyDataSetChanged()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            boolean r0 = r0.isEmpty()
            r2 = 1
            r0 = r0 ^ r2
            r1.needChangePanelVisibility(r0)
            goto L_0x0721
        L_0x0649:
            r0 = 2
            if (r6 != r0) goto L_0x06df
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.lang.String r3 = r10.toString()
            java.lang.String r3 = r3.toLowerCase()
            r4 = 0
        L_0x0664:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r8.botInfo
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x06bf
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r8.botInfo
            java.lang.Object r5 = r5.valueAt(r4)
            org.telegram.tgnet.TLRPC$BotInfo r5 = (org.telegram.tgnet.TLRPC$BotInfo) r5
            r6 = 0
        L_0x0675:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_botCommand> r9 = r5.commands
            int r9 = r9.size()
            if (r6 >= r9) goto L_0x06bc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_botCommand> r9 = r5.commands
            java.lang.Object r9 = r9.get(r6)
            org.telegram.tgnet.TLRPC$TL_botCommand r9 = (org.telegram.tgnet.TLRPC$TL_botCommand) r9
            if (r9 == 0) goto L_0x06b9
            java.lang.String r10 = r9.command
            if (r10 == 0) goto L_0x06b9
            boolean r10 = r10.startsWith(r3)
            if (r10 == 0) goto L_0x06b9
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "/"
            r10.append(r11)
            java.lang.String r11 = r9.command
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r0.add(r10)
            java.lang.String r9 = r9.description
            r1.add(r9)
            int r9 = r5.user_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r9 = r7.getUser(r9)
            r2.add(r9)
        L_0x06b9:
            int r6 = r6 + 1
            goto L_0x0675
        L_0x06bc:
            int r4 = r4 + 1
            goto L_0x0664
        L_0x06bf:
            r4 = 0
            r8.searchResultHashtags = r4
            r8.stickers = r4
            r8.searchResultUsernames = r4
            r8.searchResultUsernamesMap = r4
            r8.searchResultSuggestions = r4
            r8.searchResultCommands = r0
            r8.searchResultCommandsHelp = r1
            r8.searchResultCommandsUsers = r2
            r20.notifyDataSetChanged()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            boolean r0 = r0.isEmpty()
            r2 = 1
            r0 = r0 ^ r2
            r1.needChangePanelVisibility(r0)
            goto L_0x0721
        L_0x06df:
            r0 = 3
            if (r6 != r0) goto L_0x070f
            java.lang.String[] r0 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
            java.lang.String[] r1 = r8.lastSearchKeyboardLanguage
            boolean r1 = java.util.Arrays.equals(r0, r1)
            if (r1 != 0) goto L_0x06f7
            int r1 = r8.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.fetchNewEmojiKeywords(r0)
        L_0x06f7:
            r8.lastSearchKeyboardLanguage = r0
            int r0 = r8.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.lang.String[] r1 = r8.lastSearchKeyboardLanguage
            java.lang.String r2 = r10.toString()
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$eNDAm_YfK9yBjS93edRWWrG_HHU r3 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$eNDAm_YfK9yBjS93edRWWrG_HHU
            r3.<init>()
            r4 = 0
            r0.getEmojiSuggestions(r1, r2, r4, r3)
            goto L_0x0721
        L_0x070f:
            r0 = 4
            if (r6 != r0) goto L_0x0721
            r0 = 0
            r8.searchResultHashtags = r0
            r8.searchResultUsernames = r0
            r8.searchResultUsernamesMap = r0
            r8.searchResultSuggestions = r0
            r8.searchResultCommands = r0
            r8.searchResultCommandsHelp = r0
            r8.searchResultCommandsUsers = r0
        L_0x0721:
            return
        L_0x0722:
            r0 = r6
            r8.searchForContextBot(r0, r0)
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            r2 = 0
            r1.needChangePanelVisibility(r2)
            r8.lastText = r0
            r20.clearStickers()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchUsernameOrHashtag(java.lang.String, int, java.util.ArrayList, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchUsernameOrHashtag$7 */
    public /* synthetic */ void lambda$searchUsernameOrHashtag$7$MentionsAdapter(ArrayList arrayList, SparseArray sparseArray) {
        this.cancelDelayRunnable = null;
        showUsersResult(arrayList, sparseArray, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchUsernameOrHashtag$8 */
    public /* synthetic */ void lambda$searchUsernameOrHashtag$8$MentionsAdapter(ArrayList arrayList, String str) {
        this.searchResultSuggestions = arrayList;
        this.searchResultHashtags = null;
        this.stickers = null;
        this.searchResultUsernames = null;
        this.searchResultUsernamesMap = null;
        this.searchResultCommands = null;
        this.searchResultCommandsHelp = null;
        this.searchResultCommandsUsers = null;
        notifyDataSetChanged();
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        ArrayList<MediaDataController.KeywordResult> arrayList2 = this.searchResultSuggestions;
        mentionsAdapterDelegate.needChangePanelVisibility(arrayList2 != null && !arrayList2.isEmpty());
    }

    /* access modifiers changed from: private */
    public void showUsersResult(ArrayList<TLObject> arrayList, SparseArray<TLObject> sparseArray, boolean z) {
        this.searchResultUsernames = arrayList;
        this.searchResultUsernamesMap = sparseArray;
        Runnable runnable = this.cancelDelayRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.cancelDelayRunnable = null;
        }
        if (z) {
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

    public ArrayList<TLRPC$BotInlineResult> getSearchResultBotContext() {
        return this.searchResultBotContext;
    }

    public int getItemCount() {
        int i = 1;
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 1;
        }
        ArrayList<StickerResult> arrayList = this.stickers;
        if (arrayList != null) {
            return arrayList.size();
        }
        ArrayList<TLRPC$BotInlineResult> arrayList2 = this.searchResultBotContext;
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

    public int getItemViewType(int i) {
        if (this.stickers != null) {
            return 4;
        }
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 3;
        }
        if (this.searchResultBotContext != null) {
            return (i != 0 || this.searchResultBotContextSwitch == null) ? 1 : 2;
        }
        return 0;
    }

    public void addHashtagsFromMessage(CharSequence charSequence) {
        this.searchAdapterHelper.addHashtagsFromMessage(charSequence);
    }

    public int getItemPosition(int i) {
        return (this.searchResultBotContext == null || this.searchResultBotContextSwitch == null) ? i : i - 1;
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
            ArrayList<TLRPC$BotInlineResult> arrayList2 = this.searchResultBotContext;
            if (arrayList2 != null) {
                TLRPC$TL_inlineBotSwitchPM tLRPC$TL_inlineBotSwitchPM = this.searchResultBotContextSwitch;
                if (tLRPC$TL_inlineBotSwitchPM != null) {
                    if (i == 0) {
                        return tLRPC$TL_inlineBotSwitchPM;
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
                        ArrayList<TLRPC$User> arrayList7 = this.searchResultCommandsUsers;
                        if (arrayList7 == null || (this.botsCount == 1 && !(this.info instanceof TLRPC$TL_channelFull))) {
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

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return (this.foundContextBot == null || this.inlineMediaEnabled) && this.stickers == null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateViewHolder$9 */
    public /* synthetic */ void lambda$onCreateViewHolder$9$MentionsAdapter(ContextLinkCell contextLinkCell) {
        this.delegate.onContextClick(contextLinkCell.getResult());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.ui.Cells.StickerCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.ui.Cells.ContextLinkCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: android.widget.TextView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: org.telegram.ui.Cells.StickerCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: org.telegram.ui.Cells.MentionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: org.telegram.ui.Cells.MentionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: org.telegram.ui.Cells.BotSwitchCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: org.telegram.ui.Cells.StickerCell} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
        /*
            r4 = this;
            if (r6 == 0) goto L_0x0057
            r5 = 1
            if (r6 == r5) goto L_0x0047
            r0 = 2
            if (r6 == r0) goto L_0x003f
            r0 = 3
            if (r6 == r0) goto L_0x0013
            org.telegram.ui.Cells.StickerCell r5 = new org.telegram.ui.Cells.StickerCell
            android.content.Context r6 = r4.mContext
            r5.<init>(r6)
            goto L_0x0063
        L_0x0013:
            android.widget.TextView r6 = new android.widget.TextView
            android.content.Context r0 = r4.mContext
            r6.<init>(r0)
            r0 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.setPadding(r1, r2, r3, r0)
            r0 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r5, r0)
            java.lang.String r5 = "windowBackgroundWhiteGrayText2"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r6.setTextColor(r5)
            r5 = r6
            goto L_0x0063
        L_0x003f:
            org.telegram.ui.Cells.BotSwitchCell r5 = new org.telegram.ui.Cells.BotSwitchCell
            android.content.Context r6 = r4.mContext
            r5.<init>(r6)
            goto L_0x0063
        L_0x0047:
            org.telegram.ui.Cells.ContextLinkCell r5 = new org.telegram.ui.Cells.ContextLinkCell
            android.content.Context r6 = r4.mContext
            r5.<init>(r6)
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$d4dl4gkfAu-9v-PITGgHYSG5VBQ r6 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$d4dl4gkfAu-9v-PITGgHYSG5VBQ
            r6.<init>()
            r5.setDelegate(r6)
            goto L_0x0063
        L_0x0057:
            org.telegram.ui.Cells.MentionCell r5 = new org.telegram.ui.Cells.MentionCell
            android.content.Context r6 = r4.mContext
            r5.<init>(r6)
            boolean r6 = r4.isDarkTheme
            r5.setIsDarkTheme(r6)
        L_0x0063:
            org.telegram.ui.Components.RecyclerListView$Holder r6 = new org.telegram.ui.Components.RecyclerListView$Holder
            r6.<init>(r5)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 4) {
            StickerCell stickerCell = (StickerCell) viewHolder.itemView;
            StickerResult stickerResult = this.stickers.get(i);
            stickerCell.setSticker(stickerResult.sticker, stickerResult.parent);
            stickerCell.setClearsInputField(true);
        } else if (itemViewType == 3) {
            TextView textView = (TextView) viewHolder.itemView;
            TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
            if (currentChat == null) {
                return;
            }
            if (!ChatObject.hasAdminRights(currentChat) && (tLRPC$TL_chatBannedRights = currentChat.default_banned_rights) != null && tLRPC$TL_chatBannedRights.send_inline) {
                textView.setText(LocaleController.getString("GlobalAttachInlineRestricted", NUM));
            } else if (AndroidUtilities.isBannedForever(currentChat.banned_rights)) {
                textView.setText(LocaleController.getString("AttachInlineRestrictedForever", NUM));
            } else {
                textView.setText(LocaleController.formatString("AttachInlineRestricted", NUM, LocaleController.formatDateForBan((long) currentChat.banned_rights.until_date)));
            }
        } else if (this.searchResultBotContext != null) {
            boolean z = this.searchResultBotContextSwitch != null;
            if (viewHolder.getItemViewType() != 2) {
                if (z) {
                    i--;
                }
                ((ContextLinkCell) viewHolder.itemView).setLink(this.searchResultBotContext.get(i), this.foundContextBot, this.contextMedia, i != this.searchResultBotContext.size() - 1, z && i == 0, "gif".equals(this.searchingContextUsername));
            } else if (z) {
                ((BotSwitchCell) viewHolder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else {
            ArrayList<TLObject> arrayList = this.searchResultUsernames;
            if (arrayList != null) {
                TLObject tLObject = arrayList.get(i);
                if (tLObject instanceof TLRPC$User) {
                    ((MentionCell) viewHolder.itemView).setUser((TLRPC$User) tLObject);
                } else if (tLObject instanceof TLRPC$Chat) {
                    ((MentionCell) viewHolder.itemView).setChat((TLRPC$Chat) tLObject);
                }
            } else {
                ArrayList<String> arrayList2 = this.searchResultHashtags;
                if (arrayList2 != null) {
                    ((MentionCell) viewHolder.itemView).setText(arrayList2.get(i));
                    return;
                }
                ArrayList<MediaDataController.KeywordResult> arrayList3 = this.searchResultSuggestions;
                if (arrayList3 != null) {
                    ((MentionCell) viewHolder.itemView).setEmojiSuggestion(arrayList3.get(i));
                    return;
                }
                ArrayList<String> arrayList4 = this.searchResultCommands;
                if (arrayList4 != null) {
                    MentionCell mentionCell = (MentionCell) viewHolder.itemView;
                    String str = arrayList4.get(i);
                    String str2 = this.searchResultCommandsHelp.get(i);
                    ArrayList<TLRPC$User> arrayList5 = this.searchResultCommandsUsers;
                    mentionCell.setBotCommand(str, str2, arrayList5 != null ? arrayList5.get(i) : null);
                }
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        TLRPC$User tLRPC$User;
        if (i == 2 && (tLRPC$User = this.foundContextBot) != null && tLRPC$User.bot_inline_geo) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                onLocationUnavailable();
            } else {
                this.locationProvider.start();
            }
        }
    }
}
