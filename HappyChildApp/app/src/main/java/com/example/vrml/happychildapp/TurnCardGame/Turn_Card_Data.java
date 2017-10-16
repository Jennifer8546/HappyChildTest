package com.example.vrml.happychildapp.TurnCardGame;

/**
 * Created by VRML on 2017/5/21.
 */

public class Turn_Card_Data {
    //List<String> array = new ArrayList<String>();
    String[] array;
    public Turn_Card_Data get() {
        return new Turn_Card_Data();
    }
    public Turn_Card_Data(String[] array){
       this.array =array.clone();

    }
    public Turn_Card_Data(){
        this.array = new String[]{"1", "1", "2", "2", "3", "3", "4", "4", "5", "5", "6", "6", "7", "7", "8", "8"};
        //this.array = Arrays.asList("1", "1", "2", "2", "3", "3", "4", "4", "5", "5", "6", "6", "7", "7", "8", "8");
    }

    public String[] getData(){
        return this.array;
    }
}
