package org.telegram.ui;

import org.telegram.ui.Cells.PhotoPickerSearchCell.PhotoPickerSearchCellDelegate;

final /* synthetic */ class PhotoAlbumPickerActivity$ListAdapter$$Lambda$1 implements PhotoPickerSearchCellDelegate {
    private final ListAdapter arg$1;

    PhotoAlbumPickerActivity$ListAdapter$$Lambda$1(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public void didPressedSearchButton(int i) {
        this.arg$1.lambda$onCreateViewHolder$1$PhotoAlbumPickerActivity$ListAdapter(i);
    }
}
