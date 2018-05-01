package org.telegram.ui.Components;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.JoinSheetUserCell;
import org.telegram.ui.ChatActivity;

public class JoinGroupAlert
  extends BottomSheet
{
  private TLRPC.ChatInvite chatInvite;
  private BaseFragment fragment;
  private String hash;
  
  public JoinGroupAlert(Context paramContext, TLRPC.ChatInvite paramChatInvite, String paramString, BaseFragment paramBaseFragment)
  {
    super(paramContext, false);
    setApplyBottomPadding(false);
    setApplyTopPadding(false);
    this.fragment = paramBaseFragment;
    this.chatInvite = paramChatInvite;
    this.hash = paramString;
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    localLinearLayout.setOrientation(1);
    localLinearLayout.setClickable(true);
    setCustomView(localLinearLayout);
    paramBaseFragment = null;
    paramString = null;
    AvatarDrawable localAvatarDrawable;
    int i;
    if (paramChatInvite.chat != null)
    {
      localAvatarDrawable = new AvatarDrawable(paramChatInvite.chat);
      if (this.chatInvite.chat.photo != null) {
        paramString = this.chatInvite.chat.photo.photo_small;
      }
      paramBaseFragment = paramChatInvite.chat.title;
      i = paramChatInvite.chat.participants_count;
      BackupImageView localBackupImageView = new BackupImageView(paramContext);
      localBackupImageView.setRoundRadius(AndroidUtilities.dp(35.0F));
      localBackupImageView.setImage(paramString, "50_50", localAvatarDrawable);
      localLinearLayout.addView(localBackupImageView, LayoutHelper.createLinear(70, 70, 49, 0, 12, 0, 0));
      paramString = new TextView(paramContext);
      paramString.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString.setTextSize(1, 17.0F);
      paramString.setTextColor(Theme.getColor("dialogTextBlack"));
      paramString.setText(paramBaseFragment);
      paramString.setSingleLine(true);
      paramString.setEllipsize(TextUtils.TruncateAt.END);
      if (i <= 0) {
        break label701;
      }
    }
    label701:
    for (int j = 0;; j = 10)
    {
      localLinearLayout.addView(paramString, LayoutHelper.createLinear(-2, -2, 49, 10, 10, 10, j));
      if (i > 0)
      {
        paramString = new TextView(paramContext);
        paramString.setTextSize(1, 14.0F);
        paramString.setTextColor(Theme.getColor("dialogTextGray3"));
        paramString.setSingleLine(true);
        paramString.setEllipsize(TextUtils.TruncateAt.END);
        paramString.setText(LocaleController.formatPluralString("Members", i));
        localLinearLayout.addView(paramString, LayoutHelper.createLinear(-2, -2, 49, 10, 4, 10, 10));
      }
      if (!paramChatInvite.participants.isEmpty())
      {
        paramChatInvite = new RecyclerListView(paramContext);
        paramChatInvite.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
        paramChatInvite.setNestedScrollingEnabled(false);
        paramChatInvite.setClipToPadding(false);
        paramChatInvite.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        paramChatInvite.setHorizontalScrollBarEnabled(false);
        paramChatInvite.setVerticalScrollBarEnabled(false);
        paramChatInvite.setAdapter(new UsersAdapter(paramContext));
        paramChatInvite.setGlowColor(Theme.getColor("dialogScrollGlow"));
        localLinearLayout.addView(paramChatInvite, LayoutHelper.createLinear(-2, 90, 49, 0, 0, 0, 0));
      }
      paramChatInvite = new View(paramContext);
      paramChatInvite.setBackgroundResource(NUM);
      localLinearLayout.addView(paramChatInvite, LayoutHelper.createLinear(-1, 3));
      paramContext = new PickerBottomLayout(paramContext, false);
      localLinearLayout.addView(paramContext, LayoutHelper.createFrame(-1, 48, 83));
      paramContext.cancelButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramContext.cancelButton.setTextColor(Theme.getColor("dialogTextBlue2"));
      paramContext.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
      paramContext.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          JoinGroupAlert.this.dismiss();
        }
      });
      paramContext.doneButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramContext.doneButton.setVisibility(0);
      paramContext.doneButtonBadgeTextView.setVisibility(8);
      paramContext.doneButtonTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
      paramContext.doneButtonTextView.setText(LocaleController.getString("JoinGroup", NUM));
      paramContext.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(final View paramAnonymousView)
        {
          JoinGroupAlert.this.dismiss();
          paramAnonymousView = new TLRPC.TL_messages_importChatInvite();
          paramAnonymousView.hash = JoinGroupAlert.this.hash;
          ConnectionsManager.getInstance(JoinGroupAlert.this.currentAccount).sendRequest(paramAnonymousView, new RequestDelegate()
          {
            public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
            {
              if (paramAnonymous2TL_error == null)
              {
                TLRPC.Updates localUpdates = (TLRPC.Updates)paramAnonymous2TLObject;
                MessagesController.getInstance(JoinGroupAlert.this.currentAccount).processUpdates(localUpdates, false);
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if ((JoinGroupAlert.this.fragment == null) || (JoinGroupAlert.this.fragment.getParentActivity() == null)) {}
                  for (;;)
                  {
                    return;
                    if (paramAnonymous2TL_error == null)
                    {
                      Object localObject1 = (TLRPC.Updates)paramAnonymous2TLObject;
                      if (!((TLRPC.Updates)localObject1).chats.isEmpty())
                      {
                        Object localObject2 = (TLRPC.Chat)((TLRPC.Updates)localObject1).chats.get(0);
                        ((TLRPC.Chat)localObject2).left = false;
                        ((TLRPC.Chat)localObject2).kicked = false;
                        MessagesController.getInstance(JoinGroupAlert.this.currentAccount).putUsers(((TLRPC.Updates)localObject1).users, false);
                        MessagesController.getInstance(JoinGroupAlert.this.currentAccount).putChats(((TLRPC.Updates)localObject1).chats, false);
                        localObject1 = new Bundle();
                        ((Bundle)localObject1).putInt("chat_id", ((TLRPC.Chat)localObject2).id);
                        if (MessagesController.getInstance(JoinGroupAlert.this.currentAccount).checkCanOpenChat((Bundle)localObject1, JoinGroupAlert.this.fragment))
                        {
                          localObject2 = new ChatActivity((Bundle)localObject1);
                          JoinGroupAlert.this.fragment.presentFragment((BaseFragment)localObject2, JoinGroupAlert.this.fragment instanceof ChatActivity);
                        }
                      }
                    }
                    else
                    {
                      AlertsCreator.processError(JoinGroupAlert.this.currentAccount, paramAnonymous2TL_error, JoinGroupAlert.this.fragment, JoinGroupAlert.2.1.this.val$req, new Object[0]);
                    }
                  }
                }
              });
            }
          }, 2);
        }
      });
      return;
      localAvatarDrawable = new AvatarDrawable();
      localAvatarDrawable.setInfo(0, paramChatInvite.title, null, false);
      paramString = paramBaseFragment;
      if (this.chatInvite.photo != null) {
        paramString = this.chatInvite.photo.photo_small;
      }
      paramBaseFragment = paramChatInvite.title;
      i = paramChatInvite.participants_count;
      break;
    }
  }
  
  private class UsersAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context context;
    
    public UsersAdapter(Context paramContext)
    {
      this.context = paramContext;
    }
    
    public int getItemCount()
    {
      int i = JoinGroupAlert.this.chatInvite.participants.size();
      if (JoinGroupAlert.this.chatInvite.chat != null) {}
      for (int j = JoinGroupAlert.this.chatInvite.chat.participants_count;; j = JoinGroupAlert.this.chatInvite.participants_count)
      {
        int k = i;
        if (i != j) {
          k = i + 1;
        }
        return k;
      }
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder = (JoinSheetUserCell)paramViewHolder.itemView;
      if (paramInt < JoinGroupAlert.this.chatInvite.participants.size())
      {
        paramViewHolder.setUser((TLRPC.User)JoinGroupAlert.this.chatInvite.participants.get(paramInt));
        return;
      }
      if (JoinGroupAlert.this.chatInvite.chat != null) {}
      for (paramInt = JoinGroupAlert.this.chatInvite.chat.participants_count;; paramInt = JoinGroupAlert.this.chatInvite.participants_count)
      {
        paramViewHolder.setCount(paramInt - JoinGroupAlert.this.chatInvite.participants.size());
        break;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new JoinSheetUserCell(this.context);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(100.0F), AndroidUtilities.dp(90.0F)));
      return new RecyclerListView.Holder(paramViewGroup);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/JoinGroupAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */