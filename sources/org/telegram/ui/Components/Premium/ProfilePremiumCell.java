package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.Premium.StarParticlesView;
/* loaded from: classes3.dex */
public class ProfilePremiumCell extends TextCell {
    StarParticlesView.Drawable drawable;

    public ProfilePremiumCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(6);
        this.drawable = drawable;
        drawable.size1 = 6;
        drawable.size2 = 6;
        drawable.size3 = 6;
        drawable.useGradient = true;
        drawable.speedScale = 3.0f;
        drawable.minLifeTime = 600L;
        drawable.randLifeTime = 500;
        drawable.startFromCenter = true;
        drawable.type = 101;
        drawable.init();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Cells.TextCell, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        float x = this.imageView.getX() + (this.imageView.getWidth() / 2.0f);
        float paddingTop = ((this.imageView.getPaddingTop() + this.imageView.getY()) + (this.imageView.getHeight() / 2.0f)) - AndroidUtilities.dp(3.0f);
        this.drawable.rect.set(x - AndroidUtilities.dp(4.0f), paddingTop - AndroidUtilities.dp(4.0f), x + AndroidUtilities.dp(4.0f), paddingTop + AndroidUtilities.dp(4.0f));
        if (z) {
            this.drawable.resetPositions();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        this.drawable.onDraw(canvas);
        invalidate();
        super.dispatchDraw(canvas);
    }
}
