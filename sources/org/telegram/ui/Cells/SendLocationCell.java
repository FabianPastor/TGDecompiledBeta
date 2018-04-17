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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
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

    public SendLocationCell(Context context, boolean live) {
        Context context2 = context;
        super(context);
        this.imageView = new ImageView(context2);
        Drawable circle = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor(live ? Theme.key_location_sendLiveLocationBackground : Theme.key_location_sendLocationBackground), Theme.getColor(live ? Theme.key_location_sendLiveLocationBackground : Theme.key_location_sendLocationBackground));
        Drawable drawable;
        CombinedDrawable combinedDrawable;
        if (live) {
            r0.rect = new RectF();
            drawable = getResources().getDrawable(R.drawable.livelocationpin);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLocationIcon), Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            r0.imageView.setBackgroundDrawable(combinedDrawable);
            AndroidUtilities.runOnUIThread(r0.invalidateRunnable, 1000);
            setWillNotDraw(false);
        } else {
            drawable = getResources().getDrawable(R.drawable.pin);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLocationIcon), Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            r0.imageView.setBackgroundDrawable(combinedDrawable);
        }
        View view = r0.imageView;
        int i = 3;
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        float f = 0.0f;
        float f2 = LocaleController.isRTL ? 0.0f : 17.0f;
        if (LocaleController.isRTL) {
            f = 17.0f;
        }
        addView(view, LayoutHelper.createFrame(40, 40.0f, i2, f2, 13.0f, f, 0.0f));
        r0.titleTextView = new SimpleTextView(context2);
        r0.titleTextView.setTextSize(16);
        if (live) {
            r0.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText2));
        } else {
            r0.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText7));
        }
        r0.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(r0.titleTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
        r0.accurateTextView = new SimpleTextView(context2);
        r0.accurateTextView.setTextSize(14);
        r0.accurateTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        r0.accurateTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        view = r0.accurateTextView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, 48 | i, LocaleController.isRTL ? 16.0f : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
    }

    private ImageView getImageView() {
        return this.imageView;
    }

    public void setHasLocation(boolean value) {
        if (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null) {
            float f = 0.5f;
            this.titleTextView.setAlpha(value ? 1.0f : 0.5f);
            this.accurateTextView.setAlpha(value ? 1.0f : 0.5f);
            ImageView imageView = this.imageView;
            if (value) {
                f = 1.0f;
            }
            imageView.setAlpha(f);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0f), NUM));
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

    public void setText(String title, String text) {
        this.titleTextView.setText(title);
        this.accurateTextView.setText(text);
    }

    public void setDialogId(long did) {
        this.dialogId = did;
        checkText();
    }

    private void checkText() {
        SharingLocationInfo info = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (info != null) {
            setText(LocaleController.getString("StopLiveLocation", R.string.StopLiveLocation), LocaleController.formatLocationUpdateDate((long) (info.messageObject.messageOwner.edit_date != 0 ? info.messageObject.messageOwner.edit_date : info.messageObject.messageOwner.date)));
        } else {
            setText(LocaleController.getString("SendLiveLocation", R.string.SendLiveLocation), LocaleController.getString("SendLiveLocationInfo", R.string.SendLiveLocationInfo));
        }
    }

    protected void onDraw(Canvas canvas) {
        SharingLocationInfo currentInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (currentInfo != null) {
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (currentInfo.stopTime >= currentTime) {
                float progress = ((float) Math.abs(currentInfo.stopTime - currentTime)) / ((float) currentInfo.period);
                if (LocaleController.isRTL) {
                    this.rect.set((float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(43.0f), (float) AndroidUtilities.dp(48.0f));
                } else {
                    this.rect.set((float) (getMeasuredWidth() - AndroidUtilities.dp(43.0f)), (float) AndroidUtilities.dp(18.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(13.0f)), (float) AndroidUtilities.dp(48.0f));
                }
                int color = Theme.getColor("location_liveLocationProgress");
                Theme.chat_radialProgress2Paint.setColor(color);
                Theme.chat_livePaint.setColor(color);
                canvas.drawArc(this.rect, -90.0f, -360.0f * progress, false, Theme.chat_radialProgress2Paint);
                String text = LocaleController.formatLocationLeftTime(Math.abs(currentInfo.stopTime - currentTime));
                canvas.drawText(text, this.rect.centerX() - (Theme.chat_livePaint.measureText(text) / 2.0f), (float) AndroidUtilities.dp(37.0f), Theme.chat_livePaint);
            }
        }
    }
}
