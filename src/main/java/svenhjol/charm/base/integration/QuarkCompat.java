package svenhjol.charm.base.integration;

import svenhjol.charm.Charm;
import svenhjol.charm.base.helper.StringHelper;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleLoader;

@SuppressWarnings("unchecked")
public class QuarkCompat implements IQuarkCompat {

    @Override
    public boolean isModuleEnabled(String moduleName) {
        try {
            Class<?> clazz = Class.forName("vazkii.quark." + StringHelper.snakeToUpperCamel(moduleName));
            return ModuleLoader.INSTANCE.isModuleEnabled((Class<? extends Module>) clazz);
        } catch (Exception e) {
            Charm.LOG.debug("Failed to resolve Quark module class name");
            return false;
        }
    }
}
