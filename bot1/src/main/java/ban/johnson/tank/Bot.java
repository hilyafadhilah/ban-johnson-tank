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

        // Fix first
        if (myCar.damage >= 2) {
            return FIX;
        }

        // Accelerate first if going to slow
        if (myCar.speed <= 3) {
            return ACCELERATE;
        }

        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block, myCar.speed, gameState);
        int[] obscleCount = countObstacles(blocks);

        // Basic avoidance logic
        if ((obscleCount[0] > 0 || obscleCount[1] > 0) &&
                hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
            return LIZARD;
        }

        // Basic improvement logic
        if (myCar.damage == 0 && hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            return BOOST;
        }

        // Basic aggression logic
        if (myCar.speed == maxSpeed) {
            if (hasPowerUp(PowerUps.OIL, myCar.powerups)) {
                return OIL;
            }
            if (hasPowerUp(PowerUps.EMP, myCar.powerups)) {
                return EMP;
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
