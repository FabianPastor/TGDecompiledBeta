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

    public boolean isEnabled(ViewHolder viewHolder) {
        return true;
    }

    public StickersAdapter(Context context, StickersAdapterDelegate stickersAdapterDelegate) {
        this.mContext = context;
        this.delegate = stickersAdapterDelegate;
        DataQuery.getInstance(this.currentAccount).checkStickers(null);
        DataQuery.getInstance(this.currentAccount).checkStickers(1);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if ((i == NotificationCenter.FileDidLoaded || i == NotificationCenter.FileDidFailedLoad) && this.stickers != 0 && this.stickers.isEmpty() == 0 && this.stickersToLoad.isEmpty() == 0 && this.visible != 0) {
            i = 0;
            this.stickersToLoad.remove((String) objArr[0]);
            if (this.stickersToLoad.isEmpty() != 0) {
                i2 = this.delegate;
                if (!(this.stickers == null || this.stickers.isEmpty() != null || this.stickersToLoad.isEmpty() == null)) {
                    i = 1;
                }
                i2.needChangePanelVisibility(i);
            }
        }
    }

    private boolean checkStickerFilesExistAndDownload() {
        if (this.stickers == null) {
            return false;
        }
        this.stickersToLoad.clear();
        int min = Math.min(10, this.stickers.size());
        for (int i = 0; i < min; i++) {
            Document document = (Document) this.stickers.get(i);
            if (!FileLoader.getPathToAttach(document.thumb, "webp", true).exists()) {
                this.stickersToLoad.add(FileLoader.getAttachFileName(document.thumb, "webp"));
                FileLoader.getInstance(this.currentAccount).loadFile(document.thumb.location, "webp", 0, 1);
            }
        }
        return this.stickersToLoad.isEmpty();
    }

    private boolean isValidSticker(Document document, String str) {
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                if (!(documentAttribute.alt == null || documentAttribute.alt.contains(str) == null)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private void addStickerToResult(Document document) {
        if (document != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(document.dc_id);
            stringBuilder.append("_");
            stringBuilder.append(document.id);
            String stringBuilder2 = stringBuilder.toString();
            if (this.stickersMap == null || !this.stickersMap.containsKey(stringBuilder2)) {
                if (this.stickers == null) {
                    this.stickers = new ArrayList();
                    this.stickersMap = new HashMap();
                }
                this.stickers.add(document);
                this.stickersMap.put(stringBuilder2, document);
            }
        }
    }

    private void addStickersToResult(ArrayList<Document> arrayList) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    Document document = (Document) arrayList.get(i);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(document.dc_id);
                    stringBuilder.append("_");
                    stringBuilder.append(document.id);
                    String stringBuilder2 = stringBuilder.toString();
                    if (this.stickersMap == null || !this.stickersMap.containsKey(stringBuilder2)) {
                        if (this.stickers == null) {
                            this.stickers = new ArrayList();
                            this.stickersMap = new HashMap();
                        }
                        this.stickers.add(document);
                        this.stickersMap.put(stringBuilder2, document);
                    }
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadStikersForEmoji(CharSequence charSequence) {
        if (SharedConfig.suggestStickers != 2) {
            boolean z = false;
            boolean z2 = charSequence != null && charSequence.length() > 0 && charSequence.length() <= 14;
            if (z2) {
                int i;
                int length = charSequence.length();
                CharSequence charSequence2 = charSequence;
                charSequence = null;
                while (charSequence < length) {
                    if (charSequence < length - 1) {
                        if (charSequence2.charAt(charSequence) == '\ud83c') {
                            i = charSequence + 1;
                            if (charSequence2.charAt(i) >= '\udffb') {
                            }
                        }
                        if (charSequence2.charAt(charSequence) == '\u200d') {
                            i = charSequence + 1;
                            if (charSequence2.charAt(i) != '\u2640') {
                            }
                            charSequence2 = TextUtils.concat(new CharSequence[]{charSequence2.subSequence(0, charSequence), charSequence2.subSequence(charSequence + 2, charSequence2.length())});
                            length -= 2;
                            charSequence--;
                            charSequence += 1;
                        }
                    }
                    if (charSequence2.charAt(charSequence) == '\ufe0f') {
                        charSequence2 = TextUtils.concat(new CharSequence[]{charSequence2.subSequence(0, charSequence), charSequence2.subSequence(charSequence + 1, charSequence2.length())});
                        length--;
                        charSequence--;
                    }
                    charSequence += 1;
                }
                this.lastSticker = charSequence2.toString().trim();
                if (Emoji.isValidEmoji(this.lastSticker) == null) {
                    if (this.visible != null) {
                        this.visible = false;
                        this.delegate.needChangePanelVisibility(false);
                        notifyDataSetChanged();
                    }
                    return;
                }
                charSequence = null;
                this.stickers = null;
                this.stickersMap = null;
                this.delayLocalResults = false;
                final ArrayList recentStickersNoCopy = DataQuery.getInstance(this.currentAccount).getRecentStickersNoCopy(0);
                final ArrayList recentStickersNoCopy2 = DataQuery.getInstance(this.currentAccount).getRecentStickersNoCopy(2);
                int size = recentStickersNoCopy.size();
                i = 0;
                int i2 = i;
                while (i < size) {
                    Document document = (Document) recentStickersNoCopy.get(i);
                    if (isValidSticker(document, this.lastSticker)) {
                        addStickerToResult(document);
                        i2++;
                        if (i2 >= 5) {
                            break;
                        }
                    }
                    i++;
                }
                size = recentStickersNoCopy2.size();
                for (i = 0; i < size; i++) {
                    Document document2 = (Document) recentStickersNoCopy2.get(i);
                    if (isValidSticker(document2, this.lastSticker)) {
                        addStickerToResult(document2);
                    }
                }
                HashMap allStickers = DataQuery.getInstance(this.currentAccount).getAllStickers();
                if (allStickers != null) {
                    charSequence = (ArrayList) allStickers.get(this.lastSticker);
                }
                if (!(charSequence == null || charSequence.isEmpty())) {
                    Object arrayList = new ArrayList(charSequence);
                    if (recentStickersNoCopy.isEmpty() == null) {
                        Collections.sort(arrayList, new Comparator<Document>() {
                            private int getIndex(long j) {
                                int i = 0;
                                for (int i2 = 0; i2 < recentStickersNoCopy2.size(); i2++) {
                                    if (((Document) recentStickersNoCopy2.get(i2)).id == j) {
                                        return i2 + 1000;
                                    }
                                }
                                while (i < recentStickersNoCopy.size()) {
                                    if (((Document) recentStickersNoCopy.get(i)).id == j) {
                                        return i;
                                    }
                                    i++;
                                }
                                return -1;
                            }

                            public int compare(Document document, Document document2) {
                                document = getIndex(document.id);
                                document2 = getIndex(document2.id);
                                if (document > document2) {
                                    return -1;
                                }
                                return document < document2 ? 1 : null;
                            }
                        });
                    }
                    addStickersToResult(arrayList);
                }
                if (SharedConfig.suggestStickers == null) {
                    searchServerStickers(this.lastSticker);
                }
                if (this.stickers != null && this.stickers.isEmpty() == null) {
                    if (SharedConfig.suggestStickers != null || this.stickers.size() >= 5) {
                        checkStickerFilesExistAndDownload();
                        charSequence = this.delegate;
                        if (!(this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty())) {
                            z = true;
                        }
                        charSequence.needChangePanelVisibility(z);
                        this.visible = true;
                    } else {
                        this.delayLocalResults = true;
                        this.delegate.needChangePanelVisibility(false);
                        this.visible = false;
                    }
                    notifyDataSetChanged();
                } else if (this.visible != null) {
                    this.delegate.needChangePanelVisibility(false);
                    this.visible = false;
                }
            } else {
                this.lastSticker = TtmlNode.ANONYMOUS_REGION_ID;
                if (!(this.visible == null || this.stickers == null)) {
                    this.visible = false;
                    this.delegate.needChangePanelVisibility(false);
                }
            }
        }
    }

    private void searchServerStickers(final String str) {
        if (this.lastReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
        }
        TLObject tL_messages_getStickers = new TL_messages_getStickers();
        tL_messages_getStickers.emoticon = str;
        tL_messages_getStickers.hash = TtmlNode.ANONYMOUS_REGION_ID;
        tL_messages_getStickers.exclude_featured = false;
        this.lastReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getStickers, new RequestDelegate() {
            public void run(final TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        boolean z = false;
                        StickersAdapter.this.lastReqId = 0;
                        if (str.equals(StickersAdapter.this.lastSticker)) {
                            if (tLObject instanceof TL_messages_stickers) {
                                StickersAdapter.this.delayLocalResults = false;
                                TL_messages_stickers tL_messages_stickers = (TL_messages_stickers) tLObject;
                                int size = StickersAdapter.this.stickers != null ? StickersAdapter.this.stickers.size() : 0;
                                StickersAdapter.this.addStickersToResult(tL_messages_stickers.stickers);
                                int size2 = StickersAdapter.this.stickers != null ? StickersAdapter.this.stickers.size() : 0;
                                if (!(StickersAdapter.this.visible || StickersAdapter.this.stickers == null || StickersAdapter.this.stickers.isEmpty())) {
                                    StickersAdapter.this.checkStickerFilesExistAndDownload();
                                    StickersAdapterDelegate access$800 = StickersAdapter.this.delegate;
                                    if (!(StickersAdapter.this.stickers == null || StickersAdapter.this.stickers.isEmpty() || !StickersAdapter.this.stickersToLoad.isEmpty())) {
                                        z = true;
                                    }
                                    access$800.needChangePanelVisibility(z);
                                    StickersAdapter.this.visible = true;
                                }
                                if (size != size2) {
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
        return (this.stickers == null || i < 0 || i >= this.stickers.size()) ? 0 : (Document) this.stickers.get(i);
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(new StickerCell(this.mContext));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int i2 = 1;
        if (i == 0) {
            i2 = this.stickers.size() == 1 ? 2 : -1;
        } else if (i != this.stickers.size() - 1) {
            i2 = 0;
        }
        ((StickerCell) viewHolder.itemView).setSticker((Document) this.stickers.get(i), i2);
    }
}
