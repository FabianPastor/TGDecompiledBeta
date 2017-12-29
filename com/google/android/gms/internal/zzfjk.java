package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;

public final class zzfjk {
    private final ByteBuffer buffer;

    private zzfjk(ByteBuffer byteBuffer) {
        this.buffer = byteBuffer;
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    private zzfjk(byte[] bArr, int i, int i2) {
        this(ByteBuffer.wrap(bArr, i, i2));
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

    public static int zzad(int i, int i2) {
        return zzlg(i) + zzlh(i2);
    }

    public static int zzb(int i, zzfjs com_google_android_gms_internal_zzfjs) {
        int zzlg = zzlg(i);
        int zzho = com_google_android_gms_internal_zzfjs.zzho();
        return zzlg + (zzho + zzlp(zzho));
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

    public static zzfjk zzbf(byte[] bArr) {
        return zzo(bArr, 0, bArr.length);
    }

    public static int zzc(int i, long j) {
        return zzlg(i) + zzdi(j);
    }

    private static int zzd(CharSequence charSequence) {
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
                            if (Character.codePointAt(charSequence, i2) < C.DEFAULT_BUFFER_SEGMENT_SIZE) {
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

    private final void zzdh(long j) throws IOException {
        while ((-128 & j) != 0) {
            zzmh((((int) j) & 127) | 128);
            j >>>= 7;
        }
        zzmh((int) j);
    }

    public static int zzdi(long j) {
        return (-128 & j) == 0 ? 1 : (-16384 & j) == 0 ? 2 : (-2097152 & j) == 0 ? 3 : (-268435456 & j) == 0 ? 4 : (-34359738368L & j) == 0 ? 5 : (-4398046511104L & j) == 0 ? 6 : (-562949953421312L & j) == 0 ? 7 : (-72057594037927936L & j) == 0 ? 8 : (Long.MIN_VALUE & j) == 0 ? 9 : 10;
    }

    private final void zzdj(long j) throws IOException {
        if (this.buffer.remaining() < 8) {
            throw new zzfjl(this.buffer.position(), this.buffer.limit());
        }
        this.buffer.putLong(j);
    }

    public static int zzlg(int i) {
        return zzlp(i << 3);
    }

    public static int zzlh(int i) {
        return i >= 0 ? zzlp(i) : 10;
    }

    public static int zzlp(int i) {
        return (i & -128) == 0 ? 1 : (i & -16384) == 0 ? 2 : (-2097152 & i) == 0 ? 3 : (-268435456 & i) == 0 ? 4 : 5;
    }

    private final void zzmh(int i) throws IOException {
        byte b = (byte) i;
        if (this.buffer.hasRemaining()) {
            this.buffer.put(b);
            return;
        }
        throw new zzfjl(this.buffer.position(), this.buffer.limit());
    }

    public static int zzo(int i, String str) {
        return zzlg(i) + zztt(str);
    }

    public static zzfjk zzo(byte[] bArr, int i, int i2) {
        return new zzfjk(bArr, 0, i2);
    }

    public static int zztt(String str) {
        int zzd = zzd(str);
        return zzd + zzlp(zzd);
    }

    public final void zza(int i, double d) throws IOException {
        zzz(i, 1);
        zzdj(Double.doubleToLongBits(d));
    }

    public final void zza(int i, long j) throws IOException {
        zzz(i, 0);
        zzdh(j);
    }

    public final void zza(int i, zzfjs com_google_android_gms_internal_zzfjs) throws IOException {
        zzz(i, 2);
        zzb(com_google_android_gms_internal_zzfjs);
    }

    public final void zzaa(int i, int i2) throws IOException {
        zzz(i, 0);
        if (i2 >= 0) {
            zzmi(i2);
        } else {
            zzdh((long) i2);
        }
    }

    public final void zzb(zzfjs com_google_android_gms_internal_zzfjs) throws IOException {
        zzmi(com_google_android_gms_internal_zzfjs.zzdam());
        com_google_android_gms_internal_zzfjs.zza(this);
    }

    public final void zzbh(byte[] bArr) throws IOException {
        int length = bArr.length;
        if (this.buffer.remaining() >= length) {
            this.buffer.put(bArr, 0, length);
            return;
        }
        throw new zzfjl(this.buffer.position(), this.buffer.limit());
    }

    public final void zzc(int i, float f) throws IOException {
        zzz(i, 5);
        int floatToIntBits = Float.floatToIntBits(f);
        if (this.buffer.remaining() < 4) {
            throw new zzfjl(this.buffer.position(), this.buffer.limit());
        }
        this.buffer.putInt(floatToIntBits);
    }

    public final void zzcwt() {
        if (this.buffer.remaining() != 0) {
            throw new IllegalStateException(String.format("Did not write as much data as expected, %s bytes remaining.", new Object[]{Integer.valueOf(this.buffer.remaining())}));
        }
    }

    public final void zzf(int i, long j) throws IOException {
        zzz(i, 0);
        zzdh(j);
    }

    public final void zzl(int i, boolean z) throws IOException {
        int i2 = 0;
        zzz(i, 0);
        if (z) {
            i2 = 1;
        }
        byte b = (byte) i2;
        if (this.buffer.hasRemaining()) {
            this.buffer.put(b);
            return;
        }
        throw new zzfjl(this.buffer.position(), this.buffer.limit());
    }

    public final void zzmi(int i) throws IOException {
        while ((i & -128) != 0) {
            zzmh((i & 127) | 128);
            i >>>= 7;
        }
        zzmh(i);
    }

    public final void zzn(int i, String str) throws IOException {
        zzz(i, 2);
        try {
            int zzlp = zzlp(str.length());
            if (zzlp == zzlp(str.length() * 3)) {
                int position = this.buffer.position();
                if (this.buffer.remaining() < zzlp) {
                    throw new zzfjl(zzlp + position, this.buffer.limit());
                }
                this.buffer.position(position + zzlp);
                zza((CharSequence) str, this.buffer);
                int position2 = this.buffer.position();
                this.buffer.position(position);
                zzmi((position2 - position) - zzlp);
                this.buffer.position(position2);
                return;
            }
            zzmi(zzd(str));
            zza((CharSequence) str, this.buffer);
        } catch (Throwable e) {
            zzfjl com_google_android_gms_internal_zzfjl = new zzfjl(this.buffer.position(), this.buffer.limit());
            com_google_android_gms_internal_zzfjl.initCause(e);
            throw com_google_android_gms_internal_zzfjl;
        }
    }

    public final void zzz(int i, int i2) throws IOException {
        zzmi((i << 3) | i2);
    }
}
