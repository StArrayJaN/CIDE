package starray.android.cide.cpp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import starray.android.cide.APPUtils;
import starray.android.cide.R;

public class BaseBSD extends BottomSheetDialogFragment {

    Dialog dialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.dialog = super.onCreateDialog(savedInstanceState);
        View v = getActivity().findViewById(R.id.ll_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(v);
        behavior.setPeekHeight(APPUtils.dip2px(APPUtils.getMainActivity(),50));
        dialog.setCanceledOnTouchOutside(false);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        return dialog;
    }
    //收缩而不影响其他view


}
