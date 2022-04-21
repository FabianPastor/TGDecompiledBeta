package org.telegram.messenger.voip;

import java.nio.ByteBuffer;

public final /* synthetic */ class AudioRecordJNI$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ AudioRecordJNI f$0;
    public final /* synthetic */ ByteBuffer f$1;

    public /* synthetic */ AudioRecordJNI$$ExternalSyntheticLambda0(AudioRecordJNI audioRecordJNI, ByteBuffer byteBuffer) {
        this.f$0 = audioRecordJNI;
        this.f$1 = byteBuffer;
    }

    public final void run() {
        this.f$0.m1139lambda$startThread$0$orgtelegrammessengervoipAudioRecordJNI(this.f$1);
    }
}
