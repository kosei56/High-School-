package com.example.demo;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

public class Enemy {
    private ImageHandler imageHandler;
    private String name;
    private int hp;
    private int attack;
    private int defense;
    private int enemyNum;
    private int dropExp;
    private ArrayList<Spell> spells = new ArrayList<>();
    private long attackTime=-1;
    private long moveTime = -1;
    private AnchorPane aPane1;
    private Tiles[][] drawMap, viewMap;

    public Enemy(int enemyNum, String name, int hp, int attack, int defense, int dropExp, String tempPath, int picWidth, int picHeight, int flameX, int flameY){
        imageHandler = new ImageHandler(tempPath, picWidth, picHeight, flameX, flameY);
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.enemyNum = enemyNum;
        this.dropExp = dropExp;
    }
    public void setImageHandler(ImageHandler ih){
        this.imageHandler = ih;
    }
    public ImageHandler getImageHandler(){
        return this.imageHandler;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getDefense() {
        return this.defense;
    }

    public String getName(){
        return this.name;
    }

    public int getHp() {
        return this.hp;
    }

    public int getDropExp(){
        return this.dropExp;
    }

    public void setHp(int num){
        this.hp = num;
    }

    public void addHP(int num){
        this.hp += num;
    }
    public void addAttack(int num){
        this.attack += num;
    }
    public void addDefense(int num){
        this.defense += num;
    }
    public void loadNodes(AnchorPane anchorPane, Tiles[][] drawMap, Tiles[][] viewMap){
        this.aPane1 = anchorPane;
        this.drawMap = drawMap;
        this.viewMap = viewMap;
    }

    public int getEnemyNum(){
        return this.enemyNum;
    }

    public void attack(Character player, TextArea ta){
        int takenDamage = this.attack - player.getDefense();
        if(takenDamage <= 0){
            takenDamage = 1;
        }
        player.addHP(-takenDamage);
        ta.setText(player.getName() + " received " + takenDamage + " damage.");

    }

    public void setAttackTime(long attackTime){
        this.attackTime = attackTime;
    }

    public long getAttackTime() {
        return this.attackTime;
    }

    public ArrayList<Spell> getSpells() {
        return spells;
    }

    public void setMoveTime(long moveTime){
        this.moveTime = moveTime;
    }

    public long getMoveTime() {
        return this.moveTime;
    }

    public void castMagicBullet(){
        ArrayList<Spell> tempSpells = new ArrayList<>();
        tempSpells.add(new Spell("magicBullet", 0));
        tempSpells.add(new Spell("magicBullet", 1));
        tempSpells.add(new Spell("magicBullet", 2));
        tempSpells.add(new Spell("magicBullet", 3));
        tempSpells.add(new Spell("magicBullet", 4));
        tempSpells.add(new Spell("magicBullet", 5));
        tempSpells.add(new Spell("magicBullet", 6));
        tempSpells.add(new Spell("magicBullet", 7));
        ImageHandler tempIH;
        for(int i=0; i<tempSpells.size(); i++){
            tempIH = new ImageHandler("src/main/resources/images/mageBullet.png", 13, 13, 0, 0);
            tempSpells.get(i).setImageHandler(tempIH);
            tempSpells.get(i).getImageHandler().setPosition(this.getImageHandler().getScreenRow(), this.getImageHandler().getScreenCol(), drawMap, viewMap);
            aPane1.getChildren().add(tempSpells.get(i).getImageHandler().getImageView());
            this.getSpells().add(tempSpells.get(i));
        }
    }

    public void castTentacles(){
        ArrayList<Spell> tempSpells = new ArrayList<>();
        tempSpells.add(new Spell("tentacle", 0));
        tempSpells.add(new Spell("tentacle", 1));
        tempSpells.add(new Spell("tentacle", 2));
        tempSpells.add(new Spell("tentacle", 3));
        tempSpells.add(new Spell("tentacle", 4));
        tempSpells.add(new Spell("tentacle", 5));
        tempSpells.add(new Spell("tentacle", 6));
        tempSpells.add(new Spell("tentacle", 7));
        ImageHandler tempIH;
        for(int i=0; i<tempSpells.size(); i++){
            tempIH = new ImageHandler("src/main/resources/images/mageBullet.png", 25, 90, 0, 0);
            tempSpells.get(i).setImageHandler(tempIH);
            tempSpells.get(i).getImageHandler().setPosition(this.getImageHandler().getScreenRow(), this.getImageHandler().getScreenCol(), drawMap, viewMap);
            aPane1.getChildren().add(tempSpells.get(i).getImageHandler().getImageView());
            this.getSpells().add(tempSpells.get(i));
        }
    }
}
