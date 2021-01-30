package client.model.game;

public enum WarriorType {
    FISH_TELEPATHY("fish telepathy"),
    KRAKEN_RELEASE("kraken release"),
    POSEIDON_TRIDENT("poseidon trident"),
    SEA_THUNDERS("sea thunders"),
    UNDERSEA_FIRE("undersea fire"),
    WAVE_CONTROL("wave control");

    private final String type;

    WarriorType(String type) {
        this.type = type;
    }

    public static WarriorType getType(String type) {
        switch (type.toLowerCase()) {
            case "kraken release":
                return KRAKEN_RELEASE;
            case "poseidon trident":
                return POSEIDON_TRIDENT;
            case "fish telepathy":
                return FISH_TELEPATHY;
            case "undersea fire":
                return UNDERSEA_FIRE;
            case "sea thunders":
                return SEA_THUNDERS;
            case "wave control":
                return WAVE_CONTROL;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return this.type;
    }
}
