package org.telegram.messenger.support.widget;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;
import java.util.List;

class ChildHelper
{
  private static final boolean DEBUG = false;
  private static final String TAG = "ChildrenHelper";
  final Bucket mBucket;
  final Callback mCallback;
  final List<View> mHiddenViews;
  
  ChildHelper(Callback paramCallback)
  {
    this.mCallback = paramCallback;
    this.mBucket = new Bucket();
    this.mHiddenViews = new ArrayList();
  }
  
  private int getOffset(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = -1;
    }
    for (;;)
    {
      return paramInt;
      int i = this.mCallback.getChildCount();
      int j = paramInt;
      for (;;)
      {
        if (j >= i) {
          break label71;
        }
        int k = paramInt - (j - this.mBucket.countOnesBefore(j));
        if (k == 0) {
          for (;;)
          {
            paramInt = j;
            if (!this.mBucket.get(j)) {
              break;
            }
            j++;
          }
        }
        j += k;
      }
      label71:
      paramInt = -1;
    }
  }
  
  private void hideViewInternal(View paramView)
  {
    this.mHiddenViews.add(paramView);
    this.mCallback.onEnteredHiddenState(paramView);
  }
  
  private boolean unhideViewInternal(View paramView)
  {
    if (this.mHiddenViews.remove(paramView)) {
      this.mCallback.onLeftHiddenState(paramView);
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  void addView(View paramView, int paramInt, boolean paramBoolean)
  {
    if (paramInt < 0) {}
    for (paramInt = this.mCallback.getChildCount();; paramInt = getOffset(paramInt))
    {
      this.mBucket.insert(paramInt, paramBoolean);
      if (paramBoolean) {
        hideViewInternal(paramView);
      }
      this.mCallback.addView(paramView, paramInt);
      return;
    }
  }
  
  void addView(View paramView, boolean paramBoolean)
  {
    addView(paramView, -1, paramBoolean);
  }
  
  void attachViewToParent(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    if (paramInt < 0) {}
    for (paramInt = this.mCallback.getChildCount();; paramInt = getOffset(paramInt))
    {
      this.mBucket.insert(paramInt, paramBoolean);
      if (paramBoolean) {
        hideViewInternal(paramView);
      }
      this.mCallback.attachViewToParent(paramView, paramInt, paramLayoutParams);
      return;
    }
  }
  
  void detachViewFromParent(int paramInt)
  {
    paramInt = getOffset(paramInt);
    this.mBucket.remove(paramInt);
    this.mCallback.detachViewFromParent(paramInt);
  }
  
  View findHiddenNonRemovedView(int paramInt)
  {
    int i = this.mHiddenViews.size();
    int j = 0;
    View localView;
    if (j < i)
    {
      localView = (View)this.mHiddenViews.get(j);
      RecyclerView.ViewHolder localViewHolder = this.mCallback.getChildViewHolder(localView);
      if ((localViewHolder.getLayoutPosition() != paramInt) || (localViewHolder.isInvalid()) || (localViewHolder.isRemoved())) {}
    }
    for (;;)
    {
      return localView;
      j++;
      break;
      localView = null;
    }
  }
  
  View getChildAt(int paramInt)
  {
    paramInt = getOffset(paramInt);
    return this.mCallback.getChildAt(paramInt);
  }
  
  int getChildCount()
  {
    return this.mCallback.getChildCount() - this.mHiddenViews.size();
  }
  
  public View getHiddenChildAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mHiddenViews.size())) {}
    for (View localView = null;; localView = (View)this.mHiddenViews.get(paramInt)) {
      return localView;
    }
  }
  
  protected int getHiddenChildCount()
  {
    return this.mHiddenViews.size();
  }
  
  View getUnfilteredChildAt(int paramInt)
  {
    return this.mCallback.getChildAt(paramInt);
  }
  
  int getUnfilteredChildCount()
  {
    return this.mCallback.getChildCount();
  }
  
  void hide(View paramView)
  {
    int i = this.mCallback.indexOfChild(paramView);
    if (i < 0) {
      throw new IllegalArgumentException("view is not a child, cannot hide " + paramView);
    }
    this.mBucket.set(i);
    hideViewInternal(paramView);
  }
  
  int indexOfChild(View paramView)
  {
    int i = -1;
    int j = this.mCallback.indexOfChild(paramView);
    if (j == -1) {}
    for (;;)
    {
      return i;
      if (!this.mBucket.get(j)) {
        i = j - this.mBucket.countOnesBefore(j);
      }
    }
  }
  
  boolean isHidden(View paramView)
  {
    return this.mHiddenViews.contains(paramView);
  }
  
  void removeAllViewsUnfiltered()
  {
    this.mBucket.reset();
    for (int i = this.mHiddenViews.size() - 1; i >= 0; i--)
    {
      this.mCallback.onLeftHiddenState((View)this.mHiddenViews.get(i));
      this.mHiddenViews.remove(i);
    }
    this.mCallback.removeAllViews();
  }
  
  void removeView(View paramView)
  {
    int i = this.mCallback.indexOfChild(paramView);
    if (i < 0) {}
    for (;;)
    {
      return;
      if (this.mBucket.remove(i)) {
        unhideViewInternal(paramView);
      }
      this.mCallback.removeViewAt(i);
    }
  }
  
  void removeViewAt(int paramInt)
  {
    paramInt = getOffset(paramInt);
    View localView = this.mCallback.getChildAt(paramInt);
    if (localView == null) {}
    for (;;)
    {
      return;
      if (this.mBucket.remove(paramInt)) {
        unhideViewInternal(localView);
      }
      this.mCallback.removeViewAt(paramInt);
    }
  }
  
  boolean removeViewIfHidden(View paramView)
  {
    boolean bool = true;
    int i = this.mCallback.indexOfChild(paramView);
    if (i == -1) {
      if (!unhideViewInternal(paramView)) {}
    }
    for (;;)
    {
      return bool;
      if (this.mBucket.get(i))
      {
        this.mBucket.remove(i);
        if (!unhideViewInternal(paramView)) {}
        this.mCallback.removeViewAt(i);
      }
      else
      {
        bool = false;
      }
    }
  }
  
  public String toString()
  {
    return this.mBucket.toString() + ", hidden list:" + this.mHiddenViews.size();
  }
  
  void unhide(View paramView)
  {
    int i = this.mCallback.indexOfChild(paramView);
    if (i < 0) {
      throw new IllegalArgumentException("view is not a child, cannot hide " + paramView);
    }
    if (!this.mBucket.get(i)) {
      throw new RuntimeException("trying to unhide a view that was not hidden" + paramView);
    }
    this.mBucket.clear(i);
    unhideViewInternal(paramView);
  }
  
  static class Bucket
  {
    static final int BITS_PER_WORD = 64;
    static final long LAST_BIT = Long.MIN_VALUE;
    long mData = 0L;
    Bucket mNext;
    
    private void ensureNext()
    {
      if (this.mNext == null) {
        this.mNext = new Bucket();
      }
    }
    
    void clear(int paramInt)
    {
      if (paramInt >= 64) {
        if (this.mNext != null) {
          this.mNext.clear(paramInt - 64);
        }
      }
      for (;;)
      {
        return;
        this.mData &= (1L << paramInt ^ 0xFFFFFFFFFFFFFFFF);
      }
    }
    
    int countOnesBefore(int paramInt)
    {
      if (this.mNext == null) {
        if (paramInt >= 64) {
          paramInt = Long.bitCount(this.mData);
        }
      }
      for (;;)
      {
        return paramInt;
        paramInt = Long.bitCount(this.mData & (1L << paramInt) - 1L);
        continue;
        if (paramInt < 64) {
          paramInt = Long.bitCount(this.mData & (1L << paramInt) - 1L);
        } else {
          paramInt = this.mNext.countOnesBefore(paramInt - 64) + Long.bitCount(this.mData);
        }
      }
    }
    
    boolean get(int paramInt)
    {
      boolean bool;
      if (paramInt >= 64)
      {
        ensureNext();
        bool = this.mNext.get(paramInt - 64);
      }
      for (;;)
      {
        return bool;
        if ((this.mData & 1L << paramInt) != 0L) {
          bool = true;
        } else {
          bool = false;
        }
      }
    }
    
    void insert(int paramInt, boolean paramBoolean)
    {
      if (paramInt >= 64)
      {
        ensureNext();
        this.mNext.insert(paramInt - 64, paramBoolean);
      }
      label38:
      label111:
      label117:
      for (;;)
      {
        return;
        boolean bool;
        if ((this.mData & 0x800000NUM) != 0L)
        {
          bool = true;
          long l = (1L << paramInt) - 1L;
          this.mData = (this.mData & l | (this.mData & (0xFFFFFFFFFFFFFFFF ^ l)) << 1);
          if (!paramBoolean) {
            break label111;
          }
          set(paramInt);
        }
        for (;;)
        {
          if ((!bool) && (this.mNext == null)) {
            break label117;
          }
          ensureNext();
          this.mNext.insert(0, bool);
          break;
          bool = false;
          break label38;
          clear(paramInt);
        }
      }
    }
    
    boolean remove(int paramInt)
    {
      boolean bool1;
      if (paramInt >= 64)
      {
        ensureNext();
        bool1 = this.mNext.remove(paramInt - 64);
        return bool1;
      }
      long l = 1L << paramInt;
      if ((this.mData & l) != 0L) {}
      for (boolean bool2 = true;; bool2 = false)
      {
        this.mData &= (0xFFFFFFFFFFFFFFFF ^ l);
        l -= 1L;
        this.mData = (this.mData & l | Long.rotateRight(this.mData & (0xFFFFFFFFFFFFFFFF ^ l), 1));
        bool1 = bool2;
        if (this.mNext == null) {
          break;
        }
        if (this.mNext.get(0)) {
          set(63);
        }
        this.mNext.remove(0);
        bool1 = bool2;
        break;
      }
    }
    
    void reset()
    {
      this.mData = 0L;
      if (this.mNext != null) {
        this.mNext.reset();
      }
    }
    
    void set(int paramInt)
    {
      if (paramInt >= 64)
      {
        ensureNext();
        this.mNext.set(paramInt - 64);
      }
      for (;;)
      {
        return;
        this.mData |= 1L << paramInt;
      }
    }
    
    public String toString()
    {
      if (this.mNext == null) {}
      for (String str = Long.toBinaryString(this.mData);; str = this.mNext.toString() + "xx" + Long.toBinaryString(this.mData)) {
        return str;
      }
    }
  }
  
  static abstract interface Callback
  {
    public abstract void addView(View paramView, int paramInt);
    
    public abstract void attachViewToParent(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams);
    
    public abstract void detachViewFromParent(int paramInt);
    
    public abstract View getChildAt(int paramInt);
    
    public abstract int getChildCount();
    
    public abstract RecyclerView.ViewHolder getChildViewHolder(View paramView);
    
    public abstract int indexOfChild(View paramView);
    
    public abstract void onEnteredHiddenState(View paramView);
    
    public abstract void onLeftHiddenState(View paramView);
    
    public abstract void removeAllViews();
    
    public abstract void removeViewAt(int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/ChildHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */