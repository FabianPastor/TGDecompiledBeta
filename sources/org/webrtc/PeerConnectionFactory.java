package org.webrtc;

import android.content.Context;
import android.os.Process;
import java.util.List;
import org.webrtc.Logging;
import org.webrtc.PeerConnection;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

public class PeerConnectionFactory {
    private static final String TAG = "PeerConnectionFactory";
    public static final String TRIAL_ENABLED = "Enabled";
    private static final String VIDEO_CAPTURER_THREAD_NAME = "VideoCapturerThread";
    @Deprecated
    public static final String VIDEO_FRAME_EMIT_TRIAL = "VideoFrameEmit";
    private static volatile boolean internalTracerInitialized;
    private static ThreadInfo staticNetworkThread;
    private static ThreadInfo staticSignalingThread;
    private static ThreadInfo staticWorkerThread;
    private long nativeFactory;
    private volatile ThreadInfo networkThread;
    private volatile ThreadInfo signalingThread;
    private volatile ThreadInfo workerThread;

    private static native long nativeCreateAudioSource(long j, MediaConstraints mediaConstraints);

    private static native long nativeCreateAudioTrack(long j, String str, long j2);

    private static native long nativeCreateLocalMediaStream(long j, String str);

    private static native long nativeCreatePeerConnection(long j, PeerConnection.RTCConfiguration rTCConfiguration, MediaConstraints mediaConstraints, long j2, SSLCertificateVerifier sSLCertificateVerifier);

    /* access modifiers changed from: private */
    public static native PeerConnectionFactory nativeCreatePeerConnectionFactory(Context context, Options options, long j, long j2, long j3, VideoEncoderFactory videoEncoderFactory, VideoDecoderFactory videoDecoderFactory, long j4, long j5, long j6, long j7, long j8);

    private static native long nativeCreateVideoSource(long j, boolean z, boolean z2);

    private static native long nativeCreateVideoTrack(long j, String str, long j2);

    private static native void nativeDeleteLoggable();

    private static native String nativeFindFieldTrialsFullName(String str);

    private static native void nativeFreeFactory(long j);

    private static native long nativeGetNativePeerConnectionFactory(long j);

    private static native void nativeInitializeAndroidGlobals();

    private static native void nativeInitializeFieldTrials(String str);

    private static native void nativeInitializeInternalTracer();

    private static native void nativeInjectLoggable(JNILogging jNILogging, int i);

    private static native void nativePrintStackTrace(int i);

    private static native void nativePrintStackTracesOfRegisteredThreads();

    private static native void nativeShutdownInternalTracer();

    private static native boolean nativeStartAecDump(long j, int i, int i2);

    private static native boolean nativeStartInternalTracingCapture(String str);

    private static native void nativeStopAecDump(long j);

    private static native void nativeStopInternalTracingCapture();

    private static class ThreadInfo {
        final Thread thread;
        final int tid;

        public static ThreadInfo getCurrent() {
            return new ThreadInfo(Thread.currentThread(), Process.myTid());
        }

        private ThreadInfo(Thread thread2, int i) {
            this.thread = thread2;
            this.tid = i;
        }
    }

    public static class InitializationOptions {
        final Context applicationContext;
        final boolean enableInternalTracer;
        final String fieldTrials;
        Loggable loggable;
        Logging.Severity loggableSeverity;
        final String nativeLibraryName;

        private InitializationOptions(Context context, String str, boolean z, String str2, Loggable loggable2, Logging.Severity severity) {
            this.applicationContext = context;
            this.fieldTrials = str;
            this.enableInternalTracer = z;
            this.nativeLibraryName = str2;
            this.loggable = loggable2;
            this.loggableSeverity = severity;
        }

        public static Builder builder(Context context) {
            return new Builder(context);
        }

        public static class Builder {
            private final Context applicationContext;
            private boolean enableInternalTracer;
            private String fieldTrials = "";
            private Loggable loggable;
            private Logging.Severity loggableSeverity;
            private String nativeLibraryName = "jingle_peerconnection_so";

            Builder(Context context) {
                this.applicationContext = context;
            }

            public Builder setFieldTrials(String str) {
                this.fieldTrials = str;
                return this;
            }

            public Builder setEnableInternalTracer(boolean z) {
                this.enableInternalTracer = z;
                return this;
            }

            public Builder setNativeLibraryName(String str) {
                this.nativeLibraryName = str;
                return this;
            }

            public Builder setInjectableLogger(Loggable loggable2, Logging.Severity severity) {
                this.loggable = loggable2;
                this.loggableSeverity = severity;
                return this;
            }

            public InitializationOptions createInitializationOptions() {
                return new InitializationOptions(this.applicationContext, this.fieldTrials, this.enableInternalTracer, this.nativeLibraryName, this.loggable, this.loggableSeverity);
            }
        }
    }

    public static class Options {
        static final int ADAPTER_TYPE_ANY = 32;
        static final int ADAPTER_TYPE_CELLULAR = 4;
        static final int ADAPTER_TYPE_ETHERNET = 1;
        static final int ADAPTER_TYPE_LOOPBACK = 16;
        static final int ADAPTER_TYPE_UNKNOWN = 0;
        static final int ADAPTER_TYPE_VPN = 8;
        static final int ADAPTER_TYPE_WIFI = 2;
        public boolean disableEncryption;
        public boolean disableNetworkMonitor;
        public int networkIgnoreMask;

        /* access modifiers changed from: package-private */
        @CalledByNative("Options")
        public int getNetworkIgnoreMask() {
            return this.networkIgnoreMask;
        }

        /* access modifiers changed from: package-private */
        @CalledByNative("Options")
        public boolean getDisableEncryption() {
            return this.disableEncryption;
        }

        /* access modifiers changed from: package-private */
        @CalledByNative("Options")
        public boolean getDisableNetworkMonitor() {
            return this.disableNetworkMonitor;
        }
    }

    public static class Builder {
        private AudioDecoderFactoryFactory audioDecoderFactoryFactory;
        private AudioDeviceModule audioDeviceModule;
        private AudioEncoderFactoryFactory audioEncoderFactoryFactory;
        private AudioProcessingFactory audioProcessingFactory;
        private FecControllerFactoryFactoryInterface fecControllerFactoryFactory;
        private NetEqFactoryFactory neteqFactoryFactory;
        private NetworkControllerFactoryFactory networkControllerFactoryFactory;
        private NetworkStatePredictorFactoryFactory networkStatePredictorFactoryFactory;
        private Options options;
        private VideoDecoderFactory videoDecoderFactory;
        private VideoEncoderFactory videoEncoderFactory;

        private Builder() {
            this.audioEncoderFactoryFactory = new BuiltinAudioEncoderFactoryFactory();
            this.audioDecoderFactoryFactory = new BuiltinAudioDecoderFactoryFactory();
        }

        public Builder setOptions(Options options2) {
            this.options = options2;
            return this;
        }

        public Builder setAudioDeviceModule(AudioDeviceModule audioDeviceModule2) {
            this.audioDeviceModule = audioDeviceModule2;
            return this;
        }

        public Builder setAudioEncoderFactoryFactory(AudioEncoderFactoryFactory audioEncoderFactoryFactory2) {
            if (audioEncoderFactoryFactory2 != null) {
                this.audioEncoderFactoryFactory = audioEncoderFactoryFactory2;
                return this;
            }
            throw new IllegalArgumentException("PeerConnectionFactory.Builder does not accept a null AudioEncoderFactoryFactory.");
        }

        public Builder setAudioDecoderFactoryFactory(AudioDecoderFactoryFactory audioDecoderFactoryFactory2) {
            if (audioDecoderFactoryFactory2 != null) {
                this.audioDecoderFactoryFactory = audioDecoderFactoryFactory2;
                return this;
            }
            throw new IllegalArgumentException("PeerConnectionFactory.Builder does not accept a null AudioDecoderFactoryFactory.");
        }

        public Builder setVideoEncoderFactory(VideoEncoderFactory videoEncoderFactory2) {
            this.videoEncoderFactory = videoEncoderFactory2;
            return this;
        }

        public Builder setVideoDecoderFactory(VideoDecoderFactory videoDecoderFactory2) {
            this.videoDecoderFactory = videoDecoderFactory2;
            return this;
        }

        public Builder setAudioProcessingFactory(AudioProcessingFactory audioProcessingFactory2) {
            if (audioProcessingFactory2 != null) {
                this.audioProcessingFactory = audioProcessingFactory2;
                return this;
            }
            throw new NullPointerException("PeerConnectionFactory builder does not accept a null AudioProcessingFactory.");
        }

        public Builder setFecControllerFactoryFactoryInterface(FecControllerFactoryFactoryInterface fecControllerFactoryFactoryInterface) {
            this.fecControllerFactoryFactory = fecControllerFactoryFactoryInterface;
            return this;
        }

        public Builder setNetworkControllerFactoryFactory(NetworkControllerFactoryFactory networkControllerFactoryFactory2) {
            this.networkControllerFactoryFactory = networkControllerFactoryFactory2;
            return this;
        }

        public Builder setNetworkStatePredictorFactoryFactory(NetworkStatePredictorFactoryFactory networkStatePredictorFactoryFactory2) {
            this.networkStatePredictorFactoryFactory = networkStatePredictorFactoryFactory2;
            return this;
        }

        public Builder setNetEqFactoryFactory(NetEqFactoryFactory netEqFactoryFactory) {
            this.neteqFactoryFactory = netEqFactoryFactory;
            return this;
        }

        public PeerConnectionFactory createPeerConnectionFactory() {
            long j;
            long j2;
            PeerConnectionFactory.checkInitializeHasBeenCalled();
            if (this.audioDeviceModule == null) {
                this.audioDeviceModule = JavaAudioDeviceModule.builder(ContextUtils.getApplicationContext()).createAudioDeviceModule();
            }
            Context applicationContext = ContextUtils.getApplicationContext();
            Options options2 = this.options;
            long nativeAudioDeviceModulePointer = this.audioDeviceModule.getNativeAudioDeviceModulePointer();
            long createNativeAudioEncoderFactory = this.audioEncoderFactoryFactory.createNativeAudioEncoderFactory();
            long createNativeAudioDecoderFactory = this.audioDecoderFactoryFactory.createNativeAudioDecoderFactory();
            VideoEncoderFactory videoEncoderFactory2 = this.videoEncoderFactory;
            VideoDecoderFactory videoDecoderFactory2 = this.videoDecoderFactory;
            AudioProcessingFactory audioProcessingFactory2 = this.audioProcessingFactory;
            long j3 = 0;
            long createNative = audioProcessingFactory2 == null ? 0 : audioProcessingFactory2.createNative();
            FecControllerFactoryFactoryInterface fecControllerFactoryFactoryInterface = this.fecControllerFactoryFactory;
            long createNative2 = fecControllerFactoryFactoryInterface == null ? 0 : fecControllerFactoryFactoryInterface.createNative();
            NetworkControllerFactoryFactory networkControllerFactoryFactory2 = this.networkControllerFactoryFactory;
            if (networkControllerFactoryFactory2 == null) {
                j = 0;
            } else {
                j = networkControllerFactoryFactory2.createNativeNetworkControllerFactory();
            }
            NetworkStatePredictorFactoryFactory networkStatePredictorFactoryFactory2 = this.networkStatePredictorFactoryFactory;
            if (networkStatePredictorFactoryFactory2 == null) {
                j2 = 0;
            } else {
                j2 = networkStatePredictorFactoryFactory2.createNativeNetworkStatePredictorFactory();
            }
            NetEqFactoryFactory netEqFactoryFactory = this.neteqFactoryFactory;
            if (netEqFactoryFactory != null) {
                j3 = netEqFactoryFactory.createNativeNetEqFactory();
            }
            return PeerConnectionFactory.nativeCreatePeerConnectionFactory(applicationContext, options2, nativeAudioDeviceModulePointer, createNativeAudioEncoderFactory, createNativeAudioDecoderFactory, videoEncoderFactory2, videoDecoderFactory2, createNative, createNative2, j, j2, j3);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static void initialize(InitializationOptions initializationOptions) {
        ContextUtils.initialize(initializationOptions.applicationContext);
        nativeInitializeAndroidGlobals();
        nativeInitializeFieldTrials(initializationOptions.fieldTrials);
        if (initializationOptions.enableInternalTracer && !internalTracerInitialized) {
            initializeInternalTracer();
        }
        Loggable loggable = initializationOptions.loggable;
        if (loggable != null) {
            Logging.injectLoggable(loggable, initializationOptions.loggableSeverity);
            nativeInjectLoggable(new JNILogging(initializationOptions.loggable), initializationOptions.loggableSeverity.ordinal());
            return;
        }
        Logging.d("PeerConnectionFactory", "PeerConnectionFactory was initialized without an injected Loggable. Any existing Loggable will be deleted.");
        Logging.deleteInjectedLoggable();
        nativeDeleteLoggable();
    }

    /* access modifiers changed from: private */
    public static void checkInitializeHasBeenCalled() {
        if (ContextUtils.getApplicationContext() == null) {
            throw new IllegalStateException("PeerConnectionFactory.initialize was not called before creating a PeerConnectionFactory.");
        }
    }

    private static void initializeInternalTracer() {
        internalTracerInitialized = true;
        nativeInitializeInternalTracer();
    }

    public static void shutdownInternalTracer() {
        internalTracerInitialized = false;
        nativeShutdownInternalTracer();
    }

    @Deprecated
    public static void initializeFieldTrials(String str) {
        nativeInitializeFieldTrials(str);
    }

    public static String fieldTrialsFindFullName(String str) {
        return nativeFindFieldTrialsFullName(str);
    }

    public static boolean startInternalTracingCapture(String str) {
        return nativeStartInternalTracingCapture(str);
    }

    public static void stopInternalTracingCapture() {
        nativeStopInternalTracingCapture();
    }

    @CalledByNative
    PeerConnectionFactory(long j) {
        checkInitializeHasBeenCalled();
        if (j != 0) {
            this.nativeFactory = j;
            return;
        }
        throw new RuntimeException("Failed to initialize PeerConnectionFactory!");
    }

    /* access modifiers changed from: package-private */
    public PeerConnection createPeerConnectionInternal(PeerConnection.RTCConfiguration rTCConfiguration, MediaConstraints mediaConstraints, PeerConnection.Observer observer, SSLCertificateVerifier sSLCertificateVerifier) {
        checkPeerConnectionFactoryExists();
        long createNativePeerConnectionObserver = PeerConnection.createNativePeerConnectionObserver(observer);
        if (createNativePeerConnectionObserver == 0) {
            return null;
        }
        long nativeCreatePeerConnection = nativeCreatePeerConnection(this.nativeFactory, rTCConfiguration, mediaConstraints, createNativePeerConnectionObserver, sSLCertificateVerifier);
        if (nativeCreatePeerConnection == 0) {
            return null;
        }
        return new PeerConnection(nativeCreatePeerConnection);
    }

    @Deprecated
    public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rTCConfiguration, MediaConstraints mediaConstraints, PeerConnection.Observer observer) {
        return createPeerConnectionInternal(rTCConfiguration, mediaConstraints, observer, (SSLCertificateVerifier) null);
    }

    @Deprecated
    public PeerConnection createPeerConnection(List<PeerConnection.IceServer> list, MediaConstraints mediaConstraints, PeerConnection.Observer observer) {
        return createPeerConnection(new PeerConnection.RTCConfiguration(list), mediaConstraints, observer);
    }

    public PeerConnection createPeerConnection(List<PeerConnection.IceServer> list, PeerConnection.Observer observer) {
        return createPeerConnection(new PeerConnection.RTCConfiguration(list), observer);
    }

    public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rTCConfiguration, PeerConnection.Observer observer) {
        return createPeerConnection(rTCConfiguration, (MediaConstraints) null, observer);
    }

    public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rTCConfiguration, PeerConnectionDependencies peerConnectionDependencies) {
        return createPeerConnectionInternal(rTCConfiguration, (MediaConstraints) null, peerConnectionDependencies.getObserver(), peerConnectionDependencies.getSSLCertificateVerifier());
    }

    public MediaStream createLocalMediaStream(String str) {
        checkPeerConnectionFactoryExists();
        return new MediaStream(nativeCreateLocalMediaStream(this.nativeFactory, str));
    }

    public VideoSource createVideoSource(boolean z, boolean z2) {
        checkPeerConnectionFactoryExists();
        return new VideoSource(nativeCreateVideoSource(this.nativeFactory, z, z2));
    }

    public VideoSource createVideoSource(boolean z) {
        return createVideoSource(z, true);
    }

    public VideoTrack createVideoTrack(String str, VideoSource videoSource) {
        checkPeerConnectionFactoryExists();
        return new VideoTrack(nativeCreateVideoTrack(this.nativeFactory, str, videoSource.getNativeVideoTrackSource()));
    }

    public AudioSource createAudioSource(MediaConstraints mediaConstraints) {
        checkPeerConnectionFactoryExists();
        return new AudioSource(nativeCreateAudioSource(this.nativeFactory, mediaConstraints));
    }

    public AudioTrack createAudioTrack(String str, AudioSource audioSource) {
        checkPeerConnectionFactoryExists();
        return new AudioTrack(nativeCreateAudioTrack(this.nativeFactory, str, audioSource.getNativeAudioSource()));
    }

    public boolean startAecDump(int i, int i2) {
        checkPeerConnectionFactoryExists();
        return nativeStartAecDump(this.nativeFactory, i, i2);
    }

    public void stopAecDump() {
        checkPeerConnectionFactoryExists();
        nativeStopAecDump(this.nativeFactory);
    }

    public void dispose() {
        checkPeerConnectionFactoryExists();
        nativeFreeFactory(this.nativeFactory);
        this.networkThread = null;
        this.workerThread = null;
        this.signalingThread = null;
        this.nativeFactory = 0;
    }

    public long getNativePeerConnectionFactory() {
        checkPeerConnectionFactoryExists();
        return nativeGetNativePeerConnectionFactory(this.nativeFactory);
    }

    public long getNativeOwnedFactoryAndThreads() {
        checkPeerConnectionFactoryExists();
        return this.nativeFactory;
    }

    private void checkPeerConnectionFactoryExists() {
        if (this.nativeFactory == 0) {
            throw new IllegalStateException("PeerConnectionFactory has been disposed.");
        }
    }

    private static void printStackTrace(ThreadInfo threadInfo, boolean z) {
        if (threadInfo != null) {
            String name = threadInfo.thread.getName();
            StackTraceElement[] stackTrace = threadInfo.thread.getStackTrace();
            if (stackTrace.length > 0) {
                Logging.w("PeerConnectionFactory", name + " stacktrace:");
                for (StackTraceElement stackTraceElement : stackTrace) {
                    Logging.w("PeerConnectionFactory", stackTraceElement.toString());
                }
            }
            if (z) {
                Logging.w("PeerConnectionFactory", "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***");
                Logging.w("PeerConnectionFactory", "pid: " + Process.myPid() + ", tid: " + threadInfo.tid + ", name: " + name + "  >>> WebRTC <<<");
                nativePrintStackTrace(threadInfo.tid);
            }
        }
    }

    @Deprecated
    public static void printStackTraces() {
        printStackTrace(staticNetworkThread, false);
        printStackTrace(staticWorkerThread, false);
        printStackTrace(staticSignalingThread, false);
    }

    public void printInternalStackTraces(boolean z) {
        printStackTrace(this.signalingThread, z);
        printStackTrace(this.workerThread, z);
        printStackTrace(this.networkThread, z);
        if (z) {
            nativePrintStackTracesOfRegisteredThreads();
        }
    }

    @CalledByNative
    private void onNetworkThreadReady() {
        this.networkThread = ThreadInfo.getCurrent();
        staticNetworkThread = this.networkThread;
        Logging.d("PeerConnectionFactory", "onNetworkThreadReady");
    }

    @CalledByNative
    private void onWorkerThreadReady() {
        this.workerThread = ThreadInfo.getCurrent();
        staticWorkerThread = this.workerThread;
        Logging.d("PeerConnectionFactory", "onWorkerThreadReady");
    }

    @CalledByNative
    private void onSignalingThreadReady() {
        this.signalingThread = ThreadInfo.getCurrent();
        staticSignalingThread = this.signalingThread;
        Logging.d("PeerConnectionFactory", "onSignalingThreadReady");
    }
}
