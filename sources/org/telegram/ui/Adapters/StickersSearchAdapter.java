package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_foundStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.StickersSearchAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class StickersSearchAdapter extends RecyclerListView.SelectionAdapter {
    boolean cleared;
    private final Context context;
    private final Delegate delegate;
    private int emojiSearchId;
    private ImageView emptyImageView;
    private TextView emptyTextView;
    private final LongSparseArray<TLRPC$StickerSetCovered> installingStickerSets;
    private final TLRPC$StickerSetCovered[] primaryInstallingStickerSets;
    private final LongSparseArray<TLRPC$StickerSetCovered> removingStickerSets;
    private int reqId;
    private int reqId2;
    private final Theme.ResourcesProvider resourcesProvider;
    private String searchQuery;
    private int totalItems;
    private final int currentAccount = UserConfig.selectedAccount;
    private SparseArray<Object> rowStartPack = new SparseArray<>();
    private SparseArray<Object> cache = new SparseArray<>();
    private SparseArray<Object> cacheParent = new SparseArray<>();
    private SparseIntArray positionToRow = new SparseIntArray();
    private SparseArray<String> positionToEmoji = new SparseArray<>();
    private ArrayList<TLRPC$StickerSetCovered> serverPacks = new ArrayList<>();
    private ArrayList<TLRPC$TL_messages_stickerSet> localPacks = new ArrayList<>();
    private HashMap<TLRPC$TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
    private HashMap<TLRPC$TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
    private HashMap<ArrayList<TLRPC$Document>, String> emojiStickers = new HashMap<>();
    private ArrayList<ArrayList<TLRPC$Document>> emojiArrays = new ArrayList<>();
    private SparseArray<TLRPC$StickerSetCovered> positionsToSets = new SparseArray<>();
    private Runnable searchRunnable = new AnonymousClass1();

    /* loaded from: classes3.dex */
    public interface Delegate {
        String[] getLastSearchKeyboardLanguage();

        int getStickersPerRow();

        void onSearchStart();

        void onSearchStop();

        void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z);

        void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered);

        void setAdapterVisible(boolean z);

        void setLastSearchKeyboardLanguage(String[] strArr);
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    static /* synthetic */ int access$804(StickersSearchAdapter stickersSearchAdapter) {
        int i = stickersSearchAdapter.emojiSearchId + 1;
        stickersSearchAdapter.emojiSearchId = i;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Adapters.StickersSearchAdapter$1  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        private void clear() {
            StickersSearchAdapter stickersSearchAdapter = StickersSearchAdapter.this;
            if (stickersSearchAdapter.cleared) {
                return;
            }
            stickersSearchAdapter.cleared = true;
            stickersSearchAdapter.emojiStickers.clear();
            StickersSearchAdapter.this.emojiArrays.clear();
            StickersSearchAdapter.this.localPacks.clear();
            StickersSearchAdapter.this.serverPacks.clear();
            StickersSearchAdapter.this.localPacksByShortName.clear();
            StickersSearchAdapter.this.localPacksByName.clear();
        }

        /* JADX WARN: Code restructure failed: missing block: B:16:0x0073, code lost:
            if (r5.charAt(r9) <= 57343) goto L17;
         */
        /* JADX WARN: Code restructure failed: missing block: B:22:0x008d, code lost:
            if (r5.charAt(r9) != 9794) goto L27;
         */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 789
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.StickersSearchAdapter.AnonymousClass1.run():void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0(int i, HashMap hashMap, ArrayList arrayList, String str) {
            if (i != StickersSearchAdapter.this.emojiSearchId) {
                return;
            }
            int size = arrayList.size();
            boolean z = false;
            for (int i2 = 0; i2 < size; i2++) {
                String str2 = ((MediaDataController.KeywordResult) arrayList.get(i2)).emoji;
                ArrayList arrayList2 = hashMap != null ? (ArrayList) hashMap.get(str2) : null;
                if (arrayList2 != null && !arrayList2.isEmpty()) {
                    clear();
                    if (!StickersSearchAdapter.this.emojiStickers.containsKey(arrayList2)) {
                        StickersSearchAdapter.this.emojiStickers.put(arrayList2, str2);
                        StickersSearchAdapter.this.emojiArrays.add(arrayList2);
                        z = true;
                    }
                }
            }
            if (!z) {
                return;
            }
            StickersSearchAdapter.this.notifyDataSetChanged();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$2(final TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject instanceof TLRPC$TL_messages_foundStickerSets) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        StickersSearchAdapter.AnonymousClass1.this.lambda$run$1(tLRPC$TL_messages_searchStickerSets, tLObject);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$1(TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, TLObject tLObject) {
            if (tLRPC$TL_messages_searchStickerSets.q.equals(StickersSearchAdapter.this.searchQuery)) {
                clear();
                StickersSearchAdapter.this.delegate.onSearchStop();
                StickersSearchAdapter.this.reqId = 0;
                StickersSearchAdapter.this.delegate.setAdapterVisible(true);
                StickersSearchAdapter.this.serverPacks.addAll(((TLRPC$TL_messages_foundStickerSets) tLObject).sets);
                StickersSearchAdapter.this.notifyDataSetChanged();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$4(final TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, final ArrayList arrayList, final LongSparseArray longSparseArray, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StickersSearchAdapter.AnonymousClass1.this.lambda$run$3(tLRPC$TL_messages_getStickers, tLObject, arrayList, longSparseArray);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$3(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
            if (tLRPC$TL_messages_getStickers.emoticon.equals(StickersSearchAdapter.this.searchQuery)) {
                StickersSearchAdapter.this.reqId2 = 0;
                if (!(tLObject instanceof TLRPC$TL_messages_stickers)) {
                    return;
                }
                TLRPC$TL_messages_stickers tLRPC$TL_messages_stickers = (TLRPC$TL_messages_stickers) tLObject;
                int size = arrayList.size();
                int size2 = tLRPC$TL_messages_stickers.stickers.size();
                for (int i = 0; i < size2; i++) {
                    TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickers.stickers.get(i);
                    if (longSparseArray.indexOfKey(tLRPC$Document.id) < 0) {
                        arrayList.add(tLRPC$Document);
                    }
                }
                if (size == arrayList.size()) {
                    return;
                }
                StickersSearchAdapter.this.emojiStickers.put(arrayList, StickersSearchAdapter.this.searchQuery);
                if (size == 0) {
                    StickersSearchAdapter.this.emojiArrays.add(arrayList);
                }
                StickersSearchAdapter.this.notifyDataSetChanged();
            }
        }
    }

    public StickersSearchAdapter(Context context, Delegate delegate, TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr, LongSparseArray<TLRPC$StickerSetCovered> longSparseArray, LongSparseArray<TLRPC$StickerSetCovered> longSparseArray2, Theme.ResourcesProvider resourcesProvider) {
        this.context = context;
        this.delegate = delegate;
        this.primaryInstallingStickerSets = tLRPC$StickerSetCoveredArr;
        this.installingStickerSets = longSparseArray;
        this.removingStickerSets = longSparseArray2;
        this.resourcesProvider = resourcesProvider;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return Math.max(1, this.totalItems + 1);
    }

    public void search(String str) {
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.reqId2 != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId2, true);
            this.reqId2 = 0;
        }
        if (TextUtils.isEmpty(str)) {
            this.searchQuery = null;
            this.localPacks.clear();
            this.emojiStickers.clear();
            this.serverPacks.clear();
            this.delegate.setAdapterVisible(false);
            notifyDataSetChanged();
        } else {
            this.searchQuery = str.toLowerCase();
        }
        AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
        AndroidUtilities.runOnUIThread(this.searchRunnable, 300L);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        if (i == 0 && this.totalItems == 0) {
            return 5;
        }
        if (i == getItemCount() - 1) {
            return 4;
        }
        Object obj = this.cache.get(i);
        if (obj == null) {
            return 1;
        }
        if (obj instanceof TLRPC$Document) {
            return 0;
        }
        return obj instanceof TLRPC$StickerSetCovered ? 3 : 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
        FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
        TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
        if (stickerSet == null || this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0 || this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0) {
            return;
        }
        if (featuredStickerSetInfoCell.isInstalled()) {
            this.removingStickerSets.put(stickerSet.set.id, stickerSet);
            this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
            return;
        }
        installStickerSet(stickerSet, featuredStickerSetInfoCell);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    /* renamed from: onCreateViewHolder */
    public RecyclerView.ViewHolder moNUMonCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        LinearLayout linearLayout;
        if (i == 0) {
            StickerEmojiCell stickerEmojiCell = new StickerEmojiCell(this, this.context, false) { // from class: org.telegram.ui.Adapters.StickersSearchAdapter.2
                @Override // android.widget.FrameLayout, android.view.View
                public void onMeasure(int i2, int i3) {
                    super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                }
            };
            stickerEmojiCell.getImageView().setLayerNum(3);
            view = stickerEmojiCell;
        } else {
            if (i == 1) {
                linearLayout = new EmptyCell(this.context);
            } else if (i == 2) {
                view = new StickerSetNameCell(this.context, false, true, this.resourcesProvider);
            } else if (i == 3) {
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = new FeaturedStickerSetInfoCell(this.context, 17, true, true, this.resourcesProvider);
                featuredStickerSetInfoCell.setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Adapters.StickersSearchAdapter$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        StickersSearchAdapter.this.lambda$onCreateViewHolder$0(view2);
                    }
                });
                linearLayout = featuredStickerSetInfoCell;
            } else if (i == 4) {
                linearLayout = new View(this.context);
            } else if (i != 5) {
                linearLayout = null;
            } else {
                LinearLayout linearLayout2 = new LinearLayout(this.context);
                linearLayout2.setOrientation(1);
                linearLayout2.setGravity(17);
                ImageView imageView = new ImageView(this.context);
                this.emptyImageView = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.emptyImageView.setImageResource(R.drawable.stickers_empty);
                this.emptyImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                linearLayout2.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
                linearLayout2.addView(new Space(this.context), LayoutHelper.createLinear(-1, 15));
                TextView textView = new TextView(this.context);
                this.emptyTextView = textView;
                textView.setText(LocaleController.getString("NoStickersFound", R.string.NoStickersFound));
                this.emptyTextView.setTextSize(1, 16.0f);
                this.emptyTextView.setTextColor(getThemedColor("chat_emojiPanelEmptyText"));
                linearLayout2.addView(this.emptyTextView, LayoutHelper.createLinear(-2, -2));
                linearLayout2.setMinimumHeight(AndroidUtilities.dp(112.0f));
                linearLayout2.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f));
                linearLayout = linearLayout2;
            }
            return new RecyclerListView.Holder(linearLayout);
        }
        linearLayout = view;
        return new RecyclerListView.Holder(linearLayout);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 0) {
            ((StickerEmojiCell) viewHolder.itemView).setSticker((TLRPC$Document) this.cache.get(i), null, this.cacheParent.get(i), this.positionToEmoji.get(i), false);
        } else if (itemViewType == 1) {
            ((EmptyCell) viewHolder.itemView).setHeight(0);
        } else if (itemViewType != 2) {
            if (itemViewType != 3) {
                return;
            }
            bindFeaturedStickerSetInfoCell((FeaturedStickerSetInfoCell) viewHolder.itemView, i, false);
        } else {
            StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
            Object obj = this.cache.get(i);
            if (!(obj instanceof TLRPC$TL_messages_stickerSet)) {
                return;
            }
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) obj;
            if (!TextUtils.isEmpty(this.searchQuery) && this.localPacksByShortName.containsKey(tLRPC$TL_messages_stickerSet)) {
                TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
                if (tLRPC$StickerSet != null) {
                    stickerSetNameCell.setText(tLRPC$StickerSet.title, 0);
                }
                stickerSetNameCell.setUrl(tLRPC$TL_messages_stickerSet.set.short_name, this.searchQuery.length());
                return;
            }
            Integer num = this.localPacksByName.get(tLRPC$TL_messages_stickerSet);
            TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set;
            if (tLRPC$StickerSet2 != null && num != null) {
                stickerSetNameCell.setText(tLRPC$StickerSet2.title, 0, num.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
            }
            stickerSetNameCell.setUrl(null, 0);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List list) {
        if (list.contains(0) && viewHolder.getItemViewType() == 3) {
            bindFeaturedStickerSetInfoCell((FeaturedStickerSetInfoCell) viewHolder.itemView, i, true);
        } else {
            super.onBindViewHolder(viewHolder, i, list);
        }
    }

    public void installStickerSet(TLRPC$InputStickerSet tLRPC$InputStickerSet) {
        for (int i = 0; i < this.serverPacks.size(); i++) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = this.serverPacks.get(i);
            if (tLRPC$StickerSetCovered.set.id == tLRPC$InputStickerSet.id) {
                installStickerSet(tLRPC$StickerSetCovered, null);
                return;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x003f, code lost:
        r1 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0040, code lost:
        r2 = r7.primaryInstallingStickerSets;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0044, code lost:
        if (r1 >= r2.length) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0048, code lost:
        if (r2[r1] != null) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x004a, code lost:
        r2[r1] = r8;
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x004e, code lost:
        r1 = r1 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0051, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0052, code lost:
        if (r1 != false) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0054, code lost:
        if (r9 == null) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0056, code lost:
        r9.setAddDrawProgress(true, true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0059, code lost:
        r7.installingStickerSets.put(r8.set.id, r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0062, code lost:
        if (r9 == null) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0064, code lost:
        r7.delegate.onStickerSetAdd(r9.getStickerSet(), r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x006e, code lost:
        r9 = r7.positionsToSets.size();
        r1 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0075, code lost:
        if (r1 >= r9) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0077, code lost:
        r2 = r7.positionsToSets.get(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x007f, code lost:
        if (r2 == null) goto L39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x008b, code lost:
        if (r2.set.id != r8.set.id) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x008d, code lost:
        notifyItemChanged(r1, 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0095, code lost:
        r1 = r1 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0098, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void installStickerSet(org.telegram.tgnet.TLRPC$StickerSetCovered r8, org.telegram.ui.Cells.FeaturedStickerSetInfoCell r9) {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
        L2:
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r2 = r7.primaryInstallingStickerSets
            int r3 = r2.length
            if (r1 >= r3) goto L3f
            r2 = r2[r1]
            if (r2 == 0) goto L3c
            int r2 = r7.currentAccount
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r3 = r7.primaryInstallingStickerSets
            r3 = r3[r1]
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.set
            long r3 = r3.id
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r2.getStickerSetById(r3)
            if (r2 == 0) goto L2b
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            boolean r2 = r2.archived
            if (r2 != 0) goto L2b
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r2 = r7.primaryInstallingStickerSets
            r3 = 0
            r2[r1] = r3
            goto L3f
        L2b:
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r2 = r7.primaryInstallingStickerSets
            r2 = r2[r1]
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            long r2 = r2.id
            org.telegram.tgnet.TLRPC$StickerSet r4 = r8.set
            long r4 = r4.id
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L3c
            return
        L3c:
            int r1 = r1 + 1
            goto L2
        L3f:
            r1 = 0
        L40:
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r2 = r7.primaryInstallingStickerSets
            int r3 = r2.length
            r4 = 1
            if (r1 >= r3) goto L51
            r3 = r2[r1]
            if (r3 != 0) goto L4e
            r2[r1] = r8
            r1 = 1
            goto L52
        L4e:
            int r1 = r1 + 1
            goto L40
        L51:
            r1 = 0
        L52:
            if (r1 != 0) goto L59
            if (r9 == 0) goto L59
            r9.setAddDrawProgress(r4, r4)
        L59:
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r7.installingStickerSets
            org.telegram.tgnet.TLRPC$StickerSet r3 = r8.set
            long r3 = r3.id
            r2.put(r3, r8)
            if (r9 == 0) goto L6e
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r8 = r7.delegate
            org.telegram.tgnet.TLRPC$StickerSetCovered r9 = r9.getStickerSet()
            r8.onStickerSetAdd(r9, r1)
            goto L98
        L6e:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r9 = r7.positionsToSets
            int r9 = r9.size()
            r1 = 0
        L75:
            if (r1 >= r9) goto L98
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r7.positionsToSets
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$StickerSetCovered r2 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r2
            if (r2 == 0) goto L95
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            long r2 = r2.id
            org.telegram.tgnet.TLRPC$StickerSet r4 = r8.set
            long r4 = r4.id
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L95
            java.lang.Integer r8 = java.lang.Integer.valueOf(r0)
            r7.notifyItemChanged(r1, r8)
            goto L98
        L95:
            int r1 = r1 + 1
            goto L75
        L98:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.StickersSearchAdapter.installStickerSet(org.telegram.tgnet.TLRPC$StickerSetCovered, org.telegram.ui.Cells.FeaturedStickerSetInfoCell):void");
    }

    private void bindFeaturedStickerSetInfoCell(FeaturedStickerSetInfoCell featuredStickerSetInfoCell, int i, boolean z) {
        boolean z2;
        boolean z3;
        boolean z4;
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        ArrayList<Long> unreadStickerSets = mediaDataController.getUnreadStickerSets();
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) this.cache.get(i);
        boolean z5 = true;
        boolean z6 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(tLRPC$StickerSetCovered.set.id));
        int i2 = 0;
        while (true) {
            TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr = this.primaryInstallingStickerSets;
            if (i2 >= tLRPC$StickerSetCoveredArr.length) {
                z2 = false;
                break;
            }
            if (tLRPC$StickerSetCoveredArr[i2] != null) {
                TLRPC$TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(this.primaryInstallingStickerSets[i2].set.id);
                if (stickerSetById != null && !stickerSetById.set.archived) {
                    this.primaryInstallingStickerSets[i2] = null;
                } else if (this.primaryInstallingStickerSets[i2].set.id == tLRPC$StickerSetCovered.set.id) {
                    z2 = true;
                    break;
                }
            }
            i2++;
        }
        int indexOfIgnoreCase = TextUtils.isEmpty(this.searchQuery) ? -1 : AndroidUtilities.indexOfIgnoreCase(tLRPC$StickerSetCovered.set.title, this.searchQuery);
        if (indexOfIgnoreCase >= 0) {
            featuredStickerSetInfoCell.setStickerSet(tLRPC$StickerSetCovered, z6, z, indexOfIgnoreCase, this.searchQuery.length(), z2);
        } else {
            featuredStickerSetInfoCell.setStickerSet(tLRPC$StickerSetCovered, z6, z, 0, 0, z2);
            if (!TextUtils.isEmpty(this.searchQuery) && AndroidUtilities.indexOfIgnoreCase(tLRPC$StickerSetCovered.set.short_name, this.searchQuery) == 0) {
                featuredStickerSetInfoCell.setUrl(tLRPC$StickerSetCovered.set.short_name, this.searchQuery.length());
            }
        }
        if (z6) {
            mediaDataController.markFeaturedStickersByIdAsRead(false, tLRPC$StickerSetCovered.set.id);
        }
        boolean z7 = this.installingStickerSets.indexOfKey(tLRPC$StickerSetCovered.set.id) >= 0;
        boolean z8 = this.removingStickerSets.indexOfKey(tLRPC$StickerSetCovered.set.id) >= 0;
        if (z7 || z8) {
            if (z7 && featuredStickerSetInfoCell.isInstalled()) {
                this.installingStickerSets.remove(tLRPC$StickerSetCovered.set.id);
                z7 = false;
            } else if (z8 && !featuredStickerSetInfoCell.isInstalled()) {
                this.removingStickerSets.remove(tLRPC$StickerSetCovered.set.id);
            }
        }
        if (z2 || !z7) {
            z3 = z;
            z4 = false;
        } else {
            z3 = z;
            z4 = true;
        }
        featuredStickerSetInfoCell.setAddDrawProgress(z4, z3);
        mediaDataController.preloadStickerSetThumb(tLRPC$StickerSetCovered);
        if (i <= 0) {
            z5 = false;
        }
        featuredStickerSetInfoCell.setNeedDivider(z5);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v21, types: [org.telegram.tgnet.TLRPC$messages_StickerSet, org.telegram.tgnet.TLRPC$TL_messages_stickerSet] */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void notifyDataSetChanged() {
        int i;
        ArrayList<TLRPC$Document> arrayList;
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered;
        this.rowStartPack.clear();
        this.positionToRow.clear();
        this.cache.clear();
        this.positionsToSets.clear();
        this.positionToEmoji.clear();
        this.totalItems = 0;
        int size = this.serverPacks.size();
        int size2 = this.localPacks.size();
        int i2 = !this.emojiArrays.isEmpty() ? 1 : 0;
        int i3 = 0;
        int i4 = 0;
        while (i3 < size + size2 + i2) {
            if (i3 < size2) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.localPacks.get(i3);
                arrayList = tLRPC$TL_messages_stickerSet.documents;
                i = size;
                tLRPC$StickerSetCovered = tLRPC$TL_messages_stickerSet;
            } else {
                int i5 = i3 - size2;
                if (i5 < i2) {
                    int size3 = this.emojiArrays.size();
                    String str = "";
                    int i6 = 0;
                    for (int i7 = 0; i7 < size3; i7++) {
                        ArrayList<TLRPC$Document> arrayList2 = this.emojiArrays.get(i7);
                        String str2 = this.emojiStickers.get(arrayList2);
                        if (str2 != null && !str.equals(str2)) {
                            this.positionToEmoji.put(this.totalItems + i6, str2);
                            str = str2;
                        }
                        int size4 = arrayList2.size();
                        int i8 = 0;
                        while (i8 < size4) {
                            int i9 = this.totalItems + i6;
                            int stickersPerRow = (i6 / this.delegate.getStickersPerRow()) + i4;
                            TLRPC$Document tLRPC$Document = arrayList2.get(i8);
                            int i10 = size;
                            this.cache.put(i9, tLRPC$Document);
                            int i11 = size3;
                            String str3 = str;
                            TLRPC$TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(tLRPC$Document));
                            if (stickerSetById != null) {
                                this.cacheParent.put(i9, stickerSetById);
                            }
                            this.positionToRow.put(i9, stickersPerRow);
                            i6++;
                            i8++;
                            size = i10;
                            size3 = i11;
                            str = str3;
                        }
                    }
                    i = size;
                    int ceil = (int) Math.ceil(i6 / this.delegate.getStickersPerRow());
                    for (int i12 = 0; i12 < ceil; i12++) {
                        this.rowStartPack.put(i4 + i12, Integer.valueOf(i6));
                    }
                    this.totalItems += this.delegate.getStickersPerRow() * ceil;
                    i4 += ceil;
                    i3++;
                    size = i;
                } else {
                    i = size;
                    TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = this.serverPacks.get(i5 - i2);
                    arrayList = tLRPC$StickerSetCovered2.covers;
                    tLRPC$StickerSetCovered = tLRPC$StickerSetCovered2;
                }
            }
            if (!arrayList.isEmpty()) {
                int ceil2 = (int) Math.ceil(arrayList.size() / this.delegate.getStickersPerRow());
                this.cache.put(this.totalItems, tLRPC$StickerSetCovered);
                if (i3 >= size2 && (tLRPC$StickerSetCovered instanceof TLRPC$StickerSetCovered)) {
                    this.positionsToSets.put(this.totalItems, tLRPC$StickerSetCovered);
                }
                this.positionToRow.put(this.totalItems, i4);
                int size5 = arrayList.size();
                int i13 = 0;
                while (i13 < size5) {
                    int i14 = i13 + 1;
                    int i15 = this.totalItems + i14;
                    int stickersPerRow2 = i4 + 1 + (i13 / this.delegate.getStickersPerRow());
                    this.cache.put(i15, arrayList.get(i13));
                    this.cacheParent.put(i15, tLRPC$StickerSetCovered);
                    this.positionToRow.put(i15, stickersPerRow2);
                    if (i3 >= size2 && (tLRPC$StickerSetCovered instanceof TLRPC$StickerSetCovered)) {
                        this.positionsToSets.put(i15, tLRPC$StickerSetCovered);
                    }
                    i13 = i14;
                }
                int i16 = ceil2 + 1;
                for (int i17 = 0; i17 < i16; i17++) {
                    this.rowStartPack.put(i4 + i17, tLRPC$StickerSetCovered);
                }
                this.totalItems += (ceil2 * this.delegate.getStickersPerRow()) + 1;
                i4 += i16;
            }
            i3++;
            size = i;
        }
        super.notifyDataSetChanged();
    }

    public int getSpanSize(int i) {
        if (i == this.totalItems || (this.cache.get(i) != null && !(this.cache.get(i) instanceof TLRPC$Document))) {
            return this.delegate.getStickersPerRow();
        }
        return 1;
    }

    public TLRPC$StickerSetCovered getSetForPosition(int i) {
        return this.positionsToSets.get(i);
    }

    public void updateColors(RecyclerListView recyclerListView) {
        int childCount = recyclerListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = recyclerListView.getChildAt(i);
            if (childAt instanceof FeaturedStickerSetInfoCell) {
                ((FeaturedStickerSetInfoCell) childAt).updateColors();
            } else if (childAt instanceof StickerSetNameCell) {
                ((StickerSetNameCell) childAt).updateColors();
            }
        }
    }

    public void getThemeDescriptions(List<ThemeDescription> list, RecyclerListView recyclerListView, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
        FeaturedStickerSetInfoCell.createThemeDescriptions(list, recyclerListView, themeDescriptionDelegate);
        StickerSetNameCell.createThemeDescriptions(list, recyclerListView, themeDescriptionDelegate);
        list.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chat_emojiPanelEmptyText"));
        list.add(new ThemeDescription(this.emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_emojiPanelEmptyText"));
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
