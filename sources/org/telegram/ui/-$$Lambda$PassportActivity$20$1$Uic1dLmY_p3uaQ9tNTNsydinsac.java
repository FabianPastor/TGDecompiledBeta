package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.messenger.SecureDocument;
import org.telegram.tgnet.TLRPC.TL_account_saveSecureValue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;
import org.telegram.tgnet.TLRPC.TL_secureValue;
import org.telegram.ui.PassportActivity.20.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$20$1$Uic1dLmY_p3uaQ9tNTNsydinsac implements Runnable {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ ArrayList f$10;
    private final /* synthetic */ SecureDocument f$11;
    private final /* synthetic */ SecureDocument f$12;
    private final /* synthetic */ SecureDocument f$13;
    private final /* synthetic */ ArrayList f$14;
    private final /* synthetic */ String f$15;
    private final /* synthetic */ String f$16;
    private final /* synthetic */ int f$17;
    private final /* synthetic */ Runnable f$18;
    private final /* synthetic */ ErrorRunnable f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ TL_account_saveSecureValue f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ TL_secureRequiredType f$6;
    private final /* synthetic */ TL_secureRequiredType f$7;
    private final /* synthetic */ TL_secureValue f$8;
    private final /* synthetic */ TL_secureValue f$9;

    public /* synthetic */ -$$Lambda$PassportActivity$20$1$Uic1dLmY_p3uaQ9tNTNsydinsac(AnonymousClass1 anonymousClass1, TL_error tL_error, ErrorRunnable errorRunnable, String str, TL_account_saveSecureValue tL_account_saveSecureValue, boolean z, TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, TL_secureValue tL_secureValue, TL_secureValue tL_secureValue2, ArrayList arrayList, SecureDocument secureDocument, SecureDocument secureDocument2, SecureDocument secureDocument3, ArrayList arrayList2, String str2, String str3, int i, Runnable runnable) {
        this.f$0 = anonymousClass1;
        this.f$1 = tL_error;
        this.f$2 = errorRunnable;
        this.f$3 = str;
        this.f$4 = tL_account_saveSecureValue;
        this.f$5 = z;
        this.f$6 = tL_secureRequiredType;
        this.f$7 = tL_secureRequiredType2;
        this.f$8 = tL_secureValue;
        this.f$9 = tL_secureValue2;
        this.f$10 = arrayList;
        this.f$11 = secureDocument;
        this.f$12 = secureDocument2;
        this.f$13 = secureDocument3;
        this.f$14 = arrayList2;
        this.f$15 = str2;
        this.f$16 = str3;
        this.f$17 = i;
        this.f$18 = runnable;
    }

    public final void run() {
        AnonymousClass1 anonymousClass1 = this.f$0;
        AnonymousClass1 anonymousClass12 = anonymousClass1;
        anonymousClass12.lambda$onResult$0$PassportActivity$20$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, this.f$18);
    }
}
