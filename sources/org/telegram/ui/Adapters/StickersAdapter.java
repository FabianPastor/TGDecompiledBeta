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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ArrayList arrayList;
        if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.fileDidFailToLoad) {
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

    /* JADX WARNING: Missing block: B:103:0x01dc, code skipped:
            if (r15.isEmpty() == false) goto L_0x01e8;
     */
    public void loadStikersForEmoji(java.lang.CharSequence r14, boolean r15) {
        /*
        r13 = this;
        r0 = 1;
        r1 = 0;
        if (r14 == 0) goto L_0x0014;
    L_0x0004:
        r2 = r14.length();
        if (r2 <= 0) goto L_0x0014;
    L_0x000a:
        r2 = r14.length();
        r3 = 14;
        if (r2 > r3) goto L_0x0014;
    L_0x0012:
        r2 = 1;
        goto L_0x0015;
    L_0x0014:
        r2 = 0;
    L_0x0015:
        r3 = r14.toString();
        r4 = r14.length();
        r5 = r14;
        r14 = 0;
    L_0x001f:
        r6 = 2;
        if (r14 >= r4) goto L_0x0081;
    L_0x0022:
        r7 = r5.charAt(r14);
        r8 = r4 + -1;
        if (r14 >= r8) goto L_0x0031;
    L_0x002a:
        r9 = r14 + 1;
        r9 = r5.charAt(r9);
        goto L_0x0032;
    L_0x0031:
        r9 = 0;
    L_0x0032:
        if (r14 >= r8) goto L_0x005e;
    L_0x0034:
        r8 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        if (r7 != r8) goto L_0x005e;
    L_0x0039:
        r8 = 57339; // 0xdffb float:8.0349E-41 double:2.8329E-319;
        if (r9 < r8) goto L_0x005e;
    L_0x003e:
        r8 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        if (r9 > r8) goto L_0x005e;
    L_0x0043:
        r6 = new java.lang.CharSequence[r6];
        r7 = r5.subSequence(r1, r14);
        r6[r1] = r7;
        r7 = r14 + 2;
        r8 = r5.length();
        r5 = r5.subSequence(r7, r8);
        r6[r0] = r5;
        r5 = android.text.TextUtils.concat(r6);
        r4 = r4 + -2;
        goto L_0x007d;
    L_0x005e:
        r8 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        if (r7 != r8) goto L_0x007f;
    L_0x0063:
        r6 = new java.lang.CharSequence[r6];
        r7 = r5.subSequence(r1, r14);
        r6[r1] = r7;
        r7 = r14 + 1;
        r8 = r5.length();
        r5 = r5.subSequence(r7, r8);
        r6[r0] = r5;
        r5 = android.text.TextUtils.concat(r6);
        r4 = r4 + -1;
    L_0x007d:
        r14 = r14 + -1;
    L_0x007f:
        r14 = r14 + r0;
        goto L_0x001f;
    L_0x0081:
        r14 = r5.toString();
        r13.lastSticker = r14;
        r14 = r13.stickersToLoad;
        r14.clear();
        if (r2 == 0) goto L_0x009e;
    L_0x008e:
        r14 = org.telegram.messenger.Emoji.isValidEmoji(r3);
        if (r14 != 0) goto L_0x009c;
    L_0x0094:
        r14 = r13.lastSticker;
        r14 = org.telegram.messenger.Emoji.isValidEmoji(r14);
        if (r14 == 0) goto L_0x009e;
    L_0x009c:
        r14 = 1;
        goto L_0x009f;
    L_0x009e:
        r14 = 0;
    L_0x009f:
        if (r14 == 0) goto L_0x00d6;
    L_0x00a1:
        r2 = r13.currentAccount;
        r2 = org.telegram.messenger.MediaDataController.getInstance(r2);
        r2 = r2.getEmojiAnimatedSticker(r5);
        if (r2 == 0) goto L_0x00d6;
    L_0x00ad:
        r4 = r13.currentAccount;
        r4 = org.telegram.messenger.MediaDataController.getInstance(r4);
        r5 = 4;
        r4 = r4.getStickerSets(r5);
        r5 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r0);
        r5 = r5.exists();
        if (r5 != 0) goto L_0x00d6;
    L_0x00c2:
        r5 = r13.currentAccount;
        r7 = org.telegram.messenger.FileLoader.getInstance(r5);
        r8 = org.telegram.messenger.ImageLocation.getForDocument(r2);
        r9 = r4.get(r1);
        r10 = 0;
        r11 = 1;
        r12 = 1;
        r7.loadFile(r8, r9, r10, r11, r12);
    L_0x00d6:
        if (r15 != 0) goto L_0x01ca;
    L_0x00d8:
        r2 = org.telegram.messenger.SharedConfig.suggestStickers;
        if (r2 == r6) goto L_0x01ca;
    L_0x00dc:
        if (r14 != 0) goto L_0x00e0;
    L_0x00de:
        goto L_0x01ca;
    L_0x00e0:
        r13.cancelEmojiSearch();
        r14 = 0;
        r13.stickers = r14;
        r13.stickersMap = r14;
        r15 = r13.lastReqId;
        if (r15 == 0) goto L_0x00f9;
    L_0x00ec:
        r15 = r13.currentAccount;
        r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r2 = r13.lastReqId;
        r15.cancelRequest(r2, r0);
        r13.lastReqId = r1;
    L_0x00f9:
        r13.delayLocalResults = r1;
        r15 = r13.currentAccount;
        r15 = org.telegram.messenger.MediaDataController.getInstance(r15);
        r15 = r15.getRecentStickersNoCopy(r1);
        r2 = r13.currentAccount;
        r2 = org.telegram.messenger.MediaDataController.getInstance(r2);
        r2 = r2.getRecentStickersNoCopy(r6);
        r4 = r15.size();
        r5 = 0;
        r6 = 0;
    L_0x0115:
        r7 = 5;
        if (r5 >= r4) goto L_0x0133;
    L_0x0118:
        r8 = r15.get(r5);
        r8 = (org.telegram.tgnet.TLRPC.Document) r8;
        r9 = r13.lastSticker;
        r9 = r13.isValidSticker(r8, r9);
        if (r9 == 0) goto L_0x0130;
    L_0x0126:
        r9 = "recent";
        r13.addStickerToResult(r8, r9);
        r6 = r6 + 1;
        if (r6 < r7) goto L_0x0130;
    L_0x012f:
        goto L_0x0133;
    L_0x0130:
        r5 = r5 + 1;
        goto L_0x0115;
    L_0x0133:
        r4 = r2.size();
        r5 = 0;
    L_0x0138:
        if (r5 >= r4) goto L_0x0150;
    L_0x013a:
        r6 = r2.get(r5);
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;
        r8 = r13.lastSticker;
        r8 = r13.isValidSticker(r6, r8);
        if (r8 == 0) goto L_0x014d;
    L_0x0148:
        r8 = "fav";
        r13.addStickerToResult(r6, r8);
    L_0x014d:
        r5 = r5 + 1;
        goto L_0x0138;
    L_0x0150:
        r4 = r13.currentAccount;
        r4 = org.telegram.messenger.MediaDataController.getInstance(r4);
        r4 = r4.getAllStickers();
        if (r4 == 0) goto L_0x0165;
    L_0x015c:
        r5 = r13.lastSticker;
        r4 = r4.get(r5);
        r4 = (java.util.ArrayList) r4;
        goto L_0x0166;
    L_0x0165:
        r4 = r14;
    L_0x0166:
        if (r4 == 0) goto L_0x0171;
    L_0x0168:
        r5 = r4.isEmpty();
        if (r5 != 0) goto L_0x0171;
    L_0x016e:
        r13.addStickersToResult(r4, r14);
    L_0x0171:
        r4 = r13.stickers;
        if (r4 == 0) goto L_0x017d;
    L_0x0175:
        r5 = new org.telegram.ui.Adapters.StickersAdapter$1;
        r5.<init>(r2, r15);
        java.util.Collections.sort(r4, r5);
    L_0x017d:
        r15 = org.telegram.messenger.SharedConfig.suggestStickers;
        if (r15 != 0) goto L_0x0186;
    L_0x0181:
        r15 = r13.lastSticker;
        r13.searchServerStickers(r15, r3);
    L_0x0186:
        r15 = r13.stickers;
        if (r15 == 0) goto L_0x01be;
    L_0x018a:
        r15 = r15.isEmpty();
        if (r15 != 0) goto L_0x01be;
    L_0x0190:
        r15 = org.telegram.messenger.SharedConfig.suggestStickers;
        if (r15 != 0) goto L_0x01a6;
    L_0x0194:
        r15 = r13.stickers;
        r15 = r15.size();
        if (r15 >= r7) goto L_0x01a6;
    L_0x019c:
        r13.delayLocalResults = r0;
        r14 = r13.delegate;
        r14.needChangePanelVisibility(r1);
        r13.visible = r1;
        goto L_0x01ba;
    L_0x01a6:
        r13.checkStickerFilesExistAndDownload();
        r15 = r13.stickersToLoad;
        r15 = r15.isEmpty();
        if (r15 == 0) goto L_0x01b3;
    L_0x01b1:
        r13.keywordResults = r14;
    L_0x01b3:
        r14 = r13.delegate;
        r14.needChangePanelVisibility(r15);
        r13.visible = r0;
    L_0x01ba:
        r13.notifyDataSetChanged();
        goto L_0x01c9;
    L_0x01be:
        r14 = r13.visible;
        if (r14 == 0) goto L_0x01c9;
    L_0x01c2:
        r14 = r13.delegate;
        r14.needChangePanelVisibility(r1);
        r13.visible = r1;
    L_0x01c9:
        return;
    L_0x01ca:
        r0 = r13.visible;
        if (r0 == 0) goto L_0x01e8;
    L_0x01ce:
        if (r15 != 0) goto L_0x01de;
    L_0x01d0:
        r15 = org.telegram.messenger.SharedConfig.suggestStickers;
        if (r15 == r6) goto L_0x01de;
    L_0x01d4:
        r15 = r13.keywordResults;
        if (r15 == 0) goto L_0x01de;
    L_0x01d8:
        r15 = r15.isEmpty();
        if (r15 == 0) goto L_0x01e8;
    L_0x01de:
        r13.visible = r1;
        r15 = r13.delegate;
        r15.needChangePanelVisibility(r1);
        r13.notifyDataSetChanged();
    L_0x01e8:
        if (r14 != 0) goto L_0x01ed;
    L_0x01ea:
        r13.searchEmojiByKeyword();
    L_0x01ed:
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
