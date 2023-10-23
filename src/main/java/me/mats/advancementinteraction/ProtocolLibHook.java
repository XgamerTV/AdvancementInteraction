package me.mats.advancementinteraction;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.advancements.AdvancementHolder;
import java.util.Collection;


public final class ProtocolLibHook {

    public static void register() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        manager.addPacketListener(new PacketAdapter(AdvancementInteraction.getInstance(), ListenerPriority.HIGH, PacketType.Play.Server.ADVANCEMENTS) {

            @Override
            public void onPacketSending(PacketEvent e) {
                PacketContainer packet = e.getPacket();
                if (AdvancementInteraction.getInstance().isPlayingBingo(e.getPlayer())) {
                    Collection<AdvancementHolder> collection = packet.getSpecificModifier(Collection.class).read(0);
                    for (AdvancementHolder h : collection) {
                        if (!h.id().getNamespace().equals("bingo")) {
                            e.setCancelled(true);
                            break;
                        }
                    }

                }
            }
        });
    }
}