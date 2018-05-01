package org.telegram.messenger.support.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.OverScroller;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;

public class RecyclerView
  extends ViewGroup
  implements NestedScrollingChild2
{
  static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC;
  private static final boolean ALLOW_THREAD_GAP_WORK;
  private static final int[] CLIP_TO_PADDING_ATTR;
  static final boolean DEBUG = false;
  static final int DEFAULT_ORIENTATION = 1;
  static final boolean DISPATCH_TEMP_DETACH = false;
  private static final boolean FORCE_ABS_FOCUS_SEARCH_DIRECTION;
  static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
  static final long FOREVER_NS = Long.MAX_VALUE;
  public static final int HORIZONTAL = 0;
  private static final boolean IGNORE_DETACHED_FOCUSED_CHILD;
  private static final int INVALID_POINTER = -1;
  public static final int INVALID_TYPE = -1;
  private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
  static final int MAX_SCROLL_DURATION = 2000;
  private static final int[] NESTED_SCROLLING_ATTRS = { 16843830 };
  public static final long NO_ID = -1L;
  public static final int NO_POSITION = -1;
  static final boolean POST_UPDATES_ON_ANIMATION;
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
  static final Interpolator sQuinticInterpolator;
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
  boolean mDataSetHasChangedAfterLayout = false;
  boolean mDispatchItemsChangedEvent = false;
  private int mDispatchScrollCounter = 0;
  private int mEatenAccessibilityChangeFlags;
  private EdgeEffectFactory mEdgeEffectFactory = new EdgeEffectFactory();
  boolean mEnableFastScroller;
  boolean mFirstLayoutComplete;
  GapWorker mGapWorker;
  boolean mHasFixedSize;
  private boolean mIgnoreMotionEventTillDown;
  private int mInitialTouchX;
  private int mInitialTouchY;
  private int mInterceptRequestLayoutDepth = 0;
  boolean mIsAttached;
  ItemAnimator mItemAnimator = new DefaultItemAnimator();
  private RecyclerView.ItemAnimator.ItemAnimatorListener mItemAnimatorListener;
  private Runnable mItemAnimatorRunner;
  final ArrayList<ItemDecoration> mItemDecorations = new ArrayList();
  boolean mItemsAddedOrRemoved;
  boolean mItemsChanged;
  private int mLastTouchX;
  private int mLastTouchY;
  LayoutManager mLayout;
  boolean mLayoutFrozen;
  private int mLayoutOrScrollCounter = 0;
  boolean mLayoutWasDefered;
  private EdgeEffect mLeftGlow;
  private final int mMaxFlingVelocity;
  private final int mMinFlingVelocity;
  private final int[] mMinMaxLayoutPositions;
  private final int[] mNestedOffsets;
  private final RecyclerViewDataObserver mObserver = new RecyclerViewDataObserver();
  private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
  private OnFlingListener mOnFlingListener;
  private final ArrayList<OnItemTouchListener> mOnItemTouchListeners = new ArrayList();
  final List<ViewHolder> mPendingAccessibilityImportanceChange;
  private SavedState mPendingSavedState;
  boolean mPostedAnimatorRunner;
  GapWorker.LayoutPrefetchRegistryImpl mPrefetchRegistry;
  private boolean mPreserveFocusAfterLayout = true;
  final Recycler mRecycler = new Recycler();
  RecyclerListener mRecyclerListener;
  private EdgeEffect mRightGlow;
  private float mScaledHorizontalScrollFactor = Float.MIN_VALUE;
  private float mScaledVerticalScrollFactor = Float.MIN_VALUE;
  private final int[] mScrollConsumed;
  private OnScrollListener mScrollListener;
  private List<OnScrollListener> mScrollListeners;
  private final int[] mScrollOffset;
  private int mScrollPointerId = -1;
  private int mScrollState = 0;
  private NestedScrollingChildHelper mScrollingChildHelper;
  final State mState;
  final Rect mTempRect = new Rect();
  private final Rect mTempRect2 = new Rect();
  final RectF mTempRectF = new RectF();
  private EdgeEffect mTopGlow;
  private int mTouchSlop;
  final Runnable mUpdateChildViewsRunnable = new Runnable()
  {
    public void run()
    {
      if ((!RecyclerView.this.mFirstLayoutComplete) || (RecyclerView.this.isLayoutRequested())) {}
      for (;;)
      {
        return;
        if (!RecyclerView.this.mIsAttached) {
          RecyclerView.this.requestLayout();
        } else if (RecyclerView.this.mLayoutFrozen) {
          RecyclerView.this.mLayoutWasDefered = true;
        } else {
          RecyclerView.this.consumePendingUpdateOperations();
        }
      }
    }
  };
  private VelocityTracker mVelocityTracker;
  final ViewFlinger mViewFlinger = new ViewFlinger();
  private final ViewInfoStore.ProcessCallback mViewInfoProcessCallback;
  final ViewInfoStore mViewInfoStore = new ViewInfoStore();
  private int topGlowOffset;
  
  static
  {
    CLIP_TO_PADDING_ATTR = new int[] { 16842987 };
    if ((Build.VERSION.SDK_INT == 18) || (Build.VERSION.SDK_INT == 19) || (Build.VERSION.SDK_INT == 20))
    {
      bool = true;
      FORCE_INVALIDATE_DISPLAY_LIST = bool;
      if (Build.VERSION.SDK_INT < 23) {
        break label171;
      }
      bool = true;
      label64:
      ALLOW_SIZE_IN_UNSPECIFIED_SPEC = bool;
      if (Build.VERSION.SDK_INT < 16) {
        break label176;
      }
      bool = true;
      label78:
      POST_UPDATES_ON_ANIMATION = bool;
      if (Build.VERSION.SDK_INT < 21) {
        break label181;
      }
      bool = true;
      label92:
      ALLOW_THREAD_GAP_WORK = bool;
      if (Build.VERSION.SDK_INT > 15) {
        break label186;
      }
      bool = true;
      label106:
      FORCE_ABS_FOCUS_SEARCH_DIRECTION = bool;
      if (Build.VERSION.SDK_INT > 15) {
        break label191;
      }
    }
    label171:
    label176:
    label181:
    label186:
    label191:
    for (boolean bool = true;; bool = false)
    {
      IGNORE_DETACHED_FOCUSED_CHILD = bool;
      LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE };
      sQuinticInterpolator = new Interpolator()
      {
        public float getInterpolation(float paramAnonymousFloat)
        {
          paramAnonymousFloat -= 1.0F;
          return paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat * paramAnonymousFloat + 1.0F;
        }
      };
      return;
      bool = false;
      break;
      bool = false;
      break label64;
      bool = false;
      break label78;
      bool = false;
      break label92;
      bool = false;
      break label106;
    }
  }
  
  public RecyclerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RecyclerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public RecyclerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    GapWorker.LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl;
    if (ALLOW_THREAD_GAP_WORK)
    {
      localLayoutPrefetchRegistryImpl = new GapWorker.LayoutPrefetchRegistryImpl();
      this.mPrefetchRegistry = localLayoutPrefetchRegistryImpl;
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
      this.mItemAnimatorRunner = new Runnable()
      {
        public void run()
        {
          if (RecyclerView.this.mItemAnimator != null) {
            RecyclerView.this.mItemAnimator.runPendingAnimations();
          }
          RecyclerView.this.mPostedAnimatorRunner = false;
        }
      };
      this.mViewInfoProcessCallback = new ViewInfoStore.ProcessCallback()
      {
        public void processAppeared(RecyclerView.ViewHolder paramAnonymousViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo2)
        {
          RecyclerView.this.animateAppearance(paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2);
        }
        
        public void processDisappeared(RecyclerView.ViewHolder paramAnonymousViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo2)
        {
          RecyclerView.this.mRecycler.unscrapView(paramAnonymousViewHolder);
          RecyclerView.this.animateDisappearance(paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2);
        }
        
        public void processPersistent(RecyclerView.ViewHolder paramAnonymousViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo2)
        {
          paramAnonymousViewHolder.setIsRecyclable(false);
          if (RecyclerView.this.mDataSetHasChangedAfterLayout) {
            if (RecyclerView.this.mItemAnimator.animateChange(paramAnonymousViewHolder, paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2)) {
              RecyclerView.this.postAnimationRunner();
            }
          }
          for (;;)
          {
            return;
            if (RecyclerView.this.mItemAnimator.animatePersistence(paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2)) {
              RecyclerView.this.postAnimationRunner();
            }
          }
        }
        
        public void unused(RecyclerView.ViewHolder paramAnonymousViewHolder)
        {
          RecyclerView.this.mLayout.removeAndRecycleView(paramAnonymousViewHolder.itemView, RecyclerView.this.mRecycler);
        }
      };
      if (paramAttributeSet == null) {
        break label516;
      }
      paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, CLIP_TO_PADDING_ATTR, paramInt, 0);
      this.mClipToPadding = paramAttributeSet.getBoolean(0, true);
      paramAttributeSet.recycle();
      label363:
      setScrollContainer(true);
      setFocusableInTouchMode(true);
      paramAttributeSet = ViewConfiguration.get(paramContext);
      this.mTouchSlop = paramAttributeSet.getScaledTouchSlop();
      this.mScaledHorizontalScrollFactor = ViewConfigurationCompat.getScaledHorizontalScrollFactor(paramAttributeSet, paramContext);
      this.mScaledVerticalScrollFactor = ViewConfigurationCompat.getScaledVerticalScrollFactor(paramAttributeSet, paramContext);
      this.mMinFlingVelocity = paramAttributeSet.getScaledMinimumFlingVelocity();
      this.mMaxFlingVelocity = paramAttributeSet.getScaledMaximumFlingVelocity();
      if (getOverScrollMode() != 2) {
        break label524;
      }
    }
    label516:
    label524:
    for (boolean bool = true;; bool = false)
    {
      setWillNotDraw(bool);
      this.mItemAnimator.setListener(this.mItemAnimatorListener);
      initAdapterManager();
      initChildrenHelper();
      if (ViewCompat.getImportantForAccessibility(this) == 0) {
        ViewCompat.setImportantForAccessibility(this, 1);
      }
      this.mAccessibilityManager = ((AccessibilityManager)getContext().getSystemService("accessibility"));
      setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
      setDescendantFocusability(262144);
      setNestedScrollingEnabled(true);
      return;
      localLayoutPrefetchRegistryImpl = null;
      break;
      this.mClipToPadding = true;
      break label363;
    }
  }
  
  private void addAnimatingView(ViewHolder paramViewHolder)
  {
    View localView = paramViewHolder.itemView;
    int i;
    if (localView.getParent() == this)
    {
      i = 1;
      this.mRecycler.unscrapView(getChildViewHolder(localView));
      if (!paramViewHolder.isTmpDetached()) {
        break label54;
      }
      this.mChildHelper.attachViewToParent(localView, -1, localView.getLayoutParams(), true);
    }
    for (;;)
    {
      return;
      i = 0;
      break;
      label54:
      if (i == 0) {
        this.mChildHelper.addView(localView, true);
      } else {
        this.mChildHelper.hide(localView);
      }
    }
  }
  
  private void animateChange(ViewHolder paramViewHolder1, ViewHolder paramViewHolder2, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2, boolean paramBoolean1, boolean paramBoolean2)
  {
    paramViewHolder1.setIsRecyclable(false);
    if (paramBoolean1) {
      addAnimatingView(paramViewHolder1);
    }
    if (paramViewHolder1 != paramViewHolder2)
    {
      if (paramBoolean2) {
        addAnimatingView(paramViewHolder2);
      }
      paramViewHolder1.mShadowedHolder = paramViewHolder2;
      addAnimatingView(paramViewHolder1);
      this.mRecycler.unscrapView(paramViewHolder1);
      paramViewHolder2.setIsRecyclable(false);
      paramViewHolder2.mShadowingHolder = paramViewHolder1;
    }
    if (this.mItemAnimator.animateChange(paramViewHolder1, paramViewHolder2, paramItemHolderInfo1, paramItemHolderInfo2)) {
      postAnimationRunner();
    }
  }
  
  private void cancelTouch()
  {
    resetTouch();
    setScrollState(0);
  }
  
  static void clearNestedRecyclerViewIfNotNested(ViewHolder paramViewHolder)
  {
    Object localObject;
    if (paramViewHolder.mNestedRecyclerView != null)
    {
      localObject = (View)paramViewHolder.mNestedRecyclerView.get();
      if (localObject == null) {
        break label56;
      }
      if (localObject != paramViewHolder.itemView) {
        break label31;
      }
    }
    for (;;)
    {
      return;
      label31:
      localObject = ((View)localObject).getParent();
      if ((localObject instanceof View))
      {
        localObject = (View)localObject;
        break;
      }
      localObject = null;
      break;
      label56:
      paramViewHolder.mNestedRecyclerView = null;
    }
  }
  
  /* Error */
  private void createLayoutManager(Context paramContext, String paramString, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_2
    //   1: ifnull +109 -> 110
    //   4: aload_2
    //   5: invokevirtual 710	java/lang/String:trim	()Ljava/lang/String;
    //   8: astore_2
    //   9: aload_2
    //   10: invokevirtual 713	java/lang/String:isEmpty	()Z
    //   13: ifne +97 -> 110
    //   16: aload_0
    //   17: aload_1
    //   18: aload_2
    //   19: invokespecial 717	org/telegram/messenger/support/widget/RecyclerView:getFullClassName	(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;
    //   22: astore 6
    //   24: aload_0
    //   25: invokevirtual 720	org/telegram/messenger/support/widget/RecyclerView:isInEditMode	()Z
    //   28: ifeq +83 -> 111
    //   31: aload_0
    //   32: invokevirtual 726	java/lang/Object:getClass	()Ljava/lang/Class;
    //   35: invokevirtual 730	java/lang/Class:getClassLoader	()Ljava/lang/ClassLoader;
    //   38: astore_2
    //   39: aload_2
    //   40: aload 6
    //   42: invokevirtual 736	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   45: ldc 59
    //   47: invokevirtual 740	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   50: astore 7
    //   52: aconst_null
    //   53: astore 8
    //   55: aload 7
    //   57: getstatic 358	org/telegram/messenger/support/widget/RecyclerView:LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE	[Ljava/lang/Class;
    //   60: invokevirtual 744	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   63: astore_2
    //   64: iconst_4
    //   65: anewarray 722	java/lang/Object
    //   68: dup
    //   69: iconst_0
    //   70: aload_1
    //   71: aastore
    //   72: dup
    //   73: iconst_1
    //   74: aload_3
    //   75: aastore
    //   76: dup
    //   77: iconst_2
    //   78: iload 4
    //   80: invokestatic 748	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   83: aastore
    //   84: dup
    //   85: iconst_3
    //   86: iload 5
    //   88: invokestatic 748	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   91: aastore
    //   92: astore_1
    //   93: aload_2
    //   94: iconst_1
    //   95: invokevirtual 753	java/lang/reflect/Constructor:setAccessible	(Z)V
    //   98: aload_0
    //   99: aload_2
    //   100: aload_1
    //   101: invokevirtual 757	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   104: checkcast 59	org/telegram/messenger/support/widget/RecyclerView$LayoutManager
    //   107: invokevirtual 761	org/telegram/messenger/support/widget/RecyclerView:setLayoutManager	(Lorg/telegram/messenger/support/widget/RecyclerView$LayoutManager;)V
    //   110: return
    //   111: aload_1
    //   112: invokevirtual 762	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   115: astore_2
    //   116: goto -77 -> 39
    //   119: astore_1
    //   120: aload 7
    //   122: iconst_0
    //   123: anewarray 346	java/lang/Class
    //   126: invokevirtual 744	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   129: astore_2
    //   130: aload 8
    //   132: astore_1
    //   133: goto -40 -> 93
    //   136: astore_2
    //   137: aload_2
    //   138: aload_1
    //   139: invokevirtual 766	java/lang/NoSuchMethodException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   142: pop
    //   143: new 768	java/lang/IllegalStateException
    //   146: astore_1
    //   147: new 770	java/lang/StringBuilder
    //   150: astore 8
    //   152: aload 8
    //   154: invokespecial 771	java/lang/StringBuilder:<init>	()V
    //   157: aload_1
    //   158: aload 8
    //   160: aload_3
    //   161: invokeinterface 774 1 0
    //   166: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: ldc_w 780
    //   172: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: aload 6
    //   177: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: invokevirtual 783	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   183: aload_2
    //   184: invokespecial 786	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   187: aload_1
    //   188: athrow
    //   189: astore_1
    //   190: new 768	java/lang/IllegalStateException
    //   193: dup
    //   194: new 770	java/lang/StringBuilder
    //   197: dup
    //   198: invokespecial 771	java/lang/StringBuilder:<init>	()V
    //   201: aload_3
    //   202: invokeinterface 774 1 0
    //   207: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   210: ldc_w 788
    //   213: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   216: aload 6
    //   218: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   221: invokevirtual 783	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   224: aload_1
    //   225: invokespecial 786	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   228: athrow
    //   229: astore_1
    //   230: new 768	java/lang/IllegalStateException
    //   233: dup
    //   234: new 770	java/lang/StringBuilder
    //   237: dup
    //   238: invokespecial 771	java/lang/StringBuilder:<init>	()V
    //   241: aload_3
    //   242: invokeinterface 774 1 0
    //   247: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   250: ldc_w 790
    //   253: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   256: aload 6
    //   258: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   261: invokevirtual 783	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   264: aload_1
    //   265: invokespecial 786	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   268: athrow
    //   269: astore_1
    //   270: new 768	java/lang/IllegalStateException
    //   273: dup
    //   274: new 770	java/lang/StringBuilder
    //   277: dup
    //   278: invokespecial 771	java/lang/StringBuilder:<init>	()V
    //   281: aload_3
    //   282: invokeinterface 774 1 0
    //   287: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   290: ldc_w 790
    //   293: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   296: aload 6
    //   298: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   301: invokevirtual 783	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   304: aload_1
    //   305: invokespecial 786	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   308: athrow
    //   309: astore_1
    //   310: new 768	java/lang/IllegalStateException
    //   313: dup
    //   314: new 770	java/lang/StringBuilder
    //   317: dup
    //   318: invokespecial 771	java/lang/StringBuilder:<init>	()V
    //   321: aload_3
    //   322: invokeinterface 774 1 0
    //   327: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   330: ldc_w 792
    //   333: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   336: aload 6
    //   338: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   341: invokevirtual 783	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   344: aload_1
    //   345: invokespecial 786	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   348: athrow
    //   349: astore_1
    //   350: new 768	java/lang/IllegalStateException
    //   353: dup
    //   354: new 770	java/lang/StringBuilder
    //   357: dup
    //   358: invokespecial 771	java/lang/StringBuilder:<init>	()V
    //   361: aload_3
    //   362: invokeinterface 774 1 0
    //   367: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   370: ldc_w 794
    //   373: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   376: aload 6
    //   378: invokevirtual 778	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   381: invokevirtual 783	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   384: aload_1
    //   385: invokespecial 786	java/lang/IllegalStateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   388: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	389	0	this	RecyclerView
    //   0	389	1	paramContext	Context
    //   0	389	2	paramString	String
    //   0	389	3	paramAttributeSet	AttributeSet
    //   0	389	4	paramInt1	int
    //   0	389	5	paramInt2	int
    //   22	355	6	str	String
    //   50	71	7	localClass	Class
    //   53	106	8	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   55	64	119	java/lang/NoSuchMethodException
    //   120	130	136	java/lang/NoSuchMethodException
    //   24	39	189	java/lang/ClassNotFoundException
    //   39	52	189	java/lang/ClassNotFoundException
    //   55	64	189	java/lang/ClassNotFoundException
    //   93	110	189	java/lang/ClassNotFoundException
    //   111	116	189	java/lang/ClassNotFoundException
    //   120	130	189	java/lang/ClassNotFoundException
    //   137	189	189	java/lang/ClassNotFoundException
    //   24	39	229	java/lang/reflect/InvocationTargetException
    //   39	52	229	java/lang/reflect/InvocationTargetException
    //   55	64	229	java/lang/reflect/InvocationTargetException
    //   93	110	229	java/lang/reflect/InvocationTargetException
    //   111	116	229	java/lang/reflect/InvocationTargetException
    //   120	130	229	java/lang/reflect/InvocationTargetException
    //   137	189	229	java/lang/reflect/InvocationTargetException
    //   24	39	269	java/lang/InstantiationException
    //   39	52	269	java/lang/InstantiationException
    //   55	64	269	java/lang/InstantiationException
    //   93	110	269	java/lang/InstantiationException
    //   111	116	269	java/lang/InstantiationException
    //   120	130	269	java/lang/InstantiationException
    //   137	189	269	java/lang/InstantiationException
    //   24	39	309	java/lang/IllegalAccessException
    //   39	52	309	java/lang/IllegalAccessException
    //   55	64	309	java/lang/IllegalAccessException
    //   93	110	309	java/lang/IllegalAccessException
    //   111	116	309	java/lang/IllegalAccessException
    //   120	130	309	java/lang/IllegalAccessException
    //   137	189	309	java/lang/IllegalAccessException
    //   24	39	349	java/lang/ClassCastException
    //   39	52	349	java/lang/ClassCastException
    //   55	64	349	java/lang/ClassCastException
    //   93	110	349	java/lang/ClassCastException
    //   111	116	349	java/lang/ClassCastException
    //   120	130	349	java/lang/ClassCastException
    //   137	189	349	java/lang/ClassCastException
  }
  
  private boolean didChildRangeChange(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
    if ((this.mMinMaxLayoutPositions[0] != paramInt1) || (this.mMinMaxLayoutPositions[1] != paramInt2)) {
      bool = true;
    }
    return bool;
  }
  
  private void dispatchContentChangedIfNecessary()
  {
    int i = this.mEatenAccessibilityChangeFlags;
    this.mEatenAccessibilityChangeFlags = 0;
    if ((i != 0) && (isAccessibilityEnabled()))
    {
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
      localAccessibilityEvent.setEventType(2048);
      AccessibilityEventCompat.setContentChangeTypes(localAccessibilityEvent, i);
      sendAccessibilityEventUnchecked(localAccessibilityEvent);
    }
  }
  
  private void dispatchLayoutStep1()
  {
    this.mState.assertLayoutStep(1);
    fillRemainingScrollValues(this.mState);
    this.mState.mIsMeasuring = false;
    startInterceptRequestLayout();
    this.mViewInfoStore.clear();
    onEnterLayoutOrScroll();
    processAdapterUpdatesAndSetAnimationFlags();
    saveFocusInfo();
    Object localObject = this.mState;
    boolean bool;
    int i;
    int j;
    label143:
    ViewHolder localViewHolder;
    if ((this.mState.mRunSimpleAnimations) && (this.mItemsChanged))
    {
      bool = true;
      ((State)localObject).mTrackOldChangeHolders = bool;
      this.mItemsChanged = false;
      this.mItemsAddedOrRemoved = false;
      this.mState.mInPreLayout = this.mState.mRunPredictiveAnimations;
      this.mState.mItemCount = this.mAdapter.getItemCount();
      findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
      if (!this.mState.mRunSimpleAnimations) {
        break label298;
      }
      i = this.mChildHelper.getChildCount();
      j = 0;
      if (j >= i) {
        break label298;
      }
      localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(j));
      if ((!localViewHolder.shouldIgnore()) && ((!localViewHolder.isInvalid()) || (this.mAdapter.hasStableIds()))) {
        break label200;
      }
    }
    for (;;)
    {
      j++;
      break label143;
      bool = false;
      break;
      label200:
      localObject = this.mItemAnimator.recordPreLayoutInformation(this.mState, localViewHolder, ItemAnimator.buildAdapterChangeFlagsForAnimations(localViewHolder), localViewHolder.getUnmodifiedPayloads());
      this.mViewInfoStore.addToPreLayout(localViewHolder, (RecyclerView.ItemAnimator.ItemHolderInfo)localObject);
      if ((this.mState.mTrackOldChangeHolders) && (localViewHolder.isUpdated()) && (!localViewHolder.isRemoved()) && (!localViewHolder.shouldIgnore()) && (!localViewHolder.isInvalid()))
      {
        long l = getChangedHolderKey(localViewHolder);
        this.mViewInfoStore.addToOldChangeHolders(l, localViewHolder);
      }
    }
    label298:
    if (this.mState.mRunPredictiveAnimations)
    {
      saveOldPositions();
      bool = this.mState.mStructureChanged;
      this.mState.mStructureChanged = false;
      this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
      this.mState.mStructureChanged = bool;
      j = 0;
      if (j < this.mChildHelper.getChildCount())
      {
        localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(j));
        if (localViewHolder.shouldIgnore()) {}
        for (;;)
        {
          j++;
          break;
          if (!this.mViewInfoStore.isInPreLayout(localViewHolder))
          {
            int k = ItemAnimator.buildAdapterChangeFlagsForAnimations(localViewHolder);
            bool = localViewHolder.hasAnyOfTheFlags(8192);
            i = k;
            if (!bool) {
              i = k | 0x1000;
            }
            localObject = this.mItemAnimator.recordPreLayoutInformation(this.mState, localViewHolder, i, localViewHolder.getUnmodifiedPayloads());
            if (bool) {
              recordAnimationInfoIfBouncedHiddenView(localViewHolder, (RecyclerView.ItemAnimator.ItemHolderInfo)localObject);
            } else {
              this.mViewInfoStore.addToAppearedInPreLayoutHolders(localViewHolder, (RecyclerView.ItemAnimator.ItemHolderInfo)localObject);
            }
          }
        }
      }
      clearOldPositions();
    }
    for (;;)
    {
      onExitLayoutOrScroll();
      stopInterceptRequestLayout(false);
      this.mState.mLayoutStep = 2;
      return;
      clearOldPositions();
    }
  }
  
  private void dispatchLayoutStep2()
  {
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
    State localState = this.mState;
    if ((this.mState.mRunSimpleAnimations) && (this.mItemAnimator != null)) {}
    for (boolean bool = true;; bool = false)
    {
      localState.mRunSimpleAnimations = bool;
      this.mState.mLayoutStep = 4;
      onExitLayoutOrScroll();
      stopInterceptRequestLayout(false);
      return;
    }
  }
  
  private void dispatchLayoutStep3()
  {
    this.mState.assertLayoutStep(4);
    startInterceptRequestLayout();
    onEnterLayoutOrScroll();
    this.mState.mLayoutStep = 1;
    if (this.mState.mRunSimpleAnimations)
    {
      int i = this.mChildHelper.getChildCount() - 1;
      if (i >= 0)
      {
        ViewHolder localViewHolder1 = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
        if (localViewHolder1.shouldIgnore()) {}
        for (;;)
        {
          i--;
          break;
          long l = getChangedHolderKey(localViewHolder1);
          RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo1 = this.mItemAnimator.recordPostLayoutInformation(this.mState, localViewHolder1);
          ViewHolder localViewHolder2 = this.mViewInfoStore.getFromOldChangeHolders(l);
          if ((localViewHolder2 != null) && (!localViewHolder2.shouldIgnore()))
          {
            boolean bool1 = this.mViewInfoStore.isDisappearing(localViewHolder2);
            boolean bool2 = this.mViewInfoStore.isDisappearing(localViewHolder1);
            if ((bool1) && (localViewHolder2 == localViewHolder1))
            {
              this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo1);
            }
            else
            {
              RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo2 = this.mViewInfoStore.popFromPreLayout(localViewHolder2);
              this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo1);
              localItemHolderInfo1 = this.mViewInfoStore.popFromPostLayout(localViewHolder1);
              if (localItemHolderInfo2 == null) {
                handleMissingPreInfoForChangeError(l, localViewHolder1, localViewHolder2);
              } else {
                animateChange(localViewHolder2, localViewHolder1, localItemHolderInfo2, localItemHolderInfo1, bool1, bool2);
              }
            }
          }
          else
          {
            this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo1);
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
    if (this.mLayout.mPrefetchMaxObservedInInitialPrefetch)
    {
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
  
  private boolean dispatchOnItemTouch(MotionEvent paramMotionEvent)
  {
    boolean bool1 = true;
    int i = paramMotionEvent.getAction();
    boolean bool2;
    if (this.mActiveOnItemTouchListener != null)
    {
      if (i == 0) {
        this.mActiveOnItemTouchListener = null;
      }
    }
    else
    {
      if (i == 0) {
        break label122;
      }
      int j = this.mOnItemTouchListeners.size();
      i = 0;
      if (i >= j) {
        break label122;
      }
      OnItemTouchListener localOnItemTouchListener = (OnItemTouchListener)this.mOnItemTouchListeners.get(i);
      if (!localOnItemTouchListener.onInterceptTouchEvent(this, paramMotionEvent)) {
        break label116;
      }
      this.mActiveOnItemTouchListener = localOnItemTouchListener;
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      this.mActiveOnItemTouchListener.onTouchEvent(this, paramMotionEvent);
      if (i != 3)
      {
        bool2 = bool1;
        if (i != 1) {}
      }
      else
      {
        this.mActiveOnItemTouchListener = null;
        bool2 = bool1;
        continue;
        label116:
        i++;
        break;
        label122:
        bool2 = false;
      }
    }
  }
  
  private boolean dispatchOnItemTouchIntercept(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if ((i == 3) || (i == 0)) {
      this.mActiveOnItemTouchListener = null;
    }
    int j = this.mOnItemTouchListeners.size();
    int k = 0;
    if (k < j)
    {
      OnItemTouchListener localOnItemTouchListener = (OnItemTouchListener)this.mOnItemTouchListeners.get(k);
      if ((localOnItemTouchListener.onInterceptTouchEvent(this, paramMotionEvent)) && (i != 3)) {
        this.mActiveOnItemTouchListener = localOnItemTouchListener;
      }
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      k++;
      break;
    }
  }
  
  private void findMinMaxChildLayoutPositions(int[] paramArrayOfInt)
  {
    int i = this.mChildHelper.getChildCount();
    if (i == 0)
    {
      paramArrayOfInt[0] = -1;
      paramArrayOfInt[1] = -1;
    }
    for (;;)
    {
      return;
      int j = Integer.MAX_VALUE;
      int k = Integer.MIN_VALUE;
      int m = 0;
      if (m < i)
      {
        ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(m));
        int n;
        if (localViewHolder.shouldIgnore())
        {
          n = j;
          j = k;
        }
        for (;;)
        {
          m++;
          k = j;
          j = n;
          break;
          int i1 = localViewHolder.getLayoutPosition();
          int i2 = j;
          if (i1 < j) {
            i2 = i1;
          }
          j = k;
          n = i2;
          if (i1 > k)
          {
            j = i1;
            n = i2;
          }
        }
      }
      paramArrayOfInt[0] = j;
      paramArrayOfInt[1] = k;
    }
  }
  
  static RecyclerView findNestedRecyclerView(View paramView)
  {
    if (!(paramView instanceof ViewGroup)) {
      paramView = null;
    }
    for (;;)
    {
      return paramView;
      if ((paramView instanceof RecyclerView))
      {
        paramView = (RecyclerView)paramView;
      }
      else
      {
        ViewGroup localViewGroup = (ViewGroup)paramView;
        int i = localViewGroup.getChildCount();
        for (int j = 0;; j++)
        {
          if (j >= i) {
            break label65;
          }
          paramView = findNestedRecyclerView(localViewGroup.getChildAt(j));
          if (paramView != null) {
            break;
          }
        }
        label65:
        paramView = null;
      }
    }
  }
  
  private View findNextViewToFocus()
  {
    Object localObject1 = null;
    int j;
    int k;
    label32:
    Object localObject2;
    if (this.mState.mFocusedItemPosition != -1)
    {
      i = this.mState.mFocusedItemPosition;
      j = this.mState.getItemCount();
      k = i;
      if (k < j)
      {
        localObject2 = findViewHolderForAdapterPosition(k);
        if (localObject2 != null) {
          break label89;
        }
      }
    }
    label89:
    label116:
    label137:
    for (int i = Math.min(j, i) - 1;; i--)
    {
      localObject2 = localObject1;
      if (i >= 0)
      {
        localObject2 = findViewHolderForAdapterPosition(i);
        if (localObject2 != null) {
          break label116;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        return (View)localObject2;
        i = 0;
        break;
        if (((ViewHolder)localObject2).itemView.hasFocusable())
        {
          localObject2 = ((ViewHolder)localObject2).itemView;
        }
        else
        {
          k++;
          break label32;
          if (!((ViewHolder)localObject2).itemView.hasFocusable()) {
            break label137;
          }
          localObject2 = ((ViewHolder)localObject2).itemView;
        }
      }
    }
  }
  
  static ViewHolder getChildViewHolderInt(View paramView)
  {
    if (paramView == null) {}
    for (paramView = null;; paramView = ((LayoutParams)paramView.getLayoutParams()).mViewHolder) {
      return paramView;
    }
  }
  
  static void getDecoratedBoundsWithMarginsInt(View paramView, Rect paramRect)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect localRect = localLayoutParams.mDecorInsets;
    paramRect.set(paramView.getLeft() - localRect.left - localLayoutParams.leftMargin, paramView.getTop() - localRect.top - localLayoutParams.topMargin, paramView.getRight() + localRect.right + localLayoutParams.rightMargin, paramView.getBottom() + localRect.bottom + localLayoutParams.bottomMargin);
  }
  
  private int getDeepestFocusedViewWithId(View paramView)
  {
    int i = paramView.getId();
    while ((!paramView.isFocused()) && ((paramView instanceof ViewGroup)) && (paramView.hasFocus()))
    {
      View localView = ((ViewGroup)paramView).getFocusedChild();
      paramView = localView;
      if (localView.getId() != -1)
      {
        i = localView.getId();
        paramView = localView;
      }
    }
    return i;
  }
  
  private String getFullClassName(Context paramContext, String paramString)
  {
    if (paramString.charAt(0) == '.') {
      paramContext = paramContext.getPackageName() + paramString;
    }
    for (;;)
    {
      return paramContext;
      paramContext = paramString;
      if (!paramString.contains(".")) {
        paramContext = RecyclerView.class.getPackage().getName() + '.' + paramString;
      }
    }
  }
  
  private NestedScrollingChildHelper getScrollingChildHelper()
  {
    if (this.mScrollingChildHelper == null) {
      this.mScrollingChildHelper = new NestedScrollingChildHelper(this);
    }
    return this.mScrollingChildHelper;
  }
  
  private void handleMissingPreInfoForChangeError(long paramLong, ViewHolder paramViewHolder1, ViewHolder paramViewHolder2)
  {
    int i = this.mChildHelper.getChildCount();
    int j = 0;
    if (j < i)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(j));
      if (localViewHolder == paramViewHolder1) {}
      while (getChangedHolderKey(localViewHolder) != paramLong)
      {
        j++;
        break;
      }
      if ((this.mAdapter != null) && (this.mAdapter.hasStableIds())) {
        throw new IllegalStateException("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:" + localViewHolder + " \n View Holder 2:" + paramViewHolder1 + exceptionLabel());
      }
      throw new IllegalStateException("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:" + localViewHolder + " \n View Holder 2:" + paramViewHolder1 + exceptionLabel());
    }
    Log.e("RecyclerView", "Problem while matching changed view holders with the newones. The pre-layout information for the change holder " + paramViewHolder2 + " cannot be found but it is necessary for " + paramViewHolder1 + exceptionLabel());
  }
  
  private boolean hasUpdatedView()
  {
    int i = this.mChildHelper.getChildCount();
    int j = 0;
    if (j < i)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(j));
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      while (!localViewHolder.isUpdated())
      {
        j++;
        break;
      }
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void initChildrenHelper()
  {
    this.mChildHelper = new ChildHelper(new ChildHelper.Callback()
    {
      public void addView(View paramAnonymousView, int paramAnonymousInt)
      {
        RecyclerView.this.addView(paramAnonymousView, paramAnonymousInt);
        RecyclerView.this.dispatchChildAttached(paramAnonymousView);
      }
      
      public void attachViewToParent(View paramAnonymousView, int paramAnonymousInt, ViewGroup.LayoutParams paramAnonymousLayoutParams)
      {
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramAnonymousView);
        if (localViewHolder != null)
        {
          if ((!localViewHolder.isTmpDetached()) && (!localViewHolder.shouldIgnore())) {
            throw new IllegalArgumentException("Called attach on a child which is not detached: " + localViewHolder + RecyclerView.this.exceptionLabel());
          }
          localViewHolder.clearTmpDetachFlag();
        }
        RecyclerView.this.attachViewToParent(paramAnonymousView, paramAnonymousInt, paramAnonymousLayoutParams);
      }
      
      public void detachViewFromParent(int paramAnonymousInt)
      {
        Object localObject = getChildAt(paramAnonymousInt);
        if (localObject != null)
        {
          localObject = RecyclerView.getChildViewHolderInt((View)localObject);
          if (localObject != null)
          {
            if ((((RecyclerView.ViewHolder)localObject).isTmpDetached()) && (!((RecyclerView.ViewHolder)localObject).shouldIgnore())) {
              throw new IllegalArgumentException("called detach on an already detached child " + localObject + RecyclerView.this.exceptionLabel());
            }
            ((RecyclerView.ViewHolder)localObject).addFlags(256);
          }
        }
        RecyclerView.this.detachViewFromParent(paramAnonymousInt);
      }
      
      public View getChildAt(int paramAnonymousInt)
      {
        return RecyclerView.this.getChildAt(paramAnonymousInt);
      }
      
      public int getChildCount()
      {
        return RecyclerView.this.getChildCount();
      }
      
      public RecyclerView.ViewHolder getChildViewHolder(View paramAnonymousView)
      {
        return RecyclerView.getChildViewHolderInt(paramAnonymousView);
      }
      
      public int indexOfChild(View paramAnonymousView)
      {
        return RecyclerView.this.indexOfChild(paramAnonymousView);
      }
      
      public void onEnteredHiddenState(View paramAnonymousView)
      {
        paramAnonymousView = RecyclerView.getChildViewHolderInt(paramAnonymousView);
        if (paramAnonymousView != null) {
          RecyclerView.ViewHolder.access$200(paramAnonymousView, RecyclerView.this);
        }
      }
      
      public void onLeftHiddenState(View paramAnonymousView)
      {
        paramAnonymousView = RecyclerView.getChildViewHolderInt(paramAnonymousView);
        if (paramAnonymousView != null) {
          RecyclerView.ViewHolder.access$300(paramAnonymousView, RecyclerView.this);
        }
      }
      
      public void removeAllViews()
      {
        int i = getChildCount();
        for (int j = 0; j < i; j++)
        {
          View localView = getChildAt(j);
          RecyclerView.this.dispatchChildDetached(localView);
          localView.clearAnimation();
        }
        RecyclerView.this.removeAllViews();
      }
      
      public void removeViewAt(int paramAnonymousInt)
      {
        View localView = RecyclerView.this.getChildAt(paramAnonymousInt);
        if (localView != null)
        {
          RecyclerView.this.dispatchChildDetached(localView);
          localView.clearAnimation();
        }
        RecyclerView.this.removeViewAt(paramAnonymousInt);
      }
    });
  }
  
  private boolean isPreferredNextFocus(View paramView1, View paramView2, int paramInt)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4;
    if ((paramView2 == null) || (paramView2 == this)) {
      bool4 = false;
    }
    for (;;)
    {
      return bool4;
      if (findContainingItemView(paramView2) == null)
      {
        bool4 = false;
      }
      else
      {
        bool4 = bool1;
        if (paramView1 != null)
        {
          bool4 = bool1;
          if (findContainingItemView(paramView1) != null)
          {
            this.mTempRect.set(0, 0, paramView1.getWidth(), paramView1.getHeight());
            this.mTempRect2.set(0, 0, paramView2.getWidth(), paramView2.getHeight());
            offsetDescendantRectToMyCoords(paramView1, this.mTempRect);
            offsetDescendantRectToMyCoords(paramView2, this.mTempRect2);
            int i;
            label124:
            int j;
            int k;
            label181:
            int m;
            if (this.mLayout.getLayoutDirection() == 1)
            {
              i = -1;
              j = 0;
              if (((this.mTempRect.left >= this.mTempRect2.left) && (this.mTempRect.right > this.mTempRect2.left)) || (this.mTempRect.right >= this.mTempRect2.right)) {
                break label337;
              }
              k = 1;
              m = 0;
              if (((this.mTempRect.top >= this.mTempRect2.top) && (this.mTempRect.bottom > this.mTempRect2.top)) || (this.mTempRect.bottom >= this.mTempRect2.bottom)) {
                break label402;
              }
              j = 1;
            }
            for (;;)
            {
              switch (paramInt)
              {
              default: 
                throw new IllegalArgumentException("Invalid direction: " + paramInt + exceptionLabel());
                i = 1;
                break label124;
                label337:
                if (this.mTempRect.right <= this.mTempRect2.right)
                {
                  k = j;
                  if (this.mTempRect.left < this.mTempRect2.right) {
                    break label181;
                  }
                }
                k = j;
                if (this.mTempRect.left <= this.mTempRect2.left) {
                  break label181;
                }
                k = -1;
                break label181;
                label402:
                if (this.mTempRect.bottom <= this.mTempRect2.bottom)
                {
                  j = m;
                  if (this.mTempRect.top < this.mTempRect2.bottom) {}
                }
                else
                {
                  j = m;
                  if (this.mTempRect.top > this.mTempRect2.top) {
                    j = -1;
                  }
                }
                break;
              }
            }
            bool4 = bool1;
            if (k >= 0)
            {
              bool4 = false;
              continue;
              bool4 = bool1;
              if (k <= 0)
              {
                bool4 = false;
                continue;
                bool4 = bool1;
                if (j >= 0)
                {
                  bool4 = false;
                  continue;
                  bool4 = bool1;
                  if (j <= 0)
                  {
                    bool4 = false;
                    continue;
                    if (j <= 0)
                    {
                      bool4 = bool3;
                      if (j == 0)
                      {
                        bool4 = bool3;
                        if (k * i < 0) {}
                      }
                    }
                    else
                    {
                      bool4 = true;
                    }
                    continue;
                    if (j >= 0)
                    {
                      bool4 = bool2;
                      if (j == 0)
                      {
                        bool4 = bool2;
                        if (k * i > 0) {}
                      }
                    }
                    else
                    {
                      bool4 = true;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  private void onPointerUp(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getActionIndex();
    if (paramMotionEvent.getPointerId(i) == this.mScrollPointerId) {
      if (i != 0) {
        break label75;
      }
    }
    label75:
    for (i = 1;; i = 0)
    {
      this.mScrollPointerId = paramMotionEvent.getPointerId(i);
      int j = (int)(paramMotionEvent.getX(i) + 0.5F);
      this.mLastTouchX = j;
      this.mInitialTouchX = j;
      i = (int)(paramMotionEvent.getY(i) + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      return;
    }
  }
  
  private boolean predictiveItemAnimationsEnabled()
  {
    if ((this.mItemAnimator != null) && (this.mLayout.supportsPredictiveItemAnimations())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void processAdapterUpdatesAndSetAnimationFlags()
  {
    boolean bool1 = true;
    if (this.mDataSetHasChangedAfterLayout)
    {
      this.mAdapterHelper.reset();
      if (this.mDispatchItemsChangedEvent) {
        this.mLayout.onItemsChanged(this);
      }
    }
    int i;
    label61:
    State localState;
    if (predictiveItemAnimationsEnabled())
    {
      this.mAdapterHelper.preProcess();
      if ((!this.mItemsAddedOrRemoved) && (!this.mItemsChanged)) {
        break label180;
      }
      i = 1;
      localState = this.mState;
      if ((!this.mFirstLayoutComplete) || (this.mItemAnimator == null) || ((!this.mDataSetHasChangedAfterLayout) && (i == 0) && (!this.mLayout.mRequestedSimpleAnimations)) || ((this.mDataSetHasChangedAfterLayout) && (!this.mAdapter.hasStableIds()))) {
        break label185;
      }
      bool2 = true;
      label121:
      localState.mRunSimpleAnimations = bool2;
      localState = this.mState;
      if ((!this.mState.mRunSimpleAnimations) || (i == 0) || (this.mDataSetHasChangedAfterLayout) || (!predictiveItemAnimationsEnabled())) {
        break label191;
      }
    }
    label180:
    label185:
    label191:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      localState.mRunPredictiveAnimations = bool2;
      return;
      this.mAdapterHelper.consumeUpdatesInOnePass();
      break;
      i = 0;
      break label61;
      bool2 = false;
      break label121;
    }
  }
  
  private void pullGlows(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    int i = 0;
    if (paramFloat2 < 0.0F)
    {
      ensureLeftGlow();
      EdgeEffectCompat.onPull(this.mLeftGlow, -paramFloat2 / getWidth(), 1.0F - paramFloat3 / getHeight());
      i = 1;
      if (paramFloat4 >= 0.0F) {
        break label137;
      }
      ensureTopGlow();
      EdgeEffectCompat.onPull(this.mTopGlow, -paramFloat4 / getHeight(), paramFloat1 / getWidth());
      i = 1;
    }
    for (;;)
    {
      if ((i != 0) || (paramFloat2 != 0.0F) || (paramFloat4 != 0.0F)) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return;
      if (paramFloat2 <= 0.0F) {
        break;
      }
      ensureRightGlow();
      EdgeEffectCompat.onPull(this.mRightGlow, paramFloat2 / getWidth(), paramFloat3 / getHeight());
      i = 1;
      break;
      label137:
      if (paramFloat4 > 0.0F)
      {
        ensureBottomGlow();
        EdgeEffectCompat.onPull(this.mBottomGlow, paramFloat4 / getHeight(), 1.0F - paramFloat1 / getWidth());
        i = 1;
      }
    }
  }
  
  private void recoverFocusFromState()
  {
    if ((!this.mPreserveFocusAfterLayout) || (this.mAdapter == null) || (!hasFocus()) || (getDescendantFocusability() == 393216) || ((getDescendantFocusability() == 131072) && (isFocused()))) {}
    label263:
    for (;;)
    {
      return;
      if (!isFocused())
      {
        localObject1 = getFocusedChild();
        if ((IGNORE_DETACHED_FOCUSED_CHILD) && ((((View)localObject1).getParent() == null) || (!((View)localObject1).hasFocus())))
        {
          if (this.mChildHelper.getChildCount() == 0) {
            requestFocus();
          }
        }
        else {
          if (!this.mChildHelper.isHidden((View)localObject1)) {
            continue;
          }
        }
      }
      Object localObject2 = null;
      Object localObject1 = localObject2;
      if (this.mState.mFocusedItemId != -1L)
      {
        localObject1 = localObject2;
        if (this.mAdapter.hasStableIds()) {
          localObject1 = findViewHolderForItemId(this.mState.mFocusedItemId);
        }
      }
      localObject2 = null;
      if ((localObject1 == null) || (this.mChildHelper.isHidden(((ViewHolder)localObject1).itemView)) || (!((ViewHolder)localObject1).itemView.hasFocusable()))
      {
        localObject1 = localObject2;
        if (this.mChildHelper.getChildCount() <= 0) {}
      }
      for (localObject1 = findNextViewToFocus();; localObject1 = ((ViewHolder)localObject1).itemView)
      {
        if (localObject1 == null) {
          break label263;
        }
        localObject2 = localObject1;
        if (this.mState.mFocusedSubChildId != -1L)
        {
          View localView = ((View)localObject1).findViewById(this.mState.mFocusedSubChildId);
          localObject2 = localObject1;
          if (localView != null)
          {
            localObject2 = localObject1;
            if (localView.isFocusable()) {
              localObject2 = localView;
            }
          }
        }
        ((View)localObject2).requestFocus();
        break;
      }
    }
  }
  
  private void releaseGlows()
  {
    boolean bool1 = false;
    if (this.mLeftGlow != null)
    {
      this.mLeftGlow.onRelease();
      bool1 = this.mLeftGlow.isFinished();
    }
    boolean bool2 = bool1;
    if (this.mTopGlow != null)
    {
      this.mTopGlow.onRelease();
      bool2 = bool1 | this.mTopGlow.isFinished();
    }
    bool1 = bool2;
    if (this.mRightGlow != null)
    {
      this.mRightGlow.onRelease();
      bool1 = bool2 | this.mRightGlow.isFinished();
    }
    bool2 = bool1;
    if (this.mBottomGlow != null)
    {
      this.mBottomGlow.onRelease();
      bool2 = bool1 | this.mBottomGlow.isFinished();
    }
    if (bool2) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  private void requestChildOnScreen(View paramView1, View paramView2)
  {
    boolean bool1 = true;
    Object localObject1;
    Object localObject2;
    boolean bool2;
    if (paramView2 != null)
    {
      localObject1 = paramView2;
      this.mTempRect.set(0, 0, ((View)localObject1).getWidth(), ((View)localObject1).getHeight());
      localObject1 = ((View)localObject1).getLayoutParams();
      if ((localObject1 instanceof LayoutParams))
      {
        localObject1 = (LayoutParams)localObject1;
        if (!((LayoutParams)localObject1).mInsetsDirty)
        {
          localObject1 = ((LayoutParams)localObject1).mDecorInsets;
          localObject2 = this.mTempRect;
          ((Rect)localObject2).left -= ((Rect)localObject1).left;
          localObject2 = this.mTempRect;
          ((Rect)localObject2).right += ((Rect)localObject1).right;
          localObject2 = this.mTempRect;
          ((Rect)localObject2).top -= ((Rect)localObject1).top;
          localObject2 = this.mTempRect;
          ((Rect)localObject2).bottom += ((Rect)localObject1).bottom;
        }
      }
      if (paramView2 != null)
      {
        offsetDescendantRectToMyCoords(paramView2, this.mTempRect);
        offsetRectIntoDescendantCoords(paramView1, this.mTempRect);
      }
      localObject2 = this.mLayout;
      localObject1 = this.mTempRect;
      if (this.mFirstLayoutComplete) {
        break label221;
      }
      bool2 = true;
      label197:
      if (paramView2 != null) {
        break label227;
      }
    }
    for (;;)
    {
      ((LayoutManager)localObject2).requestChildRectangleOnScreen(this, paramView1, (Rect)localObject1, bool2, bool1);
      return;
      localObject1 = paramView1;
      break;
      label221:
      bool2 = false;
      break label197;
      label227:
      bool1 = false;
    }
  }
  
  private void resetFocusInfo()
  {
    this.mState.mFocusedItemId = -1L;
    this.mState.mFocusedItemPosition = -1;
    this.mState.mFocusedSubChildId = -1;
  }
  
  private void resetTouch()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.clear();
    }
    stopNestedScroll(0);
    releaseGlows();
  }
  
  private void saveFocusInfo()
  {
    State localState = null;
    Object localObject = localState;
    if (this.mPreserveFocusAfterLayout)
    {
      localObject = localState;
      if (hasFocus())
      {
        localObject = localState;
        if (this.mAdapter != null) {
          localObject = getFocusedChild();
        }
      }
    }
    if (localObject == null) {}
    for (localObject = null; localObject == null; localObject = findContainingViewHolder((View)localObject))
    {
      resetFocusInfo();
      return;
    }
    localState = this.mState;
    long l;
    label78:
    int i;
    if (this.mAdapter.hasStableIds())
    {
      l = ((ViewHolder)localObject).getItemId();
      localState.mFocusedItemId = l;
      localState = this.mState;
      if (!this.mDataSetHasChangedAfterLayout) {
        break label129;
      }
      i = -1;
    }
    for (;;)
    {
      localState.mFocusedItemPosition = i;
      this.mState.mFocusedSubChildId = getDeepestFocusedViewWithId(((ViewHolder)localObject).itemView);
      break;
      l = -1L;
      break label78;
      label129:
      if (((ViewHolder)localObject).isRemoved()) {
        i = ((ViewHolder)localObject).mOldPosition;
      } else {
        i = ((ViewHolder)localObject).getAdapterPosition();
      }
    }
  }
  
  private void setAdapterInternal(Adapter paramAdapter, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mAdapter != null)
    {
      this.mAdapter.unregisterAdapterDataObserver(this.mObserver);
      this.mAdapter.onDetachedFromRecyclerView(this);
    }
    if ((!paramBoolean1) || (paramBoolean2)) {
      removeAndRecycleViews();
    }
    this.mAdapterHelper.reset();
    Adapter localAdapter = this.mAdapter;
    this.mAdapter = paramAdapter;
    if (paramAdapter != null)
    {
      paramAdapter.registerAdapterDataObserver(this.mObserver);
      paramAdapter.onAttachedToRecyclerView(this);
    }
    if (this.mLayout != null) {
      this.mLayout.onAdapterChanged(localAdapter, this.mAdapter);
    }
    this.mRecycler.onAdapterChanged(localAdapter, this.mAdapter, paramBoolean1);
    this.mState.mStructureChanged = true;
  }
  
  private void stopScrollersInternal()
  {
    this.mViewFlinger.stop();
    if (this.mLayout != null) {
      this.mLayout.stopSmoothScroller();
    }
  }
  
  void absorbGlows(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0)
    {
      ensureLeftGlow();
      this.mLeftGlow.onAbsorb(-paramInt1);
      if (paramInt2 >= 0) {
        break label66;
      }
      ensureTopGlow();
      this.mTopGlow.onAbsorb(-paramInt2);
    }
    for (;;)
    {
      if ((paramInt1 != 0) || (paramInt2 != 0)) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return;
      if (paramInt1 <= 0) {
        break;
      }
      ensureRightGlow();
      this.mRightGlow.onAbsorb(paramInt1);
      break;
      label66:
      if (paramInt2 > 0)
      {
        ensureBottomGlow();
        this.mBottomGlow.onAbsorb(paramInt2);
      }
    }
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if ((this.mLayout == null) || (!this.mLayout.onAddFocusables(this, paramArrayList, paramInt1, paramInt2))) {
      super.addFocusables(paramArrayList, paramInt1, paramInt2);
    }
  }
  
  public void addItemDecoration(ItemDecoration paramItemDecoration)
  {
    addItemDecoration(paramItemDecoration, -1);
  }
  
  public void addItemDecoration(ItemDecoration paramItemDecoration, int paramInt)
  {
    if (this.mLayout != null) {
      this.mLayout.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
    }
    if (this.mItemDecorations.isEmpty()) {
      setWillNotDraw(false);
    }
    if (paramInt < 0) {
      this.mItemDecorations.add(paramItemDecoration);
    }
    for (;;)
    {
      markItemDecorInsetsDirty();
      requestLayout();
      return;
      this.mItemDecorations.add(paramInt, paramItemDecoration);
    }
  }
  
  public void addOnChildAttachStateChangeListener(OnChildAttachStateChangeListener paramOnChildAttachStateChangeListener)
  {
    if (this.mOnChildAttachStateListeners == null) {
      this.mOnChildAttachStateListeners = new ArrayList();
    }
    this.mOnChildAttachStateListeners.add(paramOnChildAttachStateChangeListener);
  }
  
  public void addOnItemTouchListener(OnItemTouchListener paramOnItemTouchListener)
  {
    this.mOnItemTouchListeners.add(paramOnItemTouchListener);
  }
  
  public void addOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    if (this.mScrollListeners == null) {
      this.mScrollListeners = new ArrayList();
    }
    this.mScrollListeners.add(paramOnScrollListener);
  }
  
  void animateAppearance(ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    paramViewHolder.setIsRecyclable(false);
    if (this.mItemAnimator.animateAppearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2)) {
      postAnimationRunner();
    }
  }
  
  void animateDisappearance(ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    addAnimatingView(paramViewHolder);
    paramViewHolder.setIsRecyclable(false);
    if (this.mItemAnimator.animateDisappearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2)) {
      postAnimationRunner();
    }
  }
  
  void applyEdgeEffectColor(EdgeEffect paramEdgeEffect)
  {
    if ((paramEdgeEffect != null) && (Build.VERSION.SDK_INT >= 21) && (this.glowColor != 0)) {
      paramEdgeEffect.setColor(this.glowColor);
    }
  }
  
  void assertInLayoutOrScroll(String paramString)
  {
    if (!isComputingLayout())
    {
      if (paramString == null) {
        throw new IllegalStateException("Cannot call this method unless RecyclerView is computing a layout or scrolling" + exceptionLabel());
      }
      throw new IllegalStateException(paramString + exceptionLabel());
    }
  }
  
  void assertNotInLayoutOrScroll(String paramString)
  {
    if (isComputingLayout())
    {
      if (paramString == null) {
        throw new IllegalStateException("Cannot call this method while RecyclerView is computing a layout or scrolling" + exceptionLabel());
      }
      throw new IllegalStateException(paramString);
    }
    if (this.mDispatchScrollCounter > 0) {
      Log.w("RecyclerView", "Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data. Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame.", new IllegalStateException("" + exceptionLabel()));
    }
  }
  
  boolean canReuseUpdatedViewHolder(ViewHolder paramViewHolder)
  {
    if ((this.mItemAnimator == null) || (this.mItemAnimator.canReuseUpdatedViewHolder(paramViewHolder, paramViewHolder.getUnmodifiedPayloads()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (((paramLayoutParams instanceof LayoutParams)) && (this.mLayout.checkLayoutParams((LayoutParams)paramLayoutParams))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  void clearOldPositions()
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if (!localViewHolder.shouldIgnore()) {
        localViewHolder.clearOldPosition();
      }
    }
    this.mRecycler.clearOldPositions();
  }
  
  public void clearOnChildAttachStateChangeListeners()
  {
    if (this.mOnChildAttachStateListeners != null) {
      this.mOnChildAttachStateListeners.clear();
    }
  }
  
  public void clearOnScrollListeners()
  {
    if (this.mScrollListeners != null) {
      this.mScrollListeners.clear();
    }
  }
  
  public int computeHorizontalScrollExtent()
  {
    int i = 0;
    if (this.mLayout == null) {}
    for (;;)
    {
      return i;
      if (this.mLayout.canScrollHorizontally()) {
        i = this.mLayout.computeHorizontalScrollExtent(this.mState);
      }
    }
  }
  
  public int computeHorizontalScrollOffset()
  {
    int i = 0;
    if (this.mLayout == null) {}
    for (;;)
    {
      return i;
      if (this.mLayout.canScrollHorizontally()) {
        i = this.mLayout.computeHorizontalScrollOffset(this.mState);
      }
    }
  }
  
  public int computeHorizontalScrollRange()
  {
    int i = 0;
    if (this.mLayout == null) {}
    for (;;)
    {
      return i;
      if (this.mLayout.canScrollHorizontally()) {
        i = this.mLayout.computeHorizontalScrollRange(this.mState);
      }
    }
  }
  
  public int computeVerticalScrollExtent()
  {
    int i = 0;
    if (this.mLayout == null) {}
    for (;;)
    {
      return i;
      if (this.mLayout.canScrollVertically()) {
        i = this.mLayout.computeVerticalScrollExtent(this.mState);
      }
    }
  }
  
  public int computeVerticalScrollOffset()
  {
    int i = 0;
    if (this.mLayout == null) {}
    for (;;)
    {
      return i;
      if (this.mLayout.canScrollVertically()) {
        i = this.mLayout.computeVerticalScrollOffset(this.mState);
      }
    }
  }
  
  public int computeVerticalScrollRange()
  {
    int i = 0;
    if (this.mLayout == null) {}
    for (;;)
    {
      return i;
      if (this.mLayout.canScrollVertically()) {
        i = this.mLayout.computeVerticalScrollRange(this.mState);
      }
    }
  }
  
  void considerReleasingGlowsOnScroll(int paramInt1, int paramInt2)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (this.mLeftGlow != null)
    {
      bool2 = bool1;
      if (!this.mLeftGlow.isFinished())
      {
        bool2 = bool1;
        if (paramInt1 > 0)
        {
          this.mLeftGlow.onRelease();
          bool2 = this.mLeftGlow.isFinished();
        }
      }
    }
    bool1 = bool2;
    if (this.mRightGlow != null)
    {
      bool1 = bool2;
      if (!this.mRightGlow.isFinished())
      {
        bool1 = bool2;
        if (paramInt1 < 0)
        {
          this.mRightGlow.onRelease();
          bool1 = bool2 | this.mRightGlow.isFinished();
        }
      }
    }
    bool2 = bool1;
    if (this.mTopGlow != null)
    {
      bool2 = bool1;
      if (!this.mTopGlow.isFinished())
      {
        bool2 = bool1;
        if (paramInt2 > 0)
        {
          this.mTopGlow.onRelease();
          bool2 = bool1 | this.mTopGlow.isFinished();
        }
      }
    }
    bool1 = bool2;
    if (this.mBottomGlow != null)
    {
      bool1 = bool2;
      if (!this.mBottomGlow.isFinished())
      {
        bool1 = bool2;
        if (paramInt2 < 0)
        {
          this.mBottomGlow.onRelease();
          bool1 = bool2 | this.mBottomGlow.isFinished();
        }
      }
    }
    if (bool1) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  void consumePendingUpdateOperations()
  {
    if ((!this.mFirstLayoutComplete) || (this.mDataSetHasChangedAfterLayout))
    {
      TraceCompat.beginSection("RV FullInvalidate");
      dispatchLayout();
      TraceCompat.endSection();
    }
    for (;;)
    {
      return;
      if (this.mAdapterHelper.hasPendingUpdates())
      {
        if ((this.mAdapterHelper.hasAnyUpdateTypes(4)) && (!this.mAdapterHelper.hasAnyUpdateTypes(11)))
        {
          TraceCompat.beginSection("RV PartialInvalidate");
          startInterceptRequestLayout();
          onEnterLayoutOrScroll();
          this.mAdapterHelper.preProcess();
          if (!this.mLayoutWasDefered)
          {
            if (!hasUpdatedView()) {
              break label113;
            }
            dispatchLayout();
          }
          for (;;)
          {
            stopInterceptRequestLayout(true);
            onExitLayoutOrScroll();
            TraceCompat.endSection();
            break;
            label113:
            this.mAdapterHelper.consumePostponedUpdates();
          }
        }
        if (this.mAdapterHelper.hasPendingUpdates())
        {
          TraceCompat.beginSection("RV FullInvalidate");
          dispatchLayout();
          TraceCompat.endSection();
        }
      }
    }
  }
  
  void defaultOnMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(LayoutManager.chooseSize(paramInt1, getPaddingLeft() + getPaddingRight(), ViewCompat.getMinimumWidth(this)), LayoutManager.chooseSize(paramInt2, getPaddingTop() + getPaddingBottom(), ViewCompat.getMinimumHeight(this)));
  }
  
  void dispatchChildAttached(View paramView)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    onChildAttachedToWindow(paramView);
    if ((this.mAdapter != null) && (localViewHolder != null)) {
      this.mAdapter.onViewAttachedToWindow(localViewHolder);
    }
    if (this.mOnChildAttachStateListeners != null) {
      for (int i = this.mOnChildAttachStateListeners.size() - 1; i >= 0; i--) {
        ((OnChildAttachStateChangeListener)this.mOnChildAttachStateListeners.get(i)).onChildViewAttachedToWindow(paramView);
      }
    }
  }
  
  void dispatchChildDetached(View paramView)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    onChildDetachedFromWindow(paramView);
    if ((this.mAdapter != null) && (localViewHolder != null)) {
      this.mAdapter.onViewDetachedFromWindow(localViewHolder);
    }
    if (this.mOnChildAttachStateListeners != null) {
      for (int i = this.mOnChildAttachStateListeners.size() - 1; i >= 0; i--) {
        ((OnChildAttachStateChangeListener)this.mOnChildAttachStateListeners.get(i)).onChildViewDetachedFromWindow(paramView);
      }
    }
  }
  
  void dispatchLayout()
  {
    if (this.mAdapter == null) {
      Log.e("RecyclerView", "No adapter attached; skipping layout");
    }
    for (;;)
    {
      return;
      if (this.mLayout != null) {
        break;
      }
      Log.e("RecyclerView", "No layout manager attached; skipping layout");
    }
    this.mState.mIsMeasuring = false;
    if (this.mState.mLayoutStep == 1)
    {
      dispatchLayoutStep1();
      this.mLayout.setExactMeasureSpecsFrom(this);
      dispatchLayoutStep2();
    }
    for (;;)
    {
      dispatchLayoutStep3();
      break;
      if ((this.mAdapterHelper.hasUpdates()) || (this.mLayout.getWidth() != getWidth()) || (this.mLayout.getHeight() != getHeight()))
      {
        this.mLayout.setExactMeasureSpecsFrom(this);
        dispatchLayoutStep2();
      }
      else
      {
        this.mLayout.setExactMeasureSpecsFrom(this);
      }
    }
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return getScrollingChildHelper().dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    return getScrollingChildHelper().dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    return getScrollingChildHelper().dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3)
  {
    return getScrollingChildHelper().dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2, paramInt3);
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    return getScrollingChildHelper().dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5)
  {
    return getScrollingChildHelper().dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5);
  }
  
  void dispatchOnScrollStateChanged(int paramInt)
  {
    if (this.mLayout != null) {
      this.mLayout.onScrollStateChanged(paramInt);
    }
    onScrollStateChanged(paramInt);
    if (this.mScrollListener != null) {
      this.mScrollListener.onScrollStateChanged(this, paramInt);
    }
    if (this.mScrollListeners != null) {
      for (int i = this.mScrollListeners.size() - 1; i >= 0; i--) {
        ((OnScrollListener)this.mScrollListeners.get(i)).onScrollStateChanged(this, paramInt);
      }
    }
  }
  
  void dispatchOnScrolled(int paramInt1, int paramInt2)
  {
    this.mDispatchScrollCounter += 1;
    int i = getScrollX();
    int j = getScrollY();
    onScrollChanged(i, j, i, j);
    onScrolled(paramInt1, paramInt2);
    if (this.mScrollListener != null) {
      this.mScrollListener.onScrolled(this, paramInt1, paramInt2);
    }
    if (this.mScrollListeners != null) {
      for (j = this.mScrollListeners.size() - 1; j >= 0; j--) {
        ((OnScrollListener)this.mScrollListeners.get(j)).onScrolled(this, paramInt1, paramInt2);
      }
    }
    this.mDispatchScrollCounter -= 1;
  }
  
  void dispatchPendingImportantForAccessibilityChanges()
  {
    int i = this.mPendingAccessibilityImportanceChange.size() - 1;
    if (i >= 0)
    {
      ViewHolder localViewHolder = (ViewHolder)this.mPendingAccessibilityImportanceChange.get(i);
      if ((localViewHolder.itemView.getParent() != this) || (localViewHolder.shouldIgnore())) {}
      for (;;)
      {
        i--;
        break;
        int j = localViewHolder.mPendingAccessibilityState;
        if (j != -1)
        {
          ViewCompat.setImportantForAccessibility(localViewHolder.itemView, j);
          localViewHolder.mPendingAccessibilityState = -1;
        }
      }
    }
    this.mPendingAccessibilityImportanceChange.clear();
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchThawSelfOnly(paramSparseArray);
  }
  
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchFreezeSelfOnly(paramSparseArray);
  }
  
  public void draw(Canvas paramCanvas)
  {
    int i = 1;
    super.draw(paramCanvas);
    int j = this.mItemDecorations.size();
    for (int k = 0; k < j; k++) {
      ((ItemDecoration)this.mItemDecorations.get(k)).onDrawOver(paramCanvas, this, this.mState);
    }
    k = 0;
    j = k;
    int m;
    if (this.mLeftGlow != null)
    {
      j = k;
      if (!this.mLeftGlow.isFinished())
      {
        m = paramCanvas.save();
        if (!this.mClipToPadding) {
          break label489;
        }
        k = getPaddingBottom();
        paramCanvas.rotate(270.0F);
        paramCanvas.translate(-getHeight() + k, 0.0F);
        if ((this.mLeftGlow == null) || (!this.mLeftGlow.draw(paramCanvas))) {
          break label495;
        }
        j = 1;
        label137:
        paramCanvas.restoreToCount(m);
      }
    }
    k = j;
    if (this.mTopGlow != null)
    {
      k = j;
      if (!this.mTopGlow.isFinished())
      {
        m = paramCanvas.save();
        if (this.mClipToPadding) {
          paramCanvas.translate(getPaddingLeft(), getPaddingTop());
        }
        paramCanvas.translate(0.0F, this.topGlowOffset);
        if ((this.mTopGlow == null) || (!this.mTopGlow.draw(paramCanvas))) {
          break label500;
        }
        k = 1;
        label224:
        k = j | k;
        paramCanvas.restoreToCount(m);
      }
    }
    j = k;
    if (this.mRightGlow != null)
    {
      j = k;
      if (!this.mRightGlow.isFinished())
      {
        m = paramCanvas.save();
        int n = getWidth();
        if (!this.mClipToPadding) {
          break label506;
        }
        j = getPaddingTop();
        label283:
        paramCanvas.rotate(90.0F);
        paramCanvas.translate(-j, -n);
        if ((this.mRightGlow == null) || (!this.mRightGlow.draw(paramCanvas))) {
          break label511;
        }
        j = 1;
        label321:
        j = k | j;
        paramCanvas.restoreToCount(m);
      }
    }
    k = j;
    if (this.mBottomGlow != null)
    {
      k = j;
      if (!this.mBottomGlow.isFinished())
      {
        m = paramCanvas.save();
        paramCanvas.rotate(180.0F);
        if (!this.mClipToPadding) {
          break label516;
        }
        paramCanvas.translate(-getWidth() + getPaddingRight(), -getHeight() + getPaddingBottom());
        label401:
        if ((this.mBottomGlow == null) || (!this.mBottomGlow.draw(paramCanvas))) {
          break label540;
        }
      }
    }
    label489:
    label495:
    label500:
    label506:
    label511:
    label516:
    label540:
    for (k = i;; k = 0)
    {
      k = j | k;
      paramCanvas.restoreToCount(m);
      j = k;
      if (k == 0)
      {
        j = k;
        if (this.mItemAnimator != null)
        {
          j = k;
          if (this.mItemDecorations.size() > 0)
          {
            j = k;
            if (this.mItemAnimator.isRunning()) {
              j = 1;
            }
          }
        }
      }
      if (j != 0) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return;
      k = 0;
      break;
      j = 0;
      break label137;
      k = 0;
      break label224;
      j = 0;
      break label283;
      j = 0;
      break label321;
      paramCanvas.translate(-getWidth(), -getHeight() + this.bottomGlowOffset);
      break label401;
    }
  }
  
  public boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    return super.drawChild(paramCanvas, paramView, paramLong);
  }
  
  void ensureBottomGlow()
  {
    if (this.mBottomGlow != null) {
      return;
    }
    this.mBottomGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 3);
    if (this.mClipToPadding) {
      this.mBottomGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
    }
    for (;;)
    {
      applyEdgeEffectColor(this.mBottomGlow);
      break;
      this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
    }
  }
  
  void ensureLeftGlow()
  {
    if (this.mLeftGlow != null) {
      return;
    }
    this.mLeftGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 0);
    if (this.mClipToPadding) {
      this.mLeftGlow.setSize(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
    }
    for (;;)
    {
      applyEdgeEffectColor(this.mLeftGlow);
      break;
      this.mLeftGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
    }
  }
  
  void ensureRightGlow()
  {
    if (this.mRightGlow != null) {
      return;
    }
    this.mRightGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 2);
    if (this.mClipToPadding) {
      this.mRightGlow.setSize(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
    }
    for (;;)
    {
      applyEdgeEffectColor(this.mRightGlow);
      break;
      this.mRightGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
    }
  }
  
  void ensureTopGlow()
  {
    if (this.mTopGlow != null) {
      return;
    }
    this.mTopGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 1);
    if (this.mClipToPadding) {
      this.mTopGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
    }
    for (;;)
    {
      applyEdgeEffectColor(this.mTopGlow);
      break;
      this.mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
    }
  }
  
  String exceptionLabel()
  {
    return " " + super.toString() + ", adapter:" + this.mAdapter + ", layout:" + this.mLayout + ", context:" + getContext();
  }
  
  final void fillRemainingScrollValues(State paramState)
  {
    OverScroller localOverScroller;
    if (getScrollState() == 2)
    {
      localOverScroller = this.mViewFlinger.mScroller;
      paramState.mRemainingScrollHorizontal = (localOverScroller.getFinalX() - localOverScroller.getCurrX());
    }
    for (paramState.mRemainingScrollVertical = (localOverScroller.getFinalY() - localOverScroller.getCurrY());; paramState.mRemainingScrollVertical = 0)
    {
      return;
      paramState.mRemainingScrollHorizontal = 0;
    }
  }
  
  public View findChildViewUnder(float paramFloat1, float paramFloat2)
  {
    int i = this.mChildHelper.getChildCount() - 1;
    View localView;
    if (i >= 0)
    {
      localView = this.mChildHelper.getChildAt(i);
      float f1 = localView.getTranslationX();
      float f2 = localView.getTranslationY();
      if ((paramFloat1 < localView.getLeft() + f1) || (paramFloat1 > localView.getRight() + f1) || (paramFloat2 < localView.getTop() + f2) || (paramFloat2 > localView.getBottom() + f2)) {}
    }
    for (;;)
    {
      return localView;
      i--;
      break;
      localView = null;
    }
  }
  
  public View findContainingItemView(View paramView)
  {
    for (ViewParent localViewParent = paramView.getParent(); (localViewParent != null) && (localViewParent != this) && ((localViewParent instanceof View)); localViewParent = paramView.getParent()) {
      paramView = (View)localViewParent;
    }
    if (localViewParent == this) {}
    for (;;)
    {
      return paramView;
      paramView = null;
    }
  }
  
  public ViewHolder findContainingViewHolder(View paramView)
  {
    paramView = findContainingItemView(paramView);
    if (paramView == null) {}
    for (paramView = null;; paramView = getChildViewHolder(paramView)) {
      return paramView;
    }
  }
  
  public ViewHolder findViewHolderForAdapterPosition(int paramInt)
  {
    Object localObject1;
    if (this.mDataSetHasChangedAfterLayout) {
      localObject1 = null;
    }
    label107:
    for (;;)
    {
      return (ViewHolder)localObject1;
      int i = this.mChildHelper.getUnfilteredChildCount();
      localObject1 = null;
      int j = 0;
      for (;;)
      {
        if (j >= i) {
          break label107;
        }
        ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
        Object localObject2 = localObject1;
        if (localViewHolder != null)
        {
          localObject2 = localObject1;
          if (!localViewHolder.isRemoved())
          {
            localObject2 = localObject1;
            if (getAdapterPositionFor(localViewHolder) == paramInt)
            {
              localObject1 = localViewHolder;
              if (!this.mChildHelper.isHidden(localViewHolder.itemView)) {
                break;
              }
              localObject2 = localViewHolder;
            }
          }
        }
        j++;
        localObject1 = localObject2;
      }
    }
  }
  
  public ViewHolder findViewHolderForItemId(long paramLong)
  {
    Object localObject1;
    if ((this.mAdapter == null) || (!this.mAdapter.hasStableIds())) {
      localObject1 = null;
    }
    label119:
    for (;;)
    {
      return (ViewHolder)localObject1;
      int i = this.mChildHelper.getUnfilteredChildCount();
      localObject1 = null;
      int j = 0;
      for (;;)
      {
        if (j >= i) {
          break label119;
        }
        ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
        Object localObject2 = localObject1;
        if (localViewHolder != null)
        {
          localObject2 = localObject1;
          if (!localViewHolder.isRemoved())
          {
            localObject2 = localObject1;
            if (localViewHolder.getItemId() == paramLong)
            {
              localObject1 = localViewHolder;
              if (!this.mChildHelper.isHidden(localViewHolder.itemView)) {
                break;
              }
              localObject2 = localViewHolder;
            }
          }
        }
        j++;
        localObject1 = localObject2;
      }
    }
  }
  
  public ViewHolder findViewHolderForLayoutPosition(int paramInt)
  {
    return findViewHolderForPosition(paramInt, false);
  }
  
  @Deprecated
  public ViewHolder findViewHolderForPosition(int paramInt)
  {
    return findViewHolderForPosition(paramInt, false);
  }
  
  ViewHolder findViewHolderForPosition(int paramInt, boolean paramBoolean)
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    Object localObject1 = null;
    int j = 0;
    if (j < i)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      Object localObject2 = localObject1;
      if (localViewHolder != null)
      {
        localObject2 = localObject1;
        if (!localViewHolder.isRemoved())
        {
          if (!paramBoolean) {
            break label82;
          }
          if (localViewHolder.mPosition == paramInt) {
            break label95;
          }
          localObject2 = localObject1;
        }
      }
      for (;;)
      {
        j++;
        localObject1 = localObject2;
        break;
        label82:
        localObject2 = localObject1;
        if (localViewHolder.getLayoutPosition() == paramInt)
        {
          label95:
          localObject1 = localViewHolder;
          if (!this.mChildHelper.isHidden(localViewHolder.itemView)) {
            break label121;
          }
          localObject2 = localViewHolder;
        }
      }
    }
    label121:
    return (ViewHolder)localObject1;
  }
  
  public boolean fling(int paramInt1, int paramInt2)
  {
    boolean bool1 = false;
    boolean bool2;
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (!this.mLayoutFrozen)
      {
        boolean bool3 = this.mLayout.canScrollHorizontally();
        boolean bool4 = this.mLayout.canScrollVertically();
        int i;
        if (bool3)
        {
          i = paramInt1;
          if (Math.abs(paramInt1) >= this.mMinFlingVelocity) {}
        }
        else
        {
          i = 0;
        }
        int j;
        if (bool4)
        {
          j = paramInt2;
          if (Math.abs(paramInt2) >= this.mMinFlingVelocity) {}
        }
        else
        {
          j = 0;
        }
        if (i == 0)
        {
          bool2 = bool1;
          if (j == 0) {}
        }
        else
        {
          bool2 = bool1;
          if (!dispatchNestedPreFling(i, j))
          {
            if ((bool3) || (bool4)) {}
            for (boolean bool5 = true;; bool5 = false)
            {
              dispatchNestedFling(i, j, bool5);
              if ((this.mOnFlingListener == null) || (!this.mOnFlingListener.onFling(i, j))) {
                break label184;
              }
              bool2 = true;
              break;
            }
            label184:
            bool2 = bool1;
            if (bool5)
            {
              paramInt1 = 0;
              if (bool3) {
                paramInt1 = 0x0 | 0x1;
              }
              paramInt2 = paramInt1;
              if (bool4) {
                paramInt2 = paramInt1 | 0x2;
              }
              startNestedScroll(paramInt2, 1);
              paramInt1 = Math.max(-this.mMaxFlingVelocity, Math.min(i, this.mMaxFlingVelocity));
              paramInt2 = Math.max(-this.mMaxFlingVelocity, Math.min(j, this.mMaxFlingVelocity));
              this.mViewFlinger.fling(paramInt1, paramInt2);
              bool2 = true;
            }
          }
        }
      }
    }
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    Object localObject = this.mLayout.onInterceptFocusSearch(paramView, paramInt);
    if (localObject != null) {
      paramView = (View)localObject;
    }
    label49:
    int k;
    for (;;)
    {
      return paramView;
      int i;
      label94:
      int m;
      if ((this.mAdapter != null) && (this.mLayout != null) && (!isComputingLayout()) && (!this.mLayoutFrozen))
      {
        i = 1;
        localObject = FocusFinder.getInstance();
        if ((i == 0) || ((paramInt != 2) && (paramInt != 1))) {
          break label356;
        }
        j = 0;
        i = paramInt;
        if (this.mLayout.canScrollVertically())
        {
          if (paramInt != 2) {
            break label254;
          }
          k = 130;
          if (((FocusFinder)localObject).findNextFocus(this, paramView, k) != null) {
            break label261;
          }
          m = 1;
          label108:
          j = m;
          i = paramInt;
          if (FORCE_ABS_FOCUS_SEARCH_DIRECTION)
          {
            i = k;
            j = m;
          }
        }
        m = j;
        k = i;
        if (j == 0)
        {
          m = j;
          k = i;
          if (this.mLayout.canScrollHorizontally())
          {
            if (this.mLayout.getLayoutDirection() != 1) {
              break label267;
            }
            paramInt = 1;
            label173:
            if (i != 2) {
              break label272;
            }
            j = 1;
            label182:
            if ((j ^ paramInt) == 0) {
              break label278;
            }
            paramInt = 66;
            label192:
            if (((FocusFinder)localObject).findNextFocus(this, paramView, paramInt) != null) {
              break label284;
            }
          }
        }
      }
      label254:
      label261:
      label267:
      label272:
      label278:
      label284:
      for (int j = 1;; j = 0)
      {
        m = j;
        k = i;
        if (FORCE_ABS_FOCUS_SEARCH_DIRECTION)
        {
          k = paramInt;
          m = j;
        }
        if (m == 0) {
          break label318;
        }
        consumePendingUpdateOperations();
        if (findContainingItemView(paramView) != null) {
          break label290;
        }
        paramView = null;
        break;
        i = 0;
        break label49;
        k = 33;
        break label94;
        m = 0;
        break label108;
        paramInt = 0;
        break label173;
        j = 0;
        break label182;
        paramInt = 17;
        break label192;
      }
      label290:
      startInterceptRequestLayout();
      this.mLayout.onFocusSearchFailed(paramView, k, this.mRecycler, this.mState);
      stopInterceptRequestLayout(false);
      label318:
      localObject = ((FocusFinder)localObject).findNextFocus(this, paramView, k);
      for (;;)
      {
        if ((localObject == null) || (((View)localObject).hasFocusable())) {
          break label446;
        }
        if (getFocusedChild() != null) {
          break label437;
        }
        paramView = super.focusSearch(paramView, k);
        break;
        label356:
        View localView = ((FocusFinder)localObject).findNextFocus(this, paramView, paramInt);
        localObject = localView;
        k = paramInt;
        if (localView == null)
        {
          localObject = localView;
          k = paramInt;
          if (i != 0)
          {
            consumePendingUpdateOperations();
            if (findContainingItemView(paramView) == null)
            {
              paramView = null;
              break;
            }
            startInterceptRequestLayout();
            localObject = this.mLayout.onFocusSearchFailed(paramView, paramInt, this.mRecycler, this.mState);
            stopInterceptRequestLayout(false);
            k = paramInt;
          }
        }
      }
      label437:
      requestChildOnScreen((View)localObject, null);
    }
    label446:
    if (isPreferredNextFocus(paramView, (View)localObject, k)) {}
    for (paramView = (View)localObject;; paramView = super.focusSearch(paramView, k)) {
      break;
    }
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    if (this.mLayout == null) {
      throw new IllegalStateException("RecyclerView has no LayoutManager" + exceptionLabel());
    }
    return this.mLayout.generateDefaultLayoutParams();
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    if (this.mLayout == null) {
      throw new IllegalStateException("RecyclerView has no LayoutManager" + exceptionLabel());
    }
    return this.mLayout.generateLayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (this.mLayout == null) {
      throw new IllegalStateException("RecyclerView has no LayoutManager" + exceptionLabel());
    }
    return this.mLayout.generateLayoutParams(paramLayoutParams);
  }
  
  public Adapter getAdapter()
  {
    return this.mAdapter;
  }
  
  int getAdapterPositionFor(ViewHolder paramViewHolder)
  {
    if ((paramViewHolder.hasAnyOfTheFlags(524)) || (!paramViewHolder.isBound())) {}
    for (int i = -1;; i = this.mAdapterHelper.applyPendingUpdatesToPosition(paramViewHolder.mPosition)) {
      return i;
    }
  }
  
  public View getAttachedScrapChildAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mRecycler.mAttachedScrap.size())) {}
    for (View localView = null;; localView = this.mRecycler.getScrapViewAt(paramInt)) {
      return localView;
    }
  }
  
  public int getAttachedScrapChildCount()
  {
    return this.mRecycler.getScrapCount();
  }
  
  public int getBaseline()
  {
    if (this.mLayout != null) {}
    for (int i = this.mLayout.getBaseline();; i = super.getBaseline()) {
      return i;
    }
  }
  
  public View getCachedChildAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mRecycler.mCachedViews.size())) {}
    for (View localView = null;; localView = ((ViewHolder)this.mRecycler.mCachedViews.get(paramInt)).itemView) {
      return localView;
    }
  }
  
  public int getCachedChildCount()
  {
    return this.mRecycler.mCachedViews.size();
  }
  
  long getChangedHolderKey(ViewHolder paramViewHolder)
  {
    if (this.mAdapter.hasStableIds()) {}
    for (long l = paramViewHolder.getItemId();; l = paramViewHolder.mPosition) {
      return l;
    }
  }
  
  public int getChildAdapterPosition(View paramView)
  {
    paramView = getChildViewHolderInt(paramView);
    if (paramView != null) {}
    for (int i = paramView.getAdapterPosition();; i = -1) {
      return i;
    }
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (this.mChildDrawingOrderCallback == null) {}
    for (paramInt1 = super.getChildDrawingOrder(paramInt1, paramInt2);; paramInt1 = this.mChildDrawingOrderCallback.onGetChildDrawingOrder(paramInt1, paramInt2)) {
      return paramInt1;
    }
  }
  
  public long getChildItemId(View paramView)
  {
    long l1 = -1L;
    long l2 = l1;
    if (this.mAdapter != null)
    {
      if (this.mAdapter.hasStableIds()) {
        break label30;
      }
      l2 = l1;
    }
    for (;;)
    {
      return l2;
      label30:
      paramView = getChildViewHolderInt(paramView);
      l2 = l1;
      if (paramView != null) {
        l2 = paramView.getItemId();
      }
    }
  }
  
  public int getChildLayoutPosition(View paramView)
  {
    paramView = getChildViewHolderInt(paramView);
    if (paramView != null) {}
    for (int i = paramView.getLayoutPosition();; i = -1) {
      return i;
    }
  }
  
  @Deprecated
  public int getChildPosition(View paramView)
  {
    return getChildAdapterPosition(paramView);
  }
  
  public ViewHolder getChildViewHolder(View paramView)
  {
    ViewParent localViewParent = paramView.getParent();
    if ((localViewParent != null) && (localViewParent != this)) {
      throw new IllegalArgumentException("View " + paramView + " is not a direct child of " + this);
    }
    return getChildViewHolderInt(paramView);
  }
  
  public boolean getClipToPadding()
  {
    return this.mClipToPadding;
  }
  
  public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate()
  {
    return this.mAccessibilityDelegate;
  }
  
  public void getDecoratedBoundsWithMargins(View paramView, Rect paramRect)
  {
    getDecoratedBoundsWithMarginsInt(paramView, paramRect);
  }
  
  public EdgeEffectFactory getEdgeEffectFactory()
  {
    return this.mEdgeEffectFactory;
  }
  
  public View getHiddenChildAt(int paramInt)
  {
    return this.mChildHelper.getHiddenChildAt(paramInt);
  }
  
  public int getHiddenChildCount()
  {
    return this.mChildHelper.getHiddenChildCount();
  }
  
  public ItemAnimator getItemAnimator()
  {
    return this.mItemAnimator;
  }
  
  Rect getItemDecorInsetsForChild(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (!localLayoutParams.mInsetsDirty) {
      paramView = localLayoutParams.mDecorInsets;
    }
    for (;;)
    {
      return paramView;
      if ((this.mState.isPreLayout()) && ((localLayoutParams.isItemChanged()) || (localLayoutParams.isViewInvalid())))
      {
        paramView = localLayoutParams.mDecorInsets;
      }
      else
      {
        Rect localRect = localLayoutParams.mDecorInsets;
        localRect.set(0, 0, 0, 0);
        int i = this.mItemDecorations.size();
        for (int j = 0; j < i; j++)
        {
          this.mTempRect.set(0, 0, 0, 0);
          ((ItemDecoration)this.mItemDecorations.get(j)).getItemOffsets(this.mTempRect, paramView, this, this.mState);
          localRect.left += this.mTempRect.left;
          localRect.top += this.mTempRect.top;
          localRect.right += this.mTempRect.right;
          localRect.bottom += this.mTempRect.bottom;
        }
        localLayoutParams.mInsetsDirty = false;
        paramView = localRect;
      }
    }
  }
  
  public ItemDecoration getItemDecorationAt(int paramInt)
  {
    int i = getItemDecorationCount();
    if ((paramInt < 0) || (paramInt >= i)) {
      throw new IndexOutOfBoundsException(paramInt + " is an invalid index for size " + i);
    }
    return (ItemDecoration)this.mItemDecorations.get(paramInt);
  }
  
  public int getItemDecorationCount()
  {
    return this.mItemDecorations.size();
  }
  
  public LayoutManager getLayoutManager()
  {
    return this.mLayout;
  }
  
  public int getMaxFlingVelocity()
  {
    return this.mMaxFlingVelocity;
  }
  
  public int getMinFlingVelocity()
  {
    return this.mMinFlingVelocity;
  }
  
  long getNanoTime()
  {
    if (ALLOW_THREAD_GAP_WORK) {}
    for (long l = System.nanoTime();; l = 0L) {
      return l;
    }
  }
  
  public OnFlingListener getOnFlingListener()
  {
    return this.mOnFlingListener;
  }
  
  public boolean getPreserveFocusAfterLayout()
  {
    return this.mPreserveFocusAfterLayout;
  }
  
  public RecycledViewPool getRecycledViewPool()
  {
    return this.mRecycler.getRecycledViewPool();
  }
  
  public int getScrollState()
  {
    return this.mScrollState;
  }
  
  public boolean hasFixedSize()
  {
    return this.mHasFixedSize;
  }
  
  public boolean hasNestedScrollingParent()
  {
    return getScrollingChildHelper().hasNestedScrollingParent();
  }
  
  public boolean hasNestedScrollingParent(int paramInt)
  {
    return getScrollingChildHelper().hasNestedScrollingParent(paramInt);
  }
  
  public boolean hasPendingAdapterUpdates()
  {
    if ((!this.mFirstLayoutComplete) || (this.mDataSetHasChangedAfterLayout) || (this.mAdapterHelper.hasPendingUpdates())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  void initAdapterManager()
  {
    this.mAdapterHelper = new AdapterHelper(new AdapterHelper.Callback()
    {
      void dispatchUpdate(AdapterHelper.UpdateOp paramAnonymousUpdateOp)
      {
        switch (paramAnonymousUpdateOp.cmd)
        {
        }
        for (;;)
        {
          return;
          RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount);
          continue;
          RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount);
          continue;
          RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount, paramAnonymousUpdateOp.payload);
          continue;
          RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount, 1);
        }
      }
      
      public RecyclerView.ViewHolder findViewHolder(int paramAnonymousInt)
      {
        RecyclerView.ViewHolder localViewHolder1 = RecyclerView.this.findViewHolderForPosition(paramAnonymousInt, true);
        RecyclerView.ViewHolder localViewHolder2;
        if (localViewHolder1 == null) {
          localViewHolder2 = null;
        }
        for (;;)
        {
          return localViewHolder2;
          localViewHolder2 = localViewHolder1;
          if (RecyclerView.this.mChildHelper.isHidden(localViewHolder1.itemView)) {
            localViewHolder2 = null;
          }
        }
      }
      
      public void markViewHoldersUpdated(int paramAnonymousInt1, int paramAnonymousInt2, Object paramAnonymousObject)
      {
        RecyclerView.this.viewRangeUpdate(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousObject);
        RecyclerView.this.mItemsChanged = true;
      }
      
      public void offsetPositionsForAdd(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForInsert(paramAnonymousInt1, paramAnonymousInt2);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }
      
      public void offsetPositionsForMove(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForMove(paramAnonymousInt1, paramAnonymousInt2);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }
      
      public void offsetPositionsForRemovingInvisible(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForRemove(paramAnonymousInt1, paramAnonymousInt2, true);
        RecyclerView.this.mItemsAddedOrRemoved = true;
        RecyclerView.State localState = RecyclerView.this.mState;
        localState.mDeletedInvisibleItemCountSincePreviousLayout += paramAnonymousInt2;
      }
      
      public void offsetPositionsForRemovingLaidOutOrNewView(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForRemove(paramAnonymousInt1, paramAnonymousInt2, false);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }
      
      public void onDispatchFirstPass(AdapterHelper.UpdateOp paramAnonymousUpdateOp)
      {
        dispatchUpdate(paramAnonymousUpdateOp);
      }
      
      public void onDispatchSecondPass(AdapterHelper.UpdateOp paramAnonymousUpdateOp)
      {
        dispatchUpdate(paramAnonymousUpdateOp);
      }
    });
  }
  
  void initFastScroller(StateListDrawable paramStateListDrawable1, Drawable paramDrawable1, StateListDrawable paramStateListDrawable2, Drawable paramDrawable2)
  {
    if ((paramStateListDrawable1 == null) || (paramDrawable1 == null) || (paramStateListDrawable2 == null) || (paramDrawable2 == null)) {
      throw new IllegalArgumentException("Trying to set fast scroller without both required drawables." + exceptionLabel());
    }
    getContext().getResources();
    new FastScroller(this, paramStateListDrawable1, paramDrawable1, paramStateListDrawable2, paramDrawable2, AndroidUtilities.dp(8.0F), AndroidUtilities.dp(50.0F), 0);
  }
  
  void invalidateGlows()
  {
    this.mBottomGlow = null;
    this.mTopGlow = null;
    this.mRightGlow = null;
    this.mLeftGlow = null;
  }
  
  public void invalidateItemDecorations()
  {
    if (this.mItemDecorations.size() == 0) {}
    for (;;)
    {
      return;
      if (this.mLayout != null) {
        this.mLayout.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
      }
      markItemDecorInsetsDirty();
      requestLayout();
    }
  }
  
  boolean isAccessibilityEnabled()
  {
    if ((this.mAccessibilityManager != null) && (this.mAccessibilityManager.isEnabled())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isAnimating()
  {
    if ((this.mItemAnimator != null) && (this.mItemAnimator.isRunning())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isAttachedToWindow()
  {
    return this.mIsAttached;
  }
  
  public boolean isComputingLayout()
  {
    if (this.mLayoutOrScrollCounter > 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isLayoutFrozen()
  {
    return this.mLayoutFrozen;
  }
  
  public boolean isNestedScrollingEnabled()
  {
    return getScrollingChildHelper().isNestedScrollingEnabled();
  }
  
  void jumpToPositionForSmoothScroller(int paramInt)
  {
    if (this.mLayout == null) {}
    for (;;)
    {
      return;
      this.mLayout.scrollToPosition(paramInt);
      awakenScrollBars();
    }
  }
  
  void markItemDecorInsetsDirty()
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++) {
      ((LayoutParams)this.mChildHelper.getUnfilteredChildAt(j).getLayoutParams()).mInsetsDirty = true;
    }
    this.mRecycler.markItemDecorInsetsDirty();
  }
  
  void markKnownViewsInvalid()
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore())) {
        localViewHolder.addFlags(6);
      }
    }
    markItemDecorInsetsDirty();
    this.mRecycler.markKnownViewsInvalid();
  }
  
  public void offsetChildrenHorizontal(int paramInt)
  {
    int i = this.mChildHelper.getChildCount();
    for (int j = 0; j < i; j++) {
      this.mChildHelper.getChildAt(j).offsetLeftAndRight(paramInt);
    }
  }
  
  public void offsetChildrenVertical(int paramInt)
  {
    int i = this.mChildHelper.getChildCount();
    for (int j = 0; j < i; j++) {
      this.mChildHelper.getChildAt(j).offsetTopAndBottom(paramInt);
    }
  }
  
  void offsetPositionRecordsForInsert(int paramInt1, int paramInt2)
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore()) && (localViewHolder.mPosition >= paramInt1))
      {
        localViewHolder.offsetPosition(paramInt2, false);
        this.mState.mStructureChanged = true;
      }
    }
    this.mRecycler.offsetPositionRecordsForInsert(paramInt1, paramInt2);
    requestLayout();
  }
  
  void offsetPositionRecordsForMove(int paramInt1, int paramInt2)
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    int j;
    int k;
    if (paramInt1 < paramInt2)
    {
      j = paramInt1;
      k = paramInt2;
    }
    ViewHolder localViewHolder;
    for (int m = -1;; m = 1)
    {
      for (int n = 0;; n++)
      {
        if (n >= i) {
          break label128;
        }
        localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(n));
        if ((localViewHolder != null) && (localViewHolder.mPosition >= j) && (localViewHolder.mPosition <= k)) {
          break;
        }
      }
      j = paramInt2;
      k = paramInt1;
    }
    if (localViewHolder.mPosition == paramInt1) {
      localViewHolder.offsetPosition(paramInt2 - paramInt1, false);
    }
    for (;;)
    {
      this.mState.mStructureChanged = true;
      break;
      localViewHolder.offsetPosition(m, false);
    }
    label128:
    this.mRecycler.offsetPositionRecordsForMove(paramInt1, paramInt2);
    requestLayout();
  }
  
  void offsetPositionRecordsForRemove(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    int j = 0;
    if (j < i)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore()))
      {
        if (localViewHolder.mPosition < paramInt1 + paramInt2) {
          break label79;
        }
        localViewHolder.offsetPosition(-paramInt2, paramBoolean);
        this.mState.mStructureChanged = true;
      }
      for (;;)
      {
        j++;
        break;
        label79:
        if (localViewHolder.mPosition >= paramInt1)
        {
          localViewHolder.flagRemovedAndOffsetPosition(paramInt1 - 1, -paramInt2, paramBoolean);
          this.mState.mStructureChanged = true;
        }
      }
    }
    this.mRecycler.offsetPositionRecordsForRemove(paramInt1, paramInt2, paramBoolean);
    requestLayout();
  }
  
  protected void onAttachedToWindow()
  {
    boolean bool = true;
    super.onAttachedToWindow();
    this.mLayoutOrScrollCounter = 0;
    this.mIsAttached = true;
    if ((this.mFirstLayoutComplete) && (!isLayoutRequested())) {}
    for (;;)
    {
      this.mFirstLayoutComplete = bool;
      if (this.mLayout != null) {
        this.mLayout.dispatchAttachedToWindow(this);
      }
      this.mPostedAnimatorRunner = false;
      if (ALLOW_THREAD_GAP_WORK)
      {
        this.mGapWorker = ((GapWorker)GapWorker.sGapWorker.get());
        if (this.mGapWorker == null)
        {
          this.mGapWorker = new GapWorker();
          Display localDisplay = ViewCompat.getDisplay(this);
          float f1 = 60.0F;
          float f2 = f1;
          if (!isInEditMode())
          {
            f2 = f1;
            if (localDisplay != null)
            {
              float f3 = localDisplay.getRefreshRate();
              f2 = f1;
              if (f3 >= 30.0F) {
                f2 = f3;
              }
            }
          }
          this.mGapWorker.mFrameIntervalNs = ((1.0E9F / f2));
          GapWorker.sGapWorker.set(this.mGapWorker);
        }
        this.mGapWorker.add(this);
      }
      return;
      bool = false;
    }
  }
  
  public void onChildAttachedToWindow(View paramView) {}
  
  public void onChildDetachedFromWindow(View paramView) {}
  
  protected void onDetachedFromWindow()
  {
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
    if ((ALLOW_THREAD_GAP_WORK) && (this.mGapWorker != null))
    {
      this.mGapWorker.remove(this);
      this.mGapWorker = null;
    }
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i = this.mItemDecorations.size();
    for (int j = 0; j < i; j++) {
      ((ItemDecoration)this.mItemDecorations.get(j)).onDraw(paramCanvas, this, this.mState);
    }
  }
  
  void onEnterLayoutOrScroll()
  {
    this.mLayoutOrScrollCounter += 1;
  }
  
  void onExitLayoutOrScroll()
  {
    onExitLayoutOrScroll(true);
  }
  
  void onExitLayoutOrScroll(boolean paramBoolean)
  {
    this.mLayoutOrScrollCounter -= 1;
    if (this.mLayoutOrScrollCounter < 1)
    {
      this.mLayoutOrScrollCounter = 0;
      if (paramBoolean)
      {
        dispatchContentChangedIfNecessary();
        dispatchPendingImportantForAccessibilityChanges();
      }
    }
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if (this.mLayout == null) {}
    label52:
    label109:
    label177:
    for (;;)
    {
      return false;
      if ((!this.mLayoutFrozen) && (paramMotionEvent.getAction() == 8))
      {
        float f1;
        float f2;
        if ((paramMotionEvent.getSource() & 0x2) != 0) {
          if (this.mLayout.canScrollVertically())
          {
            f1 = -paramMotionEvent.getAxisValue(9);
            if (!this.mLayout.canScrollHorizontally()) {
              break label109;
            }
            f2 = paramMotionEvent.getAxisValue(10);
          }
        }
        for (;;)
        {
          if ((f1 == 0.0F) && (f2 == 0.0F)) {
            break label177;
          }
          scrollByInternal((int)(this.mScaledHorizontalScrollFactor * f2), (int)(this.mScaledVerticalScrollFactor * f1), paramMotionEvent);
          break;
          f1 = 0.0F;
          break label52;
          f2 = 0.0F;
          continue;
          if ((paramMotionEvent.getSource() & 0x400000) != 0)
          {
            f2 = paramMotionEvent.getAxisValue(26);
            if (this.mLayout.canScrollVertically())
            {
              f1 = -f2;
              f2 = 0.0F;
            }
            else if (this.mLayout.canScrollHorizontally())
            {
              f1 = 0.0F;
            }
            else
            {
              f1 = 0.0F;
              f2 = 0.0F;
            }
          }
          else
          {
            f1 = 0.0F;
            f2 = 0.0F;
          }
        }
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1;
    if (this.mLayoutFrozen) {
      bool1 = false;
    }
    for (;;)
    {
      return bool1;
      if (dispatchOnItemTouchIntercept(paramMotionEvent))
      {
        cancelTouch();
        bool1 = true;
      }
      else if (this.mLayout == null)
      {
        bool1 = false;
      }
      else
      {
        boolean bool2 = this.mLayout.canScrollHorizontally();
        bool1 = this.mLayout.canScrollVertically();
        if (this.mVelocityTracker == null) {
          this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(paramMotionEvent);
        int i = paramMotionEvent.getActionMasked();
        int j = paramMotionEvent.getActionIndex();
        switch (i)
        {
        }
        for (;;)
        {
          if (this.mScrollState != 1) {
            break label574;
          }
          bool1 = true;
          break;
          if (this.mIgnoreMotionEventTillDown) {
            this.mIgnoreMotionEventTillDown = false;
          }
          this.mScrollPointerId = paramMotionEvent.getPointerId(0);
          j = (int)(paramMotionEvent.getX() + 0.5F);
          this.mLastTouchX = j;
          this.mInitialTouchX = j;
          j = (int)(paramMotionEvent.getY() + 0.5F);
          this.mLastTouchY = j;
          this.mInitialTouchY = j;
          if (this.mScrollState == 2)
          {
            getParent().requestDisallowInterceptTouchEvent(true);
            setScrollState(1);
          }
          paramMotionEvent = this.mNestedOffsets;
          this.mNestedOffsets[1] = 0;
          paramMotionEvent[0] = 0;
          j = 0;
          if (bool2) {
            j = 0x0 | 0x1;
          }
          i = j;
          if (bool1) {
            i = j | 0x2;
          }
          startNestedScroll(i, 0);
          continue;
          this.mScrollPointerId = paramMotionEvent.getPointerId(j);
          i = (int)(paramMotionEvent.getX(j) + 0.5F);
          this.mLastTouchX = i;
          this.mInitialTouchX = i;
          j = (int)(paramMotionEvent.getY(j) + 0.5F);
          this.mLastTouchY = j;
          this.mInitialTouchY = j;
          continue;
          j = paramMotionEvent.findPointerIndex(this.mScrollPointerId);
          if (j < 0)
          {
            Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
            bool1 = false;
            break;
          }
          int k = (int)(paramMotionEvent.getX(j) + 0.5F);
          int m = (int)(paramMotionEvent.getY(j) + 0.5F);
          if (this.mScrollState != 1)
          {
            int n = this.mInitialTouchX;
            int i1 = this.mInitialTouchY;
            i = 0;
            j = i;
            if (bool2)
            {
              j = i;
              if (Math.abs(k - n) > this.mTouchSlop)
              {
                this.mLastTouchX = k;
                j = 1;
              }
            }
            i = j;
            if (bool1)
            {
              i = j;
              if (Math.abs(m - i1) > this.mTouchSlop)
              {
                this.mLastTouchY = m;
                i = 1;
              }
            }
            if (i != 0)
            {
              setScrollState(1);
              continue;
              onPointerUp(paramMotionEvent);
              continue;
              this.mVelocityTracker.clear();
              stopNestedScroll(0);
              continue;
              cancelTouch();
            }
          }
        }
        label574:
        bool1 = false;
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    TraceCompat.beginSection("RV OnLayout");
    dispatchLayout();
    TraceCompat.endSection();
    this.mFirstLayoutComplete = true;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = 0;
    if (this.mLayout == null) {
      defaultOnMeasure(paramInt1, paramInt2);
    }
    for (;;)
    {
      return;
      if (this.mLayout.isAutoMeasureEnabled())
      {
        int j = View.MeasureSpec.getMode(paramInt1);
        int k = View.MeasureSpec.getMode(paramInt2);
        this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
        int m = i;
        if (j == NUM)
        {
          m = i;
          if (k == NUM) {
            m = 1;
          }
        }
        if ((m == 0) && (this.mAdapter != null))
        {
          if (this.mState.mLayoutStep == 1) {
            dispatchLayoutStep1();
          }
          this.mLayout.setMeasureSpecs(paramInt1, paramInt2);
          this.mState.mIsMeasuring = true;
          dispatchLayoutStep2();
          this.mLayout.setMeasuredDimensionFromChildren(paramInt1, paramInt2);
          if (this.mLayout.shouldMeasureTwice())
          {
            this.mLayout.setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
            this.mState.mIsMeasuring = true;
            dispatchLayoutStep2();
            this.mLayout.setMeasuredDimensionFromChildren(paramInt1, paramInt2);
          }
        }
      }
      else
      {
        if (!this.mHasFixedSize) {
          break;
        }
        this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      }
    }
    if (this.mAdapterUpdateDuringMeasure)
    {
      startInterceptRequestLayout();
      onEnterLayoutOrScroll();
      processAdapterUpdatesAndSetAnimationFlags();
      onExitLayoutOrScroll();
      if (this.mState.mRunPredictiveAnimations)
      {
        this.mState.mInPreLayout = true;
        label266:
        this.mAdapterUpdateDuringMeasure = false;
        stopInterceptRequestLayout(false);
        label276:
        if (this.mAdapter == null) {
          break label377;
        }
      }
    }
    label377:
    for (this.mState.mItemCount = this.mAdapter.getItemCount();; this.mState.mItemCount = 0)
    {
      startInterceptRequestLayout();
      this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      stopInterceptRequestLayout(false);
      this.mState.mInPreLayout = false;
      break;
      this.mAdapterHelper.consumeUpdatesInOnePass();
      this.mState.mInPreLayout = false;
      break label266;
      if (!this.mState.mRunPredictiveAnimations) {
        break label276;
      }
      setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
      break;
    }
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    if (isComputingLayout()) {}
    for (boolean bool = false;; bool = super.onRequestFocusInDescendants(paramInt, paramRect)) {
      return bool;
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
    }
    for (;;)
    {
      return;
      this.mPendingSavedState = ((SavedState)paramParcelable);
      super.onRestoreInstanceState(this.mPendingSavedState.getSuperState());
      if ((this.mLayout != null) && (this.mPendingSavedState.mLayoutState != null)) {
        this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState);
      }
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if (this.mPendingSavedState != null) {
      localSavedState.copyFrom(this.mPendingSavedState);
    }
    for (;;)
    {
      return localSavedState;
      if (this.mLayout != null) {
        localSavedState.mLayoutState = this.mLayout.onSaveInstanceState();
      } else {
        localSavedState.mLayoutState = null;
      }
    }
  }
  
  public void onScrollStateChanged(int paramInt) {}
  
  public void onScrolled(int paramInt1, int paramInt2) {}
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramInt1 != paramInt3) || (paramInt2 != paramInt4)) {
      invalidateGlows();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mLayoutFrozen) || (this.mIgnoreMotionEventTillDown)) {
      bool1 = false;
    }
    for (;;)
    {
      return bool1;
      if (dispatchOnItemTouch(paramMotionEvent))
      {
        cancelTouch();
        bool1 = true;
      }
      else
      {
        if (this.mLayout != null) {
          break;
        }
        bool1 = false;
      }
    }
    boolean bool1 = this.mLayout.canScrollHorizontally();
    boolean bool2 = this.mLayout.canScrollVertically();
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    int i = 0;
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    int j = paramMotionEvent.getActionMasked();
    int k = paramMotionEvent.getActionIndex();
    if (j == 0)
    {
      int[] arrayOfInt = this.mNestedOffsets;
      this.mNestedOffsets[1] = 0;
      arrayOfInt[0] = 0;
    }
    localMotionEvent.offsetLocation(this.mNestedOffsets[0], this.mNestedOffsets[1]);
    int m = i;
    switch (j)
    {
    default: 
      m = i;
    }
    for (;;)
    {
      if (m == 0) {
        this.mVelocityTracker.addMovement(localMotionEvent);
      }
      localMotionEvent.recycle();
      bool1 = true;
      break;
      this.mScrollPointerId = paramMotionEvent.getPointerId(0);
      m = (int)(paramMotionEvent.getX() + 0.5F);
      this.mLastTouchX = m;
      this.mInitialTouchX = m;
      m = (int)(paramMotionEvent.getY() + 0.5F);
      this.mLastTouchY = m;
      this.mInitialTouchY = m;
      m = 0;
      if (bool1) {
        m = 0x0 | 0x1;
      }
      k = m;
      if (bool2) {
        k = m | 0x2;
      }
      startNestedScroll(k, 0);
      m = i;
      continue;
      this.mScrollPointerId = paramMotionEvent.getPointerId(k);
      m = (int)(paramMotionEvent.getX(k) + 0.5F);
      this.mLastTouchX = m;
      this.mInitialTouchX = m;
      m = (int)(paramMotionEvent.getY(k) + 0.5F);
      this.mLastTouchY = m;
      this.mInitialTouchY = m;
      m = i;
      continue;
      m = paramMotionEvent.findPointerIndex(this.mScrollPointerId);
      if (m < 0)
      {
        Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
        bool1 = false;
        break;
      }
      int n = (int)(paramMotionEvent.getX(m) + 0.5F);
      int i1 = (int)(paramMotionEvent.getY(m) + 0.5F);
      int i2 = this.mLastTouchX - n;
      j = this.mLastTouchY - i1;
      k = i2;
      m = j;
      if (dispatchNestedPreScroll(i2, j, this.mScrollConsumed, this.mScrollOffset, 0))
      {
        k = i2 - this.mScrollConsumed[0];
        m = j - this.mScrollConsumed[1];
        localMotionEvent.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]);
        paramMotionEvent = this.mNestedOffsets;
        paramMotionEvent[0] += this.mScrollOffset[0];
        paramMotionEvent = this.mNestedOffsets;
        paramMotionEvent[1] += this.mScrollOffset[1];
      }
      i2 = k;
      j = m;
      int i3;
      if (this.mScrollState != 1)
      {
        i2 = 0;
        i3 = k;
        j = i2;
        if (bool1)
        {
          i3 = k;
          j = i2;
          if (Math.abs(k) > this.mTouchSlop)
          {
            if (k <= 0) {
              break label847;
            }
            i3 = k - this.mTouchSlop;
            label648:
            j = 1;
          }
        }
        k = m;
        int i4 = j;
        if (bool2)
        {
          k = m;
          i4 = j;
          if (Math.abs(m) > this.mTouchSlop)
          {
            if (m <= 0) {
              break label859;
            }
            k = m - this.mTouchSlop;
            label697:
            i4 = 1;
          }
        }
        i2 = i3;
        j = k;
        if (i4 != 0)
        {
          setScrollState(1);
          j = k;
          i2 = i3;
        }
      }
      m = i;
      if (this.mScrollState == 1)
      {
        this.mLastTouchX = (n - this.mScrollOffset[0]);
        this.mLastTouchY = (i1 - this.mScrollOffset[1]);
        if (bool1)
        {
          m = i2;
          label772:
          if (!bool2) {
            break label877;
          }
        }
        label847:
        label859:
        label877:
        for (k = j;; k = 0)
        {
          if (scrollByInternal(m, k, localMotionEvent)) {
            getParent().requestDisallowInterceptTouchEvent(true);
          }
          m = i;
          if (this.mGapWorker == null) {
            break;
          }
          if (i2 == 0)
          {
            m = i;
            if (j == 0) {
              break;
            }
          }
          this.mGapWorker.postFromTraversal(this, i2, j);
          m = i;
          break;
          i3 = k + this.mTouchSlop;
          break label648;
          k = m + this.mTouchSlop;
          break label697;
          m = 0;
          break label772;
        }
        onPointerUp(paramMotionEvent);
        m = i;
        continue;
        this.mVelocityTracker.addMovement(localMotionEvent);
        m = 1;
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
        float f1;
        if (bool1)
        {
          f1 = -this.mVelocityTracker.getXVelocity(this.mScrollPointerId);
          label940:
          if (!bool2) {
            break label1003;
          }
        }
        label1003:
        for (float f2 = -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);; f2 = 0.0F)
        {
          if (((f1 == 0.0F) && (f2 == 0.0F)) || (!fling((int)f1, (int)f2))) {
            setScrollState(0);
          }
          resetTouch();
          break;
          f1 = 0.0F;
          break label940;
        }
        cancelTouch();
        m = i;
      }
    }
  }
  
  void postAnimationRunner()
  {
    if ((!this.mPostedAnimatorRunner) && (this.mIsAttached))
    {
      ViewCompat.postOnAnimation(this, this.mItemAnimatorRunner);
      this.mPostedAnimatorRunner = true;
    }
  }
  
  void processDataSetCompletelyChanged(boolean paramBoolean)
  {
    this.mDispatchItemsChangedEvent |= paramBoolean;
    this.mDataSetHasChangedAfterLayout = true;
    markKnownViewsInvalid();
  }
  
  void recordAnimationInfoIfBouncedHiddenView(ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo)
  {
    paramViewHolder.setFlags(0, 8192);
    if ((this.mState.mTrackOldChangeHolders) && (paramViewHolder.isUpdated()) && (!paramViewHolder.isRemoved()) && (!paramViewHolder.shouldIgnore()))
    {
      long l = getChangedHolderKey(paramViewHolder);
      this.mViewInfoStore.addToOldChangeHolders(l, paramViewHolder);
    }
    this.mViewInfoStore.addToPreLayout(paramViewHolder, paramItemHolderInfo);
  }
  
  void removeAndRecycleViews()
  {
    if (this.mItemAnimator != null) {
      this.mItemAnimator.endAnimations();
    }
    if (this.mLayout != null)
    {
      this.mLayout.removeAndRecycleAllViews(this.mRecycler);
      this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
    }
    this.mRecycler.clear();
  }
  
  boolean removeAnimatingView(View paramView)
  {
    startInterceptRequestLayout();
    boolean bool1 = this.mChildHelper.removeViewIfHidden(paramView);
    if (bool1)
    {
      paramView = getChildViewHolderInt(paramView);
      this.mRecycler.unscrapView(paramView);
      this.mRecycler.recycleViewHolderInternal(paramView);
    }
    if (!bool1) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      stopInterceptRequestLayout(bool2);
      return bool1;
    }
  }
  
  protected void removeDetachedView(View paramView, boolean paramBoolean)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    if (localViewHolder != null)
    {
      if (!localViewHolder.isTmpDetached()) {
        break label36;
      }
      localViewHolder.clearTmpDetachFlag();
    }
    label36:
    while (localViewHolder.shouldIgnore())
    {
      paramView.clearAnimation();
      dispatchChildDetached(paramView);
      super.removeDetachedView(paramView, paramBoolean);
      return;
    }
    throw new IllegalArgumentException("Called removeDetachedView with a view which is not flagged as tmp detached." + localViewHolder + exceptionLabel());
  }
  
  public void removeItemDecoration(ItemDecoration paramItemDecoration)
  {
    if (this.mLayout != null) {
      this.mLayout.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
    }
    this.mItemDecorations.remove(paramItemDecoration);
    if (this.mItemDecorations.isEmpty()) {
      if (getOverScrollMode() != 2) {
        break label60;
      }
    }
    label60:
    for (boolean bool = true;; bool = false)
    {
      setWillNotDraw(bool);
      markItemDecorInsetsDirty();
      requestLayout();
      return;
    }
  }
  
  public void removeItemDecorationAt(int paramInt)
  {
    int i = getItemDecorationCount();
    if ((paramInt < 0) || (paramInt >= i)) {
      throw new IndexOutOfBoundsException(paramInt + " is an invalid index for size " + i);
    }
    removeItemDecoration(getItemDecorationAt(paramInt));
  }
  
  public void removeOnChildAttachStateChangeListener(OnChildAttachStateChangeListener paramOnChildAttachStateChangeListener)
  {
    if (this.mOnChildAttachStateListeners == null) {}
    for (;;)
    {
      return;
      this.mOnChildAttachStateListeners.remove(paramOnChildAttachStateChangeListener);
    }
  }
  
  public void removeOnItemTouchListener(OnItemTouchListener paramOnItemTouchListener)
  {
    this.mOnItemTouchListeners.remove(paramOnItemTouchListener);
    if (this.mActiveOnItemTouchListener == paramOnItemTouchListener) {
      this.mActiveOnItemTouchListener = null;
    }
  }
  
  public void removeOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    if (this.mScrollListeners != null) {
      this.mScrollListeners.remove(paramOnScrollListener);
    }
  }
  
  void repositionShadowingViews()
  {
    int i = this.mChildHelper.getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = this.mChildHelper.getChildAt(j);
      Object localObject = getChildViewHolder(localView);
      if ((localObject != null) && (((ViewHolder)localObject).mShadowingHolder != null))
      {
        localObject = ((ViewHolder)localObject).mShadowingHolder.itemView;
        int k = localView.getLeft();
        int m = localView.getTop();
        if ((k != ((View)localObject).getLeft()) || (m != ((View)localObject).getTop())) {
          ((View)localObject).layout(k, m, ((View)localObject).getWidth() + k, ((View)localObject).getHeight() + m);
        }
      }
    }
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    if ((!this.mLayout.onRequestChildFocus(this, this.mState, paramView1, paramView2)) && (paramView2 != null)) {
      requestChildOnScreen(paramView1, paramView2);
    }
    super.requestChildFocus(paramView1, paramView2);
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    return this.mLayout.requestChildRectangleOnScreen(this, paramView, paramRect, paramBoolean);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    int i = this.mOnItemTouchListeners.size();
    for (int j = 0; j < i; j++) {
      ((OnItemTouchListener)this.mOnItemTouchListeners.get(j)).onRequestDisallowInterceptTouchEvent(paramBoolean);
    }
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  public void requestLayout()
  {
    if ((this.mInterceptRequestLayoutDepth == 0) && (!this.mLayoutFrozen)) {
      super.requestLayout();
    }
    for (;;)
    {
      return;
      this.mLayoutWasDefered = true;
    }
  }
  
  void saveOldPositions()
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if (!localViewHolder.shouldIgnore()) {
        localViewHolder.saveOldPosition();
      }
    }
  }
  
  public void scrollBy(int paramInt1, int paramInt2)
  {
    if (this.mLayout == null) {
      Log.e("RecyclerView", "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
    }
    boolean bool1;
    boolean bool2;
    do
    {
      return;
      if (this.mLayoutFrozen) {
        break;
      }
      bool1 = this.mLayout.canScrollHorizontally();
      bool2 = this.mLayout.canScrollVertically();
    } while ((!bool1) && (!bool2));
    if (bool1) {
      label54:
      if (!bool2) {
        break label75;
      }
    }
    for (;;)
    {
      scrollByInternal(paramInt1, paramInt2, null);
      break;
      break;
      paramInt1 = 0;
      break label54;
      label75:
      paramInt2 = 0;
    }
  }
  
  boolean scrollByInternal(int paramInt1, int paramInt2, MotionEvent paramMotionEvent)
  {
    boolean bool = false;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    consumePendingUpdateOperations();
    if (this.mAdapter != null)
    {
      startInterceptRequestLayout();
      onEnterLayoutOrScroll();
      TraceCompat.beginSection("RV Scroll");
      fillRemainingScrollValues(this.mState);
      n = i1;
      i = j;
      if (paramInt1 != 0)
      {
        n = this.mLayout.scrollHorizontallyBy(paramInt1, this.mRecycler, this.mState);
        i = paramInt1 - n;
      }
      i2 = i3;
      k = m;
      if (paramInt2 != 0)
      {
        i2 = this.mLayout.scrollVerticallyBy(paramInt2, this.mRecycler, this.mState);
        k = paramInt2 - i2;
      }
      TraceCompat.endSection();
      repositionShadowingViews();
      onExitLayoutOrScroll();
      stopInterceptRequestLayout(false);
    }
    if (!this.mItemDecorations.isEmpty()) {
      invalidate();
    }
    if (dispatchNestedScroll(n, i2, i, k, this.mScrollOffset, 0))
    {
      this.mLastTouchX -= this.mScrollOffset[0];
      this.mLastTouchY -= this.mScrollOffset[1];
      if (paramMotionEvent != null) {
        paramMotionEvent.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]);
      }
      paramMotionEvent = this.mNestedOffsets;
      paramMotionEvent[0] += this.mScrollOffset[0];
      paramMotionEvent = this.mNestedOffsets;
      paramMotionEvent[1] += this.mScrollOffset[1];
    }
    for (;;)
    {
      if ((n != 0) || (i2 != 0)) {
        dispatchOnScrolled(n, i2);
      }
      if (!awakenScrollBars()) {
        invalidate();
      }
      if ((n != 0) || (i2 != 0)) {
        bool = true;
      }
      return bool;
      if (getOverScrollMode() != 2)
      {
        if ((paramMotionEvent != null) && (!MotionEventCompat.isFromSource(paramMotionEvent, 8194))) {
          pullGlows(paramMotionEvent.getX(), i, paramMotionEvent.getY(), k);
        }
        considerReleasingGlowsOnScroll(paramInt1, paramInt2);
      }
    }
  }
  
  public void scrollTo(int paramInt1, int paramInt2)
  {
    Log.w("RecyclerView", "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
  }
  
  public void scrollToPosition(int paramInt)
  {
    if (this.mLayoutFrozen) {}
    for (;;)
    {
      return;
      stopScroll();
      if (this.mLayout == null)
      {
        Log.e("RecyclerView", "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
      }
      else
      {
        this.mLayout.scrollToPosition(paramInt);
        awakenScrollBars();
      }
    }
  }
  
  public void sendAccessibilityEventUnchecked(AccessibilityEvent paramAccessibilityEvent)
  {
    if (shouldDeferAccessibilityEvent(paramAccessibilityEvent)) {}
    for (;;)
    {
      return;
      super.sendAccessibilityEventUnchecked(paramAccessibilityEvent);
    }
  }
  
  public void setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate paramRecyclerViewAccessibilityDelegate)
  {
    this.mAccessibilityDelegate = paramRecyclerViewAccessibilityDelegate;
    ViewCompat.setAccessibilityDelegate(this, this.mAccessibilityDelegate);
  }
  
  public void setAdapter(Adapter paramAdapter)
  {
    setLayoutFrozen(false);
    setAdapterInternal(paramAdapter, false, true);
    processDataSetCompletelyChanged(false);
    requestLayout();
  }
  
  public void setBottomGlowOffset(int paramInt)
  {
    this.bottomGlowOffset = paramInt;
  }
  
  public void setChildDrawingOrderCallback(ChildDrawingOrderCallback paramChildDrawingOrderCallback)
  {
    if (paramChildDrawingOrderCallback == this.mChildDrawingOrderCallback) {
      return;
    }
    this.mChildDrawingOrderCallback = paramChildDrawingOrderCallback;
    if (this.mChildDrawingOrderCallback != null) {}
    for (boolean bool = true;; bool = false)
    {
      setChildrenDrawingOrderEnabled(bool);
      break;
    }
  }
  
  boolean setChildImportantForAccessibilityInternal(ViewHolder paramViewHolder, int paramInt)
  {
    if (isComputingLayout())
    {
      paramViewHolder.mPendingAccessibilityState = paramInt;
      this.mPendingAccessibilityImportanceChange.add(paramViewHolder);
    }
    for (boolean bool = false;; bool = true)
    {
      return bool;
      ViewCompat.setImportantForAccessibility(paramViewHolder.itemView, paramInt);
    }
  }
  
  public void setClipToPadding(boolean paramBoolean)
  {
    if (paramBoolean != this.mClipToPadding) {
      invalidateGlows();
    }
    this.mClipToPadding = paramBoolean;
    super.setClipToPadding(paramBoolean);
    if (this.mFirstLayoutComplete) {
      requestLayout();
    }
  }
  
  public void setEdgeEffectFactory(EdgeEffectFactory paramEdgeEffectFactory)
  {
    this.mEdgeEffectFactory = paramEdgeEffectFactory;
    invalidateGlows();
  }
  
  public void setGlowColor(int paramInt)
  {
    this.glowColor = paramInt;
  }
  
  public void setHasFixedSize(boolean paramBoolean)
  {
    this.mHasFixedSize = paramBoolean;
  }
  
  public void setItemAnimator(ItemAnimator paramItemAnimator)
  {
    if (this.mItemAnimator != null)
    {
      this.mItemAnimator.endAnimations();
      this.mItemAnimator.setListener(null);
    }
    this.mItemAnimator = paramItemAnimator;
    if (this.mItemAnimator != null) {
      this.mItemAnimator.setListener(this.mItemAnimatorListener);
    }
  }
  
  public void setItemViewCacheSize(int paramInt)
  {
    this.mRecycler.setViewCacheSize(paramInt);
  }
  
  public void setLayoutFrozen(boolean paramBoolean)
  {
    if (paramBoolean != this.mLayoutFrozen)
    {
      assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
      if (paramBoolean) {
        break label55;
      }
      this.mLayoutFrozen = false;
      if ((this.mLayoutWasDefered) && (this.mLayout != null) && (this.mAdapter != null)) {
        requestLayout();
      }
      this.mLayoutWasDefered = false;
    }
    for (;;)
    {
      return;
      label55:
      long l = SystemClock.uptimeMillis();
      onTouchEvent(MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0));
      this.mLayoutFrozen = true;
      this.mIgnoreMotionEventTillDown = true;
      stopScroll();
    }
  }
  
  public void setLayoutManager(LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager == this.mLayout) {}
    for (;;)
    {
      return;
      stopScroll();
      if (this.mLayout != null)
      {
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
      }
      for (;;)
      {
        this.mChildHelper.removeAllViewsUnfiltered();
        this.mLayout = paramLayoutManager;
        if (paramLayoutManager == null) {
          break label195;
        }
        if (paramLayoutManager.mRecyclerView == null) {
          break;
        }
        throw new IllegalArgumentException("LayoutManager " + paramLayoutManager + " is already attached to a RecyclerView:" + paramLayoutManager.mRecyclerView.exceptionLabel());
        this.mRecycler.clear();
      }
      this.mLayout.setRecyclerView(this);
      if (this.mIsAttached) {
        this.mLayout.dispatchAttachedToWindow(this);
      }
      label195:
      this.mRecycler.updateViewCacheSize();
      requestLayout();
    }
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    getScrollingChildHelper().setNestedScrollingEnabled(paramBoolean);
  }
  
  public void setOnFlingListener(OnFlingListener paramOnFlingListener)
  {
    this.mOnFlingListener = paramOnFlingListener;
  }
  
  @Deprecated
  public void setOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    this.mScrollListener = paramOnScrollListener;
  }
  
  public void setPreserveFocusAfterLayout(boolean paramBoolean)
  {
    this.mPreserveFocusAfterLayout = paramBoolean;
  }
  
  public void setRecycledViewPool(RecycledViewPool paramRecycledViewPool)
  {
    this.mRecycler.setRecycledViewPool(paramRecycledViewPool);
  }
  
  public void setRecyclerListener(RecyclerListener paramRecyclerListener)
  {
    this.mRecyclerListener = paramRecyclerListener;
  }
  
  void setScrollState(int paramInt)
  {
    if (paramInt == this.mScrollState) {}
    for (;;)
    {
      return;
      this.mScrollState = paramInt;
      if (paramInt != 2) {
        stopScrollersInternal();
      }
      dispatchOnScrollStateChanged(paramInt);
    }
  }
  
  public void setScrollingTouchSlop(int paramInt)
  {
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(getContext());
    switch (paramInt)
    {
    default: 
      Log.w("RecyclerView", "setScrollingTouchSlop(): bad argument constant " + paramInt + "; using default value");
    }
    for (this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();; this.mTouchSlop = localViewConfiguration.getScaledPagingTouchSlop()) {
      return;
    }
  }
  
  public void setTopGlowOffset(int paramInt)
  {
    this.topGlowOffset = paramInt;
  }
  
  public void setViewCacheExtension(ViewCacheExtension paramViewCacheExtension)
  {
    this.mRecycler.setViewCacheExtension(paramViewCacheExtension);
  }
  
  boolean shouldDeferAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (isComputingLayout())
    {
      int i = 0;
      if (paramAccessibilityEvent != null) {
        i = AccessibilityEventCompat.getContentChangeTypes(paramAccessibilityEvent);
      }
      int j = i;
      if (i == 0) {
        j = 0;
      }
      this.mEatenAccessibilityChangeFlags |= j;
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void smoothScrollBy(int paramInt1, int paramInt2)
  {
    smoothScrollBy(paramInt1, paramInt2, null);
  }
  
  public void smoothScrollBy(int paramInt1, int paramInt2, Interpolator paramInterpolator)
  {
    if (this.mLayout == null) {
      Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
    }
    for (;;)
    {
      return;
      if (!this.mLayoutFrozen)
      {
        if (!this.mLayout.canScrollHorizontally()) {
          paramInt1 = 0;
        }
        if (!this.mLayout.canScrollVertically()) {
          paramInt2 = 0;
        }
        if ((paramInt1 != 0) || (paramInt2 != 0)) {
          this.mViewFlinger.smoothScrollBy(paramInt1, paramInt2, paramInterpolator);
        }
      }
    }
  }
  
  public void smoothScrollToPosition(int paramInt)
  {
    if (this.mLayoutFrozen) {}
    for (;;)
    {
      return;
      if (this.mLayout == null) {
        Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      } else {
        this.mLayout.smoothScrollToPosition(this, this.mState, paramInt);
      }
    }
  }
  
  void startInterceptRequestLayout()
  {
    this.mInterceptRequestLayoutDepth += 1;
    if ((this.mInterceptRequestLayoutDepth == 1) && (!this.mLayoutFrozen)) {
      this.mLayoutWasDefered = false;
    }
  }
  
  public boolean startNestedScroll(int paramInt)
  {
    return getScrollingChildHelper().startNestedScroll(paramInt);
  }
  
  public boolean startNestedScroll(int paramInt1, int paramInt2)
  {
    return getScrollingChildHelper().startNestedScroll(paramInt1, paramInt2);
  }
  
  void stopInterceptRequestLayout(boolean paramBoolean)
  {
    if (this.mInterceptRequestLayoutDepth < 1) {
      this.mInterceptRequestLayoutDepth = 1;
    }
    if ((!paramBoolean) && (!this.mLayoutFrozen)) {
      this.mLayoutWasDefered = false;
    }
    if (this.mInterceptRequestLayoutDepth == 1)
    {
      if ((paramBoolean) && (this.mLayoutWasDefered) && (!this.mLayoutFrozen) && (this.mLayout != null) && (this.mAdapter != null)) {
        dispatchLayout();
      }
      if (!this.mLayoutFrozen) {
        this.mLayoutWasDefered = false;
      }
    }
    this.mInterceptRequestLayoutDepth -= 1;
  }
  
  public void stopNestedScroll()
  {
    getScrollingChildHelper().stopNestedScroll();
  }
  
  public void stopNestedScroll(int paramInt)
  {
    getScrollingChildHelper().stopNestedScroll(paramInt);
  }
  
  public void stopScroll()
  {
    setScrollState(0);
    stopScrollersInternal();
  }
  
  public void swapAdapter(Adapter paramAdapter, boolean paramBoolean)
  {
    setLayoutFrozen(false);
    setAdapterInternal(paramAdapter, true, paramBoolean);
    processDataSetCompletelyChanged(true);
    requestLayout();
  }
  
  void viewRangeUpdate(int paramInt1, int paramInt2, Object paramObject)
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    int j = 0;
    if (j < i)
    {
      View localView = this.mChildHelper.getUnfilteredChildAt(j);
      ViewHolder localViewHolder = getChildViewHolderInt(localView);
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      for (;;)
      {
        j++;
        break;
        if ((localViewHolder.mPosition >= paramInt1) && (localViewHolder.mPosition < paramInt1 + paramInt2))
        {
          localViewHolder.addFlags(2);
          localViewHolder.addChangePayload(paramObject);
          ((LayoutParams)localView.getLayoutParams()).mInsetsDirty = true;
        }
      }
    }
    this.mRecycler.viewRangeUpdate(paramInt1, paramInt2);
  }
  
  public static abstract class Adapter<VH extends RecyclerView.ViewHolder>
  {
    private boolean mHasStableIds = false;
    private final RecyclerView.AdapterDataObservable mObservable = new RecyclerView.AdapterDataObservable();
    
    public final void bindViewHolder(VH paramVH, int paramInt)
    {
      paramVH.mPosition = paramInt;
      if (hasStableIds()) {
        paramVH.mItemId = getItemId(paramInt);
      }
      paramVH.setFlags(1, 519);
      TraceCompat.beginSection("RV OnBindView");
      onBindViewHolder(paramVH, paramInt, paramVH.getUnmodifiedPayloads());
      paramVH.clearPayload();
      paramVH = paramVH.itemView.getLayoutParams();
      if ((paramVH instanceof RecyclerView.LayoutParams)) {
        ((RecyclerView.LayoutParams)paramVH).mInsetsDirty = true;
      }
      TraceCompat.endSection();
    }
    
    public final VH createViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      try
      {
        TraceCompat.beginSection("RV CreateView");
        paramViewGroup = onCreateViewHolder(paramViewGroup, paramInt);
        if (paramViewGroup.itemView.getParent() != null)
        {
          paramViewGroup = new java/lang/IllegalStateException;
          paramViewGroup.<init>("ViewHolder views must not be attached when created. Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)");
          throw paramViewGroup;
        }
      }
      finally
      {
        TraceCompat.endSection();
      }
      paramViewGroup.mItemViewType = paramInt;
      TraceCompat.endSection();
      return paramViewGroup;
    }
    
    public abstract int getItemCount();
    
    public long getItemId(int paramInt)
    {
      return -1L;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public final boolean hasObservers()
    {
      return this.mObservable.hasObservers();
    }
    
    public final boolean hasStableIds()
    {
      return this.mHasStableIds;
    }
    
    public void notifyDataSetChanged()
    {
      this.mObservable.notifyChanged();
    }
    
    public void notifyItemChanged(int paramInt)
    {
      this.mObservable.notifyItemRangeChanged(paramInt, 1);
    }
    
    public void notifyItemChanged(int paramInt, Object paramObject)
    {
      this.mObservable.notifyItemRangeChanged(paramInt, 1, paramObject);
    }
    
    public void notifyItemInserted(int paramInt)
    {
      this.mObservable.notifyItemRangeInserted(paramInt, 1);
    }
    
    public void notifyItemMoved(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemMoved(paramInt1, paramInt2);
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeChanged(paramInt1, paramInt2);
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      this.mObservable.notifyItemRangeChanged(paramInt1, paramInt2, paramObject);
    }
    
    public void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeInserted(paramInt1, paramInt2);
    }
    
    public void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeRemoved(paramInt1, paramInt2);
    }
    
    public void notifyItemRemoved(int paramInt)
    {
      this.mObservable.notifyItemRangeRemoved(paramInt, 1);
    }
    
    public void onAttachedToRecyclerView(RecyclerView paramRecyclerView) {}
    
    public abstract void onBindViewHolder(VH paramVH, int paramInt);
    
    public void onBindViewHolder(VH paramVH, int paramInt, List<Object> paramList)
    {
      onBindViewHolder(paramVH, paramInt);
    }
    
    public abstract VH onCreateViewHolder(ViewGroup paramViewGroup, int paramInt);
    
    public void onDetachedFromRecyclerView(RecyclerView paramRecyclerView) {}
    
    public boolean onFailedToRecycleView(VH paramVH)
    {
      return false;
    }
    
    public void onViewAttachedToWindow(VH paramVH) {}
    
    public void onViewDetachedFromWindow(VH paramVH) {}
    
    public void onViewRecycled(VH paramVH) {}
    
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver paramAdapterDataObserver)
    {
      this.mObservable.registerObserver(paramAdapterDataObserver);
    }
    
    public void setHasStableIds(boolean paramBoolean)
    {
      if (hasObservers()) {
        throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
      }
      this.mHasStableIds = paramBoolean;
    }
    
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver paramAdapterDataObserver)
    {
      this.mObservable.unregisterObserver(paramAdapterDataObserver);
    }
  }
  
  static class AdapterDataObservable
    extends Observable<RecyclerView.AdapterDataObserver>
  {
    public boolean hasObservers()
    {
      if (!this.mObservers.isEmpty()) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void notifyChanged()
    {
      for (int i = this.mObservers.size() - 1; i >= 0; i--) {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onChanged();
      }
    }
    
    public void notifyItemMoved(int paramInt1, int paramInt2)
    {
      for (int i = this.mObservers.size() - 1; i >= 0; i--) {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeMoved(paramInt1, paramInt2, 1);
      }
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      notifyItemRangeChanged(paramInt1, paramInt2, null);
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      for (int i = this.mObservers.size() - 1; i >= 0; i--) {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeChanged(paramInt1, paramInt2, paramObject);
      }
    }
    
    public void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      for (int i = this.mObservers.size() - 1; i >= 0; i--) {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeInserted(paramInt1, paramInt2);
      }
    }
    
    public void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      for (int i = this.mObservers.size() - 1; i >= 0; i--) {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeRemoved(paramInt1, paramInt2);
      }
    }
  }
  
  public static abstract class AdapterDataObserver
  {
    public void onChanged() {}
    
    public void onItemRangeChanged(int paramInt1, int paramInt2) {}
    
    public void onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      onItemRangeChanged(paramInt1, paramInt2);
    }
    
    public void onItemRangeInserted(int paramInt1, int paramInt2) {}
    
    public void onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onItemRangeRemoved(int paramInt1, int paramInt2) {}
  }
  
  public static abstract interface ChildDrawingOrderCallback
  {
    public abstract int onGetChildDrawingOrder(int paramInt1, int paramInt2);
  }
  
  public static class EdgeEffectFactory
  {
    public static final int DIRECTION_BOTTOM = 3;
    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_TOP = 1;
    
    protected EdgeEffect createEdgeEffect(RecyclerView paramRecyclerView, int paramInt)
    {
      return new EdgeEffect(paramRecyclerView.getContext());
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface EdgeDirection {}
  }
  
  public static abstract class ItemAnimator
  {
    public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
    public static final int FLAG_CHANGED = 2;
    public static final int FLAG_INVALIDATED = 4;
    public static final int FLAG_MOVED = 2048;
    public static final int FLAG_REMOVED = 8;
    private long mAddDuration = 120L;
    private long mChangeDuration = 250L;
    private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners = new ArrayList();
    private ItemAnimatorListener mListener = null;
    private long mMoveDuration = 250L;
    private long mRemoveDuration = 120L;
    
    static int buildAdapterChangeFlagsForAnimations(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = RecyclerView.ViewHolder.access$1600(paramViewHolder) & 0xE;
      int j;
      if (paramViewHolder.isInvalid()) {
        j = 4;
      }
      for (;;)
      {
        return j;
        j = i;
        if ((i & 0x4) == 0)
        {
          int k = paramViewHolder.getOldPosition();
          int m = paramViewHolder.getAdapterPosition();
          j = i;
          if (k != -1)
          {
            j = i;
            if (m != -1)
            {
              j = i;
              if (k != m) {
                j = i | 0x800;
              }
            }
          }
        }
      }
    }
    
    public abstract boolean animateAppearance(RecyclerView.ViewHolder paramViewHolder, ItemHolderInfo paramItemHolderInfo1, ItemHolderInfo paramItemHolderInfo2);
    
    public abstract boolean animateChange(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, ItemHolderInfo paramItemHolderInfo1, ItemHolderInfo paramItemHolderInfo2);
    
    public abstract boolean animateDisappearance(RecyclerView.ViewHolder paramViewHolder, ItemHolderInfo paramItemHolderInfo1, ItemHolderInfo paramItemHolderInfo2);
    
    public abstract boolean animatePersistence(RecyclerView.ViewHolder paramViewHolder, ItemHolderInfo paramItemHolderInfo1, ItemHolderInfo paramItemHolderInfo2);
    
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }
    
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder paramViewHolder, List<Object> paramList)
    {
      return canReuseUpdatedViewHolder(paramViewHolder);
    }
    
    public final void dispatchAnimationFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      onAnimationFinished(paramViewHolder);
      if (this.mListener != null) {
        this.mListener.onAnimationFinished(paramViewHolder);
      }
    }
    
    public final void dispatchAnimationStarted(RecyclerView.ViewHolder paramViewHolder)
    {
      onAnimationStarted(paramViewHolder);
    }
    
    public final void dispatchAnimationsFinished()
    {
      int i = this.mFinishedListeners.size();
      for (int j = 0; j < i; j++) {
        ((ItemAnimatorFinishedListener)this.mFinishedListeners.get(j)).onAnimationsFinished();
      }
      this.mFinishedListeners.clear();
    }
    
    public abstract void endAnimation(RecyclerView.ViewHolder paramViewHolder);
    
    public abstract void endAnimations();
    
    public long getAddDuration()
    {
      return this.mAddDuration;
    }
    
    public long getChangeDuration()
    {
      return this.mChangeDuration;
    }
    
    public long getMoveDuration()
    {
      return this.mMoveDuration;
    }
    
    public long getRemoveDuration()
    {
      return this.mRemoveDuration;
    }
    
    public abstract boolean isRunning();
    
    public final boolean isRunning(ItemAnimatorFinishedListener paramItemAnimatorFinishedListener)
    {
      boolean bool = isRunning();
      if (paramItemAnimatorFinishedListener != null)
      {
        if (bool) {
          break label21;
        }
        paramItemAnimatorFinishedListener.onAnimationsFinished();
      }
      for (;;)
      {
        return bool;
        label21:
        this.mFinishedListeners.add(paramItemAnimatorFinishedListener);
      }
    }
    
    public ItemHolderInfo obtainHolderInfo()
    {
      return new ItemHolderInfo();
    }
    
    public void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder) {}
    
    public void onAnimationStarted(RecyclerView.ViewHolder paramViewHolder) {}
    
    public ItemHolderInfo recordPostLayoutInformation(RecyclerView.State paramState, RecyclerView.ViewHolder paramViewHolder)
    {
      return obtainHolderInfo().setFrom(paramViewHolder);
    }
    
    public ItemHolderInfo recordPreLayoutInformation(RecyclerView.State paramState, RecyclerView.ViewHolder paramViewHolder, int paramInt, List<Object> paramList)
    {
      return obtainHolderInfo().setFrom(paramViewHolder);
    }
    
    public abstract void runPendingAnimations();
    
    public void setAddDuration(long paramLong)
    {
      this.mAddDuration = paramLong;
    }
    
    public void setChangeDuration(long paramLong)
    {
      this.mChangeDuration = paramLong;
    }
    
    void setListener(ItemAnimatorListener paramItemAnimatorListener)
    {
      this.mListener = paramItemAnimatorListener;
    }
    
    public void setMoveDuration(long paramLong)
    {
      this.mMoveDuration = paramLong;
    }
    
    public void setRemoveDuration(long paramLong)
    {
      this.mRemoveDuration = paramLong;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface AdapterChanges {}
    
    public static abstract interface ItemAnimatorFinishedListener
    {
      public abstract void onAnimationsFinished();
    }
    
    static abstract interface ItemAnimatorListener
    {
      public abstract void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder);
    }
    
    public static class ItemHolderInfo
    {
      public int bottom;
      public int changeFlags;
      public int left;
      public int right;
      public int top;
      
      public ItemHolderInfo setFrom(RecyclerView.ViewHolder paramViewHolder)
      {
        return setFrom(paramViewHolder, 0);
      }
      
      public ItemHolderInfo setFrom(RecyclerView.ViewHolder paramViewHolder, int paramInt)
      {
        paramViewHolder = paramViewHolder.itemView;
        this.left = paramViewHolder.getLeft();
        this.top = paramViewHolder.getTop();
        this.right = paramViewHolder.getRight();
        this.bottom = paramViewHolder.getBottom();
        return this;
      }
    }
  }
  
  private class ItemAnimatorRestoreListener
    implements RecyclerView.ItemAnimator.ItemAnimatorListener
  {
    ItemAnimatorRestoreListener() {}
    
    public void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      paramViewHolder.setIsRecyclable(true);
      if ((paramViewHolder.mShadowedHolder != null) && (paramViewHolder.mShadowingHolder == null)) {
        paramViewHolder.mShadowedHolder = null;
      }
      paramViewHolder.mShadowingHolder = null;
      if ((!RecyclerView.ViewHolder.access$1500(paramViewHolder)) && (!RecyclerView.this.removeAnimatingView(paramViewHolder.itemView)) && (paramViewHolder.isTmpDetached())) {
        RecyclerView.this.removeDetachedView(paramViewHolder.itemView, false);
      }
    }
  }
  
  public static abstract class ItemDecoration
  {
    @Deprecated
    public void getItemOffsets(Rect paramRect, int paramInt, RecyclerView paramRecyclerView)
    {
      paramRect.set(0, 0, 0, 0);
    }
    
    public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      getItemOffsets(paramRect, ((RecyclerView.LayoutParams)paramView.getLayoutParams()).getViewLayoutPosition(), paramRecyclerView);
    }
    
    @Deprecated
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView) {}
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      onDraw(paramCanvas, paramRecyclerView);
    }
    
    @Deprecated
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView) {}
    
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      onDrawOver(paramCanvas, paramRecyclerView);
    }
  }
  
  public static abstract class LayoutManager
  {
    boolean mAutoMeasure = false;
    ChildHelper mChildHelper;
    private int mHeight;
    private int mHeightMode;
    ViewBoundsCheck mHorizontalBoundCheck = new ViewBoundsCheck(this.mHorizontalBoundCheckCallback);
    private final ViewBoundsCheck.Callback mHorizontalBoundCheckCallback = new ViewBoundsCheck.Callback()
    {
      public View getChildAt(int paramAnonymousInt)
      {
        return RecyclerView.LayoutManager.this.getChildAt(paramAnonymousInt);
      }
      
      public int getChildCount()
      {
        return RecyclerView.LayoutManager.this.getChildCount();
      }
      
      public int getChildEnd(View paramAnonymousView)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramAnonymousView.getLayoutParams();
        return RecyclerView.LayoutManager.this.getDecoratedRight(paramAnonymousView) + localLayoutParams.rightMargin;
      }
      
      public int getChildStart(View paramAnonymousView)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramAnonymousView.getLayoutParams();
        return RecyclerView.LayoutManager.this.getDecoratedLeft(paramAnonymousView) - localLayoutParams.leftMargin;
      }
      
      public View getParent()
      {
        return RecyclerView.LayoutManager.this.mRecyclerView;
      }
      
      public int getParentEnd()
      {
        return RecyclerView.LayoutManager.this.getWidth() - RecyclerView.LayoutManager.this.getPaddingRight();
      }
      
      public int getParentStart()
      {
        return RecyclerView.LayoutManager.this.getPaddingLeft();
      }
    };
    boolean mIsAttachedToWindow = false;
    private boolean mItemPrefetchEnabled = true;
    private boolean mMeasurementCacheEnabled = true;
    int mPrefetchMaxCountObserved;
    boolean mPrefetchMaxObservedInInitialPrefetch;
    RecyclerView mRecyclerView;
    boolean mRequestedSimpleAnimations = false;
    RecyclerView.SmoothScroller mSmoothScroller;
    ViewBoundsCheck mVerticalBoundCheck = new ViewBoundsCheck(this.mVerticalBoundCheckCallback);
    private final ViewBoundsCheck.Callback mVerticalBoundCheckCallback = new ViewBoundsCheck.Callback()
    {
      public View getChildAt(int paramAnonymousInt)
      {
        return RecyclerView.LayoutManager.this.getChildAt(paramAnonymousInt);
      }
      
      public int getChildCount()
      {
        return RecyclerView.LayoutManager.this.getChildCount();
      }
      
      public int getChildEnd(View paramAnonymousView)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramAnonymousView.getLayoutParams();
        return RecyclerView.LayoutManager.this.getDecoratedBottom(paramAnonymousView) + localLayoutParams.bottomMargin;
      }
      
      public int getChildStart(View paramAnonymousView)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramAnonymousView.getLayoutParams();
        return RecyclerView.LayoutManager.this.getDecoratedTop(paramAnonymousView) - localLayoutParams.topMargin;
      }
      
      public View getParent()
      {
        return RecyclerView.LayoutManager.this.mRecyclerView;
      }
      
      public int getParentEnd()
      {
        return RecyclerView.LayoutManager.this.getHeight() - RecyclerView.LayoutManager.this.getPaddingBottom();
      }
      
      public int getParentStart()
      {
        return RecyclerView.LayoutManager.this.getPaddingTop();
      }
    };
    private int mWidth;
    private int mWidthMode;
    
    private void addViewInt(View paramView, int paramInt, boolean paramBoolean)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      RecyclerView.LayoutParams localLayoutParams;
      if ((paramBoolean) || (localViewHolder.isRemoved()))
      {
        this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(localViewHolder);
        localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
        if ((!localViewHolder.wasReturnedFromScrap()) && (!localViewHolder.isScrap())) {
          break label128;
        }
        if (!localViewHolder.isScrap()) {
          break label120;
        }
        localViewHolder.unScrap();
        label68:
        this.mChildHelper.attachViewToParent(paramView, paramInt, paramView.getLayoutParams(), false);
      }
      for (;;)
      {
        if (localLayoutParams.mPendingInvalidate)
        {
          localViewHolder.itemView.invalidate();
          localLayoutParams.mPendingInvalidate = false;
        }
        return;
        this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(localViewHolder);
        break;
        label120:
        localViewHolder.clearReturnedFromScrapFlag();
        break label68;
        label128:
        if (paramView.getParent() == this.mRecyclerView)
        {
          int i = this.mChildHelper.indexOfChild(paramView);
          int j = paramInt;
          if (paramInt == -1) {
            j = this.mChildHelper.getChildCount();
          }
          if (i == -1) {
            throw new IllegalStateException("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:" + this.mRecyclerView.indexOfChild(paramView) + this.mRecyclerView.exceptionLabel());
          }
          if (i != j) {
            this.mRecyclerView.mLayout.moveView(i, j);
          }
        }
        else
        {
          this.mChildHelper.addView(paramView, paramInt, false);
          localLayoutParams.mInsetsDirty = true;
          if ((this.mSmoothScroller != null) && (this.mSmoothScroller.isRunning())) {
            this.mSmoothScroller.onChildAttachedToWindow(paramView);
          }
        }
      }
    }
    
    public static int chooseSize(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = View.MeasureSpec.getMode(paramInt1);
      int j = View.MeasureSpec.getSize(paramInt1);
      paramInt1 = j;
      switch (i)
      {
      }
      for (paramInt1 = Math.max(paramInt2, paramInt3);; paramInt1 = Math.min(j, Math.max(paramInt2, paramInt3))) {
        return paramInt1;
      }
    }
    
    private void detachViewInternal(int paramInt, View paramView)
    {
      this.mChildHelper.detachViewFromParent(paramInt);
    }
    
    public static int getChildMeasureSpec(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      int i = Math.max(0, paramInt1 - paramInt3);
      paramInt3 = 0;
      paramInt1 = 0;
      if (paramBoolean) {
        if (paramInt4 >= 0)
        {
          paramInt3 = paramInt4;
          paramInt1 = NUM;
        }
      }
      for (;;)
      {
        return View.MeasureSpec.makeMeasureSpec(paramInt3, paramInt1);
        if (paramInt4 == -1)
        {
          switch (paramInt2)
          {
          default: 
            break;
          case 1073741824: 
          case -2147483648: 
            paramInt3 = i;
            paramInt1 = paramInt2;
            break;
          case 0: 
            paramInt3 = 0;
            paramInt1 = 0;
            break;
          }
        }
        else if (paramInt4 == -2)
        {
          paramInt3 = 0;
          paramInt1 = 0;
          continue;
          if (paramInt4 >= 0)
          {
            paramInt3 = paramInt4;
            paramInt1 = NUM;
          }
          else if (paramInt4 == -1)
          {
            paramInt3 = i;
            paramInt1 = paramInt2;
          }
          else if (paramInt4 == -2)
          {
            paramInt3 = i;
            if ((paramInt2 == Integer.MIN_VALUE) || (paramInt2 == NUM)) {
              paramInt1 = Integer.MIN_VALUE;
            } else {
              paramInt1 = 0;
            }
          }
        }
      }
    }
    
    @Deprecated
    public static int getChildMeasureSpec(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      int i = Math.max(0, paramInt1 - paramInt2);
      paramInt2 = 0;
      paramInt1 = 0;
      if (paramBoolean) {
        if (paramInt3 >= 0)
        {
          paramInt2 = paramInt3;
          paramInt1 = NUM;
        }
      }
      for (;;)
      {
        return View.MeasureSpec.makeMeasureSpec(paramInt2, paramInt1);
        paramInt2 = 0;
        paramInt1 = 0;
        continue;
        if (paramInt3 >= 0)
        {
          paramInt2 = paramInt3;
          paramInt1 = NUM;
        }
        else if (paramInt3 == -1)
        {
          paramInt2 = i;
          paramInt1 = NUM;
        }
        else if (paramInt3 == -2)
        {
          paramInt2 = i;
          paramInt1 = Integer.MIN_VALUE;
        }
      }
    }
    
    private int[] getChildRectangleOnScreenScrollAmount(RecyclerView paramRecyclerView, View paramView, Rect paramRect, boolean paramBoolean)
    {
      int i = getPaddingLeft();
      int j = getPaddingTop();
      int k = getWidth() - getPaddingRight();
      int m = getHeight();
      int n = getPaddingBottom();
      int i1 = paramView.getLeft() + paramRect.left - paramView.getScrollX();
      int i2 = paramView.getTop() + paramRect.top - paramView.getScrollY();
      int i3 = i1 + paramRect.width();
      int i4 = paramRect.height();
      int i5 = Math.min(0, i1 - i);
      int i6 = Math.min(0, i2 - j);
      int i7 = Math.max(0, i3 - k);
      n = Math.max(0, i2 + i4 - (m - n));
      if (getLayoutDirection() == 1) {
        if (i7 != 0)
        {
          i5 = i7;
          if (i6 == 0) {
            break label206;
          }
        }
      }
      for (;;)
      {
        return new int[] { i5, i6 };
        i5 = Math.max(i5, i3 - k);
        break;
        if (i5 != 0) {
          break;
        }
        for (;;)
        {
          i5 = Math.min(i1 - i, i7);
        }
        label206:
        i6 = Math.min(i2 - j, n);
      }
    }
    
    private boolean isFocusedChildVisibleAfterScrolling(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
    {
      boolean bool1 = false;
      View localView = paramRecyclerView.getFocusedChild();
      boolean bool2;
      if (localView == null) {
        bool2 = bool1;
      }
      for (;;)
      {
        return bool2;
        int i = getPaddingLeft();
        int j = getPaddingTop();
        int k = getWidth();
        int m = getPaddingRight();
        int n = getHeight();
        int i1 = getPaddingBottom();
        paramRecyclerView = this.mRecyclerView.mTempRect;
        getDecoratedBoundsWithMargins(localView, paramRecyclerView);
        bool2 = bool1;
        if (paramRecyclerView.left - paramInt1 < k - m)
        {
          bool2 = bool1;
          if (paramRecyclerView.right - paramInt1 > i)
          {
            bool2 = bool1;
            if (paramRecyclerView.top - paramInt2 < n - i1)
            {
              bool2 = bool1;
              if (paramRecyclerView.bottom - paramInt2 > j) {
                bool2 = true;
              }
            }
          }
        }
      }
    }
    
    private static boolean isMeasurementUpToDate(int paramInt1, int paramInt2, int paramInt3)
    {
      boolean bool1 = true;
      int i = View.MeasureSpec.getMode(paramInt2);
      paramInt2 = View.MeasureSpec.getSize(paramInt2);
      boolean bool2;
      if ((paramInt3 > 0) && (paramInt1 != paramInt3)) {
        bool2 = false;
      }
      for (;;)
      {
        return bool2;
        bool2 = bool1;
        switch (i)
        {
        case 0: 
        default: 
          bool2 = false;
          break;
        case -2147483648: 
          bool2 = bool1;
          if (paramInt2 < paramInt1) {
            bool2 = false;
          }
          break;
        case 1073741824: 
          bool2 = bool1;
          if (paramInt2 != paramInt1) {
            bool2 = false;
          }
          break;
        }
      }
    }
    
    private void onSmoothScrollerStopped(RecyclerView.SmoothScroller paramSmoothScroller)
    {
      if (this.mSmoothScroller == paramSmoothScroller) {
        this.mSmoothScroller = null;
      }
    }
    
    private void scrapOrRecycleView(RecyclerView.Recycler paramRecycler, int paramInt, View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.shouldIgnore()) {}
      for (;;)
      {
        return;
        if ((localViewHolder.isInvalid()) && (!localViewHolder.isRemoved()) && (!this.mRecyclerView.mAdapter.hasStableIds()))
        {
          removeViewAt(paramInt);
          paramRecycler.recycleViewHolderInternal(localViewHolder);
        }
        else
        {
          detachViewAt(paramInt);
          paramRecycler.scrapView(paramView);
          this.mRecyclerView.mViewInfoStore.onViewDetached(localViewHolder);
        }
      }
    }
    
    public void addDisappearingView(View paramView)
    {
      addDisappearingView(paramView, -1);
    }
    
    public void addDisappearingView(View paramView, int paramInt)
    {
      addViewInt(paramView, paramInt, true);
    }
    
    public void addView(View paramView)
    {
      addView(paramView, -1);
    }
    
    public void addView(View paramView, int paramInt)
    {
      addViewInt(paramView, paramInt, false);
    }
    
    public void assertInLayoutOrScroll(String paramString)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.assertInLayoutOrScroll(paramString);
      }
    }
    
    public void assertNotInLayoutOrScroll(String paramString)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.assertNotInLayoutOrScroll(paramString);
      }
    }
    
    public void attachView(View paramView)
    {
      attachView(paramView, -1);
    }
    
    public void attachView(View paramView, int paramInt)
    {
      attachView(paramView, paramInt, (RecyclerView.LayoutParams)paramView.getLayoutParams());
    }
    
    public void attachView(View paramView, int paramInt, RecyclerView.LayoutParams paramLayoutParams)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.isRemoved()) {
        this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(localViewHolder);
      }
      for (;;)
      {
        this.mChildHelper.attachViewToParent(paramView, paramInt, paramLayoutParams, localViewHolder.isRemoved());
        return;
        this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(localViewHolder);
      }
    }
    
    public void calculateItemDecorationsForChild(View paramView, Rect paramRect)
    {
      if (this.mRecyclerView == null) {
        paramRect.set(0, 0, 0, 0);
      }
      for (;;)
      {
        return;
        paramRect.set(this.mRecyclerView.getItemDecorInsetsForChild(paramView));
      }
    }
    
    public boolean canScrollHorizontally()
    {
      return false;
    }
    
    public boolean canScrollVertically()
    {
      return false;
    }
    
    public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
    {
      if (paramLayoutParams != null) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, RecyclerView.State paramState, LayoutPrefetchRegistry paramLayoutPrefetchRegistry) {}
    
    public void collectInitialPrefetchPositions(int paramInt, LayoutPrefetchRegistry paramLayoutPrefetchRegistry) {}
    
    public int computeHorizontalScrollExtent(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeHorizontalScrollOffset(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeHorizontalScrollRange(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeVerticalScrollExtent(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeVerticalScrollOffset(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeVerticalScrollRange(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public void detachAndScrapAttachedViews(RecyclerView.Recycler paramRecycler)
    {
      for (int i = getChildCount() - 1; i >= 0; i--) {
        scrapOrRecycleView(paramRecycler, i, getChildAt(i));
      }
    }
    
    public void detachAndScrapView(View paramView, RecyclerView.Recycler paramRecycler)
    {
      scrapOrRecycleView(paramRecycler, this.mChildHelper.indexOfChild(paramView), paramView);
    }
    
    public void detachAndScrapViewAt(int paramInt, RecyclerView.Recycler paramRecycler)
    {
      scrapOrRecycleView(paramRecycler, paramInt, getChildAt(paramInt));
    }
    
    public void detachView(View paramView)
    {
      int i = this.mChildHelper.indexOfChild(paramView);
      if (i >= 0) {
        detachViewInternal(i, paramView);
      }
    }
    
    public void detachViewAt(int paramInt)
    {
      detachViewInternal(paramInt, getChildAt(paramInt));
    }
    
    void dispatchAttachedToWindow(RecyclerView paramRecyclerView)
    {
      this.mIsAttachedToWindow = true;
      onAttachedToWindow(paramRecyclerView);
    }
    
    void dispatchDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
    {
      this.mIsAttachedToWindow = false;
      onDetachedFromWindow(paramRecyclerView, paramRecycler);
    }
    
    public void endAnimation(View paramView)
    {
      if (this.mRecyclerView.mItemAnimator != null) {
        this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(paramView));
      }
    }
    
    public View findContainingItemView(View paramView)
    {
      if (this.mRecyclerView == null) {
        paramView = null;
      }
      for (;;)
      {
        return paramView;
        View localView = this.mRecyclerView.findContainingItemView(paramView);
        if (localView == null)
        {
          paramView = null;
        }
        else
        {
          paramView = localView;
          if (this.mChildHelper.isHidden(localView)) {
            paramView = null;
          }
        }
      }
    }
    
    public View findViewByPosition(int paramInt)
    {
      int i = getChildCount();
      int j = 0;
      View localView1;
      if (j < i)
      {
        localView1 = getChildAt(j);
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(localView1);
        if (localViewHolder == null) {}
        do
        {
          do
          {
            j++;
            break;
          } while ((localViewHolder.getLayoutPosition() != paramInt) || (localViewHolder.shouldIgnore()));
          localView2 = localView1;
          if (this.mRecyclerView.mState.isPreLayout()) {
            break label83;
          }
        } while (localViewHolder.isRemoved());
      }
      for (View localView2 = localView1;; localView2 = null) {
        label83:
        return localView2;
      }
    }
    
    public abstract RecyclerView.LayoutParams generateDefaultLayoutParams();
    
    public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      return new RecyclerView.LayoutParams(paramContext, paramAttributeSet);
    }
    
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      if ((paramLayoutParams instanceof RecyclerView.LayoutParams)) {
        paramLayoutParams = new RecyclerView.LayoutParams((RecyclerView.LayoutParams)paramLayoutParams);
      }
      for (;;)
      {
        return paramLayoutParams;
        if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
          paramLayoutParams = new RecyclerView.LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
        } else {
          paramLayoutParams = new RecyclerView.LayoutParams(paramLayoutParams);
        }
      }
    }
    
    public int getBaseline()
    {
      return -1;
    }
    
    public int getBottomDecorationHeight(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.bottom;
    }
    
    public View getChildAt(int paramInt)
    {
      if (this.mChildHelper != null) {}
      for (View localView = this.mChildHelper.getChildAt(paramInt);; localView = null) {
        return localView;
      }
    }
    
    public int getChildCount()
    {
      if (this.mChildHelper != null) {}
      for (int i = this.mChildHelper.getChildCount();; i = 0) {
        return i;
      }
    }
    
    public boolean getClipToPadding()
    {
      if ((this.mRecyclerView != null) && (this.mRecyclerView.mClipToPadding)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      int i = 1;
      int j = i;
      if (this.mRecyclerView != null)
      {
        if (this.mRecyclerView.mAdapter != null) {
          break label28;
        }
        j = i;
      }
      for (;;)
      {
        return j;
        label28:
        j = i;
        if (canScrollHorizontally()) {
          j = this.mRecyclerView.mAdapter.getItemCount();
        }
      }
    }
    
    public int getDecoratedBottom(View paramView)
    {
      return paramView.getBottom() + getBottomDecorationHeight(paramView);
    }
    
    public void getDecoratedBoundsWithMargins(View paramView, Rect paramRect)
    {
      RecyclerView.getDecoratedBoundsWithMarginsInt(paramView, paramRect);
    }
    
    public int getDecoratedLeft(View paramView)
    {
      return paramView.getLeft() - getLeftDecorationWidth(paramView);
    }
    
    public int getDecoratedMeasuredHeight(View paramView)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      return paramView.getMeasuredHeight() + localRect.top + localRect.bottom;
    }
    
    public int getDecoratedMeasuredWidth(View paramView)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      return paramView.getMeasuredWidth() + localRect.left + localRect.right;
    }
    
    public int getDecoratedRight(View paramView)
    {
      return paramView.getRight() + getRightDecorationWidth(paramView);
    }
    
    public int getDecoratedTop(View paramView)
    {
      return paramView.getTop() - getTopDecorationHeight(paramView);
    }
    
    public View getFocusedChild()
    {
      Object localObject;
      if (this.mRecyclerView == null) {
        localObject = null;
      }
      for (;;)
      {
        return (View)localObject;
        View localView = this.mRecyclerView.getFocusedChild();
        if (localView != null)
        {
          localObject = localView;
          if (!this.mChildHelper.isHidden(localView)) {}
        }
        else
        {
          localObject = null;
        }
      }
    }
    
    public int getHeight()
    {
      return this.mHeight;
    }
    
    public int getHeightMode()
    {
      return this.mHeightMode;
    }
    
    public int getItemCount()
    {
      RecyclerView.Adapter localAdapter;
      if (this.mRecyclerView != null)
      {
        localAdapter = this.mRecyclerView.getAdapter();
        if (localAdapter == null) {
          break label31;
        }
      }
      label31:
      for (int i = localAdapter.getItemCount();; i = 0)
      {
        return i;
        localAdapter = null;
        break;
      }
    }
    
    public int getItemViewType(View paramView)
    {
      return RecyclerView.getChildViewHolderInt(paramView).getItemViewType();
    }
    
    public int getLayoutDirection()
    {
      return ViewCompat.getLayoutDirection(this.mRecyclerView);
    }
    
    public int getLeftDecorationWidth(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.left;
    }
    
    public int getMinimumHeight()
    {
      return ViewCompat.getMinimumHeight(this.mRecyclerView);
    }
    
    public int getMinimumWidth()
    {
      return ViewCompat.getMinimumWidth(this.mRecyclerView);
    }
    
    public int getPaddingBottom()
    {
      if (this.mRecyclerView != null) {}
      for (int i = this.mRecyclerView.getPaddingBottom();; i = 0) {
        return i;
      }
    }
    
    public int getPaddingEnd()
    {
      if (this.mRecyclerView != null) {}
      for (int i = ViewCompat.getPaddingEnd(this.mRecyclerView);; i = 0) {
        return i;
      }
    }
    
    public int getPaddingLeft()
    {
      if (this.mRecyclerView != null) {}
      for (int i = this.mRecyclerView.getPaddingLeft();; i = 0) {
        return i;
      }
    }
    
    public int getPaddingRight()
    {
      if (this.mRecyclerView != null) {}
      for (int i = this.mRecyclerView.getPaddingRight();; i = 0) {
        return i;
      }
    }
    
    public int getPaddingStart()
    {
      if (this.mRecyclerView != null) {}
      for (int i = ViewCompat.getPaddingStart(this.mRecyclerView);; i = 0) {
        return i;
      }
    }
    
    public int getPaddingTop()
    {
      if (this.mRecyclerView != null) {}
      for (int i = this.mRecyclerView.getPaddingTop();; i = 0) {
        return i;
      }
    }
    
    public int getPosition(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).getViewLayoutPosition();
    }
    
    public int getRightDecorationWidth(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.right;
    }
    
    public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      int i = 1;
      int j = i;
      if (this.mRecyclerView != null)
      {
        if (this.mRecyclerView.mAdapter != null) {
          break label28;
        }
        j = i;
      }
      for (;;)
      {
        return j;
        label28:
        j = i;
        if (canScrollVertically()) {
          j = this.mRecyclerView.mAdapter.getItemCount();
        }
      }
    }
    
    public int getSelectionModeForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int getTopDecorationHeight(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.top;
    }
    
    public void getTransformedBoundingBox(View paramView, boolean paramBoolean, Rect paramRect)
    {
      Object localObject;
      if (paramBoolean)
      {
        localObject = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
        paramRect.set(-((Rect)localObject).left, -((Rect)localObject).top, paramView.getWidth() + ((Rect)localObject).right, paramView.getHeight() + ((Rect)localObject).bottom);
      }
      for (;;)
      {
        if (this.mRecyclerView != null)
        {
          localObject = paramView.getMatrix();
          if ((localObject != null) && (!((Matrix)localObject).isIdentity()))
          {
            RectF localRectF = this.mRecyclerView.mTempRectF;
            localRectF.set(paramRect);
            ((Matrix)localObject).mapRect(localRectF);
            paramRect.set((int)Math.floor(localRectF.left), (int)Math.floor(localRectF.top), (int)Math.ceil(localRectF.right), (int)Math.ceil(localRectF.bottom));
          }
        }
        paramRect.offset(paramView.getLeft(), paramView.getTop());
        return;
        paramRect.set(0, 0, paramView.getWidth(), paramView.getHeight());
      }
    }
    
    public int getWidth()
    {
      return this.mWidth;
    }
    
    public int getWidthMode()
    {
      return this.mWidthMode;
    }
    
    boolean hasFlexibleChildInBothOrientations()
    {
      int i = getChildCount();
      int j = 0;
      if (j < i)
      {
        ViewGroup.LayoutParams localLayoutParams = getChildAt(j).getLayoutParams();
        if ((localLayoutParams.width >= 0) || (localLayoutParams.height >= 0)) {}
      }
      for (boolean bool = true;; bool = false)
      {
        return bool;
        j++;
        break;
      }
    }
    
    public boolean hasFocus()
    {
      if ((this.mRecyclerView != null) && (this.mRecyclerView.hasFocus())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void ignoreView(View paramView)
    {
      if ((paramView.getParent() != this.mRecyclerView) || (this.mRecyclerView.indexOfChild(paramView) == -1)) {
        throw new IllegalArgumentException("View should be fully attached to be ignored" + this.mRecyclerView.exceptionLabel());
      }
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      paramView.addFlags(128);
      this.mRecyclerView.mViewInfoStore.removeViewHolder(paramView);
    }
    
    public boolean isAttachedToWindow()
    {
      return this.mIsAttachedToWindow;
    }
    
    public boolean isAutoMeasureEnabled()
    {
      return this.mAutoMeasure;
    }
    
    public boolean isFocused()
    {
      if ((this.mRecyclerView != null) && (this.mRecyclerView.isFocused())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public final boolean isItemPrefetchEnabled()
    {
      return this.mItemPrefetchEnabled;
    }
    
    public boolean isLayoutHierarchical(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return false;
    }
    
    public boolean isMeasurementCacheEnabled()
    {
      return this.mMeasurementCacheEnabled;
    }
    
    public boolean isSmoothScrolling()
    {
      if ((this.mSmoothScroller != null) && (this.mSmoothScroller.isRunning())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean isViewPartiallyVisible(View paramView, boolean paramBoolean1, boolean paramBoolean2)
    {
      boolean bool = true;
      if ((this.mHorizontalBoundCheck.isViewWithinBoundFlags(paramView, 24579)) && (this.mVerticalBoundCheck.isViewWithinBoundFlags(paramView, 24579))) {}
      for (paramBoolean2 = true; paramBoolean1; paramBoolean2 = false)
      {
        paramBoolean1 = paramBoolean2;
        return paramBoolean1;
      }
      if (!paramBoolean2) {}
      for (paramBoolean1 = bool;; paramBoolean1 = false) {
        break;
      }
    }
    
    public void layoutDecorated(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      paramView.layout(localRect.left + paramInt1, localRect.top + paramInt2, paramInt3 - localRect.right, paramInt4 - localRect.bottom);
    }
    
    public void layoutDecoratedWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = localLayoutParams.mDecorInsets;
      paramView.layout(localRect.left + paramInt1 + localLayoutParams.leftMargin, localRect.top + paramInt2 + localLayoutParams.topMargin, paramInt3 - localRect.right - localLayoutParams.rightMargin, paramInt4 - localRect.bottom - localLayoutParams.bottomMargin);
    }
    
    public void measureChild(View paramView, int paramInt1, int paramInt2)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = this.mRecyclerView.getItemDecorInsetsForChild(paramView);
      int i = localRect.left;
      int j = localRect.right;
      int k = localRect.top;
      int m = localRect.bottom;
      paramInt1 = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + (paramInt1 + (i + j)), localLayoutParams.width, canScrollHorizontally());
      paramInt2 = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + (paramInt2 + (k + m)), localLayoutParams.height, canScrollVertically());
      if (shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams)) {
        paramView.measure(paramInt1, paramInt2);
      }
    }
    
    public void measureChildWithMargins(View paramView, int paramInt1, int paramInt2)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = this.mRecyclerView.getItemDecorInsetsForChild(paramView);
      int i = localRect.left;
      int j = localRect.right;
      int k = localRect.top;
      int m = localRect.bottom;
      paramInt1 = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + localLayoutParams.leftMargin + localLayoutParams.rightMargin + (paramInt1 + (i + j)), localLayoutParams.width, canScrollHorizontally());
      paramInt2 = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + localLayoutParams.topMargin + localLayoutParams.bottomMargin + (paramInt2 + (k + m)), localLayoutParams.height, canScrollVertically());
      if (shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams)) {
        paramView.measure(paramInt1, paramInt2);
      }
    }
    
    public void moveView(int paramInt1, int paramInt2)
    {
      View localView = getChildAt(paramInt1);
      if (localView == null) {
        throw new IllegalArgumentException("Cannot move a child from non-existing index:" + paramInt1 + this.mRecyclerView.toString());
      }
      detachViewAt(paramInt1);
      attachView(localView, paramInt2);
    }
    
    public void offsetChildrenHorizontal(int paramInt)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.offsetChildrenHorizontal(paramInt);
      }
    }
    
    public void offsetChildrenVertical(int paramInt)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.offsetChildrenVertical(paramInt);
      }
    }
    
    public void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2) {}
    
    public boolean onAddFocusables(RecyclerView paramRecyclerView, ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
    {
      return false;
    }
    
    public void onAttachedToWindow(RecyclerView paramRecyclerView) {}
    
    @Deprecated
    public void onDetachedFromWindow(RecyclerView paramRecyclerView) {}
    
    public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
    {
      onDetachedFromWindow(paramRecyclerView);
    }
    
    public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return null;
    }
    
    public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramAccessibilityEvent);
    }
    
    public void onInitializeAccessibilityEvent(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AccessibilityEvent paramAccessibilityEvent)
    {
      boolean bool1 = true;
      if ((this.mRecyclerView == null) || (paramAccessibilityEvent == null)) {
        return;
      }
      boolean bool2 = bool1;
      if (!this.mRecyclerView.canScrollVertically(1))
      {
        bool2 = bool1;
        if (!this.mRecyclerView.canScrollVertically(-1))
        {
          bool2 = bool1;
          if (!this.mRecyclerView.canScrollHorizontally(-1)) {
            if (!this.mRecyclerView.canScrollHorizontally(1)) {
              break label108;
            }
          }
        }
      }
      label108:
      for (bool2 = bool1;; bool2 = false)
      {
        paramAccessibilityEvent.setScrollable(bool2);
        if (this.mRecyclerView.mAdapter == null) {
          break;
        }
        paramAccessibilityEvent.setItemCount(this.mRecyclerView.mAdapter.getItemCount());
        break;
      }
    }
    
    void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramAccessibilityNodeInfoCompat);
    }
    
    public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      if ((this.mRecyclerView.canScrollVertically(-1)) || (this.mRecyclerView.canScrollHorizontally(-1)))
      {
        paramAccessibilityNodeInfoCompat.addAction(8192);
        paramAccessibilityNodeInfoCompat.setScrollable(true);
      }
      if ((this.mRecyclerView.canScrollVertically(1)) || (this.mRecyclerView.canScrollHorizontally(1)))
      {
        paramAccessibilityNodeInfoCompat.addAction(4096);
        paramAccessibilityNodeInfoCompat.setScrollable(true);
      }
      paramAccessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(getRowCountForAccessibility(paramRecycler, paramState), getColumnCountForAccessibility(paramRecycler, paramState), isLayoutHierarchical(paramRecycler, paramState), getSelectionModeForAccessibility(paramRecycler, paramState)));
    }
    
    void onInitializeAccessibilityNodeInfoForItem(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if ((localViewHolder != null) && (!localViewHolder.isRemoved()) && (!this.mChildHelper.isHidden(localViewHolder.itemView))) {
        onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramView, paramAccessibilityNodeInfoCompat);
      }
    }
    
    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      int i;
      if (canScrollVertically())
      {
        i = getPosition(paramView);
        if (!canScrollHorizontally()) {
          break label51;
        }
      }
      label51:
      for (int j = getPosition(paramView);; j = 0)
      {
        paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(i, 1, j, 1, false, false));
        return;
        i = 0;
        break;
      }
    }
    
    public View onInterceptFocusSearch(View paramView, int paramInt)
    {
      return null;
    }
    
    public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
    
    public void onItemsChanged(RecyclerView paramRecyclerView) {}
    
    public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
    
    public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
    
    public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
    {
      onItemsUpdated(paramRecyclerView, paramInt1, paramInt2);
    }
    
    public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      Log.e("RecyclerView", "You must override onLayoutChildren(Recycler recycler, State state) ");
    }
    
    public void onLayoutCompleted(RecyclerView.State paramState) {}
    
    public void onMeasure(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2)
    {
      this.mRecyclerView.defaultOnMeasure(paramInt1, paramInt2);
    }
    
    @Deprecated
    public boolean onRequestChildFocus(RecyclerView paramRecyclerView, View paramView1, View paramView2)
    {
      if ((isSmoothScrolling()) || (paramRecyclerView.isComputingLayout())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean onRequestChildFocus(RecyclerView paramRecyclerView, RecyclerView.State paramState, View paramView1, View paramView2)
    {
      return onRequestChildFocus(paramRecyclerView, paramView1, paramView2);
    }
    
    public void onRestoreInstanceState(Parcelable paramParcelable) {}
    
    public Parcelable onSaveInstanceState()
    {
      return null;
    }
    
    public void onScrollStateChanged(int paramInt) {}
    
    boolean performAccessibilityAction(int paramInt, Bundle paramBundle)
    {
      return performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramInt, paramBundle);
    }
    
    public boolean performAccessibilityAction(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt, Bundle paramBundle)
    {
      boolean bool = false;
      if (this.mRecyclerView == null) {}
      label209:
      for (;;)
      {
        return bool;
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        switch (paramInt)
        {
        }
        for (;;)
        {
          if ((k == 0) && (m == 0)) {
            break label209;
          }
          this.mRecyclerView.scrollBy(m, k);
          bool = true;
          break;
          paramInt = i;
          if (this.mRecyclerView.canScrollVertically(-1)) {
            paramInt = -(getHeight() - getPaddingTop() - getPaddingBottom());
          }
          k = paramInt;
          if (this.mRecyclerView.canScrollHorizontally(-1))
          {
            m = -(getWidth() - getPaddingLeft() - getPaddingRight());
            k = paramInt;
            continue;
            paramInt = j;
            if (this.mRecyclerView.canScrollVertically(1)) {
              paramInt = getHeight() - getPaddingTop() - getPaddingBottom();
            }
            k = paramInt;
            if (this.mRecyclerView.canScrollHorizontally(1))
            {
              m = getWidth() - getPaddingLeft() - getPaddingRight();
              k = paramInt;
            }
          }
        }
      }
    }
    
    boolean performAccessibilityActionForItem(View paramView, int paramInt, Bundle paramBundle)
    {
      return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramView, paramInt, paramBundle);
    }
    
    public boolean performAccessibilityActionForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, int paramInt, Bundle paramBundle)
    {
      return false;
    }
    
    public void postOnAnimation(Runnable paramRunnable)
    {
      if (this.mRecyclerView != null) {
        ViewCompat.postOnAnimation(this.mRecyclerView, paramRunnable);
      }
    }
    
    public void removeAllViews()
    {
      for (int i = getChildCount() - 1; i >= 0; i--) {
        this.mChildHelper.removeViewAt(i);
      }
    }
    
    public void removeAndRecycleAllViews(RecyclerView.Recycler paramRecycler)
    {
      for (int i = getChildCount() - 1; i >= 0; i--) {
        if (!RecyclerView.getChildViewHolderInt(getChildAt(i)).shouldIgnore()) {
          removeAndRecycleViewAt(i, paramRecycler);
        }
      }
    }
    
    void removeAndRecycleScrapInt(RecyclerView.Recycler paramRecycler)
    {
      int i = paramRecycler.getScrapCount();
      int j = i - 1;
      if (j >= 0)
      {
        View localView = paramRecycler.getScrapViewAt(j);
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(localView);
        if (localViewHolder.shouldIgnore()) {}
        for (;;)
        {
          j--;
          break;
          localViewHolder.setIsRecyclable(false);
          if (localViewHolder.isTmpDetached()) {
            this.mRecyclerView.removeDetachedView(localView, false);
          }
          if (this.mRecyclerView.mItemAnimator != null) {
            this.mRecyclerView.mItemAnimator.endAnimation(localViewHolder);
          }
          localViewHolder.setIsRecyclable(true);
          paramRecycler.quickRecycleScrapView(localView);
        }
      }
      paramRecycler.clearScrap();
      if (i > 0) {
        this.mRecyclerView.invalidate();
      }
    }
    
    public void removeAndRecycleView(View paramView, RecyclerView.Recycler paramRecycler)
    {
      removeView(paramView);
      paramRecycler.recycleView(paramView);
    }
    
    public void removeAndRecycleViewAt(int paramInt, RecyclerView.Recycler paramRecycler)
    {
      View localView = getChildAt(paramInt);
      removeViewAt(paramInt);
      paramRecycler.recycleView(localView);
    }
    
    public boolean removeCallbacks(Runnable paramRunnable)
    {
      if (this.mRecyclerView != null) {}
      for (boolean bool = this.mRecyclerView.removeCallbacks(paramRunnable);; bool = false) {
        return bool;
      }
    }
    
    public void removeDetachedView(View paramView)
    {
      this.mRecyclerView.removeDetachedView(paramView, false);
    }
    
    public void removeView(View paramView)
    {
      this.mChildHelper.removeView(paramView);
    }
    
    public void removeViewAt(int paramInt)
    {
      if (getChildAt(paramInt) != null) {
        this.mChildHelper.removeViewAt(paramInt);
      }
    }
    
    public boolean requestChildRectangleOnScreen(RecyclerView paramRecyclerView, View paramView, Rect paramRect, boolean paramBoolean)
    {
      return requestChildRectangleOnScreen(paramRecyclerView, paramView, paramRect, paramBoolean, false);
    }
    
    public boolean requestChildRectangleOnScreen(RecyclerView paramRecyclerView, View paramView, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2)
    {
      boolean bool = false;
      paramView = getChildRectangleOnScreenScrollAmount(paramRecyclerView, paramView, paramRect, paramBoolean1);
      int i = paramView[0];
      int j = paramView[1];
      if (paramBoolean2)
      {
        paramBoolean2 = bool;
        if (!isFocusedChildVisibleAfterScrolling(paramRecyclerView, i, j)) {}
      }
      else if (i == 0)
      {
        paramBoolean2 = bool;
        if (j == 0) {}
      }
      else
      {
        if (!paramBoolean1) {
          break label77;
        }
        paramRecyclerView.scrollBy(i, j);
      }
      for (;;)
      {
        paramBoolean2 = true;
        return paramBoolean2;
        label77:
        paramRecyclerView.smoothScrollBy(i, j);
      }
    }
    
    public void requestLayout()
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.requestLayout();
      }
    }
    
    public void requestSimpleAnimationsInNextLayout()
    {
      this.mRequestedSimpleAnimations = true;
    }
    
    public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }
    
    public void scrollToPosition(int paramInt) {}
    
    public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }
    
    @Deprecated
    public void setAutoMeasureEnabled(boolean paramBoolean)
    {
      this.mAutoMeasure = paramBoolean;
    }
    
    void setExactMeasureSpecsFrom(RecyclerView paramRecyclerView)
    {
      setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(paramRecyclerView.getWidth(), NUM), View.MeasureSpec.makeMeasureSpec(paramRecyclerView.getHeight(), NUM));
    }
    
    public final void setItemPrefetchEnabled(boolean paramBoolean)
    {
      if (paramBoolean != this.mItemPrefetchEnabled)
      {
        this.mItemPrefetchEnabled = paramBoolean;
        this.mPrefetchMaxCountObserved = 0;
        if (this.mRecyclerView != null) {
          this.mRecyclerView.mRecycler.updateViewCacheSize();
        }
      }
    }
    
    void setMeasureSpecs(int paramInt1, int paramInt2)
    {
      this.mWidth = View.MeasureSpec.getSize(paramInt1);
      this.mWidthMode = View.MeasureSpec.getMode(paramInt1);
      if ((this.mWidthMode == 0) && (!RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC)) {
        this.mWidth = 0;
      }
      this.mHeight = View.MeasureSpec.getSize(paramInt2);
      this.mHeightMode = View.MeasureSpec.getMode(paramInt2);
      if ((this.mHeightMode == 0) && (!RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC)) {
        this.mHeight = 0;
      }
    }
    
    public void setMeasuredDimension(int paramInt1, int paramInt2)
    {
      this.mRecyclerView.setMeasuredDimension(paramInt1, paramInt2);
    }
    
    public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
    {
      int i = paramRect.width();
      int j = getPaddingLeft();
      int k = getPaddingRight();
      int m = paramRect.height();
      int n = getPaddingTop();
      int i1 = getPaddingBottom();
      setMeasuredDimension(chooseSize(paramInt1, i + j + k, getMinimumWidth()), chooseSize(paramInt2, m + n + i1, getMinimumHeight()));
    }
    
    void setMeasuredDimensionFromChildren(int paramInt1, int paramInt2)
    {
      int i = getChildCount();
      if (i == 0) {
        this.mRecyclerView.defaultOnMeasure(paramInt1, paramInt2);
      }
      for (;;)
      {
        return;
        int j = Integer.MAX_VALUE;
        int k = Integer.MAX_VALUE;
        int m = Integer.MIN_VALUE;
        int n = Integer.MIN_VALUE;
        int i1 = 0;
        while (i1 < i)
        {
          View localView = getChildAt(i1);
          Rect localRect = this.mRecyclerView.mTempRect;
          getDecoratedBoundsWithMargins(localView, localRect);
          int i2 = j;
          if (localRect.left < j) {
            i2 = localRect.left;
          }
          j = m;
          if (localRect.right > m) {
            j = localRect.right;
          }
          int i3 = k;
          if (localRect.top < k) {
            i3 = localRect.top;
          }
          k = n;
          if (localRect.bottom > n) {
            k = localRect.bottom;
          }
          i1++;
          m = j;
          n = k;
          j = i2;
          k = i3;
        }
        this.mRecyclerView.mTempRect.set(j, k, m, n);
        setMeasuredDimension(this.mRecyclerView.mTempRect, paramInt1, paramInt2);
      }
    }
    
    public void setMeasurementCacheEnabled(boolean paramBoolean)
    {
      this.mMeasurementCacheEnabled = paramBoolean;
    }
    
    void setRecyclerView(RecyclerView paramRecyclerView)
    {
      if (paramRecyclerView == null)
      {
        this.mRecyclerView = null;
        this.mChildHelper = null;
        this.mWidth = 0;
      }
      for (this.mHeight = 0;; this.mHeight = paramRecyclerView.getHeight())
      {
        this.mWidthMode = NUM;
        this.mHeightMode = NUM;
        return;
        this.mRecyclerView = paramRecyclerView;
        this.mChildHelper = paramRecyclerView.mChildHelper;
        this.mWidth = paramRecyclerView.getWidth();
      }
    }
    
    boolean shouldMeasureChild(View paramView, int paramInt1, int paramInt2, RecyclerView.LayoutParams paramLayoutParams)
    {
      if ((paramView.isLayoutRequested()) || (!this.mMeasurementCacheEnabled) || (!isMeasurementUpToDate(paramView.getWidth(), paramInt1, paramLayoutParams.width)) || (!isMeasurementUpToDate(paramView.getHeight(), paramInt2, paramLayoutParams.height))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean shouldMeasureTwice()
    {
      return false;
    }
    
    boolean shouldReMeasureChild(View paramView, int paramInt1, int paramInt2, RecyclerView.LayoutParams paramLayoutParams)
    {
      if ((!this.mMeasurementCacheEnabled) || (!isMeasurementUpToDate(paramView.getMeasuredWidth(), paramInt1, paramLayoutParams.width)) || (!isMeasurementUpToDate(paramView.getMeasuredHeight(), paramInt2, paramLayoutParams.height))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt)
    {
      Log.e("RecyclerView", "You must override smoothScrollToPosition to support smooth scrolling");
    }
    
    public void startSmoothScroll(RecyclerView.SmoothScroller paramSmoothScroller)
    {
      if ((this.mSmoothScroller != null) && (paramSmoothScroller != this.mSmoothScroller) && (this.mSmoothScroller.isRunning())) {
        this.mSmoothScroller.stop();
      }
      this.mSmoothScroller = paramSmoothScroller;
      this.mSmoothScroller.start(this.mRecyclerView, this);
    }
    
    public void stopIgnoringView(View paramView)
    {
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      paramView.stopIgnoring();
      paramView.resetInternal();
      paramView.addFlags(4);
    }
    
    void stopSmoothScroller()
    {
      if (this.mSmoothScroller != null) {
        this.mSmoothScroller.stop();
      }
    }
    
    public boolean supportsPredictiveItemAnimations()
    {
      return false;
    }
    
    public static abstract interface LayoutPrefetchRegistry
    {
      public abstract void addPosition(int paramInt1, int paramInt2);
    }
    
    public static class Properties
    {
      public int orientation;
      public boolean reverseLayout;
      public int spanCount;
      public boolean stackFromEnd;
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    final Rect mDecorInsets = new Rect();
    boolean mInsetsDirty = true;
    boolean mPendingInvalidate = false;
    RecyclerView.ViewHolder mViewHolder;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public int getViewAdapterPosition()
    {
      return this.mViewHolder.getAdapterPosition();
    }
    
    public int getViewLayoutPosition()
    {
      return this.mViewHolder.getLayoutPosition();
    }
    
    @Deprecated
    public int getViewPosition()
    {
      return this.mViewHolder.getPosition();
    }
    
    public boolean isItemChanged()
    {
      return this.mViewHolder.isUpdated();
    }
    
    public boolean isItemRemoved()
    {
      return this.mViewHolder.isRemoved();
    }
    
    public boolean isViewInvalid()
    {
      return this.mViewHolder.isInvalid();
    }
    
    public boolean viewNeedsUpdate()
    {
      return this.mViewHolder.needsUpdate();
    }
  }
  
  public static abstract interface OnChildAttachStateChangeListener
  {
    public abstract void onChildViewAttachedToWindow(View paramView);
    
    public abstract void onChildViewDetachedFromWindow(View paramView);
  }
  
  public static abstract class OnFlingListener
  {
    public abstract boolean onFling(int paramInt1, int paramInt2);
  }
  
  public static abstract interface OnItemTouchListener
  {
    public abstract boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent);
    
    public abstract void onRequestDisallowInterceptTouchEvent(boolean paramBoolean);
    
    public abstract void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent);
  }
  
  public static abstract class OnScrollListener
  {
    public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt) {}
    
    public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Orientation {}
  
  public static class RecycledViewPool
  {
    private static final int DEFAULT_MAX_SCRAP = 20;
    private int mAttachCount = 0;
    SparseArray<ScrapData> mScrap = new SparseArray();
    
    private ScrapData getScrapDataForType(int paramInt)
    {
      ScrapData localScrapData1 = (ScrapData)this.mScrap.get(paramInt);
      ScrapData localScrapData2 = localScrapData1;
      if (localScrapData1 == null)
      {
        localScrapData2 = new ScrapData();
        this.mScrap.put(paramInt, localScrapData2);
      }
      return localScrapData2;
    }
    
    void attach(RecyclerView.Adapter paramAdapter)
    {
      this.mAttachCount += 1;
    }
    
    public void clear()
    {
      for (int i = 0; i < this.mScrap.size(); i++) {
        ((ScrapData)this.mScrap.valueAt(i)).mScrapHeap.clear();
      }
    }
    
    void detach()
    {
      this.mAttachCount -= 1;
    }
    
    void factorInBindTime(int paramInt, long paramLong)
    {
      ScrapData localScrapData = getScrapDataForType(paramInt);
      localScrapData.mBindRunningAverageNs = runningAverage(localScrapData.mBindRunningAverageNs, paramLong);
    }
    
    void factorInCreateTime(int paramInt, long paramLong)
    {
      ScrapData localScrapData = getScrapDataForType(paramInt);
      localScrapData.mCreateRunningAverageNs = runningAverage(localScrapData.mCreateRunningAverageNs, paramLong);
    }
    
    public RecyclerView.ViewHolder getRecycledView(int paramInt)
    {
      Object localObject = (ScrapData)this.mScrap.get(paramInt);
      if ((localObject != null) && (!((ScrapData)localObject).mScrapHeap.isEmpty())) {
        localObject = ((ScrapData)localObject).mScrapHeap;
      }
      for (localObject = (RecyclerView.ViewHolder)((ArrayList)localObject).remove(((ArrayList)localObject).size() - 1);; localObject = null) {
        return (RecyclerView.ViewHolder)localObject;
      }
    }
    
    public int getRecycledViewCount(int paramInt)
    {
      return getScrapDataForType(paramInt).mScrapHeap.size();
    }
    
    void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2, boolean paramBoolean)
    {
      if (paramAdapter1 != null) {
        detach();
      }
      if ((!paramBoolean) && (this.mAttachCount == 0)) {
        clear();
      }
      if (paramAdapter2 != null) {
        attach(paramAdapter2);
      }
    }
    
    public void putRecycledView(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getItemViewType();
      ArrayList localArrayList = getScrapDataForType(i).mScrapHeap;
      if (((ScrapData)this.mScrap.get(i)).mMaxScrap <= localArrayList.size()) {}
      for (;;)
      {
        return;
        paramViewHolder.resetInternal();
        localArrayList.add(paramViewHolder);
      }
    }
    
    long runningAverage(long paramLong1, long paramLong2)
    {
      if (paramLong1 == 0L) {}
      for (;;)
      {
        return paramLong2;
        paramLong2 = paramLong1 / 4L * 3L + paramLong2 / 4L;
      }
    }
    
    public void setMaxRecycledViews(int paramInt1, int paramInt2)
    {
      Object localObject = getScrapDataForType(paramInt1);
      ((ScrapData)localObject).mMaxScrap = paramInt2;
      localObject = ((ScrapData)localObject).mScrapHeap;
      while (((ArrayList)localObject).size() > paramInt2) {
        ((ArrayList)localObject).remove(((ArrayList)localObject).size() - 1);
      }
    }
    
    int size()
    {
      int i = 0;
      int j = 0;
      while (j < this.mScrap.size())
      {
        ArrayList localArrayList = ((ScrapData)this.mScrap.valueAt(j)).mScrapHeap;
        int k = i;
        if (localArrayList != null) {
          k = i + localArrayList.size();
        }
        j++;
        i = k;
      }
      return i;
    }
    
    boolean willBindInTime(int paramInt, long paramLong1, long paramLong2)
    {
      long l = getScrapDataForType(paramInt).mBindRunningAverageNs;
      if ((l == 0L) || (paramLong1 + l < paramLong2)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean willCreateInTime(int paramInt, long paramLong1, long paramLong2)
    {
      long l = getScrapDataForType(paramInt).mCreateRunningAverageNs;
      if ((l == 0L) || (paramLong1 + l < paramLong2)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    static class ScrapData
    {
      long mBindRunningAverageNs = 0L;
      long mCreateRunningAverageNs = 0L;
      int mMaxScrap = 20;
      final ArrayList<RecyclerView.ViewHolder> mScrapHeap = new ArrayList();
    }
  }
  
  public final class Recycler
  {
    static final int DEFAULT_CACHE_SIZE = 2;
    final ArrayList<RecyclerView.ViewHolder> mAttachedScrap = new ArrayList();
    final ArrayList<RecyclerView.ViewHolder> mCachedViews = new ArrayList();
    ArrayList<RecyclerView.ViewHolder> mChangedScrap = null;
    RecyclerView.RecycledViewPool mRecyclerPool;
    private int mRequestedCacheMax = 2;
    private final List<RecyclerView.ViewHolder> mUnmodifiableAttachedScrap = Collections.unmodifiableList(this.mAttachedScrap);
    private RecyclerView.ViewCacheExtension mViewCacheExtension;
    int mViewCacheMax = 2;
    
    public Recycler() {}
    
    private void attachAccessibilityDelegateOnBind(RecyclerView.ViewHolder paramViewHolder)
    {
      if (RecyclerView.this.isAccessibilityEnabled())
      {
        View localView = paramViewHolder.itemView;
        if (ViewCompat.getImportantForAccessibility(localView) == 0) {
          ViewCompat.setImportantForAccessibility(localView, 1);
        }
        if (!ViewCompat.hasAccessibilityDelegate(localView))
        {
          paramViewHolder.addFlags(16384);
          ViewCompat.setAccessibilityDelegate(localView, RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
        }
      }
    }
    
    private void invalidateDisplayListInt(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      for (int i = paramViewGroup.getChildCount() - 1; i >= 0; i--)
      {
        View localView = paramViewGroup.getChildAt(i);
        if ((localView instanceof ViewGroup)) {
          invalidateDisplayListInt((ViewGroup)localView, true);
        }
      }
      if (!paramBoolean) {}
      for (;;)
      {
        return;
        if (paramViewGroup.getVisibility() == 4)
        {
          paramViewGroup.setVisibility(0);
          paramViewGroup.setVisibility(4);
        }
        else
        {
          i = paramViewGroup.getVisibility();
          paramViewGroup.setVisibility(4);
          paramViewGroup.setVisibility(i);
        }
      }
    }
    
    private void invalidateDisplayListInt(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.itemView instanceof ViewGroup)) {
        invalidateDisplayListInt((ViewGroup)paramViewHolder.itemView, false);
      }
    }
    
    private boolean tryBindViewHolderByDeadline(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, long paramLong)
    {
      paramViewHolder.mOwnerRecyclerView = RecyclerView.this;
      int i = paramViewHolder.getItemViewType();
      long l = RecyclerView.this.getNanoTime();
      if ((paramLong != Long.MAX_VALUE) && (!this.mRecyclerPool.willBindInTime(i, l, paramLong))) {}
      for (boolean bool = false;; bool = true)
      {
        return bool;
        RecyclerView.this.mAdapter.bindViewHolder(paramViewHolder, paramInt1);
        paramLong = RecyclerView.this.getNanoTime();
        this.mRecyclerPool.factorInBindTime(paramViewHolder.getItemViewType(), paramLong - l);
        attachAccessibilityDelegateOnBind(paramViewHolder);
        if (RecyclerView.this.mState.isPreLayout()) {
          paramViewHolder.mPreLayoutPosition = paramInt2;
        }
      }
    }
    
    void addViewHolderToRecycledViewPool(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
    {
      RecyclerView.clearNestedRecyclerViewIfNotNested(paramViewHolder);
      if (paramViewHolder.hasAnyOfTheFlags(16384))
      {
        paramViewHolder.setFlags(0, 16384);
        ViewCompat.setAccessibilityDelegate(paramViewHolder.itemView, null);
      }
      if (paramBoolean) {
        dispatchViewRecycled(paramViewHolder);
      }
      paramViewHolder.mOwnerRecyclerView = null;
      getRecycledViewPool().putRecycledView(paramViewHolder);
    }
    
    public void bindViewToPosition(View paramView, int paramInt)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder == null) {
        throw new IllegalArgumentException("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter" + RecyclerView.this.exceptionLabel());
      }
      int i = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
      if ((i < 0) || (i >= RecyclerView.this.mAdapter.getItemCount())) {
        throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + paramInt + "(offset:" + i + ").state:" + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
      }
      tryBindViewHolderByDeadline(localViewHolder, i, paramInt, Long.MAX_VALUE);
      paramView = localViewHolder.itemView.getLayoutParams();
      if (paramView == null)
      {
        paramView = (RecyclerView.LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
        localViewHolder.itemView.setLayoutParams(paramView);
        paramView.mInsetsDirty = true;
        paramView.mViewHolder = localViewHolder;
        if (localViewHolder.itemView.getParent() != null) {
          break label255;
        }
      }
      label255:
      for (boolean bool = true;; bool = false)
      {
        paramView.mPendingInvalidate = bool;
        return;
        if (!RecyclerView.this.checkLayoutParams(paramView))
        {
          paramView = (RecyclerView.LayoutParams)RecyclerView.this.generateLayoutParams(paramView);
          localViewHolder.itemView.setLayoutParams(paramView);
          break;
        }
        paramView = (RecyclerView.LayoutParams)paramView;
        break;
      }
    }
    
    public void clear()
    {
      this.mAttachedScrap.clear();
      recycleAndClearCachedViews();
    }
    
    void clearOldPositions()
    {
      int i = this.mCachedViews.size();
      for (int j = 0; j < i; j++) {
        ((RecyclerView.ViewHolder)this.mCachedViews.get(j)).clearOldPosition();
      }
      i = this.mAttachedScrap.size();
      for (j = 0; j < i; j++) {
        ((RecyclerView.ViewHolder)this.mAttachedScrap.get(j)).clearOldPosition();
      }
      if (this.mChangedScrap != null)
      {
        i = this.mChangedScrap.size();
        for (j = 0; j < i; j++) {
          ((RecyclerView.ViewHolder)this.mChangedScrap.get(j)).clearOldPosition();
        }
      }
    }
    
    void clearScrap()
    {
      this.mAttachedScrap.clear();
      if (this.mChangedScrap != null) {
        this.mChangedScrap.clear();
      }
    }
    
    public int convertPreLayoutPositionToPostLayout(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= RecyclerView.this.mState.getItemCount())) {
        throw new IndexOutOfBoundsException("invalid position " + paramInt + ". State item count is " + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
      }
      if (!RecyclerView.this.mState.isPreLayout()) {}
      for (;;)
      {
        return paramInt;
        paramInt = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
      }
    }
    
    void dispatchViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (RecyclerView.this.mRecyclerListener != null) {
        RecyclerView.this.mRecyclerListener.onViewRecycled(paramViewHolder);
      }
      if (RecyclerView.this.mAdapter != null) {
        RecyclerView.this.mAdapter.onViewRecycled(paramViewHolder);
      }
      if (RecyclerView.this.mState != null) {
        RecyclerView.this.mViewInfoStore.removeViewHolder(paramViewHolder);
      }
    }
    
    RecyclerView.ViewHolder getChangedScrapViewForPosition(int paramInt)
    {
      int i;
      RecyclerView.ViewHolder localViewHolder;
      if (this.mChangedScrap != null)
      {
        i = this.mChangedScrap.size();
        if (i != 0) {}
      }
      else
      {
        localViewHolder = null;
      }
      for (;;)
      {
        return localViewHolder;
        for (int j = 0;; j++)
        {
          if (j >= i) {
            break label75;
          }
          localViewHolder = (RecyclerView.ViewHolder)this.mChangedScrap.get(j);
          if ((!localViewHolder.wasReturnedFromScrap()) && (localViewHolder.getLayoutPosition() == paramInt))
          {
            localViewHolder.addFlags(32);
            break;
          }
        }
        label75:
        if (RecyclerView.this.mAdapter.hasStableIds())
        {
          paramInt = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
          if ((paramInt > 0) && (paramInt < RecyclerView.this.mAdapter.getItemCount()))
          {
            long l = RecyclerView.this.mAdapter.getItemId(paramInt);
            for (paramInt = 0;; paramInt++)
            {
              if (paramInt >= i) {
                break label182;
              }
              localViewHolder = (RecyclerView.ViewHolder)this.mChangedScrap.get(paramInt);
              if ((!localViewHolder.wasReturnedFromScrap()) && (localViewHolder.getItemId() == l))
              {
                localViewHolder.addFlags(32);
                break;
              }
            }
          }
        }
        label182:
        localViewHolder = null;
      }
    }
    
    RecyclerView.RecycledViewPool getRecycledViewPool()
    {
      if (this.mRecyclerPool == null) {
        this.mRecyclerPool = new RecyclerView.RecycledViewPool();
      }
      return this.mRecyclerPool;
    }
    
    int getScrapCount()
    {
      return this.mAttachedScrap.size();
    }
    
    public List<RecyclerView.ViewHolder> getScrapList()
    {
      return this.mUnmodifiableAttachedScrap;
    }
    
    RecyclerView.ViewHolder getScrapOrCachedViewForId(long paramLong, int paramInt, boolean paramBoolean)
    {
      int i = this.mAttachedScrap.size() - 1;
      RecyclerView.ViewHolder localViewHolder1;
      RecyclerView.ViewHolder localViewHolder2;
      if (i >= 0)
      {
        localViewHolder1 = (RecyclerView.ViewHolder)this.mAttachedScrap.get(i);
        if ((localViewHolder1.getItemId() == paramLong) && (!localViewHolder1.wasReturnedFromScrap())) {
          if (paramInt == localViewHolder1.getItemViewType())
          {
            localViewHolder1.addFlags(32);
            localViewHolder2 = localViewHolder1;
            if (localViewHolder1.isRemoved())
            {
              localViewHolder2 = localViewHolder1;
              if (!RecyclerView.this.mState.isPreLayout())
              {
                localViewHolder1.setFlags(2, 14);
                localViewHolder2 = localViewHolder1;
              }
            }
          }
        }
      }
      for (;;)
      {
        return localViewHolder2;
        if (!paramBoolean)
        {
          this.mAttachedScrap.remove(i);
          RecyclerView.this.removeDetachedView(localViewHolder1.itemView, false);
          quickRecycleScrapView(localViewHolder1.itemView);
        }
        i--;
        break;
        for (i = this.mCachedViews.size() - 1;; i--)
        {
          if (i < 0) {
            break label249;
          }
          localViewHolder1 = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
          if (localViewHolder1.getItemId() == paramLong)
          {
            if (paramInt == localViewHolder1.getItemViewType())
            {
              localViewHolder2 = localViewHolder1;
              if (paramBoolean) {
                break;
              }
              this.mCachedViews.remove(i);
              localViewHolder2 = localViewHolder1;
              break;
            }
            if (!paramBoolean)
            {
              recycleCachedViewAt(i);
              localViewHolder2 = null;
              break;
            }
          }
        }
        label249:
        localViewHolder2 = null;
      }
    }
    
    RecyclerView.ViewHolder getScrapOrHiddenOrCachedHolderForPosition(int paramInt, boolean paramBoolean)
    {
      int i = this.mAttachedScrap.size();
      int j = 0;
      Object localObject1;
      if (j < i)
      {
        localObject1 = (RecyclerView.ViewHolder)this.mAttachedScrap.get(j);
        if ((!((RecyclerView.ViewHolder)localObject1).wasReturnedFromScrap()) && (((RecyclerView.ViewHolder)localObject1).getLayoutPosition() == paramInt) && (!((RecyclerView.ViewHolder)localObject1).isInvalid()) && ((RecyclerView.this.mState.mInPreLayout) || (!((RecyclerView.ViewHolder)localObject1).isRemoved()))) {
          ((RecyclerView.ViewHolder)localObject1).addFlags(32);
        }
      }
      for (;;)
      {
        return (RecyclerView.ViewHolder)localObject1;
        j++;
        break;
        Object localObject2;
        if (!paramBoolean)
        {
          localObject2 = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(paramInt);
          if (localObject2 != null)
          {
            localObject1 = RecyclerView.getChildViewHolderInt((View)localObject2);
            RecyclerView.this.mChildHelper.unhide((View)localObject2);
            paramInt = RecyclerView.this.mChildHelper.indexOfChild((View)localObject2);
            if (paramInt == -1) {
              throw new IllegalStateException("layout index should not be -1 after unhiding a view:" + localObject1 + RecyclerView.this.exceptionLabel());
            }
            RecyclerView.this.mChildHelper.detachViewFromParent(paramInt);
            scrapView((View)localObject2);
            ((RecyclerView.ViewHolder)localObject1).addFlags(8224);
            continue;
          }
        }
        i = this.mCachedViews.size();
        for (j = 0;; j++)
        {
          if (j >= i) {
            break label298;
          }
          localObject2 = (RecyclerView.ViewHolder)this.mCachedViews.get(j);
          if ((!((RecyclerView.ViewHolder)localObject2).isInvalid()) && (((RecyclerView.ViewHolder)localObject2).getLayoutPosition() == paramInt))
          {
            localObject1 = localObject2;
            if (paramBoolean) {
              break;
            }
            this.mCachedViews.remove(j);
            localObject1 = localObject2;
            break;
          }
        }
        label298:
        localObject1 = null;
      }
    }
    
    View getScrapViewAt(int paramInt)
    {
      return ((RecyclerView.ViewHolder)this.mAttachedScrap.get(paramInt)).itemView;
    }
    
    public View getViewForPosition(int paramInt)
    {
      return getViewForPosition(paramInt, false);
    }
    
    View getViewForPosition(int paramInt, boolean paramBoolean)
    {
      return tryGetViewHolderForPositionByDeadline(paramInt, paramBoolean, Long.MAX_VALUE).itemView;
    }
    
    void markItemDecorInsetsDirty()
    {
      int i = this.mCachedViews.size();
      for (int j = 0; j < i; j++)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)((RecyclerView.ViewHolder)this.mCachedViews.get(j)).itemView.getLayoutParams();
        if (localLayoutParams != null) {
          localLayoutParams.mInsetsDirty = true;
        }
      }
    }
    
    void markKnownViewsInvalid()
    {
      int i = this.mCachedViews.size();
      for (int j = 0; j < i; j++)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(j);
        if (localViewHolder != null)
        {
          localViewHolder.addFlags(6);
          localViewHolder.addChangePayload(null);
        }
      }
      if ((RecyclerView.this.mAdapter == null) || (!RecyclerView.this.mAdapter.hasStableIds())) {
        recycleAndClearCachedViews();
      }
    }
    
    void offsetPositionRecordsForInsert(int paramInt1, int paramInt2)
    {
      int i = this.mCachedViews.size();
      for (int j = 0; j < i; j++)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(j);
        if ((localViewHolder != null) && (localViewHolder.mPosition >= paramInt1)) {
          localViewHolder.offsetPosition(paramInt2, true);
        }
      }
    }
    
    void offsetPositionRecordsForMove(int paramInt1, int paramInt2)
    {
      int i;
      int j;
      int k;
      int n;
      label25:
      RecyclerView.ViewHolder localViewHolder;
      if (paramInt1 < paramInt2)
      {
        i = paramInt1;
        j = paramInt2;
        k = -1;
        int m = this.mCachedViews.size();
        n = 0;
        if (n >= m) {
          return;
        }
        localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(n);
        if ((localViewHolder != null) && (localViewHolder.mPosition >= i) && (localViewHolder.mPosition <= j)) {
          break label87;
        }
      }
      for (;;)
      {
        n++;
        break label25;
        i = paramInt2;
        j = paramInt1;
        k = 1;
        break;
        label87:
        if (localViewHolder.mPosition == paramInt1) {
          localViewHolder.offsetPosition(paramInt2 - paramInt1, false);
        } else {
          localViewHolder.offsetPosition(k, false);
        }
      }
    }
    
    void offsetPositionRecordsForRemove(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      int i = this.mCachedViews.size() - 1;
      if (i >= 0)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder != null)
        {
          if (localViewHolder.mPosition < paramInt1 + paramInt2) {
            break label60;
          }
          localViewHolder.offsetPosition(-paramInt2, paramBoolean);
        }
        for (;;)
        {
          i--;
          break;
          label60:
          if (localViewHolder.mPosition >= paramInt1)
          {
            localViewHolder.addFlags(8);
            recycleCachedViewAt(i);
          }
        }
      }
    }
    
    void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2, boolean paramBoolean)
    {
      clear();
      getRecycledViewPool().onAdapterChanged(paramAdapter1, paramAdapter2, paramBoolean);
    }
    
    void quickRecycleScrapView(View paramView)
    {
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      RecyclerView.ViewHolder.access$1002(paramView, null);
      RecyclerView.ViewHolder.access$1102(paramView, false);
      paramView.clearReturnedFromScrapFlag();
      recycleViewHolderInternal(paramView);
    }
    
    void recycleAndClearCachedViews()
    {
      for (int i = this.mCachedViews.size() - 1; i >= 0; i--) {
        recycleCachedViewAt(i);
      }
      this.mCachedViews.clear();
      if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
        RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
      }
    }
    
    void recycleCachedViewAt(int paramInt)
    {
      addViewHolderToRecycledViewPool((RecyclerView.ViewHolder)this.mCachedViews.get(paramInt), true);
      this.mCachedViews.remove(paramInt);
    }
    
    public void recycleView(View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.isTmpDetached()) {
        RecyclerView.this.removeDetachedView(paramView, false);
      }
      if (localViewHolder.isScrap()) {
        localViewHolder.unScrap();
      }
      for (;;)
      {
        recycleViewHolderInternal(localViewHolder);
        return;
        if (localViewHolder.wasReturnedFromScrap()) {
          localViewHolder.clearReturnedFromScrapFlag();
        }
      }
    }
    
    void recycleViewHolderInternal(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool = false;
      if ((paramViewHolder.isScrap()) || (paramViewHolder.itemView.getParent() != null))
      {
        StringBuilder localStringBuilder = new StringBuilder().append("Scrapped or attached views may not be recycled. isScrap:").append(paramViewHolder.isScrap()).append(" isAttached:");
        if (paramViewHolder.itemView.getParent() != null) {
          bool = true;
        }
        throw new IllegalArgumentException(bool + RecyclerView.this.exceptionLabel());
      }
      if (paramViewHolder.isTmpDetached()) {
        throw new IllegalArgumentException("Tmp detached view should be removed from RecyclerView before it can be recycled: " + paramViewHolder + RecyclerView.this.exceptionLabel());
      }
      if (paramViewHolder.shouldIgnore()) {
        throw new IllegalArgumentException("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle." + RecyclerView.this.exceptionLabel());
      }
      bool = RecyclerView.ViewHolder.access$900(paramViewHolder);
      int i;
      int j;
      int m;
      int n;
      if ((RecyclerView.this.mAdapter != null) && (bool) && (RecyclerView.this.mAdapter.onFailedToRecycleView(paramViewHolder)))
      {
        i = 1;
        j = 0;
        int k = 0;
        m = 0;
        if (i == 0)
        {
          n = m;
          if (!paramViewHolder.isRecyclable()) {}
        }
        else
        {
          i = k;
          if (this.mViewCacheMax > 0)
          {
            i = k;
            if (!paramViewHolder.hasAnyOfTheFlags(526))
            {
              n = this.mCachedViews.size();
              i = n;
              if (n >= this.mViewCacheMax)
              {
                i = n;
                if (n > 0)
                {
                  recycleCachedViewAt(0);
                  i = n - 1;
                }
              }
              j = i;
              n = j;
              if (RecyclerView.ALLOW_THREAD_GAP_WORK)
              {
                n = j;
                if (i > 0)
                {
                  n = j;
                  if (!RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(paramViewHolder.mPosition)) {
                    i--;
                  }
                }
              }
            }
          }
        }
      }
      for (;;)
      {
        if (i >= 0)
        {
          n = ((RecyclerView.ViewHolder)this.mCachedViews.get(i)).mPosition;
          if (RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(n)) {}
        }
        else
        {
          n = i + 1;
          this.mCachedViews.add(n, paramViewHolder);
          i = 1;
          j = i;
          n = m;
          if (i == 0)
          {
            addViewHolderToRecycledViewPool(paramViewHolder, true);
            n = 1;
            j = i;
          }
          RecyclerView.this.mViewInfoStore.removeViewHolder(paramViewHolder);
          if ((j == 0) && (n == 0) && (bool)) {
            paramViewHolder.mOwnerRecyclerView = null;
          }
          return;
          i = 0;
          break;
        }
        i--;
      }
    }
    
    void recycleViewInternal(View paramView)
    {
      recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(paramView));
    }
    
    void scrapView(View paramView)
    {
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      if ((paramView.hasAnyOfTheFlags(12)) || (!paramView.isUpdated()) || (RecyclerView.this.canReuseUpdatedViewHolder(paramView)))
      {
        if ((paramView.isInvalid()) && (!paramView.isRemoved()) && (!RecyclerView.this.mAdapter.hasStableIds())) {
          throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool." + RecyclerView.this.exceptionLabel());
        }
        paramView.setScrapContainer(this, false);
        this.mAttachedScrap.add(paramView);
      }
      for (;;)
      {
        return;
        if (this.mChangedScrap == null) {
          this.mChangedScrap = new ArrayList();
        }
        paramView.setScrapContainer(this, true);
        this.mChangedScrap.add(paramView);
      }
    }
    
    void setRecycledViewPool(RecyclerView.RecycledViewPool paramRecycledViewPool)
    {
      if (this.mRecyclerPool != null) {
        this.mRecyclerPool.detach();
      }
      this.mRecyclerPool = paramRecycledViewPool;
      if (paramRecycledViewPool != null) {
        this.mRecyclerPool.attach(RecyclerView.this.getAdapter());
      }
    }
    
    void setViewCacheExtension(RecyclerView.ViewCacheExtension paramViewCacheExtension)
    {
      this.mViewCacheExtension = paramViewCacheExtension;
    }
    
    public void setViewCacheSize(int paramInt)
    {
      this.mRequestedCacheMax = paramInt;
      updateViewCacheSize();
    }
    
    RecyclerView.ViewHolder tryGetViewHolderForPositionByDeadline(int paramInt, boolean paramBoolean, long paramLong)
    {
      if ((paramInt < 0) || (paramInt >= RecyclerView.this.mState.getItemCount())) {
        throw new IndexOutOfBoundsException("Invalid item position " + paramInt + "(" + paramInt + "). Item count:" + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
      }
      int i = 0;
      Object localObject1 = null;
      int j;
      if (RecyclerView.this.mState.isPreLayout())
      {
        localObject1 = getChangedScrapViewForPosition(paramInt);
        if (localObject1 != null) {
          i = 1;
        }
      }
      else
      {
        localObject2 = localObject1;
        j = i;
        if (localObject1 == null)
        {
          localObject1 = getScrapOrHiddenOrCachedHolderForPosition(paramInt, paramBoolean);
          localObject2 = localObject1;
          j = i;
          if (localObject1 != null)
          {
            if (validateViewHolderForOffsetPosition((RecyclerView.ViewHolder)localObject1)) {
              break label344;
            }
            if (!paramBoolean)
            {
              ((RecyclerView.ViewHolder)localObject1).addFlags(4);
              if (!((RecyclerView.ViewHolder)localObject1).isScrap()) {
                break label328;
              }
              RecyclerView.this.removeDetachedView(((RecyclerView.ViewHolder)localObject1).itemView, false);
              ((RecyclerView.ViewHolder)localObject1).unScrap();
              label198:
              recycleViewHolderInternal((RecyclerView.ViewHolder)localObject1);
            }
            localObject2 = null;
            j = i;
          }
        }
      }
      int k;
      for (;;)
      {
        localObject1 = localObject2;
        i = j;
        if (localObject2 != null) {
          break label748;
        }
        k = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
        if ((k >= 0) && (k < RecyclerView.this.mAdapter.getItemCount())) {
          break label354;
        }
        throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + paramInt + "(offset:" + k + ").state:" + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
        i = 0;
        break;
        label328:
        if (!((RecyclerView.ViewHolder)localObject1).wasReturnedFromScrap()) {
          break label198;
        }
        ((RecyclerView.ViewHolder)localObject1).clearReturnedFromScrapFlag();
        break label198;
        label344:
        j = 1;
        localObject2 = localObject1;
      }
      label354:
      int m = RecyclerView.this.mAdapter.getItemViewType(k);
      localObject1 = localObject2;
      i = j;
      if (RecyclerView.this.mAdapter.hasStableIds())
      {
        localObject2 = getScrapOrCachedViewForId(RecyclerView.this.mAdapter.getItemId(k), m, paramBoolean);
        localObject1 = localObject2;
        i = j;
        if (localObject2 != null)
        {
          ((RecyclerView.ViewHolder)localObject2).mPosition = k;
          i = 1;
          localObject1 = localObject2;
        }
      }
      Object localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = localObject1;
        if (this.mViewCacheExtension != null)
        {
          View localView = this.mViewCacheExtension.getViewForPositionAndType(this, paramInt, m);
          localObject2 = localObject1;
          if (localView != null)
          {
            localObject1 = RecyclerView.this.getChildViewHolder(localView);
            if (localObject1 == null) {
              throw new IllegalArgumentException("getViewForPositionAndType returned a view which does not have a ViewHolder" + RecyclerView.this.exceptionLabel());
            }
            localObject2 = localObject1;
            if (((RecyclerView.ViewHolder)localObject1).shouldIgnore()) {
              throw new IllegalArgumentException("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view." + RecyclerView.this.exceptionLabel());
            }
          }
        }
      }
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject2 = getRecycledViewPool().getRecycledView(m);
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          ((RecyclerView.ViewHolder)localObject2).resetInternal();
          localObject1 = localObject2;
          if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST)
          {
            invalidateDisplayListInt((RecyclerView.ViewHolder)localObject2);
            localObject1 = localObject2;
          }
        }
      }
      if (localObject1 == null)
      {
        long l1 = RecyclerView.this.getNanoTime();
        if ((paramLong != Long.MAX_VALUE) && (!this.mRecyclerPool.willCreateInTime(m, l1, paramLong)))
        {
          localObject1 = null;
          return (RecyclerView.ViewHolder)localObject1;
        }
        localObject1 = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, m);
        if (RecyclerView.ALLOW_THREAD_GAP_WORK)
        {
          localObject2 = RecyclerView.findNestedRecyclerView(((RecyclerView.ViewHolder)localObject1).itemView);
          if (localObject2 != null) {
            ((RecyclerView.ViewHolder)localObject1).mNestedRecyclerView = new WeakReference(localObject2);
          }
        }
        long l2 = RecyclerView.this.getNanoTime();
        this.mRecyclerPool.factorInCreateTime(m, l2 - l1);
      }
      for (;;)
      {
        label748:
        if ((i != 0) && (!RecyclerView.this.mState.isPreLayout()) && (((RecyclerView.ViewHolder)localObject1).hasAnyOfTheFlags(8192)))
        {
          ((RecyclerView.ViewHolder)localObject1).setFlags(0, 8192);
          if (RecyclerView.this.mState.mRunSimpleAnimations)
          {
            j = RecyclerView.ItemAnimator.buildAdapterChangeFlagsForAnimations((RecyclerView.ViewHolder)localObject1);
            localObject2 = RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, (RecyclerView.ViewHolder)localObject1, j | 0x1000, ((RecyclerView.ViewHolder)localObject1).getUnmodifiedPayloads());
            RecyclerView.this.recordAnimationInfoIfBouncedHiddenView((RecyclerView.ViewHolder)localObject1, (RecyclerView.ItemAnimator.ItemHolderInfo)localObject2);
          }
        }
        paramBoolean = false;
        if ((RecyclerView.this.mState.isPreLayout()) && (((RecyclerView.ViewHolder)localObject1).isBound()))
        {
          ((RecyclerView.ViewHolder)localObject1).mPreLayoutPosition = paramInt;
          label878:
          localObject2 = ((RecyclerView.ViewHolder)localObject1).itemView.getLayoutParams();
          if (localObject2 != null) {
            break label989;
          }
          localObject2 = (RecyclerView.LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
          ((RecyclerView.ViewHolder)localObject1).itemView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
          label915:
          ((RecyclerView.LayoutParams)localObject2).mViewHolder = ((RecyclerView.ViewHolder)localObject1);
          if ((i == 0) || (!paramBoolean)) {
            break label1038;
          }
        }
        label989:
        label1038:
        for (paramBoolean = true;; paramBoolean = false)
        {
          ((RecyclerView.LayoutParams)localObject2).mPendingInvalidate = paramBoolean;
          break;
          if ((((RecyclerView.ViewHolder)localObject1).isBound()) && (!((RecyclerView.ViewHolder)localObject1).needsUpdate()) && (!((RecyclerView.ViewHolder)localObject1).isInvalid())) {
            break label878;
          }
          paramBoolean = tryBindViewHolderByDeadline((RecyclerView.ViewHolder)localObject1, RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt), paramInt, paramLong);
          break label878;
          if (!RecyclerView.this.checkLayoutParams((ViewGroup.LayoutParams)localObject2))
          {
            localObject2 = (RecyclerView.LayoutParams)RecyclerView.this.generateLayoutParams((ViewGroup.LayoutParams)localObject2);
            ((RecyclerView.ViewHolder)localObject1).itemView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
            break label915;
          }
          localObject2 = (RecyclerView.LayoutParams)localObject2;
          break label915;
        }
      }
    }
    
    void unscrapView(RecyclerView.ViewHolder paramViewHolder)
    {
      if (RecyclerView.ViewHolder.access$1100(paramViewHolder)) {
        this.mChangedScrap.remove(paramViewHolder);
      }
      for (;;)
      {
        RecyclerView.ViewHolder.access$1002(paramViewHolder, null);
        RecyclerView.ViewHolder.access$1102(paramViewHolder, false);
        paramViewHolder.clearReturnedFromScrapFlag();
        return;
        this.mAttachedScrap.remove(paramViewHolder);
      }
    }
    
    void updateViewCacheSize()
    {
      if (RecyclerView.this.mLayout != null) {}
      for (int i = RecyclerView.this.mLayout.mPrefetchMaxCountObserved;; i = 0)
      {
        this.mViewCacheMax = (this.mRequestedCacheMax + i);
        for (i = this.mCachedViews.size() - 1; (i >= 0) && (this.mCachedViews.size() > this.mViewCacheMax); i--) {
          recycleCachedViewAt(i);
        }
      }
    }
    
    boolean validateViewHolderForOffsetPosition(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool1 = true;
      boolean bool2;
      if (paramViewHolder.isRemoved()) {
        bool2 = RecyclerView.this.mState.isPreLayout();
      }
      for (;;)
      {
        return bool2;
        if ((paramViewHolder.mPosition < 0) || (paramViewHolder.mPosition >= RecyclerView.this.mAdapter.getItemCount())) {
          throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + paramViewHolder + RecyclerView.this.exceptionLabel());
        }
        if ((!RecyclerView.this.mState.isPreLayout()) && (RecyclerView.this.mAdapter.getItemViewType(paramViewHolder.mPosition) != paramViewHolder.getItemViewType()))
        {
          bool2 = false;
        }
        else
        {
          bool2 = bool1;
          if (RecyclerView.this.mAdapter.hasStableIds())
          {
            bool2 = bool1;
            if (paramViewHolder.getItemId() != RecyclerView.this.mAdapter.getItemId(paramViewHolder.mPosition)) {
              bool2 = false;
            }
          }
        }
      }
    }
    
    void viewRangeUpdate(int paramInt1, int paramInt2)
    {
      int i = this.mCachedViews.size() - 1;
      if (i >= 0)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder == null) {}
        for (;;)
        {
          i--;
          break;
          int j = localViewHolder.mPosition;
          if ((j >= paramInt1) && (j < paramInt1 + paramInt2))
          {
            localViewHolder.addFlags(2);
            recycleCachedViewAt(i);
          }
        }
      }
    }
  }
  
  public static abstract interface RecyclerListener
  {
    public abstract void onViewRecycled(RecyclerView.ViewHolder paramViewHolder);
  }
  
  private class RecyclerViewDataObserver
    extends RecyclerView.AdapterDataObserver
  {
    RecyclerViewDataObserver() {}
    
    public void onChanged()
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      RecyclerView.this.mState.mStructureChanged = true;
      RecyclerView.this.processDataSetCompletelyChanged(true);
      if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
        RecyclerView.this.requestLayout();
      }
    }
    
    public void onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(paramInt1, paramInt2, paramObject)) {
        triggerUpdateProcessor();
      }
    }
    
    public void onItemRangeInserted(int paramInt1, int paramInt2)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(paramInt1, paramInt2)) {
        triggerUpdateProcessor();
      }
    }
    
    public void onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(paramInt1, paramInt2, paramInt3)) {
        triggerUpdateProcessor();
      }
    }
    
    public void onItemRangeRemoved(int paramInt1, int paramInt2)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(paramInt1, paramInt2)) {
        triggerUpdateProcessor();
      }
    }
    
    void triggerUpdateProcessor()
    {
      if ((RecyclerView.POST_UPDATES_ON_ANIMATION) && (RecyclerView.this.mHasFixedSize) && (RecyclerView.this.mIsAttached)) {
        ViewCompat.postOnAnimation(RecyclerView.this, RecyclerView.this.mUpdateChildViewsRunnable);
      }
      for (;;)
      {
        return;
        RecyclerView.this.mAdapterUpdateDuringMeasure = true;
        RecyclerView.this.requestLayout();
      }
    }
  }
  
  public static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator()
    {
      public RecyclerView.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new RecyclerView.SavedState(paramAnonymousParcel, null);
      }
      
      public RecyclerView.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new RecyclerView.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public RecyclerView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new RecyclerView.SavedState[paramAnonymousInt];
      }
    };
    Parcelable mLayoutState;
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      if (paramClassLoader != null) {}
      for (;;)
      {
        this.mLayoutState = paramParcel.readParcelable(paramClassLoader);
        return;
        paramClassLoader = RecyclerView.LayoutManager.class.getClassLoader();
      }
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    void copyFrom(SavedState paramSavedState)
    {
      this.mLayoutState = paramSavedState.mLayoutState;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeParcelable(this.mLayoutState, 0);
    }
  }
  
  public static class SimpleOnItemTouchListener
    implements RecyclerView.OnItemTouchListener
  {
    public boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public void onRequestDisallowInterceptTouchEvent(boolean paramBoolean) {}
    
    public void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent) {}
  }
  
  public static abstract class SmoothScroller
  {
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean mPendingInitialRun;
    private RecyclerView mRecyclerView;
    private final Action mRecyclingAction = new Action(0, 0);
    private boolean mRunning;
    private int mTargetPosition = -1;
    private View mTargetView;
    
    private void onAnimation(int paramInt1, int paramInt2)
    {
      RecyclerView localRecyclerView = this.mRecyclerView;
      if ((!this.mRunning) || (this.mTargetPosition == -1) || (localRecyclerView == null)) {
        stop();
      }
      this.mPendingInitialRun = false;
      if (this.mTargetView != null)
      {
        if (getChildPosition(this.mTargetView) == this.mTargetPosition)
        {
          onTargetFound(this.mTargetView, localRecyclerView.mState, this.mRecyclingAction);
          this.mRecyclingAction.runIfNecessary(localRecyclerView);
          stop();
        }
      }
      else if (this.mRunning)
      {
        onSeekTargetStep(paramInt1, paramInt2, localRecyclerView.mState, this.mRecyclingAction);
        boolean bool = this.mRecyclingAction.hasJumpTarget();
        this.mRecyclingAction.runIfNecessary(localRecyclerView);
        if (bool)
        {
          if (!this.mRunning) {
            break label162;
          }
          this.mPendingInitialRun = true;
          localRecyclerView.mViewFlinger.postOnAnimation();
        }
      }
      for (;;)
      {
        return;
        Log.e("RecyclerView", "Passed over target position while smooth scrolling.");
        this.mTargetView = null;
        break;
        label162:
        stop();
      }
    }
    
    public View findViewByPosition(int paramInt)
    {
      return this.mRecyclerView.mLayout.findViewByPosition(paramInt);
    }
    
    public int getChildCount()
    {
      return this.mRecyclerView.mLayout.getChildCount();
    }
    
    public int getChildPosition(View paramView)
    {
      return this.mRecyclerView.getChildLayoutPosition(paramView);
    }
    
    public RecyclerView.LayoutManager getLayoutManager()
    {
      return this.mLayoutManager;
    }
    
    public int getTargetPosition()
    {
      return this.mTargetPosition;
    }
    
    @Deprecated
    public void instantScrollToPosition(int paramInt)
    {
      this.mRecyclerView.scrollToPosition(paramInt);
    }
    
    public boolean isPendingInitialRun()
    {
      return this.mPendingInitialRun;
    }
    
    public boolean isRunning()
    {
      return this.mRunning;
    }
    
    protected void normalize(PointF paramPointF)
    {
      float f = (float)Math.sqrt(paramPointF.x * paramPointF.x + paramPointF.y * paramPointF.y);
      paramPointF.x /= f;
      paramPointF.y /= f;
    }
    
    protected void onChildAttachedToWindow(View paramView)
    {
      if (getChildPosition(paramView) == getTargetPosition()) {
        this.mTargetView = paramView;
      }
    }
    
    protected abstract void onSeekTargetStep(int paramInt1, int paramInt2, RecyclerView.State paramState, Action paramAction);
    
    protected abstract void onStart();
    
    protected abstract void onStop();
    
    protected abstract void onTargetFound(View paramView, RecyclerView.State paramState, Action paramAction);
    
    public void setTargetPosition(int paramInt)
    {
      this.mTargetPosition = paramInt;
    }
    
    void start(RecyclerView paramRecyclerView, RecyclerView.LayoutManager paramLayoutManager)
    {
      this.mRecyclerView = paramRecyclerView;
      this.mLayoutManager = paramLayoutManager;
      if (this.mTargetPosition == -1) {
        throw new IllegalArgumentException("Invalid target position");
      }
      RecyclerView.State.access$1302(this.mRecyclerView.mState, this.mTargetPosition);
      this.mRunning = true;
      this.mPendingInitialRun = true;
      this.mTargetView = findViewByPosition(getTargetPosition());
      onStart();
      this.mRecyclerView.mViewFlinger.postOnAnimation();
    }
    
    protected final void stop()
    {
      if (!this.mRunning) {}
      for (;;)
      {
        return;
        this.mRunning = false;
        onStop();
        RecyclerView.State.access$1302(this.mRecyclerView.mState, -1);
        this.mTargetView = null;
        this.mTargetPosition = -1;
        this.mPendingInitialRun = false;
        this.mLayoutManager.onSmoothScrollerStopped(this);
        this.mLayoutManager = null;
        this.mRecyclerView = null;
      }
    }
    
    public static class Action
    {
      public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
      private boolean mChanged = false;
      private int mConsecutiveUpdates = 0;
      private int mDuration;
      private int mDx;
      private int mDy;
      private Interpolator mInterpolator;
      private int mJumpToPosition = -1;
      
      public Action(int paramInt1, int paramInt2)
      {
        this(paramInt1, paramInt2, Integer.MIN_VALUE, null);
      }
      
      public Action(int paramInt1, int paramInt2, int paramInt3)
      {
        this(paramInt1, paramInt2, paramInt3, null);
      }
      
      public Action(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
      {
        this.mDx = paramInt1;
        this.mDy = paramInt2;
        this.mDuration = paramInt3;
        this.mInterpolator = paramInterpolator;
      }
      
      private void validate()
      {
        if ((this.mInterpolator != null) && (this.mDuration < 1)) {
          throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
        }
        if (this.mDuration < 1) {
          throw new IllegalStateException("Scroll duration must be a positive number");
        }
      }
      
      public int getDuration()
      {
        return this.mDuration;
      }
      
      public int getDx()
      {
        return this.mDx;
      }
      
      public int getDy()
      {
        return this.mDy;
      }
      
      public Interpolator getInterpolator()
      {
        return this.mInterpolator;
      }
      
      boolean hasJumpTarget()
      {
        if (this.mJumpToPosition >= 0) {}
        for (boolean bool = true;; bool = false) {
          return bool;
        }
      }
      
      public void jumpTo(int paramInt)
      {
        this.mJumpToPosition = paramInt;
      }
      
      void runIfNecessary(RecyclerView paramRecyclerView)
      {
        if (this.mJumpToPosition >= 0)
        {
          int i = this.mJumpToPosition;
          this.mJumpToPosition = -1;
          paramRecyclerView.jumpToPositionForSmoothScroller(i);
          this.mChanged = false;
        }
        for (;;)
        {
          return;
          if (this.mChanged)
          {
            validate();
            if (this.mInterpolator == null) {
              if (this.mDuration == Integer.MIN_VALUE) {
                paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
              }
            }
            for (;;)
            {
              this.mConsecutiveUpdates += 1;
              if (this.mConsecutiveUpdates > 10) {
                Log.e("RecyclerView", "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
              }
              this.mChanged = false;
              break;
              paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
              continue;
              paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
            }
          }
          this.mConsecutiveUpdates = 0;
        }
      }
      
      public void setDuration(int paramInt)
      {
        this.mChanged = true;
        this.mDuration = paramInt;
      }
      
      public void setDx(int paramInt)
      {
        this.mChanged = true;
        this.mDx = paramInt;
      }
      
      public void setDy(int paramInt)
      {
        this.mChanged = true;
        this.mDy = paramInt;
      }
      
      public void setInterpolator(Interpolator paramInterpolator)
      {
        this.mChanged = true;
        this.mInterpolator = paramInterpolator;
      }
      
      public void update(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
      {
        this.mDx = paramInt1;
        this.mDy = paramInt2;
        this.mDuration = paramInt3;
        this.mInterpolator = paramInterpolator;
        this.mChanged = true;
      }
    }
    
    public static abstract interface ScrollVectorProvider
    {
      public abstract PointF computeScrollVectorForPosition(int paramInt);
    }
  }
  
  public static class State
  {
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
    
    void assertLayoutStep(int paramInt)
    {
      if ((this.mLayoutStep & paramInt) == 0) {
        throw new IllegalStateException("Layout state should be one of " + Integer.toBinaryString(paramInt) + " but it is " + Integer.toBinaryString(this.mLayoutStep));
      }
    }
    
    public boolean didStructureChange()
    {
      return this.mStructureChanged;
    }
    
    public <T> T get(int paramInt)
    {
      if (this.mData == null) {}
      for (Object localObject = null;; localObject = this.mData.get(paramInt)) {
        return (T)localObject;
      }
    }
    
    public int getItemCount()
    {
      if (this.mInPreLayout) {}
      for (int i = this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout;; i = this.mItemCount) {
        return i;
      }
    }
    
    public int getRemainingScrollHorizontal()
    {
      return this.mRemainingScrollHorizontal;
    }
    
    public int getRemainingScrollVertical()
    {
      return this.mRemainingScrollVertical;
    }
    
    public int getTargetScrollPosition()
    {
      return this.mTargetPosition;
    }
    
    public boolean hasTargetScrollPosition()
    {
      if (this.mTargetPosition != -1) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean isMeasuring()
    {
      return this.mIsMeasuring;
    }
    
    public boolean isPreLayout()
    {
      return this.mInPreLayout;
    }
    
    void prepareForNestedPrefetch(RecyclerView.Adapter paramAdapter)
    {
      this.mLayoutStep = 1;
      this.mItemCount = paramAdapter.getItemCount();
      this.mInPreLayout = false;
      this.mTrackOldChangeHolders = false;
      this.mIsMeasuring = false;
    }
    
    public void put(int paramInt, Object paramObject)
    {
      if (this.mData == null) {
        this.mData = new SparseArray();
      }
      this.mData.put(paramInt, paramObject);
    }
    
    public void remove(int paramInt)
    {
      if (this.mData == null) {}
      for (;;)
      {
        return;
        this.mData.remove(paramInt);
      }
    }
    
    State reset()
    {
      this.mTargetPosition = -1;
      if (this.mData != null) {
        this.mData.clear();
      }
      this.mItemCount = 0;
      this.mStructureChanged = false;
      this.mIsMeasuring = false;
      return this;
    }
    
    public String toString()
    {
      return "State{mTargetPosition=" + this.mTargetPosition + ", mData=" + this.mData + ", mItemCount=" + this.mItemCount + ", mIsMeasuring=" + this.mIsMeasuring + ", mPreviousLayoutItemCount=" + this.mPreviousLayoutItemCount + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.mDeletedInvisibleItemCountSincePreviousLayout + ", mStructureChanged=" + this.mStructureChanged + ", mInPreLayout=" + this.mInPreLayout + ", mRunSimpleAnimations=" + this.mRunSimpleAnimations + ", mRunPredictiveAnimations=" + this.mRunPredictiveAnimations + '}';
    }
    
    public boolean willRunPredictiveAnimations()
    {
      return this.mRunPredictiveAnimations;
    }
    
    public boolean willRunSimpleAnimations()
    {
      return this.mRunSimpleAnimations;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    static @interface LayoutState {}
  }
  
  public static abstract class ViewCacheExtension
  {
    public abstract View getViewForPositionAndType(RecyclerView.Recycler paramRecycler, int paramInt1, int paramInt2);
  }
  
  class ViewFlinger
    implements Runnable
  {
    private boolean mEatRunOnAnimationRequest = false;
    Interpolator mInterpolator = RecyclerView.sQuinticInterpolator;
    private int mLastFlingX;
    private int mLastFlingY;
    private boolean mReSchedulePostAnimationCallback = false;
    private OverScroller mScroller = new OverScroller(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
    
    ViewFlinger() {}
    
    private int computeScrollDuration(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = Math.abs(paramInt1);
      int j = Math.abs(paramInt2);
      int k;
      if (i > j)
      {
        k = 1;
        paramInt3 = (int)Math.sqrt(paramInt3 * paramInt3 + paramInt4 * paramInt4);
        paramInt2 = (int)Math.sqrt(paramInt1 * paramInt1 + paramInt2 * paramInt2);
        if (k == 0) {
          break label140;
        }
      }
      label140:
      for (paramInt1 = RecyclerView.this.getWidth();; paramInt1 = RecyclerView.this.getHeight())
      {
        paramInt4 = paramInt1 / 2;
        float f1 = Math.min(1.0F, 1.0F * paramInt2 / paramInt1);
        float f2 = paramInt4;
        float f3 = paramInt4;
        f1 = distanceInfluenceForSnapDuration(f1);
        if (paramInt3 <= 0) {
          break label151;
        }
        paramInt1 = Math.round(1000.0F * Math.abs((f2 + f3 * f1) / paramInt3)) * 4;
        return Math.min(paramInt1, 2000);
        k = 0;
        break;
      }
      label151:
      if (k != 0) {}
      for (paramInt2 = i;; paramInt2 = j)
      {
        paramInt1 = (int)((paramInt2 / paramInt1 + 1.0F) * 300.0F);
        break;
      }
    }
    
    private void disableRunOnAnimationRequests()
    {
      this.mReSchedulePostAnimationCallback = false;
      this.mEatRunOnAnimationRequest = true;
    }
    
    private float distanceInfluenceForSnapDuration(float paramFloat)
    {
      return (float)Math.sin((paramFloat - 0.5F) * 0.47123894F);
    }
    
    private void enableRunOnAnimationRequests()
    {
      this.mEatRunOnAnimationRequest = false;
      if (this.mReSchedulePostAnimationCallback) {
        postOnAnimation();
      }
    }
    
    public void fling(int paramInt1, int paramInt2)
    {
      RecyclerView.this.setScrollState(2);
      this.mLastFlingY = 0;
      this.mLastFlingX = 0;
      this.mScroller.fling(0, 0, paramInt1, paramInt2, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
      postOnAnimation();
    }
    
    void postOnAnimation()
    {
      if (this.mEatRunOnAnimationRequest) {
        this.mReSchedulePostAnimationCallback = true;
      }
      for (;;)
      {
        return;
        RecyclerView.this.removeCallbacks(this);
        ViewCompat.postOnAnimation(RecyclerView.this, this);
      }
    }
    
    public void run()
    {
      if (RecyclerView.this.mLayout == null)
      {
        stop();
        return;
      }
      disableRunOnAnimationRequests();
      RecyclerView.this.consumePendingUpdateOperations();
      OverScroller localOverScroller = this.mScroller;
      RecyclerView.SmoothScroller localSmoothScroller = RecyclerView.this.mLayout.mSmoothScroller;
      int i;
      int j;
      int k;
      int m;
      int n;
      int i1;
      int i2;
      int i3;
      int i4;
      int i5;
      int i6;
      int i7;
      if (localOverScroller.computeScrollOffset())
      {
        int[] arrayOfInt = RecyclerView.this.mScrollConsumed;
        i = localOverScroller.getCurrX();
        j = localOverScroller.getCurrY();
        k = i - this.mLastFlingX;
        m = j - this.mLastFlingY;
        n = 0;
        i1 = 0;
        this.mLastFlingX = i;
        this.mLastFlingY = j;
        i2 = 0;
        i3 = 0;
        i4 = 0;
        i5 = 0;
        i6 = k;
        i7 = m;
        if (RecyclerView.this.dispatchNestedPreScroll(k, m, arrayOfInt, null, 1))
        {
          i6 = k - arrayOfInt[0];
          i7 = m - arrayOfInt[1];
        }
        if (RecyclerView.this.mAdapter == null) {
          break label977;
        }
        RecyclerView.this.startInterceptRequestLayout();
        RecyclerView.this.onEnterLayoutOrScroll();
        TraceCompat.beginSection("RV Scroll");
        RecyclerView.this.fillRemainingScrollValues(RecyclerView.this.mState);
        if (i6 == 0) {
          break label971;
        }
        m = RecyclerView.this.mLayout.scrollHorizontallyBy(i6, RecyclerView.this.mRecycler, RecyclerView.this.mState);
        i3 = i6 - m;
        label241:
        if (i7 != 0)
        {
          i1 = RecyclerView.this.mLayout.scrollVerticallyBy(i7, RecyclerView.this.mRecycler, RecyclerView.this.mState);
          i5 = i7 - i1;
        }
        TraceCompat.endSection();
        RecyclerView.this.repositionShadowingViews();
        RecyclerView.this.onExitLayoutOrScroll();
        RecyclerView.this.stopInterceptRequestLayout(false);
        k = m;
        n = i1;
        i2 = i3;
        i4 = i5;
        if (localSmoothScroller != null)
        {
          k = m;
          n = i1;
          i2 = i3;
          i4 = i5;
          if (!localSmoothScroller.isPendingInitialRun())
          {
            k = m;
            n = i1;
            i2 = i3;
            i4 = i5;
            if (localSmoothScroller.isRunning())
            {
              k = RecyclerView.this.mState.getItemCount();
              if (k != 0) {
                break label799;
              }
              localSmoothScroller.stop();
              i4 = i5;
              i2 = i3;
              n = i1;
              k = m;
            }
          }
        }
      }
      for (;;)
      {
        label409:
        if (!RecyclerView.this.mItemDecorations.isEmpty()) {
          RecyclerView.this.invalidate();
        }
        if (RecyclerView.this.getOverScrollMode() != 2) {
          RecyclerView.this.considerReleasingGlowsOnScroll(i6, i7);
        }
        if ((!RecyclerView.this.dispatchNestedScroll(k, n, i2, i4, null, 1)) && ((i2 != 0) || (i4 != 0)))
        {
          i5 = (int)localOverScroller.getCurrVelocity();
          m = 0;
          if (i2 != i)
          {
            if (i2 >= 0) {
              break label882;
            }
            m = -i5;
          }
          label508:
          i3 = 0;
          if (i4 != j)
          {
            if (i4 >= 0) {
              break label900;
            }
            i3 = -i5;
          }
          label528:
          if (RecyclerView.this.getOverScrollMode() != 2) {
            RecyclerView.this.absorbGlows(m, i3);
          }
          if (((m != 0) || (i2 == i) || (localOverScroller.getFinalX() == 0)) && ((i3 != 0) || (i4 == j) || (localOverScroller.getFinalY() == 0))) {
            localOverScroller.abortAnimation();
          }
        }
        if ((k != 0) || (n != 0)) {
          RecyclerView.this.dispatchOnScrolled(k, n);
        }
        if (!RecyclerView.this.awakenScrollBars()) {
          RecyclerView.this.invalidate();
        }
        if ((i7 != 0) && (RecyclerView.this.mLayout.canScrollVertically()) && (n == i7))
        {
          m = 1;
          label658:
          if ((i6 == 0) || (!RecyclerView.this.mLayout.canScrollHorizontally()) || (k != i6)) {
            break label924;
          }
          i3 = 1;
          label686:
          if (((i6 != 0) || (i7 != 0)) && (i3 == 0) && (m == 0)) {
            break label930;
          }
          m = 1;
          label709:
          if ((!localOverScroller.isFinished()) && ((m != 0) || (RecyclerView.this.hasNestedScrollingParent(1)))) {
            break label936;
          }
          RecyclerView.this.setScrollState(0);
          if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
            RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
          }
          RecyclerView.this.stopNestedScroll(1);
        }
        for (;;)
        {
          if (localSmoothScroller != null)
          {
            if (localSmoothScroller.isPendingInitialRun()) {
              localSmoothScroller.onAnimation(0, 0);
            }
            if (!this.mReSchedulePostAnimationCallback) {
              localSmoothScroller.stop();
            }
          }
          enableRunOnAnimationRequests();
          break;
          label799:
          if (localSmoothScroller.getTargetPosition() >= k)
          {
            localSmoothScroller.setTargetPosition(k - 1);
            localSmoothScroller.onAnimation(i6 - i3, i7 - i5);
            k = m;
            n = i1;
            i2 = i3;
            i4 = i5;
            break label409;
          }
          localSmoothScroller.onAnimation(i6 - i3, i7 - i5);
          k = m;
          n = i1;
          i2 = i3;
          i4 = i5;
          break label409;
          label882:
          if (i2 > 0)
          {
            m = i5;
            break label508;
          }
          m = 0;
          break label508;
          label900:
          if (i4 > 0)
          {
            i3 = i5;
            break label528;
          }
          i3 = 0;
          break label528;
          m = 0;
          break label658;
          label924:
          i3 = 0;
          break label686;
          label930:
          m = 0;
          break label709;
          label936:
          postOnAnimation();
          if (RecyclerView.this.mGapWorker != null) {
            RecyclerView.this.mGapWorker.postFromTraversal(RecyclerView.this, i6, i7);
          }
        }
        label971:
        m = 0;
        break label241;
        label977:
        k = 0;
      }
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2)
    {
      smoothScrollBy(paramInt1, paramInt2, 0, 0);
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3)
    {
      smoothScrollBy(paramInt1, paramInt2, paramInt3, RecyclerView.sQuinticInterpolator);
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      smoothScrollBy(paramInt1, paramInt2, computeScrollDuration(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
    {
      if (this.mInterpolator != paramInterpolator)
      {
        this.mInterpolator = paramInterpolator;
        this.mScroller = new OverScroller(RecyclerView.this.getContext(), paramInterpolator);
      }
      RecyclerView.this.setScrollState(2);
      this.mLastFlingY = 0;
      this.mLastFlingX = 0;
      this.mScroller.startScroll(0, 0, paramInt1, paramInt2, paramInt3);
      if (Build.VERSION.SDK_INT < 23) {
        this.mScroller.computeScrollOffset();
      }
      postOnAnimation();
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, Interpolator paramInterpolator)
    {
      int i = computeScrollDuration(paramInt1, paramInt2, 0, 0);
      Interpolator localInterpolator = paramInterpolator;
      if (paramInterpolator == null) {
        localInterpolator = RecyclerView.sQuinticInterpolator;
      }
      smoothScrollBy(paramInt1, paramInt2, i, localInterpolator);
    }
    
    public void stop()
    {
      RecyclerView.this.removeCallbacks(this);
      this.mScroller.abortAnimation();
    }
  }
  
  public static abstract class ViewHolder
  {
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
    long mItemId = -1L;
    int mItemViewType = -1;
    WeakReference<RecyclerView> mNestedRecyclerView;
    int mOldPosition = -1;
    RecyclerView mOwnerRecyclerView;
    List<Object> mPayloads = null;
    int mPendingAccessibilityState = -1;
    int mPosition = -1;
    int mPreLayoutPosition = -1;
    private RecyclerView.Recycler mScrapContainer = null;
    ViewHolder mShadowedHolder = null;
    ViewHolder mShadowingHolder = null;
    List<Object> mUnmodifiedPayloads = null;
    private int mWasImportantForAccessibilityBeforeHidden = 0;
    
    public ViewHolder(View paramView)
    {
      if (paramView == null) {
        throw new IllegalArgumentException("itemView may not be null");
      }
      this.itemView = paramView;
    }
    
    private void createPayloadsIfNeeded()
    {
      if (this.mPayloads == null)
      {
        this.mPayloads = new ArrayList();
        this.mUnmodifiedPayloads = Collections.unmodifiableList(this.mPayloads);
      }
    }
    
    private boolean doesTransientStatePreventRecycling()
    {
      if (((this.mFlags & 0x10) == 0) && (ViewCompat.hasTransientState(this.itemView))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    private void onEnteredHiddenState(RecyclerView paramRecyclerView)
    {
      if (this.mPendingAccessibilityState != -1) {}
      for (this.mWasImportantForAccessibilityBeforeHidden = this.mPendingAccessibilityState;; this.mWasImportantForAccessibilityBeforeHidden = ViewCompat.getImportantForAccessibility(this.itemView))
      {
        paramRecyclerView.setChildImportantForAccessibilityInternal(this, 4);
        return;
      }
    }
    
    private void onLeftHiddenState(RecyclerView paramRecyclerView)
    {
      paramRecyclerView.setChildImportantForAccessibilityInternal(this, this.mWasImportantForAccessibilityBeforeHidden);
      this.mWasImportantForAccessibilityBeforeHidden = 0;
    }
    
    private boolean shouldBeKeptAsChild()
    {
      if ((this.mFlags & 0x10) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    void addChangePayload(Object paramObject)
    {
      if (paramObject == null) {
        addFlags(1024);
      }
      for (;;)
      {
        return;
        if ((this.mFlags & 0x400) == 0)
        {
          createPayloadsIfNeeded();
          this.mPayloads.add(paramObject);
        }
      }
    }
    
    void addFlags(int paramInt)
    {
      this.mFlags |= paramInt;
    }
    
    void clearOldPosition()
    {
      this.mOldPosition = -1;
      this.mPreLayoutPosition = -1;
    }
    
    void clearPayload()
    {
      if (this.mPayloads != null) {
        this.mPayloads.clear();
      }
      this.mFlags &= 0xFBFF;
    }
    
    void clearReturnedFromScrapFlag()
    {
      this.mFlags &= 0xFFFFFFDF;
    }
    
    void clearTmpDetachFlag()
    {
      this.mFlags &= 0xFEFF;
    }
    
    void flagRemovedAndOffsetPosition(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      addFlags(8);
      offsetPosition(paramInt2, paramBoolean);
      this.mPosition = paramInt1;
    }
    
    public final int getAdapterPosition()
    {
      if (this.mOwnerRecyclerView == null) {}
      for (int i = -1;; i = this.mOwnerRecyclerView.getAdapterPositionFor(this)) {
        return i;
      }
    }
    
    public final long getItemId()
    {
      return this.mItemId;
    }
    
    public final int getItemViewType()
    {
      return this.mItemViewType;
    }
    
    public final int getLayoutPosition()
    {
      if (this.mPreLayoutPosition == -1) {}
      for (int i = this.mPosition;; i = this.mPreLayoutPosition) {
        return i;
      }
    }
    
    public final int getOldPosition()
    {
      return this.mOldPosition;
    }
    
    @Deprecated
    public final int getPosition()
    {
      if (this.mPreLayoutPosition == -1) {}
      for (int i = this.mPosition;; i = this.mPreLayoutPosition) {
        return i;
      }
    }
    
    List<Object> getUnmodifiedPayloads()
    {
      List localList;
      if ((this.mFlags & 0x400) == 0) {
        if ((this.mPayloads == null) || (this.mPayloads.size() == 0)) {
          localList = FULLUPDATE_PAYLOADS;
        }
      }
      for (;;)
      {
        return localList;
        localList = this.mUnmodifiedPayloads;
        continue;
        localList = FULLUPDATE_PAYLOADS;
      }
    }
    
    boolean hasAnyOfTheFlags(int paramInt)
    {
      if ((this.mFlags & paramInt) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean isAdapterPositionUnknown()
    {
      if (((this.mFlags & 0x200) != 0) || (isInvalid())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean isBound()
    {
      if ((this.mFlags & 0x1) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean isInvalid()
    {
      if ((this.mFlags & 0x4) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public final boolean isRecyclable()
    {
      if (((this.mFlags & 0x10) == 0) && (!ViewCompat.hasTransientState(this.itemView))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean isRemoved()
    {
      if ((this.mFlags & 0x8) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean isScrap()
    {
      if (this.mScrapContainer != null) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean isTmpDetached()
    {
      if ((this.mFlags & 0x100) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean isUpdated()
    {
      if ((this.mFlags & 0x2) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    boolean needsUpdate()
    {
      if ((this.mFlags & 0x2) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    void offsetPosition(int paramInt, boolean paramBoolean)
    {
      if (this.mOldPosition == -1) {
        this.mOldPosition = this.mPosition;
      }
      if (this.mPreLayoutPosition == -1) {
        this.mPreLayoutPosition = this.mPosition;
      }
      if (paramBoolean) {
        this.mPreLayoutPosition += paramInt;
      }
      this.mPosition += paramInt;
      if (this.itemView.getLayoutParams() != null) {
        ((RecyclerView.LayoutParams)this.itemView.getLayoutParams()).mInsetsDirty = true;
      }
    }
    
    void resetInternal()
    {
      this.mFlags = 0;
      this.mPosition = -1;
      this.mOldPosition = -1;
      this.mItemId = -1L;
      this.mPreLayoutPosition = -1;
      this.mIsRecyclableCount = 0;
      this.mShadowedHolder = null;
      this.mShadowingHolder = null;
      clearPayload();
      this.mWasImportantForAccessibilityBeforeHidden = 0;
      this.mPendingAccessibilityState = -1;
      RecyclerView.clearNestedRecyclerViewIfNotNested(this);
    }
    
    void saveOldPosition()
    {
      if (this.mOldPosition == -1) {
        this.mOldPosition = this.mPosition;
      }
    }
    
    void setFlags(int paramInt1, int paramInt2)
    {
      this.mFlags = (this.mFlags & (paramInt2 ^ 0xFFFFFFFF) | paramInt1 & paramInt2);
    }
    
    public final void setIsRecyclable(boolean paramBoolean)
    {
      int i;
      if (paramBoolean)
      {
        i = this.mIsRecyclableCount - 1;
        this.mIsRecyclableCount = i;
        if (this.mIsRecyclableCount >= 0) {
          break label66;
        }
        this.mIsRecyclableCount = 0;
        Log.e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
      }
      for (;;)
      {
        return;
        i = this.mIsRecyclableCount + 1;
        break;
        label66:
        if ((!paramBoolean) && (this.mIsRecyclableCount == 1)) {
          this.mFlags |= 0x10;
        } else if ((paramBoolean) && (this.mIsRecyclableCount == 0)) {
          this.mFlags &= 0xFFFFFFEF;
        }
      }
    }
    
    void setScrapContainer(RecyclerView.Recycler paramRecycler, boolean paramBoolean)
    {
      this.mScrapContainer = paramRecycler;
      this.mInChangeScrap = paramBoolean;
    }
    
    boolean shouldIgnore()
    {
      if ((this.mFlags & 0x80) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    void stopIgnoring()
    {
      this.mFlags &= 0xFF7F;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder1 = new StringBuilder("ViewHolder{" + Integer.toHexString(hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
      StringBuilder localStringBuilder2;
      if (isScrap())
      {
        localStringBuilder2 = localStringBuilder1.append(" scrap ");
        if (!this.mInChangeScrap) {
          break label295;
        }
      }
      label295:
      for (String str = "[changeScrap]";; str = "[attachedScrap]")
      {
        localStringBuilder2.append(str);
        if (isInvalid()) {
          localStringBuilder1.append(" invalid");
        }
        if (!isBound()) {
          localStringBuilder1.append(" unbound");
        }
        if (needsUpdate()) {
          localStringBuilder1.append(" update");
        }
        if (isRemoved()) {
          localStringBuilder1.append(" removed");
        }
        if (shouldIgnore()) {
          localStringBuilder1.append(" ignored");
        }
        if (isTmpDetached()) {
          localStringBuilder1.append(" tmpDetached");
        }
        if (!isRecyclable()) {
          localStringBuilder1.append(" not recyclable(" + this.mIsRecyclableCount + ")");
        }
        if (isAdapterPositionUnknown()) {
          localStringBuilder1.append(" undefined adapter position");
        }
        if (this.itemView.getParent() == null) {
          localStringBuilder1.append(" no parent");
        }
        localStringBuilder1.append("}");
        return localStringBuilder1.toString();
      }
    }
    
    void unScrap()
    {
      this.mScrapContainer.unscrapView(this);
    }
    
    boolean wasReturnedFromScrap()
    {
      if ((this.mFlags & 0x20) != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/RecyclerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */