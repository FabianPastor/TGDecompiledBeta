package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.MediaActivity;
import org.telegram.ui.ProfileActivity;

public class ChatAvatarContainer
  extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private int currentAccount = UserConfig.selectedAccount;
  private int currentConnectionState;
  private CharSequence lastSubtitle;
  private boolean occupyStatusBar = true;
  private int onlineCount = -1;
  private ChatActivity parentFragment;
  private StatusDrawable[] statusDrawables = new StatusDrawable[5];
  private SimpleTextView subtitleTextView;
  private ImageView timeItem;
  private TimerDrawable timerDrawable;
  private SimpleTextView titleTextView;
  
  public ChatAvatarContainer(Context paramContext, ChatActivity paramChatActivity, boolean paramBoolean)
  {
    super(paramContext);
    this.parentFragment = paramChatActivity;
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0F));
    addView(this.avatarImageView);
    this.titleTextView = new SimpleTextView(paramContext);
    this.titleTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
    this.titleTextView.setTextSize(18);
    this.titleTextView.setGravity(3);
    this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3F));
    addView(this.titleTextView);
    this.subtitleTextView = new SimpleTextView(paramContext);
    this.subtitleTextView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
    this.subtitleTextView.setTextSize(14);
    this.subtitleTextView.setGravity(3);
    addView(this.subtitleTextView);
    if (paramBoolean)
    {
      this.timeItem = new ImageView(paramContext);
      this.timeItem.setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(5.0F), AndroidUtilities.dp(5.0F));
      this.timeItem.setScaleType(ImageView.ScaleType.CENTER);
      paramChatActivity = this.timeItem;
      paramContext = new TimerDrawable(paramContext);
      this.timerDrawable = paramContext;
      paramChatActivity.setImageDrawable(paramContext);
      addView(this.timeItem);
      this.timeItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ChatAvatarContainer.this.parentFragment.showDialog(AlertsCreator.createTTLAlert(ChatAvatarContainer.this.getContext(), ChatAvatarContainer.this.parentFragment.getCurrentEncryptedChat()).create());
        }
      });
    }
    if (this.parentFragment != null)
    {
      setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Object localObject = ChatAvatarContainer.this.parentFragment.getCurrentUser();
          paramAnonymousView = ChatAvatarContainer.this.parentFragment.getCurrentChat();
          if (localObject != null)
          {
            paramAnonymousView = new Bundle();
            if (UserObject.isUserSelf((TLRPC.User)localObject))
            {
              paramAnonymousView.putLong("dialog_id", ChatAvatarContainer.this.parentFragment.getDialogId());
              paramAnonymousView = new MediaActivity(paramAnonymousView);
              paramAnonymousView.setChatInfo(ChatAvatarContainer.this.parentFragment.getCurrentChatInfo());
              ChatAvatarContainer.this.parentFragment.presentFragment(paramAnonymousView);
            }
          }
          for (;;)
          {
            return;
            paramAnonymousView.putInt("user_id", ((TLRPC.User)localObject).id);
            if (ChatAvatarContainer.this.timeItem != null) {
              paramAnonymousView.putLong("dialog_id", ChatAvatarContainer.this.parentFragment.getDialogId());
            }
            paramAnonymousView = new ProfileActivity(paramAnonymousView);
            paramAnonymousView.setPlayProfileAnimation(true);
            ChatAvatarContainer.this.parentFragment.presentFragment(paramAnonymousView);
            continue;
            if (paramAnonymousView != null)
            {
              localObject = new Bundle();
              ((Bundle)localObject).putInt("chat_id", paramAnonymousView.id);
              paramAnonymousView = new ProfileActivity((Bundle)localObject);
              paramAnonymousView.setChatInfo(ChatAvatarContainer.this.parentFragment.getCurrentChatInfo());
              paramAnonymousView.setPlayProfileAnimation(true);
              ChatAvatarContainer.this.parentFragment.presentFragment(paramAnonymousView);
            }
          }
        }
      });
      paramContext = this.parentFragment.getCurrentChat();
      this.statusDrawables[0] = new TypingDotsDrawable();
      this.statusDrawables[1] = new RecordStatusDrawable();
      this.statusDrawables[2] = new SendingFileDrawable();
      this.statusDrawables[3] = new PlayingGameDrawable();
      this.statusDrawables[4] = new RoundStatusDrawable();
      int i = 0;
      if (i < this.statusDrawables.length)
      {
        paramChatActivity = this.statusDrawables[i];
        if (paramContext != null) {}
        for (paramBoolean = true;; paramBoolean = false)
        {
          paramChatActivity.setIsChat(paramBoolean);
          i++;
          break;
        }
      }
    }
  }
  
  private void setTypingAnimation(boolean paramBoolean)
  {
    int i;
    if (paramBoolean) {
      try
      {
        Integer localInteger = (Integer)MessagesController.getInstance(this.currentAccount).printingStringsTypes.get(this.parentFragment.getDialogId());
        this.subtitleTextView.setLeftDrawable(this.statusDrawables[localInteger.intValue()]);
        i = 0;
        if (i < this.statusDrawables.length)
        {
          if (i == localInteger.intValue()) {
            this.statusDrawables[i].start();
          }
          for (;;)
          {
            i++;
            break;
            this.statusDrawables[i].stop();
          }
        }
        return;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }
    for (;;)
    {
      this.subtitleTextView.setLeftDrawable(null);
      for (i = 0; i < this.statusDrawables.length; i++) {
        this.statusDrawables[i].stop();
      }
    }
  }
  
  private void updateCurrentConnectionState()
  {
    String str = null;
    if (this.currentConnectionState == 2)
    {
      str = LocaleController.getString("WaitingForNetwork", NUM);
      if (str != null) {
        break label108;
      }
      if (this.lastSubtitle != null)
      {
        this.subtitleTextView.setText(this.lastSubtitle);
        this.lastSubtitle = null;
      }
    }
    for (;;)
    {
      return;
      if (this.currentConnectionState == 1)
      {
        str = LocaleController.getString("Connecting", NUM);
        break;
      }
      if (this.currentConnectionState == 5)
      {
        str = LocaleController.getString("Updating", NUM);
        break;
      }
      if (this.currentConnectionState != 4) {
        break;
      }
      str = LocaleController.getString("ConnectingToProxy", NUM);
      break;
      label108:
      this.lastSubtitle = this.subtitleTextView.getText();
      this.subtitleTextView.setText(str);
    }
  }
  
  public void checkAndUpdateAvatar()
  {
    if (this.parentFragment == null) {}
    label133:
    for (;;)
    {
      return;
      Object localObject1 = null;
      Object localObject2 = null;
      TLRPC.User localUser = this.parentFragment.getCurrentUser();
      TLRPC.Chat localChat = this.parentFragment.getCurrentChat();
      if (localUser != null)
      {
        this.avatarDrawable.setInfo(localUser);
        if (UserObject.isUserSelf(localUser)) {
          this.avatarDrawable.setSavedMessages(2);
        }
      }
      for (;;)
      {
        if (this.avatarImageView == null) {
          break label133;
        }
        this.avatarImageView.setImage((TLObject)localObject2, "50_50", this.avatarDrawable);
        break;
        if (localUser.photo != null)
        {
          localObject2 = localUser.photo.photo_small;
          continue;
          if (localChat != null)
          {
            localObject2 = localObject1;
            if (localChat.photo != null) {
              localObject2 = localChat.photo.photo_small;
            }
            this.avatarDrawable.setInfo(localChat);
          }
        }
      }
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.didUpdatedConnectionState)
    {
      paramInt1 = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
      if (this.currentConnectionState != paramInt1)
      {
        this.currentConnectionState = paramInt1;
        updateCurrentConnectionState();
      }
    }
  }
  
  public SimpleTextView getSubtitleTextView()
  {
    return this.subtitleTextView;
  }
  
  public ImageView getTimeItem()
  {
    return this.timeItem;
  }
  
  public SimpleTextView getTitleTextView()
  {
    return this.titleTextView;
  }
  
  public void hideTimeItem()
  {
    if (this.timeItem == null) {}
    for (;;)
    {
      return;
      this.timeItem.setVisibility(8);
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.parentFragment != null)
    {
      NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatedConnectionState);
      this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
      updateCurrentConnectionState();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.parentFragment != null) {
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatedConnectionState);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(42.0F)) / 2;
    if ((Build.VERSION.SDK_INT >= 21) && (this.occupyStatusBar))
    {
      paramInt1 = AndroidUtilities.statusBarHeight;
      paramInt1 = paramInt2 + paramInt1;
      this.avatarImageView.layout(AndroidUtilities.dp(8.0F), paramInt1, AndroidUtilities.dp(50.0F), AndroidUtilities.dp(42.0F) + paramInt1);
      if (this.subtitleTextView.getVisibility() != 0) {
        break label222;
      }
      this.titleTextView.layout(AndroidUtilities.dp(62.0F), AndroidUtilities.dp(1.3F) + paramInt1, AndroidUtilities.dp(62.0F) + this.titleTextView.getMeasuredWidth(), this.titleTextView.getTextHeight() + paramInt1 + AndroidUtilities.dp(1.3F));
    }
    for (;;)
    {
      if (this.timeItem != null) {
        this.timeItem.layout(AndroidUtilities.dp(24.0F), AndroidUtilities.dp(15.0F) + paramInt1, AndroidUtilities.dp(58.0F), AndroidUtilities.dp(49.0F) + paramInt1);
      }
      this.subtitleTextView.layout(AndroidUtilities.dp(62.0F), AndroidUtilities.dp(24.0F) + paramInt1, AndroidUtilities.dp(62.0F) + this.subtitleTextView.getMeasuredWidth(), this.subtitleTextView.getTextHeight() + paramInt1 + AndroidUtilities.dp(24.0F));
      return;
      paramInt1 = 0;
      break;
      label222:
      this.titleTextView.layout(AndroidUtilities.dp(62.0F), AndroidUtilities.dp(11.0F) + paramInt1, AndroidUtilities.dp(62.0F) + this.titleTextView.getMeasuredWidth(), this.titleTextView.getTextHeight() + paramInt1 + AndroidUtilities.dp(11.0F));
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    int i = paramInt1 - AndroidUtilities.dp(70.0F);
    this.avatarImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0F), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0F), NUM));
    this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), Integer.MIN_VALUE));
    this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), Integer.MIN_VALUE));
    if (this.timeItem != null) {
      this.timeItem.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0F), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0F), NUM));
    }
    setMeasuredDimension(paramInt1, View.MeasureSpec.getSize(paramInt2));
  }
  
  public void setChatAvatar(TLRPC.Chat paramChat)
  {
    TLRPC.FileLocation localFileLocation = null;
    if (paramChat.photo != null) {
      localFileLocation = paramChat.photo.photo_small;
    }
    this.avatarDrawable.setInfo(paramChat);
    if (this.avatarImageView != null) {
      this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
    }
  }
  
  public void setOccupyStatusBar(boolean paramBoolean)
  {
    this.occupyStatusBar = paramBoolean;
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    if (this.lastSubtitle == null) {
      this.subtitleTextView.setText(paramCharSequence);
    }
    for (;;)
    {
      return;
      this.lastSubtitle = paramCharSequence;
    }
  }
  
  public void setTime(int paramInt)
  {
    if (this.timerDrawable == null) {}
    for (;;)
    {
      return;
      this.timerDrawable.setTime(paramInt);
    }
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.titleTextView.setText(paramCharSequence);
  }
  
  public void setTitleColors(int paramInt1, int paramInt2)
  {
    this.titleTextView.setTextColor(paramInt1);
    this.subtitleTextView.setTextColor(paramInt1);
  }
  
  public void setTitleIcons(int paramInt1, int paramInt2)
  {
    this.titleTextView.setLeftDrawable(paramInt1);
    this.titleTextView.setRightDrawable(paramInt2);
  }
  
  public void setTitleIcons(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    this.titleTextView.setLeftDrawable(paramDrawable1);
    this.titleTextView.setRightDrawable(paramDrawable2);
  }
  
  public void setUserAvatar(TLRPC.User paramUser)
  {
    Object localObject = null;
    this.avatarDrawable.setInfo(paramUser);
    if (UserObject.isUserSelf(paramUser)) {
      this.avatarDrawable.setSavedMessages(2);
    }
    for (;;)
    {
      if (this.avatarImageView != null) {
        this.avatarImageView.setImage((TLObject)localObject, "50_50", this.avatarDrawable);
      }
      return;
      if (paramUser.photo != null) {
        localObject = paramUser.photo.photo_small;
      }
    }
  }
  
  public void showTimeItem()
  {
    if (this.timeItem == null) {}
    for (;;)
    {
      return;
      this.timeItem.setVisibility(0);
    }
  }
  
  public void updateOnlineCount()
  {
    if (this.parentFragment == null) {}
    for (;;)
    {
      return;
      this.onlineCount = 0;
      TLRPC.ChatFull localChatFull = this.parentFragment.getCurrentChatInfo();
      if (localChatFull != null)
      {
        int i = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (((localChatFull instanceof TLRPC.TL_chatFull)) || (((localChatFull instanceof TLRPC.TL_channelFull)) && (localChatFull.participants_count <= 200) && (localChatFull.participants != null))) {
          for (int j = 0; j < localChatFull.participants.participants.size(); j++)
          {
            Object localObject = (TLRPC.ChatParticipant)localChatFull.participants.participants.get(j);
            localObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id));
            if ((localObject != null) && (((TLRPC.User)localObject).status != null) && ((((TLRPC.User)localObject).status.expires > i) || (((TLRPC.User)localObject).id == UserConfig.getInstance(this.currentAccount).getClientUserId())) && (((TLRPC.User)localObject).status.expires > 10000)) {
              this.onlineCount += 1;
            }
          }
        }
      }
    }
  }
  
  public void updateSubtitle()
  {
    if (this.parentFragment == null) {}
    for (;;)
    {
      return;
      Object localObject1 = this.parentFragment.getCurrentUser();
      if (UserObject.isUserSelf((TLRPC.User)localObject1))
      {
        if (this.subtitleTextView.getVisibility() != 8) {
          this.subtitleTextView.setVisibility(8);
        }
      }
      else
      {
        TLRPC.Chat localChat = this.parentFragment.getCurrentChat();
        Object localObject2 = (CharSequence)MessagesController.getInstance(this.currentAccount).printingStrings.get(this.parentFragment.getDialogId());
        Object localObject3 = localObject2;
        if (localObject2 != null) {
          localObject3 = TextUtils.replace((CharSequence)localObject2, new String[] { "..." }, new String[] { "" });
        }
        if ((localObject3 == null) || (((CharSequence)localObject3).length() == 0) || ((ChatObject.isChannel(localChat)) && (!localChat.megagroup)))
        {
          setTypingAnimation(false);
          if (localChat != null)
          {
            localObject1 = this.parentFragment.getCurrentChatInfo();
            if (ChatObject.isChannel(localChat)) {
              if ((localObject1 != null) && (((TLRPC.ChatFull)localObject1).participants_count != 0)) {
                if ((localChat.megagroup) && (((TLRPC.ChatFull)localObject1).participants_count <= 200)) {
                  if ((this.onlineCount > 1) && (((TLRPC.ChatFull)localObject1).participants_count != 0)) {
                    localObject3 = String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", ((TLRPC.ChatFull)localObject1).participants_count), LocaleController.formatPluralString("OnlineCount", this.onlineCount) });
                  }
                }
              }
            }
          }
        }
        for (;;)
        {
          if (this.lastSubtitle != null) {
            break label744;
          }
          this.subtitleTextView.setText((CharSequence)localObject3);
          break;
          localObject3 = LocaleController.formatPluralString("Members", ((TLRPC.ChatFull)localObject1).participants_count);
          continue;
          localObject3 = new int[1];
          localObject1 = LocaleController.formatShortNumber(((TLRPC.ChatFull)localObject1).participants_count, (int[])localObject3);
          if (localChat.megagroup)
          {
            localObject3 = LocaleController.formatPluralString("Members", localObject3[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject3[0]) }), (CharSequence)localObject1);
          }
          else
          {
            localObject3 = LocaleController.formatPluralString("Subscribers", localObject3[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject3[0]) }), (CharSequence)localObject1);
            continue;
            if (localChat.megagroup)
            {
              localObject3 = LocaleController.getString("Loading", NUM).toLowerCase();
            }
            else if ((localChat.flags & 0x40) != 0)
            {
              localObject3 = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
            }
            else
            {
              localObject3 = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
              continue;
              if (ChatObject.isKickedFromChat(localChat))
              {
                localObject3 = LocaleController.getString("YouWereKicked", NUM);
              }
              else if (ChatObject.isLeftFromChat(localChat))
              {
                localObject3 = LocaleController.getString("YouLeft", NUM);
              }
              else
              {
                int i = localChat.participants_count;
                int j = i;
                if (localObject1 != null)
                {
                  j = i;
                  if (((TLRPC.ChatFull)localObject1).participants != null) {
                    j = ((TLRPC.ChatFull)localObject1).participants.participants.size();
                  }
                }
                if ((this.onlineCount > 1) && (j != 0))
                {
                  localObject3 = String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", j), LocaleController.formatPluralString("OnlineCount", this.onlineCount) });
                }
                else
                {
                  localObject3 = LocaleController.formatPluralString("Members", j);
                  continue;
                  if (localObject1 != null)
                  {
                    localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.User)localObject1).id));
                    localObject3 = localObject1;
                    if (localObject2 != null) {
                      localObject3 = localObject2;
                    }
                    if (((TLRPC.User)localObject3).id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                      localObject3 = LocaleController.getString("ChatYourSelf", NUM);
                    }
                    for (;;)
                    {
                      break;
                      if ((((TLRPC.User)localObject3).id == 333000) || (((TLRPC.User)localObject3).id == 777000)) {
                        localObject3 = LocaleController.getString("ServiceNotifications", NUM);
                      } else if (((TLRPC.User)localObject3).bot) {
                        localObject3 = LocaleController.getString("Bot", NUM);
                      } else {
                        localObject3 = LocaleController.formatUserStatus(this.currentAccount, (TLRPC.User)localObject3);
                      }
                    }
                  }
                  localObject3 = "";
                  continue;
                  setTypingAnimation(true);
                }
              }
            }
          }
        }
        label744:
        this.lastSubtitle = ((CharSequence)localObject3);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ChatAvatarContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */