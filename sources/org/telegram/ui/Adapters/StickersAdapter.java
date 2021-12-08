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
import org.telegram.tgnet.TLRPC;
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

    public StickersAdapter(Context context, StickersAdapterDelegate delegate2, Theme.ResourcesProvider resourcesProvider2) {
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.mContext = context;
        this.delegate = delegate2;
        this.resourcesProvider = resourcesProvider2;
        MediaDataController.getInstance(i).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.newEmojiSuggestionsAvailable) {
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
        String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
        if (!Arrays.equals(newLanguage, this.lastSearchKeyboardLanguage)) {
            MediaDataController.getInstance(this.currentAccount).fetchNewEmojiKeywords(newLanguage);
        }
        this.lastSearchKeyboardLanguage = newLanguage;
        String query = this.lastSearch;
        cancelEmojiSearch();
        this.searchRunnable = new StickersAdapter$$ExternalSyntheticLambda0(this, query);
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(this.searchRunnable, 1000);
        } else {
            this.searchRunnable.run();
        }
    }

    /* renamed from: lambda$searchEmojiByKeyword$1$org-telegram-ui-Adapters-StickersAdapter  reason: not valid java name */
    public /* synthetic */ void m1399xf9CLASSNAMEf8c(String query) {
        MediaDataController.getInstance(this.currentAccount).getEmojiSuggestions(this.lastSearchKeyboardLanguage, query, true, new StickersAdapter$$ExternalSyntheticLambda1(this, query));
    }

    /* renamed from: lambda$searchEmojiByKeyword$0$org-telegram-ui-Adapters-StickersAdapter  reason: not valid java name */
    public /* synthetic */ void m1398xvar_a6d(String query, ArrayList param, String alias) {
        if (query.equals(this.lastSearch)) {
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

    public void searchEmojiByKeyword(CharSequence emoji) {
        ArrayList<MediaDataController.KeywordResult> arrayList;
        TLRPC.Document animatedSticker;
        boolean searchEmoji = emoji != null && emoji.length() > 0 && emoji.length() <= 14;
        String originalEmoji = "";
        if (searchEmoji) {
            originalEmoji = emoji.toString();
            int length = emoji.length();
            int a = 0;
            while (a < length) {
                char ch = emoji.charAt(a);
                char nch = a < length + -1 ? emoji.charAt(a + 1) : 0;
                if (a < length - 1 && ch == 55356 && nch >= 57339 && nch <= 57343) {
                    emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 2, emoji.length())});
                    length -= 2;
                    a--;
                } else if (ch == 65039) {
                    emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 1, emoji.length())});
                    length--;
                    a--;
                }
                a++;
            }
        }
        this.lastSearch = emoji.toString().trim();
        boolean isValidEmoji = searchEmoji && (Emoji.isValidEmoji(originalEmoji) || Emoji.isValidEmoji(this.lastSearch));
        if (isValidEmoji && (animatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emoji)) != null) {
            ArrayList<TLRPC.TL_messages_stickerSet> sets = MediaDataController.getInstance(this.currentAccount).getStickerSets(4);
            if (!FileLoader.getPathToAttach(animatedSticker, true).exists()) {
                FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForDocument(animatedSticker), sets.get(0), (String) null, 1, 1);
            }
        }
        if (this.visible && ((arrayList = this.keywordResults) == null || arrayList.isEmpty())) {
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
            notifyDataSetChanged();
        }
        if (!isValidEmoji) {
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

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return false;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new RecyclerListView.Holder(new EmojiReplacementCell(this.mContext, this.resourcesProvider));
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int side = 0;
        if (position == 0) {
            if (this.keywordResults.size() == 1) {
                side = 2;
            } else {
                side = -1;
            }
        } else if (position == this.keywordResults.size() - 1) {
            side = 1;
        }
        ((EmojiReplacementCell) holder.itemView).setEmoji(this.keywordResults.get(position).emoji, side);
    }
}
