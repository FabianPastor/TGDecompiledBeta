package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_channelParticipantBanned;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_error;
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
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ChannelEditActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int search_button = 1;
  private int blockedUsersRow;
  private int chat_id;
  private TLRPC.Chat currentChat;
  private int eventLogRow;
  private TLRPC.ChatFull info;
  private int infoRow;
  private LinearLayoutManager layoutManager;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private int loadMoreMembersRow;
  private boolean loadingUsers;
  private int managementRow;
  private int membersEndRow;
  private int membersSection2Row;
  private int membersSectionRow;
  private int membersStartRow;
  private SparseArray<TLRPC.ChatParticipant> participantsMap = new SparseArray();
  private int permissionsRow;
  private int rowCount = 0;
  private SearchAdapter searchListViewAdapter;
  private boolean searchWas;
  private boolean searching;
  private ArrayList<Integer> sortedUsers;
  private boolean usersEndReached;
  
  public ChannelEditActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private boolean createMenuForParticipant(final TLRPC.TL_chatChannelParticipant paramTL_chatChannelParticipant, final TLRPC.ChannelParticipant paramChannelParticipant, boolean paramBoolean)
  {
    if ((paramTL_chatChannelParticipant == null) && (paramChannelParticipant == null)) {
      paramBoolean = false;
    }
    for (;;)
    {
      return paramBoolean;
      final int i = UserConfig.getInstance(this.currentAccount).getClientUserId();
      int j;
      Object localObject;
      label89:
      label122:
      int k;
      label146:
      final ArrayList localArrayList;
      if (paramChannelParticipant != null)
      {
        if (i == paramChannelParticipant.user_id)
        {
          paramBoolean = false;
          continue;
        }
        j = paramChannelParticipant.user_id;
        localObject = (TLRPC.TL_chatChannelParticipant)this.participantsMap.get(paramChannelParticipant.user_id);
        i = j;
        paramTL_chatChannelParticipant = (TLRPC.TL_chatChannelParticipant)localObject;
        if (localObject != null)
        {
          paramChannelParticipant = ((TLRPC.TL_chatChannelParticipant)localObject).channelParticipant;
          paramTL_chatChannelParticipant = (TLRPC.TL_chatChannelParticipant)localObject;
          i = j;
        }
        MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
        if ((!(paramChannelParticipant instanceof TLRPC.TL_channelParticipant)) && (!(paramChannelParticipant instanceof TLRPC.TL_channelParticipantBanned))) {
          break label216;
        }
        j = 1;
        if ((((paramChannelParticipant instanceof TLRPC.TL_channelParticipantAdmin)) || ((paramChannelParticipant instanceof TLRPC.TL_channelParticipantCreator))) && (!paramChannelParticipant.can_edit)) {
          break label222;
        }
        k = 1;
        if (!paramBoolean) {
          break label228;
        }
        localObject = null;
        localArrayList = null;
      }
      for (;;)
      {
        if ((j != 0) && (ChatObject.canAddAdmins(this.currentChat)))
        {
          if (paramBoolean)
          {
            paramBoolean = true;
            break;
            if (paramTL_chatChannelParticipant.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId())
            {
              paramBoolean = false;
              break;
            }
            i = paramTL_chatChannelParticipant.user_id;
            paramChannelParticipant = paramTL_chatChannelParticipant.channelParticipant;
            break label89;
            label216:
            j = 0;
            break label122;
            label222:
            k = 0;
            break label146;
            label228:
            localObject = new ArrayList();
            localArrayList = new ArrayList();
            continue;
          }
          ((ArrayList)localObject).add(LocaleController.getString("SetAsAdmin", NUM));
          localArrayList.add(Integer.valueOf(0));
        }
      }
      if ((ChatObject.canBlockUsers(this.currentChat)) && (k != 0))
      {
        if (paramBoolean)
        {
          paramBoolean = true;
          continue;
        }
        if (!this.currentChat.megagroup) {
          break label376;
        }
        ((ArrayList)localObject).add(LocaleController.getString("KickFromSupergroup", NUM));
        localArrayList.add(Integer.valueOf(1));
        ((ArrayList)localObject).add(LocaleController.getString("KickFromGroup", NUM));
        localArrayList.add(Integer.valueOf(2));
      }
      for (;;)
      {
        if ((localObject != null) && (!((ArrayList)localObject).isEmpty())) {
          break label404;
        }
        paramBoolean = false;
        break;
        label376:
        ((ArrayList)localObject).add(LocaleController.getString("ChannelRemoveUser", NUM));
        localArrayList.add(Integer.valueOf(2));
      }
      label404:
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
      localBuilder.setItems((CharSequence[])((ArrayList)localObject).toArray(new CharSequence[((ArrayList)localObject).size()]), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, final int paramAnonymousInt)
        {
          if (((Integer)localArrayList.get(paramAnonymousInt)).intValue() == 2) {
            MessagesController.getInstance(ChannelEditActivity.this.currentAccount).deleteUserFromChat(ChannelEditActivity.this.chat_id, MessagesController.getInstance(ChannelEditActivity.this.currentAccount).getUser(Integer.valueOf(i)), ChannelEditActivity.this.info);
          }
          for (;;)
          {
            return;
            paramAnonymousDialogInterface = new ChannelRightsEditActivity(paramChannelParticipant.user_id, ChannelEditActivity.this.chat_id, paramChannelParticipant.admin_rights, paramChannelParticipant.banned_rights, ((Integer)localArrayList.get(paramAnonymousInt)).intValue(), true);
            paramAnonymousDialogInterface.setDelegate(new ChannelRightsEditActivity.ChannelRightsEditActivityDelegate()
            {
              public void didSetRights(int paramAnonymous2Int, TLRPC.TL_channelAdminRights paramAnonymous2TL_channelAdminRights, TLRPC.TL_channelBannedRights paramAnonymous2TL_channelBannedRights)
              {
                ChannelEditActivity.9.this.val$channelParticipantFinal.admin_rights = paramAnonymous2TL_channelAdminRights;
                ChannelEditActivity.9.this.val$channelParticipantFinal.banned_rights = paramAnonymous2TL_channelBannedRights;
                if (((Integer)ChannelEditActivity.9.this.val$actions.get(paramAnonymousInt)).intValue() == 0) {
                  if (ChannelEditActivity.9.this.val$userFinal != null)
                  {
                    if (paramAnonymous2Int != 1) {
                      break label153;
                    }
                    ChannelEditActivity.9.this.val$userFinal.channelParticipant = new TLRPC.TL_channelParticipantAdmin();
                    ChannelEditActivity.9.this.val$userFinal.channelParticipant.inviter_id = UserConfig.getInstance(ChannelEditActivity.this.currentAccount).getClientUserId();
                    ChannelEditActivity.9.this.val$userFinal.channelParticipant.user_id = ChannelEditActivity.9.this.val$userFinal.user_id;
                    ChannelEditActivity.9.this.val$userFinal.channelParticipant.date = ChannelEditActivity.9.this.val$userFinal.date;
                  }
                }
                label153:
                while ((((Integer)ChannelEditActivity.9.this.val$actions.get(paramAnonymousInt)).intValue() != 1) || (paramAnonymous2Int != 0) || (!ChannelEditActivity.this.currentChat.megagroup) || (ChannelEditActivity.this.info == null) || (ChannelEditActivity.this.info.participants == null)) {
                  for (;;)
                  {
                    return;
                    ChannelEditActivity.9.this.val$userFinal.channelParticipant = new TLRPC.TL_channelParticipant();
                  }
                }
                int i = 0;
                int j = 0;
                label252:
                paramAnonymous2Int = i;
                if (j < ChannelEditActivity.this.info.participants.participants.size())
                {
                  if (((TLRPC.TL_chatChannelParticipant)ChannelEditActivity.this.info.participants.participants.get(j)).channelParticipant.user_id == ChannelEditActivity.9.this.val$uid)
                  {
                    if (ChannelEditActivity.this.info != null)
                    {
                      paramAnonymous2TL_channelAdminRights = ChannelEditActivity.this.info;
                      paramAnonymous2TL_channelAdminRights.participants_count -= 1;
                    }
                    ChannelEditActivity.this.info.participants.participants.remove(j);
                    paramAnonymous2Int = 1;
                  }
                }
                else
                {
                  i = paramAnonymous2Int;
                  if (ChannelEditActivity.this.info != null)
                  {
                    i = paramAnonymous2Int;
                    if (ChannelEditActivity.this.info.participants == null) {}
                  }
                }
                for (j = 0;; j++)
                {
                  i = paramAnonymous2Int;
                  if (j < ChannelEditActivity.this.info.participants.participants.size())
                  {
                    if (((TLRPC.ChatParticipant)ChannelEditActivity.this.info.participants.participants.get(j)).user_id == ChannelEditActivity.9.this.val$uid)
                    {
                      ChannelEditActivity.this.info.participants.participants.remove(j);
                      i = 1;
                    }
                  }
                  else
                  {
                    if (i == 0) {
                      break;
                    }
                    NotificationCenter.getInstance(ChannelEditActivity.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { ChannelEditActivity.this.info, Integer.valueOf(0), Boolean.valueOf(true), null });
                    break;
                    j++;
                    break label252;
                  }
                }
              }
            });
            ChannelEditActivity.this.presentFragment(paramAnonymousDialogInterface);
          }
        }
      });
      showDialog(localBuilder.create());
      paramBoolean = true;
    }
  }
  
  private void fetchUsersFromChannelInfo()
  {
    if (((this.info instanceof TLRPC.TL_channelFull)) && (this.info.participants != null)) {
      for (int i = 0; i < this.info.participants.participants.size(); i++)
      {
        TLRPC.ChatParticipant localChatParticipant = (TLRPC.ChatParticipant)this.info.participants.participants.get(i);
        this.participantsMap.put(localChatParticipant.user_id, localChatParticipant);
      }
    }
  }
  
  private void getChannelParticipants(boolean paramBoolean)
  {
    int i = 0;
    if ((this.loadingUsers) || (this.participantsMap == null) || (this.info == null)) {
      return;
    }
    this.loadingUsers = true;
    final int j;
    label47:
    final TLRPC.TL_channels_getParticipants localTL_channels_getParticipants;
    if ((this.participantsMap.size() != 0) && (paramBoolean))
    {
      j = 300;
      localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
      localTL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
      localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
      if (!paramBoolean) {
        break label152;
      }
    }
    for (;;)
    {
      localTL_channels_getParticipants.offset = i;
      localTL_channels_getParticipants.limit = 200;
      j = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_getParticipants, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (paramAnonymousTL_error == null)
              {
                TLRPC.TL_channels_channelParticipants localTL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants)paramAnonymousTLObject;
                MessagesController.getInstance(ChannelEditActivity.this.currentAccount).putUsers(localTL_channels_channelParticipants.users, false);
                if (localTL_channels_channelParticipants.users.size() < 200) {
                  ChannelEditActivity.access$2302(ChannelEditActivity.this, true);
                }
                if (ChannelEditActivity.8.this.val$req.offset == 0)
                {
                  ChannelEditActivity.this.participantsMap.clear();
                  ChannelEditActivity.this.info.participants = new TLRPC.TL_chatParticipants();
                  MessagesStorage.getInstance(ChannelEditActivity.this.currentAccount).putUsersAndChats(localTL_channels_channelParticipants.users, null, true, true);
                  MessagesStorage.getInstance(ChannelEditActivity.this.currentAccount).updateChannelUsers(ChannelEditActivity.this.chat_id, localTL_channels_channelParticipants.participants);
                }
                for (int i = 0; i < localTL_channels_channelParticipants.participants.size(); i++)
                {
                  TLRPC.TL_chatChannelParticipant localTL_chatChannelParticipant = new TLRPC.TL_chatChannelParticipant();
                  localTL_chatChannelParticipant.channelParticipant = ((TLRPC.ChannelParticipant)localTL_channels_channelParticipants.participants.get(i));
                  localTL_chatChannelParticipant.inviter_id = localTL_chatChannelParticipant.channelParticipant.inviter_id;
                  localTL_chatChannelParticipant.user_id = localTL_chatChannelParticipant.channelParticipant.user_id;
                  localTL_chatChannelParticipant.date = localTL_chatChannelParticipant.channelParticipant.date;
                  if (ChannelEditActivity.this.participantsMap.indexOfKey(localTL_chatChannelParticipant.user_id) < 0)
                  {
                    ChannelEditActivity.this.info.participants.participants.add(localTL_chatChannelParticipant);
                    ChannelEditActivity.this.participantsMap.put(localTL_chatChannelParticipant.user_id, localTL_chatChannelParticipant);
                  }
                }
              }
              ChannelEditActivity.access$2602(ChannelEditActivity.this, false);
              NotificationCenter.getInstance(ChannelEditActivity.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { ChannelEditActivity.this.info, Integer.valueOf(0), Boolean.valueOf(true), null });
            }
          }, j);
        }
      });
      ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(j, this.classGuid);
      break;
      j = 0;
      break label47;
      label152:
      i = this.participantsMap.size();
    }
  }
  
  private void updateRowsIds()
  {
    this.rowCount = 0;
    if (ChatObject.canEditInfo(this.currentChat))
    {
      int i = this.rowCount;
      this.rowCount = (i + 1);
      this.infoRow = i;
      this.permissionsRow = -1;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.eventLogRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.managementRow = i;
      if ((!this.currentChat.megagroup) && ((this.info == null) || ((this.info.banned_count == 0) && (this.info.kicked_count == 0)))) {
        break label263;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.blockedUsersRow = i;
      label125:
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.membersSectionRow = i;
      if ((this.info == null) || (this.info.participants == null) || (this.info.participants.participants.isEmpty())) {
        break label279;
      }
      this.membersStartRow = this.rowCount;
      this.rowCount += this.info.participants.participants.size();
      this.membersEndRow = this.rowCount;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.membersSection2Row = i;
      if (this.usersEndReached) {
        break label271;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.loadMoreMembersRow = i;
    }
    for (;;)
    {
      return;
      this.infoRow = -1;
      break;
      label263:
      this.blockedUsersRow = -1;
      break label125;
      label271:
      this.loadMoreMembersRow = -1;
      continue;
      label279:
      this.membersStartRow = -1;
      this.membersEndRow = -1;
      this.loadMoreMembersRow = -1;
      this.membersSection2Row = -1;
    }
  }
  
  public View createView(Context paramContext)
  {
    Theme.createProfileResources(paramContext);
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    if (this.currentChat.megagroup) {
      this.actionBar.setTitle(LocaleController.getString("ManageGroup", NUM));
    }
    for (;;)
    {
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (ChannelEditActivity.this.getParentActivity() == null) {}
          for (;;)
          {
            return;
            if (paramAnonymousInt == -1) {
              ChannelEditActivity.this.finishFragment();
            }
          }
        }
      });
      this.searchListViewAdapter = new SearchAdapter(paramContext);
      this.actionBar.createMenu().addItem(1, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
      {
        public void onSearchCollapse()
        {
          ChannelEditActivity.this.searchListViewAdapter.searchDialogs(null);
          ChannelEditActivity.access$302(ChannelEditActivity.this, false);
          ChannelEditActivity.access$502(ChannelEditActivity.this, false);
          ChannelEditActivity.this.listView.setAdapter(ChannelEditActivity.this.listViewAdapter);
          ChannelEditActivity.this.listViewAdapter.notifyDataSetChanged();
          ChannelEditActivity.this.listView.setFastScrollVisible(true);
          ChannelEditActivity.this.listView.setVerticalScrollBarEnabled(false);
        }
        
        public void onSearchExpand()
        {
          ChannelEditActivity.access$302(ChannelEditActivity.this, true);
        }
        
        public void onTextChanged(EditText paramAnonymousEditText)
        {
          if (ChannelEditActivity.this.searchListViewAdapter == null) {}
          for (;;)
          {
            return;
            paramAnonymousEditText = paramAnonymousEditText.getText().toString();
            if (paramAnonymousEditText.length() != 0)
            {
              ChannelEditActivity.access$502(ChannelEditActivity.this, true);
              if (ChannelEditActivity.this.listView != null)
              {
                ChannelEditActivity.this.listView.setAdapter(ChannelEditActivity.this.searchListViewAdapter);
                ChannelEditActivity.this.searchListViewAdapter.notifyDataSetChanged();
                ChannelEditActivity.this.listView.setFastScrollVisible(false);
                ChannelEditActivity.this.listView.setVerticalScrollBarEnabled(true);
              }
            }
            ChannelEditActivity.this.searchListViewAdapter.searchDialogs(paramAnonymousEditText);
          }
        }
      }).getSearchField().setHint(LocaleController.getString("Search", NUM));
      this.listViewAdapter = new ListAdapter(paramContext);
      this.fragmentView = new FrameLayout(paramContext);
      this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
      Object localObject = new EmptyTextProgressView(paramContext);
      ((EmptyTextProgressView)localObject).setShowAtCenter(true);
      ((EmptyTextProgressView)localObject).setText(LocaleController.getString("NoResult", NUM));
      ((EmptyTextProgressView)localObject).showTextView();
      localFrameLayout.addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F));
      this.listView = new RecyclerListView(paramContext)
      {
        public boolean hasOverlappingRendering()
        {
          return false;
        }
      };
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setEmptyView((View)localObject);
      localObject = this.listView;
      paramContext = new LinearLayoutManager(paramContext, 1, false);
      this.layoutManager = paramContext;
      ((RecyclerListView)localObject).setLayoutManager(paramContext);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
      this.listView.setAdapter(this.listViewAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (ChannelEditActivity.this.getParentActivity() == null) {}
          for (;;)
          {
            return;
            if (ChannelEditActivity.this.listView.getAdapter() == ChannelEditActivity.this.searchListViewAdapter)
            {
              paramAnonymousView = new Bundle();
              paramAnonymousView.putInt("user_id", ChannelEditActivity.this.searchListViewAdapter.getItem(paramAnonymousInt).user_id);
              ChannelEditActivity.this.presentFragment(new ProfileActivity(paramAnonymousView));
            }
            else
            {
              if ((paramAnonymousInt >= ChannelEditActivity.this.membersStartRow) && (paramAnonymousInt < ChannelEditActivity.this.membersEndRow))
              {
                if (!ChannelEditActivity.this.sortedUsers.isEmpty()) {}
                for (paramAnonymousInt = ((TLRPC.ChatParticipant)ChannelEditActivity.this.info.participants.participants.get(((Integer)ChannelEditActivity.this.sortedUsers.get(paramAnonymousInt - ChannelEditActivity.this.membersStartRow)).intValue())).user_id;; paramAnonymousInt = ((TLRPC.ChatParticipant)ChannelEditActivity.this.info.participants.participants.get(paramAnonymousInt - ChannelEditActivity.this.membersStartRow)).user_id)
                {
                  paramAnonymousView = new Bundle();
                  paramAnonymousView.putInt("user_id", paramAnonymousInt);
                  ChannelEditActivity.this.presentFragment(new ProfileActivity(paramAnonymousView));
                  break;
                }
              }
              if ((paramAnonymousInt == ChannelEditActivity.this.blockedUsersRow) || (paramAnonymousInt == ChannelEditActivity.this.managementRow))
              {
                paramAnonymousView = new Bundle();
                paramAnonymousView.putInt("chat_id", ChannelEditActivity.this.chat_id);
                if (paramAnonymousInt == ChannelEditActivity.this.blockedUsersRow) {
                  paramAnonymousView.putInt("type", 0);
                }
                for (;;)
                {
                  ChannelEditActivity.this.presentFragment(new ChannelUsersActivity(paramAnonymousView));
                  break;
                  if (paramAnonymousInt == ChannelEditActivity.this.managementRow) {
                    paramAnonymousView.putInt("type", 1);
                  }
                }
              }
              if (paramAnonymousInt == ChannelEditActivity.this.permissionsRow)
              {
                paramAnonymousView = new ChannelPermissionsActivity(ChannelEditActivity.this.chat_id);
                paramAnonymousView.setInfo(ChannelEditActivity.this.info);
                ChannelEditActivity.this.presentFragment(paramAnonymousView);
              }
              else if (paramAnonymousInt == ChannelEditActivity.this.eventLogRow)
              {
                ChannelEditActivity.this.presentFragment(new ChannelAdminLogActivity(ChannelEditActivity.this.currentChat));
              }
              else if (paramAnonymousInt == ChannelEditActivity.this.infoRow)
              {
                paramAnonymousView = new Bundle();
                paramAnonymousView.putInt("chat_id", ChannelEditActivity.this.chat_id);
                paramAnonymousView = new ChannelEditInfoActivity(paramAnonymousView);
                paramAnonymousView.setInfo(ChannelEditActivity.this.info);
                ChannelEditActivity.this.presentFragment(paramAnonymousView);
              }
            }
          }
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          boolean bool;
          if ((paramAnonymousInt >= ChannelEditActivity.this.membersStartRow) && (paramAnonymousInt < ChannelEditActivity.this.membersEndRow)) {
            if (ChannelEditActivity.this.getParentActivity() == null) {
              bool = false;
            }
          }
          for (;;)
          {
            return bool;
            if (!ChannelEditActivity.this.sortedUsers.isEmpty()) {}
            for (paramAnonymousView = (TLRPC.TL_chatChannelParticipant)ChannelEditActivity.this.info.participants.participants.get(((Integer)ChannelEditActivity.this.sortedUsers.get(paramAnonymousInt - ChannelEditActivity.this.membersStartRow)).intValue());; paramAnonymousView = (TLRPC.TL_chatChannelParticipant)ChannelEditActivity.this.info.participants.participants.get(paramAnonymousInt - ChannelEditActivity.this.membersStartRow))
            {
              bool = ChannelEditActivity.this.createMenuForParticipant(paramAnonymousView, null, false);
              break;
            }
            bool = false;
          }
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if ((ChannelEditActivity.this.participantsMap != null) && (ChannelEditActivity.this.loadMoreMembersRow != -1) && (ChannelEditActivity.this.layoutManager.findLastVisibleItemPosition() > ChannelEditActivity.this.loadMoreMembersRow - 8)) {
            ChannelEditActivity.this.getChannelParticipants(false);
          }
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("ManageChannel", NUM));
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    paramInt2 = 0;
    if (paramInt1 == NotificationCenter.chatInfoDidLoaded)
    {
      TLRPC.ChatFull localChatFull = (TLRPC.ChatFull)paramVarArgs[0];
      if (localChatFull.id == this.chat_id)
      {
        boolean bool = ((Boolean)paramVarArgs[2]).booleanValue();
        if (((this.info instanceof TLRPC.TL_channelFull)) && (localChatFull.participants == null) && (this.info != null)) {
          localChatFull.participants = this.info.participants;
        }
        paramInt1 = paramInt2;
        if (this.info == null)
        {
          paramInt1 = paramInt2;
          if ((localChatFull instanceof TLRPC.TL_channelFull)) {
            paramInt1 = 1;
          }
        }
        this.info = localChatFull;
        fetchUsersFromChannelInfo();
        updateRowsIds();
        if (this.listViewAdapter != null) {
          this.listViewAdapter.notifyDataSetChanged();
        }
        paramVarArgs = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
        if (paramVarArgs != null) {
          this.currentChat = paramVarArgs;
        }
        if ((paramInt1 != 0) || (!bool)) {
          getChannelParticipants(true);
        }
      }
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.closeChats) {
        removeSelfFromStack();
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local10 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        if (ChannelEditActivity.this.listView != null)
        {
          int i = ChannelEditActivity.this.listView.getChildCount();
          for (int j = 0; j < i; j++)
          {
            View localView = ChannelEditActivity.this.listView.getChildAt(j);
            if ((localView instanceof ManageChatUserCell)) {
              ((ManageChatUserCell)localView).update(0);
            }
          }
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorBlue");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    ThemeDescription localThemeDescription8 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { ManageChatTextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { ManageChatTextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGreenText2");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { ManageChatTextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText5");
    localObject2 = new ThemeDescription(this.listView, 0, new Class[] { ManageChatTextCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.listView, 0, new Class[] { ManageChatUserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    localObject1 = new ThemeDescription(this.listView, 0, new Class[] { ManageChatUserCell.class }, new String[] { "statusColor" }, null, null, local10, "windowBackgroundWhiteGrayText");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.listView, 0, new Class[] { ManageChatUserCell.class }, new String[] { "statusOnlineColor" }, null, null, local10, "windowBackgroundWhiteBlueText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    Drawable localDrawable3 = Theme.avatar_savedDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localObject2, localThemeDescription13, localObject1, localThemeDescription14, new ThemeDescription(localRecyclerListView, 0, new Class[] { ManageChatUserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2, localDrawable3 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local10, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local10, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local10, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local10, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local10, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local10, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local10, "avatar_backgroundPink"), new ThemeDescription(this.listView, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4") };
  }
  
  public boolean onFragmentCreate()
  {
    boolean bool = false;
    this.chat_id = getArguments().getInt("chat_id", 0);
    this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
    final CountDownLatch localCountDownLatch;
    if (this.currentChat == null)
    {
      localCountDownLatch = new CountDownLatch(1);
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          ChannelEditActivity.access$002(ChannelEditActivity.this, MessagesStorage.getInstance(ChannelEditActivity.this.currentAccount).getChat(ChannelEditActivity.this.chat_id));
          localCountDownLatch.countDown();
        }
      });
    }
    try
    {
      localCountDownLatch.await();
      if (this.currentChat != null)
      {
        MessagesController.getInstance(this.currentAccount).putChat(this.currentChat, true);
        getChannelParticipants(true);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        this.sortedUsers = new ArrayList();
        updateRowsIds();
        bool = true;
      }
      return bool;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
  
  public void setInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
    fetchUsersFromChannelInfo();
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
      return ChannelEditActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      int j = i;
      if (paramInt != ChannelEditActivity.this.managementRow)
      {
        j = i;
        if (paramInt != ChannelEditActivity.this.blockedUsersRow)
        {
          j = i;
          if (paramInt != ChannelEditActivity.this.infoRow)
          {
            j = i;
            if (paramInt != ChannelEditActivity.this.eventLogRow)
            {
              if (paramInt != ChannelEditActivity.this.permissionsRow) {
                break label69;
              }
              j = i;
            }
          }
        }
      }
      for (;;)
      {
        return j;
        label69:
        if ((paramInt >= ChannelEditActivity.this.membersStartRow) && (paramInt < ChannelEditActivity.this.membersEndRow))
        {
          j = 1;
        }
        else if ((paramInt == ChannelEditActivity.this.membersSectionRow) || (paramInt == ChannelEditActivity.this.membersSection2Row))
        {
          j = 2;
        }
        else
        {
          j = i;
          if (paramInt == ChannelEditActivity.this.loadMoreMembersRow) {
            j = 3;
          }
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool1 = true;
      int i = paramViewHolder.getItemViewType();
      boolean bool2 = bool1;
      if (i != 0) {
        if (i != 1) {
          break label25;
        }
      }
      label25:
      for (bool2 = bool1;; bool2 = false) {
        return bool2;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = false;
      Object localObject1 = null;
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        Object localObject2 = (ManageChatTextCell)paramViewHolder.itemView;
        ((ManageChatTextCell)localObject2).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        ((ManageChatTextCell)localObject2).setTag("windowBackgroundWhiteBlackText");
        if (paramInt == ChannelEditActivity.this.managementRow)
        {
          localObject1 = LocaleController.getString("ChannelAdministrators", NUM);
          if (ChannelEditActivity.this.info != null)
          {
            paramViewHolder = String.format("%d", new Object[] { Integer.valueOf(ChannelEditActivity.this.info.admins_count) });
            label119:
            if (ChannelEditActivity.this.blockedUsersRow == -1) {
              break label151;
            }
          }
          label151:
          for (bool = true;; bool = false)
          {
            ((ManageChatTextCell)localObject2).setText((String)localObject1, paramViewHolder, NUM, bool);
            break;
            paramViewHolder = null;
            break label119;
          }
        }
        if (paramInt == ChannelEditActivity.this.blockedUsersRow)
        {
          String str = LocaleController.getString("ChannelBlacklist", NUM);
          paramViewHolder = (RecyclerView.ViewHolder)localObject1;
          if (ChannelEditActivity.this.info != null) {
            paramViewHolder = String.format("%d", new Object[] { Integer.valueOf(ChannelEditActivity.this.info.kicked_count + ChannelEditActivity.this.info.banned_count) });
          }
          ((ManageChatTextCell)localObject2).setText(str, paramViewHolder, NUM, false);
        }
        else if (paramInt == ChannelEditActivity.this.eventLogRow)
        {
          ((ManageChatTextCell)localObject2).setText(LocaleController.getString("EventLog", NUM), null, NUM, true);
        }
        else
        {
          if (paramInt == ChannelEditActivity.this.infoRow)
          {
            if (ChannelEditActivity.this.currentChat.megagroup) {}
            for (paramViewHolder = LocaleController.getString("EventLogFilterGroupInfo", NUM);; paramViewHolder = LocaleController.getString("EventLogFilterChannelInfo", NUM))
            {
              ((ManageChatTextCell)localObject2).setText(paramViewHolder, null, NUM, true);
              break;
            }
          }
          if (paramInt == ChannelEditActivity.this.permissionsRow)
          {
            continue;
            localObject1 = (ManageChatUserCell)paramViewHolder.itemView;
            ((ManageChatUserCell)localObject1).setTag(Integer.valueOf(paramInt));
            if (!ChannelEditActivity.this.sortedUsers.isEmpty())
            {
              paramViewHolder = (TLRPC.ChatParticipant)ChannelEditActivity.this.info.participants.participants.get(((Integer)ChannelEditActivity.this.sortedUsers.get(paramInt - ChannelEditActivity.this.membersStartRow)).intValue());
              label416:
              if (paramViewHolder == null) {
                break label520;
              }
              if (!(paramViewHolder instanceof TLRPC.TL_chatChannelParticipant)) {
                break label522;
              }
              localObject2 = ((TLRPC.TL_chatChannelParticipant)paramViewHolder).channelParticipant;
              if (((localObject2 instanceof TLRPC.TL_channelParticipantCreator)) || ((localObject2 instanceof TLRPC.TL_channelParticipantAdmin))) {
                bool = true;
              }
              ((ManageChatUserCell)localObject1).setIsAdmin(bool);
            }
            for (;;)
            {
              ((ManageChatUserCell)localObject1).setData(MessagesController.getInstance(ChannelEditActivity.this.currentAccount).getUser(Integer.valueOf(paramViewHolder.user_id)), null, null);
              break;
              paramViewHolder = (TLRPC.ChatParticipant)ChannelEditActivity.this.info.participants.participants.get(paramInt - ChannelEditActivity.this.membersStartRow);
              break label416;
              label520:
              break;
              label522:
              ((ManageChatUserCell)localObject1).setIsAdmin(paramViewHolder instanceof TLRPC.TL_chatParticipantAdmin);
            }
            if ((paramInt == ChannelEditActivity.this.membersSectionRow) && (ChannelEditActivity.this.membersStartRow != -1)) {
              paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            } else {
              paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      switch (paramInt)
      {
      }
      for (;;)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ManageChatTextCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new ManageChatUserCell(this.mContext, 8, true);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ((ManageChatUserCell)paramViewGroup).setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate()
        {
          public boolean onOptionsButtonCheck(ManageChatUserCell paramAnonymousManageChatUserCell, boolean paramAnonymousBoolean)
          {
            int i = ((Integer)paramAnonymousManageChatUserCell.getTag()).intValue();
            ChannelEditActivity localChannelEditActivity;
            if (!ChannelEditActivity.this.sortedUsers.isEmpty())
            {
              paramAnonymousManageChatUserCell = (TLRPC.ChatParticipant)ChannelEditActivity.this.info.participants.participants.get(((Integer)ChannelEditActivity.this.sortedUsers.get(i - ChannelEditActivity.this.membersStartRow)).intValue());
              localChannelEditActivity = ChannelEditActivity.this;
              paramAnonymousManageChatUserCell = (TLRPC.TL_chatChannelParticipant)paramAnonymousManageChatUserCell;
              if (paramAnonymousBoolean) {
                break label148;
              }
            }
            label148:
            for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false)
            {
              return localChannelEditActivity.createMenuForParticipant(paramAnonymousManageChatUserCell, null, paramAnonymousBoolean);
              paramAnonymousManageChatUserCell = (TLRPC.ChatParticipant)ChannelEditActivity.this.info.participants.participants.get(i - ChannelEditActivity.this.membersStartRow);
              break;
            }
          }
        });
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
        continue;
        paramViewGroup = new LoadingCell(this.mContext);
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
    private SearchAdapterHelper searchAdapterHelper;
    private Timer searchTimer;
    
    public SearchAdapter(Context paramContext)
    {
      this.mContext = paramContext;
      this.searchAdapterHelper = new SearchAdapterHelper(true);
      this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate()
      {
        public void onDataSetChanged()
        {
          ChannelEditActivity.SearchAdapter.this.notifyDataSetChanged();
        }
        
        public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> paramAnonymousArrayList, HashMap<String, SearchAdapterHelper.HashtagObject> paramAnonymousHashMap) {}
      });
    }
    
    private void processSearch(final String paramString)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          ChannelEditActivity.SearchAdapter.this.searchAdapterHelper.queryServerSearch(paramString, false, false, true, true, ChannelEditActivity.this.chat_id, false);
        }
      });
    }
    
    public TLRPC.ChannelParticipant getItem(int paramInt)
    {
      return (TLRPC.ChannelParticipant)this.searchAdapterHelper.getGroupSearch().get(paramInt);
    }
    
    public int getItemCount()
    {
      return this.searchAdapterHelper.getGroupSearch().size();
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool = true;
      if (paramViewHolder.getItemViewType() != 1) {}
      for (;;)
      {
        return bool;
        bool = false;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      default: 
        return;
      }
      Object localObject1 = getItem(paramInt);
      if ((localObject1 instanceof TLRPC.User))
      {
        localObject1 = (TLRPC.User)localObject1;
        Object localObject2 = (TLRPC.ChatParticipant)ChannelEditActivity.this.participantsMap.get(((TLRPC.User)localObject1).id);
        if ((localObject2 instanceof TLRPC.TL_chatChannelParticipant))
        {
          localObject2 = ((TLRPC.TL_chatChannelParticipant)localObject2).channelParticipant;
          if (((localObject2 instanceof TLRPC.TL_channelParticipantCreator)) || ((localObject2 instanceof TLRPC.TL_channelParticipantAdmin))) {
            bool = true;
          }
        }
        for (;;)
        {
          localObject2 = null;
          String str = this.searchAdapterHelper.getLastFoundChannel();
          if (str != null)
          {
            localObject2 = UserObject.getUserName((TLRPC.User)localObject1);
            SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder((CharSequence)localObject2);
            int i = ((String)localObject2).toLowerCase().indexOf(str);
            localObject2 = localSpannableStringBuilder;
            if (i != -1)
            {
              ((SpannableStringBuilder)localSpannableStringBuilder).setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), i, str.length() + i, 33);
              localObject2 = localSpannableStringBuilder;
            }
          }
          paramViewHolder = (ManageChatUserCell)paramViewHolder.itemView;
          paramViewHolder.setTag(Integer.valueOf(paramInt));
          paramViewHolder.setIsAdmin(bool);
          paramViewHolder.setData((TLRPC.User)localObject1, (CharSequence)localObject2, null);
          break;
          bool = false;
          continue;
          bool = localObject2 instanceof TLRPC.TL_chatParticipantAdmin;
        }
      }
      if (((localObject1 instanceof TLRPC.TL_channelParticipantAdmin)) || ((localObject1 instanceof TLRPC.TL_channelParticipantCreator))) {}
      for (boolean bool = true;; bool = false)
      {
        localObject1 = MessagesController.getInstance(ChannelEditActivity.this.currentAccount).getUser(Integer.valueOf(((TLRPC.ChannelParticipant)localObject1).user_id));
        break;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new ManageChatUserCell(this.mContext, 8, true);
      paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      ((ManageChatUserCell)paramViewGroup).setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate()
      {
        public boolean onOptionsButtonCheck(ManageChatUserCell paramAnonymousManageChatUserCell, boolean paramAnonymousBoolean)
        {
          ChannelEditActivity localChannelEditActivity = ChannelEditActivity.this;
          paramAnonymousManageChatUserCell = ChannelEditActivity.SearchAdapter.this.getItem(((Integer)paramAnonymousManageChatUserCell.getTag()).intValue());
          if (!paramAnonymousBoolean) {}
          for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false) {
            return localChannelEditActivity.createMenuForParticipant(null, paramAnonymousManageChatUserCell, paramAnonymousBoolean);
          }
        }
      });
      return new RecyclerListView.Holder(paramViewGroup);
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
          this.searchAdapterHelper.queryServerSearch(null, false, false, true, true, ChannelEditActivity.this.chat_id, false);
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
                ChannelEditActivity.SearchAdapter.this.searchTimer.cancel();
                ChannelEditActivity.SearchAdapter.access$3202(ChannelEditActivity.SearchAdapter.this, null);
                ChannelEditActivity.SearchAdapter.this.processSearch(paramString);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChannelEditActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */