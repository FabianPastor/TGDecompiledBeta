package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.LoadingCell;

public class DialogsAdapter
  extends RecyclerView.Adapter
{
  private int currentCount;
  private int dialogsType;
  private Context mContext;
  private long openedDialogId;
  
  public DialogsAdapter(Context paramContext, int paramInt)
  {
    this.mContext = paramContext;
    this.dialogsType = paramInt;
  }
  
  private ArrayList<TLRPC.TL_dialog> getDialogsArray()
  {
    if (this.dialogsType == 0) {
      return MessagesController.getInstance().dialogs;
    }
    if (this.dialogsType == 1) {
      return MessagesController.getInstance().dialogsServerOnly;
    }
    if (this.dialogsType == 2) {
      return MessagesController.getInstance().dialogsGroupsOnly;
    }
    return null;
  }
  
  public TLRPC.TL_dialog getItem(int paramInt)
  {
    ArrayList localArrayList = getDialogsArray();
    if ((paramInt < 0) || (paramInt >= localArrayList.size())) {
      return null;
    }
    return (TLRPC.TL_dialog)localArrayList.get(paramInt);
  }
  
  public int getItemCount()
  {
    int j = getDialogsArray().size();
    if ((j == 0) && (MessagesController.getInstance().loadingDialogs)) {
      return 0;
    }
    int i = j;
    if (!MessagesController.getInstance().dialogsEndReached) {
      i = j + 1;
    }
    this.currentCount = i;
    return i;
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    if (paramInt == getDialogsArray().size()) {
      return 1;
    }
    return 0;
  }
  
  public boolean isDataSetChanged()
  {
    int i = this.currentCount;
    return (i != getItemCount()) || (i == 1);
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    boolean bool2 = true;
    TLRPC.TL_dialog localTL_dialog;
    if (paramViewHolder.getItemViewType() == 0)
    {
      paramViewHolder = (DialogCell)paramViewHolder.itemView;
      if (paramInt == getItemCount() - 1) {
        break label88;
      }
      bool1 = true;
      paramViewHolder.useSeparator = bool1;
      localTL_dialog = getItem(paramInt);
      if ((this.dialogsType == 0) && (AndroidUtilities.isTablet())) {
        if (localTL_dialog.id != this.openedDialogId) {
          break label93;
        }
      }
    }
    label88:
    label93:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      paramViewHolder.setDialogSelected(bool1);
      paramViewHolder.setDialog(localTL_dialog, paramInt, this.dialogsType);
      return;
      bool1 = false;
      break;
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    paramViewGroup = null;
    if (paramInt == 0) {
      paramViewGroup = new DialogCell(this.mContext);
    }
    for (;;)
    {
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
      return new Holder(paramViewGroup);
      if (paramInt == 1) {
        paramViewGroup = new LoadingCell(this.mContext);
      }
    }
  }
  
  public void onViewAttachedToWindow(RecyclerView.ViewHolder paramViewHolder)
  {
    if ((paramViewHolder.itemView instanceof DialogCell)) {
      ((DialogCell)paramViewHolder.itemView).checkCurrentDialogIndex();
    }
  }
  
  public void setOpenedDialogId(long paramLong)
  {
    this.openedDialogId = paramLong;
  }
  
  private class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/DialogsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */