package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class PaymentInfoCell
  extends FrameLayout
{
  private TextView detailExTextView;
  private TextView detailTextView;
  private BackupImageView imageView;
  private TextView nameTextView;
  
  public PaymentInfoCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    Object localObject = this.imageView;
    int i;
    label143:
    label164:
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      addView((View)localObject, LayoutHelper.createFrame(100, 100.0F, i, 10.0F, 10.0F, 10.0F, 0.0F));
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setLines(1);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label484;
      }
      i = 5;
      ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label489;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label494;
      }
      f1 = 10.0F;
      label174:
      if (!LocaleController.isRTL) {
        break label501;
      }
      f2 = 123.0F;
      label184:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 9.0F, f2, 0.0F));
      this.detailTextView = new TextView(paramContext);
      this.detailTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.detailTextView.setTextSize(1, 14.0F);
      this.detailTextView.setMaxLines(3);
      this.detailTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.detailTextView;
      if (!LocaleController.isRTL) {
        break label508;
      }
      i = 5;
      label271:
      ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.detailTextView;
      if (!LocaleController.isRTL) {
        break label513;
      }
      i = 5;
      label292:
      if (!LocaleController.isRTL) {
        break label518;
      }
      f1 = 10.0F;
      label302:
      if (!LocaleController.isRTL) {
        break label525;
      }
      f2 = 123.0F;
      label312:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 33.0F, f2, 0.0F));
      this.detailExTextView = new TextView(paramContext);
      this.detailExTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.detailExTextView.setTextSize(1, 13.0F);
      this.detailExTextView.setLines(1);
      this.detailExTextView.setMaxLines(1);
      this.detailExTextView.setSingleLine(true);
      this.detailExTextView.setEllipsize(TextUtils.TruncateAt.END);
      paramContext = this.detailExTextView;
      if (!LocaleController.isRTL) {
        break label532;
      }
      i = 5;
      label415:
      paramContext.setGravity(i | 0x30);
      paramContext = this.detailExTextView;
      if (!LocaleController.isRTL) {
        break label537;
      }
      i = 5;
      label436:
      if (!LocaleController.isRTL) {
        break label542;
      }
      f1 = 10.0F;
      label446:
      if (!LocaleController.isRTL) {
        break label549;
      }
    }
    label484:
    label489:
    label494:
    label501:
    label508:
    label513:
    label518:
    label525:
    label532:
    label537:
    label542:
    label549:
    for (float f2 = 123.0F;; f2 = 10.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 90.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label143;
      i = 3;
      break label164;
      f1 = 123.0F;
      break label174;
      f2 = 10.0F;
      break label184;
      i = 3;
      break label271;
      i = 3;
      break label292;
      f1 = 123.0F;
      break label302;
      f2 = 10.0F;
      break label312;
      i = 3;
      break label415;
      i = 3;
      break label436;
      f1 = 123.0F;
      break label446;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    paramInt1 = this.detailTextView.getBottom() + AndroidUtilities.dp(3.0F);
    this.detailExTextView.layout(this.detailExTextView.getLeft(), paramInt1, this.detailExTextView.getRight(), this.detailExTextView.getMeasuredHeight() + paramInt1);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(120.0F), NUM));
  }
  
  public void setInvoice(TLRPC.TL_messageMediaInvoice paramTL_messageMediaInvoice, String paramString)
  {
    this.nameTextView.setText(paramTL_messageMediaInvoice.title);
    this.detailTextView.setText(paramTL_messageMediaInvoice.description);
    this.detailExTextView.setText(paramString);
    float f1;
    int j;
    int k;
    if (AndroidUtilities.isTablet())
    {
      i = (int)(AndroidUtilities.getMinTabletSide() * 0.7F);
      f1 = 'ʀ' / (i - AndroidUtilities.dp(2.0F));
      j = (int)('ʀ' / f1);
      k = (int)('Ũ' / f1);
      if ((paramTL_messageMediaInvoice.photo == null) || (!paramTL_messageMediaInvoice.photo.mime_type.startsWith("image/"))) {
        break label402;
      }
      paramString = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label345;
      }
      i = 5;
      label114:
      if (!LocaleController.isRTL) {
        break label350;
      }
      f1 = 10.0F;
      label124:
      if (!LocaleController.isRTL) {
        break label357;
      }
      f2 = 123.0F;
      label134:
      paramString.setLayoutParams(LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 9.0F, f2, 0.0F));
      paramString = this.detailTextView;
      if (!LocaleController.isRTL) {
        break label364;
      }
      i = 5;
      label168:
      if (!LocaleController.isRTL) {
        break label369;
      }
      f1 = 10.0F;
      label178:
      if (!LocaleController.isRTL) {
        break label376;
      }
      f2 = 123.0F;
      label188:
      paramString.setLayoutParams(LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 33.0F, f2, 0.0F));
      paramString = this.detailExTextView;
      if (!LocaleController.isRTL) {
        break label383;
      }
      i = 5;
      label222:
      if (!LocaleController.isRTL) {
        break label388;
      }
      f1 = 10.0F;
      label232:
      if (!LocaleController.isRTL) {
        break label395;
      }
    }
    label345:
    label350:
    label357:
    label364:
    label369:
    label376:
    label383:
    label388:
    label395:
    for (float f2 = 123.0F;; f2 = 10.0F)
    {
      paramString.setLayoutParams(LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 90.0F, f2, 0.0F));
      this.imageView.setVisibility(0);
      this.imageView.getImageReceiver().setImage(paramTL_messageMediaInvoice.photo, null, String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(j), Integer.valueOf(k) }), null, null, null, -1, null, 1);
      return;
      i = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7F);
      break;
      i = 3;
      break label114;
      f1 = 123.0F;
      break label124;
      f2 = 10.0F;
      break label134;
      i = 3;
      break label168;
      f1 = 123.0F;
      break label178;
      f2 = 10.0F;
      break label188;
      i = 3;
      break label222;
      f1 = 123.0F;
      break label232;
    }
    label402:
    paramTL_messageMediaInvoice = this.nameTextView;
    if (LocaleController.isRTL)
    {
      i = 5;
      label415:
      paramTL_messageMediaInvoice.setLayoutParams(LayoutHelper.createFrame(-1, -2.0F, i | 0x30, 17.0F, 9.0F, 17.0F, 0.0F));
      paramTL_messageMediaInvoice = this.detailTextView;
      if (!LocaleController.isRTL) {
        break label521;
      }
      i = 5;
      label449:
      paramTL_messageMediaInvoice.setLayoutParams(LayoutHelper.createFrame(-1, -2.0F, i | 0x30, 17.0F, 33.0F, 17.0F, 0.0F));
      paramTL_messageMediaInvoice = this.detailExTextView;
      if (!LocaleController.isRTL) {
        break label526;
      }
    }
    label521:
    label526:
    for (int i = 5;; i = 3)
    {
      paramTL_messageMediaInvoice.setLayoutParams(LayoutHelper.createFrame(-1, -2.0F, i | 0x30, 17.0F, 90.0F, 17.0F, 0.0F));
      this.imageView.setVisibility(8);
      break;
      i = 3;
      break label415;
      i = 3;
      break label449;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/PaymentInfoCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */