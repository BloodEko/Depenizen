package com.denizenscript.depenizen.bukkit.factions;

import com.massivecraft.factions.entity.*;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.ps.PS;
import com.denizenscript.denizen.objects.dChunk;
import com.denizenscript.denizen.objects.dLocation;
import com.denizenscript.denizen.objects.dPlayer;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.ElementTag;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagContext;

import java.util.Set;

public class dFaction implements ObjectTag {

    /////////////////////
    //   OBJECT FETCHER
    /////////////////

    public static dFaction valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("faction")
    public static dFaction valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }

        ////////
        // Match faction name

        string = string.replace("faction@", "");
        Faction faction = FactionColl.get().getByName(string);
        if (faction != null) {
            return new dFaction(faction);
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

    public dFaction(Faction faction) {
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
        // @attribute <faction@faction.balance>
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
        // @attribute <faction@faction.home>
        // @returns dLocation
        // @description
        // Returns the location of the faction's home, if any.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("home")) {
            if (faction.hasHome()) {
                return new dLocation(faction.getHome().asBukkitLocation())
                        .getAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <faction@faction.id>
        // @returns ElementTag
        // @description
        // Returns the unique ID for this faction.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("id")) {
            return new ElementTag(faction.getId()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <faction@faction.is_open>
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
        // @attribute <faction@faction.is_peaceful>
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
        // @attribute <faction@faction.is_permanent>
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
        // @attribute <faction@faction.leader>
        // @returns dPlayer
        // @description
        // Returns the faction's leader as a dPlayer.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("leader")) {
            if (faction.getLeader() != null) {
                return dPlayer.valueOf(faction.getLeader().getName())
                        .getAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <faction@faction.name>
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
        // @attribute <faction@faction.player_count>
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
        // @attribute <faction@faction.power>
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
        // @attribute <faction@faction.relation[<faction>]>
        // @returns ElementTag
        // @description
        // Returns the current relation between the faction and another faction.
        // @Plugin Depenizen, Factions
        // -->
        else if (attribute.startsWith("relation")) {
            dFaction to = valueOf(attribute.getContext(1));

            if (to != null) {
                return new ElementTag(faction.getRelationTo(to.getFaction()).toString())
                        .getAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <faction@faction.size>
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
        // @attribute <faction@faction.type>
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
        // @attribute <faction@faction.claimed_chunks>
        // @returns ListTag(dChunk)
        // @description
        // Returns a list of all chunks claimed in the faction.
        // @Plugin Depenizen, Factions
        // -->
        if (attribute.startsWith("claimed_chunks")) {
            Set<PS> chunks = BoardColl.get().getChunks(faction);
            ListTag dchunks = new ListTag();
            for (PS ps : chunks) {
                dchunks.add(new dChunk(ps.asBukkitChunk()).identify());
            }
            return dchunks.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <faction@faction.list_players>
        // @returns ListTag(dPlayer)
        // @description
        // Returns a list of all players in the faction.
        // @Plugin Depenizen, Factions
        // -->
        if (attribute.startsWith("list_players")) {
            Set<PS> chunks = BoardColl.get().getChunks(faction);
            ListTag players = new ListTag();
            for (MPlayer ps : faction.getMPlayers()) {
                players.add(dPlayer.valueOf(faction.getLeader().getUuid().toString()).identify());
            }
            return players.getAttribute(attribute.fulfill(1));
        }

        return new ElementTag(identify()).getAttribute(attribute);

    }
}