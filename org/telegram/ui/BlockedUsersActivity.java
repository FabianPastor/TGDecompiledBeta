package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextInfoCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.LayoutHelper;

public class BlockedUsersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, ContactsActivity.ContactsActivityDelegate
{
  private static final int block_user = 1;
  private TextView emptyTextView;
  private ListView listView;
  private ListAdapter listViewAdapter;
  private FrameLayout progressView;
  private int selectedUserId;
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView == null) {}
    for (;;)
    {
      return;
      int j = this.listView.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = this.listView.getChildAt(i);
        if ((localView instanceof UserCell)) {
          ((UserCell)localView).update(paramInt);
        }
        i += 1;
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    int i = 1;
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("BlockedUsers", 2131165360));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          BlockedUsersActivity.this.finishFragment();
        }
        while (paramAnonymousInt != 1) {
          return;
        }
        Object localObject = new Bundle();
        ((Bundle)localObject).putBoolean("onlyUsers", true);
        ((Bundle)localObject).putBoolean("destroyAfterSelect", true);
        ((Bundle)localObject).putBoolean("returnAsResult", true);
        localObject = new ContactsActivity((Bundle)localObject);
        ((ContactsActivity)localObject).setDelegate(BlockedUsersActivity.this);
        BlockedUsersActivity.this.presentFragment((BaseFragment)localObject);
      }
    });
    this.actionBar.createMenu().addItem(1, 2130837943);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.emptyTextView = new TextView(paramContext);
    this.emptyTextView.setTextColor(-8355712);
    this.emptyTextView.setTextSize(20.0F);
    this.emptyTextView.setGravity(17);
    this.emptyTextView.setVisibility(4);
    this.emptyTextView.setText(LocaleController.getString("NoBlocked", 2131165928));
    localFrameLayout.addView(this.emptyTextView, LayoutHelper.createFrame(-1, -1, 51));
    this.emptyTextView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.progressView = new FrameLayout(paramContext);
    localFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F));
    Object localObject = new ProgressBar(paramContext);
    this.progressView.addView((View)localObject, LayoutHelper.createFrame(-2, -2, 17));
    this.listView = new ListView(paramContext);
    this.listView.setEmptyView(this.emptyTextView);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    localObject = this.listView;
    paramContext = new ListAdapter(paramContext);
    this.listViewAdapter = paramContext;
    ((ListView)localObject).setAdapter(paramContext);
    paramContext = this.listView;
    if (LocaleController.isRTL)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if (paramAnonymousInt < MessagesController.getInstance().blockedUsers.size())
          {
            paramAnonymousAdapterView = new Bundle();
            paramAnonymousAdapterView.putInt("user_id", ((Integer)MessagesController.getInstance().blockedUsers.get(paramAnonymousInt)).intValue());
            BlockedUsersActivity.this.presentFragment(new ProfileActivity(paramAnonymousAdapterView));
          }
        }
      });
      this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
      {
        public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if ((paramAnonymousInt < 0) || (paramAnonymousInt >= MessagesController.getInstance().blockedUsers.size()) || (BlockedUsersActivity.this.getParentActivity() == null)) {
            return true;
          }
          BlockedUsersActivity.access$002(BlockedUsersActivity.this, ((Integer)MessagesController.getInstance().blockedUsers.get(paramAnonymousInt)).intValue());
          paramAnonymousAdapterView = new AlertDialog.Builder(BlockedUsersActivity.this.getParentActivity());
          paramAnonymousView = LocaleController.getString("Unblock", 2131166349);
          DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              if (paramAnonymous2Int == 0) {
                MessagesController.getInstance().unblockUser(BlockedUsersActivity.this.selectedUserId);
              }
            }
          };
          paramAnonymousAdapterView.setItems(new CharSequence[] { paramAnonymousView }, local1);
          BlockedUsersActivity.this.showDialog(paramAnonymousAdapterView.create());
          return true;
        }
      });
      if (!MessagesController.getInstance().loadingBlockedUsers) {
        break label405;
      }
      this.progressView.setVisibility(0);
      this.emptyTextView.setVisibility(8);
      this.listView.setEmptyView(null);
    }
    for (;;)
    {
      return this.fragmentView;
      i = 2;
      break;
      label405:
      this.progressView.setVisibility(8);
      this.listView.setEmptyView(this.emptyTextView);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0)) {
        updateVisibleRows(paramInt);
      }
    }
    do
    {
      do
      {
        return;
      } while (paramInt != NotificationCenter.blockedUsersDidLoaded);
      if (this.progressView != null) {
        this.progressView.setVisibility(8);
      }
      if ((this.listView != null) && (this.listView.getEmptyView() == null)) {
        this.listView.setEmptyView(this.emptyTextView);
      }
    } while (this.listViewAdapter == null);
    this.listViewAdapter.notifyDataSetChanged();
  }
  
  public void didSelectContact(TLRPC.User paramUser, String paramString)
  {
    if (paramUser == null) {
      return;
    }
    MessagesController.getInstance().blockUser(paramUser.id);
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
    MessagesController.getInstance().getBlockedUsers(false);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
  
  private class ListAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return false;
    }
    
    public int getCount()
    {
      if (MessagesController.getInstance().blockedUsers.isEmpty()) {
        return 0;
      }
      return MessagesController.getInstance().blockedUsers.size() + 1;
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (paramInt == MessagesController.getInstance().blockedUsers.size()) {
        return 1;
      }
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      TLRPC.User localUser;
      Object localObject;
      if (i == 0)
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new UserCell(this.mContext, 1, 0, false);
        }
        localUser = MessagesController.getInstance().getUser((Integer)MessagesController.getInstance().blockedUsers.get(paramInt));
        localObject = paramViewGroup;
        if (localUser != null)
        {
          if (!localUser.bot) {
            break label131;
          }
          paramView = LocaleController.getString("Bot", 2131165365).substring(0, 1).toUpperCase() + LocaleController.getString("Bot", 2131165365).substring(1);
          ((UserCell)paramViewGroup).setData(localUser, null, paramView, 0);
          localObject = paramViewGroup;
        }
      }
      label131:
      do
      {
        do
        {
          return (View)localObject;
          if ((localUser.phone != null) && (localUser.phone.length() != 0))
          {
            paramView = PhoneFormat.getInstance().format("+" + localUser.phone);
            break;
          }
          paramView = LocaleController.getString("NumberUnknown", 2131166043);
          break;
          localObject = paramView;
        } while (i != 1);
        localObject = paramView;
      } while (paramView != null);
      paramView = new TextInfoCell(this.mContext);
      ((TextInfoCell)paramView).setText(LocaleController.getString("UnblockText", 2131166350));
      return paramView;
    }
    
    public int getViewTypeCount()
    {
      return 2;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return MessagesController.getInstance().blockedUsers.isEmpty();
    }
    
    public boolean isEnabled(int paramInt)
    {
      return paramInt != MessagesController.getInstance().blockedUsers.size();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/BlockedUsersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */