package com.asurx.progressmanager;

public class MyWork {
    public static final String ID = "_id";
    public static final String NUMBER= "number";
    public static final String NAME = "name";
    public static final String CURRENT = "current";
    public static final String TOTAL = "total";
    public static final String WEIGHT = "weight";

    public int id;
    public String name;
    public int current,total,weight;

    MyWork(){
        this(-1,null);
    }
    MyWork(int id, String name) {
        this(id, name, 0, 100);
    }
    MyWork(int id, String name, int current, int total) {
        this(id, name, current, total, 1);
    }
    MyWork(int id, String name, int current, int total, int weight) {
        this.id = id;
        this.name = name;
        this.current = current;
        this.total = total;
        this.weight = weight;
    }

    public float getProgress() {
        return (float) current / (float) total;
    }


    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public int getCurrent(){
        return current;
    }
    public void setCurrent(int current){
        this.current = current;
    }

    public int getTotal(){
        return total;
    }
    public void setTotal(int total){
        this.total = total;
    }

    public int getWeight(){
        return weight;
    }
    public void setWeight(int weight){
        this.weight = weight;
    }

}


