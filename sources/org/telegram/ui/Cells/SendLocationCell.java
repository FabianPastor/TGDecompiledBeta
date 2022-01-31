package org.telegram.ui.Cells;

import android.content.Context;
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
import org.telegram.tgnet.TLRPC$Message;
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
    private boolean live;
    /* access modifiers changed from: private */
    public RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private SimpleTextView titleTextView;

    public SendLocationCell(Context context, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        this.live = z;
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setTag(z ? "location_sendLiveLocationBackgroundlocation_sendLiveLocationIcon" : "location_sendLocationBackgroundlocation_sendLocationIcon");
        String str = "location_sendLiveLocationBackground";
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42.0f), getThemedColor(z ? str : "location_sendLocationBackground"), getThemedColor(!z ? "location_sendLocationBackground" : str));
        if (z) {
            this.rect = new RectF();
            ShareLocationDrawable shareLocationDrawable = new ShareLocationDrawable(context, 4);
            shareLocationDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_sendLiveLocationIcon"), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(createSimpleSelectorCircleDrawable, shareLocationDrawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable);
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
            setWillNotDraw(false);
        } else {
            Drawable drawable = getResources().getDrawable(NUM);
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_sendLocationIcon"), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(createSimpleSelectorCircleDrawable, drawable);
            combinedDrawable2.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            combinedDrawable2.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable2);
        }
        ImageView imageView3 = this.imageView;
        boolean z2 = LocaleController.isRTL;
        int i = 5;
        addView(imageView3, LayoutHelper.createFrame(42, 42.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 15.0f, 12.0f, !z2 ? 0.0f : 15.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.titleTextView = simpleTextView;
        simpleTextView.setTextSize(16);
        String str2 = "location_sendLiveLocationText";
        this.titleTextView.setTag(z ? str2 : "location_sendLocationText");
        this.titleTextView.setTextColor(getThemedColor(!z ? "location_sendLocationText" : str2));
        this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        SimpleTextView simpleTextView2 = this.titleTextView;
        boolean z3 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 16.0f : 73.0f, 12.0f, z3 ? 73.0f : 16.0f, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context);
        this.accurateTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.accurateTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
        this.accurateTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        SimpleTextView simpleTextView4 = this.accurateTextView;
        boolean z4 = LocaleController.isRTL;
        addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (!z4 ? 3 : i) | 48, z4 ? 16.0f : 73.0f, 37.0f, z4 ? 73.0f : 16.0f, 0.0f));
    }

    private ImageView getImageView() {
        return this.imageView;
    }

    public void setHasLocation(boolean z) {
        if (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null) {
            float f = 1.0f;
            this.titleTextView.setAlpha(z ? 1.0f : 0.5f);
            this.accurateTextView.setAlpha(z ? 1.0f : 0.5f);
            ImageView imageView2 = this.imageView;
            if (!z) {
                f = 0.5f;
            }
            imageView2.setAlpha(f);
        }
        if (this.live) {
            checkText();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0f), NUM));
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

    public void setText(String str, String str2) {
        this.titleTextView.setText(str);
        this.accurateTextView.setText(str2);
    }

    public void setDialogId(long j) {
        this.dialogId = j;
        if (this.live) {
            checkText();
        }
    }

    /* access modifiers changed from: private */
    public void checkText() {
        LocationController.SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (sharingLocationInfo != null) {
            String string = LocaleController.getString("StopLiveLocation", NUM);
            TLRPC$Message tLRPC$Message = sharingLocationInfo.messageObject.messageOwner;
            int i = tLRPC$Message.edit_date;
            setText(string, LocaleController.formatLocationUpdateDate(i != 0 ? (long) i : (long) tLRPC$Message.date));
            return;
        }
        setText(LocaleController.getString("SendLiveLocation", NUM), LocaleController.getString("SendLiveLocationInfo", NUM));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000f, code lost:
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r11.currentAccount).getCurrentTime();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r12) {
        /*
            r11 = this;
            int r0 = r11.currentAccount
            org.telegram.messenger.LocationController r0 = org.telegram.messenger.LocationController.getInstance(r0)
            long r1 = r11.dialogId
            org.telegram.messenger.LocationController$SharingLocationInfo r0 = r0.getSharingLocationInfo(r1)
            if (r0 != 0) goto L_0x000f
            return
        L_0x000f:
            int r1 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getCurrentTime()
            int r2 = r0.stopTime
            if (r2 >= r1) goto L_0x001e
            return
        L_0x001e:
            int r2 = r2 - r1
            int r2 = java.lang.Math.abs(r2)
            float r2 = (float) r2
            int r3 = r0.period
            float r3 = (float) r3
            float r2 = r2 / r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            r4 = 1111490560(0x42400000, float:48.0)
            r5 = 1095761920(0x41500000, float:13.0)
            r6 = 1099956224(0x41900000, float:18.0)
            r7 = 1110179840(0x422CLASSNAME, float:43.0)
            if (r3 == 0) goto L_0x004e
            android.graphics.RectF r3 = r11.rect
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r3.set(r5, r6, r7, r4)
            goto L_0x0071
        L_0x004e:
            android.graphics.RectF r3 = r11.rect
            int r8 = r11.getMeasuredWidth()
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r8 = r8 - r7
            float r7 = (float) r8
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            int r8 = r11.getMeasuredWidth()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r8 = r8 - r5
            float r5 = (float) r8
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r3.set(r7, r6, r5, r4)
        L_0x0071:
            java.lang.String r3 = "location_liveLocationProgress"
            int r3 = r11.getThemedColor(r3)
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.chat_radialProgress2Paint
            r4.setColor(r3)
            android.text.TextPaint r4 = org.telegram.ui.ActionBar.Theme.chat_livePaint
            r4.setColor(r3)
            android.graphics.RectF r6 = r11.rect
            r7 = -1028390912(0xffffffffc2b40000, float:-90.0)
            r3 = -1011613696(0xffffffffc3b40000, float:-360.0)
            float r8 = r2 * r3
            r9 = 0
            android.graphics.Paint r10 = org.telegram.ui.ActionBar.Theme.chat_radialProgress2Paint
            r5 = r12
            r5.drawArc(r6, r7, r8, r9, r10)
            int r0 = r0.stopTime
            int r0 = r0 - r1
            int r0 = java.lang.Math.abs(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatLocationLeftTime(r0)
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_livePaint
            float r1 = r1.measureText(r0)
            android.graphics.RectF r2 = r11.rect
            float r2 = r2.centerX()
            r3 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 / r3
            float r2 = r2 - r1
            r1 = 1108606976(0x42140000, float:37.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_livePaint
            r12.drawText(r0, r2, r1, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SendLocationCell.onDraw(android.graphics.Canvas):void");
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
