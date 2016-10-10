package org.telegram.ui.Cells;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class AdminedChannelCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImageView;
  private TLRPC.Chat currentChannel;
  private boolean isLast;
  private SimpleTextView nameTextView;
  private SimpleTextView statusTextView;
  
  public AdminedChannelCell(Context paramContext, View.OnClickListener paramOnClickListener)
  {
    super(paramContext);
    setBackgroundResource(2130837799);
    this.avatarDrawable = new AvatarDrawable();
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label411;
      }
      f1 = 0.0F;
      label69:
      if (!LocaleController.isRTL) {
        break label417;
      }
      f2 = 12.0F;
      label79:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 12.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label423;
      }
      i = 5;
      label148:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label429;
      }
      i = 5;
      label173:
      if (!LocaleController.isRTL) {
        break label435;
      }
      f1 = 62.0F;
      label182:
      if (!LocaleController.isRTL) {
        break label441;
      }
      f2 = 73.0F;
      label192:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 15.5F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(14);
      this.statusTextView.setTextColor(-5723992);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label448;
      }
      i = 5;
      label260:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label454;
      }
      i = 5;
      label285:
      if (!LocaleController.isRTL) {
        break label460;
      }
      f1 = 62.0F;
      label294:
      if (!LocaleController.isRTL) {
        break label466;
      }
      f2 = 73.0F;
      label304:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 38.5F, f2, 0.0F));
      paramContext = new ImageView(paramContext);
      paramContext.setScaleType(ImageView.ScaleType.CENTER);
      paramContext.setImageResource(2130837637);
      paramContext.setOnClickListener(paramOnClickListener);
      if (!LocaleController.isRTL) {
        break label473;
      }
      i = 3;
      label363:
      if (!LocaleController.isRTL) {
        break label479;
      }
      f1 = 7.0F;
      label372:
      if (!LocaleController.isRTL) {
        break label484;
      }
    }
    label411:
    label417:
    label423:
    label429:
    label435:
    label441:
    label448:
    label454:
    label460:
    label466:
    label473:
    label479:
    label484:
    for (float f2 = 0.0F;; f2 = 7.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 12.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = 12.0F;
      break label69;
      f2 = 0.0F;
      break label79;
      i = 3;
      break label148;
      i = 3;
      break label173;
      f1 = 73.0F;
      break label182;
      f2 = 62.0F;
      break label192;
      i = 3;
      break label260;
      i = 3;
      break label285;
      f1 = 73.0F;
      break label294;
      f2 = 62.0F;
      break label304;
      i = 5;
      break label363;
      f1 = 0.0F;
      break label372;
    }
  }
  
  public TLRPC.Chat getCurrentChannel()
  {
    return this.currentChannel;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
    if (this.isLast) {}
    for (paramInt1 = 12;; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(paramInt1 + 60), 1073741824));
      return;
    }
  }
  
  public void setChannel(TLRPC.Chat paramChat, boolean paramBoolean)
  {
    TLRPC.FileLocation localFileLocation = null;
    if (paramChat.photo != null) {
      localFileLocation = paramChat.photo.photo_small;
    }
    this.currentChannel = paramChat;
    this.avatarDrawable.setInfo(paramChat);
    this.nameTextView.setText(paramChat.title);
    paramChat = new SpannableStringBuilder("telegram.me/" + paramChat.username);
    paramChat.setSpan(new ForegroundColorSpan(-12876608), "telegram.me/".length(), paramChat.length(), 33);
    this.statusTextView.setText(paramChat);
    this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
    this.isLast = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/AdminedChannelCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */