package org.telegram.ui;

import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate;

final /* synthetic */ class PhotoAlbumPickerActivity$ListAdapter$$Lambda$0 implements PhotoPickerAlbumsCellDelegate {
    private final ListAdapter arg$1;

    PhotoAlbumPickerActivity$ListAdapter$$Lambda$0(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public void didSelectAlbum(AlbumEntry albumEntry) {
        this.arg$1.lambda$onCreateViewHolder$0$PhotoAlbumPickerActivity$ListAdapter(albumEntry);
    }
}
