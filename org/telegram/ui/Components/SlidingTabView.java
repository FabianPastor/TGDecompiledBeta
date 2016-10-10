package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SlidingTabView
  extends LinearLayout
{
  private float animateTabXTo = 0.0F;
  private SlidingTabViewDelegate delegate;
  private DecelerateInterpolator interpolator;
  private Paint paint = new Paint();
  private int selectedTab = 0;
  private long startAnimationTime = 0L;
  private float startAnimationX = 0.0F;
  private int tabCount = 0;
  private float tabWidth = 0.0F;
  private float tabX = 0.0F;
  private long totalAnimationDiff = 0L;
  
  public SlidingTabView(Context paramContext)
  {
    super(paramContext);
    setOrientation(0);
    setWeightSum(100.0F);
    this.paint.setColor(-1);
    setWillNotDraw(false);
    this.interpolator = new DecelerateInterpolator();
  }
  
  private void animateToTab(int paramInt)
  {
    this.animateTabXTo = (paramInt * this.tabWidth);
    this.startAnimationX = this.tabX;
    this.totalAnimationDiff = 0L;
    this.startAnimationTime = System.currentTimeMillis();
    invalidate();
  }
  
  private void didSelectTab(int paramInt)
  {
    if (this.selectedTab == paramInt) {}
    do
    {
      return;
      this.selectedTab = paramInt;
      animateToTab(paramInt);
    } while (this.delegate == null);
    this.delegate.didSelectTab(paramInt);
  }
  
  public void addTextTab(final int paramInt, String paramString)
  {
    TextView localTextView = new TextView(getContext());
    localTextView.setText(paramString);
    localTextView.setFocusable(true);
    localTextView.setGravity(17);
    localTextView.setSingleLine();
    localTextView.setTextColor(-1);
    localTextView.setTextSize(1, 14.0F);
    localTextView.setTypeface(Typeface.DEFAULT_BOLD);
    localTextView.setBackgroundDrawable(Theme.createBarSelectorDrawable(-12763843, false));
    localTextView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        SlidingTabView.this.didSelectTab(paramInt);
      }
    });
    addView(localTextView);
    paramString = (LinearLayout.LayoutParams)localTextView.getLayoutParams();
    paramString.height = -1;
    paramString.width = 0;
    paramString.weight = 50.0F;
    localTextView.setLayoutParams(paramString);
    this.tabCount += 1;
  }
  
  public int getSeletedTab()
  {
    return this.selectedTab;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.tabX != this.animateTabXTo)
    {
      long l1 = System.currentTimeMillis();
      long l2 = this.startAnimationTime;
      this.startAnimationTime = System.currentTimeMillis();
      this.totalAnimationDiff += l1 - l2;
      if (this.totalAnimationDiff <= 200L) {
        break label115;
      }
      this.totalAnimationDiff = 200L;
      this.tabX = this.animateTabXTo;
    }
    for (;;)
    {
      float f1 = this.tabX;
      float f2 = getHeight() - AndroidUtilities.dp(2.0F);
      float f3 = this.tabX;
      paramCanvas.drawRect(f1, f2, this.tabWidth + f3, getHeight(), this.paint);
      return;
      label115:
      this.tabX = (this.startAnimationX + this.interpolator.getInterpolation((float)this.totalAnimationDiff / 200.0F) * (this.animateTabXTo - this.startAnimationX));
      invalidate();
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.tabWidth = ((paramInt3 - paramInt1) / this.tabCount);
    float f = this.tabWidth * this.selectedTab;
    this.tabX = f;
    this.animateTabXTo = f;
  }
  
  public void setDelegate(SlidingTabViewDelegate paramSlidingTabViewDelegate)
  {
    this.delegate = paramSlidingTabViewDelegate;
  }
  
  public static abstract interface SlidingTabViewDelegate
  {
    public abstract void didSelectTab(int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SlidingTabView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */