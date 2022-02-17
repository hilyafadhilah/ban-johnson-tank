package ban.johnson.tank.entities;

import com.google.gson.annotations.SerializedName;
import ban.johnson.tank.enums.PowerUps;
import ban.johnson.tank.enums.State;

public class Car {
    @SerializedName("id")
    public int id;

    @SerializedName("position")
    public Position position;

    @SerializedName("speed")
    public int speed;

    @SerializedName("damage")
    public int damage;

    @SerializedName("state")
    public State state;

    @SerializedName("powerups")
    public PowerUps[] powerups;

    @SerializedName("boosting")
    public Boolean boosting;

    @SerializedName("boostCounter")
    public int boostCounter;
}
