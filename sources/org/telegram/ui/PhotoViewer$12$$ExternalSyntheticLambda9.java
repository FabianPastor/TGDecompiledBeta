package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$12$$ExternalSyntheticLambda9 implements DialogsActivity.DialogsActivityDelegate {
    public final /* synthetic */ PhotoViewer.AnonymousClass12 f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ChatActivity f$2;

    public /* synthetic */ PhotoViewer$12$$ExternalSyntheticLambda9(PhotoViewer.AnonymousClass12 r1, ArrayList arrayList, ChatActivity chatActivity) {
        this.f$0 = r1;
        this.f$1 = arrayList;
        this.f$2 = chatActivity;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.m4305lambda$onItemClick$4$orgtelegramuiPhotoViewer$12(this.f$1, this.f$2, dialogsActivity, arrayList, charSequence, z);
    }
}
