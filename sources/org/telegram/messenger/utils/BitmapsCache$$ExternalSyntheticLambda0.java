package org.telegram.messenger.utils;

import android.graphics.Bitmap;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.utils.BitmapsCache;

public final /* synthetic */ class BitmapsCache$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ BitmapsCache f$0;
    public final /* synthetic */ Bitmap[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ BitmapsCache.ByteArrayOutputStream[] f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ RandomAccessFile f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ CountDownLatch[] f$7;

    public /* synthetic */ BitmapsCache$$ExternalSyntheticLambda0(BitmapsCache bitmapsCache, Bitmap[] bitmapArr, int i, BitmapsCache.ByteArrayOutputStream[] byteArrayOutputStreamArr, int i2, RandomAccessFile randomAccessFile, ArrayList arrayList, CountDownLatch[] countDownLatchArr) {
        this.f$0 = bitmapsCache;
        this.f$1 = bitmapArr;
        this.f$2 = i;
        this.f$3 = byteArrayOutputStreamArr;
        this.f$4 = i2;
        this.f$5 = randomAccessFile;
        this.f$6 = arrayList;
        this.f$7 = countDownLatchArr;
    }

    public final void run() {
        this.f$0.lambda$createCache$0(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
