package server.plugin.common;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.Packet28EntityMetadata;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class PacketHelper {

    public static void tourGuide(PacketEvent event, Entity tourguide, Player player){
        if(event.isServerPacket()) {
            final int ENTITY_INVISIBLE = 0x20;
            if(event.getPlayer() != player) {
                Packet28EntityMetadata packet = new Packet28EntityMetadata(event.getPacket());
                Entity entity = packet.getEntity(event);

                if(entity == tourguide) {
                    WrappedDataWatcher watcher = new WrappedDataWatcher(packet.getEntityMetadata());
                    Byte flag = watcher.getByte(0);
                    if(flag != null) {
                        // Clone and update it
                        packet = new Packet28EntityMetadata(packet.getHandle().deepClone());
                        watcher = new WrappedDataWatcher(packet.getEntityMetadata());
                        watcher.setObject(0, (byte) (flag | ENTITY_INVISIBLE));
                        event.setPacket(packet.getHandle());
                    }
                }
            }
        }
    }
}
