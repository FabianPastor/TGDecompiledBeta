package org.telegram.ui.Components;

import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$OOI-h5XPcAppA8kMz9_Mz1WuSyw implements OnClickListener {
    private final /* synthetic */ int[] f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ SharedPreferences f$3;
    private final /* synthetic */ Builder f$4;
    private final /* synthetic */ Runnable f$5;

    public /* synthetic */ -$$Lambda$AlertsCreator$OOI-h5XPcAppA8kMz9_Mz1WuSyw(int[] iArr, long j, int i, SharedPreferences sharedPreferences, Builder builder, Runnable runnable) {
        this.f$0 = iArr;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = sharedPreferences;
        this.f$4 = builder;
        this.f$5 = runnable;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createPrioritySelectDialog$34(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
    }
}
