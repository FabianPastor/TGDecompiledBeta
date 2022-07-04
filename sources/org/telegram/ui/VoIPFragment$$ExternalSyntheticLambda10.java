package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.voip.VoIPService;

public final /* synthetic */ class VoIPFragment$$ExternalSyntheticLambda10 implements View.OnClickListener {
    public final /* synthetic */ VoIPFragment f$0;
    public final /* synthetic */ VoIPService f$1;

    public /* synthetic */ VoIPFragment$$ExternalSyntheticLambda10(VoIPFragment voIPFragment, VoIPService voIPService) {
        this.f$0 = voIPFragment;
        this.f$1 = voIPService;
    }

    public final void onClick(View view) {
        this.f$0.m4823lambda$setVideoAction$25$orgtelegramuiVoIPFragment(this.f$1, view);
    }
}
