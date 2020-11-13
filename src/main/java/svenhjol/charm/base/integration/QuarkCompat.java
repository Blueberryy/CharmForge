package svenhjol.charm.base.integration;

import svenhjol.charm.Charm;
import svenhjol.charm.base.helper.StringHelper;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleLoader;

/**
 * This won't function if quark is not compiled in build.gradle.
 * Comment out this entire class if quark is not present in the dev environment.
 */
@SuppressWarnings("unchecked")
public class QuarkCompat implements IQuarkCompat {

    @Override
    public boolean isModuleEnabled(String moduleName) {
        try {
            int dot = moduleName.lastIndexOf(".") + 1;
            String packageName = moduleName.substring(0, dot);
            String className = moduleName.substring(dot);
            Class<?> clazz = Class.forName("vazkii.quark." + packageName + StringHelper.snakeToUpperCamel(className));
            return ModuleLoader.INSTANCE.isModuleEnabled((Class<? extends Module>) clazz);
        } catch (Exception e) {
            Charm.LOG.debug("Failed to resolve Quark module class name");
            return false;
        }
    }
}
