package org.telegram.ui;

import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.ui.Cells.PhotoPickerAlbumsCell.PhotoPickerAlbumsCellDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoAlbumPickerActivity$ListAdapter$wak8B6ZyJqrggtYN7y4fwAyLKCg implements PhotoPickerAlbumsCellDelegate {
    private final /* synthetic */ ListAdapter f$0;

    public /* synthetic */ -$$Lambda$PhotoAlbumPickerActivity$ListAdapter$wak8B6ZyJqrggtYN7y4fwAyLKCg(ListAdapter listAdapter) {
        this.f$0 = listAdapter;
    }

    public final void didSelectAlbum(AlbumEntry albumEntry) {
        this.f$0.lambda$onCreateViewHolder$0$PhotoAlbumPickerActivity$ListAdapter(albumEntry);
    }
}
