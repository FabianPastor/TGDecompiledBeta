package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build.VERSION;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoEditToolCell;

@SuppressLint({"NewApi"})
public class PhotoFilterView
  extends FrameLayout
{
  private static final int curveDataStep = 2;
  private static final int curveGranularity = 100;
  private Bitmap bitmapToEdit;
  private float blurAngle = 1.5707964F;
  private PhotoFilterBlurControl blurControl;
  private float blurExcludeBlurSize = 0.15F;
  private Point blurExcludePoint = new Point(0.5F, 0.5F);
  private float blurExcludeSize = 0.35F;
  private FrameLayout blurLayout;
  private TextView blurLinearButton;
  private TextView blurOffButton;
  private TextView blurRadialButton;
  private int blurTool = 11;
  private int blurType = 0;
  private TextView cancelTextView;
  private int contrastTool = 2;
  private float contrastValue = 0.0F;
  private FrameLayout curveLayout;
  private TextView[] curveTextView = new TextView[4];
  private PhotoFilterCurvesControl curvesControl;
  private int curvesTool = 13;
  private CurvesToolValue curvesToolValue = new CurvesToolValue();
  private TextView doneTextView;
  private FrameLayout editView;
  private EGLThread eglThread;
  private int enhanceTool = 0;
  private float enhanceValue = 0.0F;
  private int exposureTool = 1;
  private float exposureValue = 0.0F;
  private int fadeTool = 6;
  private float fadeValue = 0.0F;
  private int grainTool = 10;
  private float grainValue = 0.0F;
  private int highlightsTool = 7;
  private float highlightsValue = 0.0F;
  private TextView infoTextView;
  private int orientation;
  private TextView paramTextView;
  private float previousValue;
  private int previousValueInt;
  private int previousValueInt2;
  private RecyclerListView recyclerListView;
  private int saturationTool = 4;
  private float saturationValue = 0.0F;
  private int selectedTintMode;
  private int selectedTool = -1;
  private int shadowsTool = 8;
  private float shadowsValue = 0.0F;
  private int sharpenTool = 12;
  private float sharpenValue = 0.0F;
  private boolean showOriginal;
  private TextureView textureView;
  private LinearLayout tintButtonsContainer;
  private final int[] tintHighlighsColors = { 0, -1076602, -1388894, -859780, -5968466, -7742235, -13726776, -3303195 };
  private TextView tintHighlightsButton;
  private int tintHighlightsColor = 0;
  private FrameLayout tintLayout;
  private final int[] tintShadowColors = { 0, -45747, -753630, 52480, -8269183, -9321002, -16747844, -10080879 };
  private TextView tintShadowsButton;
  private int tintShadowsColor = 0;
  private int tintTool = 5;
  private ToolsAdapter toolsAdapter;
  private FrameLayout toolsView;
  private PhotoEditorSeekBar valueSeekBar;
  private TextView valueTextView;
  private int vignetteTool = 9;
  private float vignetteValue = 0.0F;
  private int warmthTool = 3;
  private float warmthValue = 0.0F;
  
  public PhotoFilterView(Context paramContext, Bitmap paramBitmap, int paramInt)
  {
    super(paramContext);
    this.bitmapToEdit = paramBitmap;
    this.orientation = paramInt;
    this.textureView = new TextureView(paramContext);
    if ((Build.VERSION.SDK_INT != 14) && (Build.VERSION.SDK_INT == 15)) {}
    addView(this.textureView, LayoutHelper.createFrame(-1, -1, 51));
    this.textureView.setVisibility(4);
    this.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
    {
      public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if ((PhotoFilterView.this.eglThread == null) && (paramAnonymousSurfaceTexture != null))
        {
          PhotoFilterView.access$5002(PhotoFilterView.this, new PhotoFilterView.EGLThread(PhotoFilterView.this, paramAnonymousSurfaceTexture, PhotoFilterView.this.bitmapToEdit));
          PhotoFilterView.this.eglThread.setSurfaceTextureSize(paramAnonymousInt1, paramAnonymousInt2);
          PhotoFilterView.this.eglThread.requestRender(true);
        }
      }
      
      public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
      {
        if (PhotoFilterView.this.eglThread != null)
        {
          PhotoFilterView.this.eglThread.shutdown();
          PhotoFilterView.access$5002(PhotoFilterView.this, null);
        }
        return true;
      }
      
      public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (PhotoFilterView.this.eglThread != null)
        {
          PhotoFilterView.this.eglThread.setSurfaceTextureSize(paramAnonymousInt1, paramAnonymousInt2);
          PhotoFilterView.this.eglThread.requestRender(false);
          PhotoFilterView.this.eglThread.postRunnable(new Runnable()
          {
            public void run()
            {
              if (PhotoFilterView.this.eglThread != null) {
                PhotoFilterView.this.eglThread.requestRender(false);
              }
            }
          });
        }
      }
      
      public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture) {}
    });
    this.blurControl = new PhotoFilterBlurControl(paramContext);
    this.blurControl.setVisibility(4);
    addView(this.blurControl, LayoutHelper.createFrame(-1, -1, 51));
    this.blurControl.setDelegate(new PhotoFilterBlurControl.PhotoFilterLinearBlurControlDelegate()
    {
      public void valueChanged(Point paramAnonymousPoint, float paramAnonymousFloat1, float paramAnonymousFloat2, float paramAnonymousFloat3)
      {
        PhotoFilterView.access$1802(PhotoFilterView.this, paramAnonymousFloat2);
        PhotoFilterView.access$2002(PhotoFilterView.this, paramAnonymousPoint);
        PhotoFilterView.access$1902(PhotoFilterView.this, paramAnonymousFloat1);
        PhotoFilterView.access$2102(PhotoFilterView.this, paramAnonymousFloat3);
        if (PhotoFilterView.this.eglThread != null) {
          PhotoFilterView.this.eglThread.requestRender(false);
        }
      }
    });
    this.curvesControl = new PhotoFilterCurvesControl(paramContext, this.curvesToolValue);
    this.curvesControl.setDelegate(new PhotoFilterCurvesControl.PhotoFilterCurvesControlDelegate()
    {
      public void valueChanged()
      {
        if (PhotoFilterView.this.eglThread != null) {
          PhotoFilterView.this.eglThread.requestRender(false);
        }
      }
    });
    this.curvesControl.setVisibility(4);
    addView(this.curvesControl, LayoutHelper.createFrame(-1, -1, 51));
    this.toolsView = new FrameLayout(paramContext);
    addView(this.toolsView, LayoutHelper.createFrame(-1, 126, 83));
    paramBitmap = new FrameLayout(paramContext);
    paramBitmap.setBackgroundColor(-16777216);
    this.toolsView.addView(paramBitmap, LayoutHelper.createFrame(-1, 48, 83));
    this.cancelTextView = new TextView(paramContext);
    this.cancelTextView.setTextSize(1, 14.0F);
    this.cancelTextView.setTextColor(-1);
    this.cancelTextView.setGravity(17);
    this.cancelTextView.setBackgroundDrawable(Theme.createBarSelectorDrawable(-12763843, false));
    this.cancelTextView.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
    this.cancelTextView.setText(LocaleController.getString("Cancel", 2131165386).toUpperCase());
    this.cancelTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    paramBitmap.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
    this.doneTextView = new TextView(paramContext);
    this.doneTextView.setTextSize(1, 14.0F);
    this.doneTextView.setTextColor(-11420173);
    this.doneTextView.setGravity(17);
    this.doneTextView.setBackgroundDrawable(Theme.createBarSelectorDrawable(-12763843, false));
    this.doneTextView.setPadding(AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F), 0);
    this.doneTextView.setText(LocaleController.getString("Done", 2131165590).toUpperCase());
    this.doneTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    paramBitmap.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
    this.recyclerListView = new RecyclerListView(paramContext);
    paramBitmap = new LinearLayoutManager(paramContext);
    paramBitmap.setOrientation(0);
    this.recyclerListView.setLayoutManager(paramBitmap);
    this.recyclerListView.setClipToPadding(false);
    this.recyclerListView.setTag(Integer.valueOf(12));
    this.recyclerListView.setOverScrollMode(2);
    paramBitmap = this.recyclerListView;
    Object localObject = new ToolsAdapter(paramContext);
    this.toolsAdapter = ((ToolsAdapter)localObject);
    paramBitmap.setAdapter((RecyclerView.Adapter)localObject);
    this.toolsView.addView(this.recyclerListView, LayoutHelper.createFrame(-1, 60, 51));
    this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        PhotoFilterView.access$5202(PhotoFilterView.this, paramAnonymousInt);
        if (paramAnonymousInt == PhotoFilterView.this.enhanceTool)
        {
          PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.enhanceValue);
          PhotoFilterView.this.valueSeekBar.setMinMax(0, 100);
          PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Enhance", 2131165618));
        }
        for (;;)
        {
          PhotoFilterView.this.valueSeekBar.setProgress((int)PhotoFilterView.this.previousValue, false);
          PhotoFilterView.this.updateValueTextView();
          PhotoFilterView.this.switchToOrFromEditMode();
          return;
          if (paramAnonymousInt == PhotoFilterView.this.highlightsTool)
          {
            PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.highlightsValue);
            PhotoFilterView.this.valueSeekBar.setMinMax(-100, 100);
            PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Highlights", 2131165730));
          }
          else if (paramAnonymousInt == PhotoFilterView.this.contrastTool)
          {
            PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.contrastValue);
            PhotoFilterView.this.valueSeekBar.setMinMax(-100, 100);
            PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Contrast", 2131165523));
          }
          else if (paramAnonymousInt == PhotoFilterView.this.exposureTool)
          {
            PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.exposureValue);
            PhotoFilterView.this.valueSeekBar.setMinMax(-100, 100);
            PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Exposure", 2131165628));
          }
          else if (paramAnonymousInt == PhotoFilterView.this.warmthTool)
          {
            PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.warmthValue);
            PhotoFilterView.this.valueSeekBar.setMinMax(-100, 100);
            PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Warmth", 2131166388));
          }
          else if (paramAnonymousInt == PhotoFilterView.this.saturationTool)
          {
            PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.saturationValue);
            PhotoFilterView.this.valueSeekBar.setMinMax(-100, 100);
            PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Saturation", 2131166198));
          }
          else if (paramAnonymousInt == PhotoFilterView.this.vignetteTool)
          {
            PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.vignetteValue);
            PhotoFilterView.this.valueSeekBar.setMinMax(0, 100);
            PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Vignette", 2131166386));
          }
          else if (paramAnonymousInt == PhotoFilterView.this.shadowsTool)
          {
            PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.shadowsValue);
            PhotoFilterView.this.valueSeekBar.setMinMax(-100, 100);
            PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Shadows", 2131166272));
          }
          else if (paramAnonymousInt == PhotoFilterView.this.grainTool)
          {
            PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.grainValue);
            PhotoFilterView.this.valueSeekBar.setMinMax(0, 100);
            PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Grain", 2131165717));
          }
          else if (paramAnonymousInt == PhotoFilterView.this.fadeTool)
          {
            PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.fadeValue);
            PhotoFilterView.this.valueSeekBar.setMinMax(0, 100);
            PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Fade", 2131165630));
          }
          else if (paramAnonymousInt == PhotoFilterView.this.sharpenTool)
          {
            PhotoFilterView.access$5402(PhotoFilterView.this, PhotoFilterView.this.sharpenValue);
            PhotoFilterView.this.valueSeekBar.setMinMax(0, 100);
            PhotoFilterView.this.paramTextView.setText(LocaleController.getString("Sharpen", 2131166287));
          }
          else if (paramAnonymousInt == PhotoFilterView.this.blurTool)
          {
            PhotoFilterView.access$7902(PhotoFilterView.this, PhotoFilterView.this.blurType);
          }
          else if (paramAnonymousInt == PhotoFilterView.this.tintTool)
          {
            PhotoFilterView.access$7902(PhotoFilterView.this, PhotoFilterView.this.tintShadowsColor);
            PhotoFilterView.access$8102(PhotoFilterView.this, PhotoFilterView.this.tintHighlightsColor);
          }
          else if (paramAnonymousInt == PhotoFilterView.this.curvesTool)
          {
            PhotoFilterView.this.curvesToolValue.luminanceCurve.saveValues();
            PhotoFilterView.this.curvesToolValue.redCurve.saveValues();
            PhotoFilterView.this.curvesToolValue.greenCurve.saveValues();
            PhotoFilterView.this.curvesToolValue.blueCurve.saveValues();
          }
        }
      }
    });
    this.editView = new FrameLayout(paramContext);
    this.editView.setVisibility(8);
    addView(this.editView, LayoutHelper.createFrame(-1, 126, 83));
    paramBitmap = new FrameLayout(paramContext);
    paramBitmap.setBackgroundColor(-15066598);
    this.editView.addView(paramBitmap, LayoutHelper.createFrame(-1, 48, 83));
    localObject = new ImageView(paramContext);
    ((ImageView)localObject).setImageResource(2130837659);
    ((ImageView)localObject).setBackgroundDrawable(Theme.createBarSelectorDrawable(-12763843, false));
    ((ImageView)localObject).setPadding(AndroidUtilities.dp(22.0F), 0, AndroidUtilities.dp(22.0F), 0);
    paramBitmap.addView((View)localObject, LayoutHelper.createFrame(-2, -1, 51));
    ((ImageView)localObject).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.enhanceTool)
        {
          PhotoFilterView.access$5502(PhotoFilterView.this, PhotoFilterView.this.previousValue);
          if (PhotoFilterView.this.eglThread != null)
          {
            paramAnonymousView = PhotoFilterView.this.eglThread;
            if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.blurTool) {
              break label589;
            }
          }
        }
        label589:
        for (boolean bool = true;; bool = false)
        {
          paramAnonymousView.requestRender(bool);
          PhotoFilterView.this.switchToOrFromEditMode();
          return;
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.highlightsTool)
          {
            PhotoFilterView.access$5902(PhotoFilterView.this, PhotoFilterView.this.previousValue);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.contrastTool)
          {
            PhotoFilterView.access$6102(PhotoFilterView.this, PhotoFilterView.this.previousValue);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.exposureTool)
          {
            PhotoFilterView.access$6302(PhotoFilterView.this, PhotoFilterView.this.previousValue);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.warmthTool)
          {
            PhotoFilterView.access$6502(PhotoFilterView.this, PhotoFilterView.this.previousValue);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.saturationTool)
          {
            PhotoFilterView.access$6702(PhotoFilterView.this, PhotoFilterView.this.previousValue);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.vignetteTool)
          {
            PhotoFilterView.access$6902(PhotoFilterView.this, PhotoFilterView.this.previousValue);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.shadowsTool)
          {
            PhotoFilterView.access$7102(PhotoFilterView.this, PhotoFilterView.this.previousValue);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.grainTool)
          {
            PhotoFilterView.access$7302(PhotoFilterView.this, PhotoFilterView.this.previousValue);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.sharpenTool)
          {
            PhotoFilterView.access$7702(PhotoFilterView.this, PhotoFilterView.this.previousValue);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.fadeTool)
          {
            PhotoFilterView.access$7502(PhotoFilterView.this, PhotoFilterView.this.previousValue);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.blurTool)
          {
            PhotoFilterView.access$1702(PhotoFilterView.this, PhotoFilterView.this.previousValueInt);
            break;
          }
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.tintTool)
          {
            PhotoFilterView.access$1402(PhotoFilterView.this, PhotoFilterView.this.previousValueInt);
            PhotoFilterView.access$1202(PhotoFilterView.this, PhotoFilterView.this.previousValueInt2);
            break;
          }
          if (PhotoFilterView.this.selectedTool != PhotoFilterView.this.curvesTool) {
            break;
          }
          PhotoFilterView.this.curvesToolValue.luminanceCurve.restoreValues();
          PhotoFilterView.this.curvesToolValue.redCurve.restoreValues();
          PhotoFilterView.this.curvesToolValue.greenCurve.restoreValues();
          PhotoFilterView.this.curvesToolValue.blueCurve.restoreValues();
          break;
        }
      }
    });
    localObject = new ImageView(paramContext);
    ((ImageView)localObject).setImageResource(2130837660);
    ((ImageView)localObject).setBackgroundDrawable(Theme.createBarSelectorDrawable(-12763843, false));
    ((ImageView)localObject).setPadding(AndroidUtilities.dp(22.0F), AndroidUtilities.dp(1.0F), AndroidUtilities.dp(22.0F), 0);
    paramBitmap.addView((View)localObject, LayoutHelper.createFrame(-2, -1, 53));
    ((ImageView)localObject).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoFilterView.this.toolsAdapter.notifyDataSetChanged();
        PhotoFilterView.this.switchToOrFromEditMode();
      }
    });
    this.infoTextView = new TextView(paramContext);
    this.infoTextView.setTextSize(1, 20.0F);
    this.infoTextView.setTextColor(-1);
    paramBitmap.addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0F, 1, 0.0F, 9.0F, 0.0F, 0.0F));
    this.paramTextView = new TextView(paramContext);
    this.paramTextView.setTextSize(1, 12.0F);
    this.paramTextView.setTextColor(-8355712);
    paramBitmap.addView(this.paramTextView, LayoutHelper.createFrame(-2, -2.0F, 1, 0.0F, 26.0F, 0.0F, 0.0F));
    this.valueTextView = new TextView(paramContext);
    this.valueTextView.setTextSize(1, 20.0F);
    this.valueTextView.setTextColor(-1);
    paramBitmap.addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0F, 1, 0.0F, 3.0F, 0.0F, 0.0F));
    this.valueSeekBar = new PhotoEditorSeekBar(paramContext);
    this.valueSeekBar.setDelegate(new PhotoEditorSeekBar.PhotoEditorSeekBarDelegate()
    {
      public void onProgressChanged()
      {
        int i = PhotoFilterView.this.valueSeekBar.getProgress();
        if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.enhanceTool) {
          PhotoFilterView.access$5502(PhotoFilterView.this, i);
        }
        for (;;)
        {
          PhotoFilterView.this.updateValueTextView();
          if (PhotoFilterView.this.eglThread != null) {
            PhotoFilterView.this.eglThread.requestRender(true);
          }
          return;
          if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.highlightsTool) {
            PhotoFilterView.access$5902(PhotoFilterView.this, i);
          } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.contrastTool) {
            PhotoFilterView.access$6102(PhotoFilterView.this, i);
          } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.exposureTool) {
            PhotoFilterView.access$6302(PhotoFilterView.this, i);
          } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.warmthTool) {
            PhotoFilterView.access$6502(PhotoFilterView.this, i);
          } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.saturationTool) {
            PhotoFilterView.access$6702(PhotoFilterView.this, i);
          } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.vignetteTool) {
            PhotoFilterView.access$6902(PhotoFilterView.this, i);
          } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.shadowsTool) {
            PhotoFilterView.access$7102(PhotoFilterView.this, i);
          } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.grainTool) {
            PhotoFilterView.access$7302(PhotoFilterView.this, i);
          } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.sharpenTool) {
            PhotoFilterView.access$7702(PhotoFilterView.this, i);
          } else if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.fadeTool) {
            PhotoFilterView.access$7502(PhotoFilterView.this, i);
          }
        }
      }
    });
    paramBitmap = this.editView;
    localObject = this.valueSeekBar;
    int i;
    if (AndroidUtilities.isTablet())
    {
      paramInt = 498;
      if (!AndroidUtilities.isTablet()) {
        break label1612;
      }
      i = 49;
      label1367:
      paramBitmap.addView((View)localObject, LayoutHelper.createFrame(paramInt, 60.0F, i, 14.0F, 10.0F, 14.0F, 0.0F));
      this.curveLayout = new FrameLayout(paramContext);
      this.editView.addView(this.curveLayout, LayoutHelper.createFrame(-1, 78, 1));
      paramBitmap = new LinearLayout(paramContext);
      paramBitmap.setOrientation(0);
      this.curveLayout.addView(paramBitmap, LayoutHelper.createFrame(-2, 28, 1));
      paramInt = 0;
      label1454:
      if (paramInt >= 4) {
        break label1714;
      }
      this.curveTextView[paramInt] = new TextView(paramContext);
      this.curveTextView[paramInt].setTextSize(1, 14.0F);
      this.curveTextView[paramInt].setGravity(16);
      this.curveTextView[paramInt].setTag(Integer.valueOf(paramInt));
      if (paramInt != 0) {
        break label1619;
      }
      this.curveTextView[paramInt].setText(LocaleController.getString("CurvesAll", 2131165540).toUpperCase());
      label1535:
      this.curveTextView[paramInt].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.curveTextView[paramInt];
      if (paramInt != 0) {
        break label1706;
      }
    }
    label1612:
    label1619:
    label1706:
    for (float f = 0.0F;; f = 30.0F)
    {
      paramBitmap.addView((View)localObject, LayoutHelper.createLinear(-2, 28, f, 0.0F, 0.0F, 0.0F));
      this.curveTextView[paramInt].setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          int k = ((Integer)paramAnonymousView.getTag()).intValue();
          int i = 0;
          if (i < 4)
          {
            paramAnonymousView = PhotoFilterView.this.curveTextView[i];
            if (i == k) {}
            for (int j = -1;; j = -8355712)
            {
              paramAnonymousView.setTextColor(j);
              i += 1;
              break;
            }
          }
          PhotoFilterView.this.curvesToolValue.activeType = k;
          PhotoFilterView.this.curvesControl.invalidate();
        }
      });
      paramInt += 1;
      break label1454;
      paramInt = -1;
      break;
      i = 51;
      break label1367;
      if (paramInt == 1)
      {
        this.curveTextView[paramInt].setText(LocaleController.getString("CurvesRed", 2131165543).toUpperCase());
        break label1535;
      }
      if (paramInt == 2)
      {
        this.curveTextView[paramInt].setText(LocaleController.getString("CurvesGreen", 2131165542).toUpperCase());
        break label1535;
      }
      if (paramInt != 3) {
        break label1535;
      }
      this.curveTextView[paramInt].setText(LocaleController.getString("CurvesBlue", 2131165541).toUpperCase());
      break label1535;
    }
    label1714:
    this.tintLayout = new FrameLayout(paramContext);
    this.editView.addView(this.tintLayout, LayoutHelper.createFrame(-1, 78, 1));
    paramBitmap = new LinearLayout(paramContext);
    paramBitmap.setOrientation(0);
    this.tintLayout.addView(paramBitmap, LayoutHelper.createFrame(-2, 28, 1));
    this.tintShadowsButton = new TextView(paramContext);
    this.tintShadowsButton.setTextSize(1, 14.0F);
    this.tintShadowsButton.setGravity(16);
    this.tintShadowsButton.setText(LocaleController.getString("TintShadows", 2131166342).toUpperCase());
    this.tintShadowsButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    paramBitmap.addView(this.tintShadowsButton, LayoutHelper.createLinear(-2, 28));
    this.tintShadowsButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoFilterView.access$8702(PhotoFilterView.this, 0);
        PhotoFilterView.this.updateSelectedTintButton(true);
      }
    });
    this.tintHighlightsButton = new TextView(paramContext);
    this.tintHighlightsButton.setTextSize(1, 14.0F);
    this.tintHighlightsButton.setGravity(16);
    this.tintHighlightsButton.setText(LocaleController.getString("TintHighlights", 2131166341).toUpperCase());
    this.tintHighlightsButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    paramBitmap.addView(this.tintHighlightsButton, LayoutHelper.createLinear(-2, 28, 100.0F, 0.0F, 0.0F, 0.0F));
    this.tintHighlightsButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoFilterView.access$8702(PhotoFilterView.this, 1);
        PhotoFilterView.this.updateSelectedTintButton(true);
      }
    });
    this.tintButtonsContainer = new LinearLayout(paramContext);
    this.tintButtonsContainer.setOrientation(0);
    this.tintButtonsContainer.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
    this.tintLayout.addView(this.tintButtonsContainer, LayoutHelper.createFrame(-1, 50.0F, 51, 0.0F, 24.0F, 0.0F, 0.0F));
    paramInt = 0;
    while (paramInt < this.tintShadowColors.length)
    {
      paramBitmap = new RadioButton(paramContext);
      paramBitmap.setSize(AndroidUtilities.dp(20.0F));
      paramBitmap.setTag(Integer.valueOf(paramInt));
      this.tintButtonsContainer.addView(paramBitmap, LayoutHelper.createLinear(0, -1, 1.0F / this.tintShadowColors.length));
      paramBitmap.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = (RadioButton)paramAnonymousView;
          if (PhotoFilterView.this.selectedTintMode == 0) {
            PhotoFilterView.access$1402(PhotoFilterView.this, PhotoFilterView.this.tintShadowColors[((Integer)paramAnonymousView.getTag()).intValue()]);
          }
          for (;;)
          {
            PhotoFilterView.this.updateSelectedTintButton(true);
            if (PhotoFilterView.this.eglThread != null) {
              PhotoFilterView.this.eglThread.requestRender(false);
            }
            return;
            PhotoFilterView.access$1202(PhotoFilterView.this, PhotoFilterView.this.tintHighlighsColors[((Integer)paramAnonymousView.getTag()).intValue()]);
          }
        }
      });
      paramInt += 1;
    }
    this.blurLayout = new FrameLayout(paramContext);
    this.editView.addView(this.blurLayout, LayoutHelper.createFrame(280, 60.0F, 1, 0.0F, 10.0F, 0.0F, 0.0F));
    this.blurOffButton = new TextView(paramContext);
    this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837555, 0, 0);
    this.blurOffButton.setCompoundDrawablePadding(AndroidUtilities.dp(2.0F));
    this.blurOffButton.setTextSize(1, 13.0F);
    this.blurOffButton.setTextColor(-11420173);
    this.blurOffButton.setGravity(1);
    this.blurOffButton.setText(LocaleController.getString("BlurOff", 2131165363));
    this.blurLayout.addView(this.blurOffButton, LayoutHelper.createFrame(80, 60.0F));
    this.blurOffButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoFilterView.access$1702(PhotoFilterView.this, 0);
        PhotoFilterView.this.updateSelectedBlurType();
        PhotoFilterView.this.blurControl.setVisibility(4);
        if (PhotoFilterView.this.eglThread != null) {
          PhotoFilterView.this.eglThread.requestRender(false);
        }
      }
    });
    this.blurRadialButton = new TextView(paramContext);
    this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837556, 0, 0);
    this.blurRadialButton.setCompoundDrawablePadding(AndroidUtilities.dp(2.0F));
    this.blurRadialButton.setTextSize(1, 13.0F);
    this.blurRadialButton.setTextColor(-1);
    this.blurRadialButton.setGravity(1);
    this.blurRadialButton.setText(LocaleController.getString("BlurRadial", 2131165364));
    this.blurLayout.addView(this.blurRadialButton, LayoutHelper.createFrame(80, 80.0F, 51, 100.0F, 0.0F, 0.0F, 0.0F));
    this.blurRadialButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoFilterView.access$1702(PhotoFilterView.this, 1);
        PhotoFilterView.this.updateSelectedBlurType();
        PhotoFilterView.this.blurControl.setVisibility(0);
        PhotoFilterView.this.blurControl.setType(1);
        if (PhotoFilterView.this.eglThread != null) {
          PhotoFilterView.this.eglThread.requestRender(false);
        }
      }
    });
    this.blurLinearButton = new TextView(paramContext);
    this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837552, 0, 0);
    this.blurLinearButton.setCompoundDrawablePadding(AndroidUtilities.dp(2.0F));
    this.blurLinearButton.setTextSize(1, 13.0F);
    this.blurLinearButton.setTextColor(-1);
    this.blurLinearButton.setGravity(1);
    this.blurLinearButton.setText(LocaleController.getString("BlurLinear", 2131165362));
    this.blurLayout.addView(this.blurLinearButton, LayoutHelper.createFrame(80, 80.0F, 51, 200.0F, 0.0F, 0.0F, 0.0F));
    this.blurLinearButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PhotoFilterView.access$1702(PhotoFilterView.this, 2);
        PhotoFilterView.this.updateSelectedBlurType();
        PhotoFilterView.this.blurControl.setVisibility(0);
        PhotoFilterView.this.blurControl.setType(0);
        if (PhotoFilterView.this.eglThread != null) {
          PhotoFilterView.this.eglThread.requestRender(false);
        }
      }
    });
  }
  
  private void checkEnhance()
  {
    if (this.enhanceValue == 0.0F)
    {
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.setDuration(200L);
      localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofInt(this.valueSeekBar, "progress", new int[] { 50 }) });
      localAnimatorSet.start();
    }
  }
  
  private void fixLayout(int paramInt1, int paramInt2)
  {
    if (this.bitmapToEdit == null) {}
    float f2;
    float f1;
    float f3;
    label109:
    do
    {
      return;
      paramInt1 -= AndroidUtilities.dp(28.0F);
      paramInt2 -= AndroidUtilities.dp(154.0F);
      if ((this.orientation % 360 != 90) && (this.orientation % 360 != 270)) {
        break;
      }
      f2 = this.bitmapToEdit.getHeight();
      f1 = this.bitmapToEdit.getWidth();
      f3 = paramInt1 / f2;
      float f4 = paramInt2 / f1;
      if (f3 <= f4) {
        break label395;
      }
      f1 = paramInt2;
      f2 = (int)Math.ceil(f2 * f4);
      int i = (int)Math.ceil((paramInt1 - f2) / 2.0F + AndroidUtilities.dp(14.0F));
      int j = (int)Math.ceil((paramInt2 - f1) / 2.0F + AndroidUtilities.dp(14.0F));
      localLayoutParams = (FrameLayout.LayoutParams)this.textureView.getLayoutParams();
      localLayoutParams.leftMargin = i;
      localLayoutParams.topMargin = j;
      localLayoutParams.width = ((int)f2);
      localLayoutParams.height = ((int)f1);
      this.textureView.setLayoutParams(localLayoutParams);
      this.curvesControl.setActualArea(i, j, localLayoutParams.width, localLayoutParams.height);
      this.blurControl.setActualAreaSize(localLayoutParams.width, localLayoutParams.height);
      localLayoutParams = (FrameLayout.LayoutParams)this.blurControl.getLayoutParams();
      localLayoutParams.height = (AndroidUtilities.dp(28.0F) + paramInt2);
      this.blurControl.setLayoutParams(localLayoutParams);
      localLayoutParams = (FrameLayout.LayoutParams)this.curvesControl.getLayoutParams();
      localLayoutParams.height = (AndroidUtilities.dp(28.0F) + paramInt2);
      this.curvesControl.setLayoutParams(localLayoutParams);
    } while (!AndroidUtilities.isTablet());
    paramInt2 = AndroidUtilities.dp(86.0F) * 10;
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.recyclerListView.getLayoutParams();
    if (paramInt2 < paramInt1) {
      localLayoutParams.width = paramInt2;
    }
    for (localLayoutParams.leftMargin = ((paramInt1 - paramInt2) / 2);; localLayoutParams.leftMargin = 0)
    {
      this.recyclerListView.setLayoutParams(localLayoutParams);
      return;
      f2 = this.bitmapToEdit.getWidth();
      f1 = this.bitmapToEdit.getHeight();
      break;
      label395:
      f2 = paramInt1;
      f1 = (int)Math.ceil(f1 * f3);
      break label109;
      localLayoutParams.width = -1;
    }
  }
  
  private float getContrastValue()
  {
    return this.contrastValue / 100.0F * 0.3F + 1.0F;
  }
  
  private float getEnhanceValue()
  {
    return this.enhanceValue / 100.0F;
  }
  
  private float getExposureValue()
  {
    return this.exposureValue / 100.0F;
  }
  
  private float getFadeValue()
  {
    return this.fadeValue / 100.0F;
  }
  
  private float getGrainValue()
  {
    return this.grainValue / 100.0F * 0.04F;
  }
  
  private float getHighlightsValue()
  {
    return (this.highlightsValue * 0.75F + 100.0F) / 100.0F;
  }
  
  private float getSaturationValue()
  {
    float f2 = this.saturationValue / 100.0F;
    float f1 = f2;
    if (f2 > 0.0F) {
      f1 = f2 * 1.05F;
    }
    return 1.0F + f1;
  }
  
  private float getShadowsValue()
  {
    return (this.shadowsValue * 0.55F + 100.0F) / 100.0F;
  }
  
  private float getSharpenValue()
  {
    return 0.11F + this.sharpenValue / 100.0F * 0.6F;
  }
  
  private float getTintHighlightsIntensityValue()
  {
    if (this.tintHighlightsColor == 0) {
      return 0.0F;
    }
    return 50.0F / 100.0F;
  }
  
  private float getTintShadowsIntensityValue()
  {
    if (this.tintShadowsColor == 0) {
      return 0.0F;
    }
    return 50.0F / 100.0F;
  }
  
  private float getVignetteValue()
  {
    return this.vignetteValue / 100.0F;
  }
  
  private float getWarmthValue()
  {
    return this.warmthValue / 100.0F;
  }
  
  private void setShowOriginal(boolean paramBoolean)
  {
    if (this.showOriginal == paramBoolean) {}
    do
    {
      return;
      this.showOriginal = paramBoolean;
    } while (this.eglThread == null);
    this.eglThread.requestRender(false);
  }
  
  private void updateSelectedBlurType()
  {
    if (this.blurType == 0)
    {
      this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837555, 0, 0);
      this.blurOffButton.setTextColor(-11420173);
      this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837556, 0, 0);
      this.blurRadialButton.setTextColor(-1);
      this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837552, 0, 0);
      this.blurLinearButton.setTextColor(-1);
    }
    do
    {
      return;
      if (this.blurType == 1)
      {
        this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837554, 0, 0);
        this.blurOffButton.setTextColor(-1);
        this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837557, 0, 0);
        this.blurRadialButton.setTextColor(-11420173);
        this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837552, 0, 0);
        this.blurLinearButton.setTextColor(-1);
        return;
      }
    } while (this.blurType != 2);
    this.blurOffButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837554, 0, 0);
    this.blurOffButton.setTextColor(-1);
    this.blurRadialButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837556, 0, 0);
    this.blurRadialButton.setTextColor(-1);
    this.blurLinearButton.setCompoundDrawablesWithIntrinsicBounds(0, 2130837553, 0, 0);
    this.blurLinearButton.setTextColor(-11420173);
  }
  
  private void updateSelectedTintButton(boolean paramBoolean)
  {
    int j = -8355712;
    Object localObject = this.tintHighlightsButton;
    int i;
    label42:
    int k;
    label60:
    int n;
    label118:
    label133:
    boolean bool;
    if (this.selectedTintMode == 1)
    {
      i = -1;
      ((TextView)localObject).setTextColor(i);
      localObject = this.tintShadowsButton;
      if (this.selectedTintMode != 1) {
        break label186;
      }
      i = j;
      ((TextView)localObject).setTextColor(i);
      int m = this.tintButtonsContainer.getChildCount();
      k = 0;
      if (k >= m) {
        return;
      }
      localObject = this.tintButtonsContainer.getChildAt(k);
      if ((localObject instanceof RadioButton))
      {
        localObject = (RadioButton)localObject;
        n = ((Integer)((RadioButton)localObject).getTag()).intValue();
        if (this.selectedTintMode != 0) {
          break label191;
        }
        i = this.tintShadowsColor;
        if (this.selectedTintMode != 0) {
          break label199;
        }
        j = this.tintShadowColors[n];
        if (i != j) {
          break label210;
        }
        bool = true;
        label141:
        ((RadioButton)localObject).setChecked(bool, paramBoolean);
        if (n != 0) {
          break label216;
        }
        i = -1;
        label156:
        if (n != 0) {
          break label245;
        }
        j = -1;
      }
    }
    for (;;)
    {
      ((RadioButton)localObject).setColor(i, j);
      k += 1;
      break label60;
      i = -8355712;
      break;
      label186:
      i = -1;
      break label42;
      label191:
      i = this.tintHighlightsColor;
      break label118;
      label199:
      j = this.tintHighlighsColors[n];
      break label133;
      label210:
      bool = false;
      break label141;
      label216:
      if (this.selectedTintMode == 0)
      {
        i = this.tintShadowColors[n];
        break label156;
      }
      i = this.tintHighlighsColors[n];
      break label156;
      label245:
      if (this.selectedTintMode == 0) {
        j = this.tintShadowColors[n];
      } else {
        j = this.tintHighlighsColors[n];
      }
    }
  }
  
  private void updateValueTextView()
  {
    int i = 0;
    if (this.selectedTool == this.enhanceTool) {
      i = (int)this.enhanceValue;
    }
    while (i > 0)
    {
      this.valueTextView.setText("+" + i);
      return;
      if (this.selectedTool == this.highlightsTool) {
        i = (int)this.highlightsValue;
      } else if (this.selectedTool == this.contrastTool) {
        i = (int)this.contrastValue;
      } else if (this.selectedTool == this.exposureTool) {
        i = (int)this.exposureValue;
      } else if (this.selectedTool == this.warmthTool) {
        i = (int)this.warmthValue;
      } else if (this.selectedTool == this.saturationTool) {
        i = (int)this.saturationValue;
      } else if (this.selectedTool == this.vignetteTool) {
        i = (int)this.vignetteValue;
      } else if (this.selectedTool == this.shadowsTool) {
        i = (int)this.shadowsValue;
      } else if (this.selectedTool == this.grainTool) {
        i = (int)this.grainValue;
      } else if (this.selectedTool == this.sharpenTool) {
        i = (int)this.sharpenValue;
      } else if (this.selectedTool == this.fadeTool) {
        i = (int)this.fadeValue;
      }
    }
    this.valueTextView.setText("" + i);
  }
  
  public Bitmap getBitmap()
  {
    if (this.eglThread != null) {
      return this.eglThread.getTexture();
    }
    return null;
  }
  
  public TextView getCancelTextView()
  {
    return this.cancelTextView;
  }
  
  public TextView getDoneTextView()
  {
    return this.doneTextView;
  }
  
  public FrameLayout getEditView()
  {
    return this.editView;
  }
  
  public FrameLayout getToolsView()
  {
    return this.toolsView;
  }
  
  public boolean hasChanges()
  {
    return (this.enhanceValue != 0.0F) || (this.contrastValue != 0.0F) || (this.highlightsValue != 0.0F) || (this.exposureValue != 0.0F) || (this.warmthValue != 0.0F) || (this.saturationValue != 0.0F) || (this.vignetteValue != 0.0F) || (this.shadowsValue != 0.0F) || (this.grainValue != 0.0F) || (this.sharpenValue != 0.0F) || (this.fadeValue != 0.0F) || (this.tintHighlightsColor != 0) || (this.tintShadowsColor != 0) || (!this.curvesToolValue.shouldBeSkipped());
  }
  
  public void init()
  {
    this.textureView.setVisibility(0);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    fixLayout(View.MeasureSpec.getSize(paramInt1), View.MeasureSpec.getSize(paramInt2));
    super.onMeasure(paramInt1, paramInt2);
  }
  
  public void onTouch(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getActionMasked() == 0) || (paramMotionEvent.getActionMasked() == 5))
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.textureView.getLayoutParams();
      if ((localLayoutParams != null) && (paramMotionEvent.getX() >= localLayoutParams.leftMargin) && (paramMotionEvent.getY() >= localLayoutParams.topMargin) && (paramMotionEvent.getX() <= localLayoutParams.leftMargin + localLayoutParams.width) && (paramMotionEvent.getY() <= localLayoutParams.topMargin + localLayoutParams.height)) {
        setShowOriginal(true);
      }
    }
    while ((paramMotionEvent.getActionMasked() != 1) && (paramMotionEvent.getActionMasked() != 6))
    {
      FrameLayout.LayoutParams localLayoutParams;
      return;
    }
    setShowOriginal(false);
  }
  
  public void setEditViewFirst()
  {
    this.selectedTool = 0;
    this.previousValue = this.enhanceValue;
    this.enhanceValue = 50.0F;
    this.valueSeekBar.setMinMax(0, 100);
    this.paramTextView.setText(LocaleController.getString("Enhance", 2131165618));
    this.editView.setVisibility(0);
    this.toolsView.setVisibility(8);
    this.valueSeekBar.setProgress(50, false);
    updateValueTextView();
  }
  
  public void shutdown()
  {
    if (this.eglThread != null)
    {
      this.eglThread.shutdown();
      this.eglThread = null;
    }
    this.textureView.setVisibility(8);
  }
  
  public void switchToOrFromEditMode()
  {
    final FrameLayout localFrameLayout1;
    final FrameLayout localFrameLayout2;
    Object localObject;
    int i;
    if (this.editView.getVisibility() == 8)
    {
      localFrameLayout1 = this.toolsView;
      localFrameLayout2 = this.editView;
      if ((this.selectedTool == this.blurTool) || (this.selectedTool == this.tintTool) || (this.selectedTool == this.curvesTool))
      {
        localObject = this.blurLayout;
        if (this.selectedTool == this.blurTool)
        {
          i = 0;
          ((FrameLayout)localObject).setVisibility(i);
          localObject = this.tintLayout;
          if (this.selectedTool != this.tintTool) {
            break label290;
          }
          i = 0;
          label100:
          ((FrameLayout)localObject).setVisibility(i);
          localObject = this.curveLayout;
          if (this.selectedTool != this.curvesTool) {
            break label295;
          }
          i = 0;
          label125:
          ((FrameLayout)localObject).setVisibility(i);
          if (this.selectedTool != this.blurTool) {
            break label300;
          }
          this.infoTextView.setText(LocaleController.getString("Blur", 2131165361));
          if (this.blurType != 0) {
            this.blurControl.setVisibility(0);
          }
          label173:
          this.infoTextView.setVisibility(0);
          this.valueSeekBar.setVisibility(4);
          this.paramTextView.setVisibility(4);
          this.valueTextView.setVisibility(4);
          updateSelectedBlurType();
        }
      }
    }
    for (;;)
    {
      localObject = new AnimatorSet();
      ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(localFrameLayout1, "translationY", new float[] { 0.0F, AndroidUtilities.dp(126.0F) }) });
      ((AnimatorSet)localObject).addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          localFrameLayout1.setVisibility(8);
          localFrameLayout2.setVisibility(0);
          localFrameLayout2.setTranslationY(AndroidUtilities.dp(126.0F));
          paramAnonymousAnimator = new AnimatorSet();
          paramAnonymousAnimator.playTogether(new Animator[] { ObjectAnimator.ofFloat(localFrameLayout2, "translationY", new float[] { 0.0F }) });
          paramAnonymousAnimator.addListener(new AnimatorListenerAdapterProxy()
          {
            public void onAnimationEnd(Animator paramAnonymous2Animator)
            {
              if (PhotoFilterView.this.selectedTool == PhotoFilterView.this.enhanceTool) {
                PhotoFilterView.this.checkEnhance();
              }
            }
          });
          paramAnonymousAnimator.setDuration(200L);
          paramAnonymousAnimator.start();
        }
      });
      ((AnimatorSet)localObject).setDuration(200L);
      ((AnimatorSet)localObject).start();
      return;
      i = 4;
      break;
      label290:
      i = 4;
      break label100;
      label295:
      i = 4;
      break label125;
      label300:
      if (this.selectedTool == this.curvesTool)
      {
        this.infoTextView.setText(LocaleController.getString("Curves", 2131165539));
        this.curvesControl.setVisibility(0);
        this.curvesToolValue.activeType = 0;
        i = 0;
        label345:
        if (i < 4)
        {
          localObject = this.curveTextView[i];
          if (i != 0) {
            break label377;
          }
        }
        label377:
        for (int j = -1;; j = -8355712)
        {
          ((TextView)localObject).setTextColor(j);
          i += 1;
          break label345;
          break;
        }
      }
      this.selectedTintMode = 0;
      updateSelectedTintButton(false);
      this.infoTextView.setText(LocaleController.getString("Tint", 2131166340));
      break label173;
      this.tintLayout.setVisibility(4);
      this.curveLayout.setVisibility(4);
      this.blurLayout.setVisibility(4);
      this.valueSeekBar.setVisibility(0);
      this.infoTextView.setVisibility(4);
      this.paramTextView.setVisibility(0);
      this.valueTextView.setVisibility(0);
      this.blurControl.setVisibility(4);
      this.curvesControl.setVisibility(4);
      continue;
      this.selectedTool = -1;
      localFrameLayout1 = this.editView;
      localFrameLayout2 = this.toolsView;
      this.blurControl.setVisibility(4);
      this.curvesControl.setVisibility(4);
    }
  }
  
  public static class CurvesToolValue
  {
    public static final int CurvesTypeBlue = 3;
    public static final int CurvesTypeGreen = 2;
    public static final int CurvesTypeLuminance = 0;
    public static final int CurvesTypeRed = 1;
    public int activeType;
    public PhotoFilterView.CurvesValue blueCurve = new PhotoFilterView.CurvesValue();
    public ByteBuffer curveBuffer = null;
    public PhotoFilterView.CurvesValue greenCurve = new PhotoFilterView.CurvesValue();
    public PhotoFilterView.CurvesValue luminanceCurve = new PhotoFilterView.CurvesValue();
    public PhotoFilterView.CurvesValue redCurve = new PhotoFilterView.CurvesValue();
    
    public CurvesToolValue()
    {
      this.curveBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    public void fillBuffer()
    {
      this.curveBuffer.position(0);
      float[] arrayOfFloat1 = this.luminanceCurve.getDataPoints();
      float[] arrayOfFloat2 = this.redCurve.getDataPoints();
      float[] arrayOfFloat3 = this.greenCurve.getDataPoints();
      float[] arrayOfFloat4 = this.blueCurve.getDataPoints();
      int i = 0;
      while (i < 200)
      {
        this.curveBuffer.put((byte)(int)(arrayOfFloat2[i] * 255.0F));
        this.curveBuffer.put((byte)(int)(arrayOfFloat3[i] * 255.0F));
        this.curveBuffer.put((byte)(int)(arrayOfFloat4[i] * 255.0F));
        this.curveBuffer.put((byte)(int)(arrayOfFloat1[i] * 255.0F));
        i += 1;
      }
      this.curveBuffer.position(0);
    }
    
    public boolean shouldBeSkipped()
    {
      return (this.luminanceCurve.isDefault()) && (this.redCurve.isDefault()) && (this.greenCurve.isDefault()) && (this.blueCurve.isDefault());
    }
  }
  
  public static class CurvesValue
  {
    public float blacksLevel = 0.0F;
    public float[] cachedDataPoints;
    public float highlightsLevel = 75.0F;
    public float midtonesLevel = 50.0F;
    public float previousBlacksLevel = 0.0F;
    public float previousHighlightsLevel = 75.0F;
    public float previousMidtonesLevel = 50.0F;
    public float previousShadowsLevel = 25.0F;
    public float previousWhitesLevel = 100.0F;
    public float shadowsLevel = 25.0F;
    public float whitesLevel = 100.0F;
    
    public float[] getDataPoints()
    {
      if (this.cachedDataPoints == null) {
        interpolateCurve();
      }
      return this.cachedDataPoints;
    }
    
    public float[] interpolateCurve()
    {
      float[] arrayOfFloat = new float[14];
      arrayOfFloat[0] = -0.001F;
      arrayOfFloat[1] = (this.blacksLevel / 100.0F);
      arrayOfFloat[2] = 0.0F;
      arrayOfFloat[3] = (this.blacksLevel / 100.0F);
      arrayOfFloat[4] = 0.25F;
      arrayOfFloat[5] = (this.shadowsLevel / 100.0F);
      arrayOfFloat[6] = 0.5F;
      arrayOfFloat[7] = (this.midtonesLevel / 100.0F);
      arrayOfFloat[8] = 0.75F;
      arrayOfFloat[9] = (this.highlightsLevel / 100.0F);
      arrayOfFloat[10] = 1.0F;
      arrayOfFloat[11] = (this.whitesLevel / 100.0F);
      arrayOfFloat[12] = 1.001F;
      arrayOfFloat[13] = (this.whitesLevel / 100.0F);
      ArrayList localArrayList2 = new ArrayList(100);
      ArrayList localArrayList1 = new ArrayList(100);
      localArrayList1.add(Float.valueOf(arrayOfFloat[0]));
      localArrayList1.add(Float.valueOf(arrayOfFloat[1]));
      int i = 1;
      while (i < arrayOfFloat.length / 2 - 2)
      {
        float f1 = arrayOfFloat[((i - 1) * 2)];
        float f2 = arrayOfFloat[((i - 1) * 2 + 1)];
        float f3 = arrayOfFloat[(i * 2)];
        float f4 = arrayOfFloat[(i * 2 + 1)];
        float f5 = arrayOfFloat[((i + 1) * 2)];
        float f6 = arrayOfFloat[((i + 1) * 2 + 1)];
        float f7 = arrayOfFloat[((i + 2) * 2)];
        float f8 = arrayOfFloat[((i + 2) * 2 + 1)];
        int j = 1;
        while (j < 100)
        {
          float f10 = j * 0.01F;
          float f11 = f10 * f10;
          float f12 = f11 * f10;
          float f9 = 0.5F * (2.0F * f3 + (f5 - f1) * f10 + (2.0F * f1 - 5.0F * f3 + 4.0F * f5 - f7) * f11 + (3.0F * f3 - f1 - 3.0F * f5 + f7) * f12);
          f10 = Math.max(0.0F, Math.min(1.0F, 0.5F * (2.0F * f4 + (f6 - f2) * f10 + (2.0F * f2 - 5.0F * f4 + 4.0F * f6 - f8) * f11 + (3.0F * f4 - f2 - 3.0F * f6 + f8) * f12)));
          if (f9 > f1)
          {
            localArrayList1.add(Float.valueOf(f9));
            localArrayList1.add(Float.valueOf(f10));
          }
          if ((j - 1) % 2 == 0) {
            localArrayList2.add(Float.valueOf(f10));
          }
          j += 1;
        }
        localArrayList1.add(Float.valueOf(f5));
        localArrayList1.add(Float.valueOf(f6));
        i += 1;
      }
      localArrayList1.add(Float.valueOf(arrayOfFloat[12]));
      localArrayList1.add(Float.valueOf(arrayOfFloat[13]));
      this.cachedDataPoints = new float[localArrayList2.size()];
      i = 0;
      while (i < this.cachedDataPoints.length)
      {
        this.cachedDataPoints[i] = ((Float)localArrayList2.get(i)).floatValue();
        i += 1;
      }
      arrayOfFloat = new float[localArrayList1.size()];
      i = 0;
      while (i < arrayOfFloat.length)
      {
        arrayOfFloat[i] = ((Float)localArrayList1.get(i)).floatValue();
        i += 1;
      }
      return arrayOfFloat;
    }
    
    public boolean isDefault()
    {
      return (Math.abs(this.blacksLevel - 0.0F) < 1.0E-5D) && (Math.abs(this.shadowsLevel - 25.0F) < 1.0E-5D) && (Math.abs(this.midtonesLevel - 50.0F) < 1.0E-5D) && (Math.abs(this.highlightsLevel - 75.0F) < 1.0E-5D) && (Math.abs(this.whitesLevel - 100.0F) < 1.0E-5D);
    }
    
    public void restoreValues()
    {
      this.blacksLevel = this.previousBlacksLevel;
      this.shadowsLevel = this.previousShadowsLevel;
      this.midtonesLevel = this.previousMidtonesLevel;
      this.highlightsLevel = this.previousHighlightsLevel;
      this.whitesLevel = this.previousWhitesLevel;
      interpolateCurve();
    }
    
    public void saveValues()
    {
      this.previousBlacksLevel = this.blacksLevel;
      this.previousShadowsLevel = this.shadowsLevel;
      this.previousMidtonesLevel = this.midtonesLevel;
      this.previousHighlightsLevel = this.highlightsLevel;
      this.previousWhitesLevel = this.whitesLevel;
    }
  }
  
  public class EGLThread
    extends DispatchQueue
  {
    private static final int PGPhotoEnhanceHistogramBins = 256;
    private static final int PGPhotoEnhanceSegments = 4;
    private static final String blurFragmentShaderCode = "uniform sampler2D sourceImage;varying highp vec2 blurCoordinates[9];void main() {lowp vec4 sum = vec4(0.0);sum += texture2D(sourceImage, blurCoordinates[0]) * 0.133571;sum += texture2D(sourceImage, blurCoordinates[1]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[2]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[3]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[4]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[5]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[6]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[7]) * 0.012595;sum += texture2D(sourceImage, blurCoordinates[8]) * 0.012595;gl_FragColor = sum;}";
    private static final String blurVertexShaderCode = "attribute vec4 position;attribute vec4 inputTexCoord;uniform highp float texelWidthOffset;uniform highp float texelHeightOffset;varying vec2 blurCoordinates[9];void main() {gl_Position = position;vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);blurCoordinates[0] = inputTexCoord.xy;blurCoordinates[1] = inputTexCoord.xy + singleStepOffset * 1.458430;blurCoordinates[2] = inputTexCoord.xy - singleStepOffset * 1.458430;blurCoordinates[3] = inputTexCoord.xy + singleStepOffset * 3.403985;blurCoordinates[4] = inputTexCoord.xy - singleStepOffset * 3.403985;blurCoordinates[5] = inputTexCoord.xy + singleStepOffset * 5.351806;blurCoordinates[6] = inputTexCoord.xy - singleStepOffset * 5.351806;blurCoordinates[7] = inputTexCoord.xy + singleStepOffset * 7.302940;blurCoordinates[8] = inputTexCoord.xy - singleStepOffset * 7.302940;}";
    private static final String enhanceFragmentShaderCode = "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform float intensity;float enhance(float value) {const vec2 offset = vec2(0.001953125, 0.03125);value = value + offset.x;vec2 coord = (clamp(texCoord, 0.125, 1.0 - 0.125001) - 0.125) * 4.0;vec2 frac = fract(coord);coord = floor(coord);float p00 = float(coord.y * 4.0 + coord.x) * 0.0625 + offset.y;float p01 = float(coord.y * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;float p10 = float((coord.y + 1.0) * 4.0 + coord.x) * 0.0625 + offset.y;float p11 = float((coord.y + 1.0) * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;vec3 c00 = texture2D(inputImageTexture2, vec2(value, p00)).rgb;vec3 c01 = texture2D(inputImageTexture2, vec2(value, p01)).rgb;vec3 c10 = texture2D(inputImageTexture2, vec2(value, p10)).rgb;vec3 c11 = texture2D(inputImageTexture2, vec2(value, p11)).rgb;float c1 = ((c00.r - c00.g) / (c00.b - c00.g));float c2 = ((c01.r - c01.g) / (c01.b - c01.g));float c3 = ((c10.r - c10.g) / (c10.b - c10.g));float c4 = ((c11.r - c11.g) / (c11.b - c11.g));float c1_2 = mix(c1, c2, frac.x);float c3_4 = mix(c3, c4, frac.x);return mix(c1_2, c3_4, frac.y);}vec3 hsv_to_rgb(vec3 c) {vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}void main() {vec4 texel = texture2D(sourceImage, texCoord);vec4 hsv = texel;hsv.y = min(1.0, hsv.y * 1.2);hsv.z = min(1.0, enhance(hsv.z) * 1.1);gl_FragColor = vec4(hsv_to_rgb(mix(texel.xyz, hsv.xyz, intensity)), texel.w);}";
    private static final String linearBlurFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float angle;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = abs((texCoordToUse.x - excludePoint.x) * aspectRatio * cos(angle) + (texCoordToUse.y - excludePoint.y) * sin(angle));gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}";
    private static final String radialBlurFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = distance(excludePoint, texCoordToUse);gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}";
    private static final String rgbToHsvFragmentShaderCode = "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}";
    private static final String sharpenFragmentShaderCode = "precision highp float;varying vec2 texCoord;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;uniform sampler2D sourceImage;uniform float sharpen;void main() {vec4 result = texture2D(sourceImage, texCoord);vec3 leftTextureColor = texture2D(sourceImage, leftTexCoord).rgb;vec3 rightTextureColor = texture2D(sourceImage, rightTexCoord).rgb;vec3 topTextureColor = texture2D(sourceImage, topTexCoord).rgb;vec3 bottomTextureColor = texture2D(sourceImage, bottomTexCoord).rgb;result.rgb = result.rgb * (1.0 + 4.0 * sharpen) - (leftTextureColor + rightTextureColor + topTextureColor + bottomTextureColor) * sharpen;gl_FragColor = result;}";
    private static final String sharpenVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;uniform highp float inputWidth;uniform highp float inputHeight;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;void main() {gl_Position = position;texCoord = inputTexCoord;highp vec2 widthStep = vec2(1.0 / inputWidth, 0.0);highp vec2 heightStep = vec2(0.0, 1.0 / inputHeight);leftTexCoord = inputTexCoord - widthStep;rightTexCoord = inputTexCoord + widthStep;topTexCoord = inputTexCoord + heightStep;bottomTexCoord = inputTexCoord - heightStep;}";
    private static final String simpleFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}";
    private static final String simpleVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}";
    private static final String toolsFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform highp float width;uniform highp float height;uniform sampler2D curvesImage;uniform lowp float skipTone;uniform lowp float shadows;const mediump vec3 hsLuminanceWeighting = vec3(0.3, 0.3, 0.3);uniform lowp float highlights;uniform lowp float contrast;uniform lowp float fadeAmount;const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);uniform lowp float saturation;uniform lowp float shadowsTintIntensity;uniform lowp float highlightsTintIntensity;uniform lowp vec3 shadowsTintColor;uniform lowp vec3 highlightsTintColor;uniform lowp float exposure;uniform lowp float warmth;uniform lowp float grain;const lowp float permTexUnit = 1.0 / 256.0;const lowp float permTexUnitHalf = 0.5 / 256.0;const lowp float grainsize = 2.3;uniform lowp float vignette;highp float getLuma(highp vec3 rgbP) {return (0.299 * rgbP.r) + (0.587 * rgbP.g) + (0.114 * rgbP.b);}lowp vec3 rgbToHsv(lowp vec3 c) {highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);highp vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);highp vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);highp float d = q.x - min(q.w, q.y);highp float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}lowp vec3 hsvToRgb(lowp vec3 c) {highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}highp vec3 rgbToHsl(highp vec3 color) {highp vec3 hsl;highp float fmin = min(min(color.r, color.g), color.b);highp float fmax = max(max(color.r, color.g), color.b);highp float delta = fmax - fmin;hsl.z = (fmax + fmin) / 2.0;if (delta == 0.0) {hsl.x = 0.0;hsl.y = 0.0;} else {if (hsl.z < 0.5) {hsl.y = delta / (fmax + fmin);} else {hsl.y = delta / (2.0 - fmax - fmin);}highp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;highp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;highp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;if (color.r == fmax) {hsl.x = deltaB - deltaG;} else if (color.g == fmax) {hsl.x = (1.0 / 3.0) + deltaR - deltaB;} else if (color.b == fmax) {hsl.x = (2.0 / 3.0) + deltaG - deltaR;}if (hsl.x < 0.0) {hsl.x += 1.0;} else if (hsl.x > 1.0) {hsl.x -= 1.0;}}return hsl;}highp float hueToRgb(highp float f1, highp float f2, highp float hue) {if (hue < 0.0) {hue += 1.0;} else if (hue > 1.0) {hue -= 1.0;}highp float res;if ((6.0 * hue) < 1.0) {res = f1 + (f2 - f1) * 6.0 * hue;} else if ((2.0 * hue) < 1.0) {res = f2;} else if ((3.0 * hue) < 2.0) {res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;} else {res = f1;} return res;}highp vec3 hslToRgb(highp vec3 hsl) {if (hsl.y == 0.0) {return vec3(hsl.z);} else {highp float f2;if (hsl.z < 0.5) {f2 = hsl.z * (1.0 + hsl.y);} else {f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);}highp float f1 = 2.0 * hsl.z - f2;return vec3(hueToRgb(f1, f2, hsl.x + (1.0/3.0)), hueToRgb(f1, f2, hsl.x), hueToRgb(f1, f2, hsl.x - (1.0/3.0)));}}highp vec3 rgbToYuv(highp vec3 inP) {highp float luma = getLuma(inP);return vec3(luma, (1.0 / 1.772) * (inP.b - luma), (1.0 / 1.402) * (inP.r - luma));}lowp vec3 yuvToRgb(highp vec3 inP) {return vec3(1.402 * inP.b + inP.r, (inP.r - (0.299 * 1.402 / 0.587) * inP.b - (0.114 * 1.772 / 0.587) * inP.g), 1.772 * inP.g + inP.r);}lowp float easeInOutSigmoid(lowp float value, lowp float strength) {if (value > 0.5) {return 1.0 - pow(2.0 - 2.0 * value, 1.0 / (1.0 - strength)) * 0.5;} else {return pow(2.0 * value, 1.0 / (1.0 - strength)) * 0.5;}}lowp vec3 applyLuminanceCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.z / (1.0 / 200.0), 0.0, 199.0));pixel.y = mix(0.0, pixel.y, smoothstep(0.0, 0.1, pixel.z) * (1.0 - smoothstep(0.8, 1.0, pixel.z)));pixel.z = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).a;return pixel;}lowp vec3 applyRGBCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.r / (1.0 / 200.0), 0.0, 199.0));pixel.r = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).r;index = floor(clamp(pixel.g / (1.0 / 200.0), 0.0, 199.0));pixel.g = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).g, 0.0, 1.0);index = floor(clamp(pixel.b / (1.0 / 200.0), 0.0, 199.0));pixel.b = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).b, 0.0, 1.0);return pixel;}highp vec3 fadeAdjust(highp vec3 color, highp float fadeVal) {return (color * (1.0 - fadeVal)) + ((color + (vec3(-0.9772) * pow(vec3(color), vec3(3.0)) + vec3(1.708) * pow(vec3(color), vec3(2.0)) + vec3(-0.1603) * vec3(color) + vec3(0.2878) - color * vec3(0.9))) * fadeVal);}lowp vec3 tintRaiseShadowsCurve(lowp vec3 color) {return vec3(-0.003671) * pow(color, vec3(3.0)) + vec3(0.3842) * pow(color, vec3(2.0)) + vec3(0.3764) * color + vec3(0.2515);}lowp vec3 tintShadows(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, tintRaiseShadowsCurve(texel), tintColor), tintAmount), 0.0, 1.0);} lowp vec3 tintHighlights(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, vec3(1.0) - tintRaiseShadowsCurve(vec3(1.0) - texel), (vec3(1.0) - tintColor)), tintAmount), 0.0, 1.0);}highp vec4 rnm(in highp vec2 tc) {highp float noise = sin(dot(tc, vec2(12.9898, 78.233))) * 43758.5453;return vec4(fract(noise), fract(noise * 1.2154), fract(noise * 1.3453), fract(noise * 1.3647)) * 2.0 - 1.0;}highp float fade(in highp float t) {return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);}highp float pnoise3D(in highp vec3 p) {highp vec3 pi = permTexUnit * floor(p) + permTexUnitHalf;highp vec3 pf = fract(p);highp float perm = rnm(pi.xy).a;highp float n000 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf);highp float n001 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(0.0, permTexUnit)).a;highp float n010 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 0.0));highp float n011 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, 0.0)).a;highp float n100 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 0.0));highp float n101 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, permTexUnit)).a;highp float n110 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 0.0));highp float n111 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 1.0));highp vec4 n_x = mix(vec4(n000, n001, n010, n011), vec4(n100, n101, n110, n111), fade(pf.x));highp vec2 n_xy = mix(n_x.xy, n_x.zw, fade(pf.y));return mix(n_xy.x, n_xy.y, fade(pf.z));}lowp vec2 coordRot(in lowp vec2 tc, in lowp float angle) {return vec2(((tc.x * 2.0 - 1.0) * cos(angle) - (tc.y * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5, ((tc.y * 2.0 - 1.0) * cos(angle) + (tc.x * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5);}void main() {lowp vec4 source = texture2D(sourceImage, texCoord);lowp vec4 result = source;const lowp float toolEpsilon = 0.005;if (skipTone < toolEpsilon) {result = vec4(applyRGBCurve(hslToRgb(applyLuminanceCurve(rgbToHsl(result.rgb)))), result.a);}mediump float hsLuminance = dot(result.rgb, hsLuminanceWeighting);mediump float shadow = clamp((pow(hsLuminance, 1.0 / shadows) + (-0.76) * pow(hsLuminance, 2.0 / shadows)) - hsLuminance, 0.0, 1.0);mediump float highlight = clamp((1.0 - (pow(1.0 - hsLuminance, 1.0 / (2.0 - highlights)) + (-0.8) * pow(1.0 - hsLuminance, 2.0 / (2.0 - highlights)))) - hsLuminance, -1.0, 0.0);lowp vec3 hsresult = vec3(0.0, 0.0, 0.0) + ((hsLuminance + shadow + highlight) - 0.0) * ((result.rgb - vec3(0.0, 0.0, 0.0)) / (hsLuminance - 0.0));mediump float contrastedLuminance = ((hsLuminance - 0.5) * 1.5) + 0.5;mediump float whiteInterp = contrastedLuminance * contrastedLuminance * contrastedLuminance;mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;hsresult = mix(hsresult, vec3(1.0), whiteInterp * whiteTarget);mediump float invContrastedLuminance = 1.0 - contrastedLuminance;mediump float blackInterp = invContrastedLuminance * invContrastedLuminance * invContrastedLuminance;mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);hsresult = mix(hsresult, vec3(0.0), blackInterp * blackTarget);result = vec4(hsresult.rgb, result.a);result = vec4(clamp(((result.rgb - vec3(0.5)) * contrast + vec3(0.5)), 0.0, 1.0), result.a);if (abs(fadeAmount) > toolEpsilon) {result.rgb = fadeAdjust(result.rgb, fadeAmount);}lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);lowp vec3 greyScaleColor = vec3(satLuminance);result = vec4(clamp(mix(greyScaleColor, result.rgb, saturation), 0.0, 1.0), result.a);if (abs(shadowsTintIntensity) > toolEpsilon) {result.rgb = tintShadows(result.rgb, shadowsTintColor, shadowsTintIntensity * 2.0);}if (abs(highlightsTintIntensity) > toolEpsilon) {result.rgb = tintHighlights(result.rgb, highlightsTintColor, highlightsTintIntensity * 2.0);}if (abs(exposure) > toolEpsilon) {mediump float mag = exposure * 1.045;mediump float exppower = 1.0 + abs(mag);if (mag < 0.0) {exppower = 1.0 / exppower;}result.r = 1.0 - pow((1.0 - result.r), exppower);result.g = 1.0 - pow((1.0 - result.g), exppower);result.b = 1.0 - pow((1.0 - result.b), exppower);}if (abs(warmth) > toolEpsilon) {highp vec3 yuvVec;if (warmth > 0.0 ) {yuvVec = vec3(0.1765, -0.1255, 0.0902);} else {yuvVec = -vec3(0.0588, 0.1569, -0.1255);}highp vec3 yuvColor = rgbToYuv(result.rgb);highp float luma = yuvColor.r;highp float curveScale = sin(luma * 3.14159);yuvColor += 0.375 * warmth * curveScale * yuvVec;result.rgb = yuvToRgb(yuvColor);}if (abs(grain) > toolEpsilon) {highp vec3 rotOffset = vec3(1.425, 3.892, 5.835);highp vec2 rotCoordsR = coordRot(texCoord, rotOffset.x);highp vec3 noise = vec3(pnoise3D(vec3(rotCoordsR * vec2(width / grainsize, height / grainsize),0.0)));lowp vec3 lumcoeff = vec3(0.299,0.587,0.114);lowp float luminance = dot(result.rgb, lumcoeff);lowp float lum = smoothstep(0.2, 0.0, luminance);lum += luminance;noise = mix(noise,vec3(0.0),pow(lum,4.0));result.rgb = result.rgb + noise * grain;}if (abs(vignette) > toolEpsilon) {const lowp float midpoint = 0.7;const lowp float fuzziness = 0.62;lowp float radDist = length(texCoord - 0.5) / sqrt(0.5);lowp float mag = easeInOutSigmoid(radDist * midpoint, fuzziness) * vignette * 0.645;result.rgb = mix(pow(result.rgb, vec3(1.0 / (1.0 - mag))), vec3(0.0), mag * mag);}gl_FragColor = result;}";
    private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private final int EGL_OPENGL_ES2_BIT = 4;
    private int blurHeightHandle;
    private int blurInputTexCoordHandle;
    private int blurPositionHandle;
    private int blurShaderProgram;
    private int blurSourceImageHandle;
    private int blurWidthHandle;
    private boolean blured;
    private int contrastHandle;
    private Bitmap currentBitmap;
    private int[] curveTextures = new int[1];
    private int curvesImageHandle;
    private Runnable drawRunnable = new Runnable()
    {
      public void run()
      {
        if (!PhotoFilterView.EGLThread.this.initied) {
          return;
        }
        if (((!PhotoFilterView.EGLThread.this.eglContext.equals(PhotoFilterView.EGLThread.this.egl10.eglGetCurrentContext())) || (!PhotoFilterView.EGLThread.this.eglSurface.equals(PhotoFilterView.EGLThread.this.egl10.eglGetCurrentSurface(12377)))) && (!PhotoFilterView.EGLThread.this.egl10.eglMakeCurrent(PhotoFilterView.EGLThread.this.eglDisplay, PhotoFilterView.EGLThread.this.eglSurface, PhotoFilterView.EGLThread.this.eglSurface, PhotoFilterView.EGLThread.this.eglContext)))
        {
          FileLog.e("tmessages", "eglMakeCurrent failed " + GLUtils.getEGLErrorString(PhotoFilterView.EGLThread.this.egl10.eglGetError()));
          return;
        }
        GLES20.glViewport(0, 0, PhotoFilterView.EGLThread.this.renderBufferWidth, PhotoFilterView.EGLThread.this.renderBufferHeight);
        PhotoFilterView.EGLThread.this.drawEnhancePass();
        PhotoFilterView.EGLThread.this.drawSharpenPass();
        PhotoFilterView.EGLThread.this.drawCustomParamsPass();
        PhotoFilterView.EGLThread.access$3202(PhotoFilterView.EGLThread.this, PhotoFilterView.EGLThread.this.drawBlurPass());
        GLES20.glViewport(0, 0, PhotoFilterView.EGLThread.this.surfaceWidth, PhotoFilterView.EGLThread.this.surfaceHeight);
        GLES20.glBindFramebuffer(36160, 0);
        GLES20.glClear(0);
        GLES20.glUseProgram(PhotoFilterView.EGLThread.this.simpleShaderProgram);
        GLES20.glActiveTexture(33984);
        int[] arrayOfInt = PhotoFilterView.EGLThread.this.renderTexture;
        if (PhotoFilterView.EGLThread.this.blured) {}
        for (int i = 0;; i = 1)
        {
          GLES20.glBindTexture(3553, arrayOfInt[i]);
          GLES20.glUniform1i(PhotoFilterView.EGLThread.this.simpleSourceImageHandle, 0);
          GLES20.glEnableVertexAttribArray(PhotoFilterView.EGLThread.this.simpleInputTexCoordHandle);
          GLES20.glVertexAttribPointer(PhotoFilterView.EGLThread.this.simpleInputTexCoordHandle, 2, 5126, false, 8, PhotoFilterView.EGLThread.this.textureBuffer);
          GLES20.glEnableVertexAttribArray(PhotoFilterView.EGLThread.this.simplePositionHandle);
          GLES20.glVertexAttribPointer(PhotoFilterView.EGLThread.this.simplePositionHandle, 2, 5126, false, 8, PhotoFilterView.EGLThread.this.vertexBuffer);
          GLES20.glDrawArrays(5, 0, 4);
          PhotoFilterView.EGLThread.this.egl10.eglSwapBuffers(PhotoFilterView.EGLThread.this.eglDisplay, PhotoFilterView.EGLThread.this.eglSurface);
          return;
        }
      }
    };
    private EGL10 egl10;
    private EGLConfig eglConfig;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface;
    private int enhanceInputImageTexture2Handle;
    private int enhanceInputTexCoordHandle;
    private int enhanceIntensityHandle;
    private int enhancePositionHandle;
    private int enhanceShaderProgram;
    private int enhanceSourceImageHandle;
    private int[] enhanceTextures = new int[2];
    private int exposureHandle;
    private int fadeAmountHandle;
    private GL gl;
    private int grainHandle;
    private int heightHandle;
    private int highlightsHandle;
    private int highlightsTintColorHandle;
    private int highlightsTintIntensityHandle;
    private boolean hsvGenerated;
    private boolean initied;
    private int inputTexCoordHandle;
    private long lastRenderCallTime;
    private int linearBlurAngleHandle;
    private int linearBlurAspectRatioHandle;
    private int linearBlurExcludeBlurSizeHandle;
    private int linearBlurExcludePointHandle;
    private int linearBlurExcludeSizeHandle;
    private int linearBlurInputTexCoordHandle;
    private int linearBlurPositionHandle;
    private int linearBlurShaderProgram;
    private int linearBlurSourceImage2Handle;
    private int linearBlurSourceImageHandle;
    private boolean needUpdateBlurTexture = true;
    private int positionHandle;
    private int radialBlurAspectRatioHandle;
    private int radialBlurExcludeBlurSizeHandle;
    private int radialBlurExcludePointHandle;
    private int radialBlurExcludeSizeHandle;
    private int radialBlurInputTexCoordHandle;
    private int radialBlurPositionHandle;
    private int radialBlurShaderProgram;
    private int radialBlurSourceImage2Handle;
    private int radialBlurSourceImageHandle;
    private int renderBufferHeight;
    private int renderBufferWidth;
    private int[] renderFrameBuffer = new int[3];
    private int[] renderTexture = new int[3];
    private int rgbToHsvInputTexCoordHandle;
    private int rgbToHsvPositionHandle;
    private int rgbToHsvShaderProgram;
    private int rgbToHsvSourceImageHandle;
    private int saturationHandle;
    private int shadowsHandle;
    private int shadowsTintColorHandle;
    private int shadowsTintIntensityHandle;
    private int sharpenHandle;
    private int sharpenHeightHandle;
    private int sharpenInputTexCoordHandle;
    private int sharpenPositionHandle;
    private int sharpenShaderProgram;
    private int sharpenSourceImageHandle;
    private int sharpenWidthHandle;
    private int simpleInputTexCoordHandle;
    private int simplePositionHandle;
    private int simpleShaderProgram;
    private int simpleSourceImageHandle;
    private int skipToneHandle;
    private int sourceImageHandle;
    private volatile int surfaceHeight;
    private SurfaceTexture surfaceTexture;
    private volatile int surfaceWidth;
    private FloatBuffer textureBuffer;
    private int toolsShaderProgram;
    private FloatBuffer vertexBuffer;
    private FloatBuffer vertexInvertBuffer;
    private int vignetteHandle;
    private int warmthHandle;
    private int widthHandle;
    
    public EGLThread(SurfaceTexture paramSurfaceTexture, Bitmap paramBitmap)
    {
      super();
      this.surfaceTexture = paramSurfaceTexture;
      this.currentBitmap = paramBitmap;
    }
    
    private Bitmap createBitmap(Bitmap paramBitmap, int paramInt1, int paramInt2, float paramFloat)
    {
      Matrix localMatrix = new Matrix();
      localMatrix.setScale(paramFloat, paramFloat);
      localMatrix.postRotate(PhotoFilterView.this.orientation);
      return Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
    }
    
    private boolean drawBlurPass()
    {
      if ((PhotoFilterView.this.showOriginal) || (PhotoFilterView.this.blurType == 0)) {
        return false;
      }
      if (this.needUpdateBlurTexture)
      {
        GLES20.glUseProgram(this.blurShaderProgram);
        GLES20.glUniform1i(this.blurSourceImageHandle, 0);
        GLES20.glEnableVertexAttribArray(this.blurInputTexCoordHandle);
        GLES20.glVertexAttribPointer(this.blurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(this.blurPositionHandle);
        GLES20.glVertexAttribPointer(this.blurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
        GLES20.glClear(0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.renderTexture[1]);
        GLES20.glUniform1f(this.blurWidthHandle, 0.0F);
        GLES20.glUniform1f(this.blurHeightHandle, 1.0F / this.renderBufferHeight);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[2]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[2], 0);
        GLES20.glClear(0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.renderTexture[0]);
        GLES20.glUniform1f(this.blurWidthHandle, 1.0F / this.renderBufferWidth);
        GLES20.glUniform1f(this.blurHeightHandle, 0.0F);
        GLES20.glDrawArrays(5, 0, 4);
        this.needUpdateBlurTexture = false;
      }
      GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
      GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
      GLES20.glClear(0);
      if (PhotoFilterView.this.blurType == 1)
      {
        GLES20.glUseProgram(this.radialBlurShaderProgram);
        GLES20.glUniform1i(this.radialBlurSourceImageHandle, 0);
        GLES20.glUniform1i(this.radialBlurSourceImage2Handle, 1);
        GLES20.glUniform1f(this.radialBlurExcludeSizeHandle, PhotoFilterView.this.blurExcludeSize);
        GLES20.glUniform1f(this.radialBlurExcludeBlurSizeHandle, PhotoFilterView.this.blurExcludeBlurSize);
        GLES20.glUniform2f(this.radialBlurExcludePointHandle, PhotoFilterView.this.blurExcludePoint.x, PhotoFilterView.this.blurExcludePoint.y);
        GLES20.glUniform1f(this.radialBlurAspectRatioHandle, this.renderBufferHeight / this.renderBufferWidth);
        GLES20.glEnableVertexAttribArray(this.radialBlurInputTexCoordHandle);
        GLES20.glVertexAttribPointer(this.radialBlurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(this.radialBlurPositionHandle);
        GLES20.glVertexAttribPointer(this.radialBlurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
      }
      for (;;)
      {
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.renderTexture[1]);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, this.renderTexture[2]);
        GLES20.glDrawArrays(5, 0, 4);
        return true;
        if (PhotoFilterView.this.blurType == 2)
        {
          GLES20.glUseProgram(this.linearBlurShaderProgram);
          GLES20.glUniform1i(this.linearBlurSourceImageHandle, 0);
          GLES20.glUniform1i(this.linearBlurSourceImage2Handle, 1);
          GLES20.glUniform1f(this.linearBlurExcludeSizeHandle, PhotoFilterView.this.blurExcludeSize);
          GLES20.glUniform1f(this.linearBlurExcludeBlurSizeHandle, PhotoFilterView.this.blurExcludeBlurSize);
          GLES20.glUniform1f(this.linearBlurAngleHandle, PhotoFilterView.this.blurAngle);
          GLES20.glUniform2f(this.linearBlurExcludePointHandle, PhotoFilterView.this.blurExcludePoint.x, PhotoFilterView.this.blurExcludePoint.y);
          GLES20.glUniform1f(this.linearBlurAspectRatioHandle, this.renderBufferHeight / this.renderBufferWidth);
          GLES20.glEnableVertexAttribArray(this.linearBlurInputTexCoordHandle);
          GLES20.glVertexAttribPointer(this.linearBlurInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
          GLES20.glEnableVertexAttribArray(this.linearBlurPositionHandle);
          GLES20.glVertexAttribPointer(this.linearBlurPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
        }
      }
    }
    
    private void drawCustomParamsPass()
    {
      float f = 1.0F;
      GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
      GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
      GLES20.glClear(0);
      GLES20.glUseProgram(this.toolsShaderProgram);
      GLES20.glActiveTexture(33984);
      GLES20.glBindTexture(3553, this.renderTexture[0]);
      GLES20.glUniform1i(this.sourceImageHandle, 0);
      if (PhotoFilterView.this.showOriginal)
      {
        GLES20.glUniform1f(this.shadowsHandle, 1.0F);
        GLES20.glUniform1f(this.highlightsHandle, 1.0F);
        GLES20.glUniform1f(this.exposureHandle, 0.0F);
        GLES20.glUniform1f(this.contrastHandle, 1.0F);
        GLES20.glUniform1f(this.saturationHandle, 1.0F);
        GLES20.glUniform1f(this.warmthHandle, 0.0F);
        GLES20.glUniform1f(this.vignetteHandle, 0.0F);
        GLES20.glUniform1f(this.grainHandle, 0.0F);
        GLES20.glUniform1f(this.fadeAmountHandle, 0.0F);
        GLES20.glUniform3f(this.highlightsTintColorHandle, 0.0F, 0.0F, 0.0F);
        GLES20.glUniform1f(this.highlightsTintIntensityHandle, 0.0F);
        GLES20.glUniform3f(this.shadowsTintColorHandle, 0.0F, 0.0F, 0.0F);
        GLES20.glUniform1f(this.shadowsTintIntensityHandle, 0.0F);
        GLES20.glUniform1f(this.skipToneHandle, 1.0F);
        GLES20.glUniform1f(this.widthHandle, this.renderBufferWidth);
        GLES20.glUniform1f(this.heightHandle, this.renderBufferHeight);
        GLES20.glEnableVertexAttribArray(this.inputTexCoordHandle);
        GLES20.glVertexAttribPointer(this.inputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(this.positionHandle);
        GLES20.glVertexAttribPointer(this.positionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
        GLES20.glDrawArrays(5, 0, 4);
        return;
      }
      GLES20.glUniform1f(this.shadowsHandle, PhotoFilterView.this.getShadowsValue());
      GLES20.glUniform1f(this.highlightsHandle, PhotoFilterView.this.getHighlightsValue());
      GLES20.glUniform1f(this.exposureHandle, PhotoFilterView.this.getExposureValue());
      GLES20.glUniform1f(this.contrastHandle, PhotoFilterView.this.getContrastValue());
      GLES20.glUniform1f(this.saturationHandle, PhotoFilterView.this.getSaturationValue());
      GLES20.glUniform1f(this.warmthHandle, PhotoFilterView.this.getWarmthValue());
      GLES20.glUniform1f(this.vignetteHandle, PhotoFilterView.this.getVignetteValue());
      GLES20.glUniform1f(this.grainHandle, PhotoFilterView.this.getGrainValue());
      GLES20.glUniform1f(this.fadeAmountHandle, PhotoFilterView.this.getFadeValue());
      GLES20.glUniform3f(this.highlightsTintColorHandle, (PhotoFilterView.this.tintHighlightsColor >> 16 & 0xFF) / 255.0F, (PhotoFilterView.this.tintHighlightsColor >> 8 & 0xFF) / 255.0F, (PhotoFilterView.this.tintHighlightsColor & 0xFF) / 255.0F);
      GLES20.glUniform1f(this.highlightsTintIntensityHandle, PhotoFilterView.this.getTintHighlightsIntensityValue());
      GLES20.glUniform3f(this.shadowsTintColorHandle, (PhotoFilterView.this.tintShadowsColor >> 16 & 0xFF) / 255.0F, (PhotoFilterView.this.tintShadowsColor >> 8 & 0xFF) / 255.0F, (PhotoFilterView.this.tintShadowsColor & 0xFF) / 255.0F);
      GLES20.glUniform1f(this.shadowsTintIntensityHandle, PhotoFilterView.this.getTintShadowsIntensityValue());
      boolean bool = PhotoFilterView.this.curvesToolValue.shouldBeSkipped();
      int i = this.skipToneHandle;
      if (bool) {}
      for (;;)
      {
        GLES20.glUniform1f(i, f);
        if (bool) {
          break;
        }
        PhotoFilterView.this.curvesToolValue.fillBuffer();
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, this.curveTextures[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6408, 200, 1, 0, 6408, 5121, PhotoFilterView.this.curvesToolValue.curveBuffer);
        GLES20.glUniform1i(this.curvesImageHandle, 1);
        break;
        f = 0.0F;
      }
    }
    
    private void drawEnhancePass()
    {
      ByteBuffer localByteBuffer2;
      Object localObject;
      if (!this.hsvGenerated)
      {
        GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
        GLES20.glClear(0);
        GLES20.glUseProgram(this.rgbToHsvShaderProgram);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.renderTexture[1]);
        GLES20.glUniform1i(this.rgbToHsvSourceImageHandle, 0);
        GLES20.glEnableVertexAttribArray(this.rgbToHsvInputTexCoordHandle);
        GLES20.glVertexAttribPointer(this.rgbToHsvInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(this.rgbToHsvPositionHandle);
        GLES20.glVertexAttribPointer(this.rgbToHsvPositionHandle, 2, 5126, false, 8, this.vertexBuffer);
        GLES20.glDrawArrays(5, 0, 4);
        localByteBuffer2 = ByteBuffer.allocateDirect(this.renderBufferWidth * this.renderBufferHeight * 4);
        GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, localByteBuffer2);
        GLES20.glBindTexture(3553, this.enhanceTextures[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, localByteBuffer2);
        localObject = null;
      }
      try
      {
        ByteBuffer localByteBuffer1 = ByteBuffer.allocateDirect(16384);
        localObject = localByteBuffer1;
        Utilities.calcCDT(localByteBuffer2, this.renderBufferWidth, this.renderBufferHeight, localByteBuffer1);
        localObject = localByteBuffer1;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
          continue;
          GLES20.glUniform1f(this.enhanceIntensityHandle, PhotoFilterView.this.getEnhanceValue());
        }
      }
      GLES20.glBindTexture(3553, this.enhanceTextures[1]);
      GLES20.glTexParameteri(3553, 10241, 9729);
      GLES20.glTexParameteri(3553, 10240, 9729);
      GLES20.glTexParameteri(3553, 10242, 33071);
      GLES20.glTexParameteri(3553, 10243, 33071);
      GLES20.glTexImage2D(3553, 0, 6408, 256, 16, 0, 6408, 5121, (Buffer)localObject);
      this.hsvGenerated = true;
      GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[1]);
      GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[1], 0);
      GLES20.glClear(0);
      GLES20.glUseProgram(this.enhanceShaderProgram);
      GLES20.glActiveTexture(33984);
      GLES20.glBindTexture(3553, this.enhanceTextures[0]);
      GLES20.glUniform1i(this.enhanceSourceImageHandle, 0);
      GLES20.glActiveTexture(33985);
      GLES20.glBindTexture(3553, this.enhanceTextures[1]);
      GLES20.glUniform1i(this.enhanceInputImageTexture2Handle, 1);
      if (PhotoFilterView.this.showOriginal)
      {
        GLES20.glUniform1f(this.enhanceIntensityHandle, 0.0F);
        GLES20.glEnableVertexAttribArray(this.enhanceInputTexCoordHandle);
        GLES20.glVertexAttribPointer(this.enhanceInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(this.enhancePositionHandle);
        GLES20.glVertexAttribPointer(this.enhancePositionHandle, 2, 5126, false, 8, this.vertexBuffer);
        GLES20.glDrawArrays(5, 0, 4);
        return;
      }
    }
    
    private void drawSharpenPass()
    {
      GLES20.glBindFramebuffer(36160, this.renderFrameBuffer[0]);
      GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.renderTexture[0], 0);
      GLES20.glClear(0);
      GLES20.glUseProgram(this.sharpenShaderProgram);
      GLES20.glActiveTexture(33984);
      GLES20.glBindTexture(3553, this.renderTexture[1]);
      GLES20.glUniform1i(this.sharpenSourceImageHandle, 0);
      if (PhotoFilterView.this.showOriginal) {
        GLES20.glUniform1f(this.sharpenHandle, 0.0F);
      }
      for (;;)
      {
        GLES20.glUniform1f(this.sharpenWidthHandle, this.renderBufferWidth);
        GLES20.glUniform1f(this.sharpenHeightHandle, this.renderBufferHeight);
        GLES20.glEnableVertexAttribArray(this.sharpenInputTexCoordHandle);
        GLES20.glVertexAttribPointer(this.sharpenInputTexCoordHandle, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(this.sharpenPositionHandle);
        GLES20.glVertexAttribPointer(this.sharpenPositionHandle, 2, 5126, false, 8, this.vertexInvertBuffer);
        GLES20.glDrawArrays(5, 0, 4);
        return;
        GLES20.glUniform1f(this.sharpenHandle, PhotoFilterView.this.getSharpenValue());
      }
    }
    
    private Bitmap getRenderBufferBitmap()
    {
      ByteBuffer localByteBuffer = ByteBuffer.allocateDirect(this.renderBufferWidth * this.renderBufferHeight * 4);
      GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, localByteBuffer);
      Bitmap localBitmap = Bitmap.createBitmap(this.renderBufferWidth, this.renderBufferHeight, Bitmap.Config.ARGB_8888);
      localBitmap.copyPixelsFromBuffer(localByteBuffer);
      return localBitmap;
    }
    
    private boolean initGL()
    {
      this.egl10 = ((EGL10)EGLContext.getEGL());
      this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
      if (this.eglDisplay == EGL10.EGL_NO_DISPLAY)
      {
        FileLog.e("tmessages", "eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        finish();
        return false;
      }
      Object localObject1 = new int[2];
      if (!this.egl10.eglInitialize(this.eglDisplay, (int[])localObject1))
      {
        FileLog.e("tmessages", "eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        finish();
        return false;
      }
      localObject1 = new int[1];
      Object localObject2 = new EGLConfig[1];
      if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[] { 12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344 }, (EGLConfig[])localObject2, 1, (int[])localObject1))
      {
        FileLog.e("tmessages", "eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        finish();
        return false;
      }
      if (localObject1[0] > 0)
      {
        this.eglConfig = localObject2[0];
        this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[] { 12440, 2, 12344 });
        if (this.eglContext == null)
        {
          FileLog.e("tmessages", "eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
          finish();
          return false;
        }
      }
      else
      {
        FileLog.e("tmessages", "eglConfig not initialized");
        finish();
        return false;
      }
      if ((this.surfaceTexture instanceof SurfaceTexture))
      {
        this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
        if ((this.eglSurface == null) || (this.eglSurface == EGL10.EGL_NO_SURFACE))
        {
          FileLog.e("tmessages", "createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
          finish();
          return false;
        }
      }
      else
      {
        finish();
        return false;
      }
      if (!this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext))
      {
        FileLog.e("tmessages", "eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
        finish();
        return false;
      }
      this.gl = this.eglContext.getGL();
      localObject1 = new float[8];
      Object tmp621_620 = localObject1;
      tmp621_620[0] = -1.0F;
      Object tmp627_621 = tmp621_620;
      tmp627_621[1] = 1.0F;
      Object tmp631_627 = tmp627_621;
      tmp631_627[2] = 1.0F;
      Object tmp635_631 = tmp631_627;
      tmp635_631[3] = 1.0F;
      Object tmp639_635 = tmp635_631;
      tmp639_635[4] = -1.0F;
      Object tmp645_639 = tmp639_635;
      tmp645_639[5] = -1.0F;
      Object tmp651_645 = tmp645_639;
      tmp651_645[6] = 1.0F;
      Object tmp656_651 = tmp651_645;
      tmp656_651[7] = -1.0F;
      tmp656_651;
      localObject2 = ByteBuffer.allocateDirect(localObject1.length * 4);
      ((ByteBuffer)localObject2).order(ByteOrder.nativeOrder());
      this.vertexBuffer = ((ByteBuffer)localObject2).asFloatBuffer();
      this.vertexBuffer.put((float[])localObject1);
      this.vertexBuffer.position(0);
      localObject1 = new float[8];
      Object tmp715_714 = localObject1;
      tmp715_714[0] = -1.0F;
      Object tmp721_715 = tmp715_714;
      tmp721_715[1] = -1.0F;
      Object tmp727_721 = tmp721_715;
      tmp727_721[2] = 1.0F;
      Object tmp731_727 = tmp727_721;
      tmp731_727[3] = -1.0F;
      Object tmp737_731 = tmp731_727;
      tmp737_731[4] = -1.0F;
      Object tmp743_737 = tmp737_731;
      tmp743_737[5] = 1.0F;
      Object tmp747_743 = tmp743_737;
      tmp747_743[6] = 1.0F;
      Object tmp752_747 = tmp747_743;
      tmp752_747[7] = 1.0F;
      tmp752_747;
      localObject2 = ByteBuffer.allocateDirect(localObject1.length * 4);
      ((ByteBuffer)localObject2).order(ByteOrder.nativeOrder());
      this.vertexInvertBuffer = ((ByteBuffer)localObject2).asFloatBuffer();
      this.vertexInvertBuffer.put((float[])localObject1);
      this.vertexInvertBuffer.position(0);
      localObject1 = new float[8];
      Object tmp809_808 = localObject1;
      tmp809_808[0] = 0.0F;
      Object tmp813_809 = tmp809_808;
      tmp813_809[1] = 0.0F;
      Object tmp817_813 = tmp813_809;
      tmp817_813[2] = 1.0F;
      Object tmp821_817 = tmp817_813;
      tmp821_817[3] = 0.0F;
      Object tmp825_821 = tmp821_817;
      tmp825_821[4] = 0.0F;
      Object tmp829_825 = tmp825_821;
      tmp829_825[5] = 1.0F;
      Object tmp833_829 = tmp829_825;
      tmp833_829[6] = 1.0F;
      Object tmp838_833 = tmp833_829;
      tmp838_833[7] = 1.0F;
      tmp838_833;
      localObject2 = ByteBuffer.allocateDirect(localObject1.length * 4);
      ((ByteBuffer)localObject2).order(ByteOrder.nativeOrder());
      this.textureBuffer = ((ByteBuffer)localObject2).asFloatBuffer();
      this.textureBuffer.put((float[])localObject1);
      this.textureBuffer.position(0);
      GLES20.glGenTextures(1, this.curveTextures, 0);
      GLES20.glGenTextures(2, this.enhanceTextures, 0);
      int i = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
      int j = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform highp float width;uniform highp float height;uniform sampler2D curvesImage;uniform lowp float skipTone;uniform lowp float shadows;const mediump vec3 hsLuminanceWeighting = vec3(0.3, 0.3, 0.3);uniform lowp float highlights;uniform lowp float contrast;uniform lowp float fadeAmount;const mediump vec3 satLuminanceWeighting = vec3(0.2126, 0.7152, 0.0722);uniform lowp float saturation;uniform lowp float shadowsTintIntensity;uniform lowp float highlightsTintIntensity;uniform lowp vec3 shadowsTintColor;uniform lowp vec3 highlightsTintColor;uniform lowp float exposure;uniform lowp float warmth;uniform lowp float grain;const lowp float permTexUnit = 1.0 / 256.0;const lowp float permTexUnitHalf = 0.5 / 256.0;const lowp float grainsize = 2.3;uniform lowp float vignette;highp float getLuma(highp vec3 rgbP) {return (0.299 * rgbP.r) + (0.587 * rgbP.g) + (0.114 * rgbP.b);}lowp vec3 rgbToHsv(lowp vec3 c) {highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);highp vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);highp vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);highp float d = q.x - min(q.w, q.y);highp float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}lowp vec3 hsvToRgb(lowp vec3 c) {highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}highp vec3 rgbToHsl(highp vec3 color) {highp vec3 hsl;highp float fmin = min(min(color.r, color.g), color.b);highp float fmax = max(max(color.r, color.g), color.b);highp float delta = fmax - fmin;hsl.z = (fmax + fmin) / 2.0;if (delta == 0.0) {hsl.x = 0.0;hsl.y = 0.0;} else {if (hsl.z < 0.5) {hsl.y = delta / (fmax + fmin);} else {hsl.y = delta / (2.0 - fmax - fmin);}highp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;highp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;highp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;if (color.r == fmax) {hsl.x = deltaB - deltaG;} else if (color.g == fmax) {hsl.x = (1.0 / 3.0) + deltaR - deltaB;} else if (color.b == fmax) {hsl.x = (2.0 / 3.0) + deltaG - deltaR;}if (hsl.x < 0.0) {hsl.x += 1.0;} else if (hsl.x > 1.0) {hsl.x -= 1.0;}}return hsl;}highp float hueToRgb(highp float f1, highp float f2, highp float hue) {if (hue < 0.0) {hue += 1.0;} else if (hue > 1.0) {hue -= 1.0;}highp float res;if ((6.0 * hue) < 1.0) {res = f1 + (f2 - f1) * 6.0 * hue;} else if ((2.0 * hue) < 1.0) {res = f2;} else if ((3.0 * hue) < 2.0) {res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;} else {res = f1;} return res;}highp vec3 hslToRgb(highp vec3 hsl) {if (hsl.y == 0.0) {return vec3(hsl.z);} else {highp float f2;if (hsl.z < 0.5) {f2 = hsl.z * (1.0 + hsl.y);} else {f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);}highp float f1 = 2.0 * hsl.z - f2;return vec3(hueToRgb(f1, f2, hsl.x + (1.0/3.0)), hueToRgb(f1, f2, hsl.x), hueToRgb(f1, f2, hsl.x - (1.0/3.0)));}}highp vec3 rgbToYuv(highp vec3 inP) {highp float luma = getLuma(inP);return vec3(luma, (1.0 / 1.772) * (inP.b - luma), (1.0 / 1.402) * (inP.r - luma));}lowp vec3 yuvToRgb(highp vec3 inP) {return vec3(1.402 * inP.b + inP.r, (inP.r - (0.299 * 1.402 / 0.587) * inP.b - (0.114 * 1.772 / 0.587) * inP.g), 1.772 * inP.g + inP.r);}lowp float easeInOutSigmoid(lowp float value, lowp float strength) {if (value > 0.5) {return 1.0 - pow(2.0 - 2.0 * value, 1.0 / (1.0 - strength)) * 0.5;} else {return pow(2.0 * value, 1.0 / (1.0 - strength)) * 0.5;}}lowp vec3 applyLuminanceCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.z / (1.0 / 200.0), 0.0, 199.0));pixel.y = mix(0.0, pixel.y, smoothstep(0.0, 0.1, pixel.z) * (1.0 - smoothstep(0.8, 1.0, pixel.z)));pixel.z = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).a;return pixel;}lowp vec3 applyRGBCurve(lowp vec3 pixel) {highp float index = floor(clamp(pixel.r / (1.0 / 200.0), 0.0, 199.0));pixel.r = texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).r;index = floor(clamp(pixel.g / (1.0 / 200.0), 0.0, 199.0));pixel.g = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).g, 0.0, 1.0);index = floor(clamp(pixel.b / (1.0 / 200.0), 0.0, 199.0));pixel.b = clamp(texture2D(curvesImage, vec2(1.0 / 200.0 * index, 0)).b, 0.0, 1.0);return pixel;}highp vec3 fadeAdjust(highp vec3 color, highp float fadeVal) {return (color * (1.0 - fadeVal)) + ((color + (vec3(-0.9772) * pow(vec3(color), vec3(3.0)) + vec3(1.708) * pow(vec3(color), vec3(2.0)) + vec3(-0.1603) * vec3(color) + vec3(0.2878) - color * vec3(0.9))) * fadeVal);}lowp vec3 tintRaiseShadowsCurve(lowp vec3 color) {return vec3(-0.003671) * pow(color, vec3(3.0)) + vec3(0.3842) * pow(color, vec3(2.0)) + vec3(0.3764) * color + vec3(0.2515);}lowp vec3 tintShadows(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, tintRaiseShadowsCurve(texel), tintColor), tintAmount), 0.0, 1.0);} lowp vec3 tintHighlights(lowp vec3 texel, lowp vec3 tintColor, lowp float tintAmount) {return clamp(mix(texel, mix(texel, vec3(1.0) - tintRaiseShadowsCurve(vec3(1.0) - texel), (vec3(1.0) - tintColor)), tintAmount), 0.0, 1.0);}highp vec4 rnm(in highp vec2 tc) {highp float noise = sin(dot(tc, vec2(12.9898, 78.233))) * 43758.5453;return vec4(fract(noise), fract(noise * 1.2154), fract(noise * 1.3453), fract(noise * 1.3647)) * 2.0 - 1.0;}highp float fade(in highp float t) {return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);}highp float pnoise3D(in highp vec3 p) {highp vec3 pi = permTexUnit * floor(p) + permTexUnitHalf;highp vec3 pf = fract(p);highp float perm = rnm(pi.xy).a;highp float n000 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf);highp float n001 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(0.0, permTexUnit)).a;highp float n010 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 0.0));highp float n011 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(0.0, 1.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, 0.0)).a;highp float n100 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 0.0));highp float n101 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 0.0, 1.0));perm = rnm(pi.xy + vec2(permTexUnit, permTexUnit)).a;highp float n110 = dot(rnm(vec2(perm, pi.z)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 0.0));highp float n111 = dot(rnm(vec2(perm, pi.z + permTexUnit)).rgb * 4.0 - 1.0, pf - vec3(1.0, 1.0, 1.0));highp vec4 n_x = mix(vec4(n000, n001, n010, n011), vec4(n100, n101, n110, n111), fade(pf.x));highp vec2 n_xy = mix(n_x.xy, n_x.zw, fade(pf.y));return mix(n_xy.x, n_xy.y, fade(pf.z));}lowp vec2 coordRot(in lowp vec2 tc, in lowp float angle) {return vec2(((tc.x * 2.0 - 1.0) * cos(angle) - (tc.y * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5, ((tc.y * 2.0 - 1.0) * cos(angle) + (tc.x * 2.0 - 1.0) * sin(angle)) * 0.5 + 0.5);}void main() {lowp vec4 source = texture2D(sourceImage, texCoord);lowp vec4 result = source;const lowp float toolEpsilon = 0.005;if (skipTone < toolEpsilon) {result = vec4(applyRGBCurve(hslToRgb(applyLuminanceCurve(rgbToHsl(result.rgb)))), result.a);}mediump float hsLuminance = dot(result.rgb, hsLuminanceWeighting);mediump float shadow = clamp((pow(hsLuminance, 1.0 / shadows) + (-0.76) * pow(hsLuminance, 2.0 / shadows)) - hsLuminance, 0.0, 1.0);mediump float highlight = clamp((1.0 - (pow(1.0 - hsLuminance, 1.0 / (2.0 - highlights)) + (-0.8) * pow(1.0 - hsLuminance, 2.0 / (2.0 - highlights)))) - hsLuminance, -1.0, 0.0);lowp vec3 hsresult = vec3(0.0, 0.0, 0.0) + ((hsLuminance + shadow + highlight) - 0.0) * ((result.rgb - vec3(0.0, 0.0, 0.0)) / (hsLuminance - 0.0));mediump float contrastedLuminance = ((hsLuminance - 0.5) * 1.5) + 0.5;mediump float whiteInterp = contrastedLuminance * contrastedLuminance * contrastedLuminance;mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;hsresult = mix(hsresult, vec3(1.0), whiteInterp * whiteTarget);mediump float invContrastedLuminance = 1.0 - contrastedLuminance;mediump float blackInterp = invContrastedLuminance * invContrastedLuminance * invContrastedLuminance;mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);hsresult = mix(hsresult, vec3(0.0), blackInterp * blackTarget);result = vec4(hsresult.rgb, result.a);result = vec4(clamp(((result.rgb - vec3(0.5)) * contrast + vec3(0.5)), 0.0, 1.0), result.a);if (abs(fadeAmount) > toolEpsilon) {result.rgb = fadeAdjust(result.rgb, fadeAmount);}lowp float satLuminance = dot(result.rgb, satLuminanceWeighting);lowp vec3 greyScaleColor = vec3(satLuminance);result = vec4(clamp(mix(greyScaleColor, result.rgb, saturation), 0.0, 1.0), result.a);if (abs(shadowsTintIntensity) > toolEpsilon) {result.rgb = tintShadows(result.rgb, shadowsTintColor, shadowsTintIntensity * 2.0);}if (abs(highlightsTintIntensity) > toolEpsilon) {result.rgb = tintHighlights(result.rgb, highlightsTintColor, highlightsTintIntensity * 2.0);}if (abs(exposure) > toolEpsilon) {mediump float mag = exposure * 1.045;mediump float exppower = 1.0 + abs(mag);if (mag < 0.0) {exppower = 1.0 / exppower;}result.r = 1.0 - pow((1.0 - result.r), exppower);result.g = 1.0 - pow((1.0 - result.g), exppower);result.b = 1.0 - pow((1.0 - result.b), exppower);}if (abs(warmth) > toolEpsilon) {highp vec3 yuvVec;if (warmth > 0.0 ) {yuvVec = vec3(0.1765, -0.1255, 0.0902);} else {yuvVec = -vec3(0.0588, 0.1569, -0.1255);}highp vec3 yuvColor = rgbToYuv(result.rgb);highp float luma = yuvColor.r;highp float curveScale = sin(luma * 3.14159);yuvColor += 0.375 * warmth * curveScale * yuvVec;result.rgb = yuvToRgb(yuvColor);}if (abs(grain) > toolEpsilon) {highp vec3 rotOffset = vec3(1.425, 3.892, 5.835);highp vec2 rotCoordsR = coordRot(texCoord, rotOffset.x);highp vec3 noise = vec3(pnoise3D(vec3(rotCoordsR * vec2(width / grainsize, height / grainsize),0.0)));lowp vec3 lumcoeff = vec3(0.299,0.587,0.114);lowp float luminance = dot(result.rgb, lumcoeff);lowp float lum = smoothstep(0.2, 0.0, luminance);lum += luminance;noise = mix(noise,vec3(0.0),pow(lum,4.0));result.rgb = result.rgb + noise * grain;}if (abs(vignette) > toolEpsilon) {const lowp float midpoint = 0.7;const lowp float fuzziness = 0.62;lowp float radDist = length(texCoord - 0.5) / sqrt(0.5);lowp float mag = easeInOutSigmoid(radDist * midpoint, fuzziness) * vignette * 0.645;result.rgb = mix(pow(result.rgb, vec3(1.0 / (1.0 - mag))), vec3(0.0), mag * mag);}gl_FragColor = result;}");
      if ((i != 0) && (j != 0))
      {
        this.toolsShaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(this.toolsShaderProgram, i);
        GLES20.glAttachShader(this.toolsShaderProgram, j);
        GLES20.glBindAttribLocation(this.toolsShaderProgram, 0, "position");
        GLES20.glBindAttribLocation(this.toolsShaderProgram, 1, "inputTexCoord");
        GLES20.glLinkProgram(this.toolsShaderProgram);
        localObject1 = new int[1];
        GLES20.glGetProgramiv(this.toolsShaderProgram, 35714, (int[])localObject1, 0);
        if (localObject1[0] == 0)
        {
          GLES20.glDeleteProgram(this.toolsShaderProgram);
          this.toolsShaderProgram = 0;
          i = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;uniform highp float inputWidth;uniform highp float inputHeight;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;void main() {gl_Position = position;texCoord = inputTexCoord;highp vec2 widthStep = vec2(1.0 / inputWidth, 0.0);highp vec2 heightStep = vec2(0.0, 1.0 / inputHeight);leftTexCoord = inputTexCoord - widthStep;rightTexCoord = inputTexCoord + widthStep;topTexCoord = inputTexCoord + heightStep;bottomTexCoord = inputTexCoord - heightStep;}");
          j = loadShader(35632, "precision highp float;varying vec2 texCoord;varying vec2 leftTexCoord;varying vec2 rightTexCoord;varying vec2 topTexCoord;varying vec2 bottomTexCoord;uniform sampler2D sourceImage;uniform float sharpen;void main() {vec4 result = texture2D(sourceImage, texCoord);vec3 leftTextureColor = texture2D(sourceImage, leftTexCoord).rgb;vec3 rightTextureColor = texture2D(sourceImage, rightTexCoord).rgb;vec3 topTextureColor = texture2D(sourceImage, topTexCoord).rgb;vec3 bottomTextureColor = texture2D(sourceImage, bottomTexCoord).rgb;result.rgb = result.rgb * (1.0 + 4.0 * sharpen) - (leftTextureColor + rightTextureColor + topTextureColor + bottomTextureColor) * sharpen;gl_FragColor = result;}");
          if ((i == 0) || (j == 0)) {
            break label2212;
          }
          this.sharpenShaderProgram = GLES20.glCreateProgram();
          GLES20.glAttachShader(this.sharpenShaderProgram, i);
          GLES20.glAttachShader(this.sharpenShaderProgram, j);
          GLES20.glBindAttribLocation(this.sharpenShaderProgram, 0, "position");
          GLES20.glBindAttribLocation(this.sharpenShaderProgram, 1, "inputTexCoord");
          GLES20.glLinkProgram(this.sharpenShaderProgram);
          localObject1 = new int[1];
          GLES20.glGetProgramiv(this.sharpenShaderProgram, 35714, (int[])localObject1, 0);
          if (localObject1[0] != 0) {
            break label2125;
          }
          GLES20.glDeleteProgram(this.sharpenShaderProgram);
          this.sharpenShaderProgram = 0;
          label1135:
          i = loadShader(35633, "attribute vec4 position;attribute vec4 inputTexCoord;uniform highp float texelWidthOffset;uniform highp float texelHeightOffset;varying vec2 blurCoordinates[9];void main() {gl_Position = position;vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);blurCoordinates[0] = inputTexCoord.xy;blurCoordinates[1] = inputTexCoord.xy + singleStepOffset * 1.458430;blurCoordinates[2] = inputTexCoord.xy - singleStepOffset * 1.458430;blurCoordinates[3] = inputTexCoord.xy + singleStepOffset * 3.403985;blurCoordinates[4] = inputTexCoord.xy - singleStepOffset * 3.403985;blurCoordinates[5] = inputTexCoord.xy + singleStepOffset * 5.351806;blurCoordinates[6] = inputTexCoord.xy - singleStepOffset * 5.351806;blurCoordinates[7] = inputTexCoord.xy + singleStepOffset * 7.302940;blurCoordinates[8] = inputTexCoord.xy - singleStepOffset * 7.302940;}");
          j = loadShader(35632, "uniform sampler2D sourceImage;varying highp vec2 blurCoordinates[9];void main() {lowp vec4 sum = vec4(0.0);sum += texture2D(sourceImage, blurCoordinates[0]) * 0.133571;sum += texture2D(sourceImage, blurCoordinates[1]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[2]) * 0.233308;sum += texture2D(sourceImage, blurCoordinates[3]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[4]) * 0.135928;sum += texture2D(sourceImage, blurCoordinates[5]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[6]) * 0.051383;sum += texture2D(sourceImage, blurCoordinates[7]) * 0.012595;sum += texture2D(sourceImage, blurCoordinates[8]) * 0.012595;gl_FragColor = sum;}");
          if ((i == 0) || (j == 0)) {
            break label2291;
          }
          this.blurShaderProgram = GLES20.glCreateProgram();
          GLES20.glAttachShader(this.blurShaderProgram, i);
          GLES20.glAttachShader(this.blurShaderProgram, j);
          GLES20.glBindAttribLocation(this.blurShaderProgram, 0, "position");
          GLES20.glBindAttribLocation(this.blurShaderProgram, 1, "inputTexCoord");
          GLES20.glLinkProgram(this.blurShaderProgram);
          localObject1 = new int[1];
          GLES20.glGetProgramiv(this.blurShaderProgram, 35714, (int[])localObject1, 0);
          if (localObject1[0] != 0) {
            break label2218;
          }
          GLES20.glDeleteProgram(this.blurShaderProgram);
          this.blurShaderProgram = 0;
          label1249:
          i = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
          j = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float angle;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = abs((texCoordToUse.x - excludePoint.x) * aspectRatio * cos(angle) + (texCoordToUse.y - excludePoint.y) * sin(angle));gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}");
          if ((i == 0) || (j == 0)) {
            break label2426;
          }
          this.linearBlurShaderProgram = GLES20.glCreateProgram();
          GLES20.glAttachShader(this.linearBlurShaderProgram, i);
          GLES20.glAttachShader(this.linearBlurShaderProgram, j);
          GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 0, "position");
          GLES20.glBindAttribLocation(this.linearBlurShaderProgram, 1, "inputTexCoord");
          GLES20.glLinkProgram(this.linearBlurShaderProgram);
          localObject1 = new int[1];
          GLES20.glGetProgramiv(this.linearBlurShaderProgram, 35714, (int[])localObject1, 0);
          if (localObject1[0] != 0) {
            break label2297;
          }
          GLES20.glDeleteProgram(this.linearBlurShaderProgram);
          this.linearBlurShaderProgram = 0;
          label1363:
          i = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
          j = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform lowp float excludeSize;uniform lowp vec2 excludePoint;uniform lowp float excludeBlurSize;uniform highp float aspectRatio;void main() {lowp vec4 sharpImageColor = texture2D(sourceImage, texCoord);lowp vec4 blurredImageColor = texture2D(inputImageTexture2, texCoord);highp vec2 texCoordToUse = vec2(texCoord.x, (texCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));highp float distanceFromCenter = distance(excludePoint, texCoordToUse);gl_FragColor = mix(sharpImageColor, blurredImageColor, smoothstep(excludeSize - excludeBlurSize, excludeSize, distanceFromCenter));}");
          if ((i == 0) || (j == 0)) {
            break label2547;
          }
          this.radialBlurShaderProgram = GLES20.glCreateProgram();
          GLES20.glAttachShader(this.radialBlurShaderProgram, i);
          GLES20.glAttachShader(this.radialBlurShaderProgram, j);
          GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 0, "position");
          GLES20.glBindAttribLocation(this.radialBlurShaderProgram, 1, "inputTexCoord");
          GLES20.glLinkProgram(this.radialBlurShaderProgram);
          localObject1 = new int[1];
          GLES20.glGetProgramiv(this.radialBlurShaderProgram, 35714, (int[])localObject1, 0);
          if (localObject1[0] != 0) {
            break label2432;
          }
          GLES20.glDeleteProgram(this.radialBlurShaderProgram);
          this.radialBlurShaderProgram = 0;
          label1477:
          i = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
          j = loadShader(35632, "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;vec3 rgb_to_hsv(vec3 c) {vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);float d = q.x - min(q.w, q.y);float e = 1.0e-10;return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);}void main() {vec4 texel = texture2D(sourceImage, texCoord);gl_FragColor = vec4(rgb_to_hsv(texel.rgb), texel.a);}");
          if ((i == 0) || (j == 0)) {
            break label2598;
          }
          this.rgbToHsvShaderProgram = GLES20.glCreateProgram();
          GLES20.glAttachShader(this.rgbToHsvShaderProgram, i);
          GLES20.glAttachShader(this.rgbToHsvShaderProgram, j);
          GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 0, "position");
          GLES20.glBindAttribLocation(this.rgbToHsvShaderProgram, 1, "inputTexCoord");
          GLES20.glLinkProgram(this.rgbToHsvShaderProgram);
          localObject1 = new int[1];
          GLES20.glGetProgramiv(this.rgbToHsvShaderProgram, 35714, (int[])localObject1, 0);
          if (localObject1[0] != 0) {
            break label2553;
          }
          GLES20.glDeleteProgram(this.rgbToHsvShaderProgram);
          this.rgbToHsvShaderProgram = 0;
          label1591:
          i = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
          j = loadShader(35632, "precision highp float;varying vec2 texCoord;uniform sampler2D sourceImage;uniform sampler2D inputImageTexture2;uniform float intensity;float enhance(float value) {const vec2 offset = vec2(0.001953125, 0.03125);value = value + offset.x;vec2 coord = (clamp(texCoord, 0.125, 1.0 - 0.125001) - 0.125) * 4.0;vec2 frac = fract(coord);coord = floor(coord);float p00 = float(coord.y * 4.0 + coord.x) * 0.0625 + offset.y;float p01 = float(coord.y * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;float p10 = float((coord.y + 1.0) * 4.0 + coord.x) * 0.0625 + offset.y;float p11 = float((coord.y + 1.0) * 4.0 + coord.x + 1.0) * 0.0625 + offset.y;vec3 c00 = texture2D(inputImageTexture2, vec2(value, p00)).rgb;vec3 c01 = texture2D(inputImageTexture2, vec2(value, p01)).rgb;vec3 c10 = texture2D(inputImageTexture2, vec2(value, p10)).rgb;vec3 c11 = texture2D(inputImageTexture2, vec2(value, p11)).rgb;float c1 = ((c00.r - c00.g) / (c00.b - c00.g));float c2 = ((c01.r - c01.g) / (c01.b - c01.g));float c3 = ((c10.r - c10.g) / (c10.b - c10.g));float c4 = ((c11.r - c11.g) / (c11.b - c11.g));float c1_2 = mix(c1, c2, frac.x);float c3_4 = mix(c3, c4, frac.x);return mix(c1_2, c3_4, frac.y);}vec3 hsv_to_rgb(vec3 c) {vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);}void main() {vec4 texel = texture2D(sourceImage, texCoord);vec4 hsv = texel;hsv.y = min(1.0, hsv.y * 1.2);hsv.z = min(1.0, enhance(hsv.z) * 1.1);gl_FragColor = vec4(hsv_to_rgb(mix(texel.xyz, hsv.xyz, intensity)), texel.w);}");
          if ((i == 0) || (j == 0)) {
            break label2677;
          }
          this.enhanceShaderProgram = GLES20.glCreateProgram();
          GLES20.glAttachShader(this.enhanceShaderProgram, i);
          GLES20.glAttachShader(this.enhanceShaderProgram, j);
          GLES20.glBindAttribLocation(this.enhanceShaderProgram, 0, "position");
          GLES20.glBindAttribLocation(this.enhanceShaderProgram, 1, "inputTexCoord");
          GLES20.glLinkProgram(this.enhanceShaderProgram);
          localObject1 = new int[1];
          GLES20.glGetProgramiv(this.enhanceShaderProgram, 35714, (int[])localObject1, 0);
          if (localObject1[0] != 0) {
            break label2604;
          }
          GLES20.glDeleteProgram(this.enhanceShaderProgram);
          this.enhanceShaderProgram = 0;
          label1705:
          i = loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
          j = loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}");
          if ((i == 0) || (j == 0)) {
            break label2728;
          }
          this.simpleShaderProgram = GLES20.glCreateProgram();
          GLES20.glAttachShader(this.simpleShaderProgram, i);
          GLES20.glAttachShader(this.simpleShaderProgram, j);
          GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
          GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
          GLES20.glLinkProgram(this.simpleShaderProgram);
          localObject1 = new int[1];
          GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, (int[])localObject1, 0);
          if (localObject1[0] != 0) {
            break label2683;
          }
          GLES20.glDeleteProgram(this.simpleShaderProgram);
          this.simpleShaderProgram = 0;
        }
      }
      for (;;)
      {
        if (this.currentBitmap != null) {
          loadTexture(this.currentBitmap);
        }
        return true;
        this.positionHandle = GLES20.glGetAttribLocation(this.toolsShaderProgram, "position");
        this.inputTexCoordHandle = GLES20.glGetAttribLocation(this.toolsShaderProgram, "inputTexCoord");
        this.sourceImageHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "sourceImage");
        this.shadowsHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadows");
        this.highlightsHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlights");
        this.exposureHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "exposure");
        this.contrastHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "contrast");
        this.saturationHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "saturation");
        this.warmthHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "warmth");
        this.vignetteHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "vignette");
        this.grainHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "grain");
        this.widthHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "width");
        this.heightHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "height");
        this.curvesImageHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "curvesImage");
        this.skipToneHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "skipTone");
        this.fadeAmountHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "fadeAmount");
        this.shadowsTintIntensityHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadowsTintIntensity");
        this.highlightsTintIntensityHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlightsTintIntensity");
        this.shadowsTintColorHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "shadowsTintColor");
        this.highlightsTintColorHandle = GLES20.glGetUniformLocation(this.toolsShaderProgram, "highlightsTintColor");
        break;
        finish();
        return false;
        label2125:
        this.sharpenPositionHandle = GLES20.glGetAttribLocation(this.sharpenShaderProgram, "position");
        this.sharpenInputTexCoordHandle = GLES20.glGetAttribLocation(this.sharpenShaderProgram, "inputTexCoord");
        this.sharpenSourceImageHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "sourceImage");
        this.sharpenWidthHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "inputWidth");
        this.sharpenHeightHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "inputHeight");
        this.sharpenHandle = GLES20.glGetUniformLocation(this.sharpenShaderProgram, "sharpen");
        break label1135;
        label2212:
        finish();
        return false;
        label2218:
        this.blurPositionHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "position");
        this.blurInputTexCoordHandle = GLES20.glGetAttribLocation(this.blurShaderProgram, "inputTexCoord");
        this.blurSourceImageHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "sourceImage");
        this.blurWidthHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelWidthOffset");
        this.blurHeightHandle = GLES20.glGetUniformLocation(this.blurShaderProgram, "texelHeightOffset");
        break label1249;
        label2291:
        finish();
        return false;
        label2297:
        this.linearBlurPositionHandle = GLES20.glGetAttribLocation(this.linearBlurShaderProgram, "position");
        this.linearBlurInputTexCoordHandle = GLES20.glGetAttribLocation(this.linearBlurShaderProgram, "inputTexCoord");
        this.linearBlurSourceImageHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "sourceImage");
        this.linearBlurSourceImage2Handle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "inputImageTexture2");
        this.linearBlurExcludeSizeHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludeSize");
        this.linearBlurExcludePointHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludePoint");
        this.linearBlurExcludeBlurSizeHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "excludeBlurSize");
        this.linearBlurAngleHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "angle");
        this.linearBlurAspectRatioHandle = GLES20.glGetUniformLocation(this.linearBlurShaderProgram, "aspectRatio");
        break label1363;
        label2426:
        finish();
        return false;
        label2432:
        this.radialBlurPositionHandle = GLES20.glGetAttribLocation(this.radialBlurShaderProgram, "position");
        this.radialBlurInputTexCoordHandle = GLES20.glGetAttribLocation(this.radialBlurShaderProgram, "inputTexCoord");
        this.radialBlurSourceImageHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "sourceImage");
        this.radialBlurSourceImage2Handle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "inputImageTexture2");
        this.radialBlurExcludeSizeHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludeSize");
        this.radialBlurExcludePointHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludePoint");
        this.radialBlurExcludeBlurSizeHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "excludeBlurSize");
        this.radialBlurAspectRatioHandle = GLES20.glGetUniformLocation(this.radialBlurShaderProgram, "aspectRatio");
        break label1477;
        label2547:
        finish();
        return false;
        label2553:
        this.rgbToHsvPositionHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "position");
        this.rgbToHsvInputTexCoordHandle = GLES20.glGetAttribLocation(this.rgbToHsvShaderProgram, "inputTexCoord");
        this.rgbToHsvSourceImageHandle = GLES20.glGetUniformLocation(this.rgbToHsvShaderProgram, "sourceImage");
        break label1591;
        label2598:
        finish();
        return false;
        label2604:
        this.enhancePositionHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "position");
        this.enhanceInputTexCoordHandle = GLES20.glGetAttribLocation(this.enhanceShaderProgram, "inputTexCoord");
        this.enhanceSourceImageHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "sourceImage");
        this.enhanceIntensityHandle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "intensity");
        this.enhanceInputImageTexture2Handle = GLES20.glGetUniformLocation(this.enhanceShaderProgram, "inputImageTexture2");
        break label1705;
        label2677:
        finish();
        return false;
        label2683:
        this.simplePositionHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "position");
        this.simpleInputTexCoordHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "inputTexCoord");
        this.simpleSourceImageHandle = GLES20.glGetUniformLocation(this.simpleShaderProgram, "sourceImage");
      }
      label2728:
      finish();
      return false;
    }
    
    private int loadShader(int paramInt, String paramString)
    {
      int i = GLES20.glCreateShader(paramInt);
      GLES20.glShaderSource(i, paramString);
      GLES20.glCompileShader(i);
      paramString = new int[1];
      GLES20.glGetShaderiv(i, 35713, paramString, 0);
      paramInt = i;
      if (paramString[0] == 0)
      {
        FileLog.e("tmessages", GLES20.glGetShaderInfoLog(i));
        GLES20.glDeleteShader(i);
        paramInt = 0;
      }
      return paramInt;
    }
    
    private void loadTexture(Bitmap paramBitmap)
    {
      this.renderBufferWidth = paramBitmap.getWidth();
      this.renderBufferHeight = paramBitmap.getHeight();
      float f3 = AndroidUtilities.getPhotoSize();
      float f1;
      float f2;
      if ((this.renderBufferWidth > f3) || (this.renderBufferHeight > f3) || (PhotoFilterView.this.orientation % 360 != 0))
      {
        f1 = 1.0F;
        if ((this.renderBufferWidth > f3) || (this.renderBufferHeight > f3))
        {
          f1 = f3 / paramBitmap.getWidth();
          f2 = f3 / paramBitmap.getHeight();
          if (f1 >= f2) {
            break label459;
          }
          this.renderBufferWidth = ((int)f3);
          this.renderBufferHeight = ((int)(paramBitmap.getHeight() * f1));
        }
      }
      for (;;)
      {
        if ((PhotoFilterView.this.orientation % 360 == 90) || (PhotoFilterView.this.orientation % 360 == 270))
        {
          int i = this.renderBufferWidth;
          this.renderBufferWidth = this.renderBufferHeight;
          this.renderBufferHeight = i;
        }
        this.currentBitmap = createBitmap(paramBitmap, this.renderBufferWidth, this.renderBufferHeight, f1);
        GLES20.glGenFramebuffers(3, this.renderFrameBuffer, 0);
        GLES20.glGenTextures(3, this.renderTexture, 0);
        GLES20.glBindTexture(3553, this.renderTexture[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, null);
        GLES20.glBindTexture(3553, this.renderTexture[1]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLUtils.texImage2D(3553, 0, this.currentBitmap, 0);
        GLES20.glBindTexture(3553, this.renderTexture[2]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6408, this.renderBufferWidth, this.renderBufferHeight, 0, 6408, 5121, null);
        return;
        label459:
        this.renderBufferHeight = ((int)f3);
        this.renderBufferWidth = ((int)(paramBitmap.getWidth() * f2));
        f1 = f2;
      }
    }
    
    public void finish()
    {
      if (this.eglSurface != null)
      {
        this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
        this.eglSurface = null;
      }
      if (this.eglContext != null)
      {
        this.egl10.eglDestroyContext(this.eglDisplay, this.eglContext);
        this.eglContext = null;
      }
      if (this.eglDisplay != null)
      {
        this.egl10.eglTerminate(this.eglDisplay);
        this.eglDisplay = null;
      }
    }
    
    public Bitmap getTexture()
    {
      if (!this.initied) {
        return null;
      }
      final Semaphore localSemaphore = new Semaphore(0);
      final Bitmap[] arrayOfBitmap = new Bitmap[1];
      try
      {
        postRunnable(new Runnable()
        {
          public void run()
          {
            int i = 1;
            GLES20.glBindFramebuffer(36160, PhotoFilterView.EGLThread.this.renderFrameBuffer[1]);
            int[] arrayOfInt = PhotoFilterView.EGLThread.this.renderTexture;
            if (PhotoFilterView.EGLThread.this.blured) {
              i = 0;
            }
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, arrayOfInt[i], 0);
            GLES20.glClear(0);
            arrayOfBitmap[0] = PhotoFilterView.EGLThread.this.getRenderBufferBitmap();
            localSemaphore.release();
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glClear(0);
          }
        });
        localSemaphore.acquire();
        return arrayOfBitmap[0];
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
    
    public void requestRender(final boolean paramBoolean)
    {
      postRunnable(new Runnable()
      {
        public void run()
        {
          if (!PhotoFilterView.EGLThread.this.needUpdateBlurTexture) {
            PhotoFilterView.EGLThread.access$4702(PhotoFilterView.EGLThread.this, paramBoolean);
          }
          long l = System.currentTimeMillis();
          if (Math.abs(PhotoFilterView.EGLThread.this.lastRenderCallTime - l) > 30L)
          {
            PhotoFilterView.EGLThread.access$4802(PhotoFilterView.EGLThread.this, l);
            PhotoFilterView.EGLThread.this.drawRunnable.run();
          }
        }
      });
    }
    
    public void run()
    {
      this.initied = initGL();
      super.run();
    }
    
    public void setSurfaceTextureSize(int paramInt1, int paramInt2)
    {
      this.surfaceWidth = paramInt1;
      this.surfaceHeight = paramInt2;
    }
    
    public void shutdown()
    {
      postRunnable(new Runnable()
      {
        public void run()
        {
          PhotoFilterView.EGLThread.this.finish();
          PhotoFilterView.EGLThread.access$4602(PhotoFilterView.EGLThread.this, null);
          Looper localLooper = Looper.myLooper();
          if (localLooper != null) {
            localLooper.quit();
          }
        }
      });
    }
  }
  
  public class ToolsAdapter
    extends RecyclerView.Adapter
  {
    private Context mContext;
    
    public ToolsAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return 14;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      Object localObject = (Holder)paramViewHolder;
      if (paramInt == PhotoFilterView.this.enhanceTool) {
        ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838012, LocaleController.getString("Enhance", 2131165618), PhotoFilterView.this.enhanceValue);
      }
      do
      {
        return;
        if (paramInt == PhotoFilterView.this.highlightsTool)
        {
          ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838015, LocaleController.getString("Highlights", 2131165730), PhotoFilterView.this.highlightsValue);
          return;
        }
        if (paramInt == PhotoFilterView.this.contrastTool)
        {
          ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838008, LocaleController.getString("Contrast", 2131165523), PhotoFilterView.this.contrastValue);
          return;
        }
        if (paramInt == PhotoFilterView.this.exposureTool)
        {
          ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838007, LocaleController.getString("Exposure", 2131165628), PhotoFilterView.this.exposureValue);
          return;
        }
        if (paramInt == PhotoFilterView.this.warmthTool)
        {
          ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838021, LocaleController.getString("Warmth", 2131166388), PhotoFilterView.this.warmthValue);
          return;
        }
        if (paramInt == PhotoFilterView.this.saturationTool)
        {
          ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838017, LocaleController.getString("Saturation", 2131166198), PhotoFilterView.this.saturationValue);
          return;
        }
        if (paramInt == PhotoFilterView.this.vignetteTool)
        {
          ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838020, LocaleController.getString("Vignette", 2131166386), PhotoFilterView.this.vignetteValue);
          return;
        }
        if (paramInt == PhotoFilterView.this.shadowsTool)
        {
          ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838018, LocaleController.getString("Shadows", 2131166272), PhotoFilterView.this.shadowsValue);
          return;
        }
        if (paramInt == PhotoFilterView.this.grainTool)
        {
          ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838014, LocaleController.getString("Grain", 2131165717), PhotoFilterView.this.grainValue);
          return;
        }
        if (paramInt == PhotoFilterView.this.sharpenTool)
        {
          ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838011, LocaleController.getString("Sharpen", 2131166287), PhotoFilterView.this.sharpenValue);
          return;
        }
        String str;
        if (paramInt == PhotoFilterView.this.tintTool)
        {
          localObject = (PhotoEditToolCell)((Holder)localObject).itemView;
          str = LocaleController.getString("Tint", 2131166340);
          if ((PhotoFilterView.this.tintHighlightsColor != 0) || (PhotoFilterView.this.tintShadowsColor != 0)) {}
          for (paramViewHolder = "◆";; paramViewHolder = "")
          {
            ((PhotoEditToolCell)localObject).setIconAndTextAndValue(2130838019, str, paramViewHolder);
            return;
          }
        }
        if (paramInt == PhotoFilterView.this.fadeTool)
        {
          ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838013, LocaleController.getString("Fade", 2131165630), PhotoFilterView.this.fadeValue);
          return;
        }
        if (paramInt == PhotoFilterView.this.curvesTool)
        {
          localObject = (PhotoEditToolCell)((Holder)localObject).itemView;
          str = LocaleController.getString("Curves", 2131165539);
          if (PhotoFilterView.this.curvesToolValue.shouldBeSkipped()) {}
          for (paramViewHolder = "";; paramViewHolder = "◆")
          {
            ((PhotoEditToolCell)localObject).setIconAndTextAndValue(2130838010, str, paramViewHolder);
            return;
          }
        }
      } while (paramInt != PhotoFilterView.this.blurTool);
      paramViewHolder = "";
      if (PhotoFilterView.this.blurType == 1) {
        paramViewHolder = "R";
      }
      for (;;)
      {
        ((PhotoEditToolCell)((Holder)localObject).itemView).setIconAndTextAndValue(2130838006, LocaleController.getString("Blur", 2131165361), paramViewHolder);
        return;
        if (PhotoFilterView.this.blurType == 2) {
          paramViewHolder = "L";
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new Holder(new PhotoEditToolCell(this.mContext));
    }
    
    private class Holder
      extends RecyclerView.ViewHolder
    {
      public Holder(View paramView)
      {
        super();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PhotoFilterView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */