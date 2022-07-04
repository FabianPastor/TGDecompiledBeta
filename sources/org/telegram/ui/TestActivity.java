package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.VideoCapturer;

public class TestActivity extends BaseFragment {
    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle("Test");
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    TestActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(-1);
        this.fragmentView = frameLayout;
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
    }

    private void hangup() {
    }

    private static VideoCapturer createVideoCapturer(Context context) {
        CameraEnumerator enumerator = Camera2Enumerator.isSupported(context) ? new Camera2Enumerator(context) : new Camera1Enumerator();
        return enumerator.createCapturer(enumerator.getDeviceNames()[0], (CameraVideoCapturer.CameraEventsHandler) null);
    }
}
