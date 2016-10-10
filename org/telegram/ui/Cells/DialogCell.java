package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.Components.AvatarDrawable;

public class DialogCell
  extends BaseCell
{
  private static Paint backPaint;
  private static Drawable botDrawable;
  private static Drawable broadcastDrawable;
  private static Drawable checkDrawable;
  private static Drawable clockDrawable;
  private static Drawable countDrawable;
  private static Drawable countDrawableGrey;
  private static TextPaint countPaint;
  private static Drawable errorDrawable;
  private static Drawable groupDrawable;
  private static Drawable halfCheckDrawable;
  private static Paint linePaint;
  private static Drawable lockDrawable;
  private static TextPaint messagePaint;
  private static TextPaint messagePrintingPaint;
  private static Drawable muteDrawable;
  private static TextPaint nameEncryptedPaint;
  private static TextPaint namePaint;
  private static TextPaint timePaint;
  private static Drawable verifiedDrawable;
  private AvatarDrawable avatarDrawable;
  private ImageReceiver avatarImage;
  private int avatarTop = AndroidUtilities.dp(10.0F);
  private TLRPC.Chat chat = null;
  private int checkDrawLeft;
  private int checkDrawTop = AndroidUtilities.dp(18.0F);
  private StaticLayout countLayout;
  private int countLeft;
  private int countTop = AndroidUtilities.dp(39.0F);
  private int countWidth;
  private long currentDialogId;
  private int currentEditDate;
  private boolean dialogMuted;
  private int dialogsType;
  private TLRPC.DraftMessage draftMessage;
  private boolean drawCheck1;
  private boolean drawCheck2;
  private boolean drawClock;
  private boolean drawCount;
  private boolean drawError;
  private boolean drawNameBot;
  private boolean drawNameBroadcast;
  private boolean drawNameGroup;
  private boolean drawNameLock;
  private boolean drawVerified;
  private TLRPC.EncryptedChat encryptedChat = null;
  private int errorLeft;
  private int errorTop = AndroidUtilities.dp(39.0F);
  private int halfCheckDrawLeft;
  private int index;
  private boolean isDialogCell;
  private boolean isSelected;
  private int lastMessageDate;
  private CharSequence lastPrintString = null;
  private int lastSendState;
  private boolean lastUnreadState;
  private MessageObject message;
  private StaticLayout messageLayout;
  private int messageLeft;
  private int messageTop = AndroidUtilities.dp(40.0F);
  private StaticLayout nameLayout;
  private int nameLeft;
  private int nameLockLeft;
  private int nameLockTop;
  private int nameMuteLeft;
  private StaticLayout timeLayout;
  private int timeLeft;
  private int timeTop = AndroidUtilities.dp(17.0F);
  private int unreadCount;
  public boolean useSeparator = false;
  private TLRPC.User user = null;
  
  public DialogCell(Context paramContext)
  {
    super(paramContext);
    if (namePaint == null)
    {
      namePaint = new TextPaint(1);
      namePaint.setColor(-14606047);
      namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      nameEncryptedPaint = new TextPaint(1);
      nameEncryptedPaint.setColor(-16734706);
      nameEncryptedPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      messagePaint = new TextPaint(1);
      messagePaint.setColor(-7368817);
      messagePaint.linkColor = -7368817;
      linePaint = new Paint();
      linePaint.setColor(-2302756);
      backPaint = new Paint();
      backPaint.setColor(251658240);
      messagePrintingPaint = new TextPaint(1);
      messagePrintingPaint.setColor(-11697229);
      timePaint = new TextPaint(1);
      timePaint.setColor(-6710887);
      countPaint = new TextPaint(1);
      countPaint.setColor(-1);
      countPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      lockDrawable = getResources().getDrawable(2130837795);
      checkDrawable = getResources().getDrawable(2130837641);
      halfCheckDrawable = getResources().getDrawable(2130837642);
      clockDrawable = getResources().getDrawable(2130837834);
      errorDrawable = getResources().getDrawable(2130837643);
      countDrawable = getResources().getDrawable(2130837639);
      countDrawableGrey = getResources().getDrawable(2130837640);
      groupDrawable = getResources().getDrawable(2130837792);
      broadcastDrawable = getResources().getDrawable(2130837789);
      muteDrawable = getResources().getDrawable(2130837851);
      verifiedDrawable = getResources().getDrawable(2130837592);
      botDrawable = getResources().getDrawable(2130837573);
    }
    namePaint.setTextSize(AndroidUtilities.dp(17.0F));
    nameEncryptedPaint.setTextSize(AndroidUtilities.dp(17.0F));
    messagePaint.setTextSize(AndroidUtilities.dp(16.0F));
    messagePrintingPaint.setTextSize(AndroidUtilities.dp(16.0F));
    timePaint.setTextSize(AndroidUtilities.dp(13.0F));
    countPaint.setTextSize(AndroidUtilities.dp(13.0F));
    setBackgroundResource(2130837796);
    this.avatarImage = new ImageReceiver(this);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0F));
    this.avatarDrawable = new AvatarDrawable();
  }
  
  private ArrayList<TLRPC.TL_dialog> getDialogsArray()
  {
    if (this.dialogsType == 0) {
      return MessagesController.getInstance().dialogs;
    }
    if (this.dialogsType == 1) {
      return MessagesController.getInstance().dialogsServerOnly;
    }
    if (this.dialogsType == 2) {
      return MessagesController.getInstance().dialogsGroupsOnly;
    }
    return null;
  }
  
  public void buildLayout()
  {
    Object localObject6 = "";
    String str1 = "";
    Object localObject8 = null;
    Object localObject7 = null;
    String str2 = "";
    Object localObject1 = null;
    if (this.isDialogCell) {
      localObject1 = (CharSequence)MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentDialogId));
    }
    TextPaint localTextPaint = namePaint;
    Object localObject5 = messagePaint;
    int j = 1;
    this.drawNameGroup = false;
    this.drawNameBroadcast = false;
    this.drawNameLock = false;
    this.drawNameBot = false;
    this.drawVerified = false;
    int k;
    int i;
    label328:
    Object localObject2;
    Object localObject4;
    label356:
    label376:
    label408:
    int m;
    if (this.encryptedChat != null)
    {
      this.drawNameLock = true;
      this.nameLockTop = AndroidUtilities.dp(16.5F);
      if (!LocaleController.isRTL)
      {
        this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
        this.nameLeft = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + lockDrawable.getIntrinsicWidth());
        k = this.lastMessageDate;
        i = k;
        if (this.lastMessageDate == 0)
        {
          i = k;
          if (this.message != null) {
            i = this.message.messageOwner.date;
          }
        }
        if (!this.isDialogCell) {
          break label1594;
        }
        this.draftMessage = DraftQuery.getDraft(this.currentDialogId);
        if (((this.draftMessage != null) && (((TextUtils.isEmpty(this.draftMessage.message)) && (this.draftMessage.reply_to_msg_id == 0)) || ((i > this.draftMessage.date) && (this.unreadCount != 0)))) || ((ChatObject.isChannel(this.chat)) && (!this.chat.megagroup) && (!this.chat.creator) && (!this.chat.editor)) || ((this.chat != null) && ((this.chat.left) || (this.chat.kicked)))) {
          this.draftMessage = null;
        }
        if (localObject1 == null) {
          break label1602;
        }
        localObject2 = localObject1;
        this.lastPrintString = ((CharSequence)localObject1);
        localObject4 = messagePrintingPaint;
        localObject1 = localObject2;
        k = j;
        if (this.draftMessage == null) {
          break label2910;
        }
        localObject2 = LocaleController.stringForMessageListDate(this.draftMessage.date);
        if (this.message != null) {
          break label2960;
        }
        this.drawCheck1 = false;
        this.drawCheck2 = false;
        this.drawClock = false;
        this.drawCount = false;
        this.drawError = false;
        m = (int)Math.ceil(timePaint.measureText((String)localObject2));
        this.timeLayout = new StaticLayout((CharSequence)localObject2, timePaint, m, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (LocaleController.isRTL) {
          break label3210;
        }
        this.timeLeft = (getMeasuredWidth() - AndroidUtilities.dp(15.0F) - m);
        label471:
        if (this.chat == null) {
          break label3223;
        }
        localObject2 = this.chat.title;
        localObject5 = localTextPaint;
      }
    }
    for (;;)
    {
      localObject6 = localObject2;
      if (((String)localObject2).length() == 0) {
        localObject6 = LocaleController.getString("HiddenName", 2131165729);
      }
      label541:
      label566:
      int n;
      if (!LocaleController.isRTL)
      {
        j = getMeasuredWidth() - this.nameLeft - AndroidUtilities.dp(14.0F) - m;
        if (!this.drawNameLock) {
          break label3488;
        }
        i = j - (AndroidUtilities.dp(4.0F) + lockDrawable.getIntrinsicWidth());
        if (!this.drawClock) {
          break label3608;
        }
        n = clockDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0F);
        j = i - n;
        if (LocaleController.isRTL) {
          break label3576;
        }
        this.checkDrawLeft = (this.timeLeft - n);
        label612:
        if ((!this.dialogMuted) || (this.drawVerified)) {
          break label3814;
        }
        m = AndroidUtilities.dp(6.0F) + muteDrawable.getIntrinsicWidth();
        j -= m;
        i = j;
        if (LocaleController.isRTL)
        {
          this.nameLeft += m;
          i = j;
        }
        label673:
        m = Math.max(AndroidUtilities.dp(12.0F), i);
      }
      try
      {
        this.nameLayout = new StaticLayout(TextUtils.ellipsize(((String)localObject6).replace('\n', ' '), (TextPaint)localObject5, m - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), (TextPaint)localObject5, m, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        j = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline + 16);
        if (!LocaleController.isRTL)
        {
          this.messageLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
          if (AndroidUtilities.isTablet())
          {
            f = 13.0F;
            i = AndroidUtilities.dp(f);
            this.avatarImage.setImageCoords(i, this.avatarTop, AndroidUtilities.dp(52.0F), AndroidUtilities.dp(52.0F));
            if (!this.drawError) {
              break label3963;
            }
            n = errorDrawable.getIntrinsicWidth() + AndroidUtilities.dp(8.0F);
            i = j - n;
            if (LocaleController.isRTL) {
              break label3939;
            }
            this.errorLeft = (getMeasuredWidth() - errorDrawable.getIntrinsicWidth() - AndroidUtilities.dp(11.0F));
            localObject2 = localObject1;
            if (k != 0)
            {
              localObject2 = localObject1;
              if (localObject1 == null) {
                localObject2 = "";
              }
              localObject2 = ((CharSequence)localObject2).toString();
              localObject1 = localObject2;
              if (((String)localObject2).length() > 150) {
                localObject1 = ((String)localObject2).substring(0, 150);
              }
              localObject2 = Emoji.replaceEmoji(((String)localObject1).replace('\n', ' '), messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0F), false);
            }
            i = Math.max(AndroidUtilities.dp(12.0F), i);
            localObject1 = TextUtils.ellipsize((CharSequence)localObject2, (TextPaint)localObject4, i - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END);
          }
        }
      }
      catch (Exception localException2)
      {
        try
        {
          for (;;)
          {
            this.messageLayout = new StaticLayout((CharSequence)localObject1, (TextPaint)localObject4, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            if (!LocaleController.isRTL) {
              break label4164;
            }
            if ((this.nameLayout != null) && (this.nameLayout.getLineCount() > 0))
            {
              f = this.nameLayout.getLineLeft(0);
              d = Math.ceil(this.nameLayout.getLineWidth(0));
              if ((!this.dialogMuted) || (this.drawVerified)) {
                break label4122;
              }
              this.nameMuteLeft = ((int)(this.nameLeft + (m - d) - AndroidUtilities.dp(6.0F) - muteDrawable.getIntrinsicWidth()));
              if ((f == 0.0F) && (d < m)) {
                this.nameLeft = ((int)(this.nameLeft + (m - d)));
              }
            }
            if ((this.messageLayout != null) && (this.messageLayout.getLineCount() > 0) && (this.messageLayout.getLineLeft(0) == 0.0F))
            {
              d = Math.ceil(this.messageLayout.getLineWidth(0));
              if (d < i) {
                this.messageLeft = ((int)(this.messageLeft + (i - d)));
              }
            }
            return;
            this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - lockDrawable.getIntrinsicWidth());
            this.nameLeft = AndroidUtilities.dp(14.0F);
            break;
            if (this.chat != null)
            {
              if ((this.chat.id < 0) || ((ChatObject.isChannel(this.chat)) && (!this.chat.megagroup)))
              {
                this.drawNameBroadcast = true;
                this.nameLockTop = AndroidUtilities.dp(16.5F);
                label1284:
                this.drawVerified = this.chat.verified;
                if (LocaleController.isRTL) {
                  break label1379;
                }
                this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
                k = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4);
                if (!this.drawNameGroup) {
                  break label1368;
                }
              }
              label1368:
              for (i = groupDrawable.getIntrinsicWidth();; i = broadcastDrawable.getIntrinsicWidth())
              {
                this.nameLeft = (i + k);
                break;
                this.drawNameGroup = true;
                this.nameLockTop = AndroidUtilities.dp(17.5F);
                break label1284;
              }
              label1379:
              k = getMeasuredWidth();
              m = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              if (this.drawNameGroup) {}
              for (i = groupDrawable.getIntrinsicWidth();; i = broadcastDrawable.getIntrinsicWidth())
              {
                this.nameLockLeft = (k - m - i);
                this.nameLeft = AndroidUtilities.dp(14.0F);
                break;
              }
            }
            if (!LocaleController.isRTL)
            {
              this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              label1462:
              if (this.user == null) {
                break;
              }
              if (this.user.bot)
              {
                this.drawNameBot = true;
                this.nameLockTop = AndroidUtilities.dp(16.5F);
                if (LocaleController.isRTL) {
                  break label1558;
                }
                this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              }
            }
            for (this.nameLeft = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + botDrawable.getIntrinsicWidth());; this.nameLeft = AndroidUtilities.dp(14.0F))
            {
              this.drawVerified = this.user.verified;
              break;
              this.nameLeft = AndroidUtilities.dp(14.0F);
              break label1462;
              label1558:
              this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - botDrawable.getIntrinsicWidth());
            }
            label1594:
            this.draftMessage = null;
            break label328;
            label1602:
            this.lastPrintString = null;
            if (this.draftMessage != null)
            {
              k = 0;
              if (TextUtils.isEmpty(this.draftMessage.message))
              {
                localObject2 = LocaleController.getString("Draft", 2131165591);
                localObject1 = SpannableStringBuilder.valueOf((CharSequence)localObject2);
                ((SpannableStringBuilder)localObject1).setSpan(new ForegroundColorSpan(-2274503), 0, ((String)localObject2).length(), 33);
                localObject4 = localObject5;
                break label356;
              }
              localObject2 = this.draftMessage.message;
              localObject1 = localObject2;
              if (((String)localObject2).length() > 150) {
                localObject1 = ((String)localObject2).substring(0, 150);
              }
              localObject2 = LocaleController.getString("Draft", 2131165591);
              localObject1 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject2, ((String)localObject1).replace('\n', ' ') }));
              ((SpannableStringBuilder)localObject1).setSpan(new ForegroundColorSpan(-2274503), 0, ((String)localObject2).length() + 1, 33);
              localObject1 = Emoji.replaceEmoji((CharSequence)localObject1, messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
              localObject4 = localObject5;
              break label356;
            }
            if (this.message == null)
            {
              k = j;
              localObject4 = localObject5;
              localObject1 = str2;
              if (this.encryptedChat == null) {
                break label356;
              }
              localObject2 = messagePrintingPaint;
              if ((this.encryptedChat instanceof TLRPC.TL_encryptedChatRequested))
              {
                localObject1 = LocaleController.getString("EncryptionProcessing", 2131165616);
                k = j;
                localObject4 = localObject2;
                break label356;
              }
              if ((this.encryptedChat instanceof TLRPC.TL_encryptedChatWaiting))
              {
                if ((this.user != null) && (this.user.first_name != null))
                {
                  localObject1 = LocaleController.formatString("AwaitingEncryption", 2131165355, new Object[] { this.user.first_name });
                  k = j;
                  localObject4 = localObject2;
                  break label356;
                }
                localObject1 = LocaleController.formatString("AwaitingEncryption", 2131165355, new Object[] { "" });
                k = j;
                localObject4 = localObject2;
                break label356;
              }
              if ((this.encryptedChat instanceof TLRPC.TL_encryptedChatDiscarded))
              {
                localObject1 = LocaleController.getString("EncryptionRejected", 2131165617);
                k = j;
                localObject4 = localObject2;
                break label356;
              }
              k = j;
              localObject4 = localObject2;
              localObject1 = str2;
              if (!(this.encryptedChat instanceof TLRPC.TL_encryptedChat)) {
                break label356;
              }
              if (this.encryptedChat.admin_id == UserConfig.getClientUserId())
              {
                if ((this.user != null) && (this.user.first_name != null))
                {
                  localObject1 = LocaleController.formatString("EncryptedChatStartedOutgoing", 2131165605, new Object[] { this.user.first_name });
                  k = j;
                  localObject4 = localObject2;
                  break label356;
                }
                localObject1 = LocaleController.formatString("EncryptedChatStartedOutgoing", 2131165605, new Object[] { "" });
                k = j;
                localObject4 = localObject2;
                break label356;
              }
              localObject1 = LocaleController.getString("EncryptedChatStartedIncoming", 2131165604);
              k = j;
              localObject4 = localObject2;
              break label356;
            }
            localObject2 = null;
            localObject1 = null;
            if (this.message.isFromUser()) {
              localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.message.messageOwner.from_id));
            }
            for (;;)
            {
              if (!(this.message.messageOwner instanceof TLRPC.TL_messageService)) {
                break label2238;
              }
              localObject1 = this.message.messageText;
              localObject4 = messagePrintingPaint;
              k = j;
              break;
              localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.message.messageOwner.to_id.channel_id));
            }
            label2238:
            if ((this.chat != null) && (this.chat.id > 0) && (localObject1 == null))
            {
              if (this.message.isOutOwner())
              {
                localObject1 = LocaleController.getString("FromYou", 2131165711);
                label2281:
                k = 0;
                if (this.message.caption == null) {
                  break label2481;
                }
                localObject4 = this.message.caption.toString();
                localObject2 = localObject4;
                if (((String)localObject4).length() > 150) {
                  localObject2 = ((String)localObject4).substring(0, 150);
                }
                localObject2 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject1, ((String)localObject2).replace('\n', ' ') }));
                localObject4 = localObject5;
              }
              for (;;)
              {
                if (((SpannableStringBuilder)localObject2).length() > 0) {
                  ((SpannableStringBuilder)localObject2).setSpan(new ForegroundColorSpan(-11697229), 0, ((String)localObject1).length() + 1, 33);
                }
                localObject1 = Emoji.replaceEmoji((CharSequence)localObject2, messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
                break;
                if (localObject2 != null)
                {
                  localObject1 = UserObject.getFirstName((TLRPC.User)localObject2).replace("\n", "");
                  break label2281;
                }
                if (localObject1 != null)
                {
                  localObject1 = ((TLRPC.Chat)localObject1).title.replace("\n", "");
                  break label2281;
                }
                localObject1 = "DELETED";
                break label2281;
                label2481:
                if ((this.message.messageOwner.media != null) && (!this.message.isMediaEmpty()))
                {
                  localObject4 = messagePrintingPaint;
                  if ((this.message.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {}
                  for (localObject2 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject1, "🎮 " + this.message.messageOwner.media.game.title }));; localObject2 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject1, this.message.messageText })))
                  {
                    ((SpannableStringBuilder)localObject2).setSpan(new ForegroundColorSpan(-11697229), ((String)localObject1).length() + 2, ((SpannableStringBuilder)localObject2).length(), 33);
                    break;
                  }
                }
                if (this.message.messageOwner.message != null)
                {
                  localObject4 = this.message.messageOwner.message;
                  localObject2 = localObject4;
                  if (((String)localObject4).length() > 150) {
                    localObject2 = ((String)localObject4).substring(0, 150);
                  }
                  localObject2 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject1, ((String)localObject2).replace('\n', ' ') }));
                  localObject4 = localObject5;
                }
                else
                {
                  localObject2 = SpannableStringBuilder.valueOf("");
                  localObject4 = localObject5;
                }
              }
            }
            if (this.message.caption != null)
            {
              localObject1 = this.message.caption;
              k = j;
              localObject4 = localObject5;
              break label356;
            }
            if ((this.message.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {}
            for (localObject2 = "🎮 " + this.message.messageOwner.media.game.title;; localObject2 = this.message.messageText)
            {
              k = j;
              localObject4 = localObject5;
              localObject1 = localObject2;
              if (this.message.messageOwner.media == null) {
                break;
              }
              k = j;
              localObject4 = localObject5;
              localObject1 = localObject2;
              if (this.message.isMediaEmpty()) {
                break;
              }
              localObject4 = messagePrintingPaint;
              k = j;
              localObject1 = localObject2;
              break;
            }
            label2910:
            if (this.lastMessageDate != 0)
            {
              localObject2 = LocaleController.stringForMessageListDate(this.lastMessageDate);
              break label376;
            }
            localObject2 = str1;
            if (this.message == null) {
              break label376;
            }
            localObject2 = LocaleController.stringForMessageListDate(this.message.messageOwner.date);
            break label376;
            label2960:
            if (this.unreadCount != 0)
            {
              this.drawCount = true;
              localObject5 = String.format("%d", new Object[] { Integer.valueOf(this.unreadCount) });
            }
            for (;;)
            {
              if ((this.message.isOut()) && (this.draftMessage == null))
              {
                if (this.message.isSending())
                {
                  this.drawCheck1 = false;
                  this.drawCheck2 = false;
                  this.drawClock = true;
                  this.drawError = false;
                  localObject7 = localObject5;
                  break;
                  this.drawCount = false;
                  localObject5 = localObject8;
                  continue;
                }
                if (this.message.isSendError())
                {
                  this.drawCheck1 = false;
                  this.drawCheck2 = false;
                  this.drawClock = false;
                  this.drawError = true;
                  this.drawCount = false;
                  localObject7 = localObject5;
                  break;
                }
                localObject7 = localObject5;
                if (!this.message.isSent()) {
                  break;
                }
                if ((!this.message.isUnread()) || ((ChatObject.isChannel(this.chat)) && (!this.chat.megagroup))) {}
                for (boolean bool = true;; bool = false)
                {
                  this.drawCheck1 = bool;
                  this.drawCheck2 = true;
                  this.drawClock = false;
                  this.drawError = false;
                  localObject7 = localObject5;
                  break;
                }
              }
            }
            this.drawCheck1 = false;
            this.drawCheck2 = false;
            this.drawClock = false;
            this.drawError = false;
            localObject7 = localObject5;
            break label408;
            label3210:
            this.timeLeft = AndroidUtilities.dp(15.0F);
            break label471;
            label3223:
            if (this.user == null) {
              break label4339;
            }
            if (this.user.id == UserConfig.getClientUserId()) {
              localObject2 = LocaleController.getString("ChatYourSelfName", 2131165503);
            }
            for (;;)
            {
              localObject6 = localObject2;
              if (this.encryptedChat == null) {
                break label4339;
              }
              localObject5 = nameEncryptedPaint;
              break;
              if ((this.user.id / 1000 != 777) && (this.user.id / 1000 != 333) && (ContactsController.getInstance().contactsDict.get(this.user.id) == null))
              {
                if ((ContactsController.getInstance().contactsDict.size() == 0) && ((!ContactsController.getInstance().contactsLoaded) || (ContactsController.getInstance().isLoadingContacts()))) {
                  localObject2 = UserObject.getUserName(this.user);
                } else if ((this.user.phone != null) && (this.user.phone.length() != 0)) {
                  localObject2 = PhoneFormat.getInstance().format("+" + this.user.phone);
                } else {
                  localObject2 = UserObject.getUserName(this.user);
                }
              }
              else {
                localObject2 = UserObject.getUserName(this.user);
              }
            }
            j = getMeasuredWidth() - this.nameLeft - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - m;
            this.nameLeft += m;
            break label541;
            label3488:
            if (this.drawNameGroup)
            {
              i = j - (AndroidUtilities.dp(4.0F) + groupDrawable.getIntrinsicWidth());
              break label566;
            }
            if (this.drawNameBroadcast)
            {
              i = j - (AndroidUtilities.dp(4.0F) + broadcastDrawable.getIntrinsicWidth());
              break label566;
            }
            i = j;
            if (!this.drawNameBot) {
              break label566;
            }
            i = j - (AndroidUtilities.dp(4.0F) + botDrawable.getIntrinsicWidth());
            break label566;
            label3576:
            this.checkDrawLeft = (this.timeLeft + m + AndroidUtilities.dp(5.0F));
            this.nameLeft += n;
            break label612;
            label3608:
            j = i;
            if (!this.drawCheck2) {
              break label612;
            }
            n = checkDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0F);
            j = i - n;
            if (this.drawCheck1)
            {
              j -= halfCheckDrawable.getIntrinsicWidth() - AndroidUtilities.dp(8.0F);
              if (!LocaleController.isRTL)
              {
                this.halfCheckDrawLeft = (this.timeLeft - n);
                this.checkDrawLeft = (this.halfCheckDrawLeft - AndroidUtilities.dp(5.5F));
                break label612;
              }
              this.checkDrawLeft = (this.timeLeft + m + AndroidUtilities.dp(5.0F));
              this.halfCheckDrawLeft = (this.checkDrawLeft + AndroidUtilities.dp(5.5F));
              this.nameLeft += halfCheckDrawable.getIntrinsicWidth() + n - AndroidUtilities.dp(8.0F);
              break label612;
            }
            if (!LocaleController.isRTL)
            {
              this.checkDrawLeft = (this.timeLeft - n);
              break label612;
            }
            this.checkDrawLeft = (this.timeLeft + m + AndroidUtilities.dp(5.0F));
            this.nameLeft += n;
            break label612;
            label3814:
            i = j;
            if (!this.drawVerified) {
              break label673;
            }
            m = AndroidUtilities.dp(6.0F) + verifiedDrawable.getIntrinsicWidth();
            j -= m;
            i = j;
            if (!LocaleController.isRTL) {
              break label673;
            }
            this.nameLeft += m;
            i = j;
            break label673;
            localException2 = localException2;
            FileLog.e("tmessages", localException2);
            continue;
            f = 9.0F;
            continue;
            this.messageLeft = AndroidUtilities.dp(16.0F);
            i = getMeasuredWidth();
            if (AndroidUtilities.isTablet()) {}
            for (f = 65.0F;; f = 61.0F)
            {
              i -= AndroidUtilities.dp(f);
              break;
            }
            label3939:
            this.errorLeft = AndroidUtilities.dp(11.0F);
            this.messageLeft += n;
          }
          label3963:
          if (localObject7 != null)
          {
            this.countWidth = Math.max(AndroidUtilities.dp(12.0F), (int)Math.ceil(countPaint.measureText((String)localObject7)));
            this.countLayout = new StaticLayout((CharSequence)localObject7, countPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
            n = this.countWidth + AndroidUtilities.dp(18.0F);
            i = j - n;
            if (!LocaleController.isRTL) {
              this.countLeft = (getMeasuredWidth() - this.countWidth - AndroidUtilities.dp(19.0F));
            }
            for (;;)
            {
              this.drawCount = true;
              break;
              this.countLeft = AndroidUtilities.dp(19.0F);
              this.messageLeft += n;
            }
          }
          this.drawCount = false;
          i = j;
        }
        catch (Exception localException1)
        {
          double d;
          label4122:
          label4164:
          do
          {
            do
            {
              float f;
              for (;;)
              {
                FileLog.e("tmessages", localException1);
                continue;
                if (this.drawVerified) {
                  this.nameMuteLeft = ((int)(this.nameLeft + (m - d) - AndroidUtilities.dp(6.0F) - verifiedDrawable.getIntrinsicWidth()));
                }
              }
              if ((this.nameLayout != null) && (this.nameLayout.getLineCount() > 0))
              {
                f = this.nameLayout.getLineRight(0);
                if (f == m)
                {
                  d = Math.ceil(this.nameLayout.getLineWidth(0));
                  if (d < m) {
                    this.nameLeft = ((int)(this.nameLeft - (m - d)));
                  }
                }
                if ((this.dialogMuted) || (this.drawVerified)) {
                  this.nameMuteLeft = ((int)(this.nameLeft + f + AndroidUtilities.dp(6.0F)));
                }
              }
            } while ((this.messageLayout == null) || (this.messageLayout.getLineCount() <= 0) || (this.messageLayout.getLineRight(0) != i));
            d = Math.ceil(this.messageLayout.getLineWidth(0));
          } while (d >= i);
          this.messageLeft = ((int)(this.messageLeft - (i - d)));
          return;
        }
      }
      label4339:
      localObject5 = localTextPaint;
      Object localObject3 = localObject6;
    }
  }
  
  public void checkCurrentDialogIndex()
  {
    if (this.index < getDialogsArray().size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)getDialogsArray().get(this.index);
      TLRPC.DraftMessage localDraftMessage = DraftQuery.getDraft(this.currentDialogId);
      MessageObject localMessageObject = (MessageObject)MessagesController.getInstance().dialogMessage.get(Long.valueOf(localTL_dialog.id));
      if ((this.currentDialogId != localTL_dialog.id) || ((this.message != null) && (this.message.getId() != localTL_dialog.top_message)) || ((localMessageObject != null) && (localMessageObject.messageOwner.edit_date != this.currentEditDate)) || (this.unreadCount != localTL_dialog.unread_count) || (this.message != localMessageObject) || ((this.message == null) && (localMessageObject != null)) || (localDraftMessage != this.draftMessage))
      {
        this.currentDialogId = localTL_dialog.id;
        update(0);
      }
    }
  }
  
  public long getDialogId()
  {
    return this.currentDialogId;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.avatarImage.onAttachedToWindow();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.avatarImage.onDetachedFromWindow();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.currentDialogId == 0L) {
      return;
    }
    if (this.isSelected) {
      paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight(), backPaint);
    }
    if (this.drawNameLock)
    {
      setDrawableBounds(lockDrawable, this.nameLockLeft, this.nameLockTop);
      lockDrawable.draw(paramCanvas);
    }
    for (;;)
    {
      if (this.nameLayout != null)
      {
        paramCanvas.save();
        paramCanvas.translate(this.nameLeft, AndroidUtilities.dp(13.0F));
        this.nameLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      paramCanvas.save();
      paramCanvas.translate(this.timeLeft, this.timeTop);
      this.timeLayout.draw(paramCanvas);
      paramCanvas.restore();
      if (this.messageLayout != null)
      {
        paramCanvas.save();
        paramCanvas.translate(this.messageLeft, this.messageTop);
      }
      try
      {
        this.messageLayout.draw(paramCanvas);
        paramCanvas.restore();
        if (this.drawClock)
        {
          setDrawableBounds(clockDrawable, this.checkDrawLeft, this.checkDrawTop);
          clockDrawable.draw(paramCanvas);
          if ((!this.dialogMuted) || (this.drawVerified)) {
            break label520;
          }
          setDrawableBounds(muteDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5F));
          muteDrawable.draw(paramCanvas);
          if (!this.drawError) {
            break label554;
          }
          setDrawableBounds(errorDrawable, this.errorLeft, this.errorTop);
          errorDrawable.draw(paramCanvas);
          if (this.useSeparator)
          {
            if (!LocaleController.isRTL) {
              break label711;
            }
            paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, linePaint);
          }
          this.avatarImage.draw(paramCanvas);
          return;
          if (this.drawNameGroup)
          {
            setDrawableBounds(groupDrawable, this.nameLockLeft, this.nameLockTop);
            groupDrawable.draw(paramCanvas);
            continue;
          }
          if (this.drawNameBroadcast)
          {
            setDrawableBounds(broadcastDrawable, this.nameLockLeft, this.nameLockTop);
            broadcastDrawable.draw(paramCanvas);
            continue;
          }
          if (!this.drawNameBot) {
            continue;
          }
          setDrawableBounds(botDrawable, this.nameLockLeft, this.nameLockTop);
          botDrawable.draw(paramCanvas);
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
          continue;
          if (this.drawCheck2) {
            if (this.drawCheck1)
            {
              setDrawableBounds(halfCheckDrawable, this.halfCheckDrawLeft, this.checkDrawTop);
              halfCheckDrawable.draw(paramCanvas);
              setDrawableBounds(checkDrawable, this.checkDrawLeft, this.checkDrawTop);
              checkDrawable.draw(paramCanvas);
            }
            else
            {
              setDrawableBounds(checkDrawable, this.checkDrawLeft, this.checkDrawTop);
              checkDrawable.draw(paramCanvas);
              continue;
              label520:
              if (this.drawVerified)
              {
                setDrawableBounds(verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5F));
                verifiedDrawable.draw(paramCanvas);
                continue;
                label554:
                if (this.drawCount)
                {
                  if (this.dialogMuted)
                  {
                    setDrawableBounds(countDrawableGrey, this.countLeft - AndroidUtilities.dp(5.5F), this.countTop, this.countWidth + AndroidUtilities.dp(11.0F), countDrawable.getIntrinsicHeight());
                    countDrawableGrey.draw(paramCanvas);
                  }
                  for (;;)
                  {
                    paramCanvas.save();
                    paramCanvas.translate(this.countLeft, this.countTop + AndroidUtilities.dp(4.0F));
                    if (this.countLayout != null) {
                      this.countLayout.draw(paramCanvas);
                    }
                    paramCanvas.restore();
                    break;
                    setDrawableBounds(countDrawable, this.countLeft - AndroidUtilities.dp(5.5F), this.countTop, this.countWidth + AndroidUtilities.dp(11.0F), countDrawable.getIntrinsicHeight());
                    countDrawable.draw(paramCanvas);
                  }
                  label711:
                  paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, linePaint);
                }
              }
            }
          }
        }
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.currentDialogId == 0L) {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    while (!paramBoolean) {
      return;
    }
    buildLayout();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.getSize(paramInt1);
    int i = AndroidUtilities.dp(72.0F);
    if (this.useSeparator) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, paramInt1 + i);
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2))) {
      getBackground().setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setDialog(long paramLong, MessageObject paramMessageObject, int paramInt)
  {
    this.currentDialogId = paramLong;
    this.message = paramMessageObject;
    this.isDialogCell = false;
    this.lastMessageDate = paramInt;
    if (paramMessageObject != null)
    {
      paramInt = paramMessageObject.messageOwner.edit_date;
      this.currentEditDate = paramInt;
      this.unreadCount = 0;
      if ((paramMessageObject == null) || (!paramMessageObject.isUnread())) {
        break label98;
      }
    }
    label98:
    for (boolean bool = true;; bool = false)
    {
      this.lastUnreadState = bool;
      if (this.message != null) {
        this.lastSendState = this.message.messageOwner.send_state;
      }
      update(0);
      return;
      paramInt = 0;
      break;
    }
  }
  
  public void setDialog(TLRPC.TL_dialog paramTL_dialog, int paramInt1, int paramInt2)
  {
    this.currentDialogId = paramTL_dialog.id;
    this.isDialogCell = true;
    this.index = paramInt1;
    this.dialogsType = paramInt2;
    update(0);
  }
  
  public void setDialogSelected(boolean paramBoolean)
  {
    if (this.isSelected != paramBoolean) {
      invalidate();
    }
    this.isSelected = paramBoolean;
  }
  
  public void update(int paramInt)
  {
    Object localObject1;
    boolean bool;
    int i;
    if (this.isDialogCell)
    {
      localObject1 = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.currentDialogId));
      if ((localObject1 != null) && (paramInt == 0))
      {
        this.message = ((MessageObject)MessagesController.getInstance().dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject1).id)));
        if ((this.message == null) || (!this.message.isUnread())) {
          break label474;
        }
        bool = true;
        this.lastUnreadState = bool;
        this.unreadCount = ((TLRPC.TL_dialog)localObject1).unread_count;
        if (this.message == null) {
          break label480;
        }
        i = this.message.messageOwner.edit_date;
        label114:
        this.currentEditDate = i;
        this.lastMessageDate = ((TLRPC.TL_dialog)localObject1).last_message_date;
        if (this.message != null) {
          this.lastSendState = this.message.messageOwner.send_state;
        }
      }
    }
    if (paramInt != 0)
    {
      int j = 0;
      i = j;
      if (this.isDialogCell)
      {
        i = j;
        if ((paramInt & 0x40) != 0)
        {
          localObject1 = (CharSequence)MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentDialogId));
          if (((this.lastPrintString == null) || (localObject1 != null)) && ((this.lastPrintString != null) || (localObject1 == null)))
          {
            i = j;
            if (this.lastPrintString != null)
            {
              i = j;
              if (localObject1 != null)
              {
                i = j;
                if (this.lastPrintString.equals(localObject1)) {}
              }
            }
          }
          else
          {
            i = 1;
          }
        }
      }
      j = i;
      if (i == 0)
      {
        j = i;
        if ((paramInt & 0x2) != 0)
        {
          j = i;
          if (this.chat == null) {
            j = 1;
          }
        }
      }
      i = j;
      if (j == 0)
      {
        i = j;
        if ((paramInt & 0x1) != 0)
        {
          i = j;
          if (this.chat == null) {
            i = 1;
          }
        }
      }
      j = i;
      if (i == 0)
      {
        j = i;
        if ((paramInt & 0x8) != 0)
        {
          j = i;
          if (this.user == null) {
            j = 1;
          }
        }
      }
      int k = j;
      if (j == 0)
      {
        k = j;
        if ((paramInt & 0x10) != 0)
        {
          k = j;
          if (this.user == null) {
            k = 1;
          }
        }
      }
      i = k;
      if (k == 0)
      {
        i = k;
        if ((paramInt & 0x100) != 0)
        {
          if ((this.message == null) || (this.lastUnreadState == this.message.isUnread())) {
            break label485;
          }
          this.lastUnreadState = this.message.isUnread();
          i = 1;
        }
      }
      for (;;)
      {
        j = i;
        if (i == 0)
        {
          j = i;
          if ((paramInt & 0x1000) != 0)
          {
            j = i;
            if (this.message != null)
            {
              j = i;
              if (this.lastSendState != this.message.messageOwner.send_state)
              {
                this.lastSendState = this.message.messageOwner.send_state;
                j = 1;
              }
            }
          }
        }
        if (j != 0) {
          break label553;
        }
        return;
        label474:
        bool = false;
        break;
        label480:
        i = 0;
        break label114;
        label485:
        i = k;
        if (this.isDialogCell)
        {
          localObject1 = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.currentDialogId));
          i = k;
          if (localObject1 != null)
          {
            i = k;
            if (this.unreadCount != ((TLRPC.TL_dialog)localObject1).unread_count)
            {
              this.unreadCount = ((TLRPC.TL_dialog)localObject1).unread_count;
              i = 1;
            }
          }
        }
      }
    }
    label553:
    label635:
    Object localObject3;
    Object localObject2;
    if ((this.isDialogCell) && (MessagesController.getInstance().isDialogMuted(this.currentDialogId)))
    {
      bool = true;
      this.dialogMuted = bool;
      this.user = null;
      this.chat = null;
      this.encryptedChat = null;
      paramInt = (int)this.currentDialogId;
      i = (int)(this.currentDialogId >> 32);
      if (paramInt == 0) {
        break label826;
      }
      if (i != 1) {
        break label731;
      }
      this.chat = MessagesController.getInstance().getChat(Integer.valueOf(paramInt));
      localObject3 = null;
      localObject2 = null;
      localObject1 = null;
      if (this.user == null) {
        break label870;
      }
      if (this.user.photo != null) {
        localObject1 = this.user.photo.photo_small;
      }
      this.avatarDrawable.setInfo(this.user);
      label684:
      this.avatarImage.setImage((TLObject)localObject1, "50_50", this.avatarDrawable, null, false);
      if ((getMeasuredWidth() == 0) && (getMeasuredHeight() == 0)) {
        break label921;
      }
      buildLayout();
    }
    for (;;)
    {
      invalidate();
      return;
      bool = false;
      break;
      label731:
      if (paramInt < 0)
      {
        this.chat = MessagesController.getInstance().getChat(Integer.valueOf(-paramInt));
        if ((this.isDialogCell) || (this.chat == null) || (this.chat.migrated_to == null)) {
          break label635;
        }
        localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.chat.migrated_to.channel_id));
        if (localObject1 == null) {
          break label635;
        }
        this.chat = ((TLRPC.Chat)localObject1);
        break label635;
      }
      this.user = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
      break label635;
      label826:
      this.encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i));
      if (this.encryptedChat == null) {
        break label635;
      }
      this.user = MessagesController.getInstance().getUser(Integer.valueOf(this.encryptedChat.user_id));
      break label635;
      label870:
      localObject1 = localObject3;
      if (this.chat == null) {
        break label684;
      }
      localObject1 = localObject2;
      if (this.chat.photo != null) {
        localObject1 = this.chat.photo.photo_small;
      }
      this.avatarDrawable.setInfo(this.chat);
      break label684;
      label921:
      requestLayout();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/DialogCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */