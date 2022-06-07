package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.Premium.StarParticlesView;

public class ProfilePremiumCell extends TextCell {
    StarParticlesView.Drawable drawable;

    public ProfilePremiumCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        StarParticlesView.Drawable drawable2 = new StarParticlesView.Drawable(8);
        this.drawable = drawable2;
        drawable2.size1 = 6;
        drawable2.size2 = 6;
        drawable2.size3 = 6;
        drawable2.useGradient = true;
        drawable2.speedScale = 0.2f;
        drawable2.minLifeTime = 2000;
        drawable2.init();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.drawable.rect.set(this.imageView.getX(), this.imageView.getY(), this.imageView.getX() + ((float) this.imageView.getWidth()), this.imageView.getY() + ((float) this.imageView.getMeasuredHeight()));
        if (z) {
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
