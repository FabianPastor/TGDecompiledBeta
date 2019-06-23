package org.telegram.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.LocationFetchCallback;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ShareLocationDrawable;

@TargetApi(23)
public class ActionIntroActivity extends BaseFragment implements LocationFetchCallback {
    public static final int ACTION_TYPE_CHANGE_PHONE_NUMBER = 3;
    public static final int ACTION_TYPE_CHANNEL_CREATE = 0;
    public static final int ACTION_TYPE_NEARBY_GROUP_CREATE = 2;
    public static final int ACTION_TYPE_NEARBY_LOCATION_ACCESS = 1;
    private TextView buttonTextView;
    private String currentGroupCreateAddress;
    private Location currentGroupCreateLocation;
    private int currentType;
    private TextView descriptionText;
    private TextView descriptionText2;
    private Drawable drawable1;
    private Drawable drawable2;
    private ImageView imageView;
    private TextView subtitleTextView;
    private TextView titleTextView;

    public ActionIntroActivity(int i) {
        this.currentType = i;
    }

    public View createView(Context context) {
        String str = "windowBackgroundWhite";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        this.actionBar.setBackButtonImage(NUM);
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
        this.fragmentView = new ViewGroup(context) {
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
                        if (i2 != 2) {
                            if (i2 == 3) {
                                if (i > size) {
                                    f = (float) i;
                                    i3 = (int) (0.45f * f);
                                    ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.78f), Integer.MIN_VALUE));
                                    ActionIntroActivity.this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                                    i3 = (int) (f * 0.6f);
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
                            }
                        } else if (i > size) {
                            f = (float) i;
                            i3 = (int) (0.45f * f);
                            ActionIntroActivity.this.imageView.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec((int) (((float) size) * 0.78f), Integer.MIN_VALUE));
                            ActionIntroActivity.this.subtitleTextView.measure(MeasureSpec.makeMeasureSpec(i3, NUM), MeasureSpec.makeMeasureSpec(size, 0));
                            i3 = (int) (f * 0.6f);
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
                    } else if (i > size) {
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
                ActionIntroActivity.this.actionBar.layout(0, 0, i3, ActionIntroActivity.this.actionBar.getMeasuredHeight());
                int i5 = i3 - i;
                i = i4 - i2;
                i2 = ActionIntroActivity.this.currentType;
                float f;
                float f2;
                float f3;
                if (i2 != 0) {
                    if (i2 != 1) {
                        if (i2 != 2) {
                            if (i2 == 3) {
                                if (i3 > i4) {
                                    f = (float) i;
                                    i2 = ((int) ((0.95f * f) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                                    ActionIntroActivity.this.imageView.layout(0, i2, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                                    i2 += ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                                    ActionIntroActivity.this.subtitleTextView.layout(0, i2, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i2);
                                    f2 = (float) i5;
                                    f3 = 0.4f * f2;
                                    i2 = (int) f3;
                                    i3 = (int) (0.12f * f);
                                    ActionIntroActivity.this.titleTextView.layout(i2, i3, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i2, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i3);
                                    i3 = (int) (0.24f * f);
                                    ActionIntroActivity.this.descriptionText.layout(i2, i3, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i2, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i3);
                                    i5 = (int) (f3 + (((f2 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                                    i = (int) (f * 0.8f);
                                    ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                                    return;
                                }
                                f = (float) i;
                                i2 = (int) (0.2229f * f);
                                ActionIntroActivity.this.imageView.layout(0, i2, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                                i2 = (int) (0.352f * f);
                                ActionIntroActivity.this.titleTextView.layout(0, i2, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i2);
                                i2 = (int) (0.409f * f);
                                ActionIntroActivity.this.subtitleTextView.layout(0, i2, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i2);
                                i2 = (int) (0.468f * f);
                                ActionIntroActivity.this.descriptionText.layout(0, i2, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i2);
                                i5 = (i5 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                                i = (int) (f * 0.805f);
                                ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                            }
                        } else if (i3 > i4) {
                            f = (float) i;
                            i3 = ((int) ((0.9f * f) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                            ActionIntroActivity.this.imageView.layout(0, i3, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i3);
                            i3 += ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, i3, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i3);
                            f2 = (float) i5;
                            f3 = 0.4f * f2;
                            i3 = (int) f3;
                            i4 = (int) (0.12f * f);
                            ActionIntroActivity.this.titleTextView.layout(i3, i4, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i3, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i4);
                            i4 = (int) (0.26f * f);
                            ActionIntroActivity.this.descriptionText.layout(i3, i4, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i3, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i4);
                            i5 = (int) (f3 + (((f2 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            i = (int) (f * 0.6f);
                            ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                            i5 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(i3, i5, ActionIntroActivity.this.descriptionText2.getMeasuredWidth() + i3, ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + i5);
                        } else {
                            f = (float) i;
                            i3 = (int) (0.197f * f);
                            ActionIntroActivity.this.imageView.layout(0, i3, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i3);
                            i3 = (int) (0.421f * f);
                            ActionIntroActivity.this.titleTextView.layout(0, i3, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i3);
                            i3 = (int) (0.477f * f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, i3, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i3);
                            i3 = (int) (0.537f * f);
                            ActionIntroActivity.this.descriptionText.layout(0, i3, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i3);
                            i5 = (i5 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                            i = (int) (f * 0.71f);
                            ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                            i5 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(0, i5, ActionIntroActivity.this.descriptionText2.getMeasuredWidth(), ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + i5);
                        }
                    } else if (i3 > i4) {
                        i2 = (i - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                        f2 = (float) i5;
                        i3 = ((int) ((0.5f * f2) - ((float) ActionIntroActivity.this.imageView.getMeasuredWidth()))) / 2;
                        ActionIntroActivity.this.imageView.layout(i3, i2, ActionIntroActivity.this.imageView.getMeasuredWidth() + i3, ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                        f3 = 0.4f * f2;
                        i2 = (int) f3;
                        f = (float) i;
                        i3 = (int) (0.14f * f);
                        ActionIntroActivity.this.titleTextView.layout(i2, i3, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i2, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i3);
                        i3 = (int) (0.31f * f);
                        ActionIntroActivity.this.descriptionText.layout(i2, i3, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i2, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i3);
                        i5 = (int) (f3 + (((f2 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                        i = (int) (f * 0.78f);
                        ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                    } else {
                        f = (float) i;
                        i2 = (int) (0.214f * f);
                        i3 = (i5 - ActionIntroActivity.this.imageView.getMeasuredWidth()) / 2;
                        ActionIntroActivity.this.imageView.layout(i3, i2, ActionIntroActivity.this.imageView.getMeasuredWidth() + i3, ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                        i2 = (int) (0.414f * f);
                        ActionIntroActivity.this.titleTextView.layout(0, i2, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i2);
                        i2 = (int) (0.493f * f);
                        ActionIntroActivity.this.descriptionText.layout(0, i2, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i2);
                        i5 = (i5 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        i = (int) (f * 0.71f);
                        ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                    }
                } else if (i3 > i4) {
                    i2 = (i - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                    ActionIntroActivity.this.imageView.layout(0, i2, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                    f2 = (float) i5;
                    f3 = 0.4f * f2;
                    i2 = (int) f3;
                    f = (float) i;
                    i3 = (int) (0.22f * f);
                    ActionIntroActivity.this.titleTextView.layout(i2, i3, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i2, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i3);
                    i3 = (int) (0.39f * f);
                    ActionIntroActivity.this.descriptionText.layout(i2, i3, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i2, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i3);
                    i5 = (int) (f3 + (((f2 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                    i = (int) (f * 0.69f);
                    ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                } else {
                    f = (float) i;
                    i2 = (int) (0.188f * f);
                    ActionIntroActivity.this.imageView.layout(0, i2, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                    i2 = (int) (0.651f * f);
                    ActionIntroActivity.this.titleTextView.layout(0, i2, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i2);
                    i2 = (int) (0.731f * f);
                    ActionIntroActivity.this.descriptionText.layout(0, i2, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i2);
                    i5 = (i5 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                    i = (int) (f * 0.853f);
                    ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                }
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(str));
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        viewGroup.setOnTouchListener(-$$Lambda$ActionIntroActivity$fQjBdUFaAXJ5YCul6b6pzBMDpBQ.INSTANCE);
        viewGroup.addView(this.actionBar);
        this.imageView = new ImageView(context);
        viewGroup.addView(this.imageView);
        this.titleTextView = new TextView(context);
        String str2 = "windowBackgroundWhiteBlackText";
        this.titleTextView.setTextColor(Theme.getColor(str2));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        viewGroup.addView(this.titleTextView);
        this.subtitleTextView = new TextView(context);
        this.subtitleTextView.setTextColor(Theme.getColor(str2));
        this.subtitleTextView.setGravity(1);
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
        this.descriptionText = new TextView(context);
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
        this.descriptionText2 = new TextView(context);
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
        this.buttonTextView = new TextView(context);
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        viewGroup.addView(this.buttonTextView);
        this.buttonTextView.setOnClickListener(new -$$Lambda$ActionIntroActivity$BcvFnoCk-mhvF1NmHV0oGHf_Czc(this));
        int i = this.currentType;
        TextView textView;
        if (i == 0) {
            this.imageView.setImageResource(NUM);
            this.imageView.setScaleType(ScaleType.FIT_CENTER);
            this.titleTextView.setText(LocaleController.getString("ChannelAlertTitle", NUM));
            this.descriptionText.setText(LocaleController.getString("ChannelAlertText", NUM));
            this.buttonTextView.setText(LocaleController.getString("ChannelAlertCreate2", NUM));
        } else if (i == 1) {
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 3));
            this.imageView.setScaleType(ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("PeopleNearby", NUM));
            this.descriptionText.setText(LocaleController.getString("PeopleNearbyAccessInfo", NUM));
            this.buttonTextView.setText(LocaleController.getString("PeopleNearbyAllowAccess", NUM));
        } else if (i == 2) {
            this.subtitleTextView.setVisibility(0);
            this.descriptionText2.setVisibility(0);
            this.imageView.setImageResource(Theme.getCurrentTheme().isDark() ? NUM : NUM);
            this.imageView.setScaleType(ScaleType.CENTER);
            textView = this.subtitleTextView;
            CharSequence charSequence = this.currentGroupCreateAddress;
            if (charSequence == null) {
                charSequence = "";
            }
            textView.setText(charSequence);
            this.titleTextView.setText(LocaleController.getString("NearbyCreateGroup", NUM));
            this.descriptionText.setText(LocaleController.getString("NearbyCreateGroupInfo", NUM));
            this.descriptionText2.setText(LocaleController.getString("NearbyCreateGroupInfo2", NUM));
            this.buttonTextView.setText(LocaleController.getString("NearbyStartGroup", NUM));
        } else if (i == 3) {
            this.subtitleTextView.setVisibility(0);
            this.drawable1 = context.getResources().getDrawable(NUM);
            this.drawable2 = context.getResources().getDrawable(NUM);
            this.drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image"), Mode.MULTIPLY));
            this.drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image2"), Mode.MULTIPLY));
            this.imageView.setImageDrawable(new CombinedDrawable(this.drawable1, this.drawable2));
            this.imageView.setScaleType(ScaleType.CENTER);
            textView = this.subtitleTextView;
            PhoneFormat instance = PhoneFormat.getInstance();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("+");
            stringBuilder.append(getUserConfig().getCurrentUser().phone);
            textView.setText(instance.format(stringBuilder.toString()));
            this.titleTextView.setText(LocaleController.getString("PhoneNumberChange2", NUM));
            this.descriptionText.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", NUM)));
            this.buttonTextView.setText(LocaleController.getString("PhoneNumberChange2", NUM));
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

    public void onLocationAddressAvailable(String str, Location location) {
        TextView textView = this.subtitleTextView;
        if (textView != null) {
            textView.setText(str);
            this.currentGroupCreateAddress = str;
            this.currentGroupCreateLocation = location;
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

    public void setGroupCreateAddress(String str, Location location) {
        this.currentGroupCreateAddress = str;
        this.currentGroupCreateLocation = location;
        if (location != null && str == null) {
            LocationController.fetchLocationAddress(location, this);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (!(i != 2 || iArr == null || iArr.length == 0)) {
            if (iArr[0] == 0) {
                presentFragment(new PeopleNearbyActivity(), true);
            } else if (getParentActivity() != null) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$ActionIntroActivity$VKxRXA-XbgTCJpZrpCKC8NHvoI4(this));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                showDialog(builder.create());
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

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[12];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarWhiteSelector");
        themeDescriptionArr[4] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[5] = new ThemeDescription(this.subtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[6] = new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6");
        themeDescriptionArr[7] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "featuredStickers_buttonText");
        themeDescriptionArr[8] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, null, null, null, null, "featuredStickers_addButton");
        themeDescriptionArr[9] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "featuredStickers_addButtonPressed");
        themeDescriptionArr[10] = new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, new Drawable[]{this.drawable1}, null, "changephoneinfo_image");
        themeDescriptionArr[11] = new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, new Drawable[]{this.drawable2}, null, "changephoneinfo_image2");
        return themeDescriptionArr;
    }
}
