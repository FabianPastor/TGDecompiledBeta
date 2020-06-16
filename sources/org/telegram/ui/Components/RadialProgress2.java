package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.MediaActionDrawable;

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
    private int progressColor = -1;
    private RectF progressRect = new RectF();

    public RadialProgress2(View view) {
        this.parent = view;
        ImageReceiver imageReceiver = new ImageReceiver(view);
        this.overlayImageView = imageReceiver;
        imageReceiver.setInvalidateAll(true);
        MediaActionDrawable mediaActionDrawable2 = new MediaActionDrawable();
        this.mediaActionDrawable = mediaActionDrawable2;
        view.getClass();
        mediaActionDrawable2.setDelegate(new MediaActionDrawable.MediaActionDrawableDelegate(view) {
            public final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void invalidate() {
                this.f$0.invalidate();
            }
        });
        MediaActionDrawable mediaActionDrawable3 = new MediaActionDrawable();
        this.miniMediaActionDrawable = mediaActionDrawable3;
        view.getClass();
        mediaActionDrawable3.setDelegate(new MediaActionDrawable.MediaActionDrawableDelegate(view) {
            public final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void invalidate() {
                this.f$0.invalidate();
            }
        });
        this.miniMediaActionDrawable.setMini(true);
        this.miniMediaActionDrawable.setIcon(4, false);
        int dp = AndroidUtilities.dp(22.0f);
        this.circleRadius = dp;
        this.overlayImageView.setRoundRadius(dp);
        this.overlayPaint.setColor(NUM);
    }

    public void setCircleRadius(int i) {
        this.circleRadius = i;
        this.overlayImageView.setRoundRadius(i);
    }

    public void setBackgroundDrawable(Theme.MessageDrawable messageDrawable) {
        this.mediaActionDrawable.setBackgroundDrawable(messageDrawable);
        this.miniMediaActionDrawable.setBackgroundDrawable(messageDrawable);
    }

    public void setImageOverlay(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$Document tLRPC$Document, Object obj) {
        this.overlayImageView.setImage(ImageLocation.getForDocument(tLRPC$PhotoSize, tLRPC$Document), String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)}), (Drawable) null, (String) null, obj, 1);
    }

    public void setImageOverlay(String str) {
        String str2;
        ImageReceiver imageReceiver = this.overlayImageView;
        if (str != null) {
            str2 = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)});
        } else {
            str2 = null;
        }
        imageReceiver.setImage(str, str2, (Drawable) null, (String) null, -1);
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

    public float getProgress() {
        return (this.drawMiniIcon ? this.miniMediaActionDrawable : this.mediaActionDrawable).getProgress();
    }

    private void invalidateParent() {
        int dp = AndroidUtilities.dp(2.0f);
        View view = this.parent;
        RectF rectF = this.progressRect;
        int i = ((int) rectF.left) - dp;
        int i2 = ((int) rectF.top) - dp;
        int i3 = dp * 2;
        view.invalidate(i, i2, ((int) rectF.right) + i3, ((int) rectF.bottom) + i3);
    }

    public int getIcon() {
        return this.mediaActionDrawable.getCurrentIcon();
    }

    public void setIcon(int i, boolean z, boolean z2) {
        if (!z || i != this.mediaActionDrawable.getCurrentIcon()) {
            this.mediaActionDrawable.setIcon(i, z2);
            if (!z2) {
                this.parent.invalidate();
            } else {
                invalidateParent();
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
            if (z3) {
                initMiniIcons();
            }
            if (!z2) {
                this.parent.invalidate();
            } else {
                invalidateParent();
            }
        }
    }

    public void initMiniIcons() {
        if (this.miniDrawBitmap == null) {
            try {
                this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Bitmap.Config.ARGB_8888);
                this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
            } catch (Throwable unused) {
            }
        }
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

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0178  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0195  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0213  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0223  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x022e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            int r2 = r2.getCurrentIcon()
            r3 = 4
            r4 = 1065353216(0x3var_, float:1.0)
            if (r2 != r3) goto L_0x001a
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            float r2 = r2.getTransitionProgress()
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 < 0) goto L_0x001a
            return
        L_0x001a:
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            int r2 = r2.getCurrentIcon()
            org.telegram.ui.Components.MediaActionDrawable r5 = r0.mediaActionDrawable
            int r5 = r5.getPreviousIcon()
            int r6 = r0.backgroundStroke
            r7 = 3
            if (r6 == 0) goto L_0x0042
            if (r2 != r7) goto L_0x0036
            org.telegram.ui.Components.MediaActionDrawable r5 = r0.mediaActionDrawable
            float r5 = r5.getTransitionProgress()
        L_0x0033:
            float r5 = r4 - r5
            goto L_0x0064
        L_0x0036:
            if (r5 != r7) goto L_0x003f
            org.telegram.ui.Components.MediaActionDrawable r5 = r0.mediaActionDrawable
            float r5 = r5.getTransitionProgress()
            goto L_0x0064
        L_0x003f:
            r5 = 1065353216(0x3var_, float:1.0)
            goto L_0x0064
        L_0x0042:
            if (r2 == r7) goto L_0x0051
            r6 = 6
            if (r2 == r6) goto L_0x0051
            r6 = 10
            if (r2 == r6) goto L_0x0051
            r6 = 8
            if (r2 == r6) goto L_0x0051
            if (r2 != 0) goto L_0x005a
        L_0x0051:
            if (r5 != r3) goto L_0x005a
            org.telegram.ui.Components.MediaActionDrawable r5 = r0.mediaActionDrawable
            float r5 = r5.getTransitionProgress()
            goto L_0x0064
        L_0x005a:
            if (r2 == r3) goto L_0x005d
            goto L_0x003f
        L_0x005d:
            org.telegram.ui.Components.MediaActionDrawable r5 = r0.mediaActionDrawable
            float r5 = r5.getTransitionProgress()
            goto L_0x0033
        L_0x0064:
            boolean r6 = r0.isPressedMini
            if (r6 == 0) goto L_0x0093
            java.lang.String r6 = r0.iconPressedColorKey
            if (r6 == 0) goto L_0x0076
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.miniMediaActionDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            goto L_0x007d
        L_0x0076:
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.miniMediaActionDrawable
            int r7 = r0.iconPressedColor
            r6.setColor(r7)
        L_0x007d:
            java.lang.String r6 = r0.circlePressedColorKey
            if (r6 == 0) goto L_0x008b
            android.graphics.Paint r7 = r0.circleMiniPaint
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            goto L_0x00bd
        L_0x008b:
            android.graphics.Paint r6 = r0.circleMiniPaint
            int r7 = r0.circlePressedColor
            r6.setColor(r7)
            goto L_0x00bd
        L_0x0093:
            java.lang.String r6 = r0.iconColorKey
            if (r6 == 0) goto L_0x00a1
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.miniMediaActionDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            goto L_0x00a8
        L_0x00a1:
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.miniMediaActionDrawable
            int r7 = r0.iconColor
            r6.setColor(r7)
        L_0x00a8:
            java.lang.String r6 = r0.circleColorKey
            if (r6 == 0) goto L_0x00b6
            android.graphics.Paint r7 = r0.circleMiniPaint
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            goto L_0x00bd
        L_0x00b6:
            android.graphics.Paint r6 = r0.circleMiniPaint
            int r7 = r0.circleColor
            r6.setColor(r7)
        L_0x00bd:
            boolean r6 = r0.isPressed
            if (r6 == 0) goto L_0x00ff
            java.lang.String r6 = r0.iconPressedColorKey
            if (r6 == 0) goto L_0x00da
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.mediaActionDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.mediaActionDrawable
            java.lang.String r8 = r0.circlePressedColorKey
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r7.setBackColor(r8)
            goto L_0x00e9
        L_0x00da:
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.mediaActionDrawable
            int r7 = r0.iconPressedColor
            r6.setColor(r7)
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.mediaActionDrawable
            int r8 = r0.circlePressedColor
            r6.setBackColor(r8)
            r6 = r7
        L_0x00e9:
            java.lang.String r7 = r0.circlePressedColorKey
            if (r7 == 0) goto L_0x00f7
            android.graphics.Paint r8 = r0.circlePaint
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r8.setColor(r7)
            goto L_0x013c
        L_0x00f7:
            android.graphics.Paint r7 = r0.circlePaint
            int r8 = r0.circlePressedColor
            r7.setColor(r8)
            goto L_0x013c
        L_0x00ff:
            java.lang.String r6 = r0.iconColorKey
            if (r6 == 0) goto L_0x0118
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.mediaActionDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.mediaActionDrawable
            java.lang.String r8 = r0.circleColorKey
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r7.setBackColor(r8)
            goto L_0x0127
        L_0x0118:
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.mediaActionDrawable
            int r7 = r0.iconColor
            r6.setColor(r7)
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.mediaActionDrawable
            int r8 = r0.circleColor
            r6.setBackColor(r8)
            r6 = r7
        L_0x0127:
            java.lang.String r7 = r0.circleColorKey
            if (r7 == 0) goto L_0x0135
            android.graphics.Paint r8 = r0.circlePaint
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r8.setColor(r7)
            goto L_0x013c
        L_0x0135:
            android.graphics.Paint r7 = r0.circlePaint
            int r8 = r0.circleColor
            r7.setColor(r8)
        L_0x013c:
            boolean r7 = r0.drawMiniIcon
            r8 = 0
            if (r7 == 0) goto L_0x014a
            android.graphics.Canvas r7 = r0.miniDrawCanvas
            if (r7 == 0) goto L_0x014a
            android.graphics.Bitmap r7 = r0.miniDrawBitmap
            r7.eraseColor(r8)
        L_0x014a:
            android.graphics.Paint r7 = r0.circlePaint
            int r7 = r7.getAlpha()
            android.graphics.Paint r9 = r0.circlePaint
            float r7 = (float) r7
            float r7 = r7 * r5
            float r10 = r0.overrideAlpha
            float r7 = r7 * r10
            int r7 = (int) r7
            r9.setAlpha(r7)
            android.graphics.Paint r7 = r0.circleMiniPaint
            int r7 = r7.getAlpha()
            android.graphics.Paint r9 = r0.circleMiniPaint
            float r7 = (float) r7
            float r7 = r7 * r5
            float r10 = r0.overrideAlpha
            float r7 = r7 * r10
            int r7 = (int) r7
            r9.setAlpha(r7)
            boolean r7 = r0.drawMiniIcon
            if (r7 == 0) goto L_0x0195
            android.graphics.Canvas r7 = r0.miniDrawCanvas
            if (r7 == 0) goto L_0x0195
            android.graphics.RectF r7 = r0.progressRect
            float r7 = r7.width()
            r9 = 1073741824(0x40000000, float:2.0)
            float r7 = r7 / r9
            double r10 = (double) r7
            double r10 = java.lang.Math.ceil(r10)
            int r7 = (int) r10
            android.graphics.RectF r10 = r0.progressRect
            float r10 = r10.height()
            float r10 = r10 / r9
            double r9 = (double) r10
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            goto L_0x01a3
        L_0x0195:
            android.graphics.RectF r7 = r0.progressRect
            float r7 = r7.centerX()
            int r7 = (int) r7
            android.graphics.RectF r9 = r0.progressRect
            float r9 = r9.centerY()
            int r9 = (int) r9
        L_0x01a3:
            org.telegram.messenger.ImageReceiver r10 = r0.overlayImageView
            boolean r10 = r10.hasBitmapImage()
            r12 = 2
            if (r10 == 0) goto L_0x0213
            org.telegram.messenger.ImageReceiver r10 = r0.overlayImageView
            float r10 = r10.getCurrentAlpha()
            android.graphics.Paint r13 = r0.overlayPaint
            r14 = 1120403456(0x42CLASSNAME, float:100.0)
            float r14 = r14 * r10
            float r14 = r14 * r5
            float r15 = r0.overrideAlpha
            float r14 = r14 * r15
            int r14 = (int) r14
            r13.setAlpha(r14)
            int r13 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r13 < 0) goto L_0x01c9
            r6 = -1
            r11 = 0
            goto L_0x01fa
        L_0x01c9:
            int r13 = android.graphics.Color.red(r6)
            int r14 = android.graphics.Color.green(r6)
            int r15 = android.graphics.Color.blue(r6)
            int r6 = android.graphics.Color.alpha(r6)
            int r11 = 255 - r13
            float r11 = (float) r11
            float r11 = r11 * r10
            int r11 = (int) r11
            int r8 = 255 - r14
            float r8 = (float) r8
            float r8 = r8 * r10
            int r8 = (int) r8
            int r4 = 255 - r15
            float r4 = (float) r4
            float r4 = r4 * r10
            int r4 = (int) r4
            int r3 = 255 - r6
            float r3 = (float) r3
            float r3 = r3 * r10
            int r3 = (int) r3
            int r6 = r6 + r3
            int r13 = r13 + r11
            int r14 = r14 + r8
            int r15 = r15 + r4
            int r6 = android.graphics.Color.argb(r6, r13, r14, r15)
            r11 = 1
        L_0x01fa:
            org.telegram.ui.Components.MediaActionDrawable r3 = r0.mediaActionDrawable
            r3.setColor(r6)
            org.telegram.messenger.ImageReceiver r3 = r0.overlayImageView
            int r4 = r0.circleRadius
            int r6 = r7 - r4
            float r6 = (float) r6
            int r8 = r9 - r4
            float r8 = (float) r8
            int r10 = r4 * 2
            float r10 = (float) r10
            int r4 = r4 * 2
            float r4 = (float) r4
            r3.setImageCoords(r6, r8, r10, r4)
            goto L_0x0214
        L_0x0213:
            r11 = 1
        L_0x0214:
            r3 = 0
            if (r11 == 0) goto L_0x0257
            boolean r4 = r0.drawBackground
            if (r4 == 0) goto L_0x0257
            boolean r4 = r0.drawMiniIcon
            if (r4 == 0) goto L_0x022e
            android.graphics.Canvas r4 = r0.miniDrawCanvas
            if (r4 == 0) goto L_0x022e
            float r2 = (float) r7
            float r6 = (float) r9
            int r8 = r0.circleRadius
            float r8 = (float) r8
            android.graphics.Paint r10 = r0.circlePaint
            r4.drawCircle(r2, r6, r8, r10)
            goto L_0x0257
        L_0x022e:
            r4 = 4
            if (r2 != r4) goto L_0x0235
            int r2 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0257
        L_0x0235:
            int r2 = r0.backgroundStroke
            if (r2 == 0) goto L_0x024b
            float r2 = (float) r7
            float r4 = (float) r9
            int r6 = r0.circleRadius
            r8 = 1080033280(0x40600000, float:3.5)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r6 = r6 - r8
            float r6 = (float) r6
            android.graphics.Paint r8 = r0.circlePaint
            r1.drawCircle(r2, r4, r6, r8)
            goto L_0x0257
        L_0x024b:
            float r2 = (float) r7
            float r4 = (float) r9
            int r6 = r0.circleRadius
            float r6 = (float) r6
            float r6 = r6 * r5
            android.graphics.Paint r8 = r0.circlePaint
            r1.drawCircle(r2, r4, r6, r8)
        L_0x0257:
            org.telegram.messenger.ImageReceiver r2 = r0.overlayImageView
            boolean r2 = r2.hasBitmapImage()
            if (r2 == 0) goto L_0x0291
            org.telegram.messenger.ImageReceiver r2 = r0.overlayImageView
            float r4 = r0.overrideAlpha
            float r5 = r5 * r4
            r2.setAlpha(r5)
            boolean r2 = r0.drawMiniIcon
            if (r2 == 0) goto L_0x0282
            android.graphics.Canvas r2 = r0.miniDrawCanvas
            if (r2 == 0) goto L_0x0282
            org.telegram.messenger.ImageReceiver r4 = r0.overlayImageView
            r4.draw(r2)
            android.graphics.Canvas r2 = r0.miniDrawCanvas
            float r4 = (float) r7
            float r5 = (float) r9
            int r6 = r0.circleRadius
            float r6 = (float) r6
            android.graphics.Paint r8 = r0.overlayPaint
            r2.drawCircle(r4, r5, r6, r8)
            goto L_0x0291
        L_0x0282:
            org.telegram.messenger.ImageReceiver r2 = r0.overlayImageView
            r2.draw(r1)
            float r2 = (float) r7
            float r4 = (float) r9
            int r5 = r0.circleRadius
            float r5 = (float) r5
            android.graphics.Paint r6 = r0.overlayPaint
            r1.drawCircle(r2, r4, r5, r6)
        L_0x0291:
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            int r4 = r0.circleRadius
            int r5 = r7 - r4
            int r6 = r9 - r4
            int r7 = r7 + r4
            int r9 = r9 + r4
            r2.setBounds(r5, r6, r7, r9)
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            org.telegram.messenger.ImageReceiver r4 = r0.overlayImageView
            boolean r4 = r4.hasBitmapImage()
            r2.setHasOverlayImage(r4)
            boolean r2 = r0.drawMiniIcon
            if (r2 == 0) goto L_0x02bd
            android.graphics.Canvas r2 = r0.miniDrawCanvas
            if (r2 == 0) goto L_0x02b7
            org.telegram.ui.Components.MediaActionDrawable r4 = r0.mediaActionDrawable
            r4.draw(r2)
            goto L_0x02c9
        L_0x02b7:
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            r2.draw(r1)
            goto L_0x02c9
        L_0x02bd:
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            float r4 = r0.overrideAlpha
            r2.setOverrideAlpha(r4)
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            r2.draw(r1)
        L_0x02c9:
            boolean r2 = r0.drawMiniIcon
            if (r2 == 0) goto L_0x03c7
            android.graphics.RectF r2 = r0.progressRect
            float r2 = r2.width()
            r4 = 1110441984(0x42300000, float:44.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r2 = r2 - r4
            float r2 = java.lang.Math.abs(r2)
            float r4 = org.telegram.messenger.AndroidUtilities.density
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x0304
            r2 = 20
            android.graphics.RectF r4 = r0.progressRect
            float r4 = r4.centerX()
            r5 = 16
            float r5 = (float) r5
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            float r4 = r4 + r6
            android.graphics.RectF r6 = r0.progressRect
            float r6 = r6.centerY()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r6 + r5
            r12 = 0
            goto L_0x0320
        L_0x0304:
            r2 = 22
            android.graphics.RectF r4 = r0.progressRect
            float r4 = r4.centerX()
            r5 = 1099956224(0x41900000, float:18.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            float r4 = r4 + r6
            android.graphics.RectF r6 = r0.progressRect
            float r6 = r6.centerY()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r6 = r6 + r5
        L_0x0320:
            int r5 = r2 / 2
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.miniMediaActionDrawable
            int r7 = r7.getCurrentIcon()
            r8 = 4
            if (r7 == r8) goto L_0x032e
            r16 = 1065353216(0x3var_, float:1.0)
            goto L_0x033a
        L_0x032e:
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.miniMediaActionDrawable
            float r7 = r7.getTransitionProgress()
            r8 = 1065353216(0x3var_, float:1.0)
            float r7 = r8 - r7
            r16 = r7
        L_0x033a:
            int r3 = (r16 > r3 ? 1 : (r16 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x0341
            r3 = 0
            r0.drawMiniIcon = r3
        L_0x0341:
            android.graphics.Canvas r3 = r0.miniDrawCanvas
            if (r3 == 0) goto L_0x0363
            int r2 = r2 + 18
            int r2 = r2 + r12
            float r2 = (float) r2
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r7 = (float) r7
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            int r8 = r5 + 1
            float r8 = (float) r8
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            float r8 = r8 * r16
            android.graphics.Paint r9 = org.telegram.ui.ActionBar.Theme.checkboxSquare_eraserPaint
            r3.drawCircle(r7, r2, r8, r9)
            goto L_0x0376
        L_0x0363:
            android.graphics.Paint r2 = r0.miniProgressBackgroundPaint
            int r3 = r0.progressColor
            r2.setColor(r3)
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            android.graphics.Paint r3 = r0.miniProgressBackgroundPaint
            r1.drawCircle(r4, r6, r2, r3)
        L_0x0376:
            android.graphics.Canvas r2 = r0.miniDrawCanvas
            if (r2 == 0) goto L_0x038a
            android.graphics.Bitmap r2 = r0.miniDrawBitmap
            android.graphics.RectF r3 = r0.progressRect
            float r7 = r3.left
            int r7 = (int) r7
            float r7 = (float) r7
            float r3 = r3.top
            int r3 = (int) r3
            float r3 = (float) r3
            r8 = 0
            r1.drawBitmap(r2, r7, r3, r8)
        L_0x038a:
            float r2 = (float) r5
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r3 = r3 * r16
            android.graphics.Paint r5 = r0.circleMiniPaint
            r1.drawCircle(r4, r6, r3, r5)
            org.telegram.ui.Components.MediaActionDrawable r3 = r0.miniMediaActionDrawable
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r5 = (float) r5
            float r5 = r5 * r16
            float r5 = r4 - r5
            int r5 = (int) r5
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r7 = (float) r7
            float r7 = r7 * r16
            float r7 = r6 - r7
            int r7 = (int) r7
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r8 = (float) r8
            float r8 = r8 * r16
            float r4 = r4 + r8
            int r4 = (int) r4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r2 * r16
            float r6 = r6 + r2
            int r2 = (int) r6
            r3.setBounds(r5, r7, r4, r2)
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.miniMediaActionDrawable
            r2.draw(r1)
        L_0x03c7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RadialProgress2.draw(android.graphics.Canvas):void");
    }
}
