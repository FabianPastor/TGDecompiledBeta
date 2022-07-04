package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.voip.VoIPService;

public final /* synthetic */ class VoIPFragment$$ExternalSyntheticLambda6 implements DialogInterface.OnClickListener {
    public final /* synthetic */ VoIPFragment f$0;
    public final /* synthetic */ VoIPService f$1;

    public /* synthetic */ VoIPFragment$$ExternalSyntheticLambda6(VoIPFragment voIPFragment, VoIPService voIPService) {
        this.f$0 = voIPFragment;
        this.f$1 = voIPService;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$setVideoAction$24(this.f$1, dialogInterface, i);
    }
}
