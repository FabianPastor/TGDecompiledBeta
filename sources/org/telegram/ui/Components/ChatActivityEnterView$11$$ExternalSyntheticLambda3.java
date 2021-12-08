package org.telegram.ui.Components;

import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.MediaController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatActivityEnterView;

public final /* synthetic */ class ChatActivityEnterView$11$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ChatActivityEnterView.AnonymousClass11 f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ MediaController.PhotoEntry f$3;
    public final /* synthetic */ File f$4;

    public /* synthetic */ ChatActivityEnterView$11$$ExternalSyntheticLambda3(ChatActivityEnterView.AnonymousClass11 r1, Theme.ResourcesProvider resourcesProvider, ArrayList arrayList, MediaController.PhotoEntry photoEntry, File file) {
        this.f$0 = r1;
        this.f$1 = resourcesProvider;
        this.f$2 = arrayList;
        this.f$3 = photoEntry;
        this.f$4 = file;
    }

    public final void run() {
        this.f$0.m2079x2199cCLASSNAME(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
