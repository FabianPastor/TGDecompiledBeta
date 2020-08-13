package org.telegram.ui.Components.voip;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Utilities;
import org.webrtc.TextureViewRenderer;

public class VoIPTextureView extends FrameLayout {
    public Bitmap cameraLastBitmap;
    public final ImageView imageView;
    final Path path = new Path();
    public final TextureViewRenderer renderer;
    float roundRadius;
    public float stubVisibleProgress;
    final Paint xRefPaint;

    public VoIPTextureView(Context context, boolean z) {
        super(context);
        new RectF();
        this.xRefPaint = new Paint(1);
        this.stubVisibleProgress = 1.0f;
        this.imageView = new ImageView(context);
        TextureViewRenderer textureViewRenderer = new TextureViewRenderer(context);
        this.renderer = textureViewRenderer;
        textureViewRenderer.setEnableHardwareScaler(true);
        addView(this.renderer);
        addView(this.imageView);
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), VoIPTextureView.this.roundRadius);
                }
            });
            setClipToOutline(true);
        } else {
            this.xRefPaint.setColor(-16777216);
            this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        if (z && this.cameraLastBitmap == null) {
            try {
                Bitmap decodeFile = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "voip_icthumb.jpg").getAbsolutePath());
                this.cameraLastBitmap = decodeFile;
                if (decodeFile == null) {
                    this.cameraLastBitmap = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg").getAbsolutePath());
                }
                this.imageView.setImageBitmap(this.cameraLastBitmap);
                this.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.roundRadius <= 0.0f || Build.VERSION.SDK_INT < 21) {
            super.dispatchDraw(canvas);
        } else {
            try {
                super.dispatchDraw(canvas);
                canvas.drawPath(this.path, this.xRefPaint);
            } catch (Exception unused) {
            }
        }
        if (this.imageView.getVisibility() == 0 && this.renderer.isFirstFrameRendered()) {
            float f = this.stubVisibleProgress - 0.10666667f;
            this.stubVisibleProgress = f;
            if (f <= 0.0f) {
                this.stubVisibleProgress = 0.0f;
                this.imageView.setVisibility(8);
                return;
            }
            invalidate();
            this.imageView.setAlpha(this.stubVisibleProgress);
        }
    }

    public void setRoundCorners(float f) {
        this.roundRadius = f;
        if (Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        } else {
            invalidate();
        }
    }

    public void saveCameraLastBitmap() {
        Bitmap bitmap = this.renderer.getBitmap(150, 150);
        if (bitmap != null && bitmap.getPixel(0, 0) != 0) {
            Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "voip_icthumb.jpg"));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (Throwable unused) {
            }
        }
    }
}
