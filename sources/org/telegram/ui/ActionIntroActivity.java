package org.telegram.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.LocationFetchCallback;
import org.telegram.messenger.MrzRecognizer.Result;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate;
import org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate.-CC;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.ShareLocationDrawable;
import org.telegram.ui.Components.URLSpanNoUnderline;

@TargetApi(23)
public class ActionIntroActivity extends BaseFragment implements LocationFetchCallback {
    public static final int ACTION_TYPE_CHANGE_PHONE_NUMBER = 3;
    public static final int ACTION_TYPE_CHANNEL_CREATE = 0;
    public static final int ACTION_TYPE_NEARBY_GROUP_CREATE = 2;
    public static final int ACTION_TYPE_NEARBY_LOCATION_ACCESS = 1;
    public static final int ACTION_TYPE_NEARBY_LOCATION_ENABLED = 4;
    public static final int ACTION_TYPE_QR_LOGIN = 5;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 34;
    private TextView buttonTextView;
    private int[] colors;
    private String currentGroupCreateAddress;
    private String currentGroupCreateDisplayAddress;
    private Location currentGroupCreateLocation;
    private int currentType;
    private LinearLayout descriptionLayout;
    private TextView descriptionText;
    private TextView descriptionText2;
    private TextView[] desctiptionLines = new TextView[6];
    private Drawable drawable1;
    private Drawable drawable2;
    private RLottieImageView imageView;
    private ActionIntroQRLoginDelegate qrLoginDelegate;
    private TextView subtitleTextView;
    private TextView titleTextView;

    public interface ActionIntroQRLoginDelegate {
        void didFindQRCode(String str);
    }

    public ActionIntroActivity(int i) {
        this.currentType = i;
    }

    public View createView(Context context) {
        Context context2 = context;
        String str = "windowBackgroundWhite";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        this.actionBar.setBackButtonImage(NUM);
        int i = 0;
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ActionIntroActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new ViewGroup(context2) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                i = MeasureSpec.getSize(i);
                int size = MeasureSpec.getSize(i2);
                ActionIntroActivity.this.actionBar.measure(MeasureSpec.makeMeasureSpec(i, NUM), i2);
                i2 = ActionIntroActivity.this.currentType;
                float f;
                int i3;
                if (i2 != 0) {
                    if (i2 != 1) {
                        float f2;
                        int i4;
                        if (i2 != 2) {
                            if (i2 != 3) {
                                if (i2 != 4) {
                                    if (i2 == 5) {
                                        if (i > size) {
                                            f = (float) i;
                                            ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec((int) (0.45f * f), NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.68f), NUM));
                                            i3 = (int) (f * 0.6f);
                                            ActionIntroActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                                            ActionIntroActivity.this.descriptionLayout.measure(MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(size, 0));
                                            ActionIntroActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                                        } else {
                                            ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.399f), NUM));
                                            ActionIntroActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                                            ActionIntroActivity.this.descriptionLayout.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(size, 0));
                                            ActionIntroActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                                        }
                                    }
                                }
                            } else if (i > size) {
                                f2 = (float) i;
                                i4 = (int) (0.45f * f2);
                                ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(i4, NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.78f), Integer.MIN_VALUE));
                                ActionIntroActivity.this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(i4, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                                i3 = (int) (f2 * 0.6f);
                                ActionIntroActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                                ActionIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                                ActionIntroActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            } else {
                                ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.44f), Integer.MIN_VALUE));
                                ActionIntroActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                                ActionIntroActivity.this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                                ActionIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                                ActionIntroActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                            }
                        } else if (i > size) {
                            f2 = (float) i;
                            i4 = (int) (0.45f * f2);
                            ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(i4, NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.78f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(i4, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            i3 = (int) (f2 * 0.6f);
                            ActionIntroActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            ActionIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            ActionIntroActivity.this.descriptionText2.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            ActionIntroActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                        } else {
                            ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.44f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            ActionIntroActivity.this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            ActionIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            ActionIntroActivity.this.descriptionText2.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            ActionIntroActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                        }
                    }
                    if (i > size) {
                        ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
                        i3 = (int) (((float) i) * 0.6f);
                        ActionIntroActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                        ActionIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                        ActionIntroActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                    } else {
                        ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
                        ActionIntroActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                        ActionIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                        ActionIntroActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                    }
                } else if (i > size) {
                    f = (float) i;
                    ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec((int) (0.45f * f), NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.68f), NUM));
                    i3 = (int) (f * 0.6f);
                    ActionIntroActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                    ActionIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                    ActionIntroActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                } else {
                    ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.399f), NUM));
                    ActionIntroActivity.this.titleTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                    ActionIntroActivity.this.descriptionText.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                    ActionIntroActivity.this.buttonTextView.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                }
                setMeasuredDimension(i, size);
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5 = i3;
                int i6 = i4;
                ActionIntroActivity.this.actionBar.layout(0, 0, i5, ActionIntroActivity.this.actionBar.getMeasuredHeight());
                int i7 = i5 - i;
                int i8 = i6 - i2;
                int access$100 = ActionIntroActivity.this.currentType;
                float f;
                float f2;
                float f3;
                if (access$100 != 0) {
                    float f4;
                    if (access$100 != 1) {
                        if (access$100 != 2) {
                            if (access$100 != 3) {
                                if (access$100 != 4) {
                                    if (access$100 == 5) {
                                        if (i5 > i6) {
                                            i5 = (i8 - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                                            ActionIntroActivity.this.imageView.layout(0, i5, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i5);
                                            f = (float) i7;
                                            f2 = 0.4f * f;
                                            i6 = (int) f2;
                                            f3 = (float) i8;
                                            i8 = (int) (0.22f * f3);
                                            ActionIntroActivity.this.titleTextView.layout(i6, i8, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i6, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i8);
                                            f *= 0.6f;
                                            i6 = (int) (((f - ((float) ActionIntroActivity.this.descriptionLayout.getMeasuredWidth())) / 2.0f) + f2);
                                            i8 = (int) (0.39f * f3);
                                            ActionIntroActivity.this.descriptionLayout.layout(i6, i8, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + i6, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + i8);
                                            i5 = (int) (f2 + ((f - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                                            i6 = (int) (f3 * 0.74f);
                                            ActionIntroActivity.this.buttonTextView.layout(i5, i6, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i6);
                                            return;
                                        }
                                        f = (float) i8;
                                        i6 = (int) (0.148f * f);
                                        ActionIntroActivity.this.imageView.layout(0, i6, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i6);
                                        i6 = (int) (0.551f * f);
                                        ActionIntroActivity.this.titleTextView.layout(0, i6, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i6);
                                        i6 = (int) (0.631f * f);
                                        i8 = (getMeasuredWidth() - ActionIntroActivity.this.descriptionLayout.getMeasuredWidth()) / 2;
                                        ActionIntroActivity.this.descriptionLayout.layout(i8, i6, ActionIntroActivity.this.descriptionLayout.getMeasuredWidth() + i8, ActionIntroActivity.this.descriptionLayout.getMeasuredHeight() + i6);
                                        i7 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                                        i5 = (int) (f * 0.853f);
                                        ActionIntroActivity.this.buttonTextView.layout(i7, i5, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i7, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i5);
                                        return;
                                    }
                                    return;
                                }
                            } else if (i5 > i6) {
                                f = (float) i8;
                                i6 = ((int) ((0.95f * f) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                                ActionIntroActivity.this.imageView.layout(0, i6, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i6);
                                i6 += ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                                ActionIntroActivity.this.subtitleTextView.layout(0, i6, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i6);
                                f4 = (float) i7;
                                f2 = 0.4f * f4;
                                i7 = (int) f2;
                                i8 = (int) (0.12f * f);
                                ActionIntroActivity.this.titleTextView.layout(i7, i8, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i7, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i8);
                                i8 = (int) (0.24f * f);
                                ActionIntroActivity.this.descriptionText.layout(i7, i8, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i7, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i8);
                                i6 = (int) (f2 + (((f4 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                                i5 = (int) (f * 0.8f);
                                ActionIntroActivity.this.buttonTextView.layout(i6, i5, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i6, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i5);
                                return;
                            } else {
                                f = (float) i8;
                                i6 = (int) (0.2229f * f);
                                ActionIntroActivity.this.imageView.layout(0, i6, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i6);
                                i6 = (int) (0.352f * f);
                                ActionIntroActivity.this.titleTextView.layout(0, i6, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i6);
                                i6 = (int) (0.409f * f);
                                ActionIntroActivity.this.subtitleTextView.layout(0, i6, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i6);
                                i6 = (int) (0.468f * f);
                                ActionIntroActivity.this.descriptionText.layout(0, i6, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i6);
                                i7 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                                i5 = (int) (f * 0.805f);
                                ActionIntroActivity.this.buttonTextView.layout(i7, i5, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i7, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i5);
                                return;
                            }
                        } else if (i5 > i6) {
                            f = (float) i8;
                            i6 = ((int) ((0.9f * f) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                            ActionIntroActivity.this.imageView.layout(0, i6, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i6);
                            i6 += ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, i6, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i6);
                            f4 = (float) i7;
                            f2 = 0.4f * f4;
                            i7 = (int) f2;
                            i8 = (int) (0.12f * f);
                            ActionIntroActivity.this.titleTextView.layout(i7, i8, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i7, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i8);
                            i8 = (int) (0.26f * f);
                            ActionIntroActivity.this.descriptionText.layout(i7, i8, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i7, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i8);
                            i6 = (int) (f2 + (((f4 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            i5 = (int) (f * 0.6f);
                            ActionIntroActivity.this.buttonTextView.layout(i6, i5, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i6, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i5);
                            i5 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(i7, i5, ActionIntroActivity.this.descriptionText2.getMeasuredWidth() + i7, ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + i5);
                            return;
                        } else {
                            f = (float) i8;
                            i6 = (int) (0.197f * f);
                            ActionIntroActivity.this.imageView.layout(0, i6, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i6);
                            i6 = (int) (0.421f * f);
                            ActionIntroActivity.this.titleTextView.layout(0, i6, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i6);
                            i6 = (int) (0.477f * f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, i6, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i6);
                            i6 = (int) (0.537f * f);
                            ActionIntroActivity.this.descriptionText.layout(0, i6, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i6);
                            i7 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                            i5 = (int) (f * 0.71f);
                            ActionIntroActivity.this.buttonTextView.layout(i7, i5, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i7, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i5);
                            i5 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(0, i5, ActionIntroActivity.this.descriptionText2.getMeasuredWidth(), ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + i5);
                            return;
                        }
                    }
                    if (i5 > i6) {
                        i5 = (i8 - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                        f4 = (float) i7;
                        i7 = ((int) ((0.5f * f4) - ((float) ActionIntroActivity.this.imageView.getMeasuredWidth()))) / 2;
                        ActionIntroActivity.this.imageView.layout(i7, i5, ActionIntroActivity.this.imageView.getMeasuredWidth() + i7, ActionIntroActivity.this.imageView.getMeasuredHeight() + i5);
                        f2 = 0.4f * f4;
                        i5 = (int) f2;
                        f3 = (float) i8;
                        i8 = (int) (0.14f * f3);
                        ActionIntroActivity.this.titleTextView.layout(i5, i8, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i8);
                        i8 = (int) (0.31f * f3);
                        ActionIntroActivity.this.descriptionText.layout(i5, i8, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i5, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i8);
                        i5 = (int) (f2 + (((f4 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                        i6 = (int) (f3 * 0.78f);
                        ActionIntroActivity.this.buttonTextView.layout(i5, i6, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i6);
                        return;
                    }
                    f = (float) i8;
                    i6 = (int) (0.214f * f);
                    i8 = (i7 - ActionIntroActivity.this.imageView.getMeasuredWidth()) / 2;
                    ActionIntroActivity.this.imageView.layout(i8, i6, ActionIntroActivity.this.imageView.getMeasuredWidth() + i8, ActionIntroActivity.this.imageView.getMeasuredHeight() + i6);
                    i6 = (int) (0.414f * f);
                    ActionIntroActivity.this.titleTextView.layout(0, i6, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i6);
                    i6 = (int) (0.493f * f);
                    ActionIntroActivity.this.descriptionText.layout(0, i6, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i6);
                    i7 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                    i5 = (int) (f * 0.71f);
                    ActionIntroActivity.this.buttonTextView.layout(i7, i5, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i7, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i5);
                } else if (i5 > i6) {
                    i5 = (i8 - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                    ActionIntroActivity.this.imageView.layout(0, i5, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i5);
                    f = (float) i7;
                    f2 = 0.4f * f;
                    i6 = (int) f2;
                    f3 = (float) i8;
                    i8 = (int) (0.22f * f3);
                    ActionIntroActivity.this.titleTextView.layout(i6, i8, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i6, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i8);
                    i8 = (int) (0.39f * f3);
                    ActionIntroActivity.this.descriptionText.layout(i6, i8, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i6, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i8);
                    i5 = (int) (f2 + (((f * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                    i6 = (int) (f3 * 0.69f);
                    ActionIntroActivity.this.buttonTextView.layout(i5, i6, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i6);
                } else {
                    f = (float) i8;
                    i6 = (int) (0.188f * f);
                    ActionIntroActivity.this.imageView.layout(0, i6, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i6);
                    i6 = (int) (0.651f * f);
                    ActionIntroActivity.this.titleTextView.layout(0, i6, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i6);
                    i6 = (int) (0.731f * f);
                    ActionIntroActivity.this.descriptionText.layout(0, i6, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i6);
                    i7 = (i7 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                    i5 = (int) (f * 0.853f);
                    ActionIntroActivity.this.buttonTextView.layout(i7, i5, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i7, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i5);
                }
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(str));
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        viewGroup.setOnTouchListener(-$$Lambda$ActionIntroActivity$fQjBdUFaAXJ5YCul6b6pzBMDpBQ.INSTANCE);
        viewGroup.addView(this.actionBar);
        this.imageView = new RLottieImageView(context2);
        viewGroup.addView(this.imageView);
        this.titleTextView = new TextView(context2);
        String str2 = "windowBackgroundWhiteBlackText";
        this.titleTextView.setTextColor(Theme.getColor(str2));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        viewGroup.addView(this.titleTextView);
        this.subtitleTextView = new TextView(context2);
        this.subtitleTextView.setTextColor(Theme.getColor(str2));
        this.subtitleTextView.setGravity(1);
        float f = 15.0f;
        this.subtitleTextView.setTextSize(1, 15.0f);
        this.subtitleTextView.setSingleLine(true);
        this.subtitleTextView.setEllipsize(TruncateAt.END);
        if (this.currentType == 2) {
            this.subtitleTextView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        } else {
            this.subtitleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        this.subtitleTextView.setVisibility(8);
        viewGroup.addView(this.subtitleTextView);
        this.descriptionText = new TextView(context2);
        String str3 = "windowBackgroundWhiteGrayText6";
        this.descriptionText.setTextColor(Theme.getColor(str3));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        if (this.currentType == 2) {
            this.descriptionText.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        } else {
            this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        viewGroup.addView(this.descriptionText);
        CharSequence charSequence = "";
        int i2 = 3;
        if (this.currentType == 5) {
            this.descriptionLayout = new LinearLayout(context2);
            this.descriptionLayout.setOrientation(1);
            this.descriptionLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
            viewGroup.addView(this.descriptionLayout);
            int i3 = 0;
            while (i3 < i2) {
                LinearLayout linearLayout = new LinearLayout(context2);
                linearLayout.setOrientation(i);
                this.descriptionLayout.addView(linearLayout, LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 0.0f, i3 != 2 ? 7.0f : 0.0f));
                int i4 = i3 * 2;
                this.desctiptionLines[i4] = new TextView(context2);
                this.desctiptionLines[i4].setTextColor(Theme.getColor(str2));
                this.desctiptionLines[i4].setGravity(i2);
                this.desctiptionLines[i4].setTextSize(1, f);
                TextView textView = this.desctiptionLines[i4];
                Object[] objArr = new Object[1];
                int i5 = i3 + 1;
                objArr[i] = Integer.valueOf(i5);
                textView.setText(String.format("%d.", objArr));
                this.desctiptionLines[i4].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                linearLayout.addView(this.desctiptionLines[i4], LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 4.0f, 0.0f));
                i4++;
                this.desctiptionLines[i4] = new TextView(context2);
                this.desctiptionLines[i4].setTextColor(Theme.getColor(str2));
                this.desctiptionLines[i4].setGravity(i2);
                this.desctiptionLines[i4].setTextSize(1, f);
                if (i3 == 0) {
                    this.desctiptionLines[i4].setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
                    this.desctiptionLines[i4].setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
                    str = LocaleController.getString("AuthAnotherClientInfo1", NUM);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                    int indexOf = str.indexOf(42);
                    i3 = str.lastIndexOf(42);
                    if (!(indexOf == -1 || i3 == -1 || indexOf == i3)) {
                        this.desctiptionLines[i4].setMovementMethod(new LinkMovementMethodMy());
                        spannableStringBuilder.replace(i3, i3 + 1, charSequence);
                        spannableStringBuilder.replace(indexOf, indexOf + 1, charSequence);
                        spannableStringBuilder.setSpan(new URLSpanNoUnderline(LocaleController.getString("AuthAnotherClientDownloadClientUrl", NUM)), indexOf, i3 - 1, 33);
                    }
                    this.desctiptionLines[i4].setText(spannableStringBuilder);
                } else if (i3 == 1) {
                    this.desctiptionLines[i4].setText(LocaleController.getString("AuthAnotherClientInfo2", NUM));
                } else if (i3 == 2) {
                    this.desctiptionLines[i4].setText(LocaleController.getString("AuthAnotherClientInfo3", NUM));
                }
                linearLayout.addView(this.desctiptionLines[i4], LayoutHelper.createLinear(-2, -2));
                i3 = i5;
                i = 0;
                i2 = 3;
                f = 15.0f;
            }
            this.descriptionText.setVisibility(8);
        }
        this.descriptionText2 = new TextView(context2);
        this.descriptionText2.setTextColor(Theme.getColor(str3));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setTextSize(1, 13.0f);
        this.descriptionText2.setVisibility(8);
        if (this.currentType == 2) {
            this.descriptionText2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        } else {
            this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        viewGroup.addView(this.descriptionText2);
        this.buttonTextView = new TextView(context2);
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        viewGroup.addView(this.buttonTextView);
        this.buttonTextView.setOnClickListener(new -$$Lambda$ActionIntroActivity$BcvFnoCk-mhvF1NmHV0oGHf_Czc(this));
        int i6 = this.currentType;
        TextView textView2;
        if (i6 == 0) {
            this.imageView.setImageResource(NUM);
            this.imageView.setScaleType(ScaleType.FIT_CENTER);
            this.titleTextView.setText(LocaleController.getString("ChannelAlertTitle", NUM));
            this.descriptionText.setText(LocaleController.getString("ChannelAlertText", NUM));
            this.buttonTextView.setText(LocaleController.getString("ChannelAlertCreate2", NUM));
        } else if (i6 == 1) {
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context2, 3));
            this.imageView.setScaleType(ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("PeopleNearby", NUM));
            this.descriptionText.setText(LocaleController.getString("PeopleNearbyAccessInfo", NUM));
            this.buttonTextView.setText(LocaleController.getString("PeopleNearbyAllowAccess", NUM));
        } else if (i6 == 2) {
            this.subtitleTextView.setVisibility(0);
            this.descriptionText2.setVisibility(0);
            this.imageView.setImageResource(Theme.getCurrentTheme().isDark() ? NUM : NUM);
            this.imageView.setScaleType(ScaleType.CENTER);
            textView2 = this.subtitleTextView;
            String str4 = this.currentGroupCreateDisplayAddress;
            if (str4 != null) {
                charSequence = str4;
            }
            textView2.setText(charSequence);
            this.titleTextView.setText(LocaleController.getString("NearbyCreateGroup", NUM));
            this.descriptionText.setText(LocaleController.getString("NearbyCreateGroupInfo", NUM));
            this.descriptionText2.setText(LocaleController.getString("NearbyCreateGroupInfo2", NUM));
            this.buttonTextView.setText(LocaleController.getString("NearbyStartGroup", NUM));
        } else if (i6 == 3) {
            this.subtitleTextView.setVisibility(0);
            this.drawable1 = context.getResources().getDrawable(NUM);
            this.drawable2 = context.getResources().getDrawable(NUM);
            this.drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image"), Mode.MULTIPLY));
            this.drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image2"), Mode.MULTIPLY));
            this.imageView.setImageDrawable(new CombinedDrawable(this.drawable1, this.drawable2));
            this.imageView.setScaleType(ScaleType.CENTER);
            UserConfig userConfig = getUserConfig();
            User user = getMessagesController().getUser(Integer.valueOf(userConfig.clientUserId));
            if (user == null) {
                user = userConfig.getCurrentUser();
            }
            if (user != null) {
                textView2 = this.subtitleTextView;
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(user.phone);
                textView2.setText(instance.format(stringBuilder.toString()));
            }
            this.titleTextView.setText(LocaleController.getString("PhoneNumberChange2", NUM));
            this.descriptionText.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", NUM)));
            this.buttonTextView.setText(LocaleController.getString("PhoneNumberChange2", NUM));
        } else if (i6 == 4) {
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context2, 3));
            this.imageView.setScaleType(ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("PeopleNearby", NUM));
            this.descriptionText.setText(LocaleController.getString("PeopleNearbyGpsInfo", NUM));
            this.buttonTextView.setText(LocaleController.getString("PeopleNearbyGps", NUM));
        } else if (i6 == 5) {
            this.colors = new int[8];
            updateColors();
            this.imageView.setAnimation(NUM, 334, 334, this.colors);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("AuthAnotherClient", NUM));
            this.buttonTextView.setText(LocaleController.getString("AuthAnotherClientScan", NUM));
            this.imageView.playAnimation();
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$ActionIntroActivity(View view) {
        if (getParentActivity() != null) {
            int i = this.currentType;
            Bundle bundle;
            if (i == 0) {
                bundle = new Bundle();
                bundle.putInt("step", 0);
                presentFragment(new ChannelCreateActivity(bundle), true);
            } else if (i == 1) {
                getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            } else if (i != 2) {
                if (i == 3) {
                    Builder builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("PhoneNumberChangeTitle", NUM));
                    builder.setMessage(LocaleController.getString("PhoneNumberAlert", NUM));
                    builder.setPositiveButton(LocaleController.getString("Change", NUM), new -$$Lambda$ActionIntroActivity$h-zrlY3ShP9MNAFsjq-ukj28QiM(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    showDialog(builder.create());
                } else if (i == 4) {
                    try {
                        getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (i == 5 && getParentActivity() != null) {
                    if (VERSION.SDK_INT >= 23) {
                        if (getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                            getParentActivity().requestPermissions(new String[]{r2}, 34);
                            return;
                        }
                    }
                    processOpenQrReader();
                }
            } else if (this.currentGroupCreateAddress != null && this.currentGroupCreateLocation != null) {
                bundle = new Bundle();
                ArrayList arrayList = new ArrayList();
                arrayList.add(Integer.valueOf(getUserConfig().getClientUserId()));
                bundle.putIntegerArrayList("result", arrayList);
                bundle.putInt("chatType", 4);
                bundle.putString("address", this.currentGroupCreateAddress);
                bundle.putParcelable("location", this.currentGroupCreateLocation);
                presentFragment(new GroupCreateFinalActivity(bundle), true);
            }
        }
    }

    public /* synthetic */ void lambda$null$1$ActionIntroActivity(DialogInterface dialogInterface, int i) {
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
        super.onResume();
        if (this.currentType == 4) {
            boolean isLocationEnabled;
            int i = VERSION.SDK_INT;
            if (i >= 28) {
                isLocationEnabled = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
            } else {
                if (i >= 19) {
                    try {
                        boolean z = false;
                        if (Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) != 0) {
                            z = true;
                        }
                        isLocationEnabled = z;
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                isLocationEnabled = true;
            }
            if (isLocationEnabled) {
                presentFragment(new PeopleNearbyActivity(), true);
            }
        }
    }

    private void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$ActionIntroActivity$MDYMt6ansWvDbsIN0dYdwBhtYBI(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$3$ActionIntroActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("package:");
                stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                intent.setData(Uri.parse(stringBuilder.toString()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void updateColors() {
        int[] iArr = this.colors;
        iArr[0] = 3355443;
        iArr[1] = Theme.getColor("windowBackgroundWhiteBlackText");
        iArr = this.colors;
        iArr[2] = 16777215;
        String str = "windowBackgroundWhite";
        iArr[3] = Theme.getColor(str);
        iArr = this.colors;
        iArr[4] = 5285866;
        iArr[5] = Theme.getColor("featuredStickers_addButton");
        iArr = this.colors;
        iArr[6] = 2170912;
        iArr[7] = Theme.getColor(str);
        this.imageView.replaceColors(this.colors);
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
            String str = "OK";
            String str2 = "PermissionOpenSettings";
            String str3 = "AppName";
            Builder builder;
            if (i == 2) {
                if (!(iArr == null || iArr.length == 0)) {
                    if (iArr[0] != 0) {
                        builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString(str3, NUM));
                        builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
                        builder.setNegativeButton(LocaleController.getString(str2, NUM), new -$$Lambda$ActionIntroActivity$VKxRXA-XbgTCJpZrpCKC8NHvoI4(this));
                        builder.setPositiveButton(LocaleController.getString(str, NUM), null);
                        showDialog(builder.create());
                    } else {
                        presentFragment(new PeopleNearbyActivity(), true);
                    }
                }
            } else if (i == 34) {
                if (iArr.length <= 0 || iArr[0] != 0) {
                    builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString(str3, NUM));
                    builder.setMessage(LocaleController.getString("WalletPermissionNoCamera", NUM));
                    builder.setNegativeButton(LocaleController.getString(str2, NUM), new -$$Lambda$ActionIntroActivity$LwGLA-XORNnjEoNDA2R_2qNXY_E(this));
                    builder.setPositiveButton(LocaleController.getString(str, NUM), null);
                    builder.show();
                } else {
                    processOpenQrReader();
                }
            }
        }
    }

    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$4$ActionIntroActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("package:");
                stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                intent.setData(Uri.parse(stringBuilder.toString()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$5$ActionIntroActivity(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
            intent.setData(Uri.parse(stringBuilder.toString()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setQrLoginDelegate(ActionIntroQRLoginDelegate actionIntroQRLoginDelegate) {
        this.qrLoginDelegate = actionIntroQRLoginDelegate;
    }

    private void processOpenQrReader() {
        CameraScanActivity.showAsSheet(this, false, new CameraScanActivityDelegate() {
            public /* synthetic */ void didFindMrzInfo(Result result) {
                -CC.$default$didFindMrzInfo(this, result);
            }

            public void didFindQr(String str) {
                ActionIntroActivity.this.finishFragment(false);
                ActionIntroActivity.this.qrLoginDelegate.didFindQRCode(str);
            }
        });
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ActionIntroActivity$fpGyZfwg0FJCkfDsWOrkKaY77lo -__lambda_actionintroactivity_fpgyzfwg0fjckfdsworkkay77lo = new -$$Lambda$ActionIntroActivity$fpGyZfwg0FJCkfDsWOrkKaY77lo(this);
        r10 = new ThemeDescription[19];
        r10[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, -__lambda_actionintroactivity_fpgyzfwg0fjckfdsworkkay77lo, "windowBackgroundWhite");
        r10[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        r10[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2");
        r10[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarWhiteSelector");
        r10[4] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, -__lambda_actionintroactivity_fpgyzfwg0fjckfdsworkkay77lo, "windowBackgroundWhiteBlackText");
        r10[5] = new ThemeDescription(this.subtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[6] = new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6");
        r10[7] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "featuredStickers_buttonText");
        r10[8] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, null, null, null, -__lambda_actionintroactivity_fpgyzfwg0fjckfdsworkkay77lo, "featuredStickers_addButton");
        r10[9] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, null, null, null, null, "featuredStickers_addButtonPressed");
        r10[10] = new ThemeDescription(this.desctiptionLines[0], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[11] = new ThemeDescription(this.desctiptionLines[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[12] = new ThemeDescription(this.desctiptionLines[1], ThemeDescription.FLAG_LINKCOLOR, null, null, null, null, "windowBackgroundWhiteLinkText");
        r10[13] = new ThemeDescription(this.desctiptionLines[2], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[14] = new ThemeDescription(this.desctiptionLines[3], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[15] = new ThemeDescription(this.desctiptionLines[4], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[16] = new ThemeDescription(this.desctiptionLines[5], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[17] = new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, new Drawable[]{this.drawable1}, null, "changephoneinfo_image");
        r10[18] = new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, new Drawable[]{this.drawable2}, null, "changephoneinfo_image2");
        return r10;
    }
}
