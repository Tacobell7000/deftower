package domain;

import dao.TowerDao;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map;

/**
 * This class contains most of the logic and keeps everything together.
 */
public class Game {

    private int hpPct;
    private int wave;
    private int gold;
    private int hitPoints;
    private Map map;
    private ArrayList<Tower> towers;
    private TowerDao towerDao;
    private ArrayList<Wave> waves;
    private ArrayList<Invader> invadersAlive;

    /**
     * Constructor for the game class.
     *
     * @param mapFileName Name for the .txt file that is used for building the map. This file is located in application folder.
     * @param hpPct       This value changes the % of invader hitpoints. With this the difficulty of the game can be altered. Default value is 100.
     */
    public Game(String mapFileName, int hpPct) {
        this.hpPct = hpPct;
        this.wave = 0;
        this.gold = 50000;
        this.hitPoints = 20;
        this.towers = new ArrayList<>();
        this.map = new Map(mapFileName);
        this.towerDao = new TowerDao();
        this.waves = createWaves();
        this.invadersAlive = new ArrayList<>();
    }

    /**
     * Building of towers is done here. It checks if building a tower is even possible and if true it adds the tower to towers ArrayList.
     *
     * @param typeId Different towers have different id's in the database so this makes getting the right tower stats possible
     * @param x      x coordinate for where the turret is going to be build
     * @param y      y coordinate for where the turret is going to be build
     */
    public void buildTower(int typeId, double x, double y) {

        Tower tower = this.towerDao.getTowerById(typeId);

        tower.convertXY(x, y);

        if (tower.getCostToBuild() <= this.gold && towerCanBeBuiltThere(tower)) {
            reduceGold(tower.getCostToBuild());
            this.towers.add(tower);

        } else {
            System.out.println("Cant build a tower there.");
        }
    }

    /**
     * Checks if there are any towers already built on the spot in question and then checks if the tile is grass.
     *
     * @param tower Tower to be examined
     * @return Returns true if a tower can be built in the tower's coordinates.
     */
    public boolean towerCanBeBuiltThere(Tower tower) {

        for (Tower index : this.towers) {
            if (tower.equals(index)) {
                return false;
            }
        }

        int[][] map = getMapRoute();
        if (map[(int) tower.getY()][(int) tower.getX()] != 0) {
            return false;
        }

        return true;
    }

    /**
     * Creates waves of invaders
     * @return Returns ArrayList with waves
     */
   private ArrayList<Wave> createWaves() {
    ArrayList<Wave> waves = new ArrayList<>();

    for (int i = 0; i < 1000; i++) {
        ArrayList<Invader> invaders = new ArrayList<>();

        for (int j = 0; j <= i; j++) {
            invaders.add(new Invader(100, 30, getPathThroughMap()));
        }

        waves.add(new Wave(i, invaders, (i + 1) * 10));
    }

    return waves;
}
    /**
     * Moves all invaders
     */
    public void moveAllInvaders(){
        for (Invader invader: this.invadersAlive){
            invader.move();
        }
    }

    /**
     * Starts next wave and spawns invaders
     */
    public void nextWave() {
    if (this.wave >= this.waves.size()) {
        return;
    }

    for (int i = 0; i <= this.wave; i++) {
        Invader invader = this.waves.get(this.wave).spawnInvader();

        if (invader != null) {
            this.invadersAlive.add(invader);
        }
    }
}

    /**
     * Attacks with all towers. Removes an invader if it dies.
     */
    public void attackWithAllTowers(){
        for(Tower tower: this.towers){
            for(Invader invader: this.invadersAlive){
                if(tower.isInAttackRange(invader)){
                    this.gold += tower.attackInvader(invader);
                    break;
                }
            }
        }
        ArrayList<Invader> invadersAliveCopy = new ArrayList<>();
        for(Invader invader: this.invadersAlive){
            if(invader.getHp() > 0){
                invadersAliveCopy.add(invader);
            }
        }
        this.invadersAlive=invadersAliveCopy;
        //round won
        if(this.invadersAlive.isEmpty()){
            this.gold+=this.waves.get(this.wave).getEndRoundBonusGold();
            this.wave++;
        }
    }

    public void reduceGold(int amount) {
        this.gold -= amount;
    }

    public int getGold() {
        return this.gold;
    }

    public int getWave() {
        return this.wave;
    }

    public int[][] getMapRoute() {
        return this.map.getMapRoute();
    }

    public String getMapName() {
        return this.map.getName();
    }

    public int getCurrentHitPoints() {
        return this.hitPoints;
    }

    public int getCurrentGold() {
        return this.gold;
    }

    public ArrayList<Tower> getTowers() {
        return this.towers;
    }

    public ArrayList<int[]> getPathThroughMap() {
        return this.map.getPathThroughMap();
    }

    public ArrayList<Invader> getInvadersAlive() {
        return this.invadersAlive;
    }

}
