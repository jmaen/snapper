package core;

public class Configuration {

    private static Configuration config;

    public static Configuration get() {
        if(config == null) {
            config = new Configuration();
        }
        return config;
    }

}
