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
                int access$200 = ActionIntroActivity.this.currentType;
                if (access$200 != 0) {
                    if (access$200 != 1) {
                        if (access$200 != 2) {
                            if (access$200 != 3) {
                                if (access$200 != 4) {
                                    if (access$200 == 5) {
                                        if (ActionIntroActivity.this.showingAsBottomSheet) {
                                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.32f), NUM));
                                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                            ActionIntroActivity.this.descriptionLayout.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                                            size2 = ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + ActionIntroActivity.this.imageView.getMeasuredHeight() + ActionIntroActivity.this.titleTextView.getMeasuredHeight() + AndroidUtilities.dp(20.0f) + ActionIntroActivity.this.titleTextView.getMeasuredHeight() + ActionIntroActivity.this.descriptionLayout.getMeasuredHeight();
                                        } else if (size > size2) {
                                            float f = (float) size;
                                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (0.45f * f), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.68f), NUM));
                                            int i3 = (int) (f * 0.6f);
                                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                            ActionIntroActivity.this.descriptionLayout.measure(View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                                        } else {
                                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.399f), NUM));
                                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                            ActionIntroActivity.this.descriptionLayout.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                                        }
                                    }
                                }
                            } else if (size > size2) {
                                float f2 = (float) size;
                                int i4 = (int) (0.45f * f2);
                                ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(i4, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.78f), Integer.MIN_VALUE));
                                ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(i4, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                int i5 = (int) (f2 * 0.6f);
                                ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i5, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i5, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i5, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            } else {
                                ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.44f), Integer.MIN_VALUE));
                                ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                                ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            }
                        } else if (size > size2) {
                            float f3 = (float) size;
                            int i6 = (int) (0.45f * f3);
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(i6, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.78f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(i6, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            int i7 = (int) (f3 * 0.6f);
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i7, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i7, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(i7, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i7, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                        } else {
                            ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.44f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                        }
                    }
                    ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
                    if (size > size2) {
                        int i8 = (int) (((float) size) * 0.6f);
                        ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i8, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                        ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i8, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                        ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i8, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                    } else {
                        ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                        ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                        ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                    }
                } else if (size > size2) {
                    float f4 = (float) size;
                    ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (0.45f * f4), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.68f), NUM));
                    int i9 = (int) (f4 * 0.6f);
                    ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i9, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                    ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i9, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                    ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i9, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                } else {
                    ActionIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.399f), NUM));
                    ActionIntroActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                    ActionIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                    ActionIntroActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(86.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
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
                int access$200 = ActionIntroActivity.this.currentType;
                if (access$200 != 0) {
                    if (access$200 != 1) {
                        if (access$200 != 2) {
                            if (access$200 != 3) {
                                if (access$200 != 4) {
                                    if (access$200 == 5) {
                                        if (ActionIntroActivity.this.showingAsBottomSheet) {
                                            ActionIntroActivity.this.imageView.layout(0, 0, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + 0);
                                            float f2 = (float) i8;
                                            int i9 = (int) (0.403f * f2);
                                            ActionIntroActivity.this.titleTextView.layout(0, i9, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i9);
                                            int i10 = (int) (0.631f * f2);
                                            int measuredWidth = (getMeasuredWidth() - ActionIntroActivity.this.descriptionLayout.getMeasuredWidth()) / 2;
                                            ActionIntroActivity.this.descriptionLayout.layout(measuredWidth, i10, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + measuredWidth, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + i10);
                                            int measuredWidth2 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                                            int i11 = (int) (f2 * 0.853f);
                                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth2, i11, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth2, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i11);
                                            return;
                                        } else if (i5 > i6) {
                                            int measuredHeight = (i8 - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                                            ActionIntroActivity.this.imageView.layout(0, measuredHeight, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight);
                                            float f3 = (float) i7;
                                            float f4 = 0.4f * f3;
                                            int i12 = (int) f4;
                                            float f5 = (float) i8;
                                            int i13 = (int) (0.08f * f5);
                                            ActionIntroActivity.this.titleTextView.layout(i12, i13, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i12, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i13);
                                            float f6 = f3 * 0.6f;
                                            int measuredWidth3 = (int) (((f6 - ((float) ActionIntroActivity.this.descriptionLayout.getMeasuredWidth())) / 2.0f) + f4);
                                            int i14 = (int) (0.25f * f5);
                                            ActionIntroActivity.this.descriptionLayout.layout(measuredWidth3, i14, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + measuredWidth3, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + i14);
                                            int measuredWidth4 = (int) (f4 + ((f6 - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                                            int i15 = (int) (f5 * 0.78f);
                                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth4, i15, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth4, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i15);
                                            return;
                                        } else {
                                            if (AndroidUtilities.displaySize.y < 1800) {
                                                float f7 = (float) i8;
                                                int i16 = (int) (0.06f * f7);
                                                ActionIntroActivity.this.imageView.layout(0, i16, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i16);
                                                int i17 = (int) (0.463f * f7);
                                                ActionIntroActivity.this.titleTextView.layout(0, i17, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i17);
                                                f = f7 * 0.543f;
                                            } else {
                                                float f8 = (float) i8;
                                                int i18 = (int) (0.148f * f8);
                                                ActionIntroActivity.this.imageView.layout(0, i18, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i18);
                                                int i19 = (int) (0.551f * f8);
                                                ActionIntroActivity.this.titleTextView.layout(0, i19, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i19);
                                                f = f8 * 0.631f;
                                            }
                                            int i20 = (int) f;
                                            int measuredWidth5 = (getMeasuredWidth() - ActionIntroActivity.this.descriptionLayout.getMeasuredWidth()) / 2;
                                            ActionIntroActivity.this.descriptionLayout.layout(measuredWidth5, i20, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + measuredWidth5, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + i20);
                                            int measuredWidth6 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                                            int i21 = (int) (((float) i8) * 0.853f);
                                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth6, i21, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth6, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i21);
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            } else if (i5 > i6) {
                                float f9 = (float) i8;
                                int measuredHeight2 = ((int) ((0.95f * f9) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                                ActionIntroActivity.this.imageView.layout(0, measuredHeight2, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight2);
                                int measuredHeight3 = measuredHeight2 + ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                                ActionIntroActivity.this.subtitleTextView.layout(0, measuredHeight3, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + measuredHeight3);
                                float var_ = (float) i7;
                                float var_ = 0.4f * var_;
                                int i22 = (int) var_;
                                int i23 = (int) (0.12f * f9);
                                ActionIntroActivity.this.titleTextView.layout(i22, i23, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i22, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i23);
                                int i24 = (int) (0.24f * f9);
                                ActionIntroActivity.this.descriptionText.layout(i22, i24, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i22, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i24);
                                int measuredWidth7 = (int) (var_ + (((var_ * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                                int i25 = (int) (f9 * 0.8f);
                                ActionIntroActivity.this.buttonTextView.layout(measuredWidth7, i25, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth7, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i25);
                                return;
                            } else {
                                float var_ = (float) i8;
                                int i26 = (int) (0.2229f * var_);
                                ActionIntroActivity.this.imageView.layout(0, i26, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i26);
                                int i27 = (int) (0.352f * var_);
                                ActionIntroActivity.this.titleTextView.layout(0, i27, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i27);
                                int i28 = (int) (0.409f * var_);
                                ActionIntroActivity.this.subtitleTextView.layout(0, i28, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i28);
                                int i29 = (int) (0.468f * var_);
                                ActionIntroActivity.this.descriptionText.layout(0, i29, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i29);
                                int measuredWidth8 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                                int i30 = (int) (var_ * 0.805f);
                                ActionIntroActivity.this.buttonTextView.layout(measuredWidth8, i30, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth8, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i30);
                                return;
                            }
                        } else if (i5 > i6) {
                            float var_ = (float) i8;
                            int measuredHeight4 = ((int) ((0.9f * var_) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                            ActionIntroActivity.this.imageView.layout(0, measuredHeight4, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight4);
                            int measuredHeight5 = measuredHeight4 + ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, measuredHeight5, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + measuredHeight5);
                            float var_ = (float) i7;
                            float var_ = 0.4f * var_;
                            int i31 = (int) var_;
                            int i32 = (int) (0.12f * var_);
                            ActionIntroActivity.this.titleTextView.layout(i31, i32, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i31, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i32);
                            int i33 = (int) (0.26f * var_);
                            ActionIntroActivity.this.descriptionText.layout(i31, i33, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i31, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i33);
                            int measuredWidth9 = (int) (var_ + (((var_ * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int i34 = (int) (var_ * 0.6f);
                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth9, i34, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth9, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i34);
                            int measuredHeight6 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(i31, measuredHeight6, ActionIntroActivity.this.descriptionText2.getMeasuredWidth() + i31, ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + measuredHeight6);
                            return;
                        } else {
                            float var_ = (float) i8;
                            int i35 = (int) (0.197f * var_);
                            ActionIntroActivity.this.imageView.layout(0, i35, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i35);
                            int i36 = (int) (0.421f * var_);
                            ActionIntroActivity.this.titleTextView.layout(0, i36, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i36);
                            int i37 = (int) (0.477f * var_);
                            ActionIntroActivity.this.subtitleTextView.layout(0, i37, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i37);
                            int i38 = (int) (0.537f * var_);
                            ActionIntroActivity.this.descriptionText.layout(0, i38, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i38);
                            int measuredWidth10 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                            int i39 = (int) (var_ * 0.71f);
                            ActionIntroActivity.this.buttonTextView.layout(measuredWidth10, i39, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth10, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i39);
                            int measuredHeight7 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(0, measuredHeight7, ActionIntroActivity.this.descriptionText2.getMeasuredWidth(), ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + measuredHeight7);
                            return;
                        }
                    }
                    if (i5 > i6) {
                        int measuredHeight8 = (i8 - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                        float var_ = (float) i7;
                        int measuredWidth11 = ((int) ((0.5f * var_) - ((float) ActionIntroActivity.this.imageView.getMeasuredWidth()))) / 2;
                        ActionIntroActivity.this.imageView.layout(measuredWidth11, measuredHeight8, ActionIntroActivity.this.imageView.getMeasuredWidth() + measuredWidth11, ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight8);
                        float var_ = 0.4f * var_;
                        int i40 = (int) var_;
                        float var_ = (float) i8;
                        int i41 = (int) (0.14f * var_);
                        ActionIntroActivity.this.titleTextView.layout(i40, i41, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i40, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i41);
                        int i42 = (int) (0.31f * var_);
                        ActionIntroActivity.this.descriptionText.layout(i40, i42, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i40, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i42);
                        int measuredWidth12 = (int) (var_ + (((var_ * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                        int i43 = (int) (var_ * 0.78f);
                        ActionIntroActivity.this.buttonTextView.layout(measuredWidth12, i43, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth12, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i43);
                        return;
                    }
                    float var_ = (float) i8;
                    int i44 = (int) (0.214f * var_);
                    int measuredWidth13 = (i7 - ActionIntroActivity.this.imageView.getMeasuredWidth()) / 2;
                    ActionIntroActivity.this.imageView.layout(measuredWidth13, i44, ActionIntroActivity.this.imageView.getMeasuredWidth() + measuredWidth13, ActionIntroActivity.this.imageView.getMeasuredHeight() + i44);
                    int i45 = (int) (0.414f * var_);
                    ActionIntroActivity.this.titleTextView.layout(0, i45, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i45);
                    int i46 = (int) (0.493f * var_);
                    ActionIntroActivity.this.descriptionText.layout(0, i46, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i46);
                    int measuredWidth14 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                    int i47 = (int) (var_ * 0.71f);
                    ActionIntroActivity.this.buttonTextView.layout(measuredWidth14, i47, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth14, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i47);
                } else if (i5 > i6) {
                    int measuredHeight9 = (i8 - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                    ActionIntroActivity.this.imageView.layout(0, measuredHeight9, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + measuredHeight9);
                    float var_ = (float) i7;
                    float var_ = 0.4f * var_;
                    int i48 = (int) var_;
                    float var_ = (float) i8;
                    int i49 = (int) (0.22f * var_);
                    ActionIntroActivity.this.titleTextView.layout(i48, i49, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i48, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i49);
                    int i50 = (int) (0.39f * var_);
                    ActionIntroActivity.this.descriptionText.layout(i48, i50, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i48, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i50);
                    int measuredWidth15 = (int) (var_ + (((var_ * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                    int i51 = (int) (var_ * 0.69f);
                    ActionIntroActivity.this.buttonTextView.layout(measuredWidth15, i51, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth15, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i51);
                } else {
                    float var_ = (float) i8;
                    int i52 = (int) (0.188f * var_);
                    ActionIntroActivity.this.imageView.layout(0, i52, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i52);
                    int i53 = (int) (0.651f * var_);
                    ActionIntroActivity.this.titleTextView.layout(0, i53, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i53);
                    int i54 = (int) (0.731f * var_);
                    ActionIntroActivity.this.descriptionText.layout(0, i54, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i54);
                    int measuredWidth16 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                    int i55 = (int) (var_ * 0.853f);
                    ActionIntroActivity.this.buttonTextView.layout(measuredWidth16, i55, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth16, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i55);
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
        if (this.currentType == 2) {
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
            int i5 = 0;
            for (int i6 = 3; i5 < i6; i6 = 3) {
                LinearLayout linearLayout2 = new LinearLayout(context2);
                linearLayout2.setOrientation(i2);
                this.descriptionLayout.addView(linearLayout2, LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 0.0f, i5 != i4 ? 7.0f : 0.0f));
                int i7 = i5 * 2;
                this.desctiptionLines[i7] = new TextView(context2);
                this.desctiptionLines[i7].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.desctiptionLines[i7].setGravity(LocaleController.isRTL ? 5 : 3);
                this.desctiptionLines[i7].setTextSize(i3, f);
                TextView textView4 = this.desctiptionLines[i7];
                String str2 = LocaleController.isRTL ? ".%d" : "%d.";
                Object[] objArr = new Object[i3];
                int i8 = i5 + 1;
                objArr[i2] = Integer.valueOf(i8);
                textView4.setText(String.format(str2, objArr));
                this.desctiptionLines[i7].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                int i9 = i7 + 1;
                this.desctiptionLines[i9] = new TextView(context2);
                this.desctiptionLines[i9].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.desctiptionLines[i9].setGravity(LocaleController.isRTL ? 5 : 3);
                this.desctiptionLines[i9].setTextSize(i3, f);
                if (i5 == 0) {
                    this.desctiptionLines[i9].setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
                    this.desctiptionLines[i9].setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
                    String string = LocaleController.getString("AuthAnotherClientInfo1", NUM);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                    int indexOf = string.indexOf(42);
                    int lastIndexOf = string.lastIndexOf(42);
                    if (!(indexOf == -1 || lastIndexOf == -1 || indexOf == lastIndexOf)) {
                        this.desctiptionLines[i9].setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 1, str);
                        spannableStringBuilder.replace(indexOf, indexOf + 1, str);
                        spannableStringBuilder.setSpan(new URLSpanNoUnderline(LocaleController.getString("AuthAnotherClientDownloadClientUrl", NUM)), indexOf, lastIndexOf - 1, 33);
                    }
                    this.desctiptionLines[i9].setText(spannableStringBuilder);
                } else if (i5 == 1) {
                    this.desctiptionLines[i9].setText(LocaleController.getString("AuthAnotherClientInfo2", NUM));
                } else {
                    this.desctiptionLines[i9].setText(LocaleController.getString("AuthAnotherClientInfo3", NUM));
                }
                if (LocaleController.isRTL) {
                    linearLayout2.setGravity(5);
                    linearLayout2.addView(this.desctiptionLines[i9], LayoutHelper.createLinear(0, -2, 1.0f));
                    linearLayout2.addView(this.desctiptionLines[i7], LayoutHelper.createLinear(-2, -2, 4.0f, 0.0f, 0.0f, 0.0f));
                } else {
                    linearLayout2.addView(this.desctiptionLines[i7], LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 4.0f, 0.0f));
                    linearLayout2.addView(this.desctiptionLines[i9], LayoutHelper.createLinear(-2, -2));
                }
                i5 = i8;
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
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        viewGroup.addView(this.buttonTextView);
        this.buttonTextView.setOnClickListener(new ActionIntroActivity$$ExternalSyntheticLambda3(this));
        int i10 = this.currentType;
        if (i10 == 0) {
            this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            this.imageView.setAnimation(NUM, 200, 200);
            this.titleTextView.setText(LocaleController.getString("ChannelAlertTitle", NUM));
            this.descriptionText.setText(LocaleController.getString("ChannelAlertText", NUM));
            this.buttonTextView.setText(LocaleController.getString("ChannelAlertCreate2", NUM));
            this.imageView.playAnimation();
            this.flickerButton = true;
        } else if (i10 == 1) {
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context2, 3));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("PeopleNearby", NUM));
            this.descriptionText.setText(LocaleController.getString("PeopleNearbyAccessInfo", NUM));
            this.buttonTextView.setText(LocaleController.getString("PeopleNearbyAllowAccess", NUM));
        } else if (i10 == 2) {
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
        } else if (i10 == 3) {
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
        } else if (i10 == 4) {
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context2, 3));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("PeopleNearby", NUM));
            this.descriptionText.setText(LocaleController.getString("PeopleNearbyGpsInfo", NUM));
            this.buttonTextView.setText(LocaleController.getString("PeopleNearbyGps", NUM));
        } else if (i10 == 5) {
            this.colors = new int[8];
            updateColors();
            this.imageView.setAnimation(NUM, 334, 334, this.colors);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("AuthAnotherClient", NUM));
            this.buttonTextView.setText(LocaleController.getString("AuthAnotherClientScan", NUM));
            this.imageView.playAnimation();
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
            int i = this.currentType;
            if (i == 0) {
                Bundle bundle = new Bundle();
                bundle.putInt("step", 0);
                presentFragment(new ChannelCreateActivity(bundle), true);
            } else if (i == 1) {
                getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            } else if (i != 2) {
                if (i == 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("PhoneNumberChangeTitle", NUM));
                    builder.setMessage(LocaleController.getString("PhoneNumberAlert", NUM));
                    builder.setPositiveButton(LocaleController.getString("Change", NUM), new ActionIntroActivity$$ExternalSyntheticLambda2(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                } else if (i == 4) {
                    try {
                        getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else if (i != 5 || getParentActivity() == null) {
                } else {
                    if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                        processOpenQrReader();
                        return;
                    }
                    getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 34);
                }
            } else if (this.currentGroupCreateAddress != null && this.currentGroupCreateLocation != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putLongArray("result", new long[]{getUserConfig().getClientUserId()});
                bundle2.putInt("chatType", 4);
                bundle2.putString("address", this.currentGroupCreateAddress);
                bundle2.putParcelable("location", this.currentGroupCreateLocation);
                presentFragment(new GroupCreateFinalActivity(bundle2), true);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(DialogInterface dialogInterface, int i) {
        presentFragment(new ChangePhoneActivity(), true);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
                        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new ActionIntroActivity$$ExternalSyntheticLambda0(this));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                        showDialog(builder.create());
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new ActionIntroActivity$$ExternalSyntheticLambda5(this));
                }
            } else if (i != 34) {
            } else {
                if (iArr.length <= 0 || iArr[0] != 0) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                    builder2.setTitle(LocaleController.getString("AppName", NUM));
                    builder2.setMessage(LocaleController.getString("QRCodePermissionNoCamera", NUM));
                    builder2.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new ActionIntroActivity$$ExternalSyntheticLambda1(this));
                    builder2.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    builder2.show();
                    return;
                }
                processOpenQrReader();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$4(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$5() {
        presentFragment(new PeopleNearbyActivity(), true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$6(DialogInterface dialogInterface, int i) {
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
