package org.telegram.messenger.voip;

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

    public static NativeInstance make(String version, Instance.Config config, String path, Instance.Endpoint[] endpoints, Instance.Proxy proxy, int networkType, Instance.EncryptionKey encryptionKey, VideoSink remoteSink, long videoCapturer, AudioLevelsCallback audioLevelsCallback2) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("create new tgvoip instance, version " + version);
        } else {
            String str = version;
        }
        NativeInstance instance = new NativeInstance();
        instance.persistentStateFilePath = path;
        instance.audioLevelsCallback = audioLevelsCallback2;
        instance.nativePtr = makeNativeInstance(version, instance, config, path, endpoints, proxy, networkType, encryptionKey, remoteSink, videoCapturer, ((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)));
        return instance;
    }

    public static NativeInstance makeGroup(String logPath, long videoCapturer, boolean screencast, boolean noiseSupression, PayloadCallback payloadCallback2, AudioLevelsCallback audioLevelsCallback2, VideoSourcesCallback unknownParticipantsCallback2, RequestBroadcastPartCallback requestBroadcastPartCallback2, RequestBroadcastPartCallback cancelRequestBroadcastPartCallback2) {
        ContextUtils.initialize(ApplicationLoader.applicationContext);
        NativeInstance instance = new NativeInstance();
        instance.payloadCallback = payloadCallback2;
        instance.audioLevelsCallback = audioLevelsCallback2;
        instance.unknownParticipantsCallback = unknownParticipantsCallback2;
        instance.requestBroadcastPartCallback = requestBroadcastPartCallback2;
        instance.cancelRequestBroadcastPartCallback = cancelRequestBroadcastPartCallback2;
        instance.isGroup = true;
        instance.nativePtr = makeGroupNativeInstance(instance, logPath, SharedConfig.disableVoiceAudioEffects, videoCapturer, screencast, noiseSupression);
        return instance;
    }

    public int getPeerCapabilities() {
        return 0;
    }

    public boolean isGroup() {
        return this.isGroup;
    }

    public void setOnStateUpdatedListener(Instance.OnStateUpdatedListener listener) {
        this.onStateUpdatedListener = listener;
    }

    public void setOnSignalBarsUpdatedListener(Instance.OnSignalBarsUpdatedListener listener) {
        this.onSignalBarsUpdatedListener = listener;
    }

    public void setOnSignalDataListener(Instance.OnSignalingDataListener listener) {
        this.onSignalDataListener = listener;
    }

    public void setOnRemoteMediaStateUpdatedListener(Instance.OnRemoteMediaStateUpdatedListener listener) {
        this.onRemoteMediaStateUpdatedListener = listener;
    }

    private void onStateUpdated(int state) {
        Instance.OnStateUpdatedListener onStateUpdatedListener2 = this.onStateUpdatedListener;
        if (onStateUpdatedListener2 != null) {
            onStateUpdatedListener2.onStateUpdated(state, false);
        }
    }

    private void onSignalBarsUpdated(int signalBars) {
        Instance.OnSignalBarsUpdatedListener onSignalBarsUpdatedListener2 = this.onSignalBarsUpdatedListener;
        if (onSignalBarsUpdatedListener2 != null) {
            onSignalBarsUpdatedListener2.onSignalBarsUpdated(signalBars);
        }
    }

    private void onSignalingData(byte[] data) {
        Instance.OnSignalingDataListener onSignalingDataListener = this.onSignalDataListener;
        if (onSignalingDataListener != null) {
            onSignalingDataListener.onSignalingData(data);
        }
    }

    private void onRemoteMediaStateUpdated(int audioState, int videoState) {
        Instance.OnRemoteMediaStateUpdatedListener onRemoteMediaStateUpdatedListener2 = this.onRemoteMediaStateUpdatedListener;
        if (onRemoteMediaStateUpdatedListener2 != null) {
            onRemoteMediaStateUpdatedListener2.onMediaStateUpdated(audioState, videoState);
        }
    }

    private void onNetworkStateUpdated(boolean connected, boolean inTransition) {
        if (this.onStateUpdatedListener != null) {
            AndroidUtilities.runOnUIThread(new NativeInstance$$ExternalSyntheticLambda2(this, connected, inTransition));
        }
    }

    /* renamed from: lambda$onNetworkStateUpdated$0$org-telegram-messenger-voip-NativeInstance  reason: not valid java name */
    public /* synthetic */ void m1192xvar_(boolean connected, boolean inTransition) {
        this.onStateUpdatedListener.onStateUpdated(connected, inTransition);
    }

    private void onAudioLevelsUpdated(int[] uids, float[] levels, boolean[] voice) {
        if (!this.isGroup || uids == null || uids.length != 0) {
            AndroidUtilities.runOnUIThread(new NativeInstance$$ExternalSyntheticLambda3(this, uids, levels, voice));
        }
    }

    /* renamed from: lambda$onAudioLevelsUpdated$1$org-telegram-messenger-voip-NativeInstance  reason: not valid java name */
    public /* synthetic */ void m1190xCLASSNAMEa3e1(int[] uids, float[] levels, boolean[] voice) {
        this.audioLevelsCallback.run(uids, levels, voice);
    }

    private void onParticipantDescriptionsRequired(long taskPtr, int[] ssrcs) {
        if (this.unknownParticipantsCallback != null) {
            AndroidUtilities.runOnUIThread(new NativeInstance$$ExternalSyntheticLambda1(this, taskPtr, ssrcs));
        }
    }

    /* renamed from: lambda$onParticipantDescriptionsRequired$2$org-telegram-messenger-voip-NativeInstance  reason: not valid java name */
    public /* synthetic */ void m1193xvar_CLASSNAME(long taskPtr, int[] ssrcs) {
        this.unknownParticipantsCallback.run(taskPtr, ssrcs);
    }

    private void onEmitJoinPayload(String json, int ssrc) {
        try {
            AndroidUtilities.runOnUIThread(new NativeInstance$$ExternalSyntheticLambda0(this, ssrc, json));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$onEmitJoinPayload$3$org-telegram-messenger-voip-NativeInstance  reason: not valid java name */
    public /* synthetic */ void m1191x9a75a4dc(int ssrc, String json) {
        this.payloadCallback.run(ssrc, json);
    }

    private void onRequestBroadcastPart(long timestamp, long duration, int videoChannel, int quality) {
        this.requestBroadcastPartCallback.run(timestamp, duration, videoChannel, quality);
    }

    private void onCancelRequestBroadcastPart(long timestamp, int videoChannel, int quality) {
        this.cancelRequestBroadcastPartCallback.run(timestamp, 0, 0, 0);
    }

    private void onStop(Instance.FinalState state) {
        this.finalState = state;
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
