package de.hamze.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Prefix {
    public static final Component prefix = Component.text("[", NamedTextColor.DARK_GRAY)
            .append(Component.text("Mute-Plugin", NamedTextColor.AQUA))
            .append(Component.text("] ", NamedTextColor.DARK_GRAY));
}
