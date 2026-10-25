import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.characters {
    exports net.thenextlvl.character.action;
    exports net.thenextlvl.character.event;
    exports net.thenextlvl.character.goal;
    exports net.thenextlvl.character.property;
    exports net.thenextlvl.character.skin;
    exports net.thenextlvl.character.tag;
    exports net.thenextlvl.character;

    requires com.google.common;
    requires net.kyori.adventure.key;
    requires net.kyori.adventure.text.minimessage;
    requires net.thenextlvl.binder;
    requires org.bukkit;
    requires org.joml;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}
