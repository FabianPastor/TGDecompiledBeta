package developers.mobile.abt;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.GeneratedMessageLite.MethodToInvoke;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;

/* compiled from: com.google.firebase:firebase-abt@@19.0.0 */
public final class FirebaseAbt$ExperimentLite extends GeneratedMessageLite<FirebaseAbt$ExperimentLite, Builder> implements FirebaseAbt$ExperimentLiteOrBuilder {
    private static final FirebaseAbt$ExperimentLite DEFAULT_INSTANCE = new FirebaseAbt$ExperimentLite();
    private static volatile Parser<FirebaseAbt$ExperimentLite> PARSER;
    private String experimentId_ = "";

    /* compiled from: com.google.firebase:firebase-abt@@19.0.0 */
    public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder<FirebaseAbt$ExperimentLite, Builder> implements FirebaseAbt$ExperimentLiteOrBuilder {
        /* synthetic */ Builder(FirebaseAbt$1 firebaseAbt$1) {
            this();
        }

        private Builder() {
            super(FirebaseAbt$ExperimentLite.DEFAULT_INSTANCE);
        }
    }

    private FirebaseAbt$ExperimentLite() {
    }

    /* Access modifiers changed, original: protected|final */
    public final Object dynamicMethod(MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (FirebaseAbt$1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new FirebaseAbt$ExperimentLite();
            case 2:
                return DEFAULT_INSTANCE;
            case 3:
                return null;
            case 4:
                return new Builder();
            case 5:
                FirebaseAbt$ExperimentLite firebaseAbt$ExperimentLite = (FirebaseAbt$ExperimentLite) obj2;
                this.experimentId_ = ((Visitor) obj).visitString(this.experimentId_.isEmpty() ^ 1, this.experimentId_, 1 ^ firebaseAbt$ExperimentLite.experimentId_.isEmpty(), firebaseAbt$ExperimentLite.experimentId_);
                MergeFromVisitor mergeFromVisitor = MergeFromVisitor.INSTANCE;
                return this;
            case 6:
                CodedInputStream codedInputStream = (CodedInputStream) obj;
                ExtensionRegistryLite extensionRegistryLite = (ExtensionRegistryLite) obj2;
                Object obj3 = null;
                while (obj3 == null) {
                    try {
                        int readTag = codedInputStream.readTag();
                        if (readTag != 0) {
                            if (readTag == 10) {
                                this.experimentId_ = codedInputStream.readStringRequireUtf8();
                            } else if (codedInputStream.skipField(readTag)) {
                            }
                        }
                        obj3 = 1;
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
                            PARSER = new DefaultInstanceBasedParser(DEFAULT_INSTANCE);
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

    public static Parser<FirebaseAbt$ExperimentLite> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
