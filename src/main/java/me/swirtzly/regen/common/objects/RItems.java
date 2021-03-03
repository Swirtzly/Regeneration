package me.swirtzly.regen.common.objects;

import me.swirtzly.regen.common.item.*;
import me.swirtzly.regen.util.RConstants;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.minecraft.item.ArmorMaterial.LEATHER;

public class RItems {

    public static final DeferredRegister< Item > ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RConstants.MODID);
    public static RegistryObject< Item > FOB = ITEMS.register("fobwatch", FobWatchItem::new);
    //Item group
    public static ItemGroup MAIN = new ItemGroup("regen") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RItems.FOB.get());
        }
    };
    public static RegistryObject< Item > SPAWN_ITEM = ITEMS.register("timelord", SpawnItem::new);
    public static RegistryObject< Item > PISTOL = ITEMS.register("staser", () -> new GunItem(18, 5, 4.0F));
    public static RegistryObject< Item > RIFLE = ITEMS.register("rifle", () -> new GunItem(30, 10, 10.0F));
    public static RegistryObject< Item > ELIXIR = ITEMS.register("elixir", ElixirItem::new);

    public static RegistryObject< Item > GUARD_HELMET = ITEMS.register("guard_helmet", () -> new ClothingItem(RMaterials.TIMELORD, EquipmentSlotType.HEAD, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));
    public static RegistryObject< Item > GUARD_CHEST = ITEMS.register("guard_chest", () -> new ClothingItem(RMaterials.TIMELORD, EquipmentSlotType.CHEST, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));
    public static RegistryObject< Item > GUARD_LEGS = ITEMS.register("guard_legs", () -> new ClothingItem(RMaterials.TIMELORD, EquipmentSlotType.LEGS, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));
    public static RegistryObject< Item > GUARD_FEET = ITEMS.register("guard_feet", () -> new ClothingItem(RMaterials.TIMELORD, EquipmentSlotType.FEET, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));


    public static RegistryObject< Item > F_ROBES_HEAD = ITEMS.register("f_robes_head", () -> new ClothingItem(LEATHER, EquipmentSlotType.HEAD, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));
    public static RegistryObject< Item > F_ROBES_CHEST = ITEMS.register("f_robes_chest", () -> new ClothingItem(LEATHER, EquipmentSlotType.CHEST, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));
    public static RegistryObject< Item > F_ROBES_LEGS = ITEMS.register("f_robes_legs", () -> new ClothingItem(LEATHER, EquipmentSlotType.LEGS, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));
    public static RegistryObject< Item > F_ROBES_FEET = ITEMS.register("f_robes_feet", () -> new ClothingItem(LEATHER, EquipmentSlotType.FEET, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));

    public static RegistryObject< Item > M_ROBES_HEAD = ITEMS.register("m_robes_head", () -> new ClothingItem(LEATHER, EquipmentSlotType.HEAD, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));
    public static RegistryObject< Item > M_ROBES_CHEST = ITEMS.register("m_robes_chest", () -> new ClothingItem(LEATHER, EquipmentSlotType.CHEST, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));
    public static RegistryObject< Item > M_ROBES_LEGS = ITEMS.register("m_robes_legs", () -> new ClothingItem(LEATHER, EquipmentSlotType.LEGS, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));
    public static RegistryObject< Item > M_ROBES_FEET = ITEMS.register("m_robes_feet", () -> new ClothingItem(LEATHER, EquipmentSlotType.FEET, new Item.Properties().group(RItems.MAIN).maxStackSize(1)));


}
