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
import android.graphics.Point;
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
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputDocument;
import org.telegram.tgnet.TLRPC$TL_inputDocument;
import org.telegram.tgnet.TLRPC$TL_maskCoords;
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

@SuppressLint({"NewApi"})
public class PhotoPaintView extends FrameLayout implements EntityView.EntityViewDelegate {
    private FrameLayout backgroundView;
    private float baseScale;
    private Bitmap bitmapToEdit;
    private Swatch brushSwatch;
    private Brush[] brushes = {new Brush.Radial(), new Brush.Elliptical(), new Brush.Neon(), new Brush.Arrow()};
    private TextView cancelTextView;
    /* access modifiers changed from: private */
    public ColorPicker colorPicker;
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

    /* access modifiers changed from: protected */
    public void didSetAnimatedSticker(RLottieDrawable rLottieDrawable) {
    }

    /* access modifiers changed from: protected */
    public void onOpenCloseStickersAlert(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onTextAdd() {
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v40, resolved type: org.telegram.ui.Components.Paint.Views.TextPaintView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v43, resolved type: org.telegram.ui.Components.Paint.Views.StickerView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v44, resolved type: org.telegram.ui.Components.Paint.Views.TextPaintView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v45, resolved type: org.telegram.ui.Components.Paint.Views.TextPaintView} */
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public PhotoPaintView(android.content.Context r24, android.graphics.Bitmap r25, android.graphics.Bitmap r26, int r27, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r28, org.telegram.messenger.MediaController.CropState r29, java.lang.Runnable r30, org.telegram.ui.ActionBar.Theme.ResourcesProvider r31) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = r28
            r23.<init>(r24)
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
            r5 = r31
            r0.resourcesProvider = r5
            boolean r5 = r1 instanceof org.telegram.ui.BubbleActivity
            r0.inBubbleMode = r5
            r5 = r29
            r0.currentCropState = r5
            org.telegram.messenger.DispatchQueue r5 = new org.telegram.messenger.DispatchQueue
            java.lang.String r6 = "Paint"
            r5.<init>(r6)
            r0.queue = r5
            r5 = r27
            r0.originalBitmapRotation = r5
            r0.bitmapToEdit = r2
            r5 = r26
            r0.facesBitmap = r5
            org.telegram.ui.Components.Paint.UndoStore r5 = new org.telegram.ui.Components.Paint.UndoStore
            r5.<init>()
            r0.undoStore = r5
            org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda20 r6 = new org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda20
            r6.<init>(r0)
            r5.setDelegate(r6)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r1)
            r0.curtainView = r5
            r6 = 570425344(0x22000000, float:1.7347235E-18)
            r5.setBackgroundColor(r6)
            android.widget.FrameLayout r5 = r0.curtainView
            r5.setVisibility(r4)
            android.widget.FrameLayout r5 = r0.curtainView
            r6 = -1
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r10)
            r0.addView(r5, r10)
            org.telegram.ui.Components.Paint.RenderView r5 = new org.telegram.ui.Components.Paint.RenderView
            org.telegram.ui.Components.Paint.Painting r10 = new org.telegram.ui.Components.Paint.Painting
            org.telegram.ui.Components.Size r11 = r23.getPaintingSize()
            r10.<init>(r11)
            r5.<init>(r1, r10, r2)
            r0.renderView = r5
            org.telegram.ui.Components.PhotoPaintView$1 r2 = new org.telegram.ui.Components.PhotoPaintView$1
            r10 = r30
            r2.<init>(r10)
            r5.setDelegate(r2)
            org.telegram.ui.Components.Paint.RenderView r2 = r0.renderView
            org.telegram.ui.Components.Paint.UndoStore r5 = r0.undoStore
            r2.setUndoStore(r5)
            org.telegram.ui.Components.Paint.RenderView r2 = r0.renderView
            org.telegram.messenger.DispatchQueue r5 = r0.queue
            r2.setQueue(r5)
            org.telegram.ui.Components.Paint.RenderView r2 = r0.renderView
            r2.setVisibility(r4)
            org.telegram.ui.Components.Paint.RenderView r2 = r0.renderView
            org.telegram.ui.Components.Paint.Brush[] r5 = r0.brushes
            r5 = r5[r7]
            r2.setBrush(r5)
            org.telegram.ui.Components.Paint.RenderView r2 = r0.renderView
            r5 = 51
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r5)
            r0.addView(r2, r10)
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r2 = new org.telegram.ui.Components.Paint.Views.EntitiesContainerView
            org.telegram.ui.Components.PhotoPaintView$2 r10 = new org.telegram.ui.Components.PhotoPaintView$2
            r10.<init>()
            r2.<init>(r1, r10)
            r0.entitiesView = r2
            r0.addView(r2)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.dimView = r2
            r10 = 0
            r2.setAlpha(r10)
            android.widget.FrameLayout r2 = r0.dimView
            r11 = 1711276032(0x66000000, float:1.5111573E23)
            r2.setBackgroundColor(r11)
            android.widget.FrameLayout r2 = r0.dimView
            r12 = 8
            r2.setVisibility(r12)
            android.widget.FrameLayout r2 = r0.dimView
            r0.addView(r2)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.textDimView = r2
            r2.setAlpha(r10)
            android.widget.FrameLayout r2 = r0.textDimView
            r2.setBackgroundColor(r11)
            android.widget.FrameLayout r2 = r0.textDimView
            r2.setVisibility(r12)
            android.widget.FrameLayout r2 = r0.textDimView
            org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda3 r10 = new org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda3
            r10.<init>(r0)
            r2.setOnClickListener(r10)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.backgroundView = r2
            android.content.res.Resources r2 = r23.getResources()
            r10 = 2131165431(0x7var_f7, float:1.7945079E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r10)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r12 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r10.<init>(r12, r11)
            r2.setColorFilter(r10)
            android.widget.FrameLayout r10 = r0.backgroundView
            r10.setBackground(r2)
            android.widget.FrameLayout r2 = r0.backgroundView
            r10 = 72
            r11 = 87
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r10, (int) r11)
            r0.addView(r2, r10)
            org.telegram.ui.Components.PhotoPaintView$3 r2 = new org.telegram.ui.Components.PhotoPaintView$3
            r2.<init>(r0, r1)
            r0.selectionContainerView = r2
            r0.addView(r2)
            org.telegram.ui.Components.Paint.Views.ColorPicker r2 = new org.telegram.ui.Components.Paint.Views.ColorPicker
            r2.<init>(r1)
            r0.colorPicker = r2
            r0.addView(r2)
            org.telegram.ui.Components.Paint.Views.ColorPicker r2 = r0.colorPicker
            org.telegram.ui.Components.PhotoPaintView$4 r10 = new org.telegram.ui.Components.PhotoPaintView$4
            r10.<init>()
            r2.setDelegate(r10)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            r0.toolsView = r2
            r2.setBackgroundColor(r12)
            android.widget.FrameLayout r2 = r0.toolsView
            r10 = 48
            r11 = 83
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r10, (int) r11)
            r0.addView(r2, r10)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.cancelTextView = r2
            r10 = 1096810496(0x41600000, float:14.0)
            r2.setTextSize(r8, r10)
            android.widget.TextView r2 = r0.cancelTextView
            r2.setTextColor(r6)
            android.widget.TextView r2 = r0.cancelTextView
            r11 = 17
            r2.setGravity(r11)
            android.widget.TextView r2 = r0.cancelTextView
            r12 = -12763843(0xffffffffff3d3d3d, float:-2.5154206E38)
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r12, r7)
            r2.setBackgroundDrawable(r13)
            android.widget.TextView r2 = r0.cancelTextView
            r13 = 1101004800(0x41a00000, float:20.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r2.setPadding(r14, r7, r15, r7)
            android.widget.TextView r2 = r0.cancelTextView
            java.lang.String r14 = "Cancel"
            r15 = 2131624832(0x7f0e0380, float:1.8876855E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r14, r15)
            java.lang.String r14 = r14.toUpperCase()
            r2.setText(r14)
            android.widget.TextView r2 = r0.cancelTextView
            java.lang.String r14 = "fonts/rmedium.ttf"
            android.graphics.Typeface r15 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r2.setTypeface(r15)
            android.widget.FrameLayout r2 = r0.toolsView
            android.widget.TextView r15 = r0.cancelTextView
            r4 = -2
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r6, (int) r5)
            r2.addView(r15, r5)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r0.doneTextView = r2
            r2.setTextSize(r8, r10)
            android.widget.TextView r2 = r0.doneTextView
            java.lang.String r5 = "dialogFloatingButton"
            int r5 = r0.getThemedColor(r5)
            r2.setTextColor(r5)
            android.widget.TextView r2 = r0.doneTextView
            r2.setGravity(r11)
            android.widget.TextView r2 = r0.doneTextView
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r12, r7)
            r2.setBackgroundDrawable(r5)
            android.widget.TextView r2 = r0.doneTextView
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r2.setPadding(r5, r7, r10, r7)
            android.widget.TextView r2 = r0.doneTextView
            java.lang.String r5 = "Done"
            r10 = 2131625541(0x7f0e0645, float:1.8878293E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r10)
            java.lang.String r5 = r5.toUpperCase()
            r2.setText(r5)
            android.widget.TextView r2 = r0.doneTextView
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r2.setTypeface(r5)
            android.widget.FrameLayout r2 = r0.toolsView
            android.widget.TextView r5 = r0.doneTextView
            r10 = 53
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r6, (int) r10)
            r2.addView(r5, r4)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.paintButton = r2
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r4)
            android.widget.ImageView r2 = r0.paintButton
            java.lang.String r4 = "AccDescrPaint"
            r5 = 2131624033(0x7f0e0061, float:1.8875234E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r5)
            r2.setContentDescription(r4)
            android.widget.ImageView r2 = r0.paintButton
            r4 = 2131165852(0x7var_c, float:1.7945933E38)
            r2.setImageResource(r4)
            android.widget.ImageView r2 = r0.paintButton
            r4 = 1090519039(0x40ffffff, float:7.9999995)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4)
            r2.setBackgroundDrawable(r5)
            android.widget.FrameLayout r2 = r0.toolsView
            android.widget.ImageView r5 = r0.paintButton
            r16 = 54
            r17 = -1082130432(0xffffffffbvar_, float:-1.0)
            r18 = 17
            r19 = 0
            r20 = 0
            r21 = 1113587712(0x42600000, float:56.0)
            r22 = 0
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r2.addView(r5, r10)
            android.widget.ImageView r2 = r0.paintButton
            org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda4 r5 = new org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda4
            r5.<init>(r0)
            r2.setOnClickListener(r5)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r5)
            r5 = 2131165950(0x7var_fe, float:1.7946132E38)
            r2.setImageResource(r5)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4)
            r2.setBackgroundDrawable(r5)
            android.widget.FrameLayout r5 = r0.toolsView
            r10 = 54
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r6, (int) r11)
            r5.addView(r2, r6)
            org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda2 r5 = new org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda2
            r5.<init>(r0)
            r2.setOnClickListener(r5)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r1)
            java.lang.String r1 = "AccDescrPlaceText"
            r5 = 2131624041(0x7f0e0069, float:1.887525E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r5)
            r2.setContentDescription(r1)
            r1 = 2131165857(0x7var_a1, float:1.7945943E38)
            r2.setImageResource(r1)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4)
            r2.setBackgroundDrawable(r1)
            android.widget.FrameLayout r1 = r0.toolsView
            r19 = 1113587712(0x42600000, float:56.0)
            r21 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r1.addView(r2, r4)
            org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda6 r1 = new org.telegram.ui.Components.PhotoPaintView$$ExternalSyntheticLambda6
            r1.<init>(r0)
            r2.setOnClickListener(r1)
            org.telegram.ui.Components.Paint.Views.ColorPicker r1 = r0.colorPicker
            r1.setUndoEnabled(r7)
            org.telegram.ui.Components.Paint.Views.ColorPicker r1 = r0.colorPicker
            org.telegram.ui.Components.Paint.Swatch r1 = r1.getSwatch()
            r0.setCurrentSwatch(r1, r7)
            r23.updateSettingsButton()
            if (r3 == 0) goto L_0x03bf
            boolean r1 = r28.isEmpty()
            if (r1 != 0) goto L_0x03bf
            int r1 = r28.size()
            r2 = 0
        L_0x02ff:
            if (r2 >= r1) goto L_0x03bf
            java.lang.Object r4 = r3.get(r2)
            org.telegram.messenger.VideoEditedInfo$MediaEntity r4 = (org.telegram.messenger.VideoEditedInfo.MediaEntity) r4
            byte r5 = r4.type
            if (r5 != 0) goto L_0x0328
            java.lang.Object r5 = r4.parentObject
            org.telegram.tgnet.TLRPC$Document r6 = r4.document
            org.telegram.ui.Components.Paint.Views.StickerView r5 = r0.createSticker(r5, r6, r7)
            byte r6 = r4.subType
            r6 = r6 & r9
            if (r6 == 0) goto L_0x031b
            r5.mirror()
        L_0x031b:
            android.view.ViewGroup$LayoutParams r6 = r5.getLayoutParams()
            int r10 = r4.viewWidth
            r6.width = r10
            int r10 = r4.viewHeight
            r6.height = r10
            goto L_0x0350
        L_0x0328:
            if (r5 != r8) goto L_0x03bb
            org.telegram.ui.Components.Paint.Views.TextPaintView r5 = r0.createText(r7)
            byte r6 = r4.subType
            r10 = r6 & 1
            if (r10 == 0) goto L_0x0336
            r6 = 0
            goto L_0x033d
        L_0x0336:
            r6 = r6 & 4
            if (r6 == 0) goto L_0x033c
            r6 = 2
            goto L_0x033d
        L_0x033c:
            r6 = 1
        L_0x033d:
            r5.setType(r6)
            java.lang.String r6 = r4.text
            r5.setText(r6)
            org.telegram.ui.Components.Paint.Swatch r6 = r5.getSwatch()
            int r10 = r4.color
            r6.color = r10
            r5.setSwatch(r6)
        L_0x0350:
            float r6 = r4.x
            org.telegram.ui.Components.Size r10 = r0.paintingSize
            float r10 = r10.width
            float r6 = r6 * r10
            int r10 = r4.viewWidth
            float r10 = (float) r10
            float r11 = r4.scale
            r12 = 1065353216(0x3var_, float:1.0)
            float r11 = r12 - r11
            float r10 = r10 * r11
            r11 = 1073741824(0x40000000, float:2.0)
            float r10 = r10 / r11
            float r6 = r6 - r10
            r5.setX(r6)
            float r6 = r4.y
            org.telegram.ui.Components.Size r10 = r0.paintingSize
            float r10 = r10.height
            float r6 = r6 * r10
            int r10 = r4.viewHeight
            float r10 = (float) r10
            float r13 = r4.scale
            float r12 = r12 - r13
            float r10 = r10 * r12
            float r10 = r10 / r11
            float r6 = r6 - r10
            r5.setY(r6)
            org.telegram.ui.Components.Point r6 = new org.telegram.ui.Components.Point
            float r10 = r5.getX()
            int r11 = r4.viewWidth
            int r11 = r11 / r9
            float r11 = (float) r11
            float r10 = r10 + r11
            float r11 = r5.getY()
            int r12 = r4.viewHeight
            int r12 = r12 / r9
            float r12 = (float) r12
            float r11 = r11 + r12
            r6.<init>(r10, r11)
            r5.setPosition(r6)
            float r6 = r4.scale
            r5.setScaleX(r6)
            float r6 = r4.scale
            r5.setScaleY(r6)
            float r4 = r4.rotation
            float r4 = -r4
            double r10 = (double) r4
            r12 = 4614256656552045848(0x400921fb54442d18, double:3.NUM)
            java.lang.Double.isNaN(r10)
            double r10 = r10 / r12
            r12 = 4640537203540230144(0xNUM, double:180.0)
            double r10 = r10 * r12
            float r4 = (float) r10
            r5.setRotation(r4)
        L_0x03bb:
            int r2 = r2 + 1
            goto L_0x02ff
        L_0x03bf:
            org.telegram.ui.Components.Paint.Views.EntitiesContainerView r1 = r0.entitiesView
            r2 = 4
            r1.setVisibility(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoPaintView.<init>(android.content.Context, android.graphics.Bitmap, android.graphics.Bitmap, int, java.util.ArrayList, org.telegram.messenger.MediaController$CropState, java.lang.Runnable, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.colorPicker.setUndoEnabled(this.undoStore.canUndo());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        closeTextEnter(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        selectEntity((EntityView) null);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        openStickersView();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        createText(true);
    }

    public void onResume() {
        this.renderView.redraw();
    }

    public boolean onTouch(MotionEvent motionEvent) {
        if (this.currentEntityView != null) {
            if (this.editingText) {
                closeTextEnter(true);
            } else {
                selectEntity((EntityView) null);
            }
        }
        float x = ((motionEvent.getX() - this.renderView.getTranslationX()) - ((float) (getMeasuredWidth() / 2))) / this.renderView.getScaleX();
        float y = (((motionEvent.getY() - this.renderView.getTranslationY()) - ((float) (getMeasuredHeight() / 2))) + ((float) AndroidUtilities.dp(32.0f))) / this.renderView.getScaleY();
        double d = (double) x;
        double radians = (double) ((float) Math.toRadians((double) (-this.renderView.getRotation())));
        double cos = Math.cos(radians);
        Double.isNaN(d);
        double d2 = (double) y;
        double sin = Math.sin(radians);
        Double.isNaN(d2);
        float measuredWidth = ((float) ((cos * d) - (sin * d2))) + ((float) (this.renderView.getMeasuredWidth() / 2));
        double sin2 = Math.sin(radians);
        Double.isNaN(d);
        double cos2 = Math.cos(radians);
        Double.isNaN(d2);
        MotionEvent obtain = MotionEvent.obtain(0, 0, motionEvent.getActionMasked(), measuredWidth, ((float) ((d * sin2) + (d2 * cos2))) + ((float) (this.renderView.getMeasuredHeight() / 2)), 0);
        this.renderView.onTouch(obtain);
        obtain.recycle();
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
        float floor = (float) Math.floor((double) ((1280.0f * height) / width));
        size2.height = floor;
        if (floor > 1280.0f) {
            size2.height = 1280.0f;
            size2.width = (float) Math.floor((double) ((1280.0f * width) / height));
        }
        this.paintingSize = size2;
        return size2;
    }

    private void updateSettingsButton() {
        this.colorPicker.settingsButton.setContentDescription(LocaleController.getString("AccDescrBrushType", NUM));
        EntityView entityView = this.currentEntityView;
        int i = NUM;
        if (entityView != null) {
            if (entityView instanceof StickerView) {
                i = NUM;
                this.colorPicker.settingsButton.setContentDescription(LocaleController.getString("AccDescrMirror", NUM));
            } else if (entityView instanceof TextPaintView) {
                i = NUM;
                this.colorPicker.settingsButton.setContentDescription(LocaleController.getString("PaintOutlined", NUM));
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
        this.backgroundView.setVisibility(this.currentEntityView instanceof TextPaintView ? 4 : 0);
        this.colorPicker.setSettingsButtonImage(i);
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
        this.queue.postRunnable(PhotoPaintView$$ExternalSyntheticLambda18.INSTANCE);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$shutdown$5() {
        Looper myLooper = Looper.myLooper();
        if (myLooper != null) {
            myLooper.quit();
        }
    }

    public FrameLayout getToolsView() {
        return this.toolsView;
    }

    public FrameLayout getColorPickerBackground() {
        return this.backgroundView;
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

    public Bitmap getBitmap(ArrayList<VideoEditedInfo.MediaEntity> arrayList, Bitmap[] bitmapArr) {
        int i;
        int i2;
        boolean z;
        Canvas canvas;
        int i3;
        PhotoPaintView photoPaintView = this;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList2 = arrayList;
        Bitmap resultBitmap = photoPaintView.renderView.getResultBitmap();
        photoPaintView.lcm = BigInteger.ONE;
        if (resultBitmap != null && photoPaintView.entitiesView.entitiesCount() > 0) {
            int childCount = photoPaintView.entitiesView.getChildCount();
            byte b = 0;
            Canvas canvas2 = null;
            int i4 = 0;
            while (i4 < childCount) {
                View childAt = photoPaintView.entitiesView.getChildAt(i4);
                if (childAt instanceof EntityView) {
                    EntityView entityView = (EntityView) childAt;
                    Point position = entityView.getPosition();
                    if (arrayList2 != null) {
                        VideoEditedInfo.MediaEntity mediaEntity = new VideoEditedInfo.MediaEntity();
                        if (entityView instanceof TextPaintView) {
                            mediaEntity.type = 1;
                            TextPaintView textPaintView = (TextPaintView) entityView;
                            mediaEntity.text = textPaintView.getText();
                            int type = textPaintView.getType();
                            if (type == 0) {
                                mediaEntity.subType = (byte) (mediaEntity.subType | 1);
                            } else if (type == 2) {
                                mediaEntity.subType = (byte) (mediaEntity.subType | 4);
                            }
                            mediaEntity.color = textPaintView.getSwatch().color;
                            mediaEntity.fontSize = textPaintView.getTextSize();
                            z = false;
                        } else if (entityView instanceof StickerView) {
                            mediaEntity.type = b;
                            StickerView stickerView = (StickerView) entityView;
                            Size baseSize = stickerView.getBaseSize();
                            mediaEntity.width = baseSize.width;
                            mediaEntity.height = baseSize.height;
                            mediaEntity.document = stickerView.getSticker();
                            mediaEntity.parentObject = stickerView.getParentObject();
                            TLRPC$Document sticker = stickerView.getSticker();
                            mediaEntity.text = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(sticker, true).getAbsolutePath();
                            if (MessageObject.isAnimatedStickerDocument(sticker, true) || MessageObject.isVideoStickerDocument(sticker)) {
                                boolean isAnimatedStickerDocument = MessageObject.isAnimatedStickerDocument(sticker, true);
                                mediaEntity.subType = (byte) (mediaEntity.subType | (isAnimatedStickerDocument ? (byte) 1 : 4));
                                long duration = isAnimatedStickerDocument ? stickerView.getDuration() : 5000;
                                if (duration != 0) {
                                    BigInteger valueOf = BigInteger.valueOf(duration);
                                    photoPaintView.lcm = photoPaintView.lcm.multiply(valueOf).divide(photoPaintView.lcm.gcd(valueOf));
                                }
                                z = true;
                            } else {
                                z = false;
                            }
                            if (stickerView.isMirrored()) {
                                mediaEntity.subType = (byte) (mediaEntity.subType | 2);
                            }
                        }
                        arrayList2.add(mediaEntity);
                        float scaleX = childAt.getScaleX();
                        float scaleY = childAt.getScaleY();
                        float x = childAt.getX();
                        float y = childAt.getY();
                        mediaEntity.viewWidth = childAt.getWidth();
                        mediaEntity.viewHeight = childAt.getHeight();
                        mediaEntity.width = (((float) childAt.getWidth()) * scaleX) / ((float) photoPaintView.entitiesView.getMeasuredWidth());
                        mediaEntity.height = (((float) childAt.getHeight()) * scaleY) / ((float) photoPaintView.entitiesView.getMeasuredHeight());
                        mediaEntity.x = (((((float) childAt.getWidth()) * (1.0f - scaleX)) / 2.0f) + x) / ((float) photoPaintView.entitiesView.getMeasuredWidth());
                        mediaEntity.y = (y + ((((float) childAt.getHeight()) * (1.0f - scaleY)) / 2.0f)) / ((float) photoPaintView.entitiesView.getMeasuredHeight());
                        i2 = i4;
                        double d = (double) (-childAt.getRotation());
                        Double.isNaN(d);
                        mediaEntity.rotation = (float) (d * 0.017453292519943295d);
                        mediaEntity.textViewX = (x + ((float) (childAt.getWidth() / 2))) / ((float) photoPaintView.entitiesView.getMeasuredWidth());
                        mediaEntity.textViewY = (y + ((float) (childAt.getHeight() / 2))) / ((float) photoPaintView.entitiesView.getMeasuredHeight());
                        mediaEntity.textViewWidth = ((float) mediaEntity.viewWidth) / ((float) photoPaintView.entitiesView.getMeasuredWidth());
                        mediaEntity.textViewHeight = ((float) mediaEntity.viewHeight) / ((float) photoPaintView.entitiesView.getMeasuredHeight());
                        mediaEntity.scale = scaleX;
                        if (bitmapArr[0] == null) {
                            bitmapArr[0] = Bitmap.createBitmap(resultBitmap.getWidth(), resultBitmap.getHeight(), resultBitmap.getConfig());
                            canvas2 = new Canvas(bitmapArr[0]);
                            canvas2.drawBitmap(resultBitmap, 0.0f, 0.0f, (Paint) null);
                        }
                        canvas = canvas2;
                    } else {
                        i2 = i4;
                        canvas = canvas2;
                        z = false;
                    }
                    Canvas canvas3 = new Canvas(resultBitmap);
                    int i5 = 0;
                    while (i5 < 2) {
                        Canvas canvas4 = i5 == 0 ? canvas3 : canvas;
                        if (canvas4 == null || (i5 == 0 && z)) {
                            i3 = childCount;
                        } else {
                            canvas4.save();
                            canvas4.translate(position.x, position.y);
                            canvas4.scale(childAt.getScaleX(), childAt.getScaleY());
                            canvas4.rotate(childAt.getRotation());
                            canvas4.translate((float) ((-entityView.getWidth()) / 2), (float) ((-entityView.getHeight()) / 2));
                            if (childAt instanceof TextPaintView) {
                                Bitmap createBitmap = Bitmaps.createBitmap(childAt.getWidth(), childAt.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas5 = new Canvas(createBitmap);
                                childAt.draw(canvas5);
                                i3 = childCount;
                                canvas4.drawBitmap(createBitmap, (Rect) null, new Rect(0, 0, createBitmap.getWidth(), createBitmap.getHeight()), (Paint) null);
                                try {
                                    canvas5.setBitmap((Bitmap) null);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                                createBitmap.recycle();
                            } else {
                                i3 = childCount;
                                childAt.draw(canvas4);
                            }
                            canvas4.restore();
                        }
                        i5++;
                        ArrayList<VideoEditedInfo.MediaEntity> arrayList3 = arrayList;
                        childCount = i3;
                    }
                    i = childCount;
                    canvas2 = canvas;
                    i4 = i2 + 1;
                    photoPaintView = this;
                    arrayList2 = arrayList;
                    childCount = i;
                    b = 0;
                }
                i2 = i4;
                i = childCount;
                i4 = i2 + 1;
                photoPaintView = this;
                arrayList2 = arrayList;
                childCount = i;
                b = 0;
            }
        }
        return resultBitmap;
    }

    public long getLcm() {
        return this.lcm.longValue();
    }

    public void maybeShowDismissalAlert(PhotoViewer photoViewer, Activity activity, Runnable runnable) {
        if (this.editingText) {
            closeTextEnter(false);
        } else if (!hasChanges()) {
            runnable.run();
        } else if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
            builder.setMessage(LocaleController.getString("PhotoEditorDiscardAlert", NUM));
            builder.setTitle(LocaleController.getString("DiscardChanges", NUM));
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", NUM), new PhotoPaintView$$ExternalSyntheticLambda0(runnable));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            photoViewer.showAlertDialog(builder);
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentSwatch(Swatch swatch, boolean z) {
        this.renderView.setColor(swatch.color);
        this.renderView.setBrushSize(swatch.brushWeight);
        if (z) {
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
    public void setDimVisibility(final boolean z) {
        ObjectAnimator objectAnimator;
        if (z) {
            this.dimView.setVisibility(0);
            objectAnimator = ObjectAnimator.ofFloat(this.dimView, View.ALPHA, new float[]{0.0f, 1.0f});
        } else {
            objectAnimator = ObjectAnimator.ofFloat(this.dimView, View.ALPHA, new float[]{1.0f, 0.0f});
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
            objectAnimator = ObjectAnimator.ofFloat(this.textDimView, View.ALPHA, new float[]{0.0f, 1.0f});
        } else {
            objectAnimator = ObjectAnimator.ofFloat(this.textDimView, View.ALPHA, new float[]{1.0f, 0.0f});
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
        this.ignoreLayout = true;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        int currentActionBarHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f);
        Bitmap bitmap = this.bitmapToEdit;
        if (bitmap != null) {
            f = (float) bitmap.getWidth();
            f2 = (float) this.bitmapToEdit.getHeight();
        } else {
            f2 = (float) ((size2 - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f));
            f = (float) size;
        }
        float f3 = (float) size;
        float floor = (float) Math.floor((double) ((f3 * f2) / f));
        float f4 = (float) currentActionBarHeight;
        if (floor > f4) {
            f3 = (float) Math.floor((double) ((f * f4) / f2));
            floor = f4;
        }
        int i3 = (int) f3;
        int i4 = (int) floor;
        this.renderView.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(i4, NUM));
        float f5 = f3 / this.paintingSize.width;
        this.baseScale = f5;
        this.entitiesView.setScaleX(f5);
        this.entitiesView.setScaleY(this.baseScale);
        this.entitiesView.measure(View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.width, NUM), View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.height, NUM));
        this.dimView.measure(i, View.MeasureSpec.makeMeasureSpec(currentActionBarHeight, Integer.MIN_VALUE));
        EntityView entityView = this.currentEntityView;
        if (entityView != null) {
            entityView.updateSelectionView();
        }
        this.selectionContainerView.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(i4, NUM));
        this.colorPicker.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(currentActionBarHeight, NUM));
        this.toolsView.measure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        this.curtainView.measure(i, View.MeasureSpec.makeMeasureSpec(currentActionBarHeight, NUM));
        this.backgroundView.measure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0f), NUM));
        this.ignoreLayout = false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int i7 = (Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + i7;
        int i8 = AndroidUtilities.displaySize.y;
        AndroidUtilities.dp(48.0f);
        int ceil = (int) Math.ceil((double) ((i5 - this.renderView.getMeasuredWidth()) / 2));
        int dp = ((((i6 - currentActionBarHeight) - AndroidUtilities.dp(48.0f)) - this.renderView.getMeasuredHeight()) / 2) + AndroidUtilities.dp(8.0f) + i7;
        RenderView renderView2 = this.renderView;
        renderView2.layout(ceil, dp, renderView2.getMeasuredWidth() + ceil, this.renderView.getMeasuredHeight() + dp);
        int measuredWidth = ((this.renderView.getMeasuredWidth() - this.entitiesView.getMeasuredWidth()) / 2) + ceil;
        int measuredHeight = ((this.renderView.getMeasuredHeight() - this.entitiesView.getMeasuredHeight()) / 2) + dp;
        EntitiesContainerView entitiesContainerView = this.entitiesView;
        entitiesContainerView.layout(measuredWidth, measuredHeight, entitiesContainerView.getMeasuredWidth() + measuredWidth, this.entitiesView.getMeasuredHeight() + measuredHeight);
        FrameLayout frameLayout = this.dimView;
        frameLayout.layout(0, i7, frameLayout.getMeasuredWidth(), this.dimView.getMeasuredHeight() + i7);
        FrameLayout frameLayout2 = this.selectionContainerView;
        frameLayout2.layout(ceil, dp, frameLayout2.getMeasuredWidth() + ceil, this.selectionContainerView.getMeasuredHeight() + dp);
        ColorPicker colorPicker2 = this.colorPicker;
        colorPicker2.layout(0, currentActionBarHeight, colorPicker2.getMeasuredWidth(), this.colorPicker.getMeasuredHeight() + currentActionBarHeight);
        FrameLayout frameLayout3 = this.toolsView;
        frameLayout3.layout(0, i6 - frameLayout3.getMeasuredHeight(), this.toolsView.getMeasuredWidth(), i6);
        FrameLayout frameLayout4 = this.curtainView;
        frameLayout4.layout(0, dp, frameLayout4.getMeasuredWidth(), this.curtainView.getMeasuredHeight() + dp);
        this.backgroundView.layout(0, (i6 - AndroidUtilities.dp(45.0f)) - this.backgroundView.getMeasuredHeight(), this.backgroundView.getMeasuredWidth(), i6 - AndroidUtilities.dp(45.0f));
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

    public float[] getTransformedTouch(float f, float f2) {
        Point point = AndroidUtilities.displaySize;
        float f3 = f - ((float) (point.x / 2));
        float f4 = f2 - ((float) (point.y / 2));
        float[] fArr = this.temp;
        double d = (double) f3;
        double radians = (double) ((float) Math.toRadians((double) (-this.entitiesView.getRotation())));
        double cos = Math.cos(radians);
        Double.isNaN(d);
        double d2 = (double) f4;
        double sin = Math.sin(radians);
        Double.isNaN(d2);
        fArr[0] = ((float) ((cos * d) - (sin * d2))) + ((float) (AndroidUtilities.displaySize.x / 2));
        float[] fArr2 = this.temp;
        double sin2 = Math.sin(radians);
        Double.isNaN(d);
        double cos2 = Math.cos(radians);
        Double.isNaN(d2);
        fArr2[1] = ((float) ((d * sin2) + (d2 * cos2))) + ((float) (AndroidUtilities.displaySize.y / 2));
        return this.temp;
    }

    public int[] getCenterLocation(EntityView entityView) {
        return getCenterLocationInWindow(entityView);
    }

    public boolean allowInteraction(EntityView entityView) {
        return !this.editingText;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        int i = 0;
        if ((view == this.renderView || view == this.entitiesView || view == this.selectionContainerView) && this.currentCropState != null) {
            canvas.save();
            if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
                i = AndroidUtilities.statusBarHeight;
            }
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + i;
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();
            MediaController.CropState cropState = this.currentCropState;
            int i2 = cropState.transformRotation;
            if (i2 == 90 || i2 == 270) {
                int i3 = measuredHeight;
                measuredHeight = measuredWidth;
                measuredWidth = i3;
            }
            float scaleX = ((float) measuredWidth) * cropState.cropPw * view.getScaleX();
            MediaController.CropState cropState2 = this.currentCropState;
            int i4 = (int) (scaleX / cropState2.cropScale);
            int scaleY = (int) (((((float) measuredHeight) * cropState2.cropPh) * view.getScaleY()) / this.currentCropState.cropScale);
            float ceil = ((float) Math.ceil((double) ((getMeasuredWidth() - i4) / 2))) + this.transformX;
            float measuredHeight2 = ((float) (((((getMeasuredHeight() - currentActionBarHeight) - AndroidUtilities.dp(48.0f)) - scaleY) / 2) + AndroidUtilities.dp(8.0f) + i)) + this.transformY;
            canvas.clipRect(Math.max(0.0f, ceil), Math.max(0.0f, measuredHeight2), Math.min(ceil + ((float) i4), (float) getMeasuredWidth()), Math.min((float) getMeasuredHeight(), measuredHeight2 + ((float) scaleY)));
            i = 1;
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (i != 0) {
            canvas.restore();
        }
        return drawChild;
    }

    private Point centerPositionForEntity() {
        Size paintingSize2 = getPaintingSize();
        float f = paintingSize2.width / 2.0f;
        float f2 = paintingSize2.height / 2.0f;
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            float radians = (float) Math.toRadians((double) (-(((float) cropState.transformRotation) + cropState.cropRotate)));
            double d = (double) this.currentCropState.cropPx;
            double d2 = (double) radians;
            double cos = Math.cos(d2);
            Double.isNaN(d);
            double d3 = d * cos;
            double d4 = (double) this.currentCropState.cropPy;
            double sin = Math.sin(d2);
            Double.isNaN(d4);
            float f3 = (float) (d3 - (d4 * sin));
            double d5 = (double) this.currentCropState.cropPx;
            double sin2 = Math.sin(d2);
            Double.isNaN(d5);
            double d6 = d5 * sin2;
            double d7 = (double) this.currentCropState.cropPy;
            double cos2 = Math.cos(d2);
            Double.isNaN(d7);
            f -= f3 * paintingSize2.width;
            f2 -= ((float) (d6 + (d7 * cos2))) * paintingSize2.height;
        }
        return new Point(f, f2);
    }

    private Point startPositionRelativeToEntity(EntityView entityView) {
        MediaController.CropState cropState = this.currentCropState;
        float f = 200.0f;
        if (cropState != null) {
            f = 200.0f / cropState.cropScale;
        }
        if (entityView != null) {
            Point position = entityView.getPosition();
            return new Point(position.x + f, position.y + f);
        }
        float f2 = 100.0f;
        if (cropState != null) {
            f2 = 100.0f / cropState.cropScale;
        }
        Point centerPositionForEntity = centerPositionForEntity();
        while (true) {
            boolean z = false;
            for (int i = 0; i < this.entitiesView.getChildCount(); i++) {
                View childAt = this.entitiesView.getChildAt(i);
                if (childAt instanceof EntityView) {
                    Point position2 = ((EntityView) childAt).getPosition();
                    if (((float) Math.sqrt(Math.pow((double) (position2.x - centerPositionForEntity.x), 2.0d) + Math.pow((double) (position2.y - centerPositionForEntity.y), 2.0d))) < f2) {
                        z = true;
                    }
                }
            }
            if (!z) {
                return centerPositionForEntity;
            }
            centerPositionForEntity = new Point(centerPositionForEntity.x + f, centerPositionForEntity.y + f);
        }
    }

    public ArrayList<TLRPC$InputDocument> getMasks() {
        int childCount = this.entitiesView.getChildCount();
        ArrayList<TLRPC$InputDocument> arrayList = null;
        for (int i = 0; i < childCount; i++) {
            View childAt = this.entitiesView.getChildAt(i);
            if (childAt instanceof StickerView) {
                TLRPC$Document sticker = ((StickerView) childAt).getSticker();
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                }
                TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
                tLRPC$TL_inputDocument.id = sticker.id;
                tLRPC$TL_inputDocument.access_hash = sticker.access_hash;
                byte[] bArr = sticker.file_reference;
                tLRPC$TL_inputDocument.file_reference = bArr;
                if (bArr == null) {
                    tLRPC$TL_inputDocument.file_reference = new byte[0];
                }
                arrayList.add(tLRPC$TL_inputDocument);
            }
        }
        return arrayList;
    }

    public void setTransform(float f, float f2, float f3, float f4, float f5) {
        View view;
        float f6;
        float f7;
        float f8;
        float f9;
        float var_ = f2;
        float var_ = f3;
        this.transformX = var_;
        this.transformY = var_;
        int i = 0;
        while (i < 3) {
            if (i == 0) {
                view = this.entitiesView;
            } else if (i == 1) {
                view = this.selectionContainerView;
            } else {
                view = this.renderView;
            }
            MediaController.CropState cropState = this.currentCropState;
            float var_ = 1.0f;
            if (cropState != null) {
                float var_ = cropState.cropScale * 1.0f;
                int measuredWidth = view.getMeasuredWidth();
                int measuredHeight = view.getMeasuredHeight();
                if (measuredWidth != 0 && measuredHeight != 0) {
                    MediaController.CropState cropState2 = this.currentCropState;
                    int i2 = cropState2.transformRotation;
                    if (i2 == 90 || i2 == 270) {
                        int i3 = measuredHeight;
                        measuredHeight = measuredWidth;
                        measuredWidth = i3;
                    }
                    float var_ = (float) measuredWidth;
                    float var_ = (float) measuredHeight;
                    float max = Math.max(f4 / ((float) ((int) (cropState2.cropPw * var_))), f5 / ((float) ((int) (cropState2.cropPh * var_))));
                    f9 = var_ * max;
                    MediaController.CropState cropState3 = this.currentCropState;
                    float var_ = cropState3.cropScale;
                    f6 = (cropState3.cropPx * var_ * f * max * var_) + var_;
                    f8 = var_ + (cropState3.cropPy * var_ * f * max * var_);
                    f7 = cropState3.cropRotate + ((float) i2);
                } else {
                    return;
                }
            } else {
                f6 = var_;
                f9 = i == 0 ? this.baseScale * 1.0f : 1.0f;
                f7 = 0.0f;
                f8 = var_;
            }
            float var_ = f9 * f;
            if (!Float.isNaN(var_)) {
                var_ = var_;
            }
            view.setScaleX(var_);
            view.setScaleY(var_);
            view.setTranslationX(f6);
            view.setTranslationY(f8);
            view.setRotation(f7);
            view.invalidate();
            i++;
        }
        invalidate();
    }

    /* access modifiers changed from: private */
    public boolean selectEntity(EntityView entityView) {
        boolean z;
        EntityView entityView2 = this.currentEntityView;
        boolean z2 = true;
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
        EntityView entityView3 = this.currentEntityView;
        this.currentEntityView = entityView;
        if ((entityView3 instanceof TextPaintView) && TextUtils.isEmpty(((TextPaintView) entityView3).getText())) {
            lambda$registerRemovalUndo$9(entityView3);
        }
        EntityView entityView4 = this.currentEntityView;
        if (entityView4 != null) {
            entityView4.select(this.selectionContainerView);
            this.entitiesView.bringViewToFront(this.currentEntityView);
            EntityView entityView5 = this.currentEntityView;
            if (entityView5 instanceof TextPaintView) {
                setCurrentSwatch(((TextPaintView) entityView5).getSwatch(), true);
            }
        } else {
            z2 = z;
        }
        updateSettingsButton();
        return z2;
    }

    /* access modifiers changed from: private */
    /* renamed from: removeEntity */
    public void lambda$registerRemovalUndo$9(EntityView entityView) {
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
        StickerMasksAlert stickerMasksAlert = new StickerMasksAlert(getContext(), this.facesBitmap == null, this.resourcesProvider);
        stickerMasksAlert.setDelegate(new PhotoPaintView$$ExternalSyntheticLambda21(this));
        stickerMasksAlert.setOnDismissListener(new PhotoPaintView$$ExternalSyntheticLambda1(this));
        stickerMasksAlert.show();
        onOpenCloseStickersAlert(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openStickersView$7(Object obj, TLRPC$Document tLRPC$Document) {
        createSticker(obj, tLRPC$Document, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openStickersView$8(DialogInterface dialogInterface) {
        onOpenCloseStickersAlert(false);
    }

    private Size baseStickerSize() {
        double d = (double) getPaintingSize().width;
        Double.isNaN(d);
        float floor = (float) Math.floor(d * 0.5d);
        return new Size(floor, floor);
    }

    private void registerRemovalUndo(EntityView entityView) {
        this.undoStore.registerUndo(entityView.getUUID(), new PhotoPaintView$$ExternalSyntheticLambda17(this, entityView));
    }

    private StickerView createSticker(Object obj, TLRPC$Document tLRPC$Document, boolean z) {
        StickerPosition calculateStickerPosition = calculateStickerPosition(tLRPC$Document);
        AnonymousClass7 r1 = new StickerView(getContext(), calculateStickerPosition.position, calculateStickerPosition.angle, calculateStickerPosition.scale, baseStickerSize(), tLRPC$Document, obj) {
            /* access modifiers changed from: protected */
            public void didSetAnimatedSticker(RLottieDrawable rLottieDrawable) {
                PhotoPaintView.this.didSetAnimatedSticker(rLottieDrawable);
            }
        };
        r1.setDelegate(this);
        this.entitiesView.addView(r1);
        if (z) {
            registerRemovalUndo(r1);
            selectEntity(r1);
        }
        return r1;
    }

    /* access modifiers changed from: private */
    public void mirrorSticker() {
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof StickerView) {
            ((StickerView) entityView).mirror();
        }
    }

    private TextPaintView createText(boolean z) {
        Swatch swatch;
        onTextAdd();
        Swatch swatch2 = this.colorPicker.getSwatch();
        int i = this.selectedTextType;
        if (i == 0) {
            swatch = new Swatch(-16777216, 0.85f, swatch2.brushWeight);
        } else if (i == 1) {
            swatch = new Swatch(-1, 1.0f, swatch2.brushWeight);
        } else {
            swatch = new Swatch(-1, 1.0f, swatch2.brushWeight);
        }
        Size paintingSize2 = getPaintingSize();
        TextPaintView textPaintView = new TextPaintView(getContext(), startPositionRelativeToEntity((EntityView) null), (int) (paintingSize2.width / 9.0f), "", swatch, this.selectedTextType);
        textPaintView.setDelegate(this);
        textPaintView.setMaxWidth((int) (paintingSize2.width - 20.0f));
        this.entitiesView.addView(textPaintView, LayoutHelper.createFrame(-2, -2.0f));
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            textPaintView.scale(1.0f / cropState.cropScale);
            MediaController.CropState cropState2 = this.currentCropState;
            textPaintView.rotate(-(((float) cropState2.transformRotation) + cropState2.cropRotate));
        }
        if (z) {
            registerRemovalUndo(textPaintView);
            selectEntity(textPaintView);
            editSelectedTextEntity();
        }
        setCurrentSwatch(swatch, true);
        return textPaintView;
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
                textPaintView.setRotation(-(((float) cropState.transformRotation) + cropState.cropRotate));
                textPaintView.setScale(1.0f / this.currentCropState.cropScale);
            } else {
                textPaintView.setRotation(0.0f);
                textPaintView.setScale(1.0f);
            }
            this.toolsView.setVisibility(8);
            setTextDimVisibility(true, textPaintView);
            textPaintView.beginEditing();
            View focusedView = textPaintView.getFocusedView();
            focusedView.requestFocus();
            AndroidUtilities.showKeyboard(focusedView);
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

    private void setType(int i) {
        this.selectedTextType = i;
        if (this.currentEntityView instanceof TextPaintView) {
            Swatch swatch = this.colorPicker.getSwatch();
            if (i == 0 && swatch.color == -1) {
                setCurrentSwatch(new Swatch(-16777216, 0.85f, swatch.brushWeight), true);
            } else if ((i == 1 || i == 2) && swatch.color == -16777216) {
                setCurrentSwatch(new Swatch(-1, 1.0f, swatch.brushWeight), true);
            }
            ((TextPaintView) this.currentEntityView).setType(i);
        }
    }

    private int[] getCenterLocationInWindow(View view) {
        view.getLocationInWindow(this.pos);
        float rotation = view.getRotation();
        MediaController.CropState cropState = this.currentCropState;
        float f = cropState != null ? cropState.cropRotate + ((float) cropState.transformRotation) : 0.0f;
        double width = (double) (((float) view.getWidth()) * view.getScaleX() * this.entitiesView.getScaleX());
        double radians = (double) ((float) Math.toRadians((double) (rotation + f)));
        double cos = Math.cos(radians);
        Double.isNaN(width);
        double height = (double) (((float) view.getHeight()) * view.getScaleY() * this.entitiesView.getScaleY());
        double sin = Math.sin(radians);
        Double.isNaN(height);
        float f2 = (float) ((cos * width) - (sin * height));
        double sin2 = Math.sin(radians);
        Double.isNaN(width);
        double cos2 = Math.cos(radians);
        Double.isNaN(height);
        int[] iArr = this.pos;
        iArr[0] = (int) (((float) iArr[0]) + (f2 / 2.0f));
        iArr[1] = (int) (((float) iArr[1]) + (((float) ((width * sin2) + (height * cos2))) / 2.0f));
        return iArr;
    }

    public float getCropRotation() {
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            return cropState.cropRotate + ((float) cropState.transformRotation);
        }
        return 0.0f;
    }

    private void showMenuForEntity(EntityView entityView) {
        int[] centerLocationInWindow = getCenterLocationInWindow(entityView);
        showPopup(new PhotoPaintView$$ExternalSyntheticLambda16(this, entityView), this, 51, centerLocationInWindow[0], centerLocationInWindow[1] - AndroidUtilities.dp(32.0f));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showMenuForEntity$13(EntityView entityView) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(0);
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
        textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        textView.setGravity(16);
        textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(14.0f), 0);
        textView.setTextSize(1, 18.0f);
        textView.setTag(0);
        textView.setText(LocaleController.getString("PaintDelete", NUM));
        textView.setOnClickListener(new PhotoPaintView$$ExternalSyntheticLambda10(this, entityView));
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
        if (entityView instanceof TextPaintView) {
            TextView textView2 = new TextView(getContext());
            textView2.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
            textView2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            textView2.setGravity(16);
            textView2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            textView2.setTextSize(1, 18.0f);
            textView2.setTag(1);
            textView2.setText(LocaleController.getString("PaintEdit", NUM));
            textView2.setOnClickListener(new PhotoPaintView$$ExternalSyntheticLambda7(this));
            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, 48));
        }
        TextView textView3 = new TextView(getContext());
        textView3.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
        textView3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        textView3.setGravity(16);
        textView3.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
        textView3.setTextSize(1, 18.0f);
        textView3.setTag(2);
        textView3.setText(LocaleController.getString("PaintDuplicate", NUM));
        textView3.setOnClickListener(new PhotoPaintView$$ExternalSyntheticLambda5(this));
        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, 48));
        this.popupLayout.addView(linearLayout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        linearLayout.setLayoutParams(layoutParams);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showMenuForEntity$10(EntityView entityView, View view) {
        lambda$registerRemovalUndo$9(entityView);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showMenuForEntity$11(View view) {
        editSelectedTextEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showMenuForEntity$12(View view) {
        duplicateSelectedEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    private LinearLayout buttonForBrush(int i, int i2, String str, boolean z) {
        AnonymousClass8 r0 = new LinearLayout(this, getContext()) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return true;
            }
        };
        int i3 = 0;
        r0.setOrientation(0);
        r0.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        r0.setOnClickListener(new PhotoPaintView$$ExternalSyntheticLambda8(this, i));
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(i2);
        imageView.setColorFilter(getThemedColor("actionBarDefaultSubmenuItem"));
        r0.addView(imageView, LayoutHelper.createLinear(-2, -2, 19, 16, 0, 16, 0));
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
        textView.setTextSize(1, 16.0f);
        textView.setText(str);
        textView.setMinWidth(AndroidUtilities.dp(70.0f));
        r0.addView(textView, LayoutHelper.createLinear(-2, -2, 19, 0, 0, 16, 0));
        ImageView imageView2 = new ImageView(getContext());
        imageView2.setImageResource(NUM);
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor("radioBackgroundChecked"), PorterDuff.Mode.MULTIPLY));
        if (!z) {
            i3 = 4;
        }
        imageView2.setVisibility(i3);
        r0.addView(imageView2, LayoutHelper.createLinear(50, -1));
        return r0;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$buttonForBrush$14(int i, View view) {
        setBrush(i);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    public void showBrushSettings() {
        showPopup(new PhotoPaintView$$ExternalSyntheticLambda13(this), this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showBrushSettings$15() {
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

    private LinearLayout buttonForText(int i, String str, int i2, boolean z) {
        AnonymousClass9 r0 = new LinearLayout(this, getContext()) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return true;
            }
        };
        r0.setOrientation(0);
        r0.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        r0.setOnClickListener(new PhotoPaintView$$ExternalSyntheticLambda9(this, i));
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(i2);
        imageView.setColorFilter(getThemedColor("actionBarDefaultSubmenuItem"));
        r0.addView(imageView, LayoutHelper.createLinear(-2, -2, 19, 16, 0, 16, 0));
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
        textView.setTextSize(1, 16.0f);
        textView.setText(str);
        r0.addView(textView, LayoutHelper.createLinear(-2, -2, 19, 0, 0, 16, 0));
        if (z) {
            ImageView imageView2 = new ImageView(getContext());
            imageView2.setImageResource(NUM);
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor("radioBackgroundChecked"), PorterDuff.Mode.MULTIPLY));
            r0.addView(imageView2, LayoutHelper.createLinear(50, -1));
        }
        return r0;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$buttonForText$16(int i, View view) {
        setType(i);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* access modifiers changed from: private */
    public void showTextSettings() {
        showPopup(new PhotoPaintView$$ExternalSyntheticLambda14(this), this, 85, 0, AndroidUtilities.dp(48.0f));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showTextSettings$17() {
        int i;
        String str;
        for (int i2 = 0; i2 < 3; i2++) {
            boolean z = true;
            if (i2 == 0) {
                str = LocaleController.getString("PaintOutlined", NUM);
                i = NUM;
            } else if (i2 == 1) {
                str = LocaleController.getString("PaintRegular", NUM);
                i = NUM;
            } else {
                str = LocaleController.getString("PaintFramed", NUM);
                i = NUM;
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
            if (this.selectedTextType != i2) {
                z = false;
            }
            actionBarPopupWindowLayout.addView(buttonForText(i2, str, i, z), LayoutHelper.createLinear(-1, 48));
        }
    }

    private void showPopup(Runnable runnable, View view, int i, int i2, int i3) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
                this.popupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new PhotoPaintView$$ExternalSyntheticLambda11(this));
                this.popupLayout.setDispatchKeyEventListener(new PhotoPaintView$$ExternalSyntheticLambda19(this));
                this.popupLayout.setShownFromBottom(true);
            }
            this.popupLayout.removeInnerViews();
            runnable.run();
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
                this.popupWindow.setOnDismissListener(new PhotoPaintView$$ExternalSyntheticLambda12(this));
            }
            this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            if ((i & 48) != 0) {
                i2 -= this.popupLayout.getMeasuredWidth() / 2;
                i3 -= this.popupLayout.getMeasuredHeight();
            }
            this.popupWindow.showAtLocation(view, i, i2, i3);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$showPopup$18(View view, MotionEvent motionEvent) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPopup$19(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPopup$20() {
        this.popupLayout.removeInnerViews();
    }

    private int getFrameRotation() {
        int i = this.originalBitmapRotation;
        if (i == 90) {
            return 1;
        }
        if (i != 180) {
            return i != 270 ? 0 : 3;
        }
        return 2;
    }

    private boolean isSidewardOrientation() {
        int i = this.originalBitmapRotation;
        return i % 360 == 90 || i % 360 == 270;
    }

    private void detectFaces() {
        this.queue.postRunnable(new PhotoPaintView$$ExternalSyntheticLambda15(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$detectFaces$21() {
        FaceDetector faceDetector = null;
        try {
            faceDetector = new FaceDetector.Builder(getContext()).setMode(1).setLandmarkType(1).setTrackingEnabled(false).build();
            if (!faceDetector.isOperational()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("face detection is not operational");
                }
                faceDetector.release();
                return;
            }
            try {
                SparseArray<Face> detect = faceDetector.detect(new Frame.Builder().setBitmap(this.facesBitmap).setRotation(getFrameRotation()).build());
                ArrayList<PhotoFace> arrayList = new ArrayList<>();
                Size paintingSize2 = getPaintingSize();
                for (int i = 0; i < detect.size(); i++) {
                    PhotoFace photoFace = new PhotoFace(detect.get(detect.keyAt(i)), this.facesBitmap, paintingSize2, isSidewardOrientation());
                    if (photoFace.isSufficient()) {
                        arrayList.add(photoFace);
                    }
                }
                this.faces = arrayList;
                faceDetector.release();
            } catch (Throwable th) {
                FileLog.e(th);
                faceDetector.release();
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

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0049, code lost:
        r4 = r2.n;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.telegram.ui.Components.PhotoPaintView.StickerPosition calculateStickerPosition(org.telegram.tgnet.TLRPC$Document r17) {
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
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            if (r4 == 0) goto L_0x001c
            org.telegram.tgnet.TLRPC$TL_maskCoords r2 = r3.mask_coords
            goto L_0x0020
        L_0x001c:
            int r2 = r2 + 1
            goto L_0x0005
        L_0x001f:
            r2 = 0
        L_0x0020:
            org.telegram.messenger.MediaController$CropState r3 = r0.currentCropState
            r4 = 1061158912(0x3var_, float:0.75)
            if (r3 == 0) goto L_0x0031
            int r5 = r3.transformRotation
            float r5 = (float) r5
            float r6 = r3.cropRotate
            float r5 = r5 + r6
            float r5 = -r5
            float r3 = r3.cropScale
            float r4 = r4 / r3
            goto L_0x0032
        L_0x0031:
            r5 = 0
        L_0x0032:
            org.telegram.ui.Components.PhotoPaintView$StickerPosition r3 = new org.telegram.ui.Components.PhotoPaintView$StickerPosition
            org.telegram.ui.Components.Point r6 = r16.centerPositionForEntity()
            r3.<init>(r6, r4, r5)
            if (r2 == 0) goto L_0x00d7
            java.util.ArrayList<org.telegram.ui.Components.Paint.PhotoFace> r4 = r0.faces
            if (r4 == 0) goto L_0x00d7
            int r4 = r4.size()
            if (r4 != 0) goto L_0x0049
            goto L_0x00d7
        L_0x0049:
            int r4 = r2.n
            long r5 = r1.id
            org.telegram.ui.Components.Paint.PhotoFace r1 = r0.getRandomFaceWithVacantAnchor(r4, r5, r2)
            if (r1 != 0) goto L_0x0054
            return r3
        L_0x0054:
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
        L_0x00d7:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoPaintView.calculateStickerPosition(org.telegram.tgnet.TLRPC$Document):org.telegram.ui.Components.PhotoPaintView$StickerPosition");
    }

    private PhotoFace getRandomFaceWithVacantAnchor(int i, long j, TLRPC$TL_maskCoords tLRPC$TL_maskCoords) {
        if (i >= 0 && i <= 3 && !this.faces.isEmpty()) {
            int size = this.faces.size();
            int nextInt = Utilities.random.nextInt(size);
            for (int i2 = size; i2 > 0; i2--) {
                PhotoFace photoFace = this.faces.get(nextInt);
                if (!isFaceAnchorOccupied(photoFace, i, j, tLRPC$TL_maskCoords)) {
                    return photoFace;
                }
                nextInt = (nextInt + 1) % size;
            }
        }
        return null;
    }

    private boolean isFaceAnchorOccupied(PhotoFace photoFace, int i, long j, TLRPC$TL_maskCoords tLRPC$TL_maskCoords) {
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

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private static class StickerPosition {
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
