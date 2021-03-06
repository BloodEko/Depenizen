package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.depenizen.bukkit.events.mobarena.MobArenaStartsScriptEvent;
import com.denizenscript.depenizen.bukkit.Bridge;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.denizenscript.depenizen.bukkit.commands.mobarena.MobArenaCommand;
import com.denizenscript.depenizen.bukkit.events.mobarena.MobArenaEndsScriptEvent;
import com.denizenscript.depenizen.bukkit.properties.mobarena.MobArenaPlayerProperties;
import com.denizenscript.depenizen.bukkit.objects.mobarena.MobArenaArenaTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.DenizenAPI;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.TagRunnable;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.depenizen.bukkit.events.mobarena.MobArenaWaveChangesScriptEvent;
import com.denizenscript.denizencore.tags.TagManager;

public class MobArenaBridge extends Bridge {

    public static MobArenaBridge instance;

    @Override
    public void init() {
        instance = this;
        ObjectFetcher.registerWithObjectFetcher(MobArenaArenaTag.class);
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                tagEvent(event);
            }
        }, "mobarena");
        DenizenAPI.getCurrentInstance().getCommandRegistry().registerCoreMember(MobArenaCommand.class,
                "MOBARENA", "mobarena [<mobarena>] (add:<player>|...) (remove:<player>|...) (spectate:<player>|...)", 1);
        ScriptEvent.registerScriptEvent(new MobArenaStartsScriptEvent());
        ScriptEvent.registerScriptEvent(new MobArenaEndsScriptEvent());
        ScriptEvent.registerScriptEvent(new MobArenaWaveChangesScriptEvent());
        PropertyParser.registerProperty(MobArenaPlayerProperties.class, PlayerTag.class);
    }

    public void tagEvent(ReplaceableTagEvent event) {
        Attribute attribute = event.getAttributes();

        // <--[tag]
        // @attribute <mobarena[<name>]>
        // @returns MobArena
        // @description
        // Returns the mob arena for the input name.
        // @Plugin Depenizen, MobArena
        // -->
        if (attribute.hasContext(1)) {
            MobArenaArenaTag arena = MobArenaArenaTag.valueOf(attribute.getContext(1));
            if (arena != null) {
                event.setReplacedObject(arena.getObjectAttribute(attribute.fulfill(1)));
            }
            else if (!event.hasAlternative()) {
                Debug.echoError("Unknown mob arena '" + attribute.getContext(1) + "' for mobarena[] tag.");
            }
            return;
        }

        attribute = attribute.fulfill(1);

        // <--[tag]
        // @attribute <mobarena.list_arenas>
        // @returns ListTag(MobArena)
        // @description
        // Returns a list of all MobArenas.
        // @Plugin Depenizen, MobArena
        // -->
        if (attribute.startsWith("list_arenas")) {
            ListTag arenas = new ListTag();
            for (Arena a : ((MobArena) plugin).getArenaMaster().getArenas()) {
                if (((MobArena) plugin).getArenaMaster().getArenaWithName(a.configName()) == null) {
                    continue;
                }
                arenas.add(new MobArenaArenaTag(a).identify());
            }
            event.setReplacedObject(arenas.getObjectAttribute(attribute.fulfill(1)));
        }
    }
}
