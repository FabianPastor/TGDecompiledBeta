package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.InviteMembersBottomSheet;

public final /* synthetic */ class InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ InviteMembersBottomSheet.SearchAdapter f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ InviteMembersBottomSheet$SearchAdapter$$ExternalSyntheticLambda3(InviteMembersBottomSheet.SearchAdapter searchAdapter, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = searchAdapter;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$1(this.f$1, this.f$2);
    }
}
