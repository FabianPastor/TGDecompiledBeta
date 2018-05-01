package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class AdminedChannelCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private int currentAccount = UserConfig.selectedAccount;
  private TLRPC.Chat currentChannel;
  private ImageView deleteButton;
  private boolean isLast;
  private SimpleTextView nameTextView;
  private SimpleTextView statusTextView;
  
  public AdminedChannelCell(Context paramContext, View.OnClickListener paramOnClickListener)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label467;
      }
      f1 = 0.0F;
      label70:
      if (!LocaleController.isRTL) {
        break label474;
      }
      f2 = 12.0F;
      label80:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 12.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label480;
      }
      i = 5;
      label151:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label486;
      }
      i = 5;
      label174:
      if (!LocaleController.isRTL) {
        break label492;
      }
      f1 = 62.0F;
      label184:
      if (!LocaleController.isRTL) {
        break label499;
      }
      f2 = 73.0F;
      label194:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 15.5F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(14);
      this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
      this.statusTextView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label506;
      }
      i = 5;
      label276:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label512;
      }
      i = 5;
      label299:
      if (!LocaleController.isRTL) {
        break label518;
      }
      f1 = 62.0F;
      label309:
      if (!LocaleController.isRTL) {
        break label525;
      }
      f2 = 73.0F;
      label319:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 38.5F, f2, 0.0F));
      this.deleteButton = new ImageView(paramContext);
      this.deleteButton.setScaleType(ImageView.ScaleType.CENTER);
      this.deleteButton.setImageResource(NUM);
      this.deleteButton.setOnClickListener(paramOnClickListener);
      this.deleteButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText"), PorterDuff.Mode.MULTIPLY));
      paramContext = this.deleteButton;
      if (!LocaleController.isRTL) {
        break label532;
      }
      i = 3;
      label417:
      if (!LocaleController.isRTL) {
        break label538;
      }
      f1 = 7.0F;
      label427:
      if (!LocaleController.isRTL) {
        break label544;
      }
    }
    label467:
    label474:
    label480:
    label486:
    label492:
    label499:
    label506:
    label512:
    label518:
    label525:
    label532:
    label538:
    label544:
    for (float f2 = 0.0F;; f2 = 7.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 12.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = 12.0F;
      break label70;
      f2 = 0.0F;
      break label80;
      i = 3;
      break label151;
      i = 3;
      break label174;
      f1 = 73.0F;
      break label184;
      f2 = 62.0F;
      break label194;
      i = 3;
      break label276;
      i = 3;
      break label299;
      f1 = 73.0F;
      break label309;
      f2 = 62.0F;
      break label319;
      i = 5;
      break label417;
      f1 = 0.0F;
      break label427;
    }
  }
  
  public TLRPC.Chat getCurrentChannel()
  {
    return this.currentChannel;
  }
  
  public ImageView getDeleteButton()
  {
    return this.deleteButton;
  }
  
  public SimpleTextView getNameTextView()
  {
    return this.nameTextView;
  }
  
  public SimpleTextView getStatusTextView()
  {
    return this.statusTextView;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    if (this.isLast) {}
    for (paramInt1 = 12;; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(paramInt1 + 60), NUM));
      return;
    }
  }
  
  public void setChannel(TLRPC.Chat paramChat, boolean paramBoolean)
  {
    TLRPC.FileLocation localFileLocation = null;
    if (paramChat.photo != null) {
      localFileLocation = paramChat.photo.photo_small;
    }
    String str = MessagesController.getInstance(this.currentAccount).linkPrefix + "/";
    this.currentChannel = paramChat;
    this.avatarDrawable.setInfo(paramChat);
    this.nameTextView.setText(paramChat.title);
    paramChat = new SpannableStringBuilder(str + paramChat.username);
    paramChat.setSpan(new URLSpanNoUnderline(""), str.length(), paramChat.length(), 33);
    this.statusTextView.setText(paramChat);
    this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
    this.isLast = paramBoolean;
  }
  
  public void update()
  {
    this.avatarDrawable.setInfo(this.currentChannel);
    this.avatarImageView.invalidate();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/AdminedChannelCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */