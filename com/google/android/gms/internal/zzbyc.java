package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;

public final class zzbyc {
    private final ByteBuffer zzcwB;

    public static class zza extends IOException {
        zza(int i, int i2) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space (pos " + i + " limit " + i2 + ").");
        }
    }

    private zzbyc(ByteBuffer byteBuffer) {
        this.zzcwB = byteBuffer;
        this.zzcwB.order(ByteOrder.LITTLE_ENDIAN);
    }

    private zzbyc(byte[] bArr, int i, int i2) {
        this(ByteBuffer.wrap(bArr, i, i2));
    }

    public static int zzL(int i, int i2) {
        return zzro(i) + zzrl(i2);
    }

    public static int zzM(int i, int i2) {
        return zzro(i) + zzrm(i2);
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

    public static zzbyc zzah(byte[] bArr) {
        return zzc(bArr, 0, bArr.length);
    }

    public static int zzaj(byte[] bArr) {
        return zzrq(bArr.length) + bArr.length;
    }

    public static int zzb(int i, double d) {
        return zzro(i) + 8;
    }

    public static int zzb(int i, zzbyj com_google_android_gms_internal_zzbyj) {
        return (zzro(i) * 2) + zzd(com_google_android_gms_internal_zzbyj);
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

    public static int zzbp(long j) {
        return zzbt(j);
    }

    public static int zzbq(long j) {
        return zzbt(j);
    }

    public static int zzbr(long j) {
        return zzbt(zzbv(j));
    }

    public static int zzbt(long j) {
        return (-128 & j) == 0 ? 1 : (-16384 & j) == 0 ? 2 : (-2097152 & j) == 0 ? 3 : (-268435456 & j) == 0 ? 4 : (-34359738368L & j) == 0 ? 5 : (-4398046511104L & j) == 0 ? 6 : (-562949953421312L & j) == 0 ? 7 : (-72057594037927936L & j) == 0 ? 8 : (Long.MIN_VALUE & j) == 0 ? 9 : 10;
    }

    public static long zzbv(long j) {
        return (j << 1) ^ (j >> 63);
    }

    public static int zzc(int i, zzbyj com_google_android_gms_internal_zzbyj) {
        return zzro(i) + zze(com_google_android_gms_internal_zzbyj);
    }

    public static int zzc(int i, byte[] bArr) {
        return zzro(i) + zzaj(bArr);
    }

    public static zzbyc zzc(byte[] bArr, int i, int i2) {
        return new zzbyc(bArr, i, i2);
    }

    public static int zzd(int i, float f) {
        return zzro(i) + 4;
    }

    public static int zzd(zzbyj com_google_android_gms_internal_zzbyj) {
        return com_google_android_gms_internal_zzbyj.zzafB();
    }

    public static int zze(int i, long j) {
        return zzro(i) + zzbp(j);
    }

    public static int zze(zzbyj com_google_android_gms_internal_zzbyj) {
        int zzafB = com_google_android_gms_internal_zzbyj.zzafB();
        return zzafB + zzrq(zzafB);
    }

    public static int zzf(int i, long j) {
        return zzro(i) + zzbq(j);
    }

    public static int zzg(int i, long j) {
        return zzro(i) + 8;
    }

    public static int zzh(int i, long j) {
        return zzro(i) + zzbr(j);
    }

    public static int zzh(int i, boolean z) {
        return zzro(i) + 1;
    }

    public static int zzku(String str) {
        int zzb = zzb((CharSequence) str);
        return zzb + zzrq(zzb);
    }

    public static int zzr(int i, String str) {
        return zzro(i) + zzku(str);
    }

    public static int zzrl(int i) {
        return i >= 0 ? zzrq(i) : 10;
    }

    public static int zzrm(int i) {
        return zzrq(zzrs(i));
    }

    public static int zzro(int i) {
        return zzrq(zzbym.zzO(i, 0));
    }

    public static int zzrq(int i) {
        return (i & -128) == 0 ? 1 : (i & -16384) == 0 ? 2 : (-2097152 & i) == 0 ? 3 : (-268435456 & i) == 0 ? 4 : 5;
    }

    public static int zzrs(int i) {
        return (i << 1) ^ (i >> 31);
    }

    public void zzJ(int i, int i2) throws IOException {
        zzN(i, 0);
        zzrj(i2);
    }

    public void zzK(int i, int i2) throws IOException {
        zzN(i, 0);
        zzrk(i2);
    }

    public void zzN(int i, int i2) throws IOException {
        zzrp(zzbym.zzO(i, i2));
    }

    public void zza(int i, double d) throws IOException {
        zzN(i, 1);
        zzn(d);
    }

    public void zza(int i, long j) throws IOException {
        zzN(i, 0);
        zzbl(j);
    }

    public void zza(int i, zzbyj com_google_android_gms_internal_zzbyj) throws IOException {
        zzN(i, 2);
        zzc(com_google_android_gms_internal_zzbyj);
    }

    public int zzafn() {
        return this.zzcwB.remaining();
    }

    public void zzafo() {
        if (zzafn() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public void zzai(byte[] bArr) throws IOException {
        zzrp(bArr.length);
        zzak(bArr);
    }

    public void zzak(byte[] bArr) throws IOException {
        zzd(bArr, 0, bArr.length);
    }

    public void zzb(int i, long j) throws IOException {
        zzN(i, 0);
        zzbm(j);
    }

    public void zzb(int i, byte[] bArr) throws IOException {
        zzN(i, 2);
        zzai(bArr);
    }

    public void zzb(zzbyj com_google_android_gms_internal_zzbyj) throws IOException {
        com_google_android_gms_internal_zzbyj.zza(this);
    }

    public void zzbl(long j) throws IOException {
        zzbs(j);
    }

    public void zzbm(long j) throws IOException {
        zzbs(j);
    }

    public void zzbn(long j) throws IOException {
        zzbu(j);
    }

    public void zzbo(long j) throws IOException {
        zzbs(zzbv(j));
    }

    public void zzbr(boolean z) throws IOException {
        zzrn(z ? 1 : 0);
    }

    public void zzbs(long j) throws IOException {
        while ((-128 & j) != 0) {
            zzrn((((int) j) & 127) | 128);
            j >>>= 7;
        }
        zzrn((int) j);
    }

    public void zzbu(long j) throws IOException {
        if (this.zzcwB.remaining() < 8) {
            throw new zza(this.zzcwB.position(), this.zzcwB.limit());
        }
        this.zzcwB.putLong(j);
    }

    public void zzc(byte b) throws IOException {
        if (this.zzcwB.hasRemaining()) {
            this.zzcwB.put(b);
            return;
        }
        throw new zza(this.zzcwB.position(), this.zzcwB.limit());
    }

    public void zzc(int i, float f) throws IOException {
        zzN(i, 5);
        zzk(f);
    }

    public void zzc(int i, long j) throws IOException {
        zzN(i, 1);
        zzbn(j);
    }

    public void zzc(zzbyj com_google_android_gms_internal_zzbyj) throws IOException {
        zzrp(com_google_android_gms_internal_zzbyj.zzafA());
        com_google_android_gms_internal_zzbyj.zza(this);
    }

    public void zzd(int i, long j) throws IOException {
        zzN(i, 0);
        zzbo(j);
    }

    public void zzd(byte[] bArr, int i, int i2) throws IOException {
        if (this.zzcwB.remaining() >= i2) {
            this.zzcwB.put(bArr, i, i2);
            return;
        }
        throw new zza(this.zzcwB.position(), this.zzcwB.limit());
    }

    public void zzg(int i, boolean z) throws IOException {
        zzN(i, 0);
        zzbr(z);
    }

    public void zzk(float f) throws IOException {
        zzrr(Float.floatToIntBits(f));
    }

    public void zzkt(String str) throws IOException {
        try {
            int zzrq = zzrq(str.length());
            if (zzrq == zzrq(str.length() * 3)) {
                int position = this.zzcwB.position();
                if (this.zzcwB.remaining() < zzrq) {
                    throw new zza(zzrq + position, this.zzcwB.limit());
                }
                this.zzcwB.position(position + zzrq);
                zza((CharSequence) str, this.zzcwB);
                int position2 = this.zzcwB.position();
                this.zzcwB.position(position);
                zzrp((position2 - position) - zzrq);
                this.zzcwB.position(position2);
                return;
            }
            zzrp(zzb((CharSequence) str));
            zza((CharSequence) str, this.zzcwB);
        } catch (Throwable e) {
            zza com_google_android_gms_internal_zzbyc_zza = new zza(this.zzcwB.position(), this.zzcwB.limit());
            com_google_android_gms_internal_zzbyc_zza.initCause(e);
            throw com_google_android_gms_internal_zzbyc_zza;
        }
    }

    public void zzn(double d) throws IOException {
        zzbu(Double.doubleToLongBits(d));
    }

    public void zzq(int i, String str) throws IOException {
        zzN(i, 2);
        zzkt(str);
    }

    public void zzrj(int i) throws IOException {
        if (i >= 0) {
            zzrp(i);
        } else {
            zzbs((long) i);
        }
    }

    public void zzrk(int i) throws IOException {
        zzrp(zzrs(i));
    }

    public void zzrn(int i) throws IOException {
        zzc((byte) i);
    }

    public void zzrp(int i) throws IOException {
        while ((i & -128) != 0) {
            zzrn((i & 127) | 128);
            i >>>= 7;
        }
        zzrn(i);
    }

    public void zzrr(int i) throws IOException {
        if (this.zzcwB.remaining() < 4) {
            throw new zza(this.zzcwB.position(), this.zzcwB.limit());
        }
        this.zzcwB.putInt(i);
    }
}
