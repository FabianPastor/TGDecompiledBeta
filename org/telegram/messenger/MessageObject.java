package org.telegram.messenger;

import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.SparseArray;
import java.io.File;
import java.security.SecureRandom;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.ChannelAdminLogEventAction;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Page;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangePhoto;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDeleteMessage;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantInvite;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoin;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantLeave;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionUpdatePinned;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionBotAllowed;
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
import org.telegram.tgnet.TLRPC.TL_messageActionCustomAction;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
import org.telegram.tgnet.TLRPC.TL_messageActionTTLChange;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityBotCommand;
import org.telegram.tgnet.TLRPC.TL_messageEntityCashtag;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC.TL_messageEntityHashtag;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityMention;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageEntityPhone;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageForwarded_old2;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument_layer68;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument_layer74;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument_old;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_layer68;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_layer74;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_old;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
import org.telegram.tgnet.TLRPC.TL_pageBlockPhoto;
import org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
import org.telegram.tgnet.TLRPC.TL_pageBlockVideo;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;

public class MessageObject
{
  private static final int LINES_PER_BLOCK = 10;
  public static final int MESSAGE_SEND_STATE_SENDING = 1;
  public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
  public static final int MESSAGE_SEND_STATE_SENT = 0;
  public static final int POSITION_FLAG_BOTTOM = 8;
  public static final int POSITION_FLAG_LEFT = 1;
  public static final int POSITION_FLAG_RIGHT = 2;
  public static final int POSITION_FLAG_TOP = 4;
  public static Pattern urlPattern;
  public boolean attachPathExists;
  public int audioPlayerDuration;
  public float audioProgress;
  public int audioProgressSec;
  public StringBuilder botButtonsLayout;
  public float bufferedProgress;
  public CharSequence caption;
  public int contentType;
  public int currentAccount;
  public TLRPC.TL_channelAdminLogEvent currentEvent;
  public String customReplyName;
  public String dateKey;
  public boolean deleted;
  public long eventId;
  public boolean forceUpdate;
  private int generatedWithMinSize;
  public float gifState;
  public boolean hasRtl;
  public boolean isDateObject;
  private int isRoundVideoCached;
  public int lastLineWidth;
  private boolean layoutCreated;
  public int linesCount;
  public CharSequence linkDescription;
  public boolean localChannel;
  public long localGroupId;
  public String localName;
  public int localType;
  public String localUserName;
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
  public float textXOffset;
  public int type = 1000;
  public boolean useCustomPhoto;
  public VideoEditedInfo videoEditedInfo;
  public boolean viewsReloaded;
  public int wantedBotKeyboardWidth;
  
  public MessageObject(int paramInt, TLRPC.Message paramMessage, SparseArray<TLRPC.User> paramSparseArray, SparseArray<TLRPC.Chat> paramSparseArray1, boolean paramBoolean)
  {
    this(paramInt, paramMessage, null, null, paramSparseArray, paramSparseArray1, paramBoolean, 0L);
  }
  
  public MessageObject(int paramInt, TLRPC.Message paramMessage, SparseArray<TLRPC.User> paramSparseArray, boolean paramBoolean)
  {
    this(paramInt, paramMessage, paramSparseArray, null, paramBoolean);
  }
  
  public MessageObject(int paramInt, TLRPC.Message paramMessage, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {}
    for (int i = 2;; i = 1)
    {
      this.localType = i;
      this.currentAccount = paramInt;
      this.localName = paramString2;
      this.localUserName = paramString3;
      this.messageText = paramString1;
      this.messageOwner = paramMessage;
      this.localChannel = paramBoolean2;
      return;
    }
  }
  
