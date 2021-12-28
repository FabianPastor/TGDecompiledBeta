package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.QrActivity;

public final /* synthetic */ class QrActivity$ThemeListViewController$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ QrActivity.ThemeListViewController f$0;

    public /* synthetic */ QrActivity$ThemeListViewController$$ExternalSyntheticLambda0(QrActivity.ThemeListViewController themeListViewController) {
        this.f$0 = themeListViewController;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$setupLightDarkTheme$2(valueAnimator);
    }
}
