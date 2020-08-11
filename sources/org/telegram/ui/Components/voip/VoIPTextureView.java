package org.telegram.ui.Components.voip;

import android.annotation.TargetApi;
import android.content.Context;
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
import org.telegram.ui.Components.BackupImageView;
import org.webrtc.TextureViewRenderer;

public class VoIPTextureView extends FrameLayout {
    public final BackupImageView imageView;
    final Path path = new Path();
    public final TextureViewRenderer renderer;
    float roundRadius;
    final Paint xRefPaint;

    public VoIPTextureView(Context context) {
        super(context);
        new RectF();
        this.xRefPaint = new Paint(1);
        this.imageView = new BackupImageView(context);
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
            return;
        }
        this.xRefPaint.setColor(-16777216);
        this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.roundRadius <= 0.0f || Build.VERSION.SDK_INT < 21) {
            super.dispatchDraw(canvas);
            return;
        }
        try {
            super.dispatchDraw(canvas);
            canvas.drawPath(this.path, this.xRefPaint);
        } catch (Exception unused) {
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
}
