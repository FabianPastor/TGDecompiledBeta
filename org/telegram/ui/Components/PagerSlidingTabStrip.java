package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build.VERSION;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import org.telegram.messenger.AndroidUtilities;

public class PagerSlidingTabStrip
  extends HorizontalScrollView
{
  private int currentPosition = 0;
  private float currentPositionOffset = 0.0F;
  private LinearLayout.LayoutParams defaultTabLayoutParams;
  public ViewPager.OnPageChangeListener delegatePageListener;
  private int dividerPadding = AndroidUtilities.dp(12.0F);
  private int indicatorColor = -10066330;
  private int indicatorHeight = AndroidUtilities.dp(8.0F);
  private int lastScrollX = 0;
  private final PageListener pageListener = new PageListener(null);
  private ViewPager pager;
  private Paint rectPaint;
  private int scrollOffset = AndroidUtilities.dp(52.0F);
  private boolean shouldExpand = false;
  private int tabCount;
  private int tabPadding = AndroidUtilities.dp(24.0F);
  private LinearLayout tabsContainer;
  private int underlineColor = 436207616;
  private int underlineHeight = AndroidUtilities.dp(2.0F);
  
  public PagerSlidingTabStrip(Context paramContext)
  {
    super(paramContext);
    setFillViewport(true);
    setWillNotDraw(false);
    this.tabsContainer = new LinearLayout(paramContext);
    this.tabsContainer.setOrientation(0);
    this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
    addView(this.tabsContainer);
    this.rectPaint = new Paint();
    this.rectPaint.setAntiAlias(true);
    this.rectPaint.setStyle(Paint.Style.FILL);
    this.defaultTabLayoutParams = new LinearLayout.LayoutParams(-2, -1);
  }
  
  private void addIconTab(final int paramInt1, int paramInt2)
  {
    boolean bool = true;
    ImageView local2 = new ImageView(getContext())
    {
      protected void onDraw(Canvas paramAnonymousCanvas)
      {
        super.onDraw(paramAnonymousCanvas);
        if ((PagerSlidingTabStrip.this.pager.getAdapter() instanceof PagerSlidingTabStrip.IconTabProvider)) {
          ((PagerSlidingTabStrip.IconTabProvider)PagerSlidingTabStrip.this.pager.getAdapter()).customOnDraw(paramAnonymousCanvas, paramInt1);
        }
      }
    };
    local2.setFocusable(true);
    local2.setImageResource(paramInt2);
    local2.setScaleType(ImageView.ScaleType.CENTER);
    local2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PagerSlidingTabStrip.this.pager.setCurrentItem(paramInt1);
      }
    });
    this.tabsContainer.addView(local2);
    if (paramInt1 == this.currentPosition) {}
    for (;;)
    {
      local2.setSelected(bool);
      return;
      bool = false;
    }
  }
  
  private void scrollToChild(int paramInt1, int paramInt2)
  {
    if (this.tabCount == 0) {}
    do
    {
      return;
      int i = this.tabsContainer.getChildAt(paramInt1).getLeft() + paramInt2;
      if (paramInt1 <= 0)
      {
        paramInt1 = i;
        if (paramInt2 <= 0) {}
      }
      else
      {
        paramInt1 = i - this.scrollOffset;
      }
    } while (paramInt1 == this.lastScrollX);
    this.lastScrollX = paramInt1;
    scrollTo(paramInt1, 0);
  }
  
  private void updateTabStyles()
  {
    int i = 0;
    if (i < this.tabCount)
    {
      View localView = this.tabsContainer.getChildAt(i);
      localView.setLayoutParams(this.defaultTabLayoutParams);
      if (this.shouldExpand)
      {
        localView.setPadding(0, 0, 0, 0);
        localView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1, 1.0F));
      }
      for (;;)
      {
        i += 1;
        break;
        localView.setPadding(this.tabPadding, 0, this.tabPadding, 0);
      }
    }
  }
  
  public int getDividerPadding()
  {
    return this.dividerPadding;
  }
  
  public int getIndicatorColor()
  {
    return this.indicatorColor;
  }
  
  public int getIndicatorHeight()
  {
    return this.indicatorHeight;
  }
  
  public int getScrollOffset()
  {
    return this.scrollOffset;
  }
  
  public boolean getShouldExpand()
  {
    return this.shouldExpand;
  }
  
  public int getTabPaddingLeftRight()
  {
    return this.tabPadding;
  }
  
  public int getUnderlineColor()
  {
    return this.underlineColor;
  }
  
  public int getUnderlineHeight()
  {
    return this.underlineHeight;
  }
  
  public void notifyDataSetChanged()
  {
    this.tabsContainer.removeAllViews();
    this.tabCount = this.pager.getAdapter().getCount();
    int i = 0;
    while (i < this.tabCount)
    {
      if ((this.pager.getAdapter() instanceof IconTabProvider)) {
        addIconTab(i, ((IconTabProvider)this.pager.getAdapter()).getPageIconResId(i));
      }
      i += 1;
    }
    updateTabStyles();
    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
    {
      public void onGlobalLayout()
      {
        if (Build.VERSION.SDK_INT < 16) {
          PagerSlidingTabStrip.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        for (;;)
        {
          PagerSlidingTabStrip.access$102(PagerSlidingTabStrip.this, PagerSlidingTabStrip.this.pager.getCurrentItem());
          PagerSlidingTabStrip.this.scrollToChild(PagerSlidingTabStrip.this.currentPosition, 0);
          return;
          PagerSlidingTabStrip.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
      }
    });
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((isInEditMode()) || (this.tabCount == 0)) {
      return;
    }
    int i = getHeight();
    this.rectPaint.setColor(this.underlineColor);
    paramCanvas.drawRect(0.0F, i - this.underlineHeight, this.tabsContainer.getWidth(), i, this.rectPaint);
    View localView = this.tabsContainer.getChildAt(this.currentPosition);
    float f1 = localView.getLeft();
    float f2 = localView.getRight();
    if ((this.currentPositionOffset > 0.0F) && (this.currentPosition < this.tabCount - 1))
    {
      localView = this.tabsContainer.getChildAt(this.currentPosition + 1);
      float f4 = localView.getLeft();
      float f3 = localView.getRight();
      f1 = this.currentPositionOffset * f4 + (1.0F - this.currentPositionOffset) * f1;
      f2 = this.currentPositionOffset * f3 + (1.0F - this.currentPositionOffset) * f2;
    }
    for (;;)
    {
      this.rectPaint.setColor(this.indicatorColor);
      paramCanvas.drawRect(f1, i - this.indicatorHeight, f2, i, this.rectPaint);
      return;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if ((!this.shouldExpand) || (View.MeasureSpec.getMode(paramInt1) == 0)) {
      return;
    }
    paramInt1 = getMeasuredWidth();
    this.tabsContainer.measure(0x40000000 | paramInt1, paramInt2);
  }
  
  public void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!this.shouldExpand) {
      post(new Runnable()
      {
        public void run()
        {
          PagerSlidingTabStrip.this.notifyDataSetChanged();
        }
      });
    }
  }
  
  public void setDividerPadding(int paramInt)
  {
    this.dividerPadding = paramInt;
    invalidate();
  }
  
  public void setIndicatorColor(int paramInt)
  {
    this.indicatorColor = paramInt;
    invalidate();
  }
  
  public void setIndicatorColorResource(int paramInt)
  {
    this.indicatorColor = getResources().getColor(paramInt);
    invalidate();
  }
  
  public void setIndicatorHeight(int paramInt)
  {
    this.indicatorHeight = paramInt;
    invalidate();
  }
  
  public void setOnPageChangeListener(ViewPager.OnPageChangeListener paramOnPageChangeListener)
  {
    this.delegatePageListener = paramOnPageChangeListener;
  }
  
  public void setScrollOffset(int paramInt)
  {
    this.scrollOffset = paramInt;
    invalidate();
  }
  
  public void setShouldExpand(boolean paramBoolean)
  {
    this.shouldExpand = paramBoolean;
    this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
    updateTabStyles();
    requestLayout();
  }
  
  public void setTabPaddingLeftRight(int paramInt)
  {
    this.tabPadding = paramInt;
    updateTabStyles();
  }
  
  public void setUnderlineColor(int paramInt)
  {
    this.underlineColor = paramInt;
    invalidate();
  }
  
  public void setUnderlineColorResource(int paramInt)
  {
    this.underlineColor = getResources().getColor(paramInt);
    invalidate();
  }
  
  public void setUnderlineHeight(int paramInt)
  {
    this.underlineHeight = paramInt;
    invalidate();
  }
  
  public void setViewPager(ViewPager paramViewPager)
  {
    this.pager = paramViewPager;
    if (paramViewPager.getAdapter() == null) {
      throw new IllegalStateException("ViewPager does not have adapter instance.");
    }
    paramViewPager.setOnPageChangeListener(this.pageListener);
    notifyDataSetChanged();
  }
  
  public static abstract interface IconTabProvider
  {
    public abstract void customOnDraw(Canvas paramCanvas, int paramInt);
    
    public abstract int getPageIconResId(int paramInt);
  }
  
  private class PageListener
    implements ViewPager.OnPageChangeListener
  {
    private PageListener() {}
    
    public void onPageScrollStateChanged(int paramInt)
    {
      if (paramInt == 0) {
        PagerSlidingTabStrip.this.scrollToChild(PagerSlidingTabStrip.this.pager.getCurrentItem(), 0);
      }
      if (PagerSlidingTabStrip.this.delegatePageListener != null) {
        PagerSlidingTabStrip.this.delegatePageListener.onPageScrollStateChanged(paramInt);
      }
    }
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
    {
      PagerSlidingTabStrip.access$102(PagerSlidingTabStrip.this, paramInt1);
      PagerSlidingTabStrip.access$402(PagerSlidingTabStrip.this, paramFloat);
      PagerSlidingTabStrip.this.scrollToChild(paramInt1, (int)(PagerSlidingTabStrip.this.tabsContainer.getChildAt(paramInt1).getWidth() * paramFloat));
      PagerSlidingTabStrip.this.invalidate();
      if (PagerSlidingTabStrip.this.delegatePageListener != null) {
        PagerSlidingTabStrip.this.delegatePageListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
      }
    }
    
    public void onPageSelected(int paramInt)
    {
      if (PagerSlidingTabStrip.this.delegatePageListener != null) {
        PagerSlidingTabStrip.this.delegatePageListener.onPageSelected(paramInt);
      }
      int i = 0;
      if (i < PagerSlidingTabStrip.this.tabsContainer.getChildCount())
      {
        View localView = PagerSlidingTabStrip.this.tabsContainer.getChildAt(i);
        if (i == paramInt) {}
        for (boolean bool = true;; bool = false)
        {
          localView.setSelected(bool);
          i += 1;
          break;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PagerSlidingTabStrip.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */