package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;

public final class zzbum {
    private final ByteBuffer zzcrW;

    public static class zza extends IOException {
        zza(int i, int i2) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space (pos " + i + " limit " + i2 + ").");
        }
    }

    private zzbum(ByteBuffer byteBuffer) {
        this.zzcrW = byteBuffer;
        this.zzcrW.order(ByteOrder.LITTLE_ENDIAN);
    }

    private zzbum(byte[] bArr, int i, int i2) {
        this(ByteBuffer.wrap(bArr, i, i2));
    }

    public static int zzH(int i, int i2) {
        return zzqs(i) + zzqp(i2);
    }

    public static int zzI(int i, int i2) {
        return zzqs(i) + zzqq(i2);
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

    public static zzbum zzae(byte[] bArr) {
        return zzc(bArr, 0, bArr.length);
    }

    public static int zzag(byte[] bArr) {
        return zzqu(bArr.length) + bArr.length;
    }

    public static int zzb(int i, double d) {
        return zzqs(i) + 8;
    }

    public static int zzb(int i, zzbut com_google_android_gms_internal_zzbut) {
        return (zzqs(i) * 2) + zzd(com_google_android_gms_internal_zzbut);
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

    public static int zzba(long j) {
        return zzbe(j);
    }

    public static int zzbb(long j) {
        return zzbe(j);
    }

    public static int zzbc(long j) {
        return zzbe(zzbg(j));
    }

    public static int zzbe(long j) {
        return (-128 & j) == 0 ? 1 : (-16384 & j) == 0 ? 2 : (-2097152 & j) == 0 ? 3 : (-268435456 & j) == 0 ? 4 : (-34359738368L & j) == 0 ? 5 : (-4398046511104L & j) == 0 ? 6 : (-562949953421312L & j) == 0 ? 7 : (-72057594037927936L & j) == 0 ? 8 : (Long.MIN_VALUE & j) == 0 ? 9 : 10;
    }

    public static long zzbg(long j) {
        return (j << 1) ^ (j >> 63);
    }

    public static int zzc(int i, zzbut com_google_android_gms_internal_zzbut) {
        return zzqs(i) + zze(com_google_android_gms_internal_zzbut);
    }

    public static int zzc(int i, byte[] bArr) {
        return zzqs(i) + zzag(bArr);
    }

    public static zzbum zzc(byte[] bArr, int i, int i2) {
        return new zzbum(bArr, i, i2);
    }

    public static int zzd(int i, float f) {
        return zzqs(i) + 4;
    }

    public static int zzd(zzbut com_google_android_gms_internal_zzbut) {
        return com_google_android_gms_internal_zzbut.zzacZ();
    }

    public static int zze(int i, long j) {
        return zzqs(i) + zzba(j);
    }

    public static int zze(zzbut com_google_android_gms_internal_zzbut) {
        int zzacZ = com_google_android_gms_internal_zzbut.zzacZ();
        return zzacZ + zzqu(zzacZ);
    }

    public static int zzf(int i, long j) {
        return zzqs(i) + zzbb(j);
    }

    public static int zzg(int i, long j) {
        return zzqs(i) + 8;
    }

    public static int zzh(int i, long j) {
        return zzqs(i) + zzbc(j);
    }

    public static int zzh(int i, boolean z) {
        return zzqs(i) + 1;
    }

    public static int zzkc(String str) {
        int zzb = zzb((CharSequence) str);
        return zzb + zzqu(zzb);
    }

    public static int zzqp(int i) {
        return i >= 0 ? zzqu(i) : 10;
    }

    public static int zzqq(int i) {
        return zzqu(zzqw(i));
    }

    public static int zzqs(int i) {
        return zzqu(zzbuw.zzK(i, 0));
    }

    public static int zzqu(int i) {
        return (i & -128) == 0 ? 1 : (i & -16384) == 0 ? 2 : (-2097152 & i) == 0 ? 3 : (-268435456 & i) == 0 ? 4 : 5;
    }

    public static int zzqw(int i) {
        return (i << 1) ^ (i >> 31);
    }

    public static int zzr(int i, String str) {
        return zzqs(i) + zzkc(str);
    }

    public void zzF(int i, int i2) throws IOException {
        zzJ(i, 0);
        zzqn(i2);
    }

    public void zzG(int i, int i2) throws IOException {
        zzJ(i, 0);
        zzqo(i2);
    }

    public void zzJ(int i, int i2) throws IOException {
        zzqt(zzbuw.zzK(i, i2));
    }

    public void zza(int i, double d) throws IOException {
        zzJ(i, 1);
        zzn(d);
    }

    public void zza(int i, long j) throws IOException {
        zzJ(i, 0);
        zzaW(j);
    }

    public void zza(int i, zzbut com_google_android_gms_internal_zzbut) throws IOException {
        zzJ(i, 2);
        zzc(com_google_android_gms_internal_zzbut);
    }

    public void zzaW(long j) throws IOException {
        zzbd(j);
    }

    public void zzaX(long j) throws IOException {
        zzbd(j);
    }

    public void zzaY(long j) throws IOException {
        zzbf(j);
    }

    public void zzaZ(long j) throws IOException {
        zzbd(zzbg(j));
    }

    public int zzacL() {
        return this.zzcrW.remaining();
    }

    public void zzacM() {
        if (zzacL() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public void zzaf(byte[] bArr) throws IOException {
        zzqt(bArr.length);
        zzah(bArr);
    }

    public void zzah(byte[] bArr) throws IOException {
        zzd(bArr, 0, bArr.length);
    }

    public void zzb(int i, long j) throws IOException {
        zzJ(i, 0);
        zzaX(j);
    }

    public void zzb(int i, byte[] bArr) throws IOException {
        zzJ(i, 2);
        zzaf(bArr);
    }

    public void zzb(zzbut com_google_android_gms_internal_zzbut) throws IOException {
        com_google_android_gms_internal_zzbut.zza(this);
    }

    public void zzbd(long j) throws IOException {
        while ((-128 & j) != 0) {
            zzqr((((int) j) & 127) | 128);
            j >>>= 7;
        }
        zzqr((int) j);
    }

    public void zzbf(long j) throws IOException {
        if (this.zzcrW.remaining() < 8) {
            throw new zza(this.zzcrW.position(), this.zzcrW.limit());
        }
        this.zzcrW.putLong(j);
    }

    public void zzbl(boolean z) throws IOException {
        zzqr(z ? 1 : 0);
    }

    public void zzc(byte b) throws IOException {
        if (this.zzcrW.hasRemaining()) {
            this.zzcrW.put(b);
            return;
        }
        throw new zza(this.zzcrW.position(), this.zzcrW.limit());
    }

    public void zzc(int i, float f) throws IOException {
        zzJ(i, 5);
        zzk(f);
    }

    public void zzc(int i, long j) throws IOException {
        zzJ(i, 1);
        zzaY(j);
    }

    public void zzc(zzbut com_google_android_gms_internal_zzbut) throws IOException {
        zzqt(com_google_android_gms_internal_zzbut.zzacY());
        com_google_android_gms_internal_zzbut.zza(this);
    }

    public void zzd(int i, long j) throws IOException {
        zzJ(i, 0);
        zzaZ(j);
    }

    public void zzd(byte[] bArr, int i, int i2) throws IOException {
        if (this.zzcrW.remaining() >= i2) {
            this.zzcrW.put(bArr, i, i2);
            return;
        }
        throw new zza(this.zzcrW.position(), this.zzcrW.limit());
    }

    public void zzg(int i, boolean z) throws IOException {
        zzJ(i, 0);
        zzbl(z);
    }

    public void zzk(float f) throws IOException {
        zzqv(Float.floatToIntBits(f));
    }

    public void zzkb(String str) throws IOException {
        try {
            int zzqu = zzqu(str.length());
            if (zzqu == zzqu(str.length() * 3)) {
                int position = this.zzcrW.position();
                if (this.zzcrW.remaining() < zzqu) {
                    throw new zza(zzqu + position, this.zzcrW.limit());
                }
                this.zzcrW.position(position + zzqu);
                zza((CharSequence) str, this.zzcrW);
                int position2 = this.zzcrW.position();
                this.zzcrW.position(position);
                zzqt((position2 - position) - zzqu);
                this.zzcrW.position(position2);
                return;
            }
            zzqt(zzb((CharSequence) str));
            zza((CharSequence) str, this.zzcrW);
        } catch (Throwable e) {
            zza com_google_android_gms_internal_zzbum_zza = new zza(this.zzcrW.position(), this.zzcrW.limit());
            com_google_android_gms_internal_zzbum_zza.initCause(e);
            throw com_google_android_gms_internal_zzbum_zza;
        }
    }

    public void zzn(double d) throws IOException {
        zzbf(Double.doubleToLongBits(d));
    }

    public void zzq(int i, String str) throws IOException {
        zzJ(i, 2);
        zzkb(str);
    }

    public void zzqn(int i) throws IOException {
        if (i >= 0) {
            zzqt(i);
        } else {
            zzbd((long) i);
        }
    }

    public void zzqo(int i) throws IOException {
        zzqt(zzqw(i));
    }

    public void zzqr(int i) throws IOException {
        zzc((byte) i);
    }

    public void zzqt(int i) throws IOException {
        while ((i & -128) != 0) {
            zzqr((i & 127) | 128);
            i >>>= 7;
        }
        zzqr(i);
    }

    public void zzqv(int i) throws IOException {
        if (this.zzcrW.remaining() < 4) {
            throw new zza(this.zzcrW.position(), this.zzcrW.limit());
        }
        this.zzcrW.putInt(i);
    }
}
