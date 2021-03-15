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
    private float circleCheckProgress = 1.0f;
    private int circleColor;
    private String circleColorKey;
    private String circleCrossfadeColorKey;
    private float circleCrossfadeColorProgress;
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
    private float miniIconScale = 1.0f;
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

    public void setCircleCrossfadeColor(String str, float f, float f2) {
        this.circleCrossfadeColorKey = str;
        this.circleCrossfadeColorProgress = f;
        this.circleCheckProgress = f2;
        this.miniIconScale = 1.0f;
        if (str != null) {
            initMiniIcons();
        }
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

    public int getMiniIcon() {
        return this.miniMediaActionDrawable.getCurrentIcon();
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

    public void setMiniIconScale(float f) {
        this.miniIconScale = f;
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

    /* JADX WARNING: Removed duplicated region for block: B:107:0x027a  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01d4  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x023b  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x024e  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0265  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r17) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
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
            if (r6 == 0) goto L_0x0097
            java.lang.String r6 = r0.circleCrossfadeColorKey
            if (r6 != 0) goto L_0x0097
            java.lang.String r6 = r0.iconPressedColorKey
            if (r6 == 0) goto L_0x007a
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.miniMediaActionDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            goto L_0x0081
        L_0x007a:
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.miniMediaActionDrawable
            int r7 = r0.iconPressedColor
            r6.setColor(r7)
        L_0x0081:
            java.lang.String r6 = r0.circlePressedColorKey
            if (r6 == 0) goto L_0x008f
            android.graphics.Paint r7 = r0.circleMiniPaint
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            goto L_0x00dd
        L_0x008f:
            android.graphics.Paint r6 = r0.circleMiniPaint
            int r7 = r0.circlePressedColor
            r6.setColor(r7)
            goto L_0x00dd
        L_0x0097:
            java.lang.String r6 = r0.iconColorKey
            if (r6 == 0) goto L_0x00a5
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.miniMediaActionDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            goto L_0x00ac
        L_0x00a5:
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.miniMediaActionDrawable
            int r7 = r0.iconColor
            r6.setColor(r7)
        L_0x00ac:
            java.lang.String r6 = r0.circleColorKey
            if (r6 == 0) goto L_0x00d6
            java.lang.String r7 = r0.circleCrossfadeColorKey
            if (r7 == 0) goto L_0x00cc
            android.graphics.Paint r7 = r0.circleMiniPaint
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            java.lang.String r8 = r0.circleCrossfadeColorKey
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            float r9 = r0.circleCrossfadeColorProgress
            float r10 = r0.circleCheckProgress
            int r6 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r6, r8, r9, r10)
            r7.setColor(r6)
            goto L_0x00dd
        L_0x00cc:
            android.graphics.Paint r7 = r0.circleMiniPaint
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            goto L_0x00dd
        L_0x00d6:
            android.graphics.Paint r6 = r0.circleMiniPaint
            int r7 = r0.circleColor
            r6.setColor(r7)
        L_0x00dd:
            boolean r6 = r0.isPressed
            if (r6 == 0) goto L_0x011f
            java.lang.String r6 = r0.iconPressedColorKey
            if (r6 == 0) goto L_0x00fa
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.mediaActionDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.mediaActionDrawable
            java.lang.String r8 = r0.circlePressedColorKey
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r7.setBackColor(r8)
            goto L_0x0109
        L_0x00fa:
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.mediaActionDrawable
            int r7 = r0.iconPressedColor
            r6.setColor(r7)
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.mediaActionDrawable
            int r8 = r0.circlePressedColor
            r6.setBackColor(r8)
            r6 = r7
        L_0x0109:
            java.lang.String r7 = r0.circlePressedColorKey
            if (r7 == 0) goto L_0x0117
            android.graphics.Paint r8 = r0.circlePaint
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r8.setColor(r7)
            goto L_0x015c
        L_0x0117:
            android.graphics.Paint r7 = r0.circlePaint
            int r8 = r0.circlePressedColor
            r7.setColor(r8)
            goto L_0x015c
        L_0x011f:
            java.lang.String r6 = r0.iconColorKey
            if (r6 == 0) goto L_0x0138
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.mediaActionDrawable
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r7.setColor(r6)
            org.telegram.ui.Components.MediaActionDrawable r7 = r0.mediaActionDrawable
            java.lang.String r8 = r0.circleColorKey
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r7.setBackColor(r8)
            goto L_0x0147
        L_0x0138:
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.mediaActionDrawable
            int r7 = r0.iconColor
            r6.setColor(r7)
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.mediaActionDrawable
            int r8 = r0.circleColor
            r6.setBackColor(r8)
            r6 = r7
        L_0x0147:
            java.lang.String r7 = r0.circleColorKey
            if (r7 == 0) goto L_0x0155
            android.graphics.Paint r8 = r0.circlePaint
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r8.setColor(r7)
            goto L_0x015c
        L_0x0155:
            android.graphics.Paint r7 = r0.circlePaint
            int r8 = r0.circleColor
            r7.setColor(r8)
        L_0x015c:
            boolean r7 = r0.drawMiniIcon
            r8 = 0
            if (r7 != 0) goto L_0x0165
            java.lang.String r7 = r0.circleCrossfadeColorKey
            if (r7 == 0) goto L_0x016e
        L_0x0165:
            android.graphics.Canvas r7 = r0.miniDrawCanvas
            if (r7 == 0) goto L_0x016e
            android.graphics.Bitmap r7 = r0.miniDrawBitmap
            r7.eraseColor(r8)
        L_0x016e:
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
            if (r7 != 0) goto L_0x019c
            java.lang.String r7 = r0.circleCrossfadeColorKey
            if (r7 == 0) goto L_0x01bd
        L_0x019c:
            android.graphics.Canvas r7 = r0.miniDrawCanvas
            if (r7 == 0) goto L_0x01bd
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
            goto L_0x01cb
        L_0x01bd:
            android.graphics.RectF r7 = r0.progressRect
            float r7 = r7.centerX()
            int r7 = (int) r7
            android.graphics.RectF r9 = r0.progressRect
            float r9 = r9.centerY()
            int r9 = (int) r9
        L_0x01cb:
            org.telegram.messenger.ImageReceiver r10 = r0.overlayImageView
            boolean r10 = r10.hasBitmapImage()
            r12 = 2
            if (r10 == 0) goto L_0x023b
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
            if (r13 < 0) goto L_0x01f1
            r6 = -1
            r11 = 0
            goto L_0x0222
        L_0x01f1:
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
            int r3 = 255 - r15
            float r3 = (float) r3
            float r3 = r3 * r10
            int r3 = (int) r3
            int r4 = 255 - r6
            float r4 = (float) r4
            float r4 = r4 * r10
            int r4 = (int) r4
            int r6 = r6 + r4
            int r13 = r13 + r11
            int r14 = r14 + r8
            int r15 = r15 + r3
            int r6 = android.graphics.Color.argb(r6, r13, r14, r15)
            r11 = 1
        L_0x0222:
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
            goto L_0x023c
        L_0x023b:
            r11 = 1
        L_0x023c:
            android.graphics.Canvas r3 = r0.miniDrawCanvas
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r3 == 0) goto L_0x0265
            java.lang.String r6 = r0.circleCrossfadeColorKey
            if (r6 == 0) goto L_0x0265
            float r6 = r0.circleCheckProgress
            r8 = 1065353216(0x3var_, float:1.0)
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 == 0) goto L_0x0265
            int r3 = r3.save()
            r6 = 1036831949(0x3dcccccd, float:0.1)
            float r10 = r0.circleCheckProgress
            float r10 = r8 - r10
            float r10 = r10 * r6
            float r6 = r8 - r10
            android.graphics.Canvas r8 = r0.miniDrawCanvas
            float r10 = (float) r7
            float r13 = (float) r9
            r8.scale(r6, r6, r10, r13)
            goto L_0x0267
        L_0x0265:
            r3 = -2147483648(0xfffffffvar_, float:-0.0)
        L_0x0267:
            r6 = 0
            if (r11 == 0) goto L_0x02ae
            boolean r8 = r0.drawBackground
            if (r8 == 0) goto L_0x02ae
            boolean r8 = r0.drawMiniIcon
            if (r8 != 0) goto L_0x0276
            java.lang.String r8 = r0.circleCrossfadeColorKey
            if (r8 == 0) goto L_0x0285
        L_0x0276:
            android.graphics.Canvas r8 = r0.miniDrawCanvas
            if (r8 == 0) goto L_0x0285
            float r2 = (float) r7
            float r10 = (float) r9
            int r11 = r0.circleRadius
            float r11 = (float) r11
            android.graphics.Paint r13 = r0.circlePaint
            r8.drawCircle(r2, r10, r11, r13)
            goto L_0x02ae
        L_0x0285:
            r8 = 4
            if (r2 != r8) goto L_0x028c
            int r2 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r2 == 0) goto L_0x02ae
        L_0x028c:
            int r2 = r0.backgroundStroke
            if (r2 == 0) goto L_0x02a2
            float r2 = (float) r7
            float r8 = (float) r9
            int r10 = r0.circleRadius
            r11 = 1080033280(0x40600000, float:3.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 - r11
            float r10 = (float) r10
            android.graphics.Paint r11 = r0.circlePaint
            r1.drawCircle(r2, r8, r10, r11)
            goto L_0x02ae
        L_0x02a2:
            float r2 = (float) r7
            float r8 = (float) r9
            int r10 = r0.circleRadius
            float r10 = (float) r10
            float r10 = r10 * r5
            android.graphics.Paint r11 = r0.circlePaint
            r1.drawCircle(r2, r8, r10, r11)
        L_0x02ae:
            org.telegram.messenger.ImageReceiver r2 = r0.overlayImageView
            boolean r2 = r2.hasBitmapImage()
            if (r2 == 0) goto L_0x02ec
            org.telegram.messenger.ImageReceiver r2 = r0.overlayImageView
            float r8 = r0.overrideAlpha
            float r5 = r5 * r8
            r2.setAlpha(r5)
            boolean r2 = r0.drawMiniIcon
            if (r2 != 0) goto L_0x02c7
            java.lang.String r2 = r0.circleCrossfadeColorKey
            if (r2 == 0) goto L_0x02dd
        L_0x02c7:
            android.graphics.Canvas r2 = r0.miniDrawCanvas
            if (r2 == 0) goto L_0x02dd
            org.telegram.messenger.ImageReceiver r5 = r0.overlayImageView
            r5.draw(r2)
            android.graphics.Canvas r2 = r0.miniDrawCanvas
            float r5 = (float) r7
            float r8 = (float) r9
            int r10 = r0.circleRadius
            float r10 = (float) r10
            android.graphics.Paint r11 = r0.overlayPaint
            r2.drawCircle(r5, r8, r10, r11)
            goto L_0x02ec
        L_0x02dd:
            org.telegram.messenger.ImageReceiver r2 = r0.overlayImageView
            r2.draw(r1)
            float r2 = (float) r7
            float r5 = (float) r9
            int r8 = r0.circleRadius
            float r8 = (float) r8
            android.graphics.Paint r10 = r0.overlayPaint
            r1.drawCircle(r2, r5, r8, r10)
        L_0x02ec:
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            int r5 = r0.circleRadius
            int r8 = r7 - r5
            int r10 = r9 - r5
            int r7 = r7 + r5
            int r9 = r9 + r5
            r2.setBounds(r8, r10, r7, r9)
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            org.telegram.messenger.ImageReceiver r5 = r0.overlayImageView
            boolean r5 = r5.hasBitmapImage()
            r2.setHasOverlayImage(r5)
            boolean r2 = r0.drawMiniIcon
            if (r2 != 0) goto L_0x031a
            java.lang.String r2 = r0.circleCrossfadeColorKey
            if (r2 == 0) goto L_0x030d
            goto L_0x031a
        L_0x030d:
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            float r5 = r0.overrideAlpha
            r2.setOverrideAlpha(r5)
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            r2.draw(r1)
            goto L_0x0329
        L_0x031a:
            android.graphics.Canvas r2 = r0.miniDrawCanvas
            if (r2 == 0) goto L_0x0324
            org.telegram.ui.Components.MediaActionDrawable r5 = r0.mediaActionDrawable
            r5.draw(r2)
            goto L_0x0329
        L_0x0324:
            org.telegram.ui.Components.MediaActionDrawable r2 = r0.mediaActionDrawable
            r2.draw(r1)
        L_0x0329:
            if (r3 == r4) goto L_0x0332
            android.graphics.Canvas r2 = r0.miniDrawCanvas
            if (r2 == 0) goto L_0x0332
            r2.restoreToCount(r3)
        L_0x0332:
            boolean r2 = r0.drawMiniIcon
            if (r2 != 0) goto L_0x033a
            java.lang.String r2 = r0.circleCrossfadeColorKey
            if (r2 == 0) goto L_0x0465
        L_0x033a:
            android.graphics.RectF r2 = r0.progressRect
            float r2 = r2.width()
            r3 = 1110441984(0x42300000, float:44.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r2 = r2 - r3
            float r2 = java.lang.Math.abs(r2)
            float r3 = org.telegram.messenger.AndroidUtilities.density
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x0371
            r2 = 20
            android.graphics.RectF r3 = r0.progressRect
            float r3 = r3.centerX()
            r5 = 16
            float r5 = (float) r5
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r7 = (float) r7
            float r3 = r3 + r7
            android.graphics.RectF r7 = r0.progressRect
            float r7 = r7.centerY()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r7 = r7 + r5
            r12 = 0
            goto L_0x038d
        L_0x0371:
            r2 = 22
            android.graphics.RectF r3 = r0.progressRect
            float r3 = r3.centerX()
            r5 = 1099956224(0x41900000, float:18.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r7 = (float) r7
            float r3 = r3 + r7
            android.graphics.RectF r7 = r0.progressRect
            float r7 = r7.centerY()
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r7 = r7 + r5
        L_0x038d:
            int r5 = r2 / 2
            boolean r8 = r0.drawMiniIcon
            if (r8 == 0) goto L_0x03b1
            org.telegram.ui.Components.MediaActionDrawable r8 = r0.miniMediaActionDrawable
            int r8 = r8.getCurrentIcon()
            r9 = 4
            if (r8 == r9) goto L_0x039f
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x03a9
        L_0x039f:
            org.telegram.ui.Components.MediaActionDrawable r8 = r0.miniMediaActionDrawable
            float r8 = r8.getTransitionProgress()
            r9 = 1065353216(0x3var_, float:1.0)
            float r8 = r9 - r8
        L_0x03a9:
            int r6 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x03b3
            r6 = 0
            r0.drawMiniIcon = r6
            goto L_0x03b3
        L_0x03b1:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x03b3:
            android.graphics.Canvas r6 = r0.miniDrawCanvas
            if (r6 == 0) goto L_0x03d9
            int r2 = r2 + 18
            int r2 = r2 + r12
            float r2 = (float) r2
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r9 = (float) r9
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            int r10 = r5 + 1
            float r10 = (float) r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r10 = r10 * r8
            float r11 = r0.miniIconScale
            float r10 = r10 * r11
            android.graphics.Paint r11 = org.telegram.ui.ActionBar.Theme.checkboxSquare_eraserPaint
            r6.drawCircle(r9, r2, r10, r11)
            goto L_0x03ec
        L_0x03d9:
            android.graphics.Paint r2 = r0.miniProgressBackgroundPaint
            int r6 = r0.progressColor
            r2.setColor(r6)
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            android.graphics.Paint r6 = r0.miniProgressBackgroundPaint
            r1.drawCircle(r3, r7, r2, r6)
        L_0x03ec:
            android.graphics.Canvas r2 = r0.miniDrawCanvas
            if (r2 == 0) goto L_0x0400
            android.graphics.Bitmap r2 = r0.miniDrawBitmap
            android.graphics.RectF r6 = r0.progressRect
            float r9 = r6.left
            int r9 = (int) r9
            float r9 = (float) r9
            float r6 = r6.top
            int r6 = (int) r6
            float r6 = (float) r6
            r10 = 0
            r1.drawBitmap(r2, r9, r6, r10)
        L_0x0400:
            float r2 = r0.miniIconScale
            r6 = 1065353216(0x3var_, float:1.0)
            int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r2 >= 0) goto L_0x0412
            int r2 = r17.save()
            float r9 = r0.miniIconScale
            r1.scale(r9, r9, r3, r7)
            goto L_0x0414
        L_0x0412:
            r2 = -2147483648(0xfffffffvar_, float:-0.0)
        L_0x0414:
            float r5 = (float) r5
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r9 = (float) r9
            float r9 = r9 * r8
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r10 = (float) r10
            float r11 = r0.circleCheckProgress
            float r6 = r6 - r11
            float r10 = r10 * r6
            float r9 = r9 + r10
            android.graphics.Paint r6 = r0.circleMiniPaint
            r1.drawCircle(r3, r7, r9, r6)
            boolean r6 = r0.drawMiniIcon
            if (r6 == 0) goto L_0x0460
            org.telegram.ui.Components.MediaActionDrawable r6 = r0.miniMediaActionDrawable
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r9 = (float) r9
            float r9 = r9 * r8
            float r9 = r3 - r9
            int r9 = (int) r9
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r10 = (float) r10
            float r10 = r10 * r8
            float r10 = r7 - r10
            int r10 = (int) r10
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r11 = (float) r11
            float r11 = r11 * r8
            float r3 = r3 + r11
            int r3 = (int) r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r5 = r5 * r8
            float r7 = r7 + r5
            int r5 = (int) r7
            r6.setBounds(r9, r10, r3, r5)
            org.telegram.ui.Components.MediaActionDrawable r3 = r0.miniMediaActionDrawable
            r3.draw(r1)
        L_0x0460:
            if (r2 == r4) goto L_0x0465
            r1.restoreToCount(r2)
        L_0x0465:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RadialProgress2.draw(android.graphics.Canvas):void");
    }

    public String getCircleColorKey() {
        return this.circleColorKey;
    }
}
