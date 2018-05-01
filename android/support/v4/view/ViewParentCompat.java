package android.support.v4.view;

import android.os.Build.VERSION;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

public final class ViewParentCompat
{
  static final ViewParentCompatBaseImpl IMPL;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 21) {
      IMPL = new ViewParentCompatApi21Impl();
    }
    for (;;)
    {
      return;
      if (Build.VERSION.SDK_INT >= 19) {
        IMPL = new ViewParentCompatApi19Impl();
      } else {
        IMPL = new ViewParentCompatBaseImpl();
      }
    }
  }
  
  public static boolean onNestedFling(ViewParent paramViewParent, View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return IMPL.onNestedFling(paramViewParent, paramView, paramFloat1, paramFloat2, paramBoolean);
  }
  
  public static boolean onNestedPreFling(ViewParent paramViewParent, View paramView, float paramFloat1, float paramFloat2)
  {
    return IMPL.onNestedPreFling(paramViewParent, paramView, paramFloat1, paramFloat2);
  }
  
  public static void onNestedPreScroll(ViewParent paramViewParent, View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3)
  {
    if ((paramViewParent instanceof NestedScrollingParent2)) {
      ((NestedScrollingParent2)paramViewParent).onNestedPreScroll(paramView, paramInt1, paramInt2, paramArrayOfInt, paramInt3);
    }
    for (;;)
    {
      return;
      if (paramInt3 == 0) {
        IMPL.onNestedPreScroll(paramViewParent, paramView, paramInt1, paramInt2, paramArrayOfInt);
      }
    }
  }
  
  public static void onNestedScroll(ViewParent paramViewParent, View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if ((paramViewParent instanceof NestedScrollingParent2)) {
      ((NestedScrollingParent2)paramViewParent).onNestedScroll(paramView, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    for (;;)
    {
      return;
      if (paramInt5 == 0) {
        IMPL.onNestedScroll(paramViewParent, paramView, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
  }
  
  public static void onNestedScrollAccepted(ViewParent paramViewParent, View paramView1, View paramView2, int paramInt1, int paramInt2)
  {
    if ((paramViewParent instanceof NestedScrollingParent2)) {
      ((NestedScrollingParent2)paramViewParent).onNestedScrollAccepted(paramView1, paramView2, paramInt1, paramInt2);
    }
    for (;;)
    {
      return;
      if (paramInt2 == 0) {
        IMPL.onNestedScrollAccepted(paramViewParent, paramView1, paramView2, paramInt1);
      }
    }
  }
  
  public static boolean onStartNestedScroll(ViewParent paramViewParent, View paramView1, View paramView2, int paramInt1, int paramInt2)
  {
    boolean bool;
    if ((paramViewParent instanceof NestedScrollingParent2)) {
      bool = ((NestedScrollingParent2)paramViewParent).onStartNestedScroll(paramView1, paramView2, paramInt1, paramInt2);
    }
    for (;;)
    {
      return bool;
      if (paramInt2 == 0) {
        bool = IMPL.onStartNestedScroll(paramViewParent, paramView1, paramView2, paramInt1);
      } else {
        bool = false;
      }
    }
  }
  
  public static void onStopNestedScroll(ViewParent paramViewParent, View paramView, int paramInt)
  {
    if ((paramViewParent instanceof NestedScrollingParent2)) {
      ((NestedScrollingParent2)paramViewParent).onStopNestedScroll(paramView, paramInt);
    }
    for (;;)
    {
      return;
      if (paramInt == 0) {
        IMPL.onStopNestedScroll(paramViewParent, paramView);
      }
    }
  }
  
  static class ViewParentCompatApi19Impl
    extends ViewParentCompat.ViewParentCompatBaseImpl
  {}
  
  static class ViewParentCompatApi21Impl
    extends ViewParentCompat.ViewParentCompatApi19Impl
  {
    public boolean onNestedFling(ViewParent paramViewParent, View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
    {
      try
      {
        paramBoolean = paramViewParent.onNestedFling(paramView, paramFloat1, paramFloat2, paramBoolean);
        return paramBoolean;
      }
      catch (AbstractMethodError paramView)
      {
        for (;;)
        {
          Log.e("ViewParentCompat", "ViewParent " + paramViewParent + " does not implement interface " + "method onNestedFling", paramView);
          paramBoolean = false;
        }
      }
    }
    
    public boolean onNestedPreFling(ViewParent paramViewParent, View paramView, float paramFloat1, float paramFloat2)
    {
      try
      {
        bool = paramViewParent.onNestedPreFling(paramView, paramFloat1, paramFloat2);
        return bool;
      }
      catch (AbstractMethodError paramView)
      {
        for (;;)
        {
          Log.e("ViewParentCompat", "ViewParent " + paramViewParent + " does not implement interface " + "method onNestedPreFling", paramView);
          boolean bool = false;
        }
      }
    }
    
    public void onNestedPreScroll(ViewParent paramViewParent, View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      try
      {
        paramViewParent.onNestedPreScroll(paramView, paramInt1, paramInt2, paramArrayOfInt);
        return;
      }
      catch (AbstractMethodError paramView)
      {
        for (;;)
        {
          Log.e("ViewParentCompat", "ViewParent " + paramViewParent + " does not implement interface " + "method onNestedPreScroll", paramView);
        }
      }
    }
    
    public void onNestedScroll(ViewParent paramViewParent, View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      try
      {
        paramViewParent.onNestedScroll(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
        return;
      }
      catch (AbstractMethodError paramView)
      {
        for (;;)
        {
          Log.e("ViewParentCompat", "ViewParent " + paramViewParent + " does not implement interface " + "method onNestedScroll", paramView);
        }
      }
    }
    
    public void onNestedScrollAccepted(ViewParent paramViewParent, View paramView1, View paramView2, int paramInt)
    {
      try
      {
        paramViewParent.onNestedScrollAccepted(paramView1, paramView2, paramInt);
        return;
      }
      catch (AbstractMethodError paramView1)
      {
        for (;;)
        {
          Log.e("ViewParentCompat", "ViewParent " + paramViewParent + " does not implement interface " + "method onNestedScrollAccepted", paramView1);
        }
      }
    }
    
    public boolean onStartNestedScroll(ViewParent paramViewParent, View paramView1, View paramView2, int paramInt)
    {
      try
      {
        bool = paramViewParent.onStartNestedScroll(paramView1, paramView2, paramInt);
        return bool;
      }
      catch (AbstractMethodError paramView1)
      {
        for (;;)
        {
          Log.e("ViewParentCompat", "ViewParent " + paramViewParent + " does not implement interface " + "method onStartNestedScroll", paramView1);
          boolean bool = false;
        }
      }
    }
    
    public void onStopNestedScroll(ViewParent paramViewParent, View paramView)
    {
      try
      {
        paramViewParent.onStopNestedScroll(paramView);
        return;
      }
      catch (AbstractMethodError paramView)
      {
        for (;;)
        {
          Log.e("ViewParentCompat", "ViewParent " + paramViewParent + " does not implement interface " + "method onStopNestedScroll", paramView);
        }
      }
    }
  }
  
  static class ViewParentCompatBaseImpl
  {
    public boolean onNestedFling(ViewParent paramViewParent, View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
    {
      if ((paramViewParent instanceof NestedScrollingParent)) {}
      for (paramBoolean = ((NestedScrollingParent)paramViewParent).onNestedFling(paramView, paramFloat1, paramFloat2, paramBoolean);; paramBoolean = false) {
        return paramBoolean;
      }
    }
    
    public boolean onNestedPreFling(ViewParent paramViewParent, View paramView, float paramFloat1, float paramFloat2)
    {
      if ((paramViewParent instanceof NestedScrollingParent)) {}
      for (boolean bool = ((NestedScrollingParent)paramViewParent).onNestedPreFling(paramView, paramFloat1, paramFloat2);; bool = false) {
        return bool;
      }
    }
    
    public void onNestedPreScroll(ViewParent paramViewParent, View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      if ((paramViewParent instanceof NestedScrollingParent)) {
        ((NestedScrollingParent)paramViewParent).onNestedPreScroll(paramView, paramInt1, paramInt2, paramArrayOfInt);
      }
    }
    
    public void onNestedScroll(ViewParent paramViewParent, View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((paramViewParent instanceof NestedScrollingParent)) {
        ((NestedScrollingParent)paramViewParent).onNestedScroll(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void onNestedScrollAccepted(ViewParent paramViewParent, View paramView1, View paramView2, int paramInt)
    {
      if ((paramViewParent instanceof NestedScrollingParent)) {
        ((NestedScrollingParent)paramViewParent).onNestedScrollAccepted(paramView1, paramView2, paramInt);
      }
    }
    
    public boolean onStartNestedScroll(ViewParent paramViewParent, View paramView1, View paramView2, int paramInt)
    {
      if ((paramViewParent instanceof NestedScrollingParent)) {}
      for (boolean bool = ((NestedScrollingParent)paramViewParent).onStartNestedScroll(paramView1, paramView2, paramInt);; bool = false) {
        return bool;
      }
    }
    
    public void onStopNestedScroll(ViewParent paramViewParent, View paramView)
    {
      if ((paramViewParent instanceof NestedScrollingParent)) {
        ((NestedScrollingParent)paramViewParent).onStopNestedScroll(paramView);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/ViewParentCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */