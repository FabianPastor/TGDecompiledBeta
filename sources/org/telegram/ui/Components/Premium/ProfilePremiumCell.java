package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.Premium.StarParticlesView;

public class ProfilePremiumCell extends TextCell {
    StarParticlesView.Drawable drawable;

    public ProfilePremiumCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        StarParticlesView.Drawable drawable2 = new StarParticlesView.Drawable(6);
        this.drawable = drawable2;
        drawable2.size1 = 6;
        this.drawable.size2 = 6;
        this.drawable.size3 = 6;
        this.drawable.useGradient = true;
        this.drawable.speedScale = 3.0f;
        this.drawable.minLifeTime = 600;
        this.drawable.randLifeTime = 500;
        this.drawable.startFromCenter = true;
        this.drawable.type = 101;
        this.drawable.init();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float cx = this.imageView.getX() + (((float) this.imageView.getWidth()) / 2.0f);
        float cy = ((((float) this.imageView.getPaddingTop()) + this.imageView.getY()) + (((float) this.imageView.getHeight()) / 2.0f)) - ((float) AndroidUtilities.dp(3.0f));
        this.drawable.rect.set(cx - ((float) AndroidUtilities.dp(4.0f)), cy - ((float) AndroidUtilities.dp(4.0f)), ((float) AndroidUtilities.dp(4.0f)) + cx, ((float) AndroidUtilities.dp(4.0f)) + cy);
        if (changed) {
            this.drawable.resetPositions();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.drawable.onDraw(canvas);
        invalidate();
        super.dispatchDraw(canvas);
    }
}
