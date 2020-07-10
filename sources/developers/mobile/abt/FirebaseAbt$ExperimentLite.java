package developers.mobile.abt;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Parser;

public final class FirebaseAbt$ExperimentLite extends GeneratedMessageLite<FirebaseAbt$ExperimentLite, Builder> implements Object {
    /* access modifiers changed from: private */
    public static final FirebaseAbt$ExperimentLite DEFAULT_INSTANCE;
    private static volatile Parser<FirebaseAbt$ExperimentLite> PARSER;

    private FirebaseAbt$ExperimentLite() {
    }

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
                return new Builder((FirebaseAbt$1) null);
            case 3:
                return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0000\u0000\u0001Ȉ", new Object[]{"experimentId_"});
            case 4:
                return DEFAULT_INSTANCE;
            case 5:
                Parser<FirebaseAbt$ExperimentLite> parser = PARSER;
                if (parser == null) {
                    synchronized (FirebaseAbt$ExperimentLite.class) {
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
        FirebaseAbt$ExperimentLite firebaseAbt$ExperimentLite = new FirebaseAbt$ExperimentLite();
        DEFAULT_INSTANCE = firebaseAbt$ExperimentLite;
        GeneratedMessageLite.registerDefaultInstance(FirebaseAbt$ExperimentLite.class, firebaseAbt$ExperimentLite);
    }
}
