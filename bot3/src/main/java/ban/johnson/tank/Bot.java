package ban.johnson.tank;

import ban.johnson.tank.command.*;
import ban.johnson.tank.entities.*;
import ban.johnson.tank.enums.PowerUps;
import ban.johnson.tank.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

import java.security.SecureRandom;

public class Bot {

    private static final int maxSpeed = 9;
    private List<Command> directionList = new ArrayList<>();

    private final Random random;

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot() {
        this.random = new SecureRandom();
        directionList.add(TURN_LEFT);
        directionList.add(TURN_RIGHT);
    }

    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;
        int lane = myCar.position.lane;

        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block, myCar.speed, gameState);

        // Fix first if too damaged to move
        if (myCar.damage >= 2) {
            return FIX;
        }

        // Accelerate first if going to slow
        if (myCar.speed <= 3) {
            return ACCELERATE;
        }

        // Seek for power up (ignore if already have 5 powerup each type (gajadi))
        // EMP > LIZARD > BOOST > TWEET > OIL_POWER
        // { OIL_POWER, BOOST, LIZARD, TWEET, EMP }
        // 0 1 2 3 4
        if (myCar.speed >= 8) {
            int powerupmid[] = countPowerUp(blocks, myCar.powerups);
            if (lane == 1) {
                List<Object> right = getBlocksInFront(lane + 1, myCar.position.block, myCar.speed - 1, gameState);
                int powerupright[] = countPowerUp(right, myCar.powerups);
                if (powerupright[4] > powerupmid[4]) {
                    return TURN_RIGHT;
                } else if (powerupright[4] == powerupmid[4]) {
                    if (powerupright[2] > powerupmid[2]) {
                        return TURN_RIGHT;
                    } else if (powerupright[2] == powerupmid[2]) {
                        if (powerupright[1] > powerupmid[1]) {
                            return TURN_RIGHT;
                        } else if (powerupright[1] == powerupmid[1]) {
                            if (powerupright[3] > powerupmid[3]) {
                                return TURN_RIGHT;
                            } else if (powerupright[3] == powerupmid[3]) {
                                if (powerupright[0] > powerupmid[0]) {
                                    return TURN_RIGHT;
                                }
                            }
                        }
                    }
                }
            } else if (lane == 4) {
                List<Object> left = getBlocksInFront(lane - 1, myCar.position.block, myCar.speed - 1, gameState);
                int powerupleft[] = countPowerUp(left, myCar.powerups);
                if (powerupleft[4] > powerupmid[4]) {
                    return TURN_LEFT;
                } else if (powerupleft[4] == powerupmid[4]) {
                    if (powerupleft[2] > powerupmid[2]) {
                        return TURN_LEFT;
                    } else if (powerupleft[2] == powerupmid[2]) {
                        if (powerupleft[1] > powerupmid[1]) {
                            return TURN_LEFT;
                        } else if (powerupleft[1] == powerupmid[1]) {
                            if (powerupleft[3] > powerupmid[3]) {
                                return TURN_LEFT;
                            } else if (powerupleft[3] == powerupmid[3]) {
                                if (powerupleft[0] > powerupmid[0]) {
                                    return TURN_LEFT;
                                }
                            }
                        }
                    }
                }
            } else {
                List<Object> right = getBlocksInFront(lane + 1, myCar.position.block, myCar.speed - 1, gameState);
                List<Object> left = getBlocksInFront(lane - 1, myCar.position.block, myCar.speed - 1, gameState);
                int powerupright[] = countPowerUp(right, myCar.powerups);
                int powerupleft[] = countPowerUp(left, myCar.powerups);
                if (powerupleft[4] > powerupmid[4] && powerupleft[4] > powerupright[4]) {
                    return TURN_LEFT;
                } else if (powerupright[4] > powerupmid[4] && powerupright[4] > powerupmid[4]) {
                    return TURN_RIGHT;
                } else if (powerupleft[4] == powerupright[4] && powerupmid[4] <= powerupleft[4]) {
                    if (powerupleft[2] > powerupmid[2] && powerupleft[2] > powerupright[2]) {
                        return TURN_LEFT;
                    } else if (powerupright[2] > powerupmid[2] && powerupright[2] > powerupmid[2]) {
                        return TURN_RIGHT;
                    } else if (powerupleft[2] == powerupright[4] && powerupmid[2] <= powerupleft[2]) {
                        if (powerupleft[1] > powerupmid[1] && powerupleft[1] > powerupright[1]) {
                            return TURN_LEFT;
                        } else if (powerupright[1] > powerupmid[1] && powerupright[1] > powerupmid[1]) {
                            return TURN_RIGHT;
                        } else if (powerupleft[1] == powerupright[1] && powerupmid[1] <= powerupleft[1]) {
                            if (powerupleft[3] > powerupmid[3] && powerupleft[3] > powerupright[3]) {
                                return TURN_LEFT;
                            } else if (powerupright[3] > powerupmid[3] && powerupright[3] > powerupmid[3]) {
                                return TURN_RIGHT;
                            } else if (powerupleft[3] == powerupright[3] && powerupmid[3] <= powerupleft[3]) {
                                if (powerupleft[0] > powerupmid[0] && powerupleft[0] > powerupright[0]) {
                                    return TURN_LEFT;
                                } else if (powerupright[0] > powerupmid[0] && powerupright[0] > powerupmid[0]) {
                                    return TURN_RIGHT;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Use power up
        if (hasPowerUp(PowerUps.EMP, myCar.powerups) &&
                opponent.position.block >= myCar.position.block) {
            return EMP;
        }
        if (blocks.contains(Terrain.MUD) ||
                blocks.contains(Terrain.WALL) ||
                blocks.contains(Terrain.OIL_SPILL)) {
            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                List<Object> nextBlock = blocks.subList(blocks.size() - 1, blocks.size());
                if (!(nextBlock.contains(Terrain.MUD) ||
                        nextBlock.contains(Terrain.WALL) ||
                        nextBlock.contains(Terrain.OIL_SPILL))) {
                    return LIZARD;
                }
            }
        }
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            List<Object> boosted = getBlocksInFront(myCar.position.lane, myCar.position.block, 15,
                    gameState);
            if (!(boosted.contains(Terrain.MUD) ||
                    boosted.contains(Terrain.WALL) ||
                    boosted.contains(Terrain.OIL_SPILL))) {
                return BOOST;
            }
        }
        if (hasPowerUp(PowerUps.TWEET, myCar.powerups) &&
                myCar.speed == maxSpeed) {
            return new TweetCommand(opponent.position.lane, opponent.position.block + opponent.speed);
        }
        if (hasPowerUp(PowerUps.OIL, myCar.powerups) &&
                opponent.position.lane == myCar.position.lane &&
                opponent.position.block < myCar.position.block) {
            return OIL;
        }

        // Menghindar
        int[] countObstacles = countObstacles(blocks);
        if (countObstacles[0] > 0 || countObstacles[1] > 0) {
            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                List<Object> nextBlock = blocks.subList(blocks.size() - 1, blocks.size());
                if (!(nextBlock.contains(Terrain.MUD) ||
                        nextBlock.contains(Terrain.WALL) ||
                        nextBlock.contains(Terrain.OIL_SPILL) ||
                        nextBlock.contains(Terrain.CYBER_TRUCK))) {
                    return LIZARD;
                }
            }

            if (lane == 1) {
                List<Object> right = getBlocksInFront(lane + 1, myCar.position.block, myCar.speed - 1, gameState);
                int countrightObstacles[] = countObstacles(right);

                if ((countObstacles[1] > 0 && countrightObstacles[1] > 0) ||
                        (countrightObstacles[1] == 0) ||
                        countObstacles[1] > countrightObstacles[1] ||
                        countObstacles[0] > countrightObstacles[0]) {
                    return TURN_RIGHT;
                }

            } else if (lane == 4) {
                List<Object> left = getBlocksInFront(lane - 1, myCar.position.block, myCar.speed - 1, gameState);
                int countleftObstacles[] = countObstacles(left);

                if ((countObstacles[1] > 0 && countleftObstacles[1] > 0) ||
                        (countleftObstacles[1] == 0) ||
                        countObstacles[1] > countleftObstacles[1] ||
                        countObstacles[0] > countleftObstacles[0]) {
                    return TURN_LEFT;
                }

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

    private int[] countPowerUp(List<Object> blocks, PowerUps[] available) {
        int[] count = { 0, 0, 0, 0, 0 }; // { OIL_POWER, BOOST, LIZARD, TWEET, EMP }

        for (Object block : blocks) {
            if (block == Terrain.OIL_POWER) {
                count[0]++;
            } else if (block == Terrain.BOOST) {
                count[1]++;
            } else if (block == Terrain.LIZARD) {
                count[2]++;
            } else if (block == Terrain.TWEET) {
                count[3]++;
            } else if (block == Terrain.EMP) {
                count[4]++;
            }
        }

        int[] hascount = hasCountPowerUp(available);
        for (int i = 0; i < count.length; i++) {
            if (hascount[i] > 5) {
                count[i] = 0;
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
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
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
