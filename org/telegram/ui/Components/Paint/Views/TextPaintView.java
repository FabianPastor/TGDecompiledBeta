package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Rect;

public class TextPaintView
  extends EntityView
{
  private int baseFontSize;
  private EditTextOutline editText;
  private boolean stroke;
  private Swatch swatch;
  
  public TextPaintView(Context paramContext, TextPaintView paramTextPaintView, Point paramPoint)
  {
    this(paramContext, paramPoint, paramTextPaintView.baseFontSize, paramTextPaintView.getText(), paramTextPaintView.getSwatch(), paramTextPaintView.stroke);
    setRotation(paramTextPaintView.getRotation());
    setScale(paramTextPaintView.getScale());
  }
  
  public TextPaintView(Context paramContext, Point paramPoint, int paramInt, String paramString, Swatch paramSwatch, boolean paramBoolean)
  {
    super(paramContext, paramPoint);
    this.baseFontSize = paramInt;
    this.editText = new EditTextOutline(paramContext);
    this.editText.setBackgroundColor(0);
    this.editText.setPadding(AndroidUtilities.dp(7.0F), AndroidUtilities.dp(7.0F), AndroidUtilities.dp(7.0F), AndroidUtilities.dp(7.0F));
    this.editText.setClickable(false);
    this.editText.setEnabled(false);
    this.editText.setTextSize(0, this.baseFontSize);
    this.editText.setText(paramString);
    this.editText.setTextColor(paramSwatch.color);
    this.editText.setTypeface(null, 1);
    this.editText.setGravity(17);
    this.editText.setHorizontallyScrolling(false);
    this.editText.setImeOptions(268435456);
    this.editText.setFocusableInTouchMode(true);
    this.editText.setInputType(this.editText.getInputType() | 0x4000);
    addView(this.editText, LayoutHelper.createFrame(-2, -2, 51));
    if (Build.VERSION.SDK_INT >= 23) {
      this.editText.setBreakStrategy(0);
    }
    setSwatch(paramSwatch);
    setStroke(paramBoolean);
    updatePosition();
    this.editText.addTextChangedListener(new TextWatcher()
    {
      private int beforeCursorPosition = 0;
      private String text;
      
      public void afterTextChanged(Editable paramAnonymousEditable)
      {
        TextPaintView.this.editText.removeTextChangedListener(this);
        if (TextPaintView.this.editText.getLineCount() > 9)
        {
          TextPaintView.this.editText.setText(this.text);
          TextPaintView.this.editText.setSelection(this.beforeCursorPosition);
        }
        TextPaintView.this.editText.addTextChangedListener(this);
      }
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        this.text = paramAnonymousCharSequence.toString();
        this.beforeCursorPosition = paramAnonymousInt1;
      }
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    });
  }
  
  private void updateColor()
  {
    if (this.stroke)
    {
      this.editText.setTextColor(-1);
      this.editText.setStrokeColor(this.swatch.color);
      this.editText.setShadowLayer(0.0F, 0.0F, 0.0F, 0);
    }
    for (;;)
    {
      return;
      this.editText.setTextColor(this.swatch.color);
      this.editText.setStrokeColor(0);
      this.editText.setShadowLayer(8.0F, 0.0F, 2.0F, -NUM);
    }
  }
  
  public void beginEditing()
  {
    this.editText.setEnabled(true);
    this.editText.setClickable(true);
    this.editText.requestFocus();
    this.editText.setSelection(this.editText.getText().length());
  }
  
  protected TextViewSelectionView createSelectionView()
  {
    return new TextViewSelectionView(getContext());
  }
  
  public void endEditing()
  {
    this.editText.clearFocus();
    this.editText.setEnabled(false);
    this.editText.setClickable(false);
    updateSelectionView();
  }
  
  public View getFocusedView()
  {
    return this.editText;
  }
  
  protected Rect getSelectionBounds()
  {
    float f1 = ((ViewGroup)getParent()).getScaleX();
    float f2 = getWidth() * getScale() + AndroidUtilities.dp(46.0F) / f1;
    float f3 = getHeight() * getScale() + AndroidUtilities.dp(20.0F) / f1;
    return new Rect((this.position.x - f2 / 2.0F) * f1, (this.position.y - f3 / 2.0F) * f1, f2 * f1, f3 * f1);
  }
  
  public Swatch getSwatch()
  {
    return this.swatch;
  }
  
  public String getText()
  {
    return this.editText.getText().toString();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    updatePosition();
  }
  
  public void setMaxWidth(int paramInt)
  {
    this.editText.setMaxWidth(paramInt);
  }
  
  public void setStroke(boolean paramBoolean)
  {
    this.stroke = paramBoolean;
    updateColor();
  }
  
  public void setSwatch(Swatch paramSwatch)
  {
    this.swatch = paramSwatch;
    updateColor();
  }
  
  public void setText(String paramString)
  {
    this.editText.setText(paramString);
  }
  
  public class TextViewSelectionView
    extends EntityView.SelectionView
  {
    public TextViewSelectionView(Context paramContext)
    {
      super(paramContext);
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      super.onDraw(paramCanvas);
      float f1 = AndroidUtilities.dp(3.0F);
      float f2 = AndroidUtilities.dp(3.0F);
      float f3 = AndroidUtilities.dp(1.0F);
      float f4 = AndroidUtilities.dp(4.5F);
      float f5 = f4 + f3 + AndroidUtilities.dp(15.0F);
      float f6 = getWidth() - 2.0F * f5;
      float f7 = getHeight() - 2.0F * f5;
      int i = (int)Math.floor(f6 / (f1 + f2));
      float f8 = (float)Math.ceil((f6 - i * (f1 + f2) + f1) / 2.0F);
      float f9;
      for (int j = 0; j < i; j++)
      {
        f9 = f8 + f5 + j * (f2 + f1);
        paramCanvas.drawRect(f9, f5 - f3 / 2.0F, f9 + f2, f5 + f3 / 2.0F, this.paint);
        paramCanvas.drawRect(f9, f5 + f7 - f3 / 2.0F, f9 + f2, f5 + f7 + f3 / 2.0F, this.paint);
      }
      i = (int)Math.floor(f7 / (f1 + f2));
      f8 = (float)Math.ceil((f7 - i * (f1 + f2) + f1) / 2.0F);
      for (j = 0; j < i; j++)
      {
        f9 = f8 + f5 + j * (f2 + f1);
        paramCanvas.drawRect(f5 - f3 / 2.0F, f9, f5 + f3 / 2.0F, f9 + f2, this.paint);
        paramCanvas.drawRect(f5 + f6 - f3 / 2.0F, f9, f5 + f6 + f3 / 2.0F, f9 + f2, this.paint);
      }
      paramCanvas.drawCircle(f5, f7 / 2.0F + f5, f4, this.dotPaint);
      paramCanvas.drawCircle(f5, f7 / 2.0F + f5, f4, this.dotStrokePaint);
      paramCanvas.drawCircle(f5 + f6, f7 / 2.0F + f5, f4, this.dotPaint);
      paramCanvas.drawCircle(f5 + f6, f7 / 2.0F + f5, f4, this.dotStrokePaint);
    }
    
    protected int pointInsideHandle(float paramFloat1, float paramFloat2)
    {
      float f1 = AndroidUtilities.dp(1.0F);
      float f2 = AndroidUtilities.dp(19.5F);
      float f3 = f2 + f1;
      f1 = getWidth() - f3 * 2.0F;
      float f4 = getHeight() - f3 * 2.0F;
      float f5 = f3 + f4 / 2.0F;
      int i;
      if ((paramFloat1 > f3 - f2) && (paramFloat2 > f5 - f2) && (paramFloat1 < f3 + f2) && (paramFloat2 < f5 + f2)) {
        i = 1;
      }
      for (;;)
      {
        return i;
        if ((paramFloat1 > f3 + f1 - f2) && (paramFloat2 > f5 - f2) && (paramFloat1 < f3 + f1 + f2) && (paramFloat2 < f5 + f2)) {
          i = 2;
        } else if ((paramFloat1 > f3) && (paramFloat1 < f1) && (paramFloat2 > f3) && (paramFloat2 < f4)) {
          i = 3;
        } else {
          i = 0;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Views/TextPaintView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */