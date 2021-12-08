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
import org.telegram.tgnet.TLRPC;
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
    private TLRPC.Document sticker;

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

    public StickerView(Context context, Point position, Size baseSize2, TLRPC.Document sticker2, Object parentObject2) {
        this(context, position, 0.0f, 1.0f, baseSize2, sticker2, parentObject2);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StickerView(Context context, Point position, float angle, float scale, Size baseSize2, TLRPC.Document sticker2, Object parentObject2) {
        super(context, position);
        TLRPC.Document document = sticker2;
        this.anchor = -1;
        this.mirrored = false;
        this.centerImage = new ImageReceiver();
        setRotation(angle);
        setScale(scale);
        this.sticker = document;
        this.baseSize = baseSize2;
        this.parentObject = parentObject2;
        int a = 0;
        while (true) {
            if (a >= document.attributes.size()) {
                break;
            }
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.mask_coords != null) {
                this.anchor = attribute.mask_coords.n;
            }
        }
        FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(context);
        this.containerView = frameLayoutDrawer;
        addView(frameLayoutDrawer, LayoutHelper.createFrame(-1, -1.0f));
        this.centerImage.setAspectFit(true);
        this.centerImage.setInvalidateAll(true);
        this.centerImage.setParentView(this.containerView);
        this.centerImage.setImage(ImageLocation.getForDocument(sticker2), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document), (String) null, "webp", parentObject2, 1);
        this.centerImage.setDelegate(new StickerView$$ExternalSyntheticLambda0(this));
        updatePosition();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Paint-Views-StickerView  reason: not valid java name */
    public /* synthetic */ void m2434lambda$new$0$orgtelegramuiComponentsPaintViewsStickerView(ImageReceiver imageReceiver, boolean set, boolean isThumb, boolean memCache) {
        RLottieDrawable drawable;
        if (set && !isThumb && (drawable = imageReceiver.getLottieAnimation()) != null) {
            didSetAnimatedSticker(drawable);
        }
    }

    public StickerView(Context context, StickerView stickerView, Point position) {
        this(context, position, stickerView.getRotation(), stickerView.getScale(), stickerView.baseSize, stickerView.sticker, stickerView.parentObject);
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
        setX(this.position.x - (this.baseSize.width / 2.0f));
        setY(this.position.y - (this.baseSize.height / 2.0f));
        updateSelectionView();
    }

    /* access modifiers changed from: protected */
    public void didSetAnimatedSticker(RLottieDrawable drawable) {
    }

    /* access modifiers changed from: protected */
    public void stickerDraw(Canvas canvas) {
        if (this.containerView != null) {
            canvas.save();
            if (this.mirrored) {
                canvas.scale(-1.0f, 1.0f);
                canvas.translate(-this.baseSize.width, 0.0f);
            }
            this.centerImage.setImageCoords(0.0f, 0.0f, (float) ((int) this.baseSize.width), (float) ((int) this.baseSize.height));
            this.centerImage.draw(canvas);
            canvas.restore();
        }
    }

    public long getDuration() {
        RLottieDrawable drawable = this.centerImage.getLottieAnimation();
        if (drawable == null) {
            return 0;
        }
        return drawable.getDuration();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) this.baseSize.width, NUM), View.MeasureSpec.makeMeasureSpec((int) this.baseSize.height, NUM));
    }

    /* access modifiers changed from: protected */
    public Rect getSelectionBounds() {
        float scale = ((ViewGroup) getParent()).getScaleX();
        float side = ((float) getMeasuredWidth()) * (getScale() + 0.4f);
        return new Rect((this.position.x - (side / 2.0f)) * scale, (this.position.y - (side / 2.0f)) * scale, side * scale, side * scale);
    }

    /* access modifiers changed from: protected */
    public EntityView.SelectionView createSelectionView() {
        return new StickerViewSelectionView(getContext());
    }

    public TLRPC.Document getSticker() {
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

        public StickerViewSelectionView(Context context) {
            super(context);
            this.arcPaint.setColor(-1);
            this.arcPaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
            this.arcPaint.setStyle(Paint.Style.STROKE);
        }

        /* access modifiers changed from: protected */
        public int pointInsideHandle(float x, float y) {
            float radius = (float) AndroidUtilities.dp(19.5f);
            float inset = radius + ((float) AndroidUtilities.dp(1.0f));
            float middle = ((((float) getMeasuredHeight()) - (inset * 2.0f)) / 2.0f) + inset;
            if (x > inset - radius && y > middle - radius && x < inset + radius && y < middle + radius) {
                return 1;
            }
            if (x > ((((float) getMeasuredWidth()) - (inset * 2.0f)) + inset) - radius && y > middle - radius && x < (((float) getMeasuredWidth()) - (inset * 2.0f)) + inset + radius && y < middle + radius) {
                return 2;
            }
            float selectionRadius = ((float) getMeasuredWidth()) / 2.0f;
            if (Math.pow((double) (x - selectionRadius), 2.0d) + Math.pow((double) (y - selectionRadius), 2.0d) < Math.pow((double) selectionRadius, 2.0d)) {
                return 3;
            }
            return 0;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float radius = (float) AndroidUtilities.dp(4.5f);
            float inset = radius + ((float) AndroidUtilities.dp(1.0f)) + ((float) AndroidUtilities.dp(15.0f));
            float mainRadius = ((float) (getMeasuredWidth() / 2)) - inset;
            this.arcRect.set(inset, inset, (mainRadius * 2.0f) + inset, (mainRadius * 2.0f) + inset);
            for (int i = 0; i < 48; i++) {
                canvas.drawArc(this.arcRect, (4.0f + 4.0f) * ((float) i), 4.0f, false, this.arcPaint);
            }
            canvas.drawCircle(inset, inset + mainRadius, radius, this.dotPaint);
            canvas.drawCircle(inset, inset + mainRadius, radius, this.dotStrokePaint);
            canvas.drawCircle((mainRadius * 2.0f) + inset, inset + mainRadius, radius, this.dotPaint);
            canvas.drawCircle((2.0f * mainRadius) + inset, inset + mainRadius, radius, this.dotStrokePaint);
        }
    }
}
