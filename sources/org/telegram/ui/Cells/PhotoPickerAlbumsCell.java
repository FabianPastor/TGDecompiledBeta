package org.telegram.ui.Cells;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoPickerAlbumsCell extends FrameLayout {
    private AlbumEntry[] albumEntries = new AlbumEntry[4];
    private AlbumView[] albumViews = new AlbumView[4];
    private int albumsCount;
    private PhotoPickerAlbumsCellDelegate delegate;

    private class AlbumView extends FrameLayout {
        private TextView countTextView;
        private BackupImageView imageView;
        private TextView nameTextView;
        private View selector;
        final /* synthetic */ PhotoPickerAlbumsCell this$0;

        public AlbumView(PhotoPickerAlbumsCell photoPickerAlbumsCell, Context context) {
            Context context2 = context;
            this.this$0 = photoPickerAlbumsCell;
            super(context2);
            this.imageView = new BackupImageView(context2);
            addView(this.imageView, LayoutHelper.createFrame(-1, -1.0f));
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            linearLayout.setBackgroundColor(NUM);
            addView(linearLayout, LayoutHelper.createFrame(-1, 28, 83));
            this.nameTextView = new TextView(context2);
            this.nameTextView.setTextSize(1, 13.0f);
            this.nameTextView.setTextColor(-1);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setEllipsize(TruncateAt.END);
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setGravity(16);
            linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(0, -1, 1.0f, 8, 0, 0, 0));
            this.countTextView = new TextView(context2);
            this.countTextView.setTextSize(1, 13.0f);
            this.countTextView.setTextColor(-5592406);
            this.countTextView.setSingleLine(true);
            this.countTextView.setEllipsize(TruncateAt.END);
            this.countTextView.setMaxLines(1);
            this.countTextView.setGravity(16);
            linearLayout.addView(this.countTextView, LayoutHelper.createLinear(-2, -1, 4.0f, 0.0f, 4.0f, 0.0f));
            this.selector = new View(context2);
            this.selector.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            addView(this.selector, LayoutHelper.createFrame(-1, -1.0f));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
            }
            return super.onTouchEvent(motionEvent);
        }
    }

    public interface PhotoPickerAlbumsCellDelegate {
        void didSelectAlbum(AlbumEntry albumEntry);
    }

    public PhotoPickerAlbumsCell(Context context) {
        super(context);
        for (int i = 0; i < 4; i++) {
            this.albumViews[i] = new AlbumView(this, context);
            addView(this.albumViews[i]);
            this.albumViews[i].setVisibility(4);
            this.albumViews[i].setTag(Integer.valueOf(i));
            this.albumViews[i].setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (PhotoPickerAlbumsCell.this.delegate != null) {
                        PhotoPickerAlbumsCell.this.delegate.didSelectAlbum(PhotoPickerAlbumsCell.this.albumEntries[((Integer) view.getTag()).intValue()]);
                    }
                }
            });
        }
    }

    public void setAlbumsCount(int i) {
        int i2 = 0;
        while (true) {
            AlbumView[] albumViewArr = this.albumViews;
            if (i2 < albumViewArr.length) {
                albumViewArr[i2].setVisibility(i2 < i ? 0 : 4);
                i2++;
            } else {
                this.albumsCount = i;
                return;
            }
        }
    }

    public void setDelegate(PhotoPickerAlbumsCellDelegate photoPickerAlbumsCellDelegate) {
        this.delegate = photoPickerAlbumsCellDelegate;
    }

    public void setAlbum(int i, AlbumEntry albumEntry) {
        this.albumEntries[i] = albumEntry;
        if (albumEntry != null) {
            AlbumView albumView = this.albumViews[i];
            albumView.imageView.setOrientation(0, true);
            PhotoEntry photoEntry = albumEntry.coverPhoto;
            if (photoEntry == null || photoEntry.path == null) {
                albumView.imageView.setImageResource(NUM);
            } else {
                albumView.imageView.setOrientation(albumEntry.coverPhoto.orientation, true);
                String str = ":";
                BackupImageView access$200;
                StringBuilder stringBuilder;
                if (albumEntry.coverPhoto.isVideo) {
                    access$200 = albumView.imageView;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("vthumb://");
                    stringBuilder.append(albumEntry.coverPhoto.imageId);
                    stringBuilder.append(str);
                    stringBuilder.append(albumEntry.coverPhoto.path);
                    access$200.setImage(stringBuilder.toString(), null, getContext().getResources().getDrawable(NUM));
                } else {
                    access$200 = albumView.imageView;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("thumb://");
                    stringBuilder.append(albumEntry.coverPhoto.imageId);
                    stringBuilder.append(str);
                    stringBuilder.append(albumEntry.coverPhoto.path);
                    access$200.setImage(stringBuilder.toString(), null, getContext().getResources().getDrawable(NUM));
                }
            }
            albumView.nameTextView.setText(albumEntry.bucketName);
            albumView.countTextView.setText(String.format("%d", new Object[]{Integer.valueOf(albumEntry.photos.size())}));
            return;
        }
        this.albumViews[i].setVisibility(4);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        if (AndroidUtilities.isTablet()) {
            i2 = (AndroidUtilities.dp(490.0f) - ((this.albumsCount + 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        } else {
            i2 = (AndroidUtilities.displaySize.x - ((this.albumsCount + 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        }
        for (int i3 = 0; i3 < this.albumsCount; i3++) {
            LayoutParams layoutParams = (LayoutParams) this.albumViews[i3].getLayoutParams();
            layoutParams.topMargin = AndroidUtilities.dp(4.0f);
            layoutParams.leftMargin = (AndroidUtilities.dp(4.0f) + i2) * i3;
            layoutParams.width = i2;
            layoutParams.height = i2;
            layoutParams.gravity = 51;
            this.albumViews[i3].setLayoutParams(layoutParams);
        }
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f) + i2, NUM));
    }
}
