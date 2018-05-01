package org.telegram.messenger.support.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewPropertyAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultItemAnimator
  extends SimpleItemAnimator
{
  private static final boolean DEBUG = false;
  private static TimeInterpolator sDefaultInterpolator;
  private boolean delayAnimations = true;
  ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList();
  ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList();
  ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList();
  ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList();
  ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList();
  ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList();
  private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList();
  private ArrayList<ChangeInfo> mPendingChanges = new ArrayList();
  private ArrayList<MoveInfo> mPendingMoves = new ArrayList();
  private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList();
  ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList();
  
  private void animateRemoveImpl(final RecyclerView.ViewHolder paramViewHolder)
  {
    final View localView = paramViewHolder.itemView;
    final ViewPropertyAnimator localViewPropertyAnimator = localView.animate();
    this.mRemoveAnimations.add(paramViewHolder);
    localViewPropertyAnimator.setDuration(getRemoveDuration()).alpha(0.0F).setListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        localViewPropertyAnimator.setListener(null);
        localView.setAlpha(1.0F);
        DefaultItemAnimator.this.dispatchRemoveFinished(paramViewHolder);
        DefaultItemAnimator.this.mRemoveAnimations.remove(paramViewHolder);
        DefaultItemAnimator.this.dispatchFinishedWhenDone();
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        DefaultItemAnimator.this.dispatchRemoveStarting(paramViewHolder);
      }
    }).start();
  }
  
  private void endChangeAnimation(List<ChangeInfo> paramList, RecyclerView.ViewHolder paramViewHolder)
  {
    for (int i = paramList.size() - 1; i >= 0; i--)
    {
      ChangeInfo localChangeInfo = (ChangeInfo)paramList.get(i);
      if ((endChangeAnimationIfNecessary(localChangeInfo, paramViewHolder)) && (localChangeInfo.oldHolder == null) && (localChangeInfo.newHolder == null)) {
        paramList.remove(localChangeInfo);
      }
    }
  }
  
  private void endChangeAnimationIfNecessary(ChangeInfo paramChangeInfo)
  {
    if (paramChangeInfo.oldHolder != null) {
      endChangeAnimationIfNecessary(paramChangeInfo, paramChangeInfo.oldHolder);
    }
    if (paramChangeInfo.newHolder != null) {
      endChangeAnimationIfNecessary(paramChangeInfo, paramChangeInfo.newHolder);
    }
  }
  
  private boolean endChangeAnimationIfNecessary(ChangeInfo paramChangeInfo, RecyclerView.ViewHolder paramViewHolder)
  {
    boolean bool = false;
    if (paramChangeInfo.newHolder == paramViewHolder)
    {
      paramChangeInfo.newHolder = null;
      paramViewHolder.itemView.setAlpha(1.0F);
      paramViewHolder.itemView.setTranslationX(0.0F);
      paramViewHolder.itemView.setTranslationY(0.0F);
      dispatchChangeFinished(paramViewHolder, bool);
    }
    for (bool = true;; bool = false)
    {
      return bool;
      if (paramChangeInfo.oldHolder == paramViewHolder)
      {
        paramChangeInfo.oldHolder = null;
        bool = true;
        break;
      }
    }
  }
  
  private void resetAnimation(RecyclerView.ViewHolder paramViewHolder)
  {
    if (sDefaultInterpolator == null) {
      sDefaultInterpolator = new ValueAnimator().getInterpolator();
    }
    paramViewHolder.itemView.animate().setInterpolator(sDefaultInterpolator);
    endAnimation(paramViewHolder);
  }
  
  public boolean animateAdd(RecyclerView.ViewHolder paramViewHolder)
  {
    resetAnimation(paramViewHolder);
    paramViewHolder.itemView.setAlpha(0.0F);
    this.mPendingAdditions.add(paramViewHolder);
    return true;
  }
  
  void animateAddImpl(final RecyclerView.ViewHolder paramViewHolder)
  {
    final View localView = paramViewHolder.itemView;
    final ViewPropertyAnimator localViewPropertyAnimator = localView.animate();
    this.mAddAnimations.add(paramViewHolder);
    localViewPropertyAnimator.alpha(1.0F).setDuration(getAddDuration()).setListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        localView.setAlpha(1.0F);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        localViewPropertyAnimator.setListener(null);
        DefaultItemAnimator.this.dispatchAddFinished(paramViewHolder);
        DefaultItemAnimator.this.mAddAnimations.remove(paramViewHolder);
        DefaultItemAnimator.this.dispatchFinishedWhenDone();
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        DefaultItemAnimator.this.dispatchAddStarting(paramViewHolder);
      }
    }).start();
  }
  
  public boolean animateChange(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramViewHolder1 == paramViewHolder2) {}
    for (boolean bool = animateMove(paramViewHolder1, paramInt1, paramInt2, paramInt3, paramInt4);; bool = true)
    {
      return bool;
      float f1 = paramViewHolder1.itemView.getTranslationX();
      float f2 = paramViewHolder1.itemView.getTranslationY();
      float f3 = paramViewHolder1.itemView.getAlpha();
      resetAnimation(paramViewHolder1);
      int i = (int)(paramInt3 - paramInt1 - f1);
      int j = (int)(paramInt4 - paramInt2 - f2);
      paramViewHolder1.itemView.setTranslationX(f1);
      paramViewHolder1.itemView.setTranslationY(f2);
      paramViewHolder1.itemView.setAlpha(f3);
      if (paramViewHolder2 != null)
      {
        resetAnimation(paramViewHolder2);
        paramViewHolder2.itemView.setTranslationX(-i);
        paramViewHolder2.itemView.setTranslationY(-j);
        paramViewHolder2.itemView.setAlpha(0.0F);
      }
      this.mPendingChanges.add(new ChangeInfo(paramViewHolder1, paramViewHolder2, paramInt1, paramInt2, paramInt3, paramInt4));
    }
  }
  
  void animateChangeImpl(final ChangeInfo paramChangeInfo)
  {
    final Object localObject1 = paramChangeInfo.oldHolder;
    if (localObject1 == null)
    {
      localObject1 = null;
      localObject2 = paramChangeInfo.newHolder;
      if (localObject2 == null) {
        break label177;
      }
    }
    label177:
    for (final Object localObject2 = ((RecyclerView.ViewHolder)localObject2).itemView;; localObject2 = null)
    {
      if (localObject1 != null)
      {
        final ViewPropertyAnimator localViewPropertyAnimator = ((View)localObject1).animate().setDuration(getChangeDuration());
        this.mChangeAnimations.add(paramChangeInfo.oldHolder);
        localViewPropertyAnimator.translationX(paramChangeInfo.toX - paramChangeInfo.fromX);
        localViewPropertyAnimator.translationY(paramChangeInfo.toY - paramChangeInfo.fromY);
        localViewPropertyAnimator.alpha(0.0F).setListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            localViewPropertyAnimator.setListener(null);
            localObject1.setAlpha(1.0F);
            localObject1.setTranslationX(0.0F);
            localObject1.setTranslationY(0.0F);
            DefaultItemAnimator.this.dispatchChangeFinished(paramChangeInfo.oldHolder, true);
            DefaultItemAnimator.this.mChangeAnimations.remove(paramChangeInfo.oldHolder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
          }
          
          public void onAnimationStart(Animator paramAnonymousAnimator)
          {
            DefaultItemAnimator.this.dispatchChangeStarting(paramChangeInfo.oldHolder, true);
          }
        }).start();
      }
      if (localObject2 != null)
      {
        localObject1 = ((View)localObject2).animate();
        this.mChangeAnimations.add(paramChangeInfo.newHolder);
        ((ViewPropertyAnimator)localObject1).translationX(0.0F).translationY(0.0F).setDuration(getChangeDuration()).alpha(1.0F).setListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            localObject1.setListener(null);
            localObject2.setAlpha(1.0F);
            localObject2.setTranslationX(0.0F);
            localObject2.setTranslationY(0.0F);
            DefaultItemAnimator.this.dispatchChangeFinished(paramChangeInfo.newHolder, false);
            DefaultItemAnimator.this.mChangeAnimations.remove(paramChangeInfo.newHolder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
          }
          
          public void onAnimationStart(Animator paramAnonymousAnimator)
          {
            DefaultItemAnimator.this.dispatchChangeStarting(paramChangeInfo.newHolder, false);
          }
        }).start();
      }
      return;
      localObject1 = ((RecyclerView.ViewHolder)localObject1).itemView;
      break;
    }
  }
  
  public boolean animateMove(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    View localView = paramViewHolder.itemView;
    paramInt1 += (int)paramViewHolder.itemView.getTranslationX();
    int i = paramInt2 + (int)paramViewHolder.itemView.getTranslationY();
    resetAnimation(paramViewHolder);
    int j = paramInt3 - paramInt1;
    paramInt2 = paramInt4 - i;
    if ((j == 0) && (paramInt2 == 0)) {
      dispatchMoveFinished(paramViewHolder);
    }
    for (boolean bool = false;; bool = true)
    {
      return bool;
      if (j != 0) {
        localView.setTranslationX(-j);
      }
      if (paramInt2 != 0) {
        localView.setTranslationY(-paramInt2);
      }
      this.mPendingMoves.add(new MoveInfo(paramViewHolder, paramInt1, i, paramInt3, paramInt4));
    }
  }
  
  void animateMoveImpl(final RecyclerView.ViewHolder paramViewHolder, final int paramInt1, final int paramInt2, int paramInt3, int paramInt4)
  {
    final View localView = paramViewHolder.itemView;
    paramInt1 = paramInt3 - paramInt1;
    paramInt2 = paramInt4 - paramInt2;
    if (paramInt1 != 0) {
      localView.animate().translationX(0.0F);
    }
    if (paramInt2 != 0) {
      localView.animate().translationY(0.0F);
    }
    final ViewPropertyAnimator localViewPropertyAnimator = localView.animate();
    this.mMoveAnimations.add(paramViewHolder);
    localViewPropertyAnimator.setDuration(getMoveDuration()).setListener(new AnimatorListenerAdapter()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if (paramInt1 != 0) {
          localView.setTranslationX(0.0F);
        }
        if (paramInt2 != 0) {
          localView.setTranslationY(0.0F);
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        localViewPropertyAnimator.setListener(null);
        DefaultItemAnimator.this.dispatchMoveFinished(paramViewHolder);
        DefaultItemAnimator.this.mMoveAnimations.remove(paramViewHolder);
        DefaultItemAnimator.this.dispatchFinishedWhenDone();
      }
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        DefaultItemAnimator.this.dispatchMoveStarting(paramViewHolder);
      }
    }).start();
  }
  
  public boolean animateRemove(RecyclerView.ViewHolder paramViewHolder)
  {
    resetAnimation(paramViewHolder);
    this.mPendingRemovals.add(paramViewHolder);
    return true;
  }
  
  public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder paramViewHolder, List<Object> paramList)
  {
    if ((!paramList.isEmpty()) || (super.canReuseUpdatedViewHolder(paramViewHolder, paramList))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  void cancelAll(List<RecyclerView.ViewHolder> paramList)
  {
    for (int i = paramList.size() - 1; i >= 0; i--) {
      ((RecyclerView.ViewHolder)paramList.get(i)).itemView.animate().cancel();
    }
  }
  
  void dispatchFinishedWhenDone()
  {
    if (!isRunning()) {
      dispatchAnimationsFinished();
    }
  }
  
  public void endAnimation(RecyclerView.ViewHolder paramViewHolder)
  {
    View localView = paramViewHolder.itemView;
    localView.animate().cancel();
    for (int i = this.mPendingMoves.size() - 1; i >= 0; i--) {
      if (((MoveInfo)this.mPendingMoves.get(i)).holder == paramViewHolder)
      {
        localView.setTranslationY(0.0F);
        localView.setTranslationX(0.0F);
        dispatchMoveFinished(paramViewHolder);
        this.mPendingMoves.remove(i);
      }
    }
    endChangeAnimation(this.mPendingChanges, paramViewHolder);
    if (this.mPendingRemovals.remove(paramViewHolder))
    {
      localView.setAlpha(1.0F);
      dispatchRemoveFinished(paramViewHolder);
    }
    if (this.mPendingAdditions.remove(paramViewHolder))
    {
      localView.setAlpha(1.0F);
      dispatchAddFinished(paramViewHolder);
    }
    ArrayList localArrayList;
    for (i = this.mChangesList.size() - 1; i >= 0; i--)
    {
      localArrayList = (ArrayList)this.mChangesList.get(i);
      endChangeAnimation(localArrayList, paramViewHolder);
      if (localArrayList.isEmpty()) {
        this.mChangesList.remove(i);
      }
    }
    i = this.mMovesList.size() - 1;
    if (i >= 0)
    {
      localArrayList = (ArrayList)this.mMovesList.get(i);
      for (int j = localArrayList.size() - 1;; j--)
      {
        if (j >= 0)
        {
          if (((MoveInfo)localArrayList.get(j)).holder != paramViewHolder) {
            continue;
          }
          localView.setTranslationY(0.0F);
          localView.setTranslationX(0.0F);
          dispatchMoveFinished(paramViewHolder);
          localArrayList.remove(j);
          if (localArrayList.isEmpty()) {
            this.mMovesList.remove(i);
          }
        }
        i--;
        break;
      }
    }
    for (i = this.mAdditionsList.size() - 1; i >= 0; i--)
    {
      localArrayList = (ArrayList)this.mAdditionsList.get(i);
      if (localArrayList.remove(paramViewHolder))
      {
        localView.setAlpha(1.0F);
        dispatchAddFinished(paramViewHolder);
        if (localArrayList.isEmpty()) {
          this.mAdditionsList.remove(i);
        }
      }
    }
    if ((!this.mRemoveAnimations.remove(paramViewHolder)) || ((!this.mAddAnimations.remove(paramViewHolder)) || ((!this.mChangeAnimations.remove(paramViewHolder)) || (this.mMoveAnimations.remove(paramViewHolder))))) {}
    dispatchFinishedWhenDone();
  }
  
  public void endAnimations()
  {
    Object localObject1;
    Object localObject2;
    for (int i = this.mPendingMoves.size() - 1; i >= 0; i--)
    {
      localObject1 = (MoveInfo)this.mPendingMoves.get(i);
      localObject2 = ((MoveInfo)localObject1).holder.itemView;
      ((View)localObject2).setTranslationY(0.0F);
      ((View)localObject2).setTranslationX(0.0F);
      dispatchMoveFinished(((MoveInfo)localObject1).holder);
      this.mPendingMoves.remove(i);
    }
    for (i = this.mPendingRemovals.size() - 1; i >= 0; i--)
    {
      dispatchRemoveFinished((RecyclerView.ViewHolder)this.mPendingRemovals.get(i));
      this.mPendingRemovals.remove(i);
    }
    for (i = this.mPendingAdditions.size() - 1; i >= 0; i--)
    {
      localObject1 = (RecyclerView.ViewHolder)this.mPendingAdditions.get(i);
      ((RecyclerView.ViewHolder)localObject1).itemView.setAlpha(1.0F);
      dispatchAddFinished((RecyclerView.ViewHolder)localObject1);
      this.mPendingAdditions.remove(i);
    }
    for (i = this.mPendingChanges.size() - 1; i >= 0; i--) {
      endChangeAnimationIfNecessary((ChangeInfo)this.mPendingChanges.get(i));
    }
    this.mPendingChanges.clear();
    if (!isRunning()) {}
    for (;;)
    {
      return;
      int j;
      for (i = this.mMovesList.size() - 1; i >= 0; i--)
      {
        localObject2 = (ArrayList)this.mMovesList.get(i);
        for (j = ((ArrayList)localObject2).size() - 1; j >= 0; j--)
        {
          MoveInfo localMoveInfo = (MoveInfo)((ArrayList)localObject2).get(j);
          localObject1 = localMoveInfo.holder.itemView;
          ((View)localObject1).setTranslationY(0.0F);
          ((View)localObject1).setTranslationX(0.0F);
          dispatchMoveFinished(localMoveInfo.holder);
          ((ArrayList)localObject2).remove(j);
          if (((ArrayList)localObject2).isEmpty()) {
            this.mMovesList.remove(localObject2);
          }
        }
      }
      for (i = this.mAdditionsList.size() - 1; i >= 0; i--)
      {
        localObject2 = (ArrayList)this.mAdditionsList.get(i);
        for (j = ((ArrayList)localObject2).size() - 1; j >= 0; j--)
        {
          localObject1 = (RecyclerView.ViewHolder)((ArrayList)localObject2).get(j);
          ((RecyclerView.ViewHolder)localObject1).itemView.setAlpha(1.0F);
          dispatchAddFinished((RecyclerView.ViewHolder)localObject1);
          ((ArrayList)localObject2).remove(j);
          if (((ArrayList)localObject2).isEmpty()) {
            this.mAdditionsList.remove(localObject2);
          }
        }
      }
      for (i = this.mChangesList.size() - 1; i >= 0; i--)
      {
        localObject1 = (ArrayList)this.mChangesList.get(i);
        for (j = ((ArrayList)localObject1).size() - 1; j >= 0; j--)
        {
          endChangeAnimationIfNecessary((ChangeInfo)((ArrayList)localObject1).get(j));
          if (((ArrayList)localObject1).isEmpty()) {
            this.mChangesList.remove(localObject1);
          }
        }
      }
      cancelAll(this.mRemoveAnimations);
      cancelAll(this.mMoveAnimations);
      cancelAll(this.mAddAnimations);
      cancelAll(this.mChangeAnimations);
      dispatchAnimationsFinished();
    }
  }
  
  public boolean isRunning()
  {
    if ((!this.mPendingAdditions.isEmpty()) || (!this.mPendingChanges.isEmpty()) || (!this.mPendingMoves.isEmpty()) || (!this.mPendingRemovals.isEmpty()) || (!this.mMoveAnimations.isEmpty()) || (!this.mRemoveAnimations.isEmpty()) || (!this.mAddAnimations.isEmpty()) || (!this.mChangeAnimations.isEmpty()) || (!this.mMovesList.isEmpty()) || (!this.mAdditionsList.isEmpty()) || (!this.mChangesList.isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void runPendingAnimations()
  {
    int i;
    int j;
    label24:
    int k;
    label36:
    int m;
    if (!this.mPendingRemovals.isEmpty())
    {
      i = 1;
      if (this.mPendingMoves.isEmpty()) {
        break label72;
      }
      j = 1;
      if (this.mPendingChanges.isEmpty()) {
        break label77;
      }
      k = 1;
      if (this.mPendingAdditions.isEmpty()) {
        break label82;
      }
      m = 1;
      label49:
      if ((i != 0) || (j != 0) || (m != 0) || (k != 0)) {
        break label88;
      }
    }
    for (;;)
    {
      return;
      i = 0;
      break;
      label72:
      j = 0;
      break label24;
      label77:
      k = 0;
      break label36;
      label82:
      m = 0;
      break label49;
      label88:
      Object localObject = this.mPendingRemovals.iterator();
      while (((Iterator)localObject).hasNext()) {
        animateRemoveImpl((RecyclerView.ViewHolder)((Iterator)localObject).next());
      }
      this.mPendingRemovals.clear();
      final ArrayList localArrayList;
      label218:
      label298:
      long l1;
      label380:
      long l2;
      if (j != 0)
      {
        localArrayList = new ArrayList();
        localArrayList.addAll(this.mPendingMoves);
        this.mMovesList.add(localArrayList);
        this.mPendingMoves.clear();
        localObject = new Runnable()
        {
          public void run()
          {
            Iterator localIterator = localArrayList.iterator();
            while (localIterator.hasNext())
            {
              DefaultItemAnimator.MoveInfo localMoveInfo = (DefaultItemAnimator.MoveInfo)localIterator.next();
              DefaultItemAnimator.this.animateMoveImpl(localMoveInfo.holder, localMoveInfo.fromX, localMoveInfo.fromY, localMoveInfo.toX, localMoveInfo.toY);
            }
            localArrayList.clear();
            DefaultItemAnimator.this.mMovesList.remove(localArrayList);
          }
        };
        if ((this.delayAnimations) && (i != 0)) {
          ViewCompat.postOnAnimationDelayed(((MoveInfo)localArrayList.get(0)).holder.itemView, (Runnable)localObject, getRemoveDuration());
        }
      }
      else
      {
        if (k != 0)
        {
          localArrayList = new ArrayList();
          localArrayList.addAll(this.mPendingChanges);
          this.mChangesList.add(localArrayList);
          this.mPendingChanges.clear();
          localObject = new Runnable()
          {
            public void run()
            {
              Iterator localIterator = localArrayList.iterator();
              while (localIterator.hasNext())
              {
                DefaultItemAnimator.ChangeInfo localChangeInfo = (DefaultItemAnimator.ChangeInfo)localIterator.next();
                DefaultItemAnimator.this.animateChangeImpl(localChangeInfo);
              }
              localArrayList.clear();
              DefaultItemAnimator.this.mChangesList.remove(localArrayList);
            }
          };
          if (i == 0) {
            break label444;
          }
          ViewCompat.postOnAnimationDelayed(((ChangeInfo)localArrayList.get(0)).oldHolder.itemView, (Runnable)localObject, getRemoveDuration());
        }
        if (m == 0) {
          break label452;
        }
        localArrayList = new ArrayList();
        localArrayList.addAll(this.mPendingAdditions);
        this.mAdditionsList.add(localArrayList);
        this.mPendingAdditions.clear();
        localObject = new Runnable()
        {
          public void run()
          {
            Iterator localIterator = localArrayList.iterator();
            while (localIterator.hasNext())
            {
              RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)localIterator.next();
              DefaultItemAnimator.this.animateAddImpl(localViewHolder);
            }
            localArrayList.clear();
            DefaultItemAnimator.this.mAdditionsList.remove(localArrayList);
          }
        };
        if ((!this.delayAnimations) || ((i == 0) && (j == 0) && (k == 0))) {
          break label472;
        }
        if (i == 0) {
          break label454;
        }
        l1 = getRemoveDuration();
        if (j == 0) {
          break label460;
        }
        l2 = getMoveDuration();
        label390:
        if (k == 0) {
          break label466;
        }
      }
      label444:
      label452:
      label454:
      label460:
      label466:
      for (long l3 = getChangeDuration();; l3 = 0L)
      {
        l2 = Math.max(l2, l3);
        ViewCompat.postOnAnimationDelayed(((RecyclerView.ViewHolder)localArrayList.get(0)).itemView, (Runnable)localObject, l1 + l2);
        break;
        ((Runnable)localObject).run();
        break label218;
        ((Runnable)localObject).run();
        break label298;
        break;
        l1 = 0L;
        break label380;
        l2 = 0L;
        break label390;
      }
      label472:
      ((Runnable)localObject).run();
    }
  }
  
  public void setDelayAnimations(boolean paramBoolean)
  {
    this.delayAnimations = paramBoolean;
  }
  
  private static class ChangeInfo
  {
    public int fromX;
    public int fromY;
    public RecyclerView.ViewHolder newHolder;
    public RecyclerView.ViewHolder oldHolder;
    public int toX;
    public int toY;
    
    private ChangeInfo(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      this.oldHolder = paramViewHolder1;
      this.newHolder = paramViewHolder2;
    }
    
    ChangeInfo(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this(paramViewHolder1, paramViewHolder2);
      this.fromX = paramInt1;
      this.fromY = paramInt2;
      this.toX = paramInt3;
      this.toY = paramInt4;
    }
    
    public String toString()
    {
      return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
    }
  }
  
  private static class MoveInfo
  {
    public int fromX;
    public int fromY;
    public RecyclerView.ViewHolder holder;
    public int toX;
    public int toY;
    
    MoveInfo(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.holder = paramViewHolder;
      this.fromX = paramInt1;
      this.fromY = paramInt2;
      this.toX = paramInt3;
      this.toY = paramInt4;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/DefaultItemAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */