package org.telegram.ui;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.FilterGLThread;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.VideoEditTextureView;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda64 implements VideoEditTextureView.VideoEditTextureViewDelegate {
    public final /* synthetic */ MediaController.SavedFilterState f$0;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda64(MediaController.SavedFilterState savedFilterState) {
        this.f$0 = savedFilterState;
    }

    public final void onEGLThreadAvailable(FilterGLThread filterGLThread) {
        filterGLThread.setFilterGLThreadDelegate(FilterShaders.getFilterShadersDelegate(this.f$0));
    }
}
