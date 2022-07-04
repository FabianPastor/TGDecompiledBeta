package org.telegram.ui.Components.voip;

public final /* synthetic */ class GroupCallRenderersContainer$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ GroupCallRenderersContainer f$0;
    public final /* synthetic */ GroupCallMiniTextureView f$1;
    public final /* synthetic */ GroupCallMiniTextureView f$2;

    public /* synthetic */ GroupCallRenderersContainer$$ExternalSyntheticLambda9(GroupCallRenderersContainer groupCallRenderersContainer, GroupCallMiniTextureView groupCallMiniTextureView, GroupCallMiniTextureView groupCallMiniTextureView2) {
        this.f$0 = groupCallRenderersContainer;
        this.f$1 = groupCallMiniTextureView;
        this.f$2 = groupCallMiniTextureView2;
    }

    public final void run() {
        this.f$0.lambda$requestFullscreen$3(this.f$1, this.f$2);
    }
}
