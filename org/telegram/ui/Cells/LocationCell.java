package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class LocationCell
  extends FrameLayout
{
  private static Paint paint;
  private TextView addressTextView;
  private BackupImageView imageView;
  private TextView nameTextView;
  private boolean needDivider;
  
  public LocationCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setBackgroundResource(2130837960);
    this.imageView.setSize(AndroidUtilities.dp(30.0F), AndroidUtilities.dp(30.0F));
    this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(-6710887, PorterDuff.Mode.MULTIPLY));
    Object localObject = this.imageView;
    int i;
    float f1;
    label126:
    float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label464;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label470;
      }
      f2 = 17.0F;
      label135:
      addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label475;
      }
      i = 5;
      label242:
      ((TextView)localObject).setGravity(i);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label481;
      }
      i = 5;
      label264:
      if (!LocaleController.isRTL) {
        break label487;
      }
      j = 16;
      label274:
      f1 = j;
      if (!LocaleController.isRTL) {
        break label494;
      }
      j = 72;
      label288:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 5.0F, j, 0.0F));
      this.addressTextView = new TextView(paramContext);
      this.addressTextView.setTextSize(1, 14.0F);
      this.addressTextView.setMaxLines(1);
      this.addressTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.addressTextView.setSingleLine(true);
      this.addressTextView.setTextColor(-6710887);
      paramContext = this.addressTextView;
      if (!LocaleController.isRTL) {
        break label501;
      }
      i = 5;
      label384:
      paramContext.setGravity(i);
      paramContext = this.addressTextView;
      if (!LocaleController.isRTL) {
        break label507;
      }
      i = m;
      label405:
      if (!LocaleController.isRTL) {
        break label513;
      }
    }
    label464:
    label470:
    label475:
    label481:
    label487:
    label494:
    label501:
    label507:
    label513:
    for (int j = 16;; j = 72)
    {
      f1 = j;
      j = k;
      if (LocaleController.isRTL) {
        j = 72;
      }
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 30.0F, j, 0.0F));
      return;
      i = 3;
      break;
      f1 = 17.0F;
      break label126;
      f2 = 0.0F;
      break label135;
      i = 3;
      break label242;
      i = 3;
      break label264;
      j = 72;
      break label274;
      j = 16;
      break label288;
      i = 3;
      break label384;
      i = 3;
      break label405;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(AndroidUtilities.dp(72.0F), getHeight() - 1, getWidth(), getHeight() - 1, paint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = AndroidUtilities.dp(56.0F);
    if (this.needDivider) {}
    for (paramInt2 = 1;; paramInt2 = 0)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2 + i, 1073741824));
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