  public MessageObject(int paramInt, TLRPC.Message paramMessage, AbstractMap<Integer, TLRPC.User> paramAbstractMap, AbstractMap<Integer, TLRPC.Chat> paramAbstractMap1, SparseArray<TLRPC.User> paramSparseArray, SparseArray<TLRPC.Chat> paramSparseArray1, boolean paramBoolean, long paramLong)
  {
    Theme.createChatResources(null, true);
    this.currentAccount = paramInt;
    this.messageOwner = paramMessage;
    this.eventId = paramLong;
    if (paramMessage.replyMessage != null) {
      this.replyMessageObject = new MessageObject(paramInt, paramMessage.replyMessage, paramAbstractMap, paramAbstractMap1, paramSparseArray, paramSparseArray1, false, paramLong);
    }
    Object localObject1 = null;
    Object localObject2 = null;
    if (paramMessage.from_id > 0)
    {
      if (paramAbstractMap == null) {
        break label573;
      }
      localObject2 = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(paramMessage.from_id));
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = MessagesController.getInstance(paramInt).getUser(Integer.valueOf(paramMessage.from_id));
      }
    }
    label166:
    int i;
    int j;
    if ((paramMessage instanceof TLRPC.TL_messageService))
    {
      localObject2 = localObject1;
      if (paramMessage.action != null)
      {
        if ((paramMessage.action instanceof TLRPC.TL_messageActionCustomAction))
        {
          this.messageText = paramMessage.action.message;
          localObject2 = localObject1;
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
        paramInt = paramMessage.get(1);
        j = paramMessage.get(2);
        this.dateKey = String.format("%d_%02d_%02d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(j), Integer.valueOf(i) });
        this.monthKey = String.format("%d_%02d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(j) });
        if ((this.messageOwner.message != null) && (this.messageOwner.id < 0) && (this.messageOwner.params != null))
        {
          paramMessage = (String)this.messageOwner.params.get("ve");
          if ((paramMessage != null) && ((isVideo()) || (isNewGif()) || (isRoundVideo())))
          {
            this.videoEditedInfo = new VideoEditedInfo();
            if (this.videoEditedInfo.parseString(paramMessage)) {
              break label4240;
            }
            this.videoEditedInfo = null;
          }
        }
        label394:
        generateCaption();
        if (!paramBoolean) {
          break label4300;
        }
        if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {
          break label4254;
        }
        paramMessage = Theme.chat_msgGameTextPaint;
        label420:
        if (!SharedConfig.allowBigEmoji) {
          break label4261;
        }
        paramAbstractMap = new int[1];
        label430:
        this.messageText = Emoji.replaceEmoji(this.messageText, paramMessage.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false, paramAbstractMap);
        if ((paramAbstractMap == null) || (paramAbstractMap[0] < 1) || (paramAbstractMap[0] > 3)) {
          break label4294;
        }
        switch (paramAbstractMap[0])
        {
        default: 
          paramMessage = Theme.chat_msgTextPaintThreeEmoji;
          paramInt = AndroidUtilities.dp(24.0F);
        }
      }
    }
    for (;;)
    {
      paramAbstractMap = (Emoji.EmojiSpan[])((Spannable)this.messageText).getSpans(0, this.messageText.length(), Emoji.EmojiSpan.class);
      if ((paramAbstractMap == null) || (paramAbstractMap.length <= 0)) {
        break label4294;
      }
      for (j = 0; j < paramAbstractMap.length; j++) {
        paramAbstractMap[j].replaceFontMetrics(paramMessage.getFontMetricsInt(), paramInt);
      }
      label573:
      if (paramSparseArray == null) {
        break;
      }
      localObject2 = (TLRPC.User)paramSparseArray.get(paramMessage.from_id);
      break;
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatCreate))
      {
        if (isOut())
        {
          this.messageText = LocaleController.getString("ActionYouCreateGroup", NUM);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", NUM), "un1", (TLObject)localObject1);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatDeleteUser))
      {
        if (paramMessage.action.user_id == paramMessage.from_id)
        {
          if (isOut())
          {
            this.messageText = LocaleController.getString("ActionYouLeftUser", NUM);
            localObject2 = localObject1;
            break label166;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", NUM), "un1", (TLObject)localObject1);
          localObject2 = localObject1;
          break label166;
        }
        paramAbstractMap1 = null;
        if (paramAbstractMap != null) {
          paramAbstractMap = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(paramMessage.action.user_id));
        }
        for (;;)
        {
          paramAbstractMap1 = paramAbstractMap;
          if (paramAbstractMap == null) {
            paramAbstractMap1 = MessagesController.getInstance(paramInt).getUser(Integer.valueOf(paramMessage.action.user_id));
          }
          if (!isOut()) {
            break label855;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", NUM), "un2", paramAbstractMap1);
          localObject2 = localObject1;
          break;
          paramAbstractMap = paramAbstractMap1;
          if (paramSparseArray != null) {
            paramAbstractMap = (TLRPC.User)paramSparseArray.get(paramMessage.action.user_id);
          }
        }
        label855:
        if (paramMessage.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId())
        {
          this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", NUM), "un1", (TLObject)localObject1);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", NUM), "un2", paramAbstractMap1);
        this.messageText = replaceWithLink(this.messageText, "un1", (TLObject)localObject1);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatAddUser))
      {
        i = this.messageOwner.action.user_id;
        j = i;
        if (i == 0)
        {
          j = i;
          if (this.messageOwner.action.users.size() == 1) {
            j = ((Integer)this.messageOwner.action.users.get(0)).intValue();
          }
        }
        if (j != 0)
        {
          paramAbstractMap1 = null;
          if (paramAbstractMap != null) {
            paramAbstractMap = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(j));
          }
          for (;;)
          {
            paramAbstractMap1 = paramAbstractMap;
            if (paramAbstractMap == null) {
              paramAbstractMap1 = MessagesController.getInstance(paramInt).getUser(Integer.valueOf(j));
            }
            if (j != paramMessage.from_id) {
              break label1275;
            }
            if ((paramMessage.to_id.channel_id == 0) || (isMegagroup())) {
              break label1138;
            }
            this.messageText = LocaleController.getString("ChannelJoined", NUM);
            localObject2 = localObject1;
            break;
            paramAbstractMap = paramAbstractMap1;
            if (paramSparseArray != null) {
              paramAbstractMap = (TLRPC.User)paramSparseArray.get(j);
            }
          }
          label1138:
          if ((paramMessage.to_id.channel_id != 0) && (isMegagroup()))
          {
            if (j == UserConfig.getInstance(this.currentAccount).getClientUserId())
            {
              this.messageText = LocaleController.getString("ChannelMegaJoined", NUM);
              localObject2 = localObject1;
              break label166;
            }
            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", NUM), "un1", (TLObject)localObject1);
            localObject2 = localObject1;
            break label166;
          }
          if (isOut())
          {
            this.messageText = LocaleController.getString("ActionAddUserSelfYou", NUM);
            localObject2 = localObject1;
            break label166;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", NUM), "un1", (TLObject)localObject1);
          localObject2 = localObject1;
          break label166;
          label1275:
          if (isOut())
          {
            this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", NUM), "un2", paramAbstractMap1);
            localObject2 = localObject1;
            break label166;
          }
          if (j == UserConfig.getInstance(this.currentAccount).getClientUserId())
          {
            if (paramMessage.to_id.channel_id != 0)
            {
              if (isMegagroup())
              {
                this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", NUM), "un1", (TLObject)localObject1);
                localObject2 = localObject1;
                break label166;
              }
              this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", NUM), "un1", (TLObject)localObject1);
              localObject2 = localObject1;
              break label166;
            }
            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", NUM), "un1", (TLObject)localObject1);
            localObject2 = localObject1;
            break label166;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", NUM), "un2", paramAbstractMap1);
          this.messageText = replaceWithLink(this.messageText, "un1", (TLObject)localObject1);
          localObject2 = localObject1;
          break label166;
        }
        if (isOut())
        {
          this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", NUM), "un2", paramMessage.action.users, paramAbstractMap, paramSparseArray);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", NUM), "un2", paramMessage.action.users, paramAbstractMap, paramSparseArray);
        this.messageText = replaceWithLink(this.messageText, "un1", (TLObject)localObject1);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatJoinedByLink))
      {
        if (isOut())
        {
          this.messageText = LocaleController.getString("ActionInviteYou", NUM);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", NUM), "un1", (TLObject)localObject1);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatEditPhoto))
      {
        if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
        {
          this.messageText = LocaleController.getString("ActionChannelChangedPhoto", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if (isOut())
        {
          this.messageText = LocaleController.getString("ActionYouChangedPhoto", NUM);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", NUM), "un1", (TLObject)localObject1);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatEditTitle))
      {
        if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
        {
          this.messageText = LocaleController.getString("ActionChannelChangedTitle", NUM).replace("un2", paramMessage.action.title);
          localObject2 = localObject1;
          break label166;
        }
        if (isOut())
        {
          this.messageText = LocaleController.getString("ActionYouChangedTitle", NUM).replace("un2", paramMessage.action.title);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", NUM).replace("un2", paramMessage.action.title), "un1", (TLObject)localObject1);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatDeletePhoto))
      {
        if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
        {
          this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if (isOut())
        {
          this.messageText = LocaleController.getString("ActionYouRemovedPhoto", NUM);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", NUM), "un1", (TLObject)localObject1);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionTTLChange))
      {
        if (paramMessage.action.ttl != 0)
        {
          if (isOut())
          {
            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", NUM, new Object[] { LocaleController.formatTTLString(paramMessage.action.ttl) });
            localObject2 = localObject1;
            break label166;
          }
          this.messageText = LocaleController.formatString("MessageLifetimeChanged", NUM, new Object[] { UserObject.getFirstName((TLRPC.User)localObject1), LocaleController.formatTTLString(paramMessage.action.ttl) });
          localObject2 = localObject1;
          break label166;
        }
        if (isOut())
        {
          this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", NUM);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = LocaleController.formatString("MessageLifetimeRemoved", NUM, new Object[] { UserObject.getFirstName((TLRPC.User)localObject1) });
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
      {
        paramLong = paramMessage.date * 1000L;
        if ((LocaleController.getInstance().formatterDay != null) && (LocaleController.getInstance().formatterYear != null))
        {
          paramSparseArray1 = LocaleController.formatString("formatDateAtTime", NUM, new Object[] { LocaleController.getInstance().formatterYear.format(paramLong), LocaleController.getInstance().formatterDay.format(paramLong) });
          label2238:
          localObject2 = UserConfig.getInstance(this.currentAccount).getCurrentUser();
          paramAbstractMap1 = (AbstractMap<Integer, TLRPC.Chat>)localObject2;
          if (localObject2 == null)
          {
            if (paramAbstractMap == null) {
              break label2404;
            }
            paramAbstractMap = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(this.messageOwner.to_id.user_id));
            label2284:
            paramAbstractMap1 = paramAbstractMap;
            if (paramAbstractMap == null) {
              paramAbstractMap1 = MessagesController.getInstance(paramInt).getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
            }
          }
          if (paramAbstractMap1 == null) {
            break label2434;
          }
        }
        label2404:
        label2434:
        for (paramAbstractMap = UserObject.getFirstName(paramAbstractMap1);; paramAbstractMap = "")
        {
          this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", NUM, new Object[] { paramAbstractMap, paramSparseArray1, paramMessage.action.title, paramMessage.action.address });
          localObject2 = localObject1;
          break;
          paramSparseArray1 = "" + paramMessage.date;
          break label2238;
          paramAbstractMap = (AbstractMap<Integer, TLRPC.User>)localObject2;
          if (paramSparseArray == null) {
            break label2284;
          }
          paramAbstractMap = (TLRPC.User)paramSparseArray.get(this.messageOwner.to_id.user_id);
          break label2284;
        }
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionUserJoined))
      {
        this.messageText = LocaleController.formatString("NotificationContactJoined", NUM, new Object[] { UserObject.getUserName((TLRPC.User)localObject1) });
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto))
      {
        this.messageText = LocaleController.formatString("NotificationContactNewPhoto", NUM, new Object[] { UserObject.getUserName((TLRPC.User)localObject1) });
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageEncryptedAction))
      {
        if ((paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages))
        {
          if (isOut())
          {
            this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", NUM, new Object[0]);
            localObject2 = localObject1;
            break label166;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", NUM), "un1", (TLObject)localObject1);
          localObject2 = localObject1;
          break label166;
        }
        localObject2 = localObject1;
        if (!(paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
          break label166;
        }
        paramMessage = (TLRPC.TL_decryptedMessageActionSetMessageTTL)paramMessage.action.encryptedAction;
        if (paramMessage.ttl_seconds != 0)
        {
          if (isOut())
          {
            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", NUM, new Object[] { LocaleController.formatTTLString(paramMessage.ttl_seconds) });
            localObject2 = localObject1;
            break label166;
          }
          this.messageText = LocaleController.formatString("MessageLifetimeChanged", NUM, new Object[] { UserObject.getFirstName((TLRPC.User)localObject1), LocaleController.formatTTLString(paramMessage.ttl_seconds) });
          localObject2 = localObject1;
          break label166;
        }
        if (isOut())
        {
          this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", NUM);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = LocaleController.formatString("MessageLifetimeRemoved", NUM, new Object[] { UserObject.getFirstName((TLRPC.User)localObject1) });
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionScreenshotTaken))
      {
        if (isOut())
        {
          this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", NUM, new Object[0]);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", NUM), "un1", (TLObject)localObject1);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionCreatedBroadcastList))
      {
        this.messageText = LocaleController.formatString("YouCreatedBroadcastList", NUM, new Object[0]);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChannelCreate))
      {
        if (isMegagroup())
        {
          this.messageText = LocaleController.getString("ActionCreateMega", NUM);
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = LocaleController.getString("ActionCreateChannel", NUM);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChatMigrateTo))
      {
        this.messageText = LocaleController.getString("ActionMigrateFromGroup", NUM);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionChannelMigrateFrom))
      {
        this.messageText = LocaleController.getString("ActionMigrateFromGroup", NUM);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionPinMessage))
      {
        if (localObject1 == null) {
          if (paramAbstractMap1 != null) {
            paramMessage = (TLRPC.Chat)paramAbstractMap1.get(Integer.valueOf(paramMessage.to_id.channel_id));
          }
        }
        for (;;)
        {
          generatePinMessageText((TLRPC.User)localObject1, paramMessage);
          localObject2 = localObject1;
          break;
          if (paramSparseArray1 != null)
          {
            paramMessage = (TLRPC.Chat)paramSparseArray1.get(paramMessage.to_id.channel_id);
          }
          else
          {
            paramMessage = null;
            continue;
            paramMessage = null;
          }
        }
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionHistoryClear))
      {
        this.messageText = LocaleController.getString("HistoryCleared", NUM);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionGameScore))
      {
        generateGameMessageText((TLRPC.User)localObject1);
        localObject2 = localObject1;
        break label166;
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionPhoneCall))
      {
        paramMessage = (TLRPC.TL_messageActionPhoneCall)this.messageOwner.action;
        boolean bool = paramMessage.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed;
        if (this.messageOwner.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
          if (bool) {
            this.messageText = LocaleController.getString("CallMessageOutgoingMissed", NUM);
          }
        }
        for (;;)
        {
          localObject2 = localObject1;
          if (paramMessage.duration <= 0) {
            break;
          }
          paramAbstractMap = LocaleController.formatCallDuration(paramMessage.duration);
          this.messageText = LocaleController.formatString("CallMessageWithDuration", NUM, new Object[] { this.messageText, paramAbstractMap });
          paramMessage = this.messageText.toString();
          j = paramMessage.indexOf(paramAbstractMap);
          localObject2 = localObject1;
          if (j == -1) {
            break;
          }
          paramAbstractMap1 = new SpannableString(this.messageText);
          i = j + paramAbstractMap.length();
          paramInt = j;
          if (j > 0)
          {
            paramInt = j;
            if (paramMessage.charAt(j - 1) == '(') {
              paramInt = j - 1;
            }
          }
          j = i;
          if (i < paramMessage.length())
          {
            j = i;
            if (paramMessage.charAt(i) == ')') {
              j = i + 1;
            }
          }
          paramAbstractMap1.setSpan(new TypefaceSpan(Typeface.DEFAULT), paramInt, j, 0);
          this.messageText = paramAbstractMap1;
          localObject2 = localObject1;
          break;
          this.messageText = LocaleController.getString("CallMessageOutgoing", NUM);
          continue;
          if (bool) {
            this.messageText = LocaleController.getString("CallMessageIncomingMissed", NUM);
          } else if ((paramMessage.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy)) {
            this.messageText = LocaleController.getString("CallMessageIncomingDeclined", NUM);
          } else {
            this.messageText = LocaleController.getString("CallMessageIncoming", NUM);
          }
        }
      }
      if ((paramMessage.action instanceof TLRPC.TL_messageActionPaymentSent))
      {
        j = (int)getDialogId();
        if (paramAbstractMap != null) {
          localObject1 = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(j));
        }
        for (;;)
        {
          paramMessage = (TLRPC.Message)localObject1;
          if (localObject1 == null) {
            paramMessage = MessagesController.getInstance(paramInt).getUser(Integer.valueOf(j));
          }
          generatePaymentSentMessageText(null);
          localObject2 = paramMessage;
          break;
          if (paramSparseArray != null) {
            localObject1 = (TLRPC.User)paramSparseArray.get(j);
          }
        }
      }
      localObject2 = localObject1;
      if (!(paramMessage.action instanceof TLRPC.TL_messageActionBotAllowed)) {
        break label166;
      }
      paramMessage = ((TLRPC.TL_messageActionBotAllowed)paramMessage.action).domain;
      paramAbstractMap = LocaleController.getString("ActionBotAllowed", NUM);
      paramInt = paramAbstractMap.indexOf("%1$s");
      paramAbstractMap = new SpannableString(String.format(paramAbstractMap, new Object[] { paramMessage }));
      if (paramInt >= 0) {
        paramAbstractMap.setSpan(new URLSpanNoUnderlineBold("http://" + paramMessage), paramInt, paramMessage.length() + paramInt, 33);
      }
      this.messageText = paramAbstractMap;
      localObject2 = localObject1;
      break label166;
      if (!isMediaEmpty())
      {
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
        {
          this.messageText = LocaleController.getString("AttachPhoto", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if ((isVideo()) || (((paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) && ((paramMessage.media.document instanceof TLRPC.TL_documentEmpty)) && (paramMessage.media.ttl_seconds != 0)))
        {
          this.messageText = LocaleController.getString("AttachVideo", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if (isVoice())
        {
          this.messageText = LocaleController.getString("AttachAudio", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if (isRoundVideo())
        {
          this.messageText = LocaleController.getString("AttachRound", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if (((paramMessage.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessage.media instanceof TLRPC.TL_messageMediaVenue)))
        {
          this.messageText = LocaleController.getString("AttachLocation", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaGeoLive))
        {
          this.messageText = LocaleController.getString("AttachLiveLocation", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaContact))
        {
          this.messageText = LocaleController.getString("AttachContact", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaGame))
        {
          this.messageText = paramMessage.message;
          localObject2 = localObject1;
          break label166;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaInvoice))
        {
          this.messageText = paramMessage.media.description;
          localObject2 = localObject1;
          break label166;
        }
        if ((paramMessage.media instanceof TLRPC.TL_messageMediaUnsupported))
        {
          this.messageText = LocaleController.getString("UnsupportedMedia", NUM);
          localObject2 = localObject1;
          break label166;
        }
        localObject2 = localObject1;
        if (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) {
          break label166;
        }
        if (isSticker())
        {
          paramMessage = getStrickerChar();
          if ((paramMessage != null) && (paramMessage.length() > 0))
          {
            this.messageText = String.format("%s %s", new Object[] { paramMessage, LocaleController.getString("AttachSticker", NUM) });
            localObject2 = localObject1;
            break label166;
          }
          this.messageText = LocaleController.getString("AttachSticker", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if (isMusic())
        {
          this.messageText = LocaleController.getString("AttachMusic", NUM);
          localObject2 = localObject1;
          break label166;
        }
        if (isGif())
        {
          this.messageText = LocaleController.getString("AttachGif", NUM);
          localObject2 = localObject1;
          break label166;
        }
        paramMessage = FileLoader.getDocumentFileName(paramMessage.media.document);
        if ((paramMessage != null) && (paramMessage.length() > 0))
        {
          this.messageText = paramMessage;
          localObject2 = localObject1;
          break label166;
        }
        this.messageText = LocaleController.getString("AttachDocument", NUM);
        localObject2 = localObject1;
        break label166;
      }
      this.messageText = paramMessage.message;
      localObject2 = localObject1;
      break label166;
      label4240:
      this.videoEditedInfo.roundVideo = isRoundVideo();
      break label394;
      label4254:
      paramMessage = Theme.chat_msgTextPaint;
      break label420;
      label4261:
      paramAbstractMap = null;
      break label430;
      paramMessage = Theme.chat_msgTextPaintOneEmoji;
      paramInt = AndroidUtilities.dp(32.0F);
      continue;
      paramMessage = Theme.chat_msgTextPaintTwoEmoji;
      paramInt = AndroidUtilities.dp(28.0F);
    }
    label4294:
    generateLayout((TLRPC.User)localObject2);
    label4300:
    this.layoutCreated = paramBoolean;
    generateThumbs(false);
    checkMediaExistance();
  }
  
  public MessageObject(int paramInt, TLRPC.Message paramMessage, AbstractMap<Integer, TLRPC.User> paramAbstractMap, AbstractMap<Integer, TLRPC.Chat> paramAbstractMap1, boolean paramBoolean)
  {
    this(paramInt, paramMessage, paramAbstractMap, paramAbstractMap1, paramBoolean, 0L);
  }
  
  public MessageObject(int paramInt, TLRPC.Message paramMessage, AbstractMap<Integer, TLRPC.User> paramAbstractMap, AbstractMap<Integer, TLRPC.Chat> paramAbstractMap1, boolean paramBoolean, long paramLong)
  {
    this(paramInt, paramMessage, paramAbstractMap, paramAbstractMap1, null, null, paramBoolean, paramLong);
  }
  
  public MessageObject(int paramInt, TLRPC.Message paramMessage, AbstractMap<Integer, TLRPC.User> paramAbstractMap, boolean paramBoolean)
  {
    this(paramInt, paramMessage, paramAbstractMap, null, paramBoolean);
  }
  
  public MessageObject(int paramInt, TLRPC.Message paramMessage, boolean paramBoolean)
  {
    this(paramInt, paramMessage, null, null, null, null, paramBoolean, 0L);
  }
  
  public MessageObject(int paramInt, TLRPC.TL_channelAdminLogEvent paramTL_channelAdminLogEvent, ArrayList<MessageObject> paramArrayList, HashMap<String, ArrayList<MessageObject>> paramHashMap, TLRPC.Chat paramChat, int[] paramArrayOfInt)
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (paramTL_channelAdminLogEvent.user_id > 0)
    {
      localObject2 = localObject1;
      if (0 == 0) {
        localObject2 = MessagesController.getInstance(paramInt).getUser(Integer.valueOf(paramTL_channelAdminLogEvent.user_id));
      }
    }
    this.currentEvent = paramTL_channelAdminLogEvent;
    localObject1 = new GregorianCalendar();
    ((Calendar)localObject1).setTimeInMillis(paramTL_channelAdminLogEvent.date * 1000L);
    int i = ((Calendar)localObject1).get(6);
    int j = ((Calendar)localObject1).get(1);
    int k = ((Calendar)localObject1).get(2);
    this.dateKey = String.format("%d_%02d_%02d", new Object[] { Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(i) });
    this.monthKey = String.format("%d_%02d", new Object[] { Integer.valueOf(j), Integer.valueOf(k) });
    Object localObject3 = new TLRPC.TL_peerChannel();
    ((TLRPC.Peer)localObject3).channel_id = paramChat.id;
    Object localObject4 = null;
    Object localObject5;
    if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangeTitle))
    {
      localObject1 = ((TLRPC.TL_channelAdminLogEventActionChangeTitle)paramTL_channelAdminLogEvent.action).new_value;
      if (paramChat.megagroup)
      {
        this.messageText = replaceWithLink(LocaleController.formatString("EventLogEditedGroupTitle", NUM, new Object[] { localObject1 }), "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        if (this.messageOwner == null) {
          this.messageOwner = new TLRPC.TL_messageService();
        }
        this.messageOwner.message = this.messageText.toString();
        this.messageOwner.from_id = paramTL_channelAdminLogEvent.user_id;
        this.messageOwner.date = paramTL_channelAdminLogEvent.date;
        localObject3 = this.messageOwner;
        k = paramArrayOfInt[0];
        paramArrayOfInt[0] = (k + 1);
        ((TLRPC.Message)localObject3).id = k;
        this.eventId = paramTL_channelAdminLogEvent.id;
        this.messageOwner.out = false;
        this.messageOwner.to_id = new TLRPC.TL_peerChannel();
        this.messageOwner.to_id.channel_id = paramChat.id;
        this.messageOwner.unread = false;
        if (paramChat.megagroup)
        {
          localObject3 = this.messageOwner;
          ((TLRPC.Message)localObject3).flags |= 0x80000000;
        }
        localObject5 = MediaController.getInstance();
        localObject3 = localObject1;
        if (paramTL_channelAdminLogEvent.action.message != null)
        {
          localObject3 = localObject1;
          if (!(paramTL_channelAdminLogEvent.action.message instanceof TLRPC.TL_messageEmpty)) {
            localObject3 = paramTL_channelAdminLogEvent.action.message;
          }
        }
        if (localObject3 != null)
        {
          ((TLRPC.Message)localObject3).out = false;
          k = paramArrayOfInt[0];
          paramArrayOfInt[0] = (k + 1);
          ((TLRPC.Message)localObject3).id = k;
          ((TLRPC.Message)localObject3).reply_to_msg_id = 0;
          ((TLRPC.Message)localObject3).flags &= 0xFFFF7FFF;
          if (paramChat.megagroup) {
            ((TLRPC.Message)localObject3).flags |= 0x80000000;
          }
          paramArrayOfInt = new MessageObject(paramInt, (TLRPC.Message)localObject3, null, null, true, this.eventId);
          if (paramArrayOfInt.contentType < 0) {
            break label5306;
          }
          if (((MediaController)localObject5).isPlayingMessage(paramArrayOfInt))
          {
            paramChat = ((MediaController)localObject5).getPlayingMessageObject();
            paramArrayOfInt.audioProgress = paramChat.audioProgress;
            paramArrayOfInt.audioProgressSec = paramChat.audioProgressSec;
          }
          createDateArray(paramInt, paramTL_channelAdminLogEvent, paramArrayList, paramHashMap);
          paramArrayList.add(paramArrayList.size() - 1, paramArrayOfInt);
        }
        label623:
        if (this.contentType < 0) {
          return;
        }
        createDateArray(paramInt, paramTL_channelAdminLogEvent, paramArrayList, paramHashMap);
        paramArrayList.add(paramArrayList.size() - 1, this);
        if (this.messageText == null) {
          this.messageText = "";
        }
        setType();
        measureInlineBotButtons();
        if ((this.messageOwner.message != null) && (this.messageOwner.id < 0) && (this.messageOwner.message.length() > 6) && ((isVideo()) || (isNewGif()) || (isRoundVideo())))
        {
          this.videoEditedInfo = new VideoEditedInfo();
          if (this.videoEditedInfo.parseString(this.messageOwner.message)) {
            break label5314;
          }
          this.videoEditedInfo = null;
        }
        label760:
        generateCaption();
        if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {
          break label5328;
        }
        paramTL_channelAdminLogEvent = Theme.chat_msgGameTextPaint;
        label781:
        if (!SharedConfig.allowBigEmoji) {
          break label5335;
        }
        paramArrayList = new int[1];
        label791:
        this.messageText = Emoji.replaceEmoji(this.messageText, paramTL_channelAdminLogEvent.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false, paramArrayList);
        if ((paramArrayList == null) || (paramArrayList[0] < 1) || (paramArrayList[0] > 3)) {
          break label5368;
        }
        switch (paramArrayList[0])
        {
        default: 
          paramTL_channelAdminLogEvent = Theme.chat_msgTextPaintThreeEmoji;
          paramInt = AndroidUtilities.dp(24.0F);
        }
      }
    }
    for (;;)
    {
      paramArrayList = (Emoji.EmojiSpan[])((Spannable)this.messageText).getSpans(0, this.messageText.length(), Emoji.EmojiSpan.class);
      if ((paramArrayList == null) || (paramArrayList.length <= 0)) {
        break label5368;
      }
      for (k = 0; k < paramArrayList.length; k++) {
        paramArrayList[k].replaceFontMetrics(paramTL_channelAdminLogEvent.getFontMetricsInt(), paramInt);
      }
      this.messageText = replaceWithLink(LocaleController.formatString("EventLogEditedChannelTitle", NUM, new Object[] { localObject1 }), "un1", (TLObject)localObject2);
      localObject1 = localObject4;
      break;
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangePhoto))
      {
        this.messageOwner = new TLRPC.TL_messageService();
        if ((paramTL_channelAdminLogEvent.action.new_photo instanceof TLRPC.TL_chatPhotoEmpty))
        {
          this.messageOwner.action = new TLRPC.TL_messageActionChatDeletePhoto();
          if (paramChat.megagroup)
          {
            this.messageText = replaceWithLink(LocaleController.getString("EventLogRemovedWGroupPhoto", NUM), "un1", (TLObject)localObject2);
            localObject1 = localObject4;
            break;
          }
          this.messageText = replaceWithLink(LocaleController.getString("EventLogRemovedChannelPhoto", NUM), "un1", (TLObject)localObject2);
          localObject1 = localObject4;
          break;
        }
        this.messageOwner.action = new TLRPC.TL_messageActionChatEditPhoto();
        this.messageOwner.action.photo = new TLRPC.TL_photo();
        localObject1 = new TLRPC.TL_photoSize();
        ((TLRPC.TL_photoSize)localObject1).location = paramTL_channelAdminLogEvent.action.new_photo.photo_small;
        ((TLRPC.TL_photoSize)localObject1).type = "s";
        ((TLRPC.TL_photoSize)localObject1).h = 80;
        ((TLRPC.TL_photoSize)localObject1).w = 80;
        this.messageOwner.action.photo.sizes.add(localObject1);
        localObject1 = new TLRPC.TL_photoSize();
        ((TLRPC.TL_photoSize)localObject1).location = paramTL_channelAdminLogEvent.action.new_photo.photo_big;
        ((TLRPC.TL_photoSize)localObject1).type = "m";
        ((TLRPC.TL_photoSize)localObject1).h = 640;
        ((TLRPC.TL_photoSize)localObject1).w = 640;
        this.messageOwner.action.photo.sizes.add(localObject1);
        if (paramChat.megagroup)
        {
          this.messageText = replaceWithLink(LocaleController.getString("EventLogEditedGroupPhoto", NUM), "un1", (TLObject)localObject2);
          localObject1 = localObject4;
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("EventLogEditedChannelPhoto", NUM), "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        break;
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantJoin))
      {
        if (paramChat.megagroup)
        {
          this.messageText = replaceWithLink(LocaleController.getString("EventLogGroupJoined", NUM), "un1", (TLObject)localObject2);
          localObject1 = localObject4;
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("EventLogChannelJoined", NUM), "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        break;
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantLeave))
      {
        this.messageOwner = new TLRPC.TL_messageService();
        this.messageOwner.action = new TLRPC.TL_messageActionChatDeleteUser();
        this.messageOwner.action.user_id = paramTL_channelAdminLogEvent.user_id;
        if (paramChat.megagroup)
        {
          this.messageText = replaceWithLink(LocaleController.getString("EventLogLeftGroup", NUM), "un1", (TLObject)localObject2);
          localObject1 = localObject4;
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("EventLogLeftChannel", NUM), "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        break;
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantInvite))
      {
        this.messageOwner = new TLRPC.TL_messageService();
        this.messageOwner.action = new TLRPC.TL_messageActionChatAddUser();
        localObject1 = MessagesController.getInstance(paramInt).getUser(Integer.valueOf(paramTL_channelAdminLogEvent.action.participant.user_id));
        if (paramTL_channelAdminLogEvent.action.participant.user_id == this.messageOwner.from_id)
        {
          if (paramChat.megagroup)
          {
            this.messageText = replaceWithLink(LocaleController.getString("EventLogGroupJoined", NUM), "un1", (TLObject)localObject2);
            localObject1 = localObject4;
            break;
          }
          this.messageText = replaceWithLink(LocaleController.getString("EventLogChannelJoined", NUM), "un1", (TLObject)localObject2);
          localObject1 = localObject4;
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("EventLogAdded", NUM), "un2", (TLObject)localObject1);
        this.messageText = replaceWithLink(this.messageText, "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        break;
      }
      Object localObject6;
      int m;
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin))
      {
        this.messageOwner = new TLRPC.TL_message();
        localObject3 = MessagesController.getInstance(paramInt).getUser(Integer.valueOf(paramTL_channelAdminLogEvent.action.prev_participant.user_id));
        localObject1 = LocaleController.getString("EventLogPromoted", NUM);
        k = ((String)localObject1).indexOf("%1$s");
        localObject6 = new StringBuilder(String.format((String)localObject1, new Object[] { getUserName((TLRPC.User)localObject3, this.messageOwner.entities, k) }));
        ((StringBuilder)localObject6).append("\n");
        localObject3 = paramTL_channelAdminLogEvent.action.prev_participant.admin_rights;
        localObject5 = paramTL_channelAdminLogEvent.action.new_participant.admin_rights;
        localObject1 = localObject3;
        if (localObject3 == null) {
          localObject1 = new TLRPC.TL_channelAdminRights();
        }
        localObject3 = localObject5;
        if (localObject5 == null) {
          localObject3 = new TLRPC.TL_channelAdminRights();
        }
        if (((TLRPC.TL_channelAdminRights)localObject1).change_info != ((TLRPC.TL_channelAdminRights)localObject3).change_info)
        {
          localObject5 = ((StringBuilder)localObject6).append('\n');
          if (((TLRPC.TL_channelAdminRights)localObject3).change_info)
          {
            k = 43;
            m = k;
            label1901:
            ((StringBuilder)localObject5).append(m).append(' ');
            if (!paramChat.megagroup) {
              break label2454;
            }
            localObject5 = LocaleController.getString("EventLogPromotedChangeGroupInfo", NUM);
            label1933:
            ((StringBuilder)localObject6).append((String)localObject5);
          }
        }
        else
        {
          if (!paramChat.megagroup)
          {
            if (((TLRPC.TL_channelAdminRights)localObject1).post_messages != ((TLRPC.TL_channelAdminRights)localObject3).post_messages)
            {
              localObject5 = ((StringBuilder)localObject6).append('\n');
              if (!((TLRPC.TL_channelAdminRights)localObject3).post_messages) {
                break label2468;
              }
              k = 43;
              m = k;
              label1987:
              ((StringBuilder)localObject5).append(m).append(' ');
              ((StringBuilder)localObject6).append(LocaleController.getString("EventLogPromotedPostMessages", NUM));
            }
            if (((TLRPC.TL_channelAdminRights)localObject1).edit_messages != ((TLRPC.TL_channelAdminRights)localObject3).edit_messages)
            {
              localObject5 = ((StringBuilder)localObject6).append('\n');
              if (!((TLRPC.TL_channelAdminRights)localObject3).edit_messages) {
                break label2479;
              }
              k = 43;
              m = k;
              label2053:
              ((StringBuilder)localObject5).append(m).append(' ');
              ((StringBuilder)localObject6).append(LocaleController.getString("EventLogPromotedEditMessages", NUM));
            }
          }
          if (((TLRPC.TL_channelAdminRights)localObject1).delete_messages != ((TLRPC.TL_channelAdminRights)localObject3).delete_messages)
          {
            localObject5 = ((StringBuilder)localObject6).append('\n');
            if (!((TLRPC.TL_channelAdminRights)localObject3).delete_messages) {
              break label2490;
            }
            k = 43;
            m = k;
            label2119:
            ((StringBuilder)localObject5).append(m).append(' ');
            ((StringBuilder)localObject6).append(LocaleController.getString("EventLogPromotedDeleteMessages", NUM));
          }
          if (((TLRPC.TL_channelAdminRights)localObject1).add_admins != ((TLRPC.TL_channelAdminRights)localObject3).add_admins)
          {
            localObject5 = ((StringBuilder)localObject6).append('\n');
            if (!((TLRPC.TL_channelAdminRights)localObject3).add_admins) {
              break label2501;
            }
            k = 43;
            m = k;
            label2185:
            ((StringBuilder)localObject5).append(m).append(' ');
            ((StringBuilder)localObject6).append(LocaleController.getString("EventLogPromotedAddAdmins", NUM));
          }
          if ((paramChat.megagroup) && (((TLRPC.TL_channelAdminRights)localObject1).ban_users != ((TLRPC.TL_channelAdminRights)localObject3).ban_users))
          {
            localObject5 = ((StringBuilder)localObject6).append('\n');
            if (!((TLRPC.TL_channelAdminRights)localObject3).ban_users) {
              break label2512;
            }
            k = 43;
            m = k;
            label2259:
            ((StringBuilder)localObject5).append(m).append(' ');
            ((StringBuilder)localObject6).append(LocaleController.getString("EventLogPromotedBanUsers", NUM));
          }
          if (((TLRPC.TL_channelAdminRights)localObject1).invite_users != ((TLRPC.TL_channelAdminRights)localObject3).invite_users)
          {
            localObject5 = ((StringBuilder)localObject6).append('\n');
            if (!((TLRPC.TL_channelAdminRights)localObject3).invite_users) {
              break label2523;
            }
            k = 43;
            m = k;
            label2325:
            ((StringBuilder)localObject5).append(m).append(' ');
            ((StringBuilder)localObject6).append(LocaleController.getString("EventLogPromotedAddUsers", NUM));
          }
          if ((paramChat.megagroup) && (((TLRPC.TL_channelAdminRights)localObject1).pin_messages != ((TLRPC.TL_channelAdminRights)localObject3).pin_messages))
          {
            localObject1 = ((StringBuilder)localObject6).append('\n');
            if (!((TLRPC.TL_channelAdminRights)localObject3).pin_messages) {
              break label2534;
            }
            k = 43;
          }
        }
        for (m = k;; m = k)
        {
          ((StringBuilder)localObject1).append(m).append(' ');
          ((StringBuilder)localObject6).append(LocaleController.getString("EventLogPromotedPinMessages", NUM));
          this.messageText = ((StringBuilder)localObject6).toString();
          localObject1 = localObject4;
          break;
          k = 45;
          m = k;
          break label1901;
          label2454:
          localObject5 = LocaleController.getString("EventLogPromotedChangeChannelInfo", NUM);
          break label1933;
          label2468:
          k = 45;
          m = k;
          break label1987;
          label2479:
          k = 45;
          m = k;
          break label2053;
          label2490:
          k = 45;
          m = k;
          break label2119;
          label2501:
          k = 45;
          m = k;
          break label2185;
          label2512:
          k = 45;
          m = k;
          break label2259;
          label2523:
          k = 45;
          m = k;
          break label2325;
          label2534:
          k = 45;
        }
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantToggleBan))
      {
        this.messageOwner = new TLRPC.TL_message();
        TLRPC.User localUser = MessagesController.getInstance(paramInt).getUser(Integer.valueOf(paramTL_channelAdminLogEvent.action.prev_participant.user_id));
        localObject3 = paramTL_channelAdminLogEvent.action.prev_participant.banned_rights;
        localObject5 = paramTL_channelAdminLogEvent.action.new_participant.banned_rights;
        if ((paramChat.megagroup) && ((localObject5 == null) || (!((TLRPC.TL_channelBannedRights)localObject5).view_messages) || ((localObject5 != null) && (localObject3 != null) && (((TLRPC.TL_channelBannedRights)localObject5).until_date != ((TLRPC.TL_channelBannedRights)localObject3).until_date))))
        {
          int i1;
          int i2;
          if ((localObject5 != null) && (!AndroidUtilities.isBannedForever(((TLRPC.TL_channelBannedRights)localObject5).until_date)))
          {
            localObject6 = new StringBuilder();
            k = ((TLRPC.TL_channelBannedRights)localObject5).until_date - paramTL_channelAdminLogEvent.date;
            int n = k / 60 / 60 / 24;
            k -= n * 60 * 60 * 24;
            i1 = k / 60 / 60;
            i2 = (k - i1 * 60 * 60) / 60;
            i = 0;
            j = 0;
            label2754:
            localObject1 = localObject6;
            if (j < 3)
            {
              localObject1 = null;
              if (j == 0)
              {
                k = i;
                if (n != 0)
                {
                  localObject1 = LocaleController.formatPluralString("Days", n);
                  k = i + 1;
                }
                label2797:
                if (localObject1 != null)
                {
                  if (((StringBuilder)localObject6).length() > 0) {
                    ((StringBuilder)localObject6).append(", ");
                  }
                  ((StringBuilder)localObject6).append((String)localObject1);
                }
                if (k != 2) {
                  break label3495;
                }
                localObject1 = localObject6;
              }
            }
            else
            {
              label2837:
              localObject6 = LocaleController.getString("EventLogRestrictedUntil", NUM);
              k = ((String)localObject6).indexOf("%1$s");
              localObject6 = new StringBuilder(String.format((String)localObject6, new Object[] { getUserName(localUser, this.messageOwner.entities, k), ((StringBuilder)localObject1).toString() }));
              j = 0;
              k = 0;
              localObject1 = localObject3;
              if (localObject3 == null) {
                localObject1 = new TLRPC.TL_channelBannedRights();
              }
              localObject3 = localObject5;
              if (localObject5 == null) {
                localObject3 = new TLRPC.TL_channelBannedRights();
              }
              if (((TLRPC.TL_channelBannedRights)localObject1).view_messages != ((TLRPC.TL_channelBannedRights)localObject3).view_messages)
              {
                j = k;
                if (0 == 0)
                {
                  ((StringBuilder)localObject6).append('\n');
                  j = 1;
                }
                localObject5 = ((StringBuilder)localObject6).append('\n');
                if (((TLRPC.TL_channelBannedRights)localObject3).view_messages) {
                  break label3526;
                }
                k = 43;
                m = k;
                label3001:
                ((StringBuilder)localObject5).append(m).append(' ');
                ((StringBuilder)localObject6).append(LocaleController.getString("EventLogRestrictedReadMessages", NUM));
              }
              k = j;
              if (((TLRPC.TL_channelBannedRights)localObject1).send_messages != ((TLRPC.TL_channelBannedRights)localObject3).send_messages)
              {
                k = j;
                if (j == 0)
                {
                  ((StringBuilder)localObject6).append('\n');
                  k = 1;
                }
                localObject5 = ((StringBuilder)localObject6).append('\n');
                if (((TLRPC.TL_channelBannedRights)localObject3).send_messages) {
                  break label3537;
                }
                j = 43;
                m = j;
                label3091:
                ((StringBuilder)localObject5).append(m).append(' ');
                ((StringBuilder)localObject6).append(LocaleController.getString("EventLogRestrictedSendMessages", NUM));
              }
              if ((((TLRPC.TL_channelBannedRights)localObject1).send_stickers == ((TLRPC.TL_channelBannedRights)localObject3).send_stickers) && (((TLRPC.TL_channelBannedRights)localObject1).send_inline == ((TLRPC.TL_channelBannedRights)localObject3).send_inline) && (((TLRPC.TL_channelBannedRights)localObject1).send_gifs == ((TLRPC.TL_channelBannedRights)localObject3).send_gifs))
              {
                j = k;
                if (((TLRPC.TL_channelBannedRights)localObject1).send_games == ((TLRPC.TL_channelBannedRights)localObject3).send_games) {}
              }
              else
              {
                j = k;
                if (k == 0)
                {
                  ((StringBuilder)localObject6).append('\n');
                  j = 1;
                }
                localObject5 = ((StringBuilder)localObject6).append('\n');
                if (((TLRPC.TL_channelBannedRights)localObject3).send_stickers) {
                  break label3548;
                }
                k = 43;
                m = k;
                label3220:
                ((StringBuilder)localObject5).append(m).append(' ');
                ((StringBuilder)localObject6).append(LocaleController.getString("EventLogRestrictedSendStickers", NUM));
              }
              k = j;
              if (((TLRPC.TL_channelBannedRights)localObject1).send_media != ((TLRPC.TL_channelBannedRights)localObject3).send_media)
              {
                k = j;
                if (j == 0)
                {
                  ((StringBuilder)localObject6).append('\n');
                  k = 1;
                }
                localObject5 = ((StringBuilder)localObject6).append('\n');
                if (((TLRPC.TL_channelBannedRights)localObject3).send_media) {
                  break label3559;
                }
                j = 43;
                m = j;
                label3310:
                ((StringBuilder)localObject5).append(m).append(' ');
                ((StringBuilder)localObject6).append(LocaleController.getString("EventLogRestrictedSendMedia", NUM));
              }
              if (((TLRPC.TL_channelBannedRights)localObject1).embed_links != ((TLRPC.TL_channelBannedRights)localObject3).embed_links)
              {
                if (k == 0) {
                  ((StringBuilder)localObject6).append('\n');
                }
                localObject1 = ((StringBuilder)localObject6).append('\n');
                if (((TLRPC.TL_channelBannedRights)localObject3).embed_links) {
                  break label3570;
                }
                k = 43;
              }
            }
          }
          for (m = k;; m = k)
          {
            ((StringBuilder)localObject1).append(m).append(' ');
            ((StringBuilder)localObject6).append(LocaleController.getString("EventLogRestrictedSendEmbed", NUM));
            this.messageText = ((StringBuilder)localObject6).toString();
            localObject1 = localObject4;
            break;
            if (j == 1)
            {
              k = i;
              if (i1 == 0) {
                break label2797;
              }
              localObject1 = LocaleController.formatPluralString("Hours", i1);
              k = i + 1;
              break label2797;
            }
            k = i;
            if (i2 == 0) {
              break label2797;
            }
            localObject1 = LocaleController.formatPluralString("Minutes", i2);
            k = i + 1;
            break label2797;
            label3495:
            j++;
            i = k;
            break label2754;
            localObject1 = new StringBuilder(LocaleController.getString("UserRestrictionsUntilForever", NUM));
            break label2837;
            label3526:
            k = 45;
            m = k;
            break label3001;
            label3537:
            j = 45;
            m = j;
            break label3091;
            label3548:
            k = 45;
            m = k;
            break label3220;
            label3559:
            j = 45;
            m = j;
            break label3310;
            label3570:
            k = 45;
          }
        }
        if ((localObject5 != null) && ((localObject3 == null) || (((TLRPC.TL_channelBannedRights)localObject5).view_messages))) {}
        for (localObject1 = LocaleController.getString("EventLogChannelRestricted", NUM);; localObject1 = LocaleController.getString("EventLogChannelUnrestricted", NUM))
        {
          k = ((String)localObject1).indexOf("%1$s");
          this.messageText = String.format((String)localObject1, new Object[] { getUserName(localUser, this.messageOwner.entities, k) });
          localObject1 = localObject4;
          break;
        }
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionUpdatePinned))
      {
        if ((paramTL_channelAdminLogEvent.action.message instanceof TLRPC.TL_messageEmpty))
        {
          this.messageText = replaceWithLink(LocaleController.getString("EventLogUnpinnedMessages", NUM), "un1", (TLObject)localObject2);
          localObject1 = localObject4;
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("EventLogPinnedMessages", NUM), "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        break;
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionToggleSignatures))
      {
        if (((TLRPC.TL_channelAdminLogEventActionToggleSignatures)paramTL_channelAdminLogEvent.action).new_value)
        {
          this.messageText = replaceWithLink(LocaleController.getString("EventLogToggledSignaturesOn", NUM), "un1", (TLObject)localObject2);
          localObject1 = localObject4;
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("EventLogToggledSignaturesOff", NUM), "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        break;
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionToggleInvites))
      {
        if (((TLRPC.TL_channelAdminLogEventActionToggleInvites)paramTL_channelAdminLogEvent.action).new_value)
        {
          this.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesOn", NUM), "un1", (TLObject)localObject2);
          localObject1 = localObject4;
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesOff", NUM), "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        break;
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionDeleteMessage))
      {
        this.messageText = replaceWithLink(LocaleController.getString("EventLogDeletedMessages", NUM), "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        break;
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden))
      {
        if (((TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden)paramTL_channelAdminLogEvent.action).new_value)
        {
          this.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesHistoryOff", NUM), "un1", (TLObject)localObject2);
          localObject1 = localObject4;
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesHistoryOn", NUM), "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        break;
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangeAbout))
      {
        if (paramChat.megagroup) {}
        for (localObject1 = LocaleController.getString("EventLogEditedGroupDescription", NUM);; localObject1 = LocaleController.getString("EventLogEditedChannelDescription", NUM))
        {
          this.messageText = replaceWithLink((CharSequence)localObject1, "un1", (TLObject)localObject2);
          localObject1 = new TLRPC.TL_message();
          ((TLRPC.Message)localObject1).out = false;
          ((TLRPC.Message)localObject1).unread = false;
          ((TLRPC.Message)localObject1).from_id = paramTL_channelAdminLogEvent.user_id;
          ((TLRPC.Message)localObject1).to_id = ((TLRPC.Peer)localObject3);
          ((TLRPC.Message)localObject1).date = paramTL_channelAdminLogEvent.date;
          ((TLRPC.Message)localObject1).message = ((TLRPC.TL_channelAdminLogEventActionChangeAbout)paramTL_channelAdminLogEvent.action).new_value;
          if (TextUtils.isEmpty(((TLRPC.TL_channelAdminLogEventActionChangeAbout)paramTL_channelAdminLogEvent.action).prev_value)) {
            break label4280;
          }
          ((TLRPC.Message)localObject1).media = new TLRPC.TL_messageMediaWebPage();
          ((TLRPC.Message)localObject1).media.webpage = new TLRPC.TL_webPage();
          ((TLRPC.Message)localObject1).media.webpage.flags = 10;
          ((TLRPC.Message)localObject1).media.webpage.display_url = "";
          ((TLRPC.Message)localObject1).media.webpage.url = "";
          ((TLRPC.Message)localObject1).media.webpage.site_name = LocaleController.getString("EventLogPreviousGroupDescription", NUM);
          ((TLRPC.Message)localObject1).media.webpage.description = ((TLRPC.TL_channelAdminLogEventActionChangeAbout)paramTL_channelAdminLogEvent.action).prev_value;
          break;
        }
        label4280:
        ((TLRPC.Message)localObject1).media = new TLRPC.TL_messageMediaEmpty();
        break;
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangeUsername))
      {
        localObject5 = ((TLRPC.TL_channelAdminLogEventActionChangeUsername)paramTL_channelAdminLogEvent.action).new_value;
        if (!TextUtils.isEmpty((CharSequence)localObject5)) {
          if (paramChat.megagroup)
          {
            localObject1 = LocaleController.getString("EventLogChangedGroupLink", NUM);
            label4344:
            this.messageText = replaceWithLink((CharSequence)localObject1, "un1", (TLObject)localObject2);
            localObject1 = new TLRPC.TL_message();
            ((TLRPC.Message)localObject1).out = false;
            ((TLRPC.Message)localObject1).unread = false;
            ((TLRPC.Message)localObject1).from_id = paramTL_channelAdminLogEvent.user_id;
            ((TLRPC.Message)localObject1).to_id = ((TLRPC.Peer)localObject3);
            ((TLRPC.Message)localObject1).date = paramTL_channelAdminLogEvent.date;
            if (TextUtils.isEmpty((CharSequence)localObject5)) {
              break label4720;
            }
          }
        }
        label4720:
        for (((TLRPC.Message)localObject1).message = ("https://" + MessagesController.getInstance(paramInt).linkPrefix + "/" + (String)localObject5);; ((TLRPC.Message)localObject1).message = "")
        {
          localObject3 = new TLRPC.TL_messageEntityUrl();
          ((TLRPC.TL_messageEntityUrl)localObject3).offset = 0;
          ((TLRPC.TL_messageEntityUrl)localObject3).length = ((TLRPC.Message)localObject1).message.length();
          ((TLRPC.Message)localObject1).entities.add(localObject3);
          if (TextUtils.isEmpty(((TLRPC.TL_channelAdminLogEventActionChangeUsername)paramTL_channelAdminLogEvent.action).prev_value)) {
            break label4730;
          }
          ((TLRPC.Message)localObject1).media = new TLRPC.TL_messageMediaWebPage();
          ((TLRPC.Message)localObject1).media.webpage = new TLRPC.TL_webPage();
          ((TLRPC.Message)localObject1).media.webpage.flags = 10;
          ((TLRPC.Message)localObject1).media.webpage.display_url = "";
          ((TLRPC.Message)localObject1).media.webpage.url = "";
          ((TLRPC.Message)localObject1).media.webpage.site_name = LocaleController.getString("EventLogPreviousLink", NUM);
          ((TLRPC.Message)localObject1).media.webpage.description = ("https://" + MessagesController.getInstance(paramInt).linkPrefix + "/" + ((TLRPC.TL_channelAdminLogEventActionChangeUsername)paramTL_channelAdminLogEvent.action).prev_value);
          break;
          localObject1 = LocaleController.getString("EventLogChangedChannelLink", NUM);
          break label4344;
          if (paramChat.megagroup) {}
          for (localObject1 = LocaleController.getString("EventLogRemovedGroupLink", NUM);; localObject1 = LocaleController.getString("EventLogRemovedChannelLink", NUM))
          {
            this.messageText = replaceWithLink((CharSequence)localObject1, "un1", (TLObject)localObject2);
            break;
          }
        }
        label4730:
        ((TLRPC.Message)localObject1).media = new TLRPC.TL_messageMediaEmpty();
        break;
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionEditMessage))
      {
        localObject1 = new TLRPC.TL_message();
        ((TLRPC.Message)localObject1).out = false;
        ((TLRPC.Message)localObject1).unread = false;
        ((TLRPC.Message)localObject1).from_id = paramTL_channelAdminLogEvent.user_id;
        ((TLRPC.Message)localObject1).to_id = ((TLRPC.Peer)localObject3);
        ((TLRPC.Message)localObject1).date = paramTL_channelAdminLogEvent.date;
        localObject5 = ((TLRPC.TL_channelAdminLogEventActionEditMessage)paramTL_channelAdminLogEvent.action).new_message;
        localObject3 = ((TLRPC.TL_channelAdminLogEventActionEditMessage)paramTL_channelAdminLogEvent.action).prev_message;
        if ((((TLRPC.Message)localObject5).media != null) && (!(((TLRPC.Message)localObject5).media instanceof TLRPC.TL_messageMediaEmpty)) && (!(((TLRPC.Message)localObject5).media instanceof TLRPC.TL_messageMediaWebPage)) && (TextUtils.isEmpty(((TLRPC.Message)localObject5).message)))
        {
          this.messageText = replaceWithLink(LocaleController.getString("EventLogEditedCaption", NUM), "un1", (TLObject)localObject2);
          ((TLRPC.Message)localObject1).media = ((TLRPC.Message)localObject5).media;
          ((TLRPC.Message)localObject1).media.webpage = new TLRPC.TL_webPage();
          ((TLRPC.Message)localObject1).media.webpage.site_name = LocaleController.getString("EventLogOriginalCaption", NUM);
          if (TextUtils.isEmpty(((TLRPC.Message)localObject3).message)) {
            ((TLRPC.Message)localObject1).media.webpage.description = LocaleController.getString("EventLogOriginalCaptionEmpty", NUM);
          }
        }
        for (;;)
        {
          ((TLRPC.Message)localObject1).reply_markup = ((TLRPC.Message)localObject5).reply_markup;
          ((TLRPC.Message)localObject1).media.webpage.flags = 10;
          ((TLRPC.Message)localObject1).media.webpage.display_url = "";
          ((TLRPC.Message)localObject1).media.webpage.url = "";
          break;
          ((TLRPC.Message)localObject1).media.webpage.description = ((TLRPC.Message)localObject3).message;
          continue;
          this.messageText = replaceWithLink(LocaleController.getString("EventLogEditedMessages", NUM), "un1", (TLObject)localObject2);
          ((TLRPC.Message)localObject1).message = ((TLRPC.Message)localObject5).message;
          ((TLRPC.Message)localObject1).media = new TLRPC.TL_messageMediaWebPage();
          ((TLRPC.Message)localObject1).media.webpage = new TLRPC.TL_webPage();
          ((TLRPC.Message)localObject1).media.webpage.site_name = LocaleController.getString("EventLogOriginalMessages", NUM);
          if (TextUtils.isEmpty(((TLRPC.Message)localObject3).message)) {
            ((TLRPC.Message)localObject1).media.webpage.description = LocaleController.getString("EventLogOriginalCaptionEmpty", NUM);
          } else {
            ((TLRPC.Message)localObject1).media.webpage.description = ((TLRPC.Message)localObject3).message;
          }
        }
      }
      if ((paramTL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangeStickerSet))
      {
        localObject3 = ((TLRPC.TL_channelAdminLogEventActionChangeStickerSet)paramTL_channelAdminLogEvent.action).new_stickerset;
        localObject1 = ((TLRPC.TL_channelAdminLogEventActionChangeStickerSet)paramTL_channelAdminLogEvent.action).new_stickerset;
        if ((localObject3 == null) || ((localObject3 instanceof TLRPC.TL_inputStickerSetEmpty)))
        {
          this.messageText = replaceWithLink(LocaleController.getString("EventLogRemovedStickersSet", NUM), "un1", (TLObject)localObject2);
          localObject1 = localObject4;
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("EventLogChangedStickersSet", NUM), "un1", (TLObject)localObject2);
        localObject1 = localObject4;
        break;
      }
      this.messageText = ("unsupported " + paramTL_channelAdminLogEvent.action);
      localObject1 = localObject4;
      break;
      label5306:
      this.contentType = -1;
      break label623;
      label5314:
      this.videoEditedInfo.roundVideo = isRoundVideo();
      break label760;
      label5328:
      paramTL_channelAdminLogEvent = Theme.chat_msgTextPaint;
      break label781;
      label5335:
      paramArrayList = null;
      break label791;
      paramTL_channelAdminLogEvent = Theme.chat_msgTextPaintOneEmoji;
      paramInt = AndroidUtilities.dp(32.0F);
      continue;
      paramTL_channelAdminLogEvent = Theme.chat_msgTextPaintTwoEmoji;
      paramInt = AndroidUtilities.dp(28.0F);
    }
    label5368:
    if (((MediaController)localObject5).isPlayingMessage(this))
    {
      paramTL_channelAdminLogEvent = ((MediaController)localObject5).getPlayingMessageObject();
      this.audioProgress = paramTL_channelAdminLogEvent.audioProgress;
      this.audioProgressSec = paramTL_channelAdminLogEvent.audioProgressSec;
    }
    generateLayout((TLRPC.User)localObject2);
    this.layoutCreated = true;
    generateThumbs(false);
    checkMediaExistance();
  }
  
  private boolean addEntitiesToText(CharSequence paramCharSequence, boolean paramBoolean)
  {
    return addEntitiesToText(paramCharSequence, false, paramBoolean);
  }
  
  public static void addLinks(boolean paramBoolean, CharSequence paramCharSequence)
  {
    addLinks(paramBoolean, paramCharSequence, true);
  }
  
  public static void addLinks(boolean paramBoolean1, CharSequence paramCharSequence, boolean paramBoolean2)
  {
    if ((!(paramCharSequence instanceof Spannable)) || (!containsUrls(paramCharSequence)) || (paramCharSequence.length() < 1000)) {}
    for (;;)
    {
      try
      {
        Linkify.addLinks((Spannable)paramCharSequence, 5);
        addUsernamesAndHashtags(paramBoolean1, paramCharSequence, paramBoolean2);
        return;
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
        continue;
      }
      try
      {
        Linkify.addLinks((Spannable)paramCharSequence, 1);
      }
      catch (Exception localException2)
      {
        FileLog.e(localException2);
      }
    }
  }
  
  private static void addUsernamesAndHashtags(boolean paramBoolean1, CharSequence paramCharSequence, boolean paramBoolean2)
  {
    for (;;)
    {
      int j;
      int m;
      try
      {
        if (urlPattern == null) {
          urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s)@[a-zA-Z\\d_]{1,32}|(^|\\s)#[\\w\\.]+|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)");
        }
        Matcher localMatcher = urlPattern.matcher(paramCharSequence);
        if (localMatcher.find())
        {
          i = localMatcher.start();
          j = localMatcher.end();
          int k = paramCharSequence.charAt(i);
          m = i;
          if (k != 64)
          {
            m = i;
            if (k != 35)
            {
              m = i;
              if (k != 47)
              {
                m = i;
                if (k != 36) {
                  m = i + 1;
                }
              }
            }
          }
          localObject = null;
          if (paramCharSequence.charAt(m) != '/') {
            break label196;
          }
          if (paramBoolean2)
          {
            localObject = new org/telegram/ui/Components/URLSpanBotCommand;
            String str = paramCharSequence.subSequence(m, j).toString();
            if (paramBoolean1)
            {
              i = 1;
              ((URLSpanBotCommand)localObject).<init>(str, i);
            }
          }
          else
          {
            if (localObject == null) {
              continue;
            }
            ((Spannable)paramCharSequence).setSpan(localObject, m, j, 0);
          }
        }
        else
        {
          return;
        }
      }
      catch (Exception paramCharSequence)
      {
        FileLog.e(paramCharSequence);
      }
      int i = 0;
      continue;
      label196:
      Object localObject = new URLSpanNoUnderline(paramCharSequence.subSequence(m, j).toString());
    }
  }
  
  public static boolean canDeleteMessage(int paramInt, TLRPC.Message paramMessage, TLRPC.Chat paramChat)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    if (paramMessage.id < 0) {
      bool1 = bool2;
    }
    for (;;)
    {
      return bool1;
      TLRPC.Chat localChat = paramChat;
      if (paramChat == null)
      {
        localChat = paramChat;
        if (paramMessage.to_id.channel_id != 0) {
          localChat = MessagesController.getInstance(paramInt).getChat(Integer.valueOf(paramMessage.to_id.channel_id));
        }
      }
      if (ChatObject.isChannel(localChat))
      {
        if (paramMessage.id != 1)
        {
          bool1 = bool2;
          if (localChat.creator) {
            continue;
          }
          if (localChat.admin_rights != null)
          {
            bool1 = bool2;
            if (localChat.admin_rights.delete_messages) {
              continue;
            }
            bool1 = bool2;
            if (paramMessage.out) {
              continue;
            }
          }
          if ((localChat.megagroup) && (paramMessage.out))
          {
            bool1 = bool2;
            if (paramMessage.from_id > 0) {
              continue;
            }
          }
        }
        bool1 = false;
      }
      else if ((isOut(paramMessage)) || (!ChatObject.isChannel(localChat)))
      {
        bool1 = true;
      }
    }
  }
  
  public static boolean canEditMessage(int paramInt, TLRPC.Message paramMessage, TLRPC.Chat paramChat)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    boolean bool3 = bool2;
    if (paramMessage != null)
    {
      bool3 = bool2;
      if (paramMessage.to_id != null) {
        if (paramMessage.media != null)
        {
          bool3 = bool2;
          if (!isRoundVideoDocument(paramMessage.media.document))
          {
            bool3 = bool2;
            if (isStickerDocument(paramMessage.media.document)) {}
          }
        }
        else if (paramMessage.action != null)
        {
          bool3 = bool2;
          if (!(paramMessage.action instanceof TLRPC.TL_messageActionEmpty)) {}
        }
        else
        {
          bool3 = bool2;
          if (!isForwardedMessage(paramMessage))
          {
            bool3 = bool2;
            if (paramMessage.via_bot_id == 0)
            {
              if (paramMessage.id >= 0) {
                break label122;
              }
              bool3 = bool2;
            }
          }
        }
      }
    }
    for (;;)
    {
      return bool3;
      label122:
      if ((paramMessage.from_id == paramMessage.to_id.user_id) && (paramMessage.from_id == UserConfig.getInstance(paramInt).getClientUserId()) && (!isLiveLocationMessage(paramMessage)) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaContact)))
      {
        bool3 = true;
      }
      else
      {
        TLRPC.Chat localChat = paramChat;
        if (paramChat == null)
        {
          localChat = paramChat;
          if (paramMessage.to_id.channel_id != 0)
          {
            localChat = MessagesController.getInstance(paramInt).getChat(Integer.valueOf(paramMessage.to_id.channel_id));
            bool3 = bool2;
            if (localChat == null) {
              continue;
            }
          }
        }
        if ((paramMessage.media != null) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)))
        {
          bool3 = bool2;
          if (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {}
        }
        else if ((paramMessage.out) && (localChat != null) && (localChat.megagroup) && ((localChat.creator) || ((localChat.admin_rights != null) && (localChat.admin_rights.pin_messages))))
        {
          bool3 = true;
        }
        else
        {
          bool3 = bool2;
          if (Math.abs(paramMessage.date - ConnectionsManager.getInstance(paramInt).getCurrentTime()) <= MessagesController.getInstance(paramInt).maxEditTime)
          {
            if (paramMessage.to_id.channel_id == 0)
            {
              if ((paramMessage.out) || (paramMessage.from_id == UserConfig.getInstance(paramInt).getClientUserId()))
              {
                bool3 = bool1;
                if (!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) {
                  if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
                  {
                    bool3 = bool1;
                    if (!isStickerMessage(paramMessage)) {}
                  }
                  else
                  {
                    bool3 = bool1;
                    if (!(paramMessage.media instanceof TLRPC.TL_messageMediaEmpty))
                    {
                      bool3 = bool1;
                      if (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
                        if (paramMessage.media != null) {
                          break label457;
                        }
                      }
                    }
                  }
                }
              }
              label457:
              for (bool3 = bool1;; bool3 = false) {
                break;
              }
            }
            if ((!localChat.megagroup) || (!paramMessage.out))
            {
              bool3 = bool2;
              if (!localChat.megagroup)
              {
                if (!localChat.creator)
                {
                  bool3 = bool2;
                  if (localChat.admin_rights == null) {
                    continue;
                  }
                  if (!localChat.admin_rights.edit_messages)
                  {
                    bool3 = bool2;
                    if (!paramMessage.out) {
                      continue;
                    }
                  }
                }
                bool3 = bool2;
                if (!paramMessage.post) {}
              }
            }
            else if ((!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && ((!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) || (isStickerMessage(paramMessage))) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)))
            {
              bool3 = bool2;
              if (paramMessage.media != null) {}
            }
            else
            {
              bool3 = true;
            }
          }
        }
      }
    }
  }
  
  public static boolean canEditMessageAnytime(int paramInt, TLRPC.Message paramMessage, TLRPC.Chat paramChat)
  {
    boolean bool1 = true;
    boolean bool2;
    if ((paramMessage == null) || (paramMessage.to_id == null) || ((paramMessage.media != null) && ((isRoundVideoDocument(paramMessage.media.document)) || (isStickerDocument(paramMessage.media.document)))) || ((paramMessage.action != null) && (!(paramMessage.action instanceof TLRPC.TL_messageActionEmpty))) || (isForwardedMessage(paramMessage)) || (paramMessage.via_bot_id != 0) || (paramMessage.id < 0)) {
      bool2 = false;
    }
    for (;;)
    {
      return bool2;
      if ((paramMessage.from_id == paramMessage.to_id.user_id) && (paramMessage.from_id == UserConfig.getInstance(paramInt).getClientUserId()))
      {
        bool2 = bool1;
        if (!isLiveLocationMessage(paramMessage)) {}
      }
      else
      {
        TLRPC.Chat localChat = paramChat;
        if (paramChat == null)
        {
          localChat = paramChat;
          if (paramMessage.to_id.channel_id != 0)
          {
            paramChat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(paramMessage.to_id.channel_id));
            localChat = paramChat;
            if (paramChat == null)
            {
              bool2 = false;
              continue;
            }
          }
        }
        if ((paramMessage.out) && (localChat != null) && (localChat.megagroup))
        {
          bool2 = bool1;
          if (localChat.creator) {
            continue;
          }
          if (localChat.admin_rights != null)
          {
            bool2 = bool1;
            if (localChat.admin_rights.pin_messages) {
              continue;
            }
          }
        }
        bool2 = false;
      }
    }
  }
  
  private static boolean containsUrls(CharSequence paramCharSequence)
  {
    boolean bool1 = true;
    boolean bool2;
    if ((paramCharSequence == null) || (paramCharSequence.length() < 2) || (paramCharSequence.length() > 20480)) {
      bool2 = false;
    }
    for (;;)
    {
      return bool2;
      int i = paramCharSequence.length();
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      label54:
      if (i1 < i)
      {
        int i2 = paramCharSequence.charAt(i1);
        int i3;
        int i4;
        int i5;
        if ((i2 >= 48) && (i2 <= 57))
        {
          i3 = j + 1;
          bool2 = bool1;
          if (i3 >= 6) {
            continue;
          }
          i4 = 0;
          i5 = 0;
          label105:
          if ((i2 == 64) || (i2 == 35) || (i2 == 47) || (i2 == 36))
          {
            bool2 = bool1;
            if (i1 == 0) {
              continue;
            }
          }
          if (i1 != 0)
          {
            bool2 = bool1;
            if (paramCharSequence.charAt(i1 - 1) == ' ') {
              continue;
            }
            bool2 = bool1;
            if (paramCharSequence.charAt(i1 - 1) == '\n') {
              continue;
            }
          }
          if (i2 != 58) {
            break label260;
          }
          if (i4 != 0) {
            break label254;
          }
          i4 = 1;
        }
        for (;;)
        {
          n = i2;
          i1++;
          j = i3;
          m = i5;
          k = i4;
          break label54;
          if (i2 != 32)
          {
            i3 = j;
            i5 = m;
            i4 = k;
            if (j > 0) {
              break label105;
            }
          }
          i3 = 0;
          i5 = m;
          i4 = k;
          break label105;
          label254:
          i4 = 0;
          continue;
          label260:
          if (i2 == 47)
          {
            bool2 = bool1;
            if (i4 == 2) {
              break;
            }
            if (i4 == 1)
            {
              i4++;
              continue;
            }
            i4 = 0;
            continue;
          }
          if (i2 == 46)
          {
            if ((i5 == 0) && (n != 32)) {
              i5++;
            } else {
              i5 = 0;
            }
          }
          else
          {
            if ((i2 != 32) && (n == 46))
            {
              bool2 = bool1;
              if (i5 == 1) {
                break;
              }
            }
            i5 = 0;
          }
        }
      }
      else
      {
        bool2 = false;
      }
    }
  }
  
  private void createDateArray(int paramInt, TLRPC.TL_channelAdminLogEvent paramTL_channelAdminLogEvent, ArrayList<MessageObject> paramArrayList, HashMap<String, ArrayList<MessageObject>> paramHashMap)
  {
    if ((ArrayList)paramHashMap.get(this.dateKey) == null)
    {
      ArrayList localArrayList = new ArrayList();
      paramHashMap.put(this.dateKey, localArrayList);
      paramHashMap = new TLRPC.TL_message();
      paramHashMap.message = LocaleController.formatDateChat(paramTL_channelAdminLogEvent.date);
      paramHashMap.id = 0;
      paramHashMap.date = paramTL_channelAdminLogEvent.date;
      paramTL_channelAdminLogEvent = new MessageObject(paramInt, paramHashMap, false);
      paramTL_channelAdminLogEvent.type = 10;
      paramTL_channelAdminLogEvent.contentType = 1;
      paramTL_channelAdminLogEvent.isDateObject = true;
      paramArrayList.add(paramTL_channelAdminLogEvent);
    }
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
  
  private TLRPC.Document getDocumentWithId(TLRPC.WebPage paramWebPage, long paramLong)
  {
    Object localObject;
    if ((paramWebPage == null) || (paramWebPage.cached_page == null)) {
      localObject = null;
    }
    for (;;)
    {
      return (TLRPC.Document)localObject;
      if ((paramWebPage.document != null) && (paramWebPage.document.id == paramLong))
      {
        localObject = paramWebPage.document;
      }
      else
      {
        for (int i = 0;; i++)
        {
          if (i >= paramWebPage.cached_page.documents.size()) {
            break label100;
          }
          TLRPC.Document localDocument = (TLRPC.Document)paramWebPage.cached_page.documents.get(i);
          localObject = localDocument;
          if (localDocument.id == paramLong) {
            break;
          }
        }
        label100:
        localObject = null;
      }
    }
  }
  
  public static int getInlineResultDuration(TLRPC.BotInlineResult paramBotInlineResult)
  {
    int i = getWebDocumentDuration(paramBotInlineResult.content);
    int j = i;
    if (i == 0) {
      j = getWebDocumentDuration(paramBotInlineResult.thumb);
    }
    return j;
  }
  
  public static int[] getInlineResultWidthAndHeight(TLRPC.BotInlineResult paramBotInlineResult)
  {
    int[] arrayOfInt = getWebDocumentWidthAndHeight(paramBotInlineResult.content);
    Object localObject = arrayOfInt;
    if (arrayOfInt == null)
    {
      paramBotInlineResult = getWebDocumentWidthAndHeight(paramBotInlineResult.thumb);
      localObject = paramBotInlineResult;
      if (paramBotInlineResult == null)
      {
        localObject = new int[2];
        Object tmp33_32 = localObject;
        tmp33_32[0] = 0;
        Object tmp37_33 = tmp33_32;
        tmp37_33[1] = 0;
        tmp37_33;
      }
    }
    return (int[])localObject;
  }
  
  public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Message paramMessage)
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (paramMessage.media != null)
    {
      localObject2 = localObject1;
      if (paramMessage.media.document != null)
      {
        paramMessage = paramMessage.media.document.attributes.iterator();
        do
        {
          localObject2 = localObject1;
          if (!paramMessage.hasNext()) {
            break;
          }
          localObject2 = (TLRPC.DocumentAttribute)paramMessage.next();
        } while (!(localObject2 instanceof TLRPC.TL_documentAttributeSticker));
        if (!(((TLRPC.DocumentAttribute)localObject2).stickerset instanceof TLRPC.TL_inputStickerSetEmpty)) {
          break label79;
        }
      }
    }
    label79:
    for (localObject2 = localObject1;; localObject2 = ((TLRPC.DocumentAttribute)localObject2).stickerset) {
      return (TLRPC.InputStickerSet)localObject2;
    }
  }
  
  private MessageObject getMessageObjectForBlock(TLRPC.WebPage paramWebPage, TLRPC.PageBlock paramPageBlock)
  {
    Object localObject = null;
    if ((paramPageBlock instanceof TLRPC.TL_pageBlockPhoto))
    {
      paramPageBlock = getPhotoWithId(paramWebPage, paramPageBlock.photo_id);
      if (paramPageBlock == paramWebPage.photo)
      {
        localObject = this;
        return (MessageObject)localObject;
      }
      localObject = new TLRPC.TL_message();
      ((TLRPC.TL_message)localObject).media = new TLRPC.TL_messageMediaPhoto();
      ((TLRPC.TL_message)localObject).media.photo = paramPageBlock;
    }
    for (;;)
    {
      ((TLRPC.TL_message)localObject).message = "";
      ((TLRPC.TL_message)localObject).id = Utilities.random.nextInt();
      ((TLRPC.TL_message)localObject).date = this.messageOwner.date;
      ((TLRPC.TL_message)localObject).to_id = this.messageOwner.to_id;
      ((TLRPC.TL_message)localObject).out = this.messageOwner.out;
      ((TLRPC.TL_message)localObject).from_id = this.messageOwner.from_id;
      localObject = new MessageObject(this.currentAccount, (TLRPC.Message)localObject, false);
      break;
      if ((paramPageBlock instanceof TLRPC.TL_pageBlockVideo))
      {
        localObject = this;
        if (getDocumentWithId(paramWebPage, paramPageBlock.video_id) == paramWebPage.document) {
          break;
        }
        localObject = new TLRPC.TL_message();
        ((TLRPC.TL_message)localObject).media = new TLRPC.TL_messageMediaDocument();
        ((TLRPC.TL_message)localObject).media.document = getDocumentWithId(paramWebPage, paramPageBlock.video_id);
      }
    }
  }
  
  public static int getMessageSize(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media != null) && (paramMessage.media.document != null)) {}
    for (int i = paramMessage.media.document.size;; i = 0) {
      return i;
    }
  }
  
  private TLRPC.Photo getPhotoWithId(TLRPC.WebPage paramWebPage, long paramLong)
  {
    Object localObject;
    if ((paramWebPage == null) || (paramWebPage.cached_page == null)) {
      localObject = null;
    }
    for (;;)
    {
      return (TLRPC.Photo)localObject;
      if ((paramWebPage.photo != null) && (paramWebPage.photo.id == paramLong))
      {
        localObject = paramWebPage.photo;
      }
      else
      {
        for (int i = 0;; i++)
        {
          if (i >= paramWebPage.cached_page.photos.size()) {
            break label100;
          }
          TLRPC.Photo localPhoto = (TLRPC.Photo)paramWebPage.cached_page.photos.get(i);
          localObject = localPhoto;
          if (localPhoto.id == paramLong) {
            break;
          }
        }
        label100:
        localObject = null;
      }
    }
  }
  
  public static long getStickerSetId(TLRPC.Document paramDocument)
  {
    long l1 = -1L;
    long l2;
    if (paramDocument == null)
    {
      l2 = l1;
      return l2;
    }
    for (int i = 0;; i++)
    {
      l2 = l1;
      if (i >= paramDocument.attributes.size()) {
        break;
      }
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker))
      {
        l2 = l1;
        if ((localDocumentAttribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty)) {
          break;
        }
        l2 = localDocumentAttribute.stickerset.id;
        break;
      }
    }
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
  
  private String getUserName(TLRPC.User paramUser, ArrayList<TLRPC.MessageEntity> paramArrayList, int paramInt)
  {
    if (paramUser == null) {}
    for (String str = "";; str = ContactsController.formatName(paramUser.first_name, paramUser.last_name))
    {
      if (paramInt >= 0)
      {
        localObject = new TLRPC.TL_messageEntityMentionName();
        ((TLRPC.TL_messageEntityMentionName)localObject).user_id = paramUser.id;
        ((TLRPC.TL_messageEntityMentionName)localObject).offset = paramInt;
        ((TLRPC.TL_messageEntityMentionName)localObject).length = str.length();
        paramArrayList.add(localObject);
      }
      Object localObject = str;
      if (!TextUtils.isEmpty(paramUser.username))
      {
        if (paramInt >= 0)
        {
          localObject = new TLRPC.TL_messageEntityMentionName();
          ((TLRPC.TL_messageEntityMentionName)localObject).user_id = paramUser.id;
          ((TLRPC.TL_messageEntityMentionName)localObject).offset = (str.length() + paramInt + 2);
          ((TLRPC.TL_messageEntityMentionName)localObject).length = (paramUser.username.length() + 1);
          paramArrayList.add(localObject);
        }
        localObject = String.format("%1$s (@%2$s)", new Object[] { str, paramUser.username });
      }
      return (String)localObject;
    }
  }
  
  public static int getWebDocumentDuration(TLRPC.WebDocument paramWebDocument)
  {
    int i = 0;
    int j;
    if (paramWebDocument == null)
    {
      j = i;
      return j;
    }
    int k = 0;
    int m = paramWebDocument.attributes.size();
    for (;;)
    {
      j = i;
      if (k >= m) {
        break;
      }
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramWebDocument.attributes.get(k);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo))
      {
        j = localDocumentAttribute.duration;
        break;
      }
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
      {
        j = localDocumentAttribute.duration;
        break;
      }
      k++;
    }
  }
  
  public static int[] getWebDocumentWidthAndHeight(TLRPC.WebDocument paramWebDocument)
  {
    Object localObject1 = null;
    Object localObject2;
    if (paramWebDocument == null)
    {
      localObject2 = localObject1;
      return (int[])localObject2;
    }
    int i = 0;
    int j = paramWebDocument.attributes.size();
    for (;;)
    {
      localObject2 = localObject1;
      if (i >= j) {
        break;
      }
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramWebDocument.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeImageSize))
      {
        localObject2 = new int[2];
        localObject2[0] = localDocumentAttribute.w;
        localObject2[1] = localDocumentAttribute.h;
        break;
      }
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo))
      {
        localObject2 = new int[2];
        localObject2[0] = localDocumentAttribute.w;
        localObject2[1] = localDocumentAttribute.h;
        break;
      }
      i++;
    }
  }
  
  public static boolean isContentUnread(TLRPC.Message paramMessage)
  {
    return paramMessage.media_unread;
  }
  
  public static boolean isForwardedMessage(TLRPC.Message paramMessage)
  {
    if (((paramMessage.flags & 0x4) != 0) && (paramMessage.fwd_from != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isGameMessage(TLRPC.Message paramMessage)
  {
    return paramMessage.media instanceof TLRPC.TL_messageMediaGame;
  }
  
  public static boolean isGifDocument(TLRPC.Document paramDocument)
  {
    if ((paramDocument != null) && (paramDocument.thumb != null) && (paramDocument.mime_type != null) && ((paramDocument.mime_type.equals("image/gif")) || (isNewGifDocument(paramDocument)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isGifDocument(TLRPC.TL_webDocument paramTL_webDocument)
  {
    if ((paramTL_webDocument != null) && ((paramTL_webDocument.mime_type.equals("image/gif")) || (isNewGifDocument(paramTL_webDocument)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isGifMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media != null) && (paramMessage.media.document != null) && (isGifDocument(paramMessage.media.document))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isImageWebDocument(TLRPC.TL_webDocument paramTL_webDocument)
  {
    if ((paramTL_webDocument != null) && (!isGifDocument(paramTL_webDocument)) && (paramTL_webDocument.mime_type.startsWith("image/"))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isInvoiceMessage(TLRPC.Message paramMessage)
  {
    return paramMessage.media instanceof TLRPC.TL_messageMediaInvoice;
  }
  
  public static boolean isLiveLocationMessage(TLRPC.Message paramMessage)
  {
    return paramMessage.media instanceof TLRPC.TL_messageMediaGeoLive;
  }
  
  public static boolean isMaskDocument(TLRPC.Document paramDocument)
  {
    int i;
    if (paramDocument != null)
    {
      i = 0;
      if (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if ((!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) || (!localDocumentAttribute.mask)) {}
      }
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      i++;
      break;
    }
  }
  
  public static boolean isMaskMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media != null) && (paramMessage.media.document != null) && (isMaskDocument(paramMessage.media.document))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isMediaEmpty(TLRPC.Message paramMessage)
  {
    if ((paramMessage == null) || (paramMessage.media == null) || ((paramMessage.media instanceof TLRPC.TL_messageMediaEmpty)) || ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isMegagroup(TLRPC.Message paramMessage)
  {
    if ((paramMessage.flags & 0x80000000) != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isMusicDocument(TLRPC.Document paramDocument)
  {
    boolean bool1 = true;
    int i;
    Object localObject;
    boolean bool2;
    if (paramDocument != null)
    {
      i = 0;
      if (i < paramDocument.attributes.size())
      {
        localObject = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if ((localObject instanceof TLRPC.TL_documentAttributeAudio)) {
          if (!((TLRPC.DocumentAttribute)localObject).voice) {
            bool2 = bool1;
          }
        }
      }
    }
    for (;;)
    {
      return bool2;
      bool2 = false;
      continue;
      i++;
      break;
      if (!TextUtils.isEmpty(paramDocument.mime_type))
      {
        localObject = paramDocument.mime_type.toLowerCase();
        bool2 = bool1;
        if (((String)localObject).equals("audio/flac")) {
          continue;
        }
        bool2 = bool1;
        if (((String)localObject).equals("audio/ogg")) {
          continue;
        }
        bool2 = bool1;
        if (((String)localObject).equals("audio/opus")) {
          continue;
        }
        bool2 = bool1;
        if (((String)localObject).equals("audio/x-opus+ogg")) {
          continue;
        }
        if (((String)localObject).equals("application/octet-stream"))
        {
          bool2 = bool1;
          if (FileLoader.getDocumentFileName(paramDocument).endsWith(".opus")) {
            continue;
          }
        }
      }
      bool2 = false;
    }
  }
  
  public static boolean isMusicMessage(TLRPC.Message paramMessage)
  {
    boolean bool;
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      bool = isMusicDocument(paramMessage.media.webpage.document);
    }
    for (;;)
    {
      return bool;
      if ((paramMessage.media != null) && (paramMessage.media.document != null) && (isMusicDocument(paramMessage.media.document))) {
        bool = true;
      } else {
        bool = false;
      }
    }
  }
  
  public static boolean isNewGifDocument(TLRPC.Document paramDocument)
  {
    if ((paramDocument != null) && (paramDocument.mime_type != null) && (paramDocument.mime_type.equals("video/mp4")))
    {
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      if (m < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(m);
        int n;
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAnimated)) {
          n = 1;
        }
        for (;;)
        {
          m++;
          k = n;
          break;
          n = k;
          if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo))
          {
            i = localDocumentAttribute.w;
            j = localDocumentAttribute.w;
            n = k;
          }
        }
      }
      if ((k == 0) || (i > 1280) || (j > 1280)) {}
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isNewGifDocument(TLRPC.TL_webDocument paramTL_webDocument)
  {
    if ((paramTL_webDocument != null) && (paramTL_webDocument.mime_type != null) && (paramTL_webDocument.mime_type.equals("video/mp4")))
    {
      int i = 0;
      int j = 0;
      int k = 0;
      if (k < paramTL_webDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramTL_webDocument.attributes.get(k);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAnimated)) {}
        for (;;)
        {
          k++;
          break;
          if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo))
          {
            i = localDocumentAttribute.w;
            j = localDocumentAttribute.w;
          }
        }
      }
      if ((i > 1280) || (j > 1280)) {}
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isNewGifMessage(TLRPC.Message paramMessage)
  {
    boolean bool;
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      bool = isNewGifDocument(paramMessage.media.webpage.document);
    }
    for (;;)
    {
      return bool;
      if ((paramMessage.media != null) && (paramMessage.media.document != null) && (isNewGifDocument(paramMessage.media.document))) {
        bool = true;
      } else {
        bool = false;
      }
    }
  }
  
  public static boolean isOut(TLRPC.Message paramMessage)
  {
    return paramMessage.out;
  }
  
  public static boolean isPhoto(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {}
    for (boolean bool = paramMessage.media.webpage.photo instanceof TLRPC.TL_photo;; bool = paramMessage.media instanceof TLRPC.TL_messageMediaPhoto) {
      return bool;
    }
  }
  
  public static boolean isRoundVideoDocument(TLRPC.Document paramDocument)
  {
    if ((paramDocument != null) && (paramDocument.mime_type != null) && (paramDocument.mime_type.equals("video/mp4")))
    {
      int i = 0;
      int j = 0;
      bool = false;
      for (int k = 0; k < paramDocument.attributes.size(); k++)
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(k);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo))
        {
          i = localDocumentAttribute.w;
          j = localDocumentAttribute.w;
          bool = localDocumentAttribute.round_message;
        }
      }
      if ((!bool) || (i > 1280) || (j > 1280)) {}
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isRoundVideoMessage(TLRPC.Message paramMessage)
  {
    boolean bool;
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      bool = isRoundVideoDocument(paramMessage.media.webpage.document);
    }
    for (;;)
    {
      return bool;
      if ((paramMessage.media != null) && (paramMessage.media.document != null) && (isRoundVideoDocument(paramMessage.media.document))) {
        bool = true;
      } else {
        bool = false;
      }
    }
  }
  
  public static boolean isSecretPhotoOrVideo(TLRPC.Message paramMessage)
  {
    boolean bool = true;
    if ((paramMessage instanceof TLRPC.TL_message_secret)) {
      if (((!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (!isRoundVideoMessage(paramMessage)) && (!isVideoMessage(paramMessage))) || (paramMessage.ttl <= 0) || (paramMessage.ttl > 60)) {}
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      if ((paramMessage instanceof TLRPC.TL_message))
      {
        if (((!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument))) || (paramMessage.media.ttl_seconds == 0)) {
          bool = false;
        }
      }
      else {
        bool = false;
      }
    }
  }
  
  public static boolean isStickerDocument(TLRPC.Document paramDocument)
  {
    int i;
    if (paramDocument != null)
    {
      i = 0;
      if (i < paramDocument.attributes.size()) {
        if (!((TLRPC.DocumentAttribute)paramDocument.attributes.get(i) instanceof TLRPC.TL_documentAttributeSticker)) {}
      }
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      i++;
      break;
    }
  }
  
  public static boolean isStickerMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media != null) && (paramMessage.media.document != null) && (isStickerDocument(paramMessage.media.document))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isUnread(TLRPC.Message paramMessage)
  {
    return paramMessage.unread;
  }
  
  public static boolean isVideoDocument(TLRPC.Document paramDocument)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    int i;
    int j;
    int k;
    int m;
    int n;
    TLRPC.DocumentAttribute localDocumentAttribute;
    if (paramDocument != null)
    {
      i = 0;
      j = 0;
      k = 0;
      m = 0;
      n = 0;
      if (n >= paramDocument.attributes.size()) {
        break label140;
      }
      localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(n);
      if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
        break label103;
      }
      if (!localDocumentAttribute.round_message) {
        break label68;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      label68:
      int i1 = 1;
      int i2 = localDocumentAttribute.w;
      int i3 = localDocumentAttribute.h;
      for (;;)
      {
        n++;
        m = i3;
        j = i1;
        k = i2;
        break;
        label103:
        i3 = m;
        i1 = j;
        i2 = k;
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAnimated))
        {
          i = 1;
          i3 = m;
          i1 = j;
          i2 = k;
        }
      }
      label140:
      n = i;
      if (i != 0) {
        if (k <= 1280)
        {
          n = i;
          if (m <= 1280) {}
        }
        else
        {
          n = 0;
        }
      }
      bool2 = bool1;
      if (j != 0)
      {
        bool2 = bool1;
        if (n == 0) {
          bool2 = true;
        }
      }
    }
  }
  
  public static boolean isVideoMessage(TLRPC.Message paramMessage)
  {
    boolean bool;
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      bool = isVideoDocument(paramMessage.media.webpage.document);
    }
    for (;;)
    {
      return bool;
      if ((paramMessage.media != null) && (paramMessage.media.document != null) && (isVideoDocument(paramMessage.media.document))) {
        bool = true;
      } else {
        bool = false;
      }
    }
  }
  
  public static boolean isVideoWebDocument(TLRPC.TL_webDocument paramTL_webDocument)
  {
    if ((paramTL_webDocument != null) && (paramTL_webDocument.mime_type.startsWith("video/"))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isVoiceDocument(TLRPC.Document paramDocument)
  {
    int i;
    TLRPC.DocumentAttribute localDocumentAttribute;
    if (paramDocument != null)
    {
      i = 0;
      if (i < paramDocument.attributes.size())
      {
        localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio)) {}
      }
    }
    for (boolean bool = localDocumentAttribute.voice;; bool = false)
    {
      return bool;
      i++;
      break;
    }
  }
  
  public static boolean isVoiceMessage(TLRPC.Message paramMessage)
  {
    boolean bool;
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) {
      bool = isVoiceDocument(paramMessage.media.webpage.document);
    }
    for (;;)
    {
      return bool;
      if ((paramMessage.media != null) && (paramMessage.media.document != null) && (isVoiceDocument(paramMessage.media.document))) {
        bool = true;
      } else {
        bool = false;
      }
    }
  }
  
  public static boolean isVoiceWebDocument(TLRPC.TL_webDocument paramTL_webDocument)
  {
    if ((paramTL_webDocument != null) && (paramTL_webDocument.mime_type.equals("audio/ogg"))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static void setUnreadFlags(TLRPC.Message paramMessage, int paramInt)
  {
    boolean bool1 = true;
    if ((paramInt & 0x1) == 0)
    {
      bool2 = true;
      paramMessage.unread = bool2;
      if ((paramInt & 0x2) != 0) {
        break label34;
      }
    }
    label34:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      paramMessage.media_unread = bool2;
      return;
      bool2 = false;
      break;
    }
  }
  
  public static boolean shouldEncryptPhotoOrVideo(TLRPC.Message paramMessage)
  {
    boolean bool = true;
    if ((paramMessage instanceof TLRPC.TL_message_secret)) {
      if (((!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (!isVideoMessage(paramMessage))) || (paramMessage.ttl <= 0) || (paramMessage.ttl > 60)) {}
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      if (((!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument))) || (paramMessage.media.ttl_seconds == 0)) {
        bool = false;
      }
    }
  }
  
  public boolean addEntitiesToText(CharSequence paramCharSequence, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool1 = false;
    if (!(paramCharSequence instanceof Spannable)) {}
    boolean bool2;
    for (paramBoolean1 = false;; paramBoolean1 = bool2)
    {
      return paramBoolean1;
      Spannable localSpannable = (Spannable)paramCharSequence;
      int i = this.messageOwner.entities.size();
      URLSpan[] arrayOfURLSpan = (URLSpan[])localSpannable.getSpans(0, paramCharSequence.length(), URLSpan.class);
      bool2 = bool1;
      if (arrayOfURLSpan != null)
      {
        bool2 = bool1;
        if (arrayOfURLSpan.length > 0) {
          bool2 = true;
        }
      }
      int j = 0;
      if (j < i)
      {
        TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)this.messageOwner.entities.get(j);
        if ((localMessageEntity.length > 0) && (localMessageEntity.offset >= 0)) {
          if (localMessageEntity.offset < paramCharSequence.length()) {}
        }
        for (;;)
        {
          j++;
          break;
          if (localMessageEntity.offset + localMessageEntity.length > paramCharSequence.length()) {
            localMessageEntity.length = (paramCharSequence.length() - localMessageEntity.offset);
          }
          int k;
          if (((!paramBoolean2) || ((localMessageEntity instanceof TLRPC.TL_messageEntityBold)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityItalic)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityCode)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityPre)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityMentionName)) || ((localMessageEntity instanceof TLRPC.TL_inputMessageEntityMentionName))) && (arrayOfURLSpan != null) && (arrayOfURLSpan.length > 0))
          {
            k = 0;
            if (k < arrayOfURLSpan.length)
            {
              if (arrayOfURLSpan[k] == null) {}
              for (;;)
              {
                k++;
                break;
                int m = localSpannable.getSpanStart(arrayOfURLSpan[k]);
                int n = localSpannable.getSpanEnd(arrayOfURLSpan[k]);
                if (((localMessageEntity.offset <= m) && (localMessageEntity.offset + localMessageEntity.length >= m)) || ((localMessageEntity.offset <= n) && (localMessageEntity.offset + localMessageEntity.length >= n)))
                {
                  localSpannable.removeSpan(arrayOfURLSpan[k]);
                  arrayOfURLSpan[k] = null;
                }
              }
            }
          }
          if ((localMessageEntity instanceof TLRPC.TL_messageEntityBold))
          {
            localSpannable.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
          }
          else if ((localMessageEntity instanceof TLRPC.TL_messageEntityItalic))
          {
            localSpannable.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
          }
          else
          {
            if (((localMessageEntity instanceof TLRPC.TL_messageEntityCode)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityPre)))
            {
              int i1;
              if (paramBoolean1)
              {
                k = 2;
                i1 = k;
              }
              for (;;)
              {
                localSpannable.setSpan(new URLSpanMono(localSpannable, localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, i1), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
                break;
                if (isOutOwner())
                {
                  k = 1;
                  i1 = k;
                }
                else
                {
                  k = 0;
                  i1 = k;
                }
              }
            }
            if ((localMessageEntity instanceof TLRPC.TL_messageEntityMentionName))
            {
              localSpannable.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_messageEntityMentionName)localMessageEntity).user_id, this.type), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
            }
            else if ((localMessageEntity instanceof TLRPC.TL_inputMessageEntityMentionName))
            {
              localSpannable.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_inputMessageEntityMentionName)localMessageEntity).user_id.user_id, this.type), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
            }
            else if (!paramBoolean2)
            {
              String str1 = TextUtils.substring(paramCharSequence, localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length);
              if ((localMessageEntity instanceof TLRPC.TL_messageEntityBotCommand))
              {
                localSpannable.setSpan(new URLSpanBotCommand(str1, this.type), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
              }
              else if (((localMessageEntity instanceof TLRPC.TL_messageEntityHashtag)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityMention)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityCashtag)))
              {
                localSpannable.setSpan(new URLSpanNoUnderline(str1), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
              }
              else if ((localMessageEntity instanceof TLRPC.TL_messageEntityEmail))
              {
                localSpannable.setSpan(new URLSpanReplacement("mailto:" + str1), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
              }
              else if ((localMessageEntity instanceof TLRPC.TL_messageEntityUrl))
              {
                bool2 = true;
                if ((!str1.toLowerCase().startsWith("http")) && (!str1.toLowerCase().startsWith("tg://"))) {
                  localSpannable.setSpan(new URLSpanBrowser("http://" + str1), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
                } else {
                  localSpannable.setSpan(new URLSpanBrowser(str1), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
                }
              }
              else if ((localMessageEntity instanceof TLRPC.TL_messageEntityPhone))
              {
                bool2 = true;
                String str2 = PhoneFormat.stripExceptNumbers(str1);
                String str3 = str2;
                if (str1.startsWith("+")) {
                  str3 = "+" + str2;
                }
                localSpannable.setSpan(new URLSpanBrowser("tel://" + str3), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
              }
              else if ((localMessageEntity instanceof TLRPC.TL_messageEntityTextUrl))
              {
                localSpannable.setSpan(new URLSpanReplacement(localMessageEntity.url), localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length, 33);
              }
            }
          }
        }
      }
    }
  }
  
  public void applyNewText()
  {
    if (TextUtils.isEmpty(this.messageOwner.message)) {
      return;
    }
    TLRPC.User localUser = null;
    if (isFromUser()) {
      localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
    }
    this.messageText = this.messageOwner.message;
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {}
    for (TextPaint localTextPaint = Theme.chat_msgGameTextPaint;; localTextPaint = Theme.chat_msgTextPaint)
    {
      this.messageText = Emoji.replaceEmoji(this.messageText, localTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      generateLayout(localUser);
      break;
    }
  }
  
  public boolean canDeleteMessage(TLRPC.Chat paramChat)
  {
    if ((this.eventId == 0L) && (canDeleteMessage(this.currentAccount, this.messageOwner, paramChat))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean canEditMessage(TLRPC.Chat paramChat)
  {
    return canEditMessage(this.currentAccount, this.messageOwner, paramChat);
  }
  
  public boolean canEditMessageAnytime(TLRPC.Chat paramChat)
  {
    return canEditMessageAnytime(this.currentAccount, this.messageOwner, paramChat);
  }
  
  public boolean canStreamVideo()
  {
    boolean bool1 = false;
    TLRPC.Document localDocument = getDocument();
    if (localDocument == null) {}
    for (boolean bool2 = bool1;; bool2 = true)
    {
      return bool2;
      if (!SharedConfig.streamAllVideo) {
        break;
      }
    }
    for (int i = 0;; i++)
    {
      bool2 = bool1;
      if (i >= localDocument.attributes.size()) {
        break;
      }
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)localDocument.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo))
      {
        bool2 = localDocumentAttribute.supports_streaming;
        break;
      }
    }
  }
  
  public boolean checkLayout()
  {
    boolean bool = true;
    if ((this.type != 0) || (this.messageOwner.to_id == null) || (this.messageText == null) || (this.messageText.length() == 0)) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      int i;
      if (this.layoutCreated)
      {
        if (!AndroidUtilities.isTablet()) {
          break label173;
        }
        i = AndroidUtilities.getMinTabletSide();
        label59:
        if (Math.abs(this.generatedWithMinSize - i) > AndroidUtilities.dp(52.0F)) {
          this.layoutCreated = false;
        }
      }
      if (!this.layoutCreated)
      {
        this.layoutCreated = true;
        TLRPC.User localUser = null;
        if (isFromUser()) {
          localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {}
        for (TextPaint localTextPaint = Theme.chat_msgGameTextPaint;; localTextPaint = Theme.chat_msgTextPaint)
        {
          this.messageText = Emoji.replaceEmoji(this.messageText, localTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
          generateLayout(localUser);
          break;
          label173:
          i = AndroidUtilities.displaySize.x;
          break label59;
        }
      }
      bool = false;
    }
  }
  
  public void checkMediaExistance()
  {
    this.attachPathExists = false;
    this.mediaExists = false;
    Object localObject;
    if (this.type == 1) {
      if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null)
      {
        localObject = FileLoader.getPathToMessage(this.messageOwner);
        if (needDrawBluredPreview()) {
          this.mediaExists = new File(((File)localObject).getAbsolutePath() + ".enc").exists();
        }
        if (!this.mediaExists) {
          this.mediaExists = ((File)localObject).exists();
        }
      }
    }
    for (;;)
    {
      return;
      if ((this.type == 8) || (this.type == 3) || (this.type == 9) || (this.type == 2) || (this.type == 14) || (this.type == 5))
      {
        if ((this.messageOwner.attachPath != null) && (this.messageOwner.attachPath.length() > 0)) {
          this.attachPathExists = new File(this.messageOwner.attachPath).exists();
        }
        if (!this.attachPathExists)
        {
          localObject = FileLoader.getPathToMessage(this.messageOwner);
          if ((this.type == 3) && (needDrawBluredPreview())) {
            this.mediaExists = new File(((File)localObject).getAbsolutePath() + ".enc").exists();
          }
          if (!this.mediaExists) {
            this.mediaExists = ((File)localObject).exists();
          }
        }
      }
      else
      {
        localObject = getDocument();
        if (localObject != null)
        {
          this.mediaExists = FileLoader.getPathToAttach((TLObject)localObject).exists();
        }
        else if (this.type == 0)
        {
          localObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
          if ((localObject != null) && (localObject != null)) {
            this.mediaExists = FileLoader.getPathToAttach((TLObject)localObject, true).exists();
          }
        }
      }
    }
  }
  
  public void generateCaption()
  {
    if ((this.caption != null) || (isRoundVideo())) {}
    while ((isMediaEmpty()) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) || (TextUtils.isEmpty(this.messageOwner.message))) {
      return;
    }
    this.caption = Emoji.replaceEmoji(this.messageOwner.message, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
    int j;
    label89:
    int k;
    label124:
    boolean bool;
    if (this.messageOwner.send_state != 0)
    {
      int i = 0;
      j = 0;
      k = i;
      if (j < this.messageOwner.entities.size())
      {
        if (!(this.messageOwner.entities.get(j) instanceof TLRPC.TL_inputMessageEntityMentionName)) {
          k = 1;
        }
      }
      else
      {
        if ((k != 0) || ((this.eventId == 0L) && (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto_old)) && (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto_layer68)) && (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto_layer74)) && (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument_old)) && (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument_layer68)) && (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument_layer74)) && ((!isOut()) || (this.messageOwner.send_state == 0)) && (this.messageOwner.id >= 0))) {
          break label327;
        }
        bool = true;
        if (!bool) {
          break label343;
        }
        if (!containsUrls(this.caption)) {}
      }
    }
    for (;;)
    {
      try
      {
        Linkify.addLinks((Spannable)this.caption, 5);
        addUsernamesAndHashtags(isOutOwner(), this.caption, true);
        addEntitiesToText(this.caption, bool);
        break;
        j++;
        break label89;
        if (!this.messageOwner.entities.isEmpty())
        {
          k = 1;
          break label124;
        }
        k = 0;
        continue;
        label327:
        bool = false;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        continue;
      }
      try
      {
        label343:
        Linkify.addLinks((Spannable)this.caption, 4);
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
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
        localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
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
      if ((localUser != null) && (localUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId())) {}
      for (this.messageText = LocaleController.formatString("ActionYouScored", NUM, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) });; this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", NUM, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) }), "un1", localUser)) {
        return;
      }
    }
    if ((localUser != null) && (localUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId())) {}
    for (this.messageText = LocaleController.formatString("ActionYouScoredInGame", NUM, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) });; this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", NUM, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) }), "un1", localUser))
    {
      this.messageText = replaceWithLink(this.messageText, "un2", paramUser);
      break;
    }
  }
  
  /* Error */
  public void generateLayout(TLRPC.User paramUser)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 110	org/telegram/messenger/MessageObject:type	I
    //   4: ifne +23 -> 27
    //   7: aload_0
    //   8: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   11: getfield 387	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   14: ifnull +13 -> 27
    //   17: aload_0
    //   18: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   21: invokestatic 1234	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   24: ifeq +4 -> 28
    //   27: return
    //   28: aload_0
    //   29: invokevirtual 1994	org/telegram/messenger/MessageObject:generateLinkDescription	()V
    //   32: aload_0
    //   33: new 376	java/util/ArrayList
    //   36: dup
    //   37: invokespecial 1480	java/util/ArrayList:<init>	()V
    //   40: putfield 1996	org/telegram/messenger/MessageObject:textLayoutBlocks	Ljava/util/ArrayList;
    //   43: aload_0
    //   44: iconst_0
    //   45: putfield 1998	org/telegram/messenger/MessageObject:textWidth	I
    //   48: aload_0
    //   49: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   52: getfield 1954	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   55: ifeq +978 -> 1033
    //   58: iconst_0
    //   59: istore_2
    //   60: iconst_0
    //   61: istore_3
    //   62: iload_2
    //   63: istore 4
    //   65: iload_3
    //   66: aload_0
    //   67: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   70: getfield 1029	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   73: invokevirtual 379	java/util/ArrayList:size	()I
    //   76: if_icmpge +23 -> 99
    //   79: aload_0
    //   80: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   83: getfield 1029	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   86: iload_3
    //   87: invokevirtual 380	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   90: instanceof 1786
    //   93: ifne +934 -> 1027
    //   96: iconst_1
    //   97: istore 4
    //   99: iload 4
    //   101: ifne +957 -> 1058
    //   104: aload_0
    //   105: getfield 132	org/telegram/messenger/MessageObject:eventId	J
    //   108: lconst_0
    //   109: lcmp
    //   110: ifne +126 -> 236
    //   113: aload_0
    //   114: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   117: instanceof 2000
    //   120: ifne +116 -> 236
    //   123: aload_0
    //   124: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   127: instanceof 2002
    //   130: ifne +106 -> 236
    //   133: aload_0
    //   134: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   137: instanceof 2004
    //   140: ifne +96 -> 236
    //   143: aload_0
    //   144: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   147: instanceof 2006
    //   150: ifne +86 -> 236
    //   153: aload_0
    //   154: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   157: instanceof 2008
    //   160: ifne +76 -> 236
    //   163: aload_0
    //   164: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   167: instanceof 2010
    //   170: ifne +66 -> 236
    //   173: aload_0
    //   174: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   177: instanceof 1747
    //   180: ifne +56 -> 236
    //   183: aload_0
    //   184: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   187: getfield 257	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   190: instanceof 765
    //   193: ifne +43 -> 236
    //   196: aload_0
    //   197: invokevirtual 321	org/telegram/messenger/MessageObject:isOut	()Z
    //   200: ifeq +13 -> 213
    //   203: aload_0
    //   204: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   207: getfield 1954	org/telegram/tgnet/TLRPC$Message:send_state	I
    //   210: ifne +26 -> 236
    //   213: aload_0
    //   214: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   217: getfield 222	org/telegram/tgnet/TLRPC$Message:id	I
    //   220: iflt +16 -> 236
    //   223: aload_0
    //   224: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   227: getfield 257	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   230: instanceof 770
    //   233: ifeq +825 -> 1058
    //   236: iconst_1
    //   237: istore 5
    //   239: iload 5
    //   241: ifeq +823 -> 1064
    //   244: aload_0
    //   245: invokevirtual 1813	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   248: aload_0
    //   249: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   252: invokestatic 2012	org/telegram/messenger/MessageObject:addLinks	(ZLjava/lang/CharSequence;)V
    //   255: aload_0
    //   256: aload_0
    //   257: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   260: iload 5
    //   262: invokespecial 1968	org/telegram/messenger/MessageObject:addEntitiesToText	(Ljava/lang/CharSequence;Z)Z
    //   265: istore 6
    //   267: aload_0
    //   268: getfield 132	org/telegram/messenger/MessageObject:eventId	J
    //   271: lconst_0
    //   272: lcmp
    //   273: ifne +841 -> 1114
    //   276: aload_0
    //   277: invokevirtual 1813	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   280: ifne +834 -> 1114
    //   283: aload_0
    //   284: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   287: getfield 1667	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   290: ifnull +42 -> 332
    //   293: aload_0
    //   294: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   297: getfield 1667	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   300: getfield 2017	org/telegram/tgnet/TLRPC$MessageFwdHeader:saved_from_peer	Lorg/telegram/tgnet/TLRPC$Peer;
    //   303: ifnonnull +91 -> 394
    //   306: aload_0
    //   307: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   310: getfield 1667	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   313: getfield 2018	org/telegram/tgnet/TLRPC$MessageFwdHeader:from_id	I
    //   316: ifne +78 -> 394
    //   319: aload_0
    //   320: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   323: getfield 1667	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   326: getfield 2019	org/telegram/tgnet/TLRPC$MessageFwdHeader:channel_id	I
    //   329: ifne +65 -> 394
    //   332: aload_0
    //   333: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   336: getfield 142	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   339: ifle +775 -> 1114
    //   342: aload_0
    //   343: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   346: getfield 387	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   349: getfield 392	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   352: ifne +42 -> 394
    //   355: aload_0
    //   356: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   359: getfield 387	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   362: getfield 1502	org/telegram/tgnet/TLRPC$Peer:chat_id	I
    //   365: ifne +29 -> 394
    //   368: aload_0
    //   369: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   372: getfield 257	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   375: instanceof 259
    //   378: ifne +16 -> 394
    //   381: aload_0
    //   382: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   385: getfield 257	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   388: instanceof 765
    //   391: ifeq +723 -> 1114
    //   394: iconst_1
    //   395: istore 4
    //   397: invokestatic 1891	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   400: ifeq +720 -> 1120
    //   403: invokestatic 1894	org/telegram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   406: istore_3
    //   407: aload_0
    //   408: iload_3
    //   409: putfield 1896	org/telegram/messenger/MessageObject:generatedWithMinSize	I
    //   412: aload_0
    //   413: getfield 1896	org/telegram/messenger/MessageObject:generatedWithMinSize	I
    //   416: istore_3
    //   417: iload 4
    //   419: ifne +12 -> 431
    //   422: aload_0
    //   423: getfield 132	org/telegram/messenger/MessageObject:eventId	J
    //   426: lconst_0
    //   427: lcmp
    //   428: ifeq +702 -> 1130
    //   431: ldc_w 2020
    //   434: fstore 7
    //   436: iload_3
    //   437: fload 7
    //   439: invokestatic 281	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   442: isub
    //   443: istore_3
    //   444: aload_1
    //   445: ifnull +10 -> 455
    //   448: aload_1
    //   449: getfield 2023	org/telegram/tgnet/TLRPC$User:bot	Z
    //   452: ifne +49 -> 501
    //   455: aload_0
    //   456: invokevirtual 395	org/telegram/messenger/MessageObject:isMegagroup	()Z
    //   459: ifne +32 -> 491
    //   462: iload_3
    //   463: istore 4
    //   465: aload_0
    //   466: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   469: getfield 1667	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   472: ifnull +39 -> 511
    //   475: iload_3
    //   476: istore 4
    //   478: aload_0
    //   479: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   482: getfield 1667	org/telegram/tgnet/TLRPC$Message:fwd_from	Lorg/telegram/tgnet/TLRPC$MessageFwdHeader;
    //   485: getfield 2019	org/telegram/tgnet/TLRPC$MessageFwdHeader:channel_id	I
    //   488: ifeq +23 -> 511
    //   491: iload_3
    //   492: istore 4
    //   494: aload_0
    //   495: invokevirtual 321	org/telegram/messenger/MessageObject:isOut	()Z
    //   498: ifne +13 -> 511
    //   501: iload_3
    //   502: ldc_w 275
    //   505: invokestatic 281	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   508: isub
    //   509: istore 4
    //   511: iload 4
    //   513: istore_2
    //   514: aload_0
    //   515: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   518: getfield 257	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   521: instanceof 259
    //   524: ifeq +13 -> 537
    //   527: iload 4
    //   529: ldc_w 2024
    //   532: invokestatic 281	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   535: isub
    //   536: istore_2
    //   537: aload_0
    //   538: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   541: getfield 257	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   544: instanceof 259
    //   547: ifeq +591 -> 1138
    //   550: getstatic 263	org/telegram/ui/ActionBar/Theme:chat_msgGameTextPaint	Landroid/text/TextPaint;
    //   553: astore_1
    //   554: getstatic 2029	android/os/Build$VERSION:SDK_INT	I
    //   557: bipush 24
    //   559: if_icmplt +586 -> 1145
    //   562: aload_0
    //   563: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   566: iconst_0
    //   567: aload_0
    //   568: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   571: invokeinterface 299 1 0
    //   576: aload_1
    //   577: iload_2
    //   578: invokestatic 2035	android/text/StaticLayout$Builder:obtain	(Ljava/lang/CharSequence;IILandroid/text/TextPaint;I)Landroid/text/StaticLayout$Builder;
    //   581: iconst_1
    //   582: invokevirtual 2039	android/text/StaticLayout$Builder:setBreakStrategy	(I)Landroid/text/StaticLayout$Builder;
    //   585: iconst_0
    //   586: invokevirtual 2042	android/text/StaticLayout$Builder:setHyphenationFrequency	(I)Landroid/text/StaticLayout$Builder;
    //   589: getstatic 2048	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   592: invokevirtual 2052	android/text/StaticLayout$Builder:setAlignment	(Landroid/text/Layout$Alignment;)Landroid/text/StaticLayout$Builder;
    //   595: invokevirtual 2056	android/text/StaticLayout$Builder:build	()Landroid/text/StaticLayout;
    //   598: astore 8
    //   600: aload_0
    //   601: aload 8
    //   603: invokevirtual 2061	android/text/StaticLayout:getHeight	()I
    //   606: putfield 2063	org/telegram/messenger/MessageObject:textHeight	I
    //   609: aload_0
    //   610: aload 8
    //   612: invokevirtual 2066	android/text/StaticLayout:getLineCount	()I
    //   615: putfield 2068	org/telegram/messenger/MessageObject:linesCount	I
    //   618: getstatic 2029	android/os/Build$VERSION:SDK_INT	I
    //   621: bipush 24
    //   623: if_icmplt +554 -> 1177
    //   626: iconst_1
    //   627: istore 9
    //   629: iconst_0
    //   630: istore 10
    //   632: fconst_0
    //   633: fstore 7
    //   635: iconst_0
    //   636: istore 11
    //   638: iload 11
    //   640: iload 9
    //   642: if_icmpge -615 -> 27
    //   645: getstatic 2029	android/os/Build$VERSION:SDK_INT	I
    //   648: bipush 24
    //   650: if_icmplt +546 -> 1196
    //   653: aload_0
    //   654: getfield 2068	org/telegram/messenger/MessageObject:linesCount	I
    //   657: istore 4
    //   659: new 15	org/telegram/messenger/MessageObject$TextLayoutBlock
    //   662: dup
    //   663: invokespecial 2069	org/telegram/messenger/MessageObject$TextLayoutBlock:<init>	()V
    //   666: astore 12
    //   668: iload 9
    //   670: iconst_1
    //   671: if_icmpne +542 -> 1213
    //   674: aload 12
    //   676: aload 8
    //   678: putfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   681: aload 12
    //   683: fconst_0
    //   684: putfield 2076	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   687: aload 12
    //   689: iconst_0
    //   690: putfield 2079	org/telegram/messenger/MessageObject$TextLayoutBlock:charactersOffset	I
    //   693: aload 12
    //   695: aload_0
    //   696: getfield 2063	org/telegram/messenger/MessageObject:textHeight	I
    //   699: putfield 2082	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   702: iload 4
    //   704: istore 13
    //   706: aload_0
    //   707: getfield 1996	org/telegram/messenger/MessageObject:textLayoutBlocks	Ljava/util/ArrayList;
    //   710: aload 12
    //   712: invokevirtual 974	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   715: pop
    //   716: aload 12
    //   718: getfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   721: iload 13
    //   723: iconst_1
    //   724: isub
    //   725: invokevirtual 2086	android/text/StaticLayout:getLineLeft	(I)F
    //   728: fstore 14
    //   730: fload 14
    //   732: fstore 15
    //   734: iload 11
    //   736: ifne +24 -> 760
    //   739: fload 14
    //   741: fstore 15
    //   743: fload 14
    //   745: fconst_0
    //   746: fcmpl
    //   747: iflt +13 -> 760
    //   750: aload_0
    //   751: fload 14
    //   753: putfield 2088	org/telegram/messenger/MessageObject:textXOffset	F
    //   756: fload 14
    //   758: fstore 15
    //   760: aload 12
    //   762: getfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   765: iload 13
    //   767: iconst_1
    //   768: isub
    //   769: invokevirtual 2091	android/text/StaticLayout:getLineWidth	(I)F
    //   772: fstore 14
    //   774: fload 14
    //   776: f2d
    //   777: invokestatic 2095	java/lang/Math:ceil	(D)D
    //   780: d2i
    //   781: istore 4
    //   783: iload 11
    //   785: iload 9
    //   787: iconst_1
    //   788: isub
    //   789: if_icmpne +9 -> 798
    //   792: aload_0
    //   793: iload 4
    //   795: putfield 2097	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   798: fload 14
    //   800: fload 15
    //   802: fadd
    //   803: f2d
    //   804: invokestatic 2095	java/lang/Math:ceil	(D)D
    //   807: d2i
    //   808: istore 16
    //   810: iload 16
    //   812: istore 17
    //   814: iload 13
    //   816: iconst_1
    //   817: if_icmple +863 -> 1680
    //   820: iconst_0
    //   821: istore 18
    //   823: fconst_0
    //   824: fstore 15
    //   826: fconst_0
    //   827: fstore 14
    //   829: iconst_0
    //   830: istore_3
    //   831: iload 4
    //   833: istore 19
    //   835: iload_3
    //   836: iload 13
    //   838: if_icmpge +756 -> 1594
    //   841: aload 12
    //   843: getfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   846: iload_3
    //   847: invokevirtual 2091	android/text/StaticLayout:getLineWidth	(I)F
    //   850: fstore 20
    //   852: fload 20
    //   854: fstore 21
    //   856: fload 20
    //   858: iload_2
    //   859: bipush 20
    //   861: iadd
    //   862: i2f
    //   863: fcmpl
    //   864: ifle +7 -> 871
    //   867: iload_2
    //   868: i2f
    //   869: fstore 21
    //   871: aload 12
    //   873: getfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   876: iload_3
    //   877: invokevirtual 2086	android/text/StaticLayout:getLineLeft	(I)F
    //   880: fstore 20
    //   882: fload 20
    //   884: fconst_0
    //   885: fcmpl
    //   886: ifle +683 -> 1569
    //   889: aload_0
    //   890: aload_0
    //   891: getfield 2088	org/telegram/messenger/MessageObject:textXOffset	F
    //   894: fload 20
    //   896: invokestatic 2101	java/lang/Math:min	(FF)F
    //   899: putfield 2088	org/telegram/messenger/MessageObject:textXOffset	F
    //   902: aload 12
    //   904: aload 12
    //   906: getfield 2105	org/telegram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   909: iconst_1
    //   910: ior
    //   911: i2b
    //   912: i2b
    //   913: putfield 2105	org/telegram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   916: aload_0
    //   917: iconst_1
    //   918: putfield 2107	org/telegram/messenger/MessageObject:hasRtl	Z
    //   921: iload 18
    //   923: istore 4
    //   925: iload 18
    //   927: ifne +38 -> 965
    //   930: iload 18
    //   932: istore 4
    //   934: fload 20
    //   936: fconst_0
    //   937: fcmpl
    //   938: ifne +27 -> 965
    //   941: aload 12
    //   943: getfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   946: iload_3
    //   947: invokevirtual 2110	android/text/StaticLayout:getParagraphDirection	(I)I
    //   950: istore 22
    //   952: iload 18
    //   954: istore 4
    //   956: iload 22
    //   958: iconst_1
    //   959: if_icmpne +6 -> 965
    //   962: iconst_1
    //   963: istore 4
    //   965: fload 15
    //   967: fload 21
    //   969: invokestatic 2113	java/lang/Math:max	(FF)F
    //   972: fstore 15
    //   974: fload 14
    //   976: fload 21
    //   978: fload 20
    //   980: fadd
    //   981: invokestatic 2113	java/lang/Math:max	(FF)F
    //   984: fstore 14
    //   986: iload 19
    //   988: fload 21
    //   990: f2d
    //   991: invokestatic 2095	java/lang/Math:ceil	(D)D
    //   994: d2i
    //   995: invokestatic 2116	java/lang/Math:max	(II)I
    //   998: istore 19
    //   1000: iload 17
    //   1002: fload 21
    //   1004: fload 20
    //   1006: fadd
    //   1007: f2d
    //   1008: invokestatic 2095	java/lang/Math:ceil	(D)D
    //   1011: d2i
    //   1012: invokestatic 2116	java/lang/Math:max	(II)I
    //   1015: istore 17
    //   1017: iinc 3 1
    //   1020: iload 4
    //   1022: istore 18
    //   1024: goto -189 -> 835
    //   1027: iinc 3 1
    //   1030: goto -968 -> 62
    //   1033: aload_0
    //   1034: getfield 122	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1037: getfield 1029	org/telegram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   1040: invokevirtual 1970	java/util/ArrayList:isEmpty	()Z
    //   1043: ifne +9 -> 1052
    //   1046: iconst_1
    //   1047: istore 4
    //   1049: goto -950 -> 99
    //   1052: iconst_0
    //   1053: istore 4
    //   1055: goto -6 -> 1049
    //   1058: iconst_0
    //   1059: istore 5
    //   1061: goto -822 -> 239
    //   1064: aload_0
    //   1065: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1068: instanceof 293
    //   1071: ifeq -816 -> 255
    //   1074: aload_0
    //   1075: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1078: invokeinterface 299 1 0
    //   1083: sipush 1000
    //   1086: if_icmpge -831 -> 255
    //   1089: aload_0
    //   1090: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1093: checkcast 293	android/text/Spannable
    //   1096: iconst_4
    //   1097: invokestatic 1367	android/text/util/Linkify:addLinks	(Landroid/text/Spannable;I)Z
    //   1100: pop
    //   1101: goto -846 -> 255
    //   1104: astore 8
    //   1106: aload 8
    //   1108: invokestatic 1376	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1111: goto -856 -> 255
    //   1114: iconst_0
    //   1115: istore 4
    //   1117: goto -720 -> 397
    //   1120: getstatic 1901	org/telegram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1123: getfield 1906	android/graphics/Point:x	I
    //   1126: istore_3
    //   1127: goto -720 -> 407
    //   1130: ldc_w 2117
    //   1133: fstore 7
    //   1135: goto -699 -> 436
    //   1138: getstatic 811	org/telegram/ui/ActionBar/Theme:chat_msgTextPaint	Landroid/text/TextPaint;
    //   1141: astore_1
    //   1142: goto -588 -> 554
    //   1145: new 2058	android/text/StaticLayout
    //   1148: dup
    //   1149: aload_0
    //   1150: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1153: aload_1
    //   1154: iload_2
    //   1155: getstatic 2048	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1158: fconst_1
    //   1159: fconst_0
    //   1160: iconst_0
    //   1161: invokespecial 2120	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1164: astore 8
    //   1166: goto -566 -> 600
    //   1169: astore_1
    //   1170: aload_1
    //   1171: invokestatic 1376	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1174: goto -1147 -> 27
    //   1177: aload_0
    //   1178: getfield 2068	org/telegram/messenger/MessageObject:linesCount	I
    //   1181: i2f
    //   1182: ldc_w 2024
    //   1185: fdiv
    //   1186: f2d
    //   1187: invokestatic 2095	java/lang/Math:ceil	(D)D
    //   1190: d2i
    //   1191: istore 9
    //   1193: goto -564 -> 629
    //   1196: bipush 10
    //   1198: aload_0
    //   1199: getfield 2068	org/telegram/messenger/MessageObject:linesCount	I
    //   1202: iload 10
    //   1204: isub
    //   1205: invokestatic 2122	java/lang/Math:min	(II)I
    //   1208: istore 4
    //   1210: goto -551 -> 659
    //   1213: aload 8
    //   1215: iload 10
    //   1217: invokevirtual 2125	android/text/StaticLayout:getLineStart	(I)I
    //   1220: istore 13
    //   1222: aload 8
    //   1224: iload 10
    //   1226: iload 4
    //   1228: iadd
    //   1229: iconst_1
    //   1230: isub
    //   1231: invokevirtual 2128	android/text/StaticLayout:getLineEnd	(I)I
    //   1234: istore_3
    //   1235: iload_3
    //   1236: iload 13
    //   1238: if_icmpge +9 -> 1247
    //   1241: iinc 11 1
    //   1244: goto -606 -> 638
    //   1247: aload 12
    //   1249: iload 13
    //   1251: putfield 2079	org/telegram/messenger/MessageObject$TextLayoutBlock:charactersOffset	I
    //   1254: aload 12
    //   1256: iload_3
    //   1257: putfield 2131	org/telegram/messenger/MessageObject$TextLayoutBlock:charactersEnd	I
    //   1260: iload 6
    //   1262: ifeq +200 -> 1462
    //   1265: getstatic 2029	android/os/Build$VERSION:SDK_INT	I
    //   1268: bipush 24
    //   1270: if_icmplt +192 -> 1462
    //   1273: aload 12
    //   1275: aload_0
    //   1276: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1279: iload 13
    //   1281: iload_3
    //   1282: aload_1
    //   1283: fconst_2
    //   1284: invokestatic 281	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1287: iload_2
    //   1288: iadd
    //   1289: invokestatic 2035	android/text/StaticLayout$Builder:obtain	(Ljava/lang/CharSequence;IILandroid/text/TextPaint;I)Landroid/text/StaticLayout$Builder;
    //   1292: iconst_1
    //   1293: invokevirtual 2039	android/text/StaticLayout$Builder:setBreakStrategy	(I)Landroid/text/StaticLayout$Builder;
    //   1296: iconst_0
    //   1297: invokevirtual 2042	android/text/StaticLayout$Builder:setHyphenationFrequency	(I)Landroid/text/StaticLayout$Builder;
    //   1300: getstatic 2048	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1303: invokevirtual 2052	android/text/StaticLayout$Builder:setAlignment	(Landroid/text/Layout$Alignment;)Landroid/text/StaticLayout$Builder;
    //   1306: invokevirtual 2056	android/text/StaticLayout$Builder:build	()Landroid/text/StaticLayout;
    //   1309: putfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1312: aload 12
    //   1314: aload 8
    //   1316: iload 10
    //   1318: invokevirtual 2134	android/text/StaticLayout:getLineTop	(I)I
    //   1321: i2f
    //   1322: putfield 2076	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   1325: iload 11
    //   1327: ifeq +17 -> 1344
    //   1330: aload 12
    //   1332: aload 12
    //   1334: getfield 2076	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   1337: fload 7
    //   1339: fsub
    //   1340: f2i
    //   1341: putfield 2082	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   1344: aload 12
    //   1346: aload 12
    //   1348: getfield 2082	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   1351: aload 12
    //   1353: getfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1356: aload 12
    //   1358: getfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1361: invokevirtual 2066	android/text/StaticLayout:getLineCount	()I
    //   1364: iconst_1
    //   1365: isub
    //   1366: invokevirtual 2137	android/text/StaticLayout:getLineBottom	(I)I
    //   1369: invokestatic 2116	java/lang/Math:max	(II)I
    //   1372: putfield 2082	org/telegram/messenger/MessageObject$TextLayoutBlock:height	I
    //   1375: aload 12
    //   1377: getfield 2076	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   1380: fstore 15
    //   1382: iload 4
    //   1384: istore 13
    //   1386: fload 15
    //   1388: fstore 7
    //   1390: iload 11
    //   1392: iload 9
    //   1394: iconst_1
    //   1395: isub
    //   1396: if_icmpne -690 -> 706
    //   1399: iload 4
    //   1401: aload 12
    //   1403: getfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1406: invokevirtual 2066	android/text/StaticLayout:getLineCount	()I
    //   1409: invokestatic 2116	java/lang/Math:max	(II)I
    //   1412: istore 13
    //   1414: aload_0
    //   1415: aload_0
    //   1416: getfield 2063	org/telegram/messenger/MessageObject:textHeight	I
    //   1419: aload 12
    //   1421: getfield 2076	org/telegram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   1424: aload 12
    //   1426: getfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1429: invokevirtual 2061	android/text/StaticLayout:getHeight	()I
    //   1432: i2f
    //   1433: fadd
    //   1434: f2i
    //   1435: invokestatic 2116	java/lang/Math:max	(II)I
    //   1438: putfield 2063	org/telegram/messenger/MessageObject:textHeight	I
    //   1441: fload 15
    //   1443: fstore 7
    //   1445: goto -739 -> 706
    //   1448: astore 23
    //   1450: aload 23
    //   1452: invokestatic 1376	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1455: fload 15
    //   1457: fstore 7
    //   1459: goto -753 -> 706
    //   1462: new 2058	android/text/StaticLayout
    //   1465: astore 23
    //   1467: aload 23
    //   1469: aload_0
    //   1470: getfield 120	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1473: iload 13
    //   1475: iload_3
    //   1476: aload_1
    //   1477: iload_2
    //   1478: getstatic 2048	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1481: fconst_1
    //   1482: fconst_0
    //   1483: iconst_0
    //   1484: invokespecial 2140	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;IILandroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1487: aload 12
    //   1489: aload 23
    //   1491: putfield 2073	org/telegram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1494: goto -182 -> 1312
    //   1497: astore 12
    //   1499: aload 12
    //   1501: invokestatic 1376	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1504: goto -263 -> 1241
    //   1507: astore 23
    //   1509: fconst_0
    //   1510: fstore 15
    //   1512: iload 11
    //   1514: ifne +8 -> 1522
    //   1517: aload_0
    //   1518: fconst_0
    //   1519: putfield 2088	org/telegram/messenger/MessageObject:textXOffset	F
    //   1522: aload 23
    //   1524: invokestatic 1376	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1527: goto -767 -> 760
    //   1530: astore 23
    //   1532: fconst_0
    //   1533: fstore 14
    //   1535: aload 23
    //   1537: invokestatic 1376	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1540: goto -766 -> 774
    //   1543: astore 23
    //   1545: aload 23
    //   1547: invokestatic 1376	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1550: fconst_0
    //   1551: fstore 20
    //   1553: goto -701 -> 852
    //   1556: astore 23
    //   1558: aload 23
    //   1560: invokestatic 1376	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1563: fconst_0
    //   1564: fstore 20
    //   1566: goto -684 -> 882
    //   1569: aload 12
    //   1571: aload 12
    //   1573: getfield 2105	org/telegram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   1576: iconst_2
    //   1577: ior
    //   1578: i2b
    //   1579: i2b
    //   1580: putfield 2105	org/telegram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   1583: goto -662 -> 921
    //   1586: astore 23
    //   1588: iconst_1
    //   1589: istore 4
    //   1591: goto -626 -> 965
    //   1594: iload 18
    //   1596: ifeq +58 -> 1654
    //   1599: fload 14
    //   1601: fstore 15
    //   1603: fload 15
    //   1605: fstore 14
    //   1607: iload 11
    //   1609: iload 9
    //   1611: iconst_1
    //   1612: isub
    //   1613: if_icmpne +13 -> 1626
    //   1616: aload_0
    //   1617: iload 16
    //   1619: putfield 2097	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   1622: fload 15
    //   1624: fstore 14
    //   1626: aload_0
    //   1627: aload_0
    //   1628: getfield 1998	org/telegram/messenger/MessageObject:textWidth	I
    //   1631: fload 14
    //   1633: f2d
    //   1634: invokestatic 2095	java/lang/Math:ceil	(D)D
    //   1637: d2i
    //   1638: invokestatic 2116	java/lang/Math:max	(II)I
    //   1641: putfield 1998	org/telegram/messenger/MessageObject:textWidth	I
    //   1644: iload 10
    //   1646: iload 13
    //   1648: iadd
    //   1649: istore 10
    //   1651: goto -410 -> 1241
    //   1654: fload 15
    //   1656: fstore 14
    //   1658: iload 11
    //   1660: iload 9
    //   1662: iconst_1
    //   1663: isub
    //   1664: if_icmpne -38 -> 1626
    //   1667: aload_0
    //   1668: iload 19
    //   1670: putfield 2097	org/telegram/messenger/MessageObject:lastLineWidth	I
    //   1673: fload 15
    //   1675: fstore 14
    //   1677: goto -51 -> 1626
    //   1680: fload 15
    //   1682: fconst_0
    //   1683: fcmpl
    //   1684: ifle +94 -> 1778
    //   1687: aload_0
    //   1688: aload_0
    //   1689: getfield 2088	org/telegram/messenger/MessageObject:textXOffset	F
    //   1692: fload 15
    //   1694: invokestatic 2101	java/lang/Math:min	(FF)F
    //   1697: putfield 2088	org/telegram/messenger/MessageObject:textXOffset	F
    //   1700: iload 4
    //   1702: istore_3
    //   1703: aload_0
    //   1704: getfield 2088	org/telegram/messenger/MessageObject:textXOffset	F
    //   1707: fconst_0
    //   1708: fcmpl
    //   1709: ifne +11 -> 1720
    //   1712: iload 4
    //   1714: i2f
    //   1715: fload 15
    //   1717: fadd
    //   1718: f2i
    //   1719: istore_3
    //   1720: iload 9
    //   1722: iconst_1
    //   1723: if_icmpeq +49 -> 1772
    //   1726: iconst_1
    //   1727: istore 5
    //   1729: aload_0
    //   1730: iload 5
    //   1732: putfield 2107	org/telegram/messenger/MessageObject:hasRtl	Z
    //   1735: aload 12
    //   1737: aload 12
    //   1739: getfield 2105	org/telegram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   1742: iconst_1
    //   1743: ior
    //   1744: i2b
    //   1745: i2b
    //   1746: putfield 2105	org/telegram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   1749: iload_3
    //   1750: istore 4
    //   1752: aload_0
    //   1753: aload_0
    //   1754: getfield 1998	org/telegram/messenger/MessageObject:textWidth	I
    //   1757: iload_2
    //   1758: iload 4
    //   1760: invokestatic 2122	java/lang/Math:min	(II)I
    //   1763: invokestatic 2116	java/lang/Math:max	(II)I
    //   1766: putfield 1998	org/telegram/messenger/MessageObject:textWidth	I
    //   1769: goto -125 -> 1644
    //   1772: iconst_0
    //   1773: istore 5
    //   1775: goto -46 -> 1729
    //   1778: aload 12
    //   1780: aload 12
    //   1782: getfield 2105	org/telegram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   1785: iconst_2
    //   1786: ior
    //   1787: i2b
    //   1788: i2b
    //   1789: putfield 2105	org/telegram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   1792: goto -40 -> 1752
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1795	0	this	MessageObject
    //   0	1795	1	paramUser	TLRPC.User
    //   59	1699	2	i	int
    //   61	1689	3	j	int
    //   63	1696	4	k	int
    //   237	1537	5	bool1	boolean
    //   265	996	6	bool2	boolean
    //   434	1024	7	f1	float
    //   598	79	8	localStaticLayout1	StaticLayout
    //   1104	3	8	localThrowable	Throwable
    //   1164	151	8	localStaticLayout2	StaticLayout
    //   627	1097	9	m	int
    //   630	1020	10	n	int
    //   636	1029	11	i1	int
    //   666	822	12	localTextLayoutBlock	TextLayoutBlock
    //   1497	284	12	localException1	Exception
    //   704	945	13	i2	int
    //   728	948	14	f2	float
    //   732	984	15	f3	float
    //   808	810	16	i3	int
    //   812	204	17	i4	int
    //   821	774	18	i5	int
    //   833	836	19	i6	int
    //   850	715	20	f4	float
    //   854	149	21	f5	float
    //   950	10	22	i7	int
    //   1448	3	23	localException2	Exception
    //   1465	25	23	localStaticLayout3	StaticLayout
    //   1507	16	23	localException3	Exception
    //   1530	6	23	localException4	Exception
    //   1543	3	23	localException5	Exception
    //   1556	3	23	localException6	Exception
    //   1586	1	23	localException7	Exception
    // Exception table:
    //   from	to	target	type
    //   1089	1101	1104	java/lang/Throwable
    //   554	600	1169	java/lang/Exception
    //   1145	1166	1169	java/lang/Exception
    //   1414	1441	1448	java/lang/Exception
    //   1265	1312	1497	java/lang/Exception
    //   1312	1325	1497	java/lang/Exception
    //   1330	1344	1497	java/lang/Exception
    //   1344	1382	1497	java/lang/Exception
    //   1462	1494	1497	java/lang/Exception
    //   716	730	1507	java/lang/Exception
    //   750	756	1507	java/lang/Exception
    //   760	774	1530	java/lang/Exception
    //   841	852	1543	java/lang/Exception
    //   871	882	1556	java/lang/Exception
    //   941	952	1586	java/lang/Exception
  }
  
  public void generateLinkDescription()
  {
    if (this.linkDescription != null) {}
    for (;;)
    {
      return;
      if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && ((this.messageOwner.media.webpage instanceof TLRPC.TL_webPage)) && (this.messageOwner.media.webpage.description != null))
      {
        this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.webpage.description);
        label76:
        if (this.linkDescription == null) {
          continue;
        }
        if (!containsUrls(this.linkDescription)) {}
      }
      try
      {
        Linkify.addLinks((Spannable)this.linkDescription, 1);
        this.linkDescription = Emoji.replaceEmoji(this.linkDescription, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        continue;
        if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) && (this.messageOwner.media.game.description != null))
        {
          this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.game.description);
          break label76;
        }
        if ((!(this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) || (this.messageOwner.media.description == null)) {
          break label76;
        }
        this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.description);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
  }
  
  public void generatePaymentSentMessageText(TLRPC.User paramUser)
  {
    TLRPC.User localUser = paramUser;
    if (paramUser == null) {
      localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int)getDialogId()));
    }
    if (localUser != null)
    {
      paramUser = UserObject.getFirstName(localUser);
      if ((this.replyMessageObject == null) || (!(this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice))) {
        break label130;
      }
    }
    label130:
    for (this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", NUM, new Object[] { LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), paramUser, this.replyMessageObject.messageOwner.media.title });; this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", NUM, new Object[] { LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), paramUser }))
    {
      return;
      paramUser = "";
      break;
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
          paramUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        localObject = paramUser;
        localChat = paramChat;
        if (paramUser == null)
        {
          localChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
          localObject = paramUser;
        }
      }
    }
    if ((this.replyMessageObject == null) || ((this.replyMessageObject.messageOwner instanceof TLRPC.TL_messageEmpty)) || ((this.replyMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionHistoryClear)))
    {
      paramUser = LocaleController.getString("ActionPinnedNoText", NUM);
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
      paramUser = LocaleController.getString("ActionPinnedMusic", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isVideo())
    {
      paramUser = LocaleController.getString("ActionPinnedVideo", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isGif())
    {
      paramUser = LocaleController.getString("ActionPinnedGif", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isVoice())
    {
      paramUser = LocaleController.getString("ActionPinnedVoice", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isRoundVideo())
    {
      paramUser = LocaleController.getString("ActionPinnedRound", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isSticker())
    {
      paramUser = LocaleController.getString("ActionPinnedSticker", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
    {
      paramUser = LocaleController.getString("ActionPinnedFile", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo))
    {
      paramUser = LocaleController.getString("ActionPinnedGeo", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive))
    {
      paramUser = LocaleController.getString("ActionPinnedGeoLive", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
    {
      paramUser = LocaleController.getString("ActionPinnedContact", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      paramUser = LocaleController.getString("ActionPinnedPhoto", NUM);
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
    {
      paramUser = LocaleController.formatString("ActionPinnedGame", NUM, new Object[] { "🎮 " + this.replyMessageObject.messageOwner.media.game.title });
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        this.messageText = Emoji.replaceEmoji(this.messageText, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        break;
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
      paramUser = LocaleController.formatString("ActionPinnedText", NUM, new Object[] { Emoji.replaceEmoji(paramUser, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false) });
      if (localObject != null) {}
      for (;;)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        break;
        localObject = localChat;
      }
    }
    paramUser = LocaleController.getString("ActionPinnedNoText", NUM);
    if (localObject != null) {}
    for (;;)
    {
      this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
      break;
      localObject = localChat;
    }
  }
  
  public void generateThumbs(boolean paramBoolean)
  {
    if ((this.messageOwner instanceof TLRPC.TL_messageService)) {
      if ((this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto))
      {
        if (!paramBoolean) {
          this.photoThumbs = new ArrayList(this.messageOwner.action.photo.sizes);
        }
      }
      else
      {
        return;
        break label741;
      }
    }
    for (;;)
    {
      label51:
      if ((this.photoThumbs != null) && (!this.photoThumbs.isEmpty()))
      {
        TLRPC.PhotoSize localPhotoSize1;
        int j;
        TLRPC.PhotoSize localPhotoSize2;
        for (int i = 0; i < this.photoThumbs.size(); i++)
        {
          localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
          j = 0;
          if (j < this.messageOwner.action.photo.sizes.size())
          {
            localPhotoSize2 = (TLRPC.PhotoSize)this.messageOwner.action.photo.sizes.get(j);
            if ((localPhotoSize2 instanceof TLRPC.TL_photoSizeEmpty)) {}
            while (!localPhotoSize2.type.equals(localPhotoSize1.type))
            {
              j++;
              break;
            }
            localPhotoSize1.location = localPhotoSize2.location;
          }
        }
        continue;
        if ((this.messageOwner.media != null) && (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty))) {
          if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
          {
            if ((!paramBoolean) || ((this.photoThumbs != null) && (this.photoThumbs.size() != this.messageOwner.media.photo.sizes.size()))) {
              this.photoThumbs = new ArrayList(this.messageOwner.media.photo.sizes);
            } else if ((this.photoThumbs != null) && (!this.photoThumbs.isEmpty())) {
              for (i = 0; i < this.photoThumbs.size(); i++)
              {
                localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
                j = 0;
                if (j < this.messageOwner.media.photo.sizes.size())
                {
                  localPhotoSize2 = (TLRPC.PhotoSize)this.messageOwner.media.photo.sizes.get(j);
                  if ((localPhotoSize2 instanceof TLRPC.TL_photoSizeEmpty)) {}
                  while (!localPhotoSize2.type.equals(localPhotoSize1.type))
                  {
                    j++;
                    break;
                  }
                  localPhotoSize1.location = localPhotoSize2.location;
                }
              }
            }
          }
          else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
          {
            if (!(this.messageOwner.media.document.thumb instanceof TLRPC.TL_photoSizeEmpty)) {
              if (!paramBoolean)
              {
                this.photoThumbs = new ArrayList();
                this.photoThumbs.add(this.messageOwner.media.document.thumb);
              }
              else if ((this.photoThumbs != null) && (!this.photoThumbs.isEmpty()) && (this.messageOwner.media.document.thumb != null))
              {
                localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(0);
                localPhotoSize1.location = this.messageOwner.media.document.thumb.location;
                localPhotoSize1.w = this.messageOwner.media.document.thumb.w;
                localPhotoSize1.h = this.messageOwner.media.document.thumb.h;
              }
            }
          }
          else
          {
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
              else {
                label687:
                if (this.messageOwner.media.game.photo != null)
                {
                  if ((paramBoolean) && (this.photoThumbs2 != null)) {
                    break label843;
                  }
                  this.photoThumbs2 = new ArrayList(this.messageOwner.media.game.photo.sizes);
                }
              }
              for (;;)
              {
                label741:
                if ((this.photoThumbs != null) || (this.photoThumbs2 == null)) {
                  break label51;
                }
                this.photoThumbs = this.photoThumbs2;
                this.photoThumbs2 = null;
                break label51;
                if ((this.photoThumbs == null) || (this.photoThumbs.isEmpty()) || (this.messageOwner.media.game.document.thumb == null)) {
                  break label687;
                }
                ((TLRPC.PhotoSize)this.photoThumbs.get(0)).location = this.messageOwner.media.game.document.thumb.location;
                break label687;
                label843:
                if (this.photoThumbs2.isEmpty()) {
                  break;
                }
                for (i = 0; i < this.photoThumbs2.size(); i++)
                {
                  localPhotoSize2 = (TLRPC.PhotoSize)this.photoThumbs2.get(i);
                  j = 0;
                  if (j < this.messageOwner.media.game.photo.sizes.size())
                  {
                    localPhotoSize1 = (TLRPC.PhotoSize)this.messageOwner.media.game.photo.sizes.get(j);
                    if ((localPhotoSize1 instanceof TLRPC.TL_photoSizeEmpty)) {}
                    while (!localPhotoSize1.type.equals(localPhotoSize2.type))
                    {
                      j++;
                      break;
                    }
                    localPhotoSize2.location = localPhotoSize1.location;
                  }
                }
              }
            }
            if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
              if (this.messageOwner.media.webpage.photo != null)
              {
                if ((!paramBoolean) || (this.photoThumbs == null)) {
                  this.photoThumbs = new ArrayList(this.messageOwner.media.webpage.photo.sizes);
                } else if (!this.photoThumbs.isEmpty()) {
                  for (i = 0; i < this.photoThumbs.size(); i++)
                  {
                    localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
                    j = 0;
                    if (j < this.messageOwner.media.webpage.photo.sizes.size())
                    {
                      localPhotoSize2 = (TLRPC.PhotoSize)this.messageOwner.media.webpage.photo.sizes.get(j);
                      if ((localPhotoSize2 instanceof TLRPC.TL_photoSizeEmpty)) {}
                      while (!localPhotoSize2.type.equals(localPhotoSize1.type))
                      {
                        j++;
                        break;
                      }
                      localPhotoSize1.location = localPhotoSize2.location;
                    }
                  }
                }
              }
              else if ((this.messageOwner.media.webpage.document != null) && (!(this.messageOwner.media.webpage.document.thumb instanceof TLRPC.TL_photoSizeEmpty))) {
                if (!paramBoolean)
                {
                  this.photoThumbs = new ArrayList();
                  this.photoThumbs.add(this.messageOwner.media.webpage.document.thumb);
                }
                else if ((this.photoThumbs != null) && (!this.photoThumbs.isEmpty()) && (this.messageOwner.media.webpage.document.thumb != null))
                {
                  ((TLRPC.PhotoSize)this.photoThumbs.get(0)).location = this.messageOwner.media.webpage.document.thumb.location;
                }
              }
            }
          }
        }
      }
    }
  }
  
  public int getApproximateHeight()
  {
    int i;
    if (this.type == 0)
    {
      i = this.textHeight;
      if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && ((this.messageOwner.media.webpage instanceof TLRPC.TL_webPage)))
      {
        j = AndroidUtilities.dp(100.0F);
        i += j;
        j = i;
        if (isReply()) {
          j = i + AndroidUtilities.dp(42.0F);
        }
      }
    }
    for (;;)
    {
      return j;
      j = 0;
      break;
      if (this.type == 2)
      {
        j = AndroidUtilities.dp(72.0F);
      }
      else if (this.type == 12)
      {
        j = AndroidUtilities.dp(71.0F);
      }
      else if (this.type == 9)
      {
        j = AndroidUtilities.dp(100.0F);
      }
      else if (this.type == 4)
      {
        j = AndroidUtilities.dp(114.0F);
      }
      else if (this.type == 14)
      {
        j = AndroidUtilities.dp(82.0F);
      }
      else if (this.type == 10)
      {
        j = AndroidUtilities.dp(30.0F);
      }
      else if (this.type == 11)
      {
        j = AndroidUtilities.dp(50.0F);
      }
      else
      {
        if (this.type != 5) {
          break label223;
        }
        j = AndroidUtilities.roundMessageSize;
      }
    }
    label223:
    float f2;
    Object localObject;
    int m;
    if (this.type == 13)
    {
      float f1 = AndroidUtilities.displaySize.y * 0.4F;
      if (AndroidUtilities.isTablet()) {}
      for (f2 = AndroidUtilities.getMinTabletSide() * 0.5F;; f2 = AndroidUtilities.displaySize.x * 0.5F)
      {
        i = 0;
        int k = 0;
        localObject = this.messageOwner.media.document.attributes.iterator();
        TLRPC.DocumentAttribute localDocumentAttribute;
        do
        {
          m = i;
          j = k;
          if (!((Iterator)localObject).hasNext()) {
            break;
          }
          localDocumentAttribute = (TLRPC.DocumentAttribute)((Iterator)localObject).next();
        } while (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeImageSize));
        j = localDocumentAttribute.w;
        m = localDocumentAttribute.h;
        i = j;
        if (j == 0)
        {
          m = (int)f1;
          i = m + AndroidUtilities.dp(100.0F);
        }
        j = m;
        k = i;
        if (m > f1)
        {
          k = (int)(i * (f1 / m));
          j = (int)f1;
        }
        i = j;
        if (k > f2) {
          i = (int)(j * (f2 / k));
        }
        j = i + AndroidUtilities.dp(14.0F);
        break;
      }
    }
    if (AndroidUtilities.isTablet())
    {
      j = (int)(AndroidUtilities.getMinTabletSide() * 0.7F);
      label446:
      m = j + AndroidUtilities.dp(100.0F);
      i = j;
      if (j > AndroidUtilities.getPhotoSize()) {
        i = AndroidUtilities.getPhotoSize();
      }
      j = m;
      if (m > AndroidUtilities.getPhotoSize()) {
        j = AndroidUtilities.getPhotoSize();
      }
      localObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
      m = j;
      if (localObject != null)
      {
        f2 = ((TLRPC.PhotoSize)localObject).w / i;
        m = (int)(((TLRPC.PhotoSize)localObject).h / f2);
        i = m;
        if (m == 0) {
          i = AndroidUtilities.dp(100.0F);
        }
        if (i <= j) {
          break label611;
        }
        label547:
        if (needDrawBluredPreview()) {
          if (!AndroidUtilities.isTablet()) {
            break label633;
          }
        }
      }
    }
    label611:
    label633:
    for (int j = (int)(AndroidUtilities.getMinTabletSide() * 0.5F);; j = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5F))
    {
      m = j;
      j = m + AndroidUtilities.dp(14.0F);
      break;
      j = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7F);
      break label446;
      j = i;
      if (i >= AndroidUtilities.dp(120.0F)) {
        break label547;
      }
      j = AndroidUtilities.dp(120.0F);
      break label547;
    }
  }
  
  public int getChannelId()
  {
    if (this.messageOwner.to_id != null) {}
    for (int i = this.messageOwner.to_id.channel_id;; i = 0) {
      return i;
    }
  }
  
  public long getDialogId()
  {
    return getDialogId(this.messageOwner);
  }
  
  public TLRPC.Document getDocument()
  {
    TLRPC.Document localDocument;
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
      localDocument = this.messageOwner.media.webpage.document;
    }
    for (;;)
    {
      return localDocument;
      if (this.messageOwner.media != null) {
        localDocument = this.messageOwner.media.document;
      } else {
        localDocument = null;
      }
    }
  }
  
  public String getDocumentName()
  {
    String str;
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
      str = FileLoader.getDocumentFileName(this.messageOwner.media.document);
    }
    for (;;)
    {
      return str;
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) {
        str = FileLoader.getDocumentFileName(this.messageOwner.media.webpage.document);
      } else {
        str = "";
      }
    }
  }
  
  public int getDuration()
  {
    TLRPC.Document localDocument;
    int i;
    label23:
    TLRPC.DocumentAttribute localDocumentAttribute;
    if (this.type == 0)
    {
      localDocument = this.messageOwner.media.webpage.document;
      i = 0;
      if (i >= localDocument.attributes.size()) {
        break label95;
      }
      localDocumentAttribute = (TLRPC.DocumentAttribute)localDocument.attributes.get(i);
      if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio)) {
        break label74;
      }
      i = localDocumentAttribute.duration;
    }
    for (;;)
    {
      return i;
      localDocument = this.messageOwner.media.document;
      break;
      label74:
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo))
      {
        i = localDocumentAttribute.duration;
      }
      else
      {
        i++;
        break label23;
        label95:
        i = this.audioPlayerDuration;
      }
    }
  }
  
  public String getExtension()
  {
    Object localObject1 = getFileName();
    int i = ((String)localObject1).lastIndexOf('.');
    Object localObject2 = null;
    if (i != -1) {
      localObject2 = ((String)localObject1).substring(i + 1);
    }
    if (localObject2 != null)
    {
      localObject1 = localObject2;
      if (((String)localObject2).length() != 0) {}
    }
    else
    {
      localObject1 = this.messageOwner.media.document.mime_type;
    }
    localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = "";
    }
    return ((String)localObject2).toUpperCase();
  }
  
  public String getFileName()
  {
    Object localObject;
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
      localObject = FileLoader.getAttachFileName(this.messageOwner.media.document);
    }
    for (;;)
    {
      return (String)localObject;
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
      {
        localObject = this.messageOwner.media.photo.sizes;
        if (((ArrayList)localObject).size() > 0)
        {
          localObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, AndroidUtilities.getPhotoSize());
          if (localObject != null) {
            localObject = FileLoader.getAttachFileName((TLObject)localObject);
          }
        }
      }
      else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
      {
        localObject = FileLoader.getAttachFileName(this.messageOwner.media.webpage.document);
        continue;
      }
      localObject = "";
    }
  }
  
  public int getFileType()
  {
    int i;
    if (isVideo()) {
      i = 2;
    }
    for (;;)
    {
      return i;
      if (isVoice()) {
        i = 1;
      } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
        i = 3;
      } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) {
        i = 0;
      } else {
        i = 4;
      }
    }
  }
  
  public String getForwardedName()
  {
    Object localObject;
    if (this.messageOwner.fwd_from != null) {
      if (this.messageOwner.fwd_from.channel_id != 0)
      {
        localObject = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
        if (localObject == null) {
          break label107;
        }
        localObject = ((TLRPC.Chat)localObject).title;
      }
    }
    for (;;)
    {
      return (String)localObject;
      if (this.messageOwner.fwd_from.from_id != 0)
      {
        localObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
        if (localObject != null)
        {
          localObject = UserObject.getUserName((TLRPC.User)localObject);
          continue;
        }
      }
      label107:
      localObject = null;
    }
  }
  
  public int getFromId()
  {
    int i;
    if ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.saved_from_peer != null)) {
      if (this.messageOwner.fwd_from.saved_from_peer.user_id != 0) {
        if (this.messageOwner.fwd_from.from_id != 0) {
          i = this.messageOwner.fwd_from.from_id;
        }
      }
    }
    for (;;)
    {
      return i;
      i = this.messageOwner.fwd_from.saved_from_peer.user_id;
      continue;
      if (this.messageOwner.fwd_from.saved_from_peer.channel_id != 0)
      {
        if ((isSavedFromMegagroup()) && (this.messageOwner.fwd_from.from_id != 0)) {
          i = this.messageOwner.fwd_from.from_id;
        } else if (this.messageOwner.fwd_from.channel_id != 0) {
          i = -this.messageOwner.fwd_from.channel_id;
        } else {
          i = -this.messageOwner.fwd_from.saved_from_peer.channel_id;
        }
      }
      else
      {
        if (this.messageOwner.fwd_from.saved_from_peer.chat_id != 0)
        {
          if (this.messageOwner.fwd_from.from_id != 0)
          {
            i = this.messageOwner.fwd_from.from_id;
            continue;
          }
          if (this.messageOwner.fwd_from.channel_id != 0)
          {
            i = -this.messageOwner.fwd_from.channel_id;
            continue;
          }
          i = -this.messageOwner.fwd_from.saved_from_peer.chat_id;
          continue;
          if (this.messageOwner.from_id != 0)
          {
            i = this.messageOwner.from_id;
            continue;
          }
          if (this.messageOwner.post)
          {
            i = this.messageOwner.to_id.channel_id;
            continue;
          }
        }
        i = 0;
      }
    }
  }
  
  public long getGroupId()
  {
    if (this.localGroupId != 0L) {}
    for (long l = this.localGroupId;; l = this.messageOwner.grouped_id) {
      return l;
    }
  }
  
  public int getId()
  {
    return this.messageOwner.id;
  }
  
  public long getIdWithChannel()
  {
    long l1 = this.messageOwner.id;
    long l2 = l1;
    if (this.messageOwner.to_id != null)
    {
      l2 = l1;
      if (this.messageOwner.to_id.channel_id != 0) {
        l2 = l1 | this.messageOwner.to_id.channel_id << 32;
      }
    }
    return l2;
  }
  
  public TLRPC.InputStickerSet getInputStickerSet()
  {
    return getInputStickerSet(this.messageOwner);
  }
  
  public String getMimeType()
  {
    Object localObject;
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
      localObject = this.messageOwner.media.document.mime_type;
    }
    for (;;)
    {
      return (String)localObject;
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice))
      {
        localObject = ((TLRPC.TL_messageMediaInvoice)this.messageOwner.media).photo;
        if (localObject != null) {
          localObject = ((TLRPC.WebDocument)localObject).mime_type;
        }
      }
      else
      {
        if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
        {
          localObject = "image/jpeg";
          continue;
        }
        if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
        {
          if (this.messageOwner.media.webpage.document != null)
          {
            localObject = this.messageOwner.media.document.mime_type;
            continue;
          }
          if (this.messageOwner.media.webpage.photo != null)
          {
            localObject = "image/jpeg";
            continue;
          }
        }
      }
      localObject = "";
    }
  }
  
  public String getMusicAuthor()
  {
    return getMusicAuthor(true);
  }
  
  public String getMusicAuthor(boolean paramBoolean)
  {
    TLRPC.Document localDocument;
    int i;
    int j;
    label26:
    Object localObject1;
    int k;
    if (this.type == 0)
    {
      localDocument = this.messageOwner.media.webpage.document;
      i = 0;
      j = 0;
      if (j >= localDocument.attributes.size()) {
        break label428;
      }
      localObject1 = (TLRPC.DocumentAttribute)localDocument.attributes.get(j);
      if (!(localObject1 instanceof TLRPC.TL_documentAttributeAudio)) {
        break label141;
      }
      if (!((TLRPC.DocumentAttribute)localObject1).voice) {
        break label100;
      }
      k = 1;
      label71:
      if (k == 0) {
        break label419;
      }
      if (paramBoolean) {
        break label169;
      }
      localObject1 = null;
    }
    for (;;)
    {
      return (String)localObject1;
      localDocument = this.messageOwner.media.document;
      break;
      label100:
      Object localObject2 = ((TLRPC.DocumentAttribute)localObject1).performer;
      localObject1 = localObject2;
      if (TextUtils.isEmpty((CharSequence)localObject2))
      {
        localObject1 = localObject2;
        if (paramBoolean)
        {
          localObject1 = LocaleController.getString("AudioUnknownArtist", NUM);
          continue;
          label141:
          k = i;
          if (!(localObject1 instanceof TLRPC.TL_documentAttributeVideo)) {
            break label71;
          }
          k = i;
          if (!((TLRPC.DocumentAttribute)localObject1).round_message) {
            break label71;
          }
          k = 1;
          break label71;
          label169:
          if ((isOutOwner()) || ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId())))
          {
            localObject1 = LocaleController.getString("FromYou", NUM);
          }
          else
          {
            localObject2 = null;
            localObject1 = null;
            if ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.channel_id != 0)) {
              localObject1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
            }
            for (;;)
            {
              if (localObject2 == null) {
                break label404;
              }
              localObject1 = UserObject.getUserName((TLRPC.User)localObject2);
              break;
              if ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.from_id != 0)) {
                localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
              } else if (this.messageOwner.from_id < 0) {
                localObject1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-this.messageOwner.from_id));
              } else {
                localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
              }
            }
            label404:
            if (localObject1 != null)
            {
              localObject1 = ((TLRPC.Chat)localObject1).title;
            }
            else
            {
              label419:
              j++;
              i = k;
              break label26;
              label428:
              localObject1 = LocaleController.getString("AudioUnknownArtist", NUM);
            }
          }
        }
      }
    }
  }
  
  public String getMusicTitle()
  {
    return getMusicTitle(true);
  }
  
  public String getMusicTitle(boolean paramBoolean)
  {
    Object localObject1;
    int i;
    label23:
    Object localObject2;
    if (this.type == 0)
    {
      localObject1 = this.messageOwner.media.webpage.document;
      i = 0;
      if (i >= ((TLRPC.Document)localObject1).attributes.size()) {
        break label201;
      }
      localObject2 = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject1).attributes.get(i);
      if (!(localObject2 instanceof TLRPC.TL_documentAttributeAudio)) {
        break label163;
      }
      if (!((TLRPC.DocumentAttribute)localObject2).voice) {
        break label103;
      }
      if (paramBoolean) {
        break label87;
      }
      localObject2 = null;
    }
    for (;;)
    {
      return (String)localObject2;
      localObject1 = this.messageOwner.media.document;
      break;
      label87:
      localObject2 = LocaleController.formatDateAudio(this.messageOwner.date);
      continue;
      label103:
      String str = ((TLRPC.DocumentAttribute)localObject2).title;
      if (str != null)
      {
        localObject2 = str;
        if (str.length() != 0) {}
      }
      else
      {
        localObject1 = FileLoader.getDocumentFileName((TLRPC.Document)localObject1);
        localObject2 = localObject1;
        if (TextUtils.isEmpty((CharSequence)localObject1))
        {
          localObject2 = localObject1;
          if (paramBoolean)
          {
            localObject2 = LocaleController.getString("AudioUnknownTitle", NUM);
            continue;
            label163:
            if (((localObject2 instanceof TLRPC.TL_documentAttributeVideo)) && (((TLRPC.DocumentAttribute)localObject2).round_message))
            {
              localObject2 = LocaleController.formatDateAudio(this.messageOwner.date);
            }
            else
            {
              i++;
              break label23;
              label201:
              localObject1 = FileLoader.getDocumentFileName((TLRPC.Document)localObject1);
              localObject2 = localObject1;
              if (TextUtils.isEmpty((CharSequence)localObject1)) {
                localObject2 = LocaleController.getString("AudioUnknownTitle", NUM);
              }
            }
          }
        }
      }
    }
  }
  
  public int getSecretTimeLeft()
  {
    int i = this.messageOwner.ttl;
    if (this.messageOwner.destroyTime != 0) {
      i = Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
    }
    return i;
  }
  
  public String getSecretTimeString()
  {
    String str;
    if (!isSecretMedia()) {
      str = null;
    }
    for (;;)
    {
      return str;
      int i = getSecretTimeLeft();
      if (i < 60) {
        str = i + "s";
      } else {
        str = i / 60 + "m";
      }
    }
  }
  
  public int getSize()
  {
    return getMessageSize(this.messageOwner);
  }
  
  public String getStickerEmoji()
  {
    Object localObject1 = null;
    for (int i = 0;; i++)
    {
      Object localObject2 = localObject1;
      if (i < this.messageOwner.media.document.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)this.messageOwner.media.document.attributes.get(i);
        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
          continue;
        }
        localObject2 = localObject1;
        if (localDocumentAttribute.alt != null)
        {
          localObject2 = localObject1;
          if (localDocumentAttribute.alt.length() > 0) {
            localObject2 = localDocumentAttribute.alt;
          }
        }
      }
      return (String)localObject2;
    }
  }
  
  public String getStrickerChar()
  {
    TLRPC.DocumentAttribute localDocumentAttribute;
    if ((this.messageOwner.media != null) && (this.messageOwner.media.document != null))
    {
      localObject = this.messageOwner.media.document.attributes.iterator();
      do
      {
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
        localDocumentAttribute = (TLRPC.DocumentAttribute)((Iterator)localObject).next();
      } while (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker));
    }
    for (Object localObject = localDocumentAttribute.alt;; localObject = null) {
      return (String)localObject;
    }
  }
  
  public int getUnradFlags()
  {
    return getUnreadFlags(this.messageOwner);
  }
  
  public ArrayList<MessageObject> getWebPagePhotos(ArrayList<MessageObject> paramArrayList, ArrayList<TLRPC.PageBlock> paramArrayList1)
  {
    TLRPC.WebPage localWebPage = this.messageOwner.media.webpage;
    if (paramArrayList == null)
    {
      paramArrayList = new ArrayList();
      if (localWebPage.cached_page != null) {
        break label35;
      }
    }
    label35:
    label197:
    for (;;)
    {
      return paramArrayList;
      break;
      if (paramArrayList1 == null) {
        paramArrayList1 = localWebPage.cached_page.blocks;
      }
      for (int i = 0;; i++)
      {
        if (i >= paramArrayList1.size()) {
          break label197;
        }
        Object localObject = (TLRPC.PageBlock)paramArrayList1.get(i);
        int j;
        if ((localObject instanceof TLRPC.TL_pageBlockSlideshow))
        {
          localObject = (TLRPC.TL_pageBlockSlideshow)localObject;
          for (j = 0; j < ((TLRPC.TL_pageBlockSlideshow)localObject).items.size(); j++) {
            paramArrayList.add(getMessageObjectForBlock(localWebPage, (TLRPC.PageBlock)((TLRPC.TL_pageBlockSlideshow)localObject).items.get(j)));
          }
          break;
        }
        if ((localObject instanceof TLRPC.TL_pageBlockCollage))
        {
          localObject = (TLRPC.TL_pageBlockCollage)localObject;
          for (j = 0; j < ((TLRPC.TL_pageBlockCollage)localObject).items.size(); j++) {
            paramArrayList.add(getMessageObjectForBlock(localWebPage, (TLRPC.PageBlock)((TLRPC.TL_pageBlockCollage)localObject).items.get(j)));
          }
        }
      }
    }
  }
  
  public boolean hasPhotoStickers()
  {
    if ((this.messageOwner.media != null) && (this.messageOwner.media.photo != null) && (this.messageOwner.media.photo.has_stickers)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean hasValidGroupId()
  {
    if ((getGroupId() != 0L) && (this.photoThumbs != null) && (!this.photoThumbs.isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean hasValidReplyMessageObject()
  {
    if ((this.replyMessageObject != null) && (!(this.replyMessageObject.messageOwner instanceof TLRPC.TL_messageEmpty)) && (!(this.replyMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionHistoryClear))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isContentUnread()
  {
    return this.messageOwner.media_unread;
  }
  
  public boolean isFcmMessage()
  {
    if (this.localType != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isForwarded()
  {
    return isForwardedMessage(this.messageOwner);
  }
  
  public boolean isFromUser()
  {
    if ((this.messageOwner.from_id > 0) && (!this.messageOwner.post)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isGame()
  {
    return isGameMessage(this.messageOwner);
  }
  
  public boolean isGif()
  {
    return isGifMessage(this.messageOwner);
  }
  
  public boolean isInvoice()
  {
    return isInvoiceMessage(this.messageOwner);
  }
  
  public boolean isLiveLocation()
  {
    return isLiveLocationMessage(this.messageOwner);
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
    if ((this.messageOwner.media != null) && (isNewGifDocument(this.messageOwner.media.document))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isOut()
  {
    return this.messageOwner.out;
  }
  
  public boolean isOutOwner()
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (this.messageOwner.out)
    {
      bool2 = bool1;
      if (this.messageOwner.from_id > 0)
      {
        if (!this.messageOwner.post) {
          break label40;
        }
        bool2 = bool1;
      }
    }
    for (;;)
    {
      return bool2;
      label40:
      if (this.messageOwner.fwd_from == null)
      {
        bool2 = true;
      }
      else
      {
        int i = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (getDialogId() == i)
        {
          if (this.messageOwner.fwd_from.from_id != i)
          {
            bool2 = bool1;
            if (this.messageOwner.fwd_from.saved_from_peer != null)
            {
              bool2 = bool1;
              if (this.messageOwner.fwd_from.saved_from_peer.user_id != i) {}
            }
          }
          else
          {
            bool2 = true;
          }
        }
        else if (this.messageOwner.fwd_from.saved_from_peer != null)
        {
          bool2 = bool1;
          if (this.messageOwner.fwd_from.saved_from_peer.user_id != i) {}
        }
        else
        {
          bool2 = true;
        }
      }
    }
  }
  
  public boolean isReply()
  {
    if (((this.replyMessageObject == null) || (!(this.replyMessageObject.messageOwner instanceof TLRPC.TL_messageEmpty))) && ((this.messageOwner.reply_to_msg_id != 0) || (this.messageOwner.reply_to_random_id != 0L)) && ((this.messageOwner.flags & 0x8) != 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isRoundVideo()
  {
    boolean bool = true;
    int i;
    if (this.isRoundVideoCached == 0)
    {
      if ((this.type == 5) || (isRoundVideoMessage(this.messageOwner)))
      {
        i = 1;
        this.isRoundVideoCached = i;
      }
    }
    else {
      if (this.isRoundVideoCached != 1) {
        break label49;
      }
    }
    for (;;)
    {
      return bool;
      i = 2;
      break;
      label49:
      bool = false;
    }
  }
  
  public boolean isSavedFromMegagroup()
  {
    if ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.saved_from_peer != null) && (this.messageOwner.fwd_from.saved_from_peer.channel_id != 0)) {}
    for (boolean bool = ChatObject.isMegagroup(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.saved_from_peer.channel_id)));; bool = false) {
      return bool;
    }
  }
  
  public boolean isSecretMedia()
  {
    boolean bool1 = true;
    boolean bool2 = false;
    if ((this.messageOwner instanceof TLRPC.TL_message_secret)) {
      if (((!(this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (!isGif())) || (((this.messageOwner.ttl > 0) && (this.messageOwner.ttl <= 60)) || (isVoice()) || (isRoundVideo()) || (isVideo()))) {
        bool2 = true;
      }
    }
    while (!(this.messageOwner instanceof TLRPC.TL_message)) {
      return bool2;
    }
    if ((((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))) && (this.messageOwner.media.ttl_seconds != 0)) {}
    for (bool2 = bool1;; bool2 = false) {
      break;
    }
  }
  
  public boolean isSendError()
  {
    if ((this.messageOwner.send_state == 2) && (this.messageOwner.id < 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isSending()
  {
    boolean bool = true;
    if ((this.messageOwner.send_state == 1) && (this.messageOwner.id < 0)) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public boolean isSent()
  {
    if ((this.messageOwner.send_state == 0) || (this.messageOwner.id > 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isSticker()
  {
    boolean bool;
    if (this.type != 1000) {
      if (this.type == 13) {
        bool = true;
      }
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      bool = isStickerMessage(this.messageOwner);
    }
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
  
  public boolean isWebpage()
  {
    return this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage;
  }
  
  public boolean isWebpageDocument()
  {
    if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (this.messageOwner.media.webpage.document != null) && (!isGifDocument(this.messageOwner.media.webpage.document))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void measureInlineBotButtons()
  {
    this.wantedBotKeyboardWidth = 0;
    if (!(this.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) {}
    label93:
    label338:
    for (;;)
    {
      return;
      Theme.createChatResources(null, true);
      if (this.botButtonsLayout == null) {
        this.botButtonsLayout = new StringBuilder();
      }
      for (int i = 0;; i++)
      {
        if (i >= this.messageOwner.reply_markup.rows.size()) {
          break label338;
        }
        TLRPC.TL_keyboardButtonRow localTL_keyboardButtonRow = (TLRPC.TL_keyboardButtonRow)this.messageOwner.reply_markup.rows.get(i);
        int j = 0;
        int k = localTL_keyboardButtonRow.buttons.size();
        int m = 0;
        if (m < k)
        {
          Object localObject = (TLRPC.KeyboardButton)localTL_keyboardButtonRow.buttons.get(m);
          this.botButtonsLayout.append(i).append(m);
          if (((localObject instanceof TLRPC.TL_keyboardButtonBuy)) && ((this.messageOwner.media.flags & 0x4) != 0)) {}
          for (localObject = LocaleController.getString("PaymentReceipt", NUM);; localObject = Emoji.replaceEmoji(((TLRPC.KeyboardButton)localObject).text, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0F), false))
          {
            localObject = new StaticLayout((CharSequence)localObject, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            int n = j;
            if (((StaticLayout)localObject).getLineCount() > 0)
            {
              float f1 = ((StaticLayout)localObject).getLineWidth(0);
              float f2 = ((StaticLayout)localObject).getLineLeft(0);
              float f3 = f1;
              if (f2 < f1) {
                f3 = f1 - f2;
              }
              n = Math.max(j, (int)Math.ceil(f3) + AndroidUtilities.dp(4.0F));
            }
            m++;
            j = n;
            break label93;
            this.botButtonsLayout.setLength(0);
            break;
          }
        }
        this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, (AndroidUtilities.dp(12.0F) + j) * k + AndroidUtilities.dp(5.0F) * (k - 1));
      }
    }
  }
  
  public boolean needDrawAvatar()
  {
    if ((isFromUser()) || (this.eventId != 0L) || ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.saved_from_peer != null))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean needDrawBluredPreview()
  {
    boolean bool1 = true;
    boolean bool2;
    if ((this.messageOwner instanceof TLRPC.TL_message_secret))
    {
      int i = Math.max(this.messageOwner.ttl, this.messageOwner.media.ttl_seconds);
      if (i > 0) {
        if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) || (isVideo()) || (isGif()))
        {
          bool2 = bool1;
          if (i <= 60) {}
        }
        else
        {
          if (!isRoundVideo()) {
            break label83;
          }
          bool2 = bool1;
        }
      }
    }
    for (;;)
    {
      return bool2;
      label83:
      bool2 = false;
      continue;
      if ((this.messageOwner instanceof TLRPC.TL_message))
      {
        if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)))
        {
          bool2 = bool1;
          if (this.messageOwner.media.ttl_seconds != 0) {}
        }
        else
        {
          bool2 = false;
        }
      }
      else {
        bool2 = false;
      }
    }
  }
  
  public boolean needDrawForwarded()
  {
    if (((this.messageOwner.flags & 0x4) != 0) && (this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.saved_from_peer == null) && (UserConfig.getInstance(this.currentAccount).getClientUserId() != getDialogId())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public CharSequence replaceWithLink(CharSequence paramCharSequence, String paramString, ArrayList<Integer> paramArrayList, AbstractMap<Integer, TLRPC.User> paramAbstractMap, SparseArray<TLRPC.User> paramSparseArray)
  {
    Object localObject1 = paramCharSequence;
    if (TextUtils.indexOf(paramCharSequence, paramString) >= 0)
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder("");
      int i = 0;
      if (i < paramArrayList.size())
      {
        localObject1 = null;
        if (paramAbstractMap != null) {
          localObject1 = (TLRPC.User)paramAbstractMap.get(paramArrayList.get(i));
        }
        for (;;)
        {
          Object localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = MessagesController.getInstance(this.currentAccount).getUser((Integer)paramArrayList.get(i));
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
          i++;
          break;
          if (paramSparseArray != null) {
            localObject1 = (TLRPC.User)paramSparseArray.get(((Integer)paramArrayList.get(i)).intValue());
          }
        }
      }
      localObject1 = TextUtils.replace(paramCharSequence, new String[] { paramString }, new CharSequence[] { localSpannableStringBuilder });
    }
    return (CharSequence)localObject1;
  }
  
  public CharSequence replaceWithLink(CharSequence paramCharSequence, String paramString, TLObject paramTLObject)
  {
    int i = TextUtils.indexOf(paramCharSequence, paramString);
    String str;
    if (i >= 0) {
      if ((paramTLObject instanceof TLRPC.User))
      {
        str = UserObject.getUserName((TLRPC.User)paramTLObject);
        paramTLObject = "" + ((TLRPC.User)paramTLObject).id;
        str = str.replace('\n', ' ');
        paramCharSequence = new SpannableStringBuilder(TextUtils.replace(paramCharSequence, new String[] { paramString }, new String[] { str }));
        paramCharSequence.setSpan(new URLSpanNoUnderlineBold("" + paramTLObject), i, str.length() + i, 33);
      }
    }
    for (;;)
    {
      return paramCharSequence;
      if ((paramTLObject instanceof TLRPC.Chat))
      {
        str = ((TLRPC.Chat)paramTLObject).title;
        paramTLObject = "" + -((TLRPC.Chat)paramTLObject).id;
        break;
      }
      if ((paramTLObject instanceof TLRPC.TL_game))
      {
        str = ((TLRPC.TL_game)paramTLObject).title;
        paramTLObject = "game";
        break;
      }
      str = "";
      paramTLObject = "0";
      break;
    }
  }
  
  public void resetPlayingProgress()
  {
    this.audioProgress = 0.0F;
    this.audioProgressSec = 0;
    this.bufferedProgress = 0.0F;
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
    this.isRoundVideoCached = 0;
    if (((this.messageOwner instanceof TLRPC.TL_message)) || ((this.messageOwner instanceof TLRPC.TL_messageForwarded_old2))) {
      if (isMediaEmpty())
      {
        this.type = 0;
        if ((TextUtils.isEmpty(this.messageText)) && (this.eventId == 0L)) {
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
      if ((this.messageOwner.media.ttl_seconds != 0) && (((this.messageOwner.media.photo instanceof TLRPC.TL_photoEmpty)) || ((this.messageOwner.media.document instanceof TLRPC.TL_documentEmpty))))
      {
        this.contentType = 1;
        this.type = 10;
      }
      else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
      {
        this.type = 1;
      }
      else if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive)))
      {
        this.type = 4;
      }
      else if (isRoundVideo())
      {
        this.type = 5;
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
      }
      else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice))
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
          else if ((this.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall))
          {
            this.type = 16;
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
  
  public boolean shouldEncryptPhotoOrVideo()
  {
    return shouldEncryptPhotoOrVideo(this.messageOwner);
  }
  
  public static class GroupedMessagePosition
  {
    public float aspectRatio;
    public boolean edge;
    public int flags;
    public boolean last;
    public int leftSpanOffset;
    public byte maxX;
    public byte maxY;
    public byte minX;
    public byte minY;
    public float ph;
    public int pw;
    public float[] siblingHeights;
    public int spanSize;
    
    public void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float paramFloat, int paramInt6)
    {
      this.minX = ((byte)(byte)paramInt1);
      this.maxX = ((byte)(byte)paramInt2);
      this.minY = ((byte)(byte)paramInt3);
      this.maxY = ((byte)(byte)paramInt4);
      this.pw = paramInt5;
      this.spanSize = paramInt5;
      this.ph = paramFloat;
      this.flags = ((byte)paramInt6);
    }
  }
  
  public static class GroupedMessages
  {
    private int firstSpanAdditionalSize = 200;
    public long groupId;
    public boolean hasSibling;
    private int maxSizeWidth = 800;
    public ArrayList<MessageObject> messages = new ArrayList();
    public ArrayList<MessageObject.GroupedMessagePosition> posArray = new ArrayList();
    public HashMap<MessageObject, MessageObject.GroupedMessagePosition> positions = new HashMap();
    
    private float multiHeight(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
    {
      float f = 0.0F;
      while (paramInt1 < paramInt2)
      {
        f += paramArrayOfFloat[paramInt1];
        paramInt1++;
      }
      return this.maxSizeWidth / f;
    }
    
    public void calculate()
    {
      this.posArray.clear();
      this.positions.clear();
      int i = this.messages.size();
      if (i <= 1) {
        return;
      }
      Object localObject1 = new StringBuilder();
      float f1 = 1.0F;
      boolean bool1 = false;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      this.hasSibling = false;
      int i1 = 0;
      Object localObject2;
      label193:
      Object localObject3;
      MessageObject.GroupedMessagePosition localGroupedMessagePosition;
      if (i1 < i)
      {
        localObject2 = (MessageObject)this.messages.get(i1);
        boolean bool2;
        if (i1 == 0)
        {
          bool1 = ((MessageObject)localObject2).isOutOwner();
          if ((!bool1) && (((((MessageObject)localObject2).messageOwner.fwd_from != null) && (((MessageObject)localObject2).messageOwner.fwd_from.saved_from_peer != null)) || ((((MessageObject)localObject2).messageOwner.from_id > 0) && ((((MessageObject)localObject2).messageOwner.to_id.channel_id != 0) || (((MessageObject)localObject2).messageOwner.to_id.chat_id != 0) || ((((MessageObject)localObject2).messageOwner.media instanceof TLRPC.TL_messageMediaGame)) || ((((MessageObject)localObject2).messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)))))) {
            n = 1;
          }
        }
        else
        {
          localObject3 = FileLoader.getClosestPhotoSizeWithSize(((MessageObject)localObject2).photoThumbs, AndroidUtilities.getPhotoSize());
          localGroupedMessagePosition = new MessageObject.GroupedMessagePosition();
          if (i1 != i - 1) {
            break label321;
          }
          bool2 = true;
          label226:
          localGroupedMessagePosition.last = bool2;
          if (localObject3 != null) {
            break label327;
          }
          f2 = 1.0F;
          label241:
          localGroupedMessagePosition.aspectRatio = f2;
          if (localGroupedMessagePosition.aspectRatio <= 1.2F) {
            break label345;
          }
          ((StringBuilder)localObject1).append("w");
        }
        for (;;)
        {
          f1 += localGroupedMessagePosition.aspectRatio;
          if (localGroupedMessagePosition.aspectRatio > 2.0F) {
            m = 1;
          }
          this.positions.put(localObject2, localGroupedMessagePosition);
          this.posArray.add(localGroupedMessagePosition);
          i1++;
          break;
          n = 0;
          break label193;
          label321:
          bool2 = false;
          break label226;
          label327:
          f2 = ((TLRPC.PhotoSize)localObject3).w / ((TLRPC.PhotoSize)localObject3).h;
          break label241;
          label345:
          if (localGroupedMessagePosition.aspectRatio < 0.8F) {
            ((StringBuilder)localObject1).append("n");
          } else {
            ((StringBuilder)localObject1).append("q");
          }
        }
      }
      if (n != 0)
      {
        this.maxSizeWidth -= 50;
        this.firstSpanAdditionalSize += 50;
      }
      int i2 = AndroidUtilities.dp(120.0F);
      int i3 = (int)(AndroidUtilities.dp(120.0F) / (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / this.maxSizeWidth));
      i1 = (int)(AndroidUtilities.dp(40.0F) / (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / this.maxSizeWidth));
      float f2 = this.maxSizeWidth / 814.0F;
      f1 /= i;
      if ((m == 0) && ((i == 2) || (i == 3) || (i == 4))) {
        if (i == 2)
        {
          localObject2 = (MessageObject.GroupedMessagePosition)this.posArray.get(0);
          localObject3 = (MessageObject.GroupedMessagePosition)this.posArray.get(1);
          localObject1 = ((StringBuilder)localObject1).toString();
          if ((((String)localObject1).equals("ww")) && (f1 > 1.4D * f2) && (((MessageObject.GroupedMessagePosition)localObject2).aspectRatio - ((MessageObject.GroupedMessagePosition)localObject3).aspectRatio < 0.2D))
          {
            f1 = Math.round(Math.min(this.maxSizeWidth / ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio, Math.min(this.maxSizeWidth / ((MessageObject.GroupedMessagePosition)localObject3).aspectRatio, 814.0F / 2.0F))) / 814.0F;
            ((MessageObject.GroupedMessagePosition)localObject2).set(0, 0, 0, 0, this.maxSizeWidth, f1, 7);
            ((MessageObject.GroupedMessagePosition)localObject3).set(0, 0, 1, 1, this.maxSizeWidth, f1, 11);
            n = k;
            label662:
            m = 0;
            label665:
            if (m < i)
            {
              localObject1 = (MessageObject.GroupedMessagePosition)this.posArray.get(m);
              if (!bool1) {
                break label3108;
              }
              if (((MessageObject.GroupedMessagePosition)localObject1).minX == 0) {
                ((MessageObject.GroupedMessagePosition)localObject1).spanSize += this.firstSpanAdditionalSize;
              }
              if ((((MessageObject.GroupedMessagePosition)localObject1).flags & 0x2) != 0) {
                ((MessageObject.GroupedMessagePosition)localObject1).edge = true;
              }
              label723:
              localObject3 = (MessageObject)this.messages.get(m);
              if ((!bool1) && (((MessageObject)localObject3).needDrawAvatar()))
              {
                if (!((MessageObject.GroupedMessagePosition)localObject1).edge) {
                  break label3156;
                }
                if (((MessageObject.GroupedMessagePosition)localObject1).spanSize != 1000) {
                  ((MessageObject.GroupedMessagePosition)localObject1).spanSize += 108;
                }
                ((MessageObject.GroupedMessagePosition)localObject1).pw += 108;
              }
            }
          }
        }
      }
      for (;;)
      {
        m++;
        break label665;
        break;
        if ((((String)localObject1).equals("ww")) || (((String)localObject1).equals("qq")))
        {
          n = this.maxSizeWidth / 2;
          f1 = Math.round(Math.min(n / ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio, Math.min(n / ((MessageObject.GroupedMessagePosition)localObject3).aspectRatio, 814.0F))) / 814.0F;
          ((MessageObject.GroupedMessagePosition)localObject2).set(0, 0, 0, 0, n, f1, 13);
          ((MessageObject.GroupedMessagePosition)localObject3).set(1, 1, 0, 0, n, f1, 14);
          n = 1;
          break label662;
        }
        j = (int)Math.max(0.4F * this.maxSizeWidth, Math.round(this.maxSizeWidth / ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio / (1.0F / ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio + 1.0F / ((MessageObject.GroupedMessagePosition)localObject3).aspectRatio)));
        i1 = this.maxSizeWidth - j;
        n = i1;
        m = j;
        if (i1 < i3)
        {
          n = i3;
          m = j - (i3 - i1);
        }
        f1 = Math.min(814.0F, Math.round(Math.min(n / ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio, m / ((MessageObject.GroupedMessagePosition)localObject3).aspectRatio))) / 814.0F;
        ((MessageObject.GroupedMessagePosition)localObject2).set(0, 0, 0, 0, n, f1, 13);
        ((MessageObject.GroupedMessagePosition)localObject3).set(1, 1, 0, 0, m, f1, 14);
        n = 1;
        break label662;
        if (i == 3)
        {
          localObject3 = (MessageObject.GroupedMessagePosition)this.posArray.get(0);
          localObject2 = (MessageObject.GroupedMessagePosition)this.posArray.get(1);
          localGroupedMessagePosition = (MessageObject.GroupedMessagePosition)this.posArray.get(2);
          if (((StringBuilder)localObject1).charAt(0) == 'n')
          {
            f1 = Math.min(0.5F * 814.0F, Math.round(((MessageObject.GroupedMessagePosition)localObject2).aspectRatio * this.maxSizeWidth / (localGroupedMessagePosition.aspectRatio + ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio)));
            f2 = 814.0F - f1;
            n = (int)Math.max(i3, Math.min(this.maxSizeWidth * 0.5F, Math.round(Math.min(localGroupedMessagePosition.aspectRatio * f1, ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio * f2))));
            m = Math.round(Math.min(((MessageObject.GroupedMessagePosition)localObject3).aspectRatio * 814.0F + i1, this.maxSizeWidth - n));
            ((MessageObject.GroupedMessagePosition)localObject3).set(0, 0, 0, 1, m, 1.0F, 13);
            ((MessageObject.GroupedMessagePosition)localObject2).set(1, 1, 0, 0, n, f2 / 814.0F, 6);
            localGroupedMessagePosition.set(0, 1, 1, 1, n, f1 / 814.0F, 10);
            localGroupedMessagePosition.spanSize = this.maxSizeWidth;
            ((MessageObject.GroupedMessagePosition)localObject3).siblingHeights = new float[] { f1 / 814.0F, f2 / 814.0F };
            if (bool1) {
              ((MessageObject.GroupedMessagePosition)localObject3).spanSize = (this.maxSizeWidth - n);
            }
            for (;;)
            {
              this.hasSibling = true;
              n = 1;
              break;
              ((MessageObject.GroupedMessagePosition)localObject2).spanSize = (this.maxSizeWidth - m);
              localGroupedMessagePosition.leftSpanOffset = m;
            }
          }
          f1 = Math.round(Math.min(this.maxSizeWidth / ((MessageObject.GroupedMessagePosition)localObject3).aspectRatio, 0.66F * 814.0F)) / 814.0F;
          ((MessageObject.GroupedMessagePosition)localObject3).set(0, 1, 0, 0, this.maxSizeWidth, f1, 7);
          n = this.maxSizeWidth / 2;
          f1 = Math.min(814.0F - f1, Math.round(Math.min(n / ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio, n / localGroupedMessagePosition.aspectRatio))) / 814.0F;
          ((MessageObject.GroupedMessagePosition)localObject2).set(0, 0, 1, 1, n, f1, 9);
          localGroupedMessagePosition.set(1, 1, 1, 1, n, f1, 10);
          n = 1;
          break label662;
        }
        n = k;
        if (i != 4) {
          break label662;
        }
        localObject3 = (MessageObject.GroupedMessagePosition)this.posArray.get(0);
        localGroupedMessagePosition = (MessageObject.GroupedMessagePosition)this.posArray.get(1);
        localObject2 = (MessageObject.GroupedMessagePosition)this.posArray.get(2);
        Object localObject4 = (MessageObject.GroupedMessagePosition)this.posArray.get(3);
        if (((StringBuilder)localObject1).charAt(0) == 'w')
        {
          f2 = Math.round(Math.min(this.maxSizeWidth / ((MessageObject.GroupedMessagePosition)localObject3).aspectRatio, 0.66F * 814.0F)) / 814.0F;
          ((MessageObject.GroupedMessagePosition)localObject3).set(0, 2, 0, 0, this.maxSizeWidth, f2, 7);
          f1 = Math.round(this.maxSizeWidth / (localGroupedMessagePosition.aspectRatio + ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio + ((MessageObject.GroupedMessagePosition)localObject4).aspectRatio));
          n = (int)Math.max(i3, Math.min(this.maxSizeWidth * 0.4F, localGroupedMessagePosition.aspectRatio * f1));
          i1 = (int)Math.max(Math.max(i3, this.maxSizeWidth * 0.33F), ((MessageObject.GroupedMessagePosition)localObject4).aspectRatio * f1);
          m = this.maxSizeWidth;
          f1 = Math.min(814.0F - f2, f1) / 814.0F;
          localGroupedMessagePosition.set(0, 0, 1, 1, n, f1, 9);
          ((MessageObject.GroupedMessagePosition)localObject2).set(1, 1, 1, 1, m - n - i1, f1, 8);
          ((MessageObject.GroupedMessagePosition)localObject4).set(2, 2, 1, 1, i1, f1, 10);
          n = 2;
          break label662;
        }
        f1 = 1.0F / localGroupedMessagePosition.aspectRatio;
        f2 = 1.0F / ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio;
        n = Math.max(i3, Math.round(814.0F / (1.0F / ((MessageObject.GroupedMessagePosition)this.posArray.get(3)).aspectRatio + (f2 + f1))));
        f1 = Math.min(0.33F, Math.max(i2, n / localGroupedMessagePosition.aspectRatio) / 814.0F);
        f2 = Math.min(0.33F, Math.max(i2, n / ((MessageObject.GroupedMessagePosition)localObject2).aspectRatio) / 814.0F);
        float f3 = 1.0F - f1 - f2;
        m = Math.round(Math.min(((MessageObject.GroupedMessagePosition)localObject3).aspectRatio * 814.0F + i1, this.maxSizeWidth - n));
        ((MessageObject.GroupedMessagePosition)localObject3).set(0, 0, 0, 2, m, f1 + f2 + f3, 13);
        localGroupedMessagePosition.set(1, 1, 0, 0, n, f1, 6);
        ((MessageObject.GroupedMessagePosition)localObject2).set(0, 1, 1, 1, n, f2, 2);
        ((MessageObject.GroupedMessagePosition)localObject2).spanSize = this.maxSizeWidth;
        ((MessageObject.GroupedMessagePosition)localObject4).set(0, 1, 2, 2, n, f3, 10);
        ((MessageObject.GroupedMessagePosition)localObject4).spanSize = this.maxSizeWidth;
        if (bool1) {
          ((MessageObject.GroupedMessagePosition)localObject3).spanSize = (this.maxSizeWidth - n);
        }
        for (;;)
        {
          ((MessageObject.GroupedMessagePosition)localObject3).siblingHeights = new float[] { f1, f2, f3 };
          this.hasSibling = true;
          n = 1;
          break;
          localGroupedMessagePosition.spanSize = (this.maxSizeWidth - m);
          ((MessageObject.GroupedMessagePosition)localObject2).leftSpanOffset = m;
          ((MessageObject.GroupedMessagePosition)localObject4).leftSpanOffset = m;
        }
        localObject4 = new float[this.posArray.size()];
        n = 0;
        if (n < i)
        {
          if (f1 > 1.1F) {
            localObject4[n] = Math.max(1.0F, ((MessageObject.GroupedMessagePosition)this.posArray.get(n)).aspectRatio);
          }
          for (;;)
          {
            localObject4[n] = Math.max(0.66667F, Math.min(1.7F, localObject4[n]));
            n++;
            break;
            localObject4[n] = Math.min(1.0F, ((MessageObject.GroupedMessagePosition)this.posArray.get(n)).aspectRatio);
          }
        }
        localObject3 = new ArrayList();
        n = 1;
        if (n < localObject4.length)
        {
          m = localObject4.length - n;
          if ((n > 3) || (m > 3)) {}
          for (;;)
          {
            n++;
            break;
            ((ArrayList)localObject3).add(new MessageGroupedLayoutAttempt(n, m, multiHeight((float[])localObject4, 0, n), multiHeight((float[])localObject4, n, localObject4.length)));
          }
        }
        for (n = 1; n < localObject4.length - 1; n++)
        {
          m = 1;
          if (m < localObject4.length - n)
          {
            k = localObject4.length - n - m;
            if (n <= 3)
            {
              if (f1 >= 0.85F) {
                break label2290;
              }
              i1 = 4;
              label2271:
              if ((m <= i1) && (k <= 3)) {
                break label2296;
              }
            }
            for (;;)
            {
              m++;
              break;
              label2290:
              i1 = 3;
              break label2271;
              label2296:
              ((ArrayList)localObject3).add(new MessageGroupedLayoutAttempt(n, m, k, multiHeight((float[])localObject4, 0, n), multiHeight((float[])localObject4, n, n + m), multiHeight((float[])localObject4, n + m, localObject4.length)));
            }
          }
        }
        for (n = 1; n < localObject4.length - 2; n++) {
          for (m = 1; m < localObject4.length - n; m++)
          {
            i1 = 1;
            if (i1 < localObject4.length - n - m)
            {
              k = localObject4.length - n - m - i1;
              if ((n > 3) || (m > 3) || (i1 > 3) || (k > 3)) {}
              for (;;)
              {
                i1++;
                break;
                ((ArrayList)localObject3).add(new MessageGroupedLayoutAttempt(n, m, i1, k, multiHeight((float[])localObject4, 0, n), multiHeight((float[])localObject4, n, n + m), multiHeight((float[])localObject4, n + m, n + m + i1), multiHeight((float[])localObject4, n + m + i1, localObject4.length)));
              }
            }
          }
        }
        localObject2 = null;
        f3 = 0.0F;
        float f4 = this.maxSizeWidth / 3 * 4;
        n = 0;
        while (n < ((ArrayList)localObject3).size())
        {
          localObject1 = (MessageGroupedLayoutAttempt)((ArrayList)localObject3).get(n);
          f2 = 0.0F;
          float f5 = Float.MAX_VALUE;
          m = 0;
          while (m < ((MessageGroupedLayoutAttempt)localObject1).heights.length)
          {
            f2 += localObject1.heights[m];
            f1 = f5;
            if (localObject1.heights[m] < f5) {
              f1 = localObject1.heights[m];
            }
            m++;
            f5 = f1;
          }
          f2 = Math.abs(f2 - f4);
          f1 = f2;
          if (((MessageGroupedLayoutAttempt)localObject1).lineCounts.length > 1) {
            if ((localObject1.lineCounts[0] <= localObject1.lineCounts[1]) && ((((MessageGroupedLayoutAttempt)localObject1).lineCounts.length <= 2) || (localObject1.lineCounts[1] <= localObject1.lineCounts[2])))
            {
              f1 = f2;
              if (((MessageGroupedLayoutAttempt)localObject1).lineCounts.length > 3)
              {
                f1 = f2;
                if (localObject1.lineCounts[2] <= localObject1.lineCounts[3]) {}
              }
            }
            else
            {
              f1 = f2 * 1.2F;
            }
          }
          f2 = f1;
          if (f5 < i3) {
            f2 = f1 * 1.5F;
          }
          if (localObject2 != null)
          {
            f1 = f3;
            if (f2 >= f3) {}
          }
          else
          {
            localObject2 = localObject1;
            f1 = f2;
          }
          n++;
          f3 = f1;
        }
        if (localObject2 == null) {
          break;
        }
        n = 0;
        f1 = 0.0F;
        i1 = 0;
        m = j;
        j = n;
        for (;;)
        {
          n = m;
          if (i1 >= ((MessageGroupedLayoutAttempt)localObject2).lineCounts.length) {
            break;
          }
          int i4 = localObject2.lineCounts[i1];
          f2 = localObject2.heights[i1];
          n = this.maxSizeWidth;
          localObject3 = null;
          k = Math.max(m, i4 - 1);
          i3 = 0;
          while (i3 < i4)
          {
            int i5 = (int)(localObject4[j] * f2);
            i2 = n - i5;
            localGroupedMessagePosition = (MessageObject.GroupedMessagePosition)this.posArray.get(j);
            n = 0;
            if (i1 == 0) {
              n = 0x0 | 0x4;
            }
            m = n;
            if (i1 == ((MessageGroupedLayoutAttempt)localObject2).lineCounts.length - 1) {
              m = n | 0x8;
            }
            n = m;
            localObject1 = localObject3;
            if (i3 == 0)
            {
              m |= 0x1;
              n = m;
              localObject1 = localObject3;
              if (bool1)
              {
                localObject1 = localGroupedMessagePosition;
                n = m;
              }
            }
            m = n;
            localObject3 = localObject1;
            if (i3 == i4 - 1)
            {
              n |= 0x2;
              m = n;
              localObject3 = localObject1;
              if (!bool1)
              {
                localObject3 = localGroupedMessagePosition;
                m = n;
              }
            }
            localGroupedMessagePosition.set(i3, i3, i1, i1, i5, f2 / 814.0F, m);
            j++;
            i3++;
            n = i2;
          }
          ((MessageObject.GroupedMessagePosition)localObject3).pw += n;
          ((MessageObject.GroupedMessagePosition)localObject3).spanSize += n;
          f1 += f2;
          i1++;
          m = k;
        }
        label3108:
        if ((((MessageObject.GroupedMessagePosition)localObject1).maxX == n) || ((((MessageObject.GroupedMessagePosition)localObject1).flags & 0x2) != 0)) {
          ((MessageObject.GroupedMessagePosition)localObject1).spanSize += this.firstSpanAdditionalSize;
        }
        if ((((MessageObject.GroupedMessagePosition)localObject1).flags & 0x1) == 0) {
          break label723;
        }
        ((MessageObject.GroupedMessagePosition)localObject1).edge = true;
        break label723;
        label3156:
        if ((((MessageObject.GroupedMessagePosition)localObject1).flags & 0x2) != 0) {
          if (((MessageObject.GroupedMessagePosition)localObject1).spanSize != 1000) {
            ((MessageObject.GroupedMessagePosition)localObject1).spanSize -= 108;
          } else if (((MessageObject.GroupedMessagePosition)localObject1).leftSpanOffset != 0) {
            ((MessageObject.GroupedMessagePosition)localObject1).leftSpanOffset += 108;
          }
        }
      }
    }
    
    private class MessageGroupedLayoutAttempt
    {
      public float[] heights;
      public int[] lineCounts;
      
      public MessageGroupedLayoutAttempt(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
      {
        this.lineCounts = new int[] { paramInt1, paramInt2 };
        this.heights = new float[] { paramFloat1, paramFloat2 };
      }
      
      public MessageGroupedLayoutAttempt(int paramInt1, int paramInt2, int paramInt3, float paramFloat1, float paramFloat2, float paramFloat3)
      {
        this.lineCounts = new int[] { paramInt1, paramInt2, paramInt3 };
        this.heights = new float[] { paramFloat1, paramFloat2, paramFloat3 };
      }
      
      public MessageGroupedLayoutAttempt(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
      {
        this.lineCounts = new int[] { paramInt1, paramInt2, paramInt3, paramInt4 };
        this.heights = new float[] { paramFloat1, paramFloat2, paramFloat3, paramFloat4 };
      }
    }
  }
  
  public static class TextLayoutBlock
  {
    public int charactersEnd;
    public int charactersOffset;
    public byte directionFlags;
    public int height;
    public int heightByOffset;
    public StaticLayout textLayout;
    public float textYOffset;
    
    public boolean isRtl()
    {
      if (((this.directionFlags & 0x1) != 0) && ((this.directionFlags & 0x2) == 0)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MessageObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */