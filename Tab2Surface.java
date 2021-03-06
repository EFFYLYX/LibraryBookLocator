package com.example.chirag.librarybooklocator;

/**
 * Created by effy on 2018/4/23.
 */


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by jo on 2018/4/13.
 */

public class Tab2Surface extends GLSurfaceView {
    private Render_firstmap mRenderer;
    //private Render_secondmap mRenderer2;
    private Render_secondmap mRenderer2;

    // Offsets for touch events
    private volatile float mPreviousX;
    private volatile float mPreviousY;

    private float mDensity;

    public Tab2Surface(Context context) {
        super(context);
    }

    public Tab2Surface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (mRenderer != null) {
                    float deltaX = (x - mPreviousX) / mDensity / 2f;
                    float deltaY = (y - mPreviousY) / mDensity / 2f;

                    mRenderer.mDeltaX += deltaX;
                    mRenderer.mDeltaY += deltaY;
                }
            }

            mPreviousX = x;
            mPreviousY = y;

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    // Hides superclass method.
    public void setRenderer(Render_firstmap renderer, float density) {
        mRenderer = renderer;
        mDensity = density;
        super.setRenderer(renderer);
    }

    public void setRenderer2(Render_secondmap renderer, float density) {
        mRenderer2 = renderer;
        mDensity = density;
        super.setRenderer(renderer);
    }

}
