package org.telegram.messenger;

import android.graphics.drawable.BitmapDrawable;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$ThumbGenerateTask$93CLASSNAME-AxUp0yfQKw5ZLfHAidaSg implements Runnable {
    private final /* synthetic */ ThumbGenerateTask f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ BitmapDrawable f$3;

    public /* synthetic */ -$$Lambda$ImageLoader$ThumbGenerateTask$93CLASSNAME-AxUp0yfQKw5ZLfHAidaSg(ThumbGenerateTask thumbGenerateTask, String str, ArrayList arrayList, BitmapDrawable bitmapDrawable) {
        this.f$0 = thumbGenerateTask;
        this.f$1 = str;
        this.f$2 = arrayList;
        this.f$3 = bitmapDrawable;
    }

    public final void run() {
        this.f$0.lambda$run$1$ImageLoader$ThumbGenerateTask(this.f$1, this.f$2, this.f$3);
    }
}
