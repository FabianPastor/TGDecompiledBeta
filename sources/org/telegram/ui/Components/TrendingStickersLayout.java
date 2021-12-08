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
import android.view.ViewGroup;
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
import org.telegram.tgnet.TLRPC;
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
    public long hash;
    private float highlightProgress;
    /* access modifiers changed from: private */
    public boolean ignoreLayout;
    /* access modifiers changed from: private */
    public final LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets;
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
    public final TLRPC.StickerSetCovered[] primaryInstallingStickerSets;
    /* access modifiers changed from: private */
    public final LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets;
    /* access modifiers changed from: private */
    public final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public boolean scrollFromAnimator;
    private TLRPC.StickerSetCovered scrollToSet;
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

        public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSet, boolean primary) {
        }

        public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSet) {
        }

        public boolean onListViewInterceptTouchEvent(RecyclerListView listView, MotionEvent event) {
            return false;
        }

        public boolean onListViewTouchEvent(RecyclerListView listView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent event) {
            return false;
        }

        public String[] getLastSearchKeyboardLanguage() {
            return this.lastSearchKeyboardLanguage;
        }

        public void setLastSearchKeyboardLanguage(String[] language) {
            this.lastSearchKeyboardLanguage = language;
        }

        public boolean canSendSticker() {
            return false;
        }

        public void onStickerSelected(TLRPC.Document sticker, Object parent, boolean clearsInputField, boolean notify, int scheduleDate) {
        }

        public boolean canSchedule() {
            return false;
        }

        public boolean isInScheduleMode() {
            return false;
        }
    }

    public TrendingStickersLayout(Context context, Delegate delegate2) {
        this(context, delegate2, new TLRPC.StickerSetCovered[10], new LongSparseArray(), new LongSparseArray(), (TLRPC.StickerSetCovered) null, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TrendingStickersLayout(Context context, Delegate delegate2, TLRPC.StickerSetCovered[] primaryInstallingStickerSets2, LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets2, LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets2, TLRPC.StickerSetCovered scrollToSet2, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        final Delegate delegate3 = delegate2;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.highlightProgress = 1.0f;
        this.paint = new Paint();
        this.delegate = delegate3;
        this.primaryInstallingStickerSets = primaryInstallingStickerSets2;
        this.installingStickerSets = installingStickerSets2;
        this.removingStickerSets = removingStickerSets2;
        this.scrollToSet = scrollToSet2;
        this.resourcesProvider = resourcesProvider3;
        TrendingStickersAdapter trendingStickersAdapter = new TrendingStickersAdapter(context2);
        this.adapter = trendingStickersAdapter;
        StickersSearchAdapter stickersSearchAdapter = r7;
        StickersSearchAdapter stickersSearchAdapter2 = new StickersSearchAdapter(context, new StickersSearchAdapter.Delegate() {
            public void onSearchStart() {
                TrendingStickersLayout.this.searchView.getProgressDrawable().startAnimation();
            }

            public void onSearchStop() {
                TrendingStickersLayout.this.searchView.getProgressDrawable().stopAnimation();
            }

            public void setAdapterVisible(boolean visible) {
                boolean changed = false;
                if (visible && TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.searchAdapter) {
                    TrendingStickersLayout.this.listView.setAdapter(TrendingStickersLayout.this.searchAdapter);
                    changed = true;
                } else if (!visible && TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter) {
                    TrendingStickersLayout.this.listView.setAdapter(TrendingStickersLayout.this.adapter);
                    changed = true;
                }
                if (changed && TrendingStickersLayout.this.listView.getAdapter().getItemCount() > 0) {
                    TrendingStickersLayout.this.layoutManager.scrollToPositionWithOffset(0, (-TrendingStickersLayout.this.listView.getPaddingTop()) + AndroidUtilities.dp(58.0f) + TrendingStickersLayout.this.topOffset, false);
                }
            }

            public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSet, boolean primary) {
                delegate3.onStickerSetAdd(stickerSet, primary);
            }

            public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSet) {
                delegate3.onStickerSetRemove(stickerSet);
            }

            public int getStickersPerRow() {
                return TrendingStickersLayout.this.adapter.stickersPerRow;
            }

            public String[] getLastSearchKeyboardLanguage() {
                return delegate3.getLastSearchKeyboardLanguage();
            }

            public void setLastSearchKeyboardLanguage(String[] language) {
                delegate3.setLastSearchKeyboardLanguage(language);
            }
        }, primaryInstallingStickerSets2, installingStickerSets2, removingStickerSets2, resourcesProvider2);
        this.searchAdapter = stickersSearchAdapter;
        FrameLayout frameLayout = new FrameLayout(context2);
        this.searchLayout = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor("dialogBackground"));
        AnonymousClass2 r0 = new SearchField(context2, true, resourcesProvider3) {
            public void onTextChange(String text) {
                TrendingStickersLayout.this.searchAdapter.search(text);
            }
        };
        this.searchView = r0;
        r0.setHint(LocaleController.getString("SearchTrendingStickersHint", NUM));
        frameLayout.addView(r0, LayoutHelper.createFrame(-1, -1, 48));
        AnonymousClass3 r10 = new RecyclerListView(context2) {
            public boolean onInterceptTouchEvent(MotionEvent event) {
                return super.onInterceptTouchEvent(event) || delegate3.onListViewInterceptTouchEvent(this, event);
            }

            public boolean dispatchTouchEvent(MotionEvent ev) {
                boolean unused = TrendingStickersLayout.this.motionEventCatchedByListView = true;
                return super.dispatchTouchEvent(ev);
            }

            public boolean onTouchEvent(MotionEvent e) {
                if (TrendingStickersLayout.this.glueToTopAnimator != null) {
                    return false;
                }
                return super.onTouchEvent(e);
            }

            public void requestLayout() {
                if (!TrendingStickersLayout.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) (TrendingStickersLayout.this.topOffset + AndroidUtilities.dp(58.0f)));
            }
        };
        this.listView = r10;
        RecyclerListView.OnItemClickListener trendingOnItemClickListener = new TrendingStickersLayout$$ExternalSyntheticLambda1(this);
        r10.setOnTouchListener(new TrendingStickersLayout$$ExternalSyntheticLambda0(this, delegate3, trendingOnItemClickListener));
        r10.setOverScrollMode(2);
        r10.setClipToPadding(false);
        r10.setItemAnimator((RecyclerView.ItemAnimator) null);
        r10.setLayoutAnimation((LayoutAnimationController) null);
        AnonymousClass4 r02 = new FillLastGridLayoutManager(context, 5, AndroidUtilities.dp(58.0f), r10) {
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

            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                View minView;
                if (TrendingStickersLayout.this.scrollFromAnimator) {
                    return super.scrollVerticallyBy(dy, recycler, state);
                }
                if (TrendingStickersLayout.this.glueToTopAnimator != null) {
                    return 0;
                }
                if (TrendingStickersLayout.this.gluedToTop) {
                    int minPosition = 1;
                    int i = 0;
                    while (true) {
                        if (i >= getChildCount()) {
                            break;
                        }
                        int p = TrendingStickersLayout.this.listView.getChildAdapterPosition(getChildAt(i));
                        if (p < 1) {
                            minPosition = p;
                            break;
                        }
                        i++;
                    }
                    if (minPosition == 0 && (minView = TrendingStickersLayout.this.layoutManager.findViewByPosition(minPosition)) != null && minView.getTop() - dy > AndroidUtilities.dp(58.0f)) {
                        dy = minView.getTop() - AndroidUtilities.dp(58.0f);
                    }
                }
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        };
        this.layoutManager = r02;
        r10.setLayoutManager(r02);
        r02.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                if (TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter) {
                    return TrendingStickersLayout.this.searchAdapter.getSpanSize(position);
                }
                if ((TrendingStickersLayout.this.adapter.cache.get(position) instanceof Integer) || position >= TrendingStickersLayout.this.adapter.totalItems) {
                    return TrendingStickersLayout.this.adapter.stickersPerRow;
                }
                return 1;
            }
        });
        r10.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (TrendingStickersLayout.this.onScrollListener != null) {
                    TrendingStickersLayout.this.onScrollListener.onScrolled(TrendingStickersLayout.this.listView, dx, dy);
                }
                if (dy > 0 && TrendingStickersLayout.this.listView.getAdapter() == TrendingStickersLayout.this.adapter && TrendingStickersLayout.this.loaded && !TrendingStickersLayout.this.adapter.loadingMore && !TrendingStickersLayout.this.adapter.endReached) {
                    if (TrendingStickersLayout.this.layoutManager.findLastVisibleItemPosition() >= (TrendingStickersLayout.this.adapter.getItemCount() - ((TrendingStickersLayout.this.adapter.stickersPerRow + 1) * 10)) - 1) {
                        TrendingStickersLayout.this.adapter.loadMoreStickerSets();
                    }
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (TrendingStickersLayout.this.onScrollListener != null) {
                    TrendingStickersLayout.this.onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            }
        });
        r10.setAdapter(trendingStickersAdapter);
        r10.setOnItemClickListener(trendingOnItemClickListener);
        addView(r10, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(context2);
        this.shadowView = view;
        view.setBackgroundColor(getThemedColor("dialogShadowLine"));
        view.setAlpha(0.0f);
        FrameLayout.LayoutParams shadowViewParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight());
        shadowViewParams.topMargin = AndroidUtilities.dp(58.0f);
        addView(view, shadowViewParams);
        addView(frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        updateColors();
        NotificationCenter notificationCenter = NotificationCenter.getInstance(i);
        notificationCenter.addObserver(this, NotificationCenter.stickersDidLoad);
        notificationCenter.addObserver(this, NotificationCenter.featuredStickersDidLoad);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-TrendingStickersLayout  reason: not valid java name */
    public /* synthetic */ void m2697lambda$new$0$orgtelegramuiComponentsTrendingStickersLayout(View view, int position) {
        TLRPC.StickerSetCovered pack;
        RecyclerView.Adapter adapter2 = this.listView.getAdapter();
        StickersSearchAdapter stickersSearchAdapter = this.searchAdapter;
        if (adapter2 == stickersSearchAdapter) {
            pack = stickersSearchAdapter.getSetForPosition(position);
        } else if (position < this.adapter.totalItems) {
            pack = (TLRPC.StickerSetCovered) this.adapter.positionsToSets.get(position);
        } else {
            pack = null;
        }
        if (pack != null) {
            showStickerSet(pack.set);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-TrendingStickersLayout  reason: not valid java name */
    public /* synthetic */ boolean m2698lambda$new$1$orgtelegramuiComponentsTrendingStickersLayout(Delegate delegate2, RecyclerListView.OnItemClickListener trendingOnItemClickListener, View v, MotionEvent event) {
        return delegate2.onListViewTouchEvent(this.listView, trendingOnItemClickListener, event);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        Integer pos;
        super.onLayout(changed, l, t, r, b);
        if (!this.wasLayout) {
            this.wasLayout = true;
            this.adapter.refreshStickerSets();
            if (this.scrollToSet != null && (pos = (Integer) this.adapter.setsToPosition.get(this.scrollToSet)) != null) {
                this.layoutManager.scrollToPositionWithOffset(pos.intValue(), (-this.listView.getPaddingTop()) + AndroidUtilities.dp(58.0f));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        float f = this.highlightProgress;
        if (!(f == 0.0f || this.scrollToSet == null)) {
            float f2 = f - 0.0053333333f;
            this.highlightProgress = f2;
            if (f2 < 0.0f) {
                this.highlightProgress = 0.0f;
            } else {
                invalidate();
            }
            Integer pos = (Integer) this.adapter.setsToPosition.get(this.scrollToSet);
            if (pos != null) {
                View view1 = this.layoutManager.findViewByPosition(pos.intValue());
                int t = -1;
                int b = -1;
                if (view1 != null) {
                    t = (int) view1.getY();
                    b = ((int) view1.getY()) + view1.getMeasuredHeight();
                }
                View view2 = this.layoutManager.findViewByPosition(pos.intValue() + 1);
                if (view2 != null) {
                    if (view1 == null) {
                        t = (int) view2.getY();
                    }
                    b = ((int) view2.getY()) + view2.getMeasuredHeight();
                }
                if (!(view1 == null && view2 == null)) {
                    this.paint.setColor(Theme.getColor("featuredStickers_addButton"));
                    float f3 = this.highlightProgress;
                    this.paint.setAlpha((int) (25.5f * (f3 < 0.06f ? f3 / 0.06f : 1.0f)));
                    canvas.drawRect(0.0f, (float) t, (float) getMeasuredWidth(), (float) b, this.paint);
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateLastItemInAdapter();
        this.wasLayout = false;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.motionEventCatchedByListView = false;
        boolean result = super.dispatchTouchEvent(ev);
        if (!this.motionEventCatchedByListView) {
            MotionEvent e = MotionEvent.obtain(ev);
            this.listView.dispatchTouchEvent(e);
            e.recycle();
        }
        return result;
    }

    private void showStickerSet(TLRPC.StickerSet pack) {
        showStickerSet(pack, (TLRPC.InputStickerSet) null);
    }

    public void showStickerSet(TLRPC.StickerSet pack, TLRPC.InputStickerSet inputStickerSet) {
        if (pack != null) {
            inputStickerSet = new TLRPC.TL_inputStickerSetID();
            inputStickerSet.access_hash = pack.access_hash;
            inputStickerSet.id = pack.id;
        }
        if (inputStickerSet != null) {
            showStickerSet(inputStickerSet);
        }
    }

    private void showStickerSet(final TLRPC.InputStickerSet inputStickerSet) {
        StickersAlert.StickersAlertDelegate stickersAlertDelegate;
        if (this.delegate.canSendSticker()) {
            stickersAlertDelegate = new StickersAlert.StickersAlertDelegate() {
                public void onStickerSelected(TLRPC.Document sticker, String query, Object parent, MessageObject.SendAnimationData sendAnimationData, boolean clearsInputField, boolean notify, int scheduleDate) {
                    TrendingStickersLayout.this.delegate.onStickerSelected(sticker, parent, clearsInputField, notify, scheduleDate);
                }

                public boolean canSchedule() {
                    return TrendingStickersLayout.this.delegate.canSchedule();
                }

                public boolean isInScheduleMode() {
                    return TrendingStickersLayout.this.delegate.isInScheduleMode();
                }
            };
        } else {
            stickersAlertDelegate = null;
        }
        StickersAlert stickersAlert = new StickersAlert(getContext(), this.parentFragment, inputStickerSet, (TLRPC.TL_messages_stickerSet) null, stickersAlertDelegate, this.resourcesProvider);
        stickersAlert.setShowTooltipWhenToggle(false);
        stickersAlert.setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate() {
            public void onStickerSetInstalled() {
                if (TrendingStickersLayout.this.listView.getAdapter() == TrendingStickersLayout.this.adapter) {
                    for (int i = 0; i < TrendingStickersLayout.this.adapter.sets.size(); i++) {
                        TLRPC.StickerSetCovered setCovered = (TLRPC.StickerSetCovered) TrendingStickersLayout.this.adapter.sets.get(i);
                        if (setCovered.set.id == inputStickerSet.id) {
                            TrendingStickersLayout.this.adapter.installStickerSet(setCovered, (View) null);
                            return;
                        }
                    }
                    return;
                }
                TrendingStickersLayout.this.searchAdapter.installStickerSet(inputStickerSet);
            }

            public void onStickerSetUninstalled() {
            }
        });
        this.parentFragment.showDialog(stickersAlert);
    }

    public void recycle() {
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        notificationCenter.removeObserver(this, NotificationCenter.stickersDidLoad);
        notificationCenter.removeObserver(this, NotificationCenter.featuredStickersDidLoad);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (args[0].intValue() != 0) {
                return;
            }
            if (this.loaded) {
                updateVisibleTrendingSets();
            } else {
                this.adapter.refreshStickerSets();
            }
        } else if (id == NotificationCenter.featuredStickersDidLoad) {
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

    public void setParentFragment(BaseFragment parentFragment2) {
        this.parentFragment = parentFragment2;
    }

    public void setContentViewPaddingTop(int paddingTop) {
        int paddingTop2 = paddingTop + AndroidUtilities.dp(58.0f);
        if (this.listView.getPaddingTop() != paddingTop2) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, paddingTop2, 0, 0);
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
        View child = this.listView.getChildAt(0);
        for (int i = 1; i < this.listView.getChildCount(); i++) {
            View view = this.listView.getChildAt(i);
            if (view.getTop() < child.getTop()) {
                child = view;
            }
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(58.0f);
        int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        setShadowVisible(top < 0);
        if (this.topOffset == newOffset) {
            return false;
        }
        this.topOffset = newOffset;
        this.listView.setTopGlowOffset(AndroidUtilities.dp(58.0f) + newOffset);
        this.searchLayout.setTranslationY((float) this.topOffset);
        this.shadowView.setTranslationY((float) this.topOffset);
        return true;
    }

    private void updateVisibleTrendingSets() {
        RecyclerView.Adapter listAdapter = this.listView.getAdapter();
        if (listAdapter != null) {
            listAdapter.notifyItemRangeChanged(0, listAdapter.getItemCount(), 0);
        }
    }

    private void setShadowVisible(boolean visible) {
        if (this.shadowVisible != visible) {
            this.shadowVisible = visible;
            this.shadowView.animate().alpha(visible ? 1.0f : 0.0f).setDuration(200).start();
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

    public void getThemeDescriptions(List<ThemeDescription> descriptions, ThemeDescription.ThemeDescriptionDelegate delegate2) {
        List<ThemeDescription> list = descriptions;
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = delegate2;
        this.searchView.getThemeDescriptions(list);
        this.adapter.getThemeDescriptions(list, this.listView, themeDescriptionDelegate);
        this.searchAdapter.getThemeDescriptions(list, this.listView, themeDescriptionDelegate);
        list.add(new ThemeDescription(this.shadowView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        list.add(new ThemeDescription(this.searchLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
    }

    public void glueToTop(boolean glue) {
        this.gluedToTop = glue;
        if (!glue) {
            ValueAnimator valueAnimator = this.glueToTopAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.glueToTopAnimator.cancel();
                this.glueToTopAnimator = null;
            }
        } else if (getContentTopOffset() > 0 && this.glueToTopAnimator == null) {
            final int startFrom = getContentTopOffset();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.glueToTopAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                int dy = 0;

                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int currentDy = (int) (((float) startFrom) * ((Float) valueAnimator.getAnimatedValue()).floatValue());
                    boolean unused = TrendingStickersLayout.this.scrollFromAnimator = true;
                    TrendingStickersLayout.this.listView.scrollBy(0, currentDy - this.dy);
                    boolean unused2 = TrendingStickersLayout.this.scrollFromAnimator = false;
                    this.dy = currentDy;
                }
            });
            this.glueToTopAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    TrendingStickersLayout.this.glueToTopAnimator = null;
                }
            });
            this.glueToTopAnimator.setDuration(250);
            this.glueToTopAnimator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
            this.glueToTopAnimator.start();
        }
    }

    private class TrendingStickersAdapter extends RecyclerListView.SelectionAdapter {
        private static final int ITEM_SECTION = -1;
        public static final int PAYLOAD_ANIMATED = 0;
        /* access modifiers changed from: private */
        public final SparseArray<Object> cache = new SparseArray<>();
        private final Context context;
        /* access modifiers changed from: private */
        public boolean endReached;
        /* access modifiers changed from: private */
        public boolean loadingMore;
        private final ArrayList<TLRPC.StickerSetCovered> otherPacks = new ArrayList<>();
        /* access modifiers changed from: private */
        public final SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
        /* access modifiers changed from: private */
        public final ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList<>();
        /* access modifiers changed from: private */
        public final HashMap<TLRPC.StickerSetCovered, Integer> setsToPosition = new HashMap<>();
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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 5;
        }

        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return 3;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            if (object.equals(-1)) {
                return 4;
            }
            return 2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    AnonymousClass1 r2 = new StickerEmojiCell(this.context, false) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    r2.getImageView().setLayerNum(3);
                    view = r2;
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new FeaturedStickerSetInfoCell(this.context, 17, true, true, TrendingStickersLayout.this.resourcesProvider);
                    ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda0(this));
                    break;
                case 3:
                    view = new View(this.context);
                    break;
                case 4:
                    view = new GraySectionCell(this.context, TrendingStickersLayout.this.resourcesProvider);
                    break;
                case 5:
                    FeaturedStickerSetCell2 stickerSetCell = new FeaturedStickerSetCell2(this.context, TrendingStickersLayout.this.resourcesProvider);
                    stickerSetCell.setAddOnClickListener(new TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda1(this));
                    stickerSetCell.getImageView().setLayerNum(3);
                    view = stickerSetCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-TrendingStickersLayout$TrendingStickersAdapter  reason: not valid java name */
        public /* synthetic */ void m2701xd64a6007(View v) {
            FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) v.getParent();
            TLRPC.StickerSetCovered pack = cell.getStickerSet();
            if (TrendingStickersLayout.this.installingStickerSets.indexOfKey(pack.set.id) < 0 && TrendingStickersLayout.this.removingStickerSets.indexOfKey(pack.set.id) < 0) {
                if (cell.isInstalled()) {
                    TrendingStickersLayout.this.removingStickerSets.put(pack.set.id, pack);
                    TrendingStickersLayout.this.delegate.onStickerSetRemove(pack);
                    return;
                }
                installStickerSet(pack, cell);
            }
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-Components-TrendingStickersLayout$TrendingStickersAdapter  reason: not valid java name */
        public /* synthetic */ void m2702x9var_ac8(View v) {
            FeaturedStickerSetCell2 cell = (FeaturedStickerSetCell2) v.getParent();
            TLRPC.StickerSetCovered pack = cell.getStickerSet();
            if (TrendingStickersLayout.this.installingStickerSets.indexOfKey(pack.set.id) < 0 && TrendingStickersLayout.this.removingStickerSets.indexOfKey(pack.set.id) < 0) {
                if (cell.isInstalled()) {
                    TrendingStickersLayout.this.removingStickerSets.put(pack.set.id, pack);
                    TrendingStickersLayout.this.delegate.onStickerSetRemove(pack);
                    return;
                }
                installStickerSet(pack, cell);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ((StickerEmojiCell) holder.itemView).setSticker((TLRPC.Document) this.cache.get(position), this.positionsToSets.get(position), false);
                    return;
                case 1:
                    ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                    return;
                case 2:
                case 5:
                    bindStickerSetCell(holder.itemView, position, false);
                    return;
                case 4:
                    ((GraySectionCell) holder.itemView).setText(LocaleController.getString("OtherStickers", NUM));
                    return;
                default:
                    return;
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
            if (payloads.contains(0)) {
                int type = holder.getItemViewType();
                if (type == 2 || type == 5) {
                    bindStickerSetCell(holder.itemView, position, true);
                    return;
                }
                return;
            }
            super.onBindViewHolder(holder, position, payloads);
        }

        private void bindStickerSetCell(View view, int position, boolean animated) {
            TLRPC.StickerSetCovered stickerSetCovered;
            boolean forceInstalled;
            boolean installing;
            int i = position;
            MediaDataController mediaDataController = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount);
            boolean unread = false;
            boolean z = true;
            if (i < this.totalItems) {
                stickerSetCovered = this.sets.get(((Integer) this.cache.get(i)).intValue());
                ArrayList<Long> unreadStickers = mediaDataController.getUnreadStickerSets();
                unread = unreadStickers != null && unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
                if (unread) {
                    mediaDataController.markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                }
            } else {
                stickerSetCovered = this.sets.get(((Integer) this.cache.get(i)).intValue());
            }
            mediaDataController.preloadStickerSetThumb(stickerSetCovered);
            int i2 = 0;
            while (true) {
                if (i2 >= TrendingStickersLayout.this.primaryInstallingStickerSets.length) {
                    forceInstalled = false;
                    break;
                }
                if (TrendingStickersLayout.this.primaryInstallingStickerSets[i2] != null) {
                    TLRPC.TL_messages_stickerSet s = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount).getStickerSetById(TrendingStickersLayout.this.primaryInstallingStickerSets[i2].set.id);
                    if (s != null && !s.set.archived) {
                        TrendingStickersLayout.this.primaryInstallingStickerSets[i2] = null;
                    } else if (TrendingStickersLayout.this.primaryInstallingStickerSets[i2].set.id == stickerSetCovered.set.id) {
                        forceInstalled = true;
                        break;
                    }
                }
                i2++;
            }
            boolean isSetInstalled = mediaDataController.isStickerPackInstalled(stickerSetCovered.set.id);
            boolean installing2 = TrendingStickersLayout.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
            boolean removing = TrendingStickersLayout.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
            if (!installing2 || !isSetInstalled) {
                if (removing && !isSetInstalled) {
                    TrendingStickersLayout.this.removingStickerSets.remove(stickerSetCovered.set.id);
                }
                installing = installing2;
            } else {
                TrendingStickersLayout.this.installingStickerSets.remove(stickerSetCovered.set.id);
                installing = false;
            }
            FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) view;
            FeaturedStickerSetInfoCell cell2 = cell;
            cell.setStickerSet(stickerSetCovered, unread, animated, 0, 0, forceInstalled);
            cell2.setAddDrawProgress(!forceInstalled && installing, animated);
            if (i <= 0 || (this.cache.get(i - 1) != null && this.cache.get(i - 1).equals(-1))) {
                z = false;
            }
            cell2.setNeedDivider(z);
        }

        /* access modifiers changed from: private */
        public void installStickerSet(TLRPC.StickerSetCovered pack, View view) {
            int i = 0;
            while (true) {
                if (i >= TrendingStickersLayout.this.primaryInstallingStickerSets.length) {
                    break;
                }
                if (TrendingStickersLayout.this.primaryInstallingStickerSets[i] != null) {
                    TLRPC.TL_messages_stickerSet s = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount).getStickerSetById(TrendingStickersLayout.this.primaryInstallingStickerSets[i].set.id);
                    if (s != null && !s.set.archived) {
                        TrendingStickersLayout.this.primaryInstallingStickerSets[i] = null;
                        break;
                    } else if (TrendingStickersLayout.this.primaryInstallingStickerSets[i].set.id == pack.set.id) {
                        return;
                    }
                }
                i++;
            }
            boolean primary = false;
            int i2 = 0;
            while (true) {
                if (i2 >= TrendingStickersLayout.this.primaryInstallingStickerSets.length) {
                    break;
                } else if (TrendingStickersLayout.this.primaryInstallingStickerSets[i2] == null) {
                    TrendingStickersLayout.this.primaryInstallingStickerSets[i2] = pack;
                    primary = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (!primary && view != null) {
                if (view instanceof FeaturedStickerSetCell2) {
                    ((FeaturedStickerSetCell2) view).setDrawProgress(true, true);
                } else if (view instanceof FeaturedStickerSetInfoCell) {
                    ((FeaturedStickerSetInfoCell) view).setAddDrawProgress(true, true);
                }
            }
            TrendingStickersLayout.this.installingStickerSets.put(pack.set.id, pack);
            if (view != null) {
                TrendingStickersLayout.this.delegate.onStickerSetAdd(pack, primary);
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

        public void refreshStickerSets() {
            int count;
            int i;
            int width = TrendingStickersLayout.this.getMeasuredWidth();
            if (width != 0) {
                this.stickersPerRow = Math.max(5, width / AndroidUtilities.dp(72.0f));
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
                int count2 = 0;
                MediaDataController mediaDataController = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount);
                ArrayList<TLRPC.StickerSetCovered> packs = new ArrayList<>(mediaDataController.getFeaturedStickerSets());
                int otherStickersSectionPosition = packs.size();
                packs.addAll(this.otherPacks);
                for (int a = 0; a < packs.size(); a++) {
                    TLRPC.StickerSetCovered pack = packs.get(a);
                    if (!pack.covers.isEmpty() || pack.cover != null) {
                        if (a == otherStickersSectionPosition) {
                            SparseArray<Object> sparseArray = this.cache;
                            int i2 = this.totalItems;
                            this.totalItems = i2 + 1;
                            sparseArray.put(i2, -1);
                        }
                        this.sets.add(pack);
                        this.positionsToSets.put(this.totalItems, pack);
                        this.setsToPosition.put(pack, Integer.valueOf(this.totalItems));
                        SparseArray<Object> sparseArray2 = this.cache;
                        int i3 = this.totalItems;
                        this.totalItems = i3 + 1;
                        int num = count2 + 1;
                        sparseArray2.put(i3, Integer.valueOf(count2));
                        if (!pack.covers.isEmpty()) {
                            count = (int) Math.ceil((double) (((float) pack.covers.size()) / ((float) this.stickersPerRow)));
                            for (int b = 0; b < pack.covers.size(); b++) {
                                this.cache.put(this.totalItems + b, pack.covers.get(b));
                            }
                        } else {
                            count = 1;
                            this.cache.put(this.totalItems, pack.cover);
                        }
                        int b2 = 0;
                        while (true) {
                            i = this.stickersPerRow;
                            if (b2 >= count * i) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + b2, pack);
                            b2++;
                        }
                        this.totalItems += i * count;
                        count2 = num;
                    }
                }
                if (this.totalItems != 0) {
                    boolean unused2 = TrendingStickersLayout.this.loaded = true;
                    long unused3 = TrendingStickersLayout.this.hash = mediaDataController.getFeaturesStickersHashWithoutUnread();
                }
                notifyDataSetChanged();
            }
        }

        public void loadMoreStickerSets() {
            if (TrendingStickersLayout.this.loaded && !this.loadingMore && !this.endReached) {
                this.loadingMore = true;
                TLRPC.TL_messages_getOldFeaturedStickers req = new TLRPC.TL_messages_getOldFeaturedStickers();
                req.offset = this.otherPacks.size();
                req.limit = 40;
                ConnectionsManager.getInstance(TrendingStickersLayout.this.currentAccount).sendRequest(req, new TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda3(this));
            }
        }

        /* renamed from: lambda$loadMoreStickerSets$3$org-telegram-ui-Components-TrendingStickersLayout$TrendingStickersAdapter  reason: not valid java name */
        public /* synthetic */ void m2700x81515877(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda2(this, error, response));
        }

        /* renamed from: lambda$loadMoreStickerSets$2$org-telegram-ui-Components-TrendingStickersLayout$TrendingStickersAdapter  reason: not valid java name */
        public /* synthetic */ void m2699x4da32db6(TLRPC.TL_error error, TLObject response) {
            int count;
            int i;
            this.loadingMore = false;
            if (error != null || !(response instanceof TLRPC.TL_messages_featuredStickers)) {
                this.endReached = true;
                return;
            }
            List<TLRPC.StickerSetCovered> packs = ((TLRPC.TL_messages_featuredStickers) response).sets;
            if (packs.size() < 40) {
                this.endReached = true;
            }
            if (!packs.isEmpty()) {
                if (this.otherPacks.isEmpty()) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i2 = this.totalItems;
                    this.totalItems = i2 + 1;
                    sparseArray.put(i2, -1);
                }
                this.otherPacks.addAll(packs);
                int count2 = this.sets.size();
                for (int a = 0; a < packs.size(); a++) {
                    TLRPC.StickerSetCovered pack = packs.get(a);
                    if (!pack.covers.isEmpty() || pack.cover != null) {
                        this.sets.add(pack);
                        this.positionsToSets.put(this.totalItems, pack);
                        SparseArray<Object> sparseArray2 = this.cache;
                        int i3 = this.totalItems;
                        this.totalItems = i3 + 1;
                        int num = count2 + 1;
                        sparseArray2.put(i3, Integer.valueOf(count2));
                        if (!pack.covers.isEmpty()) {
                            count = (int) Math.ceil((double) (((float) pack.covers.size()) / ((float) this.stickersPerRow)));
                            for (int b = 0; b < pack.covers.size(); b++) {
                                this.cache.put(this.totalItems + b, pack.covers.get(b));
                            }
                        } else {
                            count = 1;
                            this.cache.put(this.totalItems, pack.cover);
                        }
                        int b2 = 0;
                        while (true) {
                            i = this.stickersPerRow;
                            if (b2 >= count * i) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + b2, pack);
                            b2++;
                        }
                        this.totalItems += i * count;
                        count2 = num;
                    }
                }
                notifyDataSetChanged();
            }
        }

        public void updateColors(RecyclerListView listView) {
            int size = listView.getChildCount();
            for (int i = 0; i < size; i++) {
                View child = listView.getChildAt(i);
                if (child instanceof FeaturedStickerSetInfoCell) {
                    ((FeaturedStickerSetInfoCell) child).updateColors();
                } else if (child instanceof FeaturedStickerSetCell2) {
                    ((FeaturedStickerSetCell2) child).updateColors();
                }
            }
        }

        public void getThemeDescriptions(List<ThemeDescription> descriptions, RecyclerListView listView, ThemeDescription.ThemeDescriptionDelegate delegate) {
            FeaturedStickerSetInfoCell.createThemeDescriptions(descriptions, listView, delegate);
            FeaturedStickerSetCell2.createThemeDescriptions(descriptions, listView, delegate);
            GraySectionCell.createThemeDescriptions(descriptions, listView);
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
