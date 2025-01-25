package net.thenextlvl.character.attribute;

import org.bukkit.entity.Pose;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AttributeTypes {
    public static AttributeType<Pose> POSE = new AttributeType<>("pose", Pose.class);

    public static class Player {
        public static AttributeType<Boolean> SNEAKING = new AttributeType<>("pose", boolean.class);
    }

    public static class Allay {
        public static AttributeType<Boolean> DANCING = new AttributeType<>("dancing", boolean.class);
    }
}
