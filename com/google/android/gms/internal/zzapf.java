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

public final class zzapf<K, V> extends AbstractMap<K, V> implements Serializable {
    static final /* synthetic */ boolean $assertionsDisabled = (!zzapf.class.desiredAssertionStatus());
    private static final Comparator<Comparable> blR = new Comparator<Comparable>() {
        public /* synthetic */ int compare(Object obj, Object obj2) {
            return zza((Comparable) obj, (Comparable) obj2);
        }

        public int zza(Comparable comparable, Comparable comparable2) {
            return comparable.compareTo(comparable2);
        }
    };
    Comparator<? super K> aWP;
    zzd<K, V> blS;
    final zzd<K, V> blT;
    private zza blU;
    private zzb blV;
    int modCount;
    int size;

    class zza extends AbstractSet<Entry<K, V>> {
        final /* synthetic */ zzapf blW;

        zza(zzapf com_google_android_gms_internal_zzapf) {
            this.blW = com_google_android_gms_internal_zzapf;
        }

        public void clear() {
            this.blW.clear();
        }

        public boolean contains(Object obj) {
            return (obj instanceof Entry) && this.blW.zzc((Entry) obj) != null;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new zzc<Entry<K, V>>(this) {
                final /* synthetic */ zza blX;

                {
                    this.blX = r3;
                    zzapf com_google_android_gms_internal_zzapf = r3.blW;
                }

                public Entry<K, V> next() {
                    return bi();
                }
            };
        }

        public boolean remove(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            zzd zzc = this.blW.zzc((Entry) obj);
            if (zzc == null) {
                return false;
            }
            this.blW.zza(zzc, true);
            return true;
        }

        public int size() {
            return this.blW.size;
        }
    }

    final class zzb extends AbstractSet<K> {
        final /* synthetic */ zzapf blW;

        zzb(zzapf com_google_android_gms_internal_zzapf) {
            this.blW = com_google_android_gms_internal_zzapf;
        }

        public void clear() {
            this.blW.clear();
        }

        public boolean contains(Object obj) {
            return this.blW.containsKey(obj);
        }

        public Iterator<K> iterator() {
            return new zzc<K>(this) {
                final /* synthetic */ zzb blY;

                {
                    this.blY = r3;
                    zzapf com_google_android_gms_internal_zzapf = r3.blW;
                }

                public K next() {
                    return bi().aXd;
                }
            };
        }

        public boolean remove(Object obj) {
            return this.blW.zzcs(obj) != null;
        }

        public int size() {
            return this.blW.size;
        }
    }

    private abstract class zzc<T> implements Iterator<T> {
        final /* synthetic */ zzapf blW;
        zzd<K, V> blZ;
        zzd<K, V> bma;
        int bmb;

        private zzc(zzapf com_google_android_gms_internal_zzapf) {
            this.blW = com_google_android_gms_internal_zzapf;
            this.blZ = this.blW.blT.blZ;
            this.bma = null;
            this.bmb = this.blW.modCount;
        }

        final zzd<K, V> bi() {
            zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V = this.blZ;
            if (com_google_android_gms_internal_zzapf_zzd_K__V == this.blW.blT) {
                throw new NoSuchElementException();
            } else if (this.blW.modCount != this.bmb) {
                throw new ConcurrentModificationException();
            } else {
                this.blZ = com_google_android_gms_internal_zzapf_zzd_K__V.blZ;
                this.bma = com_google_android_gms_internal_zzapf_zzd_K__V;
                return com_google_android_gms_internal_zzapf_zzd_K__V;
            }
        }

        public final boolean hasNext() {
            return this.blZ != this.blW.blT;
        }

        public final void remove() {
            if (this.bma == null) {
                throw new IllegalStateException();
            }
            this.blW.zza(this.bma, true);
            this.bma = null;
            this.bmb = this.blW.modCount;
        }
    }

    static final class zzd<K, V> implements Entry<K, V> {
        final K aXd;
        zzd<K, V> blZ;
        zzd<K, V> bmc;
        zzd<K, V> bmd;
        zzd<K, V> bme;
        zzd<K, V> bmf;
        int height;
        V value;

        zzd() {
            this.aXd = null;
            this.bmf = this;
            this.blZ = this;
        }

        zzd(zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V, K k, zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V2, zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V3) {
            this.bmc = com_google_android_gms_internal_zzapf_zzd_K__V;
            this.aXd = k;
            this.height = 1;
            this.blZ = com_google_android_gms_internal_zzapf_zzd_K__V2;
            this.bmf = com_google_android_gms_internal_zzapf_zzd_K__V3;
            com_google_android_gms_internal_zzapf_zzd_K__V3.blZ = this;
            com_google_android_gms_internal_zzapf_zzd_K__V2.bmf = this;
        }

        public zzd<K, V> bj() {
            zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V;
            for (zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V2 = this.bmd; com_google_android_gms_internal_zzapf_zzd_K__V2 != null; com_google_android_gms_internal_zzapf_zzd_K__V2 = com_google_android_gms_internal_zzapf_zzd_K__V2.bmd) {
                com_google_android_gms_internal_zzapf_zzd_K__V = com_google_android_gms_internal_zzapf_zzd_K__V2;
            }
            return com_google_android_gms_internal_zzapf_zzd_K__V;
        }

        public zzd<K, V> bk() {
            zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V;
            for (zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V2 = this.bme; com_google_android_gms_internal_zzapf_zzd_K__V2 != null; com_google_android_gms_internal_zzapf_zzd_K__V2 = com_google_android_gms_internal_zzapf_zzd_K__V2.bme) {
                com_google_android_gms_internal_zzapf_zzd_K__V = com_google_android_gms_internal_zzapf_zzd_K__V2;
            }
            return com_google_android_gms_internal_zzapf_zzd_K__V;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            if (this.aXd == null) {
                if (entry.getKey() != null) {
                    return false;
                }
            } else if (!this.aXd.equals(entry.getKey())) {
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
            return this.aXd;
        }

        public V getValue() {
            return this.value;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = this.aXd == null ? 0 : this.aXd.hashCode();
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
            String valueOf = String.valueOf(this.aXd);
            String valueOf2 = String.valueOf(this.value);
            return new StringBuilder((String.valueOf(valueOf).length() + 1) + String.valueOf(valueOf2).length()).append(valueOf).append("=").append(valueOf2).toString();
        }
    }

    public zzapf() {
        this(blR);
    }

    public zzapf(Comparator<? super K> comparator) {
        Comparator comparator2;
        this.size = 0;
        this.modCount = 0;
        this.blT = new zzd();
        if (comparator == null) {
            comparator2 = blR;
        }
        this.aWP = comparator2;
    }

    private boolean equal(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    private void zza(zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V) {
        int i = 0;
        zzd com_google_android_gms_internal_zzapf_zzd = com_google_android_gms_internal_zzapf_zzd_K__V.bmd;
        zzd com_google_android_gms_internal_zzapf_zzd2 = com_google_android_gms_internal_zzapf_zzd_K__V.bme;
        zzd com_google_android_gms_internal_zzapf_zzd3 = com_google_android_gms_internal_zzapf_zzd2.bmd;
        zzd com_google_android_gms_internal_zzapf_zzd4 = com_google_android_gms_internal_zzapf_zzd2.bme;
        com_google_android_gms_internal_zzapf_zzd_K__V.bme = com_google_android_gms_internal_zzapf_zzd3;
        if (com_google_android_gms_internal_zzapf_zzd3 != null) {
            com_google_android_gms_internal_zzapf_zzd3.bmc = com_google_android_gms_internal_zzapf_zzd_K__V;
        }
        zza((zzd) com_google_android_gms_internal_zzapf_zzd_K__V, com_google_android_gms_internal_zzapf_zzd2);
        com_google_android_gms_internal_zzapf_zzd2.bmd = com_google_android_gms_internal_zzapf_zzd_K__V;
        com_google_android_gms_internal_zzapf_zzd_K__V.bmc = com_google_android_gms_internal_zzapf_zzd2;
        com_google_android_gms_internal_zzapf_zzd_K__V.height = Math.max(com_google_android_gms_internal_zzapf_zzd != null ? com_google_android_gms_internal_zzapf_zzd.height : 0, com_google_android_gms_internal_zzapf_zzd3 != null ? com_google_android_gms_internal_zzapf_zzd3.height : 0) + 1;
        int i2 = com_google_android_gms_internal_zzapf_zzd_K__V.height;
        if (com_google_android_gms_internal_zzapf_zzd4 != null) {
            i = com_google_android_gms_internal_zzapf_zzd4.height;
        }
        com_google_android_gms_internal_zzapf_zzd2.height = Math.max(i2, i) + 1;
    }

    private void zza(zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V, zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V2) {
        zzd com_google_android_gms_internal_zzapf_zzd = com_google_android_gms_internal_zzapf_zzd_K__V.bmc;
        com_google_android_gms_internal_zzapf_zzd_K__V.bmc = null;
        if (com_google_android_gms_internal_zzapf_zzd_K__V2 != null) {
            com_google_android_gms_internal_zzapf_zzd_K__V2.bmc = com_google_android_gms_internal_zzapf_zzd;
        }
        if (com_google_android_gms_internal_zzapf_zzd == null) {
            this.blS = com_google_android_gms_internal_zzapf_zzd_K__V2;
        } else if (com_google_android_gms_internal_zzapf_zzd.bmd == com_google_android_gms_internal_zzapf_zzd_K__V) {
            com_google_android_gms_internal_zzapf_zzd.bmd = com_google_android_gms_internal_zzapf_zzd_K__V2;
        } else if ($assertionsDisabled || com_google_android_gms_internal_zzapf_zzd.bme == com_google_android_gms_internal_zzapf_zzd_K__V) {
            com_google_android_gms_internal_zzapf_zzd.bme = com_google_android_gms_internal_zzapf_zzd_K__V2;
        } else {
            throw new AssertionError();
        }
    }

    private void zzb(zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V) {
        int i = 0;
        zzd com_google_android_gms_internal_zzapf_zzd = com_google_android_gms_internal_zzapf_zzd_K__V.bmd;
        zzd com_google_android_gms_internal_zzapf_zzd2 = com_google_android_gms_internal_zzapf_zzd_K__V.bme;
        zzd com_google_android_gms_internal_zzapf_zzd3 = com_google_android_gms_internal_zzapf_zzd.bmd;
        zzd com_google_android_gms_internal_zzapf_zzd4 = com_google_android_gms_internal_zzapf_zzd.bme;
        com_google_android_gms_internal_zzapf_zzd_K__V.bmd = com_google_android_gms_internal_zzapf_zzd4;
        if (com_google_android_gms_internal_zzapf_zzd4 != null) {
            com_google_android_gms_internal_zzapf_zzd4.bmc = com_google_android_gms_internal_zzapf_zzd_K__V;
        }
        zza((zzd) com_google_android_gms_internal_zzapf_zzd_K__V, com_google_android_gms_internal_zzapf_zzd);
        com_google_android_gms_internal_zzapf_zzd.bme = com_google_android_gms_internal_zzapf_zzd_K__V;
        com_google_android_gms_internal_zzapf_zzd_K__V.bmc = com_google_android_gms_internal_zzapf_zzd;
        com_google_android_gms_internal_zzapf_zzd_K__V.height = Math.max(com_google_android_gms_internal_zzapf_zzd2 != null ? com_google_android_gms_internal_zzapf_zzd2.height : 0, com_google_android_gms_internal_zzapf_zzd4 != null ? com_google_android_gms_internal_zzapf_zzd4.height : 0) + 1;
        int i2 = com_google_android_gms_internal_zzapf_zzd_K__V.height;
        if (com_google_android_gms_internal_zzapf_zzd3 != null) {
            i = com_google_android_gms_internal_zzapf_zzd3.height;
        }
        com_google_android_gms_internal_zzapf_zzd.height = Math.max(i2, i) + 1;
    }

    private void zzb(zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V, boolean z) {
        zzd com_google_android_gms_internal_zzapf_zzd;
        while (com_google_android_gms_internal_zzapf_zzd != null) {
            zzd com_google_android_gms_internal_zzapf_zzd2 = com_google_android_gms_internal_zzapf_zzd.bmd;
            zzd com_google_android_gms_internal_zzapf_zzd3 = com_google_android_gms_internal_zzapf_zzd.bme;
            int i = com_google_android_gms_internal_zzapf_zzd2 != null ? com_google_android_gms_internal_zzapf_zzd2.height : 0;
            int i2 = com_google_android_gms_internal_zzapf_zzd3 != null ? com_google_android_gms_internal_zzapf_zzd3.height : 0;
            int i3 = i - i2;
            zzd com_google_android_gms_internal_zzapf_zzd4;
            if (i3 == -2) {
                com_google_android_gms_internal_zzapf_zzd2 = com_google_android_gms_internal_zzapf_zzd3.bmd;
                com_google_android_gms_internal_zzapf_zzd4 = com_google_android_gms_internal_zzapf_zzd3.bme;
                i2 = (com_google_android_gms_internal_zzapf_zzd2 != null ? com_google_android_gms_internal_zzapf_zzd2.height : 0) - (com_google_android_gms_internal_zzapf_zzd4 != null ? com_google_android_gms_internal_zzapf_zzd4.height : 0);
                if (i2 == -1 || (i2 == 0 && !z)) {
                    zza(com_google_android_gms_internal_zzapf_zzd);
                } else if ($assertionsDisabled || i2 == 1) {
                    zzb(com_google_android_gms_internal_zzapf_zzd3);
                    zza(com_google_android_gms_internal_zzapf_zzd);
                } else {
                    throw new AssertionError();
                }
                if (z) {
                    return;
                }
            } else if (i3 == 2) {
                com_google_android_gms_internal_zzapf_zzd3 = com_google_android_gms_internal_zzapf_zzd2.bmd;
                com_google_android_gms_internal_zzapf_zzd4 = com_google_android_gms_internal_zzapf_zzd2.bme;
                i2 = (com_google_android_gms_internal_zzapf_zzd3 != null ? com_google_android_gms_internal_zzapf_zzd3.height : 0) - (com_google_android_gms_internal_zzapf_zzd4 != null ? com_google_android_gms_internal_zzapf_zzd4.height : 0);
                if (i2 == 1 || (i2 == 0 && !z)) {
                    zzb(com_google_android_gms_internal_zzapf_zzd);
                } else if ($assertionsDisabled || i2 == -1) {
                    zza(com_google_android_gms_internal_zzapf_zzd2);
                    zzb(com_google_android_gms_internal_zzapf_zzd);
                } else {
                    throw new AssertionError();
                }
                if (z) {
                    return;
                }
            } else if (i3 == 0) {
                com_google_android_gms_internal_zzapf_zzd.height = i + 1;
                if (z) {
                    return;
                }
            } else if ($assertionsDisabled || i3 == -1 || i3 == 1) {
                com_google_android_gms_internal_zzapf_zzd.height = Math.max(i, i2) + 1;
                if (!z) {
                    return;
                }
            } else {
                throw new AssertionError();
            }
            com_google_android_gms_internal_zzapf_zzd = com_google_android_gms_internal_zzapf_zzd.bmc;
        }
    }

    public void clear() {
        this.blS = null;
        this.size = 0;
        this.modCount++;
        zzd com_google_android_gms_internal_zzapf_zzd = this.blT;
        com_google_android_gms_internal_zzapf_zzd.bmf = com_google_android_gms_internal_zzapf_zzd;
        com_google_android_gms_internal_zzapf_zzd.blZ = com_google_android_gms_internal_zzapf_zzd;
    }

    public boolean containsKey(Object obj) {
        return zzcr(obj) != null;
    }

    public Set<Entry<K, V>> entrySet() {
        Set set = this.blU;
        if (set != null) {
            return set;
        }
        set = new zza(this);
        this.blU = set;
        return set;
    }

    public V get(Object obj) {
        zzd zzcr = zzcr(obj);
        return zzcr != null ? zzcr.value : null;
    }

    public Set<K> keySet() {
        Set set = this.blV;
        if (set != null) {
            return set;
        }
        set = new zzb(this);
        this.blV = set;
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
        zzd zzcs = zzcs(obj);
        return zzcs != null ? zzcs.value : null;
    }

    public int size() {
        return this.size;
    }

    zzd<K, V> zza(K k, boolean z) {
        int i;
        Comparator comparator = this.aWP;
        zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V = this.blS;
        if (com_google_android_gms_internal_zzapf_zzd_K__V != null) {
            int compareTo;
            Comparable comparable = comparator == blR ? (Comparable) k : null;
            while (true) {
                compareTo = comparable != null ? comparable.compareTo(com_google_android_gms_internal_zzapf_zzd_K__V.aXd) : comparator.compare(k, com_google_android_gms_internal_zzapf_zzd_K__V.aXd);
                if (compareTo == 0) {
                    return com_google_android_gms_internal_zzapf_zzd_K__V;
                }
                zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V2 = compareTo < 0 ? com_google_android_gms_internal_zzapf_zzd_K__V.bmd : com_google_android_gms_internal_zzapf_zzd_K__V.bme;
                if (com_google_android_gms_internal_zzapf_zzd_K__V2 == null) {
                    break;
                }
                com_google_android_gms_internal_zzapf_zzd_K__V = com_google_android_gms_internal_zzapf_zzd_K__V2;
            }
            int i2 = compareTo;
            zzd com_google_android_gms_internal_zzapf_zzd = com_google_android_gms_internal_zzapf_zzd_K__V;
            i = i2;
        } else {
            zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V3 = com_google_android_gms_internal_zzapf_zzd_K__V;
            i = 0;
        }
        if (!z) {
            return null;
        }
        zzd<K, V> com_google_android_gms_internal_zzapf_zzd2;
        zzd com_google_android_gms_internal_zzapf_zzd3 = this.blT;
        if (com_google_android_gms_internal_zzapf_zzd != null) {
            com_google_android_gms_internal_zzapf_zzd2 = new zzd(com_google_android_gms_internal_zzapf_zzd, k, com_google_android_gms_internal_zzapf_zzd3, com_google_android_gms_internal_zzapf_zzd3.bmf);
            if (i < 0) {
                com_google_android_gms_internal_zzapf_zzd.bmd = com_google_android_gms_internal_zzapf_zzd2;
            } else {
                com_google_android_gms_internal_zzapf_zzd.bme = com_google_android_gms_internal_zzapf_zzd2;
            }
            zzb(com_google_android_gms_internal_zzapf_zzd, true);
        } else if (comparator != blR || (k instanceof Comparable)) {
            com_google_android_gms_internal_zzapf_zzd2 = new zzd(com_google_android_gms_internal_zzapf_zzd, k, com_google_android_gms_internal_zzapf_zzd3, com_google_android_gms_internal_zzapf_zzd3.bmf);
            this.blS = com_google_android_gms_internal_zzapf_zzd2;
        } else {
            throw new ClassCastException(String.valueOf(k.getClass().getName()).concat(" is not Comparable"));
        }
        this.size++;
        this.modCount++;
        return com_google_android_gms_internal_zzapf_zzd2;
    }

    void zza(zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V, boolean z) {
        int i = 0;
        if (z) {
            com_google_android_gms_internal_zzapf_zzd_K__V.bmf.blZ = com_google_android_gms_internal_zzapf_zzd_K__V.blZ;
            com_google_android_gms_internal_zzapf_zzd_K__V.blZ.bmf = com_google_android_gms_internal_zzapf_zzd_K__V.bmf;
        }
        zzd com_google_android_gms_internal_zzapf_zzd = com_google_android_gms_internal_zzapf_zzd_K__V.bmd;
        zzd com_google_android_gms_internal_zzapf_zzd2 = com_google_android_gms_internal_zzapf_zzd_K__V.bme;
        zzd com_google_android_gms_internal_zzapf_zzd3 = com_google_android_gms_internal_zzapf_zzd_K__V.bmc;
        if (com_google_android_gms_internal_zzapf_zzd == null || com_google_android_gms_internal_zzapf_zzd2 == null) {
            if (com_google_android_gms_internal_zzapf_zzd != null) {
                zza((zzd) com_google_android_gms_internal_zzapf_zzd_K__V, com_google_android_gms_internal_zzapf_zzd);
                com_google_android_gms_internal_zzapf_zzd_K__V.bmd = null;
            } else if (com_google_android_gms_internal_zzapf_zzd2 != null) {
                zza((zzd) com_google_android_gms_internal_zzapf_zzd_K__V, com_google_android_gms_internal_zzapf_zzd2);
                com_google_android_gms_internal_zzapf_zzd_K__V.bme = null;
            } else {
                zza((zzd) com_google_android_gms_internal_zzapf_zzd_K__V, null);
            }
            zzb(com_google_android_gms_internal_zzapf_zzd3, false);
            this.size--;
            this.modCount++;
            return;
        }
        int i2;
        com_google_android_gms_internal_zzapf_zzd = com_google_android_gms_internal_zzapf_zzd.height > com_google_android_gms_internal_zzapf_zzd2.height ? com_google_android_gms_internal_zzapf_zzd.bk() : com_google_android_gms_internal_zzapf_zzd2.bj();
        zza(com_google_android_gms_internal_zzapf_zzd, false);
        com_google_android_gms_internal_zzapf_zzd3 = com_google_android_gms_internal_zzapf_zzd_K__V.bmd;
        if (com_google_android_gms_internal_zzapf_zzd3 != null) {
            i2 = com_google_android_gms_internal_zzapf_zzd3.height;
            com_google_android_gms_internal_zzapf_zzd.bmd = com_google_android_gms_internal_zzapf_zzd3;
            com_google_android_gms_internal_zzapf_zzd3.bmc = com_google_android_gms_internal_zzapf_zzd;
            com_google_android_gms_internal_zzapf_zzd_K__V.bmd = null;
        } else {
            i2 = 0;
        }
        com_google_android_gms_internal_zzapf_zzd3 = com_google_android_gms_internal_zzapf_zzd_K__V.bme;
        if (com_google_android_gms_internal_zzapf_zzd3 != null) {
            i = com_google_android_gms_internal_zzapf_zzd3.height;
            com_google_android_gms_internal_zzapf_zzd.bme = com_google_android_gms_internal_zzapf_zzd3;
            com_google_android_gms_internal_zzapf_zzd3.bmc = com_google_android_gms_internal_zzapf_zzd;
            com_google_android_gms_internal_zzapf_zzd_K__V.bme = null;
        }
        com_google_android_gms_internal_zzapf_zzd.height = Math.max(i2, i) + 1;
        zza((zzd) com_google_android_gms_internal_zzapf_zzd_K__V, com_google_android_gms_internal_zzapf_zzd);
    }

    zzd<K, V> zzc(Entry<?, ?> entry) {
        zzd<K, V> zzcr = zzcr(entry.getKey());
        Object obj = (zzcr == null || !equal(zzcr.value, entry.getValue())) ? null : 1;
        return obj != null ? zzcr : null;
    }

    zzd<K, V> zzcr(Object obj) {
        zzd<K, V> com_google_android_gms_internal_zzapf_zzd_K__V = null;
        if (obj != null) {
            try {
                com_google_android_gms_internal_zzapf_zzd_K__V = zza(obj, false);
            } catch (ClassCastException e) {
            }
        }
        return com_google_android_gms_internal_zzapf_zzd_K__V;
    }

    zzd<K, V> zzcs(Object obj) {
        zzd zzcr = zzcr(obj);
        if (zzcr != null) {
            zza(zzcr, true);
        }
        return zzcr;
    }
}
