package org.telegram.messenger.support.widget;

import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AdapterHelper
  implements OpReorderer.Callback
{
  private static final boolean DEBUG = false;
  static final int POSITION_TYPE_INVISIBLE = 0;
  static final int POSITION_TYPE_NEW_OR_LAID_OUT = 1;
  private static final String TAG = "AHT";
  final Callback mCallback;
  final boolean mDisableRecycler;
  private int mExistingUpdateTypes = 0;
  Runnable mOnItemProcessedCallback;
  final OpReorderer mOpReorderer;
  final ArrayList<UpdateOp> mPendingUpdates = new ArrayList();
  final ArrayList<UpdateOp> mPostponedList = new ArrayList();
  private Pools.Pool<UpdateOp> mUpdateOpPool = new Pools.SimplePool(30);
  
  AdapterHelper(Callback paramCallback)
  {
    this(paramCallback, false);
  }
  
  AdapterHelper(Callback paramCallback, boolean paramBoolean)
  {
    this.mCallback = paramCallback;
    this.mDisableRecycler = paramBoolean;
    this.mOpReorderer = new OpReorderer(this);
  }
  
  private void applyAdd(UpdateOp paramUpdateOp)
  {
    postponeAndUpdateViewHolders(paramUpdateOp);
  }
  
  private void applyMove(UpdateOp paramUpdateOp)
  {
    postponeAndUpdateViewHolders(paramUpdateOp);
  }
  
  private void applyRemove(UpdateOp paramUpdateOp)
  {
    int i = paramUpdateOp.positionStart;
    int j = 0;
    int k = paramUpdateOp.positionStart + paramUpdateOp.itemCount;
    int m = -1;
    int n = paramUpdateOp.positionStart;
    if (n < k)
    {
      int i1 = 0;
      int i2 = 0;
      if ((this.mCallback.findViewHolder(n) != null) || (canFindInPreLayout(n)))
      {
        if (m == 0)
        {
          dispatchAndUpdateViewHolders(obtainUpdateOp(2, i, j, null));
          i2 = 1;
        }
        m = 1;
        i1 = i2;
        i2 = m;
        label94:
        if (i1 == 0) {
          break label154;
        }
        n -= j;
        k -= j;
      }
      label154:
      for (i1 = 1;; i1 = j + 1)
      {
        n++;
        j = i1;
        m = i2;
        break;
        if (m == 1)
        {
          postponeAndUpdateViewHolders(obtainUpdateOp(2, i, j, null));
          i1 = 1;
        }
        i2 = 0;
        break label94;
      }
    }
    UpdateOp localUpdateOp = paramUpdateOp;
    if (j != paramUpdateOp.itemCount)
    {
      recycleUpdateOp(paramUpdateOp);
      localUpdateOp = obtainUpdateOp(2, i, j, null);
    }
    if (m == 0) {
      dispatchAndUpdateViewHolders(localUpdateOp);
    }
    for (;;)
    {
      return;
      postponeAndUpdateViewHolders(localUpdateOp);
    }
  }
  
  private void applyUpdate(UpdateOp paramUpdateOp)
  {
    int i = paramUpdateOp.positionStart;
    int j = 0;
    int k = paramUpdateOp.positionStart;
    int m = paramUpdateOp.itemCount;
    int n = -1;
    int i1 = paramUpdateOp.positionStart;
    if (i1 < k + m)
    {
      int i3;
      int i4;
      if ((this.mCallback.findViewHolder(i1) != null) || (canFindInPreLayout(i1)))
      {
        int i2 = j;
        i3 = i;
        if (n == 0)
        {
          dispatchAndUpdateViewHolders(obtainUpdateOp(4, i, j, paramUpdateOp.payload));
          i2 = 0;
          i3 = i1;
        }
        i4 = 1;
        i = i3;
        i3 = i2;
      }
      for (;;)
      {
        j = i3 + 1;
        i1++;
        n = i4;
        break;
        i3 = j;
        i4 = i;
        if (n == 1)
        {
          postponeAndUpdateViewHolders(obtainUpdateOp(4, i, j, paramUpdateOp.payload));
          i3 = 0;
          i4 = i1;
        }
        j = 0;
        i = i4;
        i4 = j;
      }
    }
    Object localObject = paramUpdateOp;
    if (j != paramUpdateOp.itemCount)
    {
      localObject = paramUpdateOp.payload;
      recycleUpdateOp(paramUpdateOp);
      localObject = obtainUpdateOp(4, i, j, localObject);
    }
    if (n == 0) {
      dispatchAndUpdateViewHolders((UpdateOp)localObject);
    }
    for (;;)
    {
      return;
      postponeAndUpdateViewHolders((UpdateOp)localObject);
    }
  }
  
  private boolean canFindInPreLayout(int paramInt)
  {
    boolean bool1 = true;
    int i = this.mPostponedList.size();
    int j = 0;
    UpdateOp localUpdateOp;
    boolean bool2;
    if (j < i)
    {
      localUpdateOp = (UpdateOp)this.mPostponedList.get(j);
      if (localUpdateOp.cmd == 8)
      {
        if (findPositionOffset(localUpdateOp.itemCount, j + 1) != paramInt) {
          break label129;
        }
        bool2 = bool1;
      }
    }
    for (;;)
    {
      return bool2;
      if (localUpdateOp.cmd == 1)
      {
        int k = localUpdateOp.positionStart;
        int m = localUpdateOp.itemCount;
        for (int n = localUpdateOp.positionStart;; n++)
        {
          if (n >= k + m) {
            break label129;
          }
          bool2 = bool1;
          if (findPositionOffset(n, j + 1) == paramInt) {
            break;
          }
        }
      }
      label129:
      j++;
      break;
      bool2 = false;
    }
  }
  
  private void dispatchAndUpdateViewHolders(UpdateOp paramUpdateOp)
  {
    if ((paramUpdateOp.cmd == 1) || (paramUpdateOp.cmd == 8)) {
      throw new IllegalArgumentException("should not dispatch add or move for pre layout");
    }
    int i = updatePositionWithPostponed(paramUpdateOp.positionStart, paramUpdateOp.cmd);
    int j = 1;
    int k = paramUpdateOp.positionStart;
    int m;
    int n;
    label113:
    int i1;
    switch (paramUpdateOp.cmd)
    {
    case 3: 
    default: 
      throw new IllegalArgumentException("op should be remove or update." + paramUpdateOp);
    case 4: 
      m = 1;
      n = 1;
      if (n >= paramUpdateOp.itemCount) {
        break label310;
      }
      i1 = updatePositionWithPostponed(paramUpdateOp.positionStart + m * n, paramUpdateOp.cmd);
      int i2 = 0;
      i3 = i2;
      switch (paramUpdateOp.cmd)
      {
      default: 
        i3 = i2;
      case 3: 
        if (i3 == 0) {}
        break;
      }
      break;
    }
    for (int i3 = j + 1;; i3 = j)
    {
      n++;
      j = i3;
      break label113;
      m = 0;
      break;
      if (i1 == i + 1) {}
      for (i3 = 1;; i3 = 0) {
        break;
      }
      if (i1 == i) {}
      for (i3 = 1;; i3 = 0) {
        break;
      }
      localObject = obtainUpdateOp(paramUpdateOp.cmd, i, j, paramUpdateOp.payload);
      dispatchFirstPassAndUpdateViewHolders((UpdateOp)localObject, k);
      recycleUpdateOp((UpdateOp)localObject);
      i3 = k;
      if (paramUpdateOp.cmd == 4) {
        i3 = k + j;
      }
      i = i1;
      j = 1;
      k = i3;
    }
    label310:
    Object localObject = paramUpdateOp.payload;
    recycleUpdateOp(paramUpdateOp);
    if (j > 0)
    {
      paramUpdateOp = obtainUpdateOp(paramUpdateOp.cmd, i, j, localObject);
      dispatchFirstPassAndUpdateViewHolders(paramUpdateOp, k);
      recycleUpdateOp(paramUpdateOp);
    }
  }
  
  private void postponeAndUpdateViewHolders(UpdateOp paramUpdateOp)
  {
    this.mPostponedList.add(paramUpdateOp);
    switch (paramUpdateOp.cmd)
    {
    case 3: 
    case 5: 
    case 6: 
    case 7: 
    default: 
      throw new IllegalArgumentException("Unknown update op type for " + paramUpdateOp);
    case 1: 
      this.mCallback.offsetPositionsForAdd(paramUpdateOp.positionStart, paramUpdateOp.itemCount);
    }
    for (;;)
    {
      return;
      this.mCallback.offsetPositionsForMove(paramUpdateOp.positionStart, paramUpdateOp.itemCount);
      continue;
      this.mCallback.offsetPositionsForRemovingLaidOutOrNewView(paramUpdateOp.positionStart, paramUpdateOp.itemCount);
      continue;
      this.mCallback.markViewHoldersUpdated(paramUpdateOp.positionStart, paramUpdateOp.itemCount, paramUpdateOp.payload);
    }
  }
  
  private int updatePositionWithPostponed(int paramInt1, int paramInt2)
  {
    int i = this.mPostponedList.size() - 1;
    int j = paramInt1;
    UpdateOp localUpdateOp;
    if (i >= 0)
    {
      localUpdateOp = (UpdateOp)this.mPostponedList.get(i);
      int k;
      if (localUpdateOp.cmd == 8) {
        if (localUpdateOp.positionStart < localUpdateOp.itemCount)
        {
          paramInt1 = localUpdateOp.positionStart;
          k = localUpdateOp.itemCount;
          label66:
          if ((j < paramInt1) || (j > k)) {
            break label200;
          }
          if (paramInt1 != localUpdateOp.positionStart) {
            break label155;
          }
          if (paramInt2 != 1) {
            break label135;
          }
          localUpdateOp.itemCount += 1;
          label105:
          paramInt1 = j + 1;
        }
      }
      for (;;)
      {
        i--;
        j = paramInt1;
        break;
        paramInt1 = localUpdateOp.itemCount;
        k = localUpdateOp.positionStart;
        break label66;
        label135:
        if (paramInt2 != 2) {
          break label105;
        }
        localUpdateOp.itemCount -= 1;
        break label105;
        label155:
        if (paramInt2 == 1) {
          localUpdateOp.positionStart += 1;
        }
        for (;;)
        {
          paramInt1 = j - 1;
          break;
          if (paramInt2 == 2) {
            localUpdateOp.positionStart -= 1;
          }
        }
        label200:
        paramInt1 = j;
        if (j < localUpdateOp.positionStart) {
          if (paramInt2 == 1)
          {
            localUpdateOp.positionStart += 1;
            localUpdateOp.itemCount += 1;
            paramInt1 = j;
          }
          else
          {
            paramInt1 = j;
            if (paramInt2 == 2)
            {
              localUpdateOp.positionStart -= 1;
              localUpdateOp.itemCount -= 1;
              paramInt1 = j;
              continue;
              if (localUpdateOp.positionStart <= j)
              {
                if (localUpdateOp.cmd == 1)
                {
                  paramInt1 = j - localUpdateOp.itemCount;
                }
                else
                {
                  paramInt1 = j;
                  if (localUpdateOp.cmd == 2) {
                    paramInt1 = j + localUpdateOp.itemCount;
                  }
                }
              }
              else if (paramInt2 == 1)
              {
                localUpdateOp.positionStart += 1;
                paramInt1 = j;
              }
              else
              {
                paramInt1 = j;
                if (paramInt2 == 2)
                {
                  localUpdateOp.positionStart -= 1;
                  paramInt1 = j;
                }
              }
            }
          }
        }
      }
    }
    paramInt1 = this.mPostponedList.size() - 1;
    if (paramInt1 >= 0)
    {
      localUpdateOp = (UpdateOp)this.mPostponedList.get(paramInt1);
      if (localUpdateOp.cmd == 8) {
        if ((localUpdateOp.itemCount == localUpdateOp.positionStart) || (localUpdateOp.itemCount < 0))
        {
          this.mPostponedList.remove(paramInt1);
          recycleUpdateOp(localUpdateOp);
        }
      }
      for (;;)
      {
        paramInt1--;
        break;
        if (localUpdateOp.itemCount <= 0)
        {
          this.mPostponedList.remove(paramInt1);
          recycleUpdateOp(localUpdateOp);
        }
      }
    }
    return j;
  }
  
  AdapterHelper addUpdateOp(UpdateOp... paramVarArgs)
  {
    Collections.addAll(this.mPendingUpdates, paramVarArgs);
    return this;
  }
  
  public int applyPendingUpdatesToPosition(int paramInt)
  {
    int i = this.mPendingUpdates.size();
    int j = 0;
    int k = paramInt;
    paramInt = k;
    UpdateOp localUpdateOp;
    if (j < i)
    {
      localUpdateOp = (UpdateOp)this.mPendingUpdates.get(j);
      switch (localUpdateOp.cmd)
      {
      default: 
        paramInt = k;
      }
    }
    for (;;)
    {
      j++;
      k = paramInt;
      break;
      paramInt = k;
      if (localUpdateOp.positionStart <= k)
      {
        paramInt = k + localUpdateOp.itemCount;
        continue;
        paramInt = k;
        if (localUpdateOp.positionStart <= k)
        {
          if (localUpdateOp.positionStart + localUpdateOp.itemCount > k)
          {
            paramInt = -1;
            return paramInt;
          }
          paramInt = k - localUpdateOp.itemCount;
          continue;
          if (localUpdateOp.positionStart == k)
          {
            paramInt = localUpdateOp.itemCount;
          }
          else
          {
            int m = k;
            if (localUpdateOp.positionStart < k) {
              m = k - 1;
            }
            paramInt = m;
            if (localUpdateOp.itemCount <= m) {
              paramInt = m + 1;
            }
          }
        }
      }
    }
  }
  
  void consumePostponedUpdates()
  {
    int i = this.mPostponedList.size();
    for (int j = 0; j < i; j++) {
      this.mCallback.onDispatchSecondPass((UpdateOp)this.mPostponedList.get(j));
    }
    recycleUpdateOpsAndClearList(this.mPostponedList);
    this.mExistingUpdateTypes = 0;
  }
  
  void consumeUpdatesInOnePass()
  {
    consumePostponedUpdates();
    int i = this.mPendingUpdates.size();
    int j = 0;
    if (j < i)
    {
      UpdateOp localUpdateOp = (UpdateOp)this.mPendingUpdates.get(j);
      switch (localUpdateOp.cmd)
      {
      }
      for (;;)
      {
        if (this.mOnItemProcessedCallback != null) {
          this.mOnItemProcessedCallback.run();
        }
        j++;
        break;
        this.mCallback.onDispatchSecondPass(localUpdateOp);
        this.mCallback.offsetPositionsForAdd(localUpdateOp.positionStart, localUpdateOp.itemCount);
        continue;
        this.mCallback.onDispatchSecondPass(localUpdateOp);
        this.mCallback.offsetPositionsForRemovingInvisible(localUpdateOp.positionStart, localUpdateOp.itemCount);
        continue;
        this.mCallback.onDispatchSecondPass(localUpdateOp);
        this.mCallback.markViewHoldersUpdated(localUpdateOp.positionStart, localUpdateOp.itemCount, localUpdateOp.payload);
        continue;
        this.mCallback.onDispatchSecondPass(localUpdateOp);
        this.mCallback.offsetPositionsForMove(localUpdateOp.positionStart, localUpdateOp.itemCount);
      }
    }
    recycleUpdateOpsAndClearList(this.mPendingUpdates);
    this.mExistingUpdateTypes = 0;
  }
  
  void dispatchFirstPassAndUpdateViewHolders(UpdateOp paramUpdateOp, int paramInt)
  {
    this.mCallback.onDispatchFirstPass(paramUpdateOp);
    switch (paramUpdateOp.cmd)
    {
    case 3: 
    default: 
      throw new IllegalArgumentException("only remove and update ops can be dispatched in first pass");
    case 2: 
      this.mCallback.offsetPositionsForRemovingInvisible(paramInt, paramUpdateOp.itemCount);
    }
    for (;;)
    {
      return;
      this.mCallback.markViewHoldersUpdated(paramInt, paramUpdateOp.itemCount, paramUpdateOp.payload);
    }
  }
  
  int findPositionOffset(int paramInt)
  {
    return findPositionOffset(paramInt, 0);
  }
  
  int findPositionOffset(int paramInt1, int paramInt2)
  {
    int i = this.mPostponedList.size();
    int j = paramInt2;
    paramInt2 = paramInt1;
    paramInt1 = paramInt2;
    UpdateOp localUpdateOp;
    if (j < i)
    {
      localUpdateOp = (UpdateOp)this.mPostponedList.get(j);
      if (localUpdateOp.cmd == 8) {
        if (localUpdateOp.positionStart == paramInt2) {
          paramInt1 = localUpdateOp.itemCount;
        }
      }
    }
    for (;;)
    {
      j++;
      paramInt2 = paramInt1;
      break;
      int k = paramInt2;
      if (localUpdateOp.positionStart < paramInt2) {
        k = paramInt2 - 1;
      }
      paramInt1 = k;
      if (localUpdateOp.itemCount <= k)
      {
        paramInt1 = k + 1;
        continue;
        paramInt1 = paramInt2;
        if (localUpdateOp.positionStart <= paramInt2) {
          if (localUpdateOp.cmd == 2)
          {
            if (paramInt2 < localUpdateOp.positionStart + localUpdateOp.itemCount)
            {
              paramInt1 = -1;
              return paramInt1;
            }
            paramInt1 = paramInt2 - localUpdateOp.itemCount;
          }
          else
          {
            paramInt1 = paramInt2;
            if (localUpdateOp.cmd == 1) {
              paramInt1 = paramInt2 + localUpdateOp.itemCount;
            }
          }
        }
      }
    }
  }
  
  boolean hasAnyUpdateTypes(int paramInt)
  {
    if ((this.mExistingUpdateTypes & paramInt) != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  boolean hasPendingUpdates()
  {
    if (this.mPendingUpdates.size() > 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  boolean hasUpdates()
  {
    if ((!this.mPostponedList.isEmpty()) && (!this.mPendingUpdates.isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public UpdateOp obtainUpdateOp(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    UpdateOp localUpdateOp = (UpdateOp)this.mUpdateOpPool.acquire();
    if (localUpdateOp == null) {}
    for (paramObject = new UpdateOp(paramInt1, paramInt2, paramInt3, paramObject);; paramObject = localUpdateOp)
    {
      return (UpdateOp)paramObject;
      localUpdateOp.cmd = paramInt1;
      localUpdateOp.positionStart = paramInt2;
      localUpdateOp.itemCount = paramInt3;
      localUpdateOp.payload = paramObject;
    }
  }
  
  boolean onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    if (paramInt2 < 1) {
      bool2 = bool1;
    }
    do
    {
      return bool2;
      this.mPendingUpdates.add(obtainUpdateOp(4, paramInt1, paramInt2, paramObject));
      this.mExistingUpdateTypes |= 0x4;
    } while (this.mPendingUpdates.size() == 1);
    for (;;)
    {
      bool2 = false;
    }
  }
  
  boolean onItemRangeInserted(int paramInt1, int paramInt2)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    if (paramInt2 < 1) {
      bool2 = bool1;
    }
    do
    {
      return bool2;
      this.mPendingUpdates.add(obtainUpdateOp(1, paramInt1, paramInt2, null));
      this.mExistingUpdateTypes |= 0x1;
    } while (this.mPendingUpdates.size() == 1);
    for (;;)
    {
      bool2 = false;
    }
  }
  
  boolean onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    if (paramInt1 == paramInt2) {
      bool2 = bool1;
    }
    do
    {
      return bool2;
      if (paramInt3 != 1) {
        throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
      }
      this.mPendingUpdates.add(obtainUpdateOp(8, paramInt1, paramInt2, null));
      this.mExistingUpdateTypes |= 0x8;
    } while (this.mPendingUpdates.size() == 1);
    for (;;)
    {
      bool2 = false;
    }
  }
  
  boolean onItemRangeRemoved(int paramInt1, int paramInt2)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    if (paramInt2 < 1) {
      bool2 = bool1;
    }
    do
    {
      return bool2;
      this.mPendingUpdates.add(obtainUpdateOp(2, paramInt1, paramInt2, null));
      this.mExistingUpdateTypes |= 0x2;
    } while (this.mPendingUpdates.size() == 1);
    for (;;)
    {
      bool2 = false;
    }
  }
  
  void preProcess()
  {
    this.mOpReorderer.reorderOps(this.mPendingUpdates);
    int i = this.mPendingUpdates.size();
    int j = 0;
    if (j < i)
    {
      UpdateOp localUpdateOp = (UpdateOp)this.mPendingUpdates.get(j);
      switch (localUpdateOp.cmd)
      {
      }
      for (;;)
      {
        if (this.mOnItemProcessedCallback != null) {
          this.mOnItemProcessedCallback.run();
        }
        j++;
        break;
        applyAdd(localUpdateOp);
        continue;
        applyRemove(localUpdateOp);
        continue;
        applyUpdate(localUpdateOp);
        continue;
        applyMove(localUpdateOp);
      }
    }
    this.mPendingUpdates.clear();
  }
  
  public void recycleUpdateOp(UpdateOp paramUpdateOp)
  {
    if (!this.mDisableRecycler)
    {
      paramUpdateOp.payload = null;
      this.mUpdateOpPool.release(paramUpdateOp);
    }
  }
  
  void recycleUpdateOpsAndClearList(List<UpdateOp> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i; j++) {
      recycleUpdateOp((UpdateOp)paramList.get(j));
    }
    paramList.clear();
  }
  
  void reset()
  {
    recycleUpdateOpsAndClearList(this.mPendingUpdates);
    recycleUpdateOpsAndClearList(this.mPostponedList);
    this.mExistingUpdateTypes = 0;
  }
  
  static abstract interface Callback
  {
    public abstract RecyclerView.ViewHolder findViewHolder(int paramInt);
    
    public abstract void markViewHoldersUpdated(int paramInt1, int paramInt2, Object paramObject);
    
    public abstract void offsetPositionsForAdd(int paramInt1, int paramInt2);
    
    public abstract void offsetPositionsForMove(int paramInt1, int paramInt2);
    
    public abstract void offsetPositionsForRemovingInvisible(int paramInt1, int paramInt2);
    
    public abstract void offsetPositionsForRemovingLaidOutOrNewView(int paramInt1, int paramInt2);
    
    public abstract void onDispatchFirstPass(AdapterHelper.UpdateOp paramUpdateOp);
    
    public abstract void onDispatchSecondPass(AdapterHelper.UpdateOp paramUpdateOp);
  }
  
  static class UpdateOp
  {
    static final int ADD = 1;
    static final int MOVE = 8;
    static final int POOL_SIZE = 30;
    static final int REMOVE = 2;
    static final int UPDATE = 4;
    int cmd;
    int itemCount;
    Object payload;
    int positionStart;
    
    UpdateOp(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
    {
      this.cmd = paramInt1;
      this.positionStart = paramInt2;
      this.itemCount = paramInt3;
      this.payload = paramObject;
    }
    
    String cmdToString()
    {
      String str;
      switch (this.cmd)
      {
      case 3: 
      case 5: 
      case 6: 
      case 7: 
      default: 
        str = "??";
      }
      for (;;)
      {
        return str;
        str = "add";
        continue;
        str = "rm";
        continue;
        str = "up";
        continue;
        str = "mv";
      }
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool1 = true;
      boolean bool2;
      if (this == paramObject) {
        bool2 = bool1;
      }
      for (;;)
      {
        return bool2;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
        {
          bool2 = false;
        }
        else
        {
          paramObject = (UpdateOp)paramObject;
          if (this.cmd != ((UpdateOp)paramObject).cmd)
          {
            bool2 = false;
          }
          else if ((this.cmd == 8) && (Math.abs(this.itemCount - this.positionStart) == 1) && (this.itemCount == ((UpdateOp)paramObject).positionStart))
          {
            bool2 = bool1;
            if (this.positionStart == ((UpdateOp)paramObject).itemCount) {}
          }
          else if (this.itemCount != ((UpdateOp)paramObject).itemCount)
          {
            bool2 = false;
          }
          else if (this.positionStart != ((UpdateOp)paramObject).positionStart)
          {
            bool2 = false;
          }
          else if (this.payload != null)
          {
            bool2 = bool1;
            if (!this.payload.equals(((UpdateOp)paramObject).payload)) {
              bool2 = false;
            }
          }
          else
          {
            bool2 = bool1;
            if (((UpdateOp)paramObject).payload != null) {
              bool2 = false;
            }
          }
        }
      }
    }
    
    public int hashCode()
    {
      return (this.cmd * 31 + this.positionStart) * 31 + this.itemCount;
    }
    
    public String toString()
    {
      return Integer.toHexString(System.identityHashCode(this)) + "[" + cmdToString() + ",s:" + this.positionStart + "c:" + this.itemCount + ",p:" + this.payload + "]";
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/AdapterHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */