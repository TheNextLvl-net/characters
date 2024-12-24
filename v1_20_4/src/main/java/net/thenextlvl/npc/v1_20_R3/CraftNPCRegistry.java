package net.thenextlvl.npc.v1_20_R3;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.thenextlvl.character.Character;
import net.thenextlvl.character.CharacterRegistry;
import net.thenextlvl.character.event.CharacterRegisterEvent;
import net.thenextlvl.character.event.CharacterUnregisterEvent;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class CraftNPCRegistry implements CharacterRegistry {
    private final Collection<Character> nPCs = new ArrayList<>();

    @Override
    public void register(Character npc) throws IllegalStateException {
        Preconditions.checkState(!isRegistered(npc), "NPC already registered");
        if (new CharacterRegisterEvent(npc).callEvent()) nPCs.add(npc);
    }

    @Override
    public void unregister(Character npc) throws IllegalStateException {
        Preconditions.checkState(isRegistered(npc), "NPC not registered");
        if (new CharacterUnregisterEvent(npc).callEvent()) nPCs.remove(npc);
    }

    @Override
    public boolean isRegistered(Character npc) {
        return nPCs.contains(npc);
    }
}
