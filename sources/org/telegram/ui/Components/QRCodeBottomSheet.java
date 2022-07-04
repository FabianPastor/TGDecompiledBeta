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
    public QRCodeBottomSheet(android.content.Context r20, java.lang.String r21, java.lang.String r22) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = 0
            r0.<init>(r1, r2)
            r19.fixNavigationBar()
            java.lang.String r3 = "InviteByQRCode"
            r4 = 2131626259(0x7f0e0913, float:1.887975E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)
            r4 = 1
            r0.setTitle(r3, r4)
            org.telegram.ui.Components.QRCodeBottomSheet$1 r3 = new org.telegram.ui.Components.QRCodeBottomSheet$1
            r3.<init>(r1)
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.FIT_XY
            r3.setScaleType(r5)
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 21
            if (r5 < r6) goto L_0x0033
            org.telegram.ui.Components.QRCodeBottomSheet$2 r5 = new org.telegram.ui.Components.QRCodeBottomSheet$2
            r5.<init>()
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
            r7 = r21
            android.graphics.Bitmap r6 = r0.createQR(r1, r7, r6)
            r0.qrCode = r6
            r3.setImageBitmap(r6)
            org.telegram.ui.Components.RLottieImageView r6 = new org.telegram.ui.Components.RLottieImageView
            r6.<init>(r1)
            r0.iconImage = r6
            r8 = -1
            r6.setBackgroundColor(r8)
            org.telegram.ui.Components.RLottieImageView r6 = r0.iconImage
            r6.setAutoRepeat(r4)
            org.telegram.ui.Components.RLottieImageView r6 = r0.iconImage
            r9 = 2131558516(0x7f0d0074, float:1.874235E38)
            r10 = 60
            r6.setAnimation(r9, r10, r10)
            org.telegram.ui.Components.RLottieImageView r6 = r0.iconImage
            r6.playAnimation()
            org.telegram.ui.Components.QRCodeBottomSheet$3 r6 = new org.telegram.ui.Components.QRCodeBottomSheet$3
            r6.<init>(r1, r3)
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9)
            r6.addView(r3, r8)
            org.telegram.ui.Components.RLottieImageView r8 = r0.iconImage
            r9 = 17
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r10, (int) r9)
            r6.addView(r8, r10)
            r11 = 220(0xdc, float:3.08E-43)
            r12 = 220(0xdc, float:3.08E-43)
            r13 = 1
            r14 = 30
            r15 = 0
            r16 = 30
            r17 = 0
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
            r5.addView(r6, r8)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            r0.help = r8
            r10 = 1096810496(0x41600000, float:14.0)
            r8.setTextSize(r4, r10)
            r11 = r22
            r8.setText(r11)
            r8.setGravity(r4)
            r12 = -1
            r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r14 = 0
            r15 = 1109393408(0x42200000, float:40.0)
            r16 = 1090519040(0x41000000, float:8.0)
            r17 = 1109393408(0x42200000, float:40.0)
            r18 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r5.addView(r8, r12)
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r1)
            r0.buttonTextView = r8
            r12 = 1107820544(0x42080000, float:34.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r8.setPadding(r13, r2, r12, r2)
            r8.setGravity(r9)
            r8.setTextSize(r4, r10)
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r8.setTypeface(r2)
            r2 = 2131628283(0x7f0e10fb, float:1.8883854E38)
            java.lang.String r4 = "ShareQrCode"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r8.setText(r2)
            org.telegram.ui.Components.QRCodeBottomSheet$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.QRCodeBottomSheet$$ExternalSyntheticLambda0
            r2.<init>(r0, r1)
            r8.setOnClickListener(r2)
            r12 = -1
            r13 = 48
            r14 = 80
            r15 = 16
            r16 = 15
            r17 = 16
            r18 = 16
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r5.addView(r8, r2)
            r19.updateColors()
            android.widget.ScrollView r2 = new android.widget.ScrollView
            r2.<init>(r1)
            r2.addView(r5)
            r0.setCustomView(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.QRCodeBottomSheet.<init>(android.content.Context, java.lang.String, java.lang.String):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-QRCodeBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1274lambda$new$0$orgtelegramuiComponentsQRCodeBottomSheet(Context context, View view) {
        Uri uri = AndroidUtilities.getBitmapShareUri(this.qrCode, "qr_tmp.png", Bitmap.CompressFormat.PNG);
        if (uri != null) {
            Intent i = new Intent("android.intent.action.SEND");
            i.setType("image/*");
            i.putExtra("android.intent.extra.STREAM", uri);
            try {
                AndroidUtilities.findActivity(context).startActivityForResult(Intent.createChooser(i, LocaleController.getString("InviteByQRCode", NUM)), 500);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Bitmap createQR(Context context, String key, Bitmap oldBitmap) {
        try {
            HashMap<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 0);
            QRCodeWriter writer = new QRCodeWriter();
            Bitmap bitmap = writer.encode(key, 768, 768, hints, oldBitmap);
            this.imageSize = writer.getImageSize();
            return bitmap;
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
