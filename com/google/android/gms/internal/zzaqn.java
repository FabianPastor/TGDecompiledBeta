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

public final class zzaqn {
    public static final zzapl bqA = zza(Number.class, bqz);
    public static final zzapk<Character> bqB = new zzapk<Character>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Character ch) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(ch == null ? null : String.valueOf(ch));
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzp(com_google_android_gms_internal_zzaqp);
        }

        public Character zzp(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            String nextString = com_google_android_gms_internal_zzaqp.nextString();
            if (nextString.length() == 1) {
                return Character.valueOf(nextString.charAt(0));
            }
            String str = "Expecting character, got: ";
            nextString = String.valueOf(nextString);
            throw new zzaph(nextString.length() != 0 ? str.concat(nextString) : new String(str));
        }
    };
    public static final zzapl bqC = zza(Character.TYPE, Character.class, bqB);
    public static final zzapk<String> bqD = new zzapk<String>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, String str) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(str);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzq(com_google_android_gms_internal_zzaqp);
        }

        public String zzq(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            zzaqq bq = com_google_android_gms_internal_zzaqp.bq();
            if (bq != zzaqq.NULL) {
                return bq == zzaqq.BOOLEAN ? Boolean.toString(com_google_android_gms_internal_zzaqp.nextBoolean()) : com_google_android_gms_internal_zzaqp.nextString();
            } else {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
        }
    };
    public static final zzapk<BigDecimal> bqE = new zzapk<BigDecimal>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, BigDecimal bigDecimal) throws IOException {
            com_google_android_gms_internal_zzaqr.zza(bigDecimal);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzr(com_google_android_gms_internal_zzaqp);
        }

        public BigDecimal zzr(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            try {
                return new BigDecimal(com_google_android_gms_internal_zzaqp.nextString());
            } catch (Throwable e) {
                throw new zzaph(e);
            }
        }
    };
    public static final zzapk<BigInteger> bqF = new zzapk<BigInteger>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, BigInteger bigInteger) throws IOException {
            com_google_android_gms_internal_zzaqr.zza(bigInteger);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzs(com_google_android_gms_internal_zzaqp);
        }

        public BigInteger zzs(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            try {
                return new BigInteger(com_google_android_gms_internal_zzaqp.nextString());
            } catch (Throwable e) {
                throw new zzaph(e);
            }
        }
    };
    public static final zzapl bqG = zza(String.class, bqD);
    public static final zzapk<StringBuilder> bqH = new zzapk<StringBuilder>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, StringBuilder stringBuilder) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(stringBuilder == null ? null : stringBuilder.toString());
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzt(com_google_android_gms_internal_zzaqp);
        }

        public StringBuilder zzt(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                return new StringBuilder(com_google_android_gms_internal_zzaqp.nextString());
            }
            com_google_android_gms_internal_zzaqp.nextNull();
            return null;
        }
    };
    public static final zzapl bqI = zza(StringBuilder.class, bqH);
    public static final zzapk<StringBuffer> bqJ = new zzapk<StringBuffer>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, StringBuffer stringBuffer) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(stringBuffer == null ? null : stringBuffer.toString());
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzu(com_google_android_gms_internal_zzaqp);
        }

        public StringBuffer zzu(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                return new StringBuffer(com_google_android_gms_internal_zzaqp.nextString());
            }
            com_google_android_gms_internal_zzaqp.nextNull();
            return null;
        }
    };
    public static final zzapl bqK = zza(StringBuffer.class, bqJ);
    public static final zzapk<URL> bqL = new zzapk<URL>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, URL url) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(url == null ? null : url.toExternalForm());
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzv(com_google_android_gms_internal_zzaqp);
        }

        public URL zzv(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            String nextString = com_google_android_gms_internal_zzaqp.nextString();
            return !"null".equals(nextString) ? new URL(nextString) : null;
        }
    };
    public static final zzapl bqM = zza(URL.class, bqL);
    public static final zzapk<URI> bqN = new zzapk<URI>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, URI uri) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(uri == null ? null : uri.toASCIIString());
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzw(com_google_android_gms_internal_zzaqp);
        }

        public URI zzw(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            URI uri = null;
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
            } else {
                try {
                    String nextString = com_google_android_gms_internal_zzaqp.nextString();
                    if (!"null".equals(nextString)) {
                        uri = new URI(nextString);
                    }
                } catch (Throwable e) {
                    throw new zzaoz(e);
                }
            }
            return uri;
        }
    };
    public static final zzapl bqO = zza(URI.class, bqN);
    public static final zzapk<InetAddress> bqP = new zzapk<InetAddress>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, InetAddress inetAddress) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(inetAddress == null ? null : inetAddress.getHostAddress());
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzy(com_google_android_gms_internal_zzaqp);
        }

        public InetAddress zzy(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                return InetAddress.getByName(com_google_android_gms_internal_zzaqp.nextString());
            }
            com_google_android_gms_internal_zzaqp.nextNull();
            return null;
        }
    };
    public static final zzapl bqQ = zzb(InetAddress.class, bqP);
    public static final zzapk<UUID> bqR = new zzapk<UUID>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, UUID uuid) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(uuid == null ? null : uuid.toString());
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzz(com_google_android_gms_internal_zzaqp);
        }

        public UUID zzz(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                return UUID.fromString(com_google_android_gms_internal_zzaqp.nextString());
            }
            com_google_android_gms_internal_zzaqp.nextNull();
            return null;
        }
    };
    public static final zzapl bqS = zza(UUID.class, bqR);
    public static final zzapl bqT = new zzapl() {
        public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
            if (com_google_android_gms_internal_zzaqo_T.bB() != Timestamp.class) {
                return null;
            }
            final zzapk zzk = com_google_android_gms_internal_zzaos.zzk(Date.class);
            return new zzapk<Timestamp>(this) {
                final /* synthetic */ AnonymousClass15 brc;

                public void zza(zzaqr com_google_android_gms_internal_zzaqr, Timestamp timestamp) throws IOException {
                    zzk.zza(com_google_android_gms_internal_zzaqr, timestamp);
                }

                public Timestamp zzaa(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
                    Date date = (Date) zzk.zzb(com_google_android_gms_internal_zzaqp);
                    return date != null ? new Timestamp(date.getTime()) : null;
                }

                public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
                    return zzaa(com_google_android_gms_internal_zzaqp);
                }
            };
        }
    };
    public static final zzapk<Calendar> bqU = new zzapk<Calendar>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Calendar calendar) throws IOException {
            if (calendar == null) {
                com_google_android_gms_internal_zzaqr.bA();
                return;
            }
            com_google_android_gms_internal_zzaqr.by();
            com_google_android_gms_internal_zzaqr.zzus("year");
            com_google_android_gms_internal_zzaqr.zzcs((long) calendar.get(1));
            com_google_android_gms_internal_zzaqr.zzus("month");
            com_google_android_gms_internal_zzaqr.zzcs((long) calendar.get(2));
            com_google_android_gms_internal_zzaqr.zzus("dayOfMonth");
            com_google_android_gms_internal_zzaqr.zzcs((long) calendar.get(5));
            com_google_android_gms_internal_zzaqr.zzus("hourOfDay");
            com_google_android_gms_internal_zzaqr.zzcs((long) calendar.get(11));
            com_google_android_gms_internal_zzaqr.zzus("minute");
            com_google_android_gms_internal_zzaqr.zzcs((long) calendar.get(12));
            com_google_android_gms_internal_zzaqr.zzus("second");
            com_google_android_gms_internal_zzaqr.zzcs((long) calendar.get(13));
            com_google_android_gms_internal_zzaqr.bz();
        }

        public Calendar zzab(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            int i = 0;
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            com_google_android_gms_internal_zzaqp.beginObject();
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            while (com_google_android_gms_internal_zzaqp.bq() != zzaqq.END_OBJECT) {
                String nextName = com_google_android_gms_internal_zzaqp.nextName();
                int nextInt = com_google_android_gms_internal_zzaqp.nextInt();
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
            com_google_android_gms_internal_zzaqp.endObject();
            return new GregorianCalendar(i6, i5, i4, i3, i2, i);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzab(com_google_android_gms_internal_zzaqp);
        }
    };
    public static final zzapl bqV = zzb(Calendar.class, GregorianCalendar.class, bqU);
    public static final zzapk<Locale> bqW = new zzapk<Locale>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Locale locale) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(locale == null ? null : locale.toString());
        }

        public Locale zzac(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            StringTokenizer stringTokenizer = new StringTokenizer(com_google_android_gms_internal_zzaqp.nextString(), "_");
            String nextToken = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
            String nextToken2 = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
            String nextToken3 = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
            return (nextToken2 == null && nextToken3 == null) ? new Locale(nextToken) : nextToken3 == null ? new Locale(nextToken, nextToken2) : new Locale(nextToken, nextToken2, nextToken3);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzac(com_google_android_gms_internal_zzaqp);
        }
    };
    public static final zzapl bqX = zza(Locale.class, bqW);
    public static final zzapk<zzaoy> bqY = new zzapk<zzaoy>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, zzaoy com_google_android_gms_internal_zzaoy) throws IOException {
            if (com_google_android_gms_internal_zzaoy == null || com_google_android_gms_internal_zzaoy.aY()) {
                com_google_android_gms_internal_zzaqr.bA();
            } else if (com_google_android_gms_internal_zzaoy.aX()) {
                zzape bb = com_google_android_gms_internal_zzaoy.bb();
                if (bb.be()) {
                    com_google_android_gms_internal_zzaqr.zza(bb.aT());
                } else if (bb.bd()) {
                    com_google_android_gms_internal_zzaqr.zzdh(bb.getAsBoolean());
                } else {
                    com_google_android_gms_internal_zzaqr.zzut(bb.aU());
                }
            } else if (com_google_android_gms_internal_zzaoy.aV()) {
                com_google_android_gms_internal_zzaqr.bw();
                Iterator it = com_google_android_gms_internal_zzaoy.ba().iterator();
                while (it.hasNext()) {
                    zza(com_google_android_gms_internal_zzaqr, (zzaoy) it.next());
                }
                com_google_android_gms_internal_zzaqr.bx();
            } else if (com_google_android_gms_internal_zzaoy.aW()) {
                com_google_android_gms_internal_zzaqr.by();
                for (Entry entry : com_google_android_gms_internal_zzaoy.aZ().entrySet()) {
                    com_google_android_gms_internal_zzaqr.zzus((String) entry.getKey());
                    zza(com_google_android_gms_internal_zzaqr, (zzaoy) entry.getValue());
                }
                com_google_android_gms_internal_zzaqr.bz();
            } else {
                String valueOf = String.valueOf(com_google_android_gms_internal_zzaoy.getClass());
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 15).append("Couldn't write ").append(valueOf).toString());
            }
        }

        public zzaoy zzad(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            zzaoy com_google_android_gms_internal_zzaov;
            switch (com_google_android_gms_internal_zzaqp.bq()) {
                case NUMBER:
                    return new zzape(new zzapv(com_google_android_gms_internal_zzaqp.nextString()));
                case BOOLEAN:
                    return new zzape(Boolean.valueOf(com_google_android_gms_internal_zzaqp.nextBoolean()));
                case STRING:
                    return new zzape(com_google_android_gms_internal_zzaqp.nextString());
                case NULL:
                    com_google_android_gms_internal_zzaqp.nextNull();
                    return zzapa.bou;
                case BEGIN_ARRAY:
                    com_google_android_gms_internal_zzaov = new zzaov();
                    com_google_android_gms_internal_zzaqp.beginArray();
                    while (com_google_android_gms_internal_zzaqp.hasNext()) {
                        com_google_android_gms_internal_zzaov.zzc((zzaoy) zzb(com_google_android_gms_internal_zzaqp));
                    }
                    com_google_android_gms_internal_zzaqp.endArray();
                    return com_google_android_gms_internal_zzaov;
                case BEGIN_OBJECT:
                    com_google_android_gms_internal_zzaov = new zzapb();
                    com_google_android_gms_internal_zzaqp.beginObject();
                    while (com_google_android_gms_internal_zzaqp.hasNext()) {
                        com_google_android_gms_internal_zzaov.zza(com_google_android_gms_internal_zzaqp.nextName(), (zzaoy) zzb(com_google_android_gms_internal_zzaqp));
                    }
                    com_google_android_gms_internal_zzaqp.endObject();
                    return com_google_android_gms_internal_zzaov;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzad(com_google_android_gms_internal_zzaqp);
        }
    };
    public static final zzapl bqZ = zzb(zzaoy.class, bqY);
    public static final zzapk<Class> bqj = new zzapk<Class>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Class cls) throws IOException {
            if (cls == null) {
                com_google_android_gms_internal_zzaqr.bA();
            } else {
                String valueOf = String.valueOf(cls.getName());
                throw new UnsupportedOperationException(new StringBuilder(String.valueOf(valueOf).length() + 76).append("Attempted to serialize java.lang.Class: ").append(valueOf).append(". Forgot to register a type adapter?").toString());
            }
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzo(com_google_android_gms_internal_zzaqp);
        }

        public Class zzo(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
        }
    };
    public static final zzapl bqk = zza(Class.class, bqj);
    public static final zzapk<BitSet> bql = new zzapk<BitSet>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, BitSet bitSet) throws IOException {
            if (bitSet == null) {
                com_google_android_gms_internal_zzaqr.bA();
                return;
            }
            com_google_android_gms_internal_zzaqr.bw();
            for (int i = 0; i < bitSet.length(); i++) {
                com_google_android_gms_internal_zzaqr.zzcs((long) (bitSet.get(i) ? 1 : 0));
            }
            com_google_android_gms_internal_zzaqr.bx();
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzx(com_google_android_gms_internal_zzaqp);
        }

        public BitSet zzx(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            String valueOf;
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            BitSet bitSet = new BitSet();
            com_google_android_gms_internal_zzaqp.beginArray();
            zzaqq bq = com_google_android_gms_internal_zzaqp.bq();
            int i = 0;
            while (bq != zzaqq.END_ARRAY) {
                boolean z;
                switch (bq) {
                    case NUMBER:
                        if (com_google_android_gms_internal_zzaqp.nextInt() == 0) {
                            z = false;
                            break;
                        }
                        z = true;
                        break;
                    case BOOLEAN:
                        z = com_google_android_gms_internal_zzaqp.nextBoolean();
                        break;
                    case STRING:
                        Object nextString = com_google_android_gms_internal_zzaqp.nextString();
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
                            throw new zzaph(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
                        }
                    default:
                        valueOf = String.valueOf(bq);
                        throw new zzaph(new StringBuilder(String.valueOf(valueOf).length() + 27).append("Invalid bitset value type: ").append(valueOf).toString());
                }
                if (z) {
                    bitSet.set(i);
                }
                i++;
                bq = com_google_android_gms_internal_zzaqp.bq();
            }
            com_google_android_gms_internal_zzaqp.endArray();
            return bitSet;
        }
    };
    public static final zzapl bqm = zza(BitSet.class, bql);
    public static final zzapk<Boolean> bqn = new zzapk<Boolean>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Boolean bool) throws IOException {
            if (bool == null) {
                com_google_android_gms_internal_zzaqr.bA();
            } else {
                com_google_android_gms_internal_zzaqr.zzdh(bool.booleanValue());
            }
        }

        public Boolean zzae(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                return com_google_android_gms_internal_zzaqp.bq() == zzaqq.STRING ? Boolean.valueOf(Boolean.parseBoolean(com_google_android_gms_internal_zzaqp.nextString())) : Boolean.valueOf(com_google_android_gms_internal_zzaqp.nextBoolean());
            } else {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzae(com_google_android_gms_internal_zzaqp);
        }
    };
    public static final zzapk<Boolean> bqo = new zzapk<Boolean>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Boolean bool) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(bool == null ? "null" : bool.toString());
        }

        public Boolean zzae(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                return Boolean.valueOf(com_google_android_gms_internal_zzaqp.nextString());
            }
            com_google_android_gms_internal_zzaqp.nextNull();
            return null;
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzae(com_google_android_gms_internal_zzaqp);
        }
    };
    public static final zzapl bqp = zza(Boolean.TYPE, Boolean.class, bqn);
    public static final zzapk<Number> bqq = new zzapk<Number>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Number number) throws IOException {
            com_google_android_gms_internal_zzaqr.zza(number);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzg(com_google_android_gms_internal_zzaqp);
        }

        public Number zzg(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            try {
                return Byte.valueOf((byte) com_google_android_gms_internal_zzaqp.nextInt());
            } catch (Throwable e) {
                throw new zzaph(e);
            }
        }
    };
    public static final zzapl bqr = zza(Byte.TYPE, Byte.class, bqq);
    public static final zzapk<Number> bqs = new zzapk<Number>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Number number) throws IOException {
            com_google_android_gms_internal_zzaqr.zza(number);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzg(com_google_android_gms_internal_zzaqp);
        }

        public Number zzg(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            try {
                return Short.valueOf((short) com_google_android_gms_internal_zzaqp.nextInt());
            } catch (Throwable e) {
                throw new zzaph(e);
            }
        }
    };
    public static final zzapl bqt = zza(Short.TYPE, Short.class, bqs);
    public static final zzapk<Number> bqu = new zzapk<Number>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Number number) throws IOException {
            com_google_android_gms_internal_zzaqr.zza(number);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzg(com_google_android_gms_internal_zzaqp);
        }

        public Number zzg(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            try {
                return Integer.valueOf(com_google_android_gms_internal_zzaqp.nextInt());
            } catch (Throwable e) {
                throw new zzaph(e);
            }
        }
    };
    public static final zzapl bqv = zza(Integer.TYPE, Integer.class, bqu);
    public static final zzapk<Number> bqw = new zzapk<Number>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Number number) throws IOException {
            com_google_android_gms_internal_zzaqr.zza(number);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzg(com_google_android_gms_internal_zzaqp);
        }

        public Number zzg(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() == zzaqq.NULL) {
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
            try {
                return Long.valueOf(com_google_android_gms_internal_zzaqp.nextLong());
            } catch (Throwable e) {
                throw new zzaph(e);
            }
        }
    };
    public static final zzapk<Number> bqx = new zzapk<Number>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Number number) throws IOException {
            com_google_android_gms_internal_zzaqr.zza(number);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzg(com_google_android_gms_internal_zzaqp);
        }

        public Number zzg(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                return Float.valueOf((float) com_google_android_gms_internal_zzaqp.nextDouble());
            }
            com_google_android_gms_internal_zzaqp.nextNull();
            return null;
        }
    };
    public static final zzapk<Number> bqy = new zzapk<Number>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Number number) throws IOException {
            com_google_android_gms_internal_zzaqr.zza(number);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzg(com_google_android_gms_internal_zzaqp);
        }

        public Number zzg(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                return Double.valueOf(com_google_android_gms_internal_zzaqp.nextDouble());
            }
            com_google_android_gms_internal_zzaqp.nextNull();
            return null;
        }
    };
    public static final zzapk<Number> bqz = new zzapk<Number>() {
        public void zza(zzaqr com_google_android_gms_internal_zzaqr, Number number) throws IOException {
            com_google_android_gms_internal_zzaqr.zza(number);
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzg(com_google_android_gms_internal_zzaqp);
        }

        public Number zzg(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            zzaqq bq = com_google_android_gms_internal_zzaqp.bq();
            switch (bq) {
                case NUMBER:
                    return new zzapv(com_google_android_gms_internal_zzaqp.nextString());
                case NULL:
                    com_google_android_gms_internal_zzaqp.nextNull();
                    return null;
                default:
                    String valueOf = String.valueOf(bq);
                    throw new zzaph(new StringBuilder(String.valueOf(valueOf).length() + 23).append("Expecting number, got: ").append(valueOf).toString());
            }
        }
    };
    public static final zzapl bra = new zzapl() {
        public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
            Class bB = com_google_android_gms_internal_zzaqo_T.bB();
            if (!Enum.class.isAssignableFrom(bB) || bB == Enum.class) {
                return null;
            }
            if (!bB.isEnum()) {
                bB = bB.getSuperclass();
            }
            return new zza(bB);
        }
    };

    private static final class zza<T extends Enum<T>> extends zzapk<T> {
        private final Map<String, T> brk = new HashMap();
        private final Map<T, String> brl = new HashMap();

        public zza(Class<T> cls) {
            try {
                for (Enum enumR : (Enum[]) cls.getEnumConstants()) {
                    String name = enumR.name();
                    zzapn com_google_android_gms_internal_zzapn = (zzapn) cls.getField(name).getAnnotation(zzapn.class);
                    if (com_google_android_gms_internal_zzapn != null) {
                        name = com_google_android_gms_internal_zzapn.value();
                        for (Object put : com_google_android_gms_internal_zzapn.bh()) {
                            this.brk.put(put, enumR);
                        }
                    }
                    String str = name;
                    this.brk.put(str, enumR);
                    this.brl.put(enumR, str);
                }
            } catch (NoSuchFieldException e) {
                throw new AssertionError();
            }
        }

        public void zza(zzaqr com_google_android_gms_internal_zzaqr, T t) throws IOException {
            com_google_android_gms_internal_zzaqr.zzut(t == null ? null : (String) this.brl.get(t));
        }

        public T zzaf(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                return (Enum) this.brk.get(com_google_android_gms_internal_zzaqp.nextString());
            }
            com_google_android_gms_internal_zzaqp.nextNull();
            return null;
        }

        public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            return zzaf(com_google_android_gms_internal_zzaqp);
        }
    }

    public static <TT> zzapl zza(final zzaqo<TT> com_google_android_gms_internal_zzaqo_TT, final zzapk<TT> com_google_android_gms_internal_zzapk_TT) {
        return new zzapl() {
            public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
                return com_google_android_gms_internal_zzaqo_T.equals(com_google_android_gms_internal_zzaqo_TT) ? com_google_android_gms_internal_zzapk_TT : null;
            }
        };
    }

    public static <TT> zzapl zza(final Class<TT> cls, final zzapk<TT> com_google_android_gms_internal_zzapk_TT) {
        return new zzapl() {
            public String toString() {
                String valueOf = String.valueOf(cls.getName());
                String valueOf2 = String.valueOf(com_google_android_gms_internal_zzapk_TT);
                return new StringBuilder((String.valueOf(valueOf).length() + 23) + String.valueOf(valueOf2).length()).append("Factory[type=").append(valueOf).append(",adapter=").append(valueOf2).append("]").toString();
            }

            public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
                return com_google_android_gms_internal_zzaqo_T.bB() == cls ? com_google_android_gms_internal_zzapk_TT : null;
            }
        };
    }

    public static <TT> zzapl zza(final Class<TT> cls, final Class<TT> cls2, final zzapk<? super TT> com_google_android_gms_internal_zzapk__super_TT) {
        return new zzapl() {
            public String toString() {
                String valueOf = String.valueOf(cls2.getName());
                String valueOf2 = String.valueOf(cls.getName());
                String valueOf3 = String.valueOf(com_google_android_gms_internal_zzapk__super_TT);
                return new StringBuilder(((String.valueOf(valueOf).length() + 24) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Factory[type=").append(valueOf).append("+").append(valueOf2).append(",adapter=").append(valueOf3).append("]").toString();
            }

            public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
                Class bB = com_google_android_gms_internal_zzaqo_T.bB();
                return (bB == cls || bB == cls2) ? com_google_android_gms_internal_zzapk__super_TT : null;
            }
        };
    }

    public static <TT> zzapl zzb(final Class<TT> cls, final zzapk<TT> com_google_android_gms_internal_zzapk_TT) {
        return new zzapl() {
            public String toString() {
                String valueOf = String.valueOf(cls.getName());
                String valueOf2 = String.valueOf(com_google_android_gms_internal_zzapk_TT);
                return new StringBuilder((String.valueOf(valueOf).length() + 32) + String.valueOf(valueOf2).length()).append("Factory[typeHierarchy=").append(valueOf).append(",adapter=").append(valueOf2).append("]").toString();
            }

            public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
                return cls.isAssignableFrom(com_google_android_gms_internal_zzaqo_T.bB()) ? com_google_android_gms_internal_zzapk_TT : null;
            }
        };
    }

    public static <TT> zzapl zzb(final Class<TT> cls, final Class<? extends TT> cls2, final zzapk<? super TT> com_google_android_gms_internal_zzapk__super_TT) {
        return new zzapl() {
            public String toString() {
                String valueOf = String.valueOf(cls.getName());
                String valueOf2 = String.valueOf(cls2.getName());
                String valueOf3 = String.valueOf(com_google_android_gms_internal_zzapk__super_TT);
                return new StringBuilder(((String.valueOf(valueOf).length() + 24) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Factory[type=").append(valueOf).append("+").append(valueOf2).append(",adapter=").append(valueOf3).append("]").toString();
            }

            public <T> zzapk<T> zza(zzaos com_google_android_gms_internal_zzaos, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
                Class bB = com_google_android_gms_internal_zzaqo_T.bB();
                return (bB == cls || bB == cls2) ? com_google_android_gms_internal_zzapk__super_TT : null;
            }
        };
    }
}
