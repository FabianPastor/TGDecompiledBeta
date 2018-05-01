package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_wallPaperSolid;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class WallpaperCell extends FrameLayout {
    private BackupImageView imageView;
    private ImageView imageView2;
    private View selectionView;

    public WallpaperCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        addView(this.imageView, LayoutHelper.createFrame(100, 100, 83));
        this.imageView2 = new ImageView(context);
        this.imageView2.setImageResource(C0446R.drawable.ic_gallery_background);
        this.imageView2.setScaleType(ScaleType.CENTER);
        addView(this.imageView2, LayoutHelper.createFrame(100, 100, 83));
        this.selectionView = new View(context);
        this.selectionView.setBackgroundResource(C0446R.drawable.wall_selection);
        addView(this.selectionView, LayoutHelper.createFrame(100, 102.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(102.0f), NUM));
    }

    public void setWallpaper(WallPaper wallPaper, int i, Drawable drawable, boolean z) {
        int i2 = NUM;
        int i3 = 4;
        int i4 = 0;
        if (wallPaper == null) {
            this.imageView.setVisibility(4);
            this.imageView2.setVisibility(0);
            if (z) {
                wallPaper = this.selectionView;
                if (i == true) {
                    i3 = 0;
                }
                wallPaper.setVisibility(i3);
                this.imageView2.setImageDrawable(drawable);
                this.imageView2.setScaleType(ScaleType.CENTER_CROP);
                return;
            }
            wallPaper = this.selectionView;
            if (i == -1) {
                i3 = 0;
            }
            wallPaper.setVisibility(i3);
            wallPaper = this.imageView2;
            if (i != -1) {
                if (i != 1000001) {
                    i2 = NUM;
                }
            }
            wallPaper.setBackgroundColor(i2);
            this.imageView2.setScaleType(ScaleType.CENTER);
            this.imageView2.setImageResource(C0446R.drawable.ic_gallery_background);
            return;
        }
        this.imageView.setVisibility(0);
        this.imageView2.setVisibility(4);
        drawable = this.selectionView;
        if (i == wallPaper.id) {
            i3 = 0;
        }
        drawable.setVisibility(i3);
        if ((wallPaper instanceof TL_wallPaperSolid) != 0) {
            this.imageView.setImageBitmap(null);
            this.imageView.setBackgroundColor(wallPaper.bg_color | Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
            return;
        }
        i = AndroidUtilities.dp(NUM);
        z = false;
        while (i4 < wallPaper.sizes.size()) {
            PhotoSize photoSize = (PhotoSize) wallPaper.sizes.get(i4);
            if (photoSize != null) {
                int i5 = photoSize.f43w >= photoSize.f42h ? photoSize.f43w : photoSize.f42h;
                if (!z || ((i > 100 && z.location != null && z.location.dc_id == Integer.MIN_VALUE) || (photoSize instanceof TL_photoCachedSize) || i5 <= i)) {
                    z = photoSize;
                }
            }
            i4++;
        }
        if (z && z.location != null) {
            this.imageView.setImage(z.location, "100_100", (Drawable) null);
        }
        this.imageView.setBackgroundColor(NUM);
    }
}
