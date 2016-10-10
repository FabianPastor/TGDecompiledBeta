package android.support.v4.view;

import android.graphics.Rect;
import android.os.Build.VERSION;

public class WindowInsetsCompat
{
  private static final WindowInsetsCompatImpl IMPL = new WindowInsetsCompatBaseImpl();
  private final Object mInsets;
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 21)
    {
      IMPL = new WindowInsetsCompatApi21Impl();
      return;
    }
    if (i >= 20)
    {
      IMPL = new WindowInsetsCompatApi20Impl();
      return;
    }
  }
  
  public WindowInsetsCompat(WindowInsetsCompat paramWindowInsetsCompat)
  {
    if (paramWindowInsetsCompat == null) {}
    for (paramWindowInsetsCompat = null;; paramWindowInsetsCompat = IMPL.getSourceWindowInsets(paramWindowInsetsCompat.mInsets))
    {
      this.mInsets = paramWindowInsetsCompat;
      return;
    }
  }
  
  WindowInsetsCompat(Object paramObject)
  {
    this.mInsets = paramObject;
  }
  
  static Object unwrap(WindowInsetsCompat paramWindowInsetsCompat)
  {
    if (paramWindowInsetsCompat == null) {
      return null;
    }
    return paramWindowInsetsCompat.mInsets;
  }
  
  static WindowInsetsCompat wrap(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    return new WindowInsetsCompat(paramObject);
  }
  
  public WindowInsetsCompat consumeStableInsets()
  {
    return IMPL.consumeStableInsets(this.mInsets);
  }
  
  public WindowInsetsCompat consumeSystemWindowInsets()
  {
    return IMPL.consumeSystemWindowInsets(this.mInsets);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (WindowInsetsCompat)paramObject;
      if (this.mInsets != null) {
        break;
      }
    } while (((WindowInsetsCompat)paramObject).mInsets == null);
    return false;
    return this.mInsets.equals(((WindowInsetsCompat)paramObject).mInsets);
  }
  
  public int getStableInsetBottom()
  {
    return IMPL.getStableInsetBottom(this.mInsets);
  }
  
  public int getStableInsetLeft()
  {
    return IMPL.getStableInsetLeft(this.mInsets);
  }
  
  public int getStableInsetRight()
  {
    return IMPL.getStableInsetRight(this.mInsets);
  }
  
  public int getStableInsetTop()
  {
    return IMPL.getStableInsetTop(this.mInsets);
  }
  
  public int getSystemWindowInsetBottom()
  {
    return IMPL.getSystemWindowInsetBottom(this.mInsets);
  }
  
  public int getSystemWindowInsetLeft()
  {
    return IMPL.getSystemWindowInsetLeft(this.mInsets);
  }
  
  public int getSystemWindowInsetRight()
  {
    return IMPL.getSystemWindowInsetRight(this.mInsets);
  }
  
  public int getSystemWindowInsetTop()
  {
    return IMPL.getSystemWindowInsetTop(this.mInsets);
  }
  
  public boolean hasInsets()
  {
    return IMPL.hasInsets(this.mInsets);
  }
  
  public boolean hasStableInsets()
  {
    return IMPL.hasStableInsets(this.mInsets);
  }
  
  public boolean hasSystemWindowInsets()
  {
    return IMPL.hasSystemWindowInsets(this.mInsets);
  }
  
  public int hashCode()
  {
    if (this.mInsets == null) {
      return 0;
    }
    return this.mInsets.hashCode();
  }
  
  public boolean isConsumed()
  {
    return IMPL.isConsumed(this.mInsets);
  }
  
  public boolean isRound()
  {
    return IMPL.isRound(this.mInsets);
  }
  
  public WindowInsetsCompat replaceSystemWindowInsets(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return IMPL.replaceSystemWindowInsets(this.mInsets, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public WindowInsetsCompat replaceSystemWindowInsets(Rect paramRect)
  {
    return IMPL.replaceSystemWindowInsets(this.mInsets, paramRect);
  }
  
  private static class WindowInsetsCompatApi20Impl
    extends WindowInsetsCompat.WindowInsetsCompatBaseImpl
  {
    public WindowInsetsCompat consumeSystemWindowInsets(Object paramObject)
    {
      return new WindowInsetsCompat(WindowInsetsCompatApi20.consumeSystemWindowInsets(paramObject));
    }
    
    public Object getSourceWindowInsets(Object paramObject)
    {
      return WindowInsetsCompatApi20.getSourceWindowInsets(paramObject);
    }
    
    public int getSystemWindowInsetBottom(Object paramObject)
    {
      return WindowInsetsCompatApi20.getSystemWindowInsetBottom(paramObject);
    }
    
    public int getSystemWindowInsetLeft(Object paramObject)
    {
      return WindowInsetsCompatApi20.getSystemWindowInsetLeft(paramObject);
    }
    
    public int getSystemWindowInsetRight(Object paramObject)
    {
      return WindowInsetsCompatApi20.getSystemWindowInsetRight(paramObject);
    }
    
    public int getSystemWindowInsetTop(Object paramObject)
    {
      return WindowInsetsCompatApi20.getSystemWindowInsetTop(paramObject);
    }
    
    public boolean hasInsets(Object paramObject)
    {
      return WindowInsetsCompatApi20.hasInsets(paramObject);
    }
    
    public boolean hasSystemWindowInsets(Object paramObject)
    {
      return WindowInsetsCompatApi20.hasSystemWindowInsets(paramObject);
    }
    
    public boolean isRound(Object paramObject)
    {
      return WindowInsetsCompatApi20.isRound(paramObject);
    }
    
    public WindowInsetsCompat replaceSystemWindowInsets(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return new WindowInsetsCompat(WindowInsetsCompatApi20.replaceSystemWindowInsets(paramObject, paramInt1, paramInt2, paramInt3, paramInt4));
    }
  }
  
  private static class WindowInsetsCompatApi21Impl
    extends WindowInsetsCompat.WindowInsetsCompatApi20Impl
  {
    public WindowInsetsCompat consumeStableInsets(Object paramObject)
    {
      return new WindowInsetsCompat(WindowInsetsCompatApi21.consumeStableInsets(paramObject));
    }
    
    public int getStableInsetBottom(Object paramObject)
    {
      return WindowInsetsCompatApi21.getStableInsetBottom(paramObject);
    }
    
    public int getStableInsetLeft(Object paramObject)
    {
      return WindowInsetsCompatApi21.getStableInsetLeft(paramObject);
    }
    
    public int getStableInsetRight(Object paramObject)
    {
      return WindowInsetsCompatApi21.getStableInsetRight(paramObject);
    }
    
    public int getStableInsetTop(Object paramObject)
    {
      return WindowInsetsCompatApi21.getStableInsetTop(paramObject);
    }
    
    public boolean hasStableInsets(Object paramObject)
    {
      return WindowInsetsCompatApi21.hasStableInsets(paramObject);
    }
    
    public boolean isConsumed(Object paramObject)
    {
      return WindowInsetsCompatApi21.isConsumed(paramObject);
    }
    
    public WindowInsetsCompat replaceSystemWindowInsets(Object paramObject, Rect paramRect)
    {
      return new WindowInsetsCompat(WindowInsetsCompatApi21.replaceSystemWindowInsets(paramObject, paramRect));
    }
  }
  
  private static class WindowInsetsCompatBaseImpl
    implements WindowInsetsCompat.WindowInsetsCompatImpl
  {
    public WindowInsetsCompat consumeStableInsets(Object paramObject)
    {
      return null;
    }
    
    public WindowInsetsCompat consumeSystemWindowInsets(Object paramObject)
    {
      return null;
    }
    
    public Object getSourceWindowInsets(Object paramObject)
    {
      return null;
    }
    
    public int getStableInsetBottom(Object paramObject)
    {
      return 0;
    }
    
    public int getStableInsetLeft(Object paramObject)
    {
      return 0;
    }
    
    public int getStableInsetRight(Object paramObject)
    {
      return 0;
    }
    
    public int getStableInsetTop(Object paramObject)
    {
      return 0;
    }
    
    public int getSystemWindowInsetBottom(Object paramObject)
    {
      return 0;
    }
    
    public int getSystemWindowInsetLeft(Object paramObject)
    {
      return 0;
    }
    
    public int getSystemWindowInsetRight(Object paramObject)
    {
      return 0;
    }
    
    public int getSystemWindowInsetTop(Object paramObject)
    {
      return 0;
    }
    
    public boolean hasInsets(Object paramObject)
    {
      return false;
    }
    
    public boolean hasStableInsets(Object paramObject)
    {
      return false;
    }
    
    public boolean hasSystemWindowInsets(Object paramObject)
    {
      return false;
    }
    
    public boolean isConsumed(Object paramObject)
    {
      return false;
    }
    
    public boolean isRound(Object paramObject)
    {
      return false;
    }
    
    public WindowInsetsCompat replaceSystemWindowInsets(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return null;
    }
    
    public WindowInsetsCompat replaceSystemWindowInsets(Object paramObject, Rect paramRect)
    {
      return null;
    }
  }
  
  private static abstract interface WindowInsetsCompatImpl
  {
    public abstract WindowInsetsCompat consumeStableInsets(Object paramObject);
    
    public abstract WindowInsetsCompat consumeSystemWindowInsets(Object paramObject);
    
    public abstract Object getSourceWindowInsets(Object paramObject);
    
    public abstract int getStableInsetBottom(Object paramObject);
    
    public abstract int getStableInsetLeft(Object paramObject);
    
    public abstract int getStableInsetRight(Object paramObject);
    
    public abstract int getStableInsetTop(Object paramObject);
    
    public abstract int getSystemWindowInsetBottom(Object paramObject);
    
    public abstract int getSystemWindowInsetLeft(Object paramObject);
    
    public abstract int getSystemWindowInsetRight(Object paramObject);
    
    public abstract int getSystemWindowInsetTop(Object paramObject);
    
    public abstract boolean hasInsets(Object paramObject);
    
    public abstract boolean hasStableInsets(Object paramObject);
    
    public abstract boolean hasSystemWindowInsets(Object paramObject);
    
    public abstract boolean isConsumed(Object paramObject);
    
    public abstract boolean isRound(Object paramObject);
    
    public abstract WindowInsetsCompat replaceSystemWindowInsets(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract WindowInsetsCompat replaceSystemWindowInsets(Object paramObject, Rect paramRect);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/WindowInsetsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */