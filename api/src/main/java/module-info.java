module net.thenextlvl.characters {
    exports net.thenextlvl.character.action;
    exports net.thenextlvl.character.codec;
    exports net.thenextlvl.character.event.player;
    exports net.thenextlvl.character.event;
    exports net.thenextlvl.character.goal;
    exports net.thenextlvl.character.skin;
    exports net.thenextlvl.character.tag;
    exports net.thenextlvl.character;
    
    requires com.google.common;
    requires core.paper;
    requires net.kyori.adventure.key;
    requires net.kyori.adventure.text.minimessage;
    requires net.kyori.adventure;
    requires net.thenextlvl.nbt;
    requires org.bukkit;
    requires org.joml;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}