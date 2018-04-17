package org.telegram.ui.Adapters;

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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getStickers;
import org.telegram.tgnet.TLRPC.TL_messages_stickers;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class StickersAdapter extends SelectionAdapter implements NotificationCenterDelegate {
    private int currentAccount = UserConfig.selectedAccount;
    private boolean delayLocalResults;
    private StickersAdapterDelegate delegate;
    private int lastReqId;
    private String lastSticker;
    private Context mContext;
    private ArrayList<Document> stickers;
    private HashMap<String, Document> stickersMap;
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if ((id == NotificationCenter.FileDidLoaded || id == NotificationCenter.FileDidFailedLoad) && this.stickers != null && !this.stickers.isEmpty() && !this.stickersToLoad.isEmpty() && this.visible) {
            boolean z = false;
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
        int size = Math.min(10, this.stickers.size());
        for (int a = 0; a < size; a++) {
            Document document = (Document) this.stickers.get(a);
            if (!FileLoader.getPathToAttach(document.thumb, "webp", true).exists()) {
                this.stickersToLoad.add(FileLoader.getAttachFileName(document.thumb, "webp"));
                FileLoader.getInstance(this.currentAccount).loadFile(document.thumb.location, "webp", 0, 1);
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

    private void addStickerToResult(Document document) {
        if (document != null) {
            String key = new StringBuilder();
            key.append(document.dc_id);
            key.append("_");
            key.append(document.id);
            key = key.toString();
            if (this.stickersMap == null || !this.stickersMap.containsKey(key)) {
                if (this.stickers == null) {
                    this.stickers = new ArrayList();
                    this.stickersMap = new HashMap();
                }
                this.stickers.add(document);
                this.stickersMap.put(key, document);
            }
        }
    }

    private void addStickersToResult(ArrayList<Document> documents) {
        if (documents != null) {
            if (!documents.isEmpty()) {
                int size = documents.size();
                for (int a = 0; a < size; a++) {
                    Document document = (Document) documents.get(a);
                    String key = new StringBuilder();
                    key.append(document.dc_id);
                    key.append("_");
                    key.append(document.id);
                    key = key.toString();
                    if (this.stickersMap == null || !this.stickersMap.containsKey(key)) {
                        if (this.stickers == null) {
                            this.stickers = new ArrayList();
                            this.stickersMap = new HashMap();
                        }
                        this.stickers.add(document);
                        this.stickersMap.put(key, document);
                    }
                }
            }
        }
    }

    public void loadStikersForEmoji(CharSequence emoji) {
        if (SharedConfig.suggestStickers != 2) {
            boolean z = false;
            boolean search = emoji != null && emoji.length() > 0 && emoji.length() <= 14;
            if (search) {
                int length = emoji.length();
                CharSequence emoji2 = emoji;
                int a = 0;
                while (a < length) {
                    if (a < length - 1 && ((emoji2.charAt(a) == '\ud83c' && emoji2.charAt(a + 1) >= '\udffb' && emoji2.charAt(a + 1) <= '\udfff') || (emoji2.charAt(a) == '\u200d' && (emoji2.charAt(a + 1) == '\u2640' || emoji2.charAt(a + 1) == '\u2642')))) {
                        emoji2 = TextUtils.concat(new CharSequence[]{emoji2.subSequence(0, a), emoji2.subSequence(a + 2, emoji2.length())});
                        length -= 2;
                        a--;
                    } else if (emoji2.charAt(a) == '\ufe0f') {
                        emoji2 = TextUtils.concat(new CharSequence[]{emoji2.subSequence(0, a), emoji2.subSequence(a + 1, emoji2.length())});
                        length--;
                        a--;
                    }
                    a++;
                }
                this.lastSticker = emoji2.toString().trim();
                if (Emoji.isValidEmoji(this.lastSticker)) {
                    int a2;
                    Document document;
                    ArrayList<Document> newStickers = null;
                    this.stickers = null;
                    this.stickersMap = null;
                    this.delayLocalResults = false;
                    final ArrayList<Document> recentStickers = DataQuery.getInstance(this.currentAccount).getRecentStickersNoCopy(0);
                    final ArrayList<Document> favsStickers = DataQuery.getInstance(this.currentAccount).getRecentStickersNoCopy(2);
                    int recentsAdded = 0;
                    int size = recentStickers.size();
                    for (a2 = 0; a2 < size; a2++) {
                        document = (Document) recentStickers.get(a2);
                        if (isValidSticker(document, this.lastSticker)) {
                            addStickerToResult(document);
                            recentsAdded++;
                            if (recentsAdded >= 5) {
                                break;
                            }
                        }
                    }
                    size = favsStickers.size();
                    for (a2 = 0; a2 < size; a2++) {
                        document = (Document) favsStickers.get(a2);
                        if (isValidSticker(document, this.lastSticker)) {
                            addStickerToResult(document);
                        }
                    }
                    HashMap<String, ArrayList<Document>> allStickers = DataQuery.getInstance(this.currentAccount).getAllStickers();
                    if (allStickers != null) {
                        newStickers = (ArrayList) allStickers.get(this.lastSticker);
                    }
                    if (!(newStickers == null || newStickers.isEmpty())) {
                        ArrayList<Document> arrayList = new ArrayList(newStickers);
                        if (!recentStickers.isEmpty()) {
                            Collections.sort(arrayList, new Comparator<Document>() {
                                private int getIndex(long id) {
                                    int a = 0;
                                    for (int a2 = 0; a2 < favsStickers.size(); a2++) {
                                        if (((Document) favsStickers.get(a2)).id == id) {
                                            return a2 + 1000;
                                        }
                                    }
                                    while (a < recentStickers.size()) {
                                        if (((Document) recentStickers.get(a)).id == id) {
                                            return a;
                                        }
                                        a++;
                                    }
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
                        addStickersToResult(arrayList);
                    }
                    if (SharedConfig.suggestStickers == 0) {
                        searchServerStickers(this.lastSticker);
                    }
                    if (this.stickers != null && !this.stickers.isEmpty()) {
                        if (SharedConfig.suggestStickers != 0 || this.stickers.size() >= 5) {
                            checkStickerFilesExistAndDownload();
                            StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
                            if (!(this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty())) {
                                z = true;
                            }
                            stickersAdapterDelegate.needChangePanelVisibility(z);
                            this.visible = true;
                        } else {
                            this.delayLocalResults = true;
                            this.delegate.needChangePanelVisibility(false);
                            this.visible = false;
                        }
                        notifyDataSetChanged();
                    } else if (this.visible) {
                        this.delegate.needChangePanelVisibility(false);
                        this.visible = false;
                    }
                } else {
                    if (this.visible) {
                        this.visible = false;
                        this.delegate.needChangePanelVisibility(false);
                        notifyDataSetChanged();
                    }
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

    private void searchServerStickers(final String emoji) {
        if (this.lastReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
        }
        TL_messages_getStickers req = new TL_messages_getStickers();
        req.emoticon = emoji;
        req.hash = TtmlNode.ANONYMOUS_REGION_ID;
        req.exclude_featured = false;
        this.lastReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        boolean z = false;
                        StickersAdapter.this.lastReqId = 0;
                        if (emoji.equals(StickersAdapter.this.lastSticker)) {
                            if (response instanceof TL_messages_stickers) {
                                StickersAdapter.this.delayLocalResults = false;
                                TL_messages_stickers res = response;
                                int oldCount = StickersAdapter.this.stickers != null ? StickersAdapter.this.stickers.size() : 0;
                                StickersAdapter.this.addStickersToResult(res.stickers);
                                int newCount = StickersAdapter.this.stickers != null ? StickersAdapter.this.stickers.size() : 0;
                                if (!(StickersAdapter.this.visible || StickersAdapter.this.stickers == null || StickersAdapter.this.stickers.isEmpty())) {
                                    StickersAdapter.this.checkStickerFilesExistAndDownload();
                                    StickersAdapterDelegate access$800 = StickersAdapter.this.delegate;
                                    if (!(StickersAdapter.this.stickers == null || StickersAdapter.this.stickers.isEmpty() || !StickersAdapter.this.stickersToLoad.isEmpty())) {
                                        z = true;
                                    }
                                    access$800.needChangePanelVisibility(z);
                                    StickersAdapter.this.visible = true;
                                }
                                if (oldCount != newCount) {
                                    StickersAdapter.this.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public void clearStickers() {
        this.lastSticker = null;
        this.stickers = null;
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
        ((StickerCell) viewHolder.itemView).setSticker((Document) this.stickers.get(i), side);
    }
}
