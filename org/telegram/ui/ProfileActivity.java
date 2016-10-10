package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.query.BotQuery;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantEditor;
import org.telegram.tgnet.TLRPC.TL_channelParticipantModerator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channelRoleEditor;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import org.telegram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Cells.AboutLinkCell.AboutLinkCellDelegate;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

public class ProfileActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, PhotoViewer.PhotoViewerProvider
{
  private static final int add_contact = 1;
  private static final int add_shortcut = 14;
  private static final int block_contact = 2;
  private static final int convert_to_supergroup = 13;
  private static final int delete_contact = 5;
  private static final int edit_channel = 12;
  private static final int edit_contact = 4;
  private static final int edit_name = 8;
  private static final int invite_to_group = 9;
  private static final int leave_group = 7;
  private static final int set_admins = 11;
  private static final int share = 10;
  private static final int share_contact = 3;
  private int addMemberRow;
  private boolean allowProfileAnimation = true;
  private ActionBarMenuItem animatingItem;
  private float animationProgress;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater;
  private int blockedUsersRow;
  private TLRPC.BotInfo botInfo;
  private int channelInfoRow;
  private int channelNameRow;
  private int chat_id;
  private int convertHelpRow;
  private int convertRow;
  private boolean creatingChat;
  private TLRPC.Chat currentChat;
  private TLRPC.EncryptedChat currentEncryptedChat;
  private long dialog_id;
  private int emptyRow;
  private int emptyRowChat;
  private int emptyRowChat2;
  private int extraHeight;
  private TLRPC.ChatFull info;
  private int initialAnimationExtraHeight;
  private LinearLayoutManager layoutManager;
  private int leaveChannelRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int loadMoreMembersRow;
  private boolean loadingUsers;
  private int managementRow;
  private int membersEndRow;
  private int membersRow;
  private int membersSectionRow;
  private long mergeDialogId;
  private SimpleTextView[] nameTextView = new SimpleTextView[2];
  private int onlineCount = -1;
  private SimpleTextView[] onlineTextView = new SimpleTextView[2];
  private boolean openAnimationInProgress;
  private HashMap<Integer, TLRPC.ChatParticipant> participantsMap = new HashMap();
  private int phoneRow;
  private boolean playProfileAnimation;
  private int rowCount = 0;
  private int sectionRow;
  private int selectedUser;
  private int settingsKeyRow;
  private int settingsNotificationsRow;
  private int settingsTimerRow;
  private int sharedMediaRow;
  private ArrayList<Integer> sortedUsers;
  private int startSecretChatRow;
  private TopView topView;
  private int totalMediaCount = -1;
  private int totalMediaCountMerge = -1;
  private boolean userBlocked;
  private int userInfoRow;
  private int userSectionRow;
  private int user_id;
  private int usernameRow;
  private boolean usersEndReached;
  private ImageView writeButton;
  private AnimatorSet writeButtonAnimation;
  
  public ProfileActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private void checkListViewScroll()
  {
    boolean bool = false;
    if ((this.listView.getChildCount() <= 0) || (this.openAnimationInProgress)) {}
    int i;
    do
    {
      return;
      View localView = this.listView.getChildAt(0);
      ProfileActivity.ListAdapter.Holder localHolder = (ProfileActivity.ListAdapter.Holder)this.listView.findContainingViewHolder(localView);
      int j = localView.getTop();
      int k = 0;
      i = k;
      if (j >= 0)
      {
        i = k;
        if (localHolder != null)
        {
          i = k;
          if (localHolder.getAdapterPosition() == 0) {
            i = j;
          }
        }
      }
    } while (this.extraHeight == i);
    this.extraHeight = i;
    this.topView.invalidate();
    if (this.playProfileAnimation)
    {
      if (this.extraHeight != 0) {
        bool = true;
      }
      this.allowProfileAnimation = bool;
    }
    needLayout();
  }
  
  private void createActionBarMenu()
  {
    ActionBarMenu localActionBarMenu = this.actionBar.createMenu();
    localActionBarMenu.clearItems();
    this.animatingItem = null;
    Object localObject3 = null;
    Object localObject1 = null;
    Object localObject2;
    if (this.user_id != 0) {
      if (UserConfig.getClientUserId() != this.user_id) {
        if (ContactsController.getInstance().contactsDict.get(this.user_id) == null)
        {
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
          if (localObject1 == null) {
            return;
          }
          localObject2 = localActionBarMenu.addItem(10, 2130837708);
          if (((TLRPC.User)localObject1).bot)
          {
            if (!((TLRPC.User)localObject1).bot_nochats) {
              ((ActionBarMenuItem)localObject2).addSubItem(9, LocaleController.getString("BotInvite", 2131165369), 0);
            }
            ((ActionBarMenuItem)localObject2).addSubItem(10, LocaleController.getString("BotShare", 2131165373), 0);
          }
          if ((((TLRPC.User)localObject1).phone != null) && (((TLRPC.User)localObject1).phone.length() != 0))
          {
            ((ActionBarMenuItem)localObject2).addSubItem(1, LocaleController.getString("AddContact", 2131165256), 0);
            ((ActionBarMenuItem)localObject2).addSubItem(3, LocaleController.getString("ShareContact", 2131166273), 0);
            if (!this.userBlocked)
            {
              localObject1 = LocaleController.getString("BlockContact", 2131165359);
              ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject1, 0);
              localObject1 = localObject2;
            }
          }
        }
      }
    }
    for (;;)
    {
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = localActionBarMenu.addItem(10, 2130837708);
      }
      ((ActionBarMenuItem)localObject2).addSubItem(14, LocaleController.getString("AddShortcut", 2131165265), 0);
      return;
      localObject1 = LocaleController.getString("Unblock", 2131166349);
      break;
      if (((TLRPC.User)localObject1).bot)
      {
        if (!this.userBlocked) {}
        for (localObject1 = LocaleController.getString("BotStop", 2131165377);; localObject1 = LocaleController.getString("BotRestart", 2131165371))
        {
          ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject1, 0);
          localObject1 = localObject2;
          break;
        }
      }
      if (!this.userBlocked) {}
      for (localObject1 = LocaleController.getString("BlockContact", 2131165359);; localObject1 = LocaleController.getString("Unblock", 2131166349))
      {
        ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject1, 0);
        localObject1 = localObject2;
        break;
      }
      localObject2 = localActionBarMenu.addItem(10, 2130837708);
      ((ActionBarMenuItem)localObject2).addSubItem(3, LocaleController.getString("ShareContact", 2131166273), 0);
      if (!this.userBlocked) {}
      for (localObject1 = LocaleController.getString("BlockContact", 2131165359);; localObject1 = LocaleController.getString("Unblock", 2131166349))
      {
        ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject1, 0);
        ((ActionBarMenuItem)localObject2).addSubItem(4, LocaleController.getString("EditContact", 2131165593), 0);
        ((ActionBarMenuItem)localObject2).addSubItem(5, LocaleController.getString("DeleteContact", 2131165571), 0);
        localObject1 = localObject2;
        break;
      }
      localObject1 = localActionBarMenu.addItem(10, 2130837708);
      ((ActionBarMenuItem)localObject1).addSubItem(3, LocaleController.getString("ShareContact", 2131166273), 0);
      continue;
      if (this.chat_id != 0) {
        if (this.chat_id > 0)
        {
          TLRPC.Chat localChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
          if (this.writeButton != null)
          {
            boolean bool = ChatObject.isChannel(this.currentChat);
            if (((!bool) || (this.currentChat.creator) || ((this.currentChat.megagroup) && (this.currentChat.editor))) && ((bool) || (this.currentChat.admin) || (this.currentChat.creator) || (!this.currentChat.admins_enabled))) {
              break label765;
            }
            this.writeButton.setImageResource(2130837681);
            this.writeButton.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
          }
          for (;;)
          {
            if (!ChatObject.isChannel(localChat)) {
              break label789;
            }
            if (!localChat.creator)
            {
              localObject2 = localObject3;
              if (localChat.megagroup)
              {
                localObject2 = localObject3;
                if (!localChat.editor) {}
              }
            }
            else
            {
              localObject2 = localActionBarMenu.addItem(10, 2130837708);
              ((ActionBarMenuItem)localObject2).addSubItem(12, LocaleController.getString("ChannelEdit", 2131165425), 0);
            }
            localObject1 = localObject2;
            if (localChat.creator) {
              break;
            }
            localObject1 = localObject2;
            if (localChat.left) {
              break;
            }
            localObject1 = localObject2;
            if (localChat.kicked) {
              break;
            }
            localObject1 = localObject2;
            if (!localChat.megagroup) {
              break;
            }
            localObject1 = localObject2;
            if (localObject2 == null) {
              localObject1 = localActionBarMenu.addItem(10, 2130837708);
            }
            ((ActionBarMenuItem)localObject1).addSubItem(7, LocaleController.getString("LeaveMegaMenu", 2131165818), 0);
            break;
            label765:
            this.writeButton.setImageResource(2130837679);
            this.writeButton.setPadding(0, 0, 0, 0);
          }
          label789:
          localObject1 = localActionBarMenu.addItem(10, 2130837708);
          if ((localChat.creator) && (this.chat_id > 0)) {
            ((ActionBarMenuItem)localObject1).addSubItem(11, LocaleController.getString("SetAdmins", 2131166262), 0);
          }
          if ((!localChat.admins_enabled) || (localChat.creator) || (localChat.admin)) {
            ((ActionBarMenuItem)localObject1).addSubItem(8, LocaleController.getString("EditName", 2131165596), 0);
          }
          if ((localChat.creator) && ((this.info == null) || (this.info.participants.participants.size() > 1))) {
            ((ActionBarMenuItem)localObject1).addSubItem(13, LocaleController.getString("ConvertGroupMenu", 2131165530), 0);
          }
          ((ActionBarMenuItem)localObject1).addSubItem(7, LocaleController.getString("DeleteAndExit", 2131165566), 0);
        }
        else
        {
          localObject1 = localActionBarMenu.addItem(10, 2130837708);
          ((ActionBarMenuItem)localObject1).addSubItem(8, LocaleController.getString("EditName", 2131165596), 0);
        }
      }
    }
  }
  
  private void fetchUsersFromChannelInfo()
  {
    if (((this.info instanceof TLRPC.TL_channelFull)) && (this.info.participants != null))
    {
      int i = 0;
      while (i < this.info.participants.participants.size())
      {
        TLRPC.ChatParticipant localChatParticipant = (TLRPC.ChatParticipant)this.info.participants.participants.get(i);
        this.participantsMap.put(Integer.valueOf(localChatParticipant.user_id), localChatParticipant);
        i += 1;
      }
    }
  }
  
  private void fixLayout()
  {
    if (this.fragmentView == null) {
      return;
    }
    this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        if (ProfileActivity.this.fragmentView != null)
        {
          ProfileActivity.this.checkListViewScroll();
          ProfileActivity.this.needLayout();
          ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        return true;
      }
    });
  }
  
  private void getChannelParticipants(boolean paramBoolean)
  {
    int j = 0;
    if ((this.loadingUsers) || (this.participantsMap == null) || (this.info == null)) {
      return;
    }
    this.loadingUsers = true;
    final int i;
    final TLRPC.TL_channels_getParticipants localTL_channels_getParticipants;
    if ((!this.participantsMap.isEmpty()) && (paramBoolean))
    {
      i = 300;
      localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
      localTL_channels_getParticipants.channel = MessagesController.getInputChannel(this.chat_id);
      localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
      if (!paramBoolean) {
        break label135;
      }
    }
    for (;;)
    {
      localTL_channels_getParticipants.offset = j;
      localTL_channels_getParticipants.limit = 200;
      i = ConnectionsManager.getInstance().sendRequest(localTL_channels_getParticipants, new RequestDelegate()
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
                MessagesController.getInstance().putUsers(localTL_channels_channelParticipants.users, false);
                if (localTL_channels_channelParticipants.users.size() != 200) {
                  ProfileActivity.access$4502(ProfileActivity.this, true);
                }
                if (ProfileActivity.18.this.val$req.offset == 0)
                {
                  ProfileActivity.this.participantsMap.clear();
                  ProfileActivity.this.info.participants = new TLRPC.TL_chatParticipants();
                  MessagesStorage.getInstance().putUsersAndChats(localTL_channels_channelParticipants.users, null, true, true);
                  MessagesStorage.getInstance().updateChannelUsers(ProfileActivity.this.chat_id, localTL_channels_channelParticipants.participants);
                }
                int i = 0;
                while (i < localTL_channels_channelParticipants.participants.size())
                {
                  TLRPC.TL_chatChannelParticipant localTL_chatChannelParticipant = new TLRPC.TL_chatChannelParticipant();
                  localTL_chatChannelParticipant.channelParticipant = ((TLRPC.ChannelParticipant)localTL_channels_channelParticipants.participants.get(i));
                  localTL_chatChannelParticipant.inviter_id = localTL_chatChannelParticipant.channelParticipant.inviter_id;
                  localTL_chatChannelParticipant.user_id = localTL_chatChannelParticipant.channelParticipant.user_id;
                  localTL_chatChannelParticipant.date = localTL_chatChannelParticipant.channelParticipant.date;
                  if (!ProfileActivity.this.participantsMap.containsKey(Integer.valueOf(localTL_chatChannelParticipant.user_id)))
                  {
                    ProfileActivity.this.info.participants.participants.add(localTL_chatChannelParticipant);
                    ProfileActivity.this.participantsMap.put(Integer.valueOf(localTL_chatChannelParticipant.user_id), localTL_chatChannelParticipant);
                  }
                  i += 1;
                }
              }
              ProfileActivity.this.updateOnlineCount();
              ProfileActivity.access$4702(ProfileActivity.this, false);
              ProfileActivity.this.updateRowsIds();
              if (ProfileActivity.this.listAdapter != null) {
                ProfileActivity.this.listAdapter.notifyDataSetChanged();
              }
            }
          }, i);
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(i, this.classGuid);
      return;
      i = 0;
      break;
      label135:
      j = this.participantsMap.size();
    }
  }
  
  private void kickUser(int paramInt)
  {
    if (paramInt != 0)
    {
      MessagesController.getInstance().deleteUserFromChat(this.chat_id, MessagesController.getInstance().getUser(Integer.valueOf(paramInt)), this.info);
      int k;
      int j;
      int i;
      if ((this.currentChat.megagroup) && (this.info != null) && (this.info.participants != null))
      {
        k = 0;
        j = 0;
        i = k;
        if (j < this.info.participants.participants.size())
        {
          if (((TLRPC.TL_chatChannelParticipant)this.info.participants.participants.get(j)).channelParticipant.user_id != paramInt) {
            break label257;
          }
          if (this.info != null)
          {
            TLRPC.ChatFull localChatFull = this.info;
            localChatFull.participants_count -= 1;
          }
          this.info.participants.participants.remove(j);
          i = 1;
        }
        k = i;
        if (this.info != null)
        {
          k = i;
          if (this.info.participants != null) {
            j = 0;
          }
        }
      }
      for (;;)
      {
        k = i;
        if (j < this.info.participants.participants.size())
        {
          if (((TLRPC.ChatParticipant)this.info.participants.participants.get(j)).user_id == paramInt)
          {
            this.info.participants.participants.remove(j);
            k = 1;
          }
        }
        else
        {
          if (k != 0)
          {
            updateOnlineCount();
            updateRowsIds();
            this.listAdapter.notifyDataSetChanged();
          }
          return;
          label257:
          j += 1;
          break;
        }
        j += 1;
      }
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    if (AndroidUtilities.isTablet()) {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(-this.chat_id) });
    }
    for (;;)
    {
      MessagesController.getInstance().deleteUserFromChat(this.chat_id, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), this.info);
      this.playProfileAnimation = false;
      finishFragment();
      return;
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
    }
  }
  
  private void leaveChatPressed()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    String str;
    if ((ChatObject.isChannel(this.chat_id)) && (!this.currentChat.megagroup)) {
      if (ChatObject.isChannel(this.chat_id))
      {
        str = LocaleController.getString("ChannelLeaveAlert", 2131165432);
        localBuilder.setMessage(str);
      }
    }
    for (;;)
    {
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          ProfileActivity.this.kickUser(0);
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
      showDialog(localBuilder.create());
      return;
      str = LocaleController.getString("AreYouSureDeleteAndExit", 2131165318);
      break;
      localBuilder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", 2131165318));
    }
  }
  
  private void needLayout()
  {
    int i;
    Object localObject;
    float f1;
    label135:
    label177:
    int j;
    label190:
    label210:
    label334:
    label382:
    float f2;
    if (this.actionBar.getOccupyStatusBar())
    {
      i = AndroidUtilities.statusBarHeight;
      i += ActionBar.getCurrentActionBarHeight();
      if ((this.listView != null) && (!this.openAnimationInProgress))
      {
        localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
        if (((FrameLayout.LayoutParams)localObject).topMargin != i)
        {
          ((FrameLayout.LayoutParams)localObject).topMargin = i;
          this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        }
      }
      if (this.avatarImage == null) {
        return;
      }
      f1 = this.extraHeight / AndroidUtilities.dp(88.0F);
      this.listView.setTopGlowOffset(this.extraHeight);
      if (this.writeButton != null)
      {
        localObject = this.writeButton;
        if (!this.actionBar.getOccupyStatusBar()) {
          break label519;
        }
        i = AndroidUtilities.statusBarHeight;
        ((ImageView)localObject).setTranslationY(i + ActionBar.getCurrentActionBarHeight() + this.extraHeight - AndroidUtilities.dp(29.5F));
        if (!this.openAnimationInProgress)
        {
          if (f1 <= 0.2F) {
            break label525;
          }
          i = 1;
          if (this.writeButton.getTag() != null) {
            break label531;
          }
          j = 1;
          if (i != j)
          {
            if (i == 0) {
              break label537;
            }
            this.writeButton.setTag(null);
            if (this.writeButtonAnimation != null)
            {
              localObject = this.writeButtonAnimation;
              this.writeButtonAnimation = null;
              ((AnimatorSet)localObject).cancel();
            }
            this.writeButtonAnimation = new AnimatorSet();
            if (i == 0) {
              break label551;
            }
            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
            this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 1.0F }) });
            this.writeButtonAnimation.setDuration(150L);
            this.writeButtonAnimation.addListener(new AnimatorListenerAdapterProxy()
            {
              public void onAnimationEnd(Animator paramAnonymousAnimator)
              {
                if ((ProfileActivity.this.writeButtonAnimation != null) && (ProfileActivity.this.writeButtonAnimation.equals(paramAnonymousAnimator))) {
                  ProfileActivity.access$5002(ProfileActivity.this, null);
                }
              }
            });
            this.writeButtonAnimation.start();
          }
        }
      }
      if (!this.actionBar.getOccupyStatusBar()) {
        break label643;
      }
      i = AndroidUtilities.statusBarHeight;
      f2 = i + ActionBar.getCurrentActionBarHeight() / 2.0F * (1.0F + f1) - 21.0F * AndroidUtilities.density + 27.0F * AndroidUtilities.density * f1;
      this.avatarImage.setScaleX((42.0F + 18.0F * f1) / 42.0F);
      this.avatarImage.setScaleY((42.0F + 18.0F * f1) / 42.0F);
      this.avatarImage.setTranslationX(-AndroidUtilities.dp(47.0F) * f1);
      this.avatarImage.setTranslationY((float)Math.ceil(f2));
      i = 0;
      label488:
      if (i >= 2) {
        return;
      }
      if (this.nameTextView[i] != null) {
        break label649;
      }
    }
    label519:
    label525:
    label531:
    label537:
    label551:
    label643:
    label649:
    do
    {
      i += 1;
      break label488;
      i = 0;
      break;
      i = 0;
      break label135;
      i = 0;
      break label177;
      j = 0;
      break label190;
      this.writeButton.setTag(Integer.valueOf(0));
      break label210;
      this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
      this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 0.0F }) });
      break label334;
      i = 0;
      break label382;
      this.nameTextView[i].setTranslationX(-21.0F * AndroidUtilities.density * f1);
      this.nameTextView[i].setTranslationY((float)Math.floor(f2) + AndroidUtilities.dp(1.3F) + AndroidUtilities.dp(7.0F) * f1);
      this.onlineTextView[i].setTranslationX(-21.0F * AndroidUtilities.density * f1);
      this.onlineTextView[i].setTranslationY((float)Math.floor(f2) + AndroidUtilities.dp(24.0F) + (float)Math.floor(11.0F * AndroidUtilities.density) * f1);
      this.nameTextView[i].setScaleX(1.0F + 0.12F * f1);
      this.nameTextView[i].setScaleY(1.0F + 0.12F * f1);
    } while ((i != 1) || (this.openAnimationInProgress));
    if (AndroidUtilities.isTablet())
    {
      j = AndroidUtilities.dp(490.0F);
      label821:
      j = (int)(j - AndroidUtilities.dp(126.0F + 40.0F * (1.0F - f1)) - this.nameTextView[i].getTranslationX());
      float f3 = this.nameTextView[i].getPaint().measureText(this.nameTextView[i].getText().toString());
      float f4 = this.nameTextView[i].getScaleX();
      float f5 = this.nameTextView[i].getSideDrawablesSize();
      localObject = (FrameLayout.LayoutParams)this.nameTextView[i].getLayoutParams();
      if (j >= f3 * f4 + f5) {
        break label1053;
      }
    }
    label1053:
    for (((FrameLayout.LayoutParams)localObject).width = ((int)Math.ceil(j / this.nameTextView[i].getScaleX()));; ((FrameLayout.LayoutParams)localObject).width = -2)
    {
      this.nameTextView[i].setLayoutParams((ViewGroup.LayoutParams)localObject);
      localObject = (FrameLayout.LayoutParams)this.onlineTextView[i].getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).rightMargin = ((int)Math.ceil(this.onlineTextView[i].getTranslationX() + AndroidUtilities.dp(8.0F) + AndroidUtilities.dp(40.0F) * (1.0F - f1)));
      this.onlineTextView[i].setLayoutParams((ViewGroup.LayoutParams)localObject);
      break;
      j = AndroidUtilities.displaySize.x;
      break label821;
    }
  }
  
  private void openAddMember()
  {
    boolean bool = true;
    Object localObject = new Bundle();
    ((Bundle)localObject).putBoolean("onlyUsers", true);
    ((Bundle)localObject).putBoolean("destroyAfterSelect", true);
    ((Bundle)localObject).putBoolean("returnAsResult", true);
    if (!ChatObject.isChannel(this.currentChat)) {}
    HashMap localHashMap;
    for (;;)
    {
      ((Bundle)localObject).putBoolean("needForwardCount", bool);
      if (this.chat_id > 0)
      {
        if (this.currentChat.creator) {
          ((Bundle)localObject).putInt("chat_id", this.currentChat.id);
        }
        ((Bundle)localObject).putString("selectAlertString", LocaleController.getString("AddToTheGroup", 2131165272));
      }
      localObject = new ContactsActivity((Bundle)localObject);
      ((ContactsActivity)localObject).setDelegate(new ContactsActivity.ContactsActivityDelegate()
      {
        public void didSelectContact(TLRPC.User paramAnonymousUser, String paramAnonymousString)
        {
          MessagesController localMessagesController = MessagesController.getInstance();
          int j = ProfileActivity.this.chat_id;
          TLRPC.ChatFull localChatFull = ProfileActivity.this.info;
          if (paramAnonymousString != null) {}
          for (int i = Utilities.parseInt(paramAnonymousString).intValue();; i = 0)
          {
            localMessagesController.addUserToChat(j, paramAnonymousUser, localChatFull, i, null, ProfileActivity.this);
            return;
          }
        }
      });
      if ((this.info == null) || (this.info.participants == null)) {
        break label213;
      }
      localHashMap = new HashMap();
      int i = 0;
      while (i < this.info.participants.participants.size())
      {
        localHashMap.put(Integer.valueOf(((TLRPC.ChatParticipant)this.info.participants.participants.get(i)).user_id), null);
        i += 1;
      }
      bool = false;
    }
    ((ContactsActivity)localObject).setIgnoreUsers(localHashMap);
    label213:
    presentFragment((BaseFragment)localObject);
  }
  
  private boolean processOnClickOrPress(final int paramInt)
  {
    final Object localObject1;
    AlertDialog.Builder localBuilder;
    String str;
    if (paramInt == this.usernameRow)
    {
      localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      if ((localObject1 == null) || (((TLRPC.User)localObject1).username == null)) {
        return false;
      }
      localBuilder = new AlertDialog.Builder(getParentActivity());
      str = LocaleController.getString("Copy", 2131165531);
      localObject1 = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (paramAnonymousInt == 0) {}
          try
          {
            ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + localObject1.username));
            return;
          }
          catch (Exception paramAnonymousDialogInterface)
          {
            FileLog.e("tmessages", paramAnonymousDialogInterface);
          }
        }
      };
      localBuilder.setItems(new CharSequence[] { str }, (DialogInterface.OnClickListener)localObject1);
      showDialog(localBuilder.create());
      return true;
    }
    if (paramInt == this.phoneRow)
    {
      final Object localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      if ((localObject2 == null) || (((TLRPC.User)localObject2).phone == null) || (((TLRPC.User)localObject2).phone.length() == 0) || (getParentActivity() == null)) {
        return false;
      }
      localBuilder = new AlertDialog.Builder(getParentActivity());
      str = LocaleController.getString("Call", 2131165383);
      localObject1 = LocaleController.getString("Copy", 2131165531);
      localObject2 = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (paramAnonymousInt == 0) {}
          while (paramAnonymousInt != 1) {
            try
            {
              paramAnonymousDialogInterface = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + localObject2.phone));
              paramAnonymousDialogInterface.addFlags(268435456);
              ProfileActivity.this.getParentActivity().startActivityForResult(paramAnonymousDialogInterface, 500);
              return;
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              FileLog.e("tmessages", paramAnonymousDialogInterface);
              return;
            }
          }
          try
          {
            ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + localObject2.phone));
            return;
          }
          catch (Exception paramAnonymousDialogInterface)
          {
            FileLog.e("tmessages", paramAnonymousDialogInterface);
          }
        }
      };
      localBuilder.setItems(new CharSequence[] { str, localObject1 }, (DialogInterface.OnClickListener)localObject2);
      showDialog(localBuilder.create());
      return true;
    }
    if ((paramInt == this.channelInfoRow) || (paramInt == this.userInfoRow))
    {
      localBuilder = new AlertDialog.Builder(getParentActivity());
      str = LocaleController.getString("Copy", 2131165531);
      localObject1 = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          do
          {
            try
            {
              if (paramInt == ProfileActivity.this.channelInfoRow) {
                paramAnonymousDialogInterface = ProfileActivity.this.info.about;
              } else {
                paramAnonymousDialogInterface = MessagesController.getInstance().getUserAbout(ProfileActivity.this.botInfo.user_id);
              }
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              FileLog.e("tmessages", paramAnonymousDialogInterface);
              return;
            }
            ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", paramAnonymousDialogInterface));
            return;
          } while (paramAnonymousDialogInterface != null);
        }
      };
      localBuilder.setItems(new CharSequence[] { str }, (DialogInterface.OnClickListener)localObject1);
      showDialog(localBuilder.create());
      return true;
    }
    return false;
  }
  
  private void updateOnlineCount()
  {
    this.onlineCount = 0;
    int j = ConnectionsManager.getInstance().getCurrentTime();
    this.sortedUsers.clear();
    if (((this.info instanceof TLRPC.TL_chatFull)) || (((this.info instanceof TLRPC.TL_channelFull)) && (this.info.participants_count <= 200) && (this.info.participants != null)))
    {
      int i = 0;
      while (i < this.info.participants.participants.size())
      {
        Object localObject = (TLRPC.ChatParticipant)this.info.participants.participants.get(i);
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id));
        if ((localObject != null) && (((TLRPC.User)localObject).status != null) && ((((TLRPC.User)localObject).status.expires > j) || (((TLRPC.User)localObject).id == UserConfig.getClientUserId())) && (((TLRPC.User)localObject).status.expires > 10000)) {
          this.onlineCount += 1;
        }
        this.sortedUsers.add(Integer.valueOf(i));
        i += 1;
      }
    }
    try
    {
      Collections.sort(this.sortedUsers, new Comparator()
      {
        public int compare(Integer paramAnonymousInteger1, Integer paramAnonymousInteger2)
        {
          paramAnonymousInteger2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramAnonymousInteger2.intValue())).user_id));
          paramAnonymousInteger1 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramAnonymousInteger1.intValue())).user_id));
          int j = 0;
          int k = 0;
          int i = j;
          if (paramAnonymousInteger2 != null)
          {
            i = j;
            if (paramAnonymousInteger2.status != null)
            {
              if (paramAnonymousInteger2.id != UserConfig.getClientUserId()) {
                break label172;
              }
              i = ConnectionsManager.getInstance().getCurrentTime() + 50000;
            }
          }
          j = k;
          if (paramAnonymousInteger1 != null)
          {
            j = k;
            if (paramAnonymousInteger1.status != null)
            {
              if (paramAnonymousInteger1.id != UserConfig.getClientUserId()) {
                break label183;
              }
              j = ConnectionsManager.getInstance().getCurrentTime() + 50000;
            }
          }
          for (;;)
          {
            if ((i > 0) && (j > 0))
            {
              if (i > j)
              {
                return 1;
                label172:
                i = paramAnonymousInteger2.status.expires;
                break;
                label183:
                j = paramAnonymousInteger1.status.expires;
                continue;
              }
              if (i < j) {
                return -1;
              }
              return 0;
            }
          }
          if ((i < 0) && (j < 0))
          {
            if (i > j) {
              return 1;
            }
            if (i < j) {
              return -1;
            }
            return 0;
          }
          if (((i < 0) && (j > 0)) || ((i == 0) && (j != 0))) {
            return -1;
          }
          if (((j < 0) && (i > 0)) || ((j == 0) && (i != 0))) {
            return 1;
          }
          return 0;
        }
      });
      if (this.listAdapter != null) {
        this.listAdapter.notifyItemRangeChanged(this.emptyRowChat2 + 1, this.sortedUsers.size());
      }
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
  
  private void updateProfileData()
  {
    if ((this.avatarImage == null) || (this.nameTextView == null)) {}
    Object localObject4;
    int j;
    label389:
    label428:
    int i;
    label454:
    label522:
    label527:
    label537:
    label542:
    label557:
    do
    {
      return;
      if (this.user_id != 0)
      {
        localObject4 = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
        localObject1 = null;
        localObject2 = null;
        if (((TLRPC.User)localObject4).photo != null)
        {
          localObject1 = ((TLRPC.User)localObject4).photo.photo_small;
          localObject2 = ((TLRPC.User)localObject4).photo.photo_big;
        }
        this.avatarDrawable.setInfo((TLRPC.User)localObject4);
        this.avatarImage.setImage((TLObject)localObject1, "50_50", this.avatarDrawable);
        localObject3 = UserObject.getUserName((TLRPC.User)localObject4);
        if (((TLRPC.User)localObject4).id == UserConfig.getClientUserId())
        {
          localObject1 = LocaleController.getString("ChatYourSelf", 2131165498);
          localObject3 = LocaleController.getString("ChatYourSelfName", 2131165503);
        }
        for (;;)
        {
          j = 0;
          for (;;)
          {
            if (j >= 2) {
              break label557;
            }
            if (this.nameTextView[j] != null) {
              break;
            }
            j += 1;
          }
          if ((((TLRPC.User)localObject4).id == 333000) || (((TLRPC.User)localObject4).id == 777000)) {
            localObject1 = LocaleController.getString("ServiceNotifications", 2131166256);
          } else if (((TLRPC.User)localObject4).bot) {
            localObject1 = LocaleController.getString("Bot", 2131165365);
          } else {
            localObject1 = LocaleController.formatUserStatus((TLRPC.User)localObject4);
          }
        }
        int k;
        long l;
        if ((j == 0) && (((TLRPC.User)localObject4).id != UserConfig.getClientUserId()) && (((TLRPC.User)localObject4).id / 1000 != 777) && (((TLRPC.User)localObject4).id / 1000 != 333) && (((TLRPC.User)localObject4).phone != null) && (((TLRPC.User)localObject4).phone.length() != 0) && (ContactsController.getInstance().contactsDict.get(((TLRPC.User)localObject4).id) == null) && ((ContactsController.getInstance().contactsDict.size() != 0) || (!ContactsController.getInstance().isLoadingContacts())))
        {
          Object localObject5 = PhoneFormat.getInstance().format("+" + ((TLRPC.User)localObject4).phone);
          if (!this.nameTextView[j].getText().equals(localObject5)) {
            this.nameTextView[j].setText((CharSequence)localObject5);
          }
          if (!this.onlineTextView[j].getText().equals(localObject1)) {
            this.onlineTextView[j].setText((CharSequence)localObject1);
          }
          if (this.currentEncryptedChat == null) {
            break label522;
          }
          k = 2130837737;
          i = 0;
          if (j != 0) {
            break label542;
          }
          localObject5 = MessagesController.getInstance();
          if (this.dialog_id == 0L) {
            break label527;
          }
          l = this.dialog_id;
          if (!((MessagesController)localObject5).isDialogMuted(l)) {
            break label537;
          }
          i = 2130837850;
        }
        for (;;)
        {
          this.nameTextView[j].setLeftDrawable(k);
          this.nameTextView[j].setRightDrawable(i);
          break;
          if (this.nameTextView[j].getText().equals(localObject3)) {
            break label389;
          }
          this.nameTextView[j].setText((CharSequence)localObject3);
          break label389;
          k = 0;
          break label428;
          l = this.user_id;
          break label454;
          i = 0;
          continue;
          if (((TLRPC.User)localObject4).verified) {
            i = 2130837594;
          }
        }
        localObject1 = this.avatarImage.getImageReceiver();
        if (!PhotoViewer.getInstance().isShowingImage((TLRPC.FileLocation)localObject2)) {}
        for (bool = true;; bool = false)
        {
          ((ImageReceiver)localObject1).setVisible(bool, false);
          return;
        }
      }
    } while (this.chat_id == 0);
    Object localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
    if (localObject2 != null)
    {
      this.currentChat = ((TLRPC.Chat)localObject2);
      if (!ChatObject.isChannel((TLRPC.Chat)localObject2)) {
        break label946;
      }
      if ((this.info != null) && ((this.currentChat.megagroup) || ((this.info.participants_count != 0) && (!this.currentChat.admin) && (!this.info.can_view_participants)))) {
        break label784;
      }
      if (!this.currentChat.megagroup) {
        break label739;
      }
      localObject1 = LocaleController.getString("Loading", 2131165834).toLowerCase();
      label707:
      i = 0;
      label709:
      if (i >= 2) {
        break label1381;
      }
      if (this.nameTextView[i] != null) {
        break label1035;
      }
    }
    for (;;)
    {
      i += 1;
      break label709;
      localObject2 = this.currentChat;
      break;
      label739:
      if ((((TLRPC.Chat)localObject2).flags & 0x40) != 0)
      {
        localObject1 = LocaleController.getString("ChannelPublic", 2131165468).toLowerCase();
        break label707;
      }
      localObject1 = LocaleController.getString("ChannelPrivate", 2131165465).toLowerCase();
      break label707;
      label784:
      if ((this.currentChat.megagroup) && (this.info.participants_count <= 200))
      {
        if ((this.onlineCount > 1) && (this.info.participants_count != 0))
        {
          localObject1 = String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", this.info.participants_count), LocaleController.formatPluralString("Online", this.onlineCount) });
          break label707;
        }
        localObject1 = LocaleController.formatPluralString("Members", this.info.participants_count);
        break label707;
      }
      localObject1 = new int[1];
      localObject3 = LocaleController.formatShortNumber(this.info.participants_count, (int[])localObject1);
      localObject1 = LocaleController.formatPluralString("Members", localObject1[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject1[0]) }), (CharSequence)localObject3);
      break label707;
      label946:
      i = ((TLRPC.Chat)localObject2).participants_count;
      if (this.info != null) {
        i = this.info.participants.participants.size();
      }
      if ((i != 0) && (this.onlineCount > 1))
      {
        localObject1 = String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", i), LocaleController.formatPluralString("Online", this.onlineCount) });
        break label707;
      }
      localObject1 = LocaleController.formatPluralString("Members", i);
      break label707;
      label1035:
      if ((((TLRPC.Chat)localObject2).title != null) && (!this.nameTextView[i].getText().equals(((TLRPC.Chat)localObject2).title))) {
        this.nameTextView[i].setText(((TLRPC.Chat)localObject2).title);
      }
      this.nameTextView[i].setLeftDrawable(null);
      if (i != 0)
      {
        if (((TLRPC.Chat)localObject2).verified) {
          this.nameTextView[i].setRightDrawable(2130837594);
        }
        for (;;)
        {
          if ((!this.currentChat.megagroup) || (this.info == null) || (this.info.participants_count > 200) || (this.onlineCount <= 0)) {
            break label1233;
          }
          if (this.onlineTextView[i].getText().equals(localObject1)) {
            break;
          }
          this.onlineTextView[i].setText((CharSequence)localObject1);
          break;
          this.nameTextView[i].setRightDrawable(null);
        }
      }
      localObject3 = this.nameTextView[i];
      if (MessagesController.getInstance().isDialogMuted(-this.chat_id)) {}
      for (j = 2130837850;; j = 0)
      {
        ((SimpleTextView)localObject3).setRightDrawable(j);
        break;
      }
      label1233:
      if ((i == 0) && (ChatObject.isChannel(this.currentChat)) && (this.info != null) && (this.info.participants_count != 0) && ((this.currentChat.megagroup) || (this.currentChat.broadcast)))
      {
        localObject3 = new int[1];
        localObject4 = LocaleController.formatShortNumber(this.info.participants_count, (int[])localObject3);
        this.onlineTextView[i].setText(LocaleController.formatPluralString("Members", localObject3[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject3[0]) }), (CharSequence)localObject4));
      }
      else if (!this.onlineTextView[i].getText().equals(localObject1))
      {
        this.onlineTextView[i].setText((CharSequence)localObject1);
      }
    }
    label1381:
    Object localObject3 = null;
    Object localObject1 = null;
    if (((TLRPC.Chat)localObject2).photo != null)
    {
      localObject3 = ((TLRPC.Chat)localObject2).photo.photo_small;
      localObject1 = ((TLRPC.Chat)localObject2).photo.photo_big;
    }
    this.avatarDrawable.setInfo((TLRPC.Chat)localObject2);
    this.avatarImage.setImage((TLObject)localObject3, "50_50", this.avatarDrawable);
    localObject2 = this.avatarImage.getImageReceiver();
    if (!PhotoViewer.getInstance().isShowingImage((TLRPC.FileLocation)localObject1)) {}
    for (boolean bool = true;; bool = false)
    {
      ((ImageReceiver)localObject2).setVisible(bool, false);
      return;
    }
  }
  
  private void updateRowsIds()
  {
    this.emptyRow = -1;
    this.phoneRow = -1;
    this.userInfoRow = -1;
    this.userSectionRow = -1;
    this.sectionRow = -1;
    this.sharedMediaRow = -1;
    this.settingsNotificationsRow = -1;
    this.usernameRow = -1;
    this.settingsTimerRow = -1;
    this.settingsKeyRow = -1;
    this.startSecretChatRow = -1;
    this.membersEndRow = -1;
    this.emptyRowChat2 = -1;
    this.addMemberRow = -1;
    this.channelInfoRow = -1;
    this.channelNameRow = -1;
    this.convertRow = -1;
    this.convertHelpRow = -1;
    this.emptyRowChat = -1;
    this.membersSectionRow = -1;
    this.membersRow = -1;
    this.managementRow = -1;
    this.leaveChannelRow = -1;
    this.loadMoreMembersRow = -1;
    this.blockedUsersRow = -1;
    this.rowCount = 0;
    if (this.user_id != 0)
    {
      TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.emptyRow = i;
      if ((localUser == null) || (!localUser.bot))
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.phoneRow = i;
      }
      if ((localUser != null) && (localUser.username != null) && (localUser.username.length() > 0))
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.usernameRow = i;
      }
      if (MessagesController.getInstance().getUserAbout(localUser.id) != null)
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.userSectionRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.userInfoRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.sectionRow = i;
        if (this.user_id != UserConfig.getClientUserId())
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.settingsNotificationsRow = i;
        }
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.sharedMediaRow = i;
        if ((this.currentEncryptedChat instanceof TLRPC.TL_encryptedChat))
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.settingsTimerRow = i;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.settingsKeyRow = i;
        }
        if ((localUser != null) && (!localUser.bot) && (this.currentEncryptedChat == null) && (localUser.id != UserConfig.getClientUserId()))
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.startSecretChatRow = i;
        }
      }
    }
    label1409:
    do
    {
      for (;;)
      {
        return;
        this.userSectionRow = -1;
        this.userInfoRow = -1;
        break;
        if (this.chat_id != 0)
        {
          if (this.chat_id <= 0) {
            break label1409;
          }
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.emptyRow = i;
          if ((ChatObject.isChannel(this.currentChat)) && (((this.info != null) && (this.info.about != null) && (this.info.about.length() > 0)) || ((this.currentChat.username != null) && (this.currentChat.username.length() > 0))))
          {
            if ((this.info != null) && (this.info.about != null) && (this.info.about.length() > 0))
            {
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.channelInfoRow = i;
            }
            if ((this.currentChat.username != null) && (this.currentChat.username.length() > 0))
            {
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.channelNameRow = i;
            }
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.sectionRow = i;
          }
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.settingsNotificationsRow = i;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.sharedMediaRow = i;
          if (ChatObject.isChannel(this.currentChat))
          {
            if ((!this.currentChat.megagroup) && (this.info != null) && ((this.currentChat.creator) || (this.info.can_view_participants)))
            {
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.membersRow = i;
            }
            if ((!ChatObject.isNotInChat(this.currentChat)) && (!this.currentChat.megagroup) && ((this.currentChat.creator) || (this.currentChat.editor) || (this.currentChat.moderator)))
            {
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.managementRow = i;
            }
            if ((!ChatObject.isNotInChat(this.currentChat)) && (this.currentChat.megagroup) && ((this.currentChat.editor) || (this.currentChat.creator)))
            {
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.blockedUsersRow = i;
            }
            if ((!this.currentChat.creator) && (!this.currentChat.left) && (!this.currentChat.kicked) && (!this.currentChat.megagroup))
            {
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.leaveChannelRow = i;
            }
            if ((this.currentChat.megagroup) && ((this.currentChat.editor) || (this.currentChat.creator) || (this.currentChat.democracy)) && ((this.info == null) || (this.info.participants_count < MessagesController.getInstance().maxMegagroupCount)))
            {
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.addMemberRow = i;
            }
            if ((this.info != null) && (this.info.participants != null) && (!this.info.participants.participants.isEmpty()))
            {
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.emptyRowChat = i;
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.membersSectionRow = i;
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.emptyRowChat2 = i;
              this.rowCount += this.info.participants.participants.size();
              this.membersEndRow = this.rowCount;
              if (!this.usersEndReached)
              {
                i = this.rowCount;
                this.rowCount = (i + 1);
                this.loadMoreMembersRow = i;
              }
            }
          }
          else
          {
            if (this.info != null)
            {
              if ((!(this.info.participants instanceof TLRPC.TL_chatParticipantsForbidden)) && (this.info.participants.participants.size() < MessagesController.getInstance().maxGroupCount) && ((this.currentChat.admin) || (this.currentChat.creator) || (!this.currentChat.admins_enabled)))
              {
                i = this.rowCount;
                this.rowCount = (i + 1);
                this.addMemberRow = i;
              }
              if ((this.currentChat.creator) && (this.info.participants.participants.size() >= MessagesController.getInstance().minGroupConvertSize))
              {
                i = this.rowCount;
                this.rowCount = (i + 1);
                this.convertRow = i;
              }
            }
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.emptyRowChat = i;
            if (this.convertRow != -1)
            {
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.convertHelpRow = i;
            }
            while ((this.info != null) && (!(this.info.participants instanceof TLRPC.TL_chatParticipantsForbidden)))
            {
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.emptyRowChat2 = i;
              this.rowCount += this.info.participants.participants.size();
              this.membersEndRow = this.rowCount;
              return;
              i = this.rowCount;
              this.rowCount = (i + 1);
              this.membersSectionRow = i;
            }
          }
        }
      }
    } while ((ChatObject.isChannel(this.currentChat)) || (this.info == null) || ((this.info.participants instanceof TLRPC.TL_chatParticipantsForbidden)));
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.addMemberRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emptyRowChat2 = i;
    this.rowCount += this.info.participants.participants.size();
    this.membersEndRow = this.rowCount;
  }
  
  public boolean allowCaption()
  {
    return true;
  }
  
  public boolean cancelButtonPressed()
  {
    return true;
  }
  
  protected ActionBar createActionBar(Context paramContext)
  {
    paramContext = new ActionBar(paramContext)
    {
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        return super.onTouchEvent(paramAnonymousMotionEvent);
      }
    };
    int i;
    if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      paramContext.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(i));
      paramContext.setBackButtonDrawable(new BackDrawable(false));
      paramContext.setCastShadows(false);
      paramContext.setAddToContainer(false);
      if ((Build.VERSION.SDK_INT < 21) || (AndroidUtilities.isTablet())) {
        break label100;
      }
    }
    label100:
    for (boolean bool = true;; bool = false)
    {
      paramContext.setOccupyStatusBar(bool);
      return paramContext;
      i = this.chat_id;
      break;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.hasOwnBackground = true;
    this.extraHeight = AndroidUtilities.dp(88.0F);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (ProfileActivity.this.getParentActivity() == null) {}
        do
        {
          final Object localObject1;
          do
          {
            do
            {
              do
              {
                return;
                if (paramAnonymousInt == -1)
                {
                  ProfileActivity.this.finishFragment();
                  return;
                }
                if (paramAnonymousInt != 2) {
                  break;
                }
                localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
              } while (localObject1 == null);
              if (!((TLRPC.User)localObject1).bot)
              {
                localObject1 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
                if (!ProfileActivity.this.userBlocked) {
                  ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AreYouSureBlockContact", 2131165314));
                }
                for (;;)
                {
                  ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165299));
                  ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                    {
                      if (!ProfileActivity.this.userBlocked)
                      {
                        MessagesController.getInstance().blockUser(ProfileActivity.this.user_id);
                        return;
                      }
                      MessagesController.getInstance().unblockUser(ProfileActivity.this.user_id);
                    }
                  });
                  ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                  ProfileActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
                  return;
                  ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AreYouSureUnblockContact", 2131165331));
                }
              }
              if (!ProfileActivity.this.userBlocked)
              {
                MessagesController.getInstance().blockUser(ProfileActivity.this.user_id);
                return;
              }
              MessagesController.getInstance().unblockUser(ProfileActivity.this.user_id);
              SendMessagesHelper.getInstance().sendMessage("/start", ProfileActivity.this.user_id, null, null, false, null, null, null);
              ProfileActivity.this.finishFragment();
              return;
              if (paramAnonymousInt == 1)
              {
                localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                localObject3 = new Bundle();
                ((Bundle)localObject3).putInt("user_id", ((TLRPC.User)localObject1).id);
                ((Bundle)localObject3).putBoolean("addContact", true);
                ProfileActivity.this.presentFragment(new ContactAddActivity((Bundle)localObject3));
                return;
              }
              if (paramAnonymousInt == 3)
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putBoolean("onlySelect", true);
                ((Bundle)localObject1).putInt("dialogsType", 1);
                ((Bundle)localObject1).putString("selectAlertString", LocaleController.getString("SendContactTo", 2131166235));
                ((Bundle)localObject1).putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", 2131166236));
                localObject1 = new DialogsActivity((Bundle)localObject1);
                ((DialogsActivity)localObject1).setDelegate(ProfileActivity.this);
                ProfileActivity.this.presentFragment((BaseFragment)localObject1);
                return;
              }
              if (paramAnonymousInt == 4)
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putInt("user_id", ProfileActivity.this.user_id);
                ProfileActivity.this.presentFragment(new ContactAddActivity((Bundle)localObject1));
                return;
              }
              if (paramAnonymousInt != 5) {
                break;
              }
              localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
            } while ((localObject1 == null) || (ProfileActivity.this.getParentActivity() == null));
            localObject3 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
            ((AlertDialog.Builder)localObject3).setMessage(LocaleController.getString("AreYouSureDeleteContact", 2131165319));
            ((AlertDialog.Builder)localObject3).setTitle(LocaleController.getString("AppName", 2131165299));
            ((AlertDialog.Builder)localObject3).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = new ArrayList();
                paramAnonymous2DialogInterface.add(localObject1);
                ContactsController.getInstance().deleteContact(paramAnonymous2DialogInterface);
              }
            });
            ((AlertDialog.Builder)localObject3).setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            ProfileActivity.this.showDialog(((AlertDialog.Builder)localObject3).create());
            return;
            if (paramAnonymousInt == 7)
            {
              ProfileActivity.this.leaveChatPressed();
              return;
            }
            if (paramAnonymousInt == 8)
            {
              localObject1 = new Bundle();
              ((Bundle)localObject1).putInt("chat_id", ProfileActivity.this.chat_id);
              ProfileActivity.this.presentFragment(new ChangeChatNameActivity((Bundle)localObject1));
              return;
            }
            if (paramAnonymousInt == 12)
            {
              localObject1 = new Bundle();
              ((Bundle)localObject1).putInt("chat_id", ProfileActivity.this.chat_id);
              localObject1 = new ChannelEditActivity((Bundle)localObject1);
              ((ChannelEditActivity)localObject1).setInfo(ProfileActivity.this.info);
              ProfileActivity.this.presentFragment((BaseFragment)localObject1);
              return;
            }
            if (paramAnonymousInt != 9) {
              break;
            }
            localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
          } while (localObject1 == null);
          Object localObject3 = new Bundle();
          ((Bundle)localObject3).putBoolean("onlySelect", true);
          ((Bundle)localObject3).putInt("dialogsType", 2);
          ((Bundle)localObject3).putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", 2131165274, new Object[] { UserObject.getUserName((TLRPC.User)localObject1), "%1$s" }));
          localObject3 = new DialogsActivity((Bundle)localObject3);
          ((DialogsActivity)localObject3).setDelegate(new DialogsActivity.DialogsActivityDelegate()
          {
            public void didSelectDialog(DialogsActivity paramAnonymous2DialogsActivity, long paramAnonymous2Long, boolean paramAnonymous2Boolean)
            {
              Bundle localBundle = new Bundle();
              localBundle.putBoolean("scrollToTopOnResume", true);
              localBundle.putInt("chat_id", -(int)paramAnonymous2Long);
              if (!MessagesController.checkCanOpenChat(localBundle, paramAnonymous2DialogsActivity)) {
                return;
              }
              NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
              MessagesController.getInstance().addUserToChat(-(int)paramAnonymous2Long, localObject1, null, 0, null, ProfileActivity.this);
              ProfileActivity.this.presentFragment(new ChatActivity(localBundle), true);
              ProfileActivity.this.removeSelfFromStack();
            }
          });
          ProfileActivity.this.presentFragment((BaseFragment)localObject3);
          return;
          if (paramAnonymousInt == 10) {
            for (;;)
            {
              try
              {
                localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                if (localObject1 == null) {
                  break;
                }
                localObject3 = new Intent("android.intent.action.SEND");
                ((Intent)localObject3).setType("text/plain");
                String str = MessagesController.getInstance().getUserAbout(ProfileActivity.this.botInfo.user_id);
                if ((ProfileActivity.this.botInfo != null) && (str != null))
                {
                  ((Intent)localObject3).putExtra("android.intent.extra.TEXT", String.format("%s https://telegram.me/%s", new Object[] { str, ((TLRPC.User)localObject1).username }));
                  ProfileActivity.this.startActivityForResult(Intent.createChooser((Intent)localObject3, LocaleController.getString("BotShare", 2131165373)), 500);
                  return;
                }
              }
              catch (Exception localException1)
              {
                FileLog.e("tmessages", localException1);
                return;
              }
              ((Intent)localObject3).putExtra("android.intent.extra.TEXT", String.format("https://telegram.me/%s", new Object[] { localException1.username }));
            }
          }
          Object localObject2;
          if (paramAnonymousInt == 11)
          {
            localObject2 = new Bundle();
            ((Bundle)localObject2).putInt("chat_id", ProfileActivity.this.chat_id);
            localObject2 = new SetAdminsActivity((Bundle)localObject2);
            ((SetAdminsActivity)localObject2).setChatInfo(ProfileActivity.this.info);
            ProfileActivity.this.presentFragment((BaseFragment)localObject2);
            return;
          }
          if (paramAnonymousInt == 13)
          {
            localObject2 = new Bundle();
            ((Bundle)localObject2).putInt("chat_id", ProfileActivity.this.chat_id);
            ProfileActivity.this.presentFragment(new ConvertGroupActivity((Bundle)localObject2));
            return;
          }
        } while (paramAnonymousInt != 14);
        for (;;)
        {
          long l;
          try
          {
            if (ProfileActivity.this.currentEncryptedChat != null)
            {
              l = ProfileActivity.this.currentEncryptedChat.id << 32;
              AndroidUtilities.installShortcut(l);
              return;
            }
          }
          catch (Exception localException2)
          {
            FileLog.e("tmessages", localException2);
            return;
          }
          if (ProfileActivity.this.user_id != 0)
          {
            l = ProfileActivity.this.user_id;
          }
          else
          {
            if (ProfileActivity.this.chat_id == 0) {
              break;
            }
            paramAnonymousInt = ProfileActivity.this.chat_id;
            l = -paramAnonymousInt;
          }
        }
      }
    });
    createActionBarMenu();
    this.listAdapter = new ListAdapter(paramContext);
    this.avatarDrawable = new AvatarDrawable();
    this.avatarDrawable.setProfile(true);
    this.fragmentView = new FrameLayout(paramContext)
    {
      public boolean hasOverlappingRendering()
      {
        return false;
      }
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        ProfileActivity.this.checkListViewScroll();
      }
    };
    Object localObject1 = (FrameLayout)this.fragmentView;
    this.listView = new RecyclerListView(paramContext)
    {
      public boolean hasOverlappingRendering()
      {
        return false;
      }
    };
    this.listView.setTag(Integer.valueOf(6));
    this.listView.setPadding(0, AndroidUtilities.dp(88.0F), 0, 0);
    this.listView.setBackgroundColor(-1);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    this.listView.setClipToPadding(false);
    this.layoutManager = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.layoutManager.setOrientation(1);
    this.listView.setLayoutManager(this.layoutManager);
    Object localObject2 = this.listView;
    if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      ((RecyclerListView)localObject2).setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
      ((FrameLayout)localObject1).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (ProfileActivity.this.getParentActivity() == null) {}
          for (;;)
          {
            return;
            long l;
            if (paramAnonymousInt == ProfileActivity.this.sharedMediaRow)
            {
              paramAnonymousView = new Bundle();
              if (ProfileActivity.this.user_id != 0) {
                if (ProfileActivity.this.dialog_id != 0L)
                {
                  l = ProfileActivity.this.dialog_id;
                  paramAnonymousView.putLong("dialog_id", l);
                }
              }
              for (;;)
              {
                paramAnonymousView = new MediaActivity(paramAnonymousView);
                paramAnonymousView.setChatInfo(ProfileActivity.this.info);
                ProfileActivity.this.presentFragment(paramAnonymousView);
                return;
                l = ProfileActivity.this.user_id;
                break;
                paramAnonymousView.putLong("dialog_id", -ProfileActivity.this.chat_id);
              }
            }
            if (paramAnonymousInt == ProfileActivity.this.settingsKeyRow)
            {
              paramAnonymousView = new Bundle();
              paramAnonymousView.putInt("chat_id", (int)(ProfileActivity.this.dialog_id >> 32));
              ProfileActivity.this.presentFragment(new IdenticonActivity(paramAnonymousView));
              return;
            }
            if (paramAnonymousInt == ProfileActivity.this.settingsTimerRow)
            {
              ProfileActivity.this.showDialog(AndroidUtilities.buildTTLAlert(ProfileActivity.this.getParentActivity(), ProfileActivity.this.currentEncryptedChat).create());
              return;
            }
            if (paramAnonymousInt == ProfileActivity.this.settingsNotificationsRow)
            {
              paramAnonymousView = new Bundle();
              if (ProfileActivity.this.user_id != 0) {
                if (ProfileActivity.this.dialog_id == 0L)
                {
                  l = ProfileActivity.this.user_id;
                  paramAnonymousView.putLong("dialog_id", l);
                }
              }
              for (;;)
              {
                ProfileActivity.this.presentFragment(new ProfileNotificationsActivity(paramAnonymousView));
                return;
                l = ProfileActivity.this.dialog_id;
                break;
                if (ProfileActivity.this.chat_id != 0) {
                  paramAnonymousView.putLong("dialog_id", -ProfileActivity.this.chat_id);
                }
              }
            }
            if (paramAnonymousInt == ProfileActivity.this.startSecretChatRow)
            {
              paramAnonymousView = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
              paramAnonymousView.setMessage(LocaleController.getString("AreYouSureSecretChat", 2131165326));
              paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
              paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  ProfileActivity.access$1902(ProfileActivity.this, true);
                  SecretChatHelper.getInstance().startSecretChat(ProfileActivity.this.getParentActivity(), MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id)));
                }
              });
              paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
              ProfileActivity.this.showDialog(paramAnonymousView.create());
              return;
            }
            if ((paramAnonymousInt <= ProfileActivity.this.emptyRowChat2) || (paramAnonymousInt >= ProfileActivity.this.membersEndRow)) {
              break;
            }
            if (!ProfileActivity.this.sortedUsers.isEmpty()) {}
            for (paramAnonymousInt = ((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(((Integer)ProfileActivity.this.sortedUsers.get(paramAnonymousInt - ProfileActivity.this.emptyRowChat2 - 1)).intValue())).user_id; paramAnonymousInt != UserConfig.getClientUserId(); paramAnonymousInt = ((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramAnonymousInt - ProfileActivity.this.emptyRowChat2 - 1)).user_id)
            {
              paramAnonymousView = new Bundle();
              paramAnonymousView.putInt("user_id", paramAnonymousInt);
              ProfileActivity.this.presentFragment(new ProfileActivity(paramAnonymousView));
              return;
            }
          }
          if (paramAnonymousInt == ProfileActivity.this.addMemberRow)
          {
            ProfileActivity.this.openAddMember();
            return;
          }
          if (paramAnonymousInt == ProfileActivity.this.channelNameRow) {
            for (;;)
            {
              try
              {
                paramAnonymousView = new Intent("android.intent.action.SEND");
                paramAnonymousView.setType("text/plain");
                if ((ProfileActivity.this.info.about != null) && (ProfileActivity.this.info.about.length() > 0))
                {
                  paramAnonymousView.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\n" + ProfileActivity.this.info.about + "\nhttps://telegram.me/" + ProfileActivity.this.currentChat.username);
                  ProfileActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(paramAnonymousView, LocaleController.getString("BotShare", 2131165373)), 500);
                  return;
                }
              }
              catch (Exception paramAnonymousView)
              {
                FileLog.e("tmessages", paramAnonymousView);
                return;
              }
              paramAnonymousView.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\nhttps://telegram.me/" + ProfileActivity.this.currentChat.username);
            }
          }
          if (paramAnonymousInt == ProfileActivity.this.leaveChannelRow)
          {
            ProfileActivity.this.leaveChatPressed();
            return;
          }
          if ((paramAnonymousInt == ProfileActivity.this.membersRow) || (paramAnonymousInt == ProfileActivity.this.blockedUsersRow) || (paramAnonymousInt == ProfileActivity.this.managementRow))
          {
            paramAnonymousView = new Bundle();
            paramAnonymousView.putInt("chat_id", ProfileActivity.this.chat_id);
            if (paramAnonymousInt == ProfileActivity.this.blockedUsersRow) {
              paramAnonymousView.putInt("type", 0);
            }
            for (;;)
            {
              ProfileActivity.this.presentFragment(new ChannelUsersActivity(paramAnonymousView));
              return;
              if (paramAnonymousInt == ProfileActivity.this.managementRow) {
                paramAnonymousView.putInt("type", 1);
              } else if (paramAnonymousInt == ProfileActivity.this.membersRow) {
                paramAnonymousView.putInt("type", 2);
              }
            }
          }
          if (paramAnonymousInt == ProfileActivity.this.convertRow)
          {
            paramAnonymousView = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
            paramAnonymousView.setMessage(LocaleController.getString("ConvertGroupAlert", 2131165525));
            paramAnonymousView.setTitle(LocaleController.getString("ConvertGroupAlertWarning", 2131165526));
            paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                MessagesController.getInstance().convertToMegaGroup(ProfileActivity.this.getParentActivity(), ProfileActivity.this.chat_id);
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            ProfileActivity.this.showDialog(paramAnonymousView.create());
            return;
          }
          ProfileActivity.this.processOnClickOrPress(paramAnonymousInt);
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(final View paramAnonymousView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt > ProfileActivity.this.emptyRowChat2) && (paramAnonymousInt < ProfileActivity.this.membersEndRow))
          {
            if (ProfileActivity.this.getParentActivity() == null) {}
            Object localObject1;
            label163:
            Object localObject2;
            label344:
            label385:
            label390:
            label488:
            for (;;)
            {
              return false;
              int k = 0;
              int i = 0;
              int j = 0;
              if (!ProfileActivity.this.sortedUsers.isEmpty())
              {
                paramAnonymousView = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(((Integer)ProfileActivity.this.sortedUsers.get(paramAnonymousInt - ProfileActivity.this.emptyRowChat2 - 1)).intValue());
                ProfileActivity.access$3202(ProfileActivity.this, paramAnonymousView.user_id);
                if (!ChatObject.isChannel(ProfileActivity.this.currentChat)) {
                  break label390;
                }
                localObject1 = ((TLRPC.TL_chatChannelParticipant)paramAnonymousView).channelParticipant;
                paramAnonymousInt = i;
                if (paramAnonymousView.user_id != UserConfig.getClientUserId())
                {
                  if (!ProfileActivity.this.currentChat.creator) {
                    break label344;
                  }
                  paramAnonymousInt = 1;
                }
                localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(paramAnonymousView.user_id));
                if ((!(localObject1 instanceof TLRPC.TL_channelParticipant)) || (((TLRPC.User)localObject2).bot)) {
                  break label385;
                }
                i = 1;
              }
              for (;;)
              {
                if (paramAnonymousInt == 0) {
                  break label488;
                }
                localObject1 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
                if ((!ProfileActivity.this.currentChat.megagroup) || (!ProfileActivity.this.currentChat.creator) || (i == 0)) {
                  break label490;
                }
                localObject2 = LocaleController.getString("SetAsAdmin", 2131166267);
                String str = LocaleController.getString("KickFromGroup", 2131165783);
                paramAnonymousView = new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    if (paramAnonymous2Int == 0)
                    {
                      paramAnonymous2DialogInterface = (TLRPC.TL_chatChannelParticipant)paramAnonymousView;
                      paramAnonymous2DialogInterface.channelParticipant = new TLRPC.TL_channelParticipantEditor();
                      paramAnonymous2DialogInterface.channelParticipant.inviter_id = UserConfig.getClientUserId();
                      paramAnonymous2DialogInterface.channelParticipant.user_id = paramAnonymousView.user_id;
                      paramAnonymous2DialogInterface.channelParticipant.date = paramAnonymousView.date;
                      paramAnonymous2DialogInterface = new TLRPC.TL_channels_editAdmin();
                      paramAnonymous2DialogInterface.channel = MessagesController.getInputChannel(ProfileActivity.this.chat_id);
                      paramAnonymous2DialogInterface.user_id = MessagesController.getInputUser(ProfileActivity.this.selectedUser);
                      paramAnonymous2DialogInterface.role = new TLRPC.TL_channelRoleEditor();
                      ConnectionsManager.getInstance().sendRequest(paramAnonymous2DialogInterface, new RequestDelegate()
                      {
                        public void run(TLObject paramAnonymous3TLObject, final TLRPC.TL_error paramAnonymous3TL_error)
                        {
                          if (paramAnonymous3TL_error == null)
                          {
                            MessagesController.getInstance().processUpdates((TLRPC.Updates)paramAnonymous3TLObject, false);
                            AndroidUtilities.runOnUIThread(new Runnable()
                            {
                              public void run()
                              {
                                MessagesController.getInstance().loadFullChat(ProfileActivity.this.chat_id, 0, true);
                              }
                            }, 1000L);
                            return;
                          }
                          AndroidUtilities.runOnUIThread(new Runnable()
                          {
                            public void run()
                            {
                              AlertsCreator.showAddUserAlert(paramAnonymous3TL_error.text, ProfileActivity.this, false);
                            }
                          });
                        }
                      });
                    }
                    while (paramAnonymous2Int != 1) {
                      return;
                    }
                    ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                  }
                };
                ((AlertDialog.Builder)localObject1).setItems(new CharSequence[] { localObject2, str }, paramAnonymousView);
                ProfileActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
                return true;
                paramAnonymousView = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramAnonymousInt - ProfileActivity.this.emptyRowChat2 - 1);
                break;
                paramAnonymousInt = i;
                if (!(localObject1 instanceof TLRPC.TL_channelParticipant)) {
                  break label163;
                }
                if (!ProfileActivity.this.currentChat.editor)
                {
                  paramAnonymousInt = i;
                  if (((TLRPC.ChannelParticipant)localObject1).inviter_id != UserConfig.getClientUserId()) {
                    break label163;
                  }
                }
                paramAnonymousInt = 1;
                break label163;
                i = 0;
                continue;
                paramAnonymousInt = k;
                i = j;
                if (paramAnonymousView.user_id != UserConfig.getClientUserId()) {
                  if (ProfileActivity.this.currentChat.creator)
                  {
                    paramAnonymousInt = 1;
                    i = j;
                  }
                  else
                  {
                    paramAnonymousInt = k;
                    i = j;
                    if ((paramAnonymousView instanceof TLRPC.TL_chatParticipant)) {
                      if ((!ProfileActivity.this.currentChat.admin) || (!ProfileActivity.this.currentChat.admins_enabled))
                      {
                        paramAnonymousInt = k;
                        i = j;
                        if (paramAnonymousView.inviter_id != UserConfig.getClientUserId()) {}
                      }
                      else
                      {
                        paramAnonymousInt = 1;
                        i = j;
                      }
                    }
                  }
                }
              }
            }
            label490:
            if (ProfileActivity.this.chat_id > 0) {}
            for (paramAnonymousView = LocaleController.getString("KickFromGroup", 2131165783);; paramAnonymousView = LocaleController.getString("KickFromBroadcast", 2131165782))
            {
              localObject2 = new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  if (paramAnonymous2Int == 0) {
                    ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                  }
                }
              };
              ((AlertDialog.Builder)localObject1).setItems(new CharSequence[] { paramAnonymousView }, (DialogInterface.OnClickListener)localObject2);
              break;
            }
          }
          return ProfileActivity.this.processOnClickOrPress(paramAnonymousInt);
        }
      });
      this.topView = new TopView(paramContext);
      localObject2 = this.topView;
      if ((this.user_id == 0) && ((!ChatObject.isChannel(this.chat_id)) || (this.currentChat.megagroup))) {
        break label491;
      }
    }
    label491:
    for (int i = 5;; i = this.chat_id)
    {
      ((TopView)localObject2).setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i));
      ((FrameLayout)localObject1).addView(this.topView);
      ((FrameLayout)localObject1).addView(this.actionBar);
      this.avatarImage = new BackupImageView(paramContext);
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
      this.avatarImage.setPivotX(0.0F);
      this.avatarImage.setPivotY(0.0F);
      ((FrameLayout)localObject1).addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0F, 51, 64.0F, 0.0F, 0.0F, 0.0F));
      this.avatarImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ProfileActivity.this.user_id != 0)
          {
            paramAnonymousView = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
            if ((paramAnonymousView.photo != null) && (paramAnonymousView.photo.photo_big != null)) {
              PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
            }
          }
          do
          {
            PhotoViewer.getInstance().openPhoto(paramAnonymousView.photo.photo_big, ProfileActivity.this);
            do
            {
              return;
            } while (ProfileActivity.this.chat_id == 0);
            paramAnonymousView = MessagesController.getInstance().getChat(Integer.valueOf(ProfileActivity.this.chat_id));
          } while ((paramAnonymousView.photo == null) || (paramAnonymousView.photo.photo_big == null));
          PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
          PhotoViewer.getInstance().openPhoto(paramAnonymousView.photo.photo_big, ProfileActivity.this);
        }
      });
      i = 0;
      for (;;)
      {
        if (i >= 2) {
          break label796;
        }
        if ((this.playProfileAnimation) || (i != 0)) {
          break;
        }
        i += 1;
      }
      i = this.chat_id;
      break;
    }
    this.nameTextView[i] = new SimpleTextView(paramContext);
    this.nameTextView[i].setTextColor(-1);
    this.nameTextView[i].setTextSize(18);
    this.nameTextView[i].setGravity(3);
    this.nameTextView[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView[i].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3F));
    this.nameTextView[i].setRightDrawableTopPadding(-AndroidUtilities.dp(1.3F));
    this.nameTextView[i].setPivotX(0.0F);
    this.nameTextView[i].setPivotY(0.0F);
    localObject2 = this.nameTextView[i];
    label627:
    int j;
    if (i == 0)
    {
      f = 48.0F;
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, f, 0.0F));
      this.onlineTextView[i] = new SimpleTextView(paramContext);
      localObject2 = this.onlineTextView[i];
      if ((this.user_id == 0) && ((!ChatObject.isChannel(this.chat_id)) || (this.currentChat.megagroup))) {
        break label780;
      }
      j = 5;
      label702:
      ((SimpleTextView)localObject2).setTextColor(AvatarDrawable.getProfileTextColorForId(j));
      this.onlineTextView[i].setTextSize(14);
      this.onlineTextView[i].setGravity(3);
      localObject2 = this.onlineTextView[i];
      if (i != 0) {
        break label789;
      }
    }
    label780:
    label789:
    for (float f = 48.0F;; f = 8.0F)
    {
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, f, 0.0F));
      break;
      f = 0.0F;
      break label627;
      j = this.chat_id;
      break label702;
    }
    label796:
    if ((this.user_id != 0) || ((this.chat_id >= 0) && (!ChatObject.isLeftFromChat(this.currentChat)))) {
      this.writeButton = new ImageView(paramContext);
    }
    try
    {
      this.writeButton.setBackgroundResource(2130837685);
      this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
      if (this.user_id != 0)
      {
        this.writeButton.setImageResource(2130837681);
        this.writeButton.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
        ((FrameLayout)localObject1).addView(this.writeButton, LayoutHelper.createFrame(-2, -2.0F, 53, 0.0F, 0.0F, 16.0F, 0.0F));
        if (Build.VERSION.SDK_INT >= 21)
        {
          paramContext = new StateListAnimator();
          localObject1 = ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
          paramContext.addState(new int[] { 16842919 }, (Animator)localObject1);
          localObject1 = ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
          paramContext.addState(new int[0], (Animator)localObject1);
          this.writeButton.setStateListAnimator(paramContext);
          this.writeButton.setOutlineProvider(new ViewOutlineProvider()
          {
            @SuppressLint({"NewApi"})
            public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
            {
              paramAnonymousOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
            }
          });
        }
        this.writeButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (ProfileActivity.this.getParentActivity() == null) {}
            do
            {
              do
              {
                do
                {
                  do
                  {
                    return;
                    if (ProfileActivity.this.user_id == 0) {
                      break;
                    }
                    if ((ProfileActivity.this.playProfileAnimation) && ((ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)))
                    {
                      ProfileActivity.this.finishFragment();
                      return;
                    }
                    paramAnonymousView = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                  } while ((paramAnonymousView == null) || ((paramAnonymousView instanceof TLRPC.TL_userEmpty)));
                  paramAnonymousView = new Bundle();
                  paramAnonymousView.putInt("user_id", ProfileActivity.this.user_id);
                } while (!MessagesController.checkCanOpenChat(paramAnonymousView, ProfileActivity.this));
                NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                ProfileActivity.this.presentFragment(new ChatActivity(paramAnonymousView), true);
                return;
              } while (ProfileActivity.this.chat_id == 0);
              boolean bool = ChatObject.isChannel(ProfileActivity.this.currentChat);
              if (((!bool) || (ProfileActivity.this.currentChat.creator) || ((ProfileActivity.this.currentChat.megagroup) && (ProfileActivity.this.currentChat.editor))) && ((bool) || (ProfileActivity.this.currentChat.admin) || (ProfileActivity.this.currentChat.creator) || (!ProfileActivity.this.currentChat.admins_enabled))) {
                break;
              }
              if ((ProfileActivity.this.playProfileAnimation) && ((ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)))
              {
                ProfileActivity.this.finishFragment();
                return;
              }
              paramAnonymousView = new Bundle();
              paramAnonymousView.putInt("chat_id", ProfileActivity.this.currentChat.id);
            } while (!MessagesController.checkCanOpenChat(paramAnonymousView, ProfileActivity.this));
            NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            ProfileActivity.this.presentFragment(new ChatActivity(paramAnonymousView), true);
            return;
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
            paramAnonymousView = MessagesController.getInstance().getChat(Integer.valueOf(ProfileActivity.this.chat_id));
            if ((paramAnonymousView.photo == null) || (paramAnonymousView.photo.photo_big == null) || ((paramAnonymousView.photo instanceof TLRPC.TL_chatPhotoEmpty)))
            {
              paramAnonymousView = new CharSequence[2];
              paramAnonymousView[0] = LocaleController.getString("FromCamera", 2131165703);
              paramAnonymousView[1] = LocaleController.getString("FromGalley", 2131165710);
            }
            for (;;)
            {
              localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  if (paramAnonymous2Int == 0) {
                    ProfileActivity.this.avatarUpdater.openCamera();
                  }
                  do
                  {
                    return;
                    if (paramAnonymous2Int == 1)
                    {
                      ProfileActivity.this.avatarUpdater.openGallery();
                      return;
                    }
                  } while (paramAnonymous2Int != 2);
                  MessagesController.getInstance().changeChatAvatar(ProfileActivity.this.chat_id, null);
                }
              });
              ProfileActivity.this.showDialog(localBuilder.create());
              return;
              paramAnonymousView = new CharSequence[3];
              paramAnonymousView[0] = LocaleController.getString("FromCamera", 2131165703);
              paramAnonymousView[1] = LocaleController.getString("FromGalley", 2131165710);
              paramAnonymousView[2] = LocaleController.getString("DeletePhoto", 2131165576);
            }
          }
        });
        needLayout();
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
          public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            ProfileActivity.this.checkListViewScroll();
            if ((ProfileActivity.this.participantsMap != null) && (ProfileActivity.this.loadMoreMembersRow != -1) && (ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.loadMoreMembersRow - 8)) {
              ProfileActivity.this.getChannelParticipants(false);
            }
          }
        });
        return this.fragmentView;
      }
    }
    catch (Throwable paramContext)
    {
      for (;;)
      {
        FileLog.e("tmessages", paramContext);
        continue;
        if (this.chat_id != 0)
        {
          boolean bool = ChatObject.isChannel(this.currentChat);
          if (((bool) && (!this.currentChat.creator) && ((!this.currentChat.megagroup) || (!this.currentChat.editor))) || ((!bool) && (!this.currentChat.admin) && (!this.currentChat.creator) && (this.currentChat.admins_enabled)))
          {
            this.writeButton.setImageResource(2130837681);
            this.writeButton.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
          }
          else
          {
            this.writeButton.setImageResource(2130837679);
          }
        }
      }
    }
  }
  
  public void didReceivedNotification(int paramInt, final Object... paramVarArgs)
  {
    int i;
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      i = ((Integer)paramVarArgs[0]).intValue();
      if (this.user_id != 0)
      {
        if (((i & 0x2) != 0) || ((i & 0x1) != 0) || ((i & 0x4) != 0)) {
          updateProfileData();
        }
        if (((i & 0x400) != 0) && (this.listView != null))
        {
          paramVarArgs = (ProfileActivity.ListAdapter.Holder)this.listView.findViewHolderForPosition(this.phoneRow);
          if (paramVarArgs != null)
          {
            this.listAdapter.onBindViewHolder(paramVarArgs, this.phoneRow);
            break label92;
            break label92;
            break label92;
            break label92;
            break label92;
            break label92;
            break label92;
            break label92;
            break label92;
            break label92;
            break label92;
          }
        }
      }
    }
    for (;;)
    {
      label92:
      return;
      if (this.chat_id != 0)
      {
        if ((i & 0x4000) != 0)
        {
          paramVarArgs = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
          if (paramVarArgs != null)
          {
            this.currentChat = paramVarArgs;
            createActionBarMenu();
            updateRowsIds();
            if (this.listAdapter != null) {
              this.listAdapter.notifyDataSetChanged();
            }
          }
        }
        if (((i & 0x2000) != 0) || ((i & 0x8) != 0) || ((i & 0x10) != 0) || ((i & 0x20) != 0) || ((i & 0x4) != 0))
        {
          updateOnlineCount();
          updateProfileData();
        }
        if ((i & 0x2000) != 0)
        {
          updateRowsIds();
          if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
          }
        }
        if ((((i & 0x2) == 0) && ((i & 0x1) == 0) && ((i & 0x4) == 0)) || (this.listView == null)) {
          break;
        }
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
        continue;
        if (paramInt == NotificationCenter.contactsDidLoaded)
        {
          createActionBarMenu();
          return;
        }
        if (paramInt == NotificationCenter.mediaCountDidLoaded)
        {
          long l3 = ((Long)paramVarArgs[0]).longValue();
          long l2 = this.dialog_id;
          long l1 = l2;
          if (l2 == 0L)
          {
            if (this.user_id != 0) {
              l1 = this.user_id;
            }
          }
          else
          {
            label356:
            if ((l3 != l1) && (l3 != this.mergeDialogId)) {
              break label482;
            }
            if (l3 != l1) {
              break label484;
            }
            this.totalMediaCount = ((Integer)paramVarArgs[1]).intValue();
            label395:
            if (this.listView == null) {
              break label498;
            }
            i = this.listView.getChildCount();
            paramInt = 0;
          }
          for (;;)
          {
            if (paramInt >= i) {
              break label505;
            }
            paramVarArgs = this.listView.getChildAt(paramInt);
            paramVarArgs = (ProfileActivity.ListAdapter.Holder)this.listView.getChildViewHolder(paramVarArgs);
            if (paramVarArgs.getAdapterPosition() == this.sharedMediaRow)
            {
              this.listAdapter.onBindViewHolder(paramVarArgs, this.sharedMediaRow);
              return;
              l1 = l2;
              if (this.chat_id == 0) {
                break label356;
              }
              l1 = -this.chat_id;
              break label356;
              label482:
              break;
              label484:
              this.totalMediaCountMerge = ((Integer)paramVarArgs[1]).intValue();
              break label395;
              label498:
              break;
            }
            paramInt += 1;
          }
        }
        else
        {
          label505:
          if (paramInt == NotificationCenter.encryptedChatCreated)
          {
            if (!this.creatingChat) {
              break;
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                TLRPC.EncryptedChat localEncryptedChat = (TLRPC.EncryptedChat)paramVarArgs[0];
                Bundle localBundle = new Bundle();
                localBundle.putInt("enc_id", localEncryptedChat.id);
                ProfileActivity.this.presentFragment(new ChatActivity(localBundle), true);
              }
            });
            return;
          }
          if (paramInt == NotificationCenter.encryptedChatUpdated)
          {
            paramVarArgs = (TLRPC.EncryptedChat)paramVarArgs[0];
            if ((this.currentEncryptedChat == null) || (paramVarArgs.id != this.currentEncryptedChat.id)) {
              break;
            }
            this.currentEncryptedChat = paramVarArgs;
            updateRowsIds();
            if (this.listAdapter == null) {
              break;
            }
            this.listAdapter.notifyDataSetChanged();
            return;
          }
          boolean bool;
          if (paramInt == NotificationCenter.blockedUsersDidLoaded)
          {
            bool = this.userBlocked;
            this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.user_id));
            if (bool == this.userBlocked) {
              break;
            }
            createActionBarMenu();
            return;
          }
          Object localObject;
          if (paramInt == NotificationCenter.chatInfoDidLoaded)
          {
            localObject = (TLRPC.ChatFull)paramVarArgs[0];
            if (((TLRPC.ChatFull)localObject).id != this.chat_id) {
              break;
            }
            bool = ((Boolean)paramVarArgs[2]).booleanValue();
            if (((this.info instanceof TLRPC.TL_channelFull)) && (((TLRPC.ChatFull)localObject).participants == null) && (this.info != null)) {
              ((TLRPC.ChatFull)localObject).participants = this.info.participants;
            }
            if ((this.info == null) && ((localObject instanceof TLRPC.TL_channelFull))) {}
            for (paramInt = 1;; paramInt = 0)
            {
              this.info = ((TLRPC.ChatFull)localObject);
              if ((this.mergeDialogId == 0L) && (this.info.migrated_from_chat_id != 0))
              {
                this.mergeDialogId = (-this.info.migrated_from_chat_id);
                SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
              }
              fetchUsersFromChannelInfo();
              updateOnlineCount();
              updateRowsIds();
              if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
              }
              paramVarArgs = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
              if (paramVarArgs != null)
              {
                this.currentChat = paramVarArgs;
                createActionBarMenu();
              }
              if ((!this.currentChat.megagroup) || ((paramInt == 0) && (bool))) {
                break;
              }
              getChannelParticipants(true);
              return;
            }
          }
          if (paramInt == NotificationCenter.closeChats)
          {
            removeSelfFromStack();
            return;
          }
          if (paramInt == NotificationCenter.botInfoDidLoaded)
          {
            paramVarArgs = (TLRPC.BotInfo)paramVarArgs[0];
            if (paramVarArgs.user_id != this.user_id) {
              break;
            }
            this.botInfo = paramVarArgs;
            updateRowsIds();
            if (this.listAdapter == null) {
              break;
            }
            this.listAdapter.notifyDataSetChanged();
            return;
          }
          if (paramInt == NotificationCenter.userInfoDidLoaded)
          {
            if (((Integer)paramVarArgs[0]).intValue() != this.user_id) {
              break;
            }
            updateRowsIds();
            if (this.listAdapter == null) {
              break;
            }
            this.listAdapter.notifyDataSetChanged();
            return;
          }
          if ((paramInt != NotificationCenter.didReceivedNewMessages) || (((Long)paramVarArgs[0]).longValue() != this.dialog_id)) {
            break;
          }
          paramVarArgs = (ArrayList)paramVarArgs[1];
          paramInt = 0;
          while (paramInt < paramVarArgs.size())
          {
            localObject = (MessageObject)paramVarArgs.get(paramInt);
            if ((this.currentEncryptedChat != null) && (((MessageObject)localObject).messageOwner.action != null) && ((((MessageObject)localObject).messageOwner.action instanceof TLRPC.TL_messageEncryptedAction)) && ((((MessageObject)localObject).messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)))
            {
              localObject = (TLRPC.TL_decryptedMessageActionSetMessageTTL)((MessageObject)localObject).messageOwner.action.encryptedAction;
              if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
              }
            }
            paramInt += 1;
          }
        }
      }
    }
  }
  
  public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
  {
    Bundle localBundle;
    int i;
    if (paramLong != 0L)
    {
      localBundle = new Bundle();
      localBundle.putBoolean("scrollToTopOnResume", true);
      i = (int)paramLong;
      if (i == 0) {
        break label77;
      }
      if (i <= 0) {
        break label58;
      }
      localBundle.putInt("user_id", i);
    }
    while (!MessagesController.checkCanOpenChat(localBundle, paramDialogsActivity))
    {
      return;
      label58:
      if (i < 0)
      {
        localBundle.putInt("chat_id", -i);
        continue;
        label77:
        localBundle.putInt("enc_id", (int)(paramLong >> 32));
      }
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
    presentFragment(new ChatActivity(localBundle), true);
    removeSelfFromStack();
    paramDialogsActivity = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
    SendMessagesHelper.getInstance().sendMessage(paramDialogsActivity, paramLong, null, null, null);
  }
  
  public float getAnimationProgress()
  {
    return this.animationProgress;
  }
  
  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    if (paramFileLocation == null) {
      return null;
    }
    Object localObject1 = null;
    Object localObject2;
    if (this.user_id != 0)
    {
      localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      paramMessageObject = (MessageObject)localObject1;
      if (localObject2 != null)
      {
        paramMessageObject = (MessageObject)localObject1;
        if (((TLRPC.User)localObject2).photo != null)
        {
          paramMessageObject = (MessageObject)localObject1;
          if (((TLRPC.User)localObject2).photo.photo_big != null) {
            paramMessageObject = ((TLRPC.User)localObject2).photo.photo_big;
          }
        }
      }
      label73:
      if ((paramMessageObject == null) || (paramMessageObject.local_id != paramFileLocation.local_id) || (paramMessageObject.volume_id != paramFileLocation.volume_id) || (paramMessageObject.dc_id != paramFileLocation.dc_id)) {
        break label294;
      }
      paramMessageObject = new int[2];
      this.avatarImage.getLocationInWindow(paramMessageObject);
      paramFileLocation = new PhotoViewer.PlaceProviderObject();
      paramFileLocation.viewX = paramMessageObject[0];
      paramFileLocation.viewY = (paramMessageObject[1] - AndroidUtilities.statusBarHeight);
      paramFileLocation.parentView = this.avatarImage;
      paramFileLocation.imageReceiver = this.avatarImage.getImageReceiver();
      if (this.user_id == 0) {
        break label296;
      }
      paramFileLocation.dialogId = this.user_id;
    }
    for (;;)
    {
      paramFileLocation.thumb = paramFileLocation.imageReceiver.getBitmap();
      paramFileLocation.size = -1;
      paramFileLocation.radius = this.avatarImage.getImageReceiver().getRoundRadius();
      paramFileLocation.scale = this.avatarImage.getScaleX();
      return paramFileLocation;
      paramMessageObject = (MessageObject)localObject1;
      if (this.chat_id == 0) {
        break label73;
      }
      localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
      paramMessageObject = (MessageObject)localObject1;
      if (localObject2 == null) {
        break label73;
      }
      paramMessageObject = (MessageObject)localObject1;
      if (((TLRPC.Chat)localObject2).photo == null) {
        break label73;
      }
      paramMessageObject = (MessageObject)localObject1;
      if (((TLRPC.Chat)localObject2).photo.photo_big == null) {
        break label73;
      }
      paramMessageObject = ((TLRPC.Chat)localObject2).photo.photo_big;
      break label73;
      label294:
      break;
      label296:
      if (this.chat_id != 0) {
        paramFileLocation.dialogId = (-this.chat_id);
      }
    }
  }
  
  public int getSelectedCount()
  {
    return 0;
  }
  
  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    return null;
  }
  
  public boolean isChat()
  {
    return this.chat_id != 0;
  }
  
  public boolean isPhotoChecked(int paramInt)
  {
    return false;
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (this.chat_id != 0) {
      this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }
  
  protected AnimatorSet onCustomTransitionAnimation(boolean paramBoolean, final Runnable paramRunnable)
  {
    if ((this.playProfileAnimation) && (this.allowProfileAnimation))
    {
      final AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.setDuration(180L);
      if (Build.VERSION.SDK_INT > 15) {
        this.listView.setLayerType(2, null);
      }
      Object localObject = this.actionBar.createMenu();
      if ((((ActionBarMenu)localObject).getItem(10) == null) && (this.animatingItem == null)) {
        this.animatingItem = ((ActionBarMenu)localObject).addItem(10, 2130837708);
      }
      int i;
      float f1;
      label427:
      SimpleTextView localSimpleTextView;
      if (paramBoolean)
      {
        localObject = (FrameLayout.LayoutParams)this.onlineTextView[1].getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).rightMargin = ((int)(-21.0F * AndroidUtilities.density + AndroidUtilities.dp(8.0F)));
        this.onlineTextView[1].setLayoutParams((ViewGroup.LayoutParams)localObject);
        i = (int)Math.ceil(AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0F) + 21.0F * AndroidUtilities.density);
        f1 = this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString());
        float f2 = this.nameTextView[1].getSideDrawablesSize();
        localObject = (FrameLayout.LayoutParams)this.nameTextView[1].getLayoutParams();
        if (i < f1 * 1.12F + f2)
        {
          ((FrameLayout.LayoutParams)localObject).width = ((int)Math.ceil(i / 1.12F));
          this.nameTextView[1].setLayoutParams((ViewGroup.LayoutParams)localObject);
          this.initialAnimationExtraHeight = AndroidUtilities.dp(88.0F);
          this.fragmentView.setBackgroundColor(0);
          setAnimationProgress(0.0F);
          localObject = new ArrayList();
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this, "animationProgress", new float[] { 0.0F, 1.0F }));
          if (this.writeButton != null)
          {
            this.writeButton.setScaleX(0.2F);
            this.writeButton.setScaleY(0.2F);
            this.writeButton.setAlpha(0.0F);
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 1.0F }));
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 1.0F }));
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 1.0F }));
          }
          i = 0;
          if (i >= 2) {
            break label590;
          }
          localSimpleTextView = this.onlineTextView[i];
          if (i != 0) {
            break label570;
          }
          f1 = 1.0F;
          label449:
          localSimpleTextView.setAlpha(f1);
          localSimpleTextView = this.nameTextView[i];
          if (i != 0) {
            break label575;
          }
          f1 = 1.0F;
          label471:
          localSimpleTextView.setAlpha(f1);
          localSimpleTextView = this.onlineTextView[i];
          if (i != 0) {
            break label580;
          }
          f1 = 0.0F;
          label493:
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
          localSimpleTextView = this.nameTextView[i];
          if (i != 0) {
            break label585;
          }
        }
        label570:
        label575:
        label580:
        label585:
        for (f1 = 0.0F;; f1 = 1.0F)
        {
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
          i += 1;
          break label427;
          ((FrameLayout.LayoutParams)localObject).width = -2;
          break;
          f1 = 0.0F;
          break label449;
          f1 = 0.0F;
          break label471;
          f1 = 1.0F;
          break label493;
        }
        label590:
        if (this.animatingItem != null)
        {
          this.animatingItem.setAlpha(1.0F);
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", new float[] { 0.0F }));
        }
        localAnimatorSet.playTogether((Collection)localObject);
      }
      for (;;)
      {
        localAnimatorSet.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if (Build.VERSION.SDK_INT > 15) {
              ProfileActivity.this.listView.setLayerType(0, null);
            }
            if (ProfileActivity.this.animatingItem != null)
            {
              ProfileActivity.this.actionBar.createMenu().clearItems();
              ProfileActivity.access$5502(ProfileActivity.this, null);
            }
            paramRunnable.run();
          }
        });
        localAnimatorSet.setInterpolator(new DecelerateInterpolator());
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            localAnimatorSet.start();
          }
        }, 50L);
        return localAnimatorSet;
        this.initialAnimationExtraHeight = this.extraHeight;
        localObject = new ArrayList();
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this, "animationProgress", new float[] { 1.0F, 0.0F }));
        if (this.writeButton != null)
        {
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 0.2F }));
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 0.2F }));
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 0.0F }));
        }
        i = 0;
        if (i < 2)
        {
          localSimpleTextView = this.onlineTextView[i];
          if (i == 0)
          {
            f1 = 1.0F;
            label826:
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
            localSimpleTextView = this.nameTextView[i];
            if (i != 0) {
              break label898;
            }
          }
          label898:
          for (f1 = 1.0F;; f1 = 0.0F)
          {
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
            i += 1;
            break;
            f1 = 0.0F;
            break label826;
          }
        }
        if (this.animatingItem != null)
        {
          this.animatingItem.setAlpha(0.0F);
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", new float[] { 1.0F }));
        }
        localAnimatorSet.playTogether((Collection)localObject);
      }
    }
    return null;
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    if (this.listView != null) {
      this.listView.invalidateViews();
    }
  }
  
  public boolean onFragmentCreate()
  {
    this.user_id = this.arguments.getInt("user_id", 0);
    this.chat_id = getArguments().getInt("chat_id", 0);
    final Object localObject;
    if (this.user_id != 0)
    {
      this.dialog_id = this.arguments.getLong("dialog_id", 0L);
      if (this.dialog_id != 0L) {
        this.currentEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(this.dialog_id >> 32)));
      }
      localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      if (localObject == null) {
        return false;
      }
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.botInfoDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.userInfoDidLoaded);
      if (this.currentEncryptedChat != null) {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
      }
      this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.user_id));
      if (((TLRPC.User)localObject).bot) {
        BotQuery.loadBotInfo(((TLRPC.User)localObject).id, true, this.classGuid);
      }
      MessagesController.getInstance().loadFullUser(MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
      this.participantsMap = null;
      label257:
      if (this.dialog_id == 0L) {
        break label516;
      }
      SharedMediaQuery.getMediaCount(this.dialog_id, 0, this.classGuid, true);
    }
    for (;;)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaCountDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
      updateRowsIds();
      return true;
      if (this.chat_id == 0) {
        break;
      }
      this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
      if (this.currentChat == null)
      {
        localObject = new Semaphore(0);
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
        {
          public void run()
          {
            ProfileActivity.access$402(ProfileActivity.this, MessagesStorage.getInstance().getChat(ProfileActivity.this.chat_id));
            localObject.release();
          }
        });
      }
      try
      {
        ((Semaphore)localObject).acquire();
        if (this.currentChat == null) {
          break;
        }
        MessagesController.getInstance().putChat(this.currentChat, true);
        if (this.currentChat.megagroup)
        {
          getChannelParticipants(true);
          NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
          this.sortedUsers = new ArrayList();
          updateOnlineCount();
          this.avatarUpdater = new AvatarUpdater();
          this.avatarUpdater.delegate = new AvatarUpdater.AvatarUpdaterDelegate()
          {
            public void didUploadedPhoto(TLRPC.InputFile paramAnonymousInputFile, TLRPC.PhotoSize paramAnonymousPhotoSize1, TLRPC.PhotoSize paramAnonymousPhotoSize2)
            {
              if (ProfileActivity.this.chat_id != 0) {
                MessagesController.getInstance().changeChatAvatar(ProfileActivity.this.chat_id, paramAnonymousInputFile);
              }
            }
          };
          this.avatarUpdater.parentFragment = this;
          if (!ChatObject.isChannel(this.currentChat)) {
            break label257;
          }
          MessagesController.getInstance().loadFullChat(this.chat_id, this.classGuid, true);
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
          continue;
          this.participantsMap = null;
        }
      }
      label516:
      if (this.user_id != 0)
      {
        SharedMediaQuery.getMediaCount(this.user_id, 0, this.classGuid, true);
      }
      else if (this.chat_id > 0)
      {
        SharedMediaQuery.getMediaCount(-this.chat_id, 0, this.classGuid, true);
        if (this.mergeDialogId != 0L) {
          SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
        }
      }
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaCountDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    if (this.user_id != 0)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.botInfoDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.userInfoDidLoaded);
      MessagesController.getInstance().cancelLoadFullUser(this.user_id);
      if (this.currentEncryptedChat != null) {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
      }
    }
    while (this.chat_id == 0) {
      return;
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    this.avatarUpdater.clear();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    updateProfileData();
    fixLayout();
  }
  
  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean2) && (this.playProfileAnimation) && (this.allowProfileAnimation)) {
      this.openAnimationInProgress = false;
    }
    NotificationCenter.getInstance().setAnimationInProgress(false);
  }
  
  protected void onTransitionAnimationStart(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean2) && (this.playProfileAnimation) && (this.allowProfileAnimation)) {
      this.openAnimationInProgress = true;
    }
    NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded });
    NotificationCenter.getInstance().setAnimationInProgress(true);
  }
  
  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.chat_id != 0)
    {
      MessagesController.getInstance().loadChatInfo(this.chat_id, null, false);
      if (this.avatarUpdater != null) {
        this.avatarUpdater.currentPicturePath = paramBundle.getString("path");
      }
    }
  }
  
  public void saveSelfArgs(Bundle paramBundle)
  {
    if ((this.chat_id != 0) && (this.avatarUpdater != null) && (this.avatarUpdater.currentPicturePath != null)) {
      paramBundle.putString("path", this.avatarUpdater.currentPicturePath);
    }
  }
  
  public void sendButtonPressed(int paramInt) {}
  
  public void setAnimationProgress(float paramFloat)
  {
    this.animationProgress = paramFloat;
    this.listView.setAlpha(paramFloat);
    this.listView.setTranslationX(AndroidUtilities.dp(48.0F) - AndroidUtilities.dp(48.0F) * paramFloat);
    int i1;
    int j;
    int k;
    int m;
    int n;
    label186:
    int i2;
    if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      i1 = AvatarDrawable.getProfileBackColorForId(i);
      i = Color.red(-11371101);
      j = Color.green(-11371101);
      k = Color.blue(-11371101);
      m = (int)((Color.red(i1) - i) * paramFloat);
      n = (int)((Color.green(i1) - j) * paramFloat);
      i1 = (int)((Color.blue(i1) - k) * paramFloat);
      this.topView.setBackgroundColor(Color.rgb(i + m, j + n, k + i1));
      if ((this.user_id == 0) && ((!ChatObject.isChannel(this.chat_id)) || (this.currentChat.megagroup))) {
        break label283;
      }
      i = 5;
      i = AvatarDrawable.getProfileTextColorForId(i);
      j = Color.red(-2758409);
      k = Color.green(-2758409);
      m = Color.blue(-2758409);
      n = (int)((Color.red(i) - j) * paramFloat);
      i1 = (int)((Color.green(i) - k) * paramFloat);
      i2 = (int)((Color.blue(i) - m) * paramFloat);
      i = 0;
      label254:
      if (i >= 2) {
        break label320;
      }
      if (this.onlineTextView[i] != null) {
        break label291;
      }
    }
    for (;;)
    {
      i += 1;
      break label254;
      i = this.chat_id;
      break;
      label283:
      i = this.chat_id;
      break label186;
      label291:
      this.onlineTextView[i].setTextColor(Color.rgb(j + n, k + i1, m + i2));
    }
    label320:
    this.extraHeight = ((int)(this.initialAnimationExtraHeight * paramFloat));
    if (this.user_id != 0)
    {
      i = this.user_id;
      j = AvatarDrawable.getProfileColorForId(i);
      if (this.user_id == 0) {
        break label465;
      }
    }
    label465:
    for (int i = this.user_id;; i = this.chat_id)
    {
      i = AvatarDrawable.getColorForId(i);
      if (j != i)
      {
        k = (int)((Color.red(j) - Color.red(i)) * paramFloat);
        m = (int)((Color.green(j) - Color.green(i)) * paramFloat);
        j = (int)((Color.blue(j) - Color.blue(i)) * paramFloat);
        this.avatarDrawable.setColor(Color.rgb(Color.red(i) + k, Color.green(i) + m, Color.blue(i) + j));
        this.avatarImage.invalidate();
      }
      needLayout();
      return;
      i = this.chat_id;
      break;
    }
  }
  
  public void setChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
    if ((this.info != null) && (this.info.migrated_from_chat_id != 0)) {
      this.mergeDialogId = (-this.info.migrated_from_chat_id);
    }
    fetchUsersFromChannelInfo();
  }
  
  public void setPhotoChecked(int paramInt) {}
  
  public void setPlayProfileAnimation(boolean paramBoolean)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    if ((!AndroidUtilities.isTablet()) && (localSharedPreferences.getBoolean("view_animations", true))) {
      this.playProfileAnimation = paramBoolean;
    }
  }
  
  public void updatePhotoAtIndex(int paramInt) {}
  
  public void willHidePhotoViewer()
  {
    this.avatarImage.getImageReceiver().setVisible(true, true);
  }
  
  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt) {}
  
  private class ListAdapter
    extends RecyclerView.Adapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return ProfileActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt == ProfileActivity.this.emptyRow) || (paramInt == ProfileActivity.this.emptyRowChat) || (paramInt == ProfileActivity.this.emptyRowChat2)) {}
      do
      {
        return 0;
        if ((paramInt == ProfileActivity.this.sectionRow) || (paramInt == ProfileActivity.this.userSectionRow)) {
          return 1;
        }
        if ((paramInt == ProfileActivity.this.phoneRow) || (paramInt == ProfileActivity.this.usernameRow) || (paramInt == ProfileActivity.this.channelNameRow)) {
          return 2;
        }
        if ((paramInt == ProfileActivity.this.leaveChannelRow) || (paramInt == ProfileActivity.this.sharedMediaRow) || (paramInt == ProfileActivity.this.settingsTimerRow) || (paramInt == ProfileActivity.this.settingsNotificationsRow) || (paramInt == ProfileActivity.this.startSecretChatRow) || (paramInt == ProfileActivity.this.settingsKeyRow) || (paramInt == ProfileActivity.this.membersRow) || (paramInt == ProfileActivity.this.managementRow) || (paramInt == ProfileActivity.this.blockedUsersRow) || (paramInt == ProfileActivity.this.convertRow) || (paramInt == ProfileActivity.this.addMemberRow)) {
          return 3;
        }
        if ((paramInt > ProfileActivity.this.emptyRowChat2) && (paramInt < ProfileActivity.this.membersEndRow)) {
          return 4;
        }
        if (paramInt == ProfileActivity.this.membersSectionRow) {
          return 5;
        }
        if (paramInt == ProfileActivity.this.convertHelpRow) {
          return 6;
        }
        if (paramInt == ProfileActivity.this.loadMoreMembersRow) {
          return 7;
        }
      } while ((paramInt != ProfileActivity.this.userInfoRow) && (paramInt != ProfileActivity.this.channelInfoRow));
      return 8;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      int j = 1;
      int i;
      switch (paramViewHolder.getItemViewType())
      {
      case 1: 
      case 5: 
      case 6: 
      case 7: 
      default: 
        i = 0;
        if (i != 0)
        {
          i = 0;
          if (ProfileActivity.this.user_id == 0) {
            break label1825;
          }
          if ((paramInt != ProfileActivity.this.phoneRow) && (paramInt != ProfileActivity.this.settingsTimerRow) && (paramInt != ProfileActivity.this.settingsKeyRow) && (paramInt != ProfileActivity.this.settingsNotificationsRow) && (paramInt != ProfileActivity.this.sharedMediaRow) && (paramInt != ProfileActivity.this.startSecretChatRow) && (paramInt != ProfileActivity.this.usernameRow) && (paramInt != ProfileActivity.this.userInfoRow)) {
            break label1820;
          }
          i = 1;
          label164:
          if (i == 0) {
            break label1977;
          }
          if (paramViewHolder.itemView.getBackground() == null) {
            paramViewHolder.itemView.setBackgroundResource(2130837796);
          }
        }
        break;
      }
      label763:
      label1485:
      label1525:
      label1609:
      label1643:
      label1699:
      label1820:
      label1825:
      label1977:
      while (paramViewHolder.itemView.getBackground() == null)
      {
        return;
        if ((paramInt == ProfileActivity.this.emptyRowChat) || (paramInt == ProfileActivity.this.emptyRowChat2))
        {
          ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(8.0F));
          i = j;
          break;
        }
        ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(36.0F));
        i = j;
        break;
        Object localObject2 = (TextDetailCell)paramViewHolder.itemView;
        if (paramInt == ProfileActivity.this.phoneRow)
        {
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
          if ((((TLRPC.User)localObject1).phone != null) && (((TLRPC.User)localObject1).phone.length() != 0)) {}
          for (localObject1 = PhoneFormat.getInstance().format("+" + ((TLRPC.User)localObject1).phone);; localObject1 = LocaleController.getString("NumberUnknown", 2131166043))
          {
            ((TextDetailCell)localObject2).setTextAndValueAndIcon((String)localObject1, LocaleController.getString("PhoneMobile", 2131166106), 2130837879);
            i = j;
            break;
          }
        }
        if (paramInt == ProfileActivity.this.usernameRow)
        {
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
          if ((localObject1 != null) && (((TLRPC.User)localObject1).username != null) && (((TLRPC.User)localObject1).username.length() != 0)) {}
          for (localObject1 = "@" + ((TLRPC.User)localObject1).username;; localObject1 = "-")
          {
            ((TextDetailCell)localObject2).setTextAndValue((String)localObject1, LocaleController.getString("Username", 2131166365));
            i = j;
            break;
          }
        }
        i = j;
        if (paramInt != ProfileActivity.this.channelNameRow) {
          break;
        }
        if ((ProfileActivity.this.currentChat != null) && (ProfileActivity.this.currentChat.username != null) && (ProfileActivity.this.currentChat.username.length() != 0)) {}
        for (Object localObject1 = "@" + ProfileActivity.this.currentChat.username;; localObject1 = "-")
        {
          ((TextDetailCell)localObject2).setTextAndValue((String)localObject1, "telegram.me/" + ProfileActivity.this.currentChat.username);
          i = j;
          break;
        }
        localObject2 = (TextCell)paramViewHolder.itemView;
        ((TextCell)localObject2).setTextColor(-14606047);
        if (paramInt == ProfileActivity.this.sharedMediaRow)
        {
          if (ProfileActivity.this.totalMediaCount == -1)
          {
            localObject1 = LocaleController.getString("Loading", 2131165834);
            if ((ProfileActivity.this.user_id == 0) || (UserConfig.getClientUserId() != ProfileActivity.this.user_id)) {
              break label763;
            }
            ((TextCell)localObject2).setTextAndValueAndIcon(LocaleController.getString("SharedMedia", 2131166285), (String)localObject1, 2130837949);
            i = j;
            break;
          }
          int k = ProfileActivity.this.totalMediaCount;
          if (ProfileActivity.this.totalMediaCountMerge != -1) {}
          for (i = ProfileActivity.this.totalMediaCountMerge;; i = 0)
          {
            localObject1 = String.format("%d", new Object[] { Integer.valueOf(i + k) });
            break;
          }
          ((TextCell)localObject2).setTextAndValue(LocaleController.getString("SharedMedia", 2131166285), (String)localObject1);
          i = j;
          break;
        }
        if (paramInt == ProfileActivity.this.settingsTimerRow)
        {
          localObject1 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(ProfileActivity.this.dialog_id >> 32)));
          if (((TLRPC.EncryptedChat)localObject1).ttl == 0) {}
          for (localObject1 = LocaleController.getString("ShortMessageLifetimeForever", 2131166289);; localObject1 = AndroidUtilities.formatTTLString(((TLRPC.EncryptedChat)localObject1).ttl))
          {
            ((TextCell)localObject2).setTextAndValue(LocaleController.getString("MessageLifetime", 2131165872), (String)localObject1);
            i = j;
            break;
          }
        }
        if (paramInt == ProfileActivity.this.settingsNotificationsRow)
        {
          ((TextCell)localObject2).setTextAndIcon(LocaleController.getString("NotificationsAndSounds", 2131166031), 2130837949);
          i = j;
          break;
        }
        if (paramInt == ProfileActivity.this.startSecretChatRow)
        {
          ((TextCell)localObject2).setText(LocaleController.getString("StartEncryptedChat", 2131166307));
          ((TextCell)localObject2).setTextColor(-13129447);
          i = j;
          break;
        }
        if (paramInt == ProfileActivity.this.settingsKeyRow)
        {
          localObject1 = new IdenticonDrawable();
          ((IdenticonDrawable)localObject1).setEncryptedChat(MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(ProfileActivity.this.dialog_id >> 32))));
          ((TextCell)localObject2).setTextAndValueDrawable(LocaleController.getString("EncryptionKey", 2131165613), (Drawable)localObject1);
          i = j;
          break;
        }
        if (paramInt == ProfileActivity.this.leaveChannelRow)
        {
          ((TextCell)localObject2).setTextColor(-1229511);
          ((TextCell)localObject2).setText(LocaleController.getString("LeaveChannel", 2131165815));
          i = j;
          break;
        }
        if (paramInt == ProfileActivity.this.convertRow)
        {
          ((TextCell)localObject2).setText(LocaleController.getString("UpgradeGroup", 2131166361));
          ((TextCell)localObject2).setTextColor(-13129447);
          i = j;
          break;
        }
        if (paramInt == ProfileActivity.this.membersRow)
        {
          if (ProfileActivity.this.info != null)
          {
            ((TextCell)localObject2).setTextAndValue(LocaleController.getString("ChannelMembers", 2131165436), String.format("%d", new Object[] { Integer.valueOf(ProfileActivity.this.info.participants_count) }));
            i = j;
            break;
          }
          ((TextCell)localObject2).setText(LocaleController.getString("ChannelMembers", 2131165436));
          i = j;
          break;
        }
        if (paramInt == ProfileActivity.this.managementRow)
        {
          if (ProfileActivity.this.info != null)
          {
            ((TextCell)localObject2).setTextAndValue(LocaleController.getString("ChannelAdministrators", 2131165409), String.format("%d", new Object[] { Integer.valueOf(ProfileActivity.this.info.admins_count) }));
            i = j;
            break;
          }
          ((TextCell)localObject2).setText(LocaleController.getString("ChannelAdministrators", 2131165409));
          i = j;
          break;
        }
        if (paramInt == ProfileActivity.this.blockedUsersRow)
        {
          if (ProfileActivity.this.info != null)
          {
            ((TextCell)localObject2).setTextAndValue(LocaleController.getString("ChannelBlockedUsers", 2131165414), String.format("%d", new Object[] { Integer.valueOf(ProfileActivity.this.info.kicked_count) }));
            i = j;
            break;
          }
          ((TextCell)localObject2).setText(LocaleController.getString("ChannelBlockedUsers", 2131165414));
          i = j;
          break;
        }
        i = j;
        if (paramInt != ProfileActivity.this.addMemberRow) {
          break;
        }
        if (ProfileActivity.this.chat_id > 0)
        {
          ((TextCell)localObject2).setText(LocaleController.getString("AddMember", 2131165262));
          i = j;
          break;
        }
        ((TextCell)localObject2).setText(LocaleController.getString("AddRecipient", 2131165264));
        i = j;
        break;
        localObject2 = (UserCell)paramViewHolder.itemView;
        TLRPC.ChannelParticipant localChannelParticipant;
        if (!ProfileActivity.this.sortedUsers.isEmpty())
        {
          localObject1 = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(((Integer)ProfileActivity.this.sortedUsers.get(paramInt - ProfileActivity.this.emptyRowChat2 - 1)).intValue());
          i = j;
          if (localObject1 == null) {
            break;
          }
          if (!(localObject1 instanceof TLRPC.TL_chatChannelParticipant)) {
            break label1643;
          }
          localChannelParticipant = ((TLRPC.TL_chatChannelParticipant)localObject1).channelParticipant;
          if (!(localChannelParticipant instanceof TLRPC.TL_channelParticipantCreator)) {
            break label1609;
          }
          ((UserCell)localObject2).setIsAdmin(1);
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject1).user_id));
          if (paramInt != ProfileActivity.this.emptyRowChat2 + 1) {
            break label1699;
          }
        }
        for (i = 2130837822;; i = 0)
        {
          ((UserCell)localObject2).setData((TLObject)localObject1, null, null, i);
          i = j;
          break;
          localObject1 = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramInt - ProfileActivity.this.emptyRowChat2 - 1);
          break label1485;
          if (((localChannelParticipant instanceof TLRPC.TL_channelParticipantEditor)) || ((localChannelParticipant instanceof TLRPC.TL_channelParticipantModerator)))
          {
            ((UserCell)localObject2).setIsAdmin(2);
            break label1525;
          }
          ((UserCell)localObject2).setIsAdmin(0);
          break label1525;
          if ((localObject1 instanceof TLRPC.TL_chatParticipantCreator))
          {
            ((UserCell)localObject2).setIsAdmin(1);
            break label1525;
          }
          if ((ProfileActivity.this.currentChat.admins_enabled) && ((localObject1 instanceof TLRPC.TL_chatParticipantAdmin)))
          {
            ((UserCell)localObject2).setIsAdmin(2);
            break label1525;
          }
          ((UserCell)localObject2).setIsAdmin(0);
          break label1525;
        }
        localObject2 = (AboutLinkCell)paramViewHolder.itemView;
        if (paramInt == ProfileActivity.this.userInfoRow)
        {
          ((AboutLinkCell)localObject2).setTextAndIcon(MessagesController.getInstance().getUserAbout(ProfileActivity.this.user_id), 2130837565);
          i = j;
          break;
        }
        i = j;
        if (paramInt != ProfileActivity.this.channelInfoRow) {
          break;
        }
        for (localObject1 = ProfileActivity.this.info.about; ((String)localObject1).contains("\n\n\n"); localObject1 = ((String)localObject1).replace("\n\n\n", "\n\n")) {}
        ((AboutLinkCell)localObject2).setTextAndIcon((String)localObject1, 2130837565);
        i = j;
        break;
        i = 0;
        break label164;
        if (ProfileActivity.this.chat_id == 0) {
          break label164;
        }
        if ((paramInt == ProfileActivity.this.convertRow) || (paramInt == ProfileActivity.this.settingsNotificationsRow) || (paramInt == ProfileActivity.this.sharedMediaRow) || ((paramInt > ProfileActivity.this.emptyRowChat2) && (paramInt < ProfileActivity.this.membersEndRow)) || (paramInt == ProfileActivity.this.addMemberRow) || (paramInt == ProfileActivity.this.channelNameRow) || (paramInt == ProfileActivity.this.leaveChannelRow) || (paramInt == ProfileActivity.this.membersRow) || (paramInt == ProfileActivity.this.managementRow) || (paramInt == ProfileActivity.this.blockedUsersRow) || (paramInt == ProfileActivity.this.channelInfoRow)) {}
        for (i = 1;; i = 0) {
          break;
        }
      }
      paramViewHolder.itemView.setBackgroundDrawable(null);
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
        return new Holder(paramViewGroup);
        paramViewGroup = new EmptyCell(this.mContext);
        continue;
        paramViewGroup = new DividerCell(this.mContext);
        paramViewGroup.setPadding(AndroidUtilities.dp(72.0F), 0, 0, 0);
        continue;
        paramViewGroup = new TextDetailCell(this.mContext)
        {
          public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
          {
            if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramAnonymousMotionEvent.getAction() == 0) || (paramAnonymousMotionEvent.getAction() == 2))) {
              getBackground().setHotspot(paramAnonymousMotionEvent.getX(), paramAnonymousMotionEvent.getY());
            }
            return super.onTouchEvent(paramAnonymousMotionEvent);
          }
        };
        continue;
        paramViewGroup = new TextCell(this.mContext)
        {
          public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
          {
            if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramAnonymousMotionEvent.getAction() == 0) || (paramAnonymousMotionEvent.getAction() == 2))) {
              getBackground().setHotspot(paramAnonymousMotionEvent.getX(), paramAnonymousMotionEvent.getY());
            }
            return super.onTouchEvent(paramAnonymousMotionEvent);
          }
        };
        continue;
        paramViewGroup = new UserCell(this.mContext, 61, 0, true)
        {
          public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
          {
            if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramAnonymousMotionEvent.getAction() == 0) || (paramAnonymousMotionEvent.getAction() == 2))) {
              getBackground().setHotspot(paramAnonymousMotionEvent.getX(), paramAnonymousMotionEvent.getY());
            }
            return super.onTouchEvent(paramAnonymousMotionEvent);
          }
        };
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        TextInfoPrivacyCell localTextInfoPrivacyCell = (TextInfoPrivacyCell)paramViewGroup;
        localTextInfoPrivacyCell.setBackgroundResource(2130837688);
        localTextInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ConvertGroupInfo", 2131165527, new Object[] { LocaleController.formatPluralString("Members", MessagesController.getInstance().maxMegagroupCount) })));
        continue;
        paramViewGroup = new LoadingCell(this.mContext);
        continue;
        paramViewGroup = new AboutLinkCell(this.mContext);
        ((AboutLinkCell)paramViewGroup).setDelegate(new AboutLinkCell.AboutLinkCellDelegate()
        {
          public void didPressUrl(String paramAnonymousString)
          {
            if (paramAnonymousString.startsWith("@")) {
              MessagesController.openByUserName(paramAnonymousString.substring(1), ProfileActivity.this, 0);
            }
            Object localObject;
            do
            {
              do
              {
                return;
                if (paramAnonymousString.startsWith("#"))
                {
                  localObject = new DialogsActivity(null);
                  ((DialogsActivity)localObject).setSearchString(paramAnonymousString);
                  ProfileActivity.this.presentFragment((BaseFragment)localObject);
                  return;
                }
              } while ((!paramAnonymousString.startsWith("/")) || (ProfileActivity.this.parentLayout.fragmentsStack.size() <= 1));
              localObject = (BaseFragment)ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
            } while (!(localObject instanceof ChatActivity));
            ProfileActivity.this.finishFragment();
            ((ChatActivity)localObject).chatActivityEnterView.setCommand(null, paramAnonymousString, false, false);
          }
        });
      }
    }
    
    private class Holder
      extends RecyclerView.ViewHolder
    {
      public Holder(View paramView)
      {
        super();
      }
    }
  }
  
  private class TopView
    extends View
  {
    private int currentColor;
    private Paint paint = new Paint();
    
    public TopView(Context paramContext)
    {
      super();
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int i = getMeasuredHeight() - AndroidUtilities.dp(91.0F);
      paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), ProfileActivity.this.extraHeight + i, this.paint);
      if (ProfileActivity.this.parentLayout != null) {
        ProfileActivity.this.parentLayout.drawHeaderShadow(paramCanvas, ProfileActivity.this.extraHeight + i);
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      paramInt2 = View.MeasureSpec.getSize(paramInt1);
      int i = ActionBar.getCurrentActionBarHeight();
      if (ProfileActivity.this.actionBar.getOccupyStatusBar()) {}
      for (paramInt1 = AndroidUtilities.statusBarHeight;; paramInt1 = 0)
      {
        setMeasuredDimension(paramInt2, paramInt1 + i + AndroidUtilities.dp(91.0F));
        return;
      }
    }
    
    public void setBackgroundColor(int paramInt)
    {
      if (paramInt != this.currentColor)
      {
        this.paint.setColor(paramInt);
        invalidate();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ProfileActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */