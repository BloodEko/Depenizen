package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.depenizen.bukkit.events.towny.PlayerEntersTownScriptEvent;
import com.denizenscript.depenizen.bukkit.events.towny.PlayerExitsTownScriptEvent;
import com.denizenscript.depenizen.bukkit.properties.towny.TownyCuboidProperties;
import com.denizenscript.depenizen.bukkit.properties.towny.TownyLocationProperties;
import com.denizenscript.depenizen.bukkit.objects.factions.NationTag;
import com.denizenscript.depenizen.bukkit.objects.towny.TownTag;
import com.denizenscript.depenizen.bukkit.Bridge;
import com.denizenscript.depenizen.bukkit.properties.towny.TownyPlayerProperties;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.denizenscript.denizen.objects.CuboidTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.events.ScriptEvent;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.TagRunnable;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagManager;

public class TownyBridge extends Bridge {

    @Override
    public void init() {
        ObjectFetcher.registerWithObjectFetcher(TownTag.class);
        ObjectFetcher.registerWithObjectFetcher(NationTag.class);
        PropertyParser.registerProperty(TownyPlayerProperties.class, PlayerTag.class);
        PropertyParser.registerProperty(TownyLocationProperties.class, LocationTag.class);
        PropertyParser.registerProperty(TownyCuboidProperties.class, CuboidTag.class);
        ScriptEvent.registerScriptEvent(new PlayerEntersTownScriptEvent());
        ScriptEvent.registerScriptEvent(new PlayerExitsTownScriptEvent());
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                townyTagEvent(event);
            }
        }, "towny");
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                townTagEvent(event);
            }
        }, "town");
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                nationTagEvent(event);
            }
        }, "nation");
    }

    public void townyTagEvent(ReplaceableTagEvent event) {
        Attribute attribute = event.getAttributes().fulfill(1);

        // <--[tag]
        // @attribute <towny.list_towns[<world name>]>
        // @returns ListTag(Element)
        // @description
        // Returns a list of all towns. Optionally specify a world name.
        // @Plugin Depenizen, Towny
        // -->
        if (attribute.startsWith("list_towns")) {
            ListTag towns = new ListTag();
            if (attribute.hasContext(1)) {
                try {
                    for (Town town : TownyUniverse.getDataSource().getWorld(attribute.getContext(1)).getTowns()) {
                        towns.add(new TownTag(town).identify());
                    }
                }
                catch (NotRegisteredException e) {
                    Debug.echoError("World specified is not a registered towny world!");
                    return;
                }
            }
            else {
                for (Town town : TownyUniverse.getDataSource().getTowns()) {
                    towns.add(new TownTag(town).identify());
                }
            }
            event.setReplacedObject(towns.getObjectAttribute(attribute.fulfill(1)));
        }
    }

    public void townTagEvent(ReplaceableTagEvent event) {
        Attribute attribute = event.getAttributes();

        // <--[tag]
        // @attribute <town[<name>]>
        // @returns TownTag
        // @description
        // Returns the town by the input name.
        // @Plugin Depenizen, Towny
        // -->
        if (attribute.hasContext(1)) {
            TownTag town;
            if (TownTag.matches(attribute.getContext(1))) {
                town = TownTag.valueOf(attribute.getContext(1));
            }
            else {
                Debug.echoError("Could not match '" + attribute.getContext(1) + "' to a valid town!");
                return;
            }
            if (town != null) {
                event.setReplacedObject(town.getObjectAttribute(attribute.fulfill(1)));
            }
            else if (!event.hasAlternative()) {
                Debug.echoError("Unknown town '" + attribute.getContext(1) + "' for town[] tag.");
            }
        }
    }

    public void nationTagEvent(ReplaceableTagEvent event) {
        Attribute attribute = event.getAttributes();

        // <--[tag]
        // @attribute <nation[<name>]>
        // @returns NationTag
        // @description
        // Returns the nation by the input name.
        // @Plugin Depenizen, Towny
        // -->
        if (attribute.hasContext(1)) {
            NationTag nation;
            if (NationTag.matches(attribute.getContext(1))) {
                nation = NationTag.valueOf(attribute.getContext(1));
            }
            else {
                Debug.echoError("Could not match '" + attribute.getContext(1) + "' to a valid nation!");
                return;
            }

            if (nation != null) {
                event.setReplacedObject(nation.getObjectAttribute(attribute.fulfill(1)));
            }
            else if (!event.hasAlternative()) {
                Debug.echoError("Unknown nation '" + attribute.getContext(1) + "' for nation[] tag.");
            }
        }

    }
}
