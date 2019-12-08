package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.ContentPreviewViewer;

public class StickerMasksView extends FrameLayout implements NotificationCenterDelegate {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentType = 1;
    private int lastNotifyWidth;
    private Listener listener;
    private ArrayList<Document>[] recentStickers = new ArrayList[]{new ArrayList(), new ArrayList()};
    private int recentTabBum = -2;
    private ScrollSlidingTabStrip scrollSlidingTabStrip;
    private ArrayList<TL_messages_stickerSet>[] stickerSets = new ArrayList[]{new ArrayList(), new ArrayList()};
    private TextView stickersEmptyView;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private OnItemClickListener stickersOnItemClickListener;
    private int stickersTabOffset;

    public interface Listener {
        void onStickerSelected(Object obj, Document document);

        void onTypeChanged();
    }

    private class StickersGridAdapter extends SelectionAdapter {
        private SparseArray<Document> cache = new SparseArray();
        private Context context;
        private HashMap<TL_messages_stickerSet, Integer> packStartRow = new HashMap();
        private SparseArray<TL_messages_stickerSet> positionsToSets = new SparseArray();
        private SparseArray<TL_messages_stickerSet> rowStartPack = new SparseArray();
        private int stickersPerRow;
        private int totalItems;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public StickersGridAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            int i = this.totalItems;
            return i != 0 ? i + 1 : 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(TL_messages_stickerSet tL_messages_stickerSet) {
            return ((Integer) this.packStartRow.get(tL_messages_stickerSet)).intValue() * this.stickersPerRow;
        }

        public int getItemViewType(int i) {
            return this.cache.get(i) != null ? 0 : 1;
        }

