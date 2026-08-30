package www0abdb.oss.authiva;

import org.bukkit.plugin.java.JavaPlugin;

public final class Authiva extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info("Authiva enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Authiva disabled.");
    }
}
