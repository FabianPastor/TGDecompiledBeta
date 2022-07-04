package org.telegram.ui;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.ui.ManageLinksActivity;

public final /* synthetic */ class ManageLinksActivity$LinkCell$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ManageLinksActivity.LinkCell f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ ManageLinksActivity$LinkCell$$ExternalSyntheticLambda0(ManageLinksActivity.LinkCell linkCell, ArrayList arrayList) {
        this.f$0 = linkCell;
        this.f$1 = arrayList;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$new$2(this.f$1, dialogInterface, i);
    }
}
