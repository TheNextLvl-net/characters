package net.thenextlvl.character.plugin.character;

import com.destroystokyo.paper.SkinParts;
import net.thenextlvl.character.CharacterProvider;
import net.thenextlvl.character.plugin.CharacterPlugin;
import net.thenextlvl.character.plugin.character.action.PaperActionTypeRegistry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperCharacterProvider implements CharacterProvider {
    private final PaperActionTypeRegistry actionTypeRegistry = new PaperActionTypeRegistry();
    private final PaperSkinFactory skinFactory;

    public PaperCharacterProvider(CharacterPlugin plugin) {
        this.skinFactory = new PaperSkinFactory(plugin);
    }

    @Override
    public PaperActionTypeRegistry getActionRegistry() {
        return actionTypeRegistry;
    }

    @Override
    public PaperSkinFactory skinFactory() {
        return skinFactory;
    }

    @Override
    public PaperSkinPartBuilder skinPartBuilder() {
        return new PaperSkinPartBuilder();
    }

    @Override
    public PaperSkinPartBuilder skinPartBuilder(SkinParts parts) {
        return skinPartBuilder(parts.getRaw());
    }

    @Override
    public PaperSkinPartBuilder skinPartBuilder(int raw) {
        return new PaperSkinPartBuilder(raw);
    }
}
