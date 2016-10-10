package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_maskCoords;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Rect;
import org.telegram.ui.Components.Size;

public class StickerView
  extends EntityView
{
  private int anchor = -1;
  private Size baseSize;
  private ImageReceiver centerImage = new ImageReceiver();
  private FrameLayoutDrawer containerView;
  private boolean mirrored = false;
  private TLRPC.Document sticker;
  
  public StickerView(Context paramContext, StickerView paramStickerView, Point paramPoint)
  {
    this(paramContext, paramPoint, paramStickerView.getRotation(), paramStickerView.getScale(), paramStickerView.baseSize, paramStickerView.sticker);
    if (paramStickerView.mirrored) {
      mirror();
    }
  }
  
  public StickerView(Context paramContext, Point paramPoint, float paramFloat1, float paramFloat2, Size paramSize, TLRPC.Document paramDocument)
  {
    super(paramContext, paramPoint);
    setRotation(paramFloat1);
    setScale(paramFloat2);
    this.sticker = paramDocument;
    this.baseSize = paramSize;
    int i = 0;
    for (;;)
    {
      if (i < paramDocument.attributes.size())
      {
        paramPoint = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if (!(paramPoint instanceof TLRPC.TL_documentAttributeSticker)) {
          break label186;
        }
        if (paramPoint.mask_coords != null) {
          this.anchor = paramPoint.mask_coords.n;
        }
      }
      this.containerView = new FrameLayoutDrawer(paramContext);
      addView(this.containerView, LayoutHelper.createFrame(-1, -1.0F));
      this.centerImage.setAspectFit(true);
      this.centerImage.setInvalidateAll(true);
      this.centerImage.setParentView(this.containerView);
      this.centerImage.setImage(paramDocument, null, paramDocument.thumb.location, null, "webp", true);
      updatePosition();
      return;
      label186:
      i += 1;
    }
  }
  
  public StickerView(Context paramContext, Point paramPoint, Size paramSize, TLRPC.Document paramDocument)
  {
    this(paramContext, paramPoint, 0.0F, 1.0F, paramSize, paramDocument);
  }
  
  protected EntityView.SelectionView createSelectionView()
  {
    return new StickerViewSelectionView(getContext());
  }
  
  public int getAnchor()
  {
    return this.anchor;
  }
  
  protected Rect getSelectionBounds()
  {
    float f1 = ((ViewGroup)getParent()).getScaleX();
    float f2 = getWidth() * (getScale() + 0.4F);
    return new Rect((this.position.x - f2 / 2.0F) * f1, (this.position.y - f2 / 2.0F) * f1, f2 * f1, f2 * f1);
  }
  
  public TLRPC.Document getSticker()
  {
    return this.sticker;
  }
  
  public void mirror()
  {
    if (!this.mirrored) {}
    for (boolean bool = true;; bool = false)
    {
      this.mirrored = bool;
      this.containerView.invalidate();
      return;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec((int)this.baseSize.width, 1073741824), View.MeasureSpec.makeMeasureSpec((int)this.baseSize.height, 1073741824));
  }
  
  protected void stickerDraw(Canvas paramCanvas)
  {
    if (this.containerView == null) {
      return;
    }
    paramCanvas.save();
    if (this.centerImage.getBitmap() != null)
    {
      if (this.mirrored)
      {
        paramCanvas.scale(-1.0F, 1.0F);
        paramCanvas.translate(-this.baseSize.width, 0.0F);
      }
      this.centerImage.setImageCoords(0, 0, (int)this.baseSize.width, (int)this.baseSize.height);
      this.centerImage.draw(paramCanvas);
    }
    paramCanvas.restore();
  }
  
  protected void updatePosition()
  {
    float f1 = this.baseSize.width / 2.0F;
    float f2 = this.baseSize.height / 2.0F;
    setX(this.position.x - f1);
    setY(this.position.y - f2);
    updateSelectionView();
  }
  
  private class FrameLayoutDrawer
    extends FrameLayout
  {
    public FrameLayoutDrawer(Context paramContext)
    {
      super();
      setWillNotDraw(false);
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      StickerView.this.stickerDraw(paramCanvas);
    }
  }
  
  public class StickerViewSelectionView
    extends EntityView.SelectionView
  {
    private Paint arcPaint = new Paint(1);
    private RectF arcRect = new RectF();
    
    public StickerViewSelectionView(Context paramContext)
    {
      super(paramContext);
      this.arcPaint.setColor(-1);
      this.arcPaint.setStrokeWidth(AndroidUtilities.dp(1.0F));
      this.arcPaint.setStyle(Paint.Style.STROKE);
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      super.onDraw(paramCanvas);
      float f2 = AndroidUtilities.dp(1.0F);
      float f1 = AndroidUtilities.dp(4.5F);
      f2 = f1 + f2 + AndroidUtilities.dp(15.0F);
      float f3 = getWidth() / 2 - f2;
      this.arcRect.set(f2, f2, f3 * 2.0F + f2, f3 * 2.0F + f2);
      int i = 0;
      while (i < 48)
      {
        paramCanvas.drawArc(this.arcRect, (4.0F + 4.0F) * i, 4.0F, false, this.arcPaint);
        i += 1;
      }
      paramCanvas.drawCircle(f2, f2 + f3, f1, this.dotPaint);
      paramCanvas.drawCircle(f2, f2 + f3, f1, this.dotStrokePaint);
      paramCanvas.drawCircle(f3 * 2.0F + f2, f2 + f3, f1, this.dotPaint);
      paramCanvas.drawCircle(f3 * 2.0F + f2, f2 + f3, f1, this.dotStrokePaint);
    }
    
    protected int pointInsideHandle(float paramFloat1, float paramFloat2)
    {
      float f2 = AndroidUtilities.dp(1.0F);
      float f1 = AndroidUtilities.dp(19.5F);
      f2 = f1 + f2;
      float f3 = f2 + (getHeight() - f2 * 2.0F) / 2.0F;
      if ((paramFloat1 > f2 - f1) && (paramFloat2 > f3 - f1) && (paramFloat1 < f2 + f1) && (paramFloat2 < f3 + f1)) {
        return 1;
      }
      if ((paramFloat1 > getWidth() - f2 * 2.0F + f2 - f1) && (paramFloat2 > f3 - f1) && (paramFloat1 < getWidth() - f2 * 2.0F + f2 + f1) && (paramFloat2 < f3 + f1)) {
        return 2;
      }
      f1 = getWidth() / 2.0F;
      if (Math.pow(paramFloat1 - f1, 2.0D) + Math.pow(paramFloat2 - f1, 2.0D) < Math.pow(f1, 2.0D)) {
        return 3;
      }
      return 0;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Views/StickerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */