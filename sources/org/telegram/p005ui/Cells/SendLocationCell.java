package org.telegram.p005ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.UserConfig;
import org.telegram.p005ui.ActionBar.SimpleTextView;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.CombinedDrawable;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.tgnet.ConnectionsManager;

/* renamed from: org.telegram.ui.Cells.SendLocationCell */
public class SendLocationCell extends FrameLayout {
    private SimpleTextView accurateTextView;
    private int currentAccount = UserConfig.selectedAccount;
    private long dialogId;
    private ImageView imageView;
    private Runnable invalidateRunnable = new CLASSNAME();
    private RectF rect;
    private SimpleTextView titleTextView;

    /* renamed from: org.telegram.ui.Cells.SendLocationCell$1 */
    class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            SendLocationCell.this.checkText();
            SendLocationCell.this.invalidate(((int) SendLocationCell.this.rect.left) - 5, ((int) SendLocationCell.this.rect.top) - 5, ((int) SendLocationCell.this.rect.right) + 5, ((int) SendLocationCell.this.rect.bottom) + 5);
            AndroidUtilities.runOnUIThread(SendLocationCell.this.invalidateRunnable, 1000);
        }
    }

    public SendLocationCell(Context context, boolean live) {
        String str;
        super(context);
        this.imageView = new ImageView(context);
        this.imageView.setTag(live ? "location_sendLiveLocationBackgroundlocation_sendLiveLocationIcon" : "location_sendLocationBackgroundlocation_sendLocationIcon");
        int dp = AndroidUtilities.m10dp(40.0f);
        int color = Theme.getColor(live ? Theme.key_location_sendLiveLocationBackground : Theme.key_location_sendLocationBackground);
        if (live) {
            str = Theme.key_location_sendLiveLocationBackground;
        } else {
            str = Theme.key_location_sendLocationBackground;
        }
        Drawable circle = Theme.createSimpleSelectorCircleDrawable(dp, color, Theme.getColor(str));
        Drawable drawable;
        CombinedDrawable combinedDrawable;
        if (live) {
            this.rect = new RectF();
            drawable = getResources().getDrawable(CLASSNAMER.drawable.livelocationpin);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLiveLocationIcon), Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.m10dp(40.0f), AndroidUtilities.m10dp(40.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable);
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
            setWillNotDraw(false);
        } else {
            drawable = getResources().getDrawable(CLASSNAMER.drawable.pin);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLocationIcon), Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.m10dp(40.0f), AndroidUtilities.m10dp(40.0f));
            combinedDrawable.setIconSize(AndroidUtilities.m10dp(24.0f), AndroidUtilities.m10dp(24.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable);
        }
        addView(this.imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 13.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
        this.titleTextView = new SimpleTextView(context);
        this.titleTextView.setTextSize(16);
        this.titleTextView.setTag(live ? Theme.key_windowBackgroundWhiteRedText2 : Theme.key_windowBackgroundWhiteBlueText7);
        this.titleTextView.setTextColor(Theme.getColor(live ? Theme.key_windowBackgroundWhiteRedText2 : Theme.key_windowBackgroundWhiteBlueText7));
        this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
        this.accurateTextView = new SimpleTextView(context);
        this.accurateTextView.setTextSize(14);
        this.accurateTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.accurateTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.accurateTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
    }

    private ImageView getImageView() {
        return this.imageView;
    }

    public void setHasLocation(boolean value) {
        float f = 1.0f;
        if (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null) {
            float f2;
            SimpleTextView simpleTextView = this.titleTextView;
            if (value) {
                f2 = 1.0f;
            } else {
                f2 = 0.5f;
            }
            simpleTextView.setAlpha(f2);
            simpleTextView = this.accurateTextView;
            if (value) {
                f2 = 1.0f;
            } else {
                f2 = 0.5f;
            }
            simpleTextView.setAlpha(f2);
            ImageView imageView = this.imageView;
            if (!value) {
                f = 0.5f;
            }
            imageView.setAlpha(f);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(66.0f), NUM));
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
            setText(LocaleController.getString("StopLiveLocation", CLASSNAMER.string.StopLiveLocation), LocaleController.formatLocationUpdateDate(info.messageObject.messageOwner.edit_date != 0 ? (long) info.messageObject.messageOwner.edit_date : (long) info.messageObject.messageOwner.date));
        } else {
            setText(LocaleController.getString("SendLiveLocation", CLASSNAMER.string.SendLiveLocation), LocaleController.getString("SendLiveLocationInfo", CLASSNAMER.string.SendLiveLocationInfo));
        }
    }

    protected void onDraw(Canvas canvas) {
        SharingLocationInfo currentInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (currentInfo != null) {
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (currentInfo.stopTime >= currentTime) {
                float progress = ((float) Math.abs(currentInfo.stopTime - currentTime)) / ((float) currentInfo.period);
                if (LocaleController.isRTL) {
                    this.rect.set((float) AndroidUtilities.m10dp(13.0f), (float) AndroidUtilities.m10dp(18.0f), (float) AndroidUtilities.m10dp(43.0f), (float) AndroidUtilities.m10dp(48.0f));
                } else {
                    this.rect.set((float) (getMeasuredWidth() - AndroidUtilities.m10dp(43.0f)), (float) AndroidUtilities.m10dp(18.0f), (float) (getMeasuredWidth() - AndroidUtilities.m10dp(13.0f)), (float) AndroidUtilities.m10dp(48.0f));
                }
                int color = Theme.getColor(Theme.key_location_liveLocationProgress);
                Theme.chat_radialProgress2Paint.setColor(color);
                Theme.chat_livePaint.setColor(color);
                canvas.drawArc(this.rect, -90.0f, -360.0f * progress, false, Theme.chat_radialProgress2Paint);
                String text = LocaleController.formatLocationLeftTime(Math.abs(currentInfo.stopTime - currentTime));
                canvas.drawText(text, this.rect.centerX() - (Theme.chat_livePaint.measureText(text) / 2.0f), (float) AndroidUtilities.m10dp(37.0f), Theme.chat_livePaint);
            }
        }
    }
}
