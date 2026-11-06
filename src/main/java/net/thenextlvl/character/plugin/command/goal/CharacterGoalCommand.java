package net.thenextlvl.character.plugin.command.goal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.goal.FollowGoal;
import net.thenextlvl.character.goal.Goal;
import net.thenextlvl.character.goal.LookAtGoal;
import net.thenextlvl.character.goal.MovementGoal;
import net.thenextlvl.character.goal.PatrolGoal;
import net.thenextlvl.character.goal.Target;
import net.thenextlvl.character.goal.WanderGoal;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;
import java.util.function.Function;

import static net.thenextlvl.character.plugin.command.CharacterCommand.characterArgument;

@NullMarked
public final class CharacterGoalCommand extends BrigadierCommand {
    private CharacterGoalCommand(final CharacterPlugin plugin) {
        super(plugin, "goal", "characters.command.goal");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final CharacterPlugin plugin) {
        final var command = new CharacterGoalCommand(plugin);
        return command.create()
                .then(command.add())
                .then(command.clear())
                .then(command.list())
                .then(command.remove());
    }

    // /character goal add <character> look-at|follow <target>
    // /character goal add <character> wander <radius>
    private ArgumentBuilder<CommandSourceStack, ?> add() {
        return Commands.literal("add").then(characterArgument(plugin)
                .then(targets("look-at", LookAtGoal::new))
                .then(targets("follow", FollowGoal::new))
                .then(Commands.literal("wander").then(Commands.argument("radius", DoubleArgumentType.doubleArg(1))
                        .executes(context -> add(context, new WanderGoal(context.getArgument("radius", double.class)))))));
    }

    private ArgumentBuilder<CommandSourceStack, ?> targets(final String name, final Function<Target, Goal> goal) {
        return Commands.literal(name)
                .then(Commands.literal("viewer").executes(context -> add(context, goal.apply(Target.viewer()))))
                .then(Commands.literal("player").then(Commands.argument("player", ArgumentTypes.player())
                        .executes(context -> add(context, goal.apply(Target.player(player(context)))))))
                .then(Commands.literal("nearest-player").then(Commands.argument("radius", DoubleArgumentType.doubleArg(1))
                        .executes(context -> add(context, goal.apply(Target.nearestPlayer(context.getArgument("radius", double.class)))))))
                .then(Commands.literal("here")
                        .executes(context -> add(context, goal.apply(Target.location(context.getSource().getLocation())))));
    }

    private UUID player(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        return resolver.resolve(context.getSource()).getFirst().getUniqueId();
    }

    // replaces the goals that clash with the new one: movement goals with each other, look goals with each other
    private int add(final CommandContext<CommandSourceStack> context, final Goal goal) {
        final var character = context.getArgument("character", Character.class);
        if (character.getGoals().contains(goal)) {
            plugin.bundle().sendMessage(context.getSource().getSender(), "nothing.changed");
            return 0;
        }
        final var clashing = character.getGoals().stream()
                .filter(existing -> clashes(existing, goal))
                .filter(character::removeGoal)
                .toList();
        final var success = character.addGoal(goal);
        final var message = !success ? "nothing.changed" : clashing.isEmpty() ? "character.goal.added" : "character.goal.replaced";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.unparsed("character", character.getName()),
                Placeholder.unparsed("goal", describe(goal)),
                Placeholder.unparsed("replaced", String.join(", ", clashing.stream().map(this::describe).toList())));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static boolean clashes(final Goal existing, final Goal goal) {
        return (existing instanceof MovementGoal && goal instanceof MovementGoal)
               || (existing instanceof LookAtGoal && goal instanceof LookAtGoal);
    }

    private ArgumentBuilder<CommandSourceStack, ?> clear() {
        return Commands.literal("clear").then(characterArgument(plugin).executes(context -> {
            final var character = context.getArgument("character", Character.class);
            final var success = character.clearGoals();
            final var message = success ? "character.goal.cleared" : "nothing.changed";
            plugin.bundle().sendMessage(context.getSource().getSender(), message,
                    Placeholder.unparsed("character", character.getName()));
            return success ? Command.SINGLE_SUCCESS : 0;
        }));
    }

    private ArgumentBuilder<CommandSourceStack, ?> list() {
        return Commands.literal("list").then(characterArgument(plugin).executes(context -> {
            final var sender = context.getSource().getSender();
            final var character = context.getArgument("character", Character.class);
            final var goals = character.getGoals();
            if (goals.isEmpty()) {
                plugin.bundle().sendMessage(sender, "character.goal.list.empty",
                        Placeholder.unparsed("character", character.getName()));
                return 0;
            }
            plugin.bundle().sendMessage(sender, "character.goal.list.header",
                    Placeholder.unparsed("character", character.getName()));
            for (var i = 0; i < goals.size(); i++) {
                plugin.bundle().sendMessage(sender, "character.goal.list",
                        Placeholder.unparsed("character", character.getName()),
                        Placeholder.unparsed("goal", describe(goals.get(i))),
                        Placeholder.unparsed("index", String.valueOf(i + 1)));
            }
            return Command.SINGLE_SUCCESS;
        }));
    }

    // /character goal remove <character> <index>, the index as shown by the list
    private ArgumentBuilder<CommandSourceStack, ?> remove() {
        return Commands.literal("remove").then(characterArgument(plugin)
                .then(Commands.argument("index", IntegerArgumentType.integer(1)).executes(context -> {
                    final var sender = context.getSource().getSender();
                    final var character = context.getArgument("character", Character.class);
                    final var index = context.getArgument("index", int.class);
                    final var goals = character.getGoals();
                    if (index > goals.size()) {
                        plugin.bundle().sendMessage(sender, "character.goal.not_found",
                                Placeholder.unparsed("character", character.getName()),
                                Placeholder.unparsed("index", String.valueOf(index)));
                        return 0;
                    }
                    final var goal = goals.get(index - 1);
                    final var success = character.removeGoal(goal);
                    final var message = success ? "character.goal.removed" : "nothing.changed";
                    plugin.bundle().sendMessage(sender, message,
                            Placeholder.unparsed("character", character.getName()),
                            Placeholder.unparsed("goal", describe(goal)));
                    return success ? Command.SINGLE_SUCCESS : 0;
                })));
    }

    private String describe(final Goal goal) {
        return switch (goal) {
            case final LookAtGoal lookAt -> "look at " + describe(lookAt.target()) + " (range " + lookAt.range() + ")";
            case final FollowGoal follow -> "follow " + describe(follow.target())
                                            + " (distance " + follow.distance() + ", range " + follow.range() + ")";
            case final WanderGoal wander -> "wander (radius " + wander.radius() + ")";
            case final PatrolGoal patrol -> "patrol (" + patrol.points().size() + " points)";
        };
    }

    private String describe(final Target target) {
        return switch (target) {
            case Target.Viewer ignored -> "viewer";
            case final Target.OfPlayer player -> {
                final var name = plugin.getServer().getOfflinePlayer(player.player()).getName();
                yield name != null ? name : player.player().toString();
            }
            case final Target.NearestPlayer nearest -> "nearest player (radius " + nearest.radius() + ")";
            case final Target.Position position -> {
                final var location = position.location();
                yield location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
            }
        };
    }
}
