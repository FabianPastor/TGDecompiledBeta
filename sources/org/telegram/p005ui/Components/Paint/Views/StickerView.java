package org.telegram.p005ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.Paint.Views.EntityView.SelectionView;
import org.telegram.p005ui.Components.Point;
import org.telegram.p005ui.Components.Rect;
import org.telegram.p005ui.Components.Size;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;

/* renamed from: org.telegram.ui.Components.Paint.Views.StickerView */
public class StickerView extends EntityView {
    private int anchor;
    private Size baseSize;
    private ImageReceiver centerImage;
    private FrameLayoutDrawer containerView;
    private boolean mirrored;
    private Object parentObject;
    private Document sticker;

    /* renamed from: org.telegram.ui.Components.Paint.Views.StickerView$FrameLayoutDrawer */
    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        protected void onDraw(Canvas canvas) {
            StickerView.this.stickerDraw(canvas);
        }
    }

    /* renamed from: org.telegram.ui.Components.Paint.Views.StickerView$StickerViewSelectionView */
    public class StickerViewSelectionView extends SelectionView {
        private Paint arcPaint = new Paint(1);
        private RectF arcRect = new RectF();

        public StickerViewSelectionView(Context context) {
            super(context);
            this.arcPaint.setColor(-1);
            this.arcPaint.setStrokeWidth((float) AndroidUtilities.m9dp(1.0f));
            this.arcPaint.setStyle(Style.STROKE);
        }

        protected int pointInsideHandle(float x, float y) {
            float radius = (float) AndroidUtilities.m9dp(19.5f);
            float inset = radius + ((float) AndroidUtilities.m9dp(1.0f));
            float middle = inset + ((((float) getHeight()) - (inset * 2.0f)) / 2.0f);
            if (x > inset - radius && y > middle - radius && x < inset + radius && y < middle + radius) {
                return 1;
            }
            if (x > ((((float) getWidth()) - (inset * 2.0f)) + inset) - radius && y > middle - radius && x < ((((float) getWidth()) - (inset * 2.0f)) + inset) + radius && y < middle + radius) {
                return 2;
            }
            float selectionRadius = ((float) getWidth()) / 2.0f;
            if (Math.pow((double) (x - selectionRadius), 2.0d) + Math.pow((double) (y - selectionRadius), 2.0d) < Math.pow((double) selectionRadius, 2.0d)) {
                return 3;
            }
            return 0;
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float radius = (float) AndroidUtilities.m9dp(4.5f);
            float inset = (radius + ((float) AndroidUtilities.m9dp(1.0f))) + ((float) AndroidUtilities.m9dp(15.0f));
            float mainRadius = ((float) (getWidth() / 2)) - inset;
            this.arcRect.set(inset, inset, (mainRadius * 2.0f) + inset, (mainRadius * 2.0f) + inset);
            for (int i = 0; i < 48; i++) {
                Canvas canvas2 = canvas;
                canvas2.drawArc(this.arcRect, (4.0f + 4.0f) * ((float) i), 4.0f, false, this.arcPaint);
            }
            canvas.drawCircle(inset, inset + mainRadius, radius, this.dotPaint);
            canvas.drawCircle(inset, inset + mainRadius, radius, this.dotStrokePaint);
            canvas.drawCircle((mainRadius * 2.0f) + inset, inset + mainRadius, radius, this.dotPaint);
            canvas.drawCircle((mainRadius * 2.0f) + inset, inset + mainRadius, radius, this.dotStrokePaint);
        }
    }

    public StickerView(Context context, Point position, Size baseSize, Document sticker, Object parentObject) {
        this(context, position, 0.0f, 1.0f, baseSize, sticker, parentObject);
    }

    public StickerView(Context context, Point position, float angle, float scale, Size baseSize, Document sticker, Object parentObject) {
        super(context, position);
        this.anchor = -1;
        this.mirrored = false;
        this.centerImage = new ImageReceiver();
        setRotation(angle);
        setScale(scale);
        this.sticker = sticker;
        this.baseSize = baseSize;
        this.parentObject = parentObject;
        for (int a = 0; a < sticker.attributes.size(); a++) {
            DocumentAttribute attribute = (DocumentAttribute) sticker.attributes.get(a);
            if (attribute instanceof TL_documentAttributeSticker) {
                if (attribute.mask_coords != null) {
                    this.anchor = attribute.mask_coords.f137n;
                }
                this.containerView = new FrameLayoutDrawer(context);
                addView(this.containerView, LayoutHelper.createFrame(-1, -1.0f));
                this.centerImage.setAspectFit(true);
                this.centerImage.setInvalidateAll(true);
                this.centerImage.setParentView(this.containerView);
                this.centerImage.setImage((TLObject) sticker, null, sticker.thumb.location, null, "webp", parentObject, 1);
                updatePosition();
            }
        }
        this.containerView = new FrameLayoutDrawer(context);
        addView(this.containerView, LayoutHelper.createFrame(-1, -1.0f));
        this.centerImage.setAspectFit(true);
        this.centerImage.setInvalidateAll(true);
        this.centerImage.setParentView(this.containerView);
        this.centerImage.setImage((TLObject) sticker, null, sticker.thumb.location, null, "webp", parentObject, 1);
        updatePosition();
    }

    public StickerView(Context context, StickerView stickerView, Point position) {
        this(context, position, stickerView.getRotation(), stickerView.getScale(), stickerView.baseSize, stickerView.sticker, stickerView.parentObject);
        if (stickerView.mirrored) {
            mirror();
        }
    }

    public int getAnchor() {
        return this.anchor;
    }

    public void mirror() {
        this.mirrored = !this.mirrored;
        this.containerView.invalidate();
    }

    protected void updatePosition() {
        float halfHeight = this.baseSize.height / 2.0f;
        setX(this.position.f240x - (this.baseSize.width / 2.0f));
        setY(this.position.f241y - halfHeight);
        updateSelectionView();
    }

    protected void stickerDraw(Canvas canvas) {
        if (this.containerView != null) {
            canvas.save();
            if (this.centerImage.getBitmap() != null) {
                if (this.mirrored) {
                    canvas.scale(-1.0f, 1.0f);
                    canvas.translate(-this.baseSize.width, 0.0f);
                }
                this.centerImage.setImageCoords(0, 0, (int) this.baseSize.width, (int) this.baseSize.height);
                this.centerImage.draw(canvas);
            }
            canvas.restore();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((int) this.baseSize.width, NUM), MeasureSpec.makeMeasureSpec((int) this.baseSize.height, NUM));
    }

    protected Rect getSelectionBounds() {
        float scale = ((ViewGroup) getParent()).getScaleX();
        float side = ((float) getWidth()) * (getScale() + 0.4f);
        return new Rect((this.position.f240x - (side / 2.0f)) * scale, (this.position.f241y - (side / 2.0f)) * scale, side * scale, side * scale);
    }

    protected SelectionView createSelectionView() {
        return new StickerViewSelectionView(getContext());
    }

    public Document getSticker() {
        return this.sticker;
    }
}
