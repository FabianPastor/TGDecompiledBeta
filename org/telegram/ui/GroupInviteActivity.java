package org.telegram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_exportChatInvite;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class GroupInviteActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int chat_id;
  private int copyLinkRow;
  private EmptyTextProgressView emptyView;
  private TLRPC.ExportedChatInvite invite;
  private int linkInfoRow;
  private int linkRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean loading;
  private int revokeLinkRow;
  private int rowCount;
  private int shadowRow;
  private int shareLinkRow;
  
  public GroupInviteActivity(int paramInt)
  {
    this.chat_id = paramInt;
  }
  
  private void generateLink(final boolean paramBoolean)
  {
    this.loading = true;
    Object localObject;
    if (ChatObject.isChannel(this.chat_id, this.currentAccount))
    {
      localObject = new TLRPC.TL_channels_exportInvite();
      ((TLRPC.TL_channels_exportInvite)localObject).channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
    }
    for (;;)
    {
      int i = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (paramAnonymousTL_error == null)
              {
                GroupInviteActivity.access$202(GroupInviteActivity.this, (TLRPC.ExportedChatInvite)paramAnonymousTLObject);
                if (GroupInviteActivity.3.this.val$newRequest) {
                  if (GroupInviteActivity.this.getParentActivity() != null) {}
                }
              }
              for (;;)
              {
                return;
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(GroupInviteActivity.this.getParentActivity());
                localBuilder.setMessage(LocaleController.getString("RevokeAlertNewLink", NUM));
                localBuilder.setTitle(LocaleController.getString("RevokeLink", NUM));
                localBuilder.setNegativeButton(LocaleController.getString("OK", NUM), null);
                GroupInviteActivity.this.showDialog(localBuilder.create());
                GroupInviteActivity.access$602(GroupInviteActivity.this, false);
                GroupInviteActivity.this.listAdapter.notifyDataSetChanged();
              }
            }
          });
        }
      });
      ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(i, this.classGuid);
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
      return;
      localObject = new TLRPC.TL_messages_exportChatInvite();
      ((TLRPC.TL_messages_exportChatInvite)localObject).chat_id = this.chat_id;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("InviteLink", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          GroupInviteActivity.this.finishFragment();
        }
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.showProgress();
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 51));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setEmptyView(this.emptyView);
    this.listView.setVerticalScrollBarEnabled(false);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if (GroupInviteActivity.this.getParentActivity() == null) {}
        for (;;)
        {
          return;
          if ((paramAnonymousInt == GroupInviteActivity.this.copyLinkRow) || (paramAnonymousInt == GroupInviteActivity.this.linkRow))
          {
            if (GroupInviteActivity.this.invite != null) {
              try
              {
                ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", GroupInviteActivity.this.invite.link));
                Toast.makeText(GroupInviteActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
              }
              catch (Exception paramAnonymousView)
              {
                FileLog.e(paramAnonymousView);
              }
            }
          }
          else if (paramAnonymousInt == GroupInviteActivity.this.shareLinkRow)
          {
            if (GroupInviteActivity.this.invite != null) {
              try
              {
                paramAnonymousView = new android/content/Intent;
                paramAnonymousView.<init>("android.intent.action.SEND");
                paramAnonymousView.setType("text/plain");
                paramAnonymousView.putExtra("android.intent.extra.TEXT", GroupInviteActivity.this.invite.link);
                GroupInviteActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(paramAnonymousView, LocaleController.getString("InviteToGroupByLink", NUM)), 500);
              }
              catch (Exception paramAnonymousView)
              {
                FileLog.e(paramAnonymousView);
              }
            }
          }
          else if (paramAnonymousInt == GroupInviteActivity.this.revokeLinkRow)
          {
            paramAnonymousView = new AlertDialog.Builder(GroupInviteActivity.this.getParentActivity());
            paramAnonymousView.setMessage(LocaleController.getString("RevokeAlert", NUM));
            paramAnonymousView.setTitle(LocaleController.getString("RevokeLink", NUM));
            paramAnonymousView.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                GroupInviteActivity.this.generateLink(true);
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            GroupInviteActivity.this.showDialog(paramAnonymousView.create());
          }
        }
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.chatInfoDidLoaded)
    {
      TLRPC.ChatFull localChatFull = (TLRPC.ChatFull)paramVarArgs[0];
      paramInt1 = ((Integer)paramVarArgs[1]).intValue();
      if ((localChatFull.id == this.chat_id) && (paramInt1 == this.classGuid))
      {
        this.invite = MessagesController.getInstance(this.currentAccount).getExportedInvite(this.chat_id);
        if ((this.invite instanceof TLRPC.TL_chatInviteExported)) {
          break label79;
        }
        generateLink(false);
      }
    }
    for (;;)
    {
      return;
      label79:
      this.loading = false;
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, TextBlockCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { TextBlockCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText") };
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
    MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
    this.loading = true;
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.linkRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.linkInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.copyLinkRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.revokeLinkRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.shareLinkRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.shadowRow = i;
    return true;
  }
  
  public void onFragmentDestroy()
  {
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
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
      if (GroupInviteActivity.this.loading) {}
      for (int i = 0;; i = GroupInviteActivity.this.rowCount) {
        return i;
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      int j = i;
      if (paramInt != GroupInviteActivity.this.copyLinkRow)
      {
        j = i;
        if (paramInt != GroupInviteActivity.this.shareLinkRow)
        {
          if (paramInt != GroupInviteActivity.this.revokeLinkRow) {
            break label43;
          }
          j = i;
        }
      }
      for (;;)
      {
        return j;
        label43:
        if ((paramInt == GroupInviteActivity.this.shadowRow) || (paramInt == GroupInviteActivity.this.linkInfoRow))
        {
          j = 1;
        }
        else
        {
          j = i;
          if (paramInt == GroupInviteActivity.this.linkRow) {
            j = 2;
          }
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if ((i == GroupInviteActivity.this.revokeLinkRow) || (i == GroupInviteActivity.this.copyLinkRow) || (i == GroupInviteActivity.this.shareLinkRow) || (i == GroupInviteActivity.this.linkRow)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      default: 
      case 0: 
      case 1: 
        do
        {
          for (;;)
          {
            return;
            paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
            if (paramInt == GroupInviteActivity.this.copyLinkRow)
            {
              paramViewHolder.setText(LocaleController.getString("CopyLink", NUM), true);
            }
            else if (paramInt == GroupInviteActivity.this.shareLinkRow)
            {
              paramViewHolder.setText(LocaleController.getString("ShareLink", NUM), false);
            }
            else if (paramInt == GroupInviteActivity.this.revokeLinkRow)
            {
              paramViewHolder.setText(LocaleController.getString("RevokeLink", NUM), true);
              continue;
              localObject = (TextInfoPrivacyCell)paramViewHolder.itemView;
              if (paramInt != GroupInviteActivity.this.shadowRow) {
                break;
              }
              ((TextInfoPrivacyCell)localObject).setText("");
              ((TextInfoPrivacyCell)localObject).setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
          }
        } while (paramInt != GroupInviteActivity.this.linkInfoRow);
        paramViewHolder = MessagesController.getInstance(GroupInviteActivity.this.currentAccount).getChat(Integer.valueOf(GroupInviteActivity.this.chat_id));
        if ((ChatObject.isChannel(paramViewHolder)) && (!paramViewHolder.megagroup)) {
          ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("ChannelLinkInfo", NUM));
        }
        for (;;)
        {
          ((TextInfoPrivacyCell)localObject).setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
          break;
          ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("LinkInfo", NUM));
        }
      }
      Object localObject = (TextBlockCell)paramViewHolder.itemView;
      if (GroupInviteActivity.this.invite != null) {}
      for (paramViewHolder = GroupInviteActivity.this.invite.link;; paramViewHolder = "error")
      {
        ((TextBlockCell)localObject).setText(paramViewHolder, false);
        break;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new TextBlockCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/GroupInviteActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */