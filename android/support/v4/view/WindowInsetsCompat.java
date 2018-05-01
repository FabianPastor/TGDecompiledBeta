package android.support.v4.view;

import android.os.Build.VERSION;
import android.view.WindowInsets;

public class WindowInsetsCompat
{
  private final Object mInsets;
  
  private WindowInsetsCompat(Object paramObject)
  {
    this.mInsets = paramObject;
  }
  
  static Object unwrap(WindowInsetsCompat paramWindowInsetsCompat)
  {
    if (paramWindowInsetsCompat == null) {}
    for (paramWindowInsetsCompat = null;; paramWindowInsetsCompat = paramWindowInsetsCompat.mInsets) {
      return paramWindowInsetsCompat;
    }
  }
  
  static WindowInsetsCompat wrap(Object paramObject)
  {
    if (paramObject == null) {}
    for (paramObject = null;; paramObject = new WindowInsetsCompat(paramObject)) {
      return (WindowInsetsCompat)paramObject;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (WindowInsetsCompat)paramObject;
        if (this.mInsets == null)
        {
          if (((WindowInsetsCompat)paramObject).mInsets != null) {
            bool = false;
          }
        }
        else {
          bool = this.mInsets.equals(((WindowInsetsCompat)paramObject).mInsets);
        }
      }
    }
  }
  
  public int getSystemWindowInsetBottom()
  {
    if (Build.VERSION.SDK_INT >= 20) {}
    for (int i = ((WindowInsets)this.mInsets).getSystemWindowInsetBottom();; i = 0) {
      return i;
    }
  }
  
  public int getSystemWindowInsetLeft()
  {
    if (Build.VERSION.SDK_INT >= 20) {}
    for (int i = ((WindowInsets)this.mInsets).getSystemWindowInsetLeft();; i = 0) {
      return i;
    }
  }
  
  public int getSystemWindowInsetRight()
  {
    if (Build.VERSION.SDK_INT >= 20) {}
    for (int i = ((WindowInsets)this.mInsets).getSystemWindowInsetRight();; i = 0) {
      return i;
    }
  }
  
  public int getSystemWindowInsetTop()
  {
    if (Build.VERSION.SDK_INT >= 20) {}
    for (int i = ((WindowInsets)this.mInsets).getSystemWindowInsetTop();; i = 0) {
      return i;
    }
  }
  
  public int hashCode()
  {
    if (this.mInsets == null) {}
    for (int i = 0;; i = this.mInsets.hashCode()) {
      return i;
    }
  }
  
  public boolean isConsumed()
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    for (boolean bool = ((WindowInsets)this.mInsets).isConsumed();; bool = false) {
      return bool;
    }
  }
  
  public WindowInsetsCompat replaceSystemWindowInsets(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (Build.VERSION.SDK_INT >= 20) {}
    for (WindowInsetsCompat localWindowInsetsCompat = new WindowInsetsCompat(((WindowInsets)this.mInsets).replaceSystemWindowInsets(paramInt1, paramInt2, paramInt3, paramInt4));; localWindowInsetsCompat = null) {
      return localWindowInsetsCompat;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/WindowInsetsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */