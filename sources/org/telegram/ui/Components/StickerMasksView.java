package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTabStrip;
import org.telegram.ui.ContentPreviewViewer;

public class StickerMasksView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public int currentType = 1;
    private int lastNotifyWidth;
    private Listener listener;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>()};
    /* access modifiers changed from: private */
    public int recentTabBum = -2;
    private ScrollSlidingTabStrip scrollSlidingTabStrip;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>()};
    private TextView stickersEmptyView;
    /* access modifiers changed from: private */
    public StickersGridAdapter stickersGridAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView stickersGridView;
    /* access modifiers changed from: private */
    public GridLayoutManager stickersLayoutManager;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    /* access modifiers changed from: private */
    public int stickersTabOffset;

    public interface Listener {
        void onStickerSelected(Object obj, TLRPC.Document document);

        void onTypeChanged();
    }

    public StickerMasksView(Context context) {
        super(context);
        setBackgroundColor(-14540254);
        setClickable(true);
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        this.stickersGridView = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight(), (ContentPreviewViewer.ContentPreviewViewerDelegate) null);
            }
        };
        RecyclerListView recyclerListView = this.stickersGridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
        this.stickersLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                if (i == StickerMasksView.this.stickersGridAdapter.totalItems) {
                    return StickerMasksView.this.stickersGridAdapter.stickersPerRow;
                }
                return 1;
            }
        });
        this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        this.stickersGridView.setClipToPadding(false);
        RecyclerListView recyclerListView2 = this.stickersGridView;
        StickersGridAdapter stickersGridAdapter2 = new StickersGridAdapter(context);
        this.stickersGridAdapter = stickersGridAdapter2;
        recyclerListView2.setAdapter(stickersGridAdapter2);
        this.stickersGridView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return StickerMasksView.this.lambda$new$0$StickerMasksView(view, motionEvent);
            }
        });
        this.stickersOnItemClickListener = new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                StickerMasksView.this.lambda$new$1$StickerMasksView(view, i);
            }
        };
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
        this.scrollSlidingTabStrip.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() {
            public final void onPageSelected(int i) {
                StickerMasksView.this.lambda$new$2$StickerMasksView(i);
            }
        });
        this.stickersGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                StickerMasksView.this.checkScroll();
            }
        });
    }

    public /* synthetic */ boolean lambda$new$0$StickerMasksView(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, (ContentPreviewViewer.ContentPreviewViewerDelegate) null);
    }

    public /* synthetic */ void lambda$new$1$StickerMasksView(View view, int i) {
        if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
            if (!stickerEmojiCell.isDisabled()) {
                TLRPC.Document sticker = stickerEmojiCell.getSticker();
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
            Listener listener2 = this.listener;
            if (listener2 != null) {
                listener2.onTypeChanged();
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
            int i2 = (i - 1) - this.stickersTabOffset;
            if (i2 >= this.stickerSets[this.currentType].size()) {
                i2 = this.stickerSets[this.currentType].size() - 1;
            }
            this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack(this.stickerSets[this.currentType].get(i2)), 0);
            checkScroll();
        }
    }

    /* access modifiers changed from: private */
    public void checkScroll() {
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition != -1) {
            checkStickersScroll(findFirstVisibleItemPosition);
        }
    }

    private void checkStickersScroll(int i) {
        if (this.stickersGridView != null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.scrollSlidingTabStrip;
            int tabForPosition = this.stickersGridAdapter.getTabForPosition(i) + 1;
            int i2 = this.recentTabBum;
            if (i2 <= 0) {
                i2 = this.stickersTabOffset;
            }
            scrollSlidingTabStrip2.onPageScrolled(tabForPosition, i2 + 1);
        }
    }

    public int getCurrentType() {
        return this.currentType;
    }

    private void updateStickerTabs() {
        ArrayList<TLRPC.Document> arrayList;
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.scrollSlidingTabStrip;
        if (scrollSlidingTabStrip2 != null) {
            this.recentTabBum = -2;
            this.stickersTabOffset = 0;
            int currentPosition = scrollSlidingTabStrip2.getCurrentPosition();
            this.scrollSlidingTabStrip.removeTabs();
            if (this.currentType == 0) {
                Drawable drawable = getContext().getResources().getDrawable(NUM);
                Theme.setDrawableColorByKey(drawable, "chat_emojiPanelIcon");
                this.scrollSlidingTabStrip.addIconTab(0, drawable);
                this.stickersEmptyView.setText(LocaleController.getString("NoStickers", NUM));
            } else {
                Drawable drawable2 = getContext().getResources().getDrawable(NUM);
                Theme.setDrawableColorByKey(drawable2, "chat_emojiPanelIcon");
                this.scrollSlidingTabStrip.addIconTab(1, drawable2);
                this.stickersEmptyView.setText(LocaleController.getString("NoMasks", NUM));
            }
            if (!this.recentStickers[this.currentType].isEmpty()) {
                int i = this.stickersTabOffset;
                this.recentTabBum = i;
                this.stickersTabOffset = i + 1;
                this.scrollSlidingTabStrip.addIconTab(2, Theme.createEmojiIconSelectorDrawable(getContext(), NUM, Theme.getColor("chat_emojiPanelMasksIcon"), Theme.getColor("chat_emojiPanelMasksIconSelected")));
            }
            this.stickerSets[this.currentType].clear();
            ArrayList<TLRPC.TL_messages_stickerSet> stickerSets2 = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (int i2 = 0; i2 < stickerSets2.size(); i2++) {
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSets2.get(i2);
                if (!tL_messages_stickerSet.set.archived && (arrayList = tL_messages_stickerSet.documents) != null && !arrayList.isEmpty()) {
                    this.stickerSets[this.currentType].add(tL_messages_stickerSet);
                }
            }
            for (int i3 = 0; i3 < this.stickerSets[this.currentType].size(); i3++) {
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = this.stickerSets[this.currentType].get(i3);
                TLRPC.Document document = tL_messages_stickerSet2.documents.get(0);
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
        int findFirstVisibleItemPosition;
        if (this.scrollSlidingTabStrip != null && (findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition()) != -1) {
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.scrollSlidingTabStrip;
            int tabForPosition = this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition) + 1;
            int i = this.recentTabBum;
            if (i <= 0) {
                i = this.stickersTabOffset;
            }
            scrollSlidingTabStrip2.onPageScrolled(tabForPosition, i + 1);
        }
    }

    public void addRecentSticker(TLRPC.Document document) {
        if (document != null) {
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(this.currentType, (Object) null, document, (int) (System.currentTimeMillis() / 1000), false);
            boolean isEmpty = this.recentStickers[this.currentType].isEmpty();
            this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
            StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
            if (stickersGridAdapter2 != null) {
                stickersGridAdapter2.notifyDataSetChanged();
            }
            if (isEmpty) {
                updateStickerTabs();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            reloadStickersAdapter();
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    private void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                StickerMasksView.this.lambda$onAttachedToWindow$3$StickerMasksView();
            }
        });
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
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        if (size != this.recentStickers[this.currentType].size()) {
            updateStickerTabs();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            if (objArr[0].intValue() == this.currentType) {
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoad && !objArr[0].booleanValue() && objArr[1].intValue() == this.currentType) {
            checkDocuments();
        }
    }

    private class StickersGridAdapter extends RecyclerListView.SelectionAdapter {
        private SparseArray<TLRPC.Document> cache = new SparseArray<>();
        private Context context;
        private HashMap<TLRPC.TL_messages_stickerSet, Integer> packStartRow = new HashMap<>();
        private SparseArray<TLRPC.TL_messages_stickerSet> positionsToSets = new SparseArray<>();
        private SparseArray<TLRPC.TL_messages_stickerSet> rowStartPack = new SparseArray<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int totalItems;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public StickersGridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 0) {
                return i + 1;
            }
            return 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            return this.packStartRow.get(tL_messages_stickerSet).intValue() * this.stickersPerRow;
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
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.rowStartPack.get(i / this.stickersPerRow);
            if (tL_messages_stickerSet == null) {
                return StickerMasksView.this.recentTabBum;
            }
            return StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].indexOf(tL_messages_stickerSet) + StickerMasksView.this.stickersTabOffset;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new StickerEmojiCell(this.context) {
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
            } else if (i != 1) {
                view = null;
            } else {
                view = new EmptyCell(this.context);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ((StickerEmojiCell) viewHolder.itemView).setSticker(this.cache.get(i), this.positionsToSets.get(i), false);
            } else if (itemViewType == 1) {
                if (i == this.totalItems) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.rowStartPack.get((i - 1) / this.stickersPerRow);
                    if (tL_messages_stickerSet == null) {
                        ((EmptyCell) viewHolder.itemView).setHeight(1);
                        return;
                    }
                    int measuredHeight = StickerMasksView.this.stickersGridView.getMeasuredHeight() - (((int) Math.ceil((double) (((float) tL_messages_stickerSet.documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                    EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                    if (measuredHeight <= 0) {
                        measuredHeight = 1;
                    }
                    emptyCell.setHeight(measuredHeight);
                    return;
                }
                ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
            }
        }

        public void notifyDataSetChanged() {
            ArrayList<TLRPC.Document> arrayList;
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
            ArrayList arrayList2 = StickerMasksView.this.stickerSets[StickerMasksView.this.currentType];
            for (int i = -1; i < arrayList2.size(); i++) {
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = null;
                int i2 = this.totalItems / this.stickersPerRow;
                if (i == -1) {
                    arrayList = StickerMasksView.this.recentStickers[StickerMasksView.this.currentType];
                } else {
                    tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) arrayList2.get(i);
                    arrayList = tL_messages_stickerSet.documents;
                    this.packStartRow.put(tL_messages_stickerSet, Integer.valueOf(i2));
                }
                if (!arrayList.isEmpty()) {
                    int ceil = (int) Math.ceil((double) (((float) arrayList.size()) / ((float) this.stickersPerRow)));
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        this.cache.put(this.totalItems + i3, arrayList.get(i3));
                        this.positionsToSets.put(this.totalItems + i3, tL_messages_stickerSet);
                    }
                    this.totalItems += this.stickersPerRow * ceil;
                    for (int i4 = 0; i4 < ceil; i4++) {
                        this.rowStartPack.put(i2 + i4, tL_messages_stickerSet);
                    }
                }
            }
            super.notifyDataSetChanged();
        }
    }
}
