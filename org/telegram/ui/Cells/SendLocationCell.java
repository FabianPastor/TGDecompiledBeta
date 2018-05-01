package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;

public class SendLocationCell
  extends FrameLayout
{
  private SimpleTextView accurateTextView;
  private int currentAccount = UserConfig.selectedAccount;
  private long dialogId;
  private ImageView imageView;
  private Runnable invalidateRunnable = new Runnable()
  {
    public void run()
    {
      SendLocationCell.this.checkText();
      SendLocationCell.this.invalidate((int)SendLocationCell.this.rect.left - 5, (int)SendLocationCell.this.rect.top - 5, (int)SendLocationCell.this.rect.right + 5, (int)SendLocationCell.this.rect.bottom + 5);
      AndroidUtilities.runOnUIThread(SendLocationCell.this.invalidateRunnable, 1000L);
    }
  };
  private RectF rect;
  private SimpleTextView titleTextView;
  
  public SendLocationCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.imageView = new ImageView(paramContext);
    int i = AndroidUtilities.dp(40.0F);
    Object localObject;
    int j;
    label65:
    Drawable localDrawable;
    label176:
    label191:
    float f1;
    if (paramBoolean)
    {
      localObject = "location_sendLiveLocationBackground";
      j = Theme.getColor((String)localObject);
      if (!paramBoolean) {
        break label483;
      }
      localObject = "location_sendLiveLocationBackground";
      localObject = Theme.createSimpleSelectorCircleDrawable(i, j, Theme.getColor((String)localObject));
      if (!paramBoolean) {
        break label490;
      }
      this.rect = new RectF();
      localDrawable = getResources().getDrawable(NUM);
      localDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_sendLocationIcon"), PorterDuff.Mode.MULTIPLY));
      localObject = new CombinedDrawable((Drawable)localObject, localDrawable);
      ((CombinedDrawable)localObject).setCustomSize(AndroidUtilities.dp(40.0F), AndroidUtilities.dp(40.0F));
      this.imageView.setBackgroundDrawable((Drawable)localObject);
      AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000L);
      setWillNotDraw(false);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label576;
      }
      j = 5;
      if (!LocaleController.isRTL) {
        break label582;
      }
      f1 = 0.0F;
      label200:
      if (!LocaleController.isRTL) {
        break label589;
      }
      f2 = 17.0F;
      label210:
      addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, j | 0x30, f1, 13.0F, f2, 0.0F));
      this.titleTextView = new SimpleTextView(paramContext);
      this.titleTextView.setTextSize(16);
      if (!paramBoolean) {
        break label595;
      }
      this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText2"));
      label272:
      localObject = this.titleTextView;
      if (!LocaleController.isRTL) {
        break label610;
      }
      j = 5;
      label287:
      ((SimpleTextView)localObject).setGravity(j);
      this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.titleTextView;
      if (!LocaleController.isRTL) {
        break label616;
      }
      j = 5;
      label321:
      if (!LocaleController.isRTL) {
        break label622;
      }
      f1 = 16.0F;
      label331:
      if (!LocaleController.isRTL) {
        break label629;
      }
      f2 = 73.0F;
      label341:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, j | 0x30, f1, 12.0F, f2, 0.0F));
      this.accurateTextView = new SimpleTextView(paramContext);
      this.accurateTextView.setTextSize(14);
      this.accurateTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
      paramContext = this.accurateTextView;
      if (!LocaleController.isRTL) {
        break label636;
      }
      j = 5;
      label412:
      paramContext.setGravity(j);
      paramContext = this.accurateTextView;
      if (!LocaleController.isRTL) {
        break label642;
      }
      j = 5;
      label432:
      if (!LocaleController.isRTL) {
        break label648;
      }
      f1 = 16.0F;
      label442:
      if (!LocaleController.isRTL) {
        break label655;
      }
    }
    label483:
    label490:
    label576:
    label582:
    label589:
    label595:
    label610:
    label616:
    label622:
    label629:
    label636:
    label642:
    label648:
    label655:
    for (float f2 = 73.0F;; f2 = 16.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, 20.0F, j | 0x30, f1, 37.0F, f2, 0.0F));
      return;
      localObject = "location_sendLocationBackground";
      break;
      localObject = "location_sendLocationBackground";
      break label65;
      localDrawable = getResources().getDrawable(NUM);
      localDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_sendLocationIcon"), PorterDuff.Mode.MULTIPLY));
      localObject = new CombinedDrawable((Drawable)localObject, localDrawable);
      ((CombinedDrawable)localObject).setCustomSize(AndroidUtilities.dp(40.0F), AndroidUtilities.dp(40.0F));
      ((CombinedDrawable)localObject).setIconSize(AndroidUtilities.dp(24.0F), AndroidUtilities.dp(24.0F));
      this.imageView.setBackgroundDrawable((Drawable)localObject);
      break label176;
      j = 3;
      break label191;
      f1 = 17.0F;
      break label200;
      f2 = 0.0F;
      break label210;
      this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText7"));
      break label272;
      j = 3;
      break label287;
      j = 3;
      break label321;
      f1 = 73.0F;
      break label331;
      f2 = 16.0F;
      break label341;
      j = 3;
      break label412;
      j = 3;
      break label432;
      f1 = 73.0F;
      break label442;
    }
  }
  
  private void checkText()
  {
    LocationController.SharingLocationInfo localSharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
    long l;
    if (localSharingLocationInfo != null)
    {
      String str = LocaleController.getString("StopLiveLocation", NUM);
      if (localSharingLocationInfo.messageObject.messageOwner.edit_date != 0)
      {
        l = localSharingLocationInfo.messageObject.messageOwner.edit_date;
        setText(str, LocaleController.formatLocationUpdateDate(l));
      }
    }
    for (;;)
    {
      return;
      l = localSharingLocationInfo.messageObject.messageOwner.date;
      break;
      setText(LocaleController.getString("SendLiveLocation", NUM), LocaleController.getString("SendLiveLocationInfo", NUM));
    }
  }
  
  private ImageView getImageView()
  {
    return this.imageView;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.rect != null) {
      AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000L);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    Object localObject = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
    if (localObject == null) {}
    int i;
    do
    {
      return;
      i = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
    } while (((LocationController.SharingLocationInfo)localObject).stopTime < i);
    float f = Math.abs(((LocationController.SharingLocationInfo)localObject).stopTime - i) / ((LocationController.SharingLocationInfo)localObject).period;
    if (LocaleController.isRTL) {
      this.rect.set(AndroidUtilities.dp(13.0F), AndroidUtilities.dp(18.0F), AndroidUtilities.dp(43.0F), AndroidUtilities.dp(48.0F));
    }
    for (;;)
    {
      int j = Theme.getColor("location_liveLocationProgress");
      Theme.chat_radialProgress2Paint.setColor(j);
      Theme.chat_livePaint.setColor(j);
      paramCanvas.drawArc(this.rect, -90.0F, -360.0F * f, false, Theme.chat_radialProgress2Paint);
      localObject = LocaleController.formatLocationLeftTime(Math.abs(((LocationController.SharingLocationInfo)localObject).stopTime - i));
      f = Theme.chat_livePaint.measureText((String)localObject);
      paramCanvas.drawText((String)localObject, this.rect.centerX() - f / 2.0F, AndroidUtilities.dp(37.0F), Theme.chat_livePaint);
      break;
      this.rect.set(getMeasuredWidth() - AndroidUtilities.dp(43.0F), AndroidUtilities.dp(18.0F), getMeasuredWidth() - AndroidUtilities.dp(13.0F), AndroidUtilities.dp(48.0F));
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0F), NUM));
  }
  
  public void setDialogId(long paramLong)
  {
    this.dialogId = paramLong;
    checkText();
  }
  
  public void setHasLocation(boolean paramBoolean)
  {
    float f1 = 1.0F;
    Object localObject;
    if (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null)
    {
      localObject = this.titleTextView;
      if (!paramBoolean) {
        break label74;
      }
      f2 = 1.0F;
      ((SimpleTextView)localObject).setAlpha(f2);
      localObject = this.accurateTextView;
      if (!paramBoolean) {
        break label82;
      }
      f2 = 1.0F;
      label49:
      ((SimpleTextView)localObject).setAlpha(f2);
      localObject = this.imageView;
      if (!paramBoolean) {
        break label90;
      }
    }
    label74:
    label82:
    label90:
    for (float f2 = f1;; f2 = 0.5F)
    {
      ((ImageView)localObject).setAlpha(f2);
      return;
      f2 = 0.5F;
      break;
      f2 = 0.5F;
      break label49;
    }
  }
  
  public void setText(String paramString1, String paramString2)
  {
    this.titleTextView.setText(paramString1);
    this.accurateTextView.setText(paramString2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/SendLocationCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */