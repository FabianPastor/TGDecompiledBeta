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
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
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

    public interface Listener {
        void onStickerSelected(Document document);

        void onTypeChanged();
    }

    private class StickersGridAdapter extends SelectionAdapter {
        private SparseArray<Document> cache = new SparseArray();
        private Context context;
        private HashMap<TL_messages_stickerSet, Integer> packStartRow = new HashMap();
        private SparseArray<TL_messages_stickerSet> rowStartPack = new SparseArray();
        private int stickersPerRow;
        private int totalItems;

        public StickersGridAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return this.totalItems != 0 ? this.totalItems + 1 : 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(TL_messages_stickerSet stickerSet) {
            return ((Integer) this.packStartRow.get(stickerSet)).intValue() * this.stickersPerRow;
        }

        public int getItemViewType(int position) {
            if (this.cache.get(position) != null) {
                return 0;
            }
            return 1;
        }

        public int getTabForPosition(int position) {
            if (this.stickersPerRow == 0) {
                int width = StickerMasksView.this.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            }
            TL_messages_stickerSet pack = (TL_messages_stickerSet) this.rowStartPack.get(position / this.stickersPerRow);
            if (pack == null) {
                return StickerMasksView.this.recentTabBum;
            }
            return StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].indexOf(pack) + StickerMasksView.this.stickersTabOffset;
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ((StickerEmojiCell) holder.itemView).setSticker((Document) this.cache.get(position), false);
                    return;
                case 1:
                    if (position == this.totalItems) {
                        TL_messages_stickerSet pack = (TL_messages_stickerSet) this.rowStartPack.get((position - 1) / this.stickersPerRow);
                        if (pack == null) {
                            ((EmptyCell) holder.itemView).setHeight(1);
                            return;
                        }
                        int height = StickerMasksView.this.stickersGridView.getMeasuredHeight() - (((int) Math.ceil((double) (((float) pack.documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                        EmptyCell emptyCell = (EmptyCell) holder.itemView;
                        if (height <= 0) {
                            height = 1;
                        }
                        emptyCell.setHeight(height);
                        return;
                    }
                    ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                    return;
                default:
                    return;
            }
        }

        public void notifyDataSetChanged() {
            int width = StickerMasksView.this.getMeasuredWidth();
            if (width == 0) {
                width = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            StickerMasksView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartRow.clear();
            this.cache.clear();
            this.totalItems = 0;
            ArrayList<TL_messages_stickerSet> packs = StickerMasksView.this.stickerSets[StickerMasksView.this.currentType];
            for (int a = -1; a < packs.size(); a++) {
                ArrayList<Document> documents;
                TL_messages_stickerSet pack = null;
                int startRow = this.totalItems / this.stickersPerRow;
                if (a == -1) {
                    documents = StickerMasksView.this.recentStickers[StickerMasksView.this.currentType];
                } else {
                    pack = (TL_messages_stickerSet) packs.get(a);
                    documents = pack.documents;
                    this.packStartRow.put(pack, Integer.valueOf(startRow));
                }
                if (!documents.isEmpty()) {
                    int b;
                    int count = (int) Math.ceil((double) (((float) documents.size()) / ((float) this.stickersPerRow)));
                    for (b = 0; b < documents.size(); b++) {
                        this.cache.put(this.totalItems + b, documents.get(b));
                    }
                    this.totalItems += this.stickersPerRow * count;
                    for (b = 0; b < count; b++) {
                        this.rowStartPack.put(startRow + b, pack);
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
            public boolean onInterceptTouchEvent(MotionEvent event) {
                return super.onInterceptTouchEvent(event) || StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight(), null);
            }
        };
        RecyclerListView recyclerListView = this.stickersGridView;
        LayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
        this.stickersLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.stickersLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int position) {
                if (position == StickerMasksView.this.stickersGridAdapter.totalItems) {
                    return StickerMasksView.this.stickersGridAdapter.stickersPerRow;
                }
                return 1;
            }
        });
        this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        this.stickersGridView.setClipToPadding(false);
        recyclerListView = this.stickersGridView;
        Adapter stickersGridAdapter = new StickersGridAdapter(context);
        this.stickersGridAdapter = stickersGridAdapter;
        recyclerListView.setAdapter(stickersGridAdapter);
        this.stickersGridView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return StickerPreviewViewer.getInstance().onTouch(event, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight(), StickerMasksView.this.stickersOnItemClickListener, null);
            }
        });
        this.stickersOnItemClickListener = new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (view instanceof StickerEmojiCell) {
                    StickerPreviewViewer.getInstance().reset();
                    StickerEmojiCell cell = (StickerEmojiCell) view;
                    if (!cell.isDisabled()) {
                        Document document = cell.getSticker();
                        StickerMasksView.this.listener.onStickerSelected(document);
                        DataQuery.getInstance(StickerMasksView.this.currentAccount).addRecentSticker(1, document, (int) (System.currentTimeMillis() / 1000), false);
                        MessagesController.getInstance(StickerMasksView.this.currentAccount).saveRecentSticker(document, true);
                    }
                }
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
        this.scrollSlidingTabStrip.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        this.scrollSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1.0f));
        this.scrollSlidingTabStrip.setIndicatorColor(-10305560);
        this.scrollSlidingTabStrip.setUnderlineColor(-15066598);
        this.scrollSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(1.0f) + 1);
        addView(this.scrollSlidingTabStrip, LayoutHelper.createFrame(-1, 48, 51));
        updateStickerTabs();
        this.scrollSlidingTabStrip.setDelegate(new ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int page) {
                if (page == 0) {
                    if (StickerMasksView.this.currentType == 0) {
                        StickerMasksView.this.currentType = 1;
                    } else {
                        StickerMasksView.this.currentType = 0;
                    }
                    if (StickerMasksView.this.listener != null) {
                        StickerMasksView.this.listener.onTypeChanged();
                    }
                    StickerMasksView.this.recentStickers[StickerMasksView.this.currentType] = DataQuery.getInstance(StickerMasksView.this.currentAccount).getRecentStickers(StickerMasksView.this.currentType);
                    StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
                    StickerMasksView.this.updateStickerTabs();
                    StickerMasksView.this.reloadStickersAdapter();
                    StickerMasksView.this.checkDocuments();
                    StickerMasksView.this.checkPanels();
                } else if (page == StickerMasksView.this.recentTabBum + 1) {
                    StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
                } else {
                    int index = (page - 1) - StickerMasksView.this.stickersTabOffset;
                    if (index >= StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].size()) {
                        index = StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].size() - 1;
                    }
                    StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(StickerMasksView.this.stickersGridAdapter.getPositionForPack((TL_messages_stickerSet) StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].get(index)), 0);
                    StickerMasksView.this.checkScroll();
                }
            }
        });
        this.stickersGridView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                StickerMasksView.this.checkScroll();
            }
        });
    }

    private void checkScroll() {
        int firstVisibleItem = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItem != -1) {
            checkStickersScroll(firstVisibleItem);
        }
    }

    private void checkStickersScroll(int firstVisibleItem) {
        if (this.stickersGridView != null) {
            this.scrollSlidingTabStrip.onPageScrolled(this.stickersGridAdapter.getTabForPosition(firstVisibleItem) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        }
    }

    public int getCurrentType() {
        return this.currentType;
    }

    private void updateStickerTabs() {
        if (this.scrollSlidingTabStrip != null) {
            int a;
            this.recentTabBum = -2;
            this.stickersTabOffset = 0;
            int lastPosition = this.scrollSlidingTabStrip.getCurrentPosition();
            this.scrollSlidingTabStrip.removeTabs();
            Drawable drawable;
            if (this.currentType == 0) {
                drawable = getContext().getResources().getDrawable(R.drawable.ic_masks_msk1);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                this.scrollSlidingTabStrip.addIconTab(drawable);
                this.stickersEmptyView.setText(LocaleController.getString("NoStickers", R.string.NoStickers));
            } else {
                drawable = getContext().getResources().getDrawable(R.drawable.ic_masks_sticker1);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                this.scrollSlidingTabStrip.addIconTab(drawable);
                this.stickersEmptyView.setText(LocaleController.getString("NoMasks", R.string.NoMasks));
            }
            if (!this.recentStickers[this.currentType].isEmpty()) {
                this.recentTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
                this.scrollSlidingTabStrip.addIconTab(Theme.createEmojiIconSelectorDrawable(getContext(), R.drawable.ic_masks_recent1, Theme.getColor(Theme.key_chat_emojiPanelMasksIcon), Theme.getColor(Theme.key_chat_emojiPanelMasksIconSelected)));
            }
            this.stickerSets[this.currentType].clear();
            ArrayList<TL_messages_stickerSet> packs = DataQuery.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (a = 0; a < packs.size(); a++) {
                TL_messages_stickerSet pack = (TL_messages_stickerSet) packs.get(a);
                if (!(pack.set.archived || pack.documents == null || pack.documents.isEmpty())) {
                    this.stickerSets[this.currentType].add(pack);
                }
            }
            for (a = 0; a < this.stickerSets[this.currentType].size(); a++) {
                this.scrollSlidingTabStrip.addStickerTab((Document) ((TL_messages_stickerSet) this.stickerSets[this.currentType].get(a)).documents.get(0));
            }
            this.scrollSlidingTabStrip.updateTabStyles();
            if (lastPosition != 0) {
                this.scrollSlidingTabStrip.onPageScrolled(lastPosition, lastPosition);
            }
            checkPanels();
        }
    }

    private void checkPanels() {
        if (this.scrollSlidingTabStrip != null) {
            int position = this.stickersLayoutManager.findFirstVisibleItemPosition();
            if (position != -1) {
                this.scrollSlidingTabStrip.onPageScrolled(this.stickersGridAdapter.getTabForPosition(position) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
            }
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            DataQuery.getInstance(this.currentAccount).addRecentSticker(this.currentType, document, (int) (System.currentTimeMillis() / 1000), false);
            boolean wasEmpty = this.recentStickers[this.currentType].isEmpty();
            this.recentStickers[this.currentType] = DataQuery.getInstance(this.currentAccount).getRecentStickers(this.currentType);
            if (this.stickersGridAdapter != null) {
                this.stickersGridAdapter.notifyDataSetChanged();
            }
            if (wasEmpty) {
                updateStickerTabs();
            }
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.lastNotifyWidth != right - left) {
            this.lastNotifyWidth = right - left;
            reloadStickersAdapter();
        }
        super.onLayout(changed, left, top, right, bottom);
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

    public void setListener(Listener value) {
        this.listener = value;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoaded);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                StickerMasksView.this.updateStickerTabs();
                StickerMasksView.this.reloadStickersAdapter();
            }
        });
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 8) {
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
        int previousCount = this.recentStickers[this.currentType].size();
        this.recentStickers[this.currentType] = DataQuery.getInstance(this.currentAccount).getRecentStickers(this.currentType);
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        if (previousCount != this.recentStickers[this.currentType].size()) {
            updateStickerTabs();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoaded) {
            if (((Integer) args[0]).intValue() == this.currentType) {
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (id == NotificationCenter.recentDocumentsDidLoaded && !((Boolean) args[0]).booleanValue() && ((Integer) args[1]).intValue() == this.currentType) {
            checkDocuments();
        }
    }
}
