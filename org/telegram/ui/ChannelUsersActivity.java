package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.ChannelParticipantRole;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantEditor;
import org.telegram.tgnet.TLRPC.TL_channelParticipantModerator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channelRoleEditor;
import org.telegram.tgnet.TLRPC.TL_channelRoleEmpty;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_kickFromChannel;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;

public class ChannelUsersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int chatId = this.arguments.getInt("chat_id");
  private EmptyTextProgressView emptyView;
  private boolean firstLoaded;
  private boolean isAdmin;
  private boolean isMegagroup;
  private boolean isPublic;
  private ListAdapter listViewAdapter;
  private boolean loadingUsers;
  private ArrayList<TLRPC.ChannelParticipant> participants = new ArrayList();
  private int participantsStartRow;
  private int type = this.arguments.getInt("type");
  
  public ChannelUsersActivity(Bundle paramBundle)
  {
    super(paramBundle);
    paramBundle = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
    boolean bool;
    if (paramBundle != null)
    {
      if (paramBundle.creator)
      {
        this.isAdmin = true;
        if ((paramBundle.flags & 0x40) != 0)
        {
          bool = true;
          this.isPublic = bool;
        }
      }
      else
      {
        this.isMegagroup = paramBundle.megagroup;
      }
    }
    else
    {
      if (this.type != 0) {
        break label122;
      }
      this.participantsStartRow = 0;
    }
    label122:
    do
    {
      return;
      bool = false;
      break;
      if (this.type == 1)
      {
        i = j;
        if (this.isAdmin)
        {
          i = j;
          if (this.isMegagroup) {
            i = 4;
          }
        }
        this.participantsStartRow = i;
        return;
      }
    } while (this.type != 2);
    if (this.isAdmin) {
      if (!this.isPublic) {
        break label186;
      }
    }
    label186:
    for (i = 2;; i = 3)
    {
      this.participantsStartRow = i;
      return;
    }
  }
  
  private int getChannelAdminParticipantType(TLRPC.ChannelParticipant paramChannelParticipant)
  {
    if (((paramChannelParticipant instanceof TLRPC.TL_channelParticipantCreator)) || ((paramChannelParticipant instanceof TLRPC.TL_channelParticipantSelf))) {
      return 0;
    }
    if ((paramChannelParticipant instanceof TLRPC.TL_channelParticipantEditor)) {
      return 1;
    }
    return 2;
  }
  
  private void getChannelParticipants(int paramInt1, int paramInt2)
  {
    if (this.loadingUsers) {
      return;
    }
    this.loadingUsers = true;
    if ((this.emptyView != null) && (!this.firstLoaded)) {
      this.emptyView.showProgress();
    }
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
    TLRPC.TL_channels_getParticipants localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
    localTL_channels_getParticipants.channel = MessagesController.getInputChannel(this.chatId);
    if (this.type == 0) {
      localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsKicked();
    }
    for (;;)
    {
      localTL_channels_getParticipants.offset = paramInt1;
      localTL_channels_getParticipants.limit = paramInt2;
      paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_channels_getParticipants, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              TLRPC.TL_channels_channelParticipants localTL_channels_channelParticipants;
              if (paramAnonymousTL_error == null)
              {
                localTL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants)paramAnonymousTLObject;
                MessagesController.getInstance().putUsers(localTL_channels_channelParticipants.users, false);
                ChannelUsersActivity.access$602(ChannelUsersActivity.this, localTL_channels_channelParticipants.participants);
              }
              for (;;)
              {
                try
                {
                  if ((ChannelUsersActivity.this.type != 0) && (ChannelUsersActivity.this.type != 2)) {
                    continue;
                  }
                  Collections.sort(ChannelUsersActivity.this.participants, new Comparator()
                  {
                    public int compare(TLRPC.ChannelParticipant paramAnonymous3ChannelParticipant1, TLRPC.ChannelParticipant paramAnonymous3ChannelParticipant2)
                    {
                      paramAnonymous3ChannelParticipant2 = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymous3ChannelParticipant2.user_id));
                      paramAnonymous3ChannelParticipant1 = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymous3ChannelParticipant1.user_id));
                      int j = 0;
                      int k = 0;
                      int i = j;
                      if (paramAnonymous3ChannelParticipant2 != null)
                      {
                        i = j;
                        if (paramAnonymous3ChannelParticipant2.status != null)
                        {
                          if (paramAnonymous3ChannelParticipant2.id != UserConfig.getClientUserId()) {
                            break label128;
                          }
                          i = ConnectionsManager.getInstance().getCurrentTime() + 50000;
                        }
                      }
                      j = k;
                      if (paramAnonymous3ChannelParticipant1 != null)
                      {
                        j = k;
                        if (paramAnonymous3ChannelParticipant1.status != null)
                        {
                          if (paramAnonymous3ChannelParticipant1.id != UserConfig.getClientUserId()) {
                            break label139;
                          }
                          j = ConnectionsManager.getInstance().getCurrentTime() + 50000;
                        }
                      }
                      label111:
                      if ((i > 0) && (j > 0)) {
                        if (i <= j) {}
                      }
                      label128:
                      label139:
                      label186:
                      do
                      {
                        do
                        {
                          return 1;
                          i = paramAnonymous3ChannelParticipant2.status.expires;
                          break;
                          j = paramAnonymous3ChannelParticipant1.status.expires;
                          break label111;
                          if (i < j) {
                            return -1;
                          }
                          return 0;
                          if ((i >= 0) || (j >= 0)) {
                            break label186;
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
                      return 0;
                    }
                  });
                }
                catch (Exception localException)
                {
                  FileLog.e("tmessages", localException);
                  continue;
                }
                ChannelUsersActivity.access$1002(ChannelUsersActivity.this, false);
                ChannelUsersActivity.access$1102(ChannelUsersActivity.this, true);
                if (ChannelUsersActivity.this.emptyView != null) {
                  ChannelUsersActivity.this.emptyView.showTextView();
                }
                if (ChannelUsersActivity.this.listViewAdapter != null) {
                  ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                }
                return;
                if (ChannelUsersActivity.this.type == 1) {
                  Collections.sort(localTL_channels_channelParticipants.participants, new Comparator()
                  {
                    public int compare(TLRPC.ChannelParticipant paramAnonymous3ChannelParticipant1, TLRPC.ChannelParticipant paramAnonymous3ChannelParticipant2)
                    {
                      int i = ChannelUsersActivity.this.getChannelAdminParticipantType(paramAnonymous3ChannelParticipant1);
                      int j = ChannelUsersActivity.this.getChannelAdminParticipantType(paramAnonymous3ChannelParticipant2);
                      if (i > j) {
                        return 1;
                      }
                      if (i < j) {
                        return -1;
                      }
                      return 0;
                    }
                  });
                }
              }
            }
          });
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, this.classGuid);
      return;
      if (this.type == 1) {
        localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsAdmins();
      } else if (this.type == 2) {
        localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    int i = 1;
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    if (this.type == 0)
    {
      this.actionBar.setTitle(LocaleController.getString("ChannelBlockedUsers", 2131165414));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            ChannelUsersActivity.this.finishFragment();
          }
        }
      });
      this.actionBar.createMenu();
      this.fragmentView = new FrameLayout(paramContext);
      this.fragmentView.setBackgroundColor(-986896);
      FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
      this.emptyView = new EmptyTextProgressView(paramContext);
      if (this.type == 0)
      {
        if (!this.isMegagroup) {
          break label355;
        }
        this.emptyView.setText(LocaleController.getString("NoBlockedGroup", 2131165929));
      }
      label135:
      localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
      final ListView localListView = new ListView(paramContext);
      localListView.setEmptyView(this.emptyView);
      localListView.setDivider(null);
      localListView.setDividerHeight(0);
      localListView.setDrawSelectorOnTop(true);
      paramContext = new ListAdapter(paramContext);
      this.listViewAdapter = paramContext;
      localListView.setAdapter(paramContext);
      if (!LocaleController.isRTL) {
        break label374;
      }
      label214:
      localListView.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(localListView, LayoutHelper.createFrame(-1, -1.0F));
      localListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if (ChannelUsersActivity.this.type == 2) {
            if (ChannelUsersActivity.this.isAdmin)
            {
              if (paramAnonymousInt != 0) {
                break label212;
              }
              paramAnonymousAdapterView = new Bundle();
              paramAnonymousAdapterView.putBoolean("onlyUsers", true);
              paramAnonymousAdapterView.putBoolean("destroyAfterSelect", true);
              paramAnonymousAdapterView.putBoolean("returnAsResult", true);
              paramAnonymousAdapterView.putBoolean("needForwardCount", false);
              paramAnonymousAdapterView.putBoolean("allowUsernameSearch", false);
              paramAnonymousAdapterView.putString("selectAlertString", LocaleController.getString("ChannelAddTo", 2131165404));
              paramAnonymousAdapterView = new ContactsActivity(paramAnonymousAdapterView);
              paramAnonymousAdapterView.setDelegate(new ContactsActivity.ContactsActivityDelegate()
              {
                public void didSelectContact(TLRPC.User paramAnonymous2User, String paramAnonymous2String)
                {
                  MessagesController localMessagesController = MessagesController.getInstance();
                  int j = ChannelUsersActivity.this.chatId;
                  if (paramAnonymous2String != null) {}
                  for (int i = Utilities.parseInt(paramAnonymous2String).intValue();; i = 0)
                  {
                    localMessagesController.addUserToChat(j, paramAnonymous2User, null, i, null, ChannelUsersActivity.this);
                    return;
                  }
                }
              });
              ChannelUsersActivity.this.presentFragment(paramAnonymousAdapterView);
            }
          }
          label212:
          label337:
          label370:
          label480:
          label482:
          do
          {
            do
            {
              for (;;)
              {
                paramAnonymousView = null;
                paramAnonymousAdapterView = paramAnonymousView;
                if (paramAnonymousInt >= ChannelUsersActivity.this.participantsStartRow)
                {
                  paramAnonymousAdapterView = paramAnonymousView;
                  if (paramAnonymousInt < ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) {
                    paramAnonymousAdapterView = (TLRPC.ChannelParticipant)ChannelUsersActivity.this.participants.get(paramAnonymousInt - ChannelUsersActivity.this.participantsStartRow);
                  }
                }
                if (paramAnonymousAdapterView != null)
                {
                  paramAnonymousView = new Bundle();
                  paramAnonymousView.putInt("user_id", paramAnonymousAdapterView.user_id);
                  ChannelUsersActivity.this.presentFragment(new ProfileActivity(paramAnonymousView));
                }
                return;
                if ((!ChannelUsersActivity.this.isPublic) && (paramAnonymousInt == 1)) {
                  ChannelUsersActivity.this.presentFragment(new GroupInviteActivity(ChannelUsersActivity.this.chatId));
                }
              }
            } while ((ChannelUsersActivity.this.type != 1) || (!ChannelUsersActivity.this.isAdmin));
            if ((ChannelUsersActivity.this.isMegagroup) && ((paramAnonymousInt == 1) || (paramAnonymousInt == 2)))
            {
              paramAnonymousAdapterView = MessagesController.getInstance().getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
              if (paramAnonymousAdapterView == null) {
                break;
              }
              int j = 0;
              int i;
              if ((paramAnonymousInt == 1) && (!paramAnonymousAdapterView.democracy))
              {
                paramAnonymousAdapterView.democracy = true;
                i = 1;
                if (i == 0) {
                  break label480;
                }
                MessagesController.getInstance().toogleChannelInvites(ChannelUsersActivity.this.chatId, paramAnonymousAdapterView.democracy);
                i = localListView.getChildCount();
                paramAnonymousInt = 0;
                if (paramAnonymousInt < i)
                {
                  paramAnonymousView = localListView.getChildAt(paramAnonymousInt);
                  if ((paramAnonymousView instanceof RadioCell))
                  {
                    j = ((Integer)paramAnonymousView.getTag()).intValue();
                    paramAnonymousView = (RadioCell)paramAnonymousView;
                    if (((j != 0) || (!paramAnonymousAdapterView.democracy)) && ((j != 1) || (paramAnonymousAdapterView.democracy))) {
                      break label482;
                    }
                  }
                }
              }
              for (boolean bool = true;; bool = false)
              {
                paramAnonymousView.setChecked(bool, true);
                paramAnonymousInt += 1;
                break label370;
                break;
                i = j;
                if (paramAnonymousInt != 2) {
                  break label337;
                }
                i = j;
                if (!paramAnonymousAdapterView.democracy) {
                  break label337;
                }
                paramAnonymousAdapterView.democracy = false;
                i = 1;
                break label337;
                break;
              }
            }
          } while (paramAnonymousInt != ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size());
          paramAnonymousAdapterView = new Bundle();
          paramAnonymousAdapterView.putBoolean("onlyUsers", true);
          paramAnonymousAdapterView.putBoolean("destroyAfterSelect", true);
          paramAnonymousAdapterView.putBoolean("returnAsResult", true);
          paramAnonymousAdapterView.putBoolean("needForwardCount", false);
          paramAnonymousAdapterView.putBoolean("allowUsernameSearch", true);
          paramAnonymousAdapterView.putString("selectAlertString", LocaleController.getString("ChannelAddUserAdminAlert", 2131165405));
          paramAnonymousAdapterView = new ContactsActivity(paramAnonymousAdapterView);
          paramAnonymousAdapterView.setDelegate(new ContactsActivity.ContactsActivityDelegate()
          {
            public void didSelectContact(TLRPC.User paramAnonymous2User, String paramAnonymous2String)
            {
              ChannelUsersActivity.this.setUserChannelRole(paramAnonymous2User, new TLRPC.TL_channelRoleEditor());
            }
          });
          ChannelUsersActivity.this.presentFragment(paramAnonymousAdapterView);
        }
      });
      if ((this.isAdmin) || ((this.isMegagroup) && (this.type == 0))) {
        localListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
          public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, final View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
          {
            if (ChannelUsersActivity.this.getParentActivity() == null) {}
            do
            {
              return false;
              paramAnonymousAdapterView = null;
              paramAnonymousView = paramAnonymousAdapterView;
              if (paramAnonymousInt >= ChannelUsersActivity.this.participantsStartRow)
              {
                paramAnonymousView = paramAnonymousAdapterView;
                if (paramAnonymousInt < ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) {
                  paramAnonymousView = (TLRPC.ChannelParticipant)ChannelUsersActivity.this.participants.get(paramAnonymousInt - ChannelUsersActivity.this.participantsStartRow);
                }
              }
            } while (paramAnonymousView == null);
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChannelUsersActivity.this.getParentActivity());
            paramAnonymousAdapterView = null;
            if (ChannelUsersActivity.this.type == 0)
            {
              paramAnonymousAdapterView = new CharSequence[1];
              paramAnonymousAdapterView[0] = LocaleController.getString("Unblock", 2131166349);
            }
            for (;;)
            {
              localBuilder.setItems(paramAnonymousAdapterView, new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  if (paramAnonymous2Int == 0)
                  {
                    if (ChannelUsersActivity.this.type != 0) {
                      break label109;
                    }
                    ChannelUsersActivity.this.participants.remove(paramAnonymousView);
                    ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    paramAnonymous2DialogInterface = new TLRPC.TL_channels_kickFromChannel();
                    paramAnonymous2DialogInterface.kicked = false;
                    paramAnonymous2DialogInterface.user_id = MessagesController.getInputUser(paramAnonymousView.user_id);
                    paramAnonymous2DialogInterface.channel = MessagesController.getInputChannel(ChannelUsersActivity.this.chatId);
                    ConnectionsManager.getInstance().sendRequest(paramAnonymous2DialogInterface, new RequestDelegate()
                    {
                      public void run(final TLObject paramAnonymous3TLObject, TLRPC.TL_error paramAnonymous3TL_error)
                      {
                        if (paramAnonymous3TLObject != null)
                        {
                          paramAnonymous3TLObject = (TLRPC.Updates)paramAnonymous3TLObject;
                          MessagesController.getInstance().processUpdates(paramAnonymous3TLObject, false);
                          if (!paramAnonymous3TLObject.chats.isEmpty()) {
                            AndroidUtilities.runOnUIThread(new Runnable()
                            {
                              public void run()
                              {
                                TLRPC.Chat localChat = (TLRPC.Chat)paramAnonymous3TLObject.chats.get(0);
                                MessagesController.getInstance().loadFullChat(localChat.id, 0, true);
                              }
                            }, 1000L);
                          }
                        }
                      }
                    });
                  }
                  label109:
                  do
                  {
                    return;
                    if (ChannelUsersActivity.this.type == 1)
                    {
                      ChannelUsersActivity.this.setUserChannelRole(MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousView.user_id)), new TLRPC.TL_channelRoleEmpty());
                      return;
                    }
                  } while (ChannelUsersActivity.this.type != 2);
                  MessagesController.getInstance().deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousView.user_id)), null);
                }
              });
              ChannelUsersActivity.this.showDialog(localBuilder.create());
              return true;
              if (ChannelUsersActivity.this.type == 1)
              {
                paramAnonymousAdapterView = new CharSequence[1];
                paramAnonymousAdapterView[0] = LocaleController.getString("ChannelRemoveUserAdmin", 2131165472);
              }
              else if (ChannelUsersActivity.this.type == 2)
              {
                paramAnonymousAdapterView = new CharSequence[1];
                paramAnonymousAdapterView[0] = LocaleController.getString("ChannelRemoveUser", 2131165471);
              }
            }
          }
        });
      }
      if (!this.loadingUsers) {
        break label379;
      }
      this.emptyView.showProgress();
    }
    for (;;)
    {
      return this.fragmentView;
      if (this.type == 1)
      {
        this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", 2131165409));
        break;
      }
      if (this.type != 2) {
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("ChannelMembers", 2131165436));
      break;
      label355:
      this.emptyView.setText(LocaleController.getString("NoBlocked", 2131165928));
      break label135;
      label374:
      i = 2;
      break label214;
      label379:
      this.emptyView.showTextView();
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if ((paramInt == NotificationCenter.chatInfoDidLoaded) && (((TLRPC.ChatFull)paramVarArgs[0]).id == this.chatId)) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          ChannelUsersActivity.this.getChannelParticipants(0, 200);
        }
      });
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
    getChannelParticipants(0, 200);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
  
  public void setUserChannelRole(TLRPC.User paramUser, TLRPC.ChannelParticipantRole paramChannelParticipantRole)
  {
    if ((paramUser == null) || (paramChannelParticipantRole == null)) {
      return;
    }
    TLRPC.TL_channels_editAdmin localTL_channels_editAdmin = new TLRPC.TL_channels_editAdmin();
    localTL_channels_editAdmin.channel = MessagesController.getInputChannel(this.chatId);
    localTL_channels_editAdmin.user_id = MessagesController.getInputUser(paramUser);
    localTL_channels_editAdmin.role = paramChannelParticipantRole;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_editAdmin, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          MessagesController.getInstance().processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.getInstance().loadFullChat(ChannelUsersActivity.this.chatId, 0, true);
            }
          }, 1000L);
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            String str = paramAnonymousTL_error.text;
            ChannelUsersActivity localChannelUsersActivity = ChannelUsersActivity.this;
            if (!ChannelUsersActivity.this.isMegagroup) {}
            for (boolean bool = true;; bool = false)
            {
              AlertsCreator.showAddUserAlert(str, localChannelUsersActivity, bool);
              return;
            }
          }
        });
      }
    });
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
      int i = 1;
      if (((ChannelUsersActivity.this.participants.isEmpty()) && (ChannelUsersActivity.this.type == 0)) || ((ChannelUsersActivity.this.loadingUsers) && (!ChannelUsersActivity.this.firstLoaded))) {
        return 0;
      }
      if (ChannelUsersActivity.this.type == 1)
      {
        int k = ChannelUsersActivity.this.participants.size();
        if (ChannelUsersActivity.this.isAdmin) {
          i = 2;
        }
        if ((ChannelUsersActivity.this.isAdmin) && (ChannelUsersActivity.this.isMegagroup)) {}
        for (int j = 4;; j = 0) {
          return k + i + j;
        }
      }
      return ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow + 1;
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
      int i = 3;
      if (ChannelUsersActivity.this.type == 1)
      {
        if (ChannelUsersActivity.this.isAdmin)
        {
          if (ChannelUsersActivity.this.isMegagroup)
          {
            if (paramInt == 0) {
              i = 5;
            }
            do
            {
              return i;
              if ((paramInt == 1) || (paramInt == 2)) {
                return 6;
              }
            } while (paramInt == 3);
          }
          if (paramInt == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
            return 4;
          }
          if (paramInt == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size() + 1) {
            return 1;
          }
        }
      }
      else if ((ChannelUsersActivity.this.type == 2) && (ChannelUsersActivity.this.isAdmin)) {
        if (!ChannelUsersActivity.this.isPublic)
        {
          if ((paramInt == 0) || (paramInt == 1)) {
            return 2;
          }
          if (paramInt == 2) {
            return 1;
          }
        }
        else
        {
          if (paramInt == 0) {
            return 2;
          }
          if (paramInt == 1) {
            return 1;
          }
        }
      }
      if (paramInt == ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) {
        return 1;
      }
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      Object localObject1;
      Object localObject2;
      TLRPC.ChannelParticipant localChannelParticipant;
      TLRPC.User localUser;
      if (i == 0)
      {
        localObject1 = paramView;
        if (paramView == null)
        {
          localObject1 = new UserCell(this.mContext, 1, 0, false);
          ((View)localObject1).setBackgroundColor(-1);
        }
        localObject2 = (UserCell)localObject1;
        localChannelParticipant = (TLRPC.ChannelParticipant)ChannelUsersActivity.this.participants.get(paramInt - ChannelUsersActivity.this.participantsStartRow);
        localUser = MessagesController.getInstance().getUser(Integer.valueOf(localChannelParticipant.user_id));
        paramViewGroup = (ViewGroup)localObject1;
        if (localUser != null)
        {
          if (ChannelUsersActivity.this.type != 0) {
            break label181;
          }
          if ((localUser.phone == null) || (localUser.phone.length() == 0)) {
            break label170;
          }
          paramView = PhoneFormat.getInstance().format("+" + localUser.phone);
          ((UserCell)localObject2).setData(localUser, null, paramView, 0);
          paramViewGroup = (ViewGroup)localObject1;
        }
      }
      label170:
      label181:
      label592:
      label683:
      label714:
      label738:
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
                    return paramViewGroup;
                    paramView = LocaleController.getString("NumberUnknown", 2131166043);
                    break;
                    if (ChannelUsersActivity.this.type == 1)
                    {
                      paramView = null;
                      if (((localChannelParticipant instanceof TLRPC.TL_channelParticipantCreator)) || ((localChannelParticipant instanceof TLRPC.TL_channelParticipantSelf))) {
                        paramView = LocaleController.getString("ChannelCreator", 2131165420);
                      }
                      for (;;)
                      {
                        ((UserCell)localObject2).setData(localUser, null, paramView, 0);
                        return (View)localObject1;
                        if ((localChannelParticipant instanceof TLRPC.TL_channelParticipantModerator)) {
                          paramView = LocaleController.getString("ChannelModerator", 2131165460);
                        } else if ((localChannelParticipant instanceof TLRPC.TL_channelParticipantEditor)) {
                          paramView = LocaleController.getString("ChannelEditor", 2131165426);
                        }
                      }
                    }
                    paramViewGroup = (ViewGroup)localObject1;
                  } while (ChannelUsersActivity.this.type != 2);
                  ((UserCell)localObject2).setData(localUser, null, null, 0);
                  return (View)localObject1;
                  if (i != 1) {
                    break label592;
                  }
                  localObject1 = paramView;
                  if (paramView == null) {
                    localObject1 = new TextInfoPrivacyCell(this.mContext);
                  }
                  if (ChannelUsersActivity.this.type == 0)
                  {
                    ((TextInfoPrivacyCell)localObject1).setText(String.format("%1$s\n\n%2$s", new Object[] { LocaleController.getString("NoBlockedGroup", 2131165929), LocaleController.getString("UnblockText", 2131166350) }));
                    ((View)localObject1).setBackgroundResource(2130837689);
                    return (View)localObject1;
                  }
                  if (ChannelUsersActivity.this.type == 1)
                  {
                    if (ChannelUsersActivity.this.isAdmin)
                    {
                      if (ChannelUsersActivity.this.isMegagroup)
                      {
                        ((TextInfoPrivacyCell)localObject1).setText(LocaleController.getString("MegaAdminsInfo", 2131165853));
                        ((View)localObject1).setBackgroundResource(2130837689);
                        return (View)localObject1;
                      }
                      ((TextInfoPrivacyCell)localObject1).setText(LocaleController.getString("ChannelAdminsInfo", 2131165410));
                      ((View)localObject1).setBackgroundResource(2130837689);
                      return (View)localObject1;
                    }
                    ((TextInfoPrivacyCell)localObject1).setText("");
                    ((View)localObject1).setBackgroundResource(2130837689);
                    return (View)localObject1;
                  }
                  paramViewGroup = (ViewGroup)localObject1;
                } while (ChannelUsersActivity.this.type != 2);
                if (((!ChannelUsersActivity.this.isPublic) && (paramInt == 2)) || ((paramInt == 1) && (ChannelUsersActivity.this.isAdmin)))
                {
                  if (ChannelUsersActivity.this.isMegagroup) {
                    ((TextInfoPrivacyCell)localObject1).setText("");
                  }
                  for (;;)
                  {
                    ((View)localObject1).setBackgroundResource(2130837688);
                    return (View)localObject1;
                    ((TextInfoPrivacyCell)localObject1).setText(LocaleController.getString("ChannelMembersInfo", 2131165437));
                  }
                }
                ((TextInfoPrivacyCell)localObject1).setText("");
                ((View)localObject1).setBackgroundResource(2130837689);
                return (View)localObject1;
                if (i != 2) {
                  break label714;
                }
                localObject1 = paramView;
                if (paramView == null)
                {
                  localObject1 = new TextSettingsCell(this.mContext);
                  ((View)localObject1).setBackgroundColor(-1);
                }
                paramView = (TextSettingsCell)localObject1;
                if (ChannelUsersActivity.this.type != 2) {
                  break label683;
                }
                if (paramInt == 0)
                {
                  paramView.setText(LocaleController.getString("AddMember", 2131165262), true);
                  return (View)localObject1;
                }
                paramViewGroup = (ViewGroup)localObject1;
              } while (paramInt != 1);
              paramView.setText(LocaleController.getString("ChannelInviteViaLink", 2131165428), false);
              return (View)localObject1;
              paramViewGroup = (ViewGroup)localObject1;
            } while (ChannelUsersActivity.this.type != 1);
            paramView.setTextAndIcon(LocaleController.getString("ChannelAddAdmin", 2131165402), 2130837810, false);
            return (View)localObject1;
            if (i != 3) {
              break label738;
            }
            paramViewGroup = paramView;
          } while (paramView != null);
          return new ShadowSectionCell(this.mContext);
          if (i == 4)
          {
            paramViewGroup = paramView;
            if (paramView == null)
            {
              paramViewGroup = new TextCell(this.mContext);
              paramViewGroup.setBackgroundColor(-1);
            }
            ((TextCell)paramViewGroup).setTextAndIcon(LocaleController.getString("ChannelAddAdmin", 2131165402), 2130837810);
            return paramViewGroup;
          }
          if (i == 5)
          {
            paramViewGroup = paramView;
            if (paramView == null)
            {
              paramViewGroup = new HeaderCell(this.mContext);
              paramViewGroup.setBackgroundColor(-1);
            }
            ((HeaderCell)paramViewGroup).setText(LocaleController.getString("WhoCanAddMembers", 2131166400));
            return paramViewGroup;
          }
          paramViewGroup = paramView;
        } while (i != 6);
        localObject1 = paramView;
        if (paramView == null)
        {
          localObject1 = new RadioCell(this.mContext);
          ((View)localObject1).setBackgroundColor(-1);
        }
        paramView = (RadioCell)localObject1;
        localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
        if (paramInt == 1)
        {
          paramView.setTag(Integer.valueOf(0));
          paramViewGroup = LocaleController.getString("WhoCanAddMembersAllMembers", 2131166402);
          if ((localObject2 != null) && (((TLRPC.Chat)localObject2).democracy)) {}
          for (bool = true;; bool = false)
          {
            paramView.setText(paramViewGroup, bool, true);
            return (View)localObject1;
          }
        }
        paramViewGroup = (ViewGroup)localObject1;
      } while (paramInt != 2);
      paramView.setTag(Integer.valueOf(1));
      paramViewGroup = LocaleController.getString("WhoCanAddMembersAdmins", 2131166401);
      if ((localObject2 != null) && (!((TLRPC.Chat)localObject2).democracy)) {}
      for (boolean bool = true;; bool = false)
      {
        paramView.setText(paramViewGroup, bool, false);
        return (View)localObject1;
      }
    }
    
    public int getViewTypeCount()
    {
      return 7;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return (getCount() == 0) || ((ChannelUsersActivity.this.participants.isEmpty()) && (ChannelUsersActivity.this.loadingUsers));
    }
    
    public boolean isEnabled(int paramInt)
    {
      boolean bool1 = false;
      boolean bool2 = true;
      if (ChannelUsersActivity.this.type == 2) {
        if (ChannelUsersActivity.this.isAdmin)
        {
          if (!ChannelUsersActivity.this.isPublic)
          {
            if ((paramInt == 0) || (paramInt == 1)) {
              bool1 = true;
            }
            while (paramInt == 2) {
              return bool1;
            }
          }
        }
        else {
          label53:
          if ((paramInt == ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) || (((TLRPC.ChannelParticipant)ChannelUsersActivity.this.participants.get(paramInt - ChannelUsersActivity.this.participantsStartRow)).user_id == UserConfig.getClientUserId())) {
            break label232;
          }
        }
      }
      label232:
      for (bool1 = bool2;; bool1 = false)
      {
        return bool1;
        if (paramInt == 0) {
          return true;
        }
        if (paramInt != 1) {
          break label53;
        }
        return false;
        if (ChannelUsersActivity.this.type != 1) {
          break label53;
        }
        if (paramInt == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
          return ChannelUsersActivity.this.isAdmin;
        }
        if (paramInt == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size() + 1) {
          break;
        }
        if ((!ChannelUsersActivity.this.isMegagroup) || (!ChannelUsersActivity.this.isAdmin) || (paramInt >= 4)) {
          break label53;
        }
        if ((paramInt == 1) || (paramInt == 2)) {}
        for (bool1 = true;; bool1 = false) {
          return bool1;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChannelUsersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */