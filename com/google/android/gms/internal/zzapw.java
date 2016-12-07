package com.google.android.gms.internal;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.UUID;

public final class zzapw {
    public static final zzaot<Class> bmS = new zzaot<Class>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Class cls) throws IOException {
            if (cls == null) {
                com_google_android_gms_internal_zzaqa.bx();
            } else {
                String valueOf = String.valueOf(cls.getName());
                throw new UnsupportedOperationException(new StringBuilder(String.valueOf(valueOf).length() + 76).append("Attempted to serialize java.lang.Class: ").append(valueOf).append(". Forgot to register a type adapter?").toString());
            }
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzo(com_google_android_gms_internal_zzapy);
        }

        public Class zzo(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
        }
    };
    public static final zzaou bmT = zza(Class.class, bmS);
    public static final zzaot<BitSet> bmU = new zzaot<BitSet>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, BitSet bitSet) throws IOException {
            if (bitSet == null) {
                com_google_android_gms_internal_zzaqa.bx();
                return;
            }
            com_google_android_gms_internal_zzaqa.bt();
            for (int i = 0; i < bitSet.length(); i++) {
                com_google_android_gms_internal_zzaqa.zzcu((long) (bitSet.get(i) ? 1 : 0));
            }
            com_google_android_gms_internal_zzaqa.bu();
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzx(com_google_android_gms_internal_zzapy);
        }

        public BitSet zzx(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            BitSet bitSet = new BitSet();
            com_google_android_gms_internal_zzapy.beginArray();
            zzapz bn = com_google_android_gms_internal_zzapy.bn();
            int i = 0;
            while (bn != zzapz.END_ARRAY) {
                boolean z;
                String valueOf;
                switch (bn) {
                    case NUMBER:
                        if (com_google_android_gms_internal_zzapy.nextInt() == 0) {
                            z = false;
                            break;
                        }
                        z = true;
                        break;
                    case BOOLEAN:
                        z = com_google_android_gms_internal_zzapy.nextBoolean();
                        break;
                    case STRING:
                        Object nextString = com_google_android_gms_internal_zzapy.nextString();
                        try {
                            if (Integer.parseInt(nextString) == 0) {
                                z = false;
                                break;
                            }
                            z = true;
                            break;
                        } catch (NumberFormatException e) {
                            String str = "Error: Expecting: bitset number value (1, 0), Found: ";
                            valueOf = String.valueOf(nextString);
                            throw new zzaoq(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
                        }
                    default:
                        valueOf = String.valueOf(bn);
                        throw new zzaoq(new StringBuilder(String.valueOf(valueOf).length() + 27).append("Invalid bitset value type: ").append(valueOf).toString());
                }
                if (z) {
                    bitSet.set(i);
                }
                i++;
                bn = com_google_android_gms_internal_zzapy.bn();
            }
            com_google_android_gms_internal_zzapy.endArray();
            return bitSet;
        }
    };
    public static final zzaou bmV = zza(BitSet.class, bmU);
    public static final zzaot<Boolean> bmW = new zzaot<Boolean>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Boolean bool) throws IOException {
            if (bool == null) {
                com_google_android_gms_internal_zzaqa.bx();
            } else {
                com_google_android_gms_internal_zzaqa.zzdf(bool.booleanValue());
            }
        }

        public Boolean zzae(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return com_google_android_gms_internal_zzapy.bn() == zzapz.STRING ? Boolean.valueOf(Boolean.parseBoolean(com_google_android_gms_internal_zzapy.nextString())) : Boolean.valueOf(com_google_android_gms_internal_zzapy.nextBoolean());
            } else {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzae(com_google_android_gms_internal_zzapy);
        }
    };
    public static final zzaot<Boolean> bmX = new zzaot<Boolean>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Boolean bool) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(bool == null ? "null" : bool.toString());
        }

        public Boolean zzae(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return Boolean.valueOf(com_google_android_gms_internal_zzapy.nextString());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzae(com_google_android_gms_internal_zzapy);
        }
    };
    public static final zzaou bmY = zza(Boolean.TYPE, Boolean.class, bmW);
    public static final zzaot<Number> bmZ = new zzaot<Number>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) throws IOException {
            com_google_android_gms_internal_zzaqa.zza(number);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzg(com_google_android_gms_internal_zzapy);
        }

        public Number zzg(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            try {
                return Byte.valueOf((byte) com_google_android_gms_internal_zzapy.nextInt());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
    };
    public static final zzaot<UUID> bnA = new zzaot<UUID>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, UUID uuid) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(uuid == null ? null : uuid.toString());
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzz(com_google_android_gms_internal_zzapy);
        }

        public UUID zzz(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return UUID.fromString(com_google_android_gms_internal_zzapy.nextString());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }
    };
    public static final zzaou bnB = zza(UUID.class, bnA);
    public static final zzaou bnC = new zzaou() {
        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            if (com_google_android_gms_internal_zzapx_T.by() != Timestamp.class) {
                return null;
            }
            final zzaot zzk = com_google_android_gms_internal_zzaob.zzk(Date.class);
            return new zzaot<Timestamp>(this) {
                final /* synthetic */ AnonymousClass15 bnL;

                public void zza(zzaqa com_google_android_gms_internal_zzaqa, Timestamp timestamp) throws IOException {
                    zzk.zza(com_google_android_gms_internal_zzaqa, timestamp);
                }

                public Timestamp zzaa(zzapy com_google_android_gms_internal_zzapy) throws IOException {
                    Date date = (Date) zzk.zzb(com_google_android_gms_internal_zzapy);
                    return date != null ? new Timestamp(date.getTime()) : null;
                }

                public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
                    return zzaa(com_google_android_gms_internal_zzapy);
                }
            };
        }
    };
    public static final zzaot<Calendar> bnD = new zzaot<Calendar>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Calendar calendar) throws IOException {
            if (calendar == null) {
                com_google_android_gms_internal_zzaqa.bx();
                return;
            }
            com_google_android_gms_internal_zzaqa.bv();
            com_google_android_gms_internal_zzaqa.zzus("year");
            com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(1));
            com_google_android_gms_internal_zzaqa.zzus("month");
            com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(2));
            com_google_android_gms_internal_zzaqa.zzus("dayOfMonth");
            com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(5));
            com_google_android_gms_internal_zzaqa.zzus("hourOfDay");
            com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(11));
            com_google_android_gms_internal_zzaqa.zzus("minute");
            com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(12));
            com_google_android_gms_internal_zzaqa.zzus("second");
            com_google_android_gms_internal_zzaqa.zzcu((long) calendar.get(13));
            com_google_android_gms_internal_zzaqa.bw();
        }

        public Calendar zzab(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            int i = 0;
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            com_google_android_gms_internal_zzapy.beginObject();
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            while (com_google_android_gms_internal_zzapy.bn() != zzapz.END_OBJECT) {
                String nextName = com_google_android_gms_internal_zzapy.nextName();
                int nextInt = com_google_android_gms_internal_zzapy.nextInt();
                if ("year".equals(nextName)) {
                    i6 = nextInt;
                } else if ("month".equals(nextName)) {
                    i5 = nextInt;
                } else if ("dayOfMonth".equals(nextName)) {
                    i4 = nextInt;
                } else if ("hourOfDay".equals(nextName)) {
                    i3 = nextInt;
                } else if ("minute".equals(nextName)) {
                    i2 = nextInt;
                } else if ("second".equals(nextName)) {
                    i = nextInt;
                }
            }
            com_google_android_gms_internal_zzapy.endObject();
            return new GregorianCalendar(i6, i5, i4, i3, i2, i);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzab(com_google_android_gms_internal_zzapy);
        }
    };
    public static final zzaou bnE = zzb(Calendar.class, GregorianCalendar.class, bnD);
    public static final zzaot<Locale> bnF = new zzaot<Locale>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Locale locale) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(locale == null ? null : locale.toString());
        }

        public Locale zzac(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            StringTokenizer stringTokenizer = new StringTokenizer(com_google_android_gms_internal_zzapy.nextString(), "_");
            String nextToken = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
            String nextToken2 = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
            String nextToken3 = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
            return (nextToken2 == null && nextToken3 == null) ? new Locale(nextToken) : nextToken3 == null ? new Locale(nextToken, nextToken2) : new Locale(nextToken, nextToken2, nextToken3);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzac(com_google_android_gms_internal_zzapy);
        }
    };
    public static final zzaou bnG = zza(Locale.class, bnF);
    public static final zzaot<zzaoh> bnH = new zzaot<zzaoh>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, zzaoh com_google_android_gms_internal_zzaoh) throws IOException {
            if (com_google_android_gms_internal_zzaoh == null || com_google_android_gms_internal_zzaoh.aV()) {
                com_google_android_gms_internal_zzaqa.bx();
            } else if (com_google_android_gms_internal_zzaoh.aU()) {
                zzaon aY = com_google_android_gms_internal_zzaoh.aY();
                if (aY.bb()) {
                    com_google_android_gms_internal_zzaqa.zza(aY.aQ());
                } else if (aY.ba()) {
                    com_google_android_gms_internal_zzaqa.zzdf(aY.getAsBoolean());
                } else {
                    com_google_android_gms_internal_zzaqa.zzut(aY.aR());
                }
            } else if (com_google_android_gms_internal_zzaoh.aS()) {
                com_google_android_gms_internal_zzaqa.bt();
                Iterator it = com_google_android_gms_internal_zzaoh.aX().iterator();
                while (it.hasNext()) {
                    zza(com_google_android_gms_internal_zzaqa, (zzaoh) it.next());
                }
                com_google_android_gms_internal_zzaqa.bu();
            } else if (com_google_android_gms_internal_zzaoh.aT()) {
                com_google_android_gms_internal_zzaqa.bv();
                for (Entry entry : com_google_android_gms_internal_zzaoh.aW().entrySet()) {
                    com_google_android_gms_internal_zzaqa.zzus((String) entry.getKey());
                    zza(com_google_android_gms_internal_zzaqa, (zzaoh) entry.getValue());
                }
                com_google_android_gms_internal_zzaqa.bw();
            } else {
                String valueOf = String.valueOf(com_google_android_gms_internal_zzaoh.getClass());
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 15).append("Couldn't write ").append(valueOf).toString());
            }
        }

        public zzaoh zzad(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            zzaoh com_google_android_gms_internal_zzaoe;
            switch (com_google_android_gms_internal_zzapy.bn()) {
                case NUMBER:
                    return new zzaon(new zzape(com_google_android_gms_internal_zzapy.nextString()));
                case BOOLEAN:
                    return new zzaon(Boolean.valueOf(com_google_android_gms_internal_zzapy.nextBoolean()));
                case STRING:
                    return new zzaon(com_google_android_gms_internal_zzapy.nextString());
                case NULL:
                    com_google_android_gms_internal_zzapy.nextNull();
                    return zzaoj.bld;
                case BEGIN_ARRAY:
                    com_google_android_gms_internal_zzaoe = new zzaoe();
                    com_google_android_gms_internal_zzapy.beginArray();
                    while (com_google_android_gms_internal_zzapy.hasNext()) {
                        com_google_android_gms_internal_zzaoe.zzc((zzaoh) zzb(com_google_android_gms_internal_zzapy));
                    }
                    com_google_android_gms_internal_zzapy.endArray();
                    return com_google_android_gms_internal_zzaoe;
                case BEGIN_OBJECT:
                    com_google_android_gms_internal_zzaoe = new zzaok();
                    com_google_android_gms_internal_zzapy.beginObject();
                    while (com_google_android_gms_internal_zzapy.hasNext()) {
                        com_google_android_gms_internal_zzaoe.zza(com_google_android_gms_internal_zzapy.nextName(), (zzaoh) zzb(com_google_android_gms_internal_zzapy));
                    }
                    com_google_android_gms_internal_zzapy.endObject();
                    return com_google_android_gms_internal_zzaoe;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzad(com_google_android_gms_internal_zzapy);
        }
    };
    public static final zzaou bnI = zzb(zzaoh.class, bnH);
    public static final zzaou bnJ = new zzaou() {
        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            Class by = com_google_android_gms_internal_zzapx_T.by();
            if (!Enum.class.isAssignableFrom(by) || by == Enum.class) {
                return null;
            }
            if (!by.isEnum()) {
                by = by.getSuperclass();
            }
            return new zza(by);
        }
    };
    public static final zzaou bna = zza(Byte.TYPE, Byte.class, bmZ);
    public static final zzaot<Number> bnb = new zzaot<Number>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) throws IOException {
            com_google_android_gms_internal_zzaqa.zza(number);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzg(com_google_android_gms_internal_zzapy);
        }

        public Number zzg(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            try {
                return Short.valueOf((short) com_google_android_gms_internal_zzapy.nextInt());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
    };
    public static final zzaou bnc = zza(Short.TYPE, Short.class, bnb);
    public static final zzaot<Number> bnd = new zzaot<Number>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) throws IOException {
            com_google_android_gms_internal_zzaqa.zza(number);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzg(com_google_android_gms_internal_zzapy);
        }

        public Number zzg(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            try {
                return Integer.valueOf(com_google_android_gms_internal_zzapy.nextInt());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
    };
    public static final zzaou bne = zza(Integer.TYPE, Integer.class, bnd);
    public static final zzaot<Number> bnf = new zzaot<Number>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) throws IOException {
            com_google_android_gms_internal_zzaqa.zza(number);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzg(com_google_android_gms_internal_zzapy);
        }

        public Number zzg(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            try {
                return Long.valueOf(com_google_android_gms_internal_zzapy.nextLong());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
    };
    public static final zzaot<Number> bng = new zzaot<Number>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) throws IOException {
            com_google_android_gms_internal_zzaqa.zza(number);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzg(com_google_android_gms_internal_zzapy);
        }

        public Number zzg(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return Float.valueOf((float) com_google_android_gms_internal_zzapy.nextDouble());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }
    };
    public static final zzaot<Number> bnh = new zzaot<Number>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) throws IOException {
            com_google_android_gms_internal_zzaqa.zza(number);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzg(com_google_android_gms_internal_zzapy);
        }

        public Number zzg(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return Double.valueOf(com_google_android_gms_internal_zzapy.nextDouble());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }
    };
    public static final zzaot<Number> bni = new zzaot<Number>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) throws IOException {
            com_google_android_gms_internal_zzaqa.zza(number);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzg(com_google_android_gms_internal_zzapy);
        }

        public Number zzg(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            zzapz bn = com_google_android_gms_internal_zzapy.bn();
            switch (bn) {
                case NUMBER:
                    return new zzape(com_google_android_gms_internal_zzapy.nextString());
                case NULL:
                    com_google_android_gms_internal_zzapy.nextNull();
                    return null;
                default:
                    String valueOf = String.valueOf(bn);
                    throw new zzaoq(new StringBuilder(String.valueOf(valueOf).length() + 23).append("Expecting number, got: ").append(valueOf).toString());
            }
        }
    };
    public static final zzaou bnj = zza(Number.class, bni);
    public static final zzaot<Character> bnk = new zzaot<Character>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, Character ch) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(ch == null ? null : String.valueOf(ch));
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzp(com_google_android_gms_internal_zzapy);
        }

        public Character zzp(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            String nextString = com_google_android_gms_internal_zzapy.nextString();
            if (nextString.length() == 1) {
                return Character.valueOf(nextString.charAt(0));
            }
            String str = "Expecting character, got: ";
            nextString = String.valueOf(nextString);
            throw new zzaoq(nextString.length() != 0 ? str.concat(nextString) : new String(str));
        }
    };
    public static final zzaou bnl = zza(Character.TYPE, Character.class, bnk);
    public static final zzaot<String> bnm = new zzaot<String>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, String str) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(str);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzq(com_google_android_gms_internal_zzapy);
        }

        public String zzq(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            zzapz bn = com_google_android_gms_internal_zzapy.bn();
            if (bn != zzapz.NULL) {
                return bn == zzapz.BOOLEAN ? Boolean.toString(com_google_android_gms_internal_zzapy.nextBoolean()) : com_google_android_gms_internal_zzapy.nextString();
            } else {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
        }
    };
    public static final zzaot<BigDecimal> bnn = new zzaot<BigDecimal>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, BigDecimal bigDecimal) throws IOException {
            com_google_android_gms_internal_zzaqa.zza(bigDecimal);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzr(com_google_android_gms_internal_zzapy);
        }

        public BigDecimal zzr(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            try {
                return new BigDecimal(com_google_android_gms_internal_zzapy.nextString());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
    };
    public static final zzaot<BigInteger> bno = new zzaot<BigInteger>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, BigInteger bigInteger) throws IOException {
            com_google_android_gms_internal_zzaqa.zza(bigInteger);
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzs(com_google_android_gms_internal_zzapy);
        }

        public BigInteger zzs(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            try {
                return new BigInteger(com_google_android_gms_internal_zzapy.nextString());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
    };
    public static final zzaou bnp = zza(String.class, bnm);
    public static final zzaot<StringBuilder> bnq = new zzaot<StringBuilder>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, StringBuilder stringBuilder) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(stringBuilder == null ? null : stringBuilder.toString());
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzt(com_google_android_gms_internal_zzapy);
        }

        public StringBuilder zzt(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return new StringBuilder(com_google_android_gms_internal_zzapy.nextString());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }
    };
    public static final zzaou bnr = zza(StringBuilder.class, bnq);
    public static final zzaot<StringBuffer> bns = new zzaot<StringBuffer>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, StringBuffer stringBuffer) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(stringBuffer == null ? null : stringBuffer.toString());
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzu(com_google_android_gms_internal_zzapy);
        }

        public StringBuffer zzu(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return new StringBuffer(com_google_android_gms_internal_zzapy.nextString());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }
    };
    public static final zzaou bnt = zza(StringBuffer.class, bns);
    public static final zzaot<URL> bnu = new zzaot<URL>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, URL url) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(url == null ? null : url.toExternalForm());
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzv(com_google_android_gms_internal_zzapy);
        }

        public URL zzv(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
            String nextString = com_google_android_gms_internal_zzapy.nextString();
            return !"null".equals(nextString) ? new URL(nextString) : null;
        }
    };
    public static final zzaou bnv = zza(URL.class, bnu);
    public static final zzaot<URI> bnw = new zzaot<URI>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, URI uri) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(uri == null ? null : uri.toASCIIString());
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzw(com_google_android_gms_internal_zzapy);
        }

        public URI zzw(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            URI uri = null;
            if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
                com_google_android_gms_internal_zzapy.nextNull();
            } else {
                try {
                    String nextString = com_google_android_gms_internal_zzapy.nextString();
                    if (!"null".equals(nextString)) {
                        uri = new URI(nextString);
                    }
                } catch (Throwable e) {
                    throw new zzaoi(e);
                }
            }
            return uri;
        }
    };
    public static final zzaou bnx = zza(URI.class, bnw);
    public static final zzaot<InetAddress> bny = new zzaot<InetAddress>() {
        public void zza(zzaqa com_google_android_gms_internal_zzaqa, InetAddress inetAddress) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(inetAddress == null ? null : inetAddress.getHostAddress());
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzy(com_google_android_gms_internal_zzapy);
        }

        public InetAddress zzy(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return InetAddress.getByName(com_google_android_gms_internal_zzapy.nextString());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }
    };
    public static final zzaou bnz = zzb(InetAddress.class, bny);

    private static final class zza<T extends Enum<T>> extends zzaot<T> {
        private final Map<String, T> bnT = new HashMap();
        private final Map<T, String> bnU = new HashMap();

        public zza(Class<T> cls) {
            try {
                for (Enum enumR : (Enum[]) cls.getEnumConstants()) {
                    String name = enumR.name();
                    zzaow com_google_android_gms_internal_zzaow = (zzaow) cls.getField(name).getAnnotation(zzaow.class);
                    if (com_google_android_gms_internal_zzaow != null) {
                        name = com_google_android_gms_internal_zzaow.value();
                        for (Object put : com_google_android_gms_internal_zzaow.be()) {
                            this.bnT.put(put, enumR);
                        }
                    }
                    String str = name;
                    this.bnT.put(str, enumR);
                    this.bnU.put(enumR, str);
                }
            } catch (NoSuchFieldException e) {
                throw new AssertionError();
            }
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, T t) throws IOException {
            com_google_android_gms_internal_zzaqa.zzut(t == null ? null : (String) this.bnU.get(t));
        }

        public T zzaf(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                return (Enum) this.bnT.get(com_google_android_gms_internal_zzapy.nextString());
            }
            com_google_android_gms_internal_zzapy.nextNull();
            return null;
        }

        public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            return zzaf(com_google_android_gms_internal_zzapy);
        }
    }

    public static <TT> zzaou zza(final zzapx<TT> com_google_android_gms_internal_zzapx_TT, final zzaot<TT> com_google_android_gms_internal_zzaot_TT) {
        return new zzaou() {
            public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
                return com_google_android_gms_internal_zzapx_T.equals(com_google_android_gms_internal_zzapx_TT) ? com_google_android_gms_internal_zzaot_TT : null;
            }
        };
    }

    public static <TT> zzaou zza(final Class<TT> cls, final zzaot<TT> com_google_android_gms_internal_zzaot_TT) {
        return new zzaou() {
            public String toString() {
                String valueOf = String.valueOf(cls.getName());
                String valueOf2 = String.valueOf(com_google_android_gms_internal_zzaot_TT);
                return new StringBuilder((String.valueOf(valueOf).length() + 23) + String.valueOf(valueOf2).length()).append("Factory[type=").append(valueOf).append(",adapter=").append(valueOf2).append("]").toString();
            }

            public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
                return com_google_android_gms_internal_zzapx_T.by() == cls ? com_google_android_gms_internal_zzaot_TT : null;
            }
        };
    }

    public static <TT> zzaou zza(final Class<TT> cls, final Class<TT> cls2, final zzaot<? super TT> com_google_android_gms_internal_zzaot__super_TT) {
        return new zzaou() {
            public String toString() {
                String valueOf = String.valueOf(cls2.getName());
                String valueOf2 = String.valueOf(cls.getName());
                String valueOf3 = String.valueOf(com_google_android_gms_internal_zzaot__super_TT);
                return new StringBuilder(((String.valueOf(valueOf).length() + 24) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Factory[type=").append(valueOf).append("+").append(valueOf2).append(",adapter=").append(valueOf3).append("]").toString();
            }

            public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
                Class by = com_google_android_gms_internal_zzapx_T.by();
                return (by == cls || by == cls2) ? com_google_android_gms_internal_zzaot__super_TT : null;
            }
        };
    }

    public static <TT> zzaou zzb(final Class<TT> cls, final zzaot<TT> com_google_android_gms_internal_zzaot_TT) {
        return new zzaou() {
            public String toString() {
                String valueOf = String.valueOf(cls.getName());
                String valueOf2 = String.valueOf(com_google_android_gms_internal_zzaot_TT);
                return new StringBuilder((String.valueOf(valueOf).length() + 32) + String.valueOf(valueOf2).length()).append("Factory[typeHierarchy=").append(valueOf).append(",adapter=").append(valueOf2).append("]").toString();
            }

            public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
                return cls.isAssignableFrom(com_google_android_gms_internal_zzapx_T.by()) ? com_google_android_gms_internal_zzaot_TT : null;
            }
        };
    }

    public static <TT> zzaou zzb(final Class<TT> cls, final Class<? extends TT> cls2, final zzaot<? super TT> com_google_android_gms_internal_zzaot__super_TT) {
        return new zzaou() {
            public String toString() {
                String valueOf = String.valueOf(cls.getName());
                String valueOf2 = String.valueOf(cls2.getName());
                String valueOf3 = String.valueOf(com_google_android_gms_internal_zzaot__super_TT);
                return new StringBuilder(((String.valueOf(valueOf).length() + 24) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Factory[type=").append(valueOf).append("+").append(valueOf2).append(",adapter=").append(valueOf3).append("]").toString();
            }

            public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
                Class by = com_google_android_gms_internal_zzapx_T.by();
                return (by == cls || by == cls2) ? com_google_android_gms_internal_zzaot__super_TT : null;
            }
        };
    }
}
