package org.telegram.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.ShareLocationDrawable;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

@TargetApi(23)
public class ActionIntroActivity extends BaseFragment implements LocationController.LocationFetchCallback {
    /* access modifiers changed from: private */
    public TextView buttonTextView;
    private int[] colors;
    private String currentGroupCreateAddress;
    private String currentGroupCreateDisplayAddress;
    private Location currentGroupCreateLocation;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public LinearLayout descriptionLayout;
    /* access modifiers changed from: private */
    public TextView descriptionText;
    /* access modifiers changed from: private */
    public TextView descriptionText2;
    private TextView[] desctiptionLines = new TextView[6];
    private Drawable drawable1;
    private Drawable drawable2;
    /* access modifiers changed from: private */
    public boolean flickerButton;
    /* access modifiers changed from: private */
    public RLottieImageView imageView;
    /* access modifiers changed from: private */
    public ActionIntroQRLoginDelegate qrLoginDelegate;
    /* access modifiers changed from: private */
    public boolean showingAsBottomSheet;
    /* access modifiers changed from: private */
    public TextView subtitleTextView;
    /* access modifiers changed from: private */
    public TextView titleTextView;

    public interface ActionIntroQRLoginDelegate {
        void didFindQRCode(String str);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean hasForceLightStatusBar() {
        return true;
    }

    public ActionIntroActivity(int i) {
        this.currentType = i;
    }

    public View createView(Context context) {
        int i;
        Context context2 = context;
        ActionBar actionBar = this.actionBar;
        int i2 = 0;
        if (actionBar != null) {
            actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.actionBar.setBackButtonImage(NUM);
            this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
            this.actionBar.setCastShadows(false);
            this.actionBar.setAddToContainer(false);
            if (!AndroidUtilities.isTablet()) {
                this.actionBar.showActionModeTop();
            }
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int i) {
                    if (i == -1) {
                        ActionIntroActivity.this.finishFragment();
                    }
                }
            });
        }
        AnonymousClass2 r2 = new ViewGroup(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                if (ActionIntroActivity.this.actionBar != null) {
                    ActionIntroActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), i2);
                }
                switch (ActionIntroActivity.this.currentType) {
                    case 0:
                        if (size <= size2) {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.399f), NUM));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(86.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            break;
                        } else {
                            float f = (float) size;
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (0.45f * f), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.68f), NUM));
                            int i3 = (int) (f * 0.6f);
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            break;
                        }
                    case 1:
                    case 4:
                    case 6:
                        if (ActionIntroActivity.this.currentType == 6) {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(140.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(140.0f), NUM));
                        } else {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
                        }
                        if (size <= size2) {
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            if (ActionIntroActivity.this.currentType != 6) {
                                ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.6f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                                break;
                            } else {
                                ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(48.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
                                break;
                            }
                        } else {
                            int i4 = (int) (((float) size) * 0.6f);
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i4, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i4, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i4, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            break;
                        }
                    case 2:
                        if (size <= size2) {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.44f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            break;
                        } else {
                            float f2 = (float) size;
                            int i5 = (int) (0.45f * f2);
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(i5, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.78f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(i5, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            int i6 = (int) (f2 * 0.6f);
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i6, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i6, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(i6, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i6, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            break;
                        }
                    case 3:
                        if (size <= size2) {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.44f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            break;
                        } else {
                            float f3 = (float) size;
                            int i7 = (int) (0.45f * f3);
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(i7, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.78f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(i7, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            int i8 = (int) (f3 * 0.6f);
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i8, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i8, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i8, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            break;
                        }
                    case 5:
                        if (!ActionIntroActivity.this.showingAsBottomSheet) {
                            if (size <= size2) {
                                ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.399f), NUM));
                                ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                ActionIntroActivity.this.descriptionLayout.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                                break;
                            } else {
                                float f4 = (float) size;
                                ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (0.45f * f4), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.68f), NUM));
                                int i9 = (int) (f4 * 0.6f);
                                ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i9, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                ActionIntroActivity.this.descriptionLayout.measure(View.MeasureSpec.makeMeasureSpec(i9, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i9, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                                break;
                            }
                        } else {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.32f), NUM));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionLayout.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            size2 = ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + ActionIntroActivity.this.imageView.getMeasuredHeight() + ActionIntroActivity.this.titleTextView.getMeasuredHeight() + AndroidUtilities.dp(20.0f) + ActionIntroActivity.this.titleTextView.getMeasuredHeight() + ActionIntroActivity.this.descriptionLayout.getMeasuredHeight();
                            break;
                        }
                }
                setMeasuredDimension(size, size2);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                float f;
                int i5 = i3;
                int i6 = i4;
                if (ActionIntroActivity.this.actionBar != null) {
                    ActionIntroActivity.this.actionBar.layout(0, 0, i5, ActionIntroActivity.this.actionBar.getMeasuredHeight());
                }
                int i7 = i5 - i;
                int i8 = i6 - i2;
                switch (ActionIntroActivity.this.currentType) {
                    case 0:
                        if (i5 > i6) {
                            int measuredHeight = (i8 - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                            ActionIntroActivity.this.imageView.layout(0, measuredHeight, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight);
                            float f2 = (float) i7;
                            float f3 = 0.4f * f2;
                            int i9 = (int) f3;
                            float f4 = (float) i8;
                            int i10 = (int) (0.22f * f4);
                            ActionIntroActivity.this.titleTextView.layout(i9, i10, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i9, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i10);
                            int i11 = (int) (0.39f * f4);
                            ActionIntroActivity.this.descriptionText.layout(i9, i11, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i9, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i11);
                            int measuredWidth = (int) (f3 + (((f2 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int i12 = (int) (f4 * 0.69f);
                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth, i12, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i12);
                            return;
                        }
                        float f5 = (float) i8;
                        int i13 = (int) (0.188f * f5);
                        ActionIntroActivity.this.imageView.layout(0, i13, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i13);
                        int i14 = (int) (0.651f * f5);
                        ActionIntroActivity.this.titleTextView.layout(0, i14, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i14);
                        int i15 = (int) (0.731f * f5);
                        ActionIntroActivity.this.descriptionText.layout(0, i15, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i15);
                        int measuredWidth2 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int i16 = (int) (f5 * 0.853f);
                        ActionIntroActivity.this.buttonTextView.layout(measuredWidth2, i16, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth2, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i16);
                        return;
                    case 1:
                    case 4:
                        if (i5 > i6) {
                            int measuredHeight2 = (i8 - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                            float f6 = (float) i7;
                            int measuredWidth3 = ((int) ((0.5f * f6) - ((float) ActionIntroActivity.this.imageView.getMeasuredWidth()))) / 2;
                            ActionIntroActivity.this.imageView.layout(measuredWidth3, measuredHeight2, ActionIntroActivity.this.imageView.getMeasuredWidth() + measuredWidth3, ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight2);
                            float f7 = 0.4f * f6;
                            int i17 = (int) f7;
                            float f8 = (float) i8;
                            int i18 = (int) (0.14f * f8);
                            ActionIntroActivity.this.titleTextView.layout(i17, i18, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i17, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i18);
                            int i19 = (int) (0.31f * f8);
                            ActionIntroActivity.this.descriptionText.layout(i17, i19, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i17, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i19);
                            int measuredWidth4 = (int) (f7 + (((f6 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int i20 = (int) (f8 * 0.78f);
                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth4, i20, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth4, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i20);
                            return;
                        }
                        float f9 = (float) i8;
                        int i21 = (int) (0.214f * f9);
                        int measuredWidth5 = (i7 - ActionIntroActivity.this.imageView.getMeasuredWidth()) / 2;
                        ActionIntroActivity.this.imageView.layout(measuredWidth5, i21, ActionIntroActivity.this.imageView.getMeasuredWidth() + measuredWidth5, ActionIntroActivity.this.imageView.getMeasuredHeight() + i21);
                        int i22 = (int) (0.414f * f9);
                        ActionIntroActivity.this.titleTextView.layout(0, i22, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i22);
                        int i23 = (int) (0.493f * f9);
                        ActionIntroActivity.this.descriptionText.layout(0, i23, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i23);
                        int measuredWidth6 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int i24 = (int) (f9 * 0.71f);
                        ActionIntroActivity.this.buttonTextView.layout(measuredWidth6, i24, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth6, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i24);
                        return;
                    case 2:
                        if (i5 > i6) {
                            float var_ = (float) i8;
                            int measuredHeight3 = ((int) ((0.9f * var_) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                            ActionIntroActivity.this.imageView.layout(0, measuredHeight3, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight3);
                            int measuredHeight4 = measuredHeight3 + ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, measuredHeight4, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + measuredHeight4);
                            float var_ = (float) i7;
                            float var_ = 0.4f * var_;
                            int i25 = (int) var_;
                            int i26 = (int) (0.12f * var_);
                            ActionIntroActivity.this.titleTextView.layout(i25, i26, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i25, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i26);
                            int i27 = (int) (0.26f * var_);
                            ActionIntroActivity.this.descriptionText.layout(i25, i27, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i25, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i27);
                            int measuredWidth7 = (int) (var_ + (((var_ * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int i28 = (int) (var_ * 0.6f);
                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth7, i28, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth7, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i28);
                            int measuredHeight5 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(i25, measuredHeight5, ActionIntroActivity.this.descriptionText2.getMeasuredWidth() + i25, ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + measuredHeight5);
                            return;
                        }
                        float var_ = (float) i8;
                        int i29 = (int) (0.197f * var_);
                        ActionIntroActivity.this.imageView.layout(0, i29, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i29);
                        int i30 = (int) (0.421f * var_);
                        ActionIntroActivity.this.titleTextView.layout(0, i30, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i30);
                        int i31 = (int) (0.477f * var_);
                        ActionIntroActivity.this.subtitleTextView.layout(0, i31, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i31);
                        int i32 = (int) (0.537f * var_);
                        ActionIntroActivity.this.descriptionText.layout(0, i32, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i32);
                        int measuredWidth8 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int i33 = (int) (var_ * 0.71f);
                        ActionIntroActivity.this.buttonTextView.layout(measuredWidth8, i33, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth8, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i33);
                        int measuredHeight6 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                        ActionIntroActivity.this.descriptionText2.layout(0, measuredHeight6, ActionIntroActivity.this.descriptionText2.getMeasuredWidth(), ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + measuredHeight6);
                        return;
                    case 3:
                        if (i5 > i6) {
                            float var_ = (float) i8;
                            int measuredHeight7 = ((int) ((0.95f * var_) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                            ActionIntroActivity.this.imageView.layout(0, measuredHeight7, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight7);
                            int measuredHeight8 = measuredHeight7 + ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, measuredHeight8, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + measuredHeight8);
                            float var_ = (float) i7;
                            float var_ = 0.4f * var_;
                            int i34 = (int) var_;
                            int i35 = (int) (0.12f * var_);
                            ActionIntroActivity.this.titleTextView.layout(i34, i35, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i34, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i35);
                            int i36 = (int) (0.24f * var_);
                            ActionIntroActivity.this.descriptionText.layout(i34, i36, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i34, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i36);
                            int measuredWidth9 = (int) (var_ + (((var_ * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int i37 = (int) (var_ * 0.8f);
                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth9, i37, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth9, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i37);
                            return;
                        }
                        float var_ = (float) i8;
                        int i38 = (int) (0.2229f * var_);
                        ActionIntroActivity.this.imageView.layout(0, i38, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i38);
                        int i39 = (int) (0.352f * var_);
                        ActionIntroActivity.this.titleTextView.layout(0, i39, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i39);
                        int i40 = (int) (0.409f * var_);
                        ActionIntroActivity.this.subtitleTextView.layout(0, i40, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i40);
                        int i41 = (int) (0.468f * var_);
                        ActionIntroActivity.this.descriptionText.layout(0, i41, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i41);
                        int measuredWidth10 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int i42 = (int) (var_ * 0.805f);
                        ActionIntroActivity.this.buttonTextView.layout(measuredWidth10, i42, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth10, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i42);
                        return;
                    case 5:
                        if (ActionIntroActivity.this.showingAsBottomSheet) {
                            ActionIntroActivity.this.imageView.layout(0, 0, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + 0);
                            float var_ = (float) i8;
                            int i43 = (int) (0.403f * var_);
                            ActionIntroActivity.this.titleTextView.layout(0, i43, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i43);
                            int i44 = (int) (0.631f * var_);
                            int measuredWidth11 = (getMeasuredWidth() - ActionIntroActivity.this.descriptionLayout.getMeasuredWidth()) / 2;
                            ActionIntroActivity.this.descriptionLayout.layout(measuredWidth11, i44, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + measuredWidth11, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + i44);
                            int measuredWidth12 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                            int i45 = (int) (var_ * 0.853f);
                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth12, i45, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth12, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i45);
                            return;
                        } else if (i5 > i6) {
                            int measuredHeight9 = (i8 - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                            ActionIntroActivity.this.imageView.layout(0, measuredHeight9, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight9);
                            float var_ = (float) i7;
                            float var_ = 0.4f * var_;
                            int i46 = (int) var_;
                            float var_ = (float) i8;
                            int i47 = (int) (0.08f * var_);
                            ActionIntroActivity.this.titleTextView.layout(i46, i47, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i46, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i47);
                            float var_ = var_ * 0.6f;
                            int measuredWidth13 = (int) (((var_ - ((float) ActionIntroActivity.this.descriptionLayout.getMeasuredWidth())) / 2.0f) + var_);
                            int i48 = (int) (0.25f * var_);
                            ActionIntroActivity.this.descriptionLayout.layout(measuredWidth13, i48, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + measuredWidth13, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + i48);
                            int measuredWidth14 = (int) (var_ + ((var_ - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int i49 = (int) (var_ * 0.78f);
                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth14, i49, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth14, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i49);
                            return;
                        } else {
                            if (AndroidUtilities.displaySize.y < 1800) {
                                float var_ = (float) i8;
                                int i50 = (int) (0.06f * var_);
                                ActionIntroActivity.this.imageView.layout(0, i50, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i50);
                                int i51 = (int) (0.463f * var_);
                                ActionIntroActivity.this.titleTextView.layout(0, i51, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i51);
                                f = var_ * 0.543f;
                            } else {
                                float var_ = (float) i8;
                                int i52 = (int) (0.148f * var_);
                                ActionIntroActivity.this.imageView.layout(0, i52, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i52);
                                int i53 = (int) (0.551f * var_);
                                ActionIntroActivity.this.titleTextView.layout(0, i53, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i53);
                                f = var_ * 0.631f;
                            }
                            int i54 = (int) f;
                            int measuredWidth15 = (getMeasuredWidth() - ActionIntroActivity.this.descriptionLayout.getMeasuredWidth()) / 2;
                            ActionIntroActivity.this.descriptionLayout.layout(measuredWidth15, i54, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + measuredWidth15, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + i54);
                            int measuredWidth16 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                            int i55 = (int) (((float) i8) * 0.853f);
                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth16, i55, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth16, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i55);
                            return;
                        }
                    case 6:
                        if (i5 > i6) {
                            int measuredHeight10 = (i8 - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                            float var_ = (float) i7;
                            int measuredWidth17 = ((int) ((0.5f * var_) - ((float) ActionIntroActivity.this.imageView.getMeasuredWidth()))) / 2;
                            ActionIntroActivity.this.imageView.layout(measuredWidth17, measuredHeight10, ActionIntroActivity.this.imageView.getMeasuredWidth() + measuredWidth17, ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight10);
                            float var_ = 0.4f * var_;
                            int i56 = (int) var_;
                            float var_ = (float) i8;
                            int i57 = (int) (0.14f * var_);
                            ActionIntroActivity.this.titleTextView.layout(i56, i57, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i56, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i57);
                            int i58 = (int) (0.31f * var_);
                            ActionIntroActivity.this.descriptionText.layout(i56, i58, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i56, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i58);
                            int measuredWidth18 = (int) (var_ + (((var_ * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int i59 = (int) (var_ * 0.78f);
                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth18, i59, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth18, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i59);
                            return;
                        }
                        int i60 = (int) (((float) i8) * 0.3f);
                        int measuredWidth19 = (i7 - ActionIntroActivity.this.imageView.getMeasuredWidth()) / 2;
                        ActionIntroActivity.this.imageView.layout(measuredWidth19, i60, ActionIntroActivity.this.imageView.getMeasuredWidth() + measuredWidth19, ActionIntroActivity.this.imageView.getMeasuredHeight() + i60);
                        int measuredHeight11 = i60 + ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(24.0f);
                        ActionIntroActivity.this.titleTextView.layout(0, measuredHeight11, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + measuredHeight11);
                        int textSize = (int) (((float) measuredHeight11) + ActionIntroActivity.this.titleTextView.getTextSize() + ((float) AndroidUtilities.dp(16.0f)));
                        ActionIntroActivity.this.descriptionText.layout(0, textSize, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + textSize);
                        int measuredWidth20 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int measuredHeight12 = (i8 - ActionIntroActivity.this.buttonTextView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f);
                        ActionIntroActivity.this.buttonTextView.layout(measuredWidth20, measuredHeight12, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth20, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + measuredHeight12);
                        return;
                    default:
                        return;
                }
            }
        };
        this.fragmentView = r2;
        r2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        viewGroup.setOnTouchListener(ActionIntroActivity$$ExternalSyntheticLambda4.INSTANCE);
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            viewGroup.addView(actionBar2);
        }
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        viewGroup.addView(rLottieImageView);
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        int i3 = 1;
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        viewGroup.addView(this.titleTextView);
        TextView textView2 = new TextView(context2);
        this.subtitleTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.subtitleTextView.setGravity(1);
        float f = 15.0f;
        this.subtitleTextView.setTextSize(1, 15.0f);
        this.subtitleTextView.setSingleLine(true);
        this.subtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        int i4 = 2;
        if (this.currentType == 2) {
            this.subtitleTextView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        } else {
            this.subtitleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        this.subtitleTextView.setVisibility(8);
        viewGroup.addView(this.subtitleTextView);
        TextView textView3 = new TextView(context2);
        this.descriptionText = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        int i5 = this.currentType;
        if (i5 == 6) {
            this.descriptionText.setPadding(AndroidUtilities.dp(48.0f), 0, AndroidUtilities.dp(48.0f), 0);
        } else if (i5 == 2) {
            this.descriptionText.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        } else {
            this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        viewGroup.addView(this.descriptionText);
        String str = "";
        if (this.currentType == 5) {
            LinearLayout linearLayout = new LinearLayout(context2);
            this.descriptionLayout = linearLayout;
            linearLayout.setOrientation(1);
            this.descriptionLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
            this.descriptionLayout.setGravity(LocaleController.isRTL ? 5 : 3);
            viewGroup.addView(this.descriptionLayout);
            int i6 = 0;
            for (int i7 = 3; i6 < i7; i7 = 3) {
                LinearLayout linearLayout2 = new LinearLayout(context2);
                linearLayout2.setOrientation(i2);
                this.descriptionLayout.addView(linearLayout2, LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 0.0f, i6 != i4 ? 7.0f : 0.0f));
                int i8 = i6 * 2;
                this.desctiptionLines[i8] = new TextView(context2);
                this.desctiptionLines[i8].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.desctiptionLines[i8].setGravity(LocaleController.isRTL ? 5 : 3);
                this.desctiptionLines[i8].setTextSize(i3, f);
                TextView textView4 = this.desctiptionLines[i8];
                String str2 = LocaleController.isRTL ? ".%d" : "%d.";
                Object[] objArr = new Object[i3];
                int i9 = i6 + 1;
                objArr[i2] = Integer.valueOf(i9);
                textView4.setText(String.format(str2, objArr));
                this.desctiptionLines[i8].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                int i10 = i8 + 1;
                this.desctiptionLines[i10] = new TextView(context2);
                this.desctiptionLines[i10].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.desctiptionLines[i10].setGravity(LocaleController.isRTL ? 5 : 3);
                this.desctiptionLines[i10].setTextSize(i3, f);
                if (i6 == 0) {
                    this.desctiptionLines[i10].setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
                    this.desctiptionLines[i10].setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
                    String string = LocaleController.getString("AuthAnotherClientInfo1", NUM);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                    int indexOf = string.indexOf(42);
                    int lastIndexOf = string.lastIndexOf(42);
                    if (!(indexOf == -1 || lastIndexOf == -1 || indexOf == lastIndexOf)) {
                        this.desctiptionLines[i10].setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 1, str);
                        spannableStringBuilder.replace(indexOf, indexOf + 1, str);
                        spannableStringBuilder.setSpan(new URLSpanNoUnderline(LocaleController.getString("AuthAnotherClientDownloadClientUrl", NUM)), indexOf, lastIndexOf - 1, 33);
                    }
                    this.desctiptionLines[i10].setText(spannableStringBuilder);
                } else if (i6 == 1) {
                    this.desctiptionLines[i10].setText(LocaleController.getString("AuthAnotherClientInfo2", NUM));
                } else {
                    this.desctiptionLines[i10].setText(LocaleController.getString("AuthAnotherClientInfo3", NUM));
                }
                if (LocaleController.isRTL) {
                    linearLayout2.setGravity(5);
                    linearLayout2.addView(this.desctiptionLines[i10], LayoutHelper.createLinear(0, -2, 1.0f));
                    linearLayout2.addView(this.desctiptionLines[i8], LayoutHelper.createLinear(-2, -2, 4.0f, 0.0f, 0.0f, 0.0f));
                } else {
                    linearLayout2.addView(this.desctiptionLines[i8], LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 4.0f, 0.0f));
                    linearLayout2.addView(this.desctiptionLines[i10], LayoutHelper.createLinear(-2, -2));
                }
                i6 = i9;
                i2 = 0;
                i3 = 1;
                f = 15.0f;
                i4 = 2;
            }
            this.descriptionText.setVisibility(8);
        }
        TextView textView5 = new TextView(context2);
        this.descriptionText2 = textView5;
        textView5.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setTextSize(1, 13.0f);
        this.descriptionText2.setVisibility(8);
        if (this.currentType == 2) {
            i = 0;
            this.descriptionText2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        } else {
            i = 0;
            this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        viewGroup.addView(this.descriptionText2);
        AnonymousClass3 r3 = new TextView(context2) {
            CellFlickerDrawable cellFlickerDrawable;

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (ActionIntroActivity.this.flickerButton) {
                    if (this.cellFlickerDrawable == null) {
                        CellFlickerDrawable cellFlickerDrawable2 = new CellFlickerDrawable();
                        this.cellFlickerDrawable = cellFlickerDrawable2;
                        cellFlickerDrawable2.drawFrame = false;
                        cellFlickerDrawable2.repeatProgress = 2.0f;
                    }
                    this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    this.cellFlickerDrawable.draw(canvas, rectF, (float) AndroidUtilities.dp(4.0f));
                    invalidate();
                }
            }
        };
        this.buttonTextView = r3;
        r3.setPadding(AndroidUtilities.dp(34.0f), i, AndroidUtilities.dp(34.0f), i);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp((float) (this.currentType == 6 ? 6 : 4)), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        viewGroup.addView(this.buttonTextView);
        this.buttonTextView.setOnClickListener(new ActionIntroActivity$$ExternalSyntheticLambda2(this));
        switch (this.currentType) {
            case 0:
                this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                this.imageView.setAnimation(NUM, 200, 200);
                this.titleTextView.setText(LocaleController.getString("ChannelAlertTitle", NUM));
                this.descriptionText.setText(LocaleController.getString("ChannelAlertText", NUM));
                this.buttonTextView.setText(LocaleController.getString("ChannelAlertCreate2", NUM));
                this.imageView.playAnimation();
                this.flickerButton = true;
                break;
            case 1:
                this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor("chats_archiveBackground")));
                this.imageView.setImageDrawable(new ShareLocationDrawable(context2, 3));
                this.imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.titleTextView.setText(LocaleController.getString("PeopleNearby", NUM));
                this.descriptionText.setText(LocaleController.getString("PeopleNearbyAccessInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("PeopleNearbyAllowAccess", NUM));
                break;
            case 2:
                this.subtitleTextView.setVisibility(0);
                this.descriptionText2.setVisibility(0);
                this.imageView.setImageResource(Theme.getCurrentTheme().isDark() ? NUM : NUM);
                this.imageView.setScaleType(ImageView.ScaleType.CENTER);
                TextView textView6 = this.subtitleTextView;
                String str3 = this.currentGroupCreateDisplayAddress;
                if (str3 != null) {
                    str = str3;
                }
                textView6.setText(str);
                this.titleTextView.setText(LocaleController.getString("NearbyCreateGroup", NUM));
                this.descriptionText.setText(LocaleController.getString("NearbyCreateGroupInfo", NUM));
                this.descriptionText2.setText(LocaleController.getString("NearbyCreateGroupInfo2", NUM));
                this.buttonTextView.setText(LocaleController.getString("NearbyStartGroup", NUM));
                break;
            case 3:
                this.subtitleTextView.setVisibility(0);
                this.drawable1 = context.getResources().getDrawable(NUM);
                this.drawable2 = context.getResources().getDrawable(NUM);
                this.drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image"), PorterDuff.Mode.MULTIPLY));
                this.drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image2"), PorterDuff.Mode.MULTIPLY));
                this.imageView.setImageDrawable(new CombinedDrawable(this.drawable1, this.drawable2));
                this.imageView.setScaleType(ImageView.ScaleType.CENTER);
                UserConfig userConfig = getUserConfig();
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(userConfig.clientUserId));
                if (user == null) {
                    user = userConfig.getCurrentUser();
                }
                if (user != null) {
                    TextView textView7 = this.subtitleTextView;
                    PhoneFormat instance = PhoneFormat.getInstance();
                    textView7.setText(instance.format("+" + user.phone));
                }
                this.titleTextView.setText(LocaleController.getString("PhoneNumberChange2", NUM));
                this.descriptionText.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", NUM)));
                this.buttonTextView.setText(LocaleController.getString("PhoneNumberChange2", NUM));
                break;
            case 4:
                this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor("chats_archiveBackground")));
                this.imageView.setImageDrawable(new ShareLocationDrawable(context2, 3));
                this.imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.titleTextView.setText(LocaleController.getString("PeopleNearby", NUM));
                this.descriptionText.setText(LocaleController.getString("PeopleNearbyGpsInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("PeopleNearbyGps", NUM));
                break;
            case 5:
                this.colors = new int[8];
                updateColors();
                this.imageView.setAnimation(NUM, 334, 334, this.colors);
                this.imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.titleTextView.setText(LocaleController.getString("AuthAnotherClient", NUM));
                this.buttonTextView.setText(LocaleController.getString("AuthAnotherClientScan", NUM));
                this.imageView.playAnimation();
                break;
            case 6:
                this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                this.imageView.setAnimation(NUM, 200, 200);
                this.imageView.setOnClickListener(new ActionIntroActivity$$ExternalSyntheticLambda3(this));
                this.titleTextView.setText(LocaleController.getString("Passcode", NUM));
                this.descriptionText.setText(LocaleController.getString("ChangePasscodeInfoShort", NUM));
                this.buttonTextView.setText(LocaleController.getString("EnablePasscode", NUM));
                this.imageView.playAnimation();
                this.flickerButton = true;
                break;
        }
        if (this.flickerButton) {
            this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(34.0f), AndroidUtilities.dp(8.0f));
            this.buttonTextView.setTextSize(1, 15.0f);
        }
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        if (getParentActivity() != null) {
            switch (this.currentType) {
                case 0:
                    Bundle bundle = new Bundle();
                    bundle.putInt("step", 0);
                    presentFragment(new ChannelCreateActivity(bundle), true);
                    return;
                case 1:
                    getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                    return;
                case 2:
                    if (this.currentGroupCreateAddress != null && this.currentGroupCreateLocation != null) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putLongArray("result", new long[]{getUserConfig().getClientUserId()});
                        bundle2.putInt("chatType", 4);
                        bundle2.putString("address", this.currentGroupCreateAddress);
                        bundle2.putParcelable("location", this.currentGroupCreateLocation);
                        presentFragment(new GroupCreateFinalActivity(bundle2), true);
                        return;
                    }
                    return;
                case 3:
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("PhoneNumberChangeTitle", NUM));
                    builder.setMessage(LocaleController.getString("PhoneNumberAlert", NUM));
                    builder.setPositiveButton(LocaleController.getString("Change", NUM), new ActionIntroActivity$$ExternalSyntheticLambda1(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                    return;
                case 4:
                    try {
                        getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                        return;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return;
                    }
                case 5:
                    if (getParentActivity() != null) {
                        if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                            processOpenQrReader();
                            return;
                        }
                        getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 34);
                        return;
                    }
                    return;
                case 6:
                    presentFragment(new PasscodeActivity(1), true);
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(DialogInterface dialogInterface, int i) {
        presentFragment(new ChangePhoneActivity(), true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        if (!this.imageView.getAnimatedDrawable().isRunning()) {
            this.imageView.getAnimatedDrawable().setCurrentFrame(0, false);
            this.imageView.playAnimation();
        }
    }

    public void onLocationAddressAvailable(String str, String str2, Location location) {
        TextView textView = this.subtitleTextView;
        if (textView != null) {
            textView.setText(str);
            this.currentGroupCreateAddress = str;
            this.currentGroupCreateDisplayAddress = str2;
            this.currentGroupCreateLocation = location;
        }
    }

    public void onResume() {
        boolean z;
        super.onResume();
        if (this.currentType == 4) {
            int i = Build.VERSION.SDK_INT;
            if (i >= 28) {
                z = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
            } else {
                if (i >= 19) {
                    try {
                        if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) == 0) {
                            z = false;
                        }
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                z = true;
            }
            if (z) {
                presentFragment(new PeopleNearbyActivity(), true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateColors() {
        int[] iArr = this.colors;
        if (iArr != null && this.imageView != null) {
            iArr[0] = 3355443;
            iArr[1] = Theme.getColor("windowBackgroundWhiteBlackText");
            int[] iArr2 = this.colors;
            iArr2[2] = 16777215;
            iArr2[3] = Theme.getColor("windowBackgroundWhite");
            int[] iArr3 = this.colors;
            iArr3[4] = 5285866;
            iArr3[5] = Theme.getColor("featuredStickers_addButton");
            int[] iArr4 = this.colors;
            iArr4[6] = 2170912;
            iArr4[7] = Theme.getColor("windowBackgroundWhite");
            this.imageView.replaceColors(this.colors);
        }
    }

    public void setGroupCreateAddress(String str, String str2, Location location) {
        this.currentGroupCreateAddress = str;
        this.currentGroupCreateDisplayAddress = str2;
        this.currentGroupCreateLocation = location;
        if (location != null && str == null) {
            LocationController.fetchLocationAddress(location, this);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (getParentActivity() != null) {
            if (i == 2) {
                if (iArr != null && iArr.length != 0) {
                    if (iArr[0] != 0) {
                        showDialog(AlertsCreator.createLocationRequiredDialog(getParentActivity(), false));
                    } else {
                        AndroidUtilities.runOnUIThread(new ActionIntroActivity$$ExternalSyntheticLambda5(this));
                    }
                }
            } else if (i != 34) {
            } else {
                if (iArr.length <= 0 || iArr[0] != 0) {
                    new AlertDialog.Builder((Context) getParentActivity()).setMessage(AndroidUtilities.replaceTags(LocaleController.getString("QRCodePermissionNoCameraWithHint", NUM))).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new ActionIntroActivity$$ExternalSyntheticLambda0(this)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), (DialogInterface.OnClickListener) null).setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground")).show();
                } else {
                    processOpenQrReader();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$4() {
        presentFragment(new PeopleNearbyActivity(), true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$5(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void setQrLoginDelegate(ActionIntroQRLoginDelegate actionIntroQRLoginDelegate) {
        this.qrLoginDelegate = actionIntroQRLoginDelegate;
    }

    private void processOpenQrReader() {
        CameraScanActivity.showAsSheet(this, false, 1, new CameraScanActivity.CameraScanActivityDelegate() {
            public /* synthetic */ void didFindMrzInfo(MrzRecognizer.Result result) {
                CameraScanActivity.CameraScanActivityDelegate.CC.$default$didFindMrzInfo(this, result);
            }

            public /* synthetic */ boolean processQr(String str, Runnable runnable) {
                return CameraScanActivity.CameraScanActivityDelegate.CC.$default$processQr(this, str, runnable);
            }

            public void didFindQr(String str) {
                ActionIntroActivity.this.finishFragment(false);
                ActionIntroActivity.this.qrLoginDelegate.didFindQRCode(str);
            }
        });
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ActionIntroActivity$$ExternalSyntheticLambda6 actionIntroActivity$$ExternalSyntheticLambda6 = new ActionIntroActivity$$ExternalSyntheticLambda6(this);
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, actionIntroActivity$$ExternalSyntheticLambda6, "windowBackgroundWhite"));
        if (this.actionBar != null) {
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarWhiteSelector"));
        }
        ActionIntroActivity$$ExternalSyntheticLambda6 actionIntroActivity$$ExternalSyntheticLambda62 = actionIntroActivity$$ExternalSyntheticLambda6;
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, actionIntroActivity$$ExternalSyntheticLambda62, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.subtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, (Class[]) null, (Paint) null, (Drawable[]) null, actionIntroActivity$$ExternalSyntheticLambda62, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription(this.desctiptionLines[0], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.desctiptionLines[1], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.desctiptionLines[1], ThemeDescription.FLAG_LINKCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription(this.desctiptionLines[2], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.desctiptionLines[3], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.desctiptionLines[4], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.desctiptionLines[5], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, new Drawable[]{this.drawable1}, (ThemeDescription.ThemeDescriptionDelegate) null, "changephoneinfo_image"));
        arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, new Drawable[]{this.drawable2}, (ThemeDescription.ThemeDescriptionDelegate) null, "changephoneinfo_image2"));
        return arrayList;
    }
}
