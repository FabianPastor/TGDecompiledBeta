package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;

public class PhotoPickerAlbumsCell extends FrameLayout {
    private MediaController.AlbumEntry[] albumEntries = new MediaController.AlbumEntry[4];
    private AlbumView[] albumViews = new AlbumView[4];
    private int albumsCount;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint();
    private PhotoPickerAlbumsCellDelegate delegate;

    public interface PhotoPickerAlbumsCellDelegate {
        void didSelectAlbum(MediaController.AlbumEntry albumEntry);
    }

    private class AlbumView extends FrameLayout {
        /* access modifiers changed from: private */
        public TextView countTextView;
        /* access modifiers changed from: private */
        public BackupImageView imageView;
        /* access modifiers changed from: private */
        public TextView nameTextView;
        private View selector;
        final /* synthetic */ PhotoPickerAlbumsCell this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public AlbumView(org.telegram.ui.Cells.PhotoPickerAlbumsCell r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                r2 = r19
                r0.this$0 = r1
                r0.<init>(r2)
                org.telegram.ui.Components.BackupImageView r1 = new org.telegram.ui.Components.BackupImageView
                r1.<init>(r2)
                r0.imageView = r1
                org.telegram.ui.Components.BackupImageView r1 = r0.imageView
                r3 = -1082130432(0xffffffffbvar_, float:-1.0)
                r4 = -1
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
                r0.addView(r1, r5)
                android.widget.LinearLayout r1 = new android.widget.LinearLayout
                r1.<init>(r2)
                r5 = 0
                r1.setOrientation(r5)
                r6 = 2131165259(0x7var_b, float:1.794473E38)
                r1.setBackgroundResource(r6)
                r6 = 60
                r7 = 83
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r6, (int) r7)
                r0.addView(r1, r6)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r2)
                r0.nameTextView = r6
                android.widget.TextView r6 = r0.nameTextView
                r7 = 1095761920(0x41500000, float:13.0)
                r8 = 1
                r6.setTextSize(r8, r7)
                android.widget.TextView r6 = r0.nameTextView
                r6.setTextColor(r4)
                android.widget.TextView r6 = r0.nameTextView
                r6.setSingleLine(r8)
                android.widget.TextView r6 = r0.nameTextView
                android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
                r6.setEllipsize(r9)
                android.widget.TextView r6 = r0.nameTextView
                r6.setMaxLines(r8)
                android.widget.TextView r6 = r0.nameTextView
                r9 = 80
                r6.setGravity(r9)
                android.widget.TextView r6 = r0.nameTextView
                r10 = 0
                r11 = -1
                r12 = 1065353216(0x3var_, float:1.0)
                r13 = 8
                r14 = 0
                r15 = 0
                r16 = 5
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (float) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r1.addView(r6, r10)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r2)
                r0.countTextView = r6
                android.widget.TextView r6 = r0.countTextView
                r6.setTextSize(r8, r7)
                android.widget.TextView r6 = r0.countTextView
                r6.setTextColor(r4)
                android.widget.TextView r6 = r0.countTextView
                r6.setSingleLine(r8)
                android.widget.TextView r6 = r0.countTextView
                android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
                r6.setEllipsize(r7)
                android.widget.TextView r6 = r0.countTextView
                r6.setMaxLines(r8)
                android.widget.TextView r6 = r0.countTextView
                r6.setGravity(r9)
                android.widget.TextView r6 = r0.countTextView
                r7 = -2
                r8 = -1
                r9 = 1082130432(0x40800000, float:4.0)
                r10 = 0
                r11 = 1088421888(0x40e00000, float:7.0)
                r12 = 1084227584(0x40a00000, float:5.0)
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r7, r8, r9, r10, r11, r12)
                r1.addView(r6, r7)
                android.view.View r1 = new android.view.View
                r1.<init>(r2)
                r0.selector = r1
                android.view.View r1 = r0.selector
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
                r1.setBackgroundDrawable(r2)
                android.view.View r1 = r0.selector
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
                r0.addView(r1, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.PhotoPickerAlbumsCell.AlbumView.<init>(org.telegram.ui.Cells.PhotoPickerAlbumsCell, android.content.Context):void");
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (Build.VERSION.SDK_INT >= 21) {
                this.selector.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
            }
            return super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (!this.imageView.getImageReceiver().hasNotThumb() || this.imageView.getImageReceiver().getCurrentAlpha() != 1.0f) {
                this.this$0.backgroundPaint.setColor(Theme.getColor("chat_attachPhotoBackground"));
                canvas.drawRect(0.0f, 0.0f, (float) this.imageView.getMeasuredWidth(), (float) this.imageView.getMeasuredHeight(), this.this$0.backgroundPaint);
            }
        }
    }

    public PhotoPickerAlbumsCell(Context context) {
        super(context);
        for (int i = 0; i < 4; i++) {
            this.albumViews[i] = new AlbumView(this, context);
            addView(this.albumViews[i]);
            this.albumViews[i].setVisibility(4);
            this.albumViews[i].setTag(Integer.valueOf(i));
            this.albumViews[i].setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoPickerAlbumsCell.this.lambda$new$0$PhotoPickerAlbumsCell(view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$new$0$PhotoPickerAlbumsCell(View view) {
        PhotoPickerAlbumsCellDelegate photoPickerAlbumsCellDelegate = this.delegate;
        if (photoPickerAlbumsCellDelegate != null) {
            photoPickerAlbumsCellDelegate.didSelectAlbum(this.albumEntries[((Integer) view.getTag()).intValue()]);
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

    public void setAlbum(int i, MediaController.AlbumEntry albumEntry) {
        this.albumEntries[i] = albumEntry;
        if (albumEntry != null) {
            AlbumView albumView = this.albumViews[i];
            albumView.imageView.setOrientation(0, true);
            MediaController.PhotoEntry photoEntry = albumEntry.coverPhoto;
            if (photoEntry == null || photoEntry.path == null) {
                albumView.imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
            } else {
                albumView.imageView.setOrientation(albumEntry.coverPhoto.orientation, true);
                if (albumEntry.coverPhoto.isVideo) {
                    BackupImageView access$100 = albumView.imageView;
                    access$100.setImage("vthumb://" + albumEntry.coverPhoto.imageId + ":" + albumEntry.coverPhoto.path, (String) null, Theme.chat_attachEmptyDrawable);
                } else {
                    BackupImageView access$1002 = albumView.imageView;
                    access$1002.setImage("thumb://" + albumEntry.coverPhoto.imageId + ":" + albumEntry.coverPhoto.path, (String) null, Theme.chat_attachEmptyDrawable);
                }
            }
            albumView.nameTextView.setText(albumEntry.bucketName);
            albumView.countTextView.setText(String.format("%d", new Object[]{Integer.valueOf(albumEntry.photos.size())}));
            return;
        }
        this.albumViews[i].setVisibility(4);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        if (AndroidUtilities.isTablet()) {
            i3 = ((AndroidUtilities.dp(490.0f) - AndroidUtilities.dp(12.0f)) - ((this.albumsCount - 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        } else {
            i3 = ((AndroidUtilities.displaySize.x - AndroidUtilities.dp(12.0f)) - ((this.albumsCount - 1) * AndroidUtilities.dp(4.0f))) / this.albumsCount;
        }
        for (int i4 = 0; i4 < this.albumsCount; i4++) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.albumViews[i4].getLayoutParams();
            layoutParams.topMargin = AndroidUtilities.dp(4.0f);
            layoutParams.leftMargin = (AndroidUtilities.dp(4.0f) + i3) * i4;
            layoutParams.width = i3;
            layoutParams.height = i3;
            layoutParams.gravity = 51;
            this.albumViews[i4].setLayoutParams(layoutParams);
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f) + i3, NUM));
    }
}
