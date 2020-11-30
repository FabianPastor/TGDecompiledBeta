package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoSizeProgressive;
import org.telegram.ui.Cells.EmojiReplacementCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Components.RecyclerListView;

public class StickersAdapter extends RecyclerListView.SelectionAdapter implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount = UserConfig.selectedAccount;
    private boolean delayLocalResults;
    private StickersAdapterDelegate delegate;
    private ArrayList<MediaDataController.KeywordResult> keywordResults;
    private int lastReqId;
    private String[] lastSearchKeyboardLanguage;
    private String lastSticker;
    private Context mContext;
    private Runnable searchRunnable;
    private ArrayList<StickerResult> stickers;
    private HashMap<String, TLRPC$Document> stickersMap;
    private ArrayList<String> stickersToLoad = new ArrayList<>();
    private boolean visible;

    public interface StickersAdapterDelegate {
        void needChangePanelVisibility(boolean z);
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    private static class StickerResult {
        public Object parent;
        public TLRPC$Document sticker;

        public StickerResult(TLRPC$Document tLRPC$Document, Object obj) {
            this.sticker = tLRPC$Document;
            this.parent = obj;
        }
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
        if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.fileDidFailToLoad) {
            ArrayList<StickerResult> arrayList = this.stickers;
            if (arrayList != null && !arrayList.isEmpty() && !this.stickersToLoad.isEmpty() && this.visible) {
                boolean z = false;
                this.stickersToLoad.remove(objArr[0]);
                if (this.stickersToLoad.isEmpty()) {
                    ArrayList<StickerResult> arrayList2 = this.stickers;
                    if (arrayList2 != null && !arrayList2.isEmpty() && this.stickersToLoad.isEmpty()) {
                        z = true;
                    }
                    if (z) {
                        this.keywordResults = null;
                    }
                    this.delegate.needChangePanelVisibility(z);
                }
            }
        } else if (i == NotificationCenter.newEmojiSuggestionsAvailable) {
            ArrayList<MediaDataController.KeywordResult> arrayList3 = this.keywordResults;
            if ((arrayList3 == null || arrayList3.isEmpty()) && !TextUtils.isEmpty(this.lastSticker) && getItemCount() == 0) {
                searchEmojiByKeyword();
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

    public void hide() {
        ArrayList<MediaDataController.KeywordResult> arrayList;
        if (!this.visible) {
            return;
        }
        if (this.stickers != null || ((arrayList = this.keywordResults) != null && !arrayList.isEmpty())) {
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
        this.searchRunnable = new Runnable(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                StickersAdapter.this.lambda$searchEmojiByKeyword$1$StickersAdapter(this.f$1);
            }
        };
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(this.searchRunnable, 1000);
        } else {
            this.searchRunnable.run();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchEmojiByKeyword$1 */
    public /* synthetic */ void lambda$searchEmojiByKeyword$1$StickersAdapter(String str) {
        MediaDataController.getInstance(this.currentAccount).getEmojiSuggestions(this.lastSearchKeyboardLanguage, str, true, new MediaDataController.KeywordResultCallback(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(ArrayList arrayList, String str) {
                StickersAdapter.this.lambda$null$0$StickersAdapter(this.f$1, arrayList, str);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$StickersAdapter(String str, ArrayList arrayList, String str2) {
        if (str.equals(this.lastSticker)) {
            if (!arrayList.isEmpty()) {
                this.keywordResults = arrayList;
            }
            notifyDataSetChanged();
            StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
            boolean z = !arrayList.isEmpty();
            this.visible = z;
            stickersAdapterDelegate.needChangePanelVisibility(z);
        }
    }

    public void loadStikersForEmoji(CharSequence charSequence, boolean z) {
        String str;
        ArrayList<MediaDataController.KeywordResult> arrayList;
        TLRPC$Document emojiAnimatedSticker;
        boolean z2 = charSequence != null && charSequence.length() > 0 && charSequence.length() <= 14;
        if (z2) {
            str = charSequence.toString();
            int length = charSequence.length();
            int i = 0;
            while (i < length) {
                char charAt = charSequence.charAt(i);
                int i2 = length - 1;
                char charAt2 = i < i2 ? charSequence.charAt(i + 1) : 0;
                if (i < i2 && charAt == 55356 && charAt2 >= 57339 && charAt2 <= 57343) {
                    charSequence = TextUtils.concat(new CharSequence[]{charSequence.subSequence(0, i), charSequence.subSequence(i + 2, charSequence.length())});
                    length -= 2;
                } else if (charAt == 65039) {
                    charSequence = TextUtils.concat(new CharSequence[]{charSequence.subSequence(0, i), charSequence.subSequence(i + 1, charSequence.length())});
                    length--;
                } else {
                    i++;
                }
                i--;
                i++;
            }
        } else {
            str = "";
        }
        this.lastSticker = charSequence.toString().trim();
        this.stickersToLoad.clear();
        boolean z3 = z2 && (Emoji.isValidEmoji(str) || Emoji.isValidEmoji(this.lastSticker));
        if (z3 && (emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(charSequence)) != null) {
            ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(4);
            if (!FileLoader.getPathToAttach(emojiAnimatedSticker, true).exists()) {
                FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForDocument(emojiAnimatedSticker), stickerSets.get(0), (String) null, 1, 1);
            }
        }
        if (z || SharedConfig.suggestStickers == 2 || !z3) {
            if (this.visible && (z || SharedConfig.suggestStickers == 2 || (arrayList = this.keywordResults) == null || arrayList.isEmpty())) {
                this.visible = false;
                this.delegate.needChangePanelVisibility(false);
                notifyDataSetChanged();
            }
            if (!z3) {
                searchEmojiByKeyword();
                return;
            }
            return;
        }
        cancelEmojiSearch();
        this.stickers = null;
        this.stickersMap = null;
        if (this.lastReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
            this.lastReqId = 0;
        }
        this.delayLocalResults = false;
        final ArrayList<TLRPC$Document> recentStickersNoCopy = MediaDataController.getInstance(this.currentAccount).getRecentStickersNoCopy(0);
        final ArrayList<TLRPC$Document> recentStickersNoCopy2 = MediaDataController.getInstance(this.currentAccount).getRecentStickersNoCopy(2);
        int min = Math.min(20, recentStickersNoCopy.size());
        int i3 = 0;
        for (int i4 = 0; i4 < min; i4++) {
            TLRPC$Document tLRPC$Document = recentStickersNoCopy.get(i4);
            if (isValidSticker(tLRPC$Document, this.lastSticker)) {
                addStickerToResult(tLRPC$Document, "recent");
                i3++;
                if (i3 >= 5) {
                    break;
                }
            }
        }
        int size = recentStickersNoCopy2.size();
        for (int i5 = 0; i5 < size; i5++) {
            TLRPC$Document tLRPC$Document2 = recentStickersNoCopy2.get(i5);
            if (isValidSticker(tLRPC$Document2, this.lastSticker)) {
                addStickerToResult(tLRPC$Document2, "fav");
            }
        }
        HashMap<String, ArrayList<TLRPC$Document>> allStickers = MediaDataController.getInstance(this.currentAccount).getAllStickers();
        ArrayList arrayList2 = allStickers != null ? allStickers.get(this.lastSticker) : null;
        if (arrayList2 != null && !arrayList2.isEmpty()) {
            addStickersToResult(arrayList2, (Object) null);
        }
        ArrayList<StickerResult> arrayList3 = this.stickers;
        if (arrayList3 != null) {
            Collections.sort(arrayList3, new Object(this) {
                public /* synthetic */ Comparator reversed() {
                    return Comparator.CC.$default$reversed(this);
                }

                public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                    return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                }

                public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                    return Comparator.CC.$default$thenComparing(this, function, comparator);
                }

                public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                    return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                }

                public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                    return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                }

                public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                    return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                }

                public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                    return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
                }

                private int getIndex(StickerResult stickerResult) {
                    for (int i = 0; i < recentStickersNoCopy2.size(); i++) {
                        if (((TLRPC$Document) recentStickersNoCopy2.get(i)).id == stickerResult.sticker.id) {
                            return i + 2000000;
                        }
                    }
                    for (int i2 = 0; i2 < Math.min(20, recentStickersNoCopy.size()); i2++) {
                        if (((TLRPC$Document) recentStickersNoCopy.get(i2)).id == stickerResult.sticker.id) {
                            return (recentStickersNoCopy.size() - i2) + 1000000;
                        }
                    }
                    return -1;
                }

                public int compare(StickerResult stickerResult, StickerResult stickerResult2) {
                    boolean isAnimatedStickerDocument = MessageObject.isAnimatedStickerDocument(stickerResult.sticker, true);
                    boolean isAnimatedStickerDocument2 = MessageObject.isAnimatedStickerDocument(stickerResult2.sticker, true);
                    if (isAnimatedStickerDocument == isAnimatedStickerDocument2) {
                        int index = getIndex(stickerResult);
                        int index2 = getIndex(stickerResult2);
                        if (index > index2) {
                            return -1;
                        }
                        if (index < index2) {
                            return 1;
                        }
                        return 0;
                    } else if (!isAnimatedStickerDocument || isAnimatedStickerDocument2) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }
        if (SharedConfig.suggestStickers == 0) {
            searchServerStickers(this.lastSticker, str);
        }
        ArrayList<StickerResult> arrayList4 = this.stickers;
        if (arrayList4 != null && !arrayList4.isEmpty()) {
            if (SharedConfig.suggestStickers != 0 || this.stickers.size() >= 5) {
                checkStickerFilesExistAndDownload();
                boolean isEmpty = this.stickersToLoad.isEmpty();
                if (isEmpty) {
                    this.keywordResults = null;
                }
                this.delegate.needChangePanelVisibility(isEmpty);
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
                StickersAdapter.this.lambda$searchServerStickers$3$StickersAdapter(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchServerStickers$3 */
    public /* synthetic */ void lambda$searchServerStickers$3$StickersAdapter(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, tLObject) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                StickersAdapter.this.lambda$null$2$StickersAdapter(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$StickersAdapter(String str, TLObject tLObject) {
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
            if (!this.visible && (arrayList = this.stickers) != null && !arrayList.isEmpty()) {
                checkStickerFilesExistAndDownload();
                boolean isEmpty = this.stickersToLoad.isEmpty();
                if (isEmpty) {
                    this.keywordResults = null;
                }
                this.delegate.needChangePanelVisibility(isEmpty);
                this.visible = true;
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
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        return arrayList != null && !arrayList.isEmpty();
    }

    public int getItemCount() {
        ArrayList<StickerResult> arrayList;
        ArrayList<MediaDataController.KeywordResult> arrayList2 = this.keywordResults;
        if (arrayList2 != null && !arrayList2.isEmpty()) {
            return this.keywordResults.size();
        }
        if (this.delayLocalResults || (arrayList = this.stickers) == null) {
            return 0;
        }
        return arrayList.size();
    }

    public Object getItem(int i) {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty()) {
            ArrayList<StickerResult> arrayList2 = this.stickers;
            if (arrayList2 == null || i < 0 || i >= arrayList2.size()) {
                return null;
            }
            return this.stickers.get(i).sticker;
        } else if (i < 0 || i >= this.keywordResults.size()) {
            return null;
        } else {
            return this.keywordResults.get(i).emoji;
        }
    }

    public Object getItemParent(int i) {
        ArrayList<StickerResult> arrayList;
        ArrayList<MediaDataController.KeywordResult> arrayList2 = this.keywordResults;
        if ((arrayList2 == null || arrayList2.isEmpty()) && (arrayList = this.stickers) != null && i >= 0 && i < arrayList.size()) {
            return this.stickers.get(i).parent;
        }
        return null;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (i != 0) {
            view = new EmojiReplacementCell(this.mContext);
        } else {
            view = new StickerCell(this.mContext);
        }
        return new RecyclerListView.Holder(view);
    }

    public int getItemViewType(int i) {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        return (arrayList == null || arrayList.isEmpty()) ? 0 : 1;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        int i2 = 2;
        if (itemViewType == 0) {
            if (i != 0) {
                i2 = i == this.stickers.size() - 1 ? 1 : 0;
            } else if (this.stickers.size() != 1) {
                i2 = -1;
            }
            StickerCell stickerCell = (StickerCell) viewHolder.itemView;
            StickerResult stickerResult = this.stickers.get(i);
            stickerCell.setSticker(stickerResult.sticker, stickerResult.parent, i2);
            stickerCell.setClearsInputField(true);
        } else if (itemViewType == 1) {
            if (i != 0) {
                i2 = i == this.keywordResults.size() - 1 ? 1 : 0;
            } else if (this.keywordResults.size() != 1) {
                i2 = -1;
            }
            ((EmojiReplacementCell) viewHolder.itemView).setEmoji(this.keywordResults.get(i).emoji, i2);
        }
    }
}
