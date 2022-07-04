package org.telegram.ui.Cells;

import android.content.Context;
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
        BackupImageView backupImageView = new BackupImageView(context2);
        this.imageView = backupImageView;
        backupImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(8.0f));
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, LocaleController.isRTL ? 5 : 3, 10.0f, 10.0f, 10.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 9.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.detailTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setMaxLines(3);
        this.detailTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 33.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.detailExTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.detailExTextView.setTextSize(1, 14.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.detailExTextView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 90.0f, LocaleController.isRTL ? 123.0f : 10.0f, 9.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h;
        if (this.imageView.getVisibility() != 8) {
            h = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(120.0f), NUM);
        } else {
            h = View.MeasureSpec.makeMeasureSpec(0, 0);
            measureChildWithMargins(this.detailTextView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            ((FrameLayout.LayoutParams) this.detailExTextView.getLayoutParams()).topMargin = AndroidUtilities.dp(33.0f) + this.detailTextView.getMeasuredHeight() + AndroidUtilities.dp(3.0f);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), h);
    }

    public void setInfo(String title, String description, TLRPC.WebDocument photo, String botname, Object parentObject) {
        int maxPhotoWidth;
        TLRPC.WebDocument webDocument = photo;
        this.nameTextView.setText(title);
        this.detailTextView.setText(description);
        this.detailExTextView.setText(botname);
        if (AndroidUtilities.isTablet()) {
            maxPhotoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
        } else {
            maxPhotoWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
        }
        float scale = ((float) 640) / ((float) (maxPhotoWidth - AndroidUtilities.dp(2.0f)));
        int width = (int) (((float) 640) / scale);
        int height = (int) (((float) 360) / scale);
        int i = 5;
        if (webDocument == null || !webDocument.mime_type.startsWith("image/")) {
            this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 9.0f, 17.0f, 0.0f));
            this.detailTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 33.0f, 17.0f, 0.0f));
            TextView textView = this.detailExTextView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            textView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, i | 48, 17.0f, 90.0f, 17.0f, 9.0f));
            this.imageView.setVisibility(8);
            return;
        }
        this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 9.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.detailTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 10.0f : 123.0f, 33.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        TextView textView2 = this.detailExTextView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        textView2.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, i | 48, LocaleController.isRTL ? 10.0f : 123.0f, 90.0f, LocaleController.isRTL ? 123.0f : 10.0f, 0.0f));
        this.imageView.setVisibility(0);
        this.imageView.getImageReceiver().setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(photo)), String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}), (ImageLocation) null, (String) null, -1, (String) null, parentObject, 1);
    }

    public void setInvoice(TLRPC.TL_messageMediaInvoice invoice, String botname) {
        setInfo(invoice.title, invoice.description, invoice.photo, botname, invoice);
    }

    public void setReceipt(TLRPC.TL_payments_paymentReceipt receipt, String botname) {
        setInfo(receipt.title, receipt.description, receipt.photo, botname, receipt);
    }
}
