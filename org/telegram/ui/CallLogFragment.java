package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterPhoneCalls;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.VoIPHelper;

public class CallLogFragment
  extends BaseFragment
{
  private static final int TYPE_IN = 1;
  private static final int TYPE_MISSED = 2;
  private static final int TYPE_OUT = 0;
  private View.OnClickListener callBtnClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      paramAnonymousView = (CallLogFragment.CallLogRow)paramAnonymousView.getTag();
      VoIPHelper.startCall(CallLogFragment.access$002(CallLogFragment.this, paramAnonymousView.user), CallLogFragment.this.getParentActivity());
    }
  };
  private ArrayList<CallLogRow> calls = new ArrayList();
  private EmptyTextProgressView emptyView;
  private boolean endReached;
  private boolean firstLoaded;
  private ImageSpan iconIn;
  private ImageSpan iconMissed;
  private ImageSpan iconOut;
  private TLRPC.User lastCallUser;
  private ListAdapter listViewAdapter;
  private boolean loading;
  
  private void getChats(int paramInt1, int paramInt2)
  {
    if (this.loading) {
      return;
    }
    this.loading = true;
    if ((this.emptyView != null) && (!this.firstLoaded)) {
      this.emptyView.showProgress();
    }
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
    TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
    localTL_messages_search.limit = paramInt2;
    localTL_messages_search.peer = new TLRPC.TL_inputPeerEmpty();
    localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterPhoneCalls();
    localTL_messages_search.q = "";
    localTL_messages_search.max_id = paramInt1;
    paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_messages_search, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (paramAnonymousTL_error == null)
            {
              SparseArray localSparseArray = new SparseArray();
              Object localObject2 = (TLRPC.messages_Messages)paramAnonymousTLObject;
              CallLogFragment.access$202(CallLogFragment.this, ((TLRPC.messages_Messages)localObject2).messages.isEmpty());
              Object localObject1 = ((TLRPC.messages_Messages)localObject2).users.iterator();
              Object localObject3;
              while (((Iterator)localObject1).hasNext())
              {
                localObject3 = (TLRPC.User)((Iterator)localObject1).next();
                localSparseArray.put(((TLRPC.User)localObject3).id, localObject3);
              }
              label146:
              TLRPC.Message localMessage;
              label189:
              int j;
              if (CallLogFragment.this.calls.size() > 0)
              {
                localObject1 = (CallLogFragment.CallLogRow)CallLogFragment.this.calls.get(CallLogFragment.this.calls.size() - 1);
                localObject3 = ((TLRPC.messages_Messages)localObject2).messages.iterator();
                do
                {
                  if (!((Iterator)localObject3).hasNext()) {
                    break;
                  }
                  localMessage = (TLRPC.Message)((Iterator)localObject3).next();
                } while (localMessage.action == null);
                if (localMessage.from_id != UserConfig.getClientUserId()) {
                  break label372;
                }
                i = 0;
                j = i;
                if (i == 1)
                {
                  j = i;
                  if ((((TLRPC.TL_messageActionPhoneCall)localMessage.action).reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) {
                    j = 2;
                  }
                }
                if (localMessage.from_id != UserConfig.getClientUserId()) {
                  break label377;
                }
              }
              label372:
              label377:
              for (int i = localMessage.to_id.user_id;; i = localMessage.from_id)
              {
                if ((localObject1 != null) && (((CallLogFragment.CallLogRow)localObject1).user.id == i))
                {
                  localObject2 = localObject1;
                  if (((CallLogFragment.CallLogRow)localObject1).type == j) {}
                }
                else
                {
                  if ((localObject1 != null) && (!CallLogFragment.this.calls.contains(localObject1))) {
                    CallLogFragment.this.calls.add(localObject1);
                  }
                  localObject2 = new CallLogFragment.CallLogRow(CallLogFragment.this, null);
                  ((CallLogFragment.CallLogRow)localObject2).calls = new ArrayList();
                  ((CallLogFragment.CallLogRow)localObject2).user = ((TLRPC.User)localSparseArray.get(i));
                  ((CallLogFragment.CallLogRow)localObject2).type = j;
                }
                ((CallLogFragment.CallLogRow)localObject2).calls.add(localMessage);
                localObject1 = localObject2;
                break label146;
                localObject1 = null;
                break;
                i = 1;
                break label189;
              }
              if ((localObject1 != null) && (((CallLogFragment.CallLogRow)localObject1).calls.size() > 0)) {
                CallLogFragment.this.calls.add(localObject1);
              }
            }
            for (;;)
            {
              CallLogFragment.access$302(CallLogFragment.this, false);
              CallLogFragment.access$602(CallLogFragment.this, true);
              if (CallLogFragment.this.emptyView != null) {
                CallLogFragment.this.emptyView.showTextView();
              }
              if (CallLogFragment.this.listViewAdapter != null) {
                CallLogFragment.this.listViewAdapter.notifyDataSetChanged();
              }
              return;
              CallLogFragment.access$202(CallLogFragment.this, true);
            }
          }
        });
      }
    }, 2);
    ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, this.classGuid);
  }
  
  public View createView(Context paramContext)
  {
    int i = 1;
    Object localObject = getParentActivity().getResources().getDrawable(NUM).mutate();
    ((Drawable)localObject).setBounds(0, 0, ((Drawable)localObject).getIntrinsicWidth(), ((Drawable)localObject).getIntrinsicHeight());
    this.iconOut = new ImageSpan((Drawable)localObject, 0);
    localObject = getParentActivity().getResources().getDrawable(NUM).mutate();
    ((Drawable)localObject).setBounds(0, 0, ((Drawable)localObject).getIntrinsicWidth(), ((Drawable)localObject).getIntrinsicHeight());
    this.iconIn = new ImageSpan((Drawable)localObject, 0);
    localObject = getParentActivity().getResources().getDrawable(NUM).mutate();
    ((Drawable)localObject).setBounds(0, 0, ((Drawable)localObject).getIntrinsicWidth(), ((Drawable)localObject).getIntrinsicHeight());
    this.iconMissed = new ImageSpan((Drawable)localObject, 0);
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("Calls", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          CallLogFragment.this.finishFragment();
        }
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    this.fragmentView.setBackgroundColor(-1);
    localObject = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setText(LocaleController.getString("NoCallLog", NUM));
    ((FrameLayout)localObject).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    ListView localListView = new ListView(paramContext);
    localListView.setEmptyView(this.emptyView);
    localListView.setDivider(null);
    localListView.setDividerHeight(0);
    localListView.setDrawSelectorOnTop(true);
    paramContext = new ListAdapter(paramContext);
    this.listViewAdapter = paramContext;
    localListView.setAdapter(paramContext);
    if (LocaleController.isRTL)
    {
      localListView.setVerticalScrollbarPosition(i);
      ((FrameLayout)localObject).addView(localListView, LayoutHelper.createFrame(-1, -1.0F));
      localListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if ((paramAnonymousInt < 0) || (paramAnonymousInt >= CallLogFragment.this.calls.size())) {
            return;
          }
          paramAnonymousAdapterView = (CallLogFragment.CallLogRow)CallLogFragment.this.calls.get(paramAnonymousInt);
          paramAnonymousView = new Bundle();
          paramAnonymousView.putInt("user_id", paramAnonymousAdapterView.user.id);
          paramAnonymousView.putInt("message_id", ((TLRPC.Message)paramAnonymousAdapterView.calls.get(0)).id);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
          CallLogFragment.this.presentFragment(new ChatActivity(paramAnonymousView), true);
        }
      });
      localListView.setOnScrollListener(new AbsListView.OnScrollListener()
      {
        public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if ((!CallLogFragment.this.endReached) && (!CallLogFragment.this.loading) && (!CallLogFragment.this.calls.isEmpty()) && (paramAnonymousInt1 + paramAnonymousInt2 >= paramAnonymousInt3 - 5))
          {
            paramAnonymousAbsListView = (CallLogFragment.CallLogRow)CallLogFragment.this.calls.get(CallLogFragment.this.calls.size() - 1);
            CallLogFragment.this.getChats(((TLRPC.Message)paramAnonymousAbsListView.calls.get(paramAnonymousAbsListView.calls.size() - 1)).id, 100);
          }
        }
        
        public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt) {}
      });
      if (!this.loading) {
        break label381;
      }
      this.emptyView.showProgress();
    }
    for (;;)
    {
      return this.fragmentView;
      i = 2;
      break;
      label381:
      this.emptyView.showTextView();
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    getChats(0, 50);
    return true;
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if ((paramInt == 101) && (paramArrayOfInt[0] == 0)) {
      VoIPHelper.startCall(this.lastCallUser, getParentActivity());
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
  
  private class CallLogRow
  {
    public List<TLRPC.Message> calls;
    public int type;
    public TLRPC.User user;
    
    private CallLogRow() {}
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
      int j = CallLogFragment.this.calls.size();
      int i = j;
      if (!CallLogFragment.this.calls.isEmpty())
      {
        i = j;
        if (!CallLogFragment.this.endReached) {
          i = j + 1;
        }
      }
      return i;
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
      if (paramInt < CallLogFragment.this.calls.size()) {
        return 0;
      }
      if ((!CallLogFragment.this.endReached) && (paramInt == CallLogFragment.this.calls.size())) {
        return 1;
      }
      return 2;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      CallLogFragment.CallLogRow localCallLogRow;
      label266:
      label296:
      boolean bool;
      if (i == 0)
      {
        paramViewGroup = paramView;
        Object localObject;
        if (paramView == null)
        {
          paramViewGroup = new FrameLayout(this.mContext);
          paramView = new ProfileSearchCell(this.mContext);
          paramView.setPaddingRight(AndroidUtilities.dp(32.0F));
          paramViewGroup.setBackgroundColor(0);
          paramViewGroup.addView(paramView);
          localObject = new ImageView(this.mContext);
          ((ImageView)localObject).setImageResource(NUM);
          ((ImageView)localObject).setAlpha(214);
          ((ImageView)localObject).setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152, false));
          ((ImageView)localObject).setScaleType(ImageView.ScaleType.CENTER);
          ((ImageView)localObject).setOnClickListener(CallLogFragment.this.callBtnClickListener);
          if (LocaleController.isRTL)
          {
            i = 3;
            paramViewGroup.addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x10, 8.0F, 0.0F, 8.0F, 0.0F));
            paramViewGroup.setTag(new CallLogFragment.ViewHolder(CallLogFragment.this, (ImageView)localObject, paramView));
          }
        }
        else
        {
          localObject = (CallLogFragment.ViewHolder)paramViewGroup.getTag();
          ProfileSearchCell localProfileSearchCell = ((CallLogFragment.ViewHolder)localObject).cell;
          localCallLogRow = (CallLogFragment.CallLogRow)CallLogFragment.this.calls.get(paramInt);
          paramView = (TLRPC.Message)localCallLogRow.calls.get(0);
          if (localCallLogRow.calls.size() != 1) {
            break label364;
          }
          paramView = new SpannableString("  " + LocaleController.formatDateCallLog(paramView.date));
          switch (localCallLogRow.type)
          {
          default: 
            localProfileSearchCell.setData(localCallLogRow.user, null, null, paramView, false);
            if ((paramInt != CallLogFragment.this.calls.size() - 1) || (!CallLogFragment.this.endReached))
            {
              bool = true;
              label339:
              localProfileSearchCell.useSeparator = bool;
              ((CallLogFragment.ViewHolder)localObject).button.setTag(localCallLogRow);
            }
            break;
          }
        }
      }
      label364:
      label499:
      do
      {
        do
        {
          do
          {
            return paramViewGroup;
            i = 5;
            break;
            paramView = new SpannableString(String.format("  (%d) %s", new Object[] { Integer.valueOf(localCallLogRow.calls.size()), LocaleController.formatDateCallLog(paramView.date) }));
            break label266;
            paramView.setSpan(CallLogFragment.this.iconOut, 0, 1, 0);
            break label296;
            paramView.setSpan(CallLogFragment.this.iconIn, 0, 1, 0);
            break label296;
            paramView.setSpan(CallLogFragment.this.iconMissed, 0, 1, 0);
            break label296;
            bool = false;
            break label339;
            if (i != 1) {
              break label499;
            }
            paramViewGroup = paramView;
          } while (paramView != null);
          paramView = new LoadingCell(this.mContext);
          paramView.setBackgroundColor(-1);
          return paramView;
          paramViewGroup = paramView;
        } while (i != 2);
        paramViewGroup = paramView;
      } while (paramView != null);
      paramView = new TextInfoPrivacyCell(this.mContext);
      paramView.setBackgroundResource(NUM);
      return paramView;
    }
    
    public int getViewTypeCount()
    {
      return 3;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return getCount() == 0;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return paramInt != CallLogFragment.this.calls.size();
    }
  }
  
  private class ViewHolder
  {
    public ImageView button;
    public ProfileSearchCell cell;
    
    public ViewHolder(ImageView paramImageView, ProfileSearchCell paramProfileSearchCell)
    {
      this.button = paramImageView;
      this.cell = paramProfileSearchCell;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/CallLogFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */