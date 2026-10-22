package dev.vanitygear;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class VanityGear implements ModInitializer {
    public static final String MOD_ID = "vanitygear";

    public static final Item EMBER_BLADE = register("ember_blade",
            new SwordItem(ToolMaterial.NETHERITE, new Item.Settings().registryKey(key("ember_blade")).maxCount(1)));
    public static final Item FROST_BLADE = register("frost_blade",
            new SwordItem(ToolMaterial.DIAMOND, new Item.Settings().registryKey(key("frost_blade")).maxCount(1)));
    public static final Item VOID_BLADE = register("void_blade",
            new SwordItem(ToolMaterial.NETHERITE, new Item.Settings().registryKey(key("void_blade")).maxCount(1)));

    private static RegistryKey<Item> key(String path) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, path));
    }

    private static Item register(String path, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_ID, path), item);
    }

    @Override
    public void onInitialize() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient()) return TypedActionResult.pass(player.getStackInHand(hand));
            Item held = player.getStackInHand(hand).getItem();

            if (held == EMBER_BLADE) {
                if (player.getItemCooldownManager().isCoolingDown(EMBER_BLADE)) {
                    return TypedActionResult.pass(player.getStackInHand(hand));
                }
                for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class,
                        player.getBoundingBox().expand(4.0), e -> e != player && e.isAlive())) {
                    if (target.squaredDistanceTo(player) <= 16.0) target.setOnFireFor(4);
                }
                player.getItemCooldownManager().set(EMBER_BLADE, 240);
                return TypedActionResult.success(player.getStackInHand(hand));
            }

            if (held == FROST_BLADE) {
                if (player.getItemCooldownManager().isCoolingDown(FROST_BLADE)) {
                    return TypedActionResult.pass(player.getStackInHand(hand));
                }
                for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class,
                        player.getBoundingBox().expand(5.0), e -> e != player && e.isAlive())) {
                    if (target.squaredDistanceTo(player) <= 25.0) {
                        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
                    }
                }
                player.getItemCooldownManager().set(FROST_BLADE, 200);
                return TypedActionResult.success(player.getStackInHand(hand));
            }

            if (held == VOID_BLADE) {
                if (player.getItemCooldownManager().isCoolingDown(VOID_BLADE)) {
                    return TypedActionResult.pass(player.getStackInHand(hand));
                }
                player.addVelocity(player.getRotationVector().multiply(1.45).add(0.0, 0.25, 0.0));
                player.velocityModified = true;
                player.getItemCooldownManager().set(VOID_BLADE, 400);
                return TypedActionResult.success(player.getStackInHand(hand));
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }
}
