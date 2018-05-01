package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class ContactsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int add_button = 1;
  private static final int search_button = 0;
  private ActionBarMenuItem addItem;
  private boolean addingToChannel;
  private boolean allowBots = true;
  private boolean allowUsernameSearch = true;
  private int chat_id;
  private boolean checkPermission = true;
  private boolean createSecretChat;
  private boolean creatingChat;
  private ContactsActivityDelegate delegate;
  private boolean destroyAfterSelect;
  private EmptyTextProgressView emptyView;
  private SparseArray<TLRPC.User> ignoreUsers;
  private RecyclerListView listView;
  private ContactsAdapter listViewAdapter;
  private boolean needFinishFragment = true;
  private boolean needForwardCount = true;
  private boolean needPhonebook;
  private boolean onlyUsers;
  private AlertDialog permissionDialog;
  private boolean returnAsResult;
  private SearchAdapter searchListViewAdapter;
  private boolean searchWas;
  private boolean searching;
  private String selectAlertString = null;
  
  public ContactsActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  @TargetApi(23)
  private void askForPermissons()
  {
    Activity localActivity = getParentActivity();
    if ((localActivity == null) || (localActivity.checkSelfPermission("android.permission.READ_CONTACTS") == 0)) {}
    for (;;)
    {
      return;
      ArrayList localArrayList = new ArrayList();
      localArrayList.add("android.permission.READ_CONTACTS");
      localArrayList.add("android.permission.WRITE_CONTACTS");
      localArrayList.add("android.permission.GET_ACCOUNTS");
      localActivity.requestPermissions((String[])localArrayList.toArray(new String[localArrayList.size()]), 1);
    }
  }
  
  private void didSelectResult(final TLRPC.User paramUser, boolean paramBoolean, String paramString)
  {
    if ((paramBoolean) && (this.selectAlertString != null)) {
      if (getParentActivity() != null) {}
    }
    for (;;)
    {
      return;
      if ((paramUser.bot) && (paramUser.bot_nochats) && (!this.addingToChannel))
      {
        try
        {
          Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", NUM), 0).show();
        }
        catch (Exception paramUser)
        {
          FileLog.e(paramUser);
        }
      }
      else
      {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
        localBuilder.setTitle(LocaleController.getString("AppName", NUM));
        String str = LocaleController.formatStringSimple(this.selectAlertString, new Object[] { UserObject.getUserName(paramUser) });
        Object localObject1 = null;
        final Object localObject2 = localObject1;
        paramString = str;
        if (!paramUser.bot)
        {
          localObject2 = localObject1;
          paramString = str;
          if (this.needForwardCount)
          {
            paramString = String.format("%s\n\n%s", new Object[] { str, LocaleController.getString("AddToTheGroupForwardCount", NUM) });
            localObject2 = new EditText(getParentActivity());
            ((EditText)localObject2).setTextSize(1, 18.0F);
            ((EditText)localObject2).setText("50");
            ((EditText)localObject2).setTextColor(Theme.getColor("dialogTextBlack"));
            ((EditText)localObject2).setGravity(17);
            ((EditText)localObject2).setInputType(2);
            ((EditText)localObject2).setImeOptions(6);
            ((EditText)localObject2).setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
            ((EditText)localObject2).addTextChangedListener(new TextWatcher()
            {
              public void afterTextChanged(Editable paramAnonymousEditable)
              {
                for (;;)
                {
                  try
                  {
                    localObject = paramAnonymousEditable.toString();
                    if (((String)localObject).length() != 0)
                    {
                      i = Utilities.parseInt((String)localObject).intValue();
                      if (i < 0)
                      {
                        localObject2.setText("0");
                        localObject2.setSelection(localObject2.length());
                      }
                    }
                    else
                    {
                      return;
                    }
                  }
                  catch (Exception paramAnonymousEditable)
                  {
                    int i;
                    FileLog.e(paramAnonymousEditable);
                    continue;
                    paramAnonymousEditable = new java/lang/StringBuilder;
                    paramAnonymousEditable.<init>();
                    if (((String)localObject).equals("" + i)) {
                      continue;
                    }
                    Object localObject = localObject2;
                    paramAnonymousEditable = new java/lang/StringBuilder;
                    paramAnonymousEditable.<init>();
                    ((EditText)localObject).setText("" + i);
                    localObject2.setSelection(localObject2.length());
                    continue;
                  }
                  if (i <= 300) {
                    continue;
                  }
                  localObject2.setText("300");
                  localObject2.setSelection(localObject2.length());
                }
              }
              
              public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
              
              public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
            });
            localBuilder.setView((View)localObject2);
          }
        }
        localBuilder.setMessage(paramString);
        localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            ContactsActivity localContactsActivity = ContactsActivity.this;
            TLRPC.User localUser = paramUser;
            if (localObject2 != null) {}
            for (paramAnonymousDialogInterface = localObject2.getText().toString();; paramAnonymousDialogInterface = "0")
            {
              localContactsActivity.didSelectResult(localUser, false, paramAnonymousDialogInterface);
              return;
            }
          }
        });
        localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        showDialog(localBuilder.create());
        if (localObject2 != null)
        {
          paramUser = (ViewGroup.MarginLayoutParams)((EditText)localObject2).getLayoutParams();
          if (paramUser != null)
          {
            if ((paramUser instanceof FrameLayout.LayoutParams)) {
              ((FrameLayout.LayoutParams)paramUser).gravity = 1;
            }
            int i = AndroidUtilities.dp(24.0F);
            paramUser.leftMargin = i;
            paramUser.rightMargin = i;
            paramUser.height = AndroidUtilities.dp(36.0F);
            ((EditText)localObject2).setLayoutParams(paramUser);
          }
          ((EditText)localObject2).setSelection(((EditText)localObject2).getText().length());
          continue;
          if (this.delegate != null)
          {
            this.delegate.didSelectContact(paramUser, paramString, this);
            this.delegate = null;
          }
          if (this.needFinishFragment) {
            finishFragment();
          }
        }
      }
    }
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView != null)
    {
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
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    Object localObject;
    int i;
    label181:
    boolean bool1;
    if (this.destroyAfterSelect) {
      if (this.returnAsResult)
      {
        this.actionBar.setTitle(LocaleController.getString("SelectContact", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
        {
          public void onItemClick(int paramAnonymousInt)
          {
            if (paramAnonymousInt == -1) {
              ContactsActivity.this.finishFragment();
            }
            for (;;)
            {
              return;
              if (paramAnonymousInt == 1) {
                ContactsActivity.this.presentFragment(new NewContactActivity());
              }
            }
          }
        });
        localObject = this.actionBar.createMenu();
        ((ActionBarMenu)localObject).addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
        {
          public void onSearchCollapse()
          {
            if (ContactsActivity.this.addItem != null) {
              ContactsActivity.this.addItem.setVisibility(0);
            }
            ContactsActivity.this.searchListViewAdapter.searchDialogs(null);
            ContactsActivity.access$002(ContactsActivity.this, false);
            ContactsActivity.access$302(ContactsActivity.this, false);
            ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
            ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
            ContactsActivity.this.listView.setFastScrollVisible(true);
            ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
            ContactsActivity.this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
          }
          
          public void onSearchExpand()
          {
            ContactsActivity.access$002(ContactsActivity.this, true);
            if (ContactsActivity.this.addItem != null) {
              ContactsActivity.this.addItem.setVisibility(8);
            }
          }
          
          public void onTextChanged(EditText paramAnonymousEditText)
          {
            if (ContactsActivity.this.searchListViewAdapter == null) {}
            for (;;)
            {
              return;
              paramAnonymousEditText = paramAnonymousEditText.getText().toString();
              if (paramAnonymousEditText.length() != 0)
              {
                ContactsActivity.access$302(ContactsActivity.this, true);
                if (ContactsActivity.this.listView != null)
                {
                  ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                  ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                  ContactsActivity.this.listView.setFastScrollVisible(false);
                  ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                }
                if (ContactsActivity.this.emptyView != null) {
                  ContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                }
              }
              ContactsActivity.this.searchListViewAdapter.searchDialogs(paramAnonymousEditText);
            }
          }
        }).getSearchField().setHint(LocaleController.getString("Search", NUM));
        if ((!this.createSecretChat) && (!this.returnAsResult)) {
          this.addItem = ((ActionBarMenu)localObject).addItem(1, NUM);
        }
        this.searchListViewAdapter = new SearchAdapter(paramContext, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, 0);
        if (!this.onlyUsers) {
          break label470;
        }
        i = 1;
        bool1 = this.needPhonebook;
        localObject = this.ignoreUsers;
        if (this.chat_id == 0) {
          break label475;
        }
      }
    }
    label470:
    label475:
    for (boolean bool2 = true;; bool2 = false)
    {
      this.listViewAdapter = new ContactsAdapter(paramContext, i, bool1, (SparseArray)localObject, bool2);
      this.fragmentView = new FrameLayout(paramContext);
      localObject = (FrameLayout)this.fragmentView;
      this.emptyView = new EmptyTextProgressView(paramContext);
      this.emptyView.setShowAtCenter(true);
      this.emptyView.showTextView();
      ((FrameLayout)localObject).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView = new RecyclerListView(paramContext);
      this.listView.setEmptyView(this.emptyView);
      this.listView.setSectionsType(1);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setFastScrollEnabled();
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
      this.listView.setAdapter(this.listViewAdapter);
      ((FrameLayout)localObject).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(final View paramAnonymousView, int paramAnonymousInt)
        {
          if ((ContactsActivity.this.searching) && (ContactsActivity.this.searchWas))
          {
            paramAnonymousView = (TLRPC.User)ContactsActivity.this.searchListViewAdapter.getItem(paramAnonymousInt);
            if (paramAnonymousView != null) {}
          }
          for (;;)
          {
            return;
            Object localObject;
            if (ContactsActivity.this.searchListViewAdapter.isGlobalSearch(paramAnonymousInt))
            {
              localObject = new ArrayList();
              ((ArrayList)localObject).add(paramAnonymousView);
              MessagesController.getInstance(ContactsActivity.this.currentAccount).putUsers((ArrayList)localObject, false);
              MessagesStorage.getInstance(ContactsActivity.this.currentAccount).putUsersAndChats((ArrayList)localObject, null, false, true);
            }
            if (ContactsActivity.this.returnAsResult)
            {
              if ((ContactsActivity.this.ignoreUsers == null) || (ContactsActivity.this.ignoreUsers.indexOfKey(paramAnonymousView.id) < 0)) {
                ContactsActivity.this.didSelectResult(paramAnonymousView, true, null);
              }
            }
            else if (ContactsActivity.this.createSecretChat)
            {
              if (paramAnonymousView.id != UserConfig.getInstance(ContactsActivity.this.currentAccount).getClientUserId())
              {
                ContactsActivity.access$1402(ContactsActivity.this, true);
                SecretChatHelper.getInstance(ContactsActivity.this.currentAccount).startSecretChat(ContactsActivity.this.getParentActivity(), paramAnonymousView);
              }
            }
            else
            {
              localObject = new Bundle();
              ((Bundle)localObject).putInt("user_id", paramAnonymousView.id);
              if (MessagesController.getInstance(ContactsActivity.this.currentAccount).checkCanOpenChat((Bundle)localObject, ContactsActivity.this))
              {
                ContactsActivity.this.presentFragment(new ChatActivity((Bundle)localObject), true);
                continue;
                int i = ContactsActivity.this.listViewAdapter.getSectionForPosition(paramAnonymousInt);
                paramAnonymousInt = ContactsActivity.this.listViewAdapter.getPositionInSectionForPosition(paramAnonymousInt);
                if ((paramAnonymousInt >= 0) && (i >= 0)) {
                  if (((!ContactsActivity.this.onlyUsers) || (ContactsActivity.this.chat_id != 0)) && (i == 0))
                  {
                    if (ContactsActivity.this.needPhonebook)
                    {
                      if (paramAnonymousInt == 0) {
                        ContactsActivity.this.presentFragment(new InviteContactsActivity());
                      }
                    }
                    else if (ContactsActivity.this.chat_id != 0)
                    {
                      if (paramAnonymousInt == 0) {
                        ContactsActivity.this.presentFragment(new GroupInviteActivity(ContactsActivity.this.chat_id));
                      }
                    }
                    else if (paramAnonymousInt == 0)
                    {
                      ContactsActivity.this.presentFragment(new GroupCreateActivity(), false);
                    }
                    else if (paramAnonymousInt == 1)
                    {
                      paramAnonymousView = new Bundle();
                      paramAnonymousView.putBoolean("onlyUsers", true);
                      paramAnonymousView.putBoolean("destroyAfterSelect", true);
                      paramAnonymousView.putBoolean("createSecretChat", true);
                      paramAnonymousView.putBoolean("allowBots", false);
                      ContactsActivity.this.presentFragment(new ContactsActivity(paramAnonymousView), false);
                    }
                    else if (paramAnonymousInt == 2)
                    {
                      paramAnonymousView = MessagesController.getGlobalMainSettings();
                      if ((!BuildVars.DEBUG_VERSION) && (paramAnonymousView.getBoolean("channel_intro", false)))
                      {
                        paramAnonymousView = new Bundle();
                        paramAnonymousView.putInt("step", 0);
                        ContactsActivity.this.presentFragment(new ChannelCreateActivity(paramAnonymousView));
                      }
                      else
                      {
                        ContactsActivity.this.presentFragment(new ChannelIntroActivity());
                        paramAnonymousView.edit().putBoolean("channel_intro", true).commit();
                      }
                    }
                  }
                  else
                  {
                    paramAnonymousView = ContactsActivity.this.listViewAdapter.getItem(i, paramAnonymousInt);
                    if ((paramAnonymousView instanceof TLRPC.User))
                    {
                      paramAnonymousView = (TLRPC.User)paramAnonymousView;
                      if (ContactsActivity.this.returnAsResult)
                      {
                        if ((ContactsActivity.this.ignoreUsers == null) || (ContactsActivity.this.ignoreUsers.indexOfKey(paramAnonymousView.id) < 0)) {
                          ContactsActivity.this.didSelectResult(paramAnonymousView, true, null);
                        }
                      }
                      else if (ContactsActivity.this.createSecretChat)
                      {
                        ContactsActivity.access$1402(ContactsActivity.this, true);
                        SecretChatHelper.getInstance(ContactsActivity.this.currentAccount).startSecretChat(ContactsActivity.this.getParentActivity(), paramAnonymousView);
                      }
                      else
                      {
                        localObject = new Bundle();
                        ((Bundle)localObject).putInt("user_id", paramAnonymousView.id);
                        if (MessagesController.getInstance(ContactsActivity.this.currentAccount).checkCanOpenChat((Bundle)localObject, ContactsActivity.this)) {
                          ContactsActivity.this.presentFragment(new ChatActivity((Bundle)localObject), true);
                        }
                      }
                    }
                    else if ((paramAnonymousView instanceof ContactsController.Contact))
                    {
                      localObject = (ContactsController.Contact)paramAnonymousView;
                      paramAnonymousView = null;
                      if (!((ContactsController.Contact)localObject).phones.isEmpty()) {
                        paramAnonymousView = (String)((ContactsController.Contact)localObject).phones.get(0);
                      }
                      if ((paramAnonymousView != null) && (ContactsActivity.this.getParentActivity() != null))
                      {
                        localObject = new AlertDialog.Builder(ContactsActivity.this.getParentActivity());
                        ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("InviteUser", NUM));
                        ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", NUM));
                        ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                        {
                          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                          {
                            try
                            {
                              paramAnonymous2DialogInterface = new android/content/Intent;
                              paramAnonymous2DialogInterface.<init>("android.intent.action.VIEW", Uri.fromParts("sms", paramAnonymousView, null));
                              paramAnonymous2DialogInterface.putExtra("sms_body", ContactsController.getInstance(ContactsActivity.this.currentAccount).getInviteText(1));
                              ContactsActivity.this.getParentActivity().startActivityForResult(paramAnonymous2DialogInterface, 500);
                              return;
                            }
                            catch (Exception paramAnonymous2DialogInterface)
                            {
                              for (;;)
                              {
                                FileLog.e(paramAnonymous2DialogInterface);
                              }
                            }
                          }
                        });
                        ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        ContactsActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
                      }
                    }
                  }
                }
              }
            }
          }
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == 1) && (ContactsActivity.this.searching) && (ContactsActivity.this.searchWas)) {
            AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
          }
        }
        
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          super.onScrolled(paramAnonymousRecyclerView, paramAnonymousInt1, paramAnonymousInt2);
        }
      });
      return this.fragmentView;
      if (this.createSecretChat)
      {
        this.actionBar.setTitle(LocaleController.getString("NewSecretChat", NUM));
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", NUM));
      break;
      this.actionBar.setTitle(LocaleController.getString("Contacts", NUM));
      break;
      i = 0;
      break label181;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.contactsDidLoaded) {
      if (this.listViewAdapter != null) {
        this.listViewAdapter.notifyDataSetChanged();
      }
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.updateInterfaces)
      {
        paramInt1 = ((Integer)paramVarArgs[0]).intValue();
        if (((paramInt1 & 0x2) != 0) || ((paramInt1 & 0x1) != 0) || ((paramInt1 & 0x4) != 0)) {
          updateVisibleRows(paramInt1);
        }
      }
      else if (paramInt1 == NotificationCenter.encryptedChatCreated)
      {
        if ((this.createSecretChat) && (this.creatingChat))
        {
          paramVarArgs = (TLRPC.EncryptedChat)paramVarArgs[0];
          Bundle localBundle = new Bundle();
          localBundle.putInt("enc_id", paramVarArgs.id);
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
          presentFragment(new ChatActivity(localBundle), true);
        }
      }
      else if ((paramInt1 == NotificationCenter.closeChats) && (!this.creatingChat))
      {
        removeSelfFromStack();
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    Object localObject1 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        if (ContactsActivity.this.listView != null)
        {
          int i = ContactsActivity.this.listView.getChildCount();
          int j = 0;
          if (j < i)
          {
            View localView = ContactsActivity.this.listView.getChildAt(j);
            if ((localView instanceof UserCell)) {
              ((UserCell)localView).update(0);
            }
            for (;;)
            {
              j++;
              break;
              if ((localView instanceof ProfileSearchCell)) {
                ((ProfileSearchCell)localView).update(0);
              }
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
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[] { LetterSectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4");
    Object localObject2 = this.listView;
    Object localObject3 = Theme.dividerPaint;
    ThemeDescription localThemeDescription11 = new ThemeDescription((View)localObject2, 0, new Class[] { View.class }, (Paint)localObject3, null, null, "divider");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
    localObject2 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    localObject3 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "windowBackgroundWhiteGrayText");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusOnlineColor" }, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "windowBackgroundWhiteBlueText");
    Object localObject4 = this.listView;
    Object localObject5 = Theme.avatar_photoDrawable;
    Object localObject6 = Theme.avatar_broadcastDrawable;
    Object localObject7 = Theme.avatar_savedDrawable;
    localObject4 = new ThemeDescription((View)localObject4, 0, new Class[] { UserCell.class }, null, new Drawable[] { localObject5, localObject6, localObject7 }, null, "avatar_text");
    localObject7 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundRed");
    ThemeDescription localThemeDescription17 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundOrange");
    localObject5 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundViolet");
    ThemeDescription localThemeDescription18 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundGreen");
    localObject6 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundCyan");
    ThemeDescription localThemeDescription19 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundBlue");
    localObject1 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundPink");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.listView, 0, new Class[] { TextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.listView, 0, new Class[] { TextCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.listView, 0, new Class[] { GraySectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText2");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GraySectionCell.class }, null, null, null, "graySection");
    Object localObject8 = this.listView;
    Object localObject9 = Theme.dialogs_groupDrawable;
    Object localObject10 = Theme.dialogs_broadcastDrawable;
    Object localObject11 = Theme.dialogs_botDrawable;
    localObject10 = new ThemeDescription((View)localObject8, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localObject9, localObject10, localObject11 }, null, "chats_nameIcon");
    localObject11 = this.listView;
    localObject8 = Theme.dialogs_verifiedCheckDrawable;
    localObject8 = new ThemeDescription((View)localObject11, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localObject8 }, null, "chats_verifiedCheck");
    localObject9 = this.listView;
    localObject11 = Theme.dialogs_verifiedDrawable;
    localObject11 = new ThemeDescription((View)localObject9, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localObject11 }, null, "chats_verifiedBackground");
    localObject9 = this.listView;
    TextPaint localTextPaint = Theme.dialogs_offlinePaint;
    localObject9 = new ThemeDescription((View)localObject9, 0, new Class[] { ProfileSearchCell.class }, localTextPaint, null, null, "windowBackgroundWhiteGrayText3");
    RecyclerListView localRecyclerListView = this.listView;
    localTextPaint = Theme.dialogs_onlinePaint;
    ThemeDescription localThemeDescription24 = new ThemeDescription(localRecyclerListView, 0, new Class[] { ProfileSearchCell.class }, localTextPaint, null, null, "windowBackgroundWhiteBlueText3");
    localRecyclerListView = this.listView;
    localTextPaint = Theme.dialogs_namePaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localObject2, localObject3, localThemeDescription16, localObject4, localObject7, localThemeDescription17, localObject5, localThemeDescription18, localObject6, localThemeDescription19, localObject1, localThemeDescription20, localThemeDescription21, localThemeDescription22, localThemeDescription23, localObject10, localObject8, localObject11, localObject9, localThemeDescription24, new ThemeDescription(localRecyclerListView, 0, new Class[] { ProfileSearchCell.class }, localTextPaint, null, null, "chats_name") };
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    super.onDialogDismiss(paramDialog);
    if ((this.permissionDialog != null) && (paramDialog == this.permissionDialog) && (getParentActivity() != null)) {
      askForPermissons();
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
    if (this.arguments != null)
    {
      this.onlyUsers = getArguments().getBoolean("onlyUsers", false);
      this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
      this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
      this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
      this.selectAlertString = this.arguments.getString("selectAlertString");
      this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
      this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
      this.allowBots = this.arguments.getBoolean("allowBots", true);
      this.addingToChannel = this.arguments.getBoolean("addingToChannel", false);
      this.needFinishFragment = this.arguments.getBoolean("needFinishFragment", true);
      this.chat_id = this.arguments.getInt("chat_id", 0);
    }
    for (;;)
    {
      ContactsController.getInstance(this.currentAccount).checkInviteText();
      return true;
      this.needPhonebook = true;
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    this.delegate = null;
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.actionBar != null) {
      this.actionBar.closeSearchField();
    }
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 1)
    {
      paramInt = 0;
      while (paramInt < paramArrayOfString.length) {
        if ((paramArrayOfInt.length <= paramInt) || (paramArrayOfInt[paramInt] != 0))
        {
          paramInt++;
        }
        else
        {
          String str = paramArrayOfString[paramInt];
          int i = -1;
          switch (str.hashCode())
          {
          }
          for (;;)
          {
            switch (i)
            {
            default: 
              break;
            case 0: 
              ContactsController.getInstance(this.currentAccount).forceImportContacts();
              break;
              if (str.equals("android.permission.READ_CONTACTS")) {
                i = 0;
              }
              break;
            }
          }
        }
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
    if ((this.checkPermission) && (Build.VERSION.SDK_INT >= 23))
    {
      Object localObject = getParentActivity();
      if (localObject != null)
      {
        this.checkPermission = false;
        if (((Activity)localObject).checkSelfPermission("android.permission.READ_CONTACTS") != 0)
        {
          if (!((Activity)localObject).shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
            break label132;
          }
          localObject = new AlertDialog.Builder((Context)localObject);
          ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", NUM));
          ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PermissionContacts", NUM));
          ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", NUM), null);
          localObject = ((AlertDialog.Builder)localObject).create();
          this.permissionDialog = ((AlertDialog)localObject);
          showDialog((Dialog)localObject);
        }
      }
    }
    for (;;)
    {
      return;
      label132:
      askForPermissons();
    }
  }
  
  public void setDelegate(ContactsActivityDelegate paramContactsActivityDelegate)
  {
    this.delegate = paramContactsActivityDelegate;
  }
  
  public void setIgnoreUsers(SparseArray<TLRPC.User> paramSparseArray)
  {
    this.ignoreUsers = paramSparseArray;
  }
  
  public static abstract interface ContactsActivityDelegate
  {
    public abstract void didSelectContact(TLRPC.User paramUser, String paramString, ContactsActivity paramContactsActivity);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ContactsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */