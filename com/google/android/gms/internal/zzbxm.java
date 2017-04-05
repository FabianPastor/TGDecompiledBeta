package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;

public final class zzbxm {
    private final ByteBuffer zzcuH;

    public static class zza extends IOException {
        zza(int i, int i2) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space (pos " + i + " limit " + i2 + ").");
        }
    }

    private zzbxm(ByteBuffer byteBuffer) {
        this.zzcuH = byteBuffer;
        this.zzcuH.order(ByteOrder.LITTLE_ENDIAN);
    }

    private zzbxm(byte[] bArr, int i, int i2) {
        this(ByteBuffer.wrap(bArr, i, i2));
    }

    public static int zzL(int i, int i2) {
        return zzrj(i) + zzrg(i2);
    }

    public static int zzM(int i, int i2) {
        return zzrj(i) + zzrh(i2);
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

    public static zzbxm zzag(byte[] bArr) {
        return zzc(bArr, 0, bArr.length);
    }

    public static int zzai(byte[] bArr) {
        return zzrl(bArr.length) + bArr.length;
    }

    public static int zzb(int i, double d) {
        return zzrj(i) + 8;
    }

    public static int zzb(int i, zzbxt com_google_android_gms_internal_zzbxt) {
        return (zzrj(i) * 2) + zzd(com_google_android_gms_internal_zzbxt);
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

    public static int zzbe(long j) {
        return zzbi(j);
    }

    public static int zzbf(long j) {
        return zzbi(j);
    }

    public static int zzbg(long j) {
        return zzbi(zzbk(j));
    }

    public static int zzbi(long j) {
        return (-128 & j) == 0 ? 1 : (-16384 & j) == 0 ? 2 : (-2097152 & j) == 0 ? 3 : (-268435456 & j) == 0 ? 4 : (-34359738368L & j) == 0 ? 5 : (-4398046511104L & j) == 0 ? 6 : (-562949953421312L & j) == 0 ? 7 : (-72057594037927936L & j) == 0 ? 8 : (Long.MIN_VALUE & j) == 0 ? 9 : 10;
    }

    public static long zzbk(long j) {
        return (j << 1) ^ (j >> 63);
    }

    public static int zzc(int i, zzbxt com_google_android_gms_internal_zzbxt) {
        return zzrj(i) + zze(com_google_android_gms_internal_zzbxt);
    }

    public static int zzc(int i, byte[] bArr) {
        return zzrj(i) + zzai(bArr);
    }

    public static zzbxm zzc(byte[] bArr, int i, int i2) {
        return new zzbxm(bArr, i, i2);
    }

    public static int zzd(int i, float f) {
        return zzrj(i) + 4;
    }

    public static int zzd(zzbxt com_google_android_gms_internal_zzbxt) {
        return com_google_android_gms_internal_zzbxt.zzaeT();
    }

    public static int zze(int i, long j) {
        return zzrj(i) + zzbe(j);
    }

    public static int zze(zzbxt com_google_android_gms_internal_zzbxt) {
        int zzaeT = com_google_android_gms_internal_zzbxt.zzaeT();
        return zzaeT + zzrl(zzaeT);
    }

    public static int zzf(int i, long j) {
        return zzrj(i) + zzbf(j);
    }

    public static int zzg(int i, long j) {
        return zzrj(i) + 8;
    }

    public static int zzh(int i, long j) {
        return zzrj(i) + zzbg(j);
    }

    public static int zzh(int i, boolean z) {
        return zzrj(i) + 1;
    }

    public static int zzkb(String str) {
        int zzb = zzb((CharSequence) str);
        return zzb + zzrl(zzb);
    }

    public static int zzr(int i, String str) {
        return zzrj(i) + zzkb(str);
    }

    public static int zzrg(int i) {
        return i >= 0 ? zzrl(i) : 10;
    }

    public static int zzrh(int i) {
        return zzrl(zzrn(i));
    }

    public static int zzrj(int i) {
        return zzrl(zzbxw.zzO(i, 0));
    }

    public static int zzrl(int i) {
        return (i & -128) == 0 ? 1 : (i & -16384) == 0 ? 2 : (-2097152 & i) == 0 ? 3 : (-268435456 & i) == 0 ? 4 : 5;
    }

    public static int zzrn(int i) {
        return (i << 1) ^ (i >> 31);
    }

    public void zzJ(int i, int i2) throws IOException {
        zzN(i, 0);
        zzre(i2);
    }

    public void zzK(int i, int i2) throws IOException {
        zzN(i, 0);
        zzrf(i2);
    }

    public void zzN(int i, int i2) throws IOException {
        zzrk(zzbxw.zzO(i, i2));
    }

    public void zza(int i, double d) throws IOException {
        zzN(i, 1);
        zzn(d);
    }

    public void zza(int i, long j) throws IOException {
        zzN(i, 0);
        zzba(j);
    }

    public void zza(int i, zzbxt com_google_android_gms_internal_zzbxt) throws IOException {
        zzN(i, 2);
        zzc(com_google_android_gms_internal_zzbxt);
    }

    public int zzaeF() {
        return this.zzcuH.remaining();
    }

    public void zzaeG() {
        if (zzaeF() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public void zzah(byte[] bArr) throws IOException {
        zzrk(bArr.length);
        zzaj(bArr);
    }

    public void zzaj(byte[] bArr) throws IOException {
        zzd(bArr, 0, bArr.length);
    }

    public void zzb(int i, long j) throws IOException {
        zzN(i, 0);
        zzbb(j);
    }

    public void zzb(int i, byte[] bArr) throws IOException {
        zzN(i, 2);
        zzah(bArr);
    }

    public void zzb(zzbxt com_google_android_gms_internal_zzbxt) throws IOException {
        com_google_android_gms_internal_zzbxt.zza(this);
    }

    public void zzba(long j) throws IOException {
        zzbh(j);
    }

    public void zzbb(long j) throws IOException {
        zzbh(j);
    }

    public void zzbc(long j) throws IOException {
        zzbj(j);
    }

    public void zzbd(long j) throws IOException {
        zzbh(zzbk(j));
    }

    public void zzbh(long j) throws IOException {
        while ((-128 & j) != 0) {
            zzri((((int) j) & 127) | 128);
            j >>>= 7;
        }
        zzri((int) j);
    }

    public void zzbj(long j) throws IOException {
        if (this.zzcuH.remaining() < 8) {
            throw new zza(this.zzcuH.position(), this.zzcuH.limit());
        }
        this.zzcuH.putLong(j);
    }

    public void zzbq(boolean z) throws IOException {
        zzri(z ? 1 : 0);
    }

    public void zzc(byte b) throws IOException {
        if (this.zzcuH.hasRemaining()) {
            this.zzcuH.put(b);
            return;
        }
        throw new zza(this.zzcuH.position(), this.zzcuH.limit());
    }

    public void zzc(int i, float f) throws IOException {
        zzN(i, 5);
        zzk(f);
    }

    public void zzc(int i, long j) throws IOException {
        zzN(i, 1);
        zzbc(j);
    }

    public void zzc(zzbxt com_google_android_gms_internal_zzbxt) throws IOException {
        zzrk(com_google_android_gms_internal_zzbxt.zzaeS());
        com_google_android_gms_internal_zzbxt.zza(this);
    }

    public void zzd(int i, long j) throws IOException {
        zzN(i, 0);
        zzbd(j);
    }

    public void zzd(byte[] bArr, int i, int i2) throws IOException {
        if (this.zzcuH.remaining() >= i2) {
            this.zzcuH.put(bArr, i, i2);
            return;
        }
        throw new zza(this.zzcuH.position(), this.zzcuH.limit());
    }

    public void zzg(int i, boolean z) throws IOException {
        zzN(i, 0);
        zzbq(z);
    }

    public void zzk(float f) throws IOException {
        zzrm(Float.floatToIntBits(f));
    }

    public void zzka(String str) throws IOException {
        try {
            int zzrl = zzrl(str.length());
            if (zzrl == zzrl(str.length() * 3)) {
                int position = this.zzcuH.position();
                if (this.zzcuH.remaining() < zzrl) {
                    throw new zza(zzrl + position, this.zzcuH.limit());
                }
                this.zzcuH.position(position + zzrl);
                zza((CharSequence) str, this.zzcuH);
                int position2 = this.zzcuH.position();
                this.zzcuH.position(position);
                zzrk((position2 - position) - zzrl);
                this.zzcuH.position(position2);
                return;
            }
            zzrk(zzb((CharSequence) str));
            zza((CharSequence) str, this.zzcuH);
        } catch (Throwable e) {
            zza com_google_android_gms_internal_zzbxm_zza = new zza(this.zzcuH.position(), this.zzcuH.limit());
            com_google_android_gms_internal_zzbxm_zza.initCause(e);
            throw com_google_android_gms_internal_zzbxm_zza;
        }
    }

    public void zzn(double d) throws IOException {
        zzbj(Double.doubleToLongBits(d));
    }

    public void zzq(int i, String str) throws IOException {
        zzN(i, 2);
        zzka(str);
    }

    public void zzre(int i) throws IOException {
        if (i >= 0) {
            zzrk(i);
        } else {
            zzbh((long) i);
        }
    }

    public void zzrf(int i) throws IOException {
        zzrk(zzrn(i));
    }

    public void zzri(int i) throws IOException {
        zzc((byte) i);
    }

    public void zzrk(int i) throws IOException {
        while ((i & -128) != 0) {
            zzri((i & 127) | 128);
            i >>>= 7;
        }
        zzri(i);
    }

    public void zzrm(int i) throws IOException {
        if (this.zzcuH.remaining() < 4) {
            throw new zza(this.zzcuH.position(), this.zzcuH.limit());
        }
        this.zzcuH.putInt(i);
    }
}
