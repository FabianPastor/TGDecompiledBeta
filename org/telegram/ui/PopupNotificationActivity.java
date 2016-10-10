package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PopupAudioView;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TypingDotsDrawable;

public class PopupNotificationActivity
  extends Activity
  implements NotificationCenter.NotificationCenterDelegate
{
  private ActionBar actionBar;
  private boolean animationInProgress = false;
  private long animationStartTime = 0L;
  private ArrayList<ViewGroup> audioViews = new ArrayList();
  private FrameLayout avatarContainer;
  private BackupImageView avatarImageView;
  private ViewGroup centerView;
  private ChatActivityEnterView chatActivityEnterView;
  private int classGuid;
  private TextView countText;
  private TLRPC.Chat currentChat;
  private int currentMessageNum = 0;
  private MessageObject currentMessageObject = null;
  private TLRPC.User currentUser;
  private boolean finished = false;
  private ArrayList<ViewGroup> imageViews = new ArrayList();
  private boolean isReply;
  private CharSequence lastPrintString;
  private ViewGroup leftView;
  private ViewGroup messageContainer;
  private float moveStartX = -1.0F;
  private TextView nameTextView;
  private Runnable onAnimationEndRunnable = null;
  private TextView onlineTextView;
  private RelativeLayout popupContainer;
  private ArrayList<MessageObject> popupMessages = new ArrayList();
  private RecordStatusDrawable recordStatusDrawable;
  private ViewGroup rightView;
  private boolean startedMoving = false;
  private ArrayList<ViewGroup> textViews = new ArrayList();
  private TypingDotsDrawable typingDotsDrawable;
  private VelocityTracker velocityTracker = null;
  private PowerManager.WakeLock wakeLock = null;
  
  private void applyViewsLayoutParams(int paramInt)
  {
    int i = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
    FrameLayout.LayoutParams localLayoutParams;
    if (this.leftView != null)
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.leftView.getLayoutParams();
      localLayoutParams.gravity = 51;
      localLayoutParams.height = -1;
      localLayoutParams.width = i;
      localLayoutParams.leftMargin = (-i + paramInt);
      this.leftView.setLayoutParams(localLayoutParams);
    }
    if (this.centerView != null)
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.centerView.getLayoutParams();
      localLayoutParams.gravity = 51;
      localLayoutParams.height = -1;
      localLayoutParams.width = i;
      localLayoutParams.leftMargin = paramInt;
      this.centerView.setLayoutParams(localLayoutParams);
    }
    if (this.rightView != null)
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.rightView.getLayoutParams();
      localLayoutParams.gravity = 51;
      localLayoutParams.height = -1;
      localLayoutParams.width = i;
      localLayoutParams.leftMargin = (i + paramInt);
      this.rightView.setLayoutParams(localLayoutParams);
    }
    this.messageContainer.invalidate();
  }
  
  private void checkAndUpdateAvatar()
  {
    Object localObject4 = null;
    Object localObject3 = null;
    Object localObject2 = null;
    Object localObject1 = null;
    if (this.currentChat != null)
    {
      localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
      if (localObject1 != null) {}
    }
    label168:
    for (;;)
    {
      return;
      this.currentChat = ((TLRPC.Chat)localObject1);
      if (this.currentChat.photo != null) {
        localObject2 = this.currentChat.photo.photo_small;
      }
      localObject1 = new AvatarDrawable(this.currentChat);
      for (;;)
      {
        if (this.avatarImageView == null) {
          break label168;
        }
        this.avatarImageView.setImage((TLObject)localObject2, "50_50", (Drawable)localObject1);
        return;
        localObject2 = localObject4;
        if (this.currentUser != null)
        {
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
          if (localObject1 == null) {
            break;
          }
          this.currentUser = ((TLRPC.User)localObject1);
          localObject2 = localObject3;
          if (this.currentUser.photo != null) {
            localObject2 = this.currentUser.photo.photo_small;
          }
          localObject1 = new AvatarDrawable(this.currentUser);
        }
      }
    }
  }
  
  private void fixLayout()
  {
    if (this.avatarContainer != null) {
      this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          if (PopupNotificationActivity.this.avatarContainer != null) {
            PopupNotificationActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
          }
          int i = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0F)) / 2;
          PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), i, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), i);
          return true;
        }
      });
    }
    if (this.messageContainer != null) {
      this.messageContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
          if ((!PopupNotificationActivity.this.checkTransitionAnimation()) && (!PopupNotificationActivity.this.startedMoving))
          {
            ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)PopupNotificationActivity.this.messageContainer.getLayoutParams();
            localMarginLayoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
            localMarginLayoutParams.bottomMargin = AndroidUtilities.dp(48.0F);
            localMarginLayoutParams.width = -1;
            localMarginLayoutParams.height = -1;
            PopupNotificationActivity.this.messageContainer.setLayoutParams(localMarginLayoutParams);
            PopupNotificationActivity.this.applyViewsLayoutParams(0);
          }
          return true;
        }
      });
    }
  }
  
  private void getNewMessage()
  {
    if (this.popupMessages.isEmpty())
    {
      onFinish();
      finish();
      return;
    }
    int k = 0;
    int j;
    int i;
    if ((this.currentMessageNum == 0) && (!this.chatActivityEnterView.hasText()))
    {
      j = k;
      if (!this.startedMoving) {}
    }
    else
    {
      j = k;
      if (this.currentMessageObject != null)
      {
        i = 0;
        j = k;
        if (i < this.popupMessages.size())
        {
          if (((MessageObject)this.popupMessages.get(i)).getId() != this.currentMessageObject.getId()) {
            break label174;
          }
          this.currentMessageNum = i;
          j = 1;
        }
      }
    }
    if (j == 0)
    {
      this.currentMessageNum = 0;
      this.currentMessageObject = ((MessageObject)this.popupMessages.get(0));
      updateInterfaceForCurrentMessage(0);
    }
    for (;;)
    {
      this.countText.setText(String.format("%d/%d", new Object[] { Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size()) }));
      return;
      label174:
      i += 1;
      break;
      if (this.startedMoving) {
        if (this.currentMessageNum == this.popupMessages.size() - 1) {
          prepareLayouts(3);
        } else if (this.currentMessageNum == 1) {
          prepareLayouts(4);
        }
      }
    }
  }
  
  private ViewGroup getViewForMessage(int paramInt, boolean paramBoolean)
  {
    if ((this.popupMessages.size() == 1) && ((paramInt < 0) || (paramInt >= this.popupMessages.size()))) {
      localObject2 = null;
    }
    int i;
    MessageObject localMessageObject;
    label112:
    Object localObject3;
    BackupImageView localBackupImageView;
    Object localObject1;
    TLRPC.PhotoSize localPhotoSize;
    int j;
    label262:
    label306:
    do
    {
      return (ViewGroup)localObject2;
      if (paramInt != -1) {
        break;
      }
      i = this.popupMessages.size() - 1;
      localMessageObject = (MessageObject)this.popupMessages.get(i);
      if ((localMessageObject.type != 1) && (localMessageObject.type != 4)) {
        break label671;
      }
      if (this.imageViews.size() <= 0) {
        break label429;
      }
      localObject2 = (ViewGroup)this.imageViews.get(0);
      this.imageViews.remove(0);
      localObject3 = (TextView)((ViewGroup)localObject2).findViewById(2131492914);
      localBackupImageView = (BackupImageView)((ViewGroup)localObject2).findViewById(2131492927);
      localBackupImageView.setAspectFit(true);
      if (localMessageObject.type != 1) {
        break label529;
      }
      localObject1 = FileLoader.getClosestPhotoSizeWithSize(localMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
      localPhotoSize = FileLoader.getClosestPhotoSizeWithSize(localMessageObject.photoThumbs, 100);
      j = 0;
      paramInt = j;
      if (localObject1 != null)
      {
        int k = 1;
        paramInt = k;
        if (localMessageObject.type == 1)
        {
          paramInt = k;
          if (!FileLoader.getPathToMessage(localMessageObject.messageOwner).exists()) {
            paramInt = 0;
          }
        }
        if ((paramInt == 0) && (!MediaController.getInstance().canDownloadMedia(1))) {
          break label481;
        }
        localBackupImageView.setImage(((TLRPC.PhotoSize)localObject1).location, "100_100", localPhotoSize.location, ((TLRPC.PhotoSize)localObject1).size);
        paramInt = 1;
      }
      if (paramInt != 0) {
        break label509;
      }
      localBackupImageView.setVisibility(8);
      ((TextView)localObject3).setVisibility(0);
      ((TextView)localObject3).setTextSize(2, MessagesController.getInstance().fontSize);
      ((TextView)localObject3).setText(localMessageObject.messageText);
      localObject1 = localObject2;
      if (((ViewGroup)localObject1).getParent() == null) {
        this.messageContainer.addView((View)localObject1);
      }
      ((ViewGroup)localObject1).setVisibility(0);
      localObject2 = localObject1;
    } while (!paramBoolean);
    paramInt = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
    Object localObject2 = (FrameLayout.LayoutParams)((ViewGroup)localObject1).getLayoutParams();
    ((FrameLayout.LayoutParams)localObject2).gravity = 51;
    ((FrameLayout.LayoutParams)localObject2).height = -1;
    ((FrameLayout.LayoutParams)localObject2).width = paramInt;
    if (i == this.currentMessageNum) {
      ((FrameLayout.LayoutParams)localObject2).leftMargin = 0;
    }
    for (;;)
    {
      ((ViewGroup)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      ((ViewGroup)localObject1).invalidate();
      return (ViewGroup)localObject1;
      i = paramInt;
      if (paramInt != this.popupMessages.size()) {
        break;
      }
      i = 0;
      break;
      label429:
      localObject2 = new FrameLayoutAnimationListener(this);
      ((ViewGroup)localObject2).addView(getLayoutInflater().inflate(2130903056, null));
      ((ViewGroup)localObject2).setTag(Integer.valueOf(2));
      ((ViewGroup)localObject2).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          PopupNotificationActivity.this.openCurrentMessage();
        }
      });
      break label112;
      label481:
      paramInt = j;
      if (localPhotoSize == null) {
        break label262;
      }
      localBackupImageView.setImage(localPhotoSize.location, null, (Drawable)null);
      paramInt = 1;
      break label262;
      label509:
      localBackupImageView.setVisibility(0);
      ((TextView)localObject3).setVisibility(8);
      localObject1 = localObject2;
      break label306;
      label529:
      localObject1 = localObject2;
      if (localMessageObject.type != 4) {
        break label306;
      }
      ((TextView)localObject3).setVisibility(8);
      ((TextView)localObject3).setText(localMessageObject.messageText);
      localBackupImageView.setVisibility(0);
      double d1 = localMessageObject.messageOwner.media.geo.lat;
      double d2 = localMessageObject.messageOwner.media.geo._long;
      localBackupImageView.setImage(String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:big|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) }), null, null);
      localObject1 = localObject2;
      break label306;
      label671:
      if (localMessageObject.type == 2)
      {
        if (this.audioViews.size() > 0)
        {
          localObject2 = (ViewGroup)this.audioViews.get(0);
          this.audioViews.remove(0);
          localObject3 = (PopupAudioView)((ViewGroup)localObject2).findViewWithTag(Integer.valueOf(300));
        }
        for (;;)
        {
          ((PopupAudioView)localObject3).setMessageObject(localMessageObject);
          localObject1 = localObject2;
          if (!MediaController.getInstance().canDownloadMedia(2)) {
            break;
          }
          ((PopupAudioView)localObject3).downloadAudioIfNeed();
          localObject1 = localObject2;
          break;
          localObject2 = new FrameLayoutAnimationListener(this);
          ((ViewGroup)localObject2).addView(getLayoutInflater().inflate(2130903054, null));
          ((ViewGroup)localObject2).setTag(Integer.valueOf(3));
          ((ViewGroup)localObject2).setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              PopupNotificationActivity.this.openCurrentMessage();
            }
          });
          localObject1 = (ViewGroup)((ViewGroup)localObject2).findViewById(2131492925);
          localObject3 = new PopupAudioView(this);
          ((PopupAudioView)localObject3).setTag(Integer.valueOf(300));
          ((ViewGroup)localObject1).addView((View)localObject3);
        }
      }
      if (this.textViews.size() > 0)
      {
        localObject1 = (ViewGroup)this.textViews.get(0);
        this.textViews.remove(0);
      }
      for (;;)
      {
        localObject2 = (TextView)((ViewGroup)localObject1).findViewById(2131492914);
        ((TextView)localObject2).setTag(Integer.valueOf(301));
        ((TextView)localObject2).setTextSize(2, MessagesController.getInstance().fontSize);
        ((TextView)localObject2).setText(localMessageObject.messageText);
        break;
        localObject1 = new FrameLayoutAnimationListener(this);
        ((ViewGroup)localObject1).addView(getLayoutInflater().inflate(2130903057, null));
        ((ViewGroup)localObject1).setTag(Integer.valueOf(1));
        ((ViewGroup)localObject1).findViewById(2131492924).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PopupNotificationActivity.this.openCurrentMessage();
          }
        });
      }
      if (i == this.currentMessageNum - 1) {
        ((FrameLayout.LayoutParams)localObject2).leftMargin = (-paramInt);
      } else if (i == this.currentMessageNum + 1) {
        ((FrameLayout.LayoutParams)localObject2).leftMargin = paramInt;
      }
    }
  }
  
  private void handleIntent(Intent paramIntent)
  {
    boolean bool;
    if ((paramIntent != null) && (paramIntent.getBooleanExtra("force", false)))
    {
      bool = true;
      this.isReply = bool;
      if (!this.isReply) {
        break label93;
      }
      this.popupMessages = NotificationsController.getInstance().popupReplyMessages;
      label39:
      if ((!((KeyguardManager)getSystemService("keyguard")).inKeyguardRestrictedInputMode()) && (ApplicationLoader.isScreenOn)) {
        break label106;
      }
      getWindow().addFlags(2623490);
    }
    for (;;)
    {
      if (this.currentMessageObject == null) {
        this.currentMessageNum = 0;
      }
      getNewMessage();
      return;
      bool = false;
      break;
      label93:
      this.popupMessages = NotificationsController.getInstance().popupMessages;
      break label39;
      label106:
      getWindow().addFlags(2623488);
      getWindow().clearFlags(2);
    }
  }
  
  private void openCurrentMessage()
  {
    if (this.currentMessageObject == null) {
      return;
    }
    Intent localIntent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
    long l = this.currentMessageObject.getDialogId();
    int i;
    if ((int)l != 0)
    {
      i = (int)l;
      if (i < 0) {
        localIntent.putExtra("chatId", -i);
      }
    }
    for (;;)
    {
      localIntent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
      localIntent.setFlags(32768);
      startActivity(localIntent);
      onFinish();
      finish();
      return;
      localIntent.putExtra("userId", i);
      continue;
      localIntent.putExtra("encId", (int)(l >> 32));
    }
  }
  
  private void prepareLayouts(int paramInt)
  {
    if (paramInt == 0)
    {
      reuseView(this.centerView);
      reuseView(this.leftView);
      reuseView(this.rightView);
      paramInt = this.currentMessageNum - 1;
      if (paramInt < this.currentMessageNum + 2)
      {
        if (paramInt == this.currentMessageNum - 1) {
          this.leftView = getViewForMessage(paramInt, true);
        }
        for (;;)
        {
          paramInt += 1;
          break;
          if (paramInt == this.currentMessageNum) {
            this.centerView = getViewForMessage(paramInt, true);
          } else if (paramInt == this.currentMessageNum + 1) {
            this.rightView = getViewForMessage(paramInt, true);
          }
        }
      }
    }
    else
    {
      if (paramInt != 1) {
        break label161;
      }
      reuseView(this.rightView);
      this.rightView = this.centerView;
      this.centerView = this.leftView;
      this.leftView = getViewForMessage(this.currentMessageNum - 1, true);
    }
    label161:
    do
    {
      do
      {
        do
        {
          do
          {
            return;
            if (paramInt == 2)
            {
              reuseView(this.leftView);
              this.leftView = this.centerView;
              this.centerView = this.rightView;
              this.rightView = getViewForMessage(this.currentMessageNum + 1, true);
              return;
            }
            if (paramInt != 3) {
              break;
            }
          } while (this.rightView == null);
          paramInt = ((FrameLayout.LayoutParams)this.rightView.getLayoutParams()).leftMargin;
          reuseView(this.rightView);
          localObject = getViewForMessage(this.currentMessageNum + 1, false);
          this.rightView = ((ViewGroup)localObject);
        } while (localObject == null);
        i = AndroidUtilities.displaySize.x;
        j = AndroidUtilities.dp(24.0F);
        localObject = (FrameLayout.LayoutParams)this.rightView.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).gravity = 51;
        ((FrameLayout.LayoutParams)localObject).height = -1;
        ((FrameLayout.LayoutParams)localObject).width = (i - j);
        ((FrameLayout.LayoutParams)localObject).leftMargin = paramInt;
        this.rightView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        this.rightView.invalidate();
        return;
      } while ((paramInt != 4) || (this.leftView == null));
      paramInt = ((FrameLayout.LayoutParams)this.leftView.getLayoutParams()).leftMargin;
      reuseView(this.leftView);
      localObject = getViewForMessage(0, false);
      this.leftView = ((ViewGroup)localObject);
    } while (localObject == null);
    int i = AndroidUtilities.displaySize.x;
    int j = AndroidUtilities.dp(24.0F);
    Object localObject = (FrameLayout.LayoutParams)this.leftView.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).gravity = 51;
    ((FrameLayout.LayoutParams)localObject).height = -1;
    ((FrameLayout.LayoutParams)localObject).width = (i - j);
    ((FrameLayout.LayoutParams)localObject).leftMargin = paramInt;
    this.leftView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    this.leftView.invalidate();
  }
  
  private void reuseView(ViewGroup paramViewGroup)
  {
    if (paramViewGroup == null) {}
    int i;
    do
    {
      return;
      i = ((Integer)paramViewGroup.getTag()).intValue();
      paramViewGroup.setVisibility(8);
      if (i == 1)
      {
        this.textViews.add(paramViewGroup);
        return;
      }
      if (i == 2)
      {
        this.imageViews.add(paramViewGroup);
        return;
      }
    } while (i != 3);
    this.audioViews.add(paramViewGroup);
  }
  
  private void setTypingAnimation(boolean paramBoolean)
  {
    if (this.actionBar == null) {}
    do
    {
      return;
      if (!paramBoolean) {
        break;
      }
      try
      {
        Integer localInteger = (Integer)MessagesController.getInstance().printingStringsTypes.get(Long.valueOf(this.currentMessageObject.getDialogId()));
        if (localInteger.intValue() == 0)
        {
          this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.typingDotsDrawable, null, null, null);
          this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
          this.typingDotsDrawable.start();
          this.recordStatusDrawable.stop();
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
        return;
      }
    } while (localException.intValue() != 1);
    this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.recordStatusDrawable, null, null, null);
    this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
    this.recordStatusDrawable.start();
    this.typingDotsDrawable.stop();
    return;
    this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    this.onlineTextView.setCompoundDrawablePadding(0);
    this.typingDotsDrawable.stop();
    this.recordStatusDrawable.stop();
  }
  
  private void switchToNextMessage()
  {
    if (this.popupMessages.size() > 1) {
      if (this.currentMessageNum >= this.popupMessages.size() - 1) {
        break label103;
      }
    }
    label103:
    for (this.currentMessageNum += 1;; this.currentMessageNum = 0)
    {
      this.currentMessageObject = ((MessageObject)this.popupMessages.get(this.currentMessageNum));
      updateInterfaceForCurrentMessage(2);
      this.countText.setText(String.format("%d/%d", new Object[] { Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size()) }));
      return;
    }
  }
  
  private void switchToPreviousMessage()
  {
    if (this.popupMessages.size() > 1) {
      if (this.currentMessageNum <= 0) {
        break label94;
      }
    }
    label94:
    for (this.currentMessageNum -= 1;; this.currentMessageNum = (this.popupMessages.size() - 1))
    {
      this.currentMessageObject = ((MessageObject)this.popupMessages.get(this.currentMessageNum));
      updateInterfaceForCurrentMessage(1);
      this.countText.setText(String.format("%d/%d", new Object[] { Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size()) }));
      return;
    }
  }
  
  private void updateInterfaceForCurrentMessage(int paramInt)
  {
    if (this.actionBar == null) {
      return;
    }
    this.currentChat = null;
    this.currentUser = null;
    long l = this.currentMessageObject.getDialogId();
    this.chatActivityEnterView.setDialogId(l);
    int i;
    if ((int)l != 0)
    {
      i = (int)l;
      if (i > 0)
      {
        this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(i));
        if ((this.currentChat == null) || (this.currentUser == null)) {
          break label218;
        }
        this.nameTextView.setText(this.currentChat.title);
        this.onlineTextView.setText(UserObject.getUserName(this.currentUser));
        this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        this.nameTextView.setCompoundDrawablePadding(0);
      }
    }
    for (;;)
    {
      prepareLayouts(paramInt);
      updateSubtitle();
      checkAndUpdateAvatar();
      applyViewsLayoutParams(0);
      return;
      this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-i));
      this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
      break;
      TLRPC.EncryptedChat localEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(l >> 32)));
      this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(localEncryptedChat.user_id));
      break;
      label218:
      if (this.currentUser != null)
      {
        this.nameTextView.setText(UserObject.getUserName(this.currentUser));
        if ((int)l == 0)
        {
          this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(2130837738, 0, 0, 0);
          this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
        }
        else
        {
          this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
          this.nameTextView.setCompoundDrawablePadding(0);
        }
      }
    }
  }
  
  private void updateSubtitle()
  {
    if (this.actionBar == null) {}
    while ((this.currentChat != null) || (this.currentUser == null)) {
      return;
    }
    if ((this.currentUser.id / 1000 != 777) && (this.currentUser.id / 1000 != 333) && (ContactsController.getInstance().contactsDict.get(this.currentUser.id) == null) && ((ContactsController.getInstance().contactsDict.size() != 0) || (!ContactsController.getInstance().isLoadingContacts()))) {
      if ((this.currentUser.phone != null) && (this.currentUser.phone.length() != 0)) {
        this.nameTextView.setText(PhoneFormat.getInstance().format("+" + this.currentUser.phone));
      }
    }
    Object localObject;
    for (;;)
    {
      localObject = (CharSequence)MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentMessageObject.getDialogId()));
      if ((localObject != null) && (((CharSequence)localObject).length() != 0)) {
        break;
      }
      this.lastPrintString = null;
      setTypingAnimation(false);
      localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
      if (localObject != null) {
        this.currentUser = ((TLRPC.User)localObject);
      }
      this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentUser));
      return;
      this.nameTextView.setText(UserObject.getUserName(this.currentUser));
      continue;
      this.nameTextView.setText(UserObject.getUserName(this.currentUser));
    }
    this.lastPrintString = ((CharSequence)localObject);
    this.onlineTextView.setText((CharSequence)localObject);
    setTypingAnimation(true);
  }
  
  public boolean checkTransitionAnimation()
  {
    if ((this.animationInProgress) && (this.animationStartTime < System.currentTimeMillis() - 400L))
    {
      this.animationInProgress = false;
      if (this.onAnimationEndRunnable != null)
      {
        this.onAnimationEndRunnable.run();
        this.onAnimationEndRunnable = null;
      }
    }
    return this.animationInProgress;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.appDidLogout)
    {
      onFinish();
      finish();
    }
    do
    {
      for (;;)
      {
        return;
        if (paramInt == NotificationCenter.pushMessagesUpdated)
        {
          getNewMessage();
          return;
        }
        if (paramInt == NotificationCenter.updateInterfaces)
        {
          if (this.currentMessageObject != null)
          {
            paramInt = ((Integer)paramVarArgs[0]).intValue();
            if (((paramInt & 0x1) != 0) || ((paramInt & 0x4) != 0) || ((paramInt & 0x10) != 0) || ((paramInt & 0x20) != 0)) {
              updateSubtitle();
            }
            if (((paramInt & 0x2) != 0) || ((paramInt & 0x8) != 0)) {
              checkAndUpdateAvatar();
            }
            if ((paramInt & 0x40) != 0)
            {
              paramVarArgs = (CharSequence)MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentMessageObject.getDialogId()));
              if (((this.lastPrintString != null) && (paramVarArgs == null)) || ((this.lastPrintString == null) && (paramVarArgs != null)) || ((this.lastPrintString != null) && (paramVarArgs != null) && (!this.lastPrintString.equals(paramVarArgs)))) {
                updateSubtitle();
              }
            }
          }
        }
        else
        {
          int i;
          Object localObject;
          if (paramInt == NotificationCenter.audioDidReset)
          {
            paramVarArgs = (Integer)paramVarArgs[0];
            if (this.messageContainer != null)
            {
              i = this.messageContainer.getChildCount();
              paramInt = 0;
              while (paramInt < i)
              {
                localObject = this.messageContainer.getChildAt(paramInt);
                if (((Integer)((View)localObject).getTag()).intValue() == 3)
                {
                  localObject = (PopupAudioView)((View)localObject).findViewWithTag(Integer.valueOf(300));
                  if ((((PopupAudioView)localObject).getMessageObject() != null) && (((PopupAudioView)localObject).getMessageObject().getId() == paramVarArgs.intValue()))
                  {
                    ((PopupAudioView)localObject).updateButtonState();
                    return;
                  }
                }
                paramInt += 1;
              }
            }
          }
          else if (paramInt == NotificationCenter.audioProgressDidChanged)
          {
            paramVarArgs = (Integer)paramVarArgs[0];
            if (this.messageContainer != null)
            {
              i = this.messageContainer.getChildCount();
              paramInt = 0;
              while (paramInt < i)
              {
                localObject = this.messageContainer.getChildAt(paramInt);
                if (((Integer)((View)localObject).getTag()).intValue() == 3)
                {
                  localObject = (PopupAudioView)((View)localObject).findViewWithTag(Integer.valueOf(300));
                  if ((((PopupAudioView)localObject).getMessageObject() != null) && (((PopupAudioView)localObject).getMessageObject().getId() == paramVarArgs.intValue()))
                  {
                    ((PopupAudioView)localObject).updateProgress();
                    return;
                  }
                }
                paramInt += 1;
              }
            }
          }
          else
          {
            if (paramInt != NotificationCenter.emojiDidLoaded) {
              break;
            }
            if (this.messageContainer != null)
            {
              i = this.messageContainer.getChildCount();
              paramInt = 0;
              while (paramInt < i)
              {
                paramVarArgs = this.messageContainer.getChildAt(paramInt);
                if (((Integer)paramVarArgs.getTag()).intValue() == 1)
                {
                  paramVarArgs = (TextView)paramVarArgs.findViewWithTag(Integer.valueOf(301));
                  if (paramVarArgs != null) {
                    paramVarArgs.invalidate();
                  }
                }
                paramInt += 1;
              }
            }
          }
        }
      }
    } while (paramInt != NotificationCenter.contactsDidLoaded);
    updateSubtitle();
  }
  
  public void onBackPressed()
  {
    if (this.chatActivityEnterView.isPopupShowing())
    {
      this.chatActivityEnterView.hidePopup(true);
      return;
    }
    super.onBackPressed();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    AndroidUtilities.checkDisplaySize(this, paramConfiguration);
    fixLayout();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Theme.loadRecources(this);
    int i = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (i > 0) {
      AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(i);
    }
    this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    this.typingDotsDrawable = new TypingDotsDrawable();
    this.recordStatusDrawable = new RecordStatusDrawable();
    paramBundle = new SizeNotifierFrameLayout(this)
    {
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        int n = getChildCount();
        if (getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) {}
        View localView;
        for (int k = PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();; k = 0)
        {
          int m = 0;
          for (;;)
          {
            if (m >= n) {
              break label457;
            }
            localView = getChildAt(m);
            if (localView.getVisibility() != 8) {
              break;
            }
            m += 1;
          }
        }
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        int i1 = localView.getMeasuredWidth();
        int i2 = localView.getMeasuredHeight();
        int j = localLayoutParams.gravity;
        int i = j;
        if (j == -1) {
          i = 51;
        }
        switch (i & 0x7 & 0x7)
        {
        default: 
          j = localLayoutParams.leftMargin;
          label159:
          switch (i & 0x70)
          {
          default: 
            i = localLayoutParams.topMargin;
            label207:
            if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(localView)) {
              if (k != 0) {
                i = getMeasuredHeight() - k;
              }
            }
            break;
          }
          break;
        }
        for (;;)
        {
          localView.layout(j, i, j + i1, i + i2);
          break;
          j = (paramAnonymousInt3 - paramAnonymousInt1 - i1) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
          break label159;
          j = paramAnonymousInt3 - i1 - localLayoutParams.rightMargin;
          break label159;
          i = localLayoutParams.topMargin;
          break label207;
          i = (paramAnonymousInt4 - k - paramAnonymousInt2 - i2) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
          break label207;
          i = paramAnonymousInt4 - k - paramAnonymousInt2 - i2 - localLayoutParams.bottomMargin;
          break label207;
          i = getMeasuredHeight();
          continue;
          if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(localView))
          {
            i = PopupNotificationActivity.this.popupContainer.getTop() + PopupNotificationActivity.this.popupContainer.getMeasuredHeight() - localView.getMeasuredHeight() - localLayoutParams.bottomMargin;
            j = PopupNotificationActivity.this.popupContainer.getLeft() + PopupNotificationActivity.this.popupContainer.getMeasuredWidth() - localView.getMeasuredWidth() - localLayoutParams.rightMargin;
          }
        }
        label457:
        notifyHeightChanged();
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        View.MeasureSpec.getMode(paramAnonymousInt1);
        View.MeasureSpec.getMode(paramAnonymousInt2);
        int k = View.MeasureSpec.getSize(paramAnonymousInt1);
        int j = View.MeasureSpec.getSize(paramAnonymousInt2);
        setMeasuredDimension(k, j);
        int i = j;
        if (getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) {
          i = j - PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
        }
        int m = getChildCount();
        j = 0;
        if (j < m)
        {
          View localView = getChildAt(j);
          if (localView.getVisibility() == 8) {}
          for (;;)
          {
            j += 1;
            break;
            if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(localView)) {
              localView.measure(View.MeasureSpec.makeMeasureSpec(k, 1073741824), View.MeasureSpec.makeMeasureSpec(localView.getLayoutParams().height, 1073741824));
            } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(localView)) {
              measureChildWithMargins(localView, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
            } else {
              localView.measure(View.MeasureSpec.makeMeasureSpec(k, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(2.0F) + i), 1073741824));
            }
          }
        }
      }
    };
    setContentView(paramBundle);
    paramBundle.setBackgroundColor(-1728053248);
    RelativeLayout localRelativeLayout = new RelativeLayout(this);
    paramBundle.addView(localRelativeLayout, LayoutHelper.createFrame(-1, -1.0F));
    this.popupContainer = new RelativeLayout(this);
    this.popupContainer.setBackgroundColor(-1);
    localRelativeLayout.addView(this.popupContainer, LayoutHelper.createRelative(-1, 240, 12, 0, 12, 0, 13));
    if (this.chatActivityEnterView != null) {
      this.chatActivityEnterView.onDestroy();
    }
    this.chatActivityEnterView = new ChatActivityEnterView(this, paramBundle, null, false);
    this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
    this.chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate()
    {
      public void needSendTyping()
      {
        if (PopupNotificationActivity.this.currentMessageObject != null) {
          MessagesController.getInstance().sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, PopupNotificationActivity.this.classGuid);
        }
      }
      
      public void onAttachButtonHidden() {}
      
      public void onAttachButtonShow() {}
      
      public void onMessageEditEnd(boolean paramAnonymousBoolean) {}
      
      public void onMessageSend(CharSequence paramAnonymousCharSequence)
      {
        if (PopupNotificationActivity.this.currentMessageObject == null) {
          return;
        }
        if ((PopupNotificationActivity.this.currentMessageNum >= 0) && (PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size())) {
          PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
        }
        MessagesController.getInstance().markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, true);
        PopupNotificationActivity.access$302(PopupNotificationActivity.this, null);
        PopupNotificationActivity.this.getNewMessage();
      }
      
      public void onStickersTab(boolean paramAnonymousBoolean) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, boolean paramAnonymousBoolean) {}
      
      public void onWindowSizeChanged(int paramAnonymousInt) {}
    });
    this.messageContainer = new FrameLayoutTouch(this);
    this.popupContainer.addView(this.messageContainer, 0);
    this.actionBar = new ActionBar(this);
    this.actionBar.setOccupyStatusBar(false);
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setBackgroundColor(-11371101);
    this.actionBar.setItemsBackgroundColor(-12554860);
    this.popupContainer.addView(this.actionBar);
    paramBundle = this.actionBar.getLayoutParams();
    paramBundle.width = -1;
    this.actionBar.setLayoutParams(paramBundle);
    this.countText = ((TextView)this.actionBar.createMenu().addItemResource(2, 2130903055).findViewById(2131492926));
    this.avatarContainer = new FrameLayout(this);
    this.avatarContainer.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
    this.actionBar.addView(this.avatarContainer);
    paramBundle = (FrameLayout.LayoutParams)this.avatarContainer.getLayoutParams();
    paramBundle.height = -1;
    paramBundle.width = -2;
    paramBundle.rightMargin = AndroidUtilities.dp(48.0F);
    paramBundle.leftMargin = AndroidUtilities.dp(60.0F);
    paramBundle.gravity = 51;
    this.avatarContainer.setLayoutParams(paramBundle);
    this.avatarImageView = new BackupImageView(this);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0F));
    this.avatarContainer.addView(this.avatarImageView);
    paramBundle = (FrameLayout.LayoutParams)this.avatarImageView.getLayoutParams();
    paramBundle.width = AndroidUtilities.dp(42.0F);
    paramBundle.height = AndroidUtilities.dp(42.0F);
    paramBundle.topMargin = AndroidUtilities.dp(3.0F);
    this.avatarImageView.setLayoutParams(paramBundle);
    this.nameTextView = new TextView(this);
    this.nameTextView.setTextColor(-1);
    this.nameTextView.setTextSize(1, 18.0F);
    this.nameTextView.setLines(1);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.nameTextView.setGravity(3);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.avatarContainer.addView(this.nameTextView);
    paramBundle = (FrameLayout.LayoutParams)this.nameTextView.getLayoutParams();
    paramBundle.width = -2;
    paramBundle.height = -2;
    paramBundle.leftMargin = AndroidUtilities.dp(54.0F);
    paramBundle.bottomMargin = AndroidUtilities.dp(22.0F);
    paramBundle.gravity = 80;
    this.nameTextView.setLayoutParams(paramBundle);
    this.onlineTextView = new TextView(this);
    this.onlineTextView.setTextColor(-2758409);
    this.onlineTextView.setTextSize(1, 14.0F);
    this.onlineTextView.setLines(1);
    this.onlineTextView.setMaxLines(1);
    this.onlineTextView.setSingleLine(true);
    this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.onlineTextView.setGravity(3);
    this.avatarContainer.addView(this.onlineTextView);
    paramBundle = (FrameLayout.LayoutParams)this.onlineTextView.getLayoutParams();
    paramBundle.width = -2;
    paramBundle.height = -2;
    paramBundle.leftMargin = AndroidUtilities.dp(54.0F);
    paramBundle.bottomMargin = AndroidUtilities.dp(4.0F);
    paramBundle.gravity = 80;
    this.onlineTextView.setLayoutParams(paramBundle);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1)
        {
          PopupNotificationActivity.this.onFinish();
          PopupNotificationActivity.this.finish();
        }
        do
        {
          return;
          if (paramAnonymousInt == 1)
          {
            PopupNotificationActivity.this.openCurrentMessage();
            return;
          }
        } while (paramAnonymousInt != 2);
        PopupNotificationActivity.this.switchToNextMessage();
      }
    });
    this.wakeLock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(268435462, "screen");
    this.wakeLock.setReferenceCounted(false);
    handleIntent(getIntent());
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    onFinish();
    if (this.wakeLock.isHeld()) {
      this.wakeLock.release();
    }
    if (this.avatarImageView != null) {
      this.avatarImageView.setImageDrawable(null);
    }
  }
  
  protected void onFinish()
  {
    if (this.finished) {}
    do
    {
      return;
      this.finished = true;
      if (this.isReply) {
        this.popupMessages.clear();
      }
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
      if (this.chatActivityEnterView != null) {
        this.chatActivityEnterView.onDestroy();
      }
    } while (!this.wakeLock.isHeld());
    this.wakeLock.release();
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    handleIntent(paramIntent);
  }
  
  protected void onPause()
  {
    super.onPause();
    overridePendingTransition(0, 0);
    if (this.chatActivityEnterView != null)
    {
      this.chatActivityEnterView.hidePopup(false);
      this.chatActivityEnterView.setFieldFocused(false);
    }
    ConnectionsManager.getInstance().setAppPaused(true, false);
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
    if ((paramInt != 3) || (paramArrayOfInt[0] == 0)) {
      return;
    }
    paramArrayOfString = new AlertDialog.Builder(this);
    paramArrayOfString.setTitle(LocaleController.getString("AppName", 2131165299));
    paramArrayOfString.setMessage(LocaleController.getString("PermissionNoAudio", 2131166097));
    paramArrayOfString.setNegativeButton(LocaleController.getString("PermissionOpenSettings", 2131166101), new DialogInterface.OnClickListener()
    {
      @TargetApi(9)
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        try
        {
          paramAnonymousDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
          paramAnonymousDialogInterface.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
          PopupNotificationActivity.this.startActivity(paramAnonymousDialogInterface);
          return;
        }
        catch (Exception paramAnonymousDialogInterface)
        {
          FileLog.e("tmessages", paramAnonymousDialogInterface);
        }
      }
    });
    paramArrayOfString.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
    paramArrayOfString.show();
  }
  
  protected void onResume()
  {
    super.onResume();
    if (this.chatActivityEnterView != null) {
      this.chatActivityEnterView.setFieldFocused(true);
    }
    ConnectionsManager.getInstance().setAppPaused(false, false);
    fixLayout();
    checkAndUpdateAvatar();
    this.wakeLock.acquire(7000L);
  }
  
  public boolean onTouchEventMy(MotionEvent paramMotionEvent)
  {
    if (checkTransitionAnimation()) {
      return false;
    }
    if ((paramMotionEvent != null) && (paramMotionEvent.getAction() == 0)) {
      this.moveStartX = paramMotionEvent.getX();
    }
    int j;
    int i;
    label199:
    label207:
    do
    {
      for (;;)
      {
        return this.startedMoving;
        if ((paramMotionEvent == null) || (paramMotionEvent.getAction() != 2)) {
          break;
        }
        float f = paramMotionEvent.getX();
        j = (int)(f - this.moveStartX);
        i = j;
        if (this.moveStartX != -1.0F)
        {
          i = j;
          if (!this.startedMoving)
          {
            i = j;
            if (Math.abs(j) > AndroidUtilities.dp(10.0F))
            {
              this.startedMoving = true;
              this.moveStartX = f;
              AndroidUtilities.lockOrientation(this);
              i = 0;
              if (this.velocityTracker != null) {
                break label199;
              }
              this.velocityTracker = VelocityTracker.obtain();
            }
          }
        }
        for (;;)
        {
          if (!this.startedMoving) {
            break label207;
          }
          j = i;
          if (this.leftView == null)
          {
            j = i;
            if (i > 0) {
              j = 0;
            }
          }
          i = j;
          if (this.rightView == null)
          {
            i = j;
            if (j < 0) {
              i = 0;
            }
          }
          if (this.velocityTracker != null) {
            this.velocityTracker.addMovement(paramMotionEvent);
          }
          applyViewsLayoutParams(i);
          break;
          this.velocityTracker.clear();
        }
      }
    } while ((paramMotionEvent != null) && (paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3));
    Object localObject;
    int n;
    int m;
    int k;
    if ((paramMotionEvent != null) && (this.startedMoving))
    {
      localObject = (FrameLayout.LayoutParams)this.centerView.getLayoutParams();
      n = (int)(paramMotionEvent.getX() - this.moveStartX);
      m = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
      j = 0;
      k = 0;
      paramMotionEvent = null;
      i = k;
      if (this.velocityTracker != null)
      {
        this.velocityTracker.computeCurrentVelocity(1000);
        if (this.velocityTracker.getXVelocity() >= 3500.0F) {
          i = 1;
        }
      }
      else
      {
        label322:
        if (((i != 1) && (n <= m / 3)) || (this.leftView == null)) {
          break label519;
        }
        i = m - ((FrameLayout.LayoutParams)localObject).leftMargin;
        paramMotionEvent = this.leftView;
        this.onAnimationEndRunnable = new Runnable()
        {
          public void run()
          {
            PopupNotificationActivity.access$1002(PopupNotificationActivity.this, false);
            PopupNotificationActivity.this.switchToPreviousMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
          }
        };
        label369:
        if (i != 0)
        {
          j = (int)(Math.abs(i / m) * 200.0F);
          localObject = new TranslateAnimation(0.0F, i, 0.0F, 0.0F);
          ((TranslateAnimation)localObject).setDuration(j);
          this.centerView.startAnimation((Animation)localObject);
          if (paramMotionEvent != null)
          {
            localObject = new TranslateAnimation(0.0F, i, 0.0F, 0.0F);
            ((TranslateAnimation)localObject).setDuration(j);
            paramMotionEvent.startAnimation((Animation)localObject);
          }
          this.animationInProgress = true;
          this.animationStartTime = System.currentTimeMillis();
        }
      }
    }
    for (;;)
    {
      if (this.velocityTracker != null)
      {
        this.velocityTracker.recycle();
        this.velocityTracker = null;
      }
      this.startedMoving = false;
      this.moveStartX = -1.0F;
      break;
      i = k;
      if (this.velocityTracker.getXVelocity() > -3500.0F) {
        break label322;
      }
      i = 2;
      break label322;
      label519:
      if (((i == 2) || (n < -m / 3)) && (this.rightView != null))
      {
        i = -m - ((FrameLayout.LayoutParams)localObject).leftMargin;
        paramMotionEvent = this.rightView;
        this.onAnimationEndRunnable = new Runnable()
        {
          public void run()
          {
            PopupNotificationActivity.access$1002(PopupNotificationActivity.this, false);
            PopupNotificationActivity.this.switchToNextMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
          }
        };
        break label369;
      }
      i = j;
      if (((FrameLayout.LayoutParams)localObject).leftMargin == 0) {
        break label369;
      }
      i = -((FrameLayout.LayoutParams)localObject).leftMargin;
      if (n > 0) {}
      for (paramMotionEvent = this.leftView;; paramMotionEvent = this.rightView)
      {
        this.onAnimationEndRunnable = new Runnable()
        {
          public void run()
          {
            PopupNotificationActivity.access$1002(PopupNotificationActivity.this, false);
            PopupNotificationActivity.this.applyViewsLayoutParams(0);
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
          }
        };
        break;
      }
      applyViewsLayoutParams(0);
    }
  }
  
  public class FrameLayoutAnimationListener
    extends FrameLayout
  {
    public FrameLayoutAnimationListener(Context paramContext)
    {
      super();
    }
    
    public FrameLayoutAnimationListener(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public FrameLayoutAnimationListener(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
    }
    
    protected void onAnimationEnd()
    {
      super.onAnimationEnd();
      if (PopupNotificationActivity.this.onAnimationEndRunnable != null)
      {
        PopupNotificationActivity.this.onAnimationEndRunnable.run();
        PopupNotificationActivity.access$002(PopupNotificationActivity.this, null);
      }
    }
  }
  
  private class FrameLayoutTouch
    extends FrameLayout
  {
    public FrameLayoutTouch(Context paramContext)
    {
      super();
    }
    
    public FrameLayoutTouch(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public FrameLayoutTouch(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
    }
    
    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      return (PopupNotificationActivity.this.checkTransitionAnimation()) || (((PopupNotificationActivity)getContext()).onTouchEventMy(paramMotionEvent));
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (PopupNotificationActivity.this.checkTransitionAnimation()) || (((PopupNotificationActivity)getContext()).onTouchEventMy(paramMotionEvent));
    }
    
    public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      ((PopupNotificationActivity)getContext()).onTouchEventMy(null);
      super.requestDisallowInterceptTouchEvent(paramBoolean);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PopupNotificationActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */