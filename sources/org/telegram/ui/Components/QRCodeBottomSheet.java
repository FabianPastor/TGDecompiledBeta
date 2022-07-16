package org.telegram.ui.Components;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class QRCodeBottomSheet extends BottomSheet {
    private final TextView buttonTextView;
    private final TextView help;
    RLottieImageView iconImage;
    int imageSize;
    Bitmap qrCode;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public QRCodeBottomSheet(android.content.Context r17, java.lang.String r18, java.lang.String r19) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = 0
            r0.<init>(r1, r2)
            r16.fixNavigationBar()
            java.lang.String r3 = "InviteByQRCode"
            r4 = 2131626296(0x7f0e0938, float:1.8879824E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)
            r4 = 1
            r0.setTitle(r3, r4)
            org.telegram.ui.Components.QRCodeBottomSheet$1 r3 = new org.telegram.ui.Components.QRCodeBottomSheet$1
            r3.<init>(r0, r1)
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.FIT_XY
            r3.setScaleType(r5)
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 21
            if (r5 < r6) goto L_0x0033
            org.telegram.ui.Components.QRCodeBottomSheet$2 r5 = new org.telegram.ui.Components.QRCodeBottomSheet$2
            r5.<init>(r0)
            r3.setOutlineProvider(r5)
            r3.setClipToOutline(r4)
        L_0x0033:
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r1)
            r5.setOrientation(r4)
            r6 = 1098907648(0x41800000, float:16.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r5.setPadding(r2, r6, r2, r2)
            android.graphics.Bitmap r6 = r0.qrCode
            r7 = r18
            android.graphics.Bitmap r6 = r0.createQR(r1, r7, r6)
            r0.qrCode = r6
            r3.setImageBitmap(r6)
            org.telegram.ui.Components.RLottieImageView r6 = new org.telegram.ui.Components.RLottieImageView
            r6.<init>(r1)
            r0.iconImage = r6
            r7 = -1
            r6.setBackgroundColor(r7)
            org.telegram.ui.Components.RLottieImageView r6 = r0.iconImage
            r6.setAutoRepeat(r4)
            org.telegram.ui.Components.RLottieImageView r6 = r0.iconImage
            r8 = 2131558523(0x7f0d007b, float:1.8742364E38)
            r9 = 60
            r6.setAnimation((int) r8, (int) r9, (int) r9)
            org.telegram.ui.Components.RLottieImageView r6 = r0.iconImage
            r6.playAnimation()
            org.telegram.ui.Components.QRCodeBottomSheet$3 r6 = new org.telegram.ui.Components.QRCodeBottomSheet$3
            r6.<init>(r1, r3)
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8)
            r6.addView(r3, r7)
            org.telegram.ui.Components.RLottieImageView r3 = r0.iconImage
            r7 = 17
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r7)
            r6.addView(r3, r8)
            r9 = 220(0xdc, float:3.08E-43)
            r10 = 220(0xdc, float:3.08E-43)
            r11 = 1
            r12 = 30
            r13 = 0
            r14 = 30
            r15 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
            r5.addView(r6, r3)
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r0.help = r3
            r6 = 1096810496(0x41600000, float:14.0)
            r3.setTextSize(r4, r6)
            r8 = r19
            r3.setText(r8)
            r3.setGravity(r4)
            r8 = -1
            r9 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r10 = 0
            r11 = 1109393408(0x42200000, float:40.0)
            r12 = 1090519040(0x41000000, float:8.0)
            r13 = 1109393408(0x42200000, float:40.0)
            r14 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r5.addView(r3, r8)
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r0.buttonTextView = r3
            r8 = 1107820544(0x42080000, float:34.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r3.setPadding(r9, r2, r8, r2)
            r3.setGravity(r7)
            r3.setTextSize(r4, r6)
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r3.setTypeface(r2)
            r2 = 2131628333(0x7f0e112d, float:1.8883956E38)
            java.lang.String r4 = "ShareQrCode"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r3.setText(r2)
            org.telegram.ui.Components.QRCodeBottomSheet$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.QRCodeBottomSheet$$ExternalSyntheticLambda0
            r2.<init>(r0, r1)
            r3.setOnClickListener(r2)
            r6 = -1
            r7 = 48
            r8 = 80
            r9 = 16
            r10 = 15
            r11 = 16
            r12 = 16
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r6, (int) r7, (int) r8, (int) r9, (int) r10, (int) r11, (int) r12)
            r5.addView(r3, r2)
            r16.updateColors()
            android.widget.ScrollView r2 = new android.widget.ScrollView
            r2.<init>(r1)
            r2.addView(r5)
            r0.setCustomView(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.QRCodeBottomSheet.<init>(android.content.Context, java.lang.String, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Context context, View view) {
        Uri bitmapShareUri = AndroidUtilities.getBitmapShareUri(this.qrCode, "qr_tmp.png", Bitmap.CompressFormat.PNG);
        if (bitmapShareUri != null) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("image/*");
            intent.putExtra("android.intent.extra.STREAM", bitmapShareUri);
            try {
                AndroidUtilities.findActivity(context).startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteByQRCode", NUM)), 500);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap createQR(Context context, String str, Bitmap bitmap) {
        try {
            HashMap hashMap = new HashMap();
            hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hashMap.put(EncodeHintType.MARGIN, 0);
            QRCodeWriter qRCodeWriter = new QRCodeWriter();
            Bitmap encode = qRCodeWriter.encode(str, 768, 768, hashMap, bitmap);
            this.imageSize = qRCodeWriter.getImageSize();
            return encode;
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
