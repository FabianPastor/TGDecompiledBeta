package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DrawerLayoutAdapter
  extends RecyclerListView.SelectionAdapter
{
  private ArrayList<Integer> accountNumbers = new ArrayList();
  private boolean accountsShowed;
  private ArrayList<Item> items = new ArrayList(11);
  private Context mContext;
  private DrawerProfileCell profileCell;
  
  public DrawerLayoutAdapter(Context paramContext)
  {
    this.mContext = paramContext;
    if ((UserConfig.getActivatedAccountsCount() > 1) && (MessagesController.getGlobalMainSettings().getBoolean("accountsShowed", true))) {}
    for (;;)
    {
      this.accountsShowed = bool;
      Theme.createDialogsResources(paramContext);
      resetItems();
      return;
      bool = false;
    }
  }
  
  private int getAccountRowsCount()
  {
    int i = this.accountNumbers.size() + 1;
    int j = i;
    if (this.accountNumbers.size() < 3) {
      j = i + 1;
    }
    return j;
  }
  
  private void resetItems()
  {
    this.accountNumbers.clear();
    for (int i = 0; i < 3; i++) {
      if (UserConfig.getInstance(i).isClientActivated()) {
        this.accountNumbers.add(Integer.valueOf(i));
      }
    }
    Collections.sort(this.accountNumbers, new Comparator()
    {
      public int compare(Integer paramAnonymousInteger1, Integer paramAnonymousInteger2)
      {
        long l1 = UserConfig.getInstance(paramAnonymousInteger1.intValue()).loginTime;
        long l2 = UserConfig.getInstance(paramAnonymousInteger2.intValue()).loginTime;
        int i;
        if (l1 > l2) {
          i = 1;
        }
        for (;;)
        {
          return i;
          if (l1 < l2) {
            i = -1;
          } else {
            i = 0;
          }
        }
      }
    });
    this.items.clear();
    if (!UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {}
    for (;;)
    {
      return;
      this.items.add(new Item(2, LocaleController.getString("NewGroup", NUM), NUM));
      this.items.add(new Item(3, LocaleController.getString("NewSecretChat", NUM), NUM));
      this.items.add(new Item(4, LocaleController.getString("NewChannel", NUM), NUM));
      this.items.add(null);
      this.items.add(new Item(6, LocaleController.getString("Contacts", NUM), NUM));
      this.items.add(new Item(11, LocaleController.getString("SavedMessages", NUM), NUM));
      this.items.add(new Item(10, LocaleController.getString("Calls", NUM), NUM));
      this.items.add(new Item(7, LocaleController.getString("InviteFriends", NUM), NUM));
      this.items.add(new Item(8, LocaleController.getString("Settings", NUM), NUM));
      this.items.add(new Item(9, LocaleController.getString("TelegramFAQ", NUM), NUM));
    }
  }
  
  public int getId(int paramInt)
  {
    int i = -1;
    int j = paramInt - 2;
    paramInt = j;
    if (this.accountsShowed) {
      paramInt = j - getAccountRowsCount();
    }
    j = i;
    if (paramInt >= 0)
    {
      if (paramInt < this.items.size()) {
        break label43;
      }
      j = i;
    }
    for (;;)
    {
      return j;
      label43:
      Item localItem = (Item)this.items.get(paramInt);
      j = i;
      if (localItem != null) {
        j = localItem.id;
      }
    }
  }
  
  public int getItemCount()
  {
    int i = this.items.size() + 2;
    int j = i;
    if (this.accountsShowed) {
      j = i + getAccountRowsCount();
    }
    return j;
  }
  
  public int getItemViewType(int paramInt)
  {
    int i = 1;
    if (paramInt == 0) {
      i = 0;
    }
    for (;;)
    {
      return i;
      if (paramInt != 1)
      {
        i = paramInt - 2;
        paramInt = i;
        if (this.accountsShowed)
        {
          if (i < this.accountNumbers.size())
          {
            i = 4;
          }
          else
          {
            if (this.accountNumbers.size() < 3)
            {
              if (i == this.accountNumbers.size())
              {
                i = 5;
                continue;
              }
              if (i == this.accountNumbers.size() + 1) {
                i = 2;
              }
            }
            else if (i == this.accountNumbers.size())
            {
              i = 2;
              continue;
            }
            paramInt = i - getAccountRowsCount();
          }
        }
        else if (paramInt == 3) {
          i = 2;
        } else {
          i = 3;
        }
      }
    }
  }
  
  public boolean isAccountsShowed()
  {
    return this.accountsShowed;
  }
  
  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    int i = paramViewHolder.getItemViewType();
    if ((i == 3) || (i == 4) || (i == 5)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void notifyDataSetChanged()
  {
    resetItems();
    super.notifyDataSetChanged();
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    switch (paramViewHolder.getItemViewType())
    {
    }
    for (;;)
    {
      return;
      ((DrawerProfileCell)paramViewHolder.itemView).setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())), this.accountsShowed);
      paramViewHolder.itemView.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
      continue;
      int i = paramInt - 2;
      paramInt = i;
      if (this.accountsShowed) {
        paramInt = i - getAccountRowsCount();
      }
      paramViewHolder = (DrawerActionCell)paramViewHolder.itemView;
      ((Item)this.items.get(paramInt)).bind(paramViewHolder);
      paramViewHolder.setPadding(0, 0, 0, 0);
      continue;
      ((DrawerUserCell)paramViewHolder.itemView).setAccount(((Integer)this.accountNumbers.get(paramInt - 2)).intValue());
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
    default: 
      paramViewGroup = new EmptyCell(this.mContext, AndroidUtilities.dp(8.0F));
    }
    for (;;)
    {
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
      return new RecyclerListView.Holder(paramViewGroup);
      this.profileCell = new DrawerProfileCell(this.mContext);
      this.profileCell.setOnArrowClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = (DrawerProfileCell)paramAnonymousView;
          DrawerLayoutAdapter.this.setAccountsShowed(paramAnonymousView.isAccountsShowed(), true);
        }
      });
      paramViewGroup = this.profileCell;
      continue;
      paramViewGroup = new DividerCell(this.mContext);
      continue;
      paramViewGroup = new DrawerActionCell(this.mContext);
      continue;
      paramViewGroup = new DrawerUserCell(this.mContext);
      continue;
      paramViewGroup = new DrawerAddCell(this.mContext);
    }
  }
  
  public void setAccountsShowed(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.accountsShowed == paramBoolean1) {}
    for (;;)
    {
      return;
      this.accountsShowed = paramBoolean1;
      if (this.profileCell != null) {
        this.profileCell.setAccountsShowed(this.accountsShowed);
      }
      MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShowed", this.accountsShowed).commit();
      if (paramBoolean2)
      {
        if (this.accountsShowed) {
          notifyItemRangeInserted(2, getAccountRowsCount());
        } else {
          notifyItemRangeRemoved(2, getAccountRowsCount());
        }
      }
      else {
        notifyDataSetChanged();
      }
    }
  }
  
  private class Item
  {
    public int icon;
    public int id;
    public String text;
    
    public Item(int paramInt1, String paramString, int paramInt2)
    {
      this.icon = paramInt2;
      this.id = paramInt1;
      this.text = paramString;
    }
    
    public void bind(DrawerActionCell paramDrawerActionCell)
    {
      paramDrawerActionCell.setTextAndIcon(this.text, this.icon);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/DrawerLayoutAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */