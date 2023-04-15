package com.example.demo;

public class Item {
    private String name;
    private int itemType;
    private int healNum;
    private int attackNum;
    private int haveNum;
    public Item(String name, int itemType, int num, int amount){
        this.name = name;
        this.itemType = itemType;
        this.healNum = num;
        this.attackNum = num;
        this.haveNum = amount;
    }
    public void useItem(Character player){
        player.addHP(this.healNum);
        if(this.haveNum == 1){
            player.getItems().remove(this);
        } else {
            haveNum--;
        }
    }
    public void useItem(Enemy enemy){
        enemy.addHP(-this.attackNum);
    }

    public String getName(){
        return this.name;
    }
    public int getHealNum(){
        return this.healNum;
    }
    public int getItemType(){
        return this.itemType;
    }
    public int getAttackNum() {
        return this.attackNum;
    }
    public int getHaveNum(){
        return this.haveNum;
    }
    public void addHaveNum(int num){
        this.haveNum += num;
    }
}
