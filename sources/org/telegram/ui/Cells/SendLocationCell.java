package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ShareLocationDrawable;

public class SendLocationCell extends FrameLayout {
    private SimpleTextView accurateTextView;
    private int currentAccount = UserConfig.selectedAccount;
    private long dialogId;
    private ImageView imageView;
    /* access modifiers changed from: private */
    public Runnable invalidateRunnable = new Runnable() {
        public void run() {
            SendLocationCell.this.checkText();
            SendLocationCell sendLocationCell = SendLocationCell.this;
            sendLocationCell.invalidate(((int) sendLocationCell.rect.left) - 5, ((int) SendLocationCell.this.rect.top) - 5, ((int) SendLocationCell.this.rect.right) + 5, ((int) SendLocationCell.this.rect.bottom) + 5);
            AndroidUtilities.runOnUIThread(SendLocationCell.this.invalidateRunnable, 1000);
        }
    };
    /* access modifiers changed from: private */
    public RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private SimpleTextView titleTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SendLocationCell(Context context, boolean live, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        this.resourcesProvider = resourcesProvider2;
        ImageView imageView2 = new ImageView(context2);
        this.imageView = imageView2;
        imageView2.setTag(live ? "location_sendLiveLocationBackgroundlocation_sendLiveLocationIcon" : "location_sendLocationBackgroundlocation_sendLocationIcon");
        String str = "location_sendLiveLocationBackground";
        Drawable circle = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42.0f), getThemedColor(live ? str : "location_sendLocationBackground"), getThemedColor(!live ? "location_sendLocationBackground" : str));
        if (live) {
            this.rect = new RectF();
            Drawable drawable = new ShareLocationDrawable(context2, 4);
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_sendLiveLocationIcon"), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable);
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
            setWillNotDraw(false);
        } else {
            Drawable drawable2 = getResources().getDrawable(NUM);
            drawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_sendLocationIcon"), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(circle, drawable2);
            combinedDrawable2.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            combinedDrawable2.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable2);
        }
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 12.0f, !LocaleController.isRTL ? 0.0f : 15.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.titleTextView = simpleTextView;
        simpleTextView.setTextSize(16);
        String str2 = "location_sendLiveLocationText";
        this.titleTextView.setTag(live ? str2 : "location_sendLocationText");
        this.titleTextView.setTextColor(getThemedColor(!live ? "location_sendLocationText" : str2));
        this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context2);
        this.accurateTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.accurateTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
        this.accurateTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.accurateTextView, LayoutHelper.createFrame(-1, 20.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
    }

    private ImageView getImageView() {
        return this.imageView;
    }

    public void setHasLocation(boolean value) {
        if (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null) {
            float f = 1.0f;
            this.titleTextView.setAlpha(value ? 1.0f : 0.5f);
            this.accurateTextView.setAlpha(value ? 1.0f : 0.5f);
            ImageView imageView2 = this.imageView;
            if (!value) {
                f = 0.5f;
            }
            imageView2.setAlpha(f);
        }
        checkText();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
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

    /* access modifiers changed from: private */
    public void checkText() {
        LocationController.SharingLocationInfo info = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (info != null) {
            setText(LocaleController.getString("StopLiveLocation", NUM), LocaleController.formatLocationUpdateDate((long) (info.messageObject.messageOwner.edit_date != 0 ? info.messageObject.messageOwner.edit_date : info.messageObject.messageOwner.date)));
        } else {
            setText(LocaleController.getString("SendLiveLocation", NUM), LocaleController.getString("SendLiveLocationInfo", NUM));
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int currentTime;
        LocationController.SharingLocationInfo currentInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (currentInfo != null && currentInfo.stopTime >= (currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {
            float progress = ((float) Math.abs(currentInfo.stopTime - currentTime)) / ((float) currentInfo.period);
            if (LocaleController.isRTL) {
                this.rect.set((float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(43.0f), (float) AndroidUtilities.dp(48.0f));
            } else {
                this.rect.set((float) (getMeasuredWidth() - AndroidUtilities.dp(43.0f)), (float) AndroidUtilities.dp(18.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(13.0f)), (float) AndroidUtilities.dp(48.0f));
            }
            int color = getThemedColor("location_liveLocationProgress");
            Theme.chat_radialProgress2Paint.setColor(color);
            Theme.chat_livePaint.setColor(color);
            canvas.drawArc(this.rect, -90.0f, progress * -360.0f, false, Theme.chat_radialProgress2Paint);
            String text = LocaleController.formatLocationLeftTime(Math.abs(currentInfo.stopTime - currentTime));
            canvas.drawText(text, this.rect.centerX() - (Theme.chat_livePaint.measureText(text) / 2.0f), (float) AndroidUtilities.dp(37.0f), Theme.chat_livePaint);
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
