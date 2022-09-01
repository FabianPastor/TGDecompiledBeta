package org.telegram.messenger.utils;

import android.graphics.Bitmap;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.utils.BitmapsCache;

public final /* synthetic */ class BitmapsCache$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ BitmapsCache f$0;
    public final /* synthetic */ AtomicBoolean f$1;
    public final /* synthetic */ Bitmap[] f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ BitmapsCache.ByteArrayOutputStream[] f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ RandomAccessFile f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ CountDownLatch[] f$8;

    public /* synthetic */ BitmapsCache$$ExternalSyntheticLambda0(BitmapsCache bitmapsCache, AtomicBoolean atomicBoolean, Bitmap[] bitmapArr, int i, BitmapsCache.ByteArrayOutputStream[] byteArrayOutputStreamArr, int i2, RandomAccessFile randomAccessFile, ArrayList arrayList, CountDownLatch[] countDownLatchArr) {
        this.f$0 = bitmapsCache;
        this.f$1 = atomicBoolean;
        this.f$2 = bitmapArr;
        this.f$3 = i;
        this.f$4 = byteArrayOutputStreamArr;
        this.f$5 = i2;
        this.f$6 = randomAccessFile;
        this.f$7 = arrayList;
        this.f$8 = countDownLatchArr;
    }

    public final void run() {
        this.f$0.lambda$createCache$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
