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
import org.telegram.tgnet.TLRPC;
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public LocationCell(Context context, boolean wrap, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        this.resourcesProvider = resourcesProvider2;
        this.wrapContent = wrap;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.imageView = backupImageView;
        ShapeDrawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), -1);
        this.circleDrawable = createCircleDrawable;
        backupImageView.setBackground(createCircleDrawable);
        this.imageView.setSize(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 11.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.nameTextView = textView;
        textView.setTextSize(1, 16.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 16 : 73), 10.0f, (float) (LocaleController.isRTL ? 73 : 16), 0.0f));
        TextView textView2 = new TextView(context2);
        this.addressTextView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.addressTextView.setMaxLines(1);
        this.addressTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.addressTextView.setSingleLine(true);
        this.addressTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
        this.addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.addressTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) (LocaleController.isRTL ? 16 : 73), 35.0f, (float) (LocaleController.isRTL ? 73 : 16), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.wrapContent) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), NUM));
        }
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public void setLocation(TLRPC.TL_messageMediaVenue location, String icon, int pos, boolean divider) {
        setLocation(location, icon, (String) null, pos, divider);
    }

    public static int getColorForIndex(int index) {
        switch (index % 7) {
            case 0:
                return -1351584;
            case 1:
                return -868277;
            case 2:
                return -12214795;
            case 3:
                return -13187226;
            case 4:
                return -7900675;
            case 5:
                return -12338729;
            default:
                return -1285237;
        }
    }

    public void setLocation(TLRPC.TL_messageMediaVenue location, String icon, String label, int pos, boolean divider) {
        this.needDivider = divider;
        this.circleDrawable.getPaint().setColor(getColorForIndex(pos));
        this.nameTextView.setText(location.title);
        if (label != null) {
            this.addressTextView.setText(label);
        } else {
            this.addressTextView.setText(location.address);
        }
        this.imageView.setImage(icon, (String) null, (Drawable) null);
        setWillNotDraw(!divider);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
