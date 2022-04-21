package org.telegram.ui;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell;
import org.telegram.ui.PhotoAlbumPickerActivity;

public final /* synthetic */ class PhotoAlbumPickerActivity$ListAdapter$$ExternalSyntheticLambda0 implements PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate {
    public final /* synthetic */ PhotoAlbumPickerActivity.ListAdapter f$0;

    public /* synthetic */ PhotoAlbumPickerActivity$ListAdapter$$ExternalSyntheticLambda0(PhotoAlbumPickerActivity.ListAdapter listAdapter) {
        this.f$0 = listAdapter;
    }

    public final void didSelectAlbum(MediaController.AlbumEntry albumEntry) {
        this.f$0.m2882x4evar_c(albumEntry);
    }
}
