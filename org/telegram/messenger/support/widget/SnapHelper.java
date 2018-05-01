package org.telegram.messenger.support.widget;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public abstract class SnapHelper
  extends RecyclerView.OnFlingListener
{
  static final float MILLISECONDS_PER_INCH = 100.0F;
  private Scroller mGravityScroller;
  RecyclerView mRecyclerView;
  private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener()
  {
    boolean mScrolled = false;
    
    public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
    {
      super.onScrollStateChanged(paramAnonymousRecyclerView, paramAnonymousInt);
      if ((paramAnonymousInt == 0) && (this.mScrolled))
      {
        this.mScrolled = false;
        SnapHelper.this.snapToTargetExistingView();
      }
    }
    
    public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if ((paramAnonymousInt1 != 0) || (paramAnonymousInt2 != 0)) {
        this.mScrolled = true;
      }
    }
  };
  
  private void destroyCallbacks()
  {
    this.mRecyclerView.removeOnScrollListener(this.mScrollListener);
    this.mRecyclerView.setOnFlingListener(null);
  }
  
  private void setupCallbacks()
    throws IllegalStateException
  {
    if (this.mRecyclerView.getOnFlingListener() != null) {
      throw new IllegalStateException("An instance of OnFlingListener already set.");
    }
    this.mRecyclerView.addOnScrollListener(this.mScrollListener);
    this.mRecyclerView.setOnFlingListener(this);
  }
  
  private boolean snapFromFling(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2)
  {
    boolean bool1 = false;
    boolean bool2;
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      RecyclerView.SmoothScroller localSmoothScroller = createScroller(paramLayoutManager);
      bool2 = bool1;
      if (localSmoothScroller != null)
      {
        paramInt1 = findTargetSnapPosition(paramLayoutManager, paramInt1, paramInt2);
        bool2 = bool1;
        if (paramInt1 != -1)
        {
          localSmoothScroller.setTargetPosition(paramInt1);
          paramLayoutManager.startSmoothScroll(localSmoothScroller);
          bool2 = true;
        }
      }
    }
  }
  
  public void attachToRecyclerView(RecyclerView paramRecyclerView)
    throws IllegalStateException
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
        setupCallbacks();
        this.mGravityScroller = new Scroller(this.mRecyclerView.getContext(), new DecelerateInterpolator());
        snapToTargetExistingView();
      }
    }
  }
  
  public abstract int[] calculateDistanceToFinalSnap(RecyclerView.LayoutManager paramLayoutManager, View paramView);
  
  public int[] calculateScrollDistance(int paramInt1, int paramInt2)
  {
    this.mGravityScroller.fling(0, 0, paramInt1, paramInt2, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
    return new int[] { this.mGravityScroller.getFinalX(), this.mGravityScroller.getFinalY() };
  }
  
  protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager paramLayoutManager)
  {
    return createSnapScroller(paramLayoutManager);
  }
  
  @Deprecated
  protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {}
    for (paramLayoutManager = null;; paramLayoutManager = new LinearSmoothScroller(this.mRecyclerView.getContext())
        {
          protected float calculateSpeedPerPixel(DisplayMetrics paramAnonymousDisplayMetrics)
          {
            return 100.0F / paramAnonymousDisplayMetrics.densityDpi;
          }
          
          protected void onTargetFound(View paramAnonymousView, RecyclerView.State paramAnonymousState, RecyclerView.SmoothScroller.Action paramAnonymousAction)
          {
            if (SnapHelper.this.mRecyclerView == null) {}
            for (;;)
            {
              return;
              paramAnonymousView = SnapHelper.this.calculateDistanceToFinalSnap(SnapHelper.this.mRecyclerView.getLayoutManager(), paramAnonymousView);
              int i = paramAnonymousView[0];
              int j = paramAnonymousView[1];
              int k = calculateTimeForDeceleration(Math.max(Math.abs(i), Math.abs(j)));
              if (k > 0) {
                paramAnonymousAction.update(i, j, k, this.mDecelerateInterpolator);
              }
            }
          }
        }) {
      return paramLayoutManager;
    }
  }
  
  public abstract View findSnapView(RecyclerView.LayoutManager paramLayoutManager);
  
  public abstract int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2);
  
  public boolean onFling(int paramInt1, int paramInt2)
  {
    boolean bool1 = false;
    RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
    boolean bool2;
    if (localLayoutManager == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (this.mRecyclerView.getAdapter() != null)
      {
        int i = this.mRecyclerView.getMinFlingVelocity();
        if (Math.abs(paramInt2) <= i)
        {
          bool2 = bool1;
          if (Math.abs(paramInt1) <= i) {}
        }
        else
        {
          bool2 = bool1;
          if (snapFromFling(localLayoutManager, paramInt1, paramInt2)) {
            bool2 = true;
          }
        }
      }
    }
  }
  
  void snapToTargetExistingView()
  {
    if (this.mRecyclerView == null) {}
    for (;;)
    {
      return;
      Object localObject = this.mRecyclerView.getLayoutManager();
      if (localObject != null)
      {
        View localView = findSnapView((RecyclerView.LayoutManager)localObject);
        if (localView != null)
        {
          localObject = calculateDistanceToFinalSnap((RecyclerView.LayoutManager)localObject, localView);
          if ((localObject[0] != 0) || (localObject[1] != 0)) {
            this.mRecyclerView.smoothScrollBy(localObject[0], localObject[1]);
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/SnapHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */