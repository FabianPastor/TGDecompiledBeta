package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.TextInfoCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PrivacyUsersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int block_user = 1;
  private PrivacyActivityDelegate delegate;
  private EmptyTextProgressView emptyView;
  private boolean isAlwaysShare;
  private boolean isGroup;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private int selectedUserId;
  private ArrayList<Integer> uidArray;
  
  public PrivacyUsersActivity(ArrayList<Integer> paramArrayList, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.uidArray = paramArrayList;
    this.isAlwaysShare = paramBoolean2;
    this.isGroup = paramBoolean1;
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView == null) {}
    for (;;)
    {
      return;
      int i = this.listView.getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = this.listView.getChildAt(j);
        if ((localView instanceof UserCell)) {
          ((UserCell)localView).update(paramInt);
        }
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    int i = 1;
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    FrameLayout localFrameLayout;
    if (this.isGroup) {
      if (this.isAlwaysShare)
      {
        this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
        {
          public void onItemClick(int paramAnonymousInt)
          {
            if (paramAnonymousInt == -1)
            {
              PrivacyUsersActivity.this.finishFragment();
              return;
            }
            Bundle localBundle;
            if (paramAnonymousInt == 1)
            {
              localBundle = new Bundle();
              if (!PrivacyUsersActivity.this.isAlwaysShare) {
                break label91;
              }
            }
            label91:
            for (Object localObject = "isAlwaysShare";; localObject = "isNeverShare")
            {
              localBundle.putBoolean((String)localObject, true);
              localBundle.putBoolean("isGroup", PrivacyUsersActivity.this.isGroup);
              localObject = new GroupCreateActivity(localBundle);
              ((GroupCreateActivity)localObject).setDelegate(new GroupCreateActivity.GroupCreateActivityDelegate()
              {
                public void didSelectUsers(ArrayList<Integer> paramAnonymous2ArrayList)
                {
                  Iterator localIterator = paramAnonymous2ArrayList.iterator();
                  while (localIterator.hasNext())
                  {
                    paramAnonymous2ArrayList = (Integer)localIterator.next();
                    if (!PrivacyUsersActivity.this.uidArray.contains(paramAnonymous2ArrayList)) {
                      PrivacyUsersActivity.this.uidArray.add(paramAnonymous2ArrayList);
                    }
                  }
                  PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                  if (PrivacyUsersActivity.this.delegate != null) {
                    PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, true);
                  }
                }
              });
              PrivacyUsersActivity.this.presentFragment((BaseFragment)localObject);
              break;
              break;
            }
          }
        });
        this.actionBar.createMenu().addItem(1, NUM);
        this.fragmentView = new FrameLayout(paramContext);
        localFrameLayout = (FrameLayout)this.fragmentView;
        this.emptyView = new EmptyTextProgressView(paramContext);
        this.emptyView.showTextView();
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
        this.listView = new RecyclerListView(paramContext);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
        RecyclerListView localRecyclerListView = this.listView;
        paramContext = new ListAdapter(paramContext);
        this.listViewAdapter = paramContext;
        localRecyclerListView.setAdapter(paramContext);
        paramContext = this.listView;
        if (!LocaleController.isRTL) {
          break label341;
        }
      }
    }
    for (;;)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (paramAnonymousInt < PrivacyUsersActivity.this.uidArray.size())
          {
            paramAnonymousView = new Bundle();
            paramAnonymousView.putInt("user_id", ((Integer)PrivacyUsersActivity.this.uidArray.get(paramAnonymousInt)).intValue());
            PrivacyUsersActivity.this.presentFragment(new ProfileActivity(paramAnonymousView));
          }
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt < 0) || (paramAnonymousInt >= PrivacyUsersActivity.this.uidArray.size()) || (PrivacyUsersActivity.this.getParentActivity() == null)) {}
          for (boolean bool = false;; bool = true)
          {
            return bool;
            PrivacyUsersActivity.access$502(PrivacyUsersActivity.this, ((Integer)PrivacyUsersActivity.this.uidArray.get(paramAnonymousInt)).intValue());
            paramAnonymousView = new AlertDialog.Builder(PrivacyUsersActivity.this.getParentActivity());
            String str = LocaleController.getString("Delete", NUM);
            DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (paramAnonymous2Int == 0)
                {
                  PrivacyUsersActivity.this.uidArray.remove(Integer.valueOf(PrivacyUsersActivity.this.selectedUserId));
                  PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                  if (PrivacyUsersActivity.this.delegate != null) {
                    PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, false);
                  }
                }
              }
            };
            paramAnonymousView.setItems(new CharSequence[] { str }, local1);
            PrivacyUsersActivity.this.showDialog(paramAnonymousView.create());
          }
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("NeverAllow", NUM));
      break;
      if (this.isAlwaysShare)
      {
        this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", NUM));
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", NUM));
      break;
      label341:
      i = 2;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.updateInterfaces)
    {
      paramInt1 = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt1 & 0x2) != 0) || ((paramInt1 & 0x1) != 0)) {
        updateVisibleRows(paramInt1);
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local4 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        if (PrivacyUsersActivity.this.listView != null)
        {
          int i = PrivacyUsersActivity.this.listView.getChildCount();
          for (int j = 0; j < i; j++)
          {
            View localView = PrivacyUsersActivity.this.listView.getChildAt(j);
            if ((localView instanceof UserCell)) {
              ((UserCell)localView).update(0);
            }
          }
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText5");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, local4, "windowBackgroundWhiteGrayText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    Drawable localDrawable3 = Theme.avatar_savedDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, new ThemeDescription(localRecyclerListView, 0, new Class[] { UserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2, localDrawable3 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundPink") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
  
  public void setDelegate(PrivacyActivityDelegate paramPrivacyActivityDelegate)
  {
    this.delegate = paramPrivacyActivityDelegate;
  }
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      if (PrivacyUsersActivity.this.uidArray.isEmpty()) {}
      for (int i = 0;; i = PrivacyUsersActivity.this.uidArray.size() + 1) {
        return i;
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      if (paramInt == PrivacyUsersActivity.this.uidArray.size()) {}
      for (paramInt = 1;; paramInt = 0) {
        return paramInt;
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getAdapterPosition() != PrivacyUsersActivity.this.uidArray.size()) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      TLRPC.User localUser;
      UserCell localUserCell;
      if (paramViewHolder.getItemViewType() == 0)
      {
        localUser = MessagesController.getInstance(PrivacyUsersActivity.this.currentAccount).getUser((Integer)PrivacyUsersActivity.this.uidArray.get(paramInt));
        localUserCell = (UserCell)paramViewHolder.itemView;
        if ((localUser.phone == null) || (localUser.phone.length() == 0)) {
          break label100;
        }
      }
      label100:
      for (paramViewHolder = PhoneFormat.getInstance().format("+" + localUser.phone);; paramViewHolder = LocaleController.getString("NumberUnknown", NUM))
      {
        localUserCell.setData(localUser, null, paramViewHolder, 0);
        return;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new TextInfoCell(this.mContext);
        ((TextInfoCell)paramViewGroup).setText(LocaleController.getString("RemoveFromListText", NUM));
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new UserCell(this.mContext, 1, 0, false);
      }
    }
  }
  
  public static abstract interface PrivacyActivityDelegate
  {
    public abstract void didUpdatedUserList(ArrayList<Integer> paramArrayList, boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PrivacyUsersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */