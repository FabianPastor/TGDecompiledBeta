package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import org.telegram.messenger.exoplayer.extractor.ts.PsExtractor;

public final class zzart {
    private final ByteBuffer btF;

    public static class zza extends IOException {
        zza(int i, int i2) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space (pos " + i + " limit " + i2 + ").");
        }
    }

    private zzart(ByteBuffer byteBuffer) {
        this.btF = byteBuffer;
        this.btF.order(ByteOrder.LITTLE_ENDIAN);
    }

    private zzart(byte[] bArr, int i, int i2) {
        this(ByteBuffer.wrap(bArr, i, i2));
    }

    private static int zza(CharSequence charSequence, int i) {
        int length = charSequence.length();
        int i2 = 0;
        int i3 = i;
        while (i3 < length) {
            char charAt = charSequence.charAt(i3);
            if (charAt < 'ࠀ') {
                i2 += (127 - charAt) >>> 31;
            } else {
                i2 += 2;
                if ('?' <= charAt && charAt <= '?') {
                    if (Character.codePointAt(charSequence, i3) < 65536) {
                        throw new IllegalArgumentException("Unpaired surrogate at index " + i3);
                    }
                    i3++;
                }
            }
            i3++;
        }
        return i2;
    }

    private static int zza(CharSequence charSequence, byte[] bArr, int i, int i2) {
        int length = charSequence.length();
        int i3 = 0;
        int i4 = i + i2;
        while (i3 < length && i3 + i < i4) {
            char charAt = charSequence.charAt(i3);
            if (charAt >= '') {
                break;
            }
            bArr[i + i3] = (byte) charAt;
            i3++;
        }
        if (i3 == length) {
            return i + length;
        }
        int i5 = i + i3;
        while (i3 < length) {
            int i6;
            char charAt2 = charSequence.charAt(i3);
            if (charAt2 < '' && i5 < i4) {
                i6 = i5 + 1;
                bArr[i5] = (byte) charAt2;
            } else if (charAt2 < 'ࠀ' && i5 <= i4 - 2) {
                r6 = i5 + 1;
                bArr[i5] = (byte) ((charAt2 >>> 6) | 960);
                i6 = r6 + 1;
                bArr[r6] = (byte) ((charAt2 & 63) | 128);
            } else if ((charAt2 < '?' || '?' < charAt2) && i5 <= i4 - 3) {
                i6 = i5 + 1;
                bArr[i5] = (byte) ((charAt2 >>> 12) | 480);
                i5 = i6 + 1;
                bArr[i6] = (byte) (((charAt2 >>> 6) & 63) | 128);
                i6 = i5 + 1;
                bArr[i5] = (byte) ((charAt2 & 63) | 128);
            } else if (i5 <= i4 - 4) {
                if (i3 + 1 != charSequence.length()) {
                    i3++;
                    charAt = charSequence.charAt(i3);
                    if (Character.isSurrogatePair(charAt2, charAt)) {
                        int toCodePoint = Character.toCodePoint(charAt2, charAt);
                        i6 = i5 + 1;
                        bArr[i5] = (byte) ((toCodePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK);
                        i5 = i6 + 1;
                        bArr[i6] = (byte) (((toCodePoint >>> 12) & 63) | 128);
                        r6 = i5 + 1;
                        bArr[i5] = (byte) (((toCodePoint >>> 6) & 63) | 128);
                        i6 = r6 + 1;
                        bArr[r6] = (byte) ((toCodePoint & 63) | 128);
                    }
                }
                throw new IllegalArgumentException("Unpaired surrogate at index " + (i3 - 1));
            } else {
                throw new ArrayIndexOutOfBoundsException("Failed writing " + charAt2 + " at index " + i5);
            }
            i3++;
            i5 = i6;
        }
        return i5;
    }

    private static void zza(CharSequence charSequence, ByteBuffer byteBuffer) {
        if (byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        } else if (byteBuffer.hasArray()) {
            try {
                byteBuffer.position(zza(charSequence, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining()) - byteBuffer.arrayOffset());
            } catch (Throwable e) {
                BufferOverflowException bufferOverflowException = new BufferOverflowException();
                bufferOverflowException.initCause(e);
                throw bufferOverflowException;
            }
        } else {
            zzb(charSequence, byteBuffer);
        }
    }

    public static int zzagz(int i) {
        return i >= 0 ? zzahe(i) : 10;
    }

    public static int zzah(int i, int i2) {
        return zzahc(i) + zzagz(i2);
    }

    public static int zzaha(int i) {
        return zzahe(zzahg(i));
    }

    public static int zzahc(int i) {
        return zzahe(zzasd.zzak(i, 0));
    }

    public static int zzahe(int i) {
        return (i & -128) == 0 ? 1 : (i & -16384) == 0 ? 2 : (-2097152 & i) == 0 ? 3 : (-268435456 & i) == 0 ? 4 : 5;
    }

    public static int zzahg(int i) {
        return (i << 1) ^ (i >> 31);
    }

    public static int zzai(int i, int i2) {
        return zzahc(i) + zzaha(i2);
    }

    public static int zzb(int i, double d) {
        return zzahc(i) + 8;
    }

    public static int zzb(int i, zzasa com_google_android_gms_internal_zzasa) {
        return (zzahc(i) * 2) + zzd(com_google_android_gms_internal_zzasa);
    }

    private static int zzb(CharSequence charSequence) {
        int length = charSequence.length();
        int i = 0;
        while (i < length && charSequence.charAt(i) < '') {
            i++;
        }
        int i2 = i;
        i = length;
        while (i2 < length) {
            char charAt = charSequence.charAt(i2);
            if (charAt >= 'ࠀ') {
                i += zza(charSequence, i2);
                break;
            }
            i2++;
            i = ((127 - charAt) >>> 31) + i;
        }
        if (i >= length) {
            return i;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (((long) i) + 4294967296L));
    }

    private static void zzb(CharSequence charSequence, ByteBuffer byteBuffer) {
        int length = charSequence.length();
        int i = 0;
        while (i < length) {
            char charAt = charSequence.charAt(i);
            if (charAt < '') {
                byteBuffer.put((byte) charAt);
            } else if (charAt < 'ࠀ') {
                byteBuffer.put((byte) ((charAt >>> 6) | 960));
                byteBuffer.put((byte) ((charAt & 63) | 128));
            } else if (charAt < '?' || '?' < charAt) {
                byteBuffer.put((byte) ((charAt >>> 12) | 480));
                byteBuffer.put((byte) (((charAt >>> 6) & 63) | 128));
                byteBuffer.put((byte) ((charAt & 63) | 128));
            } else {
                if (i + 1 != charSequence.length()) {
                    i++;
                    char charAt2 = charSequence.charAt(i);
                    if (Character.isSurrogatePair(charAt, charAt2)) {
                        int toCodePoint = Character.toCodePoint(charAt, charAt2);
                        byteBuffer.put((byte) ((toCodePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK));
                        byteBuffer.put((byte) (((toCodePoint >>> 12) & 63) | 128));
                        byteBuffer.put((byte) (((toCodePoint >>> 6) & 63) | 128));
                        byteBuffer.put((byte) ((toCodePoint & 63) | 128));
                    }
                }
                throw new IllegalArgumentException("Unpaired surrogate at index " + (i - 1));
            }
            i++;
        }
    }

    public static zzart zzbe(byte[] bArr) {
        return zzc(bArr, 0, bArr.length);
    }

    public static int zzbg(byte[] bArr) {
        return zzahe(bArr.length) + bArr.length;
    }

    public static int zzc(int i, zzasa com_google_android_gms_internal_zzasa) {
        return zzahc(i) + zze(com_google_android_gms_internal_zzasa);
    }

    public static int zzc(int i, byte[] bArr) {
        return zzahc(i) + zzbg(bArr);
    }

    public static zzart zzc(byte[] bArr, int i, int i2) {
        return new zzart(bArr, i, i2);
    }

    public static int zzcy(long j) {
        return zzdc(j);
    }

    public static int zzcz(long j) {
        return zzdc(j);
    }

    public static int zzd(int i, float f) {
        return zzahc(i) + 4;
    }

    public static int zzd(zzasa com_google_android_gms_internal_zzasa) {
        return com_google_android_gms_internal_zzasa.cz();
    }

    public static int zzda(long j) {
        return zzdc(zzde(j));
    }

    public static int zzdc(long j) {
        return (-128 & j) == 0 ? 1 : (-16384 & j) == 0 ? 2 : (-2097152 & j) == 0 ? 3 : (-268435456 & j) == 0 ? 4 : (-34359738368L & j) == 0 ? 5 : (-4398046511104L & j) == 0 ? 6 : (-562949953421312L & j) == 0 ? 7 : (-72057594037927936L & j) == 0 ? 8 : (Long.MIN_VALUE & j) == 0 ? 9 : 10;
    }

    public static long zzde(long j) {
        return (j << 1) ^ (j >> 63);
    }

    public static int zze(int i, long j) {
        return zzahc(i) + zzcy(j);
    }

    public static int zze(zzasa com_google_android_gms_internal_zzasa) {
        int cz = com_google_android_gms_internal_zzasa.cz();
        return cz + zzahe(cz);
    }

    public static int zzf(int i, long j) {
        return zzahc(i) + zzcz(j);
    }

    public static int zzg(int i, long j) {
        return zzahc(i) + 8;
    }

    public static int zzh(int i, long j) {
        return zzahc(i) + zzda(j);
    }

    public static int zzh(int i, boolean z) {
        return zzahc(i) + 1;
    }

    public static int zzr(int i, String str) {
        return zzahc(i) + zzuy(str);
    }

    public static int zzuy(String str) {
        int zzb = zzb((CharSequence) str);
        return zzb + zzahe(zzb);
    }

    public int cl() {
        return this.btF.remaining();
    }

    public void cm() {
        if (cl() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public void zza(int i, double d) throws IOException {
        zzaj(i, 1);
        zzn(d);
    }

    public void zza(int i, long j) throws IOException {
        zzaj(i, 0);
        zzcu(j);
    }

    public void zza(int i, zzasa com_google_android_gms_internal_zzasa) throws IOException {
        zzaj(i, 2);
        zzc(com_google_android_gms_internal_zzasa);
    }

    public void zzaf(int i, int i2) throws IOException {
        zzaj(i, 0);
        zzagx(i2);
    }

    public void zzag(int i, int i2) throws IOException {
        zzaj(i, 0);
        zzagy(i2);
    }

    public void zzagx(int i) throws IOException {
        if (i >= 0) {
            zzahd(i);
        } else {
            zzdb((long) i);
        }
    }

    public void zzagy(int i) throws IOException {
        zzahd(zzahg(i));
    }

    public void zzahb(int i) throws IOException {
        zzc((byte) i);
    }

    public void zzahd(int i) throws IOException {
        while ((i & -128) != 0) {
            zzahb((i & 127) | 128);
            i >>>= 7;
        }
        zzahb(i);
    }

    public void zzahf(int i) throws IOException {
        if (this.btF.remaining() < 4) {
            throw new zza(this.btF.position(), this.btF.limit());
        }
        this.btF.putInt(i);
    }

    public void zzaj(int i, int i2) throws IOException {
        zzahd(zzasd.zzak(i, i2));
    }

    public void zzb(int i, long j) throws IOException {
        zzaj(i, 0);
        zzcv(j);
    }

    public void zzb(int i, byte[] bArr) throws IOException {
        zzaj(i, 2);
        zzbf(bArr);
    }

    public void zzb(zzasa com_google_android_gms_internal_zzasa) throws IOException {
        com_google_android_gms_internal_zzasa.zza(this);
    }

    public void zzbf(byte[] bArr) throws IOException {
        zzahd(bArr.length);
        zzbh(bArr);
    }

    public void zzbh(byte[] bArr) throws IOException {
        zzd(bArr, 0, bArr.length);
    }

    public void zzc(byte b) throws IOException {
        if (this.btF.hasRemaining()) {
            this.btF.put(b);
            return;
        }
        throw new zza(this.btF.position(), this.btF.limit());
    }

    public void zzc(int i, float f) throws IOException {
        zzaj(i, 5);
        zzk(f);
    }

    public void zzc(int i, long j) throws IOException {
        zzaj(i, 1);
        zzcw(j);
    }

    public void zzc(zzasa com_google_android_gms_internal_zzasa) throws IOException {
        zzahd(com_google_android_gms_internal_zzasa.cy());
        com_google_android_gms_internal_zzasa.zza(this);
    }

    public void zzcu(long j) throws IOException {
        zzdb(j);
    }

    public void zzcv(long j) throws IOException {
        zzdb(j);
    }

    public void zzcw(long j) throws IOException {
        zzdd(j);
    }

    public void zzcx(long j) throws IOException {
        zzdb(zzde(j));
    }

    public void zzd(int i, long j) throws IOException {
        zzaj(i, 0);
        zzcx(j);
    }

    public void zzd(byte[] bArr, int i, int i2) throws IOException {
        if (this.btF.remaining() >= i2) {
            this.btF.put(bArr, i, i2);
            return;
        }
        throw new zza(this.btF.position(), this.btF.limit());
    }

    public void zzdb(long j) throws IOException {
        while ((-128 & j) != 0) {
            zzahb((((int) j) & 127) | 128);
            j >>>= 7;
        }
        zzahb((int) j);
    }

    public void zzdd(long j) throws IOException {
        if (this.btF.remaining() < 8) {
            throw new zza(this.btF.position(), this.btF.limit());
        }
        this.btF.putLong(j);
    }

    public void zzdm(boolean z) throws IOException {
        zzahb(z ? 1 : 0);
    }

    public void zzg(int i, boolean z) throws IOException {
        zzaj(i, 0);
        zzdm(z);
    }

    public void zzk(float f) throws IOException {
        zzahf(Float.floatToIntBits(f));
    }

    public void zzn(double d) throws IOException {
        zzdd(Double.doubleToLongBits(d));
    }

    public void zzq(int i, String str) throws IOException {
        zzaj(i, 2);
        zzux(str);
    }

    public void zzux(String str) throws IOException {
        try {
            int zzahe = zzahe(str.length());
            if (zzahe == zzahe(str.length() * 3)) {
                int position = this.btF.position();
                if (this.btF.remaining() < zzahe) {
                    throw new zza(zzahe + position, this.btF.limit());
                }
                this.btF.position(position + zzahe);
                zza((CharSequence) str, this.btF);
                int position2 = this.btF.position();
                this.btF.position(position);
                zzahd((position2 - position) - zzahe);
                this.btF.position(position2);
                return;
            }
            zzahd(zzb((CharSequence) str));
            zza((CharSequence) str, this.btF);
        } catch (Throwable e) {
            zza com_google_android_gms_internal_zzart_zza = new zza(this.btF.position(), this.btF.limit());
            com_google_android_gms_internal_zzart_zza.initCause(e);
            throw com_google_android_gms_internal_zzart_zza;
        }
    }
}
