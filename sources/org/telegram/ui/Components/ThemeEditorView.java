package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextColorThemeCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.WallpaperUpdater;
import org.telegram.ui.LaunchActivity;

public class ThemeEditorView {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ThemeEditorView Instance;
    /* access modifiers changed from: private */
    public ArrayList<ThemeDescription> currentThemeDesription;
    /* access modifiers changed from: private */
    public int currentThemeDesriptionPosition;
    /* access modifiers changed from: private */
    public DecelerateInterpolator decelerateInterpolator;
    /* access modifiers changed from: private */
    public EditorAlert editorAlert;
    private final int editorHeight = AndroidUtilities.dp(54.0f);
    /* access modifiers changed from: private */
    public final int editorWidth = AndroidUtilities.dp(54.0f);
    /* access modifiers changed from: private */
    public Activity parentActivity;
    private SharedPreferences preferences;
    /* access modifiers changed from: private */
    public Theme.ThemeInfo themeInfo;
    /* access modifiers changed from: private */
    public WallpaperUpdater wallpaperUpdater;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager windowManager;
    /* access modifiers changed from: private */
    public FrameLayout windowView;

    public static ThemeEditorView getInstance() {
        return Instance;
    }

    public void destroy() {
        FrameLayout frameLayout;
        this.wallpaperUpdater.cleanup();
        if (this.parentActivity != null && (frameLayout = this.windowView) != null) {
            try {
                this.windowManager.removeViewImmediate(frameLayout);
                this.windowView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                EditorAlert editorAlert2 = this.editorAlert;
                if (editorAlert2 != null) {
                    editorAlert2.dismiss();
                    this.editorAlert = null;
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            this.parentActivity = null;
            Instance = null;
        }
    }

    public class EditorAlert extends BottomSheet {
        /* access modifiers changed from: private */
        public boolean animationInProgress;
        /* access modifiers changed from: private */
        public FrameLayout bottomLayout;
        /* access modifiers changed from: private */
        public FrameLayout bottomSaveLayout;
        /* access modifiers changed from: private */
        public AnimatorSet colorChangeAnimation;
        /* access modifiers changed from: private */
        public ColorPicker colorPicker;
        private FrameLayout frameLayout;
        /* access modifiers changed from: private */
        public boolean ignoreTextChange;
        /* access modifiers changed from: private */
        public LinearLayoutManager layoutManager;
        /* access modifiers changed from: private */
        public ListAdapter listAdapter;
        /* access modifiers changed from: private */
        public RecyclerListView listView;
        /* access modifiers changed from: private */
        public int previousScrollPosition;
        /* access modifiers changed from: private */
        public int scrollOffsetY;
        /* access modifiers changed from: private */
        public SearchAdapter searchAdapter;
        /* access modifiers changed from: private */
        public EmptyTextProgressView searchEmptyView;
        /* access modifiers changed from: private */
        public SearchField searchField;
        /* access modifiers changed from: private */
        public View[] shadow = new View[2];
        /* access modifiers changed from: private */
        public AnimatorSet[] shadowAnimation = new AnimatorSet[2];
        /* access modifiers changed from: private */
        public Drawable shadowDrawable;
        /* access modifiers changed from: private */
        public boolean startedColorChange;
        final /* synthetic */ ThemeEditorView this$0;
        /* access modifiers changed from: private */
        public int topBeforeSwitch;

        /* access modifiers changed from: protected */
        public boolean canDismissWithSwipe() {
            return false;
        }

        private class SearchField extends FrameLayout {
            /* access modifiers changed from: private */
            public ImageView clearSearchImageView;
            /* access modifiers changed from: private */
            public EditTextBoldCursor searchEditText;

            public SearchField(Context context) {
                super(context);
                View view = new View(context);
                view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), -854795));
                addView(view, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setImageResource(NUM);
                imageView.setColorFilter(new PorterDuffColorFilter(-6182737, PorterDuff.Mode.MULTIPLY));
                addView(imageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
                ImageView imageView2 = new ImageView(context);
                this.clearSearchImageView = imageView2;
                imageView2.setScaleType(ImageView.ScaleType.CENTER);
                ImageView imageView3 = this.clearSearchImageView;
                CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
                imageView3.setImageDrawable(closeProgressDrawable2);
                closeProgressDrawable2.setSide(AndroidUtilities.dp(7.0f));
                this.clearSearchImageView.setScaleX(0.1f);
                this.clearSearchImageView.setScaleY(0.1f);
                this.clearSearchImageView.setAlpha(0.0f);
                this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(-6182737, PorterDuff.Mode.MULTIPLY));
                addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
                this.clearSearchImageView.setOnClickListener(
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00b8: INVOKE  
                      (wrap: android.widget.ImageView : 0x00b1: IGET  (r0v11 android.widget.ImageView) = 
                      (r11v0 'this' org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField A[THIS])
                     org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchField.clearSearchImageView android.widget.ImageView)
                      (wrap: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$HcA_6SfCers13rez8m4VSvqCYfM : 0x00b5: CONSTRUCTOR  (r1v15 org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$HcA_6SfCers13rez8m4VSvqCYfM) = 
                      (r11v0 'this' org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField A[THIS])
                     call: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$HcA_6SfCers13rez8m4VSvqCYfM.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField):void type: CONSTRUCTOR)
                     android.widget.ImageView.setOnClickListener(android.view.View$OnClickListener):void type: VIRTUAL in method: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchField.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert, android.content.Context):void, dex: classes3.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00b5: CONSTRUCTOR  (r1v15 org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$HcA_6SfCers13rez8m4VSvqCYfM) = 
                      (r11v0 'this' org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField A[THIS])
                     call: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$HcA_6SfCers13rez8m4VSvqCYfM.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField):void type: CONSTRUCTOR in method: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchField.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert, android.content.Context):void, dex: classes3.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$HcA_6SfCers13rez8m4VSvqCYfM, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    */
                /*
                    this = this;
                    org.telegram.ui.Components.ThemeEditorView.EditorAlert.this = r12
                    r11.<init>(r13)
                    android.view.View r0 = new android.view.View
                    r0.<init>(r13)
                    r1 = 1099956224(0x41900000, float:18.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    r2 = -854795(0xfffffffffff2f4f5, float:NaN)
                    android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r1, r2)
                    r0.setBackgroundDrawable(r1)
                    r2 = -1
                    r3 = 1108344832(0x42100000, float:36.0)
                    r4 = 51
                    r5 = 1096810496(0x41600000, float:14.0)
                    r6 = 1093664768(0x41300000, float:11.0)
                    r7 = 1096810496(0x41600000, float:14.0)
                    r8 = 0
                    android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
                    r11.addView(r0, r1)
                    android.widget.ImageView r0 = new android.widget.ImageView
                    r0.<init>(r13)
                    android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
                    r0.setScaleType(r1)
                    r1 = 2131165991(0x7var_, float:1.7946215E38)
                    r0.setImageResource(r1)
                    android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
                    android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
                    r3 = -6182737(0xffffffffffa1a8af, float:NaN)
                    r1.<init>(r3, r2)
                    r0.setColorFilter(r1)
                    r4 = 36
                    r5 = 1108344832(0x42100000, float:36.0)
                    r6 = 51
                    r7 = 1098907648(0x41800000, float:16.0)
                    r8 = 1093664768(0x41300000, float:11.0)
                    r9 = 0
                    r10 = 0
                    android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10)
                    r11.addView(r0, r1)
                    android.widget.ImageView r0 = new android.widget.ImageView
                    r0.<init>(r13)
                    r11.clearSearchImageView = r0
                    android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
                    r0.setScaleType(r1)
                    android.widget.ImageView r0 = r11.clearSearchImageView
                    org.telegram.ui.Components.CloseProgressDrawable2 r1 = new org.telegram.ui.Components.CloseProgressDrawable2
                    r1.<init>()
                    r0.setImageDrawable(r1)
                    r0 = 1088421888(0x40e00000, float:7.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    r1.setSide(r0)
                    android.widget.ImageView r0 = r11.clearSearchImageView
                    r1 = 1036831949(0x3dcccccd, float:0.1)
                    r0.setScaleX(r1)
                    android.widget.ImageView r0 = r11.clearSearchImageView
                    r0.setScaleY(r1)
                    android.widget.ImageView r0 = r11.clearSearchImageView
                    r1 = 0
                    r0.setAlpha(r1)
                    android.widget.ImageView r0 = r11.clearSearchImageView
                    android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
                    android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
                    r1.<init>(r3, r2)
                    r0.setColorFilter(r1)
                    android.widget.ImageView r0 = r11.clearSearchImageView
                    r1 = 36
                    r2 = 1108344832(0x42100000, float:36.0)
                    r3 = 53
                    r4 = 1096810496(0x41600000, float:14.0)
                    r5 = 1093664768(0x41300000, float:11.0)
                    r6 = 1096810496(0x41600000, float:14.0)
                    r7 = 0
                    android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2, r3, r4, r5, r6, r7)
                    r11.addView(r0, r1)
                    android.widget.ImageView r0 = r11.clearSearchImageView
                    org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$HcA_6SfCers13rez8m4VSvqCYfM r1 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$HcA_6SfCers13rez8m4VSvqCYfM
                    r1.<init>(r11)
                    r0.setOnClickListener(r1)
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField$1 r0 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField$1
                    r0.<init>(r13, r12)
                    r11.searchEditText = r0
                    r13 = 1
                    r1 = 1098907648(0x41800000, float:16.0)
                    r0.setTextSize(r13, r1)
                    org.telegram.ui.Components.EditTextBoldCursor r0 = r11.searchEditText
                    r1 = -6774617(0xfffffffffvar_a0a7, float:NaN)
                    r0.setHintTextColor(r1)
                    org.telegram.ui.Components.EditTextBoldCursor r0 = r11.searchEditText
                    r1 = -14540254(0xfffffffffvar_, float:-2.1551216E38)
                    r0.setTextColor(r1)
                    org.telegram.ui.Components.EditTextBoldCursor r0 = r11.searchEditText
                    r1 = 0
                    r0.setBackgroundDrawable(r1)
                    org.telegram.ui.Components.EditTextBoldCursor r0 = r11.searchEditText
                    r1 = 0
                    r0.setPadding(r1, r1, r1, r1)
                    org.telegram.ui.Components.EditTextBoldCursor r0 = r11.searchEditText
                    r0.setMaxLines(r13)
                    org.telegram.ui.Components.EditTextBoldCursor r0 = r11.searchEditText
                    r0.setLines(r13)
                    org.telegram.ui.Components.EditTextBoldCursor r0 = r11.searchEditText
                    r0.setSingleLine(r13)
                    org.telegram.ui.Components.EditTextBoldCursor r13 = r11.searchEditText
                    r0 = 268435459(0x10000003, float:2.5243558E-29)
                    r13.setImeOptions(r0)
                    org.telegram.ui.Components.EditTextBoldCursor r13 = r11.searchEditText
                    java.lang.String r0 = "Search"
                    r1 = 2131626999(0x7f0e0bf7, float:1.888125E38)
                    java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)
                    r13.setHint(r0)
                    org.telegram.ui.Components.EditTextBoldCursor r13 = r11.searchEditText
                    r0 = -11491093(0xfffffffffvar_a8eb, float:-2.773565E38)
                    r13.setCursorColor(r0)
                    org.telegram.ui.Components.EditTextBoldCursor r13 = r11.searchEditText
                    r0 = 1101004800(0x41a00000, float:20.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    r13.setCursorSize(r0)
                    org.telegram.ui.Components.EditTextBoldCursor r13 = r11.searchEditText
                    r0 = 1069547520(0x3fCLASSNAME, float:1.5)
                    r13.setCursorWidth(r0)
                    org.telegram.ui.Components.EditTextBoldCursor r13 = r11.searchEditText
                    r0 = -1
                    r1 = 1109393408(0x42200000, float:40.0)
                    r2 = 51
                    r3 = 1113063424(0x42580000, float:54.0)
                    r4 = 1091567616(0x41100000, float:9.0)
                    r5 = 1110966272(0x42380000, float:46.0)
                    r6 = 0
                    android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r1, r2, r3, r4, r5, r6)
                    r11.addView(r13, r0)
                    org.telegram.ui.Components.EditTextBoldCursor r13 = r11.searchEditText
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField$2 r0 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField$2
                    r0.<init>(r12)
                    r13.addTextChangedListener(r0)
                    org.telegram.ui.Components.EditTextBoldCursor r12 = r11.searchEditText
                    org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$10O1jd-x8vAg85L8UTaAubOCh1s r13 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$10O1jd-x8vAg85L8UTaAubOCh1s
                    r13.<init>(r11)
                    r12.setOnEditorActionListener(r13)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchField.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert, android.content.Context):void");
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$new$0 */
            public /* synthetic */ void lambda$new$0$ThemeEditorView$EditorAlert$SearchField(View view) {
                this.searchEditText.setText("");
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$new$1 */
            public /* synthetic */ boolean lambda$new$1$ThemeEditorView$EditorAlert$SearchField(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent == null) {
                    return false;
                }
                if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                    return false;
                }
                AndroidUtilities.hideKeyboard(this.searchEditText);
                return false;
            }

            public void showKeyboard() {
                this.searchEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            public void requestDisallowInterceptTouchEvent(boolean z) {
                super.requestDisallowInterceptTouchEvent(z);
            }
        }

        private class ColorPicker extends FrameLayout {
            private float alpha = 1.0f;
            private LinearGradient alphaGradient;
            private boolean alphaPressed;
            private Drawable circleDrawable;
            private Paint circlePaint;
            private boolean circlePressed;
            /* access modifiers changed from: private */
            public EditTextBoldCursor[] colorEditText = new EditTextBoldCursor[4];
            private LinearGradient colorGradient;
            private float[] colorHSV = {0.0f, 0.0f, 1.0f};
            private boolean colorPressed;
            private Bitmap colorWheelBitmap;
            private Paint colorWheelPaint;
            private int colorWheelRadius;
            private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
            private float[] hsvTemp = new float[3];
            private LinearLayout linearLayout;
            private final int paramValueSliderWidth = AndroidUtilities.dp(20.0f);
            final /* synthetic */ EditorAlert this$1;
            private Paint valueSliderPaint;

            /* JADX WARNING: Illegal instructions before constructor call */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public ColorPicker(org.telegram.ui.Components.ThemeEditorView.EditorAlert r18, android.content.Context r19) {
                /*
                    r17 = this;
                    r0 = r17
                    r1 = r18
                    r2 = r19
                    r0.this$1 = r1
                    r0.<init>(r2)
                    r3 = 1101004800(0x41a00000, float:20.0)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    r0.paramValueSliderWidth = r4
                    r4 = 4
                    org.telegram.ui.Components.EditTextBoldCursor[] r5 = new org.telegram.ui.Components.EditTextBoldCursor[r4]
                    r0.colorEditText = r5
                    r5 = 3
                    float[] r6 = new float[r5]
                    r6 = {0, 0, NUM} // fill-array
                    r0.colorHSV = r6
                    r6 = 1065353216(0x3var_, float:1.0)
                    r0.alpha = r6
                    float[] r6 = new float[r5]
                    r0.hsvTemp = r6
                    android.view.animation.DecelerateInterpolator r6 = new android.view.animation.DecelerateInterpolator
                    r6.<init>()
                    r0.decelerateInterpolator = r6
                    r6 = 0
                    r0.setWillNotDraw(r6)
                    android.graphics.Paint r7 = new android.graphics.Paint
                    r8 = 1
                    r7.<init>(r8)
                    r0.circlePaint = r7
                    android.content.res.Resources r7 = r19.getResources()
                    r9 = 2131165574(0x7var_, float:1.7945369E38)
                    android.graphics.drawable.Drawable r7 = r7.getDrawable(r9)
                    android.graphics.drawable.Drawable r7 = r7.mutate()
                    r0.circleDrawable = r7
                    android.graphics.Paint r7 = new android.graphics.Paint
                    r7.<init>()
                    r0.colorWheelPaint = r7
                    r7.setAntiAlias(r8)
                    android.graphics.Paint r7 = r0.colorWheelPaint
                    r7.setDither(r8)
                    android.graphics.Paint r7 = new android.graphics.Paint
                    r7.<init>()
                    r0.valueSliderPaint = r7
                    r7.setAntiAlias(r8)
                    android.graphics.Paint r7 = r0.valueSliderPaint
                    r7.setDither(r8)
                    android.widget.LinearLayout r7 = new android.widget.LinearLayout
                    r7.<init>(r2)
                    r0.linearLayout = r7
                    r7.setOrientation(r6)
                    android.widget.LinearLayout r7 = r0.linearLayout
                    r9 = -2
                    r10 = 49
                    android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r9, r10)
                    r0.addView(r7, r9)
                    r7 = 0
                L_0x0081:
                    if (r7 >= r4) goto L_0x016a
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    org.telegram.ui.Components.EditTextBoldCursor r10 = new org.telegram.ui.Components.EditTextBoldCursor
                    r10.<init>(r2)
                    r9[r7] = r10
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r10 = 2
                    r9.setInputType(r10)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = -14606047(0xfffffffffvar_, float:-2.1417772E38)
                    r9.setTextColor(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r9.setCursorColor(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    r9.setCursorSize(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = 1069547520(0x3fCLASSNAME, float:1.5)
                    r9.setCursorWidth(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = 1099956224(0x41900000, float:18.0)
                    r9.setTextSize(r8, r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r2, r8)
                    r9.setBackgroundDrawable(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r9.setMaxLines(r8)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r7)
                    r9.setTag(r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = 17
                    r9.setGravity(r11)
                    if (r7 != 0) goto L_0x00f4
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "red"
                    r9.setHint(r10)
                    goto L_0x0117
                L_0x00f4:
                    if (r7 != r8) goto L_0x0100
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "green"
                    r9.setHint(r10)
                    goto L_0x0117
                L_0x0100:
                    if (r7 != r10) goto L_0x010c
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "blue"
                    r9.setHint(r10)
                    goto L_0x0117
                L_0x010c:
                    if (r7 != r5) goto L_0x0117
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "alpha"
                    r9.setHint(r10)
                L_0x0117:
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    if (r7 != r5) goto L_0x011f
                    r10 = 6
                    goto L_0x0120
                L_0x011f:
                    r10 = 5
                L_0x0120:
                    r11 = 268435456(0x10000000, float:2.5243549E-29)
                    r10 = r10 | r11
                    r9.setImeOptions(r10)
                    android.text.InputFilter[] r9 = new android.text.InputFilter[r8]
                    android.text.InputFilter$LengthFilter r10 = new android.text.InputFilter$LengthFilter
                    r10.<init>(r5)
                    r9[r6] = r10
                    org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.colorEditText
                    r10 = r10[r7]
                    r10.setFilters(r9)
                    android.widget.LinearLayout r9 = r0.linearLayout
                    org.telegram.ui.Components.EditTextBoldCursor[] r10 = r0.colorEditText
                    r10 = r10[r7]
                    r11 = 55
                    r12 = 36
                    r13 = 0
                    r14 = 0
                    if (r7 == r5) goto L_0x0147
                    r15 = 1098907648(0x41800000, float:16.0)
                    goto L_0x0148
                L_0x0147:
                    r15 = 0
                L_0x0148:
                    r16 = 0
                    android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r12, r13, r14, r15, r16)
                    r9.addView(r10, r11)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker$1 r10 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker$1
                    r10.<init>(r1, r7)
                    r9.addTextChangedListener(r10)
                    org.telegram.ui.Components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$ColorPicker$9GqZntGKrZv16q2vFNB7k5l9ag8 r10 = org.telegram.ui.Components.$$Lambda$ThemeEditorView$EditorAlert$ColorPicker$9GqZntGKrZv16q2vFNB7k5l9ag8.INSTANCE
                    r9.setOnEditorActionListener(r10)
                    int r7 = r7 + 1
                    goto L_0x0081
                L_0x016a:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert, android.content.Context):void");
            }

            static /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                AndroidUtilities.hideKeyboard(textView);
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int min = Math.min(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                measureChild(this.linearLayout, i, i2);
                setMeasuredDimension(min, min);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                float f;
                Canvas canvas2 = canvas;
                int width = (getWidth() / 2) - (this.paramValueSliderWidth * 2);
                int height = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                Bitmap bitmap = this.colorWheelBitmap;
                int i = this.colorWheelRadius;
                canvas2.drawBitmap(bitmap, (float) (width - i), (float) (height - i), (Paint) null);
                double radians = (double) ((float) Math.toRadians((double) this.colorHSV[0]));
                double d = (double) this.colorHSV[1];
                Double.isNaN(d);
                double d2 = (-Math.cos(radians)) * d;
                double d3 = (double) this.colorWheelRadius;
                Double.isNaN(d3);
                float[] fArr = this.colorHSV;
                double d4 = (double) fArr[1];
                Double.isNaN(d4);
                double d5 = (-Math.sin(radians)) * d4;
                double d6 = (double) this.colorWheelRadius;
                Double.isNaN(d6);
                float[] fArr2 = this.hsvTemp;
                fArr2[0] = fArr[0];
                fArr2[1] = fArr[1];
                fArr2[2] = 1.0f;
                drawPointerArrow(canvas2, ((int) (d2 * d3)) + width, ((int) (d5 * d6)) + height, Color.HSVToColor(fArr2));
                int i2 = this.colorWheelRadius;
                int i3 = width + i2 + this.paramValueSliderWidth;
                int i4 = height - i2;
                int dp = AndroidUtilities.dp(9.0f);
                int i5 = this.colorWheelRadius * 2;
                if (this.colorGradient == null) {
                    this.colorGradient = new LinearGradient((float) i3, (float) i4, (float) (i3 + dp), (float) (i4 + i5), new int[]{-16777216, Color.HSVToColor(this.hsvTemp)}, (float[]) null, Shader.TileMode.CLAMP);
                }
                this.valueSliderPaint.setShader(this.colorGradient);
                float f2 = (float) i4;
                float f3 = (float) (i4 + i5);
                canvas.drawRect((float) i3, f2, (float) (i3 + dp), f3, this.valueSliderPaint);
                int i6 = dp / 2;
                float[] fArr3 = this.colorHSV;
                float f4 = (float) i5;
                drawPointerArrow(canvas2, i3 + i6, (int) ((fArr3[2] * f4) + f2), Color.HSVToColor(fArr3));
                int i7 = i3 + (this.paramValueSliderWidth * 2);
                if (this.alphaGradient == null) {
                    int HSVToColor = Color.HSVToColor(this.hsvTemp);
                    f = f3;
                    this.alphaGradient = new LinearGradient((float) i7, f2, (float) (i7 + dp), f, new int[]{HSVToColor, HSVToColor & 16777215}, (float[]) null, Shader.TileMode.CLAMP);
                } else {
                    f = f3;
                }
                this.valueSliderPaint.setShader(this.alphaGradient);
                canvas.drawRect((float) i7, f2, (float) (dp + i7), f, this.valueSliderPaint);
                drawPointerArrow(canvas2, i7 + i6, (int) (f2 + ((1.0f - this.alpha) * f4)), (Color.HSVToColor(this.colorHSV) & 16777215) | (((int) (this.alpha * 255.0f)) << 24));
            }

            private void drawPointerArrow(Canvas canvas, int i, int i2, int i3) {
                int dp = AndroidUtilities.dp(13.0f);
                this.circleDrawable.setBounds(i - dp, i2 - dp, i + dp, dp + i2);
                this.circleDrawable.draw(canvas);
                this.circlePaint.setColor(-1);
                float f = (float) i;
                float f2 = (float) i2;
                canvas.drawCircle(f, f2, (float) AndroidUtilities.dp(11.0f), this.circlePaint);
                this.circlePaint.setColor(i3);
                canvas.drawCircle(f, f2, (float) AndroidUtilities.dp(9.0f), this.circlePaint);
            }

            /* access modifiers changed from: protected */
            public void onSizeChanged(int i, int i2, int i3, int i4) {
                int max = Math.max(1, ((i / 2) - (this.paramValueSliderWidth * 2)) - AndroidUtilities.dp(20.0f));
                this.colorWheelRadius = max;
                this.colorWheelBitmap = createColorWheelBitmap(max * 2, max * 2);
                this.colorGradient = null;
                this.alphaGradient = null;
            }

            private Bitmap createColorWheelBitmap(int i, int i2) {
                Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                int[] iArr = new int[13];
                float[] fArr = {0.0f, 1.0f, 1.0f};
                for (int i3 = 0; i3 < 13; i3++) {
                    fArr[0] = (float) (((i3 * 30) + 180) % 360);
                    iArr[i3] = Color.HSVToColor(fArr);
                }
                iArr[12] = iArr[0];
                float f = (float) (i / 2);
                float f2 = (float) (i2 / 2);
                this.colorWheelPaint.setShader(new ComposeShader(new SweepGradient(f, f2, iArr, (float[]) null), new RadialGradient(f, f2, (float) this.colorWheelRadius, -1, 16777215, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_OVER));
                new Canvas(createBitmap).drawCircle(f, f2, (float) this.colorWheelRadius, this.colorWheelPaint);
                return createBitmap;
            }

            private void startColorChange(boolean z) {
                if (this.this$1.startedColorChange != z) {
                    if (this.this$1.colorChangeAnimation != null) {
                        this.this$1.colorChangeAnimation.cancel();
                    }
                    boolean unused = this.this$1.startedColorChange = z;
                    AnimatorSet unused2 = this.this$1.colorChangeAnimation = new AnimatorSet();
                    AnimatorSet access$1300 = this.this$1.colorChangeAnimation;
                    Animator[] animatorArr = new Animator[2];
                    ColorDrawable access$1400 = this.this$1.backDrawable;
                    Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                    int[] iArr = new int[1];
                    iArr[0] = z ? 0 : 51;
                    animatorArr[0] = ObjectAnimator.ofInt(access$1400, property, iArr);
                    ViewGroup access$1500 = this.this$1.containerView;
                    Property property2 = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = z ? 0.2f : 1.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(access$1500, property2, fArr);
                    access$1300.playTogether(animatorArr);
                    this.this$1.colorChangeAnimation.setDuration(150);
                    this.this$1.colorChangeAnimation.setInterpolator(this.decelerateInterpolator);
                    this.this$1.colorChangeAnimation.start();
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:32:0x00bd, code lost:
                if (r5 <= (r8 + r7)) goto L_0x00bf;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:3:0x000d, code lost:
                if (r1 != 2) goto L_0x0019;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:53:0x0102, code lost:
                if (r5 <= (r8 + r7)) goto L_0x0104;
             */
            /* JADX WARNING: Removed duplicated region for block: B:42:0x00e4  */
            /* JADX WARNING: Removed duplicated region for block: B:56:0x0118  */
            /* JADX WARNING: Removed duplicated region for block: B:57:0x011b  */
            /* JADX WARNING: Removed duplicated region for block: B:70:0x0145  */
            /* JADX WARNING: Removed duplicated region for block: B:84:0x01a8  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onTouchEvent(android.view.MotionEvent r17) {
                /*
                    r16 = this;
                    r0 = r16
                    int r1 = r17.getAction()
                    r2 = 2
                    r3 = 0
                    r4 = 1
                    if (r1 == 0) goto L_0x001e
                    if (r1 == r4) goto L_0x0010
                    if (r1 == r2) goto L_0x001e
                    goto L_0x0019
                L_0x0010:
                    r0.alphaPressed = r3
                    r0.colorPressed = r3
                    r0.circlePressed = r3
                    r0.startColorChange(r3)
                L_0x0019:
                    boolean r1 = super.onTouchEvent(r17)
                    return r1
                L_0x001e:
                    float r1 = r17.getX()
                    int r1 = (int) r1
                    float r5 = r17.getY()
                    int r5 = (int) r5
                    int r6 = r16.getWidth()
                    int r6 = r6 / r2
                    int r7 = r0.paramValueSliderWidth
                    int r7 = r7 * 2
                    int r6 = r6 - r7
                    int r7 = r16.getHeight()
                    int r7 = r7 / r2
                    r8 = 1090519040(0x41000000, float:8.0)
                    int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r7 = r7 - r8
                    int r8 = r1 - r6
                    int r9 = r5 - r7
                    int r10 = r8 * r8
                    int r11 = r9 * r9
                    int r10 = r10 + r11
                    double r10 = (double) r10
                    double r10 = java.lang.Math.sqrt(r10)
                    boolean r12 = r0.circlePressed
                    if (r12 != 0) goto L_0x005f
                    boolean r12 = r0.alphaPressed
                    if (r12 != 0) goto L_0x009a
                    boolean r12 = r0.colorPressed
                    if (r12 != 0) goto L_0x009a
                    int r12 = r0.colorWheelRadius
                    double r13 = (double) r12
                    int r12 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
                    if (r12 > 0) goto L_0x009a
                L_0x005f:
                    int r12 = r0.colorWheelRadius
                    double r13 = (double) r12
                    int r15 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
                    if (r15 <= 0) goto L_0x0067
                    double r10 = (double) r12
                L_0x0067:
                    r0.circlePressed = r4
                    float[] r12 = r0.colorHSV
                    double r13 = (double) r9
                    double r8 = (double) r8
                    double r8 = java.lang.Math.atan2(r13, r8)
                    double r8 = java.lang.Math.toDegrees(r8)
                    r13 = 4640537203540230144(0xNUM, double:180.0)
                    double r8 = r8 + r13
                    float r8 = (float) r8
                    r12[r3] = r8
                    float[] r8 = r0.colorHSV
                    int r9 = r0.colorWheelRadius
                    double r12 = (double) r9
                    java.lang.Double.isNaN(r12)
                    double r10 = r10 / r12
                    float r9 = (float) r10
                    r10 = 1065353216(0x3var_, float:1.0)
                    float r9 = java.lang.Math.min(r10, r9)
                    r10 = 0
                    float r9 = java.lang.Math.max(r10, r9)
                    r8[r4] = r9
                    r8 = 0
                    r0.colorGradient = r8
                    r0.alphaGradient = r8
                L_0x009a:
                    boolean r8 = r0.colorPressed
                    r9 = 1073741824(0x40000000, float:2.0)
                    if (r8 != 0) goto L_0x00bf
                    boolean r8 = r0.circlePressed
                    if (r8 != 0) goto L_0x00df
                    boolean r8 = r0.alphaPressed
                    if (r8 != 0) goto L_0x00df
                    int r8 = r0.colorWheelRadius
                    int r10 = r6 + r8
                    int r11 = r0.paramValueSliderWidth
                    int r10 = r10 + r11
                    if (r1 < r10) goto L_0x00df
                    int r10 = r6 + r8
                    int r11 = r11 * 2
                    int r10 = r10 + r11
                    if (r1 > r10) goto L_0x00df
                    int r10 = r7 - r8
                    if (r5 < r10) goto L_0x00df
                    int r8 = r8 + r7
                    if (r5 > r8) goto L_0x00df
                L_0x00bf:
                    int r8 = r0.colorWheelRadius
                    int r10 = r7 - r8
                    int r10 = r5 - r10
                    float r10 = (float) r10
                    float r8 = (float) r8
                    float r8 = r8 * r9
                    float r10 = r10 / r8
                    r8 = 0
                    int r11 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
                    if (r11 >= 0) goto L_0x00d1
                    r10 = 0
                    goto L_0x00d9
                L_0x00d1:
                    r8 = 1065353216(0x3var_, float:1.0)
                    int r11 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
                    if (r11 <= 0) goto L_0x00d9
                    r10 = 1065353216(0x3var_, float:1.0)
                L_0x00d9:
                    float[] r8 = r0.colorHSV
                    r8[r2] = r10
                    r0.colorPressed = r4
                L_0x00df:
                    boolean r8 = r0.alphaPressed
                    r10 = 4
                    if (r8 != 0) goto L_0x0104
                    boolean r8 = r0.circlePressed
                    if (r8 != 0) goto L_0x0123
                    boolean r8 = r0.colorPressed
                    if (r8 != 0) goto L_0x0123
                    int r8 = r0.colorWheelRadius
                    int r11 = r6 + r8
                    int r12 = r0.paramValueSliderWidth
                    int r13 = r12 * 3
                    int r11 = r11 + r13
                    if (r1 < r11) goto L_0x0123
                    int r6 = r6 + r8
                    int r12 = r12 * 4
                    int r6 = r6 + r12
                    if (r1 > r6) goto L_0x0123
                    int r1 = r7 - r8
                    if (r5 < r1) goto L_0x0123
                    int r8 = r8 + r7
                    if (r5 > r8) goto L_0x0123
                L_0x0104:
                    int r1 = r0.colorWheelRadius
                    int r7 = r7 - r1
                    int r5 = r5 - r7
                    float r5 = (float) r5
                    float r1 = (float) r1
                    float r1 = r1 * r9
                    float r5 = r5 / r1
                    r1 = 1065353216(0x3var_, float:1.0)
                    float r14 = r1 - r5
                    r0.alpha = r14
                    r5 = 0
                    int r6 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
                    if (r6 >= 0) goto L_0x011b
                    r0.alpha = r5
                    goto L_0x0121
                L_0x011b:
                    int r5 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
                    if (r5 <= 0) goto L_0x0121
                    r0.alpha = r1
                L_0x0121:
                    r0.alphaPressed = r4
                L_0x0123:
                    boolean r1 = r0.alphaPressed
                    if (r1 != 0) goto L_0x012f
                    boolean r1 = r0.colorPressed
                    if (r1 != 0) goto L_0x012f
                    boolean r1 = r0.circlePressed
                    if (r1 == 0) goto L_0x0223
                L_0x012f:
                    r0.startColorChange(r4)
                    int r1 = r16.getColor()
                    r5 = 0
                L_0x0137:
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r6 = r0.this$1
                    org.telegram.ui.Components.ThemeEditorView r6 = r6.this$0
                    java.util.ArrayList r6 = r6.currentThemeDesription
                    int r6 = r6.size()
                    if (r5 >= r6) goto L_0x0190
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r6 = r0.this$1
                    org.telegram.ui.Components.ThemeEditorView r6 = r6.this$0
                    java.util.ArrayList r6 = r6.currentThemeDesription
                    java.lang.Object r6 = r6.get(r5)
                    org.telegram.ui.ActionBar.ThemeDescription r6 = (org.telegram.ui.ActionBar.ThemeDescription) r6
                    java.lang.String r6 = r6.getCurrentKey()
                    if (r5 != 0) goto L_0x0161
                    java.lang.String r7 = "chat_wallpaper"
                    boolean r7 = r6.equals(r7)
                    if (r7 != 0) goto L_0x0179
                L_0x0161:
                    java.lang.String r7 = "chat_wallpaper_gradient_to"
                    boolean r7 = r6.equals(r7)
                    if (r7 != 0) goto L_0x0179
                    java.lang.String r7 = "windowBackgroundWhite"
                    boolean r7 = r6.equals(r7)
                    if (r7 != 0) goto L_0x0179
                    java.lang.String r7 = "windowBackgroundGray"
                    boolean r6 = r6.equals(r7)
                    if (r6 == 0) goto L_0x017c
                L_0x0179:
                    r6 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                    r1 = r1 | r6
                L_0x017c:
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r6 = r0.this$1
                    org.telegram.ui.Components.ThemeEditorView r6 = r6.this$0
                    java.util.ArrayList r6 = r6.currentThemeDesription
                    java.lang.Object r6 = r6.get(r5)
                    org.telegram.ui.ActionBar.ThemeDescription r6 = (org.telegram.ui.ActionBar.ThemeDescription) r6
                    r6.setColor(r1, r3)
                    int r5 = r5 + 1
                    goto L_0x0137
                L_0x0190:
                    int r5 = android.graphics.Color.red(r1)
                    int r6 = android.graphics.Color.green(r1)
                    int r7 = android.graphics.Color.blue(r1)
                    int r1 = android.graphics.Color.alpha(r1)
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r8 = r0.this$1
                    boolean r8 = r8.ignoreTextChange
                    if (r8 != 0) goto L_0x0220
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r8 = r0.this$1
                    boolean unused = r8.ignoreTextChange = r4
                    org.telegram.ui.Components.EditTextBoldCursor[] r8 = r0.colorEditText
                    r8 = r8[r3]
                    java.lang.StringBuilder r9 = new java.lang.StringBuilder
                    r9.<init>()
                    java.lang.String r11 = ""
                    r9.append(r11)
                    r9.append(r5)
                    java.lang.String r5 = r9.toString()
                    r8.setText(r5)
                    org.telegram.ui.Components.EditTextBoldCursor[] r5 = r0.colorEditText
                    r5 = r5[r4]
                    java.lang.StringBuilder r8 = new java.lang.StringBuilder
                    r8.<init>()
                    r8.append(r11)
                    r8.append(r6)
                    java.lang.String r6 = r8.toString()
                    r5.setText(r6)
                    org.telegram.ui.Components.EditTextBoldCursor[] r5 = r0.colorEditText
                    r2 = r5[r2]
                    java.lang.StringBuilder r5 = new java.lang.StringBuilder
                    r5.<init>()
                    r5.append(r11)
                    r5.append(r7)
                    java.lang.String r5 = r5.toString()
                    r2.setText(r5)
                    org.telegram.ui.Components.EditTextBoldCursor[] r2 = r0.colorEditText
                    r5 = 3
                    r2 = r2[r5]
                    java.lang.StringBuilder r5 = new java.lang.StringBuilder
                    r5.<init>()
                    r5.append(r11)
                    r5.append(r1)
                    java.lang.String r1 = r5.toString()
                    r2.setText(r1)
                    r1 = 0
                L_0x0209:
                    if (r1 >= r10) goto L_0x021b
                    org.telegram.ui.Components.EditTextBoldCursor[] r2 = r0.colorEditText
                    r5 = r2[r1]
                    r2 = r2[r1]
                    int r2 = r2.length()
                    r5.setSelection(r2)
                    int r1 = r1 + 1
                    goto L_0x0209
                L_0x021b:
                    org.telegram.ui.Components.ThemeEditorView$EditorAlert r1 = r0.this$1
                    boolean unused = r1.ignoreTextChange = r3
                L_0x0220:
                    r16.invalidate()
                L_0x0223:
                    return r4
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
            }

            public void setColor(int i) {
                int red = Color.red(i);
                int green = Color.green(i);
                int blue = Color.blue(i);
                int alpha2 = Color.alpha(i);
                if (!this.this$1.ignoreTextChange) {
                    boolean unused = this.this$1.ignoreTextChange = true;
                    EditTextBoldCursor editTextBoldCursor = this.colorEditText[0];
                    editTextBoldCursor.setText("" + red);
                    EditTextBoldCursor editTextBoldCursor2 = this.colorEditText[1];
                    editTextBoldCursor2.setText("" + green);
                    EditTextBoldCursor editTextBoldCursor3 = this.colorEditText[2];
                    editTextBoldCursor3.setText("" + blue);
                    EditTextBoldCursor editTextBoldCursor4 = this.colorEditText[3];
                    editTextBoldCursor4.setText("" + alpha2);
                    for (int i2 = 0; i2 < 4; i2++) {
                        EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
                        editTextBoldCursorArr[i2].setSelection(editTextBoldCursorArr[i2].length());
                    }
                    boolean unused2 = this.this$1.ignoreTextChange = false;
                }
                this.alphaGradient = null;
                this.colorGradient = null;
                this.alpha = ((float) alpha2) / 255.0f;
                Color.colorToHSV(i, this.colorHSV);
                invalidate();
            }

            public int getColor() {
                return (Color.HSVToColor(this.colorHSV) & 16777215) | (((int) (this.alpha * 255.0f)) << 24);
            }
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public EditorAlert(org.telegram.ui.Components.ThemeEditorView r17, android.content.Context r18, java.util.ArrayList<org.telegram.ui.ActionBar.ThemeDescription> r19) {
            /*
                r16 = this;
                r0 = r16
                r1 = r17
                r2 = r18
                r0.this$0 = r1
                r3 = 1
                r0.<init>(r2, r3)
                r4 = 2
                android.view.View[] r5 = new android.view.View[r4]
                r0.shadow = r5
                android.animation.AnimatorSet[] r4 = new android.animation.AnimatorSet[r4]
                r0.shadowAnimation = r4
                android.content.res.Resources r4 = r18.getResources()
                r5 = 2131165979(0x7var_b, float:1.794619E38)
                android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
                android.graphics.drawable.Drawable r4 = r4.mutate()
                r0.shadowDrawable = r4
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$1 r4 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$1
                r4.<init>(r2, r1)
                r0.containerView = r4
                r5 = 0
                r4.setWillNotDraw(r5)
                android.view.ViewGroup r4 = r0.containerView
                int r6 = r0.backgroundPaddingLeft
                r4.setPadding(r6, r5, r6, r5)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                r0.frameLayout = r4
                r6 = -1
                r4.setBackgroundColor(r6)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField r4 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField
                r4.<init>(r2)
                r0.searchField = r4
                android.widget.FrameLayout r7 = r0.frameLayout
                r8 = 51
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r6, r8)
                r7.addView(r4, r9)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$2 r4 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$2
                r4.<init>(r2, r1)
                r0.listView = r4
                r7 = 251658240(0xvar_, float:6.3108872E-30)
                r4.setSelectorDrawableColor(r7)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r7 = 1111490560(0x42400000, float:48.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r4.setPadding(r5, r5, r5, r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r4.setClipToPadding(r5)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                androidx.recyclerview.widget.LinearLayoutManager r9 = new androidx.recyclerview.widget.LinearLayoutManager
                android.content.Context r10 = r16.getContext()
                r9.<init>(r10)
                r0.layoutManager = r9
                r4.setLayoutManager(r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r4.setHorizontalScrollBarEnabled(r5)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r4.setVerticalScrollBarEnabled(r5)
                android.view.ViewGroup r4 = r0.containerView
                org.telegram.ui.Components.RecyclerListView r9 = r0.listView
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r6, r8)
                r4.addView(r9, r10)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$ListAdapter r9 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$ListAdapter
                r10 = r19
                r9.<init>(r0, r2, r10)
                r0.listAdapter = r9
                r4.setAdapter(r9)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter r4 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter
                r4.<init>(r2)
                r0.searchAdapter = r4
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r9 = -657673(0xfffffffffff5f6f7, float:NaN)
                r4.setGlowColor(r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r9 = 0
                r4.setItemAnimator(r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                r4.setLayoutAnimation(r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$O5hdgeqbZb0M_VcxiNeMNa7o3Vw r9 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$O5hdgeqbZb0M_VcxiNeMNa7o3Vw
                r9.<init>()
                r4.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r9)
                org.telegram.ui.Components.RecyclerListView r4 = r0.listView
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$3 r9 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$3
                r9.<init>(r1)
                r4.setOnScrollListener(r9)
                org.telegram.ui.Components.EmptyTextProgressView r1 = new org.telegram.ui.Components.EmptyTextProgressView
                r1.<init>(r2)
                r0.searchEmptyView = r1
                r1.setShowAtCenter(r3)
                org.telegram.ui.Components.EmptyTextProgressView r1 = r0.searchEmptyView
                r1.showTextView()
                org.telegram.ui.Components.EmptyTextProgressView r1 = r0.searchEmptyView
                java.lang.String r4 = "NoResult"
                r9 = 2131626097(0x7f0e0871, float:1.887942E38)
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)
                r1.setText(r4)
                org.telegram.ui.Components.RecyclerListView r1 = r0.listView
                org.telegram.ui.Components.EmptyTextProgressView r4 = r0.searchEmptyView
                r1.setEmptyView(r4)
                android.view.ViewGroup r1 = r0.containerView
                org.telegram.ui.Components.EmptyTextProgressView r4 = r0.searchEmptyView
                r9 = -1
                r10 = -1082130432(0xffffffffbvar_, float:-1.0)
                r11 = 51
                r12 = 0
                r13 = 1112539136(0x42500000, float:52.0)
                r14 = 0
                r15 = 0
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r1.addView(r4, r9)
                android.widget.FrameLayout$LayoutParams r1 = new android.widget.FrameLayout$LayoutParams
                int r4 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
                r1.<init>(r6, r4, r8)
                r4 = 1114112000(0x42680000, float:58.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                r1.topMargin = r4
                android.view.View[] r4 = r0.shadow
                android.view.View r9 = new android.view.View
                r9.<init>(r2)
                r4[r5] = r9
                android.view.View[] r4 = r0.shadow
                r4 = r4[r5]
                r9 = 301989888(0x12000000, float:4.0389678E-28)
                r4.setBackgroundColor(r9)
                android.view.View[] r4 = r0.shadow
                r4 = r4[r5]
                r10 = 0
                r4.setAlpha(r10)
                android.view.View[] r4 = r0.shadow
                r4 = r4[r5]
                java.lang.Integer r10 = java.lang.Integer.valueOf(r3)
                r4.setTag(r10)
                android.view.ViewGroup r4 = r0.containerView
                android.view.View[] r10 = r0.shadow
                r10 = r10[r5]
                r4.addView(r10, r1)
                android.view.ViewGroup r1 = r0.containerView
                android.widget.FrameLayout r4 = r0.frameLayout
                r10 = 58
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r10, r8)
                r1.addView(r4, r10)
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker r1 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker
                r1.<init>(r0, r2)
                r0.colorPicker = r1
                r4 = 8
                r1.setVisibility(r4)
                android.view.ViewGroup r1 = r0.containerView
                org.telegram.ui.Components.ThemeEditorView$EditorAlert$ColorPicker r10 = r0.colorPicker
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r6, r3)
                r1.addView(r10, r11)
                android.widget.FrameLayout$LayoutParams r1 = new android.widget.FrameLayout$LayoutParams
                int r10 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
                r11 = 83
                r1.<init>(r6, r10, r11)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r1.bottomMargin = r7
                android.view.View[] r7 = r0.shadow
                android.view.View r10 = new android.view.View
                r10.<init>(r2)
                r7[r3] = r10
                android.view.View[] r7 = r0.shadow
                r7 = r7[r3]
                r7.setBackgroundColor(r9)
                android.view.ViewGroup r7 = r0.containerView
                android.view.View[] r9 = r0.shadow
                r9 = r9[r3]
                r7.addView(r9, r1)
                android.widget.FrameLayout r1 = new android.widget.FrameLayout
                r1.<init>(r2)
                r0.bottomSaveLayout = r1
                r1.setBackgroundColor(r6)
                android.view.ViewGroup r1 = r0.containerView
                android.widget.FrameLayout r7 = r0.bottomSaveLayout
                r9 = 48
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r9, r11)
                r1.addView(r7, r10)
                android.widget.TextView r1 = new android.widget.TextView
                r1.<init>(r2)
                r7 = 1096810496(0x41600000, float:14.0)
                r1.setTextSize(r3, r7)
                r10 = -15095832(0xfffffffffvar_a7e8, float:-2.042437E38)
                r1.setTextColor(r10)
                r12 = 17
                r1.setGravity(r12)
                r13 = 788529152(0x2var_, float:1.1641532E-10)
                android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r13, r5)
                r1.setBackgroundDrawable(r14)
                r14 = 1099956224(0x41900000, float:18.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r1.setPadding(r15, r5, r9, r5)
                java.lang.String r9 = "CloseEditor"
                r15 = 2131624852(0x7f0e0394, float:1.8876895E38)
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r15)
                java.lang.String r9 = r9.toUpperCase()
                r1.setText(r9)
                java.lang.String r9 = "fonts/rmedium.ttf"
                android.graphics.Typeface r15 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
                r1.setTypeface(r15)
                android.widget.FrameLayout r15 = r0.bottomSaveLayout
                r11 = -2
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r6, r8)
                r15.addView(r1, r4)
                org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$7p_HW4PD7VHMCM7xEz7ehRvhgjw r4 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$7p_HW4PD7VHMCM7xEz7ehRvhgjw
                r4.<init>()
                r1.setOnClickListener(r4)
                android.widget.TextView r1 = new android.widget.TextView
                r1.<init>(r2)
                r1.setTextSize(r3, r7)
                r1.setTextColor(r10)
                r1.setGravity(r12)
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r13, r5)
                r1.setBackgroundDrawable(r4)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r1.setPadding(r4, r5, r15, r5)
                java.lang.String r4 = "SaveTheme"
                r15 = 2131626981(0x7f0e0be5, float:1.8881214E38)
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r15)
                java.lang.String r4 = r4.toUpperCase()
                r1.setText(r4)
                android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
                r1.setTypeface(r4)
                android.widget.FrameLayout r4 = r0.bottomSaveLayout
                r15 = 53
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r6, r15)
                r4.addView(r1, r8)
                org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$dHz7BMGjIAhbmi_G8NhEXxGCNRM r4 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$dHz7BMGjIAhbmi_G8NhEXxGCNRM
                r4.<init>()
                r1.setOnClickListener(r4)
                android.widget.FrameLayout r1 = new android.widget.FrameLayout
                r1.<init>(r2)
                r0.bottomLayout = r1
                r4 = 8
                r1.setVisibility(r4)
                android.widget.FrameLayout r1 = r0.bottomLayout
                r1.setBackgroundColor(r6)
                android.view.ViewGroup r1 = r0.containerView
                android.widget.FrameLayout r4 = r0.bottomLayout
                r8 = 48
                r15 = 83
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r8, r15)
                r1.addView(r4, r8)
                android.widget.TextView r1 = new android.widget.TextView
                r1.<init>(r2)
                r1.setTextSize(r3, r7)
                r1.setTextColor(r10)
                r1.setGravity(r12)
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r13, r5)
                r1.setBackgroundDrawable(r4)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r1.setPadding(r4, r5, r8, r5)
                java.lang.String r4 = "Cancel"
                r8 = 2131624584(0x7f0e0288, float:1.8876352E38)
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)
                java.lang.String r4 = r4.toUpperCase()
                r1.setText(r4)
                android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
                r1.setTypeface(r4)
                android.widget.FrameLayout r4 = r0.bottomLayout
                r8 = 51
                android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r6, r8)
                r4.addView(r1, r15)
                org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$hW04JYniAptAOlWceRjl0q3qmwg r4 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$hW04JYniAptAOlWceRjl0q3qmwg
                r4.<init>()
                r1.setOnClickListener(r4)
                android.widget.LinearLayout r1 = new android.widget.LinearLayout
                r1.<init>(r2)
                r1.setOrientation(r5)
                android.widget.FrameLayout r4 = r0.bottomLayout
                r8 = 53
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r6, r8)
                r4.addView(r1, r8)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r4.setTextSize(r3, r7)
                r4.setTextColor(r10)
                r4.setGravity(r12)
                android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r13, r5)
                r4.setBackgroundDrawable(r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r4.setPadding(r8, r5, r15, r5)
                java.lang.String r8 = "Default"
                r15 = 2131625012(0x7f0e0434, float:1.887722E38)
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r15)
                java.lang.String r8 = r8.toUpperCase()
                r4.setText(r8)
                android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
                r4.setTypeface(r8)
                r8 = 51
                android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r6, r8)
                r1.addView(r4, r15)
                org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$6bYjD5WZZU2NLveKog9yVMpod2M r8 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$6bYjD5WZZU2NLveKog9yVMpod2M
                r8.<init>()
                r4.setOnClickListener(r8)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                r4.setTextSize(r3, r7)
                r4.setTextColor(r10)
                r4.setGravity(r12)
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r13, r5)
                r4.setBackgroundDrawable(r2)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r4.setPadding(r2, r5, r3, r5)
                java.lang.String r2 = "Save"
                r3 = 2131626978(0x7f0e0be2, float:1.8881207E38)
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)
                java.lang.String r2 = r2.toUpperCase()
                r4.setText(r2)
                android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
                r4.setTypeface(r2)
                r2 = 51
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r6, r2)
                r1.addView(r4, r2)
                org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$W2-jYIlRWnRMAK65uZtlY8G98l4 r1 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$W2-jYIlRWnRMAK65uZtlY8G98l4
                r1.<init>()
                r4.setOnClickListener(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.<init>(org.telegram.ui.Components.ThemeEditorView, android.content.Context, java.util.ArrayList):void");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ThemeEditorView$EditorAlert(View view, int i) {
            if (i != 0) {
                RecyclerView.Adapter adapter = this.listView.getAdapter();
                ListAdapter listAdapter2 = this.listAdapter;
                if (adapter == listAdapter2) {
                    ArrayList unused = this.this$0.currentThemeDesription = listAdapter2.getItem(i - 1);
                } else {
                    ArrayList unused2 = this.this$0.currentThemeDesription = this.searchAdapter.getItem(i - 1);
                }
                int unused3 = this.this$0.currentThemeDesriptionPosition = i;
                for (int i2 = 0; i2 < this.this$0.currentThemeDesription.size(); i2++) {
                    ThemeDescription themeDescription = (ThemeDescription) this.this$0.currentThemeDesription.get(i2);
                    if (themeDescription.getCurrentKey().equals("chat_wallpaper")) {
                        this.this$0.wallpaperUpdater.showAlert(true);
                        return;
                    }
                    themeDescription.startEditing();
                    if (i2 == 0) {
                        this.colorPicker.setColor(themeDescription.getCurrentColor());
                    }
                }
                setColorPickerVisible(true);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$1 */
        public /* synthetic */ void lambda$new$1$ThemeEditorView$EditorAlert(View view) {
            dismiss();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$2 */
        public /* synthetic */ void lambda$new$2$ThemeEditorView$EditorAlert(View view) {
            Theme.saveCurrentTheme(this.this$0.themeInfo, true, false, false);
            setOnDismissListener((DialogInterface.OnDismissListener) null);
            dismiss();
            this.this$0.close();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$3 */
        public /* synthetic */ void lambda$new$3$ThemeEditorView$EditorAlert(View view) {
            for (int i = 0; i < this.this$0.currentThemeDesription.size(); i++) {
                ((ThemeDescription) this.this$0.currentThemeDesription.get(i)).setPreviousColor();
            }
            setColorPickerVisible(false);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$4 */
        public /* synthetic */ void lambda$new$4$ThemeEditorView$EditorAlert(View view) {
            for (int i = 0; i < this.this$0.currentThemeDesription.size(); i++) {
                ((ThemeDescription) this.this$0.currentThemeDesription.get(i)).setDefaultColor();
            }
            setColorPickerVisible(false);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$5 */
        public /* synthetic */ void lambda$new$5$ThemeEditorView$EditorAlert(View view) {
            setColorPickerVisible(false);
        }

        private void runShadowAnimation(final int i, final boolean z) {
            if ((z && this.shadow[i].getTag() != null) || (!z && this.shadow[i].getTag() == null)) {
                this.shadow[i].setTag(z ? null : 1);
                if (z) {
                    this.shadow[i].setVisibility(0);
                }
                AnimatorSet[] animatorSetArr = this.shadowAnimation;
                if (animatorSetArr[i] != null) {
                    animatorSetArr[i].cancel();
                }
                this.shadowAnimation[i] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[i];
                Animator[] animatorArr = new Animator[1];
                View view = this.shadow[i];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[i].setDuration(150);
                this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (EditorAlert.this.shadowAnimation[i] != null && EditorAlert.this.shadowAnimation[i].equals(animator)) {
                            if (!z) {
                                EditorAlert.this.shadow[i].setVisibility(4);
                            }
                            EditorAlert.this.shadowAnimation[i] = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (EditorAlert.this.shadowAnimation[i] != null && EditorAlert.this.shadowAnimation[i].equals(animator)) {
                            EditorAlert.this.shadowAnimation[i] = null;
                        }
                    }
                });
                this.shadowAnimation[i].start();
            }
        }

        public void dismissInternal() {
            super.dismissInternal();
            if (this.searchField.searchEditText.isFocused()) {
                AndroidUtilities.hideKeyboard(this.searchField.searchEditText);
            }
        }

        /* access modifiers changed from: private */
        public void setColorPickerVisible(boolean z) {
            float f = 0.0f;
            if (z) {
                this.animationInProgress = true;
                this.colorPicker.setVisibility(0);
                this.bottomLayout.setVisibility(0);
                this.colorPicker.setAlpha(0.0f);
                this.bottomLayout.setAlpha(0.0f);
                this.previousScrollPosition = this.scrollOffsetY;
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.colorPicker, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.listView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.frameLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.shadow[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.searchEmptyView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.bottomSaveLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofInt(this, "scrollOffsetY", new int[]{this.listView.getPaddingTop()})});
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(this.this$0.decelerateInterpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        EditorAlert.this.listView.setVisibility(4);
                        EditorAlert.this.searchField.setVisibility(4);
                        EditorAlert.this.bottomSaveLayout.setVisibility(4);
                        boolean unused = EditorAlert.this.animationInProgress = false;
                    }
                });
                animatorSet.start();
                return;
            }
            if (this.this$0.parentActivity != null) {
                ((LaunchActivity) this.this$0.parentActivity).rebuildAllFragments(false);
            }
            Theme.saveCurrentTheme(this.this$0.themeInfo, false, false, false);
            if (this.listView.getAdapter() == this.listAdapter) {
                AndroidUtilities.hideKeyboard(getCurrentFocus());
            }
            this.animationInProgress = true;
            this.listView.setVisibility(0);
            this.bottomSaveLayout.setVisibility(0);
            this.searchField.setVisibility(0);
            this.listView.setAlpha(0.0f);
            AnimatorSet animatorSet2 = new AnimatorSet();
            Animator[] animatorArr = new Animator[8];
            animatorArr[0] = ObjectAnimator.ofFloat(this.colorPicker, View.ALPHA, new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.listView, View.ALPHA, new float[]{1.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.frameLayout, View.ALPHA, new float[]{1.0f});
            View[] viewArr = this.shadow;
            View view = viewArr[0];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (viewArr[0].getTag() == null) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[4] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorArr[5] = ObjectAnimator.ofFloat(this.searchEmptyView, View.ALPHA, new float[]{1.0f});
            animatorArr[6] = ObjectAnimator.ofFloat(this.bottomSaveLayout, View.ALPHA, new float[]{1.0f});
            animatorArr[7] = ObjectAnimator.ofInt(this, "scrollOffsetY", new int[]{this.previousScrollPosition});
            animatorSet2.playTogether(animatorArr);
            animatorSet2.setDuration(150);
            animatorSet2.setInterpolator(this.this$0.decelerateInterpolator);
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (EditorAlert.this.listView.getAdapter() == EditorAlert.this.searchAdapter) {
                        EditorAlert.this.searchField.showKeyboard();
                    }
                    EditorAlert.this.colorPicker.setVisibility(8);
                    EditorAlert.this.bottomLayout.setVisibility(8);
                    boolean unused = EditorAlert.this.animationInProgress = false;
                }
            });
            animatorSet2.start();
            this.listView.getAdapter().notifyItemChanged(this.this$0.currentThemeDesriptionPosition);
        }

        /* access modifiers changed from: private */
        public int getCurrentTop() {
            if (this.listView.getChildCount() == 0) {
                return -1000;
            }
            int i = 0;
            View childAt = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
            if (holder == null) {
                return -1000;
            }
            int paddingTop = this.listView.getPaddingTop();
            if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
                i = childAt.getTop();
            }
            return paddingTop - i;
        }

        /* access modifiers changed from: private */
        @SuppressLint({"NewApi"})
        public void updateLayout() {
            int i;
            if (this.listView.getChildCount() > 0 && this.listView.getVisibility() == 0 && !this.animationInProgress) {
                int i2 = 0;
                View childAt = this.listView.getChildAt(0);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
                if (this.listView.getVisibility() != 0 || this.animationInProgress) {
                    i = this.listView.getPaddingTop();
                } else {
                    i = childAt.getTop() - AndroidUtilities.dp(8.0f);
                }
                if (i <= (-AndroidUtilities.dp(1.0f)) || holder == null || holder.getAdapterPosition() != 0) {
                    runShadowAnimation(0, true);
                } else {
                    runShadowAnimation(0, false);
                    i2 = i;
                }
                if (this.scrollOffsetY != i2) {
                    setScrollOffsetY(i2);
                }
            }
        }

        @Keep
        public int getScrollOffsetY() {
            return this.scrollOffsetY;
        }

        @Keep
        public void setScrollOffsetY(int i) {
            RecyclerListView recyclerListView = this.listView;
            this.scrollOffsetY = i;
            recyclerListView.setTopGlowOffset(i);
            this.frameLayout.setTranslationY((float) this.scrollOffsetY);
            this.colorPicker.setTranslationY((float) this.scrollOffsetY);
            this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
        }

        public class SearchAdapter extends RecyclerListView.SelectionAdapter {
            private Context context;
            private int lastSearchId;
            private String lastSearchText;
            private ArrayList<CharSequence> searchNames = new ArrayList<>();
            private ArrayList<ArrayList<ThemeDescription>> searchResult = new ArrayList<>();
            private Runnable searchRunnable;

            public int getItemViewType(int i) {
                return i == 0 ? 1 : 0;
            }

            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            public SearchAdapter(Context context2) {
                this.context = context2;
            }

            public CharSequence generateSearchName(String str, String str2) {
                if (TextUtils.isEmpty(str)) {
                    return "";
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                String trim = str.trim();
                String lowerCase = trim.toLowerCase();
                int i = 0;
                while (true) {
                    int indexOf = lowerCase.indexOf(str2, i);
                    if (indexOf == -1) {
                        break;
                    }
                    int length = str2.length() + indexOf;
                    if (i != 0 && i != indexOf + 1) {
                        spannableStringBuilder.append(trim.substring(i, indexOf));
                    } else if (i == 0 && indexOf != 0) {
                        spannableStringBuilder.append(trim.substring(0, indexOf));
                    }
                    String substring = trim.substring(indexOf, Math.min(trim.length(), length));
                    if (substring.startsWith(" ")) {
                        spannableStringBuilder.append(" ");
                    }
                    String trim2 = substring.trim();
                    int length2 = spannableStringBuilder.length();
                    spannableStringBuilder.append(trim2);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(-11697229), length2, trim2.length() + length2, 33);
                    i = length;
                }
                if (i != -1 && i < trim.length()) {
                    spannableStringBuilder.append(trim.substring(i));
                }
                return spannableStringBuilder;
            }

            /* access modifiers changed from: private */
            /* renamed from: searchDialogsInternal */
            public void lambda$searchDialogs$1(String str, int i) {
                try {
                    String lowerCase = str.trim().toLowerCase();
                    if (lowerCase.length() == 0) {
                        this.lastSearchId = -1;
                        updateSearchResults(new ArrayList(), new ArrayList(), this.lastSearchId);
                        return;
                    }
                    String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
                    if (lowerCase.equals(translitString) || translitString.length() == 0) {
                        translitString = null;
                    }
                    int i2 = (translitString != null ? 1 : 0) + 1;
                    String[] strArr = new String[i2];
                    strArr[0] = lowerCase;
                    if (translitString != null) {
                        strArr[1] = translitString;
                    }
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    int size = EditorAlert.this.listAdapter.items.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        ArrayList arrayList3 = (ArrayList) EditorAlert.this.listAdapter.items.get(i3);
                        String currentKey = ((ThemeDescription) arrayList3.get(0)).getCurrentKey();
                        String lowerCase2 = currentKey.toLowerCase();
                        int i4 = 0;
                        while (true) {
                            if (i4 >= i2) {
                                break;
                            }
                            String str2 = strArr[i4];
                            if (lowerCase2.contains(str2)) {
                                arrayList.add(arrayList3);
                                arrayList2.add(generateSearchName(currentKey, str2));
                                break;
                            }
                            i4++;
                        }
                    }
                    updateSearchResults(arrayList, arrayList2, i);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }

            private void updateSearchResults(ArrayList<ArrayList<ThemeDescription>> arrayList, ArrayList<CharSequence> arrayList2, int i) {
                AndroidUtilities.runOnUIThread(
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  
                      (wrap: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$f2ZEOVW0JnFD5dbGFZ2fs_RyvZ4 : 0x0002: CONSTRUCTOR  (r0v0 org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$f2ZEOVW0JnFD5dbGFZ2fs_RyvZ4) = 
                      (r1v0 'this' org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter A[THIS])
                      (r4v0 'i' int)
                      (r2v0 'arrayList' java.util.ArrayList<java.util.ArrayList<org.telegram.ui.ActionBar.ThemeDescription>>)
                      (r3v0 'arrayList2' java.util.ArrayList<java.lang.CharSequence>)
                     call: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$f2ZEOVW0JnFD5dbGFZ2fs_RyvZ4.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter, int, java.util.ArrayList, java.util.ArrayList):void type: CONSTRUCTOR)
                     org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchAdapter.updateSearchResults(java.util.ArrayList, java.util.ArrayList, int):void, dex: classes3.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$f2ZEOVW0JnFD5dbGFZ2fs_RyvZ4) = 
                      (r1v0 'this' org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter A[THIS])
                      (r4v0 'i' int)
                      (r2v0 'arrayList' java.util.ArrayList<java.util.ArrayList<org.telegram.ui.ActionBar.ThemeDescription>>)
                      (r3v0 'arrayList2' java.util.ArrayList<java.lang.CharSequence>)
                     call: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$f2ZEOVW0JnFD5dbGFZ2fs_RyvZ4.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter, int, java.util.ArrayList, java.util.ArrayList):void type: CONSTRUCTOR in method: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchAdapter.updateSearchResults(java.util.ArrayList, java.util.ArrayList, int):void, dex: classes3.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$f2ZEOVW0JnFD5dbGFZ2fs_RyvZ4, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    */
                /*
                    this = this;
                    org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$f2ZEOVW0JnFD5dbGFZ2fs_RyvZ4 r0 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$f2ZEOVW0JnFD5dbGFZ2fs_RyvZ4
                    r0.<init>(r1, r4, r2, r3)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchAdapter.updateSearchResults(java.util.ArrayList, java.util.ArrayList, int):void");
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$updateSearchResults$0 */
            public /* synthetic */ void lambda$updateSearchResults$0$ThemeEditorView$EditorAlert$SearchAdapter(int i, ArrayList arrayList, ArrayList arrayList2) {
                if (i == this.lastSearchId) {
                    if (EditorAlert.this.listView.getAdapter() != EditorAlert.this.searchAdapter) {
                        EditorAlert editorAlert = EditorAlert.this;
                        int unused = editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                        EditorAlert.this.listView.setAdapter(EditorAlert.this.searchAdapter);
                        EditorAlert.this.searchAdapter.notifyDataSetChanged();
                    }
                    boolean z = true;
                    boolean z2 = !this.searchResult.isEmpty() && arrayList.isEmpty();
                    if (!this.searchResult.isEmpty() || !arrayList.isEmpty()) {
                        z = false;
                    }
                    if (z2) {
                        EditorAlert editorAlert2 = EditorAlert.this;
                        int unused2 = editorAlert2.topBeforeSwitch = editorAlert2.getCurrentTop();
                    }
                    this.searchResult = arrayList;
                    this.searchNames = arrayList2;
                    notifyDataSetChanged();
                    if (!z && !z2 && EditorAlert.this.topBeforeSwitch > 0) {
                        EditorAlert.this.layoutManager.scrollToPositionWithOffset(0, -EditorAlert.this.topBeforeSwitch);
                        int unused3 = EditorAlert.this.topBeforeSwitch = -1000;
                    }
                    EditorAlert.this.searchEmptyView.showTextView();
                }
            }

            public void searchDialogs(String str) {
                if (str == null || !str.equals(this.lastSearchText)) {
                    this.lastSearchText = str;
                    if (this.searchRunnable != null) {
                        Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                        this.searchRunnable = null;
                    }
                    if (str == null || str.length() == 0) {
                        this.searchResult.clear();
                        EditorAlert editorAlert = EditorAlert.this;
                        int unused = editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                        this.lastSearchId = -1;
                        notifyDataSetChanged();
                        return;
                    }
                    int i = this.lastSearchId + 1;
                    this.lastSearchId = i;
                    this.searchRunnable = 
                    /*  JADX ERROR: Method code generation error
                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x002f: IPUT  
                          (wrap: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$0AlTYKfK1LurH-v0JsFx_OKo_MY : 0x002c: CONSTRUCTOR  (r1v0 org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$0AlTYKfK1LurH-v0JsFx_OKo_MY) = 
                          (r3v0 'this' org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter A[THIS])
                          (r4v0 'str' java.lang.String)
                          (r0v4 'i' int)
                         call: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$0AlTYKfK1LurH-v0JsFx_OKo_MY.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter, java.lang.String, int):void type: CONSTRUCTOR)
                          (r3v0 'this' org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter A[THIS])
                         org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchAdapter.searchRunnable java.lang.Runnable in method: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchAdapter.searchDialogs(java.lang.String):void, dex: classes3.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x002c: CONSTRUCTOR  (r1v0 org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$0AlTYKfK1LurH-v0JsFx_OKo_MY) = 
                          (r3v0 'this' org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter A[THIS])
                          (r4v0 'str' java.lang.String)
                          (r0v4 'i' int)
                         call: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$0AlTYKfK1LurH-v0JsFx_OKo_MY.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter, java.lang.String, int):void type: CONSTRUCTOR in method: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchAdapter.searchDialogs(java.lang.String):void, dex: classes3.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	... 70 more
                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$0AlTYKfK1LurH-v0JsFx_OKo_MY, state: NOT_LOADED
                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	... 74 more
                        */
                    /*
                        this = this;
                        if (r4 == 0) goto L_0x000b
                        java.lang.String r0 = r3.lastSearchText
                        boolean r0 = r4.equals(r0)
                        if (r0 == 0) goto L_0x000b
                        return
                    L_0x000b:
                        r3.lastSearchText = r4
                        java.lang.Runnable r0 = r3.searchRunnable
                        if (r0 == 0) goto L_0x001b
                        org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.searchQueue
                        java.lang.Runnable r1 = r3.searchRunnable
                        r0.cancelRunnable(r1)
                        r0 = 0
                        r3.searchRunnable = r0
                    L_0x001b:
                        if (r4 == 0) goto L_0x003b
                        int r0 = r4.length()
                        if (r0 != 0) goto L_0x0024
                        goto L_0x003b
                    L_0x0024:
                        int r0 = r3.lastSearchId
                        int r0 = r0 + 1
                        r3.lastSearchId = r0
                        org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$0AlTYKfK1LurH-v0JsFx_OKo_MY r1 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$0AlTYKfK1LurH-v0JsFx_OKo_MY
                        r1.<init>(r3, r4, r0)
                        r3.searchRunnable = r1
                        org.telegram.messenger.DispatchQueue r4 = org.telegram.messenger.Utilities.searchQueue
                        java.lang.Runnable r0 = r3.searchRunnable
                        r1 = 300(0x12c, double:1.48E-321)
                        r4.postRunnable(r0, r1)
                        goto L_0x004f
                    L_0x003b:
                        java.util.ArrayList<java.util.ArrayList<org.telegram.ui.ActionBar.ThemeDescription>> r4 = r3.searchResult
                        r4.clear()
                        org.telegram.ui.Components.ThemeEditorView$EditorAlert r4 = org.telegram.ui.Components.ThemeEditorView.EditorAlert.this
                        int r0 = r4.getCurrentTop()
                        int unused = r4.topBeforeSwitch = r0
                        r4 = -1
                        r3.lastSearchId = r4
                        r3.notifyDataSetChanged()
                    L_0x004f:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchAdapter.searchDialogs(java.lang.String):void");
                }

                public int getItemCount() {
                    if (this.searchResult.isEmpty()) {
                        return 0;
                    }
                    return this.searchResult.size() + 1;
                }

                public ArrayList<ThemeDescription> getItem(int i) {
                    if (i < 0 || i >= this.searchResult.size()) {
                        return null;
                    }
                    return this.searchResult.get(i);
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View view;
                    if (i != 0) {
                        view = new View(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    } else {
                        view = new TextColorThemeCell(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    }
                    return new RecyclerListView.Holder(view);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    if (viewHolder.getItemViewType() == 0) {
                        int i2 = i - 1;
                        int i3 = 0;
                        ThemeDescription themeDescription = (ThemeDescription) this.searchResult.get(i2).get(0);
                        if (!themeDescription.getCurrentKey().equals("chat_wallpaper")) {
                            i3 = themeDescription.getSetColor();
                        }
                        ((TextColorThemeCell) viewHolder.itemView).setTextAndColor(this.searchNames.get(i2), i3);
                    }
                }
            }

            private class ListAdapter extends RecyclerListView.SelectionAdapter {
                private Context context;
                /* access modifiers changed from: private */
                public ArrayList<ArrayList<ThemeDescription>> items = new ArrayList<>();

                public int getItemViewType(int i) {
                    return i == 0 ? 1 : 0;
                }

                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    return true;
                }

                public ListAdapter(EditorAlert editorAlert, Context context2, ArrayList<ThemeDescription> arrayList) {
                    this.context = context2;
                    HashMap hashMap = new HashMap();
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        ThemeDescription themeDescription = arrayList.get(i);
                        String currentKey = themeDescription.getCurrentKey();
                        ArrayList arrayList2 = (ArrayList) hashMap.get(currentKey);
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                            hashMap.put(currentKey, arrayList2);
                            this.items.add(arrayList2);
                        }
                        arrayList2.add(themeDescription);
                    }
                    if (Build.VERSION.SDK_INT >= 26 && !hashMap.containsKey("windowBackgroundGray")) {
                        ArrayList arrayList3 = new ArrayList();
                        arrayList3.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
                        this.items.add(arrayList3);
                    }
                }

                public int getItemCount() {
                    if (this.items.isEmpty()) {
                        return 0;
                    }
                    return this.items.size() + 1;
                }

                public ArrayList<ThemeDescription> getItem(int i) {
                    if (i < 0 || i >= this.items.size()) {
                        return null;
                    }
                    return this.items.get(i);
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View view;
                    if (i != 0) {
                        view = new View(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    } else {
                        view = new TextColorThemeCell(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    }
                    return new RecyclerListView.Holder(view);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    if (viewHolder.getItemViewType() == 0) {
                        int i2 = 0;
                        ThemeDescription themeDescription = (ThemeDescription) this.items.get(i - 1).get(0);
                        if (!themeDescription.getCurrentKey().equals("chat_wallpaper")) {
                            i2 = themeDescription.getSetColor();
                        }
                        ((TextColorThemeCell) viewHolder.itemView).setTextAndColor(themeDescription.getTitle(), i2);
                    }
                }
            }
        }

        public void show(Activity activity, Theme.ThemeInfo themeInfo2) {
            if (Instance != null) {
                Instance.destroy();
            }
            this.themeInfo = themeInfo2;
            this.windowView = new FrameLayout(activity) {
                private boolean dragging;
                private float startX;
                private float startY;

                static /* synthetic */ void lambda$onTouchEvent$0(DialogInterface dialogInterface) {
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return true;
                }

                /* JADX WARNING: Code restructure failed: missing block: B:29:0x0088, code lost:
                    if (r6.fragmentsStack.isEmpty() != false) goto L_0x008a;
                 */
                /* JADX WARNING: Removed duplicated region for block: B:32:0x008d  */
                /* JADX WARNING: Removed duplicated region for block: B:34:0x0093  */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public boolean onTouchEvent(android.view.MotionEvent r11) {
                    /*
                        r10 = this;
                        float r0 = r11.getRawX()
                        float r1 = r11.getRawY()
                        int r2 = r11.getAction()
                        r3 = 2
                        r4 = 0
                        r5 = 1
                        if (r2 != 0) goto L_0x0017
                        r10.startX = r0
                        r10.startY = r1
                        goto L_0x00e8
                    L_0x0017:
                        int r2 = r11.getAction()
                        if (r2 != r3) goto L_0x004a
                        boolean r2 = r10.dragging
                        if (r2 != 0) goto L_0x004a
                        float r2 = r10.startX
                        float r2 = r2 - r0
                        float r2 = java.lang.Math.abs(r2)
                        r6 = 1050253722(0x3e99999a, float:0.3)
                        float r7 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r6, r5)
                        int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                        if (r2 >= 0) goto L_0x0042
                        float r2 = r10.startY
                        float r2 = r2 - r1
                        float r2 = java.lang.Math.abs(r2)
                        float r6 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r6, r4)
                        int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
                        if (r2 < 0) goto L_0x00e8
                    L_0x0042:
                        r10.dragging = r5
                        r10.startX = r0
                        r10.startY = r1
                        goto L_0x00e8
                    L_0x004a:
                        int r2 = r11.getAction()
                        if (r2 != r5) goto L_0x00e8
                        boolean r2 = r10.dragging
                        if (r2 != 0) goto L_0x00e8
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        org.telegram.ui.Components.ThemeEditorView$EditorAlert r2 = r2.editorAlert
                        if (r2 != 0) goto L_0x00e8
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        android.app.Activity r2 = r2.parentActivity
                        org.telegram.ui.LaunchActivity r2 = (org.telegram.ui.LaunchActivity) r2
                        boolean r6 = org.telegram.messenger.AndroidUtilities.isTablet()
                        r7 = 0
                        if (r6 == 0) goto L_0x008a
                        org.telegram.ui.ActionBar.ActionBarLayout r6 = r2.getLayersActionBarLayout()
                        if (r6 == 0) goto L_0x007a
                        java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = r6.fragmentsStack
                        boolean r8 = r8.isEmpty()
                        if (r8 == 0) goto L_0x007a
                        r6 = r7
                    L_0x007a:
                        if (r6 != 0) goto L_0x008b
                        org.telegram.ui.ActionBar.ActionBarLayout r6 = r2.getRightActionBarLayout()
                        if (r6 == 0) goto L_0x008b
                        java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = r6.fragmentsStack
                        boolean r8 = r8.isEmpty()
                        if (r8 == 0) goto L_0x008b
                    L_0x008a:
                        r6 = r7
                    L_0x008b:
                        if (r6 != 0) goto L_0x0091
                        org.telegram.ui.ActionBar.ActionBarLayout r6 = r2.getActionBarLayout()
                    L_0x0091:
                        if (r6 == 0) goto L_0x00e8
                        java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r6.fragmentsStack
                        boolean r2 = r2.isEmpty()
                        if (r2 != 0) goto L_0x00a9
                        java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r6.fragmentsStack
                        int r6 = r2.size()
                        int r6 = r6 - r5
                        java.lang.Object r2 = r2.get(r6)
                        r7 = r2
                        org.telegram.ui.ActionBar.BaseFragment r7 = (org.telegram.ui.ActionBar.BaseFragment) r7
                    L_0x00a9:
                        if (r7 == 0) goto L_0x00e8
                        java.util.ArrayList r2 = r7.getThemeDescriptions()
                        if (r2 == 0) goto L_0x00e8
                        org.telegram.ui.Components.ThemeEditorView r6 = org.telegram.ui.Components.ThemeEditorView.this
                        org.telegram.ui.Components.ThemeEditorView$EditorAlert r7 = new org.telegram.ui.Components.ThemeEditorView$EditorAlert
                        org.telegram.ui.Components.ThemeEditorView r8 = org.telegram.ui.Components.ThemeEditorView.this
                        android.app.Activity r9 = r8.parentActivity
                        r7.<init>(r8, r9, r2)
                        org.telegram.ui.Components.ThemeEditorView.EditorAlert unused = r6.editorAlert = r7
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        org.telegram.ui.Components.ThemeEditorView$EditorAlert r2 = r2.editorAlert
                        org.telegram.ui.Components.-$$Lambda$ThemeEditorView$1$2v3WZDTt6HobbCzO8Hyuvi4NqYo r6 = org.telegram.ui.Components.$$Lambda$ThemeEditorView$1$2v3WZDTt6HobbCzO8Hyuvi4NqYo.INSTANCE
                        r2.setOnDismissListener(r6)
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        org.telegram.ui.Components.ThemeEditorView$EditorAlert r2 = r2.editorAlert
                        org.telegram.ui.Components.-$$Lambda$ThemeEditorView$1$nyAeTH8X8G64R_GYzVABKX3oCog r6 = new org.telegram.ui.Components.-$$Lambda$ThemeEditorView$1$nyAeTH8X8G64R_GYzVABKX3oCog
                        r6.<init>()
                        r2.setOnDismissListener(r6)
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        org.telegram.ui.Components.ThemeEditorView$EditorAlert r2 = r2.editorAlert
                        r2.show()
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        r2.hide()
                    L_0x00e8:
                        boolean r2 = r10.dragging
                        if (r2 == 0) goto L_0x022c
                        int r2 = r11.getAction()
                        if (r2 != r3) goto L_0x021f
                        float r11 = r10.startX
                        float r11 = r0 - r11
                        float r2 = r10.startY
                        float r2 = r1 - r2
                        org.telegram.ui.Components.ThemeEditorView r6 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r6 = r6.windowLayoutParams
                        int r7 = r6.x
                        float r7 = (float) r7
                        float r7 = r7 + r11
                        int r11 = (int) r7
                        r6.x = r11
                        org.telegram.ui.Components.ThemeEditorView r11 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r11 = r11.windowLayoutParams
                        int r6 = r11.y
                        float r6 = (float) r6
                        float r6 = r6 + r2
                        int r2 = (int) r6
                        r11.y = r2
                        org.telegram.ui.Components.ThemeEditorView r11 = org.telegram.ui.Components.ThemeEditorView.this
                        int r11 = r11.editorWidth
                        int r11 = r11 / r3
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                        int r2 = r2.x
                        int r3 = -r11
                        if (r2 >= r3) goto L_0x012f
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                        r2.x = r3
                        goto L_0x015d
                    L_0x012f:
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                        int r2 = r2.x
                        android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
                        int r3 = r3.x
                        org.telegram.ui.Components.ThemeEditorView r6 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r6 = r6.windowLayoutParams
                        int r6 = r6.width
                        int r3 = r3 - r6
                        int r3 = r3 + r11
                        if (r2 <= r3) goto L_0x015d
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                        android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
                        int r3 = r3.x
                        org.telegram.ui.Components.ThemeEditorView r6 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r6 = r6.windowLayoutParams
                        int r6 = r6.width
                        int r3 = r3 - r6
                        int r3 = r3 + r11
                        r2.x = r3
                    L_0x015d:
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                        int r2 = r2.x
                        r3 = 1056964608(0x3var_, float:0.5)
                        r6 = 1065353216(0x3var_, float:1.0)
                        if (r2 >= 0) goto L_0x017a
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                        int r2 = r2.x
                        float r2 = (float) r2
                        float r11 = (float) r11
                        float r2 = r2 / r11
                        float r2 = r2 * r3
                        float r6 = r6 + r2
                        goto L_0x01ad
                    L_0x017a:
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                        int r2 = r2.x
                        android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
                        int r7 = r7.x
                        org.telegram.ui.Components.ThemeEditorView r8 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r8 = r8.windowLayoutParams
                        int r8 = r8.width
                        int r7 = r7 - r8
                        if (r2 <= r7) goto L_0x01ad
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r2 = r2.windowLayoutParams
                        int r2 = r2.x
                        android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
                        int r7 = r7.x
                        int r2 = r2 - r7
                        org.telegram.ui.Components.ThemeEditorView r7 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r7 = r7.windowLayoutParams
                        int r7 = r7.width
                        int r2 = r2 + r7
                        float r2 = (float) r2
                        float r11 = (float) r11
                        float r2 = r2 / r11
                        float r2 = r2 * r3
                        float r6 = r6 - r2
                    L_0x01ad:
                        org.telegram.ui.Components.ThemeEditorView r11 = org.telegram.ui.Components.ThemeEditorView.this
                        android.widget.FrameLayout r11 = r11.windowView
                        float r11 = r11.getAlpha()
                        int r11 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
                        if (r11 == 0) goto L_0x01c4
                        org.telegram.ui.Components.ThemeEditorView r11 = org.telegram.ui.Components.ThemeEditorView.this
                        android.widget.FrameLayout r11 = r11.windowView
                        r11.setAlpha(r6)
                    L_0x01c4:
                        org.telegram.ui.Components.ThemeEditorView r11 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r11 = r11.windowLayoutParams
                        int r11 = r11.y
                        if (r11 >= 0) goto L_0x01d7
                        org.telegram.ui.Components.ThemeEditorView r11 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r11 = r11.windowLayoutParams
                        r11.y = r4
                        goto L_0x0205
                    L_0x01d7:
                        org.telegram.ui.Components.ThemeEditorView r11 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r11 = r11.windowLayoutParams
                        int r11 = r11.y
                        android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
                        int r2 = r2.y
                        org.telegram.ui.Components.ThemeEditorView r3 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r3 = r3.windowLayoutParams
                        int r3 = r3.height
                        int r2 = r2 - r3
                        int r2 = r2 + r4
                        if (r11 <= r2) goto L_0x0205
                        org.telegram.ui.Components.ThemeEditorView r11 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r11 = r11.windowLayoutParams
                        android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
                        int r2 = r2.y
                        org.telegram.ui.Components.ThemeEditorView r3 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r3 = r3.windowLayoutParams
                        int r3 = r3.height
                        int r2 = r2 - r3
                        int r2 = r2 + r4
                        r11.y = r2
                    L_0x0205:
                        org.telegram.ui.Components.ThemeEditorView r11 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager r11 = r11.windowManager
                        org.telegram.ui.Components.ThemeEditorView r2 = org.telegram.ui.Components.ThemeEditorView.this
                        android.widget.FrameLayout r2 = r2.windowView
                        org.telegram.ui.Components.ThemeEditorView r3 = org.telegram.ui.Components.ThemeEditorView.this
                        android.view.WindowManager$LayoutParams r3 = r3.windowLayoutParams
                        r11.updateViewLayout(r2, r3)
                        r10.startX = r0
                        r10.startY = r1
                        goto L_0x022c
                    L_0x021f:
                        int r11 = r11.getAction()
                        if (r11 != r5) goto L_0x022c
                        r10.dragging = r4
                        org.telegram.ui.Components.ThemeEditorView r11 = org.telegram.ui.Components.ThemeEditorView.this
                        r11.animateToBoundsMaybe()
                    L_0x022c:
                        return r5
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.AnonymousClass1.onTouchEvent(android.view.MotionEvent):boolean");
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onTouchEvent$1 */
                public /* synthetic */ void lambda$onTouchEvent$1$ThemeEditorView$1(DialogInterface dialogInterface) {
                    EditorAlert unused = ThemeEditorView.this.editorAlert = null;
                    ThemeEditorView.this.show();
                }
            };
            this.windowManager = (WindowManager) activity.getSystemService("window");
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
            this.preferences = sharedPreferences;
            int i = sharedPreferences.getInt("sidex", 1);
            int i2 = this.preferences.getInt("sidey", 0);
            float f = this.preferences.getFloat("px", 0.0f);
            float f2 = this.preferences.getFloat("py", 0.0f);
            try {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                this.windowLayoutParams = layoutParams;
                int i3 = this.editorWidth;
                layoutParams.width = i3;
                layoutParams.height = this.editorHeight;
                layoutParams.x = getSideCoord(true, i, f, i3);
                this.windowLayoutParams.y = getSideCoord(false, i2, f2, this.editorHeight);
                WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
                layoutParams2.format = -3;
                layoutParams2.gravity = 51;
                layoutParams2.type = 99;
                layoutParams2.flags = 16777736;
                this.windowManager.addView(this.windowView, layoutParams2);
                this.wallpaperUpdater = new WallpaperUpdater(activity, (BaseFragment) null, new WallpaperUpdater.WallpaperUpdaterDelegate() {
                    public void didSelectWallpaper(File file, Bitmap bitmap, boolean z) {
                        Theme.setThemeWallpaper(ThemeEditorView.this.themeInfo, bitmap, file);
                    }

                    public void needOpenColorPicker() {
                        for (int i = 0; i < ThemeEditorView.this.currentThemeDesription.size(); i++) {
                            ThemeDescription themeDescription = (ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(i);
                            themeDescription.startEditing();
                            if (i == 0) {
                                ThemeEditorView.this.editorAlert.colorPicker.setColor(themeDescription.getCurrentColor());
                            }
                        }
                        ThemeEditorView.this.editorAlert.setColorPickerVisible(true);
                    }
                });
                Instance = this;
                this.parentActivity = activity;
                showWithAnimation();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        private void showWithAnimation() {
            this.windowView.setBackgroundResource(NUM);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.windowView, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.windowView, View.SCALE_Y, new float[]{0.0f, 1.0f})});
            animatorSet.setInterpolator(this.decelerateInterpolator);
            animatorSet.setDuration(150);
            animatorSet.start();
        }

        private static int getSideCoord(boolean z, int i, float f, int i2) {
            int i3;
            int i4;
            if (z) {
                i3 = AndroidUtilities.displaySize.x;
            } else {
                i3 = AndroidUtilities.displaySize.y - i2;
                i2 = ActionBar.getCurrentActionBarHeight();
            }
            int i5 = i3 - i2;
            if (i == 0) {
                i4 = AndroidUtilities.dp(10.0f);
            } else if (i == 1) {
                i4 = i5 - AndroidUtilities.dp(10.0f);
            } else {
                i4 = Math.round(((float) (i5 - AndroidUtilities.dp(20.0f))) * f) + AndroidUtilities.dp(10.0f);
            }
            return !z ? i4 + ActionBar.getCurrentActionBarHeight() : i4;
        }

        /* access modifiers changed from: private */
        public void hide() {
            if (this.parentActivity != null) {
                try {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.windowView, View.SCALE_X, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.windowView, View.SCALE_Y, new float[]{1.0f, 0.0f})});
                    animatorSet.setInterpolator(this.decelerateInterpolator);
                    animatorSet.setDuration(150);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (ThemeEditorView.this.windowView != null) {
                                ThemeEditorView.this.windowView.setBackground((Drawable) null);
                                ThemeEditorView.this.windowManager.removeView(ThemeEditorView.this.windowView);
                            }
                        }
                    });
                    animatorSet.start();
                } catch (Exception unused) {
                }
            }
        }

        /* access modifiers changed from: private */
        public void show() {
            if (this.parentActivity != null) {
                try {
                    this.windowManager.addView(this.windowView, this.windowLayoutParams);
                    showWithAnimation();
                } catch (Exception unused) {
                }
            }
        }

        public void close() {
            try {
                this.windowManager.removeView(this.windowView);
            } catch (Exception unused) {
            }
            this.parentActivity = null;
        }

        public void onConfigurationChanged() {
            int i = this.preferences.getInt("sidex", 1);
            int i2 = this.preferences.getInt("sidey", 0);
            float f = this.preferences.getFloat("px", 0.0f);
            float f2 = this.preferences.getFloat("py", 0.0f);
            this.windowLayoutParams.x = getSideCoord(true, i, f, this.editorWidth);
            this.windowLayoutParams.y = getSideCoord(false, i2, f2, this.editorHeight);
            try {
                if (this.windowView.getParent() != null) {
                    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void onActivityResult(int i, int i2, Intent intent) {
            WallpaperUpdater wallpaperUpdater2 = this.wallpaperUpdater;
            if (wallpaperUpdater2 != null) {
                wallpaperUpdater2.onActivityResult(i, i2, intent);
            }
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x010e  */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x0178  */
        /* JADX WARNING: Removed duplicated region for block: B:53:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void animateToBoundsMaybe() {
            /*
                r16 = this;
                r0 = r16
                int r1 = r0.editorWidth
                r2 = 1
                r3 = 0
                r4 = 0
                int r1 = getSideCoord(r2, r3, r4, r1)
                int r5 = r0.editorWidth
                int r5 = getSideCoord(r2, r2, r4, r5)
                int r6 = r0.editorHeight
                int r6 = getSideCoord(r3, r3, r4, r6)
                int r7 = r0.editorHeight
                int r7 = getSideCoord(r3, r2, r4, r7)
                android.content.SharedPreferences r8 = r0.preferences
                android.content.SharedPreferences$Editor r8 = r8.edit()
                r9 = 1101004800(0x41a00000, float:20.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                android.view.WindowManager$LayoutParams r10 = r0.windowLayoutParams
                int r10 = r10.x
                int r10 = r1 - r10
                int r10 = java.lang.Math.abs(r10)
                java.lang.String r12 = "sidex"
                java.lang.String r13 = "x"
                r14 = 1065353216(0x3var_, float:1.0)
                if (r10 <= r9) goto L_0x00de
                android.view.WindowManager$LayoutParams r10 = r0.windowLayoutParams
                int r10 = r10.x
                if (r10 >= 0) goto L_0x004a
                int r15 = r0.editorWidth
                int r15 = -r15
                int r15 = r15 / 4
                if (r10 <= r15) goto L_0x004a
                goto L_0x00de
            L_0x004a:
                int r10 = r5 - r10
                int r10 = java.lang.Math.abs(r10)
                if (r10 <= r9) goto L_0x00b1
                android.view.WindowManager$LayoutParams r10 = r0.windowLayoutParams
                int r10 = r10.x
                android.graphics.Point r15 = org.telegram.messenger.AndroidUtilities.displaySize
                int r15 = r15.x
                int r4 = r0.editorWidth
                int r11 = r15 - r4
                if (r10 <= r11) goto L_0x0068
                int r4 = r4 / 4
                int r4 = r4 * 3
                int r15 = r15 - r4
                if (r10 >= r15) goto L_0x0068
                goto L_0x00b1
            L_0x0068:
                android.widget.FrameLayout r4 = r0.windowView
                float r4 = r4.getAlpha()
                int r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
                if (r4 == 0) goto L_0x009d
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                android.view.WindowManager$LayoutParams r4 = r0.windowLayoutParams
                int r4 = r4.x
                if (r4 >= 0) goto L_0x008c
                int[] r4 = new int[r2]
                int r5 = r0.editorWidth
                int r5 = -r5
                r4[r3] = r5
                android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofInt(r0, r13, r4)
                r1.add(r4)
                goto L_0x009b
            L_0x008c:
                int[] r4 = new int[r2]
                android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                int r5 = r5.x
                r4[r3] = r5
                android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofInt(r0, r13, r4)
                r1.add(r4)
            L_0x009b:
                r4 = 1
                goto L_0x010c
            L_0x009d:
                android.view.WindowManager$LayoutParams r4 = r0.windowLayoutParams
                int r4 = r4.x
                int r4 = r4 - r1
                float r4 = (float) r4
                int r5 = r5 - r1
                float r1 = (float) r5
                float r4 = r4 / r1
                java.lang.String r1 = "px"
                r8.putFloat(r1, r4)
                r1 = 2
                r8.putInt(r12, r1)
                r1 = 0
                goto L_0x010b
            L_0x00b1:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                r8.putInt(r12, r2)
                android.widget.FrameLayout r4 = r0.windowView
                float r4 = r4.getAlpha()
                int r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
                if (r4 == 0) goto L_0x00d2
                android.widget.FrameLayout r4 = r0.windowView
                android.util.Property r10 = android.view.View.ALPHA
                float[] r11 = new float[r2]
                r11[r3] = r14
                android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r10, r11)
                r1.add(r4)
            L_0x00d2:
                int[] r4 = new int[r2]
                r4[r3] = r5
                android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofInt(r0, r13, r4)
                r1.add(r4)
                goto L_0x010b
            L_0x00de:
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r8.putInt(r12, r3)
                android.widget.FrameLayout r5 = r0.windowView
                float r5 = r5.getAlpha()
                int r5 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
                if (r5 == 0) goto L_0x00ff
                android.widget.FrameLayout r5 = r0.windowView
                android.util.Property r10 = android.view.View.ALPHA
                float[] r11 = new float[r2]
                r11[r3] = r14
                android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r11)
                r4.add(r5)
            L_0x00ff:
                int[] r5 = new int[r2]
                r5[r3] = r1
                android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofInt(r0, r13, r5)
                r4.add(r1)
                r1 = r4
            L_0x010b:
                r4 = 0
            L_0x010c:
                if (r4 != 0) goto L_0x0176
                android.view.WindowManager$LayoutParams r5 = r0.windowLayoutParams
                int r5 = r5.y
                int r5 = r6 - r5
                int r5 = java.lang.Math.abs(r5)
                java.lang.String r10 = "y"
                java.lang.String r11 = "sidey"
                if (r5 <= r9) goto L_0x015e
                android.view.WindowManager$LayoutParams r5 = r0.windowLayoutParams
                int r5 = r5.y
                int r12 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
                if (r5 > r12) goto L_0x0129
                goto L_0x015e
            L_0x0129:
                android.view.WindowManager$LayoutParams r5 = r0.windowLayoutParams
                int r5 = r5.y
                int r5 = r7 - r5
                int r5 = java.lang.Math.abs(r5)
                if (r5 > r9) goto L_0x014b
                if (r1 != 0) goto L_0x013c
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
            L_0x013c:
                r8.putInt(r11, r2)
                int[] r5 = new int[r2]
                r5[r3] = r7
                android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofInt(r0, r10, r5)
                r1.add(r5)
                goto L_0x0173
            L_0x014b:
                android.view.WindowManager$LayoutParams r5 = r0.windowLayoutParams
                int r5 = r5.y
                int r5 = r5 - r6
                float r5 = (float) r5
                int r7 = r7 - r6
                float r6 = (float) r7
                float r5 = r5 / r6
                java.lang.String r6 = "py"
                r8.putFloat(r6, r5)
                r5 = 2
                r8.putInt(r11, r5)
                goto L_0x0173
            L_0x015e:
                if (r1 != 0) goto L_0x0165
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
            L_0x0165:
                r8.putInt(r11, r3)
                int[] r5 = new int[r2]
                r5[r3] = r6
                android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofInt(r0, r10, r5)
                r1.add(r5)
            L_0x0173:
                r8.commit()
            L_0x0176:
                if (r1 == 0) goto L_0x01b2
                android.view.animation.DecelerateInterpolator r5 = r0.decelerateInterpolator
                if (r5 != 0) goto L_0x0183
                android.view.animation.DecelerateInterpolator r5 = new android.view.animation.DecelerateInterpolator
                r5.<init>()
                r0.decelerateInterpolator = r5
            L_0x0183:
                android.animation.AnimatorSet r5 = new android.animation.AnimatorSet
                r5.<init>()
                android.view.animation.DecelerateInterpolator r6 = r0.decelerateInterpolator
                r5.setInterpolator(r6)
                r6 = 150(0x96, double:7.4E-322)
                r5.setDuration(r6)
                if (r4 == 0) goto L_0x01ac
                android.widget.FrameLayout r4 = r0.windowView
                android.util.Property r6 = android.view.View.ALPHA
                float[] r2 = new float[r2]
                r7 = 0
                r2[r3] = r7
                android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r4, r6, r2)
                r1.add(r2)
                org.telegram.ui.Components.ThemeEditorView$4 r2 = new org.telegram.ui.Components.ThemeEditorView$4
                r2.<init>()
                r5.addListener(r2)
            L_0x01ac:
                r5.playTogether(r1)
                r5.start()
            L_0x01b2:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.animateToBoundsMaybe():void");
        }

        @Keep
        public int getX() {
            return this.windowLayoutParams.x;
        }

        @Keep
        public int getY() {
            return this.windowLayoutParams.y;
        }

        @Keep
        public void setX(int i) {
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            layoutParams.x = i;
            this.windowManager.updateViewLayout(this.windowView, layoutParams);
        }

        @Keep
        public void setY(int i) {
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            layoutParams.y = i;
            this.windowManager.updateViewLayout(this.windowView, layoutParams);
        }
    }
