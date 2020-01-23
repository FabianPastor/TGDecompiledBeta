package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.ActionBar.Theme.MessageDrawable;

public class RadialProgress2 {
    private int backgroundStroke;
    private int circleColor;
    private String circleColorKey;
    private Paint circleMiniPaint = new Paint(1);
    private Paint circlePaint = new Paint(1);
    private int circlePressedColor;
    private String circlePressedColorKey;
    private int circleRadius;
    private boolean drawBackground = true;
    private boolean drawMiniIcon;
    private int iconColor;
    private String iconColorKey;
    private int iconPressedColor;
    private String iconPressedColorKey;
    private boolean isPressed;
    private boolean isPressedMini;
    private MediaActionDrawable mediaActionDrawable;
    private Bitmap miniDrawBitmap;
    private Canvas miniDrawCanvas;
    private MediaActionDrawable miniMediaActionDrawable;
    private Paint miniProgressBackgroundPaint = new Paint(1);
    private ImageReceiver overlayImageView;
    private Paint overlayPaint = new Paint(1);
    private float overrideAlpha = 1.0f;
    private View parent;
    private boolean previousCheckDrawable;
    private int progressColor = -1;
    private RectF progressRect = new RectF();

    public RadialProgress2(View view) {
        this.parent = view;
        this.overlayImageView = new ImageReceiver(view);
        this.overlayImageView.setInvalidateAll(true);
        this.mediaActionDrawable = new MediaActionDrawable();
        MediaActionDrawable mediaActionDrawable = this.mediaActionDrawable;
        view.getClass();
        mediaActionDrawable.setDelegate(new -$$Lambda$F8rg4UBMmP_S27QL-K3VXBnPS_E(view));
        this.miniMediaActionDrawable = new MediaActionDrawable();
        mediaActionDrawable = this.miniMediaActionDrawable;
        view.getClass();
        mediaActionDrawable.setDelegate(new -$$Lambda$F8rg4UBMmP_S27QL-K3VXBnPS_E(view));
        this.miniMediaActionDrawable.setMini(true);
        this.miniMediaActionDrawable.setIcon(4, false);
        this.circleRadius = AndroidUtilities.dp(22.0f);
        this.overlayImageView.setRoundRadius(this.circleRadius);
        this.overlayPaint.setColor(NUM);
    }

    public void setCircleRadius(int i) {
        this.circleRadius = i;
        this.overlayImageView.setRoundRadius(this.circleRadius);
    }

    public void setBackgroundStroke(int i) {
        this.backgroundStroke = i;
        this.circlePaint.setStrokeWidth((float) i);
        this.circlePaint.setStyle(Style.STROKE);
        invalidateParent();
    }

    public void setBackgroundDrawable(MessageDrawable messageDrawable) {
        this.mediaActionDrawable.setBackgroundDrawable(messageDrawable);
        this.miniMediaActionDrawable.setBackgroundDrawable(messageDrawable);
    }

    public void setImageOverlay(PhotoSize photoSize, Document document, Object obj) {
        this.overlayImageView.setImage(ImageLocation.getForDocument(photoSize, document), String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)}), null, null, obj, 1);
    }

    public void setImageOverlay(String str) {
        String format;
        ImageReceiver imageReceiver = this.overlayImageView;
        if (str != null) {
            format = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)});
        } else {
            format = null;
        }
        imageReceiver.setImage(str, format, null, null, -1);
    }

    public void onAttachedToWindow() {
        this.overlayImageView.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        this.overlayImageView.onDetachedFromWindow();
    }

    public void setColors(int i, int i2, int i3, int i4) {
        this.circleColor = i;
        this.circlePressedColor = i2;
        this.iconColor = i3;
        this.iconPressedColor = i4;
        this.circleColorKey = null;
        this.circlePressedColorKey = null;
        this.iconColorKey = null;
        this.iconPressedColorKey = null;
    }

    public void setColors(String str, String str2, String str3, String str4) {
        this.circleColorKey = str;
        this.circlePressedColorKey = str2;
        this.iconColorKey = str3;
        this.iconPressedColorKey = str4;
    }

    public void setDrawBackground(boolean z) {
        this.drawBackground = z;
    }

    public void setProgressRect(int i, int i2, int i3, int i4) {
        this.progressRect.set((float) i, (float) i2, (float) i3, (float) i4);
    }

    public RectF getProgressRect() {
        return this.progressRect;
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
    }

    public void setMiniProgressBackgroundColor(int i) {
        this.miniProgressBackgroundPaint.setColor(i);
    }

    public void setProgress(float f, boolean z) {
        if (this.drawMiniIcon) {
            this.miniMediaActionDrawable.setProgress(f, z);
        } else {
            this.mediaActionDrawable.setProgress(f, z);
        }
    }

    private void invalidateParent() {
        int dp = AndroidUtilities.dp(2.0f);
        View view = this.parent;
        RectF rectF = this.progressRect;
        int i = ((int) rectF.left) - dp;
        int i2 = ((int) rectF.top) - dp;
        dp *= 2;
        view.invalidate(i, i2, ((int) rectF.right) + dp, ((int) rectF.bottom) + dp);
    }

    public int getIcon() {
        return this.mediaActionDrawable.getCurrentIcon();
    }

    public int getMiniIcon() {
        return this.miniMediaActionDrawable.getCurrentIcon();
    }

    public void setIcon(int i, boolean z, boolean z2) {
        if (!z || i != this.mediaActionDrawable.getCurrentIcon()) {
            this.mediaActionDrawable.setIcon(i, z2);
            if (z2) {
                invalidateParent();
            } else {
                this.parent.invalidate();
            }
        }
    }

    public void setMiniIcon(int i, boolean z, boolean z2) {
        if (i != 2 && i != 3 && i != 4) {
            return;
        }
        if (!z || i != this.miniMediaActionDrawable.getCurrentIcon()) {
            this.miniMediaActionDrawable.setIcon(i, z2);
            boolean z3 = i != 4 || this.miniMediaActionDrawable.getTransitionProgress() < 1.0f;
            this.drawMiniIcon = z3;
            if (this.drawMiniIcon) {
                initMiniIcons();
            }
            if (z2) {
                invalidateParent();
            } else {
                this.parent.invalidate();
            }
        }
    }

    public void initMiniIcons() {
        if (this.miniDrawBitmap == null) {
            try {
                this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Config.ARGB_8888);
                this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
            } catch (Throwable unused) {
            }
        }
    }

    public boolean swapIcon(int i) {
        return this.mediaActionDrawable.setIcon(i, false);
    }

    public void setPressed(boolean z, boolean z2) {
        if (z2) {
            this.isPressedMini = z;
        } else {
            this.isPressed = z;
        }
        invalidateParent();
    }

    public void setOverrideAlpha(float f) {
        this.overrideAlpha = f;
    }

    public float getOverrideAlpha() {
        return this.overrideAlpha;
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x021e  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x024a  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0238  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02bc  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02cc  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0174  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x021e  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0238  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x024a  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02bc  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02cc  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0174  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x021e  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x024a  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0238  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02bc  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02cc  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0174  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x021e  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0238  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x024a  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02bc  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02cc  */
    public void draw(android.graphics.Canvas r19) {
        /*
        r18 = this;
        r0 = r18;
        r1 = r19;
        r2 = r0.mediaActionDrawable;
        r2 = r2.getCurrentIcon();
        r3 = 4;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r2 != r3) goto L_0x001a;
    L_0x000f:
        r2 = r0.mediaActionDrawable;
        r2 = r2.getTransitionProgress();
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 < 0) goto L_0x001a;
    L_0x0019:
        return;
    L_0x001a:
        r2 = r0.mediaActionDrawable;
        r2 = r2.getCurrentIcon();
        r5 = r0.mediaActionDrawable;
        r5 = r5.getPreviousIcon();
        r6 = r0.backgroundStroke;
        r7 = 3;
        if (r6 == 0) goto L_0x0042;
    L_0x002b:
        if (r2 != r7) goto L_0x0036;
    L_0x002d:
        r5 = r0.mediaActionDrawable;
        r5 = r5.getTransitionProgress();
    L_0x0033:
        r5 = r4 - r5;
        goto L_0x0064;
    L_0x0036:
        if (r5 != r7) goto L_0x003f;
    L_0x0038:
        r5 = r0.mediaActionDrawable;
        r5 = r5.getTransitionProgress();
        goto L_0x0064;
    L_0x003f:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0064;
    L_0x0042:
        if (r2 == r7) goto L_0x0051;
    L_0x0044:
        r6 = 6;
        if (r2 == r6) goto L_0x0051;
    L_0x0047:
        r6 = 10;
        if (r2 == r6) goto L_0x0051;
    L_0x004b:
        r6 = 8;
        if (r2 == r6) goto L_0x0051;
    L_0x004f:
        if (r2 != 0) goto L_0x005a;
    L_0x0051:
        if (r5 != r3) goto L_0x005a;
    L_0x0053:
        r5 = r0.mediaActionDrawable;
        r5 = r5.getTransitionProgress();
        goto L_0x0064;
    L_0x005a:
        if (r2 == r3) goto L_0x005d;
    L_0x005c:
        goto L_0x003f;
    L_0x005d:
        r5 = r0.mediaActionDrawable;
        r5 = r5.getTransitionProgress();
        goto L_0x0033;
    L_0x0064:
        r6 = r0.isPressedMini;
        if (r6 == 0) goto L_0x0093;
    L_0x0068:
        r6 = r0.iconPressedColorKey;
        if (r6 == 0) goto L_0x0076;
    L_0x006c:
        r7 = r0.miniMediaActionDrawable;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r7.setColor(r6);
        goto L_0x007d;
    L_0x0076:
        r6 = r0.miniMediaActionDrawable;
        r7 = r0.iconPressedColor;
        r6.setColor(r7);
    L_0x007d:
        r6 = r0.circlePressedColorKey;
        if (r6 == 0) goto L_0x008b;
    L_0x0081:
        r7 = r0.circleMiniPaint;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r7.setColor(r6);
        goto L_0x00bd;
    L_0x008b:
        r6 = r0.circleMiniPaint;
        r7 = r0.circlePressedColor;
        r6.setColor(r7);
        goto L_0x00bd;
    L_0x0093:
        r6 = r0.iconColorKey;
        if (r6 == 0) goto L_0x00a1;
    L_0x0097:
        r7 = r0.miniMediaActionDrawable;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r7.setColor(r6);
        goto L_0x00a8;
    L_0x00a1:
        r6 = r0.miniMediaActionDrawable;
        r7 = r0.iconColor;
        r6.setColor(r7);
    L_0x00a8:
        r6 = r0.circleColorKey;
        if (r6 == 0) goto L_0x00b6;
    L_0x00ac:
        r7 = r0.circleMiniPaint;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r7.setColor(r6);
        goto L_0x00bd;
    L_0x00b6:
        r6 = r0.circleMiniPaint;
        r7 = r0.circleColor;
        r6.setColor(r7);
    L_0x00bd:
        r6 = r0.isPressed;
        if (r6 == 0) goto L_0x00ff;
    L_0x00c1:
        r6 = r0.iconPressedColorKey;
        if (r6 == 0) goto L_0x00da;
    L_0x00c5:
        r7 = r0.mediaActionDrawable;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r7.setColor(r6);
        r7 = r0.mediaActionDrawable;
        r8 = r0.circlePressedColorKey;
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r7.setBackColor(r8);
        goto L_0x00e9;
    L_0x00da:
        r6 = r0.mediaActionDrawable;
        r7 = r0.iconPressedColor;
        r6.setColor(r7);
        r6 = r0.mediaActionDrawable;
        r8 = r0.circlePressedColor;
        r6.setBackColor(r8);
        r6 = r7;
    L_0x00e9:
        r7 = r0.circlePressedColorKey;
        if (r7 == 0) goto L_0x00f7;
    L_0x00ed:
        r8 = r0.circlePaint;
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r8.setColor(r7);
        goto L_0x013c;
    L_0x00f7:
        r7 = r0.circlePaint;
        r8 = r0.circlePressedColor;
        r7.setColor(r8);
        goto L_0x013c;
    L_0x00ff:
        r6 = r0.iconColorKey;
        if (r6 == 0) goto L_0x0118;
    L_0x0103:
        r7 = r0.mediaActionDrawable;
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r7.setColor(r6);
        r7 = r0.mediaActionDrawable;
        r8 = r0.circleColorKey;
        r8 = org.telegram.ui.ActionBar.Theme.getColor(r8);
        r7.setBackColor(r8);
        goto L_0x0127;
    L_0x0118:
        r6 = r0.mediaActionDrawable;
        r7 = r0.iconColor;
        r6.setColor(r7);
        r6 = r0.mediaActionDrawable;
        r8 = r0.circleColor;
        r6.setBackColor(r8);
        r6 = r7;
    L_0x0127:
        r7 = r0.circleColorKey;
        if (r7 == 0) goto L_0x0135;
    L_0x012b:
        r8 = r0.circlePaint;
        r7 = org.telegram.ui.ActionBar.Theme.getColor(r7);
        r8.setColor(r7);
        goto L_0x013c;
    L_0x0135:
        r7 = r0.circlePaint;
        r8 = r0.circleColor;
        r7.setColor(r8);
    L_0x013c:
        r7 = r0.drawMiniIcon;
        r8 = 0;
        if (r7 == 0) goto L_0x014a;
    L_0x0141:
        r7 = r0.miniDrawCanvas;
        if (r7 == 0) goto L_0x014a;
    L_0x0145:
        r7 = r0.miniDrawBitmap;
        r7.eraseColor(r8);
    L_0x014a:
        r7 = r0.circlePaint;
        r7 = r7.getAlpha();
        r9 = r0.circlePaint;
        r7 = (float) r7;
        r7 = r7 * r5;
        r10 = r0.overrideAlpha;
        r7 = r7 * r10;
        r7 = (int) r7;
        r9.setAlpha(r7);
        r7 = r0.circleMiniPaint;
        r7 = r7.getAlpha();
        r9 = r0.circleMiniPaint;
        r7 = (float) r7;
        r7 = r7 * r5;
        r10 = r0.overrideAlpha;
        r7 = r7 * r10;
        r7 = (int) r7;
        r9.setAlpha(r7);
        r7 = r0.drawMiniIcon;
        if (r7 == 0) goto L_0x0195;
    L_0x0174:
        r7 = r0.miniDrawCanvas;
        if (r7 == 0) goto L_0x0195;
    L_0x0178:
        r7 = r0.progressRect;
        r7 = r7.width();
        r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r7 = r7 / r9;
        r10 = (double) r7;
        r10 = java.lang.Math.ceil(r10);
        r7 = (int) r10;
        r10 = r0.progressRect;
        r10 = r10.height();
        r10 = r10 / r9;
        r9 = (double) r10;
        r9 = java.lang.Math.ceil(r9);
        r9 = (int) r9;
        goto L_0x01a3;
    L_0x0195:
        r7 = r0.progressRect;
        r7 = r7.centerX();
        r7 = (int) r7;
        r9 = r0.progressRect;
        r9 = r9.centerY();
        r9 = (int) r9;
    L_0x01a3:
        r10 = r0.overlayImageView;
        r10 = r10.hasBitmapImage();
        r12 = 2;
        if (r10 == 0) goto L_0x0211;
    L_0x01ac:
        r10 = r0.overlayImageView;
        r10 = r10.getCurrentAlpha();
        r13 = r0.overlayPaint;
        r14 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r14 = r14 * r10;
        r14 = r14 * r5;
        r15 = r0.overrideAlpha;
        r14 = r14 * r15;
        r14 = (int) r14;
        r13.setAlpha(r14);
        r13 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1));
        if (r13 < 0) goto L_0x01c9;
    L_0x01c6:
        r6 = -1;
        r11 = 0;
        goto L_0x01fa;
    L_0x01c9:
        r13 = android.graphics.Color.red(r6);
        r14 = android.graphics.Color.green(r6);
        r15 = android.graphics.Color.blue(r6);
        r6 = android.graphics.Color.alpha(r6);
        r11 = 255 - r13;
        r11 = (float) r11;
        r11 = r11 * r10;
        r11 = (int) r11;
        r8 = 255 - r14;
        r8 = (float) r8;
        r8 = r8 * r10;
        r8 = (int) r8;
        r4 = 255 - r15;
        r4 = (float) r4;
        r4 = r4 * r10;
        r4 = (int) r4;
        r3 = 255 - r6;
        r3 = (float) r3;
        r3 = r3 * r10;
        r3 = (int) r3;
        r6 = r6 + r3;
        r13 = r13 + r11;
        r14 = r14 + r8;
        r15 = r15 + r4;
        r6 = android.graphics.Color.argb(r6, r13, r14, r15);
        r11 = 1;
    L_0x01fa:
        r3 = r0.mediaActionDrawable;
        r3.setColor(r6);
        r3 = r0.overlayImageView;
        r4 = r0.circleRadius;
        r6 = r7 - r4;
        r8 = r9 - r4;
        r10 = r4 * 2;
        r4 = r4 * 2;
        r3.setImageCoords(r6, r8, r10, r4);
        r16 = r11;
        goto L_0x0213;
    L_0x0211:
        r16 = 1;
    L_0x0213:
        r3 = 0;
        if (r16 == 0) goto L_0x0256;
    L_0x0216:
        r4 = r0.drawBackground;
        if (r4 == 0) goto L_0x0256;
    L_0x021a:
        r4 = r0.drawMiniIcon;
        if (r4 == 0) goto L_0x022d;
    L_0x021e:
        r4 = r0.miniDrawCanvas;
        if (r4 == 0) goto L_0x022d;
    L_0x0222:
        r2 = (float) r7;
        r6 = (float) r9;
        r8 = r0.circleRadius;
        r8 = (float) r8;
        r10 = r0.circlePaint;
        r4.drawCircle(r2, r6, r8, r10);
        goto L_0x0256;
    L_0x022d:
        r4 = 4;
        if (r2 != r4) goto L_0x0234;
    L_0x0230:
        r2 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1));
        if (r2 == 0) goto L_0x0256;
    L_0x0234:
        r2 = r0.backgroundStroke;
        if (r2 == 0) goto L_0x024a;
    L_0x0238:
        r2 = (float) r7;
        r4 = (float) r9;
        r6 = r0.circleRadius;
        r8 = NUM; // 0x40600000 float:3.5 double:5.3360734E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 - r8;
        r6 = (float) r6;
        r8 = r0.circlePaint;
        r1.drawCircle(r2, r4, r6, r8);
        goto L_0x0256;
    L_0x024a:
        r2 = (float) r7;
        r4 = (float) r9;
        r6 = r0.circleRadius;
        r6 = (float) r6;
        r6 = r6 * r5;
        r8 = r0.circlePaint;
        r1.drawCircle(r2, r4, r6, r8);
    L_0x0256:
        r2 = r0.overlayImageView;
        r2 = r2.hasBitmapImage();
        if (r2 == 0) goto L_0x0290;
    L_0x025e:
        r2 = r0.overlayImageView;
        r4 = r0.overrideAlpha;
        r5 = r5 * r4;
        r2.setAlpha(r5);
        r2 = r0.drawMiniIcon;
        if (r2 == 0) goto L_0x0281;
    L_0x026b:
        r2 = r0.miniDrawCanvas;
        if (r2 == 0) goto L_0x0281;
    L_0x026f:
        r4 = r0.overlayImageView;
        r4.draw(r2);
        r2 = r0.miniDrawCanvas;
        r4 = (float) r7;
        r5 = (float) r9;
        r6 = r0.circleRadius;
        r6 = (float) r6;
        r8 = r0.overlayPaint;
        r2.drawCircle(r4, r5, r6, r8);
        goto L_0x0290;
    L_0x0281:
        r2 = r0.overlayImageView;
        r2.draw(r1);
        r2 = (float) r7;
        r4 = (float) r9;
        r5 = r0.circleRadius;
        r5 = (float) r5;
        r6 = r0.overlayPaint;
        r1.drawCircle(r2, r4, r5, r6);
    L_0x0290:
        r2 = r0.mediaActionDrawable;
        r4 = r0.circleRadius;
        r5 = r7 - r4;
        r6 = r9 - r4;
        r7 = r7 + r4;
        r9 = r9 + r4;
        r2.setBounds(r5, r6, r7, r9);
        r2 = r0.mediaActionDrawable;
        r4 = r0.overlayImageView;
        r4 = r4.hasBitmapImage();
        r2.setHasOverlayImage(r4);
        r2 = r0.drawMiniIcon;
        if (r2 == 0) goto L_0x02bc;
    L_0x02ac:
        r2 = r0.miniDrawCanvas;
        if (r2 == 0) goto L_0x02b6;
    L_0x02b0:
        r4 = r0.mediaActionDrawable;
        r4.draw(r2);
        goto L_0x02c8;
    L_0x02b6:
        r2 = r0.mediaActionDrawable;
        r2.draw(r1);
        goto L_0x02c8;
    L_0x02bc:
        r2 = r0.mediaActionDrawable;
        r4 = r0.overrideAlpha;
        r2.setOverrideAlpha(r4);
        r2 = r0.mediaActionDrawable;
        r2.draw(r1);
    L_0x02c8:
        r2 = r0.drawMiniIcon;
        if (r2 == 0) goto L_0x03c6;
    L_0x02cc:
        r2 = r0.progressRect;
        r2 = r2.width();
        r4 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = (float) r4;
        r2 = r2 - r4;
        r2 = java.lang.Math.abs(r2);
        r4 = org.telegram.messenger.AndroidUtilities.density;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 >= 0) goto L_0x0303;
    L_0x02e4:
        r2 = 20;
        r4 = r0.progressRect;
        r4 = r4.centerX();
        r5 = 16;
        r5 = (float) r5;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = (float) r6;
        r4 = r4 + r6;
        r6 = r0.progressRect;
        r6 = r6.centerY();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r6 = r6 + r5;
        r12 = 0;
        goto L_0x031f;
    L_0x0303:
        r2 = 22;
        r4 = r0.progressRect;
        r4 = r4.centerX();
        r5 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = (float) r6;
        r4 = r4 + r6;
        r6 = r0.progressRect;
        r6 = r6.centerY();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r6 = r6 + r5;
    L_0x031f:
        r5 = r2 / 2;
        r7 = r0.miniMediaActionDrawable;
        r7 = r7.getCurrentIcon();
        r8 = 4;
        if (r7 == r8) goto L_0x032d;
    L_0x032a:
        r17 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0339;
    L_0x032d:
        r7 = r0.miniMediaActionDrawable;
        r7 = r7.getTransitionProgress();
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r7 = r8 - r7;
        r17 = r7;
    L_0x0339:
        r3 = (r17 > r3 ? 1 : (r17 == r3 ? 0 : -1));
        if (r3 != 0) goto L_0x0340;
    L_0x033d:
        r3 = 0;
        r0.drawMiniIcon = r3;
    L_0x0340:
        r3 = r0.miniDrawCanvas;
        if (r3 == 0) goto L_0x0362;
    L_0x0344:
        r2 = r2 + 18;
        r2 = r2 + r12;
        r2 = (float) r2;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r7 = (float) r7;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r8 = r5 + 1;
        r8 = (float) r8;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r8 = r8 * r17;
        r9 = org.telegram.ui.ActionBar.Theme.checkboxSquare_eraserPaint;
        r3.drawCircle(r7, r2, r8, r9);
        goto L_0x0375;
    L_0x0362:
        r2 = r0.miniProgressBackgroundPaint;
        r3 = r0.progressColor;
        r2.setColor(r3);
        r2 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r3 = r0.miniProgressBackgroundPaint;
        r1.drawCircle(r4, r6, r2, r3);
    L_0x0375:
        r2 = r0.miniDrawCanvas;
        if (r2 == 0) goto L_0x0389;
    L_0x0379:
        r2 = r0.miniDrawBitmap;
        r3 = r0.progressRect;
        r7 = r3.left;
        r7 = (int) r7;
        r7 = (float) r7;
        r3 = r3.top;
        r3 = (int) r3;
        r3 = (float) r3;
        r8 = 0;
        r1.drawBitmap(r2, r7, r3, r8);
    L_0x0389:
        r2 = (float) r5;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = (float) r3;
        r3 = r3 * r17;
        r5 = r0.circleMiniPaint;
        r1.drawCircle(r4, r6, r3, r5);
        r3 = r0.miniMediaActionDrawable;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r5 = (float) r5;
        r5 = r5 * r17;
        r5 = r4 - r5;
        r5 = (int) r5;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r7 = (float) r7;
        r7 = r7 * r17;
        r7 = r6 - r7;
        r7 = (int) r7;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r8 = (float) r8;
        r8 = r8 * r17;
        r4 = r4 + r8;
        r4 = (int) r4;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r2 = r2 * r17;
        r6 = r6 + r2;
        r2 = (int) r6;
        r3.setBounds(r5, r7, r4, r2);
        r2 = r0.miniMediaActionDrawable;
        r2.draw(r1);
    L_0x03c6:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RadialProgress2.draw(android.graphics.Canvas):void");
    }
}
