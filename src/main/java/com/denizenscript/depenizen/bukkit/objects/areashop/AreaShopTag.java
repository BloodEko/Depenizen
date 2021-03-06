package com.denizenscript.depenizen.bukkit.objects.areashop;

import me.wiefferink.areashop.AreaShop;
import me.wiefferink.areashop.regions.GeneralRegion;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Fetchable;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.depenizen.bukkit.objects.worldguard.WorldGuardRegionTag;

public class AreaShopTag implements ObjectTag {

    // <--[language]
    // @name AreaShopTag
    // @group Depenizen Object Types
    // @plugin Depenizen, AreaShop
    // @description
    // A AreaShopTag represents an AreaShop shop.
    //
    // For format info, see <@link language areashop@>
    //
    // -->

    // <--[language]
    // @name areashop@
    // @group Depenizen Object Fetcher Types
    // @plugin Depenizen, AreaShop
    // @description
    // areashop@ refers to the 'object identifier' of a AreaShopTag. The 'areashop@' is notation for Denizen's Object
    // Fetcher. The constructor for a AreaShopTag is <shop_name>
    // For example, 'areashop@my_shot'.
    //
    // For general info, see <@link language AreaShopTag>
    //
    // -->

    public static AreaShopTag valueOf(String string) {
        return AreaShopTag.valueOf(string, null);
    }

    @Fetchable("areashop")
    public static AreaShopTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }

        ////////
        // Match areaShop name

        string = string.replace("areashop@", "");
        GeneralRegion areaShop = AreaShop.getInstance().getFileManager().getRegion(string);
        if (areaShop == null) {
            return null;
        }
        return new AreaShopTag(areaShop);
    }

    public static boolean matches(String arg) {
        arg = arg.replace("areashop@", "");
        return AreaShop.getInstance().getFileManager().getRegion(arg) != null;
    }

    GeneralRegion areaShop = null;

    public AreaShopTag(GeneralRegion areaShop) {
        if (areaShop != null) {
            this.areaShop = areaShop;
        }
        else {
            Debug.echoError("AreaShop referenced is null!");
        }
    }

    String prefix = "AreaShop";

    @Override
    public String getPrefix() {
        return prefix;
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
        return "AreaShop";
    }

    @Override
    public String identify() {
        return "areashop@" + areaShop.getName();
    }

    @Override
    public String identifySimple() {
        return identify();
    }

    @Override
    public ObjectTag setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public String toString() {
        return identify();
    }

    public GeneralRegion getAreaShop() {
        return areaShop;
    }

    public boolean equals(AreaShopTag areaShop) {
        return areaShop.getAreaShop().equals(this.getAreaShop());
    }

    @Override
    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <AreaShopTag.is_bought>
        // @returns ElementTag(Boolean)
        // @description
        // Returns whether this AreaShop has been bought.
        // @Plugin Depenizen, AreaShop
        // -->
        if (attribute.startsWith("is_bought")) {
            return new ElementTag(areaShop.isBuyRegion()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <AreaShopTag.is_rented>
        // @returns ElementTag(Boolean)
        // @description
        // Returns whether this AreaShop is being rented.
        // @Plugin Depenizen, AreaShop
        // -->
        else if (attribute.startsWith("is_rented")) {
            return new ElementTag(areaShop.isRentRegion()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <AreaShopTag.groups>
        // @returns ListTag
        // @description
        // Returns a list of groups that control this AreaShop.
        // @Plugin Depenizen, AreaShop
        // -->
        else if (attribute.startsWith("groups")) {
            return new ListTag(areaShop.getGroupNames()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <AreaShopTag.landlord>
        // @returns PlayerTag
        // @description
        // Returns the landlord of the AreaShop.
        // @Plugin Depenizen, AreaShop
        // -->
        else if (attribute.startsWith("landlord")) {
            return new PlayerTag(areaShop.getLandlord()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <AreaShopTag.name>
        // @returns ElementTag
        // @description
        // Returns the name of the AreaShop.
        // @Plugin Depenizen, AreaShop
        // -->
        else if (attribute.startsWith("name")) {
            return new ElementTag(areaShop.getName()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <AreaShopTag.owner>
        // @returns PlayerTag
        // @description
        // Returns the owner of the AreaShop.
        // @Plugin Depenizen, AreaShop
        // -->
        else if (attribute.startsWith("owner")) {
            return new PlayerTag(areaShop.getOwner()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <AreaShopTag.worldguard_region>
        // @returns WorldGuardRegion
        // @description
        // Returns the WorldGuardRegion that holds the AreaShop.
        // @Plugin Depenizen, AreaShop
        // -->
        else if (attribute.startsWith("worldguard_region")) {
            return new WorldGuardRegionTag(areaShop.getRegion(), areaShop.getWorld()).getAttribute(attribute.fulfill(1));
        }

        return new ElementTag(identify()).getAttribute(attribute);
    }
}
