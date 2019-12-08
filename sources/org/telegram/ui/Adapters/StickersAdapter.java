package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MediaDataController.KeywordResult;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
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
    private ArrayList<StickerResult> stickers;
    private HashMap<String, Document> stickersMap;
    private ArrayList<String> stickersToLoad = new ArrayList();
    private boolean visible;

    private class StickerResult {
        public Object parent;
        public Document sticker;

        public StickerResult(Document document, Object obj) {
            this.sticker = document;
            this.parent = obj;
        }
    }

    public interface StickersAdapterDelegate {
        void needChangePanelVisibility(boolean z);
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        return false;
    }

    public StickersAdapter(Context context, StickersAdapterDelegate stickersAdapterDelegate) {
        this.mContext = context;
        this.delegate = stickersAdapterDelegate;
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailedLoad);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailedLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ArrayList arrayList;
        if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.fileDidFailedLoad) {
            arrayList = this.stickers;
            if (arrayList != null && !arrayList.isEmpty() && !this.stickersToLoad.isEmpty() && this.visible) {
                boolean z = false;
                this.stickersToLoad.remove((String) objArr[0]);
                if (this.stickersToLoad.isEmpty()) {
                    ArrayList arrayList2 = this.stickers;
                    if (!(arrayList2 == null || arrayList2.isEmpty() || !this.stickersToLoad.isEmpty())) {
                        z = true;
                    }
                    if (z) {
                        this.keywordResults = null;
                    }
                    this.delegate.needChangePanelVisibility(z);
                }
            }
        } else if (i == NotificationCenter.newEmojiSuggestionsAvailable) {
            arrayList = this.keywordResults;
            if ((arrayList == null || arrayList.isEmpty()) && !TextUtils.isEmpty(this.lastSticker) && getItemCount() == 0) {
                searchEmojiByKeyword();
            }
        }
    }

    private boolean checkStickerFilesExistAndDownload() {
        int i = 0;
        if (this.stickers == null) {
            return false;
        }
        this.stickersToLoad.clear();
        int min = Math.min(6, this.stickers.size());
        while (i < min) {
            StickerResult stickerResult = (StickerResult) this.stickers.get(i);
            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(stickerResult.sticker.thumbs, 90);
            if (closestPhotoSizeWithSize instanceof TL_photoSize) {
                String str = "webp";
                if (!FileLoader.getPathToAttach(closestPhotoSizeWithSize, str, true).exists()) {
                    this.stickersToLoad.add(FileLoader.getAttachFileName(closestPhotoSizeWithSize, str));
                    FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForDocument(closestPhotoSizeWithSize, stickerResult.sticker), stickerResult.parent, "webp", 1, 1);
                }
            }
            i++;
        }
        return this.stickersToLoad.isEmpty();
    }

    private boolean isValidSticker(Document document, String str) {
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                String str2 = documentAttribute.alt;
                if (str2 != null && str2.contains(str)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private void addStickerToResult(Document document, Object obj) {
        if (document != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(document.dc_id);
            stringBuilder.append("_");
            stringBuilder.append(document.id);
            String stringBuilder2 = stringBuilder.toString();
            HashMap hashMap = this.stickersMap;
            if (hashMap == null || !hashMap.containsKey(stringBuilder2)) {
                if (this.stickers == null) {
                    this.stickers = new ArrayList();
                    this.stickersMap = new HashMap();
                }
                this.stickers.add(new StickerResult(document, obj));
                this.stickersMap.put(stringBuilder2, document);
            }
        }
    }

    private void addStickersToResult(ArrayList<Document> arrayList, Object obj) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            Object obj2 = obj;
            for (int i = 0; i < size; i++) {
                Document document = (Document) arrayList.get(i);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(document.dc_id);
                stringBuilder.append("_");
                stringBuilder.append(document.id);
                String stringBuilder2 = stringBuilder.toString();
                HashMap hashMap = this.stickersMap;
                if (hashMap == null || !hashMap.containsKey(stringBuilder2)) {
                    if (this.stickers == null) {
                        this.stickers = new ArrayList();
                        this.stickersMap = new HashMap();
                    }
                    int size2 = document.attributes.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i2);
                        if (documentAttribute instanceof TL_documentAttributeSticker) {
                            obj2 = documentAttribute.stickerset;
                            break;
                        }
                    }
                    this.stickers.add(new StickerResult(document, obj2));
                    this.stickersMap.put(stringBuilder2, document);
                }
            }
        }
    }

    public void hide() {
        if (this.visible) {
            if (this.stickers == null) {
                ArrayList arrayList = this.keywordResults;
                if (arrayList == null || arrayList.isEmpty()) {
                    return;
                }
            }
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
        }
    }

    private void cancelEmojiSearch() {
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.searchRunnable = null;
        }
    }

    private void searchEmojiByKeyword() {
        String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
        if (!Arrays.equals(currentKeyboardLanguage, this.lastSearchKeyboardLanguage)) {
            MediaDataController.getInstance(this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
        }
        this.lastSearchKeyboardLanguage = currentKeyboardLanguage;
        String str = this.lastSticker;
        cancelEmojiSearch();
        this.searchRunnable = new -$$Lambda$StickersAdapter$D58rW58m-jlo8ZaeUedvcgRz2Fw(this, str);
        ArrayList arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(this.searchRunnable, 1000);
        } else {
            this.searchRunnable.run();
        }
    }

    public /* synthetic */ void lambda$searchEmojiByKeyword$1$StickersAdapter(String str) {
        MediaDataController.getInstance(this.currentAccount).getEmojiSuggestions(this.lastSearchKeyboardLanguage, str, true, new -$$Lambda$StickersAdapter$IWP-RbXaJ9N4HusD1vEW5c2KLHE(this, str));
    }

    public /* synthetic */ void lambda$null$0$StickersAdapter(String str, ArrayList arrayList, String str2) {
        if (str.equals(this.lastSticker)) {
            if (!arrayList.isEmpty()) {
                this.keywordResults = arrayList;
            }
            notifyDataSetChanged();
            StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
            int isEmpty = arrayList.isEmpty() ^ 1;
            this.visible = isEmpty;
            stickersAdapterDelegate.needChangePanelVisibility(isEmpty);
        }
    }

    /* JADX WARNING: Missing block: B:18:0x0041, code skipped:
            if (r5.charAt(r7) <= 57343) goto L_0x005d;
     */
    /* JADX WARNING: Missing block: B:24:0x005b, code skipped:
            if (r5.charAt(r7) != 9794) goto L_0x0078;
     */
    public void loadStikersForEmoji(java.lang.CharSequence r11, boolean r12) {
        /*
        r10 = this;
        r0 = 1;
        r1 = 0;
        if (r11 == 0) goto L_0x0014;
    L_0x0004:
        r2 = r11.length();
        if (r2 <= 0) goto L_0x0014;
    L_0x000a:
        r2 = r11.length();
        r3 = 14;
        if (r2 > r3) goto L_0x0014;
    L_0x0012:
        r2 = 1;
        goto L_0x0015;
    L_0x0014:
        r2 = 0;
    L_0x0015:
        r3 = r11.toString();
        r4 = r11.length();
        r5 = r11;
        r11 = 0;
    L_0x001f:
        r6 = 2;
        if (r11 >= r4) goto L_0x009f;
    L_0x0022:
        r7 = r4 + -1;
        if (r11 >= r7) goto L_0x0078;
    L_0x0026:
        r7 = r5.charAt(r11);
        r8 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        if (r7 != r8) goto L_0x0043;
    L_0x002f:
        r7 = r11 + 1;
        r8 = r5.charAt(r7);
        r9 = 57339; // 0xdffb float:8.0349E-41 double:2.8329E-319;
        if (r8 < r9) goto L_0x0043;
    L_0x003a:
        r7 = r5.charAt(r7);
        r8 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        if (r7 <= r8) goto L_0x005d;
    L_0x0043:
        r7 = r5.charAt(r11);
        r8 = 8205; // 0x200d float:1.1498E-41 double:4.054E-320;
        if (r7 != r8) goto L_0x0078;
    L_0x004b:
        r7 = r11 + 1;
        r8 = r5.charAt(r7);
        r9 = 9792; // 0x2640 float:1.3722E-41 double:4.838E-320;
        if (r8 == r9) goto L_0x005d;
    L_0x0055:
        r7 = r5.charAt(r7);
        r8 = 9794; // 0x2642 float:1.3724E-41 double:4.839E-320;
        if (r7 != r8) goto L_0x0078;
    L_0x005d:
        r6 = new java.lang.CharSequence[r6];
        r7 = r5.subSequence(r1, r11);
        r6[r1] = r7;
        r7 = r11 + 2;
        r8 = r5.length();
        r5 = r5.subSequence(r7, r8);
        r6[r0] = r5;
        r5 = android.text.TextUtils.concat(r6);
        r4 = r4 + -2;
        goto L_0x009b;
    L_0x0078:
        r7 = r5.charAt(r11);
        r8 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        if (r7 != r8) goto L_0x009d;
    L_0x0081:
        r6 = new java.lang.CharSequence[r6];
        r7 = r5.subSequence(r1, r11);
        r6[r1] = r7;
        r7 = r11 + 1;
        r8 = r5.length();
        r5 = r5.subSequence(r7, r8);
        r6[r0] = r5;
        r5 = android.text.TextUtils.concat(r6);
        r4 = r4 + -1;
    L_0x009b:
        r11 = r11 + -1;
    L_0x009d:
        r11 = r11 + r0;
        goto L_0x001f;
    L_0x009f:
        r11 = r5.toString();
        r10.lastSticker = r11;
        r11 = r10.stickersToLoad;
        r11.clear();
        if (r2 == 0) goto L_0x00bc;
    L_0x00ac:
        r11 = org.telegram.messenger.Emoji.isValidEmoji(r3);
        if (r11 != 0) goto L_0x00ba;
    L_0x00b2:
        r11 = r10.lastSticker;
        r11 = org.telegram.messenger.Emoji.isValidEmoji(r11);
        if (r11 == 0) goto L_0x00bc;
    L_0x00ba:
        r11 = 1;
        goto L_0x00bd;
    L_0x00bc:
        r11 = 0;
    L_0x00bd:
        if (r12 != 0) goto L_0x01af;
    L_0x00bf:
        r12 = org.telegram.messenger.SharedConfig.suggestStickers;
        if (r12 == r6) goto L_0x01af;
    L_0x00c3:
        if (r11 != 0) goto L_0x00c7;
    L_0x00c5:
        goto L_0x01af;
    L_0x00c7:
        r10.cancelEmojiSearch();
        r11 = 0;
        r10.stickers = r11;
        r10.stickersMap = r11;
        r12 = r10.lastReqId;
        if (r12 == 0) goto L_0x00e0;
    L_0x00d3:
        r12 = r10.currentAccount;
        r12 = org.telegram.tgnet.ConnectionsManager.getInstance(r12);
        r2 = r10.lastReqId;
        r12.cancelRequest(r2, r0);
        r10.lastReqId = r1;
    L_0x00e0:
        r10.delayLocalResults = r1;
        r12 = r10.currentAccount;
        r12 = org.telegram.messenger.MediaDataController.getInstance(r12);
        r12 = r12.getRecentStickersNoCopy(r1);
        r2 = r10.currentAccount;
        r2 = org.telegram.messenger.MediaDataController.getInstance(r2);
        r2 = r2.getRecentStickersNoCopy(r6);
        r4 = r12.size();
        r5 = 0;
        r6 = 0;
    L_0x00fc:
        r7 = 5;
        if (r5 >= r4) goto L_0x011a;
    L_0x00ff:
        r8 = r12.get(r5);
        r8 = (org.telegram.tgnet.TLRPC.Document) r8;
        r9 = r10.lastSticker;
        r9 = r10.isValidSticker(r8, r9);
        if (r9 == 0) goto L_0x0117;
    L_0x010d:
        r9 = "recent";
        r10.addStickerToResult(r8, r9);
        r6 = r6 + 1;
        if (r6 < r7) goto L_0x0117;
    L_0x0116:
        goto L_0x011a;
    L_0x0117:
        r5 = r5 + 1;
        goto L_0x00fc;
    L_0x011a:
        r4 = r2.size();
        r5 = 0;
    L_0x011f:
        if (r5 >= r4) goto L_0x0137;
    L_0x0121:
        r6 = r2.get(r5);
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;
        r8 = r10.lastSticker;
        r8 = r10.isValidSticker(r6, r8);
        if (r8 == 0) goto L_0x0134;
    L_0x012f:
        r8 = "fav";
        r10.addStickerToResult(r6, r8);
    L_0x0134:
        r5 = r5 + 1;
        goto L_0x011f;
    L_0x0137:
        r4 = r10.currentAccount;
        r4 = org.telegram.messenger.MediaDataController.getInstance(r4);
        r4 = r4.getAllStickers();
        if (r4 == 0) goto L_0x014c;
    L_0x0143:
        r5 = r10.lastSticker;
        r4 = r4.get(r5);
        r4 = (java.util.ArrayList) r4;
        goto L_0x014d;
    L_0x014c:
        r4 = r11;
    L_0x014d:
        if (r4 == 0) goto L_0x0158;
    L_0x014f:
        r5 = r4.isEmpty();
        if (r5 != 0) goto L_0x0158;
    L_0x0155:
        r10.addStickersToResult(r4, r11);
    L_0x0158:
        r4 = r10.stickers;
        r5 = new org.telegram.ui.Adapters.StickersAdapter$1;
        r5.<init>(r2, r12);
        java.util.Collections.sort(r4, r5);
        r12 = org.telegram.messenger.SharedConfig.suggestStickers;
        if (r12 != 0) goto L_0x016b;
    L_0x0166:
        r12 = r10.lastSticker;
        r10.searchServerStickers(r12, r3);
    L_0x016b:
        r12 = r10.stickers;
        if (r12 == 0) goto L_0x01a3;
    L_0x016f:
        r12 = r12.isEmpty();
        if (r12 != 0) goto L_0x01a3;
    L_0x0175:
        r12 = org.telegram.messenger.SharedConfig.suggestStickers;
        if (r12 != 0) goto L_0x018b;
    L_0x0179:
        r12 = r10.stickers;
        r12 = r12.size();
        if (r12 >= r7) goto L_0x018b;
    L_0x0181:
        r10.delayLocalResults = r0;
        r11 = r10.delegate;
        r11.needChangePanelVisibility(r1);
        r10.visible = r1;
        goto L_0x019f;
    L_0x018b:
        r10.checkStickerFilesExistAndDownload();
        r12 = r10.stickersToLoad;
        r12 = r12.isEmpty();
        if (r12 == 0) goto L_0x0198;
    L_0x0196:
        r10.keywordResults = r11;
    L_0x0198:
        r11 = r10.delegate;
        r11.needChangePanelVisibility(r12);
        r10.visible = r0;
    L_0x019f:
        r10.notifyDataSetChanged();
        goto L_0x01ae;
    L_0x01a3:
        r11 = r10.visible;
        if (r11 == 0) goto L_0x01ae;
    L_0x01a7:
        r11 = r10.delegate;
        r11.needChangePanelVisibility(r1);
        r10.visible = r1;
    L_0x01ae:
        return;
    L_0x01af:
        r12 = r10.visible;
        if (r12 == 0) goto L_0x01c7;
    L_0x01b3:
        r12 = r10.keywordResults;
        if (r12 == 0) goto L_0x01bd;
    L_0x01b7:
        r12 = r12.isEmpty();
        if (r12 == 0) goto L_0x01c7;
    L_0x01bd:
        r10.visible = r1;
        r12 = r10.delegate;
        r12.needChangePanelVisibility(r1);
        r10.notifyDataSetChanged();
    L_0x01c7:
        if (r11 != 0) goto L_0x01cc;
    L_0x01c9:
        r10.searchEmojiByKeyword();
    L_0x01cc:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.StickersAdapter.loadStikersForEmoji(java.lang.CharSequence, boolean):void");
    }

    private void searchServerStickers(String str, String str2) {
        TL_messages_getStickers tL_messages_getStickers = new TL_messages_getStickers();
        tL_messages_getStickers.emoticon = str2;
        tL_messages_getStickers.hash = 0;
        this.lastReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getStickers, new -$$Lambda$StickersAdapter$XAmkAEU9b5B6bSCEO_rxuKc1F8U(this, str));
    }

    public /* synthetic */ void lambda$searchServerStickers$3$StickersAdapter(String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$StickersAdapter$cGnqxTxY2G2rhm3SXL_mI51dUtI(this, str, tLObject));
    }

    public /* synthetic */ void lambda$null$2$StickersAdapter(String str, TLObject tLObject) {
        int i = 0;
        this.lastReqId = 0;
        if (str.equals(this.lastSticker) && (tLObject instanceof TL_messages_stickers)) {
            this.delayLocalResults = false;
            TL_messages_stickers tL_messages_stickers = (TL_messages_stickers) tLObject;
            ArrayList arrayList = this.stickers;
            int size = arrayList != null ? arrayList.size() : 0;
            ArrayList arrayList2 = tL_messages_stickers.stickers;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("sticker_search_");
            stringBuilder.append(str);
            addStickersToResult(arrayList2, stringBuilder.toString());
            ArrayList arrayList3 = this.stickers;
            if (arrayList3 != null) {
                i = arrayList3.size();
            }
            if (!this.visible) {
                arrayList3 = this.stickers;
                if (!(arrayList3 == null || arrayList3.isEmpty())) {
                    checkStickerFilesExistAndDownload();
                    boolean isEmpty = this.stickersToLoad.isEmpty();
                    if (isEmpty) {
                        this.keywordResults = null;
                    }
                    this.delegate.needChangePanelVisibility(isEmpty);
                    this.visible = true;
                }
            }
            if (size != i) {
                notifyDataSetChanged();
            }
        }
    }

    public void clearStickers() {
        if (!this.delayLocalResults && this.lastReqId == 0) {
            if (this.stickersToLoad.isEmpty()) {
                this.lastSticker = null;
                this.stickers = null;
                this.stickersMap = null;
            }
            this.keywordResults = null;
            notifyDataSetChanged();
            if (this.lastReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
                this.lastReqId = 0;
            }
        }
    }

    public boolean isShowingKeywords() {
        ArrayList arrayList = this.keywordResults;
        return (arrayList == null || arrayList.isEmpty()) ? false : true;
    }

    public int getItemCount() {
        ArrayList arrayList = this.keywordResults;
        if (arrayList != null && !arrayList.isEmpty()) {
            return this.keywordResults.size();
        }
        int size;
        if (!this.delayLocalResults) {
            arrayList = this.stickers;
            if (arrayList != null) {
                size = arrayList.size();
                return size;
            }
        }
        size = 0;
        return size;
    }

    public Object getItem(int i) {
        ArrayList arrayList = this.keywordResults;
        if (arrayList != null && !arrayList.isEmpty()) {
            return ((KeywordResult) this.keywordResults.get(i)).emoji;
        }
        arrayList = this.stickers;
        Object obj = (arrayList == null || i < 0 || i >= arrayList.size()) ? null : ((StickerResult) this.stickers.get(i)).sticker;
        return obj;
    }

    public Object getItemParent(int i) {
        ArrayList arrayList = this.keywordResults;
        Object obj = null;
        if (arrayList != null && !arrayList.isEmpty()) {
            return null;
        }
        arrayList = this.stickers;
        if (arrayList != null && i >= 0 && i < arrayList.size()) {
            obj = ((StickerResult) this.stickers.get(i)).parent;
        }
        return obj;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View emojiReplacementCell;
        if (i != 0) {
            emojiReplacementCell = new EmojiReplacementCell(this.mContext);
        } else {
            emojiReplacementCell = new StickerCell(this.mContext);
        }
        return new Holder(emojiReplacementCell);
    }

    public int getItemViewType(int i) {
        ArrayList arrayList = this.keywordResults;
        return (arrayList == null || arrayList.isEmpty()) ? 0 : 1;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        int i2 = 2;
        if (itemViewType == 0) {
            if (i != 0) {
                i2 = i == this.stickers.size() - 1 ? 1 : 0;
            } else if (this.stickers.size() != 1) {
                i2 = -1;
            }
            StickerCell stickerCell = (StickerCell) viewHolder.itemView;
            StickerResult stickerResult = (StickerResult) this.stickers.get(i);
            stickerCell.setSticker(stickerResult.sticker, stickerResult.parent, i2);
            stickerCell.setClearsInputField(true);
        } else if (itemViewType == 1) {
            if (i != 0) {
                i2 = i == this.keywordResults.size() - 1 ? 1 : 0;
            } else if (this.keywordResults.size() != 1) {
                i2 = -1;
            }
            ((EmojiReplacementCell) viewHolder.itemView).setEmoji(((KeywordResult) this.keywordResults.get(i)).emoji, i2);
        }
    }
}
