package org.telegram.ui.Components.Premium;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class PremiumGradient {
    private static PremiumGradient instance;
    private int lastStarColor;
    Paint lockedPremiumPaint;
    private final GradientTools mainGradient;
    private final Paint mainGradientPaint;
    public Drawable premiumStarColoredDrawable = ContextCompat.getDrawable(ApplicationLoader.applicationContext, NUM).mutate();
    public Drawable premiumStarDrawableMini = ContextCompat.getDrawable(ApplicationLoader.applicationContext, NUM).mutate();
    public InternalDrawable premiumStarMenuDrawable = createGradientDrawable(ContextCompat.getDrawable(ApplicationLoader.applicationContext, NUM));
    public InternalDrawable premiumStarMenuDrawable2 = createGradientDrawable(ContextCompat.getDrawable(ApplicationLoader.applicationContext, NUM));

    public static PremiumGradient getInstance() {
        if (instance == null) {
            instance = new PremiumGradient();
        }
        return instance;
    }

    private PremiumGradient() {
        GradientTools gradientTools = new GradientTools("premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4");
        this.mainGradient = gradientTools;
        this.mainGradientPaint = gradientTools.paint;
        gradientTools.chekColors();
        checkIconColors();
    }

    public InternalDrawable createGradientDrawable(Drawable drawable) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int minimumHeight = drawable.getMinimumHeight();
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, minimumHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, intrinsicWidth, minimumHeight);
        drawable.draw(canvas);
        this.mainGradient.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        this.mainGradient.gradientMatrix(0, 0, intrinsicWidth, minimumHeight, (float) (-intrinsicWidth), 0.0f);
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, 0.0f, (float) intrinsicWidth, (float) minimumHeight, this.mainGradient.paint);
        this.mainGradient.paint.setXfermode((Xfermode) null);
        return new InternalDrawable(drawable, createBitmap, this.mainGradient.colors);
    }

    public void checkIconColors() {
        if (Theme.getColor("chats_verifiedBackground") != this.lastStarColor) {
            this.lastStarColor = Theme.getColor("chats_verifiedBackground");
            this.premiumStarDrawableMini.setColorFilter(new PorterDuffColorFilter(this.lastStarColor, PorterDuff.Mode.MULTIPLY));
        }
        this.premiumStarMenuDrawable = checkColors(this.premiumStarMenuDrawable);
        this.premiumStarMenuDrawable2 = checkColors(this.premiumStarMenuDrawable2);
    }

    private InternalDrawable checkColors(InternalDrawable internalDrawable) {
        int[] iArr = this.mainGradient.colors;
        int i = iArr[0];
        int[] iArr2 = internalDrawable.colors;
        if (i == iArr2[0] && iArr[1] == iArr2[1] && iArr[2] == iArr2[2] && iArr[3] == iArr2[3]) {
            return internalDrawable;
        }
        return createGradientDrawable(internalDrawable.originDrawable);
    }

    public void updateMainGradientMatrix(int i, int i2, int i3, int i4, float f, float f2) {
        this.mainGradient.gradientMatrix(i, i2, i3, i4, f, f2);
    }

    public static class InternalDrawable extends BitmapDrawable {
        public int[] colors;
        Drawable originDrawable;

        public void setColorFilter(int i, PorterDuff.Mode mode) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public InternalDrawable(Drawable drawable, Bitmap bitmap, int[] iArr) {
            super(ApplicationLoader.applicationContext.getResources(), bitmap);
            this.originDrawable = drawable;
            int[] iArr2 = new int[iArr.length];
            this.colors = iArr2;
            System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
        }
    }

    public Paint getMainGradientPaint() {
        if (!MessagesController.getInstance(UserConfig.selectedAccount).premiumLocked) {
            return this.mainGradientPaint;
        }
        if (this.lockedPremiumPaint == null) {
            this.lockedPremiumPaint = new Paint(1);
        }
        this.lockedPremiumPaint.setColor(Theme.getColor("featuredStickers_addButton"));
        return this.lockedPremiumPaint;
    }

    public static class GradientTools {
        final String colorKey1;
        final String colorKey2;
        final String colorKey3;
        final String colorKey4;
        final int[] colors = new int[4];
        public float cx = 0.5f;
        public float cy = 0.5f;
        public boolean exactly;
        Matrix matrix = new Matrix();
        public final Paint paint = new Paint(1);
        Shader shader;
        public float x1 = 0.0f;
        public float x2 = 1.5f;
        public float y1 = 1.0f;
        public float y2 = 0.0f;

        public GradientTools(String str, String str2, String str3, String str4) {
            this.colorKey1 = str;
            this.colorKey2 = str2;
            this.colorKey3 = str3;
            this.colorKey4 = str4;
        }

        public void gradientMatrix(int i, int i2, int i3, int i4, float f, float f2) {
            chekColors();
            if (this.exactly) {
                this.matrix.reset();
                this.matrix.postScale(((float) (i3 - i)) / 100.0f, ((float) (i4 - i2)) / 100.0f, this.cx * 100.0f, this.cy * 100.0f);
                this.matrix.postTranslate(f, f2);
                this.shader.setLocalMatrix(this.matrix);
                return;
            }
            int i5 = i4 - i2;
            int i6 = i5 + i5;
            chekColors();
            this.matrix.reset();
            this.matrix.postScale(((float) (i3 - i)) / 100.0f, ((float) i6) / 100.0f, 75.0f, 50.0f);
            this.matrix.postTranslate(f, ((float) (-i6)) + f2);
            this.shader.setLocalMatrix(this.matrix);
        }

        /* access modifiers changed from: private */
        public void chekColors() {
            int color = Theme.getColor(this.colorKey1);
            int color2 = Theme.getColor(this.colorKey2);
            String str = this.colorKey3;
            int color3 = str == null ? 0 : Theme.getColor(str);
            String str2 = this.colorKey4;
            int color4 = str2 == null ? 0 : Theme.getColor(str2);
            int[] iArr = this.colors;
            if (iArr[0] != color || iArr[1] != color2 || iArr[2] != color3 || iArr[3] != color4) {
                iArr[0] = color;
                iArr[1] = color2;
                iArr[2] = color3;
                iArr[3] = color4;
                if (color3 == 0) {
                    float f = this.x1 * 100.0f;
                    float f2 = this.y1 * 100.0f;
                    float f3 = this.x2 * 100.0f;
                    float f4 = this.y2 * 100.0f;
                    int[] iArr2 = this.colors;
                    this.shader = new LinearGradient(f, f2, f3, f4, new int[]{iArr2[0], iArr2[1]}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                } else if (color4 == 0) {
                    float f5 = this.x1 * 100.0f;
                    float f6 = this.y1 * 100.0f;
                    float f7 = this.x2 * 100.0f;
                    float f8 = 100.0f * this.y2;
                    int[] iArr3 = this.colors;
                    this.shader = new LinearGradient(f5, f6, f7, f8, new int[]{iArr3[0], iArr3[1], iArr3[2]}, new float[]{0.0f, 0.5f, 1.0f}, Shader.TileMode.CLAMP);
                } else {
                    float f9 = this.x1 * 100.0f;
                    float var_ = this.y1 * 100.0f;
                    float var_ = this.x2 * 100.0f;
                    float var_ = this.y2 * 100.0f;
                    int[] iArr4 = this.colors;
                    this.shader = new LinearGradient(f9, var_, var_, var_, new int[]{iArr4[0], iArr4[1], iArr4[2], iArr4[3]}, new float[]{0.0f, 0.5f, 0.78f, 1.0f}, Shader.TileMode.CLAMP);
                }
                this.shader.setLocalMatrix(this.matrix);
                this.paint.setShader(this.shader);
            }
        }

        public void gradientMatrixLinear(float f, float f2) {
            chekColors();
            this.matrix.reset();
            this.matrix.postScale(1.0f, f / 100.0f, 0.0f, 0.0f);
            this.matrix.postTranslate(0.0f, f2);
            this.shader.setLocalMatrix(this.matrix);
        }
    }
}
