package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;

public class SendLocationCell extends FrameLayout {
    private SimpleTextView accurateTextView;
    private int currentAccount = UserConfig.selectedAccount;
    private long dialogId;
    private ImageView imageView;
    private Runnable invalidateRunnable = new C08931();
    private RectF rect;
    private SimpleTextView titleTextView;

    /* renamed from: org.telegram.ui.Cells.SendLocationCell$1 */
    class C08931 implements Runnable {
        C08931() {
        }

        public void run() {
            SendLocationCell.this.checkText();
            SendLocationCell.this.invalidate(((int) SendLocationCell.this.rect.left) - 5, ((int) SendLocationCell.this.rect.top) - 5, ((int) SendLocationCell.this.rect.right) + 5, ((int) SendLocationCell.this.rect.bottom) + 5);
            AndroidUtilities.runOnUIThread(SendLocationCell.this.invalidateRunnable, 1000);
        }
    }

    public SendLocationCell(Context context, boolean z) {
        super(context);
        this.imageView = new ImageView(context);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor(z ? Theme.key_location_sendLiveLocationBackground : Theme.key_location_sendLocationBackground), Theme.getColor(z ? Theme.key_location_sendLiveLocationBackground : Theme.key_location_sendLocationBackground));
        Drawable drawable;
        Drawable combinedDrawable;
        if (z) {
            this.rect = new RectF();
            drawable = getResources().getDrawable(C0446R.drawable.livelocationpin);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLocationIcon), Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(createSimpleSelectorCircleDrawable, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable);
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
            setWillNotDraw(false);
        } else {
            drawable = getResources().getDrawable(C0446R.drawable.pin);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLocationIcon), Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(createSimpleSelectorCircleDrawable, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable);
        }
        View view = this.imageView;
        int i = 3;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f = 0.0f;
        float f2 = LocaleController.isRTL ? 0.0f : 17.0f;
        if (LocaleController.isRTL) {
            f = 17.0f;
        }
        addView(view, LayoutHelper.createFrame(40, 40.0f, i2, f2, 13.0f, f, 0.0f));
        this.titleTextView = new SimpleTextView(context);
        this.titleTextView.setTextSize(16);
        if (z) {
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText2));
        } else {
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText7));
        }
        this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
        this.accurateTextView = new SimpleTextView(context);
        this.accurateTextView.setTextSize(true);
        this.accurateTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.accurateTextView.setGravity(LocaleController.isRTL ? true : true);
        context = this.accurateTextView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(context, LayoutHelper.createFrame(-1, 20.0f, 48 | i, LocaleController.isRTL ? 16.0f : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
    }

    private ImageView getImageView() {
        return this.imageView;
    }

    public void setHasLocation(boolean z) {
        if (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null) {
            float f = 0.5f;
            this.titleTextView.setAlpha(z ? 1.0f : 0.5f);
            this.accurateTextView.setAlpha(z ? 1.0f : 0.5f);
            ImageView imageView = this.imageView;
            if (z) {
                f = 1.0f;
            }
            imageView.setAlpha(f);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0f), NUM));
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.rect != null) {
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
        }
    }

    public void setText(String str, String str2) {
        this.titleTextView.setText(str);
        this.accurateTextView.setText(str2);
    }

    public void setDialogId(long j) {
        this.dialogId = j;
        checkText();
    }

    private void checkText() {
        SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (sharingLocationInfo != null) {
            setText(LocaleController.getString("StopLiveLocation", C0446R.string.StopLiveLocation), LocaleController.formatLocationUpdateDate((long) (sharingLocationInfo.messageObject.messageOwner.edit_date != 0 ? sharingLocationInfo.messageObject.messageOwner.edit_date : sharingLocationInfo.messageObject.messageOwner.date)));
        } else {
            setText(LocaleController.getString("SendLiveLocation", C0446R.string.SendLiveLocation), LocaleController.getString("SendLiveLocationInfo", C0446R.string.SendLiveLocationInfo));
        }
    }

    protected void onDraw(Canvas canvas) {
        SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (sharingLocationInfo != null) {
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (sharingLocationInfo.stopTime >= currentTime) {
                float abs = ((float) Math.abs(sharingLocationInfo.stopTime - currentTime)) / ((float) sharingLocationInfo.period);
                if (LocaleController.isRTL) {
                    this.rect.set((float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(43.0f), (float) AndroidUtilities.dp(48.0f));
                } else {
                    this.rect.set((float) (getMeasuredWidth() - AndroidUtilities.dp(43.0f)), (float) AndroidUtilities.dp(18.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(13.0f)), (float) AndroidUtilities.dp(48.0f));
                }
                int color = Theme.getColor("location_liveLocationProgress");
                Theme.chat_radialProgress2Paint.setColor(color);
                Theme.chat_livePaint.setColor(color);
                canvas.drawArc(this.rect, -90.0f, -360.0f * abs, false, Theme.chat_radialProgress2Paint);
                String formatLocationLeftTime = LocaleController.formatLocationLeftTime(Math.abs(sharingLocationInfo.stopTime - currentTime));
                canvas.drawText(formatLocationLeftTime, this.rect.centerX() - (Theme.chat_livePaint.measureText(formatLocationLeftTime) / 2.0f), (float) AndroidUtilities.dp(37.0f), Theme.chat_livePaint);
            }
        }
    }
}
