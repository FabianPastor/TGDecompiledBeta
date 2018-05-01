package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_authorizations;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_getWebAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_resetAuthorization;
import org.telegram.tgnet.TLRPC.TL_account_resetWebAuthorization;
import org.telegram.tgnet.TLRPC.TL_account_resetWebAuthorizations;
import org.telegram.tgnet.TLRPC.TL_account_webAuthorizations;
import org.telegram.tgnet.TLRPC.TL_auth_resetAuthorizations;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_webAuthorization;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class SessionsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private TLRPC.TL_authorization currentSession;
  private int currentSessionRow;
  private int currentSessionSectionRow;
  private int currentType;
  private LinearLayout emptyLayout;
  private EmptyTextProgressView emptyView;
  private ImageView imageView;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean loading;
  private int noOtherSessionsRow;
  private int otherSessionsEndRow;
  private int otherSessionsSectionRow;
  private int otherSessionsStartRow;
  private int otherSessionsTerminateDetail;
  private int rowCount;
  private ArrayList<TLObject> sessions = new ArrayList();
  private int terminateAllSessionsDetailRow;
  private int terminateAllSessionsRow;
  private TextView textView1;
  private TextView textView2;
  
  public SessionsActivity(int paramInt)
  {
    this.currentType = paramInt;
  }
  
  private void loadSessions(boolean paramBoolean)
  {
    if (this.loading) {}
    for (;;)
    {
      return;
      if (!paramBoolean) {
        this.loading = true;
      }
      Object localObject;
      int i;
      if (this.currentType == 0)
      {
        localObject = new TLRPC.TL_account_getAuthorizations();
        i = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                SessionsActivity.access$1302(SessionsActivity.this, false);
                if (paramAnonymousTL_error == null)
                {
                  SessionsActivity.this.sessions.clear();
                  Iterator localIterator = ((TLRPC.TL_account_authorizations)paramAnonymousTLObject).authorizations.iterator();
                  while (localIterator.hasNext())
                  {
                    TLRPC.TL_authorization localTL_authorization = (TLRPC.TL_authorization)localIterator.next();
                    if ((localTL_authorization.flags & 0x1) != 0) {
                      SessionsActivity.access$1402(SessionsActivity.this, localTL_authorization);
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
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(i, this.classGuid);
      }
      else
      {
        localObject = new TLRPC.TL_account_getWebAuthorizations();
        i = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                SessionsActivity.access$1302(SessionsActivity.this, false);
                if (paramAnonymousTL_error == null)
                {
                  SessionsActivity.this.sessions.clear();
                  TLRPC.TL_account_webAuthorizations localTL_account_webAuthorizations = (TLRPC.TL_account_webAuthorizations)paramAnonymousTLObject;
                  MessagesController.getInstance(SessionsActivity.this.currentAccount).putUsers(localTL_account_webAuthorizations.users, false);
                  SessionsActivity.this.sessions.addAll(localTL_account_webAuthorizations.authorizations);
                  SessionsActivity.this.updateRows();
                }
                if (SessionsActivity.this.listAdapter != null) {
                  SessionsActivity.this.listAdapter.notifyDataSetChanged();
                }
              }
            });
          }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(i, this.classGuid);
      }
    }
  }
  
  private void updateRows()
  {
    this.rowCount = 0;
    int i;
    if (this.currentSession != null)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.currentSessionSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.currentSessionRow = i;
      if (!this.sessions.isEmpty()) {
        break label140;
      }
      if ((this.currentType != 1) && (this.currentSession == null)) {
        break label132;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.noOtherSessionsRow = i;
      label88:
      this.terminateAllSessionsRow = -1;
      this.terminateAllSessionsDetailRow = -1;
      this.otherSessionsSectionRow = -1;
      this.otherSessionsStartRow = -1;
      this.otherSessionsEndRow = -1;
    }
    for (this.otherSessionsTerminateDetail = -1;; this.otherSessionsTerminateDetail = i)
    {
      return;
      this.currentSessionRow = -1;
      this.currentSessionSectionRow = -1;
      break;
      label132:
      this.noOtherSessionsRow = -1;
      break label88;
      label140:
      this.noOtherSessionsRow = -1;
      i = this.rowCount;
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
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    FrameLayout localFrameLayout;
    if (this.currentType == 0)
    {
      this.actionBar.setTitle(LocaleController.getString("SessionsTitle", NUM));
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
      localFrameLayout = (FrameLayout)this.fragmentView;
      localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      this.emptyLayout = new LinearLayout(paramContext);
      this.emptyLayout.setOrientation(1);
      this.emptyLayout.setGravity(17);
      this.emptyLayout.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
      this.emptyLayout.setLayoutParams(new AbsListView.LayoutParams(-1, AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()));
      this.imageView = new ImageView(paramContext);
      if (this.currentType != 0) {
        break label603;
      }
      this.imageView.setImageResource(NUM);
      label195:
      this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("sessions_devicesImage"), PorterDuff.Mode.MULTIPLY));
      this.emptyLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2));
      this.textView1 = new TextView(paramContext);
      this.textView1.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.textView1.setGravity(17);
      this.textView1.setTextSize(1, 17.0F);
      this.textView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      if (this.currentType != 0) {
        break label616;
      }
      this.textView1.setText(LocaleController.getString("NoOtherSessions", NUM));
      label317:
      this.emptyLayout.addView(this.textView1, LayoutHelper.createLinear(-2, -2, 17, 0, 16, 0, 0));
      this.textView2 = new TextView(paramContext);
      this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.textView2.setGravity(17);
      this.textView2.setTextSize(1, 17.0F);
      this.textView2.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
      if (this.currentType != 0) {
        break label635;
      }
      this.textView2.setText(LocaleController.getString("NoOtherSessionsInfo", NUM));
    }
    for (;;)
    {
      this.emptyLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2, 17, 0, 14, 0, 0));
      this.emptyView = new EmptyTextProgressView(paramContext);
      this.emptyView.showProgress();
      localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 17));
      this.listView = new RecyclerListView(paramContext);
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setEmptyView(this.emptyView);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, final int paramAnonymousInt)
        {
          if (paramAnonymousInt == SessionsActivity.this.terminateAllSessionsRow) {
            if (SessionsActivity.this.getParentActivity() != null) {}
          }
          AlertDialog.Builder localBuilder;
          final boolean[] arrayOfBoolean;
          for (;;)
          {
            return;
            paramAnonymousView = new AlertDialog.Builder(SessionsActivity.this.getParentActivity());
            if (SessionsActivity.this.currentType == 0) {
              paramAnonymousView.setMessage(LocaleController.getString("AreYouSureSessions", NUM));
            }
            for (;;)
            {
              paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
              paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  if (SessionsActivity.this.currentType == 0)
                  {
                    paramAnonymous2DialogInterface = new TLRPC.TL_auth_resetAuthorizations();
                    ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(paramAnonymous2DialogInterface, new RequestDelegate()
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
                              Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllSessions", NUM), 0).show();
                            }
                            for (;;)
                            {
                              SessionsActivity.this.finishFragment();
                              break;
                              Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", NUM), 0).show();
                            }
                          }
                        });
                        int i = 0;
                        if (i < 3)
                        {
                          paramAnonymous3TLObject = UserConfig.getInstance(i);
                          if (!paramAnonymous3TLObject.isClientActivated()) {}
                          for (;;)
                          {
                            i++;
                            break;
                            paramAnonymous3TLObject.registeredForPush = false;
                            paramAnonymous3TLObject.saveConfig(false);
                            MessagesController.getInstance(i).registerForPush(SharedConfig.pushString);
                            ConnectionsManager.getInstance(i).setUserId(paramAnonymous3TLObject.getClientUserId());
                          }
                        }
                      }
                    });
                  }
                  for (;;)
                  {
                    return;
                    paramAnonymous2DialogInterface = new TLRPC.TL_account_resetWebAuthorizations();
                    ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest(paramAnonymous2DialogInterface, new RequestDelegate()
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
                              Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("TerminateAllWebSessions", NUM), 0).show();
                            }
                            for (;;)
                            {
                              SessionsActivity.this.finishFragment();
                              break;
                              Toast.makeText(SessionsActivity.this.getParentActivity(), LocaleController.getString("UnknownError", NUM), 0).show();
                            }
                          }
                        });
                      }
                    });
                  }
                }
              });
              paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
              SessionsActivity.this.showDialog(paramAnonymousView.create());
              break;
              paramAnonymousView.setMessage(LocaleController.getString("AreYouSureWebSessions", NUM));
            }
            if ((paramAnonymousInt >= SessionsActivity.this.otherSessionsStartRow) && (paramAnonymousInt < SessionsActivity.this.otherSessionsEndRow) && (SessionsActivity.this.getParentActivity() != null))
            {
              localBuilder = new AlertDialog.Builder(SessionsActivity.this.getParentActivity());
              localBuilder.setTitle(LocaleController.getString("AppName", NUM));
              arrayOfBoolean = new boolean[1];
              if (SessionsActivity.this.currentType != 0) {
                break;
              }
              localBuilder.setMessage(LocaleController.getString("TerminateSessionQuestion", NUM));
              localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
              {
                public void onClick(final DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  if (SessionsActivity.this.getParentActivity() == null) {}
                  for (;;)
                  {
                    return;
                    paramAnonymous2DialogInterface = new AlertDialog(SessionsActivity.this.getParentActivity(), 1);
                    paramAnonymous2DialogInterface.setMessage(LocaleController.getString("Loading", NUM));
                    paramAnonymous2DialogInterface.setCanceledOnTouchOutside(false);
                    paramAnonymous2DialogInterface.setCancelable(false);
                    paramAnonymous2DialogInterface.show();
                    final Object localObject1;
                    final Object localObject2;
                    if (SessionsActivity.this.currentType == 0)
                    {
                      localObject1 = (TLRPC.TL_authorization)SessionsActivity.this.sessions.get(paramAnonymousInt - SessionsActivity.this.otherSessionsStartRow);
                      localObject2 = new TLRPC.TL_account_resetAuthorization();
                      ((TLRPC.TL_account_resetAuthorization)localObject2).hash = ((TLRPC.TL_authorization)localObject1).hash;
                      ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest((TLObject)localObject2, new RequestDelegate()
                      {
                        public void run(TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                        {
                          AndroidUtilities.runOnUIThread(new Runnable()
                          {
                            public void run()
                            {
                              try
                              {
                                SessionsActivity.2.3.1.this.val$progressDialog.dismiss();
                                if (paramAnonymous3TL_error == null)
                                {
                                  SessionsActivity.this.sessions.remove(SessionsActivity.2.3.1.this.val$authorization);
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
                                  FileLog.e(localException);
                                }
                              }
                            }
                          });
                        }
                      });
                    }
                    else
                    {
                      localObject2 = (TLRPC.TL_webAuthorization)SessionsActivity.this.sessions.get(paramAnonymousInt - SessionsActivity.this.otherSessionsStartRow);
                      localObject1 = new TLRPC.TL_account_resetWebAuthorization();
                      ((TLRPC.TL_account_resetWebAuthorization)localObject1).hash = ((TLRPC.TL_webAuthorization)localObject2).hash;
                      ConnectionsManager.getInstance(SessionsActivity.this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
                      {
                        public void run(TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                        {
                          AndroidUtilities.runOnUIThread(new Runnable()
                          {
                            public void run()
                            {
                              try
                              {
                                SessionsActivity.2.3.2.this.val$progressDialog.dismiss();
                                if (paramAnonymous3TL_error == null)
                                {
                                  SessionsActivity.this.sessions.remove(SessionsActivity.2.3.2.this.val$authorization);
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
                                  FileLog.e(localException);
                                }
                              }
                            }
                          });
                        }
                      });
                      if (arrayOfBoolean[0] != 0) {
                        MessagesController.getInstance(SessionsActivity.this.currentAccount).blockUser(((TLRPC.TL_webAuthorization)localObject2).bot_id);
                      }
                    }
                  }
                }
              });
              localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
              SessionsActivity.this.showDialog(localBuilder.create());
            }
          }
          paramAnonymousView = (TLRPC.TL_webAuthorization)SessionsActivity.this.sessions.get(paramAnonymousInt - SessionsActivity.this.otherSessionsStartRow);
          localBuilder.setMessage(LocaleController.formatString("TerminateWebSessionQuestion", NUM, new Object[] { paramAnonymousView.domain }));
          FrameLayout localFrameLayout = new FrameLayout(SessionsActivity.this.getParentActivity());
          paramAnonymousView = MessagesController.getInstance(SessionsActivity.this.currentAccount).getUser(Integer.valueOf(paramAnonymousView.bot_id));
          label363:
          CheckBoxCell localCheckBoxCell;
          int i;
          if (paramAnonymousView != null)
          {
            paramAnonymousView = UserObject.getFirstName(paramAnonymousView);
            localCheckBoxCell = new CheckBoxCell(SessionsActivity.this.getParentActivity(), 1);
            localCheckBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            localCheckBoxCell.setText(LocaleController.formatString("TerminateWebSessionStop", NUM, new Object[] { paramAnonymousView }), "", false, false);
            if (!LocaleController.isRTL) {
              break label507;
            }
            i = AndroidUtilities.dp(16.0F);
            label426:
            if (!LocaleController.isRTL) {
              break label517;
            }
          }
          label507:
          label517:
          for (int j = AndroidUtilities.dp(8.0F);; j = AndroidUtilities.dp(16.0F))
          {
            localCheckBoxCell.setPadding(i, 0, j, 0);
            localFrameLayout.addView(localCheckBoxCell, LayoutHelper.createFrame(-1, 48.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
            localCheckBoxCell.setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymous2View)
              {
                if (!paramAnonymous2View.isEnabled()) {
                  return;
                }
                CheckBoxCell localCheckBoxCell = (CheckBoxCell)paramAnonymous2View;
                paramAnonymous2View = arrayOfBoolean;
                if (arrayOfBoolean[0] == 0) {}
                for (int i = 1;; i = 0)
                {
                  paramAnonymous2View[0] = i;
                  localCheckBoxCell.setChecked(arrayOfBoolean[0], true);
                  break;
                }
              }
            });
            localBuilder.setCustomViewOffset(16);
            localBuilder.setView(localFrameLayout);
            break;
            paramAnonymousView = "";
            break label363;
            i = AndroidUtilities.dp(8.0F);
            break label426;
          }
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("WebSessionsTitle", NUM));
      break;
      label603:
      this.imageView.setImageResource(NUM);
      break label195;
      label616:
      this.textView1.setText(LocaleController.getString("NoOtherWebSessions", NUM));
      break label317;
      label635:
      this.textView2.setText(LocaleController.getString("NoOtherWebSessionsInfo", NUM));
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.newSessionReceived) {
      loadSessions(true);
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, HeaderCell.class, SessionCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "sessions_devicesImage"), new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText2"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { SessionCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { SessionCell.class }, new String[] { "onlineTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { SessionCell.class }, new String[] { "onlineTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { SessionCell.class }, new String[] { "detailTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { SessionCell.class }, new String[] { "detailExTextView" }, null, null, null, "windowBackgroundWhiteGrayText3") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    updateRows();
    loadSessions(false);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newSessionReceived);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSessionReceived);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
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
      if (SessionsActivity.this.loading) {}
      for (int i = 0;; i = SessionsActivity.this.rowCount) {
        return i;
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      int j;
      if (paramInt == SessionsActivity.this.terminateAllSessionsRow) {
        j = i;
      }
      for (;;)
      {
        return j;
        if ((paramInt == SessionsActivity.this.terminateAllSessionsDetailRow) || (paramInt == SessionsActivity.this.otherSessionsTerminateDetail))
        {
          j = 1;
        }
        else if ((paramInt == SessionsActivity.this.currentSessionSectionRow) || (paramInt == SessionsActivity.this.otherSessionsSectionRow))
        {
          j = 2;
        }
        else if (paramInt == SessionsActivity.this.noOtherSessionsRow)
        {
          j = 3;
        }
        else if (paramInt != SessionsActivity.this.currentSessionRow)
        {
          j = i;
          if (paramInt >= SessionsActivity.this.otherSessionsStartRow)
          {
            j = i;
            if (paramInt >= SessionsActivity.this.otherSessionsEndRow) {}
          }
        }
        else
        {
          j = 4;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if ((i == SessionsActivity.this.terminateAllSessionsRow) || ((i >= SessionsActivity.this.otherSessionsStartRow) && (i < SessionsActivity.this.otherSessionsEndRow))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = true;
      boolean bool2 = false;
      switch (paramViewHolder.getItemViewType())
      {
      default: 
        paramViewHolder = (SessionCell)paramViewHolder.itemView;
        if (paramInt == SessionsActivity.this.currentSessionRow)
        {
          localObject = SessionsActivity.this.currentSession;
          bool1 = bool2;
          if (!SessionsActivity.this.sessions.isEmpty()) {
            bool1 = true;
          }
          paramViewHolder.setSession((TLObject)localObject, bool1);
        }
        break;
      case 0: 
      case 1: 
      case 2: 
      case 3: 
        do
        {
          for (;;)
          {
            return;
            paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
            if (paramInt == SessionsActivity.this.terminateAllSessionsRow)
            {
              paramViewHolder.setTextColor(Theme.getColor("windowBackgroundWhiteRedText2"));
              if (SessionsActivity.this.currentType == 0)
              {
                paramViewHolder.setText(LocaleController.getString("TerminateAllSessions", NUM), false);
              }
              else
              {
                paramViewHolder.setText(LocaleController.getString("TerminateAllWebSessions", NUM), false);
                continue;
                paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
                if (paramInt == SessionsActivity.this.terminateAllSessionsDetailRow)
                {
                  if (SessionsActivity.this.currentType == 0) {
                    paramViewHolder.setText(LocaleController.getString("ClearOtherSessionsHelp", NUM));
                  }
                  for (;;)
                  {
                    paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                    paramViewHolder.setText(LocaleController.getString("ClearOtherWebSessionsHelp", NUM));
                  }
                }
                if (paramInt == SessionsActivity.this.otherSessionsTerminateDetail)
                {
                  if (SessionsActivity.this.currentType == 0) {
                    paramViewHolder.setText(LocaleController.getString("TerminateSessionInfo", NUM));
                  }
                  for (;;)
                  {
                    paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                    paramViewHolder.setText(LocaleController.getString("TerminateWebSessionInfo", NUM));
                  }
                  paramViewHolder = (HeaderCell)paramViewHolder.itemView;
                  if (paramInt == SessionsActivity.this.currentSessionSectionRow) {
                    paramViewHolder.setText(LocaleController.getString("CurrentSession", NUM));
                  } else if (paramInt == SessionsActivity.this.otherSessionsSectionRow) {
                    if (SessionsActivity.this.currentType == 0) {
                      paramViewHolder.setText(LocaleController.getString("OtherSessions", NUM));
                    } else {
                      paramViewHolder.setText(LocaleController.getString("OtherWebSessions", NUM));
                    }
                  }
                }
              }
            }
          }
          paramViewHolder = SessionsActivity.this.emptyLayout.getLayoutParams();
        } while (paramViewHolder == null);
        int i = AndroidUtilities.dp(220.0F);
        int j = AndroidUtilities.displaySize.y;
        int k = ActionBar.getCurrentActionBarHeight();
        int m = AndroidUtilities.dp(128.0F);
        if (Build.VERSION.SDK_INT >= 21) {}
        for (paramInt = AndroidUtilities.statusBarHeight;; paramInt = 0)
        {
          paramViewHolder.height = Math.max(i, j - k - m - paramInt);
          SessionsActivity.this.emptyLayout.setLayoutParams(paramViewHolder);
          break;
        }
      }
      Object localObject = (TLObject)SessionsActivity.this.sessions.get(paramInt - SessionsActivity.this.otherSessionsStartRow);
      if (paramInt != SessionsActivity.this.otherSessionsEndRow - 1) {}
      for (;;)
      {
        paramViewHolder.setSession((TLObject)localObject, bool1);
        break;
        bool1 = false;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new SessionCell(this.mContext, SessionsActivity.this.currentType);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = SessionsActivity.this.emptyLayout;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/SessionsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */