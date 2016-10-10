package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.Components.AvatarDrawable;

public class ProfileSearchCell
  extends BaseCell
{
  private static Drawable botDrawable;
  private static Drawable broadcastDrawable;
  private static Drawable checkDrawable;
  private static Drawable countDrawable;
  private static Drawable countDrawableGrey;
  private static TextPaint countPaint;
  private static Drawable groupDrawable;
  private static Paint linePaint;
  private static Drawable lockDrawable;
  private static TextPaint nameEncryptedPaint;
  private static TextPaint namePaint;
  private static TextPaint offlinePaint;
  private static TextPaint onlinePaint;
  private AvatarDrawable avatarDrawable;
  private ImageReceiver avatarImage;
  private TLRPC.Chat chat = null;
  private StaticLayout countLayout;
  private int countLeft;
  private int countTop = AndroidUtilities.dp(25.0F);
  private int countWidth;
  private CharSequence currentName;
  private long dialog_id;
  public float drawAlpha = 1.0F;
  private boolean drawCheck;
  private boolean drawCount;
  private boolean drawNameBot;
  private boolean drawNameBroadcast;
  private boolean drawNameGroup;
  private boolean drawNameLock;
  private TLRPC.EncryptedChat encryptedChat = null;
  private TLRPC.FileLocation lastAvatar = null;
  private String lastName = null;
  private int lastStatus = 0;
  private int lastUnreadCount;
  private StaticLayout nameLayout;
  private int nameLeft;
  private int nameLockLeft;
  private int nameLockTop;
  private int nameTop;
  private StaticLayout onlineLayout;
  private int onlineLeft;
  private CharSequence subLabel;
  public boolean useSeparator = false;
  private TLRPC.User user = null;
  
  public ProfileSearchCell(Context paramContext)
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
      onlinePaint = new TextPaint(1);
      onlinePaint.setColor(-14255946);
      offlinePaint = new TextPaint(1);
      offlinePaint.setColor(-6710887);
      linePaint = new Paint();
      linePaint.setColor(-2302756);
      countPaint = new TextPaint(1);
      countPaint.setColor(-1);
      countPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      broadcastDrawable = getResources().getDrawable(2130837789);
      lockDrawable = getResources().getDrawable(2130837795);
      groupDrawable = getResources().getDrawable(2130837792);
      countDrawable = getResources().getDrawable(2130837639);
      countDrawableGrey = getResources().getDrawable(2130837640);
      checkDrawable = getResources().getDrawable(2130837592);
      botDrawable = getResources().getDrawable(2130837573);
    }
    namePaint.setTextSize(AndroidUtilities.dp(17.0F));
    nameEncryptedPaint.setTextSize(AndroidUtilities.dp(17.0F));
    onlinePaint.setTextSize(AndroidUtilities.dp(16.0F));
    offlinePaint.setTextSize(AndroidUtilities.dp(16.0F));
    countPaint.setTextSize(AndroidUtilities.dp(13.0F));
    this.avatarImage = new ImageReceiver(this);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0F));
    this.avatarDrawable = new AvatarDrawable();
  }
  
  public void buildLayout()
  {
    this.drawNameBroadcast = false;
    this.drawNameLock = false;
    this.drawNameGroup = false;
    this.drawCheck = false;
    this.drawNameBot = false;
    Object localObject2;
    Object localObject1;
    label188:
    label200:
    int i;
    int j;
    if (this.encryptedChat != null)
    {
      this.drawNameLock = true;
      this.dialog_id = (this.encryptedChat.id << 32);
      if (!LocaleController.isRTL)
      {
        this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
        this.nameLeft = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + lockDrawable.getIntrinsicWidth());
        this.nameLockTop = AndroidUtilities.dp(16.5F);
        if (this.currentName == null) {
          break label1249;
        }
        localObject2 = this.currentName;
        localObject1 = localObject2;
        if (((CharSequence)localObject2).length() == 0)
        {
          if ((this.user == null) || (this.user.phone == null) || (this.user.phone.length() == 0)) {
            break label1303;
          }
          localObject1 = PhoneFormat.getInstance().format("+" + this.user.phone);
        }
        if (this.encryptedChat == null) {
          break label1317;
        }
        localObject2 = nameEncryptedPaint;
        if (LocaleController.isRTL) {
          break label1325;
        }
        i = getMeasuredWidth() - this.nameLeft - AndroidUtilities.dp(14.0F);
        j = i;
        label228:
        if (!this.drawNameLock) {
          break label1351;
        }
        i -= AndroidUtilities.dp(6.0F) + lockDrawable.getIntrinsicWidth();
      }
    }
    label253:
    label424:
    label496:
    label523:
    label602:
    label617:
    label868:
    label1003:
    label1014:
    label1111:
    label1163:
    label1199:
    label1237:
    label1249:
    label1303:
    label1317:
    label1325:
    label1351:
    label1485:
    label1498:
    label1638:
    label1655:
    label1662:
    label1697:
    label1829:
    for (;;)
    {
      Object localObject3;
      int k;
      float f;
      double d;
      if (this.drawCount)
      {
        localObject3 = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
        if ((localObject3 != null) && (((TLRPC.TL_dialog)localObject3).unread_count != 0))
        {
          this.lastUnreadCount = ((TLRPC.TL_dialog)localObject3).unread_count;
          localObject3 = String.format("%d", new Object[] { Integer.valueOf(((TLRPC.TL_dialog)localObject3).unread_count) });
          this.countWidth = Math.max(AndroidUtilities.dp(12.0F), (int)Math.ceil(countPaint.measureText((String)localObject3)));
          this.countLayout = new StaticLayout((CharSequence)localObject3, countPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
          k = this.countWidth + AndroidUtilities.dp(18.0F);
          i -= k;
          if (!LocaleController.isRTL)
          {
            this.countLeft = (getMeasuredWidth() - this.countWidth - AndroidUtilities.dp(19.0F));
            this.nameLayout = new StaticLayout(TextUtils.ellipsize((CharSequence)localObject1, (TextPaint)localObject2, i - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), (TextPaint)localObject2, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            if ((this.chat != null) && (this.subLabel == null)) {
              break label1638;
            }
            if (LocaleController.isRTL) {
              break label1485;
            }
            this.onlineLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
            localObject1 = "";
            localObject3 = offlinePaint;
            if (this.subLabel == null) {
              break label1498;
            }
            localObject1 = this.subLabel;
            localObject2 = localObject3;
            this.onlineLayout = new StaticLayout(TextUtils.ellipsize((CharSequence)localObject1, (TextPaint)localObject2, j - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), (TextPaint)localObject2, j, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            this.nameTop = AndroidUtilities.dp(13.0F);
            if ((this.subLabel != null) && (this.chat != null)) {
              this.nameLockTop -= AndroidUtilities.dp(12.0F);
            }
            if (LocaleController.isRTL) {
              break label1662;
            }
            if (!AndroidUtilities.isTablet()) {
              break label1655;
            }
            f = 13.0F;
            k = AndroidUtilities.dp(f);
            this.avatarImage.setImageCoords(k, AndroidUtilities.dp(10.0F), AndroidUtilities.dp(52.0F), AndroidUtilities.dp(52.0F));
            if (!LocaleController.isRTL) {
              break label1697;
            }
            if ((this.nameLayout.getLineCount() > 0) && (this.nameLayout.getLineLeft(0) == 0.0F))
            {
              d = Math.ceil(this.nameLayout.getLineWidth(0));
              if (d < i) {
                this.nameLeft = ((int)(this.nameLeft + (i - d)));
              }
            }
            if ((this.onlineLayout != null) && (this.onlineLayout.getLineCount() > 0) && (this.onlineLayout.getLineLeft(0) == 0.0F))
            {
              d = Math.ceil(this.onlineLayout.getLineWidth(0));
              if (d < j) {
                this.onlineLeft = ((int)(this.onlineLeft + (j - d)));
              }
            }
          }
        }
      }
      do
      {
        do
        {
          return;
          this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline + 2) - lockDrawable.getIntrinsicWidth());
          this.nameLeft = AndroidUtilities.dp(11.0F);
          break;
          if (this.chat != null)
          {
            if (this.chat.id < 0)
            {
              this.dialog_id = AndroidUtilities.makeBroadcastId(this.chat.id);
              this.drawNameBroadcast = true;
              this.nameLockTop = AndroidUtilities.dp(28.5F);
              this.drawCheck = this.chat.verified;
              if (LocaleController.isRTL) {
                break label1014;
              }
              this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              j = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4);
              if (!this.drawNameGroup) {
                break label1003;
              }
            }
            for (i = groupDrawable.getIntrinsicWidth();; i = broadcastDrawable.getIntrinsicWidth())
            {
              this.nameLeft = (i + j);
              break;
              this.dialog_id = (-this.chat.id);
              if ((ChatObject.isChannel(this.chat)) && (!this.chat.megagroup))
              {
                this.drawNameBroadcast = true;
                this.nameLockTop = AndroidUtilities.dp(28.5F);
                break label868;
              }
              this.drawNameGroup = true;
              this.nameLockTop = AndroidUtilities.dp(30.0F);
              break label868;
            }
            j = getMeasuredWidth();
            k = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 2);
            if (this.drawNameGroup) {}
            for (i = groupDrawable.getIntrinsicWidth();; i = broadcastDrawable.getIntrinsicWidth())
            {
              this.nameLockLeft = (j - k - i);
              this.nameLeft = AndroidUtilities.dp(11.0F);
              break;
            }
          }
          this.dialog_id = this.user.id;
          if (!LocaleController.isRTL)
          {
            this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
            if (!this.user.bot) {
              break label1237;
            }
            this.drawNameBot = true;
            if (LocaleController.isRTL) {
              break label1199;
            }
            this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
            this.nameLeft = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + botDrawable.getIntrinsicWidth());
          }
          for (this.nameLockTop = AndroidUtilities.dp(16.5F);; this.nameLockTop = AndroidUtilities.dp(17.0F))
          {
            this.drawCheck = this.user.verified;
            break;
            this.nameLeft = AndroidUtilities.dp(11.0F);
            break label1111;
            this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline + 2) - botDrawable.getIntrinsicWidth());
            this.nameLeft = AndroidUtilities.dp(11.0F);
            break label1163;
          }
          localObject1 = "";
          if (this.chat != null) {
            localObject1 = this.chat.title;
          }
          for (;;)
          {
            localObject2 = ((String)localObject1).replace('\n', ' ');
            break;
            if (this.user != null) {
              localObject1 = UserObject.getUserName(this.user);
            }
          }
          localObject1 = LocaleController.getString("HiddenName", 2131165729);
          break label188;
          localObject2 = namePaint;
          break label200;
          i = getMeasuredWidth() - this.nameLeft - AndroidUtilities.dp(AndroidUtilities.leftBaseline);
          j = i;
          break label228;
          if (this.drawNameBroadcast)
          {
            i -= AndroidUtilities.dp(6.0F) + broadcastDrawable.getIntrinsicWidth();
            break label253;
          }
          if (this.drawNameGroup)
          {
            i -= AndroidUtilities.dp(6.0F) + groupDrawable.getIntrinsicWidth();
            break label253;
          }
          if (!this.drawNameBot) {
            break label1829;
          }
          i -= AndroidUtilities.dp(6.0F) + botDrawable.getIntrinsicWidth();
          break label253;
          this.countLeft = AndroidUtilities.dp(19.0F);
          this.nameLeft += k;
          break label424;
          this.lastUnreadCount = 0;
          for (this.countLayout = null;; this.countLayout = null)
          {
            break;
            this.lastUnreadCount = 0;
          }
          this.onlineLeft = AndroidUtilities.dp(11.0F);
          break label496;
          localObject2 = localObject3;
          if (this.user == null) {
            break label523;
          }
          if (this.user.bot)
          {
            localObject1 = LocaleController.getString("Bot", 2131165365);
            localObject2 = localObject3;
            break label523;
          }
          String str = LocaleController.formatUserStatus(this.user);
          localObject2 = localObject3;
          localObject1 = str;
          if (this.user == null) {
            break label523;
          }
          if (this.user.id != UserConfig.getClientUserId())
          {
            localObject2 = localObject3;
            localObject1 = str;
            if (this.user.status == null) {
              break label523;
            }
            localObject2 = localObject3;
            localObject1 = str;
            if (this.user.status.expires <= ConnectionsManager.getInstance().getCurrentTime()) {
              break label523;
            }
          }
          localObject2 = onlinePaint;
          localObject1 = LocaleController.getString("Online", 2131166046);
          break label523;
          this.onlineLayout = null;
          this.nameTop = AndroidUtilities.dp(25.0F);
          break label602;
          f = 9.0F;
          break label617;
          k = getMeasuredWidth();
          if (AndroidUtilities.isTablet()) {}
          for (f = 65.0F;; f = 61.0F)
          {
            k -= AndroidUtilities.dp(f);
            break;
          }
          if ((this.nameLayout.getLineCount() > 0) && (this.nameLayout.getLineRight(0) == i))
          {
            d = Math.ceil(this.nameLayout.getLineWidth(0));
            if (d < i) {
              this.nameLeft = ((int)(this.nameLeft - (i - d)));
            }
          }
        } while ((this.onlineLayout == null) || (this.onlineLayout.getLineCount() <= 0) || (this.onlineLayout.getLineRight(0) != j));
        d = Math.ceil(this.onlineLayout.getLineWidth(0));
      } while (d >= j);
      this.onlineLeft = ((int)(this.onlineLeft - (j - d)));
      return;
    }
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
    if ((this.user == null) && (this.chat == null) && (this.encryptedChat == null)) {
      return;
    }
    label135:
    label215:
    Drawable localDrawable;
    int i;
    int j;
    int k;
    int m;
    if (this.useSeparator)
    {
      if (LocaleController.isRTL) {
        paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, linePaint);
      }
    }
    else
    {
      if (this.drawAlpha != 1.0F) {
        paramCanvas.saveLayerAlpha(0.0F, 0.0F, paramCanvas.getWidth(), paramCanvas.getHeight(), (int)(255.0F * this.drawAlpha), 4);
      }
      if (!this.drawNameLock) {
        break label429;
      }
      setDrawableBounds(lockDrawable, this.nameLockLeft, this.nameLockTop);
      lockDrawable.draw(paramCanvas);
      if (this.nameLayout != null)
      {
        paramCanvas.save();
        paramCanvas.translate(this.nameLeft, this.nameTop);
        this.nameLayout.draw(paramCanvas);
        paramCanvas.restore();
        if (this.drawCheck)
        {
          if (!LocaleController.isRTL) {
            break label525;
          }
          setDrawableBounds(checkDrawable, this.nameLeft - AndroidUtilities.dp(4.0F) - checkDrawable.getIntrinsicWidth(), this.nameLockTop);
          checkDrawable.draw(paramCanvas);
        }
      }
      if (this.onlineLayout != null)
      {
        paramCanvas.save();
        paramCanvas.translate(this.onlineLeft, AndroidUtilities.dp(40.0F));
        this.onlineLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.countLayout != null)
      {
        if (!MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
          break label560;
        }
        localDrawable = countDrawableGrey;
        i = this.countLeft;
        j = AndroidUtilities.dp(5.5F);
        k = this.countTop;
        m = this.countWidth;
        setDrawableBounds(localDrawable, i - j, k, AndroidUtilities.dp(11.0F) + m, countDrawableGrey.getIntrinsicHeight());
        countDrawableGrey.draw(paramCanvas);
      }
    }
    for (;;)
    {
      paramCanvas.save();
      paramCanvas.translate(this.countLeft, this.countTop + AndroidUtilities.dp(4.0F));
      this.countLayout.draw(paramCanvas);
      paramCanvas.restore();
      this.avatarImage.draw(paramCanvas);
      return;
      paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, linePaint);
      break;
      label429:
      if (this.drawNameGroup)
      {
        setDrawableBounds(groupDrawable, this.nameLockLeft, this.nameLockTop);
        groupDrawable.draw(paramCanvas);
        break label135;
      }
      if (this.drawNameBroadcast)
      {
        setDrawableBounds(broadcastDrawable, this.nameLockLeft, this.nameLockTop);
        broadcastDrawable.draw(paramCanvas);
        break label135;
      }
      if (!this.drawNameBot) {
        break label135;
      }
      setDrawableBounds(botDrawable, this.nameLockLeft, this.nameLockTop);
      botDrawable.draw(paramCanvas);
      break label135;
      label525:
      setDrawableBounds(checkDrawable, this.nameLeft + (int)this.nameLayout.getLineWidth(0) + AndroidUtilities.dp(4.0F), this.nameLockTop);
      break label215;
      label560:
      localDrawable = countDrawable;
      i = this.countLeft;
      j = AndroidUtilities.dp(5.5F);
      k = this.countTop;
      m = this.countWidth;
      setDrawableBounds(localDrawable, i - j, k, AndroidUtilities.dp(11.0F) + m, countDrawable.getIntrinsicHeight());
      countDrawable.draw(paramCanvas);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.user == null) && (this.chat == null) && (this.encryptedChat == null)) {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    while (!paramBoolean) {
      return;
    }
    buildLayout();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(72.0F));
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2))) {
      getBackground().setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setData(TLObject paramTLObject, TLRPC.EncryptedChat paramEncryptedChat, CharSequence paramCharSequence1, CharSequence paramCharSequence2, boolean paramBoolean)
  {
    this.currentName = paramCharSequence1;
    if ((paramTLObject instanceof TLRPC.User))
    {
      this.user = ((TLRPC.User)paramTLObject);
      this.chat = null;
    }
    for (;;)
    {
      this.encryptedChat = paramEncryptedChat;
      this.subLabel = paramCharSequence2;
      this.drawCount = paramBoolean;
      update(0);
      return;
      if ((paramTLObject instanceof TLRPC.Chat))
      {
        this.chat = ((TLRPC.Chat)paramTLObject);
        this.user = null;
      }
    }
  }
  
  public void update(int paramInt)
  {
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject1 = null;
    int j;
    int i;
    if (this.user != null)
    {
      if (this.user.photo != null) {
        localObject1 = this.user.photo.photo_small;
      }
      this.avatarDrawable.setInfo(this.user);
      if (paramInt == 0) {
        break label467;
      }
      j = 0;
      if (((paramInt & 0x2) == 0) || (this.user == null))
      {
        i = j;
        if ((paramInt & 0x8) != 0)
        {
          i = j;
          if (this.chat == null) {}
        }
      }
      else
      {
        if ((this.lastAvatar == null) || (localObject1 != null))
        {
          i = j;
          if (this.lastAvatar != null) {
            break label165;
          }
          i = j;
          if (localObject1 == null) {
            break label165;
          }
          i = j;
          if (this.lastAvatar == null) {
            break label165;
          }
          i = j;
          if (localObject1 == null) {
            break label165;
          }
          if (this.lastAvatar.volume_id == ((TLRPC.FileLocation)localObject1).volume_id)
          {
            i = j;
            if (this.lastAvatar.local_id == ((TLRPC.FileLocation)localObject1).local_id) {
              break label165;
            }
          }
        }
        i = 1;
      }
      label165:
      j = i;
      if (i == 0)
      {
        j = i;
        if ((paramInt & 0x4) != 0)
        {
          j = i;
          if (this.user != null)
          {
            int k = 0;
            if (this.user.status != null) {
              k = this.user.status.expires;
            }
            j = i;
            if (k != this.lastStatus) {
              j = 1;
            }
          }
        }
      }
      if ((j != 0) || ((paramInt & 0x1) == 0) || (this.user == null))
      {
        i = j;
        if ((paramInt & 0x10) != 0)
        {
          i = j;
          if (this.chat == null) {}
        }
      }
      else
      {
        if (this.user == null) {
          break label455;
        }
      }
    }
    label455:
    for (localObject2 = this.user.first_name + this.user.last_name;; localObject2 = this.chat.title)
    {
      i = j;
      if (!((String)localObject2).equals(this.lastName)) {
        i = 1;
      }
      j = i;
      if (i == 0)
      {
        j = i;
        if (this.drawCount)
        {
          j = i;
          if ((paramInt & 0x100) != 0)
          {
            localObject2 = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
            j = i;
            if (localObject2 != null)
            {
              j = i;
              if (((TLRPC.TL_dialog)localObject2).unread_count != this.lastUnreadCount) {
                j = 1;
              }
            }
          }
        }
      }
      if (j != 0) {
        break label467;
      }
      return;
      if (this.chat != null)
      {
        localObject1 = localObject3;
        if (this.chat.photo != null) {
          localObject1 = this.chat.photo.photo_small;
        }
        this.avatarDrawable.setInfo(this.chat);
        break;
      }
      this.avatarDrawable.setInfo(0, null, null, false);
      localObject1 = localObject2;
      break;
    }
    label467:
    if (this.user != null) {
      if (this.user.status != null)
      {
        this.lastStatus = this.user.status.expires;
        this.lastName = (this.user.first_name + this.user.last_name);
        label532:
        this.lastAvatar = ((TLRPC.FileLocation)localObject1);
        this.avatarImage.setImage((TLObject)localObject1, "50_50", this.avatarDrawable, null, false);
        if ((getMeasuredWidth() == 0) && (getMeasuredHeight() == 0)) {
          break label608;
        }
        buildLayout();
      }
    }
    for (;;)
    {
      postInvalidate();
      return;
      this.lastStatus = 0;
      break;
      if (this.chat == null) {
        break label532;
      }
      this.lastName = this.chat.title;
      break label532;
      label608:
      requestLayout();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ProfileSearchCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */