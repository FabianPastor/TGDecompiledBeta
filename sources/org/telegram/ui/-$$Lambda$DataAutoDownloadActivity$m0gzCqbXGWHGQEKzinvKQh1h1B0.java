package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Cells.TextCheckCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataAutoDownloadActivity$m0gzCqbXGWHGQEKzinvKQh1h1B0 implements OnClickListener {
    private final /* synthetic */ TextCheckCell[] f$0;

    public /* synthetic */ -$$Lambda$DataAutoDownloadActivity$m0gzCqbXGWHGQEKzinvKQh1h1B0(TextCheckCell[] textCheckCellArr) {
        this.f$0 = textCheckCellArr;
    }

    public final void onClick(View view) {
        this.f$0[0].setChecked(this.f$0[0].isChecked() ^ 1);
    }
}
