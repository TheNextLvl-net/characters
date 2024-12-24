package net.thenextlvl.npc.v1_20_R3;

import lombok.Getter;
import net.thenextlvl.character.CharacterProvider;

@Getter
public class CraftNPCProvider implements CharacterProvider {
    private final CraftNPCRegistry nPCRegistry = new CraftNPCRegistry();
    private final CraftNPCFactory nPCFactory = new CraftNPCFactory();
    private final CraftNPCLoader nPCLoader = new CraftNPCLoader();
}
