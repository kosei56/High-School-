package com.example.demo;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Tiles {
    private int tileNum;
    private int thingNum;
    private int actionNum;
    private Weapon weapon;
    private Item item;


    public Tiles(int tileNum, int thingNum, int actionNum){
        this.tileNum = tileNum;
        this.thingNum = thingNum;
        this.actionNum = actionNum;
        this.weapon = null;
        this.item = null;
    }


    public void setTileNum(int num){
        this.tileNum = num;
    }

    public void setThingNum(int num){
        this.thingNum = num;
    }

    public void setActionNum(int num){
        this.actionNum = num;
    }

    public int getThingNum(){
        return this.thingNum;
    }

    public int getActionNum(){
        return this.actionNum;
    }
    public int getTileNum() {
        return this.tileNum;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    public Item getItem() {
        return this.item;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
