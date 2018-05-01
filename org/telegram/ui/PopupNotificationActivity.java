package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.PopupAudioView;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;
import org.telegram.ui.Components.SendingFileDrawable;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StatusDrawable;
import org.telegram.ui.Components.TypingDotsDrawable;

public class PopupNotificationActivity
  extends Activity
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int id_chat_compose_panel = 1000;
  private ActionBar actionBar;
  private boolean animationInProgress = false;
  private long animationStartTime = 0L;
  private ArrayList<ViewGroup> audioViews = new ArrayList();
  private FrameLayout avatarContainer;
  private BackupImageView avatarImageView;
  private ViewGroup centerButtonsView;
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
  private int lastResumedAccount = -1;
  private ViewGroup leftButtonsView;
  private ViewGroup leftView;
  private ViewGroup messageContainer;
  private float moveStartX = -1.0F;
  private TextView nameTextView;
  private Runnable onAnimationEndRunnable = null;
  private TextView onlineTextView;
  private RelativeLayout popupContainer;
  private ArrayList<MessageObject> popupMessages = new ArrayList();
  private ViewGroup rightButtonsView;
  private ViewGroup rightView;
  private boolean startedMoving = false;
  private StatusDrawable[] statusDrawables = new StatusDrawable[5];
  private ArrayList<ViewGroup> textViews = new ArrayList();
  private VelocityTracker velocityTracker = null;
  private PowerManager.WakeLock wakeLock = null;
  
  private void applyViewsLayoutParams(int paramInt)
  {
    int i = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
    FrameLayout.LayoutParams localLayoutParams;
    if (this.leftView != null)
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.leftView.getLayoutParams();
      if (localLayoutParams.width != i)
      {
        localLayoutParams.width = i;
        this.leftView.setLayoutParams(localLayoutParams);
      }
      this.leftView.setTranslationX(-i + paramInt);
    }
    if (this.leftButtonsView != null) {
      this.leftButtonsView.setTranslationX(-i + paramInt);
    }
    if (this.centerView != null)
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.centerView.getLayoutParams();
      if (localLayoutParams.width != i)
      {
        localLayoutParams.width = i;
        this.centerView.setLayoutParams(localLayoutParams);
      }
      this.centerView.setTranslationX(paramInt);
    }
    if (this.centerButtonsView != null) {
      this.centerButtonsView.setTranslationX(paramInt);
    }
    if (this.rightView != null)
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.rightView.getLayoutParams();
      if (localLayoutParams.width != i)
      {
        localLayoutParams.width = i;
        this.rightView.setLayoutParams(localLayoutParams);
      }
      this.rightView.setTranslationX(i + paramInt);
    }
    if (this.rightButtonsView != null) {
      this.rightButtonsView.setTranslationX(i + paramInt);
    }
    this.messageContainer.invalidate();
  }
  
  private void checkAndUpdateAvatar()
  {
    if (this.currentMessageObject == null) {}
    label199:
    for (;;)
    {
      return;
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject3 = null;
      Object localObject4 = null;
      if (this.currentChat != null)
      {
        localObject4 = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Integer.valueOf(this.currentChat.id));
        if (localObject4 != null)
        {
          this.currentChat = ((TLRPC.Chat)localObject4);
          if (this.currentChat.photo != null) {
            localObject3 = this.currentChat.photo.photo_small;
          }
          localObject4 = new AvatarDrawable(this.currentChat);
        }
      }
      else
      {
        for (;;)
        {
          if (this.avatarImageView == null) {
            break label199;
          }
          this.avatarImageView.setImage((TLObject)localObject3, "50_50", (Drawable)localObject4);
          break;
          localObject3 = localObject1;
          if (this.currentUser != null)
          {
            localObject4 = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
            if (localObject4 == null) {
              break;
            }
            this.currentUser = ((TLRPC.User)localObject4);
            localObject3 = localObject2;
            if (this.currentUser.photo != null) {
              localObject3 = this.currentUser.photo.photo_small;
            }
            localObject4 = new AvatarDrawable(this.currentUser);
          }
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
  
  private LinearLayout getButtonsViewForMessage(int paramInt, boolean paramBoolean)
  {
    if ((this.popupMessages.size() == 1) && ((paramInt < 0) || (paramInt >= this.popupMessages.size()))) {
      localObject1 = null;
    }
    int i;
    Object localObject2;
    label226:
    do
    {
      return (LinearLayout)localObject1;
      final MessageObject localMessageObject;
      int j;
      Object localObject3;
      int k;
      Object localObject4;
      int m;
      Object localObject5;
      int n;
      if (paramInt == -1)
      {
        i = this.popupMessages.size() - 1;
        localObject2 = null;
        localObject1 = null;
        localMessageObject = (MessageObject)this.popupMessages.get(i);
        j = 0;
        paramInt = 0;
        localObject3 = localMessageObject.messageOwner.reply_markup;
        k = j;
        if (localMessageObject.getDialogId() == 777000L)
        {
          k = j;
          if (localObject3 != null)
          {
            localObject4 = ((TLRPC.ReplyMarkup)localObject3).rows;
            j = 0;
            m = ((ArrayList)localObject4).size();
          }
        }
      }
      else
      {
        for (;;)
        {
          k = paramInt;
          if (j >= m) {
            break label226;
          }
          localObject5 = (TLRPC.TL_keyboardButtonRow)((ArrayList)localObject4).get(j);
          k = 0;
          n = ((TLRPC.TL_keyboardButtonRow)localObject5).buttons.size();
          for (;;)
          {
            if (k < n)
            {
              i1 = paramInt;
              if (((TLRPC.KeyboardButton)((TLRPC.TL_keyboardButtonRow)localObject5).buttons.get(k) instanceof TLRPC.TL_keyboardButtonCallback)) {
                i1 = paramInt + 1;
              }
              k++;
              paramInt = i1;
              continue;
              i = paramInt;
              if (paramInt != this.popupMessages.size()) {
                break;
              }
              i = 0;
              break;
            }
          }
          j++;
        }
      }
      final int i1 = localMessageObject.currentAccount;
      if (k > 0)
      {
        localObject3 = ((TLRPC.ReplyMarkup)localObject3).rows;
        paramInt = 0;
        m = ((ArrayList)localObject3).size();
        for (;;)
        {
          localObject2 = localObject1;
          if (paramInt >= m) {
            break;
          }
          localObject4 = (TLRPC.TL_keyboardButtonRow)((ArrayList)localObject3).get(paramInt);
          j = 0;
          n = ((TLRPC.TL_keyboardButtonRow)localObject4).buttons.size();
          for (localObject2 = localObject1; j < n; localObject2 = localObject1)
          {
            localObject5 = (TLRPC.KeyboardButton)((TLRPC.TL_keyboardButtonRow)localObject4).buttons.get(j);
            localObject1 = localObject2;
            if ((localObject5 instanceof TLRPC.TL_keyboardButtonCallback))
            {
              localObject1 = localObject2;
              if (localObject2 == null)
              {
                localObject1 = new LinearLayout(this);
                ((LinearLayout)localObject1).setOrientation(0);
                ((LinearLayout)localObject1).setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                ((LinearLayout)localObject1).setWeightSum(100.0F);
                ((LinearLayout)localObject1).setTag("b");
                ((LinearLayout)localObject1).setOnTouchListener(new View.OnTouchListener()
                {
                  public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
                  {
                    return true;
                  }
                });
              }
              localObject2 = new TextView(this);
              ((TextView)localObject2).setTextSize(1, 16.0F);
              ((TextView)localObject2).setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
              ((TextView)localObject2).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
              ((TextView)localObject2).setText(((TLRPC.KeyboardButton)localObject5).text.toUpperCase());
              ((TextView)localObject2).setTag(localObject5);
              ((TextView)localObject2).setGravity(17);
              ((TextView)localObject2).setBackgroundDrawable(Theme.getSelectorDrawable(true));
              ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-1, -1, 100.0F / k));
              ((TextView)localObject2).setOnClickListener(new View.OnClickListener()
              {
                public void onClick(View paramAnonymousView)
                {
                  paramAnonymousView = (TLRPC.KeyboardButton)paramAnonymousView.getTag();
                  if (paramAnonymousView != null) {
                    SendMessagesHelper.getInstance(i1).sendNotificationCallback(localMessageObject.getDialogId(), localMessageObject.getId(), paramAnonymousView.data);
                  }
                }
              });
            }
            j++;
          }
          paramInt++;
          localObject1 = localObject2;
        }
      }
      localObject1 = localObject2;
    } while (localObject2 == null);
    paramInt = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
    Object localObject1 = new RelativeLayout.LayoutParams(-1, -2);
    ((RelativeLayout.LayoutParams)localObject1).addRule(12);
    if (paramBoolean)
    {
      if (i != this.currentMessageNum) {
        break label584;
      }
      ((LinearLayout)localObject2).setTranslationX(0.0F);
    }
    for (;;)
    {
      this.popupContainer.addView((View)localObject2, (ViewGroup.LayoutParams)localObject1);
      localObject1 = localObject2;
      break;
      label584:
      if (i == this.currentMessageNum - 1) {
        ((LinearLayout)localObject2).setTranslationX(-paramInt);
      } else if (i == this.currentMessageNum + 1) {
        ((LinearLayout)localObject2).setTranslationX(paramInt);
      }
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
    int i = 0;
    int j;
    int k;
    if ((this.currentMessageNum == 0) && (!this.chatActivityEnterView.hasText()))
    {
      j = i;
      if (!this.startedMoving) {}
    }
    else
    {
      j = i;
      if (this.currentMessageObject != null)
      {
        k = 0;
        int m = this.popupMessages.size();
        label67:
        j = i;
        if (k < m)
        {
          MessageObject localMessageObject = (MessageObject)this.popupMessages.get(k);
          if ((localMessageObject.currentAccount != this.currentMessageObject.currentAccount) || (localMessageObject.getDialogId() != this.currentMessageObject.getDialogId()) || (localMessageObject.getId() != this.currentMessageObject.getId())) {
            break label215;
          }
          this.currentMessageNum = k;
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
      break;
      label215:
      k++;
      break label67;
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
      localObject1 = null;
    }
    int i;
    label46:
    MessageObject localMessageObject;
    label109:
    Object localObject2;
    BackupImageView localBackupImageView;
    Object localObject3;
    TLRPC.PhotoSize localPhotoSize;
    int j;
    label269:
    label309:
    do
    {
      return (ViewGroup)localObject1;
      if (paramInt != -1) {
        break;
      }
      i = this.popupMessages.size() - 1;
      localMessageObject = (MessageObject)this.popupMessages.get(i);
      if ((localMessageObject.type != 1) && (localMessageObject.type != 4)) {
        break label808;
      }
      if (this.imageViews.size() <= 0) {
        break label429;
      }
      localObject1 = (ViewGroup)this.imageViews.get(0);
      this.imageViews.remove(0);
      localObject2 = (TextView)((ViewGroup)localObject1).findViewWithTag(Integer.valueOf(312));
      localBackupImageView = (BackupImageView)((ViewGroup)localObject1).findViewWithTag(Integer.valueOf(311));
      localBackupImageView.setAspectFit(true);
      if (localMessageObject.type != 1) {
        break label665;
      }
      localObject3 = FileLoader.getClosestPhotoSizeWithSize(localMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
      localPhotoSize = FileLoader.getClosestPhotoSizeWithSize(localMessageObject.photoThumbs, 100);
      j = 0;
      paramInt = j;
      if (localObject3 != null)
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
        if ((paramInt == 0) && (!DownloadController.getInstance(localMessageObject.currentAccount).canDownloadMedia(localMessageObject))) {
          break label618;
        }
        localBackupImageView.setImage(((TLRPC.PhotoSize)localObject3).location, "100_100", localPhotoSize.location, ((TLRPC.PhotoSize)localObject3).size);
        paramInt = 1;
      }
      if (paramInt != 0) {
        break label646;
      }
      localBackupImageView.setVisibility(8);
      ((TextView)localObject2).setVisibility(0);
      ((TextView)localObject2).setTextSize(2, SharedConfig.fontSize);
      ((TextView)localObject2).setText(localMessageObject.messageText);
      localObject3 = localObject1;
      if (((ViewGroup)localObject3).getParent() == null) {
        this.messageContainer.addView((View)localObject3);
      }
      ((ViewGroup)localObject3).setVisibility(0);
      localObject1 = localObject3;
    } while (!paramBoolean);
    paramInt = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
    Object localObject1 = (FrameLayout.LayoutParams)((ViewGroup)localObject3).getLayoutParams();
    ((FrameLayout.LayoutParams)localObject1).gravity = 51;
    ((FrameLayout.LayoutParams)localObject1).height = -1;
    ((FrameLayout.LayoutParams)localObject1).width = paramInt;
    if (i == this.currentMessageNum) {
      ((ViewGroup)localObject3).setTranslationX(0.0F);
    }
    for (;;)
    {
      ((ViewGroup)localObject3).setLayoutParams((ViewGroup.LayoutParams)localObject1);
      ((ViewGroup)localObject3).invalidate();
      localObject1 = localObject3;
      break;
      i = paramInt;
      if (paramInt != this.popupMessages.size()) {
        break label46;
      }
      i = 0;
      break label46;
      label429:
      localObject1 = new FrameLayout(this);
      localObject3 = new FrameLayout(this);
      ((FrameLayout)localObject3).setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F));
      ((FrameLayout)localObject3).setBackgroundDrawable(Theme.getSelectorDrawable(false));
      ((ViewGroup)localObject1).addView((View)localObject3, LayoutHelper.createFrame(-1, -1.0F));
      localObject2 = new BackupImageView(this);
      ((BackupImageView)localObject2).setTag(Integer.valueOf(311));
      ((FrameLayout)localObject3).addView((View)localObject2, LayoutHelper.createFrame(-1, -1.0F));
      localObject2 = new TextView(this);
      ((TextView)localObject2).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      ((TextView)localObject2).setTextSize(1, 16.0F);
      ((TextView)localObject2).setGravity(17);
      ((TextView)localObject2).setTag(Integer.valueOf(312));
      ((FrameLayout)localObject3).addView((View)localObject2, LayoutHelper.createFrame(-1, -2, 17));
      ((ViewGroup)localObject1).setTag(Integer.valueOf(2));
      ((ViewGroup)localObject1).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          PopupNotificationActivity.this.openCurrentMessage();
        }
      });
      break label109;
      label618:
      paramInt = j;
      if (localPhotoSize == null) {
        break label269;
      }
      localBackupImageView.setImage(localPhotoSize.location, null, (Drawable)null);
      paramInt = 1;
      break label269;
      label646:
      localBackupImageView.setVisibility(0);
      ((TextView)localObject2).setVisibility(8);
      localObject3 = localObject1;
      break label309;
      label665:
      localObject3 = localObject1;
      if (localMessageObject.type != 4) {
        break label309;
      }
      ((TextView)localObject2).setVisibility(8);
      ((TextView)localObject2).setText(localMessageObject.messageText);
      localBackupImageView.setVisibility(0);
      double d1 = localMessageObject.messageOwner.media.geo.lat;
      double d2 = localMessageObject.messageOwner.media.geo._long;
      localBackupImageView.setImage(String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:big|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) }), null, null);
      localObject3 = localObject1;
      break label309;
      label808:
      if (localMessageObject.type == 2)
      {
        if (this.audioViews.size() > 0)
        {
          localObject1 = (ViewGroup)this.audioViews.get(0);
          this.audioViews.remove(0);
          localObject2 = (PopupAudioView)((ViewGroup)localObject1).findViewWithTag(Integer.valueOf(300));
        }
        for (;;)
        {
          ((PopupAudioView)localObject2).setMessageObject(localMessageObject);
          localObject3 = localObject1;
          if (!DownloadController.getInstance(localMessageObject.currentAccount).canDownloadMedia(localMessageObject)) {
            break;
          }
          ((PopupAudioView)localObject2).downloadAudioIfNeed();
          localObject3 = localObject1;
          break;
          localObject1 = new FrameLayout(this);
          localObject2 = new FrameLayout(this);
          ((FrameLayout)localObject2).setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F));
          ((FrameLayout)localObject2).setBackgroundDrawable(Theme.getSelectorDrawable(false));
          ((ViewGroup)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -1.0F));
          localObject3 = new FrameLayout(this);
          ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(-1, -2.0F, 17, 20.0F, 0.0F, 20.0F, 0.0F));
          localObject2 = new PopupAudioView(this);
          ((PopupAudioView)localObject2).setTag(Integer.valueOf(300));
          ((FrameLayout)localObject3).addView((View)localObject2);
          ((ViewGroup)localObject1).setTag(Integer.valueOf(3));
          ((ViewGroup)localObject1).setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              PopupNotificationActivity.this.openCurrentMessage();
            }
          });
        }
      }
      if (this.textViews.size() > 0)
      {
        localObject3 = (ViewGroup)this.textViews.get(0);
        this.textViews.remove(0);
      }
      for (;;)
      {
        localObject1 = (TextView)((ViewGroup)localObject3).findViewWithTag(Integer.valueOf(301));
        ((TextView)localObject1).setTextSize(2, SharedConfig.fontSize);
        ((TextView)localObject1).setText(localMessageObject.messageText);
        break;
        localObject3 = new FrameLayout(this);
        localObject2 = new ScrollView(this);
        ((ScrollView)localObject2).setFillViewport(true);
        ((ViewGroup)localObject3).addView((View)localObject2, LayoutHelper.createFrame(-1, -1.0F));
        localObject1 = new LinearLayout(this);
        ((LinearLayout)localObject1).setOrientation(0);
        ((LinearLayout)localObject1).setBackgroundDrawable(Theme.getSelectorDrawable(false));
        ((ScrollView)localObject2).addView((View)localObject1, LayoutHelper.createScroll(-1, -2, 1));
        ((LinearLayout)localObject1).setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F));
        ((LinearLayout)localObject1).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PopupNotificationActivity.this.openCurrentMessage();
          }
        });
        localObject2 = new TextView(this);
        ((TextView)localObject2).setTextSize(1, 16.0F);
        ((TextView)localObject2).setTag(Integer.valueOf(301));
        ((TextView)localObject2).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        ((TextView)localObject2).setLinkTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        ((TextView)localObject2).setGravity(17);
        ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-1, -2, 17));
        ((ViewGroup)localObject3).setTag(Integer.valueOf(1));
      }
      if (i == this.currentMessageNum - 1) {
        ((ViewGroup)localObject3).setTranslationX(-paramInt);
      } else if (i == this.currentMessageNum + 1) {
        ((ViewGroup)localObject3).setTranslationX(paramInt);
      }
    }
  }
  
  private void handleIntent(Intent paramIntent)
  {
    boolean bool;
    int i;
    if ((paramIntent != null) && (paramIntent.getBooleanExtra("force", false)))
    {
      bool = true;
      this.isReply = bool;
      this.popupMessages.clear();
      if (!this.isReply) {
        break label127;
      }
      if (paramIntent == null) {
        break label120;
      }
      i = paramIntent.getIntExtra("currentAccount", UserConfig.selectedAccount);
      label51:
      this.popupMessages.addAll(NotificationsController.getInstance(i).popupReplyMessages);
      label66:
      if ((!((KeyguardManager)getSystemService("keyguard")).inKeyguardRestrictedInputMode()) && (ApplicationLoader.isScreenOn)) {
        break label165;
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
      label120:
      i = UserConfig.selectedAccount;
      break label51;
      label127:
      for (i = 0; i < 3; i++) {
        if (UserConfig.getInstance(i).isClientActivated()) {
          this.popupMessages.addAll(NotificationsController.getInstance(i).popupMessages);
        }
      }
      break label66;
      label165:
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
      localIntent.putExtra("currentAccount", this.currentMessageObject.currentAccount);
      localIntent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
      localIntent.setFlags(32768);
      startActivity(localIntent);
      onFinish();
      finish();
      break;
      localIntent.putExtra("userId", i);
      continue;
      localIntent.putExtra("encId", (int)(l >> 32));
    }
  }
  
  private void prepareLayouts(int paramInt)
  {
    int i = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
    if (paramInt == 0)
    {
      reuseView(this.centerView);
      reuseView(this.leftView);
      reuseView(this.rightView);
      reuseButtonsView(this.centerButtonsView);
      reuseButtonsView(this.leftButtonsView);
      reuseButtonsView(this.rightButtonsView);
      paramInt = this.currentMessageNum - 1;
      if (paramInt < this.currentMessageNum + 2)
      {
        if (paramInt == this.currentMessageNum - 1)
        {
          this.leftView = getViewForMessage(paramInt, true);
          this.leftButtonsView = getButtonsViewForMessage(paramInt, true);
        }
        for (;;)
        {
          paramInt++;
          break;
          if (paramInt == this.currentMessageNum)
          {
            this.centerView = getViewForMessage(paramInt, true);
            this.centerButtonsView = getButtonsViewForMessage(paramInt, true);
          }
          else if (paramInt == this.currentMessageNum + 1)
          {
            this.rightView = getViewForMessage(paramInt, true);
            this.rightButtonsView = getButtonsViewForMessage(paramInt, true);
          }
        }
      }
    }
    else
    {
      if (paramInt != 1) {
        break label266;
      }
      reuseView(this.rightView);
      reuseButtonsView(this.rightButtonsView);
      this.rightView = this.centerView;
      this.centerView = this.leftView;
      this.leftView = getViewForMessage(this.currentMessageNum - 1, true);
      this.rightButtonsView = this.centerButtonsView;
      this.centerButtonsView = this.leftButtonsView;
      this.leftButtonsView = getButtonsViewForMessage(this.currentMessageNum - 1, true);
    }
    for (;;)
    {
      return;
      label266:
      if (paramInt == 2)
      {
        reuseView(this.leftView);
        reuseButtonsView(this.leftButtonsView);
        this.leftView = this.centerView;
        this.centerView = this.rightView;
        this.rightView = getViewForMessage(this.currentMessageNum + 1, true);
        this.leftButtonsView = this.centerButtonsView;
        this.centerButtonsView = this.rightButtonsView;
        this.rightButtonsView = getButtonsViewForMessage(this.currentMessageNum + 1, true);
      }
      else
      {
        float f;
        Object localObject;
        if (paramInt == 3)
        {
          if (this.rightView != null)
          {
            f = this.rightView.getTranslationX();
            reuseView(this.rightView);
            localObject = getViewForMessage(this.currentMessageNum + 1, false);
            this.rightView = ((ViewGroup)localObject);
            if (localObject != null)
            {
              localObject = (FrameLayout.LayoutParams)this.rightView.getLayoutParams();
              ((FrameLayout.LayoutParams)localObject).width = i;
              this.rightView.setLayoutParams((ViewGroup.LayoutParams)localObject);
              this.rightView.setTranslationX(f);
              this.rightView.invalidate();
            }
          }
          if (this.rightButtonsView != null)
          {
            f = this.rightButtonsView.getTranslationX();
            reuseButtonsView(this.rightButtonsView);
            localObject = getButtonsViewForMessage(this.currentMessageNum + 1, false);
            this.rightButtonsView = ((ViewGroup)localObject);
            if (localObject != null) {
              this.rightButtonsView.setTranslationX(f);
            }
          }
        }
        else if (paramInt == 4)
        {
          if (this.leftView != null)
          {
            f = this.leftView.getTranslationX();
            reuseView(this.leftView);
            localObject = getViewForMessage(0, false);
            this.leftView = ((ViewGroup)localObject);
            if (localObject != null)
            {
              localObject = (FrameLayout.LayoutParams)this.leftView.getLayoutParams();
              ((FrameLayout.LayoutParams)localObject).width = i;
              this.leftView.setLayoutParams((ViewGroup.LayoutParams)localObject);
              this.leftView.setTranslationX(f);
              this.leftView.invalidate();
            }
          }
          if (this.leftButtonsView != null)
          {
            f = this.leftButtonsView.getTranslationX();
            reuseButtonsView(this.leftButtonsView);
            localObject = getButtonsViewForMessage(0, false);
            this.leftButtonsView = ((ViewGroup)localObject);
            if (localObject != null) {
              this.leftButtonsView.setTranslationX(f);
            }
          }
        }
      }
    }
  }
  
  private void reuseButtonsView(ViewGroup paramViewGroup)
  {
    if (paramViewGroup == null) {}
    for (;;)
    {
      return;
      this.popupContainer.removeView(paramViewGroup);
    }
  }
  
  private void reuseView(ViewGroup paramViewGroup)
  {
    if (paramViewGroup == null) {}
    for (;;)
    {
      return;
      int i = ((Integer)paramViewGroup.getTag()).intValue();
      paramViewGroup.setVisibility(8);
      if (i == 1) {
        this.textViews.add(paramViewGroup);
      } else if (i == 2) {
        this.imageViews.add(paramViewGroup);
      } else if (i == 3) {
        this.audioViews.add(paramViewGroup);
      }
    }
  }
  
  private void setTypingAnimation(boolean paramBoolean)
  {
    if (this.actionBar == null) {}
    for (;;)
    {
      return;
      int i;
      if (paramBoolean)
      {
        try
        {
          Integer localInteger = (Integer)MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStringsTypes.get(this.currentMessageObject.getDialogId());
          this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[localInteger.intValue()], null, null, null);
          this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
          i = 0;
          if (i >= this.statusDrawables.length) {
            continue;
          }
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
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
      else
      {
        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        this.onlineTextView.setCompoundDrawablePadding(0);
        for (i = 0; i < this.statusDrawables.length; i++) {
          this.statusDrawables[i].stop();
        }
      }
    }
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
    if (this.lastResumedAccount != this.currentMessageObject.currentAccount)
    {
      if (this.lastResumedAccount >= 0) {
        ConnectionsManager.getInstance(this.lastResumedAccount).setAppPaused(true, false);
      }
      this.lastResumedAccount = this.currentMessageObject.currentAccount;
      ConnectionsManager.getInstance(this.lastResumedAccount).setAppPaused(false, false);
    }
    this.currentChat = null;
    this.currentUser = null;
    long l = this.currentMessageObject.getDialogId();
    this.chatActivityEnterView.setDialogId(l, this.currentMessageObject.currentAccount);
    int i;
    if ((int)l != 0)
    {
      i = (int)l;
      if (i > 0)
      {
        this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(i));
        label133:
        if ((this.currentChat == null) || (this.currentUser == null)) {
          break label322;
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
      break;
      this.currentChat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Integer.valueOf(-i));
      this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
      break label133;
      TLRPC.EncryptedChat localEncryptedChat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getEncryptedChat(Integer.valueOf((int)(l >> 32)));
      this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(localEncryptedChat.user_id));
      break label133;
      label322:
      if (this.currentUser != null)
      {
        this.nameTextView.setText(UserObject.getUserName(this.currentUser));
        if ((int)l == 0)
        {
          this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(NUM, 0, 0, 0);
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
    if ((this.actionBar == null) || (this.currentMessageObject == null)) {}
    for (;;)
    {
      return;
      if ((this.currentChat == null) && (this.currentUser != null))
      {
        if ((this.currentUser.id / 1000 != 777) && (this.currentUser.id / 1000 != 333) && (ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.get(Integer.valueOf(this.currentUser.id)) == null) && ((ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.size() != 0) || (!ContactsController.getInstance(this.currentMessageObject.currentAccount).isLoadingContacts()))) {
          if ((this.currentUser.phone != null) && (this.currentUser.phone.length() != 0)) {
            this.nameTextView.setText(PhoneFormat.getInstance().format("+" + this.currentUser.phone));
          }
        }
        for (;;)
        {
          if ((this.currentUser == null) || (this.currentUser.id != 777000)) {
            break label262;
          }
          this.onlineTextView.setText(LocaleController.getString("ServiceNotifications", NUM));
          break;
          this.nameTextView.setText(UserObject.getUserName(this.currentUser));
          continue;
          this.nameTextView.setText(UserObject.getUserName(this.currentUser));
        }
        label262:
        Object localObject = (CharSequence)MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
        if ((localObject == null) || (((CharSequence)localObject).length() == 0))
        {
          this.lastPrintString = null;
          setTypingAnimation(false);
          localObject = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
          if (localObject != null) {
            this.currentUser = ((TLRPC.User)localObject);
          }
          this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentMessageObject.currentAccount, this.currentUser));
        }
        else
        {
          this.lastPrintString = ((CharSequence)localObject);
          this.onlineTextView.setText((CharSequence)localObject);
          setTypingAnimation(true);
        }
      }
    }
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
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.appDidLogout) {
      if (paramInt2 == this.lastResumedAccount)
      {
        onFinish();
        finish();
      }
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.pushMessagesUpdated)
      {
        if (!this.isReply)
        {
          this.popupMessages.clear();
          for (paramInt1 = 0; paramInt1 < 3; paramInt1++) {
            if (UserConfig.getInstance(paramInt1).isClientActivated()) {
              this.popupMessages.addAll(NotificationsController.getInstance(paramInt1).popupMessages);
            }
          }
          getNewMessage();
        }
      }
      else if (paramInt1 == NotificationCenter.updateInterfaces)
      {
        if ((this.currentMessageObject != null) && (paramInt2 == this.lastResumedAccount))
        {
          paramInt1 = ((Integer)paramVarArgs[0]).intValue();
          if (((paramInt1 & 0x1) != 0) || ((paramInt1 & 0x4) != 0) || ((paramInt1 & 0x10) != 0) || ((paramInt1 & 0x20) != 0)) {
            updateSubtitle();
          }
          if (((paramInt1 & 0x2) != 0) || ((paramInt1 & 0x8) != 0)) {
            checkAndUpdateAvatar();
          }
          if ((paramInt1 & 0x40) != 0)
          {
            paramVarArgs = (CharSequence)MessagesController.getInstance(this.currentMessageObject.currentAccount).printingStrings.get(this.currentMessageObject.getDialogId());
            if (((this.lastPrintString != null) && (paramVarArgs == null)) || ((this.lastPrintString == null) && (paramVarArgs != null)) || ((this.lastPrintString != null) && (paramVarArgs != null) && (!this.lastPrintString.equals(paramVarArgs)))) {
              updateSubtitle();
            }
          }
        }
      }
      else
      {
        int i;
        Object localObject1;
        Object localObject2;
        if (paramInt1 == NotificationCenter.messagePlayingDidReset)
        {
          paramVarArgs = (Integer)paramVarArgs[0];
          if (this.messageContainer != null)
          {
            i = this.messageContainer.getChildCount();
            for (paramInt1 = 0;; paramInt1++)
            {
              if (paramInt1 >= i) {
                break label378;
              }
              localObject1 = this.messageContainer.getChildAt(paramInt1);
              if (((Integer)((View)localObject1).getTag()).intValue() == 3)
              {
                localObject2 = (PopupAudioView)((View)localObject1).findViewWithTag(Integer.valueOf(300));
                localObject1 = ((PopupAudioView)localObject2).getMessageObject();
                if ((localObject1 != null) && (((MessageObject)localObject1).currentAccount == paramInt2) && (((MessageObject)localObject1).getId() == paramVarArgs.intValue()))
                {
                  ((PopupAudioView)localObject2).updateButtonState();
                  break;
                }
              }
            }
          }
        }
        else
        {
          label378:
          if (paramInt1 == NotificationCenter.messagePlayingProgressDidChanged)
          {
            paramVarArgs = (Integer)paramVarArgs[0];
            if (this.messageContainer != null)
            {
              i = this.messageContainer.getChildCount();
              for (paramInt1 = 0;; paramInt1++)
              {
                if (paramInt1 >= i) {
                  break label504;
                }
                localObject1 = this.messageContainer.getChildAt(paramInt1);
                if (((Integer)((View)localObject1).getTag()).intValue() == 3)
                {
                  localObject1 = (PopupAudioView)((View)localObject1).findViewWithTag(Integer.valueOf(300));
                  localObject2 = ((PopupAudioView)localObject1).getMessageObject();
                  if ((localObject2 != null) && (((MessageObject)localObject2).currentAccount == paramInt2) && (((MessageObject)localObject2).getId() == paramVarArgs.intValue()))
                  {
                    ((PopupAudioView)localObject1).updateProgress();
                    break;
                  }
                }
              }
            }
          }
          else
          {
            label504:
            if (paramInt1 == NotificationCenter.emojiDidLoaded)
            {
              if (this.messageContainer != null)
              {
                paramInt2 = this.messageContainer.getChildCount();
                for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
                {
                  paramVarArgs = this.messageContainer.getChildAt(paramInt1);
                  if (((Integer)paramVarArgs.getTag()).intValue() == 1)
                  {
                    paramVarArgs = (TextView)paramVarArgs.findViewWithTag(Integer.valueOf(301));
                    if (paramVarArgs != null) {
                      paramVarArgs.invalidate();
                    }
                  }
                }
              }
            }
            else if ((paramInt1 == NotificationCenter.contactsDidLoaded) && (paramInt2 == this.lastResumedAccount)) {
              updateSubtitle();
            }
          }
        }
      }
    }
  }
  
  public void onBackPressed()
  {
    if (this.chatActivityEnterView.isPopupShowing()) {
      this.chatActivityEnterView.hidePopup(true);
    }
    for (;;)
    {
      return;
      super.onBackPressed();
    }
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
    Theme.createChatResources(this, false);
    int i = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (i > 0) {
      AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(i);
    }
    for (i = 0; i < 3; i++)
    {
      NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.appDidLogout);
      NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
      NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
      NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.contactsDidLoaded);
    }
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    this.classGuid = ConnectionsManager.generateClassGuid();
    this.statusDrawables[0] = new TypingDotsDrawable();
    this.statusDrawables[1] = new RecordStatusDrawable();
    this.statusDrawables[2] = new SendingFileDrawable();
    this.statusDrawables[3] = new PlayingGameDrawable();
    this.statusDrawables[4] = new RoundStatusDrawable();
    paramBundle = new SizeNotifierFrameLayout(this)
    {
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        int i = getChildCount();
        if (getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) {}
        View localView;
        for (int j = PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();; j = 0) {
          for (int k = 0;; k++)
          {
            if (k >= i) {
              break label453;
            }
            localView = getChildAt(k);
            if (localView.getVisibility() != 8) {
              break;
            }
          }
        }
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        int m = localView.getMeasuredWidth();
        int n = localView.getMeasuredHeight();
        int i1 = localLayoutParams.gravity;
        int i2 = i1;
        if (i1 == -1) {
          i2 = 51;
        }
        switch (i2 & 0x7 & 0x7)
        {
        default: 
          i1 = localLayoutParams.leftMargin;
          label155:
          switch (i2 & 0x70)
          {
          default: 
            i2 = localLayoutParams.topMargin;
            label203:
            if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(localView)) {
              if (j != 0) {
                i2 = getMeasuredHeight() - j;
              }
            }
            break;
          }
          break;
        }
        for (;;)
        {
          localView.layout(i1, i2, i1 + m, i2 + n);
          break;
          i1 = (paramAnonymousInt3 - paramAnonymousInt1 - m) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
          break label155;
          i1 = paramAnonymousInt3 - m - localLayoutParams.rightMargin;
          break label155;
          i2 = localLayoutParams.topMargin;
          break label203;
          i2 = (paramAnonymousInt4 - j - paramAnonymousInt2 - n) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
          break label203;
          i2 = paramAnonymousInt4 - j - paramAnonymousInt2 - n - localLayoutParams.bottomMargin;
          break label203;
          i2 = getMeasuredHeight();
          continue;
          if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(localView))
          {
            i2 = PopupNotificationActivity.this.popupContainer.getTop() + PopupNotificationActivity.this.popupContainer.getMeasuredHeight() - localView.getMeasuredHeight() - localLayoutParams.bottomMargin;
            i1 = PopupNotificationActivity.this.popupContainer.getLeft() + PopupNotificationActivity.this.popupContainer.getMeasuredWidth() - localView.getMeasuredWidth() - localLayoutParams.rightMargin;
          }
        }
        label453:
        notifyHeightChanged();
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        View.MeasureSpec.getMode(paramAnonymousInt1);
        View.MeasureSpec.getMode(paramAnonymousInt2);
        int i = View.MeasureSpec.getSize(paramAnonymousInt1);
        int j = View.MeasureSpec.getSize(paramAnonymousInt2);
        setMeasuredDimension(i, j);
        int k = j;
        if (getKeyboardHeight() <= AndroidUtilities.dp(20.0F)) {
          k = j - PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
        }
        int m = getChildCount();
        j = 0;
        if (j < m)
        {
          View localView = getChildAt(j);
          if (localView.getVisibility() == 8) {}
          for (;;)
          {
            j++;
            break;
            if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(localView)) {
              localView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(localView.getLayoutParams().height, NUM));
            } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(localView)) {
              measureChildWithMargins(localView, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
            } else {
              localView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(2.0F) + k), NUM));
            }
          }
        }
      }
    };
    setContentView(paramBundle);
    paramBundle.setBackgroundColor(-NUM);
    RelativeLayout localRelativeLayout = new RelativeLayout(this);
    paramBundle.addView(localRelativeLayout, LayoutHelper.createFrame(-1, -1.0F));
    this.popupContainer = new RelativeLayout(this)
    {
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        for (paramAnonymousInt1 = 0; paramAnonymousInt1 < getChildCount(); paramAnonymousInt1++)
        {
          View localView = getChildAt(paramAnonymousInt1);
          if ((localView.getTag() instanceof String)) {
            localView.layout(localView.getLeft(), PopupNotificationActivity.this.chatActivityEnterView.getTop() + AndroidUtilities.dp(3.0F), localView.getRight(), PopupNotificationActivity.this.chatActivityEnterView.getBottom());
          }
        }
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        super.onMeasure(paramAnonymousInt1, paramAnonymousInt2);
        paramAnonymousInt2 = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredWidth();
        int i = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredHeight();
        for (paramAnonymousInt1 = 0; paramAnonymousInt1 < getChildCount(); paramAnonymousInt1++)
        {
          View localView = getChildAt(paramAnonymousInt1);
          if ((localView.getTag() instanceof String)) {
            localView.measure(View.MeasureSpec.makeMeasureSpec(paramAnonymousInt2, NUM), View.MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp(3.0F), NUM));
          }
        }
      }
    };
    this.popupContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    localRelativeLayout.addView(this.popupContainer, LayoutHelper.createRelative(-1, 240, 12, 0, 12, 0, 13));
    if (this.chatActivityEnterView != null) {
      this.chatActivityEnterView.onDestroy();
    }
    this.chatActivityEnterView = new ChatActivityEnterView(this, paramBundle, null, false);
    this.chatActivityEnterView.setId(1000);
    this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
    this.chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate()
    {
      public void didPressedAttachButton() {}
      
      public void needChangeVideoPreviewState(int paramAnonymousInt, float paramAnonymousFloat) {}
      
      public void needSendTyping()
      {
        if (PopupNotificationActivity.this.currentMessageObject != null) {
          MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, PopupNotificationActivity.this.classGuid);
        }
      }
      
      public void needShowMediaBanHint() {}
      
      public void needStartRecordAudio(int paramAnonymousInt) {}
      
      public void needStartRecordVideo(int paramAnonymousInt) {}
      
      public void onAttachButtonHidden() {}
      
      public void onAttachButtonShow() {}
      
      public void onMessageEditEnd(boolean paramAnonymousBoolean) {}
      
      public void onMessageSend(CharSequence paramAnonymousCharSequence)
      {
        if (PopupNotificationActivity.this.currentMessageObject == null) {}
        for (;;)
        {
          return;
          if ((PopupNotificationActivity.this.currentMessageNum >= 0) && (PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size())) {
            PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
          }
          MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, 0, true);
          PopupNotificationActivity.access$202(PopupNotificationActivity.this, null);
          PopupNotificationActivity.this.getNewMessage();
        }
      }
      
      public void onPreAudioVideoRecord() {}
      
      public void onStickersExpandedChange() {}
      
      public void onStickersTab(boolean paramAnonymousBoolean) {}
      
      public void onSwitchRecordMode(boolean paramAnonymousBoolean) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, boolean paramAnonymousBoolean) {}
      
      public void onWindowSizeChanged(int paramAnonymousInt) {}
    });
    this.messageContainer = new FrameLayoutTouch(this);
    this.popupContainer.addView(this.messageContainer, 0);
    this.actionBar = new ActionBar(this);
    this.actionBar.setOccupyStatusBar(false);
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setBackgroundColor(Theme.getColor("actionBarDefault"));
    this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), false);
    this.popupContainer.addView(this.actionBar);
    paramBundle = this.actionBar.getLayoutParams();
    paramBundle.width = -1;
    this.actionBar.setLayoutParams(paramBundle);
    paramBundle = this.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.dp(56.0F));
    this.countText = new TextView(this);
    this.countText.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
    this.countText.setTextSize(1, 14.0F);
    this.countText.setGravity(17);
    paramBundle.addView(this.countText, LayoutHelper.createFrame(56, -1.0F));
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
    this.nameTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
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
    this.onlineTextView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
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
        for (;;)
        {
          return;
          if (paramAnonymousInt == 1) {
            PopupNotificationActivity.this.openCurrentMessage();
          } else if (paramAnonymousInt == 2) {
            PopupNotificationActivity.this.switchToNextMessage();
          }
        }
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
    MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, false);
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
    for (;;)
    {
      return;
      this.finished = true;
      if (this.isReply) {
        this.popupMessages.clear();
      }
      for (int i = 0; i < 3; i++)
      {
        NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.contactsDidLoaded);
      }
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
      if (this.chatActivityEnterView != null) {
        this.chatActivityEnterView.onDestroy();
      }
      if (this.wakeLock.isHeld()) {
        this.wakeLock.release();
      }
    }
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
    if (this.lastResumedAccount >= 0) {
      ConnectionsManager.getInstance(this.lastResumedAccount).setAppPaused(true, false);
    }
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
    if ((paramInt != 3) || (paramArrayOfInt[0] == 0)) {}
    for (;;)
    {
      return;
      paramArrayOfString = new AlertDialog.Builder(this);
      paramArrayOfString.setTitle(LocaleController.getString("AppName", NUM));
      paramArrayOfString.setMessage(LocaleController.getString("PermissionNoAudio", NUM));
      paramArrayOfString.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener()
      {
        @TargetApi(9)
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          try
          {
            Intent localIntent = new android/content/Intent;
            localIntent.<init>("android.settings.APPLICATION_DETAILS_SETTINGS");
            paramAnonymousDialogInterface = new java/lang/StringBuilder;
            paramAnonymousDialogInterface.<init>();
            localIntent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            PopupNotificationActivity.this.startActivity(localIntent);
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
      });
      paramArrayOfString.setPositiveButton(LocaleController.getString("OK", NUM), null);
      paramArrayOfString.show();
    }
  }
  
  protected void onResume()
  {
    super.onResume();
    MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, true);
    if (this.chatActivityEnterView != null) {
      this.chatActivityEnterView.setFieldFocused(true);
    }
    fixLayout();
    checkAndUpdateAvatar();
    this.wakeLock.acquire(7000L);
  }
  
  public boolean onTouchEventMy(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if (checkTransitionAnimation())
    {
      bool = false;
      return bool;
    }
    if ((paramMotionEvent != null) && (paramMotionEvent.getAction() == 0)) {
      this.moveStartX = paramMotionEvent.getX();
    }
    float f;
    int i;
    int j;
    label215:
    label223:
    label225:
    do
    {
      for (;;)
      {
        bool = this.startedMoving;
        break;
        if ((paramMotionEvent == null) || (paramMotionEvent.getAction() != 2)) {
          break label225;
        }
        f = paramMotionEvent.getX();
        i = (int)(f - this.moveStartX);
        j = i;
        if (this.moveStartX != -1.0F)
        {
          j = i;
          if (!this.startedMoving)
          {
            j = i;
            if (Math.abs(i) > AndroidUtilities.dp(10.0F))
            {
              this.startedMoving = true;
              this.moveStartX = f;
              AndroidUtilities.lockOrientation(this);
              j = 0;
              if (this.velocityTracker != null) {
                break label215;
              }
              this.velocityTracker = VelocityTracker.obtain();
            }
          }
        }
        for (;;)
        {
          if (!this.startedMoving) {
            break label223;
          }
          i = j;
          if (this.leftView == null)
          {
            i = j;
            if (j > 0) {
              i = 0;
            }
          }
          j = i;
          if (this.rightView == null)
          {
            j = i;
            if (i < 0) {
              j = 0;
            }
          }
          if (this.velocityTracker != null) {
            this.velocityTracker.addMovement(paramMotionEvent);
          }
          applyViewsLayoutParams(j);
          break;
          this.velocityTracker.clear();
        }
      }
    } while ((paramMotionEvent != null) && (paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3));
    int k;
    int m;
    ViewGroup localViewGroup;
    if ((paramMotionEvent != null) && (this.startedMoving))
    {
      k = (int)(paramMotionEvent.getX() - this.moveStartX);
      m = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
      f = 0.0F;
      i = 0;
      localViewGroup = null;
      paramMotionEvent = null;
      j = i;
      if (this.velocityTracker != null)
      {
        this.velocityTracker.computeCurrentVelocity(1000);
        if (this.velocityTracker.getXVelocity() >= 3500.0F) {
          j = 1;
        }
      }
      else
      {
        label330:
        if (((j != 1) && (k <= m / 3)) || (this.leftView == null)) {
          break label654;
        }
        f = m - this.centerView.getTranslationX();
        localViewGroup = this.leftView;
        paramMotionEvent = this.leftButtonsView;
        this.onAnimationEndRunnable = new Runnable()
        {
          public void run()
          {
            PopupNotificationActivity.access$902(PopupNotificationActivity.this, false);
            PopupNotificationActivity.this.switchToPreviousMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
          }
        };
        label387:
        if (f != 0.0F)
        {
          j = (int)(Math.abs(f / m) * 200.0F);
          ArrayList localArrayList = new ArrayList();
          localArrayList.add(ObjectAnimator.ofFloat(this.centerView, "translationX", new float[] { this.centerView.getTranslationX() + f }));
          if (this.centerButtonsView != null) {
            localArrayList.add(ObjectAnimator.ofFloat(this.centerButtonsView, "translationX", new float[] { this.centerButtonsView.getTranslationX() + f }));
          }
          if (localViewGroup != null) {
            localArrayList.add(ObjectAnimator.ofFloat(localViewGroup, "translationX", new float[] { localViewGroup.getTranslationX() + f }));
          }
          if (paramMotionEvent != null) {
            localArrayList.add(ObjectAnimator.ofFloat(paramMotionEvent, "translationX", new float[] { paramMotionEvent.getTranslationX() + f }));
          }
          paramMotionEvent = new AnimatorSet();
          paramMotionEvent.playTogether(localArrayList);
          paramMotionEvent.setDuration(j);
          paramMotionEvent.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if (PopupNotificationActivity.this.onAnimationEndRunnable != null)
              {
                PopupNotificationActivity.this.onAnimationEndRunnable.run();
                PopupNotificationActivity.access$1202(PopupNotificationActivity.this, null);
              }
            }
          });
          paramMotionEvent.start();
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
      j = i;
      if (this.velocityTracker.getXVelocity() > -3500.0F) {
        break label330;
      }
      j = 2;
      break label330;
      label654:
      if (((j == 2) || (k < -m / 3)) && (this.rightView != null))
      {
        f = -m - this.centerView.getTranslationX();
        localViewGroup = this.rightView;
        paramMotionEvent = this.rightButtonsView;
        this.onAnimationEndRunnable = new Runnable()
        {
          public void run()
          {
            PopupNotificationActivity.access$902(PopupNotificationActivity.this, false);
            PopupNotificationActivity.this.switchToNextMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
          }
        };
        break label387;
      }
      if (this.centerView.getTranslationX() == 0.0F) {
        break label387;
      }
      f = -this.centerView.getTranslationX();
      if (k > 0)
      {
        localViewGroup = this.leftView;
        label748:
        if (k <= 0) {
          break label782;
        }
      }
      label782:
      for (paramMotionEvent = this.leftButtonsView;; paramMotionEvent = this.rightButtonsView)
      {
        this.onAnimationEndRunnable = new Runnable()
        {
          public void run()
          {
            PopupNotificationActivity.access$902(PopupNotificationActivity.this, false);
            PopupNotificationActivity.this.applyViewsLayoutParams(0);
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
          }
        };
        break;
        localViewGroup = this.rightView;
        break label748;
      }
      applyViewsLayoutParams(0);
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
      if ((PopupNotificationActivity.this.checkTransitionAnimation()) || (((PopupNotificationActivity)getContext()).onTouchEventMy(paramMotionEvent))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if ((PopupNotificationActivity.this.checkTransitionAnimation()) || (((PopupNotificationActivity)getContext()).onTouchEventMy(paramMotionEvent))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
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