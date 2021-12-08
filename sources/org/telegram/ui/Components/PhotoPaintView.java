package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.math.BigInteger;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Paint.Brush;
import org.telegram.ui.Components.Paint.PhotoFace;
import org.telegram.ui.Components.Paint.RenderView;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.UndoStore;
import org.telegram.ui.Components.Paint.Views.ColorPicker;
import org.telegram.ui.Components.Paint.Views.EntitiesContainerView;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Paint.Views.StickerView;
import org.telegram.ui.Components.Paint.Views.TextPaintView;
import org.telegram.ui.PhotoViewer;

public class PhotoPaintView extends FrameLayout implements EntityView.EntityViewDelegate {
    private static final int gallery_menu_done = 1;
    private float baseScale;
    private Bitmap bitmapToEdit;
    private Swatch brushSwatch;
    private Brush[] brushes = {new Brush.Radial(), new Brush.Elliptical(), new Brush.Neon(), new Brush.Arrow()};
    private TextView cancelTextView;
    /* access modifiers changed from: private */
    public ColorPicker colorPicker;
    private Animator colorPickerAnimator;
    int currentBrush;
    private MediaController.CropState currentCropState;
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
    private Bitmap facesBitmap;
    private boolean ignoreLayout;
    private boolean inBubbleMode;
    private String initialText;
    private BigInteger lcm;
    private int originalBitmapRotation;
    private ImageView paintButton;
    private Size paintingSize;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private int[] pos = new int[2];
    private DispatchQueue queue;
    private RenderView renderView;
    private final Theme.ResourcesProvider resourcesProvider;
    private int selectedTextType = 2;
    private FrameLayout selectionContainerView;
    private float[] temp = new float[2];
    /* access modifiers changed from: private */
    public FrameLayout textDimView;
    private FrameLayout toolsView;
    private float transformX;
    private float transformY;
    /* access modifiers changed from: private */
    public UndoStore undoStore;

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: org.telegram.ui.Components.Paint.Views.StickerView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v19, resolved type: org.telegram.ui.Components.Paint.Views.TextPaintView} */
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PhotoPaintView(android.content.Context r27, android.graphics.Bitmap r28, android.graphics.Bitmap r29, int r30, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r31, org.telegram.messenger.MediaController.CropState r32, java.lang.Runnable r33, org.telegram.ui.ActionBar.Theme.ResourcesProvider r34) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r31
            r26.<init>(r27)
            r4 = 4
            org.telegram.ui.Components.Paint.Brush[] r5 = new org.telegram.ui.Components.Paint.Brush[r4]
            org.telegram.ui.Components.Paint.Brush$Radial r6 = new org.telegram.ui.Components.Paint.Brush$Radial
            r6.<init>()
            r7 = 0
            r5[r7] = r6
            org.telegram.ui.Components.Paint.Brush$Elliptical r6 = new org.telegram.ui.Components.Paint.Brush$Elliptical
            r6.<init>()
            r8 = 1
            r5[r8] = r6
            org.telegram.ui.Components.Paint.Brush$Neon r6 = new org.telegram.ui.Components.Paint.Brush$Neon
            r6.<init>()
            r9 = 2
            r5[r9] = r6
            org.telegram.ui.Components.Paint.Brush$Arrow r6 = new org.telegram.ui.Components.Paint.Brush$Arrow
            r6.<init>()
            r10 = 3
            r5[r10] = r6
            r0.brushes = r5
            float[] r5 = new float[r9]
            r0.temp = r5
            r0.selectedTextType = r9
            int[] r5 = new int[r9]
            r0.pos = r5
            r5 = r34
            r0.resourcesProvider = r5
            boolean r6 = r1 instanceof org.telegram.ui.BubbleActivity
            r0.inBubbleMode = r6
            r6 = r32
            r0.currentCropState = r6
            org.telegram.messenger.DispatchQueue r10 = new org.telegram.messenger.DispatchQueue
            java.lang.String r11 = "Paint"
            r10.<init>(r11)
            r0.queue = r10
            r10 = r30
            r0.originalBitmapRotation = r10
            r0.bitmapToEdit = r2
            r11 = r29
            r0.facesBitmap = r11
            org.telegram.ui.Components.Paint.UndoStore r12 = new org.telegram.ui.Components.Paint.UndoStore
            r12.<init>()
            r0.undoStore = r12
            org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda12 r13 = new org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda12
            r13.<init>(r0)
            r12.setDelegate(r13)
            android.widget.FrameLayout r12 = new android.widget.FrameLayout
            r12.<init>(r1)
            r0.curtainView = r12
            r13 = 570425344(0x22000000, float:1.7347235E-18)
            r12.setBackgroundColor(r13)
            android.widget.FrameLayout r12 = r0.curtainView
            r12.setVisibility(r4)
            android.widget.FrameLayout r12 = r0.curtainView
            r13 = -1
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14)
            r0.addView(r12, r14)
            org.telegram.ui.Components.Paint.RenderView r12 = new org.telegram.ui.Components.Paint.RenderView
            org.telegram.ui.Components.Paint.Painting r14 = new org.telegram.ui.Components.Paint.Painting
            org.telegram.ui.Components.Size r15 = r26.getPaintingSize()
            r14.<init>(r15)
            r12.<init>(r1, r14, r2)
            r0.renderView = r12
            org.telegram.ui.Components.PhotoPaintView$1 r14 = new org.telegram.ui.Components.PhotoPaintView$1
            r15 = r33
            r14.<init>(r15)
            r12.setDelegate(r14)
            org.telegram.ui.Components.Paint.RenderView r12 = r0.renderView
            org.telegram.ui.Components.Paint.UndoStore r14 = r0.undoStore
            r12.setUndoStore(r14)
            org.telegram.ui.Components.Paint.RenderView r12 = r0.renderView
            org.telegram.messenger.DispatchQueue r14 = r0.queue
            r12.setQueue(r14)
            org.telegram.ui.Components.Paint.RenderView r12 = r0.renderView
            r12.setVisibility(r4)
            org.telegram.ui.Components.Paint.RenderView r12 = r0.renderView
            org.telegram.ui.Components.Paint.Brush[] r14 = r0.brushes
            r14 = r14[r7]
            r12.setBrush(r14)
            org.telegram.ui.Components.Paint.RenderView r12 = r0.renderView
            r14 = 51
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r13, (int) r14)
            r0.addView(r12, r4)
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r4 = new org.telegram.ui.Components.Paint.Views.EntitiesContainerView
            org.telegram.ui.Components.PhotoPaintView$2 r12 = new org.telegram.ui.Components.PhotoPaintView$2
            r12.<init>()
            r4.<init>(r1, r12)
            r0.entitiesView = r4
            r0.addView(r4)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.dimView = r4
            r12 = 0
            r4.setAlpha(r12)
            android.widget.FrameLayout r4 = r0.dimView
            r9 = 1711276032(0x66000000, float:1.5111573E23)
            r4.setBackgroundColor(r9)
            android.widget.FrameLayout r4 = r0.dimView
            r14 = 8
            r4.setVisibility(r14)
            android.widget.FrameLayout r4 = r0.dimView
            r0.addView(r4)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.textDimView = r4
            r4.setAlpha(r12)
            android.widget.FrameLayout r4 = r0.textDimView
            r4.setBackgroundColor(r9)
            android.widget.FrameLayout r4 = r0.textDimView
            r4.setVisibility(r14)
            android.widget.FrameLayout r4 = r0.textDimView
            org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda14 r9 = new org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda14
            r9.<init>(r0)
            r4.setOnClickListener(r9)
            org.telegram.ui.Components.PhotoPaintView$3 r4 = new org.telegram.ui.Components.PhotoPaintView$3
            r4.<init>(r1)
            r0.selectionContainerView = r4
            r0.addView(r4)
            org.telegram.ui.Components.Paint.Views.ColorPicker r4 = new org.telegram.ui.Components.Paint.Views.ColorPicker
            r4.<init>(r1)
            r0.colorPicker = r4
            r0.addView(r4)
            org.telegram.ui.Components.Paint.Views.ColorPicker r4 = r0.colorPicker
            org.telegram.ui.Components.PhotoPaintView$4 r9 = new org.telegram.ui.Components.PhotoPaintView$4
            r9.<init>()
            r4.setDelegate(r9)
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.toolsView = r4
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r4.setBackgroundColor(r9)
            android.widget.FrameLayout r4 = r0.toolsView
            r9 = 48
            r12 = 83
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r9, (int) r12)
            r0.addView(r4, r9)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.cancelTextView = r4
            r9 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r8, r9)
            android.widget.TextView r4 = r0.cancelTextView
            r4.setTextColor(r13)
            android.widget.TextView r4 = r0.cancelTextView
            r12 = 17
            r4.setGravity(r12)
            android.widget.TextView r4 = r0.cancelTextView
            r14 = -12763843(0xffffffffff3d3d3d, float:-2.5154206E38)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r14, r7)
            r4.setBackgroundDrawable(r12)
            android.widget.TextView r4 = r0.cancelTextView
            r12 = 1101004800(0x41a00000, float:20.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r4.setPadding(r14, r7, r8, r7)
            android.widget.TextView r4 = r0.cancelTextView
            java.lang.String r8 = "Cancel"
            r14 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r14)
            java.lang.String r8 = r8.toUpperCase()
            r4.setText(r8)
            android.widget.TextView r4 = r0.cancelTextView
            java.lang.String r8 = "fonts/rmedium.ttf"
            android.graphics.Typeface r14 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r4.setTypeface(r14)
            android.widget.FrameLayout r4 = r0.toolsView
            android.widget.TextView r14 = r0.cancelTextView
            r12 = -2
            r7 = 51
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r13, (int) r7)
            r4.addView(r14, r7)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.doneTextView = r4
            r7 = 1
            r4.setTextSize(r7, r9)
            android.widget.TextView r4 = r0.doneTextView
            java.lang.String r7 = "dialogFloatingButton"
            int r7 = r0.getThemedColor(r7)
            r4.setTextColor(r7)
            android.widget.TextView r4 = r0.doneTextView
            r7 = 17
            r4.setGravity(r7)
            android.widget.TextView r4 = r0.doneTextView
            r7 = -12763843(0xffffffffff3d3d3d, float:-2.5154206E38)
            r9 = 0
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7, r9)
            r4.setBackgroundDrawable(r7)
            android.widget.TextView r4 = r0.doneTextView
            r7 = 1101004800(0x41a00000, float:20.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r4.setPadding(r14, r9, r7, r9)
            android.widget.TextView r4 = r0.doneTextView
            java.lang.String r7 = "Done"
            r9 = 2131625321(0x7f0e0569, float:1.8877847E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r9)
            java.lang.String r7 = r7.toUpperCase()
            r4.setText(r7)
            android.widget.TextView r4 = r0.doneTextView
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r4.setTypeface(r7)
            android.widget.FrameLayout r4 = r0.toolsView
            android.widget.TextView r7 = r0.doneTextView
            r8 = 53
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r13, (int) r8)
            r4.addView(r7, r8)
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            r0.paintButton = r4
            android.widget.ImageView$ScaleType r7 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r7)
            android.widget.ImageView r4 = r0.paintButton
            r7 = 2131165964(0x7var_c, float:1.794616E38)
            r4.setImageResource(r7)
            android.widget.ImageView r4 = r0.paintButton
            r7 = 1090519039(0x40ffffff, float:7.9999995)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7)
            r4.setBackgroundDrawable(r8)
            android.widget.FrameLayout r4 = r0.toolsView
            android.widget.ImageView r8 = r0.paintButton
            r19 = 54
            r20 = -1082130432(0xffffffffbvar_, float:-1.0)
            r21 = 17
            r22 = 0
            r23 = 0
            r24 = 1113587712(0x42600000, float:56.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r4.addView(r8, r9)
            android.widget.ImageView r4 = r0.paintButton
            org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda15 r8 = new org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda15
            r8.<init>(r0)
            r4.setOnClickListener(r8)
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            android.widget.ImageView$ScaleType r8 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r8)
            r8 = 2131165968(0x7var_, float:1.7946168E38)
            r4.setImageResource(r8)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7)
            r4.setBackgroundDrawable(r8)
            android.widget.FrameLayout r8 = r0.toolsView
            r9 = 54
            r12 = 17
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r13, (int) r12)
            r8.addView(r4, r9)
            org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda16 r8 = new org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda16
            r8.<init>(r0)
            r4.setOnClickListener(r8)
            android.widget.ImageView r8 = new android.widget.ImageView
            r8.<init>(r1)
            android.widget.ImageView$ScaleType r9 = android.widget.ImageView.ScaleType.CENTER
            r8.setScaleType(r9)
            r9 = 2131165966(0x7var_e, float:1.7946164E38)
            r8.setImageResource(r9)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7)
            r8.setBackgroundDrawable(r7)
            android.widget.FrameLayout r7 = r0.toolsView
            r22 = 1113587712(0x42600000, float:56.0)
            r24 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r7.addView(r8, r9)
            org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda17 r7 = new org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda17
            r7.<init>(r0)
            r8.setOnClickListener(r7)
            org.telegram.ui.Components.Paint.Views.ColorPicker r7 = r0.colorPicker
            r9 = 0
            r7.setUndoEnabled(r9)
            org.telegram.ui.Components.Paint.Views.ColorPicker r7 = r0.colorPicker
            org.telegram.ui.Components.Paint.Swatch r7 = r7.getSwatch()
            r0.setCurrentSwatch(r7, r9)
            r26.updateSettingsButton()
            if (r3 == 0) goto L_0x03a2
            boolean r7 = r31.isEmpty()
            if (r7 != 0) goto L_0x03a2
            r7 = 0
            int r9 = r31.size()
        L_0x02c1:
            if (r7 >= r9) goto L_0x03a2
            java.lang.Object r12 = r3.get(r7)
            org.telegram.messenger.VideoEditedInfo$MediaEntity r12 = (org.telegram.messenger.VideoEditedInfo.MediaEntity) r12
            byte r13 = r12.type
            if (r13 != 0) goto L_0x02f1
            java.lang.Object r13 = r12.parentObject
            org.telegram.tgnet.TLRPC$Document r14 = r12.document
            r1 = 0
            org.telegram.ui.Components.Paint.Views.StickerView r13 = r0.createSticker(r13, r14, r1)
            byte r1 = r12.subType
            r14 = 2
            r1 = r1 & r14
            if (r1 == 0) goto L_0x02df
            r13.mirror()
        L_0x02df:
            r1 = r13
            android.view.ViewGroup$LayoutParams r14 = r1.getLayoutParams()
            r18 = r1
            int r1 = r12.viewWidth
            r14.width = r1
            int r1 = r12.viewHeight
            r14.height = r1
            r1 = r18
            goto L_0x0323
        L_0x02f1:
            byte r1 = r12.type
            r13 = 1
            if (r1 != r13) goto L_0x0396
            r1 = 0
            org.telegram.ui.Components.Paint.Views.TextPaintView r14 = r0.createText(r1)
            byte r1 = r12.subType
            r1 = r1 & r13
            if (r1 == 0) goto L_0x0302
            r1 = 0
            goto L_0x030d
        L_0x0302:
            byte r1 = r12.subType
            r16 = 4
            r1 = r1 & 4
            if (r1 == 0) goto L_0x030c
            r1 = 2
            goto L_0x030d
        L_0x030c:
            r1 = 1
        L_0x030d:
            r14.setType(r1)
            java.lang.String r13 = r12.text
            r14.setText(r13)
            org.telegram.ui.Components.Paint.Swatch r13 = r14.getSwatch()
            r18 = r1
            int r1 = r12.color
            r13.color = r1
            r14.setSwatch(r13)
            r1 = r14
        L_0x0323:
            float r13 = r12.x
            org.telegram.ui.Components.Size r14 = r0.paintingSize
            float r14 = r14.width
            float r13 = r13 * r14
            int r14 = r12.viewWidth
            float r14 = (float) r14
            float r2 = r12.scale
            r18 = 1065353216(0x3var_, float:1.0)
            float r2 = r18 - r2
            float r14 = r14 * r2
            r2 = 1073741824(0x40000000, float:2.0)
            float r14 = r14 / r2
            float r13 = r13 - r14
            r1.setX(r13)
            float r13 = r12.y
            org.telegram.ui.Components.Size r14 = r0.paintingSize
            float r14 = r14.height
            float r13 = r13 * r14
            int r14 = r12.viewHeight
            float r14 = (float) r14
            float r2 = r12.scale
            float r18 = r18 - r2
            float r14 = r14 * r18
            r2 = 1073741824(0x40000000, float:2.0)
            float r14 = r14 / r2
            float r13 = r13 - r14
            r1.setY(r13)
            org.telegram.ui.Components.Point r2 = new org.telegram.ui.Components.Point
            float r13 = r1.getX()
            int r14 = r12.viewWidth
            r17 = 2
            int r14 = r14 / 2
            float r14 = (float) r14
            float r13 = r13 + r14
            float r14 = r1.getY()
            int r3 = r12.viewHeight
            int r3 = r3 / 2
            float r3 = (float) r3
            float r14 = r14 + r3
            r2.<init>(r13, r14)
            r1.setPosition(r2)
            float r2 = r12.scale
            r1.setScaleX(r2)
            float r2 = r12.scale
            r1.setScaleY(r2)
            float r2 = r12.rotation
            float r2 = -r2
            double r2 = (double) r2
            r13 = 4614256656552045848(0x400921fb54442d18, double:3.NUM)
            java.lang.Double.isNaN(r2)
            double r2 = r2 / r13
            r13 = 4640537203540230144(0xNUM, double:180.0)
            double r2 = r2 * r13
            float r2 = (float) r2
            r1.setRotation(r2)
            goto L_0x0398
        L_0x0396:
            r17 = 2
        L_0x0398:
            int r7 = r7 + 1
            r1 = r27
            r2 = r28
            r3 = r31
            goto L_0x02c1
        L_0x03a2:
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r1 = r0.entitiesView
            r2 = 4
            r1.setVisibility(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoPaintView.<init>(android.content.Context, android.graphics.Bitmap, android.graphics.Bitmap, int, java.util.ArrayList, org.telegram.messenger.MediaController$CropState, java.lang.Runnable, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2474lambda$new$0$orgtelegramuiComponentsPhotoPaintView() {
        this.colorPicker.setUndoEnabled(this.undoStore.canUndo());
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2475lambda$new$1$orgtelegramuiComponentsPhotoPaintView(View v) {
        closeTextEnter(true);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2476lambda$new$2$orgtelegramuiComponentsPhotoPaintView(View v) {
        selectEntity((EntityView) null);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2477lambda$new$3$orgtelegramuiComponentsPhotoPaintView(View v) {
        openStickersView();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2478lambda$new$4$orgtelegramuiComponentsPhotoPaintView(View v) {
        createText(true);
    }

    public void onResume() {
        this.renderView.redraw();
    }

    public boolean onTouch(MotionEvent ev) {
        if (this.currentEntityView != null) {
            if (this.editingText) {
                closeTextEnter(true);
            } else {
                selectEntity((EntityView) null);
            }
        }
        float x2 = ((ev.getX() - this.renderView.getTranslationX()) - ((float) (getMeasuredWidth() / 2))) / this.renderView.getScaleX();
        float y2 = (((ev.getY() - this.renderView.getTranslationY()) - ((float) (getMeasuredHeight() / 2))) + ((float) AndroidUtilities.dp(32.0f))) / this.renderView.getScaleY();
        float rotation = (float) Math.toRadians((double) (-this.renderView.getRotation()));
        double d = (double) x2;
        double cos = Math.cos((double) rotation);
        Double.isNaN(d);
        double d2 = d * cos;
        double d3 = (double) y2;
        double sin = Math.sin((double) rotation);
        Double.isNaN(d3);
        float x = ((float) (d2 - (d3 * sin))) + ((float) (this.renderView.getMeasuredWidth() / 2));
        double d4 = (double) x2;
        double sin2 = Math.sin((double) rotation);
        Double.isNaN(d4);
        double d5 = d4 * sin2;
        double d6 = (double) y2;
        double cos2 = Math.cos((double) rotation);
        Double.isNaN(d6);
        MotionEvent event = MotionEvent.obtain(0, 0, ev.getActionMasked(), x, ((float) (d5 + (d6 * cos2))) + ((float) (this.renderView.getMeasuredHeight() / 2)), 0);
        this.renderView.onTouch(event);
        event.recycle();
        return true;
    }

    private Size getPaintingSize() {
        Size size = this.paintingSize;
        if (size != null) {
            return size;
        }
        float width = (float) this.bitmapToEdit.getWidth();
        float height = (float) this.bitmapToEdit.getHeight();
        Size size2 = new Size(width, height);
        size2.width = 1280.0f;
        size2.height = (float) Math.floor((double) ((size2.width * height) / width));
        if (size2.height > 1280.0f) {
            size2.height = 1280.0f;
            size2.width = (float) Math.floor((double) ((size2.height * width) / height));
        }
        this.paintingSize = size2;
        return size2;
    }

    private void updateSettingsButton() {
        int resource = NUM;
        EntityView entityView = this.currentEntityView;
        if (entityView != null) {
            if (entityView instanceof StickerView) {
                resource = NUM;
            } else if (entityView instanceof TextPaintView) {
                resource = NUM;
            }
            this.paintButton.setImageResource(NUM);
            this.paintButton.setColorFilter((ColorFilter) null);
        } else {
            Swatch swatch = this.brushSwatch;
            if (swatch != null) {
                setCurrentSwatch(swatch, true);
                this.brushSwatch = null;
            }
            this.paintButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            this.paintButton.setImageResource(NUM);
        }
        this.colorPicker.setSettingsButtonImage(resource);
    }

    public void updateColors() {
        ImageView imageView = this.paintButton;
        if (!(imageView == null || imageView.getColorFilter() == null)) {
            this.paintButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
        }
        TextView textView = this.doneTextView;
        if (textView != null) {
            textView.setTextColor(getThemedColor("dialogFloatingButton"));
        }
    }

    public void init() {
        this.entitiesView.setVisibility(0);
        this.renderView.setVisibility(0);
        if (this.facesBitmap != null) {
            detectFaces();
        }
    }

    public void shutdown() {
        this.renderView.shutdown();
        this.entitiesView.setVisibility(8);
        this.selectionContainerView.setVisibility(8);
        this.queue.postRunnable(PhotoPaintView$$ExternalSyntheticLambda9.INSTANCE);
    }

    static /* synthetic */ void lambda$shutdown$5() {
        Looper looper = Looper.myLooper();
        if (looper != null) {
            looper.quit();
        }
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public FrameLayout getCurtainView() {
        return this.curtainView;
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

    public boolean hasChanges() {
        return this.undoStore.canUndo();
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x01df  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0212  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0249  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Bitmap getBitmap(java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r22, android.graphics.Bitmap[] r23) {
        /*
            r21 = this;
            r1 = r21
            r2 = r22
            org.telegram.ui.Components.Paint.RenderView r0 = r1.renderView
            android.graphics.Bitmap r3 = r0.getResultBitmap()
            java.math.BigInteger r0 = java.math.BigInteger.ONE
            r1.lcm = r0
            if (r3 == 0) goto L_0x0256
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r0 = r1.entitiesView
            int r0 = r0.entitiesCount()
            if (r0 <= 0) goto L_0x0256
            r0 = 0
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r4 = r1.entitiesView
            int r4 = r4.getChildCount()
            r5 = 0
        L_0x0020:
            if (r5 >= r4) goto L_0x0254
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r6 = r1.entitiesView
            android.view.View r6 = r6.getChildAt(r5)
            boolean r7 = r6 instanceof org.telegram.ui.Components.Paint.Views.EntityView
            if (r7 != 0) goto L_0x0030
            r17 = r0
            goto L_0x01ce
        L_0x0030:
            r7 = r6
            org.telegram.ui.Components.Paint.Views.EntityView r7 = (org.telegram.ui.Components.Paint.Views.EntityView) r7
            org.telegram.ui.Components.Point r8 = r7.getPosition()
            r10 = 0
            r11 = 2
            if (r2 == 0) goto L_0x01d2
            org.telegram.messenger.VideoEditedInfo$MediaEntity r12 = new org.telegram.messenger.VideoEditedInfo$MediaEntity
            r12.<init>()
            boolean r13 = r7 instanceof org.telegram.ui.Components.Paint.Views.TextPaintView
            r14 = 1
            if (r13 == 0) goto L_0x0075
            r12.type = r14
            r13 = r7
            org.telegram.ui.Components.Paint.Views.TextPaintView r13 = (org.telegram.ui.Components.Paint.Views.TextPaintView) r13
            java.lang.String r15 = r13.getText()
            r12.text = r15
            int r15 = r13.getType()
            if (r15 != 0) goto L_0x005d
            byte r9 = r12.subType
            r9 = r9 | r14
            byte r9 = (byte) r9
            r12.subType = r9
            goto L_0x0066
        L_0x005d:
            if (r15 != r11) goto L_0x0066
            byte r9 = r12.subType
            r9 = r9 | 4
            byte r9 = (byte) r9
            r12.subType = r9
        L_0x0066:
            org.telegram.ui.Components.Paint.Swatch r9 = r13.getSwatch()
            int r9 = r9.color
            r12.color = r9
            int r9 = r13.getTextSize()
            r12.fontSize = r9
            goto L_0x00dd
        L_0x0075:
            boolean r9 = r7 instanceof org.telegram.ui.Components.Paint.Views.StickerView
            if (r9 == 0) goto L_0x01cc
            r12.type = r10
            r9 = r7
            org.telegram.ui.Components.Paint.Views.StickerView r9 = (org.telegram.ui.Components.Paint.Views.StickerView) r9
            org.telegram.ui.Components.Size r13 = r9.getBaseSize()
            float r15 = r13.width
            r12.width = r15
            float r15 = r13.height
            r12.height = r15
            org.telegram.tgnet.TLRPC$Document r15 = r9.getSticker()
            r12.document = r15
            java.lang.Object r15 = r9.getParentObject()
            r12.parentObject = r15
            org.telegram.tgnet.TLRPC$Document r15 = r9.getSticker()
            java.io.File r16 = org.telegram.messenger.FileLoader.getPathToAttach(r15, r14)
            java.lang.String r10 = r16.getAbsolutePath()
            r12.text = r10
            boolean r10 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r15, r14)
            if (r10 == 0) goto L_0x00d0
            byte r10 = r12.subType
            r10 = r10 | r14
            byte r10 = (byte) r10
            r12.subType = r10
            long r17 = r9.getDuration()
            r19 = 0
            int r10 = (r17 > r19 ? 1 : (r17 == r19 ? 0 : -1))
            if (r10 == 0) goto L_0x00d0
            java.math.BigInteger r10 = java.math.BigInteger.valueOf(r17)
            java.math.BigInteger r14 = r1.lcm
            java.math.BigInteger r14 = r14.multiply(r10)
            java.math.BigInteger r11 = r1.lcm
            java.math.BigInteger r11 = r11.gcd(r10)
            java.math.BigInteger r11 = r14.divide(r11)
            r1.lcm = r11
        L_0x00d0:
            boolean r10 = r9.isMirrored()
            if (r10 == 0) goto L_0x00dd
            byte r10 = r12.subType
            r11 = 2
            r10 = r10 | r11
            byte r10 = (byte) r10
            r12.subType = r10
        L_0x00dd:
            r2.add(r12)
            float r9 = r6.getScaleX()
            float r10 = r6.getScaleY()
            float r11 = r6.getX()
            float r13 = r6.getY()
            int r14 = r6.getWidth()
            r12.viewWidth = r14
            int r14 = r6.getHeight()
            r12.viewHeight = r14
            int r14 = r6.getWidth()
            float r14 = (float) r14
            float r14 = r14 * r9
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r15 = r1.entitiesView
            int r15 = r15.getMeasuredWidth()
            float r15 = (float) r15
            float r14 = r14 / r15
            r12.width = r14
            int r14 = r6.getHeight()
            float r14 = (float) r14
            float r14 = r14 * r10
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r15 = r1.entitiesView
            int r15 = r15.getMeasuredHeight()
            float r15 = (float) r15
            float r14 = r14 / r15
            r12.height = r14
            int r14 = r6.getWidth()
            float r14 = (float) r14
            r15 = 1065353216(0x3var_, float:1.0)
            float r17 = r15 - r9
            float r14 = r14 * r17
            r17 = 1073741824(0x40000000, float:2.0)
            float r14 = r14 / r17
            float r14 = r14 + r11
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r15 = r1.entitiesView
            int r15 = r15.getMeasuredWidth()
            float r15 = (float) r15
            float r14 = r14 / r15
            r12.x = r14
            int r14 = r6.getHeight()
            float r14 = (float) r14
            r15 = 1065353216(0x3var_, float:1.0)
            float r15 = r15 - r10
            float r14 = r14 * r15
            float r14 = r14 / r17
            float r14 = r14 + r13
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r15 = r1.entitiesView
            int r15 = r15.getMeasuredHeight()
            float r15 = (float) r15
            float r14 = r14 / r15
            r12.y = r14
            float r14 = r6.getRotation()
            float r14 = -r14
            double r14 = (double) r14
            r17 = 4580687790476533049(0x3var_dvar_a2529d39, double:0.NUM)
            java.lang.Double.isNaN(r14)
            double r14 = r14 * r17
            float r14 = (float) r14
            r12.rotation = r14
            int r14 = r6.getWidth()
            r15 = 2
            int r14 = r14 / r15
            float r14 = (float) r14
            float r14 = r14 + r11
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r15 = r1.entitiesView
            int r15 = r15.getMeasuredWidth()
            float r15 = (float) r15
            float r14 = r14 / r15
            r12.textViewX = r14
            int r14 = r6.getHeight()
            r15 = 2
            int r14 = r14 / r15
            float r14 = (float) r14
            float r14 = r14 + r13
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r15 = r1.entitiesView
            int r15 = r15.getMeasuredHeight()
            float r15 = (float) r15
            float r14 = r14 / r15
            r12.textViewY = r14
            int r14 = r12.viewWidth
            float r14 = (float) r14
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r15 = r1.entitiesView
            int r15 = r15.getMeasuredWidth()
            float r15 = (float) r15
            float r14 = r14 / r15
            r12.textViewWidth = r14
            int r14 = r12.viewHeight
            float r14 = (float) r14
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r15 = r1.entitiesView
            int r15 = r15.getMeasuredHeight()
            float r15 = (float) r15
            float r14 = r14 / r15
            r12.textViewHeight = r14
            r12.scale = r9
            r14 = 0
            r15 = r23[r14]
            if (r15 != 0) goto L_0x01c9
            int r15 = r3.getWidth()
            int r14 = r3.getHeight()
            r17 = r0
            android.graphics.Bitmap$Config r0 = r3.getConfig()
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r15, r14, r0)
            r14 = 0
            r23[r14] = r0
            android.graphics.Canvas r0 = new android.graphics.Canvas
            r15 = r23[r14]
            r0.<init>(r15)
            r14 = 0
            r15 = 0
            r0.drawBitmap(r3, r14, r14, r15)
            goto L_0x01d6
        L_0x01c9:
            r17 = r0
            goto L_0x01d4
        L_0x01cc:
            r17 = r0
        L_0x01ce:
            r0 = r17
            goto L_0x0250
        L_0x01d2:
            r17 = r0
        L_0x01d4:
            r0 = r17
        L_0x01d6:
            if (r0 != 0) goto L_0x01df
            android.graphics.Canvas r9 = new android.graphics.Canvas
            r9.<init>(r3)
            r0 = r9
            goto L_0x01e0
        L_0x01df:
            r9 = r0
        L_0x01e0:
            r9.save()
            float r0 = r8.x
            float r10 = r8.y
            r9.translate(r0, r10)
            float r0 = r6.getScaleX()
            float r10 = r6.getScaleY()
            r9.scale(r0, r10)
            float r0 = r6.getRotation()
            r9.rotate(r0)
            int r0 = r7.getWidth()
            int r0 = -r0
            r10 = 2
            int r0 = r0 / r10
            float r0 = (float) r0
            int r11 = r7.getHeight()
            int r11 = -r11
            int r11 = r11 / r10
            float r10 = (float) r11
            r9.translate(r0, r10)
            boolean r0 = r6 instanceof org.telegram.ui.Components.Paint.Views.TextPaintView
            if (r0 == 0) goto L_0x0249
            int r0 = r6.getWidth()
            int r10 = r6.getHeight()
            android.graphics.Bitmap$Config r11 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r10 = org.telegram.messenger.Bitmaps.createBitmap(r0, r10, r11)
            android.graphics.Canvas r0 = new android.graphics.Canvas
            r0.<init>(r10)
            r11 = r0
            r6.draw(r11)
            android.graphics.Rect r0 = new android.graphics.Rect
            int r12 = r10.getWidth()
            int r13 = r10.getHeight()
            r14 = 0
            r0.<init>(r14, r14, r12, r13)
            r12 = 0
            r9.drawBitmap(r10, r12, r0, r12)
            r11.setBitmap(r12)     // Catch:{ Exception -> 0x023f }
            goto L_0x0245
        L_0x023f:
            r0 = move-exception
            r12 = r0
            r0 = r12
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0245:
            r10.recycle()
            goto L_0x024c
        L_0x0249:
            r6.draw(r9)
        L_0x024c:
            r9.restore()
            r0 = r9
        L_0x0250:
            int r5 = r5 + 1
            goto L_0x0020
        L_0x0254:
            r17 = r0
        L_0x0256:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoPaintView.getBitmap(java.util.ArrayList, android.graphics.Bitmap[]):android.graphics.Bitmap");
    }

    public long getLcm() {
        return this.lcm.longValue();
    }

    public void maybeShowDismissalAlert(PhotoViewer photoViewer, Activity parentActivity, Runnable okRunnable) {
        if (this.editingText) {
            closeTextEnter(false);
        } else if (!hasChanges()) {
            okRunnable.run();
        } else if (parentActivity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
            builder.setMessage(LocaleController.getString("PhotoEditorDiscardAlert", NUM));
            builder.setTitle(LocaleController.getString("DiscardChanges", NUM));
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", NUM), new PhotoPaintView$$ExternalSyntheticLambda0(okRunnable));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            photoViewer.showAlertDialog(builder);
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentSwatch(Swatch swatch, boolean updateInterface) {
        this.renderView.setColor(swatch.color);
        this.renderView.setBrushSize(swatch.brushWeight);
        if (updateInterface) {
            if (this.brushSwatch == null && this.paintButton.getColorFilter() != null) {
                this.brushSwatch = this.colorPicker.getSwatch();
            }
            this.colorPicker.setSwatch(swatch);
        }
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof TextPaintView) {
            ((TextPaintView) entityView).setSwatch(swatch);
        }
    }

    /* access modifiers changed from: private */
    public void setDimVisibility(final boolean visible) {
        Animator animator;
        if (visible) {
            this.dimView.setVisibility(0);
            animator = ObjectAnimator.ofFloat(this.dimView, View.ALPHA, new float[]{0.0f, 1.0f});
        } else {
            animator = ObjectAnimator.ofFloat(this.dimView, View.ALPHA, new float[]{1.0f, 0.0f});
        }
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    PhotoPaintView.this.dimView.setVisibility(8);
                }
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    private void setTextDimVisibility(final boolean visible, EntityView view) {
        Animator animator;
        if (visible && view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (this.textDimView.getParent() != null) {
                ((EntitiesContainerView) this.textDimView.getParent()).removeView(this.textDimView);
            }
            parent.addView(this.textDimView, parent.indexOfChild(view));
        }
        view.setSelectionVisibility(!visible);
        if (visible) {
            this.textDimView.setVisibility(0);
            animator = ObjectAnimator.ofFloat(this.textDimView, View.ALPHA, new float[]{0.0f, 1.0f});
        } else {
            animator = ObjectAnimator.ofFloat(this.textDimView, View.ALPHA, new float[]{1.0f, 0.0f});
        }
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    PhotoPaintView.this.textDimView.setVisibility(8);
                    if (PhotoPaintView.this.textDimView.getParent() != null) {
                        ((EntitiesContainerView) PhotoPaintView.this.textDimView.getParent()).removeView(PhotoPaintView.this.textDimView);
                    }
                }
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float bitmapH;
        float bitmapW;
        this.ignoreLayout = true;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        int maxHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f);
        Bitmap bitmap = this.bitmapToEdit;
        if (bitmap != null) {
            bitmapW = (float) bitmap.getWidth();
            bitmapH = (float) this.bitmapToEdit.getHeight();
        } else {
            bitmapW = (float) width;
            bitmapH = (float) ((height - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f));
        }
        float renderWidth = (float) width;
        float renderHeight = (float) Math.floor((double) ((renderWidth * bitmapH) / bitmapW));
        if (renderHeight > ((float) maxHeight)) {
            renderHeight = (float) maxHeight;
            renderWidth = (float) Math.floor((double) ((renderHeight * bitmapW) / bitmapH));
        }
        this.renderView.measure(View.MeasureSpec.makeMeasureSpec((int) renderWidth, NUM), View.MeasureSpec.makeMeasureSpec((int) renderHeight, NUM));
        float f = renderWidth / this.paintingSize.width;
        this.baseScale = f;
        this.entitiesView.setScaleX(f);
        this.entitiesView.setScaleY(this.baseScale);
        this.entitiesView.measure(View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.width, NUM), View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.height, NUM));
        this.dimView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(maxHeight, Integer.MIN_VALUE));
        EntityView entityView = this.currentEntityView;
        if (entityView != null) {
            entityView.updateSelectionView();
        }
        this.selectionContainerView.measure(View.MeasureSpec.makeMeasureSpec((int) renderWidth, NUM), View.MeasureSpec.makeMeasureSpec((int) renderHeight, NUM));
        this.colorPicker.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(maxHeight, NUM));
        this.toolsView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        this.curtainView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(maxHeight, NUM));
        this.ignoreLayout = false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int status = (Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
        int actionBarHeight = ActionBar.getCurrentActionBarHeight();
        int actionBarHeight2 = actionBarHeight + status;
        int dp = (AndroidUtilities.displaySize.y - actionBarHeight) - AndroidUtilities.dp(48.0f);
        int x = (int) Math.ceil((double) ((width - this.renderView.getMeasuredWidth()) / 2));
        int y = ((((height - actionBarHeight2) - AndroidUtilities.dp(48.0f)) - this.renderView.getMeasuredHeight()) / 2) + AndroidUtilities.dp(8.0f) + status;
        RenderView renderView2 = this.renderView;
        renderView2.layout(x, y, renderView2.getMeasuredWidth() + x, this.renderView.getMeasuredHeight() + y);
        int x2 = ((this.renderView.getMeasuredWidth() - this.entitiesView.getMeasuredWidth()) / 2) + x;
        int y2 = ((this.renderView.getMeasuredHeight() - this.entitiesView.getMeasuredHeight()) / 2) + y;
        EntitiesContainerView entitiesContainerView = this.entitiesView;
        entitiesContainerView.layout(x2, y2, entitiesContainerView.getMeasuredWidth() + x2, this.entitiesView.getMeasuredHeight() + y2);
        FrameLayout frameLayout = this.dimView;
        frameLayout.layout(0, status, frameLayout.getMeasuredWidth(), this.dimView.getMeasuredHeight() + status);
        FrameLayout frameLayout2 = this.selectionContainerView;
        frameLayout2.layout(x, y, frameLayout2.getMeasuredWidth() + x, this.selectionContainerView.getMeasuredHeight() + y);
        ColorPicker colorPicker2 = this.colorPicker;
        colorPicker2.layout(0, actionBarHeight2, colorPicker2.getMeasuredWidth(), this.colorPicker.getMeasuredHeight() + actionBarHeight2);
        FrameLayout frameLayout3 = this.toolsView;
        frameLayout3.layout(0, height - frameLayout3.getMeasuredHeight(), this.toolsView.getMeasuredWidth(), height);
        FrameLayout frameLayout4 = this.curtainView;
        frameLayout4.layout(0, y, frameLayout4.getMeasuredWidth(), this.curtainView.getMeasuredHeight() + y);
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    public boolean onEntitySelected(EntityView entityView) {
        return selectEntity(entityView);
    }

    public boolean onEntityLongClicked(EntityView entityView) {
        showMenuForEntity(entityView);
        return true;
    }

    public float[] getTransformedTouch(float x, float y) {
        float x2 = x - ((float) (AndroidUtilities.displaySize.x / 2));
        float y2 = y - ((float) (AndroidUtilities.displaySize.y / 2));
        float rotation = (float) Math.toRadians((double) (-this.entitiesView.getRotation()));
        float[] fArr = this.temp;
        double d = (double) x2;
        double cos = Math.cos((double) rotation);
        Double.isNaN(d);
        double d2 = d * cos;
        double d3 = (double) y2;
        double sin = Math.sin((double) rotation);
        Double.isNaN(d3);
        fArr[0] = ((float) (d2 - (d3 * sin))) + ((float) (AndroidUtilities.displaySize.x / 2));
        float[] fArr2 = this.temp;
        double d4 = (double) x2;
        double sin2 = Math.sin((double) rotation);
        Double.isNaN(d4);
        double d5 = d4 * sin2;
        double d6 = (double) y2;
        double cos2 = Math.cos((double) rotation);
        Double.isNaN(d6);
        fArr2[1] = ((float) (d5 + (d6 * cos2))) + ((float) (AndroidUtilities.displaySize.y / 2));
        return this.temp;
    }

    public int[] getCenterLocation(EntityView entityView) {
        return getCenterLocationInWindow(entityView);
    }

    public boolean allowInteraction(EntityView entityView) {
        return !this.editingText;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        View view = child;
        boolean restore = false;
        if (view != this.renderView && view != this.entitiesView && view != this.selectionContainerView) {
            Canvas canvas2 = canvas;
        } else if (this.currentCropState != null) {
            canvas.save();
            int status = (Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
            int actionBarHeight2 = ActionBar.getCurrentActionBarHeight() + status;
            int vw = child.getMeasuredWidth();
            int vh = child.getMeasuredHeight();
            int tr = this.currentCropState.transformRotation;
            if (tr == 90 || tr == 270) {
                int temp2 = vw;
                vw = vh;
                vh = temp2;
            }
            int w = (int) (((((float) vw) * this.currentCropState.cropPw) * child.getScaleX()) / this.currentCropState.cropScale);
            int h = (int) (((((float) vh) * this.currentCropState.cropPh) * child.getScaleY()) / this.currentCropState.cropScale);
            float x = ((float) Math.ceil((double) ((getMeasuredWidth() - w) / 2))) + this.transformX;
            float y = ((float) (((((getMeasuredHeight() - actionBarHeight2) - AndroidUtilities.dp(48.0f)) - h) / 2) + AndroidUtilities.dp(8.0f) + status)) + this.transformY;
            canvas.clipRect(Math.max(0.0f, x), Math.max(0.0f, y), Math.min(((float) w) + x, (float) getMeasuredWidth()), Math.min((float) getMeasuredHeight(), ((float) h) + y));
            restore = true;
        } else {
            Canvas canvas3 = canvas;
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (restore) {
            canvas.restore();
        }
        return result;
    }

    private Point centerPositionForEntity() {
        Size paintingSize2 = getPaintingSize();
        float x = paintingSize2.width / 2.0f;
        float y = paintingSize2.height / 2.0f;
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            float rotation = (float) Math.toRadians((double) (-(((float) cropState.transformRotation) + this.currentCropState.cropRotate)));
            double d = (double) this.currentCropState.cropPx;
            double cos = Math.cos((double) rotation);
            Double.isNaN(d);
            double d2 = d * cos;
            double d3 = (double) this.currentCropState.cropPy;
            double sin = Math.sin((double) rotation);
            Double.isNaN(d3);
            double d4 = (double) this.currentCropState.cropPx;
            double sin2 = Math.sin((double) rotation);
            Double.isNaN(d4);
            double d5 = d4 * sin2;
            double d6 = (double) this.currentCropState.cropPy;
            double cos2 = Math.cos((double) rotation);
            Double.isNaN(d6);
            x -= paintingSize2.width * ((float) (d2 - (d3 * sin)));
            y -= paintingSize2.height * ((float) (d5 + (d6 * cos2)));
        }
        return new Point(x, y);
    }

    private Point startPositionRelativeToEntity(EntityView entityView) {
        float offset = 200.0f;
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            offset = 200.0f / cropState.cropScale;
        }
        if (entityView != null) {
            Point position = entityView.getPosition();
            return new Point(position.x + offset, position.y + offset);
        }
        float minimalDistance = 100.0f;
        MediaController.CropState cropState2 = this.currentCropState;
        if (cropState2 != null) {
            minimalDistance = 100.0f / cropState2.cropScale;
        }
        Point position2 = centerPositionForEntity();
        while (true) {
            boolean occupied = false;
            for (int index = 0; index < this.entitiesView.getChildCount(); index++) {
                View view = this.entitiesView.getChildAt(index);
                if (view instanceof EntityView) {
                    Point location = ((EntityView) view).getPosition();
                    if (((float) Math.sqrt(Math.pow((double) (location.x - position2.x), 2.0d) + Math.pow((double) (location.y - position2.y), 2.0d))) < minimalDistance) {
                        occupied = true;
                    }
                }
            }
            if (!occupied) {
                return position2;
            }
            position2 = new Point(position2.x + offset, position2.y + offset);
        }
    }

    public ArrayList<TLRPC.InputDocument> getMasks() {
        ArrayList<TLRPC.InputDocument> result = null;
        int count = this.entitiesView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.entitiesView.getChildAt(a);
            if (child instanceof StickerView) {
                TLRPC.Document document = ((StickerView) child).getSticker();
                if (result == null) {
                    result = new ArrayList<>();
                }
                TLRPC.TL_inputDocument inputDocument = new TLRPC.TL_inputDocument();
                inputDocument.id = document.id;
                inputDocument.access_hash = document.access_hash;
                inputDocument.file_reference = document.file_reference;
                if (inputDocument.file_reference == null) {
                    inputDocument.file_reference = new byte[0];
                }
                result.add(inputDocument);
            }
        }
        return result;
    }

    public void setTransform(float scale, float trX, float trY, float imageWidth, float imageHeight) {
        View view;
        float rotation;
        float rotation2;
        float tx;
        float f = trX;
        float f2 = trY;
        this.transformX = f;
        this.transformY = f2;
        int a = 0;
        while (a < 3) {
            float tx2 = 1.0f;
            if (a == 0) {
                view = this.entitiesView;
            } else if (a == 1) {
                view = this.selectionContainerView;
            } else {
                view = this.renderView;
            }
            MediaController.CropState cropState = this.currentCropState;
            if (cropState != null) {
                float additionlScale = 1.0f * cropState.cropScale;
                int w = view.getMeasuredWidth();
                int h = view.getMeasuredHeight();
                int tr = this.currentCropState.transformRotation;
                int fw = w;
                int rotatedW = w;
                int fh = h;
                int rotatedH = h;
                if (tr == 90 || tr == 270) {
                    int temp2 = fw;
                    rotatedW = fh;
                    fw = fh;
                    rotatedH = temp2;
                    fh = temp2;
                }
                float sc = Math.max(imageWidth / ((float) ((int) (((float) fw) * this.currentCropState.cropPw))), imageHeight / ((float) ((int) (((float) fh) * this.currentCropState.cropPh))));
                float additionlScale2 = additionlScale * sc;
                float tx3 = f + (this.currentCropState.cropPx * ((float) rotatedW) * scale * sc * this.currentCropState.cropScale);
                rotation = this.currentCropState.cropRotate + ((float) tr);
                tx = f2 + (this.currentCropState.cropPy * ((float) rotatedH) * scale * sc * this.currentCropState.cropScale);
                rotation2 = tx3;
                tx2 = additionlScale2;
            } else {
                if (a == 0) {
                    tx2 = 1.0f * this.baseScale;
                }
                rotation2 = trX;
                tx = trY;
                rotation = 0.0f;
            }
            view.setScaleX(scale * tx2);
            view.setScaleY(scale * tx2);
            view.setTranslationX(rotation2);
            view.setTranslationY(tx);
            view.setRotation(rotation);
            view.invalidate();
            a++;
            f = trX;
        }
        invalidate();
    }

    /* access modifiers changed from: private */
    public boolean selectEntity(EntityView entityView) {
        boolean changed = false;
        EntityView entityView2 = this.currentEntityView;
        if (entityView2 != null) {
            if (entityView2 == entityView) {
                if (!this.editingText) {
                    showMenuForEntity(entityView2);
                }
                return true;
            }
            entityView2.deselect();
            changed = true;
        }
        EntityView oldEntity = this.currentEntityView;
        this.currentEntityView = entityView;
        if ((oldEntity instanceof TextPaintView) && TextUtils.isEmpty(((TextPaintView) oldEntity).getText())) {
            m2481x59ab8b73(oldEntity);
        }
        EntityView entityView3 = this.currentEntityView;
        if (entityView3 != null) {
            entityView3.select(this.selectionContainerView);
            this.entitiesView.bringViewToFront(this.currentEntityView);
            EntityView entityView4 = this.currentEntityView;
            if (entityView4 instanceof TextPaintView) {
                setCurrentSwatch(((TextPaintView) entityView4).getSwatch(), true);
            }
            changed = true;
        }
        updateSettingsButton();
        return changed;
    }

    /* access modifiers changed from: private */
    /* renamed from: removeEntity */
    public void m2481x59ab8b73(EntityView entityView) {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Components.Paint.Views.TextPaintView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: org.telegram.ui.Components.Paint.Views.TextPaintView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: org.telegram.ui.Components.Paint.Views.TextPaintView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: org.telegram.ui.Components.Paint.Views.StickerView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.ui.Components.Paint.Views.TextPaintView} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void duplicateSelectedEntity() {
        /*
            r6 = this;
            org.telegram.ui.Components.Paint.Views.EntityView r0 = r6.currentEntityView
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            r1 = 0
            org.telegram.ui.Components.Point r0 = r6.startPositionRelativeToEntity(r0)
            org.telegram.ui.Components.Paint.Views.EntityView r2 = r6.currentEntityView
            boolean r3 = r2 instanceof org.telegram.ui.Components.Paint.Views.StickerView
            if (r3 == 0) goto L_0x0027
            org.telegram.ui.Components.Paint.Views.StickerView r2 = new org.telegram.ui.Components.Paint.Views.StickerView
            android.content.Context r3 = r6.getContext()
            org.telegram.ui.Components.Paint.Views.EntityView r4 = r6.currentEntityView
            org.telegram.ui.Components.Paint.Views.StickerView r4 = (org.telegram.ui.Components.Paint.Views.StickerView) r4
            r2.<init>(r3, r4, r0)
            r2.setDelegate(r6)
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r3 = r6.entitiesView
            r3.addView(r2)
            r1 = r2
            goto L_0x0056
        L_0x0027:
            boolean r2 = r2 instanceof org.telegram.ui.Components.Paint.Views.TextPaintView
            if (r2 == 0) goto L_0x0056
            org.telegram.ui.Components.Paint.Views.TextPaintView r2 = new org.telegram.ui.Components.Paint.Views.TextPaintView
            android.content.Context r3 = r6.getContext()
            org.telegram.ui.Components.Paint.Views.EntityView r4 = r6.currentEntityView
            org.telegram.ui.Components.Paint.Views.TextPaintView r4 = (org.telegram.ui.Components.Paint.Views.TextPaintView) r4
            r2.<init>(r3, r4, r0)
            r2.setDelegate(r6)
            org.telegram.ui.Components.Size r3 = r6.getPaintingSize()
            float r3 = r3.width
            r4 = 1101004800(0x41a00000, float:20.0)
            float r3 = r3 - r4
            int r3 = (int) r3
            r2.setMaxWidth(r3)
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r3 = r6.entitiesView
            r4 = -2
            r5 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5)
            r3.addView(r2, r4)
            r1 = r2
            goto L_0x0057
        L_0x0056:
        L_0x0057:
            r6.registerRemovalUndo(r1)
            r6.selectEntity(r1)
            r6.updateSettingsButton()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoPaintView.duplicateSelectedEntity():void");
    }

    private void openStickersView() {
        StickerMasksAlert stickerMasksAlert = new StickerMasksAlert(getContext(), this.facesBitmap == null, this.resourcesProvider);
        stickerMasksAlert.setDelegate(new PhotoPaintView$$ExternalSyntheticLambda13(this));
        stickerMasksAlert.setOnDismissListener(new PhotoPaintView$$ExternalSyntheticLambda11(this));
        stickerMasksAlert.show();
        onOpenCloseStickersAlert(true);
    }

    /* renamed from: lambda$openStickersView$7$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2479x3df5var_(Object parentObject, TLRPC.Document sticker) {
        createSticker(parentObject, sticker, true);
    }

    /* renamed from: lambda$openStickersView$8$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2480x23375var_(DialogInterface dialog) {
        onOpenCloseStickersAlert(false);
    }

    /* access modifiers changed from: protected */
    public void onOpenCloseStickersAlert(boolean open) {
    }

    /* access modifiers changed from: protected */
    public void onTextAdd() {
    }

    private Size baseStickerSize() {
        double d = (double) getPaintingSize().width;
        Double.isNaN(d);
        float side = (float) Math.floor(d * 0.5d);
        return new Size(side, side);
    }

    private void registerRemovalUndo(EntityView entityView) {
        this.undoStore.registerUndo(entityView.getUUID(), new PhotoPaintView$$ExternalSyntheticLambda7(this, entityView));
    }

    private StickerView createSticker(Object parentObject, TLRPC.Document sticker, boolean select) {
        StickerPosition position = calculateStickerPosition(sticker);
        StickerView view = new StickerView(getContext(), position.position, position.angle, position.scale, baseStickerSize(), sticker, parentObject) {
            /* access modifiers changed from: protected */
            public void didSetAnimatedSticker(RLottieDrawable drawable) {
                PhotoPaintView.this.didSetAnimatedSticker(drawable);
            }
        };
        view.setDelegate(this);
        this.entitiesView.addView(view);
        if (select) {
            registerRemovalUndo(view);
            selectEntity(view);
        }
        return view;
    }

    /* access modifiers changed from: protected */
    public void didSetAnimatedSticker(RLottieDrawable drawable) {
    }

    /* access modifiers changed from: private */
    public void mirrorSticker() {
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof StickerView) {
            ((StickerView) entityView).mirror();
        }
    }

    private TextPaintView createText(boolean select) {
        Swatch swatch;
        onTextAdd();
        Swatch currentSwatch = this.colorPicker.getSwatch();
        int i = this.selectedTextType;
        if (i == 0) {
            swatch = new Swatch(-16777216, 0.85f, currentSwatch.brushWeight);
        } else if (i == 1) {
            swatch = new Swatch(-1, 1.0f, currentSwatch.brushWeight);
        } else {
            swatch = new Swatch(-1, 1.0f, currentSwatch.brushWeight);
        }
        Size paintingSize2 = getPaintingSize();
        TextPaintView view = new TextPaintView(getContext(), startPositionRelativeToEntity((EntityView) null), (int) (paintingSize2.width / 9.0f), "", swatch, this.selectedTextType);
        view.setDelegate(this);
        view.setMaxWidth((int) (paintingSize2.width - 20.0f));
        this.entitiesView.addView(view, LayoutHelper.createFrame(-2, -2.0f));
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            view.scale(1.0f / cropState.cropScale);
            view.rotate(-(((float) this.currentCropState.transformRotation) + this.currentCropState.cropRotate));
        }
        if (select) {
            registerRemovalUndo(view);
            selectEntity(view);
            editSelectedTextEntity();
        }
        setCurrentSwatch(swatch, true);
        return view;
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
            MediaController.CropState cropState = this.currentCropState;
            if (cropState != null) {
                textPaintView.setRotation(-(((float) cropState.transformRotation) + this.currentCropState.cropRotate));
                textPaintView.setScale(1.0f / this.currentCropState.cropScale);
            } else {
                textPaintView.setRotation(0.0f);
                textPaintView.setScale(1.0f);
            }
            this.toolsView.setVisibility(8);
            setTextDimVisibility(true, textPaintView);
            textPaintView.beginEditing();
            View view = textPaintView.getFocusedView();
            view.requestFocus();
            AndroidUtilities.showKeyboard(view);
        }
    }

    public void closeTextEnter(boolean apply) {
        if (this.editingText) {
            EntityView entityView = this.currentEntityView;
            if (entityView instanceof TextPaintView) {
                TextPaintView textPaintView = (TextPaintView) entityView;
                this.toolsView.setVisibility(0);
                AndroidUtilities.hideKeyboard(textPaintView.getFocusedView());
                textPaintView.getFocusedView().clearFocus();
                textPaintView.endEditing();
                if (!apply) {
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

    private void setBrush(int brush) {
        RenderView renderView2 = this.renderView;
        Brush[] brushArr = this.brushes;
        this.currentBrush = brush;
        renderView2.setBrush(brushArr[brush]);
    }

    private void setType(int type) {
        this.selectedTextType = type;
        if (this.currentEntityView instanceof TextPaintView) {
            Swatch currentSwatch = this.colorPicker.getSwatch();
            if (type == 0 && currentSwatch.color == -1) {
                setCurrentSwatch(new Swatch(-16777216, 0.85f, currentSwatch.brushWeight), true);
            } else if ((type == 1 || type == 2) && currentSwatch.color == -16777216) {
                setCurrentSwatch(new Swatch(-1, 1.0f, currentSwatch.brushWeight), true);
            }
            ((TextPaintView) this.currentEntityView).setType(type);
        }
    }

    private int[] getCenterLocationInWindow(View view) {
        view.getLocationInWindow(this.pos);
        float rotation = view.getRotation();
        MediaController.CropState cropState = this.currentCropState;
        float rotation2 = (float) Math.toRadians((double) (rotation + (cropState != null ? cropState.cropRotate + ((float) this.currentCropState.transformRotation) : 0.0f)));
        float width = ((float) view.getWidth()) * view.getScaleX() * this.entitiesView.getScaleX();
        float height = ((float) view.getHeight()) * view.getScaleY() * this.entitiesView.getScaleY();
        double d = (double) width;
        double cos = Math.cos((double) rotation2);
        Double.isNaN(d);
        double d2 = d * cos;
        double d3 = (double) height;
        double sin = Math.sin((double) rotation2);
        Double.isNaN(d3);
        double d4 = (double) width;
        double sin2 = Math.sin((double) rotation2);
        Double.isNaN(d4);
        double d5 = d4 * sin2;
        double d6 = (double) height;
        double cos2 = Math.cos((double) rotation2);
        Double.isNaN(d6);
        int[] iArr = this.pos;
        iArr[0] = (int) (((float) iArr[0]) + (((float) (d2 - (d3 * sin))) / 2.0f));
        iArr[1] = (int) (((float) iArr[1]) + (((float) (d5 + (d6 * cos2))) / 2.0f));
        return iArr;
    }

    public float getCropRotation() {
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            return cropState.cropRotate + ((float) this.currentCropState.transformRotation);
        }
        return 0.0f;
    }

    private void showMenuForEntity(EntityView entityView) {
        int[] pos2 = getCenterLocationInWindow(entityView);
        showPopup(new PhotoPaintView$$ExternalSyntheticLambda8(this, entityView), this, 51, pos2[0], pos2[1] - AndroidUtilities.dp(32.0f));
    }

    /* renamed from: lambda$showMenuForEntity$13$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2486xbCLASSNAMEa01(EntityView entityView) {
        LinearLayout parent = new LinearLayout(getContext());
        parent.setOrientation(0);
        TextView deleteView = new TextView(getContext());
        deleteView.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
        deleteView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        deleteView.setGravity(16);
        deleteView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(14.0f), 0);
        deleteView.setTextSize(1, 18.0f);
        deleteView.setTag(0);
        deleteView.setText(LocaleController.getString("PaintDelete", NUM));
        deleteView.setOnClickListener(new PhotoPaintView$$ExternalSyntheticLambda1(this, entityView));
        parent.addView(deleteView, LayoutHelper.createLinear(-2, 48));
        if (entityView instanceof TextPaintView) {
            TextView editView = new TextView(getContext());
            editView.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
            editView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            editView.setGravity(16);
            editView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            editView.setTextSize(1, 18.0f);
            editView.setTag(1);
            editView.setText(LocaleController.getString("PaintEdit", NUM));
            editView.setOnClickListener(new PhotoPaintView$$ExternalSyntheticLambda18(this));
            parent.addView(editView, LayoutHelper.createLinear(-2, 48));
        }
        TextView duplicateView = new TextView(getContext());
        duplicateView.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
        duplicateView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        duplicateView.setGravity(16);
        duplicateView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
        duplicateView.setTextSize(1, 18.0f);
        duplicateView.setTag(2);
        duplicateView.setText(LocaleController.getString("PaintDuplicate", NUM));
        duplicateView.setOnClickListener(new PhotoPaintView$$ExternalSyntheticLambda19(this));
        parent.addView(duplicateView, LayoutHelper.createLinear(-2, 48));
        this.popupLayout.addView(parent);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
        params.width = -2;
        params.height = -2;
        parent.setLayoutParams(params);
    }

    /* renamed from: lambda$showMenuForEntity$10$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2483xc5cfdbe(EntityView entityView, View v) {
        m2481x59ab8b73(entityView);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* renamed from: lambda$showMenuForEntity$11$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2484xvar_e6c7f(View v) {
        editSelectedTextEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* renamed from: lambda$showMenuForEntity$12$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2485xd6dfdb40(View v) {
        duplicateSelectedEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    private LinearLayout buttonForBrush(int brush, int icon, String text, boolean selected) {
        LinearLayout button = new LinearLayout(getContext()) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return true;
            }
        };
        int i = 0;
        button.setOrientation(0);
        button.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        button.setOnClickListener(new PhotoPaintView$$ExternalSyntheticLambda20(this, brush));
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(icon);
        imageView.setColorFilter(getThemedColor("actionBarDefaultSubmenuItem"));
        button.addView(imageView, LayoutHelper.createLinear(-2, -2, 19, 16, 0, 16, 0));
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
        textView.setTextSize(1, 16.0f);
        textView.setText(text);
        textView.setMinWidth(AndroidUtilities.dp(70.0f));
        button.addView(textView, LayoutHelper.createLinear(-2, -2, 19, 0, 0, 16, 0));
        ImageView check = new ImageView(getContext());
        check.setImageResource(NUM);
        check.setScaleType(ImageView.ScaleType.CENTER);
        check.setColorFilter(new PorterDuffColorFilter(getThemedColor("radioBackgroundChecked"), PorterDuff.Mode.MULTIPLY));
        if (!selected) {
            i = 4;
        }
        check.setVisibility(i);
        button.addView(check, LayoutHelper.createLinear(50, -1));
        return button;
    }

    /* renamed from: lambda$buttonForBrush$14$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2471xb19dd1f5(int brush, View v) {
        setBrush(brush);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    public void showBrushSettings() {
        showPopup(new PhotoPaintView$$ExternalSyntheticLambda5(this), this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    /* renamed from: lambda$showBrushSettings$15$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2482xae966b73() {
        boolean z = false;
        this.popupLayout.addView(buttonForBrush(0, NUM, LocaleController.getString("PaintPen", NUM), this.currentBrush == 0), LayoutHelper.createLinear(-1, 54));
        this.popupLayout.addView(buttonForBrush(1, NUM, LocaleController.getString("PaintMarker", NUM), this.currentBrush == 1), LayoutHelper.createLinear(-1, 54));
        this.popupLayout.addView(buttonForBrush(2, NUM, LocaleController.getString("PaintNeon", NUM), this.currentBrush == 2), LayoutHelper.createLinear(-1, 54));
        String string = LocaleController.getString("PaintArrow", NUM);
        if (this.currentBrush == 3) {
            z = true;
        }
        this.popupLayout.addView(buttonForBrush(3, NUM, string, z), LayoutHelper.createLinear(-1, 54));
    }

    private LinearLayout buttonForText(int type, String text, int icon, boolean selected) {
        LinearLayout button = new LinearLayout(getContext()) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return true;
            }
        };
        button.setOrientation(0);
        button.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        int i = type;
        button.setOnClickListener(new PhotoPaintView$$ExternalSyntheticLambda21(this, type));
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(icon);
        imageView.setColorFilter(getThemedColor("actionBarDefaultSubmenuItem"));
        button.addView(imageView, LayoutHelper.createLinear(-2, -2, 19, 16, 0, 16, 0));
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
        textView.setTextSize(1, 16.0f);
        textView.setText(text);
        button.addView(textView, LayoutHelper.createLinear(-2, -2, 19, 0, 0, 16, 0));
        if (selected) {
            ImageView check = new ImageView(getContext());
            check.setImageResource(NUM);
            check.setScaleType(ImageView.ScaleType.CENTER);
            check.setColorFilter(new PorterDuffColorFilter(getThemedColor("radioBackgroundChecked"), PorterDuff.Mode.MULTIPLY));
            button.addView(check, LayoutHelper.createLinear(50, -1));
        }
        return button;
    }

    /* renamed from: lambda$buttonForText$16$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2472x41484eb0(int type, View v) {
        setType(type);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    public void showTextSettings() {
        showPopup(new PhotoPaintView$$ExternalSyntheticLambda6(this), this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    /* renamed from: lambda$showTextSettings$17$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2490xdd81e6ee() {
        int icon;
        String text;
        for (int a = 0; a < 3; a++) {
            boolean z = true;
            if (a == 0) {
                text = LocaleController.getString("PaintOutlined", NUM);
                icon = NUM;
            } else if (a == 1) {
                text = LocaleController.getString("PaintRegular", NUM);
                icon = NUM;
            } else {
                text = LocaleController.getString("PaintFramed", NUM);
                icon = NUM;
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
            if (this.selectedTextType != a) {
                z = false;
            }
            actionBarPopupWindowLayout.addView(buttonForText(a, text, icon, z), LayoutHelper.createLinear(-1, 48));
        }
    }

    private void showPopup(Runnable setupRunnable, View parent, int gravity, int x, int y) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
                this.popupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new PhotoPaintView$$ExternalSyntheticLambda2(this));
                this.popupLayout.setDispatchKeyEventListener(new PhotoPaintView$$ExternalSyntheticLambda10(this));
                this.popupLayout.setShownFromBotton(true);
            }
            this.popupLayout.removeInnerViews();
            setupRunnable.run();
            if (this.popupWindow == null) {
                ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow = actionBarPopupWindow2;
                actionBarPopupWindow2.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(NUM);
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                this.popupWindow.getContentView().setFocusableInTouchMode(true);
                this.popupWindow.setOnDismissListener(new PhotoPaintView$$ExternalSyntheticLambda3(this));
            }
            this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            if ((gravity & 48) != 0) {
                x -= this.popupLayout.getMeasuredWidth() / 2;
                y -= this.popupLayout.getMeasuredHeight();
            }
            this.popupWindow.showAtLocation(parent, gravity, x, y);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    /* renamed from: lambda$showPopup$18$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ boolean m2487lambda$showPopup$18$orgtelegramuiComponentsPhotoPaintView(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        v.getHitRect(this.popupRect);
        if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    /* renamed from: lambda$showPopup$19$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2488lambda$showPopup$19$orgtelegramuiComponentsPhotoPaintView(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    /* renamed from: lambda$showPopup$20$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2489lambda$showPopup$20$orgtelegramuiComponentsPhotoPaintView() {
        this.popupLayout.removeInnerViews();
    }

    private int getFrameRotation() {
        switch (this.originalBitmapRotation) {
            case 90:
                return 1;
            case 180:
                return 2;
            case 270:
                return 3;
            default:
                return 0;
        }
    }

    private boolean isSidewardOrientation() {
        int i = this.originalBitmapRotation;
        return i % 360 == 90 || i % 360 == 270;
    }

    private void detectFaces() {
        this.queue.postRunnable(new PhotoPaintView$$ExternalSyntheticLambda4(this));
    }

    /* renamed from: lambda$detectFaces$21$org-telegram-ui-Components-PhotoPaintView  reason: not valid java name */
    public /* synthetic */ void m2473lambda$detectFaces$21$orgtelegramuiComponentsPhotoPaintView() {
        FaceDetector faceDetector = null;
        try {
            faceDetector = new FaceDetector.Builder(getContext()).setMode(1).setLandmarkType(1).setTrackingEnabled(false).build();
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
            try {
                SparseArray<Face> faces2 = faceDetector.detect(new Frame.Builder().setBitmap(this.facesBitmap).setRotation(getFrameRotation()).build());
                ArrayList<PhotoFace> result = new ArrayList<>();
                Size targetSize = getPaintingSize();
                for (int i = 0; i < faces2.size(); i++) {
                    PhotoFace face = new PhotoFace(faces2.get(faces2.keyAt(i)), this.facesBitmap, targetSize, isSidewardOrientation());
                    if (face.isSufficient()) {
                        result.add(face);
                    }
                }
                this.faces = result;
                if (faceDetector == null) {
                    return;
                }
                faceDetector.release();
            } catch (Throwable e) {
                FileLog.e(e);
                if (faceDetector != null) {
                    faceDetector.release();
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            if (faceDetector == null) {
            }
        } catch (Throwable th) {
            if (faceDetector != null) {
                faceDetector.release();
            }
            throw th;
        }
    }

    private StickerPosition calculateStickerPosition(TLRPC.Document document) {
        float baseScale2;
        float rotation;
        ArrayList<PhotoFace> arrayList;
        TLRPC.Document document2 = document;
        TLRPC.TL_maskCoords maskCoords = null;
        int a = 0;
        while (true) {
            if (a >= document2.attributes.size()) {
                break;
            }
            TLRPC.DocumentAttribute attribute = document2.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                maskCoords = attribute.mask_coords;
                break;
            }
            a++;
        }
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            rotation = -(((float) cropState.transformRotation) + this.currentCropState.cropRotate);
            baseScale2 = 0.75f / this.currentCropState.cropScale;
        } else {
            rotation = 0.0f;
            baseScale2 = 0.75f;
        }
        StickerPosition defaultPosition = new StickerPosition(centerPositionForEntity(), baseScale2, rotation);
        if (maskCoords == null || (arrayList = this.faces) == null) {
            float f = baseScale2;
            return defaultPosition;
        } else if (arrayList.size() == 0) {
            float f2 = rotation;
            float f3 = baseScale2;
            return defaultPosition;
        } else {
            int anchor = maskCoords.n;
            PhotoFace face = getRandomFaceWithVacantAnchor(anchor, document2.id, maskCoords);
            if (face == null) {
                return defaultPosition;
            }
            Point referencePoint = face.getPointForAnchor(anchor);
            float referenceWidth = face.getWidthForAnchor(anchor);
            float angle = face.getAngle();
            double d = (double) (referenceWidth / baseStickerSize().width);
            double d2 = maskCoords.zoom;
            Double.isNaN(d);
            float radAngle = (float) Math.toRadians((double) angle);
            double d3 = (double) radAngle;
            Double.isNaN(d3);
            double sin = Math.sin(1.5707963267948966d - d3);
            double d4 = (double) referenceWidth;
            Double.isNaN(d4);
            float xCompX = (float) (sin * d4 * maskCoords.x);
            double d5 = (double) radAngle;
            Double.isNaN(d5);
            double cos = Math.cos(1.5707963267948966d - d5);
            float f4 = rotation;
            float f5 = baseScale2;
            double d6 = (double) referenceWidth;
            Double.isNaN(d6);
            float xCompY = (float) (cos * d6 * maskCoords.x);
            double d7 = (double) radAngle;
            Double.isNaN(d7);
            double cos2 = Math.cos(d7 + 1.5707963267948966d);
            int anchor2 = anchor;
            PhotoFace photoFace = face;
            double d8 = (double) referenceWidth;
            Double.isNaN(d8);
            float yCompX = (float) (cos2 * d8 * maskCoords.y);
            double d9 = (double) radAngle;
            Double.isNaN(d9);
            double sin2 = Math.sin(d9 + 1.5707963267948966d);
            int i = anchor2;
            StickerPosition stickerPosition = defaultPosition;
            double d10 = (double) referenceWidth;
            Double.isNaN(d10);
            float f6 = xCompX;
            return new StickerPosition(new Point(referencePoint.x + xCompX + yCompX, referencePoint.y + xCompY + ((float) (sin2 * d10 * maskCoords.y))), (float) (d * d2), angle);
        }
    }

    private PhotoFace getRandomFaceWithVacantAnchor(int anchor, long documentId, TLRPC.TL_maskCoords maskCoords) {
        int i = anchor;
        if (i < 0 || i > 3 || this.faces.isEmpty()) {
            return null;
        }
        int count = this.faces.size();
        int i2 = Utilities.random.nextInt(count);
        for (int remaining = count; remaining > 0; remaining--) {
            PhotoFace face = this.faces.get(i2);
            if (!isFaceAnchorOccupied(face, anchor, documentId, maskCoords)) {
                return face;
            }
            i2 = (i2 + 1) % count;
        }
        return null;
    }

    private boolean isFaceAnchorOccupied(PhotoFace face, int anchor, long documentId, TLRPC.TL_maskCoords maskCoords) {
        Point anchorPoint = face.getPointForAnchor(anchor);
        if (anchorPoint == null) {
            return true;
        }
        float minDistance = face.getWidthForAnchor(0) * 1.1f;
        for (int index = 0; index < this.entitiesView.getChildCount(); index++) {
            View view = this.entitiesView.getChildAt(index);
            if (!(view instanceof StickerView)) {
                int i = anchor;
            } else {
                StickerView stickerView = (StickerView) view;
                if (stickerView.getAnchor() != anchor) {
                    continue;
                } else {
                    Point location = stickerView.getPosition();
                    float distance = (float) Math.hypot((double) (location.x - anchorPoint.x), (double) (location.y - anchorPoint.y));
                    if ((documentId == stickerView.getSticker().id || this.faces.size() > 1) && distance < minDistance) {
                        return true;
                    }
                }
            }
        }
        int i2 = anchor;
        return false;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private static class StickerPosition {
        /* access modifiers changed from: private */
        public float angle;
        /* access modifiers changed from: private */
        public Point position;
        /* access modifiers changed from: private */
        public float scale;

        StickerPosition(Point position2, float scale2, float angle2) {
            this.position = position2;
            this.scale = scale2;
            this.angle = angle2;
        }
    }
}
