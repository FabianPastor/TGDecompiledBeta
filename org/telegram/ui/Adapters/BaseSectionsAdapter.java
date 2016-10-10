package org.telegram.ui.Adapters;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseSectionsAdapter
  extends BaseFragmentAdapter
{
  private int count;
  private SparseArray<Integer> sectionCache;
  private int sectionCount;
  private SparseArray<Integer> sectionCountCache;
  private SparseArray<Integer> sectionPositionCache;
  
  public BaseSectionsAdapter()
  {
    cleanupCache();
  }
  
  private void cleanupCache()
  {
    this.sectionCache = new SparseArray();
    this.sectionPositionCache = new SparseArray();
    this.sectionCountCache = new SparseArray();
    this.count = -1;
    this.sectionCount = -1;
  }
  
  private int internalGetCountForSection(int paramInt)
  {
    Integer localInteger = (Integer)this.sectionCountCache.get(paramInt);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    int i = getCountForSection(paramInt);
    this.sectionCountCache.put(paramInt, Integer.valueOf(i));
    return i;
  }
  
  private int internalGetSectionCount()
  {
    if (this.sectionCount >= 0) {
      return this.sectionCount;
    }
    this.sectionCount = getSectionCount();
    return this.sectionCount;
  }
  
  public boolean areAllItemsEnabled()
  {
    return false;
  }
  
  public final int getCount()
  {
    if (this.count >= 0) {
      return this.count;
    }
    this.count = 0;
    int i = 0;
    while (i < internalGetSectionCount())
    {
      this.count += internalGetCountForSection(i);
      i += 1;
    }
    return this.count;
  }
  
  public abstract int getCountForSection(int paramInt);
  
  public final Object getItem(int paramInt)
  {
    return getItem(getSectionForPosition(paramInt), getPositionInSectionForPosition(paramInt));
  }
  
  public abstract Object getItem(int paramInt1, int paramInt2);
  
  public final long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public abstract View getItemView(int paramInt1, int paramInt2, View paramView, ViewGroup paramViewGroup);
  
  public final int getItemViewType(int paramInt)
  {
    return getItemViewType(getSectionForPosition(paramInt), getPositionInSectionForPosition(paramInt));
  }
  
  public abstract int getItemViewType(int paramInt1, int paramInt2);
  
  public int getPositionInSectionForPosition(int paramInt)
  {
    Integer localInteger = (Integer)this.sectionPositionCache.get(paramInt);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    int j = 0;
    int i = 0;
    while (i < internalGetSectionCount())
    {
      int k = j + internalGetCountForSection(i);
      if ((paramInt >= j) && (paramInt < k))
      {
        i = paramInt - j;
        this.sectionPositionCache.put(paramInt, Integer.valueOf(i));
        return i;
      }
      j = k;
      i += 1;
    }
    return -1;
  }
  
  public abstract int getSectionCount();
  
  public final int getSectionForPosition(int paramInt)
  {
    Integer localInteger = (Integer)this.sectionCache.get(paramInt);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    int j = 0;
    int i = 0;
    while (i < internalGetSectionCount())
    {
      int k = j + internalGetCountForSection(i);
      if ((paramInt >= j) && (paramInt < k))
      {
        this.sectionCache.put(paramInt, Integer.valueOf(i));
        return i;
      }
      j = k;
      i += 1;
    }
    return -1;
  }
  
  public abstract View getSectionHeaderView(int paramInt, View paramView, ViewGroup paramViewGroup);
  
  public final View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    return getItemView(getSectionForPosition(paramInt), getPositionInSectionForPosition(paramInt), paramView, paramViewGroup);
  }
  
  public boolean isEnabled(int paramInt)
  {
    return isRowEnabled(getSectionForPosition(paramInt), getPositionInSectionForPosition(paramInt));
  }
  
  public abstract boolean isRowEnabled(int paramInt1, int paramInt2);
  
  public void notifyDataSetChanged()
  {
    cleanupCache();
    super.notifyDataSetChanged();
  }
  
  public void notifyDataSetInvalidated()
  {
    cleanupCache();
    super.notifyDataSetInvalidated();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/BaseSectionsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */