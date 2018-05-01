package android.support.v4.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build.VERSION;
import android.view.Display;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import android.view.WindowManager;
import java.lang.reflect.Field;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewCompat
{
  static final ViewCompatBaseImpl IMPL;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 26) {
      IMPL = new ViewCompatApi26Impl();
    }
    for (;;)
    {
      return;
      if (Build.VERSION.SDK_INT >= 24) {
        IMPL = new ViewCompatApi24Impl();
      } else if (Build.VERSION.SDK_INT >= 23) {
        IMPL = new ViewCompatApi23Impl();
      } else if (Build.VERSION.SDK_INT >= 21) {
        IMPL = new ViewCompatApi21Impl();
      } else if (Build.VERSION.SDK_INT >= 19) {
        IMPL = new ViewCompatApi19Impl();
      } else if (Build.VERSION.SDK_INT >= 18) {
        IMPL = new ViewCompatApi18Impl();
      } else if (Build.VERSION.SDK_INT >= 17) {
        IMPL = new ViewCompatApi17Impl();
      } else if (Build.VERSION.SDK_INT >= 16) {
        IMPL = new ViewCompatApi16Impl();
      } else if (Build.VERSION.SDK_INT >= 15) {
        IMPL = new ViewCompatApi15Impl();
      } else {
        IMPL = new ViewCompatBaseImpl();
      }
    }
  }
  
  public static WindowInsetsCompat dispatchApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
  {
    return IMPL.dispatchApplyWindowInsets(paramView, paramWindowInsetsCompat);
  }
  
  public static Display getDisplay(View paramView)
  {
    return IMPL.getDisplay(paramView);
  }
  
  public static float getElevation(View paramView)
  {
    return IMPL.getElevation(paramView);
  }
  
  public static int getImportantForAccessibility(View paramView)
  {
    return IMPL.getImportantForAccessibility(paramView);
  }
  
  public static int getLayoutDirection(View paramView)
  {
    return IMPL.getLayoutDirection(paramView);
  }
  
  public static int getMinimumHeight(View paramView)
  {
    return IMPL.getMinimumHeight(paramView);
  }
  
  public static int getMinimumWidth(View paramView)
  {
    return IMPL.getMinimumWidth(paramView);
  }
  
  public static int getPaddingEnd(View paramView)
  {
    return IMPL.getPaddingEnd(paramView);
  }
  
  public static int getPaddingStart(View paramView)
  {
    return IMPL.getPaddingStart(paramView);
  }
  
  public static boolean hasAccessibilityDelegate(View paramView)
  {
    return IMPL.hasAccessibilityDelegate(paramView);
  }
  
  public static boolean hasTransientState(View paramView)
  {
    return IMPL.hasTransientState(paramView);
  }
  
  public static WindowInsetsCompat onApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
  {
    return IMPL.onApplyWindowInsets(paramView, paramWindowInsetsCompat);
  }
  
  public static void postInvalidateOnAnimation(View paramView)
  {
    IMPL.postInvalidateOnAnimation(paramView);
  }
  
  public static void postOnAnimation(View paramView, Runnable paramRunnable)
  {
    IMPL.postOnAnimation(paramView, paramRunnable);
  }
  
  public static void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong)
  {
    IMPL.postOnAnimationDelayed(paramView, paramRunnable, paramLong);
  }
  
  public static void setAccessibilityDelegate(View paramView, AccessibilityDelegateCompat paramAccessibilityDelegateCompat)
  {
    IMPL.setAccessibilityDelegate(paramView, paramAccessibilityDelegateCompat);
  }
  
  public static void setElevation(View paramView, float paramFloat)
  {
    IMPL.setElevation(paramView, paramFloat);
  }
  
  public static void setImportantForAccessibility(View paramView, int paramInt)
  {
    IMPL.setImportantForAccessibility(paramView, paramInt);
  }
  
  public static void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener)
  {
    IMPL.setOnApplyWindowInsetsListener(paramView, paramOnApplyWindowInsetsListener);
  }
  
  public static void stopNestedScroll(View paramView)
  {
    IMPL.stopNestedScroll(paramView);
  }
  
  static class ViewCompatApi15Impl
    extends ViewCompat.ViewCompatBaseImpl
  {}
  
  static class ViewCompatApi16Impl
    extends ViewCompat.ViewCompatApi15Impl
  {
    public int getImportantForAccessibility(View paramView)
    {
      return paramView.getImportantForAccessibility();
    }
    
    public int getMinimumHeight(View paramView)
    {
      return paramView.getMinimumHeight();
    }
    
    public int getMinimumWidth(View paramView)
    {
      return paramView.getMinimumWidth();
    }
    
    public boolean hasTransientState(View paramView)
    {
      return paramView.hasTransientState();
    }
    
    public void postInvalidateOnAnimation(View paramView)
    {
      paramView.postInvalidateOnAnimation();
    }
    
    public void postOnAnimation(View paramView, Runnable paramRunnable)
    {
      paramView.postOnAnimation(paramRunnable);
    }
    
    public void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong)
    {
      paramView.postOnAnimationDelayed(paramRunnable, paramLong);
    }
    
    public void setImportantForAccessibility(View paramView, int paramInt)
    {
      int i = paramInt;
      if (paramInt == 4) {
        i = 2;
      }
      paramView.setImportantForAccessibility(i);
    }
  }
  
  static class ViewCompatApi17Impl
    extends ViewCompat.ViewCompatApi16Impl
  {
    public Display getDisplay(View paramView)
    {
      return paramView.getDisplay();
    }
    
    public int getLayoutDirection(View paramView)
    {
      return paramView.getLayoutDirection();
    }
    
    public int getPaddingEnd(View paramView)
    {
      return paramView.getPaddingEnd();
    }
    
    public int getPaddingStart(View paramView)
    {
      return paramView.getPaddingStart();
    }
  }
  
  static class ViewCompatApi18Impl
    extends ViewCompat.ViewCompatApi17Impl
  {}
  
  static class ViewCompatApi19Impl
    extends ViewCompat.ViewCompatApi18Impl
  {
    public boolean isAttachedToWindow(View paramView)
    {
      return paramView.isAttachedToWindow();
    }
    
    public void setImportantForAccessibility(View paramView, int paramInt)
    {
      paramView.setImportantForAccessibility(paramInt);
    }
  }
  
  static class ViewCompatApi21Impl
    extends ViewCompat.ViewCompatApi19Impl
  {
    public WindowInsetsCompat dispatchApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
    {
      paramWindowInsetsCompat = (WindowInsets)WindowInsetsCompat.unwrap(paramWindowInsetsCompat);
      WindowInsets localWindowInsets = paramView.dispatchApplyWindowInsets(paramWindowInsetsCompat);
      paramView = paramWindowInsetsCompat;
      if (localWindowInsets != paramWindowInsetsCompat) {
        paramView = new WindowInsets(localWindowInsets);
      }
      return WindowInsetsCompat.wrap(paramView);
    }
    
    public float getElevation(View paramView)
    {
      return paramView.getElevation();
    }
    
    public WindowInsetsCompat onApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
    {
      paramWindowInsetsCompat = (WindowInsets)WindowInsetsCompat.unwrap(paramWindowInsetsCompat);
      WindowInsets localWindowInsets = paramView.onApplyWindowInsets(paramWindowInsetsCompat);
      paramView = paramWindowInsetsCompat;
      if (localWindowInsets != paramWindowInsetsCompat) {
        paramView = new WindowInsets(localWindowInsets);
      }
      return WindowInsetsCompat.wrap(paramView);
    }
    
    public void setElevation(View paramView, float paramFloat)
    {
      paramView.setElevation(paramFloat);
    }
    
    public void setOnApplyWindowInsetsListener(View paramView, final OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener)
    {
      if (paramOnApplyWindowInsetsListener == null) {
        paramView.setOnApplyWindowInsetsListener(null);
      }
      for (;;)
      {
        return;
        paramView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
        {
          public WindowInsets onApplyWindowInsets(View paramAnonymousView, WindowInsets paramAnonymousWindowInsets)
          {
            paramAnonymousWindowInsets = WindowInsetsCompat.wrap(paramAnonymousWindowInsets);
            return (WindowInsets)WindowInsetsCompat.unwrap(paramOnApplyWindowInsetsListener.onApplyWindowInsets(paramAnonymousView, paramAnonymousWindowInsets));
          }
        });
      }
    }
    
    public void stopNestedScroll(View paramView)
    {
      paramView.stopNestedScroll();
    }
  }
  
  static class ViewCompatApi23Impl
    extends ViewCompat.ViewCompatApi21Impl
  {}
  
  static class ViewCompatApi24Impl
    extends ViewCompat.ViewCompatApi23Impl
  {}
  
  static class ViewCompatApi26Impl
    extends ViewCompat.ViewCompatApi24Impl
  {}
  
  static class ViewCompatBaseImpl
  {
    static boolean sAccessibilityDelegateCheckFailed = false;
    static Field sAccessibilityDelegateField;
    private static Field sMinHeightField;
    private static boolean sMinHeightFieldFetched;
    private static Field sMinWidthField;
    private static boolean sMinWidthFieldFetched;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    WeakHashMap<View, Object> mViewPropertyAnimatorCompatMap = null;
    
    public WindowInsetsCompat dispatchApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
    {
      return paramWindowInsetsCompat;
    }
    
    public Display getDisplay(View paramView)
    {
      if (isAttachedToWindow(paramView)) {}
      for (paramView = ((WindowManager)paramView.getContext().getSystemService("window")).getDefaultDisplay();; paramView = null) {
        return paramView;
      }
    }
    
    public float getElevation(View paramView)
    {
      return 0.0F;
    }
    
    long getFrameTime()
    {
      return ValueAnimator.getFrameDelay();
    }
    
    public int getImportantForAccessibility(View paramView)
    {
      return 0;
    }
    
    public int getLayoutDirection(View paramView)
    {
      return 0;
    }
    
    public int getMinimumHeight(View paramView)
    {
      if (!sMinHeightFieldFetched) {}
      try
      {
        sMinHeightField = View.class.getDeclaredField("mMinHeight");
        sMinHeightField.setAccessible(true);
        sMinHeightFieldFetched = true;
        if (sMinHeightField != null) {}
        for (;;)
        {
          try
          {
            i = ((Integer)sMinHeightField.get(paramView)).intValue();
            return i;
          }
          catch (Exception paramView) {}
          int i = 0;
        }
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        for (;;) {}
      }
    }
    
    public int getMinimumWidth(View paramView)
    {
      if (!sMinWidthFieldFetched) {}
      try
      {
        sMinWidthField = View.class.getDeclaredField("mMinWidth");
        sMinWidthField.setAccessible(true);
        sMinWidthFieldFetched = true;
        if (sMinWidthField != null) {}
        for (;;)
        {
          try
          {
            i = ((Integer)sMinWidthField.get(paramView)).intValue();
            return i;
          }
          catch (Exception paramView) {}
          int i = 0;
        }
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        for (;;) {}
      }
    }
    
    public int getPaddingEnd(View paramView)
    {
      return paramView.getPaddingRight();
    }
    
    public int getPaddingStart(View paramView)
    {
      return paramView.getPaddingLeft();
    }
    
    /* Error */
    public boolean hasAccessibilityDelegate(View paramView)
    {
      // Byte code:
      //   0: iconst_1
      //   1: istore_2
      //   2: iconst_0
      //   3: istore_3
      //   4: getstatic 32	android/support/v4/view/ViewCompat$ViewCompatBaseImpl:sAccessibilityDelegateCheckFailed	Z
      //   7: ifeq +7 -> 14
      //   10: iload_3
      //   11: istore_2
      //   12: iload_2
      //   13: ireturn
      //   14: getstatic 130	android/support/v4/view/ViewCompat$ViewCompatBaseImpl:sAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   17: ifnonnull +20 -> 37
      //   20: ldc 47
      //   22: ldc -124
      //   24: invokevirtual 92	java/lang/Class:getDeclaredField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
      //   27: putstatic 130	android/support/v4/view/ViewCompat$ViewCompatBaseImpl:sAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   30: getstatic 130	android/support/v4/view/ViewCompat$ViewCompatBaseImpl:sAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   33: iconst_1
      //   34: invokevirtual 100	java/lang/reflect/Field:setAccessible	(Z)V
      //   37: getstatic 130	android/support/v4/view/ViewCompat$ViewCompatBaseImpl:sAccessibilityDelegateField	Ljava/lang/reflect/Field;
      //   40: aload_1
      //   41: invokevirtual 104	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   44: astore_1
      //   45: aload_1
      //   46: ifnull +16 -> 62
      //   49: goto -37 -> 12
      //   52: astore_1
      //   53: iconst_1
      //   54: putstatic 32	android/support/v4/view/ViewCompat$ViewCompatBaseImpl:sAccessibilityDelegateCheckFailed	Z
      //   57: iload_3
      //   58: istore_2
      //   59: goto -47 -> 12
      //   62: iconst_0
      //   63: istore_2
      //   64: goto -15 -> 49
      //   67: astore_1
      //   68: iconst_1
      //   69: putstatic 32	android/support/v4/view/ViewCompat$ViewCompatBaseImpl:sAccessibilityDelegateCheckFailed	Z
      //   72: iload_3
      //   73: istore_2
      //   74: goto -62 -> 12
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	77	0	this	ViewCompatBaseImpl
      //   0	77	1	paramView	View
      //   1	73	2	bool1	boolean
      //   3	70	3	bool2	boolean
      // Exception table:
      //   from	to	target	type
      //   20	37	52	java/lang/Throwable
      //   37	45	67	java/lang/Throwable
    }
    
    public boolean hasTransientState(View paramView)
    {
      return false;
    }
    
    public boolean isAttachedToWindow(View paramView)
    {
      if (paramView.getWindowToken() != null) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public WindowInsetsCompat onApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat)
    {
      return paramWindowInsetsCompat;
    }
    
    public void postInvalidateOnAnimation(View paramView)
    {
      paramView.postInvalidate();
    }
    
    public void postOnAnimation(View paramView, Runnable paramRunnable)
    {
      paramView.postDelayed(paramRunnable, getFrameTime());
    }
    
    public void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong)
    {
      paramView.postDelayed(paramRunnable, getFrameTime() + paramLong);
    }
    
    public void setAccessibilityDelegate(View paramView, AccessibilityDelegateCompat paramAccessibilityDelegateCompat)
    {
      if (paramAccessibilityDelegateCompat == null) {}
      for (paramAccessibilityDelegateCompat = null;; paramAccessibilityDelegateCompat = paramAccessibilityDelegateCompat.getBridge())
      {
        paramView.setAccessibilityDelegate(paramAccessibilityDelegateCompat);
        return;
      }
    }
    
    public void setElevation(View paramView, float paramFloat) {}
    
    public void setImportantForAccessibility(View paramView, int paramInt) {}
    
    public void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener) {}
    
    public void stopNestedScroll(View paramView)
    {
      if ((paramView instanceof NestedScrollingChild)) {
        ((NestedScrollingChild)paramView).stopNestedScroll();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/ViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */