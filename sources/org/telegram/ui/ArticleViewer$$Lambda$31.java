package org.telegram.ui;

import org.telegram.ui.ArticleViewer.PlaceProviderObject;

final /* synthetic */ class ArticleViewer$$Lambda$31 implements Runnable {
    private final ArticleViewer arg$1;
    private final PlaceProviderObject arg$2;

    ArticleViewer$$Lambda$31(ArticleViewer articleViewer, PlaceProviderObject placeProviderObject) {
        this.arg$1 = articleViewer;
        this.arg$2 = placeProviderObject;
    }

    public void run() {
        this.arg$1.lambda$openPhoto$39$ArticleViewer(this.arg$2);
    }
}
