package org.telegram.ui.Components;

import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.ChatActivityEnterView;

public final /* synthetic */ class ChatActivityEnterView$11$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ChatActivityEnterView.AnonymousClass11 f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ MediaController.PhotoEntry f$2;
    public final /* synthetic */ File f$3;

    public /* synthetic */ ChatActivityEnterView$11$$ExternalSyntheticLambda3(ChatActivityEnterView.AnonymousClass11 r1, ArrayList arrayList, MediaController.PhotoEntry photoEntry, File file) {
        this.f$0 = r1;
        this.f$1 = arrayList;
        this.f$2 = photoEntry;
        this.f$3 = file;
    }

    public final void run() {
        this.f$0.lambda$editPhoto$3(this.f$1, this.f$2, this.f$3);
    }
}
