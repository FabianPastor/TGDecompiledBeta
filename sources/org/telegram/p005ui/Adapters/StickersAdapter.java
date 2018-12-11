package org.telegram.p005ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.Cells.StickerCell;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getStickers;
import org.telegram.tgnet.TLRPC.TL_messages_stickers;

/* renamed from: org.telegram.ui.Adapters.StickersAdapter */
public class StickersAdapter extends SelectionAdapter implements NotificationCenterDelegate {
    private int currentAccount = UserConfig.selectedAccount;
    private boolean delayLocalResults;
    private StickersAdapterDelegate delegate;
    private int lastReqId;
    private String lastSticker;
    private Context mContext;
    private ArrayList<Document> stickers;
    private HashMap<String, Document> stickersMap;
    private ArrayList<Object> stickersParents;
    private ArrayList<String> stickersToLoad = new ArrayList();
    private boolean visible;

    /* renamed from: org.telegram.ui.Adapters.StickersAdapter$StickersAdapterDelegate */
    public interface StickersAdapterDelegate {
        void needChangePanelVisibility(boolean z);
    }

    public StickersAdapter(Context context, StickersAdapterDelegate delegate) {
        this.mContext = context;
        this.delegate = delegate;
        DataQuery.getInstance(this.currentAccount).checkStickers(0);
        DataQuery.getInstance(this.currentAccount).checkStickers(1);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailedLoad);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailedLoad);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        boolean z = false;
        if ((id == NotificationCenter.fileDidLoad || id == NotificationCenter.fileDidFailedLoad) && this.stickers != null && !this.stickers.isEmpty() && !this.stickersToLoad.isEmpty() && this.visible) {
            this.stickersToLoad.remove(args[0]);
            if (this.stickersToLoad.isEmpty()) {
                StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
                if (!(this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty())) {
                    z = true;
                }
                stickersAdapterDelegate.needChangePanelVisibility(z);
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
            Document document = (Document) this.stickers.get(a);
            if (!FileLoader.getPathToAttach(document.thumb, "webp", true).exists()) {
                this.stickersToLoad.add(FileLoader.getAttachFileName(document.thumb, "webp"));
                FileLoader.getInstance(this.currentAccount).loadFile(document.thumb.location, this.stickersParents.get(a), "webp", 0, 1, 1);
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
            String key = document.dc_id + "_" + document.var_id;
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
                String key = document.dc_id + "_" + document.var_id;
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

    /* JADX WARNING: Missing block: B:18:0x0056, code:
            if (r20.charAt(r3 + 1) > 57343) goto L_0x0058;
     */
    /* JADX WARNING: Missing block: B:24:0x007e, code:
            if (r20.charAt(r3 + 1) == 9794) goto L_0x0080;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadStikersForEmoji(CharSequence emoji) {
        if (SharedConfig.suggestStickers != 2) {
            boolean search = emoji != null && emoji.length() > 0 && emoji.length() <= 14;
            if (search) {
                String originalEmoji = emoji.toString();
                int length = emoji.length();
                int a = 0;
                while (a < length) {
                    CharSequence[] charSequenceArr;
                    if (a < length - 1) {
                        if (emoji.charAt(a) == 55356) {
                            if (emoji.charAt(a + 1) >= 57339) {
                            }
                        }
                        if (emoji.charAt(a) == 8205) {
                            if (emoji.charAt(a + 1) != 9792) {
                            }
                            charSequenceArr = new CharSequence[2];
                            charSequenceArr[0] = emoji.subSequence(0, a);
                            charSequenceArr[1] = emoji.subSequence(a + 2, emoji.length());
                            emoji = TextUtils.concat(charSequenceArr);
                            length -= 2;
                            a--;
                            a++;
                        }
                    }
                    if (emoji.charAt(a) == 65039) {
                        charSequenceArr = new CharSequence[2];
                        charSequenceArr[0] = emoji.subSequence(0, a);
                        charSequenceArr[1] = emoji.subSequence(a + 1, emoji.length());
                        emoji = TextUtils.concat(charSequenceArr);
                        length--;
                        a--;
                    }
                    a++;
                }
                this.lastSticker = emoji.toString().trim();
                if (Emoji.isValidEmoji(originalEmoji) || Emoji.isValidEmoji(this.lastSticker)) {
                    Document document;
                    this.stickers = null;
                    this.stickersParents = null;
                    this.stickersMap = null;
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
                                        if (((Document) favsStickers.get(a)).var_id == id) {
                                            return a + 1000;
                                        }
                                        a++;
                                    }
                                    a = 0;
                                    while (a < recentStickers.size()) {
                                        if (((Document) recentStickers.get(a)).var_id == id) {
                                            i = a;
                                            return a;
                                        }
                                        a++;
                                    }
                                    i = a;
                                    return -1;
                                }

                                public int compare(Document lhs, Document rhs) {
                                    int idx1 = getIndex(lhs.var_id);
                                    int idx2 = getIndex(rhs.var_id);
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
                            StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
                            boolean z = (this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty()) ? false : true;
                            stickersAdapterDelegate.needChangePanelVisibility(z);
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
                } else if (this.visible) {
                    this.visible = false;
                    this.delegate.needChangePanelVisibility(false);
                    notifyDataSetChanged();
                    return;
                } else {
                    return;
                }
            }
            this.lastSticker = TtmlNode.ANONYMOUS_REGION_ID;
            if (this.visible && this.stickers != null) {
                this.visible = false;
                this.delegate.needChangePanelVisibility(false);
            }
        }
    }

    private void searchServerStickers(String emoji, String originalEmoji) {
        if (this.lastReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
        }
        TL_messages_getStickers req = new TL_messages_getStickers();
        req.emoticon = originalEmoji;
        req.hash = 0;
        this.lastReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersAdapter$$Lambda$0(this, emoji));
    }

    final /* synthetic */ void lambda$searchServerStickers$1$StickersAdapter(String emoji, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersAdapter$$Lambda$1(this, emoji, response));
    }

    final /* synthetic */ void lambda$null$0$StickersAdapter(String emoji, TLObject response) {
        boolean z = false;
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
                checkStickerFilesExistAndDownload();
                StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
                if (!(this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty())) {
                    z = true;
                }
                stickersAdapterDelegate.needChangePanelVisibility(z);
                this.visible = true;
            }
            if (oldCount != newCount) {
                notifyDataSetChanged();
            }
        }
    }

    public void clearStickers() {
        this.lastSticker = null;
        this.stickers = null;
        this.stickersParents = null;
        this.stickersMap = null;
        this.stickersToLoad.clear();
        notifyDataSetChanged();
        if (this.lastReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
            this.lastReqId = 0;
        }
    }

    public int getItemCount() {
        return (this.delayLocalResults || this.stickers == null) ? 0 : this.stickers.size();
    }

    public Document getItem(int i) {
        return (this.stickers == null || i < 0 || i >= this.stickers.size()) ? null : (Document) this.stickers.get(i);
    }

    public Object getItemParent(int i) {
        return (this.stickersParents == null || i < 0 || i >= this.stickersParents.size()) ? null : this.stickersParents.get(i);
    }

    public boolean isEnabled(ViewHolder holder) {
        return true;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(new StickerCell(this.mContext));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int side = 0;
        if (i == 0) {
            if (this.stickers.size() == 1) {
                side = 2;
            } else {
                side = -1;
            }
        } else if (i == this.stickers.size() - 1) {
            side = 1;
        }
        ((StickerCell) viewHolder.itemView).setSticker((Document) this.stickers.get(i), this.stickersParents.get(i), side);
    }
}
