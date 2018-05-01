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

public class ItemTouchHelper
  extends RecyclerView.ItemDecoration
  implements RecyclerView.OnChildAttachStateChangeListener
{
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
  private RecyclerView.ChildDrawingOrderCallback mChildDrawingOrderCallback = null;
  private List<Integer> mDistances;
  private long mDragScrollStartTimeInMs;
  float mDx;
  float mDy;
  GestureDetectorCompat mGestureDetector;
  float mInitialTouchX;
  float mInitialTouchY;
  private ItemTouchHelperGestureListener mItemTouchHelperGestureListener;
  float mMaxSwipeVelocity;
  private final RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.OnItemTouchListener()
  {
    public boolean onInterceptTouchEvent(RecyclerView paramAnonymousRecyclerView, MotionEvent paramAnonymousMotionEvent)
    {
      boolean bool = true;
      ItemTouchHelper.this.mGestureDetector.onTouchEvent(paramAnonymousMotionEvent);
      int i = paramAnonymousMotionEvent.getActionMasked();
      if (i == 0)
      {
        ItemTouchHelper.this.mActivePointerId = paramAnonymousMotionEvent.getPointerId(0);
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
        if (ItemTouchHelper.this.mVelocityTracker != null) {
          ItemTouchHelper.this.mVelocityTracker.addMovement(paramAnonymousMotionEvent);
        }
        if (ItemTouchHelper.this.mSelected == null) {
          break label328;
        }
      }
      for (;;)
      {
        return bool;
        if ((i == 3) || (i == 1))
        {
          ItemTouchHelper.this.mActivePointerId = -1;
          ItemTouchHelper.this.select(null, 0);
          break;
        }
        if (ItemTouchHelper.this.mActivePointerId == -1) {
          break;
        }
        int j = paramAnonymousMotionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
        if (j < 0) {
          break;
        }
        ItemTouchHelper.this.checkSelectForSwipe(i, paramAnonymousMotionEvent, j);
        break;
        label328:
        bool = false;
      }
    }
    
    public void onRequestDisallowInterceptTouchEvent(boolean paramAnonymousBoolean)
    {
      if (!paramAnonymousBoolean) {}
      for (;;)
      {
        return;
        ItemTouchHelper.this.select(null, 0);
      }
    }
    
    public void onTouchEvent(RecyclerView paramAnonymousRecyclerView, MotionEvent paramAnonymousMotionEvent)
    {
      int i = 0;
      ItemTouchHelper.this.mGestureDetector.onTouchEvent(paramAnonymousMotionEvent);
      if (ItemTouchHelper.this.mVelocityTracker != null) {
        ItemTouchHelper.this.mVelocityTracker.addMovement(paramAnonymousMotionEvent);
      }
      if (ItemTouchHelper.this.mActivePointerId == -1) {}
      for (;;)
      {
        return;
        int j = paramAnonymousMotionEvent.getActionMasked();
        int k = paramAnonymousMotionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
        if (k >= 0) {
          ItemTouchHelper.this.checkSelectForSwipe(j, paramAnonymousMotionEvent, k);
        }
        paramAnonymousRecyclerView = ItemTouchHelper.this.mSelected;
        if (paramAnonymousRecyclerView != null) {
          switch (j)
          {
          case 4: 
          case 5: 
          default: 
            break;
          case 1: 
          case 2: 
          case 3: 
            for (;;)
            {
              ItemTouchHelper.this.select(null, 0);
              ItemTouchHelper.this.mActivePointerId = -1;
              break;
              if (k < 0) {
                break;
              }
              ItemTouchHelper.this.updateDxDy(paramAnonymousMotionEvent, ItemTouchHelper.this.mSelectedFlags, k);
              ItemTouchHelper.this.moveIfNecessary(paramAnonymousRecyclerView);
              ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
              ItemTouchHelper.this.mScrollRunnable.run();
              ItemTouchHelper.this.mRecyclerView.invalidate();
              break;
              if (ItemTouchHelper.this.mVelocityTracker != null) {
                ItemTouchHelper.this.mVelocityTracker.clear();
              }
            }
          case 6: 
            j = paramAnonymousMotionEvent.getActionIndex();
            if (paramAnonymousMotionEvent.getPointerId(j) == ItemTouchHelper.this.mActivePointerId)
            {
              if (j == 0) {
                i = 1;
              }
              ItemTouchHelper.this.mActivePointerId = paramAnonymousMotionEvent.getPointerId(i);
              ItemTouchHelper.this.updateDxDy(paramAnonymousMotionEvent, ItemTouchHelper.this.mSelectedFlags, j);
            }
            break;
          }
        }
      }
    }
  };
  View mOverdrawChild = null;
  int mOverdrawChildPosition = -1;
  final List<View> mPendingCleanup = new ArrayList();
  List<RecoverAnimation> mRecoverAnimations = new ArrayList();
  RecyclerView mRecyclerView;
  final Runnable mScrollRunnable = new Runnable()
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
  VelocityTracker mVelocityTracker;
  
  public ItemTouchHelper(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  private void addChildDrawingOrderCallback()
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    for (;;)
    {
      return;
      if (this.mChildDrawingOrderCallback == null) {
        this.mChildDrawingOrderCallback = new RecyclerView.ChildDrawingOrderCallback()
        {
          public int onGetChildDrawingOrder(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            if (ItemTouchHelper.this.mOverdrawChild == null) {
              paramAnonymousInt1 = paramAnonymousInt2;
            }
            for (;;)
            {
              return paramAnonymousInt1;
              int i = ItemTouchHelper.this.mOverdrawChildPosition;
              int j = i;
              if (i == -1)
              {
                j = ItemTouchHelper.this.mRecyclerView.indexOfChild(ItemTouchHelper.this.mOverdrawChild);
                ItemTouchHelper.this.mOverdrawChildPosition = j;
              }
              if (paramAnonymousInt2 == paramAnonymousInt1 - 1)
              {
                paramAnonymousInt1 = j;
              }
              else
              {
                paramAnonymousInt1 = paramAnonymousInt2;
                if (paramAnonymousInt2 >= j) {
                  paramAnonymousInt1 = paramAnonymousInt2 + 1;
                }
              }
            }
          }
        };
      }
      this.mRecyclerView.setChildDrawingOrderCallback(this.mChildDrawingOrderCallback);
    }
  }
  
  private int checkHorizontalSwipe(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    int i;
    float f1;
    float f2;
    int j;
    if ((paramInt & 0xC) != 0) {
      if (this.mDx > 0.0F)
      {
        i = 8;
        if ((this.mVelocityTracker == null) || (this.mActivePointerId <= -1)) {
          break label154;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
        f1 = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
        f2 = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
        if (f1 <= 0.0F) {
          break label148;
        }
        j = 8;
        label92:
        f1 = Math.abs(f1);
        if (((j & paramInt) == 0) || (i != j) || (f1 < this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity)) || (f1 <= Math.abs(f2))) {
          break label154;
        }
      }
    }
    for (;;)
    {
      return j;
      i = 4;
      break;
      label148:
      j = 4;
      break label92;
      label154:
      f1 = this.mRecyclerView.getWidth();
      f2 = this.mCallback.getSwipeThreshold(paramViewHolder);
      if (((paramInt & i) != 0) && (Math.abs(this.mDx) > f1 * f2)) {
        j = i;
      } else {
        j = 0;
      }
    }
  }
  
  private int checkVerticalSwipe(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    int i;
    float f1;
    float f2;
    int j;
    if ((paramInt & 0x3) != 0) {
      if (this.mDy > 0.0F)
      {
        i = 2;
        if ((this.mVelocityTracker == null) || (this.mActivePointerId <= -1)) {
          break label151;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
        f1 = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
        f2 = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
        if (f2 <= 0.0F) {
          break label145;
        }
        j = 2;
        label89:
        f2 = Math.abs(f2);
        if (((j & paramInt) == 0) || (j != i) || (f2 < this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity)) || (f2 <= Math.abs(f1))) {
          break label151;
        }
      }
    }
    for (;;)
    {
      return j;
      i = 1;
      break;
      label145:
      j = 1;
      break label89;
      label151:
      f2 = this.mRecyclerView.getHeight();
      f1 = this.mCallback.getSwipeThreshold(paramViewHolder);
      if (((paramInt & i) != 0) && (Math.abs(this.mDy) > f2 * f1)) {
        j = i;
      } else {
        j = 0;
      }
    }
  }
  
  private void destroyCallbacks()
  {
    this.mRecyclerView.removeItemDecoration(this);
    this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
    this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
    for (int i = this.mRecoverAnimations.size() - 1; i >= 0; i--)
    {
      RecoverAnimation localRecoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(0);
      this.mCallback.clearView(this.mRecyclerView, localRecoverAnimation.mViewHolder);
    }
    this.mRecoverAnimations.clear();
    this.mOverdrawChild = null;
    this.mOverdrawChildPosition = -1;
    releaseVelocityTracker();
    stopGestureDetection();
  }
  
  private List<RecyclerView.ViewHolder> findSwapTargets(RecyclerView.ViewHolder paramViewHolder)
  {
    int i;
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    label134:
    View localView;
    if (this.mSwapTargets == null)
    {
      this.mSwapTargets = new ArrayList();
      this.mDistances = new ArrayList();
      i = this.mCallback.getBoundingBoxMargin();
      j = Math.round(this.mSelectedStartX + this.mDx) - i;
      k = Math.round(this.mSelectedStartY + this.mDy) - i;
      m = paramViewHolder.itemView.getWidth() + j + i * 2;
      n = paramViewHolder.itemView.getHeight() + k + i * 2;
      i1 = (j + m) / 2;
      i2 = (k + n) / 2;
      RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
      int i3 = localLayoutManager.getChildCount();
      i = 0;
      if (i >= i3) {
        break label396;
      }
      localView = localLayoutManager.getChildAt(i);
      if (localView != paramViewHolder.itemView) {
        break label184;
      }
    }
    for (;;)
    {
      i++;
      break label134;
      this.mSwapTargets.clear();
      this.mDistances.clear();
      break;
      label184:
      if ((localView.getBottom() >= k) && (localView.getTop() <= n) && (localView.getRight() >= j) && (localView.getLeft() <= m))
      {
        RecyclerView.ViewHolder localViewHolder = this.mRecyclerView.getChildViewHolder(localView);
        if (this.mCallback.canDropOver(this.mRecyclerView, this.mSelected, localViewHolder))
        {
          int i4 = Math.abs(i1 - (localView.getLeft() + localView.getRight()) / 2);
          int i5 = Math.abs(i2 - (localView.getTop() + localView.getBottom()) / 2);
          int i6 = i4 * i4 + i5 * i5;
          i5 = 0;
          int i7 = this.mSwapTargets.size();
          for (i4 = 0; (i4 < i7) && (i6 > ((Integer)this.mDistances.get(i4)).intValue()); i4++) {
            i5++;
          }
          this.mSwapTargets.add(i5, localViewHolder);
          this.mDistances.add(i5, Integer.valueOf(i6));
        }
      }
    }
    label396:
    return this.mSwapTargets;
  }
  
  private RecyclerView.ViewHolder findSwipedView(MotionEvent paramMotionEvent)
  {
    Object localObject1 = null;
    RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
    Object localObject2;
    if (this.mActivePointerId == -1) {
      localObject2 = localObject1;
    }
    for (;;)
    {
      return (RecyclerView.ViewHolder)localObject2;
      int i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
      float f1 = paramMotionEvent.getX(i);
      float f2 = this.mInitialTouchX;
      float f3 = paramMotionEvent.getY(i);
      float f4 = this.mInitialTouchY;
      f2 = Math.abs(f1 - f2);
      f4 = Math.abs(f3 - f4);
      if (f2 < this.mSlop)
      {
        localObject2 = localObject1;
        if (f4 < this.mSlop) {}
      }
      else if (f2 > f4)
      {
        localObject2 = localObject1;
        if (localLayoutManager.canScrollHorizontally()) {}
      }
      else if (f4 > f2)
      {
        localObject2 = localObject1;
        if (localLayoutManager.canScrollVertically()) {}
      }
      else
      {
        paramMotionEvent = findChildView(paramMotionEvent);
        localObject2 = localObject1;
        if (paramMotionEvent != null) {
          localObject2 = this.mRecyclerView.getChildViewHolder(paramMotionEvent);
        }
      }
    }
  }
  
  private void getSelectedDxDy(float[] paramArrayOfFloat)
  {
    if ((this.mSelectedFlags & 0xC) != 0)
    {
      paramArrayOfFloat[0] = (this.mSelectedStartX + this.mDx - this.mSelected.itemView.getLeft());
      if ((this.mSelectedFlags & 0x3) == 0) {
        break label84;
      }
      paramArrayOfFloat[1] = (this.mSelectedStartY + this.mDy - this.mSelected.itemView.getTop());
    }
    for (;;)
    {
      return;
      paramArrayOfFloat[0] = this.mSelected.itemView.getTranslationX();
      break;
      label84:
      paramArrayOfFloat[1] = this.mSelected.itemView.getTranslationY();
    }
  }
  
  private static boolean hitTest(View paramView, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if ((paramFloat1 >= paramFloat3) && (paramFloat1 <= paramView.getWidth() + paramFloat3) && (paramFloat2 >= paramFloat4) && (paramFloat2 <= paramView.getHeight() + paramFloat4)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void releaseVelocityTracker()
  {
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  private void setupCallbacks()
  {
    this.mSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
    this.mRecyclerView.addItemDecoration(this);
    this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
    this.mRecyclerView.addOnChildAttachStateChangeListener(this);
    startGestureDetection();
  }
  
  private void startGestureDetection()
  {
    this.mItemTouchHelperGestureListener = new ItemTouchHelperGestureListener();
    this.mGestureDetector = new GestureDetectorCompat(this.mRecyclerView.getContext(), this.mItemTouchHelperGestureListener);
  }
  
  private void stopGestureDetection()
  {
    if (this.mItemTouchHelperGestureListener != null)
    {
      this.mItemTouchHelperGestureListener.doNotReactToLongPress();
      this.mItemTouchHelperGestureListener = null;
    }
    if (this.mGestureDetector != null) {
      this.mGestureDetector = null;
    }
  }
  
  private int swipeIfNecessary(RecyclerView.ViewHolder paramViewHolder)
  {
    int i;
    if (this.mActionState == 2) {
      i = 0;
    }
    for (;;)
    {
      return i;
      i = this.mCallback.getMovementFlags(this.mRecyclerView, paramViewHolder);
      int j = (this.mCallback.convertToAbsoluteDirection(i, ViewCompat.getLayoutDirection(this.mRecyclerView)) & 0xFF00) >> 8;
      if (j == 0)
      {
        i = 0;
      }
      else
      {
        int k = (i & 0xFF00) >> 8;
        int m;
        if (Math.abs(this.mDx) > Math.abs(this.mDy))
        {
          m = checkHorizontalSwipe(paramViewHolder, j);
          if (m > 0)
          {
            i = m;
            if ((k & m) == 0) {
              i = Callback.convertToRelativeDirection(m, ViewCompat.getLayoutDirection(this.mRecyclerView));
            }
          }
          else
          {
            m = checkVerticalSwipe(paramViewHolder, j);
            i = m;
            if (m > 0) {}
          }
        }
        else
        {
          do
          {
            i = 0;
            break;
            m = checkVerticalSwipe(paramViewHolder, j);
            i = m;
            if (m > 0) {
              break;
            }
            m = checkHorizontalSwipe(paramViewHolder, j);
          } while (m <= 0);
          i = m;
          if ((k & m) == 0) {
            i = Callback.convertToRelativeDirection(m, ViewCompat.getLayoutDirection(this.mRecyclerView));
          }
        }
      }
    }
  }
  
  public void attachToRecyclerView(RecyclerView paramRecyclerView)
  {
    if (this.mRecyclerView == paramRecyclerView) {}
    for (;;)
    {
      return;
      if (this.mRecyclerView != null) {
        destroyCallbacks();
      }
      this.mRecyclerView = paramRecyclerView;
      if (this.mRecyclerView != null)
      {
        paramRecyclerView.getResources();
        this.mSwipeEscapeVelocity = AndroidUtilities.dp(120.0F);
        this.mMaxSwipeVelocity = AndroidUtilities.dp(800.0F);
        setupCallbacks();
      }
    }
  }
  
  boolean checkSelectForSwipe(int paramInt1, MotionEvent paramMotionEvent, int paramInt2)
  {
    boolean bool;
    if ((this.mSelected != null) || (paramInt1 != 2) || (this.mActionState == 2) || (!this.mCallback.isItemViewSwipeEnabled())) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      if (this.mRecyclerView.getScrollState() == 1)
      {
        bool = false;
      }
      else
      {
        RecyclerView.ViewHolder localViewHolder = findSwipedView(paramMotionEvent);
        if (localViewHolder == null)
        {
          bool = false;
        }
        else
        {
          paramInt1 = (0xFF00 & this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, localViewHolder)) >> 8;
          if (paramInt1 == 0)
          {
            bool = false;
          }
          else
          {
            float f1 = paramMotionEvent.getX(paramInt2);
            float f2 = paramMotionEvent.getY(paramInt2);
            f1 -= this.mInitialTouchX;
            float f3 = f2 - this.mInitialTouchY;
            f2 = Math.abs(f1);
            float f4 = Math.abs(f3);
            if ((f2 < this.mSlop) && (f4 < this.mSlop))
            {
              bool = false;
            }
            else
            {
              if (f2 > f4)
              {
                if ((f1 < 0.0F) && ((paramInt1 & 0x4) == 0))
                {
                  bool = false;
                  continue;
                }
                if ((f1 > 0.0F) && ((paramInt1 & 0x8) == 0)) {
                  bool = false;
                }
              }
              else
              {
                if ((f3 < 0.0F) && ((paramInt1 & 0x1) == 0))
                {
                  bool = false;
                  continue;
                }
                if ((f3 > 0.0F) && ((paramInt1 & 0x2) == 0))
                {
                  bool = false;
                  continue;
                }
              }
              this.mDy = 0.0F;
              this.mDx = 0.0F;
              this.mActivePointerId = paramMotionEvent.getPointerId(0);
              select(localViewHolder, 1);
              bool = true;
            }
          }
        }
      }
    }
  }
  
  int endRecoverAnimation(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    int i = this.mRecoverAnimations.size() - 1;
    RecoverAnimation localRecoverAnimation;
    if (i >= 0)
    {
      localRecoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(i);
      if (localRecoverAnimation.mViewHolder == paramViewHolder)
      {
        localRecoverAnimation.mOverridden |= paramBoolean;
        if (!localRecoverAnimation.mEnded) {
          localRecoverAnimation.cancel();
        }
        this.mRecoverAnimations.remove(i);
      }
    }
    for (i = localRecoverAnimation.mAnimationType;; i = 0)
    {
      return i;
      i--;
      break;
    }
  }
  
  RecoverAnimation findAnimation(MotionEvent paramMotionEvent)
  {
    if (this.mRecoverAnimations.isEmpty()) {
      paramMotionEvent = null;
    }
    for (;;)
    {
      return paramMotionEvent;
      View localView = findChildView(paramMotionEvent);
      for (int i = this.mRecoverAnimations.size() - 1;; i--)
      {
        if (i < 0) {
          break label74;
        }
        RecoverAnimation localRecoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(i);
        paramMotionEvent = localRecoverAnimation;
        if (localRecoverAnimation.mViewHolder.itemView == localView) {
          break;
        }
      }
      label74:
      paramMotionEvent = null;
    }
  }
  
  View findChildView(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    if (this.mSelected != null)
    {
      paramMotionEvent = this.mSelected.itemView;
      if (!hitTest(paramMotionEvent, f1, f2, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy)) {}
    }
    for (;;)
    {
      return paramMotionEvent;
      for (int i = this.mRecoverAnimations.size() - 1;; i--)
      {
        if (i < 0) {
          break label125;
        }
        RecoverAnimation localRecoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(i);
        paramMotionEvent = localRecoverAnimation.mViewHolder.itemView;
        if (hitTest(paramMotionEvent, f1, f2, localRecoverAnimation.mX, localRecoverAnimation.mY)) {
          break;
        }
      }
      label125:
      paramMotionEvent = this.mRecyclerView.findChildViewUnder(f1, f2);
    }
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    paramRect.setEmpty();
  }
  
  boolean hasRunningRecoverAnim()
  {
    int i = this.mRecoverAnimations.size();
    int j = 0;
    if (j < i) {
      if (((RecoverAnimation)this.mRecoverAnimations.get(j)).mEnded) {}
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      j++;
      break;
    }
  }
  
  void moveIfNecessary(RecyclerView.ViewHolder paramViewHolder)
  {
    if (this.mRecyclerView.isLayoutRequested()) {}
    for (;;)
    {
      return;
      if (this.mActionState == 2)
      {
        float f = this.mCallback.getMoveThreshold(paramViewHolder);
        int i = (int)(this.mSelectedStartX + this.mDx);
        int j = (int)(this.mSelectedStartY + this.mDy);
        if ((Math.abs(j - paramViewHolder.itemView.getTop()) >= paramViewHolder.itemView.getHeight() * f) || (Math.abs(i - paramViewHolder.itemView.getLeft()) >= paramViewHolder.itemView.getWidth() * f))
        {
          Object localObject = findSwapTargets(paramViewHolder);
          if (((List)localObject).size() != 0)
          {
            localObject = this.mCallback.chooseDropTarget(paramViewHolder, (List)localObject, i, j);
            if (localObject == null)
            {
              this.mSwapTargets.clear();
              this.mDistances.clear();
            }
            else
            {
              int k = ((RecyclerView.ViewHolder)localObject).getAdapterPosition();
              int m = paramViewHolder.getAdapterPosition();
              if (this.mCallback.onMove(this.mRecyclerView, paramViewHolder, (RecyclerView.ViewHolder)localObject)) {
                this.mCallback.onMoved(this.mRecyclerView, paramViewHolder, m, (RecyclerView.ViewHolder)localObject, k, i, j);
              }
            }
          }
        }
      }
    }
  }
  
  void obtainVelocityTracker()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
    }
    this.mVelocityTracker = VelocityTracker.obtain();
  }
  
  public void onChildViewAttachedToWindow(View paramView) {}
  
  public void onChildViewDetachedFromWindow(View paramView)
  {
    removeChildDrawingOrderCallbackIfNecessary(paramView);
    paramView = this.mRecyclerView.getChildViewHolder(paramView);
    if (paramView == null) {}
    for (;;)
    {
      return;
      if ((this.mSelected != null) && (paramView == this.mSelected))
      {
        select(null, 0);
      }
      else
      {
        endRecoverAnimation(paramView, false);
        if (this.mPendingCleanup.remove(paramView.itemView)) {
          this.mCallback.clearView(this.mRecyclerView, paramView);
        }
      }
    }
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
  
  void postDispatchSwipe(final RecoverAnimation paramRecoverAnimation, final int paramInt)
  {
    this.mRecyclerView.post(new Runnable()
    {
      public void run()
      {
        if ((ItemTouchHelper.this.mRecyclerView != null) && (ItemTouchHelper.this.mRecyclerView.isAttachedToWindow()) && (!paramRecoverAnimation.mOverridden) && (paramRecoverAnimation.mViewHolder.getAdapterPosition() != -1))
        {
          RecyclerView.ItemAnimator localItemAnimator = ItemTouchHelper.this.mRecyclerView.getItemAnimator();
          if (((localItemAnimator != null) && (localItemAnimator.isRunning(null))) || (ItemTouchHelper.this.hasRunningRecoverAnim())) {
            break label102;
          }
          ItemTouchHelper.this.mCallback.onSwiped(paramRecoverAnimation.mViewHolder, paramInt);
        }
        for (;;)
        {
          return;
          label102:
          ItemTouchHelper.this.mRecyclerView.post(this);
        }
      }
    });
  }
  
  void removeChildDrawingOrderCallbackIfNecessary(View paramView)
  {
    if (paramView == this.mOverdrawChild)
    {
      this.mOverdrawChild = null;
      if (this.mChildDrawingOrderCallback != null) {
        this.mRecyclerView.setChildDrawingOrderCallback(null);
      }
    }
  }
  
  boolean scrollIfNecessary()
  {
    boolean bool;
    if (this.mSelected == null)
    {
      this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
      bool = false;
    }
    for (;;)
    {
      return bool;
      long l1 = System.currentTimeMillis();
      long l2;
      label36:
      int i;
      int j;
      int k;
      int m;
      if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE)
      {
        l2 = 0L;
        RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
        if (this.mTmpRect == null) {
          this.mTmpRect = new Rect();
        }
        i = 0;
        j = 0;
        localLayoutManager.calculateItemDecorationsForChild(this.mSelected.itemView, this.mTmpRect);
        k = i;
        if (localLayoutManager.canScrollHorizontally())
        {
          m = (int)(this.mSelectedStartX + this.mDx);
          k = m - this.mTmpRect.left - this.mRecyclerView.getPaddingLeft();
          if ((this.mDx >= 0.0F) || (k >= 0)) {
            break label340;
          }
        }
        label143:
        i = j;
        if (localLayoutManager.canScrollVertically())
        {
          m = (int)(this.mSelectedStartY + this.mDy);
          i = m - this.mTmpRect.top - this.mRecyclerView.getPaddingTop();
          if ((this.mDy >= 0.0F) || (i >= 0)) {
            break label408;
          }
        }
      }
      for (;;)
      {
        j = k;
        if (k != 0) {
          j = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getWidth(), k, this.mRecyclerView.getWidth(), l2);
        }
        k = i;
        if (i != 0) {
          k = this.mCallback.interpolateOutOfBoundsScroll(this.mRecyclerView, this.mSelected.itemView.getHeight(), i, this.mRecyclerView.getHeight(), l2);
        }
        if ((j == 0) && (k == 0)) {
          break label476;
        }
        if (this.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
          this.mDragScrollStartTimeInMs = l1;
        }
        this.mRecyclerView.scrollBy(j, k);
        bool = true;
        break;
        l2 = l1 - this.mDragScrollStartTimeInMs;
        break label36;
        label340:
        k = i;
        if (this.mDx <= 0.0F) {
          break label143;
        }
        m = this.mSelected.itemView.getWidth() + m + this.mTmpRect.right - (this.mRecyclerView.getWidth() - this.mRecyclerView.getPaddingRight());
        k = i;
        if (m <= 0) {
          break label143;
        }
        k = m;
        break label143;
        label408:
        i = j;
        if (this.mDy > 0.0F)
        {
          m = this.mSelected.itemView.getHeight() + m + this.mTmpRect.bottom - (this.mRecyclerView.getHeight() - this.mRecyclerView.getPaddingBottom());
          i = j;
          if (m > 0) {
            i = m;
          }
        }
      }
      label476:
      this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
      bool = false;
    }
  }
  
  void select(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if ((paramViewHolder == this.mSelected) && (paramInt == this.mActionState)) {
      return;
    }
    this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
    int i = this.mActionState;
    endRecoverAnimation(paramViewHolder, true);
    this.mActionState = paramInt;
    if (paramInt == 2)
    {
      this.mOverdrawChild = paramViewHolder.itemView;
      addChildDrawingOrderCallback();
    }
    int j = 0;
    final int k = 0;
    final Object localObject;
    label96:
    float f1;
    float f2;
    if (this.mSelected != null)
    {
      localObject = this.mSelected;
      if (((RecyclerView.ViewHolder)localObject).itemView.getParent() == null) {
        break label511;
      }
      if (i == 2)
      {
        k = 0;
        releaseVelocityTracker();
      }
    }
    else
    {
      switch (k)
      {
      default: 
        f1 = 0.0F;
        f2 = 0.0F;
        label166:
        if (i == 2)
        {
          j = 8;
          label175:
          getSelectedDxDy(this.mTmpPosition);
          float f3 = this.mTmpPosition[0];
          float f4 = this.mTmpPosition[1];
          localObject = new RecoverAnimation((RecyclerView.ViewHolder)localObject, j, i, f3, f4, f1, f2)
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              super.onAnimationEnd(paramAnonymousAnimator);
              if (this.mOverridden) {}
              label120:
              for (;;)
              {
                return;
                if (k <= 0) {
                  ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, localObject);
                }
                for (;;)
                {
                  if (ItemTouchHelper.this.mOverdrawChild != localObject.itemView) {
                    break label120;
                  }
                  ItemTouchHelper.this.removeChildDrawingOrderCallbackIfNecessary(localObject.itemView);
                  break;
                  ItemTouchHelper.this.mPendingCleanup.add(localObject.itemView);
                  this.mIsPendingCleanup = true;
                  if (k > 0) {
                    ItemTouchHelper.this.postDispatchSwipe(this, k);
                  }
                }
              }
            }
          };
          ((RecoverAnimation)localObject).setDuration(this.mCallback.getAnimationDuration(this.mRecyclerView, j, f1 - f3, f2 - f4));
          this.mRecoverAnimations.add(localObject);
          ((RecoverAnimation)localObject).start();
          j = 1;
          label274:
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
              break label540;
            }
          }
        }
        break;
      }
    }
    label511:
    label540:
    for (boolean bool = true;; bool = false)
    {
      paramViewHolder.requestDisallowInterceptTouchEvent(bool);
      if (j == 0) {
        this.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
      }
      this.mCallback.onSelectedChanged(this.mSelected, this.mActionState);
      this.mRecyclerView.invalidate();
      break;
      k = swipeIfNecessary((RecyclerView.ViewHolder)localObject);
      break label96;
      f2 = 0.0F;
      f1 = Math.signum(this.mDx) * this.mRecyclerView.getWidth();
      break label166;
      f1 = 0.0F;
      f2 = Math.signum(this.mDy) * this.mRecyclerView.getHeight();
      break label166;
      if (k > 0)
      {
        j = 2;
        break label175;
      }
      j = 4;
      break label175;
      removeChildDrawingOrderCallbackIfNecessary(((RecyclerView.ViewHolder)localObject).itemView);
      this.mCallback.clearView(this.mRecyclerView, (RecyclerView.ViewHolder)localObject);
      j = k;
      break label274;
    }
  }
  
  public void startDrag(RecyclerView.ViewHolder paramViewHolder)
  {
    if (!this.mCallback.hasDragFlag(this.mRecyclerView, paramViewHolder)) {
      Log.e("ItemTouchHelper", "Start drag has been called but dragging is not enabled");
    }
    for (;;)
    {
      return;
      if (paramViewHolder.itemView.getParent() != this.mRecyclerView)
      {
        Log.e("ItemTouchHelper", "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this ItemTouchHelper.");
      }
      else
      {
        obtainVelocityTracker();
        this.mDy = 0.0F;
        this.mDx = 0.0F;
        select(paramViewHolder, 2);
      }
    }
  }
  
  public void startSwipe(RecyclerView.ViewHolder paramViewHolder)
  {
    if (!this.mCallback.hasSwipeFlag(this.mRecyclerView, paramViewHolder)) {
      Log.e("ItemTouchHelper", "Start swipe has been called but swiping is not enabled");
    }
    for (;;)
    {
      return;
      if (paramViewHolder.itemView.getParent() != this.mRecyclerView)
      {
        Log.e("ItemTouchHelper", "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this ItemTouchHelper.");
      }
      else
      {
        obtainVelocityTracker();
        this.mDy = 0.0F;
        this.mDx = 0.0F;
        select(paramViewHolder, 1);
      }
    }
  }
  
  void updateDxDy(MotionEvent paramMotionEvent, int paramInt1, int paramInt2)
  {
    float f1 = paramMotionEvent.getX(paramInt2);
    float f2 = paramMotionEvent.getY(paramInt2);
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
    private static final ItemTouchUIUtil sUICallback;
    private int mCachedMaxScrollSpeed = -1;
    
    static
    {
      if (Build.VERSION.SDK_INT >= 21) {}
      for (sUICallback = new ItemTouchUIUtilImpl.Api21Impl();; sUICallback = new ItemTouchUIUtilImpl.BaseImpl()) {
        return;
      }
    }
    
    public static int convertToRelativeDirection(int paramInt1, int paramInt2)
    {
      int i = paramInt1 & 0xC0C0C;
      if (i == 0) {}
      for (;;)
      {
        return paramInt1;
        paramInt1 &= (i ^ 0xFFFFFFFF);
        if (paramInt2 == 0) {
          paramInt1 |= i << 2;
        } else {
          paramInt1 = paramInt1 | i << 1 & 0xFFF3F3F3 | (i << 1 & 0xC0C0C) << 2;
        }
      }
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
    
    public static int makeFlag(int paramInt1, int paramInt2)
    {
      return paramInt2 << paramInt1 * 8;
    }
    
    public static int makeMovementFlags(int paramInt1, int paramInt2)
    {
      return makeFlag(0, paramInt2 | paramInt1) | makeFlag(1, paramInt2) | makeFlag(2, paramInt1);
    }
    
    public boolean canDropOver(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      return true;
    }
    
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder paramViewHolder, List<RecyclerView.ViewHolder> paramList, int paramInt1, int paramInt2)
    {
      int i = paramViewHolder.itemView.getWidth();
      int j = paramViewHolder.itemView.getHeight();
      Object localObject1 = null;
      int k = -1;
      int m = paramInt1 - paramViewHolder.itemView.getLeft();
      int n = paramInt2 - paramViewHolder.itemView.getTop();
      int i1 = paramList.size();
      for (int i2 = 0; i2 < i1; i2++)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)paramList.get(i2);
        Object localObject2 = localObject1;
        int i3 = k;
        int i4;
        if (m > 0)
        {
          i4 = localViewHolder.itemView.getRight() - (paramInt1 + i);
          localObject2 = localObject1;
          i3 = k;
          if (i4 < 0)
          {
            localObject2 = localObject1;
            i3 = k;
            if (localViewHolder.itemView.getRight() > paramViewHolder.itemView.getRight())
            {
              i4 = Math.abs(i4);
              localObject2 = localObject1;
              i3 = k;
              if (i4 > k)
              {
                i3 = i4;
                localObject2 = localViewHolder;
              }
            }
          }
        }
        localObject1 = localObject2;
        k = i3;
        if (m < 0)
        {
          i4 = localViewHolder.itemView.getLeft() - paramInt1;
          localObject1 = localObject2;
          k = i3;
          if (i4 > 0)
          {
            localObject1 = localObject2;
            k = i3;
            if (localViewHolder.itemView.getLeft() < paramViewHolder.itemView.getLeft())
            {
              i4 = Math.abs(i4);
              localObject1 = localObject2;
              k = i3;
              if (i4 > i3)
              {
                k = i4;
                localObject1 = localViewHolder;
              }
            }
          }
        }
        localObject2 = localObject1;
        i3 = k;
        if (n < 0)
        {
          i4 = localViewHolder.itemView.getTop() - paramInt2;
          localObject2 = localObject1;
          i3 = k;
          if (i4 > 0)
          {
            localObject2 = localObject1;
            i3 = k;
            if (localViewHolder.itemView.getTop() < paramViewHolder.itemView.getTop())
            {
              i4 = Math.abs(i4);
              localObject2 = localObject1;
              i3 = k;
              if (i4 > k)
              {
                i3 = i4;
                localObject2 = localViewHolder;
              }
            }
          }
        }
        localObject1 = localObject2;
        k = i3;
        if (n > 0)
        {
          i4 = localViewHolder.itemView.getBottom() - (paramInt2 + j);
          localObject1 = localObject2;
          k = i3;
          if (i4 < 0)
          {
            localObject1 = localObject2;
            k = i3;
            if (localViewHolder.itemView.getBottom() > paramViewHolder.itemView.getBottom())
            {
              i4 = Math.abs(i4);
              localObject1 = localObject2;
              k = i3;
              if (i4 > i3)
              {
                k = i4;
                localObject1 = localViewHolder;
              }
            }
          }
        }
      }
      return (RecyclerView.ViewHolder)localObject1;
    }
    
    public void clearView(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      sUICallback.clearView(paramViewHolder.itemView);
    }
    
    public int convertToAbsoluteDirection(int paramInt1, int paramInt2)
    {
      int i = paramInt1 & 0x303030;
      if (i == 0) {}
      for (;;)
      {
        return paramInt1;
        paramInt1 &= (i ^ 0xFFFFFFFF);
        if (paramInt2 == 0) {
          paramInt1 |= i >> 2;
        } else {
          paramInt1 = paramInt1 | i >> 1 & 0xFFCFCFCF | (i >> 1 & 0x303030) >> 2;
        }
      }
    }
    
    final int getAbsoluteMovementFlags(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      return convertToAbsoluteDirection(getMovementFlags(paramRecyclerView, paramViewHolder), ViewCompat.getLayoutDirection(paramRecyclerView));
    }
    
    public long getAnimationDuration(RecyclerView paramRecyclerView, int paramInt, float paramFloat1, float paramFloat2)
    {
      paramRecyclerView = paramRecyclerView.getItemAnimator();
      long l;
      if (paramRecyclerView == null) {
        if (paramInt == 8) {
          l = 200L;
        }
      }
      for (;;)
      {
        return l;
        l = 250L;
        continue;
        if (paramInt == 8) {
          l = paramRecyclerView.getMoveDuration();
        } else {
          l = paramRecyclerView.getRemoveDuration();
        }
      }
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
    
    boolean hasDragFlag(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      if ((0xFF0000 & getAbsoluteMovementFlags(paramRecyclerView, paramViewHolder)) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean hasSwipeFlag(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder)
    {
      if ((0xFF00 & getAbsoluteMovementFlags(paramRecyclerView, paramViewHolder)) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public int interpolateOutOfBoundsScroll(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3, long paramLong)
    {
      paramInt3 = getMaxDragScroll(paramRecyclerView);
      int i = Math.abs(paramInt2);
      int j = (int)Math.signum(paramInt2);
      float f = Math.min(1.0F, 1.0F * i / paramInt1);
      paramInt1 = (int)(j * paramInt3 * sDragViewScrollCapInterpolator.getInterpolation(f));
      if (paramLong > 500L)
      {
        f = 1.0F;
        paramInt1 = (int)(paramInt1 * sDragScrollInterpolator.getInterpolation(f));
        if (paramInt1 != 0) {
          break label109;
        }
        if (paramInt2 <= 0) {
          break label104;
        }
        paramInt1 = 1;
      }
      label104:
      label109:
      for (;;)
      {
        return paramInt1;
        f = (float)paramLong / 500.0F;
        break;
        paramInt1 = -1;
      }
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
    
    void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, List<ItemTouchHelper.RecoverAnimation> paramList, int paramInt, float paramFloat1, float paramFloat2)
    {
      int i = paramList.size();
      for (int j = 0; j < i; j++)
      {
        ItemTouchHelper.RecoverAnimation localRecoverAnimation = (ItemTouchHelper.RecoverAnimation)paramList.get(j);
        localRecoverAnimation.update();
        int k = paramCanvas.save();
        onChildDraw(paramCanvas, paramRecyclerView, localRecoverAnimation.mViewHolder, localRecoverAnimation.mX, localRecoverAnimation.mY, localRecoverAnimation.mActionState, false);
        paramCanvas.restoreToCount(k);
      }
      if (paramViewHolder != null)
      {
        j = paramCanvas.save();
        onChildDraw(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, true);
        paramCanvas.restoreToCount(j);
      }
    }
    
    void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder, List<ItemTouchHelper.RecoverAnimation> paramList, int paramInt, float paramFloat1, float paramFloat2)
    {
      int i = paramList.size();
      for (int j = 0; j < i; j++)
      {
        ItemTouchHelper.RecoverAnimation localRecoverAnimation = (ItemTouchHelper.RecoverAnimation)paramList.get(j);
        int k = paramCanvas.save();
        onChildDrawOver(paramCanvas, paramRecyclerView, localRecoverAnimation.mViewHolder, localRecoverAnimation.mX, localRecoverAnimation.mY, localRecoverAnimation.mActionState, false);
        paramCanvas.restoreToCount(k);
      }
      if (paramViewHolder != null)
      {
        j = paramCanvas.save();
        onChildDrawOver(paramCanvas, paramRecyclerView, paramViewHolder, paramFloat1, paramFloat2, paramInt, true);
        paramCanvas.restoreToCount(j);
      }
      j = 0;
      paramInt = i - 1;
      if (paramInt >= 0)
      {
        paramCanvas = (ItemTouchHelper.RecoverAnimation)paramList.get(paramInt);
        if ((paramCanvas.mEnded) && (!paramCanvas.mIsPendingCleanup)) {
          paramList.remove(paramInt);
        }
        for (;;)
        {
          paramInt--;
          break;
          if (!paramCanvas.mEnded) {
            j = 1;
          }
        }
      }
      if (j != 0) {
        paramRecyclerView.invalidate();
      }
    }
    
    public abstract boolean onMove(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2);
    
    public void onMoved(RecyclerView paramRecyclerView, RecyclerView.ViewHolder paramViewHolder1, int paramInt1, RecyclerView.ViewHolder paramViewHolder2, int paramInt2, int paramInt3, int paramInt4)
    {
      RecyclerView.LayoutManager localLayoutManager = paramRecyclerView.getLayoutManager();
      if ((localLayoutManager instanceof ItemTouchHelper.ViewDropHandler)) {
        ((ItemTouchHelper.ViewDropHandler)localLayoutManager).prepareForDrop(paramViewHolder1.itemView, paramViewHolder2.itemView, paramInt3, paramInt4);
      }
      for (;;)
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
        if (localLayoutManager.canScrollVertically())
        {
          if (localLayoutManager.getDecoratedTop(paramViewHolder2.itemView) <= paramRecyclerView.getPaddingTop()) {
            paramRecyclerView.scrollToPosition(paramInt2);
          }
          if (localLayoutManager.getDecoratedBottom(paramViewHolder2.itemView) >= paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom()) {
            paramRecyclerView.scrollToPosition(paramInt2);
          }
        }
      }
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
    private boolean mShouldReactToLongPress = true;
    
    ItemTouchHelperGestureListener() {}
    
    void doNotReactToLongPress()
    {
      this.mShouldReactToLongPress = false;
    }
    
    public boolean onDown(MotionEvent paramMotionEvent)
    {
      return true;
    }
    
    public void onLongPress(MotionEvent paramMotionEvent)
    {
      if (!this.mShouldReactToLongPress) {}
      for (;;)
      {
        return;
        Object localObject = ItemTouchHelper.this.findChildView(paramMotionEvent);
        if (localObject != null)
        {
          localObject = ItemTouchHelper.this.mRecyclerView.getChildViewHolder((View)localObject);
          if ((localObject != null) && (ItemTouchHelper.this.mCallback.hasDragFlag(ItemTouchHelper.this.mRecyclerView, (RecyclerView.ViewHolder)localObject)) && (paramMotionEvent.getPointerId(0) == ItemTouchHelper.this.mActivePointerId))
          {
            int i = paramMotionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
            float f1 = paramMotionEvent.getX(i);
            float f2 = paramMotionEvent.getY(i);
            ItemTouchHelper.this.mInitialTouchX = f1;
            ItemTouchHelper.this.mInitialTouchY = f2;
            paramMotionEvent = ItemTouchHelper.this;
            ItemTouchHelper.this.mDy = 0.0F;
            paramMotionEvent.mDx = 0.0F;
            if (ItemTouchHelper.this.mCallback.isLongPressDragEnabled()) {
              ItemTouchHelper.this.select((RecyclerView.ViewHolder)localObject, 2);
            }
          }
        }
      }
    }
  }
  
  private static class RecoverAnimation
    implements Animator.AnimatorListener
  {
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
    final RecyclerView.ViewHolder mViewHolder;
    float mX;
    float mY;
    
    RecoverAnimation(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      this.mActionState = paramInt2;
      this.mAnimationType = paramInt1;
      this.mViewHolder = paramViewHolder;
      this.mStartDx = paramFloat1;
      this.mStartDy = paramFloat2;
      this.mTargetX = paramFloat3;
      this.mTargetY = paramFloat4;
      this.mValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
      this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          ItemTouchHelper.RecoverAnimation.this.setFraction(paramAnonymousValueAnimator.getAnimatedFraction());
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
    
    public void onAnimationCancel(Animator paramAnimator)
    {
      setFraction(1.0F);
    }
    
    public void onAnimationEnd(Animator paramAnimator)
    {
      if (!this.mEnded) {
        this.mViewHolder.setIsRecyclable(true);
      }
      this.mEnded = true;
    }
    
    public void onAnimationRepeat(Animator paramAnimator) {}
    
    public void onAnimationStart(Animator paramAnimator) {}
    
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
      if (this.mStartDx == this.mTargetX)
      {
        this.mX = this.mViewHolder.itemView.getTranslationX();
        if (this.mStartDy != this.mTargetY) {
          break label79;
        }
      }
      label79:
      for (this.mY = this.mViewHolder.itemView.getTranslationY();; this.mY = (this.mStartDy + this.mFraction * (this.mTargetY - this.mStartDy)))
      {
        return;
        this.mX = (this.mStartDx + this.mFraction * (this.mTargetX - this.mStartDx));
        break;
      }
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