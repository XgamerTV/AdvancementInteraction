package me.mats.advancementinteraction;

import jline.internal.Nullable;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;


import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

import java.util.*;

public class TeamAdvancements {
    private static final net.minecraft.world.item.ItemStack air = CraftItemStack.asNMSCopy(new ItemStack(Material.AIR));
    private static final AdvancementGranter granter = new AdvancementGranter();

    private static final AdvancementRevoker revoker = new AdvancementRevoker();
    private final String namespace;
    private final AdvancementHolder rootAdvHolder;
    private final Advancement.Builder builder;

    public TeamAdvancements(String namespace, String background) {
        this.namespace = namespace;

        Advancement rootAdv = new Advancement(Optional.empty(), Optional.of(new DisplayInfo(air, Component.nullToEmpty(""), Component.nullToEmpty(""), new ResourceLocation("minecraft", "textures/block/"+background.toLowerCase()+".png"), FrameType.TASK, false, false, true)), AdvancementRewards.EMPTY, new HashMap<>(),AdvancementRequirements.EMPTY, false);
        rootAdvHolder = new AdvancementHolder(new ResourceLocation(namespace, "root"), rootAdv);

        builder = Advancement.Builder.advancement();
        builder.parent(rootAdvHolder);
        builder.addCriterion("grant", new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance()));
        builder.requirements(new AdvancementRequirements(new String[][]{{"grant"}}));
    }

    public void sendRootAdvancement(Player p) {
        // Packet could be an attribute but only need it at start
        ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(true, new ArrayList<>(List.of(rootAdvHolder)), new HashSet<>(0), new LinkedHashMap<>(0));
        CraftPlayer cp = (CraftPlayer) p;
        cp.getHandle().connection.send(packet);
    }

    public void sendAdvancements(List<Player> players, List<ItemStack> items, float[][] positions, @Nullable List<String> removeAdvancementNames) {
        // Create ResourceLocations for Advancements to be removed
        HashSet<ResourceLocation> removeAdvancementSet = null;
        if (removeAdvancementNames != null) {
            removeAdvancementSet = new HashSet<>(removeAdvancementNames.size());

            for (String adv : removeAdvancementNames) {
                removeAdvancementSet.add(new ResourceLocation(namespace, adv));
            }

        } else {
            removeAdvancementSet = new HashSet<>(0);
        }

        // Create and send packet
        ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, createAdvancements(items, positions, true), removeAdvancementSet, new HashMap<>(0));

        for (Player p : players) {
                CraftPlayer cp = (CraftPlayer) p;
                cp.getHandle().connection.send(packet);
        }
    }

    public void sendFinalField(List<Player> players, List<String> removeAdvancements, List<String> grantAdvancements, List<ItemStack> items, float[][] positions) {
        List<AdvancementHolder> advancements = createAdvancements(items, positions, false);
        advancements.add(0,rootAdvHolder);

        HashMap<ResourceLocation,AdvancementProgress> progress = new HashMap<>(removeAdvancements.size()+grantAdvancements.size());
        for (String advancementName : removeAdvancements) {
            progress.put(new ResourceLocation(namespace, advancementName), revoker);
        }
        for (String advancementName : grantAdvancements) {
            progress.put(new ResourceLocation(namespace, advancementName), granter);
        }

        ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(true, advancements, new HashSet<>(0), progress);

        for (Player p : players) {
            CraftPlayer cp = (CraftPlayer) p;
            cp.getHandle().connection.send(packet);
        }
    }


    public static void grantAdvancement(List<Player> players, String namespace, String advancementName) {
        ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, new HashSet<>(0), new HashSet<>(0), new HashMap<>(Map.of(new ResourceLocation(namespace, advancementName), granter)));

        for (Player p : players) {
            CraftPlayer cp = (CraftPlayer) p;
            cp.getHandle().connection.send(packet);
        }
    }

    public static void revokeAdvancements(List<Player> players, String namespace, List<String> advancementNames) {
        Map<ResourceLocation, AdvancementProgress> revoke = new HashMap<>(advancementNames.size());

        for (String advName : advancementNames) {
            revoke.put(new ResourceLocation(namespace, advName), revoker);
        }
        ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, new HashSet<>(0), new HashSet<>(0), revoke);

        for (Player p : players) {
            CraftPlayer cp = (CraftPlayer) p;
            cp.getHandle().connection.send(packet);
        }
    }

    private List<AdvancementHolder> createAdvancements(List<ItemStack> items, float[][] positions, boolean showToast) {
        // Create Advancements
        ArrayList<AdvancementHolder> advancements = new ArrayList<>(items.size());
        // Create advancements
        for (int i = 0; i < positions.length; i++) {
            float[] position = positions[i];
            ItemStack item = items.get(i);

            Component desc = Component.empty();
            String nameAddition = "";

            // If Enchanted Book
            if (item.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                Map<Enchantment, Integer> enchantments = meta.getStoredEnchants();

                for (Enchantment ench : enchantments.keySet()) {
                    String levelString = "";

                    if (ench.getMaxLevel() > 1) {
                        Integer level = enchantments.get(ench);
                        switch (level) {
                            case 1:
                                levelString = "I";
                                break;
                            case 2:
                                levelString = "II";
                                break;
                            case 3:
                                levelString = "III";
                                break;
                            case 4:
                                levelString = "IV";
                                break;
                            case 5:
                                levelString = "V";
                                break;
                        }
                    }
                    desc = Component.nullToEmpty("§d" + WordUtils.capitalize(ench.getKey().getKey().replace("_", " ")) + " " + levelString);
                }
            } else if (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION ||item.getType() == Material.TIPPED_ARROW) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                PotionData data = meta.getBasePotionData();
                String name = WordUtils.capitalize(data.getType().getEffectType().getKey().getKey().replace("_", " "));
                nameAddition = " of " + name;

                if (data.isUpgraded()) {
                    desc = Component.nullToEmpty("§9"+name+ " II");
                } else if (data.isExtended()) {
                    desc = Component.nullToEmpty("§9Extended "+name);
                }

            }
            builder.display(CraftItemStack.asNMSCopy(item), Component.nullToEmpty(WordUtils.capitalize(item.getType().name().replace("_", " ").toLowerCase())+nameAddition), desc, new ResourceLocation("minecraft","textures/block/white_concrete.png"), FrameType.TASK, showToast, false, false);
            AdvancementHolder adv = builder.build(new ResourceLocation(namespace, item.getType().toString().toLowerCase()));
            adv.value().display().get().setLocation(position[0], position[1]);
            advancements.add(adv);
        }
        return advancements;
    }

}
