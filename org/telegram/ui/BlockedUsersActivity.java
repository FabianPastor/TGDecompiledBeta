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

public class BlockedUsersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, ContactsActivity.ContactsActivityDelegate
{
  private static final int block_user = 1;
  private EmptyTextProgressView emptyView;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private int selectedUserId;
  
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
    this.actionBar.setTitle(LocaleController.getString("BlockedUsers", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          BlockedUsersActivity.this.finishFragment();
        }
        for (;;)
        {
          return;
          if (paramAnonymousInt == 1)
          {
            Object localObject = new Bundle();
            ((Bundle)localObject).putBoolean("onlyUsers", true);
            ((Bundle)localObject).putBoolean("destroyAfterSelect", true);
            ((Bundle)localObject).putBoolean("returnAsResult", true);
            localObject = new ContactsActivity((Bundle)localObject);
            ((ContactsActivity)localObject).setDelegate(BlockedUsersActivity.this);
            BlockedUsersActivity.this.presentFragment((BaseFragment)localObject);
          }
        }
      }
    });
    this.actionBar.createMenu().addItem(1, NUM);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setText(LocaleController.getString("NoBlocked", NUM));
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setEmptyView(this.emptyView);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setVerticalScrollBarEnabled(false);
    RecyclerListView localRecyclerListView = this.listView;
    paramContext = new ListAdapter(paramContext);
    this.listViewAdapter = paramContext;
    localRecyclerListView.setAdapter(paramContext);
    paramContext = this.listView;
    if (LocaleController.isRTL)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (paramAnonymousInt >= MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.size()) {}
          for (;;)
          {
            return;
            paramAnonymousView = new Bundle();
            paramAnonymousView.putInt("user_id", ((Integer)MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.get(paramAnonymousInt)).intValue());
            BlockedUsersActivity.this.presentFragment(new ProfileActivity(paramAnonymousView));
          }
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt >= MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.size()) || (BlockedUsersActivity.this.getParentActivity() == null)) {}
          for (;;)
          {
            return true;
            BlockedUsersActivity.access$302(BlockedUsersActivity.this, ((Integer)MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.get(paramAnonymousInt)).intValue());
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(BlockedUsersActivity.this.getParentActivity());
            String str = LocaleController.getString("Unblock", NUM);
            paramAnonymousView = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (paramAnonymous2Int == 0) {
                  MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).unblockUser(BlockedUsersActivity.this.selectedUserId);
                }
              }
            };
            localBuilder.setItems(new CharSequence[] { str }, paramAnonymousView);
            BlockedUsersActivity.this.showDialog(localBuilder.create());
          }
        }
      });
      if (!MessagesController.getInstance(this.currentAccount).loadingBlockedUsers) {
        break label287;
      }
      this.emptyView.showProgress();
    }
    for (;;)
    {
      return this.fragmentView;
      i = 2;
      break;
      label287:
      this.emptyView.showTextView();
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
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.blockedUsersDidLoaded)
      {
        this.emptyView.showTextView();
        if (this.listViewAdapter != null) {
          this.listViewAdapter.notifyDataSetChanged();
        }
      }
    }
  }
  
  public void didSelectContact(TLRPC.User paramUser, String paramString, ContactsActivity paramContactsActivity)
  {
    if (paramUser == null) {}
    for (;;)
    {
      return;
      MessagesController.getInstance(this.currentAccount).blockUser(paramUser.id);
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local4 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        if (BlockedUsersActivity.this.listView != null)
        {
          int i = BlockedUsersActivity.this.listView.getChildCount();
          for (int j = 0; j < i; j++)
          {
            View localView = BlockedUsersActivity.this.listView.getChildAt(j);
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
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText5");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, local4, "windowBackgroundWhiteGrayText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    Drawable localDrawable3 = Theme.avatar_savedDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, new ThemeDescription(localRecyclerListView, 0, new Class[] { UserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2, localDrawable3 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundPink") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoaded);
    MessagesController.getInstance(this.currentAccount).getBlockedUsers(false);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
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
      if (MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.isEmpty()) {}
      for (int i = 0;; i = MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.size() + 1) {
        return i;
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      if (paramInt == MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.size()) {}
      for (paramInt = 1;; paramInt = 0) {
        return paramInt;
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getItemViewType() == 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      TLRPC.User localUser;
      String str;
      if (paramViewHolder.getItemViewType() == 0)
      {
        localUser = MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).getUser((Integer)MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.get(paramInt));
        if (localUser != null)
        {
          if (!localUser.bot) {
            break label112;
          }
          str = LocaleController.getString("Bot", NUM).substring(0, 1).toUpperCase() + LocaleController.getString("Bot", NUM).substring(1);
        }
      }
      for (;;)
      {
        ((UserCell)paramViewHolder.itemView).setData(localUser, null, str, 0);
        return;
        label112:
        if ((localUser.phone != null) && (localUser.phone.length() != 0)) {
          str = PhoneFormat.getInstance().format("+" + localUser.phone);
        } else {
          str = LocaleController.getString("NumberUnknown", NUM);
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new TextInfoCell(this.mContext);
        ((TextInfoCell)paramViewGroup).setText(LocaleController.getString("UnblockText", NUM));
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new UserCell(this.mContext, 1, 0, false);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/BlockedUsersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */