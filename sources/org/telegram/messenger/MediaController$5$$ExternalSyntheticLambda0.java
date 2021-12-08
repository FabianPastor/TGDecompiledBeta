package org.telegram.messenger;

public final /* synthetic */ class MediaController$5$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ float f$1;

    public /* synthetic */ MediaController$5$$ExternalSyntheticLambda0(String str, float f) {
        this.f$0 = str;
        this.f$1 = f;
    }

    public final void run() {
        ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit().putFloat(this.f$0, this.f$1).commit();
    }
}
