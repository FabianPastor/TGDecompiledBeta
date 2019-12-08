package org.telegram.ui.Wallet;

import javax.crypto.Cipher;
import org.telegram.ui.Components.BiometricPromtHelper.CipherCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WalletSettingsActivity$qvlGHcgNevar_au0i2cBQC4A1fmE implements CipherCallback {
    private final /* synthetic */ WalletSettingsActivity f$0;

    public /* synthetic */ -$$Lambda$WalletSettingsActivity$qvlGHcgNevar_au0i2cBQC4A1fmE(WalletSettingsActivity walletSettingsActivity) {
        this.f$0 = walletSettingsActivity;
    }

    public final void run(Cipher cipher) {
        this.f$0.doExport(cipher);
    }
}
