package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class SetAdminsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int allAdminsInfoRow;
  private int allAdminsRow;
  private TLRPC.Chat chat;
  private int chat_id;
  private EmptyTextProgressView emptyView;
  private TLRPC.ChatFull info;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private ArrayList<TLRPC.ChatParticipant> participants = new ArrayList();
  private int rowCount;
  private SearchAdapter searchAdapter;
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
    int i;
    if ((paramChatParticipant instanceof TLRPC.TL_chatParticipantCreator)) {
      i = 0;
    }
    for (;;)
    {
      return i;
      if ((paramChatParticipant instanceof TLRPC.TL_chatParticipantAdmin)) {
        i = 1;
      } else {
        i = 2;
      }
    }
  }
  
  private void updateChatParticipants()
  {
    if (this.info == null) {}
    for (;;)
    {
      return;
      if (this.participants.size() != this.info.participants.participants.size())
      {
        this.participants.clear();
        this.participants.addAll(this.info.participants.participants);
        try
        {
          ArrayList localArrayList = this.participants;
          Comparator local4 = new org/telegram/ui/SetAdminsActivity$4;
          local4.<init>(this);
          Collections.sort(localArrayList, local4);
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
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
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("SetAdminsTitle", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          SetAdminsActivity.this.finishFragment();
        }
      }
    });
    this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public void onSearchCollapse()
      {
        SetAdminsActivity.access$002(SetAdminsActivity.this, false);
        SetAdminsActivity.access$302(SetAdminsActivity.this, false);
        if (SetAdminsActivity.this.listView != null)
        {
          SetAdminsActivity.this.listView.setEmptyView(null);
          SetAdminsActivity.this.emptyView.setVisibility(8);
          if (SetAdminsActivity.this.listView.getAdapter() != SetAdminsActivity.this.listAdapter) {
            SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.listAdapter);
          }
        }
        if (SetAdminsActivity.this.searchAdapter != null) {
          SetAdminsActivity.this.searchAdapter.search(null);
        }
      }
      
      public void onSearchExpand()
      {
        SetAdminsActivity.access$002(SetAdminsActivity.this, true);
        SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.emptyView);
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
            SetAdminsActivity.this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
          }
          if ((SetAdminsActivity.this.emptyView != null) && (SetAdminsActivity.this.listView.getEmptyView() != SetAdminsActivity.this.emptyView))
          {
            SetAdminsActivity.this.emptyView.showTextView();
            SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.emptyView);
          }
        }
        if (SetAdminsActivity.this.searchAdapter != null) {
          SetAdminsActivity.this.searchAdapter.search(paramAnonymousEditText);
        }
      }
    });
    this.searchItem.getSearchField().setHint(LocaleController.getString("Search", NUM));
    this.listAdapter = new ListAdapter(paramContext);
    this.searchAdapter = new SearchAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setVerticalScrollBarEnabled(false);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        boolean bool1 = true;
        boolean bool2 = true;
        int j;
        int k;
        if ((SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) || ((paramAnonymousInt >= SetAdminsActivity.this.usersStartRow) && (paramAnonymousInt < SetAdminsActivity.this.usersEndRow)))
        {
          UserCell localUserCell = (UserCell)paramAnonymousView;
          SetAdminsActivity.access$902(SetAdminsActivity.this, MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
          int i = -1;
          if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter)
          {
            localObject = SetAdminsActivity.this.searchAdapter.getItem(paramAnonymousInt);
            j = 0;
            k = i;
            paramAnonymousView = (View)localObject;
            if (j < SetAdminsActivity.this.participants.size())
            {
              if (((TLRPC.ChatParticipant)SetAdminsActivity.this.participants.get(j)).user_id == ((TLRPC.ChatParticipant)localObject).user_id)
              {
                paramAnonymousView = (View)localObject;
                k = j;
              }
            }
            else {
              label178:
              if ((k != -1) && (!(paramAnonymousView instanceof TLRPC.TL_chatParticipantCreator)))
              {
                if (!(paramAnonymousView instanceof TLRPC.TL_chatParticipant)) {
                  break label483;
                }
                localObject = new TLRPC.TL_chatParticipantAdmin();
                ((TLRPC.ChatParticipant)localObject).user_id = paramAnonymousView.user_id;
                ((TLRPC.ChatParticipant)localObject).date = paramAnonymousView.date;
                ((TLRPC.ChatParticipant)localObject).inviter_id = paramAnonymousView.inviter_id;
                label234:
                SetAdminsActivity.this.participants.set(k, localObject);
                j = SetAdminsActivity.this.info.participants.participants.indexOf(paramAnonymousView);
                if (j != -1) {
                  SetAdminsActivity.this.info.participants.participants.set(j, localObject);
                }
                if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) {
                  SetAdminsActivity.SearchAdapter.access$1400(SetAdminsActivity.this.searchAdapter).set(paramAnonymousInt, localObject);
                }
                if (((localObject instanceof TLRPC.TL_chatParticipant)) && ((SetAdminsActivity.this.chat == null) || (SetAdminsActivity.this.chat.admins_enabled))) {
                  break label522;
                }
                bool3 = true;
                label366:
                localUserCell.setChecked(bool3, true);
                if ((SetAdminsActivity.this.chat != null) && (SetAdminsActivity.this.chat.admins_enabled))
                {
                  paramAnonymousView = MessagesController.getInstance(SetAdminsActivity.this.currentAccount);
                  j = SetAdminsActivity.this.chat_id;
                  paramAnonymousInt = ((TLRPC.ChatParticipant)localObject).user_id;
                  if ((localObject instanceof TLRPC.TL_chatParticipant)) {
                    break label528;
                  }
                  bool3 = bool2;
                  label435:
                  paramAnonymousView.toggleUserAdmin(j, paramAnonymousInt, bool3);
                }
              }
            }
          }
        }
        label483:
        label522:
        label528:
        do
        {
          do
          {
            return;
            j++;
            break;
            paramAnonymousView = SetAdminsActivity.this.participants;
            k = paramAnonymousInt - SetAdminsActivity.this.usersStartRow;
            paramAnonymousView = (TLRPC.ChatParticipant)paramAnonymousView.get(k);
            break label178;
            localObject = new TLRPC.TL_chatParticipant();
            ((TLRPC.ChatParticipant)localObject).user_id = paramAnonymousView.user_id;
            ((TLRPC.ChatParticipant)localObject).date = paramAnonymousView.date;
            ((TLRPC.ChatParticipant)localObject).inviter_id = paramAnonymousView.inviter_id;
            break label234;
            bool3 = false;
            break label366;
            bool3 = false;
            break label435;
          } while (paramAnonymousInt != SetAdminsActivity.this.allAdminsRow);
          SetAdminsActivity.access$902(SetAdminsActivity.this, MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
        } while (SetAdminsActivity.this.chat == null);
        Object localObject = SetAdminsActivity.this.chat;
        if (!SetAdminsActivity.this.chat.admins_enabled)
        {
          bool3 = true;
          label611:
          ((TLRPC.Chat)localObject).admins_enabled = bool3;
          paramAnonymousView = (TextCheckCell)paramAnonymousView;
          if (SetAdminsActivity.this.chat.admins_enabled) {
            break label684;
          }
        }
        label684:
        for (boolean bool3 = bool1;; bool3 = false)
        {
          paramAnonymousView.setChecked(bool3);
          MessagesController.getInstance(SetAdminsActivity.this.currentAccount).toggleAdminMode(SetAdminsActivity.this.chat_id, SetAdminsActivity.this.chat.admins_enabled);
          break;
          bool3 = false;
          break label611;
        }
      }
    });
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setVisibility(8);
    this.emptyView.setShowAtCenter(true);
    this.emptyView.setText(LocaleController.getString("NoResult", NUM));
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.emptyView.showTextView();
    updateRowsIds();
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.chatInfoDidLoaded)
    {
      paramVarArgs = (TLRPC.ChatFull)paramVarArgs[0];
      if (paramVarArgs.id == this.chat_id)
      {
        this.info = paramVarArgs;
        updateChatParticipants();
        updateRowsIds();
      }
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.updateInterfaces)
      {
        int i = ((Integer)paramVarArgs[0]).intValue();
        if ((((i & 0x2) == 0) && ((i & 0x1) == 0) && ((i & 0x4) == 0)) || (this.listView == null)) {
          break;
        }
        paramInt2 = this.listView.getChildCount();
        for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
        {
          paramVarArgs = this.listView.getChildAt(paramInt1);
          if ((paramVarArgs instanceof UserCell)) {
            ((UserCell)paramVarArgs).update(i);
          }
        }
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local5 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        if (SetAdminsActivity.this.listView != null)
        {
          int i = SetAdminsActivity.this.listView.getChildCount();
          for (int j = 0; j < i; j++)
          {
            View localView = SetAdminsActivity.this.listView.getChildAt(j);
            if ((localView instanceof UserCell)) {
              ((UserCell)localView).update(0);
            }
          }
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextCheckCell.class, UserCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    ThemeDescription localThemeDescription11 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4");
    localObject2 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, null, null, null, "checkboxSquareUnchecked");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, null, null, null, "checkboxSquareDisabled");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, null, null, null, "checkboxSquareBackground");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, null, null, null, "checkboxSquareCheck");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription24 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, local5, "windowBackgroundWhiteGrayText");
    ThemeDescription localThemeDescription25 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusOnlineColor" }, null, null, local5, "windowBackgroundWhiteBlueText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    localObject1 = Theme.avatar_broadcastDrawable;
    Drawable localDrawable2 = Theme.avatar_savedDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localObject2, localThemeDescription20, localThemeDescription21, localThemeDescription22, localThemeDescription23, localThemeDescription24, localThemeDescription25, new ThemeDescription(localRecyclerListView, 0, new Class[] { UserCell.class }, null, new Drawable[] { localDrawable1, localObject1, localDrawable2 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundPink") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
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
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return SetAdminsActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (paramInt == SetAdminsActivity.this.allAdminsRow) {
        paramInt = 0;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt == SetAdminsActivity.this.allAdminsInfoRow) || (paramInt == SetAdminsActivity.this.usersEndRow)) {
          paramInt = 1;
        } else {
          paramInt = 2;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool = true;
      int i = paramViewHolder.getAdapterPosition();
      if (i == SetAdminsActivity.this.allAdminsRow) {}
      for (;;)
      {
        return bool;
        if ((i < SetAdminsActivity.this.usersStartRow) || (i >= SetAdminsActivity.this.usersEndRow) || (((TLRPC.ChatParticipant)SetAdminsActivity.this.participants.get(i - SetAdminsActivity.this.usersStartRow) instanceof TLRPC.TL_chatParticipantCreator))) {
          bool = false;
        }
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = true;
      boolean bool2 = false;
      switch (paramViewHolder.getItemViewType())
      {
      default: 
      case 0: 
      case 1: 
        for (;;)
        {
          return;
          paramViewHolder = (TextCheckCell)paramViewHolder.itemView;
          SetAdminsActivity.access$902(SetAdminsActivity.this, MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
          localObject = LocaleController.getString("SetAdminsAll", NUM);
          if ((SetAdminsActivity.this.chat != null) && (!SetAdminsActivity.this.chat.admins_enabled)) {}
          for (;;)
          {
            paramViewHolder.setTextAndCheck((String)localObject, bool1, false);
            break;
            bool1 = false;
          }
          paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
          if (paramInt == SetAdminsActivity.this.allAdminsInfoRow)
          {
            if (SetAdminsActivity.this.chat.admins_enabled) {
              paramViewHolder.setText(LocaleController.getString("SetAdminsNotAllInfo", NUM));
            }
            for (;;)
            {
              if (SetAdminsActivity.this.usersStartRow == -1) {
                break label210;
              }
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
              break;
              paramViewHolder.setText(LocaleController.getString("SetAdminsAllInfo", NUM));
            }
            label210:
            paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
          }
          else if (paramInt == SetAdminsActivity.this.usersEndRow)
          {
            paramViewHolder.setText("");
            paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
          }
        }
      }
      paramViewHolder = (UserCell)paramViewHolder.itemView;
      Object localObject = (TLRPC.ChatParticipant)SetAdminsActivity.this.participants.get(paramInt - SetAdminsActivity.this.usersStartRow);
      paramViewHolder.setData(MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id)), null, null, 0);
      SetAdminsActivity.access$902(SetAdminsActivity.this, MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
      if ((!(localObject instanceof TLRPC.TL_chatParticipant)) || ((SetAdminsActivity.this.chat != null) && (!SetAdminsActivity.this.chat.admins_enabled))) {}
      for (bool1 = true;; bool1 = false)
      {
        paramViewHolder.setChecked(bool1, false);
        if ((SetAdminsActivity.this.chat != null) && (SetAdminsActivity.this.chat.admins_enabled))
        {
          bool1 = bool2;
          if (((TLRPC.ChatParticipant)localObject).user_id != UserConfig.getInstance(SetAdminsActivity.this.currentAccount).getClientUserId()) {}
        }
        else
        {
          bool1 = true;
        }
        paramViewHolder.setCheckDisabled(bool1);
        break;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new UserCell(this.mContext, 1, 2, false);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextCheckCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      }
    }
  }
  
  public class SearchAdapter
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
          localArrayList.addAll(SetAdminsActivity.this.participants);
          Utilities.searchQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              Object localObject = SetAdminsActivity.SearchAdapter.2.this.val$query.trim().toLowerCase();
              if (((String)localObject).length() == 0) {
                SetAdminsActivity.SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
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
                ArrayList localArrayList1;
                ArrayList localArrayList2;
                int j;
                label124:
                TLRPC.ChatParticipant localChatParticipant;
                if (str2 != null)
                {
                  i = 1;
                  arrayOfString = new String[i + 1];
                  arrayOfString[0] = localObject;
                  if (str2 != null) {
                    arrayOfString[1] = str2;
                  }
                  localArrayList1 = new ArrayList();
                  localArrayList2 = new ArrayList();
                  j = 0;
                  if (j >= localArrayList.size()) {
                    break label491;
                  }
                  localChatParticipant = (TLRPC.ChatParticipant)localArrayList.get(j);
                  localObject = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getUser(Integer.valueOf(localChatParticipant.user_id));
                  if (((TLRPC.User)localObject).id != UserConfig.getInstance(SetAdminsActivity.this.currentAccount).getClientUserId()) {
                    break label216;
                  }
                }
                label216:
                label355:
                label427:
                label481:
                label489:
                for (;;)
                {
                  j++;
                  break label124;
                  i = 0;
                  break;
                  String str3 = ContactsController.formatName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name).toLowerCase();
                  str1 = LocaleController.getInstance().getTranslitString(str3);
                  str2 = str1;
                  if (str3.equals(str1)) {
                    str2 = null;
                  }
                  int k = 0;
                  int m = arrayOfString.length;
                  int n = 0;
                  for (;;)
                  {
                    if (n >= m) {
                      break label489;
                    }
                    str1 = arrayOfString[n];
                    if ((str3.startsWith(str1)) || (str3.contains(" " + str1)) || ((str2 != null) && ((str2.startsWith(str1)) || (str2.contains(" " + str1)))))
                    {
                      i = 1;
                      if (i == 0) {
                        break label481;
                      }
                      if (i != 1) {
                        break label427;
                      }
                      localArrayList2.add(AndroidUtilities.generateSearchName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name, str1));
                    }
                    for (;;)
                    {
                      localArrayList1.add(localChatParticipant);
                      break;
                      i = k;
                      if (((TLRPC.User)localObject).username == null) {
                        break label355;
                      }
                      i = k;
                      if (!((TLRPC.User)localObject).username.startsWith(str1)) {
                        break label355;
                      }
                      i = 2;
                      break label355;
                      localArrayList2.add(AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject).username, null, "@" + str1));
                    }
                    n++;
                    k = i;
                  }
                }
                label491:
                SetAdminsActivity.SearchAdapter.this.updateSearchResults(localArrayList1, localArrayList2);
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
          SetAdminsActivity.SearchAdapter.access$1402(SetAdminsActivity.SearchAdapter.this, paramArrayList);
          SetAdminsActivity.SearchAdapter.access$3302(SetAdminsActivity.SearchAdapter.this, paramArrayList1);
          SetAdminsActivity.SearchAdapter.this.notifyDataSetChanged();
        }
      });
    }
    
    public TLRPC.ChatParticipant getItem(int paramInt)
    {
      return (TLRPC.ChatParticipant)this.searchResult.get(paramInt);
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
      boolean bool1 = false;
      TLRPC.ChatParticipant localChatParticipant = getItem(paramInt);
      TLRPC.User localUser = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getUser(Integer.valueOf(localChatParticipant.user_id));
      String str = localUser.username;
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject3 = localObject1;
      if (paramInt < this.searchResult.size())
      {
        CharSequence localCharSequence = (CharSequence)this.searchResultNames.get(paramInt);
        localObject2 = localCharSequence;
        localObject3 = localObject1;
        if (localCharSequence != null)
        {
          localObject2 = localCharSequence;
          localObject3 = localObject1;
          if (str != null)
          {
            localObject2 = localCharSequence;
            localObject3 = localObject1;
            if (str.length() > 0)
            {
              localObject2 = localCharSequence;
              localObject3 = localObject1;
              if (localCharSequence.toString().startsWith("@" + str))
              {
                localObject3 = localCharSequence;
                localObject2 = null;
              }
            }
          }
        }
      }
      paramViewHolder = (UserCell)paramViewHolder.itemView;
      paramViewHolder.setData(localUser, (CharSequence)localObject2, (CharSequence)localObject3, 0);
      SetAdminsActivity.access$902(SetAdminsActivity.this, MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
      if ((!(localChatParticipant instanceof TLRPC.TL_chatParticipant)) || ((SetAdminsActivity.this.chat != null) && (!SetAdminsActivity.this.chat.admins_enabled))) {}
      for (boolean bool2 = true;; bool2 = false)
      {
        paramViewHolder.setChecked(bool2, false);
        if ((SetAdminsActivity.this.chat != null) && (SetAdminsActivity.this.chat.admins_enabled))
        {
          bool2 = bool1;
          if (localChatParticipant.user_id != UserConfig.getInstance(SetAdminsActivity.this.currentAccount).getClientUserId()) {}
        }
        else
        {
          bool2 = true;
        }
        paramViewHolder.setCheckDisabled(bool2);
        return;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new RecyclerListView.Holder(new UserCell(this.mContext, 1, 2, false));
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
          FileLog.e(localException);
          continue;
          this.searchTimer = new Timer();
          this.searchTimer.schedule(new TimerTask()
          {
            public void run()
            {
              try
              {
                SetAdminsActivity.SearchAdapter.this.searchTimer.cancel();
                SetAdminsActivity.SearchAdapter.access$2802(SetAdminsActivity.SearchAdapter.this, null);
                SetAdminsActivity.SearchAdapter.this.processSearch(paramString);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/SetAdminsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */