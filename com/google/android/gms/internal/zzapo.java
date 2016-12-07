package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public final class zzapo extends zzapy {
    private static final Reader bmu = new Reader() {
        public void close() throws IOException {
            throw new AssertionError();
        }

        public int read(char[] cArr, int i, int i2) throws IOException {
            throw new AssertionError();
        }
    };
    private static final Object bmv = new Object();
    private final List<Object> bmw = new ArrayList();

    public zzapo(zzaoh com_google_android_gms_internal_zzaoh) {
        super(bmu);
        this.bmw.add(com_google_android_gms_internal_zzaoh);
    }

    private Object bo() {
        return this.bmw.get(this.bmw.size() - 1);
    }

    private Object bp() {
        return this.bmw.remove(this.bmw.size() - 1);
    }

    private void zza(zzapz com_google_android_gms_internal_zzapz) throws IOException {
        if (bn() != com_google_android_gms_internal_zzapz) {
            String valueOf = String.valueOf(com_google_android_gms_internal_zzapz);
            String valueOf2 = String.valueOf(bn());
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 18) + String.valueOf(valueOf2).length()).append("Expected ").append(valueOf).append(" but was ").append(valueOf2).toString());
        }
    }

    public void beginArray() throws IOException {
        zza(zzapz.BEGIN_ARRAY);
        this.bmw.add(((zzaoe) bo()).iterator());
    }

    public void beginObject() throws IOException {
        zza(zzapz.BEGIN_OBJECT);
        this.bmw.add(((zzaok) bo()).entrySet().iterator());
    }

    public zzapz bn() throws IOException {
        if (this.bmw.isEmpty()) {
            return zzapz.END_DOCUMENT;
        }
        Object bo = bo();
        if (bo instanceof Iterator) {
            boolean z = this.bmw.get(this.bmw.size() - 2) instanceof zzaok;
            Iterator it = (Iterator) bo;
            if (!it.hasNext()) {
                return z ? zzapz.END_OBJECT : zzapz.END_ARRAY;
            } else {
                if (z) {
                    return zzapz.NAME;
                }
                this.bmw.add(it.next());
                return bn();
            }
        } else if (bo instanceof zzaok) {
            return zzapz.BEGIN_OBJECT;
        } else {
            if (bo instanceof zzaoe) {
                return zzapz.BEGIN_ARRAY;
            }
            if (bo instanceof zzaon) {
                zzaon com_google_android_gms_internal_zzaon = (zzaon) bo;
                if (com_google_android_gms_internal_zzaon.bc()) {
                    return zzapz.STRING;
                }
                if (com_google_android_gms_internal_zzaon.ba()) {
                    return zzapz.BOOLEAN;
                }
                if (com_google_android_gms_internal_zzaon.bb()) {
                    return zzapz.NUMBER;
                }
                throw new AssertionError();
            } else if (bo instanceof zzaoj) {
                return zzapz.NULL;
            } else {
                if (bo == bmv) {
                    throw new IllegalStateException("JsonReader is closed");
                }
                throw new AssertionError();
            }
        }
    }

    public void bq() throws IOException {
        zza(zzapz.NAME);
        Entry entry = (Entry) ((Iterator) bo()).next();
        this.bmw.add(entry.getValue());
        this.bmw.add(new zzaon((String) entry.getKey()));
    }

    public void close() throws IOException {
        this.bmw.clear();
        this.bmw.add(bmv);
    }

    public void endArray() throws IOException {
        zza(zzapz.END_ARRAY);
        bp();
        bp();
    }

    public void endObject() throws IOException {
        zza(zzapz.END_OBJECT);
        bp();
        bp();
    }

    public boolean hasNext() throws IOException {
        zzapz bn = bn();
        return (bn == zzapz.END_OBJECT || bn == zzapz.END_ARRAY) ? false : true;
    }

    public boolean nextBoolean() throws IOException {
        zza(zzapz.BOOLEAN);
        return ((zzaon) bp()).getAsBoolean();
    }

    public double nextDouble() throws IOException {
        zzapz bn = bn();
        if (bn == zzapz.NUMBER || bn == zzapz.STRING) {
            double asDouble = ((zzaon) bo()).getAsDouble();
            if (isLenient() || !(Double.isNaN(asDouble) || Double.isInfinite(asDouble))) {
                bp();
                return asDouble;
            }
            throw new NumberFormatException("JSON forbids NaN and infinities: " + asDouble);
        }
        String valueOf = String.valueOf(zzapz.NUMBER);
        String valueOf2 = String.valueOf(bn);
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 18) + String.valueOf(valueOf2).length()).append("Expected ").append(valueOf).append(" but was ").append(valueOf2).toString());
    }

    public int nextInt() throws IOException {
        zzapz bn = bn();
        if (bn == zzapz.NUMBER || bn == zzapz.STRING) {
            int asInt = ((zzaon) bo()).getAsInt();
            bp();
            return asInt;
        }
        String valueOf = String.valueOf(zzapz.NUMBER);
        String valueOf2 = String.valueOf(bn);
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 18) + String.valueOf(valueOf2).length()).append("Expected ").append(valueOf).append(" but was ").append(valueOf2).toString());
    }

    public long nextLong() throws IOException {
        zzapz bn = bn();
        if (bn == zzapz.NUMBER || bn == zzapz.STRING) {
            long asLong = ((zzaon) bo()).getAsLong();
            bp();
            return asLong;
        }
        String valueOf = String.valueOf(zzapz.NUMBER);
        String valueOf2 = String.valueOf(bn);
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 18) + String.valueOf(valueOf2).length()).append("Expected ").append(valueOf).append(" but was ").append(valueOf2).toString());
    }

    public String nextName() throws IOException {
        zza(zzapz.NAME);
        Entry entry = (Entry) ((Iterator) bo()).next();
        this.bmw.add(entry.getValue());
        return (String) entry.getKey();
    }

    public void nextNull() throws IOException {
        zza(zzapz.NULL);
        bp();
    }

    public String nextString() throws IOException {
        zzapz bn = bn();
        if (bn == zzapz.STRING || bn == zzapz.NUMBER) {
            return ((zzaon) bp()).aR();
        }
        String valueOf = String.valueOf(zzapz.STRING);
        String valueOf2 = String.valueOf(bn);
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 18) + String.valueOf(valueOf2).length()).append("Expected ").append(valueOf).append(" but was ").append(valueOf2).toString());
    }

    public void skipValue() throws IOException {
        if (bn() == zzapz.NAME) {
            nextName();
        } else {
            bp();
        }
    }

    public String toString() {
        return getClass().getSimpleName();
    }
}
