package org.telegram.messenger.voip;

import android.graphics.Point;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.voip.Instance;
import org.webrtc.ContextUtils;
import org.webrtc.VideoSink;

public class NativeInstance {
    private AudioLevelsCallback audioLevelsCallback;
    private RequestBroadcastPartCallback cancelRequestBroadcastPartCallback;
    private Instance.FinalState finalState;
    private boolean isGroup;
    private long nativePtr;
    private Instance.OnRemoteMediaStateUpdatedListener onRemoteMediaStateUpdatedListener;
    private Instance.OnSignalBarsUpdatedListener onSignalBarsUpdatedListener;
    private Instance.OnSignalingDataListener onSignalDataListener;
    private Instance.OnStateUpdatedListener onStateUpdatedListener;
    private PayloadCallback payloadCallback;
    private String persistentStateFilePath;
    private RequestBroadcastPartCallback requestBroadcastPartCallback;
    private CountDownLatch stopBarrier;
    private float[] temp = new float[1];
    private VideoSourcesCallback unknownParticipantsCallback;

    public interface AudioLevelsCallback {
        void run(int[] iArr, float[] fArr, boolean[] zArr);
    }

    public interface PayloadCallback {
        void run(int i, String str);
    }

    public interface RequestBroadcastPartCallback {
        void run(long j, long j2, int i, int i2);
    }

    public static class SsrcGroup {
        public String semantics;
        public int[] ssrcs;
    }

    public interface VideoSourcesCallback {
        void run(long j, int[] iArr);
    }

    public static native long createVideoCapturer(VideoSink videoSink, int i);

    public static native void destroyVideoCapturer(long j);

    private static native long makeGroupNativeInstance(NativeInstance nativeInstance, String str, boolean z, long j, boolean z2, boolean z3);

    private static native long makeNativeInstance(String str, NativeInstance nativeInstance, Instance.Config config, String str2, Instance.Endpoint[] endpointArr, Instance.Proxy proxy, int i, Instance.EncryptionKey encryptionKey, VideoSink videoSink, long j, float f);

    public static native void setVideoStateCapturer(long j, int i);

    private native void stopGroupNative();

    private native void stopNative();

    public static native void switchCameraCapturer(long j, boolean z);

    public native void activateVideoCapturer(long j);

    public native long addIncomingVideoOutput(int i, String str, SsrcGroup[] ssrcGroupArr, VideoSink videoSink);

    public native void clearVideoCapturer();

    public native String getDebugInfo();

    public native String getLastError();

    public int getPeerCapabilities() {
        return 0;
    }

    public native byte[] getPersistentState();

    public native long getPreferredRelayId();

    public native Instance.TrafficStats getTrafficStats();

    public native String getVersion();

    public native boolean hasVideoCapturer();

    public native void onMediaDescriptionAvailable(long j, int[] iArr);

    public native void onSignalingDataReceive(byte[] bArr);

    public native void onStreamPartAvailable(long j, ByteBuffer byteBuffer, int i, long j2, int i2, int i3);

    public native void prepareForStream();

    public native void removeIncomingVideoOutput(long j);

    public native void resetGroupInstance(boolean z, boolean z2);

    public native void setAudioOutputGainControlEnabled(boolean z);

    public native void setBufferSize(int i);

    public native void setEchoCancellationStrength(int i);

    public native void setGlobalServerConfig(String str);

    public native void setJoinResponsePayload(String str);

    public native void setMuteMicrophone(boolean z);

    public native void setNetworkType(int i);

    public native void setNoiseSuppressionEnabled(boolean z);

    public native void setVideoEndpointQuality(String str, int i);

    public native void setVideoState(int i);

    public native void setVolume(int i, double d);

    public native void setupOutgoingVideo(VideoSink videoSink, int i);

    public native void setupOutgoingVideoCreated(long j);

    public native void switchCamera(boolean z);

    public static NativeInstance make(String str, Instance.Config config, String str2, Instance.Endpoint[] endpointArr, Instance.Proxy proxy, int i, Instance.EncryptionKey encryptionKey, VideoSink videoSink, long j, AudioLevelsCallback audioLevelsCallback2) {
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder sb = new StringBuilder();
            sb.append("create new tgvoip instance, version ");
            String str3 = str;
            sb.append(str);
            FileLog.d(sb.toString());
        } else {
            String str4 = str;
        }
        NativeInstance nativeInstance = new NativeInstance();
        String str5 = str2;
        nativeInstance.persistentStateFilePath = str5;
        nativeInstance.audioLevelsCallback = audioLevelsCallback2;
        Point point = AndroidUtilities.displaySize;
        Point point2 = AndroidUtilities.displaySize;
        nativeInstance.nativePtr = makeNativeInstance(str, nativeInstance, config, str5, endpointArr, proxy, i, encryptionKey, videoSink, j, ((float) Math.min(point.x, point.y)) / ((float) Math.max(point2.x, point2.y)));
        return nativeInstance;
    }

    public static NativeInstance makeGroup(String str, long j, boolean z, boolean z2, PayloadCallback payloadCallback2, AudioLevelsCallback audioLevelsCallback2, VideoSourcesCallback videoSourcesCallback, RequestBroadcastPartCallback requestBroadcastPartCallback2, RequestBroadcastPartCallback requestBroadcastPartCallback3) {
        ContextUtils.initialize(ApplicationLoader.applicationContext);
        NativeInstance nativeInstance = new NativeInstance();
        nativeInstance.payloadCallback = payloadCallback2;
        nativeInstance.audioLevelsCallback = audioLevelsCallback2;
        nativeInstance.unknownParticipantsCallback = videoSourcesCallback;
        nativeInstance.requestBroadcastPartCallback = requestBroadcastPartCallback2;
        nativeInstance.cancelRequestBroadcastPartCallback = requestBroadcastPartCallback3;
        nativeInstance.isGroup = true;
        nativeInstance.nativePtr = makeGroupNativeInstance(nativeInstance, str, SharedConfig.disableVoiceAudioEffects, j, z, z2);
        return nativeInstance;
    }

    public boolean isGroup() {
        return this.isGroup;
    }

    public void setOnStateUpdatedListener(Instance.OnStateUpdatedListener onStateUpdatedListener2) {
        this.onStateUpdatedListener = onStateUpdatedListener2;
    }

    public void setOnSignalBarsUpdatedListener(Instance.OnSignalBarsUpdatedListener onSignalBarsUpdatedListener2) {
        this.onSignalBarsUpdatedListener = onSignalBarsUpdatedListener2;
    }

    public void setOnSignalDataListener(Instance.OnSignalingDataListener onSignalingDataListener) {
        this.onSignalDataListener = onSignalingDataListener;
    }

    public void setOnRemoteMediaStateUpdatedListener(Instance.OnRemoteMediaStateUpdatedListener onRemoteMediaStateUpdatedListener2) {
        this.onRemoteMediaStateUpdatedListener = onRemoteMediaStateUpdatedListener2;
    }

    private void onStateUpdated(int i) {
        Instance.OnStateUpdatedListener onStateUpdatedListener2 = this.onStateUpdatedListener;
        if (onStateUpdatedListener2 != null) {
            onStateUpdatedListener2.onStateUpdated(i, false);
        }
    }

    private void onSignalBarsUpdated(int i) {
        Instance.OnSignalBarsUpdatedListener onSignalBarsUpdatedListener2 = this.onSignalBarsUpdatedListener;
        if (onSignalBarsUpdatedListener2 != null) {
            onSignalBarsUpdatedListener2.onSignalBarsUpdated(i);
        }
    }

    private void onSignalingData(byte[] bArr) {
        Instance.OnSignalingDataListener onSignalingDataListener = this.onSignalDataListener;
        if (onSignalingDataListener != null) {
            onSignalingDataListener.onSignalingData(bArr);
        }
    }

    private void onRemoteMediaStateUpdated(int i, int i2) {
        Instance.OnRemoteMediaStateUpdatedListener onRemoteMediaStateUpdatedListener2 = this.onRemoteMediaStateUpdatedListener;
        if (onRemoteMediaStateUpdatedListener2 != null) {
            onRemoteMediaStateUpdatedListener2.onMediaStateUpdated(i, i2);
        }
    }

    private void onNetworkStateUpdated(boolean z, boolean z2) {
        if (this.onStateUpdatedListener != null) {
            AndroidUtilities.runOnUIThread(new Runnable(z, z2) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NativeInstance.this.lambda$onNetworkStateUpdated$0$NativeInstance(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onNetworkStateUpdated$0 */
    public /* synthetic */ void lambda$onNetworkStateUpdated$0$NativeInstance(boolean z, boolean z2) {
        this.onStateUpdatedListener.onStateUpdated(z ? 1 : 0, z2);
    }

    private void onAudioLevelsUpdated(int[] iArr, float[] fArr, boolean[] zArr) {
        if (!this.isGroup || iArr == null || iArr.length != 0) {
            AndroidUtilities.runOnUIThread(new Runnable(iArr, fArr, zArr) {
                public final /* synthetic */ int[] f$1;
                public final /* synthetic */ float[] f$2;
                public final /* synthetic */ boolean[] f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    NativeInstance.this.lambda$onAudioLevelsUpdated$1$NativeInstance(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onAudioLevelsUpdated$1 */
    public /* synthetic */ void lambda$onAudioLevelsUpdated$1$NativeInstance(int[] iArr, float[] fArr, boolean[] zArr) {
        this.audioLevelsCallback.run(iArr, fArr, zArr);
    }

    private void onParticipantDescriptionsRequired(long j, int[] iArr) {
        if (this.unknownParticipantsCallback != null) {
            AndroidUtilities.runOnUIThread(new Runnable(j, iArr) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ int[] f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    NativeInstance.this.lambda$onParticipantDescriptionsRequired$2$NativeInstance(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onParticipantDescriptionsRequired$2 */
    public /* synthetic */ void lambda$onParticipantDescriptionsRequired$2$NativeInstance(long j, int[] iArr) {
        this.unknownParticipantsCallback.run(j, iArr);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onEmitJoinPayload$3 */
    public /* synthetic */ void lambda$onEmitJoinPayload$3$NativeInstance(int i, String str) {
        this.payloadCallback.run(i, str);
    }

    private void onEmitJoinPayload(String str, int i) {
        try {
            AndroidUtilities.runOnUIThread(new Runnable(i, str) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NativeInstance.this.lambda$onEmitJoinPayload$3$NativeInstance(this.f$1, this.f$2);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void onRequestBroadcastPart(long j, long j2, int i, int i2) {
        this.requestBroadcastPartCallback.run(j, j2, i, i2);
    }

    private void onCancelRequestBroadcastPart(long j, int i, int i2) {
        this.cancelRequestBroadcastPartCallback.run(j, 0, 0, 0);
    }

    private void onStop(Instance.FinalState finalState2) {
        this.finalState = finalState2;
        CountDownLatch countDownLatch = this.stopBarrier;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public Instance.FinalState stop() {
        this.stopBarrier = new CountDownLatch(1);
        stopNative();
        try {
            this.stopBarrier.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return this.finalState;
    }

    public void stopGroup() {
        stopGroupNative();
    }
}
