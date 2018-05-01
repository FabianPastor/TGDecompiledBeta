package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_account_privacyRules;
import org.telegram.tgnet.TLRPC.TL_account_setPrivacy;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowContacts;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PrivacyControlActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private int alwaysShareRow;
  private ArrayList<Integer> currentMinus;
  private ArrayList<Integer> currentPlus;
  private int currentType;
  private int detailRow;
  private View doneButton;
  private boolean enableAnimation;
  private int everybodyRow;
  private int lastCheckedType = -1;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int myContactsRow;
  private int neverShareRow;
  private int nobodyRow;
  private int rowCount;
  private int rulesType;
  private int sectionRow;
  private int shareDetailRow;
  private int shareSectionRow;
  
  public PrivacyControlActivity(int paramInt)
  {
    this.rulesType = paramInt;
  }
  
  private void applyCurrentPrivacySettings()
  {
    TLRPC.TL_account_setPrivacy localTL_account_setPrivacy = new TLRPC.TL_account_setPrivacy();
    if (this.rulesType == 2) {
      localTL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
    }
    final Object localObject1;
    int i;
    Object localObject2;
    while ((this.currentType != 0) && (this.currentPlus.size() > 0))
    {
      localObject1 = new TLRPC.TL_inputPrivacyValueAllowUsers();
      i = 0;
      for (;;)
      {
        if (i < this.currentPlus.size())
        {
          localObject2 = MessagesController.getInstance(this.currentAccount).getUser((Integer)this.currentPlus.get(i));
          if (localObject2 != null)
          {
            localObject2 = MessagesController.getInstance(this.currentAccount).getInputUser((TLRPC.User)localObject2);
            if (localObject2 != null) {
              ((TLRPC.TL_inputPrivacyValueAllowUsers)localObject1).users.add(localObject2);
            }
          }
          i++;
          continue;
          if (this.rulesType == 1)
          {
            localTL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyChatInvite();
            break;
          }
          localTL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
          break;
        }
      }
      localTL_account_setPrivacy.rules.add(localObject1);
    }
    if ((this.currentType != 1) && (this.currentMinus.size() > 0))
    {
      localObject1 = new TLRPC.TL_inputPrivacyValueDisallowUsers();
      for (i = 0; i < this.currentMinus.size(); i++)
      {
        localObject2 = MessagesController.getInstance(this.currentAccount).getUser((Integer)this.currentMinus.get(i));
        if (localObject2 != null)
        {
          localObject2 = MessagesController.getInstance(this.currentAccount).getInputUser((TLRPC.User)localObject2);
          if (localObject2 != null) {
            ((TLRPC.TL_inputPrivacyValueDisallowUsers)localObject1).users.add(localObject2);
          }
        }
      }
      localTL_account_setPrivacy.rules.add(localObject1);
    }
    if (this.currentType == 0) {
      localTL_account_setPrivacy.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
    }
    for (;;)
    {
      localObject1 = null;
      if (getParentActivity() != null)
      {
        localObject1 = new AlertDialog(getParentActivity(), 1);
        ((AlertDialog)localObject1).setMessage(LocaleController.getString("Loading", NUM));
        ((AlertDialog)localObject1).setCanceledOnTouchOutside(false);
        ((AlertDialog)localObject1).setCancelable(false);
        ((AlertDialog)localObject1).show();
      }
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_account_setPrivacy, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              try
              {
                if (PrivacyControlActivity.3.this.val$progressDialogFinal != null) {
                  PrivacyControlActivity.3.this.val$progressDialogFinal.dismiss();
                }
                if (paramAnonymousTL_error == null)
                {
                  PrivacyControlActivity.this.finishFragment();
                  TLRPC.TL_account_privacyRules localTL_account_privacyRules = (TLRPC.TL_account_privacyRules)paramAnonymousTLObject;
                  MessagesController.getInstance(PrivacyControlActivity.this.currentAccount).putUsers(localTL_account_privacyRules.users, false);
                  ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).setPrivacyRules(localTL_account_privacyRules.rules, PrivacyControlActivity.this.rulesType);
                  return;
                }
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  FileLog.e(localException);
                  continue;
                  PrivacyControlActivity.this.showErrorAlert();
                }
              }
            }
          });
        }
      }, 2);
      return;
      if (this.currentType == 1) {
        localTL_account_setPrivacy.rules.add(new TLRPC.TL_inputPrivacyValueDisallowAll());
      } else if (this.currentType == 2) {
        localTL_account_setPrivacy.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
      }
    }
  }
  
  private void checkPrivacy()
  {
    this.currentPlus = new ArrayList();
    this.currentMinus = new ArrayList();
    ArrayList localArrayList = ContactsController.getInstance(this.currentAccount).getPrivacyRules(this.rulesType);
    if ((localArrayList == null) || (localArrayList.size() == 0))
    {
      this.currentType = 1;
      return;
    }
    int i = -1;
    int j = 0;
    if (j < localArrayList.size())
    {
      TLRPC.PrivacyRule localPrivacyRule = (TLRPC.PrivacyRule)localArrayList.get(j);
      if ((localPrivacyRule instanceof TLRPC.TL_privacyValueAllowUsers)) {
        this.currentPlus.addAll(localPrivacyRule.users);
      }
      for (;;)
      {
        j++;
        break;
        if ((localPrivacyRule instanceof TLRPC.TL_privacyValueDisallowUsers)) {
          this.currentMinus.addAll(localPrivacyRule.users);
        } else if ((localPrivacyRule instanceof TLRPC.TL_privacyValueAllowAll)) {
          i = 0;
        } else if ((localPrivacyRule instanceof TLRPC.TL_privacyValueDisallowAll)) {
          i = 1;
        } else {
          i = 2;
        }
      }
    }
    if ((i == 0) || ((i == -1) && (this.currentMinus.size() > 0))) {
      this.currentType = 0;
    }
    for (;;)
    {
      if (this.doneButton != null) {
        this.doneButton.setVisibility(8);
      }
      updateRows();
      break;
      if ((i == 2) || ((i == -1) && (this.currentMinus.size() > 0) && (this.currentPlus.size() > 0))) {
        this.currentType = 2;
      } else if ((i == 1) || ((i == -1) && (this.currentPlus.size() > 0))) {
        this.currentType = 1;
      }
    }
  }
  
  private void showErrorAlert()
  {
    if (getParentActivity() == null) {}
    for (;;)
    {
      return;
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", NUM));
      localBuilder.setMessage(LocaleController.getString("PrivacyFloodControlError", NUM));
      localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
      showDialog(localBuilder.create());
    }
  }
  
  private void updateRows()
  {
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.sectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.everybodyRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.myContactsRow = i;
    if ((this.rulesType != 0) && (this.rulesType != 2))
    {
      this.nobodyRow = -1;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.detailRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.shareSectionRow = i;
      if ((this.currentType != 1) && (this.currentType != 2)) {
        break label227;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.alwaysShareRow = i;
      label143:
      if ((this.currentType != 0) && (this.currentType != 2)) {
        break label235;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    label227:
    label235:
    for (this.neverShareRow = i;; this.neverShareRow = -1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.shareDetailRow = i;
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
      return;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.nobodyRow = i;
      break;
      this.alwaysShareRow = -1;
      break label143;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    if (this.rulesType == 2)
    {
      this.actionBar.setTitle(LocaleController.getString("Calls", NUM));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            PrivacyControlActivity.this.finishFragment();
          }
          for (;;)
          {
            return;
            if ((paramAnonymousInt == 1) && (PrivacyControlActivity.this.getParentActivity() != null))
            {
              if ((PrivacyControlActivity.this.currentType != 0) && (PrivacyControlActivity.this.rulesType == 0))
              {
                final SharedPreferences localSharedPreferences = MessagesController.getGlobalMainSettings();
                if (!localSharedPreferences.getBoolean("privacyAlertShowed", false))
                {
                  AlertDialog.Builder localBuilder = new AlertDialog.Builder(PrivacyControlActivity.this.getParentActivity());
                  if (PrivacyControlActivity.this.rulesType == 1) {
                    localBuilder.setMessage(LocaleController.getString("WhoCanAddMeInfo", NUM));
                  }
                  for (;;)
                  {
                    localBuilder.setTitle(LocaleController.getString("AppName", NUM));
                    localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                    {
                      public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                      {
                        PrivacyControlActivity.this.applyCurrentPrivacySettings();
                        localSharedPreferences.edit().putBoolean("privacyAlertShowed", true).commit();
                      }
                    });
                    localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    PrivacyControlActivity.this.showDialog(localBuilder.create());
                    break;
                    localBuilder.setMessage(LocaleController.getString("CustomHelp", NUM));
                  }
                }
              }
              PrivacyControlActivity.this.applyCurrentPrivacySettings();
            }
          }
        }
      });
      if (this.doneButton == null) {
        break label276;
      }
    }
    label276:
    for (int i = this.doneButton.getVisibility();; i = 8)
    {
      this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
      this.doneButton.setVisibility(i);
      this.listAdapter = new ListAdapter(paramContext);
      this.fragmentView = new FrameLayout(paramContext);
      FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
      localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      this.listView = new RecyclerListView(paramContext);
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
      this.listView.setVerticalScrollBarEnabled(false);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, final int paramAnonymousInt)
        {
          boolean bool1 = true;
          if ((paramAnonymousInt == PrivacyControlActivity.this.nobodyRow) || (paramAnonymousInt == PrivacyControlActivity.this.everybodyRow) || (paramAnonymousInt == PrivacyControlActivity.this.myContactsRow))
          {
            i = PrivacyControlActivity.this.currentType;
            if (paramAnonymousInt == PrivacyControlActivity.this.nobodyRow)
            {
              i = 1;
              if (i != PrivacyControlActivity.this.currentType) {
                break label105;
              }
            }
          }
          label105:
          while ((paramAnonymousInt != PrivacyControlActivity.this.neverShareRow) && (paramAnonymousInt != PrivacyControlActivity.this.alwaysShareRow)) {
            for (;;)
            {
              int i;
              return;
              if (paramAnonymousInt == PrivacyControlActivity.this.everybodyRow)
              {
                i = 0;
              }
              else if (paramAnonymousInt == PrivacyControlActivity.this.myContactsRow)
              {
                i = 2;
                continue;
                PrivacyControlActivity.access$602(PrivacyControlActivity.this, true);
                PrivacyControlActivity.this.doneButton.setVisibility(0);
                PrivacyControlActivity.access$802(PrivacyControlActivity.this, PrivacyControlActivity.this.currentType);
                PrivacyControlActivity.access$002(PrivacyControlActivity.this, i);
                PrivacyControlActivity.this.updateRows();
              }
            }
          }
          label201:
          Bundle localBundle;
          if (paramAnonymousInt == PrivacyControlActivity.this.neverShareRow)
          {
            paramAnonymousView = PrivacyControlActivity.this.currentMinus;
            if (!paramAnonymousView.isEmpty()) {
              break label318;
            }
            localBundle = new Bundle();
            if (paramAnonymousInt != PrivacyControlActivity.this.neverShareRow) {
              break label306;
            }
            paramAnonymousView = "isNeverShare";
            label231:
            localBundle.putBoolean(paramAnonymousView, true);
            if (PrivacyControlActivity.this.rulesType == 0) {
              break label312;
            }
          }
          label306:
          label312:
          for (boolean bool2 = true;; bool2 = false)
          {
            localBundle.putBoolean("isGroup", bool2);
            paramAnonymousView = new GroupCreateActivity(localBundle);
            paramAnonymousView.setDelegate(new GroupCreateActivity.GroupCreateActivityDelegate()
            {
              public void didSelectUsers(ArrayList<Integer> paramAnonymous2ArrayList)
              {
                if (paramAnonymousInt == PrivacyControlActivity.this.neverShareRow)
                {
                  PrivacyControlActivity.access$1202(PrivacyControlActivity.this, paramAnonymous2ArrayList);
                  for (i = 0; i < PrivacyControlActivity.this.currentMinus.size(); i++) {
                    PrivacyControlActivity.this.currentPlus.remove(PrivacyControlActivity.this.currentMinus.get(i));
                  }
                }
                PrivacyControlActivity.access$1302(PrivacyControlActivity.this, paramAnonymous2ArrayList);
                for (int i = 0; i < PrivacyControlActivity.this.currentPlus.size(); i++) {
                  PrivacyControlActivity.this.currentMinus.remove(PrivacyControlActivity.this.currentPlus.get(i));
                }
                PrivacyControlActivity.this.doneButton.setVisibility(0);
                PrivacyControlActivity.access$802(PrivacyControlActivity.this, -1);
                PrivacyControlActivity.this.listAdapter.notifyDataSetChanged();
              }
            });
            PrivacyControlActivity.this.presentFragment(paramAnonymousView);
            break;
            paramAnonymousView = PrivacyControlActivity.this.currentPlus;
            break label201;
            paramAnonymousView = "isAlwaysShare";
            break label231;
          }
          label318:
          if (PrivacyControlActivity.this.rulesType != 0)
          {
            bool2 = true;
            label331:
            if (paramAnonymousInt != PrivacyControlActivity.this.alwaysShareRow) {
              break label385;
            }
          }
          for (;;)
          {
            paramAnonymousView = new PrivacyUsersActivity(paramAnonymousView, bool2, bool1);
            paramAnonymousView.setDelegate(new PrivacyUsersActivity.PrivacyActivityDelegate()
            {
              public void didUpdatedUserList(ArrayList<Integer> paramAnonymous2ArrayList, boolean paramAnonymous2Boolean)
              {
                int i;
                if (paramAnonymousInt == PrivacyControlActivity.this.neverShareRow)
                {
                  PrivacyControlActivity.access$1202(PrivacyControlActivity.this, paramAnonymous2ArrayList);
                  if (paramAnonymous2Boolean) {
                    for (i = 0; i < PrivacyControlActivity.this.currentMinus.size(); i++) {
                      PrivacyControlActivity.this.currentPlus.remove(PrivacyControlActivity.this.currentMinus.get(i));
                    }
                  }
                }
                else
                {
                  PrivacyControlActivity.access$1302(PrivacyControlActivity.this, paramAnonymous2ArrayList);
                  if (paramAnonymous2Boolean) {
                    for (i = 0; i < PrivacyControlActivity.this.currentPlus.size(); i++) {
                      PrivacyControlActivity.this.currentMinus.remove(PrivacyControlActivity.this.currentPlus.get(i));
                    }
                  }
                }
                PrivacyControlActivity.this.doneButton.setVisibility(0);
                PrivacyControlActivity.this.listAdapter.notifyDataSetChanged();
              }
            });
            PrivacyControlActivity.this.presentFragment(paramAnonymousView);
            break;
            bool2 = false;
            break label331;
            label385:
            bool1 = false;
          }
        }
      });
      return this.fragmentView;
      if (this.rulesType == 1)
      {
        this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", NUM));
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", NUM));
      break;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.privacyRulesUpdated) {
      checkPrivacy();
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, HeaderCell.class, RadioCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { RadioCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    checkPrivacy();
    updateRows();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
  }
  
  public void onResume()
  {
    super.onResume();
    this.lastCheckedType = -1;
    this.enableAnimation = false;
  }
  
  private static class LinkMovementMethodMy
    extends LinkMovementMethod
  {
    public boolean onTouchEvent(TextView paramTextView, Spannable paramSpannable, MotionEvent paramMotionEvent)
    {
      try
      {
        bool = super.onTouchEvent(paramTextView, paramSpannable, paramMotionEvent);
        return bool;
      }
      catch (Exception paramTextView)
      {
        for (;;)
        {
          FileLog.e(paramTextView);
          boolean bool = false;
        }
      }
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
      return PrivacyControlActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      int j = i;
      if (paramInt != PrivacyControlActivity.this.alwaysShareRow)
      {
        if (paramInt != PrivacyControlActivity.this.neverShareRow) {
          break label30;
        }
        j = i;
      }
      for (;;)
      {
        return j;
        label30:
        if ((paramInt == PrivacyControlActivity.this.shareDetailRow) || (paramInt == PrivacyControlActivity.this.detailRow))
        {
          j = 1;
        }
        else if ((paramInt == PrivacyControlActivity.this.sectionRow) || (paramInt == PrivacyControlActivity.this.shareSectionRow))
        {
          j = 2;
        }
        else if ((paramInt != PrivacyControlActivity.this.everybodyRow) && (paramInt != PrivacyControlActivity.this.myContactsRow))
        {
          j = i;
          if (paramInt != PrivacyControlActivity.this.nobodyRow) {}
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
      if ((i == PrivacyControlActivity.this.nobodyRow) || (i == PrivacyControlActivity.this.everybodyRow) || (i == PrivacyControlActivity.this.myContactsRow) || (i == PrivacyControlActivity.this.neverShareRow) || (i == PrivacyControlActivity.this.alwaysShareRow)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = true;
      boolean bool2 = true;
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        Object localObject = (TextSettingsCell)paramViewHolder.itemView;
        if (paramInt == PrivacyControlActivity.this.alwaysShareRow)
        {
          if (PrivacyControlActivity.this.currentPlus.size() != 0)
          {
            paramViewHolder = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentPlus.size());
            label90:
            if (PrivacyControlActivity.this.rulesType == 0) {
              break label150;
            }
            str = LocaleController.getString("AlwaysAllow", NUM);
            if (PrivacyControlActivity.this.neverShareRow == -1) {
              break label144;
            }
          }
          for (;;)
          {
            ((TextSettingsCell)localObject).setTextAndValue(str, paramViewHolder, bool2);
            break;
            paramViewHolder = LocaleController.getString("EmpryUsersPlaceholder", NUM);
            break label90;
            label144:
            bool2 = false;
          }
          label150:
          String str = LocaleController.getString("AlwaysShareWith", NUM);
          if (PrivacyControlActivity.this.neverShareRow != -1) {}
          for (bool2 = bool1;; bool2 = false)
          {
            ((TextSettingsCell)localObject).setTextAndValue(str, paramViewHolder, bool2);
            break;
          }
        }
        if (paramInt == PrivacyControlActivity.this.neverShareRow)
        {
          if (PrivacyControlActivity.this.currentMinus.size() != 0) {}
          for (paramViewHolder = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentMinus.size());; paramViewHolder = LocaleController.getString("EmpryUsersPlaceholder", NUM))
          {
            if (PrivacyControlActivity.this.rulesType == 0) {
              break label270;
            }
            ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("NeverAllow", NUM), paramViewHolder, false);
            break;
          }
          label270:
          ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("NeverShareWith", NUM), paramViewHolder, false);
          continue;
          paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
          if (paramInt == PrivacyControlActivity.this.detailRow)
          {
            if (PrivacyControlActivity.this.rulesType == 2) {
              paramViewHolder.setText(LocaleController.getString("WhoCanCallMeInfo", NUM));
            }
            for (;;)
            {
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
              break;
              if (PrivacyControlActivity.this.rulesType == 1) {
                paramViewHolder.setText(LocaleController.getString("WhoCanAddMeInfo", NUM));
              } else {
                paramViewHolder.setText(LocaleController.getString("CustomHelp", NUM));
              }
            }
          }
          if (paramInt == PrivacyControlActivity.this.shareDetailRow)
          {
            if (PrivacyControlActivity.this.rulesType == 2) {
              paramViewHolder.setText(LocaleController.getString("CustomCallInfo", NUM));
            }
            for (;;)
            {
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
              break;
              if (PrivacyControlActivity.this.rulesType == 1) {
                paramViewHolder.setText(LocaleController.getString("CustomShareInfo", NUM));
              } else {
                paramViewHolder.setText(LocaleController.getString("CustomShareSettingsHelp", NUM));
              }
            }
            paramViewHolder = (HeaderCell)paramViewHolder.itemView;
            if (paramInt == PrivacyControlActivity.this.sectionRow)
            {
              if (PrivacyControlActivity.this.rulesType == 2) {
                paramViewHolder.setText(LocaleController.getString("WhoCanCallMe", NUM));
              } else if (PrivacyControlActivity.this.rulesType == 1) {
                paramViewHolder.setText(LocaleController.getString("WhoCanAddMe", NUM));
              } else {
                paramViewHolder.setText(LocaleController.getString("LastSeenTitle", NUM));
              }
            }
            else if (paramInt == PrivacyControlActivity.this.shareSectionRow)
            {
              paramViewHolder.setText(LocaleController.getString("AddExceptions", NUM));
              continue;
              paramViewHolder = (RadioCell)paramViewHolder.itemView;
              int i = 0;
              if (paramInt == PrivacyControlActivity.this.everybodyRow)
              {
                localObject = LocaleController.getString("LastSeenEverybody", NUM);
                if (PrivacyControlActivity.this.lastCheckedType == 0)
                {
                  bool2 = true;
                  label627:
                  paramViewHolder.setText((String)localObject, bool2, true);
                  i = 0;
                }
              }
              for (;;)
              {
                if (PrivacyControlActivity.this.lastCheckedType == i)
                {
                  paramViewHolder.setChecked(false, PrivacyControlActivity.this.enableAnimation);
                  break;
                  bool2 = false;
                  break label627;
                  if (paramInt == PrivacyControlActivity.this.myContactsRow)
                  {
                    localObject = LocaleController.getString("LastSeenContacts", NUM);
                    if (PrivacyControlActivity.this.lastCheckedType == 2)
                    {
                      bool2 = true;
                      label706:
                      if (PrivacyControlActivity.this.nobodyRow == -1) {
                        break label740;
                      }
                    }
                    label740:
                    for (bool1 = true;; bool1 = false)
                    {
                      paramViewHolder.setText((String)localObject, bool2, bool1);
                      i = 2;
                      break;
                      bool2 = false;
                      break label706;
                    }
                  }
                  if (paramInt == PrivacyControlActivity.this.nobodyRow)
                  {
                    localObject = LocaleController.getString("LastSeenNobody", NUM);
                    if (PrivacyControlActivity.this.lastCheckedType == 1) {}
                    for (bool2 = true;; bool2 = false)
                    {
                      paramViewHolder.setText((String)localObject, bool2, false);
                      i = 1;
                      break;
                    }
                  }
                }
              }
              if (PrivacyControlActivity.this.currentType == i) {
                paramViewHolder.setChecked(true, PrivacyControlActivity.this.enableAnimation);
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
        paramViewGroup = new RadioCell(this.mContext);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PrivacyControlActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */