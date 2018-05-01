package org.telegram.messenger.support.widget.helper;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.ChildDrawingOrderCallback;
import org.telegram.messenger.support.widget.RecyclerView.ItemAnimator;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnChildAttachStateChangeListener;
import org.telegram.messenger.support.widget.RecyclerView.OnItemTouchListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;

public class ItemTouchHelper extends ItemDecoration implements OnChildAttachStateChangeListener {
    static final int ACTION_MODE_DRAG_MASK = 16711680;
    private static final int ACTION_MODE_IDLE_MASK = 255;
    static final int ACTION_MODE_SWIPE_MASK = 65280;
    public static final int ACTION_STATE_DRAG = 2;
    public static final int ACTION_STATE_IDLE = 0;
    public static final int ACTION_STATE_SWIPE = 1;
    static final int ACTIVE_POINTER_ID_NONE = -1;
    public static final int ANIMATION_TYPE_DRAG = 8;
    public static final int ANIMATION_TYPE_SWIPE_CANCEL = 4;
    public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 2;
    static final boolean DEBUG = false;
    static final int DIRECTION_FLAG_COUNT = 8;
    public static final int DOWN = 2;
    public static final int END = 32;
    public static final int LEFT = 4;
    private static final int PIXELS_PER_SECOND = 1000;
    public static final int RIGHT = 8;
    public static final int START = 16;
    static final String TAG = "ItemTouchHelper";
    public static final int UP = 1;
    int mActionState = 0;
    int mActivePointerId = -1;
    Callback mCallback;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback = null;
    private List<Integer> mDistances;
    private long mDragScrollStartTimeInMs;
    float mDx;
    float mDy;
    GestureDetectorCompat mGestureDetector;
    float mInitialTouchX;
    float mInitialTouchY;
    private ItemTouchHelperGestureListener mItemTouchHelperGestureListener;
    float mMaxSwipeVelocity;
    private final OnItemTouchListener mOnItemTouchListener = new C18752();
    View mOverdrawChild = null;
    int mOverdrawChildPosition = -1;
    final List<View> mPendingCleanup = new ArrayList();
    List<RecoverAnimation> mRecoverAnimations = new ArrayList();
    RecyclerView mRecyclerView;
    final Runnable mScrollRunnable = new C06661();
    ViewHolder mSelected = null;
    int mSelectedFlags;
    float mSelectedStartX;
    float mSelectedStartY;
    private int mSlop;
    private List<ViewHolder> mSwapTargets;
    float mSwipeEscapeVelocity;
    private final float[] mTmpPosition = new float[2];
    private Rect mTmpRect;
    VelocityTracker mVelocityTracker;

    /* renamed from: org.telegram.messenger.support.widget.helper.ItemTouchHelper$1 */
    class C06661 implements Runnable {
        C06661() {
        }

        public void run() {
            if (ItemTouchHelper.this.mSelected != null && ItemTouchHelper.this.scrollIfNecessary()) {
                if (ItemTouchHelper.this.mSelected != null) {
                    ItemTouchHelper.this.moveIfNecessary(ItemTouchHelper.this.mSelected);
                }
                ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
                ViewCompat.postOnAnimation(ItemTouchHelper.this.mRecyclerView, this);
            }
        }
    }

    public static abstract class Callback {
        private static final int ABS_HORIZONTAL_DIR_FLAGS = 789516;
        public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;
        public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;
        private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 500;
        static final int RELATIVE_DIR_FLAGS = 3158064;
        private static final Interpolator sDragScrollInterpolator = new C06681();
        private static final Interpolator sDragViewScrollCapInterpolator = new C06692();
        private static final ItemTouchUIUtil sUICallback;
        private int mCachedMaxScrollSpeed = -1;

        /* renamed from: org.telegram.messenger.support.widget.helper.ItemTouchHelper$Callback$1 */
        static class C06681 implements Interpolator {
            public float getInterpolation(float f) {
                return (((f * f) * f) * f) * f;
            }

            C06681() {
            }
        }

        /* renamed from: org.telegram.messenger.support.widget.helper.ItemTouchHelper$Callback$2 */
        static class C06692 implements Interpolator {
            public float getInterpolation(float f) {
                f -= 1.0f;
                return ((((f * f) * f) * f) * f) + 1.0f;
            }

            C06692() {
            }
        }

        public static int convertToRelativeDirection(int i, int i2) {
            int i3 = i & ABS_HORIZONTAL_DIR_FLAGS;
            if (i3 == 0) {
                return i;
            }
            i &= i3 ^ -1;
            if (i2 == 0) {
                return i | (i3 << 2);
            }
            i2 = i3 << 1;
            return (i | (-789517 & i2)) | ((i2 & ABS_HORIZONTAL_DIR_FLAGS) << 2);
        }

        public static int makeFlag(int i, int i2) {
            return i2 << (i * 8);
        }

        public boolean canDropOver(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
            return true;
        }

        public int convertToAbsoluteDirection(int i, int i2) {
            int i3 = i & RELATIVE_DIR_FLAGS;
            if (i3 == 0) {
                return i;
            }
            i &= i3 ^ -1;
            if (i2 == 0) {
                return i | (i3 >> 2);
            }
            i2 = i3 >> 1;
            return (i | (-3158065 & i2)) | ((i2 & RELATIVE_DIR_FLAGS) >> 2);
        }

        public int getBoundingBoxMargin() {
            return 0;
        }

        public float getMoveThreshold(ViewHolder viewHolder) {
            return 0.5f;
        }

        public abstract int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder);

        public float getSwipeEscapeVelocity(float f) {
            return f;
        }

        public float getSwipeThreshold(ViewHolder viewHolder) {
            return 0.5f;
        }

        public float getSwipeVelocityThreshold(float f) {
            return f;
        }

        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public abstract boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2);

        public abstract void onSwiped(ViewHolder viewHolder, int i);

        static {
            if (VERSION.SDK_INT >= 21) {
                sUICallback = new Api21Impl();
            } else {
                sUICallback = new BaseImpl();
            }
        }

        public static ItemTouchUIUtil getDefaultUIUtil() {
            return sUICallback;
        }

        public static int makeMovementFlags(int i, int i2) {
            return makeFlag(2, i) | (makeFlag(1, i2) | makeFlag(0, i2 | i));
        }

        final int getAbsoluteMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return convertToAbsoluteDirection(getMovementFlags(recyclerView, viewHolder), ViewCompat.getLayoutDirection(recyclerView));
        }

        boolean hasDragFlag(RecyclerView recyclerView, ViewHolder viewHolder) {
            return (getAbsoluteMovementFlags(recyclerView, viewHolder) & ItemTouchHelper.ACTION_MODE_DRAG_MASK) != null ? true : null;
        }

        boolean hasSwipeFlag(RecyclerView recyclerView, ViewHolder viewHolder) {
            return (getAbsoluteMovementFlags(recyclerView, viewHolder) & ItemTouchHelper.ACTION_MODE_SWIPE_MASK) != null ? true : null;
        }

        public ViewHolder chooseDropTarget(ViewHolder viewHolder, List<ViewHolder> list, int i, int i2) {
            ViewHolder viewHolder2 = viewHolder;
            int width = i + viewHolder2.itemView.getWidth();
            int height = i2 + viewHolder2.itemView.getHeight();
            int left = i - viewHolder2.itemView.getLeft();
            int top = i2 - viewHolder2.itemView.getTop();
            int size = list.size();
            ViewHolder viewHolder3 = null;
            int i3 = -1;
            for (int i4 = 0; i4 < size; i4++) {
                int right;
                ViewHolder viewHolder4 = (ViewHolder) list.get(i4);
                if (left > 0) {
                    right = viewHolder4.itemView.getRight() - width;
                    if (right < 0 && viewHolder4.itemView.getRight() > viewHolder2.itemView.getRight()) {
                        right = Math.abs(right);
                        if (right > i3) {
                            viewHolder3 = viewHolder4;
                            i3 = right;
                        }
                    }
                }
                if (left < 0) {
                    right = viewHolder4.itemView.getLeft() - i;
                    if (right > 0 && viewHolder4.itemView.getLeft() < viewHolder2.itemView.getLeft()) {
                        right = Math.abs(right);
                        if (right > i3) {
                            viewHolder3 = viewHolder4;
                            i3 = right;
                        }
                    }
                }
                if (top < 0) {
                    right = viewHolder4.itemView.getTop() - i2;
                    if (right > 0 && viewHolder4.itemView.getTop() < viewHolder2.itemView.getTop()) {
                        right = Math.abs(right);
                        if (right > i3) {
                            viewHolder3 = viewHolder4;
                            i3 = right;
                        }
                    }
                }
                if (top > 0) {
                    right = viewHolder4.itemView.getBottom() - height;
                    if (right < 0 && viewHolder4.itemView.getBottom() > viewHolder2.itemView.getBottom()) {
                        right = Math.abs(right);
                        if (right > i3) {
                            viewHolder3 = viewHolder4;
                            i3 = right;
                        }
                    }
                }
            }
            return viewHolder3;
        }

        public void onSelectedChanged(ViewHolder viewHolder, int i) {
            if (viewHolder != null) {
                sUICallback.onSelected(viewHolder.itemView);
            }
        }

        private int getMaxDragScroll(RecyclerView recyclerView) {
            if (this.mCachedMaxScrollSpeed == -1) {
                this.mCachedMaxScrollSpeed = AndroidUtilities.dp(20.0f);
            }
            return this.mCachedMaxScrollSpeed;
        }

        public void onMoved(RecyclerView recyclerView, ViewHolder viewHolder, int i, ViewHolder viewHolder2, int i2, int i3, int i4) {
            i = recyclerView.getLayoutManager();
            if (i instanceof ViewDropHandler) {
                ((ViewDropHandler) i).prepareForDrop(viewHolder.itemView, viewHolder2.itemView, i3, i4);
                return;
            }
            if (i.canScrollHorizontally() != null) {
                if (i.getDecoratedLeft(viewHolder2.itemView) <= recyclerView.getPaddingLeft()) {
                    recyclerView.scrollToPosition(i2);
                }
                if (i.getDecoratedRight(viewHolder2.itemView) >= recyclerView.getWidth() - recyclerView.getPaddingRight()) {
                    recyclerView.scrollToPosition(i2);
                }
            }
            if (i.canScrollVertically() != null) {
                if (i.getDecoratedTop(viewHolder2.itemView) <= recyclerView.getPaddingTop()) {
                    recyclerView.scrollToPosition(i2);
                }
                if (i.getDecoratedBottom(viewHolder2.itemView) >= recyclerView.getHeight() - recyclerView.getPaddingBottom()) {
                    recyclerView.scrollToPosition(i2);
                }
            }
        }

        void onDraw(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, List<RecoverAnimation> list, int i, float f, float f2) {
            Canvas canvas2 = canvas;
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                RecoverAnimation recoverAnimation = (RecoverAnimation) list.get(i2);
                recoverAnimation.update();
                int save = canvas2.save();
                onChildDraw(canvas2, recyclerView, recoverAnimation.mViewHolder, recoverAnimation.mX, recoverAnimation.mY, recoverAnimation.mActionState, false);
                canvas2.restoreToCount(save);
            }
            if (viewHolder != null) {
                size = canvas2.save();
                onChildDraw(canvas2, recyclerView, viewHolder, f, f2, i, true);
                canvas2.restoreToCount(size);
            }
        }

        void onDrawOver(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, List<RecoverAnimation> list, int i, float f, float f2) {
            int i2;
            Canvas canvas2 = canvas;
            List<RecoverAnimation> list2 = list;
            int size = list.size();
            int i3 = 0;
            for (i2 = 0; i2 < size; i2++) {
                RecoverAnimation recoverAnimation = (RecoverAnimation) list2.get(i2);
                int save = canvas2.save();
                onChildDrawOver(canvas2, recyclerView, recoverAnimation.mViewHolder, recoverAnimation.mX, recoverAnimation.mY, recoverAnimation.mActionState, false);
                canvas2.restoreToCount(save);
            }
            if (viewHolder != null) {
                i2 = canvas2.save();
                onChildDrawOver(canvas2, recyclerView, viewHolder, f, f2, i, true);
                canvas2.restoreToCount(i2);
            }
            for (size--; size >= 0; size--) {
                RecoverAnimation recoverAnimation2 = (RecoverAnimation) list2.get(size);
                if (recoverAnimation2.mEnded && !recoverAnimation2.mIsPendingCleanup) {
                    list2.remove(size);
                } else if (!recoverAnimation2.mEnded) {
                    i3 = 1;
                }
            }
            if (i3 != 0) {
                recyclerView.invalidate();
            }
        }

        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            sUICallback.clearView(viewHolder.itemView);
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            sUICallback.onDraw(canvas, recyclerView, viewHolder.itemView, f, f2, i, z);
        }

        public void onChildDrawOver(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            sUICallback.onDrawOver(canvas, recyclerView, viewHolder.itemView, f, f2, i, z);
        }

        public long getAnimationDuration(RecyclerView recyclerView, int i, float f, float f2) {
            recyclerView = recyclerView.getItemAnimator();
            if (recyclerView == null) {
                return i == 8 ? DEFAULT_DRAG_ANIMATION_DURATION : DEFAULT_SWIPE_ANIMATION_DURATION;
            }
            if (i == 8) {
                recyclerView = recyclerView.getMoveDuration();
            } else {
                recyclerView = recyclerView.getRemoveDuration();
            }
            return recyclerView;
        }

        public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int i, int i2, int i3, long j) {
            float f = 1.0f;
            recyclerView = (int) (((float) (((int) Math.signum((float) i2)) * getMaxDragScroll(recyclerView))) * sDragViewScrollCapInterpolator.getInterpolation(Math.min(1.0f, (((float) Math.abs(i2)) * NUM) / ((float) i))));
            if (j <= DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
                f = ((float) j) / NUM;
            }
            recyclerView = (int) (((float) recyclerView) * sDragScrollInterpolator.getInterpolation(f));
            if (recyclerView != null) {
                return recyclerView;
            }
            return i2 > 0 ? true : -1;
        }
    }

    private class ItemTouchHelperGestureListener extends SimpleOnGestureListener {
        private boolean mShouldReactToLongPress = true;

        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        ItemTouchHelperGestureListener() {
        }

        void doNotReactToLongPress() {
            this.mShouldReactToLongPress = false;
        }

        public void onLongPress(MotionEvent motionEvent) {
            if (this.mShouldReactToLongPress) {
                View findChildView = ItemTouchHelper.this.findChildView(motionEvent);
                if (findChildView != null) {
                    ViewHolder childViewHolder = ItemTouchHelper.this.mRecyclerView.getChildViewHolder(findChildView);
                    if (childViewHolder != null && ItemTouchHelper.this.mCallback.hasDragFlag(ItemTouchHelper.this.mRecyclerView, childViewHolder) && motionEvent.getPointerId(0) == ItemTouchHelper.this.mActivePointerId) {
                        int findPointerIndex = motionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
                        float x = motionEvent.getX(findPointerIndex);
                        motionEvent = motionEvent.getY(findPointerIndex);
                        ItemTouchHelper.this.mInitialTouchX = x;
                        ItemTouchHelper.this.mInitialTouchY = motionEvent;
                        motionEvent = ItemTouchHelper.this;
                        ItemTouchHelper.this.mDy = 0.0f;
                        motionEvent.mDx = 0.0f;
                        if (ItemTouchHelper.this.mCallback.isLongPressDragEnabled() != null) {
                            ItemTouchHelper.this.select(childViewHolder, 2);
                        }
                    }
                }
            }
        }
    }

    private static class RecoverAnimation implements AnimatorListener {
        final int mActionState;
        final int mAnimationType;
        boolean mEnded = false;
        private float mFraction;
        public boolean mIsPendingCleanup;
        boolean mOverridden = false;
        final float mStartDx;
        final float mStartDy;
        final float mTargetX;
        final float mTargetY;
        private final ValueAnimator mValueAnimator;
        final ViewHolder mViewHolder;
        float mX;
        float mY;

        /* renamed from: org.telegram.messenger.support.widget.helper.ItemTouchHelper$RecoverAnimation$1 */
        class C06701 implements AnimatorUpdateListener {
            C06701() {
            }

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RecoverAnimation.this.setFraction(valueAnimator.getAnimatedFraction());
            }
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        RecoverAnimation(ViewHolder viewHolder, int i, int i2, float f, float f2, float f3, float f4) {
            this.mActionState = i2;
            this.mAnimationType = i;
            this.mViewHolder = viewHolder;
            this.mStartDx = f;
            this.mStartDy = f2;
            this.mTargetX = f3;
            this.mTargetY = f4;
            this.mValueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.mValueAnimator.addUpdateListener(new C06701());
            this.mValueAnimator.setTarget(viewHolder.itemView);
            this.mValueAnimator.addListener(this);
            setFraction(null);
        }

        public void setDuration(long j) {
            this.mValueAnimator.setDuration(j);
        }

        public void start() {
            this.mViewHolder.setIsRecyclable(false);
            this.mValueAnimator.start();
        }

        public void cancel() {
            this.mValueAnimator.cancel();
        }

        public void setFraction(float f) {
            this.mFraction = f;
        }

        public void update() {
            if (this.mStartDx == this.mTargetX) {
                this.mX = this.mViewHolder.itemView.getTranslationX();
            } else {
                this.mX = this.mStartDx + (this.mFraction * (this.mTargetX - this.mStartDx));
            }
            if (this.mStartDy == this.mTargetY) {
                this.mY = this.mViewHolder.itemView.getTranslationY();
            } else {
                this.mY = this.mStartDy + (this.mFraction * (this.mTargetY - this.mStartDy));
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (this.mEnded == null) {
                this.mViewHolder.setIsRecyclable(true);
            }
            this.mEnded = true;
        }

        public void onAnimationCancel(Animator animator) {
            setFraction(1.0f);
        }
    }

    public interface ViewDropHandler {
        void prepareForDrop(View view, View view2, int i, int i2);
    }

    /* renamed from: org.telegram.messenger.support.widget.helper.ItemTouchHelper$2 */
    class C18752 implements OnItemTouchListener {
        C18752() {
        }

        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            ItemTouchHelper.this.mGestureDetector.onTouchEvent(motionEvent);
            recyclerView = motionEvent.getActionMasked();
            if (recyclerView == null) {
                ItemTouchHelper.this.mActivePointerId = motionEvent.getPointerId(0);
                ItemTouchHelper.this.mInitialTouchX = motionEvent.getX();
                ItemTouchHelper.this.mInitialTouchY = motionEvent.getY();
                ItemTouchHelper.this.obtainVelocityTracker();
                if (ItemTouchHelper.this.mSelected == null) {
                    recyclerView = ItemTouchHelper.this.findAnimation(motionEvent);
                    if (recyclerView != null) {
                        ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
                        itemTouchHelper.mInitialTouchX -= recyclerView.mX;
                        itemTouchHelper = ItemTouchHelper.this;
                        itemTouchHelper.mInitialTouchY -= recyclerView.mY;
                        ItemTouchHelper.this.endRecoverAnimation(recyclerView.mViewHolder, true);
                        if (ItemTouchHelper.this.mPendingCleanup.remove(recyclerView.mViewHolder.itemView)) {
                            ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, recyclerView.mViewHolder);
                        }
                        ItemTouchHelper.this.select(recyclerView.mViewHolder, recyclerView.mActionState);
                        ItemTouchHelper.this.updateDxDy(motionEvent, ItemTouchHelper.this.mSelectedFlags, 0);
                    }
                }
            } else {
                if (recyclerView != 3) {
                    if (recyclerView != 1) {
                        if (ItemTouchHelper.this.mActivePointerId != -1) {
                            int findPointerIndex = motionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
                            if (findPointerIndex >= 0) {
                                ItemTouchHelper.this.checkSelectForSwipe(recyclerView, motionEvent, findPointerIndex);
                            }
                        }
                    }
                }
                ItemTouchHelper.this.mActivePointerId = -1;
                ItemTouchHelper.this.select(null, 0);
            }
            if (ItemTouchHelper.this.mVelocityTracker != null) {
                ItemTouchHelper.this.mVelocityTracker.addMovement(motionEvent);
            }
            if (ItemTouchHelper.this.mSelected != null) {
                return true;
            }
            return false;
        }

        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            ItemTouchHelper.this.mGestureDetector.onTouchEvent(motionEvent);
            if (ItemTouchHelper.this.mVelocityTracker != null) {
                ItemTouchHelper.this.mVelocityTracker.addMovement(motionEvent);
            }
            if (ItemTouchHelper.this.mActivePointerId != -1) {
                recyclerView = motionEvent.getActionMasked();
                int findPointerIndex = motionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
                if (findPointerIndex >= 0) {
                    ItemTouchHelper.this.checkSelectForSwipe(recyclerView, motionEvent, findPointerIndex);
                }
                ViewHolder viewHolder = ItemTouchHelper.this.mSelected;
                if (viewHolder != null) {
                    int i = 0;
                    if (recyclerView != 6) {
                        switch (recyclerView) {
                            case 1:
                                break;
                            case 2:
                                if (findPointerIndex >= 0) {
                                    ItemTouchHelper.this.updateDxDy(motionEvent, ItemTouchHelper.this.mSelectedFlags, findPointerIndex);
                                    ItemTouchHelper.this.moveIfNecessary(viewHolder);
                                    ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
                                    ItemTouchHelper.this.mScrollRunnable.run();
                                    ItemTouchHelper.this.mRecyclerView.invalidate();
                                    break;
                                }
                                break;
                            case 3:
                                if (ItemTouchHelper.this.mVelocityTracker != null) {
                                    ItemTouchHelper.this.mVelocityTracker.clear();
                                    break;
                                }
                                break;
                            default:
                                break;
                        }
                        ItemTouchHelper.this.select(null, 0);
                        ItemTouchHelper.this.mActivePointerId = -1;
                    } else {
                        recyclerView = motionEvent.getActionIndex();
                        if (motionEvent.getPointerId(recyclerView) == ItemTouchHelper.this.mActivePointerId) {
                            if (recyclerView == null) {
                                i = 1;
                            }
                            ItemTouchHelper.this.mActivePointerId = motionEvent.getPointerId(i);
                            ItemTouchHelper.this.updateDxDy(motionEvent, ItemTouchHelper.this.mSelectedFlags, recyclerView);
                        }
                    }
                }
            }
        }

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
            if (z) {
                ItemTouchHelper.this.select(null, 0);
            }
        }
    }

    /* renamed from: org.telegram.messenger.support.widget.helper.ItemTouchHelper$5 */
    class C18775 implements ChildDrawingOrderCallback {
        C18775() {
        }

        public int onGetChildDrawingOrder(int i, int i2) {
            if (ItemTouchHelper.this.mOverdrawChild == null) {
                return i2;
            }
            int i3 = ItemTouchHelper.this.mOverdrawChildPosition;
            if (i3 == -1) {
                i3 = ItemTouchHelper.this.mRecyclerView.indexOfChild(ItemTouchHelper.this.mOverdrawChild);
                ItemTouchHelper.this.mOverdrawChildPosition = i3;
            }
            if (i2 == i - 1) {
                return i3;
            }
            if (i2 >= i3) {
                i2++;
            }
            return i2;
        }
    }

    public static abstract class SimpleCallback extends Callback {
        private int mDefaultDragDirs;
        private int mDefaultSwipeDirs;

        public SimpleCallback(int i, int i2) {
            this.mDefaultSwipeDirs = i2;
            this.mDefaultDragDirs = i;
        }

        public void setDefaultSwipeDirs(int i) {
            this.mDefaultSwipeDirs = i;
        }

        public void setDefaultDragDirs(int i) {
            this.mDefaultDragDirs = i;
        }

        public int getSwipeDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
            return this.mDefaultSwipeDirs;
        }

        public int getDragDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
            return this.mDefaultDragDirs;
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return Callback.makeMovementFlags(getDragDirs(recyclerView, viewHolder), getSwipeDirs(recyclerView, viewHolder));
        }
    }

    public void onChildViewAttachedToWindow(View view) {
    }

    public ItemTouchHelper(Callback callback) {
        this.mCallback = callback;
    }

    private static boolean hitTest(View view, float f, float f2, float f3, float f4) {
        return (f < f3 || f > f3 + ((float) view.getWidth()) || f2 < f4 || f2 > f4 + ((float) view.getHeight())) ? null : true;
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        if (this.mRecyclerView != recyclerView) {
            if (this.mRecyclerView != null) {
                destroyCallbacks();
            }
            this.mRecyclerView = recyclerView;
            if (this.mRecyclerView != null) {
                recyclerView.getResources();
                this.mSwipeEscapeVelocity = (float) AndroidUtilities.dp(120.0f);
                this.mMaxSwipeVelocity = (float) AndroidUtilities.dp(800.0f);
                setupCallbacks();
            }
        }
    }

    private void setupCallbacks() {
        this.mSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
        this.mRecyclerView.addItemDecoration(this);
        this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.addOnChildAttachStateChangeListener(this);
        startGestureDetection();
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeItemDecoration(this);
        this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            this.mCallback.clearView(this.mRecyclerView, ((RecoverAnimation) this.mRecoverAnimations.get(0)).mViewHolder);
        }
        this.mRecoverAnimations.clear();
        this.mOverdrawChild = null;
        this.mOverdrawChildPosition = -1;
        releaseVelocityTracker();
        stopGestureDetection();
    }

    private void startGestureDetection() {
        this.mItemTouchHelperGestureListener = new ItemTouchHelperGestureListener();
        this.mGestureDetector = new GestureDetectorCompat(this.mRecyclerView.getContext(), this.mItemTouchHelperGestureListener);
    }

    private void stopGestureDetection() {
        if (this.mItemTouchHelperGestureListener != null) {
            this.mItemTouchHelperGestureListener.doNotReactToLongPress();
            this.mItemTouchHelperGestureListener = null;
        }
        if (this.mGestureDetector != null) {
            this.mGestureDetector = null;
        }
    }

    private void getSelectedDxDy(float[] fArr) {
        if ((this.mSelectedFlags & 12) != 0) {
            fArr[0] = (this.mSelectedStartX + this.mDx) - ((float) this.mSelected.itemView.getLeft());
        } else {
            fArr[0] = this.mSelected.itemView.getTranslationX();
        }
        if ((this.mSelectedFlags & 3) != 0) {
            fArr[1] = (this.mSelectedStartY + this.mDy) - ((float) this.mSelected.itemView.getTop());
        } else {
            fArr[1] = this.mSelected.itemView.getTranslationY();
        }
    }

    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, State state) {
        float f;
        float f2;
        if (this.mSelected != null) {
            getSelectedDxDy(this.mTmpPosition);
            float f3 = this.mTmpPosition[0];
            f = this.mTmpPosition[1];
            f2 = f3;
        } else {
            f2 = 0.0f;
            f = f2;
        }
        this.mCallback.onDrawOver(canvas, recyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f2, f);
    }

    public void onDraw(Canvas canvas, RecyclerView recyclerView, State state) {
        float f;
        float f2;
        this.mOverdrawChildPosition = -1;
        if (this.mSelected != null) {
            getSelectedDxDy(this.mTmpPosition);
            float f3 = this.mTmpPosition[0];
            f = this.mTmpPosition[1];
            f2 = f3;
        } else {
            f2 = 0.0f;
            f = f2;
        }
        this.mCallback.onDraw(canvas, recyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f2, f);
    }

    void select(ViewHolder viewHolder, int i) {
        ViewHolder viewHolder2 = viewHolder;
        int i2 = i;
        if (viewHolder2 != this.mSelected || i2 != r11.mActionState) {
            Object obj;
            boolean z;
            ViewParent parent;
            r11.mDragScrollStartTimeInMs = Long.MIN_VALUE;
            int i3 = r11.mActionState;
            endRecoverAnimation(viewHolder2, true);
            r11.mActionState = i2;
            if (i2 == 2) {
                r11.mOverdrawChild = viewHolder2.itemView;
                addChildDrawingOrderCallback();
            }
            int i4 = (1 << ((8 * i2) + 8)) - 1;
            int i5;
            if (r11.mSelected != null) {
                ViewHolder viewHolder3 = r11.mSelected;
                if (viewHolder3.itemView.getParent() != null) {
                    int i6;
                    float f;
                    float signum;
                    if (i3 == 2) {
                        i6 = 0;
                    } else {
                        i6 = swipeIfNecessary(viewHolder3);
                    }
                    releaseVelocityTracker();
                    if (i6 != 4 && i6 != 8 && i6 != 16 && i6 != 32) {
                        switch (i6) {
                            case 1:
                            case 2:
                                f = 0.0f;
                                signum = Math.signum(r11.mDy) * ((float) r11.mRecyclerView.getHeight());
                                break;
                            default:
                                f = 0.0f;
                                signum = f;
                                break;
                        }
                    }
                    signum = 0.0f;
                    f = Math.signum(r11.mDx) * ((float) r11.mRecyclerView.getWidth());
                    int i7 = i3 == 2 ? 8 : i6 > 0 ? 2 : 4;
                    getSelectedDxDy(r11.mTmpPosition);
                    float f2 = r11.mTmpPosition[0];
                    float f3 = r11.mTmpPosition[1];
                    RecoverAnimation recoverAnimation = r0;
                    int i8 = i7;
                    final int i9 = i6;
                    i5 = 8;
                    final ViewHolder viewHolder4 = viewHolder3;
                    RecoverAnimation c18763 = new RecoverAnimation(viewHolder3, i7, i3, f2, f3, f, signum) {
                        public void onAnimationEnd(Animator animator) {
                            super.onAnimationEnd(animator);
                            if (this.mOverridden == null) {
                                if (i9 <= null) {
                                    ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, viewHolder4);
                                } else {
                                    ItemTouchHelper.this.mPendingCleanup.add(viewHolder4.itemView);
                                    this.mIsPendingCleanup = true;
                                    if (i9 > null) {
                                        ItemTouchHelper.this.postDispatchSwipe(this, i9);
                                    }
                                }
                                if (ItemTouchHelper.this.mOverdrawChild == viewHolder4.itemView) {
                                    ItemTouchHelper.this.removeChildDrawingOrderCallbackIfNecessary(viewHolder4.itemView);
                                }
                            }
                        }
                    };
                    recoverAnimation.setDuration(r11.mCallback.getAnimationDuration(r11.mRecyclerView, i8, f - f2, signum - f3));
                    r11.mRecoverAnimations.add(recoverAnimation);
                    recoverAnimation.start();
                    obj = 1;
                } else {
                    ViewHolder viewHolder5 = viewHolder3;
                    i5 = 8;
                    removeChildDrawingOrderCallbackIfNecessary(viewHolder5.itemView);
                    r11.mCallback.clearView(r11.mRecyclerView, viewHolder5);
                    obj = null;
                }
                r11.mSelected = null;
            } else {
                i5 = 8;
                obj = null;
            }
            if (viewHolder2 != null) {
                r11.mSelectedFlags = (r11.mCallback.getAbsoluteMovementFlags(r11.mRecyclerView, viewHolder2) & i4) >> (r11.mActionState * 8);
                r11.mSelectedStartX = (float) viewHolder2.itemView.getLeft();
                r11.mSelectedStartY = (float) viewHolder2.itemView.getTop();
                r11.mSelected = viewHolder2;
                if (i2 == 2) {
                    z = false;
                    r11.mSelected.itemView.performHapticFeedback(0);
                    parent = r11.mRecyclerView.getParent();
                    if (parent != null) {
                        if (r11.mSelected != null) {
                            z = true;
                        }
                        parent.requestDisallowInterceptTouchEvent(z);
                    }
                    if (obj == null) {
                        r11.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
                    }
                    r11.mCallback.onSelectedChanged(r11.mSelected, r11.mActionState);
                    r11.mRecyclerView.invalidate();
                }
            }
            z = false;
            parent = r11.mRecyclerView.getParent();
            if (parent != null) {
                if (r11.mSelected != null) {
                    z = true;
                }
                parent.requestDisallowInterceptTouchEvent(z);
            }
            if (obj == null) {
                r11.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
            }
            r11.mCallback.onSelectedChanged(r11.mSelected, r11.mActionState);
            r11.mRecyclerView.invalidate();
        }
    }

    void postDispatchSwipe(final RecoverAnimation recoverAnimation, final int i) {
        this.mRecyclerView.post(new Runnable() {
            public void run() {
                if (ItemTouchHelper.this.mRecyclerView != null && ItemTouchHelper.this.mRecyclerView.isAttachedToWindow() && !recoverAnimation.mOverridden && recoverAnimation.mViewHolder.getAdapterPosition() != -1) {
                    ItemAnimator itemAnimator = ItemTouchHelper.this.mRecyclerView.getItemAnimator();
                    if ((itemAnimator == null || !itemAnimator.isRunning(null)) && !ItemTouchHelper.this.hasRunningRecoverAnim()) {
                        ItemTouchHelper.this.mCallback.onSwiped(recoverAnimation.mViewHolder, i);
                    } else {
                        ItemTouchHelper.this.mRecyclerView.post(this);
                    }
                }
            }
        });
    }

    boolean hasRunningRecoverAnim() {
        int size = this.mRecoverAnimations.size();
        for (int i = 0; i < size; i++) {
            if (!((RecoverAnimation) this.mRecoverAnimations.get(i)).mEnded) {
                return true;
            }
        }
        return false;
    }

    boolean scrollIfNecessary() {
        if (this.mSelected == null) {
            r0.mDragScrollStartTimeInMs = Long.MIN_VALUE;
            return false;
        }
        int i;
        int i2;
        int i3;
        long currentTimeMillis = System.currentTimeMillis();
        long j = r0.mDragScrollStartTimeInMs == Long.MIN_VALUE ? 0 : currentTimeMillis - r0.mDragScrollStartTimeInMs;
        LayoutManager layoutManager = r0.mRecyclerView.getLayoutManager();
        if (r0.mTmpRect == null) {
            r0.mTmpRect = new Rect();
        }
        layoutManager.calculateItemDecorationsForChild(r0.mSelected.itemView, r0.mTmpRect);
        if (layoutManager.canScrollHorizontally()) {
            i = (int) (r0.mSelectedStartX + r0.mDx);
            int paddingLeft = (i - r0.mTmpRect.left) - r0.mRecyclerView.getPaddingLeft();
            if (r0.mDx < 0.0f && paddingLeft < 0) {
                i2 = paddingLeft;
                if (layoutManager.canScrollVertically()) {
                    i3 = (int) (r0.mSelectedStartY + r0.mDy);
                    i = (i3 - r0.mTmpRect.top) - r0.mRecyclerView.getPaddingTop();
                    if (r0.mDy >= 0.0f) {
                    }
                    if (r0.mDy > 0.0f) {
                        i3 = ((i3 + r0.mSelected.itemView.getHeight()) + r0.mTmpRect.bottom) - (r0.mRecyclerView.getHeight() - r0.mRecyclerView.getPaddingBottom());
                        if (i3 > 0) {
                            if (i2 != 0) {
                                i2 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getWidth(), i2, r0.mRecyclerView.getWidth(), j);
                            }
                            i = i2;
                            if (i3 != 0) {
                                i3 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getHeight(), i3, r0.mRecyclerView.getHeight(), j);
                            }
                            if (i == 0) {
                                if (i3 != 0) {
                                    r0.mDragScrollStartTimeInMs = Long.MIN_VALUE;
                                    return false;
                                }
                            }
                            if (r0.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                                r0.mDragScrollStartTimeInMs = currentTimeMillis;
                            }
                            r0.mRecyclerView.scrollBy(i, i3);
                            return true;
                        }
                    }
                }
                i3 = 0;
                if (i2 != 0) {
                    i2 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getWidth(), i2, r0.mRecyclerView.getWidth(), j);
                }
                i = i2;
                if (i3 != 0) {
                    i3 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getHeight(), i3, r0.mRecyclerView.getHeight(), j);
                }
                if (i == 0) {
                    if (i3 != 0) {
                        r0.mDragScrollStartTimeInMs = Long.MIN_VALUE;
                        return false;
                    }
                }
                if (r0.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                    r0.mDragScrollStartTimeInMs = currentTimeMillis;
                }
                r0.mRecyclerView.scrollBy(i, i3);
                return true;
            } else if (r0.mDx > 0.0f) {
                i = ((i + r0.mSelected.itemView.getWidth()) + r0.mTmpRect.right) - (r0.mRecyclerView.getWidth() - r0.mRecyclerView.getPaddingRight());
                if (i > 0) {
                    i2 = i;
                    if (layoutManager.canScrollVertically()) {
                        i3 = (int) (r0.mSelectedStartY + r0.mDy);
                        i = (i3 - r0.mTmpRect.top) - r0.mRecyclerView.getPaddingTop();
                        if (r0.mDy >= 0.0f && i < 0) {
                            i3 = i;
                            if (i2 != 0) {
                                i2 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getWidth(), i2, r0.mRecyclerView.getWidth(), j);
                            }
                            i = i2;
                            if (i3 != 0) {
                                i3 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getHeight(), i3, r0.mRecyclerView.getHeight(), j);
                            }
                            if (i == 0) {
                                if (i3 != 0) {
                                    r0.mDragScrollStartTimeInMs = Long.MIN_VALUE;
                                    return false;
                                }
                            }
                            if (r0.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                                r0.mDragScrollStartTimeInMs = currentTimeMillis;
                            }
                            r0.mRecyclerView.scrollBy(i, i3);
                            return true;
                        } else if (r0.mDy > 0.0f) {
                            i3 = ((i3 + r0.mSelected.itemView.getHeight()) + r0.mTmpRect.bottom) - (r0.mRecyclerView.getHeight() - r0.mRecyclerView.getPaddingBottom());
                            if (i3 > 0) {
                                if (i2 != 0) {
                                    i2 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getWidth(), i2, r0.mRecyclerView.getWidth(), j);
                                }
                                i = i2;
                                if (i3 != 0) {
                                    i3 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getHeight(), i3, r0.mRecyclerView.getHeight(), j);
                                }
                                if (i == 0) {
                                    if (i3 != 0) {
                                        r0.mDragScrollStartTimeInMs = Long.MIN_VALUE;
                                        return false;
                                    }
                                }
                                if (r0.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                                    r0.mDragScrollStartTimeInMs = currentTimeMillis;
                                }
                                r0.mRecyclerView.scrollBy(i, i3);
                                return true;
                            }
                        }
                    }
                    i3 = 0;
                    if (i2 != 0) {
                        i2 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getWidth(), i2, r0.mRecyclerView.getWidth(), j);
                    }
                    i = i2;
                    if (i3 != 0) {
                        i3 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getHeight(), i3, r0.mRecyclerView.getHeight(), j);
                    }
                    if (i == 0) {
                        if (i3 != 0) {
                            r0.mDragScrollStartTimeInMs = Long.MIN_VALUE;
                            return false;
                        }
                    }
                    if (r0.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                        r0.mDragScrollStartTimeInMs = currentTimeMillis;
                    }
                    r0.mRecyclerView.scrollBy(i, i3);
                    return true;
                }
            }
        }
        i2 = 0;
        if (layoutManager.canScrollVertically()) {
            i3 = (int) (r0.mSelectedStartY + r0.mDy);
            i = (i3 - r0.mTmpRect.top) - r0.mRecyclerView.getPaddingTop();
            if (r0.mDy >= 0.0f) {
            }
            if (r0.mDy > 0.0f) {
                i3 = ((i3 + r0.mSelected.itemView.getHeight()) + r0.mTmpRect.bottom) - (r0.mRecyclerView.getHeight() - r0.mRecyclerView.getPaddingBottom());
                if (i3 > 0) {
                    if (i2 != 0) {
                        i2 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getWidth(), i2, r0.mRecyclerView.getWidth(), j);
                    }
                    i = i2;
                    if (i3 != 0) {
                        i3 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getHeight(), i3, r0.mRecyclerView.getHeight(), j);
                    }
                    if (i == 0) {
                        if (i3 != 0) {
                            r0.mDragScrollStartTimeInMs = Long.MIN_VALUE;
                            return false;
                        }
                    }
                    if (r0.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                        r0.mDragScrollStartTimeInMs = currentTimeMillis;
                    }
                    r0.mRecyclerView.scrollBy(i, i3);
                    return true;
                }
            }
        }
        i3 = 0;
        if (i2 != 0) {
            i2 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getWidth(), i2, r0.mRecyclerView.getWidth(), j);
        }
        i = i2;
        if (i3 != 0) {
            i3 = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getHeight(), i3, r0.mRecyclerView.getHeight(), j);
        }
        if (i == 0) {
            if (i3 != 0) {
                r0.mDragScrollStartTimeInMs = Long.MIN_VALUE;
                return false;
            }
        }
        if (r0.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
            r0.mDragScrollStartTimeInMs = currentTimeMillis;
        }
        r0.mRecyclerView.scrollBy(i, i3);
        return true;
    }

    private List<ViewHolder> findSwapTargets(ViewHolder viewHolder) {
        ViewHolder viewHolder2 = viewHolder;
        if (this.mSwapTargets == null) {
            r0.mSwapTargets = new ArrayList();
            r0.mDistances = new ArrayList();
        } else {
            r0.mSwapTargets.clear();
            r0.mDistances.clear();
        }
        int boundingBoxMargin = r0.mCallback.getBoundingBoxMargin();
        int round = Math.round(r0.mSelectedStartX + r0.mDx) - boundingBoxMargin;
        int round2 = Math.round(r0.mSelectedStartY + r0.mDy) - boundingBoxMargin;
        boundingBoxMargin *= 2;
        int width = (viewHolder2.itemView.getWidth() + round) + boundingBoxMargin;
        int height = (viewHolder2.itemView.getHeight() + round2) + boundingBoxMargin;
        boundingBoxMargin = (round + width) / 2;
        int i = (round2 + height) / 2;
        LayoutManager layoutManager = r0.mRecyclerView.getLayoutManager();
        int childCount = layoutManager.getChildCount();
        int i2 = 0;
        while (i2 < childCount) {
            View childAt = layoutManager.getChildAt(i2);
            if (childAt != viewHolder2.itemView) {
                if (childAt.getBottom() >= round2 && childAt.getTop() <= height && childAt.getRight() >= round) {
                    if (childAt.getLeft() <= width) {
                        ViewHolder childViewHolder = r0.mRecyclerView.getChildViewHolder(childAt);
                        if (r0.mCallback.canDropOver(r0.mRecyclerView, r0.mSelected, childViewHolder)) {
                            int abs = Math.abs(boundingBoxMargin - ((childAt.getLeft() + childAt.getRight()) / 2));
                            int abs2 = Math.abs(i - ((childAt.getTop() + childAt.getBottom()) / 2));
                            abs = (abs * abs) + (abs2 * abs2);
                            abs2 = r0.mSwapTargets.size();
                            int i3 = 0;
                            int i4 = 0;
                            while (i4 < abs2 && abs > ((Integer) r0.mDistances.get(i4)).intValue()) {
                                i3++;
                                i4++;
                                viewHolder2 = viewHolder;
                            }
                            r0.mSwapTargets.add(i3, childViewHolder);
                            r0.mDistances.add(i3, Integer.valueOf(abs));
                        }
                    }
                }
            }
            i2++;
            viewHolder2 = viewHolder;
        }
        return r0.mSwapTargets;
    }

    void moveIfNecessary(ViewHolder viewHolder) {
        if (!this.mRecyclerView.isLayoutRequested() && this.mActionState == 2) {
            float moveThreshold = this.mCallback.getMoveThreshold(viewHolder);
            int i = (int) (this.mSelectedStartX + this.mDx);
            int i2 = (int) (this.mSelectedStartY + this.mDy);
            if (((float) Math.abs(i2 - viewHolder.itemView.getTop())) >= ((float) viewHolder.itemView.getHeight()) * moveThreshold || ((float) Math.abs(i - viewHolder.itemView.getLeft())) >= ((float) viewHolder.itemView.getWidth()) * moveThreshold) {
                List findSwapTargets = findSwapTargets(viewHolder);
                if (findSwapTargets.size() != 0) {
                    ViewHolder chooseDropTarget = this.mCallback.chooseDropTarget(viewHolder, findSwapTargets, i, i2);
                    if (chooseDropTarget == null) {
                        this.mSwapTargets.clear();
                        this.mDistances.clear();
                        return;
                    }
                    int adapterPosition = chooseDropTarget.getAdapterPosition();
                    int adapterPosition2 = viewHolder.getAdapterPosition();
                    if (this.mCallback.onMove(this.mRecyclerView, viewHolder, chooseDropTarget)) {
                        this.mCallback.onMoved(this.mRecyclerView, viewHolder, adapterPosition2, chooseDropTarget, adapterPosition, i, i2);
                    }
                }
            }
        }
    }

    public void onChildViewDetachedFromWindow(View view) {
        removeChildDrawingOrderCallbackIfNecessary(view);
        view = this.mRecyclerView.getChildViewHolder(view);
        if (view != null) {
            if (this.mSelected == null || view != this.mSelected) {
                endRecoverAnimation(view, false);
                if (this.mPendingCleanup.remove(view.itemView)) {
                    this.mCallback.clearView(this.mRecyclerView, view);
                }
            } else {
                select(null, 0);
            }
        }
    }

    int endRecoverAnimation(ViewHolder viewHolder, boolean z) {
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            RecoverAnimation recoverAnimation = (RecoverAnimation) this.mRecoverAnimations.get(size);
            if (recoverAnimation.mViewHolder == viewHolder) {
                recoverAnimation.mOverridden |= z;
                if (recoverAnimation.mEnded == null) {
                    recoverAnimation.cancel();
                }
                this.mRecoverAnimations.remove(size);
                return recoverAnimation.mAnimationType;
            }
        }
        return null;
    }

    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
        rect.setEmpty();
    }

    void obtainVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
        }
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    private void releaseVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private ViewHolder findSwipedView(MotionEvent motionEvent) {
        LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        if (this.mActivePointerId == -1) {
            return null;
        }
        int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
        float x = motionEvent.getX(findPointerIndex) - this.mInitialTouchX;
        float y = motionEvent.getY(findPointerIndex) - this.mInitialTouchY;
        x = Math.abs(x);
        y = Math.abs(y);
        if (x < ((float) this.mSlop) && y < ((float) this.mSlop)) {
            return null;
        }
        if (x > y && layoutManager.canScrollHorizontally()) {
            return null;
        }
        if (y > x && layoutManager.canScrollVertically()) {
            return null;
        }
        motionEvent = findChildView(motionEvent);
        if (motionEvent == null) {
            return null;
        }
        return this.mRecyclerView.getChildViewHolder(motionEvent);
    }

    boolean checkSelectForSwipe(int i, MotionEvent motionEvent, int i2) {
        if (this.mSelected == null && i == 2 && this.mActionState != 2) {
            if (this.mCallback.isItemViewSwipeEnabled() != 0) {
                if (this.mRecyclerView.getScrollState() == 1) {
                    return false;
                }
                i = findSwipedView(motionEvent);
                if (i == 0) {
                    return false;
                }
                int absoluteMovementFlags = (this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, i) & ACTION_MODE_SWIPE_MASK) >> 8;
                if (absoluteMovementFlags == 0) {
                    return false;
                }
                float x = motionEvent.getX(i2);
                x -= this.mInitialTouchX;
                i2 = motionEvent.getY(i2) - this.mInitialTouchY;
                float abs = Math.abs(x);
                float abs2 = Math.abs(i2);
                if (abs < ((float) this.mSlop) && abs2 < ((float) this.mSlop)) {
                    return false;
                }
                if (abs > abs2) {
                    if (x < 0.0f && (absoluteMovementFlags & 4) == 0) {
                        return false;
                    }
                    if (x > 0.0f && (absoluteMovementFlags & 8) == 0) {
                        return false;
                    }
                } else if (i2 < 0 && (absoluteMovementFlags & 1) == 0) {
                    return false;
                } else {
                    if (i2 > 0 && (absoluteMovementFlags & 2) == 0) {
                        return false;
                    }
                }
                this.mDy = 0.0f;
                this.mDx = 0.0f;
                this.mActivePointerId = motionEvent.getPointerId(0);
                select(i, 1);
                return true;
            }
        }
        return false;
    }

    View findChildView(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        motionEvent = motionEvent.getY();
        if (this.mSelected != null) {
            View view = this.mSelected.itemView;
            if (hitTest(view, x, motionEvent, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy)) {
                return view;
            }
        }
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            RecoverAnimation recoverAnimation = (RecoverAnimation) this.mRecoverAnimations.get(size);
            View view2 = recoverAnimation.mViewHolder.itemView;
            if (hitTest(view2, x, motionEvent, recoverAnimation.mX, recoverAnimation.mY)) {
                return view2;
            }
        }
        return this.mRecyclerView.findChildViewUnder(x, motionEvent);
    }

    public void startDrag(ViewHolder viewHolder) {
        if (!this.mCallback.hasDragFlag(this.mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start drag has been called but dragging is not enabled");
        } else if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e(TAG, "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this ItemTouchHelper.");
        } else {
            obtainVelocityTracker();
            this.mDy = 0.0f;
            this.mDx = 0.0f;
            select(viewHolder, 2);
        }
    }

    public void startSwipe(ViewHolder viewHolder) {
        if (!this.mCallback.hasSwipeFlag(this.mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start swipe has been called but swiping is not enabled");
        } else if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e(TAG, "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this ItemTouchHelper.");
        } else {
            obtainVelocityTracker();
            this.mDy = 0.0f;
            this.mDx = 0.0f;
            select(viewHolder, 1);
        }
    }

    RecoverAnimation findAnimation(MotionEvent motionEvent) {
        if (this.mRecoverAnimations.isEmpty()) {
            return null;
        }
        View findChildView = findChildView(motionEvent);
        for (int size = this.mRecoverAnimations.size() - 1; size >= 0; size--) {
            RecoverAnimation recoverAnimation = (RecoverAnimation) this.mRecoverAnimations.get(size);
            if (recoverAnimation.mViewHolder.itemView == findChildView) {
                return recoverAnimation;
            }
        }
        return null;
    }

    void updateDxDy(MotionEvent motionEvent, int i, int i2) {
        float x = motionEvent.getX(i2);
        motionEvent = motionEvent.getY(i2);
        this.mDx = x - this.mInitialTouchX;
        this.mDy = motionEvent - this.mInitialTouchY;
        if ((i & 4) == null) {
            this.mDx = Math.max(0.0f, this.mDx);
        }
        if ((i & 8) == null) {
            this.mDx = Math.min(0.0f, this.mDx);
        }
        if ((i & 1) == null) {
            this.mDy = Math.max(0.0f, this.mDy);
        }
        if ((i & 2) == null) {
            this.mDy = Math.min(0.0f, this.mDy);
        }
    }

    private int swipeIfNecessary(ViewHolder viewHolder) {
        if (this.mActionState == 2) {
            return 0;
        }
        int movementFlags = this.mCallback.getMovementFlags(this.mRecyclerView, viewHolder);
        int convertToAbsoluteDirection = (this.mCallback.convertToAbsoluteDirection(movementFlags, ViewCompat.getLayoutDirection(this.mRecyclerView)) & ACTION_MODE_SWIPE_MASK) >> 8;
        if (convertToAbsoluteDirection == 0) {
            return 0;
        }
        movementFlags = (movementFlags & ACTION_MODE_SWIPE_MASK) >> 8;
        int checkHorizontalSwipe;
        if (Math.abs(this.mDx) > Math.abs(this.mDy)) {
            checkHorizontalSwipe = checkHorizontalSwipe(viewHolder, convertToAbsoluteDirection);
            if (checkHorizontalSwipe > 0) {
                return (movementFlags & checkHorizontalSwipe) == null ? Callback.convertToRelativeDirection(checkHorizontalSwipe, ViewCompat.getLayoutDirection(this.mRecyclerView)) : checkHorizontalSwipe;
            } else {
                viewHolder = checkVerticalSwipe(viewHolder, convertToAbsoluteDirection);
                if (viewHolder > null) {
                    return viewHolder;
                }
            }
        }
        checkHorizontalSwipe = checkVerticalSwipe(viewHolder, convertToAbsoluteDirection);
        if (checkHorizontalSwipe > 0) {
            return checkHorizontalSwipe;
        }
        viewHolder = checkHorizontalSwipe(viewHolder, convertToAbsoluteDirection);
        if (viewHolder > null) {
            return (movementFlags & viewHolder) == 0 ? Callback.convertToRelativeDirection(viewHolder, ViewCompat.getLayoutDirection(this.mRecyclerView)) : viewHolder;
        }
        return 0;
    }

    private int checkHorizontalSwipe(ViewHolder viewHolder, int i) {
        if ((i & 12) != 0) {
            float abs;
            int i2 = 4;
            int i3 = this.mDx > 0.0f ? 8 : 4;
            if (this.mVelocityTracker != null && this.mActivePointerId > -1) {
                this.mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
                float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                if (xVelocity > 0.0f) {
                    i2 = 8;
                }
                abs = Math.abs(xVelocity);
                if ((i2 & i) != 0 && i3 == i2 && abs >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && abs > Math.abs(yVelocity)) {
                    return i2;
                }
            }
            abs = ((float) this.mRecyclerView.getWidth()) * this.mCallback.getSwipeThreshold(viewHolder);
            if ((i & i3) != null && Math.abs(this.mDx) > abs) {
                return i3;
            }
        }
        return null;
    }

    private int checkVerticalSwipe(ViewHolder viewHolder, int i) {
        if ((i & 3) != 0) {
            float abs;
            int i2 = 1;
            int i3 = this.mDy > 0.0f ? 2 : 1;
            if (this.mVelocityTracker != null && this.mActivePointerId > -1) {
                this.mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
                float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                if (yVelocity > 0.0f) {
                    i2 = 2;
                }
                abs = Math.abs(yVelocity);
                if ((i2 & i) != 0 && i2 == i3 && abs >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && abs > Math.abs(xVelocity)) {
                    return i2;
                }
            }
            abs = ((float) this.mRecyclerView.getHeight()) * this.mCallback.getSwipeThreshold(viewHolder);
            if ((i & i3) != null && Math.abs(this.mDy) > abs) {
                return i3;
            }
        }
        return null;
    }

    private void addChildDrawingOrderCallback() {
        if (VERSION.SDK_INT < 21) {
            if (this.mChildDrawingOrderCallback == null) {
                this.mChildDrawingOrderCallback = new C18775();
            }
            this.mRecyclerView.setChildDrawingOrderCallback(this.mChildDrawingOrderCallback);
        }
    }

    void removeChildDrawingOrderCallbackIfNecessary(View view) {
        if (view == this.mOverdrawChild) {
            this.mOverdrawChild = null;
            if (this.mChildDrawingOrderCallback != null) {
                this.mRecyclerView.setChildDrawingOrderCallback(null);
            }
        }
    }
}
