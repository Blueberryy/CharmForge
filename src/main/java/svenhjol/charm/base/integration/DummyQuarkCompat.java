package svenhjol.charm.base.integration;

public class DummyQuarkCompat implements IQuarkCompat {
    @Override
    public boolean isModuleEnabled(String moduleName) {
        return false;
    }
}
