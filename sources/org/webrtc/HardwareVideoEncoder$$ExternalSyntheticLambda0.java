package org.webrtc;

public final /* synthetic */ class HardwareVideoEncoder$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ HardwareVideoEncoder f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ HardwareVideoEncoder$$ExternalSyntheticLambda0(HardwareVideoEncoder hardwareVideoEncoder, int i) {
        this.f$0 = hardwareVideoEncoder;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$deliverEncodedImage$0(this.f$1);
    }
}
