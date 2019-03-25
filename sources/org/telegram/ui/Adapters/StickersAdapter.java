package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DataQuery.KeywordResult;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getStickers;
import org.telegram.tgnet.TLRPC.TL_messages_stickers;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.ui.Cells.EmojiReplacementCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class StickersAdapter extends SelectionAdapter implements NotificationCenterDelegate {
    private int currentAccount = UserConfig.selectedAccount;
    private boolean delayLocalResults;
    private StickersAdapterDelegate delegate;
    private ArrayList<KeywordResult> keywordResults;
    private int lastReqId;
    private String[] lastSearchKeyboardLanguage;
    private String lastSticker;
    private Context mContext;
    private Runnable searchRunnable;
    private ArrayList<Document> stickers;
    private HashMap<String, Document> stickersMap;
    private ArrayList<Object> stickersParents;
    private ArrayList<String> stickersToLoad = new ArrayList();
    private boolean visible;

    public interface StickersAdapterDelegate {
        void needChangePanelVisibility(boolean z);
    }

    public StickersAdapter(Context context, StickersAdapterDelegate delegate) {
        this.mContext = context;
        this.delegate = delegate;
        DataQuery.getInstance(this.currentAccount).checkStickers(0);
        DataQuery.getInstance(this.currentAccount).checkStickers(1);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailedLoad);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailedLoad);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        boolean show = false;
        if (id == NotificationCenter.fileDidLoad || id == NotificationCenter.fileDidFailedLoad) {
            if (this.stickers != null && !this.stickers.isEmpty() && !this.stickersToLoad.isEmpty() && this.visible) {
                this.stickersToLoad.remove(args[0]);
                if (this.stickersToLoad.isEmpty()) {
                    if (!(this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty())) {
                        show = true;
                    }
                    if (show) {
                        this.keywordResults = null;
                    }
                    this.delegate.needChangePanelVisibility(show);
                }
            }
        } else if (id != NotificationCenter.newEmojiSuggestionsAvailable) {
        } else {
            if ((this.keywordResults == null || this.keywordResults.isEmpty()) && !TextUtils.isEmpty(this.lastSticker) && getItemCount() == 0) {
                searchEmojiByKeyword();
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
            PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(((Document) this.stickers.get(a)).thumbs, 90);
            if ((thumb instanceof TL_photoSize) && !FileLoader.getPathToAttach(thumb, "webp", true).exists()) {
                this.stickersToLoad.add(FileLoader.getAttachFileName(thumb, "webp"));
                FileLoader.getInstance(this.currentAccount).loadFile(thumb.location, this.stickersParents.get(a), "webp", 0, 1, 1);
            }
        }
        return this.stickersToLoad.isEmpty();
    }

    private boolean isValidSticker(Document document, String emoji) {
        int size2 = document.attributes.size();
        for (int b = 0; b < size2; b++) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(b);
            if (attribute instanceof TL_documentAttributeSticker) {
                if (attribute.alt != null && attribute.alt.contains(emoji)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private void addStickerToResult(Document document, Object parent) {
        if (document != null) {
            String key = document.dc_id + "_" + document.id;
            if (this.stickersMap == null || !this.stickersMap.containsKey(key)) {
                if (this.stickers == null) {
                    this.stickers = new ArrayList();
                    this.stickersParents = new ArrayList();
                    this.stickersMap = new HashMap();
                }
                this.stickers.add(document);
                this.stickersParents.add(parent);
                this.stickersMap.put(key, document);
            }
        }
    }

    private void addStickersToResult(ArrayList<Document> documents, Object parent) {
        if (documents != null && !documents.isEmpty()) {
            int size = documents.size();
            for (int a = 0; a < size; a++) {
                Document document = (Document) documents.get(a);
                String key = document.dc_id + "_" + document.id;
                if (this.stickersMap == null || !this.stickersMap.containsKey(key)) {
                    if (this.stickers == null) {
                        this.stickers = new ArrayList();
                        this.stickersParents = new ArrayList();
                        this.stickersMap = new HashMap();
                    }
                    this.stickers.add(document);
                    boolean found = false;
                    int size2 = document.attributes.size();
                    for (int b = 0; b < size2; b++) {
                        DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(b);
                        if (attribute instanceof TL_documentAttributeSticker) {
                            this.stickersParents.add(attribute.stickerset);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        this.stickersParents.add(parent);
                    }
                    this.stickersMap.put(key, document);
                }
            }
        }
    }

    public void hide() {
        if (!this.visible) {
            return;
        }
        if (this.stickers != null || (this.keywordResults != null && !this.keywordResults.isEmpty())) {
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
        }
    }

    private void cancelEmojiSearch() {
        if (this.searchRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            this.searchRunnable = null;
        }
    }

    private void searchEmojiByKeyword() {
        String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
        if (!Arrays.equals(newLanguage, this.lastSearchKeyboardLanguage)) {
            DataQuery.getInstance(this.currentAccount).fetchNewEmojiKeywords(newLanguage);
        }
        this.lastSearchKeyboardLanguage = newLanguage;
        String query = this.lastSticker;
        cancelEmojiSearch();
        this.searchRunnable = new StickersAdapter$$Lambda$0(this, query);
        if (this.keywordResults == null || this.keywordResults.isEmpty()) {
            AndroidUtilities.runOnUIThread(this.searchRunnable, 1000);
        } else {
            this.searchRunnable.run();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$searchEmojiByKeyword$1$StickersAdapter(String query) {
        DataQuery.getInstance(this.currentAccount).getEmojiSuggestions(this.lastSearchKeyboardLanguage, query, true, new StickersAdapter$$Lambda$3(this, query));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$0$StickersAdapter(String query, ArrayList param, String alias) {
        if (query.equals(this.lastSticker)) {
            if (!param.isEmpty()) {
                this.keywordResults = param;
            }
            notifyDataSetChanged();
            StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
            boolean z = !param.isEmpty();
            this.visible = z;
            stickersAdapterDelegate.needChangePanelVisibility(z);
        }
    }

    public void loadStikersForEmoji(CharSequence emoji, boolean emojiOnly) {
        boolean search = emoji != null && emoji.length() > 0 && emoji.length() <= 14;
        if (search) {
            String originalEmoji = emoji.toString();
            int length = emoji.length();
            int a = 0;
            while (a < length) {
                CharSequence[] charSequenceArr;
                if (a < length - 1 && ((emoji.charAt(a) == 55356 && emoji.charAt(a + 1) >= 57339 && emoji.charAt(a + 1) <= 57343) || (emoji.charAt(a) == 8205 && (emoji.charAt(a + 1) == 9792 || emoji.charAt(a + 1) == 9794)))) {
                    charSequenceArr = new CharSequence[2];
                    charSequenceArr[0] = emoji.subSequence(0, a);
                    charSequenceArr[1] = emoji.subSequence(a + 2, emoji.length());
                    emoji = TextUtils.concat(charSequenceArr);
                    length -= 2;
                    a--;
                } else if (emoji.charAt(a) == 65039) {
                    charSequenceArr = new CharSequence[2];
                    charSequenceArr[0] = emoji.subSequence(0, a);
                    charSequenceArr[1] = emoji.subSequence(a + 1, emoji.length());
                    emoji = TextUtils.concat(charSequenceArr);
                    length--;
                    a--;
                }
                a++;
            }
            this.lastSticker = emoji.toString();
            boolean isValidEmoji = Emoji.isValidEmoji(originalEmoji) || Emoji.isValidEmoji(this.lastSticker);
            if (emojiOnly || SharedConfig.suggestStickers == 2 || !isValidEmoji) {
                if (this.visible && (this.keywordResults == null || this.keywordResults.isEmpty())) {
                    this.visible = false;
                    this.delegate.needChangePanelVisibility(false);
                    notifyDataSetChanged();
                }
                if (!isValidEmoji) {
                    searchEmojiByKeyword();
                    return;
                }
                return;
            }
            Document document;
            cancelEmojiSearch();
            this.stickers = null;
            this.stickersParents = null;
            this.stickersMap = null;
            if (this.lastReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
                this.lastReqId = 0;
            }
            this.delayLocalResults = false;
            final ArrayList<Document> recentStickers = DataQuery.getInstance(this.currentAccount).getRecentStickersNoCopy(0);
            final ArrayList<Document> favsStickers = DataQuery.getInstance(this.currentAccount).getRecentStickersNoCopy(2);
            int recentsAdded = 0;
            int size = recentStickers.size();
            for (a = 0; a < size; a++) {
                document = (Document) recentStickers.get(a);
                if (isValidSticker(document, this.lastSticker)) {
                    addStickerToResult(document, "recent");
                    recentsAdded++;
                    if (recentsAdded >= 5) {
                        break;
                    }
                }
            }
            size = favsStickers.size();
            for (a = 0; a < size; a++) {
                document = (Document) favsStickers.get(a);
                if (isValidSticker(document, this.lastSticker)) {
                    addStickerToResult(document, "fav");
                }
            }
            HashMap<String, ArrayList<Document>> allStickers = DataQuery.getInstance(this.currentAccount).getAllStickers();
            ArrayList<Document> newStickers = allStickers != null ? (ArrayList) allStickers.get(this.lastSticker) : null;
            if (!(newStickers == null || newStickers.isEmpty())) {
                ArrayList<Document> arrayList = new ArrayList(newStickers);
                if (!recentStickers.isEmpty()) {
                    Collections.sort(arrayList, new Comparator<Document>() {
                        private int getIndex(long id) {
                            int i;
                            int a = 0;
                            while (a < favsStickers.size()) {
                                if (((Document) favsStickers.get(a)).id == id) {
                                    return a + 1000;
                                }
                                a++;
                            }
                            a = 0;
                            while (a < recentStickers.size()) {
                                if (((Document) recentStickers.get(a)).id == id) {
                                    i = a;
                                    return a;
                                }
                                a++;
                            }
                            i = a;
                            return -1;
                        }

                        public int compare(Document lhs, Document rhs) {
                            int idx1 = getIndex(lhs.id);
                            int idx2 = getIndex(rhs.id);
                            if (idx1 > idx2) {
                                return -1;
                            }
                            if (idx1 < idx2) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                addStickersToResult(arrayList, null);
            }
            if (SharedConfig.suggestStickers == 0) {
                searchServerStickers(this.lastSticker, originalEmoji);
            }
            if (this.stickers != null && !this.stickers.isEmpty()) {
                if (SharedConfig.suggestStickers != 0 || this.stickers.size() >= 5) {
                    checkStickerFilesExistAndDownload();
                    boolean show = (this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty()) ? false : true;
                    if (show) {
                        this.keywordResults = null;
                    }
                    this.delegate.needChangePanelVisibility(show);
                    this.visible = true;
                } else {
                    this.delayLocalResults = true;
                    this.delegate.needChangePanelVisibility(false);
                    this.visible = false;
                }
                notifyDataSetChanged();
                return;
            } else if (this.visible) {
                this.delegate.needChangePanelVisibility(false);
                this.visible = false;
                return;
            } else {
                return;
            }
        }
        this.lastSticker = "";
        cancelEmojiSearch();
        if (!this.visible) {
            return;
        }
        if (this.stickers != null || (this.keywordResults != null && !this.keywordResults.isEmpty())) {
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
        }
    }

    private void searchServerStickers(String emoji, String originalEmoji) {
        TL_messages_getStickers req = new TL_messages_getStickers();
        req.emoticon = originalEmoji;
        req.hash = 0;
        this.lastReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersAdapter$$Lambda$1(this, emoji));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$searchServerStickers$3$StickersAdapter(String emoji, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersAdapter$$Lambda$2(this, emoji, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$2$StickersAdapter(String emoji, TLObject response) {
        this.lastReqId = 0;
        if (emoji.equals(this.lastSticker) && (response instanceof TL_messages_stickers)) {
            int oldCount;
            int newCount;
            this.delayLocalResults = false;
            TL_messages_stickers res = (TL_messages_stickers) response;
            if (this.stickers != null) {
                oldCount = this.stickers.size();
            } else {
                oldCount = 0;
            }
            addStickersToResult(res.stickers, "sticker_search_" + emoji);
            if (this.stickers != null) {
                newCount = this.stickers.size();
            } else {
                newCount = 0;
            }
            if (!(this.visible || this.stickers == null || this.stickers.isEmpty())) {
                boolean show;
                checkStickerFilesExistAndDownload();
                if (this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty()) {
                    show = false;
                } else {
                    show = true;
                }
                if (show) {
                    this.keywordResults = null;
                }
                this.delegate.needChangePanelVisibility(show);
                this.visible = true;
            }
            if (oldCount != newCount) {
                notifyDataSetChanged();
            }
        }
    }

    public void clearStickers() {
        if (!this.delayLocalResults && this.lastReqId == 0) {
            this.lastSticker = null;
            this.stickers = null;
            this.stickersParents = null;
            this.stickersMap = null;
            this.keywordResults = null;
            this.stickersToLoad.clear();
            notifyDataSetChanged();
            if (this.lastReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
                this.lastReqId = 0;
            }
        }
    }

    public boolean isShowingKeywords() {
        return (this.keywordResults == null || this.keywordResults.isEmpty()) ? false : true;
    }

    public int getItemCount() {
        if (this.keywordResults == null || this.keywordResults.isEmpty()) {
            return (this.delayLocalResults || this.stickers == null) ? 0 : this.stickers.size();
        } else {
            return this.keywordResults.size();
        }
    }

    public Object getItem(int i) {
        if (this.keywordResults == null || this.keywordResults.isEmpty()) {
            return (this.stickers == null || i < 0 || i >= this.stickers.size()) ? null : this.stickers.get(i);
        } else {
            return ((KeywordResult) this.keywordResults.get(i)).emoji;
        }
    }

    public Object getItemParent(int i) {
        if ((this.keywordResults == null || this.keywordResults.isEmpty()) && this.stickersParents != null && i >= 0 && i < this.stickersParents.size()) {
            return this.stickersParents.get(i);
        }
        return null;
    }

    public boolean isEnabled(ViewHolder holder) {
        return false;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new StickerCell(this.mContext);
                break;
            default:
                view = new EmojiReplacementCell(this.mContext);
                break;
        }
        return new Holder(view);
    }

    public int getItemViewType(int position) {
        if (this.keywordResults == null || this.keywordResults.isEmpty()) {
            return 0;
        }
        return 1;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        int side;
        switch (holder.getItemViewType()) {
            case 0:
                side = 0;
                if (position == 0) {
                    if (this.stickers.size() == 1) {
                        side = 2;
                    } else {
                        side = -1;
                    }
                } else if (position == this.stickers.size() - 1) {
                    side = 1;
                }
                holder.itemView.setSticker((Document) this.stickers.get(position), this.stickersParents.get(position), side);
                return;
            case 1:
                side = 0;
                if (position == 0) {
                    if (this.keywordResults.size() == 1) {
                        side = 2;
                    } else {
                        side = -1;
                    }
                } else if (position == this.keywordResults.size() - 1) {
                    side = 1;
                }
                holder.itemView.setEmoji(((KeywordResult) this.keywordResults.get(position)).emoji, side);
                return;
            default:
                return;
        }
    }
}
