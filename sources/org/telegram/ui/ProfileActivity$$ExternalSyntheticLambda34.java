package org.telegram.ui;

import org.telegram.messenger.LanguageDetector;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda34 implements LanguageDetector.StringCallback {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ String[] f$1;
    public final /* synthetic */ boolean[] f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ Runnable f$5;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda34(ProfileActivity profileActivity, String[] strArr, boolean[] zArr, String str, boolean z, Runnable runnable) {
        this.f$0 = profileActivity;
        this.f$1 = strArr;
        this.f$2 = zArr;
        this.f$3 = str;
        this.f$4 = z;
        this.f$5 = runnable;
    }

    public final void run(String str) {
        this.f$0.lambda$processOnClickOrPress$25(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, str);
    }
}
