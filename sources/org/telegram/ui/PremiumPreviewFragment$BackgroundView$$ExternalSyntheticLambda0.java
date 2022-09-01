package org.telegram.ui;

import android.graphics.Canvas;
import android.graphics.Path;
import androidx.core.util.Consumer;
import org.telegram.ui.PremiumPreviewFragment;

public final /* synthetic */ class PremiumPreviewFragment$BackgroundView$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ PremiumPreviewFragment.BackgroundView f$0;
    public final /* synthetic */ Path f$1;
    public final /* synthetic */ float[] f$2;

    public /* synthetic */ PremiumPreviewFragment$BackgroundView$$ExternalSyntheticLambda0(PremiumPreviewFragment.BackgroundView backgroundView, Path path, float[] fArr) {
        this.f$0 = backgroundView;
        this.f$1 = path;
        this.f$2 = fArr;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$new$1(this.f$1, this.f$2, (Canvas) obj);
    }
}
