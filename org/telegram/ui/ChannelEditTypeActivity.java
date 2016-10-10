package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import org.telegram.ui.ActionBar.BaseFragment;
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
  private boolean canCreatePublic = true;
  private int chatId;
  private int checkReqId;
  private Runnable checkRunnable;
  private TextView checkTextView;
  private TLRPC.Chat currentChat;
  private boolean donePressed;
  private HeaderCell headerCell;
  private TLRPC.ExportedChatInvite invite;
  private boolean isPrivate;
  private String lastCheckName;
  private boolean lastNameAvailable;
  private LinearLayout linearLayout;
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
        break label295;
      }
      if ((!paramString.startsWith("_")) && (!paramString.endsWith("_"))) {
        break;
      }
      this.checkTextView.setText(LocaleController.getString("LinkInvalid", 2131165826));
      this.checkTextView.setTextColor(-3198928);
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
          this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", 2131165831));
          this.checkTextView.setTextColor(-3198928);
          return false;
        }
        this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", 2131165830));
        this.checkTextView.setTextColor(-3198928);
        return false;
      }
      if (((j < 48) || (j > 57)) && ((j < 97) || (j > 122)) && ((j < 65) || (j > 90)) && (j != 95))
      {
        this.checkTextView.setText(LocaleController.getString("LinkInvalid", 2131165826));
        this.checkTextView.setTextColor(-3198928);
        return false;
      }
      i += 1;
    }
    label295:
    if ((paramString == null) || (paramString.length() < 5))
    {
      if (this.currentChat.megagroup)
      {
        this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", 2131165829));
        this.checkTextView.setTextColor(-3198928);
        return false;
      }
      this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", 2131165828));
      this.checkTextView.setTextColor(-3198928);
      return false;
    }
    if (paramString.length() > 32)
    {
      this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", 2131165827));
      this.checkTextView.setTextColor(-3198928);
      return false;
    }
    this.checkTextView.setText(LocaleController.getString("LinkChecking", 2131165822));
    this.checkTextView.setTextColor(-9605774);
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
                    ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.formatString("LinkAvailable", 2131165821, new Object[] { ChannelEditTypeActivity.9.this.val$name }));
                    ChannelEditTypeActivity.this.checkTextView.setTextColor(-14248148);
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
                  ChannelEditTypeActivity.this.checkTextView.setTextColor(-3198928);
                  ChannelEditTypeActivity.access$702(ChannelEditTypeActivity.this, false);
                  return;
                  ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", 2131165824));
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
            ChannelEditTypeActivity.access$1702(ChannelEditTypeActivity.this, false);
            TextBlockCell localTextBlockCell = ChannelEditTypeActivity.this.privateContainer;
            if (ChannelEditTypeActivity.this.invite != null) {}
            for (String str = ChannelEditTypeActivity.this.invite.link;; str = LocaleController.getString("Loading", 2131165834))
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
                  localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
                  if (paramAnonymous3View.megagroup) {
                    localBuilder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", 2131166194, new Object[] { "telegram.me/" + paramAnonymous3View.username, paramAnonymous3View.title })));
                  }
                  for (;;)
                  {
                    localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                    localBuilder.setPositiveButton(LocaleController.getString("RevokeButton", 2131166192), new DialogInterface.OnClickListener()
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
                    localBuilder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", 2131166195, new Object[] { "telegram.me/" + paramAnonymous3View.username, paramAnonymous3View.title })));
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
      this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", 2131165400));
      this.typeInfoCell.setTextColor(-3198928);
      this.linkContainer.setVisibility(8);
      this.sectionCell.setVisibility(8);
      if (this.loadingAdminedChannels)
      {
        this.loadingAdminedCell.setVisibility(0);
        i = 0;
        while (i < this.adminedChannelCells.size())
        {
          ((AdminedChannelCell)this.adminedChannelCells.get(i)).setVisibility(8);
          i += 1;
        }
        this.typeInfoCell.setBackgroundResource(2130837689);
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
        this.typeInfoCell.setBackgroundResource(2130837688);
        this.loadingAdminedCell.setVisibility(8);
        i = 0;
        while (i < this.adminedChannelCells.size())
        {
          ((AdminedChannelCell)this.adminedChannelCells.get(i)).setVisibility(0);
          i += 1;
        }
        this.adminedInfoCell.setVisibility(0);
      }
    }
    this.typeInfoCell.setTextColor(-8355712);
    this.sectionCell.setVisibility(0);
    this.adminedInfoCell.setVisibility(8);
    this.typeInfoCell.setBackgroundResource(2130837689);
    int i = 0;
    while (i < this.adminedChannelCells.size())
    {
      ((AdminedChannelCell)this.adminedChannelCells.get(i)).setVisibility(8);
      i += 1;
    }
    this.linkContainer.setVisibility(0);
    this.loadingAdminedCell.setVisibility(8);
    Object localObject2;
    if (this.currentChat.megagroup)
    {
      localObject2 = this.typeInfoCell;
      if (this.isPrivate)
      {
        localObject1 = LocaleController.getString("MegaPrivateLinkHelp", 2131165859);
        label378:
        ((TextInfoPrivacyCell)localObject2).setText((CharSequence)localObject1);
        localObject2 = this.headerCell;
        if (!this.isPrivate) {
          break label565;
        }
        localObject1 = LocaleController.getString("ChannelInviteLinkTitle", 2131165427);
        label409:
        ((HeaderCell)localObject2).setText((String)localObject1);
        localObject1 = this.publicContainer;
        if (!this.isPrivate) {
          break label672;
        }
        i = 8;
        label432:
        ((LinearLayout)localObject1).setVisibility(i);
        localObject1 = this.privateContainer;
        if (!this.isPrivate) {
          break label677;
        }
        i = 0;
        label453:
        ((TextBlockCell)localObject1).setVisibility(i);
        localObject1 = this.linkContainer;
        if (!this.isPrivate) {
          break label683;
        }
        i = 0;
        label474:
        ((LinearLayout)localObject1).setPadding(0, 0, 0, i);
        localObject2 = this.privateContainer;
        if (this.invite == null) {
          break label693;
        }
      }
    }
    label565:
    label603:
    label658:
    label672:
    label677:
    label683:
    label693:
    for (Object localObject1 = this.invite.link;; localObject1 = LocaleController.getString("Loading", 2131165834))
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
      localObject1 = LocaleController.getString("MegaUsernameHelp", 2131165862);
      break label378;
      localObject1 = LocaleController.getString("ChannelLinkTitle", 2131165434);
      break label409;
      localObject2 = this.typeInfoCell;
      if (this.isPrivate)
      {
        localObject1 = LocaleController.getString("ChannelPrivateLinkHelp", 2131165467);
        ((TextInfoPrivacyCell)localObject2).setText((CharSequence)localObject1);
        localObject2 = this.headerCell;
        if (!this.isPrivate) {
          break label658;
        }
      }
      for (localObject1 = LocaleController.getString("ChannelInviteLinkTitle", 2131165427);; localObject1 = LocaleController.getString("ChannelLinkTitle", 2131165434))
      {
        ((HeaderCell)localObject2).setText((String)localObject1);
        break;
        localObject1 = LocaleController.getString("ChannelUsernameHelp", 2131165486);
        break label603;
      }
      i = 0;
      break label432;
      i = 8;
      break label453;
      i = AndroidUtilities.dp(7.0F);
      break label474;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
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
    this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
    this.fragmentView = new ScrollView(paramContext);
    this.fragmentView.setBackgroundColor(-986896);
    Object localObject1 = (ScrollView)this.fragmentView;
    ((ScrollView)localObject1).setFillViewport(true);
    this.linearLayout = new LinearLayout(paramContext);
    ((ScrollView)localObject1).addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
    this.linearLayout.setOrientation(1);
    Object localObject2;
    String str1;
    String str2;
    boolean bool;
    if (this.currentChat.megagroup)
    {
      this.actionBar.setTitle(LocaleController.getString("GroupType", 2131165720));
      localObject1 = new LinearLayout(paramContext);
      ((LinearLayout)localObject1).setOrientation(1);
      ((LinearLayout)localObject1).setBackgroundColor(-1);
      this.linearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
      this.radioButtonCell1 = new RadioButtonCell(paramContext);
      this.radioButtonCell1.setBackgroundResource(2130837796);
      if (!this.currentChat.megagroup) {
        break label1115;
      }
      localObject2 = this.radioButtonCell1;
      str1 = LocaleController.getString("MegaPublic", 2131165860);
      str2 = LocaleController.getString("MegaPublicInfo", 2131165861);
      if (this.isPrivate) {
        break label1110;
      }
      bool = true;
      label262:
      ((RadioButtonCell)localObject2).setTextAndValue(str1, str2, bool, false);
      ((LinearLayout)localObject1).addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
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
      this.radioButtonCell2.setBackgroundResource(2130837796);
      if (!this.currentChat.megagroup) {
        break label1171;
      }
      this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", 2131165857), LocaleController.getString("MegaPrivateInfo", 2131165858), this.isPrivate, false);
      label365:
      ((LinearLayout)localObject1).addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
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
      this.linkContainer.setBackgroundColor(-1);
      this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
      this.headerCell = new HeaderCell(paramContext);
      this.linkContainer.addView(this.headerCell);
      this.publicContainer = new LinearLayout(paramContext);
      this.publicContainer.setOrientation(0);
      this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0F, 7.0F, 17.0F, 0.0F));
      localObject1 = new EditText(paramContext);
      ((EditText)localObject1).setText("telegram.me/");
      ((EditText)localObject1).setTextSize(1, 18.0F);
      ((EditText)localObject1).setHintTextColor(-6842473);
      ((EditText)localObject1).setTextColor(-14606047);
      ((EditText)localObject1).setMaxLines(1);
      ((EditText)localObject1).setLines(1);
      ((EditText)localObject1).setEnabled(false);
      ((EditText)localObject1).setBackgroundDrawable(null);
      ((EditText)localObject1).setPadding(0, 0, 0, 0);
      ((EditText)localObject1).setSingleLine(true);
      ((EditText)localObject1).setInputType(163840);
      ((EditText)localObject1).setImeOptions(6);
      this.publicContainer.addView((View)localObject1, LayoutHelper.createLinear(-2, 36));
      this.nameTextView = new EditText(paramContext);
      this.nameTextView.setTextSize(1, 18.0F);
      if (!this.isPrivate) {
        this.nameTextView.setText(this.currentChat.username);
      }
      this.nameTextView.setHintTextColor(-6842473);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setLines(1);
      this.nameTextView.setBackgroundDrawable(null);
      this.nameTextView.setPadding(0, 0, 0, 0);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setInputType(163872);
      this.nameTextView.setImeOptions(6);
      this.nameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", 2131165487));
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
      this.privateContainer.setBackgroundResource(2130837796);
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
            Toast.makeText(ChannelEditTypeActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", 2131165823), 0).show();
            return;
          }
          catch (Exception paramAnonymousView)
          {
            FileLog.e("tmessages", paramAnonymousView);
          }
        }
      });
      this.checkTextView = new TextView(paramContext);
      this.checkTextView.setTextSize(1, 15.0F);
      localObject1 = this.checkTextView;
      if (!LocaleController.isRTL) {
        break label1204;
      }
      i = 5;
      label918:
      ((TextView)localObject1).setGravity(i);
      this.checkTextView.setVisibility(8);
      localObject1 = this.linkContainer;
      localObject2 = this.checkTextView;
      if (!LocaleController.isRTL) {
        break label1209;
      }
    }
    label1110:
    label1115:
    label1171:
    label1204:
    label1209:
    for (int i = 5;; i = 3)
    {
      ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-2, -2, i, 17, 3, 17, 7));
      this.typeInfoCell = new TextInfoPrivacyCell(paramContext);
      this.typeInfoCell.setBackgroundResource(2130837689);
      this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
      this.loadingAdminedCell = new LoadingCell(paramContext);
      this.linearLayout.addView(this.loadingAdminedCell, LayoutHelper.createLinear(-1, -2));
      this.adminedInfoCell = new TextInfoPrivacyCell(paramContext);
      this.adminedInfoCell.setBackgroundResource(2130837689);
      this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
      updatePrivatePublic();
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("ChannelType", 2131165477));
      break;
      bool = false;
      break label262;
      localObject2 = this.radioButtonCell1;
      str1 = LocaleController.getString("ChannelPublic", 2131165468);
      str2 = LocaleController.getString("ChannelPublicInfo", 2131165470);
      if (!this.isPrivate) {}
      for (bool = true;; bool = false)
      {
        ((RadioButtonCell)localObject2).setTextAndValue(str1, str2, bool, false);
        break;
      }
      this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", 2131165465), LocaleController.getString("ChannelPrivateInfo", 2131165466), this.isPrivate, false);
      break label365;
      i = 3;
      break label918;
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
        FileLog.e("tmessages", localException);
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