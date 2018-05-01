package org.telegram.messenger.support.util;

import android.util.SparseArray;
import java.lang.reflect.Array;

class TileList<T>
{
  Tile<T> mLastAccessedTile;
  final int mTileSize;
  private final SparseArray<Tile<T>> mTiles = new SparseArray(10);
  
  public TileList(int paramInt)
  {
    this.mTileSize = paramInt;
  }
  
  public Tile<T> addOrReplace(Tile<T> paramTile)
  {
    int i = this.mTiles.indexOfKey(paramTile.mStartPosition);
    Object localObject;
    if (i < 0)
    {
      this.mTiles.put(paramTile.mStartPosition, paramTile);
      localObject = null;
    }
    for (;;)
    {
      return (Tile<T>)localObject;
      Tile localTile = (Tile)this.mTiles.valueAt(i);
      this.mTiles.setValueAt(i, paramTile);
      localObject = localTile;
      if (this.mLastAccessedTile == localTile)
      {
        this.mLastAccessedTile = paramTile;
        localObject = localTile;
      }
    }
  }
  
  public void clear()
  {
    this.mTiles.clear();
  }
  
  public Tile<T> getAtIndex(int paramInt)
  {
    return (Tile)this.mTiles.valueAt(paramInt);
  }
  
  public T getItemAt(int paramInt)
  {
    int i;
    if ((this.mLastAccessedTile == null) || (!this.mLastAccessedTile.containsPosition(paramInt)))
    {
      i = this.mTileSize;
      i = this.mTiles.indexOfKey(paramInt - paramInt % i);
      if (i >= 0) {}
    }
    for (Object localObject = null;; localObject = this.mLastAccessedTile.getByPosition(paramInt))
    {
      return (T)localObject;
      this.mLastAccessedTile = ((Tile)this.mTiles.valueAt(i));
    }
  }
  
  public Tile<T> removeAtPos(int paramInt)
  {
    Tile localTile = (Tile)this.mTiles.get(paramInt);
    if (this.mLastAccessedTile == localTile) {
      this.mLastAccessedTile = null;
    }
    this.mTiles.delete(paramInt);
    return localTile;
  }
  
  public int size()
  {
    return this.mTiles.size();
  }
  
  public static class Tile<T>
  {
    public int mItemCount;
    public final T[] mItems;
    Tile<T> mNext;
    public int mStartPosition;
    
    public Tile(Class<T> paramClass, int paramInt)
    {
      this.mItems = ((Object[])Array.newInstance(paramClass, paramInt));
    }
    
    boolean containsPosition(int paramInt)
    {
      if ((this.mStartPosition <= paramInt) && (paramInt < this.mStartPosition + this.mItemCount)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    T getByPosition(int paramInt)
    {
      return (T)this.mItems[(paramInt - this.mStartPosition)];
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/util/TileList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */