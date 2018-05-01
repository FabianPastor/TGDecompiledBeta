package org.telegram.messenger.support.widget;

import android.content.Context;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.v4.os.TraceCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.OverScroller;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.tgnet.ConnectionsManager;

public class RecyclerView extends ViewGroup implements NestedScrollingChild2 {
    static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC = (VERSION.SDK_INT < 23);
    private static final boolean ALLOW_THREAD_GAP_WORK = (VERSION.SDK_INT < 21);
    private static final int[] CLIP_TO_PADDING_ATTR = new int[]{16842987};
    static final boolean DEBUG = false;
    static final int DEFAULT_ORIENTATION = 1;
    static final boolean DISPATCH_TEMP_DETACH = false;
    private static final boolean FORCE_ABS_FOCUS_SEARCH_DIRECTION = (VERSION.SDK_INT > 15);
    static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
    static final long FOREVER_NS = Long.MAX_VALUE;
    public static final int HORIZONTAL = 0;
    private static final boolean IGNORE_DETACHED_FOCUSED_CHILD = (VERSION.SDK_INT > 15);
    private static final int INVALID_POINTER = -1;
    public static final int INVALID_TYPE = -1;
    private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[]{Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE};
    static final int MAX_SCROLL_DURATION = 2000;
    private static final int[] NESTED_SCROLLING_ATTRS = new int[]{16843830};
    public static final long NO_ID = -1;
    public static final int NO_POSITION = -1;
    static final boolean POST_UPDATES_ON_ANIMATION = (VERSION.SDK_INT < 16);
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    static final String TAG = "RecyclerView";
    public static final int TOUCH_SLOP_DEFAULT = 0;
    public static final int TOUCH_SLOP_PAGING = 1;
    static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
    static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
    private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
    static final String TRACE_NESTED_PREFETCH_TAG = "RV Nested Prefetch";
    private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
    private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
    static final String TRACE_PREFETCH_TAG = "RV Prefetch";
    static final String TRACE_SCROLL_TAG = "RV Scroll";
    static final boolean VERBOSE_TRACING = false;
    public static final int VERTICAL = 1;
    static final Interpolator sQuinticInterpolator = new C06613();
    private int bottomGlowOffset;
    private int glowColor;
    RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
    private final AccessibilityManager mAccessibilityManager;
    private OnItemTouchListener mActiveOnItemTouchListener;
    Adapter mAdapter;
    AdapterHelper mAdapterHelper;
    boolean mAdapterUpdateDuringMeasure;
    private EdgeEffect mBottomGlow;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback;
    ChildHelper mChildHelper;
    boolean mClipToPadding;
    boolean mDataSetHasChangedAfterLayout;
    boolean mDispatchItemsChangedEvent;
    private int mDispatchScrollCounter;
    private int mEatenAccessibilityChangeFlags;
    private EdgeEffectFactory mEdgeEffectFactory;
    boolean mEnableFastScroller;
    boolean mFirstLayoutComplete;
    GapWorker mGapWorker;
    boolean mHasFixedSize;
    private boolean mIgnoreMotionEventTillDown;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private int mInterceptRequestLayoutDepth;
    boolean mIsAttached;
    ItemAnimator mItemAnimator;
    private ItemAnimatorListener mItemAnimatorListener;
    private Runnable mItemAnimatorRunner;
    final ArrayList<ItemDecoration> mItemDecorations;
    boolean mItemsAddedOrRemoved;
    boolean mItemsChanged;
    private int mLastTouchX;
    private int mLastTouchY;
    LayoutManager mLayout;
    boolean mLayoutFrozen;
    private int mLayoutOrScrollCounter;
    boolean mLayoutWasDefered;
    private EdgeEffect mLeftGlow;
    private final int mMaxFlingVelocity;
    private final int mMinFlingVelocity;
    private final int[] mMinMaxLayoutPositions;
    private final int[] mNestedOffsets;
    private final RecyclerViewDataObserver mObserver;
    private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
    private OnFlingListener mOnFlingListener;
    private final ArrayList<OnItemTouchListener> mOnItemTouchListeners;
    final List<ViewHolder> mPendingAccessibilityImportanceChange;
    private SavedState mPendingSavedState;
    boolean mPostedAnimatorRunner;
    LayoutPrefetchRegistryImpl mPrefetchRegistry;
    private boolean mPreserveFocusAfterLayout;
    final Recycler mRecycler;
    RecyclerListener mRecyclerListener;
    private EdgeEffect mRightGlow;
    private float mScaledHorizontalScrollFactor;
    private float mScaledVerticalScrollFactor;
    private final int[] mScrollConsumed;
    private OnScrollListener mScrollListener;
    private List<OnScrollListener> mScrollListeners;
    private final int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    private NestedScrollingChildHelper mScrollingChildHelper;
    final State mState;
    final Rect mTempRect;
    private final Rect mTempRect2;
    final RectF mTempRectF;
    private EdgeEffect mTopGlow;
    private int mTouchSlop;
    final Runnable mUpdateChildViewsRunnable;
    private VelocityTracker mVelocityTracker;
    final ViewFlinger mViewFlinger;
    private final ProcessCallback mViewInfoProcessCallback;
    final ViewInfoStore mViewInfoStore;
    private int topGlowOffset;

    /* renamed from: org.telegram.messenger.support.widget.RecyclerView$1 */
    class C06591 implements Runnable {
        C06591() {
        }

        public void run() {
            if (RecyclerView.this.mFirstLayoutComplete) {
                if (!RecyclerView.this.isLayoutRequested()) {
                    if (!RecyclerView.this.mIsAttached) {
                        RecyclerView.this.requestLayout();
                    } else if (RecyclerView.this.mLayoutFrozen) {
                        RecyclerView.this.mLayoutWasDefered = true;
                    } else {
                        RecyclerView.this.consumePendingUpdateOperations();
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.support.widget.RecyclerView$2 */
    class C06602 implements Runnable {
        C06602() {
        }

        public void run() {
            if (RecyclerView.this.mItemAnimator != null) {
                RecyclerView.this.mItemAnimator.runPendingAnimations();
            }
            RecyclerView.this.mPostedAnimatorRunner = false;
        }
    }

    /* renamed from: org.telegram.messenger.support.widget.RecyclerView$3 */
    static class C06613 implements Interpolator {
        public float getInterpolation(float f) {
            f -= 1.0f;
            return ((((f * f) * f) * f) * f) + 1.0f;
        }

        C06613() {
        }
    }

    public static abstract class Adapter<VH extends ViewHolder> {
        private boolean mHasStableIds = false;
        private final AdapterDataObservable mObservable = new AdapterDataObservable();

        public abstract int getItemCount();

        public long getItemId(int i) {
            return -1;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        }

        public abstract void onBindViewHolder(VH vh, int i);

        public abstract VH onCreateViewHolder(ViewGroup viewGroup, int i);

        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        }

        public boolean onFailedToRecycleView(VH vh) {
            return false;
        }

        public void onViewAttachedToWindow(VH vh) {
        }

        public void onViewDetachedFromWindow(VH vh) {
        }

        public void onViewRecycled(VH vh) {
        }

        public void onBindViewHolder(VH vh, int i, List<Object> list) {
            onBindViewHolder(vh, i);
        }

        public final VH createViewHolder(ViewGroup viewGroup, int i) {
            try {
                TraceCompat.beginSection(RecyclerView.TRACE_CREATE_VIEW_TAG);
                viewGroup = onCreateViewHolder(viewGroup, i);
                if (viewGroup.itemView.getParent() != null) {
                    throw new IllegalStateException("ViewHolder views must not be attached when created. Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)");
                }
                viewGroup.mItemViewType = i;
                return viewGroup;
            } finally {
                TraceCompat.endSection();
            }
        }

        public final void bindViewHolder(VH vh, int i) {
            vh.mPosition = i;
            if (hasStableIds()) {
                vh.mItemId = getItemId(i);
            }
            vh.setFlags(1, 519);
            TraceCompat.beginSection(RecyclerView.TRACE_BIND_VIEW_TAG);
            onBindViewHolder(vh, i, vh.getUnmodifiedPayloads());
            vh.clearPayload();
            vh = vh.itemView.getLayoutParams();
            if ((vh instanceof LayoutParams) != 0) {
                ((LayoutParams) vh).mInsetsDirty = true;
            }
            TraceCompat.endSection();
        }

        public void setHasStableIds(boolean z) {
            if (hasObservers()) {
                throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
            }
            this.mHasStableIds = z;
        }

        public final boolean hasStableIds() {
            return this.mHasStableIds;
        }

        public final boolean hasObservers() {
            return this.mObservable.hasObservers();
        }

        public void registerAdapterDataObserver(AdapterDataObserver adapterDataObserver) {
            this.mObservable.registerObserver(adapterDataObserver);
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver adapterDataObserver) {
            this.mObservable.unregisterObserver(adapterDataObserver);
        }

        public void notifyDataSetChanged() {
            this.mObservable.notifyChanged();
        }

        public void notifyItemChanged(int i) {
            this.mObservable.notifyItemRangeChanged(i, 1);
        }

        public void notifyItemChanged(int i, Object obj) {
            this.mObservable.notifyItemRangeChanged(i, 1, obj);
        }

        public void notifyItemRangeChanged(int i, int i2) {
            this.mObservable.notifyItemRangeChanged(i, i2);
        }

        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            this.mObservable.notifyItemRangeChanged(i, i2, obj);
        }

        public void notifyItemInserted(int i) {
            this.mObservable.notifyItemRangeInserted(i, 1);
        }

        public void notifyItemMoved(int i, int i2) {
            this.mObservable.notifyItemMoved(i, i2);
        }

        public void notifyItemRangeInserted(int i, int i2) {
            this.mObservable.notifyItemRangeInserted(i, i2);
        }

        public void notifyItemRemoved(int i) {
            this.mObservable.notifyItemRangeRemoved(i, 1);
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            this.mObservable.notifyItemRangeRemoved(i, i2);
        }
    }

    static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        AdapterDataObservable() {
        }

        public boolean hasObservers() {
            return this.mObservers.isEmpty() ^ 1;
        }

        public void notifyChanged() {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((AdapterDataObserver) this.mObservers.get(size)).onChanged();
            }
        }

        public void notifyItemRangeChanged(int i, int i2) {
            notifyItemRangeChanged(i, i2, null);
        }

        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeChanged(i, i2, obj);
            }
        }

