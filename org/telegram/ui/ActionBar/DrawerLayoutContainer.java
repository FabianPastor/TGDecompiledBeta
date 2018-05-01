package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Keep;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class DrawerLayoutContainer
  extends FrameLayout
{
  private static final int MIN_DRAWER_MARGIN = 64;
  private boolean allowDrawContent = true;
  private boolean allowOpenDrawer;
  private boolean beginTrackingSent;
  private AnimatorSet currentAnimation;
  private ViewGroup drawerLayout;
  private boolean drawerOpened;
  private float drawerPosition;
  private boolean inLayout;
  private Object lastInsets;
  private boolean maybeStartTracking;
  private int minDrawerMargin = (int)(64.0F * AndroidUtilities.density + 0.5F);
  private int paddingTop;
  private ActionBarLayout parentActionBarLayout;
  private Rect rect = new Rect();
  private float scrimOpacity;
  private Paint scrimPaint = new Paint();
  private Drawable shadowLeft;
  private boolean startedTracking;
  private int startedTrackingPointerId;
  private int startedTrackingX;
  private int startedTrackingY;
  private VelocityTracker velocityTracker;
  
  public DrawerLayoutContainer(Context paramContext)
  {
    super(paramContext);
    setDescendantFocusability(262144);
    setFocusableInTouchMode(true);
    if (Build.VERSION.SDK_INT >= 21)
    {
      setFitsSystemWindows(true);
      setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
      {
        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View paramAnonymousView, WindowInsets paramAnonymousWindowInsets)
        {
          paramAnonymousView = (DrawerLayoutContainer)paramAnonymousView;
          AndroidUtilities.statusBarHeight = paramAnonymousWindowInsets.getSystemWindowInsetTop();
          DrawerLayoutContainer.access$002(DrawerLayoutContainer.this, paramAnonymousWindowInsets);
          if ((paramAnonymousWindowInsets.getSystemWindowInsetTop() <= 0) && (DrawerLayoutContainer.this.getBackground() == null)) {}
          for (boolean bool = true;; bool = false)
          {
            paramAnonymousView.setWillNotDraw(bool);
            paramAnonymousView.requestLayout();
            return paramAnonymousWindowInsets.consumeSystemWindowInsets();
          }
        }
      });
      setSystemUiVisibility(1280);
    }
    this.shadowLeft = getResources().getDrawable(NUM);
  }
  
  @SuppressLint({"NewApi"})
  private void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt, boolean paramBoolean)
  {
    int i = 0;
    WindowInsets localWindowInsets = (WindowInsets)paramObject;
    if (paramInt == 3)
    {
      paramObject = localWindowInsets.replaceSystemWindowInsets(localWindowInsets.getSystemWindowInsetLeft(), localWindowInsets.getSystemWindowInsetTop(), 0, localWindowInsets.getSystemWindowInsetBottom());
      paramMarginLayoutParams.leftMargin = ((WindowInsets)paramObject).getSystemWindowInsetLeft();
      if (!paramBoolean) {
        break label107;
      }
    }
    label107:
    for (paramInt = i;; paramInt = ((WindowInsets)paramObject).getSystemWindowInsetTop())
    {
      paramMarginLayoutParams.topMargin = paramInt;
      paramMarginLayoutParams.rightMargin = ((WindowInsets)paramObject).getSystemWindowInsetRight();
      paramMarginLayoutParams.bottomMargin = ((WindowInsets)paramObject).getSystemWindowInsetBottom();
      return;
      paramObject = localWindowInsets;
      if (paramInt != 5) {
        break;
      }
      paramObject = localWindowInsets.replaceSystemWindowInsets(0, localWindowInsets.getSystemWindowInsetTop(), localWindowInsets.getSystemWindowInsetRight(), localWindowInsets.getSystemWindowInsetBottom());
      break;
    }
  }
  
  @SuppressLint({"NewApi"})
  private void dispatchChildInsets(View paramView, Object paramObject, int paramInt)
  {
    WindowInsets localWindowInsets = (WindowInsets)paramObject;
    if (paramInt == 3) {
      paramObject = localWindowInsets.replaceSystemWindowInsets(localWindowInsets.getSystemWindowInsetLeft(), localWindowInsets.getSystemWindowInsetTop(), 0, localWindowInsets.getSystemWindowInsetBottom());
    }
    for (;;)
    {
      paramView.dispatchApplyWindowInsets((WindowInsets)paramObject);
      return;
      paramObject = localWindowInsets;
      if (paramInt == 5) {
        paramObject = localWindowInsets.replaceSystemWindowInsets(0, localWindowInsets.getSystemWindowInsetTop(), localWindowInsets.getSystemWindowInsetRight(), localWindowInsets.getSystemWindowInsetBottom());
      }
    }
  }
  
  private float getScrimOpacity()
  {
    return this.scrimOpacity;
  }
  
  private int getTopInset(Object paramObject)
  {
    int i = 0;
    int j = i;
    if (Build.VERSION.SDK_INT >= 21)
    {
      j = i;
      if (paramObject != null) {
        j = ((WindowInsets)paramObject).getSystemWindowInsetTop();
      }
    }
    return j;
  }
  
  private void onDrawerAnimationEnd(boolean paramBoolean)
  {
    this.startedTracking = false;
    this.currentAnimation = null;
    this.drawerOpened = paramBoolean;
    if ((!paramBoolean) && ((this.drawerLayout instanceof ListView))) {
      ((ListView)this.drawerLayout).setSelectionFromTop(0, 0);
    }
  }
  
  private void prepareForDrawerOpen(MotionEvent paramMotionEvent)
  {
    this.maybeStartTracking = false;
    this.startedTracking = true;
    if (paramMotionEvent != null) {
      this.startedTrackingX = ((int)paramMotionEvent.getX());
    }
    this.beginTrackingSent = false;
  }
  
  private void setScrimOpacity(float paramFloat)
  {
    this.scrimOpacity = paramFloat;
    invalidate();
  }
  
  public void cancelCurrentAnimation()
  {
    if (this.currentAnimation != null)
    {
      this.currentAnimation.cancel();
      this.currentAnimation = null;
    }
  }
  
  public void closeDrawer(boolean paramBoolean)
  {
    cancelCurrentAnimation();
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "drawerPosition", new float[] { 0.0F }) });
    localAnimatorSet.setInterpolator(new DecelerateInterpolator());
    if (paramBoolean) {
      localAnimatorSet.setDuration(Math.max((int)(200.0F / this.drawerLayout.getMeasuredWidth() * this.drawerPosition), 50));
    }
    for (;;)
    {
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          DrawerLayoutContainer.this.onDrawerAnimationEnd(false);
        }
      });
      localAnimatorSet.start();
      return;
      localAnimatorSet.setDuration(300L);
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    boolean bool1;
    if (!this.allowDrawContent) {
      bool1 = false;
    }
    for (;;)
    {
      return bool1;
      int i = getHeight();
      int j;
      int m;
      int i1;
      int i2;
      int i3;
      label68:
      View localView;
      if (paramView != this.drawerLayout)
      {
        j = 1;
        k = 0;
        m = 0;
        n = 0;
        i1 = 0;
        i2 = getWidth();
        i3 = paramCanvas.save();
        if (j == 0) {
          break label241;
        }
        int i4 = getChildCount();
        n = 0;
        if (n >= i4) {
          break label206;
        }
        localView = getChildAt(n);
        k = m;
        if (localView.getVisibility() == 0)
        {
          k = m;
          if (localView != this.drawerLayout) {
            k = n;
          }
        }
        m = i1;
        if (localView != paramView)
        {
          m = i1;
          if (localView.getVisibility() == 0)
          {
            m = i1;
            if (localView == this.drawerLayout)
            {
              if (localView.getHeight() >= i) {
                break label181;
              }
              m = i1;
            }
          }
        }
      }
      for (;;)
      {
        n++;
        i1 = m;
        m = k;
        break label68;
        j = 0;
        break;
        label181:
        int i5 = localView.getRight();
        m = i1;
        if (i5 > i1) {
          m = i5;
        }
      }
      label206:
      int n = i1;
      int k = m;
      if (i1 != 0)
      {
        paramCanvas.clipRect(i1, 0, i2, getHeight());
        k = m;
        n = i1;
      }
      label241:
      boolean bool2 = super.drawChild(paramCanvas, paramView, paramLong);
      paramCanvas.restoreToCount(i3);
      if ((this.scrimOpacity > 0.0F) && (j != 0))
      {
        bool1 = bool2;
        if (indexOfChild(paramView) == k)
        {
          this.scrimPaint.setColor((int)(153.0F * this.scrimOpacity) << 24);
          paramCanvas.drawRect(n, 0.0F, i2, getHeight(), this.scrimPaint);
          bool1 = bool2;
        }
      }
      else
      {
        bool1 = bool2;
        if (this.shadowLeft != null)
        {
          float f = Math.max(0.0F, Math.min(this.drawerPosition / AndroidUtilities.dp(20.0F), 1.0F));
          bool1 = bool2;
          if (f != 0.0F)
          {
            this.shadowLeft.setBounds((int)this.drawerPosition, paramView.getTop(), (int)this.drawerPosition + this.shadowLeft.getIntrinsicWidth(), paramView.getBottom());
            this.shadowLeft.setAlpha((int)(255.0F * f));
            this.shadowLeft.draw(paramCanvas);
            bool1 = bool2;
          }
        }
      }
    }
  }
  
  public View getDrawerLayout()
  {
    return this.drawerLayout;
  }
  
  public float getDrawerPosition()
  {
    return this.drawerPosition;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public boolean isDrawerOpened()
  {
    return this.drawerOpened;
  }
  
  public void moveDrawerByX(float paramFloat)
  {
    setDrawerPosition(this.drawerPosition + paramFloat);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.parentActionBarLayout.checkTransitionAnimation()) || (onTouchEvent(paramMotionEvent))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.inLayout = true;
    paramInt2 = getChildCount();
    paramInt1 = 0;
    if (paramInt1 < paramInt2)
    {
      View localView = getChildAt(paramInt1);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        paramInt1++;
        break;
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        if (BuildVars.DEBUG_VERSION)
        {
          if (this.drawerLayout != localView) {
            localView.layout(localLayoutParams.leftMargin, localLayoutParams.topMargin + getPaddingTop(), localLayoutParams.leftMargin + localView.getMeasuredWidth(), localLayoutParams.topMargin + localView.getMeasuredHeight() + getPaddingTop());
          } else {
            localView.layout(-localView.getMeasuredWidth(), localLayoutParams.topMargin + getPaddingTop(), 0, localLayoutParams.topMargin + localView.getMeasuredHeight() + getPaddingTop());
          }
        }
        else
        {
          try
          {
            if (this.drawerLayout == localView) {
              break label225;
            }
            localView.layout(localLayoutParams.leftMargin, localLayoutParams.topMargin + getPaddingTop(), localLayoutParams.leftMargin + localView.getMeasuredWidth(), localLayoutParams.topMargin + localView.getMeasuredHeight() + getPaddingTop());
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
          continue;
          label225:
          localException.layout(-localException.getMeasuredWidth(), localLayoutParams.topMargin + getPaddingTop(), 0, localLayoutParams.topMargin + localException.getMeasuredHeight() + getPaddingTop());
        }
      }
    }
    this.inLayout = false;
  }
  
  @SuppressLint({"NewApi"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt2);
    setMeasuredDimension(i, j);
    int k = j;
    label101:
    int n;
    label110:
    View localView;
    if (Build.VERSION.SDK_INT < 21)
    {
      this.inLayout = true;
      if (j == AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)
      {
        if ((getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
          setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
        }
        k = AndroidUtilities.displaySize.y;
        this.inLayout = false;
      }
    }
    else
    {
      if ((this.lastInsets == null) || (Build.VERSION.SDK_INT < 21)) {
        break label170;
      }
      j = 1;
      int m = getChildCount();
      n = 0;
      if (n >= m) {
        return;
      }
      localView = getChildAt(n);
      if (localView.getVisibility() != 8) {
        break label176;
      }
    }
    for (;;)
    {
      n++;
      break label110;
      k = j;
      if (!(getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
        break;
      }
      setPadding(0, 0, 0, 0);
      k = j;
      break;
      label170:
      j = 0;
      break label101;
      label176:
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
      if (j != 0)
      {
        if (!localView.getFitsSystemWindows()) {
          break label270;
        }
        dispatchChildInsets(localView, this.lastInsets, localLayoutParams.gravity);
      }
      for (;;)
      {
        if (this.drawerLayout != localView)
        {
          localView.measure(View.MeasureSpec.makeMeasureSpec(i - localLayoutParams.leftMargin - localLayoutParams.rightMargin, NUM), View.MeasureSpec.makeMeasureSpec(k - localLayoutParams.topMargin - localLayoutParams.bottomMargin, NUM));
          break;
          label270:
          if (localView.getTag() == null)
          {
            Object localObject = this.lastInsets;
            int i1 = localLayoutParams.gravity;
            if (Build.VERSION.SDK_INT >= 21) {}
            for (boolean bool = true;; bool = false)
            {
              applyMarginInsets(localLayoutParams, localObject, i1, bool);
              break;
            }
          }
        }
      }
      localView.setPadding(0, 0, 0, 0);
      localView.measure(getChildMeasureSpec(paramInt1, this.minDrawerMargin + localLayoutParams.leftMargin + localLayoutParams.rightMargin, localLayoutParams.width), getChildMeasureSpec(paramInt2, localLayoutParams.topMargin + localLayoutParams.bottomMargin, localLayoutParams.height));
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    if (!this.parentActionBarLayout.checkTransitionAnimation()) {
      if ((this.drawerOpened) && (paramMotionEvent != null) && (paramMotionEvent.getX() > this.drawerPosition) && (!this.startedTracking))
      {
        bool1 = bool2;
        if (paramMotionEvent.getAction() == 1)
        {
          closeDrawer(false);
          bool1 = bool2;
        }
      }
    }
    for (;;)
    {
      return bool1;
      if ((this.allowOpenDrawer) && (this.parentActionBarLayout.fragmentsStack.size() == 1))
      {
        if ((paramMotionEvent == null) || ((paramMotionEvent.getAction() != 0) && (paramMotionEvent.getAction() != 2)) || (this.startedTracking) || (this.maybeStartTracking)) {
          break label204;
        }
        this.parentActionBarLayout.getHitRect(this.rect);
        this.startedTrackingX = ((int)paramMotionEvent.getX());
        this.startedTrackingY = ((int)paramMotionEvent.getY());
        if (this.rect.contains(this.startedTrackingX, this.startedTrackingY))
        {
          this.startedTrackingPointerId = paramMotionEvent.getPointerId(0);
          this.maybeStartTracking = true;
          cancelCurrentAnimation();
          if (this.velocityTracker != null) {
            this.velocityTracker.clear();
          }
        }
      }
      label204:
      float f1;
      float f2;
      label454:
      do
      {
        for (;;)
        {
          bool1 = this.startedTracking;
          break;
          if ((paramMotionEvent == null) || (paramMotionEvent.getAction() != 2) || (paramMotionEvent.getPointerId(0) != this.startedTrackingPointerId)) {
            break label454;
          }
          if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
          }
          f1 = (int)(paramMotionEvent.getX() - this.startedTrackingX);
          f2 = Math.abs((int)paramMotionEvent.getY() - this.startedTrackingY);
          this.velocityTracker.addMovement(paramMotionEvent);
          if ((this.maybeStartTracking) && (!this.startedTracking) && (((f1 > 0.0F) && (f1 / 3.0F > Math.abs(f2)) && (Math.abs(f1) >= AndroidUtilities.getPixelsInCM(0.2F, true))) || ((f1 < 0.0F) && (Math.abs(f1) >= Math.abs(f2)) && (Math.abs(f1) >= AndroidUtilities.getPixelsInCM(0.4F, true)))))
          {
            prepareForDrawerOpen(paramMotionEvent);
            this.startedTrackingX = ((int)paramMotionEvent.getX());
            requestDisallowInterceptTouchEvent(true);
          }
          else if (this.startedTracking)
          {
            if (!this.beginTrackingSent)
            {
              if (((Activity)getContext()).getCurrentFocus() != null) {
                AndroidUtilities.hideKeyboard(((Activity)getContext()).getCurrentFocus());
              }
              this.beginTrackingSent = true;
            }
            moveDrawerByX(f1);
            this.startedTrackingX = ((int)paramMotionEvent.getX());
          }
        }
      } while ((paramMotionEvent != null) && ((paramMotionEvent == null) || (paramMotionEvent.getPointerId(0) != this.startedTrackingPointerId) || ((paramMotionEvent.getAction() != 3) && (paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 6))));
      if (this.velocityTracker == null) {
        this.velocityTracker = VelocityTracker.obtain();
      }
      this.velocityTracker.computeCurrentVelocity(1000);
      int i;
      if ((this.startedTracking) || ((this.drawerPosition != 0.0F) && (this.drawerPosition != this.drawerLayout.getMeasuredWidth())))
      {
        f1 = this.velocityTracker.getXVelocity();
        f2 = this.velocityTracker.getYVelocity();
        if (((this.drawerPosition >= this.drawerLayout.getMeasuredWidth() / 2.0F) || ((f1 >= 3500.0F) && (Math.abs(f1) >= Math.abs(f2)))) && ((f1 >= 0.0F) || (Math.abs(f1) < 3500.0F))) {
          break label699;
        }
        i = 1;
        label636:
        if (i != 0) {
          break label710;
        }
        if ((this.drawerOpened) || (Math.abs(f1) < 3500.0F)) {
          break label705;
        }
      }
      label699:
      label705:
      for (bool1 = true;; bool1 = false)
      {
        openDrawer(bool1);
        this.startedTracking = false;
        this.maybeStartTracking = false;
        if (this.velocityTracker == null) {
          break;
        }
        this.velocityTracker.recycle();
        this.velocityTracker = null;
        break;
        i = 0;
        break label636;
      }
      label710:
      if ((this.drawerOpened) && (Math.abs(f1) >= 3500.0F)) {}
      for (;;)
      {
        closeDrawer(bool1);
        break;
        bool1 = false;
      }
      bool1 = false;
    }
  }
  
  public void openDrawer(boolean paramBoolean)
  {
    if (!this.allowOpenDrawer) {
      return;
    }
    if ((AndroidUtilities.isTablet()) && (this.parentActionBarLayout != null) && (this.parentActionBarLayout.parentActivity != null)) {
      AndroidUtilities.hideKeyboard(this.parentActionBarLayout.parentActivity.getCurrentFocus());
    }
    cancelCurrentAnimation();
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "drawerPosition", new float[] { this.drawerLayout.getMeasuredWidth() }) });
    localAnimatorSet.setInterpolator(new DecelerateInterpolator());
    if (paramBoolean) {
      localAnimatorSet.setDuration(Math.max((int)(200.0F / this.drawerLayout.getMeasuredWidth() * (this.drawerLayout.getMeasuredWidth() - this.drawerPosition)), 50));
    }
    for (;;)
    {
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          DrawerLayoutContainer.this.onDrawerAnimationEnd(true);
        }
      });
      localAnimatorSet.start();
      this.currentAnimation = localAnimatorSet;
      break;
      localAnimatorSet.setDuration(300L);
    }
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    if ((this.maybeStartTracking) && (!this.startedTracking)) {
      onTouchEvent(null);
    }
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  public void requestLayout()
  {
    if (!this.inLayout) {
      super.requestLayout();
    }
  }
  
  public void setAllowDrawContent(boolean paramBoolean)
  {
    if (this.allowDrawContent != paramBoolean)
    {
      this.allowDrawContent = paramBoolean;
      invalidate();
    }
  }
  
  public void setAllowOpenDrawer(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.allowOpenDrawer = paramBoolean1;
    if ((!this.allowOpenDrawer) && (this.drawerPosition != 0.0F))
    {
      if (paramBoolean2) {
        break label36;
      }
      setDrawerPosition(0.0F);
      onDrawerAnimationEnd(false);
    }
    for (;;)
    {
      return;
      label36:
      closeDrawer(true);
    }
  }
  
  public void setDrawerLayout(ViewGroup paramViewGroup)
  {
    this.drawerLayout = paramViewGroup;
    addView(this.drawerLayout);
    if (Build.VERSION.SDK_INT >= 21) {
      this.drawerLayout.setFitsSystemWindows(true);
    }
  }
  
  @Keep
  public void setDrawerPosition(float paramFloat)
  {
    this.drawerPosition = paramFloat;
    if (this.drawerPosition > this.drawerLayout.getMeasuredWidth())
    {
      this.drawerPosition = this.drawerLayout.getMeasuredWidth();
      this.drawerLayout.setTranslationX(this.drawerPosition);
      if (this.drawerPosition <= 0.0F) {
        break label109;
      }
    }
    label109:
    for (int i = 0;; i = 8)
    {
      if (this.drawerLayout.getVisibility() != i) {
        this.drawerLayout.setVisibility(i);
      }
      setScrimOpacity(this.drawerPosition / this.drawerLayout.getMeasuredWidth());
      return;
      if (this.drawerPosition >= 0.0F) {
        break;
      }
      this.drawerPosition = 0.0F;
      break;
    }
  }
  
  public void setParentActionBarLayout(ActionBarLayout paramActionBarLayout)
  {
    this.parentActionBarLayout = paramActionBarLayout;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/DrawerLayoutContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */