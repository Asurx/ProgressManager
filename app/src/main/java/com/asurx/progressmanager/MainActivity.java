package com.asurx.progressmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
//import android.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private Toolbar toolbar;
    //popWin部分
    private FloatingActionButton fabAdd;
    private View popView;
    private PopupWindow popWin;
    private boolean isPwShowing = false;
    private Button btnPwAdd;
    private EditText etPwName,etPwCurrent,etPwTotal,etPwWeight;
    //数据部分
    private MyWork[]     works      = new MyWork[100];
    private MyWorkView[] workViews  = new MyWorkView[100];
    private List<Integer> workIdList         = new ArrayList<Integer>();
    private List<Integer> deletedWorkIdList  = new ArrayList<Integer>();
    private int workId = 0;
    private LinearLayout llContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        init();

        initDBWork();
        updatePbUnit();

        initPopWin();
        setSupportActionBar(toolbar);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabAdd.setTranslationY(CalFun.dip2px(mContext,-100));
                fabAdd.setRotation(45);
                popWin.showAsDropDown(findViewById(R.id.fab_add),0,50);
                isPwShowing = true;
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
//    private SharedPreferences mSp;
    private void initWorkIdData(){
        int i,id=0;
        for(i=0;i<workIdList.size();id=workIdList.get(i),i++){
            if (workId < id){
                workId = id;
            }
        }
        workId++;
        for(i = workId-1;i >= 0;i--){
            if(!workIdList.contains(i)){
                deletedWorkIdList.add(i);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //--------------------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------------------//
    private MyProgressView pb;
    private void updatePbUnit(){
        int totalUnit = 0;
        int currentUnit = 0;
        for(int i=0;i <= workId; i++){
            if(workIdList.contains(i)){
                Log.e("update pb"," " + i);
                totalUnit   += works[i].total * works[i].weight;
                currentUnit += works[i].current * works[i].weight;
            }
        }

        pb.setProgress(((float)currentUnit/totalUnit));
    }

    //--------------------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------------------//

    private void init(){
        toolbar = findViewById(R.id.toolbar);
        fabAdd = findViewById(R.id.fab_add);
        llContainer = findViewById(R.id.ll_container);

        pb = findViewById(R.id.pb);
    }


    /**********************************************************************************************/

    private static String DB_NAME = "myWorks.db";
    private static int DB_VERSION = 1;

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    //private SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.myapplication/databases/stu.db",null); ;
    private Cursor cursor;

    private void printDBWork(){
        String str = "";
        cursor = db.query(DBHelper.TB_NAME, null, null, null, null, null, null);
        while(cursor.moveToNext()){
            str += "Id: ";
            str +=  String.valueOf(cursor.getInt(0));
            str += " \tN: ";
            str +=  String.valueOf(cursor.getInt(1));
            str += " \tName: ";
            str += cursor.getString(2);
            str += " \tC: ";
            str +=  String.valueOf(cursor.getInt(3));
            str += " \tT: ";
            str +=  String.valueOf(cursor.getInt(4));
            str += " \tW: ";
            str +=  String.valueOf(cursor.getInt(5));
            str = str + "\n";
        }
        cursor.close();
        Toast toast=Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void initDBWork(){
        try{
            /* 初始化并创建数据库 */
            dbHelper = new DBHelper(mContext, DB_NAME, null, DB_VERSION);
            /* 创建表 */
            db = dbHelper.getWritableDatabase();    //调用SQLiteHelper.OnCreate()
            /* 查询表，得到cursor对象 */
            cursor = db.query(DBHelper.TB_NAME, null, null, null, null, null, null);
            while(cursor.moveToNext()){
                MyWork work = new MyWork();
                work.setId(cursor.getInt(1));
                work.setName(cursor.getString(2));
                work.setCurrent(cursor.getInt(3));
                work.setTotal(cursor.getInt(4));
                work.setWeight(cursor.getInt(5));;
                addWork(work);
                workIdList.add(work.getId());
            }
            cursor.close();
        }catch(IllegalArgumentException e){
            //当用SimpleCursorAdapter装载数据时，表ID列必须是_id，否则报错column '_id' does not exist
            e.printStackTrace();
            //当版本变更时会调用SQLiteHelper.onUpgrade()方法重建表 注：表以前数据将丢失
            ++ DB_VERSION;
            dbHelper.onUpgrade(db, --DB_VERSION, DB_VERSION);
//            dbHelper.updateColumn(db, SQLiteHelper.ID, "_"+SQLiteHelper.ID, "integer");
        }
        initWorkIdData();
    }

    /**********************************************************************************************/

    private int requestWorkId(){
        int requestId;
        if (!deletedWorkIdList.isEmpty()){       /**有中间删除掉的Id**/
            requestId = deletedWorkIdList.get(0);   //获取deletedWorkIdList中第一个Id
            deletedWorkIdList.remove(0);         //从deletedWorkIdList中删除此Id
        }else{
            requestId = workId++;                     /**没有中间删除掉的Id, 顺序分配**/
        }
        workIdList.add(requestId);
        return requestId;
    }
    private int requestViewId(int mWorkId, String viewName){
        return 0;
    }

    private void addWork(MyWork mWork){
        int mWorkId = mWork.getId();
        works[mWorkId] = mWork;
        workIdList.add(mWorkId);
//        totalUnit   += mWork.getTotal()   * mWork.getWeight();
//        currentUnit += mWork.getCurrent() * mWork.getWeight();
        updatePbUnit();

        generateWorkView(mWorkId);
        initWorkView(mWorkId);
    }
    private void delWork(int id){
        while(workIdList.contains(id)) {
            for (int i = 0; i < workIdList.size(); i++) {
                if (workIdList.get(i) == id) {
                    workIdList.remove(i);

                    Log.e("delWork", "Deleted index: " + i + "\tid: " + id);
                }
            }
        }
        deletedWorkIdList.add(id);
        Log.e("delWork","" + workIdList.contains(id));
        updatePbUnit();
        llContainer.removeView(workViews[id].workViewContainer);
        String whereClause = "NUMBER = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        db.delete(DBHelper.TB_NAME, whereClause, whereArgs);
    }
    private void updateWork(int id){
        int current = -1;
        int total   = -1;
        int weight  = -1;
        try {
            current = works[id].getCurrent();
            total   = works[id].getTotal();
            weight  = works[id].getWeight();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.execSQL("UPDATE " + DBHelper.TB_NAME +
                " SET CURRENT = " + current +
                ", TOTAL = " + total +
                ", WEIGHT = " + weight +
                " WHERE NUMBER = " + id + ";");
    }

    private void test(){
        MyWork work1 = new MyWork(requestWorkId(),"NB",60,27);
        MyWork work2 = new MyWork(requestWorkId(),"SB",70,47);
        addWork(work1);
        addWork(work2);
    }


    private void generateWorkView(int mWorkId){
        MyWork mWork = works[mWorkId];

        LinearLayout      workViewContainer;
        RelativeLayout    relativeLayout1,relativeLayout2;

        TextView          tv_name,tv_current_1,tv_current_2,tv_total_1,tv_total_2,tv_weight;
        EditText          et_current,et_total;
        Button            btn_del,btn_reduce_weight,btn_add_weight,btn_reduce_current,btn_add_current,btn_reduce_total,btn_add_total;
        MyProgressView    pv;
    //--------------------------------------------------------------------------------------------//
        workViewContainer = new LinearLayout(mContext);//主布局container

        relativeLayout1 = new RelativeLayout(mContext);
        pv = new MyProgressView(mContext);
        relativeLayout2 = new RelativeLayout(mContext);

        tv_name   = new TextView(mContext);//名称tv


        btn_reduce_weight = new Button(mContext);
        tv_weight = new TextView(mContext);
        btn_add_weight = new Button(mContext);

        btn_del   = new Button(mContext);


        tv_current_1 = new TextView(mContext);
        et_current = new EditText(mContext);
        tv_current_2 = new TextView(mContext);
        tv_total_1 = new TextView(mContext);
        et_total = new EditText(mContext);
        tv_total_2 = new TextView(mContext);

        btn_reduce_current = new Button(mContext);
        btn_add_current    = new Button(mContext);
        btn_reduce_total   = new Button(mContext);
        btn_add_total      = new Button(mContext);
        ////////////////////////////////////////////////////////////////////////////////////////
        workViewContainer.setId(mWorkId);

        relativeLayout1.setId(mWorkId + 1);
        pv.setId(mWorkId + 7);
        relativeLayout2.setId(mWorkId + 8);

        tv_name.setId(mWorkId + 2);
        btn_reduce_weight.setId(mWorkId + 3);
        tv_weight.setId(mWorkId + 4);
        btn_add_weight.setId(mWorkId + 5);
        btn_del.setId(mWorkId + 6);

        tv_current_1.setId(mWorkId + 8);
        et_current.setId(mWorkId + 9);
        tv_current_2.setId(mWorkId + 11);
        tv_total_1.setId(mWorkId + 12);
        et_total.setId(mWorkId + 13);
        tv_total_2.setId(mWorkId + 14);

        btn_reduce_current.setId(mWorkId + 15);
        btn_add_current.setId(mWorkId + 16);
        btn_reduce_total.setId(mWorkId + 17);
        btn_add_total.setId(mWorkId + 18);

        pv.setProgress(mWork.getProgress());

        tv_name.setText("Name: " + mWork.getName());
        btn_reduce_weight.setText("-");
        try{ tv_weight.setText("Weight: " + String.valueOf(mWork.getWeight()));}  catch (Exception e){}
        btn_add_weight.setText("+");
        btn_del.setText("Del");

        tv_current_1.setText("Current: ");
        et_current.setInputType(InputType.TYPE_CLASS_NUMBER);
        try{ et_current.setText(String.valueOf(mWork.getCurrent()));}catch (Exception e){}
        tv_current_2.setText("个单位");
        tv_total_1.setText("Total: ");
        et_total.setInputType(InputType.TYPE_CLASS_NUMBER);
        try{ et_total.setText(String.valueOf(mWork.getTotal()));
        Log.e("getTotal",String.valueOf(mWork.getTotal()));}    catch (Exception e){}
        tv_total_2.setText("个单位");

        btn_reduce_current.setText("-");
        btn_add_current.setText("+");
        btn_reduce_total.setText("-");
        btn_add_total.setText("+");
        ////////////////////////////////////////////////////////////////////////////////////////

        MyWorkView wv = new MyWorkView(mWork.getId(),workViewContainer,
                relativeLayout1, pv, relativeLayout2,
                tv_name,
                btn_reduce_weight, tv_weight, btn_add_weight,
                btn_del,
                tv_current_1, et_current,    tv_current_2,
                tv_total_1,   et_total,      tv_total_2,
                btn_reduce_current, btn_add_current, btn_reduce_total, btn_add_total);
        workViews[mWork.getId()] = wv;
    }
    private void initWorkView(final int mWorkId){
        final MyWorkView mWorkView = workViews[mWorkId];

        RelativeLayout.LayoutParams params;

        // 设置workViewContainer的布局参数
        mWorkView.workViewContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams paramsS = (LinearLayout.LayoutParams) mWorkView.workViewContainer.getLayoutParams();
        paramsS.topMargin = CalFun.dip2px(this,3);
        mWorkView.workViewContainer.setLayoutParams(paramsS);
        mWorkView.workViewContainer.setBackgroundColor(Color.WHITE);
        mWorkView.workViewContainer.setOrientation(LinearLayout.VERTICAL);
        mWorkView.workViewContainer.setId(mWorkId);
        /******************************************************************************************/
        //设置第一部分relativeLayout1的布局参数
        mWorkView.relativeLayout1.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,CalFun.dip2px(this,35)));
        //--------设置tv_work的布局参数--------//
        mWorkView.relativeLayout1.addView(mWorkView.tv_name);
        params = (RelativeLayout.LayoutParams) mWorkView.tv_name.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//与父控件左边界对齐
        params.addRule(RelativeLayout.CENTER_VERTICAL);  //在父控件中垂直居中
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;     //设置宽高
        params.leftMargin = CalFun.dip2px(this,10);//设置左边距
        mWorkView.tv_name.setLayoutParams(params);
        //--------设置btn_reduce_weight的布局参数--------//
        mWorkView.relativeLayout1.addView(mWorkView.btn_reduce_weight);
        params = (RelativeLayout.LayoutParams) mWorkView.btn_reduce_weight.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);   //在父控件中垂直居中
        params.addRule(RelativeLayout.LEFT_OF,mWorkView.tv_weight.getId());
        params.width = CalFun.dip2px(this,40);
        params.height= CalFun.dip2px(this,40);      //设置宽高
        params.rightMargin = CalFun.dip2px(this,10);//设置右边距
        //--------为btn_reduce_weight添加监听--------//
        mWorkView.btn_reduce_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int targetId = mWorkView.id;
                int num = works[targetId].getWeight() - 1;
                works[targetId].setWeight(num);
                updateWork(targetId);
                updatePbUnit();
                workViews[targetId].tv_weight.setText("Weight: " + String.valueOf(num));
            }
        });
        mWorkView.btn_reduce_weight.setLayoutParams(params);
        //--------设置tv_weight的布局参数--------//
        mWorkView.relativeLayout1.addView(mWorkView.tv_weight);
        params = (RelativeLayout.LayoutParams) mWorkView.tv_weight.getLayoutParams();
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_VERTICAL);  //在父控件中居中
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;//设置宽高
        mWorkView.tv_weight.setLayoutParams(params);
        //--------设置btn_add_weight的布局参数--------//
        mWorkView.relativeLayout1.addView(mWorkView.btn_add_weight);
        params = (RelativeLayout.LayoutParams) mWorkView.btn_add_weight.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);   //在父控件中垂直居中
        params.addRule(RelativeLayout.RIGHT_OF,mWorkView.tv_weight.getId());
        params.width = CalFun.dip2px(this,40);
        params.height= CalFun.dip2px(this,40);      //设置宽高
        params.leftMargin = CalFun.dip2px(this,10);//设置左边距
        //--------为btn_reduce_weight添加监听--------//
        mWorkView.btn_add_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int targetId = mWorkView.id;
                int num = works[targetId].getWeight() + 1;
                works[targetId].setWeight(num);
                updateWork(targetId);
                updatePbUnit();
                workViews[targetId].tv_weight.setText("Weight: " + String.valueOf(num));
            }
        });
        mWorkView.btn_add_weight.setLayoutParams(params);
        //--------设置btn_del的布局参数--------//
        mWorkView.relativeLayout1.addView(mWorkView.btn_del);
        params = (RelativeLayout.LayoutParams) mWorkView.btn_del.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//与父控件右边界对齐
        params.addRule(RelativeLayout.CENTER_VERTICAL);   //在父控件中垂直居中
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height= CalFun.dip2px(this,60);      //设置宽高
        params.rightMargin = CalFun.dip2px(this,10);//设置右边距
        //--------为btn_del添加监听--------//
        mWorkView.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delWork(mWorkId);
            }
        });
        mWorkView.btn_del.setLayoutParams(params);
        //添加第一部分relativeLayout1
        mWorkView.workViewContainer.addView(mWorkView.relativeLayout1);//*/
        /*****************************************************************************************/
        //设置第二部分progressView的布局参数
        mWorkView.workViewContainer.addView(mWorkView.pv);
        mWorkView.pv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,CalFun.dip2px(this,50)));//*/
        /******************************************************************************************/
        //设置第三部分relativeLayout2的布局参数
        mWorkView.relativeLayout2.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,CalFun.dip2px(this,95)));
        ////设置tv_current_1的布局参数
        mWorkView.relativeLayout2.addView(mWorkView.tv_current_1);
        params = (RelativeLayout.LayoutParams) mWorkView.tv_current_1.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);  //与父控件左、上边界对齐
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;     //设置宽高
        params.leftMargin = CalFun.dip2px(this,10);//设置左边距
        mWorkView.tv_current_1.setLayoutParams(params);
        ////设置et_current的布局参数
        mWorkView.relativeLayout2.addView(mWorkView.et_current);
        params = (RelativeLayout.LayoutParams) mWorkView.et_current.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF,mWorkView.tv_current_1.getId());//位于tv_current_1右侧
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);                       //与父控件上边界对齐
        params.width = CalFun.dip2px(this,60);
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;     //设置宽高
        params.leftMargin = CalFun.dip2px(this,10);//设置左边距
        mWorkView.et_current.setLayoutParams(params);
        ////------为et_current添加监听------////
        mWorkView.et_current.addTextChangedListener(new TextWatcher() {
            int beforeChanged = 0;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    int targetId = mWorkView.id;
                    beforeChanged = Integer.parseInt(workViews[targetId].et_current.getText().toString());
                } catch (Exception e) {

                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    int targetId = mWorkView.id;
                    int num = Integer.parseInt(workViews[targetId].et_current.getText().toString());
                    if (num<0) {
                        num=0;
                        mWorkView.et_current.setText(String.valueOf(num));
                    }
                    works[targetId].setCurrent(num);
//                    currentUnit += (num-beforeChanged);
                    updatePbUnit();
                    updateWork(targetId);
                    workViews[targetId].pv.setProgress(works[targetId].getProgress());
                }catch (Exception e){

                }
            }
        });
        ////设置tv_current_2的布局参数
        mWorkView.relativeLayout2.addView(mWorkView.tv_current_2);
        params = (RelativeLayout.LayoutParams) mWorkView.tv_current_2.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF,mWorkView.et_current.getId());  //位于et_current右侧
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);                       //与父控件上边界对齐
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;     //设置宽高
        params.leftMargin = CalFun.dip2px(this,10);//设置左边距
        mWorkView.tv_current_2.setLayoutParams(params);
        //----------------------------------------------------------------------------------------//
        ////设置tv_total_2的布局参数
        mWorkView.relativeLayout2.addView(mWorkView.tv_total_2);
        params = (RelativeLayout.LayoutParams) mWorkView.tv_total_2.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        params.rightMargin = CalFun.dip2px(this,10);
        mWorkView.tv_total_2.setLayoutParams(params);
        ////设置et_total的布局参数
        mWorkView.relativeLayout2.addView(mWorkView.et_total);
        params = (RelativeLayout.LayoutParams) mWorkView.et_total.getLayoutParams();
        params.addRule(RelativeLayout.LEFT_OF,mWorkView.tv_total_2.getId());
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.width = CalFun.dip2px(this,60);
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        params.rightMargin = CalFun.dip2px(this,10);
        mWorkView.et_total.setLayoutParams(params);
        ////------为et_total添加监听------////
        mWorkView.et_total.addTextChangedListener(new TextWatcher() {
            int beforeChanged = 0;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    int targetId = mWorkView.id;
                    beforeChanged = Integer.parseInt(workViews[targetId].et_total.getText().toString());
                } catch (Exception e) {

                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    int targetId = mWorkView.id;
                    int num = Integer.parseInt(workViews[targetId].et_total.getText().toString());
                    if (num<0) {
                        num = 0;
                        mWorkView.et_total.setText(num);
                    }
                    works[targetId].setTotal(num);
//                    totalUnit += (num-beforeChanged);
                    updatePbUnit();
                    updateWork(targetId);
                    workViews[targetId].pv.setProgress(works[targetId].getProgress());
                }catch (Exception e){
                }
            }
        });
        ////设置tv_total_1的布局参数
        mWorkView.relativeLayout2.addView(mWorkView.tv_total_1);
        params = (RelativeLayout.LayoutParams) mWorkView.tv_total_1.getLayoutParams();
        params.addRule(RelativeLayout.LEFT_OF,mWorkView.et_total.getId());
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        params.rightMargin = CalFun.dip2px(this,20);
        mWorkView.tv_total_1.setLayoutParams(params);
        //----------------------------------------------------------------------------------------//
        ////设置btn_reduce_current的布局参数
        mWorkView.relativeLayout2.addView(mWorkView.btn_reduce_current);
        params = (RelativeLayout.LayoutParams) mWorkView.btn_reduce_current.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.width = CalFun.dip2px(this,40);
        params.height= CalFun.dip2px(this,40);
        params.leftMargin = CalFun.dip2px(this,10);
        mWorkView.btn_reduce_current.setLayoutParams(params);
        ////------为btn_reduce_current添加监听------////
        mWorkView.btn_reduce_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int targetId = mWorkView.id;
                int num = works[targetId].getCurrent() - 1;
                works[targetId].setCurrent(num);
                updateWork(targetId);
                workViews[targetId].et_current.setText(String.valueOf(num));
            }
        });

        ////设置btn_add_current的布局参数
        mWorkView.relativeLayout2.addView(mWorkView.btn_add_current);
        params = (RelativeLayout.LayoutParams) mWorkView.btn_add_current.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF,mWorkView.btn_reduce_current.getId());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.width = CalFun.dip2px(this,40);
        params.height= CalFun.dip2px(this,40);
        params.leftMargin = CalFun.dip2px(this,70);
        mWorkView.btn_add_current.setLayoutParams(params);
        ////------为btn_add_current添加监听------////
        mWorkView.btn_add_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int targetId = mWorkView.id;
                int num = works[targetId].getCurrent() + 1;
                works[targetId].setCurrent(num);
                updateWork(targetId);
                workViews[targetId].et_current.setText(String.valueOf(num));
            }
        });

        ////设置btn_add_total的布局参数
        mWorkView.relativeLayout2.addView(mWorkView.btn_add_total);
        params = (RelativeLayout.LayoutParams) mWorkView.btn_add_total.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.width = CalFun.dip2px(this,40);
        params.height= CalFun.dip2px(this,40);
        params.rightMargin = CalFun.dip2px(this,10);
        mWorkView.btn_add_total.setLayoutParams(params);
        ////------为btn_add_total添加监听------////
        mWorkView.btn_add_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int targetId = mWorkView.id;
                int num = works[targetId].getTotal() + 1;
                works[targetId].setTotal(num);
                updateWork(targetId);
                workViews[targetId].et_total.setText(String.valueOf(num));
            }
        });

        ////设置btn_reduce_total的布局参数
        mWorkView.relativeLayout2.addView(mWorkView.btn_reduce_total);
        params = (RelativeLayout.LayoutParams) mWorkView.btn_reduce_total.getLayoutParams();
        params.addRule(RelativeLayout.LEFT_OF,mWorkView.btn_add_total.getId());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.width = CalFun.dip2px(this,40);
        params.height= CalFun.dip2px(this,40);
        params.rightMargin = CalFun.dip2px(this,80);
        mWorkView.btn_reduce_total.setLayoutParams(params);
        mWorkView.workViewContainer.addView(mWorkView.relativeLayout2);
        ////------为btn_reduce_total添加监听------////
        mWorkView.btn_reduce_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int targetId = mWorkView.id;
                int num = works[targetId].getTotal() - 1;
                works[targetId].setTotal(num);
                updateWork(targetId);
                workViews[targetId].et_total.setText(String.valueOf(num));
            }
        });

        llContainer.addView(mWorkView.workViewContainer);
    }

    private void initPopWin() {
        popView = getLayoutInflater().inflate(R.layout.popwin_add,null,false);
        btnPwAdd = popView.findViewById(R.id.btn_add_add);
        etPwName = popView.findViewById(R.id.et_add_name);
        etPwCurrent = popView.findViewById(R.id.et_add_current);
        etPwTotal   = popView.findViewById(R.id.et_add_total);
        etPwWeight  = popView.findViewById(R.id.et_add_weight);

        etPwName.setText("Project");
        etPwName.setInputType(InputType.TYPE_CLASS_TEXT);
        etPwCurrent.setInputType(InputType.TYPE_CLASS_NUMBER);
        etPwTotal.setInputType(InputType.TYPE_CLASS_NUMBER);
        etPwWeight.setInputType(InputType.TYPE_CLASS_NUMBER);

//        fabAdd.setPivotX(fabAdd.getWidth()/2);
//        fabAdd.setPivotY(fabAdd.getHeight()/2);

        btnPwAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = requestWorkId();
                String name;
                int current,total,weight;
                try{
                    name = etPwName.getText().toString();
                }catch (Exception e){
                    name = "Project";
                    e.printStackTrace();
                }
                try{
                    current = Integer.parseInt(etPwCurrent.getText().toString());
                }catch (Exception e){
                    current = 0;
                    e.printStackTrace();
                }
                try{
                    total = Integer.parseInt(etPwTotal.getText().toString());
                }catch (Exception e){
                    total = 100;
                    e.printStackTrace();
                }
                try{
                    weight = Integer.parseInt(etPwWeight.getText().toString());
                }catch (Exception e){
                    weight = 1;
                    e.printStackTrace();
                }
                MyWork mWork = new MyWork(num,name,current,total,weight);
                ContentValues values = new ContentValues();
                values.put(MyWork.NUMBER, num);
                values.put(MyWork.NAME,   name);
                values.put(MyWork.CURRENT, current);
                values.put(MyWork.TOTAL,   total);
                values.put(MyWork.WEIGHT,  weight);
                db.insert(DBHelper.TB_NAME,null,values);
                addWork(mWork);

                isPwShowing = false;
                fabAdd.setTranslationY(CalFun.dip2px(mContext,0));
                fabAdd.setRotation(0);
                popWin.dismiss();
            }
        });

        popWin = new PopupWindow(popView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popWin.setOutsideTouchable(false);
        popWin.setTouchInterceptor(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getY() >= 0) {//motionEvent在popWin内部
                    return false;
                }
                if(isPwShowing){
                    fabAdd.setTranslationY(CalFun.dip2px(mContext,0));
                    fabAdd.setRotation(0);
                    isPwShowing = false;
                    popWin.dismiss();
                }
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show) {
            printDBWork();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
