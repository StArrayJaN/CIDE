package starray.android.cide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.jar.Attributes;

public class DisableScrollBSB<V extends View> extends BottomSheetBehavior<V> {

    public DisableScrollBSB(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull MotionEvent event) {
        child.onTouchEvent(event);
        return true;
    }


    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int axes) {
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes);
    }
}
