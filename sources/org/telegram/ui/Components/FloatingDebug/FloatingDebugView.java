package org.telegram.ui.Components.FloatingDebug;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.GestureDetectorCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes3.dex */
public class FloatingDebugView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private LinearLayout bigLayout;
    private List<FloatingDebugController.DebugItem> debugItems;
    private SpringAnimation fabXSpring;
    private SpringAnimation fabYSpring;
    private Drawable floatingButtonBackground;
    private FrameLayout floatingButtonContainer;
    private GestureDetector.OnGestureListener gestureListener;
    private boolean inLongPress;
    private boolean isBigMenuShown;
    private boolean isFromFling;
    private boolean isScrollDisallowed;
    private boolean isScrolling;
    private RecyclerListView listView;
    private SharedPreferences mPrefs;
    private Runnable onLongPress;
    private TextView titleView;
    private int touchSlop;
    private int wasStatusBar;

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.inLongPress = true;
        performHapticFeedback(0);
    }

    public FloatingDebugView(final Context context) {
        super(context);
        this.onLongPress = new Runnable() { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                FloatingDebugView.this.lambda$new$0();
            }
        };
        this.debugItems = new ArrayList();
        this.gestureListener = new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView.1
            private float startX;
            private float startY;

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                if (!FloatingDebugView.this.inLongPress && !FloatingDebugView.this.isBigMenuShown) {
                    FloatingDebugView.this.showBigMenu(true);
                    return true;
                }
                return false;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                FloatingDebugView floatingDebugView;
                DisplayMetrics displayMetrics;
                float f3;
                if (!FloatingDebugView.this.isScrolling || FloatingDebugView.this.inLongPress) {
                    return false;
                }
                SpringForce spring = FloatingDebugView.this.fabXSpring.getSpring();
                if (FloatingDebugView.this.fabXSpring.getSpring().getFinalPosition() + (f / 7.0f) >= FloatingDebugView.this.getWidth() / 2.0f) {
                    floatingDebugView = FloatingDebugView.this;
                    displayMetrics = floatingDebugView.getResources().getDisplayMetrics();
                    f3 = 2.14748365E9f;
                } else {
                    floatingDebugView = FloatingDebugView.this;
                    displayMetrics = floatingDebugView.getResources().getDisplayMetrics();
                    f3 = -2.14748365E9f;
                }
                spring.setFinalPosition(floatingDebugView.clampX(displayMetrics, f3));
                SpringForce spring2 = FloatingDebugView.this.fabYSpring.getSpring();
                FloatingDebugView floatingDebugView2 = FloatingDebugView.this;
                spring2.setFinalPosition(floatingDebugView2.clampY(floatingDebugView2.getResources().getDisplayMetrics(), FloatingDebugView.this.fabYSpring.getSpring().getFinalPosition() + (f2 / 10.0f)));
                FloatingDebugView.this.fabXSpring.start();
                FloatingDebugView.this.fabYSpring.start();
                return FloatingDebugView.this.isFromFling = true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (!FloatingDebugView.this.inLongPress) {
                    AndroidUtilities.cancelRunOnUIThread(FloatingDebugView.this.onLongPress);
                }
                if (!FloatingDebugView.this.isScrolling && !FloatingDebugView.this.isScrollDisallowed) {
                    if (Math.abs(f) >= FloatingDebugView.this.touchSlop || Math.abs(f2) >= FloatingDebugView.this.touchSlop) {
                        this.startX = FloatingDebugView.this.fabXSpring.getSpring().getFinalPosition();
                        this.startY = FloatingDebugView.this.fabYSpring.getSpring().getFinalPosition();
                        FloatingDebugView.this.isScrolling = true;
                    } else {
                        FloatingDebugView.this.isScrollDisallowed = false;
                    }
                }
                if (FloatingDebugView.this.isScrolling && !FloatingDebugView.this.inLongPress) {
                    FloatingDebugView.this.fabXSpring.getSpring().setFinalPosition((this.startX + motionEvent2.getRawX()) - motionEvent.getRawX());
                    FloatingDebugView.this.fabYSpring.getSpring().setFinalPosition((this.startY + motionEvent2.getRawY()) - motionEvent.getRawY());
                    FloatingDebugView.this.fabXSpring.start();
                    FloatingDebugView.this.fabYSpring.start();
                }
                return FloatingDebugView.this.isScrolling;
            }
        };
        this.mPrefs = context.getSharedPreferences("floating_debug", 0);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        final GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(context, this.gestureListener);
        gestureDetectorCompat.setIsLongpressEnabled(false);
        this.floatingButtonContainer = new FrameLayout(context) { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView.2
            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                FloatingDebugView.this.invalidate();
            }

            @Override // android.view.View
            public void setTranslationX(float f) {
                super.setTranslationX(f);
                FloatingDebugView.this.invalidate();
            }

            @Override // android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                FloatingDebugView.this.invalidate();
            }

            @Override // android.view.View
            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                FloatingDebugView floatingDebugView;
                DisplayMetrics displayMetrics;
                float f;
                boolean onTouchEvent = gestureDetectorCompat.onTouchEvent(motionEvent);
                if (motionEvent.getAction() == 0) {
                    AndroidUtilities.runOnUIThread(FloatingDebugView.this.onLongPress, 200L);
                } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    AndroidUtilities.cancelRunOnUIThread(FloatingDebugView.this.onLongPress);
                    if (!FloatingDebugView.this.isFromFling) {
                        SpringForce spring = FloatingDebugView.this.fabXSpring.getSpring();
                        if (FloatingDebugView.this.fabXSpring.getSpring().getFinalPosition() >= getWidth() / 2.0f) {
                            floatingDebugView = FloatingDebugView.this;
                            displayMetrics = getResources().getDisplayMetrics();
                            f = 2.14748365E9f;
                        } else {
                            floatingDebugView = FloatingDebugView.this;
                            displayMetrics = getResources().getDisplayMetrics();
                            f = -2.14748365E9f;
                        }
                        spring.setFinalPosition(floatingDebugView.clampX(displayMetrics, f));
                        FloatingDebugView.this.fabYSpring.getSpring().setFinalPosition(FloatingDebugView.this.clampY(getResources().getDisplayMetrics(), FloatingDebugView.this.fabYSpring.getSpring().getFinalPosition()));
                        FloatingDebugView.this.fabXSpring.start();
                        FloatingDebugView.this.fabYSpring.start();
                    }
                    FloatingDebugView.this.inLongPress = false;
                    FloatingDebugView.this.isScrolling = false;
                    FloatingDebugView.this.isScrollDisallowed = false;
                    FloatingDebugView.this.isFromFling = false;
                }
                return onTouchEvent;
            }
        };
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.device_phone_android);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.SRC_IN));
        this.floatingButtonContainer.addView(imageView);
        this.floatingButtonContainer.setVisibility(8);
        addView(this.floatingButtonContainer, LayoutHelper.createFrame(56, 56.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.bigLayout = linearLayout;
        linearLayout.setOrientation(1);
        this.bigLayout.setVisibility(8);
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextSize(1, 20.0f);
        this.titleView.setText(LocaleController.getString(R.string.DebugMenu));
        this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleView.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(19.0f));
        this.bigLayout.addView(this.titleView, LayoutHelper.createLinear(-1, -2));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        this.listView.setAdapter(new RecyclerListView.SelectionAdapter() { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView.3
            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return FloatingDebugController.DebugItemType.values()[viewHolder.getItemViewType()] == FloatingDebugController.DebugItemType.SIMPLE;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            /* renamed from: onCreateViewHolder */
            public RecyclerView.ViewHolder moNUMonCreateViewHolder(ViewGroup viewGroup, int i) {
                View headerCell;
                int i2 = AnonymousClass4.$SwitchMap$org$telegram$ui$Components$FloatingDebug$FloatingDebugController$DebugItemType[FloatingDebugController.DebugItemType.values()[i].ordinal()];
                if (i2 == 2) {
                    headerCell = new HeaderCell(context);
                } else if (i2 != 3) {
                    headerCell = new AlertDialog.AlertDialogCell(context, null);
                } else {
                    headerCell = new SeekBarCell(FloatingDebugView.this, context);
                }
                headerCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(headerCell);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                FloatingDebugController.DebugItem debugItem = (FloatingDebugController.DebugItem) FloatingDebugView.this.debugItems.get(i);
                int i2 = AnonymousClass4.$SwitchMap$org$telegram$ui$Components$FloatingDebug$FloatingDebugController$DebugItemType[debugItem.type.ordinal()];
                if (i2 == 1) {
                    AlertDialog.AlertDialogCell alertDialogCell = (AlertDialog.AlertDialogCell) viewHolder.itemView;
                    alertDialogCell.setTextColor(Theme.getColor("dialogTextBlack"));
                    alertDialogCell.setTextAndIcon(debugItem.title, 0);
                } else if (i2 == 2) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    headerCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
                    headerCell.setText(debugItem.title);
                } else if (i2 != 3) {
                } else {
                    SeekBarCell seekBarCell = (SeekBarCell) viewHolder.itemView;
                    seekBarCell.title = debugItem.title.toString();
                    seekBarCell.value = debugItem.floatProperty.get(null).floatValue();
                    seekBarCell.min = debugItem.from;
                    seekBarCell.max = debugItem.to;
                    seekBarCell.callback = debugItem.floatProperty;
                    seekBarCell.invalidate();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i) {
                return ((FloatingDebugController.DebugItem) FloatingDebugView.this.debugItems.get(i)).type.ordinal();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return FloatingDebugView.this.debugItems.size();
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                FloatingDebugView.this.lambda$new$1(view, i);
            }
        });
        this.bigLayout.addView(this.listView, LayoutHelper.createLinear(-1, 0, 1.0f));
        addView(this.bigLayout, LayoutHelper.createFrame(-1, -1.0f, 0, 8.0f, 8.0f, 8.0f, 8.0f));
        updateDrawables();
        setFitsSystemWindows(true);
        setWillNotDraw(false);
    }

    /* renamed from: org.telegram.ui.Components.FloatingDebug.FloatingDebugView$4  reason: invalid class name */
    /* loaded from: classes3.dex */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$FloatingDebug$FloatingDebugController$DebugItemType;

        static {
            int[] iArr = new int[FloatingDebugController.DebugItemType.values().length];
            $SwitchMap$org$telegram$ui$Components$FloatingDebug$FloatingDebugController$DebugItemType = iArr;
            try {
                iArr[FloatingDebugController.DebugItemType.SIMPLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$FloatingDebug$FloatingDebugController$DebugItemType[FloatingDebugController.DebugItemType.HEADER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$FloatingDebug$FloatingDebugController$DebugItemType[FloatingDebugController.DebugItemType.SEEKBAR.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view, int i) {
        Runnable runnable = this.debugItems.get(i).action;
        if (runnable != null) {
            runnable.run();
            showBigMenu(false);
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        LinearLayout linearLayout = this.bigLayout;
        if (view == linearLayout) {
            canvas.drawColor(Color.argb((int) (linearLayout.getAlpha() * 122.0f), 0, 0, 0));
        }
        return super.drawChild(canvas, view, j);
    }

    public boolean onBackPressed() {
        if (this.isBigMenuShown) {
            showBigMenu(false);
            return true;
        }
        return false;
    }

    @SuppressLint({"ApplySharedPref"})
    public void saveConfig() {
        this.mPrefs.edit().putFloat("x", this.fabXSpring.getSpring().getFinalPosition()).putFloat("y", this.fabYSpring.getSpring().getFinalPosition()).commit();
    }

    private void updateDrawables() {
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        Drawable mutate = getResources().getDrawable(R.drawable.floating_shadow).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
        combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        this.floatingButtonBackground = combinedDrawable;
        Drawable drawable = getResources().getDrawable(R.drawable.popup_fixed_alert3);
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
        this.bigLayout.setBackground(drawable);
        this.titleView.setTextColor(Theme.getColor("dialogTextBlack"));
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.isBigMenuShown;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    @SuppressLint({"NotifyDataSetChanged"})
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didSetNewTheme) {
            updateDrawables();
            this.listView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        float f = this.mPrefs.getFloat("x", -1.0f);
        float f2 = this.mPrefs.getFloat("y", -1.0f);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.floatingButtonContainer.setTranslationX((f == -1.0f || f >= ((float) displayMetrics.widthPixels) / 2.0f) ? clampX(displayMetrics, 2.14748365E9f) : clampX(displayMetrics, -2.14748365E9f));
        this.floatingButtonContainer.setTranslationY(f2 == -1.0f ? clampY(displayMetrics, 2.14748365E9f) : clampY(displayMetrics, f2));
        FrameLayout frameLayout = this.floatingButtonContainer;
        this.fabXSpring = new SpringAnimation(frameLayout, DynamicAnimation.TRANSLATION_X, frameLayout.getTranslationX()).setSpring(new SpringForce(this.floatingButtonContainer.getTranslationX()).setStiffness(650.0f).setDampingRatio(0.75f));
        FrameLayout frameLayout2 = this.floatingButtonContainer;
        this.fabYSpring = new SpringAnimation(frameLayout2, DynamicAnimation.TRANSLATION_Y, frameLayout2.getTranslationY()).setSpring(new SpringForce(this.floatingButtonContainer.getTranslationY()).setStiffness(650.0f).setDampingRatio(0.75f));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.fabXSpring.cancel();
        this.fabYSpring.cancel();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void showBigMenu(final boolean z) {
        if (this.isBigMenuShown == z) {
            return;
        }
        this.isBigMenuShown = z;
        if (z) {
            this.bigLayout.setVisibility(0);
            this.debugItems.clear();
            if (getContext() instanceof LaunchActivity) {
                INavigationLayout actionBarLayout = ((LaunchActivity) getContext()).getActionBarLayout();
                if (actionBarLayout instanceof FloatingDebugProvider) {
                    this.debugItems.addAll(((FloatingDebugProvider) actionBarLayout).onGetDebugItems());
                }
                INavigationLayout rightActionBarLayout = ((LaunchActivity) getContext()).getRightActionBarLayout();
                if (rightActionBarLayout instanceof FloatingDebugProvider) {
                    this.debugItems.addAll(((FloatingDebugProvider) rightActionBarLayout).onGetDebugItems());
                }
                INavigationLayout layersActionBarLayout = ((LaunchActivity) getContext()).getLayersActionBarLayout();
                if (layersActionBarLayout instanceof FloatingDebugProvider) {
                    this.debugItems.addAll(((FloatingDebugProvider) layersActionBarLayout).onGetDebugItems());
                }
            }
            this.debugItems.addAll(getBuiltInDebugItems());
            this.listView.getAdapter().notifyDataSetChanged();
        }
        final Window window = ((Activity) getContext()).getWindow();
        if (z && Build.VERSION.SDK_INT >= 21) {
            this.wasStatusBar = window.getStatusBarColor();
        }
        final float translationX = this.floatingButtonContainer.getTranslationX();
        final float translationY = this.floatingButtonContainer.getTranslationY();
        float f = 0.0f;
        SpringAnimation springAnimation = new SpringAnimation(new FloatValueHolder(z ? 0.0f : 1000.0f));
        SpringForce dampingRatio = new SpringForce(1000.0f).setStiffness(900.0f).setDampingRatio(1.0f);
        if (z) {
            f = 1000.0f;
        }
        springAnimation.setSpring(dampingRatio.setFinalPosition(f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView$$ExternalSyntheticLambda2
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f2, float f3) {
                FloatingDebugView.this.lambda$showBigMenu$2(translationX, translationY, window, dynamicAnimation, f2, f3);
            }
        }).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView$$ExternalSyntheticLambda0
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f2, float f3) {
                FloatingDebugView.this.lambda$showBigMenu$3(translationX, translationY, z, dynamicAnimation, z2, f2, f3);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showBigMenu$2(float f, float f2, Window window, DynamicAnimation dynamicAnimation, float f3, float f4) {
        float f5 = f3 / 1000.0f;
        this.bigLayout.setAlpha(f5);
        this.bigLayout.setTranslationX(AndroidUtilities.lerp(f - AndroidUtilities.dp(8.0f), 0.0f, f5));
        this.bigLayout.setTranslationY(AndroidUtilities.lerp(f2 - AndroidUtilities.dp(8.0f), 0.0f, f5));
        this.bigLayout.setPivotX(this.floatingButtonContainer.getTranslationX() + AndroidUtilities.dp(28.0f));
        this.bigLayout.setPivotY(this.floatingButtonContainer.getTranslationY() + AndroidUtilities.dp(28.0f));
        if (this.bigLayout.getWidth() != 0) {
            this.bigLayout.setScaleX(AndroidUtilities.lerp(this.floatingButtonContainer.getWidth() / this.bigLayout.getWidth(), 1.0f, f5));
        }
        if (this.bigLayout.getHeight() != 0) {
            this.bigLayout.setScaleY(AndroidUtilities.lerp(this.floatingButtonContainer.getHeight() / this.bigLayout.getHeight(), 1.0f, f5));
        }
        this.floatingButtonContainer.setTranslationX(AndroidUtilities.lerp(f, (getWidth() / 2.0f) - AndroidUtilities.dp(28.0f), f5));
        this.floatingButtonContainer.setTranslationY(AndroidUtilities.lerp(f2, (getHeight() / 2.0f) - AndroidUtilities.dp(28.0f), f5));
        this.floatingButtonContainer.setAlpha(1.0f - f5);
        if (Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ColorUtils.blendARGB(this.wasStatusBar, NUM, f5));
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showBigMenu$3(float f, float f2, boolean z, DynamicAnimation dynamicAnimation, boolean z2, float f3, float f4) {
        this.floatingButtonContainer.setTranslationX(f);
        this.floatingButtonContainer.setTranslationY(f2);
        if (!z) {
            this.bigLayout.setVisibility(8);
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.fabXSpring.cancel();
        this.fabYSpring.cancel();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        FrameLayout frameLayout = this.floatingButtonContainer;
        frameLayout.setTranslationX(clampX(displayMetrics, frameLayout.getTranslationX() >= ((float) displayMetrics.widthPixels) / 2.0f ? 2.14748365E9f : -2.14748365E9f));
        FrameLayout frameLayout2 = this.floatingButtonContainer;
        frameLayout2.setTranslationY(clampY(displayMetrics, frameLayout2.getTranslationY()));
        this.fabXSpring.getSpring().setFinalPosition(this.floatingButtonContainer.getTranslationX());
        this.fabYSpring.getSpring().setFinalPosition(this.floatingButtonContainer.getTranslationY());
    }

    private List<FloatingDebugController.DebugItem> getBuiltInDebugItems() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FloatingDebugController.DebugItem(LocaleController.getString(R.string.DebugGeneral)));
        if (Build.VERSION.SDK_INT >= 19) {
            arrayList.add(new FloatingDebugController.DebugItem(LocaleController.getString(SharedConfig.debugWebView ? R.string.DebugMenuDisableWebViewDebug : R.string.DebugMenuEnableWebViewDebug), new Runnable() { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    FloatingDebugView.this.lambda$getBuiltInDebugItems$4();
                }
            }));
        }
        arrayList.add(new FloatingDebugController.DebugItem(LocaleController.getString(SharedConfig.useLNavigation ? R.string.AltNavigationDisable : R.string.AltNavigationEnable), new Runnable() { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                FloatingDebugView.this.lambda$getBuiltInDebugItems$5();
            }
        }));
        arrayList.add(new FloatingDebugController.DebugItem(Theme.isCurrentThemeDark() ? "Switch to day theme" : "Switch to dark theme", FloatingDebugView$$ExternalSyntheticLambda7.INSTANCE));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getBuiltInDebugItems$4() {
        SharedConfig.toggleDebugWebView();
        Toast.makeText(getContext(), LocaleController.getString(SharedConfig.debugWebView ? R.string.DebugMenuWebViewDebugEnabled : R.string.DebugMenuWebViewDebugDisabled), 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getBuiltInDebugItems$5() {
        SharedConfig.useLNavigation = !SharedConfig.useLNavigation;
        SharedConfig.saveConfig();
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).recreate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0063  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0068  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$getBuiltInDebugItems$7() {
        /*
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r1 = "themeconfig"
            r2 = 0
            android.content.SharedPreferences r0 = r0.getSharedPreferences(r1, r2)
            java.lang.String r1 = "lastDayTheme"
            java.lang.String r2 = "Blue"
            java.lang.String r1 = r0.getString(r1, r2)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r1)
            if (r3 == 0) goto L21
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r1)
            boolean r3 = r3.isDark()
            if (r3 == 0) goto L22
        L21:
            r1 = r2
        L22:
            java.lang.String r3 = "lastDarkTheme"
            java.lang.String r4 = "Dark Blue"
            java.lang.String r0 = r0.getString(r3, r4)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
            if (r3 == 0) goto L3a
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
            boolean r3 = r3.isDark()
            if (r3 != 0) goto L3b
        L3a:
            r0 = r4
        L3b:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r5 = r1.equals(r0)
            if (r5 == 0) goto L5b
            boolean r3 = r3.isDark()
            if (r3 != 0) goto L59
            boolean r3 = r1.equals(r4)
            if (r3 != 0) goto L59
            java.lang.String r3 = "Night"
            boolean r3 = r1.equals(r3)
            if (r3 == 0) goto L5c
        L59:
            r4 = r0
            goto L5d
        L5b:
            r4 = r0
        L5c:
            r2 = r1
        L5d:
            boolean r0 = org.telegram.ui.ActionBar.Theme.isCurrentThemeDark()
            if (r0 != 0) goto L68
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r4)
            goto L6c
        L68:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r2)
        L6c:
            org.telegram.ui.Components.FloatingDebug.FloatingDebugView$$ExternalSyntheticLambda3 r1 = new org.telegram.ui.Components.FloatingDebug.FloatingDebugView$$ExternalSyntheticLambda3
            r1.<init>()
            r2 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FloatingDebug.FloatingDebugView.lambda$getBuiltInDebugItems$7():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getBuiltInDebugItems$6(Theme.ThemeInfo themeInfo) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.TRUE, null, -1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float clampX(DisplayMetrics displayMetrics, float f) {
        return MathUtils.clamp(f, AndroidUtilities.dp(16.0f), displayMetrics.widthPixels - AndroidUtilities.dp(72.0f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float clampY(DisplayMetrics displayMetrics, float f) {
        return MathUtils.clamp(f, AndroidUtilities.dp(16.0f), displayMetrics.heightPixels - AndroidUtilities.dp(72.0f));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(this.floatingButtonContainer.getTranslationX(), this.floatingButtonContainer.getTranslationY());
        canvas.scale(this.floatingButtonContainer.getScaleX(), this.floatingButtonContainer.getScaleY(), this.floatingButtonContainer.getPivotX(), this.floatingButtonContainer.getPivotY());
        this.floatingButtonBackground.setAlpha((int) (this.floatingButtonContainer.getAlpha() * 255.0f));
        this.floatingButtonBackground.setBounds(this.floatingButtonContainer.getLeft(), this.floatingButtonContainer.getTop(), this.floatingButtonContainer.getRight(), this.floatingButtonContainer.getBottom());
        this.floatingButtonBackground.draw(canvas);
        canvas.restore();
    }

    public void showFab() {
        this.floatingButtonContainer.setVisibility(0);
        new SpringAnimation(new FloatValueHolder(0.0f)).setSpring(new SpringForce(1000.0f).setStiffness(750.0f).setDampingRatio(0.75f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView$$ExternalSyntheticLambda1
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                FloatingDebugView.this.lambda$showFab$8(dynamicAnimation, f, f2);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFab$8(DynamicAnimation dynamicAnimation, float f, float f2) {
        float f3 = f / 1000.0f;
        this.floatingButtonContainer.setPivotX(AndroidUtilities.dp(28.0f));
        this.floatingButtonContainer.setPivotY(AndroidUtilities.dp(28.0f));
        this.floatingButtonContainer.setScaleX(f3);
        this.floatingButtonContainer.setScaleY(f3);
        this.floatingButtonContainer.setAlpha(MathUtils.clamp(f3, 0.0f, 1.0f));
        invalidate();
    }

    public void dismiss(Runnable runnable) {
        runnable.run();
    }

    /* loaded from: classes3.dex */
    private class SeekBarCell extends FrameLayout {
        private AnimationProperties.FloatProperty callback;
        private int lastWidth;
        private float max;
        private float min;
        private SeekBarView seekBar;
        private TextPaint textPaint;
        private String title;
        private float value;

        public SeekBarCell(FloatingDebugView floatingDebugView, Context context) {
            super(context);
            setWillNotDraw(false);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.seekBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.seekBar.setDelegate(new SeekBarView.SeekBarViewDelegate(floatingDebugView) { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugView.SeekBarCell.1
                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public /* synthetic */ int getStepsCount() {
                    return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarPressed(boolean z) {
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarDrag(boolean z, float f) {
                    SeekBarCell seekBarCell = SeekBarCell.this;
                    seekBarCell.value = seekBarCell.min + ((SeekBarCell.this.max - SeekBarCell.this.min) * f);
                    if (z) {
                        SeekBarCell.this.callback.set2((AnimationProperties.FloatProperty) null, Float.valueOf(SeekBarCell.this.value));
                    }
                    SeekBarCell.this.invalidate();
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(SeekBarCell.this.min + ((SeekBarCell.this.max - SeekBarCell.this.min) * SeekBarCell.this.seekBar.getProgress())));
                }
            });
            this.seekBar.setImportantForAccessibility(2);
            addView(this.seekBar, LayoutHelper.createFrame(-1, 38.0f, 83, 5.0f, 29.0f, 47.0f, 0.0f));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.drawText(this.title, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), this.textPaint);
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            String format = String.format(Locale.ROOT, "%.2f", Float.valueOf(this.value));
            canvas.drawText(format, (getMeasuredWidth() - AndroidUtilities.dp(8.0f)) - this.textPaint.measureText(format), AndroidUtilities.dp(23.0f) + this.seekBar.getY(), this.textPaint);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            int size = View.MeasureSpec.getSize(i);
            if (this.lastWidth != size) {
                SeekBarView seekBarView = this.seekBar;
                float floatValue = this.callback.get(null).floatValue();
                float f = this.min;
                seekBarView.setProgress((floatValue - f) / (this.max - f));
                this.lastWidth = size;
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            this.seekBar.invalidate();
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.seekBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
        }

        @Override // android.view.View
        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return super.performAccessibilityAction(i, bundle) || this.seekBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, i, bundle);
        }
    }
}
