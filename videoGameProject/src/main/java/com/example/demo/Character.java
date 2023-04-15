package com.example.demo;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Character {
    private ImageHandler imageHandler;
    private String name;
    private int level = 1;
    private int ep = 0;
    private int maxHP;
    private int hp = 20;
    private int maxMP;
    private int mp = 5;
    private int attack = 6;
    private int defense = 3;
    private int requiredEp = 10;
    private int haveMoney = 0;
    private ArrayList<Spell> spells = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<ArrayList<Weapon>> haveWeapons = new ArrayList<>();
    private Weapon[] equippedWeapon = new Weapon[4];
    private Label lblLevel;
    private Label lblHP;
    private Label lblMP;
    private TextArea textArea;
    public Character(String name){
        this.name = name;
        this.imageHandler = new ImageHandler("src/main/resources/images/hero2.png", 48, 48,0, 0);
        this.maxHP = this.hp;
        this.maxMP = this.mp;
        for(int i=0; i<4; i++){
            haveWeapons.add(new ArrayList<>());
        }
    }

    public ImageHandler getImageHandler(){
        return this.imageHandler;
    }

    public int getLevel(){
        return this.level;
    }


    public void levelUp(){
        this.level ++;
        this.requiredEp += this.level*10;
        int prevHp = this.maxHP;
        int prevAttack = this.attack;
        int prevDefense = this.defense;
        int prevMp = this.mp;
        this.maxHP += 3;
        this.hp = this.maxHP;
        this.attack += 2;
        this.defense += 2;
        this.maxMP += 2;
        this.mp = this.maxMP;
        textArea.setText(this.name + " leveled up to " + this.level + "\n");
        textArea.appendText("HP " + prevHp + " -> " + this.maxHP + "\n");
        textArea.appendText("MP " + prevMp + " -> " + this.maxMP + "\n");
        textArea.appendText("Attack " + prevAttack + " -> " + this.attack + "\n");
        textArea.appendText("Defense " + prevDefense + " -> " + this.defense );

    }
    public void addHP(int num){
        int tempHP = this.hp + num;
        if(tempHP > this.maxHP){
            this.hp = this.maxHP;
        } else {
            this.hp = tempHP;
        }
        this.lblHP.setText("" + this.hp);
    }

    public int getEp(){
        return this.ep;
    }
    public void setEp(int num){
        this.ep = num;
    }
    public void addEp(int num){
        this.ep += num;
    }
    public ArrayList<Item> getItems(){
        return this.items;
    }

    public void addMp(int num){
        int tempMP = this.mp + num;
        this.mp = tempMP;
        this.lblMP.setText("" + tempMP);
    }
    public void addAttack(int num){
        this.attack += num;
    }
    public void addDefense(int num){
        this.defense += num;
    }
    public String getName(){
        return this.name;
    }
    public int getHp(){
        return this.hp;
    }
    public int getMp(){
        return this.mp;
    }
    public int getAttack(){
        return this.attack;
    }
    public int getDefense(){
        return this.defense;
    }

    public int getHaveMoney() {
        return this.haveMoney;
    }

    public void addMoney(int num){
        this.haveMoney += num;
    }

    public void setNodes(Label lblLevel, Label lblHP, Label lblMP, TextArea desTA){
        this.lblLevel = lblLevel;
        this.lblHP = lblHP;
        this.lblMP = lblMP;
        this.textArea = desTA;

    }

    public void setHp(int num){
        this.hp = hp;
        lblHP.setText(""+num);
    }
    public void attack(Enemy enemy){
        int takenDamage = this.attack- enemy.getDefense();
        if(takenDamage < 0) takenDamage = 0;
        enemy.addHP(-takenDamage);
        textArea.setText(enemy.getName() + " received " + takenDamage + " damage.");
    }

    public boolean canLevelUp(){
        if(this.ep < requiredEp){
            return false;
        }
        return true;
    }

    public int getMaxHP(){
        return this.maxHP;
    }

    public int getMaxMP() {
        return this.maxMP;
    }

    public void addMaxHP(int num){
        this.maxHP += num;
    }
    public void addMaxMP(int num){
        this.maxMP += num;
    }
    public ArrayList<ArrayList<Weapon>> getHaveWeapons(){
        return this.haveWeapons;
    }

    public Weapon[] getEquippedWeapon() {
        return this.equippedWeapon;
    }

    public void getWeapon(Weapon weapon){
        this.haveWeapons.get(weapon.getType()).add(weapon);
    }

    public void getItem(Item item){
        this.items.add(item);
    }


}
