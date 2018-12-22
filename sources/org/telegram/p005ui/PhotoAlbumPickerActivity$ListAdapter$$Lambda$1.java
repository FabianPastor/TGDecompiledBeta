package org.telegram.p005ui;

import org.telegram.p005ui.Cells.PhotoPickerSearchCell.PhotoPickerSearchCellDelegate;
import org.telegram.p005ui.PhotoAlbumPickerActivity.ListAdapter;

/* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$ListAdapter$$Lambda$1 */
final /* synthetic */ class PhotoAlbumPickerActivity$ListAdapter$$Lambda$1 implements PhotoPickerSearchCellDelegate {
    private final ListAdapter arg$1;

    PhotoAlbumPickerActivity$ListAdapter$$Lambda$1(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public void didPressedSearchButton(int i) {
        this.arg$1.lambda$onCreateViewHolder$1$PhotoAlbumPickerActivity$ListAdapter(i);
    }
}
