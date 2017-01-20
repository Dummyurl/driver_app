package com.rideeaze.views.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.rideeaze.R;

/**
 * Created by adventis on 2/3/15.
 */
public class CustomPopup extends PopupWindow {

    OnCustomPopupWindowDismiss mDismissListener;
    public interface OnCustomPopupWindowDismiss{
        public void onDismiss();
    }
    public void setCustomPopupWindowDismiss(OnCustomPopupWindowDismiss eventListener) {
        mDismissListener=eventListener;
    }

    OnCustomPopupWindowSelectitem mSelectListener;
    public interface OnCustomPopupWindowSelectitem{
        public void onSelectItem(int id);
    }
    public void setCustomPopupWindowSelectitem(OnCustomPopupWindowSelectitem eventListener) {
        mSelectListener=eventListener;
    }

    public static CustomPopup lastPopupMenu = null;
    Context	m_context;

    LinearLayout listView;

    public CustomPopup(Context context) {
        super(context);

        m_context = context;

        setContentView(LayoutInflater.from(context).inflate(R.layout.custom_popup_window, null));

        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        this.setBackgroundDrawable(new BitmapDrawable());
        getContentView().setBackgroundColor(Color.TRANSPARENT);
        setOutsideTouchable(true);
        setFocusable(true);

        listView = (LinearLayout) getContentView().findViewById(R.id.listView);
        listView.setBackgroundColor(Color.TRANSPARENT);

    }

    public void setData(Integer[] imgArray, View parent) {
        setWidth(parent.getWidth());

        for(int i=0; i<imgArray.length; i++) {
            ImageView view = new ImageView(m_context);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(parent.getWidth(), parent.getHeight());
            if(i!=0) {
                param.setMargins(0, 10, 0, 0);
            }
            view.setLayoutParams(param);
            view.setImageResource(imgArray[i]);

            /*if (i == 0) {
                view.setBackgroundResource(R.drawable.//vehicle_classfield);
            } else if (i == imgArray.length-1) {
                view.setBackgroundResource(R.drawable.//vehicle_classfield);
            } else {
                view.setBackgroundResource(R.drawable.vehicle_classfield);
            }*/

            view.setBackgroundResource(R.drawable.vehicle_classfield);
           // view.setPadding(10, 10, 10, 10);
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);

            final CustomPopup _this = this;
            final int selectedItem = imgArray[i];
            Log.d("INDEX", imgArray[i]+"");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSelectListener!=null) {
                        Log.d("INDEX", "click: "+selectedItem+"");
                        mSelectListener.onSelectItem(selectedItem);
                    }
                    if(mDismissListener!=null) {
                        _this.dismiss();
                        mDismissListener.onDismiss();
                    }

                }
            });

            listView.addView(view);
        }
    }

    public void show(View anchor)
    {
        if(CustomPopup.lastPopupMenu != null) {
            try {
                CustomPopup.lastPopupMenu.dismiss();
            }catch (Exception e) {

            }
        }
        showAtLocation(anchor, Gravity.LEFT, getRelativeLeft(anchor), 0);
        CustomPopup.lastPopupMenu = this;
        Log.d("Popup", "X: "+getRelativeLeft(anchor)+"; Y: "+getRelativeTop(anchor)+"; W: "+anchor.getWidth()+"; H: "+anchor.getHeight()+";");

        //showAsDropDown(anchor,0,0,Gravity.CENTER_VERTICAL);
    }

    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
}
