package org.telegram.ui.Cells;

import android.view.Menu;
import org.telegram.messenger.LanguageDetector;
import org.telegram.ui.Cells.TextSelectionHelper;

public final /* synthetic */ class TextSelectionHelper$4$$ExternalSyntheticLambda1 implements LanguageDetector.ExceptionCallback {
    public final /* synthetic */ TextSelectionHelper.AnonymousClass4 f$0;
    public final /* synthetic */ Menu f$1;

    public /* synthetic */ TextSelectionHelper$4$$ExternalSyntheticLambda1(TextSelectionHelper.AnonymousClass4 r1, Menu menu) {
        this.f$0 = r1;
        this.f$1 = menu;
    }

    public final void run(Exception exc) {
        this.f$0.lambda$onPrepareActionMode$1(this.f$1, exc);
    }
}