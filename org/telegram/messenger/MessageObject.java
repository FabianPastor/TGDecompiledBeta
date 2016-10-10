package org.telegram.messenger;

import android.graphics.Point;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.util.Linkify;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
import org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionTTLChange;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageForwarded_old2;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;

public class MessageObject
{
  private static final int LINES_PER_BLOCK = 10;
  public static final int MESSAGE_SEND_STATE_SENDING = 1;
  public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
  public static final int MESSAGE_SEND_STATE_SENT = 0;
  private static TextPaint botButtonPaint;
  private static TextPaint gameTextPaint;
  private static TextPaint textPaint;
  public static Pattern urlPattern;
  public boolean attachPathExists;
  public float audioProgress;
  public int audioProgressSec;
  public CharSequence caption;
  public int contentType;
  public String dateKey;
  public boolean deleted;
  public boolean forceUpdate;
  private int generatedWithMinSize;
  public int lastLineWidth;
  private boolean layoutCreated;
  public CharSequence linkDescription;
  public boolean mediaExists;
  public TLRPC.Message messageOwner;
  public CharSequence messageText;
  public String monthKey;
  public ArrayList<TLRPC.PhotoSize> photoThumbs;
  public ArrayList<TLRPC.PhotoSize> photoThumbs2;
  public MessageObject replyMessageObject;
  public boolean resendAsIs;
  public int textHeight;
  public ArrayList<TextLayoutBlock> textLayoutBlocks;
  public int textWidth;
  public int type = 1000;
  public VideoEditedInfo videoEditedInfo;
  public boolean viewsReloaded;
  public int wantedBotKeyboardWidth;
  
  public MessageObject(TLRPC.Message paramMessage, AbstractMap<Integer, TLRPC.User> paramAbstractMap, AbstractMap<Integer, TLRPC.Chat> paramAbstractMap1, boolean paramBoolean)
  {
    if (textPaint == null)
    {
      textPaint = new TextPaint(1);
      textPaint.setColor(-16777216);
      textPaint.linkColor = -14255946;
    }
    if (gameTextPaint == null)
    {
      gameTextPaint = new TextPaint(1);
      gameTextPaint.setColor(-16777216);
      gameTextPaint.linkColor = -14255946;
    }
    textPaint.setTextSize(AndroidUtilities.dp(MessagesController.getInstance().fontSize));
    gameTextPaint.setTextSize(AndroidUtilities.dp(14.0F));
    this.messageOwner = paramMessage;
    if (paramMessage.replyMessage != null) {
      this.replyMessageObject = new MessageObject(paramMessage.replyMessage, paramAbstractMap, paramAbstractMap1, false);
    }
    Object localObject = null;
    TLRPC.User localUser = null;
    if (paramMessage.from_id > 0)
    {
      if (paramAbstractMap != null) {
        localUser = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(paramMessage.from_id));
      }
      localObject = localUser;
      if (localUser == null) {
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessage.from_id));
      }
    }
    int i;
    int j;
    if ((paramMessage instanceof TLRPC.TL_messageService)) {
      if (paramMessage.action != null)
      {
        if (!(paramMessage.action instanceof TLRPC.TL_messageActionChatCreate)) {
          break label544;
        }
        if (isOut()) {
          this.messageText = LocaleController.getString("ActionYouCreateGroup", 2131165248);
        }
      }
      else
      {
        if (this.messageText == null) {
          this.messageText = "";
        }
        setType();
        measureInlineBotButtons();
        paramMessage = new GregorianCalendar();
        paramMessage.setTimeInMillis(this.messageOwner.date * 1000L);
        i = paramMessage.get(6);
        j = paramMessage.get(1);
        int k = paramMessage.get(2);
        this.dateKey = String.format("%d_%02d_%02d", new Object[] { Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(i) });
        this.monthKey = String.format("%d_%02d", new Object[] { Integer.valueOf(j), Integer.valueOf(k) });
        if ((this.messageOwner.message != null) && (this.messageOwner.id < 0) && (this.messageOwner.message.length() > 6) && ((isVideo()) || (isNewGif())))
        {
          this.videoEditedInfo = new VideoEditedInfo();
          if (!this.videoEditedInfo.parseString(this.messageOwner.message)) {
            this.videoEditedInfo = null;
          }
        }
        generateCaption();
        if (paramBoolean) {
          if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {
            break label2970;
          }
        }
      }
    }
    label544:
    label1907:
    label2059:
    label2970:
    for (paramMessage = gameTextPaint;; paramMessage = textPaint)
    {
      this.messageText = Emoji.replaceEmoji(this.messageText, paramMessage.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      generateLayout((TLRPC.User)localObject);
      this.layoutCreated = paramBoolean;
      generateThumbs(false);
      checkMediaExistance();
      return;
      this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", 2131165219), "un1", (TLObject)localObject);
      break;
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatDeleteUser))
      {
        if (paramMessage.action.user_id == paramMessage.from_id)
        {
          if (isOut())
          {
            this.messageText = LocaleController.getString("ActionYouLeftUser", 2131165250);
            break;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", 2131165225), "un1", (TLObject)localObject);
          break;
        }
        paramAbstractMap1 = null;
        if (paramAbstractMap != null) {
          paramAbstractMap1 = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(paramMessage.action.user_id));
        }
        paramAbstractMap = paramAbstractMap1;
        if (paramAbstractMap1 == null) {
          paramAbstractMap = MessagesController.getInstance().getUser(Integer.valueOf(paramMessage.action.user_id));
        }
        if (isOut())
        {
          this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", 2131165249), "un2", paramAbstractMap);
          break;
        }
        if (paramMessage.action.user_id == UserConfig.getClientUserId())
        {
          this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", 2131165224), "un1", (TLObject)localObject);
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", 2131165223), "un2", paramAbstractMap);
        this.messageText = replaceWithLink(this.messageText, "un1", (TLObject)localObject);
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatAddUser))
      {
        j = this.messageOwner.action.user_id;
        i = j;
        if (j == 0)
        {
          i = j;
          if (this.messageOwner.action.users.size() == 1) {
            i = ((Integer)this.messageOwner.action.users.get(0)).intValue();
          }
        }
        if (i != 0)
        {
          paramAbstractMap1 = null;
          if (paramAbstractMap != null) {
            paramAbstractMap1 = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(i));
          }
          paramAbstractMap = paramAbstractMap1;
          if (paramAbstractMap1 == null) {
            paramAbstractMap = MessagesController.getInstance().getUser(Integer.valueOf(i));
          }
          if (i == paramMessage.from_id)
          {
            if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
            {
              this.messageText = LocaleController.getString("ChannelJoined", 2131165431);
              break;
            }
            if ((paramMessage.to_id.channel_id != 0) && (isMegagroup()))
            {
              if (i == UserConfig.getClientUserId())
              {
                this.messageText = LocaleController.getString("ChannelMegaJoined", 2131165435);
                break;
              }
              this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", 2131165210), "un1", (TLObject)localObject);
              break;
            }
            if (isOut())
            {
              this.messageText = LocaleController.getString("ActionAddUserSelfYou", 2131165211);
              break;
            }
            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", 2131165209), "un1", (TLObject)localObject);
            break;
          }
          if (isOut())
          {
            this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", 2131165245), "un2", paramAbstractMap);
            break;
          }
          if (i == UserConfig.getClientUserId())
          {
            if (paramMessage.to_id.channel_id != 0)
            {
              if (isMegagroup())
              {
                this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", 2131165852), "un1", (TLObject)localObject);
                break;
              }
              this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", 2131165406), "un1", (TLObject)localObject);
              break;
            }
            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", 2131165212), "un1", (TLObject)localObject);
            break;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", 2131165208), "un2", paramAbstractMap);
          this.messageText = replaceWithLink(this.messageText, "un1", (TLObject)localObject);
          break;
        }
        if (isOut())
        {
          this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", 2131165245), "un2", paramMessage.action.users, paramAbstractMap);
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", 2131165208), "un2", paramMessage.action.users, paramAbstractMap);
        this.messageText = replaceWithLink(this.messageText, "un1", (TLObject)localObject);
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatJoinedByLink))
      {
        if (isOut())
        {
          this.messageText = LocaleController.getString("ActionInviteYou", 2131165222);
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", 2131165221), "un1", (TLObject)localObject);
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatEditPhoto))
      {
        if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
        {
          this.messageText = LocaleController.getString("ActionChannelChangedPhoto", 2131165215);
          break;
        }
        if (isOut())
        {
          this.messageText = LocaleController.getString("ActionYouChangedPhoto", 2131165246);
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", 2131165213), "un1", (TLObject)localObject);
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatEditTitle))
      {
        if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
        {
          this.messageText = LocaleController.getString("ActionChannelChangedTitle", 2131165216).replace("un2", paramMessage.action.title);
          break;
        }
        if (isOut())
        {
          this.messageText = LocaleController.getString("ActionYouChangedTitle", 2131165247).replace("un2", paramMessage.action.title);
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", 2131165214).replace("un2", paramMessage.action.title), "un1", (TLObject)localObject);
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatDeletePhoto))
      {
        if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
        {
          this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", 2131165217);
          break;
        }
        if (isOut())
        {
          this.messageText = LocaleController.getString("ActionYouRemovedPhoto", 2131165251);
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", 2131165240), "un1", (TLObject)localObject);
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionTTLChange))
      {
        if (paramMessage.action.ttl != 0)
        {
          if (isOut())
          {
            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", 2131165874, new Object[] { AndroidUtilities.formatTTLString(paramMessage.action.ttl) });
            break;
          }
          this.messageText = LocaleController.formatString("MessageLifetimeChanged", 2131165873, new Object[] { UserObject.getFirstName((TLRPC.User)localObject), AndroidUtilities.formatTTLString(paramMessage.action.ttl) });
          break;
        }
        if (isOut())
        {
          this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", 2131165876);
          break;
        }
        this.messageText = LocaleController.formatString("MessageLifetimeRemoved", 2131165875, new Object[] { UserObject.getFirstName((TLRPC.User)localObject) });
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
      {
        long l = paramMessage.date * 1000L;
        String str;
        if ((LocaleController.getInstance().formatterDay != null) && (LocaleController.getInstance().formatterYear != null))
        {
          str = LocaleController.formatString("formatDateAtTime", 2131166435, new Object[] { LocaleController.getInstance().formatterYear.format(l), LocaleController.getInstance().formatterDay.format(l) });
          localUser = UserConfig.getCurrentUser();
          paramAbstractMap1 = localUser;
          if (localUser == null)
          {
            if (paramAbstractMap != null) {
              localUser = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(this.messageOwner.to_id.user_id));
            }
            paramAbstractMap1 = localUser;
            if (localUser == null) {
              paramAbstractMap1 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
            }
          }
          if (paramAbstractMap1 == null) {
            break label2059;
          }
        }
        for (paramAbstractMap = UserObject.getFirstName(paramAbstractMap1);; paramAbstractMap = "")
        {
          this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", 2131166029, new Object[] { paramAbstractMap, str, paramMessage.action.title, paramMessage.action.address });
          break;
          str = "" + paramMessage.date;
          break label1907;
        }
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionUserJoined))
      {
        this.messageText = LocaleController.formatString("NotificationContactJoined", 2131165990, new Object[] { UserObject.getUserName((TLRPC.User)localObject) });
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto))
      {
        this.messageText = LocaleController.formatString("NotificationContactNewPhoto", 2131165991, new Object[] { UserObject.getUserName((TLRPC.User)localObject) });
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageEncryptedAction))
      {
        if ((paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages))
        {
          if (isOut())
          {
            this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", 2131165242, new Object[0]);
            break;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", 2131165241), "un1", (TLObject)localObject);
          break;
        }
        if (!(paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
          break;
        }
        paramMessage = (TLRPC.TL_decryptedMessageActionSetMessageTTL)paramMessage.action.encryptedAction;
        if (paramMessage.ttl_seconds != 0)
        {
          if (isOut())
          {
            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", 2131165874, new Object[] { AndroidUtilities.formatTTLString(paramMessage.ttl_seconds) });
            break;
          }
          this.messageText = LocaleController.formatString("MessageLifetimeChanged", 2131165873, new Object[] { UserObject.getFirstName((TLRPC.User)localObject), AndroidUtilities.formatTTLString(paramMessage.ttl_seconds) });
          break;
        }
        if (isOut())
        {
          this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", 2131165876);
          break;
        }
        this.messageText = LocaleController.formatString("MessageLifetimeRemoved", 2131165875, new Object[] { UserObject.getFirstName((TLRPC.User)localObject) });
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionCreatedBroadcastList))
      {
        this.messageText = LocaleController.formatString("YouCreatedBroadcastList", 2131166416, new Object[0]);
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChannelCreate))
      {
        if (isMegagroup())
        {
          this.messageText = LocaleController.getString("ActionCreateMega", 2131165220);
          break;
        }
        this.messageText = LocaleController.getString("ActionCreateChannel", 2131165218);
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatMigrateTo))
      {
        this.messageText = LocaleController.getString("ActionMigrateFromGroup", 2131165226);
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChannelMigrateFrom))
      {
        this.messageText = LocaleController.getString("ActionMigrateFromGroup", 2131165226);
        break;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionPinMessage))
      {
        if (localObject == null) {}
        for (paramMessage = (TLRPC.Chat)paramAbstractMap1.get(Integer.valueOf(paramMessage.to_id.channel_id));; paramMessage = null)
        {
          generatePinMessageText((TLRPC.User)localObject, paramMessage);
          break;
        }
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionHistoryClear))
      {
        this.messageText = LocaleController.getString("HistoryCleared", 2131165731);
        break;
      }
      if (!(paramMessage.action instanceof TLRPC.TL_messageActionGameScore)) {
        break;
      }
      generateGameMessageText((TLRPC.User)localObject);
      break;
      if (!isMediaEmpty())
      {
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
        {
          this.messageText = LocaleController.getString("AttachPhoto", 2131165343);
          break;
        }
        if (isVideo())
        {
          this.messageText = LocaleController.getString("AttachVideo", 2131165345);
          break;
        }
        if (isVoice())
        {
          this.messageText = LocaleController.getString("AttachAudio", 2131165335);
          break;
        }
        if (((paramMessage.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessage.media instanceof TLRPC.TL_messageMediaVenue)))
        {
          this.messageText = LocaleController.getString("AttachLocation", 2131165341);
          break;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaContact))
        {
          this.messageText = LocaleController.getString("AttachContact", 2131165337);
          break;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaGame))
        {
          this.messageText = paramMessage.message;
          break;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaUnsupported))
        {
          this.messageText = LocaleController.getString("UnsupportedMedia", 2131166359);
          break;
        }
        if (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) {
          break;
        }
        if (isSticker())
        {
          paramMessage = getStrickerChar();
          if ((paramMessage != null) && (paramMessage.length() > 0))
          {
            this.messageText = String.format("%s %s", new Object[] { paramMessage, LocaleController.getString("AttachSticker", 2131165344) });
            break;
          }
          this.messageText = LocaleController.getString("AttachSticker", 2131165344);
          break;
        }
        if (isMusic())
        {
          this.messageText = LocaleController.getString("AttachMusic", 2131165342);
          break;
        }
        if (isGif())
        {
          this.messageText = LocaleController.getString("AttachGif", 2131165340);
          break;
        }
        paramMessage = FileLoader.getDocumentFileName(paramMessage.media.document);
        if ((paramMessage != null) && (paramMessage.length() > 0))
        {
          this.messageText = paramMessage;
          break;
        }
        this.messageText = LocaleController.getString("AttachDocument", 2131165338);
        break;
      }
      this.messageText = paramMessage.message;
      break;
    }
  }
  
  public MessageObject(TLRPC.Message paramMessage, AbstractMap<Integer, TLRPC.User> paramAbstractMap, boolean paramBoolean)
  {
    this(paramMessage, paramAbstractMap, null, paramBoolean);
  }
  
  public static void addLinks(CharSequence paramCharSequence)
  {
    addLinks(paramCharSequence, true);
  }
  
  public static void addLinks(CharSequence paramCharSequence, boolean paramBoolean)
  {
    if ((!(paramCharSequence instanceof Spannable)) || (!containsUrls(paramCharSequence)) || (paramCharSequence.length() < 200)) {}
    for (;;)
    {
      try
      {
        Linkify.addLinks((Spannable)paramCharSequence, 5);
        addUsernamesAndHashtags(paramCharSequence, paramBoolean);
        return;
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        continue;
      }
      try
      {
        Linkify.addLinks((Spannable)paramCharSequence, 1);
      }
      catch (Exception localException2)
      {
        FileLog.e("tmessages", localException2);
      }
    }
  }
  
  private static void addUsernamesAndHashtags(CharSequence paramCharSequence, boolean paramBoolean)
  {
    for (;;)
    {
      int k;
      int i;
      try
      {
        if (urlPattern == null) {
          urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s)@[a-zA-Z\\d_]{1,32}|(^|\\s)#[\\w\\.]+");
        }
        Matcher localMatcher = urlPattern.matcher(paramCharSequence);
        if (localMatcher.find())
        {
          int j = localMatcher.start();
          k = localMatcher.end();
          i = j;
          if (paramCharSequence.charAt(j) != '@')
          {
            i = j;
            if (paramCharSequence.charAt(j) != '#')
            {
              i = j;
              if (paramCharSequence.charAt(j) != '/') {
                i = j + 1;
              }
            }
          }
          localObject = null;
          if (paramCharSequence.charAt(i) == '/')
          {
            if (paramBoolean) {
              localObject = new URLSpanBotCommand(paramCharSequence.subSequence(i, k).toString());
            }
            if (localObject == null) {
              continue;
            }
            ((Spannable)paramCharSequence).setSpan(localObject, i, k, 0);
          }
        }
        else
        {
          return;
        }
      }
      catch (Exception paramCharSequence)
      {
        FileLog.e("tmessages", paramCharSequence);
      }
      Object localObject = new URLSpanNoUnderline(paramCharSequence.subSequence(i, k).toString());
    }
  }
  
  public static boolean canDeleteMessage(TLRPC.Message paramMessage, TLRPC.Chat paramChat)
  {
    boolean bool = false;
    if (paramMessage.id < 0) {}
    TLRPC.Chat localChat;
    do
    {
      do
      {
        return true;
        localChat = paramChat;
        if (paramChat == null)
        {
          localChat = paramChat;
          if (paramMessage.to_id.channel_id != 0) {
            localChat = MessagesController.getInstance().getChat(Integer.valueOf(paramMessage.to_id.channel_id));
          }
        }
        if (!ChatObject.isChannel(localChat)) {
          break;
        }
        if (paramMessage.id == 1) {
          return false;
        }
      } while (localChat.creator);
      if (!localChat.editor) {
        break label116;
      }
    } while ((isOut(paramMessage)) || ((paramMessage.from_id > 0) && (!paramMessage.post)));
    label116:
    do
    {
      do
      {
        if ((isOut(paramMessage)) || (!ChatObject.isChannel(localChat))) {
          bool = true;
        }
        return bool;
        if (!localChat.moderator) {
          break;
        }
      } while ((paramMessage.from_id <= 0) || (paramMessage.post));
      return true;
    } while ((!isOut(paramMessage)) || (paramMessage.from_id <= 0));
    return true;
  }
  
  public static boolean canEditMessage(TLRPC.Message paramMessage, TLRPC.Chat paramChat)
  {
    boolean bool2 = true;
    if ((paramMessage == null) || (paramMessage.to_id == null) || ((paramMessage.action != null) && (!(paramMessage.action instanceof TLRPC.TL_messageActionEmpty))) || (isForwardedMessage(paramMessage)) || (paramMessage.via_bot_id != 0) || (paramMessage.id < 0)) {}
    label195:
    TLRPC.Chat localChat;
    do
    {
      do
      {
        do
        {
          return false;
          if ((paramMessage.from_id == paramMessage.to_id.user_id) && (paramMessage.from_id == UserConfig.getClientUserId())) {
            return true;
          }
        } while (Math.abs(paramMessage.date - ConnectionsManager.getInstance().getCurrentTime()) > MessagesController.getInstance().maxEditTime);
        if (paramMessage.to_id.channel_id == 0)
        {
          if ((paramMessage.out) || (paramMessage.from_id == UserConfig.getClientUserId()))
          {
            bool1 = bool2;
            if (!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) {
              if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
              {
                bool1 = bool2;
                if (!isStickerMessage(paramMessage)) {}
              }
              else
              {
                bool1 = bool2;
                if (!(paramMessage.media instanceof TLRPC.TL_messageMediaEmpty))
                {
                  bool1 = bool2;
                  if (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
                    if (paramMessage.media != null) {
                      break label195;
                    }
                  }
                }
              }
            }
          }
          for (boolean bool1 = bool2;; bool1 = false) {
            return bool1;
          }
        }
        localChat = paramChat;
        if (paramChat != null) {
          break;
        }
        localChat = paramChat;
        if (paramMessage.to_id.channel_id == 0) {
          break;
        }
        localChat = MessagesController.getInstance().getChat(Integer.valueOf(paramMessage.to_id.channel_id));
      } while (localChat == null);
    } while (((!localChat.megagroup) || (!paramMessage.out)) && ((localChat.megagroup) || ((!localChat.creator) && ((!localChat.editor) || (!isOut(paramMessage)))) || (!paramMessage.post) || ((!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && ((!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) || (isStickerMessage(paramMessage))) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) && (paramMessage.media != null))));
    return true;
  }
  
  private static boolean containsUrls(CharSequence paramCharSequence)
  {
    boolean bool2 = true;
    boolean bool1;
    if ((paramCharSequence == null) || (paramCharSequence.length() < 2) || (paramCharSequence.length() > 20480)) {
      bool1 = false;
    }
    int i3;
    int n;
    int m;
    int i2;
    int i1;
    label58:
    int i4;
    int k;
    int i;
    int j;
    label108:
    do
    {
      do
      {
        do
        {
          do
          {
            return bool1;
            int i5 = paramCharSequence.length();
            i3 = 0;
            n = 0;
            m = 0;
            i2 = 0;
            i1 = 0;
            if (i1 >= i5) {
              break label344;
            }
            i4 = paramCharSequence.charAt(i1);
            if ((i4 < 48) || (i4 > 57)) {
              break;
            }
            k = i3 + 1;
            bool1 = bool2;
          } while (k >= 6);
          i = 0;
          j = 0;
          if ((i4 != 64) && (i4 != 35) && (i4 != 47)) {
            break;
          }
          bool1 = bool2;
        } while (i1 == 0);
        if (i1 == 0) {
          break;
        }
        bool1 = bool2;
      } while (paramCharSequence.charAt(i1 - 1) == ' ');
      bool1 = bool2;
    } while (paramCharSequence.charAt(i1 - 1) == '\n');
    if (i4 == 58) {
      if (i == 0) {
        i = 1;
      }
    }
    for (;;)
    {
      i2 = i4;
      i1 += 1;
      i3 = k;
      m = j;
      n = i;
      break label58;
      if (i4 != 32)
      {
        k = i3;
        j = m;
        i = n;
        if (i3 > 0) {
          break label108;
        }
      }
      k = 0;
      j = m;
      i = n;
      break label108;
      i = 0;
      continue;
      if (i4 == 47)
      {
        bool1 = bool2;
        if (i == 2) {
          break;
        }
        if (i == 1)
        {
          i += 1;
          continue;
        }
        i = 0;
        continue;
      }
      if (i4 == 46)
      {
        if ((j == 0) && (i2 != 32)) {
          j += 1;
        } else {
          j = 0;
        }
      }
      else
      {
        if ((i4 != 32) && (i2 == 46))
        {
          bool1 = bool2;
          if (j == 1) {
            break;
          }
        }
        j = 0;
      }
    }
    label344:
    return false;
  }
  
  public static long getDialogId(TLRPC.Message paramMessage)
  {
    if ((paramMessage.dialog_id == 0L) && (paramMessage.to_id != null))
    {
      if (paramMessage.to_id.chat_id == 0) {
        break label71;
      }
      if (paramMessage.to_id.chat_id >= 0) {
        break label55;
      }
      paramMessage.dialog_id = AndroidUtilities.makeBroadcastId(paramMessage.to_id.chat_id);
    }
    for (;;)
    {
      return paramMessage.dialog_id;
      label55:
      paramMessage.dialog_id = (-paramMessage.to_id.chat_id);
      continue;
      label71:
      if (paramMessage.to_id.channel_id != 0) {
        paramMessage.dialog_id = (-paramMessage.to_id.channel_id);
      } else if (isOut(paramMessage)) {
        paramMessage.dialog_id = paramMessage.to_id.user_id;
      } else {
        paramMessage.dialog_id = paramMessage.from_id;
      }
    }
  }
  
  public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Message paramMessage)
  {
    TLRPC.DocumentAttribute localDocumentAttribute;
    if ((paramMessage.media != null) && (paramMessage.media.document != null))
    {
      paramMessage = paramMessage.media.document.attributes.iterator();
      while (paramMessage.hasNext())
      {
        localDocumentAttribute = (TLRPC.DocumentAttribute)paramMessage.next();
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
          if (!(localDocumentAttribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty)) {
            break label69;
          }
        }
      }
    }
    return null;
    label69:
    return localDocumentAttribute.stickerset;
  }
  
  public static TextPaint getTextPaint()
  {
    if (textPaint == null)
    {
      textPaint = new TextPaint(1);
      textPaint.setColor(-16777216);
      textPaint.linkColor = -14255946;
    }
    if (gameTextPaint == null)
    {
      gameTextPaint = new TextPaint(1);
      gameTextPaint.setColor(-16777216);
      gameTextPaint.linkColor = -14255946;
    }
    textPaint.setTextSize(AndroidUtilities.dp(MessagesController.getInstance().fontSize));
    gameTextPaint.setTextSize(AndroidUtilities.dp(14.0F));
    return textPaint;
  }
  
  public static int getUnreadFlags(TLRPC.Message paramMessage)
  {
    int i = 0;
    if (!paramMessage.unread) {
      i = 0x0 | 0x1;
    }
    int j = i;
    if (!paramMessage.media_unread) {
      j = i | 0x2;
    }
    return j;
  }
  
  public static boolean isContentUnread(TLRPC.Message paramMessage)
  {
    return paramMessage.media_unread;
  }
  
  public static boolean isForwardedMessage(TLRPC.Message paramMessage)
  {
    return (paramMessage.flags & 0x4) != 0;
  }
  
  public static boolean isGameMessage(TLRPC.Message paramMessage)
  {
    return paramMessage.media instanceof TLRPC.TL_messageMediaGame;
  }
  
  public static boolean isGifDocument(TLRPC.Document paramDocument)
  {
    return (paramDocument != null) && (paramDocument.thumb != null) && (paramDocument.mime_type != null) && ((paramDocument.mime_type.equals("image/gif")) || (isNewGifDocument(paramDocument)));
  }
  
  public static boolean isMaskDocument(TLRPC.Document paramDocument)
  {
    if (paramDocument != null)
    {
      int i = 0;
      while (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if (((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) && (localDocumentAttribute.mask)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public static boolean isMaskMessage(TLRPC.Message paramMessage)
  {
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isMaskDocument(paramMessage.media.document));
  }
  
  public static boolean isMediaEmpty(TLRPC.Message paramMessage)
  {
    return (paramMessage == null) || (paramMessage.media == null) || ((paramMessage.media instanceof TLRPC.TL_messageMediaEmpty)) || ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage));
  }
  
  public static boolean isMegagroup(TLRPC.Message paramMessage)
  {
    return (paramMessage.flags & 0x80000000) != 0;
  }
  
  public static boolean isMusicDocument(TLRPC.Document paramDocument)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    int i;
    if (paramDocument != null) {
      i = 0;
    }
    for (;;)
    {
      bool1 = bool2;
      if (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio)) {
          break label58;
        }
        bool1 = bool2;
        if (!localDocumentAttribute.voice) {
          bool1 = true;
        }
      }
      return bool1;
      label58:
      i += 1;
    }
  }
  
  public static boolean isMusicMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      return isMusicDocument(paramMessage.media.webpage.document);
    }
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isMusicDocument(paramMessage.media.document));
  }
  
  public static boolean isNewGifDocument(TLRPC.Document paramDocument)
  {
    if ((paramDocument != null) && (paramDocument.mime_type != null) && (paramDocument.mime_type.equals("video/mp4")))
    {
      int i = 0;
      while (i < paramDocument.attributes.size())
      {
        if ((paramDocument.attributes.get(i) instanceof TLRPC.TL_documentAttributeAnimated)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public static boolean isNewGifMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      return isNewGifDocument(paramMessage.media.webpage.document);
    }
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isNewGifDocument(paramMessage.media.document));
  }
  
  public static boolean isOut(TLRPC.Message paramMessage)
  {
    return paramMessage.out;
  }
  
  public static boolean isStickerDocument(TLRPC.Document paramDocument)
  {
    if (paramDocument != null)
    {
      int i = 0;
      while (i < paramDocument.attributes.size())
      {
        if (((TLRPC.DocumentAttribute)paramDocument.attributes.get(i) instanceof TLRPC.TL_documentAttributeSticker)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public static boolean isStickerMessage(TLRPC.Message paramMessage)
  {
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isStickerDocument(paramMessage.media.document));
  }
  
  public static boolean isUnread(TLRPC.Message paramMessage)
  {
    return paramMessage.unread;
  }
  
  public static boolean isVideoDocument(TLRPC.Document paramDocument)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramDocument != null)
    {
      int k = 0;
      int i = 0;
      int j = 0;
      if (j < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(j);
        int m;
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
          m = 1;
        }
        for (;;)
        {
          j += 1;
          i = m;
          break;
          m = i;
          if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAnimated))
          {
            k = 1;
            m = i;
          }
        }
      }
      bool1 = bool2;
      if (i != 0)
      {
        bool1 = bool2;
        if (k == 0) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public static boolean isVideoMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      return isVideoDocument(paramMessage.media.webpage.document);
    }
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isVideoDocument(paramMessage.media.document));
  }
  
  public static boolean isVoiceDocument(TLRPC.Document paramDocument)
  {
    if (paramDocument != null)
    {
      int i = 0;
      while (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio)) {
          return localDocumentAttribute.voice;
        }
        i += 1;
      }
    }
    return false;
  }
  
  public static boolean isVoiceMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      return isVoiceDocument(paramMessage.media.webpage.document);
    }
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isVoiceDocument(paramMessage.media.document));
  }
  
  public static void setUnreadFlags(TLRPC.Message paramMessage, int paramInt)
  {
    boolean bool2 = true;
    if ((paramInt & 0x1) == 0)
    {
      bool1 = true;
      paramMessage.unread = bool1;
      if ((paramInt & 0x2) != 0) {
        break label34;
      }
    }
    label34:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      paramMessage.media_unread = bool1;
      return;
      bool1 = false;
      break;
    }
  }
  
  public void applyNewText()
  {
    if (TextUtils.isEmpty(this.messageOwner.message)) {
      return;
    }
    TLRPC.User localUser = null;
    if (isFromUser()) {
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
    }
    this.messageText = this.messageOwner.message;
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {}
    for (TextPaint localTextPaint = gameTextPaint;; localTextPaint = textPaint)
    {
      this.messageText = Emoji.replaceEmoji(this.messageText, localTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      generateLayout(localUser);
      return;
    }
  }
  
  public boolean canDeleteMessage(TLRPC.Chat paramChat)
  {
    return canDeleteMessage(this.messageOwner, paramChat);
  }
  
  public boolean canEditMessage(TLRPC.Chat paramChat)
  {
    return canEditMessage(this.messageOwner, paramChat);
  }
  
  public boolean checkLayout()
  {
    if ((this.type != 0) || (this.messageOwner.to_id == null) || (this.messageText == null) || (this.messageText.length() == 0)) {
      return false;
    }
    int i;
    if (this.layoutCreated)
    {
      if (!AndroidUtilities.isTablet()) {
        break label161;
      }
      i = AndroidUtilities.getMinTabletSide();
      if (Math.abs(this.generatedWithMinSize - i) > AndroidUtilities.dp(52.0F)) {
        this.layoutCreated = false;
      }
    }
    if (!this.layoutCreated)
    {
      this.layoutCreated = true;
      TLRPC.User localUser = null;
      if (isFromUser()) {
        localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
      }
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {}
      for (TextPaint localTextPaint = gameTextPaint;; localTextPaint = textPaint)
      {
        this.messageText = Emoji.replaceEmoji(this.messageText, localTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        generateLayout(localUser);
        return true;
        label161:
        i = AndroidUtilities.displaySize.x;
        break;
      }
    }
    return false;
  }
  
  public void checkMediaExistance()
  {
    this.attachPathExists = false;
    this.mediaExists = false;
    if (this.type == 1) {
      if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
        this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
      }
    }
    Object localObject;
    do
    {
      do
      {
        do
        {
          return;
          if ((this.type != 8) && (this.type != 3) && (this.type != 9) && (this.type != 2) && (this.type != 14)) {
            break;
          }
          if ((this.messageOwner.attachPath != null) && (this.messageOwner.attachPath.length() > 0)) {
            this.attachPathExists = new File(this.messageOwner.attachPath).exists();
          }
        } while (this.attachPathExists);
        this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
        return;
        localObject = getDocument();
        if (localObject != null)
        {
          this.mediaExists = FileLoader.getPathToAttach((TLObject)localObject).exists();
          return;
        }
      } while (this.type != 0);
      localObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
    } while ((localObject == null) || (localObject == null));
    this.mediaExists = FileLoader.getPathToAttach((TLObject)localObject, true).exists();
  }
  
  public void generateCaption()
  {
    if (this.caption != null) {}
    do
    {
      do
      {
        return;
      } while ((this.messageOwner.media == null) || (this.messageOwner.media.caption == null) || (this.messageOwner.media.caption.length() <= 0));
      this.caption = Emoji.replaceEmoji(this.messageOwner.media.caption, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
    } while (!containsUrls(this.caption));
    try
    {
      Linkify.addLinks((Spannable)this.caption, 5);
      addUsernamesAndHashtags(this.caption, true);
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
  
  public void generateGameMessageText(TLRPC.User paramUser)
  {
    TLRPC.User localUser = paramUser;
    if (paramUser == null)
    {
      localUser = paramUser;
      if (this.messageOwner.from_id > 0) {
        localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
      }
    }
    Object localObject = null;
    paramUser = (TLRPC.User)localObject;
    if (this.replyMessageObject != null)
    {
      paramUser = (TLRPC.User)localObject;
      if (this.replyMessageObject.messageOwner.media != null)
      {
        paramUser = (TLRPC.User)localObject;
        if (this.replyMessageObject.messageOwner.media.game != null) {
          paramUser = this.replyMessageObject.messageOwner.media.game;
        }
      }
    }
    if (paramUser == null)
    {
      if ((localUser != null) && (localUser.id == UserConfig.getClientUserId()))
      {
        this.messageText = LocaleController.formatString("ActionYouScored", 2131165252, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) });
        return;
      }
      this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", 2131165243, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) }), "un1", localUser);
      return;
    }
    if ((localUser != null) && (localUser.id == UserConfig.getClientUserId())) {}
    for (this.messageText = LocaleController.formatString("ActionYouScoredInGame", 2131165253, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) });; this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", 2131165244, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) }), "un1", localUser))
    {
      this.messageText = replaceWithLink(this.messageText, "un2", paramUser);
      return;
    }
  }
  
  /* Error */
  public void generateLayout(TLRPC.User paramUser)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 66	org/telegram/messenger/MessageObject:type	I
    //   4: ifne +32 -> 36
    //   7: aload_0
    //   8: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11: getfield 313	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   14: ifnull +22 -> 36
    //   17: aload_0
    //   18: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   21: ifnull +15 -> 36
    //   24: aload_0
    //   25: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   28: invokeinterface 643 1 0
    //   33: ifne +4 -> 37
    //   36: return
    //   37: aload_0
    //   38: invokevirtual 994	org/telegram/messenger/MessageObject:generateLinkDescription	()V
    //   41: aload_0
    //   42: new 300	java/util/ArrayList
    //   45: dup
    //   46: invokespecial 995	java/util/ArrayList:<init>	()V
    //   49: putfield 997	org/telegram/messenger/MessageObject:textLayoutBlocks	Ljava/util/ArrayList;
    //   52: aload_0
    //   53: iconst_0
    //   54: putfield 999	org/telegram/messenger/MessageObject:textWidth	I
    //   57: aload_0
    //   58: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   61: getfield 1002	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   64: ifeq +314 -> 378
    //   67: iconst_0
    //   68: istore 9
    //   70: iconst_0
    //   71: istore 8
    //   73: iload 9
    //   75: istore 7
    //   77: iload 8
    //   79: aload_0
    //   80: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   83: getfield 1005	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   86: invokevirtual 303	java/util/ArrayList:size	()I
    //   89: if_icmpge +24 -> 113
    //   92: aload_0
    //   93: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   96: getfield 1005	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   99: iload 8
    //   101: invokevirtual 306	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   104: instanceof 1007
    //   107: ifne +262 -> 369
    //   110: iconst_1
    //   111: istore 7
    //   113: iload 7
    //   115: ifne +288 -> 403
    //   118: aload_0
    //   119: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   122: instanceof 1009
    //   125: ifne +103 -> 228
    //   128: aload_0
    //   129: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   132: instanceof 1011
    //   135: ifne +93 -> 228
    //   138: aload_0
    //   139: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   142: instanceof 1013
    //   145: ifne +83 -> 228
    //   148: aload_0
    //   149: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   152: instanceof 1015
    //   155: ifne +73 -> 228
    //   158: aload_0
    //   159: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   162: instanceof 1017
    //   165: ifne +63 -> 228
    //   168: aload_0
    //   169: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   172: instanceof 1019
    //   175: ifne +53 -> 228
    //   178: aload_0
    //   179: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   182: instanceof 1021
    //   185: ifne +43 -> 228
    //   188: aload_0
    //   189: invokevirtual 147	org/telegram/messenger/MessageObject:isOut	()Z
    //   192: ifeq +13 -> 205
    //   195: aload_0
    //   196: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   199: getfield 1002	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   202: ifne +26 -> 228
    //   205: aload_0
    //   206: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   209: getfield 203	org/telegram/tgnet/TLRPC$Message:id	I
    //   212: iflt +16 -> 228
    //   215: aload_0
    //   216: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   219: getfield 229	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   222: instanceof 579
    //   225: ifeq +178 -> 403
    //   228: iconst_1
    //   229: istore 7
    //   231: iload 7
    //   233: ifeq +176 -> 409
    //   236: aload_0
    //   237: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   240: invokestatic 1023	org/telegram/messenger/MessageObject:addLinks	(Ljava/lang/CharSequence;)V
    //   243: aload_0
    //   244: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   247: instanceof 636
    //   250: ifeq +1060 -> 1310
    //   253: aload_0
    //   254: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   257: checkcast 636	android/text/Spannable
    //   260: astore 18
    //   262: aload_0
    //   263: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   266: getfield 1005	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   269: invokevirtual 303	java/util/ArrayList:size	()I
    //   272: istore 10
    //   274: aload 18
    //   276: iconst_0
    //   277: aload_0
    //   278: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   281: invokeinterface 643 1 0
    //   286: ldc_w 1025
    //   289: invokeinterface 1029 4 0
    //   294: checkcast 1031	[Landroid/text/style/URLSpan;
    //   297: astore 19
    //   299: iconst_0
    //   300: istore 8
    //   302: iload 8
    //   304: iload 10
    //   306: if_icmpge +1004 -> 1310
    //   309: aload_0
    //   310: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   313: getfield 1005	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   316: iload 8
    //   318: invokevirtual 306	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   321: checkcast 1033	org/telegram/tgnet/TLRPC$MessageEntity
    //   324: astore 20
    //   326: aload 20
    //   328: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   331: ifle +29 -> 360
    //   334: aload 20
    //   336: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   339: iflt +21 -> 360
    //   342: aload 20
    //   344: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   347: aload_0
    //   348: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   351: getfield 200	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   354: invokevirtual 207	java/lang/String:length	()I
    //   357: if_icmplt +105 -> 462
    //   360: iload 8
    //   362: iconst_1
    //   363: iadd
    //   364: istore 8
    //   366: goto -64 -> 302
    //   369: iload 8
    //   371: iconst_1
    //   372: iadd
    //   373: istore 8
    //   375: goto -302 -> 73
    //   378: aload_0
    //   379: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   382: getfield 1005	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   385: invokevirtual 1040	java/util/ArrayList:isEmpty	()Z
    //   388: ifne +9 -> 397
    //   391: iconst_1
    //   392: istore 7
    //   394: goto -281 -> 113
    //   397: iconst_0
    //   398: istore 7
    //   400: goto -6 -> 394
    //   403: iconst_0
    //   404: istore 7
    //   406: goto -175 -> 231
    //   409: aload_0
    //   410: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   413: instanceof 636
    //   416: ifeq -173 -> 243
    //   419: aload_0
    //   420: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   423: invokeinterface 643 1 0
    //   428: sipush 200
    //   431: if_icmpge -188 -> 243
    //   434: aload_0
    //   435: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   438: checkcast 636	android/text/Spannable
    //   441: iconst_4
    //   442: invokestatic 648	android/text/util/Linkify:addLinks	(Landroid/text/Spannable;I)Z
    //   445: pop
    //   446: goto -203 -> 243
    //   449: astore 18
    //   451: ldc_w 653
    //   454: aload 18
    //   456: invokestatic 659	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   459: goto -216 -> 243
    //   462: aload 20
    //   464: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   467: aload 20
    //   469: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   472: iadd
    //   473: aload_0
    //   474: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   477: getfield 200	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   480: invokevirtual 207	java/lang/String:length	()I
    //   483: if_icmple +24 -> 507
    //   486: aload 20
    //   488: aload_0
    //   489: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   492: getfield 200	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   495: invokevirtual 207	java/lang/String:length	()I
    //   498: aload 20
    //   500: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   503: isub
    //   504: putfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   507: aload 19
    //   509: ifnull +138 -> 647
    //   512: aload 19
    //   514: arraylength
    //   515: ifle +132 -> 647
    //   518: iconst_0
    //   519: istore 9
    //   521: iload 9
    //   523: aload 19
    //   525: arraylength
    //   526: if_icmpge +121 -> 647
    //   529: aload 19
    //   531: iload 9
    //   533: aaload
    //   534: ifnonnull +12 -> 546
    //   537: iload 9
    //   539: iconst_1
    //   540: iadd
    //   541: istore 9
    //   543: goto -22 -> 521
    //   546: aload 18
    //   548: aload 19
    //   550: iload 9
    //   552: aaload
    //   553: invokeinterface 1044 2 0
    //   558: istore 11
    //   560: aload 18
    //   562: aload 19
    //   564: iload 9
    //   566: aaload
    //   567: invokeinterface 1047 2 0
    //   572: istore 12
    //   574: aload 20
    //   576: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   579: iload 11
    //   581: if_icmpgt +19 -> 600
    //   584: aload 20
    //   586: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   589: aload 20
    //   591: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   594: iadd
    //   595: iload 11
    //   597: if_icmpge +29 -> 626
    //   600: aload 20
    //   602: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   605: iload 12
    //   607: if_icmpgt -70 -> 537
    //   610: aload 20
    //   612: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   615: aload 20
    //   617: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   620: iadd
    //   621: iload 12
    //   623: if_icmplt -86 -> 537
    //   626: aload 18
    //   628: aload 19
    //   630: iload 9
    //   632: aaload
    //   633: invokeinterface 1051 2 0
    //   638: aload 19
    //   640: iload 9
    //   642: aconst_null
    //   643: aastore
    //   644: goto -107 -> 537
    //   647: aload 20
    //   649: instanceof 1053
    //   652: ifeq +44 -> 696
    //   655: aload 18
    //   657: new 1055	org/telegram/ui/Components/TypefaceSpan
    //   660: dup
    //   661: ldc_w 1057
    //   664: invokestatic 1061	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   667: invokespecial 1064	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   670: aload 20
    //   672: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   675: aload 20
    //   677: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   680: aload 20
    //   682: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   685: iadd
    //   686: bipush 33
    //   688: invokeinterface 702 5 0
    //   693: goto -333 -> 360
    //   696: aload 20
    //   698: instanceof 1066
    //   701: ifeq +44 -> 745
    //   704: aload 18
    //   706: new 1055	org/telegram/ui/Components/TypefaceSpan
    //   709: dup
    //   710: ldc_w 1068
    //   713: invokestatic 1061	org/telegram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   716: invokespecial 1064	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   719: aload 20
    //   721: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   724: aload 20
    //   726: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   729: aload 20
    //   731: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   734: iadd
    //   735: bipush 33
    //   737: invokeinterface 702 5 0
    //   742: goto -382 -> 360
    //   745: aload 20
    //   747: instanceof 1070
    //   750: ifne +11 -> 761
    //   753: aload 20
    //   755: instanceof 1072
    //   758: ifeq +53 -> 811
    //   761: aload 18
    //   763: new 1055	org/telegram/ui/Components/TypefaceSpan
    //   766: dup
    //   767: getstatic 1078	android/graphics/Typeface:MONOSPACE	Landroid/graphics/Typeface;
    //   770: invokestatic 89	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
    //   773: getfield 92	org/telegram/messenger/MessagesController:fontSize	I
    //   776: iconst_1
    //   777: isub
    //   778: i2f
    //   779: invokestatic 98	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   782: invokespecial 1081	org/telegram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;I)V
    //   785: aload 20
    //   787: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   790: aload 20
    //   792: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   795: aload 20
    //   797: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   800: iadd
    //   801: bipush 33
    //   803: invokeinterface 702 5 0
    //   808: goto -448 -> 360
    //   811: aload 20
    //   813: instanceof 1083
    //   816: ifeq +64 -> 880
    //   819: aload 18
    //   821: new 1085	org/telegram/ui/Components/URLSpanUserMention
    //   824: dup
    //   825: new 466	java/lang/StringBuilder
    //   828: dup
    //   829: invokespecial 467	java/lang/StringBuilder:<init>	()V
    //   832: ldc -96
    //   834: invokevirtual 471	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   837: aload 20
    //   839: checkcast 1083	org/telegram/tgnet/TLRPC$TL_messageEntityMentionName
    //   842: getfield 1086	org/telegram/tgnet/TLRPC$TL_messageEntityMentionName:user_id	I
    //   845: invokevirtual 474	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   848: invokevirtual 478	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   851: invokespecial 1087	org/telegram/ui/Components/URLSpanUserMention:<init>	(Ljava/lang/String;)V
    //   854: aload 20
    //   856: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   859: aload 20
    //   861: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   864: aload 20
    //   866: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   869: iadd
    //   870: bipush 33
    //   872: invokeinterface 702 5 0
    //   877: goto -517 -> 360
    //   880: aload 20
    //   882: instanceof 1007
    //   885: ifeq +67 -> 952
    //   888: aload 18
    //   890: new 1085	org/telegram/ui/Components/URLSpanUserMention
    //   893: dup
    //   894: new 466	java/lang/StringBuilder
    //   897: dup
    //   898: invokespecial 467	java/lang/StringBuilder:<init>	()V
    //   901: ldc -96
    //   903: invokevirtual 471	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   906: aload 20
    //   908: checkcast 1007	org/telegram/tgnet/TLRPC$TL_inputMessageEntityMentionName
    //   911: getfield 1090	org/telegram/tgnet/TLRPC$TL_inputMessageEntityMentionName:user_id	Lorg/telegram/tgnet/TLRPC$InputUser;
    //   914: getfield 1093	org/telegram/tgnet/TLRPC$InputUser:user_id	I
    //   917: invokevirtual 474	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   920: invokevirtual 478	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   923: invokespecial 1087	org/telegram/ui/Components/URLSpanUserMention:<init>	(Ljava/lang/String;)V
    //   926: aload 20
    //   928: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   931: aload 20
    //   933: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   936: aload 20
    //   938: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   941: iadd
    //   942: bipush 33
    //   944: invokeinterface 702 5 0
    //   949: goto -589 -> 360
    //   952: iload 7
    //   954: ifne -594 -> 360
    //   957: aload_0
    //   958: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   961: getfield 200	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   964: aload 20
    //   966: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   969: aload 20
    //   971: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   974: aload 20
    //   976: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   979: iadd
    //   980: invokevirtual 1097	java/lang/String:substring	(II)Ljava/lang/String;
    //   983: astore 21
    //   985: aload 20
    //   987: instanceof 1099
    //   990: ifeq +40 -> 1030
    //   993: aload 18
    //   995: new 690	org/telegram/ui/Components/URLSpanBotCommand
    //   998: dup
    //   999: aload 21
    //   1001: invokespecial 698	org/telegram/ui/Components/URLSpanBotCommand:<init>	(Ljava/lang/String;)V
    //   1004: aload 20
    //   1006: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1009: aload 20
    //   1011: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1014: aload 20
    //   1016: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1019: iadd
    //   1020: bipush 33
    //   1022: invokeinterface 702 5 0
    //   1027: goto -667 -> 360
    //   1030: aload 20
    //   1032: instanceof 1101
    //   1035: ifne +11 -> 1046
    //   1038: aload 20
    //   1040: instanceof 1103
    //   1043: ifeq +40 -> 1083
    //   1046: aload 18
    //   1048: new 704	org/telegram/ui/Components/URLSpanNoUnderline
    //   1051: dup
    //   1052: aload 21
    //   1054: invokespecial 705	org/telegram/ui/Components/URLSpanNoUnderline:<init>	(Ljava/lang/String;)V
    //   1057: aload 20
    //   1059: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1062: aload 20
    //   1064: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1067: aload 20
    //   1069: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1072: iadd
    //   1073: bipush 33
    //   1075: invokeinterface 702 5 0
    //   1080: goto -720 -> 360
    //   1083: aload 20
    //   1085: instanceof 1105
    //   1088: ifeq +59 -> 1147
    //   1091: aload 18
    //   1093: new 1107	org/telegram/ui/Components/URLSpanReplacement
    //   1096: dup
    //   1097: new 466	java/lang/StringBuilder
    //   1100: dup
    //   1101: invokespecial 467	java/lang/StringBuilder:<init>	()V
    //   1104: ldc_w 1109
    //   1107: invokevirtual 471	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1110: aload 21
    //   1112: invokevirtual 471	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1115: invokevirtual 478	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1118: invokespecial 1110	org/telegram/ui/Components/URLSpanReplacement:<init>	(Ljava/lang/String;)V
    //   1121: aload 20
    //   1123: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1126: aload 20
    //   1128: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1131: aload 20
    //   1133: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1136: iadd
    //   1137: bipush 33
    //   1139: invokeinterface 702 5 0
    //   1144: goto -784 -> 360
    //   1147: aload 20
    //   1149: instanceof 1112
    //   1152: ifeq +110 -> 1262
    //   1155: aload 21
    //   1157: invokevirtual 1115	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   1160: ldc_w 1117
    //   1163: invokevirtual 1120	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1166: ifne +59 -> 1225
    //   1169: aload 18
    //   1171: new 1025	android/text/style/URLSpan
    //   1174: dup
    //   1175: new 466	java/lang/StringBuilder
    //   1178: dup
    //   1179: invokespecial 467	java/lang/StringBuilder:<init>	()V
    //   1182: ldc_w 1122
    //   1185: invokevirtual 471	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1188: aload 21
    //   1190: invokevirtual 471	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1193: invokevirtual 478	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1196: invokespecial 1123	android/text/style/URLSpan:<init>	(Ljava/lang/String;)V
    //   1199: aload 20
    //   1201: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1204: aload 20
    //   1206: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1209: aload 20
    //   1211: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1214: iadd
    //   1215: bipush 33
    //   1217: invokeinterface 702 5 0
    //   1222: goto -862 -> 360
    //   1225: aload 18
    //   1227: new 1025	android/text/style/URLSpan
    //   1230: dup
    //   1231: aload 21
    //   1233: invokespecial 1123	android/text/style/URLSpan:<init>	(Ljava/lang/String;)V
    //   1236: aload 20
    //   1238: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1241: aload 20
    //   1243: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1246: aload 20
    //   1248: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1251: iadd
    //   1252: bipush 33
    //   1254: invokeinterface 702 5 0
    //   1259: goto -899 -> 360
    //   1262: aload 20
    //   1264: instanceof 1125
    //   1267: ifeq -907 -> 360
    //   1270: aload 18
    //   1272: new 1107	org/telegram/ui/Components/URLSpanReplacement
    //   1275: dup
    //   1276: aload 20
    //   1278: getfield 1128	org/telegram/tgnet/TLRPC$MessageEntity:url	Ljava/lang/String;
    //   1281: invokespecial 1110	org/telegram/ui/Components/URLSpanReplacement:<init>	(Ljava/lang/String;)V
    //   1284: aload 20
    //   1286: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1289: aload 20
    //   1291: getfield 1038	org/telegram/tgnet/TLRPC$MessageEntity:offset	I
    //   1294: aload 20
    //   1296: getfield 1035	org/telegram/tgnet/TLRPC$MessageEntity:length	I
    //   1299: iadd
    //   1300: bipush 33
    //   1302: invokeinterface 702 5 0
    //   1307: goto -947 -> 360
    //   1310: aload_0
    //   1311: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1314: getfield 117	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   1317: ifle +592 -> 1909
    //   1320: aload_0
    //   1321: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1324: getfield 313	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   1327: getfield 318	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   1330: ifne +16 -> 1346
    //   1333: aload_0
    //   1334: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1337: getfield 313	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   1340: getfield 779	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   1343: ifeq +566 -> 1909
    //   1346: aload_0
    //   1347: invokevirtual 147	org/telegram/messenger/MessageObject:isOut	()Z
    //   1350: ifne +559 -> 1909
    //   1353: iconst_1
    //   1354: istore 7
    //   1356: invokestatic 908	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   1359: ifeq +556 -> 1915
    //   1362: invokestatic 911	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   1365: istore 8
    //   1367: aload_0
    //   1368: iload 8
    //   1370: putfield 913	org/telegram/messenger/MessageObject:generatedWithMinSize	I
    //   1373: aload_0
    //   1374: getfield 913	org/telegram/messenger/MessageObject:generatedWithMinSize	I
    //   1377: istore 8
    //   1379: iload 7
    //   1381: ifeq +545 -> 1926
    //   1384: ldc_w 1129
    //   1387: fstore_2
    //   1388: iload 8
    //   1390: fload_2
    //   1391: invokestatic 98	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1394: isub
    //   1395: istore 8
    //   1397: aload_1
    //   1398: ifnull +10 -> 1408
    //   1401: aload_1
    //   1402: getfield 1132	org/telegram/tgnet/TLRPC$User:bot	Z
    //   1405: ifne +52 -> 1457
    //   1408: aload_0
    //   1409: invokevirtual 321	org/telegram/messenger/MessageObject:isMegagroup	()Z
    //   1412: ifne +34 -> 1446
    //   1415: iload 8
    //   1417: istore 7
    //   1419: aload_0
    //   1420: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1423: getfield 1136	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   1426: ifnull +41 -> 1467
    //   1429: iload 8
    //   1431: istore 7
    //   1433: aload_0
    //   1434: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1437: getfield 1136	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$TL_messageFwdHeader;
    //   1440: getfield 1139	org/telegram/tgnet/TLRPC$TL_messageFwdHeader:channel_id	I
    //   1443: ifeq +24 -> 1467
    //   1446: iload 8
    //   1448: istore 7
    //   1450: aload_0
    //   1451: invokevirtual 147	org/telegram/messenger/MessageObject:isOut	()Z
    //   1454: ifne +13 -> 1467
    //   1457: iload 8
    //   1459: ldc -20
    //   1461: invokestatic 98	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1464: isub
    //   1465: istore 7
    //   1467: iload 7
    //   1469: istore 8
    //   1471: aload_0
    //   1472: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1475: getfield 229	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1478: instanceof 231
    //   1481: ifeq +14 -> 1495
    //   1484: iload 7
    //   1486: ldc_w 1140
    //   1489: invokestatic 98	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1492: isub
    //   1493: istore 8
    //   1495: aload_0
    //   1496: getfield 105	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1499: getfield 229	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   1502: instanceof 231
    //   1505: ifeq +428 -> 1933
    //   1508: getstatic 83	org/telegram/messenger/MessageObject:gameTextPaint	Landroid/text/TextPaint;
    //   1511: astore_1
    //   1512: new 1142	android/text/StaticLayout
    //   1515: dup
    //   1516: aload_0
    //   1517: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1520: aload_1
    //   1521: iload 8
    //   1523: getstatic 1148	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1526: fconst_1
    //   1527: fconst_0
    //   1528: iconst_0
    //   1529: invokespecial 1151	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1532: astore 18
    //   1534: aload_0
    //   1535: aload 18
    //   1537: invokevirtual 1154	android/text/StaticLayout:getHeight	()I
    //   1540: putfield 1156	org/telegram/messenger/MessageObject:textHeight	I
    //   1543: aload 18
    //   1545: invokevirtual 1159	android/text/StaticLayout:getLineCount	()I
    //   1548: istore 16
    //   1550: iload 16
    //   1552: i2f
    //   1553: ldc_w 1140
    //   1556: fdiv
    //   1557: f2d
    //   1558: invokestatic 1163	java/lang/Math:ceil	(D)D
    //   1561: d2i
    //   1562: istore 17
    //   1564: iconst_0
    //   1565: istore 10
    //   1567: fconst_0
    //   1568: fstore_2
    //   1569: iconst_0
    //   1570: istore 9
    //   1572: iload 9
    //   1574: iload 17
    //   1576: if_icmpge -1540 -> 36
    //   1579: bipush 10
    //   1581: iload 16
    //   1583: iload 10
    //   1585: isub
    //   1586: invokestatic 1167	java/lang/Math:min	(II)I
    //   1589: istore 7
    //   1591: new 6	org/telegram/messenger/MessageObject$TextLayoutBlock
    //   1594: dup
    //   1595: invokespecial 1168	org/telegram/messenger/MessageObject$TextLayoutBlock:<init>	()V
    //   1598: astore 19
    //   1600: iload 17
    //   1602: iconst_1
    //   1603: if_icmpne +346 -> 1949
    //   1606: aload 19
    //   1608: aload 18
    //   1610: putfield 1172	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1613: aload 19
    //   1615: fconst_0
    //   1616: putfield 1175	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   1619: aload 19
    //   1621: iconst_0
    //   1622: putfield 1178	org/telegram/messenger/MessageObject$TextLayoutBlock:charactersOffset	I
    //   1625: aload 19
    //   1627: aload_0
    //   1628: getfield 1156	org/telegram/messenger/MessageObject:textHeight	I
    //   1631: putfield 1181	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   1634: iload 7
    //   1636: istore 11
    //   1638: aload_0
    //   1639: getfield 997	org/telegram/messenger/MessageObject:textLayoutBlocks	Ljava/util/ArrayList;
    //   1642: aload 19
    //   1644: invokevirtual 1184	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1647: pop
    //   1648: fconst_0
    //   1649: fstore_3
    //   1650: aload 19
    //   1652: fconst_0
    //   1653: putfield 1187	org/telegram/messenger/MessageObject$TextLayoutBlock:textXOffset	F
    //   1656: aload 19
    //   1658: getfield 1172	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1661: iload 11
    //   1663: iconst_1
    //   1664: isub
    //   1665: invokevirtual 1191	android/text/StaticLayout:getLineLeft	(I)F
    //   1668: fstore 4
    //   1670: aload 19
    //   1672: fload 4
    //   1674: putfield 1187	org/telegram/messenger/MessageObject$TextLayoutBlock:textXOffset	F
    //   1677: fload 4
    //   1679: fstore_3
    //   1680: fconst_0
    //   1681: fstore 4
    //   1683: aload 19
    //   1685: getfield 1172	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1688: iload 11
    //   1690: iconst_1
    //   1691: isub
    //   1692: invokevirtual 1194	android/text/StaticLayout:getLineWidth	(I)F
    //   1695: fstore 5
    //   1697: fload 5
    //   1699: fstore 4
    //   1701: fload 4
    //   1703: f2d
    //   1704: invokestatic 1163	java/lang/Math:ceil	(D)D
    //   1707: d2i
    //   1708: istore 14
    //   1710: iconst_0
    //   1711: istore 7
    //   1713: iload 9
    //   1715: iload 17
    //   1717: iconst_1
    //   1718: isub
    //   1719: if_icmpne +9 -> 1728
    //   1722: aload_0
    //   1723: iload 14
    //   1725: putfield 1196	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   1728: fload 4
    //   1730: fload_3
    //   1731: fadd
    //   1732: f2d
    //   1733: invokestatic 1163	java/lang/Math:ceil	(D)D
    //   1736: d2i
    //   1737: istore 15
    //   1739: iload 15
    //   1741: istore 13
    //   1743: fload_3
    //   1744: fconst_0
    //   1745: fcmpl
    //   1746: ifne +6 -> 1752
    //   1749: iconst_1
    //   1750: istore 7
    //   1752: iload 11
    //   1754: iconst_1
    //   1755: if_icmple +582 -> 2337
    //   1758: fconst_0
    //   1759: fstore_3
    //   1760: fconst_0
    //   1761: fstore 4
    //   1763: iconst_0
    //   1764: istore 12
    //   1766: iload 12
    //   1768: iload 11
    //   1770: if_icmpge +475 -> 2245
    //   1773: aload 19
    //   1775: getfield 1172	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1778: iload 12
    //   1780: invokevirtual 1194	android/text/StaticLayout:getLineWidth	(I)F
    //   1783: fstore 5
    //   1785: fload 5
    //   1787: fstore 6
    //   1789: fload 5
    //   1791: iload 8
    //   1793: bipush 20
    //   1795: iadd
    //   1796: i2f
    //   1797: fcmpl
    //   1798: ifle +8 -> 1806
    //   1801: iload 8
    //   1803: i2f
    //   1804: fstore 6
    //   1806: aload 19
    //   1808: getfield 1172	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1811: iload 12
    //   1813: invokevirtual 1191	android/text/StaticLayout:getLineLeft	(I)F
    //   1816: fstore 5
    //   1818: fload 5
    //   1820: fconst_0
    //   1821: fcmpl
    //   1822: iflt +18 -> 1840
    //   1825: aload 19
    //   1827: aload 19
    //   1829: getfield 1187	org/telegram/messenger/MessageObject$TextLayoutBlock:textXOffset	F
    //   1832: fload 5
    //   1834: invokestatic 1199	java/lang/Math:min	(FF)F
    //   1837: putfield 1187	org/telegram/messenger/MessageObject$TextLayoutBlock:textXOffset	F
    //   1840: fload 5
    //   1842: fconst_0
    //   1843: fcmpl
    //   1844: ifne +6 -> 1850
    //   1847: iconst_1
    //   1848: istore 7
    //   1850: fload_3
    //   1851: fload 6
    //   1853: invokestatic 1202	java/lang/Math:max	(FF)F
    //   1856: fstore_3
    //   1857: fload 4
    //   1859: fload 6
    //   1861: fload 5
    //   1863: fadd
    //   1864: invokestatic 1202	java/lang/Math:max	(FF)F
    //   1867: fstore 4
    //   1869: iload 14
    //   1871: fload 6
    //   1873: f2d
    //   1874: invokestatic 1163	java/lang/Math:ceil	(D)D
    //   1877: d2i
    //   1878: invokestatic 1204	java/lang/Math:max	(II)I
    //   1881: istore 14
    //   1883: iload 13
    //   1885: fload 6
    //   1887: fload 5
    //   1889: fadd
    //   1890: f2d
    //   1891: invokestatic 1163	java/lang/Math:ceil	(D)D
    //   1894: d2i
    //   1895: invokestatic 1204	java/lang/Math:max	(II)I
    //   1898: istore 13
    //   1900: iload 12
    //   1902: iconst_1
    //   1903: iadd
    //   1904: istore 12
    //   1906: goto -140 -> 1766
    //   1909: iconst_0
    //   1910: istore 7
    //   1912: goto -556 -> 1356
    //   1915: getstatic 918	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1918: getfield 923	android/graphics/Point:x	I
    //   1921: istore 8
    //   1923: goto -556 -> 1367
    //   1926: ldc_w 1205
    //   1929: fstore_2
    //   1930: goto -542 -> 1388
    //   1933: getstatic 68	org/telegram/messenger/MessageObject:textPaint	Landroid/text/TextPaint;
    //   1936: astore_1
    //   1937: goto -425 -> 1512
    //   1940: astore_1
    //   1941: ldc_w 653
    //   1944: aload_1
    //   1945: invokestatic 659	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1948: return
    //   1949: aload 18
    //   1951: iload 10
    //   1953: invokevirtual 1208	android/text/StaticLayout:getLineStart	(I)I
    //   1956: istore 11
    //   1958: aload 18
    //   1960: iload 10
    //   1962: iload 7
    //   1964: iadd
    //   1965: iconst_1
    //   1966: isub
    //   1967: invokevirtual 1211	android/text/StaticLayout:getLineEnd	(I)I
    //   1970: istore 12
    //   1972: iload 12
    //   1974: iload 11
    //   1976: if_icmpge +12 -> 1988
    //   1979: iload 9
    //   1981: iconst_1
    //   1982: iadd
    //   1983: istore 9
    //   1985: goto -413 -> 1572
    //   1988: aload 19
    //   1990: iload 11
    //   1992: putfield 1178	org/telegram/messenger/MessageObject$TextLayoutBlock:charactersOffset	I
    //   1995: aload 19
    //   1997: new 1142	android/text/StaticLayout
    //   2000: dup
    //   2001: aload_0
    //   2002: getfield 158	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2005: iload 11
    //   2007: iload 12
    //   2009: invokeinterface 694 3 0
    //   2014: aload_1
    //   2015: iload 8
    //   2017: getstatic 1148	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2020: fconst_1
    //   2021: fconst_0
    //   2022: iconst_0
    //   2023: invokespecial 1151	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   2026: putfield 1172	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2029: aload 19
    //   2031: aload 18
    //   2033: iload 10
    //   2035: invokevirtual 1214	android/text/StaticLayout:getLineTop	(I)I
    //   2038: i2f
    //   2039: putfield 1175	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2042: iload 9
    //   2044: ifeq +16 -> 2060
    //   2047: aload 19
    //   2049: aload 19
    //   2051: getfield 1175	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2054: fload_2
    //   2055: fsub
    //   2056: f2i
    //   2057: putfield 1181	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   2060: aload 19
    //   2062: aload 19
    //   2064: getfield 1181	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   2067: aload 19
    //   2069: getfield 1172	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2072: aload 19
    //   2074: getfield 1172	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2077: invokevirtual 1159	android/text/StaticLayout:getLineCount	()I
    //   2080: iconst_1
    //   2081: isub
    //   2082: invokevirtual 1217	android/text/StaticLayout:getLineBottom	(I)I
    //   2085: invokestatic 1204	java/lang/Math:max	(II)I
    //   2088: putfield 1181	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   2091: aload 19
    //   2093: getfield 1175	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2096: fstore_3
    //   2097: iload 7
    //   2099: istore 11
    //   2101: fload_3
    //   2102: fstore_2
    //   2103: iload 9
    //   2105: iload 17
    //   2107: iconst_1
    //   2108: isub
    //   2109: if_icmpne -471 -> 1638
    //   2112: iload 7
    //   2114: aload 19
    //   2116: getfield 1172	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2119: invokevirtual 1159	android/text/StaticLayout:getLineCount	()I
    //   2122: invokestatic 1204	java/lang/Math:max	(II)I
    //   2125: istore 11
    //   2127: aload_0
    //   2128: aload_0
    //   2129: getfield 1156	org/telegram/messenger/MessageObject:textHeight	I
    //   2132: aload 19
    //   2134: getfield 1175	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2137: aload 19
    //   2139: getfield 1172	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2142: invokevirtual 1154	android/text/StaticLayout:getHeight	()I
    //   2145: i2f
    //   2146: fadd
    //   2147: f2i
    //   2148: invokestatic 1204	java/lang/Math:max	(II)I
    //   2151: putfield 1156	org/telegram/messenger/MessageObject:textHeight	I
    //   2154: fload_3
    //   2155: fstore_2
    //   2156: goto -518 -> 1638
    //   2159: astore 20
    //   2161: ldc_w 653
    //   2164: aload 20
    //   2166: invokestatic 659	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2169: fload_3
    //   2170: fstore_2
    //   2171: goto -533 -> 1638
    //   2174: astore 19
    //   2176: ldc_w 653
    //   2179: aload 19
    //   2181: invokestatic 659	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2184: goto -205 -> 1979
    //   2187: astore 20
    //   2189: ldc_w 653
    //   2192: aload 20
    //   2194: invokestatic 659	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2197: goto -517 -> 1680
    //   2200: astore 20
    //   2202: ldc_w 653
    //   2205: aload 20
    //   2207: invokestatic 659	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2210: goto -509 -> 1701
    //   2213: astore 20
    //   2215: ldc_w 653
    //   2218: aload 20
    //   2220: invokestatic 659	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2223: fconst_0
    //   2224: fstore 5
    //   2226: goto -441 -> 1785
    //   2229: astore 20
    //   2231: ldc_w 653
    //   2234: aload 20
    //   2236: invokestatic 659	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   2239: fconst_0
    //   2240: fstore 5
    //   2242: goto -424 -> 1818
    //   2245: iload 7
    //   2247: ifeq +66 -> 2313
    //   2250: fload 4
    //   2252: fstore_3
    //   2253: fload_3
    //   2254: fstore 4
    //   2256: iload 9
    //   2258: iload 17
    //   2260: iconst_1
    //   2261: isub
    //   2262: if_icmpne +12 -> 2274
    //   2265: aload_0
    //   2266: iload 15
    //   2268: putfield 1196	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   2271: fload_3
    //   2272: fstore 4
    //   2274: aload_0
    //   2275: aload_0
    //   2276: getfield 999	org/telegram/messenger/MessageObject:textWidth	I
    //   2279: fload 4
    //   2281: f2d
    //   2282: invokestatic 1163	java/lang/Math:ceil	(D)D
    //   2285: d2i
    //   2286: invokestatic 1204	java/lang/Math:max	(II)I
    //   2289: putfield 999	org/telegram/messenger/MessageObject:textWidth	I
    //   2292: iload 7
    //   2294: ifeq +9 -> 2303
    //   2297: aload 19
    //   2299: fconst_0
    //   2300: putfield 1187	org/telegram/messenger/MessageObject$TextLayoutBlock:textXOffset	F
    //   2303: iload 10
    //   2305: iload 11
    //   2307: iadd
    //   2308: istore 10
    //   2310: goto -331 -> 1979
    //   2313: fload_3
    //   2314: fstore 4
    //   2316: iload 9
    //   2318: iload 17
    //   2320: iconst_1
    //   2321: isub
    //   2322: if_icmpne -48 -> 2274
    //   2325: aload_0
    //   2326: iload 14
    //   2328: putfield 1196	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   2331: fload_3
    //   2332: fstore 4
    //   2334: goto -60 -> 2274
    //   2337: aload_0
    //   2338: aload_0
    //   2339: getfield 999	org/telegram/messenger/MessageObject:textWidth	I
    //   2342: iload 8
    //   2344: iload 14
    //   2346: invokestatic 1167	java/lang/Math:min	(II)I
    //   2349: invokestatic 1204	java/lang/Math:max	(II)I
    //   2352: putfield 999	org/telegram/messenger/MessageObject:textWidth	I
    //   2355: goto -63 -> 2292
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	2358	0	this	MessageObject
    //   0	2358	1	paramUser	TLRPC.User
    //   1387	784	2	f1	float
    //   1649	683	3	f2	float
    //   1668	665	4	f3	float
    //   1695	546	5	f4	float
    //   1787	99	6	f5	float
    //   75	2218	7	i	int
    //   71	2272	8	j	int
    //   68	2255	9	k	int
    //   272	2037	10	m	int
    //   558	1750	11	n	int
    //   572	1436	12	i1	int
    //   1741	158	13	i2	int
    //   1708	637	14	i3	int
    //   1737	530	15	i4	int
    //   1548	38	16	i5	int
    //   1562	760	17	i6	int
    //   260	15	18	localSpannable	Spannable
    //   449	822	18	localThrowable	Throwable
    //   1532	500	18	localStaticLayout	StaticLayout
    //   297	1841	19	localObject	Object
    //   2174	124	19	localException1	Exception
    //   324	971	20	localMessageEntity	org.telegram.tgnet.TLRPC.MessageEntity
    //   2159	6	20	localException2	Exception
    //   2187	6	20	localException3	Exception
    //   2200	6	20	localException4	Exception
    //   2213	6	20	localException5	Exception
    //   2229	6	20	localException6	Exception
    //   983	249	21	str	String
    // Exception table:
    //   from	to	target	type
    //   434	446	449	java/lang/Throwable
    //   1512	1534	1940	java/lang/Exception
    //   2127	2154	2159	java/lang/Exception
    //   1995	2042	2174	java/lang/Exception
    //   2047	2060	2174	java/lang/Exception
    //   2060	2097	2174	java/lang/Exception
    //   1656	1677	2187	java/lang/Exception
    //   1683	1697	2200	java/lang/Exception
    //   1773	1785	2213	java/lang/Exception
    //   1806	1818	2229	java/lang/Exception
  }
  
  public void generateLinkDescription()
  {
    if (this.linkDescription != null) {
      return;
    }
    if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && ((this.messageOwner.media.webpage instanceof TLRPC.TL_webPage)) && (this.messageOwner.media.webpage.description != null)) {
      this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.webpage.description);
    }
    while (this.linkDescription != null)
    {
      if (containsUrls(this.linkDescription)) {}
      try
      {
        Linkify.addLinks((Spannable)this.linkDescription, 1);
        this.linkDescription = Emoji.replaceEmoji(this.linkDescription, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        return;
        if ((!(this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) || (this.messageOwner.media.game.description == null)) {
          continue;
        }
        this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.game.description);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
  }
  
  public void generatePinMessageText(TLRPC.User paramUser, TLRPC.Chat paramChat)
  {
    Object localObject = paramUser;
    TLRPC.Chat localChat = paramChat;
    if (paramUser == null)
    {
      localObject = paramUser;
      localChat = paramChat;
      if (paramChat == null)
      {
        if (this.messageOwner.from_id > 0) {
          paramUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        localObject = paramUser;
        localChat = paramChat;
        if (paramUser == null)
        {
          localChat = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
          localObject = paramUser;
        }
      }
    }
    if (this.replyMessageObject == null)
    {
      paramUser = LocaleController.getString("ActionPinnedNoText", 2131165234);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isMusic())
    {
      paramUser = LocaleController.getString("ActionPinnedMusic", 2131165233);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isVideo())
    {
      paramUser = LocaleController.getString("ActionPinnedVideo", 2131165238);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isGif())
    {
      paramUser = LocaleController.getString("ActionPinnedGif", 2131165232);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isVoice())
    {
      paramUser = LocaleController.getString("ActionPinnedVoice", 2131165239);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isSticker())
    {
      paramUser = LocaleController.getString("ActionPinnedSticker", 2131165236);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
    {
      paramUser = LocaleController.getString("ActionPinnedFile", 2131165229);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo))
    {
      paramUser = LocaleController.getString("ActionPinnedGeo", 2131165231);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
    {
      paramUser = LocaleController.getString("ActionPinnedContact", 2131165228);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      paramUser = LocaleController.getString("ActionPinnedPhoto", 2131165235);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
    {
      paramUser = LocaleController.formatString("ActionPinnedGame", 2131165230, new Object[] { "🎮 " + this.replyMessageObject.messageOwner.media.game.title });
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageText != null) && (this.replyMessageObject.messageText.length() > 0))
    {
      paramChat = this.replyMessageObject.messageText;
      paramUser = paramChat;
      if (paramChat.length() > 20) {
        paramUser = paramChat.subSequence(0, 20) + "...";
      }
      paramUser = LocaleController.formatString("ActionPinnedText", 2131165237, new Object[] { Emoji.replaceEmoji(paramUser, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false) });
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    paramUser = LocaleController.getString("ActionPinnedNoText", 2131165234);
    if (localObject != null) {}
    for (;;)
    {
      this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
      return;
      localObject = localChat;
    }
  }
  
  public void generateThumbs(boolean paramBoolean)
  {
    if ((this.messageOwner instanceof TLRPC.TL_messageService)) {
      if ((this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto))
      {
        if (paramBoolean) {
          break label52;
        }
        this.photoThumbs = new ArrayList(this.messageOwner.action.photo.sizes);
      }
    }
    label51:
    label52:
    label689:
    label843:
    label976:
    label1179:
    do
    {
      do
      {
        break label51;
        break label51;
        return;
        break label51;
        break label51;
        break label51;
        break label51;
        for (;;)
        {
          if ((this.photoThumbs != null) && (!this.photoThumbs.isEmpty()))
          {
            int i = 0;
            TLRPC.PhotoSize localPhotoSize1;
            int j;
            TLRPC.PhotoSize localPhotoSize2;
            while (i < this.photoThumbs.size())
            {
              localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
              j = 0;
              if (j < this.messageOwner.action.photo.sizes.size())
              {
                localPhotoSize2 = (TLRPC.PhotoSize)this.messageOwner.action.photo.sizes.get(j);
                if ((localPhotoSize2 instanceof TLRPC.TL_photoSizeEmpty)) {}
                while (!localPhotoSize2.type.equals(localPhotoSize1.type))
                {
                  j += 1;
                  break;
                }
                localPhotoSize1.location = localPhotoSize2.location;
              }
              i += 1;
            }
            continue;
            if ((this.messageOwner.media == null) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty))) {
              break;
            }
            if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
            {
              if ((!paramBoolean) || ((this.photoThumbs != null) && (this.photoThumbs.size() != this.messageOwner.media.photo.sizes.size())))
              {
                this.photoThumbs = new ArrayList(this.messageOwner.media.photo.sizes);
                return;
              }
              if ((this.photoThumbs == null) || (this.photoThumbs.isEmpty())) {
                break;
              }
              i = 0;
              while (i < this.photoThumbs.size())
              {
                localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
                j = 0;
                if (j < this.messageOwner.media.photo.sizes.size())
                {
                  localPhotoSize2 = (TLRPC.PhotoSize)this.messageOwner.media.photo.sizes.get(j);
                  if ((localPhotoSize2 instanceof TLRPC.TL_photoSizeEmpty)) {}
                  while (!localPhotoSize2.type.equals(localPhotoSize1.type))
                  {
                    j += 1;
                    break;
                  }
                  localPhotoSize1.location = localPhotoSize2.location;
                }
                i += 1;
              }
              continue;
            }
            if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
            {
              if ((this.messageOwner.media.document.thumb instanceof TLRPC.TL_photoSizeEmpty)) {
                break;
              }
              if (!paramBoolean)
              {
                this.photoThumbs = new ArrayList();
                this.photoThumbs.add(this.messageOwner.media.document.thumb);
                return;
              }
              if ((this.photoThumbs == null) || (this.photoThumbs.isEmpty()) || (this.messageOwner.media.document.thumb == null)) {
                break;
              }
              localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(0);
              localPhotoSize1.location = this.messageOwner.media.document.thumb.location;
              localPhotoSize1.w = this.messageOwner.media.document.thumb.w;
              localPhotoSize1.h = this.messageOwner.media.document.thumb.h;
              return;
            }
            if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
            {
              if ((this.messageOwner.media.game.document != null) && (!(this.messageOwner.media.game.document.thumb instanceof TLRPC.TL_photoSizeEmpty)))
              {
                if (!paramBoolean)
                {
                  this.photoThumbs = new ArrayList();
                  this.photoThumbs.add(this.messageOwner.media.game.document.thumb);
                }
              }
              else if (this.messageOwner.media.game.photo != null)
              {
                if ((paramBoolean) && (this.photoThumbs2 != null)) {
                  break label843;
                }
                this.photoThumbs2 = new ArrayList(this.messageOwner.media.game.photo.sizes);
              }
              for (;;)
              {
                if ((this.photoThumbs != null) || (this.photoThumbs2 == null)) {
                  break label976;
                }
                this.photoThumbs = this.photoThumbs2;
                this.photoThumbs2 = null;
                return;
                if ((this.photoThumbs == null) || (this.photoThumbs.isEmpty()) || (this.messageOwner.media.game.document.thumb == null)) {
                  break label689;
                }
                ((TLRPC.PhotoSize)this.photoThumbs.get(0)).location = this.messageOwner.media.game.document.thumb.location;
                break label689;
                if (this.photoThumbs2.isEmpty()) {
                  break;
                }
                i = 0;
                while (i < this.photoThumbs2.size())
                {
                  localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs2.get(i);
                  j = 0;
                  if (j < this.messageOwner.media.game.photo.sizes.size())
                  {
                    localPhotoSize2 = (TLRPC.PhotoSize)this.messageOwner.media.game.photo.sizes.get(j);
                    if ((localPhotoSize2 instanceof TLRPC.TL_photoSizeEmpty)) {}
                    while (!localPhotoSize2.type.equals(localPhotoSize1.type))
                    {
                      j += 1;
                      break;
                    }
                    localPhotoSize1.location = localPhotoSize2.location;
                  }
                  i += 1;
                }
              }
              break;
            }
            if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
              break;
            }
            if (this.messageOwner.media.webpage.photo == null) {
              break label1179;
            }
            if ((!paramBoolean) || (this.photoThumbs == null))
            {
              this.photoThumbs = new ArrayList(this.messageOwner.media.webpage.photo.sizes);
              return;
            }
            if (this.photoThumbs.isEmpty()) {
              break;
            }
            i = 0;
            while (i < this.photoThumbs.size())
            {
              localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
              j = 0;
              if (j < this.messageOwner.media.webpage.photo.sizes.size())
              {
                localPhotoSize2 = (TLRPC.PhotoSize)this.messageOwner.media.webpage.photo.sizes.get(j);
                if ((localPhotoSize2 instanceof TLRPC.TL_photoSizeEmpty)) {}
                while (!localPhotoSize2.type.equals(localPhotoSize1.type))
                {
                  j += 1;
                  break;
                }
                localPhotoSize1.location = localPhotoSize2.location;
              }
              i += 1;
            }
          }
        }
      } while ((this.messageOwner.media.webpage.document == null) || ((this.messageOwner.media.webpage.document.thumb instanceof TLRPC.TL_photoSizeEmpty)));
      if (!paramBoolean)
      {
        this.photoThumbs = new ArrayList();
        this.photoThumbs.add(this.messageOwner.media.webpage.document.thumb);
        return;
      }
    } while ((this.photoThumbs == null) || (this.photoThumbs.isEmpty()) || (this.messageOwner.media.webpage.document.thumb == null));
    ((TLRPC.PhotoSize)this.photoThumbs.get(0)).location = this.messageOwner.media.webpage.document.thumb.location;
  }
  
  public int getApproximateHeight()
  {
    int j;
    if (this.type == 0)
    {
      j = this.textHeight;
      if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && ((this.messageOwner.media.webpage instanceof TLRPC.TL_webPage))) {}
      for (i = AndroidUtilities.dp(100.0F);; i = 0)
      {
        j += i;
        i = j;
        if (isReply()) {
          i = j + AndroidUtilities.dp(42.0F);
        }
        return i;
      }
    }
    if (this.type == 2) {
      return AndroidUtilities.dp(72.0F);
    }
    if (this.type == 12) {
      return AndroidUtilities.dp(71.0F);
    }
    if (this.type == 9) {
      return AndroidUtilities.dp(100.0F);
    }
    if (this.type == 4) {
      return AndroidUtilities.dp(114.0F);
    }
    if (this.type == 14) {
      return AndroidUtilities.dp(82.0F);
    }
    if (this.type == 10) {
      return AndroidUtilities.dp(30.0F);
    }
    if (this.type == 11) {
      return AndroidUtilities.dp(50.0F);
    }
    float f1;
    Object localObject;
    int k;
    if (this.type == 13)
    {
      float f2 = AndroidUtilities.displaySize.y * 0.4F;
      if (AndroidUtilities.isTablet()) {}
      for (f1 = AndroidUtilities.getMinTabletSide() * 0.5F;; f1 = AndroidUtilities.displaySize.x * 0.5F)
      {
        j = 0;
        int m = 0;
        localObject = this.messageOwner.media.document.attributes.iterator();
        TLRPC.DocumentAttribute localDocumentAttribute;
        do
        {
          k = j;
          i = m;
          if (!((Iterator)localObject).hasNext()) {
            break;
          }
          localDocumentAttribute = (TLRPC.DocumentAttribute)((Iterator)localObject).next();
        } while (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeImageSize));
        i = localDocumentAttribute.w;
        k = localDocumentAttribute.h;
        j = i;
        if (i == 0)
        {
          k = (int)f2;
          j = k + AndroidUtilities.dp(100.0F);
        }
        i = k;
        m = j;
        if (k > f2)
        {
          m = (int)(j * (f2 / k));
          i = (int)f2;
        }
        j = i;
        if (m > f1) {
          j = (int)(i * (f1 / m));
        }
        return j + AndroidUtilities.dp(14.0F);
      }
    }
    if (AndroidUtilities.isTablet())
    {
      i = (int)(AndroidUtilities.getMinTabletSide() * 0.7F);
      k = i + AndroidUtilities.dp(100.0F);
      j = i;
      if (i > AndroidUtilities.getPhotoSize()) {
        j = AndroidUtilities.getPhotoSize();
      }
      i = k;
      if (k > AndroidUtilities.getPhotoSize()) {
        i = AndroidUtilities.getPhotoSize();
      }
      localObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
      k = i;
      if (localObject != null)
      {
        f1 = ((TLRPC.PhotoSize)localObject).w / j;
        k = (int)(((TLRPC.PhotoSize)localObject).h / f1);
        j = k;
        if (k == 0) {
          j = AndroidUtilities.dp(100.0F);
        }
        if (j <= i) {
          break label581;
        }
        label521:
        if (isSecretPhoto()) {
          if (!AndroidUtilities.isTablet()) {
            break label605;
          }
        }
      }
    }
    label581:
    label605:
    for (int i = (int)(AndroidUtilities.getMinTabletSide() * 0.5F);; i = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5F))
    {
      k = i;
      return k + AndroidUtilities.dp(14.0F);
      i = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7F);
      break;
      i = j;
      if (j >= AndroidUtilities.dp(120.0F)) {
        break label521;
      }
      i = AndroidUtilities.dp(120.0F);
      break label521;
    }
  }
  
  public long getDialogId()
  {
    return getDialogId(this.messageOwner);
  }
  
  public TLRPC.Document getDocument()
  {
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
      return this.messageOwner.media.webpage.document;
    }
    if (this.messageOwner.media != null) {
      return this.messageOwner.media.document;
    }
    return null;
  }
  
  public String getDocumentName()
  {
    if ((this.messageOwner.media != null) && (this.messageOwner.media.document != null)) {
      return FileLoader.getDocumentFileName(this.messageOwner.media.document);
    }
    return "";
  }
  
  public int getDuration()
  {
    TLRPC.Document localDocument;
    int i;
    if (this.type == 0)
    {
      localDocument = this.messageOwner.media.webpage.document;
      i = 0;
    }
    for (;;)
    {
      if (i >= localDocument.attributes.size()) {
        break label79;
      }
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)localDocument.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
      {
        return localDocumentAttribute.duration;
        localDocument = this.messageOwner.media.document;
        break;
      }
      i += 1;
    }
    label79:
    return 0;
  }
  
  public String getExtension()
  {
    Object localObject2 = getFileName();
    int i = ((String)localObject2).lastIndexOf('.');
    Object localObject1 = null;
    if (i != -1) {
      localObject1 = ((String)localObject2).substring(i + 1);
    }
    if (localObject1 != null)
    {
      localObject2 = localObject1;
      if (((String)localObject1).length() != 0) {}
    }
    else
    {
      localObject2 = this.messageOwner.media.document.mime_type;
    }
    localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = "";
    }
    return ((String)localObject1).toUpperCase();
  }
  
  public String getFileName()
  {
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
      return FileLoader.getAttachFileName(this.messageOwner.media.document);
    }
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      Object localObject = this.messageOwner.media.photo.sizes;
      if (((ArrayList)localObject).size() > 0)
      {
        localObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, AndroidUtilities.getPhotoSize());
        if (localObject != null) {
          return FileLoader.getAttachFileName((TLObject)localObject);
        }
      }
    }
    else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
    {
      return FileLoader.getAttachFileName(this.messageOwner.media.webpage.document);
    }
    return "";
  }
  
  public int getFileType()
  {
    if (isVideo()) {
      return 2;
    }
    if (isVoice()) {
      return 1;
    }
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
      return 3;
    }
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) {
      return 0;
    }
    return 4;
  }
  
  public String getForwardedName()
  {
    if (this.messageOwner.fwd_from != null)
    {
      Object localObject;
      if (this.messageOwner.fwd_from.channel_id != 0)
      {
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
        if (localObject != null) {
          return ((TLRPC.Chat)localObject).title;
        }
      }
      else if (this.messageOwner.fwd_from.from_id != 0)
      {
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
        if (localObject != null) {
          return UserObject.getUserName((TLRPC.User)localObject);
        }
      }
    }
    return null;
  }
  
  public int getId()
  {
    return this.messageOwner.id;
  }
  
  public TLRPC.InputStickerSet getInputStickerSet()
  {
    return getInputStickerSet(this.messageOwner);
  }
  
  public String getMusicAuthor()
  {
    Object localObject1;
    int i;
    if (this.type == 0)
    {
      localObject1 = this.messageOwner.media.webpage.document;
      i = 0;
    }
    for (;;)
    {
      if (i >= ((TLRPC.Document)localObject1).attributes.size()) {
        break label320;
      }
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject1).attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
      {
        if (localDocumentAttribute.voice) {
          if ((isOutOwner()) || ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.from_id == UserConfig.getClientUserId()))) {
            localObject1 = LocaleController.getString("FromYou", 2131165711);
          }
        }
        Object localObject2;
        do
        {
          return (String)localObject1;
          localObject1 = this.messageOwner.media.document;
          break;
          localObject2 = null;
          localObject1 = null;
          if ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.channel_id != 0)) {
            localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
          }
          while (localObject2 != null)
          {
            return UserObject.getUserName((TLRPC.User)localObject2);
            if ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.from_id != 0)) {
              localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
            } else if (this.messageOwner.from_id < 0) {
              localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-this.messageOwner.from_id));
            } else {
              localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
            }
          }
          if (localObject1 != null) {
            return ((TLRPC.Chat)localObject1).title;
          }
          localObject2 = localDocumentAttribute.performer;
          if (localObject2 == null) {
            break label303;
          }
          localObject1 = localObject2;
        } while (((String)localObject2).length() != 0);
        label303:
        return LocaleController.getString("AudioUnknownArtist", 2131165347);
      }
      i += 1;
    }
    label320:
    return "";
  }
  
  public String getMusicTitle()
  {
    Object localObject2;
    int i;
    if (this.type == 0)
    {
      localObject2 = this.messageOwner.media.webpage.document;
      i = 0;
    }
    for (;;)
    {
      if (i >= ((TLRPC.Document)localObject2).attributes.size()) {
        break label145;
      }
      Object localObject1 = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject2).attributes.get(i);
      if ((localObject1 instanceof TLRPC.TL_documentAttributeAudio))
      {
        if (((TLRPC.DocumentAttribute)localObject1).voice) {
          localObject1 = LocaleController.formatDateAudio(this.messageOwner.date);
        }
        label110:
        do
        {
          String str;
          do
          {
            return (String)localObject1;
            localObject2 = this.messageOwner.media.document;
            break;
            str = ((TLRPC.DocumentAttribute)localObject1).title;
            if (str == null) {
              break label110;
            }
            localObject1 = str;
          } while (str.length() != 0);
          localObject2 = FileLoader.getDocumentFileName((TLRPC.Document)localObject2);
          if (localObject2 == null) {
            break label128;
          }
          localObject1 = localObject2;
        } while (((String)localObject2).length() != 0);
        label128:
        return LocaleController.getString("AudioUnknownTitle", 2131165348);
      }
      i += 1;
    }
    label145:
    return "";
  }
  
  public String getSecretTimeString()
  {
    if (!isSecretMedia()) {
      return null;
    }
    int i = this.messageOwner.ttl;
    if (this.messageOwner.destroyTime != 0) {
      i = Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance().getCurrentTime());
    }
    if (i < 60) {
      return i + "s";
    }
    return i / 60 + "m";
  }
  
  public String getStickerEmoji()
  {
    Object localObject2 = null;
    int i = 0;
    for (;;)
    {
      Object localObject1 = localObject2;
      if (i < this.messageOwner.media.document.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)this.messageOwner.media.document.attributes.get(i);
        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
          break label87;
        }
        localObject1 = localObject2;
        if (localDocumentAttribute.alt != null)
        {
          localObject1 = localObject2;
          if (localDocumentAttribute.alt.length() > 0) {
            localObject1 = localDocumentAttribute.alt;
          }
        }
      }
      return (String)localObject1;
      label87:
      i += 1;
    }
  }
  
  public String getStrickerChar()
  {
    if ((this.messageOwner.media != null) && (this.messageOwner.media.document != null))
    {
      Iterator localIterator = this.messageOwner.media.document.attributes.iterator();
      while (localIterator.hasNext())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)localIterator.next();
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
          return localDocumentAttribute.alt;
        }
      }
    }
    return null;
  }
  
  public int getUnradFlags()
  {
    return getUnreadFlags(this.messageOwner);
  }
  
  public boolean hasPhotoStickers()
  {
    return (this.messageOwner.media != null) && (this.messageOwner.media.photo != null) && (this.messageOwner.media.photo.has_stickers);
  }
  
  public boolean isContentUnread()
  {
    return this.messageOwner.media_unread;
  }
  
  public boolean isForwarded()
  {
    return isForwardedMessage(this.messageOwner);
  }
  
  public boolean isFromUser()
  {
    return (this.messageOwner.from_id > 0) && (!this.messageOwner.post);
  }
  
  public boolean isGame()
  {
    return isGameMessage(this.messageOwner);
  }
  
  public boolean isGif()
  {
    return ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) && (isGifDocument(this.messageOwner.media.document));
  }
  
  public boolean isMask()
  {
    return isMaskMessage(this.messageOwner);
  }
  
  public boolean isMediaEmpty()
  {
    return isMediaEmpty(this.messageOwner);
  }
  
  public boolean isMegagroup()
  {
    return isMegagroup(this.messageOwner);
  }
  
  public boolean isMusic()
  {
    return isMusicMessage(this.messageOwner);
  }
  
  public boolean isNewGif()
  {
    return (this.messageOwner.media != null) && (isNewGifDocument(this.messageOwner.media.document));
  }
  
  public boolean isOut()
  {
    return this.messageOwner.out;
  }
  
  public boolean isOutOwner()
  {
    return (this.messageOwner.out) && (this.messageOwner.from_id > 0) && (!this.messageOwner.post);
  }
  
  public boolean isReply()
  {
    return ((this.replyMessageObject == null) || (!(this.replyMessageObject.messageOwner instanceof TLRPC.TL_messageEmpty))) && ((this.messageOwner.reply_to_msg_id != 0) || (this.messageOwner.reply_to_random_id != 0L)) && ((this.messageOwner.flags & 0x8) != 0);
  }
  
  public boolean isSecretMedia()
  {
    return ((this.messageOwner instanceof TLRPC.TL_message_secret)) && ((((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (this.messageOwner.ttl > 0) && (this.messageOwner.ttl <= 60)) || (isVoice()) || (isVideo()));
  }
  
  public boolean isSecretPhoto()
  {
    return ((this.messageOwner instanceof TLRPC.TL_message_secret)) && ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (this.messageOwner.ttl > 0) && (this.messageOwner.ttl <= 60);
  }
  
  public boolean isSendError()
  {
    return (this.messageOwner.send_state == 2) && (this.messageOwner.id < 0);
  }
  
  public boolean isSending()
  {
    return (this.messageOwner.send_state == 1) && (this.messageOwner.id < 0);
  }
  
  public boolean isSent()
  {
    return (this.messageOwner.send_state == 0) || (this.messageOwner.id > 0);
  }
  
  public boolean isSticker()
  {
    if (this.type != 1000) {
      return this.type == 13;
    }
    return isStickerMessage(this.messageOwner);
  }
  
  public boolean isUnread()
  {
    return this.messageOwner.unread;
  }
  
  public boolean isVideo()
  {
    return isVideoMessage(this.messageOwner);
  }
  
  public boolean isVoice()
  {
    return isVoiceMessage(this.messageOwner);
  }
  
  public boolean isWebpageDocument()
  {
    return ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (this.messageOwner.media.webpage.document != null) && (!isGifDocument(this.messageOwner.media.webpage.document));
  }
  
  public void measureInlineBotButtons()
  {
    this.wantedBotKeyboardWidth = 0;
    if (!(this.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) {}
    for (;;)
    {
      return;
      if (botButtonPaint == null)
      {
        botButtonPaint = new TextPaint(1);
        botButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      }
      botButtonPaint.setTextSize(AndroidUtilities.dp(15.0F));
      int i = 0;
      while (i < this.messageOwner.reply_markup.rows.size())
      {
        TLRPC.TL_keyboardButtonRow localTL_keyboardButtonRow = (TLRPC.TL_keyboardButtonRow)this.messageOwner.reply_markup.rows.get(i);
        int k = 0;
        int n = localTL_keyboardButtonRow.buttons.size();
        int j = 0;
        while (j < n)
        {
          StaticLayout localStaticLayout = new StaticLayout(Emoji.replaceEmoji(((TLRPC.KeyboardButton)localTL_keyboardButtonRow.buttons.get(j)).text, botButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0F), false), botButtonPaint, AndroidUtilities.dp(2000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          int m = k;
          if (localStaticLayout.getLineCount() > 0) {
            m = Math.max(k, (int)Math.ceil(localStaticLayout.getLineWidth(0) - localStaticLayout.getLineLeft(0)) + AndroidUtilities.dp(4.0F));
          }
          j += 1;
          k = m;
        }
        this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, (AndroidUtilities.dp(12.0F) + k) * n + AndroidUtilities.dp(5.0F) * (n - 1));
        i += 1;
      }
    }
  }
  
  public CharSequence replaceWithLink(CharSequence paramCharSequence, String paramString, ArrayList<Integer> paramArrayList, AbstractMap<Integer, TLRPC.User> paramAbstractMap)
  {
    Object localObject1 = paramCharSequence;
    if (TextUtils.indexOf(paramCharSequence, paramString) >= 0)
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder("");
      int i = 0;
      while (i < paramArrayList.size())
      {
        localObject1 = null;
        if (paramAbstractMap != null) {
          localObject1 = (TLRPC.User)paramAbstractMap.get(paramArrayList.get(i));
        }
        Object localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = MessagesController.getInstance().getUser((Integer)paramArrayList.get(i));
        }
        if (localObject2 != null)
        {
          localObject1 = UserObject.getUserName((TLRPC.User)localObject2);
          int j = localSpannableStringBuilder.length();
          if (localSpannableStringBuilder.length() != 0) {
            localSpannableStringBuilder.append(", ");
          }
          localSpannableStringBuilder.append((CharSequence)localObject1);
          localSpannableStringBuilder.setSpan(new URLSpanNoUnderlineBold("" + ((TLRPC.User)localObject2).id), j, ((String)localObject1).length() + j, 33);
        }
        i += 1;
      }
      localObject1 = TextUtils.replace(paramCharSequence, new String[] { paramString }, new CharSequence[] { localSpannableStringBuilder });
    }
    return (CharSequence)localObject1;
  }
  
  public CharSequence replaceWithLink(CharSequence paramCharSequence, String paramString, TLObject paramTLObject)
  {
    int i = TextUtils.indexOf(paramCharSequence, paramString);
    if (i >= 0)
    {
      String str;
      if ((paramTLObject instanceof TLRPC.User))
      {
        str = UserObject.getUserName((TLRPC.User)paramTLObject);
        paramTLObject = "" + ((TLRPC.User)paramTLObject).id;
      }
      for (;;)
      {
        paramCharSequence = new SpannableStringBuilder(TextUtils.replace(paramCharSequence, new String[] { paramString }, new String[] { str }));
        paramCharSequence.setSpan(new URLSpanNoUnderlineBold("" + paramTLObject), i, str.length() + i, 33);
        return paramCharSequence;
        if ((paramTLObject instanceof TLRPC.Chat))
        {
          str = ((TLRPC.Chat)paramTLObject).title;
          paramTLObject = "" + -((TLRPC.Chat)paramTLObject).id;
        }
        else if ((paramTLObject instanceof TLRPC.TL_game))
        {
          str = ((TLRPC.TL_game)paramTLObject).title;
          paramTLObject = "game";
        }
        else
        {
          str = "";
          paramTLObject = "0";
        }
      }
    }
    return paramCharSequence;
  }
  
  public void setContentIsRead()
  {
    this.messageOwner.media_unread = false;
  }
  
  public void setIsRead()
  {
    this.messageOwner.unread = false;
  }
  
  public void setType()
  {
    int i = this.type;
    if (((this.messageOwner instanceof TLRPC.TL_message)) || ((this.messageOwner instanceof TLRPC.TL_messageForwarded_old2))) {
      if (isMediaEmpty())
      {
        this.type = 0;
        if ((this.messageText == null) || (this.messageText.length() == 0)) {
          this.messageText = "Empty message";
        }
      }
    }
    for (;;)
    {
      if ((i != 1000) && (i != this.type)) {
        generateThumbs(false);
      }
      return;
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
      {
        this.type = 1;
      }
      else if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
      {
        this.type = 4;
      }
      else if (isVideo())
      {
        this.type = 3;
      }
      else if (isVoice())
      {
        this.type = 2;
      }
      else if (isMusic())
      {
        this.type = 14;
      }
      else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
      {
        this.type = 12;
      }
      else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaUnsupported))
      {
        this.type = 0;
      }
      else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
      {
        if ((this.messageOwner.media.document != null) && (this.messageOwner.media.document.mime_type != null))
        {
          if (isGifDocument(this.messageOwner.media.document)) {
            this.type = 8;
          } else if ((this.messageOwner.media.document.mime_type.equals("image/webp")) && (isSticker())) {
            this.type = 13;
          } else {
            this.type = 9;
          }
        }
        else {
          this.type = 9;
        }
      }
      else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
      {
        this.type = 0;
        continue;
        if ((this.messageOwner instanceof TLRPC.TL_messageService)) {
          if ((this.messageOwner.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
          {
            this.type = 0;
          }
          else if (((this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto)) || ((this.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto)))
          {
            this.contentType = 1;
            this.type = 11;
          }
          else if ((this.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction))
          {
            if (((this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) || ((this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)))
            {
              this.contentType = 1;
              this.type = 10;
            }
            else
            {
              this.contentType = -1;
              this.type = -1;
            }
          }
          else if ((this.messageOwner.action instanceof TLRPC.TL_messageActionHistoryClear))
          {
            this.contentType = -1;
            this.type = -1;
          }
          else
          {
            this.contentType = 1;
            this.type = 10;
          }
        }
      }
    }
  }
  
  public static class TextLayoutBlock
  {
    public int charactersOffset;
    public int height;
    public StaticLayout textLayout;
    public float textXOffset;
    public float textYOffset;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MessageObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */