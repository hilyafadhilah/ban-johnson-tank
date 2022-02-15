package ban.johnson.tank.entities;

import com.google.gson.annotations.SerializedName;
import ban.johnson.tank.enums.PowerUps;
import ban.johnson.tank.enums.State;

import java.util.List;

public class GameState {

    @SerializedName("currentRound")
    public int currentRound;

    @SerializedName("maxRounds")
    public int maxRounds;

    @SerializedName("player")
    public Car player;

    @SerializedName("opponent")
    public Car opponent;

    @SerializedName("worldMap")
    public List<Lane[]> lanes;

}
