package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
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

    /* JADX WARNING: Missing block: B:18:0x0056, code:
            if (r20.charAt(r3 + 1) > 57343) goto L_0x0058;
     */
    /* JADX WARNING: Missing block: B:24:0x007e, code:
            if (r20.charAt(r3 + 1) == 9794) goto L_0x0080;
     */
    public void loadStikersForEmoji(java.lang.CharSequence r20) {
        /*
        r19 = this;
        r15 = org.telegram.messenger.SharedConfig.suggestStickers;
        r16 = 2;
        r0 = r16;
        if (r15 != r0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        if (r20 == 0) goto L_0x00b1;
    L_0x000b:
        r15 = r20.length();
        if (r15 <= 0) goto L_0x00b1;
    L_0x0011:
        r15 = r20.length();
        r16 = 14;
        r0 = r16;
        if (r15 > r0) goto L_0x00b1;
    L_0x001b:
        r13 = 1;
    L_0x001c:
        if (r13 == 0) goto L_0x026e;
    L_0x001e:
        r10 = r20.toString();
        r8 = r20.length();
        r3 = 0;
    L_0x0027:
        if (r3 >= r8) goto L_0x00ef;
    L_0x0029:
        r15 = r8 + -1;
        if (r3 >= r15) goto L_0x00b4;
    L_0x002d:
        r0 = r20;
        r15 = r0.charAt(r3);
        r16 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        r0 = r16;
        if (r15 != r0) goto L_0x0058;
    L_0x003a:
        r15 = r3 + 1;
        r0 = r20;
        r15 = r0.charAt(r15);
        r16 = 57339; // 0xdffb float:8.0349E-41 double:2.8329E-319;
        r0 = r16;
        if (r15 < r0) goto L_0x0058;
    L_0x0049:
        r15 = r3 + 1;
        r0 = r20;
        r15 = r0.charAt(r15);
        r16 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        r0 = r16;
        if (r15 <= r0) goto L_0x0080;
    L_0x0058:
        r0 = r20;
        r15 = r0.charAt(r3);
        r16 = 8205; // 0x200d float:1.1498E-41 double:4.054E-320;
        r0 = r16;
        if (r15 != r0) goto L_0x00b4;
    L_0x0064:
        r15 = r3 + 1;
        r0 = r20;
        r15 = r0.charAt(r15);
        r16 = 9792; // 0x2640 float:1.3722E-41 double:4.838E-320;
        r0 = r16;
        if (r15 == r0) goto L_0x0080;
    L_0x0072:
        r15 = r3 + 1;
        r0 = r20;
        r15 = r0.charAt(r15);
        r16 = 9794; // 0x2642 float:1.3724E-41 double:4.839E-320;
        r0 = r16;
        if (r15 != r0) goto L_0x00b4;
    L_0x0080:
        r15 = 2;
        r15 = new java.lang.CharSequence[r15];
        r16 = 0;
        r17 = 0;
        r0 = r20;
        r1 = r17;
        r17 = r0.subSequence(r1, r3);
        r15[r16] = r17;
        r16 = 1;
        r17 = r3 + 2;
        r18 = r20.length();
        r0 = r20;
        r1 = r17;
        r2 = r18;
        r17 = r0.subSequence(r1, r2);
        r15[r16] = r17;
        r20 = android.text.TextUtils.concat(r15);
        r8 = r8 + -2;
        r3 = r3 + -1;
    L_0x00ad:
        r3 = r3 + 1;
        goto L_0x0027;
    L_0x00b1:
        r13 = 0;
        goto L_0x001c;
    L_0x00b4:
        r0 = r20;
        r15 = r0.charAt(r3);
        r16 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        r0 = r16;
        if (r15 != r0) goto L_0x00ad;
    L_0x00c1:
        r15 = 2;
        r15 = new java.lang.CharSequence[r15];
        r16 = 0;
        r17 = 0;
        r0 = r20;
        r1 = r17;
        r17 = r0.subSequence(r1, r3);
        r15[r16] = r17;
        r16 = 1;
        r17 = r3 + 1;
        r18 = r20.length();
        r0 = r20;
        r1 = r17;
        r2 = r18;
        r17 = r0.subSequence(r1, r2);
        r15[r16] = r17;
        r20 = android.text.TextUtils.concat(r15);
        r8 = r8 + -1;
        r3 = r3 + -1;
        goto L_0x00ad;
    L_0x00ef:
        r15 = r20.toString();
        r15 = r15.trim();
        r0 = r19;
        r0.lastSticker = r15;
        r15 = org.telegram.messenger.Emoji.isValidEmoji(r10);
        if (r15 != 0) goto L_0x0124;
    L_0x0101:
        r0 = r19;
        r15 = r0.lastSticker;
        r15 = org.telegram.messenger.Emoji.isValidEmoji(r15);
        if (r15 != 0) goto L_0x0124;
    L_0x010b:
        r0 = r19;
        r15 = r0.visible;
        if (r15 == 0) goto L_0x0008;
    L_0x0111:
        r15 = 0;
        r0 = r19;
        r0.visible = r15;
        r0 = r19;
        r15 = r0.delegate;
        r16 = 0;
        r15.needChangePanelVisibility(r16);
        r19.notifyDataSetChanged();
        goto L_0x0008;
    L_0x0124:
        r15 = 0;
        r0 = r19;
        r0.stickers = r15;
        r15 = 0;
        r0 = r19;
        r0.stickersParents = r15;
        r15 = 0;
        r0 = r19;
        r0.stickersMap = r15;
        r15 = 0;
        r0 = r19;
        r0.delayLocalResults = r15;
        r0 = r19;
        r15 = r0.currentAccount;
        r15 = org.telegram.messenger.DataQuery.getInstance(r15);
        r16 = 0;
        r11 = r15.getRecentStickersNoCopy(r16);
        r0 = r19;
        r15 = r0.currentAccount;
        r15 = org.telegram.messenger.DataQuery.getInstance(r15);
        r16 = 2;
        r7 = r15.getRecentStickersNoCopy(r16);
        r12 = 0;
        r3 = 0;
        r14 = r11.size();
    L_0x015a:
        if (r3 >= r14) goto L_0x017b;
    L_0x015c:
        r6 = r11.get(r3);
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;
        r0 = r19;
        r15 = r0.lastSticker;
        r0 = r19;
        r15 = r0.isValidSticker(r6, r15);
        if (r15 == 0) goto L_0x019f;
    L_0x016e:
        r15 = "recent";
        r0 = r19;
        r0.addStickerToResult(r6, r15);
        r12 = r12 + 1;
        r15 = 5;
        if (r12 < r15) goto L_0x019f;
    L_0x017b:
        r3 = 0;
        r14 = r7.size();
    L_0x0180:
        if (r3 >= r14) goto L_0x01a2;
    L_0x0182:
        r6 = r7.get(r3);
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;
        r0 = r19;
        r15 = r0.lastSticker;
        r0 = r19;
        r15 = r0.isValidSticker(r6, r15);
        if (r15 == 0) goto L_0x019c;
    L_0x0194:
        r15 = "fav";
        r0 = r19;
        r0.addStickerToResult(r6, r15);
    L_0x019c:
        r3 = r3 + 1;
        goto L_0x0180;
    L_0x019f:
        r3 = r3 + 1;
        goto L_0x015a;
    L_0x01a2:
        r0 = r19;
        r15 = r0.currentAccount;
        r15 = org.telegram.messenger.DataQuery.getInstance(r15);
        r4 = r15.getAllStickers();
        if (r4 == 0) goto L_0x0225;
    L_0x01b0:
        r0 = r19;
        r15 = r0.lastSticker;
        r15 = r4.get(r15);
        r15 = (java.util.ArrayList) r15;
        r9 = r15;
    L_0x01bb:
        if (r9 == 0) goto L_0x01de;
    L_0x01bd:
        r15 = r9.isEmpty();
        if (r15 != 0) goto L_0x01de;
    L_0x01c3:
        r5 = new java.util.ArrayList;
        r5.<init>(r9);
        r15 = r11.isEmpty();
        if (r15 != 0) goto L_0x01d8;
    L_0x01ce:
        r15 = new org.telegram.ui.Adapters.StickersAdapter$1;
        r0 = r19;
        r15.<init>(r7, r11);
        java.util.Collections.sort(r5, r15);
    L_0x01d8:
        r15 = 0;
        r0 = r19;
        r0.addStickersToResult(r5, r15);
    L_0x01de:
        r15 = org.telegram.messenger.SharedConfig.suggestStickers;
        if (r15 != 0) goto L_0x01eb;
    L_0x01e2:
        r0 = r19;
        r15 = r0.lastSticker;
        r0 = r19;
        r0.searchServerStickers(r15, r10);
    L_0x01eb:
        r0 = r19;
        r15 = r0.stickers;
        if (r15 == 0) goto L_0x0258;
    L_0x01f1:
        r0 = r19;
        r15 = r0.stickers;
        r15 = r15.isEmpty();
        if (r15 != 0) goto L_0x0258;
    L_0x01fb:
        r15 = org.telegram.messenger.SharedConfig.suggestStickers;
        if (r15 != 0) goto L_0x0227;
    L_0x01ff:
        r0 = r19;
        r15 = r0.stickers;
        r15 = r15.size();
        r16 = 5;
        r0 = r16;
        if (r15 >= r0) goto L_0x0227;
    L_0x020d:
        r15 = 1;
        r0 = r19;
        r0.delayLocalResults = r15;
        r0 = r19;
        r15 = r0.delegate;
        r16 = 0;
        r15.needChangePanelVisibility(r16);
        r15 = 0;
        r0 = r19;
        r0.visible = r15;
    L_0x0220:
        r19.notifyDataSetChanged();
        goto L_0x0008;
    L_0x0225:
        r9 = 0;
        goto L_0x01bb;
    L_0x0227:
        r19.checkStickerFilesExistAndDownload();
        r0 = r19;
        r0 = r0.delegate;
        r16 = r0;
        r0 = r19;
        r15 = r0.stickers;
        if (r15 == 0) goto L_0x0256;
    L_0x0236:
        r0 = r19;
        r15 = r0.stickers;
        r15 = r15.isEmpty();
        if (r15 != 0) goto L_0x0256;
    L_0x0240:
        r0 = r19;
        r15 = r0.stickersToLoad;
        r15 = r15.isEmpty();
        if (r15 == 0) goto L_0x0256;
    L_0x024a:
        r15 = 1;
    L_0x024b:
        r0 = r16;
        r0.needChangePanelVisibility(r15);
        r15 = 1;
        r0 = r19;
        r0.visible = r15;
        goto L_0x0220;
    L_0x0256:
        r15 = 0;
        goto L_0x024b;
    L_0x0258:
        r0 = r19;
        r15 = r0.visible;
        if (r15 == 0) goto L_0x0008;
    L_0x025e:
        r0 = r19;
        r15 = r0.delegate;
        r16 = 0;
        r15.needChangePanelVisibility(r16);
        r15 = 0;
        r0 = r19;
        r0.visible = r15;
        goto L_0x0008;
    L_0x026e:
        r15 = "";
        r0 = r19;
        r0.lastSticker = r15;
        r0 = r19;
        r15 = r0.visible;
        if (r15 == 0) goto L_0x0008;
    L_0x027b:
        r0 = r19;
        r15 = r0.stickers;
        if (r15 == 0) goto L_0x0008;
    L_0x0281:
        r15 = 0;
        r0 = r19;
        r0.visible = r15;
        r0 = r19;
        r15 = r0.delegate;
        r16 = 0;
        r15.needChangePanelVisibility(r16);
        goto L_0x0008;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.StickersAdapter.loadStikersForEmoji(java.lang.CharSequence):void");
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
