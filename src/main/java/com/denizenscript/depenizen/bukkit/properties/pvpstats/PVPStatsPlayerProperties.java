package com.denizenscript.depenizen.bukkit.properties.pvpstats;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.objects.Mechanism;
import net.slipcor.pvpstats.PVPData;

public class PVPStatsPlayerProperties implements Property {

    @Override
    public String getPropertyString() {
        return null;
    }

    @Override
    public String getPropertyId() {
        return "PVPStatsPlayer";
    }

    @Override
    public void adjust(Mechanism mechanism) {
        // None
    }

    public static boolean describes(ObjectTag object) {
        return object instanceof PlayerTag;
    }

    public static PVPStatsPlayerProperties getFrom(ObjectTag object) {
        if (!describes(object)) {
            return null;
        }
        else {
            return new PVPStatsPlayerProperties((PlayerTag) object);
        }
    }

    public static final String[] handledTags = new String[] {
            "pvpstats"
    };

    public static final String[] handledMechs = new String[] {
    }; // None

    public PVPStatsPlayerProperties(PlayerTag player) {
        this.playerName = player.getName();
    }

    String playerName = null;

    @Override
    public String getAttribute(Attribute attribute) {

        if (attribute.startsWith("pvpstats")) {

            attribute = attribute.fulfill(1);

            // <--[tag]
            // @attribute <PlayerTag.pvpstats.deaths>
            // @returns ElementTag
            // @description
            // Returns the number of times the player has died.
            // @Plugin Depenizen, PvP Stats
            // -->
            if (attribute.startsWith("deaths")) {
                return new ElementTag(PVPData.getDeaths(playerName)).getAttribute(attribute.fulfill(1));
            }

            // <--[tag]
            // @attribute <PlayerTag.pvpstats.elo>
            // @returns ElementTag
            // @description
            // Returns the Elo rating of the player.
            // @Plugin Depenizen, PvP Stats
            // -->
            if (attribute.startsWith("elo")) {
                return new ElementTag(PVPData.getEloScore(playerName)).getAttribute(attribute.fulfill(1));
            }

            // <--[tag]
            // @attribute <PlayerTag.pvpstats.kills>
            // @returns ElementTag
            // @description
            // Returns the number of players the player has killed.
            // @Plugin Depenizen, PvP Stats
            // -->
            if (attribute.startsWith("kills")) {
                return new ElementTag(PVPData.getKills(playerName)).getAttribute(attribute.fulfill(1));
            }

            // <--[tag]
            // @attribute <PlayerTag.pvpstats.streak>
            // @returns ElementTag
            // @description
            // Returns the current kill streak of the player.
            // @Plugin Depenizen, PvP Stats
            // -->
            if (attribute.startsWith("streak")) {
                return new ElementTag(PVPData.getStreak(playerName)).getAttribute(attribute.fulfill(1));
            }

            // <--[tag]
            // @attribute <PlayerTag.pvpstats.max_streak>
            // @returns ElementTag
            // @description
            // Returns the highest kill streak of the player.
            // @Plugin Depenizen, PvP Stats
            // -->
            if (attribute.startsWith("max_streak")) {
                return new ElementTag(PVPData.getMaxStreak(playerName)).getAttribute(attribute.fulfill(1));
            }

        }

        return null;

    }
}
