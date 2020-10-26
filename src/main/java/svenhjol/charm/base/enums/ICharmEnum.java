package svenhjol.charm.base.enums;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

@SuppressWarnings({"NullableProblems", "rawtypes"})
public interface ICharmEnum extends IStringSerializable {
    @Override
    default String getString() {
        return ((Enum)this).name().toLowerCase(Locale.ENGLISH);
    }

    default String getCapitalizedName() {
        String name = getString();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
