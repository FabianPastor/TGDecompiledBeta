package android.support.v4.view.accessibility;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;

public class AccessibilityNodeInfoCompat
{
  private final AccessibilityNodeInfo mInfo;
  public int mParentVirtualDescendantId = -1;
  
  private AccessibilityNodeInfoCompat(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    this.mInfo = paramAccessibilityNodeInfo;
  }
  
  private static String getActionSymbolicName(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "ACTION_UNKNOWN";
    }
    for (;;)
    {
      return str;
      str = "ACTION_FOCUS";
      continue;
      str = "ACTION_CLEAR_FOCUS";
      continue;
      str = "ACTION_SELECT";
      continue;
      str = "ACTION_CLEAR_SELECTION";
      continue;
      str = "ACTION_CLICK";
      continue;
      str = "ACTION_LONG_CLICK";
      continue;
      str = "ACTION_ACCESSIBILITY_FOCUS";
      continue;
      str = "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
      continue;
      str = "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
      continue;
      str = "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
      continue;
      str = "ACTION_NEXT_HTML_ELEMENT";
      continue;
      str = "ACTION_PREVIOUS_HTML_ELEMENT";
      continue;
      str = "ACTION_SCROLL_FORWARD";
      continue;
      str = "ACTION_SCROLL_BACKWARD";
      continue;
      str = "ACTION_CUT";
      continue;
      str = "ACTION_COPY";
      continue;
      str = "ACTION_PASTE";
      continue;
      str = "ACTION_SET_SELECTION";
    }
  }
  
  public static AccessibilityNodeInfoCompat wrap(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    return new AccessibilityNodeInfoCompat(paramAccessibilityNodeInfo);
  }
  
  public void addAction(int paramInt)
  {
    this.mInfo.addAction(paramInt);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if (paramObject == null)
      {
        bool = false;
      }
      else if (getClass() != paramObject.getClass())
      {
        bool = false;
      }
      else
      {
        paramObject = (AccessibilityNodeInfoCompat)paramObject;
        if (this.mInfo == null)
        {
          if (((AccessibilityNodeInfoCompat)paramObject).mInfo != null) {
            bool = false;
          }
        }
        else if (!this.mInfo.equals(((AccessibilityNodeInfoCompat)paramObject).mInfo)) {
          bool = false;
        }
      }
    }
  }
  
  public int getActions()
  {
    return this.mInfo.getActions();
  }
  
  public void getBoundsInParent(Rect paramRect)
  {
    this.mInfo.getBoundsInParent(paramRect);
  }
  
  public void getBoundsInScreen(Rect paramRect)
  {
    this.mInfo.getBoundsInScreen(paramRect);
  }
  
  public CharSequence getClassName()
  {
    return this.mInfo.getClassName();
  }
  
  public CharSequence getContentDescription()
  {
    return this.mInfo.getContentDescription();
  }
  
  public CharSequence getPackageName()
  {
    return this.mInfo.getPackageName();
  }
  
  public CharSequence getText()
  {
    return this.mInfo.getText();
  }
  
  public String getViewIdResourceName()
  {
    if (Build.VERSION.SDK_INT >= 18) {}
    for (String str = this.mInfo.getViewIdResourceName();; str = null) {
      return str;
    }
  }
  
  public int hashCode()
  {
    if (this.mInfo == null) {}
    for (int i = 0;; i = this.mInfo.hashCode()) {
      return i;
    }
  }
  
  public boolean isCheckable()
  {
    return this.mInfo.isCheckable();
  }
  
  public boolean isChecked()
  {
    return this.mInfo.isChecked();
  }
  
  public boolean isClickable()
  {
    return this.mInfo.isClickable();
  }
  
  public boolean isEnabled()
  {
    return this.mInfo.isEnabled();
  }
  
  public boolean isFocusable()
  {
    return this.mInfo.isFocusable();
  }
  
  public boolean isFocused()
  {
    return this.mInfo.isFocused();
  }
  
  public boolean isLongClickable()
  {
    return this.mInfo.isLongClickable();
  }
  
  public boolean isPassword()
  {
    return this.mInfo.isPassword();
  }
  
  public boolean isScrollable()
  {
    return this.mInfo.isScrollable();
  }
  
  public boolean isSelected()
  {
    return this.mInfo.isSelected();
  }
  
  public void setClassName(CharSequence paramCharSequence)
  {
    this.mInfo.setClassName(paramCharSequence);
  }
  
  public void setCollectionInfo(Object paramObject)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      this.mInfo.setCollectionInfo((AccessibilityNodeInfo.CollectionInfo)((CollectionInfoCompat)paramObject).mInfo);
    }
  }
  
  public void setCollectionItemInfo(Object paramObject)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      this.mInfo.setCollectionItemInfo((AccessibilityNodeInfo.CollectionItemInfo)((CollectionItemInfoCompat)paramObject).mInfo);
    }
  }
  
  public void setScrollable(boolean paramBoolean)
  {
    this.mInfo.setScrollable(paramBoolean);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString());
    Rect localRect = new Rect();
    getBoundsInParent(localRect);
    localStringBuilder.append("; boundsInParent: " + localRect);
    getBoundsInScreen(localRect);
    localStringBuilder.append("; boundsInScreen: " + localRect);
    localStringBuilder.append("; packageName: ").append(getPackageName());
    localStringBuilder.append("; className: ").append(getClassName());
    localStringBuilder.append("; text: ").append(getText());
    localStringBuilder.append("; contentDescription: ").append(getContentDescription());
    localStringBuilder.append("; viewId: ").append(getViewIdResourceName());
    localStringBuilder.append("; checkable: ").append(isCheckable());
    localStringBuilder.append("; checked: ").append(isChecked());
    localStringBuilder.append("; focusable: ").append(isFocusable());
    localStringBuilder.append("; focused: ").append(isFocused());
    localStringBuilder.append("; selected: ").append(isSelected());
    localStringBuilder.append("; clickable: ").append(isClickable());
    localStringBuilder.append("; longClickable: ").append(isLongClickable());
    localStringBuilder.append("; enabled: ").append(isEnabled());
    localStringBuilder.append("; password: ").append(isPassword());
    localStringBuilder.append("; scrollable: " + isScrollable());
    localStringBuilder.append("; [");
    int i = getActions();
    while (i != 0)
    {
      int j = 1 << Integer.numberOfTrailingZeros(i);
      int k = i & (j ^ 0xFFFFFFFF);
      localStringBuilder.append(getActionSymbolicName(j));
      i = k;
      if (k != 0)
      {
        localStringBuilder.append(", ");
        i = k;
      }
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public AccessibilityNodeInfo unwrap()
  {
    return this.mInfo;
  }
  
  public static class CollectionInfoCompat
  {
    final Object mInfo;
    
    CollectionInfoCompat(Object paramObject)
    {
      this.mInfo = paramObject;
    }
    
    public static CollectionInfoCompat obtain(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
    {
      CollectionInfoCompat localCollectionInfoCompat;
      if (Build.VERSION.SDK_INT >= 21) {
        localCollectionInfoCompat = new CollectionInfoCompat(AccessibilityNodeInfo.CollectionInfo.obtain(paramInt1, paramInt2, paramBoolean, paramInt3));
      }
      for (;;)
      {
        return localCollectionInfoCompat;
        if (Build.VERSION.SDK_INT >= 19) {
          localCollectionInfoCompat = new CollectionInfoCompat(AccessibilityNodeInfo.CollectionInfo.obtain(paramInt1, paramInt2, paramBoolean));
        } else {
          localCollectionInfoCompat = new CollectionInfoCompat(null);
        }
      }
    }
  }
  
  public static class CollectionItemInfoCompat
  {
    final Object mInfo;
    
    CollectionItemInfoCompat(Object paramObject)
    {
      this.mInfo = paramObject;
    }
    
    public static CollectionItemInfoCompat obtain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
    {
      CollectionItemInfoCompat localCollectionItemInfoCompat;
      if (Build.VERSION.SDK_INT >= 21) {
        localCollectionItemInfoCompat = new CollectionItemInfoCompat(AccessibilityNodeInfo.CollectionItemInfo.obtain(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean1, paramBoolean2));
      }
      for (;;)
      {
        return localCollectionItemInfoCompat;
        if (Build.VERSION.SDK_INT >= 19) {
          localCollectionItemInfoCompat = new CollectionItemInfoCompat(AccessibilityNodeInfo.CollectionItemInfo.obtain(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean1));
        } else {
          localCollectionItemInfoCompat = new CollectionItemInfoCompat(null);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/accessibility/AccessibilityNodeInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */