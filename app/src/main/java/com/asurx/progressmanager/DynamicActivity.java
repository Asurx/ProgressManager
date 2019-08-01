package com.asurx.progressmanager;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DynamicActivity extends AppCompatActivity {
    private int getWorkId() {
        int mId = workId;
        if (!deletedWorkIdList.isEmpty()) {
            mId = deletedWorkIdList.get(0);
            deletedWorkIdList.remove(0);
            return mId;
        }else{
            return workId++;
        }
    }
    private int getViewId(){
        int mId = viewId;
        if (!deletedViewIdList.isEmpty()){
            viewId = deletedViewIdList.get(0);
            deletedViewIdList.remove(0);
            return mId;
        }else{
            return viewId++;
        }
    }

    private void addWork(MyWork mWork){
        works[mWork.getId()] = mWork;
        workIdList.add(mWork.getId());
//        createWorkView(works[mWork.id]);
//        addWorkView(workViews[mWork.getId()]);
    }
    private void delWork(int id){
        works[id] = null;
        for(int i=0;i<workIdList.size();i++){
            if(workIdList.get(i) == id)
                workIdList.remove(i);
        }
        deletedWorkIdList.add(id);
        llContainer.removeView(workViews[id].workViewContainer);
    }
    private MyWork getWork(int id){
        return works[id];
    }

    private MyWork[]     works      = new MyWork[100];
    private MyWorkView[] workViews  = new MyWorkView[100];
    private List<Integer> workIdList         = new ArrayList<Integer>();
    private List<Integer> deletedWorkIdList  = new ArrayList<Integer>();
    private int workId = 0;

    private List<Integer> deletedViewIdList  = new ArrayList<Integer>();
    private int viewId = 0;

    Context mContext;

    FloatingActionButton addButton;
    LinearLayout llContainer;
    Toolbar toolbar;

    private PopupWindow popWin = null; // 弹出窗口
    private View popView = null;
    Button btnAddAdd;
    EditText etAddName,etAddCurrent,etAddTotal,etAddWeight;

    private void init(){
        llContainer = findViewById(R.id.ll_container);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater inflater = LayoutInflater.from(mContext); // 取得LayoutInflater对象
        popView = inflater.inflate(R.layout.popwin_add, llContainer,false); // 读取布局管理器

        popWin = new PopupWindow(mContext);
        popWin.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        popWin.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        popWin.setContentView(popView);
        popWin.setFocusable(true);
        //***为其中各种控件设置监听***
        btnAddAdd = popView.findViewById(R.id.btn_add_add);
        etAddName = popView.findViewById(R.id.et_add_name);
        etAddName.setText("Project");
        etAddName.setInputType(InputType.TYPE_CLASS_TEXT);

        etAddCurrent = popView.findViewById(R.id.et_add_current);
        etAddCurrent.setInputType(InputType.TYPE_CLASS_NUMBER);
        etAddTotal   = popView.findViewById(R.id.et_add_total);
        etAddTotal.setInputType(InputType.TYPE_CLASS_NUMBER);
        etAddWeight  = popView.findViewById(R.id.et_add_weight);
        etAddWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
        /*
        etAddName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    String str = etAddName.getText().toString();
//                    if (str == "" ) etAddName.setText("Project");
                }catch (Exception e){
//                    etAddName.setText("Project");
                }
            }
        });
        etAddCurrent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    int num = Integer.parseInt(etAddCurrent.getText().toString());
                    if (num<0) etAddCurrent.setText("0");
                }catch (Exception e){
                    etAddCurrent.setText("0");
                }
            }
        });
        etAddTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    int num = Integer.parseInt(etAddTotal.getText().toString());
                    if (num<0) etAddTotal.setText("0");
                }catch (Exception e){
                    etAddTotal.setText("100");
                }
            }
        });
        etAddWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    int num = Integer.parseInt(etAddWeight.getText().toString());
                    if (num<1) etAddWeight.setText("1");
                }catch (Exception e){
                    etAddWeight.setText("1");
                }
            }
        });
        */
        //*
        btnAddAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name;
                int current,total,weight;
                try{
                    name = etAddName.getText().toString();
                }catch (Exception e){
                    name = "Project";
                }
                try{
                    current = Integer.parseInt(etAddCurrent.getText().toString());
                }catch (Exception e){
                    current = 0;
                }
                try{
                    total = Integer.parseInt(etAddTotal.getText().toString());
                }catch (Exception e){
                    total = 100;
                }
                try{
                    weight = Integer.parseInt(etAddWeight.getText().toString());
                }catch (Exception e){
                    weight = 1;
                }
                MyWork mWork = new MyWork(getWorkId(),name,total,current,weight);
                addWork(mWork);
            }
        });
//        ImageView close = popView.findViewById(R.id.iv_close);
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popWin.dismiss();
//            }
//        });

        // 设置PopupWindow的弹出和消失效果
        popWin.setAnimationStyle(R.style.popupAnimation);

        addButton = findViewById(R.id.fab_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWin.showAtLocation(addButton, Gravity.BOTTOM, 0, 0); // 显示弹出窗口

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


    }
    /**
    private void createWorkView(final MyWork mWork) {
        LinearLayout workViewContainer;
        RelativeLayout relativeLayout1,relativeLayout2;
        TextView tv_work,tv_current_1,tv_current_2,tv_total_1,tv_total_2;
        EditText et_current,et_total;
        Button btn_del,btn_reduce_current,btn_add_current,btn_reduce_total,btn_add_total;
        MyProgressView pv;

        workViewContainer = new LinearLayout(this);//主布局container
            relativeLayout1 = new RelativeLayout(this);
                tv_work  = new TextView(this);//名称tv
                btn_del  = new Button(this);
            pv = new MyProgressView(this);
            relativeLayout2 = new RelativeLayout(this);
                tv_current_1 = new TextView(this);
                tv_current_1.setText("Current: ");
                et_current = new EditText(this);
                tv_current_2 = new TextView(this);
                tv_current_2.setText("个单位");

                tv_total_1 = new TextView(this);
                tv_total_1.setText("Total: ");
                et_total = new EditText(this);
                tv_total_2 = new TextView(this);
                tv_total_2.setText("个单位");

                btn_reduce_current = new Button(this);
                btn_add_current   = new Button(this);
                btn_reduce_total  = new Button(this);
                btn_add_total     = new Button(this);

        workViewContainer.setId(getViewId());
            relativeLayout1.setId(getViewId());
                tv_work.setId(getViewId());
                tv_work.setText(mWork.name);
                btn_del.setId(getViewId());
                btn_del.setText("Del");
            pv.setId(getViewId());
            pv.setProgress(mWork.getProgress());
            relativeLayout2.setId(getViewId());
                tv_current_1.setId(getViewId());
                try {
                            et_current.setText(String.valueOf(mWork.current));
                        }catch (Exception e){

                        }
                et_current.setInputType(InputType.TYPE_CLASS_NUMBER);
                et_current.setId(getViewId());
                tv_current_2.setId(getViewId());

                tv_total_1.setId(getViewId());
                try {
                    et_total.setText(String.valueOf(mWork.total));
                }catch (Exception e){

                }
        et_total.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_total.setId(getViewId());
        tv_total_2.setId(getViewId());

                btn_reduce_current.setId(getViewId());
                btn_reduce_current.setText("-");
                btn_add_current.setId(getViewId());
                btn_add_current.setText("+");
                btn_reduce_total.setId(getViewId());
                btn_reduce_total.setText("-");
                btn_add_total.setId(getViewId());
                btn_add_total.setText("+");

        MyWorkView wv = new MyWorkView(mWork.id,workViewContainer,relativeLayout1,relativeLayout2,
                                       tv_work,tv_current_1,tv_current_2,tv_total_1,tv_total_2,
                                       et_current,et_total,
                                       btn_del,btn_reduce_current,btn_add_current,btn_reduce_total,btn_add_total,
                                       pv);
        workViews[mWork.id] = wv;
    }
    //*/
//
//    private void addWorkView(final MyWorkView mWorkView) {
//        final int mWorkId = mWorkView.id;
//        RelativeLayout.LayoutParams params;
//
//        // 设置workViewContainer的布局参数
//        mWorkView.workViewContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
//        LinearLayout.LayoutParams paramss = (LinearLayout.LayoutParams) mWorkView.workViewContainer.getLayoutParams();
////        paramss.width  = LinearLayout.LayoutParams.MATCH_PARENT;
////        paramss.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        paramss.topMargin = CalFun.dip2px(this,3);
//        mWorkView.workViewContainer.setLayoutParams(paramss);
//
//        mWorkView.workViewContainer.setBackgroundColor(Color.WHITE);
//        mWorkView.workViewContainer.setOrientation(LinearLayout.VERTICAL);
//        mWorkView.workViewContainer.setId(mWorkId);
//        /******************************************************************************************/
//        //设置第一部分relativeLayout1的布局参数
//        mWorkView.relativeLayout1.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,CalFun.dip2px(this,35)));
//        ////设置tv_work的布局参数
//        mWorkView.relativeLayout1.addView(mWorkView.tv_name);
//        params = (RelativeLayout.LayoutParams) mWorkView.tv_name.getLayoutParams();
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//与父控件左边界对齐
//        params.addRule(RelativeLayout.CENTER_VERTICAL);  //在父控件中垂直居中
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;     //设置宽高
//        params.leftMargin = CalFun.dip2px(this,10);//设置左边距
//        mWorkView.tv_name.setLayoutParams(params);
//        ////设置btn_del的布局参数
//        mWorkView.relativeLayout1.addView(mWorkView.btn_del);
//        params = (RelativeLayout.LayoutParams) mWorkView.btn_del.getLayoutParams();
//        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//与父控件右边界对齐
//        params.addRule(RelativeLayout.CENTER_VERTICAL);   //在父控件中垂直居中
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.height= CalFun.dip2px(this,60);      //设置宽高
//        params.rightMargin = CalFun.dip2px(this,10);//设置右边距
//        ////------为btn_del添加监听------////
//        mWorkView.btn_del.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                delWork(mWorkId);
//            }
//        });
//        mWorkView.btn_del.setLayoutParams(params);
//        //添加第一部分relativeLayout1
//        mWorkView.workViewContainer.addView(mWorkView.relativeLayout1);//*/
//        /*****************************************************************************************/
//        //设置第二部分progressView的布局参数
//        mWorkView.workViewContainer.addView(mWorkView.pv);
//        mWorkView.pv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,CalFun.dip2px(this,50)));//*/
//        /******************************************************************************************/
//        //设置第三部分relativeLayout2的布局参数
//        mWorkView.relativeLayout2.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,CalFun.dip2px(this,95)));
//        ////设置tv_current_1的布局参数
//        mWorkView.relativeLayout2.addView(mWorkView.tv_current_1);
//        params = (RelativeLayout.LayoutParams) mWorkView.tv_current_1.getLayoutParams();
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);  //与父控件左、上边界对齐
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;     //设置宽高
//        params.leftMargin = CalFun.dip2px(this,10);//设置左边距
//        mWorkView.tv_current_1.setLayoutParams(params);
//        ////设置et_current的布局参数
//        mWorkView.relativeLayout2.addView(mWorkView.et_current);
//        params = (RelativeLayout.LayoutParams) mWorkView.et_current.getLayoutParams();
//        params.addRule(RelativeLayout.RIGHT_OF,mWorkView.tv_current_1.getId());//位于tv_current_1右侧
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);                       //与父控件上边界对齐
//        params.width = CalFun.dip2px(this,60);
//        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;     //设置宽高
//        params.leftMargin = CalFun.dip2px(this,20);//设置左边距
//        mWorkView.et_current.setLayoutParams(params);
//        ////------为et_current添加监听------////
//        mWorkView.et_current.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {
//                try{
//                    int targetId = mWorkView.id;
//                    int num = Integer.parseInt(workViews[targetId].et_current.getText().toString());
//                    if (num<0) mWorkView.et_current.setText("0");
//                    works[targetId].getCurrent() = num;
//                    workViews[targetId].pv.setProgress(works[targetId].getProgress());
//                }catch (Exception e){
//
//                }
//            }
//        });
//        ////设置tv_current_2的布局参数
//        mWorkView.relativeLayout2.addView(mWorkView.tv_current_2);
//        params = (RelativeLayout.LayoutParams) mWorkView.tv_current_2.getLayoutParams();
//        params.addRule(RelativeLayout.RIGHT_OF,mWorkView.et_current.getId());  //位于et_current右侧
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);                       //与父控件上边界对齐
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;     //设置宽高
//        params.leftMargin = CalFun.dip2px(this,20);//设置左边距
//        mWorkView.tv_current_2.setLayoutParams(params);
//        //----------------------------------------------------------------------------------------//
//        ////设置tv_total_2的布局参数
//        mWorkView.relativeLayout2.addView(mWorkView.tv_total_2);
//        params = (RelativeLayout.LayoutParams) mWorkView.tv_total_2.getLayoutParams();
//        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.rightMargin = CalFun.dip2px(this,10);
//        mWorkView.tv_total_2.setLayoutParams(params);
//        ////设置et_total的布局参数
//        mWorkView.relativeLayout2.addView(mWorkView.et_total);
//        params = (RelativeLayout.LayoutParams) mWorkView.et_total.getLayoutParams();
//        params.addRule(RelativeLayout.LEFT_OF,mWorkView.tv_total_2.getId());
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        params.width = CalFun.dip2px(this,60);
//        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.rightMargin = CalFun.dip2px(this,20);
//        mWorkView.et_total.setLayoutParams(params);
//        ////------为et_total添加监听------////
//        mWorkView.et_total.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {
//                try{
//                    int targetId = mWorkView.id;
//                    int num = Integer.parseInt(workViews[targetId].et_total.getText().toString());
//                    if (num<0) mWorkView.et_total.setText("0");
//                    works[targetId].total = num;
//                    workViews[targetId].pv.setProgress(works[targetId].getProgress());
//                }catch (Exception e){
//                }
//            }
//        });
//        ////设置tv_total_1的布局参数
//        mWorkView.relativeLayout2.addView(mWorkView.tv_total_1);
//        params = (RelativeLayout.LayoutParams) mWorkView.tv_total_1.getLayoutParams();
//        params.addRule(RelativeLayout.LEFT_OF,mWorkView.et_total.getId());
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.rightMargin = CalFun.dip2px(this,20);
//        mWorkView.tv_total_1.setLayoutParams(params);
//        //----------------------------------------------------------------------------------------//
//        ////设置btn_reduce_current的布局参数
//        mWorkView.relativeLayout2.addView(mWorkView.btn_reduce_current);
//        params = (RelativeLayout.LayoutParams) mWorkView.btn_reduce_current.getLayoutParams();
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        params.width = CalFun.dip2px(this,40);
//        params.height= CalFun.dip2px(this,40);
//        params.leftMargin = CalFun.dip2px(this,10);
//        mWorkView.btn_reduce_current.setLayoutParams(params);
//        ////------为btn_reduce_current添加监听------////
//        mWorkView.btn_reduce_current.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int targetId = mWorkView.id;
//                int num = --works[targetId].current;
//                workViews[targetId].et_current.setText(String.valueOf(num));
//            }
//        });
//
//        ////设置btn_add_current的布局参数
//        mWorkView.relativeLayout2.addView(mWorkView.btn_add_current);
//        params = (RelativeLayout.LayoutParams) mWorkView.btn_add_current.getLayoutParams();
//        params.addRule(RelativeLayout.RIGHT_OF,mWorkView.btn_reduce_current.getId());
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        params.width = CalFun.dip2px(this,40);
//        params.height= CalFun.dip2px(this,40);
//        params.leftMargin = CalFun.dip2px(this,70);
//        mWorkView.btn_add_current.setLayoutParams(params);
//        ////------为btn_add_current添加监听------////
//        mWorkView.btn_add_current.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int targetId = mWorkView.id;
//                int num = ++works[targetId].current;
//                workViews[targetId].et_current.setText(String.valueOf(num));
//            }
//        });
//
//        ////设置btn_add_total的布局参数
//        mWorkView.relativeLayout2.addView(mWorkView.btn_add_total);
//        params = (RelativeLayout.LayoutParams) mWorkView.btn_add_total.getLayoutParams();
//        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        params.width = CalFun.dip2px(this,40);
//        params.height= CalFun.dip2px(this,40);
//        params.rightMargin = CalFun.dip2px(this,10);
//        mWorkView.btn_add_total.setLayoutParams(params);
//        ////------为btn_add_total添加监听------////
//        mWorkView.btn_add_total.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int targetId = mWorkView.id;
//                int num = ++works[targetId].total;
//                workViews[targetId].et_total.setText(String.valueOf(num));
//            }
//        });
//
//        ////设置btn_reduce_total的布局参数
//        mWorkView.relativeLayout2.addView(mWorkView.btn_reduce_total);
//        params = (RelativeLayout.LayoutParams) mWorkView.btn_reduce_total.getLayoutParams();
//        params.addRule(RelativeLayout.LEFT_OF,mWorkView.btn_add_total.getId());
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        params.width = CalFun.dip2px(this,40);
//        params.height= CalFun.dip2px(this,40);
//        params.rightMargin = CalFun.dip2px(this,80);
//        mWorkView.btn_reduce_total.setLayoutParams(params);
//        mWorkView.workViewContainer.addView(mWorkView.relativeLayout2);
//        ////------为btn_reduce_total添加监听------////
//        mWorkView.btn_reduce_total.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int targetId = mWorkView.id;
//                int num = --works[targetId].total;
//                workViews[targetId].et_total.setText(String.valueOf(num));
//            }
//        });
//
//        llContainer.addView(mWorkView.workViewContainer);
//    }

}

