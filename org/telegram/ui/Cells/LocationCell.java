package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class LocationCell
  extends FrameLayout
{
  private TextView addressTextView;
  private BackupImageView imageView;
  private TextView nameTextView;
  private boolean needDivider;
  
  public LocationCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setBackgroundResource(NUM);
    this.imageView.setSize(AndroidUtilities.dp(30.0F), AndroidUtilities.dp(30.0F));
    this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText3"), PorterDuff.Mode.MULTIPLY));
    Object localObject = this.imageView;
    int k;
    float f1;
    label97:
    float f2;
    if (LocaleController.isRTL)
    {
      k = 5;
      if (!LocaleController.isRTL) {
        break label446;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label453;
      }
      f2 = 17.0F;
      label107:
      addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, k | 0x30, f1, 8.0F, f2, 0.0F));
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label459;
      }
      k = 5;
      label219:
      ((TextView)localObject).setGravity(k);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label465;
      }
      k = 5;
      label241:
      if (!LocaleController.isRTL) {
        break label471;
      }
      m = 16;
      label251:
      f1 = m;
      if (!LocaleController.isRTL) {
        break label478;
      }
      m = 72;
      label266:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, k | 0x30, f1, 5.0F, m, 0.0F));
      this.addressTextView = new TextView(paramContext);
      this.addressTextView.setTextSize(1, 14.0F);
      this.addressTextView.setMaxLines(1);
      this.addressTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.addressTextView.setSingleLine(true);
      this.addressTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
      paramContext = this.addressTextView;
      if (!LocaleController.isRTL) {
        break label485;
      }
      k = 5;
      label366:
      paramContext.setGravity(k);
      paramContext = this.addressTextView;
      if (!LocaleController.isRTL) {
        break label491;
      }
      k = j;
      label386:
      if (!LocaleController.isRTL) {
        break label497;
      }
    }
    label446:
    label453:
    label459:
    label465:
    label471:
    label478:
    label485:
    label491:
    label497:
    for (int m = 16;; m = 72)
    {
      f1 = m;
      m = i;
      if (LocaleController.isRTL) {
        m = 72;
      }
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, k | 0x30, f1, 30.0F, m, 0.0F));
      return;
      k = 3;
      break;
      f1 = 17.0F;
      break label97;
      f2 = 0.0F;
      break label107;
      k = 3;
      break label219;
      k = 3;
      break label241;
      m = 72;
      break label251;
      m = 16;
      break label266;
      k = 3;
      break label366;
      k = 3;
      break label386;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(AndroidUtilities.dp(72.0F), getHeight() - 1, getWidth(), getHeight() - 1, Theme.dividerPaint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    paramInt2 = AndroidUtilities.dp(56.0F);
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(paramInt1 + paramInt2, NUM));
      return;
    }
  }
  
  public void setLocation(TLRPC.TL_messageMediaVenue paramTL_messageMediaVenue, String paramString, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.nameTextView.setText(paramTL_messageMediaVenue.title);
    this.addressTextView.setText(paramTL_messageMediaVenue.address);
    this.imageView.setImage(paramString, null, null);
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/LocationCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */