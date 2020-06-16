package developers.mobile.abt;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;

/* compiled from: com.google.firebase:firebase-abt@@19.0.1 */
public final class FirebaseAbt$ExperimentLite extends GeneratedMessageLite<FirebaseAbt$ExperimentLite, Builder> implements Object {
    /* access modifiers changed from: private */
    public static final FirebaseAbt$ExperimentLite DEFAULT_INSTANCE;
    private static volatile Parser<FirebaseAbt$ExperimentLite> PARSER;
    private String experimentId_ = "";

    private FirebaseAbt$ExperimentLite() {
    }

    /* compiled from: com.google.firebase:firebase-abt@@19.0.1 */
    public static final class Builder extends GeneratedMessageLite.Builder<FirebaseAbt$ExperimentLite, Builder> implements Object {
        /* synthetic */ Builder(FirebaseAbt$1 firebaseAbt$1) {
            this();
        }

        private Builder() {
            super(FirebaseAbt$ExperimentLite.DEFAULT_INSTANCE);
        }
    }

    /* access modifiers changed from: protected */
    public final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (FirebaseAbt$1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new FirebaseAbt$ExperimentLite();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                return null;
            case 4:
                return new Builder((FirebaseAbt$1) null);
            case 5:
                FirebaseAbt$ExperimentLite firebaseAbt$ExperimentLite = (FirebaseAbt$ExperimentLite) obj2;
                this.experimentId_ = ((GeneratedMessageLite.Visitor) obj).visitString(!this.experimentId_.isEmpty(), this.experimentId_, true ^ firebaseAbt$ExperimentLite.experimentId_.isEmpty(), firebaseAbt$ExperimentLite.experimentId_);
                GeneratedMessageLite.MergeFromVisitor mergeFromVisitor = GeneratedMessageLite.MergeFromVisitor.INSTANCE;
                return this;
            case 6:
                CodedInputStream codedInputStream = (CodedInputStream) obj;
                ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                boolean z = false;
                while (!z) {
                    try {
                        int readTag = codedInputStream.readTag();
                        if (readTag != 0) {
                            if (readTag == 10) {
                                this.experimentId_ = codedInputStream.readStringRequireUtf8();
                            } else if (!codedInputStream.skipField(readTag)) {
                            }
                        }
                        z = true;
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
                    synchronized (FirebaseAbt$ExperimentLite.class) {
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
        FirebaseAbt$ExperimentLite firebaseAbt$ExperimentLite = new FirebaseAbt$ExperimentLite();
        DEFAULT_INSTANCE = firebaseAbt$ExperimentLite;
        firebaseAbt$ExperimentLite.makeImmutable();
    }

    public static Parser<FirebaseAbt$ExperimentLite> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
