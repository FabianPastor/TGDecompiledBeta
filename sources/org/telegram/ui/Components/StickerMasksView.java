package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate;
import org.telegram.ui.StickerPreviewViewer;

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

    /* renamed from: org.telegram.ui.Components.StickerMasksView$3 */
    class C13093 implements OnTouchListener {
        C13093() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return StickerPreviewViewer.getInstance().onTouch(motionEvent, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight(), StickerMasksView.this.stickersOnItemClickListener, null);
        }
    }

    /* renamed from: org.telegram.ui.Components.StickerMasksView$7 */
    class C13107 implements Runnable {
        C13107() {
        }

        public void run() {
            StickerMasksView.this.updateStickerTabs();
            StickerMasksView.this.reloadStickersAdapter();
        }
    }

    public interface Listener {
        void onStickerSelected(Document document);

        void onTypeChanged();
    }

    /* renamed from: org.telegram.ui.Components.StickerMasksView$2 */
    class C20862 extends SpanSizeLookup {
        C20862() {
        }

        public int getSpanSize(int i) {
            return i == StickerMasksView.this.stickersGridAdapter.totalItems ? StickerMasksView.this.stickersGridAdapter.stickersPerRow : 1;
        }
    }

    /* renamed from: org.telegram.ui.Components.StickerMasksView$4 */
    class C20874 implements OnItemClickListener {
        C20874() {
        }

        public void onItemClick(View view, int i) {
            if ((view instanceof StickerEmojiCell) != 0) {
                StickerPreviewViewer.getInstance().reset();
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
                if (stickerEmojiCell.isDisabled() == 0) {
                    view = stickerEmojiCell.getSticker();
                    StickerMasksView.this.listener.onStickerSelected(view);
                    DataQuery.getInstance(StickerMasksView.this.currentAccount).addRecentSticker(1, view, (int) (System.currentTimeMillis() / 1000), false);
                    MessagesController.getInstance(StickerMasksView.this.currentAccount).saveRecentSticker(view, true);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.StickerMasksView$5 */
    class C20885 implements ScrollSlidingTabStripDelegate {
        C20885() {
        }

        public void onPageSelected(int i) {
            if (i == 0) {
                if (StickerMasksView.this.currentType == 0) {
                    StickerMasksView.this.currentType = 1;
                } else {
                    StickerMasksView.this.currentType = 0;
                }
                if (StickerMasksView.this.listener != 0) {
                    StickerMasksView.this.listener.onTypeChanged();
                }
                StickerMasksView.this.recentStickers[StickerMasksView.this.currentType] = DataQuery.getInstance(StickerMasksView.this.currentAccount).getRecentStickers(StickerMasksView.this.currentType);
                StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
                StickerMasksView.this.updateStickerTabs();
                StickerMasksView.this.reloadStickersAdapter();
                StickerMasksView.this.checkDocuments();
                StickerMasksView.this.checkPanels();
            } else if (i == StickerMasksView.this.recentTabBum + 1) {
                StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                i = (i - 1) - StickerMasksView.this.stickersTabOffset;
                if (i >= StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].size()) {
                    i = StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].size() - 1;
                }
                StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(StickerMasksView.this.stickersGridAdapter.getPositionForPack((TL_messages_stickerSet) StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].get(i)), 0);
                StickerMasksView.this.checkScroll();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.StickerMasksView$6 */
    class C20896 extends OnScrollListener {
        C20896() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            StickerMasksView.this.checkScroll();
        }
    }

    private class StickersGridAdapter extends SelectionAdapter {
        private SparseArray<Document> cache = new SparseArray();
        private Context context;
        private HashMap<TL_messages_stickerSet, Integer> packStartRow = new HashMap();
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
            return this.totalItems != 0 ? this.totalItems + 1 : 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(TL_messages_stickerSet tL_messages_stickerSet) {
            return ((Integer) this.packStartRow.get(tL_messages_stickerSet)).intValue() * this.stickersPerRow;
        }

        public int getItemViewType(int i) {
            return this.cache.get(i) != 0 ? 0 : 1;
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
            switch (i) {
                case 0:
                    viewGroup = new StickerEmojiCell(this.context) {
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM));
                        }
                    };
                    break;
                case 1:
                    viewGroup = new EmptyCell(this.context);
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) this.cache.get(i), false);
                    return;
                case 1:
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
                    return;
                default:
                    return;
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
        DataQuery.getInstance(this.currentAccount).checkStickers(0);
        DataQuery.getInstance(this.currentAccount).checkStickers(1);
        this.stickersGridView = new RecyclerListView(context) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                boolean onInterceptTouchEvent = StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight(), null);
                if (super.onInterceptTouchEvent(motionEvent) == null) {
                    if (!onInterceptTouchEvent) {
                        return null;
                    }
                }
                return true;
            }
        };
        RecyclerListView recyclerListView = this.stickersGridView;
        LayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
        this.stickersLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.stickersLayoutManager.setSpanSizeLookup(new C20862());
        this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        this.stickersGridView.setClipToPadding(false);
        recyclerListView = this.stickersGridView;
        Adapter stickersGridAdapter = new StickersGridAdapter(context);
        this.stickersGridAdapter = stickersGridAdapter;
        recyclerListView.setAdapter(stickersGridAdapter);
        this.stickersGridView.setOnTouchListener(new C13093());
        this.stickersOnItemClickListener = new C20874();
        this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
        this.stickersGridView.setGlowColor(-657673);
        addView(this.stickersGridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.stickersEmptyView = new TextView(context);
        this.stickersEmptyView.setTextSize(1, 18.0f);
        this.stickersEmptyView.setTextColor(-7829368);
        addView(this.stickersEmptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 48.0f, 0.0f, 0.0f));
        this.stickersGridView.setEmptyView(this.stickersEmptyView);
        this.scrollSlidingTabStrip = new ScrollSlidingTabStrip(context);
        this.scrollSlidingTabStrip.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        this.scrollSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1.0f));
        this.scrollSlidingTabStrip.setIndicatorColor(-10305560);
        this.scrollSlidingTabStrip.setUnderlineColor(-15066598);
        this.scrollSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(1.0f) + 1);
        addView(this.scrollSlidingTabStrip, LayoutHelper.createFrame(-1, 48, 51));
        updateStickerTabs();
        this.scrollSlidingTabStrip.setDelegate(new C20885());
        this.stickersGridView.setOnScrollListener(new C20896());
    }

    private void checkScroll() {
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition != -1) {
            checkStickersScroll(findFirstVisibleItemPosition);
        }
    }

    private void checkStickersScroll(int i) {
        if (this.stickersGridView != null) {
            this.scrollSlidingTabStrip.onPageScrolled(this.stickersGridAdapter.getTabForPosition(i) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        }
    }

    public int getCurrentType() {
        return this.currentType;
    }

    private void updateStickerTabs() {
        if (this.scrollSlidingTabStrip != null) {
            this.recentTabBum = -2;
            this.stickersTabOffset = 0;
            int currentPosition = this.scrollSlidingTabStrip.getCurrentPosition();
            this.scrollSlidingTabStrip.removeTabs();
            Drawable drawable;
            if (this.currentType == 0) {
                drawable = getContext().getResources().getDrawable(C0446R.drawable.ic_masks_msk1);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                this.scrollSlidingTabStrip.addIconTab(drawable);
                this.stickersEmptyView.setText(LocaleController.getString("NoStickers", C0446R.string.NoStickers));
            } else {
                drawable = getContext().getResources().getDrawable(C0446R.drawable.ic_masks_sticker1);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                this.scrollSlidingTabStrip.addIconTab(drawable);
                this.stickersEmptyView.setText(LocaleController.getString("NoMasks", C0446R.string.NoMasks));
            }
            if (!this.recentStickers[this.currentType].isEmpty()) {
                this.recentTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
                this.scrollSlidingTabStrip.addIconTab(Theme.createEmojiIconSelectorDrawable(getContext(), C0446R.drawable.ic_masks_recent1, Theme.getColor(Theme.key_chat_emojiPanelMasksIcon), Theme.getColor(Theme.key_chat_emojiPanelMasksIconSelected)));
            }
            this.stickerSets[this.currentType].clear();
            ArrayList stickerSets = DataQuery.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (int i = 0; i < stickerSets.size(); i++) {
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i);
                if (!(tL_messages_stickerSet.set.archived || tL_messages_stickerSet.documents == null)) {
                    if (!tL_messages_stickerSet.documents.isEmpty()) {
                        this.stickerSets[this.currentType].add(tL_messages_stickerSet);
                    }
                }
            }
            for (int i2 = 0; i2 < this.stickerSets[this.currentType].size(); i2++) {
                this.scrollSlidingTabStrip.addStickerTab((Document) ((TL_messages_stickerSet) this.stickerSets[this.currentType].get(i2)).documents.get(0));
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
                this.scrollSlidingTabStrip.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
            }
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            DataQuery.getInstance(this.currentAccount).addRecentSticker(this.currentType, document, (int) (System.currentTimeMillis() / 1000), false);
            document = this.recentStickers[this.currentType].isEmpty();
            this.recentStickers[this.currentType] = DataQuery.getInstance(this.currentAccount).getRecentStickers(this.currentType);
            if (this.stickersGridAdapter != null) {
                this.stickersGridAdapter.notifyDataSetChanged();
            }
            if (document != null) {
                updateStickerTabs();
            }
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            reloadStickersAdapter();
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    private void reloadStickersAdapter() {
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        if (StickerPreviewViewer.getInstance().isVisible()) {
            StickerPreviewViewer.getInstance().close();
        }
        StickerPreviewViewer.getInstance().reset();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoaded);
        AndroidUtilities.runOnUIThread(new C13107());
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 8) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoaded);
            updateStickerTabs();
            reloadStickersAdapter();
            checkDocuments();
            DataQuery.getInstance(this.currentAccount).loadRecents(0, false, true, false);
            DataQuery.getInstance(this.currentAccount).loadRecents(1, false, true, false);
            DataQuery.getInstance(this.currentAccount).loadRecents(2, false, true, false);
        }
    }

    public void onDestroy() {
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoaded);
        }
    }

    private void checkDocuments() {
        int size = this.recentStickers[this.currentType].size();
        this.recentStickers[this.currentType] = DataQuery.getInstance(this.currentAccount).getRecentStickers(this.currentType);
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        if (size != this.recentStickers[this.currentType].size()) {
            updateStickerTabs();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoaded) {
            if (((Integer) objArr[0]).intValue() == this.currentType) {
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoaded && ((Boolean) objArr[0]).booleanValue() == 0 && ((Integer) objArr[1]).intValue() == this.currentType) {
            checkDocuments();
        }
    }
}
