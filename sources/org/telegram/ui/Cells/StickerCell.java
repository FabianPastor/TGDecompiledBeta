package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class StickerCell extends FrameLayout {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private boolean clearsInputField;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private Object parentObject;
    private float scale;
    private boolean scaled;
    private TLRPC.Document sticker;
    private long time = 0;

    public StickerCell(Context context) {
        super(context);
        this.imageView = new BackupImageView(context);
        this.imageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(66, 66.0f, 1, 0.0f, 5.0f, 0.0f, 0.0f));
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(76.0f) + getPaddingLeft() + getPaddingRight(), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0f), NUM));
    }

    public void setPressed(boolean z) {
        if (this.imageView.getImageReceiver().getPressed() != z) {
            this.imageView.getImageReceiver().setPressed(z ? 1 : 0);
            this.imageView.invalidate();
        }
        super.setPressed(z);
    }

    public void setClearsInputField(boolean z) {
        this.clearsInputField = z;
    }

    public boolean isClearsInputField() {
        return this.clearsInputField;
    }

    public void setSticker(TLRPC.Document document, Object obj, int i) {
        TLRPC.Document document2 = document;
        int i2 = i;
        this.parentObject = obj;
        if (document2 != null) {
            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
            if (!MessageObject.canAutoplayAnimatedSticker(document)) {
                this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, document2), (String) null, "webp", (Drawable) null, this.parentObject);
            } else if (closestPhotoSizeWithSize != null) {
                this.imageView.setImage(ImageLocation.getForDocument(document), "80_80", ImageLocation.getForDocument(closestPhotoSizeWithSize, document2), (String) null, 0, this.parentObject);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(document), "80_80", (String) null, (Drawable) null, this.parentObject);
            }
        }
        this.sticker = document2;
        if (i2 == -1) {
            setBackgroundResource(NUM);
            setPadding(AndroidUtilities.dp(7.0f), 0, 0, 0);
        } else if (i2 == 0) {
            setBackgroundResource(NUM);
            setPadding(0, 0, 0, 0);
        } else if (i2 == 1) {
            setBackgroundResource(NUM);
            setPadding(0, 0, AndroidUtilities.dp(7.0f), 0);
        } else if (i2 == 2) {
            setBackgroundResource(NUM);
            setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
        }
        Drawable background = getBackground();
        if (background != null) {
            background.setAlpha(230);
            background.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_stickersHintPanel"), PorterDuff.Mode.MULTIPLY));
        }
    }

    public TLRPC.Document getSticker() {
        return this.sticker;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && ((this.scaled && this.scale != 0.8f) || (!this.scaled && this.scale != 1.0f))) {
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.scaled) {
                float f = this.scale;
                if (f != 0.8f) {
                    this.scale = f - (((float) j2) / 400.0f);
                    if (this.scale < 0.8f) {
                        this.scale = 0.8f;
                    }
                    this.imageView.setScaleX(this.scale);
                    this.imageView.setScaleY(this.scale);
                    this.imageView.invalidate();
                    invalidate();
                }
            }
            this.scale += ((float) j2) / 400.0f;
            if (this.scale > 1.0f) {
                this.scale = 1.0f;
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return drawChild;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.sticker != null) {
            String str = null;
            for (int i = 0; i < this.sticker.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = this.sticker.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeSticker) {
                    String str2 = documentAttribute.alt;
                    str = (str2 == null || str2.length() <= 0) ? null : documentAttribute.alt;
                }
            }
            if (str != null) {
                accessibilityNodeInfo.setText(str + " " + LocaleController.getString("AttachSticker", NUM));
            } else {
                accessibilityNodeInfo.setText(LocaleController.getString("AttachSticker", NUM));
            }
            accessibilityNodeInfo.setEnabled(true);
        }
    }
}
