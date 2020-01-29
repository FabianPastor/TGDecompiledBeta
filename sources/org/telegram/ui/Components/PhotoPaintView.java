package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Looper;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Paint.Brush;
import org.telegram.ui.Components.Paint.Painting;
import org.telegram.ui.Components.Paint.PhotoFace;
import org.telegram.ui.Components.Paint.RenderView;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.UndoStore;
import org.telegram.ui.Components.Paint.Views.ColorPicker;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;
import org.telegram.ui.Components.Paint.Views.EntitiesContainerView;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Paint.Views.StickerView;
import org.telegram.ui.Components.Paint.Views.TextPaintView;
import org.telegram.ui.Components.StickerMasksView;
import org.telegram.ui.PhotoViewer;

@SuppressLint({"NewApi"})
public class PhotoPaintView extends FrameLayout implements EntityView.EntityViewDelegate {
    private static final int gallery_menu_done = 1;
    private Bitmap bitmapToEdit;
    private Brush[] brushes = {new Brush.Radial(), new Brush.Elliptical(), new Brush.Neon()};
    private TextView cancelTextView;
    /* access modifiers changed from: private */
    public ColorPicker colorPicker;
    private Animator colorPickerAnimator;
    int currentBrush;
    /* access modifiers changed from: private */
    public EntityView currentEntityView;
    private FrameLayout curtainView;
    /* access modifiers changed from: private */
    public FrameLayout dimView;
    private TextView doneTextView;
    private Point editedTextPosition;
    private float editedTextRotation;
    private float editedTextScale;
    private boolean editingText;
    private EntitiesContainerView entitiesView;
    private ArrayList<PhotoFace> faces;
    private String initialText;
    private int orientation;
    private ImageView paintButton;
    private Size paintingSize;
    private boolean pickingSticker;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private DispatchQueue queue = new DispatchQueue("Paint");
    private RenderView renderView;
    private boolean selectedStroke = true;
    private FrameLayout selectionContainerView;
    /* access modifiers changed from: private */
    public StickerMasksView stickersView;
    /* access modifiers changed from: private */
    public FrameLayout textDimView;
    private FrameLayout toolsView;
    /* access modifiers changed from: private */
    public UndoStore undoStore;

    public PhotoPaintView(Context context, Bitmap bitmap, int i) {
        super(context);
        this.bitmapToEdit = bitmap;
        this.orientation = i;
        this.undoStore = new UndoStore();
        this.undoStore.setDelegate(new UndoStore.UndoStoreDelegate() {
            public final void historyChanged() {
                PhotoPaintView.this.lambda$new$0$PhotoPaintView();
            }
        });
        this.curtainView = new FrameLayout(context);
        this.curtainView.setBackgroundColor(-16777216);
        this.curtainView.setVisibility(4);
        addView(this.curtainView);
        this.renderView = new RenderView(context, new Painting(getPaintingSize()), bitmap, this.orientation);
        this.renderView.setDelegate(new RenderView.RenderViewDelegate() {
            public void onBeganDrawing() {
                if (PhotoPaintView.this.currentEntityView != null) {
                    boolean unused = PhotoPaintView.this.selectEntity((EntityView) null);
                }
            }

            public void onFinishedDrawing(boolean z) {
                PhotoPaintView.this.colorPicker.setUndoEnabled(PhotoPaintView.this.undoStore.canUndo());
            }

            public boolean shouldDraw() {
                boolean z = PhotoPaintView.this.currentEntityView == null;
                if (!z) {
                    boolean unused = PhotoPaintView.this.selectEntity((EntityView) null);
                }
                return z;
            }
        });
        this.renderView.setUndoStore(this.undoStore);
        this.renderView.setQueue(this.queue);
        this.renderView.setVisibility(4);
        this.renderView.setBrush(this.brushes[0]);
        addView(this.renderView, LayoutHelper.createFrame(-1, -1, 51));
        this.entitiesView = new EntitiesContainerView(context, new EntitiesContainerView.EntitiesContainerViewDelegate() {
            public boolean shouldReceiveTouches() {
                return PhotoPaintView.this.textDimView.getVisibility() != 0;
            }

            public EntityView onSelectedEntityRequest() {
                return PhotoPaintView.this.currentEntityView;
            }

            public void onEntityDeselect() {
                boolean unused = PhotoPaintView.this.selectEntity((EntityView) null);
            }
        });
        this.entitiesView.setPivotX(0.0f);
        this.entitiesView.setPivotY(0.0f);
        addView(this.entitiesView);
        this.dimView = new FrameLayout(context);
        this.dimView.setAlpha(0.0f);
        this.dimView.setBackgroundColor(NUM);
        this.dimView.setVisibility(8);
        addView(this.dimView);
        this.textDimView = new FrameLayout(context);
        this.textDimView.setAlpha(0.0f);
        this.textDimView.setBackgroundColor(NUM);
        this.textDimView.setVisibility(8);
        this.textDimView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoPaintView.this.lambda$new$1$PhotoPaintView(view);
            }
        });
        this.selectionContainerView = new FrameLayout(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return false;
            }
        };
        addView(this.selectionContainerView);
        this.colorPicker = new ColorPicker(context);
        addView(this.colorPicker);
        this.colorPicker.setDelegate(new ColorPicker.ColorPickerDelegate() {
            public void onBeganColorPicking() {
                if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView)) {
                    PhotoPaintView.this.setDimVisibility(true);
                }
            }

            public void onColorValueChanged() {
                PhotoPaintView photoPaintView = PhotoPaintView.this;
                photoPaintView.setCurrentSwatch(photoPaintView.colorPicker.getSwatch(), false);
            }

            public void onFinishedColorPicking() {
                PhotoPaintView photoPaintView = PhotoPaintView.this;
                photoPaintView.setCurrentSwatch(photoPaintView.colorPicker.getSwatch(), false);
                if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView)) {
                    PhotoPaintView.this.setDimVisibility(false);
                }
            }

            public void onSettingsPressed() {
                if (PhotoPaintView.this.currentEntityView == null) {
                    PhotoPaintView.this.showBrushSettings();
                } else if (PhotoPaintView.this.currentEntityView instanceof StickerView) {
                    PhotoPaintView.this.mirrorSticker();
                } else if (PhotoPaintView.this.currentEntityView instanceof TextPaintView) {
                    PhotoPaintView.this.showTextSettings();
                }
            }

            public void onUndoPressed() {
                PhotoPaintView.this.undoStore.undo();
            }
        });
        this.toolsView = new FrameLayout(context);
        this.toolsView.setBackgroundColor(-16777216);
        addView(this.toolsView, LayoutHelper.createFrame(-1, 48, 83));
        this.cancelTextView = new TextView(context);
        this.cancelTextView.setTextSize(1, 14.0f);
        this.cancelTextView.setTextColor(-1);
        this.cancelTextView.setGravity(17);
        this.cancelTextView.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
        this.cancelTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelTextView.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
        this.cancelTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.toolsView.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
        this.doneTextView = new TextView(context);
        this.doneTextView.setTextSize(1, 14.0f);
        this.doneTextView.setTextColor(Theme.getColor("dialogFloatingButton"));
        this.doneTextView.setGravity(17);
        this.doneTextView.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
        this.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneTextView.setText(LocaleController.getString("Done", NUM).toUpperCase());
        this.doneTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.toolsView.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
        this.paintButton = new ImageView(context);
        this.paintButton.setScaleType(ImageView.ScaleType.CENTER);
        this.paintButton.setImageResource(NUM);
        this.paintButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.toolsView.addView(this.paintButton, LayoutHelper.createFrame(54, -1.0f, 17, 0.0f, 0.0f, 56.0f, 0.0f));
        this.paintButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoPaintView.this.lambda$new$2$PhotoPaintView(view);
            }
        });
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(NUM);
        imageView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.toolsView.addView(imageView, LayoutHelper.createFrame(54, -1, 17));
        imageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoPaintView.this.lambda$new$3$PhotoPaintView(view);
            }
        });
        ImageView imageView2 = new ImageView(context);
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        imageView2.setImageResource(NUM);
        imageView2.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.toolsView.addView(imageView2, LayoutHelper.createFrame(54, -1.0f, 17, 56.0f, 0.0f, 0.0f, 0.0f));
        imageView2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoPaintView.this.lambda$new$4$PhotoPaintView(view);
            }
        });
        this.colorPicker.setUndoEnabled(false);
        setCurrentSwatch(this.colorPicker.getSwatch(), false);
        updateSettingsButton();
    }

    public /* synthetic */ void lambda$new$0$PhotoPaintView() {
        this.colorPicker.setUndoEnabled(this.undoStore.canUndo());
    }

    public /* synthetic */ void lambda$new$1$PhotoPaintView(View view) {
        closeTextEnter(true);
    }

    public /* synthetic */ void lambda$new$2$PhotoPaintView(View view) {
        selectEntity((EntityView) null);
    }

    public /* synthetic */ void lambda$new$3$PhotoPaintView(View view) {
        openStickersView();
    }

    public /* synthetic */ void lambda$new$4$PhotoPaintView(View view) {
        createText();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentEntityView != null) {
            if (this.editingText) {
                closeTextEnter(true);
            } else {
                selectEntity((EntityView) null);
            }
        }
        return true;
    }

    private Size getPaintingSize() {
        Size size = this.paintingSize;
        if (size != null) {
            return size;
        }
        float height = (float) (isSidewardOrientation() ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
        float width = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        Size size2 = new Size(height, width);
        size2.width = 1280.0f;
        size2.height = (float) Math.floor((double) ((size2.width * width) / height));
        if (size2.height > 1280.0f) {
            size2.height = 1280.0f;
            size2.width = (float) Math.floor((double) ((size2.height * height) / width));
        }
        this.paintingSize = size2;
        return size2;
    }

    private boolean isSidewardOrientation() {
        int i = this.orientation;
        return i % 360 == 90 || i % 360 == 270;
    }

    private void updateSettingsButton() {
        EntityView entityView = this.currentEntityView;
        int i = NUM;
        if (entityView != null) {
            if (entityView instanceof StickerView) {
                i = NUM;
            } else if (entityView instanceof TextPaintView) {
                i = NUM;
            }
            this.paintButton.setImageResource(NUM);
            this.paintButton.setColorFilter((ColorFilter) null);
        } else {
            this.paintButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            this.paintButton.setImageResource(NUM);
        }
        this.colorPicker.setSettingsButtonImage(i);
    }

    public void updateColors() {
        ImageView imageView = this.paintButton;
        if (!(imageView == null || imageView.getColorFilter() == null)) {
            this.paintButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        }
        TextView textView = this.doneTextView;
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogFloatingButton"));
        }
    }

    public void init() {
        this.renderView.setVisibility(0);
        detectFaces();
    }

    public void shutdown() {
        this.renderView.shutdown();
        this.entitiesView.setVisibility(8);
        this.selectionContainerView.setVisibility(8);
        this.queue.postRunnable($$Lambda$PhotoPaintView$BRjjxWV4_4jng8wjr2bRzoAFJ_A.INSTANCE);
    }

    static /* synthetic */ void lambda$shutdown$5() {
        Looper myLooper = Looper.myLooper();
        if (myLooper != null) {
            myLooper.quit();
        }
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public TextView getDoneTextView() {
        return this.doneTextView;
    }

    public TextView getCancelTextView() {
        return this.cancelTextView;
    }

    public ColorPicker getColorPicker() {
        return this.colorPicker;
    }

    private boolean hasChanges() {
        return this.undoStore.canUndo() || this.entitiesView.entitiesCount() > 0;
    }

    public Bitmap getBitmap() {
        Bitmap resultBitmap = this.renderView.getResultBitmap();
        if (resultBitmap != null && this.entitiesView.entitiesCount() > 0) {
            Canvas canvas = new Canvas(resultBitmap);
            for (int i = 0; i < this.entitiesView.getChildCount(); i++) {
                View childAt = this.entitiesView.getChildAt(i);
                canvas.save();
                if (childAt instanceof EntityView) {
                    EntityView entityView = (EntityView) childAt;
                    canvas.translate(entityView.getPosition().x, entityView.getPosition().y);
                    canvas.scale(childAt.getScaleX(), childAt.getScaleY());
                    canvas.rotate(childAt.getRotation());
                    canvas.translate((float) ((-entityView.getWidth()) / 2), (float) ((-entityView.getHeight()) / 2));
                    if (childAt instanceof TextPaintView) {
                        Bitmap createBitmap = Bitmaps.createBitmap(childAt.getWidth(), childAt.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas2 = new Canvas(createBitmap);
                        childAt.draw(canvas2);
                        canvas.drawBitmap(createBitmap, (Rect) null, new Rect(0, 0, createBitmap.getWidth(), createBitmap.getHeight()), (Paint) null);
                        try {
                            canvas2.setBitmap((Bitmap) null);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        createBitmap.recycle();
                    } else {
                        childAt.draw(canvas);
                    }
                }
                canvas.restore();
            }
        }
        return resultBitmap;
    }

    public void maybeShowDismissalAlert(PhotoViewer photoViewer, Activity activity, Runnable runnable) {
        if (this.editingText) {
            closeTextEnter(false);
        } else if (this.pickingSticker) {
            closeStickersView();
        } else if (!hasChanges()) {
            runnable.run();
        } else if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
            builder.setMessage(LocaleController.getString("DiscardChanges", NUM));
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(runnable) {
                private final /* synthetic */ Runnable f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    this.f$0.run();
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            photoViewer.showAlertDialog(builder);
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentSwatch(Swatch swatch, boolean z) {
        this.renderView.setColor(swatch.color);
        this.renderView.setBrushSize(swatch.brushWeight);
        if (z) {
            this.colorPicker.setSwatch(swatch);
        }
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof TextPaintView) {
            ((TextPaintView) entityView).setSwatch(swatch);
        }
    }

    /* access modifiers changed from: private */
    public void setDimVisibility(final boolean z) {
        ObjectAnimator objectAnimator;
        if (z) {
            this.dimView.setVisibility(0);
            objectAnimator = ObjectAnimator.ofFloat(this.dimView, "alpha", new float[]{0.0f, 1.0f});
        } else {
            objectAnimator = ObjectAnimator.ofFloat(this.dimView, "alpha", new float[]{1.0f, 0.0f});
        }
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (!z) {
                    PhotoPaintView.this.dimView.setVisibility(8);
                }
            }
        });
        objectAnimator.setDuration(200);
        objectAnimator.start();
    }

    private void setTextDimVisibility(final boolean z, EntityView entityView) {
        ObjectAnimator objectAnimator;
        if (z && entityView != null) {
            ViewGroup viewGroup = (ViewGroup) entityView.getParent();
            if (this.textDimView.getParent() != null) {
                ((EntitiesContainerView) this.textDimView.getParent()).removeView(this.textDimView);
            }
            viewGroup.addView(this.textDimView, viewGroup.indexOfChild(entityView));
        }
        entityView.setSelectionVisibility(!z);
        if (z) {
            this.textDimView.setVisibility(0);
            objectAnimator = ObjectAnimator.ofFloat(this.textDimView, "alpha", new float[]{0.0f, 1.0f});
        } else {
            objectAnimator = ObjectAnimator.ofFloat(this.textDimView, "alpha", new float[]{1.0f, 0.0f});
        }
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (!z) {
                    PhotoPaintView.this.textDimView.setVisibility(8);
                    if (PhotoPaintView.this.textDimView.getParent() != null) {
                        ((EntitiesContainerView) PhotoPaintView.this.textDimView.getParent()).removeView(PhotoPaintView.this.textDimView);
                    }
                }
            }
        });
        objectAnimator.setDuration(200);
        objectAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        float f;
        float f2;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        int currentActionBarHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f);
        if (this.bitmapToEdit != null) {
            float height = (float) (isSidewardOrientation() ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
            f2 = height;
            f = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        } else {
            f2 = (float) size;
            f = (float) ((size2 - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f));
        }
        float f3 = (float) size;
        float floor = (float) Math.floor((double) ((f3 * f) / f2));
        float f4 = (float) currentActionBarHeight;
        if (floor > f4) {
            f3 = (float) Math.floor((double) ((f2 * f4) / f));
            floor = f4;
        }
        this.renderView.measure(View.MeasureSpec.makeMeasureSpec((int) f3, NUM), View.MeasureSpec.makeMeasureSpec((int) floor, NUM));
        this.entitiesView.measure(View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.width, NUM), View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.height, NUM));
        this.dimView.measure(i, View.MeasureSpec.makeMeasureSpec(currentActionBarHeight, Integer.MIN_VALUE));
        this.selectionContainerView.measure(i, View.MeasureSpec.makeMeasureSpec(currentActionBarHeight, NUM));
        this.colorPicker.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(currentActionBarHeight, NUM));
        this.toolsView.measure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView != null) {
            stickerMasksView.measure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, NUM));
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float f;
        float f2;
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int i7 = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
        int currentActionBarHeight2 = ActionBar.getCurrentActionBarHeight() + i7;
        int dp = (AndroidUtilities.displaySize.y - currentActionBarHeight) - AndroidUtilities.dp(48.0f);
        if (this.bitmapToEdit != null) {
            float height = (float) (isSidewardOrientation() ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
            f2 = height;
            f = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        } else {
            f2 = (float) i5;
            f = (float) ((i6 - currentActionBarHeight) - AndroidUtilities.dp(48.0f));
        }
        float f3 = (float) i5;
        float f4 = (float) dp;
        if (((float) Math.floor((double) ((f3 * f) / f2))) > f4) {
            f3 = (float) Math.floor((double) ((f4 * f2) / f));
        }
        int ceil = (int) Math.ceil((double) ((i5 - this.renderView.getMeasuredWidth()) / 2));
        int dp2 = ((((((i6 - currentActionBarHeight2) - AndroidUtilities.dp(48.0f)) - this.renderView.getMeasuredHeight()) / 2) + currentActionBarHeight2) - ActionBar.getCurrentActionBarHeight()) + AndroidUtilities.dp(8.0f);
        RenderView renderView2 = this.renderView;
        renderView2.layout(ceil, dp2, renderView2.getMeasuredWidth() + ceil, this.renderView.getMeasuredHeight() + dp2);
        float f5 = f3 / this.paintingSize.width;
        this.entitiesView.setScaleX(f5);
        this.entitiesView.setScaleY(f5);
        EntitiesContainerView entitiesContainerView = this.entitiesView;
        entitiesContainerView.layout(ceil, dp2, entitiesContainerView.getMeasuredWidth() + ceil, this.entitiesView.getMeasuredHeight() + dp2);
        FrameLayout frameLayout = this.dimView;
        frameLayout.layout(0, i7, frameLayout.getMeasuredWidth(), this.dimView.getMeasuredHeight() + i7);
        FrameLayout frameLayout2 = this.selectionContainerView;
        frameLayout2.layout(0, i7, frameLayout2.getMeasuredWidth(), this.selectionContainerView.getMeasuredHeight() + i7);
        ColorPicker colorPicker2 = this.colorPicker;
        colorPicker2.layout(0, currentActionBarHeight2, colorPicker2.getMeasuredWidth(), this.colorPicker.getMeasuredHeight() + currentActionBarHeight2);
        FrameLayout frameLayout3 = this.toolsView;
        frameLayout3.layout(0, i6 - frameLayout3.getMeasuredHeight(), this.toolsView.getMeasuredWidth(), i6);
        this.curtainView.layout(0, 0, i5, dp);
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView != null) {
            stickerMasksView.layout(0, i7, stickerMasksView.getMeasuredWidth(), this.stickersView.getMeasuredHeight() + i7);
        }
        EntityView entityView = this.currentEntityView;
        if (entityView != null) {
            entityView.updateSelectionView();
            this.currentEntityView.setOffset(this.entitiesView.getLeft() - this.selectionContainerView.getLeft(), this.entitiesView.getTop() - this.selectionContainerView.getTop());
        }
    }

    public boolean onEntitySelected(EntityView entityView) {
        return selectEntity(entityView);
    }

    public boolean onEntityLongClicked(EntityView entityView) {
        showMenuForEntity(entityView);
        return true;
    }

    public boolean allowInteraction(EntityView entityView) {
        return !this.editingText;
    }

    private Point centerPositionForEntity() {
        Size paintingSize2 = getPaintingSize();
        return new Point(paintingSize2.width / 2.0f, paintingSize2.height / 2.0f);
    }

    private Point startPositionRelativeToEntity(EntityView entityView) {
        if (entityView != null) {
            Point position = entityView.getPosition();
            return new Point(position.x + 200.0f, position.y + 200.0f);
        }
        Point centerPositionForEntity = centerPositionForEntity();
        while (true) {
            boolean z = false;
            for (int i = 0; i < this.entitiesView.getChildCount(); i++) {
                View childAt = this.entitiesView.getChildAt(i);
                if (childAt instanceof EntityView) {
                    Point position2 = ((EntityView) childAt).getPosition();
                    if (((float) Math.sqrt(Math.pow((double) (position2.x - centerPositionForEntity.x), 2.0d) + Math.pow((double) (position2.y - centerPositionForEntity.y), 2.0d))) < 100.0f) {
                        z = true;
                    }
                }
            }
            if (!z) {
                return centerPositionForEntity;
            }
            centerPositionForEntity = new Point(centerPositionForEntity.x + 200.0f, centerPositionForEntity.y + 200.0f);
        }
    }

    public ArrayList<TLRPC.InputDocument> getMasks() {
        int childCount = this.entitiesView.getChildCount();
        ArrayList<TLRPC.InputDocument> arrayList = null;
        for (int i = 0; i < childCount; i++) {
            View childAt = this.entitiesView.getChildAt(i);
            if (childAt instanceof StickerView) {
                TLRPC.Document sticker = ((StickerView) childAt).getSticker();
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                }
                TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
                tL_inputDocument.id = sticker.id;
                tL_inputDocument.access_hash = sticker.access_hash;
                tL_inputDocument.file_reference = sticker.file_reference;
                if (tL_inputDocument.file_reference == null) {
                    tL_inputDocument.file_reference = new byte[0];
                }
                arrayList.add(tL_inputDocument);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public boolean selectEntity(EntityView entityView) {
        boolean z;
        EntityView entityView2 = this.currentEntityView;
        if (entityView2 == null) {
            z = false;
        } else if (entityView2 == entityView) {
            if (!this.editingText) {
                showMenuForEntity(entityView2);
            }
            return true;
        } else {
            entityView2.deselect();
            z = true;
        }
        this.currentEntityView = entityView;
        EntityView entityView3 = this.currentEntityView;
        if (entityView3 != null) {
            entityView3.select(this.selectionContainerView);
            this.entitiesView.bringViewToFront(this.currentEntityView);
            EntityView entityView4 = this.currentEntityView;
            if (entityView4 instanceof TextPaintView) {
                setCurrentSwatch(((TextPaintView) entityView4).getSwatch(), true);
            }
            z = true;
        }
        updateSettingsButton();
        return z;
    }

    /* access modifiers changed from: private */
    /* renamed from: removeEntity */
    public void lambda$registerRemovalUndo$7$PhotoPaintView(EntityView entityView) {
        EntityView entityView2 = this.currentEntityView;
        if (entityView == entityView2) {
            entityView2.deselect();
            if (this.editingText) {
                closeTextEnter(false);
            }
            this.currentEntityView = null;
            updateSettingsButton();
        }
        this.entitiesView.removeView(entityView);
        this.undoStore.unregisterUndo(entityView.getUUID());
    }

    private void duplicateSelectedEntity() {
        EntityView entityView = this.currentEntityView;
        if (entityView != null) {
            StickerView stickerView = null;
            Point startPositionRelativeToEntity = startPositionRelativeToEntity(entityView);
            EntityView entityView2 = this.currentEntityView;
            if (entityView2 instanceof StickerView) {
                StickerView stickerView2 = new StickerView(getContext(), (StickerView) this.currentEntityView, startPositionRelativeToEntity);
                stickerView2.setDelegate(this);
                this.entitiesView.addView(stickerView2);
                stickerView = stickerView2;
            } else if (entityView2 instanceof TextPaintView) {
                TextPaintView textPaintView = new TextPaintView(getContext(), (TextPaintView) this.currentEntityView, startPositionRelativeToEntity);
                textPaintView.setDelegate(this);
                textPaintView.setMaxWidth((int) (getPaintingSize().width - 20.0f));
                this.entitiesView.addView(textPaintView, LayoutHelper.createFrame(-2, -2.0f));
                stickerView = textPaintView;
            }
            registerRemovalUndo(stickerView);
            selectEntity(stickerView);
            updateSettingsButton();
        }
    }

    private void openStickersView() {
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView == null || stickerMasksView.getVisibility() != 0) {
            this.pickingSticker = true;
            if (this.stickersView == null) {
                this.stickersView = new StickerMasksView(getContext());
                this.stickersView.setListener(new StickerMasksView.Listener() {
                    public void onTypeChanged() {
                    }

                    public void onStickerSelected(Object obj, TLRPC.Document document) {
                        PhotoPaintView.this.closeStickersView();
                        PhotoPaintView.this.createSticker(obj, document);
                    }
                });
                addView(this.stickersView, LayoutHelper.createFrame(-1, -1, 51));
            }
            this.stickersView.setVisibility(0);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.stickersView, "alpha", new float[]{0.0f, 1.0f});
            ofFloat.setDuration(200);
            ofFloat.start();
        }
    }

    /* access modifiers changed from: private */
    public void closeStickersView() {
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView != null && stickerMasksView.getVisibility() == 0) {
            this.pickingSticker = false;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.stickersView, "alpha", new float[]{1.0f, 0.0f});
            ofFloat.setDuration(200);
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PhotoPaintView.this.stickersView.setVisibility(8);
                }
            });
            ofFloat.start();
        }
    }

    private Size baseStickerSize() {
        double d = (double) getPaintingSize().width;
        Double.isNaN(d);
        float floor = (float) Math.floor(d * 0.5d);
        return new Size(floor, floor);
    }

    private void registerRemovalUndo(EntityView entityView) {
        this.undoStore.registerUndo(entityView.getUUID(), new Runnable(entityView) {
            private final /* synthetic */ EntityView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PhotoPaintView.this.lambda$registerRemovalUndo$7$PhotoPaintView(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    public void createSticker(Object obj, TLRPC.Document document) {
        StickerPosition calculateStickerPosition = calculateStickerPosition(document);
        StickerView stickerView = new StickerView(getContext(), calculateStickerPosition.position, calculateStickerPosition.angle, calculateStickerPosition.scale, baseStickerSize(), document, obj);
        stickerView.setDelegate(this);
        this.entitiesView.addView(stickerView);
        registerRemovalUndo(stickerView);
        selectEntity(stickerView);
    }

    /* access modifiers changed from: private */
    public void mirrorSticker() {
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof StickerView) {
            ((StickerView) entityView).mirror();
        }
    }

    private int baseFontSize() {
        return (int) (getPaintingSize().width / 9.0f);
    }

    private void createText() {
        Swatch swatch = this.colorPicker.getSwatch();
        Swatch swatch2 = new Swatch(-1, 1.0f, swatch.brushWeight);
        Swatch swatch3 = new Swatch(-16777216, 0.85f, swatch.brushWeight);
        if (this.selectedStroke) {
            swatch2 = swatch3;
        }
        setCurrentSwatch(swatch2, true);
        TextPaintView textPaintView = new TextPaintView(getContext(), startPositionRelativeToEntity((EntityView) null), baseFontSize(), "", this.colorPicker.getSwatch(), this.selectedStroke);
        textPaintView.setDelegate(this);
        textPaintView.setMaxWidth((int) (getPaintingSize().width - 20.0f));
        this.entitiesView.addView(textPaintView, LayoutHelper.createFrame(-2, -2.0f));
        registerRemovalUndo(textPaintView);
        selectEntity(textPaintView);
        editSelectedTextEntity();
    }

    private void editSelectedTextEntity() {
        if ((this.currentEntityView instanceof TextPaintView) && !this.editingText) {
            this.curtainView.setVisibility(0);
            TextPaintView textPaintView = (TextPaintView) this.currentEntityView;
            this.initialText = textPaintView.getText();
            this.editingText = true;
            this.editedTextPosition = textPaintView.getPosition();
            this.editedTextRotation = textPaintView.getRotation();
            this.editedTextScale = textPaintView.getScale();
            textPaintView.setPosition(centerPositionForEntity());
            textPaintView.setRotation(0.0f);
            textPaintView.setScale(1.0f);
            this.toolsView.setVisibility(8);
            setTextDimVisibility(true, textPaintView);
            textPaintView.beginEditing();
            ((InputMethodManager) ApplicationLoader.applicationContext.getSystemService("input_method")).toggleSoftInputFromWindow(textPaintView.getFocusedView().getWindowToken(), 2, 0);
        }
    }

    public void closeTextEnter(boolean z) {
        if (this.editingText) {
            EntityView entityView = this.currentEntityView;
            if (entityView instanceof TextPaintView) {
                TextPaintView textPaintView = (TextPaintView) entityView;
                this.toolsView.setVisibility(0);
                AndroidUtilities.hideKeyboard(textPaintView.getFocusedView());
                textPaintView.getFocusedView().clearFocus();
                textPaintView.endEditing();
                if (!z) {
                    textPaintView.setText(this.initialText);
                }
                if (textPaintView.getText().trim().length() == 0) {
                    this.entitiesView.removeView(textPaintView);
                    selectEntity((EntityView) null);
                } else {
                    textPaintView.setPosition(this.editedTextPosition);
                    textPaintView.setRotation(this.editedTextRotation);
                    textPaintView.setScale(this.editedTextScale);
                    this.editedTextPosition = null;
                    this.editedTextRotation = 0.0f;
                    this.editedTextScale = 0.0f;
                }
                setTextDimVisibility(false, textPaintView);
                this.editingText = false;
                this.initialText = null;
                this.curtainView.setVisibility(8);
            }
        }
    }

    private void setBrush(int i) {
        RenderView renderView2 = this.renderView;
        Brush[] brushArr = this.brushes;
        this.currentBrush = i;
        renderView2.setBrush(brushArr[i]);
    }

    private void setStroke(boolean z) {
        this.selectedStroke = z;
        if (this.currentEntityView instanceof TextPaintView) {
            Swatch swatch = this.colorPicker.getSwatch();
            if (z && swatch.color == -1) {
                setCurrentSwatch(new Swatch(-16777216, 0.85f, swatch.brushWeight), true);
            } else if (!z && swatch.color == -16777216) {
                setCurrentSwatch(new Swatch(-1, 1.0f, swatch.brushWeight), true);
            }
            ((TextPaintView) this.currentEntityView).setStroke(z);
        }
    }

    private void showMenuForEntity(EntityView entityView) {
        showPopup(new Runnable(entityView) {
            private final /* synthetic */ EntityView f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PhotoPaintView.this.lambda$showMenuForEntity$11$PhotoPaintView(this.f$1);
            }
        }, entityView, 17, (int) ((entityView.getPosition().x - ((float) (this.entitiesView.getWidth() / 2))) * this.entitiesView.getScaleX()), ((int) (((entityView.getPosition().y - ((((float) entityView.getHeight()) * entityView.getScale()) / 2.0f)) - ((float) (this.entitiesView.getHeight() / 2))) * this.entitiesView.getScaleY())) - AndroidUtilities.dp(32.0f));
    }

    public /* synthetic */ void lambda$showMenuForEntity$11$PhotoPaintView(EntityView entityView) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(0);
        TextView textView = new TextView(getContext());
        textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        textView.setGravity(16);
        textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(14.0f), 0);
        textView.setTextSize(1, 18.0f);
        textView.setTag(0);
        textView.setText(LocaleController.getString("PaintDelete", NUM));
        textView.setOnClickListener(new View.OnClickListener(entityView) {
            private final /* synthetic */ EntityView f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                PhotoPaintView.this.lambda$null$8$PhotoPaintView(this.f$1, view);
            }
        });
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
        if (entityView instanceof TextPaintView) {
            TextView textView2 = new TextView(getContext());
            textView2.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            textView2.setGravity(16);
            textView2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            textView2.setTextSize(1, 18.0f);
            textView2.setTag(1);
            textView2.setText(LocaleController.getString("PaintEdit", NUM));
            textView2.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoPaintView.this.lambda$null$9$PhotoPaintView(view);
                }
            });
            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, 48));
        }
        TextView textView3 = new TextView(getContext());
        textView3.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        textView3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        textView3.setGravity(16);
        textView3.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
        textView3.setTextSize(1, 18.0f);
        textView3.setTag(2);
        textView3.setText(LocaleController.getString("PaintDuplicate", NUM));
        textView3.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoPaintView.this.lambda$null$10$PhotoPaintView(view);
            }
        });
        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, 48));
        this.popupLayout.addView(linearLayout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        linearLayout.setLayoutParams(layoutParams);
    }

    public /* synthetic */ void lambda$null$8$PhotoPaintView(EntityView entityView, View view) {
        lambda$registerRemovalUndo$7$PhotoPaintView(entityView);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    public /* synthetic */ void lambda$null$9$PhotoPaintView(View view) {
        editSelectedTextEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    public /* synthetic */ void lambda$null$10$PhotoPaintView(View view) {
        duplicateSelectedEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    private FrameLayout buttonForBrush(int i, int i2, boolean z) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        frameLayout.setOnClickListener(new View.OnClickListener(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                PhotoPaintView.this.lambda$buttonForBrush$12$PhotoPaintView(this.f$1, view);
            }
        });
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(i2);
        frameLayout.addView(imageView, LayoutHelper.createFrame(165, 44.0f, 19, 46.0f, 0.0f, 8.0f, 0.0f));
        if (z) {
            ImageView imageView2 = new ImageView(getContext());
            imageView2.setImageResource(NUM);
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            frameLayout.addView(imageView2, LayoutHelper.createFrame(50, -1.0f));
        }
        return frameLayout;
    }

    public /* synthetic */ void lambda$buttonForBrush$12$PhotoPaintView(int i, View view) {
        setBrush(i);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    public void showBrushSettings() {
        showPopup(new Runnable() {
            public final void run() {
                PhotoPaintView.this.lambda$showBrushSettings$13$PhotoPaintView();
            }
        }, this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    public /* synthetic */ void lambda$showBrushSettings$13$PhotoPaintView() {
        boolean z = false;
        FrameLayout buttonForBrush = buttonForBrush(0, NUM, this.currentBrush == 0);
        this.popupLayout.addView(buttonForBrush);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) buttonForBrush.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(52.0f);
        buttonForBrush.setLayoutParams(layoutParams);
        FrameLayout buttonForBrush2 = buttonForBrush(1, NUM, this.currentBrush == 1);
        this.popupLayout.addView(buttonForBrush2);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) buttonForBrush2.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = AndroidUtilities.dp(52.0f);
        buttonForBrush2.setLayoutParams(layoutParams2);
        if (this.currentBrush == 2) {
            z = true;
        }
        FrameLayout buttonForBrush3 = buttonForBrush(2, NUM, z);
        this.popupLayout.addView(buttonForBrush3);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) buttonForBrush3.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = AndroidUtilities.dp(52.0f);
        buttonForBrush3.setLayoutParams(layoutParams3);
    }

    private FrameLayout buttonForText(boolean z, String str, boolean z2) {
        AnonymousClass9 r0 = new FrameLayout(getContext()) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return true;
            }
        };
        r0.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        r0.setOnClickListener(new View.OnClickListener(z) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                PhotoPaintView.this.lambda$buttonForText$14$PhotoPaintView(this.f$1, view);
            }
        });
        EditTextOutline editTextOutline = new EditTextOutline(getContext());
        editTextOutline.setBackgroundColor(0);
        editTextOutline.setEnabled(false);
        editTextOutline.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        int i = -16777216;
        editTextOutline.setTextColor(z ? -1 : -16777216);
        if (!z) {
            i = 0;
        }
        editTextOutline.setStrokeColor(i);
        editTextOutline.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f), 0);
        editTextOutline.setTextSize(1, 18.0f);
        editTextOutline.setTypeface((Typeface) null, 1);
        editTextOutline.setTag(Boolean.valueOf(z));
        editTextOutline.setText(str);
        r0.addView(editTextOutline, LayoutHelper.createFrame(-2, -2.0f, 19, 46.0f, 0.0f, 16.0f, 0.0f));
        if (z2) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(NUM);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            r0.addView(imageView, LayoutHelper.createFrame(50, -1.0f));
        }
        return r0;
    }

    public /* synthetic */ void lambda$buttonForText$14$PhotoPaintView(boolean z, View view) {
        setStroke(z);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    public void showTextSettings() {
        showPopup(new Runnable() {
            public final void run() {
                PhotoPaintView.this.lambda$showTextSettings$15$PhotoPaintView();
            }
        }, this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    public /* synthetic */ void lambda$showTextSettings$15$PhotoPaintView() {
        FrameLayout buttonForText = buttonForText(true, LocaleController.getString("PaintOutlined", NUM), this.selectedStroke);
        this.popupLayout.addView(buttonForText);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) buttonForText.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        buttonForText.setLayoutParams(layoutParams);
        FrameLayout buttonForText2 = buttonForText(false, LocaleController.getString("PaintRegular", NUM), !this.selectedStroke);
        this.popupLayout.addView(buttonForText2);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) buttonForText2.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = AndroidUtilities.dp(48.0f);
        buttonForText2.setLayoutParams(layoutParams2);
    }

    private void showPopup(Runnable runnable, View view, int i, int i2, int i3) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                this.popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new View.OnTouchListener() {
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        return PhotoPaintView.this.lambda$showPopup$16$PhotoPaintView(view, motionEvent);
                    }
                });
                this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        PhotoPaintView.this.lambda$showPopup$17$PhotoPaintView(keyEvent);
                    }
                });
                this.popupLayout.setShowedFromBotton(true);
            }
            this.popupLayout.removeInnerViews();
            runnable.run();
            if (this.popupWindow == null) {
                this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(NUM);
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                this.popupWindow.getContentView().setFocusableInTouchMode(true);
                this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    public final void onDismiss() {
                        PhotoPaintView.this.lambda$showPopup$18$PhotoPaintView();
                    }
                });
            }
            this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(view, i, i2, i3);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    public /* synthetic */ boolean lambda$showPopup$16$PhotoPaintView(View view, MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(this.popupRect);
        if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    public /* synthetic */ void lambda$showPopup$17$PhotoPaintView(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$showPopup$18$PhotoPaintView() {
        this.popupLayout.removeInnerViews();
    }

    private int getFrameRotation() {
        int i = this.orientation;
        if (i == 90) {
            return 1;
        }
        if (i != 180) {
            return i != 270 ? 0 : 3;
        }
        return 2;
    }

    private void detectFaces() {
        this.queue.postRunnable(new Runnable() {
            public final void run() {
                PhotoPaintView.this.lambda$detectFaces$19$PhotoPaintView();
            }
        });
    }

    public /* synthetic */ void lambda$detectFaces$19$PhotoPaintView() {
        FaceDetector faceDetector = null;
        try {
            FaceDetector.Builder builder = new FaceDetector.Builder(getContext());
            builder.setMode(1);
            builder.setLandmarkType(1);
            builder.setTrackingEnabled(false);
            faceDetector = builder.build();
            if (!faceDetector.isOperational()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("face detection is not operational");
                }
                if (faceDetector != null) {
                    faceDetector.release();
                    return;
                }
                return;
            }
            Frame.Builder builder2 = new Frame.Builder();
            builder2.setBitmap(this.bitmapToEdit);
            builder2.setRotation(getFrameRotation());
            try {
                SparseArray<Face> detect = faceDetector.detect(builder2.build());
                ArrayList<PhotoFace> arrayList = new ArrayList<>();
                Size paintingSize2 = getPaintingSize();
                for (int i = 0; i < detect.size(); i++) {
                    PhotoFace photoFace = new PhotoFace(detect.get(detect.keyAt(i)), this.bitmapToEdit, paintingSize2, isSidewardOrientation());
                    if (photoFace.isSufficient()) {
                        arrayList.add(photoFace);
                    }
                }
                this.faces = arrayList;
                if (faceDetector != null) {
                    faceDetector.release();
                }
            } catch (Throwable th) {
                FileLog.e(th);
                if (faceDetector != null) {
                    faceDetector.release();
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            if (faceDetector == null) {
            }
        } catch (Throwable th2) {
            if (faceDetector != null) {
                faceDetector.release();
            }
            throw th2;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003a, code lost:
        r4 = r2.n;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.ui.Components.PhotoPaintView.StickerPosition calculateStickerPosition(org.telegram.tgnet.TLRPC.Document r17) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = 0
        L_0x0005:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x001f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker
            if (r4 == 0) goto L_0x001c
            org.telegram.tgnet.TLRPC$TL_maskCoords r2 = r3.mask_coords
            goto L_0x0020
        L_0x001c:
            int r2 = r2 + 1
            goto L_0x0005
        L_0x001f:
            r2 = 0
        L_0x0020:
            org.telegram.ui.Components.PhotoPaintView$StickerPosition r3 = new org.telegram.ui.Components.PhotoPaintView$StickerPosition
            org.telegram.ui.Components.Point r4 = r16.centerPositionForEntity()
            r5 = 1061158912(0x3var_, float:0.75)
            r6 = 0
            r3.<init>(r4, r5, r6)
            if (r2 == 0) goto L_0x00c8
            java.util.ArrayList<org.telegram.ui.Components.Paint.PhotoFace> r4 = r0.faces
            if (r4 == 0) goto L_0x00c8
            int r4 = r4.size()
            if (r4 != 0) goto L_0x003a
            goto L_0x00c8
        L_0x003a:
            int r4 = r2.n
            long r5 = r1.id
            org.telegram.ui.Components.Paint.PhotoFace r1 = r0.getRandomFaceWithVacantAnchor(r4, r5, r2)
            if (r1 != 0) goto L_0x0045
            return r3
        L_0x0045:
            org.telegram.ui.Components.Point r3 = r1.getPointForAnchor(r4)
            float r4 = r1.getWidthForAnchor(r4)
            float r1 = r1.getAngle()
            org.telegram.ui.Components.Size r5 = r16.baseStickerSize()
            float r5 = r5.width
            float r5 = r4 / r5
            double r5 = (double) r5
            double r7 = r2.zoom
            java.lang.Double.isNaN(r5)
            double r5 = r5 * r7
            float r5 = (float) r5
            double r6 = (double) r1
            double r6 = java.lang.Math.toRadians(r6)
            float r6 = (float) r6
            double r6 = (double) r6
            r8 = 4609753056924675352(0x3fvar_fb54442d18, double:1.NUM)
            java.lang.Double.isNaN(r6)
            double r10 = r8 - r6
            double r12 = java.lang.Math.sin(r10)
            double r14 = (double) r4
            java.lang.Double.isNaN(r14)
            double r12 = r12 * r14
            double r8 = r2.x
            double r12 = r12 * r8
            float r4 = (float) r12
            double r8 = java.lang.Math.cos(r10)
            java.lang.Double.isNaN(r14)
            double r8 = r8 * r14
            double r10 = r2.x
            double r8 = r8 * r10
            float r8 = (float) r8
            java.lang.Double.isNaN(r6)
            r9 = 4609753056924675352(0x3fvar_fb54442d18, double:1.NUM)
            double r6 = r6 + r9
            double r9 = java.lang.Math.cos(r6)
            java.lang.Double.isNaN(r14)
            double r9 = r9 * r14
            double r11 = r2.y
            double r9 = r9 * r11
            float r9 = (float) r9
            double r6 = java.lang.Math.sin(r6)
            java.lang.Double.isNaN(r14)
            double r6 = r6 * r14
            double r10 = r2.y
            double r6 = r6 * r10
            float r2 = (float) r6
            float r6 = r3.x
            float r6 = r6 + r4
            float r6 = r6 + r9
            float r3 = r3.y
            float r3 = r3 + r8
            float r3 = r3 + r2
            org.telegram.ui.Components.PhotoPaintView$StickerPosition r2 = new org.telegram.ui.Components.PhotoPaintView$StickerPosition
            org.telegram.ui.Components.Point r4 = new org.telegram.ui.Components.Point
            r4.<init>(r6, r3)
            r2.<init>(r4, r5, r1)
            return r2
        L_0x00c8:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoPaintView.calculateStickerPosition(org.telegram.tgnet.TLRPC$Document):org.telegram.ui.Components.PhotoPaintView$StickerPosition");
    }

    private PhotoFace getRandomFaceWithVacantAnchor(int i, long j, TLRPC.TL_maskCoords tL_maskCoords) {
        if (i >= 0 && i <= 3 && !this.faces.isEmpty()) {
            int size = this.faces.size();
            int nextInt = Utilities.random.nextInt(size);
            for (int i2 = size; i2 > 0; i2--) {
                PhotoFace photoFace = this.faces.get(nextInt);
                if (!isFaceAnchorOccupied(photoFace, i, j, tL_maskCoords)) {
                    return photoFace;
                }
                nextInt = (nextInt + 1) % size;
            }
        }
        return null;
    }

    private boolean isFaceAnchorOccupied(PhotoFace photoFace, int i, long j, TLRPC.TL_maskCoords tL_maskCoords) {
        Point pointForAnchor = photoFace.getPointForAnchor(i);
        if (pointForAnchor == null) {
            return true;
        }
        float widthForAnchor = photoFace.getWidthForAnchor(0) * 1.1f;
        for (int i2 = 0; i2 < this.entitiesView.getChildCount(); i2++) {
            View childAt = this.entitiesView.getChildAt(i2);
            if (childAt instanceof StickerView) {
                StickerView stickerView = (StickerView) childAt;
                if (stickerView.getAnchor() != i) {
                    continue;
                } else {
                    Point position = stickerView.getPosition();
                    float hypot = (float) Math.hypot((double) (position.x - pointForAnchor.x), (double) (position.y - pointForAnchor.y));
                    if ((j == stickerView.getSticker().id || this.faces.size() > 1) && hypot < widthForAnchor) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private class StickerPosition {
        /* access modifiers changed from: private */
        public float angle;
        /* access modifiers changed from: private */
        public Point position;
        /* access modifiers changed from: private */
        public float scale;

        StickerPosition(Point point, float f, float f2) {
            this.position = point;
            this.scale = f;
            this.angle = f2;
        }
    }
}
