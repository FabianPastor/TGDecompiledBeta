package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;

public final class adh {
    private final ByteBuffer zzcsn;

    private adh(ByteBuffer byteBuffer) {
        this.zzcsn = byteBuffer;
        this.zzcsn.order(ByteOrder.LITTLE_ENDIAN);
    }

    private adh(byte[] bArr, int i, int i2) {
        this(ByteBuffer.wrap(bArr, i, i2));
    }

    public static adh zzI(byte[] bArr) {
        return zzc(bArr, 0, bArr.length);
    }

    public static int zzJ(byte[] bArr) {
        return zzcv(bArr.length) + bArr.length;
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

    private final void zzaO(long j) throws IOException {
        while ((-128 & j) != 0) {
            zzcs((((int) j) & 127) | 128);
            j >>>= 7;
        }
        zzcs((int) j);
    }

    public static int zzaP(long j) {
        return (-128 & j) == 0 ? 1 : (-16384 & j) == 0 ? 2 : (-2097152 & j) == 0 ? 3 : (-268435456 & j) == 0 ? 4 : (-34359738368L & j) == 0 ? 5 : (-4398046511104L & j) == 0 ? 6 : (-562949953421312L & j) == 0 ? 7 : (-72057594037927936L & j) == 0 ? 8 : (Long.MIN_VALUE & j) == 0 ? 9 : 10;
    }

    private final void zzaQ(long j) throws IOException {
        if (this.zzcsn.remaining() < 8) {
            throw new adi(this.zzcsn.position(), this.zzcsn.limit());
        }
        this.zzcsn.putLong(j);
    }

    private static long zzaR(long j) {
        return (j << 1) ^ (j >> 63);
    }

    public static int zzb(int i, adp com_google_android_gms_internal_adp) {
        int zzct = zzct(i);
        int zzLV = com_google_android_gms_internal_adp.zzLV();
        return zzct + (zzLV + zzcv(zzLV));
    }

    private static int zzb(CharSequence charSequence) {
        int i = 0;
        int length = charSequence.length();
        int i2 = 0;
        while (i2 < length && charSequence.charAt(i2) < '') {
            i2++;
        }
        int i3 = length;
        while (i2 < length) {
            char charAt = charSequence.charAt(i2);
            if (charAt < 'ࠀ') {
                i3 += (127 - charAt) >>> 31;
                i2++;
            } else {
                int length2 = charSequence.length();
                while (i2 < length2) {
                    char charAt2 = charSequence.charAt(i2);
                    if (charAt2 < 'ࠀ') {
                        i += (127 - charAt2) >>> 31;
                    } else {
                        i += 2;
                        if ('?' <= charAt2 && charAt2 <= '?') {
                            if (Character.codePointAt(charSequence, i2) < 65536) {
                                throw new IllegalArgumentException("Unpaired surrogate at index " + i2);
                            }
                            i2++;
                        }
                    }
                    i2++;
                }
                i2 = i3 + i;
                if (i2 < length) {
                    return i2;
                }
                throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (((long) i2) + 4294967296L));
            }
        }
        i2 = i3;
        if (i2 < length) {
            return i2;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (((long) i2) + 4294967296L));
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

    public static int zzc(int i, byte[] bArr) {
        return zzct(i) + zzJ(bArr);
    }

    public static adh zzc(byte[] bArr, int i, int i2) {
        return new adh(bArr, 0, i2);
    }

    public static int zzcr(int i) {
        return i >= 0 ? zzcv(i) : 10;
    }

    private final void zzcs(int i) throws IOException {
        byte b = (byte) i;
        if (this.zzcsn.hasRemaining()) {
            this.zzcsn.put(b);
            return;
        }
        throw new adi(this.zzcsn.position(), this.zzcsn.limit());
    }

    public static int zzct(int i) {
        return zzcv(i << 3);
    }

    public static int zzcv(int i) {
        return (i & -128) == 0 ? 1 : (i & -16384) == 0 ? 2 : (-2097152 & i) == 0 ? 3 : (-268435456 & i) == 0 ? 4 : 5;
    }

    public static int zzcw(int i) {
        return (i << 1) ^ (i >> 31);
    }

    public static int zze(int i, long j) {
        return zzct(i) + zzaP(j);
    }

    public static int zzf(int i, long j) {
        return zzct(i) + zzaP(zzaR(j));
    }

    public static int zzhQ(String str) {
        int zzb = zzb((CharSequence) str);
        return zzb + zzcv(zzb);
    }

    public static int zzm(int i, String str) {
        return zzct(i) + zzhQ(str);
    }

    public static int zzs(int i, int i2) {
        return zzct(i) + zzcr(i2);
    }

    public final void zzK(byte[] bArr) throws IOException {
        int length = bArr.length;
        if (this.zzcsn.remaining() >= length) {
            this.zzcsn.put(bArr, 0, length);
            return;
        }
        throw new adi(this.zzcsn.position(), this.zzcsn.limit());
    }

    public final void zzLM() {
        if (this.zzcsn.remaining() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public final void zza(int i, double d) throws IOException {
        zzt(i, 1);
        zzaQ(Double.doubleToLongBits(d));
    }

    public final void zza(int i, long j) throws IOException {
        zzt(i, 0);
        zzaO(j);
    }

    public final void zza(int i, adp com_google_android_gms_internal_adp) throws IOException {
        zzt(i, 2);
        zzb(com_google_android_gms_internal_adp);
    }

    public final void zzb(int i, long j) throws IOException {
        zzt(i, 0);
        zzaO(j);
    }

    public final void zzb(int i, byte[] bArr) throws IOException {
        zzt(i, 2);
        zzcu(bArr.length);
        zzK(bArr);
    }

    public final void zzb(adp com_google_android_gms_internal_adp) throws IOException {
        zzcu(com_google_android_gms_internal_adp.zzLU());
        com_google_android_gms_internal_adp.zza(this);
    }

    public final void zzc(int i, float f) throws IOException {
        zzt(i, 5);
        int floatToIntBits = Float.floatToIntBits(f);
        if (this.zzcsn.remaining() < 4) {
            throw new adi(this.zzcsn.position(), this.zzcsn.limit());
        }
        this.zzcsn.putInt(floatToIntBits);
    }

    public final void zzc(int i, long j) throws IOException {
        zzt(i, 1);
        zzaQ(j);
    }

    public final void zzcu(int i) throws IOException {
        while ((i & -128) != 0) {
            zzcs((i & 127) | 128);
            i >>>= 7;
        }
        zzcs(i);
    }

    public final void zzd(int i, long j) throws IOException {
        zzt(i, 0);
        zzaO(zzaR(j));
    }

    public final void zzk(int i, boolean z) throws IOException {
        int i2 = 0;
        zzt(i, 0);
        if (z) {
            i2 = 1;
        }
        byte b = (byte) i2;
        if (this.zzcsn.hasRemaining()) {
            this.zzcsn.put(b);
            return;
        }
        throw new adi(this.zzcsn.position(), this.zzcsn.limit());
    }

    public final void zzl(int i, String str) throws IOException {
        zzt(i, 2);
        try {
            int zzcv = zzcv(str.length());
            if (zzcv == zzcv(str.length() * 3)) {
                int position = this.zzcsn.position();
                if (this.zzcsn.remaining() < zzcv) {
                    throw new adi(zzcv + position, this.zzcsn.limit());
                }
                this.zzcsn.position(position + zzcv);
                zza((CharSequence) str, this.zzcsn);
                int position2 = this.zzcsn.position();
                this.zzcsn.position(position);
                zzcu((position2 - position) - zzcv);
                this.zzcsn.position(position2);
                return;
            }
            zzcu(zzb((CharSequence) str));
            zza((CharSequence) str, this.zzcsn);
        } catch (Throwable e) {
            adi com_google_android_gms_internal_adi = new adi(this.zzcsn.position(), this.zzcsn.limit());
            com_google_android_gms_internal_adi.initCause(e);
            throw com_google_android_gms_internal_adi;
        }
    }

    public final void zzr(int i, int i2) throws IOException {
        zzt(i, 0);
        if (i2 >= 0) {
            zzcu(i2);
        } else {
            zzaO((long) i2);
        }
    }

    public final void zzt(int i, int i2) throws IOException {
        zzcu((i << 3) | i2);
    }
}
