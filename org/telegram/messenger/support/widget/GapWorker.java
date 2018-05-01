package org.telegram.messenger.support.widget;

import android.support.v4.os.TraceCompat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

final class GapWorker
  implements Runnable
{
  static final ThreadLocal<GapWorker> sGapWorker = new ThreadLocal();
  static Comparator<Task> sTaskComparator = new Comparator()
  {
    public int compare(GapWorker.Task paramAnonymousTask1, GapWorker.Task paramAnonymousTask2)
    {
      int i = -1;
      int j = 1;
      int k;
      int m;
      if (paramAnonymousTask1.view == null)
      {
        k = 1;
        if (paramAnonymousTask2.view != null) {
          break label52;
        }
        m = 1;
        label25:
        if (k == m) {
          break label64;
        }
        if (paramAnonymousTask1.view != null) {
          break label58;
        }
        k = j;
      }
      for (;;)
      {
        return k;
        k = 0;
        break;
        label52:
        m = 0;
        break label25;
        label58:
        k = -1;
        continue;
        label64:
        if (paramAnonymousTask1.immediate != paramAnonymousTask2.immediate)
        {
          if (paramAnonymousTask1.immediate) {}
          for (k = i;; k = 1) {
            break;
          }
        }
        k = paramAnonymousTask2.viewVelocity - paramAnonymousTask1.viewVelocity;
        if (k == 0)
        {
          k = paramAnonymousTask1.distanceToItem - paramAnonymousTask2.distanceToItem;
          if (k == 0) {
            k = 0;
          }
        }
      }
    }
  };
  long mFrameIntervalNs;
  long mPostTimeNs;
  ArrayList<RecyclerView> mRecyclerViews = new ArrayList();
  private ArrayList<Task> mTasks = new ArrayList();
  
  private void buildTaskList()
  {
    int i = this.mRecyclerViews.size();
    int j = 0;
    int k = 0;
    Object localObject;
    while (k < i)
    {
      localObject = (RecyclerView)this.mRecyclerViews.get(k);
      m = j;
      if (((RecyclerView)localObject).getWindowVisibility() == 0)
      {
        ((RecyclerView)localObject).mPrefetchRegistry.collectPrefetchPositionsFromView((RecyclerView)localObject, false);
        m = j + ((RecyclerView)localObject).mPrefetchRegistry.mCount;
      }
      k++;
      j = m;
    }
    this.mTasks.ensureCapacity(j);
    j = 0;
    int m = 0;
    if (m < i)
    {
      RecyclerView localRecyclerView = (RecyclerView)this.mRecyclerViews.get(m);
      int n;
      if (localRecyclerView.getWindowVisibility() != 0) {
        n = j;
      }
      LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl;
      int i1;
      do
      {
        m++;
        j = n;
        break;
        localLayoutPrefetchRegistryImpl = localRecyclerView.mPrefetchRegistry;
        i1 = Math.abs(localLayoutPrefetchRegistryImpl.mPrefetchDx) + Math.abs(localLayoutPrefetchRegistryImpl.mPrefetchDy);
        k = 0;
        n = j;
      } while (k >= localLayoutPrefetchRegistryImpl.mCount * 2);
      if (j >= this.mTasks.size())
      {
        localObject = new Task();
        this.mTasks.add(localObject);
        label198:
        n = localLayoutPrefetchRegistryImpl.mPrefetchArray[(k + 1)];
        if (n > i1) {
          break label284;
        }
      }
      label284:
      for (boolean bool = true;; bool = false)
      {
        ((Task)localObject).immediate = bool;
        ((Task)localObject).viewVelocity = i1;
        ((Task)localObject).distanceToItem = n;
        ((Task)localObject).view = localRecyclerView;
        ((Task)localObject).position = localLayoutPrefetchRegistryImpl.mPrefetchArray[k];
        j++;
        k += 2;
        break;
        localObject = (Task)this.mTasks.get(j);
        break label198;
      }
    }
    Collections.sort(this.mTasks, sTaskComparator);
  }
  
  private void flushTaskWithDeadline(Task paramTask, long paramLong)
  {
    if (paramTask.immediate) {}
    for (long l = Long.MAX_VALUE;; l = paramLong)
    {
      paramTask = prefetchPositionWithDeadline(paramTask.view, paramTask.position, l);
      if ((paramTask != null) && (paramTask.mNestedRecyclerView != null) && (paramTask.isBound()) && (!paramTask.isInvalid())) {
        prefetchInnerRecyclerViewWithDeadline((RecyclerView)paramTask.mNestedRecyclerView.get(), paramLong);
      }
      return;
    }
  }
  
  private void flushTasksWithDeadline(long paramLong)
  {
    for (int i = 0;; i++)
    {
      Task localTask;
      if (i < this.mTasks.size())
      {
        localTask = (Task)this.mTasks.get(i);
        if (localTask.view != null) {}
      }
      else
      {
        return;
      }
      flushTaskWithDeadline(localTask, paramLong);
      localTask.clear();
    }
  }
  
  static boolean isPrefetchPositionAttached(RecyclerView paramRecyclerView, int paramInt)
  {
    int i = paramRecyclerView.mChildHelper.getUnfilteredChildCount();
    int j = 0;
    if (j < i)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramRecyclerView.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder.mPosition != paramInt) || (localViewHolder.isInvalid())) {}
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      j++;
      break;
    }
  }
  
  private void prefetchInnerRecyclerViewWithDeadline(RecyclerView paramRecyclerView, long paramLong)
  {
    if (paramRecyclerView == null) {}
    for (;;)
    {
      return;
      if ((paramRecyclerView.mDataSetHasChangedAfterLayout) && (paramRecyclerView.mChildHelper.getUnfilteredChildCount() != 0)) {
        paramRecyclerView.removeAndRecycleViews();
      }
      LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl = paramRecyclerView.mPrefetchRegistry;
      localLayoutPrefetchRegistryImpl.collectPrefetchPositionsFromView(paramRecyclerView, true);
      if (localLayoutPrefetchRegistryImpl.mCount == 0) {
        continue;
      }
      try
      {
        TraceCompat.beginSection("RV Nested Prefetch");
        paramRecyclerView.mState.prepareForNestedPrefetch(paramRecyclerView.mAdapter);
        for (int i = 0; i < localLayoutPrefetchRegistryImpl.mCount * 2; i += 2) {
          prefetchPositionWithDeadline(paramRecyclerView, localLayoutPrefetchRegistryImpl.mPrefetchArray[i], paramLong);
        }
        TraceCompat.endSection();
      }
      finally
      {
        TraceCompat.endSection();
      }
    }
  }
  
  /* Error */
  private RecyclerView.ViewHolder prefetchPositionWithDeadline(RecyclerView paramRecyclerView, int paramInt, long paramLong)
  {
    // Byte code:
    //   0: aload_1
    //   1: iload_2
    //   2: invokestatic 215	org/telegram/messenger/support/widget/GapWorker:isPrefetchPositionAttached	(Lorg/telegram/messenger/support/widget/RecyclerView;I)Z
    //   5: ifeq +7 -> 12
    //   8: aconst_null
    //   9: astore_1
    //   10: aload_1
    //   11: areturn
    //   12: aload_1
    //   13: getfield 219	org/telegram/messenger/support/widget/RecyclerView:mRecycler	Lorg/telegram/messenger/support/widget/RecyclerView$Recycler;
    //   16: astore 5
    //   18: aload_1
    //   19: invokevirtual 222	org/telegram/messenger/support/widget/RecyclerView:onEnterLayoutOrScroll	()V
    //   22: aload 5
    //   24: iload_2
    //   25: iconst_0
    //   26: lload_3
    //   27: invokevirtual 228	org/telegram/messenger/support/widget/RecyclerView$Recycler:tryGetViewHolderForPositionByDeadline	(IZJ)Lorg/telegram/messenger/support/widget/RecyclerView$ViewHolder;
    //   30: astore 6
    //   32: aload 6
    //   34: ifnull +29 -> 63
    //   37: aload 6
    //   39: invokevirtual 141	org/telegram/messenger/support/widget/RecyclerView$ViewHolder:isBound	()Z
    //   42: ifeq +32 -> 74
    //   45: aload 6
    //   47: invokevirtual 144	org/telegram/messenger/support/widget/RecyclerView$ViewHolder:isInvalid	()Z
    //   50: ifne +24 -> 74
    //   53: aload 5
    //   55: aload 6
    //   57: getfield 232	org/telegram/messenger/support/widget/RecyclerView$ViewHolder:itemView	Landroid/view/View;
    //   60: invokevirtual 236	org/telegram/messenger/support/widget/RecyclerView$Recycler:recycleView	(Landroid/view/View;)V
    //   63: aload_1
    //   64: iconst_0
    //   65: invokevirtual 240	org/telegram/messenger/support/widget/RecyclerView:onExitLayoutOrScroll	(Z)V
    //   68: aload 6
    //   70: astore_1
    //   71: goto -61 -> 10
    //   74: aload 5
    //   76: aload 6
    //   78: iconst_0
    //   79: invokevirtual 244	org/telegram/messenger/support/widget/RecyclerView$Recycler:addViewHolderToRecycledViewPool	(Lorg/telegram/messenger/support/widget/RecyclerView$ViewHolder;Z)V
    //   82: goto -19 -> 63
    //   85: astore 6
    //   87: aload_1
    //   88: iconst_0
    //   89: invokevirtual 240	org/telegram/messenger/support/widget/RecyclerView:onExitLayoutOrScroll	(Z)V
    //   92: aload 6
    //   94: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	95	0	this	GapWorker
    //   0	95	1	paramRecyclerView	RecyclerView
    //   0	95	2	paramInt	int
    //   0	95	3	paramLong	long
    //   16	59	5	localRecycler	RecyclerView.Recycler
    //   30	47	6	localViewHolder	RecyclerView.ViewHolder
    //   85	8	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   18	32	85	finally
    //   37	63	85	finally
    //   74	82	85	finally
  }
  
  public void add(RecyclerView paramRecyclerView)
  {
    this.mRecyclerViews.add(paramRecyclerView);
  }
  
  void postFromTraversal(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    if ((paramRecyclerView.isAttachedToWindow()) && (this.mPostTimeNs == 0L))
    {
      this.mPostTimeNs = paramRecyclerView.getNanoTime();
      paramRecyclerView.post(this);
    }
    paramRecyclerView.mPrefetchRegistry.setPrefetchVector(paramInt1, paramInt2);
  }
  
  void prefetch(long paramLong)
  {
    buildTaskList();
    flushTasksWithDeadline(paramLong);
  }
  
  public void remove(RecyclerView paramRecyclerView)
  {
    this.mRecyclerViews.remove(paramRecyclerView);
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: ldc_w 275
    //   3: invokestatic 196	android/support/v4/os/TraceCompat:beginSection	(Ljava/lang/String;)V
    //   6: aload_0
    //   7: getfield 47	org/telegram/messenger/support/widget/GapWorker:mRecyclerViews	Ljava/util/ArrayList;
    //   10: invokevirtual 278	java/util/ArrayList:isEmpty	()Z
    //   13: istore_1
    //   14: iload_1
    //   15: ifeq +12 -> 27
    //   18: aload_0
    //   19: lconst_0
    //   20: putfield 252	org/telegram/messenger/support/widget/GapWorker:mPostTimeNs	J
    //   23: invokestatic 213	android/support/v4/os/TraceCompat:endSection	()V
    //   26: return
    //   27: aload_0
    //   28: getfield 47	org/telegram/messenger/support/widget/GapWorker:mRecyclerViews	Ljava/util/ArrayList;
    //   31: invokevirtual 54	java/util/ArrayList:size	()I
    //   34: istore_2
    //   35: lconst_0
    //   36: lstore_3
    //   37: iconst_0
    //   38: istore 5
    //   40: iload 5
    //   42: iload_2
    //   43: if_icmpge +48 -> 91
    //   46: aload_0
    //   47: getfield 47	org/telegram/messenger/support/widget/GapWorker:mRecyclerViews	Ljava/util/ArrayList;
    //   50: iload 5
    //   52: invokevirtual 58	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   55: checkcast 60	org/telegram/messenger/support/widget/RecyclerView
    //   58: astore 6
    //   60: lload_3
    //   61: lstore 7
    //   63: aload 6
    //   65: invokevirtual 63	org/telegram/messenger/support/widget/RecyclerView:getWindowVisibility	()I
    //   68: ifne +14 -> 82
    //   71: aload 6
    //   73: invokevirtual 281	org/telegram/messenger/support/widget/RecyclerView:getDrawingTime	()J
    //   76: lload_3
    //   77: invokestatic 285	java/lang/Math:max	(JJ)J
    //   80: lstore 7
    //   82: iinc 5 1
    //   85: lload 7
    //   87: lstore_3
    //   88: goto -48 -> 40
    //   91: lload_3
    //   92: lconst_0
    //   93: lcmp
    //   94: ifne +14 -> 108
    //   97: aload_0
    //   98: lconst_0
    //   99: putfield 252	org/telegram/messenger/support/widget/GapWorker:mPostTimeNs	J
    //   102: invokestatic 213	android/support/v4/os/TraceCompat:endSection	()V
    //   105: goto -79 -> 26
    //   108: aload_0
    //   109: getstatic 291	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   112: lload_3
    //   113: invokevirtual 295	java/util/concurrent/TimeUnit:toNanos	(J)J
    //   116: aload_0
    //   117: getfield 297	org/telegram/messenger/support/widget/GapWorker:mFrameIntervalNs	J
    //   120: ladd
    //   121: invokevirtual 299	org/telegram/messenger/support/widget/GapWorker:prefetch	(J)V
    //   124: aload_0
    //   125: lconst_0
    //   126: putfield 252	org/telegram/messenger/support/widget/GapWorker:mPostTimeNs	J
    //   129: invokestatic 213	android/support/v4/os/TraceCompat:endSection	()V
    //   132: goto -106 -> 26
    //   135: astore 6
    //   137: aload_0
    //   138: lconst_0
    //   139: putfield 252	org/telegram/messenger/support/widget/GapWorker:mPostTimeNs	J
    //   142: invokestatic 213	android/support/v4/os/TraceCompat:endSection	()V
    //   145: aload 6
    //   147: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	148	0	this	GapWorker
    //   13	2	1	bool	boolean
    //   34	10	2	i	int
    //   36	77	3	l1	long
    //   38	45	5	j	int
    //   58	14	6	localRecyclerView	RecyclerView
    //   135	11	6	localObject	Object
    //   61	25	7	l2	long
    // Exception table:
    //   from	to	target	type
    //   0	14	135	finally
    //   27	35	135	finally
    //   46	60	135	finally
    //   63	82	135	finally
    //   108	124	135	finally
  }
  
  static class LayoutPrefetchRegistryImpl
    implements RecyclerView.LayoutManager.LayoutPrefetchRegistry
  {
    int mCount;
    int[] mPrefetchArray;
    int mPrefetchDx;
    int mPrefetchDy;
    
    public void addPosition(int paramInt1, int paramInt2)
    {
      if (paramInt1 < 0) {
        throw new IllegalArgumentException("Layout positions must be non-negative");
      }
      if (paramInt2 < 0) {
        throw new IllegalArgumentException("Pixel distance must be non-negative");
      }
      int i = this.mCount * 2;
      if (this.mPrefetchArray == null)
      {
        this.mPrefetchArray = new int[4];
        Arrays.fill(this.mPrefetchArray, -1);
      }
      for (;;)
      {
        this.mPrefetchArray[i] = paramInt1;
        this.mPrefetchArray[(i + 1)] = paramInt2;
        this.mCount += 1;
        return;
        if (i >= this.mPrefetchArray.length)
        {
          int[] arrayOfInt = this.mPrefetchArray;
          this.mPrefetchArray = new int[i * 2];
          System.arraycopy(arrayOfInt, 0, this.mPrefetchArray, 0, arrayOfInt.length);
        }
      }
    }
    
    void clearPrefetchPositions()
    {
      if (this.mPrefetchArray != null) {
        Arrays.fill(this.mPrefetchArray, -1);
      }
      this.mCount = 0;
    }
    
    void collectPrefetchPositionsFromView(RecyclerView paramRecyclerView, boolean paramBoolean)
    {
      this.mCount = 0;
      if (this.mPrefetchArray != null) {
        Arrays.fill(this.mPrefetchArray, -1);
      }
      RecyclerView.LayoutManager localLayoutManager = paramRecyclerView.mLayout;
      if ((paramRecyclerView.mAdapter != null) && (localLayoutManager != null) && (localLayoutManager.isItemPrefetchEnabled()))
      {
        if (!paramBoolean) {
          break label101;
        }
        if (!paramRecyclerView.mAdapterHelper.hasPendingUpdates()) {
          localLayoutManager.collectInitialPrefetchPositions(paramRecyclerView.mAdapter.getItemCount(), this);
        }
      }
      for (;;)
      {
        if (this.mCount > localLayoutManager.mPrefetchMaxCountObserved)
        {
          localLayoutManager.mPrefetchMaxCountObserved = this.mCount;
          localLayoutManager.mPrefetchMaxObservedInInitialPrefetch = paramBoolean;
          paramRecyclerView.mRecycler.updateViewCacheSize();
        }
        return;
        label101:
        if (!paramRecyclerView.hasPendingAdapterUpdates()) {
          localLayoutManager.collectAdjacentPrefetchPositions(this.mPrefetchDx, this.mPrefetchDy, paramRecyclerView.mState, this);
        }
      }
    }
    
    boolean lastPrefetchIncludedPosition(int paramInt)
    {
      int j;
      if (this.mPrefetchArray != null)
      {
        int i = this.mCount;
        j = 0;
        if (j < i * 2) {
          if (this.mPrefetchArray[j] != paramInt) {}
        }
      }
      for (boolean bool = true;; bool = false)
      {
        return bool;
        j += 2;
        break;
      }
    }
    
    void setPrefetchVector(int paramInt1, int paramInt2)
    {
      this.mPrefetchDx = paramInt1;
      this.mPrefetchDy = paramInt2;
    }
  }
  
  static class Task
  {
    public int distanceToItem;
    public boolean immediate;
    public int position;
    public RecyclerView view;
    public int viewVelocity;
    
    public void clear()
    {
      this.immediate = false;
      this.viewVelocity = 0;
      this.distanceToItem = 0;
      this.view = null;
      this.position = 0;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/GapWorker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */