package com.example.demo;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageHandler{
    private String tempPath;
    private int screenRow;
    private int screenCol;
    private int backRow;
    private int backCol;
    private int mapRow;
    private int mapCol;
    private Image image;
    private int flameX;
    private int flameY;
    private int width;
    private int height;
    private ImageView imageView;



    public ImageHandler(String tempPath, int picWidth, int picHeight, int flameX, int flameY) {
        this.tempPath = tempPath;
        FileInputStream tempFIS = null;
        try {
            tempFIS = new FileInputStream(this.tempPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.image = new Image(tempFIS);
        this.flameX = flameX * picWidth;
        this.flameY = flameY * picHeight;
        this.width = picWidth;
        this.height = picHeight;
        this.imageView = new ImageView();

    }

    public ImageView getImageView(){
        Rectangle2D rectangle2D = new Rectangle2D(this.getFlameX() * this.getWidth(), this.getFlameY() * this.getHeight(), this.getWidth(), this.getHeight());
        this.imageView.setViewport(rectangle2D);
        return this.imageView;
    }

    public void addScreenRow(int num){
        this.screenRow += num;
        this.mapRow += num;
    }

    public void addScreenCol(int num){
        this.screenCol += num;
        this.mapCol += num;
    }



    public void setBackRow(int backRow){
        this.backRow = backRow;
    }
    public void setMapRow(int mapRow){
        this.mapRow = mapRow;
    }
    public void setMapCol(int mapCol){
        this.mapCol = mapCol;
    }
    public int getMapRow(){
        return this.mapRow;
    }
    public int getMapCol(){
        return this.mapCol;
    }
    public void setBackCol(int backCol){
        this.backCol = backCol;
    }
    public void setScreenRow(int screenRow) {
        this.screenRow = screenRow;
    }
    public void setScreenCol(int screenCol){
        this.screenCol = screenCol;
    }
    public int getBackCol() {
        return this.backCol;
    }

    public String getTempPath() {
        return this.tempPath;
    }

    public int getBackRow() {
        return this.backRow;
    }

    public int getScreenRow(){
        return this.screenRow;
    }

    public int getScreenCol(){
        return this.screenCol;
    }

    public int getFlameX() {
        return this.flameX / this.width;
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public int getFlameY(){
        return this.flameY/this.height;
    }

    public void addFlameX(){
        this.flameX+= this.width;
    }

    public void addFlameY(){
        this.flameY += this.height;
    }

    public void setFlame(int x, int y){
        this.flameX = x * this.width;
        this.flameY = y * this.height;
    }

    public void setFlameX(int x){
        this.flameX = x * this.width;
    }

    public void setFlameY(int y){
        this.flameY = y * this.height;
    }

    public void setPosition(int row, int col, Tiles[][] drawMap, Tiles[][] viewMap){
        this.mapRow = row;
        this.mapCol = col;
        if(row <= viewMap.length/2 -1){
            this.screenRow = this.mapRow;
            this.backRow = viewMap.length/2;
        } else if(row >= drawMap.length - viewMap.length/2){
            this.screenRow = mapRow - (int)(drawMap.length/4.0 * 3) + viewMap.length/2 + 1;
//            this.screenRow = mapRow - (int)(drawMap.length/4.0 * 3) + 1;
            this.backRow =(int)(drawMap.length/4.0 * 3) - 1;
        } else {
            this.screenRow = viewMap.length/2;
            this.backRow = this.mapRow;
        }
        if(col <= viewMap[0].length/2 -1){
            this.screenCol = this.mapCol;
            this.backCol = viewMap[0].length/2;
        } else if(col >= drawMap[0].length - viewMap[0].length/2){
            this.screenCol = mapCol - (int)(drawMap[0].length/4.0 * 3) + 1;
            this.backCol =(int)(drawMap[0].length/4.0 * 3) - 1;
        } else {
            this.screenCol = viewMap[0].length/2;
            this.backCol = this.mapCol;
        }
    }

    public void checkAround(Tiles[][] drawMap, int checkNum, Character player, TextArea eventTA){
        for(int i=this.mapRow-checkNum; i<=this.mapRow + checkNum; i++){
            for(int j=this.mapCol-checkNum; j<=this.mapCol + checkNum; j++){
                if(i >= 0 && i < drawMap.length && j >= 0 && j < drawMap[0].length){
                    if(drawMap[i][j].getThingNum() == 6 && (drawMap[i][j].getWeapon() != null || drawMap[i][j].getItem() != null)){
                        drawMap[i][j].setThingNum(5);
                        player.getWeapon(drawMap[i][j].getWeapon());
                        if(drawMap[i][j].getItem() == null){
                            eventTA.setText("You got " + drawMap[i][j].getWeapon().getName());
                            drawMap[i][j].setWeapon(null);
                        } else {
                            eventTA.setText("You got " + drawMap[i][j].getItem().getName());
                            drawMap[i][j].setItem(null);
                        }
                        eventTA.setVisible(true);
                    }
                }

            }
        }
    }

    public javafx.scene.image.Image getImage(){
        return this.image;
    }

    public void moveUp(Tiles [][] map, int screenHeight){
        if(this.screenRow > screenHeight/2){
            this.screenRow--;
            this.mapRow--;
        } else if(this.backRow  > screenHeight/2){
            this.backRow--;
            this.mapRow--;
        } else if(this.screenRow > 0){
            this.screenRow--;
            this.mapRow--;
        }
    }
    public void moveDown(Tiles [][] map, int screenHeight){
        if(this.screenRow < screenHeight/2){
            this.screenRow++;
            this.mapRow++;
        } else if(this.backRow < map.length - screenHeight/2 - 1){
            this.backRow++;
            this.mapRow++;
        } else if(this.screenRow < screenHeight-1){
            this.screenRow++;
            this.mapRow++;
        }
    }
    public void moveRight(Tiles [][] map, int screenWidth){
        if(this.screenCol < screenWidth/2){
            this.screenCol++;
            this.mapCol++;
        } else if(this.backCol < map[0].length - screenWidth/2 - 1){
            this.backCol++;
            this.mapCol++;
        } else if(this.screenCol < screenWidth - 1){
            this.screenCol++;
            this.mapCol++;
        }
    }
    public void moveLeft(Tiles [][] map, int screenWidth){
        if(this.screenCol > screenWidth/2){
            this.screenCol--;
            this.mapCol--;
        } else if(this.backCol > screenWidth/2){
            this.backCol--;
            this.mapCol--;
        } else if(this.screenCol > 0){
            this.screenCol--;
            this.mapCol--;
        }
    }

}
