package me.mats.advancementinteraction;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementRequirements;

public class AdvancementRevoker extends AdvancementProgress {

    AdvancementRevoker() {
        super.update(new AdvancementRequirements(new String[][]{{"grant"}}));
        super.revokeProgress("grant");
    }
}