        public void notifyItemRangeInserted(int i, int i2) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeInserted(i, i2);
            }
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeRemoved(i, i2);
            }
        }

        public void notifyItemMoved(int i, int i2) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeMoved(i, i2, 1);
            }
        }
    }

    public static abstract class AdapterDataObserver {
        public void onChanged() {
        }

        public void onItemRangeChanged(int i, int i2) {
        }

        public void onItemRangeInserted(int i, int i2) {
        }

        public void onItemRangeMoved(int i, int i2, int i3) {
        }

        public void onItemRangeRemoved(int i, int i2) {
        }

        public void onItemRangeChanged(int i, int i2, Object obj) {
            onItemRangeChanged(i, i2);
        }
    }

    public interface ChildDrawingOrderCallback {
        int onGetChildDrawingOrder(int i, int i2);
    }

    public static class EdgeEffectFactory {
        public static final int DIRECTION_BOTTOM = 3;
        public static final int DIRECTION_LEFT = 0;
        public static final int DIRECTION_RIGHT = 2;
        public static final int DIRECTION_TOP = 1;

        @Retention(RetentionPolicy.SOURCE)
        public @interface EdgeDirection {
        }

        protected EdgeEffect createEdgeEffect(RecyclerView recyclerView, int i) {
            return new EdgeEffect(recyclerView.getContext());
        }
    }

    public static abstract class ItemAnimator {
        public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        public static final int FLAG_CHANGED = 2;
        public static final int FLAG_INVALIDATED = 4;
        public static final int FLAG_MOVED = 2048;
        public static final int FLAG_REMOVED = 8;
        private long mAddDuration = 120;
        private long mChangeDuration = 250;
        private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners = new ArrayList();
        private ItemAnimatorListener mListener = null;
        private long mMoveDuration = 250;
        private long mRemoveDuration = 120;

        @Retention(RetentionPolicy.SOURCE)
        public @interface AdapterChanges {
        }

        public interface ItemAnimatorFinishedListener {
            void onAnimationsFinished();
        }

        interface ItemAnimatorListener {
            void onAnimationFinished(ViewHolder viewHolder);
        }

        public static class ItemHolderInfo {
            public int bottom;
            public int changeFlags;
            public int left;
            public int right;
            public int top;

            public ItemHolderInfo setFrom(ViewHolder viewHolder) {
                return setFrom(viewHolder, 0);
            }

            public ItemHolderInfo setFrom(ViewHolder viewHolder, int i) {
                viewHolder = viewHolder.itemView;
                this.left = viewHolder.getLeft();
                this.top = viewHolder.getTop();
                this.right = viewHolder.getRight();
                this.bottom = viewHolder.getBottom();
                return this;
            }
        }

        public abstract boolean animateAppearance(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        public abstract boolean animateChange(ViewHolder viewHolder, ViewHolder viewHolder2, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        public abstract boolean animateDisappearance(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        public abstract boolean animatePersistence(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2);

        public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder) {
            return true;
        }

        public abstract void endAnimation(ViewHolder viewHolder);

        public abstract void endAnimations();

        public abstract boolean isRunning();

        public void onAnimationFinished(ViewHolder viewHolder) {
        }

        public void onAnimationStarted(ViewHolder viewHolder) {
        }

        public abstract void runPendingAnimations();

        public long getMoveDuration() {
            return this.mMoveDuration;
        }

        public void setMoveDuration(long j) {
            this.mMoveDuration = j;
        }

        public long getAddDuration() {
            return this.mAddDuration;
        }

        public void setAddDuration(long j) {
            this.mAddDuration = j;
        }

        public long getRemoveDuration() {
            return this.mRemoveDuration;
        }

        public void setRemoveDuration(long j) {
            this.mRemoveDuration = j;
        }

        public long getChangeDuration() {
            return this.mChangeDuration;
        }

        public void setChangeDuration(long j) {
            this.mChangeDuration = j;
        }

        void setListener(ItemAnimatorListener itemAnimatorListener) {
            this.mListener = itemAnimatorListener;
        }

        public ItemHolderInfo recordPreLayoutInformation(State state, ViewHolder viewHolder, int i, List<Object> list) {
            return obtainHolderInfo().setFrom(viewHolder);
        }

        public ItemHolderInfo recordPostLayoutInformation(State state, ViewHolder viewHolder) {
            return obtainHolderInfo().setFrom(viewHolder);
        }

        static int buildAdapterChangeFlagsForAnimations(ViewHolder viewHolder) {
            int access$1600 = viewHolder.mFlags & 14;
            if (viewHolder.isInvalid()) {
                return 4;
            }
            if ((access$1600 & 4) == 0) {
                int oldPosition = viewHolder.getOldPosition();
                viewHolder = viewHolder.getAdapterPosition();
                if (!(oldPosition == -1 || viewHolder == -1 || oldPosition == viewHolder)) {
                    access$1600 |= 2048;
                }
            }
            return access$1600;
        }

        public final void dispatchAnimationFinished(ViewHolder viewHolder) {
            onAnimationFinished(viewHolder);
            if (this.mListener != null) {
                this.mListener.onAnimationFinished(viewHolder);
            }
        }

        public final void dispatchAnimationStarted(ViewHolder viewHolder) {
            onAnimationStarted(viewHolder);
        }

        public final boolean isRunning(ItemAnimatorFinishedListener itemAnimatorFinishedListener) {
            boolean isRunning = isRunning();
            if (itemAnimatorFinishedListener != null) {
                if (isRunning) {
                    this.mFinishedListeners.add(itemAnimatorFinishedListener);
                } else {
                    itemAnimatorFinishedListener.onAnimationsFinished();
                }
            }
            return isRunning;
        }

        public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder, List<Object> list) {
            return canReuseUpdatedViewHolder(viewHolder);
        }

        public final void dispatchAnimationsFinished() {
            int size = this.mFinishedListeners.size();
            for (int i = 0; i < size; i++) {
                ((ItemAnimatorFinishedListener) this.mFinishedListeners.get(i)).onAnimationsFinished();
            }
            this.mFinishedListeners.clear();
        }

        public ItemHolderInfo obtainHolderInfo() {
            return new ItemHolderInfo();
        }
    }

    public static abstract class ItemDecoration {
        @Deprecated
        public void onDraw(Canvas canvas, RecyclerView recyclerView) {
        }

        @Deprecated
        public void onDrawOver(Canvas canvas, RecyclerView recyclerView) {
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, State state) {
            onDraw(canvas, recyclerView);
        }

        public void onDrawOver(Canvas canvas, RecyclerView recyclerView, State state) {
            onDrawOver(canvas, recyclerView);
        }

        @Deprecated
        public void getItemOffsets(Rect rect, int i, RecyclerView recyclerView) {
            rect.set(0, 0, 0, 0);
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
            getItemOffsets(rect, ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition(), recyclerView);
        }
    }

    public static abstract class LayoutManager {
        boolean mAutoMeasure = false;
        ChildHelper mChildHelper;
        private int mHeight;
        private int mHeightMode;
        ViewBoundsCheck mHorizontalBoundCheck = new ViewBoundsCheck(this.mHorizontalBoundCheckCallback);
        private final Callback mHorizontalBoundCheckCallback = new C18721();
        boolean mIsAttachedToWindow = false;
        private boolean mItemPrefetchEnabled = true;
        private boolean mMeasurementCacheEnabled = true;
        int mPrefetchMaxCountObserved;
        boolean mPrefetchMaxObservedInInitialPrefetch;
        RecyclerView mRecyclerView;
        boolean mRequestedSimpleAnimations = false;
        SmoothScroller mSmoothScroller;
        ViewBoundsCheck mVerticalBoundCheck = new ViewBoundsCheck(this.mVerticalBoundCheckCallback);
        private final Callback mVerticalBoundCheckCallback = new C18732();
        private int mWidth;
        private int mWidthMode;

        public interface LayoutPrefetchRegistry {
            void addPosition(int i, int i2);
        }

        public static class Properties {
            public int orientation;
            public boolean reverseLayout;
            public int spanCount;
            public boolean stackFromEnd;
        }

        /* renamed from: org.telegram.messenger.support.widget.RecyclerView$LayoutManager$1 */
        class C18721 implements Callback {
            C18721() {
            }

            public int getChildCount() {
                return LayoutManager.this.getChildCount();
            }

            public View getParent() {
                return LayoutManager.this.mRecyclerView;
            }

            public View getChildAt(int i) {
                return LayoutManager.this.getChildAt(i);
            }

            public int getParentStart() {
                return LayoutManager.this.getPaddingLeft();
            }

            public int getParentEnd() {
                return LayoutManager.this.getWidth() - LayoutManager.this.getPaddingRight();
            }

            public int getChildStart(View view) {
                return LayoutManager.this.getDecoratedLeft(view) - ((LayoutParams) view.getLayoutParams()).leftMargin;
            }

            public int getChildEnd(View view) {
                return LayoutManager.this.getDecoratedRight(view) + ((LayoutParams) view.getLayoutParams()).rightMargin;
            }
        }

        /* renamed from: org.telegram.messenger.support.widget.RecyclerView$LayoutManager$2 */
        class C18732 implements Callback {
            C18732() {
            }

            public int getChildCount() {
                return LayoutManager.this.getChildCount();
            }

            public View getParent() {
                return LayoutManager.this.mRecyclerView;
            }

            public View getChildAt(int i) {
                return LayoutManager.this.getChildAt(i);
            }

            public int getParentStart() {
                return LayoutManager.this.getPaddingTop();
            }

            public int getParentEnd() {
                return LayoutManager.this.getHeight() - LayoutManager.this.getPaddingBottom();
            }

            public int getChildStart(View view) {
                return LayoutManager.this.getDecoratedTop(view) - ((LayoutParams) view.getLayoutParams()).topMargin;
            }

            public int getChildEnd(View view) {
                return LayoutManager.this.getDecoratedBottom(view) + ((LayoutParams) view.getLayoutParams()).bottomMargin;
            }
        }

        public boolean canScrollHorizontally() {
            return false;
        }

        public boolean canScrollVertically() {
            return false;
        }

        public boolean checkLayoutParams(LayoutParams layoutParams) {
            return layoutParams != null;
        }

        public void collectAdjacentPrefetchPositions(int i, int i2, State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        }

        public void collectInitialPrefetchPositions(int i, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        }

        public int computeHorizontalScrollExtent(State state) {
            return 0;
        }

        public int computeHorizontalScrollOffset(State state) {
            return 0;
        }

        public int computeHorizontalScrollRange(State state) {
            return 0;
        }

        public int computeVerticalScrollExtent(State state) {
            return 0;
        }

        public int computeVerticalScrollOffset(State state) {
            return 0;
        }

        public int computeVerticalScrollRange(State state) {
            return 0;
        }

        public abstract LayoutParams generateDefaultLayoutParams();

        public int getBaseline() {
            return -1;
        }

        public int getSelectionModeForAccessibility(Recycler recycler, State state) {
            return 0;
        }

        public boolean isLayoutHierarchical(Recycler recycler, State state) {
            return false;
        }

        public void onAdapterChanged(Adapter adapter, Adapter adapter2) {
        }

        public boolean onAddFocusables(RecyclerView recyclerView, ArrayList<View> arrayList, int i, int i2) {
            return false;
        }

        public void onAttachedToWindow(RecyclerView recyclerView) {
        }

        @Deprecated
        public void onDetachedFromWindow(RecyclerView recyclerView) {
        }

        public View onFocusSearchFailed(View view, int i, Recycler recycler, State state) {
            return null;
        }

        public View onInterceptFocusSearch(View view, int i) {
            return null;
        }

        public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        }

        public void onItemsChanged(RecyclerView recyclerView) {
        }

        public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        }

        public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        }

        public void onItemsUpdated(RecyclerView recyclerView, int i, int i2) {
        }

        public void onLayoutCompleted(State state) {
        }

        public void onRestoreInstanceState(Parcelable parcelable) {
        }

        public Parcelable onSaveInstanceState() {
            return null;
        }

        public void onScrollStateChanged(int i) {
        }

        public boolean performAccessibilityActionForItem(Recycler recycler, State state, View view, int i, Bundle bundle) {
            return false;
        }

        public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
            return 0;
        }

        public void scrollToPosition(int i) {
        }

        public int scrollVerticallyBy(int i, Recycler recycler, State state) {
            return 0;
        }

        boolean shouldMeasureTwice() {
            return false;
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        void setRecyclerView(RecyclerView recyclerView) {
            if (recyclerView == null) {
                this.mRecyclerView = null;
                this.mChildHelper = null;
                this.mWidth = 0;
                this.mHeight = 0;
            } else {
                this.mRecyclerView = recyclerView;
                this.mChildHelper = recyclerView.mChildHelper;
                this.mWidth = recyclerView.getWidth();
                this.mHeight = recyclerView.getHeight();
            }
            this.mWidthMode = NUM;
            this.mHeightMode = NUM;
        }

        void setMeasureSpecs(int i, int i2) {
            this.mWidth = MeasureSpec.getSize(i);
            this.mWidthMode = MeasureSpec.getMode(i);
            if (this.mWidthMode == 0 && RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC == 0) {
                this.mWidth = 0;
            }
            this.mHeight = MeasureSpec.getSize(i2);
            this.mHeightMode = MeasureSpec.getMode(i2);
            if (this.mHeightMode == 0 && RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC == 0) {
                this.mHeight = 0;
            }
        }

        void setMeasuredDimensionFromChildren(int i, int i2) {
            int childCount = getChildCount();
            if (childCount == 0) {
                this.mRecyclerView.defaultOnMeasure(i, i2);
                return;
            }
            int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int i4 = Integer.MIN_VALUE;
            int i5 = i4;
            int i6 = ConnectionsManager.DEFAULT_DATACENTER_ID;
            for (int i7 = 0; i7 < childCount; i7++) {
                View childAt = getChildAt(i7);
                Rect rect = this.mRecyclerView.mTempRect;
                getDecoratedBoundsWithMargins(childAt, rect);
                if (rect.left < i3) {
                    i3 = rect.left;
                }
                if (rect.right > i4) {
                    i4 = rect.right;
                }
                if (rect.top < i6) {
                    i6 = rect.top;
                }
                if (rect.bottom > i5) {
                    i5 = rect.bottom;
                }
            }
            this.mRecyclerView.mTempRect.set(i3, i6, i4, i5);
            setMeasuredDimension(this.mRecyclerView.mTempRect, i, i2);
        }

        public void setMeasuredDimension(Rect rect, int i, int i2) {
            setMeasuredDimension(chooseSize(i, (rect.width() + getPaddingLeft()) + getPaddingRight(), getMinimumWidth()), chooseSize(i2, (rect.height() + getPaddingTop()) + getPaddingBottom(), getMinimumHeight()));
        }

        public void requestLayout() {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.requestLayout();
            }
        }

        public void assertInLayoutOrScroll(String str) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.assertInLayoutOrScroll(str);
            }
        }

        public static int chooseSize(int i, int i2, int i3) {
            int mode = MeasureSpec.getMode(i);
            i = MeasureSpec.getSize(i);
            if (mode != Integer.MIN_VALUE) {
                return mode != NUM ? Math.max(i2, i3) : i;
            } else {
                return Math.min(i, Math.max(i2, i3));
            }
        }

        public void assertNotInLayoutOrScroll(String str) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.assertNotInLayoutOrScroll(str);
            }
        }

        @Deprecated
        public void setAutoMeasureEnabled(boolean z) {
            this.mAutoMeasure = z;
        }

        public boolean isAutoMeasureEnabled() {
            return this.mAutoMeasure;
        }

        public final void setItemPrefetchEnabled(boolean z) {
            if (z != this.mItemPrefetchEnabled) {
                this.mItemPrefetchEnabled = z;
                this.mPrefetchMaxCountObserved = false;
                if (this.mRecyclerView) {
                    this.mRecyclerView.mRecycler.updateViewCacheSize();
                }
            }
        }

        public final boolean isItemPrefetchEnabled() {
            return this.mItemPrefetchEnabled;
        }

        void dispatchAttachedToWindow(RecyclerView recyclerView) {
            this.mIsAttachedToWindow = true;
            onAttachedToWindow(recyclerView);
        }

        void dispatchDetachedFromWindow(RecyclerView recyclerView, Recycler recycler) {
            this.mIsAttachedToWindow = false;
            onDetachedFromWindow(recyclerView, recycler);
        }

        public boolean isAttachedToWindow() {
            return this.mIsAttachedToWindow;
        }

        public void postOnAnimation(Runnable runnable) {
            if (this.mRecyclerView != null) {
                ViewCompat.postOnAnimation(this.mRecyclerView, runnable);
            }
        }

        public boolean removeCallbacks(Runnable runnable) {
            return this.mRecyclerView != null ? this.mRecyclerView.removeCallbacks(runnable) : null;
        }

        public void onDetachedFromWindow(RecyclerView recyclerView, Recycler recycler) {
            onDetachedFromWindow(recyclerView);
        }

        public boolean getClipToPadding() {
            return this.mRecyclerView != null && this.mRecyclerView.mClipToPadding;
        }

        public void onLayoutChildren(Recycler recycler, State state) {
            Log.e(RecyclerView.TAG, "You must override onLayoutChildren(Recycler recycler, State state) ");
        }

        public LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            if (layoutParams instanceof LayoutParams) {
                return new LayoutParams((LayoutParams) layoutParams);
            }
            if (layoutParams instanceof MarginLayoutParams) {
                return new LayoutParams((MarginLayoutParams) layoutParams);
            }
            return new LayoutParams(layoutParams);
        }

        public LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
            return new LayoutParams(context, attributeSet);
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
            Log.e(RecyclerView.TAG, "You must override smoothScrollToPosition to support smooth scrolling");
        }

        public void startSmoothScroll(SmoothScroller smoothScroller) {
            if (!(this.mSmoothScroller == null || smoothScroller == this.mSmoothScroller || !this.mSmoothScroller.isRunning())) {
                this.mSmoothScroller.stop();
            }
            this.mSmoothScroller = smoothScroller;
            this.mSmoothScroller.start(this.mRecyclerView, this);
        }

        public boolean isSmoothScrolling() {
            return this.mSmoothScroller != null && this.mSmoothScroller.isRunning();
        }

        public int getLayoutDirection() {
            return ViewCompat.getLayoutDirection(this.mRecyclerView);
        }

        public void endAnimation(View view) {
            if (this.mRecyclerView.mItemAnimator != null) {
                this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(view));
            }
        }

        public void addDisappearingView(View view) {
            addDisappearingView(view, -1);
        }

        public void addDisappearingView(View view, int i) {
            addViewInt(view, i, true);
        }

        public void addView(View view) {
            addView(view, -1);
        }

        public void addView(View view, int i) {
            addViewInt(view, i, false);
        }

        private void addViewInt(View view, int i, boolean z) {
            LayoutParams layoutParams;
            int indexOfChild;
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (!z) {
                if (!childViewHolderInt.isRemoved()) {
                    this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(childViewHolderInt);
                    layoutParams = (LayoutParams) view.getLayoutParams();
                    if (!childViewHolderInt.wasReturnedFromScrap()) {
                        if (childViewHolderInt.isScrap()) {
                            if (view.getParent() != this.mRecyclerView) {
                                indexOfChild = this.mChildHelper.indexOfChild(view);
                                if (i == -1) {
                                    i = this.mChildHelper.getChildCount();
                                }
                                if (indexOfChild == -1) {
                                    z = new StringBuilder();
                                    z.append("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:");
                                    z.append(this.mRecyclerView.indexOfChild(view));
                                    z.append(this.mRecyclerView.exceptionLabel());
                                    throw new IllegalStateException(z.toString());
                                } else if (indexOfChild != i) {
                                    this.mRecyclerView.mLayout.moveView(indexOfChild, i);
                                }
                            } else {
                                this.mChildHelper.addView(view, i, false);
                                layoutParams.mInsetsDirty = true;
                                if (!(this.mSmoothScroller == 0 || this.mSmoothScroller.isRunning() == 0)) {
                                    this.mSmoothScroller.onChildAttachedToWindow(view);
                                }
                            }
                            if (layoutParams.mPendingInvalidate == null) {
                                childViewHolderInt.itemView.invalidate();
                                layoutParams.mPendingInvalidate = false;
                            }
                        }
                    }
                    if (childViewHolderInt.isScrap()) {
                        childViewHolderInt.clearReturnedFromScrapFlag();
                    } else {
                        childViewHolderInt.unScrap();
                    }
                    this.mChildHelper.attachViewToParent(view, i, view.getLayoutParams(), false);
                    if (layoutParams.mPendingInvalidate == null) {
                        childViewHolderInt.itemView.invalidate();
                        layoutParams.mPendingInvalidate = false;
                    }
                }
            }
            this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(childViewHolderInt);
            layoutParams = (LayoutParams) view.getLayoutParams();
            if (childViewHolderInt.wasReturnedFromScrap()) {
                if (childViewHolderInt.isScrap()) {
                    if (view.getParent() != this.mRecyclerView) {
                        this.mChildHelper.addView(view, i, false);
                        layoutParams.mInsetsDirty = true;
                        this.mSmoothScroller.onChildAttachedToWindow(view);
                    } else {
                        indexOfChild = this.mChildHelper.indexOfChild(view);
                        if (i == -1) {
                            i = this.mChildHelper.getChildCount();
                        }
                        if (indexOfChild == -1) {
                            z = new StringBuilder();
                            z.append("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:");
                            z.append(this.mRecyclerView.indexOfChild(view));
                            z.append(this.mRecyclerView.exceptionLabel());
                            throw new IllegalStateException(z.toString());
                        } else if (indexOfChild != i) {
                            this.mRecyclerView.mLayout.moveView(indexOfChild, i);
                        }
                    }
                    if (layoutParams.mPendingInvalidate == null) {
                        childViewHolderInt.itemView.invalidate();
                        layoutParams.mPendingInvalidate = false;
                    }
                }
            }
            if (childViewHolderInt.isScrap()) {
                childViewHolderInt.clearReturnedFromScrapFlag();
            } else {
                childViewHolderInt.unScrap();
            }
            this.mChildHelper.attachViewToParent(view, i, view.getLayoutParams(), false);
            if (layoutParams.mPendingInvalidate == null) {
                childViewHolderInt.itemView.invalidate();
                layoutParams.mPendingInvalidate = false;
            }
        }

        public void removeView(View view) {
            this.mChildHelper.removeView(view);
        }

        public void removeViewAt(int i) {
            if (getChildAt(i) != null) {
                this.mChildHelper.removeViewAt(i);
            }
        }

        public void removeAllViews() {
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                this.mChildHelper.removeViewAt(childCount);
            }
        }

        public int getPosition(View view) {
            return ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        }

        public int getItemViewType(View view) {
            return RecyclerView.getChildViewHolderInt(view).getItemViewType();
        }

        public View findContainingItemView(View view) {
            if (this.mRecyclerView == null) {
                return null;
            }
            view = this.mRecyclerView.findContainingItemView(view);
            if (view == null || this.mChildHelper.isHidden(view)) {
                return null;
            }
            return view;
        }

        public View findViewByPosition(int i) {
            int childCount = getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = getChildAt(i2);
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(childAt);
                if (childViewHolderInt != null) {
                    if (childViewHolderInt.getLayoutPosition() == i && !childViewHolderInt.shouldIgnore() && (this.mRecyclerView.mState.isPreLayout() || !childViewHolderInt.isRemoved())) {
                        return childAt;
                    }
                }
            }
            return 0;
        }

        public void detachView(View view) {
            int indexOfChild = this.mChildHelper.indexOfChild(view);
            if (indexOfChild >= 0) {
                detachViewInternal(indexOfChild, view);
            }
        }

        public void detachViewAt(int i) {
            detachViewInternal(i, getChildAt(i));
        }

        private void detachViewInternal(int i, View view) {
            this.mChildHelper.detachViewFromParent(i);
        }

        public void attachView(View view, int i, LayoutParams layoutParams) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(childViewHolderInt);
            } else {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(childViewHolderInt);
            }
            this.mChildHelper.attachViewToParent(view, i, layoutParams, childViewHolderInt.isRemoved());
        }

        public void attachView(View view, int i) {
            attachView(view, i, (LayoutParams) view.getLayoutParams());
        }

        public void attachView(View view) {
            attachView(view, -1);
        }

        public void removeDetachedView(View view) {
            this.mRecyclerView.removeDetachedView(view, false);
        }

        public void moveView(int i, int i2) {
            View childAt = getChildAt(i);
            if (childAt == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Cannot move a child from non-existing index:");
                stringBuilder.append(i);
                stringBuilder.append(this.mRecyclerView.toString());
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            detachViewAt(i);
            attachView(childAt, i2);
        }

        public void detachAndScrapView(View view, Recycler recycler) {
            scrapOrRecycleView(recycler, this.mChildHelper.indexOfChild(view), view);
        }

        public void detachAndScrapViewAt(int i, Recycler recycler) {
            scrapOrRecycleView(recycler, i, getChildAt(i));
        }

        public void removeAndRecycleView(View view, Recycler recycler) {
            removeView(view);
            recycler.recycleView(view);
        }

        public void removeAndRecycleViewAt(int i, Recycler recycler) {
            View childAt = getChildAt(i);
            removeViewAt(i);
            recycler.recycleView(childAt);
        }

        public int getChildCount() {
            return this.mChildHelper != null ? this.mChildHelper.getChildCount() : 0;
        }

        public View getChildAt(int i) {
            return this.mChildHelper != null ? this.mChildHelper.getChildAt(i) : 0;
        }

        public int getWidthMode() {
            return this.mWidthMode;
        }

        public int getHeightMode() {
            return this.mHeightMode;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public int getPaddingLeft() {
            return this.mRecyclerView != null ? this.mRecyclerView.getPaddingLeft() : 0;
        }

        public int getPaddingTop() {
            return this.mRecyclerView != null ? this.mRecyclerView.getPaddingTop() : 0;
        }

        public int getPaddingRight() {
            return this.mRecyclerView != null ? this.mRecyclerView.getPaddingRight() : 0;
        }

        public int getPaddingBottom() {
            return this.mRecyclerView != null ? this.mRecyclerView.getPaddingBottom() : 0;
        }

        public int getPaddingStart() {
            return this.mRecyclerView != null ? ViewCompat.getPaddingStart(this.mRecyclerView) : 0;
        }

        public int getPaddingEnd() {
            return this.mRecyclerView != null ? ViewCompat.getPaddingEnd(this.mRecyclerView) : 0;
        }

        public boolean isFocused() {
            return this.mRecyclerView != null && this.mRecyclerView.isFocused();
        }

        public boolean hasFocus() {
            return this.mRecyclerView != null && this.mRecyclerView.hasFocus();
        }

        public View getFocusedChild() {
            if (this.mRecyclerView == null) {
                return null;
            }
            View focusedChild = this.mRecyclerView.getFocusedChild();
            if (focusedChild != null) {
                if (!this.mChildHelper.isHidden(focusedChild)) {
                    return focusedChild;
                }
            }
            return null;
        }

        public int getItemCount() {
            Adapter adapter = this.mRecyclerView != null ? this.mRecyclerView.getAdapter() : null;
            return adapter != null ? adapter.getItemCount() : 0;
        }

        public void offsetChildrenHorizontal(int i) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.offsetChildrenHorizontal(i);
            }
        }

        public void offsetChildrenVertical(int i) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.offsetChildrenVertical(i);
            }
        }

        public void ignoreView(View view) {
            if (view.getParent() == this.mRecyclerView) {
                if (this.mRecyclerView.indexOfChild(view) != -1) {
                    view = RecyclerView.getChildViewHolderInt(view);
                    view.addFlags(128);
                    this.mRecyclerView.mViewInfoStore.removeViewHolder(view);
                    return;
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("View should be fully attached to be ignored");
            stringBuilder.append(this.mRecyclerView.exceptionLabel());
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        public void stopIgnoringView(View view) {
            view = RecyclerView.getChildViewHolderInt(view);
            view.stopIgnoring();
            view.resetInternal();
            view.addFlags(4);
        }

        public void detachAndScrapAttachedViews(Recycler recycler) {
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                scrapOrRecycleView(recycler, childCount, getChildAt(childCount));
            }
        }

        private void scrapOrRecycleView(Recycler recycler, int i, View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (!childViewHolderInt.shouldIgnore()) {
                if (!childViewHolderInt.isInvalid() || childViewHolderInt.isRemoved() || this.mRecyclerView.mAdapter.hasStableIds()) {
                    detachViewAt(i);
                    recycler.scrapView(view);
                    this.mRecyclerView.mViewInfoStore.onViewDetached(childViewHolderInt);
                } else {
                    removeViewAt(i);
                    recycler.recycleViewHolderInternal(childViewHolderInt);
                }
            }
        }

        void removeAndRecycleScrapInt(Recycler recycler) {
            int scrapCount = recycler.getScrapCount();
            for (int i = scrapCount - 1; i >= 0; i--) {
                View scrapViewAt = recycler.getScrapViewAt(i);
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(scrapViewAt);
                if (!childViewHolderInt.shouldIgnore()) {
                    childViewHolderInt.setIsRecyclable(false);
                    if (childViewHolderInt.isTmpDetached()) {
                        this.mRecyclerView.removeDetachedView(scrapViewAt, false);
                    }
                    if (this.mRecyclerView.mItemAnimator != null) {
                        this.mRecyclerView.mItemAnimator.endAnimation(childViewHolderInt);
                    }
                    childViewHolderInt.setIsRecyclable(true);
                    recycler.quickRecycleScrapView(scrapViewAt);
                }
            }
            recycler.clearScrap();
            if (scrapCount > 0) {
                this.mRecyclerView.invalidate();
            }
        }

        public void measureChild(View view, int i, int i2) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            Rect itemDecorInsetsForChild = this.mRecyclerView.getItemDecorInsetsForChild(view);
            i2 += itemDecorInsetsForChild.top + itemDecorInsetsForChild.bottom;
            i = getChildMeasureSpec(getWidth(), getWidthMode(), (getPaddingLeft() + getPaddingRight()) + (i + (itemDecorInsetsForChild.left + itemDecorInsetsForChild.right)), layoutParams.width, canScrollHorizontally());
            i2 = getChildMeasureSpec(getHeight(), getHeightMode(), (getPaddingTop() + getPaddingBottom()) + i2, layoutParams.height, canScrollVertically());
            if (shouldMeasureChild(view, i, i2, layoutParams)) {
                view.measure(i, i2);
            }
        }

        boolean shouldReMeasureChild(View view, int i, int i2, LayoutParams layoutParams) {
            if (this.mMeasurementCacheEnabled && isMeasurementUpToDate(view.getMeasuredWidth(), i, layoutParams.width) != 0) {
                if (isMeasurementUpToDate(view.getMeasuredHeight(), i2, layoutParams.height) != null) {
                    return null;
                }
            }
            return true;
        }

        boolean shouldMeasureChild(View view, int i, int i2, LayoutParams layoutParams) {
            if (!(view.isLayoutRequested() || !this.mMeasurementCacheEnabled || isMeasurementUpToDate(view.getWidth(), i, layoutParams.width) == 0)) {
                if (isMeasurementUpToDate(view.getHeight(), i2, layoutParams.height) != null) {
                    return null;
                }
            }
            return true;
        }

        public boolean isMeasurementCacheEnabled() {
            return this.mMeasurementCacheEnabled;
        }

        public void setMeasurementCacheEnabled(boolean z) {
            this.mMeasurementCacheEnabled = z;
        }

        private static boolean isMeasurementUpToDate(int i, int i2, int i3) {
            int mode = MeasureSpec.getMode(i2);
            i2 = MeasureSpec.getSize(i2);
            boolean z = false;
            if (i3 > 0 && i != i3) {
                return false;
            }
            if (mode == Integer.MIN_VALUE) {
                if (i2 >= i) {
                    z = true;
                }
                return z;
            } else if (mode == 0) {
                return true;
            } else {
                if (mode != NUM) {
                    return false;
                }
                if (i2 == i) {
                    z = true;
                }
                return z;
            }
        }

        public void measureChildWithMargins(View view, int i, int i2) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            Rect itemDecorInsetsForChild = this.mRecyclerView.getItemDecorInsetsForChild(view);
            i2 += itemDecorInsetsForChild.top + itemDecorInsetsForChild.bottom;
            i = getChildMeasureSpec(getWidth(), getWidthMode(), (((getPaddingLeft() + getPaddingRight()) + layoutParams.leftMargin) + layoutParams.rightMargin) + (i + (itemDecorInsetsForChild.left + itemDecorInsetsForChild.right)), layoutParams.width, canScrollHorizontally());
            i2 = getChildMeasureSpec(getHeight(), getHeightMode(), (((getPaddingTop() + getPaddingBottom()) + layoutParams.topMargin) + layoutParams.bottomMargin) + i2, layoutParams.height, canScrollVertically());
            if (shouldMeasureChild(view, i, i2, layoutParams)) {
                view.measure(i, i2);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        @Deprecated
        public static int getChildMeasureSpec(int i, int i2, int i3, boolean z) {
            i -= i2;
            i2 = 0;
            i = Math.max(0, i);
            if (!z) {
                if (i3 < 0) {
                    if (i3 == true) {
                        i2 = NUM;
                        return MeasureSpec.makeMeasureSpec(i, i2);
                    }
                    if (i3 == true) {
                        i2 = Integer.MIN_VALUE;
                        return MeasureSpec.makeMeasureSpec(i, i2);
                    }
                    i = 0;
                    return MeasureSpec.makeMeasureSpec(i, i2);
                }
            }
            i = i3;
            i2 = NUM;
            return MeasureSpec.makeMeasureSpec(i, i2);
        }

        public static int getChildMeasureSpec(int i, int i2, int i3, int i4, boolean z) {
            i -= i3;
            i3 = 0;
            i = Math.max(0, i);
            if (z) {
                if (i4 < 0) {
                    if (i4 == -1) {
                        if (i2 == Integer.MIN_VALUE || (i2 != 0 && i2 == NUM)) {
                            i4 = i;
                        } else {
                            i2 = 0;
                            i4 = i2;
                        }
                        i3 = i2;
                        i = i4;
                        return MeasureSpec.makeMeasureSpec(i, i3);
                    }
                    i = 0;
                    return MeasureSpec.makeMeasureSpec(i, i3);
                }
            } else if (i4 < 0) {
                if (i4 == -1) {
                    i3 = i2;
                } else {
                    if (i4 == -2) {
                        if (i2 == Integer.MIN_VALUE || i2 == NUM) {
                            i3 = Integer.MIN_VALUE;
                        }
                    }
                    i = 0;
                }
                return MeasureSpec.makeMeasureSpec(i, i3);
            }
            i = i4;
            i3 = NUM;
            return MeasureSpec.makeMeasureSpec(i, i3);
        }

        public int getDecoratedMeasuredWidth(View view) {
            Rect rect = ((LayoutParams) view.getLayoutParams()).mDecorInsets;
            return (view.getMeasuredWidth() + rect.left) + rect.right;
        }

        public int getDecoratedMeasuredHeight(View view) {
            Rect rect = ((LayoutParams) view.getLayoutParams()).mDecorInsets;
            return (view.getMeasuredHeight() + rect.top) + rect.bottom;
        }

        public void layoutDecorated(View view, int i, int i2, int i3, int i4) {
            Rect rect = ((LayoutParams) view.getLayoutParams()).mDecorInsets;
            view.layout(i + rect.left, i2 + rect.top, i3 - rect.right, i4 - rect.bottom);
        }

        public void layoutDecoratedWithMargins(View view, int i, int i2, int i3, int i4) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            Rect rect = layoutParams.mDecorInsets;
            view.layout((i + rect.left) + layoutParams.leftMargin, (i2 + rect.top) + layoutParams.topMargin, (i3 - rect.right) - layoutParams.rightMargin, (i4 - rect.bottom) - layoutParams.bottomMargin);
        }

        public void getTransformedBoundingBox(View view, boolean z, Rect rect) {
            if (z) {
                z = ((LayoutParams) view.getLayoutParams()).mDecorInsets;
                rect.set(-z.left, -z.top, view.getWidth() + z.right, view.getHeight() + z.bottom);
            } else {
                rect.set(0, 0, view.getWidth(), view.getHeight());
            }
            if (this.mRecyclerView) {
                z = view.getMatrix();
                if (z && !z.isIdentity()) {
                    RectF rectF = this.mRecyclerView.mTempRectF;
                    rectF.set(rect);
                    z.mapRect(rectF);
                    rect.set((int) Math.floor((double) rectF.left), (int) Math.floor((double) rectF.top), (int) Math.ceil((double) rectF.right), (int) Math.ceil((double) rectF.bottom));
                }
            }
            rect.offset(view.getLeft(), view.getTop());
        }

        public void getDecoratedBoundsWithMargins(View view, Rect rect) {
            RecyclerView.getDecoratedBoundsWithMarginsInt(view, rect);
        }

        public int getDecoratedLeft(View view) {
            return view.getLeft() - getLeftDecorationWidth(view);
        }

        public int getDecoratedTop(View view) {
            return view.getTop() - getTopDecorationHeight(view);
        }

        public int getDecoratedRight(View view) {
            return view.getRight() + getRightDecorationWidth(view);
        }

        public int getDecoratedBottom(View view) {
            return view.getBottom() + getBottomDecorationHeight(view);
        }

        public void calculateItemDecorationsForChild(View view, Rect rect) {
            if (this.mRecyclerView == null) {
                rect.set(0, 0, 0, 0);
            } else {
                rect.set(this.mRecyclerView.getItemDecorInsetsForChild(view));
            }
        }

        public int getTopDecorationHeight(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.top;
        }

        public int getBottomDecorationHeight(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.bottom;
        }

        public int getLeftDecorationWidth(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.left;
        }

        public int getRightDecorationWidth(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.right;
        }

        private int[] getChildRectangleOnScreenScrollAmount(RecyclerView recyclerView, View view, Rect rect, boolean z) {
            recyclerView = new int[2];
            z = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int width = getWidth() - getPaddingRight();
            int height = getHeight() - getPaddingBottom();
            int left = (view.getLeft() + rect.left) - view.getScrollX();
            int top = (view.getTop() + rect.top) - view.getScrollY();
            view = rect.width() + left;
            rect = rect.height() + top;
            left -= z;
            int min = Math.min(0, left);
            top -= paddingTop;
            paddingTop = Math.min(0, top);
            view -= width;
            width = Math.max(0, view);
            rect = Math.max(0, rect - height);
            if (getLayoutDirection() != 1) {
                if (min == 0) {
                    min = Math.min(left, width);
                }
                width = min;
            } else if (width == 0) {
                width = Math.max(min, view);
            }
            if (paddingTop == 0) {
                paddingTop = Math.min(top, rect);
            }
            recyclerView[0] = width;
            recyclerView[1] = paddingTop;
            return recyclerView;
        }

        public boolean requestChildRectangleOnScreen(RecyclerView recyclerView, View view, Rect rect, boolean z) {
            return requestChildRectangleOnScreen(recyclerView, view, rect, z, false);
        }

        public boolean requestChildRectangleOnScreen(RecyclerView recyclerView, View view, Rect rect, boolean z, boolean z2) {
            view = getChildRectangleOnScreenScrollAmount(recyclerView, view, rect, z);
            int i = view[0];
            view = view[1];
            if (!z2 || isFocusedChildVisibleAfterScrolling(recyclerView, i, view)) {
                if (i == 0) {
                    if (view != null) {
                    }
                }
                if (z) {
                    recyclerView.scrollBy(i, view);
                } else {
                    recyclerView.smoothScrollBy(i, view);
                }
                return true;
            }
            return false;
        }

        public boolean isViewPartiallyVisible(View view, boolean z, boolean z2) {
            view = (!this.mHorizontalBoundCheck.isViewWithinBoundFlags(view, 24579) || this.mVerticalBoundCheck.isViewWithinBoundFlags(view, 24579) == null) ? null : 1;
            return z ? view : view ^ 1;
        }

        private boolean isFocusedChildVisibleAfterScrolling(RecyclerView recyclerView, int i, int i2) {
            recyclerView = recyclerView.getFocusedChild();
            if (recyclerView == null) {
                return false;
            }
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int width = getWidth() - getPaddingRight();
            int height = getHeight() - getPaddingBottom();
            Rect rect = this.mRecyclerView.mTempRect;
            getDecoratedBoundsWithMargins(recyclerView, rect);
            if (rect.left - i < width && rect.right - i > paddingLeft && rect.top - i2 < height) {
                if (rect.bottom - i2 > paddingTop) {
                    return true;
                }
            }
            return false;
        }

        @Deprecated
        public boolean onRequestChildFocus(RecyclerView recyclerView, View view, View view2) {
            if (isSmoothScrolling() == null) {
                if (recyclerView.isComputingLayout() == null) {
                    return null;
                }
            }
            return true;
        }

        public boolean onRequestChildFocus(RecyclerView recyclerView, State state, View view, View view2) {
            return onRequestChildFocus(recyclerView, view, view2);
        }

        public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
            onItemsUpdated(recyclerView, i, i2);
        }

        public void onMeasure(Recycler recycler, State state, int i, int i2) {
            this.mRecyclerView.defaultOnMeasure(i, i2);
        }

        public void setMeasuredDimension(int i, int i2) {
            this.mRecyclerView.setMeasuredDimension(i, i2);
        }

        public int getMinimumWidth() {
            return ViewCompat.getMinimumWidth(this.mRecyclerView);
        }

        public int getMinimumHeight() {
            return ViewCompat.getMinimumHeight(this.mRecyclerView);
        }

        void stopSmoothScroller() {
            if (this.mSmoothScroller != null) {
                this.mSmoothScroller.stop();
            }
        }

        private void onSmoothScrollerStopped(SmoothScroller smoothScroller) {
            if (this.mSmoothScroller == smoothScroller) {
                this.mSmoothScroller = null;
            }
        }

        public void removeAndRecycleAllViews(Recycler recycler) {
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                if (!RecyclerView.getChildViewHolderInt(getChildAt(childCount)).shouldIgnore()) {
                    removeAndRecycleViewAt(childCount, recycler);
                }
            }
        }

        void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, accessibilityNodeInfoCompat);
        }

        public void onInitializeAccessibilityNodeInfo(Recycler recycler, State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            if (this.mRecyclerView.canScrollVertically(-1) || this.mRecyclerView.canScrollHorizontally(-1)) {
                accessibilityNodeInfoCompat.addAction(MessagesController.UPDATE_MASK_CHANNEL);
                accessibilityNodeInfoCompat.setScrollable(true);
            }
            if (this.mRecyclerView.canScrollVertically(1) || this.mRecyclerView.canScrollHorizontally(1)) {
                accessibilityNodeInfoCompat.addAction(4096);
                accessibilityNodeInfoCompat.setScrollable(true);
            }
            accessibilityNodeInfoCompat.setCollectionInfo(CollectionInfoCompat.obtain(getRowCountForAccessibility(recycler, state), getColumnCountForAccessibility(recycler, state), isLayoutHierarchical(recycler, state), getSelectionModeForAccessibility(recycler, state)));
        }

        public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, accessibilityEvent);
        }

        public void onInitializeAccessibilityEvent(Recycler recycler, State state, AccessibilityEvent accessibilityEvent) {
            if (this.mRecyclerView != null) {
                if (accessibilityEvent != null) {
                    state = true;
                    if (this.mRecyclerView.canScrollVertically(1) == null && this.mRecyclerView.canScrollVertically(-1) == null && this.mRecyclerView.canScrollHorizontally(-1) == null) {
                        if (this.mRecyclerView.canScrollHorizontally(1) == null) {
                            state = null;
                        }
                    }
                    accessibilityEvent.setScrollable(state);
                    if (this.mRecyclerView.mAdapter != null) {
                        accessibilityEvent.setItemCount(this.mRecyclerView.mAdapter.getItemCount());
                    }
                }
            }
        }

        void onInitializeAccessibilityNodeInfoForItem(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt != null && !childViewHolderInt.isRemoved() && !this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, view, accessibilityNodeInfoCompat);
            }
        }

        public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            state = null;
            int position = canScrollVertically() != null ? getPosition(view) : 0;
            if (canScrollHorizontally() != null) {
                state = getPosition(view);
            }
            accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(position, 1, state, 1, false, false));
        }

        public void requestSimpleAnimationsInNextLayout() {
            this.mRequestedSimpleAnimations = true;
        }

        public int getRowCountForAccessibility(Recycler recycler, State state) {
            state = true;
            if (this.mRecyclerView != null) {
                if (this.mRecyclerView.mAdapter != null) {
                    if (canScrollVertically() != null) {
                        state = this.mRecyclerView.mAdapter.getItemCount();
                    }
                    return state;
                }
            }
            return 1;
        }

        public int getColumnCountForAccessibility(Recycler recycler, State state) {
            state = true;
            if (this.mRecyclerView != null) {
                if (this.mRecyclerView.mAdapter != null) {
                    if (canScrollHorizontally() != null) {
                        state = this.mRecyclerView.mAdapter.getItemCount();
                    }
                    return state;
                }
            }
            return 1;
        }

        boolean performAccessibilityAction(int i, Bundle bundle) {
            return performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, i, bundle);
        }

        public boolean performAccessibilityAction(Recycler recycler, State state, int i, Bundle bundle) {
            if (this.mRecyclerView == null) {
                return false;
            }
            if (i != 4096) {
                if (i != MessagesController.UPDATE_MASK_CHANNEL) {
                    recycler = null;
                    i = recycler;
                } else {
                    recycler = this.mRecyclerView.canScrollVertically(-1) != null ? -((getHeight() - getPaddingTop()) - getPaddingBottom()) : null;
                    if (this.mRecyclerView.canScrollHorizontally(-1) != 0) {
                        i = -((getWidth() - getPaddingLeft()) - getPaddingRight());
                    }
                }
                if (recycler != null && i == 0) {
                    return false;
                }
                this.mRecyclerView.scrollBy(i, recycler);
                return true;
            }
            recycler = this.mRecyclerView.canScrollVertically(1) != null ? (getHeight() - getPaddingTop()) - getPaddingBottom() : null;
            if (this.mRecyclerView.canScrollHorizontally(1) != 0) {
                i = (getWidth() - getPaddingLeft()) - getPaddingRight();
                if (recycler != null) {
                }
                this.mRecyclerView.scrollBy(i, recycler);
                return true;
            }
            i = 0;
            if (recycler != null) {
            }
            this.mRecyclerView.scrollBy(i, recycler);
            return true;
        }

        boolean performAccessibilityActionForItem(View view, int i, Bundle bundle) {
            return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, view, i, bundle);
        }

        void setExactMeasureSpecsFrom(RecyclerView recyclerView) {
            setMeasureSpecs(MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), NUM), MeasureSpec.makeMeasureSpec(recyclerView.getHeight(), NUM));
        }

        boolean hasFlexibleChildInBothOrientations() {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                android.view.ViewGroup.LayoutParams layoutParams = getChildAt(i).getLayoutParams();
                if (layoutParams.width < 0 && layoutParams.height < 0) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        final Rect mDecorInsets = new Rect();
        boolean mInsetsDirty = true;
        boolean mPendingInvalidate = false;
        ViewHolder mViewHolder;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
        }

        public boolean viewNeedsUpdate() {
            return this.mViewHolder.needsUpdate();
        }

        public boolean isViewInvalid() {
            return this.mViewHolder.isInvalid();
        }

        public boolean isItemRemoved() {
            return this.mViewHolder.isRemoved();
        }

        public boolean isItemChanged() {
            return this.mViewHolder.isUpdated();
        }

        @Deprecated
        public int getViewPosition() {
            return this.mViewHolder.getPosition();
        }

        public int getViewLayoutPosition() {
            return this.mViewHolder.getLayoutPosition();
        }

        public int getViewAdapterPosition() {
            return this.mViewHolder.getAdapterPosition();
        }
    }

    public interface OnChildAttachStateChangeListener {
        void onChildViewAttachedToWindow(View view);

        void onChildViewDetachedFromWindow(View view);
    }

    public static abstract class OnFlingListener {
        public abstract boolean onFling(int i, int i2);
    }

    public interface OnItemTouchListener {
        boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent);

        void onRequestDisallowInterceptTouchEvent(boolean z);

        void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent);
    }

    public static abstract class OnScrollListener {
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }

    public static class RecycledViewPool {
        private static final int DEFAULT_MAX_SCRAP = 20;
        private int mAttachCount = 0;
        SparseArray<ScrapData> mScrap = new SparseArray();

        static class ScrapData {
            long mBindRunningAverageNs = 0;
            long mCreateRunningAverageNs = 0;
            int mMaxScrap = 20;
            final ArrayList<ViewHolder> mScrapHeap = new ArrayList();

            ScrapData() {
            }
        }

        public void clear() {
            for (int i = 0; i < this.mScrap.size(); i++) {
                ((ScrapData) this.mScrap.valueAt(i)).mScrapHeap.clear();
            }
        }

        public void setMaxRecycledViews(int i, int i2) {
            i = getScrapDataForType(i);
            i.mMaxScrap = i2;
            i = i.mScrapHeap;
            while (i.size() > i2) {
                i.remove(i.size() - 1);
            }
        }

        public int getRecycledViewCount(int i) {
            return getScrapDataForType(i).mScrapHeap.size();
        }

        public ViewHolder getRecycledView(int i) {
            ScrapData scrapData = (ScrapData) this.mScrap.get(i);
            if (scrapData == null || scrapData.mScrapHeap.isEmpty()) {
                return 0;
            }
            i = scrapData.mScrapHeap;
            return (ViewHolder) i.remove(i.size() - 1);
        }

        int size() {
            int i = 0;
            int i2 = 0;
            while (i < this.mScrap.size()) {
                ArrayList arrayList = ((ScrapData) this.mScrap.valueAt(i)).mScrapHeap;
                if (arrayList != null) {
                    i2 += arrayList.size();
                }
                i++;
            }
            return i2;
        }

        public void putRecycledView(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            ArrayList arrayList = getScrapDataForType(itemViewType).mScrapHeap;
            if (((ScrapData) this.mScrap.get(itemViewType)).mMaxScrap > arrayList.size()) {
                viewHolder.resetInternal();
                arrayList.add(viewHolder);
            }
        }

        long runningAverage(long j, long j2) {
            return j == 0 ? j2 : ((j / 4) * 3) + (j2 / 4);
        }

        void factorInCreateTime(int i, long j) {
            i = getScrapDataForType(i);
            i.mCreateRunningAverageNs = runningAverage(i.mCreateRunningAverageNs, j);
        }

        void factorInBindTime(int i, long j) {
            i = getScrapDataForType(i);
            i.mBindRunningAverageNs = runningAverage(i.mBindRunningAverageNs, j);
        }

        boolean willCreateInTime(int i, long j, long j2) {
            long j3 = getScrapDataForType(i).mCreateRunningAverageNs;
            if (j3 != 0) {
                if (j + j3 >= j2) {
                    return false;
                }
            }
            return true;
        }

        boolean willBindInTime(int i, long j, long j2) {
            long j3 = getScrapDataForType(i).mBindRunningAverageNs;
            if (j3 != 0) {
                if (j + j3 >= j2) {
                    return false;
                }
            }
            return true;
        }

        void attach(Adapter adapter) {
            this.mAttachCount++;
        }

        void detach() {
            this.mAttachCount--;
        }

        void onAdapterChanged(Adapter adapter, Adapter adapter2, boolean z) {
            if (adapter != null) {
                detach();
            }
            if (!z && this.mAttachCount == null) {
                clear();
            }
            if (adapter2 != null) {
                attach(adapter2);
            }
        }

        private ScrapData getScrapDataForType(int i) {
            ScrapData scrapData = (ScrapData) this.mScrap.get(i);
            if (scrapData != null) {
                return scrapData;
            }
            scrapData = new ScrapData();
            this.mScrap.put(i, scrapData);
            return scrapData;
        }
    }

    public final class Recycler {
        static final int DEFAULT_CACHE_SIZE = 2;
        final ArrayList<ViewHolder> mAttachedScrap = new ArrayList();
        final ArrayList<ViewHolder> mCachedViews = new ArrayList();
        ArrayList<ViewHolder> mChangedScrap = null;
        RecycledViewPool mRecyclerPool;
        private int mRequestedCacheMax = 2;
        private final List<ViewHolder> mUnmodifiableAttachedScrap = Collections.unmodifiableList(this.mAttachedScrap);
        private ViewCacheExtension mViewCacheExtension;
        int mViewCacheMax = 2;

        public void clear() {
            this.mAttachedScrap.clear();
            recycleAndClearCachedViews();
        }

        public void setViewCacheSize(int i) {
            this.mRequestedCacheMax = i;
            updateViewCacheSize();
        }

        void updateViewCacheSize() {
            this.mViewCacheMax = this.mRequestedCacheMax + (RecyclerView.this.mLayout != null ? RecyclerView.this.mLayout.mPrefetchMaxCountObserved : 0);
            for (int size = this.mCachedViews.size() - 1; size >= 0 && this.mCachedViews.size() > this.mViewCacheMax; size--) {
                recycleCachedViewAt(size);
            }
        }

        public List<ViewHolder> getScrapList() {
            return this.mUnmodifiableAttachedScrap;
        }

        boolean validateViewHolderForOffsetPosition(ViewHolder viewHolder) {
            if (viewHolder.isRemoved()) {
                return RecyclerView.this.mState.isPreLayout();
            }
            if (viewHolder.mPosition >= 0) {
                if (viewHolder.mPosition < RecyclerView.this.mAdapter.getItemCount()) {
                    boolean z = false;
                    if (!RecyclerView.this.mState.isPreLayout() && RecyclerView.this.mAdapter.getItemViewType(viewHolder.mPosition) != viewHolder.getItemViewType()) {
                        return false;
                    }
                    if (!RecyclerView.this.mAdapter.hasStableIds()) {
                        return true;
                    }
                    if (viewHolder.getItemId() == RecyclerView.this.mAdapter.getItemId(viewHolder.mPosition)) {
                        z = true;
                    }
                    return z;
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Inconsistency detected. Invalid view holder adapter position");
            stringBuilder.append(viewHolder);
            stringBuilder.append(RecyclerView.this.exceptionLabel());
            throw new IndexOutOfBoundsException(stringBuilder.toString());
        }

        private boolean tryBindViewHolderByDeadline(ViewHolder viewHolder, int i, int i2, long j) {
            viewHolder.mOwnerRecyclerView = RecyclerView.this;
            int itemViewType = viewHolder.getItemViewType();
            long nanoTime = RecyclerView.this.getNanoTime();
            if (j != Long.MAX_VALUE && this.mRecyclerPool.willBindInTime(itemViewType, nanoTime, j) == null) {
                return null;
            }
            RecyclerView.this.mAdapter.bindViewHolder(viewHolder, i);
            this.mRecyclerPool.factorInBindTime(viewHolder.getItemViewType(), RecyclerView.this.getNanoTime() - nanoTime);
            attachAccessibilityDelegateOnBind(viewHolder);
            if (RecyclerView.this.mState.isPreLayout() != 0) {
                viewHolder.mPreLayoutPosition = i2;
            }
            return true;
        }

        public void bindViewToPosition(View view, int i) {
            view = RecyclerView.getChildViewHolderInt(view);
            if (view == null) {
                i = new StringBuilder();
                i.append("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter");
                i.append(RecyclerView.this.exceptionLabel());
                throw new IllegalArgumentException(i.toString());
            }
            int findPositionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
            if (findPositionOffset >= 0) {
                if (findPositionOffset < RecyclerView.this.mAdapter.getItemCount()) {
                    tryBindViewHolderByDeadline(view, findPositionOffset, i, Long.MAX_VALUE);
                    android.view.ViewGroup.LayoutParams layoutParams = view.itemView.getLayoutParams();
                    if (layoutParams == 0) {
                        i = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                        view.itemView.setLayoutParams(i);
                    } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                        i = (LayoutParams) layoutParams;
                    } else {
                        i = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                        view.itemView.setLayoutParams(i);
                    }
                    boolean z = true;
                    i.mInsetsDirty = true;
                    i.mViewHolder = view;
                    if (view.itemView.getParent() != null) {
                        z = false;
                    }
                    i.mPendingInvalidate = z;
                    return;
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Inconsistency detected. Invalid item position ");
            stringBuilder.append(i);
            stringBuilder.append("(offset:");
            stringBuilder.append(findPositionOffset);
            stringBuilder.append(").state:");
            stringBuilder.append(RecyclerView.this.mState.getItemCount());
            stringBuilder.append(RecyclerView.this.exceptionLabel());
            throw new IndexOutOfBoundsException(stringBuilder.toString());
        }

        public int convertPreLayoutPositionToPostLayout(int i) {
            if (i >= 0) {
                if (i < RecyclerView.this.mState.getItemCount()) {
                    if (RecyclerView.this.mState.isPreLayout()) {
                        return RecyclerView.this.mAdapterHelper.findPositionOffset(i);
                    }
                    return i;
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("invalid position ");
            stringBuilder.append(i);
            stringBuilder.append(". State item count is ");
            stringBuilder.append(RecyclerView.this.mState.getItemCount());
            stringBuilder.append(RecyclerView.this.exceptionLabel());
            throw new IndexOutOfBoundsException(stringBuilder.toString());
        }

        public View getViewForPosition(int i) {
            return getViewForPosition(i, false);
        }

        View getViewForPosition(int i, boolean z) {
            return tryGetViewHolderForPositionByDeadline(i, z, Long.MAX_VALUE).itemView;
        }

        ViewHolder tryGetViewHolderForPositionByDeadline(int i, boolean z, long j) {
            StringBuilder stringBuilder;
            Recycler recycler = this;
            int i2 = i;
            boolean z2 = z;
            if (i2 >= 0) {
                if (i2 < RecyclerView.this.mState.getItemCount()) {
                    ViewHolder changedScrapViewForPosition;
                    boolean z3;
                    int findPositionOffset;
                    int itemViewType;
                    View viewForPositionAndType;
                    long nanoTime;
                    ViewHolder createViewHolder;
                    RecyclerView findNestedRecyclerView;
                    ViewHolder viewHolder;
                    boolean z4;
                    android.view.ViewGroup.LayoutParams layoutParams;
                    LayoutParams layoutParams2;
                    boolean z5 = true;
                    if (RecyclerView.this.mState.isPreLayout()) {
                        changedScrapViewForPosition = getChangedScrapViewForPosition(i);
                        if (changedScrapViewForPosition != null) {
                            z3 = true;
                            if (changedScrapViewForPosition == null) {
                                changedScrapViewForPosition = getScrapOrHiddenOrCachedHolderForPosition(i, z);
                                if (changedScrapViewForPosition != null) {
                                    if (validateViewHolderForOffsetPosition(changedScrapViewForPosition)) {
                                        if (!z2) {
                                            changedScrapViewForPosition.addFlags(4);
                                            if (changedScrapViewForPosition.isScrap()) {
                                                RecyclerView.this.removeDetachedView(changedScrapViewForPosition.itemView, false);
                                                changedScrapViewForPosition.unScrap();
                                            } else if (changedScrapViewForPosition.wasReturnedFromScrap()) {
                                                changedScrapViewForPosition.clearReturnedFromScrapFlag();
                                            }
                                            recycleViewHolderInternal(changedScrapViewForPosition);
                                        }
                                        changedScrapViewForPosition = null;
                                    } else {
                                        z3 = true;
                                    }
                                }
                            }
                            if (changedScrapViewForPosition == null) {
                                findPositionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(i2);
                                if (findPositionOffset >= 0) {
                                    if (findPositionOffset >= RecyclerView.this.mAdapter.getItemCount()) {
                                        itemViewType = RecyclerView.this.mAdapter.getItemViewType(findPositionOffset);
                                        if (RecyclerView.this.mAdapter.hasStableIds()) {
                                            changedScrapViewForPosition = getScrapOrCachedViewForId(RecyclerView.this.mAdapter.getItemId(findPositionOffset), itemViewType, z2);
                                            if (changedScrapViewForPosition != null) {
                                                changedScrapViewForPosition.mPosition = findPositionOffset;
                                                z3 = true;
                                            }
                                        }
                                        if (changedScrapViewForPosition == null && recycler.mViewCacheExtension != null) {
                                            viewForPositionAndType = recycler.mViewCacheExtension.getViewForPositionAndType(recycler, i2, itemViewType);
                                            if (viewForPositionAndType != null) {
                                                changedScrapViewForPosition = RecyclerView.this.getChildViewHolder(viewForPositionAndType);
                                                if (changedScrapViewForPosition == null) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("getViewForPositionAndType returned a view which does not have a ViewHolder");
                                                    stringBuilder.append(RecyclerView.this.exceptionLabel());
                                                    throw new IllegalArgumentException(stringBuilder.toString());
                                                } else if (changedScrapViewForPosition.shouldIgnore()) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view.");
                                                    stringBuilder.append(RecyclerView.this.exceptionLabel());
                                                    throw new IllegalArgumentException(stringBuilder.toString());
                                                }
                                            }
                                        }
                                        if (changedScrapViewForPosition == null) {
                                            changedScrapViewForPosition = getRecycledViewPool().getRecycledView(itemViewType);
                                            if (changedScrapViewForPosition != null) {
                                                changedScrapViewForPosition.resetInternal();
                                                if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST) {
                                                    invalidateDisplayListInt(changedScrapViewForPosition);
                                                }
                                            }
                                        }
                                        if (changedScrapViewForPosition == null) {
                                            nanoTime = RecyclerView.this.getNanoTime();
                                            if (j == Long.MAX_VALUE && !recycler.mRecyclerPool.willCreateInTime(itemViewType, nanoTime, j)) {
                                                return null;
                                            }
                                            createViewHolder = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, itemViewType);
                                            if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                                                findNestedRecyclerView = RecyclerView.findNestedRecyclerView(createViewHolder.itemView);
                                                if (findNestedRecyclerView != null) {
                                                    createViewHolder.mNestedRecyclerView = new WeakReference(findNestedRecyclerView);
                                                }
                                            }
                                            recycler.mRecyclerPool.factorInCreateTime(itemViewType, RecyclerView.this.getNanoTime() - nanoTime);
                                            viewHolder = createViewHolder;
                                            z4 = z3;
                                            if (z4 && !RecyclerView.this.mState.isPreLayout() && viewHolder.hasAnyOfTheFlags(MessagesController.UPDATE_MASK_CHANNEL)) {
                                                viewHolder.setFlags(0, MessagesController.UPDATE_MASK_CHANNEL);
                                                if (RecyclerView.this.mState.mRunSimpleAnimations) {
                                                    RecyclerView.this.recordAnimationInfoIfBouncedHiddenView(viewHolder, RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, viewHolder, ItemAnimator.buildAdapterChangeFlagsForAnimations(viewHolder) | 4096, viewHolder.getUnmodifiedPayloads()));
                                                }
                                            }
                                            if (RecyclerView.this.mState.isPreLayout() || !viewHolder.isBound()) {
                                                if (viewHolder.isBound() && !viewHolder.needsUpdate()) {
                                                    if (viewHolder.isInvalid()) {
                                                    }
                                                }
                                                z2 = tryBindViewHolderByDeadline(viewHolder, RecyclerView.this.mAdapterHelper.findPositionOffset(i2), i2, j);
                                                layoutParams = viewHolder.itemView.getLayoutParams();
                                                if (layoutParams != null) {
                                                    layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                                                    viewHolder.itemView.setLayoutParams(layoutParams2);
                                                } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                                                    layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                                                    viewHolder.itemView.setLayoutParams(layoutParams2);
                                                } else {
                                                    layoutParams2 = (LayoutParams) layoutParams;
                                                }
                                                layoutParams2.mViewHolder = viewHolder;
                                                if (z4 || !r0) {
                                                    z5 = false;
                                                }
                                                layoutParams2.mPendingInvalidate = z5;
                                                return viewHolder;
                                            }
                                            viewHolder.mPreLayoutPosition = i2;
                                            z2 = false;
                                            layoutParams = viewHolder.itemView.getLayoutParams();
                                            if (layoutParams != null) {
                                                layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                                                viewHolder.itemView.setLayoutParams(layoutParams2);
                                            } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                                                layoutParams2 = (LayoutParams) layoutParams;
                                            } else {
                                                layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                                                viewHolder.itemView.setLayoutParams(layoutParams2);
                                            }
                                            layoutParams2.mViewHolder = viewHolder;
                                            if (z4) {
                                            }
                                            z5 = false;
                                            layoutParams2.mPendingInvalidate = z5;
                                            return viewHolder;
                                        }
                                    }
                                }
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("Inconsistency detected. Invalid item position ");
                                stringBuilder.append(i2);
                                stringBuilder.append("(offset:");
                                stringBuilder.append(findPositionOffset);
                                stringBuilder.append(").state:");
                                stringBuilder.append(RecyclerView.this.mState.getItemCount());
                                stringBuilder.append(RecyclerView.this.exceptionLabel());
                                throw new IndexOutOfBoundsException(stringBuilder.toString());
                            }
                            viewHolder = changedScrapViewForPosition;
                            z4 = z3;
                            viewHolder.setFlags(0, MessagesController.UPDATE_MASK_CHANNEL);
                            if (RecyclerView.this.mState.mRunSimpleAnimations) {
                                RecyclerView.this.recordAnimationInfoIfBouncedHiddenView(viewHolder, RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, viewHolder, ItemAnimator.buildAdapterChangeFlagsForAnimations(viewHolder) | 4096, viewHolder.getUnmodifiedPayloads()));
                            }
                            if (RecyclerView.this.mState.isPreLayout()) {
                            }
                            if (viewHolder.isInvalid()) {
                                z2 = tryBindViewHolderByDeadline(viewHolder, RecyclerView.this.mAdapterHelper.findPositionOffset(i2), i2, j);
                                layoutParams = viewHolder.itemView.getLayoutParams();
                                if (layoutParams != null) {
                                    layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                                    viewHolder.itemView.setLayoutParams(layoutParams2);
                                } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                                    layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                                    viewHolder.itemView.setLayoutParams(layoutParams2);
                                } else {
                                    layoutParams2 = (LayoutParams) layoutParams;
                                }
                                layoutParams2.mViewHolder = viewHolder;
                                if (z4) {
                                }
                                z5 = false;
                                layoutParams2.mPendingInvalidate = z5;
                                return viewHolder;
                            }
                            z2 = false;
                            layoutParams = viewHolder.itemView.getLayoutParams();
                            if (layoutParams != null) {
                                layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                                viewHolder.itemView.setLayoutParams(layoutParams2);
                            } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                                layoutParams2 = (LayoutParams) layoutParams;
                            } else {
                                layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                                viewHolder.itemView.setLayoutParams(layoutParams2);
                            }
                            layoutParams2.mViewHolder = viewHolder;
                            if (z4) {
                            }
                            z5 = false;
                            layoutParams2.mPendingInvalidate = z5;
                            return viewHolder;
                        }
                    }
                    changedScrapViewForPosition = null;
                    z3 = false;
                    if (changedScrapViewForPosition == null) {
                        changedScrapViewForPosition = getScrapOrHiddenOrCachedHolderForPosition(i, z);
                        if (changedScrapViewForPosition != null) {
                            if (validateViewHolderForOffsetPosition(changedScrapViewForPosition)) {
                                z3 = true;
                            } else {
                                if (z2) {
                                    changedScrapViewForPosition.addFlags(4);
                                    if (changedScrapViewForPosition.isScrap()) {
                                        RecyclerView.this.removeDetachedView(changedScrapViewForPosition.itemView, false);
                                        changedScrapViewForPosition.unScrap();
                                    } else if (changedScrapViewForPosition.wasReturnedFromScrap()) {
                                        changedScrapViewForPosition.clearReturnedFromScrapFlag();
                                    }
                                    recycleViewHolderInternal(changedScrapViewForPosition);
                                }
                                changedScrapViewForPosition = null;
                            }
                        }
                    }
                    if (changedScrapViewForPosition == null) {
                        findPositionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(i2);
                        if (findPositionOffset >= 0) {
                            if (findPositionOffset >= RecyclerView.this.mAdapter.getItemCount()) {
                                itemViewType = RecyclerView.this.mAdapter.getItemViewType(findPositionOffset);
                                if (RecyclerView.this.mAdapter.hasStableIds()) {
                                    changedScrapViewForPosition = getScrapOrCachedViewForId(RecyclerView.this.mAdapter.getItemId(findPositionOffset), itemViewType, z2);
                                    if (changedScrapViewForPosition != null) {
                                        changedScrapViewForPosition.mPosition = findPositionOffset;
                                        z3 = true;
                                    }
                                }
                                viewForPositionAndType = recycler.mViewCacheExtension.getViewForPositionAndType(recycler, i2, itemViewType);
                                if (viewForPositionAndType != null) {
                                    changedScrapViewForPosition = RecyclerView.this.getChildViewHolder(viewForPositionAndType);
                                    if (changedScrapViewForPosition == null) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("getViewForPositionAndType returned a view which does not have a ViewHolder");
                                        stringBuilder.append(RecyclerView.this.exceptionLabel());
                                        throw new IllegalArgumentException(stringBuilder.toString());
                                    } else if (changedScrapViewForPosition.shouldIgnore()) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view.");
                                        stringBuilder.append(RecyclerView.this.exceptionLabel());
                                        throw new IllegalArgumentException(stringBuilder.toString());
                                    }
                                }
                                if (changedScrapViewForPosition == null) {
                                    changedScrapViewForPosition = getRecycledViewPool().getRecycledView(itemViewType);
                                    if (changedScrapViewForPosition != null) {
                                        changedScrapViewForPosition.resetInternal();
                                        if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST) {
                                            invalidateDisplayListInt(changedScrapViewForPosition);
                                        }
                                    }
                                }
                                if (changedScrapViewForPosition == null) {
                                    nanoTime = RecyclerView.this.getNanoTime();
                                    if (j == Long.MAX_VALUE) {
                                    }
                                    createViewHolder = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, itemViewType);
                                    if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                                        findNestedRecyclerView = RecyclerView.findNestedRecyclerView(createViewHolder.itemView);
                                        if (findNestedRecyclerView != null) {
                                            createViewHolder.mNestedRecyclerView = new WeakReference(findNestedRecyclerView);
                                        }
                                    }
                                    recycler.mRecyclerPool.factorInCreateTime(itemViewType, RecyclerView.this.getNanoTime() - nanoTime);
                                    viewHolder = createViewHolder;
                                    z4 = z3;
                                    viewHolder.setFlags(0, MessagesController.UPDATE_MASK_CHANNEL);
                                    if (RecyclerView.this.mState.mRunSimpleAnimations) {
                                        RecyclerView.this.recordAnimationInfoIfBouncedHiddenView(viewHolder, RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, viewHolder, ItemAnimator.buildAdapterChangeFlagsForAnimations(viewHolder) | 4096, viewHolder.getUnmodifiedPayloads()));
                                    }
                                    if (RecyclerView.this.mState.isPreLayout()) {
                                    }
                                    if (viewHolder.isInvalid()) {
                                        z2 = tryBindViewHolderByDeadline(viewHolder, RecyclerView.this.mAdapterHelper.findPositionOffset(i2), i2, j);
                                        layoutParams = viewHolder.itemView.getLayoutParams();
                                        if (layoutParams != null) {
                                            layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                                            viewHolder.itemView.setLayoutParams(layoutParams2);
                                        } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                                            layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                                            viewHolder.itemView.setLayoutParams(layoutParams2);
                                        } else {
                                            layoutParams2 = (LayoutParams) layoutParams;
                                        }
                                        layoutParams2.mViewHolder = viewHolder;
                                        if (z4) {
                                        }
                                        z5 = false;
                                        layoutParams2.mPendingInvalidate = z5;
                                        return viewHolder;
                                    }
                                    z2 = false;
                                    layoutParams = viewHolder.itemView.getLayoutParams();
                                    if (layoutParams != null) {
                                        layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                                        viewHolder.itemView.setLayoutParams(layoutParams2);
                                    } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                                        layoutParams2 = (LayoutParams) layoutParams;
                                    } else {
                                        layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                                        viewHolder.itemView.setLayoutParams(layoutParams2);
                                    }
                                    layoutParams2.mViewHolder = viewHolder;
                                    if (z4) {
                                    }
                                    z5 = false;
                                    layoutParams2.mPendingInvalidate = z5;
                                    return viewHolder;
                                }
                            }
                        }
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Inconsistency detected. Invalid item position ");
                        stringBuilder.append(i2);
                        stringBuilder.append("(offset:");
                        stringBuilder.append(findPositionOffset);
                        stringBuilder.append(").state:");
                        stringBuilder.append(RecyclerView.this.mState.getItemCount());
                        stringBuilder.append(RecyclerView.this.exceptionLabel());
                        throw new IndexOutOfBoundsException(stringBuilder.toString());
                    }
                    viewHolder = changedScrapViewForPosition;
                    z4 = z3;
                    viewHolder.setFlags(0, MessagesController.UPDATE_MASK_CHANNEL);
                    if (RecyclerView.this.mState.mRunSimpleAnimations) {
                        RecyclerView.this.recordAnimationInfoIfBouncedHiddenView(viewHolder, RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, viewHolder, ItemAnimator.buildAdapterChangeFlagsForAnimations(viewHolder) | 4096, viewHolder.getUnmodifiedPayloads()));
                    }
                    if (RecyclerView.this.mState.isPreLayout()) {
                    }
                    if (viewHolder.isInvalid()) {
                        z2 = tryBindViewHolderByDeadline(viewHolder, RecyclerView.this.mAdapterHelper.findPositionOffset(i2), i2, j);
                        layoutParams = viewHolder.itemView.getLayoutParams();
                        if (layoutParams != null) {
                            layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                            viewHolder.itemView.setLayoutParams(layoutParams2);
                        } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                            layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                            viewHolder.itemView.setLayoutParams(layoutParams2);
                        } else {
                            layoutParams2 = (LayoutParams) layoutParams;
                        }
                        layoutParams2.mViewHolder = viewHolder;
                        if (z4) {
                        }
                        z5 = false;
                        layoutParams2.mPendingInvalidate = z5;
                        return viewHolder;
                    }
                    z2 = false;
                    layoutParams = viewHolder.itemView.getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                        viewHolder.itemView.setLayoutParams(layoutParams2);
                    } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                        layoutParams2 = (LayoutParams) layoutParams;
                    } else {
                        layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                        viewHolder.itemView.setLayoutParams(layoutParams2);
                    }
                    layoutParams2.mViewHolder = viewHolder;
                    if (z4) {
                    }
                    z5 = false;
                    layoutParams2.mPendingInvalidate = z5;
                    return viewHolder;
                }
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid item position ");
            stringBuilder.append(i2);
            stringBuilder.append("(");
            stringBuilder.append(i2);
            stringBuilder.append("). Item count:");
            stringBuilder.append(RecyclerView.this.mState.getItemCount());
            stringBuilder.append(RecyclerView.this.exceptionLabel());
            throw new IndexOutOfBoundsException(stringBuilder.toString());
        }

        private void attachAccessibilityDelegateOnBind(ViewHolder viewHolder) {
            if (RecyclerView.this.isAccessibilityEnabled()) {
                View view = viewHolder.itemView;
                if (ViewCompat.getImportantForAccessibility(view) == 0) {
                    ViewCompat.setImportantForAccessibility(view, 1);
                }
                if (!ViewCompat.hasAccessibilityDelegate(view)) {
                    viewHolder.addFlags(MessagesController.UPDATE_MASK_CHAT_ADMINS);
                    ViewCompat.setAccessibilityDelegate(view, RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
                }
            }
        }

        private void invalidateDisplayListInt(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ViewGroup) {
                invalidateDisplayListInt((ViewGroup) viewHolder.itemView, false);
            }
        }

        private void invalidateDisplayListInt(ViewGroup viewGroup, boolean z) {
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                if (childAt instanceof ViewGroup) {
                    invalidateDisplayListInt((ViewGroup) childAt, true);
                }
            }
            if (z) {
                if (viewGroup.getVisibility()) {
                    viewGroup.setVisibility(false);
                    viewGroup.setVisibility(4);
                } else {
                    z = viewGroup.getVisibility();
                    viewGroup.setVisibility(4);
                    viewGroup.setVisibility(z);
                }
            }
        }

        public void recycleView(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(view, false);
            }
            if (childViewHolderInt.isScrap() != null) {
                childViewHolderInt.unScrap();
            } else if (childViewHolderInt.wasReturnedFromScrap() != null) {
                childViewHolderInt.clearReturnedFromScrapFlag();
            }
            recycleViewHolderInternal(childViewHolderInt);
        }

        void recycleViewInternal(View view) {
            recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(view));
        }

        void recycleAndClearCachedViews() {
            for (int size = this.mCachedViews.size() - 1; size >= 0; size--) {
                recycleCachedViewAt(size);
            }
            this.mCachedViews.clear();
            if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
            }
        }

        void recycleCachedViewAt(int i) {
            addViewHolderToRecycledViewPool((ViewHolder) this.mCachedViews.get(i), true);
            this.mCachedViews.remove(i);
        }

        void recycleViewHolderInternal(ViewHolder viewHolder) {
            boolean z = false;
            if (!viewHolder.isScrap()) {
                if (viewHolder.itemView.getParent() == null) {
                    if (viewHolder.isTmpDetached()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Tmp detached view should be removed from RecyclerView before it can be recycled: ");
                        stringBuilder.append(viewHolder);
                        stringBuilder.append(RecyclerView.this.exceptionLabel());
                        throw new IllegalArgumentException(stringBuilder.toString());
                    } else if (viewHolder.shouldIgnore()) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle.");
                        stringBuilder2.append(RecyclerView.this.exceptionLabel());
                        throw new IllegalArgumentException(stringBuilder2.toString());
                    } else {
                        boolean access$900 = viewHolder.doesTransientStatePreventRecycling();
                        boolean z2 = RecyclerView.this.mAdapter != null && access$900 && RecyclerView.this.mAdapter.onFailedToRecycleView(viewHolder);
                        if (!z2) {
                            if (!viewHolder.isRecyclable()) {
                                z2 = false;
                                RecyclerView.this.mViewInfoStore.removeViewHolder(viewHolder);
                                if (!z2 && !r1 && access$900) {
                                    viewHolder.mOwnerRecyclerView = null;
                                    return;
                                }
                                return;
                            }
                        }
                        if (this.mViewCacheMax <= 0 || viewHolder.hasAnyOfTheFlags(526)) {
                            z2 = false;
                        } else {
                            int size = this.mCachedViews.size();
                            if (size >= this.mViewCacheMax && size > 0) {
                                recycleCachedViewAt(0);
                                size--;
                            }
                            if (RecyclerView.ALLOW_THREAD_GAP_WORK && size > 0 && !RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(viewHolder.mPosition)) {
                                size--;
                                while (size >= 0) {
                                    if (!RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(((ViewHolder) this.mCachedViews.get(size)).mPosition)) {
                                        break;
                                    }
                                    size--;
                                }
                                size++;
                            }
                            this.mCachedViews.add(size, viewHolder);
                            z2 = true;
                        }
                        if (!z2) {
                            addViewHolderToRecycledViewPool(viewHolder, true);
                            z = true;
                        }
                        RecyclerView.this.mViewInfoStore.removeViewHolder(viewHolder);
                        if (!z2) {
                            return;
                        }
                        return;
                    }
                }
            }
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Scrapped or attached views may not be recycled. isScrap:");
            stringBuilder3.append(viewHolder.isScrap());
            stringBuilder3.append(" isAttached:");
            if (viewHolder.itemView.getParent() != null) {
                z = true;
            }
            stringBuilder3.append(z);
            stringBuilder3.append(RecyclerView.this.exceptionLabel());
            throw new IllegalArgumentException(stringBuilder3.toString());
        }

        void addViewHolderToRecycledViewPool(ViewHolder viewHolder, boolean z) {
            RecyclerView.clearNestedRecyclerViewIfNotNested(viewHolder);
            if (viewHolder.hasAnyOfTheFlags(MessagesController.UPDATE_MASK_CHAT_ADMINS)) {
                viewHolder.setFlags(0, MessagesController.UPDATE_MASK_CHAT_ADMINS);
                ViewCompat.setAccessibilityDelegate(viewHolder.itemView, null);
            }
            if (z) {
                dispatchViewRecycled(viewHolder);
            }
            viewHolder.mOwnerRecyclerView = null;
            getRecycledViewPool().putRecycledView(viewHolder);
        }

        void quickRecycleScrapView(View view) {
            view = RecyclerView.getChildViewHolderInt(view);
            view.mScrapContainer = null;
            view.mInChangeScrap = false;
            view.clearReturnedFromScrapFlag();
            recycleViewHolderInternal(view);
        }

        void scrapView(View view) {
            view = RecyclerView.getChildViewHolderInt(view);
            if (!view.hasAnyOfTheFlags(12) && view.isUpdated()) {
                if (!RecyclerView.this.canReuseUpdatedViewHolder(view)) {
                    if (this.mChangedScrap == null) {
                        this.mChangedScrap = new ArrayList();
                    }
                    view.setScrapContainer(this, true);
                    this.mChangedScrap.add(view);
                    return;
                }
            }
            if (!view.isInvalid() || view.isRemoved() || RecyclerView.this.mAdapter.hasStableIds()) {
                view.setScrapContainer(this, false);
                this.mAttachedScrap.add(view);
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool.");
            stringBuilder.append(RecyclerView.this.exceptionLabel());
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        void unscrapView(ViewHolder viewHolder) {
            if (viewHolder.mInChangeScrap) {
                this.mChangedScrap.remove(viewHolder);
            } else {
                this.mAttachedScrap.remove(viewHolder);
            }
            viewHolder.mScrapContainer = null;
            viewHolder.mInChangeScrap = false;
            viewHolder.clearReturnedFromScrapFlag();
        }

        int getScrapCount() {
            return this.mAttachedScrap.size();
        }

        View getScrapViewAt(int i) {
            return ((ViewHolder) this.mAttachedScrap.get(i)).itemView;
        }

        void clearScrap() {
            this.mAttachedScrap.clear();
            if (this.mChangedScrap != null) {
                this.mChangedScrap.clear();
            }
        }

        ViewHolder getChangedScrapViewForPosition(int i) {
            if (this.mChangedScrap != null) {
                int size = this.mChangedScrap.size();
                if (size != 0) {
                    int i2 = 0;
                    int i3 = 0;
                    while (i3 < size) {
                        ViewHolder viewHolder = (ViewHolder) this.mChangedScrap.get(i3);
                        if (viewHolder.wasReturnedFromScrap() || viewHolder.getLayoutPosition() != i) {
                            i3++;
                        } else {
                            viewHolder.addFlags(32);
                            return viewHolder;
                        }
                    }
                    if (RecyclerView.this.mAdapter.hasStableIds()) {
                        i = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
                        if (i > 0 && i < RecyclerView.this.mAdapter.getItemCount()) {
                            long itemId = RecyclerView.this.mAdapter.getItemId(i);
                            while (i2 < size) {
                                ViewHolder viewHolder2 = (ViewHolder) this.mChangedScrap.get(i2);
                                if (viewHolder2.wasReturnedFromScrap() || viewHolder2.getItemId() != itemId) {
                                    i2++;
                                } else {
                                    viewHolder2.addFlags(32);
                                    return viewHolder2;
                                }
                            }
                        }
                    }
                    return null;
                }
            }
            return null;
        }

        ViewHolder getScrapOrHiddenOrCachedHolderForPosition(int i, boolean z) {
            int size = this.mAttachedScrap.size();
            int i2 = 0;
            int i3 = 0;
            while (i3 < size) {
                ViewHolder viewHolder = (ViewHolder) this.mAttachedScrap.get(i3);
                if (viewHolder.wasReturnedFromScrap() || viewHolder.getLayoutPosition() != i || viewHolder.isInvalid() || (!RecyclerView.this.mState.mInPreLayout && viewHolder.isRemoved())) {
                    i3++;
                } else {
                    viewHolder.addFlags(32);
                    return viewHolder;
                }
            }
            if (!z) {
                View findHiddenNonRemovedView = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(i);
                if (findHiddenNonRemovedView != null) {
                    i = RecyclerView.getChildViewHolderInt(findHiddenNonRemovedView);
                    RecyclerView.this.mChildHelper.unhide(findHiddenNonRemovedView);
                    z = RecyclerView.this.mChildHelper.indexOfChild(findHiddenNonRemovedView);
                    if (z) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("layout index should not be -1 after unhiding a view:");
                        stringBuilder.append(i);
                        stringBuilder.append(RecyclerView.this.exceptionLabel());
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                    RecyclerView.this.mChildHelper.detachViewFromParent(z);
                    scrapView(findHiddenNonRemovedView);
                    i.addFlags(true);
                    return i;
                }
            }
            size = this.mCachedViews.size();
            while (i2 < size) {
                ViewHolder viewHolder2 = (ViewHolder) this.mCachedViews.get(i2);
                if (viewHolder2.isInvalid() || viewHolder2.getLayoutPosition() != i) {
                    i2++;
                } else {
                    if (!z) {
                        this.mCachedViews.remove(i2);
                    }
                    return viewHolder2;
                }
            }
            return 0;
        }

        ViewHolder getScrapOrCachedViewForId(long j, int i, boolean z) {
            int size;
            for (size = this.mAttachedScrap.size() - 1; size >= 0; size--) {
                ViewHolder viewHolder = (ViewHolder) this.mAttachedScrap.get(size);
                if (viewHolder.getItemId() == j && !viewHolder.wasReturnedFromScrap()) {
                    if (i == viewHolder.getItemViewType()) {
                        viewHolder.addFlags(32);
                        if (viewHolder.isRemoved() != null && RecyclerView.this.mState.isPreLayout() == null) {
                            viewHolder.setFlags(2, 14);
                        }
                        return viewHolder;
                    } else if (!z) {
                        this.mAttachedScrap.remove(size);
                        RecyclerView.this.removeDetachedView(viewHolder.itemView, false);
                        quickRecycleScrapView(viewHolder.itemView);
                    }
                }
            }
            for (size = this.mCachedViews.size() - 1; size >= 0; size--) {
                ViewHolder viewHolder2 = (ViewHolder) this.mCachedViews.get(size);
                if (viewHolder2.getItemId() == j) {
                    if (i == viewHolder2.getItemViewType()) {
                        if (!z) {
                            this.mCachedViews.remove(size);
                        }
                        return viewHolder2;
                    } else if (!z) {
                        recycleCachedViewAt(size);
                        return null;
                    }
                }
            }
            return null;
        }

        void dispatchViewRecycled(ViewHolder viewHolder) {
            if (RecyclerView.this.mRecyclerListener != null) {
                RecyclerView.this.mRecyclerListener.onViewRecycled(viewHolder);
            }
            if (RecyclerView.this.mAdapter != null) {
                RecyclerView.this.mAdapter.onViewRecycled(viewHolder);
            }
            if (RecyclerView.this.mState != null) {
                RecyclerView.this.mViewInfoStore.removeViewHolder(viewHolder);
            }
        }

        void onAdapterChanged(Adapter adapter, Adapter adapter2, boolean z) {
            clear();
            getRecycledViewPool().onAdapterChanged(adapter, adapter2, z);
        }

        void offsetPositionRecordsForMove(int i, int i2) {
            int i3;
            int i4;
            int i5;
            if (i < i2) {
                i3 = i2;
                i4 = -1;
                i5 = i;
            } else {
                i3 = i;
                i4 = 1;
                i5 = i2;
            }
            int size = this.mCachedViews.size();
            for (int i6 = 0; i6 < size; i6++) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(i6);
                if (viewHolder != null && viewHolder.mPosition >= r0) {
                    if (viewHolder.mPosition <= i3) {
                        if (viewHolder.mPosition == i) {
                            viewHolder.offsetPosition(i2 - i, false);
                        } else {
                            viewHolder.offsetPosition(i4, false);
                        }
                    }
                }
            }
        }

        void offsetPositionRecordsForInsert(int i, int i2) {
            int size = this.mCachedViews.size();
            for (int i3 = 0; i3 < size; i3++) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(i3);
                if (viewHolder != null && viewHolder.mPosition >= i) {
                    viewHolder.offsetPosition(i2, true);
                }
            }
        }

        void offsetPositionRecordsForRemove(int i, int i2, boolean z) {
            int i3 = i + i2;
            for (int size = this.mCachedViews.size() - 1; size >= 0; size--) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(size);
                if (viewHolder != null) {
                    if (viewHolder.mPosition >= i3) {
                        viewHolder.offsetPosition(-i2, z);
                    } else if (viewHolder.mPosition >= i) {
                        viewHolder.addFlags(8);
                        recycleCachedViewAt(size);
                    }
                }
            }
        }

        void setViewCacheExtension(ViewCacheExtension viewCacheExtension) {
            this.mViewCacheExtension = viewCacheExtension;
        }

        void setRecycledViewPool(RecycledViewPool recycledViewPool) {
            if (this.mRecyclerPool != null) {
                this.mRecyclerPool.detach();
            }
            this.mRecyclerPool = recycledViewPool;
            if (recycledViewPool != null) {
                this.mRecyclerPool.attach(RecyclerView.this.getAdapter());
            }
        }

        RecycledViewPool getRecycledViewPool() {
            if (this.mRecyclerPool == null) {
                this.mRecyclerPool = new RecycledViewPool();
            }
            return this.mRecyclerPool;
        }

        void viewRangeUpdate(int i, int i2) {
            i2 += i;
            for (int size = this.mCachedViews.size() - 1; size >= 0; size--) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(size);
                if (viewHolder != null) {
                    int i3 = viewHolder.mPosition;
                    if (i3 >= i && i3 < i2) {
                        viewHolder.addFlags(2);
                        recycleCachedViewAt(size);
                    }
                }
            }
        }

        void markKnownViewsInvalid() {
            int size = this.mCachedViews.size();
            for (int i = 0; i < size; i++) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(i);
                if (viewHolder != null) {
                    viewHolder.addFlags(6);
                    viewHolder.addChangePayload(null);
                }
            }
            if (RecyclerView.this.mAdapter == null || !RecyclerView.this.mAdapter.hasStableIds()) {
                recycleAndClearCachedViews();
            }
        }

        void clearOldPositions() {
            int i;
            int size = this.mCachedViews.size();
            int i2 = 0;
            for (i = 0; i < size; i++) {
                ((ViewHolder) this.mCachedViews.get(i)).clearOldPosition();
            }
            size = this.mAttachedScrap.size();
            for (i = 0; i < size; i++) {
                ((ViewHolder) this.mAttachedScrap.get(i)).clearOldPosition();
            }
            if (this.mChangedScrap != null) {
                size = this.mChangedScrap.size();
                while (i2 < size) {
                    ((ViewHolder) this.mChangedScrap.get(i2)).clearOldPosition();
                    i2++;
                }
            }
        }

        void markItemDecorInsetsDirty() {
            int size = this.mCachedViews.size();
            for (int i = 0; i < size; i++) {
                LayoutParams layoutParams = (LayoutParams) ((ViewHolder) this.mCachedViews.get(i)).itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.mInsetsDirty = true;
                }
            }
        }
    }

    public interface RecyclerListener {
        void onViewRecycled(ViewHolder viewHolder);
    }

    public static abstract class SmoothScroller {
        private LayoutManager mLayoutManager;
        private boolean mPendingInitialRun;
        private RecyclerView mRecyclerView;
        private final Action mRecyclingAction = new Action(0, 0);
        private boolean mRunning;
        private int mTargetPosition = -1;
        private View mTargetView;

        public static class Action {
            public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
            private boolean mChanged;
            private int mConsecutiveUpdates;
            private int mDuration;
            private int mDx;
            private int mDy;
            private Interpolator mInterpolator;
            private int mJumpToPosition;

            public Action(int i, int i2) {
                this(i, i2, Integer.MIN_VALUE, null);
            }

            public Action(int i, int i2, int i3) {
                this(i, i2, i3, null);
            }

            public Action(int i, int i2, int i3, Interpolator interpolator) {
                this.mJumpToPosition = -1;
                this.mChanged = false;
                this.mConsecutiveUpdates = 0;
                this.mDx = i;
                this.mDy = i2;
                this.mDuration = i3;
                this.mInterpolator = interpolator;
            }

            public void jumpTo(int i) {
                this.mJumpToPosition = i;
            }

            boolean hasJumpTarget() {
                return this.mJumpToPosition >= 0;
            }

            void runIfNecessary(RecyclerView recyclerView) {
                if (this.mJumpToPosition >= 0) {
                    int i = this.mJumpToPosition;
                    this.mJumpToPosition = -1;
                    recyclerView.jumpToPositionForSmoothScroller(i);
                    this.mChanged = false;
                    return;
                }
                if (this.mChanged) {
                    validate();
                    if (this.mInterpolator != null) {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
                    } else if (this.mDuration == Integer.MIN_VALUE) {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
                    } else {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
                    }
                    this.mConsecutiveUpdates++;
                    if (this.mConsecutiveUpdates > 10) {
                        Log.e(RecyclerView.TAG, "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
                    }
                    this.mChanged = false;
                } else {
                    this.mConsecutiveUpdates = 0;
                }
            }

            private void validate() {
                if (this.mInterpolator != null && this.mDuration < 1) {
                    throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
                } else if (this.mDuration < 1) {
                    throw new IllegalStateException("Scroll duration must be a positive number");
                }
            }

            public int getDx() {
                return this.mDx;
            }

            public void setDx(int i) {
                this.mChanged = true;
                this.mDx = i;
            }

            public int getDy() {
                return this.mDy;
            }

            public void setDy(int i) {
                this.mChanged = true;
                this.mDy = i;
            }

            public int getDuration() {
                return this.mDuration;
            }

            public void setDuration(int i) {
                this.mChanged = true;
                this.mDuration = i;
            }

            public Interpolator getInterpolator() {
                return this.mInterpolator;
            }

            public void setInterpolator(Interpolator interpolator) {
                this.mChanged = true;
                this.mInterpolator = interpolator;
            }

            public void update(int i, int i2, int i3, Interpolator interpolator) {
                this.mDx = i;
                this.mDy = i2;
                this.mDuration = i3;
                this.mInterpolator = interpolator;
                this.mChanged = true;
            }
        }

        public interface ScrollVectorProvider {
            PointF computeScrollVectorForPosition(int i);
        }

        protected abstract void onSeekTargetStep(int i, int i2, State state, Action action);

        protected abstract void onStart();

        protected abstract void onStop();

        protected abstract void onTargetFound(View view, State state, Action action);

        void start(RecyclerView recyclerView, LayoutManager layoutManager) {
            this.mRecyclerView = recyclerView;
            this.mLayoutManager = layoutManager;
            if (this.mTargetPosition == -1) {
                throw new IllegalArgumentException("Invalid target position");
            }
            this.mRecyclerView.mState.mTargetPosition = this.mTargetPosition;
            this.mRunning = true;
            this.mPendingInitialRun = true;
            this.mTargetView = findViewByPosition(getTargetPosition());
            onStart();
            this.mRecyclerView.mViewFlinger.postOnAnimation();
        }

        public void setTargetPosition(int i) {
            this.mTargetPosition = i;
        }

        public LayoutManager getLayoutManager() {
            return this.mLayoutManager;
        }

        protected final void stop() {
            if (this.mRunning) {
                this.mRunning = false;
                onStop();
                this.mRecyclerView.mState.mTargetPosition = -1;
                this.mTargetView = null;
                this.mTargetPosition = -1;
                this.mPendingInitialRun = false;
                this.mLayoutManager.onSmoothScrollerStopped(this);
                this.mLayoutManager = null;
                this.mRecyclerView = null;
            }
        }

        public boolean isPendingInitialRun() {
            return this.mPendingInitialRun;
        }

        public boolean isRunning() {
            return this.mRunning;
        }

        public int getTargetPosition() {
            return this.mTargetPosition;
        }

        private void onAnimation(int i, int i2) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (!this.mRunning || this.mTargetPosition == -1 || recyclerView == null) {
                stop();
            }
            this.mPendingInitialRun = false;
            if (this.mTargetView != null) {
                if (getChildPosition(this.mTargetView) == this.mTargetPosition) {
                    onTargetFound(this.mTargetView, recyclerView.mState, this.mRecyclingAction);
                    this.mRecyclingAction.runIfNecessary(recyclerView);
                    stop();
                } else {
                    Log.e(RecyclerView.TAG, "Passed over target position while smooth scrolling.");
                    this.mTargetView = null;
                }
            }
            if (this.mRunning) {
                onSeekTargetStep(i, i2, recyclerView.mState, this.mRecyclingAction);
                i = this.mRecyclingAction.hasJumpTarget();
                this.mRecyclingAction.runIfNecessary(recyclerView);
                if (i == 0) {
                    return;
                }
                if (this.mRunning != 0) {
                    this.mPendingInitialRun = true;
                    recyclerView.mViewFlinger.postOnAnimation();
                    return;
                }
                stop();
            }
        }

        public int getChildPosition(View view) {
            return this.mRecyclerView.getChildLayoutPosition(view);
        }

        public int getChildCount() {
            return this.mRecyclerView.mLayout.getChildCount();
        }

        public View findViewByPosition(int i) {
            return this.mRecyclerView.mLayout.findViewByPosition(i);
        }

        @Deprecated
        public void instantScrollToPosition(int i) {
            this.mRecyclerView.scrollToPosition(i);
        }

        protected void onChildAttachedToWindow(View view) {
            if (getChildPosition(view) == getTargetPosition()) {
                this.mTargetView = view;
            }
        }

        protected void normalize(PointF pointF) {
            float sqrt = (float) Math.sqrt((double) ((pointF.x * pointF.x) + (pointF.y * pointF.y)));
            pointF.x /= sqrt;
            pointF.y /= sqrt;
        }
    }

    public static class State {
        static final int STEP_ANIMATIONS = 4;
        static final int STEP_LAYOUT = 2;
        static final int STEP_START = 1;
        private SparseArray<Object> mData;
        int mDeletedInvisibleItemCountSincePreviousLayout = 0;
        long mFocusedItemId;
        int mFocusedItemPosition;
        int mFocusedSubChildId;
        boolean mInPreLayout = false;
        boolean mIsMeasuring = false;
        int mItemCount = 0;
        int mLayoutStep = 1;
        int mPreviousLayoutItemCount = 0;
        int mRemainingScrollHorizontal;
        int mRemainingScrollVertical;
        boolean mRunPredictiveAnimations = false;
        boolean mRunSimpleAnimations = false;
        boolean mStructureChanged = false;
        private int mTargetPosition = -1;
        boolean mTrackOldChangeHolders = false;

        @Retention(RetentionPolicy.SOURCE)
        @interface LayoutState {
        }

        void assertLayoutStep(int i) {
            if ((this.mLayoutStep & i) == 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Layout state should be one of ");
                stringBuilder.append(Integer.toBinaryString(i));
                stringBuilder.append(" but it is ");
                stringBuilder.append(Integer.toBinaryString(this.mLayoutStep));
                throw new IllegalStateException(stringBuilder.toString());
            }
        }

        State reset() {
            this.mTargetPosition = -1;
            if (this.mData != null) {
                this.mData.clear();
            }
            this.mItemCount = 0;
            this.mStructureChanged = false;
            this.mIsMeasuring = false;
            return this;
        }

        void prepareForNestedPrefetch(Adapter adapter) {
            this.mLayoutStep = 1;
            this.mItemCount = adapter.getItemCount();
            this.mInPreLayout = false;
            this.mTrackOldChangeHolders = false;
            this.mIsMeasuring = false;
        }

        public boolean isMeasuring() {
            return this.mIsMeasuring;
        }

        public boolean isPreLayout() {
            return this.mInPreLayout;
        }

        public boolean willRunPredictiveAnimations() {
            return this.mRunPredictiveAnimations;
        }

        public boolean willRunSimpleAnimations() {
            return this.mRunSimpleAnimations;
        }

        public void remove(int i) {
            if (this.mData != null) {
                this.mData.remove(i);
            }
        }

        public <T> T get(int i) {
            if (this.mData == null) {
                return null;
            }
            return this.mData.get(i);
        }

        public void put(int i, Object obj) {
            if (this.mData == null) {
                this.mData = new SparseArray();
            }
            this.mData.put(i, obj);
        }

        public int getTargetScrollPosition() {
            return this.mTargetPosition;
        }

        public boolean hasTargetScrollPosition() {
            return this.mTargetPosition != -1;
        }

        public boolean didStructureChange() {
            return this.mStructureChanged;
        }

        public int getItemCount() {
            return this.mInPreLayout ? this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout : this.mItemCount;
        }

        public int getRemainingScrollHorizontal() {
            return this.mRemainingScrollHorizontal;
        }

        public int getRemainingScrollVertical() {
            return this.mRemainingScrollVertical;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("State{mTargetPosition=");
            stringBuilder.append(this.mTargetPosition);
            stringBuilder.append(", mData=");
            stringBuilder.append(this.mData);
            stringBuilder.append(", mItemCount=");
            stringBuilder.append(this.mItemCount);
            stringBuilder.append(", mIsMeasuring=");
            stringBuilder.append(this.mIsMeasuring);
            stringBuilder.append(", mPreviousLayoutItemCount=");
            stringBuilder.append(this.mPreviousLayoutItemCount);
            stringBuilder.append(", mDeletedInvisibleItemCountSincePreviousLayout=");
            stringBuilder.append(this.mDeletedInvisibleItemCountSincePreviousLayout);
            stringBuilder.append(", mStructureChanged=");
            stringBuilder.append(this.mStructureChanged);
            stringBuilder.append(", mInPreLayout=");
            stringBuilder.append(this.mInPreLayout);
            stringBuilder.append(", mRunSimpleAnimations=");
            stringBuilder.append(this.mRunSimpleAnimations);
            stringBuilder.append(", mRunPredictiveAnimations=");
            stringBuilder.append(this.mRunPredictiveAnimations);
            stringBuilder.append('}');
            return stringBuilder.toString();
        }
    }

    public static abstract class ViewCacheExtension {
        public abstract View getViewForPositionAndType(Recycler recycler, int i, int i2);
    }

    class ViewFlinger implements Runnable {
        private boolean mEatRunOnAnimationRequest = false;
        Interpolator mInterpolator = RecyclerView.sQuinticInterpolator;
        private int mLastFlingX;
        private int mLastFlingY;
        private boolean mReSchedulePostAnimationCallback = false;
        private OverScroller mScroller;

        ViewFlinger() {
            this.mScroller = new OverScroller(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
        }

        public void run() {
            if (RecyclerView.this.mLayout == null) {
                stop();
                return;
            }
            disableRunOnAnimationRequests();
            RecyclerView.this.consumePendingUpdateOperations();
            OverScroller overScroller = r0.mScroller;
            SmoothScroller smoothScroller = RecyclerView.this.mLayout.mSmoothScroller;
            if (overScroller.computeScrollOffset()) {
                int scrollHorizontallyBy;
                int i;
                int scrollVerticallyBy;
                int i2;
                int itemCount;
                int[] access$500 = RecyclerView.this.mScrollConsumed;
                int currX = overScroller.getCurrX();
                int currY = overScroller.getCurrY();
                int i3 = currX - r0.mLastFlingX;
                int i4 = currY - r0.mLastFlingY;
                r0.mLastFlingX = currX;
                r0.mLastFlingY = currY;
                if (RecyclerView.this.dispatchNestedPreScroll(i3, i4, access$500, null, 1)) {
                    i3 -= access$500[0];
                    i4 -= access$500[1];
                }
                if (RecyclerView.this.mAdapter != null) {
                    RecyclerView.this.startInterceptRequestLayout();
                    RecyclerView.this.onEnterLayoutOrScroll();
                    TraceCompat.beginSection(RecyclerView.TRACE_SCROLL_TAG);
                    RecyclerView.this.fillRemainingScrollValues(RecyclerView.this.mState);
                    if (i3 != 0) {
                        scrollHorizontallyBy = RecyclerView.this.mLayout.scrollHorizontallyBy(i3, RecyclerView.this.mRecycler, RecyclerView.this.mState);
                        i = i3 - scrollHorizontallyBy;
                    } else {
                        scrollHorizontallyBy = 0;
                        i = scrollHorizontallyBy;
                    }
                    if (i4 != 0) {
                        scrollVerticallyBy = RecyclerView.this.mLayout.scrollVerticallyBy(i4, RecyclerView.this.mRecycler, RecyclerView.this.mState);
                        i2 = i4 - scrollVerticallyBy;
                    } else {
                        scrollVerticallyBy = 0;
                        i2 = scrollVerticallyBy;
                    }
                    TraceCompat.endSection();
                    RecyclerView.this.repositionShadowingViews();
                    RecyclerView.this.onExitLayoutOrScroll();
                    RecyclerView.this.stopInterceptRequestLayout(false);
                    if (!(smoothScroller == null || smoothScroller.isPendingInitialRun() || !smoothScroller.isRunning())) {
                        itemCount = RecyclerView.this.mState.getItemCount();
                        if (itemCount == 0) {
                            smoothScroller.stop();
                        } else if (smoothScroller.getTargetPosition() >= itemCount) {
                            smoothScroller.setTargetPosition(itemCount - 1);
                            smoothScroller.onAnimation(i3 - i, i4 - i2);
                        } else {
                            smoothScroller.onAnimation(i3 - i, i4 - i2);
                        }
                    }
                } else {
                    scrollHorizontallyBy = 0;
                    i = scrollHorizontallyBy;
                    scrollVerticallyBy = i;
                    i2 = scrollVerticallyBy;
                }
                if (!RecyclerView.this.mItemDecorations.isEmpty()) {
                    RecyclerView.this.invalidate();
                }
                if (RecyclerView.this.getOverScrollMode() != 2) {
                    RecyclerView.this.considerReleasingGlowsOnScroll(i3, i4);
                }
                if (!(RecyclerView.this.dispatchNestedScroll(scrollHorizontallyBy, scrollVerticallyBy, i, i2, null, 1) || (i == 0 && i2 == 0))) {
                    int i5;
                    itemCount = (int) overScroller.getCurrVelocity();
                    if (i != currX) {
                        if (i < 0) {
                            i5 = -itemCount;
                        } else if (i > 0) {
                            i5 = itemCount;
                        }
                        if (i2 != currY) {
                            if (i2 < 0) {
                                itemCount = -itemCount;
                            } else if (i2 > 0) {
                            }
                            if (RecyclerView.this.getOverScrollMode() != 2) {
                                RecyclerView.this.absorbGlows(i5, itemCount);
                            }
                            if ((i5 != 0 || i == currX || overScroller.getFinalX() == 0) && (itemCount != 0 || i2 == currY || overScroller.getFinalY() == 0)) {
                                overScroller.abortAnimation();
                            }
                        }
                        itemCount = 0;
                        if (RecyclerView.this.getOverScrollMode() != 2) {
                            RecyclerView.this.absorbGlows(i5, itemCount);
                        }
                        overScroller.abortAnimation();
                    }
                    i5 = 0;
                    if (i2 != currY) {
                        if (i2 < 0) {
                            itemCount = -itemCount;
                        } else if (i2 > 0) {
                        }
                        if (RecyclerView.this.getOverScrollMode() != 2) {
                            RecyclerView.this.absorbGlows(i5, itemCount);
                        }
                        overScroller.abortAnimation();
                    }
                    itemCount = 0;
                    if (RecyclerView.this.getOverScrollMode() != 2) {
                        RecyclerView.this.absorbGlows(i5, itemCount);
                    }
                    overScroller.abortAnimation();
                }
                if (!(scrollHorizontallyBy == 0 && scrollVerticallyBy == 0)) {
                    RecyclerView.this.dispatchOnScrolled(scrollHorizontallyBy, scrollVerticallyBy);
                }
                if (!RecyclerView.this.awakenScrollBars()) {
                    RecyclerView.this.invalidate();
                }
                int i6 = (i4 != 0 && RecyclerView.this.mLayout.canScrollVertically() && scrollVerticallyBy == i4) ? 1 : 0;
                scrollHorizontallyBy = (i3 != 0 && RecyclerView.this.mLayout.canScrollHorizontally() && scrollHorizontallyBy == i3) ? 1 : 0;
                if (!(i3 == 0 && i4 == 0) && scrollHorizontallyBy == 0) {
                    if (i6 == 0) {
                        scrollHorizontallyBy = 0;
                        if (!overScroller.isFinished()) {
                            if (scrollHorizontallyBy == 0 || RecyclerView.this.hasNestedScrollingParent(1)) {
                                postOnAnimation();
                                if (RecyclerView.this.mGapWorker != null) {
                                    RecyclerView.this.mGapWorker.postFromTraversal(RecyclerView.this, i3, i4);
                                }
                            }
                        }
                        RecyclerView.this.setScrollState(0);
                        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                            RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
                        }
                        RecyclerView.this.stopNestedScroll(1);
                    }
                }
                scrollHorizontallyBy = 1;
                if (overScroller.isFinished()) {
                    if (scrollHorizontallyBy == 0) {
                    }
                    postOnAnimation();
                    if (RecyclerView.this.mGapWorker != null) {
                        RecyclerView.this.mGapWorker.postFromTraversal(RecyclerView.this, i3, i4);
                    }
                }
                RecyclerView.this.setScrollState(0);
                if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                    RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
                }
                RecyclerView.this.stopNestedScroll(1);
            }
            if (smoothScroller != null) {
                if (smoothScroller.isPendingInitialRun()) {
                    smoothScroller.onAnimation(0, 0);
                }
                if (!r0.mReSchedulePostAnimationCallback) {
                    smoothScroller.stop();
                }
            }
            enableRunOnAnimationRequests();
        }

        private void disableRunOnAnimationRequests() {
            this.mReSchedulePostAnimationCallback = false;
            this.mEatRunOnAnimationRequest = true;
        }

        private void enableRunOnAnimationRequests() {
            this.mEatRunOnAnimationRequest = false;
            if (this.mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        void postOnAnimation() {
            if (this.mEatRunOnAnimationRequest) {
                this.mReSchedulePostAnimationCallback = true;
                return;
            }
            RecyclerView.this.removeCallbacks(this);
            ViewCompat.postOnAnimation(RecyclerView.this, this);
        }

        public void fling(int i, int i2) {
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mScroller.fling(0, 0, i, i2, Integer.MIN_VALUE, ConnectionsManager.DEFAULT_DATACENTER_ID, Integer.MIN_VALUE, ConnectionsManager.DEFAULT_DATACENTER_ID);
            postOnAnimation();
        }

        public void smoothScrollBy(int i, int i2) {
            smoothScrollBy(i, i2, 0, 0);
        }

        public void smoothScrollBy(int i, int i2, int i3, int i4) {
            smoothScrollBy(i, i2, computeScrollDuration(i, i2, i3, i4));
        }

        private float distanceInfluenceForSnapDuration(float f) {
            return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
        }

        private int computeScrollDuration(int i, int i2, int i3, int i4) {
            int abs = Math.abs(i);
            int abs2 = Math.abs(i2);
            Object obj = abs > abs2 ? 1 : null;
            i3 = (int) Math.sqrt((double) ((i3 * i3) + (i4 * i4)));
            i = (int) Math.sqrt((double) ((i * i) + (i2 * i2)));
            i2 = obj != null ? RecyclerView.this.getWidth() : RecyclerView.this.getHeight();
            i4 = i2 / 2;
            i2 = (float) i2;
            i4 = (float) i4;
            i4 += distanceInfluenceForSnapDuration(Math.min(1.0f, (((float) i) * NUM) / i2)) * i4;
            if (i3 > 0) {
                i = 4 * Math.round(NUM * Math.abs(i4 / ((float) i3)));
            } else {
                if (obj == null) {
                    abs = abs2;
                }
                i = (int) (((((float) abs) / i2) + NUM) * NUM);
            }
            return Math.min(i, 2000);
        }

        public void smoothScrollBy(int i, int i2, int i3) {
            smoothScrollBy(i, i2, i3, RecyclerView.sQuinticInterpolator);
        }

        public void smoothScrollBy(int i, int i2, Interpolator interpolator) {
            int computeScrollDuration = computeScrollDuration(i, i2, 0, 0);
            if (interpolator == null) {
                interpolator = RecyclerView.sQuinticInterpolator;
            }
            smoothScrollBy(i, i2, computeScrollDuration, interpolator);
        }

        public void smoothScrollBy(int i, int i2, int i3, Interpolator interpolator) {
            if (this.mInterpolator != interpolator) {
                this.mInterpolator = interpolator;
                this.mScroller = new OverScroller(RecyclerView.this.getContext(), interpolator);
            }
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mScroller.startScroll(0, 0, i, i2, i3);
            if (VERSION.SDK_INT < 23) {
                this.mScroller.computeScrollOffset();
            }
            postOnAnimation();
        }

        public void stop() {
            RecyclerView.this.removeCallbacks(this);
            this.mScroller.abortAnimation();
        }
    }

    public static abstract class ViewHolder {
        static final int FLAG_ADAPTER_FULLUPDATE = 1024;
        static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
        static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
        static final int FLAG_BOUND = 1;
        static final int FLAG_IGNORE = 128;
        static final int FLAG_INVALID = 4;
        static final int FLAG_MOVED = 2048;
        static final int FLAG_NOT_RECYCLABLE = 16;
        static final int FLAG_REMOVED = 8;
        static final int FLAG_RETURNED_FROM_SCRAP = 32;
        static final int FLAG_SET_A11Y_ITEM_DELEGATE = 16384;
        static final int FLAG_TMP_DETACHED = 256;
        static final int FLAG_UPDATE = 2;
        private static final List<Object> FULLUPDATE_PAYLOADS = Collections.EMPTY_LIST;
        static final int PENDING_ACCESSIBILITY_STATE_NOT_SET = -1;
        public final View itemView;
        private int mFlags;
        private boolean mInChangeScrap = false;
        private int mIsRecyclableCount = 0;
        long mItemId = -1;
        int mItemViewType = -1;
        WeakReference<RecyclerView> mNestedRecyclerView;
        int mOldPosition = -1;
        RecyclerView mOwnerRecyclerView;
        List<Object> mPayloads = null;
        int mPendingAccessibilityState = -1;
        int mPosition = -1;
        int mPreLayoutPosition = -1;
        private Recycler mScrapContainer = null;
        ViewHolder mShadowedHolder = null;
        ViewHolder mShadowingHolder = null;
        List<Object> mUnmodifiedPayloads = null;
        private int mWasImportantForAccessibilityBeforeHidden = 0;

        public ViewHolder(View view) {
            if (view == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = view;
        }

        void flagRemovedAndOffsetPosition(int i, int i2, boolean z) {
            addFlags(8);
            offsetPosition(i2, z);
            this.mPosition = i;
        }

        void offsetPosition(int i, boolean z) {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
            if (this.mPreLayoutPosition == -1) {
                this.mPreLayoutPosition = this.mPosition;
            }
            if (z) {
                this.mPreLayoutPosition += i;
            }
            this.mPosition += i;
            if (this.itemView.getLayoutParams() != 0) {
                ((LayoutParams) this.itemView.getLayoutParams()).mInsetsDirty = true;
            }
        }

        void clearOldPosition() {
            this.mOldPosition = -1;
            this.mPreLayoutPosition = -1;
        }

        void saveOldPosition() {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
        }

        boolean shouldIgnore() {
            return (this.mFlags & 128) != 0;
        }

        @Deprecated
        public final int getPosition() {
            return this.mPreLayoutPosition == -1 ? this.mPosition : this.mPreLayoutPosition;
        }

        public final int getLayoutPosition() {
            return this.mPreLayoutPosition == -1 ? this.mPosition : this.mPreLayoutPosition;
        }

        public final int getAdapterPosition() {
            if (this.mOwnerRecyclerView == null) {
                return -1;
            }
            return this.mOwnerRecyclerView.getAdapterPositionFor(this);
        }

        public final int getOldPosition() {
            return this.mOldPosition;
        }

        public final long getItemId() {
            return this.mItemId;
        }

        public final int getItemViewType() {
            return this.mItemViewType;
        }

        boolean isScrap() {
            return this.mScrapContainer != null;
        }

        void unScrap() {
            this.mScrapContainer.unscrapView(this);
        }

        boolean wasReturnedFromScrap() {
            return (this.mFlags & 32) != 0;
        }

        void clearReturnedFromScrapFlag() {
            this.mFlags &= -33;
        }

        void clearTmpDetachFlag() {
            this.mFlags &= -257;
        }

        void stopIgnoring() {
            this.mFlags &= -129;
        }

        void setScrapContainer(Recycler recycler, boolean z) {
            this.mScrapContainer = recycler;
            this.mInChangeScrap = z;
        }

        boolean isInvalid() {
            return (this.mFlags & 4) != 0;
        }

        boolean needsUpdate() {
            return (this.mFlags & 2) != 0;
        }

        boolean isBound() {
            return (this.mFlags & 1) != 0;
        }

        boolean isRemoved() {
            return (this.mFlags & 8) != 0;
        }

        boolean hasAnyOfTheFlags(int i) {
            return (i & this.mFlags) != 0;
        }

        boolean isTmpDetached() {
            return (this.mFlags & 256) != 0;
        }

        boolean isAdapterPositionUnknown() {
            if ((this.mFlags & 512) == 0) {
                if (!isInvalid()) {
                    return false;
                }
            }
            return true;
        }

        void setFlags(int i, int i2) {
            this.mFlags = (i & i2) | (this.mFlags & (i2 ^ -1));
        }

        void addFlags(int i) {
            this.mFlags = i | this.mFlags;
        }

        void addChangePayload(Object obj) {
            if (obj == null) {
                addFlags(1024);
            } else if ((1024 & this.mFlags) == 0) {
                createPayloadsIfNeeded();
                this.mPayloads.add(obj);
            }
        }

        private void createPayloadsIfNeeded() {
            if (this.mPayloads == null) {
                this.mPayloads = new ArrayList();
                this.mUnmodifiedPayloads = Collections.unmodifiableList(this.mPayloads);
            }
        }

        void clearPayload() {
            if (this.mPayloads != null) {
                this.mPayloads.clear();
            }
            this.mFlags &= -1025;
        }

        List<Object> getUnmodifiedPayloads() {
            if ((this.mFlags & 1024) != 0) {
                return FULLUPDATE_PAYLOADS;
            }
            if (this.mPayloads != null) {
                if (this.mPayloads.size() != 0) {
                    return this.mUnmodifiedPayloads;
                }
            }
            return FULLUPDATE_PAYLOADS;
        }

        void resetInternal() {
            this.mFlags = 0;
            this.mPosition = -1;
            this.mOldPosition = -1;
            this.mItemId = -1;
            this.mPreLayoutPosition = -1;
            this.mIsRecyclableCount = 0;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            clearPayload();
            this.mWasImportantForAccessibilityBeforeHidden = 0;
            this.mPendingAccessibilityState = -1;
            RecyclerView.clearNestedRecyclerViewIfNotNested(this);
        }

        private void onEnteredHiddenState(RecyclerView recyclerView) {
            if (this.mPendingAccessibilityState != -1) {
                this.mWasImportantForAccessibilityBeforeHidden = this.mPendingAccessibilityState;
            } else {
                this.mWasImportantForAccessibilityBeforeHidden = ViewCompat.getImportantForAccessibility(this.itemView);
            }
            recyclerView.setChildImportantForAccessibilityInternal(this, 4);
        }

        private void onLeftHiddenState(RecyclerView recyclerView) {
            recyclerView.setChildImportantForAccessibilityInternal(this, this.mWasImportantForAccessibilityBeforeHidden);
            this.mWasImportantForAccessibilityBeforeHidden = null;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ViewHolder{");
            stringBuilder.append(Integer.toHexString(hashCode()));
            stringBuilder.append(" position=");
            stringBuilder.append(this.mPosition);
            stringBuilder.append(" id=");
            stringBuilder.append(this.mItemId);
            stringBuilder.append(", oldPos=");
            stringBuilder.append(this.mOldPosition);
            stringBuilder.append(", pLpos:");
            stringBuilder.append(this.mPreLayoutPosition);
            StringBuilder stringBuilder2 = new StringBuilder(stringBuilder.toString());
            if (isScrap()) {
                stringBuilder2.append(" scrap ");
                stringBuilder2.append(this.mInChangeScrap ? "[changeScrap]" : "[attachedScrap]");
            }
            if (isInvalid()) {
                stringBuilder2.append(" invalid");
            }
            if (!isBound()) {
                stringBuilder2.append(" unbound");
            }
            if (needsUpdate()) {
                stringBuilder2.append(" update");
            }
            if (isRemoved()) {
                stringBuilder2.append(" removed");
            }
            if (shouldIgnore()) {
                stringBuilder2.append(" ignored");
            }
            if (isTmpDetached()) {
                stringBuilder2.append(" tmpDetached");
            }
            if (!isRecyclable()) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(" not recyclable(");
                stringBuilder.append(this.mIsRecyclableCount);
                stringBuilder.append(")");
                stringBuilder2.append(stringBuilder.toString());
            }
            if (isAdapterPositionUnknown()) {
                stringBuilder2.append(" undefined adapter position");
            }
            if (this.itemView.getParent() == null) {
                stringBuilder2.append(" no parent");
            }
            stringBuilder2.append("}");
            return stringBuilder2.toString();
        }

        public final void setIsRecyclable(boolean z) {
            this.mIsRecyclableCount = z ? this.mIsRecyclableCount - 1 : this.mIsRecyclableCount + 1;
            if (this.mIsRecyclableCount < 0) {
                this.mIsRecyclableCount = false;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for ");
                stringBuilder.append(this);
                Log.e("View", stringBuilder.toString());
            } else if (!z && this.mIsRecyclableCount == 1) {
                this.mFlags |= 16;
            } else if (z && !this.mIsRecyclableCount) {
                this.mFlags &= -17;
            }
        }

        public final boolean isRecyclable() {
            return (this.mFlags & 16) == 0 && !ViewCompat.hasTransientState(this.itemView);
        }

        private boolean shouldBeKeptAsChild() {
            return (this.mFlags & 16) != 0;
        }

        private boolean doesTransientStatePreventRecycling() {
            return (this.mFlags & 16) == 0 && ViewCompat.hasTransientState(this.itemView);
        }

        boolean isUpdated() {
            return (this.mFlags & 2) != 0;
        }
    }

    /* renamed from: org.telegram.messenger.support.widget.RecyclerView$4 */
    class C18694 implements ProcessCallback {
        C18694() {
        }

        public void processDisappeared(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2) {
            RecyclerView.this.mRecycler.unscrapView(viewHolder);
            RecyclerView.this.animateDisappearance(viewHolder, itemHolderInfo, itemHolderInfo2);
        }

        public void processAppeared(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2) {
            RecyclerView.this.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2);
        }

        public void processPersistent(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2) {
            viewHolder.setIsRecyclable(false);
            if (RecyclerView.this.mDataSetHasChangedAfterLayout) {
                if (RecyclerView.this.mItemAnimator.animateChange(viewHolder, viewHolder, itemHolderInfo, itemHolderInfo2) != null) {
                    RecyclerView.this.postAnimationRunner();
                }
            } else if (RecyclerView.this.mItemAnimator.animatePersistence(viewHolder, itemHolderInfo, itemHolderInfo2) != null) {
                RecyclerView.this.postAnimationRunner();
            }
        }

        public void unused(ViewHolder viewHolder) {
            RecyclerView.this.mLayout.removeAndRecycleView(viewHolder.itemView, RecyclerView.this.mRecycler);
        }
    }

    /* renamed from: org.telegram.messenger.support.widget.RecyclerView$5 */
    class C18705 implements Callback {
        C18705() {
        }

        public int getChildCount() {
            return RecyclerView.this.getChildCount();
        }

        public void addView(View view, int i) {
            RecyclerView.this.addView(view, i);
            RecyclerView.this.dispatchChildAttached(view);
        }

        public int indexOfChild(View view) {
            return RecyclerView.this.indexOfChild(view);
        }

        public void removeViewAt(int i) {
            View childAt = RecyclerView.this.getChildAt(i);
            if (childAt != null) {
                RecyclerView.this.dispatchChildDetached(childAt);
                childAt.clearAnimation();
            }
            RecyclerView.this.removeViewAt(i);
        }

        public View getChildAt(int i) {
            return RecyclerView.this.getChildAt(i);
        }

        public void removeAllViews() {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                RecyclerView.this.dispatchChildDetached(childAt);
                childAt.clearAnimation();
            }
            RecyclerView.this.removeAllViews();
        }

        public ViewHolder getChildViewHolder(View view) {
            return RecyclerView.getChildViewHolderInt(view);
        }

        public void attachViewToParent(View view, int i, android.view.ViewGroup.LayoutParams layoutParams) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt != null) {
                if (childViewHolderInt.isTmpDetached() || childViewHolderInt.shouldIgnore()) {
                    childViewHolderInt.clearTmpDetachFlag();
                } else {
                    i = new StringBuilder();
                    i.append("Called attach on a child which is not detached: ");
                    i.append(childViewHolderInt);
                    i.append(RecyclerView.this.exceptionLabel());
                    throw new IllegalArgumentException(i.toString());
                }
            }
            RecyclerView.this.attachViewToParent(view, i, layoutParams);
        }

        public void detachViewFromParent(int i) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(childAt);
                if (childViewHolderInt != null) {
                    if (!childViewHolderInt.isTmpDetached() || childViewHolderInt.shouldIgnore()) {
                        childViewHolderInt.addFlags(256);
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("called detach on an already detached child ");
                        stringBuilder.append(childViewHolderInt);
                        stringBuilder.append(RecyclerView.this.exceptionLabel());
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                }
            }
            RecyclerView.this.detachViewFromParent(i);
        }

        public void onEnteredHiddenState(View view) {
            view = RecyclerView.getChildViewHolderInt(view);
            if (view != null) {
                view.onEnteredHiddenState(RecyclerView.this);
            }
        }

        public void onLeftHiddenState(View view) {
            view = RecyclerView.getChildViewHolderInt(view);
            if (view != null) {
                view.onLeftHiddenState(RecyclerView.this);
            }
        }
    }

    /* renamed from: org.telegram.messenger.support.widget.RecyclerView$6 */
    class C18716 implements Callback {
        C18716() {
        }

        public ViewHolder findViewHolder(int i) {
            i = RecyclerView.this.findViewHolderForPosition(i, true);
            if (i == 0 || RecyclerView.this.mChildHelper.isHidden(i.itemView)) {
                return null;
            }
            return i;
        }

        public void offsetPositionsForRemovingInvisible(int i, int i2) {
            RecyclerView.this.offsetPositionRecordsForRemove(i, i2, true);
            RecyclerView.this.mItemsAddedOrRemoved = true;
            i = RecyclerView.this.mState;
            i.mDeletedInvisibleItemCountSincePreviousLayout += i2;
        }

        public void offsetPositionsForRemovingLaidOutOrNewView(int i, int i2) {
            RecyclerView.this.offsetPositionRecordsForRemove(i, i2, false);
            RecyclerView.this.mItemsAddedOrRemoved = true;
        }

        public void markViewHoldersUpdated(int i, int i2, Object obj) {
            RecyclerView.this.viewRangeUpdate(i, i2, obj);
            RecyclerView.this.mItemsChanged = true;
        }

        public void onDispatchFirstPass(UpdateOp updateOp) {
            dispatchUpdate(updateOp);
        }

        void dispatchUpdate(UpdateOp updateOp) {
            int i = updateOp.cmd;
            if (i == 4) {
                RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, updateOp.positionStart, updateOp.itemCount, updateOp.payload);
            } else if (i != 8) {
                switch (i) {
                    case 1:
                        RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, updateOp.positionStart, updateOp.itemCount);
                        return;
                    case 2:
                        RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, updateOp.positionStart, updateOp.itemCount);
                        return;
                    default:
                        return;
                }
            } else {
                RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, updateOp.positionStart, updateOp.itemCount, 1);
            }
        }

        public void onDispatchSecondPass(UpdateOp updateOp) {
            dispatchUpdate(updateOp);
        }

        public void offsetPositionsForAdd(int i, int i2) {
            RecyclerView.this.offsetPositionRecordsForInsert(i, i2);
            RecyclerView.this.mItemsAddedOrRemoved = true;
        }

        public void offsetPositionsForMove(int i, int i2) {
            RecyclerView.this.offsetPositionRecordsForMove(i, i2);
            RecyclerView.this.mItemsAddedOrRemoved = true;
        }
    }

    private class ItemAnimatorRestoreListener implements ItemAnimatorListener {
        ItemAnimatorRestoreListener() {
        }

        public void onAnimationFinished(ViewHolder viewHolder) {
            viewHolder.setIsRecyclable(true);
            if (viewHolder.mShadowedHolder != null && viewHolder.mShadowingHolder == null) {
                viewHolder.mShadowedHolder = null;
            }
            viewHolder.mShadowingHolder = null;
            if (!viewHolder.shouldBeKeptAsChild() && !RecyclerView.this.removeAnimatingView(viewHolder.itemView) && viewHolder.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(viewHolder.itemView, false);
            }
        }
    }

    private class RecyclerViewDataObserver extends AdapterDataObserver {
        RecyclerViewDataObserver() {
        }

        public void onChanged() {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            RecyclerView.this.mState.mStructureChanged = true;
            RecyclerView.this.processDataSetCompletelyChanged(true);
            if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
                RecyclerView.this.requestLayout();
            }
        }

        public void onItemRangeChanged(int i, int i2, Object obj) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(i, i2, obj) != 0) {
                triggerUpdateProcessor();
            }
        }

        public void onItemRangeInserted(int i, int i2) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(i, i2) != 0) {
                triggerUpdateProcessor();
            }
        }

        public void onItemRangeRemoved(int i, int i2) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(i, i2) != 0) {
                triggerUpdateProcessor();
            }
        }

        public void onItemRangeMoved(int i, int i2, int i3) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(i, i2, i3) != 0) {
                triggerUpdateProcessor();
            }
        }

        void triggerUpdateProcessor() {
            if (RecyclerView.POST_UPDATES_ON_ANIMATION && RecyclerView.this.mHasFixedSize && RecyclerView.this.mIsAttached) {
                ViewCompat.postOnAnimation(RecyclerView.this, RecyclerView.this.mUpdateChildViewsRunnable);
                return;
            }
            RecyclerView.this.mAdapterUpdateDuringMeasure = true;
            RecyclerView.this.requestLayout();
        }
    }

    public static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR = new C06621();
        Parcelable mLayoutState;

        /* renamed from: org.telegram.messenger.support.widget.RecyclerView$SavedState$1 */
        static class C06621 implements ClassLoaderCreator<SavedState> {
            C06621() {
            }

            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        }

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            if (classLoader == null) {
                classLoader = LayoutManager.class.getClassLoader();
            }
            this.mLayoutState = parcel.readParcelable(classLoader);
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeParcelable(this.mLayoutState, 0);
        }

        void copyFrom(SavedState savedState) {
            this.mLayoutState = savedState.mLayoutState;
        }
    }

    public static class SimpleOnItemTouchListener implements OnItemTouchListener {
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            return false;
        }

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
        }

        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        }
    }

    public void onChildAttachedToWindow(View view) {
    }

    public void onChildDetachedFromWindow(View view) {
    }

    public void onScrollStateChanged(int i) {
    }

    public void onScrolled(int i, int i2) {
    }

    static {
        boolean z;
        if (!(VERSION.SDK_INT == 18 || VERSION.SDK_INT == 19)) {
            if (VERSION.SDK_INT != 20) {
                z = false;
                FORCE_INVALIDATE_DISPLAY_LIST = z;
            }
        }
        z = true;
        FORCE_INVALIDATE_DISPLAY_LIST = z;
        if (VERSION.SDK_INT < 23) {
        }
        if (VERSION.SDK_INT < 16) {
        }
        if (VERSION.SDK_INT < 21) {
        }
        if (VERSION.SDK_INT > 15) {
        }
        if (VERSION.SDK_INT > 15) {
        }
    }

    public void setTopGlowOffset(int i) {
        this.topGlowOffset = i;
    }

    public void setBottomGlowOffset(int i) {
        this.bottomGlowOffset = i;
    }

    public void setGlowColor(int i) {
        this.glowColor = i;
    }

    public int getAttachedScrapChildCount() {
        return this.mRecycler.getScrapCount();
    }

    public View getAttachedScrapChildAt(int i) {
        if (i >= 0) {
            if (i < this.mRecycler.mAttachedScrap.size()) {
                return this.mRecycler.getScrapViewAt(i);
            }
        }
        return 0;
    }

    public int getCachedChildCount() {
        return this.mRecycler.mCachedViews.size();
    }

    public View getCachedChildAt(int i) {
        if (i >= 0) {
            if (i < this.mRecycler.mCachedViews.size()) {
                return ((ViewHolder) this.mRecycler.mCachedViews.get(i)).itemView;
            }
        }
        return 0;
    }

    public int getHiddenChildCount() {
        return this.mChildHelper.getHiddenChildCount();
    }

    public View getHiddenChildAt(int i) {
        return this.mChildHelper.getHiddenChildAt(i);
    }

    void applyEdgeEffectColor(EdgeEffect edgeEffect) {
        if (edgeEffect != null && VERSION.SDK_INT >= 21 && this.glowColor != 0) {
            edgeEffect.setColor(this.glowColor);
        }
    }

    public RecyclerView(Context context) {
        this(context, null);
    }

    public RecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mObserver = new RecyclerViewDataObserver();
        this.mRecycler = new Recycler();
        this.mViewInfoStore = new ViewInfoStore();
        this.mUpdateChildViewsRunnable = new C06591();
        this.mTempRect = new Rect();
        this.mTempRect2 = new Rect();
        this.mTempRectF = new RectF();
        this.mItemDecorations = new ArrayList();
        this.mOnItemTouchListeners = new ArrayList();
        boolean z = false;
        this.mInterceptRequestLayoutDepth = 0;
        this.mDataSetHasChangedAfterLayout = false;
        this.mDispatchItemsChangedEvent = false;
        this.mLayoutOrScrollCounter = 0;
        this.mDispatchScrollCounter = 0;
        this.mEdgeEffectFactory = new EdgeEffectFactory();
        this.mItemAnimator = new DefaultItemAnimator();
        this.mScrollState = 0;
        this.mScrollPointerId = -1;
        this.mScaledHorizontalScrollFactor = Float.MIN_VALUE;
        this.mScaledVerticalScrollFactor = Float.MIN_VALUE;
        this.mPreserveFocusAfterLayout = true;
        this.mViewFlinger = new ViewFlinger();
        this.mPrefetchRegistry = ALLOW_THREAD_GAP_WORK ? new LayoutPrefetchRegistryImpl() : null;
        this.mState = new State();
        this.mItemsAddedOrRemoved = false;
        this.mItemsChanged = false;
        this.mItemAnimatorListener = new ItemAnimatorRestoreListener();
        this.mPostedAnimatorRunner = false;
        this.mMinMaxLayoutPositions = new int[2];
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mNestedOffsets = new int[2];
        this.topGlowOffset = 0;
        this.bottomGlowOffset = 0;
        this.glowColor = 0;
        this.mPendingAccessibilityImportanceChange = new ArrayList();
        this.mItemAnimatorRunner = new C06602();
        this.mViewInfoProcessCallback = new C18694();
        if (attributeSet != null) {
            attributeSet = context.obtainStyledAttributes(attributeSet, CLIP_TO_PADDING_ATTR, i, 0);
            this.mClipToPadding = attributeSet.getBoolean(0, true);
            attributeSet.recycle();
        } else {
            this.mClipToPadding = true;
        }
        setScrollContainer(true);
        setFocusableInTouchMode(true);
        attributeSet = ViewConfiguration.get(context);
        this.mTouchSlop = attributeSet.getScaledTouchSlop();
        this.mScaledHorizontalScrollFactor = ViewConfigurationCompat.getScaledHorizontalScrollFactor(attributeSet, context);
        this.mScaledVerticalScrollFactor = ViewConfigurationCompat.getScaledVerticalScrollFactor(attributeSet, context);
        this.mMinFlingVelocity = attributeSet.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = attributeSet.getScaledMaximumFlingVelocity();
        if (getOverScrollMode() == 2) {
            z = true;
        }
        setWillNotDraw(z);
        this.mItemAnimator.setListener(this.mItemAnimatorListener);
        initAdapterManager();
        initChildrenHelper();
        if (ViewCompat.getImportantForAccessibility(this) == null) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
        setDescendantFocusability(262144);
        setNestedScrollingEnabled(true);
    }

    String exceptionLabel() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ");
        stringBuilder.append(super.toString());
        stringBuilder.append(", adapter:");
        stringBuilder.append(this.mAdapter);
        stringBuilder.append(", layout:");
        stringBuilder.append(this.mLayout);
        stringBuilder.append(", context:");
        stringBuilder.append(getContext());
        return stringBuilder.toString();
    }

    public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public void setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate recyclerViewAccessibilityDelegate) {
        this.mAccessibilityDelegate = recyclerViewAccessibilityDelegate;
        ViewCompat.setAccessibilityDelegate(this, this.mAccessibilityDelegate);
    }

    private void createLayoutManager(Context context, String str, AttributeSet attributeSet, int i, int i2) {
        if (str != null) {
            str = str.trim();
            if (!str.isEmpty()) {
                str = getFullClassName(context, str);
                try {
                    ClassLoader classLoader;
                    Constructor constructor;
                    if (isInEditMode()) {
                        classLoader = getClass().getClassLoader();
                    } else {
                        classLoader = context.getClassLoader();
                    }
                    Class asSubclass = classLoader.loadClass(str).asSubclass(LayoutManager.class);
                    Object[] objArr = null;
                    try {
                        constructor = asSubclass.getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
                        objArr = new Object[]{context, attributeSet, Integer.valueOf(i), Integer.valueOf(i2)};
                    } catch (Context context2) {
                        constructor = asSubclass.getConstructor(new Class[0]);
                    }
                    constructor.setAccessible(true);
                    setLayoutManager((LayoutManager) constructor.newInstance(objArr));
                } catch (int i3) {
                    i3.initCause(context2);
                    i2 = new StringBuilder();
                    i2.append(attributeSet.getPositionDescription());
                    i2.append(": Error creating LayoutManager ");
                    i2.append(str);
                    throw new IllegalStateException(i2.toString(), i3);
                } catch (Context context22) {
                    i2 = new StringBuilder();
                    i2.append(attributeSet.getPositionDescription());
                    i2.append(": Unable to find LayoutManager ");
                    i2.append(str);
                    throw new IllegalStateException(i2.toString(), context22);
                } catch (Context context222) {
                    i2 = new StringBuilder();
                    i2.append(attributeSet.getPositionDescription());
                    i2.append(": Could not instantiate the LayoutManager: ");
                    i2.append(str);
                    throw new IllegalStateException(i2.toString(), context222);
                } catch (Context context2222) {
                    i2 = new StringBuilder();
                    i2.append(attributeSet.getPositionDescription());
                    i2.append(": Could not instantiate the LayoutManager: ");
                    i2.append(str);
                    throw new IllegalStateException(i2.toString(), context2222);
                } catch (Context context22222) {
                    i2 = new StringBuilder();
                    i2.append(attributeSet.getPositionDescription());
                    i2.append(": Cannot access non-public constructor ");
                    i2.append(str);
                    throw new IllegalStateException(i2.toString(), context22222);
                } catch (Context context222222) {
                    i2 = new StringBuilder();
                    i2.append(attributeSet.getPositionDescription());
                    i2.append(": Class is not a LayoutManager ");
                    i2.append(str);
                    throw new IllegalStateException(i2.toString(), context222222);
                }
            }
        }
    }

    private String getFullClassName(Context context, String str) {
        if (str.charAt(0) == '.') {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(context.getPackageName());
            stringBuilder.append(str);
            return stringBuilder.toString();
        } else if (str.contains(".") != null) {
            return str;
        } else {
            context = new StringBuilder();
            context.append(RecyclerView.class.getPackage().getName());
            context.append('.');
            context.append(str);
            return context.toString();
        }
    }

    private void initChildrenHelper() {
        this.mChildHelper = new ChildHelper(new C18705());
    }

    void initAdapterManager() {
        this.mAdapterHelper = new AdapterHelper(new C18716());
    }

    public void setHasFixedSize(boolean z) {
        this.mHasFixedSize = z;
    }

    public boolean hasFixedSize() {
        return this.mHasFixedSize;
    }

    public void setClipToPadding(boolean z) {
        if (z != this.mClipToPadding) {
            invalidateGlows();
        }
        this.mClipToPadding = z;
        super.setClipToPadding(z);
        if (this.mFirstLayoutComplete) {
            requestLayout();
        }
    }

    public boolean getClipToPadding() {
        return this.mClipToPadding;
    }

    public void setScrollingTouchSlop(int i) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        switch (i) {
            case 0:
                break;
            case 1:
                this.mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
                return;
            default:
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("setScrollingTouchSlop(): bad argument constant ");
                stringBuilder.append(i);
                stringBuilder.append("; using default value");
                Log.w(str, stringBuilder.toString());
                break;
        }
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    public void swapAdapter(Adapter adapter, boolean z) {
        setLayoutFrozen(false);
        setAdapterInternal(adapter, true, z);
        processDataSetCompletelyChanged(true);
        requestLayout();
    }

    public void setAdapter(Adapter adapter) {
        setLayoutFrozen(false);
        setAdapterInternal(adapter, false, true);
        processDataSetCompletelyChanged(false);
        requestLayout();
    }

    void removeAndRecycleViews() {
        if (this.mItemAnimator != null) {
            this.mItemAnimator.endAnimations();
        }
        if (this.mLayout != null) {
            this.mLayout.removeAndRecycleAllViews(this.mRecycler);
            this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        }
        this.mRecycler.clear();
    }

    private void setAdapterInternal(Adapter adapter, boolean z, boolean z2) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterAdapterDataObserver(this.mObserver);
            this.mAdapter.onDetachedFromRecyclerView(this);
        }
        if (!z || z2) {
            removeAndRecycleViews();
        }
        this.mAdapterHelper.reset();
        z2 = this.mAdapter;
        this.mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mObserver);
            adapter.onAttachedToRecyclerView(this);
        }
        if (this.mLayout != null) {
            this.mLayout.onAdapterChanged(z2, this.mAdapter);
        }
        this.mRecycler.onAdapterChanged(z2, this.mAdapter, z);
        this.mState.mStructureChanged = true;
    }

    public Adapter getAdapter() {
        return this.mAdapter;
    }

    public void setRecyclerListener(RecyclerListener recyclerListener) {
        this.mRecyclerListener = recyclerListener;
    }

    public int getBaseline() {
        if (this.mLayout != null) {
            return this.mLayout.getBaseline();
        }
        return super.getBaseline();
    }

    public void addOnChildAttachStateChangeListener(OnChildAttachStateChangeListener onChildAttachStateChangeListener) {
        if (this.mOnChildAttachStateListeners == null) {
            this.mOnChildAttachStateListeners = new ArrayList();
        }
        this.mOnChildAttachStateListeners.add(onChildAttachStateChangeListener);
    }

    public void removeOnChildAttachStateChangeListener(OnChildAttachStateChangeListener onChildAttachStateChangeListener) {
        if (this.mOnChildAttachStateListeners != null) {
            this.mOnChildAttachStateListeners.remove(onChildAttachStateChangeListener);
        }
    }

    public void clearOnChildAttachStateChangeListeners() {
        if (this.mOnChildAttachStateListeners != null) {
            this.mOnChildAttachStateListeners.clear();
        }
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        if (layoutManager != this.mLayout) {
            stopScroll();
            if (this.mLayout != null) {
                if (this.mItemAnimator != null) {
                    this.mItemAnimator.endAnimations();
                }
                this.mLayout.removeAndRecycleAllViews(this.mRecycler);
                this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
                this.mRecycler.clear();
                if (this.mIsAttached) {
                    this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
                }
                this.mLayout.setRecyclerView(null);
                this.mLayout = null;
            } else {
                this.mRecycler.clear();
            }
            this.mChildHelper.removeAllViewsUnfiltered();
            this.mLayout = layoutManager;
            if (layoutManager != null) {
                if (layoutManager.mRecyclerView != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("LayoutManager ");
                    stringBuilder.append(layoutManager);
                    stringBuilder.append(" is already attached to a RecyclerView:");
                    stringBuilder.append(layoutManager.mRecyclerView.exceptionLabel());
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                this.mLayout.setRecyclerView(this);
                if (this.mIsAttached != null) {
                    this.mLayout.dispatchAttachedToWindow(this);
                }
            }
            this.mRecycler.updateViewCacheSize();
            requestLayout();
        }
    }

    public void setOnFlingListener(OnFlingListener onFlingListener) {
        this.mOnFlingListener = onFlingListener;
    }

    public OnFlingListener getOnFlingListener() {
        return this.mOnFlingListener;
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable savedState = new SavedState(super.onSaveInstanceState());
        if (this.mPendingSavedState != null) {
            savedState.copyFrom(this.mPendingSavedState);
        } else if (this.mLayout != null) {
            savedState.mLayoutState = this.mLayout.onSaveInstanceState();
        } else {
            savedState.mLayoutState = null;
        }
        return savedState;
    }

    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            this.mPendingSavedState = (SavedState) parcelable;
            super.onRestoreInstanceState(this.mPendingSavedState.getSuperState());
            if (!(this.mLayout == null || this.mPendingSavedState.mLayoutState == null)) {
                this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState);
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    protected void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchFreezeSelfOnly(sparseArray);
    }

    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchThawSelfOnly(sparseArray);
    }

    private void addAnimatingView(ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        boolean z = view.getParent() == this;
        this.mRecycler.unscrapView(getChildViewHolder(view));
        if (viewHolder.isTmpDetached() != null) {
            this.mChildHelper.attachViewToParent(view, -1, view.getLayoutParams(), true);
        } else if (z) {
            this.mChildHelper.hide(view);
        } else {
            this.mChildHelper.addView(view, true);
        }
    }

    boolean removeAnimatingView(View view) {
        startInterceptRequestLayout();
        boolean removeViewIfHidden = this.mChildHelper.removeViewIfHidden(view);
        if (removeViewIfHidden) {
            view = getChildViewHolderInt(view);
            this.mRecycler.unscrapView(view);
            this.mRecycler.recycleViewHolderInternal(view);
        }
        stopInterceptRequestLayout(removeViewIfHidden ^ 1);
        return removeViewIfHidden;
    }

    public LayoutManager getLayoutManager() {
        return this.mLayout;
    }

    public RecycledViewPool getRecycledViewPool() {
        return this.mRecycler.getRecycledViewPool();
    }

    public void setRecycledViewPool(RecycledViewPool recycledViewPool) {
        this.mRecycler.setRecycledViewPool(recycledViewPool);
    }

    public void setViewCacheExtension(ViewCacheExtension viewCacheExtension) {
        this.mRecycler.setViewCacheExtension(viewCacheExtension);
    }

    public void setItemViewCacheSize(int i) {
        this.mRecycler.setViewCacheSize(i);
    }

    public int getScrollState() {
        return this.mScrollState;
    }

    void setScrollState(int i) {
        if (i != this.mScrollState) {
            this.mScrollState = i;
            if (i != 2) {
                stopScrollersInternal();
            }
            dispatchOnScrollStateChanged(i);
        }
    }

    public void addItemDecoration(ItemDecoration itemDecoration, int i) {
        if (this.mLayout != null) {
            this.mLayout.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
        }
        if (this.mItemDecorations.isEmpty()) {
            setWillNotDraw(false);
        }
        if (i < 0) {
            this.mItemDecorations.add(itemDecoration);
        } else {
            this.mItemDecorations.add(i, itemDecoration);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public void addItemDecoration(ItemDecoration itemDecoration) {
        addItemDecoration(itemDecoration, -1);
    }

    public ItemDecoration getItemDecorationAt(int i) {
        int itemDecorationCount = getItemDecorationCount();
        if (i >= 0) {
            if (i < itemDecorationCount) {
                return (ItemDecoration) this.mItemDecorations.get(i);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i);
        stringBuilder.append(" is an invalid index for size ");
        stringBuilder.append(itemDecorationCount);
        throw new IndexOutOfBoundsException(stringBuilder.toString());
    }

    public int getItemDecorationCount() {
        return this.mItemDecorations.size();
    }

    public void removeItemDecorationAt(int i) {
        int itemDecorationCount = getItemDecorationCount();
        if (i >= 0) {
            if (i < itemDecorationCount) {
                removeItemDecoration(getItemDecorationAt(i));
                return;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i);
        stringBuilder.append(" is an invalid index for size ");
        stringBuilder.append(itemDecorationCount);
        throw new IndexOutOfBoundsException(stringBuilder.toString());
    }

    public void removeItemDecoration(ItemDecoration itemDecoration) {
        if (this.mLayout != null) {
            this.mLayout.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
        }
        this.mItemDecorations.remove(itemDecoration);
        if (this.mItemDecorations.isEmpty() != null) {
            setWillNotDraw(getOverScrollMode() == 2 ? true : null);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public void setChildDrawingOrderCallback(ChildDrawingOrderCallback childDrawingOrderCallback) {
        if (childDrawingOrderCallback != this.mChildDrawingOrderCallback) {
            this.mChildDrawingOrderCallback = childDrawingOrderCallback;
            setChildrenDrawingOrderEnabled(this.mChildDrawingOrderCallback != null ? true : null);
        }
    }

    @Deprecated
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mScrollListener = onScrollListener;
    }

    public void addOnScrollListener(OnScrollListener onScrollListener) {
        if (this.mScrollListeners == null) {
            this.mScrollListeners = new ArrayList();
        }
        this.mScrollListeners.add(onScrollListener);
    }

    public void removeOnScrollListener(OnScrollListener onScrollListener) {
        if (this.mScrollListeners != null) {
            this.mScrollListeners.remove(onScrollListener);
        }
    }

    public void clearOnScrollListeners() {
        if (this.mScrollListeners != null) {
            this.mScrollListeners.clear();
        }
    }

    public void scrollToPosition(int i) {
        if (!this.mLayoutFrozen) {
            stopScroll();
            if (this.mLayout == null) {
                Log.e(TAG, "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
                return;
            }
            this.mLayout.scrollToPosition(i);
            awakenScrollBars();
        }
    }

    void jumpToPositionForSmoothScroller(int i) {
        if (this.mLayout != null) {
            this.mLayout.scrollToPosition(i);
            awakenScrollBars();
        }
    }

    public void smoothScrollToPosition(int i) {
        if (!this.mLayoutFrozen) {
            if (this.mLayout == null) {
                Log.e(TAG, "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            } else {
                this.mLayout.smoothScrollToPosition(this, this.mState, i);
            }
        }
    }

    public void scrollTo(int i, int i2) {
        Log.w(TAG, "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
    }

    public void scrollBy(int i, int i2) {
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else if (!this.mLayoutFrozen) {
            boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
            boolean canScrollVertically = this.mLayout.canScrollVertically();
            if (canScrollHorizontally || canScrollVertically) {
                if (!canScrollHorizontally) {
                    i = 0;
                }
                if (!canScrollVertically) {
                    i2 = 0;
                }
                scrollByInternal(i, i2, null);
            }
        }
    }

    void consumePendingUpdateOperations() {
        if (this.mFirstLayoutComplete) {
            if (!this.mDataSetHasChangedAfterLayout) {
                if (this.mAdapterHelper.hasPendingUpdates()) {
                    if (this.mAdapterHelper.hasAnyUpdateTypes(4) && !this.mAdapterHelper.hasAnyUpdateTypes(11)) {
                        TraceCompat.beginSection(TRACE_HANDLE_ADAPTER_UPDATES_TAG);
                        startInterceptRequestLayout();
                        onEnterLayoutOrScroll();
                        this.mAdapterHelper.preProcess();
                        if (!this.mLayoutWasDefered) {
                            if (hasUpdatedView()) {
                                dispatchLayout();
                            } else {
                                this.mAdapterHelper.consumePostponedUpdates();
                            }
                        }
                        stopInterceptRequestLayout(true);
                        onExitLayoutOrScroll();
                        TraceCompat.endSection();
                    } else if (this.mAdapterHelper.hasPendingUpdates()) {
                        TraceCompat.beginSection(TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG);
                        dispatchLayout();
                        TraceCompat.endSection();
                    }
                    return;
                }
                return;
            }
        }
        TraceCompat.beginSection(TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG);
        dispatchLayout();
        TraceCompat.endSection();
    }

    private boolean hasUpdatedView() {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (childViewHolderInt != null) {
                if (!childViewHolderInt.shouldIgnore()) {
                    if (childViewHolderInt.isUpdated()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean scrollByInternal(int i, int i2, MotionEvent motionEvent) {
        int scrollHorizontallyBy;
        int i3;
        int scrollVerticallyBy;
        int i4;
        consumePendingUpdateOperations();
        if (this.mAdapter != null) {
            startInterceptRequestLayout();
            onEnterLayoutOrScroll();
            TraceCompat.beginSection(TRACE_SCROLL_TAG);
            fillRemainingScrollValues(this.mState);
            if (i != 0) {
                scrollHorizontallyBy = this.mLayout.scrollHorizontallyBy(i, this.mRecycler, this.mState);
                i3 = i - scrollHorizontallyBy;
            } else {
                scrollHorizontallyBy = 0;
                i3 = scrollHorizontallyBy;
            }
            if (i2 != 0) {
                scrollVerticallyBy = this.mLayout.scrollVerticallyBy(i2, this.mRecycler, this.mState);
                i4 = i2 - scrollVerticallyBy;
            } else {
                scrollVerticallyBy = 0;
                i4 = scrollVerticallyBy;
            }
            TraceCompat.endSection();
            repositionShadowingViews();
            onExitLayoutOrScroll();
            stopInterceptRequestLayout(false);
        } else {
            scrollHorizontallyBy = 0;
            i3 = scrollHorizontallyBy;
            scrollVerticallyBy = i3;
            i4 = scrollVerticallyBy;
        }
        if (!this.mItemDecorations.isEmpty()) {
            invalidate();
        }
        if (dispatchNestedScroll(scrollHorizontallyBy, scrollVerticallyBy, i3, i4, this.mScrollOffset, 0)) {
            this.mLastTouchX -= this.mScrollOffset[0];
            this.mLastTouchY -= this.mScrollOffset[1];
            if (motionEvent != null) {
                motionEvent.offsetLocation((float) this.mScrollOffset[0], (float) this.mScrollOffset[1]);
            }
            i = this.mNestedOffsets;
            i[0] = i[0] + this.mScrollOffset[0];
            i = this.mNestedOffsets;
            i[1] = i[1] + this.mScrollOffset[1];
        } else if (getOverScrollMode() != 2) {
            if (!(motionEvent == null || MotionEventCompat.isFromSource(motionEvent, 8194))) {
                pullGlows(motionEvent.getX(), (float) i3, motionEvent.getY(), (float) i4);
            }
            considerReleasingGlowsOnScroll(i, i2);
        }
        if (!(scrollHorizontallyBy == 0 && scrollVerticallyBy == 0)) {
            dispatchOnScrolled(scrollHorizontallyBy, scrollVerticallyBy);
        }
        if (awakenScrollBars() == 0) {
            invalidate();
        }
        if (scrollHorizontallyBy == 0 && scrollVerticallyBy == 0) {
            return false;
        }
        return true;
    }

    public int computeHorizontalScrollOffset() {
        int i = 0;
        if (this.mLayout == null) {
            return 0;
        }
        if (this.mLayout.canScrollHorizontally()) {
            i = this.mLayout.computeHorizontalScrollOffset(this.mState);
        }
        return i;
    }

    public int computeHorizontalScrollExtent() {
        int i = 0;
        if (this.mLayout == null) {
            return 0;
        }
        if (this.mLayout.canScrollHorizontally()) {
            i = this.mLayout.computeHorizontalScrollExtent(this.mState);
        }
        return i;
    }

    public int computeHorizontalScrollRange() {
        int i = 0;
        if (this.mLayout == null) {
            return 0;
        }
        if (this.mLayout.canScrollHorizontally()) {
            i = this.mLayout.computeHorizontalScrollRange(this.mState);
        }
        return i;
    }

    public int computeVerticalScrollOffset() {
        int i = 0;
        if (this.mLayout == null) {
            return 0;
        }
        if (this.mLayout.canScrollVertically()) {
            i = this.mLayout.computeVerticalScrollOffset(this.mState);
        }
        return i;
    }

    public int computeVerticalScrollExtent() {
        int i = 0;
        if (this.mLayout == null) {
            return 0;
        }
        if (this.mLayout.canScrollVertically()) {
            i = this.mLayout.computeVerticalScrollExtent(this.mState);
        }
        return i;
    }

    public int computeVerticalScrollRange() {
        int i = 0;
        if (this.mLayout == null) {
            return 0;
        }
        if (this.mLayout.canScrollVertically()) {
            i = this.mLayout.computeVerticalScrollRange(this.mState);
        }
        return i;
    }

    void startInterceptRequestLayout() {
        this.mInterceptRequestLayoutDepth++;
        if (this.mInterceptRequestLayoutDepth == 1 && !this.mLayoutFrozen) {
            this.mLayoutWasDefered = false;
        }
    }

    void stopInterceptRequestLayout(boolean z) {
        if (this.mInterceptRequestLayoutDepth < 1) {
            this.mInterceptRequestLayoutDepth = 1;
        }
        if (!(z || this.mLayoutFrozen)) {
            this.mLayoutWasDefered = false;
        }
        if (this.mInterceptRequestLayoutDepth == 1) {
            if (z && this.mLayoutWasDefered && !this.mLayoutFrozen && this.mLayout && this.mAdapter) {
                dispatchLayout();
            }
            if (!this.mLayoutFrozen) {
                this.mLayoutWasDefered = false;
            }
        }
        this.mInterceptRequestLayoutDepth -= true;
    }

    public void setLayoutFrozen(boolean z) {
        if (z != this.mLayoutFrozen) {
            assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
            if (z) {
                long uptimeMillis = SystemClock.uptimeMillis();
                onTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0));
                this.mLayoutFrozen = true;
                this.mIgnoreMotionEventTillDown = true;
                stopScroll();
                return;
            }
            this.mLayoutFrozen = false;
            if (!(!this.mLayoutWasDefered || this.mLayout == null || this.mAdapter == null)) {
                requestLayout();
            }
            this.mLayoutWasDefered = false;
        }
    }

    public boolean isLayoutFrozen() {
        return this.mLayoutFrozen;
    }

    public void smoothScrollBy(int i, int i2) {
        smoothScrollBy(i, i2, null);
    }

    public void smoothScrollBy(int i, int i2, Interpolator interpolator) {
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else if (!this.mLayoutFrozen) {
            if (!this.mLayout.canScrollHorizontally()) {
                i = 0;
            }
            if (!this.mLayout.canScrollVertically()) {
                i2 = 0;
            }
            if (!(i == 0 && i2 == 0)) {
                this.mViewFlinger.smoothScrollBy(i, i2, interpolator);
            }
        }
    }

    public boolean fling(int i, int i2) {
        int i3 = 0;
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return false;
        } else if (this.mLayoutFrozen) {
            return false;
        } else {
            boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
            boolean canScrollVertically = this.mLayout.canScrollVertically();
            if (!canScrollHorizontally || Math.abs(i) < this.mMinFlingVelocity) {
                i = 0;
            }
            if (!canScrollVertically || Math.abs(i2) < this.mMinFlingVelocity) {
                i2 = 0;
            }
            if (i == 0 && i2 == 0) {
                return false;
            }
            float f = (float) i;
            float f2 = (float) i2;
            if (!dispatchNestedPreFling(f, f2)) {
                boolean z;
                if (!canScrollHorizontally) {
                    if (!canScrollVertically) {
                        z = false;
                        dispatchNestedFling(f, f2, z);
                        if (this.mOnFlingListener == null && this.mOnFlingListener.onFling(i, i2)) {
                            return true;
                        }
                        if (z) {
                            if (canScrollHorizontally) {
                                i3 = 1;
                            }
                            if (canScrollVertically) {
                                i3 |= 2;
                            }
                            startNestedScroll(i3, 1);
                            this.mViewFlinger.fling(Math.max(-this.mMaxFlingVelocity, Math.min(i, this.mMaxFlingVelocity)), Math.max(-this.mMaxFlingVelocity, Math.min(i2, this.mMaxFlingVelocity)));
                            return true;
                        }
                    }
                }
                z = true;
                dispatchNestedFling(f, f2, z);
                if (this.mOnFlingListener == null) {
                }
                if (z) {
                    if (canScrollHorizontally) {
                        i3 = 1;
                    }
                    if (canScrollVertically) {
                        i3 |= 2;
                    }
                    startNestedScroll(i3, 1);
                    this.mViewFlinger.fling(Math.max(-this.mMaxFlingVelocity, Math.min(i, this.mMaxFlingVelocity)), Math.max(-this.mMaxFlingVelocity, Math.min(i2, this.mMaxFlingVelocity)));
                    return true;
                }
            }
            return false;
        }
    }

    public void stopScroll() {
        setScrollState(0);
        stopScrollersInternal();
    }

    private void stopScrollersInternal() {
        this.mViewFlinger.stop();
        if (this.mLayout != null) {
            this.mLayout.stopSmoothScroller();
        }
    }

    public int getMinFlingVelocity() {
        return this.mMinFlingVelocity;
    }

    public int getMaxFlingVelocity() {
        return this.mMaxFlingVelocity;
    }

    private void pullGlows(float f, float f2, float f3, float f4) {
        Object obj = 1;
        if (f2 < 0.0f) {
            ensureLeftGlow();
            EdgeEffectCompat.onPull(this.mLeftGlow, (-f2) / ((float) getWidth()), 1.0f - (f3 / ((float) getHeight())));
        } else if (f2 > 0.0f) {
            ensureRightGlow();
            EdgeEffectCompat.onPull(this.mRightGlow, f2 / ((float) getWidth()), f3 / ((float) getHeight()));
        } else {
            f3 = 0.0f;
            if (f4 < 0.0f) {
                ensureTopGlow();
                EdgeEffectCompat.onPull(this.mTopGlow, (-f4) / ((float) getHeight()), f / ((float) getWidth()));
            } else if (f4 <= 0.0f) {
                ensureBottomGlow();
                EdgeEffectCompat.onPull(this.mBottomGlow, f4 / ((float) getHeight()), 1.0f - (f / ((float) getWidth())));
            } else {
                obj = f3;
            }
            if (obj == null || f2 != 0.0f || f4 != 0.0f) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
            return;
        }
        f3 = Float.MIN_VALUE;
        if (f4 < 0.0f) {
            ensureTopGlow();
            EdgeEffectCompat.onPull(this.mTopGlow, (-f4) / ((float) getHeight()), f / ((float) getWidth()));
        } else if (f4 <= 0.0f) {
            obj = f3;
        } else {
            ensureBottomGlow();
            EdgeEffectCompat.onPull(this.mBottomGlow, f4 / ((float) getHeight()), 1.0f - (f / ((float) getWidth())));
        }
        if (obj == null) {
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void releaseGlows() {
        int isFinished;
        if (this.mLeftGlow != null) {
            this.mLeftGlow.onRelease();
            isFinished = this.mLeftGlow.isFinished();
        } else {
            isFinished = 0;
        }
        if (this.mTopGlow != null) {
            this.mTopGlow.onRelease();
            isFinished |= this.mTopGlow.isFinished();
        }
        if (this.mRightGlow != null) {
            this.mRightGlow.onRelease();
            isFinished |= this.mRightGlow.isFinished();
        }
        if (this.mBottomGlow != null) {
            this.mBottomGlow.onRelease();
            isFinished |= this.mBottomGlow.isFinished();
        }
        if (isFinished != 0) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    void considerReleasingGlowsOnScroll(int i, int i2) {
        int i3;
        if (this.mLeftGlow == null || this.mLeftGlow.isFinished() || i <= 0) {
            i3 = 0;
        } else {
            this.mLeftGlow.onRelease();
            i3 = this.mLeftGlow.isFinished();
        }
        if (!(this.mRightGlow == null || this.mRightGlow.isFinished() || i >= 0)) {
            this.mRightGlow.onRelease();
            i3 |= this.mRightGlow.isFinished();
        }
        if (this.mTopGlow != 0 && this.mTopGlow.isFinished() == 0 && i2 > 0) {
            this.mTopGlow.onRelease();
            i3 |= this.mTopGlow.isFinished();
        }
        if (this.mBottomGlow != 0 && this.mBottomGlow.isFinished() == 0 && i2 < 0) {
            this.mBottomGlow.onRelease();
            i3 |= this.mBottomGlow.isFinished();
        }
        if (i3 != 0) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    void absorbGlows(int i, int i2) {
        if (i < 0) {
            ensureLeftGlow();
            this.mLeftGlow.onAbsorb(-i);
        } else if (i > 0) {
            ensureRightGlow();
            this.mRightGlow.onAbsorb(i);
        }
        if (i2 < 0) {
            ensureTopGlow();
            this.mTopGlow.onAbsorb(-i2);
        } else if (i2 > 0) {
            ensureBottomGlow();
            this.mBottomGlow.onAbsorb(i2);
        }
        if (i != 0 || i2 != 0) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    void ensureLeftGlow() {
        if (this.mLeftGlow == null) {
            this.mLeftGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 0);
            if (this.mClipToPadding) {
                this.mLeftGlow.setSize((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
            } else {
                this.mLeftGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
            }
            applyEdgeEffectColor(this.mLeftGlow);
        }
    }

    void ensureRightGlow() {
        if (this.mRightGlow == null) {
            this.mRightGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 2);
            if (this.mClipToPadding) {
                this.mRightGlow.setSize((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
            } else {
                this.mRightGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
            }
            applyEdgeEffectColor(this.mRightGlow);
        }
    }

    void ensureTopGlow() {
        if (this.mTopGlow == null) {
            this.mTopGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 1);
            if (this.mClipToPadding) {
                this.mTopGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
            applyEdgeEffectColor(this.mTopGlow);
        }
    }

    void ensureBottomGlow() {
        if (this.mBottomGlow == null) {
            this.mBottomGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 3);
            if (this.mClipToPadding) {
                this.mBottomGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
            applyEdgeEffectColor(this.mBottomGlow);
        }
    }

    void invalidateGlows() {
        this.mBottomGlow = null;
        this.mTopGlow = null;
        this.mRightGlow = null;
        this.mLeftGlow = null;
    }

    public void setEdgeEffectFactory(EdgeEffectFactory edgeEffectFactory) {
        this.mEdgeEffectFactory = edgeEffectFactory;
        invalidateGlows();
    }

    public EdgeEffectFactory getEdgeEffectFactory() {
        return this.mEdgeEffectFactory;
    }

    public View focusSearch(View view, int i) {
        View onInterceptFocusSearch = this.mLayout.onInterceptFocusSearch(view, i);
        if (onInterceptFocusSearch != null) {
            return onInterceptFocusSearch;
        }
        boolean z = (this.mAdapter == null || this.mLayout == null || isComputingLayout() || this.mLayoutFrozen) ? false : true;
        FocusFinder instance = FocusFinder.getInstance();
        if (z && (i == 2 || i == 1)) {
            int i2;
            boolean z2;
            if (this.mLayout.canScrollVertically()) {
                i2 = i == 2 ? TsExtractor.TS_STREAM_TYPE_HDMV_DTS : 33;
                z2 = instance.findNextFocus(this, view, i2) == null;
                if (FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
                    i = i2;
                }
            } else {
                z2 = false;
            }
            if (!z2 && this.mLayout.canScrollHorizontally()) {
                i2 = ((this.mLayout.getLayoutDirection() == 1 ? 1 : 0) ^ (i == 2 ? 1 : 0)) != 0 ? 66 : 17;
                z2 = instance.findNextFocus(this, view, i2) == null;
                if (FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
                    i = i2;
                }
            }
            if (z2) {
                consumePendingUpdateOperations();
                if (findContainingItemView(view) == null) {
                    return null;
                }
                startInterceptRequestLayout();
                this.mLayout.onFocusSearchFailed(view, i, this.mRecycler, this.mState);
                stopInterceptRequestLayout(false);
            }
            onInterceptFocusSearch = instance.findNextFocus(this, view, i);
        } else {
            View findNextFocus = instance.findNextFocus(this, view, i);
            if (findNextFocus == null && z) {
                consumePendingUpdateOperations();
                if (findContainingItemView(view) == null) {
                    return null;
                }
                startInterceptRequestLayout();
                onInterceptFocusSearch = this.mLayout.onFocusSearchFailed(view, i, this.mRecycler, this.mState);
                stopInterceptRequestLayout(false);
            } else {
                onInterceptFocusSearch = findNextFocus;
            }
        }
        if (onInterceptFocusSearch == null || onInterceptFocusSearch.hasFocusable()) {
            if (!isPreferredNextFocus(view, onInterceptFocusSearch, i)) {
                onInterceptFocusSearch = super.focusSearch(view, i);
            }
            return onInterceptFocusSearch;
        } else if (getFocusedChild() == null) {
            return super.focusSearch(view, i);
        } else {
            requestChildOnScreen(onInterceptFocusSearch, null);
            return view;
        }
    }

    private boolean isPreferredNextFocus(View view, View view2, int i) {
        boolean z = false;
        if (view2 != null) {
            if (view2 != this) {
                if (findContainingItemView(view2) == null) {
                    return false;
                }
                if (view == null || findContainingItemView(view) == null) {
                    return true;
                }
                this.mTempRect.set(0, 0, view.getWidth(), view.getHeight());
                this.mTempRect2.set(0, 0, view2.getWidth(), view2.getHeight());
                offsetDescendantRectToMyCoords(view, this.mTempRect);
                offsetDescendantRectToMyCoords(view2, this.mTempRect2);
                view2 = -1;
                view = this.mLayout.getLayoutDirection() == 1 ? -1 : 1;
                int i2 = ((this.mTempRect.left < this.mTempRect2.left || this.mTempRect.right <= this.mTempRect2.left) && this.mTempRect.right < this.mTempRect2.right) ? 1 : ((this.mTempRect.right > this.mTempRect2.right || this.mTempRect.left >= this.mTempRect2.right) && this.mTempRect.left > this.mTempRect2.left) ? -1 : 0;
                if ((this.mTempRect.top < this.mTempRect2.top || this.mTempRect.bottom <= this.mTempRect2.top) && this.mTempRect.bottom < this.mTempRect2.bottom) {
                    view2 = 1;
                } else if ((this.mTempRect.bottom <= this.mTempRect2.bottom && this.mTempRect.top < this.mTempRect2.bottom) || this.mTempRect.top <= this.mTempRect2.top) {
                    view2 = null;
                }
                if (i == 17) {
                    if (i2 < 0) {
                        z = true;
                    }
                    return z;
                } else if (i == 33) {
                    if (view2 < null) {
                        z = true;
                    }
                    return z;
                } else if (i == 66) {
                    if (i2 > 0) {
                        z = true;
                    }
                    return z;
                } else if (i != TsExtractor.TS_STREAM_TYPE_HDMV_DTS) {
                    switch (i) {
                        case 1:
                            if (view2 < null || (view2 == null && i2 * view <= 0)) {
                                z = true;
                            }
                            return z;
                        case 2:
                            if (view2 > null || (view2 == null && i2 * view >= 0)) {
                                z = true;
                            }
                            return z;
                        default:
                            view2 = new StringBuilder();
                            view2.append("Invalid direction: ");
                            view2.append(i);
                            view2.append(exceptionLabel());
                            throw new IllegalArgumentException(view2.toString());
                    }
                } else {
                    if (view2 > null) {
                        z = true;
                    }
                    return z;
                }
            }
        }
        return false;
    }

    public void requestChildFocus(View view, View view2) {
        if (!(this.mLayout.onRequestChildFocus(this, this.mState, view, view2) || view2 == null)) {
            requestChildOnScreen(view, view2);
        }
        super.requestChildFocus(view, view2);
    }

    private void requestChildOnScreen(View view, View view2) {
        View view3 = view2 != null ? view2 : view;
        this.mTempRect.set(0, 0, view3.getWidth(), view3.getHeight());
        android.view.ViewGroup.LayoutParams layoutParams = view3.getLayoutParams();
        if (layoutParams instanceof LayoutParams) {
            LayoutParams layoutParams2 = (LayoutParams) layoutParams;
            if (!layoutParams2.mInsetsDirty) {
                Rect rect = layoutParams2.mDecorInsets;
                Rect rect2 = this.mTempRect;
                rect2.left -= rect.left;
                rect2 = this.mTempRect;
                rect2.right += rect.right;
                rect2 = this.mTempRect;
                rect2.top -= rect.top;
                rect2 = this.mTempRect;
                rect2.bottom += rect.bottom;
            }
        }
        if (view2 != null) {
            offsetDescendantRectToMyCoords(view2, this.mTempRect);
            offsetRectIntoDescendantCoords(view, this.mTempRect);
        }
        this.mLayout.requestChildRectangleOnScreen(this, view, this.mTempRect, this.mFirstLayoutComplete ^ 1, view2 == null);
    }

    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        return this.mLayout.requestChildRectangleOnScreen(this, view, rect, z);
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        if (this.mLayout == null || !this.mLayout.onAddFocusables(this, arrayList, i, i2)) {
            super.addFocusables(arrayList, i, i2);
        }
    }

    protected boolean onRequestFocusInDescendants(int i, Rect rect) {
        if (isComputingLayout()) {
            return false;
        }
        return super.onRequestFocusInDescendants(i, rect);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLayoutOrScrollCounter = 0;
        boolean z = true;
        this.mIsAttached = true;
        if (!this.mFirstLayoutComplete || isLayoutRequested()) {
            z = false;
        }
        this.mFirstLayoutComplete = z;
        if (this.mLayout != null) {
            this.mLayout.dispatchAttachedToWindow(this);
        }
        this.mPostedAnimatorRunner = false;
        if (ALLOW_THREAD_GAP_WORK) {
            this.mGapWorker = (GapWorker) GapWorker.sGapWorker.get();
            if (this.mGapWorker == null) {
                float refreshRate;
                this.mGapWorker = new GapWorker();
                Display display = ViewCompat.getDisplay(this);
                if (!(isInEditMode() || display == null)) {
                    refreshRate = display.getRefreshRate();
                    if (refreshRate >= 30.0f) {
                        this.mGapWorker.mFrameIntervalNs = (long) (1.0E9f / refreshRate);
                        GapWorker.sGapWorker.set(this.mGapWorker);
                    }
                }
                refreshRate = 60.0f;
                this.mGapWorker.mFrameIntervalNs = (long) (1.0E9f / refreshRate);
                GapWorker.sGapWorker.set(this.mGapWorker);
            }
            this.mGapWorker.add(this);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mItemAnimator != null) {
            this.mItemAnimator.endAnimations();
        }
        stopScroll();
        this.mIsAttached = false;
        if (this.mLayout != null) {
            this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
        }
        this.mPendingAccessibilityImportanceChange.clear();
        removeCallbacks(this.mItemAnimatorRunner);
        this.mViewInfoStore.onDetach();
        if (ALLOW_THREAD_GAP_WORK && this.mGapWorker != null) {
            this.mGapWorker.remove(this);
            this.mGapWorker = null;
        }
    }

    public boolean isAttachedToWindow() {
        return this.mIsAttached;
    }

    void assertInLayoutOrScroll(String str) {
        if (!isComputingLayout()) {
            if (str == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Cannot call this method unless RecyclerView is computing a layout or scrolling");
                stringBuilder.append(exceptionLabel());
                throw new IllegalStateException(stringBuilder.toString());
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(exceptionLabel());
            throw new IllegalStateException(stringBuilder2.toString());
        }
    }

    void assertNotInLayoutOrScroll(String str) {
        if (isComputingLayout()) {
            if (str == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Cannot call this method while RecyclerView is computing a layout or scrolling");
                stringBuilder.append(exceptionLabel());
                throw new IllegalStateException(stringBuilder.toString());
            }
            throw new IllegalStateException(str);
        } else if (this.mDispatchScrollCounter > null) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder2.append(exceptionLabel());
            Log.w(TAG, "Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data. Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame.", new IllegalStateException(stringBuilder2.toString()));
        }
    }

    public void addOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.mOnItemTouchListeners.add(onItemTouchListener);
    }

    public void removeOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.mOnItemTouchListeners.remove(onItemTouchListener);
        if (this.mActiveOnItemTouchListener == onItemTouchListener) {
            this.mActiveOnItemTouchListener = null;
        }
    }

    private boolean dispatchOnItemTouchIntercept(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 3 || action == 0) {
            this.mActiveOnItemTouchListener = null;
        }
        int size = this.mOnItemTouchListeners.size();
        int i = 0;
        while (i < size) {
            OnItemTouchListener onItemTouchListener = (OnItemTouchListener) this.mOnItemTouchListeners.get(i);
            if (!onItemTouchListener.onInterceptTouchEvent(this, motionEvent) || action == 3) {
                i++;
            } else {
                this.mActiveOnItemTouchListener = onItemTouchListener;
                return true;
            }
        }
        return false;
    }

    private boolean dispatchOnItemTouch(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (this.mActiveOnItemTouchListener != null) {
            if (action == 0) {
                this.mActiveOnItemTouchListener = null;
            } else {
                this.mActiveOnItemTouchListener.onTouchEvent(this, motionEvent);
                if (action == 3 || action == 1) {
                    this.mActiveOnItemTouchListener = null;
                }
                return true;
            }
        }
        if (action != 0) {
            action = this.mOnItemTouchListeners.size();
            for (int i = 0; i < action; i++) {
                OnItemTouchListener onItemTouchListener = (OnItemTouchListener) this.mOnItemTouchListeners.get(i);
                if (onItemTouchListener.onInterceptTouchEvent(this, motionEvent)) {
                    this.mActiveOnItemTouchListener = onItemTouchListener;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (this.mLayoutFrozen) {
            return false;
        }
        if (dispatchOnItemTouchIntercept(motionEvent)) {
            cancelTouch();
            return true;
        } else if (this.mLayout == null) {
            return false;
        } else {
            boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
            boolean canScrollVertically = this.mLayout.canScrollVertically();
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            int actionMasked = motionEvent.getActionMasked();
            int actionIndex = motionEvent.getActionIndex();
            switch (actionMasked) {
                case 0:
                    if (this.mIgnoreMotionEventTillDown) {
                        this.mIgnoreMotionEventTillDown = false;
                    }
                    this.mScrollPointerId = motionEvent.getPointerId(0);
                    actionMasked = (int) (motionEvent.getX() + 0.5f);
                    this.mLastTouchX = actionMasked;
                    this.mInitialTouchX = actionMasked;
                    motionEvent = (int) (motionEvent.getY() + NUM);
                    this.mLastTouchY = motionEvent;
                    this.mInitialTouchY = motionEvent;
                    if (this.mScrollState == 2) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        setScrollState(1);
                    }
                    motionEvent = this.mNestedOffsets;
                    this.mNestedOffsets[1] = 0;
                    motionEvent[0] = null;
                    motionEvent = canScrollHorizontally ? 1 : null;
                    if (canScrollVertically) {
                        motionEvent |= 2;
                    }
                    startNestedScroll(motionEvent, 0);
                    break;
                case 1:
                    this.mVelocityTracker.clear();
                    stopNestedScroll(0);
                    break;
                case 2:
                    actionMasked = motionEvent.findPointerIndex(this.mScrollPointerId);
                    if (actionMasked >= 0) {
                        actionIndex = (int) (motionEvent.getX(actionMasked) + 0.5f);
                        motionEvent = (int) (motionEvent.getY(actionMasked) + NUM);
                        if (this.mScrollState != 1) {
                            actionMasked = actionIndex - this.mInitialTouchX;
                            int i = motionEvent - this.mInitialTouchY;
                            if (!canScrollHorizontally || Math.abs(actionMasked) <= this.mTouchSlop) {
                                canScrollHorizontally = false;
                            } else {
                                this.mLastTouchX = actionIndex;
                                canScrollHorizontally = true;
                            }
                            if (canScrollVertically && Math.abs(i) > this.mTouchSlop) {
                                this.mLastTouchY = motionEvent;
                                canScrollHorizontally = true;
                            }
                            if (canScrollHorizontally) {
                                setScrollState(1);
                                break;
                            }
                        }
                    }
                    motionEvent = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Error processing scroll; pointer index for id ");
                    stringBuilder.append(this.mScrollPointerId);
                    stringBuilder.append(" not found. Did any MotionEvents get skipped?");
                    Log.e(motionEvent, stringBuilder.toString());
                    return false;
                    break;
                case 3:
                    cancelTouch();
                    break;
                case 5:
                    this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
                    int x = (int) (motionEvent.getX(actionIndex) + 0.5f);
                    this.mLastTouchX = x;
                    this.mInitialTouchX = x;
                    motionEvent = (int) (motionEvent.getY(actionIndex) + NUM);
                    this.mLastTouchY = motionEvent;
                    this.mInitialTouchY = motionEvent;
                    break;
                case 6:
                    onPointerUp(motionEvent);
                    break;
                default:
                    break;
            }
            if (this.mScrollState == 1) {
                z = true;
            }
            return z;
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        int size = this.mOnItemTouchListeners.size();
        for (int i = 0; i < size; i++) {
            ((OnItemTouchListener) this.mOnItemTouchListeners.get(i)).onRequestDisallowInterceptTouchEvent(z);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!this.mLayoutFrozen) {
            if (!this.mIgnoreMotionEventTillDown) {
                if (dispatchOnItemTouch(motionEvent)) {
                    cancelTouch();
                    return true;
                } else if (this.mLayout == null) {
                    return false;
                } else {
                    int[] iArr;
                    boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
                    boolean canScrollVertically = this.mLayout.canScrollVertically();
                    if (this.mVelocityTracker == null) {
                        this.mVelocityTracker = VelocityTracker.obtain();
                    }
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    int actionMasked = motionEvent.getActionMasked();
                    int actionIndex = motionEvent.getActionIndex();
                    if (actionMasked == 0) {
                        iArr = this.mNestedOffsets;
                        this.mNestedOffsets[1] = 0;
                        iArr[0] = 0;
                    }
                    obtain.offsetLocation((float) this.mNestedOffsets[0], (float) this.mNestedOffsets[1]);
                    switch (actionMasked) {
                        case 0:
                            this.mScrollPointerId = motionEvent.getPointerId(0);
                            actionMasked = (int) (motionEvent.getX() + 0.5f);
                            this.mLastTouchX = actionMasked;
                            this.mInitialTouchX = actionMasked;
                            motionEvent = (int) (motionEvent.getY() + NUM);
                            this.mLastTouchY = motionEvent;
                            this.mInitialTouchY = motionEvent;
                            motionEvent = canScrollHorizontally ? 1 : null;
                            if (canScrollVertically) {
                                motionEvent |= 2;
                            }
                            startNestedScroll(motionEvent, 0);
                            break;
                        case 1:
                            this.mVelocityTracker.addMovement(obtain);
                            this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mMaxFlingVelocity);
                            float f = canScrollHorizontally ? -this.mVelocityTracker.getXVelocity(this.mScrollPointerId) : 0.0f;
                            float f2 = canScrollVertically ? -this.mVelocityTracker.getYVelocity(this.mScrollPointerId) : 0.0f;
                            if ((f == 0.0f && f2 == 0.0f) || fling((int) f, (int) f2) == null) {
                                setScrollState(0);
                            }
                            resetTouch();
                            z = true;
                            break;
                        case 2:
                            actionMasked = motionEvent.findPointerIndex(this.mScrollPointerId);
                            if (actionMasked >= 0) {
                                actionIndex = (int) (motionEvent.getX(actionMasked) + 0.5f);
                                motionEvent = (int) (motionEvent.getY(actionMasked) + NUM);
                                actionMasked = this.mLastTouchX - actionIndex;
                                int i = this.mLastTouchY - motionEvent;
                                if (dispatchNestedPreScroll(actionMasked, i, this.mScrollConsumed, this.mScrollOffset, 0)) {
                                    actionMasked -= this.mScrollConsumed[0];
                                    i -= this.mScrollConsumed[1];
                                    obtain.offsetLocation((float) this.mScrollOffset[0], (float) this.mScrollOffset[1]);
                                    iArr = this.mNestedOffsets;
                                    iArr[0] = iArr[0] + this.mScrollOffset[0];
                                    iArr = this.mNestedOffsets;
                                    iArr[1] = iArr[1] + this.mScrollOffset[1];
                                }
                                if (this.mScrollState != 1) {
                                    boolean z2;
                                    if (!canScrollHorizontally || Math.abs(actionMasked) <= this.mTouchSlop) {
                                        z2 = false;
                                    } else {
                                        if (actionMasked > 0) {
                                            actionMasked -= this.mTouchSlop;
                                        } else {
                                            actionMasked += this.mTouchSlop;
                                        }
                                        z2 = true;
                                    }
                                    if (canScrollVertically && Math.abs(i) > this.mTouchSlop) {
                                        if (i > 0) {
                                            i -= this.mTouchSlop;
                                        } else {
                                            i += this.mTouchSlop;
                                        }
                                        z2 = true;
                                    }
                                    if (z2) {
                                        setScrollState(1);
                                    }
                                }
                                if (this.mScrollState == 1) {
                                    this.mLastTouchX = actionIndex - this.mScrollOffset[0];
                                    this.mLastTouchY = motionEvent - this.mScrollOffset[1];
                                    if (scrollByInternal(canScrollHorizontally ? actionMasked : null, canScrollVertically ? i : 0, obtain) != null) {
                                        getParent().requestDisallowInterceptTouchEvent(true);
                                    }
                                    if (!(this.mGapWorker == null || (actionMasked == 0 && i == 0))) {
                                        this.mGapWorker.postFromTraversal(this, actionMasked, i);
                                        break;
                                    }
                                }
                            }
                            motionEvent = TAG;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Error processing scroll; pointer index for id ");
                            stringBuilder.append(this.mScrollPointerId);
                            stringBuilder.append(" not found. Did any MotionEvents get skipped?");
                            Log.e(motionEvent, stringBuilder.toString());
                            return false;
                            break;
                        case 3:
                            cancelTouch();
                            break;
                        case 5:
                            this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
                            int x = (int) (motionEvent.getX(actionIndex) + 0.5f);
                            this.mLastTouchX = x;
                            this.mInitialTouchX = x;
                            motionEvent = (int) (motionEvent.getY(actionIndex) + NUM);
                            this.mLastTouchY = motionEvent;
                            this.mInitialTouchY = motionEvent;
                            break;
                        case 6:
                            onPointerUp(motionEvent);
                            break;
                        default:
                            break;
                    }
                    if (!z) {
                        this.mVelocityTracker.addMovement(obtain);
                    }
                    obtain.recycle();
                    return true;
                }
            }
        }
        return false;
    }

    private void resetTouch() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.clear();
        }
        stopNestedScroll(0);
        releaseGlows();
    }

    private void cancelTouch() {
        resetTouch();
        setScrollState(0);
    }

    private void onPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mScrollPointerId) {
            actionIndex = actionIndex == 0 ? 1 : 0;
            this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
            int x = (int) (motionEvent.getX(actionIndex) + 0.5f);
            this.mLastTouchX = x;
            this.mInitialTouchX = x;
            motionEvent = (int) (motionEvent.getY(actionIndex) + NUM);
            this.mLastTouchY = motionEvent;
            this.mInitialTouchY = motionEvent;
        }
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        if (!(this.mLayout == null || this.mLayoutFrozen || motionEvent.getAction() != 8)) {
            float f;
            float axisValue;
            if ((motionEvent.getSource() & 2) != 0) {
                f = this.mLayout.canScrollVertically() ? -motionEvent.getAxisValue(9) : 0.0f;
                if (this.mLayout.canScrollHorizontally()) {
                    axisValue = motionEvent.getAxisValue(10);
                    if (!(f == 0.0f && axisValue == 0.0f)) {
                        scrollByInternal((int) (axisValue * this.mScaledHorizontalScrollFactor), (int) (f * this.mScaledVerticalScrollFactor), motionEvent);
                    }
                }
            } else {
                if ((motionEvent.getSource() & 4194304) != 0) {
                    f = motionEvent.getAxisValue(26);
                    if (this.mLayout.canScrollVertically()) {
                        f = -f;
                    } else if (this.mLayout.canScrollHorizontally()) {
                        axisValue = f;
                        f = 0.0f;
                        scrollByInternal((int) (axisValue * this.mScaledHorizontalScrollFactor), (int) (f * this.mScaledVerticalScrollFactor), motionEvent);
                    }
                }
                f = 0.0f;
                axisValue = f;
                scrollByInternal((int) (axisValue * this.mScaledHorizontalScrollFactor), (int) (f * this.mScaledVerticalScrollFactor), motionEvent);
            }
            axisValue = 0.0f;
            scrollByInternal((int) (axisValue * this.mScaledHorizontalScrollFactor), (int) (f * this.mScaledVerticalScrollFactor), motionEvent);
        }
        return false;
    }

    protected void onMeasure(int i, int i2) {
        if (this.mLayout == null) {
            defaultOnMeasure(i, i2);
            return;
        }
        boolean z = false;
        if (this.mLayout.isAutoMeasureEnabled()) {
            int mode = MeasureSpec.getMode(i);
            int mode2 = MeasureSpec.getMode(i2);
            this.mLayout.onMeasure(this.mRecycler, this.mState, i, i2);
            if (mode == NUM && mode2 == NUM) {
                z = true;
            }
            if (!z) {
                if (this.mAdapter != null) {
                    if (this.mState.mLayoutStep == 1) {
                        dispatchLayoutStep1();
                    }
                    this.mLayout.setMeasureSpecs(i, i2);
                    this.mState.mIsMeasuring = true;
                    dispatchLayoutStep2();
                    this.mLayout.setMeasuredDimensionFromChildren(i, i2);
                    if (this.mLayout.shouldMeasureTwice()) {
                        this.mLayout.setMeasureSpecs(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
                        this.mState.mIsMeasuring = true;
                        dispatchLayoutStep2();
                        this.mLayout.setMeasuredDimensionFromChildren(i, i2);
                    }
                }
            }
        } else if (this.mHasFixedSize) {
            this.mLayout.onMeasure(this.mRecycler, this.mState, i, i2);
        } else {
            if (this.mAdapterUpdateDuringMeasure) {
                startInterceptRequestLayout();
                onEnterLayoutOrScroll();
                processAdapterUpdatesAndSetAnimationFlags();
                onExitLayoutOrScroll();
                if (this.mState.mRunPredictiveAnimations) {
                    this.mState.mInPreLayout = true;
                } else {
                    this.mAdapterHelper.consumeUpdatesInOnePass();
                    this.mState.mInPreLayout = false;
                }
                this.mAdapterUpdateDuringMeasure = false;
                stopInterceptRequestLayout(false);
            } else if (this.mState.mRunPredictiveAnimations) {
                setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
                return;
            }
            if (this.mAdapter != null) {
                this.mState.mItemCount = this.mAdapter.getItemCount();
            } else {
                this.mState.mItemCount = 0;
            }
            startInterceptRequestLayout();
            this.mLayout.onMeasure(this.mRecycler, this.mState, i, i2);
            stopInterceptRequestLayout(false);
            this.mState.mInPreLayout = false;
        }
    }

    void defaultOnMeasure(int i, int i2) {
        setMeasuredDimension(LayoutManager.chooseSize(i, getPaddingLeft() + getPaddingRight(), ViewCompat.getMinimumWidth(this)), LayoutManager.chooseSize(i2, getPaddingTop() + getPaddingBottom(), ViewCompat.getMinimumHeight(this)));
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 || i2 != i4) {
            invalidateGlows();
        }
    }

    public void setItemAnimator(ItemAnimator itemAnimator) {
        if (this.mItemAnimator != null) {
            this.mItemAnimator.endAnimations();
            this.mItemAnimator.setListener(null);
        }
        this.mItemAnimator = itemAnimator;
        if (this.mItemAnimator != null) {
            this.mItemAnimator.setListener(this.mItemAnimatorListener);
        }
    }

    void onEnterLayoutOrScroll() {
        this.mLayoutOrScrollCounter++;
    }

    void onExitLayoutOrScroll() {
        onExitLayoutOrScroll(true);
    }

    void onExitLayoutOrScroll(boolean z) {
        this.mLayoutOrScrollCounter--;
        if (this.mLayoutOrScrollCounter < 1) {
            this.mLayoutOrScrollCounter = 0;
            if (z) {
                dispatchContentChangedIfNecessary();
                dispatchPendingImportantForAccessibilityChanges();
            }
        }
    }

    boolean isAccessibilityEnabled() {
        return this.mAccessibilityManager != null && this.mAccessibilityManager.isEnabled();
    }

    private void dispatchContentChangedIfNecessary() {
        int i = this.mEatenAccessibilityChangeFlags;
        this.mEatenAccessibilityChangeFlags = 0;
        if (i != 0 && isAccessibilityEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(2048);
            AccessibilityEventCompat.setContentChangeTypes(obtain, i);
            sendAccessibilityEventUnchecked(obtain);
        }
    }

    public boolean isComputingLayout() {
        return this.mLayoutOrScrollCounter > 0;
    }

    boolean shouldDeferAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (!isComputingLayout()) {
            return false;
        }
        accessibilityEvent = accessibilityEvent != null ? AccessibilityEventCompat.getContentChangeTypes(accessibilityEvent) : null;
        if (accessibilityEvent == null) {
            accessibilityEvent = null;
        }
        this.mEatenAccessibilityChangeFlags = accessibilityEvent | this.mEatenAccessibilityChangeFlags;
        return true;
    }

    public void sendAccessibilityEventUnchecked(AccessibilityEvent accessibilityEvent) {
        if (!shouldDeferAccessibilityEvent(accessibilityEvent)) {
            super.sendAccessibilityEventUnchecked(accessibilityEvent);
        }
    }

    public ItemAnimator getItemAnimator() {
        return this.mItemAnimator;
    }

    void postAnimationRunner() {
        if (!this.mPostedAnimatorRunner && this.mIsAttached) {
            ViewCompat.postOnAnimation(this, this.mItemAnimatorRunner);
            this.mPostedAnimatorRunner = true;
        }
    }

    private boolean predictiveItemAnimationsEnabled() {
        return this.mItemAnimator != null && this.mLayout.supportsPredictiveItemAnimations();
    }

    private void processAdapterUpdatesAndSetAnimationFlags() {
        boolean z;
        State state;
        boolean z2;
        if (this.mDataSetHasChangedAfterLayout) {
            this.mAdapterHelper.reset();
            if (this.mDispatchItemsChangedEvent) {
                this.mLayout.onItemsChanged(this);
            }
        }
        if (predictiveItemAnimationsEnabled()) {
            this.mAdapterHelper.preProcess();
        } else {
            this.mAdapterHelper.consumeUpdatesInOnePass();
        }
        boolean z3 = true;
        if (!this.mItemsAddedOrRemoved) {
            if (!this.mItemsChanged) {
                z = false;
                state = this.mState;
                z2 = this.mFirstLayoutComplete && this.mItemAnimator != null && ((this.mDataSetHasChangedAfterLayout || z || this.mLayout.mRequestedSimpleAnimations) && (!this.mDataSetHasChangedAfterLayout || this.mAdapter.hasStableIds()));
                state.mRunSimpleAnimations = z2;
                state = this.mState;
                if (this.mState.mRunSimpleAnimations || !z || this.mDataSetHasChangedAfterLayout || !predictiveItemAnimationsEnabled()) {
                    z3 = false;
                }
                state.mRunPredictiveAnimations = z3;
            }
        }
        z = true;
        state = this.mState;
        if (!this.mFirstLayoutComplete) {
        }
        state.mRunSimpleAnimations = z2;
        state = this.mState;
        if (this.mState.mRunSimpleAnimations) {
        }
        z3 = false;
        state.mRunPredictiveAnimations = z3;
    }

    void dispatchLayout() {
        if (this.mAdapter == null) {
            Log.e(TAG, "No adapter attached; skipping layout");
        } else if (this.mLayout == null) {
            Log.e(TAG, "No layout manager attached; skipping layout");
        } else {
            this.mState.mIsMeasuring = false;
            if (this.mState.mLayoutStep == 1) {
                dispatchLayoutStep1();
                this.mLayout.setExactMeasureSpecsFrom(this);
                dispatchLayoutStep2();
            } else {
                if (!this.mAdapterHelper.hasUpdates() && this.mLayout.getWidth() == getWidth()) {
                    if (this.mLayout.getHeight() == getHeight()) {
                        this.mLayout.setExactMeasureSpecsFrom(this);
                    }
                }
                this.mLayout.setExactMeasureSpecsFrom(this);
                dispatchLayoutStep2();
            }
            dispatchLayoutStep3();
        }
    }

    private void saveFocusInfo() {
        ViewHolder viewHolder = null;
        View focusedChild = (this.mPreserveFocusAfterLayout && hasFocus() && this.mAdapter != null) ? getFocusedChild() : null;
        if (focusedChild != null) {
            viewHolder = findContainingViewHolder(focusedChild);
        }
        if (viewHolder == null) {
            resetFocusInfo();
            return;
        }
        int i;
        this.mState.mFocusedItemId = this.mAdapter.hasStableIds() ? viewHolder.getItemId() : -1;
        State state = this.mState;
        if (this.mDataSetHasChangedAfterLayout) {
            i = -1;
        } else if (viewHolder.isRemoved()) {
            i = viewHolder.mOldPosition;
        } else {
            i = viewHolder.getAdapterPosition();
        }
        state.mFocusedItemPosition = i;
        this.mState.mFocusedSubChildId = getDeepestFocusedViewWithId(viewHolder.itemView);
    }

    private void resetFocusInfo() {
        this.mState.mFocusedItemId = -1;
        this.mState.mFocusedItemPosition = -1;
        this.mState.mFocusedSubChildId = -1;
    }

    private View findNextViewToFocus() {
        int i = this.mState.mFocusedItemPosition != -1 ? this.mState.mFocusedItemPosition : 0;
        int itemCount = this.mState.getItemCount();
        int i2 = i;
        while (i2 < itemCount) {
            ViewHolder findViewHolderForAdapterPosition = findViewHolderForAdapterPosition(i2);
            if (findViewHolderForAdapterPosition == null) {
                break;
            } else if (findViewHolderForAdapterPosition.itemView.hasFocusable()) {
                return findViewHolderForAdapterPosition.itemView;
            } else {
                i2++;
            }
        }
        for (i = Math.min(itemCount, i) - 1; i >= 0; i--) {
            ViewHolder findViewHolderForAdapterPosition2 = findViewHolderForAdapterPosition(i);
            if (findViewHolderForAdapterPosition2 == null) {
                return null;
            }
            if (findViewHolderForAdapterPosition2.itemView.hasFocusable()) {
                return findViewHolderForAdapterPosition2.itemView;
            }
        }
        return null;
    }

    private void recoverFocusFromState() {
        if (this.mPreserveFocusAfterLayout && this.mAdapter != null && hasFocus() && getDescendantFocusability() != 393216) {
            if (getDescendantFocusability() != 131072 || !isFocused()) {
                View focusedChild;
                View findViewById;
                if (!isFocused()) {
                    focusedChild = getFocusedChild();
                    if (!IGNORE_DETACHED_FOCUSED_CHILD || (focusedChild.getParent() != null && focusedChild.hasFocus())) {
                        if (!this.mChildHelper.isHidden(focusedChild)) {
                            return;
                        }
                    } else if (this.mChildHelper.getChildCount() == 0) {
                        requestFocus();
                        return;
                    }
                }
                focusedChild = null;
                ViewHolder findViewHolderForItemId = (this.mState.mFocusedItemId == -1 || !this.mAdapter.hasStableIds()) ? null : findViewHolderForItemId(this.mState.mFocusedItemId);
                if (!(findViewHolderForItemId == null || this.mChildHelper.isHidden(findViewHolderForItemId.itemView))) {
                    if (findViewHolderForItemId.itemView.hasFocusable()) {
                        focusedChild = findViewHolderForItemId.itemView;
                        if (focusedChild != null) {
                            if (((long) this.mState.mFocusedSubChildId) != -1) {
                                findViewById = focusedChild.findViewById(this.mState.mFocusedSubChildId);
                                if (findViewById != null && findViewById.isFocusable()) {
                                    focusedChild = findViewById;
                                }
                            }
                            focusedChild.requestFocus();
                        }
                    }
                }
                if (this.mChildHelper.getChildCount() > 0) {
                    focusedChild = findNextViewToFocus();
                }
                if (focusedChild != null) {
                    if (((long) this.mState.mFocusedSubChildId) != -1) {
                        findViewById = focusedChild.findViewById(this.mState.mFocusedSubChildId);
                        focusedChild = findViewById;
                    }
                    focusedChild.requestFocus();
                }
            }
        }
    }

    private int getDeepestFocusedViewWithId(View view) {
        int id = view.getId();
        while (!view.isFocused() && (view instanceof ViewGroup) && view.hasFocus()) {
            view = ((ViewGroup) view).getFocusedChild();
            if (view.getId() != -1) {
                id = view.getId();
            }
        }
        return id;
    }

    final void fillRemainingScrollValues(State state) {
        if (getScrollState() == 2) {
            OverScroller access$400 = this.mViewFlinger.mScroller;
            state.mRemainingScrollHorizontal = access$400.getFinalX() - access$400.getCurrX();
            state.mRemainingScrollVertical = access$400.getFinalY() - access$400.getCurrY();
            return;
        }
        state.mRemainingScrollHorizontal = 0;
        state.mRemainingScrollVertical = 0;
    }

    private void dispatchLayoutStep1() {
        int childCount;
        boolean z = true;
        this.mState.assertLayoutStep(1);
        fillRemainingScrollValues(this.mState);
        this.mState.mIsMeasuring = false;
        startInterceptRequestLayout();
        this.mViewInfoStore.clear();
        onEnterLayoutOrScroll();
        processAdapterUpdatesAndSetAnimationFlags();
        saveFocusInfo();
        State state = this.mState;
        if (!this.mState.mRunSimpleAnimations || !this.mItemsChanged) {
            z = false;
        }
        state.mTrackOldChangeHolders = z;
        this.mItemsChanged = false;
        this.mItemsAddedOrRemoved = false;
        this.mState.mInPreLayout = this.mState.mRunPredictiveAnimations;
        this.mState.mItemCount = this.mAdapter.getItemCount();
        findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        if (this.mState.mRunSimpleAnimations) {
            childCount = this.mChildHelper.getChildCount();
            for (int i = 0; i < childCount; i++) {
                ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!childViewHolderInt.shouldIgnore()) {
                    if (!childViewHolderInt.isInvalid() || this.mAdapter.hasStableIds()) {
                        this.mViewInfoStore.addToPreLayout(childViewHolderInt, this.mItemAnimator.recordPreLayoutInformation(this.mState, childViewHolderInt, ItemAnimator.buildAdapterChangeFlagsForAnimations(childViewHolderInt), childViewHolderInt.getUnmodifiedPayloads()));
                        if (!(!this.mState.mTrackOldChangeHolders || !childViewHolderInt.isUpdated() || childViewHolderInt.isRemoved() || childViewHolderInt.shouldIgnore() || childViewHolderInt.isInvalid())) {
                            this.mViewInfoStore.addToOldChangeHolders(getChangedHolderKey(childViewHolderInt), childViewHolderInt);
                        }
                    }
                }
            }
        }
        if (this.mState.mRunPredictiveAnimations) {
            saveOldPositions();
            boolean z2 = this.mState.mStructureChanged;
            this.mState.mStructureChanged = false;
            this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
            this.mState.mStructureChanged = z2;
            for (childCount = 0; childCount < this.mChildHelper.getChildCount(); childCount++) {
                ViewHolder childViewHolderInt2 = getChildViewHolderInt(this.mChildHelper.getChildAt(childCount));
                if (!childViewHolderInt2.shouldIgnore()) {
                    if (!this.mViewInfoStore.isInPreLayout(childViewHolderInt2)) {
                        int buildAdapterChangeFlagsForAnimations = ItemAnimator.buildAdapterChangeFlagsForAnimations(childViewHolderInt2);
                        boolean hasAnyOfTheFlags = childViewHolderInt2.hasAnyOfTheFlags(MessagesController.UPDATE_MASK_CHANNEL);
                        if (!hasAnyOfTheFlags) {
                            buildAdapterChangeFlagsForAnimations |= 4096;
                        }
                        ItemHolderInfo recordPreLayoutInformation = this.mItemAnimator.recordPreLayoutInformation(this.mState, childViewHolderInt2, buildAdapterChangeFlagsForAnimations, childViewHolderInt2.getUnmodifiedPayloads());
                        if (hasAnyOfTheFlags) {
                            recordAnimationInfoIfBouncedHiddenView(childViewHolderInt2, recordPreLayoutInformation);
                        } else {
                            this.mViewInfoStore.addToAppearedInPreLayoutHolders(childViewHolderInt2, recordPreLayoutInformation);
                        }
                    }
                }
            }
            clearOldPositions();
        } else {
            clearOldPositions();
        }
        onExitLayoutOrScroll();
        stopInterceptRequestLayout(false);
        this.mState.mLayoutStep = 2;
    }

    private void dispatchLayoutStep2() {
        startInterceptRequestLayout();
        onEnterLayoutOrScroll();
        this.mState.assertLayoutStep(6);
        this.mAdapterHelper.consumeUpdatesInOnePass();
        this.mState.mItemCount = this.mAdapter.getItemCount();
        this.mState.mDeletedInvisibleItemCountSincePreviousLayout = 0;
        this.mState.mInPreLayout = false;
        this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
        this.mState.mStructureChanged = false;
        this.mPendingSavedState = null;
        State state = this.mState;
        boolean z = this.mState.mRunSimpleAnimations && this.mItemAnimator != null;
        state.mRunSimpleAnimations = z;
        this.mState.mLayoutStep = 4;
        onExitLayoutOrScroll();
        stopInterceptRequestLayout(false);
    }

    private void dispatchLayoutStep3() {
        this.mState.assertLayoutStep(4);
        startInterceptRequestLayout();
        onEnterLayoutOrScroll();
        this.mState.mLayoutStep = 1;
        if (this.mState.mRunSimpleAnimations) {
            for (int childCount = this.mChildHelper.getChildCount() - 1; childCount >= 0; childCount--) {
                ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(childCount));
                if (!childViewHolderInt.shouldIgnore()) {
                    long changedHolderKey = getChangedHolderKey(childViewHolderInt);
                    ItemHolderInfo recordPostLayoutInformation = this.mItemAnimator.recordPostLayoutInformation(this.mState, childViewHolderInt);
                    ViewHolder fromOldChangeHolders = this.mViewInfoStore.getFromOldChangeHolders(changedHolderKey);
                    if (fromOldChangeHolders == null || fromOldChangeHolders.shouldIgnore()) {
                        this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                    } else {
                        boolean isDisappearing = this.mViewInfoStore.isDisappearing(fromOldChangeHolders);
                        boolean isDisappearing2 = this.mViewInfoStore.isDisappearing(childViewHolderInt);
                        if (isDisappearing && fromOldChangeHolders == childViewHolderInt) {
                            this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                        } else {
                            ItemHolderInfo popFromPreLayout = this.mViewInfoStore.popFromPreLayout(fromOldChangeHolders);
                            this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                            ItemHolderInfo popFromPostLayout = this.mViewInfoStore.popFromPostLayout(childViewHolderInt);
                            if (popFromPreLayout == null) {
                                handleMissingPreInfoForChangeError(changedHolderKey, childViewHolderInt, fromOldChangeHolders);
                            } else {
                                animateChange(fromOldChangeHolders, childViewHolderInt, popFromPreLayout, popFromPostLayout, isDisappearing, isDisappearing2);
                            }
                        }
                    }
                }
            }
            this.mViewInfoStore.process(this.mViewInfoProcessCallback);
        }
        this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        this.mState.mPreviousLayoutItemCount = this.mState.mItemCount;
        this.mDataSetHasChangedAfterLayout = false;
        this.mDispatchItemsChangedEvent = false;
        this.mState.mRunSimpleAnimations = false;
        this.mState.mRunPredictiveAnimations = false;
        this.mLayout.mRequestedSimpleAnimations = false;
        if (this.mRecycler.mChangedScrap != null) {
            this.mRecycler.mChangedScrap.clear();
        }
        if (this.mLayout.mPrefetchMaxObservedInInitialPrefetch) {
            this.mLayout.mPrefetchMaxCountObserved = 0;
            this.mLayout.mPrefetchMaxObservedInInitialPrefetch = false;
            this.mRecycler.updateViewCacheSize();
        }
        this.mLayout.onLayoutCompleted(this.mState);
        onExitLayoutOrScroll();
        stopInterceptRequestLayout(false);
        this.mViewInfoStore.clear();
        if (didChildRangeChange(this.mMinMaxLayoutPositions[0], this.mMinMaxLayoutPositions[1])) {
            dispatchOnScrolled(0, 0);
        }
        recoverFocusFromState();
        resetFocusInfo();
    }

    private void handleMissingPreInfoForChangeError(long j, ViewHolder viewHolder, ViewHolder viewHolder2) {
        StringBuilder stringBuilder;
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (childViewHolderInt != viewHolder) {
                if (getChangedHolderKey(childViewHolderInt) == j) {
                    if (this.mAdapter == null || this.mAdapter.hasStableIds() == null) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:");
                        stringBuilder.append(childViewHolderInt);
                        stringBuilder.append(" \n View Holder 2:");
                        stringBuilder.append(viewHolder);
                        stringBuilder.append(exceptionLabel());
                        throw new IllegalStateException(stringBuilder.toString());
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:");
                    stringBuilder.append(childViewHolderInt);
                    stringBuilder.append(" \n View Holder 2:");
                    stringBuilder.append(viewHolder);
                    stringBuilder.append(exceptionLabel());
                    throw new IllegalStateException(stringBuilder.toString());
                }
            }
        }
        j = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("Problem while matching changed view holders with the newones. The pre-layout information for the change holder ");
        stringBuilder.append(viewHolder2);
        stringBuilder.append(" cannot be found but it is necessary for ");
        stringBuilder.append(viewHolder);
        stringBuilder.append(exceptionLabel());
        Log.e(j, stringBuilder.toString());
    }

    void recordAnimationInfoIfBouncedHiddenView(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo) {
        viewHolder.setFlags(0, MessagesController.UPDATE_MASK_CHANNEL);
        if (this.mState.mTrackOldChangeHolders && viewHolder.isUpdated() && !viewHolder.isRemoved() && !viewHolder.shouldIgnore()) {
            this.mViewInfoStore.addToOldChangeHolders(getChangedHolderKey(viewHolder), viewHolder);
        }
        this.mViewInfoStore.addToPreLayout(viewHolder, itemHolderInfo);
    }

    private void findMinMaxChildLayoutPositions(int[] iArr) {
        int childCount = this.mChildHelper.getChildCount();
        if (childCount == 0) {
            iArr[0] = -1;
            iArr[1] = -1;
            return;
        }
        int i = Integer.MIN_VALUE;
        int i2 = Integer.MAX_VALUE;
        for (int i3 = 0; i3 < childCount; i3++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i3));
            if (!childViewHolderInt.shouldIgnore()) {
                int layoutPosition = childViewHolderInt.getLayoutPosition();
                if (layoutPosition < i2) {
                    i2 = layoutPosition;
                }
                if (layoutPosition > i) {
                    i = layoutPosition;
                }
            }
        }
        iArr[0] = i2;
        iArr[1] = i;
    }

    private boolean didChildRangeChange(int i, int i2) {
        findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        return (this.mMinMaxLayoutPositions[0] == i && this.mMinMaxLayoutPositions[1] == i2) ? false : true;
    }

    protected void removeDetachedView(View view, boolean z) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        if (childViewHolderInt != null) {
            if (childViewHolderInt.isTmpDetached()) {
                childViewHolderInt.clearTmpDetachFlag();
            } else if (!childViewHolderInt.shouldIgnore()) {
                z = new StringBuilder();
                z.append("Called removeDetachedView with a view which is not flagged as tmp detached.");
                z.append(childViewHolderInt);
                z.append(exceptionLabel());
                throw new IllegalArgumentException(z.toString());
            }
        }
        view.clearAnimation();
        dispatchChildDetached(view);
        super.removeDetachedView(view, z);
    }

    long getChangedHolderKey(ViewHolder viewHolder) {
        return this.mAdapter.hasStableIds() ? viewHolder.getItemId() : (long) viewHolder.mPosition;
    }

    void animateAppearance(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2) {
        viewHolder.setIsRecyclable(false);
        if (this.mItemAnimator.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2) != null) {
            postAnimationRunner();
        }
    }

    void animateDisappearance(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2) {
        addAnimatingView(viewHolder);
        viewHolder.setIsRecyclable(false);
        if (this.mItemAnimator.animateDisappearance(viewHolder, itemHolderInfo, itemHolderInfo2) != null) {
            postAnimationRunner();
        }
    }

    private void animateChange(ViewHolder viewHolder, ViewHolder viewHolder2, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2, boolean z, boolean z2) {
        viewHolder.setIsRecyclable(false);
        if (z) {
            addAnimatingView(viewHolder);
        }
        if (viewHolder != viewHolder2) {
            if (z2) {
                addAnimatingView(viewHolder2);
            }
            viewHolder.mShadowedHolder = viewHolder2;
            addAnimatingView(viewHolder);
            this.mRecycler.unscrapView(viewHolder);
            viewHolder2.setIsRecyclable(false);
            viewHolder2.mShadowingHolder = viewHolder;
        }
        if (this.mItemAnimator.animateChange(viewHolder, viewHolder2, itemHolderInfo, itemHolderInfo2) != null) {
            postAnimationRunner();
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        TraceCompat.beginSection(TRACE_ON_LAYOUT_TAG);
        dispatchLayout();
        TraceCompat.endSection();
        this.mFirstLayoutComplete = true;
    }

    public void requestLayout() {
        if (this.mInterceptRequestLayoutDepth != 0 || this.mLayoutFrozen) {
            this.mLayoutWasDefered = true;
        } else {
            super.requestLayout();
        }
    }

    void markItemDecorInsetsDirty() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < unfilteredChildCount; i++) {
            ((LayoutParams) this.mChildHelper.getUnfilteredChildAt(i).getLayoutParams()).mInsetsDirty = true;
        }
        this.mRecycler.markItemDecorInsetsDirty();
    }

    public void draw(Canvas canvas) {
        int i;
        int i2;
        super.draw(canvas);
        int size = this.mItemDecorations.size();
        int i3 = 0;
        for (i = 0; i < size; i++) {
            ((ItemDecoration) this.mItemDecorations.get(i)).onDrawOver(canvas, this, this.mState);
        }
        if (this.mLeftGlow == null || this.mLeftGlow.isFinished()) {
            i2 = 0;
        } else {
            size = canvas.save();
            i2 = this.mClipToPadding ? getPaddingBottom() : 0;
            canvas.rotate(270.0f);
            canvas.translate((float) ((-getHeight()) + i2), 0.0f);
            i2 = (this.mLeftGlow == null || !this.mLeftGlow.draw(canvas)) ? 0 : 1;
            canvas.restoreToCount(size);
        }
        if (!(this.mTopGlow == null || this.mTopGlow.isFinished())) {
            size = canvas.save();
            if (this.mClipToPadding) {
                canvas.translate((float) getPaddingLeft(), (float) getPaddingTop());
            }
            canvas.translate(0.0f, (float) this.topGlowOffset);
            i = (this.mTopGlow == null || !this.mTopGlow.draw(canvas)) ? 0 : 1;
            i2 |= i;
            canvas.restoreToCount(size);
        }
        if (!(this.mRightGlow == null || this.mRightGlow.isFinished())) {
            size = canvas.save();
            i = getWidth();
            int paddingTop = this.mClipToPadding ? getPaddingTop() : 0;
            canvas.rotate(90.0f);
            canvas.translate((float) (-paddingTop), (float) (-i));
            i = (this.mRightGlow == null || !this.mRightGlow.draw(canvas)) ? 0 : 1;
            i2 |= i;
            canvas.restoreToCount(size);
        }
        if (this.mBottomGlow == null || this.mBottomGlow.isFinished()) {
            i3 = i2;
        } else {
            size = canvas.save();
            canvas.rotate(180.0f);
            if (this.mClipToPadding) {
                canvas.translate((float) ((-getWidth()) + getPaddingRight()), (float) ((-getHeight()) + getPaddingBottom()));
            } else {
                canvas.translate((float) (-getWidth()), (float) ((-getHeight()) + this.bottomGlowOffset));
            }
            if (this.mBottomGlow != null && this.mBottomGlow.draw(canvas)) {
                i3 = 1;
            }
            i3 |= i2;
            canvas.restoreToCount(size);
        }
        if (i3 == 0 && this.mItemAnimator != null && this.mItemDecorations.size() > null && this.mItemAnimator.isRunning() != null) {
            i3 = 1;
        }
        if (i3 != 0) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = this.mItemDecorations.size();
        for (int i = 0; i < size; i++) {
            ((ItemDecoration) this.mItemDecorations.get(i)).onDraw(canvas, this, this.mState);
        }
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return (!(layoutParams instanceof LayoutParams) || this.mLayout.checkLayoutParams((LayoutParams) layoutParams) == null) ? null : true;
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        if (this.mLayout != null) {
            return this.mLayout.generateDefaultLayoutParams();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RecyclerView has no LayoutManager");
        stringBuilder.append(exceptionLabel());
        throw new IllegalStateException(stringBuilder.toString());
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        if (this.mLayout != null) {
            return this.mLayout.generateLayoutParams(getContext(), attributeSet);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RecyclerView has no LayoutManager");
        stringBuilder.append(exceptionLabel());
        throw new IllegalStateException(stringBuilder.toString());
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (this.mLayout != null) {
            return this.mLayout.generateLayoutParams(layoutParams);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RecyclerView has no LayoutManager");
        stringBuilder.append(exceptionLabel());
        throw new IllegalStateException(stringBuilder.toString());
    }

    public boolean isAnimating() {
        return this.mItemAnimator != null && this.mItemAnimator.isRunning();
    }

    void saveOldPositions() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < unfilteredChildCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.saveOldPosition();
            }
        }
    }

    void clearOldPositions() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < unfilteredChildCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.clearOldPosition();
            }
        }
        this.mRecycler.clearOldPositions();
    }

    void offsetPositionRecordsForMove(int i, int i2) {
        int i3;
        int i4;
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        int i5;
        if (i < i2) {
            i3 = i2;
            i4 = -1;
            i5 = i;
        } else {
            i3 = i;
            i5 = i2;
            i4 = 1;
        }
        for (int i6 = 0; i6 < unfilteredChildCount; i6++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i6));
            if (childViewHolderInt != null && childViewHolderInt.mPosition >= r2) {
                if (childViewHolderInt.mPosition <= i3) {
                    if (childViewHolderInt.mPosition == i) {
                        childViewHolderInt.offsetPosition(i2 - i, false);
                    } else {
                        childViewHolderInt.offsetPosition(i4, false);
                    }
                    this.mState.mStructureChanged = true;
                }
            }
        }
        this.mRecycler.offsetPositionRecordsForMove(i, i2);
        requestLayout();
    }

    void offsetPositionRecordsForInsert(int i, int i2) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i3 = 0; i3 < unfilteredChildCount; i3++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i3));
            if (!(childViewHolderInt == null || childViewHolderInt.shouldIgnore() || childViewHolderInt.mPosition < i)) {
                childViewHolderInt.offsetPosition(i2, false);
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForInsert(i, i2);
        requestLayout();
    }

    void offsetPositionRecordsForRemove(int i, int i2, boolean z) {
        int i3 = i + i2;
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i4 = 0; i4 < unfilteredChildCount; i4++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i4));
            if (!(childViewHolderInt == null || childViewHolderInt.shouldIgnore())) {
                if (childViewHolderInt.mPosition >= i3) {
                    childViewHolderInt.offsetPosition(-i2, z);
                    this.mState.mStructureChanged = true;
                } else if (childViewHolderInt.mPosition >= i) {
                    childViewHolderInt.flagRemovedAndOffsetPosition(i - 1, -i2, z);
                    this.mState.mStructureChanged = true;
                }
            }
        }
        this.mRecycler.offsetPositionRecordsForRemove(i, i2, z);
        requestLayout();
    }

    void viewRangeUpdate(int i, int i2, Object obj) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        int i3 = i + i2;
        for (int i4 = 0; i4 < unfilteredChildCount; i4++) {
            View unfilteredChildAt = this.mChildHelper.getUnfilteredChildAt(i4);
            ViewHolder childViewHolderInt = getChildViewHolderInt(unfilteredChildAt);
            if (childViewHolderInt != null) {
                if (!childViewHolderInt.shouldIgnore()) {
                    if (childViewHolderInt.mPosition >= i && childViewHolderInt.mPosition < i3) {
                        childViewHolderInt.addFlags(2);
                        childViewHolderInt.addChangePayload(obj);
                        ((LayoutParams) unfilteredChildAt.getLayoutParams()).mInsetsDirty = true;
                    }
                }
            }
        }
        this.mRecycler.viewRangeUpdate(i, i2);
    }

    boolean canReuseUpdatedViewHolder(ViewHolder viewHolder) {
        if (this.mItemAnimator != null) {
            if (this.mItemAnimator.canReuseUpdatedViewHolder(viewHolder, viewHolder.getUnmodifiedPayloads()) == null) {
                return null;
            }
        }
        return true;
    }

    void processDataSetCompletelyChanged(boolean z) {
        this.mDispatchItemsChangedEvent = z | this.mDispatchItemsChangedEvent;
        this.mDataSetHasChangedAfterLayout = true;
        markKnownViewsInvalid();
    }

    void markKnownViewsInvalid() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < unfilteredChildCount; i++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!(childViewHolderInt == null || childViewHolderInt.shouldIgnore())) {
                childViewHolderInt.addFlags(6);
            }
        }
        markItemDecorInsetsDirty();
        this.mRecycler.markKnownViewsInvalid();
    }

    public void invalidateItemDecorations() {
        if (this.mItemDecorations.size() != 0) {
            if (this.mLayout != null) {
                this.mLayout.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
            }
            markItemDecorInsetsDirty();
            requestLayout();
        }
    }

    public boolean getPreserveFocusAfterLayout() {
        return this.mPreserveFocusAfterLayout;
    }

    public void setPreserveFocusAfterLayout(boolean z) {
        this.mPreserveFocusAfterLayout = z;
    }

    public ViewHolder getChildViewHolder(View view) {
        Object parent = view.getParent();
        if (parent == null || parent == this) {
            return getChildViewHolderInt(view);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("View ");
        stringBuilder.append(view);
        stringBuilder.append(" is not a direct child of ");
        stringBuilder.append(this);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public View findContainingItemView(View view) {
        View parent = view.getParent();
        while (parent != null && parent != this && (parent instanceof View)) {
            view = parent;
            parent = view.getParent();
        }
        return parent == this ? view : null;
    }

    public ViewHolder findContainingViewHolder(View view) {
        view = findContainingItemView(view);
        if (view == null) {
            return null;
        }
        return getChildViewHolder(view);
    }

    static ViewHolder getChildViewHolderInt(View view) {
        return view == null ? null : ((LayoutParams) view.getLayoutParams()).mViewHolder;
    }

    @Deprecated
    public int getChildPosition(View view) {
        return getChildAdapterPosition(view);
    }

    public int getChildAdapterPosition(View view) {
        view = getChildViewHolderInt(view);
        return view != null ? view.getAdapterPosition() : -1;
    }

    public int getChildLayoutPosition(View view) {
        view = getChildViewHolderInt(view);
        return view != null ? view.getLayoutPosition() : -1;
    }

    public long getChildItemId(View view) {
        long j = -1;
        if (this.mAdapter != null) {
            if (this.mAdapter.hasStableIds()) {
                view = getChildViewHolderInt(view);
                if (view != null) {
                    j = view.getItemId();
                }
                return j;
            }
        }
        return -1;
    }

    @Deprecated
    public ViewHolder findViewHolderForPosition(int i) {
        return findViewHolderForPosition(i, false);
    }

    public ViewHolder findViewHolderForLayoutPosition(int i) {
        return findViewHolderForPosition(i, false);
    }

    public ViewHolder findViewHolderForAdapterPosition(int i) {
        ViewHolder viewHolder = null;
        if (this.mDataSetHasChangedAfterLayout) {
            return null;
        }
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i2 = 0; i2 < unfilteredChildCount; i2++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i2));
            if (!(childViewHolderInt == null || childViewHolderInt.isRemoved() || getAdapterPositionFor(childViewHolderInt) != i)) {
                if (!this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                    return childViewHolderInt;
                }
                viewHolder = childViewHolderInt;
            }
        }
        return viewHolder;
    }

    ViewHolder findViewHolderForPosition(int i, boolean z) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        ViewHolder viewHolder = null;
        for (int i2 = 0; i2 < unfilteredChildCount; i2++) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i2));
            if (!(childViewHolderInt == null || childViewHolderInt.isRemoved())) {
                if (z) {
                    if (childViewHolderInt.mPosition != i) {
                    }
                } else if (childViewHolderInt.getLayoutPosition() != i) {
                }
                if (!this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                    return childViewHolderInt;
                }
                viewHolder = childViewHolderInt;
            }
        }
        return viewHolder;
    }

    public ViewHolder findViewHolderForItemId(long j) {
        ViewHolder viewHolder = null;
        if (this.mAdapter != null) {
            if (this.mAdapter.hasStableIds()) {
                int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
                for (int i = 0; i < unfilteredChildCount; i++) {
                    ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
                    if (!(childViewHolderInt == null || childViewHolderInt.isRemoved() || childViewHolderInt.getItemId() != j)) {
                        if (!this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                            return childViewHolderInt;
                        }
                        viewHolder = childViewHolderInt;
                    }
                }
                return viewHolder;
            }
        }
        return null;
    }

    public View findChildViewUnder(float f, float f2) {
        for (int childCount = this.mChildHelper.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = this.mChildHelper.getChildAt(childCount);
            float translationX = childAt.getTranslationX();
            float translationY = childAt.getTranslationY();
            if (f >= ((float) childAt.getLeft()) + translationX && f <= ((float) childAt.getRight()) + translationX && f2 >= ((float) childAt.getTop()) + translationY && f2 <= ((float) childAt.getBottom()) + translationY) {
                return childAt;
            }
        }
        return 0.0f;
    }

    public boolean drawChild(Canvas canvas, View view, long j) {
        return super.drawChild(canvas, view, j);
    }

    public void offsetChildrenVertical(int i) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            this.mChildHelper.getChildAt(i2).offsetTopAndBottom(i);
        }
    }

    public void offsetChildrenHorizontal(int i) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            this.mChildHelper.getChildAt(i2).offsetLeftAndRight(i);
        }
    }

    public void getDecoratedBoundsWithMargins(View view, Rect rect) {
        getDecoratedBoundsWithMarginsInt(view, rect);
    }

    static void getDecoratedBoundsWithMarginsInt(View view, Rect rect) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        Rect rect2 = layoutParams.mDecorInsets;
        rect.set((view.getLeft() - rect2.left) - layoutParams.leftMargin, (view.getTop() - rect2.top) - layoutParams.topMargin, (view.getRight() + rect2.right) + layoutParams.rightMargin, (view.getBottom() + rect2.bottom) + layoutParams.bottomMargin);
    }

    Rect getItemDecorInsetsForChild(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (!layoutParams.mInsetsDirty) {
            return layoutParams.mDecorInsets;
        }
        if (this.mState.isPreLayout() && (layoutParams.isItemChanged() || layoutParams.isViewInvalid())) {
            return layoutParams.mDecorInsets;
        }
        Rect rect = layoutParams.mDecorInsets;
        rect.set(0, 0, 0, 0);
        int size = this.mItemDecorations.size();
        for (int i = 0; i < size; i++) {
            this.mTempRect.set(0, 0, 0, 0);
            ((ItemDecoration) this.mItemDecorations.get(i)).getItemOffsets(this.mTempRect, view, this, this.mState);
            rect.left += this.mTempRect.left;
            rect.top += this.mTempRect.top;
            rect.right += this.mTempRect.right;
            rect.bottom += this.mTempRect.bottom;
        }
        layoutParams.mInsetsDirty = false;
        return rect;
    }

    void dispatchOnScrolled(int i, int i2) {
        this.mDispatchScrollCounter++;
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        onScrollChanged(scrollX, scrollY, scrollX, scrollY);
        onScrolled(i, i2);
        if (this.mScrollListener != null) {
            this.mScrollListener.onScrolled(this, i, i2);
        }
        if (this.mScrollListeners != null) {
            for (scrollX = this.mScrollListeners.size() - 1; scrollX >= 0; scrollX--) {
                ((OnScrollListener) this.mScrollListeners.get(scrollX)).onScrolled(this, i, i2);
            }
        }
        this.mDispatchScrollCounter--;
    }

    void dispatchOnScrollStateChanged(int i) {
        if (this.mLayout != null) {
            this.mLayout.onScrollStateChanged(i);
        }
        onScrollStateChanged(i);
        if (this.mScrollListener != null) {
            this.mScrollListener.onScrollStateChanged(this, i);
        }
        if (this.mScrollListeners != null) {
            for (int size = this.mScrollListeners.size() - 1; size >= 0; size--) {
                ((OnScrollListener) this.mScrollListeners.get(size)).onScrollStateChanged(this, i);
            }
        }
    }

    public boolean hasPendingAdapterUpdates() {
        if (this.mFirstLayoutComplete && !this.mDataSetHasChangedAfterLayout) {
            if (!this.mAdapterHelper.hasPendingUpdates()) {
                return false;
            }
        }
        return true;
    }

    void repositionShadowingViews() {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mChildHelper.getChildAt(i);
            ViewHolder childViewHolder = getChildViewHolder(childAt);
            if (!(childViewHolder == null || childViewHolder.mShadowingHolder == null)) {
                View view = childViewHolder.mShadowingHolder.itemView;
                int left = childAt.getLeft();
                int top = childAt.getTop();
                if (left != view.getLeft() || top != view.getTop()) {
                    view.layout(left, top, view.getWidth() + left, view.getHeight() + top);
                }
            }
        }
    }

    static RecyclerView findNestedRecyclerView(View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        if (view instanceof RecyclerView) {
            return (RecyclerView) view;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RecyclerView findNestedRecyclerView = findNestedRecyclerView(viewGroup.getChildAt(i));
            if (findNestedRecyclerView != null) {
                return findNestedRecyclerView;
            }
        }
        return null;
    }

    static void clearNestedRecyclerViewIfNotNested(ViewHolder viewHolder) {
        if (viewHolder.mNestedRecyclerView != null) {
            View view = (View) viewHolder.mNestedRecyclerView.get();
            while (view != null) {
                if (view != viewHolder.itemView) {
                    ViewParent parent = view.getParent();
                    view = parent instanceof View ? (View) parent : null;
                } else {
                    return;
                }
            }
            viewHolder.mNestedRecyclerView = null;
        }
    }

    long getNanoTime() {
        return ALLOW_THREAD_GAP_WORK ? System.nanoTime() : 0;
    }

    void dispatchChildDetached(View view) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        onChildDetachedFromWindow(view);
        if (!(this.mAdapter == null || childViewHolderInt == null)) {
            this.mAdapter.onViewDetachedFromWindow(childViewHolderInt);
        }
        if (this.mOnChildAttachStateListeners != null) {
            for (int size = this.mOnChildAttachStateListeners.size() - 1; size >= 0; size--) {
                ((OnChildAttachStateChangeListener) this.mOnChildAttachStateListeners.get(size)).onChildViewDetachedFromWindow(view);
            }
        }
    }

    void dispatchChildAttached(View view) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        onChildAttachedToWindow(view);
        if (!(this.mAdapter == null || childViewHolderInt == null)) {
            this.mAdapter.onViewAttachedToWindow(childViewHolderInt);
        }
        if (this.mOnChildAttachStateListeners != null) {
            for (int size = this.mOnChildAttachStateListeners.size() - 1; size >= 0; size--) {
                ((OnChildAttachStateChangeListener) this.mOnChildAttachStateListeners.get(size)).onChildViewAttachedToWindow(view);
            }
        }
    }

    boolean setChildImportantForAccessibilityInternal(ViewHolder viewHolder, int i) {
        if (isComputingLayout()) {
            viewHolder.mPendingAccessibilityState = i;
            this.mPendingAccessibilityImportanceChange.add(viewHolder);
            return null;
        }
        ViewCompat.setImportantForAccessibility(viewHolder.itemView, i);
        return true;
    }

    void dispatchPendingImportantForAccessibilityChanges() {
        for (int size = this.mPendingAccessibilityImportanceChange.size() - 1; size >= 0; size--) {
            ViewHolder viewHolder = (ViewHolder) this.mPendingAccessibilityImportanceChange.get(size);
            if (viewHolder.itemView.getParent() == this) {
                if (!viewHolder.shouldIgnore()) {
                    int i = viewHolder.mPendingAccessibilityState;
                    if (i != -1) {
                        ViewCompat.setImportantForAccessibility(viewHolder.itemView, i);
                        viewHolder.mPendingAccessibilityState = -1;
                    }
                }
            }
        }
        this.mPendingAccessibilityImportanceChange.clear();
    }

    int getAdapterPositionFor(ViewHolder viewHolder) {
        if (!viewHolder.hasAnyOfTheFlags(524)) {
            if (viewHolder.isBound()) {
                return this.mAdapterHelper.applyPendingUpdatesToPosition(viewHolder.mPosition);
            }
        }
        return -1;
    }

    void initFastScroller(StateListDrawable stateListDrawable, Drawable drawable, StateListDrawable stateListDrawable2, Drawable drawable2) {
        if (!(stateListDrawable == null || drawable == null || stateListDrawable2 == null)) {
            if (drawable2 != null) {
                getContext().getResources();
                FastScroller fastScroller = new FastScroller(this, stateListDrawable, drawable, stateListDrawable2, drawable2, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(50.0f), 0);
                return;
            }
        }
        drawable = new StringBuilder();
        drawable.append("Trying to set fast scroller without both required drawables.");
        drawable.append(exceptionLabel());
        throw new IllegalArgumentException(drawable.toString());
    }

    public void setNestedScrollingEnabled(boolean z) {
        getScrollingChildHelper().setNestedScrollingEnabled(z);
    }

    public boolean isNestedScrollingEnabled() {
        return getScrollingChildHelper().isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int i) {
        return getScrollingChildHelper().startNestedScroll(i);
    }

    public boolean startNestedScroll(int i, int i2) {
        return getScrollingChildHelper().startNestedScroll(i, i2);
    }

    public void stopNestedScroll() {
        getScrollingChildHelper().stopNestedScroll();
    }

    public void stopNestedScroll(int i) {
        getScrollingChildHelper().stopNestedScroll(i);
    }

    public boolean hasNestedScrollingParent() {
        return getScrollingChildHelper().hasNestedScrollingParent();
    }

    public boolean hasNestedScrollingParent(int i) {
        return getScrollingChildHelper().hasNestedScrollingParent(i);
    }

    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr) {
        return getScrollingChildHelper().dispatchNestedScroll(i, i2, i3, i4, iArr);
    }

    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5) {
        return getScrollingChildHelper().dispatchNestedScroll(i, i2, i3, i4, iArr, i5);
    }

    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2) {
        return getScrollingChildHelper().dispatchNestedPreScroll(i, i2, iArr, iArr2);
    }

    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
        return getScrollingChildHelper().dispatchNestedPreScroll(i, i2, iArr, iArr2, i3);
    }

    public boolean dispatchNestedFling(float f, float f2, boolean z) {
        return getScrollingChildHelper().dispatchNestedFling(f, f2, z);
    }

    public boolean dispatchNestedPreFling(float f, float f2) {
        return getScrollingChildHelper().dispatchNestedPreFling(f, f2);
    }

    protected int getChildDrawingOrder(int i, int i2) {
        if (this.mChildDrawingOrderCallback == null) {
            return super.getChildDrawingOrder(i, i2);
        }
        return this.mChildDrawingOrderCallback.onGetChildDrawingOrder(i, i2);
    }

    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (this.mScrollingChildHelper == null) {
            this.mScrollingChildHelper = new NestedScrollingChildHelper(this);
        }
        return this.mScrollingChildHelper;
    }
}
