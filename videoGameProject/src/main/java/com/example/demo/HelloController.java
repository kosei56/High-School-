package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class HelloController {
    @FXML
    private AnchorPane anchorPane, aPane1;
    @FXML
    private Label lblLevel, lblHP, lblMP, lblStatsName, lblStatsLevel, lblStatsHP, lblStatsMP, lblBattleName;
    @FXML
    private TextField text1;
    @FXML
    private TextArea desTA, eventTA;
    @FXML
    private GridPane gPaneBoard, gpMenu, gPaneThing;
    @FXML
    private TabPane tPane;
    @FXML
    private ImageView enemyIV;
    @FXML
    private Rectangle rect2;
    @FXML
    private Button btnStart, btnEquip;
    @FXML
    private ListView listLV, lstStatsDisplay, lstEquippedWeapons, lstViewPort;

    private Enemy enemy1, enemy2, enemy3, enemy4, enemy5, enemy6, enemy7, enemy8;
    private Item heal1 = new Item("heal1", 0, 10, 0);



    private static final double cellSize = 30;
    private Tiles [][] viewMap = new Tiles[15][15];
    private ImageView [][] imgMap = new ImageView[viewMap.length][viewMap[0].length];
    private ImageView [][] imgThingMap = new ImageView[viewMap.length][viewMap[0].length];
    private Tiles[][] drawMap;


    private Character player = new Character("player");
    private Enemy currentEnemy;
    private Image grass1, grass2, house1, rock, tree1, tree2, chestOpened, chestClosed, lake, fenceRight, fenceLeft, fenceTop, fenceBottom, fenceTopLeft, fenceTopRight, fenceBottomRight, fenceBottomLeft;
    private int healthBar;

    private boolean inBattle = false, beginning = true;
    private String item;





    @FXML
    public void handleStart() {
        btnStart.setVisible(false);
        gPaneBoard.setGridLinesVisible(true);
        handleLoad();
        drawMap = villageMap;
        setupPlayer();
        setupEnemies();
        setupImage();
        setupMap();
        drawMap();
        setUpScreen();
        updateStats();
        start();
        handleBeginning();
    }

    @FXML
    public void handleAttack(){
        enemyIV.setImage(null);
        gpMenu.setDisable(true);
        player.attack(currentEnemy);
        attackedTime = System.nanoTime();
        enemyTime = System.nanoTime();
        checkResult();
    }
    @FXML
    public void handleSpell(){

    }
    @FXML
    public void handleItems(){
        Button button = (Button)(gpMenu.getChildren().get(2));
        if(button.getText().equals("Use")){
            handleUse();
        }
        gpMenu.getChildren().get(2).setDisable(true);
        listLV.setVisible(true);
        listLV.getItems().clear();
        for(int i=0; i<player.getItems().size(); i++){
            listLV.getItems().add(player.getItems().get(i).getName() +  "                                                    "+ player.getItems().get(i).getHaveNum());
        }
        button.setText("Use");
    }
    @FXML
    public void handleDefend(){

    }
    @FXML
    public void handleRun(){

    }
    @FXML
    public void handleBack(){
        if(!inBattle) tPane.getSelectionModel().select(0);
        if(listLV.isVisible()){
            listLV.setVisible(false);
            Button button = (Button) gpMenu.getChildren().get(2);
            button.setText("Items");
            gpMenu.getChildren().get(2).setDisable(false);
        }
    }
    @FXML
    public void handleItemUse(){
        item = listLV.getSelectionModel().getSelectedItem().toString();
        gpMenu.getChildren().get(2).setDisable(false);
    }

    @FXML
    public void handleBackMap(){
        tPane.getSelectionModel().select(0);
    }


    private String takeOffWeapon;
    private String selectedWeapon;
    @FXML
    public void handleEquipLst(){
        lstViewPort.setDisable(false);
        takeOffWeapon = (String) lstEquippedWeapons.getSelectionModel().getSelectedItem();
        takeOffWeapon = takeOffWeapon.substring(9);
        if(!takeOffWeapon.equals("none")){
            btnEquip.setDisable(false);
            btnEquip.setText("Take Off");
        } else {
            btnEquip.setDisable(true);
            btnEquip.setText("Equip");
        }
        updateViewPort();
        updateStats();
    }

    @FXML
    public void handleViewPort(){
        btnEquip.setDisable(false);
        btnEquip.setText("Equip");
        selectedWeapon = (String) lstViewPort.getSelectionModel().getSelectedItem();
        showTempStats(getSelectedWeapon(selectedWeapon));
    }


    @FXML
    public void handleEquip(){
        btnEquip.setDisable(true);
        if(btnEquip.getText().equals("Equip")){
            lstViewPort.setDisable(true);
            Weapon weapon = getSelectedWeapon(selectedWeapon);
            if(player.getEquippedWeapon()[weapon.getType()] == null){
                player.addMaxHP(weapon.getHP());
                player.addAttack(weapon.getAttack());
                player.addDefense(weapon.getDefense());
                player.addMaxMP(weapon.getMP());
            } else {
                Weapon replacedWeapon = player.getEquippedWeapon()[weapon.getType()];
                player.addMaxHP(weapon.getHP() - replacedWeapon.getHP());
                player.addAttack(weapon.getAttack() - replacedWeapon.getAttack());
                player.addDefense(weapon.getDefense() - replacedWeapon.getDefense());
                player.addMaxMP(weapon.getMP() - replacedWeapon.getMP());
                player.getHaveWeapons().get(weapon.getType()).add(player.getEquippedWeapon()[weapon.getType()]);
            }
            player.getHaveWeapons().get(weapon.getType()).remove(weapon);
            player.getEquippedWeapon()[weapon.getType()] = weapon;

        } else {
            btnEquip.setText("Equip");
            for(Weapon weapon : player.getEquippedWeapon()){
                if(weapon != null && weapon.getName().equals(takeOffWeapon)){
                    player.getEquippedWeapon()[weapon.getType()] = null;
                    player.getHaveWeapons().get(weapon.getType()).add(weapon);
                    player.addMaxHP(-weapon.getHP());
                    player.addMaxMP(-weapon.getMP());
                    player.addAttack(-weapon.getAttack());
                    player.addDefense(-weapon.getDefense());
                }
            }
        }
        updateStats();
        updateViewPort();
        updateEquipList();
    }

    public void updateEquipList(){
        lstEquippedWeapons.getItems().clear();
        for(Weapon weapon : player.getEquippedWeapon()){
            if(weapon == null){
                lstEquippedWeapons.getItems().add("         " + "none");
            } else {
                lstEquippedWeapons.getItems().add("         " + weapon.getName());
            }
        }
    }




    public void drawMap(){
        for(int i=0; i<viewMap.length; i++){
            for(int j=0; j<viewMap[0].length; j++){
                viewMap[i][j] = drawMap[player.getImageHandler().getBackRow() - viewMap.length/2 + i][player.getImageHandler().getBackCol() - viewMap.length/2 + j];
            }
        }
        for(int i=0; i<viewMap.length; i++){
            for(int j=0; j<viewMap[0].length; j++){
                // background
                // 0 = light grass 1 = darker grass
                if(viewMap[i][j].getTileNum() == 0){
                    imgMap[i][j].setImage(grass1);
                }
                if(viewMap[i][j].getTileNum() == 1){
                    imgMap[i][j].setImage(grass2);
                }
                // Things
                // 1 = house       2 = rock
                // 3 = tree1(red)  4 = tree2(blue)   6 = chest closed     5 = chest opened;
                // 7 = lake        8 = fence(Top)    9 = fence(Right)     10= fence(Bottom)    11 = fence(Left)
                // 12= fence(Top Left)   13 = fence(Top Right)    14 = fence(Bottom Right)    15 = fence(Bottom Left)
                // 20 = exit
                if(viewMap[i][j].getThingNum() == 0 || viewMap[i][j].getThingNum() == 20){
                    imgThingMap[i][j].setImage(null);
                }
                if(viewMap[i][j].getThingNum() == 1){
                    imgThingMap[i][j].setImage(house1);
                }
                if(viewMap[i][j].getThingNum() == 2){
                    imgThingMap[i][j].setImage(rock);
                }
                if(viewMap[i][j].getThingNum() == 3){
                    imgThingMap[i][j].setImage(tree1);
                }
                if(viewMap[i][j].getThingNum() == 4){
                    imgThingMap[i][j].setImage(tree2);
                }
                if(viewMap[i][j].getThingNum() == 5){
                    imgThingMap[i][j].setImage(chestClosed);
                }
                if(viewMap[i][j].getThingNum() == 6){
                    imgThingMap[i][j].setImage(chestOpened);
                }
                if(viewMap[i][j].getThingNum() == 7){
                    imgThingMap[i][j].setImage(lake);
                }
                if(viewMap[i][j].getThingNum() == 8){
                    imgThingMap[i][j].setImage(fenceTop);
                }
                if(viewMap[i][j].getThingNum() == 9){
                    imgThingMap[i][j].setImage(fenceRight);
                }
                if(viewMap[i][j].getThingNum() == 10){
                    imgThingMap[i][j].setImage(fenceBottom);
                }
                if(viewMap[i][j].getThingNum() == 11){
                    imgThingMap[i][j].setImage(fenceLeft);
                }
                if(viewMap[i][j].getThingNum() == 12){
                    imgThingMap[i][j].setImage(fenceTopLeft);
                }
                if(viewMap[i][j].getThingNum() == 13){
                    imgThingMap[i][j].setImage(fenceTopRight);
                }
                if(viewMap[i][j].getThingNum() == 14){
                    imgThingMap[i][j].setImage(fenceBottomRight);
                }
                if(viewMap[i][j].getThingNum() == 15){
                    imgThingMap[i][j].setImage(fenceBottomLeft);
                }
            }
        }
        //hero2
        //192 * 192
        // 4 * 4
        drawPlayer();
    }


    public void checkSpot(){
        ImageHandler playerImg = player.getImageHandler();
        int thingNum = drawMap[playerImg.getMapRow()][playerImg.getMapCol()].getThingNum();
        int actionNum = drawMap[playerImg.getMapRow()][playerImg.getMapCol()].getActionNum();

        if(drawMap == villageMap){
            if(actionNum == 1){
                drawMap = worldMap;
                player.getImageHandler().setPosition(drawMap.length/2 + 1 , drawMap[0].length/2, drawMap, viewMap);
            }

        } else if(drawMap == worldMap){
            if(actionNum == 1){
                drawMap = dungeonMap;
                currentEnemies = enemies2;
                playerImg.setPosition(1, drawMap[0].length/2, drawMap, viewMap);
            }
            if(actionNum == 2){
                drawMap = villageMap;
                player.getImageHandler().setPosition(23, 12, drawMap, viewMap);
            }

        } else if(drawMap == dungeonMap){
            if(actionNum == 1){
                drawMap = battleFieldMap;
                playerImg.setPosition(7, 7, drawMap, viewMap);
                startBossBattle();
            }
            if(actionNum == 2){
                drawMap = worldMap;
                playerImg.setPosition(0, 1, drawMap, viewMap);
            }
        }

        if(playerImg.getScreenRow() > viewMap.length - 4){
            statsDisplayRect.setOpacity(0.5);
        } else {
            statsDisplayRect.setOpacity(1);
        }
        drawMap();
    }

    public void updateStats(){
        lblStatsLevel.setText(String.valueOf(player.getLevel()));
        lblStatsHP.setText(String.valueOf(player.getHp()));
        lblStatsMP.setText(String.valueOf(player.getMp()));
        lblLevel.setText(String.valueOf(player.getLevel()));
        lblHP.setText(String.valueOf(player.getHp()));
        lblMP.setText(String.valueOf(player.getMp()));

        lstStatsDisplay.getItems().clear();
        lstStatsDisplay.getItems().add("HP " + "     " + player.getHp() + " / " + player.getMaxHP());
        lstStatsDisplay.getItems().add("MP " + "     " + player.getMp() + " / " + player.getMaxMP());
        lstStatsDisplay.getItems().add("ATK" + "     " + player.getAttack());
        lstStatsDisplay.getItems().add("DEF" + "     " + player.getDefense());

    }

    public void showTempStats(Weapon weapon){
        lstStatsDisplay.getItems().clear();
        if(player.getEquippedWeapon()[weapon.getType()] != null){
            lstStatsDisplay.getItems().add("HP " + "     " + player.getHp() + " / " + player.getMaxHP() + "    +    " + (weapon.getHP() - player.getEquippedWeapon()[weapon.getType()].getHP()));
            lstStatsDisplay.getItems().add("MP " + "     " + player.getMp() + " / " + player.getMaxMP() + "     +    " + (weapon.getMP() - player.getEquippedWeapon()[weapon.getType()].getMP()));
            lstStatsDisplay.getItems().add("ATK" + "     " + player.getAttack() + "    +    " + (weapon.getAttack() - player.getEquippedWeapon()[weapon.getType()].getAttack()));
            lstStatsDisplay.getItems().add("DEF" + "     " + player.getDefense() + "    +    " + (weapon.getDefense() - player.getEquippedWeapon()[weapon.getType()].getDefense()));
        } else {
            lstStatsDisplay.getItems().add("HP " + "     " + player.getHp() + " / " + player.getMaxHP() + "    +    " + weapon.getHP());
            lstStatsDisplay.getItems().add("MP " + "     " + player.getMp() + " / " + player.getMaxMP() + "     +    " + weapon.getMP());
            lstStatsDisplay.getItems().add("ATK" + "     " + player.getAttack() + "    +    " + weapon.getAttack());
            lstStatsDisplay.getItems().add("DEF" + "     " + player.getDefense() + "    +    " + weapon.getDefense());
        }

    }

    public void updateViewPort(){
        lstViewPort.getItems().clear();
        for(int i=0; i<player.getHaveWeapons().size(); i++){
            if(lstEquippedWeapons.getSelectionModel().isSelected(i)){
                for(int j=0; j<player.getHaveWeapons().get(i).size(); j++){
                    lstViewPort.getItems().add(player.getHaveWeapons().get(i).get(j).getName());
                }
            }
        }
    }

    public void updateLstEquipped(){

    }


    public void encounterEnemy(){
        if(getChance(2) && drawMap != villageMap){
            tPane.getSelectionModel().select(1);
            currentEnemy = currentEnemies.get((int)(Math.random()*currentEnemies.size()));
            enemyIV.setImage(currentEnemy.getImageHandler().getImage());
            Rectangle2D viewPort = new Rectangle2D(currentEnemy.getImageHandler().getFlameX()* currentEnemy.getImageHandler().getWidth(), currentEnemy.getImageHandler().getFlameY() * currentEnemy.getImageHandler().getHeight(), currentEnemy.getImageHandler().getWidth(), currentEnemy.getImageHandler().getHeight());
            enemyIV.setViewport(viewPort);
            desTA.setText("You encountered with " + currentEnemy.getName());
            inBattle = true;
            for(int i=0; i<6; i++) {
                gpMenu.getChildren().get(i).setDisable(false);
            }
        }
    }

    public void setupPlayer(){
        player.getImageHandler().setPosition(drawMap.length/2, drawMap.length/2, drawMap, viewMap);
        player.setNodes(lblLevel, lblHP, lblMP, desTA);
        player.getImageHandler().setFlame(2, 0);
        player.getItems().add(new Item(heal1.getName(), heal1.getItemType(), heal1.getHealNum(), 3));

        ArrayList<ArrayList<Weapon>> haveWeapons = player.getHaveWeapons();
        haveWeapons.get(0).add(getWeapon("wooden sword"));
        haveWeapons.get(1).add(getWeapon("wooden helmet"));
        haveWeapons.get(2).add(getWeapon("wooden armor"));
        haveWeapons.get(3).add(getWeapon("wooden shield"));


        aPane1.getChildren().add(player.getImageHandler().getImageView());
    }

    private Rectangle statsDisplayRect;
    private Label lblL, lblH, lblM;
    public void setUpScreen(){
        statsDisplayRect = new Rectangle(gPaneBoard.getLayoutX() + cellSize/2, gPaneBoard.getLayoutY() + cellSize * 12 + cellSize/1.3, cellSize*14, cellSize*1.6);
        statsDisplayRect.setStroke(Color.WHITE);
        statsDisplayRect.setStrokeWidth(2);
        aPane1.getChildren().add(statsDisplayRect);

        lblStatsName = createLabel(player.getName(), statsDisplayRect.getX() + cellSize/2.0,statsDisplayRect.getY() + cellSize/3, 20, Color.WHITE);
        lblL = createLabel("L", statsDisplayRect.getX() + cellSize*5, statsDisplayRect.getY() + cellSize/3, 20, Color.WHITE);
        lblH = createLabel("H", statsDisplayRect.getX() + cellSize*8, statsDisplayRect.getY() + cellSize/3, 20, Color.WHITE);
        lblM = createLabel("M", statsDisplayRect.getX() + cellSize*11, statsDisplayRect.getY() + cellSize/3, 20, Color.WHITE);
        lblStatsLevel = createLabel(String.valueOf(player.getLevel()), statsDisplayRect.getX() + cellSize*6.5, statsDisplayRect.getY() + cellSize/3, 20, Color.WHITE);
        lblStatsHP = createLabel(String.valueOf(player.getHp()), statsDisplayRect.getX() + cellSize*9.5, statsDisplayRect.getY() + cellSize/3, 20, Color.WHITE);
        lblStatsMP = createLabel(String.valueOf(player.getMp()), statsDisplayRect.getX() + cellSize*12.5, statsDisplayRect.getY() + cellSize/3, 20, Color.WHITE);

        aPane1.getChildren().add(lblStatsName);
        aPane1.getChildren().add(lblL);
        aPane1.getChildren().add(lblH);
        aPane1.getChildren().add(lblM);
        aPane1.getChildren().add(lblStatsLevel);
        aPane1.getChildren().add(lblStatsHP);
        aPane1.getChildren().add(lblStatsMP);

        statsDisplayRect.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                tPane.getSelectionModel().select(3);
            }
        });
        for(int i=0; i<4; i++){
            lstEquippedWeapons.getItems().add("         " + "none");
        }


        eventTA = new TextArea();
        eventTA.setId("eventTA");
        eventTA.setPrefWidth(cellSize * (viewMap[0].length-1));
        eventTA.setPrefHeight(cellSize*2);
        eventTA.setLayoutX(gPaneBoard.getLayoutX() + cellSize/2);
        eventTA.setLayoutY(gPaneBoard.getLayoutY() + cellSize);
        eventTA.setEditable(false);
        eventTA.setFocusTraversable(false);
        aPane1.getChildren().add(eventTA);
        eventTA.setVisible(false);


        //battle screen
        healthBar = viewMap[0].length - 3;
        battleStats = new Rectangle(gPaneBoard.getLayoutX() + cellSize/2, gPaneBoard.getLayoutY() + (viewMap.length - 1) * cellSize, cellSize*3, cellSize/1.2);
        lblBattleName = createLabel(player.getName(), gPaneBoard.getLayoutX() + cellSize/1.5, gPaneBoard.getLayoutY() + (viewMap.length - 1) * cellSize, 20, Color.WHITE);
        battleStats.setVisible(false);
        lblBattleName.setVisible(false);
        aPane1.getChildren().add(battleStats);
        aPane1.getChildren().add(lblBattleName);
    }

    public Label createLabel(String text, double x, double y, double fontSize, Color color){
        Label tempLabel = new Label(text);
        tempLabel.setLayoutX(x);
        tempLabel.setLayoutY(y);
        tempLabel.setFont(new Font("Arial", fontSize));
        tempLabel.setTextFill(color);
        return tempLabel;
    }


    private ArrayList<Enemy> enemyList = new ArrayList<>();
    private ArrayList<Enemy> enemies1 = new ArrayList<>();
    private ArrayList<Enemy> enemies2 = new ArrayList<>();
    private ArrayList<Enemy> currentEnemies;
    public void setupEnemies(){
        enemy1 = new Enemy(1, "enemy1", 8, 5, 3, 5, "src/main/resources/images/enemy.png", 72, 103, 0, 0);
        enemy2 = new Enemy(2, "enemy2", 6, 10, 1, 7, "src/main/resources/images/enemy.png", 72, 103, 1, 0);
        enemy3 = new Enemy(3, "enemy3", 13,4, 4, 6, "src/main/resources/images/enemy.png", 72, 103, 2, 0);
        enemy4 = new Enemy(4, "enemy4", 30,9, 7, 10, "src/main/resources/images/enemy2.png", 50, 50, 0, 0);
        enemy5 = new Enemy(5, "enemy5", 40,13, 9, 15, "src/main/resources/images/enemy2.png", 50, 50, 1, 0);
        enemy6 = new Enemy(6, "enemy6", 35,14, 10, 15, "src/main/resources/images/enemy2.png", 50, 50, 2, 0);
        enemy7 = new Enemy(7, "enemy7", 45,15, 9, 20, "src/main/resources/images/enemy2.png", 50, 50, 3, 0);
        enemy8 = new Enemy(8, "enemy8", 45,15, 9, 20, "src/main/resources/images/enemy2.png", 50, 50, 4, 0);
//        enemy1 = new Enemy(1, "enemy1", 1, 5, 1, 5, "src/main/resources/images/enemy.png", 72, 103, 0, 0);
//        enemy2 = new Enemy(2, "enemy2", 1, 10, 1, 7, "src/main/resources/images/enemy.png", 72, 103, 1, 0);
//        enemy3 = new Enemy(3, "enemy3", 1,4, 1, 6, "src/main/resources/images/enemy.png", 72, 103, 2, 0);
//        enemy4 = new Enemy(4, "enemy4", 1,9, 1, 10, "src/main/resources/images/enemy2.png", 50, 50, 0, 0);
//        enemy5 = new Enemy(5, "enemy5", 1,13, 1, 15, "src/main/resources/images/enemy2.png", 50, 50, 1, 0);
//        enemy6 = new Enemy(6, "enemy6", 1,14, 1, 15, "src/main/resources/images/enemy2.png", 50, 50, 2, 0);
//        enemy7 = new Enemy(7, "enemy7", 1,15, 1, 20, "src/main/resources/images/enemy2.png", 50, 50, 3, 0);
//        enemy8 = new Enemy(8, "enemy8", 1,15, 1, 20, "src/main/resources/images/enemy2.png", 50, 50, 4, 0);

        enemyList.add(enemy1);
        enemyList.add(enemy2);
        enemyList.add(enemy3);
        enemyList.add(enemy4);
        enemyList.add(enemy5);
        enemyList.add(enemy6);
        enemyList.add(enemy7);
        enemyList.add(enemy8);

        enemies1.add(getEnemy(1));
        enemies1.add(getEnemy(2));
        enemies1.add(getEnemy(3));
        enemies2.add(getEnemy(4));
        enemies2.add(getEnemy(5));
        enemies2.add(getEnemy(6));
        enemies2.add(getEnemy(7));
        enemies2.add(getEnemy(8));

        currentEnemies = enemies1;

        // setup first enemy
        aPane1.getChildren().add(firstEnemy.getImageView());
        firstEnemy.getImageView().setImage(firstEnemy.getImage());
        firstEnemy.getImageView().setLayoutX(gPaneBoard.getLayoutX() + cellSize*7);
        firstEnemy.getImageView().setLayoutY(gPaneBoard.getLayoutY() + cellSize*3);
        // setup boss
        aPane1.getChildren().add(boss.getImageHandler().getImageView());
        boss.getImageHandler().getImageView().setVisible(false);
        boss.getImageHandler().setScreenRow(0);
        boss.getImageHandler().setScreenCol(viewMap[0].length/2 - 2);
        boss.loadNodes(aPane1, drawMap, viewMap);
    }

    public void drawBullet(){
        ArrayList<Spell> spells = boss.getSpells();
        for(int i=0; i<spells.size(); i++){
            ImageView spellIV = spells.get(i).getImageHandler().getImageView();
            spellIV.setX(gPaneBoard.getLayoutX() + spells.get(i).getImageHandler().getScreenCol()*cellSize);
            spellIV.setY(gPaneBoard.getLayoutY() + spells.get(i).getImageHandler().getScreenRow()*cellSize);
            spellIV.setFitWidth(cellSize);
            spellIV.setFitHeight(cellSize);
            spellIV.setImage(spells.get(i).getImageHandler().getImage());
        }

    }
    public void drawBoss(){
        ImageView bossIV = boss.getImageHandler().getImageView();
        bossIV.setX(gPaneBoard.getLayoutX() + boss.getImageHandler().getScreenCol()*cellSize);
        bossIV.setY(gPaneBoard.getLayoutY() + boss.getImageHandler().getScreenRow()*cellSize);
        bossIV.setFitWidth(cellSize*4);
        bossIV.setFitHeight(cellSize*4);
        bossIV.setImage(boss.getImageHandler().getImage());
    }

    public void drawPlayer(){
        ImageView playerIV = player.getImageHandler().getImageView();
        playerIV.setImage(player.getImageHandler().getImage());
        playerIV.setFitWidth(cellSize);
        playerIV.setFitHeight(cellSize);
        playerIV.setX(gPaneBoard.getLayoutX() + player.getImageHandler().getScreenCol()*cellSize);
        playerIV.setY(gPaneBoard.getLayoutY() + player.getImageHandler().getScreenRow()*cellSize);
    }

    private Enemy boss = new Enemy(10, "boss", 10, 10, 10, 10, "src/main/resources/images/boss.png", 122, 110, 0, 0);
    private ImageHandler firstEnemy = new ImageHandler("src/main/resources/images/firstEnemy.png", 45, 51, 0, 0);


    public Enemy getEnemy(int num){
        for(Enemy enemy : enemyList){
            if(enemy.getName().substring(5).equals(String.valueOf(num))){
                return new Enemy(enemy.getEnemyNum(), enemy.getName(), enemy.getHp(), enemy.getAttack(), enemy.getDefense(), enemy.getDropExp(), enemy.getImageHandler().getTempPath(), enemy.getImageHandler().getWidth(), enemy.getImageHandler().getHeight(), enemy.getImageHandler().getFlameX(), enemy.getImageHandler().getFlameY());
            }
        }
        return null;
    }

    public void setupMap(){
        for(int i=0; i<worldMap.length; i++){
            for(int j=0; j<worldMap[0].length; j++){
                worldMap[i][j].setThingNum(worldThingMap[i][j].getTileNum());
            }
        }
        for(int i=0; i<villageMap.length; i++){
            for(int j=0; j<villageMap[0].length; j++){
                villageMap[i][j].setThingNum(villageThingMap[i][j].getTileNum());
            }
        }
        for(int i=0; i<dungeonMap.length; i++){
            for(int j=0; j<dungeonMap[0].length; j++){
                dungeonMap[i][j].setThingNum(dungeonThingMap[i][j].getTileNum());
            }
        }
        for(int i=0; i<battleFieldMap.length; i++){
            for(int j=0; j<battleFieldMap[0].length; j++){
                battleFieldMap[i][j].setThingNum(battleFieldThingMap[i][j].getTileNum());
            }
        }
        for(int i=0; i<imgMap.length; i++){
            for(int j=0; j<imgMap[0].length; j++){
                imgMap[i][j] = new ImageView();
                imgMap[i][j].setFitHeight(cellSize);
                imgMap[i][j].setFitWidth(cellSize);
                gPaneBoard.add(imgMap[i][j], j, i);

                imgThingMap[i][j] = new ImageView();
                imgThingMap[i][j].setFitHeight(cellSize);
                imgThingMap[i][j].setFitWidth(cellSize);
                gPaneBoard.add(imgThingMap[i][j], j, i);
            }
        }

        // Action events
        worldMap[27][27].setActionNum(2);
        worldMap[0][0].setActionNum(1);
        villageMap[24][12].setActionNum(1);
        dungeonMap[0][27].setActionNum(2);
        dungeonMap[2][24].setWeapon(getWeapon("bronze sword"));
        dungeonMap[11][14].setActionNum(1);
    }
    public void handleUse(){
            for(int i=0; i<player.getItems().size(); i++){
                if(item != null && item.startsWith(player.getItems().get(i).getName())){
                    player.getItems().get(i).useItem(player);
                }
            }
    }



    public void checkResult(){
        if(player.getHp() <= 0){
            handleGameOver();
        }
        if(currentEnemy.getHp() <= 0){
            handleWinBattle();

        }
    }

    public void handleBeginning(){
        eventTA.setVisible(true);
        eventTA.setText("Your goal is to defeat the boss");
    }

    public Weapon getSelectedWeapon(String weapon){
        for(int i=0; i<player.getHaveWeapons().size(); i++){
            for(int j=0; j<player.getHaveWeapons().get(i).size(); j++){
                if(player.getHaveWeapons().get(i).get(j).getName().equals(weapon)){
                    return player.getHaveWeapons().get(i).get(j);
                }
            }
        }
        return null;
    }
    public Weapon getWeapon(String name){
        for(Weapon weapon : weaponsList){
            if(weapon.getName().equals(name)){
                return weapon;
            }
        }
        return null;
    }

    public void handleGameOver(){
        player.setHp(0);
        tPane.getSelectionModel().select(2);
    }
    public void handleWinBattle(){
        enemyTime = -1;
        attackedTime = -1;
        inBattle = false;
        enemyIV.setImage(null);
        desTA.setText("You defeated " + currentEnemy.getName()+"." + "\n" + "You got " + currentEnemy.getDropExp() + " experience point.");
        player.addEp(currentEnemy.getDropExp());
        if(player.canLevelUp()) {
            player.levelUp();
            updateStats();
        }
        currentEnemies.add(getEnemy(currentEnemy.getEnemyNum()));
        currentEnemies.remove(currentEnemy);
        gpMenu.setDisable(false);
        for(int i=0; i<5; i++){
            gpMenu.getChildren().get(i).setDisable(true);
        }
        updateStats();
    }

    private boolean canMove = false;
    public boolean canGo(String direction){
        ImageHandler playerImg = player.getImageHandler();
        if(direction.equals("right")){
            if(playerImg.getMapCol() + 1 >= drawMap[0].length || !canMove) return false;
            if(drawMap[playerImg.getMapRow()][playerImg.getMapCol() + 1].getThingNum() == 0 ||drawMap[playerImg.getMapRow()][playerImg.getMapCol() + 1].getActionNum() != 0){
                return true;
            }
        }
        if(direction.equals("down")){
            if(playerImg.getMapRow() + 1 >= drawMap.length || !canMove) return false;
            if(playerImg.getMapRow() + 1 < drawMap.length && drawMap[playerImg.getMapRow() + 1][playerImg.getMapCol()].getThingNum() == 0 || drawMap[playerImg.getMapRow() + 1][playerImg.getMapCol()].getActionNum() != 0){
                return true;
            }
        }
        if(direction.equals("left")){
            if(playerImg.getMapCol() - 1 < 0 || !canMove) return false;
            if(playerImg.getMapCol() - 1 > -1 && drawMap[playerImg.getMapRow()][playerImg.getMapCol() - 1].getThingNum() == 0 || drawMap[playerImg.getMapRow()][playerImg.getMapCol() - 1].getActionNum() != 0){
                return true;
            }
        }
        if(direction.equals("up")){
            if(playerImg.getMapRow() - 1 < 0 || !canMove) return false;
            if(playerImg.getMapRow() - 1 > -1 && drawMap[playerImg.getMapRow() - 1][playerImg.getMapCol()].getThingNum() == 0 || drawMap[playerImg.getMapRow() - 1][playerImg.getMapCol()].getActionNum() != 0){
                return true;
            }
        }
        return false;
    }


    private boolean gameStarted = false;
    public void handleSpaceBar(){
        if(!gameStarted){
            firstEnemyTime = 1;
            spriteTime = System.nanoTime();
            canMove = true;
            gameStarted = true;
        }
        if(eventTA.isVisible()) {
            eventTA.clear();
            eventTA.setVisible(false);
            canMove = true;
        } else {
            player.getImageHandler().checkAround(drawMap, 1, player, eventTA);
            updateEquipList();
            drawMap();
            canMove = false;
        }



    }


    public void moveUp(){
        player.getImageHandler().setFlameX(2);
        if(!inBattle && canGo("up")){
            player.getImageHandler().moveUp(drawMap, viewMap.length);
            encounterEnemy();
            drawMap();
            checkSpot();
        }
    }

    public void moveDown(){
        player.getImageHandler().setFlameX(0);
        if(!inBattle && canGo("down")){
            player.getImageHandler().moveDown(drawMap, viewMap.length);
            encounterEnemy();
            drawMap();
            checkSpot();
        }
    }
    public void moveRight(){
        player.getImageHandler().setFlameX(3);
        if(!inBattle && canGo("right")){
            player.getImageHandler().moveRight(drawMap, viewMap[0].length);
            encounterEnemy();
            drawMap();
            checkSpot();
        }
    }
    public void moveLeft(){
        player.getImageHandler().setFlameX(1);
        if(!inBattle && canGo("left")){
            player.getImageHandler().moveLeft(drawMap, viewMap[0].length);
            encounterEnemy();
            drawMap();
            checkSpot();
        }
    }

    public void deleteStatsDisplay(){
        boss.getImageHandler().getImageView().setVisible(true);
        statsDisplayRect.setVisible(false);
        lblStatsHP.setVisible(false);
        lblStatsMP.setVisible(false);
        lblStatsLevel.setVisible(false);
        lblStatsName.setVisible(false);
        lblL.setVisible(false);
        lblH.setVisible(false);
        lblM.setVisible(false);
    }






    private ArrayList<Weapon> weaponsList= new ArrayList<>();
    public void loadWeapons(){
        for(int i=0; i<tempRow.size(); i++){
            String [] tempArr;
            tempArr = tempRow.get(i).split((",  "));
            Weapon tempWeapon = new Weapon(Integer.parseInt(tempArr[0]), tempArr[1], Integer.parseInt(tempArr[2]), Integer.parseInt(tempArr[3]), Integer.parseInt(tempArr[4]), Integer.parseInt(tempArr[5]));
            weaponsList.add(tempWeapon);
        }
    }
    public Tiles[][] loadMap(){
        Tiles [][] loadMap = new Tiles[0][];
        for(int i=0; i<tempRow.size(); i++){
            String[] tempArr;
            tempArr = tempRow.get(i).split(", ");
            if(first){
                loadMap = new Tiles[tempRow.size()][tempArr.length];
                first = false;
            }
            for(int j=0; j<tempArr.length; j++){
                loadMap[i][j] = new Tiles(Integer.parseInt(tempArr[j]), 0, 0);
            }
        }
        first = true;
        return loadMap;
    }
    private Tiles [][] worldMap;
    private Tiles [][] worldThingMap;
    private Tiles [][] villageMap;
    private Tiles [][] villageThingMap;
    private Tiles [][] dungeonMap;
    private Tiles [][] dungeonThingMap;
    private Tiles [][] battleFieldMap;
    private Tiles [][] battleFieldThingMap;
    private boolean first = true;
    private ArrayList<String> tempRow = new ArrayList<>();
    public void handleLoad(){
        try{
            FileReader tempReader;
            Scanner tempScanner;
            // Load Maps
            tempReader = new FileReader("src/main/resources/maps/world.txt");
            tempScanner = new Scanner(tempReader);
            while(tempScanner.hasNextLine()) {
                String temp = tempScanner.nextLine();
                tempRow.add(temp);
            }
            worldMap = loadMap();
            tempRow.clear();
            tempReader = new FileReader("src/main/resources/maps/village.txt");
            tempScanner = new Scanner(tempReader);
            while(tempScanner.hasNextLine()){
                String temp = tempScanner.nextLine();
                tempRow.add(temp);
            }
            villageMap = loadMap();
            tempRow.clear();
            tempReader = new FileReader("src/main/resources/maps/villageThing.txt");
            tempScanner = new Scanner(tempReader);
            while(tempScanner.hasNextLine()){
                String temp = tempScanner.nextLine();
                tempRow.add(temp);
            }
            villageThingMap = loadMap();
            tempRow.clear();
            tempReader = new FileReader("src/main/resources/maps/dungeon.txt");
            tempScanner = new Scanner(tempReader);
            while(tempScanner.hasNextLine()){
                String temp = tempScanner.nextLine();
                tempRow.add(temp);
            }
            dungeonMap = loadMap();
            tempRow.clear();
            tempReader = new FileReader("src/main/resources/maps/dungeonThing.txt");
            tempScanner = new Scanner(tempReader);
            while(tempScanner.hasNextLine()){
                String temp = tempScanner.nextLine();
                tempRow.add(temp);
            }
            dungeonThingMap = loadMap();
            tempRow.clear();
            tempReader = new FileReader("src/main/resources/maps/worldThing.txt");
            tempScanner = new Scanner(tempReader);
            while(tempScanner.hasNextLine()){
                String temp = tempScanner.nextLine();
                tempRow.add(temp);
            }
            worldThingMap = loadMap();
            tempRow.clear();
            tempReader = new FileReader("src/main/resources/maps/battleField.txt");
            tempScanner = new Scanner(tempReader);
            while(tempScanner.hasNextLine()){
                String temp = tempScanner.nextLine();
                tempRow.add(temp);
            }
            battleFieldMap = loadMap();
            tempRow.clear();
            tempReader = new FileReader("src/main/resources/maps/battleFieldThing.txt");
            tempScanner = new Scanner(tempReader);
            while(tempScanner.hasNextLine()){
                String temp = tempScanner.nextLine();
                tempRow.add(temp);
            }
            battleFieldThingMap = loadMap();

            // Load Weapons
            tempRow.clear();
            tempReader = new FileReader("src/main/resources/weapons.txt");
            tempScanner = new Scanner(tempReader);
            while (tempScanner.hasNextLine()){
                String temp = tempScanner.nextLine();
                tempRow.add(temp);
            }
            loadWeapons();


        } catch (FileNotFoundException var){
            System.out.println("Something went wrong");
        }
    }

    private Rectangle battleHealth, battleStats;
    public void updateBattleStats(){
        battleHealth = new Rectangle(gPaneBoard.getLayoutX() + cellSize*2.5, gPaneBoard.getLayoutY() + (viewMap.length - 1) * cellSize, cellSize * (healthBar), cellSize/1.2);
        battleHealth.setVisible(true);
        battleStats.setVisible(true);
        lblBattleName.setVisible(true);
        battleHealth.setFill(Color.rgb(255, 0, 0));
        battleHealth.setStroke(Color.BLACK);
        aPane1.getChildren().add(battleHealth);

    }


    public void startBossBattle(){
        boss.setAttackTime(System.nanoTime());
        deleteStatsDisplay();
        updateBattleStats();

    }

    public boolean getChance(int chanceNum){
        switch(chanceNum){
            case 0:
                return false;
            // 5% chance
            case 1:
                return Math.random()*100000 > 95000;
            // 10% chance
            case 2:
                return Math.random()*100000 > 90000;
            // 15% chance
            case 3:
                return Math.random()*100000 > 85000;
            // 30% chance
            case 4:
                return Math.random()*100000 > 70000;
            // 50% chance
            case 5:
                return Math.random()*100000 > 50000;
            // 60% chance
            case 6:
                return Math.random()*100000 > 40000;
            // 70% chance
            case 7:
                return Math.random()*100000 > 30000;
            // 80% chance
            case 8:
                return Math.random()*100000 > 20000;
            // 90% chance
            case 9:
                return Math.random()*100000 > 10000;
        }
        return true;
    }

    public void setupImage(){
        FileInputStream tempFIS = null;
        try {
            tempFIS = new FileInputStream("src/main/resources/images/grass1.png");
            grass1 = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/grass2.png");
            grass2 = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/house.png");
            house1 = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/rock.png");
            rock = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/tree1.png");
            tree1 = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/tree2.png");
            tree2 = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/chestOpened.png");
            chestClosed = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/chestClosed.png");
            chestOpened = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/lake.png");
            lake = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/fenceTop.png");
            fenceTop = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/fenceRight.png");
            fenceRight = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/fenceBottom.png");
            fenceBottom = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/fenceLeft.png");
            fenceLeft = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/fenceTopLeft.png");
            fenceTopLeft = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/fenceTopRight.png");
            fenceTopRight = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/fenceBottomRight.png");
            fenceBottomRight = new Image(tempFIS);
            tempFIS = new FileInputStream("src/main/resources/images/fenceBottomLeft.png");
            fenceBottomLeft= new Image(tempFIS);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void checkCollision(){
        for(int i=0; i<boss.getSpells().size(); i++){
            if(boss.getSpells().get(i).getImageHandler().getScreenRow() == player.getImageHandler().getScreenRow()
            && boss.getSpells().get(i).getImageHandler().getScreenCol() == player.getImageHandler().getScreenCol()
            && !boss.getSpells().get(i).isHit()){
                boss.getSpells().get(i).setHit(true);
                healthBar -= 1;
                aPane1.getChildren().remove(battleHealth);
                updateBattleStats();
            }
        }
    }


    private long beginBattleTime = -1;
    private long spriteTime = -1;
    private long attackedTime = -1;
    private long enemyTime = -1;
    private long firstEnemyTime = -1;
//    private long bulletTime = -1;
    boolean done = false;


    public void start(){
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkCollision();

                if(spriteTime > 0){
                    if(now - spriteTime > 150000000.0){
                        spriteTime = System.nanoTime();
                        if(firstEnemyTime > 0){
                            if(firstEnemy.getFlameY() >= 2 && firstEnemy.getFlameX() >= 3){
                                firstEnemyTime = -1;
                            } else if(firstEnemy.getFlameX() >= 3){
                                firstEnemy.setFlameX(0);
                                firstEnemy.addFlameY();
                            }
                            firstEnemy.addFlameX();
                            aPane1.getChildren().remove(firstEnemy.getImageView());
                            aPane1.getChildren().add(firstEnemy.getImageView());
                        }
                        if(player.getImageHandler().getFlameY() >= 3){
                            player.getImageHandler().setFlame(player.getImageHandler().getFlameX(), 0);
                        }
                        if(boss.getImageHandler().getFlameX() >= 3 && boss.getImageHandler().getFlameY() == 0){
                            boss.getImageHandler().setFlame(0, 1);
                        }
                        if(boss.getImageHandler().getFlameX() >= 3 && boss.getImageHandler().getFlameY() == 1){
                            boss.getImageHandler().setFlame(0, 0);
                        }

                        for(int i=0; i<boss.getSpells().size(); i++){
                            if(boss.getSpells().get(i).getImageHandler().getFlameX() >= 4){
                                boss.getSpells().get(i).getImageHandler().setFlameX(0);
                            }
                            boss.getSpells().get(i).getImageHandler().addFlameX();
                        }
                        player.getImageHandler().addFlameY();
                        boss.getImageHandler().addFlameX();
                        drawBullet();
                        drawBoss();
                        drawMap();
                    }
                }

                if(attackedTime > 0){
                    if(now - attackedTime > 100000000.0){
                        attackedTime = -1;
                        enemyIV.setImage(currentEnemy.getImageHandler().getImage());
                    }
                }
                if(enemyTime > 0){
                    if(!done && now - enemyTime > 1500000000.0){
                        enemyTime = System.nanoTime();
                        currentEnemy.attack(player, desTA);
                        rect2.setFill(Color.WHITE);
                        done = true;
                        checkResult();
                    }
                    if(done && now - enemyTime > 150000000.0){
                        enemyTime = -1;
                        rect2.setFill(Color.rgb(43,43,43));
                        gpMenu.setDisable(false);
                        done = false;
                    }
                }
                if(beginBattleTime > 0){
                    if(now - beginBattleTime > 300000000.0){
                        beginBattleTime = System.nanoTime();

                    }
                }
                if(boss.getMoveTime() > 0){
                    if(now - boss.getMoveTime() > 1000000000){
                        boss.setMoveTime(System.nanoTime());
                        boss.getImageHandler().addScreenRow(1);
                    }
                }
                if(boss.getAttackTime() > -1){
                    if(now - boss.getAttackTime() > 3000000000.0){
                        boss.castMagicBullet();
                        boss.setAttackTime(System.nanoTime());
                    }

                }
                if(boss.getSpells().size() > 0){
                    for(int i=0; i<boss.getSpells().size(); i++){
                        ArrayList<Spell> spells = boss.getSpells();
                        if(now - spells.get(i).getDrawTime() > 400000000.0){
                            boss.getSpells().get(i).setHit(false);
                            spells.get(i).setDrawTime(System.nanoTime());
                            if(spells.get(i).getDirection() == 0){
                                spells.get(i).getImageHandler().addScreenRow(-1);
                            } else if(spells.get(i).getDirection() == 1){
                                spells.get(i).getImageHandler().addScreenRow(-1);
                                spells.get(i).getImageHandler().addScreenCol(1);
                            } else if(spells.get(i).getDirection() == 2){
                                spells.get(i).getImageHandler().addScreenCol(1);
                            } else if(spells.get(i).getDirection() == 3){
                                spells.get(i).getImageHandler().addScreenRow(1);
                                spells.get(i).getImageHandler().addScreenCol(1);
                            } else if(spells.get(i).getDirection() == 4){
                                spells.get(i).getImageHandler().addScreenRow(1);
                            } else if(spells.get(i).getDirection() == 5){
                                spells.get(i).getImageHandler().addScreenRow(1);
                                spells.get(i).getImageHandler().addScreenCol(-1);
                            } else if(spells.get(i).getDirection() == 6){
                                spells.get(i).getImageHandler().addScreenCol(-1);
                            } else if(spells.get(i).getDirection() == 7){
                                spells.get(i).getImageHandler().addScreenRow(-1);
                                spells.get(i).getImageHandler().addScreenCol(-1);
                            }
                            if(spells.get(i).getImageHandler().getScreenRow() < 0 || spells.get(i).getImageHandler().getScreenRow() > viewMap.length-1 || spells.get(i).getImageHandler().getScreenCol() < 0 || spells.get(i).getImageHandler().getScreenCol() > viewMap[0].length-1){
                                aPane1.getChildren().remove(spells.get(i).getImageHandler().getImageView());
                                spells.remove(spells.get(i));
                                i--;
                            }
                            drawBullet();
                        }

                    }
                }
            }
        }.start();
    }
}