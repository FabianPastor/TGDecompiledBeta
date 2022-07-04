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
    private static PremiumGradient instance = null;
    private static final int size = 100;
    private static final int sizeHalf = 50;
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
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getMinimumHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        this.mainGradient.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        this.mainGradient.gradientMatrix(0, 0, width, height, (float) (-width), 0.0f);
        canvas.drawRect(0.0f, 0.0f, (float) width, (float) height, this.mainGradient.paint);
        this.mainGradient.paint.setXfermode((Xfermode) null);
        return new InternalDrawable(drawable, bitmap, this.mainGradient.colors);
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
        if (this.mainGradient.colors[0] == internalDrawable.colors[0] && this.mainGradient.colors[1] == internalDrawable.colors[1] && this.mainGradient.colors[2] == internalDrawable.colors[2] && this.mainGradient.colors[3] == internalDrawable.colors[3]) {
            return internalDrawable;
        }
        return createGradientDrawable(internalDrawable.originDrawable);
    }

    public void updateMainGradientMatrix(int x, int y, int width, int height, float xOffset, float yOffset) {
        this.mainGradient.gradientMatrix(x, y, width, height, xOffset, yOffset);
    }

    public static class InternalDrawable extends BitmapDrawable {
        public int[] colors;
        Drawable originDrawable;

        public InternalDrawable(Drawable originDrawable2, Bitmap bitmap, int[] colors2) {
            super(ApplicationLoader.applicationContext.getResources(), bitmap);
            this.originDrawable = originDrawable2;
            int[] iArr = new int[colors2.length];
            this.colors = iArr;
            System.arraycopy(colors2, 0, iArr, 0, colors2.length);
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public void setColorFilter(int color, PorterDuff.Mode mode) {
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

        public GradientTools(String colorKey12, String colorKey22, String colorKey32, String colorKey42) {
            this.colorKey1 = colorKey12;
            this.colorKey2 = colorKey22;
            this.colorKey3 = colorKey32;
            this.colorKey4 = colorKey42;
        }

        public void gradientMatrix(int x, int y, int x12, int y12, float xOffset, float yOffset) {
            chekColors();
            if (this.exactly) {
                this.matrix.reset();
                this.matrix.postScale(((float) (x12 - x)) / 100.0f, ((float) (y12 - y)) / 100.0f, this.cx * 100.0f, this.cy * 100.0f);
                this.matrix.postTranslate(xOffset, yOffset);
                this.shader.setLocalMatrix(this.matrix);
                return;
            }
            int height = y12 - y;
            int gradientHeight = height + height;
            chekColors();
            this.matrix.reset();
            this.matrix.postScale(((float) (x12 - x)) / 100.0f, ((float) gradientHeight) / 100.0f, 75.0f, 50.0f);
            this.matrix.postTranslate(xOffset, ((float) (-gradientHeight)) + yOffset);
            this.shader.setLocalMatrix(this.matrix);
        }

        /* access modifiers changed from: private */
        public void chekColors() {
            int c1 = Theme.getColor(this.colorKey1);
            int c2 = Theme.getColor(this.colorKey2);
            String str = this.colorKey3;
            int c3 = str == null ? 0 : Theme.getColor(str);
            String str2 = this.colorKey4;
            int c4 = str2 == null ? 0 : Theme.getColor(str2);
            int[] iArr = this.colors;
            if (iArr[0] != c1 || iArr[1] != c2 || iArr[2] != c3 || iArr[3] != c4) {
                iArr[0] = c1;
                iArr[1] = c2;
                iArr[2] = c3;
                iArr[3] = c4;
                if (c3 == 0) {
                    float f = this.x1 * 100.0f;
                    float f2 = this.y1 * 100.0f;
                    float f3 = this.x2 * 100.0f;
                    float f4 = this.y2 * 100.0f;
                    int[] iArr2 = this.colors;
                    this.shader = new LinearGradient(f, f2, f3, f4, new int[]{iArr2[0], iArr2[1]}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                } else if (c4 == 0) {
                    float f5 = this.x1 * 100.0f;
                    float f6 = this.y1 * 100.0f;
                    float f7 = this.x2 * 100.0f;
                    float f8 = this.y2 * 100.0f;
                    int[] iArr3 = this.colors;
                    this.shader = new LinearGradient(f5, f6, f7, f8, new int[]{iArr3[0], iArr3[1], iArr3[2]}, new float[]{0.0f, 0.5f, 1.0f}, Shader.TileMode.CLAMP);
                } else {
                    float f9 = 100.0f * this.y2;
                    int[] iArr4 = this.colors;
                    this.shader = new LinearGradient(this.x1 * 100.0f, this.y1 * 100.0f, this.x2 * 100.0f, f9, new int[]{iArr4[0], iArr4[1], iArr4[2], iArr4[3]}, new float[]{0.0f, 0.5f, 0.78f, 1.0f}, Shader.TileMode.CLAMP);
                }
                this.shader.setLocalMatrix(this.matrix);
                this.paint.setShader(this.shader);
            }
        }

        public void gradientMatrixLinear(float totalHeight, float offset) {
            chekColors();
            this.matrix.reset();
            this.matrix.postScale(1.0f, totalHeight / 100.0f, 0.0f, 0.0f);
            this.matrix.postTranslate(0.0f, offset);
            this.shader.setLocalMatrix(this.matrix);
        }
    }
}
