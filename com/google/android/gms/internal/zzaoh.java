package com.google.android.gms.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class zzaoh {
    public Number aQ() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public String aR() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public boolean aS() {
        return this instanceof zzaoe;
    }

    public boolean aT() {
        return this instanceof zzaok;
    }

    public boolean aU() {
        return this instanceof zzaon;
    }

    public boolean aV() {
        return this instanceof zzaoj;
    }

    public zzaok aW() {
        if (aT()) {
            return (zzaok) this;
        }
        String valueOf = String.valueOf(this);
        throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 19).append("Not a JSON Object: ").append(valueOf).toString());
    }

    public zzaoe aX() {
        if (aS()) {
            return (zzaoe) this;
        }
        throw new IllegalStateException("This is not a JSON Array.");
    }

    public zzaon aY() {
        if (aU()) {
            return (zzaon) this;
        }
        throw new IllegalStateException("This is not a JSON Primitive.");
    }

    Boolean aZ() {
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
            zzaqa com_google_android_gms_internal_zzaqa = new zzaqa(stringWriter);
            com_google_android_gms_internal_zzaqa.setLenient(true);
            zzapi.zzb(this, com_google_android_gms_internal_zzaqa);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
