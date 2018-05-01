package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.util.LongSparseArray;
import android.view.View.MeasureSpec;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.GroupCreateCheckBox;

public class DialogCell
  extends BaseCell
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private ImageReceiver avatarImage = new ImageReceiver(this);
  private int avatarTop = AndroidUtilities.dp(10.0F);
  private TLRPC.Chat chat = null;
  private GroupCreateCheckBox checkBox;
  private int checkDrawLeft;
  private int checkDrawTop = AndroidUtilities.dp(18.0F);
  private StaticLayout countLayout;
  private int countLeft;
  private int countTop = AndroidUtilities.dp(39.0F);
  private int countWidth;
  private int currentAccount = UserConfig.selectedAccount;
  private long currentDialogId;
  private int currentEditDate;
  private CustomDialog customDialog;
  private boolean dialogMuted;
  private int dialogsType;
  private TLRPC.DraftMessage draftMessage;
  private boolean drawCheck1;
  private boolean drawCheck2;
  private boolean drawClock;
  private boolean drawCount;
  private boolean drawError;
  private boolean drawMention;
  private boolean drawNameBot;
  private boolean drawNameBroadcast;
  private boolean drawNameGroup;
  private boolean drawNameLock;
  private boolean drawPin;
  private boolean drawPinBackground;
  private boolean drawVerified;
  private TLRPC.EncryptedChat encryptedChat = null;
  private int errorLeft;
  private int errorTop = AndroidUtilities.dp(39.0F);
  private int halfCheckDrawLeft;
  private int index;
  private boolean isDialogCell;
  private boolean isSelected;
  private int lastMessageDate;
  private CharSequence lastMessageString;
  private CharSequence lastPrintString = null;
  private int lastSendState;
  private boolean lastUnreadState;
  private int mentionCount;
  private int mentionLeft;
  private int mentionWidth;
  private MessageObject message;
  private StaticLayout messageLayout;
  private int messageLeft;
  private int messageTop = AndroidUtilities.dp(40.0F);
  private StaticLayout nameLayout;
  private int nameLeft;
  private int nameLockLeft;
  private int nameLockTop;
  private int nameMuteLeft;
  private int pinLeft;
  private int pinTop = AndroidUtilities.dp(39.0F);
  private RectF rect = new RectF();
  private StaticLayout timeLayout;
  private int timeLeft;
  private int timeTop = AndroidUtilities.dp(17.0F);
  private int unreadCount;
  public boolean useSeparator = false;
  private TLRPC.User user = null;
  
  public DialogCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    Theme.createDialogsResources(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0F));
    if (paramBoolean)
    {
      this.checkBox = new GroupCreateCheckBox(paramContext);
      this.checkBox.setVisibility(0);
      addView(this.checkBox);
    }
  }
  
  private ArrayList<TLRPC.TL_dialog> getDialogsArray()
  {
    ArrayList localArrayList;
    if (this.dialogsType == 0) {
      localArrayList = MessagesController.getInstance(this.currentAccount).dialogs;
    }
    for (;;)
    {
      return localArrayList;
      if (this.dialogsType == 1) {
        localArrayList = MessagesController.getInstance(this.currentAccount).dialogsServerOnly;
      } else if (this.dialogsType == 2) {
        localArrayList = MessagesController.getInstance(this.currentAccount).dialogsGroupsOnly;
      } else if (this.dialogsType == 3) {
        localArrayList = MessagesController.getInstance(this.currentAccount).dialogsForward;
      } else {
        localArrayList = null;
      }
    }
  }
  
  /* Error */
  public void buildLayout()
  {
    // Byte code:
    //   0: ldc -52
    //   2: astore_1
    //   3: ldc -52
    //   5: astore_2
    //   6: aconst_null
    //   7: astore_3
    //   8: aconst_null
    //   9: astore 4
    //   11: aconst_null
    //   12: astore 5
    //   14: aconst_null
    //   15: astore 6
    //   17: aconst_null
    //   18: astore 7
    //   20: aconst_null
    //   21: astore 8
    //   23: ldc -52
    //   25: astore 9
    //   27: aconst_null
    //   28: astore 10
    //   30: aload_0
    //   31: getfield 206	org/telegram/ui/Cells/DialogCell:isDialogCell	Z
    //   34: ifeq +25 -> 59
    //   37: aload_0
    //   38: getfield 98	org/telegram/ui/Cells/DialogCell:currentAccount	I
    //   41: invokestatic 184	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   44: getfield 210	org/telegram/messenger/MessagesController:printingStrings	Landroid/util/LongSparseArray;
    //   47: aload_0
    //   48: getfield 212	org/telegram/ui/Cells/DialogCell:currentDialogId	J
    //   51: invokevirtual 218	android/util/LongSparseArray:get	(J)Ljava/lang/Object;
    //   54: checkcast 220	java/lang/CharSequence
    //   57: astore 10
    //   59: getstatic 224	org/telegram/ui/ActionBar/Theme:dialogs_namePaint	Landroid/text/TextPaint;
    //   62: astore 11
    //   64: getstatic 227	org/telegram/ui/ActionBar/Theme:dialogs_messagePaint	Landroid/text/TextPaint;
    //   67: astore 12
    //   69: iconst_1
    //   70: istore 13
    //   72: iconst_1
    //   73: istore 14
    //   75: aload_0
    //   76: iconst_0
    //   77: putfield 229	org/telegram/ui/Cells/DialogCell:drawNameGroup	Z
    //   80: aload_0
    //   81: iconst_0
    //   82: putfield 231	org/telegram/ui/Cells/DialogCell:drawNameBroadcast	Z
    //   85: aload_0
    //   86: iconst_0
    //   87: putfield 233	org/telegram/ui/Cells/DialogCell:drawNameLock	Z
    //   90: aload_0
    //   91: iconst_0
    //   92: putfield 235	org/telegram/ui/Cells/DialogCell:drawNameBot	Z
    //   95: aload_0
    //   96: iconst_0
    //   97: putfield 237	org/telegram/ui/Cells/DialogCell:drawVerified	Z
    //   100: aload_0
    //   101: iconst_0
    //   102: putfield 239	org/telegram/ui/Cells/DialogCell:drawPinBackground	Z
    //   105: aload_0
    //   106: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   109: invokestatic 245	org/telegram/messenger/UserObject:isUserSelf	(Lorg/telegram/tgnet/TLRPC$User;)Z
    //   112: ifne +1199 -> 1311
    //   115: iconst_1
    //   116: istore 15
    //   118: iconst_1
    //   119: istore 16
    //   121: iconst_1
    //   122: istore 17
    //   124: getstatic 250	android/os/Build$VERSION:SDK_INT	I
    //   127: bipush 18
    //   129: if_icmplt +1188 -> 1317
    //   132: ldc -4
    //   134: astore 18
    //   136: aload_0
    //   137: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   140: ifnull +1185 -> 1325
    //   143: aload_0
    //   144: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   147: getfield 259	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   150: astore 19
    //   152: aload_0
    //   153: aload 19
    //   155: putfield 261	org/telegram/ui/Cells/DialogCell:lastMessageString	Ljava/lang/CharSequence;
    //   158: aload_0
    //   159: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   162: ifnull +1562 -> 1724
    //   165: aload_0
    //   166: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   169: getfield 266	org/telegram/ui/Cells/DialogCell$CustomDialog:type	I
    //   172: iconst_2
    //   173: if_icmpne +1194 -> 1367
    //   176: aload_0
    //   177: iconst_1
    //   178: putfield 233	org/telegram/ui/Cells/DialogCell:drawNameLock	Z
    //   181: aload_0
    //   182: ldc_w 267
    //   185: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   188: putfield 269	org/telegram/ui/Cells/DialogCell:nameLockTop	I
    //   191: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   194: ifne +1137 -> 1331
    //   197: aload_0
    //   198: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   201: i2f
    //   202: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   205: putfield 279	org/telegram/ui/Cells/DialogCell:nameLockLeft	I
    //   208: aload_0
    //   209: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   212: iconst_4
    //   213: iadd
    //   214: i2f
    //   215: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   218: getstatic 283	org/telegram/ui/ActionBar/Theme:dialogs_lockDrawable	Landroid/graphics/drawable/Drawable;
    //   221: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   224: iadd
    //   225: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   228: aload_0
    //   229: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   232: getfield 266	org/telegram/ui/Cells/DialogCell$CustomDialog:type	I
    //   235: iconst_1
    //   236: if_icmpne +1406 -> 1642
    //   239: ldc_w 293
    //   242: ldc_w 294
    //   245: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   248: astore 7
    //   250: iconst_0
    //   251: istore 15
    //   253: aload_0
    //   254: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   257: getfield 301	org/telegram/ui/Cells/DialogCell$CustomDialog:isMedia	Z
    //   260: ifeq +1309 -> 1569
    //   263: getstatic 304	org/telegram/ui/ActionBar/Theme:dialogs_messagePrintingPaint	Landroid/text/TextPaint;
    //   266: astore 10
    //   268: aload 18
    //   270: iconst_2
    //   271: anewarray 306	java/lang/Object
    //   274: dup
    //   275: iconst_0
    //   276: aload 7
    //   278: aastore
    //   279: dup
    //   280: iconst_1
    //   281: aload_0
    //   282: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   285: getfield 259	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   288: aastore
    //   289: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   292: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   295: astore 19
    //   297: aload 19
    //   299: new 320	android/text/style/ForegroundColorSpan
    //   302: dup
    //   303: ldc_w 322
    //   306: invokestatic 326	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   309: invokespecial 328	android/text/style/ForegroundColorSpan:<init>	(I)V
    //   312: aload 7
    //   314: invokevirtual 331	java/lang/String:length	()I
    //   317: iconst_2
    //   318: iadd
    //   319: aload 19
    //   321: invokevirtual 332	android/text/SpannableStringBuilder:length	()I
    //   324: bipush 33
    //   326: invokevirtual 336	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   329: aload 19
    //   331: invokevirtual 332	android/text/SpannableStringBuilder:length	()I
    //   334: ifle +31 -> 365
    //   337: aload 19
    //   339: new 320	android/text/style/ForegroundColorSpan
    //   342: dup
    //   343: ldc_w 338
    //   346: invokestatic 326	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   349: invokespecial 328	android/text/style/ForegroundColorSpan:<init>	(I)V
    //   352: iconst_0
    //   353: aload 7
    //   355: invokevirtual 331	java/lang/String:length	()I
    //   358: iconst_1
    //   359: iadd
    //   360: bipush 33
    //   362: invokevirtual 336	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   365: aload 19
    //   367: getstatic 227	org/telegram/ui/ActionBar/Theme:dialogs_messagePaint	Landroid/text/TextPaint;
    //   370: invokevirtual 344	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   373: ldc_w 345
    //   376: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   379: iconst_0
    //   380: invokestatic 351	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   383: astore 19
    //   385: aload_0
    //   386: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   389: getfield 354	org/telegram/ui/Cells/DialogCell$CustomDialog:date	I
    //   392: i2l
    //   393: invokestatic 358	org/telegram/messenger/LocaleController:stringForMessageListDate	(J)Ljava/lang/String;
    //   396: astore 7
    //   398: aload_0
    //   399: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   402: getfield 361	org/telegram/ui/Cells/DialogCell$CustomDialog:unread_count	I
    //   405: ifeq +1284 -> 1689
    //   408: aload_0
    //   409: iconst_1
    //   410: putfield 363	org/telegram/ui/Cells/DialogCell:drawCount	Z
    //   413: ldc_w 365
    //   416: iconst_1
    //   417: anewarray 306	java/lang/Object
    //   420: dup
    //   421: iconst_0
    //   422: aload_0
    //   423: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   426: getfield 361	org/telegram/ui/Cells/DialogCell$CustomDialog:unread_count	I
    //   429: invokestatic 370	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   432: aastore
    //   433: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   436: astore 18
    //   438: aload_0
    //   439: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   442: getfield 373	org/telegram/ui/Cells/DialogCell$CustomDialog:sent	Z
    //   445: ifeq +1256 -> 1701
    //   448: aload_0
    //   449: iconst_1
    //   450: putfield 375	org/telegram/ui/Cells/DialogCell:drawCheck1	Z
    //   453: aload_0
    //   454: iconst_1
    //   455: putfield 377	org/telegram/ui/Cells/DialogCell:drawCheck2	Z
    //   458: aload_0
    //   459: iconst_0
    //   460: putfield 379	org/telegram/ui/Cells/DialogCell:drawClock	Z
    //   463: aload_0
    //   464: iconst_0
    //   465: putfield 381	org/telegram/ui/Cells/DialogCell:drawError	Z
    //   468: aload_0
    //   469: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   472: getfield 385	org/telegram/ui/Cells/DialogCell$CustomDialog:name	Ljava/lang/String;
    //   475: astore 4
    //   477: aload 7
    //   479: astore 20
    //   481: iload 15
    //   483: istore 21
    //   485: aload 18
    //   487: astore 9
    //   489: aload 10
    //   491: astore_2
    //   492: iload 17
    //   494: istore 13
    //   496: aload 8
    //   498: astore 5
    //   500: aload 19
    //   502: astore 12
    //   504: aload 4
    //   506: astore_1
    //   507: aload_0
    //   508: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   511: getfield 266	org/telegram/ui/Cells/DialogCell$CustomDialog:type	I
    //   514: iconst_2
    //   515: if_icmpne +38 -> 553
    //   518: getstatic 388	org/telegram/ui/ActionBar/Theme:dialogs_nameEncryptedPaint	Landroid/text/TextPaint;
    //   521: astore 11
    //   523: aload 4
    //   525: astore_1
    //   526: aload 19
    //   528: astore 12
    //   530: aload 8
    //   532: astore 5
    //   534: iload 17
    //   536: istore 13
    //   538: aload 10
    //   540: astore_2
    //   541: aload 18
    //   543: astore 9
    //   545: iload 15
    //   547: istore 21
    //   549: aload 7
    //   551: astore 20
    //   553: iload 13
    //   555: ifeq +4596 -> 5151
    //   558: getstatic 391	org/telegram/ui/ActionBar/Theme:dialogs_timePaint	Landroid/text/TextPaint;
    //   561: aload 20
    //   563: invokevirtual 395	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   566: f2d
    //   567: invokestatic 401	java/lang/Math:ceil	(D)D
    //   570: d2i
    //   571: istore 14
    //   573: aload_0
    //   574: new 403	android/text/StaticLayout
    //   577: dup
    //   578: aload 20
    //   580: getstatic 391	org/telegram/ui/ActionBar/Theme:dialogs_timePaint	Landroid/text/TextPaint;
    //   583: iload 14
    //   585: getstatic 409	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   588: fconst_1
    //   589: fconst_0
    //   590: iconst_0
    //   591: invokespecial 412	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   594: putfield 414	org/telegram/ui/Cells/DialogCell:timeLayout	Landroid/text/StaticLayout;
    //   597: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   600: ifne +4538 -> 5138
    //   603: aload_0
    //   604: aload_0
    //   605: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   608: ldc_w 418
    //   611: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   614: isub
    //   615: iload 14
    //   617: isub
    //   618: putfield 420	org/telegram/ui/Cells/DialogCell:timeLeft	I
    //   621: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   624: ifne +4543 -> 5167
    //   627: aload_0
    //   628: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   631: aload_0
    //   632: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   635: isub
    //   636: ldc_w 421
    //   639: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   642: isub
    //   643: iload 14
    //   645: isub
    //   646: istore 17
    //   648: aload_0
    //   649: getfield 233	org/telegram/ui/Cells/DialogCell:drawNameLock	Z
    //   652: ifeq +4551 -> 5203
    //   655: iload 17
    //   657: ldc_w 422
    //   660: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   663: getstatic 283	org/telegram/ui/ActionBar/Theme:dialogs_lockDrawable	Landroid/graphics/drawable/Drawable;
    //   666: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   669: iadd
    //   670: isub
    //   671: istore 15
    //   673: aload_0
    //   674: getfield 379	org/telegram/ui/Cells/DialogCell:drawClock	Z
    //   677: ifeq +4646 -> 5323
    //   680: getstatic 425	org/telegram/ui/ActionBar/Theme:dialogs_clockDrawable	Landroid/graphics/drawable/Drawable;
    //   683: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   686: ldc_w 426
    //   689: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   692: iadd
    //   693: istore 13
    //   695: iload 15
    //   697: iload 13
    //   699: isub
    //   700: istore 17
    //   702: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   705: ifne +4586 -> 5291
    //   708: aload_0
    //   709: aload_0
    //   710: getfield 420	org/telegram/ui/Cells/DialogCell:timeLeft	I
    //   713: iload 13
    //   715: isub
    //   716: putfield 428	org/telegram/ui/Cells/DialogCell:checkDrawLeft	I
    //   719: aload_0
    //   720: getfield 430	org/telegram/ui/Cells/DialogCell:dialogMuted	Z
    //   723: ifeq +4806 -> 5529
    //   726: aload_0
    //   727: getfield 237	org/telegram/ui/Cells/DialogCell:drawVerified	Z
    //   730: ifne +4799 -> 5529
    //   733: ldc_w 431
    //   736: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   739: getstatic 434	org/telegram/ui/ActionBar/Theme:dialogs_muteDrawable	Landroid/graphics/drawable/Drawable;
    //   742: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   745: iadd
    //   746: istore 14
    //   748: iload 17
    //   750: iload 14
    //   752: isub
    //   753: istore 17
    //   755: iload 17
    //   757: istore 15
    //   759: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   762: ifeq +18 -> 780
    //   765: aload_0
    //   766: aload_0
    //   767: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   770: iload 14
    //   772: iadd
    //   773: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   776: iload 17
    //   778: istore 15
    //   780: ldc_w 435
    //   783: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   786: iload 15
    //   788: invokestatic 439	java/lang/Math:max	(II)I
    //   791: istore 14
    //   793: aload_1
    //   794: bipush 10
    //   796: bipush 32
    //   798: invokevirtual 443	java/lang/String:replace	(CC)Ljava/lang/String;
    //   801: aload 11
    //   803: iload 14
    //   805: ldc_w 435
    //   808: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   811: isub
    //   812: i2f
    //   813: getstatic 449	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   816: invokestatic 455	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   819: astore 10
    //   821: new 403	android/text/StaticLayout
    //   824: astore 19
    //   826: aload 19
    //   828: aload 10
    //   830: aload 11
    //   832: iload 14
    //   834: getstatic 409	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   837: fconst_1
    //   838: fconst_0
    //   839: iconst_0
    //   840: invokespecial 412	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   843: aload_0
    //   844: aload 19
    //   846: putfield 457	org/telegram/ui/Cells/DialogCell:nameLayout	Landroid/text/StaticLayout;
    //   849: aload_0
    //   850: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   853: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   856: bipush 16
    //   858: iadd
    //   859: i2f
    //   860: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   863: isub
    //   864: istore 15
    //   866: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   869: ifne +4739 -> 5608
    //   872: aload_0
    //   873: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   876: i2f
    //   877: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   880: putfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   883: invokestatic 463	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   886: ifeq +4714 -> 5600
    //   889: ldc_w 464
    //   892: fstore 22
    //   894: fload 22
    //   896: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   899: istore 17
    //   901: aload_0
    //   902: getfield 105	org/telegram/ui/Cells/DialogCell:avatarImage	Lorg/telegram/messenger/ImageReceiver;
    //   905: iload 17
    //   907: aload_0
    //   908: getfield 147	org/telegram/ui/Cells/DialogCell:avatarTop	I
    //   911: ldc_w 465
    //   914: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   917: ldc_w 465
    //   920: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   923: invokevirtual 469	org/telegram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   926: aload_0
    //   927: getfield 381	org/telegram/ui/Cells/DialogCell:drawError	Z
    //   930: ifeq +4750 -> 5680
    //   933: ldc_w 470
    //   936: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   939: istore 17
    //   941: iload 15
    //   943: iload 17
    //   945: isub
    //   946: istore 15
    //   948: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   951: ifne +4705 -> 5656
    //   954: aload_0
    //   955: aload_0
    //   956: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   959: ldc_w 471
    //   962: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   965: isub
    //   966: putfield 473	org/telegram/ui/Cells/DialogCell:errorLeft	I
    //   969: aload 12
    //   971: astore 10
    //   973: iload 21
    //   975: ifeq +77 -> 1052
    //   978: aload 12
    //   980: astore 10
    //   982: aload 12
    //   984: ifnonnull +7 -> 991
    //   987: ldc -52
    //   989: astore 10
    //   991: aload 10
    //   993: invokeinterface 477 1 0
    //   998: astore 19
    //   1000: aload 19
    //   1002: astore 10
    //   1004: aload 19
    //   1006: invokevirtual 331	java/lang/String:length	()I
    //   1009: sipush 150
    //   1012: if_icmple +14 -> 1026
    //   1015: aload 19
    //   1017: iconst_0
    //   1018: sipush 150
    //   1021: invokevirtual 481	java/lang/String:substring	(II)Ljava/lang/String;
    //   1024: astore 10
    //   1026: aload 10
    //   1028: bipush 10
    //   1030: bipush 32
    //   1032: invokevirtual 443	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1035: getstatic 227	org/telegram/ui/ActionBar/Theme:dialogs_messagePaint	Landroid/text/TextPaint;
    //   1038: invokevirtual 344	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   1041: ldc 123
    //   1043: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1046: iconst_0
    //   1047: invokestatic 351	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   1050: astore 10
    //   1052: ldc_w 435
    //   1055: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1058: iload 15
    //   1060: invokestatic 439	java/lang/Math:max	(II)I
    //   1063: istore 15
    //   1065: aload 10
    //   1067: aload_2
    //   1068: iload 15
    //   1070: ldc_w 435
    //   1073: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1076: isub
    //   1077: i2f
    //   1078: getstatic 449	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1081: invokestatic 455	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1084: astore 19
    //   1086: new 403	android/text/StaticLayout
    //   1089: astore 10
    //   1091: aload 10
    //   1093: aload 19
    //   1095: aload_2
    //   1096: iload 15
    //   1098: getstatic 409	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1101: fconst_1
    //   1102: fconst_0
    //   1103: iconst_0
    //   1104: invokespecial 412	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1107: aload_0
    //   1108: aload 10
    //   1110: putfield 483	org/telegram/ui/Cells/DialogCell:messageLayout	Landroid/text/StaticLayout;
    //   1113: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   1116: ifeq +5036 -> 6152
    //   1119: aload_0
    //   1120: getfield 457	org/telegram/ui/Cells/DialogCell:nameLayout	Landroid/text/StaticLayout;
    //   1123: ifnull +117 -> 1240
    //   1126: aload_0
    //   1127: getfield 457	org/telegram/ui/Cells/DialogCell:nameLayout	Landroid/text/StaticLayout;
    //   1130: invokevirtual 486	android/text/StaticLayout:getLineCount	()I
    //   1133: ifle +107 -> 1240
    //   1136: aload_0
    //   1137: getfield 457	org/telegram/ui/Cells/DialogCell:nameLayout	Landroid/text/StaticLayout;
    //   1140: iconst_0
    //   1141: invokevirtual 490	android/text/StaticLayout:getLineLeft	(I)F
    //   1144: fstore 22
    //   1146: aload_0
    //   1147: getfield 457	org/telegram/ui/Cells/DialogCell:nameLayout	Landroid/text/StaticLayout;
    //   1150: iconst_0
    //   1151: invokevirtual 493	android/text/StaticLayout:getLineWidth	(I)F
    //   1154: f2d
    //   1155: invokestatic 401	java/lang/Math:ceil	(D)D
    //   1158: dstore 23
    //   1160: aload_0
    //   1161: getfield 430	org/telegram/ui/Cells/DialogCell:dialogMuted	Z
    //   1164: ifeq +4945 -> 6109
    //   1167: aload_0
    //   1168: getfield 237	org/telegram/ui/Cells/DialogCell:drawVerified	Z
    //   1171: ifne +4938 -> 6109
    //   1174: aload_0
    //   1175: aload_0
    //   1176: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   1179: i2d
    //   1180: iload 14
    //   1182: i2d
    //   1183: dload 23
    //   1185: dsub
    //   1186: dadd
    //   1187: ldc_w 431
    //   1190: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1193: i2d
    //   1194: dsub
    //   1195: getstatic 434	org/telegram/ui/ActionBar/Theme:dialogs_muteDrawable	Landroid/graphics/drawable/Drawable;
    //   1198: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   1201: i2d
    //   1202: dsub
    //   1203: d2i
    //   1204: putfield 495	org/telegram/ui/Cells/DialogCell:nameMuteLeft	I
    //   1207: fload 22
    //   1209: fconst_0
    //   1210: fcmpl
    //   1211: ifne +29 -> 1240
    //   1214: dload 23
    //   1216: iload 14
    //   1218: i2d
    //   1219: dcmpg
    //   1220: ifge +20 -> 1240
    //   1223: aload_0
    //   1224: aload_0
    //   1225: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   1228: i2d
    //   1229: iload 14
    //   1231: i2d
    //   1232: dload 23
    //   1234: dsub
    //   1235: dadd
    //   1236: d2i
    //   1237: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   1240: aload_0
    //   1241: getfield 483	org/telegram/ui/Cells/DialogCell:messageLayout	Landroid/text/StaticLayout;
    //   1244: ifnull +66 -> 1310
    //   1247: aload_0
    //   1248: getfield 483	org/telegram/ui/Cells/DialogCell:messageLayout	Landroid/text/StaticLayout;
    //   1251: invokevirtual 486	android/text/StaticLayout:getLineCount	()I
    //   1254: ifle +56 -> 1310
    //   1257: aload_0
    //   1258: getfield 483	org/telegram/ui/Cells/DialogCell:messageLayout	Landroid/text/StaticLayout;
    //   1261: iconst_0
    //   1262: invokevirtual 490	android/text/StaticLayout:getLineLeft	(I)F
    //   1265: fconst_0
    //   1266: fcmpl
    //   1267: ifne +43 -> 1310
    //   1270: aload_0
    //   1271: getfield 483	org/telegram/ui/Cells/DialogCell:messageLayout	Landroid/text/StaticLayout;
    //   1274: iconst_0
    //   1275: invokevirtual 493	android/text/StaticLayout:getLineWidth	(I)F
    //   1278: f2d
    //   1279: invokestatic 401	java/lang/Math:ceil	(D)D
    //   1282: dstore 23
    //   1284: dload 23
    //   1286: iload 15
    //   1288: i2d
    //   1289: dcmpg
    //   1290: ifge +20 -> 1310
    //   1293: aload_0
    //   1294: aload_0
    //   1295: getfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   1298: i2d
    //   1299: iload 15
    //   1301: i2d
    //   1302: dload 23
    //   1304: dsub
    //   1305: dadd
    //   1306: d2i
    //   1307: putfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   1310: return
    //   1311: iconst_0
    //   1312: istore 15
    //   1314: goto -1196 -> 118
    //   1317: ldc_w 497
    //   1320: astore 18
    //   1322: goto -1186 -> 136
    //   1325: aconst_null
    //   1326: astore 19
    //   1328: goto -1176 -> 152
    //   1331: aload_0
    //   1332: aload_0
    //   1333: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   1336: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   1339: i2f
    //   1340: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1343: isub
    //   1344: getstatic 283	org/telegram/ui/ActionBar/Theme:dialogs_lockDrawable	Landroid/graphics/drawable/Drawable;
    //   1347: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   1350: isub
    //   1351: putfield 279	org/telegram/ui/Cells/DialogCell:nameLockLeft	I
    //   1354: aload_0
    //   1355: ldc_w 421
    //   1358: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1361: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   1364: goto -1136 -> 228
    //   1367: aload_0
    //   1368: aload_0
    //   1369: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   1372: getfield 500	org/telegram/ui/Cells/DialogCell$CustomDialog:verified	Z
    //   1375: putfield 237	org/telegram/ui/Cells/DialogCell:drawVerified	Z
    //   1378: aload_0
    //   1379: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   1382: getfield 266	org/telegram/ui/Cells/DialogCell$CustomDialog:type	I
    //   1385: iconst_1
    //   1386: if_icmpne +150 -> 1536
    //   1389: aload_0
    //   1390: iconst_1
    //   1391: putfield 229	org/telegram/ui/Cells/DialogCell:drawNameGroup	Z
    //   1394: aload_0
    //   1395: ldc_w 501
    //   1398: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1401: putfield 269	org/telegram/ui/Cells/DialogCell:nameLockTop	I
    //   1404: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   1407: ifne +63 -> 1470
    //   1410: aload_0
    //   1411: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   1414: i2f
    //   1415: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1418: putfield 279	org/telegram/ui/Cells/DialogCell:nameLockLeft	I
    //   1421: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   1424: iconst_4
    //   1425: iadd
    //   1426: i2f
    //   1427: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1430: istore 21
    //   1432: aload_0
    //   1433: getfield 229	org/telegram/ui/Cells/DialogCell:drawNameGroup	Z
    //   1436: ifeq +23 -> 1459
    //   1439: getstatic 504	org/telegram/ui/ActionBar/Theme:dialogs_groupDrawable	Landroid/graphics/drawable/Drawable;
    //   1442: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   1445: istore 15
    //   1447: aload_0
    //   1448: iload 15
    //   1450: iload 21
    //   1452: iadd
    //   1453: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   1456: goto -1228 -> 228
    //   1459: getstatic 507	org/telegram/ui/ActionBar/Theme:dialogs_broadcastDrawable	Landroid/graphics/drawable/Drawable;
    //   1462: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   1465: istore 15
    //   1467: goto -20 -> 1447
    //   1470: aload_0
    //   1471: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   1474: istore 13
    //   1476: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   1479: i2f
    //   1480: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1483: istore 21
    //   1485: aload_0
    //   1486: getfield 229	org/telegram/ui/Cells/DialogCell:drawNameGroup	Z
    //   1489: ifeq +36 -> 1525
    //   1492: getstatic 504	org/telegram/ui/ActionBar/Theme:dialogs_groupDrawable	Landroid/graphics/drawable/Drawable;
    //   1495: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   1498: istore 15
    //   1500: aload_0
    //   1501: iload 13
    //   1503: iload 21
    //   1505: isub
    //   1506: iload 15
    //   1508: isub
    //   1509: putfield 279	org/telegram/ui/Cells/DialogCell:nameLockLeft	I
    //   1512: aload_0
    //   1513: ldc_w 421
    //   1516: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1519: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   1522: goto -1294 -> 228
    //   1525: getstatic 507	org/telegram/ui/ActionBar/Theme:dialogs_broadcastDrawable	Landroid/graphics/drawable/Drawable;
    //   1528: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   1531: istore 15
    //   1533: goto -33 -> 1500
    //   1536: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   1539: ifne +17 -> 1556
    //   1542: aload_0
    //   1543: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   1546: i2f
    //   1547: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1550: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   1553: goto -1325 -> 228
    //   1556: aload_0
    //   1557: ldc_w 421
    //   1560: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1563: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   1566: goto -1338 -> 228
    //   1569: aload_0
    //   1570: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   1573: getfield 509	org/telegram/ui/Cells/DialogCell$CustomDialog:message	Ljava/lang/String;
    //   1576: astore 19
    //   1578: aload 19
    //   1580: astore 10
    //   1582: aload 19
    //   1584: invokevirtual 331	java/lang/String:length	()I
    //   1587: sipush 150
    //   1590: if_icmple +14 -> 1604
    //   1593: aload 19
    //   1595: iconst_0
    //   1596: sipush 150
    //   1599: invokevirtual 481	java/lang/String:substring	(II)Ljava/lang/String;
    //   1602: astore 10
    //   1604: aload 18
    //   1606: iconst_2
    //   1607: anewarray 306	java/lang/Object
    //   1610: dup
    //   1611: iconst_0
    //   1612: aload 7
    //   1614: aastore
    //   1615: dup
    //   1616: iconst_1
    //   1617: aload 10
    //   1619: bipush 10
    //   1621: bipush 32
    //   1623: invokevirtual 443	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1626: aastore
    //   1627: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   1630: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   1633: astore 19
    //   1635: aload 12
    //   1637: astore 10
    //   1639: goto -1310 -> 329
    //   1642: aload_0
    //   1643: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   1646: getfield 509	org/telegram/ui/Cells/DialogCell$CustomDialog:message	Ljava/lang/String;
    //   1649: astore 18
    //   1651: iload 14
    //   1653: istore 15
    //   1655: aload 12
    //   1657: astore 10
    //   1659: aload 18
    //   1661: astore 19
    //   1663: aload_0
    //   1664: getfield 263	org/telegram/ui/Cells/DialogCell:customDialog	Lorg/telegram/ui/Cells/DialogCell$CustomDialog;
    //   1667: getfield 301	org/telegram/ui/Cells/DialogCell$CustomDialog:isMedia	Z
    //   1670: ifeq -1285 -> 385
    //   1673: getstatic 304	org/telegram/ui/ActionBar/Theme:dialogs_messagePrintingPaint	Landroid/text/TextPaint;
    //   1676: astore 10
    //   1678: iload 14
    //   1680: istore 15
    //   1682: aload 18
    //   1684: astore 19
    //   1686: goto -1301 -> 385
    //   1689: aload_0
    //   1690: iconst_0
    //   1691: putfield 363	org/telegram/ui/Cells/DialogCell:drawCount	Z
    //   1694: aload 5
    //   1696: astore 18
    //   1698: goto -1260 -> 438
    //   1701: aload_0
    //   1702: iconst_0
    //   1703: putfield 375	org/telegram/ui/Cells/DialogCell:drawCheck1	Z
    //   1706: aload_0
    //   1707: iconst_0
    //   1708: putfield 377	org/telegram/ui/Cells/DialogCell:drawCheck2	Z
    //   1711: aload_0
    //   1712: iconst_0
    //   1713: putfield 379	org/telegram/ui/Cells/DialogCell:drawClock	Z
    //   1716: aload_0
    //   1717: iconst_0
    //   1718: putfield 381	org/telegram/ui/Cells/DialogCell:drawError	Z
    //   1721: goto -1253 -> 468
    //   1724: aload_0
    //   1725: getfield 118	org/telegram/ui/Cells/DialogCell:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   1728: ifnull +495 -> 2223
    //   1731: aload_0
    //   1732: iconst_1
    //   1733: putfield 233	org/telegram/ui/Cells/DialogCell:drawNameLock	Z
    //   1736: aload_0
    //   1737: ldc_w 267
    //   1740: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1743: putfield 269	org/telegram/ui/Cells/DialogCell:nameLockTop	I
    //   1746: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   1749: ifne +438 -> 2187
    //   1752: aload_0
    //   1753: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   1756: i2f
    //   1757: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1760: putfield 279	org/telegram/ui/Cells/DialogCell:nameLockLeft	I
    //   1763: aload_0
    //   1764: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   1767: iconst_4
    //   1768: iadd
    //   1769: i2f
    //   1770: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   1773: getstatic 283	org/telegram/ui/ActionBar/Theme:dialogs_lockDrawable	Landroid/graphics/drawable/Drawable;
    //   1776: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   1779: iadd
    //   1780: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   1783: aload_0
    //   1784: getfield 511	org/telegram/ui/Cells/DialogCell:lastMessageDate	I
    //   1787: istore 14
    //   1789: iload 14
    //   1791: istore 17
    //   1793: aload_0
    //   1794: getfield 511	org/telegram/ui/Cells/DialogCell:lastMessageDate	I
    //   1797: ifne +26 -> 1823
    //   1800: iload 14
    //   1802: istore 17
    //   1804: aload_0
    //   1805: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   1808: ifnull +15 -> 1823
    //   1811: aload_0
    //   1812: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   1815: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   1818: getfield 518	org/telegram/tgnet/TLRPC$Message:date	I
    //   1821: istore 17
    //   1823: aload_0
    //   1824: getfield 206	org/telegram/ui/Cells/DialogCell:isDialogCell	Z
    //   1827: ifeq +758 -> 2585
    //   1830: aload_0
    //   1831: aload_0
    //   1832: getfield 98	org/telegram/ui/Cells/DialogCell:currentAccount	I
    //   1835: invokestatic 523	org/telegram/messenger/DataQuery:getInstance	(I)Lorg/telegram/messenger/DataQuery;
    //   1838: aload_0
    //   1839: getfield 212	org/telegram/ui/Cells/DialogCell:currentDialogId	J
    //   1842: invokevirtual 527	org/telegram/messenger/DataQuery:getDraft	(J)Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   1845: putfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   1848: aload_0
    //   1849: getfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   1852: ifnull +45 -> 1897
    //   1855: aload_0
    //   1856: getfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   1859: getfield 532	org/telegram/tgnet/TLRPC$DraftMessage:message	Ljava/lang/String;
    //   1862: invokestatic 536	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   1865: ifeq +13 -> 1878
    //   1868: aload_0
    //   1869: getfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   1872: getfield 539	org/telegram/tgnet/TLRPC$DraftMessage:reply_to_msg_id	I
    //   1875: ifeq +102 -> 1977
    //   1878: iload 17
    //   1880: aload_0
    //   1881: getfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   1884: getfield 540	org/telegram/tgnet/TLRPC$DraftMessage:date	I
    //   1887: if_icmple +10 -> 1897
    //   1890: aload_0
    //   1891: getfield 542	org/telegram/ui/Cells/DialogCell:unreadCount	I
    //   1894: ifne +83 -> 1977
    //   1897: aload_0
    //   1898: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1901: invokestatic 548	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   1904: ifeq +46 -> 1950
    //   1907: aload_0
    //   1908: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1911: getfield 553	org/telegram/tgnet/TLRPC$Chat:megagroup	Z
    //   1914: ifne +36 -> 1950
    //   1917: aload_0
    //   1918: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1921: getfield 556	org/telegram/tgnet/TLRPC$Chat:creator	Z
    //   1924: ifne +26 -> 1950
    //   1927: aload_0
    //   1928: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1931: getfield 560	org/telegram/tgnet/TLRPC$Chat:admin_rights	Lorg/telegram/tgnet/TLRPC$TL_channelAdminRights;
    //   1934: ifnull +43 -> 1977
    //   1937: aload_0
    //   1938: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1941: getfield 560	org/telegram/tgnet/TLRPC$Chat:admin_rights	Lorg/telegram/tgnet/TLRPC$TL_channelAdminRights;
    //   1944: getfield 565	org/telegram/tgnet/TLRPC$TL_channelAdminRights:post_messages	Z
    //   1947: ifeq +30 -> 1977
    //   1950: aload_0
    //   1951: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1954: ifnull +28 -> 1982
    //   1957: aload_0
    //   1958: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1961: getfield 568	org/telegram/tgnet/TLRPC$Chat:left	Z
    //   1964: ifne +13 -> 1977
    //   1967: aload_0
    //   1968: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   1971: getfield 571	org/telegram/tgnet/TLRPC$Chat:kicked	Z
    //   1974: ifeq +8 -> 1982
    //   1977: aload_0
    //   1978: aconst_null
    //   1979: putfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   1982: aload 10
    //   1984: ifnull +609 -> 2593
    //   1987: aload 10
    //   1989: astore 19
    //   1991: aload_0
    //   1992: aload 10
    //   1994: putfield 120	org/telegram/ui/Cells/DialogCell:lastPrintString	Ljava/lang/CharSequence;
    //   1997: getstatic 304	org/telegram/ui/ActionBar/Theme:dialogs_messagePrintingPaint	Landroid/text/TextPaint;
    //   2000: astore 12
    //   2002: iload 15
    //   2004: istore 21
    //   2006: aload 19
    //   2008: astore 10
    //   2010: iload 16
    //   2012: istore 14
    //   2014: aload 12
    //   2016: astore 19
    //   2018: iload 13
    //   2020: istore 17
    //   2022: aload_0
    //   2023: getfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   2026: ifnull +2451 -> 4477
    //   2029: aload_0
    //   2030: getfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   2033: getfield 540	org/telegram/tgnet/TLRPC$DraftMessage:date	I
    //   2036: i2l
    //   2037: invokestatic 358	org/telegram/messenger/LocaleController:stringForMessageListDate	(J)Ljava/lang/String;
    //   2040: astore 18
    //   2042: aload_0
    //   2043: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   2046: ifnonnull +2480 -> 4526
    //   2049: aload_0
    //   2050: iconst_0
    //   2051: putfield 375	org/telegram/ui/Cells/DialogCell:drawCheck1	Z
    //   2054: aload_0
    //   2055: iconst_0
    //   2056: putfield 377	org/telegram/ui/Cells/DialogCell:drawCheck2	Z
    //   2059: aload_0
    //   2060: iconst_0
    //   2061: putfield 379	org/telegram/ui/Cells/DialogCell:drawClock	Z
    //   2064: aload_0
    //   2065: iconst_0
    //   2066: putfield 363	org/telegram/ui/Cells/DialogCell:drawCount	Z
    //   2069: aload_0
    //   2070: iconst_0
    //   2071: putfield 573	org/telegram/ui/Cells/DialogCell:drawMention	Z
    //   2074: aload_0
    //   2075: iconst_0
    //   2076: putfield 381	org/telegram/ui/Cells/DialogCell:drawError	Z
    //   2079: aload 6
    //   2081: astore 4
    //   2083: aload_0
    //   2084: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2087: ifnull +2778 -> 4865
    //   2090: aload_0
    //   2091: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2094: getfield 576	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2097: astore 7
    //   2099: aload 11
    //   2101: astore 6
    //   2103: aload 18
    //   2105: astore 20
    //   2107: iload 17
    //   2109: istore 21
    //   2111: aload_3
    //   2112: astore 9
    //   2114: aload 19
    //   2116: astore_2
    //   2117: aload 6
    //   2119: astore 11
    //   2121: iload 14
    //   2123: istore 13
    //   2125: aload 4
    //   2127: astore 5
    //   2129: aload 10
    //   2131: astore 12
    //   2133: aload 7
    //   2135: astore_1
    //   2136: aload 7
    //   2138: invokevirtual 331	java/lang/String:length	()I
    //   2141: ifne -1588 -> 553
    //   2144: ldc_w 578
    //   2147: ldc_w 579
    //   2150: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2153: astore_1
    //   2154: aload 18
    //   2156: astore 20
    //   2158: iload 17
    //   2160: istore 21
    //   2162: aload_3
    //   2163: astore 9
    //   2165: aload 19
    //   2167: astore_2
    //   2168: aload 6
    //   2170: astore 11
    //   2172: iload 14
    //   2174: istore 13
    //   2176: aload 4
    //   2178: astore 5
    //   2180: aload 10
    //   2182: astore 12
    //   2184: goto -1631 -> 553
    //   2187: aload_0
    //   2188: aload_0
    //   2189: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   2192: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   2195: i2f
    //   2196: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2199: isub
    //   2200: getstatic 283	org/telegram/ui/ActionBar/Theme:dialogs_lockDrawable	Landroid/graphics/drawable/Drawable;
    //   2203: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   2206: isub
    //   2207: putfield 279	org/telegram/ui/Cells/DialogCell:nameLockLeft	I
    //   2210: aload_0
    //   2211: ldc_w 421
    //   2214: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2217: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   2220: goto -437 -> 1783
    //   2223: aload_0
    //   2224: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2227: ifnull +209 -> 2436
    //   2230: aload_0
    //   2231: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2234: getfield 582	org/telegram/tgnet/TLRPC$Chat:id	I
    //   2237: iflt +23 -> 2260
    //   2240: aload_0
    //   2241: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2244: invokestatic 548	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   2247: ifeq +94 -> 2341
    //   2250: aload_0
    //   2251: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2254: getfield 553	org/telegram/tgnet/TLRPC$Chat:megagroup	Z
    //   2257: ifne +84 -> 2341
    //   2260: aload_0
    //   2261: iconst_1
    //   2262: putfield 231	org/telegram/ui/Cells/DialogCell:drawNameBroadcast	Z
    //   2265: aload_0
    //   2266: ldc_w 267
    //   2269: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2272: putfield 269	org/telegram/ui/Cells/DialogCell:nameLockTop	I
    //   2275: aload_0
    //   2276: aload_0
    //   2277: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   2280: getfield 583	org/telegram/tgnet/TLRPC$Chat:verified	Z
    //   2283: putfield 237	org/telegram/ui/Cells/DialogCell:drawVerified	Z
    //   2286: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   2289: ifne +81 -> 2370
    //   2292: aload_0
    //   2293: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   2296: i2f
    //   2297: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2300: putfield 279	org/telegram/ui/Cells/DialogCell:nameLockLeft	I
    //   2303: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   2306: iconst_4
    //   2307: iadd
    //   2308: i2f
    //   2309: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2312: istore 14
    //   2314: aload_0
    //   2315: getfield 229	org/telegram/ui/Cells/DialogCell:drawNameGroup	Z
    //   2318: ifeq +41 -> 2359
    //   2321: getstatic 504	org/telegram/ui/ActionBar/Theme:dialogs_groupDrawable	Landroid/graphics/drawable/Drawable;
    //   2324: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   2327: istore 17
    //   2329: aload_0
    //   2330: iload 17
    //   2332: iload 14
    //   2334: iadd
    //   2335: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   2338: goto -555 -> 1783
    //   2341: aload_0
    //   2342: iconst_1
    //   2343: putfield 229	org/telegram/ui/Cells/DialogCell:drawNameGroup	Z
    //   2346: aload_0
    //   2347: ldc_w 501
    //   2350: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2353: putfield 269	org/telegram/ui/Cells/DialogCell:nameLockTop	I
    //   2356: goto -81 -> 2275
    //   2359: getstatic 507	org/telegram/ui/ActionBar/Theme:dialogs_broadcastDrawable	Landroid/graphics/drawable/Drawable;
    //   2362: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   2365: istore 17
    //   2367: goto -38 -> 2329
    //   2370: aload_0
    //   2371: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   2374: istore 14
    //   2376: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   2379: i2f
    //   2380: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2383: istore 21
    //   2385: aload_0
    //   2386: getfield 229	org/telegram/ui/Cells/DialogCell:drawNameGroup	Z
    //   2389: ifeq +36 -> 2425
    //   2392: getstatic 504	org/telegram/ui/ActionBar/Theme:dialogs_groupDrawable	Landroid/graphics/drawable/Drawable;
    //   2395: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   2398: istore 17
    //   2400: aload_0
    //   2401: iload 14
    //   2403: iload 21
    //   2405: isub
    //   2406: iload 17
    //   2408: isub
    //   2409: putfield 279	org/telegram/ui/Cells/DialogCell:nameLockLeft	I
    //   2412: aload_0
    //   2413: ldc_w 421
    //   2416: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2419: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   2422: goto -639 -> 1783
    //   2425: getstatic 507	org/telegram/ui/ActionBar/Theme:dialogs_broadcastDrawable	Landroid/graphics/drawable/Drawable;
    //   2428: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   2431: istore 17
    //   2433: goto -33 -> 2400
    //   2436: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   2439: ifne +97 -> 2536
    //   2442: aload_0
    //   2443: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   2446: i2f
    //   2447: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2450: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   2453: aload_0
    //   2454: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   2457: ifnull -674 -> 1783
    //   2460: aload_0
    //   2461: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   2464: getfield 588	org/telegram/tgnet/TLRPC$User:bot	Z
    //   2467: ifeq +55 -> 2522
    //   2470: aload_0
    //   2471: iconst_1
    //   2472: putfield 235	org/telegram/ui/Cells/DialogCell:drawNameBot	Z
    //   2475: aload_0
    //   2476: ldc_w 267
    //   2479: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2482: putfield 269	org/telegram/ui/Cells/DialogCell:nameLockTop	I
    //   2485: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   2488: ifne +61 -> 2549
    //   2491: aload_0
    //   2492: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   2495: i2f
    //   2496: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2499: putfield 279	org/telegram/ui/Cells/DialogCell:nameLockLeft	I
    //   2502: aload_0
    //   2503: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   2506: iconst_4
    //   2507: iadd
    //   2508: i2f
    //   2509: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2512: getstatic 591	org/telegram/ui/ActionBar/Theme:dialogs_botDrawable	Landroid/graphics/drawable/Drawable;
    //   2515: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   2518: iadd
    //   2519: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   2522: aload_0
    //   2523: aload_0
    //   2524: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   2527: getfield 592	org/telegram/tgnet/TLRPC$User:verified	Z
    //   2530: putfield 237	org/telegram/ui/Cells/DialogCell:drawVerified	Z
    //   2533: goto -750 -> 1783
    //   2536: aload_0
    //   2537: ldc_w 421
    //   2540: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2543: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   2546: goto -93 -> 2453
    //   2549: aload_0
    //   2550: aload_0
    //   2551: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   2554: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   2557: i2f
    //   2558: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2561: isub
    //   2562: getstatic 591	org/telegram/ui/ActionBar/Theme:dialogs_botDrawable	Landroid/graphics/drawable/Drawable;
    //   2565: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   2568: isub
    //   2569: putfield 279	org/telegram/ui/Cells/DialogCell:nameLockLeft	I
    //   2572: aload_0
    //   2573: ldc_w 421
    //   2576: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2579: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   2582: goto -60 -> 2522
    //   2585: aload_0
    //   2586: aconst_null
    //   2587: putfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   2590: goto -608 -> 1982
    //   2593: aload_0
    //   2594: aconst_null
    //   2595: putfield 120	org/telegram/ui/Cells/DialogCell:lastPrintString	Ljava/lang/CharSequence;
    //   2598: aload_0
    //   2599: getfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   2602: ifnull +218 -> 2820
    //   2605: iconst_0
    //   2606: istore 17
    //   2608: aload_0
    //   2609: getfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   2612: getfield 532	org/telegram/tgnet/TLRPC$DraftMessage:message	Ljava/lang/String;
    //   2615: invokestatic 536	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   2618: ifeq +62 -> 2680
    //   2621: ldc_w 594
    //   2624: ldc_w 595
    //   2627: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2630: astore 19
    //   2632: aload 19
    //   2634: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   2637: astore 10
    //   2639: aload 10
    //   2641: new 320	android/text/style/ForegroundColorSpan
    //   2644: dup
    //   2645: ldc_w 597
    //   2648: invokestatic 326	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   2651: invokespecial 328	android/text/style/ForegroundColorSpan:<init>	(I)V
    //   2654: iconst_0
    //   2655: aload 19
    //   2657: invokevirtual 331	java/lang/String:length	()I
    //   2660: bipush 33
    //   2662: invokevirtual 336	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   2665: aload 12
    //   2667: astore 19
    //   2669: iload 16
    //   2671: istore 14
    //   2673: iload 15
    //   2675: istore 21
    //   2677: goto -655 -> 2022
    //   2680: aload_0
    //   2681: getfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   2684: getfield 532	org/telegram/tgnet/TLRPC$DraftMessage:message	Ljava/lang/String;
    //   2687: astore 19
    //   2689: aload 19
    //   2691: astore 10
    //   2693: aload 19
    //   2695: invokevirtual 331	java/lang/String:length	()I
    //   2698: sipush 150
    //   2701: if_icmple +14 -> 2715
    //   2704: aload 19
    //   2706: iconst_0
    //   2707: sipush 150
    //   2710: invokevirtual 481	java/lang/String:substring	(II)Ljava/lang/String;
    //   2713: astore 10
    //   2715: ldc_w 594
    //   2718: ldc_w 595
    //   2721: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2724: astore 19
    //   2726: aload 18
    //   2728: iconst_2
    //   2729: anewarray 306	java/lang/Object
    //   2732: dup
    //   2733: iconst_0
    //   2734: aload 19
    //   2736: aastore
    //   2737: dup
    //   2738: iconst_1
    //   2739: aload 10
    //   2741: bipush 10
    //   2743: bipush 32
    //   2745: invokevirtual 443	java/lang/String:replace	(CC)Ljava/lang/String;
    //   2748: aastore
    //   2749: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2752: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   2755: astore 10
    //   2757: aload 10
    //   2759: new 320	android/text/style/ForegroundColorSpan
    //   2762: dup
    //   2763: ldc_w 597
    //   2766: invokestatic 326	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   2769: invokespecial 328	android/text/style/ForegroundColorSpan:<init>	(I)V
    //   2772: iconst_0
    //   2773: aload 19
    //   2775: invokevirtual 331	java/lang/String:length	()I
    //   2778: iconst_1
    //   2779: iadd
    //   2780: bipush 33
    //   2782: invokevirtual 336	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   2785: aload 10
    //   2787: getstatic 227	org/telegram/ui/ActionBar/Theme:dialogs_messagePaint	Landroid/text/TextPaint;
    //   2790: invokevirtual 344	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   2793: ldc_w 345
    //   2796: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   2799: iconst_0
    //   2800: invokestatic 351	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   2803: astore 10
    //   2805: aload 12
    //   2807: astore 19
    //   2809: iload 16
    //   2811: istore 14
    //   2813: iload 15
    //   2815: istore 21
    //   2817: goto -795 -> 2022
    //   2820: aload_0
    //   2821: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   2824: ifnonnull +405 -> 3229
    //   2827: iload 13
    //   2829: istore 17
    //   2831: aload 12
    //   2833: astore 19
    //   2835: iload 16
    //   2837: istore 14
    //   2839: aload 9
    //   2841: astore 10
    //   2843: iload 15
    //   2845: istore 21
    //   2847: aload_0
    //   2848: getfield 118	org/telegram/ui/Cells/DialogCell:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   2851: ifnull -829 -> 2022
    //   2854: getstatic 304	org/telegram/ui/ActionBar/Theme:dialogs_messagePrintingPaint	Landroid/text/TextPaint;
    //   2857: astore 12
    //   2859: aload_0
    //   2860: getfield 118	org/telegram/ui/Cells/DialogCell:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   2863: instanceof 599
    //   2866: ifeq +33 -> 2899
    //   2869: ldc_w 601
    //   2872: ldc_w 602
    //   2875: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2878: astore 10
    //   2880: iload 13
    //   2882: istore 17
    //   2884: aload 12
    //   2886: astore 19
    //   2888: iload 16
    //   2890: istore 14
    //   2892: iload 15
    //   2894: istore 21
    //   2896: goto -874 -> 2022
    //   2899: aload_0
    //   2900: getfield 118	org/telegram/ui/Cells/DialogCell:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   2903: instanceof 604
    //   2906: ifeq +103 -> 3009
    //   2909: aload_0
    //   2910: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   2913: ifnull +57 -> 2970
    //   2916: aload_0
    //   2917: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   2920: getfield 607	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   2923: ifnull +47 -> 2970
    //   2926: ldc_w 609
    //   2929: ldc_w 610
    //   2932: iconst_1
    //   2933: anewarray 306	java/lang/Object
    //   2936: dup
    //   2937: iconst_0
    //   2938: aload_0
    //   2939: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   2942: getfield 607	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   2945: aastore
    //   2946: invokestatic 614	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   2949: astore 10
    //   2951: iload 13
    //   2953: istore 17
    //   2955: aload 12
    //   2957: astore 19
    //   2959: iload 16
    //   2961: istore 14
    //   2963: iload 15
    //   2965: istore 21
    //   2967: goto -945 -> 2022
    //   2970: ldc_w 609
    //   2973: ldc_w 610
    //   2976: iconst_1
    //   2977: anewarray 306	java/lang/Object
    //   2980: dup
    //   2981: iconst_0
    //   2982: ldc -52
    //   2984: aastore
    //   2985: invokestatic 614	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   2988: astore 10
    //   2990: iload 13
    //   2992: istore 17
    //   2994: aload 12
    //   2996: astore 19
    //   2998: iload 16
    //   3000: istore 14
    //   3002: iload 15
    //   3004: istore 21
    //   3006: goto -984 -> 2022
    //   3009: aload_0
    //   3010: getfield 118	org/telegram/ui/Cells/DialogCell:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   3013: instanceof 616
    //   3016: ifeq +33 -> 3049
    //   3019: ldc_w 618
    //   3022: ldc_w 619
    //   3025: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   3028: astore 10
    //   3030: iload 13
    //   3032: istore 17
    //   3034: aload 12
    //   3036: astore 19
    //   3038: iload 16
    //   3040: istore 14
    //   3042: iload 15
    //   3044: istore 21
    //   3046: goto -1024 -> 2022
    //   3049: iload 13
    //   3051: istore 17
    //   3053: aload 12
    //   3055: astore 19
    //   3057: iload 16
    //   3059: istore 14
    //   3061: aload 9
    //   3063: astore 10
    //   3065: iload 15
    //   3067: istore 21
    //   3069: aload_0
    //   3070: getfield 118	org/telegram/ui/Cells/DialogCell:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   3073: instanceof 621
    //   3076: ifeq -1054 -> 2022
    //   3079: aload_0
    //   3080: getfield 118	org/telegram/ui/Cells/DialogCell:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   3083: getfield 626	org/telegram/tgnet/TLRPC$EncryptedChat:admin_id	I
    //   3086: aload_0
    //   3087: getfield 98	org/telegram/ui/Cells/DialogCell:currentAccount	I
    //   3090: invokestatic 629	org/telegram/messenger/UserConfig:getInstance	(I)Lorg/telegram/messenger/UserConfig;
    //   3093: invokevirtual 632	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   3096: if_icmpne +103 -> 3199
    //   3099: aload_0
    //   3100: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   3103: ifnull +57 -> 3160
    //   3106: aload_0
    //   3107: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   3110: getfield 607	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   3113: ifnull +47 -> 3160
    //   3116: ldc_w 634
    //   3119: ldc_w 635
    //   3122: iconst_1
    //   3123: anewarray 306	java/lang/Object
    //   3126: dup
    //   3127: iconst_0
    //   3128: aload_0
    //   3129: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   3132: getfield 607	org/telegram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   3135: aastore
    //   3136: invokestatic 614	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   3139: astore 10
    //   3141: iload 13
    //   3143: istore 17
    //   3145: aload 12
    //   3147: astore 19
    //   3149: iload 16
    //   3151: istore 14
    //   3153: iload 15
    //   3155: istore 21
    //   3157: goto -1135 -> 2022
    //   3160: ldc_w 634
    //   3163: ldc_w 635
    //   3166: iconst_1
    //   3167: anewarray 306	java/lang/Object
    //   3170: dup
    //   3171: iconst_0
    //   3172: ldc -52
    //   3174: aastore
    //   3175: invokestatic 614	org/telegram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   3178: astore 10
    //   3180: iload 13
    //   3182: istore 17
    //   3184: aload 12
    //   3186: astore 19
    //   3188: iload 16
    //   3190: istore 14
    //   3192: iload 15
    //   3194: istore 21
    //   3196: goto -1174 -> 2022
    //   3199: ldc_w 637
    //   3202: ldc_w 638
    //   3205: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   3208: astore 10
    //   3210: iload 13
    //   3212: istore 17
    //   3214: aload 12
    //   3216: astore 19
    //   3218: iload 16
    //   3220: istore 14
    //   3222: iload 15
    //   3224: istore 21
    //   3226: goto -1204 -> 2022
    //   3229: aconst_null
    //   3230: astore 19
    //   3232: aconst_null
    //   3233: astore 10
    //   3235: aload_0
    //   3236: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3239: invokevirtual 641	org/telegram/messenger/MessageObject:isFromUser	()Z
    //   3242: ifeq +74 -> 3316
    //   3245: aload_0
    //   3246: getfield 98	org/telegram/ui/Cells/DialogCell:currentAccount	I
    //   3249: invokestatic 184	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   3252: aload_0
    //   3253: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3256: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3259: getfield 644	org/telegram/tgnet/TLRPC$Message:from_id	I
    //   3262: invokestatic 370	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3265: invokevirtual 648	org/telegram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$User;
    //   3268: astore 19
    //   3270: aload_0
    //   3271: getfield 178	org/telegram/ui/Cells/DialogCell:dialogsType	I
    //   3274: iconst_3
    //   3275: if_icmpne +72 -> 3347
    //   3278: aload_0
    //   3279: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   3282: invokestatic 245	org/telegram/messenger/UserObject:isUserSelf	(Lorg/telegram/tgnet/TLRPC$User;)Z
    //   3285: ifeq +62 -> 3347
    //   3288: ldc_w 650
    //   3291: ldc_w 651
    //   3294: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   3297: astore 10
    //   3299: iconst_0
    //   3300: istore 21
    //   3302: iconst_0
    //   3303: istore 14
    //   3305: iload 13
    //   3307: istore 17
    //   3309: aload 12
    //   3311: astore 19
    //   3313: goto -1291 -> 2022
    //   3316: aload_0
    //   3317: getfield 98	org/telegram/ui/Cells/DialogCell:currentAccount	I
    //   3320: invokestatic 184	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
    //   3323: aload_0
    //   3324: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3327: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3330: getfield 655	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
    //   3333: getfield 660	org/telegram/tgnet/TLRPC$Peer:channel_id	I
    //   3336: invokestatic 370	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3339: invokevirtual 664	org/telegram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/telegram/tgnet/TLRPC$Chat;
    //   3342: astore 10
    //   3344: goto -74 -> 3270
    //   3347: aload_0
    //   3348: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3351: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3354: instanceof 666
    //   3357: ifeq +68 -> 3425
    //   3360: aload_0
    //   3361: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   3364: invokestatic 548	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   3367: ifeq +46 -> 3413
    //   3370: aload_0
    //   3371: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3374: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3377: getfield 670	org/telegram/tgnet/TLRPC$Message:action	Lorg/telegram/tgnet/TLRPC$MessageAction;
    //   3380: instanceof 672
    //   3383: ifeq +30 -> 3413
    //   3386: ldc -52
    //   3388: astore 10
    //   3390: iconst_0
    //   3391: istore 15
    //   3393: getstatic 304	org/telegram/ui/ActionBar/Theme:dialogs_messagePrintingPaint	Landroid/text/TextPaint;
    //   3396: astore 19
    //   3398: iload 13
    //   3400: istore 17
    //   3402: iload 16
    //   3404: istore 14
    //   3406: iload 15
    //   3408: istore 21
    //   3410: goto -1388 -> 2022
    //   3413: aload_0
    //   3414: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3417: getfield 259	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   3420: astore 10
    //   3422: goto -29 -> 3393
    //   3425: aload_0
    //   3426: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   3429: ifnull +649 -> 4078
    //   3432: aload_0
    //   3433: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   3436: getfield 582	org/telegram/tgnet/TLRPC$Chat:id	I
    //   3439: ifle +639 -> 4078
    //   3442: aload 10
    //   3444: ifnonnull +634 -> 4078
    //   3447: aload_0
    //   3448: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3451: invokevirtual 675	org/telegram/messenger/MessageObject:isOutOwner	()Z
    //   3454: ifeq +169 -> 3623
    //   3457: ldc_w 293
    //   3460: ldc_w 294
    //   3463: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   3466: astore 19
    //   3468: iconst_0
    //   3469: istore 17
    //   3471: aload_0
    //   3472: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3475: getfield 678	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   3478: ifnull +199 -> 3677
    //   3481: aload_0
    //   3482: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3485: getfield 678	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   3488: invokeinterface 477 1 0
    //   3493: astore 5
    //   3495: aload 5
    //   3497: astore 10
    //   3499: aload 5
    //   3501: invokevirtual 331	java/lang/String:length	()I
    //   3504: sipush 150
    //   3507: if_icmple +14 -> 3521
    //   3510: aload 5
    //   3512: iconst_0
    //   3513: sipush 150
    //   3516: invokevirtual 481	java/lang/String:substring	(II)Ljava/lang/String;
    //   3519: astore 10
    //   3521: aload 18
    //   3523: iconst_2
    //   3524: anewarray 306	java/lang/Object
    //   3527: dup
    //   3528: iconst_0
    //   3529: aload 19
    //   3531: aastore
    //   3532: dup
    //   3533: iconst_1
    //   3534: aload 10
    //   3536: bipush 10
    //   3538: bipush 32
    //   3540: invokevirtual 443	java/lang/String:replace	(CC)Ljava/lang/String;
    //   3543: aastore
    //   3544: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3547: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   3550: astore 10
    //   3552: aload 10
    //   3554: invokevirtual 332	android/text/SpannableStringBuilder:length	()I
    //   3557: ifle +31 -> 3588
    //   3560: aload 10
    //   3562: new 320	android/text/style/ForegroundColorSpan
    //   3565: dup
    //   3566: ldc_w 338
    //   3569: invokestatic 326	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   3572: invokespecial 328	android/text/style/ForegroundColorSpan:<init>	(I)V
    //   3575: iconst_0
    //   3576: aload 19
    //   3578: invokevirtual 331	java/lang/String:length	()I
    //   3581: iconst_1
    //   3582: iadd
    //   3583: bipush 33
    //   3585: invokevirtual 336	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   3588: aload 10
    //   3590: getstatic 227	org/telegram/ui/ActionBar/Theme:dialogs_messagePaint	Landroid/text/TextPaint;
    //   3593: invokevirtual 344	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   3596: ldc_w 345
    //   3599: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   3602: iconst_0
    //   3603: invokestatic 351	org/telegram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   3606: astore 10
    //   3608: aload 12
    //   3610: astore 19
    //   3612: iload 16
    //   3614: istore 14
    //   3616: iload 15
    //   3618: istore 21
    //   3620: goto -1598 -> 2022
    //   3623: aload 19
    //   3625: ifnull +21 -> 3646
    //   3628: aload 19
    //   3630: invokestatic 682	org/telegram/messenger/UserObject:getFirstName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   3633: ldc_w 684
    //   3636: ldc -52
    //   3638: invokevirtual 687	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3641: astore 19
    //   3643: goto -175 -> 3468
    //   3646: aload 10
    //   3648: ifnull +21 -> 3669
    //   3651: aload 10
    //   3653: getfield 576	org/telegram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   3656: ldc_w 684
    //   3659: ldc -52
    //   3661: invokevirtual 687	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   3664: astore 19
    //   3666: goto -198 -> 3468
    //   3669: ldc_w 689
    //   3672: astore 19
    //   3674: goto -206 -> 3468
    //   3677: aload_0
    //   3678: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3681: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3684: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3687: ifnull +296 -> 3983
    //   3690: aload_0
    //   3691: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3694: invokevirtual 696	org/telegram/messenger/MessageObject:isMediaEmpty	()Z
    //   3697: ifne +286 -> 3983
    //   3700: getstatic 304	org/telegram/ui/ActionBar/Theme:dialogs_messagePrintingPaint	Landroid/text/TextPaint;
    //   3703: astore 12
    //   3705: aload_0
    //   3706: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3709: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3712: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3715: instanceof 698
    //   3718: ifeq +127 -> 3845
    //   3721: getstatic 250	android/os/Build$VERSION:SDK_INT	I
    //   3724: bipush 18
    //   3726: if_icmplt +77 -> 3803
    //   3729: ldc_w 700
    //   3732: iconst_2
    //   3733: anewarray 306	java/lang/Object
    //   3736: dup
    //   3737: iconst_0
    //   3738: aload 19
    //   3740: aastore
    //   3741: dup
    //   3742: iconst_1
    //   3743: aload_0
    //   3744: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3747: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3750: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3753: getfield 706	org/telegram/tgnet/TLRPC$MessageMedia:game	Lorg/telegram/tgnet/TLRPC$TL_game;
    //   3756: getfield 709	org/telegram/tgnet/TLRPC$TL_game:title	Ljava/lang/String;
    //   3759: aastore
    //   3760: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3763: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   3766: astore 10
    //   3768: aload 10
    //   3770: new 320	android/text/style/ForegroundColorSpan
    //   3773: dup
    //   3774: ldc_w 322
    //   3777: invokestatic 326	org/telegram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   3780: invokespecial 328	android/text/style/ForegroundColorSpan:<init>	(I)V
    //   3783: aload 19
    //   3785: invokevirtual 331	java/lang/String:length	()I
    //   3788: iconst_2
    //   3789: iadd
    //   3790: aload 10
    //   3792: invokevirtual 332	android/text/SpannableStringBuilder:length	()I
    //   3795: bipush 33
    //   3797: invokevirtual 336	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   3800: goto -248 -> 3552
    //   3803: ldc_w 711
    //   3806: iconst_2
    //   3807: anewarray 306	java/lang/Object
    //   3810: dup
    //   3811: iconst_0
    //   3812: aload 19
    //   3814: aastore
    //   3815: dup
    //   3816: iconst_1
    //   3817: aload_0
    //   3818: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3821: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3824: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   3827: getfield 706	org/telegram/tgnet/TLRPC$MessageMedia:game	Lorg/telegram/tgnet/TLRPC$TL_game;
    //   3830: getfield 709	org/telegram/tgnet/TLRPC$TL_game:title	Ljava/lang/String;
    //   3833: aastore
    //   3834: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3837: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   3840: astore 10
    //   3842: goto -74 -> 3768
    //   3845: aload_0
    //   3846: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3849: getfield 712	org/telegram/messenger/MessageObject:type	I
    //   3852: bipush 14
    //   3854: if_icmpne +97 -> 3951
    //   3857: getstatic 250	android/os/Build$VERSION:SDK_INT	I
    //   3860: bipush 18
    //   3862: if_icmplt +46 -> 3908
    //   3865: ldc_w 714
    //   3868: iconst_3
    //   3869: anewarray 306	java/lang/Object
    //   3872: dup
    //   3873: iconst_0
    //   3874: aload 19
    //   3876: aastore
    //   3877: dup
    //   3878: iconst_1
    //   3879: aload_0
    //   3880: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3883: invokevirtual 717	org/telegram/messenger/MessageObject:getMusicAuthor	()Ljava/lang/String;
    //   3886: aastore
    //   3887: dup
    //   3888: iconst_2
    //   3889: aload_0
    //   3890: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3893: invokevirtual 720	org/telegram/messenger/MessageObject:getMusicTitle	()Ljava/lang/String;
    //   3896: aastore
    //   3897: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3900: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   3903: astore 10
    //   3905: goto -137 -> 3768
    //   3908: ldc_w 722
    //   3911: iconst_3
    //   3912: anewarray 306	java/lang/Object
    //   3915: dup
    //   3916: iconst_0
    //   3917: aload 19
    //   3919: aastore
    //   3920: dup
    //   3921: iconst_1
    //   3922: aload_0
    //   3923: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3926: invokevirtual 717	org/telegram/messenger/MessageObject:getMusicAuthor	()Ljava/lang/String;
    //   3929: aastore
    //   3930: dup
    //   3931: iconst_2
    //   3932: aload_0
    //   3933: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3936: invokevirtual 720	org/telegram/messenger/MessageObject:getMusicTitle	()Ljava/lang/String;
    //   3939: aastore
    //   3940: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3943: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   3946: astore 10
    //   3948: goto -180 -> 3768
    //   3951: aload 18
    //   3953: iconst_2
    //   3954: anewarray 306	java/lang/Object
    //   3957: dup
    //   3958: iconst_0
    //   3959: aload 19
    //   3961: aastore
    //   3962: dup
    //   3963: iconst_1
    //   3964: aload_0
    //   3965: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3968: getfield 259	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   3971: aastore
    //   3972: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3975: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   3978: astore 10
    //   3980: goto -212 -> 3768
    //   3983: aload_0
    //   3984: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   3987: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   3990: getfield 723	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   3993: ifnull +75 -> 4068
    //   3996: aload_0
    //   3997: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4000: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4003: getfield 723	org/telegram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   4006: astore 5
    //   4008: aload 5
    //   4010: astore 10
    //   4012: aload 5
    //   4014: invokevirtual 331	java/lang/String:length	()I
    //   4017: sipush 150
    //   4020: if_icmple +14 -> 4034
    //   4023: aload 5
    //   4025: iconst_0
    //   4026: sipush 150
    //   4029: invokevirtual 481	java/lang/String:substring	(II)Ljava/lang/String;
    //   4032: astore 10
    //   4034: aload 18
    //   4036: iconst_2
    //   4037: anewarray 306	java/lang/Object
    //   4040: dup
    //   4041: iconst_0
    //   4042: aload 19
    //   4044: aastore
    //   4045: dup
    //   4046: iconst_1
    //   4047: aload 10
    //   4049: bipush 10
    //   4051: bipush 32
    //   4053: invokevirtual 443	java/lang/String:replace	(CC)Ljava/lang/String;
    //   4056: aastore
    //   4057: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   4060: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   4063: astore 10
    //   4065: goto -513 -> 3552
    //   4068: ldc -52
    //   4070: invokestatic 318	android/text/SpannableStringBuilder:valueOf	(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;
    //   4073: astore 10
    //   4075: goto -523 -> 3552
    //   4078: aload_0
    //   4079: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4082: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4085: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4088: instanceof 725
    //   4091: ifeq +68 -> 4159
    //   4094: aload_0
    //   4095: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4098: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4101: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4104: getfield 729	org/telegram/tgnet/TLRPC$MessageMedia:photo	Lorg/telegram/tgnet/TLRPC$Photo;
    //   4107: instanceof 731
    //   4110: ifeq +49 -> 4159
    //   4113: aload_0
    //   4114: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4117: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4120: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4123: getfield 734	org/telegram/tgnet/TLRPC$MessageMedia:ttl_seconds	I
    //   4126: ifeq +33 -> 4159
    //   4129: ldc_w 736
    //   4132: ldc_w 737
    //   4135: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4138: astore 10
    //   4140: iload 13
    //   4142: istore 17
    //   4144: aload 12
    //   4146: astore 19
    //   4148: iload 16
    //   4150: istore 14
    //   4152: iload 15
    //   4154: istore 21
    //   4156: goto -2134 -> 2022
    //   4159: aload_0
    //   4160: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4163: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4166: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4169: instanceof 739
    //   4172: ifeq +68 -> 4240
    //   4175: aload_0
    //   4176: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4179: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4182: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4185: getfield 743	org/telegram/tgnet/TLRPC$MessageMedia:document	Lorg/telegram/tgnet/TLRPC$Document;
    //   4188: instanceof 745
    //   4191: ifeq +49 -> 4240
    //   4194: aload_0
    //   4195: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4198: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4201: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4204: getfield 734	org/telegram/tgnet/TLRPC$MessageMedia:ttl_seconds	I
    //   4207: ifeq +33 -> 4240
    //   4210: ldc_w 747
    //   4213: ldc_w 748
    //   4216: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4219: astore 10
    //   4221: iload 13
    //   4223: istore 17
    //   4225: aload 12
    //   4227: astore 19
    //   4229: iload 16
    //   4231: istore 14
    //   4233: iload 15
    //   4235: istore 21
    //   4237: goto -2215 -> 2022
    //   4240: aload_0
    //   4241: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4244: getfield 678	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   4247: ifnull +31 -> 4278
    //   4250: aload_0
    //   4251: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4254: getfield 678	org/telegram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   4257: astore 10
    //   4259: iload 13
    //   4261: istore 17
    //   4263: aload 12
    //   4265: astore 19
    //   4267: iload 16
    //   4269: istore 14
    //   4271: iload 15
    //   4273: istore 21
    //   4275: goto -2253 -> 2022
    //   4278: aload_0
    //   4279: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4282: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4285: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4288: instanceof 698
    //   4291: ifeq +127 -> 4418
    //   4294: new 750	java/lang/StringBuilder
    //   4297: dup
    //   4298: invokespecial 751	java/lang/StringBuilder:<init>	()V
    //   4301: ldc_w 753
    //   4304: invokevirtual 757	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4307: aload_0
    //   4308: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4311: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4314: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4317: getfield 706	org/telegram/tgnet/TLRPC$MessageMedia:game	Lorg/telegram/tgnet/TLRPC$TL_game;
    //   4320: getfield 709	org/telegram/tgnet/TLRPC$TL_game:title	Ljava/lang/String;
    //   4323: invokevirtual 757	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4326: invokevirtual 758	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4329: astore 18
    //   4331: iload 13
    //   4333: istore 17
    //   4335: aload 12
    //   4337: astore 19
    //   4339: iload 16
    //   4341: istore 14
    //   4343: aload 18
    //   4345: astore 10
    //   4347: iload 15
    //   4349: istore 21
    //   4351: aload_0
    //   4352: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4355: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4358: getfield 693	org/telegram/tgnet/TLRPC$Message:media	Lorg/telegram/tgnet/TLRPC$MessageMedia;
    //   4361: ifnull -2339 -> 2022
    //   4364: iload 13
    //   4366: istore 17
    //   4368: aload 12
    //   4370: astore 19
    //   4372: iload 16
    //   4374: istore 14
    //   4376: aload 18
    //   4378: astore 10
    //   4380: iload 15
    //   4382: istore 21
    //   4384: aload_0
    //   4385: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4388: invokevirtual 696	org/telegram/messenger/MessageObject:isMediaEmpty	()Z
    //   4391: ifne -2369 -> 2022
    //   4394: getstatic 304	org/telegram/ui/ActionBar/Theme:dialogs_messagePrintingPaint	Landroid/text/TextPaint;
    //   4397: astore 19
    //   4399: iload 13
    //   4401: istore 17
    //   4403: iload 16
    //   4405: istore 14
    //   4407: aload 18
    //   4409: astore 10
    //   4411: iload 15
    //   4413: istore 21
    //   4415: goto -2393 -> 2022
    //   4418: aload_0
    //   4419: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4422: getfield 712	org/telegram/messenger/MessageObject:type	I
    //   4425: bipush 14
    //   4427: if_icmpne +38 -> 4465
    //   4430: ldc_w 760
    //   4433: iconst_2
    //   4434: anewarray 306	java/lang/Object
    //   4437: dup
    //   4438: iconst_0
    //   4439: aload_0
    //   4440: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4443: invokevirtual 717	org/telegram/messenger/MessageObject:getMusicAuthor	()Ljava/lang/String;
    //   4446: aastore
    //   4447: dup
    //   4448: iconst_1
    //   4449: aload_0
    //   4450: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4453: invokevirtual 720	org/telegram/messenger/MessageObject:getMusicTitle	()Ljava/lang/String;
    //   4456: aastore
    //   4457: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   4460: astore 18
    //   4462: goto -131 -> 4331
    //   4465: aload_0
    //   4466: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4469: getfield 259	org/telegram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   4472: astore 18
    //   4474: goto -143 -> 4331
    //   4477: aload_0
    //   4478: getfield 511	org/telegram/ui/Cells/DialogCell:lastMessageDate	I
    //   4481: ifeq +16 -> 4497
    //   4484: aload_0
    //   4485: getfield 511	org/telegram/ui/Cells/DialogCell:lastMessageDate	I
    //   4488: i2l
    //   4489: invokestatic 358	org/telegram/messenger/LocaleController:stringForMessageListDate	(J)Ljava/lang/String;
    //   4492: astore 18
    //   4494: goto -2452 -> 2042
    //   4497: aload_2
    //   4498: astore 18
    //   4500: aload_0
    //   4501: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4504: ifnull -2462 -> 2042
    //   4507: aload_0
    //   4508: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4511: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4514: getfield 518	org/telegram/tgnet/TLRPC$Message:date	I
    //   4517: i2l
    //   4518: invokestatic 358	org/telegram/messenger/LocaleController:stringForMessageListDate	(J)Ljava/lang/String;
    //   4521: astore 18
    //   4523: goto -2481 -> 2042
    //   4526: aload_0
    //   4527: getfield 542	org/telegram/ui/Cells/DialogCell:unreadCount	I
    //   4530: ifeq +148 -> 4678
    //   4533: aload_0
    //   4534: getfield 542	org/telegram/ui/Cells/DialogCell:unreadCount	I
    //   4537: iconst_1
    //   4538: if_icmpne +34 -> 4572
    //   4541: aload_0
    //   4542: getfield 542	org/telegram/ui/Cells/DialogCell:unreadCount	I
    //   4545: aload_0
    //   4546: getfield 762	org/telegram/ui/Cells/DialogCell:mentionCount	I
    //   4549: if_icmpne +23 -> 4572
    //   4552: aload_0
    //   4553: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4556: ifnull +16 -> 4572
    //   4559: aload_0
    //   4560: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4563: getfield 515	org/telegram/messenger/MessageObject:messageOwner	Lorg/telegram/tgnet/TLRPC$Message;
    //   4566: getfield 765	org/telegram/tgnet/TLRPC$Message:mentioned	Z
    //   4569: ifne +109 -> 4678
    //   4572: aload_0
    //   4573: iconst_1
    //   4574: putfield 363	org/telegram/ui/Cells/DialogCell:drawCount	Z
    //   4577: ldc_w 365
    //   4580: iconst_1
    //   4581: anewarray 306	java/lang/Object
    //   4584: dup
    //   4585: iconst_0
    //   4586: aload_0
    //   4587: getfield 542	org/telegram/ui/Cells/DialogCell:unreadCount	I
    //   4590: invokestatic 370	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4593: aastore
    //   4594: invokestatic 312	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   4597: astore 12
    //   4599: aload_0
    //   4600: getfield 762	org/telegram/ui/Cells/DialogCell:mentionCount	I
    //   4603: ifeq +87 -> 4690
    //   4606: aload_0
    //   4607: iconst_1
    //   4608: putfield 573	org/telegram/ui/Cells/DialogCell:drawMention	Z
    //   4611: ldc_w 767
    //   4614: astore 7
    //   4616: aload_0
    //   4617: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4620: invokevirtual 770	org/telegram/messenger/MessageObject:isOut	()Z
    //   4623: ifeq +212 -> 4835
    //   4626: aload_0
    //   4627: getfield 529	org/telegram/ui/Cells/DialogCell:draftMessage	Lorg/telegram/tgnet/TLRPC$DraftMessage;
    //   4630: ifnonnull +205 -> 4835
    //   4633: iload 21
    //   4635: ifeq +200 -> 4835
    //   4638: aload_0
    //   4639: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4642: invokevirtual 773	org/telegram/messenger/MessageObject:isSending	()Z
    //   4645: ifeq +53 -> 4698
    //   4648: aload_0
    //   4649: iconst_0
    //   4650: putfield 375	org/telegram/ui/Cells/DialogCell:drawCheck1	Z
    //   4653: aload_0
    //   4654: iconst_0
    //   4655: putfield 377	org/telegram/ui/Cells/DialogCell:drawCheck2	Z
    //   4658: aload_0
    //   4659: iconst_1
    //   4660: putfield 379	org/telegram/ui/Cells/DialogCell:drawClock	Z
    //   4663: aload_0
    //   4664: iconst_0
    //   4665: putfield 381	org/telegram/ui/Cells/DialogCell:drawError	Z
    //   4668: aload 12
    //   4670: astore_3
    //   4671: aload 7
    //   4673: astore 4
    //   4675: goto -2592 -> 2083
    //   4678: aload_0
    //   4679: iconst_0
    //   4680: putfield 363	org/telegram/ui/Cells/DialogCell:drawCount	Z
    //   4683: aload 4
    //   4685: astore 12
    //   4687: goto -88 -> 4599
    //   4690: aload_0
    //   4691: iconst_0
    //   4692: putfield 573	org/telegram/ui/Cells/DialogCell:drawMention	Z
    //   4695: goto -79 -> 4616
    //   4698: aload_0
    //   4699: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4702: invokevirtual 776	org/telegram/messenger/MessageObject:isSendError	()Z
    //   4705: ifeq +43 -> 4748
    //   4708: aload_0
    //   4709: iconst_0
    //   4710: putfield 375	org/telegram/ui/Cells/DialogCell:drawCheck1	Z
    //   4713: aload_0
    //   4714: iconst_0
    //   4715: putfield 377	org/telegram/ui/Cells/DialogCell:drawCheck2	Z
    //   4718: aload_0
    //   4719: iconst_0
    //   4720: putfield 379	org/telegram/ui/Cells/DialogCell:drawClock	Z
    //   4723: aload_0
    //   4724: iconst_1
    //   4725: putfield 381	org/telegram/ui/Cells/DialogCell:drawError	Z
    //   4728: aload_0
    //   4729: iconst_0
    //   4730: putfield 363	org/telegram/ui/Cells/DialogCell:drawCount	Z
    //   4733: aload_0
    //   4734: iconst_0
    //   4735: putfield 573	org/telegram/ui/Cells/DialogCell:drawMention	Z
    //   4738: aload 12
    //   4740: astore_3
    //   4741: aload 7
    //   4743: astore 4
    //   4745: goto -2662 -> 2083
    //   4748: aload 12
    //   4750: astore_3
    //   4751: aload 7
    //   4753: astore 4
    //   4755: aload_0
    //   4756: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4759: invokevirtual 779	org/telegram/messenger/MessageObject:isSent	()Z
    //   4762: ifeq -2679 -> 2083
    //   4765: aload_0
    //   4766: getfield 254	org/telegram/ui/Cells/DialogCell:message	Lorg/telegram/messenger/MessageObject;
    //   4769: invokevirtual 782	org/telegram/messenger/MessageObject:isUnread	()Z
    //   4772: ifeq +23 -> 4795
    //   4775: aload_0
    //   4776: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   4779: invokestatic 548	org/telegram/messenger/ChatObject:isChannel	(Lorg/telegram/tgnet/TLRPC$Chat;)Z
    //   4782: ifeq +47 -> 4829
    //   4785: aload_0
    //   4786: getfield 116	org/telegram/ui/Cells/DialogCell:chat	Lorg/telegram/tgnet/TLRPC$Chat;
    //   4789: getfield 553	org/telegram/tgnet/TLRPC$Chat:megagroup	Z
    //   4792: ifne +37 -> 4829
    //   4795: iconst_1
    //   4796: istore 25
    //   4798: aload_0
    //   4799: iload 25
    //   4801: putfield 375	org/telegram/ui/Cells/DialogCell:drawCheck1	Z
    //   4804: aload_0
    //   4805: iconst_1
    //   4806: putfield 377	org/telegram/ui/Cells/DialogCell:drawCheck2	Z
    //   4809: aload_0
    //   4810: iconst_0
    //   4811: putfield 379	org/telegram/ui/Cells/DialogCell:drawClock	Z
    //   4814: aload_0
    //   4815: iconst_0
    //   4816: putfield 381	org/telegram/ui/Cells/DialogCell:drawError	Z
    //   4819: aload 12
    //   4821: astore_3
    //   4822: aload 7
    //   4824: astore 4
    //   4826: goto -2743 -> 2083
    //   4829: iconst_0
    //   4830: istore 25
    //   4832: goto -34 -> 4798
    //   4835: aload_0
    //   4836: iconst_0
    //   4837: putfield 375	org/telegram/ui/Cells/DialogCell:drawCheck1	Z
    //   4840: aload_0
    //   4841: iconst_0
    //   4842: putfield 377	org/telegram/ui/Cells/DialogCell:drawCheck2	Z
    //   4845: aload_0
    //   4846: iconst_0
    //   4847: putfield 379	org/telegram/ui/Cells/DialogCell:drawClock	Z
    //   4850: aload_0
    //   4851: iconst_0
    //   4852: putfield 381	org/telegram/ui/Cells/DialogCell:drawError	Z
    //   4855: aload 12
    //   4857: astore_3
    //   4858: aload 7
    //   4860: astore 4
    //   4862: goto -2779 -> 2083
    //   4865: aload 11
    //   4867: astore 6
    //   4869: aload_1
    //   4870: astore 7
    //   4872: aload_0
    //   4873: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   4876: ifnull -2773 -> 2103
    //   4879: aload_0
    //   4880: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   4883: invokestatic 245	org/telegram/messenger/UserObject:isUserSelf	(Lorg/telegram/tgnet/TLRPC$User;)Z
    //   4886: ifeq +54 -> 4940
    //   4889: aload_0
    //   4890: getfield 178	org/telegram/ui/Cells/DialogCell:dialogsType	I
    //   4893: iconst_3
    //   4894: if_icmpne +8 -> 4902
    //   4897: aload_0
    //   4898: iconst_1
    //   4899: putfield 239	org/telegram/ui/Cells/DialogCell:drawPinBackground	Z
    //   4902: ldc_w 784
    //   4905: ldc_w 785
    //   4908: invokestatic 298	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4911: astore 12
    //   4913: aload 11
    //   4915: astore 6
    //   4917: aload 12
    //   4919: astore 7
    //   4921: aload_0
    //   4922: getfield 118	org/telegram/ui/Cells/DialogCell:encryptedChat	Lorg/telegram/tgnet/TLRPC$EncryptedChat;
    //   4925: ifnull -2822 -> 2103
    //   4928: getstatic 388	org/telegram/ui/ActionBar/Theme:dialogs_nameEncryptedPaint	Landroid/text/TextPaint;
    //   4931: astore 6
    //   4933: aload 12
    //   4935: astore 7
    //   4937: goto -2834 -> 2103
    //   4940: aload_0
    //   4941: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   4944: getfield 786	org/telegram/tgnet/TLRPC$User:id	I
    //   4947: sipush 1000
    //   4950: idiv
    //   4951: sipush 777
    //   4954: if_icmpeq +172 -> 5126
    //   4957: aload_0
    //   4958: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   4961: getfield 786	org/telegram/tgnet/TLRPC$User:id	I
    //   4964: sipush 1000
    //   4967: idiv
    //   4968: sipush 333
    //   4971: if_icmpeq +155 -> 5126
    //   4974: aload_0
    //   4975: getfield 98	org/telegram/ui/Cells/DialogCell:currentAccount	I
    //   4978: invokestatic 791	org/telegram/messenger/ContactsController:getInstance	(I)Lorg/telegram/messenger/ContactsController;
    //   4981: getfield 795	org/telegram/messenger/ContactsController:contactsDict	Ljava/util/concurrent/ConcurrentHashMap;
    //   4984: aload_0
    //   4985: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   4988: getfield 786	org/telegram/tgnet/TLRPC$User:id	I
    //   4991: invokestatic 370	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   4994: invokevirtual 800	java/util/concurrent/ConcurrentHashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4997: ifnonnull +129 -> 5126
    //   5000: aload_0
    //   5001: getfield 98	org/telegram/ui/Cells/DialogCell:currentAccount	I
    //   5004: invokestatic 791	org/telegram/messenger/ContactsController:getInstance	(I)Lorg/telegram/messenger/ContactsController;
    //   5007: getfield 795	org/telegram/messenger/ContactsController:contactsDict	Ljava/util/concurrent/ConcurrentHashMap;
    //   5010: invokevirtual 803	java/util/concurrent/ConcurrentHashMap:size	()I
    //   5013: ifne +41 -> 5054
    //   5016: aload_0
    //   5017: getfield 98	org/telegram/ui/Cells/DialogCell:currentAccount	I
    //   5020: invokestatic 791	org/telegram/messenger/ContactsController:getInstance	(I)Lorg/telegram/messenger/ContactsController;
    //   5023: getfield 806	org/telegram/messenger/ContactsController:contactsLoaded	Z
    //   5026: ifeq +16 -> 5042
    //   5029: aload_0
    //   5030: getfield 98	org/telegram/ui/Cells/DialogCell:currentAccount	I
    //   5033: invokestatic 791	org/telegram/messenger/ContactsController:getInstance	(I)Lorg/telegram/messenger/ContactsController;
    //   5036: invokevirtual 809	org/telegram/messenger/ContactsController:isLoadingContacts	()Z
    //   5039: ifeq +15 -> 5054
    //   5042: aload_0
    //   5043: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   5046: invokestatic 812	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   5049: astore 12
    //   5051: goto -138 -> 4913
    //   5054: aload_0
    //   5055: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   5058: getfield 815	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   5061: ifnull +53 -> 5114
    //   5064: aload_0
    //   5065: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   5068: getfield 815	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   5071: invokevirtual 331	java/lang/String:length	()I
    //   5074: ifeq +40 -> 5114
    //   5077: invokestatic 820	org/telegram/PhoneFormat/PhoneFormat:getInstance	()Lorg/telegram/PhoneFormat/PhoneFormat;
    //   5080: new 750	java/lang/StringBuilder
    //   5083: dup
    //   5084: invokespecial 751	java/lang/StringBuilder:<init>	()V
    //   5087: ldc_w 822
    //   5090: invokevirtual 757	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5093: aload_0
    //   5094: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   5097: getfield 815	org/telegram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   5100: invokevirtual 757	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5103: invokevirtual 758	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5106: invokevirtual 825	org/telegram/PhoneFormat/PhoneFormat:format	(Ljava/lang/String;)Ljava/lang/String;
    //   5109: astore 12
    //   5111: goto -198 -> 4913
    //   5114: aload_0
    //   5115: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   5118: invokestatic 812	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   5121: astore 12
    //   5123: goto -210 -> 4913
    //   5126: aload_0
    //   5127: getfield 114	org/telegram/ui/Cells/DialogCell:user	Lorg/telegram/tgnet/TLRPC$User;
    //   5130: invokestatic 812	org/telegram/messenger/UserObject:getUserName	(Lorg/telegram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   5133: astore 12
    //   5135: goto -222 -> 4913
    //   5138: aload_0
    //   5139: ldc_w 418
    //   5142: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5145: putfield 420	org/telegram/ui/Cells/DialogCell:timeLeft	I
    //   5148: goto -4527 -> 621
    //   5151: iconst_0
    //   5152: istore 14
    //   5154: aload_0
    //   5155: aconst_null
    //   5156: putfield 414	org/telegram/ui/Cells/DialogCell:timeLayout	Landroid/text/StaticLayout;
    //   5159: aload_0
    //   5160: iconst_0
    //   5161: putfield 420	org/telegram/ui/Cells/DialogCell:timeLeft	I
    //   5164: goto -4543 -> 621
    //   5167: aload_0
    //   5168: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   5171: aload_0
    //   5172: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5175: isub
    //   5176: getstatic 277	org/telegram/messenger/AndroidUtilities:leftBaseline	I
    //   5179: i2f
    //   5180: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5183: isub
    //   5184: iload 14
    //   5186: isub
    //   5187: istore 17
    //   5189: aload_0
    //   5190: aload_0
    //   5191: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5194: iload 14
    //   5196: iadd
    //   5197: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5200: goto -4552 -> 648
    //   5203: aload_0
    //   5204: getfield 229	org/telegram/ui/Cells/DialogCell:drawNameGroup	Z
    //   5207: ifeq +24 -> 5231
    //   5210: iload 17
    //   5212: ldc_w 422
    //   5215: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5218: getstatic 504	org/telegram/ui/ActionBar/Theme:dialogs_groupDrawable	Landroid/graphics/drawable/Drawable;
    //   5221: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   5224: iadd
    //   5225: isub
    //   5226: istore 15
    //   5228: goto -4555 -> 673
    //   5231: aload_0
    //   5232: getfield 231	org/telegram/ui/Cells/DialogCell:drawNameBroadcast	Z
    //   5235: ifeq +24 -> 5259
    //   5238: iload 17
    //   5240: ldc_w 422
    //   5243: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5246: getstatic 507	org/telegram/ui/ActionBar/Theme:dialogs_broadcastDrawable	Landroid/graphics/drawable/Drawable;
    //   5249: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   5252: iadd
    //   5253: isub
    //   5254: istore 15
    //   5256: goto -4583 -> 673
    //   5259: iload 17
    //   5261: istore 15
    //   5263: aload_0
    //   5264: getfield 235	org/telegram/ui/Cells/DialogCell:drawNameBot	Z
    //   5267: ifeq -4594 -> 673
    //   5270: iload 17
    //   5272: ldc_w 422
    //   5275: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5278: getstatic 591	org/telegram/ui/ActionBar/Theme:dialogs_botDrawable	Landroid/graphics/drawable/Drawable;
    //   5281: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   5284: iadd
    //   5285: isub
    //   5286: istore 15
    //   5288: goto -4615 -> 673
    //   5291: aload_0
    //   5292: aload_0
    //   5293: getfield 420	org/telegram/ui/Cells/DialogCell:timeLeft	I
    //   5296: iload 14
    //   5298: iadd
    //   5299: ldc_w 426
    //   5302: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5305: iadd
    //   5306: putfield 428	org/telegram/ui/Cells/DialogCell:checkDrawLeft	I
    //   5309: aload_0
    //   5310: aload_0
    //   5311: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5314: iload 13
    //   5316: iadd
    //   5317: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5320: goto -4601 -> 719
    //   5323: iload 15
    //   5325: istore 17
    //   5327: aload_0
    //   5328: getfield 377	org/telegram/ui/Cells/DialogCell:drawCheck2	Z
    //   5331: ifeq -4612 -> 719
    //   5334: getstatic 828	org/telegram/ui/ActionBar/Theme:dialogs_checkDrawable	Landroid/graphics/drawable/Drawable;
    //   5337: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   5340: ldc_w 426
    //   5343: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5346: iadd
    //   5347: istore 13
    //   5349: iload 15
    //   5351: iload 13
    //   5353: isub
    //   5354: istore 17
    //   5356: aload_0
    //   5357: getfield 375	org/telegram/ui/Cells/DialogCell:drawCheck1	Z
    //   5360: ifeq +117 -> 5477
    //   5363: iload 17
    //   5365: getstatic 831	org/telegram/ui/ActionBar/Theme:dialogs_halfCheckDrawable	Landroid/graphics/drawable/Drawable;
    //   5368: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   5371: ldc_w 832
    //   5374: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5377: isub
    //   5378: isub
    //   5379: istore 17
    //   5381: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   5384: ifne +32 -> 5416
    //   5387: aload_0
    //   5388: aload_0
    //   5389: getfield 420	org/telegram/ui/Cells/DialogCell:timeLeft	I
    //   5392: iload 13
    //   5394: isub
    //   5395: putfield 834	org/telegram/ui/Cells/DialogCell:halfCheckDrawLeft	I
    //   5398: aload_0
    //   5399: aload_0
    //   5400: getfield 834	org/telegram/ui/Cells/DialogCell:halfCheckDrawLeft	I
    //   5403: ldc_w 835
    //   5406: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5409: isub
    //   5410: putfield 428	org/telegram/ui/Cells/DialogCell:checkDrawLeft	I
    //   5413: goto -4694 -> 719
    //   5416: aload_0
    //   5417: aload_0
    //   5418: getfield 420	org/telegram/ui/Cells/DialogCell:timeLeft	I
    //   5421: iload 14
    //   5423: iadd
    //   5424: ldc_w 426
    //   5427: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5430: iadd
    //   5431: putfield 428	org/telegram/ui/Cells/DialogCell:checkDrawLeft	I
    //   5434: aload_0
    //   5435: aload_0
    //   5436: getfield 428	org/telegram/ui/Cells/DialogCell:checkDrawLeft	I
    //   5439: ldc_w 835
    //   5442: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5445: iadd
    //   5446: putfield 834	org/telegram/ui/Cells/DialogCell:halfCheckDrawLeft	I
    //   5449: aload_0
    //   5450: aload_0
    //   5451: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5454: getstatic 831	org/telegram/ui/ActionBar/Theme:dialogs_halfCheckDrawable	Landroid/graphics/drawable/Drawable;
    //   5457: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   5460: iload 13
    //   5462: iadd
    //   5463: ldc_w 832
    //   5466: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5469: isub
    //   5470: iadd
    //   5471: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5474: goto -4755 -> 719
    //   5477: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   5480: ifne +17 -> 5497
    //   5483: aload_0
    //   5484: aload_0
    //   5485: getfield 420	org/telegram/ui/Cells/DialogCell:timeLeft	I
    //   5488: iload 13
    //   5490: isub
    //   5491: putfield 428	org/telegram/ui/Cells/DialogCell:checkDrawLeft	I
    //   5494: goto -4775 -> 719
    //   5497: aload_0
    //   5498: aload_0
    //   5499: getfield 420	org/telegram/ui/Cells/DialogCell:timeLeft	I
    //   5502: iload 14
    //   5504: iadd
    //   5505: ldc_w 426
    //   5508: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5511: iadd
    //   5512: putfield 428	org/telegram/ui/Cells/DialogCell:checkDrawLeft	I
    //   5515: aload_0
    //   5516: aload_0
    //   5517: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5520: iload 13
    //   5522: iadd
    //   5523: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5526: goto -4807 -> 719
    //   5529: iload 17
    //   5531: istore 15
    //   5533: aload_0
    //   5534: getfield 237	org/telegram/ui/Cells/DialogCell:drawVerified	Z
    //   5537: ifeq -4757 -> 780
    //   5540: ldc_w 431
    //   5543: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5546: getstatic 838	org/telegram/ui/ActionBar/Theme:dialogs_verifiedDrawable	Landroid/graphics/drawable/Drawable;
    //   5549: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   5552: iadd
    //   5553: istore 14
    //   5555: iload 17
    //   5557: iload 14
    //   5559: isub
    //   5560: istore 17
    //   5562: iload 17
    //   5564: istore 15
    //   5566: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   5569: ifeq -4789 -> 780
    //   5572: aload_0
    //   5573: aload_0
    //   5574: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5577: iload 14
    //   5579: iadd
    //   5580: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   5583: iload 17
    //   5585: istore 15
    //   5587: goto -4807 -> 780
    //   5590: astore 10
    //   5592: aload 10
    //   5594: invokestatic 844	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   5597: goto -4748 -> 849
    //   5600: ldc_w 845
    //   5603: fstore 22
    //   5605: goto -4711 -> 894
    //   5608: aload_0
    //   5609: ldc_w 846
    //   5612: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5615: putfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   5618: aload_0
    //   5619: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   5622: istore 17
    //   5624: invokestatic 463	org/telegram/messenger/AndroidUtilities:isTablet	()Z
    //   5627: ifeq +21 -> 5648
    //   5630: ldc_w 847
    //   5633: fstore 22
    //   5635: iload 17
    //   5637: fload 22
    //   5639: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5642: isub
    //   5643: istore 17
    //   5645: goto -4744 -> 901
    //   5648: ldc_w 848
    //   5651: fstore 22
    //   5653: goto -18 -> 5635
    //   5656: aload_0
    //   5657: ldc_w 849
    //   5660: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5663: putfield 473	org/telegram/ui/Cells/DialogCell:errorLeft	I
    //   5666: aload_0
    //   5667: aload_0
    //   5668: getfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   5671: iload 17
    //   5673: iadd
    //   5674: putfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   5677: goto -4708 -> 969
    //   5680: aload 9
    //   5682: ifnonnull +8 -> 5690
    //   5685: aload 5
    //   5687: ifnull +318 -> 6005
    //   5690: aload 9
    //   5692: ifnull +239 -> 5931
    //   5695: aload_0
    //   5696: ldc_w 435
    //   5699: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5702: getstatic 852	org/telegram/ui/ActionBar/Theme:dialogs_countTextPaint	Landroid/text/TextPaint;
    //   5705: aload 9
    //   5707: invokevirtual 395	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   5710: f2d
    //   5711: invokestatic 401	java/lang/Math:ceil	(D)D
    //   5714: d2i
    //   5715: invokestatic 439	java/lang/Math:max	(II)I
    //   5718: putfield 854	org/telegram/ui/Cells/DialogCell:countWidth	I
    //   5721: aload_0
    //   5722: new 403	android/text/StaticLayout
    //   5725: dup
    //   5726: aload 9
    //   5728: getstatic 852	org/telegram/ui/ActionBar/Theme:dialogs_countTextPaint	Landroid/text/TextPaint;
    //   5731: aload_0
    //   5732: getfield 854	org/telegram/ui/Cells/DialogCell:countWidth	I
    //   5735: getstatic 857	android/text/Layout$Alignment:ALIGN_CENTER	Landroid/text/Layout$Alignment;
    //   5738: fconst_1
    //   5739: fconst_0
    //   5740: iconst_0
    //   5741: invokespecial 412	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   5744: putfield 859	org/telegram/ui/Cells/DialogCell:countLayout	Landroid/text/StaticLayout;
    //   5747: aload_0
    //   5748: getfield 854	org/telegram/ui/Cells/DialogCell:countWidth	I
    //   5751: ldc -124
    //   5753: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5756: iadd
    //   5757: istore 13
    //   5759: iload 15
    //   5761: iload 13
    //   5763: isub
    //   5764: istore 17
    //   5766: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   5769: ifne +138 -> 5907
    //   5772: aload_0
    //   5773: aload_0
    //   5774: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   5777: aload_0
    //   5778: getfield 854	org/telegram/ui/Cells/DialogCell:countWidth	I
    //   5781: isub
    //   5782: ldc_w 860
    //   5785: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5788: isub
    //   5789: putfield 862	org/telegram/ui/Cells/DialogCell:countLeft	I
    //   5792: aload_0
    //   5793: iconst_1
    //   5794: putfield 363	org/telegram/ui/Cells/DialogCell:drawCount	Z
    //   5797: iload 17
    //   5799: istore 15
    //   5801: aload 5
    //   5803: ifnull -4834 -> 969
    //   5806: aload_0
    //   5807: ldc_w 435
    //   5810: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5813: putfield 864	org/telegram/ui/Cells/DialogCell:mentionWidth	I
    //   5816: aload_0
    //   5817: getfield 864	org/telegram/ui/Cells/DialogCell:mentionWidth	I
    //   5820: ldc -124
    //   5822: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5825: iadd
    //   5826: istore 13
    //   5828: iload 17
    //   5830: iload 13
    //   5832: isub
    //   5833: istore 17
    //   5835: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   5838: ifne +111 -> 5949
    //   5841: aload_0
    //   5842: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   5845: istore 13
    //   5847: aload_0
    //   5848: getfield 864	org/telegram/ui/Cells/DialogCell:mentionWidth	I
    //   5851: istore 26
    //   5853: ldc_w 860
    //   5856: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5859: istore 16
    //   5861: aload_0
    //   5862: getfield 854	org/telegram/ui/Cells/DialogCell:countWidth	I
    //   5865: ifeq +78 -> 5943
    //   5868: aload_0
    //   5869: getfield 854	org/telegram/ui/Cells/DialogCell:countWidth	I
    //   5872: ldc -124
    //   5874: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5877: iadd
    //   5878: istore 15
    //   5880: aload_0
    //   5881: iload 13
    //   5883: iload 26
    //   5885: isub
    //   5886: iload 16
    //   5888: isub
    //   5889: iload 15
    //   5891: isub
    //   5892: putfield 866	org/telegram/ui/Cells/DialogCell:mentionLeft	I
    //   5895: aload_0
    //   5896: iconst_1
    //   5897: putfield 573	org/telegram/ui/Cells/DialogCell:drawMention	Z
    //   5900: iload 17
    //   5902: istore 15
    //   5904: goto -4935 -> 969
    //   5907: aload_0
    //   5908: ldc_w 860
    //   5911: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5914: putfield 862	org/telegram/ui/Cells/DialogCell:countLeft	I
    //   5917: aload_0
    //   5918: aload_0
    //   5919: getfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   5922: iload 13
    //   5924: iadd
    //   5925: putfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   5928: goto -136 -> 5792
    //   5931: aload_0
    //   5932: iconst_0
    //   5933: putfield 854	org/telegram/ui/Cells/DialogCell:countWidth	I
    //   5936: iload 15
    //   5938: istore 17
    //   5940: goto -143 -> 5797
    //   5943: iconst_0
    //   5944: istore 15
    //   5946: goto -66 -> 5880
    //   5949: ldc_w 860
    //   5952: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5955: istore 16
    //   5957: aload_0
    //   5958: getfield 854	org/telegram/ui/Cells/DialogCell:countWidth	I
    //   5961: ifeq +38 -> 5999
    //   5964: aload_0
    //   5965: getfield 854	org/telegram/ui/Cells/DialogCell:countWidth	I
    //   5968: ldc -124
    //   5970: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   5973: iadd
    //   5974: istore 15
    //   5976: aload_0
    //   5977: iload 15
    //   5979: iload 16
    //   5981: iadd
    //   5982: putfield 866	org/telegram/ui/Cells/DialogCell:mentionLeft	I
    //   5985: aload_0
    //   5986: aload_0
    //   5987: getfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   5990: iload 13
    //   5992: iadd
    //   5993: putfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   5996: goto -101 -> 5895
    //   5999: iconst_0
    //   6000: istore 15
    //   6002: goto -26 -> 5976
    //   6005: aload_0
    //   6006: getfield 868	org/telegram/ui/Cells/DialogCell:drawPin	Z
    //   6009: ifeq +329 -> 6338
    //   6012: getstatic 871	org/telegram/ui/ActionBar/Theme:dialogs_pinnedDrawable	Landroid/graphics/drawable/Drawable;
    //   6015: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   6018: ldc_w 832
    //   6021: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6024: iadd
    //   6025: istore 17
    //   6027: iload 15
    //   6029: iload 17
    //   6031: isub
    //   6032: istore 15
    //   6034: getstatic 274	org/telegram/messenger/LocaleController:isRTL	Z
    //   6037: ifne +38 -> 6075
    //   6040: aload_0
    //   6041: aload_0
    //   6042: invokevirtual 417	org/telegram/ui/Cells/DialogCell:getMeasuredWidth	()I
    //   6045: getstatic 871	org/telegram/ui/ActionBar/Theme:dialogs_pinnedDrawable	Landroid/graphics/drawable/Drawable;
    //   6048: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   6051: isub
    //   6052: ldc_w 421
    //   6055: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6058: isub
    //   6059: putfield 873	org/telegram/ui/Cells/DialogCell:pinLeft	I
    //   6062: aload_0
    //   6063: iconst_0
    //   6064: putfield 363	org/telegram/ui/Cells/DialogCell:drawCount	Z
    //   6067: aload_0
    //   6068: iconst_0
    //   6069: putfield 573	org/telegram/ui/Cells/DialogCell:drawMention	Z
    //   6072: goto -5103 -> 969
    //   6075: aload_0
    //   6076: ldc_w 421
    //   6079: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6082: putfield 873	org/telegram/ui/Cells/DialogCell:pinLeft	I
    //   6085: aload_0
    //   6086: aload_0
    //   6087: getfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   6090: iload 17
    //   6092: iadd
    //   6093: putfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   6096: goto -34 -> 6062
    //   6099: astore 10
    //   6101: aload 10
    //   6103: invokestatic 844	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   6106: goto -4993 -> 1113
    //   6109: aload_0
    //   6110: getfield 237	org/telegram/ui/Cells/DialogCell:drawVerified	Z
    //   6113: ifeq -4906 -> 1207
    //   6116: aload_0
    //   6117: aload_0
    //   6118: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   6121: i2d
    //   6122: iload 14
    //   6124: i2d
    //   6125: dload 23
    //   6127: dsub
    //   6128: dadd
    //   6129: ldc_w 431
    //   6132: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6135: i2d
    //   6136: dsub
    //   6137: getstatic 838	org/telegram/ui/ActionBar/Theme:dialogs_verifiedDrawable	Landroid/graphics/drawable/Drawable;
    //   6140: invokevirtual 289	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   6143: i2d
    //   6144: dsub
    //   6145: d2i
    //   6146: putfield 495	org/telegram/ui/Cells/DialogCell:nameMuteLeft	I
    //   6149: goto -4942 -> 1207
    //   6152: aload_0
    //   6153: getfield 457	org/telegram/ui/Cells/DialogCell:nameLayout	Landroid/text/StaticLayout;
    //   6156: ifnull +107 -> 6263
    //   6159: aload_0
    //   6160: getfield 457	org/telegram/ui/Cells/DialogCell:nameLayout	Landroid/text/StaticLayout;
    //   6163: invokevirtual 486	android/text/StaticLayout:getLineCount	()I
    //   6166: ifle +97 -> 6263
    //   6169: aload_0
    //   6170: getfield 457	org/telegram/ui/Cells/DialogCell:nameLayout	Landroid/text/StaticLayout;
    //   6173: iconst_0
    //   6174: invokevirtual 876	android/text/StaticLayout:getLineRight	(I)F
    //   6177: fstore 22
    //   6179: fload 22
    //   6181: iload 14
    //   6183: i2f
    //   6184: fcmpl
    //   6185: ifne +43 -> 6228
    //   6188: aload_0
    //   6189: getfield 457	org/telegram/ui/Cells/DialogCell:nameLayout	Landroid/text/StaticLayout;
    //   6192: iconst_0
    //   6193: invokevirtual 493	android/text/StaticLayout:getLineWidth	(I)F
    //   6196: f2d
    //   6197: invokestatic 401	java/lang/Math:ceil	(D)D
    //   6200: dstore 23
    //   6202: dload 23
    //   6204: iload 14
    //   6206: i2d
    //   6207: dcmpg
    //   6208: ifge +20 -> 6228
    //   6211: aload_0
    //   6212: aload_0
    //   6213: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   6216: i2d
    //   6217: iload 14
    //   6219: i2d
    //   6220: dload 23
    //   6222: dsub
    //   6223: dsub
    //   6224: d2i
    //   6225: putfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   6228: aload_0
    //   6229: getfield 430	org/telegram/ui/Cells/DialogCell:dialogMuted	Z
    //   6232: ifne +10 -> 6242
    //   6235: aload_0
    //   6236: getfield 237	org/telegram/ui/Cells/DialogCell:drawVerified	Z
    //   6239: ifeq +24 -> 6263
    //   6242: aload_0
    //   6243: aload_0
    //   6244: getfield 291	org/telegram/ui/Cells/DialogCell:nameLeft	I
    //   6247: i2f
    //   6248: fload 22
    //   6250: fadd
    //   6251: ldc_w 431
    //   6254: invokestatic 129	org/telegram/messenger/AndroidUtilities:dp	(F)I
    //   6257: i2f
    //   6258: fadd
    //   6259: f2i
    //   6260: putfield 495	org/telegram/ui/Cells/DialogCell:nameMuteLeft	I
    //   6263: aload_0
    //   6264: getfield 483	org/telegram/ui/Cells/DialogCell:messageLayout	Landroid/text/StaticLayout;
    //   6267: ifnull -4957 -> 1310
    //   6270: aload_0
    //   6271: getfield 483	org/telegram/ui/Cells/DialogCell:messageLayout	Landroid/text/StaticLayout;
    //   6274: invokevirtual 486	android/text/StaticLayout:getLineCount	()I
    //   6277: ifle -4967 -> 1310
    //   6280: aload_0
    //   6281: getfield 483	org/telegram/ui/Cells/DialogCell:messageLayout	Landroid/text/StaticLayout;
    //   6284: iconst_0
    //   6285: invokevirtual 876	android/text/StaticLayout:getLineRight	(I)F
    //   6288: iload 15
    //   6290: i2f
    //   6291: fcmpl
    //   6292: ifne -4982 -> 1310
    //   6295: aload_0
    //   6296: getfield 483	org/telegram/ui/Cells/DialogCell:messageLayout	Landroid/text/StaticLayout;
    //   6299: iconst_0
    //   6300: invokevirtual 493	android/text/StaticLayout:getLineWidth	(I)F
    //   6303: f2d
    //   6304: invokestatic 401	java/lang/Math:ceil	(D)D
    //   6307: dstore 23
    //   6309: dload 23
    //   6311: iload 15
    //   6313: i2d
    //   6314: dcmpg
    //   6315: ifge -5005 -> 1310
    //   6318: aload_0
    //   6319: aload_0
    //   6320: getfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   6323: i2d
    //   6324: iload 15
    //   6326: i2d
    //   6327: dload 23
    //   6329: dsub
    //   6330: dsub
    //   6331: d2i
    //   6332: putfield 459	org/telegram/ui/Cells/DialogCell:messageLeft	I
    //   6335: goto -5025 -> 1310
    //   6338: goto -276 -> 6062
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	6341	0	this	DialogCell
    //   2	4868	1	localObject1	Object
    //   5	4493	2	localObject2	Object
    //   7	4851	3	localObject3	Object
    //   9	4852	4	localObject4	Object
    //   12	5790	5	localObject5	Object
    //   15	4917	6	localObject6	Object
    //   18	4918	7	localObject7	Object
    //   21	510	8	localObject8	Object
    //   25	5702	9	localObject9	Object
    //   28	4382	10	localObject10	Object
    //   5590	3	10	localException1	Exception
    //   6099	3	10	localException2	Exception
    //   62	4852	11	localObject11	Object
    //   67	5067	12	localObject12	Object
    //   70	5923	13	i	int
    //   73	6145	14	j	int
    //   116	6209	15	k	int
    //   119	5863	16	m	int
    //   122	5971	17	n	int
    //   134	4388	18	localObject13	Object
    //   150	4248	19	localObject14	Object
    //   479	1678	20	localObject15	Object
    //   483	4151	21	i1	int
    //   892	5357	22	f	float
    //   1158	5170	23	d	double
    //   4796	35	25	bool	boolean
    //   5851	35	26	i2	int
    // Exception table:
    //   from	to	target	type
    //   793	849	5590	java/lang/Exception
    //   1086	1113	6099	java/lang/Exception
  }
  
  public void checkCurrentDialogIndex()
  {
    if (this.index < getDialogsArray().size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)getDialogsArray().get(this.index);
      TLRPC.DraftMessage localDraftMessage = DataQuery.getInstance(this.currentAccount).getDraft(this.currentDialogId);
      MessageObject localMessageObject = (MessageObject)MessagesController.getInstance(this.currentAccount).dialogMessage.get(localTL_dialog.id);
      if ((this.currentDialogId != localTL_dialog.id) || ((this.message != null) && (this.message.getId() != localTL_dialog.top_message)) || ((localMessageObject != null) && (localMessageObject.messageOwner.edit_date != this.currentEditDate)) || (this.unreadCount != localTL_dialog.unread_count) || (this.mentionCount != localTL_dialog.unread_mentions_count) || (this.message != localMessageObject) || ((this.message == null) && (localMessageObject != null)) || (localDraftMessage != this.draftMessage) || (this.drawPin != localTL_dialog.pinned))
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
    if ((this.currentDialogId == 0L) && (this.customDialog == null)) {}
    for (;;)
    {
      return;
      if (this.isSelected) {
        paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight(), Theme.dialogs_tabletSeletedPaint);
      }
      if ((this.drawPin) || (this.drawPinBackground)) {
        paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight(), Theme.dialogs_pinnedPaint);
      }
      if (this.drawNameLock)
      {
        setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
        Theme.dialogs_lockDrawable.draw(paramCanvas);
        label104:
        if (this.nameLayout != null)
        {
          paramCanvas.save();
          paramCanvas.translate(this.nameLeft, AndroidUtilities.dp(13.0F));
          this.nameLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        if (this.timeLayout != null)
        {
          paramCanvas.save();
          paramCanvas.translate(this.timeLeft, this.timeTop);
          this.timeLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        if (this.messageLayout != null)
        {
          paramCanvas.save();
          paramCanvas.translate(this.messageLeft, this.messageTop);
        }
      }
      try
      {
        this.messageLayout.draw(paramCanvas);
        paramCanvas.restore();
        if (this.drawClock)
        {
          setDrawableBounds(Theme.dialogs_clockDrawable, this.checkDrawLeft, this.checkDrawTop);
          Theme.dialogs_clockDrawable.draw(paramCanvas);
          if ((!this.dialogMuted) || (this.drawVerified)) {
            break label637;
          }
          setDrawableBounds(Theme.dialogs_muteDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5F));
          Theme.dialogs_muteDrawable.draw(paramCanvas);
          if (!this.drawError) {
            break label693;
          }
          this.rect.set(this.errorLeft, this.errorTop, this.errorLeft + AndroidUtilities.dp(23.0F), this.errorTop + AndroidUtilities.dp(23.0F));
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5F, AndroidUtilities.density * 11.5F, Theme.dialogs_errorPaint);
          setDrawableBounds(Theme.dialogs_errorDrawable, this.errorLeft + AndroidUtilities.dp(5.5F), this.errorTop + AndroidUtilities.dp(5.0F));
          Theme.dialogs_errorDrawable.draw(paramCanvas);
          if (this.useSeparator)
          {
            if (!LocaleController.isRTL) {
              break label1027;
            }
            paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
          }
          this.avatarImage.draw(paramCanvas);
          continue;
          if (this.drawNameGroup)
          {
            setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_groupDrawable.draw(paramCanvas);
            break label104;
          }
          if (this.drawNameBroadcast)
          {
            setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_broadcastDrawable.draw(paramCanvas);
            break label104;
          }
          if (!this.drawNameBot) {
            break label104;
          }
          setDrawableBounds(Theme.dialogs_botDrawable, this.nameLockLeft, this.nameLockTop);
          Theme.dialogs_botDrawable.draw(paramCanvas);
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
          continue;
          if (this.drawCheck2) {
            if (this.drawCheck1)
            {
              setDrawableBounds(Theme.dialogs_halfCheckDrawable, this.halfCheckDrawLeft, this.checkDrawTop);
              Theme.dialogs_halfCheckDrawable.draw(paramCanvas);
              setDrawableBounds(Theme.dialogs_checkDrawable, this.checkDrawLeft, this.checkDrawTop);
              Theme.dialogs_checkDrawable.draw(paramCanvas);
            }
            else
            {
              setDrawableBounds(Theme.dialogs_checkDrawable, this.checkDrawLeft, this.checkDrawTop);
              Theme.dialogs_checkDrawable.draw(paramCanvas);
              continue;
              label637:
              if (this.drawVerified)
              {
                setDrawableBounds(Theme.dialogs_verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5F));
                setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5F));
                Theme.dialogs_verifiedDrawable.draw(paramCanvas);
                Theme.dialogs_verifiedCheckDrawable.draw(paramCanvas);
                continue;
                label693:
                if ((this.drawCount) || (this.drawMention))
                {
                  int i;
                  RectF localRectF;
                  float f1;
                  float f2;
                  if (this.drawCount)
                  {
                    i = this.countLeft - AndroidUtilities.dp(5.5F);
                    this.rect.set(i, this.countTop, this.countWidth + i + AndroidUtilities.dp(11.0F), this.countTop + AndroidUtilities.dp(23.0F));
                    localRectF = this.rect;
                    f1 = AndroidUtilities.density;
                    f2 = AndroidUtilities.density;
                    if (!this.dialogMuted) {
                      break label989;
                    }
                  }
                  label989:
                  for (Paint localPaint = Theme.dialogs_countGrayPaint;; localPaint = Theme.dialogs_countPaint)
                  {
                    paramCanvas.drawRoundRect(localRectF, 11.5F * f1, 11.5F * f2, localPaint);
                    paramCanvas.save();
                    paramCanvas.translate(this.countLeft, this.countTop + AndroidUtilities.dp(4.0F));
                    if (this.countLayout != null) {
                      this.countLayout.draw(paramCanvas);
                    }
                    paramCanvas.restore();
                    if (!this.drawMention) {
                      break;
                    }
                    i = this.mentionLeft - AndroidUtilities.dp(5.5F);
                    this.rect.set(i, this.countTop, this.mentionWidth + i + AndroidUtilities.dp(11.0F), this.countTop + AndroidUtilities.dp(23.0F));
                    paramCanvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5F, AndroidUtilities.density * 11.5F, Theme.dialogs_countPaint);
                    setDrawableBounds(Theme.dialogs_mentionDrawable, this.mentionLeft - AndroidUtilities.dp(2.0F), this.countTop + AndroidUtilities.dp(3.2F), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F));
                    Theme.dialogs_mentionDrawable.draw(paramCanvas);
                    break;
                  }
                }
                if (this.drawPin)
                {
                  setDrawableBounds(Theme.dialogs_pinnedDrawable, this.pinLeft, this.pinTop);
                  Theme.dialogs_pinnedDrawable.draw(paramCanvas);
                  continue;
                  label1027:
                  paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
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
    if ((this.currentDialogId == 0L) && (this.customDialog == null)) {
      return;
    }
    if (this.checkBox != null) {
      if (!LocaleController.isRTL) {
        break label97;
      }
    }
    label97:
    for (paramInt1 = paramInt3 - paramInt1 - AndroidUtilities.dp(42.0F);; paramInt1 = AndroidUtilities.dp(42.0F))
    {
      paramInt2 = AndroidUtilities.dp(43.0F);
      this.checkBox.layout(paramInt1, paramInt2, this.checkBox.getMeasuredWidth() + paramInt1, this.checkBox.getMeasuredHeight() + paramInt2);
      if (!paramBoolean) {
        break;
      }
      try
      {
        buildLayout();
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
      break;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.checkBox != null) {
      this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), NUM));
    }
    int i = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = AndroidUtilities.dp(72.0F);
    if (this.useSeparator) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      setMeasuredDimension(i, paramInt1 + paramInt2);
      return;
    }
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.checkBox == null) {}
    for (;;)
    {
      return;
      this.checkBox.setChecked(paramBoolean1, paramBoolean2);
    }
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
      this.mentionCount = 0;
      if ((paramMessageObject == null) || (!paramMessageObject.isUnread())) {
        break label103;
      }
    }
    label103:
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
  
  public void setDialog(CustomDialog paramCustomDialog)
  {
    this.customDialog = paramCustomDialog;
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
    boolean bool;
    if (this.customDialog != null)
    {
      this.lastMessageDate = this.customDialog.date;
      if (this.customDialog.unread_count != 0)
      {
        bool = true;
        this.lastUnreadState = bool;
        this.unreadCount = this.customDialog.unread_count;
        this.drawPin = this.customDialog.pinned;
        this.dialogMuted = this.customDialog.muted;
        this.avatarDrawable.setInfo(this.customDialog.id, this.customDialog.name, null, false);
        this.avatarImage.setImage((TLObject)null, "50_50", this.avatarDrawable, null, 0);
        if ((getMeasuredWidth() == 0) && (getMeasuredHeight() == 0)) {
          break label1217;
        }
        buildLayout();
      }
    }
    for (;;)
    {
      invalidate();
      Object localObject1;
      label218:
      int i;
      label258:
      label301:
      int j;
      label653:
      do
      {
        return;
        bool = false;
        break;
        if (!this.isDialogCell) {
          break label890;
        }
        localObject1 = (TLRPC.TL_dialog)MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
        if ((localObject1 != null) && (paramInt == 0))
        {
          this.message = ((MessageObject)MessagesController.getInstance(this.currentAccount).dialogMessage.get(((TLRPC.TL_dialog)localObject1).id));
          if ((this.message == null) || (!this.message.isUnread())) {
            break label879;
          }
          bool = true;
          this.lastUnreadState = bool;
          this.unreadCount = ((TLRPC.TL_dialog)localObject1).unread_count;
          this.mentionCount = ((TLRPC.TL_dialog)localObject1).unread_mentions_count;
          if (this.message == null) {
            break label884;
          }
          i = this.message.messageOwner.edit_date;
          this.currentEditDate = i;
          this.lastMessageDate = ((TLRPC.TL_dialog)localObject1).last_message_date;
          this.drawPin = ((TLRPC.TL_dialog)localObject1).pinned;
          if (this.message != null) {
            this.lastSendState = this.message.messageOwner.send_state;
          }
        }
        if (paramInt == 0) {
          break label728;
        }
        j = 0;
        i = j;
        if (this.isDialogCell)
        {
          i = j;
          if ((paramInt & 0x40) != 0)
          {
            localObject1 = (CharSequence)MessagesController.getInstance(this.currentAccount).printingStrings.get(this.currentDialogId);
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
          if ((0x8000 & paramInt) != 0)
          {
            j = i;
            if (this.message != null)
            {
              j = i;
              if (this.message.messageText != this.lastMessageString) {
                j = 1;
              }
            }
          }
        }
        i = j;
        if (j == 0)
        {
          i = j;
          if ((paramInt & 0x2) != 0)
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
          if ((paramInt & 0x1) != 0)
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
          if ((paramInt & 0x8) != 0)
          {
            i = j;
            if (this.user == null) {
              i = 1;
            }
          }
        }
        j = i;
        if (i == 0)
        {
          j = i;
          if ((paramInt & 0x10) != 0)
          {
            j = i;
            if (this.user == null) {
              j = 1;
            }
          }
        }
        i = j;
        if (j == 0)
        {
          i = j;
          if ((paramInt & 0x100) != 0)
          {
            if ((this.message == null) || (this.lastUnreadState == this.message.isUnread())) {
              break label898;
            }
            this.lastUnreadState = this.message.isUnread();
            i = 1;
          }
        }
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
      } while (j == 0);
      label728:
      label754:
      label818:
      Object localObject2;
      if ((this.isDialogCell) && (MessagesController.getInstance(this.currentAccount).isDialogMuted(this.currentDialogId)))
      {
        bool = true;
        this.dialogMuted = bool;
        this.user = null;
        this.chat = null;
        this.encryptedChat = null;
        paramInt = (int)this.currentDialogId;
        i = (int)(this.currentDialogId >> 32);
        if (paramInt == 0) {
          break label1095;
        }
        if (i != 1) {
          break label991;
        }
        this.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(paramInt));
        localObject2 = null;
        localObject1 = null;
        if (this.user == null) {
          break label1172;
        }
        this.avatarDrawable.setInfo(this.user);
        if (!UserObject.isUserSelf(this.user)) {
          break label1148;
        }
        this.avatarDrawable.setSavedMessages(1);
      }
      for (;;)
      {
        this.avatarImage.setImage((TLObject)localObject1, "50_50", this.avatarDrawable, null, 0);
        break;
        label879:
        bool = false;
        break label218;
        label884:
        i = 0;
        break label258;
        label890:
        this.drawPin = false;
        break label301;
        label898:
        i = j;
        if (!this.isDialogCell) {
          break label653;
        }
        localObject1 = (TLRPC.TL_dialog)MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
        i = j;
        if (localObject1 == null) {
          break label653;
        }
        if (this.unreadCount == ((TLRPC.TL_dialog)localObject1).unread_count)
        {
          i = j;
          if (this.mentionCount == ((TLRPC.TL_dialog)localObject1).unread_mentions_count) {
            break label653;
          }
        }
        this.unreadCount = ((TLRPC.TL_dialog)localObject1).unread_count;
        this.mentionCount = ((TLRPC.TL_dialog)localObject1).unread_mentions_count;
        i = 1;
        break label653;
        bool = false;
        break label754;
        label991:
        if (paramInt < 0)
        {
          this.chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-paramInt));
          if ((this.isDialogCell) || (this.chat == null) || (this.chat.migrated_to == null)) {
            break label818;
          }
          localObject1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat.migrated_to.channel_id));
          if (localObject1 == null) {
            break label818;
          }
          this.chat = ((TLRPC.Chat)localObject1);
          break label818;
        }
        this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramInt));
        break label818;
        label1095:
        this.encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i));
        if (this.encryptedChat == null) {
          break label818;
        }
        this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.encryptedChat.user_id));
        break label818;
        label1148:
        if (this.user.photo != null)
        {
          localObject1 = this.user.photo.photo_small;
          continue;
          label1172:
          if (this.chat != null)
          {
            localObject1 = localObject2;
            if (this.chat.photo != null) {
              localObject1 = this.chat.photo.photo_small;
            }
            this.avatarDrawable.setInfo(this.chat);
          }
        }
      }
      label1217:
      requestLayout();
    }
  }
  
  public static class CustomDialog
  {
    public int date;
    public int id;
    public boolean isMedia;
    public String message;
    public boolean muted;
    public String name;
    public boolean pinned;
    public boolean sent;
    public int type;
    public int unread_count;
    public boolean verified;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/DialogCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */