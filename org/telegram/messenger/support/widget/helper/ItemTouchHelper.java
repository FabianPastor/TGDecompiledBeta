package org.telegram.messenger.support.widget.helper;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.animation.AnimatorListenerCompat;
import android.support.v4.animation.AnimatorUpdateListenerCompat;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
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

public class ItemTouchHelper
  extends RecyclerView.ItemDecoration
  implements RecyclerView.OnChildAttachStateChangeListener
{
  private static final int ACTION_MODE_DRAG_MASK = 16711680;
  private static final int ACTION_MODE_IDLE_MASK = 255;
  private static final int ACTION_MODE_SWIPE_MASK = 65280;
  public static final int ACTION_STATE_DRAG = 2;
  public static final int ACTION_STATE_IDLE = 0;
  public static final int ACTION_STATE_SWIPE = 1;
  private static final int ACTIVE_POINTER_ID_NONE = -1;
  public static final int ANIMATION_TYPE_DRAG = 8;
  public static final int ANIMATION_TYPE_SWIPE_CANCEL = 4;
  public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 2;
  private static final boolean DEBUG = false;
  private static final int DIRECTION_FLAG_COUNT = 8;
  public static final int DOWN = 2;
  public static final int END = 32;
  public static final int LEFT = 4;
  private static final int PIXELS_PER_SECOND = 1000;
  public static final int RIGHT = 8;
  public static final int START = 16;
  private static final String TAG = "ItemTouchHelper";
  public static final int UP = 1;
  int mActionState = 0;
  int mActivePointerId = -1;
  Callback mCallback;
  private RecyclerView.ChildDrawingOrderCallback mChildDrawingOrderCallback = null;
  private List<Integer> mDistances;
  private long mDragScrollStartTimeInMs;
  float mDx;
  float mDy;
  private GestureDetectorCompat mGestureDetector;
  float mInitialTouchX;
  float mInitialTouchY;
  float mMaxSwipeVelocity;
  private final RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.OnItemTouchListener()
  {
    public boolean onInterceptTouchEvent(RecyclerView paramAnonymousRecyclerView, MotionEvent paramAnonymousMotionEvent)
    {
      ItemTouchHelper.this.mGestureDetector.onTouchEvent(paramAnonymousMotionEvent);
      int i = MotionEventCompat.getActionMasked(paramAnonymousMotionEvent);
      if (i == 0)
      {
        ItemTouchHelper.this.mActivePointerId = MotionEventCompat.getPointerId(paramAnonymousMotionEvent, 0);
        ItemTouchHelper.this.mInitialTouchX = paramAnonymousMotionEvent.getX();
        ItemTouchHelper.this.mInitialTouchY = paramAnonymousMotionEvent.getY();
        ItemTouchHelper.this.obtainVelocityTracker();
        if (ItemTouchHelper.this.mSelected == null)
        {
          paramAnonymousRecyclerView = ItemTouchHelper.this.findAnimation(paramAnonymousMotionEvent);
          if (paramAnonymousRecyclerView != null)
          {
            ItemTouchHelper localItemTouchHelper = ItemTouchHelper.this;
            localItemTouchHelper.mInitialTouchX -= paramAnonymousRecyclerView.mX;
            localItemTouchHelper = ItemTouchHelper.this;
            localItemTouchHelper.mInitialTouchY -= paramAnonymousRecyclerView.mY;
            ItemTouchHelper.this.endRecoverAnimation(paramAnonymousRecyclerView.mViewHolder, true);
            if (ItemTouchHelper.this.mPendingCleanup.remove(paramAnonymousRecyclerView.mViewHolder.itemView)) {
              ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, paramAnonymousRecyclerView.mViewHolder);
            }
            ItemTouchHelper.this.select(paramAnonymousRecyclerView.mViewHolder, paramAnonymousRecyclerView.mActionState);
            ItemTouchHelper.this.updateDxDy(paramAnonymousMotionEvent, ItemTouchHelper.this.mSelectedFlags, 0);
          }
        }
      }
      for (;;)
      {
        if (ItemTouchHelper.this.mVelocityTracker != null) {
          ItemTouchHelper.this.mVelocityTracker.addMovement(paramAnonymousMotionEvent);
        }
        if (ItemTouchHelper.this.mSelected == null) {
          break;
        }
        return true;
        if ((i == 3) || (i == 1))
        {
          ItemTouchHelper.this.mActivePointerId = -1;
          ItemTouchHelper.this.select(null, 0);
        }
        else if (ItemTouchHelper.this.mActivePointerId != -1)
        {
          int j = MotionEventCompat.findPointerIndex(paramAnonymousMotionEvent, ItemTouchHelper.this.mActivePointerId);
          if (j >= 0) {
            ItemTouchHelper.this.checkSelectForSwipe(i, paramAnonymousMotionEvent, j);
          }
        }
      }
      return false;
    }
    
    public void onRequestDisallowInterceptTouchEvent(boolean paramAnonymousBoolean)
    {
      if (!paramAnonymousBoolean) {
        return;
      }
      ItemTouchHelper.this.select(null, 0);
    }
    
    public void onTouchEvent(RecyclerView paramAnonymousRecyclerView, MotionEvent paramAnonymousMotionEvent)
    {
      int i = 0;
      ItemTouchHelper.this.mGestureDetector.onTouchEvent(paramAnonymousMotionEvent);
      if (ItemTouchHelper.this.mVelocityTracker != null) {
        ItemTouchHelper.this.mVelocityTracker.addMovement(paramAnonymousMotionEvent);
      }
      if (ItemTouchHelper.this.mActivePointerId == -1) {}
      int j;
      do
      {
        int k;
        do
        {
          return;
          j = MotionEventCompat.getActionMasked(paramAnonymousMotionEvent);
          k = MotionEventCompat.findPointerIndex(paramAnonymousMotionEvent, ItemTouchHelper.this.mActivePointerId);
          if (k >= 0) {
            ItemTouchHelper.this.checkSelectForSwipe(j, paramAnonymousMotionEvent, k);
          }
          paramAnonymousRecyclerView = ItemTouchHelper.this.mSelected;
        } while (paramAnonymousRecyclerView == null);
        switch (j)
        {
        case 4: 
        case 5: 
        default: 
          return;
        case 1: 
        case 2: 
        case 3: 
          for (;;)
          {
            ItemTouchHelper.this.select(null, 0);
            ItemTouchHelper.this.mActivePointerId = -1;
            return;
            if (k < 0) {
              break;
            }
            ItemTouchHelper.this.updateDxDy(paramAnonymousMotionEvent, ItemTouchHelper.this.mSelectedFlags, k);
            ItemTouchHelper.this.moveIfNecessary(paramAnonymousRecyclerView);
            ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
            ItemTouchHelper.this.mScrollRunnable.run();
            ItemTouchHelper.this.mRecyclerView.invalidate();
            return;
            if (ItemTouchHelper.this.mVelocityTracker != null) {
              ItemTouchHelper.this.mVelocityTracker.clear();
            }
          }
        }
        j = MotionEventCompat.getActionIndex(paramAnonymousMotionEvent);
      } while (MotionEventCompat.getPointerId(paramAnonymousMotionEvent, j) != ItemTouchHelper.this.mActivePointerId);
      if (j == 0) {
        i = 1;
      }
      ItemTouchHelper.this.mActivePointerId = MotionEventCompat.getPointerId(paramAnonymousMotionEvent, i);
      ItemTouchHelper.this.updateDxDy(paramAnonymousMotionEvent, ItemTouchHelper.this.mSelectedFlags, j);
    }
  };
  private View mOverdrawChild = null;
  private int mOverdrawChildPosition = -1;
  final List<View> mPendingCleanup = new ArrayList();
  List<RecoverAnimation> mRecoverAnimations = new ArrayList();
  private RecyclerView mRecyclerView;
  private final Runnable mScrollRunnable = new Runnable()
  {
    public void run()
    {
      if ((ItemTouchHelper.this.mSelected != null) && (ItemTouchHelper.this.scrollIfNecessary()))
      {
        if (ItemTouchHelper.this.mSelected != null) {
          ItemTouchHelper.this.moveIfNecessary(ItemTouchHelper.this.mSelected);
        }
        ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
        ViewCompat.postOnAnimation(ItemTouchHelper.this.mRecyclerView, this);
      }
    }
  };
  RecyclerView.ViewHolder mSelected = null;
  int mSelectedFlags;
  float mSelectedStartX;
  float mSelectedStartY;
  private int mSlop;
  private List<RecyclerView.ViewHolder> mSwapTargets;
  float mSwipeEscapeVelocity;
  private final float[] mTmpPosition = new float[2];
  private Rect mTmpRect;
  private VelocityTracker mVelocityTracker;
  
  public ItemTouchHelper(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  private void addChildDrawingOrderCallback()
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return;
    }
    if (this.mChildDrawingOrderCallback == null) {
      this.mChildDrawingOrderCallback = new RecyclerView.ChildDrawingOrderCallback()
      {
        public int onGetChildDrawingOrder(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if (ItemTouchHelper.this.mOverdrawChild == null) {}
          int i;
          do
          {
            return paramAnonymousInt2;
            int j = ItemTouchHelper.this.mOverdrawChildPosition;
            i = j;
            if (j == -1)
            {
              i = ItemTouchHelper.this.mRecyclerView.indexOfChild(ItemTouchHelper.this.mOverdrawChild);
              ItemTouchHelper.access$2302(ItemTouchHelper.this, i);
            }
            if (paramAnonymousInt2 == paramAnonymousInt1 - 1) {
              return i;
            }
          } while (paramAnonymousInt2 < i);
          return paramAnonymousInt2 + 1;
        }
      };
    }
    this.mRecyclerView.setChildDrawingOrderCallback(this.mChildDrawingOrderCallback);
  }
  
  private int checkHorizontalSwipe(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if ((paramInt & 0xC) != 0)
    {
      int i;
      if (this.mDx > 0.0F)
      {
        i = 8;
        if ((this.mVelocityTracker == null) || (this.mActivePointerId <= -1)) {
          break label155;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
        f2 = VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId);
        f1 = VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId);
        if (f2 <= 0.0F) {
          break label149;
        }
      }
      label149:
      for (int j = 8;; j = 4)
      {
        f2 = Math.abs(f2);
        if (((j & paramInt) == 0) || (i != j) || (f2 < this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity)) || (f2 <= Math.abs(f1))) {
          break label155;
        }
        return j;
        i = 4;
        break;
      }
      label155:
      float f1 = this.mRecyclerView.getWidth();
      float f2 = this.mCallback.getSwipeThreshold(paramViewHolder);
      if (((paramInt & i) != 0) && (Math.abs(this.mDx) > f1 * f2)) {
        return i;
      }
    }
    return 0;
  }
  
  private boolean checkSelectForSwipe(int paramInt1, MotionEvent paramMotionEvent, int paramInt2)
  {
    if ((this.mSelected != null) || (paramInt1 != 2) || (this.mActionState == 2) || (!this.mCallback.isItemViewSwipeEnabled())) {
      return false;
    }
    if (this.mRecyclerView.getScrollState() == 1) {
      return false;
    }
    RecyclerView.ViewHolder localViewHolder = findSwipedView(paramMotionEvent);
    if (localViewHolder == null) {
      return false;
    }
    paramInt1 = (0xFF00 & this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, localViewHolder)) >> 8;
    if (paramInt1 == 0) {
      return false;
    }
    float f1 = MotionEventCompat.getX(paramMotionEvent, paramInt2);
    float f2 = MotionEventCompat.getY(paramMotionEvent, paramInt2);
    f1 -= this.mInitialTouchX;
    f2 -= this.mInitialTouchY;
    float f3 = Math.abs(f1);
    float f4 = Math.abs(f2);
    if ((f3 < this.mSlop) && (f4 < this.mSlop)) {
      return false;
    }
    if (f3 > f4)
    {
      if ((f1 < 0.0F) && ((paramInt1 & 0x4) == 0)) {
        return false;
      }
      if ((f1 > 0.0F) && ((paramInt1 & 0x8) == 0)) {
        return false;
      }
    }
    else
    {
      if ((f2 < 0.0F) && ((paramInt1 & 0x1) == 0)) {
        return false;
      }
      if ((f2 > 0.0F) && ((paramInt1 & 0x2) == 0)) {
        return false;
      }
    }
    this.mDy = 0.0F;
    this.mDx = 0.0F;
    this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
    select(localViewHolder, 1);
    return true;
  }
  
  private int checkVerticalSwipe(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if ((paramInt & 0x3) != 0)
    {
      int i;
      if (this.mDy > 0.0F)
      {
        i = 2;
        if ((this.mVelocityTracker == null) || (this.mActivePointerId <= -1)) {
          break label152;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
        f1 = VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId);
        f2 = VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId);
        if (f2 <= 0.0F) {
          break label146;
        }
      }
      label146:
      for (int j = 2;; j = 1)
      {
        f2 = Math.abs(f2);
        if (((j & paramInt) == 0) || (j != i) || (f2 < this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity)) || (f2 <= Math.abs(f1))) {
          break label152;
        }
        return j;
        i = 1;
        break;
      }
      label152:
      float f1 = this.mRecyclerView.getHeight();
      float f2 = this.mCallback.getSwipeThreshold(paramViewHolder);
      if (((paramInt & i) != 0) && (Math.abs(this.mDy) > f1 * f2)) {
        return i;
      }
    }
    return 0;
  }
  
  private void destroyCallbacks()
  {
    this.mRecyclerView.removeItemDecoration(this);
    this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
    this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
    int i = this.mRecoverAnimations.size() - 1;
    while (i >= 0)
    {
      RecoverAnimation localRecoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(0);
      this.mCallback.clearView(this.mRecyclerView, localRecoverAnimation.mViewHolder);
      i -= 1;
    }
    this.mRecoverAnimations.clear();
    this.mOverdrawChild = null;
    this.mOverdrawChildPosition = -1;
    releaseVelocityTracker();
  }
  
  private int endRecoverAnimation(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    int i = this.mRecoverAnimations.size() - 1;
    while (i >= 0)
    {
      RecoverAnimation localRecoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(i);
      if (localRecoverAnimation.mViewHolder == paramViewHolder)
      {
        localRecoverAnimation.mOverridden |= paramBoolean;
        if (!localRecoverAnimation.mEnded) {
          localRecoverAnimation.cancel();
        }
        this.mRecoverAnimations.remove(i);
        return localRecoverAnimation.mAnimationType;
      }
      i -= 1;
    }
    return 0;
  }
  
  private RecoverAnimation findAnimation(MotionEvent paramMotionEvent)
  {
    if (this.mRecoverAnimations.isEmpty())
    {
      paramMotionEvent = null;
      return paramMotionEvent;
    }
    View localView = findChildView(paramMotionEvent);
    int i = this.mRecoverAnimations.size() - 1;
    for (;;)
    {
      if (i < 0) {
        break label74;
      }
      RecoverAnimation localRecoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(i);
      paramMotionEvent = localRecoverAnimation;
      if (localRecoverAnimation.mViewHolder.itemView == localView) {
        break;
      }
      i -= 1;
    }
    label74:
    return null;
  }
  
  private View findChildView(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    if (this.mSelected != null)
    {
      paramMotionEvent = this.mSelected.itemView;
      if (hitTest(paramMotionEvent, f1, f2, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy)) {
        return paramMotionEvent;
      }
    }
    int i = this.mRecoverAnimations.size() - 1;
    while (i >= 0)
    {
      paramMotionEvent = (RecoverAnimation)this.mRecoverAnimations.get(i);
      View localView = paramMotionEvent.mViewHolder.itemView;
      if (hitTest(localView, f1, f2, paramMotionEvent.mX, paramMotionEvent.mY)) {
        return localView;
      }
      i -= 1;
    }
    return this.mRecyclerView.findChildViewUnder(f1, f2);
  }
  
  private List<RecyclerView.ViewHolder> findSwapTargets(RecyclerView.ViewHolder paramViewHolder)
  {
    int i;
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    label137:
    View localView;
    if (this.mSwapTargets == null)
    {
      this.mSwapTargets = new ArrayList();
      this.mDistances = new ArrayList();
      i = this.mCallback.getBoundingBoxMargin();
      m = Math.round(this.mSelectedStartX + this.mDx) - i;
      n = Math.round(this.mSelectedStartY + this.mDy) - i;
      i1 = paramViewHolder.itemView.getWidth() + m + i * 2;
      i2 = paramViewHolder.itemView.getHeight() + n + i * 2;
      i3 = (m + i1) / 2;
      i4 = (n + i2) / 2;
      RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
      int i5 = localLayoutManager.getChildCount();
      i = 0;
      if (i >= i5) {
        break label399;
      }
      localView = localLayoutManager.getChildAt(i);
      if (localView != paramViewHolder.itemView) {
        break label188;
      }
    }
    for (;;)
    {
      i += 1;
      break label137;
      this.mSwapTargets.clear();
      this.mDistances.clear();
      break;
      label188:
      if ((localView.getBottom() >= n) && (localView.getTop() <= i2) && (localView.getRight() >= m) && (localView.getLeft() <= i1))
      {
        RecyclerView.ViewHolder localViewHolder = this.mRecyclerView.getChildViewHolder(localView);
        if (this.mCallback.canDropOver(this.mRecyclerView, this.mSelected, localViewHolder))
        {
          int j = Math.abs(i3 - (localView.getLeft() + localView.getRight()) / 2);
          int k = Math.abs(i4 - (localView.getTop() + localView.getBottom()) / 2);
          int i6 = j * j + k * k;
          k = 0;
          int i7 = this.mSwapTargets.size();
          j = 0;
          while ((j < i7) && (i6 > ((Integer)this.mDistances.get(j)).intValue()))
          {
            k += 1;
            j += 1;
          }
          this.mSwapTargets.add(k, localViewHolder);
          this.mDistances.add(k, Integer.valueOf(i6));
        }
      }
    }
    label399:
    return this.mSwapTargets;
  }
  
  private RecyclerView.ViewHolder findSwipedView(MotionEvent paramMotionEvent)
  {
    RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
    if (this.mActivePointerId == -1) {}
    do
    {
      float f3;
      float f1;
      do
      {
        return null;
        int i = MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId);
        f3 = MotionEventCompat.getX(paramMotionEvent, i);
        float f4 = this.mInitialTouchX;
        f1 = MotionEventCompat.getY(paramMotionEvent, i);
        float f2 = this.mInitialTouchY;
        f3 = Math.abs(f3 - f4);
        f1 = Math.abs(f1 - f2);
      } while (((f3 < this.mSlop) && (f1 < this.mSlop)) || ((f3 > f1) && (localLayoutManager.canScrollHorizontally())) || ((f1 > f3) && (localLayoutManager.canScrollVertically())));
      paramMotionEvent = findChildView(paramMotionEvent);
    } while (paramMotionEvent == null);
    return this.mRecyclerView.getChildViewHolder(paramMotionEvent);
  }
  
  private void getSelectedDxDy(float[] paramArrayOfFloat)
  {
    if ((this.mSelectedFlags & 0xC) != 0) {
      paramArrayOfFloat[0] = (this.mSelectedStartX + this.mDx - this.mSelected.itemView.getLeft());
    }
    while ((this.mSelectedFlags & 0x3) != 0)
    {
      paramArrayOfFloat[1] = (this.mSelectedStartY + this.mDy - this.mSelected.itemView.getTop());
      return;
      paramArrayOfFloat[0] = ViewCompat.getTranslationX(this.mSelected.itemView);
    }
    paramArrayOfFloat[1] = ViewCompat.getTranslationY(this.mSelected.itemView);
  }
  
  private boolean hasRunningRecoverAnim()
  {
    int j = this.mRecoverAnimations.size();
    int i = 0;
    while (i < j)
    {
      if (!((RecoverAnimation)this.mRecoverAnimations.get(i)).mEnded) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static boolean hitTest(View paramView, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return (paramFloat1 >= paramFloat3) && (paramFloat1 <= paramView.getWidth() + paramFloat3) && (paramFloat2 >= paramFloat4) && (paramFloat2 <= paramView.getHeight() + paramFloat4);
  }
  
  private void initGestureDetector()
  {
    if (this.mGestureDetector != null) {
      return;
    }
    this.mGestureDetector = new GestureDetectorCompat(this.mRecyclerView.getContext(), new ItemTouchHelperGestureListener(null));
  }
  
  private void moveIfNecessary(RecyclerView.ViewHolder paramViewHolder)
  {
    if (this.mRecyclerView.isLayoutRequested()) {}
    label10:
    int i;
    int j;
    Object localObject;
    int k;
    int m;
    do
    {
      do
      {
        float f;
        do
        {
          break label10;
          do
          {
            return;
          } while (this.mActionState != 2);
          f = this.mCallback.getMoveThreshold(paramViewHolder);
          i = (int)(this.mSelectedStartX + this.mDx);
          j = (int)(this.mSelectedStartY + this.mDy);
        } while ((Math.abs(j - paramViewHolder.itemView.getTop()) < paramViewHolder.itemView.getHeight() * f) && (Math.abs(i - paramViewHolder.itemView.getLeft()) < paramViewHolder.itemView.getWidth() * f));
        localObject = findSwapTargets(paramViewHolder);
      } while (((List)localObject).size() == 0);
      localObject = this.mCallback.chooseDropTarget(paramViewHolder, (List)localObject, i, j);
      if (localObject == null)
      {
        this.mSwapTargets.clear();
        this.mDistances.clear();
        return;
      }
      k = ((RecyclerView.ViewHolder)localObject).getAdapterPosition();
      m = paramViewHolder.getAdapterPosition();
    } while (!this.mCallback.onMove(this.mRecyclerView, paramViewHolder, (RecyclerView.ViewHolder)localObject));
    this.mCallback.onMoved(this.mRecyclerView, paramViewHolder, m, (RecyclerView.ViewHolder)localObject, k, i, j);
  }
  
  private void obtainVelocityTracker()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
    }
    this.mVelocityTracker = VelocityTracker.obtain();
  }
  
  private void postDispatchSwipe(final RecoverAnimation paramRecoverAnimation, final int paramInt)
  {
    this.mRecyclerView.post(new Runnable()
    {
      public void run()
      {
        if ((ItemTouchHelper.this.mRecyclerView != null) && (ItemTouchHelper.this.mRecyclerView.isAttachedToWindow()) && (!paramRecoverAnimation.mOverridden) && (paramRecoverAnimation.mViewHolder.getAdapterPosition() != -1))
        {
          RecyclerView.ItemAnimator localItemAnimator = ItemTouchHelper.this.mRecyclerView.getItemAnimator();
          if (((localItemAnimator == null) || (!localItemAnimator.isRunning(null))) && (!ItemTouchHelper.this.hasRunningRecoverAnim())) {
            ItemTouchHelper.this.mCallback.onSwiped(paramRecoverAnimation.mViewHolder, paramInt);
          }
        }
        else
        {
          return;
        }
        ItemTouchHelper.this.mRecyclerView.post(this);
      }
    });
  }
  
  private void releaseVelocityTracker()
  {
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  private void removeChildDrawingOrderCallbackIfNecessary(View paramView)
  {
    if (paramView == this.mOverdrawChild)
    {
      this.mOverdrawChild = null;
      if (this.mChildDrawingOrderCallback != null) {
        this.mRecyclerView.setChildDrawingOrderCallback(null);
      }
    }
  }
  
  private boolean scrollIfNecessary()
  {
    if (this.mSelected == null)
    {
      this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
      return false;
    }
    long l2 = System.currentTimeMillis();
    long l1;
    int j;
    int k;
    int i;
    int m;
    if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE)
    {
      l1 = 0L;
      RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
      if (this.mTmpRect == null) {
        this.mTmpRect = new Rect();
      }
      j = 0;
      k = 0;
      localLayoutManager.calculateItemDecorationsForChild(this.mSelected.itemView, this.mTmpRect);
      i = j;
      if (localLayoutManager.canScrollHorizontally())
      {
        m = (int)(this.mSelectedStartX + this.mDx);
        i = m - this.mTmpRect.left - this.mRecyclerView.getPaddingLeft();
        if ((this.mDx >= 0.0F) || (i >= 0)) {
          break label314;
        }
      }
      label136:
      j = k;
      if (localLayoutManager.canScrollVertically())
      {
        m = (int)(this.mSelectedStartY + this.mDy);
        j = m - this.mTmpRect.top - this.mRecyclerView.getPaddingTop();
        if ((this.mDy >= 0.0F) || (j >= 0)) {
          break label377;
        }
      }
    }
    for (;;)
    {
      k = i;
      if (i != 0) {
        k = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getWidth(), i, this.mRecyclerView.getWidth(), l1);
      }
      i = j;
      if (j != 0) {
        i = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getHeight(), j, this.mRecyclerView.getHeight(), l1);
      }
      if ((k == 0) && (i == 0)) {
        break label440;
      }
      if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
        this.mDragScrollStartTimeInMs = l2;
      }
      this.mRecyclerView.scrollBy(k, i);
      return true;
      l1 = l2 - this.mDragScrollStartTimeInMs;
      break;
      label314:
      i = j;
      if (this.mDx <= 0.0F) {
        break label136;
      }
      m = this.mSelected.itemView.getWidth() + m + this.mTmpRect.right - (this.mRecyclerView.getWidth() - this.mRecyclerView.getPaddingRight());
      i = j;
      if (m <= 0) {
        break label136;
      }
      i = m;
      break label136;
      label377:
      j = k;
      if (this.mDy > 0.0F)
      {
        m = this.mSelected.itemView.getHeight() + m + this.mTmpRect.bottom - (this.mRecyclerView.getHeight() - this.mRecyclerView.getPaddingBottom());
        j = k;
        if (m > 0) {
          j = m;
        }
      }
    }
    label440:
    this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
    return false;
  }
  
  private void select(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if ((paramViewHolder == this.mSelected) && (paramInt == this.mActionState)) {
      return;
    }
    this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
    int k = this.mActionState;
    endRecoverAnimation(paramViewHolder, true);
    this.mActionState = paramInt;
    if (paramInt == 2)
    {
      this.mOverdrawChild = paramViewHolder.itemView;
      addChildDrawingOrderCallback();
    }
    int i = 0;
    final int j = 0;
    final Object localObject;
    float f1;
    float f2;
    if (this.mSelected != null)
    {
      localObject = this.mSelected;
      if (((RecyclerView.ViewHolder)localObject).itemView.getParent() == null) {
        break label510;
      }
      if (k == 2)
      {
        j = 0;
        releaseVelocityTracker();
      }
    }
    else
    {
      switch (j)
      {
      default: 
        f1 = 0.0F;
        f2 = 0.0F;
        label169:
        if (k == 2)
        {
          i = 8;
          label179:
          getSelectedDxDy(this.mTmpPosition);
          float f3 = this.mTmpPosition[0];
          float f4 = this.mTmpPosition[1];
          localObject = new RecoverAnimation((RecyclerView.ViewHolder)localObject, i, k, f3, f4, f1, f2, j)
          {
            public void onAnimationEnd(ValueAnimatorCompat paramAnonymousValueAnimatorCompat)
            {
              super.onAnimationEnd(paramAnonymousValueAnimatorCompat);
              if (this.mOverridden) {}
              for (;;)
              {
                return;
                if (j <= 0) {
                  ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, localObject);
                }
                while (ItemTouchHelper.this.mOverdrawChild == localObject.itemView)
                {
                  ItemTouchHelper.this.removeChildDrawingOrderCallbackIfNecessary(localObject.itemView);
                  return;
                  ItemTouchHelper.this.mPendingCleanup.add(localObject.itemView);
                  this.mIsPendingCleanup = true;
                  if (j > 0) {
                    ItemTouchHelper.this.postDispatchSwipe(this, j);
                  }
                }
              }
            }
          };
          ((RecoverAnimation)localObject).setDuration(this.mCallback.getAnimationDuration(this.mRecyclerView, i, f1 - f3, f2 - f4));
          this.mRecoverAnimations.add(localObject);
          ((RecoverAnimation)localObject).start();
          i = 1;
          label277:
          this.mSelected = null;
          if (paramViewHolder != null)
          {
            this.mSelectedFlags = ((this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, paramViewHolder) & (1 << paramInt * 8 + 8) - 1) >> this.mActionState * 8);
            this.mSelectedStartX = paramViewHolder.itemView.getLeft();
            this.mSelectedStartY = paramViewHolder.itemView.getTop();
            this.mSelected = paramViewHolder;
            if (paramInt == 2) {
              this.mSelected.itemView.performHapticFeedback(0);
            }
          }
          paramViewHolder = this.mRecyclerView.getParent();
          if (paramViewHolder != null) {
            if (this.mSelected == null) {
              break label539;
            }
          }
        }
        break;
      }
    }
    label510:
    label539:
    for (boolean bool = true;; bool = false)
    {
      paramViewHolder.requestDisallowInterceptTouchEvent(bool);
      if (i == 0) {
        this.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
      }
      this.mCallback.onSelectedChanged(this.mSelected, this.mActionState);
      this.mRecyclerView.invalidate();
      return;
      j = swipeIfNecessary((RecyclerView.ViewHolder)localObject);
      break;
      f2 = 0.0F;
      f1 = Math.signum(this.mDx) * this.mRecyclerView.getWidth();
      break label169;
      f1 = 0.0F;
      f2 = Math.signum(this.mDy) * this.mRecyclerView.getHeight();
      break label169;
      if (j > 0)
      {
        i = 2;
        break label179;
      }
      i = 4;
      break label179;
      removeChildDrawingOrderCallbackIfNecessary(((RecyclerView.ViewHolder)localObject).itemView);
      this.mCallback.clearView(this.mRecyclerView, (RecyclerView.ViewHolder)localObject);
      i = j;
      break label277;
    }
  }
  
  private void setupCallbacks()
  {
    this.mSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
    this.mRecyclerView.addItemDecoration(this);
    this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
    this.mRecyclerView.addOnChildAttachStateChangeListener(this);
    initGestureDetector();
  }
  
  private int swipeIfNecessary(RecyclerView.ViewHolder paramViewHolder)
  {
    int i;
    if (this.mActionState == 2) {
      i = 0;
    }
    int m;
    int j;
    label131:
    do
    {
      int k;
      do
      {
        do
        {
          return i;
          i = this.mCallback.getMovementFlags(this.mRecyclerView, paramViewHolder);
          k = (this.mCallback.convertToAbsoluteDirection(i, ViewCompat.getLayoutDirection(this.mRecyclerView)) & 0xFF00) >> 8;
          if (k == 0) {
            return 0;
          }
          m = (i & 0xFF00) >> 8;
          if (Math.abs(this.mDx) <= Math.abs(this.mDy)) {
            break label131;
          }
          j = checkHorizontalSwipe(paramViewHolder, k);
          if (j <= 0) {
            break;
          }
          i = j;
        } while ((m & j) != 0);
        return Callback.convertToRelativeDirection(j, ViewCompat.getLayoutDirection(this.mRecyclerView));
        j = checkVerticalSwipe(paramViewHolder, k);
        i = j;
      } while (j > 0);
      do
      {
        return 0;
        j = checkVerticalSwipe(paramViewHolder, k);
        i = j;
        if (j > 0) {
          break;
        }
        j = checkHorizontalSwipe(paramViewHolder, k);
      } while (j <= 0);
      i = j;
    } while ((m & j) != 0);
    return Callback.convertToRelativeDirection(j, ViewCompat.getLayoutDirection(this.mRecyclerView));
  }
  
  private void updateDxDy(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
  {
    float f1 = MotionEventCompat.getX(paramMotionEvent, paramInt2);
    float f2 = MotionEventCompat.getY(paramMotionEvent, paramInt2);
    this.mDx = (f1 - this.mInitialTouchX);
    this.mDy = (f2 - this.mInitialTouchY);
    if ((paramInt1 & 0x4) == 0) {
      this.mDx = Math.max(0.0F, this.mDx);
    }
    if ((paramInt1 & 0x8) == 0) {
      this.mDx = Math.min(0.0F, this.mDx);
    }
    if ((paramInt1 & 0x1) == 0) {
      this.mDy = Math.max(0.0F, this.mDy);
    }
    if ((paramInt1 & 0x2) == 0) {
      this.mDy = Math.min(0.0F, this.mDy);
    }
  }
  
  public void attachToRecyclerView(@Nullable RecyclerView paramRecyclerView)
  {
    if (this.mRecyclerView == paramRecyclerView) {}
    do
    {
      return;
      if (this.mRecyclerView != null) {
        destroyCallbacks();
      }
      this.mRecyclerView = paramRecyclerView;
    } while (this.mRecyclerView == null);
    paramRecyclerView.getResources();
    this.mSwipeEscapeVelocity = AndroidUtilities.dp(120.0F);
    this.mMaxSwipeVelocity = AndroidUtilities.dp(800.0F);
    setupCallbacks();
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    paramRect.setEmpty();
  }
  
  public void onChildViewAttachedToWindow(View paramView) {}
  
  public void onChildViewDetachedFromWindow(View paramView)
  {
    removeChildDrawingOrderCallbackIfNecessary(paramView);
    paramView = this.mRecyclerView.getChildViewHolder(paramView);
    if (paramView == null) {}
    do
    {
      return;
      if ((this.mSelected != null) && (paramView == this.mSelected))
      {
        select(null, 0);
        return;
      }
      endRecoverAnimation(paramView, false);
    } while (!this.mPendingCleanup.remove(paramView.itemView));
    this.mCallback.clearView(this.mRecyclerView, paramView);
  }
  
  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    this.mOverdrawChildPosition = -1;
    float f1 = 0.0F;
    float f2 = 0.0F;
    if (this.mSelected != null)
    {
      getSelectedDxDy(this.mTmpPosition);
      f1 = this.mTmpPosition[0];
      f2 = this.mTmpPosition[1];
    }
    this.mCallback.onDraw(paramCanvas, paramRecyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f1, f2);
  }
  
  public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    float f1 = 0.0F;
    float f2 = 0.0F;
    if (this.mSelected != null)
    {
      getSelectedDxDy(this.mTmpPosition);
      f1 = this.mTmpPosition[0];
      f2 = this.mTmpPosition[1];
    }
    this.mCallback.onDrawOver(paramCanvas, paramRecyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f1, f2);
  }
  
  public void startDrag(RecyclerView.ViewHolder paramViewHolder)
  {
    if (!this.mCallback.hasDragFlag(this.mRecyclerView, paramViewHolder))
    {
      Log.e("ItemTouchHelper", "Start drag has been called but swiping is not enabled");
      return;
    }
    if (paramViewHolder.itemView.getParent() != this.mRecyclerView)
    {
      Log.e("ItemTouchHelper", "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this ItemTouchHelper.");
      return;
    }
    obtainVelocityTracker();
    this.mDy = 0.0F;
    this.mDx = 0.0F;
    select(paramViewHolder, 2);
  }
  
  public void startSwipe(RecyclerView.ViewHolder paramViewHolder)
  {
    if (!this.mCallback.hasSwipeFlag(this.mRecyclerView, paramViewHolder))
    {
      Log.e("ItemTouchHelper", "Start swipe has been called but dragging is not enabled");
      return;
    }
    if (paramViewHolder.itemView.getParent() != this.mRecyclerView)
    {
      Log.e("ItemTouchHelper", "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this ItemTouchHelper.");
      return;
    }
    obtainVelocityTracker();
    this.mDy = 0.0F;
    this.mDx = 0.0F;
    select(paramViewHolder, 1);
  }
  
  public static abstract class Callback
  {
    private static final int ABS_HORIZONTAL_DIR_FLAGS = 789516;
    public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;
    public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;
    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 500L;
    static final int RELATIVE_DIR_FLAGS = 3158064;
    private static final Interpolator sDragScrollInterpolator = new Interpolator()
    {
      public float getInterpolation(float paramAnonymousFloat)
      {
        return paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat;
      }
    };
    private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator()
    {
      public float getInterpolation(float paramAnonymousFloat)
      {
        paramAnonymousFloat -= 1.0F;
        return paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat + 1.0F;
      }
    };
    private static final ItemTouchUIUtil sUICallback = new ItemTouchUIUtilImpl.Gingerbread();
    private int mCachedMaxScrollSpeed = -1;
    
    static
    {
      if (Build.VERSION.SDK_INT >= 21)
      {
        sUICallback = new ItemTouchUIUtilImpl.Lollipop();
        return;
      }
      if (Build.VERSION.SDK_INT >= 11)
      {
        sUICallback = new ItemTouchUIUtilImpl.Honeycomb();
        return;
      }
    }
    
    public static int convertToRelativeDirection(int paramInt1, int paramInt2)
    {
      int i = paramInt1 & 0xC0C0C;
      if (i == 0) {
        return paramInt1;
      }
      paramInt1 &= (i ^ 0xFFFFFFFF);
      if (paramInt2 == 0) {
        return paramInt1 | i << 2;
      }
      return paramInt1 | i << 1 & 0xFFF3F3F3 | (i << 1 & 0xC0C0C) << 2;
    }
    
    public static ItemTouchUIUtil getDefaultUIUtil()
    {
      return sUICallback;
    }
    
    private int getMaxDragScroll(RecyclerView paramRecyclerView)
    {
      if (this.mCachedMaxScrollSpeed == -1) {
        this.mCachedMaxScrollSpeed = AndroidUtilities.dp(20.0F);
      }
      return this.mCachedMaxScrollSpeed;
    }
    
    private boolean hasDragFlag(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return (0xFF0000 & getAbsoluteMovementFlags(paramRecyclerView, paramViewHolder)) != 0;
    }
    
    private boolean hasSwipeFlag(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return (0xFF00 & getAbsoluteMovementFlags(paramRecyclerView, paramViewHolder)) != 0;
    }
    
    public static int makeFlag(int paramInt1, int paramInt2)
    {
      return paramInt2 << paramInt1 * 8;
    }
    
    public static int makeMovementFlags(int paramInt1, int paramInt2)
    {
      return makeFlag(0, paramInt2 | paramInt1) | makeFlag(1, paramInt2) | makeFlag(2, paramInt1);
    }
    
    private void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, List<ItemTouchHelper.RecoverAnimation> paramList, int paramInt, float paramFloat1, float paramFloat2)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        ItemTouchHelper.RecoverAnimation localRecoverAnimation = (ItemTouchHelper.RecoverAnimation)paramList.get(i);
        localRecoverAnimation.update();
        int k = paramCanvas.save();
        onChildDraw(paramCanvas, paramRecyclerView, localRecoverAnimation.mViewHolder, localRecoverAnimation.mX, localRecoverAnimation.mY, localRecoverAnimation.mActionState, false);
        paramCanvas.restoreToCount(k);
        i += 1;
      }
      if (paramViewHolder != null)
      {
        i = paramCanvas.save();
        onChildDraw(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, true);
        paramCanvas.restoreToCount(i);
      }
    }
    
    private void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, List<ItemTouchHelper.RecoverAnimation> paramList, int paramInt, float paramFloat1, float paramFloat2)
    {
      int j = paramList.size();
      int i = 0;
      while (i < j)
      {
        ItemTouchHelper.RecoverAnimation localRecoverAnimation = (ItemTouchHelper.RecoverAnimation)paramList.get(i);
        int k = paramCanvas.save();
        onChildDrawOver(paramCanvas, paramRecyclerView, localRecoverAnimation.mViewHolder, localRecoverAnimation.mX, localRecoverAnimation.mY, localRecoverAnimation.mActionState, false);
        paramCanvas.restoreToCount(k);
        i += 1;
      }
      if (paramViewHolder != null)
      {
        i = paramCanvas.save();
        onChildDrawOver(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, true);
        paramCanvas.restoreToCount(i);
      }
      i = 0;
      paramInt = j - 1;
      if (paramInt >= 0)
      {
        paramCanvas = (ItemTouchHelper.RecoverAnimation)paramList.get(paramInt);
        if ((ItemTouchHelper.RecoverAnimation.access$1900(paramCanvas)) && (!paramCanvas.mIsPendingCleanup)) {
          paramList.remove(paramInt);
        }
        for (;;)
        {
          paramInt -= 1;
          break;
          if (!ItemTouchHelper.RecoverAnimation.access$1900(paramCanvas)) {
            i = 1;
          }
        }
      }
      if (i != 0) {
        paramRecyclerView.invalidate();
      }
    }
    
    public boolean canDropOver(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      return true;
    }
    
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder paramViewHolder, List<RecyclerView.ViewHolder> paramList, int paramInt1, int paramInt2)
    {
      int n = paramViewHolder.itemView.getWidth();
      int i1 = paramViewHolder.itemView.getHeight();
      Object localObject2 = null;
      int j = -1;
      int i2 = paramInt1 - paramViewHolder.itemView.getLeft();
      int i3 = paramInt2 - paramViewHolder.itemView.getTop();
      int i4 = paramList.size();
      int k = 0;
      while (k < i4)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)paramList.get(k);
        Object localObject1 = localObject2;
        int i = j;
        int m;
        if (i2 > 0)
        {
          m = localViewHolder.itemView.getRight() - (paramInt1 + n);
          localObject1 = localObject2;
          i = j;
          if (m < 0)
          {
            localObject1 = localObject2;
            i = j;
            if (localViewHolder.itemView.getRight() > paramViewHolder.itemView.getRight())
            {
              m = Math.abs(m);
              localObject1 = localObject2;
              i = j;
              if (m > j)
              {
                i = m;
                localObject1 = localViewHolder;
              }
            }
          }
        }
        localObject2 = localObject1;
        j = i;
        if (i2 < 0)
        {
          m = localViewHolder.itemView.getLeft() - paramInt1;
          localObject2 = localObject1;
          j = i;
          if (m > 0)
          {
            localObject2 = localObject1;
            j = i;
            if (localViewHolder.itemView.getLeft() < paramViewHolder.itemView.getLeft())
            {
              m = Math.abs(m);
              localObject2 = localObject1;
              j = i;
              if (m > i)
              {
                j = m;
                localObject2 = localViewHolder;
              }
            }
          }
        }
        localObject1 = localObject2;
        i = j;
        if (i3 < 0)
        {
          m = localViewHolder.itemView.getTop() - paramInt2;
          localObject1 = localObject2;
          i = j;
          if (m > 0)
          {
            localObject1 = localObject2;
            i = j;
            if (localViewHolder.itemView.getTop() < paramViewHolder.itemView.getTop())
            {
              m = Math.abs(m);
              localObject1 = localObject2;
              i = j;
              if (m > j)
              {
                i = m;
                localObject1 = localViewHolder;
              }
            }
          }
        }
        localObject2 = localObject1;
        j = i;
        if (i3 > 0)
        {
          m = localViewHolder.itemView.getBottom() - (paramInt2 + i1);
          localObject2 = localObject1;
          j = i;
          if (m < 0)
          {
            localObject2 = localObject1;
            j = i;
            if (localViewHolder.itemView.getBottom() > paramViewHolder.itemView.getBottom())
            {
              m = Math.abs(m);
              localObject2 = localObject1;
              j = i;
              if (m > i)
              {
                j = m;
                localObject2 = localViewHolder;
              }
            }
          }
        }
        k += 1;
      }
      return (RecyclerView.ViewHolder)localObject2;
    }
    
    public void clearView(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      sUICallback.clearView(paramViewHolder.itemView);
    }
    
    public int convertToAbsoluteDirection(int paramInt1, int paramInt2)
    {
      int i = paramInt1 & 0x303030;
      if (i == 0) {
        return paramInt1;
      }
      paramInt1 &= (i ^ 0xFFFFFFFF);
      if (paramInt2 == 0) {
        return paramInt1 | i >> 2;
      }
      return paramInt1 | i >> 1 & 0xFFCFCFCF | (i >> 1 & 0x303030) >> 2;
    }
    
    final int getAbsoluteMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return convertToAbsoluteDirection(getMovementFlags(paramRecyclerView, paramViewHolder), ViewCompat.getLayoutDirection(paramRecyclerView));
    }
    
    public long getAnimationDuration(RecyclerView paramRecyclerView, int paramInt, float paramFloat1, float paramFloat2)
    {
      paramRecyclerView = paramRecyclerView.getItemAnimator();
      if (paramRecyclerView == null)
      {
        if (paramInt == 8) {
          return 200L;
        }
        return 250L;
      }
      if (paramInt == 8) {
        return paramRecyclerView.getMoveDuration();
      }
      return paramRecyclerView.getRemoveDuration();
    }
    
    public int getBoundingBoxMargin()
    {
      return 0;
    }
    
    public float getMoveThreshold(RecyclerView.ViewHolder paramViewHolder)
    {
      return 0.5F;
    }
    
    public abstract int getMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder);
    
    public float getSwipeEscapeVelocity(float paramFloat)
    {
      return paramFloat;
    }
    
    public float getSwipeThreshold(RecyclerView.ViewHolder paramViewHolder)
    {
      return 0.5F;
    }
    
    public float getSwipeVelocityThreshold(float paramFloat)
    {
      return paramFloat;
    }
    
    public int interpolateOutOfBoundsScroll(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3, long paramLong)
    {
      paramInt3 = getMaxDragScroll(paramRecyclerView);
      int i = Math.abs(paramInt2);
      int j = (int)Math.signum(paramInt2);
      float f = Math.min(1.0F, 1.0F * i / paramInt1);
      paramInt1 = (int)(j * paramInt3 * sDragViewScrollCapInterpolator.getInterpolation(f));
      if (paramLong > 500L) {}
      for (f = 1.0F;; f = (float)paramLong / 500.0F)
      {
        paramInt1 = (int)(paramInt1 * sDragScrollInterpolator.getInterpolation(f));
        if (paramInt1 != 0) {
          return paramInt1;
        }
        if (paramInt2 <= 0) {
          break;
        }
        return 1;
      }
      return -1;
      return paramInt1;
    }
    
    public boolean isItemViewSwipeEnabled()
    {
      return true;
    }
    
    public boolean isLongPressDragEnabled()
    {
      return true;
    }
    
    public void onChildDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      sUICallback.onDraw(paramCanvas, paramRecyclerView, paramViewHolder.itemView, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
    
    public void onChildDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      sUICallback.onDrawOver(paramCanvas, paramRecyclerView, paramViewHolder.itemView, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
    
    public abstract boolean onMove(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2);
    
    public void onMoved(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, int paramInt1, RecyclerView.ViewHolder paramViewHolder2, int paramInt2, int paramInt3, int paramInt4)
    {
      RecyclerView.LayoutManager localLayoutManager = paramRecyclerView.getLayoutManager();
      if ((localLayoutManager instanceof ItemTouchHelper.ViewDropHandler)) {
        ((ItemTouchHelper.ViewDropHandler)localLayoutManager).prepareForDrop(paramViewHolder1.itemView, paramViewHolder2.itemView, paramInt3, paramInt4);
      }
      do
      {
        do
        {
          return;
          if (localLayoutManager.canScrollHorizontally())
          {
            if (localLayoutManager.getDecoratedLeft(paramViewHolder2.itemView) <= paramRecyclerView.getPaddingLeft()) {
              paramRecyclerView.scrollToPosition(paramInt2);
            }
            if (localLayoutManager.getDecoratedRight(paramViewHolder2.itemView) >= paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight()) {
              paramRecyclerView.scrollToPosition(paramInt2);
            }
          }
        } while (!localLayoutManager.canScrollVertically());
        if (localLayoutManager.getDecoratedTop(paramViewHolder2.itemView) <= paramRecyclerView.getPaddingTop()) {
          paramRecyclerView.scrollToPosition(paramInt2);
        }
      } while (localLayoutManager.getDecoratedBottom(paramViewHolder2.itemView) < paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom());
      paramRecyclerView.scrollToPosition(paramInt2);
    }
    
    public void onSelectedChanged(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      if (paramViewHolder != null) {
        sUICallback.onSelected(paramViewHolder.itemView);
      }
    }
    
    public abstract void onSwiped(RecyclerView.ViewHolder paramViewHolder, int paramInt);
  }
  
  private class ItemTouchHelperGestureListener
    extends GestureDetector.SimpleOnGestureListener
  {
    private ItemTouchHelperGestureListener() {}
    
    public boolean onDown(MotionEvent paramMotionEvent)
    {
      return true;
    }
    
    public void onLongPress(MotionEvent paramMotionEvent)
    {
      Object localObject = ItemTouchHelper.this.findChildView(paramMotionEvent);
      if (localObject != null)
      {
        localObject = ItemTouchHelper.this.mRecyclerView.getChildViewHolder((View)localObject);
        if ((localObject != null) && (ItemTouchHelper.this.mCallback.hasDragFlag(ItemTouchHelper.this.mRecyclerView, (RecyclerView.ViewHolder)localObject))) {
          break label57;
        }
      }
      label57:
      do
      {
        do
        {
          return;
        } while (MotionEventCompat.getPointerId(paramMotionEvent, 0) != ItemTouchHelper.this.mActivePointerId);
        int i = MotionEventCompat.findPointerIndex(paramMotionEvent, ItemTouchHelper.this.mActivePointerId);
        float f1 = MotionEventCompat.getX(paramMotionEvent, i);
        float f2 = MotionEventCompat.getY(paramMotionEvent, i);
        ItemTouchHelper.this.mInitialTouchX = f1;
        ItemTouchHelper.this.mInitialTouchY = f2;
        paramMotionEvent = ItemTouchHelper.this;
        ItemTouchHelper.this.mDy = 0.0F;
        paramMotionEvent.mDx = 0.0F;
      } while (!ItemTouchHelper.this.mCallback.isLongPressDragEnabled());
      ItemTouchHelper.this.select((RecyclerView.ViewHolder)localObject, 2);
    }
  }
  
  private class RecoverAnimation
    implements AnimatorListenerCompat
  {
    final int mActionState;
    private final int mAnimationType;
    private boolean mEnded = false;
    private float mFraction;
    public boolean mIsPendingCleanup;
    boolean mOverridden = false;
    final float mStartDx;
    final float mStartDy;
    final float mTargetX;
    final float mTargetY;
    private final ValueAnimatorCompat mValueAnimator;
    final RecyclerView.ViewHolder mViewHolder;
    float mX;
    float mY;
    
    public RecoverAnimation(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      this.mActionState = paramInt2;
      this.mAnimationType = paramInt1;
      this.mViewHolder = paramViewHolder;
      this.mStartDx = paramFloat1;
      this.mStartDy = paramFloat2;
      this.mTargetX = paramFloat3;
      this.mTargetY = paramFloat4;
      this.mValueAnimator = AnimatorCompatHelper.emptyValueAnimator();
      this.mValueAnimator.addUpdateListener(new AnimatorUpdateListenerCompat()
      {
        public void onAnimationUpdate(ValueAnimatorCompat paramAnonymousValueAnimatorCompat)
        {
          ItemTouchHelper.RecoverAnimation.this.setFraction(paramAnonymousValueAnimatorCompat.getAnimatedFraction());
        }
      });
      this.mValueAnimator.setTarget(paramViewHolder.itemView);
      this.mValueAnimator.addListener(this);
      setFraction(0.0F);
    }
    
    public void cancel()
    {
      this.mValueAnimator.cancel();
    }
    
    public void onAnimationCancel(ValueAnimatorCompat paramValueAnimatorCompat)
    {
      setFraction(1.0F);
    }
    
    public void onAnimationEnd(ValueAnimatorCompat paramValueAnimatorCompat)
    {
      if (!this.mEnded) {
        this.mViewHolder.setIsRecyclable(true);
      }
      this.mEnded = true;
    }
    
    public void onAnimationRepeat(ValueAnimatorCompat paramValueAnimatorCompat) {}
    
    public void onAnimationStart(ValueAnimatorCompat paramValueAnimatorCompat) {}
    
    public void setDuration(long paramLong)
    {
      this.mValueAnimator.setDuration(paramLong);
    }
    
    public void setFraction(float paramFloat)
    {
      this.mFraction = paramFloat;
    }
    
    public void start()
    {
      this.mViewHolder.setIsRecyclable(false);
      this.mValueAnimator.start();
    }
    
    public void update()
    {
      if (this.mStartDx == this.mTargetX) {}
      for (this.mX = ViewCompat.getTranslationX(this.mViewHolder.itemView); this.mStartDy == this.mTargetY; this.mX = (this.mStartDx + this.mFraction * (this.mTargetX - this.mStartDx)))
      {
        this.mY = ViewCompat.getTranslationY(this.mViewHolder.itemView);
        return;
      }
      this.mY = (this.mStartDy + this.mFraction * (this.mTargetY - this.mStartDy));
    }
  }
  
  public static abstract class SimpleCallback
    extends ItemTouchHelper.Callback
  {
    private int mDefaultDragDirs;
    private int mDefaultSwipeDirs;
    
    public SimpleCallback(int paramInt1, int paramInt2)
    {
      this.mDefaultSwipeDirs = paramInt2;
      this.mDefaultDragDirs = paramInt1;
    }
    
    public int getDragDirs(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return this.mDefaultDragDirs;
    }
    
    public int getMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return makeMovementFlags(getDragDirs(paramRecyclerView, paramViewHolder), getSwipeDirs(paramRecyclerView, paramViewHolder));
    }
    
    public int getSwipeDirs(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return this.mDefaultSwipeDirs;
    }
    
    public void setDefaultDragDirs(int paramInt)
    {
      this.mDefaultDragDirs = paramInt;
    }
    
    public void setDefaultSwipeDirs(int paramInt)
    {
      this.mDefaultSwipeDirs = paramInt;
    }
  }
  
  public static abstract interface ViewDropHandler
  {
    public abstract void prepareForDrop(View paramView1, View paramView2, int paramInt1, int paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/helper/ItemTouchHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */