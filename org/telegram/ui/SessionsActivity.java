package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_authorizations;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_resetAuthorization;
import org.telegram.tgnet.TLRPC.TL_auth_resetAuthorizations;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;

public class SessionsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private TLRPC.TL_authorization currentSession = null;
  private int currentSessionRow;
  private int currentSessionSectionRow;
  private LinearLayout emptyLayout;
  private ListAdapter listAdapter;
  private boolean loading;
  private int noOtherSessionsRow;
  private int otherSessionsEndRow;
  private int otherSessionsSectionRow;
  private int otherSessionsStartRow;
  private int otherSessionsTerminateDetail;
  private int rowCount;
  private ArrayList<TLRPC.TL_authorization> sessions = new ArrayList();
  private int terminateAllSessionsDetailRow;
  private int terminateAllSessionsRow;
  
  private void loadSessions(boolean paramBoolean)
  {
    if (this.loading) {
      return;
    }
    if (!paramBoolean) {
      this.loading = true;
    }
    TLRPC.TL_account_getAuthorizations localTL_account_getAuthorizations = new TLRPC.TL_account_getAuthorizations();
    int i = ConnectionsManager.getInstance().sendRequest(localTL_account_getAuthorizations, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            SessionsActivity.access$602(SessionsActivity.this, false);
            if (paramAnonymousTL_error == null)
            {
              SessionsActivity.this.sessions.clear();
              Iterator localIterator = ((TLRPC.TL_account_authorizations)paramAnonymousTLObject).authorizations.iterator();
              while (localIterator.hasNext())
              {
                TLRPC.TL_authorization localTL_authorization = (TLRPC.TL_authorization)localIterator.next();
                if ((localTL_authorization.flags & 0x1) != 0) {
                  SessionsActivity.access$702(SessionsActivity.this, localTL_authorization);
                } else {
                  SessionsActivity.this.sessions.add(localTL_authorization);
                }
              }
              SessionsActivity.this.updateRows();
            }
            if (SessionsActivity.this.listAdapter != null) {
              SessionsActivity.this.listAdapter.notifyDataSetChanged();
            }
          }
        });
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(i, this.classGuid);
  }
  
  private void updateRows()
  {
    this.rowCount = 0;
    if (this.currentSession != null)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.currentSessionSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.currentSessionRow = i;
      if (!this.sessions.isEmpty()) {
        break label132;
      }
      if (this.currentSession == null) {
        break label124;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    label124:
    for (this.noOtherSessionsRow = i;; this.noOtherSessionsRow = -1)
    {
      this.terminateAllSessionsRow = -1;
      this.terminateAllSessionsDetailRow = -1;
      this.otherSessionsSectionRow = -1;
      this.otherSessionsStartRow = -1;
      this.otherSessionsEndRow = -1;
      this.otherSessionsTerminateDetail = -1;
      return;
      this.currentSessionRow = -1;
      this.currentSessionSectionRow = -1;
      break;
    }
    label132:
    this.noOtherSessionsRow = -1;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.terminateAllSessionsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.terminateAllSessionsDetailRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.otherSessionsSectionRow = i;
    this.otherSessionsStartRow = (this.otherSessionsSectionRow + 1);
    this.otherSessionsEndRow = (this.otherSessionsStartRow + this.sessions.size());
    this.rowCount += this.sessions.size();
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.otherSessionsTerminateDetail = i;
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("SessionsTitle", 2131166258));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          SessionsActivity.this.finishFragment();
        }
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject1 = (FrameLayout)this.fragmentView;
    ((FrameLayout)localObject1).setBackgroundColor(-986896);
    this.emptyLayout = new LinearLayout(paramContext);
    this.emptyLayout.setOrientation(1);
    this.emptyLayout.setGravity(17);
    this.emptyLayout.setBackgroundResource(2130837689);
    this.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
    Object localObject2 = new ImageView(paramContext);
    ((ImageView)localObject2).setImageResource(2130837638);
    this.emptyLayout.addView((View)localObject2);
    Object localObject3 = (LinearLayout.LayoutParams)((ImageView)localObject2).getLayoutParams();
    ((LinearLayout.LayoutParams)localObject3).width = -2;
    ((LinearLayout.LayoutParams)localObject3).height = -2;
    ((ImageView)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject3);
    localObject2 = new TextView(paramContext);
    ((TextView)localObject2).setTextColor(-7697782);
    ((TextView)localObject2).setGravity(17);
    ((TextView)localObject2).setTextSize(1, 17.0F);
    ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    ((TextView)localObject2).setText(LocaleController.getString("NoOtherSessions", 2131165941));
    this.emptyLayout.addView((View)localObject2);
    localObject3 = (LinearLayout.LayoutParams)((TextView)localObject2).getLayoutParams();
    ((LinearLayout.LayoutParams)localObject3).topMargin = AndroidUtilities.dp(16.0F);
    ((LinearLayout.LayoutParams)localObject3).width = -2;
    ((LinearLayout.LayoutParams)localObject3).height = -2;
    ((LinearLayout.LayoutParams)localObject3).gravity = 17;
    ((TextView)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject3);
    localObject2 = new TextView(paramContext);
    ((TextView)localObject2).setTextColor(-7697782);
    ((TextView)localObject2).setGravity(17);
    ((TextView)localObject2).setTextSize(1, 17.0F);
    ((TextView)localObject2).setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
    ((TextView)localObject2).setText(LocaleController.getString("NoOtherSessionsInfo", 2131165942));
    this.emptyLayout.addView((View)localObject2);
    localObject3 = (LinearLayout.LayoutParams)((TextView)localObject2).getLayoutParams();
    ((LinearLayout.LayoutParams)localObject3).topMargin = AndroidUtilities.dp(14.0F);
    ((LinearLayout.LayoutParams)localObject3).width = -2;
    ((LinearLayout.LayoutParams)localObject3).height = -2;
    ((LinearLayout.LayoutParams)localObject3).gravity = 17;
    ((TextView)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject3);
    localObject2 = new FrameLayout(paramContext);
    ((FrameLayout)localObject1).addView((View)localObject2);
    localObject3 = (FrameLayout.LayoutParams)((FrameLayout)localObject2).getLayoutParams();
    ((FrameLayout.LayoutParams)localObject3).width = -1;
    ((FrameLayout.LayoutParams)localObject3).height = -1;
    ((FrameLayout)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject3);
    ((FrameLayout)localObject2).setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    ((FrameLayout)localObject2).addView(new ProgressBar(paramContext));
    localObject3 = (FrameLayout.LayoutParams)((FrameLayout)localObject2).getLayoutParams();
    ((FrameLayout.LayoutParams)localObject3).width = -2;
    ((FrameLayout.LayoutParams)localObject3).height = -2;
    ((FrameLayout.LayoutParams)localObject3).gravity = 17;
    ((FrameLayout)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject3);
    paramContext = new ListView(paramContext);
    paramContext.setDivider(null);
    paramContext.setDividerHeight(0);
    paramContext.setVerticalScrollBarEnabled(false);
    paramContext.setDrawSelectorOnTop(true);
    paramContext.setEmptyView((View)localObject2);
    ((FrameLayout)localObject1).addView(paramContext);
    localObject1 = (FrameLayout.LayoutParams)paramContext.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject1).width = -1;
    ((FrameLayout.LayoutParams)localObject1).height = -1;
    ((FrameLayout.LayoutParams)localObject1).gravity = 48;
    paramContext.setLayoutParams((ViewGroup.LayoutParams)localObject1);
    paramContext.setAdapter(this.listAdapter);
    paramContext.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, final int paramAnonymousInt, long paramAnonymousLong)
      {
        if (paramAnonymousInt == SessionsActivity.this.terminateAllSessionsRow) {
          if (SessionsActivity.this.getParentActivity() != null) {}
        }
        while ((paramAnonymousInt < SessionsActivity.this.otherSessionsStartRow) || (paramAnonymousInt >= SessionsActivity.this.otherSessionsEndRow))
        {
          return;
          paramAnonymousAdapterView = new AlertDialog.Builder(SessionsActivity.this.getParentActivity());
          paramAnonymousAdapterView.setMessage(LocaleController.getString("AreYouSureSessions", 2131165327));
          paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165299));
          paramAnonymousAdapterView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              paramAnonymous2DialogInterface = new TLRPC.TL_auth_resetAuthorizations();
              ConnectionsManager.getInstance().sendRequest(paramAnonymous2DialogInterface, new RequestDelegate()
              {
                public void run(final TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      if (SessionsActivity.this.getParentActivity() == null) {
                        return;
                      }
                      if ((paramAnonymous3TL_error == null) && ((paramAnonymous3TLObject instanceof TLRPC.TL_boolTrue))) {
                        Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllSessions", 2131166328), 0).show();
                      }
                      for (;;)
                      {
                        SessionsActivity.this.finishFragment();
                        return;
                        Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", 2131166352), 0).show();
                      }
                    }
                  });
                  UserConfig.registeredForPush = false;
                  UserConfig.saveConfig(false);
                  MessagesController.getInstance().registerForPush(UserConfig.pushString);
                  ConnectionsManager.getInstance().setUserId(UserConfig.getClientUserId());
                }
              });
            }
          });
          paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
          SessionsActivity.this.showDialog(paramAnonymousAdapterView.create());
          return;
        }
        paramAnonymousAdapterView = new AlertDialog.Builder(SessionsActivity.this.getParentActivity());
        paramAnonymousAdapterView.setMessage(LocaleController.getString("TerminateSessionQuestion", 2131166330));
        paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165299));
        paramAnonymousAdapterView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
        {
          public void onClick(final DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            paramAnonymous2DialogInterface = new ProgressDialog(SessionsActivity.this.getParentActivity());
            paramAnonymous2DialogInterface.setMessage(LocaleController.getString("Loading", 2131165834));
            paramAnonymous2DialogInterface.setCanceledOnTouchOutside(false);
            paramAnonymous2DialogInterface.setCancelable(false);
            paramAnonymous2DialogInterface.show();
            final TLRPC.TL_authorization localTL_authorization = (TLRPC.TL_authorization)SessionsActivity.this.sessions.get(paramAnonymousInt - SessionsActivity.this.otherSessionsStartRow);
            TLRPC.TL_account_resetAuthorization localTL_account_resetAuthorization = new TLRPC.TL_account_resetAuthorization();
            localTL_account_resetAuthorization.hash = localTL_authorization.hash;
            ConnectionsManager.getInstance().sendRequest(localTL_account_resetAuthorization, new RequestDelegate()
            {
              public void run(TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    try
                    {
                      SessionsActivity.3.2.1.this.val$progressDialog.dismiss();
                      if (paramAnonymous3TL_error == null)
                      {
                        SessionsActivity.this.sessions.remove(SessionsActivity.3.2.1.this.val$authorization);
                        SessionsActivity.this.updateRows();
                        if (SessionsActivity.this.listAdapter != null) {
                          SessionsActivity.this.listAdapter.notifyDataSetChanged();
                        }
                      }
                      return;
                    }
                    catch (Exception localException)
                    {
                      for (;;)
                      {
                        FileLog.e("tmessages", localException);
                      }
                    }
                  }
                });
              }
            });
          }
        });
        paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
        SessionsActivity.this.showDialog(paramAnonymousAdapterView.create());
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.newSessionReceived) {
      loadSessions(true);
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    updateRows();
    loadSessions(false);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.newSessionReceived);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.newSessionReceived);
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
      if (SessionsActivity.this.loading) {
        return 0;
      }
      return SessionsActivity.this.rowCount;
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
      if (paramInt == SessionsActivity.this.terminateAllSessionsRow) {}
      do
      {
        return 0;
        if ((paramInt == SessionsActivity.this.terminateAllSessionsDetailRow) || (paramInt == SessionsActivity.this.otherSessionsTerminateDetail)) {
          return 1;
        }
        if ((paramInt == SessionsActivity.this.currentSessionSectionRow) || (paramInt == SessionsActivity.this.otherSessionsSectionRow)) {
          return 2;
        }
        if (paramInt == SessionsActivity.this.noOtherSessionsRow) {
          return 3;
        }
      } while ((paramInt != SessionsActivity.this.currentSessionRow) && ((paramInt < SessionsActivity.this.otherSessionsStartRow) || (paramInt >= SessionsActivity.this.otherSessionsEndRow)));
      return 4;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool1 = true;
      boolean bool2 = false;
      int i = getItemViewType(paramInt);
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
        if (paramInt == SessionsActivity.this.terminateAllSessionsRow)
        {
          paramView.setTextColor(-2404015);
          paramView.setText(LocaleController.getString("TerminateAllSessions", 2131166328), false);
          paramViewGroup = (ViewGroup)localObject;
        }
      }
      for (;;)
      {
        return paramViewGroup;
        if (i == 1)
        {
          localObject = paramView;
          if (paramView == null) {
            localObject = new TextInfoPrivacyCell(this.mContext);
          }
          if (paramInt == SessionsActivity.this.terminateAllSessionsDetailRow)
          {
            ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("ClearOtherSessionsHelp", 2131165512));
            ((View)localObject).setBackgroundResource(2130837688);
            paramViewGroup = (ViewGroup)localObject;
          }
          else
          {
            paramViewGroup = (ViewGroup)localObject;
            if (paramInt == SessionsActivity.this.otherSessionsTerminateDetail)
            {
              ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("TerminateSessionInfo", 2131166329));
              ((View)localObject).setBackgroundResource(2130837689);
              paramViewGroup = (ViewGroup)localObject;
            }
          }
        }
        else if (i == 2)
        {
          localObject = paramView;
          if (paramView == null)
          {
            localObject = new HeaderCell(this.mContext);
            ((View)localObject).setBackgroundColor(-1);
          }
          if (paramInt == SessionsActivity.this.currentSessionSectionRow)
          {
            ((HeaderCell)localObject).setText(LocaleController.getString("CurrentSession", 2131165538));
            paramViewGroup = (ViewGroup)localObject;
          }
          else
          {
            paramViewGroup = (ViewGroup)localObject;
            if (paramInt == SessionsActivity.this.otherSessionsSectionRow)
            {
              ((HeaderCell)localObject).setText(LocaleController.getString("OtherSessions", 2131166060));
              paramViewGroup = (ViewGroup)localObject;
            }
          }
        }
        else
        {
          if (i == 3)
          {
            paramView = SessionsActivity.this.emptyLayout.getLayoutParams();
            int j;
            int k;
            int m;
            if (paramView != null)
            {
              i = AndroidUtilities.dp(220.0F);
              j = AndroidUtilities.displaySize.y;
              k = ActionBar.getCurrentActionBarHeight();
              m = AndroidUtilities.dp(128.0F);
              if (Build.VERSION.SDK_INT < 21) {
                break label391;
              }
            }
            label391:
            for (paramInt = AndroidUtilities.statusBarHeight;; paramInt = 0)
            {
              paramView.height = Math.max(i, j - k - m - paramInt);
              SessionsActivity.this.emptyLayout.setLayoutParams(paramView);
              return SessionsActivity.this.emptyLayout;
            }
          }
          paramViewGroup = paramView;
          if (i == 4)
          {
            paramViewGroup = paramView;
            if (paramView == null)
            {
              paramViewGroup = new SessionCell(this.mContext);
              paramViewGroup.setBackgroundColor(-1);
            }
            if (paramInt != SessionsActivity.this.currentSessionRow) {
              break;
            }
            paramView = (SessionCell)paramViewGroup;
            localObject = SessionsActivity.this.currentSession;
            bool1 = bool2;
            if (!SessionsActivity.this.sessions.isEmpty()) {
              bool1 = true;
            }
            paramView.setSession((TLRPC.TL_authorization)localObject, bool1);
          }
        }
      }
      paramView = (SessionCell)paramViewGroup;
      Object localObject = (TLRPC.TL_authorization)SessionsActivity.this.sessions.get(paramInt - SessionsActivity.this.otherSessionsStartRow);
      if (paramInt != SessionsActivity.this.otherSessionsEndRow - 1) {}
      for (;;)
      {
        paramView.setSession((TLRPC.TL_authorization)localObject, bool1);
        break;
        bool1 = false;
      }
    }
    
    public int getViewTypeCount()
    {
      return 5;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return SessionsActivity.this.loading;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return (paramInt == SessionsActivity.this.terminateAllSessionsRow) || ((paramInt >= SessionsActivity.this.otherSessionsStartRow) && (paramInt < SessionsActivity.this.otherSessionsEndRow));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/SessionsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */