package com.mikeroll.dreadnote.frontend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import com.mikeroll.dreadnote.R;

public class ColorChooser extends PopupWindow {

    private Context mContext;
    private OnColorChooseListener mOnColorChooseListener;
    private RadioButton mCheckedButton;
    private OnColorButtonCheckedChangeListener mOnCheckedListener = new OnColorButtonCheckedChangeListener();
    private LinearLayout[] rows = new LinearLayout[2];

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("InflateParams")
    public ColorChooser(Context context) {
        super(context);
        mContext = context;
        LinearLayout table = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.popup_color_chooser, null);
        rows[0] = (LinearLayout) table.findViewById(R.id.color_row0);
        rows[1] = (LinearLayout) table.findViewById(R.id.color_row1);
        TypedArray colors = context.getResources().obtainTypedArray(R.array.note_colors);
        if (colors != null) {
            for (int i = 0; i < colors.length() / 2; i++)
                rows[0].addView(newButton(colors.getColor(i, android.R.color.white)));
            for (int i = colors.length() / 2; i < colors.length(); i++)
                rows[1].addView(newButton(colors.getColor(i, android.R.color.white)));
        }
        setContentView(table);
        setWindowLayoutMode(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        setFocusable(true);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("InflateParams")
    private RadioButton newButton(int color) {
        RadioButton btn = (RadioButton)LayoutInflater.from(mContext).inflate(R.layout.color_button, null);
        btn.setBackgroundColor(color);
        btn.setOnCheckedChangeListener(mOnCheckedListener);
        return btn;
    }

    public void readCurrentColor(int color) {
        for (LinearLayout row : rows) {
            for (int i = 0; i < row.getChildCount(); i++) {
                RadioButton btn = (RadioButton)row.getChildAt(i);
                //noinspection ConstantConditions
                if (color == ((ColorDrawable)btn.getBackground()).getColor()) {
                    btn.setChecked(true);
                    return;
                }
            }
        }
    }

    private boolean mProtectFromRecursion = false;

    public OnColorChooseListener getOnColorChooseListener() {
        return mOnColorChooseListener;
    }

    public void setOnColorChooseListener(OnColorChooseListener listener) {
        this.mOnColorChooseListener = listener;
    }

    interface OnColorChooseListener {
        void onColorChoose(int color);
    }

    private class OnColorButtonCheckedChangeListener implements RadioButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if (checked) {
                if (mProtectFromRecursion) {
                    return;
                }
                mProtectFromRecursion = true;
                if (mCheckedButton != null) {
                    mCheckedButton.setChecked(false);
                }
                mProtectFromRecursion = false;
                mCheckedButton = (RadioButton)compoundButton;

                //noinspection ConstantConditions
                int color = ((ColorDrawable)compoundButton.getBackground()).getColor();
                if (mOnColorChooseListener != null) {
                    mOnColorChooseListener.onColorChoose(color);
                }
            }
        }
    }
}
