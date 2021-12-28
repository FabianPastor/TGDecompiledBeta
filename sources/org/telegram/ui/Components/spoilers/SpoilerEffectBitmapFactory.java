package org.telegram.ui.Components.spoilers;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;

public class SpoilerEffectBitmapFactory {
    private static SpoilerEffectBitmapFactory factory;
    Bitmap backgroundBitmap;
    Bitmap bufferBitmap;
    DispatchQueue dispatchQueue = new DispatchQueue("SpoilerEffectBitmapFactory");
    boolean isRunning;
    long lastUpdateTime;
    private Bitmap shaderBitmap;
    Canvas shaderCanvas;
    Paint shaderPaint;
    ArrayList<SpoilerEffect> shaderSpoilerEffects;

    public SpoilerEffectBitmapFactory() {
        new Matrix();
    }

    public static SpoilerEffectBitmapFactory getInstance() {
        if (factory == null) {
            factory = new SpoilerEffectBitmapFactory();
        }
        return factory;
    }

    /* access modifiers changed from: package-private */
    public Paint getPaint() {
        if (this.shaderBitmap == null) {
            this.shaderBitmap = Bitmap.createBitmap(AndroidUtilities.dp(200.0f), AndroidUtilities.dp(200.0f), Bitmap.Config.ARGB_8888);
            this.shaderCanvas = new Canvas(this.shaderBitmap);
            this.shaderPaint = new Paint();
            this.shaderSpoilerEffects = new ArrayList<>(100);
            Paint paint = this.shaderPaint;
            Bitmap bitmap = this.shaderBitmap;
            Shader.TileMode tileMode = Shader.TileMode.REPEAT;
            paint.setShader(new BitmapShader(bitmap, tileMode, tileMode));
            for (int i = 0; i < 10; i++) {
                for (int i2 = 0; i2 < 10; i2++) {
                    SpoilerEffect spoilerEffect = new SpoilerEffect();
                    float f = (float) (i2 * 20);
                    spoilerEffect.setBounds(AndroidUtilities.dp(20.0f) * i, AndroidUtilities.dp(f) - AndroidUtilities.dp(2.0f), (AndroidUtilities.dp(20.0f) * i) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(f) + AndroidUtilities.dp(22.0f));
                    spoilerEffect.drawPoints = true;
                    int length = SpoilerEffect.ALPHAS.length;
                    int[] iArr = new int[2];
                    iArr[1] = 120;
                    iArr[0] = length;
                    spoilerEffect.particlePoints = (float[][]) Array.newInstance(float.class, iArr);
                    spoilerEffect.setMaxParticlesCount(60);
                    spoilerEffect.setColor(-1);
                    this.shaderSpoilerEffects.add(spoilerEffect);
                }
            }
            for (int i3 = 0; i3 < 10; i3++) {
                for (int i4 = 0; i4 < 10; i4++) {
                    this.shaderSpoilerEffects.get((i3 * 10) + i4).draw(this.shaderCanvas);
                }
            }
            Paint paint2 = this.shaderPaint;
            Bitmap bitmap2 = this.shaderBitmap;
            Shader.TileMode tileMode2 = Shader.TileMode.REPEAT;
            paint2.setShader(new BitmapShader(bitmap2, tileMode2, tileMode2));
            this.lastUpdateTime = System.currentTimeMillis();
        }
        return this.shaderPaint;
    }

    public void checkUpdate() {
        if (System.currentTimeMillis() - this.lastUpdateTime > 32 && !this.isRunning) {
            this.lastUpdateTime = System.currentTimeMillis();
            this.isRunning = true;
            this.dispatchQueue.postRunnable(new SpoilerEffectBitmapFactory$$ExternalSyntheticLambda0(this, this.bufferBitmap));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUpdate$1(Bitmap bitmap) {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(AndroidUtilities.dp(200.0f), AndroidUtilities.dp(200.0f), Bitmap.Config.ARGB_8888);
        }
        Bitmap bitmap2 = this.backgroundBitmap;
        if (bitmap2 == null) {
            this.backgroundBitmap = Bitmap.createBitmap(AndroidUtilities.dp(200.0f), AndroidUtilities.dp(200.0f), Bitmap.Config.ARGB_8888);
        } else {
            bitmap2.eraseColor(0);
        }
        Canvas canvas = new Canvas(bitmap);
        Canvas canvas2 = new Canvas(this.backgroundBitmap);
        for (int i = 0; i < 10; i++) {
            for (int i2 = 0; i2 < 10; i2++) {
                this.shaderSpoilerEffects.get((i * 10) + i2).draw(canvas2);
            }
        }
        bitmap.eraseColor(0);
        canvas.drawBitmap(this.backgroundBitmap, 0.0f, 0.0f, (Paint) null);
        AndroidUtilities.runOnUIThread(new SpoilerEffectBitmapFactory$$ExternalSyntheticLambda1(this, bitmap));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUpdate$0(Bitmap bitmap) {
        this.bufferBitmap = this.shaderBitmap;
        this.shaderBitmap = bitmap;
        Paint paint = this.shaderPaint;
        Bitmap bitmap2 = this.shaderBitmap;
        Shader.TileMode tileMode = Shader.TileMode.REPEAT;
        paint.setShader(new BitmapShader(bitmap2, tileMode, tileMode));
        this.isRunning = false;
    }
}
