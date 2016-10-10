package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.messenger.query.MessagesQuery;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_keyboardButton;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestPhone;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.StickersActivity;

public class ChatActivityEnterView
  extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StickersAlert.StickersAlertDelegate
{
  private boolean allowGifs;
  private boolean allowShowTopView;
  private boolean allowStickers;
  private LinearLayout attachButton;
  private int audioInterfaceState;
  private ImageView audioSendButton;
  private TLRPC.TL_document audioToSend;
  private MessageObject audioToSendMessageObject;
  private String audioToSendPath;
  private Drawable backgroundDrawable;
  private ImageView botButton;
  private MessageObject botButtonsMessageObject;
  private int botCount;
  private PopupWindow botKeyboardPopup;
  private BotKeyboardView botKeyboardView;
  private MessageObject botMessageObject;
  private TLRPC.TL_replyKeyboardMarkup botReplyMarkup;
  private boolean canWriteToChannel;
  private ImageView cancelBotButton;
  private int currentPopupContentType = -1;
  private AnimatorSet currentTopViewAnimation;
  private ChatActivityEnterViewDelegate delegate;
  private long dialog_id;
  private float distCanMove = AndroidUtilities.dp(80.0F);
  private Drawable dotDrawable;
  private boolean editingCaption;
  private MessageObject editingMessageObject;
  private int editingMessageReqId;
  private ImageView emojiButton;
  private int emojiPadding;
  private EmojiView emojiView;
  private boolean forceShowSendButton;
  private boolean hasBotCommands;
  private boolean ignoreTextChange;
  private int innerTextChange;
  private boolean isPaused = true;
  private int keyboardHeight;
  private int keyboardHeightLand;
  private boolean keyboardVisible;
  private int lastSizeChangeValue1;
  private boolean lastSizeChangeValue2;
  private String lastTimeString;
  private long lastTypingTimeSend;
  private PowerManager.WakeLock mWakeLock;
  private EditTextCaption messageEditText;
  private TLRPC.WebPage messageWebPage;
  private boolean messageWebPageSearch = true;
  private boolean needShowTopView;
  private ImageView notifyButton;
  private Runnable openKeyboardRunnable = new Runnable()
  {
    public void run()
    {
      if ((ChatActivityEnterView.this.messageEditText != null) && (ChatActivityEnterView.this.waitingForKeyboardOpen) && (!ChatActivityEnterView.this.keyboardVisible) && (!AndroidUtilities.usingHardwareInput) && (!AndroidUtilities.isInMultiwindow))
      {
        ChatActivityEnterView.this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
        AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
        AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100L);
      }
    }
  };
  private Activity parentActivity;
  private ChatActivity parentFragment;
  private TLRPC.KeyboardButton pendingLocationButton;
  private MessageObject pendingMessageObject;
  private CloseProgressDrawable2 progressDrawable;
  private RecordCircle recordCircle;
  private RecordDot recordDot;
  private FrameLayout recordPanel;
  private TextView recordTimeText;
  private FrameLayout recordedAudioPanel;
  private ImageView recordedAudioPlayButton;
  private SeekBarWaveformView recordedAudioSeekBar;
  private TextView recordedAudioTimeTextView;
  private boolean recordingAudio;
  private MessageObject replyingMessageObject;
  private AnimatorSet runningAnimation;
  private AnimatorSet runningAnimation2;
  private AnimatorSet runningAnimationAudio;
  private int runningAnimationType;
  private ImageView sendButton;
  private FrameLayout sendButtonContainer;
  private boolean sendByEnter;
  private boolean showKeyboardOnResume;
  private boolean silent;
  private SizeNotifierFrameLayout sizeNotifierLayout;
  private LinearLayout slideText;
  private float startedDraggingX = -1.0F;
  private LinearLayout textFieldContainer;
  private View topView;
  private boolean topViewShowed;
  private boolean waitingForKeyboardOpen;
  
  public ChatActivityEnterView(Activity paramActivity, SizeNotifierFrameLayout paramSizeNotifierFrameLayout, ChatActivity paramChatActivity, boolean paramBoolean)
  {
    super(paramActivity);
    this.backgroundDrawable = paramActivity.getResources().getDrawable(2130837623);
    this.dotDrawable = paramActivity.getResources().getDrawable(2130837550);
    setFocusable(true);
    setFocusableInTouchMode(true);
    setWillNotDraw(false);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStarted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStartError);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStopped);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordProgressChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidSent);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioRouteChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    this.parentActivity = paramActivity;
    this.parentFragment = paramChatActivity;
    this.sizeNotifierLayout = paramSizeNotifierFrameLayout;
    this.sizeNotifierLayout.setDelegate(this);
    this.sendByEnter = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("send_by_enter", false);
    this.textFieldContainer = new LinearLayout(paramActivity);
    this.textFieldContainer.setOrientation(0);
    addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0F, 51, 0.0F, 2.0F, 0.0F, 0.0F));
    paramSizeNotifierFrameLayout = new FrameLayout(paramActivity);
    this.textFieldContainer.addView(paramSizeNotifierFrameLayout, LayoutHelper.createLinear(0, -2, 1.0F));
    this.emojiButton = new ImageView(paramActivity)
    {
      protected void onDraw(Canvas paramAnonymousCanvas)
      {
        super.onDraw(paramAnonymousCanvas);
        if (((ChatActivityEnterView.this.emojiView == null) || (ChatActivityEnterView.this.emojiView.getVisibility() != 0)) && (!StickersQuery.getUnreadStickerSets().isEmpty()) && (ChatActivityEnterView.this.dotDrawable != null))
        {
          int i = paramAnonymousCanvas.getWidth() / 2 + AndroidUtilities.dp(4.0F);
          int j = paramAnonymousCanvas.getHeight() / 2 - AndroidUtilities.dp(13.0F);
          ChatActivityEnterView.this.dotDrawable.setBounds(i, j, ChatActivityEnterView.this.dotDrawable.getIntrinsicWidth() + i, ChatActivityEnterView.this.dotDrawable.getIntrinsicHeight() + j);
          ChatActivityEnterView.this.dotDrawable.draw(paramAnonymousCanvas);
        }
      }
    };
    this.emojiButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    this.emojiButton.setPadding(0, AndroidUtilities.dp(1.0F), 0, 0);
    if (Build.VERSION.SDK_INT >= 21) {
      this.emojiButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(-2697514));
    }
    setEmojiButtonImage();
    paramSizeNotifierFrameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48.0F, 83, 3.0F, 0.0F, 0.0F, 0.0F));
    this.emojiButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        boolean bool = true;
        if ((!ChatActivityEnterView.this.isPopupShowing()) || (ChatActivityEnterView.this.currentPopupContentType != 0))
        {
          ChatActivityEnterView.this.showPopup(1, 0);
          paramAnonymousView = ChatActivityEnterView.this.emojiView;
          if (ChatActivityEnterView.this.messageEditText.length() > 0) {}
          for (;;)
          {
            paramAnonymousView.onOpen(bool);
            return;
            bool = false;
          }
        }
        ChatActivityEnterView.this.openKeyboardInternal();
        ChatActivityEnterView.this.removeGifFromInputField();
      }
    });
    this.messageEditText = new EditTextCaption(paramActivity);
    updateFieldHint();
    this.messageEditText.setImeOptions(268435456);
    this.messageEditText.setInputType(this.messageEditText.getInputType() | 0x4000 | 0x20000);
    this.messageEditText.setSingleLine(false);
    this.messageEditText.setMaxLines(4);
    this.messageEditText.setTextSize(1, 18.0F);
    this.messageEditText.setGravity(80);
    this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0F), 0, AndroidUtilities.dp(12.0F));
    this.messageEditText.setBackgroundDrawable(null);
    this.messageEditText.setTextColor(-16777216);
    this.messageEditText.setHintTextColor(-5066062);
    paramChatActivity = this.messageEditText;
    float f;
    if (paramBoolean) {
      f = 50.0F;
    }
    for (;;)
    {
      paramSizeNotifierFrameLayout.addView(paramChatActivity, LayoutHelper.createFrame(-1, -2.0F, 80, 52.0F, 0.0F, f, 0.0F));
      this.messageEditText.setOnKeyListener(new View.OnKeyListener()
      {
        boolean ctrlPressed = false;
        
        public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          boolean bool = false;
          if ((paramAnonymousInt == 4) && (!ChatActivityEnterView.this.keyboardVisible) && (ChatActivityEnterView.this.isPopupShowing()))
          {
            if (paramAnonymousKeyEvent.getAction() == 1)
            {
              if ((ChatActivityEnterView.this.currentPopupContentType == 1) && (ChatActivityEnterView.this.botButtonsMessageObject != null)) {
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
              }
              ChatActivityEnterView.this.showPopup(0, 0);
              ChatActivityEnterView.this.removeGifFromInputField();
            }
            return true;
          }
          if ((paramAnonymousInt == 66) && ((this.ctrlPressed) || (ChatActivityEnterView.this.sendByEnter)) && (paramAnonymousKeyEvent.getAction() == 0) && (ChatActivityEnterView.this.editingMessageObject == null))
          {
            ChatActivityEnterView.this.sendMessage();
            return true;
          }
          if ((paramAnonymousInt == 113) || (paramAnonymousInt == 114))
          {
            if (paramAnonymousKeyEvent.getAction() == 0) {
              bool = true;
            }
            this.ctrlPressed = bool;
            return true;
          }
          return false;
        }
      });
      this.messageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        boolean ctrlPressed = false;
        
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          boolean bool = false;
          if (paramAnonymousInt == 4)
          {
            ChatActivityEnterView.this.sendMessage();
            return true;
          }
          if ((paramAnonymousKeyEvent != null) && (paramAnonymousInt == 0))
          {
            if (((this.ctrlPressed) || (ChatActivityEnterView.this.sendByEnter)) && (paramAnonymousKeyEvent.getAction() == 0) && (ChatActivityEnterView.this.editingMessageObject == null))
            {
              ChatActivityEnterView.this.sendMessage();
              return true;
            }
            if ((paramAnonymousInt == 113) || (paramAnonymousInt == 114))
            {
              if (paramAnonymousKeyEvent.getAction() == 0) {
                bool = true;
              }
              this.ctrlPressed = bool;
              return true;
            }
          }
          return false;
        }
      });
      this.messageEditText.addTextChangedListener(new TextWatcher()
      {
        boolean processChange = false;
        
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          if (ChatActivityEnterView.this.innerTextChange != 0) {}
          do
          {
            return;
            if ((ChatActivityEnterView.this.sendByEnter) && (paramAnonymousEditable.length() > 0) && (paramAnonymousEditable.charAt(paramAnonymousEditable.length() - 1) == '\n') && (ChatActivityEnterView.this.editingMessageObject == null)) {
              ChatActivityEnterView.this.sendMessage();
            }
          } while (!this.processChange);
          ImageSpan[] arrayOfImageSpan = (ImageSpan[])paramAnonymousEditable.getSpans(0, paramAnonymousEditable.length(), ImageSpan.class);
          int i = 0;
          while (i < arrayOfImageSpan.length)
          {
            paramAnonymousEditable.removeSpan(arrayOfImageSpan[i]);
            i += 1;
          }
          Emoji.replaceEmoji(paramAnonymousEditable, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
          this.processChange = false;
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if (ChatActivityEnterView.this.innerTextChange == 1) {
            return;
          }
          ChatActivityEnterView.this.checkSendButton(true);
          CharSequence localCharSequence = AndroidUtilities.getTrimmedString(paramAnonymousCharSequence.toString());
          ChatActivityEnterView.ChatActivityEnterViewDelegate localChatActivityEnterViewDelegate;
          if ((ChatActivityEnterView.this.delegate != null) && (!ChatActivityEnterView.this.ignoreTextChange))
          {
            if ((paramAnonymousInt3 > 2) || (paramAnonymousCharSequence == null) || (paramAnonymousCharSequence.length() == 0)) {
              ChatActivityEnterView.access$2002(ChatActivityEnterView.this, true);
            }
            localChatActivityEnterViewDelegate = ChatActivityEnterView.this.delegate;
            if ((paramAnonymousInt2 <= paramAnonymousInt3 + 1) && (paramAnonymousInt3 - paramAnonymousInt2 <= 2)) {
              break label328;
            }
          }
          label328:
          for (boolean bool = true;; bool = false)
          {
            localChatActivityEnterViewDelegate.onTextChanged(paramAnonymousCharSequence, bool);
            if ((ChatActivityEnterView.this.innerTextChange != 2) && (paramAnonymousInt2 != paramAnonymousInt3) && (paramAnonymousInt3 - paramAnonymousInt2 > 1)) {
              this.processChange = true;
            }
            if ((ChatActivityEnterView.this.editingMessageObject != null) || (ChatActivityEnterView.this.canWriteToChannel) || (localCharSequence.length() == 0) || (ChatActivityEnterView.this.lastTypingTimeSend >= System.currentTimeMillis() - 5000L) || (ChatActivityEnterView.this.ignoreTextChange)) {
              break;
            }
            paramAnonymousInt1 = ConnectionsManager.getInstance().getCurrentTime();
            paramAnonymousCharSequence = null;
            if ((int)ChatActivityEnterView.this.dialog_id > 0) {
              paramAnonymousCharSequence = MessagesController.getInstance().getUser(Integer.valueOf((int)ChatActivityEnterView.this.dialog_id));
            }
            if ((paramAnonymousCharSequence != null) && ((paramAnonymousCharSequence.id == UserConfig.getClientUserId()) || ((paramAnonymousCharSequence.status != null) && (paramAnonymousCharSequence.status.expires < paramAnonymousInt1) && (!MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(paramAnonymousCharSequence.id)))))) {
              break;
            }
            ChatActivityEnterView.access$2202(ChatActivityEnterView.this, System.currentTimeMillis());
            if (ChatActivityEnterView.this.delegate == null) {
              break;
            }
            ChatActivityEnterView.this.delegate.needSendTyping();
            return;
          }
        }
      });
      try
      {
        paramChatActivity = TextView.class.getDeclaredField("mCursorDrawableRes");
        paramChatActivity.setAccessible(true);
        paramChatActivity.set(this.messageEditText, Integer.valueOf(2130837662));
        if (paramBoolean)
        {
          this.attachButton = new LinearLayout(paramActivity);
          this.attachButton.setOrientation(0);
          this.attachButton.setEnabled(false);
          this.attachButton.setPivotX(AndroidUtilities.dp(48.0F));
          paramSizeNotifierFrameLayout.addView(this.attachButton, LayoutHelper.createFrame(-2, 48, 85));
          this.botButton = new ImageView(paramActivity);
          this.botButton.setImageResource(2130837567);
          this.botButton.setScaleType(ImageView.ScaleType.CENTER);
          this.botButton.setVisibility(8);
          if (Build.VERSION.SDK_INT >= 21) {
            this.botButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(-2697514));
          }
          this.attachButton.addView(this.botButton, LayoutHelper.createLinear(48, 48));
          this.botButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (ChatActivityEnterView.this.botReplyMarkup != null) {
                if ((!ChatActivityEnterView.this.isPopupShowing()) || (ChatActivityEnterView.this.currentPopupContentType != 1))
                {
                  ChatActivityEnterView.this.showPopup(1, 1);
                  ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("hidekeyboard_" + ChatActivityEnterView.this.dialog_id).commit();
                }
              }
              while (!ChatActivityEnterView.this.hasBotCommands)
              {
                return;
                if ((ChatActivityEnterView.this.currentPopupContentType == 1) && (ChatActivityEnterView.this.botButtonsMessageObject != null)) {
                  ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                }
                ChatActivityEnterView.this.openKeyboardInternal();
                return;
              }
              ChatActivityEnterView.this.setFieldText("/");
              ChatActivityEnterView.this.openKeyboard();
            }
          });
          this.notifyButton = new ImageView(paramActivity);
          paramChatActivity = this.notifyButton;
          if (this.silent)
          {
            i = 2130837860;
            label843:
            paramChatActivity.setImageResource(i);
            this.notifyButton.setScaleType(ImageView.ScaleType.CENTER);
            paramChatActivity = this.notifyButton;
            if (!this.canWriteToChannel) {
              break label2108;
            }
            i = 0;
            label874:
            paramChatActivity.setVisibility(i);
            if (Build.VERSION.SDK_INT >= 21) {
              this.notifyButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(-2697514));
            }
            this.attachButton.addView(this.notifyButton, LayoutHelper.createLinear(48, 48));
            this.notifyButton.setOnClickListener(new View.OnClickListener()
            {
              private Toast visibleToast;
              
              public void onClick(View paramAnonymousView)
              {
                paramAnonymousView = ChatActivityEnterView.this;
                boolean bool;
                if (!ChatActivityEnterView.this.silent) {
                  bool = true;
                }
                for (;;)
                {
                  ChatActivityEnterView.access$2502(paramAnonymousView, bool);
                  paramAnonymousView = ChatActivityEnterView.this.notifyButton;
                  int i;
                  if (ChatActivityEnterView.this.silent)
                  {
                    i = 2130837860;
                    paramAnonymousView.setImageResource(i);
                    ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("silent_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.silent).commit();
                    NotificationsController.updateServerNotificationsSettings(ChatActivityEnterView.this.dialog_id);
                  }
                  try
                  {
                    if (this.visibleToast != null) {
                      this.visibleToast.cancel();
                    }
                    if (ChatActivityEnterView.this.silent)
                    {
                      this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOff", 2131165462), 0);
                      this.visibleToast.show();
                      ChatActivityEnterView.this.updateFieldHint();
                      return;
                      bool = false;
                      continue;
                      i = 2130837861;
                    }
                  }
                  catch (Exception paramAnonymousView)
                  {
                    for (;;)
                    {
                      FileLog.e("tmessages", paramAnonymousView);
                      continue;
                      this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOn", 2131165463), 0);
                    }
                  }
                }
              }
            });
          }
        }
        else
        {
          this.recordedAudioPanel = new FrameLayout(paramActivity);
          paramChatActivity = this.recordedAudioPanel;
          if (this.audioToSend != null) {
            break label2115;
          }
        }
        label2108:
        label2115:
        for (int i = 8;; i = 0)
        {
          paramChatActivity.setVisibility(i);
          this.recordedAudioPanel.setBackgroundColor(-1);
          this.recordedAudioPanel.setFocusable(true);
          this.recordedAudioPanel.setFocusableInTouchMode(true);
          this.recordedAudioPanel.setClickable(true);
          paramSizeNotifierFrameLayout.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
          paramChatActivity = new ImageView(paramActivity);
          paramChatActivity.setScaleType(ImageView.ScaleType.CENTER);
          paramChatActivity.setImageResource(2130837705);
          this.recordedAudioPanel.addView(paramChatActivity, LayoutHelper.createFrame(48, 48.0F));
          paramChatActivity.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              paramAnonymousView = MediaController.getInstance().getPlayingMessageObject();
              if ((paramAnonymousView != null) && (paramAnonymousView == ChatActivityEnterView.this.audioToSendMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
              }
              if (ChatActivityEnterView.this.audioToSendPath != null) {
                new File(ChatActivityEnterView.this.audioToSendPath).delete();
              }
              ChatActivityEnterView.this.hideRecordedAudioPanel();
              ChatActivityEnterView.this.checkSendButton(true);
            }
          });
          paramChatActivity = new View(paramActivity);
          paramChatActivity.setBackgroundResource(2130837953);
          this.recordedAudioPanel.addView(paramChatActivity, LayoutHelper.createFrame(-1, 32.0F, 19, 48.0F, 0.0F, 0.0F, 0.0F));
          this.recordedAudioSeekBar = new SeekBarWaveformView(paramActivity);
          this.recordedAudioPanel.addView(this.recordedAudioSeekBar, LayoutHelper.createFrame(-1, 32.0F, 19, 92.0F, 0.0F, 52.0F, 0.0F));
          this.recordedAudioPlayButton = new ImageView(paramActivity);
          this.recordedAudioPlayButton.setImageResource(2130837966);
          this.recordedAudioPlayButton.setScaleType(ImageView.ScaleType.CENTER);
          this.recordedAudioPanel.addView(this.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0F, 83, 48.0F, 0.0F, 0.0F, 0.0F));
          this.recordedAudioPlayButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (ChatActivityEnterView.this.audioToSend == null) {
                return;
              }
              if ((MediaController.getInstance().isPlayingAudio(ChatActivityEnterView.this.audioToSendMessageObject)) && (!MediaController.getInstance().isAudioPaused()))
              {
                MediaController.getInstance().pauseAudio(ChatActivityEnterView.this.audioToSendMessageObject);
                ChatActivityEnterView.this.recordedAudioPlayButton.setImageResource(2130837966);
                return;
              }
              ChatActivityEnterView.this.recordedAudioPlayButton.setImageResource(2130837965);
              MediaController.getInstance().playAudio(ChatActivityEnterView.this.audioToSendMessageObject);
            }
          });
          this.recordedAudioTimeTextView = new TextView(paramActivity);
          this.recordedAudioTimeTextView.setTextColor(-1);
          this.recordedAudioTimeTextView.setTextSize(1, 13.0F);
          this.recordedAudioTimeTextView.setText("0:13");
          this.recordedAudioPanel.addView(this.recordedAudioTimeTextView, LayoutHelper.createFrame(-2, -2.0F, 21, 0.0F, 0.0F, 13.0F, 0.0F));
          this.recordPanel = new FrameLayout(paramActivity);
          this.recordPanel.setVisibility(8);
          this.recordPanel.setBackgroundColor(-1);
          paramSizeNotifierFrameLayout.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48, 80));
          this.slideText = new LinearLayout(paramActivity);
          this.slideText.setOrientation(0);
          this.recordPanel.addView(this.slideText, LayoutHelper.createFrame(-2, -2.0F, 17, 30.0F, 0.0F, 0.0F, 0.0F));
          paramSizeNotifierFrameLayout = new ImageView(paramActivity);
          paramSizeNotifierFrameLayout.setImageResource(2130837985);
          this.slideText.addView(paramSizeNotifierFrameLayout, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 0, 0));
          paramSizeNotifierFrameLayout = new TextView(paramActivity);
          paramSizeNotifierFrameLayout.setText(LocaleController.getString("SlideToCancel", 2131166292));
          paramSizeNotifierFrameLayout.setTextColor(-6710887);
          paramSizeNotifierFrameLayout.setTextSize(1, 12.0F);
          this.slideText.addView(paramSizeNotifierFrameLayout, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
          paramSizeNotifierFrameLayout = new LinearLayout(paramActivity);
          paramSizeNotifierFrameLayout.setOrientation(0);
          paramSizeNotifierFrameLayout.setPadding(AndroidUtilities.dp(13.0F), 0, 0, 0);
          paramSizeNotifierFrameLayout.setBackgroundColor(-1);
          this.recordPanel.addView(paramSizeNotifierFrameLayout, LayoutHelper.createFrame(-2, -2, 16));
          this.recordDot = new RecordDot(paramActivity);
          paramSizeNotifierFrameLayout.addView(this.recordDot, LayoutHelper.createLinear(11, 11, 16, 0, 1, 0, 0));
          this.recordTimeText = new TextView(paramActivity);
          this.recordTimeText.setText("00:00");
          this.recordTimeText.setTextColor(-11711413);
          this.recordTimeText.setTextSize(1, 16.0F);
          paramSizeNotifierFrameLayout.addView(this.recordTimeText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
          this.sendButtonContainer = new FrameLayout(paramActivity);
          this.textFieldContainer.addView(this.sendButtonContainer, LayoutHelper.createLinear(48, 48, 80));
          this.audioSendButton = new ImageView(paramActivity);
          this.audioSendButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
          this.audioSendButton.setImageResource(2130837826);
          this.audioSendButton.setBackgroundColor(-1);
          this.audioSendButton.setSoundEffectsEnabled(false);
          this.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0F), 0);
          this.sendButtonContainer.addView(this.audioSendButton, LayoutHelper.createFrame(48, 48.0F));
          this.audioSendButton.setOnTouchListener(new View.OnTouchListener()
          {
            public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
            {
              Object localObject;
              if (paramAnonymousMotionEvent.getAction() == 0) {
                if (ChatActivityEnterView.this.parentFragment != null)
                {
                  if ((Build.VERSION.SDK_INT >= 23) && (ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0))
                  {
                    ChatActivityEnterView.this.parentActivity.requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 3);
                    return false;
                  }
                  if ((int)ChatActivityEnterView.this.dialog_id >= 0) {
                    break label199;
                  }
                  localObject = MessagesController.getInstance().getChat(Integer.valueOf(-(int)ChatActivityEnterView.this.dialog_id));
                  if ((localObject != null) && (((TLRPC.Chat)localObject).participants_count > MessagesController.getInstance().groupBigSize))
                  {
                    localObject = "bigchat_upload_audio";
                    label116:
                    if (!MessagesController.isFeatureEnabled((String)localObject, ChatActivityEnterView.this.parentFragment)) {
                      break label204;
                    }
                  }
                }
                else
                {
                  ChatActivityEnterView.access$3402(ChatActivityEnterView.this, -1.0F);
                  MediaController.getInstance().startRecording(ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
                  ChatActivityEnterView.this.updateAudioRecordIntefrace();
                  ChatActivityEnterView.this.audioSendButton.getParent().requestDisallowInterceptTouchEvent(true);
                }
              }
              label199:
              label204:
              label428:
              label646:
              label678:
              for (;;)
              {
                paramAnonymousView.onTouchEvent(paramAnonymousMotionEvent);
                return true;
                localObject = "chat_upload_audio";
                break label116;
                localObject = "pm_upload_audio";
                break label116;
                break;
                if ((paramAnonymousMotionEvent.getAction() == 1) || (paramAnonymousMotionEvent.getAction() == 3))
                {
                  ChatActivityEnterView.access$3402(ChatActivityEnterView.this, -1.0F);
                  MediaController.getInstance().stopRecording(1);
                  ChatActivityEnterView.access$3802(ChatActivityEnterView.this, false);
                  ChatActivityEnterView.this.updateAudioRecordIntefrace();
                }
                else if ((paramAnonymousMotionEvent.getAction() == 2) && (ChatActivityEnterView.this.recordingAudio))
                {
                  float f1 = paramAnonymousMotionEvent.getX();
                  if (f1 < -ChatActivityEnterView.this.distCanMove)
                  {
                    MediaController.getInstance().stopRecording(0);
                    ChatActivityEnterView.access$3802(ChatActivityEnterView.this, false);
                    ChatActivityEnterView.this.updateAudioRecordIntefrace();
                  }
                  float f3 = f1 + ChatActivityEnterView.this.audioSendButton.getX();
                  localObject = (FrameLayout.LayoutParams)ChatActivityEnterView.this.slideText.getLayoutParams();
                  float f2;
                  if (ChatActivityEnterView.this.startedDraggingX != -1.0F)
                  {
                    f1 = f3 - ChatActivityEnterView.this.startedDraggingX;
                    ChatActivityEnterView.this.recordCircle.setTranslationX(f1);
                    ((FrameLayout.LayoutParams)localObject).leftMargin = (AndroidUtilities.dp(30.0F) + (int)f1);
                    ChatActivityEnterView.this.slideText.setLayoutParams((ViewGroup.LayoutParams)localObject);
                    f2 = 1.0F + f1 / ChatActivityEnterView.this.distCanMove;
                    if (f2 > 1.0F)
                    {
                      f1 = 1.0F;
                      ChatActivityEnterView.this.slideText.setAlpha(f1);
                    }
                  }
                  else if ((f3 <= ChatActivityEnterView.this.slideText.getX() + ChatActivityEnterView.this.slideText.getWidth() + AndroidUtilities.dp(30.0F)) && (ChatActivityEnterView.this.startedDraggingX == -1.0F))
                  {
                    ChatActivityEnterView.access$3402(ChatActivityEnterView.this, f3);
                    ChatActivityEnterView.access$3902(ChatActivityEnterView.this, (ChatActivityEnterView.this.recordPanel.getMeasuredWidth() - ChatActivityEnterView.this.slideText.getMeasuredWidth() - AndroidUtilities.dp(48.0F)) / 2.0F);
                    if (ChatActivityEnterView.this.distCanMove > 0.0F) {
                      break label646;
                    }
                    ChatActivityEnterView.access$3902(ChatActivityEnterView.this, AndroidUtilities.dp(80.0F));
                  }
                  for (;;)
                  {
                    if (((FrameLayout.LayoutParams)localObject).leftMargin <= AndroidUtilities.dp(30.0F)) {
                      break label678;
                    }
                    ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(30.0F);
                    ChatActivityEnterView.this.recordCircle.setTranslationX(0.0F);
                    ChatActivityEnterView.this.slideText.setLayoutParams((ViewGroup.LayoutParams)localObject);
                    ChatActivityEnterView.this.slideText.setAlpha(1.0F);
                    ChatActivityEnterView.access$3402(ChatActivityEnterView.this, -1.0F);
                    break;
                    f1 = f2;
                    if (f2 >= 0.0F) {
                      break label428;
                    }
                    f1 = 0.0F;
                    break label428;
                    if (ChatActivityEnterView.this.distCanMove > AndroidUtilities.dp(80.0F)) {
                      ChatActivityEnterView.access$3902(ChatActivityEnterView.this, AndroidUtilities.dp(80.0F));
                    }
                  }
                }
              }
            }
          });
          this.recordCircle = new RecordCircle(paramActivity);
          this.recordCircle.setVisibility(8);
          this.sizeNotifierLayout.addView(this.recordCircle, LayoutHelper.createFrame(124, 124.0F, 85, 0.0F, 0.0F, -36.0F, -38.0F));
          this.cancelBotButton = new ImageView(paramActivity);
          this.cancelBotButton.setVisibility(4);
          this.cancelBotButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
          paramSizeNotifierFrameLayout = this.cancelBotButton;
          paramChatActivity = new CloseProgressDrawable2();
          this.progressDrawable = paramChatActivity;
          paramSizeNotifierFrameLayout.setImageDrawable(paramChatActivity);
          this.cancelBotButton.setSoundEffectsEnabled(false);
          this.cancelBotButton.setScaleX(0.1F);
          this.cancelBotButton.setScaleY(0.1F);
          this.cancelBotButton.setAlpha(0.0F);
          this.sendButtonContainer.addView(this.cancelBotButton, LayoutHelper.createFrame(48, 48.0F));
          this.cancelBotButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              paramAnonymousView = ChatActivityEnterView.this.messageEditText.getText().toString();
              int i = paramAnonymousView.indexOf(' ');
              if ((i == -1) || (i == paramAnonymousView.length() - 1))
              {
                ChatActivityEnterView.this.setFieldText("");
                return;
              }
              ChatActivityEnterView.this.setFieldText(paramAnonymousView.substring(0, i + 1));
            }
          });
          this.sendButton = new ImageView(paramActivity);
          this.sendButton.setVisibility(4);
          this.sendButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
          this.sendButton.setImageResource(2130837751);
          this.sendButton.setSoundEffectsEnabled(false);
          this.sendButton.setScaleX(0.1F);
          this.sendButton.setScaleY(0.1F);
          this.sendButton.setAlpha(0.0F);
          this.sendButtonContainer.addView(this.sendButton, LayoutHelper.createFrame(48, 48.0F));
          this.sendButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              ChatActivityEnterView.this.sendMessage();
            }
          });
          paramActivity = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
          this.keyboardHeight = paramActivity.getInt("kbd_height", AndroidUtilities.dp(200.0F));
          this.keyboardHeightLand = paramActivity.getInt("kbd_height_land3", AndroidUtilities.dp(200.0F));
          checkSendButton(false);
          return;
          f = 2.0F;
          break;
          i = 2130837861;
          break label843;
          i = 8;
          break label874;
        }
      }
      catch (Exception paramChatActivity)
      {
        for (;;) {}
      }
    }
  }
  
  private void checkSendButton(boolean paramBoolean)
  {
    if (this.editingMessageObject != null) {}
    label71:
    label93:
    label499:
    label639:
    label642:
    label724:
    label809:
    label1042:
    do
    {
      do
      {
        do
        {
          int i;
          int j;
          do
          {
            return;
            if (this.isPaused) {
              paramBoolean = false;
            }
            if ((AndroidUtilities.getTrimmedString(this.messageEditText.getText()).length() <= 0) && (!this.forceShowSendButton) && (this.audioToSend == null)) {
              break label1042;
            }
            if ((this.messageEditText.caption == null) || (this.sendButton.getVisibility() != 0)) {
              break;
            }
            i = 1;
            if ((this.messageEditText.caption != null) || (this.cancelBotButton.getVisibility() != 0)) {
              break label639;
            }
            j = 1;
            if ((this.audioSendButton.getVisibility() != 0) && (i == 0) && (j == 0)) {
              break label642;
            }
            if (!paramBoolean) {
              break label809;
            }
          } while (((this.runningAnimationType == 1) && (this.messageEditText.caption == null)) || ((this.runningAnimationType == 3) && (this.messageEditText.caption != null)));
          if (this.runningAnimation != null)
          {
            this.runningAnimation.cancel();
            this.runningAnimation = null;
          }
          if (this.runningAnimation2 != null)
          {
            this.runningAnimation2.cancel();
            this.runningAnimation2 = null;
          }
          if (this.attachButton != null)
          {
            this.runningAnimation2 = new AnimatorSet();
            this.runningAnimation2.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.attachButton, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.attachButton, "scaleX", new float[] { 0.0F }) });
            this.runningAnimation2.setDuration(100L);
            this.runningAnimation2.addListener(new AnimatorListenerAdapterProxy()
            {
              public void onAnimationCancel(Animator paramAnonymousAnimator)
              {
                if ((ChatActivityEnterView.this.runningAnimation2 != null) && (ChatActivityEnterView.this.runningAnimation2.equals(paramAnonymousAnimator))) {
                  ChatActivityEnterView.access$5002(ChatActivityEnterView.this, null);
                }
              }
              
              public void onAnimationEnd(Animator paramAnonymousAnimator)
              {
                if ((ChatActivityEnterView.this.runningAnimation2 != null) && (ChatActivityEnterView.this.runningAnimation2.equals(paramAnonymousAnimator))) {
                  ChatActivityEnterView.this.attachButton.setVisibility(8);
                }
              }
            });
            this.runningAnimation2.start();
            updateFieldRight(0);
            if ((this.delegate != null) && (getVisibility() == 0)) {
              this.delegate.onAttachButtonHidden();
            }
          }
          this.runningAnimation = new AnimatorSet();
          localArrayList = new ArrayList();
          if (this.audioSendButton.getVisibility() == 0)
          {
            localArrayList.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleX", new float[] { 0.1F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleY", new float[] { 0.1F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[] { 0.0F }));
          }
          if (i != 0)
          {
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[] { 0.1F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[] { 0.1F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[] { 0.0F }));
            if (this.messageEditText.caption == null) {
              break label724;
            }
            this.runningAnimationType = 3;
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[] { 1.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[] { 1.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[] { 1.0F }));
            this.cancelBotButton.setVisibility(0);
          }
          for (;;)
          {
            this.runningAnimation.playTogether(localArrayList);
            this.runningAnimation.setDuration(150L);
            this.runningAnimation.addListener(new AnimatorListenerAdapterProxy()
            {
              public void onAnimationCancel(Animator paramAnonymousAnimator)
              {
                if ((ChatActivityEnterView.this.runningAnimation != null) && (ChatActivityEnterView.this.runningAnimation.equals(paramAnonymousAnimator))) {
                  ChatActivityEnterView.access$5202(ChatActivityEnterView.this, null);
                }
              }
              
              public void onAnimationEnd(Animator paramAnonymousAnimator)
              {
                if ((ChatActivityEnterView.this.runningAnimation != null) && (ChatActivityEnterView.this.runningAnimation.equals(paramAnonymousAnimator)))
                {
                  if (ChatActivityEnterView.EditTextCaption.access$4900(ChatActivityEnterView.this.messageEditText) == null) {
                    break label91;
                  }
                  ChatActivityEnterView.this.cancelBotButton.setVisibility(0);
                  ChatActivityEnterView.this.sendButton.setVisibility(8);
                }
                for (;;)
                {
                  ChatActivityEnterView.this.audioSendButton.setVisibility(8);
                  ChatActivityEnterView.access$5202(ChatActivityEnterView.this, null);
                  ChatActivityEnterView.access$5502(ChatActivityEnterView.this, 0);
                  return;
                  label91:
                  ChatActivityEnterView.this.sendButton.setVisibility(0);
                  ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                }
              }
            });
            this.runningAnimation.start();
            return;
            i = 0;
            break label71;
            j = 0;
            break label93;
            break;
            if (j == 0) {
              break label499;
            }
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[] { 0.1F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[] { 0.1F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[] { 0.0F }));
            break label499;
            this.runningAnimationType = 1;
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[] { 1.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[] { 1.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[] { 1.0F }));
            this.sendButton.setVisibility(0);
          }
          this.audioSendButton.setScaleX(0.1F);
          this.audioSendButton.setScaleY(0.1F);
          this.audioSendButton.setAlpha(0.0F);
          if (this.messageEditText.caption != null)
          {
            this.sendButton.setScaleX(0.1F);
            this.sendButton.setScaleY(0.1F);
            this.sendButton.setAlpha(0.0F);
            this.cancelBotButton.setScaleX(1.0F);
            this.cancelBotButton.setScaleY(1.0F);
            this.cancelBotButton.setAlpha(1.0F);
            this.cancelBotButton.setVisibility(0);
            this.sendButton.setVisibility(8);
          }
          for (;;)
          {
            this.audioSendButton.setVisibility(8);
            if (this.attachButton == null) {
              break;
            }
            this.attachButton.setVisibility(8);
            if ((this.delegate != null) && (getVisibility() == 0)) {
              this.delegate.onAttachButtonHidden();
            }
            updateFieldRight(0);
            return;
            this.cancelBotButton.setScaleX(0.1F);
            this.cancelBotButton.setScaleY(0.1F);
            this.cancelBotButton.setAlpha(0.0F);
            this.sendButton.setScaleX(1.0F);
            this.sendButton.setScaleY(1.0F);
            this.sendButton.setAlpha(1.0F);
            this.sendButton.setVisibility(0);
            this.cancelBotButton.setVisibility(8);
          }
        } while ((this.sendButton.getVisibility() != 0) && (this.cancelBotButton.getVisibility() != 0));
        if (!paramBoolean) {
          break;
        }
      } while (this.runningAnimationType == 2);
      if (this.runningAnimation != null)
      {
        this.runningAnimation.cancel();
        this.runningAnimation = null;
      }
      if (this.runningAnimation2 != null)
      {
        this.runningAnimation2.cancel();
        this.runningAnimation2 = null;
      }
      if (this.attachButton != null)
      {
        this.attachButton.setVisibility(0);
        this.runningAnimation2 = new AnimatorSet();
        this.runningAnimation2.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.attachButton, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.attachButton, "scaleX", new float[] { 1.0F }) });
        this.runningAnimation2.setDuration(100L);
        this.runningAnimation2.start();
        updateFieldRight(1);
        if (getVisibility() == 0) {
          this.delegate.onAttachButtonShow();
        }
      }
      this.audioSendButton.setVisibility(0);
      this.runningAnimation = new AnimatorSet();
      this.runningAnimationType = 2;
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleX", new float[] { 1.0F }));
      localArrayList.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleY", new float[] { 1.0F }));
      localArrayList.add(ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[] { 1.0F }));
      if (this.cancelBotButton.getVisibility() == 0)
      {
        localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[] { 0.1F }));
        localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[] { 0.1F }));
        localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[] { 0.0F }));
      }
      for (;;)
      {
        this.runningAnimation.playTogether(localArrayList);
        this.runningAnimation.setDuration(150L);
        this.runningAnimation.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator)
          {
            if ((ChatActivityEnterView.this.runningAnimation != null) && (ChatActivityEnterView.this.runningAnimation.equals(paramAnonymousAnimator))) {
              ChatActivityEnterView.access$5202(ChatActivityEnterView.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((ChatActivityEnterView.this.runningAnimation != null) && (ChatActivityEnterView.this.runningAnimation.equals(paramAnonymousAnimator)))
            {
              ChatActivityEnterView.this.sendButton.setVisibility(8);
              ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
              ChatActivityEnterView.this.audioSendButton.setVisibility(0);
              ChatActivityEnterView.access$5202(ChatActivityEnterView.this, null);
              ChatActivityEnterView.access$5502(ChatActivityEnterView.this, 0);
            }
          }
        });
        this.runningAnimation.start();
        return;
        localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[] { 0.1F }));
        localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[] { 0.1F }));
        localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[] { 0.0F }));
      }
      this.sendButton.setScaleX(0.1F);
      this.sendButton.setScaleY(0.1F);
      this.sendButton.setAlpha(0.0F);
      this.cancelBotButton.setScaleX(0.1F);
      this.cancelBotButton.setScaleY(0.1F);
      this.cancelBotButton.setAlpha(0.0F);
      this.audioSendButton.setScaleX(1.0F);
      this.audioSendButton.setScaleY(1.0F);
      this.audioSendButton.setAlpha(1.0F);
      this.cancelBotButton.setVisibility(8);
      this.sendButton.setVisibility(8);
      this.audioSendButton.setVisibility(0);
    } while (this.attachButton == null);
    if (getVisibility() == 0) {
      this.delegate.onAttachButtonShow();
    }
    this.attachButton.setVisibility(0);
    updateFieldRight(1);
  }
  
  private void createEmojiView()
  {
    if (this.emojiView != null) {
      return;
    }
    this.emojiView = new EmojiView(this.allowStickers, this.allowGifs, this.parentActivity);
    this.emojiView.setVisibility(8);
    this.emojiView.setListener(new EmojiView.Listener()
    {
      public boolean onBackspace()
      {
        if (ChatActivityEnterView.this.messageEditText.length() == 0) {
          return false;
        }
        ChatActivityEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
        return true;
      }
      
      public void onClearEmojiRecent()
      {
        if ((ChatActivityEnterView.this.parentFragment == null) || (ChatActivityEnterView.this.parentActivity == null)) {
          return;
        }
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChatActivityEnterView.this.parentActivity);
        localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
        localBuilder.setMessage(LocaleController.getString("ClearRecentEmoji", 2131165513));
        localBuilder.setPositiveButton(LocaleController.getString("ClearButton", 2131165508).toUpperCase(), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            ChatActivityEnterView.this.emojiView.clearRecentEmoji();
          }
        });
        localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
        ChatActivityEnterView.this.parentFragment.showDialog(localBuilder.create());
      }
      
      public void onEmojiSelected(String paramAnonymousString)
      {
        int j = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
        int i = j;
        if (j < 0) {
          i = 0;
        }
        try
        {
          ChatActivityEnterView.access$1602(ChatActivityEnterView.this, 2);
          paramAnonymousString = Emoji.replaceEmoji(paramAnonymousString, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
          ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(i, paramAnonymousString));
          i += paramAnonymousString.length();
          ChatActivityEnterView.this.messageEditText.setSelection(i, i);
          return;
        }
        catch (Exception paramAnonymousString)
        {
          FileLog.e("tmessages", paramAnonymousString);
          return;
        }
        finally
        {
          ChatActivityEnterView.access$1602(ChatActivityEnterView.this, 0);
        }
      }
      
      public void onGifSelected(TLRPC.Document paramAnonymousDocument)
      {
        SendMessagesHelper.getInstance().sendSticker(paramAnonymousDocument, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
        StickersQuery.addRecentGif(paramAnonymousDocument, (int)(System.currentTimeMillis() / 1000L));
        if ((int)ChatActivityEnterView.this.dialog_id == 0) {
          MessagesController.getInstance().saveGif(paramAnonymousDocument);
        }
        if (ChatActivityEnterView.this.delegate != null) {
          ChatActivityEnterView.this.delegate.onMessageSend(null);
        }
      }
      
      public void onGifTab(boolean paramAnonymousBoolean)
      {
        if (!AndroidUtilities.usingHardwareInput)
        {
          if (!paramAnonymousBoolean) {
            break label56;
          }
          if (ChatActivityEnterView.this.messageEditText.length() == 0)
          {
            ChatActivityEnterView.this.messageEditText.setText("@gif ");
            ChatActivityEnterView.this.messageEditText.setSelection(ChatActivityEnterView.this.messageEditText.length());
          }
        }
        label56:
        while (!ChatActivityEnterView.this.messageEditText.getText().toString().equals("@gif ")) {
          return;
        }
        ChatActivityEnterView.this.messageEditText.setText("");
      }
      
      public void onShowStickerSet(TLRPC.StickerSetCovered paramAnonymousStickerSetCovered)
      {
        if ((ChatActivityEnterView.this.parentFragment == null) || (ChatActivityEnterView.this.parentActivity == null)) {
          return;
        }
        TLRPC.TL_inputStickerSetID localTL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
        localTL_inputStickerSetID.access_hash = paramAnonymousStickerSetCovered.set.access_hash;
        localTL_inputStickerSetID.id = paramAnonymousStickerSetCovered.set.id;
        ChatActivityEnterView.this.parentFragment.showDialog(new StickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, localTL_inputStickerSetID, null, ChatActivityEnterView.this));
      }
      
      public void onStickerSelected(TLRPC.Document paramAnonymousDocument)
      {
        ChatActivityEnterView.this.onStickerSelected(paramAnonymousDocument);
        StickersQuery.addRecentSticker(0, paramAnonymousDocument, (int)(System.currentTimeMillis() / 1000L));
        if ((int)ChatActivityEnterView.this.dialog_id == 0) {
          MessagesController.getInstance().saveGif(paramAnonymousDocument);
        }
      }
      
      public void onStickerSetAdd(TLRPC.StickerSetCovered paramAnonymousStickerSetCovered)
      {
        StickersQuery.removeStickersSet(ChatActivityEnterView.this.parentActivity, paramAnonymousStickerSetCovered.set, 2, ChatActivityEnterView.this.parentFragment, false);
      }
      
      public void onStickerSetRemove(TLRPC.StickerSetCovered paramAnonymousStickerSetCovered)
      {
        StickersQuery.removeStickersSet(ChatActivityEnterView.this.parentActivity, paramAnonymousStickerSetCovered.set, 0, ChatActivityEnterView.this.parentFragment, false);
      }
      
      public void onStickersSettingsClick()
      {
        if (ChatActivityEnterView.this.parentFragment != null) {
          ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0));
        }
      }
      
      public void onStickersTab(boolean paramAnonymousBoolean)
      {
        ChatActivityEnterView.this.delegate.onStickersTab(paramAnonymousBoolean);
      }
    });
    this.emojiView.setVisibility(8);
    this.sizeNotifierLayout.addView(this.emojiView);
  }
  
  private void hideRecordedAudioPanel()
  {
    this.audioToSendPath = null;
    this.audioToSend = null;
    this.audioToSendMessageObject = null;
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.recordedAudioPanel, "alpha", new float[] { 0.0F }) });
    localAnimatorSet.setDuration(200L);
    localAnimatorSet.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
      }
    });
    localAnimatorSet.start();
  }
  
  private void onWindowSizeChanged()
  {
    int j = this.sizeNotifierLayout.getHeight();
    int i = j;
    if (!this.keyboardVisible) {
      i = j - this.emojiPadding;
    }
    if (this.delegate != null) {
      this.delegate.onWindowSizeChanged(i);
    }
    if (this.topView != null)
    {
      if (i >= AndroidUtilities.dp(72.0F) + ActionBar.getCurrentActionBarHeight()) {
        break label114;
      }
      if (this.allowShowTopView)
      {
        this.allowShowTopView = false;
        if (this.needShowTopView)
        {
          this.topView.setVisibility(8);
          resizeForTopView(false);
          this.topView.setTranslationY(this.topView.getLayoutParams().height);
        }
      }
    }
    label114:
    do
    {
      do
      {
        return;
      } while (this.allowShowTopView);
      this.allowShowTopView = true;
    } while (!this.needShowTopView);
    this.topView.setVisibility(0);
    resizeForTopView(true);
    this.topView.setTranslationY(0.0F);
  }
  
  private void openKeyboardInternal()
  {
    int i;
    if ((AndroidUtilities.usingHardwareInput) || (this.isPaused))
    {
      i = 0;
      showPopup(i, 0);
      this.messageEditText.requestFocus();
      AndroidUtilities.showKeyboard(this.messageEditText);
      if (!this.isPaused) {
        break label54;
      }
      this.showKeyboardOnResume = true;
    }
    label54:
    while ((AndroidUtilities.usingHardwareInput) || (this.keyboardVisible) || (AndroidUtilities.isInMultiwindow))
    {
      return;
      i = 2;
      break;
    }
    this.waitingForKeyboardOpen = true;
    AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
    AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
  }
  
  private void removeGifFromInputField()
  {
    if ((!AndroidUtilities.usingHardwareInput) && (this.messageEditText.getText().toString().equals("@gif "))) {
      this.messageEditText.setText("");
    }
  }
  
  private void resizeForTopView(boolean paramBoolean)
  {
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.textFieldContainer.getLayoutParams();
    int j = AndroidUtilities.dp(2.0F);
    if (paramBoolean) {}
    for (int i = this.topView.getLayoutParams().height;; i = 0)
    {
      localLayoutParams.topMargin = (i + j);
      this.textFieldContainer.setLayoutParams(localLayoutParams);
      return;
    }
  }
  
  private void sendMessage()
  {
    Object localObject;
    if (this.parentFragment != null) {
      if ((int)this.dialog_id < 0)
      {
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(-(int)this.dialog_id));
        if ((localObject != null) && (((TLRPC.Chat)localObject).participants_count > MessagesController.getInstance().groupBigSize))
        {
          localObject = "bigchat_message";
          if (MessagesController.isFeatureEnabled((String)localObject, this.parentFragment)) {
            break label78;
          }
        }
      }
    }
    label78:
    label213:
    do
    {
      do
      {
        return;
        localObject = "chat_message";
        break;
        localObject = "pm_message";
        break;
        if (this.audioToSend != null)
        {
          localObject = MediaController.getInstance().getPlayingMessageObject();
          if ((localObject != null) && (localObject == this.audioToSendMessageObject)) {
            MediaController.getInstance().cleanupPlayer(true, true);
          }
          SendMessagesHelper.getInstance().sendMessage(this.audioToSend, null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, null, null);
          if (this.delegate != null) {
            this.delegate.onMessageSend(null);
          }
          hideRecordedAudioPanel();
          checkSendButton(true);
          return;
        }
        localObject = this.messageEditText.getText();
        if (!processSendingText((CharSequence)localObject)) {
          break label213;
        }
        this.messageEditText.setText("");
        this.lastTypingTimeSend = 0L;
      } while (this.delegate == null);
      this.delegate.onMessageSend((CharSequence)localObject);
      return;
    } while ((!this.forceShowSendButton) || (this.delegate == null));
    this.delegate.onMessageSend(null);
  }
  
  private void setEmojiButtonImage()
  {
    int i;
    if (this.emojiView == null)
    {
      i = getContext().getSharedPreferences("emoji", 0).getInt("selected_page", 0);
      if ((i != 0) && ((this.allowStickers) || (this.allowGifs))) {
        break label68;
      }
      this.emojiButton.setImageResource(2130837747);
    }
    label68:
    do
    {
      return;
      i = this.emojiView.getCurrentPage();
      break;
      if (i == 1)
      {
        this.emojiButton.setImageResource(2130837748);
        return;
      }
    } while (i != 2);
    this.emojiButton.setImageResource(2130837745);
  }
  
  private void showPopup(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 1)
    {
      if ((paramInt2 == 0) && (this.emojiView == null)) {
        if (this.parentActivity != null) {}
      }
      Object localObject;
      label172:
      int i;
      do
      {
        return;
        createEmojiView();
        localObject = null;
        if (paramInt2 != 0) {
          break;
        }
        this.emojiView.setVisibility(0);
        if ((this.botKeyboardView != null) && (this.botKeyboardView.getVisibility() != 8)) {
          this.botKeyboardView.setVisibility(8);
        }
        localObject = this.emojiView;
        this.currentPopupContentType = paramInt2;
        if (this.keyboardHeight <= 0) {
          this.keyboardHeight = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200.0F));
        }
        if (this.keyboardHeightLand <= 0) {
          this.keyboardHeightLand = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height_land3", AndroidUtilities.dp(200.0F));
        }
        if (AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
          break label334;
        }
        paramInt1 = this.keyboardHeightLand;
        i = paramInt1;
        if (paramInt2 == 1) {
          i = Math.min(this.botKeyboardView.getKeyboardHeight(), paramInt1);
        }
        if (this.botKeyboardView != null) {
          this.botKeyboardView.setPanelHeight(i);
        }
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)((View)localObject).getLayoutParams();
        localLayoutParams.height = i;
        ((View)localObject).setLayoutParams(localLayoutParams);
        if (!AndroidUtilities.isInMultiwindow) {
          AndroidUtilities.hideKeyboard(this.messageEditText);
        }
      } while (this.sizeNotifierLayout == null);
      this.emojiPadding = i;
      this.sizeNotifierLayout.requestLayout();
      if (paramInt2 == 0) {
        this.emojiButton.setImageResource(2130837746);
      }
      for (;;)
      {
        updateBotButton();
        onWindowSizeChanged();
        return;
        if (paramInt2 != 1) {
          break;
        }
        if ((this.emojiView != null) && (this.emojiView.getVisibility() != 8)) {
          this.emojiView.setVisibility(8);
        }
        this.botKeyboardView.setVisibility(0);
        localObject = this.botKeyboardView;
        break;
        label334:
        paramInt1 = this.keyboardHeight;
        break label172;
        if (paramInt2 == 1) {
          setEmojiButtonImage();
        }
      }
    }
    if (this.emojiButton != null) {
      setEmojiButtonImage();
    }
    this.currentPopupContentType = -1;
    if (this.emojiView != null) {
      this.emojiView.setVisibility(8);
    }
    if (this.botKeyboardView != null) {
      this.botKeyboardView.setVisibility(8);
    }
    if (this.sizeNotifierLayout != null)
    {
      if (paramInt1 == 0) {
        this.emojiPadding = 0;
      }
      this.sizeNotifierLayout.requestLayout();
      onWindowSizeChanged();
    }
    updateBotButton();
  }
  
  private void updateAudioRecordIntefrace()
  {
    if (this.recordingAudio) {
      if (this.audioInterfaceState != 1) {}
    }
    for (;;)
    {
      return;
      this.audioInterfaceState = 1;
      try
      {
        if (this.mWakeLock == null)
        {
          this.mWakeLock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(536870918, "audio record lock");
          this.mWakeLock.acquire();
        }
        AndroidUtilities.lockOrientation(this.parentActivity);
        this.recordPanel.setVisibility(0);
        this.recordCircle.setVisibility(0);
        this.recordCircle.setAmplitude(0.0D);
        this.recordTimeText.setText("00:00");
        this.recordDot.resetAlpha();
        this.lastTimeString = null;
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.slideText.getLayoutParams();
        localLayoutParams.leftMargin = AndroidUtilities.dp(30.0F);
        this.slideText.setLayoutParams(localLayoutParams);
        this.slideText.setAlpha(1.0F);
        this.recordPanel.setX(AndroidUtilities.displaySize.x);
        this.recordCircle.setTranslationX(0.0F);
        if (this.runningAnimationAudio != null) {
          this.runningAnimationAudio.cancel();
        }
        this.runningAnimationAudio = new AnimatorSet();
        this.runningAnimationAudio.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[] { 0.0F }) });
        this.runningAnimationAudio.setDuration(300L);
        this.runningAnimationAudio.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((ChatActivityEnterView.this.runningAnimationAudio != null) && (ChatActivityEnterView.this.runningAnimationAudio.equals(paramAnonymousAnimator)))
            {
              ChatActivityEnterView.this.recordPanel.setX(0.0F);
              ChatActivityEnterView.access$5602(ChatActivityEnterView.this, null);
            }
          }
        });
        this.runningAnimationAudio.setInterpolator(new DecelerateInterpolator());
        this.runningAnimationAudio.start();
        return;
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException1);
        }
      }
      if (this.mWakeLock != null) {}
      try
      {
        this.mWakeLock.release();
        this.mWakeLock = null;
        AndroidUtilities.unlockOrientation(this.parentActivity);
        if (this.audioInterfaceState == 0) {
          continue;
        }
        this.audioInterfaceState = 0;
        if (this.runningAnimationAudio != null) {
          this.runningAnimationAudio.cancel();
        }
        this.runningAnimationAudio = new AnimatorSet();
        this.runningAnimationAudio.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[] { AndroidUtilities.displaySize.x }), ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[] { 1.0F }) });
        this.runningAnimationAudio.setDuration(300L);
        this.runningAnimationAudio.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((ChatActivityEnterView.this.runningAnimationAudio != null) && (ChatActivityEnterView.this.runningAnimationAudio.equals(paramAnonymousAnimator)))
            {
              paramAnonymousAnimator = (FrameLayout.LayoutParams)ChatActivityEnterView.this.slideText.getLayoutParams();
              paramAnonymousAnimator.leftMargin = AndroidUtilities.dp(30.0F);
              ChatActivityEnterView.this.slideText.setLayoutParams(paramAnonymousAnimator);
              ChatActivityEnterView.this.slideText.setAlpha(1.0F);
              ChatActivityEnterView.this.recordPanel.setVisibility(8);
              ChatActivityEnterView.this.recordCircle.setVisibility(8);
              ChatActivityEnterView.access$5602(ChatActivityEnterView.this, null);
            }
          }
        });
        this.runningAnimationAudio.setInterpolator(new AccelerateInterpolator());
        this.runningAnimationAudio.start();
        return;
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException2);
        }
      }
    }
  }
  
  private void updateBotButton()
  {
    if (this.botButton == null) {
      return;
    }
    LinearLayout localLinearLayout;
    if ((this.hasBotCommands) || (this.botReplyMarkup != null))
    {
      if (this.botButton.getVisibility() != 0) {
        this.botButton.setVisibility(0);
      }
      if (this.botReplyMarkup != null) {
        if ((isPopupShowing()) && (this.currentPopupContentType == 1))
        {
          this.botButton.setImageResource(2130837746);
          updateFieldRight(2);
          localLinearLayout = this.attachButton;
          if (((this.botButton != null) && (this.botButton.getVisibility() != 8)) || ((this.notifyButton != null) && (this.notifyButton.getVisibility() != 8))) {
            break label172;
          }
        }
      }
    }
    label172:
    for (float f = 48.0F;; f = 96.0F)
    {
      localLinearLayout.setPivotX(AndroidUtilities.dp(f));
      return;
      this.botButton.setImageResource(2130837567);
      break;
      this.botButton.setImageResource(2130837566);
      break;
      this.botButton.setVisibility(8);
      break;
    }
  }
  
  private void updateFieldHint()
  {
    int i = 0;
    EditTextCaption localEditTextCaption;
    if ((int)this.dialog_id < 0)
    {
      localObject = MessagesController.getInstance().getChat(Integer.valueOf(-(int)this.dialog_id));
      if ((ChatObject.isChannel((TLRPC.Chat)localObject)) && (!((TLRPC.Chat)localObject).megagroup)) {
        i = 1;
      }
    }
    else
    {
      if (i == 0) {
        break label140;
      }
      if (this.editingMessageObject == null) {
        break label99;
      }
      localEditTextCaption = this.messageEditText;
      if (!this.editingCaption) {
        break label86;
      }
    }
    label86:
    for (Object localObject = LocaleController.getString("Caption", 2131165392);; localObject = LocaleController.getString("TypeMessage", 2131166347))
    {
      localEditTextCaption.setHint((CharSequence)localObject);
      return;
      i = 0;
      break;
    }
    label99:
    if (this.silent)
    {
      this.messageEditText.setHint(LocaleController.getString("ChannelSilentBroadcast", 2131165476));
      return;
    }
    this.messageEditText.setHint(LocaleController.getString("ChannelBroadcast", 2131165415));
    return;
    label140:
    this.messageEditText.setHint(LocaleController.getString("TypeMessage", 2131166347));
  }
  
  private void updateFieldRight(int paramInt)
  {
    if ((this.messageEditText == null) || (this.editingMessageObject != null)) {
      return;
    }
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.messageEditText.getLayoutParams();
    if (paramInt == 1) {
      if (((this.botButton != null) && (this.botButton.getVisibility() == 0)) || ((this.notifyButton != null) && (this.notifyButton.getVisibility() == 0))) {
        localLayoutParams.rightMargin = AndroidUtilities.dp(98.0F);
      }
    }
    for (;;)
    {
      this.messageEditText.setLayoutParams(localLayoutParams);
      return;
      localLayoutParams.rightMargin = AndroidUtilities.dp(50.0F);
      continue;
      if (paramInt == 2)
      {
        if (localLayoutParams.rightMargin != AndroidUtilities.dp(2.0F)) {
          if (((this.botButton != null) && (this.botButton.getVisibility() == 0)) || ((this.notifyButton != null) && (this.notifyButton.getVisibility() == 0))) {
            localLayoutParams.rightMargin = AndroidUtilities.dp(98.0F);
          } else {
            localLayoutParams.rightMargin = AndroidUtilities.dp(50.0F);
          }
        }
      }
      else {
        localLayoutParams.rightMargin = AndroidUtilities.dp(2.0F);
      }
    }
  }
  
  public void addRecentGif(TLRPC.Document paramDocument)
  {
    if (this.emojiView == null) {
      return;
    }
    this.emojiView.addRecentGif(paramDocument);
  }
  
  public void addStickerToRecent(TLRPC.Document paramDocument)
  {
    createEmojiView();
    this.emojiView.addRecentSticker(paramDocument);
  }
  
  public void addToAttachLayout(View paramView)
  {
    if (this.attachButton == null) {
      return;
    }
    if (paramView.getParent() != null) {
      ((ViewGroup)paramView.getParent()).removeView(paramView);
    }
    if (Build.VERSION.SDK_INT >= 21) {
      paramView.setBackgroundDrawable(Theme.createBarSelectorDrawable(-2697514));
    }
    this.attachButton.addView(paramView, LayoutHelper.createLinear(48, 48));
  }
  
  public void addTopView(View paramView, int paramInt)
  {
    if (paramView == null) {
      return;
    }
    this.topView = paramView;
    this.topView.setVisibility(8);
    this.topView.setTranslationY(paramInt);
    addView(this.topView, 0, LayoutHelper.createFrame(-1, paramInt, 51, 0.0F, 2.0F, 0.0F, 0.0F));
    this.needShowTopView = false;
  }
  
  public void closeKeyboard()
  {
    AndroidUtilities.hideKeyboard(this.messageEditText);
  }
  
  public void didPressedBotButton(final TLRPC.KeyboardButton paramKeyboardButton, MessageObject paramMessageObject1, final MessageObject paramMessageObject2)
  {
    if ((paramKeyboardButton == null) || (paramMessageObject2 == null)) {}
    do
    {
      do
      {
        return;
        if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButton))
        {
          SendMessagesHelper.getInstance().sendMessage(paramKeyboardButton.text, this.dialog_id, paramMessageObject1, null, false, null, null, null);
          return;
        }
        if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonUrl))
        {
          this.parentFragment.showOpenUrlAlert(paramKeyboardButton.url, true);
          return;
        }
        if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonRequestPhone))
        {
          this.parentFragment.shareMyContact(paramMessageObject2);
          return;
        }
        if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonRequestGeoLocation))
        {
          paramMessageObject1 = new AlertDialog.Builder(this.parentActivity);
          paramMessageObject1.setTitle(LocaleController.getString("ShareYouLocationTitle", 2131166281));
          paramMessageObject1.setMessage(LocaleController.getString("ShareYouLocationInfo", 2131166279));
          paramMessageObject1.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              if ((Build.VERSION.SDK_INT >= 23) && (ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0))
              {
                ChatActivityEnterView.this.parentActivity.requestPermissions(new String[] { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" }, 2);
                ChatActivityEnterView.access$5802(ChatActivityEnterView.this, paramMessageObject2);
                ChatActivityEnterView.access$5902(ChatActivityEnterView.this, paramKeyboardButton);
                return;
              }
              SendMessagesHelper.getInstance().sendCurrentLocation(paramMessageObject2, paramKeyboardButton);
            }
          });
          paramMessageObject1.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
          this.parentFragment.showDialog(paramMessageObject1.create());
          return;
        }
        if (((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonCallback)) || ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame)))
        {
          SendMessagesHelper.getInstance().sendCallback(paramMessageObject2, paramKeyboardButton, this.parentFragment);
          return;
        }
      } while ((!(paramKeyboardButton instanceof TLRPC.TL_keyboardButtonSwitchInline)) || (this.parentFragment.processSwitchButton((TLRPC.TL_keyboardButtonSwitchInline)paramKeyboardButton)));
      if (!paramKeyboardButton.same_peer) {
        break;
      }
      int i = paramMessageObject2.messageOwner.from_id;
      if (paramMessageObject2.messageOwner.via_bot_id != 0) {
        i = paramMessageObject2.messageOwner.via_bot_id;
      }
      paramMessageObject1 = MessagesController.getInstance().getUser(Integer.valueOf(i));
    } while (paramMessageObject1 == null);
    setFieldText("@" + paramMessageObject1.username + " " + paramKeyboardButton.query);
    return;
    paramMessageObject1 = new Bundle();
    paramMessageObject1.putBoolean("onlySelect", true);
    paramMessageObject1.putInt("dialogsType", 1);
    paramMessageObject1 = new DialogsActivity(paramMessageObject1);
    paramMessageObject1.setDelegate(new DialogsActivity.DialogsActivityDelegate()
    {
      public void didSelectDialog(DialogsActivity paramAnonymousDialogsActivity, long paramAnonymousLong, boolean paramAnonymousBoolean)
      {
        int i = paramMessageObject2.messageOwner.from_id;
        if (paramMessageObject2.messageOwner.via_bot_id != 0) {
          i = paramMessageObject2.messageOwner.via_bot_id;
        }
        Object localObject = MessagesController.getInstance().getUser(Integer.valueOf(i));
        if (localObject == null) {
          paramAnonymousDialogsActivity.finishFragment();
        }
        label218:
        for (;;)
        {
          return;
          DraftQuery.saveDraft(paramAnonymousLong, "@" + ((TLRPC.User)localObject).username + " " + paramKeyboardButton.query, null, null, true);
          if (paramAnonymousLong == ChatActivityEnterView.this.dialog_id) {
            break label230;
          }
          i = (int)paramAnonymousLong;
          if (i == 0) {
            break label225;
          }
          localObject = new Bundle();
          if (i > 0) {
            ((Bundle)localObject).putInt("user_id", i);
          }
          for (;;)
          {
            if (!MessagesController.checkCanOpenChat((Bundle)localObject, paramAnonymousDialogsActivity)) {
              break label218;
            }
            localObject = new ChatActivity((Bundle)localObject);
            if (!ChatActivityEnterView.this.parentFragment.presentFragment((BaseFragment)localObject, true)) {
              break label220;
            }
            if (AndroidUtilities.isTablet()) {
              break;
            }
            ChatActivityEnterView.this.parentFragment.removeSelfFromStack();
            return;
            if (i < 0) {
              ((Bundle)localObject).putInt("chat_id", -i);
            }
          }
        }
        label220:
        paramAnonymousDialogsActivity.finishFragment();
        return;
        label225:
        paramAnonymousDialogsActivity.finishFragment();
        return;
        label230:
        paramAnonymousDialogsActivity.finishFragment();
      }
    });
    this.parentFragment.presentFragment(paramMessageObject1);
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.emojiDidLoaded)
    {
      if (this.emojiView != null) {
        this.emojiView.invalidateViews();
      }
      if (this.botKeyboardView != null) {
        this.botKeyboardView.invalidateViews();
      }
    }
    label702:
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          return;
                          if (paramInt != NotificationCenter.recordProgressChanged) {
                            break;
                          }
                          long l = ((Long)paramVarArgs[0]).longValue();
                          localObject = Long.valueOf(l / 1000L);
                          paramInt = (int)(l % 1000L) / 10;
                          String str = String.format("%02d:%02d.%02d", new Object[] { Long.valueOf(((Long)localObject).longValue() / 60L), Long.valueOf(((Long)localObject).longValue() % 60L), Integer.valueOf(paramInt) });
                          if ((this.lastTimeString == null) || (!this.lastTimeString.equals(str)))
                          {
                            if (((Long)localObject).longValue() % 5L == 0L) {
                              MessagesController.getInstance().sendTyping(this.dialog_id, 1, 0);
                            }
                            if (this.recordTimeText != null) {
                              this.recordTimeText.setText(str);
                            }
                          }
                        } while (this.recordCircle == null);
                        this.recordCircle.setAmplitude(((Double)paramVarArgs[1]).doubleValue());
                        return;
                        if (paramInt != NotificationCenter.closeChats) {
                          break;
                        }
                      } while ((this.messageEditText == null) || (!this.messageEditText.isFocused()));
                      AndroidUtilities.hideKeyboard(this.messageEditText);
                      return;
                      if ((paramInt != NotificationCenter.recordStartError) && (paramInt != NotificationCenter.recordStopped)) {
                        break;
                      }
                    } while (!this.recordingAudio);
                    MessagesController.getInstance().sendTyping(this.dialog_id, 2, 0);
                    this.recordingAudio = false;
                    updateAudioRecordIntefrace();
                    return;
                    if (paramInt != NotificationCenter.recordStarted) {
                      break;
                    }
                  } while (this.recordingAudio);
                  this.recordingAudio = true;
                  updateAudioRecordIntefrace();
                  return;
                  if (paramInt != NotificationCenter.audioDidSent) {
                    break label702;
                  }
                  this.audioToSend = ((TLRPC.TL_document)paramVarArgs[0]);
                  this.audioToSendPath = ((String)paramVarArgs[1]);
                  if (this.audioToSend == null) {
                    break;
                  }
                } while (this.recordedAudioPanel == null);
                paramVarArgs = new TLRPC.TL_message();
                paramVarArgs.out = true;
                paramVarArgs.id = 0;
                paramVarArgs.to_id = new TLRPC.TL_peerUser();
                Object localObject = paramVarArgs.to_id;
                paramInt = UserConfig.getClientUserId();
                paramVarArgs.from_id = paramInt;
                ((TLRPC.Peer)localObject).user_id = paramInt;
                paramVarArgs.date = ((int)(System.currentTimeMillis() / 1000L));
                paramVarArgs.message = "-1";
                paramVarArgs.attachPath = this.audioToSendPath;
                paramVarArgs.media = new TLRPC.TL_messageMediaDocument();
                paramVarArgs.media.document = this.audioToSend;
                paramVarArgs.flags |= 0x300;
                this.audioToSendMessageObject = new MessageObject(paramVarArgs, null, false);
                this.recordedAudioPanel.setAlpha(1.0F);
                this.recordedAudioPanel.setVisibility(0);
                int j = 0;
                int i = 0;
                paramInt = j;
                if (i < this.audioToSend.attributes.size())
                {
                  paramVarArgs = (TLRPC.DocumentAttribute)this.audioToSend.attributes.get(i);
                  if ((paramVarArgs instanceof TLRPC.TL_documentAttributeAudio)) {
                    paramInt = paramVarArgs.duration;
                  }
                }
                else
                {
                  i = 0;
                }
                for (;;)
                {
                  if (i < this.audioToSend.attributes.size())
                  {
                    paramVarArgs = (TLRPC.DocumentAttribute)this.audioToSend.attributes.get(i);
                    if ((paramVarArgs instanceof TLRPC.TL_documentAttributeAudio))
                    {
                      if ((paramVarArgs.waveform == null) || (paramVarArgs.waveform.length == 0)) {
                        paramVarArgs.waveform = MediaController.getInstance().getWaveform(this.audioToSendPath);
                      }
                      this.recordedAudioSeekBar.setWaveform(paramVarArgs.waveform);
                    }
                  }
                  else
                  {
                    this.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(paramInt / 60), Integer.valueOf(paramInt % 60) }));
                    closeKeyboard();
                    hidePopup(false);
                    checkSendButton(false);
                    return;
                    i += 1;
                    break;
                  }
                  i += 1;
                }
              } while (this.delegate == null);
              this.delegate.onMessageSend(null);
              return;
              if (paramInt != NotificationCenter.audioRouteChanged) {
                break;
              }
            } while (this.parentActivity == null);
            boolean bool = ((Boolean)paramVarArgs[0]).booleanValue();
            paramVarArgs = this.parentActivity;
            if (bool) {}
            for (paramInt = 0;; paramInt = Integer.MIN_VALUE)
            {
              paramVarArgs.setVolumeControlStream(paramInt);
              return;
            }
            if (paramInt != NotificationCenter.audioDidReset) {
              break;
            }
          } while ((this.audioToSendMessageObject == null) || (MediaController.getInstance().isPlayingAudio(this.audioToSendMessageObject)));
          this.recordedAudioPlayButton.setImageResource(2130837966);
          this.recordedAudioSeekBar.setProgress(0.0F);
          return;
          if (paramInt != NotificationCenter.audioProgressDidChanged) {
            break;
          }
          paramVarArgs = (Integer)paramVarArgs[0];
        } while ((this.audioToSendMessageObject == null) || (!MediaController.getInstance().isPlayingAudio(this.audioToSendMessageObject)));
        paramVarArgs = MediaController.getInstance().getPlayingMessageObject();
        this.audioToSendMessageObject.audioProgress = paramVarArgs.audioProgress;
        this.audioToSendMessageObject.audioProgressSec = paramVarArgs.audioProgressSec;
      } while (this.recordedAudioSeekBar.isDragging());
      this.recordedAudioSeekBar.setProgress(this.audioToSendMessageObject.audioProgress);
      return;
    } while ((paramInt != NotificationCenter.featuredStickersDidLoaded) || (this.emojiButton == null));
    this.emojiButton.invalidate();
  }
  
  public void doneEditingMessage()
  {
    if (this.editingMessageObject != null)
    {
      this.delegate.onMessageEditEnd(true);
      CharSequence[] arrayOfCharSequence = new CharSequence[1];
      arrayOfCharSequence[0] = this.messageEditText.getText();
      ArrayList localArrayList = MessagesQuery.getEntities(arrayOfCharSequence);
      this.editingMessageReqId = SendMessagesHelper.getInstance().editMessage(this.editingMessageObject, arrayOfCharSequence[0].toString(), this.messageWebPageSearch, this.parentFragment, localArrayList, new Runnable()
      {
        public void run()
        {
          ChatActivityEnterView.access$4802(ChatActivityEnterView.this, 0);
          ChatActivityEnterView.this.setEditingMessageObject(null, false);
        }
      });
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    if (paramView == this.topView)
    {
      paramCanvas.save();
      paramCanvas.clipRect(0, 0, getMeasuredWidth(), paramView.getLayoutParams().height + AndroidUtilities.dp(2.0F));
    }
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    if (paramView == this.topView) {
      paramCanvas.restore();
    }
    return bool;
  }
  
  public int getCursorPosition()
  {
    if (this.messageEditText == null) {
      return 0;
    }
    return this.messageEditText.getSelectionStart();
  }
  
  public MessageObject getEditingMessageObject()
  {
    return this.editingMessageObject;
  }
  
  public int getEmojiPadding()
  {
    return this.emojiPadding;
  }
  
  public CharSequence getFieldText()
  {
    if ((this.messageEditText != null) && (this.messageEditText.length() > 0)) {
      return this.messageEditText.getText();
    }
    return null;
  }
  
  public boolean hasAudioToSend()
  {
    return this.audioToSendMessageObject != null;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public boolean hasText()
  {
    return (this.messageEditText != null) && (this.messageEditText.length() > 0);
  }
  
  public void hidePopup(boolean paramBoolean)
  {
    if (isPopupShowing())
    {
      if ((this.currentPopupContentType == 1) && (paramBoolean) && (this.botButtonsMessageObject != null)) {
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
      }
      showPopup(0, 0);
      removeGifFromInputField();
    }
  }
  
  public void hideTopView(boolean paramBoolean)
  {
    if ((this.topView == null) || (!this.topViewShowed)) {}
    do
    {
      return;
      this.topViewShowed = false;
      this.needShowTopView = false;
    } while (!this.allowShowTopView);
    if (this.currentTopViewAnimation != null)
    {
      this.currentTopViewAnimation.cancel();
      this.currentTopViewAnimation = null;
    }
    if (paramBoolean)
    {
      this.currentTopViewAnimation = new AnimatorSet();
      this.currentTopViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.topView, "translationY", new float[] { this.topView.getLayoutParams().height }) });
      this.currentTopViewAnimation.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((ChatActivityEnterView.this.currentTopViewAnimation != null) && (ChatActivityEnterView.this.currentTopViewAnimation.equals(paramAnonymousAnimator))) {
            ChatActivityEnterView.access$4302(ChatActivityEnterView.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((ChatActivityEnterView.this.currentTopViewAnimation != null) && (ChatActivityEnterView.this.currentTopViewAnimation.equals(paramAnonymousAnimator)))
          {
            ChatActivityEnterView.this.topView.setVisibility(8);
            ChatActivityEnterView.this.resizeForTopView(false);
            ChatActivityEnterView.access$4302(ChatActivityEnterView.this, null);
          }
        }
      });
      this.currentTopViewAnimation.setDuration(200L);
      this.currentTopViewAnimation.start();
      return;
    }
    this.topView.setVisibility(8);
    this.topView.setTranslationY(this.topView.getLayoutParams().height);
  }
  
  public boolean isEditingCaption()
  {
    return this.editingCaption;
  }
  
  public boolean isEditingMessage()
  {
    return this.editingMessageObject != null;
  }
  
  public boolean isKeyboardVisible()
  {
    return this.keyboardVisible;
  }
  
  public boolean isMessageWebPageSearchEnabled()
  {
    return this.messageWebPageSearch;
  }
  
  public boolean isPopupShowing()
  {
    return ((this.emojiView != null) && (this.emojiView.getVisibility() == 0)) || ((this.botKeyboardView != null) && (this.botKeyboardView.getVisibility() == 0));
  }
  
  public boolean isPopupView(View paramView)
  {
    return (paramView == this.botKeyboardView) || (paramView == this.emojiView);
  }
  
  public boolean isRecordCircle(View paramView)
  {
    return paramView == this.recordCircle;
  }
  
  public boolean isTopViewVisible()
  {
    return (this.topView != null) && (this.topView.getVisibility() == 0);
  }
  
  public void onDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStarted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStartError);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStopped);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordProgressChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidSent);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioRouteChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    if (this.emojiView != null) {
      this.emojiView.onDestroy();
    }
    if (this.mWakeLock != null) {}
    try
    {
      this.mWakeLock.release();
      this.mWakeLock = null;
      if (this.sizeNotifierLayout != null) {
        this.sizeNotifierLayout.setDelegate(null);
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if ((this.topView != null) && (this.topView.getVisibility() == 0)) {}
    for (int i = (int)this.topView.getTranslationY();; i = 0)
    {
      this.backgroundDrawable.setBounds(0, i, getMeasuredWidth(), getMeasuredHeight());
      this.backgroundDrawable.draw(paramCanvas);
      return;
    }
  }
  
  public void onPause()
  {
    this.isPaused = true;
    closeKeyboard();
  }
  
  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if ((paramInt == 2) && (this.pendingLocationButton != null))
    {
      if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0)) {
        SendMessagesHelper.getInstance().sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
      }
      this.pendingLocationButton = null;
      this.pendingMessageObject = null;
    }
  }
  
  public void onResume()
  {
    this.isPaused = false;
    if (this.showKeyboardOnResume)
    {
      this.showKeyboardOnResume = false;
      this.messageEditText.requestFocus();
      AndroidUtilities.showKeyboard(this.messageEditText);
      if ((!AndroidUtilities.usingHardwareInput) && (!this.keyboardVisible) && (!AndroidUtilities.isInMultiwindow))
      {
        this.waitingForKeyboardOpen = true;
        AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
      }
    }
  }
  
  public void onSizeChanged(int paramInt, boolean paramBoolean)
  {
    boolean bool1 = true;
    int i;
    label84:
    int j;
    Object localObject;
    if ((paramInt > AndroidUtilities.dp(50.0F)) && (this.keyboardVisible) && (!AndroidUtilities.isInMultiwindow))
    {
      if (paramBoolean)
      {
        this.keyboardHeightLand = paramInt;
        ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
      }
    }
    else if (isPopupShowing())
    {
      if (!paramBoolean) {
        break label301;
      }
      i = this.keyboardHeightLand;
      j = i;
      if (this.currentPopupContentType == 1)
      {
        j = i;
        if (!this.botKeyboardView.isFullSize()) {
          j = Math.min(this.botKeyboardView.getKeyboardHeight(), i);
        }
      }
      localObject = null;
      if (this.currentPopupContentType != 0) {
        break label309;
      }
      localObject = this.emojiView;
    }
    for (;;)
    {
      if (this.botKeyboardView != null) {
        this.botKeyboardView.setPanelHeight(j);
      }
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)((View)localObject).getLayoutParams();
      if ((localLayoutParams.width != AndroidUtilities.displaySize.x) || (localLayoutParams.height != j))
      {
        localLayoutParams.width = AndroidUtilities.displaySize.x;
        localLayoutParams.height = j;
        ((View)localObject).setLayoutParams(localLayoutParams);
        if (this.sizeNotifierLayout != null)
        {
          this.emojiPadding = localLayoutParams.height;
          this.sizeNotifierLayout.requestLayout();
          onWindowSizeChanged();
        }
      }
      if ((this.lastSizeChangeValue1 != paramInt) || (this.lastSizeChangeValue2 != paramBoolean)) {
        break label326;
      }
      onWindowSizeChanged();
      return;
      this.keyboardHeight = paramInt;
      ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height", this.keyboardHeight).commit();
      break;
      label301:
      i = this.keyboardHeight;
      break label84;
      label309:
      if (this.currentPopupContentType == 1) {
        localObject = this.botKeyboardView;
      }
    }
    label326:
    this.lastSizeChangeValue1 = paramInt;
    this.lastSizeChangeValue2 = paramBoolean;
    boolean bool2 = this.keyboardVisible;
    if (paramInt > 0) {}
    for (paramBoolean = bool1;; paramBoolean = false)
    {
      this.keyboardVisible = paramBoolean;
      if ((this.keyboardVisible) && (isPopupShowing())) {
        showPopup(0, this.currentPopupContentType);
      }
      if ((this.emojiPadding != 0) && (!this.keyboardVisible) && (this.keyboardVisible != bool2) && (!isPopupShowing()))
      {
        this.emojiPadding = 0;
        this.sizeNotifierLayout.requestLayout();
      }
      if ((this.keyboardVisible) && (this.waitingForKeyboardOpen))
      {
        this.waitingForKeyboardOpen = false;
        AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
      }
      onWindowSizeChanged();
      return;
    }
  }
  
  public void onStickerSelected(TLRPC.Document paramDocument)
  {
    SendMessagesHelper.getInstance().sendSticker(paramDocument, this.dialog_id, this.replyingMessageObject);
    if (this.delegate != null) {
      this.delegate.onMessageSend(null);
    }
  }
  
  public void openKeyboard()
  {
    AndroidUtilities.showKeyboard(this.messageEditText);
  }
  
  public boolean processSendingText(CharSequence paramCharSequence)
  {
    paramCharSequence = AndroidUtilities.getTrimmedString(paramCharSequence);
    if (paramCharSequence.length() != 0)
    {
      int j = (int)Math.ceil(paramCharSequence.length() / 4096.0F);
      int i = 0;
      while (i < j)
      {
        CharSequence[] arrayOfCharSequence = new CharSequence[1];
        arrayOfCharSequence[0] = paramCharSequence.subSequence(i * 4096, Math.min((i + 1) * 4096, paramCharSequence.length()));
        ArrayList localArrayList = MessagesQuery.getEntities(arrayOfCharSequence);
        SendMessagesHelper.getInstance().sendMessage(arrayOfCharSequence[0].toString(), this.dialog_id, this.replyingMessageObject, this.messageWebPage, this.messageWebPageSearch, localArrayList, null, null);
        i += 1;
      }
      return true;
    }
    return false;
  }
  
  public void replaceWithText(int paramInt1, int paramInt2, CharSequence paramCharSequence)
  {
    try
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(this.messageEditText.getText());
      localSpannableStringBuilder.replace(paramInt1, paramInt1 + paramInt2, paramCharSequence);
      this.messageEditText.setText(localSpannableStringBuilder);
      this.messageEditText.setSelection(paramCharSequence.length() + paramInt1);
      return;
    }
    catch (Exception paramCharSequence)
    {
      FileLog.e("tmessages", paramCharSequence);
    }
  }
  
  public void setAllowStickersAndGifs(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (((this.allowStickers != paramBoolean1) || (this.allowGifs != paramBoolean2)) && (this.emojiView != null))
    {
      if (this.emojiView.getVisibility() == 0) {
        hidePopup(false);
      }
      this.sizeNotifierLayout.removeView(this.emojiView);
      this.emojiView = null;
    }
    this.allowStickers = paramBoolean1;
    this.allowGifs = paramBoolean2;
    setEmojiButtonImage();
  }
  
  public void setBotsCount(int paramInt, boolean paramBoolean)
  {
    this.botCount = paramInt;
    if (this.hasBotCommands != paramBoolean)
    {
      this.hasBotCommands = paramBoolean;
      updateBotButton();
    }
  }
  
  public void setButtons(MessageObject paramMessageObject)
  {
    setButtons(paramMessageObject, true);
  }
  
  public void setButtons(MessageObject paramMessageObject, boolean paramBoolean)
  {
    Object localObject2 = null;
    if ((this.replyingMessageObject != null) && (this.replyingMessageObject == this.botButtonsMessageObject) && (this.replyingMessageObject != paramMessageObject)) {
      this.botMessageObject = paramMessageObject;
    }
    while ((this.botButton == null) || ((this.botButtonsMessageObject != null) && (this.botButtonsMessageObject == paramMessageObject)) || ((this.botButtonsMessageObject == null) && (paramMessageObject == null))) {
      return;
    }
    if (this.botKeyboardView == null)
    {
      this.botKeyboardView = new BotKeyboardView(this.parentActivity);
      this.botKeyboardView.setVisibility(8);
      this.botKeyboardView.setDelegate(new BotKeyboardView.BotKeyboardViewDelegate()
      {
        public void didPressedButton(TLRPC.KeyboardButton paramAnonymousKeyboardButton)
        {
          MessageObject localMessageObject1;
          MessageObject localMessageObject2;
          if (ChatActivityEnterView.this.replyingMessageObject != null)
          {
            localMessageObject1 = ChatActivityEnterView.this.replyingMessageObject;
            ChatActivityEnterView localChatActivityEnterView = ChatActivityEnterView.this;
            if (ChatActivityEnterView.this.replyingMessageObject == null) {
              break label133;
            }
            localMessageObject2 = ChatActivityEnterView.this.replyingMessageObject;
            label42:
            localChatActivityEnterView.didPressedBotButton(paramAnonymousKeyboardButton, localMessageObject1, localMessageObject2);
            if (ChatActivityEnterView.this.replyingMessageObject == null) {
              break label144;
            }
            ChatActivityEnterView.this.openKeyboardInternal();
            ChatActivityEnterView.this.setButtons(ChatActivityEnterView.this.botMessageObject, false);
          }
          for (;;)
          {
            if (ChatActivityEnterView.this.delegate != null) {
              ChatActivityEnterView.this.delegate.onMessageSend(null);
            }
            return;
            if ((int)ChatActivityEnterView.this.dialog_id < 0)
            {
              localMessageObject1 = ChatActivityEnterView.this.botButtonsMessageObject;
              break;
            }
            localMessageObject1 = null;
            break;
            label133:
            localMessageObject2 = ChatActivityEnterView.this.botButtonsMessageObject;
            break label42;
            label144:
            if (ChatActivityEnterView.this.botButtonsMessageObject.messageOwner.reply_markup.single_use)
            {
              ChatActivityEnterView.this.openKeyboardInternal();
              ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("answered_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
            }
          }
        }
      });
      this.sizeNotifierLayout.addView(this.botKeyboardView);
    }
    this.botButtonsMessageObject = paramMessageObject;
    Object localObject1;
    label159:
    int i;
    if ((paramMessageObject != null) && ((paramMessageObject.messageOwner.reply_markup instanceof TLRPC.TL_replyKeyboardMarkup)))
    {
      localObject1 = (TLRPC.TL_replyKeyboardMarkup)paramMessageObject.messageOwner.reply_markup;
      this.botReplyMarkup = ((TLRPC.TL_replyKeyboardMarkup)localObject1);
      localObject1 = this.botKeyboardView;
      if (AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
        break label383;
      }
      i = this.keyboardHeightLand;
      label191:
      ((BotKeyboardView)localObject1).setPanelHeight(i);
      BotKeyboardView localBotKeyboardView = this.botKeyboardView;
      localObject1 = localObject2;
      if (this.botReplyMarkup != null) {
        localObject1 = this.botReplyMarkup;
      }
      localBotKeyboardView.setButtons((TLRPC.TL_replyKeyboardMarkup)localObject1);
      if (this.botReplyMarkup == null) {
        break label396;
      }
      localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
      if (((SharedPreferences)localObject1).getInt("hidekeyboard_" + this.dialog_id, 0) != paramMessageObject.getId()) {
        break label391;
      }
      i = 1;
      label286:
      if ((this.botButtonsMessageObject != this.replyingMessageObject) && (this.botReplyMarkup.single_use) && (((SharedPreferences)localObject1).getInt("answered_" + this.dialog_id, 0) == paramMessageObject.getId())) {
        break label394;
      }
      if ((i == 0) && (this.messageEditText.length() == 0) && (!isPopupShowing())) {
        showPopup(1, 1);
      }
    }
    for (;;)
    {
      updateBotButton();
      return;
      localObject1 = null;
      break label159;
      label383:
      i = this.keyboardHeight;
      break label191;
      label391:
      i = 0;
      break label286;
      label394:
      break;
      label396:
      if ((isPopupShowing()) && (this.currentPopupContentType == 1)) {
        if (paramBoolean) {
          openKeyboardInternal();
        } else {
          showPopup(0, 1);
        }
      }
    }
  }
  
  public void setCaption(String paramString)
  {
    if (this.messageEditText != null)
    {
      this.messageEditText.setCaption(paramString);
      checkSendButton(true);
    }
  }
  
  public void setCommand(MessageObject paramMessageObject, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramString == null) || (getVisibility() != 0)) {
      return;
    }
    if (paramBoolean1)
    {
      String str = this.messageEditText.getText().toString();
      if ((paramMessageObject != null) && ((int)this.dialog_id < 0))
      {
        paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
        label57:
        if (((this.botCount == 1) && (!paramBoolean2)) || (paramMessageObject == null) || (!paramMessageObject.bot) || (paramString.contains("@"))) {
          break label235;
        }
      }
      label235:
      for (paramMessageObject = String.format(Locale.US, "%s@%s", new Object[] { paramString, paramMessageObject.username }) + " " + str.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", "");; paramMessageObject = paramString + " " + str.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", ""))
      {
        this.ignoreTextChange = true;
        this.messageEditText.setText(paramMessageObject);
        this.messageEditText.setSelection(this.messageEditText.getText().length());
        this.ignoreTextChange = false;
        if (this.delegate != null) {
          this.delegate.onTextChanged(this.messageEditText.getText(), true);
        }
        if ((this.keyboardVisible) || (this.currentPopupContentType != -1)) {
          break;
        }
        openKeyboard();
        return;
        paramMessageObject = null;
        break label57;
      }
    }
    if ((paramMessageObject != null) && ((int)this.dialog_id < 0)) {}
    for (paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id)); ((this.botCount != 1) || (paramBoolean2)) && (paramMessageObject != null) && (paramMessageObject.bot) && (!paramString.contains("@")); paramMessageObject = null)
    {
      SendMessagesHelper.getInstance().sendMessage(String.format(Locale.US, "%s@%s", new Object[] { paramString, paramMessageObject.username }), this.dialog_id, null, null, false, null, null, null);
      return;
    }
    SendMessagesHelper.getInstance().sendMessage(paramString, this.dialog_id, null, null, false, null, null, null);
  }
  
  public void setDelegate(ChatActivityEnterViewDelegate paramChatActivityEnterViewDelegate)
  {
    this.delegate = paramChatActivityEnterViewDelegate;
  }
  
  public void setDialogId(long paramLong)
  {
    int j = 1;
    this.dialog_id = paramLong;
    boolean bool;
    label140:
    label165:
    float f;
    if ((int)this.dialog_id < 0)
    {
      Object localObject = MessagesController.getInstance().getChat(Integer.valueOf(-(int)this.dialog_id));
      this.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + this.dialog_id, false);
      if ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || ((!((TLRPC.Chat)localObject).creator) && (!((TLRPC.Chat)localObject).editor)) || (((TLRPC.Chat)localObject).megagroup)) {
        break label262;
      }
      bool = true;
      this.canWriteToChannel = bool;
      if (this.notifyButton != null)
      {
        localObject = this.notifyButton;
        if (!this.canWriteToChannel) {
          break label268;
        }
        i = 0;
        ((ImageView)localObject).setVisibility(i);
        localObject = this.notifyButton;
        if (!this.silent) {
          break label275;
        }
        i = 2130837860;
        ((ImageView)localObject).setImageResource(i);
        localObject = this.attachButton;
        if (((this.botButton != null) && (this.botButton.getVisibility() != 8)) || ((this.notifyButton != null) && (this.notifyButton.getVisibility() != 8))) {
          break label283;
        }
        f = 48.0F;
        label220:
        ((LinearLayout)localObject).setPivotX(AndroidUtilities.dp(f));
      }
      if (this.attachButton != null) {
        if (this.attachButton.getVisibility() != 0) {
          break label290;
        }
      }
    }
    label262:
    label268:
    label275:
    label283:
    label290:
    for (int i = j;; i = 0)
    {
      updateFieldRight(i);
      updateFieldHint();
      return;
      bool = false;
      break;
      i = 8;
      break label140;
      i = 2130837861;
      break label165;
      f = 96.0F;
      break label220;
    }
  }
  
  public void setEditingMessageObject(MessageObject paramMessageObject, boolean paramBoolean)
  {
    if ((this.audioToSend != null) || (this.editingMessageObject == paramMessageObject)) {
      return;
    }
    if (this.editingMessageReqId != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.editingMessageReqId, true);
      this.editingMessageReqId = 0;
    }
    this.editingMessageObject = paramMessageObject;
    this.editingCaption = paramBoolean;
    if (this.editingMessageObject != null)
    {
      paramMessageObject = new InputFilter[1];
      if (paramBoolean)
      {
        paramMessageObject[0] = new InputFilter.LengthFilter(200);
        if (this.editingMessageObject.caption != null)
        {
          setFieldText(Emoji.replaceEmoji(new SpannableStringBuilder(this.editingMessageObject.caption.toString()), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false));
          this.messageEditText.setFilters(paramMessageObject);
          openKeyboard();
          paramMessageObject = (FrameLayout.LayoutParams)this.messageEditText.getLayoutParams();
          paramMessageObject.rightMargin = AndroidUtilities.dp(4.0F);
          this.messageEditText.setLayoutParams(paramMessageObject);
          this.sendButton.setVisibility(8);
          this.cancelBotButton.setVisibility(8);
          this.audioSendButton.setVisibility(8);
          this.attachButton.setVisibility(8);
          this.sendButtonContainer.setVisibility(8);
        }
      }
    }
    for (;;)
    {
      updateFieldHint();
      return;
      setFieldText("");
      break;
      paramMessageObject[0] = new InputFilter.LengthFilter(4096);
      if (this.editingMessageObject.messageText != null)
      {
        Object localObject = this.editingMessageObject.messageText;
        ArrayList localArrayList = this.editingMessageObject.messageOwner.entities;
        localObject = new SpannableStringBuilder(new CharSequence[] { localObject }[0]);
        int k = 0;
        if (localArrayList != null)
        {
          int j = 0;
          if (j < localArrayList.size())
          {
            TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)localArrayList.get(j);
            int i;
            if ((localMessageEntity instanceof TLRPC.TL_inputMessageEntityMentionName))
            {
              if ((localMessageEntity.offset + localMessageEntity.length + k < ((SpannableStringBuilder)localObject).length()) && (((SpannableStringBuilder)localObject).charAt(localMessageEntity.offset + localMessageEntity.length + k) == ' ')) {
                localMessageEntity.length += 1;
              }
              ((SpannableStringBuilder)localObject).setSpan(new URLSpanUserMention("" + ((TLRPC.TL_inputMessageEntityMentionName)localMessageEntity).user_id.user_id), localMessageEntity.offset + k, localMessageEntity.offset + localMessageEntity.length + k, 33);
              i = k;
            }
            for (;;)
            {
              j += 1;
              k = i;
              break;
              if ((localMessageEntity instanceof TLRPC.TL_messageEntityCode))
              {
                ((SpannableStringBuilder)localObject).insert(localMessageEntity.offset + localMessageEntity.length + k, "`");
                ((SpannableStringBuilder)localObject).insert(localMessageEntity.offset + k, "`");
                i = k + 2;
              }
              else
              {
                i = k;
                if ((localMessageEntity instanceof TLRPC.TL_messageEntityPre))
                {
                  ((SpannableStringBuilder)localObject).insert(localMessageEntity.offset + localMessageEntity.length + k, "```");
                  ((SpannableStringBuilder)localObject).insert(localMessageEntity.offset + k, "```");
                  i = k + 6;
                }
              }
            }
          }
        }
        setFieldText(Emoji.replaceEmoji((CharSequence)localObject, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false));
        break;
      }
      setFieldText("");
      break;
      this.messageEditText.setFilters(new InputFilter[0]);
      this.delegate.onMessageEditEnd(false);
      this.audioSendButton.setVisibility(0);
      this.attachButton.setVisibility(0);
      this.sendButtonContainer.setVisibility(0);
      this.attachButton.setScaleX(1.0F);
      this.attachButton.setAlpha(1.0F);
      this.sendButton.setScaleX(0.1F);
      this.sendButton.setScaleY(0.1F);
      this.sendButton.setAlpha(0.0F);
      this.cancelBotButton.setScaleX(0.1F);
      this.cancelBotButton.setScaleY(0.1F);
      this.cancelBotButton.setAlpha(0.0F);
      this.audioSendButton.setScaleX(1.0F);
      this.audioSendButton.setScaleY(1.0F);
      this.audioSendButton.setAlpha(1.0F);
      this.sendButton.setVisibility(8);
      this.cancelBotButton.setVisibility(8);
      this.messageEditText.setText("");
      if (getVisibility() == 0) {
        this.delegate.onAttachButtonShow();
      }
      updateFieldRight(1);
    }
  }
  
  public void setFieldFocused()
  {
    if (this.messageEditText != null) {}
    try
    {
      this.messageEditText.requestFocus();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public void setFieldFocused(boolean paramBoolean)
  {
    if (this.messageEditText == null) {}
    do
    {
      do
      {
        return;
        if (!paramBoolean) {
          break;
        }
      } while (this.messageEditText.isFocused());
      this.messageEditText.postDelayed(new Runnable()
      {
        public void run()
        {
          if (ChatActivityEnterView.this.messageEditText != null) {}
          try
          {
            ChatActivityEnterView.this.messageEditText.requestFocus();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
        }
      }, 600L);
      return;
    } while ((!this.messageEditText.isFocused()) || (this.keyboardVisible));
    this.messageEditText.clearFocus();
  }
  
  public void setFieldText(CharSequence paramCharSequence)
  {
    if (this.messageEditText == null) {}
    do
    {
      return;
      this.ignoreTextChange = true;
      this.messageEditText.setText(paramCharSequence);
      this.messageEditText.setSelection(this.messageEditText.getText().length());
      this.ignoreTextChange = false;
    } while (this.delegate == null);
    this.delegate.onTextChanged(this.messageEditText.getText(), true);
  }
  
  public void setForceShowSendButton(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.forceShowSendButton = paramBoolean1;
    checkSendButton(paramBoolean2);
  }
  
  public void setOpenGifsTabFirst()
  {
    createEmojiView();
    StickersQuery.loadRecents(0, true, true);
    this.emojiView.switchToGifRecent();
  }
  
  public void setReplyingMessageObject(MessageObject paramMessageObject)
  {
    if (paramMessageObject != null)
    {
      if ((this.botMessageObject == null) && (this.botButtonsMessageObject != this.replyingMessageObject)) {
        this.botMessageObject = this.botButtonsMessageObject;
      }
      this.replyingMessageObject = paramMessageObject;
      setButtons(this.replyingMessageObject, true);
      return;
    }
    if ((paramMessageObject == null) && (this.replyingMessageObject == this.botButtonsMessageObject))
    {
      this.replyingMessageObject = null;
      setButtons(this.botMessageObject, false);
      this.botMessageObject = null;
      return;
    }
    this.replyingMessageObject = paramMessageObject;
  }
  
  public void setSelection(int paramInt)
  {
    if (this.messageEditText == null) {
      return;
    }
    this.messageEditText.setSelection(paramInt, this.messageEditText.length());
  }
  
  public void setWebPage(TLRPC.WebPage paramWebPage, boolean paramBoolean)
  {
    this.messageWebPage = paramWebPage;
    this.messageWebPageSearch = paramBoolean;
  }
  
  public void showContextProgress(boolean paramBoolean)
  {
    if (this.progressDrawable == null) {
      return;
    }
    if (paramBoolean)
    {
      this.progressDrawable.startAnimation();
      return;
    }
    this.progressDrawable.stopAnimation();
  }
  
  public void showTopView(boolean paramBoolean1, final boolean paramBoolean2)
  {
    if ((this.topView == null) || (this.topViewShowed) || (getVisibility() != 0)) {}
    do
    {
      do
      {
        return;
        this.needShowTopView = true;
        this.topViewShowed = true;
      } while (!this.allowShowTopView);
      this.topView.setVisibility(0);
      if (this.currentTopViewAnimation != null)
      {
        this.currentTopViewAnimation.cancel();
        this.currentTopViewAnimation = null;
      }
      resizeForTopView(true);
      if (!paramBoolean1) {
        break;
      }
      if ((this.keyboardVisible) || (isPopupShowing()))
      {
        this.currentTopViewAnimation = new AnimatorSet();
        this.currentTopViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.topView, "translationY", new float[] { 0.0F }) });
        this.currentTopViewAnimation.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator)
          {
            if ((ChatActivityEnterView.this.currentTopViewAnimation != null) && (ChatActivityEnterView.this.currentTopViewAnimation.equals(paramAnonymousAnimator))) {
              ChatActivityEnterView.access$4302(ChatActivityEnterView.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((ChatActivityEnterView.this.currentTopViewAnimation != null) && (ChatActivityEnterView.this.currentTopViewAnimation.equals(paramAnonymousAnimator)))
            {
              if ((ChatActivityEnterView.this.recordedAudioPanel.getVisibility() != 0) && ((!ChatActivityEnterView.this.forceShowSendButton) || (paramBoolean2))) {
                ChatActivityEnterView.this.openKeyboard();
              }
              ChatActivityEnterView.access$4302(ChatActivityEnterView.this, null);
            }
          }
        });
        this.currentTopViewAnimation.setDuration(200L);
        this.currentTopViewAnimation.start();
        return;
      }
      this.topView.setTranslationY(0.0F);
    } while ((this.recordedAudioPanel.getVisibility() == 0) || ((this.forceShowSendButton) && (!paramBoolean2)));
    openKeyboard();
    return;
    this.topView.setTranslationY(0.0F);
  }
  
  public static abstract interface ChatActivityEnterViewDelegate
  {
    public abstract void needSendTyping();
    
    public abstract void onAttachButtonHidden();
    
    public abstract void onAttachButtonShow();
    
    public abstract void onMessageEditEnd(boolean paramBoolean);
    
    public abstract void onMessageSend(CharSequence paramCharSequence);
    
    public abstract void onStickersTab(boolean paramBoolean);
    
    public abstract void onTextChanged(CharSequence paramCharSequence, boolean paramBoolean);
    
    public abstract void onWindowSizeChanged(int paramInt);
  }
  
  private class EditTextCaption
    extends EditText
  {
    private String caption;
    private StaticLayout captionLayout;
    private Object editor;
    private Field editorField;
    private Drawable[] mCursorDrawable;
    private Field mCursorDrawableField;
    private int triesCount = 0;
    private int userNameLength;
    private int xOffset;
    private int yOffset;
    
    public EditTextCaption(Context paramContext)
    {
      super();
      try
      {
        this$1 = TextView.class.getDeclaredField("mEditor");
        ChatActivityEnterView.this.setAccessible(true);
        this.editor = ChatActivityEnterView.this.get(this);
        this$1 = Class.forName("android.widget.Editor");
        this.editorField = ChatActivityEnterView.this.getDeclaredField("mShowCursor");
        this.editorField.setAccessible(true);
        this.mCursorDrawableField = ChatActivityEnterView.this.getDeclaredField("mCursorDrawable");
        this.mCursorDrawableField.setAccessible(true);
        this.mCursorDrawable = ((Drawable[])this.mCursorDrawableField.get(this.editor));
        return;
      }
      catch (Throwable this$1) {}
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int i = 0;
      try
      {
        super.onDraw(paramCanvas);
        if ((this.captionLayout != null) && (this.userNameLength == length()))
        {
          TextPaint localTextPaint = getPaint();
          int j = getPaint().getColor();
          localTextPaint.setColor(-5066062);
          paramCanvas.save();
          paramCanvas.translate(this.xOffset, this.yOffset);
          this.captionLayout.draw(paramCanvas);
          paramCanvas.restore();
          localTextPaint.setColor(j);
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          try
          {
            if ((this.editorField != null) && (this.mCursorDrawable != null) && (this.mCursorDrawable[0] != null))
            {
              long l = this.editorField.getLong(this.editor);
              if ((SystemClock.uptimeMillis() - l) % 1000L < 500L) {
                i = 1;
              }
              if (i != 0)
              {
                paramCanvas.save();
                paramCanvas.translate(0.0F, getPaddingTop());
                this.mCursorDrawable[0].draw(paramCanvas);
                paramCanvas.restore();
              }
            }
            return;
          }
          catch (Throwable paramCanvas) {}
          localException = localException;
          FileLog.e("tmessages", localException);
        }
      }
    }
    
    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      try
      {
        super.onMeasure(paramInt1, paramInt2);
        this.captionLayout = null;
        if ((this.caption != null) && (this.caption.length() > 0))
        {
          localObject = getText();
          if ((((CharSequence)localObject).length() > 1) && (((CharSequence)localObject).charAt(0) == '@'))
          {
            paramInt1 = TextUtils.indexOf((CharSequence)localObject, ' ');
            if (paramInt1 != -1)
            {
              TextPaint localTextPaint = getPaint();
              CharSequence localCharSequence = ((CharSequence)localObject).subSequence(0, paramInt1 + 1);
              paramInt1 = (int)Math.ceil(localTextPaint.measureText((CharSequence)localObject, 0, paramInt1 + 1));
              paramInt2 = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
              this.userNameLength = localCharSequence.length();
              localObject = TextUtils.ellipsize(this.caption, localTextPaint, paramInt2 - paramInt1, TextUtils.TruncateAt.END);
              this.xOffset = paramInt1;
            }
          }
        }
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          try
          {
            Object localObject;
            this.captionLayout = new StaticLayout((CharSequence)localObject, getPaint(), paramInt2 - paramInt1, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            if (this.captionLayout.getLineCount() > 0) {
              this.xOffset = ((int)(this.xOffset + -this.captionLayout.getLineLeft(0)));
            }
            this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2 + AndroidUtilities.dp(0.5F));
            return;
          }
          catch (Exception localException2)
          {
            FileLog.e("tmessages", localException2);
          }
          localException1 = localException1;
          setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(51.0F));
          FileLog.e("tmessages", localException1);
        }
      }
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      ChatActivityEnterView localChatActivityEnterView;
      if ((ChatActivityEnterView.this.isPopupShowing()) && (paramMotionEvent.getAction() == 0))
      {
        localChatActivityEnterView = ChatActivityEnterView.this;
        if (!AndroidUtilities.usingHardwareInput) {
          break label53;
        }
      }
      for (int i = 0;; i = 2)
      {
        localChatActivityEnterView.showPopup(i, 0);
        ChatActivityEnterView.this.openKeyboardInternal();
        try
        {
          boolean bool = super.onTouchEvent(paramMotionEvent);
          return bool;
        }
        catch (Exception paramMotionEvent)
        {
          label53:
          FileLog.e("tmessages", paramMotionEvent);
        }
      }
      return false;
    }
    
    public void setCaption(String paramString)
    {
      if (((this.caption != null) && (this.caption.length() != 0)) || ((paramString == null) || (paramString.length() == 0) || ((this.caption != null) && (paramString != null) && (this.caption.equals(paramString))))) {
        return;
      }
      this.caption = paramString;
      if (this.caption != null) {
        this.caption = this.caption.replace('\n', ' ');
      }
      requestLayout();
    }
  }
  
  private class RecordCircle
    extends View
  {
    private float amplitude;
    private float animateAmplitudeDiff;
    private float animateToAmplitude;
    private long lastUpdateTime;
    private Drawable micDrawable;
    private Paint paint = new Paint(1);
    private Paint paintRecord = new Paint(1);
    private float scale;
    
    public RecordCircle(Context paramContext)
    {
      super();
      this.paint.setColor(-11037236);
      this.paintRecord.setColor(218103808);
      this.micDrawable = getResources().getDrawable(2130837827);
    }
    
    public float getScale()
    {
      return this.scale;
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int i = getMeasuredWidth() / 2;
      int j = getMeasuredHeight() / 2;
      float f2;
      float f1;
      if (this.scale <= 0.5F)
      {
        f2 = this.scale / 0.5F;
        f1 = f2;
        long l1 = System.currentTimeMillis();
        long l2 = this.lastUpdateTime;
        if (this.animateToAmplitude != this.amplitude)
        {
          this.amplitude += this.animateAmplitudeDiff * (float)(l1 - l2);
          if (this.animateAmplitudeDiff <= 0.0F) {
            break label332;
          }
          if (this.amplitude > this.animateToAmplitude) {
            this.amplitude = this.animateToAmplitude;
          }
        }
      }
      for (;;)
      {
        invalidate();
        this.lastUpdateTime = System.currentTimeMillis();
        if (this.amplitude != 0.0F) {
          paramCanvas.drawCircle(getMeasuredWidth() / 2.0F, getMeasuredHeight() / 2.0F, (AndroidUtilities.dp(42.0F) + AndroidUtilities.dp(20.0F) * this.amplitude) * this.scale, this.paintRecord);
        }
        paramCanvas.drawCircle(getMeasuredWidth() / 2.0F, getMeasuredHeight() / 2.0F, AndroidUtilities.dp(42.0F) * f2, this.paint);
        this.micDrawable.setBounds(i - this.micDrawable.getIntrinsicWidth() / 2, j - this.micDrawable.getIntrinsicHeight() / 2, this.micDrawable.getIntrinsicWidth() / 2 + i, this.micDrawable.getIntrinsicHeight() / 2 + j);
        this.micDrawable.setAlpha((int)(255.0F * f1));
        this.micDrawable.draw(paramCanvas);
        return;
        if (this.scale <= 0.75F)
        {
          f2 = 1.0F - (this.scale - 0.5F) / 0.25F * 0.1F;
          f1 = 1.0F;
          break;
        }
        f2 = 0.9F + (this.scale - 0.75F) / 0.25F * 0.1F;
        f1 = 1.0F;
        break;
        label332:
        if (this.amplitude < this.animateToAmplitude) {
          this.amplitude = this.animateToAmplitude;
        }
      }
    }
    
    public void setAmplitude(double paramDouble)
    {
      this.animateToAmplitude = ((float)Math.min(100.0D, paramDouble) / 100.0F);
      this.animateAmplitudeDiff = ((this.animateToAmplitude - this.amplitude) / 150.0F);
      this.lastUpdateTime = System.currentTimeMillis();
      invalidate();
    }
    
    public void setScale(float paramFloat)
    {
      this.scale = paramFloat;
      invalidate();
    }
  }
  
  private class RecordDot
    extends View
  {
    private float alpha;
    private Drawable dotDrawable = getResources().getDrawable(2130837952);
    private boolean isIncr;
    private long lastUpdateTime;
    
    public RecordDot(Context paramContext)
    {
      super();
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      this.dotDrawable.setBounds(0, 0, AndroidUtilities.dp(11.0F), AndroidUtilities.dp(11.0F));
      this.dotDrawable.setAlpha((int)(255.0F * this.alpha));
      long l = System.currentTimeMillis() - this.lastUpdateTime;
      if (!this.isIncr)
      {
        this.alpha -= (float)l / 400.0F;
        if (this.alpha <= 0.0F)
        {
          this.alpha = 0.0F;
          this.isIncr = true;
        }
      }
      for (;;)
      {
        this.lastUpdateTime = System.currentTimeMillis();
        this.dotDrawable.draw(paramCanvas);
        invalidate();
        return;
        this.alpha += (float)l / 400.0F;
        if (this.alpha >= 1.0F)
        {
          this.alpha = 1.0F;
          this.isIncr = false;
        }
      }
    }
    
    public void resetAlpha()
    {
      this.alpha = 1.0F;
      this.lastUpdateTime = System.currentTimeMillis();
      this.isIncr = false;
      invalidate();
    }
  }
  
  private class SeekBarWaveformView
    extends View
  {
    private SeekBarWaveform seekBarWaveform;
    
    public SeekBarWaveformView(Context paramContext)
    {
      super();
      this.seekBarWaveform = new SeekBarWaveform(paramContext);
      this.seekBarWaveform.setColors(-6107400, -1, -6107400);
      this.seekBarWaveform.setDelegate(new SeekBar.SeekBarDelegate()
      {
        public void onSeekBarDrag(float paramAnonymousFloat)
        {
          if (ChatActivityEnterView.this.audioToSendMessageObject != null)
          {
            ChatActivityEnterView.this.audioToSendMessageObject.audioProgress = paramAnonymousFloat;
            MediaController.getInstance().seekToProgress(ChatActivityEnterView.this.audioToSendMessageObject, paramAnonymousFloat);
          }
        }
      });
    }
    
    public boolean isDragging()
    {
      return this.seekBarWaveform.isDragging();
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      super.onDraw(paramCanvas);
      this.seekBarWaveform.draw(paramCanvas);
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      this.seekBarWaveform.setSize(paramInt3 - paramInt1, paramInt4 - paramInt2);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool = this.seekBarWaveform.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX(), paramMotionEvent.getY());
      if (bool)
      {
        if (paramMotionEvent.getAction() == 0) {
          ChatActivityEnterView.this.requestDisallowInterceptTouchEvent(true);
        }
        invalidate();
      }
      return (bool) || (super.onTouchEvent(paramMotionEvent));
    }
    
    public void setProgress(float paramFloat)
    {
      this.seekBarWaveform.setProgress(paramFloat);
      invalidate();
    }
    
    public void setWaveform(byte[] paramArrayOfByte)
    {
      this.seekBarWaveform.setWaveform(paramArrayOfByte);
      invalidate();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ChatActivityEnterView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */