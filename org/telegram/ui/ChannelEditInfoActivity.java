package org.telegram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_chatPhoto;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_chats;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ChannelEditInfoActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, AvatarUpdater.AvatarUpdaterDelegate
{
  private static final int done_button = 1;
  private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList();
  private ShadowSectionCell adminedInfoCell;
  private LinearLayout adminnedChannelsLayout;
  private TLRPC.FileLocation avatar;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater = new AvatarUpdater();
  private boolean canCreatePublic = true;
  private int chatId;
  private int checkReqId;
  private Runnable checkRunnable;
  private TextView checkTextView;
  private FrameLayout container1;
  private FrameLayout container2;
  private FrameLayout container3;
  private FrameLayout container4;
  private boolean createAfterUpload;
  private TLRPC.Chat currentChat;
  private EditTextBoldCursor descriptionTextView;
  private View doneButton;
  private boolean donePressed;
  private EditText editText;
  private HeaderCell headerCell;
  private HeaderCell headerCell2;
  private boolean historyHidden;
  private TLRPC.ChatFull info;
  private TextInfoPrivacyCell infoCell;
  private TextInfoPrivacyCell infoCell2;
  private TextInfoPrivacyCell infoCell3;
  private TLRPC.ExportedChatInvite invite;
  private boolean isPrivate;
  private String lastCheckName;
  private boolean lastNameAvailable;
  private View lineView;
  private View lineView2;
  private View lineView3;
  private LinearLayout linearLayout;
  private LinearLayout linearLayout2;
  private LinearLayout linearLayout3;
  private LinearLayout linearLayoutInviteContainer;
  private LinearLayout linearLayoutTypeContainer;
  private LinearLayout linkContainer;
  private LoadingCell loadingAdminedCell;
  private boolean loadingAdminedChannels;
  private boolean loadingInvite;
  private EditTextBoldCursor nameTextView;
  private TextBlockCell privateContainer;
  private AlertDialog progressDialog;
  private LinearLayout publicContainer;
  private RadioButtonCell radioButtonCell1;
  private RadioButtonCell radioButtonCell2;
  private RadioButtonCell radioButtonCell3;
  private RadioButtonCell radioButtonCell4;
  private ShadowSectionCell sectionCell;
  private ShadowSectionCell sectionCell2;
  private ShadowSectionCell sectionCell3;
  private boolean signMessages;
  private TextSettingsCell textCell;
  private TextSettingsCell textCell2;
  private TextCheckCell textCheckCell;
  private TextInfoPrivacyCell typeInfoCell;
  private TLRPC.InputFile uploadedAvatar;
  private EditTextBoldCursor usernameTextView;
  
  public ChannelEditInfoActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatId = paramBundle.getInt("chat_id", 0);
  }
  
  private boolean checkUserName(final String paramString)
  {
    boolean bool = false;
    if ((paramString != null) && (paramString.length() > 0))
    {
      this.checkTextView.setVisibility(0);
      if (this.checkRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        this.checkRunnable = null;
        this.lastCheckName = null;
        if (this.checkReqId != 0) {
          ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
        }
      }
      this.lastNameAvailable = false;
      if (paramString == null) {
        break label375;
      }
      if ((!paramString.startsWith("_")) && (!paramString.endsWith("_"))) {
        break label149;
      }
      this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
      this.checkTextView.setTag("windowBackgroundWhiteRedText4");
      this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
    }
    for (;;)
    {
      return bool;
      this.checkTextView.setVisibility(8);
      break;
      label149:
      for (int i = 0;; i++)
      {
        if (i >= paramString.length()) {
          break label375;
        }
        int j = paramString.charAt(i);
        if ((i == 0) && (j >= 48) && (j <= 57))
        {
          if (this.currentChat.megagroup)
          {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", NUM));
            this.checkTextView.setTag("windowBackgroundWhiteRedText4");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            break;
          }
          this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", NUM));
          this.checkTextView.setTag("windowBackgroundWhiteRedText4");
          this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
          break;
        }
        if (((j < 48) || (j > 57)) && ((j < 97) || (j > 122)) && ((j < 65) || (j > 90)) && (j != 95))
        {
          this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
          this.checkTextView.setTag("windowBackgroundWhiteRedText4");
          this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
          break;
        }
      }
      label375:
      if ((paramString == null) || (paramString.length() < 5))
      {
        if (this.currentChat.megagroup)
        {
          this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", NUM));
          this.checkTextView.setTag("windowBackgroundWhiteRedText4");
          this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
        }
        else
        {
          this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", NUM));
          this.checkTextView.setTag("windowBackgroundWhiteRedText4");
          this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
        }
      }
      else if (paramString.length() > 32)
      {
        this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", NUM));
        this.checkTextView.setTag("windowBackgroundWhiteRedText4");
        this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      }
      else
      {
        this.checkTextView.setText(LocaleController.getString("LinkChecking", NUM));
        this.checkTextView.setTag("windowBackgroundWhiteGrayText8");
        this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
        this.lastCheckName = paramString;
        this.checkRunnable = new Runnable()
        {
          public void run()
          {
            TLRPC.TL_channels_checkUsername localTL_channels_checkUsername = new TLRPC.TL_channels_checkUsername();
            localTL_channels_checkUsername.username = paramString;
            localTL_channels_checkUsername.channel = MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).getInputChannel(ChannelEditInfoActivity.this.chatId);
            ChannelEditInfoActivity.access$4902(ChannelEditInfoActivity.this, ConnectionsManager.getInstance(ChannelEditInfoActivity.this.currentAccount).sendRequest(localTL_channels_checkUsername, new RequestDelegate()
            {
              public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    ChannelEditInfoActivity.access$4902(ChannelEditInfoActivity.this, 0);
                    if ((ChannelEditInfoActivity.this.lastCheckName != null) && (ChannelEditInfoActivity.this.lastCheckName.equals(ChannelEditInfoActivity.19.this.val$name)))
                    {
                      if ((paramAnonymous2TL_error == null) && ((paramAnonymous2TLObject instanceof TLRPC.TL_boolTrue)))
                      {
                        ChannelEditInfoActivity.this.checkTextView.setText(LocaleController.formatString("LinkAvailable", NUM, new Object[] { ChannelEditInfoActivity.19.this.val$name }));
                        ChannelEditInfoActivity.this.checkTextView.setTag("windowBackgroundWhiteGreenText");
                        ChannelEditInfoActivity.this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
                        ChannelEditInfoActivity.access$902(ChannelEditInfoActivity.this, true);
                      }
                    }
                    else {
                      return;
                    }
                    if ((paramAnonymous2TL_error != null) && (paramAnonymous2TL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")))
                    {
                      ChannelEditInfoActivity.access$302(ChannelEditInfoActivity.this, false);
                      ChannelEditInfoActivity.this.loadAdminedChannels();
                    }
                    for (;;)
                    {
                      ChannelEditInfoActivity.this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                      ChannelEditInfoActivity.this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                      ChannelEditInfoActivity.access$902(ChannelEditInfoActivity.this, false);
                      break;
                      ChannelEditInfoActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", NUM));
                    }
                  }
                });
              }
            }, 2));
          }
        };
        AndroidUtilities.runOnUIThread(this.checkRunnable, 300L);
        bool = true;
      }
    }
  }
  
  private void generateLink()
  {
    if ((this.loadingInvite) || (this.invite != null)) {}
    for (;;)
    {
      return;
      this.loadingInvite = true;
      TLRPC.TL_channels_exportInvite localTL_channels_exportInvite = new TLRPC.TL_channels_exportInvite();
      localTL_channels_exportInvite.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_exportInvite, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (paramAnonymousTL_error == null) {
                ChannelEditInfoActivity.access$3302(ChannelEditInfoActivity.this, (TLRPC.ExportedChatInvite)paramAnonymousTLObject);
              }
              ChannelEditInfoActivity.access$5202(ChannelEditInfoActivity.this, false);
              TextBlockCell localTextBlockCell;
              if (ChannelEditInfoActivity.this.privateContainer != null)
              {
                localTextBlockCell = ChannelEditInfoActivity.this.privateContainer;
                if (ChannelEditInfoActivity.this.invite == null) {
                  break label95;
                }
              }
              label95:
              for (String str = ChannelEditInfoActivity.this.invite.link;; str = LocaleController.getString("Loading", NUM))
              {
                localTextBlockCell.setText(str, false);
                return;
              }
            }
          });
        }
      });
    }
  }
  
  private void loadAdminedChannels()
  {
    if ((this.loadingAdminedChannels) || (this.adminnedChannelsLayout == null)) {}
    for (;;)
    {
      return;
      this.loadingAdminedChannels = true;
      updatePrivatePublic();
      TLRPC.TL_channels_getAdminedPublicChannels localTL_channels_getAdminedPublicChannels = new TLRPC.TL_channels_getAdminedPublicChannels();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_getAdminedPublicChannels, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              ChannelEditInfoActivity.access$4102(ChannelEditInfoActivity.this, false);
              if ((paramAnonymousTLObject == null) || (ChannelEditInfoActivity.this.getParentActivity() == null)) {}
              for (;;)
              {
                return;
                for (int i = 0; i < ChannelEditInfoActivity.this.adminedChannelCells.size(); i++) {
                  ChannelEditInfoActivity.this.linearLayout.removeView((View)ChannelEditInfoActivity.this.adminedChannelCells.get(i));
                }
                ChannelEditInfoActivity.this.adminedChannelCells.clear();
                TLRPC.TL_messages_chats localTL_messages_chats = (TLRPC.TL_messages_chats)paramAnonymousTLObject;
                i = 0;
                if (i < localTL_messages_chats.chats.size())
                {
                  AdminedChannelCell localAdminedChannelCell = new AdminedChannelCell(ChannelEditInfoActivity.this.getParentActivity(), new View.OnClickListener()
                  {
                    public void onClick(final View paramAnonymous3View)
                    {
                      paramAnonymous3View = ((AdminedChannelCell)paramAnonymous3View.getParent()).getCurrentChannel();
                      AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChannelEditInfoActivity.this.getParentActivity());
                      localBuilder.setTitle(LocaleController.getString("AppName", NUM));
                      if (paramAnonymous3View.megagroup) {
                        localBuilder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", NUM, new Object[] { MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).linkPrefix + "/" + paramAnonymous3View.username, paramAnonymous3View.title })));
                      }
                      for (;;)
                      {
                        localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        localBuilder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new DialogInterface.OnClickListener()
                        {
                          public void onClick(DialogInterface paramAnonymous4DialogInterface, int paramAnonymous4Int)
                          {
                            paramAnonymous4DialogInterface = new TLRPC.TL_channels_updateUsername();
                            paramAnonymous4DialogInterface.channel = MessagesController.getInputChannel(paramAnonymous3View);
                            paramAnonymous4DialogInterface.username = "";
                            ConnectionsManager.getInstance(ChannelEditInfoActivity.this.currentAccount).sendRequest(paramAnonymous4DialogInterface, new RequestDelegate()
                            {
                              public void run(TLObject paramAnonymous5TLObject, TLRPC.TL_error paramAnonymous5TL_error)
                              {
                                if ((paramAnonymous5TLObject instanceof TLRPC.TL_boolTrue)) {
                                  AndroidUtilities.runOnUIThread(new Runnable()
                                  {
                                    public void run()
                                    {
                                      ChannelEditInfoActivity.access$302(ChannelEditInfoActivity.this, true);
                                      if (ChannelEditInfoActivity.this.nameTextView.length() > 0) {
                                        ChannelEditInfoActivity.this.checkUserName(ChannelEditInfoActivity.this.nameTextView.getText().toString());
                                      }
                                      ChannelEditInfoActivity.this.updatePrivatePublic();
                                    }
                                  });
                                }
                              }
                            }, 64);
                          }
                        });
                        ChannelEditInfoActivity.this.showDialog(localBuilder.create());
                        return;
                        localBuilder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", NUM, new Object[] { MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).linkPrefix + "/" + paramAnonymous3View.username, paramAnonymous3View.title })));
                      }
                    }
                  });
                  TLRPC.Chat localChat = (TLRPC.Chat)localTL_messages_chats.chats.get(i);
                  if (i == localTL_messages_chats.chats.size() - 1) {}
                  for (boolean bool = true;; bool = false)
                  {
                    localAdminedChannelCell.setChannel(localChat, bool);
                    ChannelEditInfoActivity.this.adminedChannelCells.add(localAdminedChannelCell);
                    ChannelEditInfoActivity.this.adminnedChannelsLayout.addView(localAdminedChannelCell, LayoutHelper.createLinear(-1, 72));
                    i++;
                    break;
                  }
                }
                ChannelEditInfoActivity.this.updatePrivatePublic();
              }
            }
          });
        }
      });
    }
  }
  
  private void updatePrivatePublic()
  {
    int i = 8;
    boolean bool = false;
    if (this.sectionCell2 == null) {
      return;
    }
    if ((!this.isPrivate) && (!this.canCreatePublic))
    {
      this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", NUM));
      this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
      this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      this.linkContainer.setVisibility(8);
      this.sectionCell2.setVisibility(8);
      this.adminedInfoCell.setVisibility(0);
      if (this.loadingAdminedChannels)
      {
        this.loadingAdminedCell.setVisibility(0);
        this.adminnedChannelsLayout.setVisibility(8);
        this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
        this.adminedInfoCell.setBackgroundDrawable(null);
        label147:
        if (this.headerCell2 != null)
        {
          this.headerCell2.setVisibility(8);
          this.linearLayoutInviteContainer.setVisibility(8);
          this.sectionCell3.setVisibility(8);
        }
      }
    }
    Object localObject2;
    label418:
    label447:
    label469:
    label490:
    label511:
    label541:
    label573:
    do
    {
      localObject1 = this.radioButtonCell1;
      if (!this.isPrivate) {
        bool = true;
      }
      ((RadioButtonCell)localObject1).setChecked(bool, true);
      this.radioButtonCell2.setChecked(this.isPrivate, true);
      this.usernameTextView.clearFocus();
      AndroidUtilities.hideKeyboard(this.nameTextView);
      break;
      this.adminedInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.adminedInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
      this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
      this.loadingAdminedCell.setVisibility(8);
      this.adminnedChannelsLayout.setVisibility(0);
      break label147;
      this.typeInfoCell.setTag("windowBackgroundWhiteGrayText4");
      this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
      this.sectionCell2.setVisibility(0);
      this.adminedInfoCell.setVisibility(8);
      this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
      this.adminnedChannelsLayout.setVisibility(8);
      this.linkContainer.setVisibility(0);
      this.loadingAdminedCell.setVisibility(8);
      if (!this.currentChat.megagroup) {
        break label681;
      }
      localObject2 = this.typeInfoCell;
      if (!this.isPrivate) {
        break label655;
      }
      localObject1 = LocaleController.getString("MegaPrivateLinkHelp", NUM);
      ((TextInfoPrivacyCell)localObject2).setText((CharSequence)localObject1);
      localObject2 = this.headerCell;
      if (!this.isPrivate) {
        break label668;
      }
      localObject1 = LocaleController.getString("ChannelInviteLinkTitle", NUM);
      ((HeaderCell)localObject2).setText((String)localObject1);
      localObject1 = this.publicContainer;
      if (!this.isPrivate) {
        break label768;
      }
      j = 8;
      ((LinearLayout)localObject1).setVisibility(j);
      localObject1 = this.privateContainer;
      if (!this.isPrivate) {
        break label774;
      }
      j = 0;
      ((TextBlockCell)localObject1).setVisibility(j);
      localObject1 = this.linkContainer;
      if (!this.isPrivate) {
        break label781;
      }
      j = 0;
      ((LinearLayout)localObject1).setPadding(0, 0, 0, j);
      localObject2 = this.privateContainer;
      if (this.invite == null) {
        break label792;
      }
      localObject1 = this.invite.link;
      ((TextBlockCell)localObject2).setText((String)localObject1, false);
      localObject1 = this.checkTextView;
      if ((this.isPrivate) || (this.checkTextView.length() == 0)) {
        break label805;
      }
      j = 0;
      ((TextView)localObject1).setVisibility(j);
    } while (this.headerCell2 == null);
    Object localObject1 = this.headerCell2;
    if (this.isPrivate)
    {
      j = 0;
      label601:
      ((HeaderCell)localObject1).setVisibility(j);
      localObject1 = this.linearLayoutInviteContainer;
      if (!this.isPrivate) {
        break label819;
      }
    }
    label655:
    label668:
    label681:
    label704:
    label755:
    label768:
    label774:
    label781:
    label792:
    label805:
    label819:
    for (int j = 0;; j = 8)
    {
      ((LinearLayout)localObject1).setVisibility(j);
      localObject1 = this.sectionCell3;
      j = i;
      if (this.isPrivate) {
        j = 0;
      }
      ((ShadowSectionCell)localObject1).setVisibility(j);
      break;
      localObject1 = LocaleController.getString("MegaUsernameHelp", NUM);
      break label418;
      localObject1 = LocaleController.getString("ChannelLinkTitle", NUM);
      break label447;
      localObject2 = this.typeInfoCell;
      if (this.isPrivate)
      {
        localObject1 = LocaleController.getString("ChannelPrivateLinkHelp", NUM);
        ((TextInfoPrivacyCell)localObject2).setText((CharSequence)localObject1);
        localObject2 = this.headerCell;
        if (!this.isPrivate) {
          break label755;
        }
      }
      for (localObject1 = LocaleController.getString("ChannelInviteLinkTitle", NUM);; localObject1 = LocaleController.getString("ChannelLinkTitle", NUM))
      {
        ((HeaderCell)localObject2).setText((String)localObject1);
        break;
        localObject1 = LocaleController.getString("ChannelUsernameHelp", NUM);
        break label704;
      }
      j = 0;
      break label469;
      j = 8;
      break label490;
      j = AndroidUtilities.dp(7.0F);
      break label511;
      localObject1 = LocaleController.getString("Loading", NUM);
      break label541;
      j = 8;
      break label573;
      j = 8;
      break label601;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ChannelEditInfoActivity.this.finishFragment();
        }
        Object localObject;
        for (;;)
        {
          return;
          if ((paramAnonymousInt != 1) || (ChannelEditInfoActivity.this.donePressed)) {
            break label799;
          }
          if (ChannelEditInfoActivity.this.nameTextView.length() == 0)
          {
            localObject = (Vibrator)ChannelEditInfoActivity.this.getParentActivity().getSystemService("vibrator");
            if (localObject != null) {
              ((Vibrator)localObject).vibrate(200L);
            }
            AndroidUtilities.shakeView(ChannelEditInfoActivity.this.nameTextView, 2.0F, 0);
          }
          else if ((ChannelEditInfoActivity.this.usernameTextView != null) && (!ChannelEditInfoActivity.this.isPrivate) && (((ChannelEditInfoActivity.this.currentChat.username == null) && (ChannelEditInfoActivity.this.usernameTextView.length() != 0)) || ((ChannelEditInfoActivity.this.currentChat.username != null) && (!ChannelEditInfoActivity.this.currentChat.username.equalsIgnoreCase(ChannelEditInfoActivity.this.usernameTextView.getText().toString())) && (ChannelEditInfoActivity.this.nameTextView.length() != 0) && (!ChannelEditInfoActivity.this.lastNameAvailable))))
          {
            localObject = (Vibrator)ChannelEditInfoActivity.this.getParentActivity().getSystemService("vibrator");
            if (localObject != null) {
              ((Vibrator)localObject).vibrate(200L);
            }
            AndroidUtilities.shakeView(ChannelEditInfoActivity.this.checkTextView, 2.0F, 0);
          }
          else
          {
            ChannelEditInfoActivity.access$502(ChannelEditInfoActivity.this, true);
            if (ChannelEditInfoActivity.this.avatarUpdater.uploadingAvatar == null) {
              break;
            }
            ChannelEditInfoActivity.access$1202(ChannelEditInfoActivity.this, true);
            ChannelEditInfoActivity.access$1302(ChannelEditInfoActivity.this, new AlertDialog(ChannelEditInfoActivity.this.getParentActivity(), 1));
            ChannelEditInfoActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", NUM));
            ChannelEditInfoActivity.this.progressDialog.setCanceledOnTouchOutside(false);
            ChannelEditInfoActivity.this.progressDialog.setCancelable(false);
            ChannelEditInfoActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                ChannelEditInfoActivity.access$1202(ChannelEditInfoActivity.this, false);
                ChannelEditInfoActivity.access$1302(ChannelEditInfoActivity.this, null);
                ChannelEditInfoActivity.access$502(ChannelEditInfoActivity.this, false);
                try
                {
                  paramAnonymous2DialogInterface.dismiss();
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
            ChannelEditInfoActivity.this.progressDialog.show();
          }
        }
        label403:
        String str;
        if (ChannelEditInfoActivity.this.usernameTextView != null)
        {
          if (ChannelEditInfoActivity.this.currentChat.username == null) {
            break label801;
          }
          localObject = ChannelEditInfoActivity.this.currentChat.username;
          if (!ChannelEditInfoActivity.this.isPrivate) {
            break label807;
          }
          str = "";
          label416:
          if (!((String)localObject).equals(str)) {
            MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).updateChannelUserName(ChannelEditInfoActivity.this.chatId, str);
          }
        }
        if (!ChannelEditInfoActivity.this.currentChat.title.equals(ChannelEditInfoActivity.this.nameTextView.getText().toString())) {
          MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).changeChatTitle(ChannelEditInfoActivity.this.chatId, ChannelEditInfoActivity.this.nameTextView.getText().toString());
        }
        if ((ChannelEditInfoActivity.this.info != null) && (!ChannelEditInfoActivity.this.info.about.equals(ChannelEditInfoActivity.this.descriptionTextView.getText().toString()))) {
          MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).updateChannelAbout(ChannelEditInfoActivity.this.chatId, ChannelEditInfoActivity.this.descriptionTextView.getText().toString(), ChannelEditInfoActivity.this.info);
        }
        if ((ChannelEditInfoActivity.this.headerCell2 != null) && (ChannelEditInfoActivity.this.headerCell2.getVisibility() == 0) && (ChannelEditInfoActivity.this.info != null) && (ChannelEditInfoActivity.this.currentChat.creator) && (ChannelEditInfoActivity.this.info.hidden_prehistory != ChannelEditInfoActivity.this.historyHidden))
        {
          ChannelEditInfoActivity.this.info.hidden_prehistory = ChannelEditInfoActivity.this.historyHidden;
          MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).toogleChannelInvitesHistory(ChannelEditInfoActivity.this.chatId, ChannelEditInfoActivity.this.historyHidden);
        }
        if (ChannelEditInfoActivity.this.signMessages != ChannelEditInfoActivity.this.currentChat.signatures)
        {
          ChannelEditInfoActivity.this.currentChat.signatures = true;
          MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).toogleChannelSignatures(ChannelEditInfoActivity.this.chatId, ChannelEditInfoActivity.this.signMessages);
        }
        if (ChannelEditInfoActivity.this.uploadedAvatar != null) {
          MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).changeChatAvatar(ChannelEditInfoActivity.this.chatId, ChannelEditInfoActivity.this.uploadedAvatar);
        }
        for (;;)
        {
          ChannelEditInfoActivity.this.finishFragment();
          break;
          label799:
          break;
          label801:
          localObject = "";
          break label403;
          label807:
          str = ChannelEditInfoActivity.this.usernameTextView.getText().toString();
          break label416;
          if ((ChannelEditInfoActivity.this.avatar == null) && ((ChannelEditInfoActivity.this.currentChat.photo instanceof TLRPC.TL_chatPhoto))) {
            MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).changeChatAvatar(ChannelEditInfoActivity.this.chatId, null);
          }
        }
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
    this.fragmentView = new ScrollView(paramContext);
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    Object localObject1 = (ScrollView)this.fragmentView;
    ((ScrollView)localObject1).setFillViewport(true);
    this.linearLayout = new LinearLayout(paramContext);
    ((ScrollView)localObject1).addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
    this.linearLayout.setOrientation(1);
    this.actionBar.setTitle(LocaleController.getString("ChannelEdit", NUM));
    this.linearLayout2 = new LinearLayout(paramContext);
    this.linearLayout2.setOrientation(1);
    this.linearLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
    localObject1 = new FrameLayout(paramContext);
    this.linearLayout2.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0F));
    this.avatarDrawable.setInfo(5, null, null, false);
    this.avatarDrawable.setDrawPhoto(true);
    Object localObject2 = this.avatarImage;
    int i;
    float f1;
    label289:
    float f2;
    label300:
    label381:
    label403:
    label602:
    label613:
    label830:
    String str;
    boolean bool;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label3224;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label3232;
      }
      f2 = 16.0F;
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 64.0F, i | 0x30, f1, 12.0F, f2, 12.0F));
      this.avatarImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChannelEditInfoActivity.this.getParentActivity() == null) {
            return;
          }
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChannelEditInfoActivity.this.getParentActivity());
          if (ChannelEditInfoActivity.this.avatar != null)
          {
            paramAnonymousView = new CharSequence[3];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", NUM);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", NUM);
            paramAnonymousView[2] = LocaleController.getString("DeletePhoto", NUM);
          }
          for (;;)
          {
            localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (paramAnonymous2Int == 0) {
                  ChannelEditInfoActivity.this.avatarUpdater.openCamera();
                }
                for (;;)
                {
                  return;
                  if (paramAnonymous2Int == 1)
                  {
                    ChannelEditInfoActivity.this.avatarUpdater.openGallery();
                  }
                  else if (paramAnonymous2Int == 2)
                  {
                    ChannelEditInfoActivity.access$2602(ChannelEditInfoActivity.this, null);
                    ChannelEditInfoActivity.access$2402(ChannelEditInfoActivity.this, null);
                    ChannelEditInfoActivity.this.avatarImage.setImage(ChannelEditInfoActivity.this.avatar, "50_50", ChannelEditInfoActivity.this.avatarDrawable);
                  }
                }
              }
            });
            ChannelEditInfoActivity.this.showDialog(localBuilder.create());
            break;
            paramAnonymousView = new CharSequence[2];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", NUM);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", NUM);
          }
        }
      });
      this.nameTextView = new EditTextBoldCursor(paramContext);
      if (!this.currentChat.megagroup) {
        break label3238;
      }
      this.nameTextView.setHint(LocaleController.getString("GroupName", NUM));
      this.nameTextView.setMaxLines(4);
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label3257;
      }
      i = 5;
      ((EditTextBoldCursor)localObject2).setGravity(i | 0x10);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.nameTextView.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
      this.nameTextView.setImeOptions(268435456);
      this.nameTextView.setInputType(16385);
      this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      this.nameTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
      this.nameTextView.setFocusable(this.nameTextView.isEnabled());
      localObject2 = new InputFilter.LengthFilter(100);
      this.nameTextView.setFilters(new InputFilter[] { localObject2 });
      this.nameTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setCursorSize(AndroidUtilities.dp(20.0F));
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setCursorWidth(1.5F);
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label3263;
      }
      f1 = 16.0F;
      if (!LocaleController.isRTL) {
        break label3271;
      }
      f2 = 96.0F;
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, 16, f1, 0.0F, f2, 0.0F));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          AvatarDrawable localAvatarDrawable = ChannelEditInfoActivity.this.avatarDrawable;
          if (ChannelEditInfoActivity.this.nameTextView.length() > 0) {}
          for (paramAnonymousEditable = ChannelEditInfoActivity.this.nameTextView.getText().toString();; paramAnonymousEditable = null)
          {
            localAvatarDrawable.setInfo(5, paramAnonymousEditable, null, false);
            ChannelEditInfoActivity.this.avatarImage.invalidate();
            return;
          }
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      this.lineView = new View(paramContext);
      this.lineView.setBackgroundColor(Theme.getColor("divider"));
      this.linearLayout.addView(this.lineView, new LinearLayout.LayoutParams(-1, 1));
      this.linearLayout3 = new LinearLayout(paramContext);
      this.linearLayout3.setOrientation(1);
      this.linearLayout3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      this.linearLayout.addView(this.linearLayout3, LayoutHelper.createLinear(-1, -2));
      this.descriptionTextView = new EditTextBoldCursor(paramContext);
      this.descriptionTextView.setTextSize(1, 16.0F);
      this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.descriptionTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0F));
      this.descriptionTextView.setBackgroundDrawable(null);
      localObject1 = this.descriptionTextView;
      if (!LocaleController.isRTL) {
        break label3279;
      }
      i = 5;
      ((EditTextBoldCursor)localObject1).setGravity(i);
      this.descriptionTextView.setInputType(180225);
      this.descriptionTextView.setImeOptions(6);
      this.descriptionTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
      this.descriptionTextView.setFocusable(this.descriptionTextView.isEnabled());
      localObject1 = new InputFilter.LengthFilter(255);
      this.descriptionTextView.setFilters(new InputFilter[] { localObject1 });
      this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", NUM));
      this.descriptionTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0F));
      this.descriptionTextView.setCursorWidth(1.5F);
      this.linearLayout3.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 17.0F, 12.0F, 17.0F, 6.0F));
      this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousInt == 6) && (ChannelEditInfoActivity.this.doneButton != null)) {
            ChannelEditInfoActivity.this.doneButton.performClick();
          }
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
      });
      this.descriptionTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable) {}
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      this.sectionCell = new ShadowSectionCell(paramContext);
      this.linearLayout.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
      this.container1 = new FrameLayout(paramContext);
      this.container1.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      this.linearLayout.addView(this.container1, LayoutHelper.createLinear(-1, -2));
      if ((this.currentChat.creator) && ((this.info == null) || (this.info.can_set_username)))
      {
        this.linearLayoutTypeContainer = new LinearLayout(paramContext);
        this.linearLayoutTypeContainer.setOrientation(1);
        this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1 = new RadioButtonCell(paramContext);
        this.radioButtonCell1.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (!this.currentChat.megagroup) {
          break label3291;
        }
        localObject2 = this.radioButtonCell1;
        str = LocaleController.getString("MegaPublic", NUM);
        localObject1 = LocaleController.getString("MegaPublicInfo", NUM);
        if (this.isPrivate) {
          break label3285;
        }
        bool = true;
        label1237:
        ((RadioButtonCell)localObject2).setTextAndValue(str, (String)localObject1, bool);
        this.linearLayoutTypeContainer.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (!ChannelEditInfoActivity.this.isPrivate) {}
            for (;;)
            {
              return;
              ChannelEditInfoActivity.access$802(ChannelEditInfoActivity.this, false);
              ChannelEditInfoActivity.this.updatePrivatePublic();
            }
          }
        });
        this.radioButtonCell2 = new RadioButtonCell(paramContext);
        this.radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (!this.currentChat.megagroup) {
          break label3345;
        }
        this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", NUM), LocaleController.getString("MegaPrivateInfo", NUM), this.isPrivate);
        label1340:
        this.linearLayoutTypeContainer.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell2.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (ChannelEditInfoActivity.this.isPrivate) {}
            for (;;)
            {
              return;
              ChannelEditInfoActivity.access$802(ChannelEditInfoActivity.this, true);
              ChannelEditInfoActivity.this.updatePrivatePublic();
            }
          }
        });
        this.sectionCell2 = new ShadowSectionCell(paramContext);
        this.linearLayout.addView(this.sectionCell2, LayoutHelper.createLinear(-1, -2));
        this.linkContainer = new LinearLayout(paramContext);
        this.linkContainer.setOrientation(1);
        this.linkContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(paramContext);
        this.linkContainer.addView(this.headerCell);
        this.publicContainer = new LinearLayout(paramContext);
        this.publicContainer.setOrientation(0);
        this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0F, 7.0F, 17.0F, 0.0F));
        this.editText = new EditText(paramContext);
        this.editText.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/");
        this.editText.setTextSize(1, 18.0F);
        this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setBackgroundDrawable(null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
        this.usernameTextView = new EditTextBoldCursor(paramContext);
        this.usernameTextView.setTextSize(1, 18.0F);
        if (!this.isPrivate) {
          this.usernameTextView.setText(this.currentChat.username);
        }
        this.usernameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.usernameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setMaxLines(1);
        this.usernameTextView.setLines(1);
        this.usernameTextView.setBackgroundDrawable(null);
        this.usernameTextView.setPadding(0, 0, 0, 0);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setInputType(163872);
        this.usernameTextView.setImeOptions(6);
        this.usernameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", NUM));
        this.usernameTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.usernameTextView.setCursorSize(AndroidUtilities.dp(20.0F));
        this.usernameTextView.setCursorWidth(1.5F);
        this.publicContainer.addView(this.usernameTextView, LayoutHelper.createLinear(-1, 36));
        this.usernameTextView.addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramAnonymousEditable) {}
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            ChannelEditInfoActivity.this.checkUserName(ChannelEditInfoActivity.this.usernameTextView.getText().toString());
          }
        });
        this.privateContainer = new TextBlockCell(paramContext);
        this.privateContainer.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.linkContainer.addView(this.privateContainer);
        this.privateContainer.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (ChannelEditInfoActivity.this.invite == null) {}
            for (;;)
            {
              return;
              try
              {
                ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelEditInfoActivity.this.invite.link));
                Toast.makeText(ChannelEditInfoActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
              }
              catch (Exception paramAnonymousView)
              {
                FileLog.e(paramAnonymousView);
              }
            }
          }
        });
        this.checkTextView = new TextView(paramContext);
        this.checkTextView.setTextSize(1, 15.0F);
        localObject1 = this.checkTextView;
        if (!LocaleController.isRTL) {
          break label3377;
        }
        i = 5;
        label1996:
        ((TextView)localObject1).setGravity(i);
        this.checkTextView.setVisibility(8);
        localObject1 = this.linkContainer;
        localObject2 = this.checkTextView;
        if (!LocaleController.isRTL) {
          break label3383;
        }
        i = 5;
        label2030:
        ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-2, -2, i, 17, 3, 17, 7));
        this.typeInfoCell = new TextInfoPrivacyCell(paramContext);
        this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
        this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
        this.loadingAdminedCell = new LoadingCell(paramContext);
        this.linearLayout.addView(this.loadingAdminedCell, LayoutHelper.createLinear(-1, -2));
        this.adminnedChannelsLayout = new LinearLayout(paramContext);
        this.adminnedChannelsLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.adminnedChannelsLayout.setOrientation(1);
        this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
        this.adminedInfoCell = new ShadowSectionCell(paramContext);
        this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
        updatePrivatePublic();
      }
      if ((this.currentChat.creator) && (this.currentChat.megagroup))
      {
        this.headerCell2 = new HeaderCell(paramContext);
        this.headerCell2.setText(LocaleController.getString("ChatHistory", NUM));
        this.headerCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.headerCell2);
        this.linearLayoutInviteContainer = new LinearLayout(paramContext);
        this.linearLayoutInviteContainer.setOrientation(1);
        this.linearLayoutInviteContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell3 = new RadioButtonCell(paramContext);
        this.radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        localObject1 = this.radioButtonCell3;
        str = LocaleController.getString("ChatHistoryVisible", NUM);
        localObject2 = LocaleController.getString("ChatHistoryVisibleInfo", NUM);
        if (this.historyHidden) {
          break label3389;
        }
        bool = true;
        label2390:
        ((RadioButtonCell)localObject1).setTextAndValue(str, (String)localObject2, bool);
        this.linearLayoutInviteContainer.addView(this.radioButtonCell3, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell3.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            ChannelEditInfoActivity.this.radioButtonCell3.setChecked(true, true);
            ChannelEditInfoActivity.this.radioButtonCell4.setChecked(false, true);
            ChannelEditInfoActivity.access$2002(ChannelEditInfoActivity.this, false);
          }
        });
        this.radioButtonCell4 = new RadioButtonCell(paramContext);
        this.radioButtonCell4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.radioButtonCell4.setTextAndValue(LocaleController.getString("ChatHistoryHidden", NUM), LocaleController.getString("ChatHistoryHiddenInfo", NUM), this.historyHidden);
        this.linearLayoutInviteContainer.addView(this.radioButtonCell4, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell4.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            ChannelEditInfoActivity.this.radioButtonCell3.setChecked(false, true);
            ChannelEditInfoActivity.this.radioButtonCell4.setChecked(true, true);
            ChannelEditInfoActivity.access$2002(ChannelEditInfoActivity.this, true);
          }
        });
        this.sectionCell3 = new ShadowSectionCell(paramContext);
        this.linearLayout.addView(this.sectionCell3, LayoutHelper.createLinear(-1, -2));
        updatePrivatePublic();
      }
      this.lineView2 = new View(paramContext);
      this.lineView2.setBackgroundColor(Theme.getColor("divider"));
      this.linearLayout.addView(this.lineView2, new LinearLayout.LayoutParams(-1, 1));
      this.container2 = new FrameLayout(paramContext);
      this.container2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      this.container3 = new FrameLayout(paramContext);
      this.container3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      this.linearLayout.addView(this.container3, LayoutHelper.createLinear(-1, -2));
      this.lineView3 = new View(paramContext);
      this.lineView3.setBackgroundColor(Theme.getColor("divider"));
      this.linearLayout.addView(this.lineView3, new LinearLayout.LayoutParams(-1, 1));
      this.linearLayout.addView(this.container2, LayoutHelper.createLinear(-1, -2));
      if (this.currentChat.megagroup) {
        break label3395;
      }
      this.textCheckCell = new TextCheckCell(paramContext);
      this.textCheckCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      this.textCheckCell.setTextAndCheck(LocaleController.getString("ChannelSignMessages", NUM), this.signMessages, false);
      this.container2.addView(this.textCheckCell, LayoutHelper.createFrame(-1, -2.0F));
      this.textCheckCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ChannelEditInfoActivity localChannelEditInfoActivity = ChannelEditInfoActivity.this;
          if (!ChannelEditInfoActivity.this.signMessages) {}
          for (boolean bool = true;; bool = false)
          {
            ChannelEditInfoActivity.access$2202(localChannelEditInfoActivity, bool);
            ((TextCheckCell)paramAnonymousView).setChecked(ChannelEditInfoActivity.this.signMessages);
            return;
          }
        }
      });
      this.infoCell = new TextInfoPrivacyCell(paramContext);
      this.infoCell.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
      this.infoCell.setText(LocaleController.getString("ChannelSignMessagesInfo", NUM));
      this.linearLayout.addView(this.infoCell, LayoutHelper.createLinear(-1, -2));
      label2871:
      if (!this.currentChat.creator) {
        break label3625;
      }
      this.container3 = new FrameLayout(paramContext);
      this.container3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      this.linearLayout.addView(this.container3, LayoutHelper.createLinear(-1, -2));
      this.textCell = new TextSettingsCell(paramContext);
      this.textCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
      this.textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      if (!this.currentChat.megagroup) {
        break label3586;
      }
      this.textCell.setText(LocaleController.getString("DeleteMega", NUM), false);
      label2986:
      this.container3.addView(this.textCell, LayoutHelper.createFrame(-1, -2.0F));
      this.textCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new AlertDialog.Builder(ChannelEditInfoActivity.this.getParentActivity());
          if (ChannelEditInfoActivity.this.currentChat.megagroup) {
            paramAnonymousView.setMessage(LocaleController.getString("MegaDeleteAlert", NUM));
          }
          for (;;)
          {
            paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
            paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (AndroidUtilities.isTablet()) {
                  NotificationCenter.getInstance(ChannelEditInfoActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(-ChannelEditInfoActivity.this.chatId) });
                }
                for (;;)
                {
                  MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).deleteUserFromChat(ChannelEditInfoActivity.this.chatId, MessagesController.getInstance(ChannelEditInfoActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(ChannelEditInfoActivity.this.currentAccount).getClientUserId())), ChannelEditInfoActivity.this.info, true);
                  ChannelEditInfoActivity.this.finishFragment();
                  return;
                  NotificationCenter.getInstance(ChannelEditInfoActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            ChannelEditInfoActivity.this.showDialog(paramAnonymousView.create());
            return;
            paramAnonymousView.setMessage(LocaleController.getString("ChannelDeleteAlert", NUM));
          }
        }
      });
      this.infoCell2 = new TextInfoPrivacyCell(paramContext);
      this.infoCell2.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
      if (!this.currentChat.megagroup) {
        break label3606;
      }
      this.infoCell2.setText(LocaleController.getString("MegaDeleteInfo", NUM));
      label3074:
      this.linearLayout.addView(this.infoCell2, LayoutHelper.createLinear(-1, -2));
      if (this.infoCell3 != null)
      {
        if (this.infoCell2 != null) {
          break label3700;
        }
        this.infoCell3.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
      }
      label3122:
      this.nameTextView.setText(this.currentChat.title);
      this.nameTextView.setSelection(this.nameTextView.length());
      if (this.info != null) {
        this.descriptionTextView.setText(this.info.about);
      }
      if (this.currentChat.photo == null) {
        break label3720;
      }
      this.avatar = this.currentChat.photo.photo_small;
      this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable);
    }
    for (;;)
    {
      return this.fragmentView;
      i = 3;
      break;
      label3224:
      f1 = 16.0F;
      break label289;
      label3232:
      f2 = 0.0F;
      break label300;
      label3238:
      this.nameTextView.setHint(LocaleController.getString("EnterChannelName", NUM));
      break label381;
      label3257:
      i = 3;
      break label403;
      label3263:
      f1 = 96.0F;
      break label602;
      label3271:
      f2 = 16.0F;
      break label613;
      label3279:
      i = 3;
      break label830;
      label3285:
      bool = false;
      break label1237;
      label3291:
      localObject1 = this.radioButtonCell1;
      str = LocaleController.getString("ChannelPublic", NUM);
      localObject2 = LocaleController.getString("ChannelPublicInfo", NUM);
      if (!this.isPrivate) {}
      for (bool = true;; bool = false)
      {
        ((RadioButtonCell)localObject1).setTextAndValue(str, (String)localObject2, bool);
        break;
      }
      label3345:
      this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", NUM), LocaleController.getString("ChannelPrivateInfo", NUM), this.isPrivate);
      break label1340;
      label3377:
      i = 3;
      break label1996;
      label3383:
      i = 3;
      break label2030;
      label3389:
      bool = false;
      break label2390;
      label3395:
      if ((this.info == null) || (!this.info.can_set_stickers)) {
        break label2871;
      }
      this.textCell2 = new TextSettingsCell(paramContext);
      this.textCell2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.textCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      if (this.info.stickerset != null) {
        this.textCell2.setTextAndValue(LocaleController.getString("GroupStickers", NUM), this.info.stickerset.title, false);
      }
      for (;;)
      {
        this.container3.addView(this.textCell2, LayoutHelper.createFrame(-1, -2.0F));
        this.textCell2.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new GroupStickersActivity(ChannelEditInfoActivity.this.currentChat.id);
            paramAnonymousView.setInfo(ChannelEditInfoActivity.this.info);
            ChannelEditInfoActivity.this.presentFragment(paramAnonymousView);
          }
        });
        this.infoCell3 = new TextInfoPrivacyCell(paramContext);
        this.infoCell3.setText(LocaleController.getString("GroupStickersInfo", NUM));
        this.linearLayout.addView(this.infoCell3, LayoutHelper.createLinear(-1, -2));
        break;
        this.textCell2.setText(LocaleController.getString("GroupStickers", NUM), false);
      }
      label3586:
      this.textCell.setText(LocaleController.getString("ChannelDelete", NUM), false);
      break label2986;
      label3606:
      this.infoCell2.setText(LocaleController.getString("ChannelDeleteInfo", NUM));
      break label3074;
      label3625:
      if (this.currentChat.megagroup) {
        if (this.infoCell3 == null) {
          this.sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
        }
      }
      for (;;)
      {
        this.lineView3.setVisibility(8);
        this.lineView2.setVisibility(8);
        break;
        this.infoCell.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
      }
      label3700:
      this.infoCell3.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
      break label3122;
      label3720:
      this.avatarImage.setImageDrawable(this.avatarDrawable);
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    RadioButtonCell localRadioButtonCell;
    if (paramInt1 == NotificationCenter.chatInfoDidLoaded)
    {
      paramVarArgs = (TLRPC.ChatFull)paramVarArgs[0];
      if (paramVarArgs.id == this.chatId) {
        if (this.info == null)
        {
          this.descriptionTextView.setText(paramVarArgs.about);
          this.historyHidden = paramVarArgs.hidden_prehistory;
          if (this.radioButtonCell3 != null)
          {
            localRadioButtonCell = this.radioButtonCell3;
            if (this.historyHidden) {
              break label112;
            }
          }
        }
      }
    }
    label112:
    for (boolean bool = true;; bool = false)
    {
      localRadioButtonCell.setChecked(bool, false);
      this.radioButtonCell4.setChecked(this.historyHidden, false);
      this.info = paramVarArgs;
      this.invite = paramVarArgs.exported_invite;
      updatePrivatePublic();
      return;
    }
  }
  
  public void didUploadedPhoto(final TLRPC.InputFile paramInputFile, final TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ChannelEditInfoActivity.access$2402(ChannelEditInfoActivity.this, paramInputFile);
        ChannelEditInfoActivity.access$2602(ChannelEditInfoActivity.this, paramPhotoSize1.location);
        ChannelEditInfoActivity.this.avatarImage.setImage(ChannelEditInfoActivity.this.avatar, "50_50", ChannelEditInfoActivity.this.avatarDrawable);
        if (ChannelEditInfoActivity.this.createAfterUpload) {}
        try
        {
          if ((ChannelEditInfoActivity.this.progressDialog != null) && (ChannelEditInfoActivity.this.progressDialog.isShowing()))
          {
            ChannelEditInfoActivity.this.progressDialog.dismiss();
            ChannelEditInfoActivity.access$1302(ChannelEditInfoActivity.this, null);
          }
          ChannelEditInfoActivity.access$502(ChannelEditInfoActivity.this, false);
          ChannelEditInfoActivity.this.doneButton.performClick();
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
    });
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local21 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        AvatarDrawable localAvatarDrawable;
        if (ChannelEditInfoActivity.this.avatarImage != null)
        {
          localAvatarDrawable = ChannelEditInfoActivity.this.avatarDrawable;
          if (ChannelEditInfoActivity.this.nameTextView.length() <= 0) {
            break label126;
          }
        }
        label126:
        for (Object localObject = ChannelEditInfoActivity.this.nameTextView.getText().toString();; localObject = null)
        {
          localAvatarDrawable.setInfo(5, (String)localObject, null, false);
          ChannelEditInfoActivity.this.avatarImage.invalidate();
          if (ChannelEditInfoActivity.this.adminnedChannelsLayout == null) {
            break;
          }
          int i = ChannelEditInfoActivity.this.adminnedChannelsLayout.getChildCount();
          for (int j = 0; j < i; j++)
          {
            localObject = ChannelEditInfoActivity.this.adminnedChannelsLayout.getChildAt(j);
            if ((localObject instanceof AdminedChannelCell)) {
              ((AdminedChannelCell)localObject).update();
            }
          }
        }
      }
    };
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.linearLayout3, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.container1, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.container2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.container3, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.container4, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(null, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable }, local21, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundBlue"), new ThemeDescription(this.lineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "divider"), new ThemeDescription(this.lineView2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "divider"), new ThemeDescription(this.lineView3, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "divider"), new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.sectionCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.sectionCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.textCheckCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.textCheckCell, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.textCheckCell, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.textCheckCell, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.textCheckCell, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.textCheckCell, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"), new ThemeDescription(this.infoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.infoCell, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.textCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.textCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText5"), new ThemeDescription(this.textCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.textCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.infoCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.infoCell2, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.infoCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.infoCell3, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.headerCell, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.headerCell2, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGrayText8"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGreenText"), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.privateContainer, 0, new Class[] { TextBlockCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.loadingAdminedCell, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.linearLayoutInviteContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"), new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"), new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "statusTextView" }, null, null, null, "windowBackgroundWhiteGrayText"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "statusTextView" }, null, null, null, "windowBackgroundWhiteLinkText"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "deleteButton" }, null, null, null, "windowBackgroundWhiteGrayText"), new ThemeDescription(null, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable }, local21, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local21, "avatar_backgroundPink") };
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public boolean onFragmentCreate()
  {
    boolean bool1 = false;
    boolean bool2 = false;
    this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
    final CountDownLatch localCountDownLatch;
    if (this.currentChat == null)
    {
      localCountDownLatch = new CountDownLatch(1);
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          ChannelEditInfoActivity.access$002(ChannelEditInfoActivity.this, MessagesStorage.getInstance(ChannelEditInfoActivity.this.currentAccount).getChat(ChannelEditInfoActivity.this.chatId));
          localCountDownLatch.countDown();
        }
      });
    }
    for (;;)
    {
      try
      {
        localCountDownLatch.await();
        bool3 = bool2;
        if (this.currentChat != null)
        {
          MessagesController.getInstance(this.currentAccount).putChat(this.currentChat, true);
          if (this.info != null) {
            break label151;
          }
          MessagesStorage.getInstance(this.currentAccount).loadChatInfo(this.chatId, localCountDownLatch, false, false);
        }
      }
      catch (Exception localException2)
      {
        try
        {
          localCountDownLatch.await();
          if (this.info == null)
          {
            bool3 = bool2;
            return bool3;
            localException2 = localException2;
            FileLog.e(localException2);
          }
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
          continue;
        }
      }
      label151:
      if (this.currentChat.username != null)
      {
        bool3 = bool1;
        if (this.currentChat.username.length() != 0) {}
      }
      else
      {
        bool3 = true;
      }
      this.isPrivate = bool3;
      if ((this.isPrivate) && (this.currentChat.creator))
      {
        TLRPC.TL_channels_checkUsername localTL_channels_checkUsername = new TLRPC.TL_channels_checkUsername();
        localTL_channels_checkUsername.username = "1";
        localTL_channels_checkUsername.channel = new TLRPC.TL_inputChannelEmpty();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_checkUsername, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                ChannelEditInfoActivity localChannelEditInfoActivity = ChannelEditInfoActivity.this;
                if ((paramAnonymousTL_error == null) || (!paramAnonymousTL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH"))) {}
                for (boolean bool = true;; bool = false)
                {
                  ChannelEditInfoActivity.access$302(localChannelEditInfoActivity, bool);
                  if (!ChannelEditInfoActivity.this.canCreatePublic) {
                    ChannelEditInfoActivity.this.loadAdminedChannels();
                  }
                  return;
                }
              }
            });
          }
        });
      }
      this.avatarUpdater.parentFragment = this;
      this.avatarUpdater.delegate = this;
      this.signMessages = this.currentChat.signatures;
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
      boolean bool3 = super.onFragmentCreate();
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.avatarUpdater != null) {
      this.avatarUpdater.clear();
    }
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    if ((this.textCell2 != null) && (this.info != null))
    {
      if (this.info.stickerset == null) {
        break label67;
      }
      this.textCell2.setTextAndValue(LocaleController.getString("GroupStickers", NUM), this.info.stickerset.title, false);
    }
    for (;;)
    {
      return;
      label67:
      this.textCell2.setText(LocaleController.getString("GroupStickers", NUM), false);
    }
  }
  
  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.avatarUpdater != null) {
      this.avatarUpdater.currentPicturePath = paramBundle.getString("path");
    }
  }
  
  public void saveSelfArgs(Bundle paramBundle)
  {
    if ((this.avatarUpdater != null) && (this.avatarUpdater.currentPicturePath != null)) {
      paramBundle.putString("path", this.avatarUpdater.currentPicturePath);
    }
    if (this.nameTextView != null)
    {
      String str = this.nameTextView.getText().toString();
      if ((str != null) && (str.length() != 0)) {
        paramBundle.putString("nameTextView", str);
      }
    }
  }
  
  public void setInfo(TLRPC.ChatFull paramChatFull)
  {
    if ((this.info == null) && (paramChatFull != null)) {
      this.historyHidden = paramChatFull.hidden_prehistory;
    }
    this.info = paramChatFull;
    if (paramChatFull != null)
    {
      if (!(paramChatFull.exported_invite instanceof TLRPC.TL_chatInviteExported)) {
        break label47;
      }
      this.invite = paramChatFull.exported_invite;
    }
    for (;;)
    {
      return;
      label47:
      generateLink();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChannelEditInfoActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */