package com.asurx.progressmanager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyWorkView {
    int id;

    LinearLayout      workViewContainer;
    RelativeLayout    relativeLayout1,relativeLayout2;

    TextView          tv_name,tv_current_1,tv_current_2,tv_total_1,tv_total_2,tv_weight;
    EditText          et_current,et_total;
    Button            btn_del,btn_reduce_current,btn_add_current,btn_reduce_total,btn_add_total;
    MyProgressView    pv;

    MyWorkView(int id, LinearLayout workViewContainer,
               RelativeLayout relativeLayout1,    MyProgressView pv,    RelativeLayout relativeLayout2,
               TextView tv_name, TextView tv_weight, Button btn_del,
               TextView tv_current_1,    EditText et_current,    TextView tv_current_2,
               TextView tv_total_1,      EditText et_total,      TextView tv_total_2,
               Button btn_reduce_current,    Button btn_add_current,
               Button btn_reduce_total,      Button btn_add_total){
        this.id = id;
        this.workViewContainer = workViewContainer;

        this.relativeLayout1 = relativeLayout1;
        this.pv = pv;
        this.relativeLayout2 = relativeLayout2;

        this.tv_name   = tv_name;
        this.tv_weight = tv_weight;
        this.btn_del   = btn_del;

        this.tv_current_1 = tv_current_1;
        this.et_current = et_current;
        this.tv_current_2 = tv_current_2;
                this.tv_total_1 = tv_total_1;
        this.et_total = et_total;
        this.tv_total_2 = tv_total_2;

        this.btn_reduce_current = btn_reduce_current;
        this.btn_add_current = btn_add_current;

        this.btn_reduce_total = btn_reduce_total;
        this.btn_add_total = btn_add_total;
    }
}
