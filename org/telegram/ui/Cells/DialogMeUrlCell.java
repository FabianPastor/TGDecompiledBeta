package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.RecentMeUrl;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlChat;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlUnknown;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlUser;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;

public class DialogMeUrlCell
  extends BaseCell
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private ImageReceiver avatarImage = new ImageReceiver(this);
  private int avatarTop = AndroidUtilities.dp(10.0F);
  private int currentAccount = UserConfig.selectedAccount;
  private boolean drawNameBot;
  private boolean drawNameBroadcast;
  private boolean drawNameGroup;
  private boolean drawNameLock;
  private boolean drawVerified;
  private boolean isSelected;
  private StaticLayout messageLayout;
  private int messageLeft;
  private int messageTop = AndroidUtilities.dp(40.0F);
  private StaticLayout nameLayout;
  private int nameLeft;
  private int nameLockLeft;
  private int nameLockTop;
  private int nameMuteLeft;
  private TLRPC.RecentMeUrl recentMeUrl;
  public boolean useSeparator;
  
  public DialogMeUrlCell(Context paramContext)
  {
    super(paramContext);
    Theme.createDialogsResources(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0F));
  }
  
  public void buildLayout()
  {
    Object localObject1 = "";
    TextPaint localTextPaint1 = Theme.dialogs_namePaint;
    TextPaint localTextPaint2 = Theme.dialogs_messagePaint;
    this.drawNameGroup = false;
    this.drawNameBroadcast = false;
    this.drawNameLock = false;
    this.drawNameBot = false;
    this.drawVerified = false;
    Object localObject2;
    if ((this.recentMeUrl instanceof TLRPC.TL_recentMeUrlChat))
    {
      localObject2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.recentMeUrl.chat_id));
      if ((((TLRPC.Chat)localObject2).id < 0) || ((ChatObject.isChannel((TLRPC.Chat)localObject2)) && (!((TLRPC.Chat)localObject2).megagroup)))
      {
        this.drawNameBroadcast = true;
        this.nameLockTop = AndroidUtilities.dp(16.5F);
      }
    }
    for (;;)
    {
      this.drawVerified = ((TLRPC.Chat)localObject2).verified;
      int i;
      int j;
      label158:
      Object localObject3;
      label191:
      label200:
      label299:
      label323:
      int k;
      if (!LocaleController.isRTL)
      {
        this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
        i = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4);
        if (this.drawNameGroup)
        {
          j = Theme.dialogs_groupDrawable.getIntrinsicWidth();
          this.nameLeft = (j + i);
          localObject1 = ((TLRPC.Chat)localObject2).title;
          if (((TLRPC.Chat)localObject2).photo == null) {
            break label865;
          }
          localObject3 = ((TLRPC.Chat)localObject2).photo.photo_small;
          this.avatarDrawable.setInfo((TLRPC.Chat)localObject2);
          localObject2 = MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + this.recentMeUrl.url;
          this.avatarImage.setImage((TLObject)localObject3, "50_50", this.avatarDrawable, null, 0);
          localObject3 = localObject1;
          if (TextUtils.isEmpty((CharSequence)localObject1)) {
            localObject3 = LocaleController.getString("HiddenName", NUM);
          }
          if (LocaleController.isRTL) {
            break label1686;
          }
          i = getMeasuredWidth() - this.nameLeft - AndroidUtilities.dp(14.0F);
          if (!this.drawNameLock) {
            break label1708;
          }
          j = i - (AndroidUtilities.dp(4.0F) + Theme.dialogs_lockDrawable.getIntrinsicWidth());
          i = j;
          if (this.drawVerified)
          {
            k = AndroidUtilities.dp(6.0F) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
            j -= k;
            i = j;
            if (LocaleController.isRTL)
            {
              this.nameLeft += k;
              i = j;
            }
          }
          i = Math.max(AndroidUtilities.dp(12.0F), i);
        }
      }
      try
      {
        localObject1 = TextUtils.ellipsize(((String)localObject3).replace('\n', ' '), localTextPaint1, i - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END);
        localObject3 = new android/text/StaticLayout;
        ((StaticLayout)localObject3).<init>((CharSequence)localObject1, localTextPaint1, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.nameLayout = ((StaticLayout)localObject3);
        int m = getMeasuredWidth();
        k = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 16);
        if (!LocaleController.isRTL)
        {
          this.messageLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
          if (AndroidUtilities.isTablet())
          {
            f = 13.0F;
            j = AndroidUtilities.dp(f);
            this.avatarImage.setImageCoords(j, this.avatarTop, AndroidUtilities.dp(52.0F), AndroidUtilities.dp(52.0F));
            j = Math.max(AndroidUtilities.dp(12.0F), m - k);
            localObject3 = TextUtils.ellipsize((CharSequence)localObject2, localTextPaint2, j - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END);
          }
        }
      }
      catch (Exception localException1)
      {
        try
        {
          for (;;)
          {
            localObject1 = new android/text/StaticLayout;
            ((StaticLayout)localObject1).<init>((CharSequence)localObject3, localTextPaint2, j, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            this.messageLayout = ((StaticLayout)localObject1);
            if (!LocaleController.isRTL) {
              break label1865;
            }
            if ((this.nameLayout != null) && (this.nameLayout.getLineCount() > 0))
            {
              f = this.nameLayout.getLineLeft(0);
              d = Math.ceil(this.nameLayout.getLineWidth(0));
              if (this.drawVerified) {
                this.nameMuteLeft = ((int)(this.nameLeft + (i - d) - AndroidUtilities.dp(6.0F) - Theme.dialogs_verifiedDrawable.getIntrinsicWidth()));
              }
              if ((f == 0.0F) && (d < i)) {
                this.nameLeft = ((int)(this.nameLeft + (i - d)));
              }
            }
            if ((this.messageLayout != null) && (this.messageLayout.getLineCount() > 0) && (this.messageLayout.getLineLeft(0) == 0.0F))
            {
              d = Math.ceil(this.messageLayout.getLineWidth(0));
              if (d < j) {
                this.messageLeft = ((int)(this.messageLeft + (j - d)));
              }
            }
            return;
            this.drawNameGroup = true;
            this.nameLockTop = AndroidUtilities.dp(17.5F);
            break;
            j = Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
            break label158;
            k = getMeasuredWidth();
            i = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
            if (this.drawNameGroup) {}
            for (j = Theme.dialogs_groupDrawable.getIntrinsicWidth();; j = Theme.dialogs_broadcastDrawable.getIntrinsicWidth())
            {
              this.nameLockLeft = (k - i - j);
              this.nameLeft = AndroidUtilities.dp(14.0F);
              break;
            }
            label865:
            localObject3 = null;
            break label191;
            if ((this.recentMeUrl instanceof TLRPC.TL_recentMeUrlUser))
            {
              localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.recentMeUrl.user_id));
              if (!LocaleController.isRTL)
              {
                this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
                label920:
                if (localObject2 != null)
                {
                  if (((TLRPC.User)localObject2).bot)
                  {
                    this.drawNameBot = true;
                    this.nameLockTop = AndroidUtilities.dp(16.5F);
                    if (LocaleController.isRTL) {
                      break label1041;
                    }
                    this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
                    this.nameLeft = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + Theme.dialogs_botDrawable.getIntrinsicWidth());
                  }
                  label984:
                  this.drawVerified = ((TLRPC.User)localObject2).verified;
                }
                localObject1 = UserObject.getUserName((TLRPC.User)localObject2);
                if (((TLRPC.User)localObject2).photo == null) {
                  break label1076;
                }
              }
              label1041:
              label1076:
              for (localObject3 = ((TLRPC.User)localObject2).photo.photo_small;; localObject3 = null)
              {
                this.avatarDrawable.setInfo((TLRPC.User)localObject2);
                break;
                this.nameLeft = AndroidUtilities.dp(14.0F);
                break label920;
                this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - Theme.dialogs_botDrawable.getIntrinsicWidth());
                this.nameLeft = AndroidUtilities.dp(14.0F);
                break label984;
              }
            }
            if ((this.recentMeUrl instanceof TLRPC.TL_recentMeUrlStickerSet))
            {
              if (!LocaleController.isRTL) {}
              for (this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);; this.nameLeft = AndroidUtilities.dp(14.0F))
              {
                localObject1 = this.recentMeUrl.set.set.title;
                localObject3 = this.recentMeUrl.set.cover;
                this.avatarDrawable.setInfo(5, this.recentMeUrl.set.set.title, null, false);
                break;
              }
            }
            if ((this.recentMeUrl instanceof TLRPC.TL_recentMeUrlChatInvite))
            {
              if (!LocaleController.isRTL)
              {
                this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
                label1200:
                if (this.recentMeUrl.chat_invite.chat == null) {
                  break label1448;
                }
                this.avatarDrawable.setInfo(this.recentMeUrl.chat_invite.chat);
                localObject1 = this.recentMeUrl.chat_invite.chat.title;
                if (this.recentMeUrl.chat_invite.chat.photo == null) {
                  break label1424;
                }
                localObject3 = this.recentMeUrl.chat_invite.chat.photo.photo_small;
                label1278:
                if ((this.recentMeUrl.chat_invite.chat.id >= 0) && ((!ChatObject.isChannel(this.recentMeUrl.chat_invite.chat)) || (this.recentMeUrl.chat_invite.chat.megagroup))) {
                  break label1430;
                }
                this.drawNameBroadcast = true;
                this.nameLockTop = AndroidUtilities.dp(16.5F);
                label1340:
                this.drawVerified = this.recentMeUrl.chat_invite.chat.verified;
                label1357:
                if (LocaleController.isRTL) {
                  break label1566;
                }
                this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
                i = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4);
                if (!this.drawNameGroup) {
                  break label1555;
                }
              }
              label1424:
              label1430:
              label1448:
              label1555:
              for (j = Theme.dialogs_groupDrawable.getIntrinsicWidth();; j = Theme.dialogs_broadcastDrawable.getIntrinsicWidth())
              {
                this.nameLeft = (j + i);
                break;
                this.nameLeft = AndroidUtilities.dp(14.0F);
                break label1200;
                localObject3 = null;
                break label1278;
                this.drawNameGroup = true;
                this.nameLockTop = AndroidUtilities.dp(17.5F);
                break label1340;
                localObject1 = this.recentMeUrl.chat_invite.title;
                localObject3 = this.recentMeUrl.chat_invite.photo.photo_small;
                this.avatarDrawable.setInfo(5, this.recentMeUrl.chat_invite.title, null, false);
                if ((this.recentMeUrl.chat_invite.broadcast) || (this.recentMeUrl.chat_invite.channel))
                {
                  this.drawNameBroadcast = true;
                  this.nameLockTop = AndroidUtilities.dp(16.5F);
                  break label1357;
                }
                this.drawNameGroup = true;
                this.nameLockTop = AndroidUtilities.dp(17.5F);
                break label1357;
              }
              label1566:
              i = getMeasuredWidth();
              k = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              if (this.drawNameGroup) {}
              for (j = Theme.dialogs_groupDrawable.getIntrinsicWidth();; j = Theme.dialogs_broadcastDrawable.getIntrinsicWidth())
              {
                this.nameLockLeft = (i - k - j);
                this.nameLeft = AndroidUtilities.dp(14.0F);
                break;
              }
            }
            if ((this.recentMeUrl instanceof TLRPC.TL_recentMeUrlUnknown))
            {
              if (!LocaleController.isRTL) {}
              for (this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);; this.nameLeft = AndroidUtilities.dp(14.0F))
              {
                localObject1 = "Url";
                localObject3 = null;
                break;
              }
            }
            localObject3 = null;
            break label200;
            label1686:
            i = getMeasuredWidth() - this.nameLeft - AndroidUtilities.dp(AndroidUtilities.leftBaseline);
            break label299;
            label1708:
            if (this.drawNameGroup)
            {
              j = i - (AndroidUtilities.dp(4.0F) + Theme.dialogs_groupDrawable.getIntrinsicWidth());
              break label323;
            }
            if (this.drawNameBroadcast)
            {
              j = i - (AndroidUtilities.dp(4.0F) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth());
              break label323;
            }
            j = i;
            if (!this.drawNameBot) {
              break label323;
            }
            j = i - (AndroidUtilities.dp(4.0F) + Theme.dialogs_botDrawable.getIntrinsicWidth());
            break label323;
            localException1 = localException1;
            FileLog.e(localException1);
            continue;
            f = 9.0F;
          }
          this.messageLeft = AndroidUtilities.dp(16.0F);
          j = getMeasuredWidth();
          if (AndroidUtilities.isTablet()) {}
          for (f = 65.0F;; f = 61.0F)
          {
            j -= AndroidUtilities.dp(f);
            break;
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            float f;
            double d;
            FileLog.e(localException2);
            continue;
            label1865:
            if ((this.nameLayout != null) && (this.nameLayout.getLineCount() > 0))
            {
              f = this.nameLayout.getLineRight(0);
              if (f == i)
              {
                d = Math.ceil(this.nameLayout.getLineWidth(0));
                if (d < i) {
                  this.nameLeft = ((int)(this.nameLeft - (i - d)));
                }
              }
              if (this.drawVerified) {
                this.nameMuteLeft = ((int)(this.nameLeft + f + AndroidUtilities.dp(6.0F)));
              }
            }
            if ((this.messageLayout != null) && (this.messageLayout.getLineCount() > 0) && (this.messageLayout.getLineRight(0) == j))
            {
              d = Math.ceil(this.messageLayout.getLineWidth(0));
              if (d < j) {
                this.messageLeft = ((int)(this.messageLeft - (j - d)));
              }
            }
          }
        }
      }
    }
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
    if (this.isSelected) {
      paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight(), Theme.dialogs_tabletSeletedPaint);
    }
    if (this.drawNameLock)
    {
      setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
      Theme.dialogs_lockDrawable.draw(paramCanvas);
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
      if (this.messageLayout != null)
      {
        paramCanvas.save();
        paramCanvas.translate(this.messageLeft, this.messageTop);
      }
      try
      {
        this.messageLayout.draw(paramCanvas);
        paramCanvas.restore();
        if (this.drawVerified)
        {
          setDrawableBounds(Theme.dialogs_verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5F));
          setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5F));
          Theme.dialogs_verifiedDrawable.draw(paramCanvas);
          Theme.dialogs_verifiedCheckDrawable.draw(paramCanvas);
        }
        if (this.useSeparator)
        {
          if (LocaleController.isRTL) {
            paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
          }
        }
        else
        {
          this.avatarImage.draw(paramCanvas);
          return;
          if (this.drawNameGroup)
          {
            setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_groupDrawable.draw(paramCanvas);
            continue;
          }
          if (this.drawNameBroadcast)
          {
            setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_broadcastDrawable.draw(paramCanvas);
            continue;
          }
          if (!this.drawNameBot) {
            continue;
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
          paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramBoolean) {
      buildLayout();
    }
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
  
  public void setDialogSelected(boolean paramBoolean)
  {
    if (this.isSelected != paramBoolean) {
      invalidate();
    }
    this.isSelected = paramBoolean;
  }
  
  public void setRecentMeUrl(TLRPC.RecentMeUrl paramRecentMeUrl)
  {
    this.recentMeUrl = paramRecentMeUrl;
    requestLayout();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/DialogMeUrlCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */