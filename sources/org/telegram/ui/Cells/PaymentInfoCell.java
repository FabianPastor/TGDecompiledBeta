package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class PaymentInfoCell extends FrameLayout {
    private TextView detailExTextView;
    private TextView detailTextView;
    private BackupImageView imageView;
    private TextView nameTextView;

    public PaymentInfoCell(Context context) {
        Context context2 = context;
        super(context);
        this.imageView = new BackupImageView(context2);
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, LocaleController.isRTL ? 5 : 3, 10.0f, 10.0f, 10.0f, 0.0f));
        this.nameTextView = new TextView(context2);
        String str = "windowBackgroundWhiteBlackText";
        this.nameTextView.setTextColor(Theme.getColor(str));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 9.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.detailTextView = new TextView(context2);
        this.detailTextView.setTextColor(Theme.getColor(str));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setMaxLines(3);
        this.detailTextView.setEllipsize(TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 33.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.detailExTextView = new TextView(context2);
        this.detailExTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.detailExTextView.setTextSize(1, 13.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        TextView textView = this.detailExTextView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(textView, LayoutHelper.createFrame(-1, -2.0f, i | 48, LocaleController.isRTL ? 10.0f : 123.0f, 90.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(120.0f), NUM));
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int bottom = this.detailTextView.getBottom() + AndroidUtilities.dp(3.0f);
        TextView textView = this.detailExTextView;
        textView.layout(textView.getLeft(), bottom, this.detailExTextView.getRight(), this.detailExTextView.getMeasuredHeight() + bottom);
    }

    public void setInvoice(TL_messageMediaInvoice tL_messageMediaInvoice, String str) {
        int minTabletSide;
        this.nameTextView.setText(tL_messageMediaInvoice.title);
        this.detailTextView.setText(tL_messageMediaInvoice.description);
        this.detailExTextView.setText(str);
        if (AndroidUtilities.isTablet()) {
            minTabletSide = AndroidUtilities.getMinTabletSide();
        } else {
            Point point = AndroidUtilities.displaySize;
            minTabletSide = Math.min(point.x, point.y);
        }
        float f = (float) 640;
        float dp = f / ((float) (((int) (((float) minTabletSide) * 0.7f)) - AndroidUtilities.dp(2.0f)));
        int i = (int) (f / dp);
        minTabletSide = (int) (((float) 360) / dp);
        WebDocument webDocument = tL_messageMediaInvoice.photo;
        int i2 = 5;
        if (webDocument == null || !webDocument.mime_type.startsWith("image/")) {
            this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 9.0f, 17.0f, 0.0f));
            this.detailTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 33.0f, 17.0f, 0.0f));
            TextView textView = this.detailExTextView;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            textView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, i2 | 48, 17.0f, 90.0f, 17.0f, 0.0f));
            this.imageView.setVisibility(8);
            return;
        }
        this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 9.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.detailTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 33.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        TextView textView2 = this.detailExTextView;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        textView2.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, i2 | 48, LocaleController.isRTL ? 10.0f : 123.0f, 90.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.imageView.setVisibility(0);
        this.imageView.getImageReceiver().setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(tL_messageMediaInvoice.photo)), String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(i), Integer.valueOf(minTabletSide)}), null, null, -1, null, tL_messageMediaInvoice, 1);
    }
}
