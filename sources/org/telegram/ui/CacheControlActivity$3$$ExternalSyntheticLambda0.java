package org.telegram.ui;

import j$.util.function.Consumer;
import java.io.File;
import java.nio.file.Path;
import org.telegram.ui.CacheControlActivity;

public final /* synthetic */ class CacheControlActivity$3$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ CacheControlActivity.AnonymousClass3 f$0;
    public final /* synthetic */ File f$1;

    public /* synthetic */ CacheControlActivity$3$$ExternalSyntheticLambda0(CacheControlActivity.AnonymousClass3 r1, File file) {
        this.f$0 = r1;
        this.f$1 = file;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$moveDirectory$0(this.f$1, (Path) obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
