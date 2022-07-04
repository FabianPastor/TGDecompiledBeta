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
        drawable2.size2 = 6;
        drawable2.size3 = 6;
        drawable2.useGradient = true;
        drawable2.speedScale = 3.0f;
        drawable2.minLifeTime = 600;
        drawable2.randLifeTime = 500;
        drawable2.startFromCenter = true;
        drawable2.type = 101;
        drawable2.init();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        float x = this.imageView.getX() + (((float) this.imageView.getWidth()) / 2.0f);
        float paddingTop = ((((float) this.imageView.getPaddingTop()) + this.imageView.getY()) + (((float) this.imageView.getHeight()) / 2.0f)) - ((float) AndroidUtilities.dp(3.0f));
        this.drawable.rect.set(x - ((float) AndroidUtilities.dp(4.0f)), paddingTop - ((float) AndroidUtilities.dp(4.0f)), x + ((float) AndroidUtilities.dp(4.0f)), paddingTop + ((float) AndroidUtilities.dp(4.0f)));
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
