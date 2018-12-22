package org.telegram.p005ui;

import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.p005ui.Cells.PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate;
import org.telegram.p005ui.PhotoAlbumPickerActivity.ListAdapter;

/* renamed from: org.telegram.ui.PhotoAlbumPickerActivity$ListAdapter$$Lambda$0 */
final /* synthetic */ class PhotoAlbumPickerActivity$ListAdapter$$Lambda$0 implements PhotoPickerAlbumsCellDelegate {
    private final ListAdapter arg$1;

    PhotoAlbumPickerActivity$ListAdapter$$Lambda$0(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public void didSelectAlbum(AlbumEntry albumEntry) {
        this.arg$1.lambda$onCreateViewHolder$0$PhotoAlbumPickerActivity$ListAdapter(albumEntry);
    }
}
