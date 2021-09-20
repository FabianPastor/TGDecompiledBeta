package org.telegram.messenger.voip;

import android.graphics.Point;

public final /* synthetic */ class VideoCapturerDevice$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ VideoCapturerDevice f$0;
    public final /* synthetic */ Point f$1;

    public /* synthetic */ VideoCapturerDevice$$ExternalSyntheticLambda7(VideoCapturerDevice videoCapturerDevice, Point point) {
        this.f$0 = videoCapturerDevice;
        this.f$1 = point;
    }

    public final void run() {
        VideoCapturerDevice.lambda$checkScreenCapturerSize$1(this.f$0, this.f$1);
    }
}
