package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$pP0eV3Hy639MJNxG0c3lV4tp0MM implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$PassportActivity$pP0eV3Hy639MJNxG0c3lV4tp0MM INSTANCE = new -$$Lambda$PassportActivity$pP0eV3Hy639MJNxG0c3lV4tp0MM();

    private /* synthetic */ -$$Lambda$PassportActivity$pP0eV3Hy639MJNxG0c3lV4tp0MM() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PassportActivity$fK7dwz8bOfjtlF5bGs64fAkaNiU(tLObject));
    }
}
