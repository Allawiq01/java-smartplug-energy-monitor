package config;

public final class SmartPlugConfig {
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 4000;
    public static final String TEAM_NAME = "MyTeam";

    public static final String[] APPLIANCES = {
            "Laptop",
            "TV",
            "Kylsk\u00e5p",
            "Mikrov\u00e5gsugn",
            "LED-lampa",
            "Dator",
            "Incandescent Bulb"
    };

    private SmartPlugConfig() {
    }

    public static int getMaxPowerConsumption(String appliance) {
        return switch (appliance) {
            case "LED-lampa" -> 15;
            case "Incandescent Bulb" -> 40;
            case "Laptop" -> 30;
            case "Dator" -> 200;
            case "TV" -> 150;
            case "Kylsk\u00e5p" -> 250;
            case "Mikrov\u00e5gsugn" -> 1000;
            default -> 500;
        };
    }
}
