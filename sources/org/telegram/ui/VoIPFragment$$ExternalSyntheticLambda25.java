package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.voip.VoIPService;

public final /* synthetic */ class VoIPFragment$$ExternalSyntheticLambda25 implements DialogInterface.OnClickListener {
    public final /* synthetic */ VoIPFragment f$0;
    public final /* synthetic */ VoIPService f$1;

    public /* synthetic */ VoIPFragment$$ExternalSyntheticLambda25(VoIPFragment voIPFragment, VoIPService voIPService) {
        this.f$0 = voIPFragment;
        this.f$1 = voIPService;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m4060lambda$setVideoAction$23$orgtelegramuiVoIPFragment(this.f$1, dialogInterface, i);
    }
}
