package org.telegram.ui.Components;

import android.app.Activity;
import android.net.Uri;
import java.io.File;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatActivityEnterView;

public final /* synthetic */ class ChatActivityEnterView$11$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ ChatActivityEnterView.AnonymousClass11 f$0;
    public final /* synthetic */ Activity f$1;
    public final /* synthetic */ Uri f$2;
    public final /* synthetic */ File f$3;
    public final /* synthetic */ Theme.ResourcesProvider f$4;

    public /* synthetic */ ChatActivityEnterView$11$$ExternalSyntheticLambda2(ChatActivityEnterView.AnonymousClass11 r1, Activity activity, Uri uri, File file, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = r1;
        this.f$1 = activity;
        this.f$2 = uri;
        this.f$3 = file;
        this.f$4 = resourcesProvider;
    }

    public final void run() {
        this.f$0.lambda$editPhoto$4(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
