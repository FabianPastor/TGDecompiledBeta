package org.telegram.messenger.support.widget.helper;

import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.view.View;
import org.telegram.messenger.support.widget.RecyclerView;

class ItemTouchUIUtilImpl
{
  static class Gingerbread
    implements ItemTouchUIUtil
  {
    private void draw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2)
    {
      paramCanvas.save();
      paramCanvas.translate(paramFloat1, paramFloat2);
      paramRecyclerView.drawChild(paramCanvas, paramView, 0L);
      paramCanvas.restore();
    }
    
    public void clearView(View paramView)
    {
      paramView.setVisibility(0);
    }
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      if (paramInt != 2) {
        draw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2);
      }
    }
    
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      if (paramInt == 2) {
        draw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2);
      }
    }
    
    public void onSelected(View paramView)
    {
      paramView.setVisibility(4);
    }
  }
  
  static class Honeycomb
    implements ItemTouchUIUtil
  {
    public void clearView(View paramView)
    {
      ViewCompat.setTranslationX(paramView, 0.0F);
      ViewCompat.setTranslationY(paramView, 0.0F);
    }
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      ViewCompat.setTranslationX(paramView, paramFloat1);
      ViewCompat.setTranslationY(paramView, paramFloat2);
    }
    
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean) {}
    
    public void onSelected(View paramView) {}
  }
  
  static class Lollipop
    extends ItemTouchUIUtilImpl.Honeycomb
  {
    private float findMaxElevation(RecyclerView paramRecyclerView, View paramView)
    {
      int j = paramRecyclerView.getChildCount();
      float f1 = 0.0F;
      int i = 0;
      if (i < j)
      {
        View localView = paramRecyclerView.getChildAt(i);
        float f2;
        if (localView == paramView) {
          f2 = f1;
        }
        for (;;)
        {
          i += 1;
          f1 = f2;
          break;
          float f3 = ViewCompat.getElevation(localView);
          f2 = f1;
          if (f3 > f1) {
            f2 = f3;
          }
        }
      }
      return f1;
    }
    
    public void clearView(View paramView)
    {
      Object localObject = paramView.getTag();
      if ((localObject != null) && ((localObject instanceof Float))) {
        ViewCompat.setElevation(paramView, ((Float)localObject).floatValue());
      }
      paramView.setTag(null);
      super.clearView(paramView);
    }
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      if ((paramBoolean) && (paramView.getTag() == null))
      {
        float f = ViewCompat.getElevation(paramView);
        ViewCompat.setElevation(paramView, 1.0F + findMaxElevation(paramRecyclerView, paramView));
        paramView.setTag(Float.valueOf(f));
      }
      super.onDraw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/helper/ItemTouchUIUtilImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */