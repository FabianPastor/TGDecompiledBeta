package org.telegram.ui;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.Date;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ChannelRightsEditActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private int addAdminsRow;
  private int addUsersRow;
  private TLRPC.TL_channelAdminRights adminRights;
  private int banUsersRow;
  private TLRPC.TL_channelBannedRights bannedRights;
  private boolean canEdit;
  private int cantEditInfoRow;
  private int changeInfoRow;
  private int chatId;
  private int currentType;
  private TLRPC.User currentUser;
  private ChannelRightsEditActivityDelegate delegate;
  private int deleteMessagesRow;
  private int editMesagesRow;
  private int embedLinksRow;
  private boolean isDemocracy;
  private boolean isMegagroup;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private TLRPC.TL_channelAdminRights myAdminRights;
  private int pinMessagesRow;
  private int postMessagesRow;
  private int removeAdminRow;
  private int removeAdminShadowRow;
  private int rightsShadowRow;
  private int rowCount;
  private int sendMediaRow;
  private int sendMessagesRow;
  private int sendStickersRow;
  private int untilDateRow;
  private int viewMessagesRow;
  
  public ChannelRightsEditActivity(int paramInt1, int paramInt2, TLRPC.TL_channelAdminRights paramTL_channelAdminRights, TLRPC.TL_channelBannedRights paramTL_channelBannedRights, int paramInt3, boolean paramBoolean)
  {
    this.chatId = paramInt2;
    this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramInt1));
    this.currentType = paramInt3;
    this.canEdit = paramBoolean;
    TLRPC.Chat localChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
    if (localChat != null)
    {
      this.isMegagroup = localChat.megagroup;
      this.myAdminRights = localChat.admin_rights;
    }
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    Object localObject6;
    if (this.myAdminRights == null)
    {
      this.myAdminRights = new TLRPC.TL_channelAdminRights();
      localObject1 = this.myAdminRights;
      localObject2 = this.myAdminRights;
      localObject3 = this.myAdminRights;
      TLRPC.TL_channelAdminRights localTL_channelAdminRights1 = this.myAdminRights;
      localObject4 = this.myAdminRights;
      localObject5 = this.myAdminRights;
      localObject6 = this.myAdminRights;
      TLRPC.TL_channelAdminRights localTL_channelAdminRights2 = this.myAdminRights;
      this.myAdminRights.add_admins = true;
      localTL_channelAdminRights2.pin_messages = true;
      ((TLRPC.TL_channelAdminRights)localObject6).invite_link = true;
      ((TLRPC.TL_channelAdminRights)localObject5).invite_users = true;
      ((TLRPC.TL_channelAdminRights)localObject4).ban_users = true;
      localTL_channelAdminRights1.delete_messages = true;
      ((TLRPC.TL_channelAdminRights)localObject3).edit_messages = true;
      ((TLRPC.TL_channelAdminRights)localObject2).post_messages = true;
      ((TLRPC.TL_channelAdminRights)localObject1).change_info = true;
    }
    if (paramInt3 == 0)
    {
      this.adminRights = new TLRPC.TL_channelAdminRights();
      if (paramTL_channelAdminRights == null)
      {
        this.adminRights.change_info = this.myAdminRights.change_info;
        this.adminRights.post_messages = this.myAdminRights.post_messages;
        this.adminRights.edit_messages = this.myAdminRights.edit_messages;
        this.adminRights.delete_messages = this.myAdminRights.delete_messages;
        this.adminRights.ban_users = this.myAdminRights.ban_users;
        this.adminRights.invite_users = this.myAdminRights.invite_users;
        this.adminRights.invite_link = this.myAdminRights.invite_link;
        this.adminRights.pin_messages = this.myAdminRights.pin_messages;
        paramInt1 = 0;
        this.rowCount += 3;
        if (paramInt3 != 0) {
          break label1082;
        }
        if (!this.isMegagroup) {
          break label977;
        }
        paramInt2 = this.rowCount;
        this.rowCount = (paramInt2 + 1);
        this.changeInfoRow = paramInt2;
        paramInt2 = this.rowCount;
        this.rowCount = (paramInt2 + 1);
        this.deleteMessagesRow = paramInt2;
        paramInt2 = this.rowCount;
        this.rowCount = (paramInt2 + 1);
        this.banUsersRow = paramInt2;
        paramInt2 = this.rowCount;
        this.rowCount = (paramInt2 + 1);
        this.addUsersRow = paramInt2;
        paramInt2 = this.rowCount;
        this.rowCount = (paramInt2 + 1);
        this.pinMessagesRow = paramInt2;
        paramInt2 = this.rowCount;
        this.rowCount = (paramInt2 + 1);
        this.addAdminsRow = paramInt2;
        this.isDemocracy = localChat.democracy;
        label470:
        if ((!this.canEdit) || (paramInt1 == 0)) {
          break label1193;
        }
        paramInt1 = this.rowCount;
        this.rowCount = (paramInt1 + 1);
        this.rightsShadowRow = paramInt1;
        paramInt1 = this.rowCount;
        this.rowCount = (paramInt1 + 1);
        this.removeAdminRow = paramInt1;
        paramInt1 = this.rowCount;
        this.rowCount = (paramInt1 + 1);
        this.removeAdminShadowRow = paramInt1;
        this.cantEditInfoRow = -1;
      }
    }
    for (;;)
    {
      return;
      this.adminRights.change_info = paramTL_channelAdminRights.change_info;
      this.adminRights.post_messages = paramTL_channelAdminRights.post_messages;
      this.adminRights.edit_messages = paramTL_channelAdminRights.edit_messages;
      this.adminRights.delete_messages = paramTL_channelAdminRights.delete_messages;
      this.adminRights.ban_users = paramTL_channelAdminRights.ban_users;
      this.adminRights.invite_users = paramTL_channelAdminRights.invite_users;
      this.adminRights.invite_link = paramTL_channelAdminRights.invite_link;
      this.adminRights.pin_messages = paramTL_channelAdminRights.pin_messages;
      this.adminRights.add_admins = paramTL_channelAdminRights.add_admins;
      if ((this.adminRights.change_info) || (this.adminRights.post_messages) || (this.adminRights.edit_messages) || (this.adminRights.delete_messages) || (this.adminRights.ban_users) || (this.adminRights.invite_users) || (this.adminRights.invite_link) || (this.adminRights.pin_messages) || (this.adminRights.add_admins)) {}
      for (paramInt1 = 1;; paramInt1 = 0) {
        break;
      }
      this.bannedRights = new TLRPC.TL_channelBannedRights();
      if (paramTL_channelBannedRights == null)
      {
        paramTL_channelAdminRights = this.bannedRights;
        localObject5 = this.bannedRights;
        localObject6 = this.bannedRights;
        localObject2 = this.bannedRights;
        localObject1 = this.bannedRights;
        localObject3 = this.bannedRights;
        localObject4 = this.bannedRights;
        this.bannedRights.send_inline = true;
        ((TLRPC.TL_channelBannedRights)localObject4).send_games = true;
        ((TLRPC.TL_channelBannedRights)localObject3).send_gifs = true;
        ((TLRPC.TL_channelBannedRights)localObject1).send_stickers = true;
        ((TLRPC.TL_channelBannedRights)localObject2).embed_links = true;
        ((TLRPC.TL_channelBannedRights)localObject6).send_messages = true;
        ((TLRPC.TL_channelBannedRights)localObject5).send_media = true;
        paramTL_channelAdminRights.view_messages = true;
        label843:
        if ((paramTL_channelBannedRights != null) && (paramTL_channelBannedRights.view_messages)) {
          break label972;
        }
      }
      label972:
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        break;
        this.bannedRights.view_messages = paramTL_channelBannedRights.view_messages;
        this.bannedRights.send_messages = paramTL_channelBannedRights.send_messages;
        this.bannedRights.send_media = paramTL_channelBannedRights.send_media;
        this.bannedRights.send_stickers = paramTL_channelBannedRights.send_stickers;
        this.bannedRights.send_gifs = paramTL_channelBannedRights.send_gifs;
        this.bannedRights.send_games = paramTL_channelBannedRights.send_games;
        this.bannedRights.send_inline = paramTL_channelBannedRights.send_inline;
        this.bannedRights.embed_links = paramTL_channelBannedRights.embed_links;
        this.bannedRights.until_date = paramTL_channelBannedRights.until_date;
        break label843;
      }
      label977:
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.changeInfoRow = paramInt2;
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.postMessagesRow = paramInt2;
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.editMesagesRow = paramInt2;
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.deleteMessagesRow = paramInt2;
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.addUsersRow = paramInt2;
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.addAdminsRow = paramInt2;
      break label470;
      label1082:
      if (paramInt3 != 1) {
        break label470;
      }
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.viewMessagesRow = paramInt2;
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.sendMessagesRow = paramInt2;
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.sendMediaRow = paramInt2;
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.sendStickersRow = paramInt2;
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.embedLinksRow = paramInt2;
      paramInt2 = this.rowCount;
      this.rowCount = (paramInt2 + 1);
      this.untilDateRow = paramInt2;
      break label470;
      label1193:
      this.removeAdminRow = -1;
      this.removeAdminShadowRow = -1;
      if ((paramInt3 == 0) && (!this.canEdit))
      {
        this.rightsShadowRow = -1;
        paramInt1 = this.rowCount;
        this.rowCount = (paramInt1 + 1);
        this.cantEditInfoRow = paramInt1;
      }
      else
      {
        paramInt1 = this.rowCount;
        this.rowCount = (paramInt1 + 1);
        this.rightsShadowRow = paramInt1;
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    int i = 1;
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    FrameLayout localFrameLayout;
    if (this.currentType == 0)
    {
      this.actionBar.setTitle(LocaleController.getString("EditAdmin", NUM));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1)
          {
            ChannelRightsEditActivity.this.finishFragment();
            return;
          }
          Object localObject;
          if (paramAnonymousInt == 1)
          {
            if (ChannelRightsEditActivity.this.currentType != 0) {
              break label311;
            }
            if (!ChannelRightsEditActivity.this.isMegagroup) {
              break label279;
            }
            localObject = ChannelRightsEditActivity.this.adminRights;
            ChannelRightsEditActivity.this.adminRights.edit_messages = false;
            ((TLRPC.TL_channelAdminRights)localObject).post_messages = false;
            label62:
            MessagesController.getInstance(ChannelRightsEditActivity.this.currentAccount).setUserAdminRole(ChannelRightsEditActivity.this.chatId, ChannelRightsEditActivity.this.currentUser, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.isMegagroup, ChannelRightsEditActivity.this.getFragmentForAlert(1));
            if (ChannelRightsEditActivity.this.delegate != null)
            {
              localObject = ChannelRightsEditActivity.this.delegate;
              if ((!ChannelRightsEditActivity.this.adminRights.change_info) && (!ChannelRightsEditActivity.this.adminRights.post_messages) && (!ChannelRightsEditActivity.this.adminRights.edit_messages) && (!ChannelRightsEditActivity.this.adminRights.delete_messages) && (!ChannelRightsEditActivity.this.adminRights.ban_users) && (!ChannelRightsEditActivity.this.adminRights.invite_users) && (!ChannelRightsEditActivity.this.adminRights.invite_link) && (!ChannelRightsEditActivity.this.adminRights.pin_messages) && (!ChannelRightsEditActivity.this.adminRights.add_admins)) {
                break label306;
              }
              paramAnonymousInt = 1;
              label248:
              ((ChannelRightsEditActivity.ChannelRightsEditActivityDelegate)localObject).didSetRights(paramAnonymousInt, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.bannedRights);
            }
          }
          label279:
          label306:
          label311:
          label536:
          for (;;)
          {
            ChannelRightsEditActivity.this.finishFragment();
            break;
            break;
            localObject = ChannelRightsEditActivity.this.adminRights;
            ChannelRightsEditActivity.this.adminRights.ban_users = false;
            ((TLRPC.TL_channelAdminRights)localObject).pin_messages = false;
            break label62;
            paramAnonymousInt = 0;
            break label248;
            if (ChannelRightsEditActivity.this.currentType == 1)
            {
              MessagesController.getInstance(ChannelRightsEditActivity.this.currentAccount).setUserBannedRole(ChannelRightsEditActivity.this.chatId, ChannelRightsEditActivity.this.currentUser, ChannelRightsEditActivity.this.bannedRights, ChannelRightsEditActivity.this.isMegagroup, ChannelRightsEditActivity.this.getFragmentForAlert(1));
              if (ChannelRightsEditActivity.this.bannedRights.view_messages) {
                paramAnonymousInt = 0;
              }
              for (;;)
              {
                if (ChannelRightsEditActivity.this.delegate == null) {
                  break label536;
                }
                ChannelRightsEditActivity.this.delegate.didSetRights(paramAnonymousInt, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.bannedRights);
                break;
                if ((ChannelRightsEditActivity.this.bannedRights.send_messages) || (ChannelRightsEditActivity.this.bannedRights.send_stickers) || (ChannelRightsEditActivity.this.bannedRights.embed_links) || (ChannelRightsEditActivity.this.bannedRights.send_media) || (ChannelRightsEditActivity.this.bannedRights.send_gifs) || (ChannelRightsEditActivity.this.bannedRights.send_games) || (ChannelRightsEditActivity.this.bannedRights.send_inline))
                {
                  paramAnonymousInt = 1;
                }
                else
                {
                  ChannelRightsEditActivity.this.bannedRights.until_date = 0;
                  paramAnonymousInt = 2;
                }
              }
            }
          }
        }
      });
      if (this.canEdit) {
        this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
      }
      this.fragmentView = new FrameLayout(paramContext);
      this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      localFrameLayout = (FrameLayout)this.fragmentView;
      this.listView = new RecyclerListView(paramContext);
      Object localObject = new LinearLayoutManager(paramContext, 1, false)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.listView.setItemAnimator(null);
      this.listView.setLayoutAnimation(null);
      this.listView.setLayoutManager((RecyclerView.LayoutManager)localObject);
      localObject = this.listView;
      paramContext = new ListAdapter(paramContext);
      this.listViewAdapter = paramContext;
      ((RecyclerListView)localObject).setAdapter(paramContext);
      paramContext = this.listView;
      if (!LocaleController.isRTL) {
        break label266;
      }
    }
    for (;;)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (!ChannelRightsEditActivity.this.canEdit) {}
          for (;;)
          {
            return;
            if (paramAnonymousInt == 0)
            {
              paramAnonymousView = new Bundle();
              paramAnonymousView.putInt("user_id", ChannelRightsEditActivity.this.currentUser.id);
              ChannelRightsEditActivity.this.presentFragment(new ProfileActivity(paramAnonymousView));
            }
            else
            {
              if (paramAnonymousInt == ChannelRightsEditActivity.this.removeAdminRow)
              {
                if (ChannelRightsEditActivity.this.currentType == 0) {
                  MessagesController.getInstance(ChannelRightsEditActivity.this.currentAccount).setUserAdminRole(ChannelRightsEditActivity.this.chatId, ChannelRightsEditActivity.this.currentUser, new TLRPC.TL_channelAdminRights(), ChannelRightsEditActivity.this.isMegagroup, ChannelRightsEditActivity.this.getFragmentForAlert(0));
                }
                for (;;)
                {
                  if (ChannelRightsEditActivity.this.delegate != null) {
                    ChannelRightsEditActivity.this.delegate.didSetRights(0, ChannelRightsEditActivity.this.adminRights, ChannelRightsEditActivity.this.bannedRights);
                  }
                  ChannelRightsEditActivity.this.finishFragment();
                  break;
                  if (ChannelRightsEditActivity.this.currentType == 1)
                  {
                    ChannelRightsEditActivity.access$702(ChannelRightsEditActivity.this, new TLRPC.TL_channelBannedRights());
                    ChannelRightsEditActivity.this.bannedRights.view_messages = true;
                    ChannelRightsEditActivity.this.bannedRights.send_media = true;
                    ChannelRightsEditActivity.this.bannedRights.send_messages = true;
                    ChannelRightsEditActivity.this.bannedRights.send_stickers = true;
                    ChannelRightsEditActivity.this.bannedRights.send_gifs = true;
                    ChannelRightsEditActivity.this.bannedRights.send_games = true;
                    ChannelRightsEditActivity.this.bannedRights.send_inline = true;
                    ChannelRightsEditActivity.this.bannedRights.embed_links = true;
                    ChannelRightsEditActivity.this.bannedRights.until_date = 0;
                    MessagesController.getInstance(ChannelRightsEditActivity.this.currentAccount).setUserBannedRole(ChannelRightsEditActivity.this.chatId, ChannelRightsEditActivity.this.currentUser, ChannelRightsEditActivity.this.bannedRights, ChannelRightsEditActivity.this.isMegagroup, ChannelRightsEditActivity.this.getFragmentForAlert(0));
                  }
                }
              }
              int i;
              Object localObject1;
              Object localObject2;
              Object localObject3;
              if (paramAnonymousInt == ChannelRightsEditActivity.this.untilDateRow)
              {
                if (ChannelRightsEditActivity.this.getParentActivity() != null)
                {
                  paramAnonymousView = Calendar.getInstance();
                  paramAnonymousInt = paramAnonymousView.get(1);
                  i = paramAnonymousView.get(2);
                  int j = paramAnonymousView.get(5);
                  try
                  {
                    paramAnonymousView = new android/app/DatePickerDialog;
                    localObject1 = ChannelRightsEditActivity.this.getParentActivity();
                    localObject2 = new org/telegram/ui/ChannelRightsEditActivity$3$1;
                    ((1)localObject2).<init>(this);
                    paramAnonymousView.<init>((Context)localObject1, (DatePickerDialog.OnDateSetListener)localObject2, paramAnonymousInt, i, j);
                    localObject1 = paramAnonymousView.getDatePicker();
                    localObject2 = Calendar.getInstance();
                    ((Calendar)localObject2).setTimeInMillis(System.currentTimeMillis());
                    ((Calendar)localObject2).set(11, ((Calendar)localObject2).getMinimum(11));
                    ((Calendar)localObject2).set(12, ((Calendar)localObject2).getMinimum(12));
                    ((Calendar)localObject2).set(13, ((Calendar)localObject2).getMinimum(13));
                    ((Calendar)localObject2).set(14, ((Calendar)localObject2).getMinimum(14));
                    ((DatePicker)localObject1).setMinDate(((Calendar)localObject2).getTimeInMillis());
                    ((Calendar)localObject2).setTimeInMillis(System.currentTimeMillis() + 31536000000L);
                    ((Calendar)localObject2).set(11, ((Calendar)localObject2).getMaximum(11));
                    ((Calendar)localObject2).set(12, ((Calendar)localObject2).getMaximum(12));
                    ((Calendar)localObject2).set(13, ((Calendar)localObject2).getMaximum(13));
                    ((Calendar)localObject2).set(14, ((Calendar)localObject2).getMaximum(14));
                    ((DatePicker)localObject1).setMaxDate(((Calendar)localObject2).getTimeInMillis());
                    paramAnonymousView.setButton(-1, LocaleController.getString("Set", NUM), paramAnonymousView);
                    localObject2 = LocaleController.getString("UserRestrictionsUntilForever", NUM);
                    localObject3 = new org/telegram/ui/ChannelRightsEditActivity$3$2;
                    ((2)localObject3).<init>(this);
                    paramAnonymousView.setButton(-3, (CharSequence)localObject2, (DialogInterface.OnClickListener)localObject3);
                    localObject3 = LocaleController.getString("Cancel", NUM);
                    localObject2 = new org/telegram/ui/ChannelRightsEditActivity$3$3;
                    ((3)localObject2).<init>(this);
                    paramAnonymousView.setButton(-2, (CharSequence)localObject3, (DialogInterface.OnClickListener)localObject2);
                    if (Build.VERSION.SDK_INT >= 21)
                    {
                      localObject2 = new org/telegram/ui/ChannelRightsEditActivity$3$4;
                      ((4)localObject2).<init>(this, (DatePicker)localObject1);
                      paramAnonymousView.setOnShowListener((DialogInterface.OnShowListener)localObject2);
                    }
                    ChannelRightsEditActivity.this.showDialog(paramAnonymousView);
                  }
                  catch (Exception paramAnonymousView)
                  {
                    FileLog.e(paramAnonymousView);
                  }
                }
              }
              else if ((paramAnonymousView instanceof TextCheckCell2))
              {
                paramAnonymousView = (TextCheckCell2)paramAnonymousView;
                if (paramAnonymousView.isEnabled())
                {
                  if (!paramAnonymousView.isChecked())
                  {
                    bool = true;
                    label744:
                    paramAnonymousView.setChecked(bool);
                    if (paramAnonymousInt != ChannelRightsEditActivity.this.changeInfoRow) {
                      break label806;
                    }
                    paramAnonymousView = ChannelRightsEditActivity.this.adminRights;
                    if (ChannelRightsEditActivity.this.adminRights.change_info) {
                      break label800;
                    }
                  }
                  label800:
                  for (boolean bool = true;; bool = false)
                  {
                    paramAnonymousView.change_info = bool;
                    break;
                    bool = false;
                    break label744;
                  }
                  label806:
                  if (paramAnonymousInt == ChannelRightsEditActivity.this.postMessagesRow)
                  {
                    paramAnonymousView = ChannelRightsEditActivity.this.adminRights;
                    if (!ChannelRightsEditActivity.this.adminRights.post_messages) {}
                    for (bool = true;; bool = false)
                    {
                      paramAnonymousView.post_messages = bool;
                      break;
                    }
                  }
                  if (paramAnonymousInt == ChannelRightsEditActivity.this.editMesagesRow)
                  {
                    paramAnonymousView = ChannelRightsEditActivity.this.adminRights;
                    if (!ChannelRightsEditActivity.this.adminRights.edit_messages) {}
                    for (bool = true;; bool = false)
                    {
                      paramAnonymousView.edit_messages = bool;
                      break;
                    }
                  }
                  if (paramAnonymousInt == ChannelRightsEditActivity.this.deleteMessagesRow)
                  {
                    paramAnonymousView = ChannelRightsEditActivity.this.adminRights;
                    if (!ChannelRightsEditActivity.this.adminRights.delete_messages) {}
                    for (bool = true;; bool = false)
                    {
                      paramAnonymousView.delete_messages = bool;
                      break;
                    }
                  }
                  if (paramAnonymousInt == ChannelRightsEditActivity.this.addAdminsRow)
                  {
                    paramAnonymousView = ChannelRightsEditActivity.this.adminRights;
                    if (!ChannelRightsEditActivity.this.adminRights.add_admins) {}
                    for (bool = true;; bool = false)
                    {
                      paramAnonymousView.add_admins = bool;
                      break;
                    }
                  }
                  if (paramAnonymousInt == ChannelRightsEditActivity.this.banUsersRow)
                  {
                    paramAnonymousView = ChannelRightsEditActivity.this.adminRights;
                    if (!ChannelRightsEditActivity.this.adminRights.ban_users) {}
                    for (bool = true;; bool = false)
                    {
                      paramAnonymousView.ban_users = bool;
                      break;
                    }
                  }
                  if (paramAnonymousInt == ChannelRightsEditActivity.this.addUsersRow)
                  {
                    localObject1 = ChannelRightsEditActivity.this.adminRights;
                    paramAnonymousView = ChannelRightsEditActivity.this.adminRights;
                    if (!ChannelRightsEditActivity.this.adminRights.invite_users) {}
                    for (bool = true;; bool = false)
                    {
                      paramAnonymousView.invite_link = bool;
                      ((TLRPC.TL_channelAdminRights)localObject1).invite_users = bool;
                      break;
                    }
                  }
                  if (paramAnonymousInt == ChannelRightsEditActivity.this.pinMessagesRow)
                  {
                    paramAnonymousView = ChannelRightsEditActivity.this.adminRights;
                    if (!ChannelRightsEditActivity.this.adminRights.pin_messages) {}
                    for (bool = true;; bool = false)
                    {
                      paramAnonymousView.pin_messages = bool;
                      break;
                    }
                  }
                  if (ChannelRightsEditActivity.this.bannedRights != null)
                  {
                    if (!paramAnonymousView.isChecked())
                    {
                      i = 1;
                      label1191:
                      if (paramAnonymousInt != ChannelRightsEditActivity.this.viewMessagesRow) {
                        break label1638;
                      }
                      paramAnonymousView = ChannelRightsEditActivity.this.bannedRights;
                      if (ChannelRightsEditActivity.this.bannedRights.view_messages) {
                        break label1632;
                      }
                      bool = true;
                      label1226:
                      paramAnonymousView.view_messages = bool;
                    }
                    for (;;)
                    {
                      if (i != 0)
                      {
                        if ((ChannelRightsEditActivity.this.bannedRights.view_messages) && (!ChannelRightsEditActivity.this.bannedRights.send_messages))
                        {
                          ChannelRightsEditActivity.this.bannedRights.send_messages = true;
                          paramAnonymousView = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMessagesRow);
                          if (paramAnonymousView != null) {
                            ((TextCheckCell2)paramAnonymousView.itemView).setChecked(false);
                          }
                        }
                        if (((ChannelRightsEditActivity.this.bannedRights.view_messages) || (ChannelRightsEditActivity.this.bannedRights.send_messages)) && (!ChannelRightsEditActivity.this.bannedRights.send_media))
                        {
                          ChannelRightsEditActivity.this.bannedRights.send_media = true;
                          paramAnonymousView = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMediaRow);
                          if (paramAnonymousView != null) {
                            ((TextCheckCell2)paramAnonymousView.itemView).setChecked(false);
                          }
                        }
                        if (((ChannelRightsEditActivity.this.bannedRights.view_messages) || (ChannelRightsEditActivity.this.bannedRights.send_messages) || (ChannelRightsEditActivity.this.bannedRights.send_media)) && (!ChannelRightsEditActivity.this.bannedRights.send_stickers))
                        {
                          localObject2 = ChannelRightsEditActivity.this.bannedRights;
                          paramAnonymousView = ChannelRightsEditActivity.this.bannedRights;
                          localObject1 = ChannelRightsEditActivity.this.bannedRights;
                          ChannelRightsEditActivity.this.bannedRights.send_inline = true;
                          ((TLRPC.TL_channelBannedRights)localObject1).send_gifs = true;
                          paramAnonymousView.send_games = true;
                          ((TLRPC.TL_channelBannedRights)localObject2).send_stickers = true;
                          paramAnonymousView = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendStickersRow);
                          if (paramAnonymousView != null) {
                            ((TextCheckCell2)paramAnonymousView.itemView).setChecked(false);
                          }
                        }
                        if (((!ChannelRightsEditActivity.this.bannedRights.view_messages) && (!ChannelRightsEditActivity.this.bannedRights.send_messages) && (!ChannelRightsEditActivity.this.bannedRights.send_media)) || (ChannelRightsEditActivity.this.bannedRights.embed_links)) {
                          break;
                        }
                        ChannelRightsEditActivity.this.bannedRights.embed_links = true;
                        paramAnonymousView = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.embedLinksRow);
                        if (paramAnonymousView == null) {
                          break;
                        }
                        ((TextCheckCell2)paramAnonymousView.itemView).setChecked(false);
                        break;
                        i = 0;
                        break label1191;
                        label1632:
                        bool = false;
                        break label1226;
                        label1638:
                        if (paramAnonymousInt == ChannelRightsEditActivity.this.sendMessagesRow)
                        {
                          paramAnonymousView = ChannelRightsEditActivity.this.bannedRights;
                          if (!ChannelRightsEditActivity.this.bannedRights.send_messages) {}
                          for (bool = true;; bool = false)
                          {
                            paramAnonymousView.send_messages = bool;
                            break;
                          }
                        }
                        if (paramAnonymousInt == ChannelRightsEditActivity.this.sendMediaRow)
                        {
                          paramAnonymousView = ChannelRightsEditActivity.this.bannedRights;
                          if (!ChannelRightsEditActivity.this.bannedRights.send_media) {}
                          for (bool = true;; bool = false)
                          {
                            paramAnonymousView.send_media = bool;
                            break;
                          }
                        }
                        if (paramAnonymousInt == ChannelRightsEditActivity.this.sendStickersRow)
                        {
                          localObject3 = ChannelRightsEditActivity.this.bannedRights;
                          localObject2 = ChannelRightsEditActivity.this.bannedRights;
                          paramAnonymousView = ChannelRightsEditActivity.this.bannedRights;
                          localObject1 = ChannelRightsEditActivity.this.bannedRights;
                          if (!ChannelRightsEditActivity.this.bannedRights.send_stickers) {}
                          for (bool = true;; bool = false)
                          {
                            ((TLRPC.TL_channelBannedRights)localObject1).send_inline = bool;
                            paramAnonymousView.send_gifs = bool;
                            ((TLRPC.TL_channelBannedRights)localObject2).send_games = bool;
                            ((TLRPC.TL_channelBannedRights)localObject3).send_stickers = bool;
                            break;
                          }
                        }
                        if (paramAnonymousInt == ChannelRightsEditActivity.this.embedLinksRow)
                        {
                          paramAnonymousView = ChannelRightsEditActivity.this.bannedRights;
                          if (!ChannelRightsEditActivity.this.bannedRights.embed_links) {}
                          for (bool = true;; bool = false)
                          {
                            paramAnonymousView.embed_links = bool;
                            break;
                          }
                        }
                      }
                    }
                    if (((!ChannelRightsEditActivity.this.bannedRights.send_messages) || (!ChannelRightsEditActivity.this.bannedRights.embed_links) || (!ChannelRightsEditActivity.this.bannedRights.send_inline) || (!ChannelRightsEditActivity.this.bannedRights.send_media)) && (ChannelRightsEditActivity.this.bannedRights.view_messages))
                    {
                      ChannelRightsEditActivity.this.bannedRights.view_messages = false;
                      paramAnonymousView = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.viewMessagesRow);
                      if (paramAnonymousView != null) {
                        ((TextCheckCell2)paramAnonymousView.itemView).setChecked(true);
                      }
                    }
                    if (((!ChannelRightsEditActivity.this.bannedRights.embed_links) || (!ChannelRightsEditActivity.this.bannedRights.send_inline) || (!ChannelRightsEditActivity.this.bannedRights.send_media)) && (ChannelRightsEditActivity.this.bannedRights.send_messages))
                    {
                      ChannelRightsEditActivity.this.bannedRights.send_messages = false;
                      paramAnonymousView = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMessagesRow);
                      if (paramAnonymousView != null) {
                        ((TextCheckCell2)paramAnonymousView.itemView).setChecked(true);
                      }
                    }
                    if (((!ChannelRightsEditActivity.this.bannedRights.send_inline) || (!ChannelRightsEditActivity.this.bannedRights.embed_links)) && (ChannelRightsEditActivity.this.bannedRights.send_media))
                    {
                      ChannelRightsEditActivity.this.bannedRights.send_media = false;
                      paramAnonymousView = ChannelRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChannelRightsEditActivity.this.sendMediaRow);
                      if (paramAnonymousView != null) {
                        ((TextCheckCell2)paramAnonymousView.itemView).setChecked(true);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("UserRestrictions", NUM));
      break;
      label266:
      i = 2;
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local4 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        if (ChannelRightsEditActivity.this.listView != null)
        {
          int i = ChannelRightsEditActivity.this.listView.getChildCount();
          for (int j = 0; j < i; j++)
          {
            View localView = ChannelRightsEditActivity.this.listView.getChildAt(j);
            if ((localView instanceof UserCell)) {
              ((UserCell)localView).update(0);
            }
          }
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { UserCell.class, TextSettingsCell.class, TextCheckCell2.class, HeaderCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    ThemeDescription localThemeDescription9 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    localObject2 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText3");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueImageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell2.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell2.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell2.class }, new String[] { "checkBox" }, null, null, null, "switchThumb");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell2.class }, new String[] { "checkBox" }, null, null, null, "switchTrack");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell2.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell2.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    localObject1 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, local4, "windowBackgroundWhiteGrayText");
    ThemeDescription localThemeDescription24 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusOnlineColor" }, null, null, local4, "windowBackgroundWhiteBlueText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    Drawable localDrawable3 = Theme.avatar_savedDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localObject2, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, localThemeDescription21, localThemeDescription22, localThemeDescription23, localObject1, localThemeDescription24, new ThemeDescription(localRecyclerListView, 0, new Class[] { UserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2, localDrawable3 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundPink") };
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
  
  public void setDelegate(ChannelRightsEditActivityDelegate paramChannelRightsEditActivityDelegate)
  {
    this.delegate = paramChannelRightsEditActivityDelegate;
  }
  
  public static abstract interface ChannelRightsEditActivityDelegate
  {
    public abstract void didSetRights(int paramInt, TLRPC.TL_channelAdminRights paramTL_channelAdminRights, TLRPC.TL_channelBannedRights paramTL_channelBannedRights);
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
      return ChannelRightsEditActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 1;
      if (paramInt == 0) {
        i = 0;
      }
      for (;;)
      {
        return i;
        if ((paramInt == 1) || (paramInt == ChannelRightsEditActivity.this.rightsShadowRow) || (paramInt == ChannelRightsEditActivity.this.removeAdminShadowRow)) {
          i = 5;
        } else if (paramInt == 2) {
          i = 3;
        } else if ((paramInt == ChannelRightsEditActivity.this.changeInfoRow) || (paramInt == ChannelRightsEditActivity.this.postMessagesRow) || (paramInt == ChannelRightsEditActivity.this.editMesagesRow) || (paramInt == ChannelRightsEditActivity.this.deleteMessagesRow) || (paramInt == ChannelRightsEditActivity.this.addAdminsRow) || (paramInt == ChannelRightsEditActivity.this.banUsersRow) || (paramInt == ChannelRightsEditActivity.this.addUsersRow) || (paramInt == ChannelRightsEditActivity.this.pinMessagesRow) || (paramInt == ChannelRightsEditActivity.this.viewMessagesRow) || (paramInt == ChannelRightsEditActivity.this.sendMessagesRow) || (paramInt == ChannelRightsEditActivity.this.sendMediaRow) || (paramInt == ChannelRightsEditActivity.this.sendStickersRow) || (paramInt == ChannelRightsEditActivity.this.embedLinksRow)) {
          i = 4;
        } else if (paramInt != ChannelRightsEditActivity.this.cantEditInfoRow) {
          i = 2;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool1 = true;
      boolean bool2 = false;
      if (!ChannelRightsEditActivity.this.canEdit) {}
      int i;
      for (;;)
      {
        return bool2;
        i = paramViewHolder.getItemViewType();
        if ((ChannelRightsEditActivity.this.currentType != 0) || (i != 4)) {
          break;
        }
        int j = paramViewHolder.getAdapterPosition();
        if (j == ChannelRightsEditActivity.this.changeInfoRow)
        {
          bool2 = ChannelRightsEditActivity.this.myAdminRights.change_info;
        }
        else if (j == ChannelRightsEditActivity.this.postMessagesRow)
        {
          bool2 = ChannelRightsEditActivity.this.myAdminRights.post_messages;
        }
        else if (j == ChannelRightsEditActivity.this.editMesagesRow)
        {
          bool2 = ChannelRightsEditActivity.this.myAdminRights.edit_messages;
        }
        else if (j == ChannelRightsEditActivity.this.deleteMessagesRow)
        {
          bool2 = ChannelRightsEditActivity.this.myAdminRights.delete_messages;
        }
        else if (j == ChannelRightsEditActivity.this.addAdminsRow)
        {
          bool2 = ChannelRightsEditActivity.this.myAdminRights.add_admins;
        }
        else if (j == ChannelRightsEditActivity.this.banUsersRow)
        {
          bool2 = ChannelRightsEditActivity.this.myAdminRights.ban_users;
        }
        else if (j == ChannelRightsEditActivity.this.addUsersRow)
        {
          bool2 = ChannelRightsEditActivity.this.myAdminRights.invite_users;
        }
        else
        {
          if (j != ChannelRightsEditActivity.this.pinMessagesRow) {
            break;
          }
          bool2 = ChannelRightsEditActivity.this.myAdminRights.pin_messages;
        }
      }
      if ((i != 3) && (i != 1) && (i != 5)) {}
      for (bool2 = bool1;; bool2 = false) {
        break;
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
        ((UserCell)paramViewHolder.itemView).setData(ChannelRightsEditActivity.this.currentUser, null, null, 0);
        continue;
        paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
        if (paramInt == ChannelRightsEditActivity.this.cantEditInfoRow)
        {
          paramViewHolder.setText(LocaleController.getString("EditAdminCantEdit", NUM));
          continue;
          Object localObject = (TextSettingsCell)paramViewHolder.itemView;
          if (paramInt == ChannelRightsEditActivity.this.removeAdminRow)
          {
            ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
            ((TextSettingsCell)localObject).setTag("windowBackgroundWhiteRedText3");
            if (ChannelRightsEditActivity.this.currentType == 0) {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("EditAdminRemoveAdmin", NUM), false);
            } else if (ChannelRightsEditActivity.this.currentType == 1) {
              ((TextSettingsCell)localObject).setText(LocaleController.getString("UserRestrictionsBlock", NUM), false);
            }
          }
          else if (paramInt == ChannelRightsEditActivity.this.untilDateRow)
          {
            ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            ((TextSettingsCell)localObject).setTag("windowBackgroundWhiteBlackText");
            if ((ChannelRightsEditActivity.this.bannedRights.until_date == 0) || (Math.abs(ChannelRightsEditActivity.this.bannedRights.until_date - System.currentTimeMillis() / 1000L) > 315360000L)) {}
            for (paramViewHolder = LocaleController.getString("UserRestrictionsUntilForever", NUM);; paramViewHolder = LocaleController.formatDateForBan(ChannelRightsEditActivity.this.bannedRights.until_date))
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("UserRestrictionsUntil", NUM), paramViewHolder, false);
              break;
            }
            paramViewHolder = (HeaderCell)paramViewHolder.itemView;
            if (ChannelRightsEditActivity.this.currentType == 0)
            {
              paramViewHolder.setText(LocaleController.getString("EditAdminWhatCanDo", NUM));
            }
            else if (ChannelRightsEditActivity.this.currentType == 1)
            {
              paramViewHolder.setText(LocaleController.getString("UserRestrictionsCanDo", NUM));
              continue;
              paramViewHolder = (TextCheckCell2)paramViewHolder.itemView;
              if (paramInt == ChannelRightsEditActivity.this.changeInfoRow) {
                if (ChannelRightsEditActivity.this.isMegagroup)
                {
                  paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", NUM), ChannelRightsEditActivity.this.adminRights.change_info, true);
                  label404:
                  if ((paramInt != ChannelRightsEditActivity.this.sendMediaRow) && (paramInt != ChannelRightsEditActivity.this.sendStickersRow) && (paramInt != ChannelRightsEditActivity.this.embedLinksRow)) {
                    break label1118;
                  }
                  if ((ChannelRightsEditActivity.this.bannedRights.send_messages) || (ChannelRightsEditActivity.this.bannedRights.view_messages)) {
                    break label1112;
                  }
                }
              }
              label1112:
              for (boolean bool = true;; bool = false)
              {
                paramViewHolder.setEnabled(bool);
                break;
                paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", NUM), ChannelRightsEditActivity.this.adminRights.change_info, true);
                break label404;
                if (paramInt == ChannelRightsEditActivity.this.postMessagesRow)
                {
                  paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminPostMessages", NUM), ChannelRightsEditActivity.this.adminRights.post_messages, true);
                  break label404;
                }
                if (paramInt == ChannelRightsEditActivity.this.editMesagesRow)
                {
                  paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminEditMessages", NUM), ChannelRightsEditActivity.this.adminRights.edit_messages, true);
                  break label404;
                }
                if (paramInt == ChannelRightsEditActivity.this.deleteMessagesRow)
                {
                  if (ChannelRightsEditActivity.this.isMegagroup)
                  {
                    paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminGroupDeleteMessages", NUM), ChannelRightsEditActivity.this.adminRights.delete_messages, true);
                    break label404;
                  }
                  paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminDeleteMessages", NUM), ChannelRightsEditActivity.this.adminRights.delete_messages, true);
                  break label404;
                }
                if (paramInt == ChannelRightsEditActivity.this.addAdminsRow)
                {
                  paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", NUM), ChannelRightsEditActivity.this.adminRights.add_admins, false);
                  break label404;
                }
                if (paramInt == ChannelRightsEditActivity.this.banUsersRow)
                {
                  paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", NUM), ChannelRightsEditActivity.this.adminRights.ban_users, true);
                  break label404;
                }
                if (paramInt == ChannelRightsEditActivity.this.addUsersRow)
                {
                  if (!ChannelRightsEditActivity.this.isDemocracy)
                  {
                    paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminAddUsers", NUM), ChannelRightsEditActivity.this.adminRights.invite_users, true);
                    break label404;
                  }
                  paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", NUM), ChannelRightsEditActivity.this.adminRights.invite_users, true);
                  break label404;
                }
                if (paramInt == ChannelRightsEditActivity.this.pinMessagesRow)
                {
                  paramViewHolder.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", NUM), ChannelRightsEditActivity.this.adminRights.pin_messages, true);
                  break label404;
                }
                if (paramInt == ChannelRightsEditActivity.this.viewMessagesRow)
                {
                  localObject = LocaleController.getString("UserRestrictionsRead", NUM);
                  if (!ChannelRightsEditActivity.this.bannedRights.view_messages) {}
                  for (bool = true;; bool = false)
                  {
                    paramViewHolder.setTextAndCheck((String)localObject, bool, true);
                    break;
                  }
                }
                if (paramInt == ChannelRightsEditActivity.this.sendMessagesRow)
                {
                  localObject = LocaleController.getString("UserRestrictionsSend", NUM);
                  if (!ChannelRightsEditActivity.this.bannedRights.send_messages) {}
                  for (bool = true;; bool = false)
                  {
                    paramViewHolder.setTextAndCheck((String)localObject, bool, true);
                    break;
                  }
                }
                if (paramInt == ChannelRightsEditActivity.this.sendMediaRow)
                {
                  localObject = LocaleController.getString("UserRestrictionsSendMedia", NUM);
                  if (!ChannelRightsEditActivity.this.bannedRights.send_media) {}
                  for (bool = true;; bool = false)
                  {
                    paramViewHolder.setTextAndCheck((String)localObject, bool, true);
                    break;
                  }
                }
                if (paramInt == ChannelRightsEditActivity.this.sendStickersRow)
                {
                  localObject = LocaleController.getString("UserRestrictionsSendStickers", NUM);
                  if (!ChannelRightsEditActivity.this.bannedRights.send_stickers) {}
                  for (bool = true;; bool = false)
                  {
                    paramViewHolder.setTextAndCheck((String)localObject, bool, true);
                    break;
                  }
                }
                if (paramInt != ChannelRightsEditActivity.this.embedLinksRow) {
                  break label404;
                }
                localObject = LocaleController.getString("UserRestrictionsEmbedLinks", NUM);
                if (!ChannelRightsEditActivity.this.bannedRights.embed_links) {}
                for (bool = true;; bool = false)
                {
                  paramViewHolder.setTextAndCheck((String)localObject, bool, true);
                  break;
                }
              }
              label1118:
              if (paramInt == ChannelRightsEditActivity.this.sendMessagesRow)
              {
                if (!ChannelRightsEditActivity.this.bannedRights.view_messages) {}
                for (bool = true;; bool = false)
                {
                  paramViewHolder.setEnabled(bool);
                  break;
                }
                paramViewHolder = (ShadowSectionCell)paramViewHolder.itemView;
                if (paramInt == ChannelRightsEditActivity.this.rightsShadowRow)
                {
                  localObject = this.mContext;
                  if (ChannelRightsEditActivity.this.removeAdminRow == -1) {}
                  for (paramInt = NUM;; paramInt = NUM)
                  {
                    paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable((Context)localObject, paramInt, "windowBackgroundGrayShadow"));
                    break;
                  }
                }
                if (paramInt == ChannelRightsEditActivity.this.removeAdminShadowRow) {
                  paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else {
                  paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
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
        paramViewGroup = new ShadowSectionCell(this.mContext);
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new UserCell(this.mContext, 1, 0, false);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextCheckCell2(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChannelRightsEditActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */