package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Configuration;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_messages_featuredStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getOldFeaturedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.StickersSearchAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetCell2;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TrendingStickersLayout;

public class TrendingStickersLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public final TrendingStickersAdapter adapter;
    /* access modifiers changed from: private */
    public AlertDelegate alertDelegate;
    /* access modifiers changed from: private */
    public final int currentAccount;
    /* access modifiers changed from: private */
    public final Delegate delegate;
    /* access modifiers changed from: private */
    public int hash;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public final LongSparseArray<TLRPC$StickerSetCovered> installingStickerSets;
    /* access modifiers changed from: private */
    public final GridLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public final RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loaded;
    /* access modifiers changed from: private */
    public boolean motionEventCatchedByListView;
    /* access modifiers changed from: private */
    public RecyclerView.OnScrollListener onScrollListener;
    private BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public final TLRPC$StickerSetCovered[] primaryInstallingStickerSets;
    /* access modifiers changed from: private */
    public final LongSparseArray<TLRPC$StickerSetCovered> removingStickerSets;
    /* access modifiers changed from: private */
    public final StickersSearchAdapter searchAdapter;
    private final FrameLayout searchLayout;
    /* access modifiers changed from: private */
    public final SearchField searchView;
    private final View shadowView;
    private boolean shadowVisible;
    /* access modifiers changed from: private */
    public int topOffset;
    private boolean wasLayout;

    public interface AlertDelegate {
        void setHeavyOperationsEnabled(boolean z);
    }

    public static abstract class Delegate {
        private String[] lastSearchKeyboardLanguage = new String[0];

        public boolean onListViewInterceptTouchEvent(RecyclerListView recyclerListView, MotionEvent motionEvent) {
            return false;
        }

        public boolean onListViewTouchEvent(RecyclerListView recyclerListView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent motionEvent) {
            return false;
        }

        public abstract void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z);

        public abstract void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered);

        public String[] getLastSearchKeyboardLanguage() {
            return this.lastSearchKeyboardLanguage;
        }

        public void setLastSearchKeyboardLanguage(String[] strArr) {
            this.lastSearchKeyboardLanguage = strArr;
        }
    }

    public TrendingStickersLayout(Context context, Delegate delegate2) {
        this(context, delegate2, new TLRPC$StickerSetCovered[10], new LongSparseArray(), new LongSparseArray());
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TrendingStickersLayout(Context context, Delegate delegate2, TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr, LongSparseArray<TLRPC$StickerSetCovered> longSparseArray, LongSparseArray<TLRPC$StickerSetCovered> longSparseArray2) {
        super(context);
        Context context2 = context;
        final Delegate delegate3 = delegate2;
        this.currentAccount = UserConfig.selectedAccount;
        this.delegate = delegate3;
        TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr2 = tLRPC$StickerSetCoveredArr;
        this.primaryInstallingStickerSets = tLRPC$StickerSetCoveredArr2;
        LongSparseArray<TLRPC$StickerSetCovered> longSparseArray3 = longSparseArray;
        this.installingStickerSets = longSparseArray3;
        LongSparseArray<TLRPC$StickerSetCovered> longSparseArray4 = longSparseArray2;
        this.removingStickerSets = longSparseArray4;
        this.adapter = new TrendingStickersAdapter(context2);
        this.searchAdapter = new StickersSearchAdapter(context, new StickersSearchAdapter.Delegate() {
            public void onSearchStart() {
                TrendingStickersLayout.this.searchView.getProgressDrawable().startAnimation();
            }

            public void onSearchStop() {
                TrendingStickersLayout.this.searchView.getProgressDrawable().stopAnimation();
            }

            public void setAdapterVisible(boolean z) {
                if (z && TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.searchAdapter) {
                    TrendingStickersLayout.this.listView.setAdapter(TrendingStickersLayout.this.searchAdapter);
                } else if (!z && TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter) {
                    TrendingStickersLayout.this.listView.setAdapter(TrendingStickersLayout.this.adapter);
                }
            }

            public void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z) {
                delegate3.onStickerSetAdd(tLRPC$StickerSetCovered, z);
            }

            public void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                delegate3.onStickerSetRemove(tLRPC$StickerSetCovered);
            }

            public int getStickersPerRow() {
                return TrendingStickersLayout.this.adapter.stickersPerRow;
            }

            public String[] getLastSearchKeyboardLanguage() {
                return delegate3.getLastSearchKeyboardLanguage();
            }

            public void setLastSearchKeyboardLanguage(String[] strArr) {
                delegate3.setLastSearchKeyboardLanguage(strArr);
            }
        }, tLRPC$StickerSetCoveredArr2, longSparseArray3, longSparseArray4);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.searchLayout = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
        AnonymousClass2 r0 = new SearchField(context2, true) {
            public void onTextChange(String str) {
                TrendingStickersLayout.this.searchAdapter.search(str);
            }
        };
        this.searchView = r0;
        r0.setHint(LocaleController.getString("SearchTrendingStickersHint", NUM));
        this.searchLayout.addView(this.searchView, LayoutHelper.createFrame(-1, -1, 48));
        this.listView = new RecyclerListView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent) || delegate3.onListViewInterceptTouchEvent(this, motionEvent);
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                boolean unused = TrendingStickersLayout.this.motionEventCatchedByListView = true;
                return super.dispatchTouchEvent(motionEvent);
            }

            public void requestLayout() {
                if (!TrendingStickersLayout.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) (TrendingStickersLayout.this.topOffset + AndroidUtilities.dp(58.0f)));
            }
        };
        $$Lambda$TrendingStickersLayout$HT1xW9v9YcfQHbmkDeikpDsJQ0E r10 = new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                TrendingStickersLayout.this.lambda$new$0$TrendingStickersLayout(view, i);
            }
        };
        this.listView.setOnTouchListener(new View.OnTouchListener(delegate3, r10) {
            private final /* synthetic */ TrendingStickersLayout.Delegate f$1;
            private final /* synthetic */ RecyclerListView.OnItemClickListener f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return TrendingStickersLayout.this.lambda$new$1$TrendingStickersLayout(this.f$1, this.f$2, view, motionEvent);
            }
        });
        this.listView.setClipToPadding(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass4 r02 = new FillLastGridLayoutManager(context, 5, AndroidUtilities.dp(58.0f), this.listView) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            /* access modifiers changed from: protected */
            public boolean isLayoutRTL() {
                return LocaleController.isRTL;
            }

            /* access modifiers changed from: protected */
            public boolean shouldCalcLastItemHeight() {
                return TrendingStickersLayout.this.listView.getAdapter() == TrendingStickersLayout.this.searchAdapter;
            }
        };
        this.layoutManager = r02;
        recyclerListView.setLayoutManager(r02);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                if (TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter) {
                    return TrendingStickersLayout.this.searchAdapter.getSpanSize(i);
                }
                if ((TrendingStickersLayout.this.adapter.cache.get(i) instanceof Integer) || i >= TrendingStickersLayout.this.adapter.totalItems) {
                    return TrendingStickersLayout.this.adapter.stickersPerRow;
                }
                return 1;
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (TrendingStickersLayout.this.onScrollListener != null) {
                    TrendingStickersLayout.this.onScrollListener.onScrolled(TrendingStickersLayout.this.listView, i, i2);
                }
                if (i2 > 0 && TrendingStickersLayout.this.listView.getAdapter() == TrendingStickersLayout.this.adapter && TrendingStickersLayout.this.loaded && !TrendingStickersLayout.this.adapter.loadingMore && !TrendingStickersLayout.this.adapter.endReached) {
                    if (TrendingStickersLayout.this.layoutManager.findLastVisibleItemPosition() >= (TrendingStickersLayout.this.adapter.getItemCount() - ((TrendingStickersLayout.this.adapter.stickersPerRow + 1) * 10)) - 1) {
                        TrendingStickersLayout.this.adapter.loadMoreStickerSets();
                    }
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (TrendingStickersLayout.this.onScrollListener != null) {
                    TrendingStickersLayout.this.onScrollListener.onScrollStateChanged(recyclerView, i);
                }
            }
        });
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) r10);
        this.listView.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(context2);
        this.shadowView = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.shadowView.setAlpha(0.0f);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight());
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        addView(this.shadowView, layoutParams);
        addView(this.searchLayout, LayoutHelper.createFrame(-1, 58, 51));
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        instance.addObserver(this, NotificationCenter.stickersDidLoad);
        instance.addObserver(this, NotificationCenter.featuredStickersDidLoad);
    }

    public /* synthetic */ void lambda$new$0$TrendingStickersLayout(View view, int i) {
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered;
        RecyclerView.Adapter adapter2 = this.listView.getAdapter();
        StickersSearchAdapter stickersSearchAdapter = this.searchAdapter;
        if (adapter2 == stickersSearchAdapter) {
            tLRPC$StickerSetCovered = stickersSearchAdapter.getSetForPosition(i);
        } else {
            tLRPC$StickerSetCovered = i < this.adapter.totalItems ? (TLRPC$StickerSetCovered) this.adapter.positionsToSets.get(i) : null;
        }
        if (tLRPC$StickerSetCovered != null) {
            showStickerSet(tLRPC$StickerSetCovered.set);
        }
    }

    public /* synthetic */ boolean lambda$new$1$TrendingStickersLayout(Delegate delegate2, RecyclerListView.OnItemClickListener onItemClickListener, View view, MotionEvent motionEvent) {
        return delegate2.onListViewTouchEvent(this.listView, onItemClickListener, motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (!this.wasLayout) {
            this.wasLayout = true;
            this.adapter.refreshStickerSets();
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateLastItemInAdapter();
        this.wasLayout = false;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.motionEventCatchedByListView = false;
        boolean dispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
        if (!this.motionEventCatchedByListView) {
            MotionEvent obtain = MotionEvent.obtain(motionEvent);
            this.listView.dispatchTouchEvent(obtain);
            obtain.recycle();
        }
        return dispatchTouchEvent;
    }

    private void showStickerSet(TLRPC$StickerSet tLRPC$StickerSet) {
        showStickerSet(tLRPC$StickerSet, (TLRPC$InputStickerSet) null);
    }

    public void showStickerSet(TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet) {
        if (tLRPC$StickerSet != null) {
            tLRPC$InputStickerSet = new TLRPC$TL_inputStickerSetID();
            tLRPC$InputStickerSet.access_hash = tLRPC$StickerSet.access_hash;
            tLRPC$InputStickerSet.id = tLRPC$StickerSet.id;
        }
        if (tLRPC$InputStickerSet != null) {
            showStickerSet(tLRPC$InputStickerSet);
        }
    }

    private void showStickerSet(final TLRPC$InputStickerSet tLRPC$InputStickerSet) {
        AnonymousClass7 r0 = new StickersAlert(getContext(), this.parentFragment, tLRPC$InputStickerSet, (TLRPC$TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null) {
            public void show() {
                super.show();
                if (TrendingStickersLayout.this.alertDelegate != null) {
                    TrendingStickersLayout.this.alertDelegate.setHeavyOperationsEnabled(true);
                }
            }

            public void dismiss() {
                super.dismiss();
                if (TrendingStickersLayout.this.alertDelegate != null) {
                    TrendingStickersLayout.this.alertDelegate.setHeavyOperationsEnabled(false);
                }
            }
        };
        r0.setShowTooltipWhenToggle(false);
        r0.setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate() {
            public void onStickerSetUninstalled() {
            }

            public void onStickerSetInstalled() {
                if (TrendingStickersLayout.this.listView.getAdapter() == TrendingStickersLayout.this.adapter) {
                    for (int i = 0; i < TrendingStickersLayout.this.adapter.sets.size(); i++) {
                        TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) TrendingStickersLayout.this.adapter.sets.get(i);
                        if (tLRPC$StickerSetCovered.set.id == tLRPC$InputStickerSet.id) {
                            TrendingStickersLayout.this.adapter.installStickerSet(tLRPC$StickerSetCovered, (View) null);
                            return;
                        }
                    }
                    return;
                }
                TrendingStickersLayout.this.searchAdapter.installStickerSet(tLRPC$InputStickerSet);
            }
        });
        this.parentFragment.showDialog(r0);
    }

    public void recycle() {
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        instance.removeObserver(this, NotificationCenter.stickersDidLoad);
        instance.removeObserver(this, NotificationCenter.featuredStickersDidLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            if (objArr[0].intValue() != 0) {
                return;
            }
            if (this.loaded) {
                updateVisibleTrendingSets();
            } else {
                this.adapter.refreshStickerSets();
            }
        } else if (i == NotificationCenter.featuredStickersDidLoad) {
            if (this.hash != MediaDataController.getInstance(this.currentAccount).getFeaturesStickersHashWithoutUnread()) {
                this.loaded = false;
            }
            if (this.loaded) {
                updateVisibleTrendingSets();
            } else {
                this.adapter.refreshStickerSets();
            }
        }
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener2) {
        this.onScrollListener = onScrollListener2;
    }

    public void setAlertDelegate(AlertDelegate alertDelegate2) {
        this.alertDelegate = alertDelegate2;
    }

    public void setParentFragment(BaseFragment baseFragment) {
        this.parentFragment = baseFragment;
    }

    public void setContentViewPaddingTop(int i) {
        int dp = i + AndroidUtilities.dp(58.0f);
        if (this.listView.getPaddingTop() != dp) {
            this.ignoreLayout = true;
            RecyclerListView recyclerListView = this.listView;
            recyclerListView.setPadding(0, dp, 0, recyclerListView.getPaddingBottom());
            this.ignoreLayout = false;
        }
    }

    public void setContentViewPaddingBottom(int i) {
        if (this.listView.getPaddingBottom() != i) {
            this.ignoreLayout = true;
            RecyclerListView recyclerListView = this.listView;
            recyclerListView.setPadding(0, recyclerListView.getPaddingTop(), 0, i);
            updateLastItemInAdapter();
            this.ignoreLayout = false;
        }
    }

    private void updateLastItemInAdapter() {
        RecyclerView.Adapter adapter2 = this.listView.getAdapter();
        adapter2.notifyItemChanged(adapter2.getItemCount() - 1);
    }

    public int getContentTopOffset() {
        return this.topOffset;
    }

    public boolean update() {
        if (this.listView.getChildCount() <= 0) {
            int paddingTop = this.listView.getPaddingTop();
            this.topOffset = paddingTop;
            this.listView.setTopGlowOffset(paddingTop);
            this.searchLayout.setTranslationY((float) this.topOffset);
            this.shadowView.setTranslationY((float) this.topOffset);
            setShadowVisible(false);
            return true;
        }
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(58.0f);
        int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        setShadowVisible(top < 0);
        if (this.topOffset == i) {
            return false;
        }
        this.topOffset = i;
        this.listView.setTopGlowOffset(i + AndroidUtilities.dp(58.0f));
        this.searchLayout.setTranslationY((float) this.topOffset);
        this.shadowView.setTranslationY((float) this.topOffset);
        return true;
    }

    private void updateVisibleTrendingSets() {
        RecyclerView.Adapter adapter2 = this.listView.getAdapter();
        if (adapter2 != null) {
            int itemCount = adapter2.getItemCount();
            TrendingStickersAdapter trendingStickersAdapter = this.adapter;
            adapter2.notifyItemRangeChanged(0, itemCount, 0);
        }
    }

    private void setShadowVisible(boolean z) {
        if (this.shadowVisible != z) {
            this.shadowVisible = z;
            this.shadowView.animate().alpha(z ? 1.0f : 0.0f).setDuration(200).start();
        }
    }

    private class TrendingStickersAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public final SparseArray<Object> cache = new SparseArray<>();
        private final Context context;
        /* access modifiers changed from: private */
        public boolean endReached;
        /* access modifiers changed from: private */
        public boolean loadingMore;
        private final ArrayList<TLRPC$StickerSetCovered> otherPacks = new ArrayList<>();
        /* access modifiers changed from: private */
        public final SparseArray<TLRPC$StickerSetCovered> positionsToSets = new SparseArray<>();
        /* access modifiers changed from: private */
        public final ArrayList<TLRPC$StickerSetCovered> sets = new ArrayList<>();
        /* access modifiers changed from: private */
        public int stickersPerRow = 5;
        /* access modifiers changed from: private */
        public int totalItems;

        public TrendingStickersAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return this.cache.size() + 1;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 5;
        }

        public int getItemViewType(int i) {
            if (i == getItemCount() - 1) {
                return 3;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC$Document) {
                return 0;
            }
            return obj.equals(-1) ? 4 : 2;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$TrendingStickersLayout$TrendingStickersAdapter(View view) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
            TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
            if (TrendingStickersLayout.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0 && TrendingStickersLayout.this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                if (featuredStickerSetInfoCell.isInstalled()) {
                    TrendingStickersLayout.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                    TrendingStickersLayout.this.delegate.onStickerSetRemove(stickerSet);
                    return;
                }
                installStickerSet(stickerSet, featuredStickerSetInfoCell);
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: org.telegram.ui.Cells.EmptyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v12, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v11, resolved type: org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$1} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
            /*
                r2 = this;
                r3 = 3
                if (r4 == 0) goto L_0x0054
                r0 = 1
                if (r4 == r0) goto L_0x004c
                r1 = 2
                if (r4 == r1) goto L_0x003a
                if (r4 == r3) goto L_0x0032
                r0 = 4
                if (r4 == r0) goto L_0x002a
                r0 = 5
                if (r4 == r0) goto L_0x0013
                r3 = 0
                goto L_0x0063
            L_0x0013:
                org.telegram.ui.Cells.FeaturedStickerSetCell2 r4 = new org.telegram.ui.Cells.FeaturedStickerSetCell2
                android.content.Context r0 = r2.context
                r4.<init>(r0)
                org.telegram.ui.Components.-$$Lambda$TrendingStickersLayout$TrendingStickersAdapter$BGf_ekA2jvJsKmLERxyFUOGPJSk r0 = new org.telegram.ui.Components.-$$Lambda$TrendingStickersLayout$TrendingStickersAdapter$BGf_ekA2jvJsKmLERxyFUOGPJSk
                r0.<init>()
                r4.setAddOnClickListener(r0)
                org.telegram.ui.Components.BackupImageView r0 = r4.getImageView()
                r0.setLayerNum(r3)
                goto L_0x0062
            L_0x002a:
                org.telegram.ui.Cells.GraySectionCell r3 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r4 = r2.context
                r3.<init>(r4)
                goto L_0x0063
            L_0x0032:
                android.view.View r3 = new android.view.View
                android.content.Context r4 = r2.context
                r3.<init>(r4)
                goto L_0x0063
            L_0x003a:
                org.telegram.ui.Cells.FeaturedStickerSetInfoCell r3 = new org.telegram.ui.Cells.FeaturedStickerSetInfoCell
                android.content.Context r4 = r2.context
                r1 = 17
                r3.<init>(r4, r1, r0)
                org.telegram.ui.Components.-$$Lambda$TrendingStickersLayout$TrendingStickersAdapter$RXIiDwrbl8vjqU3_RmIxZWokTUU r4 = new org.telegram.ui.Components.-$$Lambda$TrendingStickersLayout$TrendingStickersAdapter$RXIiDwrbl8vjqU3_RmIxZWokTUU
                r4.<init>()
                r3.setAddOnClickListener(r4)
                goto L_0x0063
            L_0x004c:
                org.telegram.ui.Cells.EmptyCell r3 = new org.telegram.ui.Cells.EmptyCell
                android.content.Context r4 = r2.context
                r3.<init>(r4)
                goto L_0x0063
            L_0x0054:
                org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$1 r4 = new org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$1
                android.content.Context r0 = r2.context
                r4.<init>(r2, r0)
                org.telegram.ui.Components.BackupImageView r0 = r4.getImageView()
                r0.setLayerNum(r3)
            L_0x0062:
                r3 = r4
            L_0x0063:
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r3)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TrendingStickersLayout.TrendingStickersAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1$TrendingStickersLayout$TrendingStickersAdapter(View view) {
            FeaturedStickerSetCell2 featuredStickerSetCell2 = (FeaturedStickerSetCell2) view.getParent();
            TLRPC$StickerSetCovered stickerSet = featuredStickerSetCell2.getStickerSet();
            if (TrendingStickersLayout.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0 && TrendingStickersLayout.this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                if (featuredStickerSetCell2.isInstalled()) {
                    TrendingStickersLayout.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                    TrendingStickersLayout.this.delegate.onStickerSetRemove(stickerSet);
                    return;
                }
                installStickerSet(stickerSet, featuredStickerSetCell2);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ((StickerEmojiCell) viewHolder.itemView).setSticker((TLRPC$Document) this.cache.get(i), this.positionsToSets.get(i), false);
            } else if (itemViewType != 1) {
                if (itemViewType != 2) {
                    if (itemViewType == 4) {
                        ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("OtherStickers", NUM));
                        return;
                    } else if (itemViewType != 5) {
                        return;
                    }
                }
                bindStickerSetCell(viewHolder.itemView, i, false);
            } else {
                ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List list) {
            if (list.contains(0)) {
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType == 2 || itemViewType == 5) {
                    bindStickerSetCell(viewHolder.itemView, i, true);
                    return;
                }
                return;
            }
            super.onBindViewHolder(viewHolder, i, list);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:52:0x013b, code lost:
            if (r11.cache.get(r13).equals(-1) != false) goto L_0x013e;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void bindStickerSetCell(android.view.View r12, int r13, boolean r14) {
            /*
                r11 = this;
                org.telegram.ui.Components.TrendingStickersLayout r0 = org.telegram.ui.Components.TrendingStickersLayout.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                int r1 = r11.totalItems
                r2 = 1
                r3 = 0
                if (r13 >= r1) goto L_0x0047
                java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r11.sets
                android.util.SparseArray<java.lang.Object> r4 = r11.cache
                java.lang.Object r4 = r4.get(r13)
                java.lang.Integer r4 = (java.lang.Integer) r4
                int r4 = r4.intValue()
                java.lang.Object r1 = r1.get(r4)
                org.telegram.tgnet.TLRPC$StickerSetCovered r1 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r1
                java.util.ArrayList r4 = r0.getUnreadStickerSets()
                if (r4 == 0) goto L_0x003a
                org.telegram.tgnet.TLRPC$StickerSet r5 = r1.set
                long r5 = r5.id
                java.lang.Long r5 = java.lang.Long.valueOf(r5)
                boolean r4 = r4.contains(r5)
                if (r4 == 0) goto L_0x003a
                r4 = 1
                goto L_0x003b
            L_0x003a:
                r4 = 0
            L_0x003b:
                if (r4 == 0) goto L_0x0044
                org.telegram.tgnet.TLRPC$StickerSet r5 = r1.set
                long r5 = r5.id
                r0.markFaturedStickersByIdAsRead(r5)
            L_0x0044:
                r5 = r1
                r6 = r4
                goto L_0x005d
            L_0x0047:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r11.sets
                android.util.SparseArray<java.lang.Object> r4 = r11.cache
                java.lang.Object r4 = r4.get(r13)
                java.lang.Integer r4 = (java.lang.Integer) r4
                int r4 = r4.intValue()
                java.lang.Object r1 = r1.get(r4)
                org.telegram.tgnet.TLRPC$StickerSetCovered r1 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r1
                r5 = r1
                r6 = 0
            L_0x005d:
                r0.preloadStickerSetThumb((org.telegram.tgnet.TLRPC$StickerSetCovered) r5)
                r1 = 0
            L_0x0061:
                org.telegram.ui.Components.TrendingStickersLayout r4 = org.telegram.ui.Components.TrendingStickersLayout.this
                org.telegram.tgnet.TLRPC$StickerSetCovered[] r4 = r4.primaryInstallingStickerSets
                int r4 = r4.length
                if (r1 >= r4) goto L_0x00b9
                org.telegram.ui.Components.TrendingStickersLayout r4 = org.telegram.ui.Components.TrendingStickersLayout.this
                org.telegram.tgnet.TLRPC$StickerSetCovered[] r4 = r4.primaryInstallingStickerSets
                r4 = r4[r1]
                if (r4 == 0) goto L_0x00b6
                org.telegram.ui.Components.TrendingStickersLayout r4 = org.telegram.ui.Components.TrendingStickersLayout.this
                int r4 = r4.currentAccount
                org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
                org.telegram.ui.Components.TrendingStickersLayout r7 = org.telegram.ui.Components.TrendingStickersLayout.this
                org.telegram.tgnet.TLRPC$StickerSetCovered[] r7 = r7.primaryInstallingStickerSets
                r7 = r7[r1]
                org.telegram.tgnet.TLRPC$StickerSet r7 = r7.set
                long r7 = r7.id
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = r4.getStickerSetById(r7)
                if (r4 == 0) goto L_0x00a0
                org.telegram.tgnet.TLRPC$StickerSet r4 = r4.set
                boolean r4 = r4.archived
                if (r4 != 0) goto L_0x00a0
                org.telegram.ui.Components.TrendingStickersLayout r4 = org.telegram.ui.Components.TrendingStickersLayout.this
                org.telegram.tgnet.TLRPC$StickerSetCovered[] r4 = r4.primaryInstallingStickerSets
                r7 = 0
                r4[r1] = r7
                goto L_0x00b6
            L_0x00a0:
                org.telegram.ui.Components.TrendingStickersLayout r4 = org.telegram.ui.Components.TrendingStickersLayout.this
                org.telegram.tgnet.TLRPC$StickerSetCovered[] r4 = r4.primaryInstallingStickerSets
                r4 = r4[r1]
                org.telegram.tgnet.TLRPC$StickerSet r4 = r4.set
                long r7 = r4.id
                org.telegram.tgnet.TLRPC$StickerSet r4 = r5.set
                long r9 = r4.id
                int r4 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r4 != 0) goto L_0x00b6
                r1 = 1
                goto L_0x00ba
            L_0x00b6:
                int r1 = r1 + 1
                goto L_0x0061
            L_0x00b9:
                r1 = 0
            L_0x00ba:
                org.telegram.tgnet.TLRPC$StickerSet r4 = r5.set
                long r7 = r4.id
                boolean r0 = r0.isStickerPackInstalled((long) r7)
                org.telegram.ui.Components.TrendingStickersLayout r4 = org.telegram.ui.Components.TrendingStickersLayout.this
                android.util.LongSparseArray r4 = r4.installingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r7 = r5.set
                long r7 = r7.id
                int r4 = r4.indexOfKey(r7)
                if (r4 < 0) goto L_0x00d4
                r4 = 1
                goto L_0x00d5
            L_0x00d4:
                r4 = 0
            L_0x00d5:
                org.telegram.ui.Components.TrendingStickersLayout r7 = org.telegram.ui.Components.TrendingStickersLayout.this
                android.util.LongSparseArray r7 = r7.removingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r8 = r5.set
                long r8 = r8.id
                int r7 = r7.indexOfKey(r8)
                if (r7 < 0) goto L_0x00e7
                r7 = 1
                goto L_0x00e8
            L_0x00e7:
                r7 = 0
            L_0x00e8:
                if (r4 == 0) goto L_0x00fb
                if (r0 == 0) goto L_0x00fb
                org.telegram.ui.Components.TrendingStickersLayout r0 = org.telegram.ui.Components.TrendingStickersLayout.this
                android.util.LongSparseArray r0 = r0.installingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r4 = r5.set
                long r7 = r4.id
                r0.remove(r7)
                r0 = 0
                goto L_0x010d
            L_0x00fb:
                if (r7 == 0) goto L_0x010c
                if (r0 != 0) goto L_0x010c
                org.telegram.ui.Components.TrendingStickersLayout r0 = org.telegram.ui.Components.TrendingStickersLayout.this
                android.util.LongSparseArray r0 = r0.removingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r7 = r5.set
                long r7 = r7.id
                r0.remove(r7)
            L_0x010c:
                r0 = r4
            L_0x010d:
                org.telegram.ui.Cells.FeaturedStickerSetInfoCell r12 = (org.telegram.ui.Cells.FeaturedStickerSetInfoCell) r12
                r8 = 0
                r9 = 0
                r4 = r12
                r7 = r14
                r10 = r1
                r4.setStickerSet(r5, r6, r7, r8, r9, r10)
                if (r1 != 0) goto L_0x011d
                if (r0 == 0) goto L_0x011d
                r0 = 1
                goto L_0x011e
            L_0x011d:
                r0 = 0
            L_0x011e:
                r12.setAddDrawProgress(r0, r14)
                if (r13 <= 0) goto L_0x013e
                android.util.SparseArray<java.lang.Object> r14 = r11.cache
                int r13 = r13 - r2
                java.lang.Object r14 = r14.get(r13)
                if (r14 == 0) goto L_0x013f
                android.util.SparseArray<java.lang.Object> r14 = r11.cache
                java.lang.Object r13 = r14.get(r13)
                r14 = -1
                java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
                boolean r13 = r13.equals(r14)
                if (r13 != 0) goto L_0x013e
                goto L_0x013f
            L_0x013e:
                r2 = 0
            L_0x013f:
                r12.setNeedDivider(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TrendingStickersLayout.TrendingStickersAdapter.bindStickerSetCell(android.view.View, int, boolean):void");
        }

        /* access modifiers changed from: private */
        public void installStickerSet(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, View view) {
            boolean z;
            int i = 0;
            while (true) {
                if (i >= TrendingStickersLayout.this.primaryInstallingStickerSets.length) {
                    break;
                }
                if (TrendingStickersLayout.this.primaryInstallingStickerSets[i] != null) {
                    TLRPC$TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount).getStickerSetById(TrendingStickersLayout.this.primaryInstallingStickerSets[i].set.id);
                    if (stickerSetById != null && !stickerSetById.set.archived) {
                        TrendingStickersLayout.this.primaryInstallingStickerSets[i] = null;
                        break;
                    } else if (TrendingStickersLayout.this.primaryInstallingStickerSets[i].set.id == tLRPC$StickerSetCovered.set.id) {
                        return;
                    }
                }
                i++;
            }
            int i2 = 0;
            while (true) {
                if (i2 >= TrendingStickersLayout.this.primaryInstallingStickerSets.length) {
                    z = false;
                    break;
                } else if (TrendingStickersLayout.this.primaryInstallingStickerSets[i2] == null) {
                    TrendingStickersLayout.this.primaryInstallingStickerSets[i2] = tLRPC$StickerSetCovered;
                    z = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (!z && view != null) {
                if (view instanceof FeaturedStickerSetCell2) {
                    ((FeaturedStickerSetCell2) view).setDrawProgress(true, true);
                } else if (view instanceof FeaturedStickerSetInfoCell) {
                    ((FeaturedStickerSetInfoCell) view).setAddDrawProgress(true, true);
                }
            }
            TrendingStickersLayout.this.installingStickerSets.put(tLRPC$StickerSetCovered.set.id, tLRPC$StickerSetCovered);
            if (view != null) {
                TrendingStickersLayout.this.delegate.onStickerSetAdd(tLRPC$StickerSetCovered, z);
                return;
            }
            int size = this.positionsToSets.size();
            int i3 = 0;
            while (i3 < size) {
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = this.positionsToSets.get(i3);
                if (tLRPC$StickerSetCovered2 == null || tLRPC$StickerSetCovered2.set.id != tLRPC$StickerSetCovered.set.id) {
                    i3++;
                } else {
                    notifyItemChanged(i3, 0);
                    return;
                }
            }
        }

        public void refreshStickerSets() {
            int i;
            int measuredWidth = TrendingStickersLayout.this.getMeasuredWidth();
            if (measuredWidth != 0) {
                this.stickersPerRow = Math.max(5, measuredWidth / AndroidUtilities.dp(72.0f));
                if (TrendingStickersLayout.this.layoutManager.getSpanCount() != this.stickersPerRow) {
                    TrendingStickersLayout.this.layoutManager.setSpanCount(this.stickersPerRow);
                    boolean unused = TrendingStickersLayout.this.loaded = false;
                }
            }
            if (!TrendingStickersLayout.this.loaded) {
                this.cache.clear();
                this.positionsToSets.clear();
                this.sets.clear();
                this.totalItems = 0;
                MediaDataController instance = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount);
                ArrayList arrayList = new ArrayList(instance.getFeaturedStickerSets());
                int size = arrayList.size();
                arrayList.addAll(this.otherPacks);
                int i2 = 0;
                int i3 = 0;
                while (true) {
                    int i4 = 1;
                    if (i2 >= arrayList.size()) {
                        break;
                    }
                    TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) arrayList.get(i2);
                    if (!tLRPC$StickerSetCovered.covers.isEmpty() || tLRPC$StickerSetCovered.cover != null) {
                        if (i2 == size) {
                            SparseArray<Object> sparseArray = this.cache;
                            int i5 = this.totalItems;
                            this.totalItems = i5 + 1;
                            sparseArray.put(i5, -1);
                        }
                        this.sets.add(tLRPC$StickerSetCovered);
                        this.positionsToSets.put(this.totalItems, tLRPC$StickerSetCovered);
                        SparseArray<Object> sparseArray2 = this.cache;
                        int i6 = this.totalItems;
                        this.totalItems = i6 + 1;
                        int i7 = i3 + 1;
                        sparseArray2.put(i6, Integer.valueOf(i3));
                        if (!tLRPC$StickerSetCovered.covers.isEmpty()) {
                            i4 = (int) Math.ceil((double) (((float) tLRPC$StickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                            for (int i8 = 0; i8 < tLRPC$StickerSetCovered.covers.size(); i8++) {
                                this.cache.put(this.totalItems + i8, tLRPC$StickerSetCovered.covers.get(i8));
                            }
                        } else {
                            this.cache.put(this.totalItems, tLRPC$StickerSetCovered.cover);
                        }
                        int i9 = 0;
                        while (true) {
                            i = this.stickersPerRow;
                            if (i9 >= i4 * i) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + i9, tLRPC$StickerSetCovered);
                            i9++;
                        }
                        this.totalItems += i4 * i;
                        i3 = i7;
                    }
                    i2++;
                }
                if (this.totalItems != 0) {
                    boolean unused2 = TrendingStickersLayout.this.loaded = true;
                    int unused3 = TrendingStickersLayout.this.hash = instance.getFeaturesStickersHashWithoutUnread();
                }
                notifyDataSetChanged();
            }
        }

        public void loadMoreStickerSets() {
            if (TrendingStickersLayout.this.loaded && !this.loadingMore && !this.endReached) {
                this.loadingMore = true;
                TLRPC$TL_messages_getOldFeaturedStickers tLRPC$TL_messages_getOldFeaturedStickers = new TLRPC$TL_messages_getOldFeaturedStickers();
                tLRPC$TL_messages_getOldFeaturedStickers.offset = this.otherPacks.size();
                tLRPC$TL_messages_getOldFeaturedStickers.limit = 40;
                ConnectionsManager.getInstance(TrendingStickersLayout.this.currentAccount).sendRequest(tLRPC$TL_messages_getOldFeaturedStickers, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        TrendingStickersLayout.TrendingStickersAdapter.this.lambda$loadMoreStickerSets$3$TrendingStickersLayout$TrendingStickersAdapter(tLObject, tLRPC$TL_error);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$loadMoreStickerSets$3$TrendingStickersLayout$TrendingStickersAdapter(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
                private final /* synthetic */ TLRPC$TL_error f$1;
                private final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    TrendingStickersLayout.TrendingStickersAdapter.this.lambda$null$2$TrendingStickersLayout$TrendingStickersAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$2$TrendingStickersLayout$TrendingStickersAdapter(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
            int i;
            int i2;
            this.loadingMore = false;
            if (tLRPC$TL_error != null || !(tLObject instanceof TLRPC$TL_messages_featuredStickers)) {
                this.endReached = true;
                return;
            }
            ArrayList<TLRPC$StickerSetCovered> arrayList = ((TLRPC$TL_messages_featuredStickers) tLObject).sets;
            if (arrayList.size() < 40) {
                this.endReached = true;
            }
            if (!arrayList.isEmpty()) {
                if (this.otherPacks.isEmpty()) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i3 = this.totalItems;
                    this.totalItems = i3 + 1;
                    sparseArray.put(i3, -1);
                }
                this.otherPacks.addAll(arrayList);
                int size = this.sets.size();
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    TLRPC$StickerSetCovered tLRPC$StickerSetCovered = arrayList.get(i4);
                    if (!tLRPC$StickerSetCovered.covers.isEmpty() || tLRPC$StickerSetCovered.cover != null) {
                        this.sets.add(tLRPC$StickerSetCovered);
                        this.positionsToSets.put(this.totalItems, tLRPC$StickerSetCovered);
                        SparseArray<Object> sparseArray2 = this.cache;
                        int i5 = this.totalItems;
                        this.totalItems = i5 + 1;
                        int i6 = size + 1;
                        sparseArray2.put(i5, Integer.valueOf(size));
                        if (!tLRPC$StickerSetCovered.covers.isEmpty()) {
                            i = (int) Math.ceil((double) (((float) tLRPC$StickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                            for (int i7 = 0; i7 < tLRPC$StickerSetCovered.covers.size(); i7++) {
                                this.cache.put(this.totalItems + i7, tLRPC$StickerSetCovered.covers.get(i7));
                            }
                        } else {
                            this.cache.put(this.totalItems, tLRPC$StickerSetCovered.cover);
                            i = 1;
                        }
                        int i8 = 0;
                        while (true) {
                            i2 = this.stickersPerRow;
                            if (i8 >= i * i2) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + i8, tLRPC$StickerSetCovered);
                            i8++;
                        }
                        this.totalItems += i * i2;
                        size = i6;
                    }
                }
                notifyDataSetChanged();
            }
        }
    }
}
