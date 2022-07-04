package org.telegram.ui.Components;

import org.telegram.ui.Components.FilterShaders;

public final /* synthetic */ class FilterGLThread$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ FilterGLThread f$0;
    public final /* synthetic */ FilterShaders.FilterShadersDelegate f$1;

    public /* synthetic */ FilterGLThread$$ExternalSyntheticLambda5(FilterGLThread filterGLThread, FilterShaders.FilterShadersDelegate filterShadersDelegate) {
        this.f$0 = filterGLThread;
        this.f$1 = filterShadersDelegate;
    }

    public final void run() {
        this.f$0.lambda$setFilterGLThreadDelegate$0(this.f$1);
    }
}
