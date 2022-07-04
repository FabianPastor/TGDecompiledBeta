package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.SecureDocument;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda26 implements View.OnLongClickListener {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ SecureDocument f$2;
    public final /* synthetic */ PassportActivity.SecureDocumentCell f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda26(PassportActivity passportActivity, int i, SecureDocument secureDocument, PassportActivity.SecureDocumentCell secureDocumentCell, String str) {
        this.f$0 = passportActivity;
        this.f$1 = i;
        this.f$2 = secureDocument;
        this.f$3 = secureDocumentCell;
        this.f$4 = str;
    }

    public final boolean onLongClick(View view) {
        return this.f$0.m4025lambda$addDocumentView$57$orgtelegramuiPassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, view);
    }
}
