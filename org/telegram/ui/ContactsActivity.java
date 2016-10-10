package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
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
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseSectionsAdapter;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.LetterSectionsListView;

public class ContactsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int add_button = 1;
  private static final int search_button = 0;
  private boolean allowBots = true;
  private boolean allowUsernameSearch = true;
  private int chat_id;
  private boolean createSecretChat;
  private boolean creatingChat = false;
  private ContactsActivityDelegate delegate;
  private boolean destroyAfterSelect;
  private TextView emptyTextView;
  private HashMap<Integer, TLRPC.User> ignoreUsers;
  private LetterSectionsListView listView;
  private BaseSectionsAdapter listViewAdapter;
  private boolean needForwardCount = true;
  private boolean needPhonebook;
  private boolean onlyUsers;
  private boolean returnAsResult;
  private SearchAdapter searchListViewAdapter;
  private boolean searchWas;
  private boolean searching;
  private String selectAlertString = null;
  
  public ContactsActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private void didSelectResult(final TLRPC.User paramUser, boolean paramBoolean, final String paramString)
  {
    if ((paramBoolean) && (this.selectAlertString != null))
    {
      if (getParentActivity() == null) {}
      do
      {
        return;
        if ((paramUser.bot) && (paramUser.bot_nochats)) {
          try
          {
            Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", 2131165366), 0).show();
            return;
          }
          catch (Exception paramUser)
          {
            FileLog.e("tmessages", paramUser);
            return;
          }
        }
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
        localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
        String str2 = LocaleController.formatStringSimple(this.selectAlertString, new Object[] { UserObject.getUserName(paramUser) });
        Object localObject = null;
        paramString = (String)localObject;
        String str1 = str2;
        if (!paramUser.bot)
        {
          paramString = (String)localObject;
          str1 = str2;
          if (this.needForwardCount)
          {
            str1 = String.format("%s\n\n%s", new Object[] { str2, LocaleController.getString("AddToTheGroupForwardCount", 2131165273) });
            paramString = new EditText(getParentActivity());
            paramString.setTextSize(18.0F);
            paramString.setText("50");
            paramString.setGravity(17);
            paramString.setInputType(2);
            paramString.setImeOptions(6);
            paramString.addTextChangedListener(new TextWatcher()
            {
              public void afterTextChanged(Editable paramAnonymousEditable)
              {
                int i;
                try
                {
                  paramAnonymousEditable = paramAnonymousEditable.toString();
                  if (paramAnonymousEditable.length() == 0) {
                    return;
                  }
                  i = Utilities.parseInt(paramAnonymousEditable).intValue();
                  if (i < 0)
                  {
                    paramString.setText("0");
                    paramString.setSelection(paramString.length());
                    return;
                  }
                  if (i > 300)
                  {
                    paramString.setText("300");
                    paramString.setSelection(paramString.length());
                    return;
                  }
                }
                catch (Exception paramAnonymousEditable)
                {
                  FileLog.e("tmessages", paramAnonymousEditable);
                  return;
                }
                if (!paramAnonymousEditable.equals("" + i))
                {
                  paramString.setText("" + i);
                  paramString.setSelection(paramString.length());
                }
              }
              
              public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
              
              public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
            });
            localBuilder.setView(paramString);
          }
        }
        localBuilder.setMessage(str1);
        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            ContactsActivity localContactsActivity = ContactsActivity.this;
            TLRPC.User localUser = paramUser;
            if (paramString != null) {}
            for (paramAnonymousDialogInterface = paramString.getText().toString();; paramAnonymousDialogInterface = "0")
            {
              localContactsActivity.didSelectResult(localUser, false, paramAnonymousDialogInterface);
              return;
            }
          }
        });
        localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
        showDialog(localBuilder.create());
      } while (paramString == null);
      paramUser = (ViewGroup.MarginLayoutParams)paramString.getLayoutParams();
      if (paramUser != null)
      {
        if ((paramUser instanceof FrameLayout.LayoutParams)) {
          ((FrameLayout.LayoutParams)paramUser).gravity = 1;
        }
        int i = AndroidUtilities.dp(10.0F);
        paramUser.leftMargin = i;
        paramUser.rightMargin = i;
        paramString.setLayoutParams(paramUser);
      }
      paramString.setSelection(paramString.getText().length());
      return;
    }
    if (this.delegate != null)
    {
      this.delegate.didSelectContact(paramUser, paramString);
      this.delegate = null;
    }
    finishFragment();
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView != null)
    {
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
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    label166:
    boolean bool1;
    if (this.destroyAfterSelect) {
      if (this.returnAsResult)
      {
        this.actionBar.setTitle(LocaleController.getString("SelectContact", 2131166231));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
        {
          public void onItemClick(int paramAnonymousInt)
          {
            if (paramAnonymousInt == -1) {
              ContactsActivity.this.finishFragment();
            }
            while (paramAnonymousInt != 1) {
              return;
            }
            ContactsActivity.this.presentFragment(new NewContactActivity());
          }
        });
        Object localObject1 = this.actionBar.createMenu();
        ((ActionBarMenu)localObject1).addItem(0, 2130837711).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
        {
          public void onSearchCollapse()
          {
            ContactsActivity.this.searchListViewAdapter.searchDialogs(null);
            ContactsActivity.access$002(ContactsActivity.this, false);
            ContactsActivity.access$202(ContactsActivity.this, false);
            ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
            ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
            ContactsActivity.this.listView.setFastScrollAlwaysVisible(true);
            ContactsActivity.this.listView.setFastScrollEnabled(true);
            ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
            ContactsActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131165932));
          }
          
          public void onSearchExpand()
          {
            ContactsActivity.access$002(ContactsActivity.this, true);
          }
          
          public void onTextChanged(EditText paramAnonymousEditText)
          {
            if (ContactsActivity.this.searchListViewAdapter == null) {
              return;
            }
            paramAnonymousEditText = paramAnonymousEditText.getText().toString();
            if (paramAnonymousEditText.length() != 0)
            {
              ContactsActivity.access$202(ContactsActivity.this, true);
              if (ContactsActivity.this.listView != null)
              {
                ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                ContactsActivity.this.listView.setFastScrollAlwaysVisible(false);
                ContactsActivity.this.listView.setFastScrollEnabled(false);
                ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
              }
              if (ContactsActivity.this.emptyTextView != null) {
                ContactsActivity.this.emptyTextView.setText(LocaleController.getString("NoResult", 2131165949));
              }
            }
            ContactsActivity.this.searchListViewAdapter.searchDialogs(paramAnonymousEditText);
          }
        }).getSearchField().setHint(LocaleController.getString("Search", 2131166206));
        ((ActionBarMenu)localObject1).addItem(1, 2130837506);
        this.searchListViewAdapter = new SearchAdapter(paramContext, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots);
        if (!this.onlyUsers) {
          break label704;
        }
        i = 1;
        boolean bool2 = this.needPhonebook;
        localObject1 = this.ignoreUsers;
        if (this.chat_id == 0) {
          break label709;
        }
        bool1 = true;
        label187:
        this.listViewAdapter = new ContactsAdapter(paramContext, i, bool2, (HashMap)localObject1, bool1);
        this.fragmentView = new FrameLayout(paramContext);
        localObject1 = new LinearLayout(paramContext);
        ((LinearLayout)localObject1).setVisibility(4);
        ((LinearLayout)localObject1).setOrientation(1);
        ((FrameLayout)this.fragmentView).addView((View)localObject1);
        Object localObject2 = (FrameLayout.LayoutParams)((LinearLayout)localObject1).getLayoutParams();
        ((FrameLayout.LayoutParams)localObject2).width = -1;
        ((FrameLayout.LayoutParams)localObject2).height = -1;
        ((FrameLayout.LayoutParams)localObject2).gravity = 48;
        ((LinearLayout)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
        ((LinearLayout)localObject1).setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            return true;
          }
        });
        this.emptyTextView = new TextView(paramContext);
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(1, 20.0F);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131165932));
        ((LinearLayout)localObject1).addView(this.emptyTextView);
        localObject2 = (LinearLayout.LayoutParams)this.emptyTextView.getLayoutParams();
        ((LinearLayout.LayoutParams)localObject2).width = -1;
        ((LinearLayout.LayoutParams)localObject2).height = -1;
        ((LinearLayout.LayoutParams)localObject2).weight = 0.5F;
        this.emptyTextView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
        localObject2 = new FrameLayout(paramContext);
        ((LinearLayout)localObject1).addView((View)localObject2);
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)((FrameLayout)localObject2).getLayoutParams();
        localLayoutParams.width = -1;
        localLayoutParams.height = -1;
        localLayoutParams.weight = 0.5F;
        ((FrameLayout)localObject2).setLayoutParams(localLayoutParams);
        this.listView = new LetterSectionsListView(paramContext);
        this.listView.setEmptyView((View)localObject1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setFastScrollEnabled(true);
        this.listView.setScrollBarStyle(33554432);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setFastScrollAlwaysVisible(true);
        paramContext = this.listView;
        if (!LocaleController.isRTL) {
          break label714;
        }
      }
    }
    label704:
    label709:
    label714:
    for (int i = 1;; i = 2)
    {
      paramContext.setVerticalScrollbarPosition(i);
      ((FrameLayout)this.fragmentView).addView(this.listView);
      paramContext = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
      paramContext.width = -1;
      paramContext.height = -1;
      this.listView.setLayoutParams(paramContext);
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(final AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if ((ContactsActivity.this.searching) && (ContactsActivity.this.searchWas))
          {
            paramAnonymousAdapterView = (TLRPC.User)ContactsActivity.this.searchListViewAdapter.getItem(paramAnonymousInt);
            if (paramAnonymousAdapterView != null) {}
          }
          label606:
          label764:
          do
          {
            do
            {
              do
              {
                do
                {
                  int i;
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            do
                            {
                              do
                              {
                                do
                                {
                                  return;
                                  if (ContactsActivity.this.searchListViewAdapter.isGlobalSearch(paramAnonymousInt))
                                  {
                                    paramAnonymousView = new ArrayList();
                                    paramAnonymousView.add(paramAnonymousAdapterView);
                                    MessagesController.getInstance().putUsers(paramAnonymousView, false);
                                    MessagesStorage.getInstance().putUsersAndChats(paramAnonymousView, null, false, true);
                                  }
                                  if (!ContactsActivity.this.returnAsResult) {
                                    break;
                                  }
                                } while ((ContactsActivity.this.ignoreUsers != null) && (ContactsActivity.this.ignoreUsers.containsKey(Integer.valueOf(paramAnonymousAdapterView.id))));
                                ContactsActivity.this.didSelectResult(paramAnonymousAdapterView, true, null);
                                return;
                                if (!ContactsActivity.this.createSecretChat) {
                                  break;
                                }
                              } while (paramAnonymousAdapterView.id == UserConfig.getClientUserId());
                              ContactsActivity.access$1002(ContactsActivity.this, true);
                              SecretChatHelper.getInstance().startSecretChat(ContactsActivity.this.getParentActivity(), paramAnonymousAdapterView);
                              return;
                              paramAnonymousView = new Bundle();
                              paramAnonymousView.putInt("user_id", paramAnonymousAdapterView.id);
                            } while (!MessagesController.checkCanOpenChat(paramAnonymousView, ContactsActivity.this));
                            ContactsActivity.this.presentFragment(new ChatActivity(paramAnonymousView), true);
                            return;
                            i = ContactsActivity.this.listViewAdapter.getSectionForPosition(paramAnonymousInt);
                            paramAnonymousInt = ContactsActivity.this.listViewAdapter.getPositionInSectionForPosition(paramAnonymousInt);
                          } while ((paramAnonymousInt < 0) || (i < 0));
                          if (((ContactsActivity.this.onlyUsers) && (ContactsActivity.this.chat_id == 0)) || (i != 0)) {
                            break label606;
                          }
                          if (!ContactsActivity.this.needPhonebook) {
                            break;
                          }
                        } while (paramAnonymousInt != 0);
                        try
                        {
                          paramAnonymousAdapterView = new Intent("android.intent.action.SEND");
                          paramAnonymousAdapterView.setType("text/plain");
                          paramAnonymousAdapterView.putExtra("android.intent.extra.TEXT", ContactsController.getInstance().getInviteText());
                          ContactsActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(paramAnonymousAdapterView, LocaleController.getString("InviteFriends", 2131165761)), 500);
                          return;
                        }
                        catch (Exception paramAnonymousAdapterView)
                        {
                          FileLog.e("tmessages", paramAnonymousAdapterView);
                          return;
                        }
                        if (ContactsActivity.this.chat_id == 0) {
                          break;
                        }
                      } while (paramAnonymousInt != 0);
                      ContactsActivity.this.presentFragment(new GroupInviteActivity(ContactsActivity.this.chat_id));
                      return;
                      if (paramAnonymousInt != 0) {
                        break;
                      }
                    } while (!MessagesController.isFeatureEnabled("chat_create", ContactsActivity.this));
                    ContactsActivity.this.presentFragment(new GroupCreateActivity(), false);
                    return;
                    if (paramAnonymousInt == 1)
                    {
                      paramAnonymousAdapterView = new Bundle();
                      paramAnonymousAdapterView.putBoolean("onlyUsers", true);
                      paramAnonymousAdapterView.putBoolean("destroyAfterSelect", true);
                      paramAnonymousAdapterView.putBoolean("createSecretChat", true);
                      paramAnonymousAdapterView.putBoolean("allowBots", false);
                      ContactsActivity.this.presentFragment(new ContactsActivity(paramAnonymousAdapterView), false);
                      return;
                    }
                  } while ((paramAnonymousInt != 2) || (!MessagesController.isFeatureEnabled("broadcast_create", ContactsActivity.this)));
                  paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                  if (paramAnonymousAdapterView.getBoolean("channel_intro", false))
                  {
                    paramAnonymousAdapterView = new Bundle();
                    paramAnonymousAdapterView.putInt("step", 0);
                    ContactsActivity.this.presentFragment(new ChannelCreateActivity(paramAnonymousAdapterView));
                    return;
                  }
                  ContactsActivity.this.presentFragment(new ChannelIntroActivity());
                  paramAnonymousAdapterView.edit().putBoolean("channel_intro", true).commit();
                  return;
                  paramAnonymousAdapterView = ContactsActivity.this.listViewAdapter.getItem(i, paramAnonymousInt);
                  if (!(paramAnonymousAdapterView instanceof TLRPC.User)) {
                    break label764;
                  }
                  paramAnonymousAdapterView = (TLRPC.User)paramAnonymousAdapterView;
                  if (!ContactsActivity.this.returnAsResult) {
                    break;
                  }
                } while ((ContactsActivity.this.ignoreUsers != null) && (ContactsActivity.this.ignoreUsers.containsKey(Integer.valueOf(paramAnonymousAdapterView.id))));
                ContactsActivity.this.didSelectResult(paramAnonymousAdapterView, true, null);
                return;
                if (ContactsActivity.this.createSecretChat)
                {
                  ContactsActivity.access$1002(ContactsActivity.this, true);
                  SecretChatHelper.getInstance().startSecretChat(ContactsActivity.this.getParentActivity(), paramAnonymousAdapterView);
                  return;
                }
                paramAnonymousView = new Bundle();
                paramAnonymousView.putInt("user_id", paramAnonymousAdapterView.id);
              } while (!MessagesController.checkCanOpenChat(paramAnonymousView, ContactsActivity.this));
              ContactsActivity.this.presentFragment(new ChatActivity(paramAnonymousView), true);
              return;
            } while (!(paramAnonymousAdapterView instanceof ContactsController.Contact));
            paramAnonymousView = (ContactsController.Contact)paramAnonymousAdapterView;
            paramAnonymousAdapterView = null;
            if (!paramAnonymousView.phones.isEmpty()) {
              paramAnonymousAdapterView = (String)paramAnonymousView.phones.get(0);
            }
          } while ((paramAnonymousAdapterView == null) || (ContactsActivity.this.getParentActivity() == null));
          paramAnonymousView = new AlertDialog.Builder(ContactsActivity.this.getParentActivity());
          paramAnonymousView.setMessage(LocaleController.getString("InviteUser", 2131165767));
          paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
          paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              try
              {
                paramAnonymous2DialogInterface = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", paramAnonymousAdapterView, null));
                paramAnonymous2DialogInterface.putExtra("sms_body", LocaleController.getString("InviteText", 2131165763));
                ContactsActivity.this.getParentActivity().startActivityForResult(paramAnonymous2DialogInterface, 500);
                return;
              }
              catch (Exception paramAnonymous2DialogInterface)
              {
                FileLog.e("tmessages", paramAnonymous2DialogInterface);
              }
            }
          });
          paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
          ContactsActivity.this.showDialog(paramAnonymousView.create());
        }
      });
      this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
      {
        public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if (paramAnonymousAbsListView.isFastScrollEnabled()) {
            AndroidUtilities.clearDrawableAnimation(paramAnonymousAbsListView);
          }
        }
        
        public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == 1) && (ContactsActivity.this.searching) && (ContactsActivity.this.searchWas)) {
            AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
          }
        }
      });
      return this.fragmentView;
      if (this.createSecretChat)
      {
        this.actionBar.setTitle(LocaleController.getString("NewSecretChat", 2131165925));
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", 2131165918));
      break;
      this.actionBar.setTitle(LocaleController.getString("Contacts", 2131165522));
      break;
      i = 0;
      break label166;
      bool1 = false;
      break label187;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.contactsDidLoaded) {
      if (this.listViewAdapter != null) {
        this.listViewAdapter.notifyDataSetChanged();
      }
    }
    do
    {
      do
      {
        do
        {
          return;
          if (paramInt != NotificationCenter.updateInterfaces) {
            break;
          }
          paramInt = ((Integer)paramVarArgs[0]).intValue();
        } while (((paramInt & 0x2) == 0) && ((paramInt & 0x1) == 0) && ((paramInt & 0x4) == 0));
        updateVisibleRows(paramInt);
        return;
        if (paramInt != NotificationCenter.encryptedChatCreated) {
          break;
        }
      } while ((!this.createSecretChat) || (!this.creatingChat));
      paramVarArgs = (TLRPC.EncryptedChat)paramVarArgs[0];
      Bundle localBundle = new Bundle();
      localBundle.putInt("enc_id", paramVarArgs.id);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
      presentFragment(new ChatActivity(localBundle), true);
      return;
    } while ((paramInt != NotificationCenter.closeChats) || (this.creatingChat));
    removeSelfFromStack();
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
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
      this.chat_id = this.arguments.getInt("chat_id", 0);
    }
    for (;;)
    {
      ContactsController.getInstance().checkInviteText();
      return true;
      this.needPhonebook = true;
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    this.delegate = null;
  }
  
  public void onPause()
  {
    super.onPause();
    if (this.actionBar != null) {
      this.actionBar.closeSearchField();
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
  
  public void setDelegate(ContactsActivityDelegate paramContactsActivityDelegate)
  {
    this.delegate = paramContactsActivityDelegate;
  }
  
  public void setIgnoreUsers(HashMap<Integer, TLRPC.User> paramHashMap)
  {
    this.ignoreUsers = paramHashMap;
  }
  
  public static abstract interface ContactsActivityDelegate
  {
    public abstract void didSelectContact(TLRPC.User paramUser, String paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ContactsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */