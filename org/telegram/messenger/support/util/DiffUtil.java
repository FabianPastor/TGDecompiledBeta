package org.telegram.messenger.support.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;

public class DiffUtil
{
  private static final Comparator<Snake> SNAKE_COMPARATOR = new Comparator()
  {
    public int compare(DiffUtil.Snake paramAnonymousSnake1, DiffUtil.Snake paramAnonymousSnake2)
    {
      int i = paramAnonymousSnake1.x - paramAnonymousSnake2.x;
      int j = i;
      if (i == 0) {
        j = paramAnonymousSnake1.y - paramAnonymousSnake2.y;
      }
      return j;
    }
  };
  
  public static DiffResult calculateDiff(Callback paramCallback)
  {
    return calculateDiff(paramCallback, true);
  }
  
  public static DiffResult calculateDiff(Callback paramCallback, boolean paramBoolean)
  {
    int i = paramCallback.getOldListSize();
    int j = paramCallback.getNewListSize();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    localArrayList2.add(new Range(0, i, 0, j));
    j = i + j + Math.abs(i - j);
    int[] arrayOfInt1 = new int[j * 2];
    int[] arrayOfInt2 = new int[j * 2];
    ArrayList localArrayList3 = new ArrayList();
    while (!localArrayList2.isEmpty())
    {
      Range localRange1 = (Range)localArrayList2.remove(localArrayList2.size() - 1);
      Snake localSnake = diffPartial(paramCallback, localRange1.oldListStart, localRange1.oldListEnd, localRange1.newListStart, localRange1.newListEnd, arrayOfInt1, arrayOfInt2, j);
      if (localSnake != null)
      {
        if (localSnake.size > 0) {
          localArrayList1.add(localSnake);
        }
        localSnake.x += localRange1.oldListStart;
        localSnake.y += localRange1.newListStart;
        Range localRange2;
        if (localArrayList3.isEmpty())
        {
          localRange2 = new Range();
          label217:
          localRange2.oldListStart = localRange1.oldListStart;
          localRange2.newListStart = localRange1.newListStart;
          if (!localSnake.reverse) {
            break label362;
          }
          localRange2.oldListEnd = localSnake.x;
          localRange2.newListEnd = localSnake.y;
          label265:
          localArrayList2.add(localRange2);
          if (!localSnake.reverse) {
            break label457;
          }
          if (!localSnake.removal) {
            break label420;
          }
          localRange1.oldListStart = (localSnake.x + localSnake.size + 1);
          localRange1.newListStart = (localSnake.y + localSnake.size);
        }
        for (;;)
        {
          localArrayList2.add(localRange1);
          break;
          localRange2 = (Range)localArrayList3.remove(localArrayList3.size() - 1);
          break label217;
          label362:
          if (localSnake.removal)
          {
            localRange2.oldListEnd = (localSnake.x - 1);
            localRange2.newListEnd = localSnake.y;
            break label265;
          }
          localRange2.oldListEnd = localSnake.x;
          localRange2.newListEnd = (localSnake.y - 1);
          break label265;
          label420:
          localRange1.oldListStart = (localSnake.x + localSnake.size);
          localRange1.newListStart = (localSnake.y + localSnake.size + 1);
          continue;
          label457:
          localRange1.oldListStart = (localSnake.x + localSnake.size);
          localRange1.newListStart = (localSnake.y + localSnake.size);
        }
      }
      localArrayList3.add(localRange1);
    }
    Collections.sort(localArrayList1, SNAKE_COMPARATOR);
    return new DiffResult(paramCallback, localArrayList1, arrayOfInt1, arrayOfInt2, paramBoolean);
  }
  
  private static Snake diffPartial(Callback paramCallback, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt5)
  {
    int i = paramInt2 - paramInt1;
    int j = paramInt4 - paramInt3;
    if ((paramInt2 - paramInt1 < 1) || (paramInt4 - paramInt3 < 1))
    {
      paramCallback = null;
      return paramCallback;
    }
    int k = i - j;
    int m = (i + j + 1) / 2;
    Arrays.fill(paramArrayOfInt1, paramInt5 - m - 1, paramInt5 + m + 1, 0);
    Arrays.fill(paramArrayOfInt2, paramInt5 - m - 1 + k, paramInt5 + m + 1 + k, i);
    if (k % 2 != 0) {
      paramInt4 = 1;
    }
    label375:
    label639:
    for (int n = 0;; n++)
    {
      if (n > m) {
        break label645;
      }
      boolean bool;
      int i2;
      for (int i1 = -n;; i1 += 2)
      {
        if (i1 > n) {
          break label375;
        }
        if ((i1 == -n) || ((i1 != n) && (paramArrayOfInt1[(paramInt5 + i1 - 1)] < paramArrayOfInt1[(paramInt5 + i1 + 1)]))) {
          paramInt2 = paramArrayOfInt1[(paramInt5 + i1 + 1)];
        }
        for (bool = false;; bool = true)
        {
          for (i2 = paramInt2 - i1; (paramInt2 < i) && (i2 < j) && (paramCallback.areItemsTheSame(paramInt1 + paramInt2, paramInt3 + i2)); i2++) {
            paramInt2++;
          }
          paramInt4 = 0;
          break;
          paramInt2 = paramArrayOfInt1[(paramInt5 + i1 - 1)] + 1;
        }
        paramArrayOfInt1[(paramInt5 + i1)] = paramInt2;
        if ((paramInt4 != 0) && (i1 >= k - n + 1) && (i1 <= k + n - 1) && (paramArrayOfInt1[(paramInt5 + i1)] >= paramArrayOfInt2[(paramInt5 + i1)]))
        {
          paramCallback = new Snake();
          paramCallback.x = paramArrayOfInt2[(paramInt5 + i1)];
          paramCallback.y = (paramCallback.x - i1);
          paramCallback.size = (paramArrayOfInt1[(paramInt5 + i1)] - paramArrayOfInt2[(paramInt5 + i1)]);
          paramCallback.removal = bool;
          paramCallback.reverse = false;
          break;
        }
      }
      for (i1 = -n;; i1 += 2)
      {
        if (i1 > n) {
          break label639;
        }
        int i3 = i1 + k;
        if ((i3 == n + k) || ((i3 != -n + k) && (paramArrayOfInt2[(paramInt5 + i3 - 1)] < paramArrayOfInt2[(paramInt5 + i3 + 1)]))) {
          paramInt2 = paramArrayOfInt2[(paramInt5 + i3 - 1)];
        }
        for (bool = false;; bool = true)
        {
          for (i2 = paramInt2 - i3; (paramInt2 > 0) && (i2 > 0) && (paramCallback.areItemsTheSame(paramInt1 + paramInt2 - 1, paramInt3 + i2 - 1)); i2--) {
            paramInt2--;
          }
          paramInt2 = paramArrayOfInt2[(paramInt5 + i3 + 1)] - 1;
        }
        paramArrayOfInt2[(paramInt5 + i3)] = paramInt2;
        if ((paramInt4 == 0) && (i1 + k >= -n) && (i1 + k <= n) && (paramArrayOfInt1[(paramInt5 + i3)] >= paramArrayOfInt2[(paramInt5 + i3)]))
        {
          paramCallback = new Snake();
          paramCallback.x = paramArrayOfInt2[(paramInt5 + i3)];
          paramCallback.y = (paramCallback.x - i3);
          paramCallback.size = (paramArrayOfInt1[(paramInt5 + i3)] - paramArrayOfInt2[(paramInt5 + i3)]);
          paramCallback.removal = bool;
          paramCallback.reverse = true;
          break;
        }
      }
    }
    label645:
    throw new IllegalStateException("DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation.");
  }
  
  public static abstract class Callback
  {
    public abstract boolean areContentsTheSame(int paramInt1, int paramInt2);
    
    public abstract boolean areItemsTheSame(int paramInt1, int paramInt2);
    
    public Object getChangePayload(int paramInt1, int paramInt2)
    {
      return null;
    }
    
    public abstract int getNewListSize();
    
    public abstract int getOldListSize();
  }
  
  public static class DiffResult
  {
    private static final int FLAG_CHANGED = 2;
    private static final int FLAG_IGNORE = 16;
    private static final int FLAG_MASK = 31;
    private static final int FLAG_MOVED_CHANGED = 4;
    private static final int FLAG_MOVED_NOT_CHANGED = 8;
    private static final int FLAG_NOT_CHANGED = 1;
    private static final int FLAG_OFFSET = 5;
    private final DiffUtil.Callback mCallback;
    private final boolean mDetectMoves;
    private final int[] mNewItemStatuses;
    private final int mNewListSize;
    private final int[] mOldItemStatuses;
    private final int mOldListSize;
    private final List<DiffUtil.Snake> mSnakes;
    
    DiffResult(DiffUtil.Callback paramCallback, List<DiffUtil.Snake> paramList, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
    {
      this.mSnakes = paramList;
      this.mOldItemStatuses = paramArrayOfInt1;
      this.mNewItemStatuses = paramArrayOfInt2;
      Arrays.fill(this.mOldItemStatuses, 0);
      Arrays.fill(this.mNewItemStatuses, 0);
      this.mCallback = paramCallback;
      this.mOldListSize = paramCallback.getOldListSize();
      this.mNewListSize = paramCallback.getNewListSize();
      this.mDetectMoves = paramBoolean;
      addRootSnake();
      findMatchingItems();
    }
    
    private void addRootSnake()
    {
      if (this.mSnakes.isEmpty()) {}
      for (DiffUtil.Snake localSnake = null;; localSnake = (DiffUtil.Snake)this.mSnakes.get(0))
      {
        if ((localSnake == null) || (localSnake.x != 0) || (localSnake.y != 0))
        {
          localSnake = new DiffUtil.Snake();
          localSnake.x = 0;
          localSnake.y = 0;
          localSnake.removal = false;
          localSnake.size = 0;
          localSnake.reverse = false;
          this.mSnakes.add(0, localSnake);
        }
        return;
      }
    }
    
    private void dispatchAdditions(List<DiffUtil.PostponedUpdate> paramList, ListUpdateCallback paramListUpdateCallback, int paramInt1, int paramInt2, int paramInt3)
    {
      if (!this.mDetectMoves) {
        paramListUpdateCallback.onInserted(paramInt1, paramInt2);
      }
      do
      {
        return;
        paramInt2--;
      } while (paramInt2 < 0);
      int i = this.mNewItemStatuses[(paramInt3 + paramInt2)] & 0x1F;
      Iterator localIterator;
      switch (i)
      {
      default: 
        throw new IllegalStateException("unknown flag for pos " + (paramInt3 + paramInt2) + " " + Long.toBinaryString(i));
      case 0: 
        paramListUpdateCallback.onInserted(paramInt1, 1);
        localIterator = paramList.iterator();
      case 4: 
      case 8: 
        while (localIterator.hasNext())
        {
          DiffUtil.PostponedUpdate localPostponedUpdate = (DiffUtil.PostponedUpdate)localIterator.next();
          localPostponedUpdate.currentPos += 1;
          continue;
          int j = this.mNewItemStatuses[(paramInt3 + paramInt2)] >> 5;
          paramListUpdateCallback.onMoved(removePostponedUpdate(paramList, j, true).currentPos, paramInt1);
          if (i == 4) {
            paramListUpdateCallback.onChanged(paramInt1, 1, this.mCallback.getChangePayload(j, paramInt3 + paramInt2));
          }
        }
      }
      for (;;)
      {
        paramInt2--;
        break;
        paramList.add(new DiffUtil.PostponedUpdate(paramInt3 + paramInt2, paramInt1, false));
      }
    }
    
    private void dispatchRemovals(List<DiffUtil.PostponedUpdate> paramList, ListUpdateCallback paramListUpdateCallback, int paramInt1, int paramInt2, int paramInt3)
    {
      if (!this.mDetectMoves) {
        paramListUpdateCallback.onRemoved(paramInt1, paramInt2);
      }
      do
      {
        return;
        paramInt2--;
      } while (paramInt2 < 0);
      int i = this.mOldItemStatuses[(paramInt3 + paramInt2)] & 0x1F;
      Object localObject;
      switch (i)
      {
      default: 
        throw new IllegalStateException("unknown flag for pos " + (paramInt3 + paramInt2) + " " + Long.toBinaryString(i));
      case 0: 
        paramListUpdateCallback.onRemoved(paramInt1 + paramInt2, 1);
        localObject = paramList.iterator();
      case 4: 
      case 8: 
        while (((Iterator)localObject).hasNext())
        {
          DiffUtil.PostponedUpdate localPostponedUpdate = (DiffUtil.PostponedUpdate)((Iterator)localObject).next();
          localPostponedUpdate.currentPos -= 1;
          continue;
          int j = this.mOldItemStatuses[(paramInt3 + paramInt2)] >> 5;
          localObject = removePostponedUpdate(paramList, j, false);
          paramListUpdateCallback.onMoved(paramInt1 + paramInt2, ((DiffUtil.PostponedUpdate)localObject).currentPos - 1);
          if (i == 4) {
            paramListUpdateCallback.onChanged(((DiffUtil.PostponedUpdate)localObject).currentPos - 1, 1, this.mCallback.getChangePayload(paramInt3 + paramInt2, j));
          }
        }
      }
      for (;;)
      {
        paramInt2--;
        break;
        paramList.add(new DiffUtil.PostponedUpdate(paramInt3 + paramInt2, paramInt1 + paramInt2, true));
      }
    }
    
    private void findAddition(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.mOldItemStatuses[(paramInt1 - 1)] != 0) {}
      for (;;)
      {
        return;
        findMatchingItem(paramInt1, paramInt2, paramInt3, false);
      }
    }
    
    private boolean findMatchingItem(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      int i;
      int j;
      int k;
      DiffUtil.Snake localSnake;
      int n;
      int i1;
      if (paramBoolean)
      {
        i = paramInt2 - 1;
        j = paramInt1;
        k = paramInt2 - 1;
        paramInt2 = j;
        if (paramInt3 < 0) {
          break label281;
        }
        localSnake = (DiffUtil.Snake)this.mSnakes.get(paramInt3);
        int m = localSnake.x;
        j = localSnake.size;
        n = localSnake.y;
        i1 = localSnake.size;
        if (!paramBoolean) {
          break label175;
        }
        paramInt2--;
        label76:
        if (paramInt2 < m + j) {
          break label262;
        }
        if (!this.mCallback.areItemsTheSame(paramInt2, i)) {
          break label169;
        }
        if (!this.mCallback.areContentsTheSame(paramInt2, i)) {
          break label164;
        }
        paramInt1 = 8;
        label114:
        this.mNewItemStatuses[i] = (paramInt2 << 5 | 0x10);
        this.mOldItemStatuses[paramInt2] = (i << 5 | paramInt1);
        paramBoolean = true;
      }
      for (;;)
      {
        return paramBoolean;
        i = paramInt1 - 1;
        j = paramInt1 - 1;
        k = paramInt2;
        paramInt2 = j;
        break;
        label164:
        paramInt1 = 4;
        break label114;
        label169:
        paramInt2--;
        break label76;
        label175:
        for (paramInt2 = k - 1;; paramInt2--)
        {
          if (paramInt2 < n + i1) {
            break label262;
          }
          if (this.mCallback.areItemsTheSame(i, paramInt2))
          {
            if (this.mCallback.areContentsTheSame(i, paramInt2)) {}
            for (paramInt3 = 8;; paramInt3 = 4)
            {
              this.mOldItemStatuses[(paramInt1 - 1)] = (paramInt2 << 5 | 0x10);
              this.mNewItemStatuses[paramInt2] = (paramInt1 - 1 << 5 | paramInt3);
              paramBoolean = true;
              break;
            }
          }
        }
        label262:
        paramInt2 = localSnake.x;
        k = localSnake.y;
        paramInt3--;
        break;
        label281:
        paramBoolean = false;
      }
    }
    
    private void findMatchingItems()
    {
      int i = this.mOldListSize;
      int j = this.mNewListSize;
      for (int k = this.mSnakes.size() - 1; k >= 0; k--)
      {
        DiffUtil.Snake localSnake = (DiffUtil.Snake)this.mSnakes.get(k);
        int m = localSnake.x;
        int n = localSnake.size;
        int i1 = localSnake.y;
        int i2 = localSnake.size;
        int i3;
        if (this.mDetectMoves)
        {
          for (;;)
          {
            i3 = j;
            if (i <= m + n) {
              break;
            }
            findAddition(i, j, k);
            i--;
          }
          while (i3 > i1 + i2)
          {
            findRemoval(i, i3, k);
            i3--;
          }
        }
        j = 0;
        if (j < localSnake.size)
        {
          i3 = localSnake.x + j;
          i1 = localSnake.y + j;
          if (this.mCallback.areContentsTheSame(i3, i1)) {}
          for (i = 1;; i = 2)
          {
            this.mOldItemStatuses[i3] = (i1 << 5 | i);
            this.mNewItemStatuses[i1] = (i3 << 5 | i);
            j++;
            break;
          }
        }
        i = localSnake.x;
        j = localSnake.y;
      }
    }
    
