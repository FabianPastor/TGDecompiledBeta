package org.telegram.ui.Components.voip;

import android.content.SharedPreferences;
import android.view.View;
import org.telegram.ui.Cells.TextCheckCell;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda9 implements View.OnClickListener {
    public final /* synthetic */ SharedPreferences f$0;
    public final /* synthetic */ TextCheckCell f$1;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda9(SharedPreferences sharedPreferences, TextCheckCell textCheckCell) {
        this.f$0 = sharedPreferences;
        this.f$1 = textCheckCell;
    }

    public final void onClick(View view) {
        VoIPHelper.lambda$showCallDebugSettings$17(this.f$0, this.f$1, view);
    }
}
