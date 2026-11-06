package net.thenextlvl.character.goal;

import com.google.common.base.Preconditions;
import org.bukkit.Location;

import java.util.List;
import java.util.Objects;

/**
 * Makes the character walk along the given points, starting over after the last one.
 *
 * @param points the points to walk along, all in the world of the character
 * @param speed  the speed modifier, see {@link MovementGoal#speed()}
 * @since 0.9.0
 */
public record PatrolGoal(List<Location> points, double speed) implements MovementGoal {
    public PatrolGoal {
        Preconditions.checkArgument(!points.isEmpty(), "Points cannot be empty");
        Preconditions.checkArgument(points.stream().map(Location::getWorld).allMatch(Objects::nonNull), "World cannot be null");
        Preconditions.checkArgument(speed > 0, "Speed must be positive");
        points = points.stream().map(Location::clone).toList();
    }

    /**
     * Makes the character patrol along the given points at the default speed.
     *
     * @param points the points to walk along
     * @since 0.9.0
     */
    public PatrolGoal(final List<Location> points) {
        this(points, DEFAULT_SPEED);
    }

    @Override
    public List<Location> points() {
        return points.stream().map(Location::clone).toList();
    }
}
