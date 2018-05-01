package org.telegram.ui.Adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;

public class ContactsAdapter
  extends RecyclerListView.SectionsAdapter
{
  private SparseArray<?> checkedMap;
  private int currentAccount = UserConfig.selectedAccount;
  private SparseArray<TLRPC.User> ignoreUsers;
  private boolean isAdmin;
  private Context mContext;
  private boolean needPhonebook;
  private int onlyUsers;
  private boolean scrolling;
  
  public ContactsAdapter(Context paramContext, int paramInt, boolean paramBoolean1, SparseArray<TLRPC.User> paramSparseArray, boolean paramBoolean2)
  {
    this.mContext = paramContext;
    this.onlyUsers = paramInt;
    this.needPhonebook = paramBoolean1;
    this.ignoreUsers = paramSparseArray;
    this.isAdmin = paramBoolean2;
  }
  
  public int getCountForSection(int paramInt)
  {
    int i = 2;
    HashMap localHashMap;
    ArrayList localArrayList;
    if (this.onlyUsers == 2)
    {
      localHashMap = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
      if (this.onlyUsers != 2) {
        break label121;
      }
      localArrayList = ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray;
      label41:
      if ((this.onlyUsers == 0) || (this.isAdmin)) {
        break label136;
      }
      if (paramInt >= localArrayList.size()) {
        break label222;
      }
      i = ((ArrayList)localHashMap.get(localArrayList.get(paramInt))).size();
      if (paramInt == localArrayList.size() - 1)
      {
        paramInt = i;
        if (!this.needPhonebook) {}
      }
      else
      {
        paramInt = i + 1;
      }
    }
    for (;;)
    {
      return paramInt;
      localHashMap = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
      break;
      label121:
      localArrayList = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
      break label41;
      label136:
      if (paramInt == 0)
      {
        paramInt = i;
        if (!this.needPhonebook)
        {
          paramInt = i;
          if (!this.isAdmin) {
            paramInt = 4;
          }
        }
      }
      else if (paramInt - 1 < localArrayList.size())
      {
        i = ((ArrayList)localHashMap.get(localArrayList.get(paramInt - 1))).size();
        if (paramInt - 1 == localArrayList.size() - 1)
        {
          paramInt = i;
          if (!this.needPhonebook) {}
        }
        else
        {
          paramInt = i + 1;
        }
      }
      else
      {
        label222:
        if (this.needPhonebook) {
          paramInt = ContactsController.getInstance(this.currentAccount).phoneBookContacts.size();
        } else {
          paramInt = 0;
        }
      }
    }
  }
  
  public Object getItem(int paramInt1, int paramInt2)
  {
    Object localObject1 = null;
    Object localObject2;
    ArrayList localArrayList;
    label42:
    Object localObject3;
    if (this.onlyUsers == 2)
    {
      localObject2 = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
      if (this.onlyUsers != 2) {
        break label141;
      }
      localArrayList = ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray;
      if ((this.onlyUsers == 0) || (this.isAdmin)) {
        break label156;
      }
      localObject3 = localObject1;
      if (paramInt1 < localArrayList.size())
      {
        localObject2 = (ArrayList)((HashMap)localObject2).get(localArrayList.get(paramInt1));
        localObject3 = localObject1;
        if (paramInt2 < ((ArrayList)localObject2).size()) {
          localObject3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)((ArrayList)localObject2).get(paramInt2)).user_id));
        }
      }
    }
    for (;;)
    {
      return localObject3;
      localObject2 = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
      break;
      label141:
      localArrayList = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
      break label42;
      label156:
      localObject3 = localObject1;
      if (paramInt1 != 0) {
        if (paramInt1 - 1 < localArrayList.size())
        {
          localObject2 = (ArrayList)((HashMap)localObject2).get(localArrayList.get(paramInt1 - 1));
          localObject3 = localObject1;
          if (paramInt2 < ((ArrayList)localObject2).size()) {
            localObject3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)((ArrayList)localObject2).get(paramInt2)).user_id));
          }
        }
        else
        {
          localObject3 = localObject1;
          if (this.needPhonebook) {
            localObject3 = ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(paramInt2);
          }
        }
      }
    }
  }
  
  public int getItemViewType(int paramInt1, int paramInt2)
  {
    int i = 0;
    HashMap localHashMap;
    ArrayList localArrayList;
    if (this.onlyUsers == 2)
    {
      localHashMap = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
      if (this.onlyUsers != 2) {
        break label94;
      }
      localArrayList = ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray;
      label42:
      if ((this.onlyUsers == 0) || (this.isAdmin)) {
        break label114;
      }
      if (paramInt2 >= ((ArrayList)localHashMap.get(localArrayList.get(paramInt1))).size()) {
        break label109;
      }
    }
    for (;;)
    {
      return i;
      localHashMap = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
      break;
      label94:
      localArrayList = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
      break label42;
      label109:
      i = 3;
      continue;
      label114:
      if (paramInt1 == 0)
      {
        if (((!this.needPhonebook) && (!this.isAdmin)) || ((paramInt2 == 1) || (paramInt2 == 3))) {
          i = 2;
        }
      }
      else if (paramInt1 - 1 < localArrayList.size())
      {
        if (paramInt2 < ((ArrayList)localHashMap.get(localArrayList.get(paramInt1 - 1))).size()) {
          continue;
        }
        i = 3;
        continue;
      }
      i = 1;
    }
  }
  
  public String getLetter(int paramInt)
  {
    if (this.onlyUsers == 2)
    {
      localObject = ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray;
      int i = getSectionForPosition(paramInt);
      paramInt = i;
      if (i == -1) {
        paramInt = ((ArrayList)localObject).size() - 1;
      }
      if ((paramInt <= 0) || (paramInt > ((ArrayList)localObject).size())) {
        break label78;
      }
    }
    label78:
    for (Object localObject = (String)((ArrayList)localObject).get(paramInt - 1);; localObject = null)
    {
      return (String)localObject;
      localObject = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
      break;
    }
  }
  
  public int getPositionForScrollProgress(float paramFloat)
  {
    return (int)(getItemCount() * paramFloat);
  }
  
  public int getSectionCount()
  {
    if (this.onlyUsers == 2) {}
    for (ArrayList localArrayList = ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray;; localArrayList = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray)
    {
      int i = localArrayList.size();
      int j = i;
      if (this.onlyUsers == 0) {
        j = i + 1;
      }
      i = j;
      if (this.isAdmin) {
        i = j + 1;
      }
      j = i;
      if (this.needPhonebook) {
        j = i + 1;
      }
      return j;
    }
  }
  
  public View getSectionHeaderView(int paramInt, View paramView)
  {
    Object localObject1;
    label38:
    Object localObject2;
    if (this.onlyUsers == 2)
    {
      localObject1 = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
      if (this.onlyUsers != 2) {
        break label115;
      }
      localObject1 = ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray;
      localObject2 = paramView;
      if (paramView == null) {
        localObject2 = new LetterSectionCell(this.mContext);
      }
      paramView = (LetterSectionCell)localObject2;
      if ((this.onlyUsers == 0) || (this.isAdmin)) {
        break label138;
      }
      if (paramInt >= ((ArrayList)localObject1).size()) {
        break label129;
      }
      paramView.setLetter((String)((ArrayList)localObject1).get(paramInt));
    }
    for (;;)
    {
      return (View)localObject2;
      localObject1 = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
      break;
      label115:
      localObject1 = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
      break label38;
      label129:
      paramView.setLetter("");
      continue;
      label138:
      if (paramInt == 0) {
        paramView.setLetter("");
      } else if (paramInt - 1 < ((ArrayList)localObject1).size()) {
        paramView.setLetter((String)((ArrayList)localObject1).get(paramInt - 1));
      } else {
        paramView.setLetter("");
      }
    }
  }
  
  public boolean isEnabled(int paramInt1, int paramInt2)
  {
    boolean bool1 = true;
    HashMap localHashMap;
    ArrayList localArrayList;
    label42:
    boolean bool2;
    if (this.onlyUsers == 2)
    {
      localHashMap = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
      if (this.onlyUsers != 2) {
        break label98;
      }
      localArrayList = ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray;
      if ((this.onlyUsers == 0) || (this.isAdmin)) {
        break label119;
      }
      if (paramInt2 >= ((ArrayList)localHashMap.get(localArrayList.get(paramInt1))).size()) {
        break label113;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      localHashMap = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
      break;
      label98:
      localArrayList = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
      break label42;
      label113:
      bool2 = false;
      continue;
      label119:
      if (paramInt1 == 0)
      {
        if ((this.needPhonebook) || (this.isAdmin))
        {
          bool2 = bool1;
          if (paramInt2 == 1) {
            bool2 = false;
          }
        }
        else
        {
          bool2 = bool1;
          if (paramInt2 == 3) {
            bool2 = false;
          }
        }
      }
      else
      {
        bool2 = bool1;
        if (paramInt1 - 1 < localArrayList.size())
        {
          bool2 = bool1;
          if (paramInt2 >= ((ArrayList)localHashMap.get(localArrayList.get(paramInt1 - 1))).size()) {
            bool2 = false;
          }
        }
      }
    }
  }
  
  public void onBindViewHolder(int paramInt1, int paramInt2, RecyclerView.ViewHolder paramViewHolder)
  {
    switch (paramViewHolder.getItemViewType())
    {
    }
    for (;;)
    {
      return;
      UserCell localUserCell = (UserCell)paramViewHolder.itemView;
      label57:
      label77:
      int i;
      label94:
      boolean bool1;
      if (this.onlyUsers == 2)
      {
        paramViewHolder = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
        if (this.onlyUsers != 2) {
          break label233;
        }
        localObject = ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray;
        if ((this.onlyUsers == 0) || (this.isAdmin)) {
          break label248;
        }
        i = 0;
        paramViewHolder = (ArrayList)paramViewHolder.get(((ArrayList)localObject).get(paramInt1 - i));
        paramViewHolder = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)paramViewHolder.get(paramInt2)).user_id));
        localUserCell.setData(paramViewHolder, null, null, 0);
        if (this.checkedMap != null)
        {
          if (this.checkedMap.indexOfKey(paramViewHolder.id) < 0) {
            break label254;
          }
          bool1 = true;
          label169:
          if (this.scrolling) {
            break label260;
          }
        }
      }
      label233:
      label248:
      label254:
      label260:
      for (boolean bool2 = true;; bool2 = false)
      {
        localUserCell.setChecked(bool1, bool2);
        if (this.ignoreUsers == null) {
          break;
        }
        if (this.ignoreUsers.indexOfKey(paramViewHolder.id) < 0) {
          break label266;
        }
        localUserCell.setAlpha(0.5F);
        break;
        paramViewHolder = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        break label57;
        localObject = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        break label77;
        i = 1;
        break label94;
        bool1 = false;
        break label169;
      }
      label266:
      localUserCell.setAlpha(1.0F);
      continue;
      Object localObject = (TextCell)paramViewHolder.itemView;
      if (paramInt1 == 0)
      {
        if (this.needPhonebook) {
          ((TextCell)localObject).setTextAndIcon(LocaleController.getString("InviteFriends", NUM), NUM);
        } else if (this.isAdmin) {
          ((TextCell)localObject).setTextAndIcon(LocaleController.getString("InviteToGroupByLink", NUM), NUM);
        } else if (paramInt2 == 0) {
          ((TextCell)localObject).setTextAndIcon(LocaleController.getString("NewGroup", NUM), NUM);
        } else if (paramInt2 == 1) {
          ((TextCell)localObject).setTextAndIcon(LocaleController.getString("NewSecretChat", NUM), NUM);
        } else if (paramInt2 == 2) {
          ((TextCell)localObject).setTextAndIcon(LocaleController.getString("NewChannel", NUM), NUM);
        }
      }
      else
      {
        paramViewHolder = (ContactsController.Contact)ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(paramInt2);
        if ((paramViewHolder.first_name != null) && (paramViewHolder.last_name != null)) {
          ((TextCell)localObject).setText(paramViewHolder.first_name + " " + paramViewHolder.last_name);
        } else if ((paramViewHolder.first_name != null) && (paramViewHolder.last_name == null)) {
          ((TextCell)localObject).setText(paramViewHolder.first_name);
        } else {
          ((TextCell)localObject).setText(paramViewHolder.last_name);
        }
      }
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    float f1 = 72.0F;
    switch (paramInt)
    {
    default: 
      paramViewGroup = new DividerCell(this.mContext);
      if (LocaleController.isRTL)
      {
        f2 = 28.0F;
        paramInt = AndroidUtilities.dp(f2);
        if (!LocaleController.isRTL) {
          break label165;
        }
      }
      break;
    }
    label165:
    for (float f2 = f1;; f2 = 28.0F)
    {
      paramViewGroup.setPadding(paramInt, 0, AndroidUtilities.dp(f2), 0);
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new UserCell(this.mContext, 58, 1, false);
        continue;
        paramViewGroup = new TextCell(this.mContext);
        continue;
        paramViewGroup = new GraySectionCell(this.mContext);
        ((GraySectionCell)paramViewGroup).setText(LocaleController.getString("Contacts", NUM).toUpperCase());
      }
      f2 = 72.0F;
      break;
    }
  }
  
  public void setCheckedMap(SparseArray<?> paramSparseArray)
  {
    this.checkedMap = paramSparseArray;
  }
  
  public void setIsScrolling(boolean paramBoolean)
  {
    this.scrolling = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/ContactsAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */