package com.example.demo;

public class Weapon {
    private int type;
    private String name;
    private int attack;
    private int defense;
    private int HP;
    private int MP;
    public Weapon(int type, String name, int attack, int defense, int HP, int MP){
        this.type = type;
        this.name = name;
        this.attack = attack;
        this.defense = defense;
        this.MP = MP;
        this.HP = HP;
    }
    public int getType(){
        return this.type;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public int getAttack(){
        return this.attack;
    }
    public void setAttack(int attack){
        this.attack = attack;
    }
    public int getDefense(){
        return this.defense;
    }
    public void setDefense(int defense){
        this.defense = defense;
    }
    public int getHP() {
        return this.HP;
    }
    public void setHP(int HP){
        this.HP = HP;
    }
    public int getMP(){
        return this.MP;
    }
    public void setMP(int MP){
        this.MP = MP;
    }
}
