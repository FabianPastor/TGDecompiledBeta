package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Looper;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
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
import org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;
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
import org.telegram.ui.Components.Paint.UndoStore.UndoStoreDelegate;
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

    /* renamed from: org.telegram.ui.Components.PhotoPaintView$4 */
    class C12564 implements OnClickListener {
        C12564() {
        }

        public void onClick(View view) {
            PhotoPaintView.this.closeTextEnter(true);
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoPaintView$7 */
    class C12587 implements OnClickListener {
        C12587() {
        }

        public void onClick(View view) {
            PhotoPaintView.this.selectEntity(null);
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoPaintView$8 */
    class C12598 implements OnClickListener {
        C12598() {
        }

        public void onClick(View view) {
            PhotoPaintView.this.openStickersView();
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoPaintView$9 */
    class C12609 implements OnClickListener {
        C12609() {
        }

        public void onClick(View view) {
            PhotoPaintView.this.createText();
        }
    }

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

    /* renamed from: org.telegram.ui.Components.PhotoPaintView$1 */
    class C20711 implements UndoStoreDelegate {
        C20711() {
        }

        public void historyChanged() {
            PhotoPaintView.this.colorPicker.setUndoEnabled(PhotoPaintView.this.undoStore.canUndo());
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoPaintView$2 */
    class C20722 implements RenderViewDelegate {
        C20722() {
        }

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
    }

    /* renamed from: org.telegram.ui.Components.PhotoPaintView$3 */
    class C20733 implements EntitiesContainerViewDelegate {
        C20733() {
        }

        public boolean shouldReceiveTouches() {
            return PhotoPaintView.this.textDimView.getVisibility() != 0;
        }

        public EntityView onSelectedEntityRequest() {
            return PhotoPaintView.this.currentEntityView;
        }

        public void onEntityDeselect() {
            PhotoPaintView.this.selectEntity(null);
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoPaintView$6 */
    class C20746 implements ColorPickerDelegate {
        C20746() {
        }

        public void onBeganColorPicking() {
            if (!(PhotoPaintView.this.currentEntityView instanceof TextPaintView)) {
                PhotoPaintView.this.setDimVisibility(true);
            }
        }

        public void onColorValueChanged() {
            PhotoPaintView.this.setCurrentSwatch(PhotoPaintView.this.colorPicker.getSwatch(), false);
        }

        public void onFinishedColorPicking() {
            PhotoPaintView.this.setCurrentSwatch(PhotoPaintView.this.colorPicker.getSwatch(), false);
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
    }

    public PhotoPaintView(Context context, Bitmap bitmap, int i) {
        super(context);
        this.bitmapToEdit = bitmap;
        this.orientation = i;
        this.undoStore = new UndoStore();
        this.undoStore.setDelegate(new C20711());
        this.curtainView = new FrameLayout(context);
        this.curtainView.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        this.curtainView.setVisibility(4);
        addView(this.curtainView);
        this.renderView = new RenderView(context, new Painting(getPaintingSize()), bitmap, this.orientation);
        this.renderView.setDelegate(new C20722());
        this.renderView.setUndoStore(this.undoStore);
        this.renderView.setQueue(this.queue);
        this.renderView.setVisibility(4);
        this.renderView.setBrush(this.brushes[0]);
        addView(this.renderView, LayoutHelper.createFrame(-1, -1, 51));
        this.entitiesView = new EntitiesContainerView(context, new C20733());
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
        this.textDimView.setOnClickListener(new C12564());
        this.selectionContainerView = new FrameLayout(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return false;
            }
        };
        addView(this.selectionContainerView);
        this.colorPicker = new ColorPicker(context);
        addView(this.colorPicker);
        this.colorPicker.setDelegate(new C20746());
        this.toolsView = new FrameLayout(context);
        this.toolsView.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        addView(this.toolsView, LayoutHelper.createFrame(-1, 48, 83));
        this.cancelTextView = new TextView(context);
        this.cancelTextView.setTextSize(1, 14.0f);
        this.cancelTextView.setTextColor(-1);
        this.cancelTextView.setGravity(17);
        this.cancelTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        this.cancelTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelTextView.setText(LocaleController.getString("Cancel", C0446R.string.Cancel).toUpperCase());
        this.cancelTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.toolsView.addView(this.cancelTextView, LayoutHelper.createFrame(-2, -1, 51));
        this.doneTextView = new TextView(context);
        this.doneTextView.setTextSize(1, 14.0f);
        this.doneTextView.setTextColor(-11420173);
        this.doneTextView.setGravity(17);
        this.doneTextView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        this.doneTextView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneTextView.setText(LocaleController.getString("Done", C0446R.string.Done).toUpperCase());
        this.doneTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.toolsView.addView(this.doneTextView, LayoutHelper.createFrame(-2, -1, 53));
        this.paintButton = new ImageView(context);
        this.paintButton.setScaleType(ScaleType.CENTER);
        this.paintButton.setImageResource(C0446R.drawable.photo_paint);
        this.paintButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(this.paintButton, LayoutHelper.createFrame(54, -1.0f, 17, 0.0f, 0.0f, 56.0f, 0.0f));
        this.paintButton.setOnClickListener(new C12587());
        bitmap = new ImageView(context);
        bitmap.setScaleType(ScaleType.CENTER);
        bitmap.setImageResource(C0446R.drawable.photo_sticker);
        bitmap.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(bitmap, LayoutHelper.createFrame(54, -1, 17));
        bitmap.setOnClickListener(new C12598());
        bitmap = new ImageView(context);
        bitmap.setScaleType(ScaleType.CENTER);
        bitmap.setImageResource(C0446R.drawable.photo_paint_text);
        bitmap.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.toolsView.addView(bitmap, LayoutHelper.createFrame(54, -1.0f, 17, 56.0f, 0.0f, 0.0f, 0.0f));
        bitmap.setOnClickListener(new C12609());
        this.colorPicker.setUndoEnabled(false);
        setCurrentSwatch(this.colorPicker.getSwatch(), false);
        updateSettingsButton();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentEntityView != null) {
            if (this.editingText != null) {
                closeTextEnter(true);
            } else {
                selectEntity(null);
            }
        }
        return true;
    }

    private Size getPaintingSize() {
        if (this.paintingSize != null) {
            return this.paintingSize;
        }
        float height = (float) (isSidewardOrientation() ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
        float width = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        Size size = new Size(height, width);
        size.width = 1280.0f;
        size.height = (float) Math.floor((double) ((size.width * width) / height));
        if (size.height > 1280.0f) {
            size.height = 1280.0f;
            size.width = (float) Math.floor((double) ((size.height * height) / width));
        }
        this.paintingSize = size;
        return size;
    }

    private boolean isSidewardOrientation() {
        if (this.orientation % 360 != 90) {
            if (this.orientation % 360 != 270) {
                return false;
            }
        }
        return true;
    }

    private void updateSettingsButton() {
        EntityView entityView = this.currentEntityView;
        int i = C0446R.drawable.photo_paint_brush;
        if (entityView != null) {
            int i2;
            if (this.currentEntityView instanceof StickerView) {
                i2 = C0446R.drawable.photo_flip;
            } else {
                if (this.currentEntityView instanceof TextPaintView) {
                    i2 = C0446R.drawable.photo_outline;
                }
                this.paintButton.setImageResource(C0446R.drawable.photo_paint);
                this.paintButton.setColorFilter(null);
            }
            i = i2;
            this.paintButton.setImageResource(C0446R.drawable.photo_paint);
            this.paintButton.setColorFilter(null);
        } else {
            this.paintButton.setColorFilter(new PorterDuffColorFilter(-11420173, Mode.MULTIPLY));
            this.paintButton.setImageResource(C0446R.drawable.photo_paint);
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
        this.queue.postRunnable(new Runnable() {
            public void run() {
                Looper myLooper = Looper.myLooper();
                if (myLooper != null) {
                    myLooper.quit();
                }
            }
        });
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
        if (!this.undoStore.canUndo()) {
            if (this.entitiesView.entitiesCount() <= 0) {
                return false;
            }
        }
        return true;
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
                    canvas.translate(entityView.getPosition().f24x, entityView.getPosition().f25y);
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
                        } catch (Throwable e) {
                            FileLog.m3e(e);
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

    public void maybeShowDismissalAlert(PhotoViewer photoViewer, Activity activity, final Runnable runnable) {
        if (this.editingText) {
            closeTextEnter(null);
        } else if (this.pickingSticker) {
            closeStickersView();
        } else {
            if (!hasChanges()) {
                runnable.run();
            } else if (activity != null) {
                Builder builder = new Builder((Context) activity);
                builder.setMessage(LocaleController.getString("DiscardChanges", C0446R.string.DiscardChanges));
                builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        runnable.run();
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
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
        if (this.currentEntityView instanceof TextPaintView) {
            ((TextPaintView) this.currentEntityView).setSwatch(swatch);
        }
    }

    private void setDimVisibility(final boolean z) {
        Animator ofFloat;
        if (z) {
            this.dimView.setVisibility(0);
            ofFloat = ObjectAnimator.ofFloat(this.dimView, "alpha", new float[]{0.0f, 1.0f});
        } else {
            ofFloat = ObjectAnimator.ofFloat(this.dimView, "alpha", new float[]{1.0f, 0.0f});
        }
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (z == null) {
                    PhotoPaintView.this.dimView.setVisibility(8);
                }
            }
        });
        ofFloat.setDuration(200);
        ofFloat.start();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setTextDimVisibility(final boolean z, EntityView entityView) {
        if (z && entityView != null) {
            ViewGroup viewGroup = (ViewGroup) entityView.getParent();
            if (this.textDimView.getParent() != null) {
                ((EntitiesContainerView) this.textDimView.getParent()).removeView(this.textDimView);
            }
            viewGroup.addView(this.textDimView, viewGroup.indexOfChild(entityView));
        }
        entityView.setSelectionVisibility(z ^ 1);
        if (z) {
            this.textDimView.setVisibility(0);
            entityView = ObjectAnimator.ofFloat(this.textDimView, "alpha", new float[]{0.0f, 1.0f});
        } else {
            entityView = ObjectAnimator.ofFloat(this.textDimView, "alpha", new float[]{1.0f, 0.0f});
        }
        entityView.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (z == null) {
                    PhotoPaintView.this.textDimView.setVisibility(8);
                    if (PhotoPaintView.this.textDimView.getParent() != null) {
                        ((EntitiesContainerView) PhotoPaintView.this.textDimView.getParent()).removeView(PhotoPaintView.this.textDimView);
                    }
                }
            }
        });
        entityView.setDuration(200);
        entityView.start();
    }

    protected void onMeasure(int i, int i2) {
        float height;
        int size = MeasureSpec.getSize(i);
        i2 = MeasureSpec.getSize(i2);
        setMeasuredDimension(size, i2);
        int currentActionBarHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f);
        if (this.bitmapToEdit != null) {
            height = (float) (isSidewardOrientation() != 0 ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
            i2 = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        } else {
            height = (float) size;
            i2 = (float) ((i2 - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f));
        }
        float f = (float) size;
        float floor = (float) Math.floor((double) ((f * i2) / height));
        float f2 = (float) currentActionBarHeight;
        if (floor > f2) {
            f = (float) Math.floor((double) ((height * f2) / i2));
            floor = f2;
        }
        this.renderView.measure(MeasureSpec.makeMeasureSpec((int) f, NUM), MeasureSpec.makeMeasureSpec((int) floor, NUM));
        this.entitiesView.measure(MeasureSpec.makeMeasureSpec((int) this.paintingSize.width, NUM), MeasureSpec.makeMeasureSpec((int) this.paintingSize.height, NUM));
        this.dimView.measure(i, MeasureSpec.makeMeasureSpec(currentActionBarHeight, Integer.MIN_VALUE));
        this.selectionContainerView.measure(i, MeasureSpec.makeMeasureSpec(currentActionBarHeight, NUM));
        this.colorPicker.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(currentActionBarHeight, NUM));
        this.toolsView.measure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        if (this.stickersView != 0) {
            this.stickersView.measure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, NUM));
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float height;
        i3 -= i;
        i4 -= i2;
        z = VERSION.SDK_INT >= true ? AndroidUtilities.statusBarHeight : false;
        i2 = ActionBar.getCurrentActionBarHeight();
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + z;
        int dp = (AndroidUtilities.displaySize.y - i2) - AndroidUtilities.dp(48.0f);
        if (this.bitmapToEdit != null) {
            height = (float) (isSidewardOrientation() != 0 ? this.bitmapToEdit.getHeight() : this.bitmapToEdit.getWidth());
            i2 = (float) (isSidewardOrientation() ? this.bitmapToEdit.getWidth() : this.bitmapToEdit.getHeight());
        } else {
            height = (float) i3;
            i2 = (float) ((i4 - i2) - AndroidUtilities.dp(48.0f));
        }
        float f = (float) i3;
        float f2 = (float) dp;
        if (((float) Math.floor((double) ((f * i2) / height))) > f2) {
            f = (float) Math.floor((double) ((f2 * height) / i2));
        }
        i2 = (int) Math.ceil((double) ((i3 - this.renderView.getMeasuredWidth()) / 2));
        int dp2 = ((((((i4 - currentActionBarHeight) - AndroidUtilities.dp(48.0f)) - this.renderView.getMeasuredHeight()) / 2) + currentActionBarHeight) - ActionBar.getCurrentActionBarHeight()) + AndroidUtilities.dp(8.0f);
        this.renderView.layout(i2, dp2, this.renderView.getMeasuredWidth() + i2, this.renderView.getMeasuredHeight() + dp2);
        f /= this.paintingSize.width;
        this.entitiesView.setScaleX(f);
        this.entitiesView.setScaleY(f);
        this.entitiesView.layout(i2, dp2, this.entitiesView.getMeasuredWidth() + i2, this.entitiesView.getMeasuredHeight() + dp2);
        this.dimView.layout(0, z, this.dimView.getMeasuredWidth(), this.dimView.getMeasuredHeight() + z);
        this.selectionContainerView.layout(0, z, this.selectionContainerView.getMeasuredWidth(), this.selectionContainerView.getMeasuredHeight() + z);
        this.colorPicker.layout(0, currentActionBarHeight, this.colorPicker.getMeasuredWidth(), this.colorPicker.getMeasuredHeight() + currentActionBarHeight);
        this.toolsView.layout(0, i4 - this.toolsView.getMeasuredHeight(), this.toolsView.getMeasuredWidth(), i4);
        this.curtainView.layout(0, 0, i3, dp);
        if (this.stickersView != 0) {
            this.stickersView.layout(0, z, this.stickersView.getMeasuredWidth(), this.stickersView.getMeasuredHeight() + z);
        }
        if (this.currentEntityView) {
            this.currentEntityView.updateSelectionView();
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
        if (entityView != null) {
            entityView = entityView.getPosition();
            return new Point(entityView.f24x + 200.0f, entityView.f25y + NUM);
        }
        entityView = centerPositionForEntity();
        while (true) {
            int i = 0;
            int i2 = 0;
            while (i < this.entitiesView.getChildCount()) {
                View childAt = this.entitiesView.getChildAt(i);
                if (childAt instanceof EntityView) {
                    Point position = ((EntityView) childAt).getPosition();
                    if (((float) Math.sqrt(Math.pow((double) (position.f24x - entityView.f24x), 2.0d) + Math.pow((double) (position.f25y - entityView.f25y), 2.0d))) < 100.0f) {
                        i2 = 1;
                    }
                }
                i++;
            }
            if (i2 == 0) {
                return entityView;
            }
            Object point = new Point(entityView.f24x + 200.0f, entityView.f25y + NUM);
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
                arrayList.add(tL_inputDocument);
            }
        }
        return arrayList;
    }

    private boolean selectEntity(EntityView entityView) {
        boolean z;
        if (this.currentEntityView == null) {
            z = false;
        } else if (this.currentEntityView == entityView) {
            if (this.editingText == null) {
                showMenuForEntity(this.currentEntityView);
            }
            return true;
        } else {
            this.currentEntityView.deselect();
            z = true;
        }
        this.currentEntityView = entityView;
        if (this.currentEntityView != null) {
            this.currentEntityView.select(this.selectionContainerView);
            this.entitiesView.bringViewToFront(this.currentEntityView);
            if ((this.currentEntityView instanceof TextPaintView) != null) {
                setCurrentSwatch(((TextPaintView) this.currentEntityView).getSwatch(), true);
            }
            z = true;
        }
        updateSettingsButton();
        return z;
    }

    private void removeEntity(EntityView entityView) {
        if (entityView == this.currentEntityView) {
            this.currentEntityView.deselect();
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
        if (this.currentEntityView != null) {
            EntityView entityView = null;
            Point startPositionRelativeToEntity = startPositionRelativeToEntity(this.currentEntityView);
            if (this.currentEntityView instanceof StickerView) {
                entityView = new StickerView(getContext(), (StickerView) this.currentEntityView, startPositionRelativeToEntity);
                entityView.setDelegate(this);
                this.entitiesView.addView(entityView);
            } else if (this.currentEntityView instanceof TextPaintView) {
                entityView = new TextPaintView(getContext(), (TextPaintView) this.currentEntityView, startPositionRelativeToEntity);
                entityView.setDelegate(this);
                entityView.setMaxWidth((int) (getPaintingSize().width - 20.0f));
                this.entitiesView.addView(entityView, LayoutHelper.createFrame(-2, -2.0f));
            }
            registerRemovalUndo(entityView);
            selectEntity(entityView);
            updateSettingsButton();
        }
    }

    private void openStickersView() {
        if (this.stickersView == null || this.stickersView.getVisibility() != 0) {
            this.pickingSticker = true;
            if (this.stickersView == null) {
                this.stickersView = new StickerMasksView(getContext());
                this.stickersView.setListener(new Listener() {
                    public void onTypeChanged() {
                    }

                    public void onStickerSelected(Document document) {
                        PhotoPaintView.this.closeStickersView();
                        PhotoPaintView.this.createSticker(document);
                    }
                });
                addView(this.stickersView, LayoutHelper.createFrame(-1, -1, 51));
            }
            this.stickersView.setVisibility(0);
            Animator ofFloat = ObjectAnimator.ofFloat(this.stickersView, "alpha", new float[]{0.0f, 1.0f});
            ofFloat.setDuration(200);
            ofFloat.start();
        }
    }

    private void closeStickersView() {
        if (this.stickersView != null) {
            if (this.stickersView.getVisibility() == 0) {
                this.pickingSticker = false;
                Animator ofFloat = ObjectAnimator.ofFloat(this.stickersView, "alpha", new float[]{1.0f, 0.0f});
                ofFloat.setDuration(200);
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        PhotoPaintView.this.stickersView.setVisibility(8);
                    }
                });
                ofFloat.start();
            }
        }
    }

    private Size baseStickerSize() {
        float floor = (float) Math.floor(((double) getPaintingSize().width) * 0.5d);
        return new Size(floor, floor);
    }

    private void registerRemovalUndo(final EntityView entityView) {
        this.undoStore.registerUndo(entityView.getUUID(), new Runnable() {
            public void run() {
                PhotoPaintView.this.removeEntity(entityView);
            }
        });
    }

    private void createSticker(Document document) {
        StickerPosition calculateStickerPosition = calculateStickerPosition(document);
        View stickerView = new StickerView(getContext(), calculateStickerPosition.position, calculateStickerPosition.angle, calculateStickerPosition.scale, baseStickerSize(), document);
        stickerView.setDelegate(this);
        this.entitiesView.addView(stickerView);
        registerRemovalUndo(stickerView);
        selectEntity(stickerView);
    }

    private void mirrorSticker() {
        if (this.currentEntityView instanceof StickerView) {
            ((StickerView) this.currentEntityView).mirror();
        }
    }

    private int baseFontSize() {
        return (int) (getPaintingSize().width / 9.0f);
    }

    private void createText() {
        Swatch swatch = this.colorPicker.getSwatch();
        Swatch swatch2 = new Swatch(-1, 1.0f, swatch.brushWeight);
        Swatch swatch3 = new Swatch(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, 0.85f, swatch.brushWeight);
        if (this.selectedStroke) {
            swatch2 = swatch3;
        }
        setCurrentSwatch(swatch2, true);
        View textPaintView = new TextPaintView(getContext(), startPositionRelativeToEntity(null), baseFontSize(), TtmlNode.ANONYMOUS_REGION_ID, this.colorPicker.getSwatch(), this.selectedStroke);
        textPaintView.setDelegate(this);
        textPaintView.setMaxWidth((int) (getPaintingSize().width - 20.0f));
        this.entitiesView.addView(textPaintView, LayoutHelper.createFrame(-2, -2.0f));
        registerRemovalUndo(textPaintView);
        selectEntity(textPaintView);
        editSelectedTextEntity();
    }

    private void editSelectedTextEntity() {
        if (this.currentEntityView instanceof TextPaintView) {
            if (!this.editingText) {
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
    }

    public void closeTextEnter(boolean z) {
        if (this.editingText) {
            if (this.currentEntityView instanceof TextPaintView) {
                TextPaintView textPaintView = (TextPaintView) this.currentEntityView;
                this.toolsView.setVisibility(0);
                AndroidUtilities.hideKeyboard(textPaintView.getFocusedView());
                textPaintView.getFocusedView().clearFocus();
                textPaintView.endEditing();
                if (!z) {
                    textPaintView.setText(this.initialText);
                }
                if (textPaintView.getText().trim().length()) {
                    textPaintView.setPosition(this.editedTextPosition);
                    textPaintView.setRotation(this.editedTextRotation);
                    textPaintView.setScale(this.editedTextScale);
                    this.editedTextPosition = null;
                    this.editedTextRotation = 0.0f;
                    this.editedTextScale = 0.0f;
                } else {
                    this.entitiesView.removeView(textPaintView);
                    selectEntity(null);
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
                setCurrentSwatch(new Swatch(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, 0.85f, swatch.brushWeight), true);
            } else if (!z && swatch.color == Theme.ACTION_BAR_VIDEO_EDIT_COLOR) {
                setCurrentSwatch(new Swatch(-1, 1.0f, swatch.brushWeight), true);
            }
            ((TextPaintView) this.currentEntityView).setStroke(z);
        }
    }

    private void showMenuForEntity(final EntityView entityView) {
        showPopup(new Runnable() {

            /* renamed from: org.telegram.ui.Components.PhotoPaintView$17$1 */
            class C12531 implements OnClickListener {
                C12531() {
                }

                public void onClick(View view) {
                    PhotoPaintView.this.removeEntity(entityView);
                    if (PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing() != null) {
                        PhotoPaintView.this.popupWindow.dismiss(true);
                    }
                }
            }

            /* renamed from: org.telegram.ui.Components.PhotoPaintView$17$2 */
            class C12542 implements OnClickListener {
                C12542() {
                }

                public void onClick(View view) {
                    PhotoPaintView.this.editSelectedTextEntity();
                    if (PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing() != null) {
                        PhotoPaintView.this.popupWindow.dismiss(true);
                    }
                }
            }

            /* renamed from: org.telegram.ui.Components.PhotoPaintView$17$3 */
            class C12553 implements OnClickListener {
                C12553() {
                }

                public void onClick(View view) {
                    PhotoPaintView.this.duplicateSelectedEntity();
                    if (PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing() != null) {
                        PhotoPaintView.this.popupWindow.dismiss(true);
                    }
                }
            }

            public void run() {
                View linearLayout = new LinearLayout(PhotoPaintView.this.getContext());
                linearLayout.setOrientation(0);
                View textView = new TextView(PhotoPaintView.this.getContext());
                textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
                textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                textView.setGravity(16);
                textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(14.0f), 0);
                textView.setTextSize(1, 18.0f);
                textView.setTag(Integer.valueOf(0));
                textView.setText(LocaleController.getString("PaintDelete", C0446R.string.PaintDelete));
                textView.setOnClickListener(new C12531());
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
                if (entityView instanceof TextPaintView) {
                    textView = new TextView(PhotoPaintView.this.getContext());
                    textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
                    textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    textView.setGravity(16);
                    textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
                    textView.setTextSize(1, 18.0f);
                    textView.setTag(Integer.valueOf(1));
                    textView.setText(LocaleController.getString("PaintEdit", C0446R.string.PaintEdit));
                    textView.setOnClickListener(new C12542());
                    linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
                }
                textView = new TextView(PhotoPaintView.this.getContext());
                textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
                textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                textView.setGravity(16);
                textView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
                textView.setTextSize(1, 18.0f);
                textView.setTag(Integer.valueOf(2));
                textView.setText(LocaleController.getString("PaintDuplicate", C0446R.string.PaintDuplicate));
                textView.setOnClickListener(new C12553());
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
                PhotoPaintView.this.popupLayout.addView(linearLayout);
                LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
                layoutParams.width = -2;
                layoutParams.height = -2;
                linearLayout.setLayoutParams(layoutParams);
            }
        }, entityView, 17, (int) ((entityView.getPosition().f24x - ((float) (this.entitiesView.getWidth() / 2))) * this.entitiesView.getScaleX()), ((int) (((entityView.getPosition().f25y - ((((float) entityView.getHeight()) * entityView.getScale()) / 2.0f)) - ((float) (this.entitiesView.getHeight() / 2))) * this.entitiesView.getScaleY())) - AndroidUtilities.dp(32.0f));
    }

    private FrameLayout buttonForBrush(final int i, int i2, boolean z) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        frameLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotoPaintView.this.setBrush(i);
                if (PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing() != null) {
                    PhotoPaintView.this.popupWindow.dismiss(true);
                }
            }
        });
        i = new ImageView(getContext());
        i.setImageResource(i2);
        frameLayout.addView(i, LayoutHelper.createFrame(165, 44.0f, 19, 46.0f, 0.0f, 8.0f, 0.0f));
        if (z) {
            i = new ImageView(getContext());
            i.setImageResource(C0446R.drawable.ic_ab_done);
            i.setScaleType(ScaleType.CENTER);
            frameLayout.addView(i, LayoutHelper.createFrame(50, true));
        }
        return frameLayout;
    }

    private void showBrushSettings() {
        showPopup(new Runnable() {
            public void run() {
                boolean z = false;
                View access$2500 = PhotoPaintView.this.buttonForBrush(0, C0446R.drawable.paint_radial_preview, PhotoPaintView.this.currentBrush == 0);
                PhotoPaintView.this.popupLayout.addView(access$2500);
                LayoutParams layoutParams = (LayoutParams) access$2500.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = AndroidUtilities.dp(52.0f);
                access$2500.setLayoutParams(layoutParams);
                access$2500 = PhotoPaintView.this.buttonForBrush(1, C0446R.drawable.paint_elliptical_preview, PhotoPaintView.this.currentBrush == 1);
                PhotoPaintView.this.popupLayout.addView(access$2500);
                layoutParams = (LayoutParams) access$2500.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = AndroidUtilities.dp(52.0f);
                access$2500.setLayoutParams(layoutParams);
                PhotoPaintView photoPaintView = PhotoPaintView.this;
                if (PhotoPaintView.this.currentBrush == 2) {
                    z = true;
                }
                access$2500 = photoPaintView.buttonForBrush(2, C0446R.drawable.paint_neon_preview, z);
                PhotoPaintView.this.popupLayout.addView(access$2500);
                layoutParams = (LayoutParams) access$2500.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = AndroidUtilities.dp(52.0f);
                access$2500.setLayoutParams(layoutParams);
            }
        }, this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    private FrameLayout buttonForText(final boolean z, String str, boolean z2) {
        FrameLayout anonymousClass20 = new FrameLayout(getContext()) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return true;
            }
        };
        anonymousClass20.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        anonymousClass20.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                PhotoPaintView.this.setStroke(z);
                if (PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing() != null) {
                    PhotoPaintView.this.popupWindow.dismiss(true);
                }
            }
        });
        View editTextOutline = new EditTextOutline(getContext());
        editTextOutline.setBackgroundColor(0);
        editTextOutline.setEnabled(false);
        editTextOutline.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        int i = Theme.ACTION_BAR_VIDEO_EDIT_COLOR;
        editTextOutline.setTextColor(z ? -1 : Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        if (!z) {
            i = 0;
        }
        editTextOutline.setStrokeColor(i);
        editTextOutline.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f), 0);
        editTextOutline.setTextSize(1, 18.0f);
        editTextOutline.setTypeface(null, 1);
        editTextOutline.setTag(Boolean.valueOf(z));
        editTextOutline.setText(str);
        anonymousClass20.addView(editTextOutline, LayoutHelper.createFrame(-2, -2.0f, 19, 46.0f, 0.0f, 16.0f, 0.0f));
        if (z2) {
            z = new ImageView(getContext());
            z.setImageResource(C0446R.drawable.ic_ab_done);
            z.setScaleType(ScaleType.CENTER);
            anonymousClass20.addView(z, LayoutHelper.createFrame(50, true));
        }
        return anonymousClass20;
    }

    private void showTextSettings() {
        showPopup(new Runnable() {
            public void run() {
                View access$2800 = PhotoPaintView.this.buttonForText(true, LocaleController.getString("PaintOutlined", C0446R.string.PaintOutlined), PhotoPaintView.this.selectedStroke);
                PhotoPaintView.this.popupLayout.addView(access$2800);
                LayoutParams layoutParams = (LayoutParams) access$2800.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = AndroidUtilities.dp(48.0f);
                access$2800.setLayoutParams(layoutParams);
                access$2800 = PhotoPaintView.this.buttonForText(false, LocaleController.getString("PaintRegular", C0446R.string.PaintRegular), true ^ PhotoPaintView.this.selectedStroke);
                PhotoPaintView.this.popupLayout.addView(access$2800);
                layoutParams = (LayoutParams) access$2800.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = AndroidUtilities.dp(48.0f);
                access$2800.setLayoutParams(layoutParams);
            }
        }, this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    private void showPopup(Runnable runnable, View view, int i, int i2, int i3) {
        if (this.popupWindow == null || !this.popupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                this.popupLayout = new ActionBarPopupWindowLayout(getContext());
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() == 0 && PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing()) {
                            view.getHitRect(PhotoPaintView.this.popupRect);
                            if (PhotoPaintView.this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY()) == null) {
                                PhotoPaintView.this.popupWindow.dismiss();
                            }
                        }
                        return null;
                    }
                });
                this.popupLayout.setDispatchKeyEventListener(new OnDispatchKeyEventListener() {
                    public void onDispatchKeyEvent(KeyEvent keyEvent) {
                        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == null && PhotoPaintView.this.popupWindow != null && PhotoPaintView.this.popupWindow.isShowing() != null) {
                            PhotoPaintView.this.popupWindow.dismiss();
                        }
                    }
                });
                this.popupLayout.setShowedFromBotton(true);
            }
            this.popupLayout.removeInnerViews();
            runnable.run();
            if (this.popupWindow == null) {
                this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(C0446R.style.PopupAnimation);
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                this.popupWindow.getContentView().setFocusableInTouchMode(true);
                this.popupWindow.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss() {
                        PhotoPaintView.this.popupLayout.removeInnerViews();
                    }
                });
            }
            this.popupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(view, i, i2, i3);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
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
        this.queue.postRunnable(new Runnable() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                Throwable th;
                Throwable th2;
                FaceDetector build;
                try {
                    int i = 0;
                    build = new FaceDetector.Builder(PhotoPaintView.this.getContext()).setMode(1).setLandmarkType(1).setTrackingEnabled(false).build();
                    try {
                        if (build.isOperational()) {
                            try {
                                SparseArray detect = build.detect(new Frame.Builder().setBitmap(PhotoPaintView.this.bitmapToEdit).setRotation(PhotoPaintView.this.getFrameRotation()).build());
                                ArrayList arrayList = new ArrayList();
                                Size access$3200 = PhotoPaintView.this.getPaintingSize();
                                while (i < detect.size()) {
                                    PhotoFace photoFace = new PhotoFace((Face) detect.get(detect.keyAt(i)), PhotoPaintView.this.bitmapToEdit, access$3200, PhotoPaintView.this.isSidewardOrientation());
                                    if (photoFace.isSufficient()) {
                                        arrayList.add(photoFace);
                                    }
                                    i++;
                                }
                                PhotoPaintView.this.faces = arrayList;
                                if (build != null) {
                                    build.release();
                                }
                                return;
                            } catch (Throwable th3) {
                                FileLog.m3e(th3);
                                if (build != null) {
                                    build.release();
                                }
                                return;
                            }
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m1e("face detection is not operational");
                        }
                        if (build != null) {
                            build.release();
                        }
                    } catch (Exception e) {
                        th3 = e;
                        try {
                            FileLog.m3e(th3);
                        } catch (Throwable th4) {
                            th3 = th4;
                            if (build != null) {
                                build.release();
                            }
                            throw th3;
                        }
                    }
                } catch (Throwable e2) {
                    th2 = e2;
                    build = null;
                    th3 = th2;
                    FileLog.m3e(th3);
                } catch (Throwable e22) {
                    th2 = e22;
                    build = null;
                    th3 = th2;
                    if (build != null) {
                        build.release();
                    }
                    throw th3;
                }
            }
        });
    }

    private StickerPosition calculateStickerPosition(Document document) {
        TL_maskCoords tL_maskCoords;
        PhotoPaintView photoPaintView = this;
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
        if (!(tL_maskCoords == null || photoPaintView.faces == null)) {
            if (photoPaintView.faces.size() != 0) {
                int i2 = tL_maskCoords.f46n;
                PhotoFace randomFaceWithVacantAnchor = getRandomFaceWithVacantAnchor(i2, document2.id, tL_maskCoords);
                if (randomFaceWithVacantAnchor == null) {
                    return stickerPosition;
                }
                Point pointForAnchor = randomFaceWithVacantAnchor.getPointForAnchor(i2);
                float widthForAnchor = randomFaceWithVacantAnchor.getWidthForAnchor(i2);
                float angle = randomFaceWithVacantAnchor.getAngle();
                double toRadians = (double) ((float) Math.toRadians((double) angle));
                double d = 1.5707963267948966d - toRadians;
                double d2 = (double) widthForAnchor;
                float cos = (float) ((Math.cos(d) * d2) * tL_maskCoords.f47x);
                toRadians += 1.5707963267948966d;
                return new StickerPosition(new Point((pointForAnchor.f24x + ((float) ((Math.sin(d) * d2) * tL_maskCoords.f47x))) + ((float) ((Math.cos(toRadians) * d2) * tL_maskCoords.f48y)), (pointForAnchor.f25y + cos) + ((float) ((Math.sin(toRadians) * d2) * tL_maskCoords.f48y))), (float) (((double) (widthForAnchor / baseStickerSize().width)) * tL_maskCoords.zoom), angle);
            }
        }
        return stickerPosition;
    }

    private PhotoFace getRandomFaceWithVacantAnchor(int i, long j, TL_maskCoords tL_maskCoords) {
        if (i >= 0 && i <= 3) {
            if (!this.faces.isEmpty()) {
                int size = this.faces.size();
                int nextInt = Utilities.random.nextInt(size);
                for (int i2 = size; i2 > 0; i2--) {
                    PhotoFace photoFace = (PhotoFace) this.faces.get(nextInt);
                    if (!isFaceAnchorOccupied(photoFace, i, j, tL_maskCoords)) {
                        return photoFace;
                    }
                    nextInt = (nextInt + 1) % size;
                }
                return null;
            }
        }
        return null;
    }

    private boolean isFaceAnchorOccupied(PhotoFace photoFace, int i, long j, TL_maskCoords tL_maskCoords) {
        tL_maskCoords = photoFace.getPointForAnchor(i);
        if (tL_maskCoords == null) {
            return true;
        }
        photoFace = photoFace.getWidthForAnchor(0) * 1.1f;
        for (int i2 = 0; i2 < this.entitiesView.getChildCount(); i2++) {
            View childAt = this.entitiesView.getChildAt(i2);
            if (childAt instanceof StickerView) {
                StickerView stickerView = (StickerView) childAt;
                if (stickerView.getAnchor() == i) {
                    Point position = stickerView.getPosition();
                    float hypot = (float) Math.hypot((double) (position.f24x - tL_maskCoords.f24x), (double) (position.f25y - tL_maskCoords.f25y));
                    if ((j == stickerView.getSticker().id || this.faces.size() > 1) && hypot < photoFace) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
