package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class RoundVideoPlayingDrawable extends Drawable {
    private View parentView;
    private final Theme.ResourcesProvider resourcesProvider;
    private long lastUpdateTime = 0;
    private boolean started = false;
    private Paint paint = new Paint(1);
    private float progress1 = 0.47f;
    private float progress2 = 0.0f;
    private float progress3 = 0.32f;
    private int progress1Direction = 1;
    private int progress2Direction = 1;
    private int progress3Direction = 1;
    int alpha = 255;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public RoundVideoPlayingDrawable(View view, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        this.parentView = view;
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (j > 50) {
            j = 50;
        }
        float f = (float) j;
        float f2 = this.progress1 + ((f / 300.0f) * this.progress1Direction);
        this.progress1 = f2;
        if (f2 > 1.0f) {
            this.progress1Direction = -1;
            this.progress1 = 1.0f;
        } else if (f2 < 0.0f) {
            this.progress1Direction = 1;
            this.progress1 = 0.0f;
        }
        float f3 = this.progress2 + ((f / 310.0f) * this.progress2Direction);
        this.progress2 = f3;
        if (f3 > 1.0f) {
            this.progress2Direction = -1;
            this.progress2 = 1.0f;
        } else if (f3 < 0.0f) {
            this.progress2Direction = 1;
            this.progress2 = 0.0f;
        }
        float f4 = this.progress3 + ((f / 320.0f) * this.progress3Direction);
        this.progress3 = f4;
        if (f4 > 1.0f) {
            this.progress3Direction = -1;
            this.progress3 = 1.0f;
        } else if (f4 < 0.0f) {
            this.progress3Direction = 1;
            this.progress3 = 0.0f;
        }
        this.parentView.invalidate();
    }

    public void start() {
        if (this.started) {
            return;
        }
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        this.parentView.invalidate();
    }

    public void stop() {
        if (!this.started) {
            return;
        }
        this.started = false;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.paint.setColor(getThemedColor("chat_serviceText"));
        int i = this.alpha;
        if (i != 255) {
            Paint paint = this.paint;
            paint.setAlpha((int) (i * (paint.getAlpha() / 255.0f)));
        }
        int i2 = getBounds().left;
        int i3 = getBounds().top;
        for (int i4 = 0; i4 < 3; i4++) {
            canvas.drawRect(AndroidUtilities.dp(2.0f) + i2, AndroidUtilities.dp((this.progress1 * 7.0f) + 2.0f) + i3, AndroidUtilities.dp(4.0f) + i2, AndroidUtilities.dp(10.0f) + i3, this.paint);
            canvas.drawRect(AndroidUtilities.dp(5.0f) + i2, AndroidUtilities.dp((this.progress2 * 7.0f) + 2.0f) + i3, AndroidUtilities.dp(7.0f) + i2, AndroidUtilities.dp(10.0f) + i3, this.paint);
            canvas.drawRect(AndroidUtilities.dp(8.0f) + i2, AndroidUtilities.dp((this.progress3 * 7.0f) + 2.0f) + i3, AndroidUtilities.dp(10.0f) + i2, AndroidUtilities.dp(10.0f) + i3, this.paint);
        }
        if (this.started) {
            update();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.alpha = i;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(12.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(12.0f);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
