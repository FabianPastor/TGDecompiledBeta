package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
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
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ChatUsersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int search_button = 0;
  private int chatId = this.arguments.getInt("chat_id");
  private TLRPC.Chat currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
  private EmptyTextProgressView emptyView;
  private boolean firstLoaded;
  private TLRPC.ChatFull info;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private boolean loadingUsers;
  private ArrayList<TLRPC.ChatParticipant> participants = new ArrayList();
  private int participantsEndRow;
  private int participantsInfoRow;
  private int participantsStartRow;
  private int rowCount;
  private ActionBarMenuItem searchItem;
  private SearchAdapter searchListViewAdapter;
  private boolean searchWas;
  private boolean searching;
  
  public ChatUsersActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private boolean createMenuForParticipant(final TLRPC.ChatParticipant paramChatParticipant, boolean paramBoolean)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramChatParticipant == null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      int i = UserConfig.getInstance(this.currentAccount).getClientUserId();
      bool2 = bool1;
      if (paramChatParticipant.user_id != i)
      {
        int j = 0;
        int k;
        if (this.currentChat.creator) {
          k = 1;
        }
        for (;;)
        {
          bool2 = bool1;
          if (k == 0) {
            break;
          }
          if (!paramBoolean) {
            break label120;
          }
          bool2 = true;
          break;
          k = j;
          if ((paramChatParticipant instanceof TLRPC.TL_chatParticipant)) {
            if ((!this.currentChat.admin) || (!this.currentChat.admins_enabled))
            {
              k = j;
              if (paramChatParticipant.inviter_id != i) {}
            }
            else
            {
              k = 1;
            }
          }
        }
        label120:
        ArrayList localArrayList1 = new ArrayList();
        final ArrayList localArrayList2 = new ArrayList();
        localArrayList1.add(LocaleController.getString("KickFromGroup", NUM));
        localArrayList2.add(Integer.valueOf(0));
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
        localBuilder.setItems((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList2.size()]), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            if (((Integer)localArrayList2.get(paramAnonymousInt)).intValue() == 0) {
              MessagesController.getInstance(ChatUsersActivity.this.currentAccount).deleteUserFromChat(ChatUsersActivity.this.chatId, MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(paramChatParticipant.user_id)), ChatUsersActivity.this.info);
            }
          }
        });
        showDialog(localBuilder.create());
        bool2 = true;
      }
    }
  }
  
  private void fetchUsers()
  {
    if (this.info == null) {
      this.loadingUsers = true;
    }
    for (;;)
    {
      return;
      this.loadingUsers = false;
      this.participants = new ArrayList(this.info.participants.participants);
      if (this.listViewAdapter != null) {
        this.listViewAdapter.notifyDataSetChanged();
      }
    }
  }
  
  private void updateRows()
  {
    this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
    if (this.currentChat == null) {}
    label129:
    for (;;)
    {
      return;
      this.participantsStartRow = -1;
      this.participantsEndRow = -1;
      this.participantsInfoRow = -1;
      this.rowCount = 0;
      if (!this.participants.isEmpty())
      {
        this.participantsStartRow = this.rowCount;
        this.rowCount += this.participants.size();
      }
      for (this.participantsEndRow = this.rowCount;; this.participantsEndRow = -1)
      {
        if (this.rowCount == 0) {
          break label129;
        }
        int i = this.rowCount;
        this.rowCount = (i + 1);
        this.participantsInfoRow = i;
        break;
        this.participantsStartRow = -1;
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    int i = 1;
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("GroupMembers", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ChatUsersActivity.this.finishFragment();
        }
      }
    });
    this.searchListViewAdapter = new SearchAdapter(paramContext);
    this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public void onSearchCollapse()
      {
        ChatUsersActivity.this.searchListViewAdapter.searchDialogs(null);
        ChatUsersActivity.access$002(ChatUsersActivity.this, false);
        ChatUsersActivity.access$302(ChatUsersActivity.this, false);
        ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
        ChatUsersActivity.this.listViewAdapter.notifyDataSetChanged();
        ChatUsersActivity.this.listView.setFastScrollVisible(true);
        ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(false);
        ChatUsersActivity.this.emptyView.setShowAtCenter(false);
      }
      
      public void onSearchExpand()
      {
        ChatUsersActivity.access$002(ChatUsersActivity.this, true);
        ChatUsersActivity.this.emptyView.setShowAtCenter(true);
      }
      
      public void onTextChanged(EditText paramAnonymousEditText)
      {
        if (ChatUsersActivity.this.searchListViewAdapter == null) {}
        for (;;)
        {
          return;
          paramAnonymousEditText = paramAnonymousEditText.getText().toString();
          if (paramAnonymousEditText.length() != 0)
          {
            ChatUsersActivity.access$302(ChatUsersActivity.this, true);
            if (ChatUsersActivity.this.listView != null)
            {
              ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.searchListViewAdapter);
              ChatUsersActivity.this.searchListViewAdapter.notifyDataSetChanged();
              ChatUsersActivity.this.listView.setFastScrollVisible(false);
              ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
            }
          }
          ChatUsersActivity.this.searchListViewAdapter.searchDialogs(paramAnonymousEditText);
        }
      }
    });
    this.searchItem.getSearchField().setHint(LocaleController.getString("Search", NUM));
    this.fragmentView = new FrameLayout(paramContext);
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setText(LocaleController.getString("NoResult", NUM));
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setEmptyView(this.emptyView);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    RecyclerListView localRecyclerListView = this.listView;
    paramContext = new ListAdapter(paramContext);
    this.listViewAdapter = paramContext;
    localRecyclerListView.setAdapter(paramContext);
    paramContext = this.listView;
    if (LocaleController.isRTL)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          int i = 0;
          if (ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.listViewAdapter)
          {
            paramAnonymousView = ChatUsersActivity.this.listViewAdapter.getItem(paramAnonymousInt);
            paramAnonymousInt = i;
            if (paramAnonymousView != null) {
              paramAnonymousInt = paramAnonymousView.user_id;
            }
            if (paramAnonymousInt != 0)
            {
              paramAnonymousView = new Bundle();
              paramAnonymousView.putInt("user_id", paramAnonymousInt);
              ChatUsersActivity.this.presentFragment(new ProfileActivity(paramAnonymousView));
            }
            return;
          }
          paramAnonymousView = ChatUsersActivity.this.searchListViewAdapter.getItem(paramAnonymousInt);
          if ((paramAnonymousView instanceof TLRPC.ChatParticipant)) {}
          for (paramAnonymousView = (TLRPC.ChatParticipant)paramAnonymousView;; paramAnonymousView = null)
          {
            paramAnonymousInt = i;
            if (paramAnonymousView == null) {
              break;
            }
            paramAnonymousInt = paramAnonymousView.user_id;
            break;
          }
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          boolean bool1 = false;
          boolean bool2 = bool1;
          if (ChatUsersActivity.this.getParentActivity() != null)
          {
            bool2 = bool1;
            if (ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.listViewAdapter)
            {
              bool2 = bool1;
              if (ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(paramAnonymousInt), false)) {
                bool2 = true;
              }
            }
          }
          return bool2;
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == 1) && (ChatUsersActivity.this.searching) && (ChatUsersActivity.this.searchWas)) {
            AndroidUtilities.hideKeyboard(ChatUsersActivity.this.getParentActivity().getCurrentFocus());
          }
        }
        
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          super.onScrolled(paramAnonymousRecyclerView, paramAnonymousInt1, paramAnonymousInt2);
        }
      });
      if (!this.loadingUsers) {
        break label373;
      }
      this.emptyView.showProgress();
    }
    for (;;)
    {
      updateRows();
      return this.fragmentView;
      i = 2;
      break;
      label373:
      this.emptyView.showTextView();
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.chatInfoDidLoaded)
    {
      TLRPC.ChatFull localChatFull = (TLRPC.ChatFull)paramVarArgs[0];
      boolean bool = ((Boolean)paramVarArgs[2]).booleanValue();
      if ((localChatFull.id == this.chatId) && (!bool))
      {
        this.info = localChatFull;
        fetchUsers();
        updateRows();
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local7 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        if (ChatUsersActivity.this.listView != null)
        {
          int i = ChatUsersActivity.this.listView.getChildCount();
          for (int j = 0; j < i; j++)
          {
            View localView = ChatUsersActivity.this.listView.getChildAt(j);
            if ((localView instanceof ManageChatUserCell)) {
              ((ManageChatUserCell)localView).update(0);
            }
          }
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { ManageChatUserCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    localObject1 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4");
    localObject2 = new ThemeDescription(this.listView, 0, new Class[] { ManageChatUserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, 0, new Class[] { ManageChatUserCell.class }, new String[] { "statusColor" }, null, null, local7, "windowBackgroundWhiteGrayText");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, 0, new Class[] { ManageChatUserCell.class }, new String[] { "statusOnlineColor" }, null, null, local7, "windowBackgroundWhiteBlueText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    Drawable localDrawable3 = Theme.avatar_savedDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localObject1, localThemeDescription9, localThemeDescription10, localObject2, localThemeDescription11, localThemeDescription12, new ThemeDescription(localRecyclerListView, 0, new Class[] { ManageChatUserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2, localDrawable3 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundPink") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
    fetchUsers();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
  
  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (!paramBoolean2)) {
      this.searchItem.openSearch(true);
    }
  }
  
  public void setInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
  }
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public TLRPC.ChatParticipant getItem(int paramInt)
    {
      if ((ChatUsersActivity.this.participantsStartRow != -1) && (paramInt >= ChatUsersActivity.this.participantsStartRow) && (paramInt < ChatUsersActivity.this.participantsEndRow)) {}
      for (TLRPC.ChatParticipant localChatParticipant = (TLRPC.ChatParticipant)ChatUsersActivity.this.participants.get(paramInt - ChatUsersActivity.this.participantsStartRow);; localChatParticipant = null) {
        return localChatParticipant;
      }
    }
    
    public int getItemCount()
    {
      if (ChatUsersActivity.this.loadingUsers) {}
      for (int i = 0;; i = ChatUsersActivity.this.rowCount) {
        return i;
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      if ((paramInt >= ChatUsersActivity.this.participantsStartRow) && (paramInt < ChatUsersActivity.this.participantsEndRow)) {}
      for (;;)
      {
        return i;
        if (paramInt == ChatUsersActivity.this.participantsInfoRow) {
          i = 1;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getItemViewType();
      if ((i == 0) || (i == 2) || (i == 6)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        paramViewHolder = (ManageChatUserCell)paramViewHolder.itemView;
        paramViewHolder.setTag(Integer.valueOf(paramInt));
        Object localObject = getItem(paramInt);
        localObject = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id));
        if (localObject != null)
        {
          paramViewHolder.setData((TLRPC.User)localObject, null, null);
          continue;
          paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
          if (paramInt == ChatUsersActivity.this.participantsInfoRow) {
            paramViewHolder.setText("");
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ManageChatUserCell(this.mContext, 1, true);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ((ManageChatUserCell)paramViewGroup).setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate()
        {
          public boolean onOptionsButtonCheck(ManageChatUserCell paramAnonymousManageChatUserCell, boolean paramAnonymousBoolean)
          {
            paramAnonymousManageChatUserCell = ChatUsersActivity.this.listViewAdapter.getItem(((Integer)paramAnonymousManageChatUserCell.getTag()).intValue());
            ChatUsersActivity localChatUsersActivity = ChatUsersActivity.this;
            if (!paramAnonymousBoolean) {}
            for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false) {
              return localChatUsersActivity.createMenuForParticipant(paramAnonymousManageChatUserCell, paramAnonymousBoolean);
            }
          }
        });
      }
    }
    
    public void onViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.itemView instanceof ManageChatUserCell)) {
        ((ManageChatUserCell)paramViewHolder.itemView).recycle();
      }
    }
  }
  
  private class SearchAdapter
    extends RecyclerListView.SelectionAdapter
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
          localArrayList.addAll(ChatUsersActivity.this.participants);
          Utilities.searchQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              Object localObject = ChatUsersActivity.SearchAdapter.2.this.val$query.trim().toLowerCase();
              if (((String)localObject).length() == 0) {
                ChatUsersActivity.SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
              }
              for (;;)
              {
                return;
                String str1 = LocaleController.getInstance().getTranslitString((String)localObject);
                String str2;
                if (!((String)localObject).equals(str1))
                {
                  str2 = str1;
                  if (str1.length() != 0) {}
                }
                else
                {
                  str2 = null;
                }
                int i;
                String[] arrayOfString;
                ArrayList localArrayList;
                int j;
                label123:
                TLRPC.ChatParticipant localChatParticipant;
                TLRPC.User localUser;
                String str3;
                int k;
                int m;
                int n;
                if (str2 != null)
                {
                  i = 1;
                  arrayOfString = new String[i + 1];
                  arrayOfString[0] = localObject;
                  if (str2 != null) {
                    arrayOfString[1] = str2;
                  }
                  localArrayList = new ArrayList();
                  localObject = new ArrayList();
                  j = 0;
                  if (j < localArrayList.size())
                  {
                    localChatParticipant = (TLRPC.ChatParticipant)localArrayList.get(j);
                    localUser = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(localChatParticipant.user_id));
                    str3 = ContactsController.formatName(localUser.first_name, localUser.last_name).toLowerCase();
                    str1 = LocaleController.getInstance().getTranslitString(str3);
                    str2 = str1;
                    if (str3.equals(str1)) {
                      str2 = null;
                    }
                    k = 0;
                    m = arrayOfString.length;
                    n = 0;
                  }
                }
                else
                {
                  for (;;)
                  {
                    if (n < m)
                    {
                      str1 = arrayOfString[n];
                      if ((!str3.startsWith(str1)) && (!str3.contains(" " + str1)) && ((str2 == null) || ((!str2.startsWith(str1)) && (!str2.contains(" " + str1))))) {
                        break label369;
                      }
                      i = 1;
                      label319:
                      if (i == 0) {
                        break label457;
                      }
                      if (i != 1) {
                        break label403;
                      }
                      ((ArrayList)localObject).add(AndroidUtilities.generateSearchName(localUser.first_name, localUser.last_name, str1));
                    }
                    for (;;)
                    {
                      localArrayList.add(localChatParticipant);
                      j++;
                      break label123;
                      i = 0;
                      break;
                      label369:
                      i = k;
                      if (localUser.username == null) {
                        break label319;
                      }
                      i = k;
                      if (!localUser.username.startsWith(str1)) {
                        break label319;
                      }
                      i = 2;
                      break label319;
                      label403:
                      ((ArrayList)localObject).add(AndroidUtilities.generateSearchName("@" + localUser.username, null, "@" + str1));
                    }
                    label457:
                    n++;
                    k = i;
                  }
                }
                ChatUsersActivity.SearchAdapter.this.updateSearchResults(localArrayList, (ArrayList)localObject);
              }
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
          ChatUsersActivity.SearchAdapter.access$1602(ChatUsersActivity.SearchAdapter.this, paramArrayList);
          ChatUsersActivity.SearchAdapter.access$1702(ChatUsersActivity.SearchAdapter.this, paramArrayList1);
          ChatUsersActivity.SearchAdapter.this.notifyDataSetChanged();
        }
      });
    }
    
    public TLObject getItem(int paramInt)
    {
      return (TLObject)this.searchResult.get(paramInt);
    }
    
    public int getItemCount()
    {
      return this.searchResult.size();
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      Object localObject1 = getItem(paramInt);
      if ((localObject1 instanceof TLRPC.User)) {}
      for (localObject1 = (TLRPC.User)localObject1;; localObject1 = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject1).user_id)))
      {
        String str = ((TLRPC.User)localObject1).username;
        CharSequence localCharSequence1 = (CharSequence)this.searchResultNames.get(paramInt);
        Object localObject2 = null;
        CharSequence localCharSequence2 = localCharSequence1;
        Object localObject3 = localObject2;
        if (localCharSequence1 != null)
        {
          localCharSequence2 = localCharSequence1;
          localObject3 = localObject2;
          if (str != null)
          {
            localCharSequence2 = localCharSequence1;
            localObject3 = localObject2;
            if (str.length() > 0)
            {
              localCharSequence2 = localCharSequence1;
              localObject3 = localObject2;
              if (localCharSequence1.toString().startsWith("@" + str))
              {
                localObject3 = localCharSequence1;
                localCharSequence2 = null;
              }
            }
          }
        }
        paramViewHolder = (ManageChatUserCell)paramViewHolder.itemView;
        paramViewHolder.setTag(Integer.valueOf(paramInt));
        paramViewHolder.setData((TLRPC.User)localObject1, localCharSequence2, (CharSequence)localObject3);
        return;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new ManageChatUserCell(this.mContext, 2, true);
      paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      ((ManageChatUserCell)paramViewGroup).setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate()
      {
        public boolean onOptionsButtonCheck(ManageChatUserCell paramAnonymousManageChatUserCell, boolean paramAnonymousBoolean)
        {
          boolean bool = false;
          ChatUsersActivity localChatUsersActivity;
          if ((ChatUsersActivity.SearchAdapter.this.getItem(((Integer)paramAnonymousManageChatUserCell.getTag()).intValue()) instanceof TLRPC.ChatParticipant))
          {
            paramAnonymousManageChatUserCell = (TLRPC.ChatParticipant)ChatUsersActivity.SearchAdapter.this.getItem(((Integer)paramAnonymousManageChatUserCell.getTag()).intValue());
            localChatUsersActivity = ChatUsersActivity.this;
            if (paramAnonymousBoolean) {
              break label71;
            }
          }
          label71:
          for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false)
          {
            bool = localChatUsersActivity.createMenuForParticipant(paramAnonymousManageChatUserCell, paramAnonymousBoolean);
            return bool;
          }
        }
      });
      return new RecyclerListView.Holder(paramViewGroup);
    }
    
    public void onViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.itemView instanceof ManageChatUserCell)) {
        ((ManageChatUserCell)paramViewHolder.itemView).recycle();
      }
    }
    
    public void searchDialogs(final String paramString)
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
          FileLog.e(localException);
          continue;
          this.searchTimer = new Timer();
          this.searchTimer.schedule(new TimerTask()
          {
            public void run()
            {
              try
              {
                ChatUsersActivity.SearchAdapter.this.searchTimer.cancel();
                ChatUsersActivity.SearchAdapter.access$1102(ChatUsersActivity.SearchAdapter.this, null);
                ChatUsersActivity.SearchAdapter.this.processSearch(paramString);
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
          }, 200L, 300L);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChatUsersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */