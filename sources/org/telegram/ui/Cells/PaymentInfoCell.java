package org.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class PaymentInfoCell extends FrameLayout {
    private TextView detailExTextView;
    private TextView detailTextView;
    private BackupImageView imageView;
    private TextView nameTextView;

    public PaymentInfoCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, LocaleController.isRTL ? 5 : 3, 10.0f, 10.0f, 10.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 9.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.detailTextView = new TextView(context);
        this.detailTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setMaxLines(3);
        this.detailTextView.setEllipsize(TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 33.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.detailExTextView = new TextView(context);
        this.detailExTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.detailExTextView.setTextSize(1, 13.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view = this.detailExTextView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -2.0f, i | 48, LocaleController.isRTL ? 10.0f : 123.0f, 90.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(120.0f), NUM));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int top2 = this.detailTextView.getBottom() + AndroidUtilities.dp(3.0f);
        this.detailExTextView.layout(this.detailExTextView.getLeft(), top2, this.detailExTextView.getRight(), this.detailExTextView.getMeasuredHeight() + top2);
    }

    public void setInvoice(TL_messageMediaInvoice invoice, String botname) {
        int maxPhotoWidth;
        TL_messageMediaInvoice tL_messageMediaInvoice = invoice;
        this.nameTextView.setText(tL_messageMediaInvoice.title);
        this.detailTextView.setText(tL_messageMediaInvoice.description);
        this.detailExTextView.setText(botname);
        if (AndroidUtilities.isTablet()) {
            maxPhotoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * NUM);
        } else {
            maxPhotoWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
        }
        float scale = ((float) 640) / ((float) (maxPhotoWidth - AndroidUtilities.dp(2.0f)));
        int width = (int) (((float) 640) / scale);
        int height = (int) (((float) 360) / scale);
        int i = 3;
        if (tL_messageMediaInvoice.photo == null || !tL_messageMediaInvoice.photo.mime_type.startsWith("image/")) {
            r0.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 9.0f, 17.0f, 0.0f));
            r0.detailTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 33.0f, 17.0f, 0.0f));
            TextView textView = r0.detailExTextView;
            if (LocaleController.isRTL) {
                i = 5;
            }
            textView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, i | 48, 17.0f, 90.0f, 17.0f, 0.0f));
            r0.imageView.setVisibility(8);
            return;
        }
        r0.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 9.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        r0.detailTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 33.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        textView = r0.detailExTextView;
        if (LocaleController.isRTL) {
            i = 5;
        }
        textView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, i | 48, LocaleController.isRTL ? 10.0f : 123.0f, 90.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        r0.imageView.setVisibility(0);
        r0.imageView.getImageReceiver().setImage(tL_messageMediaInvoice.photo, null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}), null, null, null, -1, null, 1);
    }
}
