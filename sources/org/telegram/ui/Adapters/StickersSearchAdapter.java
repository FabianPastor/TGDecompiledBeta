package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class StickersSearchAdapter extends RecyclerListView.SelectionAdapter {
    public static final int PAYLOAD_ANIMATED = 0;
    private SparseArray<Object> cache = new SparseArray<>();
    private SparseArray<Object> cacheParent = new SparseArray<>();
    boolean cleared;
    private final Context context;
    /* access modifiers changed from: private */
    public final int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public final Delegate delegate;
    /* access modifiers changed from: private */
    public ArrayList<ArrayList<TLRPC.Document>> emojiArrays = new ArrayList<>();
    /* access modifiers changed from: private */
    public int emojiSearchId;
    /* access modifiers changed from: private */
    public HashMap<ArrayList<TLRPC.Document>, String> emojiStickers = new HashMap<>();
    private ImageView emptyImageView;
    private TextView emptyTextView;
    private final LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_messages_stickerSet> localPacks = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<TLRPC.TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
    /* access modifiers changed from: private */
    public HashMap<TLRPC.TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
    private SparseArray<String> positionToEmoji = new SparseArray<>();
    private SparseIntArray positionToRow = new SparseIntArray();
    private SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
    private final TLRPC.StickerSetCovered[] primaryInstallingStickerSets;
    private final LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets;
    /* access modifiers changed from: private */
    public int reqId;
    /* access modifiers changed from: private */
    public int reqId2;
    private final Theme.ResourcesProvider resourcesProvider;
    private SparseArray<Object> rowStartPack = new SparseArray<>();
    /* access modifiers changed from: private */
    public String searchQuery;
    private Runnable searchRunnable = new Runnable() {
        private void clear() {
            if (!StickersSearchAdapter.this.cleared) {
                StickersSearchAdapter.this.cleared = true;
                StickersSearchAdapter.this.emojiStickers.clear();
                StickersSearchAdapter.this.emojiArrays.clear();
                StickersSearchAdapter.this.localPacks.clear();
                StickersSearchAdapter.this.serverPacks.clear();
                StickersSearchAdapter.this.localPacksByShortName.clear();
                StickersSearchAdapter.this.localPacksByName.clear();
            }
        }

        public void run() {
            if (!TextUtils.isEmpty(StickersSearchAdapter.this.searchQuery)) {
                StickersSearchAdapter.this.delegate.onSearchStart();
                StickersSearchAdapter.this.cleared = false;
                int lastId = StickersSearchAdapter.access$804(StickersSearchAdapter.this);
                ArrayList<TLRPC.Document> emojiStickersArray = new ArrayList<>(0);
                LongSparseArray<TLRPC.Document> emojiStickersMap = new LongSparseArray<>(0);
                HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(StickersSearchAdapter.this.currentAccount).getAllStickers();
                if (StickersSearchAdapter.this.searchQuery.length() <= 14) {
                    CharSequence emoji = StickersSearchAdapter.this.searchQuery;
                    int length = emoji.length();
                    int a = 0;
                    while (a < length) {
                        if (a < length - 1 && ((emoji.charAt(a) == 55356 && emoji.charAt(a + 1) >= 57339 && emoji.charAt(a + 1) <= 57343) || (emoji.charAt(a) == 8205 && (emoji.charAt(a + 1) == 9792 || emoji.charAt(a + 1) == 9794)))) {
                            emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 2, emoji.length())});
                            length -= 2;
                            a--;
                        } else if (emoji.charAt(a) == 65039) {
                            emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 1, emoji.length())});
                            length--;
                            a--;
                        }
                        a++;
                    }
                    ArrayList<TLRPC.Document> newStickers = allStickers != null ? allStickers.get(emoji.toString()) : null;
                    if (newStickers != null && !newStickers.isEmpty()) {
                        clear();
                        emojiStickersArray.addAll(newStickers);
                        int size = newStickers.size();
                        for (int a2 = 0; a2 < size; a2++) {
                            TLRPC.Document document = newStickers.get(a2);
                            emojiStickersMap.put(document.id, document);
                        }
                        StickersSearchAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchAdapter.this.searchQuery);
                        StickersSearchAdapter.this.emojiArrays.add(emojiStickersArray);
                    }
                }
                if (allStickers != null && !allStickers.isEmpty() && StickersSearchAdapter.this.searchQuery.length() > 1) {
                    String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                    if (!Arrays.equals(StickersSearchAdapter.this.delegate.getLastSearchKeyboardLanguage(), newLanguage)) {
                        MediaDataController.getInstance(StickersSearchAdapter.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                    }
                    StickersSearchAdapter.this.delegate.setLastSearchKeyboardLanguage(newLanguage);
                    MediaDataController.getInstance(StickersSearchAdapter.this.currentAccount).getEmojiSuggestions(StickersSearchAdapter.this.delegate.getLastSearchKeyboardLanguage(), StickersSearchAdapter.this.searchQuery, false, new StickersSearchAdapter$1$$ExternalSyntheticLambda2(this, lastId, allStickers));
                }
                ArrayList<TLRPC.TL_messages_stickerSet> local = MediaDataController.getInstance(StickersSearchAdapter.this.currentAccount).getStickerSets(0);
                int size2 = local.size();
                for (int a3 = 0; a3 < size2; a3++) {
                    TLRPC.TL_messages_stickerSet set = local.get(a3);
                    int indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(set.set.title, StickersSearchAdapter.this.searchQuery);
                    int index = indexOfIgnoreCase;
                    if (indexOfIgnoreCase >= 0) {
                        if (index == 0 || set.set.title.charAt(index - 1) == ' ') {
                            clear();
                            StickersSearchAdapter.this.localPacks.add(set);
                            StickersSearchAdapter.this.localPacksByName.put(set, Integer.valueOf(index));
                        }
                    } else if (set.set.short_name != null) {
                        int indexOfIgnoreCase2 = AndroidUtilities.indexOfIgnoreCase(set.set.short_name, StickersSearchAdapter.this.searchQuery);
                        int index2 = indexOfIgnoreCase2;
                        if (indexOfIgnoreCase2 >= 0 && (index2 == 0 || set.set.short_name.charAt(index2 - 1) == ' ')) {
                            clear();
                            StickersSearchAdapter.this.localPacks.add(set);
                            StickersSearchAdapter.this.localPacksByShortName.put(set, true);
                        }
                    }
                }
                ArrayList<TLRPC.TL_messages_stickerSet> local2 = MediaDataController.getInstance(StickersSearchAdapter.this.currentAccount).getStickerSets(3);
                int size3 = local2.size();
                for (int a4 = 0; a4 < size3; a4++) {
                    TLRPC.TL_messages_stickerSet set2 = local2.get(a4);
                    int indexOfIgnoreCase3 = AndroidUtilities.indexOfIgnoreCase(set2.set.title, StickersSearchAdapter.this.searchQuery);
                    int index3 = indexOfIgnoreCase3;
                    if (indexOfIgnoreCase3 >= 0) {
                        if (index3 == 0 || set2.set.title.charAt(index3 - 1) == ' ') {
                            clear();
                            StickersSearchAdapter.this.localPacks.add(set2);
                            StickersSearchAdapter.this.localPacksByName.put(set2, Integer.valueOf(index3));
                        }
                    } else if (set2.set.short_name != null) {
                        int indexOfIgnoreCase4 = AndroidUtilities.indexOfIgnoreCase(set2.set.short_name, StickersSearchAdapter.this.searchQuery);
                        int index4 = indexOfIgnoreCase4;
                        if (indexOfIgnoreCase4 >= 0 && (index4 == 0 || set2.set.short_name.charAt(index4 - 1) == ' ')) {
                            clear();
                            StickersSearchAdapter.this.localPacks.add(set2);
                            StickersSearchAdapter.this.localPacksByShortName.put(set2, true);
                        }
                    }
                }
                if (!StickersSearchAdapter.this.localPacks.isEmpty() || !StickersSearchAdapter.this.emojiStickers.isEmpty()) {
                    StickersSearchAdapter.this.delegate.setAdapterVisible(true);
                }
                TLRPC.TL_messages_searchStickerSets req = new TLRPC.TL_messages_searchStickerSets();
                req.q = StickersSearchAdapter.this.searchQuery;
                StickersSearchAdapter stickersSearchAdapter = StickersSearchAdapter.this;
                int unused = stickersSearchAdapter.reqId = ConnectionsManager.getInstance(stickersSearchAdapter.currentAccount).sendRequest(req, new StickersSearchAdapter$1$$ExternalSyntheticLambda4(this, req));
                if (Emoji.isValidEmoji(StickersSearchAdapter.this.searchQuery)) {
                    TLRPC.TL_messages_getStickers req2 = new TLRPC.TL_messages_getStickers();
                    req2.emoticon = StickersSearchAdapter.this.searchQuery;
                    req2.hash = 0;
                    StickersSearchAdapter stickersSearchAdapter2 = StickersSearchAdapter.this;
                    int unused2 = stickersSearchAdapter2.reqId2 = ConnectionsManager.getInstance(stickersSearchAdapter2.currentAccount).sendRequest(req2, new StickersSearchAdapter$1$$ExternalSyntheticLambda3(this, req2, emojiStickersArray, emojiStickersMap));
                }
                StickersSearchAdapter.this.notifyDataSetChanged();
            }
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Adapters-StickersSearchAdapter$1  reason: not valid java name */
        public /* synthetic */ void m1401lambda$run$0$orgtelegramuiAdaptersStickersSearchAdapter$1(int lastId, HashMap allStickers, ArrayList param, String alias) {
            if (lastId == StickersSearchAdapter.this.emojiSearchId) {
                boolean added = false;
                int size = param.size();
                for (int a = 0; a < size; a++) {
                    String emoji = ((MediaDataController.KeywordResult) param.get(a)).emoji;
                    ArrayList<TLRPC.Document> newStickers = allStickers != null ? (ArrayList) allStickers.get(emoji) : null;
                    if (newStickers != null && !newStickers.isEmpty()) {
                        clear();
                        if (!StickersSearchAdapter.this.emojiStickers.containsKey(newStickers)) {
                            StickersSearchAdapter.this.emojiStickers.put(newStickers, emoji);
                            StickersSearchAdapter.this.emojiArrays.add(newStickers);
                            added = true;
                        }
                    }
                }
                if (added) {
                    StickersSearchAdapter.this.notifyDataSetChanged();
                }
            }
        }

        /* renamed from: lambda$run$2$org-telegram-ui-Adapters-StickersSearchAdapter$1  reason: not valid java name */
        public /* synthetic */ void m1403lambda$run$2$orgtelegramuiAdaptersStickersSearchAdapter$1(TLRPC.TL_messages_searchStickerSets req, TLObject response, TLRPC.TL_error error) {
            if (response instanceof TLRPC.TL_messages_foundStickerSets) {
                AndroidUtilities.runOnUIThread(new StickersSearchAdapter$1$$ExternalSyntheticLambda1(this, req, response));
            }
        }

        /* renamed from: lambda$run$1$org-telegram-ui-Adapters-StickersSearchAdapter$1  reason: not valid java name */
        public /* synthetic */ void m1402lambda$run$1$orgtelegramuiAdaptersStickersSearchAdapter$1(TLRPC.TL_messages_searchStickerSets req, TLObject response) {
            if (req.q.equals(StickersSearchAdapter.this.searchQuery)) {
                clear();
                StickersSearchAdapter.this.delegate.onSearchStop();
                int unused = StickersSearchAdapter.this.reqId = 0;
                StickersSearchAdapter.this.delegate.setAdapterVisible(true);
                StickersSearchAdapter.this.serverPacks.addAll(((TLRPC.TL_messages_foundStickerSets) response).sets);
                StickersSearchAdapter.this.notifyDataSetChanged();
            }
        }

        /* renamed from: lambda$run$4$org-telegram-ui-Adapters-StickersSearchAdapter$1  reason: not valid java name */
        public /* synthetic */ void m1405lambda$run$4$orgtelegramuiAdaptersStickersSearchAdapter$1(TLRPC.TL_messages_getStickers req2, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new StickersSearchAdapter$1$$ExternalSyntheticLambda0(this, req2, response, emojiStickersArray, emojiStickersMap));
        }

        /* renamed from: lambda$run$3$org-telegram-ui-Adapters-StickersSearchAdapter$1  reason: not valid java name */
        public /* synthetic */ void m1404lambda$run$3$orgtelegramuiAdaptersStickersSearchAdapter$1(TLRPC.TL_messages_getStickers req2, TLObject response, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap) {
            if (req2.emoticon.equals(StickersSearchAdapter.this.searchQuery)) {
                int unused = StickersSearchAdapter.this.reqId2 = 0;
                if (response instanceof TLRPC.TL_messages_stickers) {
                    TLRPC.TL_messages_stickers res = (TLRPC.TL_messages_stickers) response;
                    int oldCount = emojiStickersArray.size();
                    int size = res.stickers.size();
                    for (int a = 0; a < size; a++) {
                        TLRPC.Document document = res.stickers.get(a);
                        if (emojiStickersMap.indexOfKey(document.id) < 0) {
                            emojiStickersArray.add(document);
                        }
                    }
                    if (oldCount != emojiStickersArray.size()) {
                        StickersSearchAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchAdapter.this.searchQuery);
                        if (oldCount == 0) {
                            StickersSearchAdapter.this.emojiArrays.add(emojiStickersArray);
                        }
                        StickersSearchAdapter.this.notifyDataSetChanged();
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.StickerSetCovered> serverPacks = new ArrayList<>();
    private int totalItems;

    public interface Delegate {
        String[] getLastSearchKeyboardLanguage();

        int getStickersPerRow();

        void onSearchStart();

        void onSearchStop();

        void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered, boolean z);

        void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered);

        void setAdapterVisible(boolean z);

        void setLastSearchKeyboardLanguage(String[] strArr);
    }

    static /* synthetic */ int access$804(StickersSearchAdapter x0) {
        int i = x0.emojiSearchId + 1;
        x0.emojiSearchId = i;
        return i;
    }

    public StickersSearchAdapter(Context context2, Delegate delegate2, TLRPC.StickerSetCovered[] primaryInstallingStickerSets2, LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets2, LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets2, Theme.ResourcesProvider resourcesProvider2) {
        this.context = context2;
        this.delegate = delegate2;
        this.primaryInstallingStickerSets = primaryInstallingStickerSets2;
        this.installingStickerSets = installingStickerSets2;
        this.removingStickerSets = removingStickerSets2;
        this.resourcesProvider = resourcesProvider2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return false;
    }

    public int getItemCount() {
        return Math.max(1, this.totalItems + 1);
    }

    public Object getItem(int i) {
        return this.cache.get(i);
    }

    public void search(String text) {
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.reqId2 != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId2, true);
            this.reqId2 = 0;
        }
        if (TextUtils.isEmpty(text)) {
            this.searchQuery = null;
            this.localPacks.clear();
            this.emojiStickers.clear();
            this.serverPacks.clear();
            this.delegate.setAdapterVisible(false);
            notifyDataSetChanged();
        } else {
            this.searchQuery = text.toLowerCase();
        }
        AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
        AndroidUtilities.runOnUIThread(this.searchRunnable, 300);
    }

    public int getItemViewType(int position) {
        if (position == 0 && this.totalItems == 0) {
            return 5;
        }
        if (position == getItemCount() - 1) {
            return 4;
        }
        Object object = this.cache.get(position);
        if (object == null) {
            return 1;
        }
        if (object instanceof TLRPC.Document) {
            return 0;
        }
        if (object instanceof TLRPC.StickerSetCovered) {
            return 3;
        }
        return 2;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                AnonymousClass2 r1 = new StickerEmojiCell(this.context, false) {
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
                view = r1;
                r1.getImageView().setLayerNum(3);
                break;
            case 1:
                view = new EmptyCell(this.context);
                break;
            case 2:
                view = new StickerSetNameCell(this.context, false, true, this.resourcesProvider);
                break;
            case 3:
                view = new FeaturedStickerSetInfoCell(this.context, 17, true, true, this.resourcesProvider);
                ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new StickersSearchAdapter$$ExternalSyntheticLambda0(this));
                break;
            case 4:
                view = new View(this.context);
                break;
            case 5:
                LinearLayout layout = new LinearLayout(this.context);
                layout.setOrientation(1);
                layout.setGravity(17);
                ImageView imageView = new ImageView(this.context);
                this.emptyImageView = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.emptyImageView.setImageResource(NUM);
                this.emptyImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                layout.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
                layout.addView(new Space(this.context), LayoutHelper.createLinear(-1, 15));
                TextView textView = new TextView(this.context);
                this.emptyTextView = textView;
                textView.setText(LocaleController.getString("NoStickersFound", NUM));
                this.emptyTextView.setTextSize(1, 16.0f);
                this.emptyTextView.setTextColor(getThemedColor("chat_emojiPanelEmptyText"));
                layout.addView(this.emptyTextView, LayoutHelper.createLinear(-2, -2));
                view = layout;
                view.setMinimumHeight(AndroidUtilities.dp(112.0f));
                view.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f));
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Adapters-StickersSearchAdapter  reason: not valid java name */
    public /* synthetic */ void m1400x31f1c9cd(View v) {
        FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) v.getParent();
        TLRPC.StickerSetCovered pack = cell.getStickerSet();
        if (pack != null && this.installingStickerSets.indexOfKey(pack.set.id) < 0 && this.removingStickerSets.indexOfKey(pack.set.id) < 0) {
            if (cell.isInstalled()) {
                this.removingStickerSets.put(pack.set.id, pack);
                this.delegate.onStickerSetRemove(cell.getStickerSet());
                return;
            }
            installStickerSet(pack, cell);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((StickerEmojiCell) holder.itemView).setSticker((TLRPC.Document) this.cache.get(position), (SendMessagesHelper.ImportingSticker) null, this.cacheParent.get(position), this.positionToEmoji.get(position), false);
                return;
            case 1:
                ((EmptyCell) holder.itemView).setHeight(0);
                return;
            case 2:
                StickerSetNameCell cell = (StickerSetNameCell) holder.itemView;
                Object object = this.cache.get(position);
                if (object instanceof TLRPC.TL_messages_stickerSet) {
                    TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) object;
                    if (TextUtils.isEmpty(this.searchQuery) || !this.localPacksByShortName.containsKey(set)) {
                        Integer start = this.localPacksByName.get(set);
                        if (!(set.set == null || start == null)) {
                            cell.setText(set.set.title, 0, start.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                        }
                        cell.setUrl((CharSequence) null, 0);
                        return;
                    }
                    if (set.set != null) {
                        cell.setText(set.set.title, 0);
                    }
                    cell.setUrl(set.set.short_name, this.searchQuery.length());
                    return;
                }
                return;
            case 3:
                bindFeaturedStickerSetInfoCell((FeaturedStickerSetInfoCell) holder.itemView, position, false);
                return;
            default:
                return;
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (!payloads.contains(0) || holder.getItemViewType() != 3) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            bindFeaturedStickerSetInfoCell((FeaturedStickerSetInfoCell) holder.itemView, position, true);
        }
    }

    public void installStickerSet(TLRPC.InputStickerSet inputSet) {
        for (int i = 0; i < this.serverPacks.size(); i++) {
            TLRPC.StickerSetCovered setCovered = this.serverPacks.get(i);
            if (setCovered.set.id == inputSet.id) {
                installStickerSet(setCovered, (FeaturedStickerSetInfoCell) null);
                return;
            }
        }
    }

    public void installStickerSet(TLRPC.StickerSetCovered pack, FeaturedStickerSetInfoCell cell) {
        int i = 0;
        while (true) {
            TLRPC.StickerSetCovered[] stickerSetCoveredArr = this.primaryInstallingStickerSets;
            if (i >= stickerSetCoveredArr.length) {
                break;
            }
            if (stickerSetCoveredArr[i] != null) {
                TLRPC.TL_messages_stickerSet s = MediaDataController.getInstance(this.currentAccount).getStickerSetById(this.primaryInstallingStickerSets[i].set.id);
                if (s != null && !s.set.archived) {
                    this.primaryInstallingStickerSets[i] = null;
                    break;
                } else if (this.primaryInstallingStickerSets[i].set.id == pack.set.id) {
                    return;
                }
            }
            i++;
        }
        boolean primary = false;
        int i2 = 0;
        while (true) {
            TLRPC.StickerSetCovered[] stickerSetCoveredArr2 = this.primaryInstallingStickerSets;
            if (i2 >= stickerSetCoveredArr2.length) {
                break;
            } else if (stickerSetCoveredArr2[i2] == null) {
                stickerSetCoveredArr2[i2] = pack;
                primary = true;
                break;
            } else {
                i2++;
            }
        }
        if (!primary && cell != null) {
            cell.setAddDrawProgress(true, true);
        }
        this.installingStickerSets.put(pack.set.id, pack);
        if (cell != null) {
            this.delegate.onStickerSetAdd(cell.getStickerSet(), primary);
            return;
        }
        int i3 = 0;
        int size = this.positionsToSets.size();
        while (i3 < size) {
            TLRPC.StickerSetCovered item = this.positionsToSets.get(i3);
            if (item == null || item.set.id != pack.set.id) {
                i3++;
            } else {
                notifyItemChanged(i3, 0);
                return;
            }
        }
    }

    private void bindFeaturedStickerSetInfoCell(FeaturedStickerSetInfoCell cell, int position, boolean animated) {
        boolean forceInstalled;
        FeaturedStickerSetInfoCell featuredStickerSetInfoCell = cell;
        int i = position;
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        ArrayList<Long> unreadStickers = mediaDataController.getUnreadStickerSets();
        TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) this.cache.get(i);
        boolean z = false;
        boolean unread = unreadStickers != null && unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
        int i2 = 0;
        while (true) {
            TLRPC.StickerSetCovered[] stickerSetCoveredArr = this.primaryInstallingStickerSets;
            if (i2 >= stickerSetCoveredArr.length) {
                forceInstalled = false;
                break;
            }
            if (stickerSetCoveredArr[i2] != null) {
                TLRPC.TL_messages_stickerSet s = MediaDataController.getInstance(this.currentAccount).getStickerSetById(this.primaryInstallingStickerSets[i2].set.id);
                if (s != null && !s.set.archived) {
                    this.primaryInstallingStickerSets[i2] = null;
                } else if (this.primaryInstallingStickerSets[i2].set.id == stickerSetCovered.set.id) {
                    forceInstalled = true;
                    break;
                }
            }
            i2++;
        }
        int idx = TextUtils.isEmpty(this.searchQuery) ? -1 : AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.title, this.searchQuery);
        if (idx >= 0) {
            cell.setStickerSet(stickerSetCovered, unread, animated, idx, this.searchQuery.length(), forceInstalled);
        } else {
            cell.setStickerSet(stickerSetCovered, unread, animated, 0, 0, forceInstalled);
            if (!TextUtils.isEmpty(this.searchQuery) && AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.short_name, this.searchQuery) == 0) {
                featuredStickerSetInfoCell.setUrl(stickerSetCovered.set.short_name, this.searchQuery.length());
            }
        }
        if (unread) {
            mediaDataController.markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
        }
        boolean installing = this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
        boolean removing = this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
        if (installing || removing) {
            if (installing && cell.isInstalled()) {
                this.installingStickerSets.remove(stickerSetCovered.set.id);
                installing = false;
            } else if (removing && !cell.isInstalled()) {
                this.removingStickerSets.remove(stickerSetCovered.set.id);
            }
        }
        featuredStickerSetInfoCell.setAddDrawProgress(!forceInstalled && installing, animated);
        mediaDataController.preloadStickerSetThumb(stickerSetCovered);
        if (i > 0) {
            z = true;
        }
        featuredStickerSetInfoCell.setNeedDivider(z);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: org.telegram.tgnet.TLRPC$TL_messages_stickerSet} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyDataSetChanged() {
        /*
            r22 = this;
            r0 = r22
            android.util.SparseArray<java.lang.Object> r1 = r0.rowStartPack
            r1.clear()
            android.util.SparseIntArray r1 = r0.positionToRow
            r1.clear()
            android.util.SparseArray<java.lang.Object> r1 = r0.cache
            r1.clear()
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r0.positionsToSets
            r1.clear()
            android.util.SparseArray<java.lang.String> r1 = r0.positionToEmoji
            r1.clear()
            r1 = 0
            r0.totalItems = r1
            r1 = 0
            r2 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r3 = r0.serverPacks
            int r3 = r3.size()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r4 = r0.localPacks
            int r4 = r4.size()
            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r5 = r0.emojiArrays
            boolean r5 = r5.isEmpty()
            r5 = r5 ^ 1
        L_0x0034:
            int r6 = r3 + r4
            int r6 = r6 + r5
            if (r2 >= r6) goto L_0x01d1
            r6 = 0
            r7 = r2
            if (r7 >= r4) goto L_0x004c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r8 = r0.localPacks
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r8
            java.util.ArrayList r9 = r8.documents
            r6 = r8
            r16 = r3
            goto L_0x0132
        L_0x004c:
            int r7 = r7 - r4
            if (r7 >= r5) goto L_0x0124
            r8 = 0
            java.lang.String r9 = ""
            r10 = 0
            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r11 = r0.emojiArrays
            int r11 = r11.size()
        L_0x0059:
            if (r10 >= r11) goto L_0x00f0
            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r12 = r0.emojiArrays
            java.lang.Object r12 = r12.get(r10)
            java.util.ArrayList r12 = (java.util.ArrayList) r12
            java.util.HashMap<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>, java.lang.String> r13 = r0.emojiStickers
            java.lang.Object r13 = r13.get(r12)
            java.lang.String r13 = (java.lang.String) r13
            if (r13 == 0) goto L_0x007c
            boolean r14 = r9.equals(r13)
            if (r14 != 0) goto L_0x007c
            r9 = r13
            android.util.SparseArray<java.lang.String> r14 = r0.positionToEmoji
            int r15 = r0.totalItems
            int r15 = r15 + r8
            r14.put(r15, r9)
        L_0x007c:
            r14 = 0
            int r15 = r12.size()
        L_0x0081:
            if (r14 >= r15) goto L_0x00e0
            r16 = r3
            int r3 = r0.totalItems
            int r3 = r3 + r8
            r17 = r9
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r9 = r0.delegate
            int r9 = r9.getStickersPerRow()
            int r9 = r8 / r9
            int r9 = r9 + r1
            java.lang.Object r18 = r12.get(r14)
            r19 = r11
            r11 = r18
            org.telegram.tgnet.TLRPC$Document r11 = (org.telegram.tgnet.TLRPC.Document) r11
            r18 = r12
            android.util.SparseArray<java.lang.Object> r12 = r0.cache
            r12.put(r3, r11)
            int r12 = r0.currentAccount
            org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
            r20 = r13
            r21 = r14
            long r13 = org.telegram.messenger.MediaDataController.getStickerSetId(r11)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r12 = r12.getStickerSetById(r13)
            if (r12 == 0) goto L_0x00bd
            android.util.SparseArray<java.lang.Object> r13 = r0.cacheParent
            r13.put(r3, r12)
        L_0x00bd:
            android.util.SparseIntArray r13 = r0.positionToRow
            r13.put(r3, r9)
            if (r2 < r4) goto L_0x00d0
            boolean r13 = r6 instanceof org.telegram.tgnet.TLRPC.StickerSetCovered
            if (r13 == 0) goto L_0x00d0
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r13 = r0.positionsToSets
            r14 = r6
            org.telegram.tgnet.TLRPC$StickerSetCovered r14 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r14
            r13.put(r3, r14)
        L_0x00d0:
            int r8 = r8 + 1
            int r14 = r21 + 1
            r3 = r16
            r9 = r17
            r12 = r18
            r11 = r19
            r13 = r20
            goto L_0x0081
        L_0x00e0:
            r16 = r3
            r17 = r9
            r19 = r11
            r18 = r12
            r20 = r13
            r21 = r14
            int r10 = r10 + 1
            goto L_0x0059
        L_0x00f0:
            r16 = r3
            r19 = r11
            float r3 = (float) r8
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r10 = r0.delegate
            int r10 = r10.getStickersPerRow()
            float r10 = (float) r10
            float r3 = r3 / r10
            double r10 = (double) r3
            double r10 = java.lang.Math.ceil(r10)
            int r3 = (int) r10
            r10 = 0
        L_0x0104:
            if (r10 >= r3) goto L_0x0114
            android.util.SparseArray<java.lang.Object> r11 = r0.rowStartPack
            int r12 = r1 + r10
            java.lang.Integer r13 = java.lang.Integer.valueOf(r8)
            r11.put(r12, r13)
            int r10 = r10 + 1
            goto L_0x0104
        L_0x0114:
            int r10 = r0.totalItems
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r11 = r0.delegate
            int r11 = r11.getStickersPerRow()
            int r11 = r11 * r3
            int r10 = r10 + r11
            r0.totalItems = r10
            int r1 = r1 + r3
            goto L_0x01cb
        L_0x0124:
            r16 = r3
            int r7 = r7 - r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r3 = r0.serverPacks
            java.lang.Object r3 = r3.get(r7)
            org.telegram.tgnet.TLRPC$StickerSetCovered r3 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r3.covers
            r6 = r3
        L_0x0132:
            boolean r3 = r9.isEmpty()
            if (r3 == 0) goto L_0x013a
            goto L_0x01cb
        L_0x013a:
            int r3 = r9.size()
            float r3 = (float) r3
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r8 = r0.delegate
            int r8 = r8.getStickersPerRow()
            float r8 = (float) r8
            float r3 = r3 / r8
            double r10 = (double) r3
            double r10 = java.lang.Math.ceil(r10)
            int r3 = (int) r10
            android.util.SparseArray<java.lang.Object> r8 = r0.cache
            int r10 = r0.totalItems
            r8.put(r10, r6)
            if (r2 < r4) goto L_0x0164
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC.StickerSetCovered
            if (r8 == 0) goto L_0x0164
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r8 = r0.positionsToSets
            int r10 = r0.totalItems
            r11 = r6
            org.telegram.tgnet.TLRPC$StickerSetCovered r11 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r11
            r8.put(r10, r11)
        L_0x0164:
            android.util.SparseIntArray r8 = r0.positionToRow
            int r10 = r0.totalItems
            r8.put(r10, r1)
            r8 = 0
            int r10 = r9.size()
        L_0x0170:
            if (r8 >= r10) goto L_0x01aa
            int r11 = r8 + 1
            int r12 = r0.totalItems
            int r11 = r11 + r12
            int r12 = r1 + 1
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r13 = r0.delegate
            int r13 = r13.getStickersPerRow()
            int r13 = r8 / r13
            int r12 = r12 + r13
            java.lang.Object r13 = r9.get(r8)
            org.telegram.tgnet.TLRPC$Document r13 = (org.telegram.tgnet.TLRPC.Document) r13
            android.util.SparseArray<java.lang.Object> r14 = r0.cache
            r14.put(r11, r13)
            if (r6 == 0) goto L_0x0194
            android.util.SparseArray<java.lang.Object> r14 = r0.cacheParent
            r14.put(r11, r6)
        L_0x0194:
            android.util.SparseIntArray r14 = r0.positionToRow
            r14.put(r11, r12)
            if (r2 < r4) goto L_0x01a7
            boolean r14 = r6 instanceof org.telegram.tgnet.TLRPC.StickerSetCovered
            if (r14 == 0) goto L_0x01a7
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r14 = r0.positionsToSets
            r15 = r6
            org.telegram.tgnet.TLRPC$StickerSetCovered r15 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r15
            r14.put(r11, r15)
        L_0x01a7:
            int r8 = r8 + 1
            goto L_0x0170
        L_0x01aa:
            r8 = 0
            int r10 = r3 + 1
        L_0x01ad:
            if (r8 >= r10) goto L_0x01b9
            android.util.SparseArray<java.lang.Object> r11 = r0.rowStartPack
            int r12 = r1 + r8
            r11.put(r12, r6)
            int r8 = r8 + 1
            goto L_0x01ad
        L_0x01b9:
            int r8 = r0.totalItems
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r10 = r0.delegate
            int r10 = r10.getStickersPerRow()
            int r10 = r10 * r3
            int r10 = r10 + 1
            int r8 = r8 + r10
            r0.totalItems = r8
            int r8 = r3 + 1
            int r1 = r1 + r8
        L_0x01cb:
            int r2 = r2 + 1
            r3 = r16
            goto L_0x0034
        L_0x01d1:
            super.notifyDataSetChanged()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.StickersSearchAdapter.notifyDataSetChanged():void");
    }

    public int getSpanSize(int position) {
        if (position == this.totalItems || (this.cache.get(position) != null && !(this.cache.get(position) instanceof TLRPC.Document))) {
            return this.delegate.getStickersPerRow();
        }
        return 1;
    }

    public TLRPC.StickerSetCovered getSetForPosition(int position) {
        return this.positionsToSets.get(position);
    }

    public void updateColors(RecyclerListView listView) {
        int size = listView.getChildCount();
        for (int i = 0; i < size; i++) {
            View child = listView.getChildAt(i);
            if (child instanceof FeaturedStickerSetInfoCell) {
                ((FeaturedStickerSetInfoCell) child).updateColors();
            } else if (child instanceof StickerSetNameCell) {
                ((StickerSetNameCell) child).updateColors();
            }
        }
    }

    public void getThemeDescriptions(List<ThemeDescription> descriptions, RecyclerListView listView, ThemeDescription.ThemeDescriptionDelegate delegate2) {
        List<ThemeDescription> list = descriptions;
        FeaturedStickerSetInfoCell.createThemeDescriptions(descriptions, listView, delegate2);
        StickerSetNameCell.createThemeDescriptions(descriptions, listView, delegate2);
        list.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_emojiPanelEmptyText"));
        list.add(new ThemeDescription(this.emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_emojiPanelEmptyText"));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
