package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class LanguageCell
  extends FrameLayout
{
  private ImageView checkImage;
  private LocaleController.LocaleInfo currentLocale;
  private boolean isDialog;
  private boolean needDivider;
  private TextView textView;
  private TextView textView2;
  
  public LanguageCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    setWillNotDraw(false);
    this.isDialog = paramBoolean;
    this.textView = new TextView(paramContext);
    TextView localTextView = this.textView;
    Object localObject;
    label112:
    label137:
    float f1;
    int k;
    label157:
    label176:
    float f3;
    if (paramBoolean)
    {
      localObject = "dialogTextBlack";
      localTextView.setTextColor(Theme.getColor((String)localObject));
      this.textView.setTextSize(1, 16.0F);
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label482;
      }
      j = 5;
      ((TextView)localObject).setGravity(j | 0x30);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label488;
      }
      j = 5;
      if (!LocaleController.isRTL) {
        break label494;
      }
      f1 = 71.0F;
      if (!this.isDialog) {
        break label517;
      }
      k = 4;
      float f2 = k;
      if (!LocaleController.isRTL) {
        break label531;
      }
      if (!paramBoolean) {
        break label524;
      }
      k = 23;
      f3 = k;
      label181:
      addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, f1, f2, f3, 0.0F));
      this.textView2 = new TextView(paramContext);
      localTextView = this.textView2;
      if (!paramBoolean) {
        break label538;
      }
      localObject = "dialogTextGray3";
      label231:
      localTextView.setTextColor(Theme.getColor((String)localObject));
      this.textView2.setTextSize(1, 13.0F);
      this.textView2.setLines(1);
      this.textView2.setMaxLines(1);
      this.textView2.setSingleLine(true);
      this.textView2.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.textView2;
      if (!LocaleController.isRTL) {
        break label545;
      }
      j = 5;
      label300:
      ((TextView)localObject).setGravity(j | 0x30);
      localObject = this.textView2;
      if (!LocaleController.isRTL) {
        break label551;
      }
      j = 5;
      label325:
      if (!LocaleController.isRTL) {
        break label557;
      }
      f1 = 71.0F;
      if (!this.isDialog) {
        break label580;
      }
      k = 25;
      label346:
      f2 = k;
      if (!LocaleController.isRTL) {
        break label594;
      }
      if (!paramBoolean) {
        break label587;
      }
      k = 23;
      label365:
      f3 = k;
      label370:
      addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, f1, f2, f3, 0.0F));
      this.checkImage = new ImageView(paramContext);
      this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
      this.checkImage.setImageResource(NUM);
      paramContext = this.checkImage;
      if (!LocaleController.isRTL) {
        break label601;
      }
    }
    label482:
    label488:
    label494:
    label517:
    label524:
    label531:
    label538:
    label545:
    label551:
    label557:
    label580:
    label587:
    label594:
    label601:
    for (int j = i;; j = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(19, 14.0F, j | 0x10, 23.0F, 0.0F, 23.0F, 0.0F));
      return;
      localObject = "windowBackgroundWhiteBlackText";
      break;
      j = 3;
      break label112;
      j = 3;
      break label137;
      if (paramBoolean) {}
      for (k = 23;; k = 16)
      {
        f1 = k;
        break;
      }
      k = 6;
      break label157;
      k = 16;
      break label176;
      f3 = 71.0F;
      break label181;
      localObject = "windowBackgroundWhiteGrayText3";
      break label231;
      j = 3;
      break label300;
      j = 3;
      break label325;
      if (paramBoolean) {}
      for (k = 23;; k = 16)
      {
        f1 = k;
        break;
      }
      k = 28;
      break label346;
      k = 16;
      break label365;
      f3 = 71.0F;
      break label370;
    }
  }
  
  public LocaleController.LocaleInfo getCurrentLocale()
  {
    return this.currentLocale;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    float f;
    int i;
    if (this.isDialog)
    {
      f = 48.0F;
      i = AndroidUtilities.dp(f);
      if (!this.needDivider) {
        break label56;
      }
    }
    label56:
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, NUM));
      return;
      f = 54.0F;
      break;
    }
  }
  
  public void setLanguage(LocaleController.LocaleInfo paramLocaleInfo, String paramString, boolean paramBoolean)
  {
    TextView localTextView = this.textView;
    if (paramString != null) {}
    for (;;)
    {
      localTextView.setText(paramString);
      this.textView2.setText(paramLocaleInfo.nameEnglish);
      this.currentLocale = paramLocaleInfo;
      this.needDivider = paramBoolean;
      return;
      paramString = paramLocaleInfo.name;
    }
  }
  
  public void setLanguageSelected(boolean paramBoolean)
  {
    ImageView localImageView = this.checkImage;
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      localImageView.setVisibility(i);
      return;
    }
  }
  
  public void setValue(String paramString1, String paramString2)
  {
    this.textView.setText(paramString1);
    this.textView2.setText(paramString2);
    this.checkImage.setVisibility(4);
    this.currentLocale = null;
    this.needDivider = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/LanguageCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */