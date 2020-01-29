package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class PaymentInfoCell extends FrameLayout {
    private TextView detailExTextView;
    private TextView detailTextView;
    private BackupImageView imageView;
    private TextView nameTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PaymentInfoCell(Context context) {
        super(context);
        Context context2 = context;
        this.imageView = new BackupImageView(context2);
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, LocaleController.isRTL ? 5 : 3, 10.0f, 10.0f, 10.0f, 0.0f));
        this.nameTextView = new TextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 9.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.detailTextView = new TextView(context2);
        this.detailTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setMaxLines(3);
        this.detailTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 33.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.detailExTextView = new TextView(context2);
        this.detailExTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.detailExTextView.setTextSize(1, 13.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailExTextView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 90.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(120.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int bottom = this.detailTextView.getBottom() + AndroidUtilities.dp(3.0f);
        TextView textView = this.detailExTextView;
        textView.layout(textView.getLeft(), bottom, this.detailExTextView.getRight(), this.detailExTextView.getMeasuredHeight() + bottom);
    }

    public void setInvoice(TLRPC.TL_messageMediaInvoice tL_messageMediaInvoice, String str) {
        int i;
        this.nameTextView.setText(tL_messageMediaInvoice.title);
        this.detailTextView.setText(tL_messageMediaInvoice.description);
        this.detailExTextView.setText(str);
        if (AndroidUtilities.isTablet()) {
            i = AndroidUtilities.getMinTabletSide();
        } else {
            Point point = AndroidUtilities.displaySize;
            i = Math.min(point.x, point.y);
        }
        float f = (float) 640;
        float dp = f / ((float) (((int) (((float) i) * 0.7f)) - AndroidUtilities.dp(2.0f)));
        int i2 = (int) (f / dp);
        int i3 = (int) (((float) 360) / dp);
        TLRPC.WebDocument webDocument = tL_messageMediaInvoice.photo;
        int i4 = 5;
        if (webDocument == null || !webDocument.mime_type.startsWith("image/")) {
            this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 9.0f, 17.0f, 0.0f));
            this.detailTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 33.0f, 17.0f, 0.0f));
            TextView textView = this.detailExTextView;
            if (!LocaleController.isRTL) {
                i4 = 3;
            }
            textView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, i4 | 48, 17.0f, 90.0f, 17.0f, 0.0f));
            this.imageView.setVisibility(8);
            return;
        }
        this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 9.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.detailTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 33.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        TextView textView2 = this.detailExTextView;
        if (!LocaleController.isRTL) {
            i4 = 3;
        }
        textView2.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, i4 | 48, LocaleController.isRTL ? 10.0f : 123.0f, 90.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.imageView.setVisibility(0);
        this.imageView.getImageReceiver().setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(tL_messageMediaInvoice.photo)), String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)}), (ImageLocation) null, (String) null, -1, (String) null, tL_messageMediaInvoice, 1);
    }
}
