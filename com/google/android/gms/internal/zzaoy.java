package com.google.android.gms.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class zzaoy {
    public Number aT() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public String aU() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public boolean aV() {
        return this instanceof zzaov;
    }

    public boolean aW() {
        return this instanceof zzapb;
    }

    public boolean aX() {
        return this instanceof zzape;
    }

    public boolean aY() {
        return this instanceof zzapa;
    }

    public zzapb aZ() {
        if (aW()) {
            return (zzapb) this;
        }
        String valueOf = String.valueOf(this);
        throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 19).append("Not a JSON Object: ").append(valueOf).toString());
    }

    public zzaov ba() {
        if (aV()) {
            return (zzaov) this;
        }
        throw new IllegalStateException("This is not a JSON Array.");
    }

    public zzape bb() {
        if (aX()) {
            return (zzape) this;
        }
        throw new IllegalStateException("This is not a JSON Primitive.");
    }

    Boolean bc() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public boolean getAsBoolean() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public double getAsDouble() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public int getAsInt() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public long getAsLong() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public String toString() {
        try {
            Writer stringWriter = new StringWriter();
            zzaqr com_google_android_gms_internal_zzaqr = new zzaqr(stringWriter);
            com_google_android_gms_internal_zzaqr.setLenient(true);
            zzapz.zzb(this, com_google_android_gms_internal_zzaqr);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
