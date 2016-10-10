package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;

public class ConvertGroupActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int chat_id;
  private int convertDetailRow;
  private int convertInfoRow;
  private int convertRow;
  private ListAdapter listAdapter;
  private int rowCount;
  
  public ConvertGroupActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chat_id = paramBundle.getInt("chat_id");
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ConvertGroup", 2131165524));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ConvertGroupActivity.this.finishFragment();
        }
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(-986896);
    paramContext = new ListView(paramContext);
    paramContext.setDivider(null);
    paramContext.setDividerHeight(0);
    paramContext.setVerticalScrollBarEnabled(false);
    paramContext.setDrawSelectorOnTop(true);
    localFrameLayout.addView(paramContext, LayoutHelper.createFrame(-1, -1.0F));
    paramContext.setAdapter(this.listAdapter);
    paramContext.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (paramAnonymousInt == ConvertGroupActivity.this.convertRow)
        {
          paramAnonymousAdapterView = new AlertDialog.Builder(ConvertGroupActivity.this.getParentActivity());
          paramAnonymousAdapterView.setMessage(LocaleController.getString("ConvertGroupAlert", 2131165525));
          paramAnonymousAdapterView.setTitle(LocaleController.getString("ConvertGroupAlertWarning", 2131165526));
          paramAnonymousAdapterView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              MessagesController.getInstance().convertToMegaGroup(ConvertGroupActivity.this.getParentActivity(), ConvertGroupActivity.this.chat_id);
            }
          });
          paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
          ConvertGroupActivity.this.showDialog(paramAnonymousAdapterView.create());
        }
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.closeChats) {
      removeSelfFromStack();
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.convertInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.convertRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.convertDetailRow = i;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
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
      return ConvertGroupActivity.this.rowCount;
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
      if (paramInt == ConvertGroupActivity.this.convertRow) {}
      while ((paramInt != ConvertGroupActivity.this.convertInfoRow) && (paramInt != ConvertGroupActivity.this.convertDetailRow)) {
        return 0;
      }
      return 1;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      Object localObject;
      if (i == 0)
      {
        localObject = paramView;
        if (paramView == null)
        {
          localObject = new TextSettingsCell(this.mContext);
          ((View)localObject).setBackgroundColor(-1);
        }
        paramView = (TextSettingsCell)localObject;
        paramViewGroup = (ViewGroup)localObject;
        if (paramInt == ConvertGroupActivity.this.convertRow)
        {
          paramView.setText(LocaleController.getString("ConvertGroup", 2131165524), false);
          paramViewGroup = (ViewGroup)localObject;
        }
      }
      do
      {
        do
        {
          return paramViewGroup;
          paramViewGroup = paramView;
        } while (i != 1);
        localObject = paramView;
        if (paramView == null) {
          localObject = new TextInfoPrivacyCell(this.mContext);
        }
        if (paramInt == ConvertGroupActivity.this.convertInfoRow)
        {
          ((TextInfoPrivacyCell)localObject).setText(AndroidUtilities.replaceTags(LocaleController.getString("ConvertGroupInfo2", 2131165528)));
          ((View)localObject).setBackgroundResource(2130837688);
          return (View)localObject;
        }
        paramViewGroup = (ViewGroup)localObject;
      } while (paramInt != ConvertGroupActivity.this.convertDetailRow);
      ((TextInfoPrivacyCell)localObject).setText(AndroidUtilities.replaceTags(LocaleController.getString("ConvertGroupInfo3", 2131165529)));
      ((View)localObject).setBackgroundResource(2130837689);
      return (View)localObject;
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
      return false;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return paramInt == ConvertGroupActivity.this.convertRow;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ConvertGroupActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */