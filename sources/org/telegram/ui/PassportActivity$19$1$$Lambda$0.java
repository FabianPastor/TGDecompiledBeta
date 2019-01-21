package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.messenger.SecureDocument;
import org.telegram.tgnet.TLRPC.TL_account_saveSecureValue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;
import org.telegram.tgnet.TLRPC.TL_secureValue;
import org.telegram.ui.PassportActivity.19.AnonymousClass1;

final /* synthetic */ class PassportActivity$19$1$$Lambda$0 implements Runnable {
    private final AnonymousClass1 arg$1;
    private final TL_secureValue arg$10;
    private final ArrayList arg$11;
    private final SecureDocument arg$12;
    private final SecureDocument arg$13;
    private final SecureDocument arg$14;
    private final ArrayList arg$15;
    private final String arg$16;
    private final String arg$17;
    private final int arg$18;
    private final Runnable arg$19;
    private final TL_error arg$2;
    private final ErrorRunnable arg$3;
    private final String arg$4;
    private final TL_account_saveSecureValue arg$5;
    private final boolean arg$6;
    private final TL_secureRequiredType arg$7;
    private final TL_secureRequiredType arg$8;
    private final TL_secureValue arg$9;

    PassportActivity$19$1$$Lambda$0(AnonymousClass1 anonymousClass1, TL_error tL_error, ErrorRunnable errorRunnable, String str, TL_account_saveSecureValue tL_account_saveSecureValue, boolean z, TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, TL_secureValue tL_secureValue, TL_secureValue tL_secureValue2, ArrayList arrayList, SecureDocument secureDocument, SecureDocument secureDocument2, SecureDocument secureDocument3, ArrayList arrayList2, String str2, String str3, int i, Runnable runnable) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = tL_error;
        this.arg$3 = errorRunnable;
        this.arg$4 = str;
        this.arg$5 = tL_account_saveSecureValue;
        this.arg$6 = z;
        this.arg$7 = tL_secureRequiredType;
        this.arg$8 = tL_secureRequiredType2;
        this.arg$9 = tL_secureValue;
        this.arg$10 = tL_secureValue2;
        this.arg$11 = arrayList;
        this.arg$12 = secureDocument;
        this.arg$13 = secureDocument2;
        this.arg$14 = secureDocument3;
        this.arg$15 = arrayList2;
        this.arg$16 = str2;
        this.arg$17 = str3;
        this.arg$18 = i;
        this.arg$19 = runnable;
    }

    public void run() {
        this.arg$1.lambda$onResult$0$PassportActivity$19$1(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, this.arg$13, this.arg$14, this.arg$15, this.arg$16, this.arg$17, this.arg$18, this.arg$19);
    }
}
