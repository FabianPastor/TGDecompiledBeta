package org.telegram.ui;

import android.graphics.Rect;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.Components.GestureDetector2;
/* loaded from: classes3.dex */
public class LongPressListenerWithMovingGesture implements View.OnTouchListener {
    GestureDetector2 gestureDetector2;
    private int[] location;
    Rect rect = new Rect();
    private View selectedMenuView;
    boolean subItemClicked;
    ActionBarPopupWindow submenu;
    View view;

    public void onLongPress() {
        throw null;
    }

    public LongPressListenerWithMovingGesture() {
        GestureDetector2 gestureDetector2 = new GestureDetector2(new GestureDetector2.OnGestureListener() { // from class: org.telegram.ui.LongPressListenerWithMovingGesture.1
            @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                return false;
            }

            @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                return false;
            }

            @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
            public boolean onDown(MotionEvent motionEvent) {
                View view = LongPressListenerWithMovingGesture.this.view;
                if (view != null) {
                    view.setPressed(true);
                    LongPressListenerWithMovingGesture.this.view.setSelected(true);
                    int i = Build.VERSION.SDK_INT;
                    if (i >= 21) {
                        if (i == 21 && LongPressListenerWithMovingGesture.this.view.getBackground() != null) {
                            LongPressListenerWithMovingGesture.this.view.getBackground().setVisible(true, false);
                        }
                        LongPressListenerWithMovingGesture.this.view.drawableHotspotChanged(motionEvent.getX(), motionEvent.getY());
                    }
                }
                return true;
            }

            @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
            public void onUp(MotionEvent motionEvent) {
                View view = LongPressListenerWithMovingGesture.this.view;
                if (view != null) {
                    view.setPressed(false);
                    LongPressListenerWithMovingGesture.this.view.setSelected(false);
                    if (Build.VERSION.SDK_INT == 21 && LongPressListenerWithMovingGesture.this.view.getBackground() != null) {
                        LongPressListenerWithMovingGesture.this.view.getBackground().setVisible(false, false);
                    }
                }
                if (LongPressListenerWithMovingGesture.this.selectedMenuView != null) {
                    LongPressListenerWithMovingGesture longPressListenerWithMovingGesture = LongPressListenerWithMovingGesture.this;
                    if (longPressListenerWithMovingGesture.subItemClicked) {
                        return;
                    }
                    longPressListenerWithMovingGesture.selectedMenuView.callOnClick();
                    LongPressListenerWithMovingGesture.this.subItemClicked = true;
                }
            }

            @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                View view = LongPressListenerWithMovingGesture.this.view;
                if (view != null) {
                    view.callOnClick();
                    return false;
                }
                return false;
            }

            @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
            public void onLongPress(MotionEvent motionEvent) {
                LongPressListenerWithMovingGesture longPressListenerWithMovingGesture = LongPressListenerWithMovingGesture.this;
                if (longPressListenerWithMovingGesture.view != null) {
                    longPressListenerWithMovingGesture.onLongPress();
                }
            }
        });
        this.gestureDetector2 = gestureDetector2;
        this.location = new int[2];
        gestureDetector2.setIsLongpressEnabled(true);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        View view2;
        this.view = view;
        this.gestureDetector2.onTouchEvent(motionEvent);
        if (this.submenu != null && !this.subItemClicked && motionEvent.getAction() == 2) {
            this.view.getLocationOnScreen(this.location);
            float x = motionEvent.getX() + this.location[0];
            float y = motionEvent.getY() + this.location[1];
            this.submenu.getContentView().getLocationOnScreen(this.location);
            int[] iArr = this.location;
            float f = x - iArr[0];
            float f2 = y - iArr[1];
            this.selectedMenuView = null;
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = (ActionBarPopupWindow.ActionBarPopupWindowLayout) this.submenu.getContentView();
            for (int i = 0; i < actionBarPopupWindowLayout.getItemsCount(); i++) {
                View itemAt = actionBarPopupWindowLayout.getItemAt(i);
                itemAt.getHitRect(this.rect);
                itemAt.getTag();
                if (itemAt.getVisibility() == 0 && itemAt.isClickable()) {
                    if (!this.rect.contains((int) f, (int) f2)) {
                        itemAt.setPressed(false);
                        itemAt.setSelected(false);
                        if (Build.VERSION.SDK_INT == 21 && itemAt.getBackground() != null) {
                            itemAt.getBackground().setVisible(false, false);
                        }
                    } else {
                        itemAt.setPressed(true);
                        itemAt.setSelected(true);
                        int i2 = Build.VERSION.SDK_INT;
                        if (i2 >= 21) {
                            if (i2 == 21 && itemAt.getBackground() != null) {
                                itemAt.getBackground().setVisible(true, false);
                            }
                            itemAt.drawableHotspotChanged(f, f2 - itemAt.getTop());
                        }
                        this.selectedMenuView = itemAt;
                    }
                }
            }
        }
        if (motionEvent.getAction() == 1 && !this.subItemClicked && (view2 = this.selectedMenuView) != null) {
            view2.callOnClick();
            this.subItemClicked = true;
        }
        return true;
    }

    public void setSubmenu(ActionBarPopupWindow actionBarPopupWindow) {
        this.submenu = actionBarPopupWindow;
    }
}
