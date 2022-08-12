package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

@SuppressLint({"NewApi"})
public class PhotoAttachCameraCell extends FrameLayout {
    private ImageView backgroundView;
    private ImageView imageView;
    private int itemSize = AndroidUtilities.dp(0.0f);
    private final Theme.ResourcesProvider resourcesProvider;

    public PhotoAttachCameraCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        ImageView imageView2 = new ImageView(context);
        this.backgroundView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(this.backgroundView, LayoutHelper.createFrame(80, 80.0f));
        ImageView imageView3 = new ImageView(context);
        this.imageView = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setImageResource(R.drawable.instant_camera);
        addView(this.imageView, LayoutHelper.createFrame(80, 80.0f));
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), NUM), View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), NUM));
    }

    public void setItemSize(int i) {
        this.itemSize = i;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.imageView.getLayoutParams();
        int i2 = this.itemSize;
        layoutParams.height = i2;
        layoutParams.width = i2;
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.backgroundView.getLayoutParams();
        int i3 = this.itemSize;
        layoutParams2.height = i3;
        layoutParams2.width = i3;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogCameraIcon"), PorterDuff.Mode.MULTIPLY));
    }

    public void updateBitmap() {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg").getAbsolutePath());
        } catch (Throwable unused) {
            bitmap = null;
        }
        if (bitmap != null) {
            this.backgroundView.setImageBitmap(bitmap);
        } else {
            this.backgroundView.setImageResource(R.drawable.icplaceholder);
        }
    }

    /* access modifiers changed from: protected */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
