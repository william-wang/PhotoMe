package com.example.photome.editor.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

import com.example.photome.R;

/**
 * 1. crete PopupWindow object;
 * 2. set the background, register event listener and add animation;
 * 3. show PopupWindow.
 */
public class MateInfoWindow extends PopupWindow {

    private static String TAG = MateInfoWindow.class.getSimpleName();

    private View mView;

    private Context mContext;

    private PopupWindow mPopWindow;

    private Window mWindow;

    public MateInfoWindow(Context context, View view, Window window) {
        this.mView = view;
        this.mContext = context;
        this.mWindow = window;

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.editor_mateinfo_window, null);
        mPopWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

        initWindow();
    }

    protected void initWindow() {
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopWindow.setAnimationStyle(R.style.animWindowTranslate);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setTouchable(true);

        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mWindow.getAttributes();
                lp.alpha = 1.0f;
                mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mWindow.setAttributes(lp);
            }
        });
    }

    public void Dismiss() {
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.alpha = 0.5f;
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mWindow.setAttributes(lp);
    }

    public void showBashOfAnchor(View anchor, LayoutGravity layoutGravity, int xmerge, int ymerge) {
        int[] offset = layoutGravity.getOffset(anchor, mPopWindow);
        mPopWindow.showAsDropDown(anchor, offset[0] + xmerge, offset[1] + ymerge);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        mPopWindow.showAsDropDown(anchor, xoff, yoff);
    }

    public void showAtLocation(int gravity, int x, int y) {
        // mPopWindow.showAtLocation(mView, Gravity.BOTTOM, 0, 0);
        mPopWindow.showAtLocation(mView, gravity, x, y);
    }

    public View getContentView() {
        return this.mView;
    }

    public PopupWindow getPopupWindow() {
        return this.mPopWindow;
    }


    public static class LayoutGravity {
        private int layoutGravity;
        // waring, don't change the order of these constants!
        public static final int ALIGN_LEFT = 0x1;
        public static final int ALIGN_ABOVE = 0x2;
        public static final int ALIGN_RIGHT = 0x4;
        public static final int ALIGN_BOTTOM = 0x8;
        public static final int TO_LEFT = 0x10;
        public static final int TO_ABOVE = 0x20;
        public static final int TO_RIGHT = 0x40;
        public static final int TO_BOTTOM = 0x80;
        public static final int CENTER_HORI = 0x100;
        public static final int CENTER_VERT = 0x200;

        public LayoutGravity(int gravity) {
            layoutGravity = gravity;
        }

        public int getLayoutGravity() {
            return layoutGravity;
        }

        public void setLayoutGravity(int gravity) {
            layoutGravity = gravity;
        }

        public void setHoriGravity(int gravity) {
            layoutGravity &= (0x2 + 0x8 + 0x20 + 0x80 + 0x200);
            layoutGravity |= gravity;
        }

        public void setVertGravity(int gravity) {
            layoutGravity &= (0x1 + 0x4 + 0x10 + 0x40 + 0x100);
            layoutGravity |= gravity;
        }

        public boolean isParamFit(int param) {
            return (layoutGravity & param) > 0;
        }

        public int getHoriParam() {
            for (int i = 0x1; i <= 0x100; i = i << 2)
                if (isParamFit(i))
                    return i;
            return ALIGN_LEFT;
        }

        public int getVertParam() {
            for (int i = 0x2; i <= 0x200; i = i << 2)
                if (isParamFit(i))
                    return i;
            return TO_BOTTOM;
        }

        public int[] getOffset(View anchor, PopupWindow window) {
            int anchWidth = anchor.getWidth();
            int anchHeight = anchor.getHeight();

            int winWidth = window.getWidth();
            int winHeight = window.getHeight();
            View view = window.getContentView();
            if (winWidth <= 0)
                winWidth = view.getWidth();
            if (winHeight <= 0)
                winHeight = view.getHeight();

            int xoff = 0;
            int yoff = 0;

            switch (getHoriParam()) {
                case ALIGN_LEFT:
                    xoff = 0;
                    break;
                case ALIGN_RIGHT:
                    xoff = anchWidth - winWidth;
                    break;
                case TO_LEFT:
                    xoff = -winWidth;
                    break;
                case TO_RIGHT:
                    xoff = anchWidth;
                    break;
                case CENTER_HORI:
                    xoff = (anchWidth - winWidth) / 2;
                    break;
                default:
                    break;
            }
            switch (getVertParam()) {
                case ALIGN_ABOVE:
                    yoff = -anchHeight;
                    break;
                case ALIGN_BOTTOM:
                    yoff = -winHeight;
                    break;
                case TO_ABOVE:
                    yoff = -anchHeight - winHeight;
                    break;
                case TO_BOTTOM:
                    yoff = 0;
                    break;
                case CENTER_VERT:
                    yoff = (-winHeight - anchHeight) / 2;
                    break;
                default:
                    break;
            }
            return new int[]{xoff, yoff};
        }
    }

}
