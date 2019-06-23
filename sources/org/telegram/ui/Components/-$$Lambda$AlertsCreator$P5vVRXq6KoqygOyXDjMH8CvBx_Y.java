package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$P5vVRXq6KoqygOyXDjMH8CvBx_Y implements OnClickListener {
    private final /* synthetic */ int[] f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Builder f$2;
    private final /* synthetic */ Runnable f$3;

    public /* synthetic */ -$$Lambda$AlertsCreator$P5vVRXq6KoqygOyXDjMH8CvBx_Y(int[] iArr, int i, Builder builder, Runnable runnable) {
        this.f$0 = iArr;
        this.f$1 = i;
        this.f$2 = builder;
        this.f$3 = runnable;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createPopupSelectDialog$38(this.f$0, this.f$1, this.f$2, this.f$3, view);
    }
}
