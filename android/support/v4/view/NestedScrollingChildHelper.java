package android.support.v4.view;

import android.view.View;
import android.view.ViewParent;

public class NestedScrollingChildHelper
{
  private boolean mIsNestedScrollingEnabled;
  private ViewParent mNestedScrollingParentNonTouch;
  private ViewParent mNestedScrollingParentTouch;
  private int[] mTempNestedScrollConsumed;
  private final View mView;
  
  public NestedScrollingChildHelper(View paramView)
  {
    this.mView = paramView;
  }
  
  private ViewParent getNestedScrollingParentForType(int paramInt)
  {
    ViewParent localViewParent;
    switch (paramInt)
    {
    default: 
      localViewParent = null;
    }
    for (;;)
    {
      return localViewParent;
      localViewParent = this.mNestedScrollingParentTouch;
      continue;
      localViewParent = this.mNestedScrollingParentNonTouch;
    }
  }
  
  private void setNestedScrollingParentForType(int paramInt, ViewParent paramViewParent)
  {
    switch (paramInt)
    {
    }
    for (;;)
    {
      return;
      this.mNestedScrollingParentTouch = paramViewParent;
      continue;
      this.mNestedScrollingParentNonTouch = paramViewParent;
    }
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (isNestedScrollingEnabled())
    {
      ViewParent localViewParent = getNestedScrollingParentForType(0);
      bool2 = bool1;
      if (localViewParent != null) {
        bool2 = ViewParentCompat.onNestedFling(localViewParent, this.mView, paramFloat1, paramFloat2, paramBoolean);
      }
    }
    return bool2;
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (isNestedScrollingEnabled())
    {
      ViewParent localViewParent = getNestedScrollingParentForType(0);
      bool2 = bool1;
      if (localViewParent != null) {
        bool2 = ViewParentCompat.onNestedPreFling(localViewParent, this.mView, paramFloat1, paramFloat2);
      }
    }
    return bool2;
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    return dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2, 0);
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    ViewParent localViewParent;
    if (isNestedScrollingEnabled())
    {
      localViewParent = getNestedScrollingParentForType(paramInt3);
      if (localViewParent != null) {
        break label34;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      label34:
      if ((paramInt1 != 0) || (paramInt2 != 0))
      {
        int i = 0;
        int j = 0;
        if (paramArrayOfInt2 != null)
        {
          this.mView.getLocationInWindow(paramArrayOfInt2);
          i = paramArrayOfInt2[0];
          j = paramArrayOfInt2[1];
        }
        int[] arrayOfInt = paramArrayOfInt1;
        if (paramArrayOfInt1 == null)
        {
          if (this.mTempNestedScrollConsumed == null) {
            this.mTempNestedScrollConsumed = new int[2];
          }
          arrayOfInt = this.mTempNestedScrollConsumed;
        }
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 0;
        ViewParentCompat.onNestedPreScroll(localViewParent, this.mView, paramInt1, paramInt2, arrayOfInt, paramInt3);
        if (paramArrayOfInt2 != null)
        {
          this.mView.getLocationInWindow(paramArrayOfInt2);
          paramArrayOfInt2[0] -= i;
          paramArrayOfInt2[1] -= j;
        }
        if ((arrayOfInt[0] != 0) || (arrayOfInt[1] != 0)) {}
        for (bool2 = true;; bool2 = false) {
          break;
        }
      }
      bool2 = bool1;
      if (paramArrayOfInt2 != null)
      {
        paramArrayOfInt2[0] = 0;
        paramArrayOfInt2[1] = 0;
        bool2 = bool1;
      }
    }
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    return dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, 0);
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5)
  {
    ViewParent localViewParent;
    boolean bool;
    if (isNestedScrollingEnabled())
    {
      localViewParent = getNestedScrollingParentForType(paramInt5);
      if (localViewParent == null) {
        bool = false;
      }
    }
    for (;;)
    {
      return bool;
      if ((paramInt1 != 0) || (paramInt2 != 0) || (paramInt3 != 0) || (paramInt4 != 0))
      {
        int i = 0;
        int j = 0;
        if (paramArrayOfInt != null)
        {
          this.mView.getLocationInWindow(paramArrayOfInt);
          i = paramArrayOfInt[0];
          j = paramArrayOfInt[1];
        }
        ViewParentCompat.onNestedScroll(localViewParent, this.mView, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
        if (paramArrayOfInt != null)
        {
          this.mView.getLocationInWindow(paramArrayOfInt);
          paramArrayOfInt[0] -= i;
          paramArrayOfInt[1] -= j;
        }
        bool = true;
      }
      else
      {
        if (paramArrayOfInt != null)
        {
          paramArrayOfInt[0] = 0;
          paramArrayOfInt[1] = 0;
        }
        bool = false;
      }
    }
  }
  
  public boolean hasNestedScrollingParent()
  {
    return hasNestedScrollingParent(0);
  }
  
  public boolean hasNestedScrollingParent(int paramInt)
  {
    if (getNestedScrollingParentForType(paramInt) != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isNestedScrollingEnabled()
  {
    return this.mIsNestedScrollingEnabled;
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    if (this.mIsNestedScrollingEnabled) {
      ViewCompat.stopNestedScroll(this.mView);
    }
    this.mIsNestedScrollingEnabled = paramBoolean;
  }
  
  public boolean startNestedScroll(int paramInt)
  {
    return startNestedScroll(paramInt, 0);
  }
  
  public boolean startNestedScroll(int paramInt1, int paramInt2)
  {
    boolean bool = true;
    if (hasNestedScrollingParent(paramInt2)) {}
    for (;;)
    {
      return bool;
      if (isNestedScrollingEnabled())
      {
        ViewParent localViewParent = this.mView.getParent();
        View localView = this.mView;
        for (;;)
        {
          if (localViewParent == null) {
            break label105;
          }
          if (ViewParentCompat.onStartNestedScroll(localViewParent, localView, this.mView, paramInt1, paramInt2))
          {
            setNestedScrollingParentForType(paramInt2, localViewParent);
            ViewParentCompat.onNestedScrollAccepted(localViewParent, localView, this.mView, paramInt1, paramInt2);
            break;
          }
          if ((localViewParent instanceof View)) {
            localView = (View)localViewParent;
          }
          localViewParent = localViewParent.getParent();
        }
      }
      label105:
      bool = false;
    }
  }
  
  public void stopNestedScroll()
  {
    stopNestedScroll(0);
  }
  
  public void stopNestedScroll(int paramInt)
  {
    ViewParent localViewParent = getNestedScrollingParentForType(paramInt);
    if (localViewParent != null)
    {
      ViewParentCompat.onStopNestedScroll(localViewParent, this.mView, paramInt);
      setNestedScrollingParentForType(paramInt, null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/NestedScrollingChildHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */