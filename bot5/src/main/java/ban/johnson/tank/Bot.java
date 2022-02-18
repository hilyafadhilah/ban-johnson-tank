package ban.johnson.tank;

import ban.johnson.tank.command.*;
import ban.johnson.tank.entities.*;
import ban.johnson.tank.enums.PowerUps;
import ban.johnson.tank.enums.State;
import ban.johnson.tank.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

public class Bot {

    private static final int maxSpeed = 9;
    private List<Command> directionList = new ArrayList<>();

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot() {
        directionList.add(TURN_LEFT);
        directionList.add(TURN_RIGHT);
    }

    public Command run(GameState gameState) {
        // Mengambil state player bot dan player musuh
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;

        // Strategi jika terkena EMP oleh musuh yaitu dengan membalasnya
        // menggunakan OIL / TWEET / EMP (jika lawan menyalip bot)
        if (myCar.state != null && myCar.state.equals(State.HIT_EMP)) {
            if (hasPowerUp(PowerUps.EMP, myCar.powerups) && opponent.position.block >= myCar.position.block) {
                return EMP;
            }
            if (myCar.damage > 0) {
                return FIX;
            }
            if (hasPowerUp(PowerUps.TWEET, myCar.powerups)) {
                // Menaruh Truck di depan musuh sejauh speed + 1
                return new TweetCommand(opponent.position.lane, opponent.position.block + opponent.speed + 1);
            }
            if (hasPowerUp(PowerUps.OIL, myCar.powerups)) {
                return OIL;
            }
        }

        // Memperbaiki mobil jika telah rusak sebanyak 2 atau lebih
        if (myCar.damage >= 2) {
            return FIX;
        }

        // Menginjak gas jika kecepatan terlalu lambat (3 atau kurang)
        if (myCar.speed <= 3) {
            return ACCELERATE;
        }

        // Mengambil data block lane saat ini
        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block + 1, myCar.speed - 1,
                gameState);

        // Mengambil data obstacle lane saat ini
        int countObstacles[] = countObstacles(blocks);
        // indeks 0 berisi count obstacle mud + oil_spill
        // indeks 1 berisi count obstacle wall + truck

        // Algoritma menghindar
        // Mencari lane dengan obstacle paling kecil dari lane yang dapat dilalui,
        // dengan value wall = truck > mud = oil_spill

        if (countObstacles[0] > 0 || countObstacles[1] > 0) {

            // Strategi Lizard X Boost yang dipakai pada bot, Lizard hanya digunakan
            // jika obstacle didepan berjumlah lebih dari 3 atau jika mobil sedang di BOOST
            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                if ((countObstacles[0] + countObstacles[1]) > 3 || myCar.speed == 15) {
                    return LIZARD;
                }
            }

            int lane = myCar.position.lane; // lane saat ini

            // Algoritma menghindar jika berada di lane 1 (hanya bisa lurus atau belok
            // kanan)
            if (lane == 1) {
                List<Object> right = getBlocksInFront(lane + 1, myCar.position.block, myCar.speed - 1, gameState);
                // Data block di lane sebelah kanan bot
                int countrightObstacles[] = countObstacles(right);
                // Mengambil data obstacle di lane kanan bot
                if ((countObstacles[1] > 0 && countrightObstacles[1] > 0) ||
                        (countrightObstacles[1] == 0) ||
                        countObstacles[1] > countrightObstacles[1] ||
                        countObstacles[0] > countrightObstacles[0] ||
                        (countObstacles[1] == countrightObstacles[1] &&
                                countObstacles[0] == countrightObstacles[0])) {
                    return TURN_RIGHT;
                }

                // Algoritma menghindar jika berada di lane 1 (hanya bisa lurus atau belok kiri)
            } else if (lane == 4) {
                List<Object> left = getBlocksInFront(lane - 1, myCar.position.block, myCar.speed - 1, gameState);
                // Data block di lane sebelah kiri bot
                int countleftObstacles[] = countObstacles(left);
                // Mengambil data obstacle di lane kiri bot
                if ((countObstacles[1] > 0 && countleftObstacles[1] > 0) ||
                        (countleftObstacles[1] == 0) ||
                        countObstacles[1] > countleftObstacles[1] ||
                        countObstacles[0] > countleftObstacles[0] ||
                        (countObstacles[1] == countleftObstacles[1] ||
                                countObstacles[0] == countleftObstacles[0])) {
                    return TURN_LEFT;
                }

                // Algoritma menghindar jika berada di lane 2 atau 3 (bisa lurus, belok kanan,
                // atau belok kiri)
            } else {
                List<Object> left = getBlocksInFront(lane - 1, myCar.position.block, myCar.speed - 1, gameState);
                List<Object> right = getBlocksInFront(lane + 1, myCar.position.block, myCar.speed - 1, gameState);
                int countleftObstacles[] = countObstacles(left);
                int countrightObstacles[] = countObstacles(right);

                if (countObstacles[1] > 0) {
                    if (countleftObstacles[1] == 0 && (countrightObstacles[0] >= countleftObstacles[0])) {
                        if (countrightObstacles[1] == 0 &&
                                countrightObstacles[0] == countleftObstacles[0] &&
                                lane == 2) {
                            return TURN_RIGHT;
                        } else {
                            return TURN_LEFT;
                        }
                    } else {
                        return TURN_RIGHT;
                    }
                } else {
                    if (countleftObstacles[1] == 0 &&
                            (countrightObstacles[0] >= countleftObstacles[0]) &&
                            (countObstacles[0] > countleftObstacles[0])) {
                        if (countrightObstacles[1] == 0 &&
                                countrightObstacles[0] == countleftObstacles[0] &&
                                lane == 2) {
                            return TURN_RIGHT;
                        }
                        return TURN_LEFT;
                    } else if (countrightObstacles[1] == 0 &&
                            (countleftObstacles[0] >= countrightObstacles[0]) &&
                            (countObstacles[0] > countrightObstacles[0])) {
                        if (countleftObstacles[1] == 0 &&
                                countrightObstacles[0] == countleftObstacles[0] &&
                                lane == 3) {
                            return TURN_LEFT;
                        }
                        return TURN_RIGHT;
                    }
                }
            }
        }

        // Algoritma dasar penggunaan EMP
        if (hasPowerUp(PowerUps.EMP, myCar.powerups) &&
                opponent.position.block >= myCar.position.block &&
                opponent.speed > 3) {
            return EMP;
        }

        // Berperan pada strategi BOOST X LIZARD yaitu menggunakan BOOST jika
        // telah memiliki LIZARD > 3 dan tidak ada obstacle di depan bot saat ini
        int[] hascount = hasCountPowerUp(myCar.powerups); // Mengambil data power up yang dimiliki bot saat ini
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups) && !myCar.boosting) {
            List<Object> boosted = getBlocksInFront(myCar.position.lane, myCar.position.block + 1, 15 - 1,
                    gameState);
            if (!(boosted.contains(Terrain.MUD) ||
                    boosted.contains(Terrain.WALL) ||
                    boosted.contains(Terrain.OIL_SPILL) ||
                    boosted.contains(Terrain.CYBER_TRUCK))) {
                if ((hascount[2] >= 3 && myCar.damage == 0) ||
                        (myCar.speed <= 6 && myCar.damage > 0 && hascount[1] >= 2) ||
                        (myCar.damage == 0 && hascount[1] >= 2)) {
                    return BOOST;
                }
            }
        }

        // Algoritma dasar penggunaan TWEET dan OIL
        if (myCar.speed == maxSpeed) {
            if (hasPowerUp(PowerUps.TWEET, myCar.powerups) && myCar.speed == maxSpeed) {
                return new TweetCommand(opponent.position.lane, opponent.position.block + opponent.speed + 1);
            }
            if (hasPowerUp(PowerUps.OIL, myCar.powerups) &&
                    opponent.position.lane == myCar.position.lane &&
                    opponent.position.block < myCar.position.block) {
                return OIL;
            }
        }

        // ACCELERATE jika tidak ada satupun kondisi di atas dipenuhi
        return ACCELERATE;
    }

    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp : available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    private int[] hasCountPowerUp(PowerUps[] available) {
        // { OIL_POWER, BOOST, LIZARD, TWEET, EMP }
        int[] count = { 0, 0, 0, 0, 0 };
        for (Object powerUp : available) {
            if (powerUp.equals(PowerUps.OIL)) {
                count[0]++;
            } else if (powerUp.equals(PowerUps.BOOST)) {
                count[1]++;
            } else if (powerUp.equals(PowerUps.LIZARD)) {
                count[2]++;
            } else if (powerUp.equals(PowerUps.TWEET)) {
                count[3]++;
            } else if (powerUp.equals(PowerUps.EMP)) {
                count[4]++;
            }
        }
        return count;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns
     * the amount of blocks that can be traversed at max speed.
     **/
    private List<Object> getBlocksInFront(int lane, int block, int range, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + range; i++) {
            if (i >= laneList.length || laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            if (laneList[i].isOccupiedByCyberTruck && !laneList[i].terrain.equals(Terrain.WALL)) {
                blocks.add(Terrain.CYBER_TRUCK);
            } else {
                blocks.add(laneList[i].terrain);
            }
        }
        return blocks;
    }

    private int[] countObstacles(List<Object> blocks) {
        int[] count = { 0, 0 };

        for (Object block : blocks) {
            if (block == Terrain.MUD || block == Terrain.OIL_SPILL) {
                count[0]++;
            } else if (block == Terrain.CYBER_TRUCK || block == Terrain.WALL) {
                count[1]++;
            }
        }

        return count;
    }

}
