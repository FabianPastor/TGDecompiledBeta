package org.telegram.ui.Components;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ProfileActivity;

public class ChatAvatarContainer
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private int onlineCount = -1;
  private ChatActivity parentFragment;
  private RecordStatusDrawable recordStatusDrawable;
  private SendingFileExDrawable sendingFileDrawable;
  private SimpleTextView subtitleTextView;
  private ImageView timeItem;
  private TimerDrawable timerDrawable;
  private SimpleTextView titleTextView;
  private TypingDotsDrawable typingDotsDrawable;
  
  public ChatAvatarContainer(Context paramContext, ChatActivity paramChatActivity, boolean paramBoolean)
  {
    super(paramContext);
    this.parentFragment = paramChatActivity;
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0F));
    addView(this.avatarImageView);
    this.titleTextView = new SimpleTextView(paramContext);
    this.titleTextView.setTextColor(-1);
    this.titleTextView.setTextSize(18);
    this.titleTextView.setGravity(3);
    this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3F));
    this.titleTextView.setRightDrawableTopPadding(-AndroidUtilities.dp(1.3F));
    addView(this.titleTextView);
    this.subtitleTextView = new SimpleTextView(paramContext);
    this.subtitleTextView.setTextColor(-2758409);
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
          ChatAvatarContainer.this.parentFragment.showDialog(AndroidUtilities.buildTTLAlert(ChatAvatarContainer.this.getContext(), ChatAvatarContainer.this.parentFragment.getCurrentEncryptedChat()).create());
        }
      });
    }
    setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = ChatAvatarContainer.this.parentFragment.getCurrentUser();
        Object localObject = ChatAvatarContainer.this.parentFragment.getCurrentChat();
        if (paramAnonymousView != null)
        {
          localObject = new Bundle();
          ((Bundle)localObject).putInt("user_id", paramAnonymousView.id);
          if (ChatAvatarContainer.this.timeItem != null) {
            ((Bundle)localObject).putLong("dialog_id", ChatAvatarContainer.this.parentFragment.getDialogId());
          }
          paramAnonymousView = new ProfileActivity((Bundle)localObject);
          paramAnonymousView.setPlayProfileAnimation(true);
          ChatAvatarContainer.this.parentFragment.presentFragment(paramAnonymousView);
        }
        while (localObject == null) {
          return;
        }
        paramAnonymousView = new Bundle();
        paramAnonymousView.putInt("chat_id", ((TLRPC.Chat)localObject).id);
        paramAnonymousView = new ProfileActivity(paramAnonymousView);
        paramAnonymousView.setChatInfo(ChatAvatarContainer.this.parentFragment.getCurrentChatInfo());
        paramAnonymousView.setPlayProfileAnimation(true);
        ChatAvatarContainer.this.parentFragment.presentFragment(paramAnonymousView);
      }
    });
    paramContext = this.parentFragment.getCurrentChat();
    this.typingDotsDrawable = new TypingDotsDrawable();
    paramChatActivity = this.typingDotsDrawable;
    if (paramContext != null)
    {
      paramBoolean = true;
      paramChatActivity.setIsChat(paramBoolean);
      this.recordStatusDrawable = new RecordStatusDrawable();
      paramChatActivity = this.recordStatusDrawable;
      if (paramContext == null) {
        break label398;
      }
      paramBoolean = true;
      label359:
      paramChatActivity.setIsChat(paramBoolean);
      this.sendingFileDrawable = new SendingFileExDrawable();
      paramChatActivity = this.sendingFileDrawable;
      if (paramContext == null) {
        break label403;
      }
    }
    label398:
    label403:
    for (paramBoolean = bool;; paramBoolean = false)
    {
      paramChatActivity.setIsChat(paramBoolean);
      return;
      paramBoolean = false;
      break;
      paramBoolean = false;
      break label359;
    }
  }
  
  private void setTypingAnimation(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      try
      {
        Integer localInteger = (Integer)MessagesController.getInstance().printingStringsTypes.get(Long.valueOf(this.parentFragment.getDialogId()));
        if (localInteger.intValue() == 0)
        {
          this.subtitleTextView.setLeftDrawable(this.typingDotsDrawable);
          this.typingDotsDrawable.start();
          this.recordStatusDrawable.stop();
          this.sendingFileDrawable.stop();
          return;
        }
        if (localInteger.intValue() == 1)
        {
          this.subtitleTextView.setLeftDrawable(this.recordStatusDrawable);
          this.recordStatusDrawable.start();
          this.typingDotsDrawable.stop();
          this.sendingFileDrawable.stop();
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
        return;
      }
      if (localException.intValue() == 2)
      {
        this.subtitleTextView.setLeftDrawable(this.sendingFileDrawable);
        this.sendingFileDrawable.start();
        this.typingDotsDrawable.stop();
        this.recordStatusDrawable.stop();
      }
    }
    else
    {
      this.subtitleTextView.setLeftDrawable(null);
      this.typingDotsDrawable.stop();
      this.recordStatusDrawable.stop();
      this.sendingFileDrawable.stop();
    }
  }
  
  public void checkAndUpdateAvatar()
  {
    Object localObject3 = null;
    Object localObject2 = null;
    Object localObject1 = null;
    TLRPC.User localUser = this.parentFragment.getCurrentUser();
    TLRPC.Chat localChat = this.parentFragment.getCurrentChat();
    if (localUser != null)
    {
      if (localUser.photo != null) {
        localObject1 = localUser.photo.photo_small;
      }
      this.avatarDrawable.setInfo(localUser);
    }
    for (;;)
    {
      if (this.avatarImageView != null) {
        this.avatarImageView.setImage((TLObject)localObject1, "50_50", this.avatarDrawable);
      }
      return;
      localObject1 = localObject3;
      if (localChat != null)
      {
        localObject1 = localObject2;
        if (localChat.photo != null) {
          localObject1 = localChat.photo.photo_small;
        }
        this.avatarDrawable.setInfo(localChat);
      }
    }
  }
  
  public void hideTimeItem()
  {
    if (this.timeItem == null) {
      return;
    }
    this.timeItem.setVisibility(8);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(42.0F)) / 2;
    if (Build.VERSION.SDK_INT >= 21) {}
    for (paramInt1 = AndroidUtilities.statusBarHeight;; paramInt1 = 0)
    {
      paramInt1 = paramInt2 + paramInt1;
      this.avatarImageView.layout(AndroidUtilities.dp(8.0F), paramInt1, AndroidUtilities.dp(50.0F), AndroidUtilities.dp(42.0F) + paramInt1);
      this.titleTextView.layout(AndroidUtilities.dp(62.0F), AndroidUtilities.dp(1.3F) + paramInt1, AndroidUtilities.dp(62.0F) + this.titleTextView.getMeasuredWidth(), this.titleTextView.getTextHeight() + paramInt1 + AndroidUtilities.dp(1.3F));
      if (this.timeItem != null) {
        this.timeItem.layout(AndroidUtilities.dp(24.0F), AndroidUtilities.dp(15.0F) + paramInt1, AndroidUtilities.dp(58.0F), AndroidUtilities.dp(49.0F) + paramInt1);
      }
      this.subtitleTextView.layout(AndroidUtilities.dp(62.0F), AndroidUtilities.dp(24.0F) + paramInt1, AndroidUtilities.dp(62.0F) + this.subtitleTextView.getMeasuredWidth(), this.subtitleTextView.getTextHeight() + paramInt1 + AndroidUtilities.dp(24.0F));
      return;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    int i = paramInt1 - AndroidUtilities.dp(70.0F);
    this.avatarImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0F), 1073741824));
    this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), Integer.MIN_VALUE));
    this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), Integer.MIN_VALUE));
    if (this.timeItem != null) {
      this.timeItem.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0F), 1073741824));
    }
    setMeasuredDimension(paramInt1, View.MeasureSpec.getSize(paramInt2));
  }
  
  public void setTime(int paramInt)
  {
    if (this.timerDrawable == null) {
      return;
    }
    this.timerDrawable.setTime(paramInt);
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.titleTextView.setText(paramCharSequence);
  }
  
  public void setTitleIcons(int paramInt1, int paramInt2)
  {
    this.titleTextView.setLeftDrawable(paramInt1);
    this.titleTextView.setRightDrawable(paramInt2);
  }
  
  public void showTimeItem()
  {
    if (this.timeItem == null) {
      return;
    }
    this.timeItem.setVisibility(0);
  }
  
  public void updateOnlineCount()
  {
    this.onlineCount = 0;
    TLRPC.ChatFull localChatFull = this.parentFragment.getCurrentChatInfo();
    if (localChatFull == null) {}
    for (;;)
    {
      return;
      int j = ConnectionsManager.getInstance().getCurrentTime();
      if (((localChatFull instanceof TLRPC.TL_chatFull)) || (((localChatFull instanceof TLRPC.TL_channelFull)) && (localChatFull.participants_count <= 200) && (localChatFull.participants != null)))
      {
        int i = 0;
        while (i < localChatFull.participants.participants.size())
        {
          Object localObject = (TLRPC.ChatParticipant)localChatFull.participants.participants.get(i);
          localObject = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id));
          if ((localObject != null) && (((TLRPC.User)localObject).status != null) && ((((TLRPC.User)localObject).status.expires > j) || (((TLRPC.User)localObject).id == UserConfig.getClientUserId())) && (((TLRPC.User)localObject).status.expires > 10000)) {
            this.onlineCount += 1;
          }
          i += 1;
        }
      }
    }
  }
  
  public void updateSubtitle()
  {
    TLRPC.User localUser = this.parentFragment.getCurrentUser();
    TLRPC.Chat localChat = this.parentFragment.getCurrentChat();
    Object localObject2 = (CharSequence)MessagesController.getInstance().printingStrings.get(Long.valueOf(this.parentFragment.getDialogId()));
    Object localObject1 = localObject2;
    if (localObject2 != null) {
      localObject1 = TextUtils.replace((CharSequence)localObject2, new String[] { "..." }, new String[] { "" });
    }
    if ((localObject1 == null) || (((CharSequence)localObject1).length() == 0) || ((ChatObject.isChannel(localChat)) && (!localChat.megagroup)))
    {
      setTypingAnimation(false);
      if (localChat != null)
      {
        localObject1 = this.parentFragment.getCurrentChatInfo();
        if (ChatObject.isChannel(localChat)) {
          if ((localObject1 != null) && (((TLRPC.ChatFull)localObject1).participants_count != 0)) {
            if ((localChat.megagroup) && (((TLRPC.ChatFull)localObject1).participants_count <= 200)) {
              if ((this.onlineCount > 1) && (((TLRPC.ChatFull)localObject1).participants_count != 0)) {
                this.subtitleTextView.setText(String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", ((TLRPC.ChatFull)localObject1).participants_count), LocaleController.formatPluralString("Online", this.onlineCount) }));
              }
            }
          }
        }
      }
      while (localUser == null)
      {
        return;
        this.subtitleTextView.setText(LocaleController.formatPluralString("Members", ((TLRPC.ChatFull)localObject1).participants_count));
        return;
        localObject2 = new int[1];
        localObject1 = LocaleController.formatShortNumber(((TLRPC.ChatFull)localObject1).participants_count, (int[])localObject2);
        localObject1 = LocaleController.formatPluralString("Members", localObject2[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject2[0]) }), (CharSequence)localObject1);
        this.subtitleTextView.setText((CharSequence)localObject1);
        return;
        if (localChat.megagroup)
        {
          this.subtitleTextView.setText(LocaleController.getString("Loading", 2131165834).toLowerCase());
          return;
        }
        if ((localChat.flags & 0x40) != 0)
        {
          this.subtitleTextView.setText(LocaleController.getString("ChannelPublic", 2131165468).toLowerCase());
          return;
        }
        this.subtitleTextView.setText(LocaleController.getString("ChannelPrivate", 2131165465).toLowerCase());
        return;
        if (ChatObject.isKickedFromChat(localChat))
        {
          this.subtitleTextView.setText(LocaleController.getString("YouWereKicked", 2131166419));
          return;
        }
        if (ChatObject.isLeftFromChat(localChat))
        {
          this.subtitleTextView.setText(LocaleController.getString("YouLeft", 2131166418));
          return;
        }
        int j = localChat.participants_count;
        int i = j;
        if (localObject1 != null)
        {
          i = j;
          if (((TLRPC.ChatFull)localObject1).participants != null) {
            i = ((TLRPC.ChatFull)localObject1).participants.participants.size();
          }
        }
        if ((this.onlineCount > 1) && (i != 0))
        {
          this.subtitleTextView.setText(String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", i), LocaleController.formatPluralString("Online", this.onlineCount) }));
          return;
        }
        this.subtitleTextView.setText(LocaleController.formatPluralString("Members", i));
        return;
      }
      localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(localUser.id));
      if (((TLRPC.User)localObject1).id == UserConfig.getClientUserId()) {
        localObject1 = LocaleController.getString("ChatYourSelf", 2131165498);
      }
      for (;;)
      {
        this.subtitleTextView.setText((CharSequence)localObject1);
        return;
        if ((((TLRPC.User)localObject1).id == 333000) || (((TLRPC.User)localObject1).id == 777000)) {
          localObject1 = LocaleController.getString("ServiceNotifications", 2131166256);
        } else if (((TLRPC.User)localObject1).bot) {
          localObject1 = LocaleController.getString("Bot", 2131165365);
        } else {
          localObject1 = LocaleController.formatUserStatus((TLRPC.User)localObject1);
        }
      }
    }
    this.subtitleTextView.setText((CharSequence)localObject1);
    setTypingAnimation(true);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ChatAvatarContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */