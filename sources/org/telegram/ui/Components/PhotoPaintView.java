package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_maskCoords;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Paint.Brush;
import org.telegram.ui.Components.Paint.Brush.Elliptical;
import org.telegram.ui.Components.Paint.Brush.Neon;
import org.telegram.ui.Components.Paint.Brush.Radial;
import org.telegram.ui.Components.Paint.Painting;
import org.telegram.ui.Components.Paint.PhotoFace;
import org.telegram.ui.Components.Paint.RenderView;
import org.telegram.ui.Components.Paint.RenderView.RenderViewDelegate;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.UndoStore;
import org.telegram.ui.Components.Paint.Views.ColorPicker;
import org.telegram.ui.Components.Paint.Views.ColorPicker.ColorPickerDelegate;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;
import org.telegram.ui.Components.Paint.Views.EntitiesContainerView;
import org.telegram.ui.Components.Paint.Views.EntitiesContainerView.EntitiesContainerViewDelegate;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Paint.Views.EntityView.EntityViewDelegate;
import org.telegram.ui.Components.Paint.Views.StickerView;
import org.telegram.ui.Components.Paint.Views.TextPaintView;
import org.telegram.ui.Components.StickerMasksView.Listener;
import org.telegram.ui.PhotoViewer;

@SuppressLint({"NewApi"})
public class PhotoPaintView extends FrameLayout implements EntityViewDelegate {
    private static final int gallery_menu_done = 1;
    private Bitmap bitmapToEdit;
    private Brush[] brushes = new Brush[]{new Radial(), new Elliptical(), new Neon()};
    private TextView cancelTextView;
    private ColorPicker colorPicker;
    private Animator colorPickerAnimator;
    int currentBrush;
    private EntityView currentEntityView;
    private FrameLayout curtainView;
    private FrameLayout dimView;
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
    private ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private DispatchQueue queue = new DispatchQueue("Paint");
    private RenderView renderView;
    private boolean selectedStroke = true;
    private FrameLayout selectionContainerView;
    private StickerMasksView stickersView;
    private FrameLayout textDimView;
    private FrameLayout toolsView;
    private UndoStore undoStore;

    private class StickerPosition {
        private float angle;
        private Point position;
        private float scale;

        StickerPosition(Point point, float f, float f2) {
            this.position = point;
            this.scale = f;
            this.angle = f2;
        }
    }

    public PhotoPaintView(Context context, Bitmap bitmap, int i) {
        super(context);
        this.bitmapToEdit = bitmap;
        this.orientation = i;
        this.undoStore = new UndoStore();
        this.undoStore.setDelegate(new -$$Lambda$PhotoPaintView$R6Re0Kk5HfUX12qMoexuM_2zbtQ(this));
        this.curtainView = new FrameLayout(context);
        this.curtainView.setBackgroundColor(-16777216);
        this.curtainView.setVisibility(4);
        addView(this.curtainView);
        this.renderView = new RenderView(context, new Painting(getPaintingSize()), bitmap, this.orientation);
        this.renderView.setDelegate(new RenderViewDelegate() {
            public void onBeganDrawing() {
                if (PhotoPaintView.this.currentEntityView != null) {
                    PhotoPaintView.this.selectEntity(null);
                }
            }

            public void onFinishedDrawing(boolean z) {
                PhotoPaintView.this.colorPicker.setUndoEnabled(PhotoPaintView.this.undoStore.canUndo());
            }

            public boolean shouldDraw() {
                boolean z = PhotoPaintView.this.currentEntityView == null;
                if (!z) {
                    PhotoPaintView.this.selectEntity(null);
                }
                return z;
            }
        });
        this.renderView.setUndoStore(this.undoStore);
        this.renderView.setQueue(this.queue);
        this.renderView.setVisibility(4);
        this.renderView.setBrush(this.brushes[0]);
        addView(this.renderView, LayoutHelper.createFrame(-1, -1, 51));
        this.entitiesView = new EntitiesContainerView(context, new EntitiesContainerViewDelegate() {
            public boolean shouldReceiveTouches() {
                return PhotoPaintView.this.textDimView.getVisibility() != 0;
            }

            public EntityView onSelectedEntityRequest() {
                return PhotoPaintView.this.currentEntityView;
            }

            public void onEntityDeselect() {
                PhotoPaintView.this.selectEntity(null);
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
        this.textDimView.setOnClickListener(new -$$Lambda$PhotoPaintView$ZykewOMvdivcJ4QbiFmia3gfIL0(this));
        this.selectionContainerView = new FrameLayout(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return false;
            }
        };
        addView(this.selectionContainerView);
        this.colorPicker = new ColorPicker(context);
        addView(this.colorPicker);
        this.colorPicker.setDelegate(new ColorPickerDelegate() {
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
        String str = "fonts/rmedium.ttf";
        this.cancelTextView.setTypeface(AndroidUtilities.getTypeface(str));
        this.toolsView.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
        this.doneTextView = new TextView(context);
        this.doneTextView.setTextSize(1, 14.0f);
        this.doneTextView.setTextColor(-11420173);
        this.doneTextView.setGravity(17);
        this.doneTextView.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
        this.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneTextView.setText(LocaleController.getString("Done", NUM).toUpperCase());
        this.doneTextView.setTypeface(AndroidUtilities.getTypeface(str));
        this.toolsView.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
        this.paintButton = new ImageView(context);
        this.paintButton.setScaleType(ScaleType.CENTER);
        this.paintButton.setImageResource(NUM);
        this.paintButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.toolsView.addView(this.paintButton, LayoutHelper.createFrame(54, -1.0f, 17, 0.0f, 0.0f, 56.0f, 0.0f));
        this.paintButton.setOnClickListener(new -$$Lambda$PhotoPaintView$rvar_UpLc_844_0-ExvqyyhAN3g(this));
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageResource(NUM);
        imageView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.toolsView.addView(imageView, LayoutHelper.createFrame(54, -1, 17));
        imageView.setOnClickListener(new -$$Lambda$PhotoPaintView$bh1PtB_nRqdfutcf4y5QOEZjU74(this));
        imageView = new ImageView(context);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageResource(NUM);
        imageView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.toolsView.addView(imageView, LayoutHelper.createFrame(54, -1.0f, 17, 56.0f, 0.0f, 0.0f, 0.0f));
        imageView.setOnClickListener(new -$$Lambda$PhotoPaintView$SEafmfD5xvHbkIUST7pqiumb7sI(this));
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
        selectEntity(null);
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
                selectEntity(null);
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
            this.paintButton.setColorFilter(null);
        } else {
            this.paintButton.setColorFilter(new PorterDuffColorFilter(-11420173, Mode.MULTIPLY));
            this.paintButton.setImageResource(NUM);
        }
        this.colorPicker.setSettingsButtonImage(i);
    }

    public void init() {
        this.renderView.setVisibility(0);
        detectFaces();
    }

    public void shutdown() {
        this.renderView.shutdown();
        this.entitiesView.setVisibility(8);
        this.selectionContainerView.setVisibility(8);
        this.queue.postRunnable(-$$Lambda$PhotoPaintView$BRjjxWV4_4jng8wjr2bRzoAFJ_A.INSTANCE);
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
                        Bitmap createBitmap = Bitmaps.createBitmap(childAt.getWidth(), childAt.getHeight(), Config.ARGB_8888);
                        Canvas canvas2 = new Canvas(createBitmap);
                        childAt.draw(canvas2);
                        canvas.drawBitmap(createBitmap, null, new Rect(0, 0, createBitmap.getWidth(), createBitmap.getHeight()), null);
                        try {
                            canvas2.setBitmap(null);
                        } catch (Exception e) {
                            FileLog.e(e);
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
        } else {
            if (!hasChanges()) {
                runnable.run();
            } else if (activity != null) {
                Builder builder = new Builder((Context) activity);
                builder.setMessage(LocaleController.getString("DiscardChanges", NUM));
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$PhotoPaintView$zLiJ6Xhod_3MqoXM9DwNMRgBw8Y(runnable));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                photoViewer.showAlertDialog(builder);
            }
        }
    }

    private void setCurrentSwatch(Swatch swatch, boolean z) {
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

    private void setDimVisibility(final boolean z) {
        Animator ofFloat;
        String str = "alpha";
        if (z) {
            this.dimView.setVisibility(0);
            ofFloat = ObjectAnimator.ofFloat(this.dimView, str, new float[]{0.0f, 1.0f});
        } else {
            ofFloat = ObjectAnimator.ofFloat(this.dimView, str, new float[]{1.0f, 0.0f});
        }
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (!z) {
                    PhotoPaintView.this.dimView.setVisibility(8);
                }
            }
        });
        ofFloat.setDuration(200);
        ofFloat.start();
    }

    private void setTextDimVisibility(final boolean z, EntityView entityView) {
        Animator ofFloat;
        if (z && entityView != null) {
            ViewGroup viewGroup = (ViewGroup) entityView.getParent();
            if (this.textDimView.getParent() != null) {
                ((EntitiesContainerView) this.textDimView.getParent()).removeView(this.textDimView);
            }
            viewGroup.addView(this.textDimView, viewGroup.indexOfChild(entityView));
        }
        entityView.setSelectionVisibility(z ^ 1);
        String str = "alpha";
        if (z) {
            this.textDimView.setVisibility(0);
            ofFloat = ObjectAnimator.ofFloat(this.textDimView, str, new float[]{0.0f, 1.0f});
        } else {
            ofFloat = ObjectAnimator.ofFloat(this.textDimView, str, new float[]{1.0f, 0.0f});
        }
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (!z) {
                    PhotoPaintView.this.textDimView.setVisibility(8);
                    if (PhotoPaintView.this.textDimView.getParent() != null) {
                        ((EntitiesContainerView) PhotoPaintView.this.textDimView.getParent()).removeView(PhotoPaintView.this.textDimView);
                    }
                }
            }
        });
        ofFloat.setDuration(200);
        ofFloat.start();
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        float height;
        float width;
        int size = MeasureSpec.getSize(i);
        i2 = MeasureSpec.getSize(i2);
        setMeasuredDimension(size, i2);
        int currentActionBarHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f);
        if (this.bitmapToEdit != null) {
            height = (float) (isSidewardOrientation() ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
            width = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        } else {
            height = (float) size;
            width = (float) ((i2 - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f));
        }
        float f = (float) size;
        float floor = (float) Math.floor((double) ((f * width) / height));
        float f2 = (float) currentActionBarHeight;
        if (floor > f2) {
            f = (float) Math.floor((double) ((height * f2) / width));
            floor = f2;
        }
        this.renderView.measure(MeasureSpec.makeMeasureSpec((int) f, NUM), MeasureSpec.makeMeasureSpec((int) floor, NUM));
        this.entitiesView.measure(MeasureSpec.makeMeasureSpec((int) this.paintingSize.width, NUM), MeasureSpec.makeMeasureSpec((int) this.paintingSize.height, NUM));
        this.dimView.measure(i, MeasureSpec.makeMeasureSpec(currentActionBarHeight, Integer.MIN_VALUE));
        this.selectionContainerView.measure(i, MeasureSpec.makeMeasureSpec(currentActionBarHeight, NUM));
        this.colorPicker.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(currentActionBarHeight, NUM));
        this.toolsView.measure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView != null) {
            stickerMasksView.measure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, NUM));
        }
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float height;
        float width;
        i3 -= i;
        i4 -= i2;
        int i5 = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
        i2 = ActionBar.getCurrentActionBarHeight();
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + i5;
        int dp = (AndroidUtilities.displaySize.y - i2) - AndroidUtilities.dp(48.0f);
        if (this.bitmapToEdit != null) {
            height = (float) (isSidewardOrientation() ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
            width = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        } else {
            height = (float) i3;
            width = (float) ((i4 - i2) - AndroidUtilities.dp(48.0f));
        }
        float f = (float) i3;
        float f2 = (float) dp;
        if (((float) Math.floor((double) ((f * width) / height))) > f2) {
            f = (float) Math.floor((double) ((f2 * height) / width));
        }
        i2 = (int) Math.ceil((double) ((i3 - this.renderView.getMeasuredWidth()) / 2));
        int dp2 = ((((((i4 - currentActionBarHeight) - AndroidUtilities.dp(48.0f)) - this.renderView.getMeasuredHeight()) / 2) + currentActionBarHeight) - ActionBar.getCurrentActionBarHeight()) + AndroidUtilities.dp(8.0f);
        RenderView renderView = this.renderView;
        renderView.layout(i2, dp2, renderView.getMeasuredWidth() + i2, this.renderView.getMeasuredHeight() + dp2);
        f /= this.paintingSize.width;
        this.entitiesView.setScaleX(f);
        this.entitiesView.setScaleY(f);
        EntitiesContainerView entitiesContainerView = this.entitiesView;
        entitiesContainerView.layout(i2, dp2, entitiesContainerView.getMeasuredWidth() + i2, this.entitiesView.getMeasuredHeight() + dp2);
        FrameLayout frameLayout = this.dimView;
        frameLayout.layout(0, i5, frameLayout.getMeasuredWidth(), this.dimView.getMeasuredHeight() + i5);
        frameLayout = this.selectionContainerView;
        frameLayout.layout(0, i5, frameLayout.getMeasuredWidth(), this.selectionContainerView.getMeasuredHeight() + i5);
        ColorPicker colorPicker = this.colorPicker;
        colorPicker.layout(0, currentActionBarHeight, colorPicker.getMeasuredWidth(), this.colorPicker.getMeasuredHeight() + currentActionBarHeight);
        frameLayout = this.toolsView;
        frameLayout.layout(0, i4 - frameLayout.getMeasuredHeight(), this.toolsView.getMeasuredWidth(), i4);
        this.curtainView.layout(0, 0, i3, dp);
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView != null) {
            stickerMasksView.layout(0, i5, stickerMasksView.getMeasuredWidth(), this.stickersView.getMeasuredHeight() + i5);
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
        return this.editingText ^ 1;
    }

    private Point centerPositionForEntity() {
        Size paintingSize = getPaintingSize();
        return new Point(paintingSize.width / 2.0f, paintingSize.height / 2.0f);
    }

    private Point startPositionRelativeToEntity(EntityView entityView) {
        Point position;
        if (entityView != null) {
            position = entityView.getPosition();
            return new Point(position.x + 200.0f, position.y + 200.0f);
        }
        position = centerPositionForEntity();
        while (true) {
            Object obj = null;
            for (int i = 0; i < this.entitiesView.getChildCount(); i++) {
                View childAt = this.entitiesView.getChildAt(i);
                if (childAt instanceof EntityView) {
                    Point position2 = ((EntityView) childAt).getPosition();
                    if (((float) Math.sqrt(Math.pow((double) (position2.x - position.x), 2.0d) + Math.pow((double) (position2.y - position.y), 2.0d))) < 100.0f) {
                        obj = 1;
                    }
                }
            }
            if (obj == null) {
                return position;
            }
            position = new Point(position.x + 200.0f, position.y + 200.0f);
        }
    }

    public ArrayList<InputDocument> getMasks() {
        int childCount = this.entitiesView.getChildCount();
        ArrayList<InputDocument> arrayList = null;
        for (int i = 0; i < childCount; i++) {
            View childAt = this.entitiesView.getChildAt(i);
            if (childAt instanceof StickerView) {
                Document sticker = ((StickerView) childAt).getSticker();
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                TL_inputDocument tL_inputDocument = new TL_inputDocument();
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

    private boolean selectEntity(EntityView entityView) {
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
        entityView = this.currentEntityView;
        if (entityView != null) {
            entityView.select(this.selectionContainerView);
            this.entitiesView.bringViewToFront(this.currentEntityView);
            entityView = this.currentEntityView;
            if (entityView instanceof TextPaintView) {
                setCurrentSwatch(((TextPaintView) entityView).getSwatch(), true);
            }
            z = true;
        }
        updateSettingsButton();
        return z;
    }

    private void removeEntity(EntityView entityView) {
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
            EntityView entityView2 = null;
            Point startPositionRelativeToEntity = startPositionRelativeToEntity(entityView);
            EntityView entityView3 = this.currentEntityView;
            if (entityView3 instanceof StickerView) {
                entityView2 = new StickerView(getContext(), (StickerView) this.currentEntityView, startPositionRelativeToEntity);
                entityView2.setDelegate(this);
                this.entitiesView.addView(entityView2);
            } else if (entityView3 instanceof TextPaintView) {
                entityView2 = new TextPaintView(getContext(), (TextPaintView) this.currentEntityView, startPositionRelativeToEntity);
                entityView2.setDelegate(this);
                entityView2.setMaxWidth((int) (getPaintingSize().width - 20.0f));
                this.entitiesView.addView(entityView2, LayoutHelper.createFrame(-2, -2.0f));
            }
            registerRemovalUndo(entityView2);
            selectEntity(entityView2);
            updateSettingsButton();
        }
    }

    private void openStickersView() {
        StickerMasksView stickerMasksView = this.stickersView;
        if (stickerMasksView == null || stickerMasksView.getVisibility() != 0) {
            this.pickingSticker = true;
            if (this.stickersView == null) {
                this.stickersView = new StickerMasksView(getContext());
                this.stickersView.setListener(new Listener() {
                    public void onTypeChanged() {
                    }

                    public void onStickerSelected(Object obj, Document document) {
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

    private void closeStickersView() {
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
        this.undoStore.registerUndo(entityView.getUUID(), new -$$Lambda$PhotoPaintView$tId4geOoF8wmSuxKFoBjX4PhzNE(this, entityView));
    }

    public /* synthetic */ void lambda$registerRemovalUndo$7$PhotoPaintView(EntityView entityView) {
        removeEntity(entityView);
    }

    private void createSticker(Object obj, Document document) {
        StickerPosition calculateStickerPosition = calculateStickerPosition(document);
        StickerView stickerView = new StickerView(getContext(), calculateStickerPosition.position, calculateStickerPosition.angle, calculateStickerPosition.scale, baseStickerSize(), document, obj);
        stickerView.setDelegate(this);
        this.entitiesView.addView(stickerView);
        registerRemovalUndo(stickerView);
        selectEntity(stickerView);
    }

    private void mirrorSticker() {
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
        TextPaintView textPaintView = new TextPaintView(getContext(), startPositionRelativeToEntity(null), baseFontSize(), "", this.colorPicker.getSwatch(), this.selectedStroke);
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
                    selectEntity(null);
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
        RenderView renderView = this.renderView;
        Brush[] brushArr = this.brushes;
        this.currentBrush = i;
        renderView.setBrush(brushArr[i]);
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
        showPopup(new -$$Lambda$PhotoPaintView$W57YTvVSDuzEA0lVLhPNgGrpM0g(this, entityView), entityView, 17, (int) ((entityView.getPosition().x - ((float) (this.entitiesView.getWidth() / 2))) * this.entitiesView.getScaleX()), ((int) (((entityView.getPosition().y - ((((float) entityView.getHeight()) * entityView.getScale()) / 2.0f)) - ((float) (this.entitiesView.getHeight() / 2))) * this.entitiesView.getScaleY())) - AndroidUtilities.dp(32.0f));
    }

    public /* synthetic */ void lambda$showMenuForEntity$11$PhotoPaintView(EntityView entityView) {
        TextView textView;
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(0);
        TextView textView2 = new TextView(getContext());
        String str = "actionBarDefaultSubmenuItem";
        textView2.setTextColor(Theme.getColor(str));
        textView2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        textView2.setGravity(16);
        textView2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(14.0f), 0);
        textView2.setTextSize(1, 18.0f);
        textView2.setTag(Integer.valueOf(0));
        textView2.setText(LocaleController.getString("PaintDelete", NUM));
        textView2.setOnClickListener(new -$$Lambda$PhotoPaintView$sYCjwRMr-S5-hGejNJaJoYD8A4Q(this, entityView));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, 48));
        if (entityView instanceof TextPaintView) {
            textView = new TextView(getContext());
            textView.setTextColor(Theme.getColor(str));
            textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            textView.setGravity(16);
            textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            textView.setTextSize(1, 18.0f);
            textView.setTag(Integer.valueOf(1));
            textView.setText(LocaleController.getString("PaintEdit", NUM));
            textView.setOnClickListener(new -$$Lambda$PhotoPaintView$-rzL0nO-ZFC3zc_eG729dIHU-X0(this));
            linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
        }
        textView = new TextView(getContext());
        textView.setTextColor(Theme.getColor(str));
        textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        textView.setGravity(16);
        textView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
        textView.setTextSize(1, 18.0f);
        textView.setTag(Integer.valueOf(2));
        textView.setText(LocaleController.getString("PaintDuplicate", NUM));
        textView.setOnClickListener(new -$$Lambda$PhotoPaintView$mPRLCtV6-7XvBSiDh7u_Mg52bn8(this));
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
        this.popupLayout.addView(linearLayout);
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        linearLayout.setLayoutParams(layoutParams);
    }

    public /* synthetic */ void lambda$null$8$PhotoPaintView(EntityView entityView, View view) {
        removeEntity(entityView);
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
        frameLayout.setOnClickListener(new -$$Lambda$PhotoPaintView$gGzFKkJs5bEvFPWs_vHHaN5jxJY(this, i));
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(i2);
        frameLayout.addView(imageView, LayoutHelper.createFrame(165, 44.0f, 19, 46.0f, 0.0f, 8.0f, 0.0f));
        if (z) {
            imageView = new ImageView(getContext());
            imageView.setImageResource(NUM);
            imageView.setScaleType(ScaleType.CENTER);
            imageView.setColorFilter(new PorterDuffColorFilter(-13660983, Mode.MULTIPLY));
            frameLayout.addView(imageView, LayoutHelper.createFrame(50, -1.0f));
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

    private void showBrushSettings() {
        showPopup(new -$$Lambda$PhotoPaintView$xEc6bzU-od8KaDlhUDbuZvHSrF0(this), this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    public /* synthetic */ void lambda$showBrushSettings$13$PhotoPaintView() {
        boolean z = false;
        FrameLayout buttonForBrush = buttonForBrush(0, NUM, this.currentBrush == 0);
        this.popupLayout.addView(buttonForBrush);
        LayoutParams layoutParams = (LayoutParams) buttonForBrush.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(52.0f);
        buttonForBrush.setLayoutParams(layoutParams);
        buttonForBrush = buttonForBrush(1, NUM, this.currentBrush == 1);
        this.popupLayout.addView(buttonForBrush);
        layoutParams = (LayoutParams) buttonForBrush.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(52.0f);
        buttonForBrush.setLayoutParams(layoutParams);
        if (this.currentBrush == 2) {
            z = true;
        }
        buttonForBrush = buttonForBrush(2, NUM, z);
        this.popupLayout.addView(buttonForBrush);
        LayoutParams layoutParams2 = (LayoutParams) buttonForBrush.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = AndroidUtilities.dp(52.0f);
        buttonForBrush.setLayoutParams(layoutParams2);
    }

    private FrameLayout buttonForText(boolean z, String str, boolean z2) {
        AnonymousClass9 anonymousClass9 = new FrameLayout(getContext()) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return true;
            }
        };
        anonymousClass9.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        anonymousClass9.setOnClickListener(new -$$Lambda$PhotoPaintView$Ooj6P__OHnWplT4W-LITMgEHYP8(this, z));
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
        editTextOutline.setTypeface(null, 1);
        editTextOutline.setTag(Boolean.valueOf(z));
        editTextOutline.setText(str);
        anonymousClass9.addView(editTextOutline, LayoutHelper.createFrame(-2, -2.0f, 19, 46.0f, 0.0f, 16.0f, 0.0f));
        if (z2) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(NUM);
            imageView.setScaleType(ScaleType.CENTER);
            imageView.setColorFilter(new PorterDuffColorFilter(-13660983, Mode.MULTIPLY));
            anonymousClass9.addView(imageView, LayoutHelper.createFrame(50, -1.0f));
        }
        return anonymousClass9;
    }

    public /* synthetic */ void lambda$buttonForText$14$PhotoPaintView(boolean z, View view) {
        setStroke(z);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    private void showTextSettings() {
        showPopup(new -$$Lambda$PhotoPaintView$buAbz9vEN2US4kAn-glr3fxkqZ8(this), this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    public /* synthetic */ void lambda$showTextSettings$15$PhotoPaintView() {
        FrameLayout buttonForText = buttonForText(true, LocaleController.getString("PaintOutlined", NUM), this.selectedStroke);
        this.popupLayout.addView(buttonForText);
        LayoutParams layoutParams = (LayoutParams) buttonForText.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        buttonForText.setLayoutParams(layoutParams);
        buttonForText = buttonForText(false, LocaleController.getString("PaintRegular", NUM), this.selectedStroke ^ 1);
        this.popupLayout.addView(buttonForText);
        layoutParams = (LayoutParams) buttonForText.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        buttonForText.setLayoutParams(layoutParams);
    }

    private void showPopup(Runnable runnable, View view, int i, int i2, int i3) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                this.popupLayout = new ActionBarPopupWindowLayout(getContext());
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new -$$Lambda$PhotoPaintView$zZo9_q6rUClGLuFi2Fkr8DYrX2w(this));
                this.popupLayout.setDispatchKeyEventListener(new -$$Lambda$PhotoPaintView$JREJI5nlVBnmTQKnZtaJEmOzNLY(this));
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
                this.popupWindow.setOnDismissListener(new -$$Lambda$PhotoPaintView$67QsJI8M7iQb0wPE-b73gcxw_Ro(this));
            }
            this.popupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(view, i, i2, i3);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    public /* synthetic */ boolean lambda$showPopup$16$PhotoPaintView(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                view.getHitRect(this.popupRect);
                if (!this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    this.popupWindow.dismiss();
                }
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$showPopup$17$PhotoPaintView(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.popupWindow.dismiss();
            }
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
        } else {
            return 2;
        }
    }

    private void detectFaces() {
        this.queue.postRunnable(new -$$Lambda$PhotoPaintView$SzGvQrBi6uK_uDahuUw-nbi2sbM(this));
    }

    /* JADX WARNING: Missing block: B:34:0x008c, code skipped:
            if (r0 == null) goto L_0x0091;
     */
    public /* synthetic */ void lambda$detectFaces$19$PhotoPaintView() {
        /*
        r9 = this;
        r0 = 0;
        r1 = new com.google.android.gms.vision.face.FaceDetector$Builder;	 Catch:{ Exception -> 0x0088 }
        r2 = r9.getContext();	 Catch:{ Exception -> 0x0088 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0088 }
        r2 = 1;
        r1.setMode(r2);	 Catch:{ Exception -> 0x0088 }
        r1.setLandmarkType(r2);	 Catch:{ Exception -> 0x0088 }
        r2 = 0;
        r1.setTrackingEnabled(r2);	 Catch:{ Exception -> 0x0088 }
        r0 = r1.build();	 Catch:{ Exception -> 0x0088 }
        r1 = r0.isOperational();	 Catch:{ Exception -> 0x0088 }
        if (r1 != 0) goto L_0x002e;
    L_0x001f:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0088 }
        if (r1 == 0) goto L_0x0028;
    L_0x0023:
        r1 = "face detection is not operational";
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0088 }
    L_0x0028:
        if (r0 == 0) goto L_0x002d;
    L_0x002a:
        r0.release();
    L_0x002d:
        return;
    L_0x002e:
        r1 = new com.google.android.gms.vision.Frame$Builder;	 Catch:{ Exception -> 0x0088 }
        r1.<init>();	 Catch:{ Exception -> 0x0088 }
        r3 = r9.bitmapToEdit;	 Catch:{ Exception -> 0x0088 }
        r1.setBitmap(r3);	 Catch:{ Exception -> 0x0088 }
        r3 = r9.getFrameRotation();	 Catch:{ Exception -> 0x0088 }
        r1.setRotation(r3);	 Catch:{ Exception -> 0x0088 }
        r1 = r1.build();	 Catch:{ Exception -> 0x0088 }
        r1 = r0.detect(r1);	 Catch:{ Throwable -> 0x007c }
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0088 }
        r3.<init>();	 Catch:{ Exception -> 0x0088 }
        r4 = r9.getPaintingSize();	 Catch:{ Exception -> 0x0088 }
    L_0x0050:
        r5 = r1.size();	 Catch:{ Exception -> 0x0088 }
        if (r2 >= r5) goto L_0x0077;
    L_0x0056:
        r5 = r1.keyAt(r2);	 Catch:{ Exception -> 0x0088 }
        r5 = r1.get(r5);	 Catch:{ Exception -> 0x0088 }
        r5 = (com.google.android.gms.vision.face.Face) r5;	 Catch:{ Exception -> 0x0088 }
        r6 = new org.telegram.ui.Components.Paint.PhotoFace;	 Catch:{ Exception -> 0x0088 }
        r7 = r9.bitmapToEdit;	 Catch:{ Exception -> 0x0088 }
        r8 = r9.isSidewardOrientation();	 Catch:{ Exception -> 0x0088 }
        r6.<init>(r5, r7, r4, r8);	 Catch:{ Exception -> 0x0088 }
        r5 = r6.isSufficient();	 Catch:{ Exception -> 0x0088 }
        if (r5 == 0) goto L_0x0074;
    L_0x0071:
        r3.add(r6);	 Catch:{ Exception -> 0x0088 }
    L_0x0074:
        r2 = r2 + 1;
        goto L_0x0050;
    L_0x0077:
        r9.faces = r3;	 Catch:{ Exception -> 0x0088 }
        if (r0 == 0) goto L_0x0091;
    L_0x007b:
        goto L_0x008e;
    L_0x007c:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0088 }
        if (r0 == 0) goto L_0x0085;
    L_0x0082:
        r0.release();
    L_0x0085:
        return;
    L_0x0086:
        r1 = move-exception;
        goto L_0x0092;
    L_0x0088:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0086 }
        if (r0 == 0) goto L_0x0091;
    L_0x008e:
        r0.release();
    L_0x0091:
        return;
    L_0x0092:
        if (r0 == 0) goto L_0x0097;
    L_0x0094:
        r0.release();
    L_0x0097:
        goto L_0x0099;
    L_0x0098:
        throw r1;
    L_0x0099:
        goto L_0x0098;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoPaintView.lambda$detectFaces$19$PhotoPaintView():void");
    }

    private StickerPosition calculateStickerPosition(Document document) {
        TL_maskCoords tL_maskCoords;
        Document document2 = document;
        for (int i = 0; i < document2.attributes.size(); i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document2.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                tL_maskCoords = documentAttribute.mask_coords;
                break;
            }
        }
        tL_maskCoords = null;
        StickerPosition stickerPosition = new StickerPosition(centerPositionForEntity(), 0.75f, 0.0f);
        if (tL_maskCoords != null) {
            ArrayList arrayList = this.faces;
            if (!(arrayList == null || arrayList.size() == 0)) {
                int i2 = tL_maskCoords.n;
                PhotoFace randomFaceWithVacantAnchor = getRandomFaceWithVacantAnchor(i2, document2.id, tL_maskCoords);
                if (randomFaceWithVacantAnchor == null) {
                    return stickerPosition;
                }
                Point pointForAnchor = randomFaceWithVacantAnchor.getPointForAnchor(i2);
                float widthForAnchor = randomFaceWithVacantAnchor.getWidthForAnchor(i2);
                float angle = randomFaceWithVacantAnchor.getAngle();
                double d = (double) (widthForAnchor / baseStickerSize().width);
                double d2 = tL_maskCoords.zoom;
                Double.isNaN(d);
                float f = (float) (d * d2);
                double toRadians = (double) ((float) Math.toRadians((double) angle));
                Double.isNaN(toRadians);
                double d3 = 1.5707963267948966d - toRadians;
                double sin = Math.sin(d3);
                double d4 = (double) widthForAnchor;
                Double.isNaN(d4);
                widthForAnchor = (float) ((sin * d4) * tL_maskCoords.x);
                double cos = Math.cos(d3);
                Double.isNaN(d4);
                float f2 = (float) ((cos * d4) * tL_maskCoords.x);
                Double.isNaN(toRadians);
                toRadians += 1.5707963267948966d;
                double cos2 = Math.cos(toRadians);
                Double.isNaN(d4);
                float f3 = (float) ((cos2 * d4) * tL_maskCoords.y);
                toRadians = Math.sin(toRadians);
                Double.isNaN(d4);
                return new StickerPosition(new Point((pointForAnchor.x + widthForAnchor) + f3, (pointForAnchor.y + f2) + ((float) ((toRadians * d4) * tL_maskCoords.y))), f, angle);
            }
        }
        return stickerPosition;
    }

    private PhotoFace getRandomFaceWithVacantAnchor(int i, long j, TL_maskCoords tL_maskCoords) {
        if (i >= 0 && i <= 3 && !this.faces.isEmpty()) {
            int size = this.faces.size();
            int nextInt = Utilities.random.nextInt(size);
            for (int i2 = size; i2 > 0; i2--) {
                PhotoFace photoFace = (PhotoFace) this.faces.get(nextInt);
                if (!isFaceAnchorOccupied(photoFace, i, j, tL_maskCoords)) {
                    return photoFace;
                }
                nextInt = (nextInt + 1) % size;
            }
        }
        return null;
    }

    private boolean isFaceAnchorOccupied(PhotoFace photoFace, int i, long j, TL_maskCoords tL_maskCoords) {
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
}
