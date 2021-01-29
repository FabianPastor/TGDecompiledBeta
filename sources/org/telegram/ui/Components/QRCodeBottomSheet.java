package org.telegram.ui.Components;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class QRCodeBottomSheet extends BottomSheet {
    private final TextView buttonTextView;
    private final TextView help;
    Bitmap qrCode;

    public QRCodeBottomSheet(Context context, String str) {
        super(context, false);
        setTitle(LocaleController.getString("InviteByQRCode", NUM), true);
        AnonymousClass1 r1 = new ImageView(this, context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size, NUM));
            }
        };
        int dp = AndroidUtilities.dp(54.0f);
        r1.setPadding(dp, dp, dp, dp);
        r1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        Bitmap createQR = createQR(context, str, this.qrCode);
        this.qrCode = createQR;
        r1.setImageBitmap(createQR);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(r1);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 30.0f, 0.0f, 30.0f, 0.0f));
        TextView textView = new TextView(context);
        this.help = textView;
        textView.setTextSize(1, 14.0f);
        textView.setText(LocaleController.getString("QRCodeLinkHelp", NUM));
        textView.setGravity(1);
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 80, 40.0f, 0.0f, 40.0f, 8.0f));
        TextView textView2 = new TextView(context);
        this.buttonTextView = textView2;
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView2.setText(LocaleController.getString("ShareQrCode", NUM));
        textView2.setOnClickListener(new View.OnClickListener(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                QRCodeBottomSheet.this.lambda$new$0$QRCodeBottomSheet(this.f$1, view);
            }
        });
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, 48, 80, 16, 15, 16, 16));
        updateColors();
        setCustomView(linearLayout);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$QRCodeBottomSheet(Context context, View view) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra("android.intent.extra.STREAM", getImageUri(context, this.qrCode));
        try {
            AndroidUtilities.findActivity(context).startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteByQRCode", NUM)), 500);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        return Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "group_invite_qr", (String) null));
    }

    public Bitmap createQR(Context context, String str, Bitmap bitmap) {
        try {
            HashMap hashMap = new HashMap();
            hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hashMap.put(EncodeHintType.MARGIN, 0);
            return new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, 768, 768, hashMap, bitmap, context);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateColors() {
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.help.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        if (getTitleView() != null) {
            getTitleView().setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        }
        setBackgroundColor(Theme.getColor("dialogBackground"));
    }
}
