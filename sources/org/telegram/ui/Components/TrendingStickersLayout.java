package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.StickersSearchAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetCell2;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;

public class TrendingStickersLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public final TrendingStickersAdapter adapter;
    /* access modifiers changed from: private */
    public final int currentAccount;
    /* access modifiers changed from: private */
    public final Delegate delegate;
    ValueAnimator glueToTopAnimator;
    /* access modifiers changed from: private */
    public boolean gluedToTop;
    /* access modifiers changed from: private */
    public int hash;
    private float highlightProgress;
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
    Paint paint;
    private BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public final TLRPC$StickerSetCovered[] primaryInstallingStickerSets;
    /* access modifiers changed from: private */
    public final LongSparseArray<TLRPC$StickerSetCovered> removingStickerSets;
    /* access modifiers changed from: private */
    public boolean scrollFromAnimator;
    private TLRPC$StickerSetCovered scrollToSet;
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

    public static abstract class Delegate {
        private String[] lastSearchKeyboardLanguage = new String[0];

        public boolean canSchedule() {
            return false;
        }

        public boolean canSendSticker() {
            return false;
        }

        public boolean isInScheduleMode() {
            return false;
        }

        public boolean onListViewInterceptTouchEvent(RecyclerListView recyclerListView, MotionEvent motionEvent) {
            return false;
        }

        public boolean onListViewTouchEvent(RecyclerListView recyclerListView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent motionEvent) {
            return false;
        }

        public void onStickerSelected(TLRPC$Document tLRPC$Document, Object obj, boolean z, boolean z2, int i) {
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
        this(context, delegate2, new TLRPC$StickerSetCovered[10], new LongSparseArray(), new LongSparseArray(), (TLRPC$StickerSetCovered) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TrendingStickersLayout(Context context, Delegate delegate2, TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr, LongSparseArray<TLRPC$StickerSetCovered> longSparseArray, LongSparseArray<TLRPC$StickerSetCovered> longSparseArray2, TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
        super(context);
        Context context2 = context;
        final Delegate delegate3 = delegate2;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.highlightProgress = 1.0f;
        this.paint = new Paint();
        this.delegate = delegate3;
        TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr2 = tLRPC$StickerSetCoveredArr;
        this.primaryInstallingStickerSets = tLRPC$StickerSetCoveredArr2;
        LongSparseArray<TLRPC$StickerSetCovered> longSparseArray3 = longSparseArray;
        this.installingStickerSets = longSparseArray3;
        LongSparseArray<TLRPC$StickerSetCovered> longSparseArray4 = longSparseArray2;
        this.removingStickerSets = longSparseArray4;
        this.scrollToSet = tLRPC$StickerSetCovered;
        TrendingStickersAdapter trendingStickersAdapter = new TrendingStickersAdapter(context2);
        this.adapter = trendingStickersAdapter;
        this.searchAdapter = new StickersSearchAdapter(context, new StickersSearchAdapter.Delegate() {
            public void onSearchStart() {
                TrendingStickersLayout.this.searchView.getProgressDrawable().startAnimation();
            }

            public void onSearchStop() {
                TrendingStickersLayout.this.searchView.getProgressDrawable().stopAnimation();
            }

            public void setAdapterVisible(boolean z) {
                boolean z2 = true;
                if (z && TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.searchAdapter) {
                    TrendingStickersLayout.this.listView.setAdapter(TrendingStickersLayout.this.searchAdapter);
                } else if (z || TrendingStickersLayout.this.listView.getAdapter() == TrendingStickersLayout.this.adapter) {
                    z2 = false;
                } else {
                    TrendingStickersLayout.this.listView.setAdapter(TrendingStickersLayout.this.adapter);
                }
                if (z2 && TrendingStickersLayout.this.listView.getAdapter().getItemCount() > 0) {
                    TrendingStickersLayout.this.layoutManager.scrollToPositionWithOffset(0, (-TrendingStickersLayout.this.listView.getPaddingTop()) + AndroidUtilities.dp(58.0f) + TrendingStickersLayout.this.topOffset, false);
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
        frameLayout.addView(r0, LayoutHelper.createFrame(-1, -1, 48));
        AnonymousClass3 r13 = new RecyclerListView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent) || delegate3.onListViewInterceptTouchEvent(this, motionEvent);
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                boolean unused = TrendingStickersLayout.this.motionEventCatchedByListView = true;
                return super.dispatchTouchEvent(motionEvent);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (TrendingStickersLayout.this.glueToTopAnimator != null) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
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
        this.listView = r13;
        TrendingStickersLayout$$ExternalSyntheticLambda1 trendingStickersLayout$$ExternalSyntheticLambda1 = new TrendingStickersLayout$$ExternalSyntheticLambda1(this);
        r13.setOnTouchListener(new TrendingStickersLayout$$ExternalSyntheticLambda0(this, delegate3, trendingStickersLayout$$ExternalSyntheticLambda1));
        r13.setOverScrollMode(2);
        r13.setClipToPadding(false);
        r13.setItemAnimator((RecyclerView.ItemAnimator) null);
        r13.setLayoutAnimation((LayoutAnimationController) null);
        AnonymousClass4 r02 = new FillLastGridLayoutManager(context, 5, AndroidUtilities.dp(58.0f), r13) {
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

            public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
                int i2;
                View findViewByPosition;
                if (TrendingStickersLayout.this.scrollFromAnimator) {
                    return super.scrollVerticallyBy(i, recycler, state);
                }
                TrendingStickersLayout trendingStickersLayout = TrendingStickersLayout.this;
                int i3 = 0;
                if (trendingStickersLayout.glueToTopAnimator != null) {
                    return 0;
                }
                if (trendingStickersLayout.gluedToTop) {
                    while (true) {
                        i2 = 1;
                        if (i3 >= getChildCount()) {
                            break;
                        }
                        int childAdapterPosition = TrendingStickersLayout.this.listView.getChildAdapterPosition(getChildAt(i3));
                        if (childAdapterPosition < 1) {
                            i2 = childAdapterPosition;
                            break;
                        }
                        i3++;
                    }
                    if (i2 == 0 && (findViewByPosition = TrendingStickersLayout.this.layoutManager.findViewByPosition(i2)) != null && findViewByPosition.getTop() - i > AndroidUtilities.dp(58.0f)) {
                        i = findViewByPosition.getTop() - AndroidUtilities.dp(58.0f);
                    }
                }
                return super.scrollVerticallyBy(i, recycler, state);
            }
        };
        this.layoutManager = r02;
        r13.setLayoutManager(r02);
        r02.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
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
        r13.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
        r13.setAdapter(trendingStickersAdapter);
        r13.setOnItemClickListener((RecyclerListView.OnItemClickListener) trendingStickersLayout$$ExternalSyntheticLambda1);
        addView(r13, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(context2);
        this.shadowView = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        view.setAlpha(0.0f);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight());
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        addView(view, layoutParams);
        addView(frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        updateColors();
        NotificationCenter instance = NotificationCenter.getInstance(i);
        instance.addObserver(this, NotificationCenter.stickersDidLoad);
        instance.addObserver(this, NotificationCenter.featuredStickersDidLoad);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1(Delegate delegate2, RecyclerListView.OnItemClickListener onItemClickListener, View view, MotionEvent motionEvent) {
        return delegate2.onListViewTouchEvent(this.listView, onItemClickListener, motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        Integer num;
        super.onLayout(z, i, i2, i3, i4);
        if (!this.wasLayout) {
            this.wasLayout = true;
            this.adapter.refreshStickerSets();
            if (this.scrollToSet != null && (num = (Integer) this.adapter.setsToPosition.get(this.scrollToSet)) != null) {
                this.layoutManager.scrollToPositionWithOffset(num.intValue(), (-this.listView.getPaddingTop()) + AndroidUtilities.dp(58.0f));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        int i;
        float f = this.highlightProgress;
        if (!(f == 0.0f || this.scrollToSet == null)) {
            this.highlightProgress = f - 0.0053333333f;
            Integer num = (Integer) this.adapter.setsToPosition.get(this.scrollToSet);
            if (num != null) {
                View findViewByPosition = this.layoutManager.findViewByPosition(num.intValue());
                int i2 = -1;
                if (findViewByPosition != null) {
                    i2 = (int) findViewByPosition.getY();
                    i = ((int) findViewByPosition.getY()) + findViewByPosition.getMeasuredHeight();
                } else {
                    i = -1;
                }
                View findViewByPosition2 = this.layoutManager.findViewByPosition(num.intValue() + 1);
                if (findViewByPosition2 != null) {
                    if (findViewByPosition == null) {
                        i2 = (int) findViewByPosition2.getY();
                    }
                    i = ((int) findViewByPosition2.getY()) + findViewByPosition2.getMeasuredHeight();
                }
                if (!(findViewByPosition == null && findViewByPosition2 == null)) {
                    this.paint.setColor(Theme.getColor("featuredStickers_addButton"));
                    float f2 = this.highlightProgress;
                    this.paint.setAlpha((int) ((f2 < 0.06f ? f2 / 0.06f : 1.0f) * 25.5f));
                    canvas.drawRect(0.0f, (float) i2, (float) getMeasuredWidth(), (float) i, this.paint);
                }
            }
            invalidate();
        }
        super.dispatchDraw(canvas);
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
        StickersAlert stickersAlert = new StickersAlert(getContext(), this.parentFragment, tLRPC$InputStickerSet, (TLRPC$TL_messages_stickerSet) null, this.delegate.canSendSticker() ? new StickersAlert.StickersAlertDelegate() {
            public void onStickerSelected(TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, boolean z2, int i) {
                TrendingStickersLayout.this.delegate.onStickerSelected(tLRPC$Document, obj, z, z2, i);
            }

            public boolean canSchedule() {
                return TrendingStickersLayout.this.delegate.canSchedule();
            }

            public boolean isInScheduleMode() {
                return TrendingStickersLayout.this.delegate.isInScheduleMode();
            }
        } : null);
        stickersAlert.setShowTooltipWhenToggle(false);
        stickersAlert.setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate() {
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
        this.parentFragment.showDialog(stickersAlert);
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

    public void setParentFragment(BaseFragment baseFragment) {
        this.parentFragment = baseFragment;
    }

    public void setContentViewPaddingTop(int i) {
        int dp = i + AndroidUtilities.dp(58.0f);
        if (this.listView.getPaddingTop() != dp) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, dp, 0, 0);
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
        for (int i = 1; i < this.listView.getChildCount(); i++) {
            View childAt2 = this.listView.getChildAt(i);
            if (childAt2.getTop() < childAt.getTop()) {
                childAt = childAt2;
            }
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(58.0f);
        int i2 = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        setShadowVisible(top < 0);
        if (this.topOffset == i2) {
            return false;
        }
        this.topOffset = i2;
        this.listView.setTopGlowOffset(i2 + AndroidUtilities.dp(58.0f));
        this.searchLayout.setTranslationY((float) this.topOffset);
        this.shadowView.setTranslationY((float) this.topOffset);
        return true;
    }

    private void updateVisibleTrendingSets() {
        RecyclerView.Adapter adapter2 = this.listView.getAdapter();
        if (adapter2 != null) {
            adapter2.notifyItemRangeChanged(0, adapter2.getItemCount(), 0);
        }
    }

    private void setShadowVisible(boolean z) {
        if (this.shadowVisible != z) {
            this.shadowVisible = z;
            this.shadowView.animate().alpha(z ? 1.0f : 0.0f).setDuration(200).start();
        }
    }

    public void updateColors() {
        RecyclerView.Adapter adapter2 = this.listView.getAdapter();
        TrendingStickersAdapter trendingStickersAdapter = this.adapter;
        if (adapter2 == trendingStickersAdapter) {
            trendingStickersAdapter.updateColors(this.listView);
        } else {
            this.searchAdapter.updateColors(this.listView);
        }
    }

    public void getThemeDescriptions(List<ThemeDescription> list, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
        List<ThemeDescription> list2 = list;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = themeDescriptionDelegate;
        this.searchView.getThemeDescriptions(list2);
        this.adapter.getThemeDescriptions(list2, this.listView, themeDescriptionDelegate2);
        this.searchAdapter.getThemeDescriptions(list2, this.listView, themeDescriptionDelegate2);
        list2.add(new ThemeDescription(this.shadowView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        list2.add(new ThemeDescription(this.searchLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
    }

    public void glueToTop(boolean z) {
        this.gluedToTop = z;
        if (!z) {
            ValueAnimator valueAnimator = this.glueToTopAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.glueToTopAnimator.cancel();
                this.glueToTopAnimator = null;
            }
        } else if (getContentTopOffset() > 0 && this.glueToTopAnimator == null) {
            final int contentTopOffset = getContentTopOffset();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.glueToTopAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                int dy = 0;

                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int floatValue = (int) (((float) contentTopOffset) * ((Float) valueAnimator.getAnimatedValue()).floatValue());
                    boolean unused = TrendingStickersLayout.this.scrollFromAnimator = true;
                    TrendingStickersLayout.this.listView.scrollBy(0, floatValue - this.dy);
                    boolean unused2 = TrendingStickersLayout.this.scrollFromAnimator = false;
                    this.dy = floatValue;
                }
            });
            this.glueToTopAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    TrendingStickersLayout.this.glueToTopAnimator = null;
                }
            });
            this.glueToTopAnimator.setDuration(250);
            this.glueToTopAnimator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
            this.glueToTopAnimator.start();
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
        public final HashMap<TLRPC$StickerSetCovered, Integer> setsToPosition = new HashMap<>();
        /* access modifiers changed from: private */
        public int stickersPerRow = 5;
        /* access modifiers changed from: private */
        public int totalItems;

        public TrendingStickersAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return this.totalItems + 1;
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
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
                goto L_0x0064
            L_0x0013:
                org.telegram.ui.Cells.FeaturedStickerSetCell2 r4 = new org.telegram.ui.Cells.FeaturedStickerSetCell2
                android.content.Context r0 = r2.context
                r4.<init>(r0)
                org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda1 r0 = new org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda1
                r0.<init>(r2)
                r4.setAddOnClickListener(r0)
                org.telegram.ui.Components.BackupImageView r0 = r4.getImageView()
                r0.setLayerNum(r3)
                goto L_0x0063
            L_0x002a:
                org.telegram.ui.Cells.GraySectionCell r3 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r4 = r2.context
                r3.<init>(r4)
                goto L_0x0064
            L_0x0032:
                android.view.View r3 = new android.view.View
                android.content.Context r4 = r2.context
                r3.<init>(r4)
                goto L_0x0064
            L_0x003a:
                org.telegram.ui.Cells.FeaturedStickerSetInfoCell r3 = new org.telegram.ui.Cells.FeaturedStickerSetInfoCell
                android.content.Context r4 = r2.context
                r1 = 17
                r3.<init>(r4, r1, r0)
                org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda0 r4 = new org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda0
                r4.<init>(r2)
                r3.setAddOnClickListener(r4)
                goto L_0x0064
            L_0x004c:
                org.telegram.ui.Cells.EmptyCell r3 = new org.telegram.ui.Cells.EmptyCell
                android.content.Context r4 = r2.context
                r3.<init>(r4)
                goto L_0x0064
            L_0x0054:
                org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$1 r4 = new org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$1
                android.content.Context r0 = r2.context
                r1 = 0
                r4.<init>(r2, r0, r1)
                org.telegram.ui.Components.BackupImageView r0 = r4.getImageView()
                r0.setLayerNum(r3)
            L_0x0063:
                r3 = r4
            L_0x0064:
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r3)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TrendingStickersLayout.TrendingStickersAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$1(View view) {
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
                this.setsToPosition.clear();
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
                        this.setsToPosition.put(tLRPC$StickerSetCovered, Integer.valueOf(this.totalItems));
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
                ConnectionsManager.getInstance(TrendingStickersLayout.this.currentAccount).sendRequest(tLRPC$TL_messages_getOldFeaturedStickers, new TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda3(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadMoreStickerSets$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda2(this, tLRPC$TL_error, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadMoreStickerSets$2(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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

        public void updateColors(RecyclerListView recyclerListView) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = recyclerListView.getChildAt(i);
                if (childAt instanceof FeaturedStickerSetInfoCell) {
                    ((FeaturedStickerSetInfoCell) childAt).updateColors();
                } else if (childAt instanceof FeaturedStickerSetCell2) {
                    ((FeaturedStickerSetCell2) childAt).updateColors();
                }
            }
        }

        public void getThemeDescriptions(List<ThemeDescription> list, RecyclerListView recyclerListView, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
            FeaturedStickerSetInfoCell.createThemeDescriptions(list, recyclerListView, themeDescriptionDelegate);
            FeaturedStickerSetCell2.createThemeDescriptions(list, recyclerListView, themeDescriptionDelegate);
            GraySectionCell.createThemeDescriptions(list, recyclerListView);
        }
    }
}
