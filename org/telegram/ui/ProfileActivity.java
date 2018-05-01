package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.TextUtils;
import android.util.LongSparseArray;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
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
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_channelParticipantBanned;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipant;
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
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
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
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.voip.VoIPHelper;

public class ProfileActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate
{
  private static final int add_contact = 1;
  private static final int add_shortcut = 14;
  private static final int block_contact = 2;
  private static final int call_item = 15;
  private static final int convert_to_supergroup = 13;
  private static final int delete_contact = 5;
  private static final int edit_channel = 12;
  private static final int edit_contact = 4;
  private static final int edit_name = 8;
  private static final int invite_to_group = 9;
  private static final int leave_group = 7;
  private static final int search_members = 16;
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
  private int banFromGroup;
  private TLRPC.BotInfo botInfo;
  private ActionBarMenuItem callItem;
  private int channelInfoRow;
  private int channelNameRow;
  private int chat_id;
  private int convertHelpRow;
  private int convertRow;
  private boolean creatingChat;
  private TLRPC.ChannelParticipant currentChannelParticipant;
  private TLRPC.Chat currentChat;
  private TLRPC.EncryptedChat currentEncryptedChat;
  private long dialog_id;
  private ActionBarMenuItem editItem;
  private int emptyRow;
  private int emptyRowChat;
  private int emptyRowChat2;
  private int extraHeight;
  private int groupsInCommonRow;
  private TLRPC.ChatFull info;
  private int initialAnimationExtraHeight;
  private boolean isBot;
  private LinearLayoutManager layoutManager;
  private int leaveChannelRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int loadMoreMembersRow;
  private boolean loadingUsers;
  private int membersEndRow;
  private int membersRow;
  private int membersSectionRow;
  private long mergeDialogId;
  private SimpleTextView[] nameTextView = new SimpleTextView[2];
  private int onlineCount = -1;
  private SimpleTextView[] onlineTextView = new SimpleTextView[2];
  private boolean openAnimationInProgress;
  private SparseArray<TLRPC.ChatParticipant> participantsMap = new SparseArray();
  private int phoneRow;
  private boolean playProfileAnimation;
  private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider()
  {
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramAnonymousMessageObject, TLRPC.FileLocation paramAnonymousFileLocation, int paramAnonymousInt)
    {
      Object localObject1 = null;
      paramAnonymousInt = 0;
      if (paramAnonymousFileLocation == null) {
        localObject2 = localObject1;
      }
      Object localObject3;
      label96:
      do
      {
        do
        {
          do
          {
            do
            {
              return (PhotoViewer.PlaceProviderObject)localObject2;
              localObject2 = null;
              if (ProfileActivity.this.user_id == 0) {
                break;
              }
              localObject3 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
              paramAnonymousMessageObject = (MessageObject)localObject2;
              if (localObject3 != null)
              {
                paramAnonymousMessageObject = (MessageObject)localObject2;
                if (((TLRPC.User)localObject3).photo != null)
                {
                  paramAnonymousMessageObject = (MessageObject)localObject2;
                  if (((TLRPC.User)localObject3).photo.photo_big != null) {
                    paramAnonymousMessageObject = ((TLRPC.User)localObject3).photo.photo_big;
                  }
                }
              }
              localObject2 = localObject1;
            } while (paramAnonymousMessageObject == null);
            localObject2 = localObject1;
          } while (paramAnonymousMessageObject.local_id != paramAnonymousFileLocation.local_id);
          localObject2 = localObject1;
        } while (paramAnonymousMessageObject.volume_id != paramAnonymousFileLocation.volume_id);
        localObject2 = localObject1;
      } while (paramAnonymousMessageObject.dc_id != paramAnonymousFileLocation.dc_id);
      paramAnonymousMessageObject = new int[2];
      ProfileActivity.this.avatarImage.getLocationInWindow(paramAnonymousMessageObject);
      Object localObject2 = new PhotoViewer.PlaceProviderObject();
      ((PhotoViewer.PlaceProviderObject)localObject2).viewX = paramAnonymousMessageObject[0];
      int i = paramAnonymousMessageObject[1];
      if (Build.VERSION.SDK_INT >= 21)
      {
        label195:
        ((PhotoViewer.PlaceProviderObject)localObject2).viewY = (i - paramAnonymousInt);
        ((PhotoViewer.PlaceProviderObject)localObject2).parentView = ProfileActivity.this.avatarImage;
        ((PhotoViewer.PlaceProviderObject)localObject2).imageReceiver = ProfileActivity.this.avatarImage.getImageReceiver();
        if (ProfileActivity.this.user_id == 0) {
          break label398;
        }
        ((PhotoViewer.PlaceProviderObject)localObject2).dialogId = ProfileActivity.this.user_id;
      }
      for (;;)
      {
        ((PhotoViewer.PlaceProviderObject)localObject2).thumb = ((PhotoViewer.PlaceProviderObject)localObject2).imageReceiver.getBitmapSafe();
        ((PhotoViewer.PlaceProviderObject)localObject2).size = -1;
        ((PhotoViewer.PlaceProviderObject)localObject2).radius = ProfileActivity.this.avatarImage.getImageReceiver().getRoundRadius();
        ((PhotoViewer.PlaceProviderObject)localObject2).scale = ProfileActivity.this.avatarImage.getScaleX();
        break;
        paramAnonymousMessageObject = (MessageObject)localObject2;
        if (ProfileActivity.this.chat_id == 0) {
          break label96;
        }
        localObject3 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
        paramAnonymousMessageObject = (MessageObject)localObject2;
        if (localObject3 == null) {
          break label96;
        }
        paramAnonymousMessageObject = (MessageObject)localObject2;
        if (((TLRPC.Chat)localObject3).photo == null) {
          break label96;
        }
        paramAnonymousMessageObject = (MessageObject)localObject2;
        if (((TLRPC.Chat)localObject3).photo.photo_big == null) {
          break label96;
        }
        paramAnonymousMessageObject = ((TLRPC.Chat)localObject3).photo.photo_big;
        break label96;
        paramAnonymousInt = AndroidUtilities.statusBarHeight;
        break label195;
        label398:
        if (ProfileActivity.this.chat_id != 0) {
          ((PhotoViewer.PlaceProviderObject)localObject2).dialogId = (-ProfileActivity.this.chat_id);
        }
      }
    }
    
    public void willHidePhotoViewer()
    {
      ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
    }
  };
  private boolean recreateMenuAfterAnimation;
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
  private int userInfoDetailedRow;
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
    for (;;)
    {
      return;
      View localView = this.listView.getChildAt(0);
      RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.listView.findContainingViewHolder(localView);
      int i = localView.getTop();
      int j = 0;
      int k = j;
      if (i >= 0)
      {
        k = j;
        if (localHolder != null)
        {
          k = j;
          if (localHolder.getAdapterPosition() == 0) {
            k = i;
          }
        }
      }
      if (this.extraHeight != k)
      {
        this.extraHeight = k;
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
    }
  }
  
  private void createActionBarMenu()
  {
    ActionBarMenu localActionBarMenu = this.actionBar.createMenu();
    localActionBarMenu.clearItems();
    this.animatingItem = null;
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    if (this.user_id != 0) {
      if (UserConfig.getInstance(this.currentAccount).getClientUserId() != this.user_id)
      {
        localObject3 = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
        if ((localObject3 != null) && (((TLRPC.TL_userFull)localObject3).phone_calls_available)) {
          this.callItem = localActionBarMenu.addItem(15, NUM);
        }
        if (ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.user_id)) == null)
        {
          localObject3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
          if (localObject3 == null) {
            return;
          }
          localObject2 = localActionBarMenu.addItem(10, NUM);
          if (this.isBot)
          {
            if (!((TLRPC.User)localObject3).bot_nochats) {
              ((ActionBarMenuItem)localObject2).addSubItem(9, LocaleController.getString("BotInvite", NUM));
            }
            ((ActionBarMenuItem)localObject2).addSubItem(10, LocaleController.getString("BotShare", NUM));
          }
          if ((((TLRPC.User)localObject3).phone != null) && (((TLRPC.User)localObject3).phone.length() != 0))
          {
            ((ActionBarMenuItem)localObject2).addSubItem(1, LocaleController.getString("AddContact", NUM));
            ((ActionBarMenuItem)localObject2).addSubItem(3, LocaleController.getString("ShareContact", NUM));
            if (!this.userBlocked)
            {
              localObject3 = LocaleController.getString("BlockContact", NUM);
              label262:
              ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject3);
              localObject3 = localObject2;
            }
          }
        }
      }
    }
    for (;;)
    {
      localObject2 = localObject3;
      if (localObject3 == null) {
        localObject2 = localActionBarMenu.addItem(10, NUM);
      }
      ((ActionBarMenuItem)localObject2).addSubItem(14, LocaleController.getString("AddShortcut", NUM));
      break;
      localObject3 = LocaleController.getString("Unblock", NUM);
      break label262;
      if (this.isBot)
      {
        if (!this.userBlocked) {}
        for (localObject3 = LocaleController.getString("BotStop", NUM);; localObject3 = LocaleController.getString("BotRestart", NUM))
        {
          ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject3);
          localObject3 = localObject2;
          break;
        }
      }
      if (!this.userBlocked) {}
      for (localObject3 = LocaleController.getString("BlockContact", NUM);; localObject3 = LocaleController.getString("Unblock", NUM))
      {
        ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject3);
        localObject3 = localObject2;
        break;
      }
      localObject2 = localActionBarMenu.addItem(10, NUM);
      ((ActionBarMenuItem)localObject2).addSubItem(3, LocaleController.getString("ShareContact", NUM));
      if (!this.userBlocked) {}
      for (localObject3 = LocaleController.getString("BlockContact", NUM);; localObject3 = LocaleController.getString("Unblock", NUM))
      {
        ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject3);
        ((ActionBarMenuItem)localObject2).addSubItem(4, LocaleController.getString("EditContact", NUM));
        ((ActionBarMenuItem)localObject2).addSubItem(5, LocaleController.getString("DeleteContact", NUM));
        localObject3 = localObject2;
        break;
      }
      localObject3 = localActionBarMenu.addItem(10, NUM);
      ((ActionBarMenuItem)localObject3).addSubItem(3, LocaleController.getString("ShareContact", NUM));
      continue;
      if (this.chat_id != 0) {
        if (this.chat_id > 0)
        {
          TLRPC.Chat localChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
          if (this.writeButton != null)
          {
            boolean bool = ChatObject.isChannel(this.currentChat);
            if (((bool) && (!ChatObject.canChangeChatInfo(this.currentChat))) || ((!bool) && (!this.currentChat.admin) && (!this.currentChat.creator) && (this.currentChat.admins_enabled)))
            {
              this.writeButton.setImageResource(NUM);
              this.writeButton.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
            }
          }
          else
          {
            label679:
            if (!ChatObject.isChannel(localChat)) {
              break label889;
            }
            if (ChatObject.hasAdminRights(localChat))
            {
              this.editItem = localActionBarMenu.addItem(12, NUM);
              localObject2 = localObject1;
              if (0 == 0) {
                localObject2 = localActionBarMenu.addItem(10, NUM);
              }
              if (!localChat.megagroup) {
                break label870;
              }
              ((ActionBarMenuItem)localObject2).addSubItem(12, LocaleController.getString("ManageGroupMenu", NUM));
            }
          }
          for (;;)
          {
            localObject3 = localObject2;
            if (!localChat.megagroup) {
              break;
            }
            localObject1 = localObject2;
            if (localObject2 == null) {
              localObject1 = localActionBarMenu.addItem(10, NUM);
            }
            ((ActionBarMenuItem)localObject1).addSubItem(16, LocaleController.getString("SearchMembers", NUM));
            localObject3 = localObject1;
            if (localChat.creator) {
              break;
            }
            localObject3 = localObject1;
            if (localChat.left) {
              break;
            }
            localObject3 = localObject1;
            if (localChat.kicked) {
              break;
            }
            ((ActionBarMenuItem)localObject1).addSubItem(7, LocaleController.getString("LeaveMegaMenu", NUM));
            localObject3 = localObject1;
            break;
            this.writeButton.setImageResource(NUM);
            this.writeButton.setPadding(0, 0, 0, 0);
            break label679;
            label870:
            ((ActionBarMenuItem)localObject2).addSubItem(12, LocaleController.getString("ManageChannelMenu", NUM));
          }
          label889:
          if ((!localChat.admins_enabled) || (localChat.creator) || (localChat.admin)) {
            this.editItem = localActionBarMenu.addItem(8, NUM);
          }
          localObject3 = localActionBarMenu.addItem(10, NUM);
          if ((localChat.creator) && (this.chat_id > 0)) {
            ((ActionBarMenuItem)localObject3).addSubItem(11, LocaleController.getString("SetAdmins", NUM));
          }
          if ((!localChat.admins_enabled) || (localChat.creator) || (localChat.admin)) {
            ((ActionBarMenuItem)localObject3).addSubItem(8, LocaleController.getString("ChannelEdit", NUM));
          }
          ((ActionBarMenuItem)localObject3).addSubItem(16, LocaleController.getString("SearchMembers", NUM));
          if ((localChat.creator) && ((this.info == null) || (this.info.participants.participants.size() > 0))) {
            ((ActionBarMenuItem)localObject3).addSubItem(13, LocaleController.getString("ConvertGroupMenu", NUM));
          }
          ((ActionBarMenuItem)localObject3).addSubItem(7, LocaleController.getString("DeleteAndExit", NUM));
        }
        else
        {
          localObject3 = localActionBarMenu.addItem(10, NUM);
          ((ActionBarMenuItem)localObject3).addSubItem(8, LocaleController.getString("EditName", NUM));
        }
      }
    }
  }
  
  private void fetchUsersFromChannelInfo()
  {
    if ((this.currentChat == null) || (!this.currentChat.megagroup)) {}
    for (;;)
    {
      return;
      if (((this.info instanceof TLRPC.TL_channelFull)) && (this.info.participants != null)) {
        for (int i = 0; i < this.info.participants.participants.size(); i++)
        {
          TLRPC.ChatParticipant localChatParticipant = (TLRPC.ChatParticipant)this.info.participants.participants.get(i);
          this.participantsMap.put(localChatParticipant.user_id, localChatParticipant);
        }
      }
    }
  }
  
  private void fixLayout()
  {
    if (this.fragmentView == null) {}
    for (;;)
    {
      return;
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
                MessagesController.getInstance(ProfileActivity.this.currentAccount).putUsers(localTL_channels_channelParticipants.users, false);
                if (localTL_channels_channelParticipants.users.size() < 200) {
                  ProfileActivity.access$11202(ProfileActivity.this, true);
                }
                if (ProfileActivity.22.this.val$req.offset == 0)
                {
                  ProfileActivity.this.participantsMap.clear();
                  ProfileActivity.this.info.participants = new TLRPC.TL_chatParticipants();
                  MessagesStorage.getInstance(ProfileActivity.this.currentAccount).putUsersAndChats(localTL_channels_channelParticipants.users, null, true, true);
                  MessagesStorage.getInstance(ProfileActivity.this.currentAccount).updateChannelUsers(ProfileActivity.this.chat_id, localTL_channels_channelParticipants.participants);
                }
                for (int i = 0; i < localTL_channels_channelParticipants.participants.size(); i++)
                {
                  TLRPC.TL_chatChannelParticipant localTL_chatChannelParticipant = new TLRPC.TL_chatChannelParticipant();
                  localTL_chatChannelParticipant.channelParticipant = ((TLRPC.ChannelParticipant)localTL_channels_channelParticipants.participants.get(i));
                  localTL_chatChannelParticipant.inviter_id = localTL_chatChannelParticipant.channelParticipant.inviter_id;
                  localTL_chatChannelParticipant.user_id = localTL_chatChannelParticipant.channelParticipant.user_id;
                  localTL_chatChannelParticipant.date = localTL_chatChannelParticipant.channelParticipant.date;
                  if (ProfileActivity.this.participantsMap.indexOfKey(localTL_chatChannelParticipant.user_id) < 0)
                  {
                    ProfileActivity.this.info.participants.participants.add(localTL_chatChannelParticipant);
                    ProfileActivity.this.participantsMap.put(localTL_chatChannelParticipant.user_id, localTL_chatChannelParticipant);
                  }
                }
              }
              ProfileActivity.this.updateOnlineCount();
              ProfileActivity.access$11502(ProfileActivity.this, false);
              ProfileActivity.this.updateRowsIds();
              if (ProfileActivity.this.listAdapter != null) {
                ProfileActivity.this.listAdapter.notifyDataSetChanged();
              }
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
  
  private void kickUser(int paramInt)
  {
    if (paramInt != 0)
    {
      MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramInt)), this.info);
      return;
    }
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    if (AndroidUtilities.isTablet()) {
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(-this.chat_id) });
    }
    for (;;)
    {
      MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.info);
      this.playProfileAnimation = false;
      finishFragment();
      break;
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
    }
  }
  
  private void leaveChatPressed()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    String str;
    if ((ChatObject.isChannel(this.chat_id, this.currentAccount)) && (!this.currentChat.megagroup)) {
      if (ChatObject.isChannel(this.chat_id, this.currentAccount))
      {
        str = LocaleController.getString("ChannelLeaveAlert", NUM);
        localBuilder.setMessage(str);
      }
    }
    for (;;)
    {
      localBuilder.setTitle(LocaleController.getString("AppName", NUM));
      localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          ProfileActivity.this.kickUser(0);
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
      showDialog(localBuilder.create());
      return;
      str = LocaleController.getString("AreYouSureDeleteAndExit", NUM);
      break;
      localBuilder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", NUM));
    }
  }
  
  private void needLayout()
  {
    int i;
    Object localObject;
    float f1;
    label124:
    label163:
    int j;
    label176:
    label194:
    label315:
    label362:
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
          break label493;
        }
        i = AndroidUtilities.statusBarHeight;
        ((ImageView)localObject).setTranslationY(i + ActionBar.getCurrentActionBarHeight() + this.extraHeight - AndroidUtilities.dp(29.5F));
        if (!this.openAnimationInProgress)
        {
          if (f1 <= 0.2F) {
            break label498;
          }
          i = 1;
          if (this.writeButton.getTag() != null) {
            break label503;
          }
          j = 1;
          if (i != j)
          {
            if (i == 0) {
              break label509;
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
              break label523;
            }
            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
            this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 1.0F }) });
            this.writeButtonAnimation.setDuration(150L);
            this.writeButtonAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymousAnimator)
              {
                if ((ProfileActivity.this.writeButtonAnimation != null) && (ProfileActivity.this.writeButtonAnimation.equals(paramAnonymousAnimator))) {
                  ProfileActivity.access$11702(ProfileActivity.this, null);
                }
              }
            });
            this.writeButtonAnimation.start();
          }
        }
      }
      if (!this.actionBar.getOccupyStatusBar()) {
        break label615;
      }
      i = AndroidUtilities.statusBarHeight;
      f2 = i + ActionBar.getCurrentActionBarHeight() / 2.0F * (1.0F + f1) - 21.0F * AndroidUtilities.density + 27.0F * AndroidUtilities.density * f1;
      this.avatarImage.setScaleX((42.0F + 18.0F * f1) / 42.0F);
      this.avatarImage.setScaleY((42.0F + 18.0F * f1) / 42.0F);
      this.avatarImage.setTranslationX(-AndroidUtilities.dp(47.0F) * f1);
      this.avatarImage.setTranslationY((float)Math.ceil(f2));
      i = 0;
      label468:
      if (i >= 2) {
        return;
      }
      if (this.nameTextView[i] != null) {
        break label620;
      }
    }
    label493:
    label498:
    label503:
    label509:
    label523:
    label615:
    label620:
    do
    {
      i++;
      break label468;
      i = 0;
      break;
      i = 0;
      break label124;
      i = 0;
      break label163;
      j = 0;
      break label176;
      this.writeButton.setTag(Integer.valueOf(0));
      break label194;
      this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
      this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 0.0F }) });
      break label315;
      i = 0;
      break label362;
      this.nameTextView[i].setTranslationX(-21.0F * AndroidUtilities.density * f1);
      this.nameTextView[i].setTranslationY((float)Math.floor(f2) + AndroidUtilities.dp(1.3F) + AndroidUtilities.dp(7.0F) * f1);
      this.onlineTextView[i].setTranslationX(-21.0F * AndroidUtilities.density * f1);
      this.onlineTextView[i].setTranslationY((float)Math.floor(f2) + AndroidUtilities.dp(24.0F) + (float)Math.floor(11.0F * AndroidUtilities.density) * f1);
      this.nameTextView[i].setScaleX(1.0F + 0.12F * f1);
      this.nameTextView[i].setScaleY(1.0F + 0.12F * f1);
    } while ((i != 1) || (this.openAnimationInProgress));
    label787:
    int k;
    if (AndroidUtilities.isTablet())
    {
      j = AndroidUtilities.dp(490.0F);
      if ((this.callItem == null) && (this.editItem == null)) {
        break label1025;
      }
      k = 48;
      label805:
      j = (int)(j - AndroidUtilities.dp((k + 40) * (1.0F - f1) + 126.0F) - this.nameTextView[i].getTranslationX());
      float f3 = this.nameTextView[i].getPaint().measureText(this.nameTextView[i].getText().toString());
      float f4 = this.nameTextView[i].getScaleX();
      float f5 = this.nameTextView[i].getSideDrawablesSize();
      localObject = (FrameLayout.LayoutParams)this.nameTextView[i].getLayoutParams();
      if (j >= f3 * f4 + f5) {
        break label1031;
      }
    }
    label1025:
    label1031:
    for (((FrameLayout.LayoutParams)localObject).width = ((int)Math.ceil(j / this.nameTextView[i].getScaleX()));; ((FrameLayout.LayoutParams)localObject).width = -2)
    {
      this.nameTextView[i].setLayoutParams((ViewGroup.LayoutParams)localObject);
      localObject = (FrameLayout.LayoutParams)this.onlineTextView[i].getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).rightMargin = ((int)Math.ceil(this.onlineTextView[i].getTranslationX() + AndroidUtilities.dp(8.0F) + AndroidUtilities.dp(40.0F) * (1.0F - f1)));
      this.onlineTextView[i].setLayoutParams((ViewGroup.LayoutParams)localObject);
      break;
      j = AndroidUtilities.displaySize.x;
      break label787;
      k = 0;
      break label805;
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
    ContactsActivity localContactsActivity;
    for (;;)
    {
      ((Bundle)localObject).putBoolean("needForwardCount", bool);
      if (this.chat_id > 0)
      {
        if (ChatObject.canAddViaLink(this.currentChat)) {
          ((Bundle)localObject).putInt("chat_id", this.currentChat.id);
        }
        ((Bundle)localObject).putString("selectAlertString", LocaleController.getString("AddToTheGroup", NUM));
      }
      localContactsActivity = new ContactsActivity((Bundle)localObject);
      localContactsActivity.setDelegate(new ContactsActivity.ContactsActivityDelegate()
      {
        public void didSelectContact(TLRPC.User paramAnonymousUser, String paramAnonymousString, ContactsActivity paramAnonymousContactsActivity)
        {
          paramAnonymousContactsActivity = MessagesController.getInstance(ProfileActivity.this.currentAccount);
          int i = ProfileActivity.this.chat_id;
          TLRPC.ChatFull localChatFull = ProfileActivity.this.info;
          if (paramAnonymousString != null) {}
          for (int j = Utilities.parseInt(paramAnonymousString).intValue();; j = 0)
          {
            paramAnonymousContactsActivity.addUserToChat(i, paramAnonymousUser, localChatFull, j, null, ProfileActivity.this);
            return;
          }
        }
      });
      if ((this.info == null) || (this.info.participants == null)) {
        break label208;
      }
      localObject = new SparseArray();
      for (int i = 0; i < this.info.participants.participants.size(); i++) {
        ((SparseArray)localObject).put(((TLRPC.ChatParticipant)this.info.participants.participants.get(i)).user_id, null);
      }
      bool = false;
    }
    localContactsActivity.setIgnoreUsers((SparseArray)localObject);
    label208:
    presentFragment(localContactsActivity);
  }
  
  private boolean processOnClickOrPress(final int paramInt)
  {
    boolean bool1 = false;
    final Object localObject1;
    boolean bool2;
    if ((paramInt == this.usernameRow) || (paramInt == this.channelNameRow)) {
      if (paramInt == this.usernameRow)
      {
        localObject1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
        bool2 = bool1;
        if (localObject1 != null)
        {
          if (((TLRPC.User)localObject1).username != null) {
            break label64;
          }
          bool2 = bool1;
        }
      }
    }
    for (;;)
    {
      return bool2;
      label64:
      Object localObject2;
      final Object localObject3;
      for (localObject1 = ((TLRPC.User)localObject1).username;; localObject1 = ((TLRPC.Chat)localObject1).username)
      {
        localObject2 = new AlertDialog.Builder(getParentActivity());
        localObject3 = LocaleController.getString("Copy", NUM);
        localObject1 = new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            if (paramAnonymousInt == 0) {}
            try
            {
              paramAnonymousDialogInterface = (ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard");
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              paramAnonymousDialogInterface.setPrimaryClip(ClipData.newPlainText("label", "@" + localObject1));
              return;
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              for (;;)
              {
                FileLog.e(paramAnonymousDialogInterface);
              }
            }
          }
        };
        ((AlertDialog.Builder)localObject2).setItems(new CharSequence[] { localObject3 }, (DialogInterface.OnClickListener)localObject1);
        showDialog(((AlertDialog.Builder)localObject2).create());
        bool2 = true;
        break;
        localObject1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
        bool2 = bool1;
        if (localObject1 == null) {
          break;
        }
        bool2 = bool1;
        if (((TLRPC.Chat)localObject1).username == null) {
          break;
        }
      }
      if (paramInt == this.phoneRow)
      {
        localObject1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
        bool2 = bool1;
        if (localObject1 != null)
        {
          bool2 = bool1;
          if (((TLRPC.User)localObject1).phone != null)
          {
            bool2 = bool1;
            if (((TLRPC.User)localObject1).phone.length() != 0)
            {
              bool2 = bool1;
              if (getParentActivity() != null)
              {
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
                ArrayList localArrayList = new ArrayList();
                localObject3 = new ArrayList();
                localObject2 = MessagesController.getInstance(this.currentAccount).getUserFull(((TLRPC.User)localObject1).id);
                if ((localObject2 != null) && (((TLRPC.TL_userFull)localObject2).phone_calls_available))
                {
                  localArrayList.add(LocaleController.getString("CallViaTelegram", NUM));
                  ((ArrayList)localObject3).add(Integer.valueOf(2));
                }
                localArrayList.add(LocaleController.getString("Call", NUM));
                ((ArrayList)localObject3).add(Integer.valueOf(0));
                localArrayList.add(LocaleController.getString("Copy", NUM));
                ((ArrayList)localObject3).add(Integer.valueOf(1));
                localBuilder.setItems((CharSequence[])localArrayList.toArray(new CharSequence[localArrayList.size()]), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                  {
                    paramAnonymousInt = ((Integer)localObject3.get(paramAnonymousInt)).intValue();
                    if (paramAnonymousInt == 0) {}
                    for (;;)
                    {
                      Object localObject;
                      try
                      {
                        localObject = new android/content/Intent;
                        paramAnonymousDialogInterface = new java/lang/StringBuilder;
                        paramAnonymousDialogInterface.<init>();
                        ((Intent)localObject).<init>("android.intent.action.DIAL", Uri.parse("tel:+" + localObject1.phone));
                        ((Intent)localObject).addFlags(268435456);
                        ProfileActivity.this.getParentActivity().startActivityForResult((Intent)localObject, 500);
                        return;
                      }
                      catch (Exception paramAnonymousDialogInterface)
                      {
                        FileLog.e(paramAnonymousDialogInterface);
                        continue;
                      }
                      if (paramAnonymousInt == 1) {
                        try
                        {
                          localObject = (ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard");
                          paramAnonymousDialogInterface = new java/lang/StringBuilder;
                          paramAnonymousDialogInterface.<init>();
                          ((ClipboardManager)localObject).setPrimaryClip(ClipData.newPlainText("label", "+" + localObject1.phone));
                        }
                        catch (Exception paramAnonymousDialogInterface)
                        {
                          FileLog.e(paramAnonymousDialogInterface);
                        }
                      } else if (paramAnonymousInt == 2) {
                        VoIPHelper.startCall(localObject1, ProfileActivity.this.getParentActivity(), MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(localObject1.id));
                      }
                    }
                  }
                });
                showDialog(localBuilder.create());
                bool2 = true;
              }
            }
          }
        }
      }
      else if ((paramInt != this.channelInfoRow) && (paramInt != this.userInfoRow))
      {
        bool2 = bool1;
        if (paramInt != this.userInfoDetailedRow) {}
      }
      else
      {
        localObject2 = new AlertDialog.Builder(getParentActivity());
        localObject1 = LocaleController.getString("Copy", NUM);
        localObject3 = new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            for (;;)
            {
              try
              {
                if (paramInt == ProfileActivity.this.channelInfoRow)
                {
                  paramAnonymousDialogInterface = ProfileActivity.this.info.about;
                  if (!TextUtils.isEmpty(paramAnonymousDialogInterface)) {
                    continue;
                  }
                  return;
                }
              }
              catch (Exception paramAnonymousDialogInterface)
              {
                FileLog.e(paramAnonymousDialogInterface);
                continue;
              }
              paramAnonymousDialogInterface = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
              if (paramAnonymousDialogInterface != null)
              {
                paramAnonymousDialogInterface = paramAnonymousDialogInterface.about;
              }
              else
              {
                paramAnonymousDialogInterface = null;
                continue;
                AndroidUtilities.addToClipboard(paramAnonymousDialogInterface);
              }
            }
          }
        };
        ((AlertDialog.Builder)localObject2).setItems(new CharSequence[] { localObject1 }, (DialogInterface.OnClickListener)localObject3);
        showDialog(((AlertDialog.Builder)localObject2).create());
        bool2 = true;
      }
    }
  }
  
  private void updateOnlineCount()
  {
    this.onlineCount = 0;
    int i = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
    this.sortedUsers.clear();
    Object localObject;
    if (((this.info instanceof TLRPC.TL_chatFull)) || (((this.info instanceof TLRPC.TL_channelFull)) && (this.info.participants_count <= 200) && (this.info.participants != null))) {
      for (int j = 0; j < this.info.participants.participants.size(); j++)
      {
        localObject = (TLRPC.ChatParticipant)this.info.participants.participants.get(j);
        localObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id));
        if ((localObject != null) && (((TLRPC.User)localObject).status != null) && ((((TLRPC.User)localObject).status.expires > i) || (((TLRPC.User)localObject).id == UserConfig.getInstance(this.currentAccount).getClientUserId())) && (((TLRPC.User)localObject).status.expires > 10000)) {
          this.onlineCount += 1;
        }
        this.sortedUsers.add(Integer.valueOf(j));
      }
    }
    try
    {
      ArrayList localArrayList = this.sortedUsers;
      localObject = new org/telegram/ui/ProfileActivity$29;
      ((29)localObject).<init>(this);
      Collections.sort(localArrayList, (Comparator)localObject);
      if (this.listAdapter != null) {
        this.listAdapter.notifyItemRangeChanged(this.emptyRowChat2 + 1, this.sortedUsers.size());
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
  
  private void updateProfileData()
  {
    if ((this.avatarImage == null) || (this.nameTextView == null)) {}
    int i;
    Object localObject3;
    Object localObject4;
    label247:
    label492:
    label510:
    label522:
    label553:
    label624:
    label655:
    label661:
    label671:
    label677:
    label702:
    do
    {
      return;
      i = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
      TLRPC.User localUser;
      if (i == 2)
      {
        localObject1 = LocaleController.getString("WaitingForNetwork", NUM);
        if (this.user_id == 0) {
          continue;
        }
        localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
        localObject2 = null;
        localObject3 = null;
        if (localUser.photo != null)
        {
          localObject2 = localUser.photo.photo_small;
          localObject3 = localUser.photo.photo_big;
        }
        this.avatarDrawable.setInfo(localUser);
        this.avatarImage.setImage((TLObject)localObject2, "50_50", this.avatarDrawable);
        localObject4 = UserObject.getUserName(localUser);
        if (localUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
          break label247;
        }
        localObject2 = LocaleController.getString("ChatYourSelf", NUM);
        localObject4 = LocaleController.getString("ChatYourSelfName", NUM);
      }
      for (;;)
      {
        for (i = 0;; i++)
        {
          if (i >= 2) {
            break label702;
          }
          if (this.nameTextView[i] != null) {
            break;
          }
        }
        if (i == 1)
        {
          localObject1 = LocaleController.getString("Connecting", NUM);
          break;
        }
        if (i == 5)
        {
          localObject1 = LocaleController.getString("Updating", NUM);
          break;
        }
        if (i == 4)
        {
          localObject1 = LocaleController.getString("ConnectingToProxy", NUM);
          break;
        }
        localObject1 = null;
        break;
        if ((localUser.id == 333000) || (localUser.id == 777000)) {
          localObject2 = LocaleController.getString("ServiceNotifications", NUM);
        } else if (this.isBot) {
          localObject2 = LocaleController.getString("Bot", NUM);
        } else {
          localObject2 = LocaleController.formatUserStatus(this.currentAccount, localUser);
        }
      }
      Drawable localDrawable;
      long l;
      if ((i == 0) && (localUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) && (localUser.id / 1000 != 777) && (localUser.id / 1000 != 333) && (localUser.phone != null) && (localUser.phone.length() != 0) && (ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(localUser.id)) == null) && ((ContactsController.getInstance(this.currentAccount).contactsDict.size() != 0) || (!ContactsController.getInstance(this.currentAccount).isLoadingContacts())))
      {
        localObject5 = PhoneFormat.getInstance().format("+" + localUser.phone);
        if (!this.nameTextView[i].getText().equals(localObject5)) {
          this.nameTextView[i].setText((CharSequence)localObject5);
        }
        if ((i != 0) || (localObject1 == null)) {
          break label624;
        }
        this.onlineTextView[i].setText((CharSequence)localObject1);
        if (this.currentEncryptedChat == null) {
          break label655;
        }
        localDrawable = Theme.chat_lockIconDrawable;
        localObject5 = null;
        if (i != 0) {
          break label677;
        }
        localObject5 = MessagesController.getInstance(this.currentAccount);
        if (this.dialog_id == 0L) {
          break label661;
        }
        l = this.dialog_id;
        if (!((MessagesController)localObject5).isDialogMuted(l)) {
          break label671;
        }
        localObject5 = Theme.chat_muteIconDrawable;
      }
      for (;;)
      {
        this.nameTextView[i].setLeftDrawable(localDrawable);
        this.nameTextView[i].setRightDrawable((Drawable)localObject5);
        break;
        if (this.nameTextView[i].getText().equals(localObject4)) {
          break label492;
        }
        this.nameTextView[i].setText((CharSequence)localObject4);
        break label492;
        if (this.onlineTextView[i].getText().equals(localObject2)) {
          break label510;
        }
        this.onlineTextView[i].setText((CharSequence)localObject2);
        break label510;
        localDrawable = null;
        break label522;
        l = this.user_id;
        break label553;
        localObject5 = null;
        continue;
        if (localUser.verified) {
          localObject5 = new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable);
        }
      }
      localObject2 = this.avatarImage.getImageReceiver();
      if (!PhotoViewer.isShowingImage((TLRPC.FileLocation)localObject3)) {}
      for (bool = true;; bool = false)
      {
        ((ImageReceiver)localObject2).setVisible(bool, false);
        break;
      }
    } while (this.chat_id == 0);
    Object localObject5 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
    if (localObject5 != null)
    {
      this.currentChat = ((TLRPC.Chat)localObject5);
      if (!ChatObject.isChannel((TLRPC.Chat)localObject5)) {
        break label1143;
      }
      if ((this.info != null) && ((this.currentChat.megagroup) || ((this.info.participants_count != 0) && (!this.currentChat.admin) && (!this.info.can_view_participants)))) {
        break label931;
      }
      if (!this.currentChat.megagroup) {
        break label886;
      }
      localObject2 = LocaleController.getString("Loading", NUM).toLowerCase();
      label855:
      i = 0;
      label857:
      if (i >= 2) {
        break label1673;
      }
      if (this.nameTextView[i] != null) {
        break label1232;
      }
    }
    for (;;)
    {
      i++;
      break label857;
      localObject5 = this.currentChat;
      break;
      label886:
      if ((((TLRPC.Chat)localObject5).flags & 0x40) != 0)
      {
        localObject2 = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
        break label855;
      }
      localObject2 = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
      break label855;
      label931:
      if ((this.currentChat.megagroup) && (this.info.participants_count <= 200))
      {
        if ((this.onlineCount > 1) && (this.info.participants_count != 0))
        {
          localObject2 = String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", this.info.participants_count), LocaleController.formatPluralString("OnlineCount", this.onlineCount) });
          break label855;
        }
        localObject2 = LocaleController.formatPluralString("Members", this.info.participants_count);
        break label855;
      }
      localObject2 = new int[1];
      localObject3 = LocaleController.formatShortNumber(this.info.participants_count, (int[])localObject2);
      if (this.currentChat.megagroup)
      {
        localObject2 = LocaleController.formatPluralString("Members", localObject2[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject2[0]) }), (CharSequence)localObject3);
        break label855;
      }
      localObject2 = LocaleController.formatPluralString("Subscribers", localObject2[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject2[0]) }), (CharSequence)localObject3);
      break label855;
      label1143:
      i = ((TLRPC.Chat)localObject5).participants_count;
      if (this.info != null) {
        i = this.info.participants.participants.size();
      }
      if ((i != 0) && (this.onlineCount > 1))
      {
        localObject2 = String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", i), LocaleController.formatPluralString("OnlineCount", this.onlineCount) });
        break label855;
      }
      localObject2 = LocaleController.formatPluralString("Members", i);
      break label855;
      label1232:
      if ((((TLRPC.Chat)localObject5).title != null) && (!this.nameTextView[i].getText().equals(((TLRPC.Chat)localObject5).title))) {
        this.nameTextView[i].setText(((TLRPC.Chat)localObject5).title);
      }
      this.nameTextView[i].setLeftDrawable(null);
      if (i != 0)
      {
        if (((TLRPC.Chat)localObject5).verified) {
          this.nameTextView[i].setRightDrawable(new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable));
        }
        for (;;)
        {
          if ((i != 0) || (localObject1 == null)) {
            break label1400;
          }
          this.onlineTextView[i].setText((CharSequence)localObject1);
          break;
          this.nameTextView[i].setRightDrawable(null);
        }
      }
      localObject4 = this.nameTextView[i];
      if (MessagesController.getInstance(this.currentAccount).isDialogMuted(-this.chat_id)) {}
      for (localObject3 = Theme.chat_muteIconDrawable;; localObject3 = null)
      {
        ((SimpleTextView)localObject4).setRightDrawable((Drawable)localObject3);
        break;
      }
      label1400:
      if ((this.currentChat.megagroup) && (this.info != null) && (this.info.participants_count <= 200) && (this.onlineCount > 0))
      {
        if (!this.onlineTextView[i].getText().equals(localObject2)) {
          this.onlineTextView[i].setText((CharSequence)localObject2);
        }
      }
      else if ((i == 0) && (ChatObject.isChannel(this.currentChat)) && (this.info != null) && (this.info.participants_count != 0) && ((this.currentChat.megagroup) || (this.currentChat.broadcast)))
      {
        localObject4 = new int[1];
        localObject3 = LocaleController.formatShortNumber(this.info.participants_count, (int[])localObject4);
        if (this.currentChat.megagroup) {
          this.onlineTextView[i].setText(LocaleController.formatPluralString("Members", localObject4[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject4[0]) }), (CharSequence)localObject3));
        } else {
          this.onlineTextView[i].setText(LocaleController.formatPluralString("Subscribers", localObject4[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject4[0]) }), (CharSequence)localObject3));
        }
      }
      else if (!this.onlineTextView[i].getText().equals(localObject2))
      {
        this.onlineTextView[i].setText((CharSequence)localObject2);
      }
    }
    label1673:
    Object localObject1 = null;
    Object localObject2 = null;
    if (((TLRPC.Chat)localObject5).photo != null)
    {
      localObject1 = ((TLRPC.Chat)localObject5).photo.photo_small;
      localObject2 = ((TLRPC.Chat)localObject5).photo.photo_big;
    }
    this.avatarDrawable.setInfo((TLRPC.Chat)localObject5);
    this.avatarImage.setImage((TLObject)localObject1, "50_50", this.avatarDrawable);
    localObject1 = this.avatarImage.getImageReceiver();
    if (!PhotoViewer.isShowingImage((TLRPC.FileLocation)localObject2)) {}
    for (boolean bool = true;; bool = false)
    {
      ((ImageReceiver)localObject1).setVisible(bool, false);
      break;
    }
  }
  
  private void updateRowsIds()
  {
    int i = 0;
    this.emptyRow = -1;
    this.phoneRow = -1;
    this.userInfoRow = -1;
    this.userInfoDetailedRow = -1;
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
    this.leaveChannelRow = -1;
    this.loadMoreMembersRow = -1;
    this.groupsInCommonRow = -1;
    this.rowCount = 0;
    int j;
    if (this.user_id != 0)
    {
      TLRPC.User localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
      j = this.rowCount;
      this.rowCount = (j + 1);
      this.emptyRow = j;
      if ((!this.isBot) && (!TextUtils.isEmpty(localUser.phone)))
      {
        j = this.rowCount;
        this.rowCount = (j + 1);
        this.phoneRow = j;
      }
      TLRPC.TL_userFull localTL_userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
      j = i;
      if (localUser != null)
      {
        j = i;
        if (!TextUtils.isEmpty(localUser.username)) {
          j = 1;
        }
      }
      if ((localTL_userFull != null) && (!TextUtils.isEmpty(localTL_userFull.about)))
      {
        if (this.phoneRow != -1)
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.userSectionRow = i;
        }
        if ((j != 0) || (this.isBot))
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.userInfoRow = i;
        }
      }
      else
      {
        if (j != 0)
        {
          j = this.rowCount;
          this.rowCount = (j + 1);
          this.usernameRow = j;
        }
        if ((this.phoneRow != -1) || (this.userInfoRow != -1) || (this.userInfoDetailedRow != -1) || (this.usernameRow != -1))
        {
          j = this.rowCount;
          this.rowCount = (j + 1);
          this.sectionRow = j;
        }
        if (this.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId())
        {
          j = this.rowCount;
          this.rowCount = (j + 1);
          this.settingsNotificationsRow = j;
        }
        j = this.rowCount;
        this.rowCount = (j + 1);
        this.sharedMediaRow = j;
        if ((this.currentEncryptedChat instanceof TLRPC.TL_encryptedChat))
        {
          j = this.rowCount;
          this.rowCount = (j + 1);
          this.settingsTimerRow = j;
          j = this.rowCount;
          this.rowCount = (j + 1);
          this.settingsKeyRow = j;
        }
        if ((localTL_userFull != null) && (localTL_userFull.common_chats_count != 0))
        {
          j = this.rowCount;
          this.rowCount = (j + 1);
          this.groupsInCommonRow = j;
        }
        if ((localUser != null) && (!this.isBot) && (this.currentEncryptedChat == null) && (localUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId()))
        {
          j = this.rowCount;
          this.rowCount = (j + 1);
          this.startSecretChatRow = j;
        }
      }
    }
    for (;;)
    {
      return;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.userInfoDetailedRow = i;
      break;
      if (this.chat_id != 0) {
        if (this.chat_id > 0)
        {
          j = this.rowCount;
          this.rowCount = (j + 1);
          this.emptyRow = j;
          if ((ChatObject.isChannel(this.currentChat)) && (((this.info != null) && (this.info.about != null) && (this.info.about.length() > 0)) || ((this.currentChat.username != null) && (this.currentChat.username.length() > 0))))
          {
            if ((this.info != null) && (this.info.about != null) && (this.info.about.length() > 0))
            {
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.channelInfoRow = j;
            }
            if ((this.currentChat.username != null) && (this.currentChat.username.length() > 0))
            {
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.channelNameRow = j;
            }
            j = this.rowCount;
            this.rowCount = (j + 1);
            this.sectionRow = j;
          }
          j = this.rowCount;
          this.rowCount = (j + 1);
          this.settingsNotificationsRow = j;
          j = this.rowCount;
          this.rowCount = (j + 1);
          this.sharedMediaRow = j;
          if (ChatObject.isChannel(this.currentChat))
          {
            if ((!this.currentChat.megagroup) && (this.info != null) && ((this.currentChat.creator) || (this.info.can_view_participants)))
            {
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.membersRow = j;
            }
            if ((!this.currentChat.creator) && (!this.currentChat.left) && (!this.currentChat.kicked) && (!this.currentChat.megagroup))
            {
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.leaveChannelRow = j;
            }
            if ((this.currentChat.megagroup) && (((this.currentChat.admin_rights != null) && (this.currentChat.admin_rights.invite_users)) || (((this.currentChat.creator) || (this.currentChat.democracy)) && ((this.info == null) || (this.info.participants_count < MessagesController.getInstance(this.currentAccount).maxMegagroupCount)))))
            {
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.addMemberRow = j;
            }
            if ((this.info != null) && (this.currentChat.megagroup) && (this.info.participants != null) && (!this.info.participants.participants.isEmpty()))
            {
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.emptyRowChat = j;
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.membersSectionRow = j;
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.emptyRowChat2 = j;
              this.rowCount += this.info.participants.participants.size();
              this.membersEndRow = this.rowCount;
              if (!this.usersEndReached)
              {
                j = this.rowCount;
                this.rowCount = (j + 1);
                this.loadMoreMembersRow = j;
              }
            }
          }
          else
          {
            if (this.info != null)
            {
              if ((!(this.info.participants instanceof TLRPC.TL_chatParticipantsForbidden)) && (this.info.participants.participants.size() < MessagesController.getInstance(this.currentAccount).maxGroupCount) && ((this.currentChat.admin) || (this.currentChat.creator) || (!this.currentChat.admins_enabled)))
              {
                j = this.rowCount;
                this.rowCount = (j + 1);
                this.addMemberRow = j;
              }
              if ((this.currentChat.creator) && (this.info.participants.participants.size() >= MessagesController.getInstance(this.currentAccount).minGroupConvertSize))
              {
                j = this.rowCount;
                this.rowCount = (j + 1);
                this.convertRow = j;
              }
            }
            j = this.rowCount;
            this.rowCount = (j + 1);
            this.emptyRowChat = j;
            if (this.convertRow != -1)
            {
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.convertHelpRow = j;
            }
            for (;;)
            {
              if ((this.info == null) || ((this.info.participants instanceof TLRPC.TL_chatParticipantsForbidden))) {
                break label1458;
              }
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.emptyRowChat2 = j;
              this.rowCount += this.info.participants.participants.size();
              this.membersEndRow = this.rowCount;
              break;
              j = this.rowCount;
              this.rowCount = (j + 1);
              this.membersSectionRow = j;
            }
          }
        }
        else
        {
          label1458:
          if ((!ChatObject.isChannel(this.currentChat)) && (this.info != null) && (!(this.info.participants instanceof TLRPC.TL_chatParticipantsForbidden)))
          {
            j = this.rowCount;
            this.rowCount = (j + 1);
            this.addMemberRow = j;
            j = this.rowCount;
            this.rowCount = (j + 1);
            this.emptyRowChat2 = j;
            this.rowCount += this.info.participants.participants.size();
            this.membersEndRow = this.rowCount;
          }
        }
      }
    }
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
    if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id, this.currentAccount)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      paramContext.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(i), false);
      paramContext.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
      paramContext.setItemsColor(Theme.getColor("actionBarActionModeDefaultIcon"), true);
      paramContext.setBackButtonDrawable(new BackDrawable(false));
      paramContext.setCastShadows(false);
      paramContext.setAddToContainer(false);
      if ((Build.VERSION.SDK_INT < 21) || (AndroidUtilities.isTablet())) {
        break label127;
      }
    }
    label127:
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
    Theme.createProfileResources(paramContext);
    this.hasOwnBackground = true;
    this.extraHeight = AndroidUtilities.dp(88.0F);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (ProfileActivity.this.getParentActivity() == null) {}
        for (;;)
        {
          return;
          if (paramAnonymousInt == -1)
          {
            ProfileActivity.this.finishFragment();
          }
          else
          {
            final Object localObject1;
            if (paramAnonymousInt == 2)
            {
              if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) != null)
              {
                if (!ProfileActivity.this.isBot)
                {
                  localObject1 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
                  if (!ProfileActivity.this.userBlocked) {
                    ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AreYouSureBlockContact", NUM));
                  }
                  for (;;)
                  {
                    ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", NUM));
                    ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                    {
                      public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                      {
                        if (!ProfileActivity.this.userBlocked) {
                          MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                        }
                        for (;;)
                        {
                          return;
                          MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                        }
                      }
                    });
                    ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    ProfileActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
                    break;
                    ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AreYouSureUnblockContact", NUM));
                  }
                }
                if (!ProfileActivity.this.userBlocked)
                {
                  MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                }
                else
                {
                  MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                  SendMessagesHelper.getInstance(ProfileActivity.this.currentAccount).sendMessage("/start", ProfileActivity.this.user_id, null, null, false, null, null, null);
                  ProfileActivity.this.finishFragment();
                }
              }
            }
            else
            {
              Object localObject4;
              if (paramAnonymousInt == 1)
              {
                localObject1 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                localObject4 = new Bundle();
                ((Bundle)localObject4).putInt("user_id", ((TLRPC.User)localObject1).id);
                ((Bundle)localObject4).putBoolean("addContact", true);
                ProfileActivity.this.presentFragment(new ContactAddActivity((Bundle)localObject4));
              }
              else if (paramAnonymousInt == 3)
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putBoolean("onlySelect", true);
                ((Bundle)localObject1).putString("selectAlertString", LocaleController.getString("SendContactTo", NUM));
                ((Bundle)localObject1).putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", NUM));
                localObject1 = new DialogsActivity((Bundle)localObject1);
                ((DialogsActivity)localObject1).setDelegate(ProfileActivity.this);
                ProfileActivity.this.presentFragment((BaseFragment)localObject1);
              }
              else if (paramAnonymousInt == 4)
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putInt("user_id", ProfileActivity.this.user_id);
                ProfileActivity.this.presentFragment(new ContactAddActivity((Bundle)localObject1));
              }
              else if (paramAnonymousInt == 5)
              {
                localObject1 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                if ((localObject1 != null) && (ProfileActivity.this.getParentActivity() != null))
                {
                  localObject4 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
                  ((AlertDialog.Builder)localObject4).setMessage(LocaleController.getString("AreYouSureDeleteContact", NUM));
                  ((AlertDialog.Builder)localObject4).setTitle(LocaleController.getString("AppName", NUM));
                  ((AlertDialog.Builder)localObject4).setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                    {
                      paramAnonymous2DialogInterface = new ArrayList();
                      paramAnonymous2DialogInterface.add(localObject1);
                      ContactsController.getInstance(ProfileActivity.this.currentAccount).deleteContact(paramAnonymous2DialogInterface);
                    }
                  });
                  ((AlertDialog.Builder)localObject4).setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                  ProfileActivity.this.showDialog(((AlertDialog.Builder)localObject4).create());
                }
              }
              else if (paramAnonymousInt == 7)
              {
                ProfileActivity.this.leaveChatPressed();
              }
              else if (paramAnonymousInt == 8)
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putInt("chat_id", ProfileActivity.this.chat_id);
                ProfileActivity.this.presentFragment(new ChangeChatNameActivity((Bundle)localObject1));
              }
              else if (paramAnonymousInt == 12)
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putInt("chat_id", ProfileActivity.this.chat_id);
                localObject1 = new ChannelEditActivity((Bundle)localObject1);
                ((ChannelEditActivity)localObject1).setInfo(ProfileActivity.this.info);
                ProfileActivity.this.presentFragment((BaseFragment)localObject1);
              }
              else if (paramAnonymousInt == 9)
              {
                localObject1 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                if (localObject1 != null)
                {
                  localObject4 = new Bundle();
                  ((Bundle)localObject4).putBoolean("onlySelect", true);
                  ((Bundle)localObject4).putInt("dialogsType", 2);
                  ((Bundle)localObject4).putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", NUM, new Object[] { UserObject.getUserName((TLRPC.User)localObject1), "%1$s" }));
                  localObject4 = new DialogsActivity((Bundle)localObject4);
                  ((DialogsActivity)localObject4).setDelegate(new DialogsActivity.DialogsActivityDelegate()
                  {
                    public void didSelectDialogs(DialogsActivity paramAnonymous2DialogsActivity, ArrayList<Long> paramAnonymous2ArrayList, CharSequence paramAnonymous2CharSequence, boolean paramAnonymous2Boolean)
                    {
                      long l = ((Long)paramAnonymous2ArrayList.get(0)).longValue();
                      paramAnonymous2ArrayList = new Bundle();
                      paramAnonymous2ArrayList.putBoolean("scrollToTopOnResume", true);
                      paramAnonymous2ArrayList.putInt("chat_id", -(int)l);
                      if (!MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(paramAnonymous2ArrayList, paramAnonymous2DialogsActivity)) {}
                      for (;;)
                      {
                        return;
                        NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                        NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        MessagesController.getInstance(ProfileActivity.this.currentAccount).addUserToChat(-(int)l, localObject1, null, 0, null, ProfileActivity.this);
                        ProfileActivity.this.presentFragment(new ChatActivity(paramAnonymous2ArrayList), true);
                        ProfileActivity.this.removeSelfFromStack();
                      }
                    }
                  });
                  ProfileActivity.this.presentFragment((BaseFragment)localObject4);
                }
              }
              else
              {
                if (paramAnonymousInt == 10) {
                  for (;;)
                  {
                    try
                    {
                      localObject4 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                      if (localObject4 == null) {
                        break;
                      }
                      localObject1 = new android/content/Intent;
                      ((Intent)localObject1).<init>("android.intent.action.SEND");
                      ((Intent)localObject1).setType("text/plain");
                      localObject5 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.botInfo.user_id);
                      if ((ProfileActivity.this.botInfo == null) || (localObject5 == null) || (TextUtils.isEmpty(((TLRPC.TL_userFull)localObject5).about))) {
                        break label1046;
                      }
                      StringBuilder localStringBuilder = new java/lang/StringBuilder;
                      localStringBuilder.<init>();
                      ((Intent)localObject1).putExtra("android.intent.extra.TEXT", String.format("%s https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[] { ((TLRPC.TL_userFull)localObject5).about, ((TLRPC.User)localObject4).username }));
                      ProfileActivity.this.startActivityForResult(Intent.createChooser((Intent)localObject1, LocaleController.getString("BotShare", NUM)), 500);
                    }
                    catch (Exception localException1)
                    {
                      FileLog.e(localException1);
                    }
                    break;
                    label1046:
                    Object localObject5 = new java/lang/StringBuilder;
                    ((StringBuilder)localObject5).<init>();
                    localException1.putExtra("android.intent.extra.TEXT", String.format("https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[] { ((TLRPC.User)localObject4).username }));
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
                }
                else if (paramAnonymousInt == 13)
                {
                  localObject2 = new Bundle();
                  ((Bundle)localObject2).putInt("chat_id", ProfileActivity.this.chat_id);
                  ProfileActivity.this.presentFragment(new ConvertGroupActivity((Bundle)localObject2));
                }
                else
                {
                  if (paramAnonymousInt == 14) {
                    for (;;)
                    {
                      long l;
                      try
                      {
                        if (ProfileActivity.this.currentEncryptedChat == null) {
                          break label1277;
                        }
                        l = ProfileActivity.this.currentEncryptedChat.id << 32;
                        DataQuery.getInstance(ProfileActivity.this.currentAccount).installShortcut(l);
                      }
                      catch (Exception localException2)
                      {
                        FileLog.e(localException2);
                      }
                      break;
                      label1277:
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
                  Object localObject3;
                  if (paramAnonymousInt == 15)
                  {
                    localObject3 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (localObject3 != null) {
                      VoIPHelper.startCall((TLRPC.User)localObject3, ProfileActivity.this.getParentActivity(), MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(((TLRPC.User)localObject3).id));
                    }
                  }
                  else if (paramAnonymousInt == 16)
                  {
                    localObject3 = new Bundle();
                    ((Bundle)localObject3).putInt("chat_id", ProfileActivity.this.chat_id);
                    if (ChatObject.isChannel(ProfileActivity.this.currentChat))
                    {
                      ((Bundle)localObject3).putInt("type", 2);
                      ((Bundle)localObject3).putBoolean("open_search", true);
                      ProfileActivity.this.presentFragment(new ChannelUsersActivity((Bundle)localObject3));
                    }
                    else
                    {
                      localObject3 = new ChatUsersActivity((Bundle)localObject3);
                      ((ChatUsersActivity)localObject3).setInfo(ProfileActivity.this.info);
                      ProfileActivity.this.presentFragment((BaseFragment)localObject3);
                    }
                  }
                }
              }
            }
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
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.listView = new RecyclerListView(paramContext)
    {
      public boolean hasOverlappingRendering()
      {
        return false;
      }
    };
    this.listView.setTag(Integer.valueOf(6));
    this.listView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
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
    Object localObject1 = this.listView;
    Object localObject2;
    if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id, this.currentAccount)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      ((RecyclerListView)localObject1).setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (ProfileActivity.this.getParentActivity() == null) {}
          for (;;)
          {
            return;
            final long l;
            if (paramAnonymousInt == ProfileActivity.this.sharedMediaRow)
            {
              paramAnonymousView = new Bundle();
              if (ProfileActivity.this.user_id != 0) {
                if (ProfileActivity.this.dialog_id != 0L)
                {
                  l = ProfileActivity.this.dialog_id;
                  label60:
                  paramAnonymousView.putLong("dialog_id", l);
                }
              }
              for (;;)
              {
                paramAnonymousView = new MediaActivity(paramAnonymousView);
                paramAnonymousView.setChatInfo(ProfileActivity.this.info);
                ProfileActivity.this.presentFragment(paramAnonymousView);
                break;
                l = ProfileActivity.this.user_id;
                break label60;
                paramAnonymousView.putLong("dialog_id", -ProfileActivity.this.chat_id);
              }
            }
            if (paramAnonymousInt == ProfileActivity.this.groupsInCommonRow)
            {
              ProfileActivity.this.presentFragment(new CommonGroupsActivity(ProfileActivity.this.user_id));
            }
            else if (paramAnonymousInt == ProfileActivity.this.settingsKeyRow)
            {
              paramAnonymousView = new Bundle();
              paramAnonymousView.putInt("chat_id", (int)(ProfileActivity.this.dialog_id >> 32));
              ProfileActivity.this.presentFragment(new IdenticonActivity(paramAnonymousView));
            }
            else if (paramAnonymousInt == ProfileActivity.this.settingsTimerRow)
            {
              ProfileActivity.this.showDialog(AlertsCreator.createTTLAlert(ProfileActivity.this.getParentActivity(), ProfileActivity.this.currentEncryptedChat).create());
            }
            else
            {
              Object localObject;
              if (paramAnonymousInt == ProfileActivity.this.settingsNotificationsRow)
              {
                if (ProfileActivity.this.dialog_id != 0L) {
                  l = ProfileActivity.this.dialog_id;
                }
                for (;;)
                {
                  String[] arrayOfString = new String[5];
                  arrayOfString[0] = LocaleController.getString("NotificationsTurnOn", NUM);
                  arrayOfString[1] = LocaleController.formatString("MuteFor", NUM, new Object[] { LocaleController.formatPluralString("Hours", 1) });
                  arrayOfString[2] = LocaleController.formatString("MuteFor", NUM, new Object[] { LocaleController.formatPluralString("Days", 2) });
                  arrayOfString[3] = LocaleController.getString("NotificationsCustomize", NUM);
                  arrayOfString[4] = LocaleController.getString("NotificationsTurnOff", NUM);
                  paramAnonymousView = new LinearLayout(ProfileActivity.this.getParentActivity());
                  paramAnonymousView.setOrientation(1);
                  for (paramAnonymousInt = 0; paramAnonymousInt < arrayOfString.length; paramAnonymousInt++)
                  {
                    localObject = new TextView(ProfileActivity.this.getParentActivity());
                    ((TextView)localObject).setTextColor(Theme.getColor("dialogTextBlack"));
                    ((TextView)localObject).setTextSize(1, 16.0F);
                    ((TextView)localObject).setLines(1);
                    ((TextView)localObject).setMaxLines(1);
                    Drawable localDrawable = ProfileActivity.this.getParentActivity().getResources().getDrawable(new int[] { NUM, NUM, NUM, NUM, NUM }[paramAnonymousInt]);
                    localDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
                    ((TextView)localObject).setCompoundDrawablesWithIntrinsicBounds(localDrawable, null, null, null);
                    ((TextView)localObject).setTag(Integer.valueOf(paramAnonymousInt));
                    ((TextView)localObject).setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    ((TextView)localObject).setPadding(AndroidUtilities.dp(24.0F), 0, AndroidUtilities.dp(24.0F), 0);
                    ((TextView)localObject).setSingleLine(true);
                    ((TextView)localObject).setGravity(19);
                    ((TextView)localObject).setCompoundDrawablePadding(AndroidUtilities.dp(26.0F));
                    ((TextView)localObject).setText(arrayOfString[paramAnonymousInt]);
                    paramAnonymousView.addView((View)localObject, LayoutHelper.createLinear(-1, 48, 51));
                    ((TextView)localObject).setOnClickListener(new View.OnClickListener()
                    {
                      public void onClick(View paramAnonymous2View)
                      {
                        int i = ((Integer)paramAnonymous2View.getTag()).intValue();
                        if (i == 0)
                        {
                          paramAnonymous2View = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount).edit();
                          paramAnonymous2View.putInt("notify2_" + l, 0);
                          MessagesStorage.getInstance(ProfileActivity.this.currentAccount).setDialogFlags(l, 0L);
                          paramAnonymous2View.commit();
                          paramAnonymous2View = (TLRPC.TL_dialog)MessagesController.getInstance(ProfileActivity.this.currentAccount).dialogs_dict.get(l);
                          if (paramAnonymous2View != null) {
                            paramAnonymous2View.notify_settings = new TLRPC.TL_peerNotifySettings();
                          }
                          NotificationsController.getInstance(ProfileActivity.this.currentAccount).updateServerNotificationsSettings(l);
                        }
                        for (;;)
                        {
                          ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.settingsNotificationsRow);
                          ProfileActivity.this.dismissCurrentDialig();
                          return;
                          if (i != 3) {
                            break;
                          }
                          paramAnonymous2View = new Bundle();
                          paramAnonymous2View.putLong("dialog_id", l);
                          ProfileActivity.this.presentFragment(new ProfileNotificationsActivity(paramAnonymous2View));
                        }
                        int j = ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime();
                        if (i == 1)
                        {
                          j += 3600;
                          label261:
                          paramAnonymous2View = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount).edit();
                          if (i != 4) {
                            break label464;
                          }
                          paramAnonymous2View.putInt("notify2_" + l, 2);
                        }
                        for (long l = 1L;; l = j << 32 | 1L)
                        {
                          NotificationsController.getInstance(ProfileActivity.this.currentAccount).removeNotificationsForDialog(l);
                          MessagesStorage.getInstance(ProfileActivity.this.currentAccount).setDialogFlags(l, l);
                          paramAnonymous2View.commit();
                          paramAnonymous2View = (TLRPC.TL_dialog)MessagesController.getInstance(ProfileActivity.this.currentAccount).dialogs_dict.get(l);
                          if (paramAnonymous2View != null)
                          {
                            paramAnonymous2View.notify_settings = new TLRPC.TL_peerNotifySettings();
                            paramAnonymous2View.notify_settings.mute_until = j;
                          }
                          NotificationsController.getInstance(ProfileActivity.this.currentAccount).updateServerNotificationsSettings(l);
                          break;
                          if (i == 2)
                          {
                            j += 172800;
                            break label261;
                          }
                          if (i != 4) {
                            break label261;
                          }
                          j = Integer.MAX_VALUE;
                          break label261;
                          label464:
                          paramAnonymous2View.putInt("notify2_" + l, 3);
                          paramAnonymous2View.putInt("notifyuntil_" + l, j);
                        }
                      }
                    });
                  }
                  if (ProfileActivity.this.user_id != 0) {
                    l = ProfileActivity.this.user_id;
                  } else {
                    l = -ProfileActivity.this.chat_id;
                  }
                }
                localObject = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
                ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("Notifications", NUM));
                ((AlertDialog.Builder)localObject).setView(paramAnonymousView);
                ProfileActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
              }
              else if (paramAnonymousInt == ProfileActivity.this.startSecretChatRow)
              {
                paramAnonymousView = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
                paramAnonymousView.setMessage(LocaleController.getString("AreYouSureSecretChat", NUM));
                paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
                paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    ProfileActivity.access$5802(ProfileActivity.this, true);
                    SecretChatHelper.getInstance(ProfileActivity.this.currentAccount).startSecretChat(ProfileActivity.this.getParentActivity(), MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)));
                  }
                });
                paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                ProfileActivity.this.showDialog(paramAnonymousView.create());
              }
              else if ((paramAnonymousInt > ProfileActivity.this.emptyRowChat2) && (paramAnonymousInt < ProfileActivity.this.membersEndRow))
              {
                if (!ProfileActivity.this.sortedUsers.isEmpty()) {}
                for (paramAnonymousInt = ((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(((Integer)ProfileActivity.this.sortedUsers.get(paramAnonymousInt - ProfileActivity.this.emptyRowChat2 - 1)).intValue())).user_id;; paramAnonymousInt = ((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramAnonymousInt - ProfileActivity.this.emptyRowChat2 - 1)).user_id)
                {
                  if (paramAnonymousInt == UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                    break label999;
                  }
                  paramAnonymousView = new Bundle();
                  paramAnonymousView.putInt("user_id", paramAnonymousInt);
                  ProfileActivity.this.presentFragment(new ProfileActivity(paramAnonymousView));
                  break;
                }
              }
              else
              {
                label999:
                if (paramAnonymousInt == ProfileActivity.this.addMemberRow)
                {
                  ProfileActivity.this.openAddMember();
                }
                else
                {
                  if (paramAnonymousInt == ProfileActivity.this.channelNameRow) {
                    for (;;)
                    {
                      try
                      {
                        paramAnonymousView = new android/content/Intent;
                        paramAnonymousView.<init>("android.intent.action.SEND");
                        paramAnonymousView.setType("text/plain");
                        if ((ProfileActivity.this.info.about == null) || (ProfileActivity.this.info.about.length() <= 0)) {
                          break label1214;
                        }
                        localObject = new java/lang/StringBuilder;
                        ((StringBuilder)localObject).<init>();
                        paramAnonymousView.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\n" + ProfileActivity.this.info.about + "\nhttps://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/" + ProfileActivity.this.currentChat.username);
                        ProfileActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(paramAnonymousView, LocaleController.getString("BotShare", NUM)), 500);
                      }
                      catch (Exception paramAnonymousView)
                      {
                        FileLog.e(paramAnonymousView);
                      }
                      break;
                      label1214:
                      localObject = new java/lang/StringBuilder;
                      ((StringBuilder)localObject).<init>();
                      paramAnonymousView.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\nhttps://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/" + ProfileActivity.this.currentChat.username);
                    }
                  }
                  if (paramAnonymousInt == ProfileActivity.this.leaveChannelRow)
                  {
                    ProfileActivity.this.leaveChatPressed();
                  }
                  else if (paramAnonymousInt == ProfileActivity.this.membersRow)
                  {
                    paramAnonymousView = new Bundle();
                    paramAnonymousView.putInt("chat_id", ProfileActivity.this.chat_id);
                    paramAnonymousView.putInt("type", 2);
                    ProfileActivity.this.presentFragment(new ChannelUsersActivity(paramAnonymousView));
                  }
                  else if (paramAnonymousInt == ProfileActivity.this.convertRow)
                  {
                    paramAnonymousView = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
                    paramAnonymousView.setMessage(LocaleController.getString("ConvertGroupAlert", NUM));
                    paramAnonymousView.setTitle(LocaleController.getString("ConvertGroupAlertWarning", NUM));
                    paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                    {
                      public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                      {
                        MessagesController.getInstance(ProfileActivity.this.currentAccount).convertToMegaGroup(ProfileActivity.this.getParentActivity(), ProfileActivity.this.chat_id);
                      }
                    });
                    paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    ProfileActivity.this.showDialog(paramAnonymousView.create());
                  }
                  else
                  {
                    ProfileActivity.this.processOnClickOrPress(paramAnonymousInt);
                  }
                }
              }
            }
          }
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(final View paramAnonymousView, int paramAnonymousInt)
        {
          boolean bool;
          if ((paramAnonymousInt > ProfileActivity.this.emptyRowChat2) && (paramAnonymousInt < ProfileActivity.this.membersEndRow)) {
            if (ProfileActivity.this.getParentActivity() == null) {
              bool = false;
            }
          }
          for (;;)
          {
            return bool;
            int i = 0;
            int j = 0;
            int k = 0;
            if (!ProfileActivity.this.sortedUsers.isEmpty()) {}
            final TLRPC.ChannelParticipant localChannelParticipant;
            for (paramAnonymousView = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(((Integer)ProfileActivity.this.sortedUsers.get(paramAnonymousInt - ProfileActivity.this.emptyRowChat2 - 1)).intValue());; paramAnonymousView = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramAnonymousInt - ProfileActivity.this.emptyRowChat2 - 1))
            {
              ProfileActivity.access$7502(ProfileActivity.this, paramAnonymousView.user_id);
              if (!ChatObject.isChannel(ProfileActivity.this.currentChat)) {
                break label446;
              }
              localChannelParticipant = ((TLRPC.TL_chatChannelParticipant)paramAnonymousView).channelParticipant;
              if (paramAnonymousView.user_id != UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                break label198;
              }
              bool = false;
              break;
            }
            label198:
            MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(paramAnonymousView.user_id));
            if (((localChannelParticipant instanceof TLRPC.TL_channelParticipant)) || ((localChannelParticipant instanceof TLRPC.TL_channelParticipantBanned)))
            {
              paramAnonymousInt = 1;
              label237:
              if ((((localChannelParticipant instanceof TLRPC.TL_channelParticipantAdmin)) || ((localChannelParticipant instanceof TLRPC.TL_channelParticipantCreator))) && (!localChannelParticipant.can_edit)) {
                break label437;
              }
              k = 1;
              j = paramAnonymousInt;
            }
            AlertDialog.Builder localBuilder;
            ArrayList localArrayList1;
            final ArrayList localArrayList2;
            label437:
            label446:
            label496:
            label558:
            for (;;)
            {
              localBuilder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
              localArrayList1 = new ArrayList();
              localArrayList2 = new ArrayList();
              if (!ProfileActivity.this.currentChat.megagroup) {
                break label560;
              }
              if ((j != 0) && (ChatObject.canAddAdmins(ProfileActivity.this.currentChat)))
              {
                localArrayList1.add(LocaleController.getString("SetAsAdmin", NUM));
                localArrayList2.add(Integer.valueOf(0));
              }
              if ((ChatObject.canBlockUsers(ProfileActivity.this.currentChat)) && (k != 0))
              {
                localArrayList1.add(LocaleController.getString("KickFromSupergroup", NUM));
                localArrayList2.add(Integer.valueOf(1));
                localArrayList1.add(LocaleController.getString("KickFromGroup", NUM));
                localArrayList2.add(Integer.valueOf(2));
              }
              if (!localArrayList1.isEmpty()) {
                break label612;
              }
              bool = false;
              break;
              paramAnonymousInt = 0;
              break label237;
              k = 0;
              j = paramAnonymousInt;
              continue;
              localChannelParticipant = null;
              paramAnonymousInt = i;
              if (paramAnonymousView.user_id != UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId())
              {
                if (!ProfileActivity.this.currentChat.creator) {
                  break label496;
                }
                paramAnonymousInt = 1;
              }
              for (;;)
              {
                if (paramAnonymousInt != 0) {
                  break label558;
                }
                bool = false;
                break;
                paramAnonymousInt = i;
                if ((paramAnonymousView instanceof TLRPC.TL_chatParticipant)) {
                  if ((!ProfileActivity.this.currentChat.admin) || (!ProfileActivity.this.currentChat.admins_enabled))
                  {
                    paramAnonymousInt = i;
                    if (paramAnonymousView.inviter_id != UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {}
                  }
                  else
                  {
                    paramAnonymousInt = 1;
                  }
                }
              }
            }
            label560:
            if (ProfileActivity.this.chat_id > 0) {}
            for (String str = LocaleController.getString("KickFromGroup", NUM);; str = LocaleController.getString("KickFromBroadcast", NUM))
            {
              localArrayList1.add(str);
              localArrayList2.add(Integer.valueOf(2));
              break;
            }
            label612:
            localBuilder.setItems((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, final int paramAnonymous2Int)
              {
                if (((Integer)localArrayList2.get(paramAnonymous2Int)).intValue() == 2) {
                  ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                }
                for (;;)
                {
                  return;
                  paramAnonymous2DialogInterface = new ChannelRightsEditActivity(paramAnonymousView.user_id, ProfileActivity.this.chat_id, localChannelParticipant.admin_rights, localChannelParticipant.banned_rights, ((Integer)localArrayList2.get(paramAnonymous2Int)).intValue(), true);
                  paramAnonymous2DialogInterface.setDelegate(new ChannelRightsEditActivity.ChannelRightsEditActivityDelegate()
                  {
                    public void didSetRights(int paramAnonymous3Int, TLRPC.TL_channelAdminRights paramAnonymous3TL_channelAdminRights, TLRPC.TL_channelBannedRights paramAnonymous3TL_channelBannedRights)
                    {
                      if (((Integer)ProfileActivity.10.1.this.val$actions.get(paramAnonymous2Int)).intValue() == 0)
                      {
                        localTL_chatChannelParticipant = (TLRPC.TL_chatChannelParticipant)ProfileActivity.10.1.this.val$user;
                        if (paramAnonymous3Int == 1)
                        {
                          localTL_chatChannelParticipant.channelParticipant = new TLRPC.TL_channelParticipantAdmin();
                          localTL_chatChannelParticipant.channelParticipant.inviter_id = UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId();
                          localTL_chatChannelParticipant.channelParticipant.user_id = ProfileActivity.10.1.this.val$user.user_id;
                          localTL_chatChannelParticipant.channelParticipant.date = ProfileActivity.10.1.this.val$user.date;
                          localTL_chatChannelParticipant.channelParticipant.banned_rights = paramAnonymous3TL_channelBannedRights;
                          localTL_chatChannelParticipant.channelParticipant.admin_rights = paramAnonymous3TL_channelAdminRights;
                        }
                      }
                      while ((((Integer)ProfileActivity.10.1.this.val$actions.get(paramAnonymous2Int)).intValue() != 1) || (paramAnonymous3Int != 0) || (!ProfileActivity.this.currentChat.megagroup) || (ProfileActivity.this.info == null) || (ProfileActivity.this.info.participants == null)) {
                        for (;;)
                        {
                          TLRPC.TL_chatChannelParticipant localTL_chatChannelParticipant;
                          return;
                          localTL_chatChannelParticipant.channelParticipant = new TLRPC.TL_channelParticipant();
                        }
                      }
                      int i = 0;
                      int j = 0;
                      label237:
                      paramAnonymous3Int = i;
                      if (j < ProfileActivity.this.info.participants.participants.size())
                      {
                        if (((TLRPC.TL_chatChannelParticipant)ProfileActivity.this.info.participants.participants.get(j)).channelParticipant.user_id == ProfileActivity.10.1.this.val$user.user_id)
                        {
                          if (ProfileActivity.this.info != null)
                          {
                            paramAnonymous3TL_channelAdminRights = ProfileActivity.this.info;
                            paramAnonymous3TL_channelAdminRights.participants_count -= 1;
                          }
                          ProfileActivity.this.info.participants.participants.remove(j);
                          paramAnonymous3Int = 1;
                        }
                      }
                      else
                      {
                        i = paramAnonymous3Int;
                        if (ProfileActivity.this.info != null)
                        {
                          i = paramAnonymous3Int;
                          if (ProfileActivity.this.info.participants == null) {}
                        }
                      }
                      for (j = 0;; j++)
                      {
                        i = paramAnonymous3Int;
                        if (j < ProfileActivity.this.info.participants.participants.size())
                        {
                          if (((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(j)).user_id == ProfileActivity.10.1.this.val$user.user_id)
                          {
                            ProfileActivity.this.info.participants.participants.remove(j);
                            i = 1;
                          }
                        }
                        else
                        {
                          if (i == 0) {
                            break;
                          }
                          ProfileActivity.this.updateOnlineCount();
                          ProfileActivity.this.updateRowsIds();
                          ProfileActivity.this.listAdapter.notifyDataSetChanged();
                          break;
                          j++;
                          break label237;
                        }
                      }
                    }
                  });
                  ProfileActivity.this.presentFragment(paramAnonymous2DialogInterface);
                }
              }
            });
            ProfileActivity.this.showDialog(localBuilder.create());
            bool = true;
            continue;
            bool = ProfileActivity.this.processOnClickOrPress(paramAnonymousInt);
          }
        }
      });
      if (this.banFromGroup == 0) {
        break label724;
      }
      if (this.currentChannelParticipant == null)
      {
        localObject1 = new TLRPC.TL_channels_getParticipant();
        ((TLRPC.TL_channels_getParticipant)localObject1).channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.banFromGroup);
        ((TLRPC.TL_channels_getParticipant)localObject1).user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user_id);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTLObject != null) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  ProfileActivity.access$8402(ProfileActivity.this, ((TLRPC.TL_channels_channelParticipant)paramAnonymousTLObject).participant);
                }
              });
            }
          }
        });
      }
      localObject1 = new FrameLayout(paramContext)
      {
        protected void onDraw(Canvas paramAnonymousCanvas)
        {
          int i = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
          Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), i);
          Theme.chat_composeShadowDrawable.draw(paramAnonymousCanvas);
          paramAnonymousCanvas.drawRect(0.0F, i, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
        }
      };
      ((FrameLayout)localObject1).setWillNotDraw(false);
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-1, 51, 83));
      ((FrameLayout)localObject1).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          int i = ProfileActivity.this.user_id;
          int j = ProfileActivity.this.banFromGroup;
          if (ProfileActivity.this.currentChannelParticipant != null) {}
          for (paramAnonymousView = ProfileActivity.this.currentChannelParticipant.banned_rights;; paramAnonymousView = null)
          {
            paramAnonymousView = new ChannelRightsEditActivity(i, j, null, paramAnonymousView, 1, true);
            paramAnonymousView.setDelegate(new ChannelRightsEditActivity.ChannelRightsEditActivityDelegate()
            {
              public void didSetRights(int paramAnonymous2Int, TLRPC.TL_channelAdminRights paramAnonymous2TL_channelAdminRights, TLRPC.TL_channelBannedRights paramAnonymous2TL_channelBannedRights)
              {
                ProfileActivity.this.removeSelfFromStack();
              }
            });
            ProfileActivity.this.presentFragment(paramAnonymousView);
            return;
          }
        }
      });
      localObject2 = new TextView(paramContext);
      ((TextView)localObject2).setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
      ((TextView)localObject2).setTextSize(1, 15.0F);
      ((TextView)localObject2).setGravity(17);
      ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((TextView)localObject2).setText(LocaleController.getString("BanFromTheGroup", NUM));
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 1.0F, 0.0F, 0.0F));
      this.listView.setPadding(0, AndroidUtilities.dp(88.0F), 0, AndroidUtilities.dp(48.0F));
      this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0F));
      label531:
      this.topView = new TopView(paramContext);
      localObject1 = this.topView;
      if ((this.user_id == 0) && ((!ChatObject.isChannel(this.chat_id, this.currentAccount)) || (this.currentChat.megagroup))) {
        break label743;
      }
    }
    label724:
    label743:
    for (int i = 5;; i = this.chat_id)
    {
      ((TopView)localObject1).setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i));
      localFrameLayout.addView(this.topView);
      localFrameLayout.addView(this.actionBar);
      this.avatarImage = new BackupImageView(paramContext);
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
      this.avatarImage.setPivotX(0.0F);
      this.avatarImage.setPivotY(0.0F);
      localFrameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0F, 51, 64.0F, 0.0F, 0.0F, 0.0F));
      this.avatarImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ProfileActivity.this.user_id != 0)
          {
            paramAnonymousView = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
            if ((paramAnonymousView.photo != null) && (paramAnonymousView.photo.photo_big != null))
            {
              PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
              PhotoViewer.getInstance().openPhoto(paramAnonymousView.photo.photo_big, ProfileActivity.this.provider);
            }
          }
          for (;;)
          {
            return;
            if (ProfileActivity.this.chat_id != 0)
            {
              paramAnonymousView = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
              if ((paramAnonymousView.photo != null) && (paramAnonymousView.photo.photo_big != null))
              {
                PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
                PhotoViewer.getInstance().openPhoto(paramAnonymousView.photo.photo_big, ProfileActivity.this.provider);
              }
            }
          }
        }
      });
      for (i = 0;; i++)
      {
        if (i >= 2) {
          break label1137;
        }
        if ((this.playProfileAnimation) || (i != 0)) {
          break;
        }
      }
      i = this.chat_id;
      break;
      this.listView.setPadding(0, AndroidUtilities.dp(88.0F), 0, 0);
      break label531;
    }
    this.nameTextView[i] = new SimpleTextView(paramContext);
    label789:
    label883:
    label907:
    int j;
    if (i == 1)
    {
      this.nameTextView[i].setTextColor(Theme.getColor("profile_title"));
      this.nameTextView[i].setTextSize(18);
      this.nameTextView[i].setGravity(3);
      this.nameTextView[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView[i].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3F));
      this.nameTextView[i].setPivotX(0.0F);
      this.nameTextView[i].setPivotY(0.0F);
      localObject1 = this.nameTextView[i];
      if (i != 0) {
        break label1102;
      }
      f = 0.0F;
      ((SimpleTextView)localObject1).setAlpha(f);
      localObject1 = this.nameTextView[i];
      if (i != 0) {
        break label1108;
      }
      f = 48.0F;
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, f, 0.0F));
      this.onlineTextView[i] = new SimpleTextView(paramContext);
      localObject1 = this.onlineTextView[i];
      if ((this.user_id == 0) && ((!ChatObject.isChannel(this.chat_id, this.currentAccount)) || (this.currentChat.megagroup))) {
        break label1114;
      }
      j = 5;
      label986:
      ((SimpleTextView)localObject1).setTextColor(AvatarDrawable.getProfileTextColorForId(j));
      this.onlineTextView[i].setTextSize(14);
      this.onlineTextView[i].setGravity(3);
      localObject1 = this.onlineTextView[i];
      if (i != 0) {
        break label1123;
      }
      f = 0.0F;
      label1034:
      ((SimpleTextView)localObject1).setAlpha(f);
      localObject1 = this.onlineTextView[i];
      if (i != 0) {
        break label1129;
      }
    }
    label1102:
    label1108:
    label1114:
    label1123:
    label1129:
    for (float f = 48.0F;; f = 8.0F)
    {
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, f, 0.0F));
      break;
      this.nameTextView[i].setTextColor(Theme.getColor("actionBarDefaultTitle"));
      break label789;
      f = 1.0F;
      break label883;
      f = 0.0F;
      break label907;
      j = this.chat_id;
      break label986;
      f = 1.0F;
      break label1034;
    }
    label1137:
    if ((this.user_id != 0) || ((this.chat_id >= 0) && ((!ChatObject.isLeftFromChat(this.currentChat)) || (ChatObject.isChannel(this.currentChat)))))
    {
      this.writeButton = new ImageView(paramContext);
      localObject2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
      localObject1 = localObject2;
      if (Build.VERSION.SDK_INT < 21)
      {
        paramContext = paramContext.getResources().getDrawable(NUM).mutate();
        paramContext.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        localObject1 = new CombinedDrawable(paramContext, (Drawable)localObject2, 0, 0);
        ((CombinedDrawable)localObject1).setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
      }
      this.writeButton.setBackgroundDrawable((Drawable)localObject1);
      this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
      this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
      if (this.user_id == 0) {
        break label1577;
      }
      this.writeButton.setImageResource(NUM);
      this.writeButton.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
      paramContext = this.writeButton;
      if (Build.VERSION.SDK_INT < 21) {
        break label1685;
      }
      i = 56;
      label1368:
      if (Build.VERSION.SDK_INT < 21) {
        break label1692;
      }
    }
    label1577:
    label1685:
    label1692:
    for (f = 56.0F;; f = 60.0F)
    {
      localFrameLayout.addView(paramContext, LayoutHelper.createFrame(i, f, 53, 0.0F, 0.0F, 16.0F, 0.0F));
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
          for (;;)
          {
            return;
            if (ProfileActivity.this.user_id != 0)
            {
              if ((ProfileActivity.this.playProfileAnimation) && ((ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)))
              {
                ProfileActivity.this.finishFragment();
              }
              else
              {
                paramAnonymousView = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                if ((paramAnonymousView != null) && (!(paramAnonymousView instanceof TLRPC.TL_userEmpty)))
                {
                  paramAnonymousView = new Bundle();
                  paramAnonymousView.putInt("user_id", ProfileActivity.this.user_id);
                  if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(paramAnonymousView, ProfileActivity.this))
                  {
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    ProfileActivity.this.presentFragment(new ChatActivity(paramAnonymousView), true);
                  }
                }
              }
            }
            else if (ProfileActivity.this.chat_id != 0)
            {
              boolean bool = ChatObject.isChannel(ProfileActivity.this.currentChat);
              if (((!bool) || (ChatObject.canEditInfo(ProfileActivity.this.currentChat))) && ((bool) || (ProfileActivity.this.currentChat.admin) || (ProfileActivity.this.currentChat.creator) || (!ProfileActivity.this.currentChat.admins_enabled))) {
                break;
              }
              if ((ProfileActivity.this.playProfileAnimation) && ((ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)))
              {
                ProfileActivity.this.finishFragment();
              }
              else
              {
                paramAnonymousView = new Bundle();
                paramAnonymousView.putInt("chat_id", ProfileActivity.this.currentChat.id);
                if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(paramAnonymousView, ProfileActivity.this))
                {
                  NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                  NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                  ProfileActivity.this.presentFragment(new ChatActivity(paramAnonymousView), true);
                }
              }
            }
          }
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
          paramAnonymousView = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
          if ((paramAnonymousView.photo == null) || (paramAnonymousView.photo.photo_big == null) || ((paramAnonymousView.photo instanceof TLRPC.TL_chatPhotoEmpty)))
          {
            paramAnonymousView = new CharSequence[2];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", NUM);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", NUM);
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
                for (;;)
                {
                  return;
                  if (paramAnonymous2Int == 1) {
                    ProfileActivity.this.avatarUpdater.openGallery();
                  } else if (paramAnonymous2Int == 2) {
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).changeChatAvatar(ProfileActivity.this.chat_id, null);
                  }
                }
              }
            });
            ProfileActivity.this.showDialog(localBuilder.create());
            break;
            paramAnonymousView = new CharSequence[3];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", NUM);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", NUM);
            paramAnonymousView[2] = LocaleController.getString("DeletePhoto", NUM);
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
      if (this.chat_id == 0) {
        break;
      }
      boolean bool = ChatObject.isChannel(this.currentChat);
      if (((bool) && (!ChatObject.canEditInfo(this.currentChat))) || ((!bool) && (!this.currentChat.admin) && (!this.currentChat.creator) && (this.currentChat.admins_enabled)))
      {
        this.writeButton.setImageResource(NUM);
        this.writeButton.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
        break;
      }
      this.writeButton.setImageResource(NUM);
      break;
      i = 60;
      break label1368;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, final Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.updateInterfaces)
    {
      paramInt2 = ((Integer)paramVarArgs[0]).intValue();
      if (this.user_id != 0)
      {
        if (((paramInt2 & 0x2) != 0) || ((paramInt2 & 0x1) != 0) || ((paramInt2 & 0x4) != 0)) {
          updateProfileData();
        }
        if (((paramInt2 & 0x400) != 0) && (this.listView != null))
        {
          paramVarArgs = (RecyclerListView.Holder)this.listView.findViewHolderForPosition(this.phoneRow);
          if (paramVarArgs != null) {
            this.listAdapter.onBindViewHolder(paramVarArgs, this.phoneRow);
          }
        }
      }
    }
    for (;;)
    {
      return;
      label361:
      label400:
      label489:
      label491:
      label505:
      label511:
      Object localObject;
      if (this.chat_id != 0)
      {
        if ((paramInt2 & 0x4000) != 0)
        {
          paramVarArgs = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
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
        if (((paramInt2 & 0x2000) != 0) || ((paramInt2 & 0x8) != 0) || ((paramInt2 & 0x10) != 0) || ((paramInt2 & 0x20) != 0) || ((paramInt2 & 0x4) != 0))
        {
          updateOnlineCount();
          updateProfileData();
        }
        if ((paramInt2 & 0x2000) != 0)
        {
          updateRowsIds();
          if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
          }
        }
        if ((((paramInt2 & 0x2) == 0) && ((paramInt2 & 0x1) == 0) && ((paramInt2 & 0x4) == 0)) || (this.listView == null)) {
          continue;
        }
        int i = this.listView.getChildCount();
        for (paramInt1 = 0; paramInt1 < i; paramInt1++)
        {
          paramVarArgs = this.listView.getChildAt(paramInt1);
          if ((paramVarArgs instanceof UserCell)) {
            ((UserCell)paramVarArgs).update(paramInt2);
          }
        }
        continue;
        if (paramInt1 == NotificationCenter.contactsDidLoaded)
        {
          createActionBarMenu();
          continue;
        }
        if (paramInt1 == NotificationCenter.mediaCountDidLoaded)
        {
          long l1 = ((Long)paramVarArgs[0]).longValue();
          long l2 = this.dialog_id;
          long l3 = l2;
          if (l2 == 0L)
          {
            if (this.user_id != 0) {
              l3 = this.user_id;
            }
          }
          else
          {
            if ((l1 != l3) && (l1 != this.mergeDialogId)) {
              break label489;
            }
            if (l1 != l3) {
              break label491;
            }
            this.totalMediaCount = ((Integer)paramVarArgs[1]).intValue();
            if (this.listView == null) {
              break label505;
            }
            paramInt2 = this.listView.getChildCount();
          }
          for (paramInt1 = 0;; paramInt1++)
          {
            if (paramInt1 >= paramInt2) {
              break label511;
            }
            paramVarArgs = this.listView.getChildAt(paramInt1);
            paramVarArgs = (RecyclerListView.Holder)this.listView.getChildViewHolder(paramVarArgs);
            if (paramVarArgs.getAdapterPosition() == this.sharedMediaRow)
            {
              this.listAdapter.onBindViewHolder(paramVarArgs, this.sharedMediaRow);
              break;
              l3 = l2;
              if (this.chat_id == 0) {
                break label361;
              }
              l3 = -this.chat_id;
              break label361;
              break;
              this.totalMediaCountMerge = ((Integer)paramVarArgs[1]).intValue();
              break label400;
              break;
            }
          }
          continue;
        }
        if (paramInt1 == NotificationCenter.encryptedChatCreated)
        {
          if (!this.creatingChat) {
            continue;
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
              NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
              TLRPC.EncryptedChat localEncryptedChat = (TLRPC.EncryptedChat)paramVarArgs[0];
              Bundle localBundle = new Bundle();
              localBundle.putInt("enc_id", localEncryptedChat.id);
              ProfileActivity.this.presentFragment(new ChatActivity(localBundle), true);
            }
          });
          continue;
        }
        if (paramInt1 == NotificationCenter.encryptedChatUpdated)
        {
          paramVarArgs = (TLRPC.EncryptedChat)paramVarArgs[0];
          if ((this.currentEncryptedChat == null) || (paramVarArgs.id != this.currentEncryptedChat.id)) {
            continue;
          }
          this.currentEncryptedChat = paramVarArgs;
          updateRowsIds();
          if (this.listAdapter == null) {
            continue;
          }
          this.listAdapter.notifyDataSetChanged();
          continue;
        }
        boolean bool;
        if (paramInt1 == NotificationCenter.blockedUsersDidLoaded)
        {
          bool = this.userBlocked;
          this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.contains(Integer.valueOf(this.user_id));
          if (bool == this.userBlocked) {
            continue;
          }
          createActionBarMenu();
          continue;
        }
        if (paramInt1 == NotificationCenter.chatInfoDidLoaded)
        {
          localObject = (TLRPC.ChatFull)paramVarArgs[0];
          if (((TLRPC.ChatFull)localObject).id != this.chat_id) {
            continue;
          }
          bool = ((Boolean)paramVarArgs[2]).booleanValue();
          if (((this.info instanceof TLRPC.TL_channelFull)) && (((TLRPC.ChatFull)localObject).participants == null) && (this.info != null)) {
            ((TLRPC.ChatFull)localObject).participants = this.info.participants;
          }
          if ((this.info == null) && ((localObject instanceof TLRPC.TL_channelFull))) {}
          for (paramInt1 = 1;; paramInt1 = 0)
          {
            this.info = ((TLRPC.ChatFull)localObject);
            if ((this.mergeDialogId == 0L) && (this.info.migrated_from_chat_id != 0))
            {
              this.mergeDialogId = (-this.info.migrated_from_chat_id);
              DataQuery.getInstance(this.currentAccount).getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
            }
            fetchUsersFromChannelInfo();
            updateOnlineCount();
            updateRowsIds();
            if (this.listAdapter != null) {
              this.listAdapter.notifyDataSetChanged();
            }
            paramVarArgs = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
            if (paramVarArgs != null)
            {
              this.currentChat = paramVarArgs;
              createActionBarMenu();
            }
            if ((!this.currentChat.megagroup) || ((paramInt1 == 0) && (bool))) {
              break;
            }
            getChannelParticipants(true);
            break;
          }
        }
        if (paramInt1 == NotificationCenter.closeChats)
        {
          removeSelfFromStack();
          continue;
        }
        if (paramInt1 == NotificationCenter.botInfoDidLoaded)
        {
          paramVarArgs = (TLRPC.BotInfo)paramVarArgs[0];
          if (paramVarArgs.user_id != this.user_id) {
            continue;
          }
          this.botInfo = paramVarArgs;
          updateRowsIds();
          if (this.listAdapter == null) {
            continue;
          }
          this.listAdapter.notifyDataSetChanged();
          continue;
        }
        if (paramInt1 != NotificationCenter.userInfoDidLoaded) {
          break label1030;
        }
        if (((Integer)paramVarArgs[0]).intValue() != this.user_id) {
          continue;
        }
        if ((this.openAnimationInProgress) || (this.callItem != null)) {
          break label1022;
        }
        createActionBarMenu();
      }
      for (;;)
      {
        updateRowsIds();
        if (this.listAdapter == null) {
          break;
        }
        this.listAdapter.notifyDataSetChanged();
        break;
        break;
        label1022:
        this.recreateMenuAfterAnimation = true;
      }
      label1030:
      if ((paramInt1 != NotificationCenter.didReceivedNewMessages) || (((Long)paramVarArgs[0]).longValue() != this.dialog_id)) {
        break;
      }
      paramVarArgs = (ArrayList)paramVarArgs[1];
      for (paramInt1 = 0; paramInt1 < paramVarArgs.size(); paramInt1++)
      {
        localObject = (MessageObject)paramVarArgs.get(paramInt1);
        if ((this.currentEncryptedChat != null) && (((MessageObject)localObject).messageOwner.action != null) && ((((MessageObject)localObject).messageOwner.action instanceof TLRPC.TL_messageEncryptedAction)) && ((((MessageObject)localObject).messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)))
        {
          localObject = (TLRPC.TL_decryptedMessageActionSetMessageTTL)((MessageObject)localObject).messageOwner.action.encryptedAction;
          if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
          }
        }
      }
    }
  }
  
  public void didSelectDialogs(DialogsActivity paramDialogsActivity, ArrayList<Long> paramArrayList, CharSequence paramCharSequence, boolean paramBoolean)
  {
    long l = ((Long)paramArrayList.get(0)).longValue();
    paramArrayList = new Bundle();
    paramArrayList.putBoolean("scrollToTopOnResume", true);
    int i = (int)l;
    if (i != 0) {
      if (i > 0) {
        paramArrayList.putInt("user_id", i);
      }
    }
    label187:
    for (;;)
    {
      if (!MessagesController.getInstance(this.currentAccount).checkCanOpenChat(paramArrayList, paramDialogsActivity)) {}
      for (;;)
      {
        return;
        if (i >= 0) {
          break label187;
        }
        paramArrayList.putInt("chat_id", -i);
        break;
        paramArrayList.putInt("enc_id", (int)(l >> 32));
        break;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        presentFragment(new ChatActivity(paramArrayList), true);
        removeSelfFromStack();
        paramDialogsActivity = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(paramDialogsActivity, l, null, null, null);
      }
    }
  }
  
  public float getAnimationProgress()
  {
    return this.animationProgress;
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    Object localObject1 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        if (ProfileActivity.this.listView != null)
        {
          int i = ProfileActivity.this.listView.getChildCount();
          for (int j = 0; j < i; j++)
          {
            View localView = ProfileActivity.this.listView.getChildAt(j);
            if ((localView instanceof UserCell)) {
              ((UserCell)localView).update(0);
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
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorBlue");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.nameTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "profile_title");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileBlue");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarRed");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarRed");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarRed");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorRed");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileRed");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconRed");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarOrange");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarOrange");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarOrange");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorOrange");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileOrange");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconOrange");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarViolet");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarViolet");
    ThemeDescription localThemeDescription24 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarViolet");
    ThemeDescription localThemeDescription25 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorViolet");
    ThemeDescription localThemeDescription26 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileViolet");
    ThemeDescription localThemeDescription27 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconViolet");
    ThemeDescription localThemeDescription28 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarGreen");
    ThemeDescription localThemeDescription29 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarGreen");
    ThemeDescription localThemeDescription30 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarGreen");
    ThemeDescription localThemeDescription31 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorGreen");
    ThemeDescription localThemeDescription32 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileGreen");
    ThemeDescription localThemeDescription33 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconGreen");
    ThemeDescription localThemeDescription34 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarCyan");
    ThemeDescription localThemeDescription35 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarCyan");
    ThemeDescription localThemeDescription36 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarCyan");
    ThemeDescription localThemeDescription37 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorCyan");
    ThemeDescription localThemeDescription38 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileCyan");
    ThemeDescription localThemeDescription39 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconCyan");
    ThemeDescription localThemeDescription40 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarPink");
    ThemeDescription localThemeDescription41 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarPink");
    ThemeDescription localThemeDescription42 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarPink");
    ThemeDescription localThemeDescription43 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorPink");
    ThemeDescription localThemeDescription44 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfilePink");
    ThemeDescription localThemeDescription45 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconPink");
    ThemeDescription localThemeDescription46 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    Object localObject2 = this.listView;
    Object localObject3 = Theme.dividerPaint;
    ThemeDescription localThemeDescription47 = new ThemeDescription((View)localObject2, 0, new Class[] { View.class }, (Paint)localObject3, null, null, "divider");
    ThemeDescription localThemeDescription48 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription49 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable }, null, "avatar_text");
    ThemeDescription localThemeDescription50 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileRed");
    ThemeDescription localThemeDescription51 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileOrange");
    ThemeDescription localThemeDescription52 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileViolet");
    ThemeDescription localThemeDescription53 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileGreen");
    ThemeDescription localThemeDescription54 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileCyan");
    ThemeDescription localThemeDescription55 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileBlue");
    ThemeDescription localThemeDescription56 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfilePink");
    ThemeDescription localThemeDescription57 = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon");
    ThemeDescription localThemeDescription58 = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground");
    ThemeDescription localThemeDescription59 = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground");
    localObject2 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription60 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGreenText2");
    ThemeDescription localThemeDescription61 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText5");
    ThemeDescription localThemeDescription62 = new ThemeDescription(this.listView, 0, new Class[] { TextCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText");
    localObject3 = new ThemeDescription(this.listView, 0, new Class[] { TextCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription63 = new ThemeDescription(this.listView, 0, new Class[] { TextDetailCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription64 = new ThemeDescription(this.listView, 0, new Class[] { TextDetailCell.class }, new String[] { "valueImageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription65 = new ThemeDescription(this.listView, 0, new Class[] { TextDetailCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription66 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { UserCell.class }, new String[] { "adminImage" }, null, null, null, "profile_creatorIcon");
    ThemeDescription localThemeDescription67 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { UserCell.class }, new String[] { "adminImage" }, null, null, null, "profile_adminIcon");
    ThemeDescription localThemeDescription68 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription69 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "windowBackgroundWhiteGrayText");
    ThemeDescription localThemeDescription70 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusOnlineColor" }, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "windowBackgroundWhiteBlueText");
    Object localObject4 = this.listView;
    Object localObject5 = Theme.avatar_photoDrawable;
    Object localObject6 = Theme.avatar_broadcastDrawable;
    Object localObject7 = Theme.avatar_savedDrawable;
    localObject6 = new ThemeDescription((View)localObject4, 0, new Class[] { UserCell.class }, null, new Drawable[] { localObject5, localObject6, localObject7 }, null, "avatar_text");
    ThemeDescription localThemeDescription71 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundRed");
    ThemeDescription localThemeDescription72 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundOrange");
    localObject4 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundViolet");
    localObject7 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundGreen");
    ThemeDescription localThemeDescription73 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundCyan");
    localObject5 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundBlue");
    ThemeDescription localThemeDescription74 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject1, "avatar_backgroundPink");
    ThemeDescription localThemeDescription75 = new ThemeDescription(this.listView, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle");
    localObject1 = new ThemeDescription(this.listView, 0, new Class[] { AboutLinkCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    Object localObject8 = this.listView;
    int i = ThemeDescription.FLAG_TEXTCOLOR;
    Object localObject9 = Theme.profile_aboutTextPaint;
    localObject8 = new ThemeDescription((View)localObject8, i, new Class[] { AboutLinkCell.class }, (Paint)localObject9, null, null, "windowBackgroundWhiteBlackText");
    Object localObject10 = this.listView;
    i = ThemeDescription.FLAG_LINKCOLOR;
    localObject9 = Theme.profile_aboutTextPaint;
    localObject10 = new ThemeDescription((View)localObject10, i, new Class[] { AboutLinkCell.class }, (Paint)localObject9, null, null, "windowBackgroundWhiteLinkText");
    RecyclerListView localRecyclerListView = this.listView;
    localObject9 = Theme.linkSelectionPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, localThemeDescription21, localThemeDescription22, localThemeDescription23, localThemeDescription24, localThemeDescription25, localThemeDescription26, localThemeDescription27, localThemeDescription28, localThemeDescription29, localThemeDescription30, localThemeDescription31, localThemeDescription32, localThemeDescription33, localThemeDescription34, localThemeDescription35, localThemeDescription36, localThemeDescription37, localThemeDescription38, localThemeDescription39, localThemeDescription40, localThemeDescription41, localThemeDescription42, localThemeDescription43, localThemeDescription44, localThemeDescription45, localThemeDescription46, localThemeDescription47, localThemeDescription48, localThemeDescription49, localThemeDescription50, localThemeDescription51, localThemeDescription52, localThemeDescription53, localThemeDescription54, localThemeDescription55, localThemeDescription56, localThemeDescription57, localThemeDescription58, localThemeDescription59, localObject2, localThemeDescription60, localThemeDescription61, localThemeDescription62, localObject3, localThemeDescription63, localThemeDescription64, localThemeDescription65, localThemeDescription66, localThemeDescription67, localThemeDescription68, localThemeDescription69, localThemeDescription70, localObject6, localThemeDescription71, localThemeDescription72, localObject4, localObject7, localThemeDescription73, localObject5, localThemeDescription74, localThemeDescription75, localObject1, localObject8, localObject10, new ThemeDescription(localRecyclerListView, 0, new Class[] { AboutLinkCell.class }, (Paint)localObject9, null, null, "windowBackgroundWhiteLinkSelection"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[] { Theme.profile_verifiedCheckDrawable }, null, "profile_verifiedCheck"), new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[] { Theme.profile_verifiedDrawable }, null, "profile_verifiedBackground") };
  }
  
  public boolean isChat()
  {
    if (this.chat_id != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
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
    final AnimatorSet localAnimatorSet;
    Object localObject;
    int i;
    float f1;
    label419:
    SimpleTextView localSimpleTextView;
    if ((this.playProfileAnimation) && (this.allowProfileAnimation))
    {
      localAnimatorSet = new AnimatorSet();
      localAnimatorSet.setDuration(180L);
      this.listView.setLayerType(2, null);
      localObject = this.actionBar.createMenu();
      if ((((ActionBarMenu)localObject).getItem(10) == null) && (this.animatingItem == null)) {
        this.animatingItem = ((ActionBarMenu)localObject).addItem(10, NUM);
      }
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
            break label591;
          }
          localSimpleTextView = this.onlineTextView[i];
          if (i != 0) {
            break label567;
          }
          f1 = 1.0F;
          label442:
          localSimpleTextView.setAlpha(f1);
          localSimpleTextView = this.nameTextView[i];
          if (i != 0) {
            break label573;
          }
          f1 = 1.0F;
          label466:
          localSimpleTextView.setAlpha(f1);
          localSimpleTextView = this.onlineTextView[i];
          if (i != 0) {
            break label579;
          }
          f1 = 0.0F;
          label490:
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
          localSimpleTextView = this.nameTextView[i];
          if (i != 0) {
            break label585;
          }
        }
        label567:
        label573:
        label579:
        label585:
        for (f1 = 0.0F;; f1 = 1.0F)
        {
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
          i++;
          break label419;
          ((FrameLayout.LayoutParams)localObject).width = -2;
          break;
          f1 = 0.0F;
          break label442;
          f1 = 0.0F;
          break label466;
          f1 = 1.0F;
          break label490;
        }
        label591:
        if (this.animatingItem != null)
        {
          this.animatingItem.setAlpha(1.0F);
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", new float[] { 0.0F }));
        }
        if (this.callItem != null)
        {
          this.callItem.setAlpha(0.0F);
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.callItem, "alpha", new float[] { 1.0F }));
        }
        if (this.editItem != null)
        {
          this.editItem.setAlpha(0.0F);
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.editItem, "alpha", new float[] { 1.0F }));
        }
        localAnimatorSet.playTogether((Collection)localObject);
        localAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            ProfileActivity.this.listView.setLayerType(0, null);
            if (ProfileActivity.this.animatingItem != null)
            {
              ProfileActivity.this.actionBar.createMenu().clearItems();
              ProfileActivity.access$12402(ProfileActivity.this, null);
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
      }
    }
    for (paramRunnable = localAnimatorSet;; paramRunnable = null)
    {
      return paramRunnable;
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
          label901:
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
          localSimpleTextView = this.nameTextView[i];
          if (i != 0) {
            break label974;
          }
        }
        label974:
        for (f1 = 1.0F;; f1 = 0.0F)
        {
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
          i++;
          break;
          f1 = 0.0F;
          break label901;
        }
      }
      if (this.animatingItem != null)
      {
        this.animatingItem.setAlpha(0.0F);
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", new float[] { 1.0F }));
      }
      if (this.callItem != null)
      {
        this.callItem.setAlpha(1.0F);
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.callItem, "alpha", new float[] { 0.0F }));
      }
      if (this.editItem != null)
      {
        this.editItem.setAlpha(1.0F);
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.editItem, "alpha", new float[] { 0.0F }));
      }
      localAnimatorSet.playTogether((Collection)localObject);
      break;
    }
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    if (this.listView != null) {
      this.listView.invalidateViews();
    }
  }
  
  public boolean onFragmentCreate()
  {
    boolean bool1 = false;
    this.user_id = this.arguments.getInt("user_id", 0);
    this.chat_id = this.arguments.getInt("chat_id", 0);
    this.banFromGroup = this.arguments.getInt("ban_chat_id", 0);
    final Object localObject;
    boolean bool2;
    if (this.user_id != 0)
    {
      this.dialog_id = this.arguments.getLong("dialog_id", 0L);
      if (this.dialog_id != 0L) {
        this.currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int)(this.dialog_id >> 32)));
      }
      localObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
      if (localObject == null)
      {
        bool2 = bool1;
        return bool2;
      }
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatUpdated);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoaded);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.botInfoDidLoaded);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoaded);
      if (this.currentEncryptedChat != null) {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceivedNewMessages);
      }
      this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.contains(Integer.valueOf(this.user_id));
      if (((TLRPC.User)localObject).bot)
      {
        this.isBot = true;
        DataQuery.getInstance(this.currentAccount).loadBotInfo(((TLRPC.User)localObject).id, true, this.classGuid);
      }
      MessagesController.getInstance(this.currentAccount).loadFullUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
      this.participantsMap = null;
      label340:
      if (this.dialog_id == 0L) {
        break label642;
      }
      DataQuery.getInstance(this.currentAccount).getMediaCount(this.dialog_id, 0, this.classGuid, true);
    }
    for (;;)
    {
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountDidLoaded);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
      updateRowsIds();
      bool2 = true;
      break;
      bool2 = bool1;
      if (this.chat_id == 0) {
        break;
      }
      this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
      if (this.currentChat == null)
      {
        localObject = new CountDownLatch(1);
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
        {
          public void run()
          {
            ProfileActivity.access$902(ProfileActivity.this, MessagesStorage.getInstance(ProfileActivity.this.currentAccount).getChat(ProfileActivity.this.chat_id));
            localObject.countDown();
          }
        });
      }
      try
      {
        ((CountDownLatch)localObject).await();
        bool2 = bool1;
        if (this.currentChat == null) {
          break;
        }
        MessagesController.getInstance(this.currentAccount).putChat(this.currentChat, true);
        if (this.currentChat.megagroup)
        {
          getChannelParticipants(true);
          NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
          this.sortedUsers = new ArrayList();
          updateOnlineCount();
          this.avatarUpdater = new AvatarUpdater();
          this.avatarUpdater.delegate = new AvatarUpdater.AvatarUpdaterDelegate()
          {
            public void didUploadedPhoto(TLRPC.InputFile paramAnonymousInputFile, TLRPC.PhotoSize paramAnonymousPhotoSize1, TLRPC.PhotoSize paramAnonymousPhotoSize2)
            {
              if (ProfileActivity.this.chat_id != 0) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).changeChatAvatar(ProfileActivity.this.chat_id, paramAnonymousInputFile);
              }
            }
          };
          this.avatarUpdater.parentFragment = this;
          if (!ChatObject.isChannel(this.currentChat)) {
            break label340;
          }
          MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
          continue;
          this.participantsMap = null;
        }
      }
      label642:
      if (this.user_id != 0)
      {
        DataQuery.getInstance(this.currentAccount).getMediaCount(this.user_id, 0, this.classGuid, true);
      }
      else if (this.chat_id > 0)
      {
        DataQuery.getInstance(this.currentAccount).getMediaCount(-this.chat_id, 0, this.classGuid, true);
        if (this.mergeDialogId != 0L) {
          DataQuery.getInstance(this.currentAccount).getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
        }
      }
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    if (this.user_id != 0)
    {
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatUpdated);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botInfoDidLoaded);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoaded);
      MessagesController.getInstance(this.currentAccount).cancelLoadFullUser(this.user_id);
      if (this.currentEncryptedChat != null) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedNewMessages);
      }
    }
    for (;;)
    {
      return;
      if (this.chat_id != 0)
      {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        this.avatarUpdater.clear();
      }
    }
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 101)
    {
      paramArrayOfString = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
      if (paramArrayOfString != null) {
        break label29;
      }
    }
    for (;;)
    {
      return;
      label29:
      if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0)) {
        VoIPHelper.startCall(paramArrayOfString, getParentActivity(), MessagesController.getInstance(this.currentAccount).getUserFull(paramArrayOfString.id));
      } else {
        VoIPHelper.permissionDenied(getParentActivity(), null);
      }
    }
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
    if ((!paramBoolean2) && (this.playProfileAnimation) && (this.allowProfileAnimation))
    {
      this.openAnimationInProgress = false;
      if (this.recreateMenuAfterAnimation) {
        createActionBarMenu();
      }
    }
    NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
  }
  
  protected void onTransitionAnimationStart(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean2) && (this.playProfileAnimation) && (this.allowProfileAnimation)) {
      this.openAnimationInProgress = true;
    }
    NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded });
    NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
  }
  
  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.chat_id != 0)
    {
      MessagesController.getInstance(this.currentAccount).loadChatInfo(this.chat_id, null, false);
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
  
  @Keep
  public void setAnimationProgress(float paramFloat)
  {
    this.animationProgress = paramFloat;
    this.listView.setAlpha(paramFloat);
    this.listView.setTranslationX(AndroidUtilities.dp(48.0F) - AndroidUtilities.dp(48.0F) * paramFloat);
    int j;
    int k;
    int m;
    int n;
    int i1;
    label196:
    int i2;
    int i3;
    int i4;
    if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id, this.currentAccount)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      j = AvatarDrawable.getProfileBackColorForId(i);
      k = Theme.getColor("actionBarDefault");
      m = Color.red(k);
      i = Color.green(k);
      n = Color.blue(k);
      k = (int)((Color.red(j) - m) * paramFloat);
      i1 = (int)((Color.green(j) - i) * paramFloat);
      j = (int)((Color.blue(j) - n) * paramFloat);
      this.topView.setBackgroundColor(Color.rgb(m + k, i + i1, n + j));
      if ((this.user_id == 0) && ((!ChatObject.isChannel(this.chat_id, this.currentAccount)) || (this.currentChat.megagroup))) {
        break label417;
      }
      i = 5;
      n = AvatarDrawable.getIconColorForId(i);
      k = Theme.getColor("actionBarDefaultIcon");
      m = Color.red(k);
      i = Color.green(k);
      i1 = Color.blue(k);
      k = (int)((Color.red(n) - m) * paramFloat);
      j = (int)((Color.green(n) - i) * paramFloat);
      n = (int)((Color.blue(n) - i1) * paramFloat);
      this.actionBar.setItemsColor(Color.rgb(m + k, i + j, i1 + n), false);
      i = Theme.getColor("profile_title");
      i1 = Theme.getColor("actionBarDefaultTitle");
      m = Color.red(i1);
      j = Color.green(i1);
      k = Color.blue(i1);
      i2 = Color.alpha(i1);
      n = (int)((Color.red(i) - m) * paramFloat);
      i3 = (int)((Color.green(i) - j) * paramFloat);
      i1 = (int)((Color.blue(i) - k) * paramFloat);
      i4 = (int)((Color.alpha(i) - i2) * paramFloat);
      i = 0;
      label389:
      if (i >= 2) {
        break label459;
      }
      if (this.nameTextView[i] != null) {
        break label425;
      }
    }
    for (;;)
    {
      i++;
      break label389;
      i = this.chat_id;
      break;
      label417:
      i = this.chat_id;
      break label196;
      label425:
      this.nameTextView[i].setTextColor(Color.argb(i2 + i4, m + n, j + i3, k + i1));
    }
    label459:
    if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id, this.currentAccount)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      i = AvatarDrawable.getProfileTextColorForId(i);
      i1 = Theme.getColor("actionBarDefaultSubtitle");
      k = Color.red(i1);
      m = Color.green(i1);
      j = Color.blue(i1);
      i2 = Color.alpha(i1);
      i1 = (int)((Color.red(i) - k) * paramFloat);
      i3 = (int)((Color.green(i) - m) * paramFloat);
      n = (int)((Color.blue(i) - j) * paramFloat);
      i4 = (int)((Color.alpha(i) - i2) * paramFloat);
      i = 0;
      label585:
      if (i >= 2) {
        break label647;
      }
      if (this.onlineTextView[i] != null) {
        break label613;
      }
    }
    for (;;)
    {
      i++;
      break label585;
      i = this.chat_id;
      break;
      label613:
      this.onlineTextView[i].setTextColor(Color.argb(i2 + i4, k + i1, m + i3, j + n));
    }
    label647:
    this.extraHeight = ((int)(this.initialAnimationExtraHeight * paramFloat));
    if (this.user_id != 0)
    {
      i = this.user_id;
      m = AvatarDrawable.getProfileColorForId(i);
      if (this.user_id == 0) {
        break label797;
      }
    }
    label797:
    for (int i = this.user_id;; i = this.chat_id)
    {
      j = AvatarDrawable.getColorForId(i);
      if (m != j)
      {
        k = (int)((Color.red(m) - Color.red(j)) * paramFloat);
        i = (int)((Color.green(m) - Color.green(j)) * paramFloat);
        m = (int)((Color.blue(m) - Color.blue(j)) * paramFloat);
        this.avatarDrawable.setColor(Color.rgb(Color.red(j) + k, Color.green(j) + i, Color.blue(j) + m));
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
  
  public void setPlayProfileAnimation(boolean paramBoolean)
  {
    SharedPreferences localSharedPreferences = MessagesController.getGlobalMainSettings();
    if ((!AndroidUtilities.isTablet()) && (localSharedPreferences.getBoolean("view_animations", true))) {
      this.playProfileAnimation = paramBoolean;
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
      return ProfileActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      int j = i;
      if (paramInt != ProfileActivity.this.emptyRow)
      {
        j = i;
        if (paramInt != ProfileActivity.this.emptyRowChat)
        {
          if (paramInt != ProfileActivity.this.emptyRowChat2) {
            break label43;
          }
          j = i;
        }
      }
      for (;;)
      {
        return j;
        label43:
        if ((paramInt == ProfileActivity.this.sectionRow) || (paramInt == ProfileActivity.this.userSectionRow))
        {
          j = 1;
        }
        else if ((paramInt == ProfileActivity.this.phoneRow) || (paramInt == ProfileActivity.this.usernameRow) || (paramInt == ProfileActivity.this.channelNameRow) || (paramInt == ProfileActivity.this.userInfoDetailedRow))
        {
          j = 2;
        }
        else if ((paramInt == ProfileActivity.this.leaveChannelRow) || (paramInt == ProfileActivity.this.sharedMediaRow) || (paramInt == ProfileActivity.this.settingsTimerRow) || (paramInt == ProfileActivity.this.settingsNotificationsRow) || (paramInt == ProfileActivity.this.startSecretChatRow) || (paramInt == ProfileActivity.this.settingsKeyRow) || (paramInt == ProfileActivity.this.convertRow) || (paramInt == ProfileActivity.this.addMemberRow) || (paramInt == ProfileActivity.this.groupsInCommonRow) || (paramInt == ProfileActivity.this.membersRow))
        {
          j = 3;
        }
        else if ((paramInt > ProfileActivity.this.emptyRowChat2) && (paramInt < ProfileActivity.this.membersEndRow))
        {
          j = 4;
        }
        else if (paramInt == ProfileActivity.this.membersSectionRow)
        {
          j = 5;
        }
        else if (paramInt == ProfileActivity.this.convertHelpRow)
        {
          j = 6;
        }
        else if (paramInt == ProfileActivity.this.loadMoreMembersRow)
        {
          j = 7;
        }
        else if (paramInt != ProfileActivity.this.userInfoRow)
        {
          j = i;
          if (paramInt != ProfileActivity.this.channelInfoRow) {}
        }
        else
        {
          j = 8;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool1 = false;
      int i = paramViewHolder.getAdapterPosition();
      boolean bool2;
      if (ProfileActivity.this.user_id != 0) {
        if ((i != ProfileActivity.this.phoneRow) && (i != ProfileActivity.this.settingsTimerRow) && (i != ProfileActivity.this.settingsKeyRow) && (i != ProfileActivity.this.settingsNotificationsRow) && (i != ProfileActivity.this.sharedMediaRow) && (i != ProfileActivity.this.startSecretChatRow) && (i != ProfileActivity.this.usernameRow) && (i != ProfileActivity.this.userInfoRow) && (i != ProfileActivity.this.groupsInCommonRow))
        {
          bool2 = bool1;
          if (i != ProfileActivity.this.userInfoDetailedRow) {}
        }
        else
        {
          bool2 = true;
        }
      }
      for (;;)
      {
        return bool2;
        bool2 = bool1;
        if (ProfileActivity.this.chat_id != 0) {
          if ((i != ProfileActivity.this.convertRow) && (i != ProfileActivity.this.settingsNotificationsRow) && (i != ProfileActivity.this.sharedMediaRow) && ((i <= ProfileActivity.this.emptyRowChat2) || (i >= ProfileActivity.this.membersEndRow)) && (i != ProfileActivity.this.addMemberRow) && (i != ProfileActivity.this.channelNameRow) && (i != ProfileActivity.this.leaveChannelRow) && (i != ProfileActivity.this.channelInfoRow))
          {
            bool2 = bool1;
            if (i != ProfileActivity.this.membersRow) {}
          }
          else
          {
            bool2 = true;
          }
        }
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
        if ((paramInt == ProfileActivity.this.emptyRowChat) || (paramInt == ProfileActivity.this.emptyRowChat2))
        {
          ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(8.0F));
        }
        else
        {
          ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(36.0F));
          continue;
          Object localObject1 = (TextDetailCell)paramViewHolder.itemView;
          ((TextDetailCell)localObject1).setMultiline(false);
          if (paramInt == ProfileActivity.this.phoneRow)
          {
            paramViewHolder = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
            if ((paramViewHolder.phone != null) && (paramViewHolder.phone.length() != 0)) {}
            for (paramViewHolder = PhoneFormat.getInstance().format("+" + paramViewHolder.phone);; paramViewHolder = LocaleController.getString("NumberUnknown", NUM))
            {
              ((TextDetailCell)localObject1).setTextAndValueAndIcon(paramViewHolder, LocaleController.getString("PhoneMobile", NUM), NUM, 0);
              break;
            }
          }
          if (paramInt == ProfileActivity.this.usernameRow)
          {
            paramViewHolder = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
            if ((paramViewHolder != null) && (!TextUtils.isEmpty(paramViewHolder.username))) {}
            for (paramViewHolder = "@" + paramViewHolder.username;; paramViewHolder = "-")
            {
              if ((ProfileActivity.this.phoneRow != -1) || (ProfileActivity.this.userInfoRow != -1) || (ProfileActivity.this.userInfoDetailedRow != -1)) {
                break label368;
              }
              ((TextDetailCell)localObject1).setTextAndValueAndIcon(paramViewHolder, LocaleController.getString("Username", NUM), NUM, 11);
              break;
            }
            label368:
            ((TextDetailCell)localObject1).setTextAndValue(paramViewHolder, LocaleController.getString("Username", NUM));
          }
          else
          {
            if (paramInt == ProfileActivity.this.channelNameRow)
            {
              if ((ProfileActivity.this.currentChat != null) && (!TextUtils.isEmpty(ProfileActivity.this.currentChat.username))) {}
              for (paramViewHolder = "@" + ProfileActivity.this.currentChat.username;; paramViewHolder = "-")
              {
                ((TextDetailCell)localObject1).setTextAndValue(paramViewHolder, MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/" + ProfileActivity.this.currentChat.username);
                break;
              }
            }
            if (paramInt == ProfileActivity.this.userInfoDetailedRow)
            {
              paramViewHolder = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
              ((TextDetailCell)localObject1).setMultiline(true);
              if (paramViewHolder != null) {}
              for (paramViewHolder = paramViewHolder.about;; paramViewHolder = null)
              {
                ((TextDetailCell)localObject1).setTextAndValueAndIcon(paramViewHolder, LocaleController.getString("UserBio", NUM), NUM, 11);
                break;
              }
              localObject1 = (TextCell)paramViewHolder.itemView;
              ((TextCell)localObject1).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
              ((TextCell)localObject1).setTag("windowBackgroundWhiteBlackText");
              int i;
              if (paramInt == ProfileActivity.this.sharedMediaRow)
              {
                if (ProfileActivity.this.totalMediaCount == -1)
                {
                  paramViewHolder = LocaleController.getString("Loading", NUM);
                  if ((ProfileActivity.this.user_id != 0) && (UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId() == ProfileActivity.this.user_id)) {
                    ((TextCell)localObject1).setTextAndValueAndIcon(LocaleController.getString("SharedMedia", NUM), paramViewHolder, NUM);
                  }
                }
                else
                {
                  i = ProfileActivity.this.totalMediaCount;
                  if (ProfileActivity.this.totalMediaCountMerge != -1) {}
                  for (paramInt = ProfileActivity.this.totalMediaCountMerge;; paramInt = 0)
                  {
                    paramViewHolder = String.format("%d", new Object[] { Integer.valueOf(paramInt + i) });
                    break;
                  }
                }
                ((TextCell)localObject1).setTextAndValue(LocaleController.getString("SharedMedia", NUM), paramViewHolder);
              }
              else
              {
                Object localObject2;
                if (paramInt == ProfileActivity.this.groupsInCommonRow)
                {
                  localObject2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                  paramViewHolder = LocaleController.getString("GroupsInCommon", NUM);
                  if (localObject2 != null) {}
                  for (paramInt = ((TLRPC.TL_userFull)localObject2).common_chats_count;; paramInt = 0)
                  {
                    ((TextCell)localObject1).setTextAndValue(paramViewHolder, String.format("%d", new Object[] { Integer.valueOf(paramInt) }));
                    break;
                  }
                }
                if (paramInt == ProfileActivity.this.settingsTimerRow)
                {
                  paramViewHolder = MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int)(ProfileActivity.this.dialog_id >> 32)));
                  if (paramViewHolder.ttl == 0) {}
                  for (paramViewHolder = LocaleController.getString("ShortMessageLifetimeForever", NUM);; paramViewHolder = LocaleController.formatTTLString(paramViewHolder.ttl))
                  {
                    ((TextCell)localObject1).setTextAndValue(LocaleController.getString("MessageLifetime", NUM), paramViewHolder);
                    break;
                  }
                }
                if (paramInt == ProfileActivity.this.settingsNotificationsRow)
                {
                  paramViewHolder = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount);
                  long l;
                  label974:
                  boolean bool1;
                  boolean bool2;
                  if (ProfileActivity.this.dialog_id != 0L)
                  {
                    l = ProfileActivity.this.dialog_id;
                    bool1 = paramViewHolder.getBoolean("custom_" + l, false);
                    bool2 = paramViewHolder.contains("notify2_" + l);
                    paramInt = paramViewHolder.getInt("notify2_" + l, 0);
                    i = paramViewHolder.getInt("notifyuntil_" + l, 0);
                    if ((paramInt != 3) || (i == Integer.MAX_VALUE)) {
                      break label1357;
                    }
                    paramInt = i - ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime();
                    if (paramInt > 0) {
                      break label1215;
                    }
                    if (!bool1) {
                      break label1202;
                    }
                    paramViewHolder = LocaleController.getString("NotificationsCustom", NUM);
                  }
                  for (;;)
                  {
                    if (paramViewHolder != null)
                    {
                      ((TextCell)localObject1).setTextAndValueAndIcon(LocaleController.getString("Notifications", NUM), paramViewHolder, NUM);
                      break;
                      if (ProfileActivity.this.user_id != 0)
                      {
                        l = ProfileActivity.this.user_id;
                        break label974;
                      }
                      l = -ProfileActivity.this.chat_id;
                      break label974;
                      label1202:
                      paramViewHolder = LocaleController.getString("NotificationsOn", NUM);
                      continue;
                      label1215:
                      if (paramInt < 3600)
                      {
                        paramViewHolder = LocaleController.formatString("WillUnmuteIn", NUM, new Object[] { LocaleController.formatPluralString("Minutes", paramInt / 60) });
                      }
                      else if (paramInt < 86400)
                      {
                        paramViewHolder = LocaleController.formatString("WillUnmuteIn", NUM, new Object[] { LocaleController.formatPluralString("Hours", (int)Math.ceil(paramInt / 60.0F / 60.0F)) });
                      }
                      else if (paramInt < 31536000)
                      {
                        paramViewHolder = LocaleController.formatString("WillUnmuteIn", NUM, new Object[] { LocaleController.formatPluralString("Days", (int)Math.ceil(paramInt / 60.0F / 60.0F / 24.0F)) });
                      }
                      else
                      {
                        paramViewHolder = null;
                        continue;
                        label1357:
                        if (paramInt == 0) {
                          if (bool2) {
                            bool2 = true;
                          }
                        }
                        for (;;)
                        {
                          if ((!bool2) || (!bool1)) {
                            break label1456;
                          }
                          paramViewHolder = LocaleController.getString("NotificationsCustom", NUM);
                          break;
                          if ((int)l < 0)
                          {
                            bool2 = paramViewHolder.getBoolean("EnableGroup", true);
                          }
                          else
                          {
                            bool2 = paramViewHolder.getBoolean("EnableAll", true);
                            continue;
                            if (paramInt == 1) {
                              bool2 = true;
                            } else if (paramInt == 2) {
                              bool2 = false;
                            } else {
                              bool2 = false;
                            }
                          }
                        }
                        label1456:
                        if (bool2) {}
                        for (paramViewHolder = LocaleController.getString("NotificationsOn", NUM);; paramViewHolder = LocaleController.getString("NotificationsOff", NUM)) {
                          break;
                        }
                      }
                    }
                  }
                  ((TextCell)localObject1).setTextAndValueAndIcon(LocaleController.getString("Notifications", NUM), LocaleController.getString("NotificationsOff", NUM), NUM);
                }
                else if (paramInt == ProfileActivity.this.startSecretChatRow)
                {
                  ((TextCell)localObject1).setText(LocaleController.getString("StartEncryptedChat", NUM));
                  ((TextCell)localObject1).setTag("windowBackgroundWhiteGreenText2");
                  ((TextCell)localObject1).setTextColor(Theme.getColor("windowBackgroundWhiteGreenText2"));
                }
                else if (paramInt == ProfileActivity.this.settingsKeyRow)
                {
                  paramViewHolder = new IdenticonDrawable();
                  paramViewHolder.setEncryptedChat(MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int)(ProfileActivity.this.dialog_id >> 32))));
                  ((TextCell)localObject1).setTextAndValueDrawable(LocaleController.getString("EncryptionKey", NUM), paramViewHolder);
                }
                else if (paramInt == ProfileActivity.this.leaveChannelRow)
                {
                  ((TextCell)localObject1).setTag("windowBackgroundWhiteRedText5");
                  ((TextCell)localObject1).setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
                  ((TextCell)localObject1).setText(LocaleController.getString("LeaveChannel", NUM));
                }
                else if (paramInt == ProfileActivity.this.convertRow)
                {
                  ((TextCell)localObject1).setText(LocaleController.getString("UpgradeGroup", NUM));
                  ((TextCell)localObject1).setTag("windowBackgroundWhiteGreenText2");
                  ((TextCell)localObject1).setTextColor(Theme.getColor("windowBackgroundWhiteGreenText2"));
                }
                else if (paramInt == ProfileActivity.this.addMemberRow)
                {
                  if (ProfileActivity.this.chat_id > 0) {
                    ((TextCell)localObject1).setText(LocaleController.getString("AddMember", NUM));
                  } else {
                    ((TextCell)localObject1).setText(LocaleController.getString("AddRecipient", NUM));
                  }
                }
                else if (paramInt == ProfileActivity.this.membersRow)
                {
                  if (ProfileActivity.this.info != null)
                  {
                    if ((ChatObject.isChannel(ProfileActivity.this.currentChat)) && (!ProfileActivity.this.currentChat.megagroup)) {
                      ((TextCell)localObject1).setTextAndValue(LocaleController.getString("ChannelSubscribers", NUM), String.format("%d", new Object[] { Integer.valueOf(ProfileActivity.this.info.participants_count) }));
                    } else {
                      ((TextCell)localObject1).setTextAndValue(LocaleController.getString("ChannelMembers", NUM), String.format("%d", new Object[] { Integer.valueOf(ProfileActivity.this.info.participants_count) }));
                    }
                  }
                  else if ((ChatObject.isChannel(ProfileActivity.this.currentChat)) && (!ProfileActivity.this.currentChat.megagroup))
                  {
                    ((TextCell)localObject1).setText(LocaleController.getString("ChannelSubscribers", NUM));
                  }
                  else
                  {
                    ((TextCell)localObject1).setText(LocaleController.getString("ChannelMembers", NUM));
                    continue;
                    localObject1 = (UserCell)paramViewHolder.itemView;
                    if (!ProfileActivity.this.sortedUsers.isEmpty())
                    {
                      paramViewHolder = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(((Integer)ProfileActivity.this.sortedUsers.get(paramInt - ProfileActivity.this.emptyRowChat2 - 1)).intValue());
                      label2024:
                      if (paramViewHolder == null) {
                        break label2138;
                      }
                      if (!(paramViewHolder instanceof TLRPC.TL_chatChannelParticipant)) {
                        break label2164;
                      }
                      localObject2 = ((TLRPC.TL_chatChannelParticipant)paramViewHolder).channelParticipant;
                      if (!(localObject2 instanceof TLRPC.TL_channelParticipantCreator)) {
                        break label2140;
                      }
                      ((UserCell)localObject1).setIsAdmin(1);
                      label2057:
                      paramViewHolder = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(paramViewHolder.user_id));
                      if (paramInt != ProfileActivity.this.emptyRowChat2 + 1) {
                        break label2215;
                      }
                    }
                    label2138:
                    label2140:
                    label2164:
                    label2215:
                    for (paramInt = NUM;; paramInt = 0)
                    {
                      ((UserCell)localObject1).setData(paramViewHolder, null, null, paramInt);
                      break;
                      paramViewHolder = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramInt - ProfileActivity.this.emptyRowChat2 - 1);
                      break label2024;
                      break;
                      if ((localObject2 instanceof TLRPC.TL_channelParticipantAdmin))
                      {
                        ((UserCell)localObject1).setIsAdmin(2);
                        break label2057;
                      }
                      ((UserCell)localObject1).setIsAdmin(0);
                      break label2057;
                      if ((paramViewHolder instanceof TLRPC.TL_chatParticipantCreator))
                      {
                        ((UserCell)localObject1).setIsAdmin(1);
                        break label2057;
                      }
                      if ((ProfileActivity.this.currentChat.admins_enabled) && ((paramViewHolder instanceof TLRPC.TL_chatParticipantAdmin)))
                      {
                        ((UserCell)localObject1).setIsAdmin(2);
                        break label2057;
                      }
                      ((UserCell)localObject1).setIsAdmin(0);
                      break label2057;
                    }
                    localObject1 = (AboutLinkCell)paramViewHolder.itemView;
                    if (paramInt == ProfileActivity.this.userInfoRow)
                    {
                      paramViewHolder = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                      if (paramViewHolder != null) {}
                      for (paramViewHolder = paramViewHolder.about;; paramViewHolder = null)
                      {
                        ((AboutLinkCell)localObject1).setTextAndIcon(paramViewHolder, NUM, ProfileActivity.this.isBot);
                        break;
                      }
                    }
                    if (paramInt == ProfileActivity.this.channelInfoRow)
                    {
                      for (paramViewHolder = ProfileActivity.this.info.about; paramViewHolder.contains("\n\n\n"); paramViewHolder = paramViewHolder.replace("\n\n\n", "\n\n")) {}
                      ((AboutLinkCell)localObject1).setTextAndIcon(paramViewHolder, NUM, true);
                    }
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
      paramViewGroup = null;
      switch (paramInt)
      {
      }
      for (;;)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new EmptyCell(this.mContext);
        continue;
        paramViewGroup = new DividerCell(this.mContext);
        paramViewGroup.setPadding(AndroidUtilities.dp(72.0F), 0, 0, 0);
        continue;
        paramViewGroup = new TextDetailCell(this.mContext);
        continue;
        paramViewGroup = new TextCell(this.mContext);
        continue;
        paramViewGroup = new UserCell(this.mContext, 61, 0, true);
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
        Object localObject1 = Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow");
        localObject1 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), (Drawable)localObject1);
        ((CombinedDrawable)localObject1).setFullsize(true);
        paramViewGroup.setBackgroundDrawable((Drawable)localObject1);
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        localObject1 = (TextInfoPrivacyCell)paramViewGroup;
        Object localObject2 = Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow");
        localObject2 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), (Drawable)localObject2);
        ((CombinedDrawable)localObject2).setFullsize(true);
        ((TextInfoPrivacyCell)localObject1).setBackgroundDrawable((Drawable)localObject2);
        ((TextInfoPrivacyCell)localObject1).setText(AndroidUtilities.replaceTags(LocaleController.formatString("ConvertGroupInfo", NUM, new Object[] { LocaleController.formatPluralString("Members", MessagesController.getInstance(ProfileActivity.this.currentAccount).maxMegagroupCount) })));
        continue;
        paramViewGroup = new LoadingCell(this.mContext);
        continue;
        paramViewGroup = new AboutLinkCell(this.mContext);
        ((AboutLinkCell)paramViewGroup).setDelegate(new AboutLinkCell.AboutLinkCellDelegate()
        {
          public void didPressUrl(String paramAnonymousString)
          {
            if (paramAnonymousString.startsWith("@")) {
              MessagesController.getInstance(ProfileActivity.this.currentAccount).openByUserName(paramAnonymousString.substring(1), ProfileActivity.this, 0);
            }
            for (;;)
            {
              return;
              Object localObject;
              if (paramAnonymousString.startsWith("#"))
              {
                localObject = new DialogsActivity(null);
                ((DialogsActivity)localObject).setSearchString(paramAnonymousString);
                ProfileActivity.this.presentFragment((BaseFragment)localObject);
              }
              else if ((paramAnonymousString.startsWith("/")) && (ProfileActivity.this.parentLayout.fragmentsStack.size() > 1))
              {
                localObject = (BaseFragment)ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                if ((localObject instanceof ChatActivity))
                {
                  ProfileActivity.this.finishFragment();
                  ((ChatActivity)localObject).chatActivityEnterView.setCommand(null, paramAnonymousString, false, false);
                }
              }
            }
          }
        });
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
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt2 = ActionBar.getCurrentActionBarHeight();
      if (ProfileActivity.this.actionBar.getOccupyStatusBar()) {}
      for (paramInt1 = AndroidUtilities.statusBarHeight;; paramInt1 = 0)
      {
        setMeasuredDimension(i, paramInt1 + paramInt2 + AndroidUtilities.dp(91.0F));
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