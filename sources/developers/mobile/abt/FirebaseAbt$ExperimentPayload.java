package developers.mobile.abt;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

public final class FirebaseAbt$ExperimentPayload extends GeneratedMessageLite<FirebaseAbt$ExperimentPayload, Builder> implements Object {
    /* access modifiers changed from: private */
    public static final FirebaseAbt$ExperimentPayload DEFAULT_INSTANCE;
    private static volatile Parser<FirebaseAbt$ExperimentPayload> PARSER;
    private String experimentId_ = "";
    private long experimentStartTimeMillis_;
    private long timeToLiveMillis_;
    private String triggerEvent_ = "";
    private long triggerTimeoutMillis_;
    private String variantId_ = "";

    private FirebaseAbt$ExperimentPayload() {
        GeneratedMessageLite.emptyProtobufList();
    }

    public String getExperimentId() {
        return this.experimentId_;
    }

    public String getVariantId() {
        return this.variantId_;
    }

    public long getExperimentStartTimeMillis() {
        return this.experimentStartTimeMillis_;
    }

    public String getTriggerEvent() {
        return this.triggerEvent_;
    }

    public long getTriggerTimeoutMillis() {
        return this.triggerTimeoutMillis_;
    }

    public long getTimeToLiveMillis() {
        return this.timeToLiveMillis_;
    }

    public static FirebaseAbt$ExperimentPayload parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (FirebaseAbt$ExperimentPayload) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<FirebaseAbt$ExperimentPayload, Builder> implements Object {
        /* synthetic */ Builder(FirebaseAbt$1 firebaseAbt$1) {
            this();
        }

        private Builder() {
            super(FirebaseAbt$ExperimentPayload.DEFAULT_INSTANCE);
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (FirebaseAbt$1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new FirebaseAbt$ExperimentPayload();
            case 2:
                return new Builder((FirebaseAbt$1) null);
            case 3:
                return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0000\r\u0000\u0000\u0001\r\r\u0000\u0001\u0000\u0001Ȉ\u0002Ȉ\u0003\u0002\u0004Ȉ\u0005\u0002\u0006\u0002\u0007Ȉ\bȈ\tȈ\nȈ\u000bȈ\f\f\r\u001b", new Object[]{"experimentId_", "variantId_", "experimentStartTimeMillis_", "triggerEvent_", "triggerTimeoutMillis_", "timeToLiveMillis_", "setEventToLog_", "activateEventToLog_", "clearEventToLog_", "timeoutEventToLog_", "ttlExpiryEventToLog_", "overflowPolicy_", "ongoingExperiments_", FirebaseAbt$ExperimentLite.class});
            case 4:
                return DEFAULT_INSTANCE;
            case 5:
                Parser<FirebaseAbt$ExperimentPayload> parser = PARSER;
                if (parser == null) {
                    synchronized (FirebaseAbt$ExperimentPayload.class) {
                        parser = PARSER;
                        if (parser == null) {
                            parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                            PARSER = parser;
                        }
                    }
                }
                return parser;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    static {
        FirebaseAbt$ExperimentPayload firebaseAbt$ExperimentPayload = new FirebaseAbt$ExperimentPayload();
        DEFAULT_INSTANCE = firebaseAbt$ExperimentPayload;
        GeneratedMessageLite.registerDefaultInstance(FirebaseAbt$ExperimentPayload.class, firebaseAbt$ExperimentPayload);
    }
}
