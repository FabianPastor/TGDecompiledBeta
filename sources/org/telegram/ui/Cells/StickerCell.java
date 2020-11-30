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
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class StickerCell extends FrameLayout {
    private boolean clearsInputField;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private Object parentObject;
    private float scale;
    private boolean scaled;
    private TLRPC$Document sticker;

    static {
        new AccelerateInterpolator(0.5f);
    }

    public StickerCell(Context context) {
        super(context);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
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

    public void setSticker(TLRPC$Document tLRPC$Document, Object obj, int i) {
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        int i2 = i;
        this.parentObject = obj;
        if (tLRPC$Document2 != null) {
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90);
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$Document2, false);
            if (!MessageObject.canAutoplayAnimatedSticker(tLRPC$Document)) {
                this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document2), (String) null, "webp", (Drawable) null, this.parentObject);
            } else if (svgThumb != null) {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "80_80", (String) null, svgThumb, this.parentObject);
            } else if (closestPhotoSizeWithSize != null) {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "80_80", ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document2), (String) null, 0, this.parentObject);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "80_80", (String) null, (Drawable) null, this.parentObject);
            }
        }
        this.sticker = tLRPC$Document2;
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

    public TLRPC$Document getSticker() {
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
        boolean z;
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && ((z && this.scale != 0.8f) || (!(z = this.scaled) && this.scale != 1.0f))) {
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.scaled) {
                float f = this.scale;
                if (f != 0.8f) {
                    float f2 = f - (((float) j2) / 400.0f);
                    this.scale = f2;
                    if (f2 < 0.8f) {
                        this.scale = 0.8f;
                    }
                    this.imageView.setScaleX(this.scale);
                    this.imageView.setScaleY(this.scale);
                    this.imageView.invalidate();
                    invalidate();
                }
            }
            float f3 = this.scale + (((float) j2) / 400.0f);
            this.scale = f3;
            if (f3 > 1.0f) {
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
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.sticker.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                    String str2 = tLRPC$DocumentAttribute.alt;
                    str = (str2 == null || str2.length() <= 0) ? null : tLRPC$DocumentAttribute.alt;
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
