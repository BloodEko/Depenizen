package com.denizenscript.depenizen.bukkit.objects.factions;

import com.massivecraft.factions.entity.*;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.ps.PS;
import com.denizenscript.denizen.objects.ChunkTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagContext;

import java.util.Set;

public class FactionTag implements ObjectTag {

    // <--[language]
    // @name FactionTag
    // @group Depenizen Object Types
    // @plugin Depenizen, Factions
    // @description
    // A FactionTag represents a Factions faction.
    //
    // For format info, see <@link language faction@>
    //
    // -->

    // <--[language]
    // @name faction@
    // @group Depenizen Object Fetcher Types
    // @plugin Depenizen, Factions
    // @description
    // faction@ refers to the 'object identifier' of a FactionTag. The 'faction@' is notation for Denizen's Object
    // Fetcher. The constructor for a FactionTag is <faction_name>
    // For example, 'faction@my_faction'.
    //
    // For general info, see <@link language FactionTag>
    //
    // -->

    /////////////////////
    //   OBJECT FETCHER
    /////////////////

    public static FactionTag valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("faction")
    public static FactionTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }

        ////////
        // Match faction name

        string = string.replace("faction@", "");
        Faction faction = FactionColl.get().getByName(string);
        if (faction != null) {
            return new FactionTag(faction);
        }

        return null;
    }

    public static boolean matches(String arg) {
        if (valueOf(arg) != null) {
            return true;
        }

        return false;
    }

    /////////////////////
    //   STATIC CONSTRUCTORS
    /////////////////

    Faction faction = null;

    public FactionTag(Faction faction) {
        if (faction != null) {
            this.faction = faction;
        }
        else {
            Debug.echoError("Faction referenced is null!");
        }
    }

    public Faction getFaction() {
        return faction;
    }

    /////////////////////
    //   ObjectTag Methods
    /////////////////

    private String prefix = "Faction";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public String debug() {
        return (prefix + "='<A>" + identify() + "<G>' ");
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public String getObjectType() {
        return "Faction";
    }

    @Override
    public String identify() {
        return "faction@" + faction.getName();
    }

    @Override
    public String identifySimple() {
        // TODO: Properties?
        return identify();
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public String getAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <FactionTag.balance>
        // @returns ElementTag(Decimal)
        // @description
        // Returns the amount of money the faction currently has.
        // @Plugin Depenizen, Factions
        // -->
        if (attribute.startsWith("balance")) {
            return new ElementTag(Money.get(faction))
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.warp[<name>]>
        // @returns LocationTag
        // @description
        // Returns the location of the faction's warp by name, if any.
        // Note that this was previously named "home" instead of "warp".
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("warp") && attribute.hasContext(1)) {
            Warp warp = faction.getWarp(attribute.getContext(1));
            if (warp != null) {
                return new LocationTag(warp.getLocation().asBukkitLocation())
                        .getAttribute(attribute.fulfill(1));
            }
        }
        else if (attribute.startsWith("home")) { // Legacy sorta-compat
            Warp warp = faction.getWarp("home");
            if (warp != null) {
                return new LocationTag(warp.getLocation().asBukkitLocation())
                        .getAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <FactionTag.id>
        // @returns ElementTag
        // @description
        // Returns the unique ID for this faction.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("id")) {
            return new ElementTag(faction.getId()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.is_open>
        // @returns ElementTag(Boolean)
        // @description
        // Returns true if the faction is open.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("isopen") || attribute.startsWith("is_open")) {
            return new ElementTag(faction.isOpen())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.is_peaceful>
        // @returns ElementTag(Boolean)
        // @description
        // Returns true if the faction is peaceful.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("ispeaceful") || attribute.startsWith("is_peaceful")) {
            return new ElementTag(faction.getFlag(MFlag.getFlagPeaceful()))
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.is_permanent>
        // @returns ElementTag(Boolean)
        // @description
        // Returns true if the faction is permanent.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("ispermanent") || attribute.startsWith("is_permanent")) {
            return new ElementTag(faction.getFlag(MFlag.getFlagPermanent()))
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.leader>
        // @returns PlayerTag
        // @description
        // Returns the faction's leader as a PlayerTag.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("leader")) {
            if (faction.getLeader() != null) {
                return PlayerTag.valueOf(faction.getLeader().getName())
                        .getAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <FactionTag.name>
        // @returns ElementTag
        // @description
        // Returns the name of the faction.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("name")) {
            return new ElementTag(faction.getName())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.player_count>
        // @returns ElementTag(Number)
        // @description
        // Returns the number of players in the faction.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("playercount") || attribute.startsWith("player_count")) {
            return new ElementTag(faction.getMPlayers().size())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.power>
        // @returns ElementTag(Decimal)
        // @description
        // Returns the amount of power the faction currently has.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("power")) {
            return new ElementTag(faction.getPower())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.relation[<faction>]>
        // @returns ElementTag
        // @description
        // Returns the current relation between the faction and another faction.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("relation")) {
            FactionTag to = valueOf(attribute.getContext(1));

            if (to != null) {
                return new ElementTag(faction.getRelationTo(to.getFaction()).toString())
                        .getAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <FactionTag.size>
        // @returns ElementTag(Number)
        // @description
        // Returns the amount of land the faction has.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("size")) {
            return new ElementTag(faction.getLandCount())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.type>
        // @returns ElementTag
        // @description
        // Always returns 'Faction' for dFaction objects. All objects fetchable by the Object Fetcher will return the
        // type of object that is fulfilling this attribute.
        // @Plugin Depenizen, Factions
        // -->
        if (attribute.startsWith("type")) {
            return new ElementTag("Faction").getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.claimed_chunks>
        // @returns ListTag(ChunkTag)
        // @description
        // Returns a list of all chunks claimed in the faction.
        // @Plugin Depenizen, Factions
        // -->
        if (attribute.startsWith("claimed_chunks")) {
            Set<PS> chunks = BoardColl.get().getChunks(faction);
            ListTag dchunks = new ListTag();
            for (PS ps : chunks) {
                dchunks.add(new ChunkTag(ps.asBukkitChunk()).identify());
            }
            return dchunks.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <FactionTag.list_players>
        // @returns ListTag(PlayerTag)
        // @description
        // Returns a list of all players in the faction.
        // @Plugin Depenizen, Factions
        // -->
        if (attribute.startsWith("list_players")) {
            Set<PS> chunks = BoardColl.get().getChunks(faction);
            ListTag players = new ListTag();
            for (MPlayer ps : faction.getMPlayers()) {
                players.add(PlayerTag.valueOf(faction.getLeader().getUuid().toString()).identify());
            }
            return players.getAttribute(attribute.fulfill(1));
        }

        return new ElementTag(identify()).getAttribute(attribute);

    }
}
