package com.a2driano.note100.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.a2driano.note100.R;

/**
 * Created by Andrii Papai on 16.05.2017.
 */

public class AnimationUtil {
    public static void visibleAnimationColorRectangle(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.visible_color_rectangle);
        view.startAnimation(animation);
    }

    public static void visibleAnimationColorTextDown(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.visible_color_text_rectangle_down);
        view.startAnimation(animation);
    }

    public static void visibleAnimationCheckBox(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.visible_checkbox);
        view.startAnimation(animation);
    }

    public static void visibleAnimationCheckBoxRevers(final View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.visible_checkbox_revers);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
//                view.setVisibility(View.GONE);
            }
        });
    }


    public static void visibleElements(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.visible_elements);
        view.startAnimation(animation);
    }

    public static void visibleElementAfterTransition(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.visible_element_after_transition);
        view.startAnimation(animation);
    }

    public static void hideElements(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.unvisible_elements);
        view.startAnimation(animation);

    }

    public static void visibleFab(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.visible_fab);
        view.startAnimation(animation);
    }

    public static void visibleFabOffset(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.visible_fab_offset_for_resume);
        view.startAnimation(animation);
    }

    public static void hideFab(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.unvisible_fab);
        view.startAnimation(animation);
    }

    public static void emptyViewAnimation(View view, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate_and_transition_animation);
//        animation.setRepeatCount(Animation.INFINITE);
//        animation.setRepeatCount(Animation.RESTART);
        view.startAnimation(animation);
    }


}
