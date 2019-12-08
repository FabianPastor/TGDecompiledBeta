package org.telegram.ui.Wallet;

import javax.crypto.Cipher;
import org.telegram.ui.Components.BiometricPromtHelper.CipherCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletCreateActivity$hxGciPi6qDkdBcXTJwtmPE9-4x0 implements CipherCallback {
    private final /* synthetic */ WalletCreateActivity f$0;

    public /* synthetic */ -$$Lambda$WalletCreateActivity$hxGciPi6qDkdBcXTJwtmPE9-4x0(WalletCreateActivity walletCreateActivity) {
        this.f$0 = walletCreateActivity;
    }

    public final void run(Cipher cipher) {
        this.f$0.doExport(cipher);
    }
}
