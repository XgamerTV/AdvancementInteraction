package me.mats.advancementinteraction;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementRequirements;

class AdvancementGranter extends AdvancementProgress {

    AdvancementGranter() {
        super.update(new AdvancementRequirements(new String[][]{{"grant"}}));
        super.grantProgress("grant");
    }

}