        public int getTabForPosition(int i) {
            if (this.stickersPerRow == 0) {
                int measuredWidth = StickerMasksView.this.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            }
            TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.rowStartPack.get(i / this.stickersPerRow);
            if (tL_messages_stickerSet == null) {
                return StickerMasksView.this.recentTabBum;
            }
            return StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].indexOf(tL_messages_stickerSet) + StickerMasksView.this.stickersTabOffset;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View anonymousClass1;
            if (i == 0) {
                anonymousClass1 = new StickerEmojiCell(this.context) {
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
            } else if (i != 1) {
                anonymousClass1 = null;
            } else {
                anonymousClass1 = new EmptyCell(this.context);
            }
            return new Holder(anonymousClass1);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) this.cache.get(i), this.positionsToSets.get(i), false);
            } else if (itemViewType == 1) {
                if (i == this.totalItems) {
                    TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) this.rowStartPack.get((i - 1) / this.stickersPerRow);
                    if (tL_messages_stickerSet == null) {
                        ((EmptyCell) viewHolder.itemView).setHeight(1);
                        return;
                    }
                    i = StickerMasksView.this.stickersGridView.getMeasuredHeight() - (((int) Math.ceil((double) (((float) tL_messages_stickerSet.documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                    EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                    if (i <= 0) {
                        i = 1;
                    }
                    emptyCell.setHeight(i);
                    return;
                }
                ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
            }
        }

        public void notifyDataSetChanged() {
            int measuredWidth = StickerMasksView.this.getMeasuredWidth();
            if (measuredWidth == 0) {
                measuredWidth = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            StickerMasksView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartRow.clear();
            this.cache.clear();
            this.positionsToSets.clear();
            this.totalItems = 0;
            ArrayList arrayList = StickerMasksView.this.stickerSets[StickerMasksView.this.currentType];
            for (int i = -1; i < arrayList.size(); i++) {
                ArrayList arrayList2;
                Object obj = null;
                int i2 = this.totalItems / this.stickersPerRow;
                if (i == -1) {
                    arrayList2 = StickerMasksView.this.recentStickers[StickerMasksView.this.currentType];
                } else {
                    obj = (TL_messages_stickerSet) arrayList.get(i);
                    arrayList2 = obj.documents;
                    this.packStartRow.put(obj, Integer.valueOf(i2));
                }
                if (!arrayList2.isEmpty()) {
                    int ceil = (int) Math.ceil((double) (((float) arrayList2.size()) / ((float) this.stickersPerRow)));
                    for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                        this.cache.put(this.totalItems + i3, arrayList2.get(i3));
                        this.positionsToSets.put(this.totalItems + i3, obj);
                    }
                    this.totalItems += this.stickersPerRow * ceil;
                    for (int i4 = 0; i4 < ceil; i4++) {
                        this.rowStartPack.put(i2 + i4, obj);
                    }
                }
            }
            super.notifyDataSetChanged();
        }
    }

    public StickerMasksView(Context context) {
        super(context);
        setBackgroundColor(-14540254);
        setClickable(true);
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        this.stickersGridView = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight(), null);
            }
        };
        RecyclerListView recyclerListView = this.stickersGridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
        this.stickersLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.stickersLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int i) {
                return i == StickerMasksView.this.stickersGridAdapter.totalItems ? StickerMasksView.this.stickersGridAdapter.stickersPerRow : 1;
            }
        });
        this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        this.stickersGridView.setClipToPadding(false);
        recyclerListView = this.stickersGridView;
        StickersGridAdapter stickersGridAdapter = new StickersGridAdapter(context);
        this.stickersGridAdapter = stickersGridAdapter;
        recyclerListView.setAdapter(stickersGridAdapter);
        this.stickersGridView.setOnTouchListener(new -$$Lambda$StickerMasksView$UsFOY2iHtoAth4G0Jx5Am_K6Gjw(this));
        this.stickersOnItemClickListener = new -$$Lambda$StickerMasksView$dIIbEIRSoGx8-DynOnZBV9n5UHM(this);
        this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
        this.stickersGridView.setGlowColor(-657673);
        addView(this.stickersGridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.stickersEmptyView = new TextView(context);
        this.stickersEmptyView.setTextSize(1, 18.0f);
        this.stickersEmptyView.setTextColor(-7829368);
        addView(this.stickersEmptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 48.0f, 0.0f, 0.0f));
        this.stickersGridView.setEmptyView(this.stickersEmptyView);
        this.scrollSlidingTabStrip = new ScrollSlidingTabStrip(context);
        this.scrollSlidingTabStrip.setBackgroundColor(-16777216);
        this.scrollSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1.0f));
        this.scrollSlidingTabStrip.setIndicatorColor(-10305560);
        this.scrollSlidingTabStrip.setUnderlineColor(-15066598);
        this.scrollSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(1.0f) + 1);
        addView(this.scrollSlidingTabStrip, LayoutHelper.createFrame(-1, 48, 51));
        updateStickerTabs();
        this.scrollSlidingTabStrip.setDelegate(new -$$Lambda$StickerMasksView$AhwYOOvx1aUeFtWqQPYn16OGWYU(this));
        this.stickersGridView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                StickerMasksView.this.checkScroll();
            }
        });
    }

    public /* synthetic */ boolean lambda$new$0$StickerMasksView(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, null);
    }

    public /* synthetic */ void lambda$new$1$StickerMasksView(View view, int i) {
        if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
            if (!stickerEmojiCell.isDisabled()) {
                Document sticker = stickerEmojiCell.getSticker();
                Object parentObject = stickerEmojiCell.getParentObject();
                this.listener.onStickerSelected(parentObject, sticker);
                MediaDataController.getInstance(this.currentAccount).addRecentSticker(1, parentObject, sticker, (int) (System.currentTimeMillis() / 1000), false);
                MessagesController.getInstance(this.currentAccount).saveRecentSticker(parentObject, sticker, true);
            }
        }
    }

    public /* synthetic */ void lambda$new$2$StickerMasksView(int i) {
        if (i == 0) {
            if (this.currentType == 0) {
                this.currentType = 1;
            } else {
                this.currentType = 0;
            }
            Listener listener = this.listener;
            if (listener != null) {
                listener.onTypeChanged();
            }
            this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
            this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
            updateStickerTabs();
            reloadStickersAdapter();
            checkDocuments();
            checkPanels();
        } else if (i == this.recentTabBum + 1) {
            this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
        } else {
            i = (i - 1) - this.stickersTabOffset;
            if (i >= this.stickerSets[this.currentType].size()) {
                i = this.stickerSets[this.currentType].size() - 1;
            }
            this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack((TL_messages_stickerSet) this.stickerSets[this.currentType].get(i)), 0);
            checkScroll();
        }
    }

    private void checkScroll() {
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition != -1) {
            checkStickersScroll(findFirstVisibleItemPosition);
        }
    }

    private void checkStickersScroll(int i) {
        if (this.stickersGridView != null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.scrollSlidingTabStrip;
            i = this.stickersGridAdapter.getTabForPosition(i) + 1;
            int i2 = this.recentTabBum;
            if (i2 <= 0) {
                i2 = this.stickersTabOffset;
            }
            scrollSlidingTabStrip.onPageScrolled(i, i2 + 1);
        }
    }

    public int getCurrentType() {
        return this.currentType;
    }

    private void updateStickerTabs() {
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.scrollSlidingTabStrip;
        if (scrollSlidingTabStrip != null) {
            int i;
            this.recentTabBum = -2;
            this.stickersTabOffset = 0;
            int currentPosition = scrollSlidingTabStrip.getCurrentPosition();
            this.scrollSlidingTabStrip.removeTabs();
            String str = "chat_emojiPanelIcon";
            Drawable drawable;
            if (this.currentType == 0) {
                drawable = getContext().getResources().getDrawable(NUM);
                Theme.setDrawableColorByKey(drawable, str);
                this.scrollSlidingTabStrip.addIconTab(0, drawable);
                this.stickersEmptyView.setText(LocaleController.getString("NoStickers", NUM));
            } else {
                drawable = getContext().getResources().getDrawable(NUM);
                Theme.setDrawableColorByKey(drawable, str);
                this.scrollSlidingTabStrip.addIconTab(1, drawable);
                this.stickersEmptyView.setText(LocaleController.getString("NoMasks", NUM));
            }
            if (!this.recentStickers[this.currentType].isEmpty()) {
                i = this.stickersTabOffset;
                this.recentTabBum = i;
                this.stickersTabOffset = i + 1;
                this.scrollSlidingTabStrip.addIconTab(2, Theme.createEmojiIconSelectorDrawable(getContext(), NUM, Theme.getColor("chat_emojiPanelMasksIcon"), Theme.getColor("chat_emojiPanelMasksIconSelected")));
            }
            this.stickerSets[this.currentType].clear();
            ArrayList stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (int i2 = 0; i2 < stickerSets.size(); i2++) {
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i2);
                if (!tL_messages_stickerSet.set.archived) {
                    ArrayList arrayList = tL_messages_stickerSet.documents;
                    if (!(arrayList == null || arrayList.isEmpty())) {
                        this.stickerSets[this.currentType].add(tL_messages_stickerSet);
                    }
                }
            }
            for (i = 0; i < this.stickerSets[this.currentType].size(); i++) {
                TL_messages_stickerSet tL_messages_stickerSet2 = (TL_messages_stickerSet) this.stickerSets[this.currentType].get(i);
                Document document = (Document) tL_messages_stickerSet2.documents.get(0);
                this.scrollSlidingTabStrip.addStickerTab(document, document, tL_messages_stickerSet2);
            }
            this.scrollSlidingTabStrip.updateTabStyles();
            if (currentPosition != 0) {
                this.scrollSlidingTabStrip.onPageScrolled(currentPosition, currentPosition);
            }
            checkPanels();
        }
    }

    private void checkPanels() {
        if (this.scrollSlidingTabStrip != null) {
            int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition != -1) {
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.scrollSlidingTabStrip;
                findFirstVisibleItemPosition = this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition) + 1;
                int i = this.recentTabBum;
                if (i <= 0) {
                    i = this.stickersTabOffset;
                }
                scrollSlidingTabStrip.onPageScrolled(findFirstVisibleItemPosition, i + 1);
            }
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(this.currentType, null, document, (int) (System.currentTimeMillis() / 1000), false);
            boolean isEmpty = this.recentStickers[this.currentType].isEmpty();
            this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
            StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
            if (stickersGridAdapter != null) {
                stickersGridAdapter.notifyDataSetChanged();
            }
            if (isEmpty) {
                updateStickerTabs();
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            reloadStickersAdapter();
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    private void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
        AndroidUtilities.runOnUIThread(new -$$Lambda$StickerMasksView$77XyfxOn3O_qUeajE-5-7BnPFnM(this));
    }

    public /* synthetic */ void lambda$onAttachedToWindow$3$StickerMasksView() {
        updateStickerTabs();
        reloadStickersAdapter();
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 8) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
            updateStickerTabs();
            reloadStickersAdapter();
            checkDocuments();
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, false, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(1, false, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(2, false, true, false);
        }
    }

    public void onDestroy() {
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoad);
        }
    }

    private void checkDocuments() {
        int size = this.recentStickers[this.currentType].size();
        this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        if (size != this.recentStickers[this.currentType].size()) {
            updateStickerTabs();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            if (((Integer) objArr[0]).intValue() == this.currentType) {
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoad && !((Boolean) objArr[0]).booleanValue() && ((Integer) objArr[1]).intValue() == this.currentType) {
            checkDocuments();
        }
    }
}
