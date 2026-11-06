package net.thenextlvl.character.notification;

import net.kyori.adventure.key.KeyPattern;

record SimpleNotificationTrigger(@KeyPattern.Value String name) implements NotificationTrigger {
    public static final NotificationTrigger SUCCESS = NotificationTrigger.create("success");
    public static final NotificationTrigger COOLDOWN = NotificationTrigger.create("cooldown");
    public static final NotificationTrigger NO_PERMISSION = NotificationTrigger.create("no_permission");

    @Override
    public @KeyPattern.Value String getName() {
        return name;
    }
}
