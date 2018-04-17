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
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoPickerAlbumsCell extends FrameLayout {
    private AlbumEntry[] albumEntries = new AlbumEntry[4];
    private AlbumView[] albumViews = new AlbumView[4];
    private int albumsCount;
    private PhotoPickerAlbumsCellDelegate delegate;

    /* renamed from: org.telegram.ui.Cells.PhotoPickerAlbumsCell$1 */
    class C08821 implements OnClickListener {
        C08821() {
        }

        public void onClick(View v) {
            if (PhotoPickerAlbumsCell.this.delegate != null) {
                PhotoPickerAlbumsCell.this.delegate.didSelectAlbum(PhotoPickerAlbumsCell.this.albumEntries[((Integer) v.getTag()).intValue()]);
            }
        }
    }

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
            linearLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
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

        public boolean onTouchEvent(MotionEvent event) {
            if (VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(event.getX(), event.getY());
            }
            return super.onTouchEvent(event);
        }
    }

    public interface PhotoPickerAlbumsCellDelegate {
        void didSelectAlbum(AlbumEntry albumEntry);
    }

    public PhotoPickerAlbumsCell(Context context) {
        super(context);
        for (int a = 0; a < 4; a++) {
            this.albumViews[a] = new AlbumView(this, context);
            addView(this.albumViews[a]);
            this.albumViews[a].setVisibility(4);
            this.albumViews[a].setTag(Integer.valueOf(a));
            this.albumViews[a].setOnClickListener(new C08821());
        }
    }

    public void setAlbumsCount(int count) {
        int a = 0;
        while (a < this.albumViews.length) {
            this.albumViews[a].setVisibility(a < count ? 0 : 4);
            a++;
        }
        this.albumsCount = count;
    }

    public void setDelegate(PhotoPickerAlbumsCellDelegate delegate) {
        this.delegate = delegate;
    }

    public void setAlbum(int a, AlbumEntry albumEntry) {
        this.albumEntries[a] = albumEntry;
        if (albumEntry != null) {
            AlbumView albumView = this.albumViews[a];
            albumView.imageView.setOrientation(0, true);
            if (albumEntry.coverPhoto == null || albumEntry.coverPhoto.path == null) {
                albumView.imageView.setImageResource(R.drawable.nophotos);
            } else {
                albumView.imageView.setOrientation(albumEntry.coverPhoto.orientation, true);
                BackupImageView access$200;
                StringBuilder stringBuilder;
                if (albumEntry.coverPhoto.isVideo) {
                    access$200 = albumView.imageView;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("vthumb://");
                    stringBuilder.append(albumEntry.coverPhoto.imageId);
                    stringBuilder.append(":");
                    stringBuilder.append(albumEntry.coverPhoto.path);
                    access$200.setImage(stringBuilder.toString(), null, getContext().getResources().getDrawable(R.drawable.nophotos));
                } else {
                    access$200 = albumView.imageView;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("thumb://");
                    stringBuilder.append(albumEntry.coverPhoto.imageId);
                    stringBuilder.append(":");
                    stringBuilder.append(albumEntry.coverPhoto.path);
                    access$200.setImage(stringBuilder.toString(), null, getContext().getResources().getDrawable(R.drawable.nophotos));
                }
            }
            albumView.nameTextView.setText(albumEntry.bucketName);
            albumView.countTextView.setText(String.format("%d", new Object[]{Integer.valueOf(albumEntry.photos.size())}));
            return;
        }
        this.albumViews[a].setVisibility(4);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int itemWidth;
        if (AndroidUtilities.isTablet()) {
            itemWidth = (AndroidUtilities.dp(490.0f) - ((this.albumsCount + 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        } else {
            itemWidth = (AndroidUtilities.displaySize.x - ((this.albumsCount + 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        }
        for (int a = 0; a < this.albumsCount; a++) {
            LayoutParams layoutParams = (LayoutParams) this.albumViews[a].getLayoutParams();
            layoutParams.topMargin = AndroidUtilities.dp(4.0f);
            layoutParams.leftMargin = (AndroidUtilities.dp(4.0f) + itemWidth) * a;
            layoutParams.width = itemWidth;
            layoutParams.height = itemWidth;
            layoutParams.gravity = 51;
            this.albumViews[a].setLayoutParams(layoutParams);
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f) + itemWidth, NUM));
    }
}
