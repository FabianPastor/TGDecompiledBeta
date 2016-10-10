package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;

public class SetAdminsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int allAdminsInfoRow;
  private int allAdminsRow;
  private TLRPC.Chat chat;
  private int chat_id;
  private TLRPC.ChatFull info;
  private ListAdapter listAdapter;
  private ListView listView;
  private ArrayList<TLRPC.ChatParticipant> participants = new ArrayList();
  private int rowCount;
  private SearchAdapter searchAdapter;
  private EmptyTextProgressView searchEmptyView;
  private ActionBarMenuItem searchItem;
  private boolean searchWas;
  private boolean searching;
  private int usersEndRow;
  private int usersStartRow;
  
  public SetAdminsActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chat_id = paramBundle.getInt("chat_id");
  }
  
  private int getChatAdminParticipantType(TLRPC.ChatParticipant paramChatParticipant)
  {
    if ((paramChatParticipant instanceof TLRPC.TL_chatParticipantCreator)) {
      return 0;
    }
    if ((paramChatParticipant instanceof TLRPC.TL_chatParticipantAdmin)) {
      return 1;
    }
    return 2;
  }
  
  private void updateChatParticipants()
  {
    if (this.info == null) {}
    while (this.participants.size() == this.info.participants.participants.size()) {
      return;
    }
    this.participants.clear();
    this.participants.addAll(this.info.participants.participants);
    try
    {
      Collections.sort(this.participants, new Comparator()
      {
        public int compare(TLRPC.ChatParticipant paramAnonymousChatParticipant1, TLRPC.ChatParticipant paramAnonymousChatParticipant2)
        {
          int i = SetAdminsActivity.this.getChatAdminParticipantType(paramAnonymousChatParticipant1);
          int j = SetAdminsActivity.this.getChatAdminParticipantType(paramAnonymousChatParticipant2);
          if (i > j) {}
          do
          {
            do
            {
              do
              {
                return 1;
                if (i < j) {
                  return -1;
                }
                if (i != j) {
                  break label216;
                }
                paramAnonymousChatParticipant2 = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousChatParticipant2.user_id));
                paramAnonymousChatParticipant1 = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousChatParticipant1.user_id));
                j = 0;
                int k = 0;
                i = j;
                if (paramAnonymousChatParticipant2 != null)
                {
                  i = j;
                  if (paramAnonymousChatParticipant2.status != null) {
                    i = paramAnonymousChatParticipant2.status.expires;
                  }
                }
                j = k;
                if (paramAnonymousChatParticipant1 != null)
                {
                  j = k;
                  if (paramAnonymousChatParticipant1.status != null) {
                    j = paramAnonymousChatParticipant1.status.expires;
                  }
                }
                if ((i <= 0) || (j <= 0)) {
                  break;
                }
              } while (i > j);
              if (i < j) {
                return -1;
              }
              return 0;
              if ((i >= 0) || (j >= 0)) {
                break;
              }
            } while (i > j);
            if (i < j) {
              return -1;
            }
            return 0;
            if (((i < 0) && (j > 0)) || ((i == 0) && (j != 0))) {
              return -1;
            }
          } while (((j < 0) && (i > 0)) || ((j == 0) && (i != 0)));
          label216:
          return 0;
        }
      });
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  private void updateRowsIds()
  {
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.allAdminsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.allAdminsInfoRow = i;
    if (this.info != null)
    {
      this.usersStartRow = this.rowCount;
      this.rowCount += this.participants.size();
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.usersEndRow = i;
      if ((this.searchItem != null) && (!this.searchWas)) {
        this.searchItem.setVisibility(0);
      }
    }
    for (;;)
    {
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
      return;
      this.usersStartRow = -1;
      this.usersEndRow = -1;
      if (this.searchItem != null) {
        this.searchItem.setVisibility(8);
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("SetAdminsTitle", 2131166266));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          SetAdminsActivity.this.finishFragment();
        }
      }
    });
    this.searchItem = this.actionBar.createMenu().addItem(0, 2130837711).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public void onSearchCollapse()
      {
        SetAdminsActivity.access$002(SetAdminsActivity.this, false);
        SetAdminsActivity.access$302(SetAdminsActivity.this, false);
        if (SetAdminsActivity.this.listView != null)
        {
          SetAdminsActivity.this.listView.setEmptyView(null);
          SetAdminsActivity.this.searchEmptyView.setVisibility(8);
          if (SetAdminsActivity.this.listView.getAdapter() != SetAdminsActivity.this.listAdapter)
          {
            SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.listAdapter);
            SetAdminsActivity.this.fragmentView.setBackgroundColor(-986896);
          }
        }
        if (SetAdminsActivity.this.searchAdapter != null) {
          SetAdminsActivity.this.searchAdapter.search(null);
        }
      }
      
      public void onSearchExpand()
      {
        SetAdminsActivity.access$002(SetAdminsActivity.this, true);
        SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.searchEmptyView);
      }
      
      public void onTextChanged(EditText paramAnonymousEditText)
      {
        paramAnonymousEditText = paramAnonymousEditText.getText().toString();
        if (paramAnonymousEditText.length() != 0)
        {
          SetAdminsActivity.access$302(SetAdminsActivity.this, true);
          if ((SetAdminsActivity.this.searchAdapter != null) && (SetAdminsActivity.this.listView.getAdapter() != SetAdminsActivity.this.searchAdapter))
          {
            SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.searchAdapter);
            SetAdminsActivity.this.fragmentView.setBackgroundColor(-1);
          }
          if ((SetAdminsActivity.this.searchEmptyView != null) && (SetAdminsActivity.this.listView.getEmptyView() != SetAdminsActivity.this.searchEmptyView))
          {
            SetAdminsActivity.this.searchEmptyView.showTextView();
            SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.searchEmptyView);
          }
        }
        if (SetAdminsActivity.this.searchAdapter != null) {
          SetAdminsActivity.this.searchAdapter.search(paramAnonymousEditText);
        }
      }
    });
    this.searchItem.getSearchField().setHint(LocaleController.getString("Search", 2131166206));
    this.listAdapter = new ListAdapter(paramContext);
    this.searchAdapter = new SearchAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.fragmentView.setBackgroundColor(-986896);
    this.listView = new ListView(paramContext);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setDrawSelectorOnTop(true);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        int i;
        int j;
        if ((SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) || ((paramAnonymousInt >= SetAdminsActivity.this.usersStartRow) && (paramAnonymousInt < SetAdminsActivity.this.usersEndRow)))
        {
          UserCell localUserCell = (UserCell)paramAnonymousView;
          SetAdminsActivity.access$1002(SetAdminsActivity.this, MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
          int k = -1;
          if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter)
          {
            paramAnonymousView = SetAdminsActivity.this.searchAdapter.getItem(paramAnonymousInt);
            i = 0;
            j = k;
            paramAnonymousAdapterView = paramAnonymousView;
            if (i < SetAdminsActivity.this.participants.size())
            {
              if (((TLRPC.ChatParticipant)SetAdminsActivity.this.participants.get(i)).user_id == paramAnonymousView.user_id)
              {
                paramAnonymousAdapterView = paramAnonymousView;
                j = i;
              }
            }
            else {
              label162:
              if ((j != -1) && (!(paramAnonymousAdapterView instanceof TLRPC.TL_chatParticipantCreator)))
              {
                if (!(paramAnonymousAdapterView instanceof TLRPC.TL_chatParticipant)) {
                  break label452;
                }
                paramAnonymousView = new TLRPC.TL_chatParticipantAdmin();
                paramAnonymousView.user_id = paramAnonymousAdapterView.user_id;
                paramAnonymousView.date = paramAnonymousAdapterView.date;
                paramAnonymousView.inviter_id = paramAnonymousAdapterView.inviter_id;
                label214:
                SetAdminsActivity.this.participants.set(j, paramAnonymousView);
                i = SetAdminsActivity.this.info.participants.participants.indexOf(paramAnonymousAdapterView);
                if (i != -1) {
                  SetAdminsActivity.this.info.participants.participants.set(i, paramAnonymousView);
                }
                if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) {
                  SetAdminsActivity.SearchAdapter.access$1400(SetAdminsActivity.this.searchAdapter).set(paramAnonymousInt, paramAnonymousView);
                }
                if (((paramAnonymousView instanceof TLRPC.TL_chatParticipant)) && ((SetAdminsActivity.this.chat == null) || (SetAdminsActivity.this.chat.admins_enabled))) {
                  break label487;
                }
                bool = true;
                label342:
                localUserCell.setChecked(bool, true);
                if ((SetAdminsActivity.this.chat != null) && (SetAdminsActivity.this.chat.admins_enabled))
                {
                  paramAnonymousAdapterView = MessagesController.getInstance();
                  paramAnonymousInt = SetAdminsActivity.this.chat_id;
                  i = paramAnonymousView.user_id;
                  if ((paramAnonymousView instanceof TLRPC.TL_chatParticipant)) {
                    break label493;
                  }
                  bool = true;
                  label401:
                  paramAnonymousAdapterView.toggleUserAdmin(paramAnonymousInt, i, bool);
                }
              }
            }
          }
        }
        label452:
        label487:
        label493:
        do
        {
          do
          {
            return;
            i += 1;
            break;
            paramAnonymousAdapterView = SetAdminsActivity.this.participants;
            j = paramAnonymousInt - SetAdminsActivity.this.usersStartRow;
            paramAnonymousAdapterView = (TLRPC.ChatParticipant)paramAnonymousAdapterView.get(j);
            break label162;
            paramAnonymousView = new TLRPC.TL_chatParticipant();
            paramAnonymousView.user_id = paramAnonymousAdapterView.user_id;
            paramAnonymousView.date = paramAnonymousAdapterView.date;
            paramAnonymousView.inviter_id = paramAnonymousAdapterView.inviter_id;
            break label214;
            bool = false;
            break label342;
            bool = false;
            break label401;
          } while (paramAnonymousInt != SetAdminsActivity.this.allAdminsRow);
          SetAdminsActivity.access$1002(SetAdminsActivity.this, MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
        } while (SetAdminsActivity.this.chat == null);
        paramAnonymousAdapterView = SetAdminsActivity.this.chat;
        if (!SetAdminsActivity.this.chat.admins_enabled)
        {
          bool = true;
          paramAnonymousAdapterView.admins_enabled = bool;
          paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
          if (SetAdminsActivity.this.chat.admins_enabled) {
            break label631;
          }
        }
        label631:
        for (boolean bool = true;; bool = false)
        {
          paramAnonymousAdapterView.setChecked(bool);
          MessagesController.getInstance().toggleAdminMode(SetAdminsActivity.this.chat_id, SetAdminsActivity.this.chat.admins_enabled);
          return;
          bool = false;
          break;
        }
      }
    });
    this.searchEmptyView = new EmptyTextProgressView(paramContext);
    this.searchEmptyView.setVisibility(8);
    this.searchEmptyView.setShowAtCenter(true);
    this.searchEmptyView.setText(LocaleController.getString("NoResult", 2131165949));
    localFrameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.searchEmptyView.showTextView();
    updateRowsIds();
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.chatInfoDidLoaded)
    {
      TLRPC.ChatFull localChatFull = (TLRPC.ChatFull)paramVarArgs[0];
      if (localChatFull.id == this.chat_id)
      {
        this.info = localChatFull;
        updateChatParticipants();
        updateRowsIds();
      }
    }
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      int i = ((Integer)paramVarArgs[0]).intValue();
      if ((((i & 0x2) != 0) || ((i & 0x1) != 0) || ((i & 0x4) != 0)) && (this.listView != null))
      {
        int j = this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < j)
        {
          paramVarArgs = this.listView.getChildAt(paramInt);
          if ((paramVarArgs instanceof UserCell)) {
            ((UserCell)paramVarArgs).update(i);
          }
          paramInt += 1;
        }
      }
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  public void setChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
    updateChatParticipants();
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
      return SetAdminsActivity.this.rowCount;
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
      if (paramInt == SetAdminsActivity.this.allAdminsRow) {
        return 0;
      }
      if ((paramInt == SetAdminsActivity.this.allAdminsInfoRow) || (paramInt == SetAdminsActivity.this.usersEndRow)) {
        return 1;
      }
      return 2;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool1 = true;
      boolean bool2 = false;
      int i = getItemViewType(paramInt);
      if (i == 0)
      {
        paramViewGroup = paramView;
        if (paramView == null)
        {
          paramViewGroup = new TextCheckCell(this.mContext);
          paramViewGroup.setBackgroundColor(-1);
        }
        paramView = (TextCheckCell)paramViewGroup;
        SetAdminsActivity.access$1002(SetAdminsActivity.this, MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
        localObject = LocaleController.getString("SetAdminsAll", 2131166263);
        if ((SetAdminsActivity.this.chat != null) && (!SetAdminsActivity.this.chat.admins_enabled)) {
          paramView.setTextAndCheck((String)localObject, bool1, false);
        }
      }
      label267:
      do
      {
        do
        {
          return paramViewGroup;
          bool1 = false;
          break;
          if (i != 1) {
            break label267;
          }
          localObject = paramView;
          if (paramView == null) {
            localObject = new TextInfoPrivacyCell(this.mContext);
          }
          if (paramInt == SetAdminsActivity.this.allAdminsInfoRow)
          {
            if (SetAdminsActivity.this.chat.admins_enabled) {
              ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("SetAdminsNotAllInfo", 2131166265));
            }
            while (SetAdminsActivity.this.usersStartRow != -1)
            {
              ((View)localObject).setBackgroundResource(2130837688);
              return (View)localObject;
              ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("SetAdminsAllInfo", 2131166264));
            }
            ((View)localObject).setBackgroundResource(2130837689);
            return (View)localObject;
          }
          paramViewGroup = (ViewGroup)localObject;
        } while (paramInt != SetAdminsActivity.this.usersEndRow);
        ((TextInfoPrivacyCell)localObject).setText("");
        ((View)localObject).setBackgroundResource(2130837689);
        return (View)localObject;
        paramViewGroup = paramView;
      } while (i != 2);
      paramViewGroup = paramView;
      if (paramView == null)
      {
        paramViewGroup = new UserCell(this.mContext, 1, 2, false);
        paramViewGroup.setBackgroundColor(-1);
      }
      paramView = (UserCell)paramViewGroup;
      Object localObject = (TLRPC.ChatParticipant)SetAdminsActivity.this.participants.get(paramInt - SetAdminsActivity.this.usersStartRow);
      paramView.setData(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id)), null, null, 0);
      SetAdminsActivity.access$1002(SetAdminsActivity.this, MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
      if ((!(localObject instanceof TLRPC.TL_chatParticipant)) || ((SetAdminsActivity.this.chat != null) && (!SetAdminsActivity.this.chat.admins_enabled))) {}
      for (bool1 = true;; bool1 = false)
      {
        paramView.setChecked(bool1, false);
        if ((SetAdminsActivity.this.chat != null) && (SetAdminsActivity.this.chat.admins_enabled))
        {
          bool1 = bool2;
          if (((TLRPC.ChatParticipant)localObject).user_id != UserConfig.getClientUserId()) {}
        }
        else
        {
          bool1 = true;
        }
        paramView.setCheckDisabled(bool1);
        return paramViewGroup;
      }
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
      return false;
    }
    
    public boolean isEnabled(int paramInt)
    {
      if (paramInt == SetAdminsActivity.this.allAdminsRow) {}
      while ((paramInt >= SetAdminsActivity.this.usersStartRow) && (paramInt < SetAdminsActivity.this.usersEndRow) && (!((TLRPC.ChatParticipant)SetAdminsActivity.this.participants.get(paramInt - SetAdminsActivity.this.usersStartRow) instanceof TLRPC.TL_chatParticipantCreator))) {
        return true;
      }
      return false;
    }
  }
  
  public class SearchAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    private ArrayList<TLRPC.ChatParticipant> searchResult = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Timer searchTimer;
    
    public SearchAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    private void processSearch(final String paramString)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          final ArrayList localArrayList = new ArrayList();
          localArrayList.addAll(SetAdminsActivity.this.participants);
          Utilities.searchQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              Object localObject = SetAdminsActivity.SearchAdapter.2.this.val$query.trim().toLowerCase();
              if (((String)localObject).length() == 0)
              {
                SetAdminsActivity.SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                return;
              }
              String str2 = LocaleController.getInstance().getTranslitString((String)localObject);
              String str1;
              if (!((String)localObject).equals(str2))
              {
                str1 = str2;
                if (str2.length() != 0) {}
              }
              else
              {
                str1 = null;
              }
              int i;
              String[] arrayOfString;
              ArrayList localArrayList;
              int j;
              label135:
              TLRPC.ChatParticipant localChatParticipant;
              TLRPC.User localUser;
              if (str1 != null)
              {
                i = 1;
                arrayOfString = new String[i + 1];
                arrayOfString[0] = localObject;
                if (str1 != null) {
                  arrayOfString[1] = str1;
                }
                localObject = new ArrayList();
                localArrayList = new ArrayList();
                j = 0;
                if (j >= localArrayList.size()) {
                  break label487;
                }
                localChatParticipant = (TLRPC.ChatParticipant)localArrayList.get(j);
                localUser = MessagesController.getInstance().getUser(Integer.valueOf(localChatParticipant.user_id));
                if (localUser.id != UserConfig.getClientUserId()) {
                  break label198;
                }
              }
              label198:
              label348:
              label421:
              label477:
              label485:
              for (;;)
              {
                j += 1;
                break label135;
                i = 0;
                break;
                String str3 = ContactsController.formatName(localUser.first_name, localUser.last_name).toLowerCase();
                str2 = LocaleController.getInstance().getTranslitString(str3);
                str1 = str2;
                if (str3.equals(str2)) {
                  str1 = null;
                }
                int m = 0;
                int n = arrayOfString.length;
                int k = 0;
                for (;;)
                {
                  if (k >= n) {
                    break label485;
                  }
                  str2 = arrayOfString[k];
                  if ((str3.startsWith(str2)) || (str3.contains(" " + str2)) || ((str1 != null) && ((str1.startsWith(str2)) || (str1.contains(" " + str2)))))
                  {
                    i = 1;
                    if (i == 0) {
                      break label477;
                    }
                    if (i != 1) {
                      break label421;
                    }
                    localArrayList.add(AndroidUtilities.generateSearchName(localUser.first_name, localUser.last_name, str2));
                  }
                  for (;;)
                  {
                    ((ArrayList)localObject).add(localChatParticipant);
                    break;
                    i = m;
                    if (localUser.username == null) {
                      break label348;
                    }
                    i = m;
                    if (!localUser.username.startsWith(str2)) {
                      break label348;
                    }
                    i = 2;
                    break label348;
                    localArrayList.add(AndroidUtilities.generateSearchName("@" + localUser.username, null, "@" + str2));
                  }
                  k += 1;
                  m = i;
                }
              }
              label487:
              SetAdminsActivity.SearchAdapter.this.updateSearchResults((ArrayList)localObject, localArrayList);
            }
          });
        }
      });
    }
    
    private void updateSearchResults(final ArrayList<TLRPC.ChatParticipant> paramArrayList, final ArrayList<CharSequence> paramArrayList1)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          SetAdminsActivity.SearchAdapter.access$1402(SetAdminsActivity.SearchAdapter.this, paramArrayList);
          SetAdminsActivity.SearchAdapter.access$2202(SetAdminsActivity.SearchAdapter.this, paramArrayList1);
          SetAdminsActivity.SearchAdapter.this.notifyDataSetChanged();
        }
      });
    }
    
    public boolean areAllItemsEnabled()
    {
      return true;
    }
    
    public int getCount()
    {
      return this.searchResult.size();
    }
    
    public TLRPC.ChatParticipant getItem(int paramInt)
    {
      return (TLRPC.ChatParticipant)this.searchResult.get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool2 = false;
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new UserCell(this.mContext, 1, 2, false);
      }
      TLRPC.ChatParticipant localChatParticipant = getItem(paramInt);
      TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(localChatParticipant.user_id));
      String str = localUser.username;
      Object localObject3 = null;
      paramView = null;
      Object localObject1 = localObject3;
      if (paramInt < this.searchResult.size())
      {
        localObject2 = (CharSequence)this.searchResultNames.get(paramInt);
        paramView = (View)localObject2;
        localObject1 = localObject3;
        if (localObject2 != null)
        {
          paramView = (View)localObject2;
          localObject1 = localObject3;
          if (str != null)
          {
            paramView = (View)localObject2;
            localObject1 = localObject3;
            if (str.length() > 0)
            {
              paramView = (View)localObject2;
              localObject1 = localObject3;
              if (((CharSequence)localObject2).toString().startsWith("@" + str))
              {
                localObject1 = localObject2;
                paramView = null;
              }
            }
          }
        }
      }
      Object localObject2 = (UserCell)paramViewGroup;
      ((UserCell)localObject2).setData(localUser, paramView, (CharSequence)localObject1, 0);
      SetAdminsActivity.access$1002(SetAdminsActivity.this, MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
      if ((!(localChatParticipant instanceof TLRPC.TL_chatParticipant)) || ((SetAdminsActivity.this.chat != null) && (!SetAdminsActivity.this.chat.admins_enabled))) {}
      for (boolean bool1 = true;; bool1 = false)
      {
        ((UserCell)localObject2).setChecked(bool1, false);
        if ((SetAdminsActivity.this.chat != null) && (SetAdminsActivity.this.chat.admins_enabled))
        {
          bool1 = bool2;
          if (localChatParticipant.user_id != UserConfig.getClientUserId()) {}
        }
        else
        {
          bool1 = true;
        }
        ((UserCell)localObject2).setCheckDisabled(bool1);
        return paramViewGroup;
      }
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
    
    public boolean isEmpty()
    {
      return this.searchResult.isEmpty();
    }
    
    public boolean isEnabled(int paramInt)
    {
      return true;
    }
    
    public void search(final String paramString)
    {
      try
      {
        if (this.searchTimer != null) {
          this.searchTimer.cancel();
        }
        if (paramString == null)
        {
          this.searchResult.clear();
          this.searchResultNames.clear();
          notifyDataSetChanged();
          return;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask()
        {
          public void run()
          {
            try
            {
              SetAdminsActivity.SearchAdapter.this.searchTimer.cancel();
              SetAdminsActivity.SearchAdapter.access$1902(SetAdminsActivity.SearchAdapter.this, null);
              SetAdminsActivity.SearchAdapter.this.processSearch(paramString);
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
        }, 200L, 300L);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/SetAdminsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */