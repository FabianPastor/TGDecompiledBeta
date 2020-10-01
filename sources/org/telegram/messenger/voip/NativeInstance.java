package org.telegram.messenger.voip;

import android.graphics.Point;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.Instance;
import org.webrtc.VideoSink;

public class NativeInstance {
    private Instance.FinalState finalState;
    private long nativePtr;
    private Instance.OnRemoteMediaStateUpdatedListener onRemoteMediaStateUpdatedListener;
    private Instance.OnSignalBarsUpdatedListener onSignalBarsUpdatedListener;
    private Instance.OnSignalingDataListener onSignalDataListener;
    private Instance.OnStateUpdatedListener onStateUpdatedListener;
    private String persistentStateFilePath;
    private CountDownLatch stopBarrier;

    public static native long createVideoCapturer(VideoSink videoSink, boolean z);

    public static native void destroyVideoCapturer(long j);

    private static native long makeNativeInstance(String str, NativeInstance nativeInstance, Instance.Config config, String str2, Instance.Endpoint[] endpointArr, Instance.Proxy proxy, int i, Instance.EncryptionKey encryptionKey, VideoSink videoSink, long j, float f);

    public static native void setVideoStateCapturer(long j, int i);

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

    public native void setAudioOutputGainControlEnabled(boolean z);

    public native void setBufferSize(int i);

    public native void setEchoCancellationStrength(int i);

    public native void setGlobalServerConfig(String str);

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
}
