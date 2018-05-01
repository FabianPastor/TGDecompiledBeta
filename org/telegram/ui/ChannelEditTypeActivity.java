package org.telegram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_chats;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
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
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;

public class ChannelEditTypeActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList();
  private TextInfoPrivacyCell adminedInfoCell;
  private LinearLayout adminnedChannelsLayout;
  private boolean canCreatePublic = true;
  private int chatId;
  private int checkReqId;
  private Runnable checkRunnable;
  private TextView checkTextView;
  private TLRPC.Chat currentChat;
  private boolean donePressed;
  private EditText editText;
  private HeaderCell headerCell;
  private TLRPC.ExportedChatInvite invite;
  private boolean isPrivate;
  private String lastCheckName;
  private boolean lastNameAvailable;
  private LinearLayout linearLayout;
  private LinearLayout linearLayout2;
  private LinearLayout linkContainer;
  private LoadingCell loadingAdminedCell;
  private boolean loadingAdminedChannels;
  private boolean loadingInvite;
  private EditText nameTextView;
  private TextBlockCell privateContainer;
  private LinearLayout publicContainer;
  private RadioButtonCell radioButtonCell1;
  private RadioButtonCell radioButtonCell2;
  private ShadowSectionCell sectionCell;
  private TextInfoPrivacyCell typeInfoCell;
  
  public ChannelEditTypeActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatId = paramBundle.getInt("chat_id", 0);
  }
  
  private boolean checkUserName(final String paramString)
  {
    if ((paramString != null) && (paramString.length() > 0)) {
      this.checkTextView.setVisibility(0);
    }
    for (;;)
    {
      if (this.checkRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        this.checkRunnable = null;
        this.lastCheckName = null;
        if (this.checkReqId != 0) {
          ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
        }
      }
      this.lastNameAvailable = false;
      if (paramString == null) {
        break label343;
      }
      if ((!paramString.startsWith("_")) && (!paramString.endsWith("_"))) {
        break;
      }
      this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
      this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
      this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      return false;
      this.checkTextView.setVisibility(8);
    }
    int i = 0;
    while (i < paramString.length())
    {
      int j = paramString.charAt(i);
      if ((i == 0) && (j >= 48) && (j <= 57))
      {
        if (this.currentChat.megagroup)
        {
          this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", NUM));
          this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
          this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
          return false;
        }
        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", NUM));
        this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
        this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
        return false;
      }
      if (((j < 48) || (j > 57)) && ((j < 97) || (j > 122)) && ((j < 65) || (j > 90)) && (j != 95))
      {
        this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
        this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
        this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
        return false;
      }
      i += 1;
    }
    label343:
    if ((paramString == null) || (paramString.length() < 5))
    {
      if (this.currentChat.megagroup)
      {
        this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", NUM));
        this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
        this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
        return false;
      }
      this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", NUM));
      this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
      this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      return false;
    }
    if (paramString.length() > 32)
    {
      this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", NUM));
      this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
      this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      return false;
    }
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
        localTL_channels_checkUsername.channel = MessagesController.getInputChannel(ChannelEditTypeActivity.this.chatId);
        ChannelEditTypeActivity.access$1502(ChannelEditTypeActivity.this, ConnectionsManager.getInstance().sendRequest(localTL_channels_checkUsername, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                ChannelEditTypeActivity.access$1502(ChannelEditTypeActivity.this, 0);
                if ((ChannelEditTypeActivity.this.lastCheckName != null) && (ChannelEditTypeActivity.this.lastCheckName.equals(ChannelEditTypeActivity.9.this.val$name)))
                {
                  if ((paramAnonymous2TL_error == null) && ((paramAnonymous2TLObject instanceof TLRPC.TL_boolTrue)))
                  {
                    ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.formatString("LinkAvailable", NUM, new Object[] { ChannelEditTypeActivity.9.this.val$name }));
                    ChannelEditTypeActivity.this.checkTextView.setTag("windowBackgroundWhiteGreenText");
                    ChannelEditTypeActivity.this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
                    ChannelEditTypeActivity.access$702(ChannelEditTypeActivity.this, true);
                  }
                }
                else {
                  return;
                }
                if ((paramAnonymous2TL_error != null) && (paramAnonymous2TL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")))
                {
                  ChannelEditTypeActivity.access$202(ChannelEditTypeActivity.this, false);
                  ChannelEditTypeActivity.this.loadAdminedChannels();
                }
                for (;;)
                {
                  ChannelEditTypeActivity.this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
                  ChannelEditTypeActivity.this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                  ChannelEditTypeActivity.access$702(ChannelEditTypeActivity.this, false);
                  return;
                  ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", NUM));
                }
              }
            });
          }
        }, 2));
      }
    };
    AndroidUtilities.runOnUIThread(this.checkRunnable, 300L);
    return true;
  }
  
  private void generateLink()
  {
    if ((this.loadingInvite) || (this.invite != null)) {
      return;
    }
    this.loadingInvite = true;
    TLRPC.TL_channels_exportInvite localTL_channels_exportInvite = new TLRPC.TL_channels_exportInvite();
    localTL_channels_exportInvite.channel = MessagesController.getInputChannel(this.chatId);
    ConnectionsManager.getInstance().sendRequest(localTL_channels_exportInvite, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (paramAnonymousTL_error == null) {
              ChannelEditTypeActivity.access$1102(ChannelEditTypeActivity.this, (TLRPC.ExportedChatInvite)paramAnonymousTLObject);
            }
            ChannelEditTypeActivity.access$1802(ChannelEditTypeActivity.this, false);
            TextBlockCell localTextBlockCell = ChannelEditTypeActivity.this.privateContainer;
            if (ChannelEditTypeActivity.this.invite != null) {}
            for (String str = ChannelEditTypeActivity.this.invite.link;; str = LocaleController.getString("Loading", NUM))
            {
              localTextBlockCell.setText(str, false);
              return;
            }
          }
        });
      }
    });
  }
  
  private void loadAdminedChannels()
  {
    if (this.loadingAdminedChannels) {
      return;
    }
    this.loadingAdminedChannels = true;
    updatePrivatePublic();
    TLRPC.TL_channels_getAdminedPublicChannels localTL_channels_getAdminedPublicChannels = new TLRPC.TL_channels_getAdminedPublicChannels();
    ConnectionsManager.getInstance().sendRequest(localTL_channels_getAdminedPublicChannels, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            ChannelEditTypeActivity.access$1202(ChannelEditTypeActivity.this, false);
            if ((paramAnonymousTLObject == null) || (ChannelEditTypeActivity.this.getParentActivity() == null)) {
              return;
            }
            int i = 0;
            while (i < ChannelEditTypeActivity.this.adminedChannelCells.size())
            {
              ChannelEditTypeActivity.this.linearLayout.removeView((View)ChannelEditTypeActivity.this.adminedChannelCells.get(i));
              i += 1;
            }
            ChannelEditTypeActivity.this.adminedChannelCells.clear();
            TLRPC.TL_messages_chats localTL_messages_chats = (TLRPC.TL_messages_chats)paramAnonymousTLObject;
            i = 0;
            if (i < localTL_messages_chats.chats.size())
            {
              AdminedChannelCell localAdminedChannelCell = new AdminedChannelCell(ChannelEditTypeActivity.this.getParentActivity(), new View.OnClickListener()
              {
                public void onClick(final View paramAnonymous3View)
                {
                  paramAnonymous3View = ((AdminedChannelCell)paramAnonymous3View.getParent()).getCurrentChannel();
                  AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChannelEditTypeActivity.this.getParentActivity());
                  localBuilder.setTitle(LocaleController.getString("AppName", NUM));
                  if (paramAnonymous3View.megagroup) {
                    localBuilder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", NUM, new Object[] { MessagesController.getInstance().linkPrefix + "/" + paramAnonymous3View.username, paramAnonymous3View.title })));
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
                        ConnectionsManager.getInstance().sendRequest(paramAnonymous4DialogInterface, new RequestDelegate()
                        {
                          public void run(TLObject paramAnonymous5TLObject, TLRPC.TL_error paramAnonymous5TL_error)
                          {
                            if ((paramAnonymous5TLObject instanceof TLRPC.TL_boolTrue)) {
                              AndroidUtilities.runOnUIThread(new Runnable()
                              {
                                public void run()
                                {
                                  ChannelEditTypeActivity.access$202(ChannelEditTypeActivity.this, true);
                                  if (ChannelEditTypeActivity.this.nameTextView.length() > 0) {
                                    ChannelEditTypeActivity.this.checkUserName(ChannelEditTypeActivity.this.nameTextView.getText().toString());
                                  }
                                  ChannelEditTypeActivity.this.updatePrivatePublic();
                                }
                              });
                            }
                          }
                        }, 64);
                      }
                    });
                    ChannelEditTypeActivity.this.showDialog(localBuilder.create());
                    return;
                    localBuilder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", NUM, new Object[] { MessagesController.getInstance().linkPrefix + "/" + paramAnonymous3View.username, paramAnonymous3View.title })));
                  }
                }
              });
              TLRPC.Chat localChat = (TLRPC.Chat)localTL_messages_chats.chats.get(i);
              if (i == localTL_messages_chats.chats.size() - 1) {}
              for (boolean bool = true;; bool = false)
              {
                localAdminedChannelCell.setChannel(localChat, bool);
                ChannelEditTypeActivity.this.adminedChannelCells.add(localAdminedChannelCell);
                ChannelEditTypeActivity.this.linearLayout.addView(localAdminedChannelCell, ChannelEditTypeActivity.this.linearLayout.getChildCount() - 1, LayoutHelper.createLinear(-1, 72));
                i += 1;
                break;
              }
            }
            ChannelEditTypeActivity.this.updatePrivatePublic();
          }
        });
      }
    });
  }
  
  private void updatePrivatePublic()
  {
    int j = 8;
    boolean bool = false;
    if (this.sectionCell == null) {
      return;
    }
    if ((!this.isPrivate) && (!this.canCreatePublic))
    {
      this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", NUM));
      this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
      this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      this.linkContainer.setVisibility(8);
      this.sectionCell.setVisibility(8);
      if (this.loadingAdminedChannels)
      {
        this.loadingAdminedCell.setVisibility(0);
        this.adminnedChannelsLayout.setVisibility(8);
        this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
        this.adminedInfoCell.setVisibility(8);
      }
      for (;;)
      {
        localObject1 = this.radioButtonCell1;
        if (!this.isPrivate) {
          bool = true;
        }
        ((RadioButtonCell)localObject1).setChecked(bool, true);
        this.radioButtonCell2.setChecked(this.isPrivate, true);
        this.nameTextView.clearFocus();
        AndroidUtilities.hideKeyboard(this.nameTextView);
        return;
        this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
        this.loadingAdminedCell.setVisibility(8);
        this.adminnedChannelsLayout.setVisibility(0);
        this.adminedInfoCell.setVisibility(0);
      }
    }
    this.typeInfoCell.setTag("windowBackgroundWhiteGrayText4");
    this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
    this.sectionCell.setVisibility(0);
    this.adminedInfoCell.setVisibility(8);
    this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), NUM, "windowBackgroundGrayShadow"));
    this.adminnedChannelsLayout.setVisibility(8);
    this.linkContainer.setVisibility(0);
    this.loadingAdminedCell.setVisibility(8);
    Object localObject2;
    label361:
    label392:
    int i;
    if (this.currentChat.megagroup)
    {
      localObject2 = this.typeInfoCell;
      if (this.isPrivate)
      {
        localObject1 = LocaleController.getString("MegaPrivateLinkHelp", NUM);
        ((TextInfoPrivacyCell)localObject2).setText((CharSequence)localObject1);
        localObject2 = this.headerCell;
        if (!this.isPrivate) {
          break label548;
        }
        localObject1 = LocaleController.getString("ChannelInviteLinkTitle", NUM);
        ((HeaderCell)localObject2).setText((String)localObject1);
        localObject1 = this.publicContainer;
        if (!this.isPrivate) {
          break label655;
        }
        i = 8;
        label415:
        ((LinearLayout)localObject1).setVisibility(i);
        localObject1 = this.privateContainer;
        if (!this.isPrivate) {
          break label660;
        }
        i = 0;
        label436:
        ((TextBlockCell)localObject1).setVisibility(i);
        localObject1 = this.linkContainer;
        if (!this.isPrivate) {
          break label666;
        }
        i = 0;
        label457:
        ((LinearLayout)localObject1).setPadding(0, 0, 0, i);
        localObject2 = this.privateContainer;
        if (this.invite == null) {
          break label676;
        }
      }
    }
    label548:
    label586:
    label641:
    label655:
    label660:
    label666:
    label676:
    for (Object localObject1 = this.invite.link;; localObject1 = LocaleController.getString("Loading", NUM))
    {
      ((TextBlockCell)localObject2).setText((String)localObject1, false);
      localObject1 = this.checkTextView;
      i = j;
      if (!this.isPrivate)
      {
        i = j;
        if (this.checkTextView.length() != 0) {
          i = 0;
        }
      }
      ((TextView)localObject1).setVisibility(i);
      break;
      localObject1 = LocaleController.getString("MegaUsernameHelp", NUM);
      break label361;
      localObject1 = LocaleController.getString("ChannelLinkTitle", NUM);
      break label392;
      localObject2 = this.typeInfoCell;
      if (this.isPrivate)
      {
        localObject1 = LocaleController.getString("ChannelPrivateLinkHelp", NUM);
        ((TextInfoPrivacyCell)localObject2).setText((CharSequence)localObject1);
        localObject2 = this.headerCell;
        if (!this.isPrivate) {
          break label641;
        }
      }
      for (localObject1 = LocaleController.getString("ChannelInviteLinkTitle", NUM);; localObject1 = LocaleController.getString("ChannelLinkTitle", NUM))
      {
        ((HeaderCell)localObject2).setText((String)localObject1);
        break;
        localObject1 = LocaleController.getString("ChannelUsernameHelp", NUM);
        break label586;
      }
      i = 0;
      break label415;
      i = 8;
      break label436;
      i = AndroidUtilities.dp(7.0F);
      break label457;
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
          ChannelEditTypeActivity.this.finishFragment();
        }
        while ((paramAnonymousInt != 1) || (ChannelEditTypeActivity.this.donePressed)) {
          return;
        }
        Object localObject;
        if ((!ChannelEditTypeActivity.this.isPrivate) && (((ChannelEditTypeActivity.this.currentChat.username == null) && (ChannelEditTypeActivity.this.nameTextView.length() != 0)) || ((ChannelEditTypeActivity.this.currentChat.username != null) && (!ChannelEditTypeActivity.this.currentChat.username.equalsIgnoreCase(ChannelEditTypeActivity.this.nameTextView.getText().toString())) && (ChannelEditTypeActivity.this.nameTextView.length() != 0) && (!ChannelEditTypeActivity.this.lastNameAvailable))))
        {
          localObject = (Vibrator)ChannelEditTypeActivity.this.getParentActivity().getSystemService("vibrator");
          if (localObject != null) {
            ((Vibrator)localObject).vibrate(200L);
          }
          AndroidUtilities.shakeView(ChannelEditTypeActivity.this.checkTextView, 2.0F, 0);
          return;
        }
        ChannelEditTypeActivity.access$402(ChannelEditTypeActivity.this, true);
        if (ChannelEditTypeActivity.this.currentChat.username != null)
        {
          localObject = ChannelEditTypeActivity.this.currentChat.username;
          if (!ChannelEditTypeActivity.this.isPrivate) {
            break label251;
          }
        }
        label251:
        for (String str = "";; str = ChannelEditTypeActivity.this.nameTextView.getText().toString())
        {
          if (!((String)localObject).equals(str)) {
            MessagesController.getInstance().updateChannelUserName(ChannelEditTypeActivity.this.chatId, str);
          }
          ChannelEditTypeActivity.this.finishFragment();
          return;
          localObject = "";
          break;
        }
      }
    });
    this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
    this.fragmentView = new ScrollView(paramContext);
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    Object localObject1 = (ScrollView)this.fragmentView;
    ((ScrollView)localObject1).setFillViewport(true);
    this.linearLayout = new LinearLayout(paramContext);
    ((ScrollView)localObject1).addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
    this.linearLayout.setOrientation(1);
    Object localObject2;
    String str;
    boolean bool;
    if (this.currentChat.megagroup)
    {
      this.actionBar.setTitle(LocaleController.getString("GroupType", NUM));
      this.linearLayout2 = new LinearLayout(paramContext);
      this.linearLayout2.setOrientation(1);
      this.linearLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
      this.radioButtonCell1 = new RadioButtonCell(paramContext);
      this.radioButtonCell1.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      if (!this.currentChat.megagroup) {
        break label1267;
      }
      localObject1 = this.radioButtonCell1;
      localObject2 = LocaleController.getString("MegaPublic", NUM);
      str = LocaleController.getString("MegaPublicInfo", NUM);
      if (this.isPrivate) {
        break label1262;
      }
      bool = true;
      label279:
      ((RadioButtonCell)localObject1).setTextAndValue((String)localObject2, str, bool);
      this.linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
      this.radioButtonCell1.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (!ChannelEditTypeActivity.this.isPrivate) {
            return;
          }
          ChannelEditTypeActivity.access$502(ChannelEditTypeActivity.this, false);
          ChannelEditTypeActivity.this.updatePrivatePublic();
        }
      });
      this.radioButtonCell2 = new RadioButtonCell(paramContext);
      this.radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      if (!this.currentChat.megagroup) {
        break label1322;
      }
      this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", NUM), LocaleController.getString("MegaPrivateInfo", NUM), this.isPrivate);
      label383:
      this.linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
      this.radioButtonCell2.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChannelEditTypeActivity.this.isPrivate) {
            return;
          }
          ChannelEditTypeActivity.access$502(ChannelEditTypeActivity.this, true);
          ChannelEditTypeActivity.this.updatePrivatePublic();
        }
      });
      this.sectionCell = new ShadowSectionCell(paramContext);
      this.linearLayout.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
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
      this.editText.setText(MessagesController.getInstance().linkPrefix + "/");
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
      this.nameTextView = new EditText(paramContext);
      this.nameTextView.setTextSize(1, 18.0F);
      if (!this.isPrivate) {
        this.nameTextView.setText(this.currentChat.username);
      }
      this.nameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setLines(1);
      this.nameTextView.setBackgroundDrawable(null);
      this.nameTextView.setPadding(0, 0, 0, 0);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setInputType(163872);
      this.nameTextView.setImeOptions(6);
      this.nameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", NUM));
      AndroidUtilities.clearCursorDrawable(this.nameTextView);
      this.publicContainer.addView(this.nameTextView, LayoutHelper.createLinear(-1, 36));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable) {}
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          ChannelEditTypeActivity.this.checkUserName(ChannelEditTypeActivity.this.nameTextView.getText().toString());
        }
      });
      this.privateContainer = new TextBlockCell(paramContext);
      this.privateContainer.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      this.linkContainer.addView(this.privateContainer);
      this.privateContainer.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChannelEditTypeActivity.this.invite == null) {
            return;
          }
          try
          {
            ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelEditTypeActivity.this.invite.link));
            Toast.makeText(ChannelEditTypeActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
            return;
          }
          catch (Exception paramAnonymousView)
          {
            FileLog.e(paramAnonymousView);
          }
        }
      });
      this.checkTextView = new TextView(paramContext);
      this.checkTextView.setTextSize(1, 15.0F);
      localObject1 = this.checkTextView;
      if (!LocaleController.isRTL) {
        break label1354;
      }
      i = 5;
      label1006:
      ((TextView)localObject1).setGravity(i);
      this.checkTextView.setVisibility(8);
      localObject1 = this.linkContainer;
      localObject2 = this.checkTextView;
      if (!LocaleController.isRTL) {
        break label1359;
      }
    }
    label1262:
    label1267:
    label1322:
    label1354:
    label1359:
    for (int i = 5;; i = 3)
    {
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
      this.adminedInfoCell = new TextInfoPrivacyCell(paramContext);
      this.adminedInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
      this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
      updatePrivatePublic();
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("ChannelType", NUM));
      break;
      bool = false;
      break label279;
      localObject1 = this.radioButtonCell1;
      localObject2 = LocaleController.getString("ChannelPublic", NUM);
      str = LocaleController.getString("ChannelPublicInfo", NUM);
      if (!this.isPrivate) {}
      for (bool = true;; bool = false)
      {
        ((RadioButtonCell)localObject1).setTextAndValue((String)localObject2, str, bool);
        break;
      }
      this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", NUM), LocaleController.getString("ChannelPrivateInfo", NUM), this.isPrivate);
      break label383;
      i = 3;
      break label1006;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.chatInfoDidLoaded)
    {
      paramVarArgs = (TLRPC.ChatFull)paramVarArgs[0];
      if (paramVarArgs.id == this.chatId)
      {
        this.invite = paramVarArgs.exported_invite;
        updatePrivatePublic();
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local11 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramAnonymousInt)
      {
        if (ChannelEditTypeActivity.this.adminnedChannelsLayout != null)
        {
          int i = ChannelEditTypeActivity.this.adminnedChannelsLayout.getChildCount();
          paramAnonymousInt = 0;
          while (paramAnonymousInt < i)
          {
            View localView = ChannelEditTypeActivity.this.adminnedChannelsLayout.getChildAt(paramAnonymousInt);
            if ((localView instanceof AdminedChannelCell)) {
              ((AdminedChannelCell)localView).update();
            }
            paramAnonymousInt += 1;
          }
        }
      }
    };
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.headerCell, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGrayText8"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGreenText"), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.privateContainer, 0, new Class[] { TextBlockCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.loadingAdminedCell, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "statusTextView" }, null, null, null, "windowBackgroundWhiteGrayText"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "statusTextView" }, null, null, null, "windowBackgroundWhiteLinkText"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "deleteButton" }, null, null, null, "windowBackgroundWhiteGrayText"), new ThemeDescription(null, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable }, local11, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundPink") };
  }
  
  public boolean onFragmentCreate()
  {
    boolean bool1 = false;
    boolean bool2 = false;
    this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
    final Object localObject;
    if (this.currentChat == null)
    {
      localObject = new Semaphore(0);
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          ChannelEditTypeActivity.access$002(ChannelEditTypeActivity.this, MessagesStorage.getInstance().getChat(ChannelEditTypeActivity.this.chatId));
          localObject.release();
        }
      });
    }
    try
    {
      ((Semaphore)localObject).acquire();
      if (this.currentChat != null)
      {
        MessagesController.getInstance().putChat(this.currentChat, true);
        if (this.currentChat.username != null)
        {
          bool1 = bool2;
          if (this.currentChat.username.length() != 0) {}
        }
        else
        {
          bool1 = true;
        }
        this.isPrivate = bool1;
        if (this.isPrivate)
        {
          localObject = new TLRPC.TL_channels_checkUsername();
          ((TLRPC.TL_channels_checkUsername)localObject).username = "1";
          ((TLRPC.TL_channels_checkUsername)localObject).channel = new TLRPC.TL_inputChannelEmpty();
          ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  ChannelEditTypeActivity localChannelEditTypeActivity = ChannelEditTypeActivity.this;
                  if ((paramAnonymousTL_error == null) || (!paramAnonymousTL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH"))) {}
                  for (boolean bool = true;; bool = false)
                  {
                    ChannelEditTypeActivity.access$202(localChannelEditTypeActivity, bool);
                    if (!ChannelEditTypeActivity.this.canCreatePublic) {
                      ChannelEditTypeActivity.this.loadAdminedChannels();
                    }
                    return;
                  }
                }
              });
            }
          });
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        bool1 = super.onFragmentCreate();
      }
      return bool1;
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
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
  }
  
  public void setInfo(TLRPC.ChatFull paramChatFull)
  {
    if (paramChatFull != null)
    {
      if ((paramChatFull.exported_invite instanceof TLRPC.TL_chatInviteExported)) {
        this.invite = paramChatFull.exported_invite;
      }
    }
    else {
      return;
    }
    generateLink();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChannelEditTypeActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */