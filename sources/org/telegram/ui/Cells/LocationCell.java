package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class LocationCell extends FrameLayout {
    private TextView addressTextView;
    private ShapeDrawable circleDrawable;
    private BackupImageView imageView;
    private TextView nameTextView;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean wrapContent;

    public LocationCell(Context context, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        this.wrapContent = z;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        ShapeDrawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), -1);
        this.circleDrawable = createCircleDrawable;
        backupImageView.setBackground(createCircleDrawable);
        this.imageView.setSize(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
        BackupImageView backupImageView2 = this.imageView;
        boolean z2 = LocaleController.isRTL;
        int i = 5;
        addView(backupImageView2, LayoutHelper.createFrame(42, 42.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 15.0f, 11.0f, z2 ? 15.0f : 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextSize(1, 16.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView2 = this.nameTextView;
        boolean z3 = LocaleController.isRTL;
        int i2 = 16;
        addView(textView2, LayoutHelper.createFrame(-2, -2.0f, (z3 ? 5 : 3) | 48, (float) (z3 ? 16 : 73), 10.0f, (float) (z3 ? 73 : 16), 0.0f));
        TextView textView3 = new TextView(context);
        this.addressTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.addressTextView.setMaxLines(1);
        this.addressTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.addressTextView.setSingleLine(true);
        this.addressTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
        this.addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView4 = this.addressTextView;
        boolean z4 = LocaleController.isRTL;
        addView(textView4, LayoutHelper.createFrame(-2, -2.0f, (!z4 ? 3 : i) | 48, (float) (z4 ? 16 : 73), 35.0f, (float) (z4 ? 73 : i2), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.wrapContent) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        }
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public void setLocation(TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, String str, int i, boolean z) {
        setLocation(tLRPC$TL_messageMediaVenue, str, (String) null, i, z);
    }

    public static int getColorForIndex(int i) {
        int i2 = i % 7;
        if (i2 == 0) {
            return -1351584;
        }
        if (i2 == 1) {
            return -868277;
        }
        if (i2 == 2) {
            return -12214795;
        }
        if (i2 == 3) {
            return -13187226;
        }
        if (i2 != 4) {
            return i2 != 5 ? -1285237 : -12338729;
        }
        return -7900675;
    }

    public void setLocation(TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue, String str, String str2, int i, boolean z) {
        this.needDivider = z;
        this.circleDrawable.getPaint().setColor(getColorForIndex(i));
        this.nameTextView.setText(tLRPC$TL_messageMediaVenue.title);
        if (str2 != null) {
            this.addressTextView.setText(str2);
        } else {
            this.addressTextView.setText(tLRPC$TL_messageMediaVenue.address);
        }
        this.imageView.setImage(str, (String) null, (Drawable) null);
        setWillNotDraw(!z);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
