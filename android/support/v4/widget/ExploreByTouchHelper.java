package android.support.v4.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewParentCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.util.ArrayList;
import java.util.List;

public abstract class ExploreByTouchHelper
  extends AccessibilityDelegateCompat
{
  private static final String DEFAULT_CLASS_NAME = "android.view.View";
  public static final int HOST_ID = -1;
  public static final int INVALID_ID = Integer.MIN_VALUE;
  private static final Rect INVALID_PARENT_BOUNDS = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
  private static final FocusStrategy.BoundsAdapter<AccessibilityNodeInfoCompat> NODE_ADAPTER = new FocusStrategy.BoundsAdapter()
  {
    public void obtainBounds(AccessibilityNodeInfoCompat paramAnonymousAccessibilityNodeInfoCompat, Rect paramAnonymousRect)
    {
      paramAnonymousAccessibilityNodeInfoCompat.getBoundsInParent(paramAnonymousRect);
    }
  };
  private static final FocusStrategy.CollectionAdapter<SparseArrayCompat<AccessibilityNodeInfoCompat>, AccessibilityNodeInfoCompat> SPARSE_VALUES_ADAPTER = new FocusStrategy.CollectionAdapter()
  {
    public AccessibilityNodeInfoCompat get(SparseArrayCompat<AccessibilityNodeInfoCompat> paramAnonymousSparseArrayCompat, int paramAnonymousInt)
    {
      return (AccessibilityNodeInfoCompat)paramAnonymousSparseArrayCompat.valueAt(paramAnonymousInt);
    }
    
    public int size(SparseArrayCompat<AccessibilityNodeInfoCompat> paramAnonymousSparseArrayCompat)
    {
      return paramAnonymousSparseArrayCompat.size();
    }
  };
  private int mAccessibilityFocusedVirtualViewId = Integer.MIN_VALUE;
  private final View mHost;
  private int mHoveredVirtualViewId = Integer.MIN_VALUE;
  private int mKeyboardFocusedVirtualViewId = Integer.MIN_VALUE;
  private final AccessibilityManager mManager;
  private MyNodeProvider mNodeProvider;
  private final int[] mTempGlobalRect = new int[2];
  private final Rect mTempParentRect = new Rect();
  private final Rect mTempScreenRect = new Rect();
  private final Rect mTempVisibleRect = new Rect();
  
  public ExploreByTouchHelper(View paramView)
  {
    if (paramView == null) {
      throw new IllegalArgumentException("View may not be null");
    }
    this.mHost = paramView;
    this.mManager = ((AccessibilityManager)paramView.getContext().getSystemService("accessibility"));
    paramView.setFocusable(true);
    if (ViewCompat.getImportantForAccessibility(paramView) == 0) {
      ViewCompat.setImportantForAccessibility(paramView, 1);
    }
  }
  
  private boolean clearAccessibilityFocus(int paramInt)
  {
    if (this.mAccessibilityFocusedVirtualViewId == paramInt)
    {
      this.mAccessibilityFocusedVirtualViewId = Integer.MIN_VALUE;
      this.mHost.invalidate();
      sendEventForVirtualView(paramInt, 65536);
      return true;
    }
    return false;
  }
  
  private boolean clickKeyboardFocusedVirtualView()
  {
    return (this.mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) && (onPerformActionForVirtualView(this.mKeyboardFocusedVirtualViewId, 16, null));
  }
  
  private AccessibilityEvent createEvent(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return createEventForChild(paramInt1, paramInt2);
    }
    return createEventForHost(paramInt2);
  }
  
  private AccessibilityEvent createEventForChild(int paramInt1, int paramInt2)
  {
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt2);
    AccessibilityRecordCompat localAccessibilityRecordCompat = AccessibilityEventCompat.asRecord(localAccessibilityEvent);
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = obtainAccessibilityNodeInfo(paramInt1);
    localAccessibilityRecordCompat.getText().add(localAccessibilityNodeInfoCompat.getText());
    localAccessibilityRecordCompat.setContentDescription(localAccessibilityNodeInfoCompat.getContentDescription());
    localAccessibilityRecordCompat.setScrollable(localAccessibilityNodeInfoCompat.isScrollable());
    localAccessibilityRecordCompat.setPassword(localAccessibilityNodeInfoCompat.isPassword());
    localAccessibilityRecordCompat.setEnabled(localAccessibilityNodeInfoCompat.isEnabled());
    localAccessibilityRecordCompat.setChecked(localAccessibilityNodeInfoCompat.isChecked());
    onPopulateEventForVirtualView(paramInt1, localAccessibilityEvent);
    if ((localAccessibilityEvent.getText().isEmpty()) && (localAccessibilityEvent.getContentDescription() == null)) {
      throw new RuntimeException("Callbacks must add text or a content description in populateEventForVirtualViewId()");
    }
    localAccessibilityRecordCompat.setClassName(localAccessibilityNodeInfoCompat.getClassName());
    localAccessibilityRecordCompat.setSource(this.mHost, paramInt1);
    localAccessibilityEvent.setPackageName(this.mHost.getContext().getPackageName());
    return localAccessibilityEvent;
  }
  
  private AccessibilityEvent createEventForHost(int paramInt)
  {
    AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(paramInt);
    ViewCompat.onInitializeAccessibilityEvent(this.mHost, localAccessibilityEvent);
    return localAccessibilityEvent;
  }
  
  @NonNull
  private AccessibilityNodeInfoCompat createNodeForChild(int paramInt)
  {
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat1 = AccessibilityNodeInfoCompat.obtain();
    localAccessibilityNodeInfoCompat1.setEnabled(true);
    localAccessibilityNodeInfoCompat1.setFocusable(true);
    localAccessibilityNodeInfoCompat1.setClassName("android.view.View");
    localAccessibilityNodeInfoCompat1.setBoundsInParent(INVALID_PARENT_BOUNDS);
    localAccessibilityNodeInfoCompat1.setBoundsInScreen(INVALID_PARENT_BOUNDS);
    localAccessibilityNodeInfoCompat1.setParent(this.mHost);
    onPopulateNodeForVirtualView(paramInt, localAccessibilityNodeInfoCompat1);
    if ((localAccessibilityNodeInfoCompat1.getText() == null) && (localAccessibilityNodeInfoCompat1.getContentDescription() == null)) {
      throw new RuntimeException("Callbacks must add text or a content description in populateNodeForVirtualViewId()");
    }
    localAccessibilityNodeInfoCompat1.getBoundsInParent(this.mTempParentRect);
    if (this.mTempParentRect.equals(INVALID_PARENT_BOUNDS)) {
      throw new RuntimeException("Callbacks must set parent bounds in populateNodeForVirtualViewId()");
    }
    int i = localAccessibilityNodeInfoCompat1.getActions();
    if ((i & 0x40) != 0) {
      throw new RuntimeException("Callbacks must not add ACTION_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
    }
    if ((i & 0x80) != 0) {
      throw new RuntimeException("Callbacks must not add ACTION_CLEAR_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
    }
    localAccessibilityNodeInfoCompat1.setPackageName(this.mHost.getContext().getPackageName());
    localAccessibilityNodeInfoCompat1.setSource(this.mHost, paramInt);
    boolean bool;
    if (this.mAccessibilityFocusedVirtualViewId == paramInt)
    {
      localAccessibilityNodeInfoCompat1.setAccessibilityFocused(true);
      localAccessibilityNodeInfoCompat1.addAction(128);
      if (this.mKeyboardFocusedVirtualViewId != paramInt) {
        break label379;
      }
      bool = true;
      label216:
      if (!bool) {
        break label384;
      }
      localAccessibilityNodeInfoCompat1.addAction(2);
    }
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat2;
    for (;;)
    {
      localAccessibilityNodeInfoCompat1.setFocused(bool);
      this.mHost.getLocationOnScreen(this.mTempGlobalRect);
      localAccessibilityNodeInfoCompat1.getBoundsInScreen(this.mTempScreenRect);
      if (!this.mTempScreenRect.equals(INVALID_PARENT_BOUNDS)) {
        break label441;
      }
      localAccessibilityNodeInfoCompat1.getBoundsInParent(this.mTempScreenRect);
      if (localAccessibilityNodeInfoCompat1.mParentVirtualDescendantId == -1) {
        break label406;
      }
      localAccessibilityNodeInfoCompat2 = AccessibilityNodeInfoCompat.obtain();
      for (paramInt = localAccessibilityNodeInfoCompat1.mParentVirtualDescendantId; paramInt != -1; paramInt = localAccessibilityNodeInfoCompat2.mParentVirtualDescendantId)
      {
        localAccessibilityNodeInfoCompat2.setParent(this.mHost, -1);
        localAccessibilityNodeInfoCompat2.setBoundsInParent(INVALID_PARENT_BOUNDS);
        onPopulateNodeForVirtualView(paramInt, localAccessibilityNodeInfoCompat2);
        localAccessibilityNodeInfoCompat2.getBoundsInParent(this.mTempParentRect);
        this.mTempScreenRect.offset(this.mTempParentRect.left, this.mTempParentRect.top);
      }
      localAccessibilityNodeInfoCompat1.setAccessibilityFocused(false);
      localAccessibilityNodeInfoCompat1.addAction(64);
      break;
      label379:
      bool = false;
      break label216;
      label384:
      if (localAccessibilityNodeInfoCompat1.isFocusable()) {
        localAccessibilityNodeInfoCompat1.addAction(1);
      }
    }
    localAccessibilityNodeInfoCompat2.recycle();
    label406:
    this.mTempScreenRect.offset(this.mTempGlobalRect[0] - this.mHost.getScrollX(), this.mTempGlobalRect[1] - this.mHost.getScrollY());
    label441:
    if (this.mHost.getLocalVisibleRect(this.mTempVisibleRect))
    {
      this.mTempVisibleRect.offset(this.mTempGlobalRect[0] - this.mHost.getScrollX(), this.mTempGlobalRect[1] - this.mHost.getScrollY());
      this.mTempScreenRect.intersect(this.mTempVisibleRect);
      localAccessibilityNodeInfoCompat1.setBoundsInScreen(this.mTempScreenRect);
      if (isVisibleToUser(this.mTempScreenRect)) {
        localAccessibilityNodeInfoCompat1.setVisibleToUser(true);
      }
    }
    return localAccessibilityNodeInfoCompat1;
  }
  
  @NonNull
  private AccessibilityNodeInfoCompat createNodeForHost()
  {
    AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(this.mHost);
    ViewCompat.onInitializeAccessibilityNodeInfo(this.mHost, localAccessibilityNodeInfoCompat);
    ArrayList localArrayList = new ArrayList();
    getVisibleVirtualViews(localArrayList);
    if ((localAccessibilityNodeInfoCompat.getChildCount() > 0) && (localArrayList.size() > 0)) {
      throw new RuntimeException("Views cannot have both real and virtual children");
    }
    int i = 0;
    int j = localArrayList.size();
    while (i < j)
    {
      localAccessibilityNodeInfoCompat.addChild(this.mHost, ((Integer)localArrayList.get(i)).intValue());
      i += 1;
    }
    return localAccessibilityNodeInfoCompat;
  }
  
  private SparseArrayCompat<AccessibilityNodeInfoCompat> getAllNodes()
  {
    ArrayList localArrayList = new ArrayList();
    getVisibleVirtualViews(localArrayList);
    SparseArrayCompat localSparseArrayCompat = new SparseArrayCompat();
    int i = 0;
    while (i < localArrayList.size())
    {
      localSparseArrayCompat.put(i, createNodeForChild(i));
      i += 1;
    }
    return localSparseArrayCompat;
  }
  
  private void getBoundsInParent(int paramInt, Rect paramRect)
  {
    obtainAccessibilityNodeInfo(paramInt).getBoundsInParent(paramRect);
  }
  
  private static Rect guessPreviouslyFocusedRect(@NonNull View paramView, int paramInt, @NonNull Rect paramRect)
  {
    int i = paramView.getWidth();
    int j = paramView.getHeight();
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    case 17: 
      paramRect.set(i, 0, i, j);
      return paramRect;
    case 33: 
      paramRect.set(0, j, i, j);
      return paramRect;
    case 66: 
      paramRect.set(-1, 0, -1, j);
      return paramRect;
    }
    paramRect.set(0, -1, i, -1);
    return paramRect;
  }
  
  private boolean isVisibleToUser(Rect paramRect)
  {
    if ((paramRect == null) || (paramRect.isEmpty())) {}
    label67:
    do
    {
      do
      {
        return false;
      } while (this.mHost.getWindowVisibility() != 0);
      for (paramRect = this.mHost.getParent();; paramRect = paramRect.getParent())
      {
        if (!(paramRect instanceof View)) {
          break label67;
        }
        paramRect = (View)paramRect;
        if ((ViewCompat.getAlpha(paramRect) <= 0.0F) || (paramRect.getVisibility() != 0)) {
          break;
        }
      }
    } while (paramRect == null);
    return true;
  }
  
  private static int keyToDirection(int paramInt)
  {
    switch (paramInt)
    {
    case 20: 
    default: 
      return 130;
    case 21: 
      return 17;
    case 19: 
      return 33;
    }
    return 66;
  }
  
  private boolean moveFocus(int paramInt, @Nullable Rect paramRect)
  {
    SparseArrayCompat localSparseArrayCompat = getAllNodes();
    int i = this.mKeyboardFocusedVirtualViewId;
    if (i == Integer.MIN_VALUE) {}
    for (AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = null;; localAccessibilityNodeInfoCompat = (AccessibilityNodeInfoCompat)localSparseArrayCompat.get(i)) {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("direction must be one of {FOCUS_FORWARD, FOCUS_BACKWARD, FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
      }
    }
    boolean bool;
    if (ViewCompat.getLayoutDirection(this.mHost) == 1)
    {
      bool = true;
      paramRect = (AccessibilityNodeInfoCompat)FocusStrategy.findNextFocusInRelativeDirection(localSparseArrayCompat, SPARSE_VALUES_ADAPTER, NODE_ADAPTER, localAccessibilityNodeInfoCompat, paramInt, bool, false);
      if (paramRect != null) {
        break label237;
      }
    }
    label237:
    for (paramInt = Integer.MIN_VALUE;; paramInt = localSparseArrayCompat.keyAt(localSparseArrayCompat.indexOfValue(paramRect)))
    {
      return requestKeyboardFocusForVirtualView(paramInt);
      bool = false;
      break;
      Rect localRect = new Rect();
      if (this.mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
        getBoundsInParent(this.mKeyboardFocusedVirtualViewId, localRect);
      }
      for (;;)
      {
        paramRect = (AccessibilityNodeInfoCompat)FocusStrategy.findNextFocusInAbsoluteDirection(localSparseArrayCompat, SPARSE_VALUES_ADAPTER, NODE_ADAPTER, localAccessibilityNodeInfoCompat, localRect, paramInt);
        break;
        if (paramRect != null) {
          localRect.set(paramRect);
        } else {
          guessPreviouslyFocusedRect(this.mHost, paramInt, localRect);
        }
      }
    }
  }
  
  private boolean performActionForChild(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    switch (paramInt2)
    {
    default: 
      return onPerformActionForVirtualView(paramInt1, paramInt2, paramBundle);
    case 64: 
      return requestAccessibilityFocus(paramInt1);
    case 128: 
      return clearAccessibilityFocus(paramInt1);
    case 1: 
      return requestKeyboardFocusForVirtualView(paramInt1);
    }
    return clearKeyboardFocusForVirtualView(paramInt1);
  }
  
  private boolean performActionForHost(int paramInt, Bundle paramBundle)
  {
    return ViewCompat.performAccessibilityAction(this.mHost, paramInt, paramBundle);
  }
  
  private boolean requestAccessibilityFocus(int paramInt)
  {
    if ((!this.mManager.isEnabled()) || (!AccessibilityManagerCompat.isTouchExplorationEnabled(this.mManager))) {}
    while (this.mAccessibilityFocusedVirtualViewId == paramInt) {
      return false;
    }
    if (this.mAccessibilityFocusedVirtualViewId != Integer.MIN_VALUE) {
      clearAccessibilityFocus(this.mAccessibilityFocusedVirtualViewId);
    }
    this.mAccessibilityFocusedVirtualViewId = paramInt;
    this.mHost.invalidate();
    sendEventForVirtualView(paramInt, 32768);
    return true;
  }
  
  private void updateHoveredVirtualView(int paramInt)
  {
    if (this.mHoveredVirtualViewId == paramInt) {
      return;
    }
    int i = this.mHoveredVirtualViewId;
    this.mHoveredVirtualViewId = paramInt;
    sendEventForVirtualView(paramInt, 128);
    sendEventForVirtualView(i, 256);
  }
  
  public final boolean clearKeyboardFocusForVirtualView(int paramInt)
  {
    if (this.mKeyboardFocusedVirtualViewId != paramInt) {
      return false;
    }
    this.mKeyboardFocusedVirtualViewId = Integer.MIN_VALUE;
    onVirtualViewKeyboardFocusChanged(paramInt, false);
    sendEventForVirtualView(paramInt, 8);
    return true;
  }
  
  public final boolean dispatchHoverEvent(@NonNull MotionEvent paramMotionEvent)
  {
    boolean bool = true;
    if ((!this.mManager.isEnabled()) || (!AccessibilityManagerCompat.isTouchExplorationEnabled(this.mManager))) {}
    do
    {
      return false;
      switch (paramMotionEvent.getAction())
      {
      case 8: 
      default: 
        return false;
      case 7: 
      case 9: 
        int i = getVirtualViewAt(paramMotionEvent.getX(), paramMotionEvent.getY());
        updateHoveredVirtualView(i);
        if (i != Integer.MIN_VALUE) {}
        for (;;)
        {
          return bool;
          bool = false;
        }
      }
    } while (this.mAccessibilityFocusedVirtualViewId == Integer.MIN_VALUE);
    updateHoveredVirtualView(Integer.MIN_VALUE);
    return true;
  }
  
  public final boolean dispatchKeyEvent(@NonNull KeyEvent paramKeyEvent)
  {
    boolean bool3 = false;
    boolean bool1 = false;
    boolean bool2 = bool1;
    int i;
    if (paramKeyEvent.getAction() != 1)
    {
      i = paramKeyEvent.getKeyCode();
      switch (i)
      {
      default: 
        bool2 = bool1;
      }
    }
    do
    {
      do
      {
        do
        {
          do
          {
            return bool2;
            bool2 = bool1;
          } while (!KeyEventCompat.hasNoModifiers(paramKeyEvent));
          int j = keyToDirection(i);
          int k = paramKeyEvent.getRepeatCount();
          i = 0;
          bool1 = bool3;
          for (;;)
          {
            bool2 = bool1;
            if (i >= k + 1) {
              break;
            }
            bool2 = bool1;
            if (!moveFocus(j, null)) {
              break;
            }
            bool1 = true;
            i += 1;
          }
          bool2 = bool1;
        } while (!KeyEventCompat.hasNoModifiers(paramKeyEvent));
        bool2 = bool1;
      } while (paramKeyEvent.getRepeatCount() != 0);
      clickKeyboardFocusedVirtualView();
      return true;
      if (KeyEventCompat.hasNoModifiers(paramKeyEvent)) {
        return moveFocus(2, null);
      }
      bool2 = bool1;
    } while (!KeyEventCompat.hasModifiers(paramKeyEvent, 1));
    return moveFocus(1, null);
  }
  
  public final int getAccessibilityFocusedVirtualViewId()
  {
    return this.mAccessibilityFocusedVirtualViewId;
  }
  
  public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView)
  {
    if (this.mNodeProvider == null) {
      this.mNodeProvider = new MyNodeProvider();
    }
    return this.mNodeProvider;
  }
  
  @Deprecated
  public int getFocusedVirtualView()
  {
    return getAccessibilityFocusedVirtualViewId();
  }
  
  public final int getKeyboardFocusedVirtualViewId()
  {
    return this.mKeyboardFocusedVirtualViewId;
  }
  
  protected abstract int getVirtualViewAt(float paramFloat1, float paramFloat2);
  
  protected abstract void getVisibleVirtualViews(List<Integer> paramList);
  
  public final void invalidateRoot()
  {
    invalidateVirtualView(-1, 1);
  }
  
  public final void invalidateVirtualView(int paramInt)
  {
    invalidateVirtualView(paramInt, 0);
  }
  
  public final void invalidateVirtualView(int paramInt1, int paramInt2)
  {
    if ((paramInt1 != Integer.MIN_VALUE) && (this.mManager.isEnabled()))
    {
      ViewParent localViewParent = this.mHost.getParent();
      if (localViewParent != null)
      {
        AccessibilityEvent localAccessibilityEvent = createEvent(paramInt1, 2048);
        AccessibilityEventCompat.setContentChangeTypes(localAccessibilityEvent, paramInt2);
        ViewParentCompat.requestSendAccessibilityEvent(localViewParent, this.mHost, localAccessibilityEvent);
      }
    }
  }
  
  @NonNull
  AccessibilityNodeInfoCompat obtainAccessibilityNodeInfo(int paramInt)
  {
    if (paramInt == -1) {
      return createNodeForHost();
    }
    return createNodeForChild(paramInt);
  }
  
  public final void onFocusChanged(boolean paramBoolean, int paramInt, @Nullable Rect paramRect)
  {
    if (this.mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
      clearKeyboardFocusForVirtualView(this.mKeyboardFocusedVirtualViewId);
    }
    if (paramBoolean) {
      moveFocus(paramInt, paramRect);
    }
  }
  
  public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
    onPopulateEventForHost(paramAccessibilityEvent);
  }
  
  public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
    onPopulateNodeForHost(paramAccessibilityNodeInfoCompat);
  }
  
  protected abstract boolean onPerformActionForVirtualView(int paramInt1, int paramInt2, Bundle paramBundle);
  
  protected void onPopulateEventForHost(AccessibilityEvent paramAccessibilityEvent) {}
  
  protected void onPopulateEventForVirtualView(int paramInt, AccessibilityEvent paramAccessibilityEvent) {}
  
  protected void onPopulateNodeForHost(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat) {}
  
  protected abstract void onPopulateNodeForVirtualView(int paramInt, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat);
  
  protected void onVirtualViewKeyboardFocusChanged(int paramInt, boolean paramBoolean) {}
  
  boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
  {
    switch (paramInt1)
    {
    default: 
      return performActionForChild(paramInt1, paramInt2, paramBundle);
    }
    return performActionForHost(paramInt2, paramBundle);
  }
  
  public final boolean requestKeyboardFocusForVirtualView(int paramInt)
  {
    if ((!this.mHost.isFocused()) && (!this.mHost.requestFocus())) {}
    while (this.mKeyboardFocusedVirtualViewId == paramInt) {
      return false;
    }
    if (this.mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
      clearKeyboardFocusForVirtualView(this.mKeyboardFocusedVirtualViewId);
    }
    this.mKeyboardFocusedVirtualViewId = paramInt;
    onVirtualViewKeyboardFocusChanged(paramInt, true);
    sendEventForVirtualView(paramInt, 8);
    return true;
  }
  
  public final boolean sendEventForVirtualView(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == Integer.MIN_VALUE) || (!this.mManager.isEnabled())) {}
    ViewParent localViewParent;
    do
    {
      return false;
      localViewParent = this.mHost.getParent();
    } while (localViewParent == null);
    AccessibilityEvent localAccessibilityEvent = createEvent(paramInt1, paramInt2);
    return ViewParentCompat.requestSendAccessibilityEvent(localViewParent, this.mHost, localAccessibilityEvent);
  }
  
  private class MyNodeProvider
    extends AccessibilityNodeProviderCompat
  {
    MyNodeProvider() {}
    
    public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(int paramInt)
    {
      return AccessibilityNodeInfoCompat.obtain(ExploreByTouchHelper.this.obtainAccessibilityNodeInfo(paramInt));
    }
    
    public AccessibilityNodeInfoCompat findFocus(int paramInt)
    {
      if (paramInt == 2) {}
      for (paramInt = ExploreByTouchHelper.this.mAccessibilityFocusedVirtualViewId; paramInt == Integer.MIN_VALUE; paramInt = ExploreByTouchHelper.this.mKeyboardFocusedVirtualViewId) {
        return null;
      }
      return createAccessibilityNodeInfo(paramInt);
    }
    
    public boolean performAction(int paramInt1, int paramInt2, Bundle paramBundle)
    {
      return ExploreByTouchHelper.this.performAction(paramInt1, paramInt2, paramBundle);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/widget/ExploreByTouchHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */