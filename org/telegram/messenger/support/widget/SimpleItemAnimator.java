package org.telegram.messenger.support.widget;

import android.view.View;

public abstract class SimpleItemAnimator
  extends RecyclerView.ItemAnimator
{
  private static final boolean DEBUG = false;
  private static final String TAG = "SimpleItemAnimator";
  boolean mSupportsChangeAnimations = true;
  
  public abstract boolean animateAdd(RecyclerView.ViewHolder paramViewHolder);
  
  public boolean animateAppearance(RecyclerView.ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    if ((paramItemHolderInfo1 != null) && ((paramItemHolderInfo1.left != paramItemHolderInfo2.left) || (paramItemHolderInfo1.top != paramItemHolderInfo2.top))) {}
    for (boolean bool = animateMove(paramViewHolder, paramItemHolderInfo1.left, paramItemHolderInfo1.top, paramItemHolderInfo2.left, paramItemHolderInfo2.top);; bool = animateAdd(paramViewHolder)) {
      return bool;
    }
  }
  
  public abstract boolean animateChange(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public boolean animateChange(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    int i = paramItemHolderInfo1.left;
    int j = paramItemHolderInfo1.top;
    int k;
    if (paramViewHolder2.shouldIgnore()) {
      k = paramItemHolderInfo1.left;
    }
    for (int m = paramItemHolderInfo1.top;; m = paramItemHolderInfo2.top)
    {
      return animateChange(paramViewHolder1, paramViewHolder2, i, j, k, m);
      k = paramItemHolderInfo2.left;
    }
  }
  
  public boolean animateDisappearance(RecyclerView.ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    int i = paramItemHolderInfo1.left;
    int j = paramItemHolderInfo1.top;
    paramItemHolderInfo1 = paramViewHolder.itemView;
    int k;
    int m;
    if (paramItemHolderInfo2 == null)
    {
      k = paramItemHolderInfo1.getLeft();
      if (paramItemHolderInfo2 != null) {
        break label107;
      }
      m = paramItemHolderInfo1.getTop();
      label37:
      if ((paramViewHolder.isRemoved()) || ((i == k) && (j == m))) {
        break label116;
      }
      paramItemHolderInfo1.layout(k, m, paramItemHolderInfo1.getWidth() + k, paramItemHolderInfo1.getHeight() + m);
    }
    label107:
    label116:
    for (boolean bool = animateMove(paramViewHolder, i, j, k, m);; bool = animateRemove(paramViewHolder))
    {
      return bool;
      k = paramItemHolderInfo2.left;
      break;
      m = paramItemHolderInfo2.top;
      break label37;
    }
  }
  
  public abstract boolean animateMove(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public boolean animatePersistence(RecyclerView.ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    if ((paramItemHolderInfo1.left != paramItemHolderInfo2.left) || (paramItemHolderInfo1.top != paramItemHolderInfo2.top)) {}
    for (boolean bool = animateMove(paramViewHolder, paramItemHolderInfo1.left, paramItemHolderInfo1.top, paramItemHolderInfo2.left, paramItemHolderInfo2.top);; bool = false)
    {
      return bool;
      dispatchMoveFinished(paramViewHolder);
    }
  }
  
  public abstract boolean animateRemove(RecyclerView.ViewHolder paramViewHolder);
  
  public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder paramViewHolder)
  {
    if ((!this.mSupportsChangeAnimations) || (paramViewHolder.isInvalid())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final void dispatchAddFinished(RecyclerView.ViewHolder paramViewHolder)
  {
    onAddFinished(paramViewHolder);
    dispatchAnimationFinished(paramViewHolder);
  }
  
  public final void dispatchAddStarting(RecyclerView.ViewHolder paramViewHolder)
  {
    onAddStarting(paramViewHolder);
  }
  
  public final void dispatchChangeFinished(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    onChangeFinished(paramViewHolder, paramBoolean);
    dispatchAnimationFinished(paramViewHolder);
  }
  
  public final void dispatchChangeStarting(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    onChangeStarting(paramViewHolder, paramBoolean);
  }
  
  public final void dispatchMoveFinished(RecyclerView.ViewHolder paramViewHolder)
  {
    onMoveFinished(paramViewHolder);
    dispatchAnimationFinished(paramViewHolder);
  }
  
  public final void dispatchMoveStarting(RecyclerView.ViewHolder paramViewHolder)
  {
    onMoveStarting(paramViewHolder);
  }
  
  public final void dispatchRemoveFinished(RecyclerView.ViewHolder paramViewHolder)
  {
    onRemoveFinished(paramViewHolder);
    dispatchAnimationFinished(paramViewHolder);
  }
  
  public final void dispatchRemoveStarting(RecyclerView.ViewHolder paramViewHolder)
  {
    onRemoveStarting(paramViewHolder);
  }
  
  public boolean getSupportsChangeAnimations()
  {
    return this.mSupportsChangeAnimations;
  }
  
  public void onAddFinished(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void onAddStarting(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void onChangeFinished(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean) {}
  
  public void onChangeStarting(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean) {}
  
  public void onMoveFinished(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void onMoveStarting(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void onRemoveFinished(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void onRemoveStarting(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void setSupportsChangeAnimations(boolean paramBoolean)
  {
    this.mSupportsChangeAnimations = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/SimpleItemAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */