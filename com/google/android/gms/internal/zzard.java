package com.google.android.gms.internal;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import org.telegram.messenger.exoplayer.extractor.ts.PsExtractor;

public final class zzard {
    private final ByteBuffer bqu;

    public static class zza extends IOException {
        zza(int i, int i2) {
            super("CodedOutputStream was writing to a flat byte array and ran out of space (pos " + i + " limit " + i2 + ").");
        }
    }

    private zzard(ByteBuffer byteBuffer) {
        this.bqu = byteBuffer;
        this.bqu.order(ByteOrder.LITTLE_ENDIAN);
    }

    private zzard(byte[] bArr, int i, int i2) {
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

    public static int zzag(int i, int i2) {
        return zzahl(i) + zzahi(i2);
    }

    public static int zzah(int i, int i2) {
        return zzahl(i) + zzahj(i2);
    }

    public static int zzahi(int i) {
        return i >= 0 ? zzahn(i) : 10;
    }

    public static int zzahj(int i) {
        return zzahn(zzahp(i));
    }

    public static int zzahl(int i) {
        return zzahn(zzarn.zzaj(i, 0));
    }

    public static int zzahn(int i) {
        return (i & -128) == 0 ? 1 : (i & -16384) == 0 ? 2 : (-2097152 & i) == 0 ? 3 : (-268435456 & i) == 0 ? 4 : 5;
    }

    public static int zzahp(int i) {
        return (i << 1) ^ (i >> 31);
    }

    public static int zzb(int i, double d) {
        return zzahl(i) + zzp(d);
    }

    public static int zzb(int i, zzark com_google_android_gms_internal_zzark) {
        return (zzahl(i) * 2) + zzd(com_google_android_gms_internal_zzark);
    }

    public static int zzb(int i, byte[] bArr) {
        return zzahl(i) + zzbg(bArr);
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

    public static zzard zzbe(byte[] bArr) {
        return zzc(bArr, 0, bArr.length);
    }

    public static int zzbg(byte[] bArr) {
        return zzahn(bArr.length) + bArr.length;
    }

    public static int zzc(int i, zzark com_google_android_gms_internal_zzark) {
        return zzahl(i) + zze(com_google_android_gms_internal_zzark);
    }

    public static zzard zzc(byte[] bArr, int i, int i2) {
        return new zzard(bArr, i, i2);
    }

    public static int zzd(int i, float f) {
        return zzahl(i) + zzl(f);
    }

    public static int zzd(zzark com_google_android_gms_internal_zzark) {
        return com_google_android_gms_internal_zzark.db();
    }

    private static int zzd(CharSequence charSequence) {
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

    public static int zzda(long j) {
        return zzdf(j);
    }

    public static int zzdb(long j) {
        return zzdf(j);
    }

    public static int zzdc(long j) {
        return 8;
    }

    public static int zzdd(long j) {
        return zzdf(zzdh(j));
    }

    public static int zzdf(long j) {
        return (-128 & j) == 0 ? 1 : (-16384 & j) == 0 ? 2 : (-2097152 & j) == 0 ? 3 : (-268435456 & j) == 0 ? 4 : (-34359738368L & j) == 0 ? 5 : (-4398046511104L & j) == 0 ? 6 : (-562949953421312L & j) == 0 ? 7 : (-72057594037927936L & j) == 0 ? 8 : (Long.MIN_VALUE & j) == 0 ? 9 : 10;
    }

    public static long zzdh(long j) {
        return (j << 1) ^ (j >> 63);
    }

    public static int zzdl(boolean z) {
        return 1;
    }

    public static int zze(int i, long j) {
        return zzahl(i) + zzda(j);
    }

    public static int zze(zzark com_google_android_gms_internal_zzark) {
        int db = com_google_android_gms_internal_zzark.db();
        return db + zzahn(db);
    }

    public static int zzf(int i, long j) {
        return zzahl(i) + zzdb(j);
    }

    public static int zzg(int i, long j) {
        return zzahl(i) + zzdc(j);
    }

    public static int zzh(int i, long j) {
        return zzahl(i) + zzdd(j);
    }

    public static int zzk(int i, boolean z) {
        return zzahl(i) + zzdl(z);
    }

    public static int zzl(float f) {
        return 4;
    }

    public static int zzp(double d) {
        return 8;
    }

    public static int zzs(int i, String str) {
        return zzahl(i) + zzuy(str);
    }

    public static int zzuy(String str) {
        int zzd = zzd((CharSequence) str);
        return zzd + zzahn(zzd);
    }

    public int cN() {
        return this.bqu.remaining();
    }

    public void cO() {
        if (cN() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }

    public void zza(int i, double d) throws IOException {
        zzai(i, 1);
        zzo(d);
    }

    public void zza(int i, long j) throws IOException {
        zzai(i, 0);
        zzcw(j);
    }

    public void zza(int i, zzark com_google_android_gms_internal_zzark) throws IOException {
        zzai(i, 2);
        zzc(com_google_android_gms_internal_zzark);
    }

    public void zza(int i, byte[] bArr) throws IOException {
        zzai(i, 2);
        zzbf(bArr);
    }

    public void zzae(int i, int i2) throws IOException {
        zzai(i, 0);
        zzahg(i2);
    }

    public void zzaf(int i, int i2) throws IOException {
        zzai(i, 0);
        zzahh(i2);
    }

    public void zzahg(int i) throws IOException {
        if (i >= 0) {
            zzahm(i);
        } else {
            zzde((long) i);
        }
    }

    public void zzahh(int i) throws IOException {
        zzahm(zzahp(i));
    }

    public void zzahk(int i) throws IOException {
        zzc((byte) i);
    }

    public void zzahm(int i) throws IOException {
        while ((i & -128) != 0) {
            zzahk((i & 127) | 128);
            i >>>= 7;
        }
        zzahk(i);
    }

    public void zzaho(int i) throws IOException {
        if (this.bqu.remaining() < 4) {
            throw new zza(this.bqu.position(), this.bqu.limit());
        }
        this.bqu.putInt(i);
    }

    public void zzai(int i, int i2) throws IOException {
        zzahm(zzarn.zzaj(i, i2));
    }

    public void zzb(int i, long j) throws IOException {
        zzai(i, 0);
        zzcx(j);
    }

    public void zzb(zzark com_google_android_gms_internal_zzark) throws IOException {
        com_google_android_gms_internal_zzark.zza(this);
    }

    public void zzbf(byte[] bArr) throws IOException {
        zzahm(bArr.length);
        zzbh(bArr);
    }

    public void zzbh(byte[] bArr) throws IOException {
        zzd(bArr, 0, bArr.length);
    }

    public void zzc(byte b) throws IOException {
        if (this.bqu.hasRemaining()) {
            this.bqu.put(b);
            return;
        }
        throw new zza(this.bqu.position(), this.bqu.limit());
    }

    public void zzc(int i, float f) throws IOException {
        zzai(i, 5);
        zzk(f);
    }

    public void zzc(int i, long j) throws IOException {
        zzai(i, 1);
        zzcy(j);
    }

    public void zzc(zzark com_google_android_gms_internal_zzark) throws IOException {
        zzahm(com_google_android_gms_internal_zzark.da());
        com_google_android_gms_internal_zzark.zza(this);
    }

    public void zzcw(long j) throws IOException {
        zzde(j);
    }

    public void zzcx(long j) throws IOException {
        zzde(j);
    }

    public void zzcy(long j) throws IOException {
        zzdg(j);
    }

    public void zzcz(long j) throws IOException {
        zzde(zzdh(j));
    }

    public void zzd(int i, long j) throws IOException {
        zzai(i, 0);
        zzcz(j);
    }

    public void zzd(byte[] bArr, int i, int i2) throws IOException {
        if (this.bqu.remaining() >= i2) {
            this.bqu.put(bArr, i, i2);
            return;
        }
        throw new zza(this.bqu.position(), this.bqu.limit());
    }

    public void zzde(long j) throws IOException {
        while ((-128 & j) != 0) {
            zzahk((((int) j) & 127) | 128);
            j >>>= 7;
        }
        zzahk((int) j);
    }

    public void zzdg(long j) throws IOException {
        if (this.bqu.remaining() < 8) {
            throw new zza(this.bqu.position(), this.bqu.limit());
        }
        this.bqu.putLong(j);
    }

    public void zzdk(boolean z) throws IOException {
        zzahk(z ? 1 : 0);
    }

    public void zzj(int i, boolean z) throws IOException {
        zzai(i, 0);
        zzdk(z);
    }

    public void zzk(float f) throws IOException {
        zzaho(Float.floatToIntBits(f));
    }

    public void zzo(double d) throws IOException {
        zzdg(Double.doubleToLongBits(d));
    }

    public void zzr(int i, String str) throws IOException {
        zzai(i, 2);
        zzux(str);
    }

    public void zzux(String str) throws IOException {
        try {
            int zzahn = zzahn(str.length());
            if (zzahn == zzahn(str.length() * 3)) {
                int position = this.bqu.position();
                if (this.bqu.remaining() < zzahn) {
                    throw new zza(zzahn + position, this.bqu.limit());
                }
                this.bqu.position(position + zzahn);
                zza((CharSequence) str, this.bqu);
                int position2 = this.bqu.position();
                this.bqu.position(position);
                zzahm((position2 - position) - zzahn);
                this.bqu.position(position2);
                return;
            }
            zzahm(zzd((CharSequence) str));
            zza((CharSequence) str, this.bqu);
        } catch (Throwable e) {
            zza com_google_android_gms_internal_zzard_zza = new zza(this.bqu.position(), this.bqu.limit());
            com_google_android_gms_internal_zzard_zza.initCause(e);
            throw com_google_android_gms_internal_zzard_zza;
        }
    }
}
