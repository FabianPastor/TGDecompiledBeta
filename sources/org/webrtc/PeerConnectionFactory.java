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

        private ThreadInfo(Thread thread2, int tid2) {
            this.thread = thread2;
            this.tid = tid2;
        }
    }

    public static class InitializationOptions {
        final Context applicationContext;
        final boolean enableInternalTracer;
        final String fieldTrials;
        Loggable loggable;
        Logging.Severity loggableSeverity;
        final String nativeLibraryName;

        private InitializationOptions(Context applicationContext2, String fieldTrials2, boolean enableInternalTracer2, String nativeLibraryName2, Loggable loggable2, Logging.Severity loggableSeverity2) {
            this.applicationContext = applicationContext2;
            this.fieldTrials = fieldTrials2;
            this.enableInternalTracer = enableInternalTracer2;
            this.nativeLibraryName = nativeLibraryName2;
            this.loggable = loggable2;
            this.loggableSeverity = loggableSeverity2;
        }

        public static Builder builder(Context applicationContext2) {
            return new Builder(applicationContext2);
        }

        public static class Builder {
            private final Context applicationContext;
            private boolean enableInternalTracer;
            private String fieldTrials = "";
            private Loggable loggable;
            private Logging.Severity loggableSeverity;
            private String nativeLibraryName = "jingle_peerconnection_so";

            Builder(Context applicationContext2) {
                this.applicationContext = applicationContext2;
            }

            public Builder setFieldTrials(String fieldTrials2) {
                this.fieldTrials = fieldTrials2;
                return this;
            }

            public Builder setEnableInternalTracer(boolean enableInternalTracer2) {
                this.enableInternalTracer = enableInternalTracer2;
                return this;
            }

            public Builder setNativeLibraryName(String nativeLibraryName2) {
                this.nativeLibraryName = nativeLibraryName2;
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
        public int getNetworkIgnoreMask() {
            return this.networkIgnoreMask;
        }

        /* access modifiers changed from: package-private */
        public boolean getDisableEncryption() {
            return this.disableEncryption;
        }

        /* access modifiers changed from: package-private */
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

        public Builder setFecControllerFactoryFactoryInterface(FecControllerFactoryFactoryInterface fecControllerFactoryFactory2) {
            this.fecControllerFactoryFactory = fecControllerFactoryFactory2;
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

        public Builder setNetEqFactoryFactory(NetEqFactoryFactory neteqFactoryFactory2) {
            this.neteqFactoryFactory = neteqFactoryFactory2;
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

    public static void initialize(InitializationOptions options) {
        ContextUtils.initialize(options.applicationContext);
        nativeInitializeAndroidGlobals();
        nativeInitializeFieldTrials(options.fieldTrials);
        if (options.enableInternalTracer && !internalTracerInitialized) {
            initializeInternalTracer();
        }
        if (options.loggable != null) {
            Logging.injectLoggable(options.loggable, options.loggableSeverity);
            nativeInjectLoggable(new JNILogging(options.loggable), options.loggableSeverity.ordinal());
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
    public static void initializeFieldTrials(String fieldTrialsInitString) {
        nativeInitializeFieldTrials(fieldTrialsInitString);
    }

    public static String fieldTrialsFindFullName(String name) {
        return nativeFindFieldTrialsFullName(name);
    }

    public static boolean startInternalTracingCapture(String tracingFilename) {
        return nativeStartInternalTracingCapture(tracingFilename);
    }

    public static void stopInternalTracingCapture() {
        nativeStopInternalTracingCapture();
    }

    PeerConnectionFactory(long nativeFactory2) {
        checkInitializeHasBeenCalled();
        if (nativeFactory2 != 0) {
            this.nativeFactory = nativeFactory2;
            return;
        }
        throw new RuntimeException("Failed to initialize PeerConnectionFactory!");
    }

    /* access modifiers changed from: package-private */
    public PeerConnection createPeerConnectionInternal(PeerConnection.RTCConfiguration rtcConfig, MediaConstraints constraints, PeerConnection.Observer observer, SSLCertificateVerifier sslCertificateVerifier) {
        checkPeerConnectionFactoryExists();
        long nativeObserver = PeerConnection.createNativePeerConnectionObserver(observer);
        if (nativeObserver == 0) {
            return null;
        }
        long nativePeerConnection = nativeCreatePeerConnection(this.nativeFactory, rtcConfig, constraints, nativeObserver, sslCertificateVerifier);
        if (nativePeerConnection == 0) {
            return null;
        }
        return new PeerConnection(nativePeerConnection);
    }

    @Deprecated
    public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, MediaConstraints constraints, PeerConnection.Observer observer) {
        return createPeerConnectionInternal(rtcConfig, constraints, observer, (SSLCertificateVerifier) null);
    }

    @Deprecated
    public PeerConnection createPeerConnection(List<PeerConnection.IceServer> iceServers, MediaConstraints constraints, PeerConnection.Observer observer) {
        return createPeerConnection(new PeerConnection.RTCConfiguration(iceServers), constraints, observer);
    }

    public PeerConnection createPeerConnection(List<PeerConnection.IceServer> iceServers, PeerConnection.Observer observer) {
        return createPeerConnection(new PeerConnection.RTCConfiguration(iceServers), observer);
    }

    public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, PeerConnection.Observer observer) {
        return createPeerConnection(rtcConfig, (MediaConstraints) null, observer);
    }

    public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, PeerConnectionDependencies dependencies) {
        return createPeerConnectionInternal(rtcConfig, (MediaConstraints) null, dependencies.getObserver(), dependencies.getSSLCertificateVerifier());
    }

    public MediaStream createLocalMediaStream(String label) {
        checkPeerConnectionFactoryExists();
        return new MediaStream(nativeCreateLocalMediaStream(this.nativeFactory, label));
    }

    public VideoSource createVideoSource(boolean isScreencast, boolean alignTimestamps) {
        checkPeerConnectionFactoryExists();
        return new VideoSource(nativeCreateVideoSource(this.nativeFactory, isScreencast, alignTimestamps));
    }

    public VideoSource createVideoSource(boolean isScreencast) {
        return createVideoSource(isScreencast, true);
    }

    public VideoTrack createVideoTrack(String id, VideoSource source) {
        checkPeerConnectionFactoryExists();
        return new VideoTrack(nativeCreateVideoTrack(this.nativeFactory, id, source.getNativeVideoTrackSource()));
    }

    public AudioSource createAudioSource(MediaConstraints constraints) {
        checkPeerConnectionFactoryExists();
        return new AudioSource(nativeCreateAudioSource(this.nativeFactory, constraints));
    }

    public AudioTrack createAudioTrack(String id, AudioSource source) {
        checkPeerConnectionFactoryExists();
        return new AudioTrack(nativeCreateAudioTrack(this.nativeFactory, id, source.getNativeAudioSource()));
    }

    public boolean startAecDump(int file_descriptor, int filesize_limit_bytes) {
        checkPeerConnectionFactoryExists();
        return nativeStartAecDump(this.nativeFactory, file_descriptor, filesize_limit_bytes);
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

    private static void printStackTrace(ThreadInfo threadInfo, boolean printNativeStackTrace) {
        if (threadInfo != null) {
            String threadName = threadInfo.thread.getName();
            StackTraceElement[] stackTraces = threadInfo.thread.getStackTrace();
            if (stackTraces.length > 0) {
                Logging.w("PeerConnectionFactory", threadName + " stacktrace:");
                for (StackTraceElement stackTrace : stackTraces) {
                    Logging.w("PeerConnectionFactory", stackTrace.toString());
                }
            }
            if (printNativeStackTrace) {
                Logging.w("PeerConnectionFactory", "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***");
                Logging.w("PeerConnectionFactory", "pid: " + Process.myPid() + ", tid: " + threadInfo.tid + ", name: " + threadName + "  >>> WebRTC <<<");
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

    public void printInternalStackTraces(boolean printNativeStackTraces) {
        printStackTrace(this.signalingThread, printNativeStackTraces);
        printStackTrace(this.workerThread, printNativeStackTraces);
        printStackTrace(this.networkThread, printNativeStackTraces);
        if (printNativeStackTraces) {
            nativePrintStackTracesOfRegisteredThreads();
        }
    }

    private void onNetworkThreadReady() {
        this.networkThread = ThreadInfo.getCurrent();
        staticNetworkThread = this.networkThread;
        Logging.d("PeerConnectionFactory", "onNetworkThreadReady");
    }

    private void onWorkerThreadReady() {
        this.workerThread = ThreadInfo.getCurrent();
        staticWorkerThread = this.workerThread;
        Logging.d("PeerConnectionFactory", "onWorkerThreadReady");
    }

    private void onSignalingThreadReady() {
        this.signalingThread = ThreadInfo.getCurrent();
        staticSignalingThread = this.signalingThread;
        Logging.d("PeerConnectionFactory", "onSignalingThreadReady");
    }
}
