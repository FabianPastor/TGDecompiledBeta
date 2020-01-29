package developers.mobile.abt;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;

/* compiled from: com.google.firebase:firebase-abt@@19.0.0 */
public final class FirebaseAbt$ExperimentPayload extends GeneratedMessageLite<FirebaseAbt$ExperimentPayload, Builder> implements FirebaseAbt$ExperimentPayloadOrBuilder {
    /* access modifiers changed from: private */
    public static final FirebaseAbt$ExperimentPayload DEFAULT_INSTANCE = new FirebaseAbt$ExperimentPayload();
    private static volatile Parser<FirebaseAbt$ExperimentPayload> PARSER;
    private String activateEventToLog_ = "";
    private int bitField0_;
    private String clearEventToLog_ = "";
    private String experimentId_ = "";
    private long experimentStartTimeMillis_;
    private Internal.ProtobufList<FirebaseAbt$ExperimentLite> ongoingExperiments_ = GeneratedMessageLite.emptyProtobufList();
    private int overflowPolicy_;
    private String setEventToLog_ = "";
    private long timeToLiveMillis_;
    private String timeoutEventToLog_ = "";
    private String triggerEvent_ = "";
    private long triggerTimeoutMillis_;
    private String ttlExpiryEventToLog_ = "";
    private String variantId_ = "";

    private FirebaseAbt$ExperimentPayload() {
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

    /* compiled from: com.google.firebase:firebase-abt@@19.0.0 */
    public static final class Builder extends GeneratedMessageLite.Builder<FirebaseAbt$ExperimentPayload, Builder> implements FirebaseAbt$ExperimentPayloadOrBuilder {
        /* synthetic */ Builder(FirebaseAbt$1 firebaseAbt$1) {
            this();
        }

        private Builder() {
            super(FirebaseAbt$ExperimentPayload.DEFAULT_INSTANCE);
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        boolean z = false;
        switch (FirebaseAbt$1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new FirebaseAbt$ExperimentPayload();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                this.ongoingExperiments_.makeImmutable();
                return null;
            case 4:
                return new Builder((FirebaseAbt$1) null);
            case 5:
                GeneratedMessageLite.Visitor visitor = (GeneratedMessageLite.Visitor) obj;
                FirebaseAbt$ExperimentPayload firebaseAbt$ExperimentPayload = (FirebaseAbt$ExperimentPayload) obj2;
                this.experimentId_ = visitor.visitString(!this.experimentId_.isEmpty(), this.experimentId_, !firebaseAbt$ExperimentPayload.experimentId_.isEmpty(), firebaseAbt$ExperimentPayload.experimentId_);
                this.variantId_ = visitor.visitString(!this.variantId_.isEmpty(), this.variantId_, !firebaseAbt$ExperimentPayload.variantId_.isEmpty(), firebaseAbt$ExperimentPayload.variantId_);
                this.experimentStartTimeMillis_ = visitor.visitLong(this.experimentStartTimeMillis_ != 0, this.experimentStartTimeMillis_, firebaseAbt$ExperimentPayload.experimentStartTimeMillis_ != 0, firebaseAbt$ExperimentPayload.experimentStartTimeMillis_);
                this.triggerEvent_ = visitor.visitString(!this.triggerEvent_.isEmpty(), this.triggerEvent_, !firebaseAbt$ExperimentPayload.triggerEvent_.isEmpty(), firebaseAbt$ExperimentPayload.triggerEvent_);
                this.triggerTimeoutMillis_ = visitor.visitLong(this.triggerTimeoutMillis_ != 0, this.triggerTimeoutMillis_, firebaseAbt$ExperimentPayload.triggerTimeoutMillis_ != 0, firebaseAbt$ExperimentPayload.triggerTimeoutMillis_);
                this.timeToLiveMillis_ = visitor.visitLong(this.timeToLiveMillis_ != 0, this.timeToLiveMillis_, firebaseAbt$ExperimentPayload.timeToLiveMillis_ != 0, firebaseAbt$ExperimentPayload.timeToLiveMillis_);
                this.setEventToLog_ = visitor.visitString(!this.setEventToLog_.isEmpty(), this.setEventToLog_, !firebaseAbt$ExperimentPayload.setEventToLog_.isEmpty(), firebaseAbt$ExperimentPayload.setEventToLog_);
                this.activateEventToLog_ = visitor.visitString(!this.activateEventToLog_.isEmpty(), this.activateEventToLog_, !firebaseAbt$ExperimentPayload.activateEventToLog_.isEmpty(), firebaseAbt$ExperimentPayload.activateEventToLog_);
                this.clearEventToLog_ = visitor.visitString(!this.clearEventToLog_.isEmpty(), this.clearEventToLog_, !firebaseAbt$ExperimentPayload.clearEventToLog_.isEmpty(), firebaseAbt$ExperimentPayload.clearEventToLog_);
                this.timeoutEventToLog_ = visitor.visitString(!this.timeoutEventToLog_.isEmpty(), this.timeoutEventToLog_, !firebaseAbt$ExperimentPayload.timeoutEventToLog_.isEmpty(), firebaseAbt$ExperimentPayload.timeoutEventToLog_);
                this.ttlExpiryEventToLog_ = visitor.visitString(!this.ttlExpiryEventToLog_.isEmpty(), this.ttlExpiryEventToLog_, !firebaseAbt$ExperimentPayload.ttlExpiryEventToLog_.isEmpty(), firebaseAbt$ExperimentPayload.ttlExpiryEventToLog_);
                boolean z2 = this.overflowPolicy_ != 0;
                int i = this.overflowPolicy_;
                if (firebaseAbt$ExperimentPayload.overflowPolicy_ != 0) {
                    z = true;
                }
                this.overflowPolicy_ = visitor.visitInt(z2, i, z, firebaseAbt$ExperimentPayload.overflowPolicy_);
                this.ongoingExperiments_ = visitor.visitList(this.ongoingExperiments_, firebaseAbt$ExperimentPayload.ongoingExperiments_);
                if (visitor == GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
                    this.bitField0_ |= firebaseAbt$ExperimentPayload.bitField0_;
                }
                return this;
            case 6:
                CodedInputStream codedInputStream = (CodedInputStream) obj;
                ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                while (!z) {
                    try {
                        int readTag = codedInputStream.readTag();
                        switch (readTag) {
                            case 0:
                                z = true;
                                break;
                            case 10:
                                this.experimentId_ = codedInputStream.readStringRequireUtf8();
                                break;
                            case 18:
                                this.variantId_ = codedInputStream.readStringRequireUtf8();
                                break;
                            case 24:
                                this.experimentStartTimeMillis_ = codedInputStream.readInt64();
                                break;
                            case 34:
                                this.triggerEvent_ = codedInputStream.readStringRequireUtf8();
                                break;
                            case 40:
                                this.triggerTimeoutMillis_ = codedInputStream.readInt64();
                                break;
                            case 48:
                                this.timeToLiveMillis_ = codedInputStream.readInt64();
                                break;
                            case 58:
                                this.setEventToLog_ = codedInputStream.readStringRequireUtf8();
                                break;
                            case 66:
                                this.activateEventToLog_ = codedInputStream.readStringRequireUtf8();
                                break;
                            case 74:
                                this.clearEventToLog_ = codedInputStream.readStringRequireUtf8();
                                break;
                            case 82:
                                this.timeoutEventToLog_ = codedInputStream.readStringRequireUtf8();
                                break;
                            case 90:
                                this.ttlExpiryEventToLog_ = codedInputStream.readStringRequireUtf8();
                                break;
                            case 96:
                                this.overflowPolicy_ = codedInputStream.readEnum();
                                break;
                            case 106:
                                if (!this.ongoingExperiments_.isModifiable()) {
                                    this.ongoingExperiments_ = GeneratedMessageLite.mutableCopy(this.ongoingExperiments_);
                                }
                                this.ongoingExperiments_.add((FirebaseAbt$ExperimentLite) codedInputStream.readMessage(FirebaseAbt$ExperimentLite.parser(), extensionRegistryLite));
                                break;
                            default:
                                if (codedInputStream.skipField(readTag)) {
                                    break;
                                }
                                z = true;
                                break;
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.setUnfinishedMessage(this);
                        throw new RuntimeException(e);
                    } catch (IOException e2) {
                        InvalidProtocolBufferException invalidProtocolBufferException = new InvalidProtocolBufferException(e2.getMessage());
                        invalidProtocolBufferException.setUnfinishedMessage(this);
                        throw new RuntimeException(invalidProtocolBufferException);
                    }
                }
                break;
            case 7:
                break;
            case 8:
                if (PARSER == null) {
                    synchronized (FirebaseAbt$ExperimentPayload.class) {
                        if (PARSER == null) {
                            PARSER = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                        }
                    }
                }
                return PARSER;
            default:
                throw new UnsupportedOperationException();
        }
        return DEFAULT_INSTANCE;
    }

    static {
        DEFAULT_INSTANCE.makeImmutable();
    }
}
