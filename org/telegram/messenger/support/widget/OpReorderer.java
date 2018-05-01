package org.telegram.messenger.support.widget;

import java.util.List;

class OpReorderer
{
  final Callback mCallback;
  
  OpReorderer(Callback paramCallback)
  {
    this.mCallback = paramCallback;
  }
  
  private int getLastMoveOutOfOrder(List<AdapterHelper.UpdateOp> paramList)
  {
    int i = 0;
    int j = paramList.size() - 1;
    int k;
    if (j >= 0) {
      if (((AdapterHelper.UpdateOp)paramList.get(j)).cmd == 8)
      {
        k = i;
        if (i == 0) {
          break label45;
        }
      }
    }
    for (;;)
    {
      return j;
      k = 1;
      label45:
      j--;
      i = k;
      break;
      j = -1;
    }
  }
  
  private void swapMoveAdd(List<AdapterHelper.UpdateOp> paramList, int paramInt1, AdapterHelper.UpdateOp paramUpdateOp1, int paramInt2, AdapterHelper.UpdateOp paramUpdateOp2)
  {
    int i = 0;
    if (paramUpdateOp1.itemCount < paramUpdateOp2.positionStart) {
      i = 0 - 1;
    }
    int j = i;
    if (paramUpdateOp1.positionStart < paramUpdateOp2.positionStart) {
      j = i + 1;
    }
    if (paramUpdateOp2.positionStart <= paramUpdateOp1.positionStart) {
      paramUpdateOp1.positionStart += paramUpdateOp2.itemCount;
    }
    if (paramUpdateOp2.positionStart <= paramUpdateOp1.itemCount) {
      paramUpdateOp1.itemCount += paramUpdateOp2.itemCount;
    }
    paramUpdateOp2.positionStart += j;
    paramList.set(paramInt1, paramUpdateOp2);
    paramList.set(paramInt2, paramUpdateOp1);
  }
  
  private void swapMoveOp(List<AdapterHelper.UpdateOp> paramList, int paramInt1, int paramInt2)
  {
    AdapterHelper.UpdateOp localUpdateOp1 = (AdapterHelper.UpdateOp)paramList.get(paramInt1);
    AdapterHelper.UpdateOp localUpdateOp2 = (AdapterHelper.UpdateOp)paramList.get(paramInt2);
    switch (localUpdateOp2.cmd)
    {
    }
    for (;;)
    {
      return;
      swapMoveRemove(paramList, paramInt1, localUpdateOp1, paramInt2, localUpdateOp2);
      continue;
      swapMoveAdd(paramList, paramInt1, localUpdateOp1, paramInt2, localUpdateOp2);
      continue;
      swapMoveUpdate(paramList, paramInt1, localUpdateOp1, paramInt2, localUpdateOp2);
    }
  }
  
  void reorderOps(List<AdapterHelper.UpdateOp> paramList)
  {
    for (;;)
    {
      int i = getLastMoveOutOfOrder(paramList);
      if (i == -1) {
        break;
      }
      swapMoveOp(paramList, i, i + 1);
    }
  }
  
  void swapMoveRemove(List<AdapterHelper.UpdateOp> paramList, int paramInt1, AdapterHelper.UpdateOp paramUpdateOp1, int paramInt2, AdapterHelper.UpdateOp paramUpdateOp2)
  {
    AdapterHelper.UpdateOp localUpdateOp = null;
    int i = 0;
    int j;
    int k;
    int m;
    if (paramUpdateOp1.positionStart < paramUpdateOp1.itemCount)
    {
      j = 0;
      k = j;
      m = i;
      if (paramUpdateOp2.positionStart == paramUpdateOp1.positionStart)
      {
        k = j;
        m = i;
        if (paramUpdateOp2.itemCount == paramUpdateOp1.itemCount - paramUpdateOp1.positionStart)
        {
          m = 1;
          k = j;
        }
      }
      if (paramUpdateOp1.itemCount >= paramUpdateOp2.positionStart) {
        break label215;
      }
      paramUpdateOp2.positionStart -= 1;
      label96:
      if (paramUpdateOp1.positionStart > paramUpdateOp2.positionStart) {
        break label286;
      }
      paramUpdateOp2.positionStart += 1;
      label120:
      if (m == 0) {
        break label369;
      }
      paramList.set(paramInt1, paramUpdateOp2);
      paramList.remove(paramInt2);
      this.mCallback.recycleUpdateOp(paramUpdateOp1);
    }
    label215:
    label286:
    label369:
    label483:
    label643:
    label653:
    for (;;)
    {
      return;
      j = 1;
      k = j;
      m = i;
      if (paramUpdateOp2.positionStart != paramUpdateOp1.itemCount + 1) {
        break;
      }
      k = j;
      m = i;
      if (paramUpdateOp2.itemCount != paramUpdateOp1.positionStart - paramUpdateOp1.itemCount) {
        break;
      }
      m = 1;
      k = j;
      break;
      if (paramUpdateOp1.itemCount >= paramUpdateOp2.positionStart + paramUpdateOp2.itemCount) {
        break label96;
      }
      paramUpdateOp2.itemCount -= 1;
      paramUpdateOp1.cmd = 2;
      paramUpdateOp1.itemCount = 1;
      if (paramUpdateOp2.itemCount == 0)
      {
        paramList.remove(paramInt2);
        this.mCallback.recycleUpdateOp(paramUpdateOp2);
        continue;
        if (paramUpdateOp1.positionStart >= paramUpdateOp2.positionStart + paramUpdateOp2.itemCount) {
          break label120;
        }
        int n = paramUpdateOp2.positionStart;
        j = paramUpdateOp2.itemCount;
        i = paramUpdateOp1.positionStart;
        localUpdateOp = this.mCallback.obtainUpdateOp(2, paramUpdateOp1.positionStart + 1, n + j - i, null);
        paramUpdateOp2.itemCount = (paramUpdateOp1.positionStart - paramUpdateOp2.positionStart);
        break label120;
        if (k != 0)
        {
          if (localUpdateOp != null)
          {
            if (paramUpdateOp1.positionStart > localUpdateOp.positionStart) {
              paramUpdateOp1.positionStart -= localUpdateOp.itemCount;
            }
            if (paramUpdateOp1.itemCount > localUpdateOp.positionStart) {
              paramUpdateOp1.itemCount -= localUpdateOp.itemCount;
            }
          }
          if (paramUpdateOp1.positionStart > paramUpdateOp2.positionStart) {
            paramUpdateOp1.positionStart -= paramUpdateOp2.itemCount;
          }
          if (paramUpdateOp1.itemCount > paramUpdateOp2.positionStart) {
            paramUpdateOp1.itemCount -= paramUpdateOp2.itemCount;
          }
          paramList.set(paramInt1, paramUpdateOp2);
          if (paramUpdateOp1.positionStart == paramUpdateOp1.itemCount) {
            break label643;
          }
          paramList.set(paramInt2, paramUpdateOp1);
        }
        for (;;)
        {
          if (localUpdateOp == null) {
            break label653;
          }
          paramList.add(paramInt1, localUpdateOp);
          break;
          if (localUpdateOp != null)
          {
            if (paramUpdateOp1.positionStart >= localUpdateOp.positionStart) {
              paramUpdateOp1.positionStart -= localUpdateOp.itemCount;
            }
            if (paramUpdateOp1.itemCount >= localUpdateOp.positionStart) {
              paramUpdateOp1.itemCount -= localUpdateOp.itemCount;
            }
          }
          if (paramUpdateOp1.positionStart >= paramUpdateOp2.positionStart) {
            paramUpdateOp1.positionStart -= paramUpdateOp2.itemCount;
          }
          if (paramUpdateOp1.itemCount < paramUpdateOp2.positionStart) {
            break label483;
          }
          paramUpdateOp1.itemCount -= paramUpdateOp2.itemCount;
          break label483;
          paramList.remove(paramInt2);
        }
      }
    }
  }
  
  void swapMoveUpdate(List<AdapterHelper.UpdateOp> paramList, int paramInt1, AdapterHelper.UpdateOp paramUpdateOp1, int paramInt2, AdapterHelper.UpdateOp paramUpdateOp2)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    if (paramUpdateOp1.itemCount < paramUpdateOp2.positionStart)
    {
      paramUpdateOp2.positionStart -= 1;
      if (paramUpdateOp1.positionStart > paramUpdateOp2.positionStart) {
        break label166;
      }
      paramUpdateOp2.positionStart += 1;
      label54:
      paramList.set(paramInt2, paramUpdateOp1);
      if (paramUpdateOp2.itemCount <= 0) {
        break label243;
      }
      paramList.set(paramInt1, paramUpdateOp2);
    }
    for (;;)
    {
      if (localObject1 != null) {
        paramList.add(paramInt1, localObject1);
      }
      if (localObject2 != null) {
        paramList.add(paramInt1, localObject2);
      }
      return;
      if (paramUpdateOp1.itemCount >= paramUpdateOp2.positionStart + paramUpdateOp2.itemCount) {
        break;
      }
      paramUpdateOp2.itemCount -= 1;
      localObject1 = this.mCallback.obtainUpdateOp(4, paramUpdateOp1.positionStart, 1, paramUpdateOp2.payload);
      break;
      label166:
      if (paramUpdateOp1.positionStart >= paramUpdateOp2.positionStart + paramUpdateOp2.itemCount) {
        break label54;
      }
      int i = paramUpdateOp2.positionStart + paramUpdateOp2.itemCount - paramUpdateOp1.positionStart;
      localObject2 = this.mCallback.obtainUpdateOp(4, paramUpdateOp1.positionStart + 1, i, paramUpdateOp2.payload);
      paramUpdateOp2.itemCount -= i;
      break label54;
      label243:
      paramList.remove(paramInt1);
      this.mCallback.recycleUpdateOp(paramUpdateOp2);
    }
  }
  
  static abstract interface Callback
  {
    public abstract AdapterHelper.UpdateOp obtainUpdateOp(int paramInt1, int paramInt2, int paramInt3, Object paramObject);
    
    public abstract void recycleUpdateOp(AdapterHelper.UpdateOp paramUpdateOp);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/OpReorderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */