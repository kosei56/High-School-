package com.example.demo;

public class Spell {
    private String name;
    private int attack = 5;
    private int mp = 5;
    private int direction;
    private long drawTime;
    private boolean hit;
    ImageHandler imageHandler;
    public Spell(String name, int direction){
        this.name = name;
        this.direction = direction;
        this.drawTime = System.nanoTime();
        this.hit = false;
    }

    public void setDrawTime(long drawTime){
        this.drawTime = drawTime;
    }

    public long getDrawTime() {
        return this.drawTime;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setImageHandler(ImageHandler ih){
        this.imageHandler = ih;
    }
    public ImageHandler getImageHandler(){
        return this.imageHandler;
    }
    public String getName(){
        return this.name;
    }
    public int getAttack(){
        return this.attack;
    }
    public int getMp(){
        return this.mp;
    }

    public boolean isHit(){
        return this.hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}

