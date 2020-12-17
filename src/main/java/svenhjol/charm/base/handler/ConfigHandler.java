package svenhjol.charm.base.handler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConfigHandler {
    private final static List<Runnable> refreshConfig = new ArrayList<>();

    public void createConfig(ModContainer forgeModContainer, Map<String, CharmModule> moduleMap) {
        List<CharmModule> modules = new ArrayList<>(moduleMap.values());

        // build config tree for modules
        ForgeConfigSpec spec = new ForgeConfigSpec.Builder().configure((b -> buildConfig(b, new ArrayList<>(modules)))).getRight();

        ModConfig config = new ModConfig(ModConfig.Type.COMMON, spec, forgeModContainer);
        forgeModContainer.addConfig(config);

        // config is loaded too late to do vanilla overrides, parse it here
        this.earlyConfigHack(config, modules);
    }

    public static void refreshAllConfig() {
        refreshConfig.forEach(Runnable::run);
    }

    private Void buildConfig(ForgeConfigSpec.Builder builder, List<CharmModule> modules) {
        modules.forEach(module -> {
            if (!module.description.isEmpty())
                builder.comment(module.description);

            if (module.alwaysEnabled) {
                module.enabled = true;
                return;
            }

            ForgeConfigSpec.ConfigValue<Boolean> val = builder.define(
                module.getName() + " enabled", module.enabledByDefault
            );

            refreshConfig.add(() -> module.enabled = module.enabled && val.get());
        });

        modules.forEach(module -> {
            builder.push(module.getName());

            List<Field> fields = new ArrayList<>(Arrays.asList(module.getClass().getDeclaredFields()));
            fields.forEach(field -> {
                Config annotation = field.getDeclaredAnnotation(Config.class);
                if (annotation == null)
                    return;

                field.setAccessible(true);
                String name = annotation.name();
                String description = annotation.description();

                if (name.isEmpty())
                    name = field.getName();

                if (!description.isEmpty())
                    builder.comment(description);

                try {
                    ForgeConfigSpec.ConfigValue<?> val;
                    Object defaultVal = field.get(null);

                    if (defaultVal instanceof List) {
                        val = builder.defineList(name, (List<?>) defaultVal, o -> true);
                    } else {
                        val = builder.define(name, defaultVal);
                    }

                    final String finalName = name;
                    final ForgeConfigSpec.ConfigValue<?> finalVal = val;

                    refreshConfig.add(() -> {
                        try {
                            Charm.LOG.debug("[" + module.getName() + "] Setting config field " + finalName + " to " + finalVal.get());
                            field.set(null, val.get());
                        } catch (IllegalAccessException e) {
                            Charm.LOG.error("Could not set config value for " + module.getName());
                            throw new RuntimeException(e);
                        }
                    });

                } catch (ReflectiveOperationException e) {
                    Charm.LOG.error("Failed to get config for " + module.getName());
                }
            });
            builder.pop();
        });
        return null;
    }

    private void earlyConfigHack(ModConfig config, List<CharmModule> modules) {
        List<String> lines;

        Path path = FMLPaths.CONFIGDIR.get();
        if (path == null) {
            Charm.LOG.warn("Could not fetch config dir path");
            return;
        }

        String name = config.getFileName();
        if (name == null) {
            Charm.LOG.warn("Could not fetch mod config filename");
            return;
        }

        Path configPath = Paths.get(path.toString() + File.separator + name);
        if (Files.isRegularFile(path)) {
            Charm.LOG.warn("Config file does not exist: " + path);
            return;
        }

        try {
            lines = Files.readAllLines(configPath);
            for (String line : lines) {
                if (!line.contains("enabled")) continue;
                modules.forEach(module -> {
                    if (line.contains(module.getName())) {
                        if (line.contains("false")) {
                            module.enabled = false;
                        } else if (line.contains("true")) {
                            module.enabled = true;
                        }
                    }
                });
            }
        } catch (Exception e) {
            Charm.LOG.warn("Could not read config file: " + e);
        }
    }
}
