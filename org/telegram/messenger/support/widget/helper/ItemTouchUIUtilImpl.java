package org.telegram.messenger.support.widget.helper;

import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.view.View;
import org.telegram.messenger.support.widget.RecyclerView;

class ItemTouchUIUtilImpl
{
  static class Api21Impl
    extends ItemTouchUIUtilImpl.BaseImpl
  {
    private float findMaxElevation(RecyclerView paramRecyclerView, View paramView)
    {
      int i = paramRecyclerView.getChildCount();
      float f1 = 0.0F;
      int j = 0;
      if (j < i)
      {
        View localView = paramRecyclerView.getChildAt(j);
        float f2;
        if (localView == paramView) {
          f2 = f1;
        }
        for (;;)
        {
          j++;
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
  
  static class BaseImpl
    implements ItemTouchUIUtil
  {
    public void clearView(View paramView)
    {
      paramView.setTranslationX(0.0F);
      paramView.setTranslationY(0.0F);
    }
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      paramView.setTranslationX(paramFloat1);
      paramView.setTranslationY(paramFloat2);
    }
    
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean) {}
    
    public void onSelected(View paramView) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/helper/ItemTouchUIUtilImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */