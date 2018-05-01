package org.telegram.ui.Components;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class EmptyTextProgressView
  extends FrameLayout
{
  private boolean inLayout;
  private RadialProgressView progressBar;
  private boolean showAtCenter;
  private TextView textView;
  
  public EmptyTextProgressView(Context paramContext)
  {
    super(paramContext);
    this.progressBar = new RadialProgressView(paramContext);
    this.progressBar.setVisibility(4);
    addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0F));
    this.textView = new TextView(paramContext);
    this.textView.setTextSize(1, 20.0F);
    this.textView.setTextColor(Theme.getColor("emptyListPlaceholder"));
    this.textView.setGravity(17);
    this.textView.setVisibility(4);
    this.textView.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
    this.textView.setText(LocaleController.getString("NoResult", NUM));
    addView(this.textView, LayoutHelper.createFrame(-2, -2.0F));
    setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.inLayout = true;
    int i = paramInt4 - paramInt2;
    int j = getChildCount();
    paramInt2 = 0;
    while (paramInt2 < j)
    {
      View localView = getChildAt(paramInt2);
      if (localView.getVisibility() == 8)
      {
        paramInt2++;
      }
      else
      {
        int k = (paramInt3 - paramInt1 - localView.getMeasuredWidth()) / 2;
        if (this.showAtCenter) {}
        for (paramInt4 = (i / 2 - localView.getMeasuredHeight()) / 2;; paramInt4 = (i - localView.getMeasuredHeight()) / 2)
        {
          localView.layout(k, paramInt4, localView.getMeasuredWidth() + k, localView.getMeasuredHeight() + paramInt4);
          break;
        }
      }
    }
    this.inLayout = false;
  }
  
  public void requestLayout()
  {
    if (!this.inLayout) {
      super.requestLayout();
    }
  }
  
  public void setProgressBarColor(int paramInt)
  {
    this.progressBar.setProgressColor(paramInt);
  }
  
  public void setShowAtCenter(boolean paramBoolean)
  {
    this.showAtCenter = paramBoolean;
  }
  
  public void setText(String paramString)
  {
    this.textView.setText(paramString);
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
  
  public void setTextSize(int paramInt)
  {
    this.textView.setTextSize(1, paramInt);
  }
  
  public void showProgress()
  {
    this.textView.setVisibility(4);
    this.progressBar.setVisibility(0);
  }
  
  public void showTextView()
  {
    this.textView.setVisibility(0);
    this.progressBar.setVisibility(4);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/EmptyTextProgressView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */