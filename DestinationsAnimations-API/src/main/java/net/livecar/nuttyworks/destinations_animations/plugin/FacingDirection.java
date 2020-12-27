package net.livecar.nuttyworks.destinations_animations.plugin;

public enum FacingDirection {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    NORTH_EAST,
    NORTH_WEST,
    SOUTH_EAST,
    SOUTH_WEST;
    
    public static FacingDirection YawToDirection(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections)
            return (new FacingDirection[]{SOUTH, SOUTH_EAST, EAST, NORTH_EAST, NORTH, NORTH_WEST,WEST, SOUTH_WEST})[Math.round(yaw / 45f) & 0x7];
        else
            return (new FacingDirection[]{SOUTH, EAST, NORTH, WEST})[Math.round(yaw / 90f) & 0x3];
    }
}
