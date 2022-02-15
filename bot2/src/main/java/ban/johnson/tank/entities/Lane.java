package ban.johnson.tank.entities;

import com.google.gson.annotations.SerializedName;
import ban.johnson.tank.enums.Terrain;

public class Lane {
    @SerializedName("position")
    public Position position;

    @SerializedName("surfaceObject")
    public Terrain terrain;

    @SerializedName("occupiedByPlayerId")
    public int occupiedByPlayerId;

    @SerializedName("isOccupiedByCyberTruck")
    public boolean isOccupiedByCyberTruck;
}
