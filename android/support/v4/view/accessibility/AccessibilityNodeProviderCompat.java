package android.support.v4.view.accessibility;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import java.util.ArrayList;
import java.util.List;

public class AccessibilityNodeProviderCompat
{
  private final Object mProvider;
  
  public AccessibilityNodeProviderCompat()
  {
    if (Build.VERSION.SDK_INT >= 19) {
      this.mProvider = new AccessibilityNodeProviderApi19(this);
    }
    for (;;)
    {
      return;
      if (Build.VERSION.SDK_INT >= 16) {
        this.mProvider = new AccessibilityNodeProviderApi16(this);
      } else {
        this.mProvider = null;
      }
    }
  }
  
  public AccessibilityNodeProviderCompat(Object paramObject)
  {
    this.mProvider = paramObject;
  }
  
  public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(int paramInt)
  {
    return null;
  }
  
  public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText(String paramString, int paramInt)
  {
    return null;
  }
  
  public AccessibilityNodeInfoCompat findFocus(int paramInt)
  {
    return null;
  }
  
  public Object getProvider()
  {
    return this.mProvider;
  }
  
  public boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    return false;
  }
  
  static class AccessibilityNodeProviderApi16
    extends AccessibilityNodeProvider
  {
    final AccessibilityNodeProviderCompat mCompat;
    
    AccessibilityNodeProviderApi16(AccessibilityNodeProviderCompat paramAccessibilityNodeProviderCompat)
    {
      this.mCompat = paramAccessibilityNodeProviderCompat;
    }
    
    public AccessibilityNodeInfo createAccessibilityNodeInfo(int paramInt)
    {
      Object localObject = this.mCompat.createAccessibilityNodeInfo(paramInt);
      if (localObject == null) {}
      for (localObject = null;; localObject = ((AccessibilityNodeInfoCompat)localObject).unwrap()) {
        return (AccessibilityNodeInfo)localObject;
      }
    }
    
    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String paramString, int paramInt)
    {
      List localList = this.mCompat.findAccessibilityNodeInfosByText(paramString, paramInt);
      if (localList == null)
      {
        paramString = null;
        return paramString;
      }
      ArrayList localArrayList = new ArrayList();
      int i = localList.size();
      for (paramInt = 0;; paramInt++)
      {
        paramString = localArrayList;
        if (paramInt >= i) {
          break;
        }
        localArrayList.add(((AccessibilityNodeInfoCompat)localList.get(paramInt)).unwrap());
      }
    }
    
    public boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      return this.mCompat.performAction(paramInt1, paramInt2, paramBundle);
    }
  }
  
  static class AccessibilityNodeProviderApi19
    extends AccessibilityNodeProviderCompat.AccessibilityNodeProviderApi16
  {
    AccessibilityNodeProviderApi19(AccessibilityNodeProviderCompat paramAccessibilityNodeProviderCompat)
    {
      super();
    }
    
    public AccessibilityNodeInfo findFocus(int paramInt)
    {
      Object localObject = this.mCompat.findFocus(paramInt);
      if (localObject == null) {}
      for (localObject = null;; localObject = ((AccessibilityNodeInfoCompat)localObject).unwrap()) {
        return (AccessibilityNodeInfo)localObject;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/accessibility/AccessibilityNodeProviderCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */