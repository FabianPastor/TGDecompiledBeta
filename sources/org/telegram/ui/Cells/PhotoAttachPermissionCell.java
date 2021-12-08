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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoAttachPermissionCell extends FrameLayout {
    private ImageView imageView;
    private ImageView imageView2;
    private int itemSize = AndroidUtilities.dp(80.0f);
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public PhotoAttachPermissionCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        ImageView imageView3 = new ImageView(context);
        this.imageView = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_attachPermissionImage"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(44, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
        ImageView imageView4 = new ImageView(context);
        this.imageView2 = imageView4;
        imageView4.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_attachPermissionMark"), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView2, LayoutHelper.createFrame(44, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(getThemedColor("chat_attachPermissionText"));
        this.textView.setTextSize(1, 12.0f);
        this.textView.setGravity(17);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 17, 5.0f, 13.0f, 5.0f, 0.0f));
    }

    public void setItemSize(int size) {
        this.itemSize = size;
    }

    public void setType(int type) {
        if (type == 0) {
            this.imageView.setImageResource(NUM);
            this.imageView2.setImageResource(NUM);
            this.textView.setText(LocaleController.getString("CameraPermissionText", NUM));
            this.imageView.setLayoutParams(LayoutHelper.createFrame(44, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
            this.imageView2.setLayoutParams(LayoutHelper.createFrame(44, 44.0f, 17, 5.0f, 0.0f, 0.0f, 27.0f));
            return;
        }
        this.imageView.setImageResource(NUM);
        this.imageView2.setImageResource(NUM);
        this.textView.setText(LocaleController.getString("GalleryPermissionText", NUM));
        this.imageView.setLayoutParams(LayoutHelper.createFrame(44, 44.0f, 17, 0.0f, 0.0f, 2.0f, 27.0f));
        this.imageView2.setLayoutParams(LayoutHelper.createFrame(44, 44.0f, 17, 0.0f, 0.0f, 2.0f, 27.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.itemSize, NUM), View.MeasureSpec.makeMeasureSpec(this.itemSize + AndroidUtilities.dp(5.0f), NUM));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
