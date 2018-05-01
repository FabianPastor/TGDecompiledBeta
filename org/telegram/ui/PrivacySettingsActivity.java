package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_accountDaysTTL;
import org.telegram.tgnet.TLRPC.TL_account_setAccountTTL;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_payments_clearSavedInfo;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.voip.VoIPHelper;

public class PrivacySettingsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int blockedRow;
  private int botsDetailRow;
  private int botsSectionRow;
  private int callsDetailRow;
  private int callsP2PRow;
  private int callsRow;
  private int callsSectionRow;
  private boolean[] clear = new boolean[2];
  private int contactsDetailRow;
  private int contactsSectionRow;
  private int contactsSyncRow;
  private boolean currentSync;
  private int deleteAccountDetailRow;
  private int deleteAccountRow;
  private int deleteAccountSectionRow;
  private int groupsDetailRow;
  private int groupsRow;
  private int lastSeenRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean newSync;
  private int passcodeRow;
  private int passwordRow;
  private int paymentsClearRow;
  private int privacySectionRow;
  private int rowCount;
  private int secretDetailRow;
  private int secretSectionRow;
  private int secretWebpageRow;
  private int securitySectionRow;
  private int sessionsDetailRow;
  private int sessionsRow;
  private int webSessionsRow;
  
  private String formatRulesString(int paramInt)
  {
    ArrayList localArrayList = ContactsController.getInstance(this.currentAccount).getPrivacyRules(paramInt);
    Object localObject;
    if (localArrayList.size() == 0) {
      localObject = LocaleController.getString("LastSeenNobody", NUM);
    }
    for (;;)
    {
      return (String)localObject;
      paramInt = -1;
      int i = 0;
      int j = 0;
      int k = 0;
      if (k < localArrayList.size())
      {
        localObject = (TLRPC.PrivacyRule)localArrayList.get(k);
        if ((localObject instanceof TLRPC.TL_privacyValueAllowUsers)) {
          i += ((TLRPC.PrivacyRule)localObject).users.size();
        }
        for (;;)
        {
          k++;
          break;
          if ((localObject instanceof TLRPC.TL_privacyValueDisallowUsers)) {
            j += ((TLRPC.PrivacyRule)localObject).users.size();
          } else if ((localObject instanceof TLRPC.TL_privacyValueAllowAll)) {
            paramInt = 0;
          } else if ((localObject instanceof TLRPC.TL_privacyValueDisallowAll)) {
            paramInt = 1;
          } else {
            paramInt = 2;
          }
        }
      }
      if ((paramInt == 0) || ((paramInt == -1) && (j > 0)))
      {
        if (j == 0) {
          localObject = LocaleController.getString("LastSeenEverybody", NUM);
        } else {
          localObject = LocaleController.formatString("LastSeenEverybodyMinus", NUM, new Object[] { Integer.valueOf(j) });
        }
      }
      else if ((paramInt == 2) || ((paramInt == -1) && (j > 0) && (i > 0)))
      {
        if ((i == 0) && (j == 0)) {
          localObject = LocaleController.getString("LastSeenContacts", NUM);
        } else if ((i != 0) && (j != 0)) {
          localObject = LocaleController.formatString("LastSeenContactsMinusPlus", NUM, new Object[] { Integer.valueOf(j), Integer.valueOf(i) });
        } else if (j != 0) {
          localObject = LocaleController.formatString("LastSeenContactsMinus", NUM, new Object[] { Integer.valueOf(j) });
        } else {
          localObject = LocaleController.formatString("LastSeenContactsPlus", NUM, new Object[] { Integer.valueOf(i) });
        }
      }
      else if ((paramInt == 1) || (i > 0))
      {
        if (i == 0) {
          localObject = LocaleController.getString("LastSeenNobody", NUM);
        } else {
          localObject = LocaleController.formatString("LastSeenNobodyPlus", NUM, new Object[] { Integer.valueOf(i) });
        }
      }
      else {
        localObject = "unknown";
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("PrivacySettings", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          PrivacySettingsActivity.this.finishFragment();
        }
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    });
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if (!paramAnonymousView.isEnabled()) {}
        for (;;)
        {
          return;
          if (paramAnonymousInt == PrivacySettingsActivity.this.blockedRow)
          {
            PrivacySettingsActivity.this.presentFragment(new BlockedUsersActivity());
          }
          else if (paramAnonymousInt == PrivacySettingsActivity.this.sessionsRow)
          {
            PrivacySettingsActivity.this.presentFragment(new SessionsActivity(0));
          }
          else if (paramAnonymousInt == PrivacySettingsActivity.this.webSessionsRow)
          {
            PrivacySettingsActivity.this.presentFragment(new SessionsActivity(1));
          }
          else
          {
            Object localObject1;
            Object localObject2;
            Object localObject3;
            Object localObject4;
            if (paramAnonymousInt == PrivacySettingsActivity.this.deleteAccountRow)
            {
              if (PrivacySettingsActivity.this.getParentActivity() != null)
              {
                localObject1 = new AlertDialog.Builder(PrivacySettingsActivity.this.getParentActivity());
                ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("DeleteAccountTitle", NUM));
                paramAnonymousView = LocaleController.formatPluralString("Months", 1);
                localObject2 = LocaleController.formatPluralString("Months", 3);
                localObject3 = LocaleController.formatPluralString("Months", 6);
                String str = LocaleController.formatPluralString("Years", 1);
                localObject4 = new DialogInterface.OnClickListener()
                {
                  public void onClick(final DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    int i = 0;
                    if (paramAnonymous2Int == 0) {
                      i = 30;
                    }
                    for (;;)
                    {
                      paramAnonymous2DialogInterface = new AlertDialog(PrivacySettingsActivity.this.getParentActivity(), 1);
                      paramAnonymous2DialogInterface.setMessage(LocaleController.getString("Loading", NUM));
                      paramAnonymous2DialogInterface.setCanceledOnTouchOutside(false);
                      paramAnonymous2DialogInterface.setCancelable(false);
                      paramAnonymous2DialogInterface.show();
                      final TLRPC.TL_account_setAccountTTL localTL_account_setAccountTTL = new TLRPC.TL_account_setAccountTTL();
                      localTL_account_setAccountTTL.ttl = new TLRPC.TL_accountDaysTTL();
                      localTL_account_setAccountTTL.ttl.days = i;
                      ConnectionsManager.getInstance(PrivacySettingsActivity.this.currentAccount).sendRequest(localTL_account_setAccountTTL, new RequestDelegate()
                      {
                        public void run(final TLObject paramAnonymous3TLObject, TLRPC.TL_error paramAnonymous3TL_error)
                        {
                          AndroidUtilities.runOnUIThread(new Runnable()
                          {
                            public void run()
                            {
                              try
                              {
                                PrivacySettingsActivity.3.1.1.this.val$progressDialog.dismiss();
                                if ((paramAnonymous3TLObject instanceof TLRPC.TL_boolTrue))
                                {
                                  ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).setDeleteAccountTTL(PrivacySettingsActivity.3.1.1.this.val$req.ttl.days);
                                  PrivacySettingsActivity.this.listAdapter.notifyDataSetChanged();
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
                      return;
                      if (paramAnonymous2Int == 1) {
                        i = 90;
                      } else if (paramAnonymous2Int == 2) {
                        i = 182;
                      } else if (paramAnonymous2Int == 3) {
                        i = 365;
                      }
                    }
                  }
                };
                ((AlertDialog.Builder)localObject1).setItems(new CharSequence[] { paramAnonymousView, localObject2, localObject3, str }, (DialogInterface.OnClickListener)localObject4);
                ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                PrivacySettingsActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
              }
            }
            else if (paramAnonymousInt == PrivacySettingsActivity.this.lastSeenRow)
            {
              PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(0));
            }
            else if (paramAnonymousInt == PrivacySettingsActivity.this.callsRow)
            {
              PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(2));
            }
            else if (paramAnonymousInt == PrivacySettingsActivity.this.groupsRow)
            {
              PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(1));
            }
            else if (paramAnonymousInt == PrivacySettingsActivity.this.passwordRow)
            {
              PrivacySettingsActivity.this.presentFragment(new TwoStepVerificationActivity(0));
            }
            else if (paramAnonymousInt == PrivacySettingsActivity.this.passcodeRow)
            {
              if (SharedConfig.passcodeHash.length() > 0) {
                PrivacySettingsActivity.this.presentFragment(new PasscodeActivity(2));
              } else {
                PrivacySettingsActivity.this.presentFragment(new PasscodeActivity(0));
              }
            }
            else
            {
              label465:
              boolean bool;
              if (paramAnonymousInt == PrivacySettingsActivity.this.secretWebpageRow)
              {
                if (MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview == 1)
                {
                  MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview = 0;
                  MessagesController.getGlobalMainSettings().edit().putInt("secretWebpage2", MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview).commit();
                  if (!(paramAnonymousView instanceof TextCheckCell)) {
                    continue;
                  }
                  paramAnonymousView = (TextCheckCell)paramAnonymousView;
                  if (MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview != 1) {
                    break label557;
                  }
                }
                label557:
                for (bool = true;; bool = false)
                {
                  paramAnonymousView.setChecked(bool);
                  break;
                  MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview = 1;
                  break label465;
                }
              }
              else
              {
                if (paramAnonymousInt == PrivacySettingsActivity.this.contactsSyncRow)
                {
                  localObject1 = PrivacySettingsActivity.this;
                  if (!PrivacySettingsActivity.this.newSync) {}
                  for (bool = true;; bool = false)
                  {
                    PrivacySettingsActivity.access$1902((PrivacySettingsActivity)localObject1, bool);
                    if ((paramAnonymousView instanceof TextCheckCell)) {
                      ((TextCheckCell)paramAnonymousView).setChecked(PrivacySettingsActivity.this.newSync);
                    }
                    PrivacySettingsActivity.this.listAdapter.notifyItemChanged(PrivacySettingsActivity.this.contactsDetailRow);
                    break;
                  }
                }
                if (paramAnonymousInt == PrivacySettingsActivity.this.callsP2PRow)
                {
                  paramAnonymousView = new AlertDialog.Builder(PrivacySettingsActivity.this.getParentActivity()).setTitle(LocaleController.getString("PrivacyCallsP2PTitle", NUM));
                  localObject4 = LocaleController.getString("LastSeenEverybody", NUM);
                  localObject1 = LocaleController.getString("LastSeenContacts", NUM);
                  localObject2 = LocaleController.getString("LastSeenNobody", NUM);
                  localObject3 = new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                    {
                      MessagesController.getMainSettings(PrivacySettingsActivity.this.currentAccount).edit().putInt("calls_p2p_new", paramAnonymous2Int).commit();
                      PrivacySettingsActivity.this.listAdapter.notifyDataSetChanged();
                    }
                  };
                  paramAnonymousView.setItems(new String[] { localObject4, localObject1, localObject2 }, (DialogInterface.OnClickListener)localObject3).setNegativeButton(LocaleController.getString("Cancel", NUM), null).show();
                }
                else if (paramAnonymousInt == PrivacySettingsActivity.this.paymentsClearRow)
                {
                  localObject2 = new BottomSheet.Builder(PrivacySettingsActivity.this.getParentActivity());
                  ((BottomSheet.Builder)localObject2).setApplyTopPadding(false);
                  ((BottomSheet.Builder)localObject2).setApplyBottomPadding(false);
                  localObject1 = new LinearLayout(PrivacySettingsActivity.this.getParentActivity());
                  ((LinearLayout)localObject1).setOrientation(1);
                  paramAnonymousInt = 0;
                  if (paramAnonymousInt < 2)
                  {
                    paramAnonymousView = null;
                    if (paramAnonymousInt == 0) {
                      paramAnonymousView = LocaleController.getString("PrivacyClearShipping", NUM);
                    }
                    for (;;)
                    {
                      PrivacySettingsActivity.this.clear[paramAnonymousInt] = 1;
                      localObject4 = new CheckBoxCell(PrivacySettingsActivity.this.getParentActivity(), 1);
                      ((CheckBoxCell)localObject4).setTag(Integer.valueOf(paramAnonymousInt));
                      ((CheckBoxCell)localObject4).setBackgroundDrawable(Theme.getSelectorDrawable(false));
                      ((LinearLayout)localObject1).addView((View)localObject4, LayoutHelper.createLinear(-1, 48));
                      ((CheckBoxCell)localObject4).setText(paramAnonymousView, null, true, true);
                      ((CheckBoxCell)localObject4).setTextColor(Theme.getColor("dialogTextBlack"));
                      ((CheckBoxCell)localObject4).setOnClickListener(new View.OnClickListener()
                      {
                        public void onClick(View paramAnonymous2View)
                        {
                          CheckBoxCell localCheckBoxCell = (CheckBoxCell)paramAnonymous2View;
                          int i = ((Integer)localCheckBoxCell.getTag()).intValue();
                          paramAnonymous2View = PrivacySettingsActivity.this.clear;
                          if (PrivacySettingsActivity.this.clear[i] == 0) {}
                          for (int j = 1;; j = 0)
                          {
                            paramAnonymous2View[i] = j;
                            localCheckBoxCell.setChecked(PrivacySettingsActivity.this.clear[i], true);
                            return;
                          }
                        }
                      });
                      paramAnonymousInt++;
                      break;
                      if (paramAnonymousInt == 1) {
                        paramAnonymousView = LocaleController.getString("PrivacyClearPayment", NUM);
                      }
                    }
                  }
                  paramAnonymousView = new BottomSheet.BottomSheetCell(PrivacySettingsActivity.this.getParentActivity(), 1);
                  paramAnonymousView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                  paramAnonymousView.setTextAndIcon(LocaleController.getString("ClearButton", NUM).toUpperCase(), 0);
                  paramAnonymousView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
                  paramAnonymousView.setOnClickListener(new View.OnClickListener()
                  {
                    public void onClick(View paramAnonymous2View)
                    {
                      try
                      {
                        if (PrivacySettingsActivity.this.visibleDialog != null) {
                          PrivacySettingsActivity.this.visibleDialog.dismiss();
                        }
                        paramAnonymous2View = new AlertDialog.Builder(PrivacySettingsActivity.this.getParentActivity());
                        paramAnonymous2View.setTitle(LocaleController.getString("AppName", NUM));
                        paramAnonymous2View.setMessage(LocaleController.getString("PrivacyPaymentsClearAlert", NUM));
                        paramAnonymous2View.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                        {
                          public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                          {
                            paramAnonymous3DialogInterface = new TLRPC.TL_payments_clearSavedInfo();
                            paramAnonymous3DialogInterface.credentials = PrivacySettingsActivity.this.clear[1];
                            paramAnonymous3DialogInterface.info = PrivacySettingsActivity.this.clear[0];
                            UserConfig.getInstance(PrivacySettingsActivity.this.currentAccount).tmpPassword = null;
                            UserConfig.getInstance(PrivacySettingsActivity.this.currentAccount).saveConfig(false);
                            ConnectionsManager.getInstance(PrivacySettingsActivity.this.currentAccount).sendRequest(paramAnonymous3DialogInterface, new RequestDelegate()
                            {
                              public void run(TLObject paramAnonymous4TLObject, TLRPC.TL_error paramAnonymous4TL_error) {}
                            });
                          }
                        });
                        paramAnonymous2View.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        PrivacySettingsActivity.this.showDialog(paramAnonymous2View.create());
                        return;
                      }
                      catch (Exception paramAnonymous2View)
                      {
                        for (;;)
                        {
                          FileLog.e(paramAnonymous2View);
                        }
                      }
                    }
                  });
                  ((LinearLayout)localObject1).addView(paramAnonymousView, LayoutHelper.createLinear(-1, 48));
                  ((BottomSheet.Builder)localObject2).setCustomView((View)localObject1);
                  PrivacySettingsActivity.this.showDialog(((BottomSheet.Builder)localObject2).create());
                }
              }
            }
          }
        }
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if ((paramInt1 == NotificationCenter.privacyRulesUpdated) && (this.listAdapter != null)) {
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, HeaderCell.class, TextCheckCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
    boolean bool = UserConfig.getInstance(this.currentAccount).syncContacts;
    this.newSync = bool;
    this.currentSync = bool;
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.privacySectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.blockedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.lastSeenRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.groupsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.groupsDetailRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.securitySectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.passcodeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.passwordRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.sessionsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.sessionsDetailRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.deleteAccountSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.deleteAccountRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.deleteAccountDetailRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.botsSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.paymentsClearRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.webSessionsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.botsDetailRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.contactsSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.contactsSyncRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.contactsDetailRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsP2PRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.callsDetailRow = i;
    if (MessagesController.getInstance(this.currentAccount).secretWebpagePreview != 1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.secretSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.secretWebpageRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    for (this.secretDetailRow = i;; this.secretDetailRow = -1)
    {
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
      VoIPHelper.upgradeP2pSetting(this.currentAccount);
      return true;
      this.secretSectionRow = -1;
      this.secretWebpageRow = -1;
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
    if (this.currentSync != this.newSync)
    {
      UserConfig.getInstance(this.currentAccount).syncContacts = this.newSync;
      UserConfig.getInstance(this.currentAccount).saveConfig(false);
      if (this.newSync)
      {
        ContactsController.getInstance(this.currentAccount).forceImportContacts();
        if (getParentActivity() != null) {
          Toast.makeText(getParentActivity(), LocaleController.getString("SyncContactsAdded", NUM), 0).show();
        }
      }
    }
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
      return PrivacySettingsActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      int j = i;
      if (paramInt != PrivacySettingsActivity.this.lastSeenRow)
      {
        j = i;
        if (paramInt != PrivacySettingsActivity.this.blockedRow)
        {
          j = i;
          if (paramInt != PrivacySettingsActivity.this.deleteAccountRow)
          {
            j = i;
            if (paramInt != PrivacySettingsActivity.this.sessionsRow)
            {
              j = i;
              if (paramInt != PrivacySettingsActivity.this.webSessionsRow)
              {
                j = i;
                if (paramInt != PrivacySettingsActivity.this.passwordRow)
                {
                  j = i;
                  if (paramInt != PrivacySettingsActivity.this.passcodeRow)
                  {
                    j = i;
                    if (paramInt != PrivacySettingsActivity.this.groupsRow)
                    {
                      j = i;
                      if (paramInt != PrivacySettingsActivity.this.paymentsClearRow)
                      {
                        if (paramInt != PrivacySettingsActivity.this.callsP2PRow) {
                          break label134;
                        }
                        j = i;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      for (;;)
      {
        return j;
        label134:
        if ((paramInt == PrivacySettingsActivity.this.deleteAccountDetailRow) || (paramInt == PrivacySettingsActivity.this.groupsDetailRow) || (paramInt == PrivacySettingsActivity.this.sessionsDetailRow) || (paramInt == PrivacySettingsActivity.this.secretDetailRow) || (paramInt == PrivacySettingsActivity.this.botsDetailRow) || (paramInt == PrivacySettingsActivity.this.callsDetailRow) || (paramInt == PrivacySettingsActivity.this.contactsDetailRow))
        {
          j = 1;
        }
        else if ((paramInt == PrivacySettingsActivity.this.securitySectionRow) || (paramInt == PrivacySettingsActivity.this.deleteAccountSectionRow) || (paramInt == PrivacySettingsActivity.this.privacySectionRow) || (paramInt == PrivacySettingsActivity.this.secretSectionRow) || (paramInt == PrivacySettingsActivity.this.botsSectionRow) || (paramInt == PrivacySettingsActivity.this.callsSectionRow) || (paramInt == PrivacySettingsActivity.this.contactsSectionRow))
        {
          j = 2;
        }
        else if (paramInt != PrivacySettingsActivity.this.secretWebpageRow)
        {
          j = i;
          if (paramInt != PrivacySettingsActivity.this.contactsSyncRow) {}
        }
        else
        {
          j = 3;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if ((i == PrivacySettingsActivity.this.passcodeRow) || (i == PrivacySettingsActivity.this.passwordRow) || (i == PrivacySettingsActivity.this.blockedRow) || (i == PrivacySettingsActivity.this.sessionsRow) || (i == PrivacySettingsActivity.this.secretWebpageRow) || (i == PrivacySettingsActivity.this.webSessionsRow) || ((i == PrivacySettingsActivity.this.groupsRow) && (!ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingGroupInfo())) || ((i == PrivacySettingsActivity.this.lastSeenRow) && (!ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingLastSeenInfo())) || ((i == PrivacySettingsActivity.this.callsRow) && (!ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingCallsInfo())) || ((i == PrivacySettingsActivity.this.deleteAccountRow) && (!ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingDeleteInfo())) || (i == PrivacySettingsActivity.this.paymentsClearRow) || (i == PrivacySettingsActivity.this.callsP2PRow) || (i == PrivacySettingsActivity.this.contactsSyncRow)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = true;
      int i = 1;
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        Object localObject = (TextSettingsCell)paramViewHolder.itemView;
        if (paramInt == PrivacySettingsActivity.this.blockedRow)
        {
          ((TextSettingsCell)localObject).setText(LocaleController.getString("BlockedUsers", NUM), true);
        }
        else if (paramInt == PrivacySettingsActivity.this.sessionsRow)
        {
          ((TextSettingsCell)localObject).setText(LocaleController.getString("SessionsTitle", NUM), false);
        }
        else if (paramInt == PrivacySettingsActivity.this.webSessionsRow)
        {
          ((TextSettingsCell)localObject).setText(LocaleController.getString("WebSessionsTitle", NUM), false);
        }
        else if (paramInt == PrivacySettingsActivity.this.passwordRow)
        {
          ((TextSettingsCell)localObject).setText(LocaleController.getString("TwoStepVerification", NUM), true);
        }
        else if (paramInt == PrivacySettingsActivity.this.passcodeRow)
        {
          ((TextSettingsCell)localObject).setText(LocaleController.getString("Passcode", NUM), true);
        }
        else
        {
          if (paramInt == PrivacySettingsActivity.this.lastSeenRow)
          {
            if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingLastSeenInfo()) {}
            for (paramViewHolder = LocaleController.getString("Loading", NUM);; paramViewHolder = PrivacySettingsActivity.this.formatRulesString(0))
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("PrivacyLastSeen", NUM), paramViewHolder, true);
              break;
            }
          }
          if (paramInt == PrivacySettingsActivity.this.callsRow)
          {
            if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingCallsInfo()) {}
            for (paramViewHolder = LocaleController.getString("Loading", NUM);; paramViewHolder = PrivacySettingsActivity.this.formatRulesString(2))
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("Calls", NUM), paramViewHolder, true);
              break;
            }
          }
          if (paramInt == PrivacySettingsActivity.this.groupsRow)
          {
            if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingGroupInfo()) {}
            for (paramViewHolder = LocaleController.getString("Loading", NUM);; paramViewHolder = PrivacySettingsActivity.this.formatRulesString(1))
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("GroupsAndChannels", NUM), paramViewHolder, false);
              break;
            }
          }
          if (paramInt == PrivacySettingsActivity.this.deleteAccountRow)
          {
            if (ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getLoadingDeleteInfo()) {
              paramViewHolder = LocaleController.getString("Loading", NUM);
            }
            for (;;)
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor", NUM), paramViewHolder, false);
              break;
              paramInt = ContactsController.getInstance(PrivacySettingsActivity.this.currentAccount).getDeleteAccountTTL();
              if (paramInt <= 182) {
                paramViewHolder = LocaleController.formatPluralString("Months", paramInt / 30);
              } else if (paramInt == 365) {
                paramViewHolder = LocaleController.formatPluralString("Years", paramInt / 365);
              } else {
                paramViewHolder = LocaleController.formatPluralString("Days", paramInt);
              }
            }
          }
          if (paramInt == PrivacySettingsActivity.this.paymentsClearRow)
          {
            ((TextSettingsCell)localObject).setText(LocaleController.getString("PrivacyPaymentsClear", NUM), true);
          }
          else if (paramInt == PrivacySettingsActivity.this.callsP2PRow)
          {
            paramViewHolder = MessagesController.getMainSettings(PrivacySettingsActivity.this.currentAccount);
            if (MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).defaultP2pContacts)
            {
              paramInt = i;
              label562:
              switch (paramViewHolder.getInt("calls_p2p_new", paramInt))
              {
              default: 
                paramViewHolder = LocaleController.getString("LastSeenEverybody", NUM);
              }
            }
            for (;;)
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("PrivacyCallsP2PTitle", NUM), paramViewHolder, false);
              break;
              paramInt = 0;
              break label562;
              paramViewHolder = LocaleController.getString("LastSeenContacts", NUM);
              continue;
              paramViewHolder = LocaleController.getString("LastSeenNobody", NUM);
            }
            localObject = (TextInfoPrivacyCell)paramViewHolder.itemView;
            if (paramInt == PrivacySettingsActivity.this.deleteAccountDetailRow)
            {
              ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("DeleteAccountHelp", NUM));
              ((TextInfoPrivacyCell)localObject).setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
            else if (paramInt == PrivacySettingsActivity.this.groupsDetailRow)
            {
              ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("GroupsAndChannelsHelp", NUM));
              ((TextInfoPrivacyCell)localObject).setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
            else if (paramInt == PrivacySettingsActivity.this.sessionsDetailRow)
            {
              ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("SessionsInfo", NUM));
              ((TextInfoPrivacyCell)localObject).setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
            else if (paramInt == PrivacySettingsActivity.this.secretDetailRow)
            {
              ((TextInfoPrivacyCell)localObject).setText("");
              ((TextInfoPrivacyCell)localObject).setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
            else if (paramInt == PrivacySettingsActivity.this.botsDetailRow)
            {
              ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("PrivacyBotsInfo", NUM));
              ((TextInfoPrivacyCell)localObject).setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
            else
            {
              if (paramInt == PrivacySettingsActivity.this.callsDetailRow)
              {
                ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("PrivacyCallsP2PHelp", NUM));
                paramViewHolder = this.mContext;
                if (PrivacySettingsActivity.this.secretSectionRow == -1) {}
                for (paramInt = NUM;; paramInt = NUM)
                {
                  ((TextInfoPrivacyCell)localObject).setBackgroundDrawable(Theme.getThemedDrawable(paramViewHolder, paramInt, "windowBackgroundGrayShadow"));
                  break;
                }
              }
              if (paramInt == PrivacySettingsActivity.this.contactsDetailRow)
              {
                if (PrivacySettingsActivity.this.newSync) {
                  ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("SyncContactsInfoOn", NUM));
                }
                for (;;)
                {
                  ((TextInfoPrivacyCell)localObject).setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                  break;
                  ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("SyncContactsInfoOff", NUM));
                }
                paramViewHolder = (HeaderCell)paramViewHolder.itemView;
                if (paramInt == PrivacySettingsActivity.this.privacySectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("PrivacyTitle", NUM));
                }
                else if (paramInt == PrivacySettingsActivity.this.securitySectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("SecurityTitle", NUM));
                }
                else if (paramInt == PrivacySettingsActivity.this.deleteAccountSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("DeleteAccountTitle", NUM));
                }
                else if (paramInt == PrivacySettingsActivity.this.secretSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("SecretChat", NUM));
                }
                else if (paramInt == PrivacySettingsActivity.this.botsSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("PrivacyBots", NUM));
                }
                else if (paramInt == PrivacySettingsActivity.this.callsSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("Calls", NUM));
                }
                else if (paramInt == PrivacySettingsActivity.this.contactsSectionRow)
                {
                  paramViewHolder.setText(LocaleController.getString("Contacts", NUM));
                  continue;
                  localObject = (TextCheckCell)paramViewHolder.itemView;
                  if (paramInt == PrivacySettingsActivity.this.secretWebpageRow)
                  {
                    paramViewHolder = LocaleController.getString("SecretWebPage", NUM);
                    if (MessagesController.getInstance(PrivacySettingsActivity.this.currentAccount).secretWebpagePreview == 1) {}
                    for (;;)
                    {
                      ((TextCheckCell)localObject).setTextAndCheck(paramViewHolder, bool, false);
                      break;
                      bool = false;
                    }
                  }
                  if (paramInt == PrivacySettingsActivity.this.contactsSyncRow) {
                    ((TextCheckCell)localObject).setTextAndCheck(LocaleController.getString("SyncContacts", NUM), PrivacySettingsActivity.this.newSync, false);
                  }
                }
              }
            }
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new TextCheckCell(this.mContext);
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
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PrivacySettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */