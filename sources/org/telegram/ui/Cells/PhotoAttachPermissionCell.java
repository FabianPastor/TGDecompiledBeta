package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes3.dex */
public class PhotoAttachPermissionCell extends FrameLayout {
    private ImageView imageView;
    private ImageView imageView2;
    private int itemSize;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public PhotoAttachPermissionCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_attachPermissionImage"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(44, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
        ImageView imageView2 = new ImageView(context);
        this.imageView2 = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_attachPermissionMark"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView2, LayoutHelper.createFrame(44, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(getThemedColor("chat_attachPermissionText"));
        this.textView.setTextSize(1, 12.0f);
        this.textView.setGravity(17);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 17, 5.0f, 13.0f, 5.0f, 0.0f));
        this.itemSize = AndroidUtilities.dp(80.0f);
    }

    public void setItemSize(int i) {
        this.itemSize = i;
    }

    public void setType(int i) {
        if (i == 0) {
            this.imageView.setImageResource(R.drawable.permissions_camera1);
            this.imageView2.setImageResource(R.drawable.permissions_camera2);
            this.textView.setText(LocaleController.getString("CameraPermissionText", R.string.CameraPermissionText));
            this.imageView.setLayoutParams(LayoutHelper.createFrame(44, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
            this.imageView2.setLayoutParams(LayoutHelper.createFrame(44, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
            return;
        }
        this.imageView.setImageResource(R.drawable.permissions_gallery1);
        this.imageView2.setImageResource(R.drawable.permissions_gallery2);
        this.textView.setText(LocaleController.getString("GalleryPermissionText", R.string.GalleryPermissionText));
        this.imageView.setLayoutParams(LayoutHelper.createFrame(44, 44.0f, 17, 0.0f, 0.0f, 2.0f, 27.0f));
        this.imageView2.setLayoutParams(LayoutHelper.createFrame(44, 44.0f, 17, 0.0f, 0.0f, 2.0f, 27.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemSize, NUM), View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), NUM));
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
