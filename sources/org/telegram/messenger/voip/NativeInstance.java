package org.telegram.messenger.voip;

import android.graphics.Point;
import java.util.concurrent.CountDownLatch;
import org.json.JSONArray;
import org.json.JSONObject;
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
    private Instance.FinalState finalState;
    private boolean isGroup;
    private long nativePtr;
    private Instance.OnRemoteMediaStateUpdatedListener onRemoteMediaStateUpdatedListener;
    private Instance.OnSignalBarsUpdatedListener onSignalBarsUpdatedListener;
    private Instance.OnSignalingDataListener onSignalDataListener;
    private Instance.OnStateUpdatedListener onStateUpdatedListener;
    private PayloadCallback payloadCallback;
    private String persistentStateFilePath;
    private CountDownLatch stopBarrier;
    private float[] temp = new float[1];

    public interface AudioLevelsCallback {
        void run(int[] iArr, float[] fArr, boolean[] zArr);
    }

    public interface PayloadCallback {
        void run(int i, String str);
    }

    public static native long createVideoCapturer(VideoSink videoSink, boolean z);

    public static native void destroyVideoCapturer(long j);

    private static native long makeGroupNativeInstance(NativeInstance nativeInstance, boolean z);

    private static native long makeNativeInstance(String str, NativeInstance nativeInstance, Instance.Config config, String str2, Instance.Endpoint[] endpointArr, Instance.Proxy proxy, int i, Instance.EncryptionKey encryptionKey, VideoSink videoSink, long j, float f);

    public static native void setVideoStateCapturer(long j, int i);

    private native void stopGroupNative();

    private native void stopNative();

    public static native void switchCameraCapturer(long j, boolean z);

    public native String getDebugInfo();

    public native String getLastError();

    public int getPeerCapabilities() {
        return 0;
    }

    public native byte[] getPersistentState();

    public native long getPreferredRelayId();

    public native Instance.TrafficStats getTrafficStats();

    public native String getVersion();

    public native void onSignalingDataReceive(byte[] bArr);

    public native void removeSsrcs(int[] iArr);

    public native void setAudioOutputGainControlEnabled(boolean z);

    public native void setBufferSize(int i);

    public native void setEchoCancellationStrength(int i);

    public native void setGlobalServerConfig(String str);

    public native void setJoinResponsePayload(String str, String str2, Instance.Fingerprint[] fingerprintArr, Instance.Candidate[] candidateArr);

    public native void setMuteMicrophone(boolean z);

    public native void setNetworkType(int i);

    public native void setVideoState(int i);

    public native void setupOutgoingVideo(VideoSink videoSink, boolean z);

    public native void switchCamera(boolean z);

    public static NativeInstance make(String str, Instance.Config config, String str2, Instance.Endpoint[] endpointArr, Instance.Proxy proxy, int i, Instance.EncryptionKey encryptionKey, VideoSink videoSink, long j) {
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
        Point point = AndroidUtilities.displaySize;
        Point point2 = AndroidUtilities.displaySize;
        nativeInstance.nativePtr = makeNativeInstance(str, nativeInstance, config, str5, endpointArr, proxy, i, encryptionKey, videoSink, j, ((float) Math.min(point.x, point.y)) / ((float) Math.max(point2.x, point2.y)));
        return nativeInstance;
    }

    public static NativeInstance makeGroup(PayloadCallback payloadCallback2, AudioLevelsCallback audioLevelsCallback2) {
        ContextUtils.initialize(ApplicationLoader.applicationContext);
        NativeInstance nativeInstance = new NativeInstance();
        nativeInstance.payloadCallback = payloadCallback2;
        nativeInstance.audioLevelsCallback = audioLevelsCallback2;
        nativeInstance.isGroup = true;
        nativeInstance.nativePtr = makeGroupNativeInstance(nativeInstance, SharedConfig.disableVoiceAudioEffects);
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
            onStateUpdatedListener2.onStateUpdated(i);
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

    private void onNetworkStateUpdated(boolean z) {
        if (this.onStateUpdatedListener != null) {
            AndroidUtilities.runOnUIThread(new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NativeInstance.this.lambda$onNetworkStateUpdated$0$NativeInstance(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onNetworkStateUpdated$0 */
    public /* synthetic */ void lambda$onNetworkStateUpdated$0$NativeInstance(boolean z) {
        this.onStateUpdatedListener.onStateUpdated(z ? 1 : 0);
    }

    private void onAudioLevelsUpdated(int[] iArr, float[] fArr, boolean[] zArr) {
        if (iArr.length != 0) {
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

    private void onEmitJoinPayload(String str, String str2, Instance.Fingerprint[] fingerprintArr, int i) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("ufrag", str);
            jSONObject.put("pwd", str2);
            JSONArray jSONArray = new JSONArray();
            for (int i2 = 0; i2 < fingerprintArr.length; i2++) {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("hash", fingerprintArr[i2].hash);
                jSONObject2.put("fingerprint", fingerprintArr[i2].fingerprint);
                jSONObject2.put("setup", fingerprintArr[i2].setup);
                jSONArray.put(jSONObject2);
            }
            jSONObject.put("fingerprints", jSONArray);
            jSONObject.put("ssrc", i);
            AndroidUtilities.runOnUIThread(new Runnable(i, jSONObject) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ JSONObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NativeInstance.this.lambda$onEmitJoinPayload$2$NativeInstance(this.f$1, this.f$2);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onEmitJoinPayload$2 */
    public /* synthetic */ void lambda$onEmitJoinPayload$2$NativeInstance(int i, JSONObject jSONObject) {
        this.payloadCallback.run(i, jSONObject.toString());
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
