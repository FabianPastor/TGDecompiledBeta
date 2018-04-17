package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class LocationCell extends FrameLayout {
    private TextView addressTextView;
    private BackupImageView imageView;
    private TextView nameTextView;
    private boolean needDivider;

    public LocationCell(Context context) {
        Context context2 = context;
        super(context);
        this.imageView = new BackupImageView(context2);
        this.imageView.setBackgroundResource(R.drawable.round_grey);
        this.imageView.setSize(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
        this.imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), Mode.MULTIPLY));
        int i = 3;
        addView(this.imageView, LayoutHelper.createFrame(40, 40.0f, 48 | (LocaleController.isRTL ? 5 : 3), LocaleController.isRTL ? 0.0f : 17.0f, 8.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
        r0.nameTextView = new TextView(context2);
        r0.nameTextView.setTextSize(1, 16.0f);
        r0.nameTextView.setMaxLines(1);
        r0.nameTextView.setEllipsize(TruncateAt.END);
        r0.nameTextView.setSingleLine(true);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        int i2 = 72;
        addView(r0.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 48 | (LocaleController.isRTL ? 5 : 3), (float) (LocaleController.isRTL ? 16 : 72), 5.0f, (float) (LocaleController.isRTL ? 72 : 16), 0.0f));
        r0.addressTextView = new TextView(context2);
        r0.addressTextView.setTextSize(1, 14.0f);
        r0.addressTextView.setMaxLines(1);
        r0.addressTextView.setEllipsize(TruncateAt.END);
        r0.addressTextView.setSingleLine(true);
        r0.addressTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        r0.addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        View view = r0.addressTextView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        int i3 = 48 | i;
        float f = (float) (LocaleController.isRTL ? 16 : 72);
        if (!LocaleController.isRTL) {
            i2 = 16;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i3, f, 30.0f, (float) i2, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f) + this.needDivider, NUM));
    }

    public void setLocation(TL_messageMediaVenue location, String icon, boolean divider) {
        this.needDivider = divider;
        this.nameTextView.setText(location.title);
        this.addressTextView.setText(location.address);
        this.imageView.setImage(icon, null, null);
        setWillNotDraw(divider ^ 1);
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
