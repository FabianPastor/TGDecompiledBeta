package org.telegram.messenger.support.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

public class DividerItemDecoration
  extends RecyclerView.ItemDecoration
{
  private static final int[] ATTRS = { 16843284 };
  public static final int HORIZONTAL = 0;
  private static final String TAG = "DividerItem";
  public static final int VERTICAL = 1;
  private final Rect mBounds = new Rect();
  private Drawable mDivider;
  private int mOrientation;
  
  public DividerItemDecoration(Context paramContext, int paramInt)
  {
    paramContext = paramContext.obtainStyledAttributes(ATTRS);
    this.mDivider = paramContext.getDrawable(0);
    if (this.mDivider == null) {
      Log.w("DividerItem", "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration. Please set that attribute all call setDrawable()");
    }
    paramContext.recycle();
    setOrientation(paramInt);
  }
  
  private void drawHorizontal(Canvas paramCanvas, RecyclerView paramRecyclerView)
  {
    paramCanvas.save();
    int i;
    int j;
    if (paramRecyclerView.getClipToPadding())
    {
      i = paramRecyclerView.getPaddingTop();
      j = paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom();
      paramCanvas.clipRect(paramRecyclerView.getPaddingLeft(), i, paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight(), j);
    }
    for (;;)
    {
      int k = paramRecyclerView.getChildCount();
      for (int m = 0; m < k; m++)
      {
        View localView = paramRecyclerView.getChildAt(m);
        paramRecyclerView.getLayoutManager().getDecoratedBoundsWithMargins(localView, this.mBounds);
        int n = this.mBounds.right + Math.round(localView.getTranslationX());
        int i1 = this.mDivider.getIntrinsicWidth();
        this.mDivider.setBounds(n - i1, i, n, j);
        this.mDivider.draw(paramCanvas);
      }
      i = 0;
      j = paramRecyclerView.getHeight();
    }
    paramCanvas.restore();
  }
  
  private void drawVertical(Canvas paramCanvas, RecyclerView paramRecyclerView)
  {
    paramCanvas.save();
    int i;
    int j;
    if (paramRecyclerView.getClipToPadding())
    {
      i = paramRecyclerView.getPaddingLeft();
      j = paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight();
      paramCanvas.clipRect(i, paramRecyclerView.getPaddingTop(), j, paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom());
    }
    for (;;)
    {
      int k = paramRecyclerView.getChildCount();
      for (int m = 0; m < k; m++)
      {
        View localView = paramRecyclerView.getChildAt(m);
        paramRecyclerView.getDecoratedBoundsWithMargins(localView, this.mBounds);
        int n = this.mBounds.bottom + Math.round(localView.getTranslationY());
        int i1 = this.mDivider.getIntrinsicHeight();
        this.mDivider.setBounds(i, n - i1, j, n);
        this.mDivider.draw(paramCanvas);
      }
      i = 0;
      j = paramRecyclerView.getWidth();
    }
    paramCanvas.restore();
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    if (this.mDivider == null) {
      paramRect.set(0, 0, 0, 0);
    }
    for (;;)
    {
      return;
      if (this.mOrientation == 1) {
        paramRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
      } else {
        paramRect.set(0, 0, this.mDivider.getIntrinsicWidth(), 0);
      }
    }
  }
  
  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    if ((paramRecyclerView.getLayoutManager() == null) || (this.mDivider == null)) {}
    for (;;)
    {
      return;
      if (this.mOrientation == 1) {
        drawVertical(paramCanvas, paramRecyclerView);
      } else {
        drawHorizontal(paramCanvas, paramRecyclerView);
      }
    }
  }
  
  public void setDrawable(Drawable paramDrawable)
  {
    if (paramDrawable == null) {
      throw new IllegalArgumentException("Drawable cannot be null.");
    }
    this.mDivider = paramDrawable;
  }
  
  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
    }
    this.mOrientation = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/DividerItemDecoration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */