package com.google.android.gms.internal;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public final class zzapw<K, V> extends AbstractMap<K, V> implements Serializable {
    static final /* synthetic */ boolean $assertionsDisabled = (!zzapw.class.desiredAssertionStatus());
    private static final Comparator<Comparable> bpi = new Comparator<Comparable>() {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((Comparable) obj, (Comparable) obj2);
        }

        public int zza(Comparable comparable, Comparable comparable2) {
            return comparable.compareTo(comparable2);
        }
    };
    Comparator<? super K> bab;
    zzd<K, V> bpj;
    final zzd<K, V> bpk;
    private zza bpl;
    private zzb bpm;
    int modCount;
    int size;

    class zza extends AbstractSet<Entry<K, V>> {
        final /* synthetic */ zzapw bpn;

        zza(zzapw com_google_android_gms_internal_zzapw) {
            this.bpn = com_google_android_gms_internal_zzapw;
        }

        public void clear() {
            this.bpn.clear();
        }

        public boolean contains(Object obj) {
            return (obj instanceof Entry) && this.bpn.zzc((Entry) obj) != null;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new zzc<Entry<K, V>>(this) {
                final /* synthetic */ zza bpo;

                {
                    this.bpo = r3;
                    zzapw com_google_android_gms_internal_zzapw = r3.bpn;
                }

                public Entry<K, V> next() {
                    return bl();
                }
            };
        }

        public boolean remove(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            zzd zzc = this.bpn.zzc((Entry) obj);
            if (zzc == null) {
                return false;
            }
            this.bpn.zza(zzc, true);
            return true;
        }

        public int size() {
            return this.bpn.size;
        }
    }

    final class zzb extends AbstractSet<K> {
        final /* synthetic */ zzapw bpn;

        zzb(zzapw com_google_android_gms_internal_zzapw) {
            this.bpn = com_google_android_gms_internal_zzapw;
        }

        public void clear() {
            this.bpn.clear();
        }

        public boolean contains(Object obj) {
            return this.bpn.containsKey(obj);
        }

        public Iterator<K> iterator() {
            return new zzc<K>(this) {
                final /* synthetic */ zzb bpp;

                {
                    this.bpp = r3;
                    zzapw com_google_android_gms_internal_zzapw = r3.bpn;
                }

                public K next() {
                    return bl().bap;
                }
            };
        }

        public boolean remove(Object obj) {
            return this.bpn.zzcr(obj) != null;
        }

        public int size() {
            return this.bpn.size;
        }
    }

    private abstract class zzc<T> implements Iterator<T> {
        final /* synthetic */ zzapw bpn;
        zzd<K, V> bpq;
        zzd<K, V> bpr;
        int bps;

        private zzc(zzapw com_google_android_gms_internal_zzapw) {
            this.bpn = com_google_android_gms_internal_zzapw;
            this.bpq = this.bpn.bpk.bpq;
            this.bpr = null;
            this.bps = this.bpn.modCount;
        }

        final zzd<K, V> bl() {
            zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V = this.bpq;
            if (com_google_android_gms_internal_zzapw_zzd_K__V == this.bpn.bpk) {
                throw new NoSuchElementException();
            } else if (this.bpn.modCount != this.bps) {
                throw new ConcurrentModificationException();
            } else {
                this.bpq = com_google_android_gms_internal_zzapw_zzd_K__V.bpq;
                this.bpr = com_google_android_gms_internal_zzapw_zzd_K__V;
                return com_google_android_gms_internal_zzapw_zzd_K__V;
            }
        }

        public final boolean hasNext() {
            return this.bpq != this.bpn.bpk;
        }

        public final void remove() {
            if (this.bpr == null) {
                throw new IllegalStateException();
            }
            this.bpn.zza(this.bpr, true);
            this.bpr = null;
            this.bps = this.bpn.modCount;
        }
    }

    static final class zzd<K, V> implements Entry<K, V> {
        final K bap;
        zzd<K, V> bpq;
        zzd<K, V> bpt;
        zzd<K, V> bpu;
        zzd<K, V> bpv;
        zzd<K, V> bpw;
        int height;
        V value;

        zzd() {
            this.bap = null;
            this.bpw = this;
            this.bpq = this;
        }

        zzd(zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V, K k, zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V2, zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V3) {
            this.bpt = com_google_android_gms_internal_zzapw_zzd_K__V;
            this.bap = k;
            this.height = 1;
            this.bpq = com_google_android_gms_internal_zzapw_zzd_K__V2;
            this.bpw = com_google_android_gms_internal_zzapw_zzd_K__V3;
            com_google_android_gms_internal_zzapw_zzd_K__V3.bpq = this;
            com_google_android_gms_internal_zzapw_zzd_K__V2.bpw = this;
        }

        public zzd<K, V> bm() {
            zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V;
            for (zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V2 = this.bpu; com_google_android_gms_internal_zzapw_zzd_K__V2 != null; com_google_android_gms_internal_zzapw_zzd_K__V2 = com_google_android_gms_internal_zzapw_zzd_K__V2.bpu) {
                com_google_android_gms_internal_zzapw_zzd_K__V = com_google_android_gms_internal_zzapw_zzd_K__V2;
            }
            return com_google_android_gms_internal_zzapw_zzd_K__V;
        }

        public zzd<K, V> bn() {
            zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V;
            for (zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V2 = this.bpv; com_google_android_gms_internal_zzapw_zzd_K__V2 != null; com_google_android_gms_internal_zzapw_zzd_K__V2 = com_google_android_gms_internal_zzapw_zzd_K__V2.bpv) {
                com_google_android_gms_internal_zzapw_zzd_K__V = com_google_android_gms_internal_zzapw_zzd_K__V2;
            }
            return com_google_android_gms_internal_zzapw_zzd_K__V;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            if (this.bap == null) {
                if (entry.getKey() != null) {
                    return false;
                }
            } else if (!this.bap.equals(entry.getKey())) {
                return false;
            }
            if (this.value == null) {
                if (entry.getValue() != null) {
                    return false;
                }
            } else if (!this.value.equals(entry.getValue())) {
                return false;
            }
            return true;
        }

        public K getKey() {
            return this.bap;
        }

        public V getValue() {
            return this.value;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = this.bap == null ? 0 : this.bap.hashCode();
            if (this.value != null) {
                i = this.value.hashCode();
            }
            return hashCode ^ i;
        }

        public V setValue(V v) {
            V v2 = this.value;
            this.value = v;
            return v2;
        }

        public String toString() {
            String valueOf = String.valueOf(this.bap);
            String valueOf2 = String.valueOf(this.value);
            return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(valueOf2).length()).append(valueOf).append("=").append(valueOf2).toString();
        }
    }

    public zzapw() {
        this(bpi);
    }

    public zzapw(Comparator<? super K> comparator) {
        Comparator comparator2;
        this.size = 0;
        this.modCount = 0;
        this.bpk = new zzd();
        if (comparator == null) {
            comparator2 = bpi;
        }
        this.bab = comparator2;
    }

    private boolean equal(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    private void zza(zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V) {
        int i = 0;
        zzd com_google_android_gms_internal_zzapw_zzd = com_google_android_gms_internal_zzapw_zzd_K__V.bpu;
        zzd com_google_android_gms_internal_zzapw_zzd2 = com_google_android_gms_internal_zzapw_zzd_K__V.bpv;
        zzd com_google_android_gms_internal_zzapw_zzd3 = com_google_android_gms_internal_zzapw_zzd2.bpu;
        zzd com_google_android_gms_internal_zzapw_zzd4 = com_google_android_gms_internal_zzapw_zzd2.bpv;
        com_google_android_gms_internal_zzapw_zzd_K__V.bpv = com_google_android_gms_internal_zzapw_zzd3;
        if (com_google_android_gms_internal_zzapw_zzd3 != null) {
            com_google_android_gms_internal_zzapw_zzd3.bpt = com_google_android_gms_internal_zzapw_zzd_K__V;
        }
        zza((zzd) com_google_android_gms_internal_zzapw_zzd_K__V, com_google_android_gms_internal_zzapw_zzd2);
        com_google_android_gms_internal_zzapw_zzd2.bpu = com_google_android_gms_internal_zzapw_zzd_K__V;
        com_google_android_gms_internal_zzapw_zzd_K__V.bpt = com_google_android_gms_internal_zzapw_zzd2;
        com_google_android_gms_internal_zzapw_zzd_K__V.height = Math.max(com_google_android_gms_internal_zzapw_zzd != null ? com_google_android_gms_internal_zzapw_zzd.height : 0, com_google_android_gms_internal_zzapw_zzd3 != null ? com_google_android_gms_internal_zzapw_zzd3.height : 0) + 1;
        int i2 = com_google_android_gms_internal_zzapw_zzd_K__V.height;
        if (com_google_android_gms_internal_zzapw_zzd4 != null) {
            i = com_google_android_gms_internal_zzapw_zzd4.height;
        }
        com_google_android_gms_internal_zzapw_zzd2.height = Math.max(i2, i) + 1;
    }

    private void zza(zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V, zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V2) {
        zzd com_google_android_gms_internal_zzapw_zzd = com_google_android_gms_internal_zzapw_zzd_K__V.bpt;
        com_google_android_gms_internal_zzapw_zzd_K__V.bpt = null;
        if (com_google_android_gms_internal_zzapw_zzd_K__V2 != null) {
            com_google_android_gms_internal_zzapw_zzd_K__V2.bpt = com_google_android_gms_internal_zzapw_zzd;
        }
        if (com_google_android_gms_internal_zzapw_zzd == null) {
            this.bpj = com_google_android_gms_internal_zzapw_zzd_K__V2;
        } else if (com_google_android_gms_internal_zzapw_zzd.bpu == com_google_android_gms_internal_zzapw_zzd_K__V) {
            com_google_android_gms_internal_zzapw_zzd.bpu = com_google_android_gms_internal_zzapw_zzd_K__V2;
        } else if ($assertionsDisabled || com_google_android_gms_internal_zzapw_zzd.bpv == com_google_android_gms_internal_zzapw_zzd_K__V) {
            com_google_android_gms_internal_zzapw_zzd.bpv = com_google_android_gms_internal_zzapw_zzd_K__V2;
        } else {
            throw new AssertionError();
        }
    }

    private void zzb(zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V) {
        int i = 0;
        zzd com_google_android_gms_internal_zzapw_zzd = com_google_android_gms_internal_zzapw_zzd_K__V.bpu;
        zzd com_google_android_gms_internal_zzapw_zzd2 = com_google_android_gms_internal_zzapw_zzd_K__V.bpv;
        zzd com_google_android_gms_internal_zzapw_zzd3 = com_google_android_gms_internal_zzapw_zzd.bpu;
        zzd com_google_android_gms_internal_zzapw_zzd4 = com_google_android_gms_internal_zzapw_zzd.bpv;
        com_google_android_gms_internal_zzapw_zzd_K__V.bpu = com_google_android_gms_internal_zzapw_zzd4;
        if (com_google_android_gms_internal_zzapw_zzd4 != null) {
            com_google_android_gms_internal_zzapw_zzd4.bpt = com_google_android_gms_internal_zzapw_zzd_K__V;
        }
        zza((zzd) com_google_android_gms_internal_zzapw_zzd_K__V, com_google_android_gms_internal_zzapw_zzd);
        com_google_android_gms_internal_zzapw_zzd.bpv = com_google_android_gms_internal_zzapw_zzd_K__V;
        com_google_android_gms_internal_zzapw_zzd_K__V.bpt = com_google_android_gms_internal_zzapw_zzd;
        com_google_android_gms_internal_zzapw_zzd_K__V.height = Math.max(com_google_android_gms_internal_zzapw_zzd2 != null ? com_google_android_gms_internal_zzapw_zzd2.height : 0, com_google_android_gms_internal_zzapw_zzd4 != null ? com_google_android_gms_internal_zzapw_zzd4.height : 0) + 1;
        int i2 = com_google_android_gms_internal_zzapw_zzd_K__V.height;
        if (com_google_android_gms_internal_zzapw_zzd3 != null) {
            i = com_google_android_gms_internal_zzapw_zzd3.height;
        }
        com_google_android_gms_internal_zzapw_zzd.height = Math.max(i2, i) + 1;
    }

    private void zzb(zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V, boolean z) {
        zzd com_google_android_gms_internal_zzapw_zzd;
        while (com_google_android_gms_internal_zzapw_zzd != null) {
            zzd com_google_android_gms_internal_zzapw_zzd2 = com_google_android_gms_internal_zzapw_zzd.bpu;
            zzd com_google_android_gms_internal_zzapw_zzd3 = com_google_android_gms_internal_zzapw_zzd.bpv;
            int i = com_google_android_gms_internal_zzapw_zzd2 != null ? com_google_android_gms_internal_zzapw_zzd2.height : 0;
            int i2 = com_google_android_gms_internal_zzapw_zzd3 != null ? com_google_android_gms_internal_zzapw_zzd3.height : 0;
            int i3 = i - i2;
            zzd com_google_android_gms_internal_zzapw_zzd4;
            if (i3 == -2) {
                com_google_android_gms_internal_zzapw_zzd2 = com_google_android_gms_internal_zzapw_zzd3.bpu;
                com_google_android_gms_internal_zzapw_zzd4 = com_google_android_gms_internal_zzapw_zzd3.bpv;
                i2 = (com_google_android_gms_internal_zzapw_zzd2 != null ? com_google_android_gms_internal_zzapw_zzd2.height : 0) - (com_google_android_gms_internal_zzapw_zzd4 != null ? com_google_android_gms_internal_zzapw_zzd4.height : 0);
                if (i2 == -1 || (i2 == 0 && !z)) {
                    zza(com_google_android_gms_internal_zzapw_zzd);
                } else if ($assertionsDisabled || i2 == 1) {
                    zzb(com_google_android_gms_internal_zzapw_zzd3);
                    zza(com_google_android_gms_internal_zzapw_zzd);
                } else {
                    throw new AssertionError();
                }
                if (z) {
                    return;
                }
            } else if (i3 == 2) {
                com_google_android_gms_internal_zzapw_zzd3 = com_google_android_gms_internal_zzapw_zzd2.bpu;
                com_google_android_gms_internal_zzapw_zzd4 = com_google_android_gms_internal_zzapw_zzd2.bpv;
                i2 = (com_google_android_gms_internal_zzapw_zzd3 != null ? com_google_android_gms_internal_zzapw_zzd3.height : 0) - (com_google_android_gms_internal_zzapw_zzd4 != null ? com_google_android_gms_internal_zzapw_zzd4.height : 0);
                if (i2 == 1 || (i2 == 0 && !z)) {
                    zzb(com_google_android_gms_internal_zzapw_zzd);
                } else if ($assertionsDisabled || i2 == -1) {
                    zza(com_google_android_gms_internal_zzapw_zzd2);
                    zzb(com_google_android_gms_internal_zzapw_zzd);
                } else {
                    throw new AssertionError();
                }
                if (z) {
                    return;
                }
            } else if (i3 == 0) {
                com_google_android_gms_internal_zzapw_zzd.height = i + 1;
                if (z) {
                    return;
                }
            } else if ($assertionsDisabled || i3 == -1 || i3 == 1) {
                com_google_android_gms_internal_zzapw_zzd.height = Math.max(i, i2) + 1;
                if (!z) {
                    return;
                }
            } else {
                throw new AssertionError();
            }
            com_google_android_gms_internal_zzapw_zzd = com_google_android_gms_internal_zzapw_zzd.bpt;
        }
    }

    public void clear() {
        this.bpj = null;
        this.size = 0;
        this.modCount++;
        zzd com_google_android_gms_internal_zzapw_zzd = this.bpk;
        com_google_android_gms_internal_zzapw_zzd.bpw = com_google_android_gms_internal_zzapw_zzd;
        com_google_android_gms_internal_zzapw_zzd.bpq = com_google_android_gms_internal_zzapw_zzd;
    }

    public boolean containsKey(Object obj) {
        return zzcq(obj) != null;
    }

    public Set<Entry<K, V>> entrySet() {
        Set set = this.bpl;
        if (set != null) {
            return set;
        }
        set = new zza(this);
        this.bpl = set;
        return set;
    }

    public V get(Object obj) {
        zzd zzcq = zzcq(obj);
        return zzcq != null ? zzcq.value : null;
    }

    public Set<K> keySet() {
        Set set = this.bpm;
        if (set != null) {
            return set;
        }
        set = new zzb(this);
        this.bpm = set;
        return set;
    }

    public V put(K k, V v) {
        if (k == null) {
            throw new NullPointerException("key == null");
        }
        zzd zza = zza((Object) k, true);
        V v2 = zza.value;
        zza.value = v;
        return v2;
    }

    public V remove(Object obj) {
        zzd zzcr = zzcr(obj);
        return zzcr != null ? zzcr.value : null;
    }

    public int size() {
        return this.size;
    }

    zzd<K, V> zza(K k, boolean z) {
        int i;
        Comparator comparator = this.bab;
        zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V = this.bpj;
        if (com_google_android_gms_internal_zzapw_zzd_K__V != null) {
            int compareTo;
            Comparable comparable = comparator == bpi ? (Comparable) k : null;
            while (true) {
                compareTo = comparable != null ? comparable.compareTo(com_google_android_gms_internal_zzapw_zzd_K__V.bap) : comparator.compare(k, com_google_android_gms_internal_zzapw_zzd_K__V.bap);
                if (compareTo == 0) {
                    return com_google_android_gms_internal_zzapw_zzd_K__V;
                }
                zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V2 = compareTo < 0 ? com_google_android_gms_internal_zzapw_zzd_K__V.bpu : com_google_android_gms_internal_zzapw_zzd_K__V.bpv;
                if (com_google_android_gms_internal_zzapw_zzd_K__V2 == null) {
                    break;
                }
                com_google_android_gms_internal_zzapw_zzd_K__V = com_google_android_gms_internal_zzapw_zzd_K__V2;
            }
            int i2 = compareTo;
            zzd com_google_android_gms_internal_zzapw_zzd = com_google_android_gms_internal_zzapw_zzd_K__V;
            i = i2;
        } else {
            zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V3 = com_google_android_gms_internal_zzapw_zzd_K__V;
            i = 0;
        }
        if (!z) {
            return null;
        }
        zzd<K, V> com_google_android_gms_internal_zzapw_zzd2;
        zzd com_google_android_gms_internal_zzapw_zzd3 = this.bpk;
        if (com_google_android_gms_internal_zzapw_zzd != null) {
            com_google_android_gms_internal_zzapw_zzd2 = new zzd(com_google_android_gms_internal_zzapw_zzd, k, com_google_android_gms_internal_zzapw_zzd3, com_google_android_gms_internal_zzapw_zzd3.bpw);
            if (i < 0) {
                com_google_android_gms_internal_zzapw_zzd.bpu = com_google_android_gms_internal_zzapw_zzd2;
            } else {
                com_google_android_gms_internal_zzapw_zzd.bpv = com_google_android_gms_internal_zzapw_zzd2;
            }
            zzb(com_google_android_gms_internal_zzapw_zzd, true);
        } else if (comparator != bpi || (k instanceof Comparable)) {
            com_google_android_gms_internal_zzapw_zzd2 = new zzd(com_google_android_gms_internal_zzapw_zzd, k, com_google_android_gms_internal_zzapw_zzd3, com_google_android_gms_internal_zzapw_zzd3.bpw);
            this.bpj = com_google_android_gms_internal_zzapw_zzd2;
        } else {
            throw new ClassCastException(String.valueOf(k.getClass().getName()).concat(" is not Comparable"));
        }
        this.size++;
        this.modCount++;
        return com_google_android_gms_internal_zzapw_zzd2;
    }

    void zza(zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V, boolean z) {
        int i = 0;
        if (z) {
            com_google_android_gms_internal_zzapw_zzd_K__V.bpw.bpq = com_google_android_gms_internal_zzapw_zzd_K__V.bpq;
            com_google_android_gms_internal_zzapw_zzd_K__V.bpq.bpw = com_google_android_gms_internal_zzapw_zzd_K__V.bpw;
        }
        zzd com_google_android_gms_internal_zzapw_zzd = com_google_android_gms_internal_zzapw_zzd_K__V.bpu;
        zzd com_google_android_gms_internal_zzapw_zzd2 = com_google_android_gms_internal_zzapw_zzd_K__V.bpv;
        zzd com_google_android_gms_internal_zzapw_zzd3 = com_google_android_gms_internal_zzapw_zzd_K__V.bpt;
        if (com_google_android_gms_internal_zzapw_zzd == null || com_google_android_gms_internal_zzapw_zzd2 == null) {
            if (com_google_android_gms_internal_zzapw_zzd != null) {
                zza((zzd) com_google_android_gms_internal_zzapw_zzd_K__V, com_google_android_gms_internal_zzapw_zzd);
                com_google_android_gms_internal_zzapw_zzd_K__V.bpu = null;
            } else if (com_google_android_gms_internal_zzapw_zzd2 != null) {
                zza((zzd) com_google_android_gms_internal_zzapw_zzd_K__V, com_google_android_gms_internal_zzapw_zzd2);
                com_google_android_gms_internal_zzapw_zzd_K__V.bpv = null;
            } else {
                zza((zzd) com_google_android_gms_internal_zzapw_zzd_K__V, null);
            }
            zzb(com_google_android_gms_internal_zzapw_zzd3, false);
            this.size--;
            this.modCount++;
            return;
        }
        int i2;
        com_google_android_gms_internal_zzapw_zzd = com_google_android_gms_internal_zzapw_zzd.height > com_google_android_gms_internal_zzapw_zzd2.height ? com_google_android_gms_internal_zzapw_zzd.bn() : com_google_android_gms_internal_zzapw_zzd2.bm();
        zza(com_google_android_gms_internal_zzapw_zzd, false);
        com_google_android_gms_internal_zzapw_zzd3 = com_google_android_gms_internal_zzapw_zzd_K__V.bpu;
        if (com_google_android_gms_internal_zzapw_zzd3 != null) {
            i2 = com_google_android_gms_internal_zzapw_zzd3.height;
            com_google_android_gms_internal_zzapw_zzd.bpu = com_google_android_gms_internal_zzapw_zzd3;
            com_google_android_gms_internal_zzapw_zzd3.bpt = com_google_android_gms_internal_zzapw_zzd;
            com_google_android_gms_internal_zzapw_zzd_K__V.bpu = null;
        } else {
            i2 = 0;
        }
        com_google_android_gms_internal_zzapw_zzd3 = com_google_android_gms_internal_zzapw_zzd_K__V.bpv;
        if (com_google_android_gms_internal_zzapw_zzd3 != null) {
            i = com_google_android_gms_internal_zzapw_zzd3.height;
            com_google_android_gms_internal_zzapw_zzd.bpv = com_google_android_gms_internal_zzapw_zzd3;
            com_google_android_gms_internal_zzapw_zzd3.bpt = com_google_android_gms_internal_zzapw_zzd;
            com_google_android_gms_internal_zzapw_zzd_K__V.bpv = null;
        }
        com_google_android_gms_internal_zzapw_zzd.height = Math.max(i2, i) + 1;
        zza((zzd) com_google_android_gms_internal_zzapw_zzd_K__V, com_google_android_gms_internal_zzapw_zzd);
    }

    zzd<K, V> zzc(Entry<?, ?> entry) {
        zzd<K, V> zzcq = zzcq(entry.getKey());
        Object obj = (zzcq == null || !equal(zzcq.value, entry.getValue())) ? null : 1;
        return obj != null ? zzcq : null;
    }

    zzd<K, V> zzcq(Object obj) {
        zzd<K, V> com_google_android_gms_internal_zzapw_zzd_K__V = null;
        if (obj != null) {
            try {
                com_google_android_gms_internal_zzapw_zzd_K__V = zza(obj, false);
            } catch (ClassCastException e) {
            }
        }
        return com_google_android_gms_internal_zzapw_zzd_K__V;
    }

    zzd<K, V> zzcr(Object obj) {
        zzd zzcq = zzcq(obj);
        if (zzcq != null) {
            zza(zzcq, true);
        }
        return zzcq;
    }
}
