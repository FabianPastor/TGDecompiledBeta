package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_maskCoords;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.Rect;
import org.telegram.ui.Components.Size;

public class StickerView extends EntityView {
    private int anchor;
    private Size baseSize;
    private ImageReceiver centerImage;
    private FrameLayoutDrawer containerView;
    private boolean mirrored;
    private Object parentObject;
    private TLRPC$Document sticker;

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            StickerView.this.stickerDraw(canvas);
        }
    }

    public StickerView(Context context, Point point, float f, float f2, Size size, TLRPC$Document tLRPC$Document, Object obj) {
        super(context, point);
        this.anchor = -1;
        int i = 0;
        this.mirrored = false;
        this.centerImage = new ImageReceiver();
        setRotation(f);
        setScale(f2);
        this.sticker = tLRPC$Document;
        this.baseSize = size;
        this.parentObject = obj;
        while (true) {
            if (i >= tLRPC$Document.attributes.size()) {
                break;
            }
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                TLRPC$TL_maskCoords tLRPC$TL_maskCoords = tLRPC$DocumentAttribute.mask_coords;
                if (tLRPC$TL_maskCoords != null) {
                    this.anchor = tLRPC$TL_maskCoords.n;
                }
            } else {
                i++;
            }
        }
        FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(context);
        this.containerView = frameLayoutDrawer;
        addView(frameLayoutDrawer, LayoutHelper.createFrame(-1, -1.0f));
        this.centerImage.setAspectFit(true);
        this.centerImage.setInvalidateAll(true);
        this.centerImage.setParentView(this.containerView);
        this.centerImage.setImage(ImageLocation.getForDocument(tLRPC$Document), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document), (String) null, "webp", obj, 1);
        updatePosition();
    }

    public StickerView(Context context, StickerView stickerView, Point point) {
        this(context, point, stickerView.getRotation(), stickerView.getScale(), stickerView.baseSize, stickerView.sticker, stickerView.parentObject);
        if (stickerView.mirrored) {
            mirror();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.centerImage.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.centerImage.onAttachedToWindow();
    }

    public int getAnchor() {
        return this.anchor;
    }

    public void mirror() {
        this.mirrored = !this.mirrored;
        this.containerView.invalidate();
    }

    public boolean isMirrored() {
        return this.mirrored;
    }

    /* access modifiers changed from: protected */
    public void updatePosition() {
        Size size = this.baseSize;
        setX(this.position.x - (size.width / 2.0f));
        setY(this.position.y - (size.height / 2.0f));
        updateSelectionView();
    }

    /* access modifiers changed from: protected */
    public void stickerDraw(Canvas canvas) {
        if (this.containerView != null) {
            canvas.save();
            if (this.mirrored) {
                canvas.scale(-1.0f, 1.0f);
                canvas.translate(-this.baseSize.width, 0.0f);
            }
            ImageReceiver imageReceiver = this.centerImage;
            Size size = this.baseSize;
            imageReceiver.setImageCoords(0.0f, 0.0f, (float) ((int) size.width), (float) ((int) size.height));
            this.centerImage.draw(canvas);
            canvas.restore();
        }
    }

    public long getDuration() {
        RLottieDrawable lottieAnimation = this.centerImage.getLottieAnimation();
        if (lottieAnimation == null) {
            return 0;
        }
        return lottieAnimation.getDuration();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) this.baseSize.width, NUM), View.MeasureSpec.makeMeasureSpec((int) this.baseSize.height, NUM));
    }

    /* access modifiers changed from: protected */
    public Rect getSelectionBounds() {
        float scaleX = ((ViewGroup) getParent()).getScaleX();
        float measuredWidth = ((float) getMeasuredWidth()) * (getScale() + 0.4f);
        Point point = this.position;
        float f = measuredWidth / 2.0f;
        float f2 = measuredWidth * scaleX;
        return new Rect((point.x - f) * scaleX, (point.y - f) * scaleX, f2, f2);
    }

    /* access modifiers changed from: protected */
    public EntityView.SelectionView createSelectionView() {
        return new StickerViewSelectionView(this, getContext());
    }

    public TLRPC$Document getSticker() {
        return this.sticker;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public Size getBaseSize() {
        return this.baseSize;
    }

    public class StickerViewSelectionView extends EntityView.SelectionView {
        private Paint arcPaint = new Paint(1);
        private RectF arcRect = new RectF();

        public StickerViewSelectionView(StickerView stickerView, Context context) {
            super(context);
            this.arcPaint.setColor(-1);
            this.arcPaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
            this.arcPaint.setStyle(Paint.Style.STROKE);
        }

        /* access modifiers changed from: protected */
        public int pointInsideHandle(float f, float f2) {
            float dp = (float) AndroidUtilities.dp(19.5f);
            float dp2 = ((float) AndroidUtilities.dp(1.0f)) + dp;
            float f3 = dp2 * 2.0f;
            float measuredHeight = ((((float) getMeasuredHeight()) - f3) / 2.0f) + dp2;
            if (f > dp2 - dp && f2 > measuredHeight - dp && f < dp2 + dp && f2 < measuredHeight + dp) {
                return 1;
            }
            if (f > ((((float) getMeasuredWidth()) - f3) + dp2) - dp && f2 > measuredHeight - dp && f < dp2 + (((float) getMeasuredWidth()) - f3) + dp && f2 < measuredHeight + dp) {
                return 2;
            }
            float measuredWidth = ((float) getMeasuredWidth()) / 2.0f;
            return Math.pow((double) (f - measuredWidth), 2.0d) + Math.pow((double) (f2 - measuredWidth), 2.0d) < Math.pow((double) measuredWidth, 2.0d) ? 3 : 0;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float dp = (float) AndroidUtilities.dp(4.5f);
            float dp2 = ((float) AndroidUtilities.dp(1.0f)) + dp + ((float) AndroidUtilities.dp(15.0f));
            float measuredWidth = ((float) (getMeasuredWidth() / 2)) - dp2;
            float f = (2.0f * measuredWidth) + dp2;
            this.arcRect.set(dp2, dp2, f, f);
            for (int i = 0; i < 48; i++) {
                canvas.drawArc(this.arcRect, ((float) i) * 8.0f, 4.0f, false, this.arcPaint);
            }
            float f2 = measuredWidth + dp2;
            canvas.drawCircle(dp2, f2, dp, this.dotPaint);
            canvas.drawCircle(dp2, f2, dp, this.dotStrokePaint);
            canvas.drawCircle(f, f2, dp, this.dotPaint);
            canvas.drawCircle(f, f2, dp, this.dotStrokePaint);
        }
    }
}
