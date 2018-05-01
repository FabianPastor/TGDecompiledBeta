package android.support.v4.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.Scroller;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewPager
  extends ViewGroup
{
  private static final int CLOSE_ENOUGH = 2;
  private static final Comparator<ItemInfo> COMPARATOR = new Comparator()
  {
    public int compare(ViewPager.ItemInfo paramAnonymousItemInfo1, ViewPager.ItemInfo paramAnonymousItemInfo2)
    {
      return paramAnonymousItemInfo1.position - paramAnonymousItemInfo2.position;
    }
  };
  private static final boolean DEBUG = false;
  private static final int DEFAULT_GUTTER_SIZE = 16;
  private static final int DEFAULT_OFFSCREEN_PAGES = 1;
  private static final int DRAW_ORDER_DEFAULT = 0;
  private static final int DRAW_ORDER_FORWARD = 1;
  private static final int DRAW_ORDER_REVERSE = 2;
  private static final int INVALID_POINTER = -1;
  static final int[] LAYOUT_ATTRS = { 16842931 };
  private static final int MAX_SETTLE_DURATION = 600;
  private static final int MIN_DISTANCE_FOR_FLING = 25;
  private static final int MIN_FLING_VELOCITY = 400;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_SETTLING = 2;
  private static final String TAG = "ViewPager";
  private static final boolean USE_CACHE = false;
  private static final Interpolator sInterpolator = new Interpolator()
  {
    public float getInterpolation(float paramAnonymousFloat)
    {
      paramAnonymousFloat -= 1.0F;
      return paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat + 1.0F;
    }
  };
  private static final ViewPositionComparator sPositionComparator = new ViewPositionComparator();
  private int mActivePointerId = -1;
  PagerAdapter mAdapter;
  private List<OnAdapterChangeListener> mAdapterChangeListeners;
  private int mBottomPageBounds;
  private boolean mCalledSuper;
  private int mChildHeightMeasureSpec;
  private int mChildWidthMeasureSpec;
  private int mCloseEnough;
  int mCurItem;
  private int mDecorChildCount;
  private int mDefaultGutterSize;
  private int mDrawingOrder;
  private ArrayList<View> mDrawingOrderedChildren;
  private final Runnable mEndScrollRunnable = new Runnable()
  {
    public void run()
    {
      ViewPager.this.setScrollState(0);
      ViewPager.this.populate();
    }
  };
  private int mExpectedAdapterCount;
  private long mFakeDragBeginTime;
  private boolean mFakeDragging;
  private boolean mFirstLayout = true;
  private float mFirstOffset = -3.4028235E38F;
  private int mFlingDistance;
  private int mGutterSize;
  private boolean mInLayout;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private OnPageChangeListener mInternalPageChangeListener;
  private boolean mIsBeingDragged;
  private boolean mIsScrollStarted;
  private boolean mIsUnableToDrag;
  private final ArrayList<ItemInfo> mItems = new ArrayList();
  private float mLastMotionX;
  private float mLastMotionY;
  private float mLastOffset = Float.MAX_VALUE;
  private EdgeEffect mLeftEdge;
  private Drawable mMarginDrawable;
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  private boolean mNeedCalculatePageOffsets = false;
  private PagerObserver mObserver;
  private int mOffscreenPageLimit = 1;
  private OnPageChangeListener mOnPageChangeListener;
  private List<OnPageChangeListener> mOnPageChangeListeners;
  private int mPageMargin;
  private PageTransformer mPageTransformer;
  private int mPageTransformerLayerType;
  private boolean mPopulatePending;
  private Parcelable mRestoredAdapterState = null;
  private ClassLoader mRestoredClassLoader = null;
  private int mRestoredCurItem = -1;
  private EdgeEffect mRightEdge;
  private int mScrollState = 0;
  private Scroller mScroller;
  private boolean mScrollingCacheEnabled;
  private final ItemInfo mTempItem = new ItemInfo();
  private final Rect mTempRect = new Rect();
  private int mTopPageBounds;
  private int mTouchSlop;
  private VelocityTracker mVelocityTracker;
  
  public ViewPager(Context paramContext)
  {
    super(paramContext);
    initViewPager();
  }
  
  public ViewPager(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViewPager();
  }
  
  private void calculatePageOffsets(ItemInfo paramItemInfo1, int paramInt, ItemInfo paramItemInfo2)
  {
    int i = this.mAdapter.getCount();
    int j = getClientWidth();
    float f1;
    if (j > 0)
    {
      f1 = this.mPageMargin / j;
      if (paramItemInfo2 == null) {
        break label391;
      }
      j = paramItemInfo2.position;
      if (j < paramItemInfo1.position)
      {
        k = 0;
        f2 = paramItemInfo2.offset + paramItemInfo2.widthFactor + f1;
        j++;
      }
    }
    else
    {
      for (;;)
      {
        if ((j > paramItemInfo1.position) || (k >= this.mItems.size())) {
          break label391;
        }
        for (paramItemInfo2 = (ItemInfo)this.mItems.get(k);; paramItemInfo2 = (ItemInfo)this.mItems.get(k))
        {
          f3 = f2;
          m = j;
          if (j <= paramItemInfo2.position) {
            break;
          }
          f3 = f2;
          m = j;
          if (k >= this.mItems.size() - 1) {
            break;
          }
          k++;
        }
        f1 = 0.0F;
        break;
        while (m < paramItemInfo2.position)
        {
          f3 += this.mAdapter.getPageWidth(m) + f1;
          m++;
        }
        paramItemInfo2.offset = f3;
        f2 = f3 + (paramItemInfo2.widthFactor + f1);
        j = m + 1;
      }
    }
    if (j > paramItemInfo1.position)
    {
      k = this.mItems.size() - 1;
      f2 = paramItemInfo2.offset;
      j--;
      while ((j >= paramItemInfo1.position) && (k >= 0))
      {
        for (paramItemInfo2 = (ItemInfo)this.mItems.get(k);; paramItemInfo2 = (ItemInfo)this.mItems.get(k))
        {
          f3 = f2;
          m = j;
          if (j >= paramItemInfo2.position) {
            break;
          }
          f3 = f2;
          m = j;
          if (k <= 0) {
            break;
          }
          k--;
        }
        while (m > paramItemInfo2.position)
        {
          f3 -= this.mAdapter.getPageWidth(m) + f1;
          m--;
        }
        f2 = f3 - (paramItemInfo2.widthFactor + f1);
        paramItemInfo2.offset = f2;
        j = m - 1;
      }
    }
    label391:
    int m = this.mItems.size();
    float f3 = paramItemInfo1.offset;
    j = paramItemInfo1.position - 1;
    if (paramItemInfo1.position == 0)
    {
      f2 = paramItemInfo1.offset;
      this.mFirstOffset = f2;
      if (paramItemInfo1.position != i - 1) {
        break label529;
      }
      f2 = paramItemInfo1.offset + paramItemInfo1.widthFactor - 1.0F;
      label457:
      this.mLastOffset = f2;
      k = paramInt - 1;
      f2 = f3;
    }
    for (;;)
    {
      if (k < 0) {
        break label576;
      }
      paramItemInfo2 = (ItemInfo)this.mItems.get(k);
      for (;;)
      {
        if (j > paramItemInfo2.position)
        {
          f2 -= this.mAdapter.getPageWidth(j) + f1;
          j--;
          continue;
          f2 = -3.4028235E38F;
          break;
          label529:
          f2 = Float.MAX_VALUE;
          break label457;
        }
      }
      f2 -= paramItemInfo2.widthFactor + f1;
      paramItemInfo2.offset = f2;
      if (paramItemInfo2.position == 0) {
        this.mFirstOffset = f2;
      }
      k--;
      j--;
    }
    label576:
    float f2 = paramItemInfo1.offset + paramItemInfo1.widthFactor + f1;
    j = paramItemInfo1.position + 1;
    int k = paramInt + 1;
    paramInt = j;
    j = k;
    while (j < m)
    {
      paramItemInfo1 = (ItemInfo)this.mItems.get(j);
      while (paramInt < paramItemInfo1.position)
      {
        f2 += this.mAdapter.getPageWidth(paramInt) + f1;
        paramInt++;
      }
      if (paramItemInfo1.position == i - 1) {
        this.mLastOffset = (paramItemInfo1.widthFactor + f2 - 1.0F);
      }
      paramItemInfo1.offset = f2;
      f2 += paramItemInfo1.widthFactor + f1;
      j++;
      paramInt++;
    }
    this.mNeedCalculatePageOffsets = false;
  }
  
  private void completeScroll(boolean paramBoolean)
  {
    int i = 1;
    int j;
    if (this.mScrollState == 2)
    {
      j = 1;
      if (j != 0)
      {
        setScrollingCacheEnabled(false);
        if (this.mScroller.isFinished()) {
          break label169;
        }
      }
    }
    for (;;)
    {
      if (i != 0)
      {
        this.mScroller.abortAnimation();
        int k = getScrollX();
        m = getScrollY();
        int n = this.mScroller.getCurrX();
        i = this.mScroller.getCurrY();
        if ((k != n) || (m != i))
        {
          scrollTo(n, i);
          if (n != k) {
            pageScrolled(n);
          }
        }
      }
      this.mPopulatePending = false;
      int m = 0;
      i = j;
      for (j = m; j < this.mItems.size(); j++)
      {
        ItemInfo localItemInfo = (ItemInfo)this.mItems.get(j);
        if (localItemInfo.scrolling)
        {
          i = 1;
          localItemInfo.scrolling = false;
        }
      }
      j = 0;
      break;
      label169:
      i = 0;
    }
    if (i != 0)
    {
      if (!paramBoolean) {
        break label191;
      }
      ViewCompat.postOnAnimation(this, this.mEndScrollRunnable);
    }
    for (;;)
    {
      return;
      label191:
      this.mEndScrollRunnable.run();
    }
  }
  
  private int determineTargetPage(int paramInt1, float paramFloat, int paramInt2, int paramInt3)
  {
    if ((Math.abs(paramInt3) > this.mFlingDistance) && (Math.abs(paramInt2) > this.mMinimumVelocity))
    {
      if (paramInt2 > 0) {}
      for (;;)
      {
        paramInt2 = paramInt1;
        if (this.mItems.size() > 0)
        {
          ItemInfo localItemInfo1 = (ItemInfo)this.mItems.get(0);
          ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
          paramInt2 = Math.max(localItemInfo1.position, Math.min(paramInt1, localItemInfo2.position));
        }
        return paramInt2;
        paramInt1++;
      }
    }
    if (paramInt1 >= this.mCurItem) {}
    for (float f = 0.4F;; f = 0.6F)
    {
      paramInt1 += (int)(paramFloat + f);
      break;
    }
  }
  
  private void dispatchOnPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
    if (this.mOnPageChangeListener != null) {
      this.mOnPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
    }
    if (this.mOnPageChangeListeners != null)
    {
      int i = 0;
      int j = this.mOnPageChangeListeners.size();
      while (i < j)
      {
        OnPageChangeListener localOnPageChangeListener = (OnPageChangeListener)this.mOnPageChangeListeners.get(i);
        if (localOnPageChangeListener != null) {
          localOnPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
        }
        i++;
      }
    }
    if (this.mInternalPageChangeListener != null) {
      this.mInternalPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
    }
  }
  
  private void dispatchOnPageSelected(int paramInt)
  {
    if (this.mOnPageChangeListener != null) {
      this.mOnPageChangeListener.onPageSelected(paramInt);
    }
    if (this.mOnPageChangeListeners != null)
    {
      int i = 0;
      int j = this.mOnPageChangeListeners.size();
      while (i < j)
      {
        OnPageChangeListener localOnPageChangeListener = (OnPageChangeListener)this.mOnPageChangeListeners.get(i);
        if (localOnPageChangeListener != null) {
          localOnPageChangeListener.onPageSelected(paramInt);
        }
        i++;
      }
    }
    if (this.mInternalPageChangeListener != null) {
      this.mInternalPageChangeListener.onPageSelected(paramInt);
    }
  }
  
  private void dispatchOnScrollStateChanged(int paramInt)
  {
    if (this.mOnPageChangeListener != null) {
      this.mOnPageChangeListener.onPageScrollStateChanged(paramInt);
    }
    if (this.mOnPageChangeListeners != null)
    {
      int i = 0;
      int j = this.mOnPageChangeListeners.size();
      while (i < j)
      {
        OnPageChangeListener localOnPageChangeListener = (OnPageChangeListener)this.mOnPageChangeListeners.get(i);
        if (localOnPageChangeListener != null) {
          localOnPageChangeListener.onPageScrollStateChanged(paramInt);
        }
        i++;
      }
    }
    if (this.mInternalPageChangeListener != null) {
      this.mInternalPageChangeListener.onPageScrollStateChanged(paramInt);
    }
  }
  
  private void enableLayers(boolean paramBoolean)
  {
    int i = getChildCount();
    int j = 0;
    if (j < i)
    {
      if (paramBoolean) {}
      for (int k = this.mPageTransformerLayerType;; k = 0)
      {
        getChildAt(j).setLayerType(k, null);
        j++;
        break;
      }
    }
  }
  
  private void endDrag()
  {
    this.mIsBeingDragged = false;
    this.mIsUnableToDrag = false;
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  private Rect getChildRectInPagerCoordinates(Rect paramRect, View paramView)
  {
    Rect localRect = paramRect;
    if (paramRect == null) {
      localRect = new Rect();
    }
    if (paramView == null) {
      localRect.set(0, 0, 0, 0);
    }
    for (;;)
    {
      return localRect;
      localRect.left = paramView.getLeft();
      localRect.right = paramView.getRight();
      localRect.top = paramView.getTop();
      localRect.bottom = paramView.getBottom();
      for (paramRect = paramView.getParent(); ((paramRect instanceof ViewGroup)) && (paramRect != this); paramRect = paramRect.getParent())
      {
        paramRect = (ViewGroup)paramRect;
        localRect.left += paramRect.getLeft();
        localRect.right += paramRect.getRight();
        localRect.top += paramRect.getTop();
        localRect.bottom += paramRect.getBottom();
      }
    }
  }
  
  private int getClientWidth()
  {
    return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
  }
  
  private ItemInfo infoForCurrentScrollPosition()
  {
    float f1 = 0.0F;
    int i = getClientWidth();
    float f2;
    int j;
    float f3;
    float f4;
    int k;
    Object localObject1;
    if (i > 0)
    {
      f2 = getScrollX() / i;
      if (i > 0) {
        f1 = this.mPageMargin / i;
      }
      j = -1;
      f3 = 0.0F;
      f4 = 0.0F;
      k = 1;
      localObject1 = null;
      i = 0;
    }
    for (;;)
    {
      Object localObject2 = localObject1;
      int m;
      Object localObject3;
      if (i < this.mItems.size())
      {
        localObject2 = (ItemInfo)this.mItems.get(i);
        m = i;
        localObject3 = localObject2;
        if (k == 0)
        {
          m = i;
          localObject3 = localObject2;
          if (((ItemInfo)localObject2).position != j + 1)
          {
            localObject3 = this.mTempItem;
            ((ItemInfo)localObject3).offset = (f3 + f4 + f1);
            ((ItemInfo)localObject3).position = (j + 1);
            ((ItemInfo)localObject3).widthFactor = this.mAdapter.getPageWidth(((ItemInfo)localObject3).position);
            m = i - 1;
          }
        }
        f3 = ((ItemInfo)localObject3).offset;
        f4 = ((ItemInfo)localObject3).widthFactor;
        if (k == 0)
        {
          localObject2 = localObject1;
          if (f2 < f3) {}
        }
        else
        {
          if ((f2 >= f4 + f3 + f1) && (m != this.mItems.size() - 1)) {
            break label226;
          }
          localObject2 = localObject3;
        }
      }
      return (ItemInfo)localObject2;
      f2 = 0.0F;
      break;
      label226:
      k = 0;
      j = ((ItemInfo)localObject3).position;
      f4 = ((ItemInfo)localObject3).widthFactor;
      i = m + 1;
      localObject1 = localObject3;
    }
  }
  
  private static boolean isDecorView(View paramView)
  {
    if (paramView.getClass().getAnnotation(DecorView.class) != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean isGutterDrag(float paramFloat1, float paramFloat2)
  {
    if (((paramFloat1 < this.mGutterSize) && (paramFloat2 > 0.0F)) || ((paramFloat1 > getWidth() - this.mGutterSize) && (paramFloat2 < 0.0F))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionIndex();
    if (paramMotionEvent.getPointerId(i) == this.mActivePointerId) {
      if (i != 0) {
        break label56;
      }
    }
    label56:
    for (i = 1;; i = 0)
    {
      this.mLastMotionX = paramMotionEvent.getX(i);
      this.mActivePointerId = paramMotionEvent.getPointerId(i);
      if (this.mVelocityTracker != null) {
        this.mVelocityTracker.clear();
      }
      return;
    }
  }
  
  private boolean pageScrolled(int paramInt)
  {
    boolean bool = false;
    if (this.mItems.size() == 0) {
      if (!this.mFirstLayout) {}
    }
    for (;;)
    {
      return bool;
      this.mCalledSuper = false;
      onPageScrolled(0, 0.0F, 0);
      if (!this.mCalledSuper)
      {
        throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        ItemInfo localItemInfo = infoForCurrentScrollPosition();
        int i = getClientWidth();
        int j = this.mPageMargin;
        float f = this.mPageMargin / i;
        int k = localItemInfo.position;
        f = (paramInt / i - localItemInfo.offset) / (localItemInfo.widthFactor + f);
        paramInt = (int)((i + j) * f);
        this.mCalledSuper = false;
        onPageScrolled(k, f, paramInt);
        if (!this.mCalledSuper) {
          throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        }
        bool = true;
      }
    }
  }
  
  private boolean performDrag(float paramFloat)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    float f1 = this.mLastMotionX;
    this.mLastMotionX = paramFloat;
    float f2 = getScrollX() + (f1 - paramFloat);
    int i = getClientWidth();
    paramFloat = i * this.mFirstOffset;
    f1 = i * this.mLastOffset;
    int j = 1;
    int k = 1;
    ItemInfo localItemInfo1 = (ItemInfo)this.mItems.get(0);
    ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
    if (localItemInfo1.position != 0)
    {
      j = 0;
      paramFloat = localItemInfo1.offset * i;
    }
    if (localItemInfo2.position != this.mAdapter.getCount() - 1)
    {
      k = 0;
      f1 = localItemInfo2.offset * i;
    }
    if (f2 < paramFloat) {
      if (j != 0)
      {
        this.mLeftEdge.onPull(Math.abs(paramFloat - f2) / i);
        bool3 = true;
      }
    }
    for (;;)
    {
      this.mLastMotionX += paramFloat - (int)paramFloat;
      scrollTo((int)paramFloat, getScrollY());
      pageScrolled((int)paramFloat);
      return bool3;
      bool3 = bool1;
      paramFloat = f2;
      if (f2 > f1)
      {
        bool3 = bool2;
        if (k != 0)
        {
          this.mRightEdge.onPull(Math.abs(f2 - f1) / i);
          bool3 = true;
        }
        paramFloat = f1;
      }
    }
  }
  
  private void recomputeScrollPosition(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt2 > 0) && (!this.mItems.isEmpty()))
    {
      if (!this.mScroller.isFinished()) {
        this.mScroller.setFinalX(getCurrentItem() * getClientWidth());
      }
      for (;;)
      {
        return;
        int i = getPaddingLeft();
        int j = getPaddingRight();
        int k = getPaddingLeft();
        int m = getPaddingRight();
        f = getScrollX() / (paramInt2 - k - m + paramInt4);
        scrollTo((int)((paramInt1 - i - j + paramInt3) * f), getScrollY());
      }
    }
    ItemInfo localItemInfo = infoForPosition(this.mCurItem);
    if (localItemInfo != null) {}
    for (float f = Math.min(localItemInfo.offset, this.mLastOffset);; f = 0.0F)
    {
      paramInt1 = (int)((paramInt1 - getPaddingLeft() - getPaddingRight()) * f);
      if (paramInt1 == getScrollX()) {
        break;
      }
      completeScroll(false);
      scrollTo(paramInt1, getScrollY());
      break;
    }
  }
  
  private void removeNonDecorViews()
  {
    int j;
    for (int i = 0; i < getChildCount(); i = j + 1)
    {
      j = i;
      if (!((LayoutParams)getChildAt(i).getLayoutParams()).isDecor)
      {
        removeViewAt(i);
        j = i - 1;
      }
    }
  }
  
  private void requestParentDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    ViewParent localViewParent = getParent();
    if (localViewParent != null) {
      localViewParent.requestDisallowInterceptTouchEvent(paramBoolean);
    }
  }
  
  private boolean resetTouch()
  {
    this.mActivePointerId = -1;
    endDrag();
    this.mLeftEdge.onRelease();
    this.mRightEdge.onRelease();
    if ((this.mLeftEdge.isFinished()) || (this.mRightEdge.isFinished())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void scrollToItem(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2)
  {
    ItemInfo localItemInfo = infoForPosition(paramInt1);
    int i = 0;
    if (localItemInfo != null) {
      i = (int)(getClientWidth() * Math.max(this.mFirstOffset, Math.min(localItemInfo.offset, this.mLastOffset)));
    }
    if (paramBoolean1)
    {
      smoothScrollTo(i, 0, paramInt2);
      if (paramBoolean2) {
        dispatchOnPageSelected(paramInt1);
      }
    }
    for (;;)
    {
      return;
      if (paramBoolean2) {
        dispatchOnPageSelected(paramInt1);
      }
      completeScroll(false);
      scrollTo(i, 0);
      pageScrolled(i);
    }
  }
  
  private void setScrollingCacheEnabled(boolean paramBoolean)
  {
    if (this.mScrollingCacheEnabled != paramBoolean) {
      this.mScrollingCacheEnabled = paramBoolean;
    }
  }
  
  private void sortChildDrawingOrder()
  {
    if (this.mDrawingOrder != 0)
    {
      if (this.mDrawingOrderedChildren == null) {
        this.mDrawingOrderedChildren = new ArrayList();
      }
      for (;;)
      {
        int i = getChildCount();
        for (int j = 0; j < i; j++)
        {
          View localView = getChildAt(j);
          this.mDrawingOrderedChildren.add(localView);
        }
        this.mDrawingOrderedChildren.clear();
      }
      Collections.sort(this.mDrawingOrderedChildren, sPositionComparator);
    }
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    int i = paramArrayList.size();
    int j = getDescendantFocusability();
    if (j != 393216) {
      for (int k = 0; k < getChildCount(); k++)
      {
        View localView = getChildAt(k);
        if (localView.getVisibility() == 0)
        {
          ItemInfo localItemInfo = infoForChild(localView);
          if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem)) {
            localView.addFocusables(paramArrayList, paramInt1, paramInt2);
          }
        }
      }
    }
    if (((j == 262144) && (i != paramArrayList.size())) || (!isFocusable())) {}
    for (;;)
    {
      return;
      if ((((paramInt2 & 0x1) != 1) || (!isInTouchMode()) || (isFocusableInTouchMode())) && (paramArrayList != null)) {
        paramArrayList.add(this);
      }
    }
  }
  
  ItemInfo addNewItem(int paramInt1, int paramInt2)
  {
    ItemInfo localItemInfo = new ItemInfo();
    localItemInfo.position = paramInt1;
    localItemInfo.object = this.mAdapter.instantiateItem(this, paramInt1);
    localItemInfo.widthFactor = this.mAdapter.getPageWidth(paramInt1);
    if ((paramInt2 < 0) || (paramInt2 >= this.mItems.size())) {
      this.mItems.add(localItemInfo);
    }
    for (;;)
    {
      return localItemInfo;
      this.mItems.add(paramInt2, localItemInfo);
    }
  }
  
  public void addOnAdapterChangeListener(OnAdapterChangeListener paramOnAdapterChangeListener)
  {
    if (this.mAdapterChangeListeners == null) {
      this.mAdapterChangeListeners = new ArrayList();
    }
    this.mAdapterChangeListeners.add(paramOnAdapterChangeListener);
  }
  
  public void addOnPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    if (this.mOnPageChangeListeners == null) {
      this.mOnPageChangeListeners = new ArrayList();
    }
    this.mOnPageChangeListeners.add(paramOnPageChangeListener);
  }
  
  public void addTouchables(ArrayList<View> paramArrayList)
  {
    for (int i = 0; i < getChildCount(); i++)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() == 0)
      {
        ItemInfo localItemInfo = infoForChild(localView);
        if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem)) {
          localView.addTouchables(paramArrayList);
        }
      }
    }
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    ViewGroup.LayoutParams localLayoutParams = paramLayoutParams;
    if (!checkLayoutParams(paramLayoutParams)) {
      localLayoutParams = generateLayoutParams(paramLayoutParams);
    }
    paramLayoutParams = (LayoutParams)localLayoutParams;
    paramLayoutParams.isDecor |= isDecorView(paramView);
    if (this.mInLayout)
    {
      if ((paramLayoutParams != null) && (paramLayoutParams.isDecor)) {
        throw new IllegalStateException("Cannot add pager decor view during layout");
      }
      paramLayoutParams.needsMeasure = true;
      addViewInLayout(paramView, paramInt, localLayoutParams);
    }
    for (;;)
    {
      return;
      super.addView(paramView, paramInt, localLayoutParams);
    }
  }
  
  public boolean arrowScroll(int paramInt)
  {
    View localView = findFocus();
    Object localObject;
    boolean bool;
    int i;
    int j;
    if (localView == this)
    {
      localObject = null;
      bool = false;
      localView = FocusFinder.getInstance().findNextFocus(this, (View)localObject, paramInt);
      if ((localView == null) || (localView == localObject)) {
        break label321;
      }
      if (paramInt != 17) {
        break label258;
      }
      i = getChildRectInPagerCoordinates(this.mTempRect, localView).left;
      j = getChildRectInPagerCoordinates(this.mTempRect, (View)localObject).left;
      if ((localObject == null) || (i < j)) {
        break label249;
      }
      bool = pageLeft();
    }
    for (;;)
    {
      if (bool) {
        playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
      }
      return bool;
      localObject = localView;
      if (localView == null) {
        break;
      }
      j = 0;
      StringBuilder localStringBuilder;
      for (localObject = localView.getParent();; localObject = ((ViewParent)localObject).getParent())
      {
        i = j;
        if ((localObject instanceof ViewGroup))
        {
          if (localObject == this) {
            i = 1;
          }
        }
        else
        {
          localObject = localView;
          if (i != 0) {
            break;
          }
          localStringBuilder = new StringBuilder();
          localStringBuilder.append(localView.getClass().getSimpleName());
          for (localObject = localView.getParent(); (localObject instanceof ViewGroup); localObject = ((ViewParent)localObject).getParent()) {
            localStringBuilder.append(" => ").append(localObject.getClass().getSimpleName());
          }
        }
      }
      Log.e("ViewPager", "arrowScroll tried to find focus based on non-child current focused view " + localStringBuilder.toString());
      localObject = null;
      break;
      label249:
      bool = localView.requestFocus();
      continue;
      label258:
      if (paramInt == 66)
      {
        i = getChildRectInPagerCoordinates(this.mTempRect, localView).left;
        j = getChildRectInPagerCoordinates(this.mTempRect, (View)localObject).left;
        if ((localObject != null) && (i <= j))
        {
          bool = pageRight();
        }
        else
        {
          bool = localView.requestFocus();
          continue;
          label321:
          if ((paramInt == 17) || (paramInt == 1)) {
            bool = pageLeft();
          } else if ((paramInt == 66) || (paramInt == 2)) {
            bool = pageRight();
          }
        }
      }
    }
  }
  
  public boolean beginFakeDrag()
  {
    boolean bool = false;
    if (this.mIsBeingDragged) {
      return bool;
    }
    this.mFakeDragging = true;
    setScrollState(1);
    this.mLastMotionX = 0.0F;
    this.mInitialMotionX = 0.0F;
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    for (;;)
    {
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 0, 0.0F, 0.0F, 0);
      this.mVelocityTracker.addMovement(localMotionEvent);
      localMotionEvent.recycle();
      this.mFakeDragBeginTime = l;
      bool = true;
      break;
      this.mVelocityTracker.clear();
    }
  }
  
  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    int k;
    if ((paramView instanceof ViewGroup))
    {
      ViewGroup localViewGroup = (ViewGroup)paramView;
      int i = paramView.getScrollX();
      int j = paramView.getScrollY();
      k = localViewGroup.getChildCount() - 1;
      if (k >= 0)
      {
        View localView = localViewGroup.getChildAt(k);
        if ((paramInt2 + i >= localView.getLeft()) && (paramInt2 + i < localView.getRight()) && (paramInt3 + j >= localView.getTop()) && (paramInt3 + j < localView.getBottom()) && (canScroll(localView, true, paramInt1, paramInt2 + i - localView.getLeft(), paramInt3 + j - localView.getTop()))) {
          paramBoolean = true;
        }
      }
    }
    for (;;)
    {
      return paramBoolean;
      k--;
      break;
      if ((paramBoolean) && (paramView.canScrollHorizontally(-paramInt1))) {
        paramBoolean = true;
      } else {
        paramBoolean = false;
      }
    }
  }
  
  public boolean canScrollHorizontally(int paramInt)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    boolean bool3 = false;
    if (this.mAdapter == null) {}
    int i;
    int j;
    do
    {
      return bool3;
      i = getClientWidth();
      j = getScrollX();
      if (paramInt < 0)
      {
        if (j > (int)(i * this.mFirstOffset)) {}
        for (bool3 = bool2;; bool3 = false) {
          break;
        }
      }
    } while (paramInt <= 0);
    if (j < (int)(i * this.mLastOffset)) {}
    for (bool3 = bool1;; bool3 = false) {
      break;
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (((paramLayoutParams instanceof LayoutParams)) && (super.checkLayoutParams(paramLayoutParams))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void clearOnPageChangeListeners()
  {
    if (this.mOnPageChangeListeners != null) {
      this.mOnPageChangeListeners.clear();
    }
  }
  
  public void computeScroll()
  {
    this.mIsScrollStarted = true;
    if ((!this.mScroller.isFinished()) && (this.mScroller.computeScrollOffset()))
    {
      int i = getScrollX();
      int j = getScrollY();
      int k = this.mScroller.getCurrX();
      int m = this.mScroller.getCurrY();
      if ((i != k) || (j != m))
      {
        scrollTo(k, m);
        if (!pageScrolled(k))
        {
          this.mScroller.abortAnimation();
          scrollTo(0, m);
        }
      }
      ViewCompat.postInvalidateOnAnimation(this);
    }
    for (;;)
    {
      return;
      completeScroll(true);
    }
  }
  
  void dataSetChanged()
  {
    int i = this.mAdapter.getCount();
    this.mExpectedAdapterCount = i;
    int j;
    int k;
    int m;
    int n;
    label55:
    Object localObject;
    int i1;
    int i2;
    int i3;
    int i4;
    if ((this.mItems.size() < this.mOffscreenPageLimit * 2 + 1) && (this.mItems.size() < i))
    {
      j = 1;
      k = this.mCurItem;
      m = 0;
      n = 0;
      if (n >= this.mItems.size()) {
        break label307;
      }
      localObject = (ItemInfo)this.mItems.get(n);
      i1 = this.mAdapter.getItemPosition(((ItemInfo)localObject).object);
      if (i1 != -1) {
        break label133;
      }
      i2 = k;
      i3 = m;
      i4 = n;
    }
    for (;;)
    {
      n = i4 + 1;
      m = i3;
      k = i2;
      break label55;
      j = 0;
      break;
      label133:
      if (i1 == -2)
      {
        this.mItems.remove(n);
        i1 = n - 1;
        n = m;
        if (m == 0)
        {
          this.mAdapter.startUpdate(this);
          n = 1;
        }
        this.mAdapter.destroyItem(this, ((ItemInfo)localObject).position, ((ItemInfo)localObject).object);
        j = 1;
        i4 = i1;
        i3 = n;
        i2 = k;
        if (this.mCurItem == ((ItemInfo)localObject).position)
        {
          i2 = Math.max(0, Math.min(this.mCurItem, i - 1));
          j = 1;
          i4 = i1;
          i3 = n;
        }
      }
      else
      {
        i4 = n;
        i3 = m;
        i2 = k;
        if (((ItemInfo)localObject).position != i1)
        {
          if (((ItemInfo)localObject).position == this.mCurItem) {
            k = i1;
          }
          ((ItemInfo)localObject).position = i1;
          j = 1;
          i4 = n;
          i3 = m;
          i2 = k;
        }
      }
    }
    label307:
    if (m != 0) {
      this.mAdapter.finishUpdate(this);
    }
    Collections.sort(this.mItems, COMPARATOR);
    if (j != 0)
    {
      m = getChildCount();
      for (j = 0; j < m; j++)
      {
        localObject = (LayoutParams)getChildAt(j).getLayoutParams();
        if (!((LayoutParams)localObject).isDecor) {
          ((LayoutParams)localObject).widthFactor = 0.0F;
        }
      }
      setCurrentItemInternal(k, false, true);
      requestLayout();
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((super.dispatchKeyEvent(paramKeyEvent)) || (executeKeyEvent(paramKeyEvent))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    boolean bool;
    if (paramAccessibilityEvent.getEventType() == 4096) {
      bool = super.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent);
    }
    for (;;)
    {
      return bool;
      int i = getChildCount();
      for (int j = 0;; j++)
      {
        if (j >= i) {
          break label93;
        }
        View localView = getChildAt(j);
        if (localView.getVisibility() == 0)
        {
          ItemInfo localItemInfo = infoForChild(localView);
          if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem) && (localView.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent)))
          {
            bool = true;
            break;
          }
        }
      }
      label93:
      bool = false;
    }
  }
  
  float distanceInfluenceForSnapDuration(float paramFloat)
  {
    return (float)Math.sin((paramFloat - 0.5F) * 0.47123894F);
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    int i = 0;
    int j = 0;
    int m = getOverScrollMode();
    boolean bool;
    if ((m == 0) || ((m == 1) && (this.mAdapter != null) && (this.mAdapter.getCount() > 1)))
    {
      int k;
      if (!this.mLeftEdge.isFinished())
      {
        i = paramCanvas.save();
        m = getHeight() - getPaddingTop() - getPaddingBottom();
        j = getWidth();
        paramCanvas.rotate(270.0F);
        paramCanvas.translate(-m + getPaddingTop(), this.mFirstOffset * j);
        this.mLeftEdge.setSize(m, j);
        k = false | this.mLeftEdge.draw(paramCanvas);
        paramCanvas.restoreToCount(i);
      }
      i = k;
      if (!this.mRightEdge.isFinished())
      {
        m = paramCanvas.save();
        int n = getWidth();
        int i1 = getHeight();
        int i2 = getPaddingTop();
        i = getPaddingBottom();
        paramCanvas.rotate(90.0F);
        paramCanvas.translate(-getPaddingTop(), -(this.mLastOffset + 1.0F) * n);
        this.mRightEdge.setSize(i1 - i2 - i, n);
        bool = k | this.mRightEdge.draw(paramCanvas);
        paramCanvas.restoreToCount(m);
      }
    }
    for (;;)
    {
      if (bool) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return;
      this.mLeftEdge.finish();
      this.mRightEdge.finish();
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = this.mMarginDrawable;
    if ((localDrawable != null) && (localDrawable.isStateful())) {
      localDrawable.setState(getDrawableState());
    }
  }
  
  public void endFakeDrag()
  {
    if (!this.mFakeDragging) {
      throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
    }
    if (this.mAdapter != null)
    {
      Object localObject = this.mVelocityTracker;
      ((VelocityTracker)localObject).computeCurrentVelocity(1000, this.mMaximumVelocity);
      int i = (int)((VelocityTracker)localObject).getXVelocity(this.mActivePointerId);
      this.mPopulatePending = true;
      int j = getClientWidth();
      int k = getScrollX();
      localObject = infoForCurrentScrollPosition();
      setCurrentItemInternal(determineTargetPage(((ItemInfo)localObject).position, (k / j - ((ItemInfo)localObject).offset) / ((ItemInfo)localObject).widthFactor, i, (int)(this.mLastMotionX - this.mInitialMotionX)), true, true, i);
    }
    endDrag();
    this.mFakeDragging = false;
  }
  
  public boolean executeKeyEvent(KeyEvent paramKeyEvent)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (paramKeyEvent.getAction() == 0) {
      switch (paramKeyEvent.getKeyCode())
      {
      default: 
        bool2 = bool1;
      }
    }
    for (;;)
    {
      return bool2;
      if (paramKeyEvent.hasModifiers(2))
      {
        bool2 = pageLeft();
      }
      else
      {
        bool2 = arrowScroll(17);
        continue;
        if (paramKeyEvent.hasModifiers(2))
        {
          bool2 = pageRight();
        }
        else
        {
          bool2 = arrowScroll(66);
          continue;
          if (paramKeyEvent.hasNoModifiers())
          {
            bool2 = arrowScroll(2);
          }
          else
          {
            bool2 = bool1;
            if (paramKeyEvent.hasModifiers(1)) {
              bool2 = arrowScroll(1);
            }
          }
        }
      }
    }
  }
  
  public void fakeDragBy(float paramFloat)
  {
    if (!this.mFakeDragging) {
      throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
    }
    if (this.mAdapter == null) {
      return;
    }
    this.mLastMotionX += paramFloat;
    float f1 = getScrollX() - paramFloat;
    int i = getClientWidth();
    paramFloat = i * this.mFirstOffset;
    float f2 = i * this.mLastOffset;
    ItemInfo localItemInfo = (ItemInfo)this.mItems.get(0);
    Object localObject = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
    if (localItemInfo.position != 0) {
      paramFloat = localItemInfo.offset * i;
    }
    if (((ItemInfo)localObject).position != this.mAdapter.getCount() - 1) {
      f2 = ((ItemInfo)localObject).offset * i;
    }
    if (f1 < paramFloat) {}
    for (;;)
    {
      this.mLastMotionX += paramFloat - (int)paramFloat;
      scrollTo((int)paramFloat, getScrollY());
      pageScrolled((int)paramFloat);
      long l = SystemClock.uptimeMillis();
      localObject = MotionEvent.obtain(this.mFakeDragBeginTime, l, 2, this.mLastMotionX, 0.0F, 0);
      this.mVelocityTracker.addMovement((MotionEvent)localObject);
      ((MotionEvent)localObject).recycle();
      break;
      paramFloat = f1;
      if (f1 > f2) {
        paramFloat = f2;
      }
    }
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams();
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return generateDefaultLayoutParams();
  }
  
  public PagerAdapter getAdapter()
  {
    return this.mAdapter;
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (this.mDrawingOrder == 2) {}
    for (paramInt1 = paramInt1 - 1 - paramInt2;; paramInt1 = paramInt2) {
      return ((LayoutParams)((View)this.mDrawingOrderedChildren.get(paramInt1)).getLayoutParams()).childIndex;
    }
  }
  
  public int getCurrentItem()
  {
    return this.mCurItem;
  }
  
  public int getOffscreenPageLimit()
  {
    return this.mOffscreenPageLimit;
  }
  
  public int getPageMargin()
  {
    return this.mPageMargin;
  }
  
  ItemInfo infoForAnyChild(View paramView)
  {
    ViewParent localViewParent = paramView.getParent();
    if (localViewParent != this) {
      if ((localViewParent != null) && ((localViewParent instanceof View))) {}
    }
    for (paramView = null;; paramView = infoForChild(paramView))
    {
      return paramView;
      paramView = (View)localViewParent;
      break;
    }
  }
  
  ItemInfo infoForChild(View paramView)
  {
    int i = 0;
    ItemInfo localItemInfo;
    if (i < this.mItems.size())
    {
      localItemInfo = (ItemInfo)this.mItems.get(i);
      if (!this.mAdapter.isViewFromObject(paramView, localItemInfo.object)) {}
    }
    for (paramView = localItemInfo;; paramView = null)
    {
      return paramView;
      i++;
      break;
    }
  }
  
  ItemInfo infoForPosition(int paramInt)
  {
    int i = 0;
    ItemInfo localItemInfo;
    if (i < this.mItems.size())
    {
      localItemInfo = (ItemInfo)this.mItems.get(i);
      if (localItemInfo.position != paramInt) {}
    }
    for (;;)
    {
      return localItemInfo;
      i++;
      break;
      localItemInfo = null;
    }
  }
  
  void initViewPager()
  {
    setWillNotDraw(false);
    setDescendantFocusability(262144);
    setFocusable(true);
    Context localContext = getContext();
    this.mScroller = new Scroller(localContext, sInterpolator);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(localContext);
    float f = localContext.getResources().getDisplayMetrics().density;
    this.mTouchSlop = localViewConfiguration.getScaledPagingTouchSlop();
    this.mMinimumVelocity = ((int)(400.0F * f));
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
    this.mLeftEdge = new EdgeEffect(localContext);
    this.mRightEdge = new EdgeEffect(localContext);
    this.mFlingDistance = ((int)(25.0F * f));
    this.mCloseEnough = ((int)(2.0F * f));
    this.mDefaultGutterSize = ((int)(16.0F * f));
    ViewCompat.setAccessibilityDelegate(this, new MyAccessibilityDelegate());
    if (ViewCompat.getImportantForAccessibility(this) == 0) {
      ViewCompat.setImportantForAccessibility(this, 1);
    }
    ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener()
    {
      private final Rect mTempRect = new Rect();
      
      public WindowInsetsCompat onApplyWindowInsets(View paramAnonymousView, WindowInsetsCompat paramAnonymousWindowInsetsCompat)
      {
        paramAnonymousView = ViewCompat.onApplyWindowInsets(paramAnonymousView, paramAnonymousWindowInsetsCompat);
        if (paramAnonymousView.isConsumed()) {}
        for (;;)
        {
          return paramAnonymousView;
          paramAnonymousWindowInsetsCompat = this.mTempRect;
          paramAnonymousWindowInsetsCompat.left = paramAnonymousView.getSystemWindowInsetLeft();
          paramAnonymousWindowInsetsCompat.top = paramAnonymousView.getSystemWindowInsetTop();
          paramAnonymousWindowInsetsCompat.right = paramAnonymousView.getSystemWindowInsetRight();
          paramAnonymousWindowInsetsCompat.bottom = paramAnonymousView.getSystemWindowInsetBottom();
          int i = 0;
          int j = ViewPager.this.getChildCount();
          while (i < j)
          {
            WindowInsetsCompat localWindowInsetsCompat = ViewCompat.dispatchApplyWindowInsets(ViewPager.this.getChildAt(i), paramAnonymousView);
            paramAnonymousWindowInsetsCompat.left = Math.min(localWindowInsetsCompat.getSystemWindowInsetLeft(), paramAnonymousWindowInsetsCompat.left);
            paramAnonymousWindowInsetsCompat.top = Math.min(localWindowInsetsCompat.getSystemWindowInsetTop(), paramAnonymousWindowInsetsCompat.top);
            paramAnonymousWindowInsetsCompat.right = Math.min(localWindowInsetsCompat.getSystemWindowInsetRight(), paramAnonymousWindowInsetsCompat.right);
            paramAnonymousWindowInsetsCompat.bottom = Math.min(localWindowInsetsCompat.getSystemWindowInsetBottom(), paramAnonymousWindowInsetsCompat.bottom);
            i++;
          }
          paramAnonymousView = paramAnonymousView.replaceSystemWindowInsets(paramAnonymousWindowInsetsCompat.left, paramAnonymousWindowInsetsCompat.top, paramAnonymousWindowInsetsCompat.right, paramAnonymousWindowInsetsCompat.bottom);
        }
      }
    });
  }
  
  public boolean isFakeDragging()
  {
    return this.mFakeDragging;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow()
  {
    removeCallbacks(this.mEndScrollRunnable);
    if ((this.mScroller != null) && (!this.mScroller.isFinished())) {
      this.mScroller.abortAnimation();
    }
    super.onDetachedFromWindow();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i;
    int j;
    float f1;
    int k;
    Object localObject;
    float f2;
    int m;
    int n;
    int i1;
    if ((this.mPageMargin > 0) && (this.mMarginDrawable != null) && (this.mItems.size() > 0) && (this.mAdapter != null))
    {
      i = getScrollX();
      j = getWidth();
      f1 = this.mPageMargin / j;
      k = 0;
      localObject = (ItemInfo)this.mItems.get(0);
      f2 = ((ItemInfo)localObject).offset;
      m = this.mItems.size();
      n = ((ItemInfo)localObject).position;
      i1 = ((ItemInfo)this.mItems.get(m - 1)).position;
    }
    for (;;)
    {
      float f3;
      if (n < i1)
      {
        while ((n > ((ItemInfo)localObject).position) && (k < m))
        {
          localObject = this.mItems;
          k++;
          localObject = (ItemInfo)((ArrayList)localObject).get(k);
        }
        if (n != ((ItemInfo)localObject).position) {
          break label268;
        }
        f3 = (((ItemInfo)localObject).offset + ((ItemInfo)localObject).widthFactor) * j;
      }
      label268:
      float f4;
      for (f2 = ((ItemInfo)localObject).offset + ((ItemInfo)localObject).widthFactor + f1;; f2 += f4 + f1)
      {
        if (this.mPageMargin + f3 > i)
        {
          this.mMarginDrawable.setBounds(Math.round(f3), this.mTopPageBounds, Math.round(this.mPageMargin + f3), this.mBottomPageBounds);
          this.mMarginDrawable.draw(paramCanvas);
        }
        if (f3 <= i + j) {
          break;
        }
        return;
        f4 = this.mAdapter.getPageWidth(n);
        f3 = (f2 + f4) * j;
      }
      n++;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction() & 0xFF;
    boolean bool;
    if ((i == 3) || (i == 1))
    {
      resetTouch();
      bool = false;
    }
    for (;;)
    {
      return bool;
      if (i == 0) {
        break;
      }
      if (this.mIsBeingDragged)
      {
        bool = true;
      }
      else
      {
        if (!this.mIsUnableToDrag) {
          break;
        }
        bool = false;
      }
    }
    switch (i)
    {
    }
    for (;;)
    {
      if (this.mVelocityTracker == null) {
        this.mVelocityTracker = VelocityTracker.obtain();
      }
      this.mVelocityTracker.addMovement(paramMotionEvent);
      bool = this.mIsBeingDragged;
      break;
      i = this.mActivePointerId;
      if (i != -1)
      {
        i = paramMotionEvent.findPointerIndex(i);
        float f1 = paramMotionEvent.getX(i);
        float f2 = f1 - this.mLastMotionX;
        float f3 = Math.abs(f2);
        float f4 = paramMotionEvent.getY(i);
        float f5 = Math.abs(f4 - this.mInitialMotionY);
        if ((f2 != 0.0F) && (!isGutterDrag(this.mLastMotionX, f2)) && (canScroll(this, false, (int)f2, (int)f1, (int)f4)))
        {
          this.mLastMotionX = f1;
          this.mLastMotionY = f4;
          this.mIsUnableToDrag = true;
          bool = false;
          break;
        }
        if ((f3 > this.mTouchSlop) && (0.5F * f3 > f5))
        {
          this.mIsBeingDragged = true;
          requestParentDisallowInterceptTouchEvent(true);
          setScrollState(1);
          if (f2 > 0.0F)
          {
            f2 = this.mInitialMotionX + this.mTouchSlop;
            label297:
            this.mLastMotionX = f2;
            this.mLastMotionY = f4;
            setScrollingCacheEnabled(true);
          }
        }
        while ((this.mIsBeingDragged) && (performDrag(f1)))
        {
          ViewCompat.postInvalidateOnAnimation(this);
          break;
          f2 = this.mInitialMotionX - this.mTouchSlop;
          break label297;
          if (f5 > this.mTouchSlop) {
            this.mIsUnableToDrag = true;
          }
        }
        f2 = paramMotionEvent.getX();
        this.mInitialMotionX = f2;
        this.mLastMotionX = f2;
        f2 = paramMotionEvent.getY();
        this.mInitialMotionY = f2;
        this.mLastMotionY = f2;
        this.mActivePointerId = paramMotionEvent.getPointerId(0);
        this.mIsUnableToDrag = false;
        this.mIsScrollStarted = true;
        this.mScroller.computeScrollOffset();
        if ((this.mScrollState == 2) && (Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) > this.mCloseEnough))
        {
          this.mScroller.abortAnimation();
          this.mPopulatePending = false;
          populate();
          this.mIsBeingDragged = true;
          requestParentDisallowInterceptTouchEvent(true);
          setScrollState(1);
        }
        else
        {
          completeScroll(false);
          this.mIsBeingDragged = false;
          continue;
          onSecondaryPointerUp(paramMotionEvent);
        }
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getChildCount();
    int j = paramInt3 - paramInt1;
    int k = paramInt4 - paramInt2;
    paramInt2 = getPaddingLeft();
    paramInt1 = getPaddingTop();
    int m = getPaddingRight();
    paramInt4 = getPaddingBottom();
    int n = getScrollX();
    int i1 = 0;
    int i2 = 0;
    View localView;
    Object localObject;
    if (i2 < i)
    {
      localView = getChildAt(i2);
      int i3 = i1;
      int i4 = paramInt4;
      i5 = paramInt2;
      int i6 = m;
      paramInt3 = paramInt1;
      if (localView.getVisibility() != 8)
      {
        localObject = (LayoutParams)localView.getLayoutParams();
        i3 = i1;
        i4 = paramInt4;
        i5 = paramInt2;
        i6 = m;
        paramInt3 = paramInt1;
        if (((LayoutParams)localObject).isDecor)
        {
          paramInt3 = ((LayoutParams)localObject).gravity;
          i6 = ((LayoutParams)localObject).gravity;
          switch (paramInt3 & 0x7)
          {
          case 2: 
          case 4: 
          default: 
            paramInt3 = paramInt2;
            i5 = paramInt2;
            label190:
            switch (i6 & 0x70)
            {
            default: 
              paramInt2 = paramInt1;
            }
            break;
          }
        }
      }
      for (;;)
      {
        paramInt3 += n;
        localView.layout(paramInt3, paramInt2, localView.getMeasuredWidth() + paramInt3, localView.getMeasuredHeight() + paramInt2);
        i3 = i1 + 1;
        paramInt3 = paramInt1;
        i6 = m;
        i4 = paramInt4;
        i2++;
        i1 = i3;
        paramInt4 = i4;
        paramInt2 = i5;
        m = i6;
        paramInt1 = paramInt3;
        break;
        paramInt3 = paramInt2;
        i5 = paramInt2 + localView.getMeasuredWidth();
        break label190;
        paramInt3 = Math.max((j - localView.getMeasuredWidth()) / 2, paramInt2);
        i5 = paramInt2;
        break label190;
        paramInt3 = j - m - localView.getMeasuredWidth();
        m += localView.getMeasuredWidth();
        i5 = paramInt2;
        break label190;
        paramInt2 = paramInt1;
        paramInt1 += localView.getMeasuredHeight();
        continue;
        paramInt2 = Math.max((k - localView.getMeasuredHeight()) / 2, paramInt1);
        continue;
        paramInt2 = k - paramInt4 - localView.getMeasuredHeight();
        paramInt4 += localView.getMeasuredHeight();
      }
    }
    int i5 = j - paramInt2 - m;
    for (paramInt3 = 0; paramInt3 < i; paramInt3++)
    {
      localView = getChildAt(paramInt3);
      if (localView.getVisibility() != 8)
      {
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (!localLayoutParams.isDecor)
        {
          localObject = infoForChild(localView);
          if (localObject != null)
          {
            m = paramInt2 + (int)(i5 * ((ItemInfo)localObject).offset);
            if (localLayoutParams.needsMeasure)
            {
              localLayoutParams.needsMeasure = false;
              localView.measure(View.MeasureSpec.makeMeasureSpec((int)(i5 * localLayoutParams.widthFactor), NUM), View.MeasureSpec.makeMeasureSpec(k - paramInt1 - paramInt4, NUM));
            }
            localView.layout(m, paramInt1, localView.getMeasuredWidth() + m, localView.getMeasuredHeight() + paramInt1);
          }
        }
      }
    }
    this.mTopPageBounds = paramInt1;
    this.mBottomPageBounds = (k - paramInt4);
    this.mDecorChildCount = i1;
    if (this.mFirstLayout) {
      scrollToItem(this.mCurItem, false, 0, false);
    }
    this.mFirstLayout = false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(getDefaultSize(0, paramInt1), getDefaultSize(0, paramInt2));
    paramInt1 = getMeasuredWidth();
    this.mGutterSize = Math.min(paramInt1 / 10, this.mDefaultGutterSize);
    paramInt1 = paramInt1 - getPaddingLeft() - getPaddingRight();
    paramInt2 = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    int i = getChildCount();
    int j = 0;
    Object localObject1;
    Object localObject2;
    if (j < i)
    {
      localObject1 = getChildAt(j);
      k = paramInt2;
      int m = paramInt1;
      int n;
      int i1;
      label181:
      int i2;
      if (((View)localObject1).getVisibility() != 8)
      {
        localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
        k = paramInt2;
        m = paramInt1;
        if (localObject2 != null)
        {
          k = paramInt2;
          m = paramInt1;
          if (((LayoutParams)localObject2).isDecor)
          {
            m = ((LayoutParams)localObject2).gravity & 0x7;
            n = ((LayoutParams)localObject2).gravity & 0x70;
            i1 = Integer.MIN_VALUE;
            k = Integer.MIN_VALUE;
            if ((n != 48) && (n != 80)) {
              break label354;
            }
            n = 1;
            if ((m != 3) && (m != 5)) {
              break label360;
            }
            i2 = 1;
            label196:
            if (n == 0) {
              break label366;
            }
            m = NUM;
            label206:
            int i3 = paramInt1;
            i1 = paramInt2;
            int i4 = i3;
            int i5;
            if (((LayoutParams)localObject2).width != -2)
            {
              i5 = NUM;
              m = i5;
              i4 = i3;
              if (((LayoutParams)localObject2).width != -1)
              {
                i4 = ((LayoutParams)localObject2).width;
                m = i5;
              }
            }
            i3 = i1;
            if (((LayoutParams)localObject2).height != -2)
            {
              i5 = NUM;
              k = i5;
              i3 = i1;
              if (((LayoutParams)localObject2).height != -1)
              {
                i3 = ((LayoutParams)localObject2).height;
                k = i5;
              }
            }
            ((View)localObject1).measure(View.MeasureSpec.makeMeasureSpec(i4, m), View.MeasureSpec.makeMeasureSpec(i3, k));
            if (n == 0) {
              break label387;
            }
            k = paramInt2 - ((View)localObject1).getMeasuredHeight();
            m = paramInt1;
          }
        }
      }
      for (;;)
      {
        j++;
        paramInt2 = k;
        paramInt1 = m;
        break;
        label354:
        n = 0;
        break label181;
        label360:
        i2 = 0;
        break label196;
        label366:
        m = i1;
        if (i2 == 0) {
          break label206;
        }
        k = NUM;
        m = i1;
        break label206;
        label387:
        k = paramInt2;
        m = paramInt1;
        if (i2 != 0)
        {
          m = paramInt1 - ((View)localObject1).getMeasuredWidth();
          k = paramInt2;
        }
      }
    }
    this.mChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(paramInt1, NUM);
    this.mChildHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(paramInt2, NUM);
    this.mInLayout = true;
    populate();
    this.mInLayout = false;
    int k = getChildCount();
    for (paramInt2 = 0; paramInt2 < k; paramInt2++)
    {
      localObject2 = getChildAt(paramInt2);
      if (((View)localObject2).getVisibility() != 8)
      {
        localObject1 = (LayoutParams)((View)localObject2).getLayoutParams();
        if ((localObject1 == null) || (!((LayoutParams)localObject1).isDecor)) {
          ((View)localObject2).measure(View.MeasureSpec.makeMeasureSpec((int)(paramInt1 * ((LayoutParams)localObject1).widthFactor), NUM), this.mChildHeightMeasureSpec);
        }
      }
    }
  }
  
  protected void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
    int j;
    View localView;
    if (this.mDecorChildCount > 0)
    {
      int i = getScrollX();
      j = getPaddingLeft();
      int k = getPaddingRight();
      int m = getWidth();
      int n = getChildCount();
      int i1 = 0;
      while (i1 < n)
      {
        localView = getChildAt(i1);
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        int i2;
        int i3;
        if (!localLayoutParams.isDecor)
        {
          i2 = k;
          i3 = j;
          i1++;
          j = i3;
          k = i2;
        }
        else
        {
          switch (localLayoutParams.gravity & 0x7)
          {
          case 2: 
          case 4: 
          default: 
            i2 = j;
          }
          for (;;)
          {
            int i4 = i2 + i - localView.getLeft();
            i3 = j;
            i2 = k;
            if (i4 == 0) {
              break;
            }
            localView.offsetLeftAndRight(i4);
            i3 = j;
            i2 = k;
            break;
            i2 = j;
            j += localView.getWidth();
            continue;
            i2 = Math.max((m - localView.getMeasuredWidth()) / 2, j);
            continue;
            i2 = m - k - localView.getMeasuredWidth();
            k += localView.getMeasuredWidth();
          }
        }
      }
    }
    dispatchOnPageScrolled(paramInt1, paramFloat, paramInt2);
    if (this.mPageTransformer != null)
    {
      paramInt2 = getScrollX();
      j = getChildCount();
      paramInt1 = 0;
      if (paramInt1 < j)
      {
        localView = getChildAt(paramInt1);
        if (((LayoutParams)localView.getLayoutParams()).isDecor) {}
        for (;;)
        {
          paramInt1++;
          break;
          paramFloat = (localView.getLeft() - paramInt2) / getClientWidth();
          this.mPageTransformer.transformPage(localView, paramFloat);
        }
      }
    }
    this.mCalledSuper = true;
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    int i = getChildCount();
    int j;
    int k;
    if ((paramInt & 0x2) != 0)
    {
      j = 0;
      k = 1;
      if (j == i) {
        break label103;
      }
      View localView = getChildAt(j);
      if (localView.getVisibility() != 0) {
        break label93;
      }
      ItemInfo localItemInfo = infoForChild(localView);
      if ((localItemInfo == null) || (localItemInfo.position != this.mCurItem) || (!localView.requestFocus(paramInt, paramRect))) {
        break label93;
      }
    }
    label93:
    label103:
    for (boolean bool = true;; bool = false)
    {
      return bool;
      j = i - 1;
      k = -1;
      i = -1;
      break;
      j += k;
      break;
    }
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
    }
    for (;;)
    {
      return;
      paramParcelable = (SavedState)paramParcelable;
      super.onRestoreInstanceState(paramParcelable.getSuperState());
      if (this.mAdapter != null)
      {
        this.mAdapter.restoreState(paramParcelable.adapterState, paramParcelable.loader);
        setCurrentItemInternal(paramParcelable.position, false, true);
      }
      else
      {
        this.mRestoredCurItem = paramParcelable.position;
        this.mRestoredAdapterState = paramParcelable.adapterState;
        this.mRestoredClassLoader = paramParcelable.loader;
      }
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.position = this.mCurItem;
    if (this.mAdapter != null) {
      localSavedState.adapterState = this.mAdapter.saveState();
    }
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 != paramInt3) {
      recomputeScrollPosition(paramInt1, paramInt3, this.mPageMargin, this.mPageMargin);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mFakeDragging) {
      bool1 = true;
    }
    for (;;)
    {
      return bool1;
      if ((paramMotionEvent.getAction() == 0) && (paramMotionEvent.getEdgeFlags() != 0))
      {
        bool1 = false;
      }
      else
      {
        if ((this.mAdapter != null) && (this.mAdapter.getCount() != 0)) {
          break;
        }
        bool1 = false;
      }
    }
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    int i = paramMotionEvent.getAction();
    boolean bool2 = false;
    boolean bool1 = bool2;
    switch (i & 0xFF)
    {
    default: 
      bool1 = bool2;
    }
    for (;;)
    {
      if (bool1) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      bool1 = true;
      break;
      this.mScroller.abortAnimation();
      this.mPopulatePending = false;
      populate();
      float f1 = paramMotionEvent.getX();
      this.mInitialMotionX = f1;
      this.mLastMotionX = f1;
      f1 = paramMotionEvent.getY();
      this.mInitialMotionY = f1;
      this.mLastMotionY = f1;
      this.mActivePointerId = paramMotionEvent.getPointerId(0);
      bool1 = bool2;
      continue;
      float f3;
      if (!this.mIsBeingDragged)
      {
        i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
        if (i == -1)
        {
          bool1 = resetTouch();
          continue;
        }
        float f2 = paramMotionEvent.getX(i);
        f1 = Math.abs(f2 - this.mLastMotionX);
        f3 = paramMotionEvent.getY(i);
        float f4 = Math.abs(f3 - this.mLastMotionY);
        if ((f1 > this.mTouchSlop) && (f1 > f4))
        {
          this.mIsBeingDragged = true;
          requestParentDisallowInterceptTouchEvent(true);
          if (f2 - this.mInitialMotionX <= 0.0F) {
            break label408;
          }
        }
      }
      Object localObject;
      label408:
      for (f1 = this.mInitialMotionX + this.mTouchSlop;; f1 = this.mInitialMotionX - this.mTouchSlop)
      {
        this.mLastMotionX = f1;
        this.mLastMotionY = f3;
        setScrollState(1);
        setScrollingCacheEnabled(true);
        localObject = getParent();
        if (localObject != null) {
          ((ViewParent)localObject).requestDisallowInterceptTouchEvent(true);
        }
        bool1 = bool2;
        if (!this.mIsBeingDragged) {
          break;
        }
        bool1 = false | performDrag(paramMotionEvent.getX(paramMotionEvent.findPointerIndex(this.mActivePointerId)));
        break;
      }
      bool1 = bool2;
      if (this.mIsBeingDragged)
      {
        localObject = this.mVelocityTracker;
        ((VelocityTracker)localObject).computeCurrentVelocity(1000, this.mMaximumVelocity);
        int j = (int)((VelocityTracker)localObject).getXVelocity(this.mActivePointerId);
        this.mPopulatePending = true;
        i = getClientWidth();
        int k = getScrollX();
        localObject = infoForCurrentScrollPosition();
        f1 = this.mPageMargin / i;
        setCurrentItemInternal(determineTargetPage(((ItemInfo)localObject).position, (k / i - ((ItemInfo)localObject).offset) / (((ItemInfo)localObject).widthFactor + f1), j, (int)(paramMotionEvent.getX(paramMotionEvent.findPointerIndex(this.mActivePointerId)) - this.mInitialMotionX)), true, true, j);
        bool1 = resetTouch();
        continue;
        bool1 = bool2;
        if (this.mIsBeingDragged)
        {
          scrollToItem(this.mCurItem, true, 0, false);
          bool1 = resetTouch();
          continue;
          i = paramMotionEvent.getActionIndex();
          this.mLastMotionX = paramMotionEvent.getX(i);
          this.mActivePointerId = paramMotionEvent.getPointerId(i);
          bool1 = bool2;
          continue;
          onSecondaryPointerUp(paramMotionEvent);
          this.mLastMotionX = paramMotionEvent.getX(paramMotionEvent.findPointerIndex(this.mActivePointerId));
          bool1 = bool2;
        }
      }
    }
  }
  
  boolean pageLeft()
  {
    boolean bool = true;
    if (this.mCurItem > 0) {
      setCurrentItem(this.mCurItem - 1, true);
    }
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  boolean pageRight()
  {
    boolean bool = true;
    if ((this.mAdapter != null) && (this.mCurItem < this.mAdapter.getCount() - 1)) {
      setCurrentItem(this.mCurItem + 1, true);
    }
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  void populate()
  {
    populate(this.mCurItem);
  }
  
  void populate(int paramInt)
  {
    Object localObject1 = null;
    if (this.mCurItem != paramInt)
    {
      localObject1 = infoForPosition(this.mCurItem);
      this.mCurItem = paramInt;
    }
    if (this.mAdapter == null) {
      sortChildDrawingOrder();
    }
    label349:
    label363:
    label448:
    label616:
    label622:
    label628:
    label650:
    label778:
    label788:
    label905:
    label911:
    label927:
    label1042:
    label1055:
    label1275:
    label1281:
    for (;;)
    {
      return;
      if (this.mPopulatePending)
      {
        sortChildDrawingOrder();
      }
      else if (getWindowToken() != null)
      {
        this.mAdapter.startUpdate(this);
        paramInt = this.mOffscreenPageLimit;
        int i = Math.max(0, this.mCurItem - paramInt);
        int j = this.mAdapter.getCount();
        int k = Math.min(j - 1, this.mCurItem + paramInt);
        if (j != this.mExpectedAdapterCount) {
          try
          {
            String str = getResources().getResourceName(getId());
            throw new IllegalStateException("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: " + this.mExpectedAdapterCount + ", found: " + j + " Pager id: " + str + " Pager class: " + getClass() + " Problematic adapter: " + this.mAdapter.getClass());
          }
          catch (Resources.NotFoundException localNotFoundException)
          {
            for (;;)
            {
              localObject2 = Integer.toHexString(getId());
            }
          }
        }
        Object localObject3 = null;
        paramInt = 0;
        Object localObject2 = localObject3;
        if (paramInt < this.mItems.size())
        {
          localObject4 = (ItemInfo)this.mItems.get(paramInt);
          if (((ItemInfo)localObject4).position < this.mCurItem) {
            break label616;
          }
          localObject2 = localObject3;
          if (((ItemInfo)localObject4).position == this.mCurItem) {
            localObject2 = localObject4;
          }
        }
        Object localObject4 = localObject2;
        if (localObject2 == null)
        {
          localObject4 = localObject2;
          if (j > 0) {
            localObject4 = addNewItem(this.mCurItem, paramInt);
          }
        }
        float f1;
        int m;
        int n;
        int i1;
        int i2;
        if (localObject4 != null)
        {
          f1 = 0.0F;
          m = paramInt - 1;
          if (m < 0) {
            break label622;
          }
          localObject2 = (ItemInfo)this.mItems.get(m);
          n = getClientWidth();
          if (n > 0) {
            break label628;
          }
          f2 = 0.0F;
          i1 = this.mCurItem - 1;
          localObject3 = localObject2;
          i2 = paramInt;
          if (i1 >= 0)
          {
            if ((f1 < f2) || (i1 >= i)) {
              break label788;
            }
            if (localObject3 != null) {
              break label650;
            }
          }
          f1 = ((ItemInfo)localObject4).widthFactor;
          i1 = i2 + 1;
          if (f1 < 2.0F)
          {
            if (i1 >= this.mItems.size()) {
              break label905;
            }
            localObject2 = (ItemInfo)this.mItems.get(i1);
            if (n > 0) {
              break label911;
            }
          }
        }
        int i3;
        for (float f2 = 0.0F;; f2 = getPaddingRight() / n + 2.0F)
        {
          i3 = this.mCurItem + 1;
          localObject3 = localObject2;
          if (i3 < j)
          {
            if ((f1 < f2) || (i3 <= k)) {
              break label1055;
            }
            if (localObject3 != null) {
              break label927;
            }
          }
          calculatePageOffsets((ItemInfo)localObject4, i2, (ItemInfo)localObject1);
          this.mAdapter.setPrimaryItem(this, this.mCurItem, ((ItemInfo)localObject4).object);
          this.mAdapter.finishUpdate(this);
          i3 = getChildCount();
          for (paramInt = 0; paramInt < i3; paramInt++)
          {
            localObject1 = getChildAt(paramInt);
            localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
            ((LayoutParams)localObject2).childIndex = paramInt;
            if ((!((LayoutParams)localObject2).isDecor) && (((LayoutParams)localObject2).widthFactor == 0.0F))
            {
              localObject1 = infoForChild((View)localObject1);
              if (localObject1 != null)
              {
                ((LayoutParams)localObject2).widthFactor = ((ItemInfo)localObject1).widthFactor;
                ((LayoutParams)localObject2).position = ((ItemInfo)localObject1).position;
              }
            }
          }
          paramInt++;
          break;
          localObject2 = null;
          break label349;
          f2 = 2.0F - ((ItemInfo)localObject4).widthFactor + getPaddingLeft() / n;
          break label363;
          paramInt = i2;
          f3 = f1;
          localObject2 = localObject3;
          i3 = m;
          if (i1 == ((ItemInfo)localObject3).position)
          {
            paramInt = i2;
            f3 = f1;
            localObject2 = localObject3;
            i3 = m;
            if (!((ItemInfo)localObject3).scrolling)
            {
              this.mItems.remove(m);
              this.mAdapter.destroyItem(this, i1, ((ItemInfo)localObject3).object);
              i3 = m - 1;
              paramInt = i2 - 1;
              if (i3 < 0) {
                break label778;
              }
              localObject2 = (ItemInfo)this.mItems.get(i3);
            }
          }
          for (f3 = f1;; f3 = f1)
          {
            i1--;
            i2 = paramInt;
            f1 = f3;
            localObject3 = localObject2;
            m = i3;
            break;
            localObject2 = null;
          }
          if ((localObject3 != null) && (i1 == ((ItemInfo)localObject3).position))
          {
            f3 = f1 + ((ItemInfo)localObject3).widthFactor;
            i3 = m - 1;
            if (i3 >= 0) {}
            for (localObject2 = (ItemInfo)this.mItems.get(i3);; localObject2 = null)
            {
              paramInt = i2;
              break;
            }
          }
          f3 = f1 + addNewItem(i1, m + 1).widthFactor;
          paramInt = i2 + 1;
          if (m >= 0) {}
          for (localObject2 = (ItemInfo)this.mItems.get(m);; localObject2 = null)
          {
            i3 = m;
            break;
          }
          localObject2 = null;
          break label448;
        }
        float f3 = f1;
        localObject2 = localObject3;
        paramInt = i1;
        if (i3 == ((ItemInfo)localObject3).position)
        {
          f3 = f1;
          localObject2 = localObject3;
          paramInt = i1;
          if (!((ItemInfo)localObject3).scrolling)
          {
            this.mItems.remove(i1);
            this.mAdapter.destroyItem(this, i3, ((ItemInfo)localObject3).object);
            if (i1 >= this.mItems.size()) {
              break label1042;
            }
            localObject2 = (ItemInfo)this.mItems.get(i1);
            paramInt = i1;
            f3 = f1;
          }
        }
        for (;;)
        {
          i3++;
          f1 = f3;
          localObject3 = localObject2;
          i1 = paramInt;
          break;
          localObject2 = null;
          f3 = f1;
          paramInt = i1;
        }
        if ((localObject3 != null) && (i3 == ((ItemInfo)localObject3).position))
        {
          f3 = f1 + ((ItemInfo)localObject3).widthFactor;
          paramInt = i1 + 1;
          if (paramInt < this.mItems.size()) {}
          for (localObject2 = (ItemInfo)this.mItems.get(paramInt);; localObject2 = null) {
            break;
          }
        }
        localObject2 = addNewItem(i3, i1);
        paramInt = i1 + 1;
        f3 = f1 + ((ItemInfo)localObject2).widthFactor;
        if (paramInt < this.mItems.size()) {}
        for (localObject2 = (ItemInfo)this.mItems.get(paramInt);; localObject2 = null) {
          break;
        }
        sortChildDrawingOrder();
        if (hasFocus())
        {
          localObject2 = findFocus();
          if (localObject2 != null) {}
          for (localObject2 = infoForAnyChild((View)localObject2);; localObject2 = null)
          {
            if ((localObject2 != null) && (((ItemInfo)localObject2).position == this.mCurItem)) {
              break label1281;
            }
            for (paramInt = 0;; paramInt++)
            {
              if (paramInt >= getChildCount()) {
                break label1275;
              }
              localObject2 = getChildAt(paramInt);
              localObject1 = infoForChild((View)localObject2);
              if ((localObject1 != null) && (((ItemInfo)localObject1).position == this.mCurItem) && (((View)localObject2).requestFocus(2))) {
                break;
              }
            }
            break;
          }
        }
      }
    }
  }
  
  public void removeOnAdapterChangeListener(OnAdapterChangeListener paramOnAdapterChangeListener)
  {
    if (this.mAdapterChangeListeners != null) {
      this.mAdapterChangeListeners.remove(paramOnAdapterChangeListener);
    }
  }
  
  public void removeOnPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    if (this.mOnPageChangeListeners != null) {
      this.mOnPageChangeListeners.remove(paramOnPageChangeListener);
    }
  }
  
  public void removeView(View paramView)
  {
    if (this.mInLayout) {
      removeViewInLayout(paramView);
    }
    for (;;)
    {
      return;
      super.removeView(paramView);
    }
  }
  
  public void setAdapter(PagerAdapter paramPagerAdapter)
  {
    int i;
    if (this.mAdapter != null)
    {
      this.mAdapter.setViewPagerObserver(null);
      this.mAdapter.startUpdate(this);
      for (i = 0; i < this.mItems.size(); i++)
      {
        localObject = (ItemInfo)this.mItems.get(i);
        this.mAdapter.destroyItem(this, ((ItemInfo)localObject).position, ((ItemInfo)localObject).object);
      }
      this.mAdapter.finishUpdate(this);
      this.mItems.clear();
      removeNonDecorViews();
      this.mCurItem = 0;
      scrollTo(0, 0);
    }
    Object localObject = this.mAdapter;
    this.mAdapter = paramPagerAdapter;
    this.mExpectedAdapterCount = 0;
    boolean bool;
    if (this.mAdapter != null)
    {
      if (this.mObserver == null) {
        this.mObserver = new PagerObserver();
      }
      this.mAdapter.setViewPagerObserver(this.mObserver);
      this.mPopulatePending = false;
      bool = this.mFirstLayout;
      this.mFirstLayout = true;
      this.mExpectedAdapterCount = this.mAdapter.getCount();
      if (this.mRestoredCurItem < 0) {
        break label291;
      }
      this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
      setCurrentItemInternal(this.mRestoredCurItem, false, true);
      this.mRestoredCurItem = -1;
      this.mRestoredAdapterState = null;
      this.mRestoredClassLoader = null;
    }
    while ((this.mAdapterChangeListeners != null) && (!this.mAdapterChangeListeners.isEmpty()))
    {
      i = 0;
      int j = this.mAdapterChangeListeners.size();
      while (i < j)
      {
        ((OnAdapterChangeListener)this.mAdapterChangeListeners.get(i)).onAdapterChanged(this, (PagerAdapter)localObject, paramPagerAdapter);
        i++;
      }
      label291:
      if (!bool) {
        populate();
      } else {
        requestLayout();
      }
    }
  }
  
  public void setCurrentItem(int paramInt)
  {
    this.mPopulatePending = false;
    if (!this.mFirstLayout) {}
    for (boolean bool = true;; bool = false)
    {
      setCurrentItemInternal(paramInt, bool, false);
      return;
    }
  }
  
  public void setCurrentItem(int paramInt, boolean paramBoolean)
  {
    this.mPopulatePending = false;
    setCurrentItemInternal(paramInt, paramBoolean, false);
  }
  
  void setCurrentItemInternal(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    setCurrentItemInternal(paramInt, paramBoolean1, paramBoolean2, 0);
  }
  
  void setCurrentItemInternal(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
  {
    boolean bool = true;
    if ((this.mAdapter == null) || (this.mAdapter.getCount() <= 0)) {
      setScrollingCacheEnabled(false);
    }
    for (;;)
    {
      return;
      if ((!paramBoolean2) && (this.mCurItem == paramInt1) && (this.mItems.size() != 0))
      {
        setScrollingCacheEnabled(false);
      }
      else
      {
        int i;
        if (paramInt1 < 0) {
          i = 0;
        }
        for (;;)
        {
          paramInt1 = this.mOffscreenPageLimit;
          if ((i <= this.mCurItem + paramInt1) && (i >= this.mCurItem - paramInt1)) {
            break;
          }
          for (paramInt1 = 0; paramInt1 < this.mItems.size(); paramInt1++) {
            ((ItemInfo)this.mItems.get(paramInt1)).scrolling = true;
          }
          i = paramInt1;
          if (paramInt1 >= this.mAdapter.getCount()) {
            i = this.mAdapter.getCount() - 1;
          }
        }
        if (this.mCurItem != i) {}
        for (paramBoolean2 = bool;; paramBoolean2 = false)
        {
          if (!this.mFirstLayout) {
            break label199;
          }
          this.mCurItem = i;
          if (paramBoolean2) {
            dispatchOnPageSelected(i);
          }
          requestLayout();
          break;
        }
        label199:
        populate(i);
        scrollToItem(i, paramBoolean1, paramInt2, paramBoolean2);
      }
    }
  }
  
  OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    OnPageChangeListener localOnPageChangeListener = this.mInternalPageChangeListener;
    this.mInternalPageChangeListener = paramOnPageChangeListener;
    return localOnPageChangeListener;
  }
  
  public void setOffscreenPageLimit(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 1)
    {
      Log.w("ViewPager", "Requested offscreen page limit " + paramInt + " too small; defaulting to " + 1);
      i = 1;
    }
    if (i != this.mOffscreenPageLimit)
    {
      this.mOffscreenPageLimit = i;
      populate();
    }
  }
  
  @Deprecated
  public void setOnPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    this.mOnPageChangeListener = paramOnPageChangeListener;
  }
  
  public void setPageMargin(int paramInt)
  {
    int i = this.mPageMargin;
    this.mPageMargin = paramInt;
    int j = getWidth();
    recomputeScrollPosition(j, j, paramInt, i);
    requestLayout();
  }
  
  public void setPageMarginDrawable(int paramInt)
  {
    setPageMarginDrawable(ContextCompat.getDrawable(getContext(), paramInt));
  }
  
  public void setPageMarginDrawable(Drawable paramDrawable)
  {
    this.mMarginDrawable = paramDrawable;
    if (paramDrawable != null) {
      refreshDrawableState();
    }
    if (paramDrawable == null) {}
    for (boolean bool = true;; bool = false)
    {
      setWillNotDraw(bool);
      invalidate();
      return;
    }
  }
  
  public void setPageTransformer(boolean paramBoolean, PageTransformer paramPageTransformer)
  {
    setPageTransformer(paramBoolean, paramPageTransformer, 2);
  }
  
  public void setPageTransformer(boolean paramBoolean, PageTransformer paramPageTransformer, int paramInt)
  {
    int i = 1;
    boolean bool1;
    boolean bool2;
    label20:
    int j;
    if (paramPageTransformer != null)
    {
      bool1 = true;
      if (this.mPageTransformer == null) {
        break label80;
      }
      bool2 = true;
      if (bool1 == bool2) {
        break label86;
      }
      j = 1;
      label30:
      this.mPageTransformer = paramPageTransformer;
      setChildrenDrawingOrderEnabled(bool1);
      if (!bool1) {
        break label92;
      }
      if (paramBoolean) {
        i = 2;
      }
      this.mDrawingOrder = i;
      this.mPageTransformerLayerType = paramInt;
    }
    for (;;)
    {
      if (j != 0) {
        populate();
      }
      return;
      bool1 = false;
      break;
      label80:
      bool2 = false;
      break label20;
      label86:
      j = 0;
      break label30;
      label92:
      this.mDrawingOrder = 0;
    }
  }
  
  void setScrollState(int paramInt)
  {
    if (this.mScrollState == paramInt) {
      return;
    }
    this.mScrollState = paramInt;
    if (this.mPageTransformer != null) {
      if (paramInt == 0) {
        break label40;
      }
    }
    label40:
    for (boolean bool = true;; bool = false)
    {
      enableLayers(bool);
      dispatchOnScrollStateChanged(paramInt);
      break;
    }
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2)
  {
    smoothScrollTo(paramInt1, paramInt2, 0);
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2, int paramInt3)
  {
    if (getChildCount() == 0)
    {
      setScrollingCacheEnabled(false);
      return;
    }
    int i;
    if ((this.mScroller != null) && (!this.mScroller.isFinished()))
    {
      i = 1;
      label33:
      if (i == 0) {
        break label127;
      }
      if (!this.mIsScrollStarted) {
        break label115;
      }
      i = this.mScroller.getCurrX();
      label54:
      this.mScroller.abortAnimation();
      setScrollingCacheEnabled(false);
    }
    int j;
    int k;
    for (;;)
    {
      j = getScrollY();
      k = paramInt1 - i;
      paramInt2 -= j;
      if ((k != 0) || (paramInt2 != 0)) {
        break label136;
      }
      completeScroll(false);
      populate();
      setScrollState(0);
      break;
      i = 0;
      break label33;
      label115:
      i = this.mScroller.getStartX();
      break label54;
      label127:
      i = getScrollX();
    }
    label136:
    setScrollingCacheEnabled(true);
    setScrollState(2);
    paramInt1 = getClientWidth();
    int m = paramInt1 / 2;
    float f1 = Math.min(1.0F, 1.0F * Math.abs(k) / paramInt1);
    float f2 = m;
    float f3 = m;
    f1 = distanceInfluenceForSnapDuration(f1);
    paramInt3 = Math.abs(paramInt3);
    if (paramInt3 > 0) {}
    for (paramInt1 = Math.round(1000.0F * Math.abs((f2 + f3 * f1) / paramInt3)) * 4;; paramInt1 = (int)((1.0F + Math.abs(k) / (this.mPageMargin + f2 * f3)) * 100.0F))
    {
      paramInt1 = Math.min(paramInt1, 600);
      this.mIsScrollStarted = false;
      this.mScroller.startScroll(i, j, k, paramInt2, paramInt1);
      ViewCompat.postInvalidateOnAnimation(this);
      break;
      f2 = paramInt1;
      f3 = this.mAdapter.getPageWidth(this.mCurItem);
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if ((super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mMarginDrawable)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({java.lang.annotation.ElementType.TYPE})
  public static @interface DecorView {}
  
  static class ItemInfo
  {
    Object object;
    float offset;
    int position;
    boolean scrolling;
    float widthFactor;
  }
  
  public static class LayoutParams
    extends ViewGroup.LayoutParams
  {
    int childIndex;
    public int gravity;
    public boolean isDecor;
    boolean needsMeasure;
    int position;
    float widthFactor = 0.0F;
    
    public LayoutParams()
    {
      super(-1);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, ViewPager.LAYOUT_ATTRS);
      this.gravity = paramContext.getInteger(0, 48);
      paramContext.recycle();
    }
  }
  
  class MyAccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    MyAccessibilityDelegate() {}
    
    private boolean canScroll()
    {
      boolean bool = true;
      if ((ViewPager.this.mAdapter != null) && (ViewPager.this.mAdapter.getCount() > 1)) {}
      for (;;)
      {
        return bool;
        bool = false;
      }
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(ViewPager.class.getName());
      paramAccessibilityEvent.setScrollable(canScroll());
      if ((paramAccessibilityEvent.getEventType() == 4096) && (ViewPager.this.mAdapter != null))
      {
        paramAccessibilityEvent.setItemCount(ViewPager.this.mAdapter.getCount());
        paramAccessibilityEvent.setFromIndex(ViewPager.this.mCurItem);
        paramAccessibilityEvent.setToIndex(ViewPager.this.mCurItem);
      }
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      paramAccessibilityNodeInfoCompat.setClassName(ViewPager.class.getName());
      paramAccessibilityNodeInfoCompat.setScrollable(canScroll());
      if (ViewPager.this.canScrollHorizontally(1)) {
        paramAccessibilityNodeInfoCompat.addAction(4096);
      }
      if (ViewPager.this.canScrollHorizontally(-1)) {
        paramAccessibilityNodeInfoCompat.addAction(8192);
      }
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      boolean bool = true;
      if (super.performAccessibilityAction(paramView, paramInt, paramBundle)) {}
      for (;;)
      {
        return bool;
        switch (paramInt)
        {
        default: 
          bool = false;
          break;
        case 4096: 
          if (ViewPager.this.canScrollHorizontally(1)) {
            ViewPager.this.setCurrentItem(ViewPager.this.mCurItem + 1);
          } else {
            bool = false;
          }
          break;
        case 8192: 
          if (ViewPager.this.canScrollHorizontally(-1)) {
            ViewPager.this.setCurrentItem(ViewPager.this.mCurItem - 1);
          } else {
            bool = false;
          }
          break;
        }
      }
    }
  }
  
  public static abstract interface OnAdapterChangeListener
  {
    public abstract void onAdapterChanged(ViewPager paramViewPager, PagerAdapter paramPagerAdapter1, PagerAdapter paramPagerAdapter2);
  }
  
  public static abstract interface OnPageChangeListener
  {
    public abstract void onPageScrollStateChanged(int paramInt);
    
    public abstract void onPageScrolled(int paramInt1, float paramFloat, int paramInt2);
    
    public abstract void onPageSelected(int paramInt);
  }
  
  public static abstract interface PageTransformer
  {
    public abstract void transformPage(View paramView, float paramFloat);
  }
  
  private class PagerObserver
    extends DataSetObserver
  {
    PagerObserver() {}
    
    public void onChanged()
    {
      ViewPager.this.dataSetChanged();
    }
    
    public void onInvalidated()
    {
      ViewPager.this.dataSetChanged();
    }
  }
  
  public static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator()
    {
      public ViewPager.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ViewPager.SavedState(paramAnonymousParcel, null);
      }
      
      public ViewPager.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new ViewPager.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public ViewPager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ViewPager.SavedState[paramAnonymousInt];
      }
    };
    Parcelable adapterState;
    ClassLoader loader;
    int position;
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      ClassLoader localClassLoader = paramClassLoader;
      if (paramClassLoader == null) {
        localClassLoader = getClass().getClassLoader();
      }
      this.position = paramParcel.readInt();
      this.adapterState = paramParcel.readParcelable(localClassLoader);
      this.loader = localClassLoader;
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.position);
      paramParcel.writeParcelable(this.adapterState, paramInt);
    }
  }
  
  static class ViewPositionComparator
    implements Comparator<View>
  {
    public int compare(View paramView1, View paramView2)
    {
      paramView1 = (ViewPager.LayoutParams)paramView1.getLayoutParams();
      paramView2 = (ViewPager.LayoutParams)paramView2.getLayoutParams();
      int i;
      if (paramView1.isDecor != paramView2.isDecor) {
        if (paramView1.isDecor) {
          i = 1;
        }
      }
      for (;;)
      {
        return i;
        i = -1;
        continue;
        i = paramView1.position - paramView2.position;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/ViewPager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */