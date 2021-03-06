package com.denizenscript.depenizen.bukkit.objects.towny;

import com.denizenscript.depenizen.bukkit.objects.factions.NationTag;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.Location;

public class TownTag implements ObjectTag {

    // <--[language]
    // @name TownTag
    // @group Depenizen Object Types
    // @plugin Depenizen, Towny
    // @description
    // A TownTag represents a Towny town in the world.
    //
    // For format info, see <@link language town@>
    //
    // -->

    // <--[language]
    // @name town@
    // @group Depenizen Object Fetcher Types
    // @plugin Depenizen, Towny
    // @description
    // town@ refers to the 'object identifier' of a TownTag. The 'town@' is notation for Denizen's Object
    // Fetcher. The constructor for a TownTag is <town_name>
    // For example, 'town@mytown'.
    //
    // For general info, see <@link language TownTag>
    //
    // -->

    /////////////////////
    //   OBJECT FETCHER
    /////////////////

    public static TownTag valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("town")
    public static TownTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }

        ////////
        // Match town name

        string = string.replace("town@", "");
        try {
            return new TownTag(TownyUniverse.getDataSource().getTown(string));
        }
        catch (NotRegisteredException e) {
            return null;
        }
    }

    public static boolean matches(String arg) {
        arg = arg.replace("town@", "");
        return TownyUniverse.getDataSource().hasTown(arg);
    }

    /////////////////////
    //   STATIC CONSTRUCTORS
    /////////////////

    Town town = null;

    public TownTag(Town town) {
        this.town = town;
    }

    public static TownTag fromWorldCoord(WorldCoord coord) {
        if (coord == null) {
            return null;
        }
        try {
            return new TownTag(coord.getTownBlock().getTown());
        }
        catch (NotRegisteredException e) {
            return null;
        }
    }

    /////////////////////
    //   ObjectTag Methods
    /////////////////

    private String prefix = "Town";

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public TownTag setPrefix(String prefix) {
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
        return "Town";
    }

    @Override
    public String identify() {
        return "town@" + town.getName();
    }

    @Override
    public String identifySimple() {
        // TODO: Properties?
        return identify();
    }

    public Town getTown() {
        return town;
    }

    public Boolean equals(TownTag town) {
        return CoreUtilities.toLowerCase(town.getTown().getName()).equals(CoreUtilities.toLowerCase(this.getTown().getName()));
    }

    @Override
    public String toString() {
        return identify();
    }

    @Override
    public String getAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <TownTag.assistants>
        // @returns ListTag(PlayerTag)
        // @description
        // Returns a list of the town's assistants.
        // @Plugin Depenizen, Towny
        // -->
        if (attribute.startsWith("assistants")) {
            ListTag list = new ListTag();
            for (Resident resident : town.getAssistants()) {
                list.add(PlayerTag.valueOf(resident.getName()).identify());
            }
            return list.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.balance>
        // @returns ElementTag(Decimal)
        // @description
        // Returns the current money balance of the town.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("balance")) {
            try {
                return new ElementTag(town.getHoldingBalance()).getAttribute(attribute.fulfill(1));
            }
            catch (EconomyException e) {
                if (!attribute.hasAlternative()) {
                    Debug.echoError("Invalid economy response!");
                }
            }
        }

        // <--[tag]
        // @attribute <TownTag.board>
        // @returns ElementTag
        // @description
        // Returns the town's current board.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("board")) {
            return new ElementTag(town.getTownBoard())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.is_open>
        // @returns ElementTag(Boolean)
        // @description
        // Returns true if the town is currently open.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("isopen") || attribute.startsWith("is_open")) {
            return new ElementTag(town.isOpen())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.is_public>
        // @returns ElementTag(Boolean)
        // @description
        // Returns true if the town is currently public.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("ispublic") || attribute.startsWith("is_public")) {
            return new ElementTag(town.isPublic())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.mayor>
        // @returns PlayerTag
        // @description
        // Returns the mayor of the town.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("mayor")) {
            return PlayerTag.valueOf(town.getMayor().getName())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.name>
        // @returns ElementTag
        // @description
        // Returns the town's names.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("name")) {
            return new ElementTag(town.getName())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.nation>
        // @returns NationTag
        // @description
        // Returns the nation that the town belongs to.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("nation")) {
            try {
                return new NationTag(town.getNation())
                        .getAttribute(attribute.fulfill(1));
            }
            catch (NotRegisteredException e) {
            }
        }

        // <--[tag]
        // @attribute <TownTag.player_count>
        // @returns ElementTag(Number)
        // @description
        // Returns the number of players in the town.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("playercount") || attribute.startsWith("player_count")) {
            return new ElementTag(town.getNumResidents())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.residents>
        // @returns ListTag(PlayerTag)
        // @description
        // Returns a list of the town's residents.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("residents")) {
            ListTag list = new ListTag();
            for (Resident resident : town.getResidents()) {
                list.add(PlayerTag.valueOf(resident.getName()).identify());
            }
            return list.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.size>
        // @returns ElementTag(Number)
        // @description
        // Returns the number of blocks the town owns.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("size")) {
            return new ElementTag(town.getPurchasedBlocks())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.spawn>
        // @returns LocationTag
        // @description
        // Returns the spawn point of the town.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("spawn")) {
            try {
                return new LocationTag(town.getSpawn().getBlock().getLocation())
                        .getAttribute(attribute.fulfill(1));
            }
            catch (TownyException e) {
            }
        }

        // <--[tag]
        // @attribute <TownTag.tag>
        // @returns ElementTag
        // @description
        // Returns the town's tag.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("tag")) {
            return new ElementTag(town.getTag())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.taxes>
        // @returns ElementTag(Decimal)
        // @description
        // Returns the town's current taxes.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("taxes")) {
            return new ElementTag(town.getTaxes())
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.outposts>
        // @returns ListTag(LocationTag)
        // @description
        // Returns a list of the town's outpost locations.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("outposts")) {
            ListTag posts = new ListTag();
            for (Location p : town.getAllOutpostSpawns()) {
                posts.add(new LocationTag(p).identify());
            }
            return posts.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.type>
        // @returns ElementTag
        // @description
        // Always returns 'Town' for dTown objects. All objects fetchable by the Object Fetcher will return the
        // type of object that is fulfilling this attribute.
        // @Plugin Depenizen, Towny
        // -->
        if (attribute.startsWith("type")) {
            return new ElementTag("Town").getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.has_explosions>
        // @returns ElementTag(Boolean)
        // @description
        // Returns if the town has explosions turned on.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("has_explosions")) {
            return new ElementTag(town.isBANG()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.has_mobs>
        // @returns ElementTag(Boolean)
        // @description
        // Returns if the town has mobs turned on.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("has_mobs")) {
            return new ElementTag(town.hasMobs()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.has_pvp>
        // @returns ElementTag(Boolean)
        // @description
        // Returns if the town has PvP turned on.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("has_pvp")) {
            return new ElementTag(town.isPVP()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.has_firespread>
        // @returns ElementTag(Boolean)
        // @description
        // Returns if the town has firespread turned on.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("has_firespread")) {
            return new ElementTag(town.isFire()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.has_taxpercent>
        // @returns ElementTag(Boolean)
        // @description
        // Returns if the town has taxes in percentage.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("has_taxpercent")) {
            return new ElementTag(town.isTaxPercentage()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.plottax>
        // @returns ElementTag(Decimal)
        // @description
        // Returns the amount of taxes collected from plots.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("plottax")) {
            return new ElementTag(town.getPlotTax()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <TownTag.plotprice>
        // @returns ElementTag(Decimal)
        // @description
        // Returns the price of a plot.
        // @Plugin Depenizen, Towny
        // -->
        else if (attribute.startsWith("plotprice")) {
            return new ElementTag(town.getPlotPrice()).getAttribute(attribute.fulfill(1));
        }

        return new ElementTag(identify()).getAttribute(attribute);

    }
}