    private void findRemoval(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.mNewItemStatuses[(paramInt2 - 1)] != 0) {}
      for (;;)
      {
        return;
        findMatchingItem(paramInt1, paramInt2, paramInt3, true);
      }
    }
    
    private static DiffUtil.PostponedUpdate removePostponedUpdate(List<DiffUtil.PostponedUpdate> paramList, int paramInt, boolean paramBoolean)
    {
      for (int i = paramList.size() - 1; i >= 0; i--)
      {
        DiffUtil.PostponedUpdate localPostponedUpdate1 = (DiffUtil.PostponedUpdate)paramList.get(i);
        if ((localPostponedUpdate1.posInOwnerList == paramInt) && (localPostponedUpdate1.removal == paramBoolean))
        {
          paramList.remove(i);
          paramInt = i;
          localPostponedUpdate2 = localPostponedUpdate1;
          if (paramInt >= paramList.size()) {
            break label121;
          }
          localPostponedUpdate2 = (DiffUtil.PostponedUpdate)paramList.get(paramInt);
          int j = localPostponedUpdate2.currentPos;
          if (paramBoolean) {}
          for (i = 1;; i = -1)
          {
            localPostponedUpdate2.currentPos = (i + j);
            paramInt++;
            break;
          }
        }
      }
      DiffUtil.PostponedUpdate localPostponedUpdate2 = null;
      label121:
      return localPostponedUpdate2;
    }
    
    public void dispatchUpdatesTo(ListUpdateCallback paramListUpdateCallback)
    {
      ArrayList localArrayList;
      int i;
      int j;
      if ((paramListUpdateCallback instanceof BatchingListUpdateCallback))
      {
        paramListUpdateCallback = (BatchingListUpdateCallback)paramListUpdateCallback;
        localArrayList = new ArrayList();
        i = this.mOldListSize;
        j = this.mNewListSize;
      }
      for (int k = this.mSnakes.size() - 1;; k--)
      {
        if (k < 0) {
          break label232;
        }
        DiffUtil.Snake localSnake = (DiffUtil.Snake)this.mSnakes.get(k);
        int m = localSnake.size;
        int n = localSnake.x + m;
        int i1 = localSnake.y + m;
        if (n < i) {
          dispatchRemovals(localArrayList, paramListUpdateCallback, n, i - n, n);
        }
        if (i1 < j) {
          dispatchAdditions(localArrayList, paramListUpdateCallback, n, j - i1, i1);
        }
        i = m - 1;
        for (;;)
        {
          if (i >= 0)
          {
            if ((this.mOldItemStatuses[(localSnake.x + i)] & 0x1F) == 2) {
              paramListUpdateCallback.onChanged(localSnake.x + i, 1, this.mCallback.getChangePayload(localSnake.x + i, localSnake.y + i));
            }
            i--;
            continue;
            paramListUpdateCallback = new BatchingListUpdateCallback(paramListUpdateCallback);
            break;
          }
        }
        i = localSnake.x;
        j = localSnake.y;
      }
      label232:
      paramListUpdateCallback.dispatchLastEvent();
    }
    
    public void dispatchUpdatesTo(RecyclerView.Adapter paramAdapter)
    {
      dispatchUpdatesTo(new AdapterListUpdateCallback(paramAdapter));
    }
    
    List<DiffUtil.Snake> getSnakes()
    {
      return this.mSnakes;
    }
  }
  
  public static abstract class ItemCallback<T>
  {
    public abstract boolean areContentsTheSame(T paramT1, T paramT2);
    
    public abstract boolean areItemsTheSame(T paramT1, T paramT2);
    
    public Object getChangePayload(T paramT1, T paramT2)
    {
      return null;
    }
  }
  
  private static class PostponedUpdate
  {
    int currentPos;
    int posInOwnerList;
    boolean removal;
    
    public PostponedUpdate(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.posInOwnerList = paramInt1;
      this.currentPos = paramInt2;
      this.removal = paramBoolean;
    }
  }
  
  static class Range
  {
    int newListEnd;
    int newListStart;
    int oldListEnd;
    int oldListStart;
    
    public Range() {}
    
    public Range(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.oldListStart = paramInt1;
      this.oldListEnd = paramInt2;
      this.newListStart = paramInt3;
      this.newListEnd = paramInt4;
    }
  }
  
  static class Snake
  {
    boolean removal;
    boolean reverse;
    int size;
    int x;
    int y;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/util/DiffUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */