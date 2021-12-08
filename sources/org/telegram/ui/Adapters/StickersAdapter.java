package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmojiReplacementCell;
import org.telegram.ui.Components.RecyclerListView;

public class StickersAdapter extends RecyclerListView.SelectionAdapter implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount;
    private StickersAdapterDelegate delegate;
    private ArrayList<MediaDataController.KeywordResult> keywordResults;
    private String lastSearch;
    private String[] lastSearchKeyboardLanguage;
    private Context mContext;
    private final Theme.ResourcesProvider resourcesProvider;
    private Runnable searchRunnable;
    private boolean visible;

    public interface StickersAdapterDelegate {
        void needChangePanelVisibility(boolean z);
    }

    public int getItemViewType(int i) {
        return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    public StickersAdapter(Context context, StickersAdapterDelegate stickersAdapterDelegate, Theme.ResourcesProvider resourcesProvider2) {
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.mContext = context;
        this.delegate = stickersAdapterDelegate;
        this.resourcesProvider = resourcesProvider2;
        MediaDataController.getInstance(i).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.newEmojiSuggestionsAvailable) {
            ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
            if ((arrayList == null || arrayList.isEmpty()) && !TextUtils.isEmpty(this.lastSearch) && getItemCount() == 0) {
                searchEmojiByKeyword();
            }
        }
    }

    public void hide() {
        ArrayList<MediaDataController.KeywordResult> arrayList;
        if (this.visible && (arrayList = this.keywordResults) != null && !arrayList.isEmpty()) {
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
        String str = this.lastSearch;
        cancelEmojiSearch();
        this.searchRunnable = new StickersAdapter$$ExternalSyntheticLambda0(this, str);
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(this.searchRunnable, 1000);
        } else {
            this.searchRunnable.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchEmojiByKeyword$1(String str) {
        MediaDataController.getInstance(this.currentAccount).getEmojiSuggestions(this.lastSearchKeyboardLanguage, str, true, new StickersAdapter$$ExternalSyntheticLambda1(this, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchEmojiByKeyword$0(String str, ArrayList arrayList, String str2) {
        if (str.equals(this.lastSearch)) {
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

    public void searchEmojiByKeyword(CharSequence charSequence) {
        String str;
        ArrayList<MediaDataController.KeywordResult> arrayList;
        TLRPC$Document emojiAnimatedSticker;
        boolean z = charSequence != null && charSequence.length() > 0 && charSequence.length() <= 14;
        if (z) {
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
        this.lastSearch = charSequence.toString().trim();
        boolean z2 = z && (Emoji.isValidEmoji(str) || Emoji.isValidEmoji(this.lastSearch));
        if (z2 && (emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(charSequence)) != null) {
            ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(4);
            if (!FileLoader.getPathToAttach(emojiAnimatedSticker, true).exists()) {
                FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForDocument(emojiAnimatedSticker), stickerSets.get(0), (String) null, 1, 1);
            }
        }
        if (this.visible && ((arrayList = this.keywordResults) == null || arrayList.isEmpty())) {
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
            notifyDataSetChanged();
        }
        if (!z2) {
            searchEmojiByKeyword();
            return;
        }
        clearSearch();
        this.delegate.needChangePanelVisibility(false);
    }

    public void clearSearch() {
        this.lastSearch = null;
        this.keywordResults = null;
        notifyDataSetChanged();
    }

    public String getQuery() {
        return this.lastSearch;
    }

    public boolean isShowingKeywords() {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        return arrayList != null && !arrayList.isEmpty();
    }

    public int getItemCount() {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty()) {
            return 0;
        }
        return this.keywordResults.size();
    }

    public Object getItem(int i) {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty() || i < 0 || i >= this.keywordResults.size()) {
            return null;
        }
        return this.keywordResults.get(i).emoji;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RecyclerListView.Holder(new EmojiReplacementCell(this.mContext, this.resourcesProvider));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int i2 = 1;
        if (i == 0) {
            i2 = this.keywordResults.size() == 1 ? 2 : -1;
        } else if (i != this.keywordResults.size() - 1) {
            i2 = 0;
        }
        ((EmojiReplacementCell) viewHolder.itemView).setEmoji(this.keywordResults.get(i).emoji, i2);
    }
}
