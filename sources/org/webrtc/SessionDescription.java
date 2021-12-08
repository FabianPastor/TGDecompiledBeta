package org.webrtc;

import java.util.Locale;

public class SessionDescription {
    public final String description;
    public final Type type;

    public enum Type {
        OFFER,
        PRANSWER,
        ANSWER,
        ROLLBACK;

        public String canonicalForm() {
            return name().toLowerCase(Locale.US);
        }

        public static Type fromCanonicalForm(String canonical) {
            return (Type) valueOf(Type.class, canonical.toUpperCase(Locale.US));
        }
    }

    public SessionDescription(Type type2, String description2) {
        this.type = type2;
        this.description = description2;
    }

    /* access modifiers changed from: package-private */
    public String getDescription() {
        return this.description;
    }

    /* access modifiers changed from: package-private */
    public String getTypeInCanonicalForm() {
        return this.type.canonicalForm();
    }
}
