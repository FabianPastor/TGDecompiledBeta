package org.telegram.messenger;

import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileLoader$4dX6FY75qVi0nYcXFAFSA0OGeOs implements Runnable {
    private final /* synthetic */ ArrayList f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$FileLoader$4dX6FY75qVi0nYcXFAFSA0OGeOs(ArrayList arrayList, int i) {
        this.f$0 = arrayList;
        this.f$1 = i;
    }

    public final void run() {
        FileLoader.lambda$deleteFiles$10(this.f$0, this.f$1);
    }
}
