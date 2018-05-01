package org.telegram.ui.Components.Paint.Views;

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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Views.EntityView.SelectionView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Rect;
import org.telegram.ui.Components.Size;

public class StickerView extends EntityView {
    private int anchor;
    private Size baseSize;
    private ImageReceiver centerImage;
    private FrameLayoutDrawer containerView;
    private boolean mirrored;
    private Document sticker;

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(null);
        }

        protected void onDraw(Canvas canvas) {
            StickerView.this.stickerDraw(canvas);
        }
    }

    public class StickerViewSelectionView extends SelectionView {
        private Paint arcPaint = new Paint(1);
        private RectF arcRect = new RectF();

        public StickerViewSelectionView(Context context) {
            super(context);
            this.arcPaint.setColor(-1);
            this.arcPaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
            this.arcPaint.setStyle(Style.STROKE);
        }

        protected int pointInsideHandle(float f, float f2) {
            float dp = (float) AndroidUtilities.dp(19.5f);
            float dp2 = ((float) AndroidUtilities.dp(1.0f)) + dp;
            float f3 = dp2 * 2.0f;
            float height = ((((float) getHeight()) - f3) / 2.0f) + dp2;
            if (f > dp2 - dp && f2 > height - dp && f < dp2 + dp && f2 < height + dp) {
                return 1;
            }
            if (f > ((((float) getWidth()) - f3) + dp2) - dp && f2 > height - dp && f < (dp2 + (((float) getWidth()) - f3)) + dp && f2 < height + dp) {
                return 2;
            }
            dp2 = ((float) getWidth()) / 2.0f;
            return Math.pow((double) (f - dp2), 2.0d) + Math.pow((double) (f2 - dp2), 2.0d) < Math.pow((double) dp2, 2.0d) ? 3 : 0;
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float dp = (float) AndroidUtilities.dp(4.5f);
            float dp2 = (((float) AndroidUtilities.dp(1.0f)) + dp) + ((float) AndroidUtilities.dp(15.0f));
            float width = ((float) (getWidth() / 2)) - dp2;
            float f = (2.0f * width) + dp2;
            this.arcRect.set(dp2, dp2, f, f);
            for (int i = 0; i < 48; i++) {
                canvas.drawArc(this.arcRect, ((float) i) * 8.0f, 4.0f, false, this.arcPaint);
            }
            width += dp2;
            canvas.drawCircle(dp2, width, dp, this.dotPaint);
            canvas.drawCircle(dp2, width, dp, this.dotStrokePaint);
            canvas.drawCircle(f, width, dp, this.dotPaint);
            canvas.drawCircle(f, width, dp, this.dotStrokePaint);
        }
    }

    public StickerView(Context context, Point point, Size size, Document document) {
        this(context, point, 0.0f, 1.0f, size, document);
    }

    public StickerView(Context context, Point point, float f, float f2, Size size, Document document) {
        super(context, point);
        this.anchor = -1;
        int i = 0;
        this.mirrored = false;
        this.centerImage = new ImageReceiver();
        setRotation(f);
        setScale(f2);
        this.sticker = document;
        this.baseSize = size;
        while (i < document.attributes.size()) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if ((documentAttribute instanceof TL_documentAttributeSticker) != null) {
                if (documentAttribute.mask_coords != null) {
                    this.anchor = documentAttribute.mask_coords.f46n;
                }
                this.containerView = new FrameLayoutDrawer(context);
                addView(this.containerView, LayoutHelper.createFrame(-1, -1.0f));
                this.centerImage.setAspectFit(true);
                this.centerImage.setInvalidateAll(true);
                this.centerImage.setParentView(this.containerView);
                this.centerImage.setImage((TLObject) document, null, document.thumb.location, null, "webp", 1);
                updatePosition();
            }
            i++;
        }
        this.containerView = new FrameLayoutDrawer(context);
        addView(this.containerView, LayoutHelper.createFrame(-1, -1.0f));
        this.centerImage.setAspectFit(true);
        this.centerImage.setInvalidateAll(true);
        this.centerImage.setParentView(this.containerView);
        this.centerImage.setImage((TLObject) document, null, document.thumb.location, null, "webp", 1);
        updatePosition();
    }

    public StickerView(Context context, StickerView stickerView, Point point) {
        this(context, point, stickerView.getRotation(), stickerView.getScale(), stickerView.baseSize, stickerView.sticker);
        if (stickerView.mirrored != null) {
            mirror();
        }
    }

    public int getAnchor() {
        return this.anchor;
    }

    public void mirror() {
        this.mirrored ^= 1;
        this.containerView.invalidate();
    }

    protected void updatePosition() {
        float f = this.baseSize.height / 2.0f;
        setX(this.position.f24x - (this.baseSize.width / 2.0f));
        setY(this.position.f25y - f);
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

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((int) this.baseSize.width, NUM), MeasureSpec.makeMeasureSpec((int) this.baseSize.height, NUM));
    }

    protected Rect getSelectionBounds() {
        float scaleX = ((ViewGroup) getParent()).getScaleX();
        float width = ((float) getWidth()) * (getScale() + 0.4f);
        float f = width / 2.0f;
        width *= scaleX;
        return new Rect((this.position.f24x - f) * scaleX, (this.position.f25y - f) * scaleX, width, width);
    }

    protected SelectionView createSelectionView() {
        return new StickerViewSelectionView(getContext());
    }

    public Document getSticker() {
        return this.sticker;
    }
}
